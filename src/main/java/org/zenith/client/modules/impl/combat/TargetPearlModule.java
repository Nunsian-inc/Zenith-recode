package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.InteractItemEvent;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "TargetPearl",
   description = "module.targetpearl.description",
   category = ModuleCategory.COMBAT
)
public final class TargetPearlModule extends Module {
   public static final TargetPearlModule INSTANCE = new TargetPearlModule();
   private static final double CONST_doubleValue = 1.5;
   private static final double CONST_doubleValue2 = 0.03;
   private static final double CONST_doubleValue3 = 2.0;
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 8;
   private final StopWatch stopWatch = new StopWatch();
   private final ModeSetting modeSetting = new ModeSetting(
      "module.targetPearl.modeSetting", "module.targetPearl.modeSetting.desc", "module.targetPearl.modeBind", "module.targetPearl.modeAlways"
   );
   private final BindSetting throwSetting = new BindSetting(
      "module.targetPearl.throwSetting", "module.targetPearl.throwSetting.desc", () -> this.modeSetting.isSelected(0)
   );
   private final ModeSetting targetSetting = new ModeSetting(
      "module.targetPearl.targetSetting", "module.targetPearl.targetSetting.desc", "module.targetPearl.targetTarget", "module.targetPearl.targetAll"
   );
   private final SliderSetting distanceSetting = new SliderSetting(
      "module.targetPearl.distanceSetting", 6.0F, 3.0F, 15.0F, 1.0F, "module.targetPearl.distanceSetting.desc", "b"
   );
   private final ModeSetting versionSetting = new ModeSetting("module.targetPearl.versionSetting", "module.targetPearl.versionSetting.desc", "1.17.1", "1.21.4");
   private Rotation rotation;
   private Vec3 vec3d;
   private int intValue;
   private boolean flag;
   private int intValue2;

   private TargetPearlModule() {
   }

   @EventTarget
   public void method4829(EntityAddEvent event) {
      if (mc.level != null && mc.player != null) {
         if (event.getEntity() instanceof ThrownEnderpearl entity && entity.getOwner() == null) {
            mc.level
               .players()
               .stream()
               .filter(var1x -> var1x != mc.player && var1x.distanceTo(entity) <= 3.0F)
               .min(Comparator.comparingDouble(var1x -> var1x.distanceTo(entity)))
               .ifPresent(entity::setOwner);
         }
      }
   }

   @EventTarget
   public void method4830(PreRotationEvent event) {
      if (mc.player == null || mc.level == null) {
         this.method4843();
      } else if (this.flag) {
         if (++this.intValue2 > 10) {
            this.method4843();
         } else {
            this.method4841();
         }
      } else if (!this.method4832()) {
         this.method4843();
      } else if (this.rotation != null && ++this.intValue > 8) {
         this.method4843();
         this.stopWatch.reset();
      } else {
         Rotation rotation2 = Zenith.getInstance().getRotationManager().getRotation2();
         if (this.rotation != null && this.vec3d != null && this.versionSetting.isSelected(0) && this.method4836(rotation2, this.vec3d)) {
            this.rotation = rotation2;
            this.method4841();
            this.method4842();
         } else {
            this.method4833();
            if (this.rotation != null) {
               this.method4841();
               if (this.versionSetting.isSelected(1)) {
                  this.method4842();
               }
            }
         }
      }
   }

   @EventTarget(0)
   public void method4831(InteractItemEvent event) {
      if (this.flag && mc.player != null) {
         if (mc.player.getMainHandItem().is(Items.ENDER_PEARL) || mc.player.getOffhandItem().is(Items.ENDER_PEARL)) {
            this.method4843();
         }
      }
   }

   private boolean method4832() {
      if (!this.stopWatch.hasElapsed(1000L)) {
         return false;
      } else if (this.modeSetting.isSelected(0) && !UseItemUtils.method6521(this.throwSetting)) {
         return false;
      } else if (mc.gui.screen() != null || mc.player.isUsingItem()) {
         return false;
      } else if (InventoryUtils.method906(Items.ENDER_PEARL) == null) {
         return false;
      } else {
         return mc.player.getCooldowns().getCooldownPercent(Items.ENDER_PEARL.getDefaultInstance(), 0.0F) > 0.0F
            ? false
            : UseItemUtils.method6510()
               .filter(ThrownEnderpearl.class::isInstance)
               .map(ThrownEnderpearl.class::cast)
               .noneMatch(thrownEnderpearl -> Objects.equals(thrownEnderpearl.getOwner(), mc.player));
      }
   }

   private void method4833() {
      PredictionsModule predictionsModule = PredictionsModule.INSTANCE;
      Vec3 position = mc.player.position();
      Rotation rotation = Zenith.getInstance().getRotationManager().getEffectiveRotation();
      this.rotation = null;
      this.vec3d = null;
      UseItemUtils.method6510()
         .filter(ThrownEnderpearl.class::isInstance)
         .map(ThrownEnderpearl.class::cast)
         .filter(this::method4834)
         .map(predictionsModule::method2919)
         .filter(Objects::nonNull)
         .map(HitResult::getLocation)
         .filter(var2x -> var2x.distanceTo(position) >= this.distanceSetting.getValue3())
         .map(var1x -> new TargetPearlModule.Data(var1x, this.method4835(var1x)))
         .filter(data -> data.rotation() != null)
         .min(Comparator.comparingDouble(var1x -> rotation.wrappedDeltaTo(var1x.rotation()).length()))
         .ifPresent(var1x -> {
            this.rotation = var1x.rotation();
            this.vec3d = var1x.getVec3d();
         });
   }

