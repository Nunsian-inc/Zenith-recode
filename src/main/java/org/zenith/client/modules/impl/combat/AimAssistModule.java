package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.AssistSilent;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.ReplayStrokes;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.misc.MovementInputUtils;

@ModuleInfo(
   name = "AimAssist",
   description = "module.aimassist.description",
   category = ModuleCategory.COMBAT
)
public class AimAssistModule extends Module {
   public static final AimAssistModule INSTANCE = new AimAssistModule();
   private final GroupSetting safeTarget = new GroupSetting(
      "module.aimAssist.targetSettingWindow",
      "module.aura.targetWindow.desc",
      () -> true,
      MultiSelectSetting.method5224(
         "module.aura.targetTypeSetting",
         "module.aura.targetTypeSetting.desc",
         List.of("module.aura.targetPlayers", "module.aura.noarmor", "module.aura.targetHostile", "module.aura.targetPeaceful")
      ),
      MultiSelectSetting.method5224(
         "module.aura.targetSortSetting",
         "module.aura.targetSortSetting.desc",
         List.of("module.aura.targetFov", "module.aura.targetArmor", "module.aura.targetHp", "module.aura.targetDistance")
      ),
      new BooleanSetting("module.aura.safeTarget", "module.aura.safeTarget.desc", false)
   );
   private final SliderSetting distance = new SliderSetting("module.aimAssist.distance", 4.0F, 0.5F, 6.0F, 0.1F);
   private final ModeSetting mode = new ModeSetting(
      "module.aimAssist.mode",
      "module.aimAssist.mode.desc",
      "module.aimAssist.mode.multiply",
      "module.aimAssist.mode.silent",
      "module.aimAssist.mode.silentStrong",
      "module.aimAssist.mode.replay"
   );
   private final BooleanSetting replayLearn = new BooleanSetting("module.aimAssist.replayLearn", "module.aimAssist.replayLearn.desc", true, this::method5154);
   private final SliderSetting replayFov = new SliderSetting(
      "module.aimAssist.replayFov", 60.0F, 5.0F, 180.0F, 5.0F, "module.aimAssist.replayFov.desc", "°", this::method5154
   );
   private final SliderSetting replayIdle = new SliderSetting(
      "module.aimAssist.replayIdle", 2.0F, 1.0F, 10.0F, 1.0F, "module.aimAssist.replayIdle.desc", "t", this::method5154
   );
   private final BooleanSetting replayPitch = new BooleanSetting("module.aimAssist.replayPitch", "module.aimAssist.replayPitch.desc", false, this::method5154);
   private final ButtonSetting replayClear = new ButtonSetting("module.aimAssist.replayClear", "t", "module.aimAssist.replayClear.desc", () -> {
      ReplayStrokes.INSTANCE.clear();
      ChatLogger.method2019(Zenith.getInstance().getLocalization().translate("module.aimAssist.replayCleared"));
   });
   private final BooleanSetting neuroDebug = new BooleanSetting("module.aimAssist.neuroDebug", false, () -> Zenith.method1502() && !this.method5153());
   private int intValue = -32768 << 16 | 0;
   private int intValue2;
   private final BooleanSetting changeX = new BooleanSetting("module.aimAssist.changeX", true, this::method5153);
   private final SliderSetting accelerationX = new SliderSetting(
      "module.aimAssist.accelerationX", 1.75F, 0.1F, 2.5F, 0.05F, () -> this.method5153() && this.changeX.isEnabled()
   );
   private final SliderSetting decelerationX = new SliderSetting(
      "module.aimAssist.decelerationX", 0.4F, 0.1F, 1.0F, 0.05F, () -> this.method5153() && this.changeX.isEnabled()
   );
   private final SliderSetting diffRangeX = new SliderSetting(
      "module.aimAssist.diffRangeX", 20.0F, 0.0F, 100.0F, 5.0F, () -> this.method5153() && this.changeX.isEnabled()
   );
   private final BooleanSetting changeY = new BooleanSetting("module.aimAssist.changeY", false, this::method5153);
   private final SliderSetting accelerationY = new SliderSetting(
      "module.aimAssist.accelerationY", 1.25F, 0.1F, 2.5F, 0.05F, () -> this.method5153() && this.changeY.isEnabled()
   );
   private final SliderSetting decelerationY = new SliderSetting(
      "module.aimAssist.decelerationY", 0.75F, 0.1F, 1.0F, 0.05F, () -> this.method5153() && this.changeY.isEnabled()
   );
   private final SliderSetting diffRangeY = new SliderSetting(
      "module.aimAssist.diffRangeY", 20.0F, 0.0F, 100.0F, 5.0F, () -> this.method5153() && this.changeY.isEnabled()
   );
   private final ModeSetting correction = new ModeSetting("module.aura.correction", "module.aura.correction.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.correction, "module.aura.correctionFocus").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.correction, "module.aura.correctionNone");
   private LivingEntity livingEntity;
   private final List<Entity> items = new ArrayList<>();

   private boolean method5153() {
      return this.mode.method2101("module.aimAssist.mode.multiply");
   }

   private boolean method5154() {
      return this.mode.method2101("module.aimAssist.mode.replay");
   }

   private AssistSilent method5155() {
      if (this.mode.method2101("module.aimAssist.mode.silent")) {
         return AssistSilent.INSTANCE;
      } else {
         return this.mode.method2101("module.aimAssist.mode.silentStrong") ? AssistSilent.INSTANCE2 : null;
      }
   }

   private AimAssistModule() {
      this.replayClear.setVisible(this::method5154);
   }

   public LivingEntity method5156() {
      return !this.isEnabled() ? null : this.livingEntity;
   }

   @EventTarget
   public void method5157(MouseRotationEvent event) {
      if (mc.player != null && mc.level != null) {
         this.livingEntity = this.method5159();
         if (!RotationRecorderModule.INSTANCE.isEnabled()) {
            if (this.method5154()) {
               ReplayStrokes.INSTANCE
                  .method6649(
                     event,
                     this.livingEntity,
                     this.replayLearn.isEnabled(),
                     this.replayFov.getValue3(),
                     this.replayPitch.isEnabled(),
                     this.replayIdle.getValue3() * 50.0
                  );
               if (Zenith.method1502() && this.neuroDebug.isEnabled() && mc.player.tickCount != this.intValue) {
                  this.intValue = mc.player.tickCount;
                  if (++this.intValue2 >= 20) {
                     this.intValue2 = 0;
                     ChatLogger.method2019(ReplayStrokes.INSTANCE.method6667());
                  }
               }
            } else {
               ReplayStrokes.INSTANCE.reset();
               if (this.method5155() == null) {
                  if (this.livingEntity != null) {
                     Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());
                     Rotation rotation2 = RotationUtils.method6912(this.livingEntity.getBoundingBox().getCenter().subtract(mc.player.getEyePosition()));
                     float f = Mth.wrapDegrees(rotation2.getFloatValue() - rotation.getFloatValue());
                     float f2 = Mth.wrapDegrees(rotation2.getFloatValue2() - rotation.getFloatValue2());
                     if (this.changeX.isEnabled() && Math.abs(f) > this.diffRangeX.getValue3()) {
                        event.setDoubleValue(
                           this.method5161(
                              event.getDoubleValue(), f, this.accelerationX.getValue3(), Math.abs(f) < 40.0F ? this.decelerationX.getValue3() : 1.0F
                           )
                        );
                     }

                     if (this.changeY.isEnabled() && Math.abs(f2) > this.diffRangeY.getValue3()) {
                        event.setDoubleValue2(this.method5161(event.getDoubleValue2(), f2, this.accelerationY.getValue3(), this.decelerationY.getValue3()));
                     }
                  }
               }
            }
         }
      } else {
         this.livingEntity = null;
      }
   }

