package org.zenith.client.scripting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerArray;
import net.minecraft.network.chat.Component;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.settings.Setting;

public final class ScriptModuleApi {
   public static final String[] texts = new String[]{
      "Module",
      "events",
      "settings",
      "player",
      "world",
      "client",
      "mc",
      "input",
      "rotation",
      "render",
      "render3d",
      "Color",
      "storage",
      "console",
      "print",
      "chat",
      "include",
      "setTimeout",
      "setInterval",
      "clearTimeout",
      "clearInterval",
      "Timer",
      "script",
      "keys",
      "commands",
      "hooks",
      "requireApi",
      "sound",
      "image",
      "particles",
      "waypoints",
      "ui",
      "pathfind",
      "camera",
      "http",
      "cloud",
      "packets",
      "exports",
      "require",
      "i18n",
      "anim",
      "util",
      "ai",
      "server",
      "combat",
      "containers",
      "schematic",
      "crosshair",
      "music",
      "debug",
      "profile",
      "test",
      "assert"
   };
   private static final AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(Script.Type.values().length);

   private ScriptModuleApi() {
   }

   public static void method321(Script workspace, Script.Type key, Function function) {
      workspace.method1414(key, function);
      atomicIntegerArray.incrementAndGet(key.ordinal());
      workspace.method1413(() -> atomicIntegerArray.decrementAndGet(key.ordinal()));
   }

   public static boolean method322(Script.Type type) {
      return atomicIntegerArray.get(type.ordinal()) > 0;
   }

   public static Map<String, Setting> method323(Script workspace) {
      return workspace.getItems6();
   }

   public static Module method324(Script workspace, String str) {
      Module1 module1 = Module1.method7803(workspace, str);
      if (!module1.isEnabledRaw()) {
         module1.toggle();
      }

      workspace.method1413(() -> {
         if (module1.isEnabledRaw()) {
            module1.toggle();
         }
      });
      return module1;
   }

