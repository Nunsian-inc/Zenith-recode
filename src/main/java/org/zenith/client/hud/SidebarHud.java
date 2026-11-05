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

   public SidebarHud(String var1) {
      super(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, null);
   }

   @Override
   public void method285(CustomDrawContext var1, Interface.Entry var2) {
   }

   @Override
   public void method286(CustomDrawContext var1, Interface.Entry var2) {
   }

   @Override
   public void method287(float var1, float var2) {
   }

   @Override
   public void update(float var1, float var2) {
   }

   @Override
   public void release() {
   }

   @Override
   public void method288(float var1, float var2) {
   }

   @Override
   public void method289(CustomDrawContext var1, float var2, float var3, InterfaceModule var4, float var5, float var6) {
   }

   @Override
   public JsonObject save() {
      return new JsonObject();
   }

   @Override
   public void load(JsonObject var1) {
   }

   @Override
   public float method290() {
      return 1.0F;
   }

   @Override
   public void method291(MenuDrawContext var1) {
      float var2 = this.method290();
      var1.pushMatrix();
      var1.pose().translate(this.floatValue + this.width / 2.0F, this.floatValue2);
      var1.pose().scale(var2, var2);
      var1.pose().translate(-this.floatValue - this.width / 2.0F, -this.floatValue2);
      this.method8(var1);
      var1.popMatrix();
   }

   @Override
   public void method8(CustomDrawContext var1) {
      Scoreboard var2 = mc.level.getScoreboard();
      Objective var3 = var2.getDisplayObjective(DisplaySlot.LIST);
      this.animation
         .method2807(
            mc.options.keyPlayerList.isDown()
               && (!mc.isLocalServer() || mc.player.connection.getListedOnlinePlayers().size() > 1 || var3 != null)
         );
      if (this.animation.getValue() != 0.0F) {
         this.method292(var1, var1.guiWidth(), var2, var3);
      }

      this.animation.setDuration(250L);
      this.animation.setEasing(Easing.INSTANCE4);
   }

   public void method292(GuiGraphicsExtractor var1, int var2, Scoreboard var3, @Nullable Objective var4) {
      float var5 = this.animation.getValue();
      List<PlayerInfo> var6 = mc.gui.hud.getTabList().getPlayerInfos();
      var var7 = new ArrayList<>(var6.size());
      int var8 = mc.font.width(" ");
      int var9 = 0;
      int var10 = 0;

      for (Object var12_o : (Iterable<?>)(Object)(var6)) {
         PlayerInfo var12 = (PlayerInfo)var12_o;
         Component var13 = mc.gui.hud.getTabList().getNameForDisplay(var12);
         var9 = Math.max(var9, mc.font.width(var13));
         int var14 = 0;
         MutableComponent var15 = null;
         int var16 = 0;
         if (var4 != null) {
            ScoreHolder var17 = ScoreHolder.fromGameProfile(var12.getProfile());
            ReadOnlyScoreInfo var18 = var3.getPlayerScoreInfo(var17, var4);
            if (var18 != null) {
               var14 = var18.value();
            }

            if (var4.getRenderType() != RenderType.HEARTS) {
               NumberFormat var19 = var4.numberFormatOrDefault(StyledFormat.PLAYER_LIST_DEFAULT);
               var15 = var18 == null ? net.minecraft.network.chat.Component.empty() : var18.formatValue(var19);
               var16 = mc.font.width(var15);
               var10 = Math.max(var10, var16 > 0 ? var8 + var16 : 0);
            }
         }

         var7.add(new ScoreDisplayEntry(var13, var14, var15, var16));
      }

      if (!mc.gui.hud.getTabList().healthStates.isEmpty()) {
         var var45 = var6.stream().map(var0 -> var0.getProfile().id()).collect(Collectors.toSet());
         mc.gui.hud.getTabList().healthStates.keySet().removeIf(var1x -> !var45.contains(var1x));
      }

      int var46 = var6.size();
      int var47 = var46;

      int var48;
      for (var48 = 1; var47 > 20; var47 = (var46 + var48 - 1) / var48) {
         var48++;
      }

      boolean var49 = true;
      int var50;
      if (var4 != null) {
         var50 = var4.getRenderType() == RenderType.HEARTS ? 90 : var10;
      } else {
         var50 = 0;
      }

      int var51 = Math.min(var48 * ((var49 ? 9 : 0) + var9 + var50 + 13), var2 - 50) / var48;
      int var52 = var2 / 2 - (var51 * var48 + (var48 - 1) * 5) / 2;
      this.floatValue = var52;
      int var53 = 10;
      int var55 = var51 * var48 + (var48 - 1) * 5;
      this.floatValue2 = var53;
      this.width = var55;
      List<FormattedCharSequence> var20 = null;
      if (mc.gui.hud.getTabList().header != null) {
         Component var21 = ScoreboardParser.method7074(mc.gui.hud.getTabList().header);
         var20 = mc.font.split(var21, var2 - 50);

         for (Object var23_o : (Iterable<?>)(Object)(var20)) {
            FormattedCharSequence var23 = (FormattedCharSequence)var23_o;
            var55 = Math.max(var55, mc.font.width(var23));
         }
      }

      List<FormattedCharSequence> var56 = null;
      if (mc.gui.hud.getTabList().footer != null) {
         Component var57 = ScoreboardParser.method7074(mc.gui.hud.getTabList().footer);
         var56 = mc.font.split(var57, var2 - 50);

         for (Object var24_o : (Iterable<?>)(Object)(var56)) {
            FormattedCharSequence var24 = (FormattedCharSequence)var24_o;
            var55 = Math.max(var55, mc.font.width(var24));
         }
      }

      int var58 = var20 != null ? var20.size() * 9 : 0;
      int var60 = var20 != null ? 1 : 0;
      int var61 = var47 * 9;
      int var25 = var56 != null ? 1 : 0;
      int var26 = var56 != null ? var56.size() * 9 : 0;
      int var27 = var58 + var60 + var61 + var25 + var26;
      this.height = var27;
      float var28 = var2 / 2 - 1;
      float var29 = 10.0F + var27 / 2.0F;
      float var30 = Math.max(1.0E-4F, var5);
      Matrix3x2fStack var31 = var1.pose();
      var31.pushMatrix();
      var31.translate(var28, var29);
      var31.scale(var30, var30);
      var31.translate(-var28, -var29);
      if (var20 != null) {
         var1.fill(var2 / 2 - var55 / 2 - 1, var53 - 1, var2 / 2 + var55 / 2 + 1, var53 + var20.size() * 9, Integer.MIN_VALUE);

         for (Object var33_o : (Iterable<?>)(Object)(var20)) {
            FormattedCharSequence var33 = (FormattedCharSequence)var33_o;
            int var34 = mc.font.width(var33);
            var1.text(mc.font, var33, var2 / 2 - var34 / 2, var53, -1);
            var53 += 9;
         }

         var53++;
      }

      var1.fill(var2 / 2 - var55 / 2 - 1, var53 - 1, var2 / 2 + var55 / 2 + 1, var53 + var47 * 9, Integer.MIN_VALUE);
      int var62 = mc.options.getBackgroundColor(553648127);

      for (int var63 = 0; var63 < var46; var63++) {
         int var65 = var63 / var47;
         int var35 = var63 % var47;
         int var36 = var52 + var65 * var51 + var65 * 5;
         int var37 = var53 + var35 * 9;
         var1.fill(var36, var37, var36 + var51, var37 + 8, var62);
         if (var63 < var6.size()) {
            PlayerInfo var38 = (PlayerInfo)var6.get(var63);
            ScoreDisplayEntry var39 = (ScoreDisplayEntry)var7.get(var63);
            GameProfile var40 = var38.getProfile();
            int var41 = var36;
            if (var49) {
               Player var42 = mc.level.getPlayerByUUID(var40.id());
               boolean var43 = var42 != null && method298(var42);
               PlayerFaceExtractor.extractRenderState(var1, var38.getSkin().body().texturePath(), var36, var37, 8, var38.showHat(), var43, -1);
               var41 = var36 + 9;
            }

            int var68 = var38.getGameMode() == GameType.SPECTATOR ? -1859310289 : -1;
            var1.text(mc.font, var39.name(), var41, var37, var68);
            if (var4 != null && var38.getGameMode() != GameType.SPECTATOR) {
               int var69 = var41 + var9 + 1;
               int var44 = var69 + var50;
               if (var44 - var69 > 5) {
                  mc.gui.hud.getTabList().extractTablistScore(var4, var37, var39, var69, var44, var40.id(), var1);
               }
            }

            mc.gui.hud.getTabList().extractPingIcon(var1, var51, var41 - (var49 ? 9 : 0), var37, var38);
         }
      }

      if (var56 != null) {
         var53 += var47 * 9 + 1;
         var1.fill(var2 / 2 - var55 / 2 - 1, var53 - 1, var2 / 2 + var55 / 2 + 1, var53 + var56.size() * 9, Integer.MIN_VALUE);

         for (Object var66_o : (Iterable<?>)(Object)(var56)) {
            FormattedCharSequence var66 = (FormattedCharSequence)var66_o;
            int var67 = mc.font.width(var66);
            var1.text(mc.font, var66, var2 / 2 - var67 / 2, var53, -1);
            var53 += 9;
         }
      }

      var31.popMatrix();
   }

   @Override
   public boolean method293(double var1, double var3) {
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

   private static boolean method298(Player var0) {
      return AvatarRenderer.isPlayerUpsideDown(var0) && var0.isModelPartShown(PlayerModelPart.CAPE);
   }
}
