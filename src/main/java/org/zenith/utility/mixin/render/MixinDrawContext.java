package org.zenith.utility.mixin.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;
import org.zenith.client.modules.impl.player.AHHelperModule;
import org.zenith.utility.game.ItemNbtUtils;

@Mixin({GuiGraphicsExtractor.class})
public abstract class MixinDrawContext {
   @Inject(
      method = {"nextStratum()V"},
      at = {@At("HEAD")}
   )
   private void zenith$flushMsdfTextBatch(CallbackInfo callbackInfo) {
      MsdfRenderer.flushBatch();
   }

   @Invoker("setTooltipForNextFrameInternal")
   public abstract void invokeDrawTooltip(
      Font font, List<ClientTooltipComponent> clientTooltipComponents, int n, int n2, ClientTooltipPositioner tooltipPositioner, @Nullable Identifier id, boolean flag
   );

   @ModifyVariable(
      method = {"itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private String zenith$showRealLotCount(@Nullable String str, Font font, ItemStack stack, int n, int n2) {
      if (str != null) {
         return str;
      } else if (!AHHelperModule.INSTANCE.isEnabled() || !AHHelperModule.INSTANCE.method4054()) {
         return null;
      } else if (stack != null && !stack.isEmpty() && this.zenith$isContainerScreenOpen()) {
         int n3 = ItemNbtUtils.method91(stack);
         return n3 > 1 && n3 != stack.getCount() ? String.valueOf(n3) : null;
      } else {
         return null;
      }
   }

   @Unique
   private boolean zenith$isContainerScreenOpen() {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft.gui.screen() instanceof AbstractContainerScreen;
   }

   @Inject(
      method = {"setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void filterShulkerTooltip(
      Font font, List<Component> components, Optional<TooltipComponent> optionalTooltipComponent, int n, int n2, @Nullable Identifier id, CallbackInfo callbackInfo
   ) {
      if (optionalTooltipComponent != null) {
         TooltipComponent tooltipComponent = (TooltipComponent)optionalTooltipComponent.orElse(null);
         if (tooltipComponent instanceof ShulkerPreviewModule.Entry) {
            ArrayList arrayList = new ArrayList(2);
            if (components != null && !components.isEmpty()) {
               arrayList.add(ClientTooltipComponent.create(((Component)components.get(0)).getVisualOrderText()));
            }

            ClientTooltipComponent clientTooltipComponent = ShulkerPreviewModule.method1388(tooltipComponent);
            if (clientTooltipComponent != null) {
               arrayList.add(clientTooltipComponent);
            }

            if (components != null && components.size() > 1) {
               Integer n3 = java.util.Optional.ofNullable(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).getColor()).map(net.minecraft.network.chat.TextColor::getValue).orElse(null);

               for (int i = 1; i < components.size(); i++) {
                  Component component = (Component)components.get(i);
                  if (n3 != null && component.getStyle().getColor() != null && component.getStyle().getColor().getValue() == n3) {
                     arrayList.add(ClientTooltipComponent.create(component.getVisualOrderText()));
                  }
               }
            }

            if (!arrayList.isEmpty()) {
               this.invokeDrawTooltip(font, arrayList, n, n2, DefaultTooltipPositioner.INSTANCE, id, false);
               callbackInfo.cancel();
            }
         }
      }
   }
}