   public static void method325(Storage storage, Script workspace, ScriptableObject scope) {
      ScriptScreenApi.method7045(workspace, scope);
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scope);
      entry2.method6277("events", ScriptEventBus.method1869(storage, workspace, scope));
      entry2.method6277("player", ScriptScreenApi.method7049(workspace, scope));
      entry2.method6277("world", ScriptScreenApi.method7055(scope));
      entry2.method6277("client", ScriptScreenApi.method7061(workspace, storage, scope));
      entry2.method6277("mc", ScriptScreenApi.method7065(scope));
      entry2.method6277("input", ScriptScreenApi.method7056(workspace, scope));
      entry2.method6277("rotation", ScriptScreenApi.method7060(workspace, scope));
      entry2.method6277("render", ScriptDrawApi.method2607(workspace, storage, scope));
      entry2.method6277("render3d", ScriptDrawApi.method2609(scope));
      entry2.method6277("Color", ScriptDrawApi.method2611(scope));
      entry2.method6277("hooks", ScriptHooks.method5050(storage, workspace, scope));
      ScriptWorldApi.method4817(workspace, scope, (ScriptableObject)scope.get("world", scope));
      ScriptWorldApi.method4820(scope, (ScriptableObject)scope.get("player", scope));
      ScriptGuiApi.method5320(workspace, storage, scope, (ScriptableObject)scope.get("render", scope));
      ScriptGuiApi.method5322(scope, (ScriptableObject)scope.get("render3d", scope));
      ScriptValueUtils.method6246(scope, (ScriptableObject)scope.get("client", scope))
         .method6277("friends", ScriptNetApi.method3285(workspace, scope))
         .method6277("configs", ScriptNetApi.method3286(workspace, scope));
      entry2.method6277("sound", RemoteUtil.method1845(workspace, storage, scope));
      entry2.method6277("image", RemoteUtil.method1844(workspace, storage, scope));
      entry2.method6277("particles", ScriptGuiApi.method5323(workspace, storage, scope));
      entry2.method6277("waypoints", ScriptGuiApi.method5324(workspace, scope));
      entry2.method6277("ui", ScriptGuiApi.method5325(workspace, storage, scope));
      entry2.method6277("pathfind", ScriptWorldApi.method4825(workspace, scope));
      entry2.method6277("camera", ScriptWorldApi.method4828(workspace, scope));
      entry2.method6277("http", ScriptNetApi.method3281(workspace, storage, scope));
      entry2.method6277("cloud", ScriptNetApi.method3283(workspace, storage, scope));
      entry2.method6277("packets", ScriptNetApi.method3284(workspace, scope));
      ScriptNotifyApi.method2200(scope, (ScriptableObject)scope.get("world", scope));
      ScriptNotifyApi.method2203(workspace, scope, (ScriptableObject)scope.get("player", scope));
      ScriptNotifyApi.method2207(workspace, scope, (ScriptableObject)((ScriptableObject)scope.get("player", scope)).get("inventory", scope));
      ScriptNotifyApi.method2195(workspace, scope, (ScriptableObject)scope.get("pathfind", scope));
      entry2.method6277("server", ScriptNotifyApi.method2199(scope));
      entry2.method6277("combat", ScriptNotifyApi.method2202(scope));
      entry2.method6277("containers", ScriptNotifyApi.method2211(workspace, storage, scope));
      entry2.method6277("schematic", ScriptNotifyApi.method2213(workspace, storage, scope));
      ScriptImGui.method646(workspace, storage, scope, (ScriptableObject)scope.get("ui", scope));
      ScriptImGui.method653(workspace, scope, (ScriptableObject)scope.get("render3d", scope));
      ScriptImGui.method654(scope, (ScriptableObject)scope.get("render", scope));
      ScriptImGui.method655(workspace, scope, (ScriptableObject)scope.get("player", scope));
      ScriptRequire.method4889(storage, workspace, scope);
      ScriptValueUtils.Entry2 entry22 = ScriptValueUtils.method6247(scope);
      JsonObject json = storage.method1314(workspace);
      ScriptSettingsApi.method1221(entry22, workspace, storage, scope, ScriptSettingsApi.method1226(workspace, json));
      entry2.method6277("settings", entry22.getScriptableObject());
      entry2.method6274("Module", var3x -> method329(storage, workspace, scope, var3x));
      ScriptValueUtils.Entry2 entry23 = ScriptValueUtils.method6247(scope);
      entry23.method6274("log", var2x -> {
         storage.method1327(workspace, "info", method326(var2x));
         return null;
      });
      entry23.method6274("info", var2x -> {
         storage.method1327(workspace, "info", method326(var2x));
         return null;
      });
      entry23.method6274("warn", var2x -> {
         storage.method1327(workspace, "warn", method326(var2x));
         return null;
      });
      entry23.method6274("error", var2x -> {
         storage.method1327(workspace, "error", method326(var2x));
         return null;
      });
      entry2.method6277("console", entry23.getScriptableObject());
      entry2.method6274("print", var2x -> {
         storage.method1327(workspace, "info", method326(var2x));
         return null;
      });
      entry2.method6274("chat", var0x -> {
         if (ScriptEntityApi.method6575().player != null) {
            ScriptEntityApi.method6575().player.sendSystemMessage(Component.literal(ScriptValueUtils.method6254(var0x, 0, "")));
         }

         return null;
      });
      ScriptableObject scriptableObject = (ScriptableObject)ScriptNotifyApi.method2197(workspace, storage, scope);
      ScriptValueUtils.Entry2 entry24 = ScriptValueUtils.method6246(scope, (ScriptableObject)scope.get("chat", scope));

      for (Object allId : scriptableObject.getAllIds()) {
         entry24.method6277(String.valueOf(allId), scriptableObject.get(String.valueOf(allId), scriptableObject));
      }

