package org.zenith.client.hud;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay.ScoreDisplayEntry;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class SidebarHud extends Interface {
   private final Animation animation = new Animation(250L, Easing.INSTANCE4);

   public SidebarHud(String text) {
      super(text, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, null);
   }

   @Override
   public void method285(CustomDrawContext customDrawContext, Interface.Entry entry) {
   }

   @Override
   public void method286(CustomDrawContext customDrawContext, Interface.Entry entry) {
   }

   @Override
   public void method287(float f, float f2) {
   }

   @Override
   public void update(float f, float f2) {
   }

   @Override
   public void release() {
   }

   @Override
   public void method288(float f, float f2) {
   }

   @Override
   public void method289(CustomDrawContext customDrawContext, float f, float f2, InterfaceModule interfaceModule, float f3, float f4) {
   }

   @Override
   public JsonObject save() {
      return new JsonObject();
   }

   @Override
   public void load(JsonObject json) {
   }

   @Override
   public float method290() {
      return 1.0F;
   }

   @Override
   public void method291(MenuDrawContext graphics) {
      float x = this.method290();
      graphics.pushMatrix();
      graphics.pose().translate(this.floatValue + this.width / 2.0F, this.floatValue2);
      graphics.pose().scale(x, x);
      graphics.pose().translate(-this.floatValue - this.width / 2.0F, -this.floatValue2);
      this.method8(graphics);
      graphics.popMatrix();
   }

   @Override
   public void method8(CustomDrawContext graphics) {
      Scoreboard scoreboard = mc.level.getScoreboard();
      Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
      this.animation
         .updateVisibility(
            mc.options.keyPlayerList.isDown()
               && (!mc.isLocalServer() || mc.player.connection.getListedOnlinePlayers().size() > 1 || displayObjective != null)
         );
      if (this.animation.getValue() != 0.0F) {
         this.method292(graphics, graphics.guiWidth(), scoreboard, displayObjective);
      }

      this.animation.setDuration(250L);
      this.animation.setEasing(Easing.INSTANCE4);
   }

   public void method292(GuiGraphicsExtractor graphics, int guiWidth, Scoreboard scoreboard, @Nullable Objective displayObjective) {
      float f = this.animation.getValue();
      List<PlayerInfo> playerInfos = mc.gui.hud.getTabList().getPlayerInfos();
      var arrayList = new ArrayList<>(playerInfos.size());
      int width = mc.font.width(" ");
      int n = 0;
      int n2 = 0;

      for (PlayerInfo playerInfo : playerInfos) {
         Component nameForDisplay = mc.gui.hud.getTabList().getNameForDisplay(playerInfo);
         n = Math.max(n, mc.font.width(nameForDisplay));
         int score = 0;
         MutableComponent empty = null;
         int scoreWidth = 0;
         if (displayObjective != null) {
            ScoreHolder name = ScoreHolder.fromGameProfile(playerInfo.getProfile());
            ReadOnlyScoreInfo playerScoreInfo = scoreboard.getPlayerScoreInfo(name, displayObjective);
            if (playerScoreInfo != null) {
               score = playerScoreInfo.value();
            }

            if (displayObjective.getRenderType() != RenderType.HEARTS) {
               NumberFormat numberFormatOrDefault = displayObjective.numberFormatOrDefault(StyledFormat.PLAYER_LIST_DEFAULT);
               empty = playerScoreInfo == null ? net.minecraft.network.chat.Component.empty() : playerScoreInfo.formatValue(numberFormatOrDefault);
               scoreWidth = mc.font.width(empty);
               n2 = Math.max(n2, scoreWidth > 0 ? width + scoreWidth : 0);
            }
         }

         arrayList.add(new ScoreDisplayEntry(nameForDisplay, score, empty, scoreWidth));
      }

      if (!mc.gui.hud.getTabList().healthStates.isEmpty()) {
         var uuids = playerInfos.stream().map(playerInfo -> playerInfo.getProfile().id()).collect(Collectors.toSet());
         mc.gui.hud.getTabList().healthStates.keySet().removeIf(var1x -> !uuids.contains(var1x));
      }

      int size = playerInfos.size();
      int size2 = size;

      int n3;
      for (n3 = 1; size2 > 20; size2 = (size + n3 - 1) / n3) {
         n3++;
      }

      boolean flag = true;
      int n4;
      if (displayObjective != null) {
         n4 = displayObjective.getRenderType() == RenderType.HEARTS ? 90 : n2;
      } else {
         n4 = 0;
      }

      int slotWidth = Math.min(n3 * ((flag ? 9 : 0) + n + n4 + 13), guiWidth - 50) / n3;
      int floatValue = guiWidth / 2 - (slotWidth * n3 + (n3 - 1) * 5) / 2;
      this.floatValue = floatValue;
      int y = 10;
      int width2 = slotWidth * n3 + (n3 - 1) * 5;
      this.floatValue2 = y;
      this.width = width2;
      List<FormattedCharSequence> formattedCharSequences = null;
      if (mc.gui.hud.getTabList().header != null) {
         Component input = ScoreboardParser.method7074(mc.gui.hud.getTabList().header);
         formattedCharSequences = mc.font.split(input, guiWidth - 50);

         for (FormattedCharSequence text : formattedCharSequences) {
            width2 = Math.max(width2, mc.font.width(text));
         }
      }

      List<FormattedCharSequence> formattedCharSequences2 = null;
      if (mc.gui.hud.getTabList().footer != null) {
         Component input = ScoreboardParser.method7074(mc.gui.hud.getTabList().footer);
         formattedCharSequences2 = mc.font.split(input, guiWidth - 50);

         for (FormattedCharSequence text : formattedCharSequences2) {
            width2 = Math.max(width2, mc.font.width(text));
         }
      }

      int n5 = formattedCharSequences != null ? formattedCharSequences.size() * 9 : 0;
      int n6 = formattedCharSequences != null ? 1 : 0;
      int n7 = size2 * 9;
      int n8 = formattedCharSequences2 != null ? 1 : 0;
      int n9 = formattedCharSequences2 != null ? formattedCharSequences2.size() * 9 : 0;
      int height = n5 + n6 + n7 + n8 + n9;
      this.height = height;
      float x = guiWidth / 2 - 1;
      float y2 = 10.0F + height / 2.0F;
      float x2 = Math.max(1.0E-4F, f);
      Matrix3x2fStack pose = graphics.pose();
      pose.pushMatrix();
      pose.translate(x, y2);
      pose.scale(x2, x2);
      pose.translate(-x, -y2);
      if (formattedCharSequences != null) {
         graphics.fill(guiWidth / 2 - width2 / 2 - 1, y - 1, guiWidth / 2 + width2 / 2 + 1, y + formattedCharSequences.size() * 9, Integer.MIN_VALUE);

         for (FormattedCharSequence text : formattedCharSequences) {
            int width3 = mc.font.width(text);
            graphics.text(mc.font, text, guiWidth / 2 - width3 / 2, y, -1);
            y += 9;
         }

         y++;
      }

      graphics.fill(guiWidth / 2 - width2 / 2 - 1, y - 1, guiWidth / 2 + width2 / 2 + 1, y + size2 * 9, Integer.MIN_VALUE);
      int backgroundColor = mc.options.getBackgroundColor(553648127);

      for (int i = 0; i < size; i++) {
         int n10 = i / size2;
         int n11 = i % size2;
         int x0 = floatValue + n10 * slotWidth + n10 * 5;
         int y3 = y + n11 * 9;
         graphics.fill(x0, y3, x0 + slotWidth, y3 + 8, backgroundColor);
         if (i < playerInfos.size()) {
            PlayerInfo info = (PlayerInfo)playerInfos.get(i);
            ScoreDisplayEntry entry = (ScoreDisplayEntry)arrayList.get(i);
            GameProfile profile = info.getProfile();
            int x02 = x0;
            if (flag) {
               Player playerByUUID = mc.level.getPlayerByUUID(profile.id());
               boolean flip = playerByUUID != null && method298(playerByUUID);
               PlayerFaceExtractor.extractRenderState(graphics, info.getSkin().body().texturePath(), x0, y3, 8, info.showHat(), flip, -1);
               x02 = x0 + 9;
            }

            int color = info.getGameMode() == GameType.SPECTATOR ? -1859310289 : -1;
            graphics.text(mc.font, entry.name(), x02, y3, color);
            if (displayObjective != null && info.getGameMode() != GameType.SPECTATOR) {
               int left = x02 + n + 1;
               int right = left + n4;
               if (right - left > 5) {
                  mc.gui.hud.getTabList().extractTablistScore(displayObjective, y3, entry, left, right, profile.id(), graphics);
               }
            }

            mc.gui.hud.getTabList().extractPingIcon(graphics, slotWidth, x02 - (flag ? 9 : 0), y3, info);
         }
      }

      if (formattedCharSequences2 != null) {
         y += size2 * 9 + 1;
         graphics.fill(guiWidth / 2 - width2 / 2 - 1, y - 1, guiWidth / 2 + width2 / 2 + 1, y + formattedCharSequences2.size() * 9, Integer.MIN_VALUE);

         for (FormattedCharSequence text : formattedCharSequences2) {
            int width3 = mc.font.width(text);
            graphics.text(mc.font, text, guiWidth / 2 - width3 / 2, y, -1);
            y += 9;
         }
      }

      pose.popMatrix();
   }

   @Override
   public boolean method293(double d, double d2) {
      return false;
   }

   @Override
   public float method294() {
      return 0.0F;
   }

   @Override
   public float method295() {
      return 0.0F;
   }

   @Override
   public float method296() {
      return 0.0F;
   }

   @Override
   public float method297() {
      return 0.0F;
   }

   private static boolean method298(Player playerByUUID) {
      return AvatarRenderer.isPlayerUpsideDown(playerByUUID) && playerByUUID.isModelPartShown(PlayerModelPart.CAPE);
   }
}
