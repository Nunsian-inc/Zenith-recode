package org.zenith.utility.misc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class ImageLoader {
   public static final int CONST_intValue = 16384;
   public static final long CONST_longValue = 41943040L;

   private ImageLoader() {
   }

   public static int[] method231(byte[] allBytes) throws IOException {
      int[] ints;
      try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(allBytes))) {
         if (imageInputStream == null) {
            throw new IOException("не удалось прочитать картинку");
         }

         var imageReaders = ImageIO.getImageReaders(imageInputStream);
         if (!imageReaders.hasNext()) {
            throw new IOException("неизвестный формат картинки (png, jpg, gif, bmp)");
         }

         ImageReader imageReader = (ImageReader)imageReaders.next();

         try {
            imageReader.setInput(imageInputStream, true, true);
            ints = new int[]{imageReader.getWidth(0), imageReader.getHeight(0)};
         } finally {
            imageReader.dispose();
         }
      }

      return ints;
   }

   public static void method232(byte[] allBytes) throws IOException {
      int[] ints = method231(allBytes);
      method233(ints[0], ints[1]);
   }

   public static void method233(int width, int height) throws IOException {
      if (width <= 0 || height <= 0 || width > 16384 || height > 16384 || (long)width * height > 41943040L) {
         throw new IOException("картинка слишком большая: " + width + "×" + height + " (до 16384 по стороне и 40 Мпикс)");
      }
   }

   public static BufferedImage method234(byte[] allBytes, int n) throws IOException {
      BufferedImage bufferedImage;
      try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(allBytes))) {
         if (imageInputStream == null) {
            throw new IOException("не удалось прочитать картинку");
         }

         var imageReaders = ImageIO.getImageReaders(imageInputStream);
         if (!imageReaders.hasNext()) {
            throw new IOException("неизвестный формат картинки (png, jpg, gif, bmp)");
         }

         ImageReader imageReader = (ImageReader)imageReaders.next();

         try {
            imageReader.setInput(imageInputStream, true, true);
            int width = imageReader.getWidth(0);
            int height = imageReader.getHeight(0);
            method233(width, height);
            ImageReadParam defaultReadParam = imageReader.getDefaultReadParam();
            int sourceXSubsampling = Math.max(1, (int)Math.ceil((double)Math.max(width, height) / n));
            if (sourceXSubsampling > 1) {
               defaultReadParam.setSourceSubsampling(sourceXSubsampling, sourceXSubsampling, 0, 0);
            }

            BufferedImage bufferedImage2 = imageReader.read(0, defaultReadParam);
            if (bufferedImage2 == null) {
               throw new IOException("не удалось прочитать картинку");
            }

            bufferedImage = bufferedImage2;
         } finally {
            imageReader.dispose();
         }
      }

      return bufferedImage;
   }
}
