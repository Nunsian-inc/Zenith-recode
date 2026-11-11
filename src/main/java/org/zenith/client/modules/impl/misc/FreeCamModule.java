package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.CameraPosEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "FreeCam",
   description = "module.freecam.description",
   category = ModuleCategory.MISC
)
public final class FreeCamModule extends Module {
   public static final FreeCamModule INSTANCE = new FreeCamModule();
   private final SliderSetting speedSetting = new SliderSetting("module.freeCam.speedSetting", 2.0F, 0.5F, 5.0F, 0.5F, "module.freeCam.speedSetting.desc", "x");
   public Vec3 vec3d;
   public Vec3 vec3d2;

   public Vec3 method1660() {
      return this.isEnabled() && this.vec3d != null ? this.vec3d : mc.getCameraEntity().getEyePosition();
   }

   private FreeCamModule() {
   }

   @Override
   public void onEnable() {
      Camera camera = mc.getEntityRenderDispatcher() == null ? null : mc.getEntityRenderDispatcher().camera;
      this.vec3d2 = this.vec3d = camera != null && mc.level != null ? new Vec3(camera.position().toVector3f()) : null;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.options.setCameraType(CameraType.FIRST_PERSON);
      super.onDisable();
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      GlowUtil.method2230(
         mc.player.getBoundingBox().move(MathUtils.method2656(mc.player).subtract(mc.player.position())),
         zenith.getStyleManager().getClientColor(90).method7287(),
         1.0F
      );
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      if (this.vec3d != null) {
         String text = MathUtils.round((float)this.vec3d.x) + "   " + MathUtils.round((float)this.vec3d.y) + "   " + MathUtils.round((float)this.vec3d.z);
         float width = Fonts.MEDIUM.getWidth(text, 7.0F);
         event.getCustomDrawContext()
            .drawText(
               Fonts.MEDIUM.getFont(7.0F),
               text,
               mc.getWindow().getGuiScaledWidth() / 2.0F - width / 2.0F,
               10.0F,
               Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor()
            );
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.vec3d == null) {
         Entity cameraEntity = mc.getCameraEntity();
         if (cameraEntity == null) {
            event.clearInput();
            return;
         }

         this.vec3d2 = this.vec3d = cameraEntity.getEyePosition();
      }

      float speed = this.speedSetting.getValue3();
      double[] horizontalMovement = MovementInputUtils.horizontalVelocityForPlayer(speed);
      this.vec3d2 = this.vec3d;
      this.vec3d = this.vec3d.add(horizontalMovement[0], event.getPlayerInput().jump() ? speed : (event.getPlayerInput().shift() ? -speed : 0.0), horizontalMovement[1]);
      event.clearInput();
   }

   @EventTarget
   public void onCameraPosition(CameraPosEvent event) {
      if (this.vec3d == null && event.getVec3d() != null) {
         this.vec3d2 = this.vec3d = event.getVec3d();
      }

      if (this.vec3d2 != null && this.vec3d != null) {
         event.setVec3d(MathUtils.method2655(this.vec3d2, this.vec3d));
         mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
      }
   }
}
