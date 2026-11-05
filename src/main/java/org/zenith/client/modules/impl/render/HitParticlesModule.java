package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.Class28;
import org.zenith.utility.misc.Class42;
import org.zenith.utility.misc.Util28;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.MatrixStackUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Hit Particles",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.hitparticles.description"
)
public final class HitParticlesModule extends Module {
   public static final HitParticlesModule INSTANCE = new HitParticlesModule();
   private static final int CONST_intValue = 16;
   private static final float[] CONST_floatValues = new float[]{0.8F, 1.15F, 1.0F};
   private static final float CONST_floatValue = 100.0F;
   private static final float CONST_floatValue2 = 2.1F;
   private static final float CONST_floatValue3 = 110.0F;
   private static final float CONST_floatValue4 = 0.55F;
   private static final float CONST_floatValue5 = 0.3F;
   private static final float CONST_floatValue6 = 1.2F;
   private static final float CONST_floatValue7 = 1.5F;
   private static final float CONST_floatValue8 = 0.4F;
   private static final double CONST_doubleValue = 0.18;
   private final ModeSetting mode = new ModeSetting(
      "module.hitParticles.mode",
      "module.hitParticles.mode.desc",
      () -> true,
      "module.hitParticles.impact",
      "module.hitParticles.rift",
      "module.hitParticles.sparks"
   );
   private final SliderSetting impactScale = new SliderSetting(
      "module.hitParticles.impactScale", 0.6F, 0.3F, 1.5F, 0.05F, "module.hitParticles.impactScale.desc", "x", () -> this.mode.isSelected(0), null
   );
   private final SliderSetting impactLifetime = new SliderSetting(
      "module.hitParticles.impactLifetime",
      1500.0F,
      200.0F,
      2500.0F,
      50.0F,
      "module.hitParticles.impactLifetime.desc",
      "ms",
      () -> this.mode.isSelected(0),
      null
   );
   private final SliderSetting riftSpeed = new SliderSetting(
      "module.hitParticles.riftSpeed", 2.2F, 0.3F, 3.0F, 0.1F, "module.hitParticles.riftSpeed.desc", "x", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting riftLifetime = new SliderSetting(
      "module.hitParticles.riftLifetime", 1750.0F, 200.0F, 2500.0F, 50.0F, "module.hitParticles.riftLifetime.desc", "ms", () -> this.mode.isSelected(1), null
   );
   private final SliderSetting sparksDensity = new SliderSetting(
      "module.hitParticles.sparksDensity", 100.0F, 6.0F, 100.0F, 2.0F, "module.hitParticles.sparksDensity.desc", "", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting sparksLifetime = new SliderSetting(
      "module.hitParticles.sparksLifetime",
      1150.0F,
      200.0F,
      2500.0F,
      50.0F,
      "module.hitParticles.sparksLifetime.desc",
      "ms",
      () -> this.mode.isSelected(2),
      null
   );
   private final ModeSetting color = new ModeSetting(
      "module.hitParticles.color", "module.hitParticles.colorMode.desc", () -> true, "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.hitParticles.customColor", "module.hitParticles.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(1)
   );
   private final BooleanSetting onlyCrit = new BooleanSetting("module.hitParticles.onlyCrit", "module.hitParticles.onlyCrit.desc", false);
   private final List<Class28> items = new CopyOnWriteArrayList<>();
   private final Glowboost glowboost = new Glowboost();
   private long longValue;
   private int intValue;
   private static final Class42.Handler handler = (d, d2, d3) -> d2 < 0.0;
   private final Class42.Handler handler2 = (x, y, z) -> {
      ClientLevel level = mc.level;
      if (level == null) {
         return false;
      } else {
         BlockPos containing = BlockPos.containing(x, y, z);
         if (!level.hasChunk(containing.getX() >> 4, containing.getZ() >> 4)) {
            return false;
         } else {
            BlockState blockState = level.getBlockState(containing);
            if (blockState.isAir()) {
               return false;
            } else {
               VoxelShape collisionShape = blockState.getCollisionShape(level, containing);
               if (collisionShape.isEmpty()) {
                  return false;
               } else {
                  double d = x - containing.getX();
                  double y2 = y - containing.getY();
                  double z2 = z - containing.getZ();

                  for (AABB aabb : collisionShape.toAabbs()) {
                     if (aabb.contains(d, y2, z2)) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   };
   private Class28 class28;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(
         this.mode,
         this.impactScale,
         this.impactLifetime,
         this.riftSpeed,
         this.riftLifetime,
         this.sparksDensity,
         this.sparksLifetime,
         this.color,
         this.customColor,
         this.onlyCrit
      );
   }

   private HitParticlesModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
   }

   @EventTarget
   public void method7792(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST) {
         if (mc.player != null && mc.level != null) {
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity && entity != mc.player) {
               if (!this.onlyCrit.isEnabled() || this.method7801()) {
                  int intValue = this.method7797();
                  Vec3 eyePosition = intValue == 0 ? entity.getEyePosition() : entity.getBoundingBox().getCenter();
                  if (intValue == 1) {
                     eyePosition = eyePosition.add(0.0, entity.getBbHeight() * 0.15, 0.0);
                  }

                  Vec3 vec = mc.player.getEyePosition().subtract(eyePosition);
                  vec = vec.lengthSqr() > 1.0E-6 ? vec.normalize() : new Vec3(0.0, 0.0, -1.0);
                  Vec3 vec2 = intValue == 0 ? eyePosition.add(vec.scale(0.18)) : eyePosition;
                  int n = Math.round(this.method7798(intValue) * CONST_floatValues[intValue]);
                  if (this.items.size() >= 16) {
                     this.items.stream().findFirst().ifPresent(this.items::remove);
                  }

                  this.items.add(new Class28(intValue, vec2, vec, entity.getBbWidth(), n, this.intValue += 37, ++this.longValue).method4652((LivingEntity)entity));
               }
            }
         }
      }
   }

   @EventTarget
   public void method7793(WorldRenderEvent event) {
      if (!this.items.isEmpty()) {
         if (mc.player != null && mc.level != null) {
            long now = System.currentTimeMillis();
            this.items.removeIf(var2x -> var2x.method4656(now));
            if (!this.items.isEmpty()) {
               Camera camera = mc.getEntityRenderDispatcher().camera;
               float f = method7799(this.method7797());
               Class42.method7379(this.handler2);
               this.method7794(event, camera, f, now, false);
               this.method7794(event, camera, f, now, true);
            }
         } else {
            this.items.clear();
         }
      }
   }

   private void method7794(WorldRenderEvent event, Camera camera, float f, long now, boolean flag) {
      boolean flag2 = false;

      for (Class28 class2 : this.items) {
         if (class2.intValue == 0 == flag) {
            if (!flag2) {
               this.glowboost.method3232(camera, f);
               this.glowboost.setFlag(flag);
               flag2 = true;
            }

            this.method7796(this.glowboost, class2, class2.method4655(now), 1.0F);
         }
      }

      if (flag2) {
         this.glowboost.method3253(event.getMatrixStack());
      }
   }

   public void method7795(MenuDrawContext customDrawContext, float xo, float yo, float xScale, float previewAlpha) {
      long now = System.currentTimeMillis();
      if (this.class28 == null || now >= this.longValue2) {
         int intValue = this.method7797();
         int n = Math.round(this.method7798(intValue) * CONST_floatValues[intValue]);
         Vec3 vec = new Vec3(0.3, 0.16, 0.94).normalize();
         Vec3 vec2 = intValue == 0 ? new Vec3(0.0, 1.62, 0.0) : new Vec3(0.0, 0.9 + (intValue == 1 ? 0.27 : 0.0), 0.0);
         Vec3 vec3 = intValue == 0 ? vec2.add(vec.scale(0.18)) : vec2;
         this.class28 = new Class28(intValue, vec3, vec, 0.6, n, this.intValue += 37, ++this.longValue);
         this.longValue2 = now + n + 350L;
      }

      float f = this.class28.method4655(now);
      if (!(f >= 1.0F)) {
         Class42.method7379(handler);
         PoseStack matrixStack = MatrixStackUtils.method5467(customDrawContext, xo, yo, xScale);
         this.glowboost.method3233(MatrixStackUtils.method5468(), MatrixStackUtils.method5469(), method7799(this.class28.intValue));
         this.method7796(this.glowboost, this.class28, f, previewAlpha);
         this.glowboost.method3253(matrixStack);
      }
   }

   private void method7796(Glowboost glowboost, Class28 class2, float f, float previewAlpha) {
      if (!(f < 0.0F) && !(f >= 1.0F)) {
         glowboost.method3235(method7799(class2.intValue) * previewAlpha);
         int color = this.method7800(class2.intValue3);
         switch (class2.intValue) {
            case 0:
               Util28.method7552(glowboost, class2, f, color, this.impactScale.getValue3(), 100.0F);
               break;
            case 1:
               Util28.method7553(glowboost, class2, f, color, this.riftSpeed.getValue3(), 0.55F, 110.0F);
               break;
            default:
               Util28.method7556(glowboost, class2, f, color, 1.5F, 1.2F, this.sparksDensity.getValue3());
         }
      }
   }

   private int method7797() {
      for (int i = 0; i < 3; i++) {
         if (this.mode.isSelected(i)) {
            return i;
         }
      }

      return 0;
   }

   private float method7798(int intValue) {
      return switch (intValue) {
         case 0 -> this.impactLifetime.getValue3();
         case 1 -> this.riftLifetime.getValue3();
         default -> this.sparksLifetime.getValue3();
      };
   }

   private static float method7799(int intValue) {
      return switch (intValue) {
         case 0 -> 2.1F;
         case 1 -> 0.3F;
         default -> 0.4F;
      };
   }

   private int method7800(int intValue) {
      return this.color.isSelected(1)
         ? this.customColor.getZenithColor().method7287()
         : Zenith.getInstance().getStyleManager().getClientColor(intValue).method7287();
   }

   private boolean method7801() {
      LocalPlayer player = mc.player;
      return player != null
         && player.fallDistance > 0.0
         && !player.onGround()
         && !player.onClimbable()
         && !player.isInWater()
         && !player.isPassenger()
         && !player.isSprinting();
   }
}
