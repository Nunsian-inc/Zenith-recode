package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.Identifier;

public final class FoliageSprites {
   public static volatile boolean enabled;
   private static final float CONST_floatValue = 3.2F;
   private static final int CONST_intValue = 16777215;
   private static final int CONST_intValue2 = 14213872;
   private static final String[] texts = new String[]{
      "leaves",
      "grass",
      "fern",
      "bush",
      "sapling",
      "vine",
      "flower",
      "tulip",
      "orchid",
      "dandelion",
      "poppy",
      "allium",
      "bluet",
      "daisy",
      "cornflower",
      "lily",
      "rose",
      "peony",
      "lilac",
      "sunflower",
      "azalea",
      "petals",
      "mushroom",
      "wheat",
      "carrots",
      "potatoes",
      "beetroots",
      "sugar_cane",
      "bamboo",
      "moss_carpet",
      "seagrass",
      "kelp"
   };
   private static final String[] texts2 = new String[]{"grass_block", "grass_path", "short_grass_top", "seagrass_", "kelp_plant"};
   private static final float CONST_floatValue2 = 0.8F;
   public static int intValue;

   private FoliageSprites() {
   }

   public static boolean method1431(Identifier id) {
      if (enabled && id != null && "minecraft".equals(id.getNamespace())) {
         String path = id.getPath();
         if (!path.startsWith("block/")) {
            return false;
         } else {
            for (String str : texts2) {
               if (path.contains(str)) {
                  return false;
               }
            }

            for (String text : texts) {
               if (path.contains(text)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static void method1432(Identifier id, NativeImage originalImage) {
      int width = originalImage.getWidth();
      int height = originalImage.getHeight();
      if (width > 0 && height > 0) {
         intValue++;
         boolean contains = id.getPath().contains("leaves");
         if (contains) {
            for (int i = 0; i < width; i++) {
               for (int j = 0; j < height; j++) {
                  int pixel = originalImage.getPixel(i, j);
                  if (pixel >>> 24 > 16) {
                     int n = method1434(14213872, 16777215, method1436(method1433(id), i, j));
                     originalImage.setPixel(i, j, pixel & 0xFF000000 | method1435(pixel, n, 0.8F));
                  }
               }
            }
         }

         float f = width / 16.0F;
         long l = method1433(id);

         for (int i = 0; i < width; i++) {
            int n = -1;

            for (int j = 0; j < height; j++) {
               if (originalImage.getPixel(i, j) >>> 24 > 128) {
                  n = j;
                  break;
               }
            }

            if (n >= 0) {
               float f2 = method1436(l, i, 0) + 0.5F * method1436(l, i, 7);
               int n2 = Math.max(1, Math.round(3.2F * f * (0.55F + 0.75F * f2)));

               for (int j = 0; j < n2; j++) {
                  int y = n + j;
                  if (y >= height) {
                     break;
                  }

                  int pixel = originalImage.getPixel(i, y);
                  int n3 = pixel >>> 24;
                  if (n3 <= 128) {
                     break;
                  }

                  int n4 = method1434(14213872, 16777215, method1436(l, i, y + 3));
                  float f3 = j == n2 - 1 ? 0.45F : 1.0F;
                  originalImage.setPixel(i, y, n3 << 24 | method1435(pixel, n4, f3));
               }
            }
         }
      }
   }

   private static long method1433(Identifier id) {
      return id.hashCode() * -7046029254386353131L;
   }

   private static int method1434(int pixel, int n, float f) {
      int channel2 = channel(pixel >> 16, n >> 16, f);
      int channel3 = channel(pixel >> 8, n >> 8, f);
      int channel4 = channel(pixel, n, f);
      return channel2 << 16 | channel3 << 8 | channel4;
   }

   private static int method1435(int pixel, int n, float f) {
      int channel2 = channel(pixel >> 16, n >> 16, f);
      int channel3 = channel(pixel >> 8, n >> 8, f);
      int channel4 = channel(pixel, n, f);
      return channel2 << 16 | channel3 << 8 | channel4;
   }

   private static int channel(int pixel, int n, float f) {
      int n2 = pixel & 0xFF;
      int n3 = n & 0xFF;
      return Math.clamp((long)((int)(n2 + (n3 - n2) * f + 0.5F)), 0, 255);
   }

   private static float method1436(long l, int n, int n2) {
      long l2 = l ^ n * -6752110988234923001L ^ n2 * -6939452855193903323L;
      l2 ^= l2 >>> 29;
      l2 *= -4658895280553007687L;
      l2 ^= l2 >>> 32;
      return (float)(l2 >>> 40) / 1.6777216E7F;
   }
}
