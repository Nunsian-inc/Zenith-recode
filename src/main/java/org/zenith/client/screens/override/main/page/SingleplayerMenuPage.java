package org.zenith.client.screens.override.main.page;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelCandidates;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class SingleplayerMenuPage implements MainMenuPage, MinecraftAccess {
   private static final float WIDTH = MenuTheme.px(431.0F);
   private static final float LIST_HEIGHT = MenuTheme.px(342.0F);
   private static final float CONTROLS_GAP = MenuTheme.px(48.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(62.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float ROW_RADIUS = MenuTheme.px(12.0F);
   private static final float ROW_PADDING = MenuTheme.px(16.0F);
   private static final float ICON_SIZE = MenuTheme.px(30.0F);
   private static final float ICON_RADIUS = MenuTheme.px(6.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final DateTimeFormatter LAST_PLAYED = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.systemDefault());
   private final Screen parent;
   private final List<SingleplayerMenuPage.Row> rows = new ArrayList<>();
   private final MenuScroll scroll = new MenuScroll();
   private final MenuButton createButton = MenuButton.accent(MenuIcons.ADD, MenuTheme.t("menu.singleplayer.create"));
   private final Animation entrance = new Animation(520L, 0.0F, Easing.INSTANCE12);
   private boolean loading;
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private int loadGeneration;

   public SingleplayerMenuPage(Screen parent) {
      this.parent = parent;
      this.createButton.onClick(this::createWorld);
   }

   @Override
   public float contentWidth() {
      return WIDTH;
   }

   @Override
   public float contentHeight() {
      return LIST_HEIGHT + CONTROLS_GAP + MenuButton.HEIGHT;
   }

   @Override
   public void onShow() {
      this.reload();
   }

   @Override
   public void onHide() {
      this.loadGeneration++;
      this.loading = false;
      this.closeRows();
   }

   private void closeRows() {
      for (SingleplayerMenuPage.Row row : this.rows) {
         row.icon.close();
      }

      this.rows.clear();
   }

   private void reload() {
      this.closeRows();
      this.scroll.reset();
      this.loading = true;
      int n = ++this.loadGeneration;
      LevelStorageSource levelSource = mc.getLevelSource();

      try {
         LevelCandidates levelCandidates = levelSource.findLevelCandidates();
         levelSource.loadLevelSummaries(levelCandidates).thenAcceptAsync(var2x -> {
            if (n == this.loadGeneration) {
               this.closeRows();

               for (LevelSummary var4x : var2x) {
                  if (!var4x.isDisabled()) {
                     this.rows.add(new SingleplayerMenuPage.Row(var4x));
                  }
               }

               this.loading = false;
            }
         }, mc);
      } catch (Exception e) {
         this.loading = false;
      }
   }

   private void createWorld() {
      CreateWorldScreen.openFresh(mc, () -> mc.gui.setScreen(this.parent));
   }

   private void play(SingleplayerMenuPage.Row row) {
      if (!row.summary.isLocked() && !row.summary.requiresManualConversion()) {
         mc.createWorldOpenFlows().openWorld(row.summary.getLevelId(), () -> mc.gui.setScreen(this.parent));
      }
   }

   private void confirmDelete(SingleplayerMenuPage.Row row) {
      Screen screen = mc.gui.screen();
      mc.gui.setScreen(
         new ConfirmScreen(
            flag -> {
               if (flag) {
                  this.delete(row);
               }

               mc.gui.setScreen(screen);
            },
            Component.translatable("selectWorld.deleteQuestion"),
            Component.translatable("selectWorld.deleteWarning", row.summary.getLevelName()),
            Component.translatable("selectWorld.deleteButton"),
            CommonComponents.GUI_CANCEL
         )
      );
   }

   private void delete(SingleplayerMenuPage.Row row) {
      try (LevelStorageAccess validateAndCreateAccess = mc.getLevelSource().validateAndCreateAccess(row.summary.getLevelId())) {
         validateAndCreateAccess.deleteLevel();
      } catch (Exception e) {
      }

      this.reload();
   }

   @Override
   public void layout(BoundsData boundsData) {
      float f = boundsData.getFloatValue2() + LIST_HEIGHT + CONTROLS_GAP;
      float preferredWidth = this.createButton.preferredWidth();
      this.createButton.setBounds(boundsData.getFloatValue() + (boundsData.width() - preferredWidth) / 2.0F, f, preferredWidth, MenuButton.HEIGHT);
   }

   @Override
   public void render(MenuDrawContext graphics, float mouseX, float mouseY, BoundsData boundsData) {
      BoundsData listArea = MenuSkin.listArea(graphics, boundsData, LIST_HEIGHT);
      float width = listArea.width() - RAIL_GAP - RAIL_WIDTH;
      float content = this.rows.isEmpty() ? 0.0F : this.rows.size() * ROW_HEIGHT + (this.rows.size() - 1) * ROW_GAP;
      this.scroll.update(listArea.height(), content);
      this.viewport = new BoundsData(listArea.getFloatValue(), listArea.getFloatValue2(), width, listArea.height());
      float f = this.entrance.animateTo(1.0F);
      graphics.enableScissor(listArea.getFloatValue(), listArea.getFloatValue2(), listArea.getFloatValue() + width, listArea.getFloatValue2() + listArea.height());
      float y = listArea.getFloatValue2() - this.scroll.offset();
      int n = 0;

      for (SingleplayerMenuPage.Row row : this.rows) {
         row.bounds = new BoundsData(listArea.getFloatValue(), y, width, ROW_HEIGHT);
         if (y + ROW_HEIGHT >= listArea.getFloatValue2() && y <= listArea.getFloatValue2() + listArea.height()) {
            row.render(graphics, mouseX, mouseY, listArea.getFloatValue(), y + MenuSkin.enterOffset(f, n), width);
         }

         y += ROW_HEIGHT + ROW_GAP;
         n++;
      }

      graphics.disableScissor();
      if (this.rows.isEmpty()) {
         Font medium = MenuTheme.medium(12.0F);
         String text = MenuTheme.t(this.loading ? "menu.singleplayer.loading" : "menu.singleplayer.empty");
         graphics.drawText(
            medium,
            text,
            listArea.getFloatValue() + (width - medium.width(text)) / 2.0F,
            listArea.getFloatValue2() + (listArea.height() - medium.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      }

      this.scroll.renderRail(graphics, listArea.getFloatValue() + listArea.width() - RAIL_WIDTH, listArea.getFloatValue2(), listArea.height());
      this.createButton.render(graphics, mouseX, mouseY);
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.createButton.mouseClicked(lastClickX, lastClickY, glfwButton)) {
         return true;
      } else if (glfwButton == 0 && this.viewport.contains(lastClickX, lastClickY)) {
         for (SingleplayerMenuPage.Row row : this.rows) {
            if (row.playBounds.contains(lastClickX, lastClickY)) {
               this.play(row);
               return true;
            }

            if (row.removeBounds.contains(lastClickX, lastClickY)) {
               this.confirmDelete(row);
               return true;
            }

            if (row.bounds.contains(lastClickX, lastClickY)) {
               this.play(row);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double d, double d2, double d3) {
      return this.scroll.scroll(d3, ROW_HEIGHT + ROW_GAP);
   }

   private final class Row {
      private final LevelSummary summary;
      private final FaviconTexture icon;
      private BoundsData bounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData removeBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
      private BoundsData playBounds = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);

      private Row(LevelSummary summary) {
         this.summary = summary;
         this.icon = FaviconTexture.forWorld(MinecraftAccess.mc.getTextureManager(), summary.getLevelId());
         this.loadIcon();
      }

      private void loadIcon() {
         Path icon = this.summary.getIcon();
         if (icon != null && Files.isRegularFile(icon)) {
            try (InputStream inputStream = Files.newInputStream(icon)) {
               this.icon.upload(NativeImage.read(inputStream));
            } catch (Exception e) {
            }
         }
      }

      private void render(MenuDrawContext graphics, float mouseX, float mouseY, float floatValue, float snap, float width) {
         boolean contains = this.bounds.contains(mouseX, mouseY);
         MenuSkin.plate(
            graphics, floatValue, snap, width, SingleplayerMenuPage.ROW_HEIGHT, contains ? MenuTheme.plate().method7295(MenuTheme.track(), 0.5F) : MenuTheme.plate()
         );
         Identifier textureLocation = this.icon.textureLocation();
         float y = snap + (SingleplayerMenuPage.ROW_HEIGHT - SingleplayerMenuPage.ICON_SIZE) / 2.0F;
         RenderShaders.method5294(
            MatrixUtils.method664(graphics.pose()),
            textureLocation,
            floatValue + MenuSkin.EDGE,
            y,
            SingleplayerMenuPage.ICON_SIZE,
            SingleplayerMenuPage.ICON_SIZE,
            ProxyDotRadiusData.method8069(MenuSkin.tileRadius(SingleplayerMenuPage.ICON_SIZE)),
            ZenithColor.INSTANCE
         );
         float divider = MenuSkin.divider(graphics, floatValue + MenuSkin.EDGE + SingleplayerMenuPage.ICON_SIZE, snap, SingleplayerMenuPage.ROW_HEIGHT);
         Font medium = MenuTheme.medium(11.0F);
         Font regular = MenuTheme.regular(10.0F);
         float px = MenuTheme.px(30.0F);
         float centeredTextY = snap + (SingleplayerMenuPage.ROW_HEIGHT - px) / 2.0F;
         graphics.drawText(medium, this.summary.getLevelName(), divider, centeredTextY, MenuTheme.text());
         graphics.drawText(regular, this.details(), divider, centeredTextY + MenuTheme.px(18.0F), MenuTheme.textTertiary());
         float f = floatValue + width - MenuSkin.EDGE;
         float px2 = MenuTheme.px(12.0F);
         float y2 = snap + (SingleplayerMenuPage.ROW_HEIGHT - px2) / 2.0F;
         this.playBounds = new BoundsData(f - px2, y2, px2, px2);
         this.removeBounds = new BoundsData(f - px2 * 2.0F - MenuTheme.px(4.0F), y2, px2, px2);
         Font font = MenuIcons.REMOVE.font(12.0F);
         graphics.drawText(
            font,
            MenuIcons.REMOVE.glyph(),
            this.removeBounds.getFloatValue(),
            y2 + (px2 - font.height()) / 2.0F,
            this.removeBounds.contains(mouseX, mouseY) ? MenuTheme.danger() : MenuTheme.textTertiary()
         );
         Font font2 = MenuIcons.PLAY.font(12.0F);
         graphics.drawText(font2, MenuIcons.PLAY.glyph(), this.playBounds.getFloatValue(), y2 + (px2 - font2.height()) / 2.0F, MenuTheme.accent());
      }

      private String details() {
         String string = this.summary.getGameMode().getLongDisplayName().getString();
         String str = this.summary.getLastPlayed() <= 0L ? "-" : SingleplayerMenuPage.LAST_PLAYED.format(Instant.ofEpochMilli(this.summary.getLastPlayed()));
         return string + " - " + str;
      }
   }
}
