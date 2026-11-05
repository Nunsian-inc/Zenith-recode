package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "SwingAnimation",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.swinganimation.description"
)
public final class SwingAnimationModule extends Module {
   public static final SwingAnimationModule INSTANCE = new SwingAnimationModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 4;
   private static final int CONST_intValue6 = 5;
   private static final int CONST_intValue7 = 6;
   private static final int CONST_intValue8 = 7;
   private static final int CONST_intValue9 = 8;
   public ModeSetting animationMode = new ModeSetting(
      "module.swingAnimation.animationMode",
      "module.swingAnimation.animationMode.desc",
      "module.swingAnimation.normal",
      "module.swingAnimation.first",
      "module.swingAnimation.second",
      "module.swingAnimation.third",
      "module.swingAnimation.fourth",
      "module.swingAnimation.fifth",
      "module.swingAnimation.sixth",
      "module.swingAnimation.orbit",
      "module.swingAnimation.recoil"
   );
   public final SliderSetting duration = new SliderSetting("module.swingAnimation.duration", 2.0F, 1.0F, 5.0F, 0.5F, "module.swingAnimation.duration.desc", "x");
   public final BooleanSetting onlyAura = new BooleanSetting("module.swingAnimation.onlyAura", "module.swingAnimation.onlyAura.desc", false);

   private SwingAnimationModule() {
   }

   public boolean active() {
      return !this.isEnabled() ? false : !this.onlyAura.isEnabled() || AuraModule.INSTANCE.isEnabled() && AuraModule.INSTANCE.method6358() != null;
   }

   public int method3269(int n) {
      float f = this.duration.getValue3();
      if (!Float.isFinite(f)) {
         f = 2.0F;
      }

      return Math.max(1, (int)(n * Mth.clamp(f, 1.0F, 5.0F)));
   }

   public void method3270(PoseStack poseStack, float f, float f2, HumanoidArm humanoidArm) {
      int n = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      int n2 = Mth.clamp(this.animationMode.getIndex(), 0, 8);
      float x = Mth.clamp(f, 0.0F, 1.0F);
      float f3 = Mth.sin(Mth.sqrt(x) * (float) Math.PI);
      float f4 = Mth.sin(x * (float) Math.PI);
      if (n2 <= 6) {
         poseStack.translate(-n * 0.56F, 0.52F + f2 * 0.6F, 0.72F);
      }

      switch (n2) {
         case 1:
            if (x > 0.0F) {
               poseStack.translate(n * 0.56F, f2 * -0.2F - 0.5F, -0.7F);
               poseStack.rotate(Axis.YP.rotationDegrees(n * 45.0F));
               poseStack.rotate(Axis.XP.rotationDegrees(f3 * -85.0F));
               poseStack.translate(n * -0.1F, 0.28F, 0.2F);
               poseStack.rotate(Axis.XP.rotationDegrees(-85.0F));
            } else {
               method3271(poseStack, n, f2);
               method3272(poseStack, x, n);
            }
            break;
         case 2:
            method3271(poseStack, n, 0.0F);
            poseStack.rotate(Axis.XP.rotationDegrees(50.0F));
            poseStack.rotate(Axis.YP.rotationDegrees(n * -60.0F));
            poseStack.rotate(Axis.ZP.rotationDegrees(n * (110.0F + 20.0F * f3)));
            break;
         case 3:
            method3271(poseStack, n, 0.0F);
            poseStack.rotate(Axis.XP.rotationDegrees(50.0F));
            poseStack.rotate(Axis.YP.rotationDegrees(n * (-30.0F * (1.0F - f3) - 30.0F)));
            poseStack.rotate(Axis.ZP.rotationDegrees(n * 110.0F));
            break;
         case 4:
            method3271(poseStack, n, 0.0F);
            poseStack.translate(n * 0.1F, -0.2F, -0.3F);
            poseStack.rotate(Axis.XP.rotationDegrees(-30.0F * f4 - 36.0F));
            poseStack.rotate(Axis.YP.rotationDegrees(n * 25.0F * f4));
            poseStack.rotate(Axis.ZP.rotationDegrees(n * 12.0F));
            break;
         case 5:
            method3271(poseStack, n, 0.0F);
            poseStack.translate(0.0F, -0.2F, -0.4F);
            poseStack.rotate(Axis.XP.rotationDegrees(-120.0F * f3 - 3.0F));
            break;
         case 6:
            method3271(poseStack, n, f2);
            poseStack.translate(0.0F, 0.12F * f4, 0.0F);
            method3272(poseStack, x, n);
            break;
         case 7:
            float f5 = Mth.sin(x * (float) Math.PI * 2.0F) * f4;
            method3273(poseStack, n, 0.0F, 0.2F, -0.1F, -0.16F * f5, 0.1F * f4, -0.12F * f4, -35.0F * f4, 55.0F * f5, -45.0F * f5);
            break;
         case 8:
            float f6 = Mth.sin(x * (float) Math.PI * 3.0F) * f4 * (1.0F - x);
            method3273(poseStack, n, 0.0F, -0.1F, 0.05F, -0.05F * f3, 0.06F * f3, -0.22F * f3, -65.0F * f3 + 12.0F * f6, 10.0F * f6, 15.0F * f3);
            break;
         default:
            method3271(poseStack, n, 0.0F);
            method3272(poseStack, x, n);
      }
   }

   private static void method3271(PoseStack poseStack, int n, float f) {
      poseStack.translate(n * 0.56F, -0.52F - f * 0.6F, -0.72F);
   }

   private static void method3272(PoseStack poseStack, float x, int n) {
      float f = Mth.sin(x * x * (float) Math.PI);
      float f2 = Mth.sin(Mth.sqrt(x) * (float) Math.PI);
      poseStack.rotate(Axis.YP.rotationDegrees(n * (45.0F - f * 20.0F)));
      poseStack.rotate(Axis.ZP.rotationDegrees(n * f2 * -20.0F));
      poseStack.rotate(Axis.XP.rotationDegrees(f2 * -80.0F));
      poseStack.rotate(Axis.YP.rotationDegrees(n * -45.0F));
   }

   private static void method3273(
      PoseStack poseStack, int n, float f, float yo, float zo, float f2, float yo2, float zo2, float f3, float f4, float f5
   ) {
      poseStack.translate(n * f, yo, zo);
      poseStack.translate(n * f2, yo2, zo2);
      poseStack.rotate(
         new Quaternionf().rotationXYZ(f3 * (float) (Math.PI / 180.0), n * f4 * (float) (Math.PI / 180.0), n * f5 * (float) (Math.PI / 180.0))
      );
      poseStack.translate(-n * f, -yo, -zo);
   }
}
