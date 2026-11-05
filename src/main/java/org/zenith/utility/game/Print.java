package org.zenith.utility.game;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zenith.Zenith;
import org.zenith.utility.misc.FlakeUtil;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.render.Glowboost;

public final class Print {
   private static final int CONST_intValue = 128;
   private static final double CONST_doubleValue = 0.62;
   private static final double CONST_doubleValue2 = 0.13;
   private static final double CONST_doubleValue3 = 0.012;
   private static final double CONST_doubleValue4 = 0.006;
   private static final int CONST_intValue2 = 14;
   private static final float CONST_floatValue = 3.5F;
   private static final float CONST_floatValue2 = 0.55F;
   private static final float CONST_floatValue3 = 0.014F;
   private static final Identifier identifier = Zenith.method1501("visuals/weather/print.png");
   private static final Identifier identifier2 = Zenith.method1501("visuals/weather/flake.png");
   private static final long CONST_longValue = 420L;
   private static final double CONST_doubleValue5 = 0.105;
   private static final double CONST_doubleValue6 = 0.165;
   private final ArrayDeque<Print.Data> arrayDeque = new ArrayDeque<>();
   private final Map<UUID, Print.Entry> items = new HashMap<>();

   public void method5440(Level level, Iterable<? extends Player> players, WeatherFlakeChannel weatherFlakeChannel, long longValue) {
      if (weatherFlakeChannel.intValue7 > 0 && level != null) {
         for (Player player : players) {
            if (!player.isSpectator() && player.onGround()) {
               double x = player.getX();
               double z = player.getZ();
               double y = player.getY();
               UUID uuid = player.getUUID();
               Print.Entry entry = this.items.get(uuid);
               if (entry == null) {
                  entry = new Print.Entry();
                  entry.doubleValue = x;
                  entry.doubleValue2 = z;
                  this.items.put(uuid, entry);
               }

               entry.longValue = longValue;
               double d = x - entry.doubleValue;
               double d2 = z - entry.doubleValue2;
               if (!(d * d + d2 * d2 < 0.3844)) {
                  entry.doubleValue = x;
                  entry.doubleValue2 = z;
                  entry.left = !entry.left;
                  float yRot = player.getYRot();
                  double d3 = Math.toRadians(yRot);
                  double d4 = Math.cos(d3);
                  double d5 = Math.sin(d3);
                  double d6 = entry.left ? -0.13 : 0.13;
                  double x2 = x + d4 * d6;
                  double z2 = z + d5 * d6;
                  if (method5441(level, x2, y, z2)) {
                     this.method5442(new Print.Data(x2, y + 0.012, z2, yRot, entry.left, longValue));
                  }
               }
            }
         }

         var iterator = this.items.values().iterator();

         while (iterator.hasNext()) {
            if (longValue - ((Print.Entry)iterator.next()).longValue > 30000L) {
               iterator.remove();
            }
         }

         this.method5443(weatherFlakeChannel, longValue);
      }
   }

   private static boolean method5441(Level level, double x, double y, double z) {
      BlockPos containing = BlockPos.containing(x, y - 0.1, z);
      if (!level.hasChunk(containing.getX() >> 4, containing.getZ() >> 4)) {
         return false;
      } else {
         BlockState blockState = level.getBlockState(containing);
         return !blockState.isAir() && blockState.isCollisionShapeFullBlock(level, containing) && blockState.getFluidState().isEmpty();
      }
   }

   private void method5442(Print.Data data) {
      if (this.arrayDeque.size() >= 128) {
         this.arrayDeque.pollFirst();
      }

      this.arrayDeque.addLast(data);
   }

   private void method5443(WeatherFlakeChannel weatherFlakeChannel, long longValue) {
      var iterator = this.arrayDeque.iterator();

      while (iterator.hasNext() && longValue - ((Print.Data)iterator.next()).getLongValue() > weatherFlakeChannel.intValue7) {
         iterator.remove();
      }
   }

   public void clear() {
      this.arrayDeque.clear();
      this.items.clear();
   }

   public void method5444(Glowboost glowboost, WeatherFlakeChannel weatherFlakeChannel, float f, long now, double x, double y, double z) {
      if (weatherFlakeChannel.intValue7 > 0 && !this.arrayDeque.isEmpty()) {
         glowboost.method3244(identifier);
         boolean flag = false;

         for (Print.Data data : this.arrayDeque) {
            float f2 = (float)(now - data.getLongValue()) / weatherFlakeChannel.intValue7;
            if (!(f2 >= 1.0F)) {
               double d = data.getDoubleValue() - x;
               double d2 = data.getDoubleValue3() - z;
               double d3 = Math.sqrt(d * d + d2 * d2);
               if (!(d3 > 14.0)
                  && !(Math.abs(data.getDoubleValue2() - y) > 12.0)
                  && (!(d3 > 3.0) || !(d * glowboost.getDoubleValue10() + d2 * glowboost.getDoubleValue12() < -0.25 * d3))) {
                  float f3 = (float)Math.min(1.0, (14.0 - d3) / 3.5);
                  double d4 = weatherFlakeChannel.flag ? FlakeUtil.method7509(data.getDoubleValue(), data.getDoubleValue3()) + 0.006 : 0.0;
                  method5447(
                     glowboost,
                     weatherFlakeChannel,
                     data.getDoubleValue(),
                     data.getDoubleValue2() + d4,
                     data.getDoubleValue3(),
                     data.getFloatValue() + (data.isFlag() ? -8.0F : 8.0F),
                     f * method5449(f2) * f3,
                     f2
                  );
                  flag |= weatherFlakeChannel.flag && now - data.getLongValue() < 420L;
               }
            }
         }

         if (flag) {
            glowboost.method3244(identifier2);

            for (Print.Data data : this.arrayDeque) {
               long l = now - data.getLongValue();
               if (l < 420L) {
                  method5445(glowboost, data, (float)l / 420.0F, f, FlakeUtil.method7509(data.getDoubleValue(), data.getDoubleValue3()));
               }
            }
         }
      }
   }

