package org.zenith.utility.mixin.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.client.modules.impl.combat.PvpHelperModule;

@Mixin({ItemCooldowns.class})
public abstract class MixinItemCooldownManager {
   @ModifyVariable(
      method = {"addCooldown(Lnet/minecraft/resources/Identifier;I)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private int zenith$reduceCooldown(int var1, Identifier var2) {
      Minecraft var3 = Minecraft.getInstance();
      return var3.player != null && (Object)this == var3.player.getCooldowns() ? PvpHelperModule.INSTANCE.method4641(var2, var1) : var1;
   }
}
