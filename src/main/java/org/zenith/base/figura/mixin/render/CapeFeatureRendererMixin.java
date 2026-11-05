package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.ducks.PlayerModelCapeAccessor;
import org.zenith.base.figura.render.FiguraModelEdits;

@Mixin(CapeLayer.class)
public abstract class CapeFeatureRendererMixin {
   @Unique
   private static final String RENDER = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V";
   @Shadow
   @Final
   private HumanoidModel<AvatarRenderState> model;
   @Unique
   private final Deque<List<Avatar>> figura$capeFrames = new ArrayDeque<>();

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
      at = @At("HEAD")
   )
   private void figura$renderCape(
      PoseStack matrices, SubmitNodeCollector queue, int light, AvatarRenderState state, float limbAngle, float limbDistance, CallbackInfo ci
   ) {
      FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
      List<Avatar> avatars = List.copyOf(AvatarManager.getAvatars(extension.figura$getEntityUuid()));
      this.figura$capeFrames.push(avatars);
      Minecraft client = Minecraft.getInstance();
      if (!avatars.isEmpty() && !state.isInvisible && !state.chestEquipment.is(Items.ELYTRA) && client.level != null) {
         Entity entity = client.level.getEntity(extension.figura$getEntityId());
         if (entity == null && client.player != null && client.player.getUUID().equals(extension.figura$getEntityUuid())) {
            entity = client.player;
         }

         if (entity != null) {
            this.model.setupAnim(state);
            PlayerModelCapeAccessor accessor = (PlayerModelCapeAccessor)this.model;
            ModelPart cape = accessor.figura$getFakeCloak();
            ModelPart cloak = accessor.figura$getCloak();
            cape.loadPose(cloak.storePose());
            cape.xScale = cloak.xScale;
            cape.yScale = cloak.yScale;
            cape.zScale = cloak.zScale;
            float delta = extension.figura$getTickDelta();

            for (Avatar avatar : avatars) {
               if (avatar.luaRuntime != null) {
                  avatar.luaRuntime.vanilla_model.CAPE.save(this.model);
                  avatar.luaRuntime.vanilla_model.CAPE.preTransform(this.model);
               }

               avatar.capeRender(entity, queue, matrices, light, delta, cape);
            }

            for (Avatar avatar : avatars) {
               if (avatar.luaRuntime != null) {
                  avatar.luaRuntime.vanilla_model.CAPE.posTransform(this.model);
               }
            }
         }
      }
   }

   @WrapOperation(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;III)V"
      )
   )
   private void figura$submitCape(
      SubmitNodeCollector queue,
      Model<?> model,
      Object state,
      PoseStack matrices,
      RenderType layer,
      int light,
      int overlay,
      int outline,
      Operation<Void> original
   ) {
      List<Avatar> avatars = this.figura$capeFrames.peek();
      if (avatars != null && !avatars.isEmpty()) {
         FiguraModelEdits.submitWith(
            FiguraModelEdits.vanillaParts(avatars, avatar -> avatar.luaRuntime.vanilla_model.CAPE, this.model),
            () -> original.call(new Object[]{queue, model, state, matrices, layer, light, overlay, outline})
         );
      } else {
         original.call(new Object[]{queue, model, state, matrices, layer, light, overlay, outline});
      }
   }

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
      at = @At("RETURN")
   )
   private void figura$restoreCape(
      PoseStack matrices, SubmitNodeCollector queue, int light, AvatarRenderState state, float limbAngle, float limbDistance, CallbackInfo ci
   ) {
      List<Avatar> avatars = this.figura$capeFrames.poll();
      if (avatars != null) {
         for (int i = avatars.size() - 1; i >= 0; i--) {
            Avatar avatar = avatars.get(i);
            if (avatar.luaRuntime != null) {
               avatar.luaRuntime.vanilla_model.CAPE.restore(this.model);
            }
         }
      }
   }
}
