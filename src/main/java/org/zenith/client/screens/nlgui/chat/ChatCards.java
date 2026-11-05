package org.zenith.client.screens.nlgui.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.StickerPackService;
import org.zenith.utility.config.ChatImageCache;
import org.zenith.utility.misc.Coord;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ChatCards {
   private static final float PAD = 4.0F;
   private static final float CELL = 12.0F;
   private static final float CELL_GAP = 1.0F;
   private static final int COLUMNS = 9;
   private static final float STICKER = 44.0F;
   private static final float COORD_WIDTH = 132.0F;
   private static final float MAP_HEIGHT = 42.0F;
   private static final float MAP_GRID = 9.0F;
   private static final float MAP_CELL = 3.0F;
   private static final int MAP_SCALE = 6;
   private static final int GEAR_SLOTS = 5;
   private static final float GEAR_GAP = 5.0F;
   private static final Map<String, CloudInventoryData> INVENTORIES = new LinkedHashMap<>();
   private static final int MAX_INVENTORIES = 48;
   private static final Map<ChatCards.MapKey, ZenithColor[]> MAPS = new LinkedHashMap<>();
   private static final int MAX_MAPS = 64;
   private static final Map<String, Animation> STICKER_FADE = new ConcurrentHashMap<>();
   private static final List<Runnable> PENDING_COUNTS = new ArrayList<>();

   private ChatCards() {
   }

   public static boolean isCard(Coord coord) {
      return coord != null && (coord.kind().equals("coord") || coord.kind().equals("inv") || coord.kind().equals("st"));
   }

   public static float width(Coord card) {
      String kind = card.kind();

      return switch (kind) {
         case "coord" -> coordWidth(card);
         case "inv" -> 133.0F;
         case "st" -> 44.0F;
         default -> 0.0F;
      };
   }

   public static float height(Coord card) {
      String kind = card.kind();

      return switch (kind) {
         case "coord" -> coordHeight(card);
         case "inv" -> inventoryHeight(card);
         case "st" -> 44.0F;
         default -> 0.0F;
      };
   }

   public static void render(MenuDrawContext menuDrawContext, Coord card, float floatValue, float centeredTextY, float previewAlpha, ZenithStyle zenithStyle) {
      String kind = card.kind();
      switch (kind) {
         case "coord":
            renderCoord(menuDrawContext, card, floatValue, centeredTextY, previewAlpha, zenithStyle);
            break;
         case "inv":
            renderInventory(menuDrawContext, card, floatValue, centeredTextY, previewAlpha, zenithStyle);
            break;
         case "st":
            renderSticker(menuDrawContext, card, floatValue, centeredTextY, previewAlpha, zenithStyle);
      }
   }

   private static Font coordFont() {
      return Fonts.NEW_MEDIUM.getFont(5.4F);
   }

   private static Font underFont() {
      return Fonts.NEW_REGULAR.getFont(4.2F);
   }

   private static String coordLine(Coord card) {
      return card.method5170("x", 0) + "  " + card.method5170("y", 0) + "  " + card.method5170("z", 0);
   }

   private static String coordUnder(Coord card) {
      String dimensionLabel = ChatCompose.dimensionLabel(card.method5169("d", ""));
      String display = ServerNameUtils.display(card.method5169("s", ""));
      if (dimensionLabel.isEmpty()) {
         return display;
      } else {
         return display.isEmpty() ? dimensionLabel : dimensionLabel + " · " + display;
      }
   }

   private static float coordWidth(Coord card) {
      return 132.0F;
   }

   private static float coordHeight(Coord card) {
      float f = coordFont().height() + 1.0F + underFont().height();
      if (!card.method5169("n", "").isEmpty()) {
         f += underFont().height() + 1.0F;
      }

      return 46.0F + f + 4.0F;
   }

   private static void renderCoord(MenuDrawContext menuDrawContext, Coord card, float floatValue, float centeredTextY, float previewAlpha, ZenithStyle zenithStyle) {
      float width = 132.0F;
      float height = coordHeight(card);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
      menuDrawContext.drawRoundedRect(floatValue, centeredTextY, width, height, headerRadius, zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha));
      renderMap(menuDrawContext, card, floatValue, centeredTextY, width, zenithStyle, previewAlpha);
      menuDrawContext.drawRect(floatValue, centeredTextY + 42.0F, width, 1.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      Font font = coordFont();
      Font underFont2 = underFont();
      float f = centeredTextY + 42.0F + 4.0F;
      menuDrawContext.drawText(font, coordLine(card), floatValue + 4.0F, f, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      f += font.height() + 1.0F;
      menuDrawContext.drawText(underFont2, fit(underFont2, coordUnder(card), width - 8.0F), floatValue + 4.0F, f, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
      String str = card.method5169("n", "");
      if (!str.isEmpty()) {
         menuDrawContext.drawText(
            underFont2, fit(underFont2, str, width - 8.0F), floatValue + 4.0F, f + underFont2.height() + 1.0F, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
         );
      }

      menuDrawContext.drawRoundedBorder(floatValue, centeredTextY, width, height, 0.15F, headerRadius, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
   }

   private static void renderMap(MenuDrawContext menuDrawContext, Coord card, float floatValue, float centeredTextY, float width, ZenithStyle zenithStyle, float previewAlpha) {
      menuDrawContext.drawRoundedRect(
         floatValue,
         centeredTextY,
         width,
         42.0F,
         ProxyDotRadiusData.method8075(GuiStyle.ROUND.intValue() * 0.5F),
         zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 2.6F)
      );
      int x = card.method5170("x", 0);
      int worldZ = card.method5170("z", 0);
      int palette = paletteOf(card.method5169("d", ""));
      int columns = (int)Math.ceil(width / 3.0F);
      int rows = (int)Math.ceil(14.0);
      ZenithColor[] zenithColors = mapColors(x, worldZ, palette, columns, rows);

      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j++) {
            float f = floatValue + j * 3.0F;
            float y = centeredTextY + i * 3.0F;
            float width2 = Math.min(3.0F, floatValue + width - f);
            float height = Math.min(3.0F, centeredTextY + 42.0F - y);
            if (!(width2 <= 0.0F) && !(height <= 0.0F)) {
               ZenithColor zenithColor = zenithColors[i * columns + j];
               menuDrawContext.drawRect(f, y, width2, height, previewAlpha >= 1.0F ? zenithColor : zenithColor.method7294(previewAlpha));
            }
         }
      }

      ZenithColor zenithColor = zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 0.9F);
      float floorMod = Math.floorMod(x, 9);
      float f = Math.floorMod(worldZ, 9);

      for (float floatValue2 = floatValue + floorMod; floatValue2 < floatValue + width; floatValue2 += 9.0F) {
         menuDrawContext.drawRect(floatValue2, centeredTextY, 0.5F, 42.0F, zenithColor);
      }

      for (float y = centeredTextY + f; y < centeredTextY + 42.0F; y += 9.0F) {
         menuDrawContext.drawRect(floatValue, y, width, 0.5F, zenithColor);
      }

      ZenithColor zenithColor2 = zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha * 0.55F);

      for (int i = 0; i < 4; i++) {
         float f2 = i;
         ZenithColor zenithColor3 = zenithColor2.method7294(0.38F - i * 0.08F);
         menuDrawContext.drawRect(floatValue + f2, centeredTextY + f2, width - f2 * 2.0F, 1.0F, zenithColor3);
         menuDrawContext.drawRect(floatValue + f2, centeredTextY + 42.0F - f2 - 1.0F, width - f2 * 2.0F, 1.0F, zenithColor3);
         menuDrawContext.drawRect(floatValue + f2, centeredTextY + f2, 1.0F, 42.0F - f2 * 2.0F, zenithColor3);
         menuDrawContext.drawRect(floatValue + width - f2 - 1.0F, centeredTextY + f2, 1.0F, 42.0F - f2 * 2.0F, zenithColor3);
      }

      compass(menuDrawContext, zenithStyle, floatValue + width - 8.0F, centeredTextY + 4.0F, previewAlpha);
      scaleBar(menuDrawContext, zenithStyle, floatValue + 5.0F, centeredTextY + 42.0F - 6.0F, previewAlpha);
      pin(menuDrawContext, zenithStyle, floatValue + width / 2.0F, centeredTextY + 21.0F + 3.0F, previewAlpha);
   }

   private static ZenithColor[] mapColors(int x, int worldZ, int palette, int columns, int rows) {
      ChatCards.MapKey mapKey = new ChatCards.MapKey(x, worldZ, palette, columns, rows);
      synchronized (MAPS) {
         ZenithColor[] zenithColors = MAPS.get(mapKey);
         if (zenithColors != null) {
            return zenithColors;
         }
      }

      int n = x - columns / 2 * 6;
      int n2 = worldZ - rows / 2 * 6;
      ZenithColor[] zenithColors = new ZenithColor[columns * rows];

      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j++) {
            int n3 = n + j * 6;
            int n4 = n2 + i * 6;
            float f = terrain(n3, n4);
            float terrain2 = terrain(n3 + 8123, n4 - 4517);
            float terrain3 = terrain(n3, n4 - 6);
            int surface2 = surface(palette, f, terrain2);
            float f2 = Math.clamp(0.5F + (f - terrain3) * 5.5F, 0.18F, 0.92F);
            zenithColors[i * columns + j] = ZenithColor.method7291(shade(surface2, 0.78F + 0.44F * f2));
         }
      }

      synchronized (MAPS) {
         if (MAPS.size() >= 64) {
            MAPS.remove(MAPS.keySet().iterator().next());
         }

         MAPS.put(mapKey, zenithColors);
         return zenithColors;
      }
   }

   private static int paletteOf(String str) {
      String str2 = str == null ? "" : str;
      if (str2.contains("nether")) {
         return 1;
      } else {
         return str2.contains("end") ? 2 : 0;
      }
   }

   private static float terrain(int n, int n2) {
      float f = smooth(n / 96.0F, n2 / 96.0F) * 0.62F
         + smooth(n / 34.0F + 11.7F, n2 / 34.0F - 4.1F) * 0.28F
         + smooth(n / 13.0F - 7.3F, n2 / 13.0F + 2.9F) * 0.1F;
      return Math.clamp(f, 0.0F, 1.0F);
   }

   private static float smooth(float f, float f2) {
      int n = (int)Math.floor(f);
      int n2 = (int)Math.floor(f2);
      float f3 = f - n;
      float f4 = f2 - n2;
      f3 = f3 * f3 * (3.0F - 2.0F * f3);
      f4 = f4 * f4 * (3.0F - 2.0F * f4);
      float spot2 = spot(n, n2);
      float spot3 = spot(n + 1, n2);
      float spot4 = spot(n, n2 + 1);
      float spot5 = spot(n + 1, n2 + 1);
      return (spot2 * (1.0F - f3) + spot3 * f3) * (1.0F - f4) + (spot4 * (1.0F - f3) + spot5 * f3) * f4;
   }

   private static float spot(int n, int n2) {
      int n3 = n * 73856093 ^ n2 * 19349663;
      n3 ^= n3 >>> 13;
      n3 *= 1274126177;
      n3 ^= n3 >>> 16;
      return (n3 >>> 8 & 65535) / 65535.0F;
   }

   private static int surface(int palette, float f, float f2) {
      if (palette == 1) {
         if (f < 0.34F) {
            return 2755594;
         } else if (f < 0.44F) {
            return 7215632;
         } else if (f < 0.62F) {
            return f2 < 0.45F ? 9185814 : 8004627;
         } else {
            return f < 0.78F ? 4860450 : 7035474;
         }
      } else if (palette == 2) {
         if (f < 0.38F) {
            return 723474;
         } else if (f < 0.46F) {
            return 1709862;
         } else if (f < 0.7F) {
            return f2 < 0.5F ? 13222026 : 12432510;
         } else {
            return 14209702;
         }
      } else if (f < 0.34F) {
         return 2902650;
      } else if (f < 0.42F) {
         return 4023961;
      } else if (f < 0.45F) {
         return 13089930;
      } else if (f < 0.6F) {
         return f2 < 0.42F ? 7245642 : 6061886;
      } else if (f < 0.72F) {
         return f2 < 0.5F ? 4153648 : 3494442;
      } else {
         return f < 0.84F ? 7236194 : 14081252;
      }
   }

   private static int shade(int n, float f) {
      int n2 = Math.clamp((long)((int)((n >> 16 & 0xFF) * f)), 0, 255);
      int n3 = Math.clamp((long)((int)((n >> 8 & 0xFF) * f)), 0, 255);
      int n4 = Math.clamp((long)((int)((n & 0xFF) * f)), 0, 255);
      return 0xFF000000 | n2 << 16 | n3 << 8 | n4;
   }

   private static void compass(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float f, float f2, float previewAlpha) {
      ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 1.6F);

      for (int i = 0; i < 3; i++) {
         float width = 1.0F + i * 2.0F;
         menuDrawContext.drawRect(f - width / 2.0F, f2 + i, width, 1.0F, zenithColor);
      }

      menuDrawContext.drawRect(f - 0.5F, f2 + 3.0F, 1.0F, 4.0F, zenithColor);
   }

   private static void scaleBar(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float floatValue, float y, float previewAlpha) {
      ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 1.4F);
      float width = 18.0F;
      menuDrawContext.drawRect(floatValue, y + 2.0F, width, 0.8F, zenithColor);
      menuDrawContext.drawRect(floatValue, y, 0.8F, 3.0F, zenithColor);
      menuDrawContext.drawRect(floatValue + width - 0.8F, y, 0.8F, 3.0F, zenithColor);
   }

   private static void pin(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, float f, float f2, float previewAlpha) {
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      float width = 7.0F;
      float y = f2 - 5.0F - width;

      for (int i = 3; i >= 1; i--) {
         float width2 = 3.0F + i * 2.6F;
         menuDrawContext.drawRoundedRect(
            f - width2 / 2.0F, f2 - width2 / 4.0F, width2, width2 / 2.0F, ProxyDotRadiusData.method8069(width2 / 4.0F), zenithColor.method7294(previewAlpha * 0.07F * i)
         );
      }

      for (int i = 0; i < 5; i++) {
         float width2 = 5.0F - i;
         menuDrawContext.drawRect(f - width2 / 2.0F, y + width - 1.0F + i, width2, 1.0F, zenithColor.method7294(previewAlpha));
      }

      menuDrawContext.drawRoundedRect(f - width / 2.0F, y, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor.method7294(previewAlpha));
      float width2 = 2.6F;
      menuDrawContext.drawRoundedRect(
         f - width2 / 2.0F,
         y + (width - width2) / 2.0F,
         width2,
         width2,
         ProxyDotRadiusData.method8069(width2 / 2.0F),
         zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha * 4.0F)
      );
   }

   private static String fit(Font font, String coordUnder, float f) {
      if (coordUnder != null && !(f <= 0.0F)) {
         if (font.width(coordUnder) <= f) {
            return coordUnder;
         } else {
            float width = font.width("…");
            if (width > f) {
               return "";
            } else {
               int length = coordUnder.length();

               while (length > 0 && font.width(coordUnder.substring(0, length)) + width > f) {
                  length--;
               }

               return length == 0 ? "" : coordUnder.substring(0, length) + "…";
            }
         }
      } else {
         return "";
      }
   }

   public static String coordText(Coord coord) {
      return coord.method5170("x", 0) + " " + coord.method5170("y", 0) + " " + coord.method5170("z", 0);
   }

   private static CloudInventoryData inventory(Coord card) {
      String str = card.method5169("v", "");
      if (str.isEmpty()) {
         return null;
      } else {
         synchronized (INVENTORIES) {
            CloudInventoryData cloudInventoryData = INVENTORIES.get(str);
            if (cloudInventoryData != null) {
               return cloudInventoryData;
            }
         }

         CloudInventoryData inventoryOf = ChatCompose.inventoryOf(card);
         if (inventoryOf == null) {
            return null;
         } else {
            synchronized (INVENTORIES) {
               if (INVENTORIES.size() >= 48) {
                  INVENTORIES.remove(INVENTORIES.keySet().iterator().next());
               }

               INVENTORIES.put(str, inventoryOf);
               return inventoryOf;
            }
         }
      }
   }

   private static int backpackRows(CloudInventoryData cloudInventoryData) {
      int n = -1;

      for (int i = 9; i < 36; i++) {
         if (!cloudInventoryData.main().get(i).isEmpty()) {
            n = i;
         }
      }

      return n < 0 ? 0 : (n - 9) / 9 + 1;
   }

   private static float inventoryHeight(Coord card) {
      CloudInventoryData cloudInventoryData = inventory(card);
      if (cloudInventoryData == null) {
         return 0.0F;
      } else {
         Font font = underFont();
         int n = Math.max(5, 1 + backpackRows(cloudInventoryData));
         return 8.0F + font.height() + 2.0F + n * 12.0F;
      }
   }

   private static void renderInventory(MenuDrawContext menuDrawContext, Coord card, float floatValue, float centeredTextY, float previewAlpha, ZenithStyle zenithStyle) {
      CloudInventoryData cloudInventoryData = inventory(card);
      if (cloudInventoryData != null) {
         float f = width(card);
         float height = inventoryHeight(card);
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
         menuDrawContext.drawRoundedRect(floatValue, centeredTextY, f, height, headerRadius, zenithStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha));
         Font font = underFont();
         float centeredTextY2 = centeredTextY + 4.0F;
         MenuGlyph.CHESTPLATE.draw(menuDrawContext, floatValue + 4.0F, centeredTextY2 + (font.height() - 6.0F) / 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(font, "инвентарь", floatValue + 4.0F + 6.0F + 3.0F, centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         String display = ServerNameUtils.display(card.method5169("s", ""));
         if (!display.isEmpty()) {
            String text = fit(font, display, f - 8.0F - 6.0F - 3.0F - font.width("инвентарь") - 6.0F);
            menuDrawContext.drawText(font, text, floatValue + f - 4.0F - font.width(text), centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.7F));
         }

         float f2 = centeredTextY + 4.0F + font.height() + 3.0F;
         menuDrawContext.drawRect(floatValue + 4.0F, f2 - 2.0F, f - 8.0F, 1.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 1.6F));
         float f3 = floatValue + 4.0F;
         float f4 = f3 + 12.0F + 5.0F;

         for (int i = 0; i < 2; i++) {
            boolean flag = i == 1;
            var armor = cloudInventoryData.armor();

            for (int j = 0; j < armor.size() && j < 4; j++) {
               slot(menuDrawContext, (ItemStack)armor.get(armor.size() - 1 - j), f3, f2 + j * 12.0F, previewAlpha, zenithStyle, flag);
            }

            slot(menuDrawContext, cloudInventoryData.offhand(), f3, f2 + 48.0F, previewAlpha, zenithStyle, flag);
            if (!flag) {
               menuDrawContext.drawRect(f3 + 12.0F + 2.5F, f2 + 1.0F, 1.0F, 58.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 1.6F));
               menuDrawContext.drawRect(f4 - 2.5F + 1.5F, f2 + 2.0F, 1.0F, 8.0F, zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.8F));
            }

            for (int j = 0; j < 9; j++) {
               slot(menuDrawContext, cloudInventoryData.hotbar(j), f4 + j * 12.0F, f2, previewAlpha, zenithStyle, flag);
            }

            int n = backpackRows(cloudInventoryData);

            for (int j = 0; j < n; j++) {
               for (int k = 0; k < 9; k++) {
                  ItemStack offhand = cloudInventoryData.main().get(9 + j * 9 + k);
                  slot(menuDrawContext, offhand, f4 + k * 12.0F, f2 + 12.0F + j * 12.0F, previewAlpha, zenithStyle, flag);
               }
            }
         }

         if (!PENDING_COUNTS.isEmpty()) {
            ScissorFlusher.flush();
            PENDING_COUNTS.forEach(Runnable::run);
            PENDING_COUNTS.clear();
         }

         menuDrawContext.drawRoundedBorder(floatValue, centeredTextY, f, height, 0.15F, headerRadius, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      }
   }

   private static void slot(MenuDrawContext menuDrawContext, ItemStack offhand, float f, float f2, float previewAlpha, ZenithStyle zenithStyle, boolean flag) {
      boolean flag2 = offhand != null && !offhand.isEmpty();
      float width = 11.0F;
      if (flag) {
         if (flag2 && previewAlpha >= 0.35F) {
            item(menuDrawContext, offhand, f, f2, previewAlpha);
         }
      } else {
         menuDrawContext.drawRoundedRect(
            f + 0.5F,
            f2 + 0.5F,
            width,
            width,
            ProxyDotRadiusData.method8069(1.5F),
            zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * (flag2 ? 2.4F : 1.1F))
         );
         if (flag2) {
            menuDrawContext.drawRoundedBorder(
               f + 0.5F, f2 + 0.5F, width, width, 0.15F, ProxyDotRadiusData.method8069(1.5F), zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 1.8F)
            );
         }
      }
   }

   private static void item(MenuDrawContext menuDrawContext, ItemStack offhand, float f, float f2, float previewAlpha) {
      menuDrawContext.pushMatrix();
      float x = 0.5625F;
      menuDrawContext.pose().translate(f + 0.5F + 1.0F, f2 + 0.5F + 1.0F);
      menuDrawContext.pose().scale(x, x);
      menuDrawContext.item(offhand, 0, 0);
      menuDrawContext.renderItemBar(offhand, 0, 0);
      menuDrawContext.popMatrix();
      if (offhand.getCount() > 1) {
         Font font = Fonts.NEW_MEDIUM.getFont(3.6F);
         String string = Integer.toString(offhand.getCount());
         PENDING_COUNTS.add(
            () -> menuDrawContext.drawText(
               font, string, f + 12.0F - 0.5F - 1.0F - font.width(string), f2 + 12.0F - 0.5F - 1.0F - font.height(), ZenithColor.INSTANCE.method7294(previewAlpha)
            )
         );
      }
   }

   private static void renderSticker(MenuDrawContext menuDrawContext, Coord card, float floatValue, float centeredTextY, float previewAlpha, ZenithStyle zenithStyle) {
      String key = StickerPackService.getInstance().method5507(card.method5169("p", ""), card.method5169("i", ""));
      Identifier texture = ChatImageCache.get(key);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
      if (texture == null) {
         float f = 0.7F + 0.3F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0));
         menuDrawContext.drawRoundedRect(floatValue, centeredTextY, 44.0F, 44.0F, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 1.4F * f));
      } else {
         if (STICKER_FADE.size() > 512 && !STICKER_FADE.containsKey(key)) {
            STICKER_FADE.clear();
         }

         float f = STICKER_FADE.computeIfAbsent(key, var0x -> new Animation(240L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         if (f < 1.0F) {
            menuDrawContext.drawRoundedRect(floatValue, centeredTextY, 44.0F, 44.0F, headerRadius, zenithStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha * 1.4F * (1.0F - f)));
         }

         float height = 44.0F * (0.86F + 0.14F * f);
         drawSticker(
            menuDrawContext,
            card.method5169("i", ""),
            texture,
            floatValue + (44.0F - height) / 2.0F,
            centeredTextY + (44.0F - height) / 2.0F,
            height,
            headerRadius,
            ZenithColor.INSTANCE.method7294(previewAlpha * f)
         );
      }
   }

   public static void drawSticker(
      MenuDrawContext menuDrawContext, String input, Identifier texture, float floatValue, float cardAvatarY, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor
   ) {
      StickerPackService.Data3 data3 = StickerPackService.method5504(input);
      if (data3 == null) {
         menuDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, height, height, headerRadius, zenithColor);
      } else {
         int n = data3.method5511(System.currentTimeMillis());
         float f = 1.0F / data3.columns();
         float f2 = 1.0F / data3.rows();
         float f3 = n % data3.columns() * f;
         float f4 = n / data3.columns() * f2;
         int n2 = ChatImageCache.method1488(texture);
         int n3 = ChatImageCache.method1489(texture);
         float f5 = n2 > 0 ? 0.5F / n2 : 0.0F;
         float f6 = n3 > 0 ? 0.5F / n3 : 0.0F;
         menuDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, height, height, headerRadius, zenithColor, f3 + f5, f4 + f6, f3 + f - f5, f4 + f2 - f6);
      }
   }

   private static final class MapKey {
      private final int worldX;
      private final int worldZ;
      private final int palette;
      private final int columns;
      private final int rows;

      public int worldX() {
         return this.worldX;
      }

      public int worldZ() {
         return this.worldZ;
      }

      public int palette() {
         return this.palette;
      }

      public int columns() {
         return this.columns;
      }

      public int rows() {
         return this.rows;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof MapKey other)) return false;
         return java.util.Objects.equals(this.worldX, other.worldX) && java.util.Objects.equals(this.worldZ, other.worldZ) && java.util.Objects.equals(this.palette, other.palette) && java.util.Objects.equals(this.columns, other.columns) && java.util.Objects.equals(this.rows, other.rows);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(worldX, worldZ, palette, columns, rows);
      }

      @Override
      public String toString() {
         return "MapKey[" + "worldX=" + this.worldX + ", " + "worldZ=" + this.worldZ + ", " + "palette=" + this.palette + ", " + "columns=" + this.columns + ", " + "rows=" + this.rows + "]";
      }

      private MapKey(int worldX, int worldZ, int palette, int columns, int rows) {
         this.worldX = worldX;
         this.worldZ = worldZ;
         this.palette = palette;
         this.columns = columns;
         this.rows = rows;
      }
   }
}
