package org.zenith.utility.config;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.AiStreamClient;

public final class AiImageStore {
   public static final int CONST_intValue = 1568;
   public static final long CONST_longValue = 25165824L;
   private static final Map<String, Identifier> items = new ConcurrentHashMap<>();
   private static final Map<String, int[]> items2 = new ConcurrentHashMap<>();
   private static final Set<String> items3 = ConcurrentHashMap.newKeySet();

   private AiImageStore() {
   }

   public static Path method4630() {
      return Zenith.file.toPath().resolve("ai-chats").resolve("images");
   }

   public static boolean method4631(Path path) {
      String lowerCase = path.getFileName().toString().toLowerCase(Locale.ROOT);
      return lowerCase.endsWith(".png")
         || lowerCase.endsWith(".jpg")
         || lowerCase.endsWith(".jpeg")
         || lowerCase.endsWith(".gif")
         || lowerCase.endsWith(".bmp")
         || lowerCase.endsWith(".webp");
   }

   public static String method4632(Path path) throws IOException {
      if (Files.size(path) > 25165824L) {
         throw new IOException("картинка больше 24 МБ");
      } else {
         BufferedImage bufferedImage;
         try (InputStream inputStream = Files.newInputStream(path)) {
            bufferedImage = ImageIO.read(inputStream);
         }

         if (bufferedImage == null) {
            throw new IOException("не удалось прочитать " + path.getFileName() + " (png, jpg, gif, bmp)");
         } else {
            return method4634(bufferedImage);
         }
      }
   }

   public static String method4633(NativeImage nativeImage) throws IOException {
      BufferedImage bufferedImage = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), 2);

      for (int i = 0; i < nativeImage.getHeight(); i++) {
         for (int j = 0; j < nativeImage.getWidth(); j++) {
            bufferedImage.setRGB(j, i, nativeImage.getPixel(j, i));
         }
      }

      return method4634(bufferedImage);
   }

   private static String method4634(BufferedImage bufferedImage) throws IOException {
      BufferedImage im = method4635(bufferedImage, 1568, 2);
      Files.createDirectories(method4630());
      String str = UUID.randomUUID().toString().substring(0, 12) + ".png";
      ImageIO.write(im, "png", method4630().resolve(str).toFile());
      return str;
   }

   private static BufferedImage method4635(BufferedImage bufferedImage, int n, int imageType) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      double d = Math.min(1.0, (double)n / Math.max(width, height));
      int width2 = Math.max(1, (int)Math.round(width * d));
      int height2 = Math.max(1, (int)Math.round(height * d));
      BufferedImage bufferedImage2 = new BufferedImage(width2, height2, imageType);
      Graphics2D graphics = bufferedImage2.createGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      if (imageType == 1) {
         graphics.setColor(Color.BLACK);
         graphics.fillRect(0, 0, width2, height2);
      }

      graphics.drawImage(bufferedImage, 0, 0, width2, height2, null);
      graphics.dispose();
      return bufferedImage2;
   }

   public static AiStreamClient.Data2 method4636(String str) throws IOException {
      Path path = method4630().resolve(str);
      BufferedImage bufferedImage = ImageIO.read(path.toFile());
      if (bufferedImage == null) {
         throw new IOException("картинка " + str + " испорчена");
      } else {
         BufferedImage image = method4635(bufferedImage, 1568, 1);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();

         try (MemoryCacheImageOutputStream cacheImageOutputStream = new MemoryCacheImageOutputStream(byteArrayOutputStream)) {
            imageWriter.setOutput(cacheImageOutputStream);
            ImageWriteParam defaultWriteParam = imageWriter.getDefaultWriteParam();
            defaultWriteParam.setCompressionMode(2);
            defaultWriteParam.setCompressionQuality(0.85F);
            imageWriter.write(null, new IIOImage(image, null, null), defaultWriteParam);
         } finally {
            imageWriter.dispose();
         }

         return new AiStreamClient.Data2("image/jpeg", Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
      }
   }

   public static Path method4637(String str) {
      return method4630().resolve(str);
   }

   public static int[] method4638(String key) {
      return items2.getOrDefault(key, new int[]{1, 1});
   }

   public static Identifier method4639(String key) {
      Identifier id = items.get(key);
      if (id != null) {
         return id;
      } else if (items3.contains(key)) {
         return null;
      } else {
         Path path = method4630().resolve(key);
         if (!Files.isRegularFile(path)) {
            items3.add(key);
            return null;
         } else {
            NativeImage image = null;

            try {
               Identifier location;
               try (InputStream inputStream = Files.newInputStream(path)) {
                  image = NativeImage.read(inputStream);
                  int width = image.getWidth();
                  int height = image.getHeight();
                  Identifier location2 = Identifier.fromNamespaceAndPath("zenith", "ai_image/" + key.replace(".png", "").toLowerCase(Locale.ROOT));
                  Minecraft.getInstance().getTextureManager().register(location2, new DynamicTexture(() -> "zenith-ai", image));
                  image = null;
                  items2.put(key, new int[]{width, height});
                  items.put(key, location2);
                  location = location2;
               }

               return location;
            } catch (Exception e) {
               if (image != null) {
                  image.close();
               }

               items3.add(key);
               return null;
            }
         }
      }
   }

   public static void method4640(Collection<String> coll) {
      if (!coll.isEmpty()) {
         List<String> strings = List.copyOf(coll);
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.execute(() -> {
            for (String str : strings) {
               Identifier location = items.remove(str);
               items2.remove(str);
               items3.remove(str);
               if (location != null) {
                  minecraft.getTextureManager().release(location);
               }
            }
         });
      }
   }
}
