package org.zenith.base.figura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.math.vector.FiguraVec3;

@Mixin(Entity.class)
public abstract class EntityMixin {
   @ModifyReturnValue(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
   private Vec3 figura$offsetEyePosition(Vec3 original) {
      Avatar avatar = AvatarManager.getAvatar((Entity)(Object)this);
      if (avatar != null && avatar.luaRuntime != null) {
         FiguraVec3 offset = avatar.luaRuntime.renderer.getEyeOffset();
         return offset != null && offset.notNaN() ? original.add(offset.asVec3()) : original;
      } else {
         return original;
      }
   }
}
