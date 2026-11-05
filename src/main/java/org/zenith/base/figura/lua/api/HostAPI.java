package org.zenith.base.figura.lua.api;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaError;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.lua.docs.LuaFieldDoc;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.mixin.LivingEntityAccessor;
import org.zenith.base.figura.model.rendering.texture.FiguraTexture;
import org.zenith.base.figura.runtime.RuntimeDefaults;
import org.zenith.base.figura.utils.ColorUtils;
import org.zenith.base.figura.utils.LuaUtils;
import org.zenith.base.figura.utils.TextUtils;

@LuaWhitelist
@LuaTypeDoc(name = "HostAPI", value = "host")
public class HostAPI {
   private final Avatar owner;
   private final boolean isHost;
   private final Minecraft minecraft;
   @LuaWhitelist
   @LuaFieldDoc("host.unlock_cursor")
   public boolean unlockCursor = false;
   public Integer chatColor;

   public HostAPI(Avatar owner) {
      this.owner = owner;
      this.minecraft = Minecraft.getInstance();
      this.isHost = owner.isHost;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_host")
   public boolean isHost() {
      return this.isHost;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_cursor_unlocked")
   public boolean isCursorUnlocked() {
      return this.unlockCursor;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = Boolean.class, argumentNames = "boolean"), value = "host.set_unlock_cursor")
   public HostAPI setUnlockCursor(boolean bool) {
      this.unlockCursor = bool;
      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = FiguraVec3.class, argumentNames = "timesData"),
            @LuaMethodOverload(argumentTypes = {Integer.class, Integer.class, Integer.class}, argumentNames = {"fadeInTime", "stayTime", "fadeOutTime"})
      },
      aliases = "titleTimes",
      value = "host.set_title_times"
   )
   public HostAPI setTitleTimes(Object x, Double y, Double z) {
      if (!this.isHost()) {
         return this;
      }

      FiguraVec3 times = LuaUtils.parseVec3("setTitleTimes", x, y, z);
      this.minecraft.gui.hud.setTimes((int)times.x, (int)times.y, (int)times.z);
      return this;
   }

   @LuaWhitelist
   public HostAPI titleTimes(Object x, Double y, Double z) {
      return this.setTitleTimes(x, y, z);
   }

   @LuaWhitelist
   @LuaMethodDoc("host.clear_title")
   public HostAPI clearTitle() {
      if (this.isHost()) {
         this.minecraft.gui.hud.clearTitles();
      }

      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), aliases = "title", value = "host.set_title")
   public HostAPI setTitle(@LuaNotNil String text) {
      if (this.isHost()) {
         this.minecraft.gui.hud.setTitle(TextUtils.tryParseJson(text));
      }

      return this;
   }

   @LuaWhitelist
   public HostAPI title(@LuaNotNil String text) {
      return this.setTitle(text);
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), aliases = "subtitle", value = "host.set_subtitle")
   public HostAPI setSubtitle(@LuaNotNil String text) {
      if (this.isHost()) {
         this.minecraft.gui.hud.setSubtitle(TextUtils.tryParseJson(text));
      }

      return this;
   }

   @LuaWhitelist
   public HostAPI subtitle(@LuaNotNil String text) {
      return this.setSubtitle(text);
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"),
            @LuaMethodOverload(argumentTypes = {String.class, boolean.class}, argumentNames = {"text", "animated"})
      },
      aliases = "actionbar",
      value = "host.set_actionbar"
   )
   public HostAPI setActionbar(@LuaNotNil String text, boolean animated) {
      if (this.isHost()) {
         this.minecraft.gui.hud.setOverlayMessage(TextUtils.tryParseJson(text), animated);
      }

      return this;
   }

   @LuaWhitelist
   public HostAPI actionbar(@LuaNotNil String text, boolean animated) {
      return this.setActionbar(text, animated);
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "message"), value = "host.send_chat_message")
   public HostAPI sendChatMessage(@LuaNotNil String message) {
      if (this.isHost() && RuntimeDefaults.CHAT_MESSAGES.value) {
         ClientPacketListener connection = this.minecraft.getConnection();
         if (connection != null) {
            connection.sendChat(message);
         }

         return this;
      } else {
         return this;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "command"), value = "host.send_chat_command")
   public HostAPI sendChatCommand(@LuaNotNil String command) {
      if (this.isHost() && RuntimeDefaults.CHAT_MESSAGES.value) {
         ClientPacketListener connection = this.minecraft.getConnection();
         if (connection != null) {
            connection.sendCommand(command.startsWith("/") ? command.substring(1) : command);
         }

         return this;
      } else {
         return this;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "message"), value = "host.append_chat_history")
   public HostAPI appendChatHistory(@LuaNotNil String message) {
      if (this.isHost()) {
         this.minecraft.gui.hud.getChat().addRecentChat(message);
      }

      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = Integer.class, argumentNames = "index"), value = "host.get_chat_message")
   public Map<String, Object> getChatMessage(int index) {
      return null;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.set_chat_message")
   public HostAPI setChatMessage(int index, String newMessage, FiguraVec3 backgroundColor) {
      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = {@LuaMethodOverload, @LuaMethodOverload(argumentTypes = Boolean.class, argumentNames = "offhand")}, value = "host.swing_arm")
   public HostAPI swingArm(boolean offhand) {
      if (this.isHost() && this.minecraft.player != null) {
         org.zenith.utility.game.SwingCompat.swing(this.minecraft.player, offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
      }

      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "slot"), @LuaMethodOverload(argumentTypes = Integer.class, argumentNames = "slot")
      },
      value = "host.get_slot"
   )
   public ItemStackAPI getSlot(@LuaNotNil Object slot) {
      if (!this.isHost()) {
         return null;
      } else {
         Entity e = this.owner.luaRuntime == null ? null : this.owner.luaRuntime.getUser();
         if (e != null && e.isAlive()) {
            SlotAccess slotAccess = e.getSlot(LuaUtils.parseSlot(slot, null));
            return ItemStackAPI.verify(slotAccess.get());
         } else {
            return ItemStackAPI.verify(ItemStack.EMPTY);
         }
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "slot"),
            @LuaMethodOverload(argumentTypes = Integer.class, argumentNames = "slot"),
            @LuaMethodOverload(argumentTypes = {String.class, String.class}, argumentNames = {"slot", "item"}),
            @LuaMethodOverload(argumentTypes = {Integer.class, ItemStackAPI.class}, argumentNames = {"slot", "item"})
      },
      value = "host.set_slot"
   )
   public HostAPI setSlot(@LuaNotNil Object slot, Object item) {
      if (this.isHost()
         && (slot != null || item != null)
         && this.minecraft.gameMode != null
         && this.minecraft.player != null
         && this.minecraft.gameMode.getPlayerMode().isCreative()) {
         Inventory inventory = this.minecraft.player.getInventory();
         int index = LuaUtils.parseSlot(slot, inventory);
         if (index >= 0 && index < inventory.getContainerSize()) {
            ItemStack stack = LuaUtils.parseItemStack("setSlot", item);
            inventory.setItem(index, stack);
            this.minecraft.gameMode.handleCreativeModeItemAdd(stack, index + 36);
            return this;
         } else {
            return this;
         }
      } else {
         return this;
      }
   }

   @LuaWhitelist
   public HostAPI setBadge(int index, boolean value, boolean pride) {
      return this;
   }

   @LuaWhitelist
   public HostAPI badge(int index, boolean value, boolean pride) {
      return this.setBadge(index, value, pride);
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_chat_color")
   public Integer getChatColor() {
      return this.isHost() ? this.chatColor : null;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = FiguraVec3.class, argumentNames = "color"),
            @LuaMethodOverload(argumentTypes = {Double.class, Double.class, Double.class}, argumentNames = {"r", "g", "b"})
      },
      aliases = "chatColor",
      value = "host.set_chat_color"
   )
   public HostAPI setChatColor(Object x, Double y, Double z) {
      if (this.isHost()) {
         this.chatColor = x == null ? null : ColorUtils.rgbToInt(LuaUtils.parseVec3("setChatColor", x, y, z));
      }

      return this;
   }

   @LuaWhitelist
   public HostAPI chatColor(Object x, Double y, Double z) {
      return this.setChatColor(x, y, z);
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_chat_text")
   public String getChatText() {
      return null;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), aliases = "chatText", value = "host.set_chat_text")
   public HostAPI setChatText(@LuaNotNil String text) {
      return this;
   }

   @LuaWhitelist
   public HostAPI chatText(@LuaNotNil String text) {
      return this.setChatText(text);
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_screen")
   public String getScreen() {
      return this.isHost() && this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getName() : null;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_screen_slot_count")
   public Integer getScreenSlotCount() {
      return this.isHost() && this.minecraft.gui.screen() instanceof AbstractContainerScreen<?> screen ? screen.getMenu().slots.size() : null;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "slot"), @LuaMethodOverload(argumentTypes = Integer.class, argumentNames = "slot")
      },
      value = "host.get_screen_slot"
   )
   public ItemStackAPI getScreenSlot(@LuaNotNil Object slot) {
      if (this.isHost() && this.minecraft.gui.screen() instanceof AbstractContainerScreen<?> screen) {
         NonNullList slots = screen.getMenu().slots;
         int index = LuaUtils.parseSlot(slot, null);
         return index >= 0 && index < slots.size() ? ItemStackAPI.verify(((Slot)slots.get(index)).getItem()) : null;
      } else {
         return null;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_chat_open")
   public boolean isChatOpen() {
      return this.isHost() && this.minecraft.gui.screen() instanceof ChatScreen;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_container_open")
   public boolean isContainerOpen() {
      return this.isHost() && this.minecraft.gui.screen() instanceof AbstractContainerScreen;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "name"), value = "host.screenshot")
   public FiguraTexture screenshot(@LuaNotNil String name) {
      if (this.isHost() && this.owner.luaRuntime != null && this.owner.luaRuntime.texture != null) {
         RenderTarget framebuffer = this.minecraft.gameRenderer.mainRenderTarget();
         NativeImage img = new NativeImage(framebuffer.width, framebuffer.height, false);
         FiguraTexture texture = this.owner.luaRuntime.texture.register(name, img, true);
         Screenshot.takeScreenshot(framebuffer, shot -> {
            img.copyFrom(shot);
            shot.close();
            texture.update();
         });
         return texture;
      } else {
         return null;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_avatar_uploaded")
   public boolean isAvatarUploaded() {
      return false;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_status_effects")
   public List<Map<String, Object>> getStatusEffects() {
      List<Map<String, Object>> list = new ArrayList<>();
      LocalPlayer player = this.minecraft.player;
      if (this.isHost() && player != null) {
         for (MobEffectInstance effect : player.getActiveEffects()) {
            Map<String, Object> map = new HashMap<>();
            Identifier id = BuiltInRegistries.MOB_EFFECT.getKey((MobEffect)effect.getEffect().value());
            map.put("name", id == null ? effect.getDescriptionId() : id.toString());
            map.put("amplifier", effect.getAmplifier());
            map.put("duration", effect.getDuration());
            map.put("visible", effect.isVisible());
            list.add(map);
         }

         return list;
      } else {
         return list;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_clipboard")
   public String getClipboard() {
      return this.isHost() ? this.minecraft.keyboardHandler.getClipboard() : null;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), aliases = "clipboard", value = "host.set_clipboard")
   public HostAPI setClipboard(@LuaNotNil String text) {
      if (this.isHost()) {
         this.minecraft.keyboardHandler.setClipboard(text);
      }

      return this;
   }

   @LuaWhitelist
   public HostAPI clipboard(@LuaNotNil String text) {
      return this.setClipboard(text);
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_attack_charge")
   public float getAttackCharge() {
      LocalPlayer player = this.minecraft.player;
      return this.isHost() && player != null ? player.getAttackStrengthScale(0.0F) : 0.0F;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_jumping")
   public boolean isJumping() {
      LocalPlayer player = this.minecraft.player;
      return this.isHost() && player != null ? ((LivingEntityAccessor)player).figura$isJumping() : false;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_flying")
   public boolean isFlying() {
      LocalPlayer player = this.minecraft.player;
      return this.isHost() && player != null ? player.getAbilities().flying : false;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_reach_distance")
   public double getReachDistance() {
      return this.minecraft.player == null ? 0.0 : this.minecraft.player.blockInteractionRange();
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_air")
   public int getAir() {
      LocalPlayer player = this.minecraft.player;
      return this.isHost() && player != null ? player.getAirSupply() : 0;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_pick_block")
   public Object[] getPickBlock() {
      return this.isHost() ? LuaUtils.parseBlockHitResult(this.minecraft.hitResult) : null;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.get_pick_entity")
   public EntityAPI<?> getPickEntity() {
      return this.isHost() && this.minecraft.crosshairPickEntity != null ? EntityAPI.wrap(this.minecraft.crosshairPickEntity) : null;
   }

   @LuaWhitelist
   @LuaMethodDoc("host.is_chat_verified")
   public boolean isChatVerified() {
      if (!this.isHost()) {
         return false;
      }

      ClientPacketListener connection = this.minecraft.getConnection();
      PlayerInfo playerInfo = connection != null ? connection.getPlayerInfo(this.owner.owner) : null;
      return playerInfo != null;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "string"), value = "host.write_to_log")
   public void writeToLog(@LuaNotNil String string) {
      if (this.isHost()) {
         FiguraMod.LOGGER.info("[FIGURA/LUA] -- {}", string);
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "string"), value = "host.warn_to_log")
   public void warnToLog(@LuaNotNil String string) {
      if (this.isHost()) {
         FiguraMod.LOGGER.warn("[FIGURA/LUA] -- {}", string);
      }
   }

   @LuaWhitelist
   public Object __index(String arg) {
      return "unlockCursor".equals(arg) ? this.unlockCursor : null;
   }

   @LuaWhitelist
   public void __newindex(@LuaNotNil String key, Object value) {
      if (!"unlockCursor".equals(key)) {
         throw new LuaError("Cannot assign value on key \"" + key + "\"");
      }

      this.unlockCursor = Boolean.TRUE.equals(value) || value instanceof Boolean b && b;
   }

   @Override
   public String toString() {
      return "HostAPI";
   }
}
