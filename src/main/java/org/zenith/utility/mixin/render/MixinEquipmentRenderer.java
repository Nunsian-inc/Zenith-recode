package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.render.ViewArmorDurabilityModule;

@Mixin({EquipmentLayerRenderer.class})
public class MixinEquipmentRenderer implements MinecraftAccess {
   @Unique
   private ItemStack renderStack = ItemStack.EMPTY;
   @Unique
   private boolean renderSelf;

   @Inject(
      method = {"renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"},
      at = {@At("HEAD")}
   )
   private void onRenderHead(
      LayerType layerType,
      ResourceKey<EquipmentAsset> resourceKey,
      Model model,
      Object obj,
      ItemStack renderStack,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      Identifier id,
      int n2,
      int n3,
      CallbackInfo callbackInfo
   ) {
      this.renderStack = renderStack;
      this.renderSelf = obj instanceof AvatarRenderState avatarRenderState && mc.player != null && avatarRenderState.id == mc.player.getId();
   }

   @Inject(
      method = {"renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"},
      at = {@At("TAIL")}
   )
   private void onRenderTail(
      LayerType layerType,
      ResourceKey<EquipmentAsset> resourceKey,
      Model model,
      Object obj,
      ItemStack stack,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      Identifier id,
      int n2,
      int n3,
      CallbackInfo callbackInfo
   ) {
      this.renderStack = ItemStack.EMPTY;
      this.renderSelf = false;
   }

   @ModifyArg(
      method = {"renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/UvMapping;I)V"
      ),
      index = 6
   )
   private int onModifyColor(int n) {
      if (mc.player == null || mc.level == null) {
         return n;
      } else if (this.renderStack != null && !this.renderStack.isEmpty()) {
         ViewArmorDurabilityModule armorDurabilityModule = ViewArmorDurabilityModule.INSTANCE;
         if (!armorDurabilityModule.isEnabled()) {
            return n;
         } else {
            return !armorDurabilityModule.method7550(this.renderStack, this.renderSelf) ? n : armorDurabilityModule.method7551(this.renderStack);
         }
      } else {
         return n;
      }
   }
}
