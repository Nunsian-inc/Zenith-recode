package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.utility.game.ScriptRenderOverrides;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptImGui {
   private static long longValue;
   private static float floatValue = Float.NaN;
   private static float floatValue2 = Float.NaN;
   private static long longValue2 = -1L;
   private static boolean flag;
   private static float floatValue3;
   private static float floatValue4;
   private static final Map<Script, Map<String, Object>> items = new WeakHashMap<>();
   private static final Map<Script, String> items2 = new WeakHashMap<>();
   private static final Map<Script, String> items3 = new WeakHashMap<>();
   private static final List<ScriptImGui.Data> items4 = new CopyOnWriteArrayList<>();
   private static final List<ScriptImGui.Entry3> items5 = new CopyOnWriteArrayList<>();
   private static final List<Object[]> items6 = new ArrayList<>();
   private static ScriptImGui.Entry entry;
   private static boolean flag2;

   private ScriptImGui() {
   }

   private static Minecraft method632() {
      return Minecraft.getInstance();
   }

   public static synchronized void method633() {
      if (!flag2) {
         flag2 = true;
         EventManager.main().subscribe(DrawEvent.class, (byte)0, (obj, obj2) -> beginFrame());
         EventManager.main().subscribe(DrawEvent.class, (byte)4, (obj, obj2) -> method649(((DrawEvent)obj2).getCustomDrawContext()));
         EventManager.main().subscribe(WorldRenderEvent.class, (byte)4, (obj, obj2) -> method652());
         EventManager.main().subscribe(TickEvent.class, (byte)4, (obj, obj2) -> method651());
         EventManager.main().subscribe(MouseButtonEvent.class, (byte)0, (obj, obj2) -> {
            MouseButtonEvent event = (MouseButtonEvent)obj2;
            if (event.getIntValue() == 0 && event.getIntValue2() == 1) {
               method634(ScriptImGui.Entry2.method656(), ScriptImGui.Entry2.method657());
            }
         });
      }
   }

   public static void method634(float x, float y) {
      floatValue = x;
      floatValue2 = y;
      flag = true;
   }

   private static void beginFrame() {
      longValue++;
      if (flag) {
         flag = false;
         longValue2 = longValue;
      } else if (longValue2 != longValue) {
         floatValue = Float.NaN;
      }
   }

   public static void method635(float floatValue, float f) {
      floatValue3 = floatValue;
      floatValue4 = f;
   }

   private static float method636() {
      return ScriptImGui.Entry2.method656() - floatValue3;
   }

   private static float method637() {
      return ScriptImGui.Entry2.method657() - floatValue4;
   }

   private static boolean method638(float f, float y, float width, float height) {
      if (longValue2 == longValue && !Float.isNaN(floatValue)) {
         float f2 = floatValue - floatValue3;
         float f3 = floatValue2 - floatValue4;
         if (f2 >= f && f2 <= f + width && f3 >= y && f3 <= y + height) {
            floatValue = Float.NaN;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean method639(float f, float y, float width, float height) {
      float f2 = method636();
      float f3 = method637();
      return f2 >= f && f2 <= f + width && f3 >= y && f3 <= y + height;
   }

   private static boolean method640() {
      return org.zenith.utility.misc.KeyCompat.isMouseDown(0);
   }

   private static Map<String, Object> method641(Script workspace) {
      return items.computeIfAbsent(workspace, var0x -> new HashMap<>());
   }

   private static int method642() {
      return ScriptDrawApi.method2603();
   }

   private static Font font(float f) {
      return Fonts.NEW_MEDIUM.getFont(Math.max(3.0F, Math.min(24.0F, f)));
   }

   public static boolean method643(Script script) {
      return items2.get(script) != null;
   }

   public static void method644(Script script, String str) {
      String str2 = items2.get(script);
      if (str2 != null) {
         var map = method641(script);
         String str3 = String.valueOf(map.getOrDefault("in:" + str2, ""));
         if (str3.length() + str.length() <= 256) {
            map.put("in:" + str2, str3 + str);
         }
      }
   }

   public static boolean method645(Script script, int keyCode) {
      String str = items2.get(script);
      if (str == null) {
         return false;
      } else {
         var map = method641(script);
         String str2 = String.valueOf(map.getOrDefault("in:" + str, ""));
         if (keyCode == 259) {
            if (!str2.isEmpty()) {
               map.put("in:" + str, str2.substring(0, str2.length() - 1));
            }

            return true;
         } else if (keyCode != 257 && keyCode != 335 && keyCode != 258) {
            return keyCode != 256;
         } else {
            items2.remove(script);
            map.put("submit:" + str, true);
            return true;
         }
      }
   }

   public static void method646(Script workspace, Storage storage, Scriptable scope, ScriptableObject scriptableObject) {
      method633();
      workspace.method1413(() -> {
         items.remove(workspace);
         items2.remove(workspace);
         items3.remove(workspace);
         items4.removeIf(var1x -> var1x.script == workspace);
         if (entry != null && entry.script == workspace) {
            entry = null;
         }
      });
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "checkbox",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "check");
            String var4x = ScriptValueUtils.method6254(var1x, 1, "");
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var1x, 3, 0.0);
            var map = method641(workspace);
            boolean flag = ((Boolean)map.computeIfAbsent("cb:" + var3x, var1xx -> ScriptValueUtils.method6259(var1x, 4, false)));
            Font font2 = font(6.0F);
            float width = 12.0F + font2.width(var4x) + 4.0F;
            if (method638(var5x, var6x, width, 10.0F)) {
               flag = !flag;
               map.put("cb:" + var3x, flag);
            }

            var2x.drawRoundedRect(var5x, var6x + 1.0F, 8.0F, 8.0F, ProxyDotRadiusData.method8069(2.0F), new ZenithColor(flag ? method642() : 1090519039));
            if (flag) {
               var2x.drawRoundedRect(var5x + 2.5F, var6x + 3.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.0F), new ZenithColor(-1));
            }

            var2x.drawText(
               font2, var4x, var5x + 12.0F, var6x + (10.0F - font2.height()) / 2.0F, new ZenithColor(method639(var5x, var6x, width, 10.0F) ? -1 : -788529153)
            );
            return flag;
         }
      );
      entry2.method6274(
         "slider",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "slider");
            float var4x = (float)ScriptValueUtils.method6256(var1x, 1, 0.0);
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)Math.max(20.0, ScriptValueUtils.method6256(var1x, 3, 80.0));
            double x = ScriptValueUtils.method6256(var1x, 4, 0.0);
            double d = ScriptValueUtils.method6256(var1x, 5, 1.0);
            double d2 = Math.max(0.0, ScriptValueUtils.method6256(var1x, 7, 0.0));
            var map = method641(workspace);
            double d3 = (Double)map.computeIfAbsent(
               "sl:" + var3x, var5xx -> Mth.clamp(ScriptValueUtils.method6256(var1x, 6, x), Math.min(x, d), Math.max(x, d))
            );
            if (method638(var4x - 2.0F, var5x - 3.0F, var6x + 4.0F, 10.0F)) {
               items3.put(workspace, var3x);
            }

            if (var3x.equals(items3.get(workspace))) {
               if (!method640()) {
                  items3.remove(workspace);
               } else {
                  double d4 = Mth.clamp((method636() - var4x) / var6x, 0.0F, 1.0F);
                  d3 = x + (d - x) * d4;
                  if (d2 > 0.0) {
                     d3 = Math.round(d3 / d2) * d2;
                  }

                  map.put("sl:" + var3x, d3);
               }
            }

            double d4 = d == x ? 0.0 : Mth.clamp((d3 - x) / (d - x), 0.0, 1.0);
            var2x.drawRoundedRect(var4x, var5x + 1.5F, var6x, 3.0F, ProxyDotRadiusData.method8069(1.5F), new ZenithColor(1090519039));
            var2x.drawRoundedRect(var4x, var5x + 1.5F, (float)(var6x * d4), 3.0F, ProxyDotRadiusData.method8069(1.5F), new ZenithColor(method642()));
            var2x.drawRoundedRect(var4x + (float)(var6x * d4) - 3.0F, var5x, 6.0F, 6.0F, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(-1));
            return d3;
         }
      );
      entry2.method6274("input", var1x -> {
         CustomDrawContext var2x = ScriptDrawApi.method2601();
         String var3x = ScriptValueUtils.method6254(var1x, 0, "input");
         float var4x = (float)ScriptValueUtils.method6256(var1x, 1, 0.0);
         float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
         float var6x = (float)Math.max(30.0, ScriptValueUtils.method6256(var1x, 3, 100.0));
         String str = ScriptValueUtils.method6254(var1x, 4, "");
         var map = method641(workspace);
         map.computeIfAbsent("in:" + var3x, var1xx -> ScriptValueUtils.method6254(var1x, 5, ""));
         float height = 14.0F;
         if (method638(var4x, var5x, var6x, height)) {
            items2.put(workspace, var3x);
         } else if (longValue2 == longValue && !Float.isNaN(floatValue) && var3x.equals(items2.get(workspace))) {
            items2.remove(workspace);
         }

         boolean flag = var3x.equals(items2.get(workspace));
         String str2 = String.valueOf(map.get("in:" + var3x));
         Font font2 = font(6.0F);
         var2x.drawRoundedRect(var4x, var5x, var6x, height, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(1342177280));
         var2x.drawRoundedBorder(var4x, var5x, var6x, height, 0.5F, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(flag ? method642() : 822083583));
         String text = str2.isEmpty() && !flag ? str : str2;

         while (text.length() > 1 && font2.width(text) > var6x - 10.0F) {
            text = text.substring(1);
         }

         var2x.drawText(font2, text, var4x + 4.0F, var5x + (height - font2.height()) / 2.0F, new ZenithColor(str2.isEmpty() && !flag ? -2130706433 : -1));
         if (flag && System.currentTimeMillis() / 500L % 2L == 0L) {
            var2x.drawRect(var4x + 4.0F + font2.width(text) + 1.0F, var5x + 3.0F, 0.7F, height - 6.0F, new ZenithColor(-1));
         }

         return str2;
      });
      entry2.method6274("submitted", var1x -> Boolean.TRUE.equals(method641(workspace).remove("submit:" + ScriptValueUtils.method6254(var1x, 0, "input"))));
      entry2.method6274(
         "tabs",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "tabs");
            var var4x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 1));
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var1x, 3, 0.0);
            float width = (float)ScriptValueUtils.method6256(var1x, 4, 120.0);
            float height = (float)ScriptValueUtils.method6256(var1x, 5, 14.0);
            var map = method641(workspace);
            int n = ((Integer)map.computeIfAbsent("tb:" + var3x, var0xx -> 0));
            if (var4x.isEmpty()) {
               return (double)n;
            } else {
               float f = width / var4x.size();
               Font font2 = font(Math.min(7.0F, height * 0.45F));
               var2x.drawRoundedRect(var5x, var6x, width, height, ProxyDotRadiusData.method8069(height / 2.0F), new ZenithColor(1073741824));

               for (int i = 0; i < var4x.size(); i++) {
                  float f2 = var5x + f * i;
                  if (method638(f2, var6x, f, height)) {
                     n = i;
                     map.put("tb:" + var3x, i);
                  }

                  if (i == n) {
                     var2x.drawRoundedRect(
                        f2 + 1.0F,
                        var6x + 1.0F,
                        f - 2.0F,
                        height - 2.0F,
                        ProxyDotRadiusData.method8069((height - 2.0F) / 2.0F),
                        new ZenithColor(method642() & 16777215 | -1728053248)
                     );
                  }

                  String name = ScriptValueUtils.method6255(var4x.get(i));
                  var2x.drawText(
                     font2,
                     font2.clip(name, f - 6.0F),
                     f2 + (f - Math.min(f - 6.0F, font2.width(name))) / 2.0F,
                     var6x + (height - font2.height()) / 2.0F,
                     new ZenithColor(i != n && !method639(f2, var6x, f, height) ? -1593835521 : -1)
                  );
               }

               return (double)n;
            }
         }
      );
      entry2.method6274(
         "list",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "list");
            var var4x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 1));
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var1x, 3, 0.0);
            float width = (float)ScriptValueUtils.method6256(var1x, 4, 100.0);
            float height = (float)Math.max(8.0, ScriptValueUtils.method6256(var1x, 5, 12.0));
            int n = Math.max(1, Math.min(200, ScriptValueUtils.method6257(var1x, 6, 10)));
            var map = method641(workspace);
            int n2 = ((Integer)map.computeIfAbsent("ls:" + var3x, var0xx -> -1));
            int n3 = ((Integer)map.computeIfAbsent("lo:" + var3x, var0xx -> 0));
            n3 = Math.max(0, Math.min(n3, Math.max(0, var4x.size() - n)));
            Font font2 = font(Math.min(7.0F, height * 0.55F));

            for (int i = 0; i < Math.min(n, var4x.size() - n3); i++) {
               int n4 = i + n3;
               float y = var6x + i * height;
               boolean flag = method639(var5x, y, width, height);
               if (method638(var5x, y, width, height)) {
                  n2 = n4;
                  map.put("ls:" + var3x, n4);
               }

               if (n4 == n2 || flag) {
                  var2x.drawRoundedRect(
                     var5x,
                     y,
                     width,
                     height,
                     ProxyDotRadiusData.method8069(3.0F),
                     new ZenithColor(n4 == n2 ? method642() & 16777215 | 1879048192 : 637534207)
                  );
               }

               var2x.drawText(
                  font2,
                  font2.clip(ScriptValueUtils.method6255(var4x.get(n4)), width - 8.0F),
                  var5x + 4.0F,
                  y + (height - font2.height()) / 2.0F,
                  new ZenithColor(-251658241)
               );
            }

            if (var4x.size() > n) {
               float f = n * height;
               float height2 = f * n / var4x.size();
               float y = var6x + (f - height2) * n3 / Math.max(1, var4x.size() - n);
               var2x.drawRoundedRect(var5x + width - 2.0F, y, 2.0F, height2, ProxyDotRadiusData.method8069(1.0F), new ZenithColor(1627389951));
            }

            return (double)n2;
         }
      );
      entry2.method6274("scrollList", var1x -> {
         var var2x = method641(workspace);
         String var3x = ScriptValueUtils.method6254(var1x, 0, "list");
         int var4x = ((Integer)var2x.getOrDefault("lo:" + var3x, 0));
         var2x.put("lo:" + var3x, Math.max(0, var4x + ScriptValueUtils.method6257(var1x, 1, 1)));
         return null;
      });
      entry2.method6274("graph", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0));
         float var3x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var4x = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         float var5x = (float)ScriptValueUtils.method6256(var0x, 3, 100.0);
         float var6x = (float)ScriptValueUtils.method6256(var0x, 4, 40.0);
         int intValue = ScriptValueUtils.method6258(var0x, 5, method642());
         Scriptable scriptable = ScriptValueUtils.method6262(var0x, 6);
         if (var2x.size() < 2) {
            return null;
         } else {
            double[] doubles = new double[Math.min(var2x.size(), 512)];
            double d = Double.MAX_VALUE;
            double d2 = -Double.MAX_VALUE;

            for (int i = 0; i < doubles.length; i++) {
               doubles[i] = Context.toNumber(var2x.get(var2x.size() - doubles.length + i));
               d = Math.min(d, doubles[i]);
               d2 = Math.max(d2, doubles[i]);
            }

            if (scriptable != null) {
               d = ScriptValueUtils.method6264(scriptable, "min", d);
               d2 = ScriptValueUtils.method6264(scriptable, "max", d2);
            }

            if (d2 - d < 1.0E-9) {
               d2 = d + 1.0;
            }

            boolean flag = scriptable == null || ScriptValueUtils.method6265(scriptable, "fill", true);
            float f = var5x / (doubles.length - 1);
            ZenithColor zenithColor = new ZenithColor(intValue & 16777215 | 805306368);
            ZenithColor zenithColor2 = new ZenithColor(intValue);

            for (int i = 0; i < doubles.length; i++) {
               float floatValue2 = var3x + f * i;
               float y = (float)(var4x + var6x - (doubles[i] - d) / (d2 - d) * var6x);
               if (flag) {
                  var1x.drawRect(floatValue2, y, Math.max(1.0F, f), var4x + var6x - y, zenithColor);
               }

               if (i > 0) {
                  float f2 = var3x + f * (i - 1);
                  float f3 = (float)(var4x + var6x - (doubles[i - 1] - d) / (d2 - d) * var6x);
                  float f4 = Math.max(1.0F, Math.abs(y - f3));

                  for (int j = 0; j <= f4; j++) {
                     float f5 = j / f4;
                     var1x.drawRect(f2 + (floatValue2 - f2) * f5, f3 + (y - f3) * f5, Math.max(1.0F, f * 0.6F), 1.0F, zenithColor2);
                  }
               }
            }

            return null;
         }
      });
      entry2.method6274(
         "progress",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            float var2x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var4x = (float)ScriptValueUtils.method6256(var0x, 2, 80.0);
            float var5x = (float)ScriptValueUtils.method6256(var0x, 3, 4.0);
            double var6x = Mth.clamp(ScriptValueUtils.method6256(var0x, 4, 0.0), 0.0, 1.0);
            var1x.drawRoundedRect(var2x, var3x, var4x, var5x, ProxyDotRadiusData.method8069(var5x / 2.0F), new ZenithColor(1090519039));
            var1x.drawRoundedRect(
               var2x,
               var3x,
               (float)(var4x * var6x),
               var5x,
               ProxyDotRadiusData.method8069(var5x / 2.0F),
               new ZenithColor(ScriptValueUtils.method6258(var0x, 5, method642()))
            );
            return null;
         }
      );
      entry2.method6274("value", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         var var3x = method641(workspace);

         for (String str : new String[]{"cb:", "sl:", "in:", "tb:", "ls:"}) {
            Object obj = var3x.get(str + var2x);
            if (obj != null) {
               return obj instanceof Integer n ? (double)n.intValue() : obj;
            }
         }

         return null;
      });
      entry2.method6274("setValue", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         Object var3x = ScriptValueUtils.arg(var1x, 1);
         var var4x = method641(workspace);
         if (var3x instanceof Boolean var5x) {
            var4x.put("cb:" + var2x, var5x);
         } else if (var3x instanceof Number var6x) {
            var4x.put("sl:" + var2x, var6x.doubleValue());
            var4x.put("tb:" + var2x, var6x.intValue());
            var4x.put("ls:" + var2x, var6x.intValue());
         } else {
            var4x.put("in:" + var2x, ScriptValueUtils.method6255(var3x));
         }

         return null;
      });
      entry2.method6274("island", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, workspace.method1403());
         String var3x = ScriptValueUtils.method6254(var1x, 1, "");
         long var4x = (long)Mth.clamp(ScriptValueUtils.method6256(var1x, 2, 3000.0), 500.0, 20000.0);
         Scriptable var6x = ScriptValueUtils.method6262(var1x, 3);
         int intValue = var6x == null ? method642() : (int)((long)ScriptValueUtils.method6264(var6x, "color", method642() & 4294967295L));
         RemoteUtil.Entry entry3 = null;
         if (var6x != null && !ScriptValueUtils.method6253(ScriptValueUtils.method6263(var6x, "icon"))) {
            entry3 = RemoteUtil.method1827(ScriptValueUtils.method6263(var6x, "icon"));
         }

         items4.removeIf(var1xx -> var1xx.script == workspace);
         if (items4.size() >= 3) {
            items4.remove(0);
         }

         items4.add(new ScriptImGui.Data(workspace, var2x, var3x, intValue, entry3, System.currentTimeMillis(), var4x));
         return null;
      });
      ScriptValueUtils.Entry2 entry22 = ScriptValueUtils.method6247(scope);
      entry22.method6274("hide", var2x -> {
         entry = method647(workspace, storage);
         entry.flag = true;
         return null;
      });
      entry22.method6274("set", var2x -> {
         ScriptImGui.Entry var3x = method647(workspace, storage);
         Object var4x = ScriptValueUtils.arg(var2x, 0);
         if (var4x instanceof Function var5x) {
            var3x.function = var5x;
         } else {
            var3x.entry = RemoteUtil.method1827(var4x);
         }

         var3x.size = (float)Mth.clamp(ScriptValueUtils.method6256(var2x, 1, 16.0), 2.0, 128.0);
         entry = var3x;
         return null;
      });
      entry22.method6274("reset", var1x -> {
         if (entry != null && entry.script == workspace) {
            entry = null;
         }

         return null;
      });
      entry22.method6274("custom", var0x -> entry != null);
      ScriptableObject scope2 = (ScriptableObject)scope;
      ScriptValueUtils.method6246(scope, scope2).method6277("crosshair", entry22.getScriptableObject());
   }

   private static ScriptImGui.Entry method647(Script script, Storage storage) {
      ScriptImGui.Entry entry = new ScriptImGui.Entry();
      entry.script = script;
      entry.storage = storage;
      return entry;
   }

   public static boolean method648() {
      ScriptImGui.Entry entry2 = entry;
      if (entry2 == null) {
         return false;
      } else if (!entry2.script.isRunning()) {
         entry = null;
         return false;
      } else {
         return true;
      }
   }

   private static void method649(CustomDrawContext customDrawContext) {
      try {
         method650(customDrawContext);
      } finally {
         items6.clear();
      }
   }

   private static void method650(CustomDrawContext customDrawContext) {
      int guiScaledWidth = method632().getWindow().getGuiScaledWidth();
      int guiScaledHeight = method632().getWindow().getGuiScaledHeight();
      ScriptImGui.Entry entry2 = entry;
      if (entry2 != null && entry2.script.isRunning() && !entry2.flag && method632().gui.screen() == null) {
         float f = guiScaledWidth / 2.0F;
         float f2 = guiScaledHeight / 2.0F;
         if (entry2.entry != null && !entry2.entry.flag) {
            Identifier texture = entry2.entry.texture();
            if (texture != null) {
               customDrawContext.drawRoundedTexture(
                  texture, f - entry2.size / 2.0F, f2 - entry2.size / 2.0F, entry2.size, entry2.size, ProxyDotRadiusData.method8069(0.0F), new ZenithColor(-1)
               );
            }
         } else if (entry2.function != null) {
            ScriptDrawApi.method2597(customDrawContext);

            try {
               entry2.storage.method1321(entry2.script, entry2.function, (double)f, (double)f2);
            } finally {
               ScriptDrawApi.end();
            }
         }
      }

      for (Object[] objects : items6) {
         Vec3 vec = ProjectionUtils.method301(new Vec3((Double)objects[1], (Double)objects[2], (Double)objects[3]));
         if (!(vec.z <= 0.0) && !(vec.z >= 1.0)) {
            float f = (Float)objects[4];
            customDrawContext.pose().pushMatrix();
            customDrawContext.pose().translate((float)vec.x - f / 2.0F, (float)vec.y - f / 2.0F);
            customDrawContext.pose().scale(f / 16.0F, f / 16.0F);
            customDrawContext.item((ItemStack)objects[0], 0, 0);
            customDrawContext.pose().popMatrix();
         }
      }

      long now = System.currentTimeMillis();
      float y = 6.0F;

      for (ScriptImGui.Data data : items4) {
         long l = now - data.longValue;
         if (l <= data.longValue2 && data.script.isRunning()) {
            float f = (float)Math.min(1.0, Math.min(l, data.longValue2 - l) / 220.0);
            float previewAlpha = 1.0F - (1.0F - f) * (1.0F - f) * (1.0F - f);
            Font font = Fonts.NEW_SEMIBOLD.getFont(6.5F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
            float size = data.entry == null ? 0.0F : 16.0F;
            float f2 = Math.max(font.width(data.text), font2.width(data.text2)) + 20.0F + size + (size > 0.0F ? 6 : 0);
            float height = data.text2.isEmpty() ? 18.0F : 26.0F;
            float floatValue = (guiScaledWidth - f2 * (0.6F + 0.4F * previewAlpha)) / 2.0F;
            float width = f2 * (0.6F + 0.4F * previewAlpha);
            customDrawContext.drawBlurHud(floatValue, y, width, height, 14.0F, ProxyDotRadiusData.method8069(height / 2.0F), new ZenithColor(-1341387758).method7294(previewAlpha));
            customDrawContext.drawRoundedRect(
               floatValue + 6.0F, y + height / 2.0F - 2.0F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), new ZenithColor(data.intValue).method7294(previewAlpha)
            );
            float floatValue2 = floatValue + 14.0F;
            if (data.entry != null && !data.entry.flag && data.entry.texture() != null) {
               customDrawContext.drawRoundedTexture(
                  data.entry.texture(),
                  floatValue2,
                  y + (height - size) / 2.0F,
                  size,
                  size,
                  ProxyDotRadiusData.method8069(4.0F),
                  new ZenithColor(-1).method7294(previewAlpha)
               );
               floatValue2 += size + 6.0F;
            }

            if (previewAlpha > 0.5F) {
               float previewAlpha2 = (previewAlpha - 0.5F) * 2.0F;
               customDrawContext.drawText(
                  font,
                  font.clip(data.text, width - (floatValue2 - floatValue) - 8.0F),
                  floatValue2,
                  y + (data.text2.isEmpty() ? (height - font.height()) / 2.0F : 4.0F),
                  new ZenithColor(-1).method7294(previewAlpha2)
               );
               if (!data.text2.isEmpty()) {
                  customDrawContext.drawText(
                     font2,
                     font2.clip(data.text2, width - (floatValue2 - floatValue) - 8.0F),
                     floatValue2,
                     y + 6.0F + font.height(),
                     new ZenithColor(-1325400065).method7294(previewAlpha2)
                  );
               }
            }

            y += (height + 4.0F) * previewAlpha;
         } else {
            items4.remove(data);
         }
      }
   }

   private static void method651() {
      if (method632().level == null) {
         items5.clear();
      } else {
         long now = System.currentTimeMillis();

         for (ScriptImGui.Entry3 entry3 : items5) {
            if (!entry3.script.isRunning()) {
               items5.remove(entry3);
            } else {
               Entity entity = method632().level.getEntity(entry3.intValue);
               if (entity != null) {
                  entry3.deque.addLast(new double[]{entity.getX(), entity.getY() + entity.getBbHeight() * 0.1, entity.getZ(), now});
               }

               while (!entry3.deque.isEmpty() && now - ((double[])entry3.deque.peekFirst())[3] > entry3.longValue) {
                  entry3.deque.pollFirst();
               }

               while (entry3.deque.size() > 400) {
                  entry3.deque.pollFirst();
               }
            }
         }
      }
   }

   private static void method652() {
      for (ScriptImGui.Entry3 entry3 : items5) {
         double[] doubles = null;
         int n = 0;
         int size = entry3.deque.size();

         for (double[] doubles2 : entry3.deque) {
            if (doubles != null) {
               float f = (float)n / Math.max(1, size);
               int n2 = (int)((entry3.color >>> 24) * f);
               GlowUtil.method2243(doubles[0], doubles[1], doubles[2], doubles2[0], doubles2[1], doubles2[2], n2 << 24 | entry3.color & 16777215, entry3.width, false);
            }

            doubles = doubles2;
            n++;
         }
      }
   }

   public static void method653(Script script, Scriptable scope, ScriptableObject scriptableObject) {
      method633();
      script.method1413(() -> items5.removeIf(var1x -> var1x.script == script));
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("trail", var1x -> {
         Entity var2x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2x == null) {
            return false;
         } else {
            items5.removeIf(var2xx -> var2xx.script == script && var2xx.intValue == var2x.getId());
            if (items5.stream().filter(var1xx -> var1xx.script == script).count() >= 32L) {
               throw ScriptValueUtils.method6251("больше 32 следов");
            } else {
               ScriptImGui.Entry3 var3x = new ScriptImGui.Entry3();
               var3x.script = script;
               var3x.intValue = var2x.getId();
               var3x.longValue = (long)Mth.clamp(ScriptValueUtils.method6256(var1x, 1, 1500.0), 100.0, 20000.0);
               var3x.color = ScriptValueUtils.method6258(var1x, 2, method642());
               var3x.width = (float)Mth.clamp(ScriptValueUtils.method6256(var1x, 3, 2.0), 0.5, 8.0);
               items5.add(var3x);
               return true;
            }
         }
      });
      entry2.method6274("clearTrails", var1x -> {
         items5.removeIf(var1xx -> var1xx.script == script);
         return null;
      });
      entry2.method6274(
         "sphere",
         var0x -> {
            ScriptDrawApi.method2602();
            double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
            double var3x = ScriptValueUtils.method6256(var0x, 1, 0.0);
            double d = ScriptValueUtils.method6256(var0x, 2, 0.0);
            double d2 = Mth.clamp(ScriptValueUtils.method6256(var0x, 3, 1.0), 0.05, 64.0);
            int color = ScriptValueUtils.method6258(var0x, 4, method642());
            int n = Mth.clamp(ScriptValueUtils.method6257(var0x, 5, 16), 6, 48);
            float width = (float)Mth.clamp(ScriptValueUtils.method6256(var0x, 6, 1.5), 0.5, 6.0);

            for (int i = 1; i < n / 2; i++) {
               double d3 = Math.PI * i / (n / 2.0);
               double d4 = Math.sin(d3) * d2;
               double y = var3x + Math.cos(d3) * d2;
               Vec3 vec = null;

               for (int j = 0; j <= n; j++) {
                  double d5 = (Math.PI * 2) * j / n;
                  Vec3 vec2 = new Vec3(var1x + Math.cos(d5) * d4, y, d + Math.sin(d5) * d4);
                  if (vec != null) {
                     GlowUtil.method2245(vec, vec2, color, width, false);
                  }

                  vec = vec2;
               }
            }

            for (int i = 0; i < n / 2; i++) {
               double d3 = (Math.PI * 2) * i / (n / 2.0);
               Vec3 vec = null;

               for (int j = 0; j <= n; j++) {
                  double d4 = Math.PI * j / n;
                  Vec3 vec2 = new Vec3(
                     var1x + Math.sin(d4) * Math.cos(d3) * d2, var3x + Math.cos(d4) * d2, d + Math.sin(d4) * Math.sin(d3) * d2
                  );
                  if (vec != null) {
                     GlowUtil.method2245(vec, vec2, color, width, false);
                  }

                  vec = vec2;
               }
            }

            return null;
         }
      );
      entry2.method6274("beam", var0x -> {
         ScriptDrawApi.method2602();
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         double var3x = ScriptValueUtils.method6256(var0x, 1, 0.0);
         double z = ScriptValueUtils.method6256(var0x, 2, 0.0);
         double d = Mth.clamp(ScriptValueUtils.method6256(var0x, 3, 64.0), 0.1, 512.0);
         int color = ScriptValueUtils.method6258(var0x, 4, method642());
         float width = (float)Mth.clamp(ScriptValueUtils.method6256(var0x, 5, 3.0), 0.5, 10.0);
         GlowUtil.method2245(new Vec3(var1x, var3x, z), new Vec3(var1x, var3x + d, z), color, width, false);
         GlowUtil.method2245(new Vec3(var1x, var3x, z), new Vec3(var1x, var3x + d, z), color & 16777215 | 805306368, width * 4.0F, false);
         return null;
      });
      entry2.method6274(
         "item",
         var0x -> {
            ItemStack var1x = ScriptDrawApi.method2608(ScriptValueUtils.method6254(var0x, 0, ""), 1);
            if (!var1x.isEmpty() && items6.size() < 256) {
               items6.add(
                  new Object[]{
                     var1x,
                     ScriptValueUtils.method6256(var0x, 1, 0.0),
                     ScriptValueUtils.method6256(var0x, 2, 0.0),
                     ScriptValueUtils.method6256(var0x, 3, 0.0),
                     (float)Mth.clamp(ScriptValueUtils.method6256(var0x, 4, 16.0), 4.0, 64.0)
                  }
               );
               return true;
            } else {
               return false;
            }
         }
      );
   }

   public static void method654(Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "glow",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            float var2x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
            float f = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float f2 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
            float f3 = (float)ScriptValueUtils.method6256(var0x, 3, 0.0);
            int n = ScriptValueUtils.method6258(var0x, 4, method642());
            float f4 = (float)ScriptValueUtils.method6256(var0x, 5, 4.0);
            int n2 = Mth.clamp(ScriptValueUtils.method6257(var0x, 6, 8), 1, 24);
            int n3 = n >>> 24;

            for (int i = n2; i >= 1; i--) {
               float f5 = (float)i / n2;
               int n4 = (int)(n3 * 0.18F * (1.0F - f5) * (1.0F - f5) + 2.0F);
               var1x.drawRoundedRect(
                  var2x - i,
                  f - i,
                  f2 + i * 2,
                  f3 + i * 2,
                  ProxyDotRadiusData.method8069(f4 + i),
                  new ZenithColor(n4 << 24 | n & 16777215)
               );
            }

            return null;
         }
      );
      entry2.method6274("glowText", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         String var2x = ScriptValueUtils.method6254(var0x, 0, "");
         float floatValue = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float centeredTextY = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         int intValue = ScriptValueUtils.method6258(var0x, 3, -1);
         Font font = ScriptDrawApi.method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 8.0));
         int n = ScriptValueUtils.method6258(var0x, 6, method642());
         float f = (float)Mth.clamp(ScriptValueUtils.method6256(var0x, 7, 1.5), 0.5, 4.0);
         ZenithColor zenithColor = new ZenithColor((n >>> 24) / 6 << 24 | n & 16777215);

         for (int i = 1; i <= 2; i++) {
            float f2 = f * i / 2.0F;

            for (int j = 0; j < 8; j++) {
               double d = (Math.PI / 4) * j;
               var1x.drawText(font, var2x, floatValue + (float)Math.cos(d) * f2, centeredTextY + (float)Math.sin(d) * f2, zenithColor);
            }
         }

         var1x.drawText(font, var2x, floatValue, centeredTextY, new ZenithColor(intValue));
         return (double)font.width(var2x);
      });
      entry2.method6274("outlineText", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         String var2x = ScriptValueUtils.method6254(var0x, 0, "");
         float floatValue = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float centeredTextY = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         Font font = ScriptDrawApi.method2605(ScriptValueUtils.arg(var0x, 6), (float)ScriptValueUtils.method6256(var0x, 5, 8.0));
         ZenithColor zenithColor = new ZenithColor(ScriptValueUtils.method6258(var0x, 4, -16777216));
         float f = Math.max(0.4F, font.height() / 14.0F);
         var1x.drawText(font, var2x, floatValue - f, centeredTextY, zenithColor);
         var1x.drawText(font, var2x, floatValue + f, centeredTextY, zenithColor);
         var1x.drawText(font, var2x, floatValue, centeredTextY - f, zenithColor);
         var1x.drawText(font, var2x, floatValue, centeredTextY + f, zenithColor);
         var1x.drawText(font, var2x, floatValue, centeredTextY, new ZenithColor(ScriptValueUtils.method6258(var0x, 3, -1)));
         return (double)font.width(var2x);
      });
      entry2.method6274("chromaText", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         String var2x = ScriptValueUtils.method6254(var0x, 0, "");
         float f = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float centeredTextY = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         Font font = ScriptDrawApi.method2605(ScriptValueUtils.arg(var0x, 4), (float)ScriptValueUtils.method6256(var0x, 3, 8.0));
         double d = Math.max(1.0, ScriptValueUtils.method6256(var0x, 5, 8.0));
         double d2 = ScriptValueUtils.method6256(var0x, 6, 12.0);
         float floatValue = f;

         for (int i = 0; i < var2x.length(); i++) {
            String text = String.valueOf(var2x.charAt(i));
            float hue = (float)((System.currentTimeMillis() / d + i * d2) % 360.0 / 360.0);
            var1x.drawText(font, text, floatValue, centeredTextY, new ZenithColor(Color.HSBtoRGB(hue, 0.75F, 1.0F) | 0xFF000000));
            floatValue += font.width(text);
         }

         return (double)(floatValue - f);
      });
   }

   public static void method655(Script workspace, Scriptable scope, ScriptableObject scriptableObject) {
      workspace.method1413(() -> ScriptRenderOverrides.clearPlayerSkinOverrides(workspace));
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("skin", var1x -> {
         Object var2x = ScriptValueUtils.arg(var1x, 0);
         if (!ScriptValueUtils.method6253(var2x) && !(var2x instanceof Boolean)) {
            RemoteUtil.Entry var3x = RemoteUtil.method1827(var2x);
            if (var3x.width() == 64 && (var3x.height() == 64 || var3x.height() == 32)) {
               Identifier texture = var3x.texture();
               if (texture == null) {
                  throw ScriptValueUtils.method6251("картинка ещё не готова");
               } else {
                  ScriptRenderOverrides.setPlayerSkinTexture(workspace, texture, ScriptValueUtils.method6259(var1x, 1, false));
                  return null;
               }
            } else {
               throw ScriptValueUtils.method6251("скин — картинка 64×64 (или 64×32)");
            }
         } else {
            ScriptRenderOverrides.setPlayerSkinTexture(workspace, null, false);
            return null;
         }
      });
      entry2.method6274("cape", var1x -> {
         Object var2x = ScriptValueUtils.arg(var1x, 0);
         if (!ScriptValueUtils.method6253(var2x) && !(var2x instanceof Boolean)) {
            RemoteUtil.Entry var3x = RemoteUtil.method1827(var2x);
            if (var3x.width() != 64 * var3x.height() / 32 && var3x.width() != 2 * var3x.height()) {
               throw ScriptValueUtils.method6251("плащ — картинка 64×32 (или кратная)");
            } else {
               Identifier texture = var3x.texture();
               if (texture == null) {
                  throw ScriptValueUtils.method6251("картинка ещё не готова");
               } else {
                  ScriptRenderOverrides.setPlayerCapeTexture(workspace, texture);
                  return null;
               }
            }
         } else {
            ScriptRenderOverrides.setPlayerCapeTexture(workspace, null);
            return null;
         }
      });
   }

   private static final class Data {
      private final Script script;
      private final String text;
      private final String text2;
      private final int intValue;
      private final RemoteUtil.Entry entry;
      private final long longValue;
      private final long longValue2;

      public Script script() {
         return this.script;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue() {
         return this.intValue;
      }

      public RemoteUtil.Entry entry() {
         return this.entry;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.entry, other.entry) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(script, text, text2, intValue, entry, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "script=" + this.script + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + ", " + "entry=" + this.entry + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      private Data(Script script, String text, String str, int intValue, RemoteUtil.Entry entry, long longValue, long l) {
         this.script = script;
         this.text = text;
         this.text2 = str;
         this.intValue = intValue;
         this.entry = entry;
         this.longValue = longValue;
         this.longValue2 = l;
      }

      public Script getScript() {
         return this.script;
      }

      public String title() {
         return this.text;
      }

      public String text() {
         return this.text2;
      }

      public int color() {
         return this.intValue;
      }

      public RemoteUtil.Entry getEntry() {
         return this.entry;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   private static final class Entry {
      public Script script;
      public Storage storage;
      public RemoteUtil.Entry entry;
      public Function function;
      public float size;
      public boolean flag;
   }

   public static final class Entry2 {
      public static float method656() {
         Minecraft minecraft = Minecraft.getInstance();
         return (float)(minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / Math.max(1, minecraft.getWindow().getScreenWidth()));
      }

      public static float method657() {
         Minecraft minecraft = Minecraft.getInstance();
         return (float)(minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / Math.max(1, minecraft.getWindow().getScreenHeight()));
      }
   }

   private static final class Entry3 {
      public Script script;
      public int intValue;
      public long longValue;
      public int color;
      public float width;
      public final Deque<double[]> deque = new ArrayDeque<>();
   }
}
