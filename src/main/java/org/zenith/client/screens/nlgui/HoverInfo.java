package org.zenith.client.screens.nlgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class HoverInfo {
   private static String requested;
   private static String requestedTitle;
   private static final Animation SHOWN = new Animation(240L, 1.0F, Easing.INSTANCE12);
   private static String shownText;
   private static String shownTitle;
   private static String captionFor;
   private static String captionText;
   private static String wrapText;
   private static Font wrapFont;
   private static float wrapWidth = Float.NaN;
   private static List<String> wrapLines;

   private HoverInfo() {
   }

   public static boolean enabled() {
      Zenith zenith = Zenith.getInstance();
      NLMenuScreen nlMenuScreen = zenith == null ? null : zenith.getNLMenuScreen();
      return nlMenuScreen == null || !nlMenuScreen.isShortMode();
   }

   public static float boxHeight() {
      return 34.0F * SHOWN.animateTo(enabled() ? 1.0F : 0.0F);
   }

   public static void request(String name, String description) {
      if (description != null && !description.isBlank()) {
         requestedTitle = name;
         requested = description;
      }
   }

   public static void beginFrame() {
      requested = null;
      requestedTitle = null;
   }

   public static boolean hasText() {
      return requested != null;
   }

   public static void render(MenuDrawContext customDrawContext, float floatValue, float centeredTextY, float f1, float f, float previewAlpha) {
      float f2;
      if (!((f2 = SHOWN.getValue()) <= 0.01F)) {
         previewAlpha *= f2;
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
            Font font2 = Fonts.NEW_REGULAR.getFont(4.8F);
            if (requested != null) {
               shownText = requested;
               shownTitle = requestedTitle;
            }

            String str = shownTitle != null && !shownTitle.isBlank() ? shownTitle : null;
            if (captionText == null || !Objects.equals(str, captionFor)) {
               captionFor = str;
               captionText = spaced(str == null ? "HOVER" : str.toUpperCase());
            }

            customDrawContext.drawText(font, captionText, floatValue, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.9F));
            if (shownText != null) {
               float f3 = centeredTextY + font.height() + 3.0F;
               float f4 = font2.height() + 1.5F;
               int n = Math.max(1, (int)((f - (f3 - centeredTextY)) / f4));
               if (wrapLines == null || font2 != wrapFont || Float.compare(f1, wrapWidth) != 0 || !shownText.equals(wrapText)) {
                  wrapLines = wrap(shownText, font2, f1);
                  wrapText = shownText;
                  wrapFont = font2;
                  wrapWidth = f1;
               }

               var strings = wrapLines;

               for (int i = 0; i < Math.min(strings.size(), n); i++) {
                  customDrawContext.drawText(font2, (String)strings.get(i), floatValue, f3 + i * f4, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
               }
            }
         }
      }
   }

   public static String spaced(String upperCase) {
      StringBuilder builder = new StringBuilder(upperCase.length() * 2);

      for (int i = 0; i < upperCase.length(); i++) {
         if (i > 0) {
            builder.append(' ');
         }

         builder.append(upperCase.charAt(i));
      }

      return builder.toString();
   }

   private static List<String> wrap(String shownText, Font font, float f1) {
      ArrayList<String> strings = new ArrayList<>();
      StringBuilder builder = new StringBuilder();

      for (String str : shownText.split(" ")) {
         String text = builder.isEmpty() ? str : builder + " " + str;
         if (font.width(text) > f1 && !builder.isEmpty()) {
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
}
