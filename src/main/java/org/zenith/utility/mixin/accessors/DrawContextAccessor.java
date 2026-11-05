package org.zenith.utility.mixin.accessors;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GuiGraphicsExtractor.class})
public interface DrawContextAccessor {
   @Accessor("guiRenderState")
   GuiRenderState zenith$state();

   @Accessor("mouseX")
   int zenith$mouseX();

   @Accessor("mouseY")
   int zenith$mouseY();

   @Invoker("itemBar")
   void callDrawItemBar(ItemStack itemStack, int intValue, int n);

   @Invoker("itemCooldown")
   void callDrawCooldownProgress(ItemStack itemStack, int intValue, int n);
}
