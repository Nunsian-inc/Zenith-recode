package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ElytraMath;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.Util13;

@ModuleInfo(
   name = "Grim Glide",
   category = ModuleCategory.MOVEMENT,
   description = "module.grimglide.description"
)
public final class GrimGlideModule extends Module {
   public static final GrimGlideModule INSTANCE = new GrimGlideModule();
   private final SliderSetting timerBoost = new SliderSetting("module.grimGlide.timerBoost", 1.5F, 1.0F, 5.0F, 0.1F, "module.grimGlide.timerBoost.desc", "x");
   private final SliderSetting timerReset = new SliderSetting("module.grimGlide.timerReset", 0.5F, 0.0F, 0.9F, 0.01F, "module.grimGlide.timerReset.desc", "x");
   private int intValue;
   private boolean flag;
   private int intValue2;
   private boolean flag2;

   private GrimGlideModule() {
   }

   @Override
   public void onEnable() {
      this.intValue = 0;
      this.flag = false;
      this.intValue2 = 0;
      this.flag2 = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.flag2) {
         Util13.method2404(1.0F);
         this.flag2 = false;
      }

      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.intValue > 0) {
         this.intValue--;
      }

      boolean flag = mc.player != null && mc.level != null && mc.player.isFallFlying();
      if (!flag) {
         if (this.flag2) {
            Util13.method2404(1.0F);
            this.flag2 = false;
         }
      } else {
         Util13.method2404(this.intValue2 % 2 == 0 ? this.timerBoost.getValue3() : this.timerReset.getValue3());
         this.flag2 = true;
         this.intValue2++;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null) {
         if (event.method7100() && event.getPacket3() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> {
               this.intValue = 2;
               this.flag = true;
               if (this.intValue2 % 2 == 1) {
                  this.intValue2++;
               }
            });
         } else {
            if (event.method7099() && event.getPacket3() instanceof ServerboundMovePlayerPacket movePlayerPacket && !PacketUtils.getItems().contains(movePlayerPacket)) {
               if (mc.player.isFallFlying() && this.intValue == 0 && !this.flag) {
                  event.setPacket2(new StatusOnly(true, true));
               }

               this.flag = false;
            }
         }
      }
   }

   public Vec3 method5976(Vec3 vec, Vec3 returnValue, Vec3 lookAngle) {
      if (this.isEnabled() && mc.player != null) {
         Vec3 vec2 = ElytraMath.method5974(mc.player, vec, lookAngle, 3.2);
         if (this.intValue >= 1) {
            double d = Math.toRadians(mc.player.getYRot());
            return vec2.multiply(0.99F, 0.98F, 0.99F).add(-Math.sin(d) * 0.09F, 0.03F, Math.cos(d) * 0.09F);
         } else {
            return vec2.multiply(0.3F, 0.3F, 0.3F);
         }
      } else {
         return returnValue;
      }
   }
}
