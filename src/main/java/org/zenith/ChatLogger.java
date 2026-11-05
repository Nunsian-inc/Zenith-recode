package org.zenith;

import lombok.Generated;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.ThemeStyle;

public final class ChatLogger implements MinecraftAccess {
   public static final Component text = method2023(ZenithColor.INSTANCE5, ZenithColor.INSTANCE, ZenithColor.INSTANCE7);
   private static final String text2 = "\\u00A7.";

   public static void method2019(String status) {
      method2020(ChatLogger.Type.INFO, status);
   }

   public static void method2020(ChatLogger.Type type, String string) {
      if (mc.player != null) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         MutableComponent message = method2023(
               currentStyle.getPrimaryColor().getZenithColor(), currentStyle.getSecondaryPrimaryColor().getZenithColor(), currentStyle.getTextSecondary().getZenithColor()
            )
            .append(Component.literal(" "))
            .append(method2025(string, type, currentStyle));
         mc.player.sendSystemMessage(message);
      }
   }

   public static void method2021(String string) {
      method2020(ChatLogger.Type.WARN, string);
   }

   public static void method2022(String status) {
      method2020(ChatLogger.Type.ERROR, status);
   }

   private static MutableComponent method2023(ZenithColor zenithColor, ZenithColor zenithColor2, ZenithColor zenithColor7) {
      return Component.empty()
         .append(method2026("[ ", zenithColor7, false))
         .append(method2027("Zenith", zenithColor.method7303(0.12F), zenithColor2.method7303(0.08F), true, 0))
         .append(method2026(" ]", zenithColor7, false));
   }

   private static MutableComponent method2024(ChatLogger.Type type, ThemeStyle themeStyle) {
      ZenithColor zenithColor = type.getZenithColor().method7295(themeStyle.getZenithColor(), 0.15F).method7303(0.08F);
      ZenithColor zenithColor2 = zenithColor.method7295(themeStyle.getZenithColor2(), 0.25F);
      return Component.empty()
         .append(method2026("<", themeStyle.getZenithColor7(), false))
         .append(method2027(type.getText(), zenithColor, zenithColor2, true, 0))
         .append(method2026(">", themeStyle.getZenithColor7(), false));
   }

   private static MutableComponent method2025(String string, ChatLogger.Type type, ZenithStyle currentStyle) {
      String text = string == null ? "" : string.replaceAll("\\u00A7.", "");
      int n = Math.min(8, text.codePointCount(0, text.length()));
      ZenithColor zenithColor = type.getZenithColor().method7295(currentStyle.getPrimaryColor().getZenithColor(), 0.3F).method7303(0.12F);
      ZenithColor zenithColor2 = currentStyle.getTextEnable().getZenithColor().method7295(currentStyle.getSecondaryPrimaryColor().getZenithColor(), 0.15F);
      return method2027(text, zenithColor, zenithColor2, false, n);
   }

   private static MutableComponent method2026(String text, ZenithColor zenithColor7, boolean bold) {
      return Component.literal(text).setStyle(Style.EMPTY.withColor(zenithColor7.method7287()).withBold(bold));
   }

   private static MutableComponent method2027(String text, ZenithColor zenithColor, ZenithColor zenithColor2, boolean flag, int n) {
      MutableComponent empty = Component.empty();
      if (text != null && !text.isEmpty()) {
         int codePointCount = text.codePointCount(0, text.length());
         int[] ints = new int[]{0};
         text.codePoints().forEach(var7x -> {
            float hover = codePointCount <= 1 ? 0.0F : (float)ints[0] / (codePointCount - 1);
            ZenithColor zenithColor3 = zenithColor.method7295(zenithColor2, hover);
            boolean bold = flag || ints[0] < n;
            empty.append(Component.literal(new String(Character.toChars(var7x))).setStyle(Style.EMPTY.withColor(zenithColor3.method7287()).withBold(bold)));
            ints[0]++;
         });
         return empty;
      } else {
         return empty;
      }
   }

   @Generated
   private ChatLogger() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static enum Type {
      WARN("WARN", new ZenithColor(247, 206, 59)),
      ERROR("ERROR", new ZenithColor(242, 79, 68)),
      INFO("INFO", new ZenithColor(87, 126, 255));

      private final String text;
      private final ZenithColor zenithColor;

      @Generated
      public String getText() {
         return this.text;
      }

      @Generated
      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      @Generated
      private Type(String text, ZenithColor zenithColor) {
         this.text = text;
         this.zenithColor = zenithColor;
      }
   }
}
