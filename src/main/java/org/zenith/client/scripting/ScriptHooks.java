package org.zenith.client.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.utility.misc.ScriptHook;
import org.zenith.utility.misc.ModuleHookDefinitions;

public final class ScriptHooks {
   private ScriptHooks() {
   }

   public static List<String> method5049() {
      ArrayList<String> hookDescriptions = new ArrayList<>();

      for (Entry entry : ModuleHookDefinitions.method6117().entrySet()) {
         hookDescriptions.add("`" + (String)entry.getKey() + "` — " + ((ScriptHook)entry.getValue()).getText2());
      }

      return hookDescriptions;
   }

   public static Scriptable method5050(Storage storage, Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 hooksApi = ScriptValueUtils.method6247(scope);
      hooksApi.method6274("on", args -> {
         String hookName = ScriptValueUtils.method6254(args, 0, "");
         ScriptHook hookDefinition = ModuleHookDefinitions.method6117().get(hookName);
         if (hookDefinition == null) {
            throw ScriptValueUtils.method6251("неизвестный хук: " + hookName + " (список — hooks.names())");
         } else {
            var callback = ScriptValueUtils.method6260(args, 1);
            String order = ScriptValueUtils.method6254(args, 2, "late").toLowerCase(Locale.ROOT);

            byte priority = switch (order) {
               case "early", "before" -> -100;
               case "late", "after" -> 100;
               default -> throw ScriptValueUtils.method6251("порядок хука: early или late");
            };
            Runnable unregisterHook = registerHook(hookDefinition, priority, storage, workspace, scope, callback);
            workspace.method1413(unregisterHook::run);
            return null;
         }
      });
      hooksApi.method6274("names", args -> ScriptValueUtils.method6267(scope, new ArrayList<>(ModuleHookDefinitions.method6117().keySet())));
      return hooksApi.getScriptableObject();
   }

   private static Runnable registerHook(ScriptHook<?> hookDefinition, int priority, Storage storage, Script workspace, Scriptable scope, Function callback) {
      Class hookType = hookDefinition.getClassValue();
      if (hookType == ModuleHookDefinitions.Entry6.class) {
         return ((ScriptHook<ModuleHookDefinitions.Entry6>)(ScriptHook)hookDefinition).subscribe(
            priority,
            event -> invokeHook(
               storage,
               workspace,
               callback,
               () -> ScriptValueUtils.method6247(scope).method6277("value", event.value).method6277("vanilla", event.doubleValue).method6274("set", args -> {
                  event.value = ScriptValueUtils.method6256(args, 0, event.value);
                  return null;
               }).getScriptableObject(),
               null
            )
         );
      } else if (hookType == ModuleHookDefinitions.Entry.class) {
         return ((ScriptHook<ModuleHookDefinitions.Entry>)(ScriptHook)hookDefinition).subscribe(priority, event -> invokeHook(storage, workspace, callback, () -> ScriptValueUtils.method6247(scope).method6274("cancel", args -> {
            event.cancelled = !ScriptValueUtils.method6252(args, 0) || ScriptValueUtils.method6259(args, 0, true);
            return null;
         }).method6274("cancelled", args -> event.cancelled).getScriptableObject(), () -> event.cancelled = true));
      } else if (hookType == ModuleHookDefinitions.Entry3.class) {
         return ((ScriptHook<ModuleHookDefinitions.Entry3>)(ScriptHook)hookDefinition).subscribe(
            priority,
            event -> invokeHook(storage, workspace, callback, () -> ScriptValueUtils.method6247(scope).method6277("kind", event.text).method6274("cancel", args -> {
               event.cancelled = !ScriptValueUtils.method6252(args, 0) || ScriptValueUtils.method6259(args, 0, true);
               return null;
            }).method6274("cancelled", args -> event.cancelled).getScriptableObject(), () -> event.cancelled = true)
         );
      } else if (hookType == ModuleHookDefinitions.Entry5.class) {
         return ((ScriptHook<ModuleHookDefinitions.Entry5>)(ScriptHook)hookDefinition).subscribe(
            priority,
            event -> invokeHook(storage, workspace, callback, () -> ScriptValueUtils.method6247(scope).method6277("text", event.text).method6274("setText", args -> {
               event.text = ScriptValueUtils.method6254(args, 0, "");
               return null;
            }).method6274("replace", args -> {
               String searchText = ScriptValueUtils.method6254(args, 0, "");
               if (!searchText.isEmpty()) {
                  event.text = event.text.replace(searchText, ScriptValueUtils.method6254(args, 1, ""));
               }

               return null;
            }).getScriptableObject(), null)
         );
      } else if (hookType == ModuleHookDefinitions.Entry4.class) {
         return ((ScriptHook<ModuleHookDefinitions.Entry4>)(ScriptHook)hookDefinition).subscribe(
            priority,
            event -> invokeHook(
               storage,
               workspace,
               callback,
               () -> ScriptValueUtils.method6247(scope)
                  .method6277("velocity", ScriptValueUtils.method6270(scope, event.vec3d.x, event.vec3d.y, event.vec3d.z))
                  .method6277("player", ScriptValueUtils.method6270(scope, event.vec3d3.x, event.vec3d3.y, event.vec3d3.z))
                  .method6277("look", ScriptValueUtils.method6270(scope, event.vec3d4.x, event.vec3d4.y, event.vec3d4.z))
                  .method6277("life", (double)event.intValue)
                  .method6274(
                     "set",
                     args -> {
                        event.vec3d = new Vec3(
                           ScriptValueUtils.method6256(args, 0, event.vec3d.x),
                           ScriptValueUtils.method6256(args, 1, event.vec3d.y),
                           ScriptValueUtils.method6256(args, 2, event.vec3d.z)
                        );
                        return null;
                     }
                  )
                  .getScriptableObject(),
               null
            )
         );
      } else if (hookType == ModuleHookDefinitions.Entry2.class) {
         return ((ScriptHook<ModuleHookDefinitions.Entry2>)(ScriptHook)hookDefinition).subscribe(
            priority,
            event -> invokeHook(
               storage,
               workspace,
               callback,
               () -> ScriptValueUtils.method6247(scope)
                  .method6277("id", event.text)
                  .method6277("category", event.text2)
                  .method6277("x", event.doubleValue)
                  .method6277("y", event.doubleValue2)
                  .method6277("z", event.doubleValue3)
                  .method6277("volume", (double)event.floatValue)
                  .method6277("pitch", (double)event.floatValue2)
                  .method6274("cancel", args -> {
                     event.cancelled = !ScriptValueUtils.method6252(args, 0) || ScriptValueUtils.method6259(args, 0, true);
                     return null;
                  })
                  .method6274("cancelled", args -> event.cancelled)
                  .getScriptableObject(),
               () -> event.cancelled = true
            )
         );
      } else {
         throw ScriptValueUtils.method6251("хук " + hookDefinition.name() + " не поддерживается");
      }
   }

   private static void invokeHook(Storage storage, Script workspace, Function callback, Supplier<Scriptable> eventDataFactory, Runnable cancelAction) {
      if (workspace.isRunning() && ScriptEntityApi.method6575().isSameThread()) {
         ScriptContextFactory.method7409();

         Scriptable eventData;
         try {
            eventData = (Scriptable)eventDataFactory.get();
         } finally {
            Context.exit();
         }

         Object result = storage.method1321(workspace, callback, eventData);
         if (cancelAction != null && result != null && !ScriptValueUtils.method6253(result) && Context.toBoolean(result)) {
            cancelAction.run();
         }
      }
   }
}
