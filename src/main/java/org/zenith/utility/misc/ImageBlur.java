package org.zenith.utility.misc;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;
import org.jetbrains.annotations.NotNull;

public class ImageBlur {
   public float floatValue;
   public Kernel kernel;

   public ImageBlur(float floatValue) {
      this.method2286(floatValue);
   }

   public static void method2283(@NotNull Kernel kernel, int[] rgbArray, int[] ints, int width, int height, boolean flag, boolean flag2, boolean flag3, int n) {
      float[] kernelData = kernel.getKernelData(null);
      int width2 = kernel.getWidth();
      int n2 = width2 / 2;

      for (int i = 0; i < height; i++) {
         int n3 = i;
         int n4 = i * width;

         for (int j = 0; j < width; j++) {
            float f = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            int n5 = n2;

            for (int k = -n2; k <= n2; k++) {
               float f5 = kernelData[n5 + k];
               if (f5 != 0.0F) {
                  int n6 = j + k;
                  if (n6 < 0) {
                     if (n == 1) {
                        n6 = 0;
                     } else if (n == 2) {
                        n6 = (j + width) % width;
                     }
                  } else if (n6 >= width) {
                     if (n == 1) {
                        n6 = width - 1;
                     } else if (n == 2) {
                        n6 = (j + width) % width;
                     }
                  }

                  int n7 = rgbArray[n4 + n6];
                  int n8 = n7 >> 24 & 0xFF;
                  int n9 = n7 >> 16 & 0xFF;
                  int n10 = n7 >> 8 & 0xFF;
                  int n11 = n7 & 0xFF;
                  if (flag2) {
                     float f6 = n8 * 0.003921569F;
                     n9 = (int)(n9 * f6);
                     n10 = (int)(n10 * f6);
                     n11 = (int)(n11 * f6);
                  }

                  f4 += f5 * n8;
                  f += f5 * n9;
                  f2 += f5 * n10;
                  f3 += f5 * n11;
               }
            }

            if (flag3 && f4 != 0.0F && f4 != 255.0F) {
               float f5 = 255.0F / f4;
               f *= f5;
               f2 *= f5;
               f3 *= f5;
            }

            int n6 = flag ? method2284((int)(f4 + 0.5)) : 255;
            int n7 = method2284((int)(f + 0.5));
            int n8 = method2284((int)(f2 + 0.5));
            int n9 = method2284((int)(f3 + 0.5));
            ints[n3] = n6 << 24 | n7 << 16 | n8 << 8 | n9;
            n3 += height;
         }
      }
   }

   public static int method2284(int n) {
      return n < 0 ? 0 : Math.min(n, 255);
   }

   public static Kernel method2285(float floatValue) {
      int n = (int)Math.ceil(floatValue);
      int width = n * 2 + 1;
      float[] data = new float[width];
      float f = floatValue / 3.0F;
      float f2 = 2.0F * f * f;
      float f3 = (float) (Math.PI * 2) * f;
      float f4 = (float)Math.sqrt(f3);
      float f5 = floatValue * floatValue;
      float f6 = 0.0F;
      int n2 = 0;

      for (int i = -n; i <= n; i++) {
         float f7 = i * i;
         if (f7 > f5) {
            data[n2] = 0.0F;
         } else {
            data[n2] = (float)Math.exp(-f7 / f2) / f4;
         }

         f6 += data[n2];
         n2++;
      }

      for (int i = 0; i < width; i++) {
         data[i] /= f6;
      }

      return new Kernel(width, 1, data);
   }

   public void method2286(float floatValue) {
      this.floatValue = floatValue;
      this.kernel = method2285(floatValue);
   }

   public BufferedImage method2287(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      if (bufferedImage2 == null) {
         bufferedImage2 = this.method2288(bufferedImage, null);
      }

      int[] rgbArray = new int[width * height];
      int[] ints = new int[width * height];
      bufferedImage.getRGB(0, 0, width, height, rgbArray, 0, width);
      if (this.floatValue > 0.0F) {
         method2283(this.kernel, rgbArray, ints, width, height, true, true, false, 1);
         method2283(this.kernel, ints, rgbArray, height, width, true, false, true, 1);
      }

      bufferedImage2.setRGB(0, 0, width, height, rgbArray, 0, width);
      return bufferedImage2;
   }

   public BufferedImage method2288(BufferedImage bufferedImage, ColorModel cm) {
      if (cm == null) {
         cm = bufferedImage.getColorModel();
      }

      return new BufferedImage(cm, cm.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), cm.isAlphaPremultiplied(), null);
   }
}
