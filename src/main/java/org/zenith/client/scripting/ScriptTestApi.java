package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptTestApi {
   private static final Map<Script, Map<String, Function>> items = new WeakHashMap<>();
   private static final Map<Script, Map<String, String>> items2 = new WeakHashMap<>();
   private static final Map<Script, Boolean> items3 = new WeakHashMap<>();
   private static final Map<Script, Map<String, double[]>> items4 = new WeakHashMap<>();
   private static final Map<Script, Map<String, Long>> items5 = new WeakHashMap<>();
   private static final List<Object[]> items6 = new CopyOnWriteArrayList<>();
   private static boolean flag;

   private ScriptTestApi() {
   }

   private static synchronized void method5409() {
      if (!flag) {
         flag = true;
         EventManager.main().subscribe(DrawEvent.class, (byte)3, (obj, obj2) -> method5414(((DrawEvent)obj2).getCustomDrawContext()));
         EventManager.main().subscribe(WorldRenderEvent.class, (byte)3, (obj, obj2) -> method5415());
      }
   }

   public static void method5410(Storage storage, Script workspace, ScriptableObject scope) {
      method5409();
      workspace.method1413(() -> {
         items2.remove(workspace);
         items3.remove(workspace);
         items6.removeIf(var1x -> var1x[0] == workspace);
         items.remove(workspace);
      });
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scope);
      entry2.method6274("test", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "тест");
         var var3x = items.computeIfAbsent(workspace, var0xx -> new LinkedHashMap<>());
         if (var3x.size() >= 200 && !var3x.containsKey(var2x)) {
            throw ScriptValueUtils.method6251("больше 200 тестов");
         } else {
            var3x.put(var2x, ScriptValueUtils.method6260(var1x, 1));
            return null;
         }
      });
      items.remove(workspace);
      entry2.method6274(
         "assert",
         var0x -> {
            if (!ScriptValueUtils.method6259(var0x, 0, false)) {
               throw ScriptValueUtils.method6251(
                  "проверка не прошла" + (ScriptValueUtils.method6252(var0x, 1) ? ": " + ScriptValueUtils.method6254(var0x, 1, "") : "")
               );
            } else {
               return true;
            }
         }
      );
      ScriptValueUtils.Entry2 entry22 = ScriptValueUtils.method6246(scope, (ScriptableObject)scope.get("assert", scope));
      entry22.method6274(
         "eq",
         var0x -> {
            String var1x = ScriptValueUtils.method6271(ScriptValueUtils.arg(var0x, 0), 0).toString();
            String var2x = ScriptValueUtils.method6271(ScriptValueUtils.arg(var0x, 1), 0).toString();
            if (!var1x.equals(var2x)) {
               throw ScriptValueUtils.method6251(
                  "ожидалось "
                     + var2x
                     + ", получено "
                     + var1x
                     + (ScriptValueUtils.method6252(var0x, 2) ? " — " + ScriptValueUtils.method6254(var0x, 2, "") : "")
               );
            } else {
               return true;
            }
         }
      );
      entry22.method6274("near", var0x -> {
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         double var3x = ScriptValueUtils.method6256(var0x, 1, 0.0);
         double var5x = ScriptValueUtils.method6256(var0x, 2, 1.0E-6);
         if (Math.abs(var1x - var3x) > var5x) {
            throw ScriptValueUtils.method6251("ожидалось ≈" + var3x + ", получено " + var1x);
         } else {
            return true;
         }
      });
      entry22.method6274(
         "throws",
         var1x -> {
            var var2x = ScriptValueUtils.method6260(var1x, 0);

            try {
               var2x.call(Context.getCurrentContext(), scope, scope, new Object[0]);
            } catch (RhinoException var4x) {
               return true;
            }

            throw ScriptValueUtils.method6251(
               "ожидалась ошибка" + (ScriptValueUtils.method6252(var1x, 1) ? ": " + ScriptValueUtils.method6254(var1x, 1, "") : "")
            );
         }
      );
      ScriptValueUtils.Entry2 entry23 = ScriptValueUtils.method6247(scope);
      entry23.method6274(
         "watch",
         var1x -> {
            var var2x = items2.computeIfAbsent(workspace, var0xx -> new LinkedHashMap<>());
            String var3x = ScriptValueUtils.method6254(var1x, 0, "value");
            if (var2x.size() >= 40 && !var2x.containsKey(var3x)) {
               throw ScriptValueUtils.method6251("больше 40 наблюдений");
            } else {
               Object var4x = ScriptValueUtils.arg(var1x, 1);
               String var5x = var4x instanceof Scriptable var6x && !(var4x instanceof Function)
                  ? ScriptValueUtils.method6271(var6x, 0).toString()
                  : ScriptValueUtils.method6255(var4x);
               var2x.put(var3x, var5x.length() > 120 ? var5x.substring(0, 120) + "…" : var5x);
               return null;
            }
         }
      );
      entry23.method6274("unwatch", var1x -> {
         var var2x = items2.get(workspace);
         if (var2x != null) {
            var2x.remove(ScriptValueUtils.method6254(var1x, 0, ""));
         }

         return null;
      });
      entry23.method6274("panel", var1x -> {
         boolean var2x = !ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true);
         items3.put(workspace, var2x);
         return var2x;
      });
      entry23.method6274(
         "line",
         var1x -> method5411(
            workspace,
            "line",
            new double[]{
               ScriptValueUtils.method6256(var1x, 0, 0.0),
               ScriptValueUtils.method6256(var1x, 1, 0.0),
               ScriptValueUtils.method6256(var1x, 2, 0.0),
               ScriptValueUtils.method6256(var1x, 3, 0.0),
               ScriptValueUtils.method6256(var1x, 4, 0.0),
               ScriptValueUtils.method6256(var1x, 5, 0.0)
            },
            ScriptValueUtils.method6258(var1x, 6, -43691),
            ScriptValueUtils.method6256(var1x, 7, 2000.0),
            null
         )
      );
      entry23.method6274(
         "box",
         var1x -> method5411(
            workspace,
            "box",
            new double[]{ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)},
            ScriptValueUtils.method6258(var1x, 3, -43691),
            ScriptValueUtils.method6256(var1x, 4, 2000.0),
            null
         )
      );
      entry23.method6274(
         "point",
         var1x -> method5411(
            workspace,
            "point",
            new double[]{ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)},
            ScriptValueUtils.method6258(var1x, 3, 65365),
            ScriptValueUtils.method6256(var1x, 4, 2000.0),
            null
         )
      );
      entry23.method6274(
         "text",
         var1x -> method5411(
            workspace,
            "text",
            new double[]{ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0), ScriptValueUtils.method6256(var1x, 3, 0.0)},
            ScriptValueUtils.method6258(var1x, 4, -1),
            ScriptValueUtils.method6256(var1x, 5, 2000.0),
            ScriptValueUtils.method6254(var1x, 0, "")
         )
      );
      entry23.method6274("clear", var1x -> {
         items6.removeIf(var1xx -> var1xx[0] == workspace);
         var var2x = items2.get(workspace);
         if (var2x != null) {
            var2x.clear();
         }

         return null;
      });
      entry2.method6277("debug", entry23.getScriptableObject());
      ScriptValueUtils.Entry2 entry24 = ScriptValueUtils.method6247(scope);
      entry24.method6274("mark", var1x -> {
         items5.computeIfAbsent(workspace, var0xx -> new LinkedHashMap<>()).put(ScriptValueUtils.method6254(var1x, 0, "mark"), System.nanoTime());
         return null;
      });
      entry24.method6274("end", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "mark");
         var var3x = items5.get(workspace);
         Long var4x = var3x == null ? null : (Long)var3x.remove(var2x);
         if (var4x == null) {
            return null;
         } else {
            double var5x = (System.nanoTime() - var4x) / 1000000.0;
            method5412(workspace, var2x, var5x);
            return var5x;
         }
      });
      entry24.method6274("measure", var2x -> {
         String var3x = ScriptValueUtils.method6254(var2x, 0, "measure");
         var var4x = ScriptValueUtils.method6260(var2x, 1);
         long var5x = System.nanoTime();

         Object var7x;
         try {
            var7x = var4x.call(Context.getCurrentContext(), scope, scope, new Object[0]);
         } finally {
            method5412(workspace, var3x, (System.nanoTime() - var5x) / 1000000.0);
         }

         return var7x;
      });
      entry24.method6274(
         "report",
         var2x -> {
            ArrayList var3x = new ArrayList();
            var var4x = items4.get(workspace);
            if (var4x != null) {
               var4x.forEach(
                  (var2xx, var3xx) -> var3x.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("name", var2xx)
                        .method6277("count", var3xx[0])
                        .method6277("totalMs", var3xx[1])
                        .method6277("avgMs", var3xx[0] == 0.0 ? 0.0 : var3xx[1] / var3xx[0])
                        .method6277("maxMs", var3xx[2])
                        .getScriptableObject()
                  )
               );
            }

            return ScriptValueUtils.method6267(scope, var3x);
         }
      );
      entry24.method6274("reset", var1x -> {
         items4.remove(workspace);
         items5.remove(workspace);
         return null;
      });
      entry2.method6277("profile", entry24.getScriptableObject());
      ScriptValueUtils.Entry2 entry25 = ScriptValueUtils.method6246(scope, (ScriptableObject)scope.get("events", scope));
      entry25.method6274("every", var2x -> {
         int var3x = Mth.clamp(ScriptValueUtils.method6257(var2x, 0, 20), 1, 72000);
         return (double)storage.method1329(workspace, ScriptValueUtils.method6260(var2x, 1), var3x * 50L, true);
      });
      entry25.method6274("debounce", var3x -> {
         long var4x = (long)Mth.clamp(ScriptValueUtils.method6256(var3x, 0, 200.0), 1.0, 600000.0);
         var var6x = ScriptValueUtils.method6260(var3x, 1);
         Object[][] var7x = new Object[][]{new Object[0]};
         int[] var8x = new int[]{-1};
         BaseFunction var9x = ScriptValueUtils.method6249(scope, "debounced", var3xx -> var6x.call(Context.getCurrentContext(), scope, scope, var7x[0]));
         return ScriptValueUtils.method6249(scope, "debounce", var7xx -> {
            var7x[0] = (Object[])var7xx.clone();
            if (var8x[0] >= 0) {
               storage.method1330(workspace, var8x[0]);
            }

            var8x[0] = storage.method1329(workspace, var9x, var4x, false);
            return null;
         });
      });
      entry25.method6274("throttle", var1x -> {
         long var2x = (long)Mth.clamp(ScriptValueUtils.method6256(var1x, 0, 200.0), 1.0, 600000.0);
         var var4x = ScriptValueUtils.method6260(var1x, 1);
         long[] var5x = new long[]{0L};
         return ScriptValueUtils.method6249(scope, "throttle", var5xx -> {
            long var6x = System.currentTimeMillis();
            if (var6x - var5x[0] < var2x) {
               return null;
            } else {
               var5x[0] = var6x;
               return var4x.call(Context.getCurrentContext(), scope, scope, var5xx);
            }
         });
      });
      ScriptableObject scriptableObject = (ScriptableObject)scope.get("script", scope);
      ScriptValueUtils.Entry2 entry26 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry26.method6274("keep", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "state");
         if (workspace.getItems().size() >= 64 && !workspace.getItems().containsKey(var4x)) {
            throw ScriptValueUtils.method6251("больше 64 сохраняемых значений");
         } else {
            JsonElement var5x = storage.method1316(workspace, var4x);
            Object var6x = var5x != null ? ScriptValueUtils.method6273(scope, var5x) : ScriptValueUtils.arg(var3x, 1);
            workspace.getItems().put(var4x, var6x);
            return var6x;
         }
      });
      entry26.method6274("forget", var2x -> {
         String var3x = ScriptValueUtils.method6254(var2x, 0, "state");
         workspace.getItems().remove(var3x);
         storage.method1317(workspace, var3x);
         return null;
      });
      entry26.method6274("settingsUI", var1x -> {
         Minecraft.getInstance().execute(() -> {
            NLMenuScreen var1xx = Zenith.getInstance().getNLMenuScreen();
            if (var1xx != null) {
               var1xx.openScriptSettings(workspace);
            }
         });
         return null;
      });
   }

   private static Object method5411(Script workspace, String str, double[] doubles, int n, double d, String str2) {
      if (items6.stream().filter(var1x -> var1x[0] == workspace).count() >= 500L) {
         return false;
      } else {
         items6.add(new Object[]{workspace, str, doubles, n, System.currentTimeMillis() + (long)Mth.clamp(d, 50.0, 120000.0), str2});
         return true;
      }
   }

   private static void method5412(Script workspace, String key, double d) {
      var map = items4.computeIfAbsent(workspace, var0x -> new LinkedHashMap<>());
      if (map.size() < 200 || map.containsKey(key)) {
         double[] doubles = map.computeIfAbsent(key, var0x -> new double[3]);
         doubles[0]++;
         doubles[1] += d;
         doubles[2] = Math.max(doubles[2], d);
      }
   }

   public static String method5413(Storage host, Script workspace) {
      var map = items.get(workspace);
      if (map != null && !map.isEmpty()) {
         int n = 0;
         ArrayList elements = new ArrayList();

         for (Object obj : (Iterable<?>)(Object)(new LinkedHashMap(map).entrySet())) {
            Entry entry = (Entry)obj;
            if (!workspace.isRunning() || workspace.getScope() == null) {
               break;
            }

            try {
               ScriptContextFactory.method7410(1000L, (Callable)entry.getValue(), workspace.getScope(), new Object[0]);
               n++;
               host.method1327(workspace, "info", "✔ " + (String)entry.getKey());
            } catch (Throwable e) {
               String str = Storage.method1325(e);
               elements.add((String)entry.getKey() + ": " + str);
               host.method1327(workspace, "error", "✘ " + (String)entry.getKey() + " — " + str);
            }
         }

         return "тесты «"
            + workspace.method1403()
            + "»: прошло "
            + n
            + " из "
            + (n + elements.size())
            + (elements.isEmpty() ? "" : " · упали: " + String.join("; ", elements));
      } else {
         return "в скрипте нет тестов: test(\"имя\", function () { assert(...) })";
      }
   }

   private static void method5414(CustomDrawContext customDrawContext) {
      float y = 40.0F;

      for (Entry entry : new ArrayList<>(items3.entrySet())) {
         Script key = (Script)entry.getKey();
         if (Boolean.TRUE.equals(entry.getValue()) && key.isRunning()) {
            var map = items2.getOrDefault(key, Map.of());
            var map2 = items4.getOrDefault(key, Map.of());
            Font font = Fonts.NEW_SEMIBOLD.getFont(6.0F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
            ArrayList<String> strings = new ArrayList();
            map.forEach((var1x, str) -> strings.add(var1x + " = " + str));
            map2.forEach(
               (var1x, doubles) -> strings.add(
                  "⏱ " + var1x + ": " + String.format(Locale.ROOT, "%.2f мс (макс %.2f)", doubles[0] == 0.0 ? 0.0 : doubles[1] / doubles[0], doubles[2])
               )
            );
            long[] longs = key.method1409();
            double d = Math.max(0.001, (System.currentTimeMillis() - longs[3]) / 1000.0);
            strings.add(String.format(Locale.ROOT, "нагрузка: %.1f мс/с · вызовов %d", longs[0] / 1000000.0 / d, longs[1]));
            float width = font.width(key.method1403()) + 12.0F;

            for (String text : strings) {
               width = Math.max(width, font2.width(text) + 12.0F);
            }

            width = Math.min(width, 260.0F);
            float height = 12.0F + strings.size() * (font2.height() + 2.0F) + 4.0F;
            customDrawContext.drawRoundedRect(6.0F, y, width, height, ProxyDotRadiusData.method8069(4.0F), new ZenithColor(-1072952302));
            customDrawContext.drawText(font, key.method1403(), 12.0F, y + 4.0F, new ZenithColor(ScriptDrawApi.method2603()));
            float centeredTextY = y + 12.0F;

            for (String name : strings) {
               customDrawContext.drawText(font2, font2.clip(name, width - 12.0F), 12.0F, centeredTextY, new ZenithColor(-419430401));
               centeredTextY += font2.height() + 2.0F;
            }

            y += height + 4.0F;
         }
      }

      long now = System.currentTimeMillis();

      for (Object[] objects : items6) {
         if (now > (Long)objects[4] || !((Script)objects[0]).isRunning()) {
            items6.remove(objects);
         } else if ("text".equals(objects[1])) {
            double[] doubles = (double[])objects[2];
            Vec3 vec = ProjectionUtils.method301(new Vec3(doubles[0], doubles[1], doubles[2]));
            if (!(vec.z <= 0.0) && !(vec.z >= 1.0)) {
               Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
               String text = (String)objects[5];
               customDrawContext.drawText(font, text, (float)vec.x - font.width(text) / 2.0F, (float)vec.y, new ZenithColor((Integer)objects[3]));
            }
         }
      }
   }

   private static void method5415() {
      for (Object[] objects : items6) {
         double[] doubles = (double[])objects[2];
         int color = (Integer)objects[3];
         String str = (String)objects[1];
         switch (str) {
            case "line":
               GlowUtil.method2245(new Vec3(doubles[0], doubles[1], doubles[2]), new Vec3(doubles[3], doubles[4], doubles[5]), color, 2.0F, false);
               break;
            case "box":
               double minX = Math.floor(doubles[0]);
               double minY = Math.floor(doubles[1]);
               double minZ = Math.floor(doubles[2]);
               GlowUtil.method2232(new AABB(minX, minY, minZ, minX + 1.0, minY + 1.0, minZ + 1.0), color, 2.0F, true, false, false);
               break;
            case "point":
               GlowUtil.method2232(
                  new AABB(doubles[0] - 0.08, doubles[1] - 0.08, doubles[2] - 0.08, doubles[0] + 0.08, doubles[1] + 0.08, doubles[2] + 0.08), color, 1.0F, false, true, false
               );
         }
      }
   }
}
