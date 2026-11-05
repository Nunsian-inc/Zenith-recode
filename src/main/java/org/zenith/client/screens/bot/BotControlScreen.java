package org.zenith.client.screens.bot;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.ChatMessage;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.view.BotPlayerGuiRender;
import org.zenith.base.bot.view.BotRemoteControl;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.font.Fonts;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class BotControlScreen extends CustomScreen {
   private static final Identifier HOTBAR_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar");
   private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar_selection");
   private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");
   private static final Identifier HEART_CONTAINER_TEXTURE = Identifier.withDefaultNamespace("hud/heart/container");
   private static final Identifier HEART_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/heart/full");
   private static final Identifier HEART_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/heart/half");
   private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/food_empty");
   private static final Identifier FOOD_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/food_full");
   private static final Identifier FOOD_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/food_half");
   private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/armor_empty");
   private static final Identifier ARMOR_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/armor_full");
   private static final Identifier ARMOR_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/armor_half");
   private static final Identifier AIR_TEXTURE = Identifier.withDefaultNamespace("hud/air");
   private static final Identifier AIR_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/air_empty");
   private static final Identifier XP_BAR_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("hud/experience_bar_background");
   private static final Identifier XP_BAR_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("hud/experience_bar_progress");
   private static final Identifier INVENTORY_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");
   private static final Identifier GENERIC_54_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
   private static final Identifier SHULKER_BOX_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/shulker_box.png");
   private static final Identifier HOPPER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/hopper.png");
   private static final Identifier DISPENSER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");
   private static final Identifier FURNACE_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/furnace.png");
   private static final Identifier BLAST_FURNACE_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/blast_furnace.png");
   private static final Identifier SMOKER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/smoker.png");
   private static final Identifier CRAFTING_TABLE_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");
   private static final Identifier SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.withDefaultNamespace("container/slot_highlight_back");
   private static final Identifier SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.withDefaultNamespace("container/slot_highlight_front");
   private final String botName;
   private BotClient boundClient;
   private BotRemoteControl control;
   private BotWorldView view;
   private boolean inventoryOpen;
   private boolean cursorLocked;
   private double lastMouseX = Double.NaN;
   private double lastMouseY = Double.NaN;
   @Nullable
   private Slot hoveredSlotCache;
   private int wheelSlot = -1;
   private static final int CHAT_WIDTH = 320;
   private static final int CHAT_LINE_HEIGHT = 9;
   private static final long CHAT_VISIBLE_MS = 10000L;
   private final SearchBox chatField = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "", 0.0F);
   private final BotChatInputSuggestor chatSuggestor;
   private String originalChatText = "";
   private int chatFirstChar;
   private int messageHistoryIndex;
   private String chatLastMessage = "";
   private boolean chatOpen;
   private boolean suppressNextChar;
   private int chatScrollLines;
   private int chatTotalLines;
   private final Map<ChatMessage, BotControlScreen.CachedChat> chatCache = new IdentityHashMap<>();
   private boolean chatSeeded;
   private boolean keyForward;
   private boolean keyBack;
   private boolean keyLeft;
   private boolean keyRight;
   private boolean keyJump;
   private boolean keySneak;
   private boolean keySprint;
   private BotClient sidebarClient;
   private Component sidebarTitle;
   private List<BotControlScreen.SidebarRow> sidebarRows = List.of();

   public String getBotName() {
      return this.botName;
   }

   public BotControlScreen(String botName) {
      this.botName = botName;
      this.chatSuggestor = new BotChatInputSuggestor(
         this,
         this.chatField,
         mc.font,
         () -> this.boundClient != null ? this.boundClient.getPlayHandler() : null,
         this::chatCharacterX,
         () -> this.height - 16
      );
   }

   private BotClient ensureBound() {
      BotClient boundClient = HeadlessBots.get(this.botName);
      if (boundClient != this.boundClient) {
         this.unbind();
         this.boundClient = boundClient;
         if (boundClient != null) {
            this.control = new BotRemoteControl(boundClient);
            this.control.attach();
            this.view = new BotWorldView(boundClient);
         }
      }

      return boundClient;
   }

   private void unbind() {
      if (this.control != null) {
         this.control.detach();
         this.control = null;
      }

      if (this.view != null) {
         this.view.close();
         this.view = null;
      }

      this.boundClient = null;
      this.closeChat();
      this.chatCache.clear();
      this.chatSeeded = false;
   }

   @Override
   public void removed() {
      this.setCursorLocked(false);
      this.unbind();
      super.removed();
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float xo, float yo) {
      BotClient ensureBound = this.ensureBound();
      BotPlayer player = ensureBound != null ? ensureBound.getPlayer() : null;
      boolean flag = ensureBound != null && ensureBound.isJoined() && player != null;
      AbstractContainerMenu currentUiHandler = flag ? this.currentUiHandler(ensureBound, player) : null;
      boolean flag2 = currentUiHandler != null;
      boolean flag3 = mc.gui.screen() != this;
      if (this.chatOpen && (!flag || flag2)) {
         this.closeChat();
      }

      if (this.control != null) {
         this.control.setUiScreenOpen(flag2 || this.chatOpen || flag3);
      }

      this.setCursorLocked(!flag3 && flag && !flag2 && !this.chatOpen);
      menuDrawContext.drawRoundedRect(0.0F, 0.0F, this.width, this.height, ProxyDotRadiusData.INSTANCE, new ZenithColor(10, 10, 14, 255));
      if (!flag) {
         String str = t("gui.bot.notInGame");
         menuDrawContext.text(mc.font, str, (this.width - mc.font.width(str)) / 2, this.height / 2 - 4, -1, true);
         String str2 = t("gui.bot.escBack");
         menuDrawContext.text(mc.font, str2, (this.width - mc.font.width(str2)) / 2, this.height / 2 + 8, -6643542, true);
      } else {
         int width = mc.getWindow().getWidth();
         int height = mc.getWindow().getHeight();
         boolean flag4 = this.view != null && this.view.renderToFbo(width, height);
         if (flag4) {
            RenderShaders.method5298(
               MatrixUtils.method664(menuDrawContext.pose()),
               this.view.getColorAttachment(),
               0.0F,
               0.0F,
               this.width,
               this.height,
               ProxyDotRadiusData.INSTANCE,
               ZenithColor.INSTANCE,
               0.0F,
               1.0F,
               1.0F,
               0.0F
            );
         } else {
            String str = t("gui.bot.loadingWorld");
            menuDrawContext.text(mc.font, str, (this.width - mc.font.width(str)) / 2, this.height / 2 - 4, -1, true);
         }

         this.renderHud(menuDrawContext, ensureBound, player);
         this.renderScoreboardSidebar(menuDrawContext, ensureBound);
         ScissorFlusher.flush();
         this.renderChat(menuDrawContext, ensureBound);
         ScissorFlusher.flush();
         if (flag2) {
            this.renderContainer(menuDrawContext, player, currentUiHandler, (int)xo, (int)yo);
            ScissorFlusher.flush();
         } else {
            this.hoveredSlotCache = null;
            this.renderCrosshair(menuDrawContext);
         }
      }
   }

   private AbstractContainerMenu currentUiHandler(BotClient boundClient, BotPlayer player) {
      BotPlayHandler playHandler = boundClient.getPlayHandler();
      if (playHandler != null && playHandler.hasOpenScreen()) {
         this.inventoryOpen = false;
         return player.containerMenu;
      } else {
         return this.inventoryOpen ? player.inventoryMenu : null;
      }
   }

   private void renderCrosshair(MenuDrawContext menuDrawContext) {
      int x0 = this.width / 2;
      int y0 = this.height / 2;
      int col = -855638017;
      menuDrawContext.fill(x0 - 5, y0, x0 + 4, y0 + 1, col);
      menuDrawContext.fill(x0, y0 - 5, x0 + 1, y0 + 4, col);
   }

   private void renderChat(MenuDrawContext menuDrawContext, BotClient ensureBound) {
      List<ChatMessage> chatLog = ensureBound.getChatLog();
      long now = System.currentTimeMillis();
      this.syncChatCache(chatLog, now);
      var arrayList = new ArrayList<>(chatLog.size());
      this.chatTotalLines = 0;

      for (ChatMessage chatMessage : chatLog) {
         BotControlScreen.CachedChat cachedChat = this.chatCache.get(chatMessage);
         if (cachedChat != null) {
            arrayList.add(cachedChat);
            this.chatTotalLines = this.chatTotalLines + cachedChat.lines().size();
         }
      }

      int visibleChatLines = this.visibleChatLines();
      this.chatScrollLines = Mth.clamp(this.chatScrollLines, 0, Math.max(0, this.chatTotalLines - visibleChatLines));
      int chatScrollLines = this.chatOpen ? this.chatScrollLines : 0;
      int n = this.height - 52;
      byte x = 4;
      int n2 = 0;

      label55:
      for (int i = arrayList.size() - 1; i >= 0 && n2 < visibleChatLines; i--) {
         BotControlScreen.CachedChat cachedChat = (BotControlScreen.CachedChat)arrayList.get(i);
         float f = 1.0F;
         if (!this.chatOpen) {
            long l = now - cachedChat.seenAt();
            if (l >= 10000L) {
               break;
            }

            float f2 = (float)(10000L - l) / 10000.0F * 10.0F;
            f = Mth.clamp(f2, 0.0F, 1.0F);
            f *= f;
         }

         var lines = cachedChat.lines();

         for (int j = lines.size() - 1; j >= 0; j--) {
            if (chatScrollLines > 0) {
               chatScrollLines--;
            } else {
               int y = n - n2 * 9;
               int n3 = (int)(f * 128.0F);
               int n4 = (int)(f * 255.0F);
               if (n4 > 8) {
                  menuDrawContext.fill(x - 2, y - 1, x + 320 + 2, y + 9 - 1, n3 << 24);
                  menuDrawContext.text(mc.font, (FormattedCharSequence)lines.get(j), x, y, n4 << 24 | 16777215, true);
               }

               if (++n2 >= visibleChatLines) {
                  break label55;
               }
            }
         }
      }

      if (this.chatOpen) {
         this.renderChatField(menuDrawContext);
      }
   }

   private void renderChatField(MenuDrawContext menuDrawContext) {
      this.clampChatFieldScroll();
      this.chatSuggestor.update(this.originalChatText);
      String text = this.chatField.getText();
      int y = this.height - 16;
      menuDrawContext.drawRoundedRect(2.0F, y, this.width - 2, 14.0F, ProxyDotRadiusData.INSTANCE, new ZenithColor(Integer.MIN_VALUE));
      byte x = 6;
      int n = y + 3;
      int n2 = Mth.clamp(this.chatField.getCursor(), 0, text.length());
      String plainSubstrByWidth = mc.font.plainSubstrByWidth(text.substring(this.chatFirstChar), this.chatInnerWidth());
      if (this.chatField.isFlag() && !text.isEmpty()) {
         menuDrawContext.fill(x - 1, y + 1, x + mc.font.width(plainSubstrByWidth) + 1, y + 13, -2139062144);
      }

      if (!plainSubstrByWidth.isEmpty()) {
         FormattedCharSequence provideRenderText = this.chatSuggestor.provideRenderText(plainSubstrByWidth, this.chatFirstChar);
         menuDrawContext.text(mc.font, provideRenderText, x, n, -2039584, true);
      }

      String suggestion = this.chatSuggestor.getSuggestion();
      boolean flag = this.chatFirstChar + plainSubstrByWidth.length() >= text.length();
      int x2 = x + mc.font.width(plainSubstrByWidth);
      if (suggestion != null && !suggestion.isEmpty() && n2 == text.length() && flag) {
         menuDrawContext.text(mc.font, mc.font.plainSubstrByWidth(suggestion, this.width - 6 - x2), x2, n, -8355712, true);
      }

      if (System.currentTimeMillis() / 300L % 2L == 0L) {
         int endIndex = Mth.clamp(n2 - this.chatFirstChar, 0, plainSubstrByWidth.length());
         int x3 = x + mc.font.width(plainSubstrByWidth.substring(0, endIndex));
         if (n2 == text.length()) {
            menuDrawContext.text(mc.font, "_", x3, n, -2039584, true);
         } else {
            menuDrawContext.fill(x3, n - 1, x3 + 1, n + 10, -3092272);
         }
      }

      menuDrawContext.pose().pushMatrix();
      menuDrawContext.pose().translate(0.0F, 0.0F);
      this.chatSuggestor.render(menuDrawContext, menuDrawContext.getMouseX(), menuDrawContext.getMouseY());
      menuDrawContext.pose().popMatrix();
   }

   private int chatInnerWidth() {
      return this.width - 12;
   }

   private void clampChatFieldScroll() {
      String text = this.chatField.getText();
      int chatFirstChar = Mth.clamp(this.chatField.getCursor(), 0, text.length());
      this.chatFirstChar = Mth.clamp(this.chatFirstChar, 0, text.length());
      if (chatFirstChar < this.chatFirstChar) {
         this.chatFirstChar = chatFirstChar;
      }

      while (this.chatFirstChar < chatFirstChar && mc.font.width(text.substring(this.chatFirstChar, chatFirstChar)) > this.chatInnerWidth()) {
         this.chatFirstChar++;
      }
   }

   private int chatCharacterX(int n) {
      String text = this.chatField.getText();
      int endIndex = Mth.clamp(n, this.chatFirstChar, text.length());
      return 6 + mc.font.width(text.substring(this.chatFirstChar, endIndex));
   }

   private void syncChatCache(List<ChatMessage> chatLog, long now) {
      if (!this.chatSeeded) {
         this.chatSeeded = true;

         for (ChatMessage chatMessage : chatLog) {
            this.chatCache.put(chatMessage, new BotControlScreen.CachedChat(0L, this.wrapChatLines(chatMessage)));
         }
      }

      for (ChatMessage key : chatLog) {
         this.chatCache.computeIfAbsent(key, chatMessage -> new BotControlScreen.CachedChat(now, this.wrapChatLines(chatMessage)));
      }

      if (this.chatCache.size() > chatLog.size()) {
         var setFromMap = Collections.newSetFromMap(new IdentityHashMap());
         setFromMap.addAll(chatLog);
         this.chatCache.keySet().retainAll(setFromMap);
      }
   }

   private List<FormattedCharSequence> wrapChatLines(ChatMessage chatMessage) {
      return ComponentRenderUtils.wrapComponents(chatMessage.text(), 312, mc.font);
   }

   private int visibleChatLines() {
      return this.chatOpen ? Mth.clamp((this.height - 100) / 9, 4, 20) : 12;
   }

   private void openChat(String originalChatText) {
      this.chatOpen = true;
      this.suppressNextChar = true;
      this.chatScrollLines = 0;
      this.chatFirstChar = 0;
      this.chatField.setMaxLength(256);
      this.chatField.setText(originalChatText);
      this.chatField.setCursor(originalChatText.length());
      this.chatField.setSelected(true);
      this.chatField.setFlag(false);
      this.originalChatText = originalChatText;
      this.chatLastMessage = "";
      BotClient boundClient = this.boundClient;
      this.messageHistoryIndex = boundClient != null ? boundClient.getSentMessages().size() : 0;
      this.chatSuggestor.reset(originalChatText);
      this.clearMovementKeys();
   }

   private void closeChat() {
      this.chatOpen = false;
      this.suppressNextChar = false;
      this.chatScrollLines = 0;
      this.chatFirstChar = 0;
      this.chatField.setText("");
      this.chatField.setCursor(0);
      this.chatField.setSelected(false);
      this.originalChatText = "";
      this.chatSuggestor.setWindowActive(false);
      this.chatSuggestor.clearWindow();
   }

   private void setChatFromHistory(int chatFromHistory) {
      BotClient boundClient = this.boundClient;
      if (boundClient != null) {
         var sentMessages = boundClient.getSentMessages();
         int messageHistoryIndex = Mth.clamp(this.messageHistoryIndex + chatFromHistory, 0, sentMessages.size());
         if (messageHistoryIndex != this.messageHistoryIndex) {
            if (messageHistoryIndex == sentMessages.size()) {
               this.messageHistoryIndex = messageHistoryIndex;
               this.chatField.setText(this.chatLastMessage);
               this.chatField.setCursor(this.chatLastMessage.length());
            } else {
               if (this.messageHistoryIndex == sentMessages.size()) {
                  this.chatLastMessage = this.chatField.getText();
               }

               String text = (String)sentMessages.get(messageHistoryIndex);
               this.chatField.setText(text);
               this.chatField.setCursor(text.length());
               this.chatSuggestor.update(this.originalChatText);
               this.chatSuggestor.setWindowActive(false);
               this.messageHistoryIndex = messageHistoryIndex;
            }
         }
      }
   }

   private void clearMovementKeys() {
      this.keyForward = this.keyBack = this.keyLeft = this.keyRight = this.keyJump = this.keySneak = this.keySprint = false;
      if (this.control != null) {
         this.control.setMovement(false, false, false, false, false, false, false);
      }
   }

   private void renderHud(MenuDrawContext menuDrawContext, BotClient ensureBound, BotPlayer player) {
      int n = this.width / 2;
      int height = this.height;
      menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, n - 91, height - 22, 182, 22);
      int selectedSlot = player.getInventory().getSelectedSlot();
      menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, n - 91 - 1 + selectedSlot * 20, height - 22 - 1, 24, 23);
      ItemStack offhandItem = player.getOffhandItem();
      if (!offhandItem.isEmpty()) {
         menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_TEXTURE, n - 91 - 29, height - 23, 29, 24);
      }

      int y = height - 16 - 3;

      for (int i = 0; i < 9; i++) {
         ItemStack nonEquipmentItem = player.getInventory().getNonEquipmentItems().get(i);
         int x = n - 90 + i * 20 + 2;
         this.drawStack(menuDrawContext, nonEquipmentItem, x, y);
      }

      if (!offhandItem.isEmpty()) {
         this.drawStack(menuDrawContext, offhandItem, n - 91 - 26, y);
      }

      int x = n - 91;
      int n2 = n + 91;
      int y2 = height - 39;
      int n3 = Mth.ceil(player.getHealth());
      int n4 = Mth.ceil(player.getMaxHealth() / 2.0F);

      for (int i = 0; i < Math.min(n4, 10); i++) {
         int x2 = x + i * 8;
         menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_CONTAINER_TEXTURE, x2, y2, 9, 9);
         if (i * 2 + 1 < n3) {
            menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_FULL_TEXTURE, x2, y2, 9, 9);
         } else if (i * 2 + 1 == n3) {
            menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_HALF_TEXTURE, x2, y2, 9, 9);
         }
      }

      int armorValue = player.getArmorValue();
      if (armorValue > 0) {
         int y3 = y2 - 10;

         for (int i = 0; i < 10; i++) {
            int x2 = x + i * 8;
            if (i * 2 + 1 < armorValue) {
               menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_FULL_TEXTURE, x2, y3, 9, 9);
            } else if (i * 2 + 1 == armorValue) {
               menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_HALF_TEXTURE, x2, y3, 9, 9);
            } else {
               menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_TEXTURE, x2, y3, 9, 9);
            }
         }
      }

      int foodLevel = player.getFoodData().getFoodLevel();

      for (int i = 0; i < 10; i++) {
         int x2 = n2 - i * 8 - 9;
         menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY_TEXTURE, x2, y2, 9, 9);
         if (i * 2 + 1 < foodLevel) {
            menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_FULL_TEXTURE, x2, y2, 9, 9);
         } else if (i * 2 + 1 == foodLevel) {
            menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, FOOD_HALF_TEXTURE, x2, y2, 9, 9);
         }
      }

      int maxAirSupply = player.getMaxAirSupply();
      int n5 = Math.min(player.getAirSupply(), maxAirSupply);
      if (player.isUnderWater() || n5 < maxAirSupply) {
         int y3 = y2 - 10;
         int n6 = Mth.ceil((n5 - 2.0) * 10.0 / maxAirSupply);
         int n7 = Mth.ceil(n5 * 10.0 / maxAirSupply) - n6;

         for (int i = 0; i < n6 + n7; i++) {
            menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, i < n6 ? AIR_TEXTURE : AIR_EMPTY_TEXTURE, n2 - i * 8 - 9, y3, 9, 9);
         }
      }

      int y3 = height - 32 + 3;
      menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, XP_BAR_BACKGROUND_TEXTURE, x, y3, 182, 5);
      int width = (int)(player.experienceProgress * 183.0F);
      if (width > 0) {
         menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, XP_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, x, y3, width, 5);
      }

      if (player.experienceLevel > 0) {
         String str = String.valueOf(player.experienceLevel);
         int x2 = (this.width - mc.font.width(str)) / 2;
         int y4 = height - 31 - 4;
         menuDrawContext.text(mc.font, str, x2 + 1, y4, -16777216, false);
         menuDrawContext.text(mc.font, str, x2 - 1, y4, -16777216, false);
         menuDrawContext.text(mc.font, str, x2, y4 + 1, -16777216, false);
         menuDrawContext.text(mc.font, str, x2, y4 - 1, -16777216, false);
         menuDrawContext.text(mc.font, str, x2, y4, -8323296, false);
      }

      String str = ensureBound.name() + "  §7" + (int)player.getX() + " " + (int)player.getY() + " " + (int)player.getZ();
      menuDrawContext.text(mc.font, str, 6, 6, -1, true);
      String str2 = t("gui.bot.escExit");
      menuDrawContext.text(mc.font, str2, 6, 17, -6643542, true);
      if (this.view != null) {
      }
   }

   private void renderScoreboardSidebar(MenuDrawContext menuDrawContext, BotClient sidebarClient) {
      BotWorld world = sidebarClient.getWorld();
      if (world != null) {
         Component displayName;
         List sidebarRows;
         try {
            synchronized (sidebarClient.getRenderStateLock()) {
               label120: {
                  Scoreboard scoreboard = world.getScoreboard();
                  synchronized (scoreboard) {
                     Objective displayObjective = null;
                     PlayerTeam playersTeam = scoreboard.getPlayersTeam(sidebarClient.name());
                     if (playersTeam != null) {
                        DisplaySlot slot = playersTeam.getColor().map(net.minecraft.world.scores.TeamColor::displaySlot).orElse(null);
                        if (slot != null) {
                           displayObjective = scoreboard.getDisplayObjective(slot);
                        }
                     }

                     if (displayObjective == null) {
                        displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
                     }

                     if (displayObjective == null) {
                        this.sidebarTitle = null;
                     } else {
                        NumberFormat numberFormatOrDefault = displayObjective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
                        List<PlayerScoreEntry> subList = new ArrayList<>(scoreboard.listPlayerScores(displayObjective));
                        subList.removeIf(PlayerScoreEntry::isHidden);
                        subList.sort(net.minecraft.client.gui.Hud.SCORE_DISPLAY_ORDER);
                        if (subList.size() > 15) {
                           subList = subList.subList(0, 15);
                        }

                        sidebarRows = new ArrayList(subList.size());

                        for (PlayerScoreEntry sub : subList) {
                           MutableComponent formatNameForTeam = PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(sub.owner()), sub.ownerName());
                           MutableComponent formatValue = sub.formatValue(numberFormatOrDefault);
                           sidebarRows.add(new BotControlScreen.SidebarRow(formatNameForTeam, formatValue, mc.font.width(formatValue)));
                        }

                        displayName = displayObjective.getDisplayName();
                        break label120;
                     }
                  }

                  return;
               }
            }

            this.sidebarClient = sidebarClient;
            this.sidebarTitle = displayName;
            this.sidebarRows = (List<BotControlScreen.SidebarRow>)sidebarRows;
         } catch (RuntimeException e) {
            if (this.sidebarClient != sidebarClient || this.sidebarTitle == null) {
               return;
            }

            displayName = this.sidebarTitle;
            sidebarRows = this.sidebarRows;
         }

         int width = mc.font.width(displayName);
         int n = width;
         int width2 = mc.font.width(": ");

         for (Object sidebarRow : (Iterable<?>)(Object)(sidebarRows)) {
            BotControlScreen.SidebarRow sidebarRow2 = (BotControlScreen.SidebarRow)sidebarRow;
            n = Math.max(n, mc.font.width(sidebarRow2.name()) + (sidebarRow2.scoreWidth() > 0 ? width2 + sidebarRow2.scoreWidth() : 0));
         }

         byte b = 9;
         int n2 = sidebarRows.size() * b;
         int y1 = this.height / 2 + n2 / 3;
         int n3 = this.width - 3;
         int x = n3 - n;
         int n4 = y1 - n2;
         menuDrawContext.fill(x - 2, n4 - b - 1, n3 + 2, n4 - 1, 1711276032);
         menuDrawContext.fill(x - 2, n4 - 1, n3 + 2, y1, 1275068416);
         menuDrawContext.text(mc.font, displayName, x + n / 2 - width / 2, n4 - b, -1, false);

         for (int i = 0; i < sidebarRows.size(); i++) {
            BotControlScreen.SidebarRow sidebarRow = (BotControlScreen.SidebarRow)sidebarRows.get(i);
            int y = n4 + i * b;
            menuDrawContext.text(mc.font, sidebarRow.name(), x, y, -1, false);
            if (sidebarRow.scoreWidth() > 0) {
               menuDrawContext.text(mc.font, sidebarRow.score(), n3 - sidebarRow.scoreWidth(), y, -1, false);
            }
         }
      }
   }

   private void drawStack(MenuDrawContext menuDrawContext, ItemStack nonEquipmentItem, int x, int y) {
      if (!nonEquipmentItem.isEmpty()) {
         menuDrawContext.fakeItem(nonEquipmentItem, x, y);
         menuDrawContext.itemDecorations(mc.font, nonEquipmentItem, x, y);
      }
   }

   private BotControlScreen.ContainerLayout layoutFor(BotPlayer player, AbstractContainerMenu currentUiHandler) {
      if (currentUiHandler == player.inventoryMenu) {
         return new BotControlScreen.ContainerLayout(INVENTORY_TEXTURE, 176, 166, 0, 97, false, false);
      } else if (currentUiHandler instanceof ChestMenu chestMenu) {
         int rowCount = chestMenu.getRowCount();
         return new BotControlScreen.ContainerLayout(GENERIC_54_TEXTURE, 176, 114 + rowCount * 18, rowCount, 8, false, true);
      } else if (currentUiHandler instanceof ShulkerBoxMenu) {
         return new BotControlScreen.ContainerLayout(SHULKER_BOX_TEXTURE, 176, 166, 0, 8, false, true);
      } else if (currentUiHandler instanceof HopperMenu) {
         return new BotControlScreen.ContainerLayout(HOPPER_TEXTURE, 176, 133, 0, 8, false, true);
      } else if (currentUiHandler instanceof DispenserMenu) {
         return new BotControlScreen.ContainerLayout(DISPENSER_TEXTURE, 176, 166, 0, 8, true, true);
      } else if (currentUiHandler instanceof AbstractFurnaceMenu) {
         Identifier texture = currentUiHandler instanceof BlastFurnaceMenu
            ? BLAST_FURNACE_TEXTURE
            : (currentUiHandler instanceof SmokerMenu ? SMOKER_TEXTURE : FURNACE_TEXTURE);
         return new BotControlScreen.ContainerLayout(texture, 176, 166, 0, 8, true, true);
      } else if (currentUiHandler instanceof CraftingMenu) {
         return new BotControlScreen.ContainerLayout(CRAFTING_TABLE_TEXTURE, 176, 166, 0, 29, false, true);
      } else {
         int n = 160;
         int n2 = 142;

         for (Slot slot : currentUiHandler.slots) {
            n = Math.max(n, slot.x);
            n2 = Math.max(n2, slot.y);
         }

         return new BotControlScreen.ContainerLayout(null, Math.max(176, n + 16 + 8), n2 + 16 + 8, 0, 8, false, false);
      }
   }

   private void renderContainer(MenuDrawContext menuDrawContext, BotPlayer player, AbstractContainerMenu currentUiHandler, int xo, int yo) {
      menuDrawContext.drawRoundedRect(0.0F, 0.0F, this.width, this.height, ProxyDotRadiusData.INSTANCE, new ZenithColor(0, 0, 0, 120));
      BotControlScreen.ContainerLayout layoutFor = this.layoutFor(player, currentUiHandler);
      int x = (this.width - layoutFor.width()) / 2;
      int y = (this.height - layoutFor.height()) / 2;
      if (layoutFor.texture() == null) {
         this.drawSyntheticPanel(menuDrawContext, x, y, layoutFor.width(), layoutFor.height());

         for (Slot slot : currentUiHandler.slots) {
            this.drawSlotFrame(menuDrawContext, x + slot.x, y + slot.y);
         }
      } else if (layoutFor.chestRows() > 0) {
         menuDrawContext.blit(RenderPipelines.GUI_TEXTURED, layoutFor.texture(), x, y, 0.0F, 0.0F, layoutFor.width(), layoutFor.chestRows() * 18 + 17, 256, 256);
         menuDrawContext.blit(RenderPipelines.GUI_TEXTURED, layoutFor.texture(), x, y + layoutFor.chestRows() * 18 + 17, 0.0F, 126.0F, layoutFor.width(), 96, 256, 256);
      } else {
         menuDrawContext.blit(RenderPipelines.GUI_TEXTURED, layoutFor.texture(), x, y, 0.0F, 0.0F, layoutFor.width(), layoutFor.height(), 256, 256);
      }

      if (currentUiHandler == player.inventoryMenu) {
         BotClient boundClient = this.boundClient;
         BotPlayerGuiRender.drawEntity(
            menuDrawContext, x + 26, y + 8, x + 75, y + 78, 30, 0.0625F, xo, yo, player, boundClient != null ? boundClient.getPlayHandler() : null
         );
      }

      Slot hoveredSlot = this.hoveredSlot(player, currentUiHandler, xo, yo);
      this.hoveredSlotCache = hoveredSlot;
      if (hoveredSlot != null && hoveredSlot.isHighlightable()) {
         menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_TEXTURE, x + hoveredSlot.x - 4, y + hoveredSlot.y - 4, 24, 24);
      }

      for (Slot slot : currentUiHandler.slots) {
         if (slot.isActive()) {
            this.drawStack(menuDrawContext, slot.getItem(), x + slot.x, y + slot.y);
         }
      }

      if (hoveredSlot != null && hoveredSlot.isHighlightable()) {
         menuDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_TEXTURE, x + hoveredSlot.x - 4, y + hoveredSlot.y - 4, 24, 24);
      }

      Component containerTitle = this.containerTitle(player, currentUiHandler);
      int titleX = layoutFor.centerTitle() ? (layoutFor.width() - mc.font.width(containerTitle)) / 2 : layoutFor.titleX();
      menuDrawContext.text(mc.font, containerTitle, x + titleX, y + 6, 4210752, false);
      if (layoutFor.playerInventoryLabel()) {
         menuDrawContext.text(mc.font, Component.translatable("container.inventory"), x + 8, y + layoutFor.height() - 94, 4210752, false);
      }

      ItemStack carried = currentUiHandler.getCarried();
      if (!carried.isEmpty()) {
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(0.0F, 0.0F);
         this.drawStack(menuDrawContext, carried, xo - 8, yo - 8);
         menuDrawContext.pose().popMatrix();
      }

      if (hoveredSlot != null && hoveredSlot.hasItem() && carried.isEmpty()) {
         ItemStack stack = hoveredSlot.getItem();
         var tooltipLines = stack.getTooltipLines(
            TooltipContext.of(player.level()), player, mc.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL
         );
         menuDrawContext.setTooltipForNextFrame(mc.font, tooltipLines, stack.getTooltipImage(), xo, yo, stack.get(DataComponents.TOOLTIP_STYLE));
      }
   }

   private void drawSyntheticPanel(MenuDrawContext menuDrawContext, int x, int y, int width, int height) {
      menuDrawContext.fill(x - 1, y - 1, x + width + 1, y + height + 1, -16777216);
      menuDrawContext.fill(x, y, x + width, y + height, -3750202);
      menuDrawContext.fill(x, y, x + width - 1, y + 1, -1);
      menuDrawContext.fill(x, y + 1, x + 1, y + height - 1, -1);
      menuDrawContext.fill(x + 1, y + height - 1, x + width, y + height, -11184811);
      menuDrawContext.fill(x + width - 1, y + 1, x + width, y + height - 1, -11184811);
   }

   private void drawSlotFrame(MenuDrawContext menuDrawContext, int x1, int y0) {
      menuDrawContext.fill(x1 - 1, y0 - 1, x1 + 17, y0 + 17, -7631989);
      menuDrawContext.fill(x1 - 1, y0 - 1, x1 + 16, y0, -13158601);
      menuDrawContext.fill(x1 - 1, y0, x1, y0 + 16, -13158601);
      menuDrawContext.fill(x1, y0 + 16, x1 + 17, y0 + 17, -1);
      menuDrawContext.fill(x1 + 16, y0, x1 + 17, y0 + 16, -1);
   }

   private Component containerTitle(BotPlayer player, AbstractContainerMenu currentUiHandler) {
      if (currentUiHandler == player.inventoryMenu) {
         return Component.translatable("container.crafting");
      } else {
         BotClient boundClient = this.boundClient;
         BotPlayHandler playHandler = boundClient != null ? boundClient.getPlayHandler() : null;
         Component currentScreenTitle = playHandler != null ? playHandler.getCurrentScreenTitle() : null;
         return (Component)(currentScreenTitle != null ? currentScreenTitle : Component.literal(t("gui.bot.container")));
      }
   }

   @Nullable
   private Slot hoveredSlot(BotPlayer player, AbstractContainerMenu currentUiHandler, int xo, int yo) {
      BotControlScreen.ContainerLayout layoutFor = this.layoutFor(player, currentUiHandler);
      int n = (this.width - layoutFor.width()) / 2;
      int n2 = (this.height - layoutFor.height()) / 2;

      for (Slot slot : currentUiHandler.slots) {
         if (slot.isActive()) {
            int n3 = xo - n;
            int n4 = yo - n2;
            if (n3 >= slot.x - 1 && n3 < slot.x + 17 && n4 >= slot.y - 1 && n4 < slot.y + 17) {
               return slot;
            }
         }
      }

      return null;
   }

   private boolean isClickOutsideBounds(BotPlayer player, AbstractContainerMenu currentUiHandler, double xo, double yo) {
      BotControlScreen.ContainerLayout layoutFor = this.layoutFor(player, currentUiHandler);
      int n = (this.width - layoutFor.width()) / 2;
      int n2 = (this.height - layoutFor.height()) / 2;
      return xo < n || yo < n2 || xo >= n + layoutFor.width() || yo >= n2 + layoutFor.height();
   }

   private boolean uiOpen() {
      BotClient boundClient = this.boundClient;
      BotPlayer player = boundClient != null ? boundClient.getPlayer() : null;
      return boundClient != null && player != null ? this.currentUiHandler(boundClient, player) != null : false;
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
      if (this.cursorLocked && this.control != null) {
         if (!Double.isNaN(this.lastMouseX)) {
            double guiScale = mc.getWindow().getGuiScale();
            this.control.turn((mouseX - this.lastMouseX) * guiScale, (mouseY - this.lastMouseY) * guiScale);
         }

         this.lastMouseX = mouseX;
         this.lastMouseY = mouseY;
      } else {
         this.lastMouseX = Double.NaN;
         this.lastMouseY = Double.NaN;
      }

      super.mouseMoved(mouseX, mouseY);
   }

   @Override
   public void onMouseClicked(double xo, double yo, Enum3 enum2) {
      if (this.chatOpen) {
         this.chatSuggestor.mouseClicked(xo, yo);
      } else if (this.control != null) {
         BotClient boundClient = this.boundClient;
         BotPlayer player = boundClient != null ? boundClient.getPlayer() : null;
         if (player != null) {
            AbstractContainerMenu currentUiHandler = this.currentUiHandler(boundClient, player);
            if (currentUiHandler != null) {
               int buttonNum = enum2 == Enum3.RIGHT ? 1 : 0;
               Slot hoveredSlot = this.hoveredSlot(player, currentUiHandler, (int)xo, (int)yo);
               if (hoveredSlot != null) {
                  ContainerInput containerInput = WindowUtils.method5476() ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP;
                  this.control.clickSlot(hoveredSlot.index, buttonNum, containerInput);
               } else if (!currentUiHandler.getCarried().isEmpty() && this.isClickOutsideBounds(player, currentUiHandler, xo, yo)) {
                  this.control.clickSlot(64537, buttonNum, ContainerInput.PICKUP);
               }
            } else {
               if (enum2 == Enum3.LEFT) {
                  this.control.pressAttack();
               } else if (enum2 == Enum3.RIGHT) {
                  this.control.pressUse();
               }
            }
         }
      }
   }

   @Override
   public void onMouseReleased(double d, double d2, Enum3 enum2) {
      if (this.control != null) {
         if (enum2 == Enum3.LEFT) {
            this.control.releaseAttack();
         } else if (enum2 == Enum3.RIGHT) {
            this.control.releaseUse();
         }
      }
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      BotClient boundClient = this.boundClient;
      boolean flag = boundClient != null && boundClient.getPlayHandler() != null && boundClient.getPlayHandler().hasOpenScreen();
      if (this.chatOpen) {
         if (this.chatSuggestor.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         } else if (keyCode == 256) {
            this.closeChat();
            return true;
         } else if (keyCode == 257 || keyCode == 335) {
            String anObject = this.chatField.getText().trim();
            if (!anObject.isEmpty() && boundClient != null) {
               boundClient.addSentMessage(anObject);
               boundClient.sendChat(anObject);
            }

            this.closeChat();
            return true;
         } else if (keyCode == 265) {
            this.setChatFromHistory(-1);
            return true;
         } else if (keyCode == 264) {
            this.setChatFromHistory(1);
            return true;
         } else if (keyCode == 266) {
            this.chatScrollLines = Mth.clamp(
               this.chatScrollLines + this.visibleChatLines() - 1, 0, Math.max(0, this.chatTotalLines - this.visibleChatLines())
            );
            return true;
         } else if (keyCode == 267) {
            this.chatScrollLines = Mth.clamp(
               this.chatScrollLines - this.visibleChatLines() + 1, 0, Math.max(0, this.chatTotalLines - this.visibleChatLines())
            );
            return true;
         } else {
            this.chatField.keyPressed(keyCode, keycode, modifiers);
            return true;
         }
      } else if (keyCode == 256) {
         if (flag && this.control != null) {
            this.control.closeScreen();
            return true;
         } else if (this.inventoryOpen) {
            this.inventoryOpen = false;
            if (this.control != null) {
               this.control.closeScreen();
            }

            return true;
         } else {
            mc.gui.setScreen(new BotScreen());
            return true;
         }
      } else if (keyCode == 69) {
         if (this.control != null) {
            if (flag) {
               this.control.closeScreen();
            } else if (this.inventoryOpen) {
               this.inventoryOpen = false;
               this.control.closeScreen();
            } else {
               this.inventoryOpen = true;
            }
         }

         return true;
      } else if (keyCode == 70 && this.control != null && this.uiOpen()) {
         BotPlayer player = boundClient != null ? boundClient.getPlayer() : null;
         AbstractContainerMenu currentUiHandler = player != null ? this.currentUiHandler(boundClient, player) : null;
         Slot hoveredSlotCache = this.hoveredSlotCache;
         if (currentUiHandler != null && hoveredSlotCache != null && currentUiHandler.getCarried().isEmpty()) {
            this.control.clickSlot(hoveredSlotCache.index, 40, ContainerInput.SWAP);
         }

         return true;
      } else {
         if (this.control != null && !this.uiOpen()) {
            if (keyCode >= 49 && keyCode <= 57) {
               this.wheelSlot = keyCode - 49;
               this.control.requestSlot(this.wheelSlot);
               return true;
            }

            if (keyCode == 81) {
               this.control.requestDrop(WindowUtils.method5475());
               return true;
            }

            if (keyCode == 70) {
               this.control.requestSwapHands();
               return true;
            }

            if (keyCode == 84) {
               this.openChat("");
               return true;
            }

            if (keyCode == 47) {
               this.openChat("/");
               return true;
            }
         }

         return this.updateMovementKey(keyCode, true) ? true : super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
      }
   }

   @Override
   public boolean charTyped(CharacterEvent input) {
      return this.charTyped((char)input.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      if (this.suppressNextChar) {
         this.suppressNextChar = false;
         return true;
      } else if (this.chatOpen) {
         this.chatField.charTyped(codepoint, n);
         return true;
      } else {
         return super.charTyped(new CharacterEvent(codepoint));
      }
   }

   @Override
   public boolean keyReleased(KeyEvent input) {
      return this.keyReleased(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyReleased(int keyCode, int keycode, int modifiers) {
      return this.updateMovementKey(keyCode, false) ? true : super.keyReleased(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.chatOpen && this.chatSuggestor.mouseScrolled(verticalAmount, mouseX, mouseY)) {
         return true;
      } else if (this.chatOpen && verticalAmount != 0.0) {
         int max = Math.max(0, this.chatTotalLines - this.visibleChatLines());
         this.chatScrollLines = Mth.clamp(this.chatScrollLines + (verticalAmount > 0.0 ? 1 : -1), 0, max);
         return true;
      } else {
         if (this.control != null && verticalAmount != 0.0 && !this.uiOpen()) {
            BotClient boundClient = this.boundClient;
            BotPlayer player = boundClient != null ? boundClient.getPlayer() : null;
            if (player != null) {
               int selectedSlot = player.getInventory().getSelectedSlot();
               int wheelSlot = this.wheelSlot >= 0 && this.wheelSlot != selectedSlot ? this.wheelSlot : selectedSlot;
               this.wheelSlot = Math.floorMod(wheelSlot - (int)Math.signum(verticalAmount), 9);
               this.control.requestSlot(this.wheelSlot);
               return true;
            }
         }

         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   private boolean updateMovementKey(int keyCode, boolean keyJump) {
      switch (keyCode) {
         case 32:
            this.keyJump = keyJump;
            break;
         case 65:
            this.keyLeft = keyJump;
            break;
         case 68:
            this.keyRight = keyJump;
            break;
         case 83:
            this.keyBack = keyJump;
            break;
         case 87:
            this.keyForward = keyJump;
            break;
         case 340:
            this.keySneak = keyJump;
            break;
         case 341:
            this.keySprint = keyJump;
            break;
         default:
            return false;
      }

      if (this.control != null) {
         this.control.setMovement(this.keyForward, this.keyBack, this.keyLeft, this.keyRight, this.keyJump, this.keySneak, this.keySprint);
      }

      return true;
   }

   private void setCursorLocked(boolean cursorLocked) {
      if (this.cursorLocked != cursorLocked) {
         this.cursorLocked = cursorLocked;
         this.lastMouseX = Double.NaN;
         this.lastMouseY = Double.NaN;
         Window window = mc.getWindow();
         double xpos = mc.getWindow().getScreenWidth() / 2.0;
         double ypos = mc.getWindow().getScreenHeight() / 2.0;
         if (cursorLocked) {
            InputConstants.grabMouse(window, xpos, ypos);
         } else {
            InputConstants.releaseMouse(window, xpos, ypos);
         }
      }
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private static final class CachedChat {
      private final long seenAt;
      private final List<FormattedCharSequence> lines;

      public long seenAt() {
         return this.seenAt;
      }

      public List<FormattedCharSequence> lines() {
         return this.lines;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CachedChat other)) return false;
         return java.util.Objects.equals(this.seenAt, other.seenAt) && java.util.Objects.equals(this.lines, other.lines);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(seenAt, lines);
      }

      @Override
      public String toString() {
         return "CachedChat[" + "seenAt=" + this.seenAt + ", " + "lines=" + this.lines + "]";
      }

      private CachedChat(long seenAt, List<FormattedCharSequence> lines) {
         this.seenAt = seenAt;
         this.lines = lines;
      }
   }

   private static final class ContainerLayout {
      @Nullable
      private final Identifier texture;
      private final int width;
      private final int height;
      private final int chestRows;
      private final int titleX;
      private final boolean centerTitle;
      private final boolean playerInventoryLabel;

      public Identifier texture() {
         return this.texture;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public int chestRows() {
         return this.chestRows;
      }

      public int titleX() {
         return this.titleX;
      }

      public boolean centerTitle() {
         return this.centerTitle;
      }

      public boolean playerInventoryLabel() {
         return this.playerInventoryLabel;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ContainerLayout other)) return false;
         return java.util.Objects.equals(this.texture, other.texture) && java.util.Objects.equals(this.width, other.width) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.chestRows, other.chestRows) && java.util.Objects.equals(this.titleX, other.titleX) && java.util.Objects.equals(this.centerTitle, other.centerTitle) && java.util.Objects.equals(this.playerInventoryLabel, other.playerInventoryLabel);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(texture, width, height, chestRows, titleX, centerTitle, playerInventoryLabel);
      }

      @Override
      public String toString() {
         return "ContainerLayout[" + "texture=" + this.texture + ", " + "width=" + this.width + ", " + "height=" + this.height + ", " + "chestRows=" + this.chestRows + ", " + "titleX=" + this.titleX + ", " + "centerTitle=" + this.centerTitle + ", " + "playerInventoryLabel=" + this.playerInventoryLabel + "]";
      }

      private ContainerLayout(@Nullable Identifier texture, int width, int height, int chestRows, int titleX, boolean centerTitle, boolean playerInventoryLabel) {
         this.texture = texture;
         this.width = width;
         this.height = height;
         this.chestRows = chestRows;
         this.titleX = titleX;
         this.centerTitle = centerTitle;
         this.playerInventoryLabel = playerInventoryLabel;
      }
   }

   private static final class SidebarRow {
      private final Component name;
      private final Component score;
      private final int scoreWidth;

      public Component name() {
         return this.name;
      }

      public Component score() {
         return this.score;
      }

      public int scoreWidth() {
         return this.scoreWidth;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SidebarRow other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.score, other.score) && java.util.Objects.equals(this.scoreWidth, other.scoreWidth);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, score, scoreWidth);
      }

      @Override
      public String toString() {
         return "SidebarRow[" + "name=" + this.name + ", " + "score=" + this.score + ", " + "scoreWidth=" + this.scoreWidth + "]";
      }

      private SidebarRow(Component name, Component score, int scoreWidth) {
         this.name = name;
         this.score = score;
         this.scoreWidth = scoreWidth;
      }
   }
}
