package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.CameraUpdateEvent;
import org.zenith.client.events.FovEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.ProjectionMatrixEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "CameraTweaks",
   description = "module.cameratweaks.description",
   category = ModuleCategory.MISC
)
public final class CameraTweaksModule extends Module {
   public static final CameraTweaksModule INSTANCE = new CameraTweaksModule();
   private float floatValue = 110.0F;
   private float floatValue2 = 30.0F;
   private float floatValue3 = 30.0F;
   private CameraType perspective;
   private Rotation rotation;
   private final MultiSelectSetting multiSetting = MultiSelectSetting.method5224(
      "module.cameraTweaks.multiSetting",
      "module.cameraTweaks.multiSetting.desc",
      List.of("module.cameraTweaks.ratio", "module.cameraTweaks.clip", "module.cameraTweaks.distance")
   );
   private final SliderSetting ratioSetting = new SliderSetting(
      "module.cameraTweaks.ratioSetting", 1.0F, 0.1F, 2.0F, 0.1F, "module.cameraTweaks.ratioSetting.desc", "x", () -> this.multiSetting.method5227(0), null
   );
   private final SliderSetting distanceSetting = new SliderSetting(
      "module.cameraTweaks.distanceSetting",
      3.0F,
      2.0F,
      5.0F,
      0.5F,
      "module.cameraTweaks.distanceSetting.desc",
      "b",
      () -> this.multiSetting.method5227(2),
      null
   );
   private final BindSetting zoomSetting = new BindSetting("module.cameraTweaks.zoomSetting", "module.cameraTweaks.zoomSetting.desc");
   private final BindSetting freeLookSetting = new BindSetting("module.cameraTweaks.freeLookSetting", "module.cameraTweaks.freeLookSetting.desc");

   private CameraTweaksModule() {
   }

   @EventTarget
   public void method2927(KeyEvent event) {
      if (mc.gui.screen() == null) {
         if (event.isPress(this.zoomSetting.getKeyCode())) {
            this.floatValue = Math.min(this.floatValue3, (float)(mc.options.fov().get() - 20));
         }

         if (event.isRelease(this.zoomSetting.getKeyCode(), true)) {
            this.floatValue3 = this.floatValue;
            this.floatValue = mc.options.fov().get().intValue();
         }

         if (event.isPress(this.freeLookSetting.getKeyCode())) {
            this.perspective = mc.options.getCameraType();
         }
      }
   }

   @EventTarget
   public void method2928(MouseScrollEvent event) {
      if (UseItemUtils.method6521(this.zoomSetting)) {
         this.floatValue = (int)Mth.clamp(this.floatValue - event.getDoubleValue2() * 10.0, 10.0, (double)mc.options.fov().get().intValue());
         event.setCancelled(true);
      }
   }

   @EventTarget
   public void method2929(FovEvent event) {
      if (UseItemUtils.method6521(this.freeLookSetting)) {
         if (mc.options.getCameraType().isFirstPerson()) {
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
         }
      } else if (this.perspective != null) {
         mc.options.setCameraType(this.perspective);
         this.perspective = null;
      }

      if (this.zoomSetting.isVisible()) {
         event.setIntValue(
            (int)Mth.clamp(
               (this.floatValue2 = MathUtils.method2660(1.6, this.floatValue2, this.floatValue)) + 1.0F,
               10.0F,
               (float)mc.options.fov().get().intValue()
            )
         );
         event.cancel();
      }
   }

   @EventTarget
   public void method2930(MouseRotationEvent event) {
      if (UseItemUtils.method6521(this.freeLookSetting)) {
         Rotation rotation = this.rotation != null ? this.rotation : new Rotation(mc.player.getYRot(), mc.player.getXRot());
         this.rotation = new Rotation(
            (float)(rotation.getFloatValue() + event.getDoubleValue() * 0.15F),
            (float)Mth.clamp(rotation.getFloatValue2() + event.getDoubleValue2() * 0.15F, -90.0, 90.0)
         );
         event.setCancelled(true);
      } else {
         this.rotation = null;
      }
   }

   @EventTarget
   public void method2931(CameraUpdateEvent event) {
      event.setFlag(this.multiSetting.method5227(1));
      if (this.multiSetting.method5227(2)) {
         event.setFloatValue(this.distanceSetting.getValue3());
      }

      if (this.rotation != null) {
         event.setRotation(this.rotation);
      }

      if (this.multiSetting.method5227(1) || this.multiSetting.method5227(2) || this.rotation != null) {
         event.cancel();
      }
   }

   @EventTarget
   public void method2932(ProjectionMatrixEvent event) {
      if (this.multiSetting.method5227(0)) {
         event.setFloatValue(this.ratioSetting.getValue3());
         event.setCancelled(true);
      }
   }
}