   @EventTarget
   public void method5158(HandleInputPostEvent event) {
      AssistSilent assistSilent = this.method5155();
      if (assistSilent != AssistSilent.INSTANCE) {
         AssistSilent.INSTANCE.reset();
      }

      if (assistSilent != AssistSilent.INSTANCE2) {
         AssistSilent.INSTANCE2.reset();
      }

      if (assistSilent != null && mc.player != null && mc.level != null && !RotationRecorderModule.INSTANCE.isEnabled()) {
         this.livingEntity = this.method5159();
         assistSilent.method6309(this.livingEntity, this);
         if (Zenith.method1502() && this.neuroDebug.isEnabled() && ++this.intValue2 >= 20) {
            this.intValue2 = 0;
            ChatLogger.method2019(assistSilent.method6312());
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.livingEntity = null;
      AssistSilent.INSTANCE.reset();
      AssistSilent.INSTANCE2.reset();
      ReplayStrokes.INSTANCE.shutdown();
   }

   private LivingEntity method5159() {
      float f = this.distance.getValue3();
      LivingEntity livingEntity = AuraUtil.method7446(
         this.method5160(f),
         f,
         true,
         ((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(1)).method5229(),
         ((BooleanSetting)this.safeTarget.getSettings().get(2)).isEnabled()
      );
      this.items.clear();
      return livingEntity;
   }

   private List<Entity> method5160(float f) {
      this.items.clear();
      Vec3 eyePosition = mc.player.getEyePosition();
      double d = (f + 0.01) * (f + 0.01);

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof LivingEntity && entity.getBoundingBox().distanceToSqr(eyePosition) <= d) {
            this.items.add(entity);
         }
      }

      return this.items;
   }

   private double method5161(double doubleValue, float f, float f2, float f3) {
      float f4 = Math.signum(doubleValue) == Math.signum(f) ? f2 : f3;
      return doubleValue * f4;
   }

   @EventTarget
   private void method5162(MoveInputEvent event) {
      if (!this.entry2.isSelected() && this.livingEntity != null) {
         Rotation rotation = RotationUtils.method6912(this.livingEntity.getBoundingBox().getCenter().subtract(mc.player.getBoundingBox().getCenter()));
         MovementInputUtils.method7426(event, rotationManager.getEffectiveRotation().getFloatValue(), rotation.getFloatValue());
      }
   }
}
