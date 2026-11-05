package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.render.FiguraModelEdits;
import org.zenith.base.figura.render.FiguraRenderFrames;

@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin {
   @Unique
   private final Deque<FiguraRenderFrames.Arm> figura$armFrames = new ArrayDeque<>();

   @WrapOperation(
      method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/UvMapping;)V"
      )
   )
   private void figura$submitArm(
      SubmitNodeCollector queue,
      ModelPart part,
      PoseStack matrices,
      RenderType layer,
      int light,
      int overlay,
      net.minecraft.client.renderer.texture.UvMapping sprite,
      Operation<Void> original
   ) {
      Minecraft client = Minecraft.getInstance();
      List<Avatar> avatars = client.player == null ? List.of() : List.copyOf(AvatarManager.getAvatars(client.player.getUUID()));
      PlayerModel model = this.figura$model();

      for (Avatar avatar : avatars) {
         if (avatar.luaRuntime != null) {
            avatar.luaRuntime.vanilla_model.PLAYER.save(model);
            avatar.luaRuntime.vanilla_model.PLAYER.preTransform(model);
            avatar.luaRuntime.vanilla_model.PLAYER.posTransform(model);
         }
      }

      this.figura$armFrames.push(new FiguraRenderFrames.Arm(avatars, part));
      if (avatars.isEmpty()) {
         original.call(new Object[]{queue, part, matrices, layer, light, overlay, sprite});
      } else {
         FiguraModelEdits.submitWith(
            FiguraModelEdits.vanillaParts(avatars, avatarx -> avatarx.luaRuntime.vanilla_model.PLAYER, model),
            () -> original.call(new Object[]{queue, part, matrices, layer, light, overlay, sprite})
         );
      }
   }

   @Inject(
      method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V",
      at = @At("RETURN")
   )
   private void figura$renderArm(
      PoseStack matrices, SubmitNodeCollector queue, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible, CallbackInfo ci
   ) {
      FiguraRenderFrames.Arm frame = this.figura$armFrames.poll();
      Minecraft client = Minecraft.getInstance();
      if (frame != null && client.player != null) {
         AvatarRenderer<?> renderer = (AvatarRenderer<?>)(Object)this;
         float delta = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);

         for (Avatar avatar : frame.avatars()) {
            avatar.renderMode = EntityRenderMode.FIRST_PERSON;
            avatar.firstPersonRender(matrices, queue, client.player, renderer, frame.arm(), light, delta);
         }

         PlayerModel model = this.figura$model();

         for (int i = frame.avatars().size() - 1; i >= 0; i--) {
            Avatar avatar = frame.avatars().get(i);
            if (avatar.luaRuntime != null) {
               avatar.luaRuntime.vanilla_model.PLAYER.restore(model);
            }
         }
      }
   }

   @Unique
   private PlayerModel figura$model() {
      return (PlayerModel)((AvatarRenderer)(Object)this).getModel();
   }
}
