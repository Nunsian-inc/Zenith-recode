package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ParticleEmitter;
import org.zenith.utility.misc.TotemParticleRenderer;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.MatrixStackUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Totem Particles",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.totemparticles.description"
)
public final class TotemParticlesModule extends Module {
   public static final TotemParticlesModule INSTANCE = new TotemParticlesModule();
   private static final int CONST_intValue = 35;
   private static final int CONST_intValue2 = 8;
   private static final int CONST_intValue3 = 3000;
   private static final float CONST_floatValue = 2.5F;
   private static final float CONST_floatValue2 = 0.5F;
   private static final float CONST_floatValue3 = 130.0F;
   private static final float CONST_floatValue4 = 1.0F;
   private static final float CONST_floatValue5 = 1.8F;
   private static final float CONST_floatValue6 = 1.6F;
   private static final float CONST_floatValue7 = 1.0F;
   private final ModeSetting mode = new ModeSetting(
      "module.totemParticles.mode",
      "module.totemParticles.mode.desc",
      () -> true,
      "module.totemParticles.burst",
      "module.totemParticles.halo",
      "module.totemParticles.storm"
   );
   private final SliderSetting burstDensity = new SliderSetting(
      "module.totemParticles.burstDensity", 70.0F, 10.0F, 160.0F, 5.0F, "module.totemParticles.burstDensity.desc", "", () -> this.mode.isSelected(0), null
   );
   private final SliderSetting burstScale = new SliderSetting(
      "module.totemParticles.burstScale", 0.9F, 0.3F, 2.5F, 0.05F, "module.totemParticles.burstScale.desc", "x", () -> this.mode.isSelected(0), null
   );
   private final SliderSetting stormDensity = new SliderSetting(
      "module.totemParticles.stormDensity", 90.0F, 10.0F, 160.0F, 5.0F, "module.totemParticles.stormDensity.desc", "", () -> this.mode.isSelected(2), null
   );
   private final SliderSetting stormScale = new SliderSetting(
      "module.totemParticles.stormScale", 1.0F, 0.3F, 2.5F, 0.05F, "module.totemParticles.stormScale.desc", "x", () -> this.mode.isSelected(2), null
   );
   private final ModeSetting color = new ModeSetting(
      "module.totemParticles.color",
      "module.totemParticles.colorMode.desc",
      () -> true,
      "module.totemParticles.vanilla",
      "module.particles.sync",
      "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.totemParticles.customColor", "module.totemParticles.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(2)
   );
   private final List<ParticleEmitter> items = new ArrayList<>();
   private final Glowboost glowboost = new Glowboost();
   private long longValue;
   private int intValue;
   private ParticleEmitter class28;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(this.mode, this.burstDensity, this.burstScale, this.stormDensity, this.stormScale, this.color, this.customColor);
   }

   private TotemParticlesModule() {
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
   public void method4696(EntityStatusEvent event) {
      ClientboundEntityEventPacket entityStatusS2CPacket = event.getEntityStatusS2CPacket();
      if (entityStatusS2CPacket.getEventId() == 35 && mc.level != null) {
         this.method4697(entityStatusS2CPacket.getEntity(mc.level));
      }
   }

   public void method4697(Entity npc) {
      if (npc != null) {
         AABB boundingBox = npc.getBoundingBox();
         Vec3 vec = new Vec3((boundingBox.minX + boundingBox.maxX) * 0.5, boundingBox.minY, (boundingBox.minZ + boundingBox.maxZ) * 0.5);
         if (this.items.size() >= 8) {
            this.items.removeFirst();
         }

         this.items
            .add(
               new ParticleEmitter(
                     this.method4702(),
                     vec,
                     new Vec3(0.0, 1.0, 0.0),
                     npc.getBbWidth(),
                     npc.getBbHeight(),
                     boundingBox.minY,
                     3000,
                     this.intValue += 53,
                     ++this.longValue
                  )
                  .method4652(npc instanceof LivingEntity livingEntity ? livingEntity : null)
            );
      }
   }

   @EventTarget
   public void method4698(WorldRenderEvent event) {
      if (!this.items.isEmpty()) {
         if (mc.player != null && mc.level != null) {
            long now = System.currentTimeMillis();
            this.items.removeIf(var2x -> var2x.method4656(now));
            if (!this.items.isEmpty()) {
               Camera camera = mc.getEntityRenderDispatcher().camera;
               boolean flag = false;

               for (ParticleEmitter class2 : this.items) {
                  if (!flag) {
                     this.glowboost.method3232(camera, method4701(this.method4702()));
                     flag = true;
                  }

                  this.method4700(this.glowboost, class2, class2.method4655(now), 1.0F);
               }

               if (flag) {
                  this.glowboost.method3253(event.getMatrixStack());
               }
            }
         } else {
            this.items.clear();
         }
      }
   }

   public void method4699(MenuDrawContext customDrawContext, float xo, float yo, float xScale, float previewAlpha) {
      long now = System.currentTimeMillis();
      if (this.class28 == null || now >= this.longValue2) {
         this.class28 = new ParticleEmitter(this.method4702(), Vec3.ZERO, new Vec3(0.0, 1.0, 0.0), 0.6, 1.8, 0.0, 3000, this.intValue += 53, ++this.longValue);
         this.longValue2 = now + 3000L + 350L;
      }

      float f = this.class28.method4655(now);
      if (!(f >= 1.0F)) {
         PoseStack matrixStack = MatrixStackUtils.method5467(customDrawContext, xo, yo, xScale);
         this.glowboost.method3233(MatrixStackUtils.method5468(), MatrixStackUtils.method5469(), method4701(this.class28.intValue));
         this.method4700(this.glowboost, this.class28, f, previewAlpha);
         this.glowboost.method3253(matrixStack);
      }
   }

   private void method4700(Glowboost glowboost, ParticleEmitter class2, float f, float previewAlpha) {
      if (!(f < 0.0F) && !(f >= 1.0F)) {
         glowboost.method3235(method4701(class2.intValue) * previewAlpha);
         int color = this.method4703(class2);
         switch (class2.intValue) {
            case 1:
               TotemParticleRenderer.drawRings(glowboost, class2, f, color, 1.0F, 130.0F);
               break;
            case 2:
               TotemParticleRenderer.drawStorm(glowboost, class2, f, color, this.stormScale.getValue3(), this.stormDensity.getValue3(), 1.6F);
               break;
            default:
               TotemParticleRenderer.drawBurst(glowboost, class2, f, color, this.burstScale.getValue3(), this.burstDensity.getValue3(), 2.5F);
         }
      }
   }

   private static float method4701(int intValue) {
      return switch (intValue) {
         case 1 -> 1.8F;
         case 2 -> 1.0F;
         default -> 0.5F;
      };
   }

   private int method4702() {
      for (int i = 0; i < 3; i++) {
         if (this.mode.isSelected(i)) {
            return i;
         }
      }

      return 0;
   }

   private int method4703(ParticleEmitter class2) {
      if (this.color.isSelected(2)) {
         return this.customColor.getZenithColor().method7287();
      } else if (this.color.isSelected(1)) {
         return Zenith.getInstance().getStyleManager().getClientColor(class2.intValue3).method7287();
      } else {
         float f = 60.0F + Glowboost.method3268(class2.longValue2, 0, 31) * 60.0F;
         return ZenithColor.method7297(f / 360.0F, 0.9F, 1.0F).method7287();
      }
   }
}
