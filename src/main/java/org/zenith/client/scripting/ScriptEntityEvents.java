package org.zenith.client.scripting;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Scriptable;

public final class ScriptEntityEvents {
   private ScriptEntityEvents() {
   }

   public static String method7557(Packet<?> packet) {
      return packet == null ? "" : packet.type().id().getPath();
   }

   public static Object method7558(Scriptable scope, Packet<?> packet) {
      ScriptValueUtils.Entry2 eventData = ScriptValueUtils.method6247(scope);
      Minecraft minecraft = ScriptEntityApi.method6575();
      switch (packet) {
         case ClientboundSetEntityMotionPacket setEntityMotionPacket:
            Vec3 movement = setEntityMotionPacket.movement();
            eventData.method6277("entityId", (double)setEntityMotionPacket.id()).method6277("velocity", ScriptValueUtils.method6270(scope, movement.x, movement.y, movement.z));
            addEntityData(scope, eventData, setEntityMotionPacket.id());
            break;
         case ClientboundSetHealthPacket setHealthPacket:
            eventData.method6277("health", (double)setHealthPacket.getHealth())
               .method6277("food", (double)setHealthPacket.getFood())
               .method6277("saturation", (double)setHealthPacket.getSaturation());
            break;
         case ClientboundEntityEventPacket entityEventPacket:
            eventData.method6277("status", (double)entityEventPacket.getEventId());
            if (minecraft.level != null) {
               Entity entity = entityEventPacket.getEntity(minecraft.level);
               if (entity != null) {
                  eventData.method6277("entityId", (double)entity.getId()).method6277("entity", ScriptEntityApi.method6576(scope, entity));
               }
            }
            break;
         case ClientboundSystemChatPacket systemChatPacket:
            eventData.method6277("text", systemChatPacket.content().getString()).method6277("overlay", systemChatPacket.overlay());
            break;
         case ClientboundSetTitleTextPacket setTitleTextPacket:
            eventData.method6277("text", setTitleTextPacket.text().getString());
            break;
         case ClientboundSetSubtitleTextPacket setSubtitleTextPacket:
            eventData.method6277("text", setSubtitleTextPacket.text().getString());
            break;
         case ClientboundPlayerPositionPacket playerPositionPacket:
            Vec3 position = playerPositionPacket.change().position();
            eventData.method6277("teleportId", (double)playerPositionPacket.id())
               .method6277("x", position.x)
               .method6277("y", position.y)
               .method6277("z", position.z)
               .method6277("yaw", (double)playerPositionPacket.change().yRot())
               .method6277("pitch", (double)playerPositionPacket.change().xRot());
            break;
         case ClientboundSetHeldSlotPacket setHeldSlotPacket:
            eventData.method6277("slot", (double)setHeldSlotPacket.slot());
            break;
         case ClientboundContainerSetSlotPacket containerSetSlotPacket:
            eventData.method6277("syncId", (double)containerSetSlotPacket.getContainerId())
               .method6277("slot", (double)containerSetSlotPacket.getSlot())
               .method6277("item", ScriptEntityApi.method6579(scope, containerSetSlotPacket.getItem()));
            break;
         case ClientboundOpenScreenPacket openScreenPacket:
            eventData.method6277("syncId", (double)openScreenPacket.getContainerId()).method6277("title", openScreenPacket.getTitle().getString());
            break;
         case ClientboundSoundPacket clientboundSoundPacket:
            eventData.method6277("sound", clientboundSoundPacket.getSound().getRegisteredName())
               .method6277("x", clientboundSoundPacket.getX())
               .method6277("y", clientboundSoundPacket.getY())
               .method6277("z", clientboundSoundPacket.getZ())
               .method6277("volume", (double)clientboundSoundPacket.getVolume())
               .method6277("pitch", (double)clientboundSoundPacket.getPitch());
            break;
         case ClientboundAnimatePacket animatePacket:
            // scripts keep the 1.21.11 ids (2 wake up, 4 crit, 5 magic crit); 26.3 renumbered them to 0..2
            eventData.method6277("entityId", (double)animatePacket.getId()).method6277("animation", (double)(animatePacket.getAction() == 0 ? 2 : animatePacket.getAction() + 3));
            addEntityData(scope, eventData, animatePacket.getId());
            break;
         case net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket swingAnimationPacket:
            // 26.3 moved swings out of ClientboundAnimatePacket; report them with the old ids (0 main hand, 3 off hand)
            eventData.method6277("entityId", (double)swingAnimationPacket.entityId())
               .method6277("animation", (double)(swingAnimationPacket.hand() == net.minecraft.world.InteractionHand.MAIN_HAND ? 0 : 3));
            addEntityData(scope, eventData, swingAnimationPacket.entityId());
            break;
         case ClientboundBlockUpdatePacket blockUpdatePacket:
            eventData.method6277("x", (double)blockUpdatePacket.getPos().getX())
               .method6277("y", (double)blockUpdatePacket.getPos().getY())
               .method6277("z", (double)blockUpdatePacket.getPos().getZ())
               .method6277("block", BuiltInRegistries.BLOCK.getKey(blockUpdatePacket.getBlockState().getBlock()).toString());
            break;
         case ClientboundExplodePacket explodePacket:
            Vec3 center = explodePacket.center();
            eventData.method6277("x", center.x).method6277("y", center.y).method6277("z", center.z).method6277("radius", (double)explodePacket.radius());
            explodePacket.playerKnockback().ifPresent(knockback -> eventData.method6277("knockback", ScriptValueUtils.method6270(scope, knockback.x, knockback.y, knockback.z)));
            break;
         case ServerboundMovePlayerPacket movePlayerPacket:
            LocalPlayer player = minecraft.player;
            double x = player == null ? 0.0 : player.getX();
            double y = player == null ? 0.0 : player.getY();
            double z = player == null ? 0.0 : player.getZ();
            float yRot = player == null ? 0.0F : player.getYRot();
            float xRot = player == null ? 0.0F : player.getXRot();
            eventData.method6277("x", movePlayerPacket.getX(x))
               .method6277("y", movePlayerPacket.getY(y))
               .method6277("z", movePlayerPacket.getZ(z))
               .method6277("yaw", (double)movePlayerPacket.getYRot(yRot))
               .method6277("pitch", (double)movePlayerPacket.getXRot(xRot))
               .method6277("onGround", movePlayerPacket.isOnGround())
               .method6277("changesPosition", movePlayerPacket.hasPosition())
               .method6277("changesLook", movePlayerPacket.hasRotation());
            break;
         case ServerboundContainerClickPacket containerClickPacket:
            eventData.method6277("syncId", (double)containerClickPacket.containerId())
               .method6277("slot", (double)containerClickPacket.slotNum())
               .method6277("button", (double)containerClickPacket.buttonNum())
               .method6277("action", containerClickPacket.containerInput().name().toLowerCase(Locale.ROOT));
            break;
         case ServerboundSetCarriedItemPacket setCarriedItemPacket:
            eventData.method6277("slot", (double)setCarriedItemPacket.getSlot());
            break;
         case ServerboundChatPacket serverboundChatPacket:
            eventData.method6277("text", serverboundChatPacket.message());
            break;
         case ServerboundChatCommandPacket chatCommandPacket:
            eventData.method6277("command", chatCommandPacket.command());
            break;
         case ServerboundPunchPacket serverboundPunchPacket:
            eventData.method6277("hand", "main_hand"); // 26.3 punch packet has no hand
            break;
         case ServerboundPlayerActionPacket playerActionPacket:
            eventData.method6277("action", playerActionPacket.getAction().name().toLowerCase(Locale.ROOT))
               .method6277("x", (double)playerActionPacket.getPos().getX())
               .method6277("y", (double)playerActionPacket.getPos().getY())
               .method6277("z", (double)playerActionPacket.getPos().getZ())
               .method6277("side", playerActionPacket.getDirection().getSerializedName());
            break;
         default:
            return null;
      }

      return eventData.getScriptableObject();
   }

   private static void addEntityData(Scriptable scope, ScriptValueUtils.Entry2 eventData, int id) {
      if (ScriptEntityApi.method6575().level != null) {
         Entity entity = ScriptEntityApi.method6575().level.getEntity(id);
         if (entity != null) {
            eventData.method6277("entity", ScriptEntityApi.method6576(scope, entity));
         }
      }
   }
}
