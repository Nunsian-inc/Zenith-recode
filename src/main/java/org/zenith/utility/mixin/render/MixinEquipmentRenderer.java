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
      LayerType var1,
      ResourceKey<EquipmentAsset> var2,
      Model var3,
      Object var4,
      ItemStack var5,
      PoseStack var6,
      SubmitNodeCollector var7,
      int var8,
      Identifier var9,
      int var10,
      int var11,
      CallbackInfo var12
   ) {
      this.renderStack = var5;
      this.renderSelf = var4 instanceof AvatarRenderState var13 && mc.player != null && var13.id == mc.player.getId();
   }

   @Inject(
      method = {"renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"},
      at = {@At("TAIL")}
   )
   private void onRenderTail(
      LayerType var1,
      ResourceKey<EquipmentAsset> var2,
      Model var3,
      Object var4,
      ItemStack var5,
      PoseStack var6,
      SubmitNodeCollector var7,
      int var8,
      Identifier var9,
      int var10,
      int var11,
      CallbackInfo var12
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
   private int onModifyColor(int var1) {
      if (mc.player == null || mc.level == null) {
         return var1;
      } else if (this.renderStack != null && !this.renderStack.isEmpty()) {
         ViewArmorDurabilityModule var2 = ViewArmorDurabilityModule.INSTANCE;
         if (!var2.isEnabled()) {
            return var1;
         } else {
            return !var2.method7550(this.renderStack, this.renderSelf) ? var1 : var2.method7551(this.renderStack);
         }
      } else {
         return var1;
      }
   }
}
