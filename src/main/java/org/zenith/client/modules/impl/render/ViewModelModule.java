package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "ViewModel",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.viewmodel.description"
)
public final class ViewModelModule extends Module {
   public static final ViewModelModule INSTANCE = new ViewModelModule();
   public final GroupSetting rightArmZ = new GroupSetting(
      "module.viewModel.rightArm",
      "module.viewModel.rightArm.desc",
      () -> true,
      new SliderSetting("module.viewModel.rightArmX", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.rightArmX.desc", "b"),
      new SliderSetting("module.viewModel.rightArmY", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.rightArmY.desc", "b"),
      new SliderSetting("module.viewModel.rightArmZ", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.rightArmZ.desc", "b")
   );
   public final GroupSetting leftArmZ = new GroupSetting(
      "module.viewModel.leftArm",
      "module.viewModel.leftArm.desc",
      () -> true,
      new SliderSetting("module.viewModel.leftArmX", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.leftArmX.desc", "b"),
      new SliderSetting("module.viewModel.leftArmY", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.leftArmY.desc", "b"),
      new SliderSetting("module.viewModel.leftArmZ", 0.0F, -1.0F, 1.0F, 0.1F, "module.viewModel.leftArmZ.desc", "b")
   );
   public final GroupSetting leftArmSize = new GroupSetting(
      "module.viewModel.sizeList",
      "module.viewModel.sizeList.desc",
      () -> true,
      new SliderSetting("module.viewModel.rightArmSize", 1.0F, 0.5F, 1.5F, 0.05F, "module.viewModel.rightArmSize.desc", "x"),
      new SliderSetting("module.viewModel.leftArmSize", 1.0F, 0.5F, 1.5F, 0.05F, "module.viewModel.leftArmSize.desc", "x")
   );
   public final ButtonSetting reset = new ButtonSetting("module.viewModel.reset", "W", () -> {
      SliderSetting sliderSetting = this.leftArmSize.method2786(0);
      SliderSetting sliderSetting2 = this.leftArmSize.method2786(1);
      SliderSetting sliderSetting3 = this.rightArmZ.method2786(0);
      SliderSetting sliderSetting4 = this.rightArmZ.method2786(1);
      SliderSetting sliderSetting5 = this.rightArmZ.method2786(2);
      SliderSetting sliderSetting6 = this.leftArmZ.method2786(0);
      SliderSetting sliderSetting7 = this.leftArmZ.method2786(1);
      SliderSetting sliderSetting8 = this.leftArmZ.method2786(2);
      sliderSetting.method2400(1.0F);
      sliderSetting2.method2400(1.0F);
      sliderSetting3.method2400(0.0F);
      sliderSetting4.method2400(0.0F);
      sliderSetting5.method2400(0.0F);
      sliderSetting6.method2400(0.0F);
      sliderSetting7.method2400(0.0F);
      sliderSetting8.method2400(0.0F);
   });

   private ViewModelModule() {
   }

   public void method235(PoseStack poseStack, HumanoidArm mainArm) {
      if (this.isEnabled()) {
         if (mainArm == HumanoidArm.RIGHT) {
            SliderSetting sliderSetting = this.leftArmSize.method2786(0);
            poseStack.scale(sliderSetting.getValue3(), sliderSetting.getValue3(), sliderSetting.getValue3());
         } else {
            SliderSetting sliderSetting = this.leftArmSize.method2786(1);
            poseStack.scale(sliderSetting.getValue3(), sliderSetting.getValue3(), sliderSetting.getValue3());
         }
      } else {
         poseStack.scale(1.0F, 1.0F, 1.0F);
      }
   }

   public void method236(PoseStack poseStack, HumanoidArm mainArm) {
      if (this.isEnabled()) {
         if (mainArm == HumanoidArm.RIGHT) {
            SliderSetting sliderSetting = this.rightArmZ.method2786(0);
            SliderSetting sliderSetting2 = this.rightArmZ.method2786(1);
            SliderSetting sliderSetting3 = this.rightArmZ.method2786(2);
            poseStack.translate(sliderSetting.getValue3(), sliderSetting2.getValue3(), sliderSetting3.getValue3());
         } else {
            SliderSetting sliderSetting = this.leftArmZ.method2786(0);
            SliderSetting sliderSetting2 = this.leftArmZ.method2786(1);
            SliderSetting sliderSetting3 = this.leftArmZ.method2786(2);
            poseStack.translate(-sliderSetting.getValue3(), sliderSetting2.getValue3(), sliderSetting3.getValue3());
         }
      } else {
         poseStack.translate(0.0F, 0.0F, 0.0F);
      }
   }
}