   private static void method5445(Glowboost glowboost, Print.Data data, float f, float f2, float f3) {
      float f4 = (1.0F - f) * (1.0F - f);
      int n = method5451(-591105, (int)(150.0F * f4 * Math.clamp(f2, 0.0F, 1.0F)));
      if (n >>> 24 != 0) {
         long longValue = Double.doubleToLongBits(data.getDoubleValue()) ^ Long.rotateLeft(data.getLongValue(), 17);

         for (int i = 0; i < 5; i++) {
            float f5 = 0.05F + 0.16F * Glowboost.method3268(longValue, i, 0);
            double d = Glowboost.method3268(longValue, i, 1) * Math.PI * 2.0;
            double doubleValue = data.getDoubleValue() + Math.cos(d) * f5 * (0.4 + f);
            double d2 = data.getDoubleValue3() + Math.sin(d) * f5 * (0.4 + f);
            double d3 = 0.2 * (0.5 + 0.5 * Glowboost.method3268(longValue, i, 2));
            double d4 = data.getDoubleValue2() + f3 + d3 * (f * 2.2 - f * f * 2.4);
            float f6 = 0.035F + 0.03F * Glowboost.method3268(longValue, i, 3);
            glowboost.method3249(doubleValue, d4, d2, 0.0, 1.0, 0.0, f6, f6, 0.0F, 0.0F, 1.0F, 1.0F, n);
         }
      }
   }

   public static void method5446(Glowboost glowboost, WeatherFlakeChannel weatherFlakeChannel, float previewAlpha) {
      if (weatherFlakeChannel.intValue7 > 0) {
         glowboost.method3244(identifier);

         for (int i = 0; i < 6; i++) {
            boolean flag = (i & 1) == 0;
            double d = flag ? -0.13 : 0.13;
            double d2 = 2.4 - i * 0.62;
            double d3 = d2 <= -2.0 ? 1.0 : 0.0;
            float f = 0.35F + 0.65F * (i / 5.0F);
            method5447(glowboost, weatherFlakeChannel, -0.4 + d, d3 + 0.012, d2, flag ? -8.0F : 8.0F, previewAlpha * f, 1.0F - f);
         }
      }
   }

   private static void method5447(Glowboost glowboost, WeatherFlakeChannel weatherFlakeChannel, double doubleValue, double d, double d2, float angdeg, float f, float f2) {
      if (!(f <= 0.01F)) {
         double d3 = Math.toRadians(angdeg);
         double d4 = -Math.sin(d3);
         double d5 = Math.cos(d3);
         int n = method5451(weatherFlakeChannel.intValue6, (int)((weatherFlakeChannel.intValue6 >>> 24 & 0xFF) * Math.clamp(f, 0.0F, 1.0F)));
         if (weatherFlakeChannel.flag) {
            float f3 = Math.clamp(1.0F - f2 * 1.8F, 0.0F, 1.0F) * f;
            if (f3 > 0.02F) {
               method5448(glowboost, doubleValue, d, d2, d4, d5, 1.22, method5451(-722689, (int)(70.0F * f3)));
            }

            method5448(glowboost, doubleValue, d + 0.001, d2, d4, d5, 1.0 - 0.35 * f2, n);
         } else {
            method5448(glowboost, doubleValue, d, d2, d4, d5, 1.0, n);
         }
      }
   }

   private static void method5448(Glowboost glowboost, double doubleValue, double d, double d2, double d3, double d4, double d5, int n) {
      glowboost.method3246(doubleValue, d, d2, d3, d4, 0.105 * d5, 0.165 * d5, 0.0F, 0.0F, 1.0F, 1.0F, n);
   }

   private static float method5449(float f) {
      float f2 = method5450(Math.clamp(f / 0.014F, 0.0F, 1.0F));
      if (f <= 0.55F) {
         return f2;
      } else {
         float f3 = (f - 0.55F) / 0.45F;
         return f2 * (1.0F - method5450(Math.clamp(f3, 0.0F, 1.0F)));
      }
   }

   private static float method5450(float f) {
      return f * f * (3.0F - 2.0F * f);
   }

   private static int method5451(int n, int n2) {
      return Math.clamp((long)n2, 0, 255) << 24 | n & 16777215;
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final boolean flag;
      private final long longValue;

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, floatValue, flag, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(double doubleValue, double d, double d2, float floatValue, boolean flag, long longValue) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.floatValue = floatValue;
         this.flag = flag;
         this.longValue = longValue;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private double doubleValue;
      private double doubleValue2;
      private boolean left;
      private long longValue;
   }
}
