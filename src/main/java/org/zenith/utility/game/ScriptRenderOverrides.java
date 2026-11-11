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

public final class ScriptRenderOverrides {
   private static final Map<Integer, Integer> teamColors = new ConcurrentHashMap<>();
   private static final Map<Integer, Script> teamColorOwners = new ConcurrentHashMap<>();
   private static final Map<Integer, Script> hiddenEntityOwners = new ConcurrentHashMap<>();
   private static final Map<Script, Float> cameraZoomByScript = new ConcurrentHashMap<>();
   private static volatile Identifier skinTexture;
   private static volatile boolean slimSkin;
   private static volatile Identifier capeTexture;
   private static volatile Script skinOwner;
   private static volatile Script capeOwner;

   private ScriptRenderOverrides() {
   }

   public static boolean hasTeamColorOverride(int id) {
      return !teamColors.isEmpty() && teamColors.containsKey(id);
   }

   public static int getTeamColor(int id, int teamColor) {
      Integer n = teamColors.get(id);
      return n == null ? teamColor : n & 16777215;
   }

   public static boolean isEntityHidden(int id) {
      return !hiddenEntityOwners.isEmpty() && hiddenEntityOwners.containsKey(id);
   }

   public static float getMaxCameraZoom() {
      if (cameraZoomByScript.isEmpty()) {
         return 1.0F;
      } else {
         float f = 1.0F;

         for (float f2 : cameraZoomByScript.values()) {
            f = Math.max(f, f2);
         }

         return f;
      }
   }

   public static void setTeamColorOverride(Script workspace, int id, Integer n) {
      if (n == null) {
         teamColors.remove(id);
         teamColorOwners.remove(id);
      } else {
         teamColors.put(id, n);
         teamColorOwners.put(id, workspace);
      }
   }

   public static void setEntityHidden(Script workspace, int id, boolean flag) {
      if (flag) {
         hiddenEntityOwners.put(id, workspace);
      } else {
         hiddenEntityOwners.remove(id);
      }
   }

   public static void setCameraZoom(Script workspace, float f) {
      if (f <= 1.001F) {
         cameraZoomByScript.remove(workspace);
      } else {
         cameraZoomByScript.put(workspace, Math.min(50.0F, f));
      }
   }

   public static boolean hasSkinOverride() {
      return skinTexture != null || capeTexture != null;
   }

   public static PlayerSkin applySkinOverride(PlayerSkin returnValue) {
      Identifier id = skinTexture;
      Identifier activeCapeTexture = capeTexture;
      if (id != null && (skinOwner == null || !skinOwner.isRunning())) {
         skinTexture = null;
         id = null;
      }

      if (activeCapeTexture != null && (capeOwner == null || !capeOwner.isRunning())) {
         capeTexture = null;
         activeCapeTexture = null;
      }

      if (id == null && activeCapeTexture == null) {
         return returnValue;
      } else {
         Object body = id == null ? returnValue.body() : new ResourceTexture(id, id);
         Object cape = activeCapeTexture == null ? returnValue.cape() : new ResourceTexture(activeCapeTexture, activeCapeTexture);
         Object elytra = activeCapeTexture == null ? returnValue.elytra() : cape;
         PlayerModelType model = id == null ? returnValue.model() : (slimSkin ? PlayerModelType.SLIM : PlayerModelType.WIDE);
         return new PlayerSkin((Texture)body, (Texture)cape, (Texture)elytra, model, returnValue.secure());
      }
   }

   public static boolean isImGuiOpen() {
      return ScriptImGui.method648();
   }

   public static void setPlayerSkinTexture(Script workspace, Identifier texture, boolean flag2) {
      skinOwner = texture == null ? null : workspace;
      slimSkin = flag2;
      skinTexture = texture;
   }

   public static void setPlayerCapeTexture(Script workspace, Identifier texture) {
      capeOwner = texture == null ? null : workspace;
      capeTexture = texture;
   }

   public static void clearPlayerSkinOverrides(Script workspace) {
      if (skinOwner == workspace) {
         skinTexture = null;
         skinOwner = null;
      }

      if (capeOwner == workspace) {
         capeTexture = null;
         capeOwner = null;
      }
   }

   public static void clearScriptOverrides(Script workspace) {
      teamColorOwners.entrySet().removeIf(entry -> {
         if (entry.getValue() == workspace) {
            teamColors.remove(entry.getKey());
            return true;
         } else {
            return false;
         }
      });
      hiddenEntityOwners.values().removeIf(script -> script == workspace);
      cameraZoomByScript.remove(workspace);
   }
}
