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

   public MixinHandledScreen(Component title) {
      super(title);
   }

   @Shadow
   public abstract AbstractContainerMenu getMenu();

   @Inject(
      method = {"init()V"},
      at = {@At("HEAD")}
   )
   private void zenith$initScaleAnimation(CallbackInfo callbackInfo) {
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
   private void zenith$applyBetterMinecraftScale(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f, CallbackInfo callbackInfo) {
      this.zenith$scaleApplied = false;
      if (this.zenith$shouldAnimateBetterMinecraft()) {
         float zenith$getScale = this.zenith$getScale();
         if (!(Math.abs(zenith$getScale - 1.0F) < 0.001F)) {
            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate(this.width / 2.0F, this.height / 2.0F);
            guiGraphicsExtractor.pose().scale(zenith$getScale, zenith$getScale);
            guiGraphicsExtractor.pose().translate(-this.width / 2.0F, -this.height / 2.0F);
            this.zenith$scaleApplied = true;
         }
      }
   }

   @Inject(
      method = {"onClose()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$closeWithBetterMinecraftAnimation(CallbackInfo callbackInfo) {
      if (!this.zenith$forceClose && this.zenith$shouldAnimateBetterMinecraft()) {
         this.zenith$startClosingAnimation();
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void tickScreen(CallbackInfo callbackInfo) {
      try {
         if (!AHHelperModule.INSTANCE.isEnabled()) {
            this.isAuc = false;
            this.lowSumSlotId = null;
            this.lowAllSumSlotId = null;
            this.zenith$lastScanRevision = -1;
            return;
         }

         int stateId = this.menu.getStateId();
         if (stateId == this.zenith$lastScanRevision) {
            return;
         }

         this.zenith$lastScanRevision = stateId;
         this.isAuc = ItemNbtUtils.method81(this.menu);
         this.lowSumSlotId = null;
         this.lowAllSumSlotId = null;
         if (this.isAuc) {
            int n = Integer.MAX_VALUE;
            int n2 = Integer.MAX_VALUE;

            for (int i = 0; i < 44; i++) {
               Slot slot = this.getMenu().slots.get(i);
               if (!slot.getItem().isEmpty() && !ItemNbtUtils.method87(slot.getItem())) {
                  int n3 = ItemNbtUtils.method62(slot.getItem());
                  if (n3 != Integer.MAX_VALUE) {
                     if (n3 < n) {
                        this.lowSumSlotId = slot;
                        n = n3;
                     }

                     int n4 = ItemNbtUtils.method64(slot.getItem());
                     if (n4 != Integer.MAX_VALUE && n4 < n2) {
                        n2 = n4;
                        this.lowAllSumSlotId = slot;
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
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
   private void onClick(Slot slot, int index, int count, ContainerInput slotActionType, CallbackInfo callbackInfo) {
      SlotClickEvent event = new SlotClickEvent(this.menu.containerId, index, count, slotActionType);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$renderAuctionHighlights(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f, CallbackInfo callbackInfo) {
      if (this.isAuc && AHHelperModule.INSTANCE.isEnabled()) {
         if (this.lowSumSlotId != null) {
            AHHelperModule.INSTANCE.method4044(guiGraphicsExtractor, this.leftPos, this.topPos, this.lowSumSlotId, true);
         }

         if (this.lowAllSumSlotId != null && this.lowAllSumSlotId != this.lowSumSlotId) {
            AHHelperModule.INSTANCE.method4044(guiGraphicsExtractor, this.leftPos, this.topPos, this.lowAllSumSlotId, false);
         }
      }
   }

   @Inject(
      method = {"init()V"},
      at = {@At("RETURN")}
   )
   public void injectInit(CallbackInfo callbackInfo) {
      try {
         Minecraft minecraft = Minecraft.getInstance();
         if (AutoInventoryModule.INSTANCE.isEnabled()) {
            this.addRenderableWidget(
               Button.builder(Component.nullToEmpty("AutoSbor: " + AutoInventoryModule.INSTANCE.isEnabled()), button -> AutoInventoryModule.INSTANCE.toggle())
                  .bounds(this.width / 2 + 2, this.topPos, 98, 20)
                  .build()
            );
         }

         if (ContainerHelperModule.INSTANCE.isEnabled()) {
            if ((Object)this instanceof InventoryScreen) {
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Выкинуть все"), var2x -> {
                  for (int var3x = 0; var3x < minecraft.player.containerMenu.slots.size(); var3x++) {
                     ItemStack stack = minecraft.player.containerMenu.getSlot(var3x).getItem();
                     if (!stack.isEmpty()) {
                        SlotClickEvent event = new SlotClickEvent(this.menu.containerId, var3x, stack.getCount(), ContainerInput.THROW);
                        EventManager.call(event);
                        if (!event.isCancelled()) {
                           minecraft.gameMode.handleContainerInput(this.menu.containerId, var3x, stack.getCount(), ContainerInput.THROW, minecraft.player);
                        }
                     }
                  }
               }).bounds(this.leftPos + 5, this.topPos - 25, this.imageWidth - 10, 20).build());
            } else if (ContainerScreen.class.isInstance(this) || ShulkerBoxScreen.class.isInstance(this)) {
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Выкинуть"), var2x -> {
                  for (int var3x = 0; var3x < this.menu.slots.size() - 36; var3x++) {
                     ItemStack stack = this.menu.getSlot(var3x).getItem();
                     if (!stack.isEmpty()) {
                        minecraft.gameMode.handleContainerInput(this.menu.containerId, var3x, stack.getCount(), ContainerInput.THROW, minecraft.player);
                     }
                  }
               }).bounds(this.leftPos, this.topPos - 25, 56, 20).build());
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Сложить"), var2x -> {
                  for (int var3x = this.menu.slots.size() - 36; var3x < this.menu.slots.size(); var3x++) {
                     if (!this.menu.getSlot(var3x).getItem().isEmpty()) {
                        minecraft.gameMode.handleContainerInput(this.menu.containerId, var3x, 0, ContainerInput.QUICK_MOVE, minecraft.player);
                     }
                  }
               }).bounds(this.leftPos + 56 + 4, this.topPos - 25, 56, 20).build());
               this.addRenderableWidget(Button.builder(Component.nullToEmpty("Забрать"), var2x -> {
                  for (int var3x = 0; var3x < this.menu.slots.size() - 36; var3x++) {
                     if (!this.menu.getSlot(var3x).getItem().isEmpty()) {
                        minecraft.gameMode.handleContainerInput(this.menu.containerId, var3x, 0, ContainerInput.QUICK_MOVE, minecraft.player);
                     }
                  }
               }).bounds(this.leftPos + 56 + 56 + 4 + 4, this.topPos - 25, 56, 20).build());
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   public void render(GuiGraphicsExtractor drawContext, int n, int n2, float f, CallbackInfo callbackInfo) {
      EventManager.call(new HandledScreenRenderEvent(drawContext, this.hoveredSlot, this.imageWidth, this.imageHeight));
      if (!this.zenith$isInventoryScreen()) {
         this.zenith$betterMinecraft$popScaleIfNeeded(drawContext);
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
   public void zenith$betterMinecraft$popScaleIfNeeded(GuiGraphicsExtractor drawContext) {
      if (this.zenith$scaleApplied) {
         drawContext.pose().popMatrix();
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
