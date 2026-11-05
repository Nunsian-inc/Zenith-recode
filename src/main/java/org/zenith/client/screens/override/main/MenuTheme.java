package org.zenith.client.screens.override.main;

import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;

public final class MenuTheme {
   public static final float SCALE = 0.5F;

   private MenuTheme() {
   }

   public static float px(float f) {
      return f * 0.5F;
   }

   public static String t(String labelKey) {
      return Zenith.getInstance().getLocalization().translate(labelKey);
   }

   public static ZenithStyle style() {
      return Zenith.getInstance().getStyleManager().getCurrentStyle();
   }

   public static ZenithColor accent() {
      return style().getPrimaryColor().getZenithColor();
   }

   public static ZenithColor accentSecondary() {
      return style().getSecondaryPrimaryColor().getZenithColor();
   }

   public static ZenithColor panel() {
      return style().getHudBackground().getZenithColor();
   }

   public static ZenithColor plate() {
      return style().getHeaderHudBackground().getZenithColor();
   }

   public static ZenithColor surface() {
      return style().getHudBackground().getZenithColor();
   }

   public static ZenithColor border() {
      return style().getFieldBorder().getZenithColor();
   }

   public static ZenithColor borderSubtle() {
      return border().method7294(0.5F);
   }

   public static ZenithColor track() {
      return style().getDisableActiveBg().getZenithColor();
   }

   public static ZenithColor text() {
      return style().getTextEnable().getZenithColor();
   }

   public static ZenithColor textSecondary() {
      return style().getTextSecondary().getZenithColor();
   }

   public static ZenithColor textTertiary() {
      return style().getTextTertiary().getZenithColor();
   }

   public static ZenithColor danger() {
      return style().getHeartIcon().getZenithColor();
   }

   public static ZenithColor onAccent() {
      return ZenithColor.INSTANCE;
   }

   public static Font regular(float f) {
      return Fonts.NEW_REGULAR.getFont(px(f));
   }

   public static Font medium(float f) {
      return Fonts.NEW_MEDIUM.getFont(px(f));
   }

   public static Font semibold(float f) {
      return Fonts.NEW_SEMIBOLD.getFont(px(f));
   }

   public static Font icon(float f) {
      return Fonts.NEW_ICONS.getFont(px(f));
   }

   public static Font legacyIcon(float f) {
      return Fonts.ICONS.getFont(px(f));
   }

   public static float snap(float floatValue) {
      Window window = Minecraft.getInstance().getWindow();
      if (window == null) {
         return floatValue;
      } else {
         double guiScale = window.getGuiScale();
         return guiScale <= 0.0 ? floatValue : (float)(Math.round(floatValue * guiScale) / guiScale);
      }
   }

   public static List<String> wrap(Font regular, String description, float width) {
      ArrayList<String> strings = new ArrayList<>();
      if (description != null && !description.isEmpty()) {
         if (regular.width(description) <= width) {
            strings.add(description);
            return strings;
         } else {
            StringBuilder builder = new StringBuilder();

            for (String str : description.split(" ")) {
               String text = builder.isEmpty() ? str : builder + " " + str;
               if (regular.width(text) > width && !builder.isEmpty()) {
                  strings.add(builder.toString());
                  builder = new StringBuilder(str);
               } else {
                  builder = new StringBuilder(text);
               }
            }

            if (!builder.isEmpty()) {
               strings.add(builder.toString());
            }

            return strings;
         }
      } else {
         return strings;
      }
   }
}
