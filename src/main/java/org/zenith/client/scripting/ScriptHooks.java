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
import org.zenith.utility.misc.Class12;
import org.zenith.utility.misc.ModuleHookDefinitions;

public final class ScriptHooks {
   private ScriptHooks() {
   }

   public static List<String> method5049() {
      ArrayList<String> strings = new ArrayList<>();

      for (Entry entry : ModuleHookDefinitions.method6117().entrySet()) {
         strings.add("`" + (String)entry.getKey() + "` — " + ((Class12)entry.getValue()).getText2());
      }

      return strings;
   }

   public static Scriptable method5050(Storage storage, Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("on", var3x -> {
         String str = ScriptValueUtils.method6254(var3x, 0, "");
         Class12 class2 = ModuleHookDefinitions.method6117().get(str);
         if (class2 == null) {
            throw ScriptValueUtils.method6251("неизвестный хук: " + str + " (список — hooks.names())");
         } else {
            var function = ScriptValueUtils.method6260(var3x, 1);
            String lowerCase = ScriptValueUtils.method6254(var3x, 2, "late").toLowerCase(Locale.ROOT);

            byte intValue = switch (lowerCase) {
               case "early", "before" -> -100;
               case "late", "after" -> 100;
               default -> throw ScriptValueUtils.method6251("порядок хука: early или late");
            };
            Runnable runnable = method5051(class2, intValue, storage, workspace, scope, function);
            workspace.method1413(runnable::run);
            return null;
         }
      });
      entry2.method6274("names", var1x -> ScriptValueUtils.method6267(scope, new ArrayList<>(ModuleHookDefinitions.method6117().keySet())));
      return entry2.getScriptableObject();
   }

   private static Runnable method5051(Class12<?> class2, int intValue, Storage storage, Script workspace, Scriptable scope, Function function) {
      Class classValue = class2.getClassValue();
      if (classValue == ModuleHookDefinitions.Entry6.class) {
         return ((Class12<ModuleHookDefinitions.Entry6>)(Class12)class2).method1646(
            intValue,
            var4x -> method5052(
               storage,
               workspace,
               function,
               () -> ScriptValueUtils.method6247(scope).method6277("value", var4x.value).method6277("vanilla", var4x.doubleValue).method6274("set", var1xxx -> {
                  var4x.value = ScriptValueUtils.method6256(var1xxx, 0, var4x.value);
                  return null;
               }).getScriptableObject(),
               null
            )
         );
      } else if (classValue == ModuleHookDefinitions.Entry.class) {
         return ((Class12<ModuleHookDefinitions.Entry>)(Class12)class2).method1646(intValue, var4x -> method5052(storage, workspace, function, () -> ScriptValueUtils.method6247(scope).method6274("cancel", var1xxx -> {
            var4x.cancelled = !ScriptValueUtils.method6252(var1xxx, 0) || ScriptValueUtils.method6259(var1xxx, 0, true);
            return null;
         }).method6274("cancelled", var1xxx -> var4x.cancelled).getScriptableObject(), () -> var4x.cancelled = true));
      } else if (classValue == ModuleHookDefinitions.Entry3.class) {
         return ((Class12<ModuleHookDefinitions.Entry3>)(Class12)class2).method1646(
            intValue,
            var4x -> method5052(storage, workspace, function, () -> ScriptValueUtils.method6247(scope).method6277("kind", var4x.text).method6274("cancel", var1xxx -> {
               var4x.cancelled = !ScriptValueUtils.method6252(var1xxx, 0) || ScriptValueUtils.method6259(var1xxx, 0, true);
               return null;
            }).method6274("cancelled", var1xxx -> var4x.cancelled).getScriptableObject(), () -> var4x.cancelled = true)
         );
      } else if (classValue == ModuleHookDefinitions.Entry5.class) {
         return ((Class12<ModuleHookDefinitions.Entry5>)(Class12)class2).method1646(
            intValue,
            var4x -> method5052(storage, workspace, function, () -> ScriptValueUtils.method6247(scope).method6277("text", var4x.text).method6274("setText", var1xxx -> {
               var4x.text = ScriptValueUtils.method6254(var1xxx, 0, "");
               return null;
            }).method6274("replace", var1xxx -> {
               String var2xx = ScriptValueUtils.method6254(var1xxx, 0, "");
               if (!var2xx.isEmpty()) {
                  var4x.text = var4x.text.replace(var2xx, ScriptValueUtils.method6254(var1xxx, 1, ""));
               }

               return null;
            }).getScriptableObject(), null)
         );
      } else if (classValue == ModuleHookDefinitions.Entry4.class) {
         return ((Class12<ModuleHookDefinitions.Entry4>)(Class12)class2).method1646(
            intValue,
            var4x -> method5052(
               storage,
               workspace,
               function,
               () -> ScriptValueUtils.method6247(scope)
                  .method6277("velocity", ScriptValueUtils.method6270(scope, var4x.vec3d.x, var4x.vec3d.y, var4x.vec3d.z))
                  .method6277("player", ScriptValueUtils.method6270(scope, var4x.vec3d3.x, var4x.vec3d3.y, var4x.vec3d3.z))
                  .method6277("look", ScriptValueUtils.method6270(scope, var4x.vec3d4.x, var4x.vec3d4.y, var4x.vec3d4.z))
                  .method6277("life", (double)var4x.intValue)
                  .method6274(
                     "set",
                     var1xxx -> {
                        var4x.vec3d = new Vec3(
                           ScriptValueUtils.method6256(var1xxx, 0, var4x.vec3d.x),
                           ScriptValueUtils.method6256(var1xxx, 1, var4x.vec3d.y),
                           ScriptValueUtils.method6256(var1xxx, 2, var4x.vec3d.z)
                        );
                        return null;
                     }
                  )
                  .getScriptableObject(),
               null
            )
         );
      } else if (classValue == ModuleHookDefinitions.Entry2.class) {
         return ((Class12<ModuleHookDefinitions.Entry2>)(Class12)class2).method1646(
            intValue,
            var4x -> method5052(
               storage,
               workspace,
               function,
               () -> ScriptValueUtils.method6247(scope)
                  .method6277("id", var4x.text)
                  .method6277("category", var4x.text2)
                  .method6277("x", var4x.doubleValue)
                  .method6277("y", var4x.doubleValue2)
                  .method6277("z", var4x.doubleValue3)
                  .method6277("volume", (double)var4x.floatValue)
                  .method6277("pitch", (double)var4x.floatValue2)
                  .method6274("cancel", var1xxx -> {
                     var4x.cancelled = !ScriptValueUtils.method6252(var1xxx, 0) || ScriptValueUtils.method6259(var1xxx, 0, true);
                     return null;
                  })
                  .method6274("cancelled", var1xxx -> var4x.cancelled)
                  .getScriptableObject(),
               () -> var4x.cancelled = true
            )
         );
      } else {
         throw ScriptValueUtils.method6251("хук " + class2.name() + " не поддерживается");
      }
   }

   private static void method5052(Storage storage, Script workspace, Function function, Supplier<Scriptable> supplier, Runnable runnable) {
      if (workspace.isRunning() && ScriptEntityApi.method6575().isSameThread()) {
         ScriptContextFactory.method7409();

         Scriptable scriptableObject;
         try {
            scriptableObject = (Scriptable)supplier.get();
         } finally {
            Context.exit();
         }

         Object id = storage.method1321(workspace, function, scriptableObject);
         if (runnable != null && id != null && !ScriptValueUtils.method6253(id) && Context.toBoolean(id)) {
            runnable.run();
         }
      }
   }
}
