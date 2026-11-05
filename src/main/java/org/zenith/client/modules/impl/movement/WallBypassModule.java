package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "WallBypass",
   description = "module.wallbypass.description",
   category = ModuleCategory.MOVEMENT
)
public final class WallBypassModule extends Module {
   public static final WallBypassModule INSTANCE = new WallBypassModule();
   private static final double CONST_doubleValue = 3.0;
   private static final int CONST_intValue = 70;
   private static final int[] CONST_intValues = new int[]{15, 5, 1};
   private final SliderSetting attackDistance = new SliderSetting(
      "module.wallBypass.attackDistance", 3.0F, 3.0F, 6.0F, 0.1F, "module.wallBypass.attackDistance.desc", "b"
   );
   private Rotation rotation;
   private boolean flag;
   private boolean flag2;
   private int intValue;

   private WallBypassModule() {
   }

   @Override
   public void onEnable() {
      this.rotation = this.method7503();
      this.flag = this.rotation != null;
      this.flag2 = false;
      this.intValue = 1;
      if (!this.flag) {
         this.setToggled(false);
      } else {
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.rotation = null;
      this.flag = false;
      this.flag2 = false;
      this.intValue = 0;
      super.onDisable();
   }

   @EventTarget
   public void method7499(PlayerMoveEvent event) {
      if (this.intValue <= 0) {
         event.setVec3d(Vec3.ZERO);
      }
   }

   @EventTarget
   public void method7500(Event5 event) {
      if (!this.flag || this.rotation == null || mc.player == null || mc.level == null) {
         this.setToggled(false);
      } else if (!this.flag2) {
         Rotation rotation = this.rotation;
         rotationManager.method7848(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 70, this, 1);
         this.flag2 = true;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.getPacket3() instanceof ServerboundMovePlayerPacket) {
         if (this.intValue > 0) {
            if (this.flag2) {
               this.intValue--;
            }

            return;
         }

         event.setCancelled(true);
      }
   }

   public boolean method7501() {
      return this.isEnabled() && this.flag && this.rotation != null && this.intValue <= 0;
   }

   public float method7502() {
      return this.attackDistance.getValue3();
   }

   private Rotation method7503() {
      if (mc.player != null && mc.level != null) {
         Rotation rotation = rotationManager.method7844();
         float floatValue = rotation.getFloatValue();
         Rotation[] rotations = new Rotation[]{
            new Rotation(floatValue, -90.0F),
            new Rotation(floatValue, 90.0F),
            new Rotation(floatValue, 0.0F),
            new Rotation(mc.player.getYRot(), -90.0F),
            new Rotation(mc.player.getYRot(), 90.0F),
            new Rotation(mc.player.getYRot(), 0.0F)
         };

         for (Rotation rotation2 : rotations) {
            if (this.method7505(rotation2)) {
               return rotation2;
            }
         }

         for (int n : CONST_intValues) {
            Rotation rotation2 = this.method7504(floatValue, n);
            if (rotation2 != null) {
               return rotation2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Rotation method7504(float floatValue, int n) {
      int xRot = -90;

      while (xRot <= 90) {
         for (int i = 0; i < 360; i += n) {
            Rotation rotation = new Rotation(floatValue + i, xRot);
            if (this.method7505(rotation)) {
               return rotation;
            }

            if (i != 0) {
               Rotation rotation2 = new Rotation(floatValue - i, xRot);
               if (this.method7505(rotation2)) {
                  return rotation2;
               }
            }
         }

         xRot += n;
      }

      return null;
   }

   private boolean method7505(Rotation rotation) {
      if (mc.player != null && mc.level != null) {
         BlockHitResult blockHitResult = TargetUtils.method3216(mc.player.getEyePosition(1.0F), rotation, 3.0, blockHitResult2 -> true);
         return blockHitResult != null && blockHitResult.getType() == Type.MISS && UseItemUtils.method6523(BlockPos.containing(blockHitResult.getLocation()));
      } else {
         return false;
      }
   }
}
