package org.zenith.client.screens.nlgui.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.Server;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class QuickMenuPanel extends Panel {
   private static final float ROW_HEIGHT = 15.0F;
   private static final float CAPTION_HEIGHT = 13.0F;
   private static final float OPTION_HEIGHT = 12.0F;
   private static final float ICON_COLUMN = 10.0F;
   private static final float INSET = 8.0F;
   private static final float ACCOUNT_HEIGHT = 16.0F;
   private static final float ACCOUNT_AVATAR = 16.0F;
   private static final float ACCOUNT_BLOCK = 16.0F + GuiStyle.PADDING.intValue() * 4.0F;
   private static final float TOGGLE_WIDTH = 12.0F;
   private static final float CHEVRON_SIZE = 5.0F;
   private static final float NUMBER_HEIGHT = 24.0F;
   private static final float TRACK_OFFSET = 17.0F;
   private static final float TRACK_GRAB = 5.0F;
   private static final float HANDLE_GRAB = 5.0F;
   private static final float PIP = 2.0F;
   private static final float PIP_GAP = 1.5F;
   private static final float OPTION_INDENT = 6.0F;
   private static final float SCROLL_SPEED = 18.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private static final float MAX_ROW_DELAY = 0.4F;
   private final Animation openAnimation = new Animation(380L, 0.0F, Easing.INSTANCE15);
   private final List<QuickMenuPanel.Row> rows = new ArrayList<>();
   private boolean expanded;
   private QuickMenuPanel.Row openRow;
   private BoundsData bounds;
   private BoundsData contentBounds;
   private BoundsData closeBounds;
   private BoundsData accountBounds;
   private float scroll;
   private float scrollTarget;

   public QuickMenuPanel() {
      this.build();
   }

   private static String t(String key) {
      return Zenith.getInstance().getLocalization().translate(key);
   }

   private void build() {
      NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
      ProfilePanel profilePanel = nlMenuScreen.getProfilePanel();
      ClientPanel clientPanel = nlMenuScreen.getClientPanel();
      SliderSetting setting = clientPanel.getGuiScale().getSetting();
      SliderSetting sliderSetting = clientPanel.getBlurStrength().getSetting();
      BooleanSetting booleanSetting = clientPanel.getRenderDescription().getSetting();
      BooleanSetting setting2 = clientPanel.getRenderIcon().getSetting();
      BooleanSetting setting3 = profilePanel.getSteamMode().getSetting();
      BooleanSetting setting4 = profilePanel.getDiscordRPC().getSetting();
      QuickMenuPanel.Row mode = this.mode("quick.language", MenuGlyph.MODE);

      for (String text : Zenith.getInstance().getLocalization().method1732()) {
         mode.option(
            () -> languageTitle(text),
            () -> text.equalsIgnoreCase(Zenith.getInstance().getLocalization().getLanguageName()),
            () -> Zenith.getInstance().getLocalization().method1728(text)
         );
      }

      this.number("quick.menuScale", MenuGlyph.SIZE_GLYPH, setting, () -> (int)setting.getValue3() + "%");
      this.number("quick.blur", MenuGlyph.OPACITY, sliderSetting, () -> (int)sliderSetting.getValue3() == 0 ? t("quick.value.off") : (int)sliderSetting.getValue3() + "px");
      this.toggle("quick.description", MenuGlyph.TEXT, booleanSetting::isEnabled, booleanSetting::toggle);
      this.toggle("quick.icons", MenuGlyph.ITEMS, setting2::isEnabled, setting2::toggle);
      this.caption("quick.caption.privacy");
      this.toggle("quick.streamer", MenuGlyph.SHIELD, setting3::isEnabled, setting3::toggle);
      this.toggle("quick.discord", MenuGlyph.NETWORK, setting4::isEnabled, setting4::toggle);
      this.caption("quick.caption.ai");
      Server server = Server.getInstance();
      this.toggle("quick.mcp", MenuGlyph.NETWORK, server::isRunning, () -> server.setEnabled(!server.isRunning()));
      this.action("quick.mcpCopy", MenuGlyph.COPY, () -> t("quick.value.copy"), () -> {
         if (!server.isRunning()) {
            server.setEnabled(true);
         }

         Minecraft.getInstance().keyboardHandler.setClipboard(server.method216());
      });
      this.action("quick.mcpToken", MenuGlyph.RESET, () -> t("quick.value.reset"), server::method218);
      this.action("quick.scriptDocs", MenuGlyph.TEXT, () -> openLink(RegionUtil.method2038().get(0) + "/docs/addons"));
      this.caption("quick.caption.links");
      this.action("quick.webProfile", MenuGlyph.PROFILE, () -> openLink(RegionUtil.method2038().get(0) + "/dashboard"));
      this.action("quick.folder", MenuGlyph.CONFIGS, this::openFolder);
      this.action("quick.devlog", MenuGlyph.CHAT, () -> openLink("https://t.me/zenithdlcdevlog"));
      this.action("quick.chat", MenuGlyph.CHAT, () -> openLink("https://t.me/+J-lV24z-Wmg1M2Y6"));
      this.caption("quick.caption.about");
      QuickMenuPanel.Row submenu = this.submenu("quick.about", MenuGlyph.CLIENT, () -> "3.5");
      submenu.info("quick.about.version", () -> "3.5");
      submenu.info("quick.about.role", () -> Zenith.getInstance().getEntry().method1504().getName());
      PlayTimeTracker playTimeTracker = Zenith.getInstance().getPlayTimeTracker();
      submenu.info("quick.playtime.session", () -> PlayTimeTracker.method1402(playTimeTracker.method1399()));
      submenu.info("quick.playtime.week", () -> PlayTimeTracker.method1402(playTimeTracker.method1400()));
      submenu.info("quick.playtime.total", () -> PlayTimeTracker.method1402(playTimeTracker.method1401()));
   }

   private void caption(String key) {
      this.rows.add(QuickMenuPanel.Row.caption(key));
   }

   private void toggle(String key, MenuGlyph icon, BooleanSupplier state, Runnable click) {
      this.rows.add(QuickMenuPanel.Row.toggle(key, icon, state, click));
   }

   private void action(String key, MenuGlyph icon, Runnable click) {
      this.rows.add(QuickMenuPanel.Row.action(key, icon, click));
   }

   private void action(String key, MenuGlyph icon, Supplier<String> supplier, Runnable click) {
      this.rows.add(QuickMenuPanel.Row.action(key, icon, supplier, click));
   }

   private QuickMenuPanel.Row submenu(String key, MenuGlyph icon, Supplier<String> supplier) {
      QuickMenuPanel.Row submenu = QuickMenuPanel.Row.submenu(key, icon, supplier);
      this.rows.add(submenu);
      return submenu;
   }

   private QuickMenuPanel.Row number(String key, MenuGlyph icon, SliderSetting setting, Supplier<String> supplier) {
      QuickMenuPanel.Row number = QuickMenuPanel.Row.number(key, icon, setting, supplier);
      this.rows.add(number);
      return number;
   }

   private QuickMenuPanel.Row mode(String key, MenuGlyph icon) {
      QuickMenuPanel.Row mode = QuickMenuPanel.Row.mode(key, icon);
      this.rows.add(mode);
      return mode;
   }

   public boolean isRender() {
      return this.bounds != null;
   }

   public void setExpanded(boolean expanded) {
      if (this.expanded != expanded) {
         this.expanded = expanded;
         this.openRow = null;
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         if (expanded) {
            this.openAnimation.reset(0.0F);
            this.openAnimation.method2810(1.0F);
         }
      }
   }

   public void toggleExpanded() {
      this.setExpanded(!this.expanded);
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public boolean collapseOpenRow() {
      if (this.openRow == null) {
         return false;
      } else {
         this.openRow = null;
         return true;
      }
   }

   @Override
   public void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float previewAlpha, float floatValue, float y) {
      this.renderColumn(customDrawContext, mouseX, mouseY, previewAlpha, floatValue, y, 100.0F);
   }

   public void renderColumn(MenuDrawContext customDrawContext, int mouseX, int mouseY, float previewAlpha, float floatValue, float y, float leftWidthPanel) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (!(previewAlpha <= 0.01F) && currentStyle != null) {
         float heightPanel = NLMenuScreen.heightPanel;
         this.bounds = new BoundsData(floatValue, y, leftWidthPanel, heightPanel);
         this.renderTopBar(customDrawContext, mouseX, mouseY, floatValue, y, leftWidthPanel, previewAlpha, currentStyle);
         this.renderAccount(customDrawContext, mouseX, mouseY, floatValue, y + heightPanel, leftWidthPanel, previewAlpha, currentStyle);
         float f = y + 23.0F + GuiStyle.PADDING.intValue();
         float height = y + heightPanel - ACCOUNT_BLOCK - f;
         this.contentBounds = new BoundsData(floatValue, f, leftWidthPanel, height);
         float f2 = 0.0F;

         for (QuickMenuPanel.Row row : this.rows) {
            f2 += row.height();
         }

         this.clampScroll(f2, height);
         this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
         float f3 = this.openAnimation.method2808(1.0F);
         customDrawContext.enableScissor(floatValue, f, floatValue + leftWidthPanel, f + height);
         float y2 = f + this.scroll;
         int n = 0;

         for (QuickMenuPanel.Row row : this.rows) {
            float rowEntrance2;
            if ((rowEntrance2 = rowEntrance(f3, n++)) > 0.001F) {
               row.render(customDrawContext, mouseX, mouseY, floatValue + (1.0F - rowEntrance2) * 7.0F, y2, leftWidthPanel, previewAlpha * rowEntrance2, currentStyle, row == this.openRow);
            } else {
               row.clearBounds();
            }

            y2 += row.height();
         }

         customDrawContext.disableScissor();
         if (f2 > height) {
            float height2 = Math.max(12.0F, height * (height / f2));
            float f4;
            float f5 = (f4 = f2 - height) <= 0.0F ? 0.0F : Mth.clamp(-this.scroll / f4, 0.0F, 1.0F);
            customDrawContext.drawRect(floatValue + leftWidthPanel - 2.0F, f + (height - height2) * f5, 1.0F, height2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.6F));
         }
      } else {
         this.clearBounds();
      }
   }

   private void clampScroll(float f, float height) {
      if (f <= height) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float min = height - f;
         this.scrollTarget = Mth.clamp(this.scrollTarget, min, 0.0F);
         this.scroll = Mth.clamp(this.scroll, min, 0.0F);
      }
   }

   private static float rowEntrance(float f, int n) {
      if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = Math.min(n * 0.045F, 0.4F);
         return Mth.clamp((f - f2) / 0.6F, 0.0F, 1.0F);
      }
   }

   private void clearBounds() {
      this.bounds = null;
      this.contentBounds = null;
      this.closeBounds = null;
      this.accountBounds = null;

      for (QuickMenuPanel.Row row : this.rows) {
         row.clearBounds();
         row.dragging = false;
      }
   }

   private void renderTopBar(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float leftWidthPanel, float previewAlpha, ZenithStyle currentStyle) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
      customDrawContext.drawText(
         font, HoverInfo.spaced("ZENITH"), floatValue + 8.0F, y + (23.0F - font.height()) / 2.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      float f = floatValue + leftWidthPanel - 8.0F - 6.0F;
      float centeredTextY = y + 8.5F;
      this.closeBounds = new BoundsData(f - 4.0F, centeredTextY - 4.0F, 14.0F, 14.0F);
      MenuGlyph.CLOSE
         .draw(customDrawContext, f, centeredTextY, (this.closeBounds.contains(mouseX, mouseY) ? currentStyle.getTextEnable() : currentStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha));
   }

   private void renderAccount(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float f, float leftWidthPanel, float previewAlpha, ZenithStyle currentStyle) {
      Zenith.Entry entry = Zenith.getInstance().getEntry();
      String username = entry.getUsername() != null && !entry.getUsername().isBlank() ? entry.getUsername() : "guest";
      float floatValue2 = floatValue + 8.0F;
      float width = leftWidthPanel - 16.0F;
      float cardAvatarY = f - GuiStyle.PADDING.intValue() * 2.0F - 16.0F;
      customDrawContext.drawRect(floatValue2, cardAvatarY - GuiStyle.PADDING.intValue() * 2.0F, width, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha));
      this.accountBounds = new BoundsData(floatValue, cardAvatarY - 2.0F, leftWidthPanel, 20.0F);
      Identifier texture = CloudAvatarCache.get(entry.getText2());
      if (texture != null) {
         customDrawContext.drawRoundedTexture(texture, floatValue2, cardAvatarY, 16.0F, 16.0F, ProxyDotRadiusData.method8069(8.0F), ZenithColor.INSTANCE.method7294(previewAlpha));
      } else {
         customDrawContext.drawRoundedRect(floatValue2, cardAvatarY, 16.0F, 16.0F, ProxyDotRadiusData.method8069(8.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.3F));
         Font font = Fonts.NEW_MEDIUM.getFont(6.72F);
         String upperCase = username.substring(0, 1).toUpperCase(Locale.ROOT);
         customDrawContext.drawText(
            font,
            upperCase,
            floatValue2 + (16.0F - font.width(upperCase)) / 2.0F,
            cardAvatarY + (16.0F - font.height()) / 2.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
      }

      Font font = Fonts.NEW_MEDIUM.getFont(6.4F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
      float floatValue3 = floatValue2 + 16.0F + GuiStyle.PADDING.intValue() - 1.0F;
      float f2 = floatValue2 + width;
      String lowerCase = entry.method1504() == null ? "user" : entry.method1504().getName().toLowerCase(Locale.ROOT);
      customDrawContext.drawText(font, font.clip(username, f2 - floatValue3), floatValue3, cardAvatarY + 0.4F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      customDrawContext.drawText(
         font2, font2.clip(lowerCase, f2 - floatValue3), floatValue3, cardAvatarY + 16.0F - font2.height() - 0.4F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
   }

   @Override
   public boolean onMouseClicked(double x, double y, Enum3 enum2) {
      if (this.expanded && this.bounds != null && this.bounds.contains(x, y)) {
         if (this.closeBounds != null && this.closeBounds.contains(x, y)) {
            this.setExpanded(false);
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else if (this.accountBounds != null && this.accountBounds.contains(x, y)) {
            this.setExpanded(false);
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else {
            if (this.openRow != null) {
               for (QuickMenuPanel.Option option : this.openRow.options) {
                  if (option.bounds != null && option.bounds.contains(x, y) && option.clickable()) {
                     option.click.run();
                     Zenith.getInstance().getClientSounds().method1570(false);
                     return true;
                  }
               }
            }

            for (QuickMenuPanel.Row row : this.rows) {
               if (row.kind == QuickMenuPanel.Row.Kind.NUMBER && row.trackBounds != null && row.trackBounds.contains(x, y)) {
                  row.startDrag(x);
                  return true;
               }

               if (row.bounds != null && row.bounds.contains(x, y)) {
                  switch (row.kind) {
                     case SUBMENU:
                        this.openRow = this.openRow == row ? null : row;
                     case NUMBER:
                        break;
                     case MODE:
                        row.cycle();
                        break;
                     default:
                        if (row.click != null) {
                           row.click.run();
                           this.openRow = null;
                        }
                  }

                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double x, double y, Enum3 enum2) {
      boolean flag = false;

      for (QuickMenuPanel.Row row : this.rows) {
         if (row.dragging) {
            row.dragging = false;
            flag = true;
         }
      }

      return flag;
   }

   public boolean onMouseDragged(double offsetX, float guiScale) {
      for (QuickMenuPanel.Row row : this.rows) {
         if (row.dragging) {
            row.dragBy(offsetX, guiScale);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.expanded && this.contentBounds != null && this.contentBounds.contains(mouseX, mouseY)) {
         this.scrollTarget += (float)verticalAmount * 18.0F;
         return true;
      } else {
         return false;
      }
   }

   private static String languageTitle(String text) {
      String lowerCase = text == null ? "" : text.toLowerCase(Locale.ROOT);

      return switch (lowerCase) {
         case "ru" -> "Русский";
         case "pl" -> "Polski";
         case "tr" -> "Türkçe";
         default -> "English";
      };
   }

   private void openFolder() {
      try {
         Runtime.getRuntime().exec("explorer.exe \"" + Zenith.file.getAbsolutePath() + "\"");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static void openLink(String str) {
      try {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + str);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static final class Option {
      private final Supplier<String> label;
      private final BooleanSupplier selected;
      private final Runnable click;
      private final Supplier<String> value;
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private BoundsData bounds;

      private Option(Supplier<String> label, BooleanSupplier selected, Runnable click, Supplier<String> supplier) {
         this.label = label;
         this.selected = selected;
         this.click = click;
         this.value = supplier;
      }

      private boolean clickable() {
         return this.click != null;
      }

      private void renderOption(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float leftWidthPanel, float previewAlpha, ZenithStyle currentStyle) {
         this.bounds = new BoundsData(floatValue, y, leftWidthPanel, 12.0F);
         boolean flag = this.clickable() && this.bounds.contains(mouseX, mouseY);
         boolean flag2 = this.selected != null && this.selected.getAsBoolean();
         float f = this.hover.method2808(flag ? 1.0F : 0.0F);
         float floatValue2 = floatValue + 8.0F + 6.0F;
         float f2 = floatValue + leftWidthPanel - 8.0F;
         if (flag2 || f > 0.002F) {
            customDrawContext.drawRoundedRect(
               floatValue2 - 3.0F,
               y + 0.5F,
               f2 - floatValue2 + 5.0F,
               11.0F,
               ProxyDotRadiusData.method8069(3.0F),
               currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (flag2 ? 1.0F : f * 0.5F))
            );
         }

         if (flag2) {
            customDrawContext.drawRect(floatValue, y + 2.0F, 2.0F, 8.0F, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
         }

         Font font = Fonts.NEW_REGULAR.getFont(4.8F);
         float centeredTextY = y + (12.0F - font.height()) / 2.0F;
         if (this.value != null) {
            String text = this.value.get();
            Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
            float width = font2.width(text);
            customDrawContext.drawText(font2, text, f2 - width, y + (12.0F - font2.height()) / 2.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            customDrawContext.drawText(
               font,
               font.clip(this.label.get(), f2 - width - GuiStyle.PADDING.intValue() - floatValue2),
               floatValue2,
               centeredTextY,
               currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
            );
         } else {
            ZenithColor zenithColor = (!flag2 && !flag ? currentStyle.getTextSecondary() : currentStyle.getTextEnable()).getZenithColor().method7294(previewAlpha);
            customDrawContext.drawText(font, font.clip(this.label.get(), f2 - floatValue2), floatValue2, centeredTextY, zenithColor);
         }
      }
   }

   private static final class Row {
      private final QuickMenuPanel.Row.Kind kind;
      private final String key;
      private final MenuGlyph icon;
      private final BooleanSupplier state;
      private final Supplier<String> value;
      private final Runnable click;
      private final SliderSetting number;
      private float shownNumber = Float.NaN;
      private boolean dragging;
      private float dragValue;
      private BoundsData trackBounds;
      private final List<QuickMenuPanel.Option> options = new ArrayList<>();
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private final Animation toggleAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private final Animation expandAnimation = new Animation(180L, 0.0F, Easing.INSTANCE13);
      private BoundsData bounds;

      private Row(QuickMenuPanel.Row.Kind kind, String key, MenuGlyph icon, BooleanSupplier state, Supplier<String> supplier, Runnable click, SliderSetting number) {
         this.kind = kind;
         this.key = key;
         this.icon = icon;
         this.state = state;
         this.value = supplier;
         this.click = click;
         this.number = number;
      }

      private static QuickMenuPanel.Row caption(String key) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.CAPTION, key, null, null, null, null, null);
      }

      private static QuickMenuPanel.Row toggle(String key, MenuGlyph icon, BooleanSupplier state, Runnable click) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.TOGGLE, key, icon, state, null, click, null);
      }

      private static QuickMenuPanel.Row action(String key, MenuGlyph icon, Runnable click) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.ACTION, key, icon, null, null, click, null);
      }

      private static QuickMenuPanel.Row action(String key, MenuGlyph icon, Supplier<String> supplier, Runnable click) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.ACTION, key, icon, null, supplier, click, null);
      }

      private static QuickMenuPanel.Row submenu(String key, MenuGlyph icon, Supplier<String> supplier) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.SUBMENU, key, icon, null, supplier, null, null);
      }

      private static QuickMenuPanel.Row number(String key, MenuGlyph icon, SliderSetting setting, Supplier<String> supplier) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.NUMBER, key, icon, null, supplier, null, setting);
      }

      private static QuickMenuPanel.Row mode(String key, MenuGlyph icon) {
         return new QuickMenuPanel.Row(QuickMenuPanel.Row.Kind.MODE, key, icon, null, null, null, null);
      }

      private QuickMenuPanel.Row option(Supplier<String> label, BooleanSupplier selected, Runnable click) {
         this.options.add(new QuickMenuPanel.Option(label, selected, click, null));
         return this;
      }

      private QuickMenuPanel.Row info(String key, Supplier<String> supplier) {
         this.options.add(new QuickMenuPanel.Option(() -> QuickMenuPanel.t(key), null, null, supplier));
         return this;
      }

      private float height() {
         return switch (this.kind) {
            case CAPTION -> 13.0F;
            case NUMBER -> 24.0F;
            case MODE -> 15.0F;
            default -> 15.0F + this.options.size() * 12.0F * this.expandAnimation.getValue();
         };
      }

      private float ownHeight() {
         return this.kind == QuickMenuPanel.Row.Kind.NUMBER ? 24.0F : 15.0F;
      }

      private void clearBounds() {
         this.bounds = null;
         this.trackBounds = null;

         for (QuickMenuPanel.Option option : this.options) {
            option.bounds = null;
         }
      }

      private void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float leftWidthPanel, float previewAlpha, ZenithStyle currentStyle, boolean flag) {
         if (this.kind == QuickMenuPanel.Row.Kind.CAPTION) {
            this.clearBounds();
            Font font = Fonts.NEW_MEDIUM.getFont(3.8F);
            customDrawContext.drawText(
               font,
               HoverInfo.spaced(QuickMenuPanel.t(this.key).toUpperCase(Locale.ROOT)),
               floatValue + 8.0F,
               y + (13.0F - font.height()) / 2.0F + 1.5F,
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.9F)
            );
         } else {
            float ownHeight = this.ownHeight();
            this.bounds = new BoundsData(floatValue, y, leftWidthPanel, ownHeight);
            boolean contains = this.bounds.contains(mouseX, mouseY);
            float f = this.hover.method2808(contains ? 1.0F : 0.0F);
            float f2 = this.expandAnimation.method2808(flag ? 1.0F : 0.0F);
            float hover = Math.max(Math.max(f, f2), this.dragging ? 1.0F : 0.0F);
            if (hover > 0.002F) {
               customDrawContext.drawRoundedRect(
                  floatValue + 2.0F,
                  y,
                  leftWidthPanel - 4.0F,
                  ownHeight,
                  ProxyDotRadiusData.method8069(3.0F),
                  currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * hover * 0.7F)
               );
            }

            if (contains) {
               HoverInfo.request(QuickMenuPanel.t(this.key), QuickMenuPanel.t(this.key + ".desc"));
            }

            Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.01F);
            ZenithColor zenithColor = currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha);
            float floatValue2 = floatValue + 8.0F;
            float f3 = this.kind == QuickMenuPanel.Row.Kind.NUMBER ? 15.0F : ownHeight;
            if (this.icon != null && Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
               this.icon.draw(customDrawContext, floatValue2, y + (f3 - 6.0F) / 2.0F, zenithColor);
               floatValue2 += 10.0F;
            }
            String text = switch (this.kind) {
               case ACTION -> this.value == null ? QuickMenuPanel.t("quick.value.open") : (String)this.value.get();
               case SUBMENU, NUMBER -> this.value == null ? "" : (String)this.value.get();
               case MODE -> this.currentOptionLabel();
               default -> "";
            };

            float f4 = switch (this.kind) {
               case TOGGLE -> 12.0F;
               default -> font2.width(text);
               case SUBMENU -> 8.0F + font2.width(text);
               case MODE -> this.pipsWidth() + GuiStyle.PADDING.intValue() + font2.width(text);
            };
            float width = floatValue + leftWidthPanel - 8.0F - f4 - GuiStyle.PADDING.intValue() - floatValue2;
            customDrawContext.drawText(font, font.clip(QuickMenuPanel.t(this.key), width), floatValue2, y + (f3 - font.height()) / 2.0F, zenithColor);
            float centeredTextY = y + (f3 - font2.height()) / 2.0F;
            float f5 = floatValue + leftWidthPanel - 8.0F;
            switch (this.kind) {
               case TOGGLE:
                  this.renderToggle(customDrawContext, f5, y, previewAlpha, currentStyle);
                  break;
               case ACTION:
                  customDrawContext.drawText(font2, text, f5 - font2.width(text), centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
                  break;
               case SUBMENU:
                  float f6 = f5 - 5.0F;
                  MenuGlyph.drawChevron(customDrawContext, f6, y + ownHeight / 2.0F, f2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
                  customDrawContext.drawText(font2, text, f6 - 3.0F - font2.width(text), centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
                  break;
               case NUMBER:
                  customDrawContext.drawText(font2, text, f5 - font2.width(text), centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
                  this.renderTrack(customDrawContext, floatValue, y, leftWidthPanel, previewAlpha, currentStyle);
                  break;
               case MODE:
                  float width2 = font2.width(text);
                  customDrawContext.drawText(font2, text, f5 - width2, centeredTextY, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
                  this.renderPips(customDrawContext, f5 - width2 - GuiStyle.PADDING.intValue(), y + ownHeight / 2.0F, previewAlpha, currentStyle);
            }

            if (f2 <= 0.001F) {
               for (QuickMenuPanel.Option option : this.options) {
                  option.bounds = null;
               }
            } else {
               float y2 = y + 15.0F;
               float f6 = this.options.size() * 12.0F * f2;
               customDrawContext.enableScissor(floatValue, y2, floatValue + leftWidthPanel, y2 + f6);
               float y3 = y2;

               for (QuickMenuPanel.Option option : this.options) {
                  option.renderOption(customDrawContext, mouseX, mouseY, floatValue, y3, leftWidthPanel, previewAlpha, currentStyle);
                  y3 += 12.0F;
               }

               customDrawContext.disableScissor();
            }
         }
      }

      private void renderTrack(MenuDrawContext customDrawContext, float floatValue, float y, float leftWidthPanel, float previewAlpha, ZenithStyle currentStyle) {
         if (this.number == null) {
            this.trackBounds = null;
         } else {
            float f = floatValue + 8.0F;
            float width = leftWidthPanel - 16.0F;
            float y2 = y + 17.0F;
            this.trackBounds = new BoundsData(f - 5.0F, y2 - 5.0F, width + 10.0F, 10.0F);
            float smoothNumber = this.smoothNumber();
            float f2 = this.number.getMax() - this.number.getMin();
            float f3 = f2 <= 0.0F ? 0.0F : Mth.clamp((smoothNumber - this.number.getMin()) / f2, 0.0F, 1.0F);
            customDrawContext.drawRect(f, y2, width, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 3.0F));
            customDrawContext.drawRect(f, y2, width * f3, 1.0F, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
            customDrawContext.drawRect(f + width * f3 - 0.75F, y2 - 2.0F, 1.5F, 5.0F, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
         }
      }

      private float smoothNumber() {
         float shownNumber = this.number.getValue3();
         if (Float.isNaN(this.shownNumber)) {
            this.shownNumber = shownNumber;
            return shownNumber;
         } else {
            this.shownNumber = Mth.lerp(0.3F, this.shownNumber, shownNumber);
            if (Math.abs(shownNumber - this.shownNumber) < this.number.getFloatValue2() / 100.0F) {
               this.shownNumber = shownNumber;
            }

            return this.shownNumber;
         }
      }

      private void renderPips(MenuDrawContext customDrawContext, float f, float f2, float previewAlpha, ZenithStyle currentStyle) {
         int size = this.options.size();
         if (size > 1) {
            float f3 = f - this.pipsWidth();
            int currentOptionIndex = this.currentOptionIndex();
            ZenithColor zenithColor = currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 4.0F);
            ZenithColor zenithColor2 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);

            for (int i = 0; i < size; i++) {
               customDrawContext.drawRect(f3 + i * 3.5F, f2 - 1.0F, 2.0F, 2.0F, i == currentOptionIndex ? zenithColor2 : zenithColor);
            }
         }
      }

      private float pipsWidth() {
         int size = this.options.size();
         return size <= 1 ? 0.0F : size * 2.0F + (size - 1) * 1.5F;
      }

      private int currentOptionIndex() {
         for (int i = 0; i < this.options.size(); i++) {
            QuickMenuPanel.Option option = this.options.get(i);
            if (option.selected != null && option.selected.getAsBoolean()) {
               return i;
            }
         }

         return 0;
      }

      private String currentOptionLabel() {
         return this.options.isEmpty() ? "" : this.options.get(this.currentOptionIndex()).label.get();
      }

      private void cycle() {
         if (!this.options.isEmpty()) {
            QuickMenuPanel.Option option = this.options.get((this.currentOptionIndex() + 1) % this.options.size());
            if (option.click != null) {
               option.click.run();
            }
         }
      }

      private boolean startDrag(double x) {
         if (this.number != null && this.trackBounds != null) {
            float f = this.number.getMax() - this.number.getMin();
            float f2 = f <= 0.0F ? 0.0F : (this.number.getValue3() - this.number.getMin()) / f;
            float pointerAt = this.pointerAt(x);
            this.dragValue = Math.abs(pointerAt - f2) * this.trackWidth() <= 5.0F ? f2 : pointerAt;
            this.dragging = true;
            this.applyNormalized(this.dragValue);
            return true;
         } else {
            return false;
         }
      }

      private void dragBy(double offsetX, float guiScale) {
         if (this.number != null && this.trackBounds != null) {
            float trackWidth = this.trackWidth();
            if (!(trackWidth <= 0.0F) && !(guiScale <= 0.0F)) {
               this.dragValue = Mth.clamp(this.dragValue + (float)(offsetX / guiScale) / trackWidth, 0.0F, 1.0F);
               this.applyNormalized(this.dragValue);
            }
         }
      }

      private float trackWidth() {
         return this.trackBounds == null ? 0.0F : this.trackBounds.width() - 10.0F;
      }

      private float pointerAt(double x) {
         float f = this.trackBounds.getFloatValue() + 5.0F;
         float trackWidth = this.trackWidth();
         return trackWidth <= 0.0F ? 0.0F : Mth.clamp((float)(x - f) / trackWidth, 0.0F, 1.0F);
      }

      private void applyNormalized(float dragValue) {
         float f = this.number.getMin() + (this.number.getMax() - this.number.getMin()) * dragValue;
         float f2 = this.number.getFloatValue2();
         if (f2 > 0.0F) {
            f = Math.round(f / f2) * f2;
         }

         float f3 = this.number.getValue3();
         this.number.method2400(Mth.clamp(f, this.number.getMin(), this.number.getMax()));
         if (this.number.getValue3() != f3) {
            Zenith.getInstance().getClientSounds().method1571(dragValue);
         }
      }

      private void renderToggle(MenuDrawContext customDrawContext, float f, float y, float previewAlpha, ZenithStyle currentStyle) {
         float width = 12.0F;
         float height = 6.5F;
         float floatValue = f - width;
         float y2 = y + (15.0F - height) / 2.0F;
         boolean flag = this.state != null && this.state.getAsBoolean();
         float hover = this.toggleAnimation.method2808(flag ? 1.0F : 0.0F);
         customDrawContext.drawRoundedRect(
            floatValue,
            y2,
            width,
            height,
            ProxyDotRadiusData.method8069(height / 2.0F),
            currentStyle.getFieldSurfaceBackground()
               .getZenithColor()
               .method7295(currentStyle.getPrimaryColor().getZenithColor(), hover)
               .method7294(previewAlpha * (1.0F + (1.0F - hover)))
         );
         float width2 = height - 2.0F;
         float floatValue2 = floatValue + 1.0F + (width - width2 - 2.0F) * hover;
         customDrawContext.drawRoundedRect(
            floatValue2,
            y2 + 1.0F,
            width2,
            width2,
            ProxyDotRadiusData.method8069(width2 / 2.0F),
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha * (0.55F + 0.45F * hover))
         );
      }

      private static enum Kind {
         CAPTION,
         TOGGLE,
         ACTION,
         SUBMENU,
         NUMBER,
         MODE;
      }
   }
}