   private boolean method4834(ThrownEnderpearl thrownEnderpearl) {
      Entity owner = thrownEnderpearl.getOwner();
      if (owner == null || owner == mc.player || Zenith.getInstance().getFriends().method7626(owner)) {
         return false;
      } else if (!this.targetSetting.isSelected(1) && AuraModule.INSTANCE.isEnabled()) {
         LivingEntity livingEntity22 = AuraModule.INSTANCE.getLivingEntity22();
         return livingEntity22 == null || livingEntity22.equals(owner);
      } else {
         return true;
      }
   }

   private Rotation method4835(Vec3 vec3d) {
      Vec3 vec = this.method4839();
      Vec3 vec2 = this.method4840();
      MutableBlockPos mutablePos = new MutableBlockPos();
      float yRot = (float)(Mth.atan2(vec3d.z - vec.z, vec3d.x - vec.x) * (180.0 / Math.PI)) - 90.0F;
      float f = Float.NaN;
      double d = Double.MAX_VALUE;
      double d2 = this.method4837(new Rotation(yRot, 89.0F), vec3d, vec, vec2, mutablePos);

      for (int i = 0; i < 179; i++) {
         double d3 = i == 178 ? Double.MAX_VALUE : this.method4837(new Rotation(yRot, 89 - (i + 1)), vec3d, vec, vec2, mutablePos);
         if (d2 < 8.0 && d2 <= d && d2 <= d3) {
            f = 89 - i;
            break;
         }

         d = d2;
         d2 = d3;
      }

      if (Float.isNaN(f)) {
         return null;
      } else {
         Rotation rotation = null;
         double d3 = Double.MAX_VALUE;

         for (float f2 = f - 1.0F; f2 <= f + 1.0F; f2 += 0.1F) {
            for (float yRot2 = yRot - 1.5F; yRot2 <= yRot + 1.5F; yRot2 += 0.5F) {
               Rotation rotation2 = new Rotation(yRot2, Mth.clamp(f2, -90.0F, 90.0F));
               double d4;
               if ((d4 = this.method4837(rotation2, vec3d, vec, vec2, mutablePos)) < d3) {
                  d3 = d4;
                  rotation = rotation2;
               }
            }
         }

         return d3 <= 2.0 ? rotation : null;
      }
   }

   private boolean method4836(Rotation rotation2, Vec3 vec3d) {
      return rotation2 != null && this.method4837(rotation2, vec3d, this.method4839(), this.method4840(), new MutableBlockPos()) <= 2.0;
   }

   private double method4837(Rotation rotation, Vec3 vec3d, Vec3 vec, Vec3 vec2, MutableBlockPos mutablePos) {
      Vec3 vec3 = this.method4838(rotation, vec, vec2, mutablePos);
      return vec3 == null ? Double.MAX_VALUE : vec3.distanceTo(vec3d);
   }

   private Vec3 method4838(Rotation rotation, Vec3 vec, Vec3 vec2, MutableBlockPos mutablePos) {
      Vec3 from = vec;
      Vec3 vec3 = rotation.toDirection().scale(1.5).add(vec2);

      for (int i = 0; i < 200; i++) {
         Vec3 to = from.add(vec3);
         BlockHitResult clip = mc.level.clip(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, mc.player));
         if (clip.getType() != Type.MISS) {
            return clip.getLocation();
         }

         boolean is = mc.level.getBlockState(mutablePos.set(from.x, from.y, from.z)).getFluidState().is(FluidTags.WATER);
         vec3 = vec3.scale(is ? 0.8 : 0.99).add(0.0, -0.03, 0.0);
         from = to;
         if (to.y < mc.level.getMinY() - 64) {
            return null;
         }
      }

      return null;
   }

   private Vec3 method4839() {
      return new Vec3(mc.player.getX(), mc.player.getEyeY() - 0.1, mc.player.getZ());
   }

   private Vec3 method4840() {
      Vec3 vec = mc.player.position().subtract(mc.player.xo, mc.player.yo, mc.player.zo);
      return new Vec3(vec.x, mc.player.onGround() ? 0.0 : vec.y, vec.z);
   }

   private void method4841() {
      Rotation rotation = this.rotation;
      if (rotation != null) {
         Zenith.getInstance()
            .getRotationManager()
            .requestRotation(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
               20,
               this,
               2
            );
      }
   }

   private void method4842() {
      if (InventoryUtils.method906(Items.ENDER_PEARL) == null) {
         this.method4843();
      } else {
         this.flag = true;
         this.intValue2 = 0;
         this.stopWatch.reset();
         InventoryUtils.method927(Items.ENDER_PEARL);
      }
   }

   private void method4843() {
      this.rotation = null;
      this.vec3d = null;
      this.intValue = 0;
      this.flag = false;
      this.intValue2 = 0;
   }

   @Override
   public void onDisable() {
      this.method4843();
      super.onDisable();
   }

   private static final class Data {
      private final Vec3 vec3d;
      private final Rotation rotation;

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public Rotation rotation() {
         return this.rotation;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.rotation, other.rotation);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, rotation);
      }

      @Override
      public String toString() {
         return "Data[" + "vec3d=" + this.vec3d + ", " + "rotation=" + this.rotation + "]";
      }

      private Data(Vec3 vec3d, Rotation rotation) {
         this.vec3d = vec3d;
         this.rotation = rotation;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }
   }
}
