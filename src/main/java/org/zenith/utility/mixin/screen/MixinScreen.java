package org.zenith.utility.mixin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.Handler1;

@Mixin({Screen.class})
public class MixinScreen implements Handler1 {
   @Unique
   private long startTime = System.currentTimeMillis();
   @Shadow
   @Nullable
   public Minecraft minecraft;

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
