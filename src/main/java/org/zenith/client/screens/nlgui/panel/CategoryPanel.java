package org.zenith.client.screens.nlgui.panel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CategoryPanel extends Panel {
   private static final float ROW_HEIGHT = 15.0F;
   private static final float TEXT_INSET = 8.0F;
   private static final float MARKER_WIDTH = 2.0F;
   private static final float CARD_HEIGHT = 26.0F;
   private static final float CARD_INSET = 6.0F;
   private static final float CARD_PADDING = 5.0F;
   private static final float BOTTOM_BLOCK_HEIGHT = 38.0F;
   private static final float AVATAR_SIZE = 16.0F;
   private static final float STATUS_DOT = 4.0F;
   private static final float CHIP_HEIGHT = 6.5F;
   private static final ZenithColor DOT_RING = new ZenithColor("#121214");
   private static final float ICON_COLUMN = 10.0F;
   private static final float MAX_ROW_DELAY = 0.4F;
   private static final String BRAND = HoverInfo.spaced("ZENITH");
   private static final int[] ENABLED_BY_CATEGORY = new int[ModuleCategory.values().length];
   private final List<CategoryPanel.Row> rows = new ArrayList<>();
   private long daysLeftFor = Long.MIN_VALUE;
   private long daysLeftUntil;
   private long daysLeftValue;
   private String statusText = "";
   private String statusLanguage;
   private final Animation profileHover = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation profileSelect = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private BoundsData profileBounds;

   public CategoryPanel() {
      for (ModuleCategory category : ModuleCategory.values()) {
         if (category != ModuleCategory.THEMES) {
            this.rows.add(new CategoryPanel.Row(category.getName(), MenuGlyph.forCategory(category), new CategoryPanel.CategoryTarget(category)));
         }
      }

      for (NLMenuScreen.ElementsType type : NLMenuScreen.ElementsType.values()) {
         if (type != NLMenuScreen.ElementsType.CATEGORY) {
            this.rows.add(new CategoryPanel.Row(type.getName(), MenuGlyph.forSection(type), new CategoryPanel.SectionTarget(type)));
         }
      }
   }

   @Override
   public void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float previewAlpha, float f, float y) {
      this.render(customDrawContext, mouseX, mouseY, previewAlpha, f, y, 1.0F);
   }

   public void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float previewAlpha, float f, float y, float f2) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         if (!(previewAlpha <= 0.01F)) {
            float width = 100.0F;
            float heightPanel = NLMenuScreen.heightPanel;
            Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
            customDrawContext.drawText(font, BRAND, f + 8.0F, y + (23.0F - font.height()) / 2.0F, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            float y2 = y + 23.0F + GuiStyle.PADDING.intValue();
            int n = 0;

            for (int i = 0; i < this.rows.size(); i++) {
               if (this.rows.get(i).target instanceof CategoryPanel.CategoryTarget) {
                  n = i;
               }
            }

            float f3 = GuiStyle.PADDING.intValue() * 2.0F + 1.0F;
            float f4 = heightPanel - 23.0F - GuiStyle.PADDING.intValue() - f3 - 38.0F;
            float height = Math.min(15.0F, f4 / this.rows.size());
            countEnabled();

            for (int i = 0; i < this.rows.size(); i++) {
               CategoryPanel.Row row = this.rows.get(i);
               float rowEntrance2;
               if ((rowEntrance2 = rowEntrance(f2, i)) > 0.001F) {
                  row.render(customDrawContext, mouseX, mouseY, f - (1.0F - rowEntrance2) * 7.0F, y2, width, height, previewAlpha * rowEntrance2);
               } else {
                  row.bounds = null;
               }

               y2 += height;
               if (i == n) {
                  customDrawContext.drawRect(f + 8.0F, y2 + GuiStyle.PADDING.intValue(), width - 16.0F, 1.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha));
                  y2 += f3;
               }
            }

            float rowEntrance2;
            if ((rowEntrance2 = rowEntrance(f2, this.rows.size())) > 0.001F) {
               this.renderProfileCard(customDrawContext, mouseX, mouseY, f, y + heightPanel, width, previewAlpha * rowEntrance2);
            } else {
               this.profileBounds = null;
            }
         } else {
            this.profileBounds = null;

            for (CategoryPanel.Row row : this.rows) {
               row.bounds = null;
            }
         }
      }
   }

   private static float rowEntrance(float f, int size) {
      if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = Math.min(size * 0.045F, 0.4F);
         return Mth.clamp((f - f2) / 0.6F, 0.0F, 1.0F);
      }
   }

   private void renderProfileCard(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float f2, float width, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         float floatValue = f + 6.0F;
         float width2 = width - 12.0F;
         float y = f2 - 6.0F - 26.0F;
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.HUD_ROUND.intValue());
         this.profileBounds = new BoundsData(floatValue, y, width2, 26.0F);
         boolean contains = this.profileBounds.contains(mouseX, mouseY);
         boolean flag = Zenith.getInstance().getNLMenuScreen().getQuickMenuPanel() != null
            && Zenith.getInstance().getNLMenuScreen().getQuickMenuPanel().isExpanded();
         float f3 = this.profileHover.method2808(contains ? 1.0F : 0.0F);
         float hover = this.profileSelect.method2808(flag ? 1.0F : 0.0F);
         customDrawContext.drawRoundedRect(
            floatValue, y, width2, 26.0F, headerRadius, currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * (2.5F + f3 * 2.0F + hover * 1.5F))
         );
         ZenithColor zenithColor = currentStyle.getFieldBorder()
            .getZenithColor()
            .method7294(1.6F + f3 * 1.4F)
            .method7295(currentStyle.getPrimaryColor().getZenithColor().method7293(110), hover);
         customDrawContext.drawRoundedBorder(floatValue, y, width2, 26.0F, 0.15F, headerRadius, zenithColor.method7294(previewAlpha));
         Zenith.Entry entry = Zenith.getInstance().getEntry();
         String username = entry.getUsername() != null && !entry.getUsername().isBlank() ? entry.getUsername() : "guest";
         Zenith.Entry.Type type = entry.method1504();
         String lowerCase = type == null ? "user" : type.getName().toLowerCase(Locale.ROOT);
         long daysLeft;
         boolean flag2 = (daysLeft = this.daysLeft(entry)) == Long.MAX_VALUE;
         boolean flag3 = daysLeft == -1L;
         boolean flag4;
         boolean flag5 = !(flag4 = daysLeft == 0L) && !flag2 && !flag3 && daysLeft <= 365L;
         float floatValue2 = floatValue + 5.0F;
         float cardAvatarY = y + 5.0F;
         this.drawAvatar(customDrawContext, entry.getText2(), username, floatValue2, cardAvatarY, 16.0F, previewAlpha, currentStyle);
         float floatValue3 = floatValue2 + 16.0F - 4.0F + 0.5F;
         float y2 = cardAvatarY + 16.0F - 4.0F + 0.5F;
         customDrawContext.drawRoundedRect(floatValue3 - 1.0F, y2 - 1.0F, 6.0F, 6.0F, ProxyDotRadiusData.method8069(3.0F), DOT_RING.method7294(previewAlpha));
         customDrawContext.drawRoundedRect(
            floatValue3,
            y2,
            4.0F,
            4.0F,
            ProxyDotRadiusData.method8069(2.0F),
            (flag4 ? currentStyle.getHeartIcon() : currentStyle.getFriendColor()).getZenithColor().method7293(255).method7294(previewAlpha)
         );
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
         Font font3 = Fonts.NEW_MEDIUM.getFont(3.4F);
         float floatValue4 = floatValue2 + 16.0F + 5.0F;
         float f4 = floatValue + width2 - 5.0F;
         float f5 = font.height() + 2.0F + 6.5F;
         float centeredTextY;
         float y3 = (centeredTextY = cardAvatarY + (16.0F - f5) / 2.0F) + font.height() + 2.0F;
         customDrawContext.drawText(font, font.clip(username, f4 - floatValue4), floatValue4, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         String upperCase = lowerCase.toUpperCase(Locale.ROOT);
         float width3 = Math.min(font3.width(upperCase) + 5.0F, f4 - floatValue4);
         boolean flag6;
         ZenithColor zenithColor2 = (flag6 = type == null || type == Zenith.Entry.Type.USER)
            ? currentStyle.getTextSecondary().getZenithColor()
            : currentStyle.getPrimaryColor().getZenithColor();
         customDrawContext.drawRoundedRect(floatValue4, y3, width3, 6.5F, ProxyDotRadiusData.method8069(3.25F), zenithColor2.method7293(flag6 ? 20 : 38).method7294(previewAlpha));
         customDrawContext.drawText(
            font3,
            font3.clip(upperCase, width3 - 4.0F),
            floatValue4 + (width3 - font3.width(font3.clip(upperCase, width3 - 4.0F))) / 2.0F,
            y3 + (6.5F - font3.height()) / 2.0F,
            zenithColor2.method7294(previewAlpha)
         );
         String statusText = this.statusText;
         ZenithColor zenithColor3 = flag4
            ? currentStyle.getHeartIcon().getZenithColor()
            : (flag5 ? currentStyle.getPrimaryColor().getZenithColor() : currentStyle.getTextTertiary().getZenithColor());
         float floatValue5 = floatValue4 + width3 + 3.0F;
         customDrawContext.drawText(font2, font2.clip(statusText, f4 - floatValue5), floatValue5, y3 + (6.5F - font2.height()) / 2.0F, zenithColor3.method7294(previewAlpha));
         if (flag5) {
            float animationPosY = y + 26.0F - 3.0F;
            float width4 = f4 - floatValue4;
            customDrawContext.drawRoundedRectBatched(
               floatValue4, animationPosY, width4, 1.0F, ProxyDotRadiusData.method8069(0.5F), currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 3.0F)
            );
            customDrawContext.drawRoundedRectBatched(
               floatValue4,
               animationPosY,
               width4 * ((float)daysLeft / 365.0F),
               1.0F,
               ProxyDotRadiusData.method8069(0.5F),
               currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );
            customDrawContext.flushRoundedRects();
         }

         if (contains) {
            HoverInfo.request(
               username, Zenith.getInstance().getLocalization().translate("gui.profileCard.desc").replace("{uid}", entry.getText2()).replace("{role}", lowerCase)
            );
         }
      }
   }

   private void drawAvatar(MenuDrawContext customDrawContext, String string, String username, float floatValue, float cardAvatarY, float height, float previewAlpha, ZenithStyle currentStyle) {
      Identifier texture = CloudAvatarCache.get(string);
      if (texture != null) {
         customDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, height, height, ProxyDotRadiusData.method8069(height / 2.0F), ZenithColor.INSTANCE.method7294(previewAlpha));
      } else {
         customDrawContext.drawRoundedRectBatched(
            floatValue, cardAvatarY, height, height, ProxyDotRadiusData.method8069(height / 2.0F), currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.3F)
         );
         customDrawContext.flushRoundedRects();
         Font font = Fonts.NEW_MEDIUM.getFont(height * 0.42F);
         String upperCase = username.substring(0, 1).toUpperCase(Locale.ROOT);
         customDrawContext.drawText(
            font,
            upperCase,
            floatValue + (height - font.width(upperCase)) / 2.0F,
            cardAvatarY + (height - font.height()) / 2.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   private long daysLeft(Zenith.Entry entry) {
      long now = System.currentTimeMillis();
      long daysLeftFor = entry.method1510();
      String statusLanguage = Zenith.getInstance().getLocalization().translate("gui.sub.till");
      if (now >= this.daysLeftUntil || daysLeftFor != this.daysLeftFor || !statusLanguage.equals(this.statusLanguage)) {
         ZoneId systemDefault = ZoneId.systemDefault();
         LocalDate localDate = LocalDate.now(systemDefault);
         this.daysLeftValue = entry.method1506();
         this.daysLeftFor = daysLeftFor;
         this.daysLeftUntil = localDate.plusDays(1L).atStartOfDay(systemDefault).toInstant().toEpochMilli();
         this.statusLanguage = statusLanguage;
         this.statusText = statusText(entry, this.daysLeftValue);
      }

      return this.daysLeftValue;
   }

   private static String statusText(Zenith.Entry entry, long daysLeftValue) {
      Localization localization = Zenith.getInstance().getLocalization();
      if (daysLeftValue == Long.MAX_VALUE) {
         return localization.translate("gui.sub.lifetime");
      } else if (daysLeftValue == -1L) {
         return localization.translate("gui.sub.active");
      } else if (daysLeftValue == 0L) {
         return localization.translate("gui.sub.expired");
      } else {
         return daysLeftValue <= 365L ? daysLeftValue + " " + localization.translate("gui.sub.daysShort") : localization.translate("gui.sub.till").replace("{date}", entry.method1509());
      }
   }

   private static void countEnabled() {
      Arrays.fill(ENABLED_BY_CATEGORY, 0);

      for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
         if (!moduleValue.isHidden() && moduleValue.isEnabled() && moduleValue.getCategory() != null) {
            ENABLED_BY_CATEGORY[moduleValue.getCategory().ordinal()]++;
         }
      }
   }

   @Override
   public boolean onMouseClicked(double x, double y, Enum3 enum2) {
      if (this.profileBounds != null && this.profileBounds.contains(x, y)) {
         NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
         if (nlMenuScreen.getQuickMenuPanel() != null) {
            nlMenuScreen.getQuickMenuPanel().toggleExpanded();
         }

         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else {
         for (CategoryPanel.Row row : this.rows) {
            if (row.onMouseClicked(x, y)) {
               return true;
            }
         }

         return false;
      }
   }

   private static final class CategoryTarget implements CategoryPanel.Target {
      private final ModuleCategory category;

      public ModuleCategory category() {
         return this.category;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CategoryTarget other)) return false;
         return java.util.Objects.equals(this.category, other.category);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(category);
      }

      @Override
      public String toString() {
         return "CategoryTarget[" + "category=" + this.category + "]";
      }

      private CategoryTarget(ModuleCategory category) {
         this.category = category;
      }

      @Override
      public boolean isSelected() {
         NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
         return nlMenuScreen.getType() == NLMenuScreen.ElementsType.CATEGORY && nlMenuScreen.getGuiModulePanel().getCurrentCategory() == this.category;
      }

      @Override
      public void activate() {
         NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
         nlMenuScreen.resetSearch();
         nlMenuScreen.getGuiModulePanel().setCategory(this.category);
         nlMenuScreen.setType(NLMenuScreen.ElementsType.CATEGORY);
      }

      @Override
      public String counter() {
         return String.valueOf(CategoryPanel.ENABLED_BY_CATEGORY[this.category.ordinal()]);
      }
   }

   private static final class Row {
      private final String label;
      private final MenuGlyph icon;
      private final CategoryPanel.Target target;
      private final Animation selection = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private BoundsData bounds;

      private Row(String label, MenuGlyph icon, CategoryPanel.Target target) {
         this.label = label;
         this.icon = icon;
         this.target = target;
      }

      private void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float width, float height, float previewAlpha) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            this.bounds = new BoundsData(floatValue, y, width, height);
            boolean selected = this.target.isSelected();
            boolean contains = this.bounds.contains(mouseX, mouseY);
            float f = this.selection.method2808(selected ? 1.0F : 0.0F);
            float f2 = this.hover.method2808(contains ? 1.0F : 0.0F);
            if (f > 0.01F) {
               customDrawContext.drawRect(floatValue, y + 2.0F, 2.0F, height - 4.0F, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * f));
            }

            Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
            ZenithColor zenithColor = currentStyle.getTextSecondary()
               .getZenithColor()
               .method7295(currentStyle.getTextEnable().getZenithColor(), Math.max(f, f2))
               .method7294(previewAlpha);
            this.icon.draw(customDrawContext, floatValue + 8.0F, y + (height - 6.0F) / 2.0F, zenithColor);
            customDrawContext.drawText(font, this.label, floatValue + 8.0F + 10.0F, y + (height - font.height()) / 2.0F, zenithColor);
            String counter = this.target.counter();
            if (counter != null) {
               Font font2 = Fonts.NEW_REGULAR.getFont(5.0F);
               customDrawContext.drawText(
                  font2,
                  counter,
                  floatValue + width - 8.0F - font2.width(counter),
                  y + (height - font2.height()) / 2.0F,
                  currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
            }
         }
      }

      private boolean onMouseClicked(double x, double y) {
         if (this.bounds != null && this.bounds.contains(x, y)) {
            this.target.activate();
            if (this.target instanceof CategoryPanel.CategoryTarget categoryTarget) {
               ModuleCategory category = categoryTarget.category();
               Zenith.getInstance().getClientSounds().method1564(category.ordinal(), ModuleCategory.values().length);
            } else {
               Zenith.getInstance().getClientSounds().method1570(false);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private static final class SectionTarget implements CategoryPanel.Target {
      private final NLMenuScreen.ElementsType type;

      public NLMenuScreen.ElementsType type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SectionTarget other)) return false;
         return java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type);
      }

      @Override
      public String toString() {
         return "SectionTarget[" + "type=" + this.type + "]";
      }

      private SectionTarget(NLMenuScreen.ElementsType type) {
         this.type = type;
      }

      @Override
      public boolean isSelected() {
         return Zenith.getInstance().getNLMenuScreen().getType() == this.type;
      }

      @Override
      public void activate() {
         NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
         nlMenuScreen.resetSearch();
         nlMenuScreen.setType(this.type);
      }
   }

   private interface Target {
      boolean isSelected();

      void activate();

      default String counter() {
         return null;
      }
   }
}
