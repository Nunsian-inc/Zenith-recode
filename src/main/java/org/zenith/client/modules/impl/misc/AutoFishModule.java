package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(name = "AutoFish", description = "module.autoFish.description", category = ModuleCategory.MISC)
public final class AutoFishModule extends Module {
   public static final AutoFishModule INSTANCE = new AutoFishModule();
   private final SliderSetting recastDelay = new SliderSetting("module.autoFish.recastDelay", 15.0F, 5.0F, 60.0F, 1.0F,
      "module.autoFish.recastDelay.desc", "ticks");
   private final SliderSetting delayRandomness = new SliderSetting("module.autoFish.delayRandomness", 5.0F, 0.0F, 10.0F, 1.0F,
      "module.autoFish.delayRandomness.desc", "ticks");
   private final BooleanSetting autoReplace = new BooleanSetting("module.autoFish.autoReplace", "module.autoFish.autoReplace.desc", true);
   private final BooleanSetting pauseWhenFull = new BooleanSetting("module.autoFish.pauseWhenFull", "module.autoFish.pauseWhenFull.desc", true);
   private final BooleanSetting onlyHoldingRod = new BooleanSetting("module.autoFish.onlyHoldingRod", "module.autoFish.onlyHoldingRod.desc", true);
   private ClientLevel fishingLevel;
   private LocalPlayer fishingPlayer;
   private FishingHook trackedHook;
   private FishingHook bittenHook;
   private FishingHook reeledHook;
   private InteractionHand fishingHand;
   private int fishingSlot = -1;
   private int recastTicks = -1;
   private int actionCooldown;
   private volatile int sessionId;

   private AutoFishModule() {
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.sessionId++;
      this.fishingLevel = null;
      this.fishingPlayer = null;
      this.trackedHook = null;
      this.bittenHook = null;
      this.reeledHook = null;
      this.fishingHand = null;
      this.fishingSlot = -1;
      this.recastTicks = -1;
      this.actionCooldown = 0;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.fishingPlayer != mc.player || this.fishingLevel != mc.level) {
         this.reset();
         this.fishingPlayer = mc.player;
         this.fishingLevel = mc.level;
      }
      if (this.isPaused()) {
         this.bittenHook = null;
         return;
      }
      if (this.actionCooldown > 0) {
         this.actionCooldown--;
      }

      FishingHook hook = mc.player.fishing;
      if (hook != null && hook.isRemoved()) {
         hook = null;
      }
      if (hook != this.trackedHook) {
         this.trackedHook = hook;
         this.reeledHook = null;
         if (this.bittenHook != hook) {
            this.bittenHook = null;
         }
         if (hook != null) {
            this.recastTicks = -1;
         } else {
            this.actionCooldown = 0;
            if (this.recastTicks < 0) {
               this.recastTicks = this.nextRecastDelay();
            }
         }
      }

      InteractionHand hand = this.findOrEquipRod();
      if (hand == null) {
         this.bittenHook = null;
         return;
      }
      if (hook != null) {
         if (this.bittenHook == hook && this.reeledHook != hook && this.actionCooldown == 0
            && !mc.player.getCooldowns().isOnCooldown(mc.player.getItemInHand(hand))) {
            this.bittenHook = null;
            this.reeledHook = hook;
            this.recastTicks = this.nextRecastDelay();
            this.actionCooldown = 20;
            UseItemUtils.useItem(hand);
         }
         // Если сервер отклонил подсечку, ждём новой поклёвки, а не дёргаем удочку каждый тик.
         if (this.actionCooldown == 0) {
            this.reeledHook = null;
         }
         return;
      }

