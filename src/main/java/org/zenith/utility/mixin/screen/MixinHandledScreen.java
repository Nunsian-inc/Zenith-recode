package org.zenith.utility.mixin.screen;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.HandledScreenRenderEvent;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.modules.impl.misc.AutoInventoryModule;
import org.zenith.client.modules.impl.misc.ContainerHelperModule;
import org.zenith.client.modules.impl.player.AHHelperModule;
import org.zenith.client.modules.impl.render.BetterMinecraftModule;
import org.zenith.utility.game.ItemNbtUtils;
import org.zenith.utility.misc.Handler3;

@Mixin({AbstractContainerScreen.class})
public abstract class MixinHandledScreen extends Screen implements Handler3 {
   @Unique
   @Mutable
   private boolean isAuc;
   @Unique
   @Mutable
   private Slot lowSumSlotId = null;
   @Unique
   @Mutable
   private Slot lowAllSumSlotId = null;
   @Unique
   private int zenith$lastScanRevision = -1;
   @Unique
   private long zenith$openedAt = System.currentTimeMillis();
   @Unique
   private long zenith$closedAt;
   @Unique
   private float zenith$closeStartScale = 1.0F;
   @Unique
   private boolean zenith$closingAnimation;
   @Unique
   private boolean zenith$forceClose;
   @Unique
   private boolean zenith$scaleApplied;
   @Shadow
   @Final
   public AbstractContainerMenu menu;
   @Shadow
   public int topPos;
   @Shadow
   public int leftPos;
   @Shadow
   public int imageWidth;
   @Shadow
   public int imageHeight;
   @Shadow
   @Nullable
   public Slot hoveredSlot;

   public MixinHandledScreen(Component var1) {
      super(var1);
   }

   @Shadow
   public abstract AbstractContainerMenu getMenu();

   @Inject(
      method = {"init()V"},
      at = {@At("HEAD")}
   )
   private void zenith$initScaleAnimation(CallbackInfo var1) {
      this.zenith$openedAt = System.currentTimeMillis();
      this.zenith$closedAt = 0L;
      this.zenith$closeStartScale = 1.0F;
      this.zenith$closingAnimation = false;
      this.zenith$forceClose = false;
      this.zenith$scaleApplied = false;
   }

