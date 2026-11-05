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

   public static String method7557(Packet<?> var0) {
      return var0 == null ? "" : var0.type().id().getPath();
   }

   public static Object method7558(Scriptable var0, Packet<?> var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var0);
      Minecraft var3 = ScriptEntityApi.method6575();
      switch (var1) {
         case ClientboundSetEntityMotionPacket var6:
            Vec3 var30 = var6.movement();
            var2.method6277("entityId", (double)var6.id()).method6277("velocity", ScriptValueUtils.method6270(var0, var30.x, var30.y, var30.z));
            method7559(var0, var2, var6.id());
            break;
         case ClientboundSetHealthPacket var7:
            var2.method6277("health", (double)var7.getHealth())
               .method6277("food", (double)var7.getFood())
               .method6277("saturation", (double)var7.getSaturation());
            break;
         case ClientboundEntityEventPacket var8:
            var2.method6277("status", (double)var8.getEventId());
            if (var3.level != null) {
               Entity var31 = var8.getEntity(var3.level);
               if (var31 != null) {
                  var2.method6277("entityId", (double)var31.getId()).method6277("entity", ScriptEntityApi.method6576(var0, var31));
               }
            }
            break;
         case ClientboundSystemChatPacket var9:
            var2.method6277("text", var9.content().getString()).method6277("overlay", var9.overlay());
            break;
         case ClientboundSetTitleTextPacket var10:
            var2.method6277("text", var10.text().getString());
            break;
         case ClientboundSetSubtitleTextPacket var11:
            var2.method6277("text", var11.text().getString());
            break;
         case ClientboundPlayerPositionPacket var12:
            Vec3 var32 = var12.change().position();
            var2.method6277("teleportId", (double)var12.id())
               .method6277("x", var32.x)
               .method6277("y", var32.y)
               .method6277("z", var32.z)
               .method6277("yaw", (double)var12.change().yRot())
               .method6277("pitch", (double)var12.change().xRot());
            break;
         case ClientboundSetHeldSlotPacket var13:
            var2.method6277("slot", (double)var13.slot());
            break;
         case ClientboundContainerSetSlotPacket var14:
            var2.method6277("syncId", (double)var14.getContainerId())
               .method6277("slot", (double)var14.getSlot())
               .method6277("item", ScriptEntityApi.method6579(var0, var14.getItem()));
            break;
         case ClientboundOpenScreenPacket var15:
            var2.method6277("syncId", (double)var15.getContainerId()).method6277("title", var15.getTitle().getString());
            break;
         case ClientboundSoundPacket var16:
            var2.method6277("sound", var16.getSound().getRegisteredName())
               .method6277("x", var16.getX())
               .method6277("y", var16.getY())
               .method6277("z", var16.getZ())
               .method6277("volume", (double)var16.getVolume())
               .method6277("pitch", (double)var16.getPitch());
            break;
         case ClientboundAnimatePacket var17:
            // scripts keep the 1.21.11 ids (2 wake up, 4 crit, 5 magic crit); 26.3 renumbered them to 0..2
            var2.method6277("entityId", (double)var17.getId()).method6277("animation", (double)(var17.getAction() == 0 ? 2 : var17.getAction() + 3));
            method7559(var0, var2, var17.getId());
            break;
         case net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket var40:
            // 26.3 moved swings out of ClientboundAnimatePacket; report them with the old ids (0 main hand, 3 off hand)
            var2.method6277("entityId", (double)var40.entityId())
               .method6277("animation", (double)(var40.hand() == net.minecraft.world.InteractionHand.MAIN_HAND ? 0 : 3));
            method7559(var0, var2, var40.entityId());
            break;
         case ClientboundBlockUpdatePacket var18:
            var2.method6277("x", (double)var18.getPos().getX())
               .method6277("y", (double)var18.getPos().getY())
               .method6277("z", (double)var18.getPos().getZ())
               .method6277("block", BuiltInRegistries.BLOCK.getKey(var18.getBlockState().getBlock()).toString());
            break;
         case ClientboundExplodePacket var19:
            Vec3 var33 = var19.center();
            var2.method6277("x", var33.x).method6277("y", var33.y).method6277("z", var33.z).method6277("radius", (double)var19.radius());
            var19.playerKnockback().ifPresent(var2x -> var2.method6277("knockback", ScriptValueUtils.method6270(var0, var2x.x, var2x.y, var2x.z)));
            break;
         case ServerboundMovePlayerPacket var20:
            LocalPlayer var34 = var3.player;
            double var35 = var34 == null ? 0.0 : var34.getX();
            double var36 = var34 == null ? 0.0 : var34.getY();
            double var37 = var34 == null ? 0.0 : var34.getZ();
            float var28 = var34 == null ? 0.0F : var34.getYRot();
            float var29 = var34 == null ? 0.0F : var34.getXRot();
            var2.method6277("x", var20.getX(var35))
               .method6277("y", var20.getY(var36))
               .method6277("z", var20.getZ(var37))
               .method6277("yaw", (double)var20.getYRot(var28))
               .method6277("pitch", (double)var20.getXRot(var29))
               .method6277("onGround", var20.isOnGround())
               .method6277("changesPosition", var20.hasPosition())
               .method6277("changesLook", var20.hasRotation());
            break;
         case ServerboundContainerClickPacket var21:
            var2.method6277("syncId", (double)var21.containerId())
               .method6277("slot", (double)var21.slotNum())
               .method6277("button", (double)var21.buttonNum())
               .method6277("action", var21.containerInput().name().toLowerCase(Locale.ROOT));
            break;
         case ServerboundSetCarriedItemPacket var22:
            var2.method6277("slot", (double)var22.getSlot());
            break;
         case ServerboundChatPacket var23:
            var2.method6277("text", var23.message());
            break;
         case ServerboundChatCommandPacket var24:
            var2.method6277("command", var24.command());
            break;
         case ServerboundPunchPacket var25:
            var2.method6277("hand", "main_hand"); // 26.3 punch packet has no hand
            break;
         case ServerboundPlayerActionPacket var26:
            var2.method6277("action", var26.getAction().name().toLowerCase(Locale.ROOT))
               .method6277("x", (double)var26.getPos().getX())
               .method6277("y", (double)var26.getPos().getY())
               .method6277("z", (double)var26.getPos().getZ())
               .method6277("side", var26.getDirection().getSerializedName());
            break;
         default:
            return null;
      }

      return var2.getScriptableObject();
   }

   private static void method7559(Scriptable var0, ScriptValueUtils.Entry2 var1, int var2) {
      if (ScriptEntityApi.method6575().level != null) {
         Entity var3 = ScriptEntityApi.method6575().level.getEntity(var2);
         if (var3 != null) {
            var1.method6277("entity", ScriptEntityApi.method6576(var0, var3));
         }
      }
   }
}
