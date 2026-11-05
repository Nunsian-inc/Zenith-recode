package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.CanvasSetting;
import org.zenith.utility.misc.Canvas;
import org.zenith.utility.misc.CrosshairPresets;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Crosshair",
   category = ModuleCategory.RENDER,
   description = "module.crosshair.description"
)
public final class CrosshairModule extends Module {
   public static final CrosshairModule INSTANCE = new CrosshairModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 4;
   private static final float CONST_floatValue = 220.0F;
   private static final long CONST_longValue = System.currentTimeMillis();
   private final CanvasSetting canvas = new CanvasSetting("module.crosshair.canvas");
   private final ButtonSetting editor = new ButtonSetting("module.crosshair.editor", "w", this::method516);
   private final SliderSetting scale = new SliderSetting("module.crosshair.scale", 2.0F, 1.0F, 6.0F, 1.0F, "module.crosshair.scale.desc", "x");
   private final ModeSetting color = new ModeSetting(
      "module.crosshair.color", "module.crosshair.colorMode.desc", "module.crosshair.asDrawn", "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.crosshair.customColor", "module.crosshair.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(2)
   );
   private final ModeSetting animation = new ModeSetting(
      "module.crosshair.animation",
      "module.crosshair.animation.desc",
      "module.crosshair.animNone",
      "module.crosshair.animSpin",
      "module.crosshair.animPulse",
      "module.crosshair.animRecoil",
      "module.crosshair.animHit"
   );
   private final SliderSetting animationSpeed = new SliderSetting(
      "module.crosshair.animationSpeed", 1.0F, 0.2F, 3.0F, 0.1F, "module.crosshair.animationSpeed.desc", "x", () -> !this.animation.isSelected(0), null
   );
   private long longValue;

   private List<Setting> method515() {
      return List.of(this.scale, this.color, this.customColor, this.animation, this.animationSpeed);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.editor, this.canvas);
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      JsonObject jsonObject = json.getAsJsonObject("Settings");

      for (Setting setting : this.method515()) {
         setting.safe(jsonObject);
      }

      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null) {
         JsonObject jsonObject = json.getAsJsonObject("Settings");
         if (jsonObject != null) {
            for (Setting setting : this.method515()) {
               if (jsonObject.has(setting.getKey())) {
                  setting.load(jsonObject);
               }
            }
         }
      }
   }

   private void method516() {
      Zenith.getInstance().getNLMenuScreen().getCrosshairEditor().open(this.canvas.getCanvas(), this.method515());
   }

   private CrosshairModule() {
      this.canvas.getCanvas().method139(CrosshairPresets.method5712(0));
   }

   @EventTarget
   public void method517(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST) {
         this.longValue = System.currentTimeMillis();
      }
   }

   @EventTarget(0)
   public void method518(ScreenRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            Canvas canvas = this.canvas.getCanvas();
            if (!canvas.isEmpty()) {
               MenuDrawContext menuDrawContext = event.getMenuDrawContext();
               float x = mc.getWindow().getGuiScaledWidth() / 2.0F;
               float y = mc.getWindow().getGuiScaledHeight() / 2.0F;
               float cell = this.scale.getValue3();
               float f = this.animationSpeed.getValue3();
               float f2 = (float)(System.currentTimeMillis() - CONST_longValue) / 1000.0F;
               float angdeg = 0.0F;
               float x2 = 1.0F;
               float f3 = 1.0F;
               switch (this.method520()) {
                  case 1:
                     angdeg = f2 * 60.0F * f;
                     break;
                  case 2:
                     x2 = 1.0F + 0.14F * Mth.sin(f2 * 3.0F * f);
                     break;
                  case 3:
                     float f4 = Mth.clamp(mc.player.getAttackStrengthScale(0.0F), 0.0F, 1.0F);
                     x2 = 1.0F + 0.45F * (1.0F - f4) * f;
                     break;
                  case 4:
                     float f5 = (float)(System.currentTimeMillis() - this.longValue) / (220.0F / f);
                     float f6 = f5 >= 1.0F ? 0.0F : (1.0F - f5) * (1.0F - f5);
                     x2 = 1.0F + 0.5F * f6;
                     f3 = 1.0F + 0.8F * f6;
               }

               menuDrawContext.pushMatrix();
               menuDrawContext.pose().translate(x, y);
               if (angdeg != 0.0F) {
                  menuDrawContext.pose().rotate((float)Math.toRadians(angdeg));
               }

               if (x2 != 1.0F) {
                  menuDrawContext.pose().scale(x2, x2);
               }

               menuDrawContext.beginRectBatch();
               float f4 = -15.0F * cell;

               for (int i = 0; i < 31; i++) {
                  for (int j = 0; j < 31; j++) {
                     int n = canvas.method136(j, i);
                     if (n >>> 24 != 0) {
                        menuDrawContext.drawRectBatched(f4 + j * cell, f4 + i * cell, cell, cell, this.method519(n, f3));
                     }
                  }
               }

               menuDrawContext.flushRects();
               menuDrawContext.popMatrix();
            }
         }
      }
   }

   private ZenithColor method519(int n, float f) {
      int alpha = n >>> 24;
      int intValue = n >> 16 & 0xFF;
      int green = n >> 8 & 0xFF;
      int blue = n & 0xFF;
      if (this.color.isSelected(1)) {
         ZenithColor clientColor = Zenith.getInstance().getStyleManager().getClientColor(0);
         intValue = clientColor.getIntValue();
         green = clientColor.getIntValue2();
         blue = clientColor.getIntValue3();
      } else if (this.color.isSelected(2)) {
         ZenithColor zenithColor = this.customColor.getZenithColor();
         intValue = zenithColor.getIntValue();
         green = zenithColor.getIntValue2();
         blue = zenithColor.getIntValue3();
      }

      if (f != 1.0F) {
         intValue = Math.min(255, Math.round(intValue * f));
         green = Math.min(255, Math.round(green * f));
         blue = Math.min(255, Math.round(blue * f));
      }

      return new ZenithColor(intValue, green, blue, alpha);
   }

   private int method520() {
      for (int i = 0; i < 5; i++) {
         if (this.animation.isSelected(i)) {
            return i;
         }
      }

      return 0;
   }
}
