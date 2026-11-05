package org.zenith.client.scripting;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfFont;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptDrawApi {
   private static CustomDrawContext customDrawContext;
   private static boolean flag;
   private static int intValue;
   private static int intValue2;
   private static final int CONST_intValue = 20000;

   private ScriptDrawApi() {
   }

   public static void method2597(CustomDrawContext customDrawContext2) {
      customDrawContext = customDrawContext2;
      intValue = 0;
      intValue2 = 0;
   }

   public static void end() {
      if (customDrawContext != null) {
         while (intValue > 0) {
            customDrawContext.disableScissor();
            intValue--;
         }
      }

      customDrawContext = null;
   }

   public static void method2598(PoseStack matrixStack) {
      flag = true;
      intValue2 = 0;
   }

   public static void method2599() {
      flag = false;
   }

   private static CustomDrawContext method2600() {
      if (customDrawContext == null) {
         throw ScriptValueUtils.method6251("render.* работает только внутри events.render2d");
      } else if (++intValue2 > 20000) {
         throw ScriptValueUtils.method6251("больше 20000 вызовов отрисовки за кадр");
      } else {
         return customDrawContext;
      }
   }

   public static CustomDrawContext method2601() {
      return method2600();
   }

   public static void method2602() {
      method2604();
   }

   public static int method2603() {
      return method2612();
   }

   private static void method2604() {
      if (!flag) {
         throw ScriptValueUtils.method6251("render3d.* работает только внутри events.render3d");
      } else if (++intValue2 > 20000) {
         throw ScriptValueUtils.method6251("больше 20000 вызовов отрисовки за кадр");
      }
   }

   public static Font method2605(Object id, float f) {
      String lowerCase = ScriptValueUtils.method6253(id) ? "medium" : ScriptValueUtils.method6255(id).toLowerCase(Locale.ROOT);

      MsdfFont msdfFont = switch (lowerCase) {
         case "bold" -> Fonts.NEW_SEMIBOLD;
         case "semibold" -> Fonts.SEMIBOLD;
         case "regular" -> Fonts.NEW_REGULAR;
         case "icons" -> Fonts.NEW_ICONS;
         default -> Fonts.NEW_MEDIUM;
      };
      return msdfFont.getFont(Math.max(3.0F, Math.min(64.0F, f)));
   }

   private static ZenithColor method2606(Object[] objects, int n, int n2) {
      return new ZenithColor(ScriptValueUtils.method6258(objects, n, n2));
   }

   public static Scriptable method2607(Script workspace, Storage storage, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "text",
         var0x -> {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            Font var2x = method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 7.0));
            method2600()
               .drawText(
                  var2x, var1x, (float)ScriptValueUtils.method6256(var0x, 1, 0.0), (float)ScriptValueUtils.method6256(var0x, 2, 0.0), method2606(var0x, 3, -1)
               );
            return (double)var2x.width(var1x);
         }
      );
      entry2.method6274(
         "textCenter",
         var0x -> {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            Font var2x = method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 7.0));
            float var3x = var2x.width(var1x);
            method2600()
               .drawText(
                  var2x,
                  var1x,
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0) - var3x / 2.0F,
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  method2606(var0x, 3, -1)
               );
            return (double)var3x;
         }
      );
      entry2.method6274(
         "textRight",
         var0x -> {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            Font var2x = method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 7.0));
            float var3x = var2x.width(var1x);
            method2600()
               .drawText(
                  var2x,
                  var1x,
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0) - var3x,
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  method2606(var0x, 3, -1)
               );
            return (double)var3x;
         }
      );
      entry2.method6274(
         "textWidth",
         var0x -> (double)method2605(ScriptValueUtils.arg(var0x, 2), (float)ScriptValueUtils.method6256(var0x, 1, 7.0))
            .width(ScriptValueUtils.method6254(var0x, 0, ""))
      );
      entry2.method6274("textHeight", var0x -> (double)method2605(ScriptValueUtils.arg(var0x, 1), (float)ScriptValueUtils.method6256(var0x, 0, 7.0)).height());
      entry2.method6274("rect", var0x -> {
         float var1x = (float)ScriptValueUtils.method6256(var0x, 5, 0.0);
         ZenithColor var2x = method2606(var0x, 4, -1);
         float var3x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
         float y = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float width = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         float height = (float)ScriptValueUtils.method6256(var0x, 3, 0.0);
         if (var1x <= 0.0F) {
            method2600().drawRect(var3x, y, width, height, var2x);
         } else {
            method2600().drawRoundedRect(var3x, y, width, height, ProxyDotRadiusData.method8069(var1x), var2x);
         }

         return null;
      });
      entry2.method6274(
         "outline",
         var0x -> {
            method2600()
               .drawRoundedBorder(
                  (float)ScriptValueUtils.method6256(var0x, 0, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 3, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 4, 1.0),
                  ProxyDotRadiusData.method8069((float)ScriptValueUtils.method6256(var0x, 6, 0.0)),
                  method2606(var0x, 5, -1)
               );
            return null;
         }
      );
      entry2.method6274("circle", var0x -> {
         float var1x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
         float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 1.0);
         method2600().drawRoundedRect(var1x - var3x, var2x - var3x, var3x * 2.0F, var3x * 2.0F, ProxyDotRadiusData.method8069(var3x), method2606(var0x, 3, -1));
         return null;
      });
      entry2.method6274(
         "arc",
         var0x -> {
            float var1x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
            float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 5.0);
            method2600()
               .drawArcBorder(
                  var1x - var3x,
                  var2x - var3x,
                  var3x * 2.0F,
                  var3x * 2.0F,
                  (float)ScriptValueUtils.method6256(var0x, 4, 1.5),
                  (float)ScriptValueUtils.method6256(var0x, 3, 360.0),
                  1.0F,
                  method2606(var0x, 5, -1)
               );
            return null;
         }
      );
      entry2.method6274(
         "blur",
         var0x -> {
            method2600()
               .drawBlurHud(
                  (float)ScriptValueUtils.method6256(var0x, 0, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 3, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 5, 12.0),
                  ProxyDotRadiusData.method8069((float)ScriptValueUtils.method6256(var0x, 4, 0.0)),
                  method2606(var0x, 6, 1711276032)
               );
            return null;
         }
      );
      entry2.method6274("item", var0x -> {
         ItemStack var1x = method2608(ScriptValueUtils.method6254(var0x, 0, ""), ScriptValueUtils.method6257(var0x, 4, 1));
         if (var1x.isEmpty()) {
            return null;
         } else {
            float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
            float f = (float)ScriptValueUtils.method6256(var0x, 3, 16.0);
            CustomDrawContext customDrawContext = method2600();
            customDrawContext.pose().pushMatrix();
            customDrawContext.pose().translate(var2x, var3x);
            customDrawContext.pose().scale(f / 16.0F, f / 16.0F);
            customDrawContext.item(var1x, 0, 0);
            if (var1x.getCount() > 1) {
               customDrawContext.itemDecorations(Minecraft.getInstance().font, var1x, 0, 0);
            }

            customDrawContext.pose().popMatrix();
            return null;
         }
      });
      entry2.method6274("clip", var1x -> {
         var var2x = ScriptValueUtils.method6260(var1x, 4);
         CustomDrawContext var3x = method2600();
         float floatValue = (float)ScriptValueUtils.method6256(var1x, 0, 0.0);
         float y = (float)ScriptValueUtils.method6256(var1x, 1, 0.0);
         var3x.enableScissor(floatValue, y, floatValue + (float)ScriptValueUtils.method6256(var1x, 2, 0.0), y + (float)ScriptValueUtils.method6256(var1x, 3, 0.0));
         intValue++;

         try {
            var2x.call(Context.getCurrentContext(), scope, scope, new Object[0]);
         } finally {
            var3x.disableScissor();
            intValue--;
         }

         return null;
      });
      entry2.method6274("width", var0x -> (double)ScriptEntityApi.method6575().getWindow().getGuiScaledWidth());
      entry2.method6274("height", var0x -> (double)ScriptEntityApi.method6575().getWindow().getGuiScaledHeight());
      entry2.method6274(
         "toScreen",
         var1x -> {
            Vec3 var2x = ProjectionUtils.method301(
               new Vec3(ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0))
            );
            return !(var2x.z <= 0.0) && !(var2x.z >= 1.0)
               ? ScriptValueUtils.method6247(scope).method6277("x", var2x.x).method6277("y", var2x.y).getScriptableObject()
               : null;
         }
      );
      return entry2.getScriptableObject();
   }

   public static ItemStack method2608(String str, int n) {
      if (str.isBlank()) {
         return ItemStack.EMPTY;
      } else {
         Identifier tryParse = Identifier.tryParse(str.contains(":") ? str : "minecraft:" + str);
         if (tryParse != null && BuiltInRegistries.ITEM.containsKey(tryParse)) {
            ItemStack defaultInstance = BuiltInRegistries.ITEM.getValue(tryParse).getDefaultInstance();
            defaultInstance.setCount(Math.max(1, Math.min(99, n)));
            return defaultInstance;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   public static Scriptable method2609(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "box",
         var0x -> {
            method2604();
            AABB var1x = new AABB(
               ScriptValueUtils.method6256(var0x, 0, 0.0),
               ScriptValueUtils.method6256(var0x, 1, 0.0),
               ScriptValueUtils.method6256(var0x, 2, 0.0),
               ScriptValueUtils.method6256(var0x, 3, 0.0),
               ScriptValueUtils.method6256(var0x, 4, 0.0),
               ScriptValueUtils.method6256(var0x, 5, 0.0)
            );
            method2610(var1x, var0x, 6);
            return null;
         }
      );
      entry2.method6274("blockBox", var0x -> {
         method2604();
         double var1x = Math.floor(ScriptValueUtils.method6256(var0x, 0, 0.0));
         double minY = Math.floor(ScriptValueUtils.method6256(var0x, 1, 0.0));
         double minZ = Math.floor(ScriptValueUtils.method6256(var0x, 2, 0.0));
         method2610(new AABB(var1x, minY, minZ, var1x + 1.0, minY + 1.0, minZ + 1.0), var0x, 3);
         return null;
      });
      entry2.method6274("entityBox", var0x -> {
         method2604();
         Entity var1x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var0x, 0));
         if (var1x == null) {
            return null;
         } else {
            float f = ScriptEntityApi.method6574();
            double xa = var1x.xo + (var1x.getX() - var1x.xo) * f - var1x.getX();
            double ya = var1x.yo + (var1x.getY() - var1x.yo) * f - var1x.getY();
            double za = var1x.zo + (var1x.getZ() - var1x.zo) * f - var1x.getZ();
            method2610(var1x.getBoundingBox().move(xa, ya, za), var0x, 1);
            return null;
         }
      });
      entry2.method6274(
         "line",
         var0x -> {
            method2604();
            GlowUtil.method2245(
               new Vec3(ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)),
               new Vec3(ScriptValueUtils.method6256(var0x, 3, 0.0), ScriptValueUtils.method6256(var0x, 4, 0.0), ScriptValueUtils.method6256(var0x, 5, 0.0)),
               ScriptValueUtils.method6258(var0x, 6, -1),
               (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var0x, 7, 2.0))),
               ScriptValueUtils.method6259(var0x, 8, false)
            );
            return null;
         }
      );
      return entry2.getScriptableObject();
   }

   private static void method2610(AABB aabb, Object[] objects, int n) {
      int color = ScriptValueUtils.method6258(objects, n, 1090519039);
      int color2 = ScriptValueUtils.method6258(objects, n + 1, 0);
      float width = (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(objects, n + 2, 2.0)));
      boolean flag = ScriptValueUtils.method6259(objects, n + 3, false);
      if (color >>> 24 != 0) {
         GlowUtil.method2232(aabb, color, width, false, true, flag);
      }

      if (color2 >>> 24 != 0) {
         GlowUtil.method2232(aabb, color2, width, true, false, flag);
      }
   }

   public static Scriptable method2611(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "rgb",
         var0x -> (double)method2614(
            ScriptValueUtils.method6257(var0x, 3, 255),
            ScriptValueUtils.method6257(var0x, 0, 255),
            ScriptValueUtils.method6257(var0x, 1, 255),
            ScriptValueUtils.method6257(var0x, 2, 255)
         )
      );
      entry2.method6274("hex", var0x -> {
         String var1x = ScriptValueUtils.method6254(var0x, 0, "").replace("#", "");

         try {
            long l = Long.parseLong(var1x, 16);
            if (var1x.length() <= 6) {
               l |= 4278190080L;
            }

            return (double)((int)l);
         } catch (NumberFormatException e) {
            return -1.0;
         }
      });
      entry2.method6274("alpha", var0x -> {
         int var1x = ScriptValueUtils.method6258(var0x, 0, -1);
         int n = (int)Math.round(Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6256(var0x, 1, 1.0))) * 255.0);
         return (double)(var1x & 16777215 | n << 24);
      });
      entry2.method6274(
         "mix",
         var0x -> {
            int var1x = ScriptValueUtils.method6258(var0x, 0, -1);
            int n = ScriptValueUtils.method6258(var0x, 1, -1);
            double d = Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6256(var0x, 2, 0.5)));
            return (double)method2614(
               method2613(var1x >>> 24, n >>> 24, d),
               method2613(var1x >> 16 & 0xFF, n >> 16 & 0xFF, d),
               method2613(var1x >> 8 & 0xFF, n >> 8 & 0xFF, d),
               method2613(var1x & 0xFF, n & 0xFF, d)
            );
         }
      );
      entry2.method6274(
         "hsb",
         var0x -> {
            int var1x = Color.HSBtoRGB(
               (float)ScriptValueUtils.method6256(var0x, 0, 0.0),
               (float)ScriptValueUtils.method6256(var0x, 1, 1.0),
               (float)ScriptValueUtils.method6256(var0x, 2, 1.0)
            );
            int n = (int)Math.round(Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6256(var0x, 3, 1.0))) * 255.0);
            return (double)(var1x & 16777215 | n << 24);
         }
      );
      entry2.method6274("rainbow", var0x -> {
         double var1x = Math.max(1.0, ScriptValueUtils.method6256(var0x, 0, 10.0));
         double d = ScriptValueUtils.method6256(var0x, 1, 0.0);
         float hue = (float)((System.currentTimeMillis() / var1x + d) % 360.0 / 360.0);
         return (double)(Color.HSBtoRGB(hue, (float)ScriptValueUtils.method6256(var0x, 2, 0.8), 1.0F) | 0xFF000000);
      });
      entry2.method6274("accent", var0x -> (double)method2612());
      entry2.method6274("red", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) >> 16 & 0xFF));
      entry2.method6274("green", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) >> 8 & 0xFF));
      entry2.method6274("blue", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) & 0xFF));
      entry2.method6274("alphaOf", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) >>> 24));
      return entry2.getScriptableObject();
   }

   private static int method2612() {
      try {
         return Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287();
      } catch (RuntimeException e) {
         return -9663233;
      }
   }

   private static int method2613(int n, int n2, double d) {
      return (int)Math.round(n + (n2 - n) * d);
   }

   private static int method2614(int n, int n2, int n3, int n4) {
      return method2615(n) << 24 | method2615(n2) << 16 | method2615(n3) << 8 | method2615(n4);
   }

   private static int method2615(int n) {
      return Math.max(0, Math.min(255, n));
   }
}
