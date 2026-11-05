package org.zenith.utility.mixin.accessors;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({FirstPersonHandsAndItemsRenderer.class})
public interface HeldItemRendererAccessor {
   // 26.3 moved the equip-progress state to FirstPersonHandsAndItems; only the arm invoker is still used
   @Invoker("renderPlayerArm")
   void zenith$renderArmHoldingItem(PoseStack var1, SubmitNodeCollector var2, int var3, float var4, float var5, HumanoidArm var6, net.minecraft.client.renderer.state.level.PlayerRenderState var7);
}
