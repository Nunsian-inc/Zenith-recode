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
   private int zenith$reduceCooldown(int n, Identifier key) {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft.player != null && (Object)this == minecraft.player.getCooldowns() ? PvpHelperModule.INSTANCE.method4641(key, n) : n;
   }
}
