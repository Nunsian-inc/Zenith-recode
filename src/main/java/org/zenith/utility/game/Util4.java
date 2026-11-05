package org.zenith.utility.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.ScriptImGui;

public final class Util4 {
   private static final Map<Integer, Integer> items = new ConcurrentHashMap<>();
   private static final Map<Integer, Script> items2 = new ConcurrentHashMap<>();
   private static final Map<Integer, Script> items3 = new ConcurrentHashMap<>();
   private static final Map<Script, Float> items4 = new ConcurrentHashMap<>();
   private static volatile Identifier identifier;
   private static volatile boolean flag;
   private static volatile Identifier identifier2;
   private static volatile Script script;
   private static volatile Script script2;

   private Util4() {
   }

   public static boolean method1278(int id) {
      return !items.isEmpty() && items.containsKey(id);
   }

   public static int method1279(int id, int teamColor) {
      Integer n = items.get(id);
      return n == null ? teamColor : n & 16777215;
   }

   public static boolean method1280(int id) {
      return !items3.isEmpty() && items3.containsKey(id);
   }

   public static float method1281() {
      if (items4.isEmpty()) {
         return 1.0F;
      } else {
         float f = 1.0F;

         for (float f2 : items4.values()) {
            f = Math.max(f, f2);
         }

         return f;
      }
   }

   public static void method1282(Script workspace, int id, Integer n) {
      if (n == null) {
         items.remove(id);
         items2.remove(id);
      } else {
         items.put(id, n);
         items2.put(id, workspace);
      }
   }

   public static void method1283(Script workspace, int id, boolean flag) {
      if (flag) {
         items3.put(id, workspace);
      } else {
         items3.remove(id);
      }
   }

   public static void method1284(Script workspace, float f) {
      if (f <= 1.001F) {
         items4.remove(workspace);
      } else {
         items4.put(workspace, Math.min(50.0F, f));
      }
   }

   public static boolean method1285() {
      return identifier != null || identifier2 != null;
   }

   public static PlayerSkin method1286(PlayerSkin returnValue) {
      Identifier id = identifier;
      Identifier identifier22 = identifier2;
      if (id != null && (script == null || !script.isRunning())) {
         identifier = null;
         id = null;
      }

      if (identifier22 != null && (script2 == null || !script2.isRunning())) {
         identifier2 = null;
         identifier22 = null;
      }

      if (id == null && identifier22 == null) {
         return returnValue;
      } else {
         Object body = id == null ? returnValue.body() : new ResourceTexture(id, id);
         Object cape = identifier22 == null ? returnValue.cape() : new ResourceTexture(identifier22, identifier22);
         Object elytra = identifier22 == null ? returnValue.elytra() : cape;
         PlayerModelType model = id == null ? returnValue.model() : (flag ? PlayerModelType.SLIM : PlayerModelType.WIDE);
         return new PlayerSkin((Texture)body, (Texture)cape, (Texture)elytra, model, returnValue.secure());
      }
   }

   public static boolean method1287() {
      return ScriptImGui.method648();
   }

   public static void method1288(Script workspace, Identifier texture, boolean flag2) {
      script = texture == null ? null : workspace;
      flag = flag2;
      identifier = texture;
   }

   public static void method1289(Script workspace, Identifier texture) {
      script2 = texture == null ? null : workspace;
      identifier2 = texture;
   }

   public static void method1290(Script workspace) {
      if (script == workspace) {
         identifier = null;
         script = null;
      }

      if (script2 == workspace) {
         identifier2 = null;
         script2 = null;
      }
   }

   public static void method1291(Script workspace) {
      items2.entrySet().removeIf(entry -> {
         if (entry.getValue() == workspace) {
            items.remove(entry.getKey());
            return true;
         } else {
            return false;
         }
      });
      items3.values().removeIf(script -> script == workspace);
      items4.remove(workspace);
   }
}
