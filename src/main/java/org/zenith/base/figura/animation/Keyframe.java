package org.zenith.base.figura.animation;

import com.mojang.datafixers.util.Pair;
import java.util.regex.Pattern;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.model.FiguraModelPart;

public class Keyframe implements Comparable<Keyframe> {
   private final Avatar owner;
   private final FiguraModelPart part;
   private final TransformType channel;
   private final Animation animation;
   private final float time;
   private final Interpolation interpolation;
   private final FiguraVec3 targetA;
   private final FiguraVec3 targetB;
   private final String[] aCode;
   private final String[] bCode;
   private final FiguraVec3 bezierLeft;
   private final FiguraVec3 bezierRight;
   private final FiguraVec3 bezierLeftTime;
   private final FiguraVec3 bezierRightTime;
   private final Keyframe.KeyframeValue[] aCache = new Keyframe.KeyframeValue[]{null, null, null};
   private final Keyframe.KeyframeValue[] bCache = new Keyframe.KeyframeValue[]{null, null, null};
   private static final Pattern regex = Pattern.compile("^\\[string \"[^\"]*?\"]:");

   public Keyframe(
      Avatar owner,
      FiguraModelPart part,
      TransformType channel,
      Animation animation,
      float time,
      Interpolation interpolation,
      Pair<FiguraVec3, String[]> a,
      Pair<FiguraVec3, String[]> b,
      FiguraVec3 bezierLeft,
      FiguraVec3 bezierRight,
      FiguraVec3 bezierLeftTime,
      FiguraVec3 bezierRightTime
   ) {
      this.owner = owner;
      this.part = part;
      this.channel = channel;
      this.animation = animation;
      this.time = time;
      this.interpolation = interpolation;
      this.targetA = (FiguraVec3)a.getFirst();
      this.targetB = (FiguraVec3)b.getFirst();
      this.aCode = (String[])a.getSecond();
      this.bCode = (String[])b.getSecond();
      this.bezierLeft = bezierLeft;
      this.bezierRight = bezierRight;
      this.bezierLeftTime = bezierLeftTime;
      this.bezierRightTime = bezierRightTime;
   }

   public FiguraVec3 getTargetA(float delta) {
      return this.targetA != null ? this.targetA.copy() : FiguraVec3.of(this.getA(0, delta), this.getA(1, delta), this.getA(2, delta));
   }

   public FiguraVec3 getTargetB(float delta) {
      return this.targetB != null ? this.targetB.copy() : FiguraVec3.of(this.getB(0, delta), this.getB(1, delta), this.getB(2, delta));
   }

   private static String trimErrorMessage(String message) {
      return regex.matcher(message).replaceAll("");
   }

   private Keyframe.KeyframeValue compile(String source, String chunkName) {
      try {
         try {
            return Keyframe.KeyframeValue.literal(Double.parseDouble(source));
         } catch (NumberFormatException var9) {
            try {
               String exprSrc = "return " + source;
               LuaValue chunk = this.owner.loadScript(chunkName, exprSrc);
               return chunk == null ? null : Keyframe.KeyframeValue.function(chunk, chunkName);
            } catch (LuaError var8) {
               try {
                  LuaValue chunkx = this.owner.loadScript(chunkName, source);
                  return chunkx == null ? null : Keyframe.KeyframeValue.function(chunkx, chunkName);
               } catch (LuaError var7) {
                  String trailers = "";
                  if (this.part.formatVersion >= 2) {
                     trailers = "\n\n§6If you opened a 4.12 model file in 5.0, your keyframes might be corrupted.§r\n§6You'll have to manually fix them; note that the X and Y values on rotation,§r\n§6 as well as the X value on position, need to be negated.§r";
                  }

                  throw new LuaError(
                     String.format(
                        "Syntax error in keyframe [%s]:\n\nNot a valid expression, because:\n%s\nNot a valid block, because:\n%s\n\nscript:\n%s%s",
                        chunkName,
                        trimErrorMessage(var8.getMessage()),
                        trimErrorMessage(var7.getMessage()),
                        source,
                        trailers
                     )
                  );
               }
            }
         }
      } catch (Exception var10) {
         if (this.owner.luaRuntime != null) {
            this.owner.luaRuntime.error(var10);
         }

         return null;
      }
   }

   private double evaluate(Keyframe.KeyframeValue k, float delta) {
      if (k.function == null) {
         return k.literal;
      } else {
         Varargs v = this.owner.run(k.function, this.owner.animation, delta, this.animation);
         if (v == null) {
            throw new LuaError(String.format("Tried to run animation with code [%s] without a functioning avatar runtime", k.chunkName));
         } else if (!v.isnumber(1)) {
            throw new LuaError(String.format("Failed to parse data from [%s]: expected number, but got %s", k.chunkName, v.arg1().typename()));
         } else {
            return v.todouble(1);
         }
      }
   }

   private double evalCompile(Keyframe.KeyframeValue[] cache, String[] code, int idx, float delta) {
      try {
         if (cache[idx] != null) {
            return this.evaluate(cache[idx], delta);
         }

         cache[idx] = this.compile(code[idx], this.generateChunkName(idx));
         if (cache[idx] != null) {
            return this.evaluate(cache[idx], delta);
         }

         String var10002 = this.generateChunkName(idx);
         throw new LuaError("Can't compile keyframe [" + var10002 + "] (code: " + code[idx] + ")");
      } catch (Exception var6) {
         if (this.owner.luaRuntime != null) {
            this.owner.luaRuntime.error(var6);
         }

         return 0.0;
      }
   }

   private double getA(int idx, float delta) {
      return this.evalCompile(this.aCache, this.aCode, idx, delta);
   }

   private double getB(int idx, float delta) {
      return this.evalCompile(this.bCache, this.bCode, idx, delta);
   }

   private String generateChunkName(int idx) {
      StringBuilder b = new StringBuilder();
      b.append(this.animation.getName()).append(" keyframe (part '");
      b.append(this.part.name);
      b.append("', time ").append(this.time).append("s, ");
      b.append(this.channel.name()).append(" ");
      switch (idx) {
         case 0:
            b.append("X");
            break;
         case 1:
            b.append("Y");
            break;
         case 2:
            b.append("Z");
            break;
         default:
            b.append("?");
      }

      b.append(")");
      return b.toString();
   }

   public float getTime() {
      return this.time;
   }

   public Interpolation getInterpolation() {
      return this.interpolation;
   }

   public FiguraVec3 getBezierLeft() {
      return this.bezierLeft.copy();
   }

   public FiguraVec3 getBezierRight() {
      return this.bezierRight.copy();
   }

   public FiguraVec3 getBezierLeftTime() {
      return this.bezierLeftTime.copy();
   }

   public FiguraVec3 getBezierRightTime() {
      return this.bezierRightTime.copy();
   }

   public int compareTo(Keyframe other) {
      return Float.compare(this.getTime(), other.getTime());
   }

   public record KeyframeValue(double literal, LuaValue function, String chunkName) {
      public static Keyframe.KeyframeValue literal(double literal) {
         return new Keyframe.KeyframeValue(literal, (LuaValue)null, (String)null);
      }

      public static Keyframe.KeyframeValue function(LuaValue function, String chunkName) {
         return new Keyframe.KeyframeValue(0.0, function, chunkName);
      }
   }
}
