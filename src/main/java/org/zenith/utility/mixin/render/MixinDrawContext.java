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
   private void zenith$flushMsdfTextBatch(CallbackInfo var1) {
      MsdfRenderer.flushBatch();
   }

   @Invoker("setTooltipForNextFrameInternal")
   public abstract void invokeDrawTooltip(
      Font var1, List<ClientTooltipComponent> var2, int var3, int var4, ClientTooltipPositioner var5, @Nullable Identifier var6, boolean var7
   );

   @ModifyVariable(
      method = {"itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private String zenith$showRealLotCount(@Nullable String var1, Font var2, ItemStack var3, int var4, int var5) {
      if (var1 != null) {
         return var1;
      } else if (!AHHelperModule.INSTANCE.isEnabled() || !AHHelperModule.INSTANCE.method4054()) {
         return null;
      } else if (var3 != null && !var3.isEmpty() && this.zenith$isContainerScreenOpen()) {
         int var6 = ItemNbtUtils.method91(var3);
         return var6 > 1 && var6 != var3.getCount() ? String.valueOf(var6) : null;
      } else {
         return null;
      }
   }

   @Unique
   private boolean zenith$isContainerScreenOpen() {
      Minecraft var1 = Minecraft.getInstance();
      return var1.gui.screen() instanceof AbstractContainerScreen;
   }

   @Inject(
      method = {"setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void filterShulkerTooltip(
      Font var1, List<Component> var2, Optional<TooltipComponent> var3, int var4, int var5, @Nullable Identifier var6, CallbackInfo var7
   ) {
      if (var3 != null) {
         TooltipComponent var8 = (TooltipComponent)var3.orElse(null);
         if (var8 instanceof ShulkerPreviewModule.Entry) {
            ArrayList var9 = new ArrayList(2);
            if (var2 != null && !var2.isEmpty()) {
               var9.add(ClientTooltipComponent.create(((Component)var2.get(0)).getVisualOrderText()));
            }

            ClientTooltipComponent var10 = ShulkerPreviewModule.method1388(var8);
            if (var10 != null) {
               var9.add(var10);
            }

            if (var2 != null && var2.size() > 1) {
               Integer var11 = java.util.Optional.ofNullable(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).getColor()).map(net.minecraft.network.chat.TextColor::getValue).orElse(null);

               for (int var12 = 1; var12 < var2.size(); var12++) {
                  Component var13 = (Component)var2.get(var12);
                  if (var11 != null && var13.getStyle().getColor() != null && var13.getStyle().getColor().getValue() == var11) {
                     var9.add(ClientTooltipComponent.create(var13.getVisualOrderText()));
                  }
               }
            }

            if (!var9.isEmpty()) {
               this.invokeDrawTooltip(var1, var9, var4, var5, DefaultTooltipPositioner.INSTANCE, var6, false);
               var7.cancel();
            }
         }
      }
   }
}