      // Подсечка удаляет поплавок только после ответа сервера: до этого повторный заброс запрещён.
      if (this.recastTicks < 0) {
         this.recastTicks = this.nextRecastDelay();
      }
      if (this.recastTicks > 0) {
         this.recastTicks--;
      }
      if (this.recastTicks == 0 && this.actionCooldown == 0
         && !mc.player.getCooldowns().isOnCooldown(mc.player.getItemInHand(hand))) {
         UseItemUtils.useItem(hand);
         // Ждём появления поплавка до пяти секунд, чтобы высокий пинг не превратил заброс в подсечку.
         this.recastTicks = 100;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (!event.isIncoming() || event.isCancelled()) {
         return;
      }
      var packet = event.getPacket();
      var listener = event.getPacketListener();
      boolean splash = packet instanceof ClientboundSoundPacket sound
         && sound.getSound().value().equals(SoundEvents.FISHING_BOBBER_SPLASH)
         || packet instanceof ClientboundSoundEntityPacket soundEntity
         && soundEntity.getSound().value().equals(SoundEvents.FISHING_BOBBER_SPLASH);
      if (!splash) {
         return;
      }
      int receivedSession = this.sessionId;
      // PacketEvent приходит из сетевого потока; игрока и поплавок читаем только в клиентском.
      mc.execute(() -> {
         if (!this.isEnabled() || receivedSession != this.sessionId || listener != mc.getConnection()
            || this.fishingPlayer != mc.player || this.fishingLevel != mc.level || this.isPaused()) {
            return;
         }
         FishingHook hook = mc.player.fishing;
         if (hook == null || hook.isRemoved() || hook == this.reeledHook || this.actionCooldown > 0) {
            return;
         }
         boolean ownHook = packet instanceof ClientboundSoundPacket sound
            && hook.position().distanceToSqr(sound.getX(), sound.getY(), sound.getZ()) <= 1.0
            || packet instanceof ClientboundSoundEntityPacket soundEntity && soundEntity.getId() == hook.getId();
         if (ownHook) {
            this.bittenHook = hook;
         }
      });
   }

   private boolean isPaused() {
      return mc.player == null || mc.level == null || mc.gameMode == null || mc.player.isDeadOrDying() || mc.player.isSpectator()
         || mc.gui.screen() != null || mc.player.isUsingItem() || AuraModule.INSTANCE.isEnabled()
         || mc.player.containerMenu != mc.player.inventoryMenu || !mc.player.containerMenu.getCarried().isEmpty()
         || this.pauseWhenFull.isEnabled() && mc.player.getInventory().getFreeSlot() == -1;
   }

   private InteractionHand findOrEquipRod() {
      Inventory inventory = mc.player.getInventory();
      for (InteractionHand hand : InteractionHand.values()) {
         ItemStack stack = mc.player.getItemInHand(hand);
         if (this.isUsableRod(stack)) {
            this.rememberRod(hand);
            return hand;
         }
         if (stack.is(Items.FISHING_ROD)) {
            this.rememberRod(hand);
         }
      }

      // Пустая рука или исчерпанная прочность означают поломку; ручное переключение на другой предмет не отменяем.
      ItemStack currentHandStack = this.fishingHand == null ? ItemStack.EMPTY : mc.player.getItemInHand(this.fishingHand);
      boolean rodBroken = this.fishingHand != null && !this.isUsableRod(currentHandStack)
         && (currentHandStack.isEmpty() || currentHandStack.is(Items.FISHING_ROD))
         && (this.fishingHand == InteractionHand.OFF_HAND || inventory.getSelectedSlot() == this.fishingSlot);
      if (rodBroken && !this.autoReplace.isEnabled()) {
         return null;
      }
      if (!rodBroken && this.onlyHoldingRod.isEnabled()) {
         this.fishingHand = null;
         this.fishingSlot = -1;
         return null;
      }
      InteractionHand targetHand = rodBroken ? this.fishingHand : InteractionHand.MAIN_HAND;
      int hotbarSlot = InventoryUtils.method922(slot -> this.isUsableRod(inventory.getItem(slot)));
      if (hotbarSlot != -1 && targetHand == InteractionHand.MAIN_HAND) {
         inventory.setSelectedSlot(hotbarSlot);
      } else {
         Slot replacement = InventoryUtils.method910(slot -> slot.container == inventory && slot.getContainerSlot() >= 0
            && slot.getContainerSlot() < 36 && this.isUsableRod(slot.getItem()));
         if (replacement == null) {
            return null;
         }
         InventoryUtils.method900(replacement, targetHand, false);
      }
      if (!this.isUsableRod(mc.player.getItemInHand(targetHand))) {
         return null;
      }
      this.rememberRod(targetHand);
      return targetHand;
   }

   private void rememberRod(InteractionHand hand) {
      this.fishingHand = hand;
      this.fishingSlot = mc.player.getInventory().getSelectedSlot();
   }

   private boolean isUsableRod(ItemStack stack) {
      return stack.is(Items.FISHING_ROD) && (!stack.isDamageableItem() || stack.getDamageValue() < stack.getMaxDamage());
   }

   private int nextRecastDelay() {
      int variation = Math.round(this.delayRandomness.getValue3());
      return Math.max(1, Math.round(this.recastDelay.getValue3()) + ThreadLocalRandom.current().nextInt(-variation, variation + 1));
   }
}