   @Inject(
      // 26.3: container backgrounds are drawn inside Screen.extractRenderState (super call in extractContents); the menu
      // blur is scaled with them during the open animation
      method = {"extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
      )}
   )
   private void zenith$applyBetterMinecraftScale(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.zenith$scaleApplied = false;
      if (this.zenith$shouldAnimateBetterMinecraft()) {
         float var6 = this.zenith$getScale();
         if (!(Math.abs(var6 - 1.0F) < 0.001F)) {
            var1.pose().pushMatrix();
            var1.pose().translate(this.width / 2.0F, this.height / 2.0F);
            var1.pose().scale(var6, var6);
            var1.pose().translate(-this.width / 2.0F, -this.height / 2.0F);
            this.zenith$scaleApplied = true;
         }
      }
   }

   @Inject(
      method = {"onClose()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$closeWithBetterMinecraftAnimation(CallbackInfo var1) {
      if (!this.zenith$forceClose && this.zenith$shouldAnimateBetterMinecraft()) {
         this.zenith$startClosingAnimation();
         var1.cancel();
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void tickScreen(CallbackInfo var1) {
      try {
         if (!AHHelperModule.INSTANCE.isEnabled()) {
            this.isAuc = false;
            this.lowSumSlotId = null;
            this.lowAllSumSlotId = null;
            this.zenith$lastScanRevision = -1;
            return;
         }

         int var2 = this.menu.getStateId();
         if (var2 == this.zenith$lastScanRevision) {
            return;
         }

         this.zenith$lastScanRevision = var2;
         this.isAuc = ItemNbtUtils.method81(this.menu);
         this.lowSumSlotId = null;
         this.lowAllSumSlotId = null;
         if (this.isAuc) {
            int var3 = Integer.MAX_VALUE;
            int var4 = Integer.MAX_VALUE;

            for (int var5 = 0; var5 < 44; var5++) {
               Slot var6 = this.getMenu().slots.get(var5);
               if (!var6.getItem().isEmpty() && !ItemNbtUtils.method87(var6.getItem())) {
                  int var7 = ItemNbtUtils.method62(var6.getItem());
                  if (var7 != Integer.MAX_VALUE) {
                     if (var7 < var3) {
                        this.lowSumSlotId = var6;
                        var3 = var7;
                     }

                     int var8 = ItemNbtUtils.method64(var6.getItem());
                     if (var8 != Integer.MAX_VALUE && var8 < var4) {
                        var4 = var8;
                        this.lowAllSumSlotId = var6;
                     }
                  }
               }
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }
   }

   @Inject(
      method = {"slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;onMouseClickAction(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ContainerInput;)V"
      )},
      cancellable = true
   )
   private void onClick(Slot var1, int var2, int var3, ContainerInput var4, CallbackInfo var5) {
      SlotClickEvent var6 = new SlotClickEvent(this.menu.containerId, var2, var3, var4);
      EventManager.call(var6);
      if (var6.isCancelled()) {
         var5.cancel();
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$renderAuctionHighlights(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.isAuc && AHHelperModule.INSTANCE.isEnabled()) {
         if (this.lowSumSlotId != null) {
            AHHelperModule.INSTANCE.method4044(var1, this.leftPos, this.topPos, this.lowSumSlotId, true);
         }

         if (this.lowAllSumSlotId != null && this.lowAllSumSlotId != this.lowSumSlotId) {
            AHHelperModule.INSTANCE.method4044(var1, this.leftPos, this.topPos, this.lowAllSumSlotId, false);
         }
      }
   }

   @Inject(
      method = {"init()V"},
      at = {@At("RETURN")}
   )
   public void injectInit(CallbackInfo var1) {
      try {
         Minecraft var2 = Minecraft.getInstance();
         if (AutoInventoryModule.INSTANCE.isEnabled()) {
            this.addRenderableWidget(
               Button.builder(Component.nullToEmpty("AutoSbor: " + AutoInventoryModule.INSTANCE.isEnabled()), var0 -> AutoInventoryModule.INSTANCE.toggle())
                  .bounds(this.width / 2 + 2, this.topPos, 98, 20)
                  .build()
            );
         }

         if (ContainerHelperModule.INSTANCE.isEnabled()) {
            if ((Object)this instanceof InventoryScreen) {
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Выкинуть все"), var2x -> {
                  for (int var3x = 0; var3x < var2.player.containerMenu.slots.size(); var3x++) {
                     ItemStack var4 = var2.player.containerMenu.getSlot(var3x).getItem();
                     if (!var4.isEmpty()) {
                        SlotClickEvent var5 = new SlotClickEvent(this.menu.containerId, var3x, var4.getCount(), ContainerInput.THROW);
                        EventManager.call(var5);
                        if (!var5.isCancelled()) {
                           var2.gameMode.handleContainerInput(this.menu.containerId, var3x, var4.getCount(), ContainerInput.THROW, var2.player);
                        }
                     }
                  }
               }).bounds(this.leftPos + 5, this.topPos - 25, this.imageWidth - 10, 20).build());
            } else if (ContainerScreen.class.isInstance(this) || ShulkerBoxScreen.class.isInstance(this)) {
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Выкинуть"), var2x -> {
                  for (int var3x = 0; var3x < this.menu.slots.size() - 36; var3x++) {
                     ItemStack var4 = this.menu.getSlot(var3x).getItem();
                     if (!var4.isEmpty()) {
                        var2.gameMode.handleContainerInput(this.menu.containerId, var3x, var4.getCount(), ContainerInput.THROW, var2.player);
                     }
                  }
               }).bounds(this.leftPos, this.topPos - 25, 56, 20).build());
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Сложить"), var2x -> {
                  for (int var3x = this.menu.slots.size() - 36; var3x < this.menu.slots.size(); var3x++) {
                     if (!this.menu.getSlot(var3x).getItem().isEmpty()) {
                        var2.gameMode.handleContainerInput(this.menu.containerId, var3x, 0, ContainerInput.QUICK_MOVE, var2.player);
                     }
                  }
               }).bounds(this.leftPos + 56 + 4, this.topPos - 25, 56, 20).build());
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Забрать"), var2x -> {
                  for (int var3x = 0; var3x < this.menu.slots.size() - 36; var3x++) {
                     if (!this.menu.getSlot(var3x).getItem().isEmpty()) {
                        var2.gameMode.handleContainerInput(this.menu.containerId, var3x, 0, ContainerInput.QUICK_MOVE, var2.player);
                     }
                  }
               }).bounds(this.leftPos + 56 + 56 + 4 + 4, this.topPos - 25, 56, 20).build());
            }
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   public void render(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      EventManager.call(new HandledScreenRenderEvent(var1, this.hoveredSlot, this.imageWidth, this.imageHeight));
      if (!this.zenith$isInventoryScreen()) {
         this.zenith$betterMinecraft$popScaleIfNeeded(var1);
         this.zenith$finishClosingAnimation();
      }
   }

   @Unique
   @Override
   public boolean zenith$betterMinecraft$isClosingAnimation() {
      return this.zenith$closingAnimation && this.zenith$shouldAnimateBetterMinecraft();
   }

   @Unique
   @Override
   public void zenith$betterMinecraft$popScaleIfNeeded(GuiGraphicsExtractor var1) {
      if (this.zenith$scaleApplied) {
         var1.pose().popMatrix();
         this.zenith$scaleApplied = false;
      }
   }

   @Unique
   @Override
   public void zenith$betterMinecraft$finishClosingAnimation() {
      this.zenith$finishClosingAnimation();
   }

   @Unique
   private boolean zenith$shouldAnimateBetterMinecraft() {
      return this.zenith$isInventoryScreen() ? BetterMinecraftModule.INSTANCE.method2011() : BetterMinecraftModule.INSTANCE.method2010();
   }

   @Unique
   private boolean zenith$isInventoryScreen() {
      return InventoryScreen.class.isInstance(this);
   }

   @Unique
   private float zenith$getScale() {
      return this.zenith$closingAnimation
         ? BetterMinecraftModule.INSTANCE.method2013(this.zenith$closedAt, this.zenith$closeStartScale)
         : BetterMinecraftModule.INSTANCE.method2012(this.zenith$openedAt);
   }

   @Unique
   private void zenith$startClosingAnimation() {
      if (!this.zenith$closingAnimation) {
         this.zenith$closeStartScale = this.zenith$getScale();
         this.zenith$closedAt = System.currentTimeMillis();
         this.zenith$closingAnimation = true;
      }
   }

   @Unique
   private void zenith$finishClosingAnimation() {
      if (this.zenith$closingAnimation && BetterMinecraftModule.INSTANCE.method2014(this.zenith$closedAt)) {
         this.zenith$forceClose = true;
         this.onClose();
         this.zenith$forceClose = false;
      }
   }
}
