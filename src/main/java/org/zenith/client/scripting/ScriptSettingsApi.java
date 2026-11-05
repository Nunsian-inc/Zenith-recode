package org.zenith.client.scripting;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ZenithColor;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;

public final class ScriptSettingsApi {
   private ScriptSettingsApi() {
   }

   public static void method1221(ScriptValueUtils.Entry2 entry2, Script workspace, Storage storage, Scriptable scope, ScriptSettingsApi.Handler handler) {
      BooleanSetting[] booleanSettings = new BooleanSetting[]{null};
      java.util.function.Consumer<org.zenith.client.settings.Setting> consumer = var2x -> {
         if (booleanSettings[0] != null) {
            BooleanSetting var3x = booleanSettings[0];
            var2x.setVisible(var3x::isEnabled);
         }

         if (handler.contains(var2x.getKey())) {
            throw ScriptValueUtils.method6251("настройка «" + var2x.getKey() + "» уже есть");
         } else {
            JsonObject var6x = handler.method1228();
            if (var6x != null) {
               try {
                  if (var6x.has(var2x.getKey()) || var2x instanceof MultiSelectSetting) {
                     var2x.load(var6x);
                  }
               } catch (RuntimeException var5x) {
               }
            }

            handler.method1227(var2x);
         }
      };
      entry2.method6274("checkbox", var4x -> {
         BooleanSetting var5x = new BooleanSetting(method1222(var4x), ScriptValueUtils.method6259(var4x, 1, false));
         consumer.accept(var5x);
         return method1224(scope, var5x, workspace, storage);
      });
      entry2.method6274(
         "slider",
         var4x -> {
            float var5x = (float)ScriptValueUtils.method6256(var4x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var4x, 3, 1.0);
            if (var6x < var5x) {
               float f = var5x;
               var5x = var6x;
               var6x = f;
            }

            float f = (float)Math.max(1.0E-4, ScriptValueUtils.method6256(var4x, 4, 0.1));
            float floatValue = (float)Math.max((double)var5x, Math.min((double)var6x, ScriptValueUtils.method6256(var4x, 1, var5x)));
            SliderSetting sliderSetting = new SliderSetting(
               method1222(var4x),
               floatValue,
               var5x,
               var6x,
               f,
               "",
               ScriptValueUtils.method6254(var4x, 5, ""),
               (Supplier<Boolean>)null,
               (SliderSetting.Handler2)null
            );
            consumer.accept(sliderSetting);
            return method1224(scope, sliderSetting, workspace, storage);
         }
      );
      entry2.method6274("mode", var4x -> {
         var var5x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var4x, 1));
         if (var5x.isEmpty()) {
            throw ScriptValueUtils.method6251("mode: нужен массив вариантов");
         } else {
            String[] var6x = var5x.stream().map(ScriptValueUtils::method6255).toArray(String[]::new);
            ModeSetting modeSetting = new ModeSetting(method1222(var4x), "", var6x);
            String text = ScriptValueUtils.method6254(var4x, 2, var6x[0]);
            modeSetting.set(text);
            consumer.accept(modeSetting);
            return method1224(scope, modeSetting, workspace, storage);
         }
      });
      entry2.method6274("multi", var4x -> {
         var var5x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var4x, 1));
         if (var5x.isEmpty()) {
            throw ScriptValueUtils.method6251("multi: нужен массив вариантов");
         } else {
            var var6x = var5x.stream().map(ScriptValueUtils::method6255).toList();
            MultiSelectSetting sliderSetting = MultiSelectSetting.method5223(method1222(var4x), var6x);
            var strings = ScriptValueUtils.method6269(ScriptValueUtils.arg(var4x, 2)).stream().map(ScriptValueUtils::method6255).toList();

            for (MultiSelectSetting.Entry entry : sliderSetting.getItems2()) {
               entry.setEnabled(strings.stream().anyMatch(var1xx -> var1xx.equalsIgnoreCase(entry.getKey())));
            }

            consumer.accept(sliderSetting);
            return method1224(scope, sliderSetting, workspace, storage);
         }
      });
      entry2.method6274("color", var4x -> {
         ColorSetting var5x = new ColorSetting(method1222(var4x), new ZenithColor(ScriptValueUtils.method6258(var4x, 1, -1)));
         consumer.accept(var5x);
         return method1224(scope, var5x, workspace, storage);
      });
      entry2.method6274(
         "text",
         var4x -> {
            TextSetting var5x = new TextSetting(
               method1222(var4x), ScriptValueUtils.method6254(var4x, 1, ""), ScriptValueUtils.method6254(var4x, 2, ""), TextSetting.Entry.method2125(256)
            );
            consumer.accept(var5x);
            return method1224(scope, var5x, workspace, storage);
         }
      );
      entry2.method6274("bind", var4x -> {
         BindSetting var5x = new BindSetting(method1222(var4x), ScriptValueUtils.method6257(var4x, 1, -1));
         consumer.accept(var5x);
         return method1224(scope, var5x, workspace, storage);
      });
      entry2.method6274("button", var4x -> {
         var var5x = ScriptValueUtils.method6260(var4x, 1);
         ButtonSetting var6x = new ButtonSetting(method1222(var4x), () -> storage.method1321(workspace, var5x));
         consumer.accept(var6x);
         return method1224(scope, var6x, workspace, storage);
      });
      entry2.method6274(
         "group",
         var6x -> {
            BooleanSetting booleanSetting = new BooleanSetting(
               "▾ " + method1222(var6x), !ScriptValueUtils.method6252(var6x, 1) || ScriptValueUtils.method6259(var6x, 1, true)
            );
            consumer.accept(booleanSetting);
            ScriptableObject scriptableObject = entry2.getScriptableObject();
            ScriptValueUtils.Entry2 entry22 = ScriptValueUtils.method6247(scope);

            for (String str : new String[]{"checkbox", "slider", "mode", "multi", "color", "text", "bind", "button"}) {
               entry22.method6274(str, var5xx -> {
                  Function var6xx = (Function)scriptableObject.get(str, scriptableObject);
                  BooleanSetting var7x = booleanSettings[0];
                  booleanSettings[0] = booleanSetting;

                  Object var8x;
                  try {
                     var8x = var6xx.call(Context.getCurrentContext(), scope, scriptableObject, var5xx);
                  } finally {
                     booleanSettings[0] = var7x;
                  }

                  return var8x;
               });
            }

            entry22.method6277("toggle", method1224(scope, booleanSetting, workspace, storage));
            entry22.method6274("open", var1xx -> booleanSetting.isEnabled());
            return entry22.getScriptableObject();
         }
      );
   }

   private static String method1222(Object[] objects) {
      String str = ScriptValueUtils.method6254(objects, 0, "").trim();
      if (!str.isEmpty() && str.length() <= 64) {
         return str;
      } else {
         throw ScriptValueUtils.method6251("имя настройки: от 1 до 64 символов");
      }
   }

   public static Scriptable method1223(Scriptable scope, Setting setting) {
      return method1224(scope, setting, null, null);
   }

   public static Scriptable method1224(Scriptable scope, Setting sliderSetting, Script workspace, Storage storage) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6277("name", sliderSetting.getKey());
      if (workspace != null) {
         var visible = sliderSetting.getVisible();
         entry2.method6274("visibleIf", var5x -> {
            var var6x = ScriptValueUtils.method6260(var5x, 0);
            ScriptableObject var7x = entry2.getScriptableObject();
            sliderSetting.setVisible(() -> {
               if (visible != null && !(Boolean)visible.get()) {
                  return false;
               } else if (!workspace.isRunning()) {
                  return true;
               } else {
                  Object var4xx = storage.method1321(workspace, var6x);
                  return var4xx == null || Context.toBoolean(var4xx);
               }
            });
            return var7x;
         });
      }

      entry2.method6274("visible", var1x -> sliderSetting.isVisible());
      if (sliderSetting instanceof BooleanSetting booleanSetting) {
         entry2.method6277("type", "checkbox");
         entry2.method6274("get", var1x -> booleanSetting.isEnabled());
         entry2.method6274("set", var1x -> {
            booleanSetting.setEnabled(ScriptValueUtils.method6259(var1x, 0, true));
            return null;
         });
         entry2.method6274("toggle", var1x -> {
            booleanSetting.setEnabled(!booleanSetting.isEnabled());
            return booleanSetting.isEnabled();
         });
      } else if (sliderSetting instanceof SliderSetting sliderSetting2) {
         entry2.method6277("type", "slider");
         entry2.method6277("min", (double)sliderSetting2.getMin()).method6277("max", (double)sliderSetting2.getMax()).method6277("step", (double)sliderSetting2.getFloatValue2());
         entry2.method6274("get", var1x -> (double)sliderSetting2.getValue3());
         entry2.method6274("set", var1x -> {
            sliderSetting2.method2400((float)ScriptValueUtils.method6256(var1x, 0, sliderSetting2.getValue3()));
            return null;
         });
      } else if (sliderSetting instanceof ModeSetting modeSetting) {
         entry2.method6277("type", "mode");
         entry2.method6274("get", var1x -> modeSetting.get());
         entry2.method6274("set", var1x -> {
            modeSetting.set(ScriptValueUtils.method6254(var1x, 0, modeSetting.get()));
            return null;
         });
         entry2.method6274("is", var1x -> modeSetting.method2101(ScriptValueUtils.method6254(var1x, 0, "")));
         entry2.method6274("options", var2x -> {
            ArrayList var3x = new ArrayList();

            for (ModeSetting.Entry var5x : modeSetting.getValues()) {
               var3x.add(var5x.getKey());
            }

            return ScriptValueUtils.method6267(scope, var3x);
         });
      } else if (sliderSetting instanceof MultiSelectSetting multiSelectSetting) {
         entry2.method6277("type", "multi");
         entry2.method6274("get", var2x -> ScriptValueUtils.method6267(scope, new ArrayList<>(multiSelectSetting.method5229())));
         entry2.method6274("enabled", var1x -> {
            MultiSelectSetting.Entry var2x = multiSelectSetting.method5222(ScriptValueUtils.method6254(var1x, 0, ""));
            return var2x != null && var2x.isEnabled();
         });
         entry2.method6274("set", var1x -> {
            var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 0)).stream().map(ScriptValueUtils::method6255).toList();

            for (MultiSelectSetting.Entry var4x : multiSelectSetting.getItems2()) {
               var4x.setEnabled(var2x.stream().anyMatch(var1xx -> var1xx.equalsIgnoreCase(var4x.getKey())));
            }

            return null;
         });
      } else if (sliderSetting instanceof ColorSetting colorSetting) {
         entry2.method6277("type", "color");
         entry2.method6274("get", var1x -> (double)colorSetting.method1445());
         entry2.method6274("set", var1x -> {
            colorSetting.method1446(ScriptValueUtils.method6258(var1x, 0, colorSetting.method1445()));
            return null;
         });
      } else if (sliderSetting instanceof TextSetting textSetting) {
         entry2.method6277("type", "text");
         entry2.method6274("get", var1x -> textSetting.getValue() == null ? "" : textSetting.getValue());
         entry2.method6274("set", var1x -> {
            textSetting.method2122(ScriptValueUtils.method6254(var1x, 0, ""));
            return null;
         });
      } else if (sliderSetting instanceof BindSetting bindSetting) {
         entry2.method6277("type", "bind");
         entry2.method6274("get", var1x -> (double)bindSetting.getKeyCode());
         entry2.method6274("set", var1x -> {
            bindSetting.setKeyCode(ScriptValueUtils.method6257(var1x, 0, -1));
            return null;
         });
         entry2.method6274("isKey", var1x -> bindSetting.getKeyCode() >= 0 && bindSetting.getKeyCode() == ScriptValueUtils.method6257(var1x, 0, -2));
      } else if (sliderSetting instanceof ButtonSetting buttonSetting) {
         entry2.method6277("type", "button");
         entry2.method6274("press", var1x -> {
            buttonSetting.getRunnable().run();
            return null;
         });
      } else {
         entry2.method6277("type", "other");
      }

      return entry2.getScriptableObject();
   }

   public static ScriptSettingsApi.Handler method1225(final List<Setting> settings, final JsonObject jsonObject, final Consumer<Setting> consumer) {
      return new ScriptSettingsApi.Handler() {
         @Override
         public void method1227(Setting var1x) {
            settings.add(var1x);
            consumer.accept(var1x);
         }

         @Override
         public JsonObject method1228() {
            return jsonObject;
         }

         @Override
         public boolean contains(String var1x) {
            return settings.stream().anyMatch(var1xxx -> var1xxx.getKey().equalsIgnoreCase(var1x));
         }
      };
   }

   public static ScriptSettingsApi.Handler method1226(Script workspace, final JsonObject json) {
      var map = ScriptModuleApi.method323(workspace);
      return new ScriptSettingsApi.Handler() {
         @Override
         public void method1227(Setting var1x) {
            map.put(var1x.getKey(), var1x);
         }

         @Override
         public JsonObject method1228() {
            return json;
         }

         @Override
         public boolean contains(String var1x) {
            return map.containsKey(var1x);
         }
      };
   }

   public interface Handler {
      void method1227(Setting setting);

      JsonObject method1228();

      boolean contains(String key);
   }
}
