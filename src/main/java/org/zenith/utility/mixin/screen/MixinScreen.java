package org.zenith.utility.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.screens.override.main.component.PauseMenuScope;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;
import org.zenith.utility.misc.ScreenCreationTimeAccess;

@Mixin({Screen.class})
public class MixinScreen implements ScreenCreationTimeAccess {
   @Unique
   private long startTime = System.currentTimeMillis();
   @Shadow
   @Nullable
   public Minecraft minecraft;

   @WrapMethod(method = "extractRenderStateWithTooltipAndSubtitles")
   private void zenith$pauseStyle(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
      boolean wasActive = VanillaOptionStyle.isActive();
      if (PauseMenuScope.isDescendant((Screen)(Object)this)) {
         VanillaOptionStyle.begin();
      }
      try {
         original.call(graphics, mouseX, mouseY, partialTick);
      } finally {
         // Восстанавливаем внешний scope даже при исключении или вложенном рендере.
         if (wasActive) {
            VanillaOptionStyle.begin();
         } else {
            VanillaOptionStyle.end();
         }
      }
   }

   @WrapOperation(
      method = "extractRenderStateWithTooltipAndSubtitles",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V")
   )
   private void zenith$pauseBackground(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
      if (PauseMenuScope.isDescendant(screen)) {
         // Пропускаем и переопределённые фоны, но сохраняем ванильные субтитры.
         minecraft.gui.hud.extractDeferredSubtitles();
      } else {
         original.call(screen, graphics, mouseX, mouseY, partialTick);
      }
   }

   @Unique
   public long getStartTime() {
      return this.startTime;
   }

   @Unique
   @Override
   public long zenithDLC$callGetStartTime() {
      return this.startTime;
   }
}
