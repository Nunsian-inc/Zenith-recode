package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Zenith;
import org.zenith.base.font.HintedText;
import org.zenith.client.events.HandleKeybindsEvent;
import org.zenith.client.events.InteractionInputEvent;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.PacketProcessStartEvent;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.StopUsingItemEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.combat.CrystalAuraModule;
import org.zenith.client.modules.impl.misc.NoInteractModule;
import org.zenith.client.modules.impl.player.AutoMineModule;
import org.zenith.utility.misc.TimerSpeed;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.UniformStorage;

@Mixin({Minecraft.class})
public abstract class MixinMinecraftClient {
   @Shadow
   @Final
   private Window window;
   @Shadow
   @Nullable
   public LocalPlayer player;
   @Shadow
   @Nullable
   public ClientLevel level;
   @Shadow
   @Nullable
   public MultiPlayerGameMode gameMode;
   @Shadow
   @Final
   public GameRenderer gameRenderer;
   @Shadow
   public int rightClickDelay;
   @Shadow
   @Final
   public Options options;
   @Shadow
   @Final
   private Timer deltaTracker;
   @Shadow
   private volatile boolean pause;
   @Unique
   private final Timer zenith$renderTickCounter = new Timer(40.0F, 0L, this::getTickTargetMillis);

   @Shadow
   public abstract Window getWindow();

   @Shadow
   public abstract void startUseItem();

   @Shadow
   public abstract boolean isLevelRunningNormally();

   @Shadow
   public abstract float getTickTargetMillis(float f);

   @Inject(
      method = {"runTick(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Minecraft;runAllTasks()V",
         shift = Shift.BEFORE
      )}
   )
   private void hookPacketProcess(boolean flag, CallbackInfo callbackInfo) {
      EventManager.call(new PacketProcessEndEvent());
      int advanceGameTime = this.zenith$renderTickCounter.advanceGameTime(Util.getMillis());

      for (int i = 0; i < Math.min(10, advanceGameTime); i++) {
         EventManager.call(new PacketProcessStartEvent());
      }
   }

   @Inject(
      method = {"runTick(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/DeltaTracker$Timer;updateFrozenState(Z)V",
         shift = Shift.AFTER
      )}
   )
   private void hookTickRender(boolean flag, CallbackInfo callbackInfo) {
      this.zenith$renderTickCounter.updatePauseState(this.pause);
      this.zenith$renderTickCounter.updateFrozenState(!this.isLevelRunningNormally());
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void hookTickEvent(CallbackInfo callbackInfo) {
      EventManager.call(new TickEvent());
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"}
   )
   private void stop(CallbackInfo callbackInfo) {
      Zenith.getInstance().shutdown();
   }

   @Inject(
      method = {"handleKeybinds()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipInputWithoutPlayer(CallbackInfo callbackInfo) {
      if (this.player == null || this.gameMode == null) {
         callbackInfo.cancel();
      }
   }

   @Redirect(
      method = {"handleKeybinds()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",
         ordinal = 0
      )
   )
   public void injectHandleInputEventss(ClientPacketListener clientPacketListener, Packet packet) {
      InteractionInputEvent event = new InteractionInputEvent();
      EventManager.call(event);
      if (!event.isCancelled()) {
         clientPacketListener.send(packet);
      }
   }

   @Redirect(
      method = {"handleKeybinds()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V",
         ordinal = 0
      )
   )
   public void injectHandleInputEventsss(MultiPlayerGameMode multiPlayerGameMode, Player player) {
      StopUsingItemEvent event = new StopUsingItemEvent();
      EventManager.call(event);
      if (!event.isCancelled()) {
         multiPlayerGameMode.releaseUsingItem(player);
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/Gui;overlay()Lnet/minecraft/client/gui/screens/Overlay;",
         ordinal = 0
      )}
   )
   public void injectHandleInputEvents(CallbackInfo callbackInfo) {
      EventManager.call(new HandleInputPostEvent());
      InteractionInputEvent event = new InteractionInputEvent();
      EventManager.call(event);
      if (!event.isCancelled()) {
         HandleInputEvent event2 = new HandleInputEvent();
         EventManager.call(event2);
      }
   }

   @ModifyArg(
      method = {"handleKeybinds()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"
      )
   )
   private boolean zenith$autoMineHoldAttack(boolean flag) {
      return flag || AutoMineModule.INSTANCE.method4352();
   }

   @WrapWithCondition(
      method = {"continueAttack(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;stopDestroyBlock()V"
      )}
   )
   private boolean zenith$keepAuraDig(MultiPlayerGameMode multiPlayerGameMode) {
      return !CrystalAuraModule.INSTANCE.method5743();
   }

   @ModifyReturnValue(
      method = {"getTickTargetMillis(F)F"},
      at = {@At("RETURN")}
   )
   private float zenith$applyTimerSpeed(float f) {
      float floatValue = TimerSpeed.getSpeedMultiplier();
      return floatValue == 1.0F ? f : f / floatValue;
   }

   @Inject(
      method = {"startUseItem()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"
      )},
      cancellable = true
   )
   public void doItemUseHook(CallbackInfo callbackInfo) {
      if (this.player != null && this.gameMode != null) {
         InteractionInputEvent event = new InteractionInputEvent();
         EventManager.call(event);
         if (event.isCancelled()) {
            callbackInfo.cancel();
         } else {
            if (NoInteractModule.INSTANCE.isEnabled()) {
               for (InteractionHand hand : InteractionHand.values()) {
                  if (!this.player.getItemInHand(hand).isEmpty()) {
                     InteractionResult useItem = this.gameMode.useItem(this.player, hand);
                     if (useItem.consumesAction()) {
                        if (useItem instanceof Success success && success.swingSource().equals(SwingSource.PREDICTED)) {
                           org.zenith.utility.game.SwingCompat.swing(this.player, hand);
                        }

                        this.player.itemUsed(hand);
                        callbackInfo.cancel();
                        return;
                     }
                  }
               }
            }
         }
      } else {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"handleKeybinds()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getInventory()Lnet/minecraft/world/entity/player/Inventory;"
      )},
      cancellable = true
   )
   public void handleInputEventsHook(CallbackInfo callbackInfo) {
      HandleKeybindsEvent event = new HandleKeybindsEvent();
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"resizeGui()V"},
      at = {@At("TAIL")}
   )
   private void captureResize(CallbackInfo callbackInfo) {
      Zenith.getInstance().getKawaseBlur().method7367();
      ProjectionUtils.method300();
   }

   @Inject(
      method = {"renderFrame(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/renderpearl/api/device/GpuSurface;isAcquired()Z",
         ordinal = 1,
         shift = Shift.BEFORE
      )}
   )
   private void captureRessize(boolean flag, CallbackInfo callbackInfo) {
      ImmediateRenderer.method8009();
      UniformStorage.endFrame();
      HintedText.endFrame();
      Zenith.getInstance().getKawaseBlur().method7368();
   }

   // old GameRenderer.pick(F) hook: 26.3 moved crosshair picking to Minecraft
   @Inject(method = {"pick(F)V"}, at = {@At("RETURN")})
   private void zenith$crosshairTarget(float f, CallbackInfo callbackInfo) {
      EventManager.call(new org.zenith.client.events.CrosshairTargetEvent());
   }

   // setScreen moved to Gui: see MixinGuiScreenEvent
}
