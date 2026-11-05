package org.zenith.utility.misc;

import java.awt.Color;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.util.Mth;
import org.zenith.Zenith;
import org.zenith.ZenithColor;

public final class ColorUtils {
   public static final int CONST_intValue = method6440(255, 85, 85, 255);
   private static final Pattern pattern = Pattern.compile("(?i)§[0-9a-f-or]");

   public static int method6420(int intValue) {
      return intValue >> 16 & 0xFF;
   }

   public static int method6421(int intValue) {
      return intValue >> 8 & 0xFF;
   }

   public static int method6422(int intValue) {
      return intValue & 0xFF;
   }

   public static int method6423(int intValue) {
      return intValue >> 24 & 0xFF;
   }

   public static float method6424(int intValue) {
      return method6420(intValue) / 255.0F;
   }

   public static float method6425(int intValue) {
      return method6421(intValue) / 255.0F;
   }

   public static float method6426(int intValue) {
      return method6422(intValue) / 255.0F;
   }

   public static float method6427(int intValue) {
      return method6423(intValue) / 255.0F;
   }

   public static int[] method6428(int intValue) {
      return new int[]{method6420(intValue), method6421(intValue), method6422(intValue), method6423(intValue)};
   }

   public static int[] method6429(int intValue) {
      return new int[]{method6420(intValue), method6421(intValue), method6422(intValue)};
   }

   public static float[] method6430(int intValue) {
      return new float[]{method6424(intValue), method6425(intValue), method6426(intValue), method6427(intValue)};
   }

   public static float[] method6431(int intValue) {
      return new float[]{method6424(intValue), method6425(intValue), method6426(intValue)};
   }

   public static boolean method6432(String text) {
      return text != null && text.matches("(?i)^[a-f0-9]{6}$");
   }

   public static ZenithColor method6433(String text, ZenithColor zenithColor) {
      if (!method6432(text)) {
         return zenithColor;
      } else {
         int n = Integer.parseInt(text, 16);
         int n2 = n >> 16 & 0xFF;
         int n3 = n >> 8 & 0xFF;
         int n4 = n & 0xFF;
         return new ZenithColor(new Color(n2, n3, n4));
      }
   }

   public static String method6434(ZenithColor zenithColor) {
      int n = zenithColor.method7287();
      return String.format("%06X", n & 16777215);
   }

   public static ZenithColor method6435(int n, int intValue, ZenithColor zenithColor, ZenithColor zenithColor2) {
      int n2 = (int)((System.currentTimeMillis() / n + intValue) % 360L);
      n2 = (n2 >= 180 ? 360 - n2 : n2) * 2;
      return method6437(zenithColor, zenithColor2, n2 / 360.0F);
   }

   public static ZenithColor method6436(int n, int n2, ZenithColor... zenithColors) {
      int n3 = (int)((System.currentTimeMillis() / n + n2) % 360L);
      n3 = (n3 > 180 ? 360 - n3 : n3) + 180;
      int n4 = (int)(n3 / 360.0F * zenithColors.length);
      if (n4 == zenithColors.length) {
         n4--;
      }

      ZenithColor zenithColor = zenithColors[n4];
      ZenithColor zenithColor2 = zenithColors[n4 == zenithColors.length - 1 ? 0 : n4 + 1];
      return method6437(zenithColor, zenithColor2, n3 / 360.0F * zenithColors.length - n4);
   }

   public static ZenithColor method6437(ZenithColor zenithColor, ZenithColor zenithColor2, float hover) {
      return zenithColor.method7295(zenithColor2, hover);
   }

   public static String method6438(String string) {
      return string != null && !string.isEmpty() ? pattern.matcher(string).replaceAll("") : null;
   }

   public static int method6439(int color, float alpha) {
      return method6440(method6420(color), method6421(color), method6422(color), Math.round(method6423(color) * alpha));
   }

   private static int method6440(int n, int n2, int n3, int n4) {
      return Mth.clamp(n4, 0, 255) << 24 | Mth.clamp(n, 0, 255) << 16 | Mth.clamp(n2, 0, 255) << 8 | Mth.clamp(n3, 0, 255);
   }

   public static int method6441(int intValue) {
      return Zenith.getInstance().getStyleManager().getClientColor(intValue).method7287();
   }

   @Generated
   private ColorUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