      entry2.method6274(
         "setTimeout", var2x -> (double)storage.method1329(workspace, ScriptValueUtils.method6260(var2x, 0), (long)ScriptValueUtils.method6256(var2x, 1, 0.0), false)
      );
      entry2.method6274(
         "setInterval", var2x -> (double)storage.method1329(workspace, ScriptValueUtils.method6260(var2x, 0), (long)ScriptValueUtils.method6256(var2x, 1, 50.0), true)
      );
      entry2.method6274("clearTimeout", var2x -> {
         storage.method1330(workspace, ScriptValueUtils.method6257(var2x, 0, -1));
         return null;
      });
      entry2.method6274("clearInterval", var2x -> {
         storage.method1330(workspace, ScriptValueUtils.method6257(var2x, 0, -1));
         return null;
      });
      entry2.method6274("Timer", var1x -> method327(scope));
      entry2.method6274("requireApi", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, 1);
         if (var1x > 3) {
            throw ScriptValueUtils.method6251("скрипту нужна версия API " + var1x + ", у клиента 3");
         } else {
            return 3.0;
         }
      });
      HashSet hashSet = new HashSet();
      entry2.method6274("include", var4x -> {
         method331(storage, workspace, scope, ScriptValueUtils.method6254(var4x, 0, ""), hashSet);
         return null;
      });
      entry2.method6277(
         "script",
         ScriptValueUtils.method6247(scope)
            .method6277("id", workspace.getId())
            .method6277("name", workspace.method1403())
            .method6277("version", workspace.getVersion())
            .method6277("author", workspace.getText2())
            .method6277("api", 3.0)
            .getScriptableObject()
      );
      ScriptRequire.method4891(storage, workspace, scope, (ScriptableObject)scope.get("script", scope));
      ScriptValueUtils.Entry2 entry25 = ScriptValueUtils.method6247(scope);
      entry25.method6274("get", var2x -> {
         JsonElement var3x = workspace.method1415().method1006(ScriptValueUtils.method6254(var2x, 0, ""));
         return var3x == null ? ScriptValueUtils.arg(var2x, 1) : ScriptValueUtils.method6273(scope, var3x);
      });
      entry25.method6274("set", var1x -> {
         workspace.method1415().method1008(ScriptValueUtils.method6254(var1x, 0, ""), ScriptValueUtils.method6271(ScriptValueUtils.arg(var1x, 1), 0));
         return null;
      });
      entry25.method6274("remove", var1x -> {
         workspace.method1415().method1008(ScriptValueUtils.method6254(var1x, 0, ""), null);
         return null;
      });
      entry25.method6274("keys", var2x -> ScriptValueUtils.method6267(scope, new ArrayList<>(workspace.method1415().method1007())));
      entry25.method6274("size", var1x -> (double)workspace.method1415().size());
      entry2.method6277("storage", entry25.getScriptableObject());
      ScriptValueUtils.Entry2 entry26 = ScriptValueUtils.method6247(scope);
      entry26.method6274(
         "register",
         var2x -> {
            String var3x = ScriptValueUtils.method6254(var2x, 0, "").toLowerCase(Locale.ROOT).trim();
            if (!var3x.matches("[a-z0-9а-яё_-]{1,24}")) {
               throw ScriptValueUtils.method6251("имя команды: 1-24 символа, буквы, цифры, _ и -");
            } else if (!List.of("js", "friend", "config", "bind", "binds", "macro", "help").contains(var3x)
               && !Zenith.getInstance().getCommandManager().commandNames().contains(var3x)) {
               for (Script var5x : storage.method1293()) {
                  if (var5x != workspace && var5x.isRunning() && var5x.getItems7().containsKey(var3x)) {
                     throw ScriptValueUtils.method6251("команда ." + var3x + " уже занята скриптом «" + var5x.method1403() + "»");
                  }
               }

               if (workspace.getItems7().size() >= 32 && !workspace.getItems7().containsKey(var3x)) {
                  throw ScriptValueUtils.method6251("у скрипта больше 32 команд");
               } else {
                  workspace.getItems7().put(var3x, ScriptValueUtils.method6260(var2x, 1));
                  return null;
               }
            } else {
               throw ScriptValueUtils.method6251("команда ." + var3x + " занята клиентом");
            }
         }
      );
      entry2.method6277("commands", entry26.getScriptableObject());
      entry2.method6277("music", ScriptMediaApi.method1736(scope));
      ScriptMediaApi.method1738(workspace, storage, scope, (ScriptableObject)scope.get("image", scope));
      ScriptMediaApi.method1740(workspace, storage, scope, (ScriptableObject)scope.get("sound", scope));
      ScriptValueUtils.Entry2 entry27 = ScriptValueUtils.method6247(scope);
      entry27.method6274("code", var0x -> {
         String var1x = ScriptValueUtils.method6254(var0x, 0, "").toLowerCase(Locale.ROOT);

         try {
            return (double)InputConstants.getKey("key.keyboard." + var1x).getValue();
         } catch (RuntimeException var3x) {
            return -1.0;
         }
      });
      entry27.method6274("name", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
         return var1x < 0 ? "" : Type.KEYBOARD.getOrCreate(org.zenith.utility.misc.KeyCompat.toSdl(var1x)).getDisplayName().getString();
      });
      entry2.method6277("keys", entry27.getScriptableObject());
      ScriptTestApi.method5410(storage, workspace, scope);
   }

   private static String method326(Object[] objects) {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < objects.length && builder.length() < 4000; i++) {
         if (i > 0) {
            builder.append(' ');
         }

         Object id = objects[i];
         if (id instanceof Scriptable scriptable && !(id instanceof Function)) {
            try {
               builder.append(ScriptValueUtils.method6271(scriptable, 0));
               continue;
            } catch (RuntimeException e) {
            }
         }

         builder.append(ScriptValueUtils.method6255(id));
      }

      return builder.toString();
   }

   private static Scriptable method327(Scriptable scope) {
      long[] longs = new long[]{System.currentTimeMillis()};
      return ScriptValueUtils.method6247(scope)
         .method6274("passed", var1x -> System.currentTimeMillis() - longs[0] >= ScriptValueUtils.method6256(var1x, 0, 0.0))
         .method6274("reset", var1x -> {
            longs[0] = System.currentTimeMillis();
            return null;
         })
         .method6274("elapsed", var1x -> (double)(System.currentTimeMillis() - longs[0]))
         .getScriptableObject();
   }

   private static ModuleCategory method328(String str) {
      String lowerCase = str.toLowerCase(Locale.ROOT);

      return switch (lowerCase) {
         case "combat" -> ModuleCategory.COMBAT;
         case "movement" -> ModuleCategory.MOVEMENT;
         case "render", "visuals", "visual" -> ModuleCategory.RENDER;
         case "player", "pve" -> ModuleCategory.PLAYER;
         default -> ModuleCategory.MISC;
      };
   }

   private static Object method329(Storage storage, Script workspace, Scriptable scope, Object[] objects) {
      String name = ScriptValueUtils.method6254(objects, 0, "").trim();
      if (!name.isEmpty() && name.length() <= 40) {
         Scriptable scriptable = ScriptValueUtils.method6262(objects, 1);
         Module1 module1 = workspace.getItems32().get(name);
         if (module1 != null) {
            return method330(storage, workspace, scope, module1);
         } else if (workspace.getItems32().size() >= 32) {
            throw ScriptValueUtils.method6251("у скрипта больше 32 модулей");
         } else {
            ModuleCategory category = method328(scriptable == null ? ScriptValueUtils.method6254(objects, 1, "misc") : ScriptValueUtils.method6266(scriptable, "category", "misc"));
            String str = scriptable == null ? "" : ScriptValueUtils.method6266(scriptable, "description", "");
            String anObject = Module1.method7805(workspace, name);

            for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
               if (moduleValue.getId().equals(anObject) || moduleValue.getName().equalsIgnoreCase(name) && !(moduleValue instanceof Module1)) {
                  throw ScriptValueUtils.method6251("модуль «" + name + "» уже есть в клиенте");
               }
            }

            Module1 module12 = Module1.method7802(workspace, name, str, category);
            module12.setFlag2(scriptable != null && ScriptValueUtils.method6265(scriptable, "enabled", false));
            workspace.getItems32().put(name, module12);
            Zenith.getInstance().getModuleManager().method2482(module12);
            return method330(storage, workspace, scope, module12);
         }
      } else {
         throw ScriptValueUtils.method6251("имя модуля: от 1 до 40 символов");
      }
   }

   private static Scriptable method330(Storage storage, Script workspace, Scriptable scope, Module1 module1) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6277("name", module1.getName());
      entry2.method6274("enabled", var1x -> module1.isEnabled());
      entry2.method6274("toggle", var1x -> {
         module1.toggle();
         return module1.isEnabled();
      });
      entry2.method6274("setEnabled", var1x -> {
         module1.setToggled(ScriptValueUtils.method6259(var1x, 0, true));
         return null;
      });
      entry2.method6274("key", var1x -> (double)module1.getKeyCode());
      entry2.method6274("setKey", var1x -> {
         module1.setKeyCode(ScriptValueUtils.method6257(var1x, 0, -1));
         return null;
      });
      Scriptable[] scriptables = new Scriptable[]{null};
      entry2.method6274("onEnable", var2x -> {
         module1.setFunction(ScriptValueUtils.method6260(var2x, 0));
         return scriptables[0];
      });
      entry2.method6274("onDisable", var2x -> {
         module1.setFunction2(ScriptValueUtils.method6260(var2x, 0));
         return scriptables[0];
      });
      JsonObject json = storage.method1313(workspace, module1.getName());
      JsonObject jsonObject = json != null && json.has("Settings") && json.get("Settings").isJsonObject() ? json.getAsJsonObject("Settings") : null;
      var settings = new ArrayList<>(module1.getSettings());
      ScriptSettingsApi.method1221(entry2, workspace, storage, scope, ScriptSettingsApi.method1225(settings, jsonObject, module1::method7804));
      scriptables[0] = entry2.getScriptableObject();
      return scriptables[0];
   }

   private static void method331(Storage storage, Script workspace, Scriptable scope, String str, Set<String> strings) {
      if (workspace.getPath() == null) {
         throw ScriptValueUtils.method6251("include доступен только скриптам-папкам");
      } else {
         if (!str.toLowerCase(Locale.ROOT).endsWith(".js")) {
            str = str + ".js";
         }

         Path path = workspace.getPath2().getParent().toAbsolutePath().normalize();
         Path path2 = path.resolve(str).toAbsolutePath().normalize();
         Path path3 = workspace.getPath().toAbsolutePath().normalize();
         if (path2.startsWith(path3) && Files.isRegularFile(path2)) {
            String string = path2.toString();
            if (strings.add(string)) {
               if (strings.size() > 64) {
                  throw ScriptValueUtils.method6251("больше 64 подключённых файлов");
               } else {
                  String str2;
                  try {
                     str2 = storage.method1306(path2);
                  } catch (Exception e) {
                     throw ScriptValueUtils.method6251("не удалось прочитать " + str);
                  }

                  String str3 = workspace.getId() + "/" + path3.relativize(path2).toString().replace('\\', '/');
                  Context currentContext = Context.getCurrentContext();
                  currentContext.compileString(str2, str3, 1, null).exec(currentContext, scope);
               }
            }
         } else {
            throw ScriptValueUtils.method6251("файл не найден: " + str);
         }
      }
   }
}
