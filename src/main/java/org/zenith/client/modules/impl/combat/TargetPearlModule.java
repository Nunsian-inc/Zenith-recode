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
import org.zenith.client.events.Event5;
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
   public void method4829(EntityAddEvent var1) {
      if (mc.level != null && mc.player != null) {
         if (var1.getEntity() instanceof ThrownEnderpearl var2 && var2.getOwner() == null) {
            mc.level
               .players()
               .stream()
               .filter(var1x -> var1x != mc.player && var1x.distanceTo(var2) <= 3.0F)
               .min(Comparator.comparingDouble(var1x -> var1x.distanceTo(var2)))
               .ifPresent(var2::setOwner);
         }
      }
   }

   @EventTarget
   public void method4830(Event5 var1) {
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
         Rotation var2 = Zenith.getInstance().getRotationManager().getRotation2();
         if (this.rotation != null && this.vec3d != null && this.versionSetting.isSelected(0) && this.method4836(var2, this.vec3d)) {
            this.rotation = var2;
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
   public void method4831(InteractItemEvent var1) {
      if (this.flag && mc.player != null) {
         if (mc.player.getMainHandItem().is(Items.ENDER_PEARL) || mc.player.getOffhandItem().is(Items.ENDER_PEARL)) {
            this.method4843();
         }
      }
   }

   private boolean method4832() {
      if (!this.stopWatch.method7546(1000L)) {
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
               .noneMatch(var0 -> Objects.equals(var0.getOwner(), mc.player));
      }
   }

   private void method4833() {
      PredictionsModule var1 = PredictionsModule.INSTANCE;
      Vec3 var2 = mc.player.position();
      Rotation var3 = Zenith.getInstance().getRotationManager().method7844();
      this.rotation = null;
      this.vec3d = null;
      UseItemUtils.method6510()
         .filter(ThrownEnderpearl.class::isInstance)
         .map(ThrownEnderpearl.class::cast)
         .filter(this::method4834)
         .map(var1::method2919)
         .filter(Objects::nonNull)
         .map(HitResult::getLocation)
         .filter(var2x -> var2x.distanceTo(var2) >= this.distanceSetting.getValue3())
         .map(var1x -> new TargetPearlModule.Data(var1x, this.method4835(var1x)))
         .filter(var0 -> var0.rotation() != null)
         .min(Comparator.comparingDouble(var1x -> var3.method3131(var1x.rotation()).method5327()))
         .ifPresent(var1x -> {
            this.rotation = var1x.rotation();
            this.vec3d = var1x.getVec3d();
         });
   }

   private boolean method4834(ThrownEnderpearl var1) {
      Entity var2 = var1.getOwner();
      if (var2 == null || var2 == mc.player || Zenith.getInstance().getFriends().method7626(var2)) {
         return false;
      } else if (!this.targetSetting.isSelected(1) && AuraModule.INSTANCE.isEnabled()) {
         LivingEntity var3 = AuraModule.INSTANCE.getLivingEntity22();
         return var3 == null || var3.equals(var2);
      } else {
         return true;
      }
   }

   private Rotation method4835(Vec3 var1) {
      Vec3 var2 = this.method4839();
      Vec3 var3 = this.method4840();
      MutableBlockPos var4 = new MutableBlockPos();
      float var5 = (float)(Mth.atan2(var1.z - var2.z, var1.x - var2.x) * (180.0 / Math.PI)) - 90.0F;
      float var6 = Float.NaN;
      double var7 = Double.MAX_VALUE;
      double var9 = this.method4837(new Rotation(var5, 89.0F), var1, var2, var3, var4);

      for (int var11 = 0; var11 < 179; var11++) {
         double var12 = var11 == 178 ? Double.MAX_VALUE : this.method4837(new Rotation(var5, 89 - (var11 + 1)), var1, var2, var3, var4);
         if (var9 < 8.0 && var9 <= var7 && var9 <= var12) {
            var6 = 89 - var11;
            break;
         }

         var7 = var9;
         var9 = var12;
      }

      if (Float.isNaN(var6)) {
         return null;
      } else {
         Rotation var19 = null;
         double var20 = Double.MAX_VALUE;

         for (float var14 = var6 - 1.0F; var14 <= var6 + 1.0F; var14 += 0.1F) {
            for (float var15 = var5 - 1.5F; var15 <= var5 + 1.5F; var15 += 0.5F) {
               Rotation var16 = new Rotation(var15, Mth.clamp(var14, -90.0F, 90.0F));
               double var17;
               if ((var17 = this.method4837(var16, var1, var2, var3, var4)) < var20) {
                  var20 = var17;
                  var19 = var16;
               }
            }
         }

         return var20 <= 2.0 ? var19 : null;
      }
   }

   private boolean method4836(Rotation var1, Vec3 var2) {
      return var1 != null && this.method4837(var1, var2, this.method4839(), this.method4840(), new MutableBlockPos()) <= 2.0;
   }

   private double method4837(Rotation var1, Vec3 var2, Vec3 var3, Vec3 var4, MutableBlockPos var5) {
      Vec3 var6 = this.method4838(var1, var3, var4, var5);
      return var6 == null ? Double.MAX_VALUE : var6.distanceTo(var2);
   }

   private Vec3 method4838(Rotation var1, Vec3 var2, Vec3 var3, MutableBlockPos var4) {
      Vec3 var5 = var2;
      Vec3 var6 = var1.method3135().scale(1.5).add(var3);

      for (int var7 = 0; var7 < 200; var7++) {
         Vec3 var8 = var5.add(var6);
         BlockHitResult var9 = mc.level.clip(new ClipContext(var5, var8, Block.COLLIDER, Fluid.NONE, mc.player));
         if (var9.getType() != Type.MISS) {
            return var9.getLocation();
         }

         boolean var10 = mc.level.getBlockState(var4.set(var5.x, var5.y, var5.z)).getFluidState().is(FluidTags.WATER);
         var6 = var6.scale(var10 ? 0.8 : 0.99).add(0.0, -0.03, 0.0);
         var5 = var8;
         if (var8.y < mc.level.getMinY() - 64) {
            return null;
         }
      }

      return null;
   }

   private Vec3 method4839() {
      return new Vec3(mc.player.getX(), mc.player.getEyeY() - 0.1, mc.player.getZ());
   }

   private Vec3 method4840() {
      Vec3 var1 = mc.player.position().subtract(mc.player.xo, mc.player.yo, mc.player.zo);
      return new Vec3(var1.x, mc.player.onGround() ? 0.0 : var1.y, var1.z);
   }

   private void method4841() {
      Rotation var1 = this.rotation;
      if (var1 != null) {
         Zenith.getInstance()
            .getRotationManager()
            .method7848(
               new RotationRequest(var1, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var1), bcCameraTestnerv.getRotationConfig()),
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

      private Data(Vec3 var1, Rotation var2) {
         this.vec3d = var1;
         this.rotation = var2;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }
   }
}
