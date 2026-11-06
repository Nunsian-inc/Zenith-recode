package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorProvider;
import org.zenith.utility.misc.AmbientStrandRenderer;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.MatrixStackUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Ambient Particles",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.ambientparticles.description"
)
public final class AmbientParticlesModule extends Module {
   public static final AmbientParticlesModule INSTANCE = new AmbientParticlesModule();
   private static final float CONST_floatValue = 3.0F;
   private static final float CONST_floatValue2 = 1.15F;
   private static final double CONST_doubleValue = 4.0;
   private static final float CONST_floatValue3 = 900.0F;
   private final SliderSetting strands = new SliderSetting(
      "module.ambientParticles.strands", 80.0F, 4.0F, 160.0F, 2.0F, "module.ambientParticles.strands.desc", "", () -> true, null
   );
   private final SliderSetting radius = new SliderSetting(
      "module.ambientParticles.radius", 25.0F, 6.0F, 64.0F, 1.0F, "module.ambientParticles.radius.desc", "b", () -> true, null
   );
   private final ModeSetting color = new ModeSetting(
      "module.ambientParticles.color", "module.ambientParticles.colorMode.desc", () -> true, "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.ambientParticles.customColor", "module.ambientParticles.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(1)
   );
   private final Glowboost glowboost = new Glowboost();
   private final ColorProvider colorProvider = this::method2395;
   private static final long CONST_longValue = System.currentTimeMillis();
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private long longValue;
   private boolean flag;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(this.strands, this.radius, this.color, this.customColor);
   }

   private AmbientParticlesModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.longValue2 = System.currentTimeMillis();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.flag = false;
   }

   private float method2390() {
      return Mth.clamp((float)(System.currentTimeMillis() - this.longValue2) / 900.0F, 0.0F, 1.0F);
   }

   @EventTarget
   public void method2391(WorldRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         Camera camera = mc.getEntityRenderDispatcher().camera;
         Vec3 position = mc.player.position();
         this.method2393(position);
         this.glowboost.method3232(camera, 1.15F);
         AmbientStrandRenderer.renderStrands(
            this.glowboost,
            position.x,
            position.y + 1.0,
            position.z,
            this.doubleValue,
            this.doubleValue2,
            this.doubleValue3,
            method2394(),
            (int)this.strands.getValue3(),
            this.radius.getValue3(),
            this.method2390(),
            this.colorProvider
         );
         this.glowboost.method3253(event.getMatrixStack());
      }
   }

   public void method2392(MenuDrawContext customDrawContext, float xo, float yo, float xScale, float previewAlpha) {
      PoseStack matrixStack = MatrixStackUtils.method5467(customDrawContext, xo, yo, xScale);
      this.glowboost.method3233(MatrixStackUtils.method5468(), MatrixStackUtils.method5469(), 1.15F * previewAlpha);
      AmbientStrandRenderer.renderStrands(this.glowboost, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, method2394(), (int)this.strands.getValue3(), 3.0F, 1.0F, this.colorProvider);
      this.glowboost.method3253(matrixStack);
   }

   private void method2393(Vec3 position) {
      long now = System.currentTimeMillis();
      if (!this.flag) {
         this.doubleValue = position.x;
         this.doubleValue2 = position.y + 1.0;
         this.doubleValue3 = position.z;
         this.longValue = now;
         this.flag = true;
      } else {
         double d = Math.min(0.25, (now - this.longValue) / 1000.0);
         this.longValue = now;
         double d2 = 1.0 - Math.exp(-4.0 * d);
         this.doubleValue = this.doubleValue + (position.x - this.doubleValue) * d2;
         this.doubleValue2 = this.doubleValue2 + (position.y + 1.0 - this.doubleValue2) * d2;
         this.doubleValue3 = this.doubleValue3 + (position.z - this.doubleValue3) * d2;
      }
   }

   private static float method2394() {
      return (float)(System.currentTimeMillis() - CONST_longValue) / 1000.0F;
   }

   private int method2395(int intValue) {
      return this.color.isSelected(1)
         ? this.customColor.getZenithColor().method7287()
         : Zenith.getInstance().getStyleManager().getClientColor(intValue).method7287();
   }
}
