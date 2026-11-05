package org.zenith.client.scripting;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.zenith.utility.misc.FileDialogs;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zenith.utility.config.ScriptSoundLoader;
import org.zenith.utility.misc.ImageLoader;
import org.zenith.utility.misc.ScriptPermissions;

public final class RemoteUtil {
   public static final int CONST_intValue = 2048;
   public static final int CONST_intValue2 = 64;
   public static final long CONST_longValue = 25165824L;
   public static final int CONST_intValue3 = 120;
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   private static final Map<Script, List<RemoteUtil.Entry>> items = new HashMap<>();
   private static final AtomicInteger atomicInteger2 = new AtomicInteger();
   private static final Map<Integer, RemoteUtil.Entry> items2 = new ConcurrentHashMap<>();

   private RemoteUtil() {
   }

   public static synchronized List<RemoteUtil.Entry> method1826(Script script) {
      return items.computeIfAbsent(script, var0x -> new ArrayList<>());
   }

   public static RemoteUtil.Entry method1827(Object obj) {
      if (obj instanceof Scriptable scriptable && scriptable.get("__img", scriptable) instanceof Number number) {
         RemoteUtil.Entry entry = items2.get(number.intValue());
         if (entry != null && !entry.flag) {
            return entry;
         } else {
            throw ScriptValueUtils.method6251("картинка уже освобождена");
         }
      } else {
         throw ScriptValueUtils.method6251("ожидалась картинка (объект из image.load / image.create)");
      }
   }

   private static void method1828(Script script, long l) {
      List<RemoteUtil.Entry> entries = method1826(script);
      if (entries.size() >= 64) {
         throw ScriptValueUtils.method6251("у скрипта больше 64 картинок — освободи лишние через img.release()");
      } else {
         long l2 = 0L;

         for (RemoteUtil.Entry entry : entries) {
            l2 += entry.method1849();
         }

         if (l2 + l > 25165824L) {
            throw ScriptValueUtils.method6251("картинки скрипта занимают слишком много памяти");
         }
      }
   }

   public static RemoteUtil.Entry method1829(Script script, List<NativeImage> nativeImages, List<Integer> integers) {
      long l = 0L;

      for (NativeImage nativeImage : nativeImages) {
         l += (long)nativeImage.getWidth() * nativeImage.getHeight();
      }

      try {
         method1828(script, l);
      } catch (RuntimeException e) {
         for (NativeImage nativeImage : nativeImages) {
            nativeImage.close();
         }

         throw e;
      }

      RemoteUtil.Entry entry = new RemoteUtil.Entry(script);
      entry.items.addAll(nativeImages);
      entry.items2.addAll(integers);
      method1826(script).add(entry);
      items2.put(entry.intValue, entry);
      return entry;
   }

   public static void method1830(Script workspace) {
      List list;
      synchronized (RemoteUtil.class) {
         list = items.remove(workspace);
      }

      if (list != null) {
         for (Object obj : (Iterable<?>)(Object)(new ArrayList(list))) {
            RemoteUtil.Entry entry = (RemoteUtil.Entry)obj;
            items2.remove(entry.intValue);
            Minecraft minecraft = Minecraft.getInstance();

            for (Identifier location : entry.items3) {
               try {
                  minecraft.getTextureManager().release(location);
               } catch (RuntimeException e) {
               }
            }

            if (entry.items4.isEmpty()) {
               for (NativeImage nativeImage : entry.items) {
                  nativeImage.close();
               }
            }

            entry.flag = true;
         }
      }
   }

   private static NativeImage method1831(BufferedImage bufferedImage) {
      int width = Math.min(2048, bufferedImage.getWidth());
      int height = Math.min(2048, bufferedImage.getHeight());
      int[] rgbArray = new int[width];
      NativeImage nativeImage = new NativeImage(width, height, false);

      for (int i = 0; i < height; i++) {
         bufferedImage.getRGB(0, i, width, 1, rgbArray, 0, width);

         for (int j = 0; j < width; j++) {
            nativeImage.setPixel(j, i, rgbArray[j]);
         }
      }

      return nativeImage;
   }

   public static List<Object[]> method1832(byte[] allBytes, String string) throws IOException {
      ArrayList<Object[]> objectses = new ArrayList<>();
      if (string.toLowerCase(Locale.ROOT).endsWith(".gif")) {
         try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(allBytes))) {
            var imageReadersByFormatName = ImageIO.getImageReadersByFormatName("gif");
            if (!imageReadersByFormatName.hasNext()) {
               throw new IOException("gif не поддерживается");
            }

            ImageReader imageReader = (ImageReader)imageReadersByFormatName.next();

            try {
               imageReader.setInput(imageInputStream, false, false);
               int width = imageReader.getWidth(0);
               int height = imageReader.getHeight(0);
               IIOMetadata streamMetadata = imageReader.getStreamMetadata();
               if (streamMetadata != null) {
                  Node tree = streamMetadata.getAsTree("javax_imageio_gif_stream_1.0");

                  for (Node firstChild = tree.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                     if ("LogicalScreenDescriptor".equals(firstChild.getNodeName())) {
                        NamedNodeMap attributes = firstChild.getAttributes();
                        width = Math.max(1, Integer.parseInt(attributes.getNamedItem("logicalScreenWidth").getNodeValue()));
                        height = Math.max(1, Integer.parseInt(attributes.getNamedItem("logicalScreenHeight").getNodeValue()));
                     }
                  }
               }

               ImageLoader.method233(width, height);
               int n = Math.min(2048, width);
               int height2 = Math.min(2048, height);
               long l = (long)n * height2;
               int n2 = (int)Math.max(1L, Math.min(120L, 25165824L / Math.max(1L, l)));
               BufferedImage bufferedImage = new BufferedImage(n, height2, 2);

               for (int i = 0; i < n2; i++) {
                  int width2;
                  int height3;
                  try {
                     width2 = imageReader.getWidth(i);
                     height3 = imageReader.getHeight(i);
                  } catch (IndexOutOfBoundsException e) {
                     break;
                  }

                  ImageLoader.method233(width2, height3);
                  if (width2 > width * 2 || height3 > height * 2) {
                     throw new IOException("повреждённый gif");
                  }

                  int n3 = 0;
                  int n4 = 0;
                  int n5 = 100;
                  IIOMetadata imageMetadata = imageReader.getImageMetadata(i);
                  Node tree = imageMetadata.getAsTree("javax_imageio_gif_image_1.0");

                  for (Node firstChild = tree.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                     NamedNodeMap attributes = firstChild.getAttributes();
                     if ("ImageDescriptor".equals(firstChild.getNodeName())) {
                        n3 = Integer.parseInt(attributes.getNamedItem("imageLeftPosition").getNodeValue());
                        n4 = Integer.parseInt(attributes.getNamedItem("imageTopPosition").getNodeValue());
                     } else if ("GraphicControlExtension".equals(firstChild.getNodeName())) {
                        n5 = Math.max(20, Integer.parseInt(attributes.getNamedItem("delayTime").getNodeValue()) * 10);
                     }
                  }

                  BufferedImage bufferedImage2 = imageReader.read(i);
                  Graphics2D graphics = bufferedImage.createGraphics();
                  graphics.drawImage(bufferedImage2, n3, n4, null);
                  graphics.dispose();
                  objectses.add(new Object[]{method1831(bufferedImage), n5});
               }
            } finally {
               imageReader.dispose();
            }
         }

         if (objectses.isEmpty()) {
            throw new IOException("пустой gif");
         }
      } else {
         BufferedImage bufferedImage = ImageLoader.method234(allBytes, 2048);
         objectses.add(new Object[]{method1831(bufferedImage), 0});
      }

      return objectses;
   }

   public static RemoteUtil.Entry method1833(Script workspace, byte[] allBytes, String string) throws IOException {
      List<Object[]> objectses = method1832(allBytes, string);
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (Object[] objects : objectses) {
         arrayList.add((NativeImage)objects[0]);
         arrayList2.add((Integer)objects[1]);
      }

      return method1829(workspace, arrayList, arrayList2);
   }

   public static Path method1834(Script workspace, String str) {
      if (workspace.getPath() == null) {
         throw ScriptValueUtils.method6251("файлы доступны только скриптам-папкам");
      } else {
         Path path = workspace.getPath().toAbsolutePath().normalize();
         Path path2 = path.resolve(str).toAbsolutePath().normalize();
         if (!path2.startsWith(path)) {
            throw ScriptValueUtils.method6251("путь вне папки скрипта");
         } else {
            return path2;
         }
      }
   }

   private static void method1835(Storage storage, Script workspace, Function function, Supplier<Object[]> supplier) {
      Minecraft.getInstance().execute(() -> {
         if (workspace.isRunning() && function != null) {
            Scriptable scriptable = ScriptScreenApi.method7044(workspace);
            if (scriptable != null) {
               storage.method1321(workspace, function, (Object[])supplier.get());
            }
         }
      });
   }

   public static Scriptable method1836(Scriptable scope, Storage storage, RemoteUtil.Entry entry) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6277("__img", (double)entry.intValue);
      entry2.method6274("width", var1x -> (double)entry.width());
      entry2.method6274("height", var1x -> (double)entry.height());
      entry2.method6274("frames", var1x -> (double)entry.items.size());
      entry2.method6274("animated", var1x -> entry.items.size() > 1);
      entry2.method6274("get", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         return var2x >= 0 && var3x >= 0 && var2x < entry.width() && var3x < entry.height() ? (double)entry.method1848().getPixel(var2x, var3x) : 0.0;
      });
      entry2.method6274("set", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         if (var2x >= 0 && var3x >= 0 && var2x < entry.width() && var3x < entry.height()) {
            entry.method1848().setPixel(var2x, var3x, ScriptValueUtils.method6258(var1x, 2, -1));
            entry.dirty = true;
         }

         return null;
      });
      entry2.method6274("fill", var1x -> {
         int var2x = ScriptValueUtils.method6258(var1x, 0, 0);
         method1837(entry, 0, 0, entry.width(), entry.height(), var2x);
         return null;
      });
      entry2.method6274(
         "rect",
         var1x -> {
            method1837(
               entry,
               ScriptValueUtils.method6257(var1x, 0, 0),
               ScriptValueUtils.method6257(var1x, 1, 0),
               ScriptValueUtils.method6257(var1x, 2, 0),
               ScriptValueUtils.method6257(var1x, 3, 0),
               ScriptValueUtils.method6258(var1x, 4, -1)
            );
            return null;
         }
      );
      entry2.method6274("circle", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         int n = Math.max(0, ScriptValueUtils.method6257(var1x, 2, 1));
         int pixel = ScriptValueUtils.method6258(var1x, 3, -1);

         for (int i = Math.max(0, var3x - n); i <= Math.min(entry.height() - 1, var3x + n); i++) {
            for (int j = Math.max(0, var2x - n); j <= Math.min(entry.width() - 1, var2x + n); j++) {
               if ((j - var2x) * (j - var2x) + (i - var3x) * (i - var3x) <= n * n) {
                  method1838(entry.method1848(), j, i, pixel);
               }
            }
         }

         entry.dirty = true;
         return null;
      });
      entry2.method6274("line", var1x -> {
         int var2x = ScriptValueUtils.method6257(var1x, 0, 0);
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         int n = ScriptValueUtils.method6257(var1x, 2, 0);
         int n2 = ScriptValueUtils.method6257(var1x, 3, 0);
         int pixel = ScriptValueUtils.method6258(var1x, 4, -1);
         int n3 = Math.abs(n - var2x);
         int n4 = -Math.abs(n2 - var3x);
         int n5 = var2x < n ? 1 : -1;
         int n6 = var3x < n2 ? 1 : -1;
         int n7 = n3 + n4;

         for (int i = 0; i < 8192; i++) {
            if (var2x >= 0 && var3x >= 0 && var2x < entry.width() && var3x < entry.height()) {
               method1838(entry.method1848(), var2x, var3x, pixel);
            }

            if (var2x == n && var3x == n2) {
               break;
            }

            int n8 = 2 * n7;
            if (n8 >= n4) {
               n7 += n4;
               var2x += n5;
            }

            if (n8 <= n3) {
               n7 += n3;
               var3x += n6;
            }
         }

         entry.dirty = true;
         return null;
      });
      entry2.method6274("draw", var1x -> {
         RemoteUtil.Entry var2x = method1827(ScriptValueUtils.arg(var1x, 0));
         int var3x = ScriptValueUtils.method6257(var1x, 1, 0);
         int n = ScriptValueUtils.method6257(var1x, 2, 0);
         int n2 = ScriptValueUtils.method6257(var1x, 3, var2x.width());
         int n3 = ScriptValueUtils.method6257(var1x, 4, var2x.height());

         for (int i = 0; i < n3; i++) {
            for (int j = 0; j < n2; j++) {
               int x = var3x + j;
               int y = n + i;
               if (x >= 0 && y >= 0 && x < entry.width() && y < entry.height()) {
                  int x2 = j * var2x.width() / Math.max(1, n2);
                  int y2 = i * var2x.height() / Math.max(1, n3);
                  method1838(entry.method1848(), x, y, var2x.method1848().getPixel(x2, y2));
               }
            }
         }

         entry.dirty = true;
         return null;
      });
      entry2.method6274("filter", var1x -> {
         method1839(entry, ScriptValueUtils.method6254(var1x, 0, "grayscale").toLowerCase(Locale.ROOT), ScriptValueUtils.method6256(var1x, 1, 1.0));
         entry.dirty = true;
         return null;
      });
      entry2.method6274("crop", var3x -> {
         int n = Math.max(0, ScriptValueUtils.method6257(var3x, 0, 0));
         int n2 = Math.max(0, ScriptValueUtils.method6257(var3x, 1, 0));
         int width = Math.max(1, Math.min(entry.width() - n, ScriptValueUtils.method6257(var3x, 2, entry.width())));
         int height = Math.max(1, Math.min(entry.height() - n2, ScriptValueUtils.method6257(var3x, 3, entry.height())));
         NativeImage nativeImage = new NativeImage(width, height, false);

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               nativeImage.setPixel(j, i, entry.method1848().getPixel(n + j, n2 + i));
            }
         }

         return method1836(scope, storage, method1829(entry.script, List.of(nativeImage), List.of(0)));
      });
      entry2.method6274("resize", var3x -> {
         int width = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 0, entry.width())));
         int height = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 1, entry.height())));
         NativeImage nativeImage = new NativeImage(width, height, false);

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               nativeImage.setPixel(j, i, entry.method1848().getPixel(j * entry.width() / width, i * entry.height() / height));
            }
         }

         return method1836(scope, storage, method1829(entry.script, List.of(nativeImage), List.of(0)));
      });
      entry2.method6274("copy", var3x -> {
         NativeImage nativeImage = new NativeImage(entry.width(), entry.height(), false);
         nativeImage.copyFrom(entry.method1848());
         return method1836(scope, storage, method1829(entry.script, List.of(nativeImage), List.of(0)));
      });
      entry2.method6274("flip", var1x -> {
         boolean var2x = !ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true);
         NativeImage var3x = entry.method1848();
         int width = var3x.getWidth();
         int height = var3x.getHeight();

         for (int i = 0; i < (var2x ? height : height / 2); i++) {
            for (int j = 0; j < (var2x ? width / 2 : width); j++) {
               int x = var2x ? width - 1 - j : j;
               int y = var2x ? i : height - 1 - i;
               int pixel = var3x.getPixel(j, i);
               int n = var3x.getPixel(x, y);
               var3x.setPixel(j, i, n);
               var3x.setPixel(x, y, pixel);
            }
         }

         entry.dirty = true;
         return null;
      });
      entry2.method6274("rotate", var3x -> {
         int n = (ScriptValueUtils.method6257(var3x, 0, 90) / 90 % 4 + 4) % 4;
         NativeImage nativeImage = entry.method1848();
         int width = nativeImage.getWidth();
         int height = nativeImage.getHeight();
         boolean flag = n % 2 == 1;
         NativeImage nativeImage2 = new NativeImage(flag ? height : width, flag ? width : height, false);

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               int pixel = nativeImage.getPixel(j, i);
               switch (n) {
                  case 1:
                     nativeImage2.setPixel(height - 1 - i, j, pixel);
                     break;
                  case 2:
                     nativeImage2.setPixel(width - 1 - j, height - 1 - i, pixel);
                     break;
                  case 3:
                     nativeImage2.setPixel(i, width - 1 - j, pixel);
                     break;
                  default:
                     nativeImage2.setPixel(j, i, pixel);
               }
            }
         }

         return method1836(scope, storage, method1829(entry.script, List.of(nativeImage2), List.of(0)));
      });
      entry2.method6274("average", var1x -> {
         long var2x = 0L;
         long l = 0L;
         long l2 = 0L;
         long l3 = 0L;
         NativeImage nativeImage = entry.method1848();
         int n = Math.max(1, nativeImage.getWidth() * nativeImage.getHeight() / 40000);

         for (int i = 0; i < nativeImage.getWidth() * nativeImage.getHeight(); i += n) {
            int pixel = nativeImage.getPixel(i % nativeImage.getWidth(), i / nativeImage.getWidth());
            if (pixel >>> 24 >= 16) {
               var2x += pixel >> 16 & 0xFF;
               l += pixel >> 8 & 0xFF;
               l2 += pixel & 0xFF;
               l3++;
            }
         }

         return l3 == 0L ? 0.0 : (double)(0xFF000000 | (int)(var2x / l3) << 16 | (int)(l / l3) << 8 | (int)(l2 / l3));
      });
      entry2.method6274("save", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "image.png");
         if (!var2x.toLowerCase(Locale.ROOT).endsWith(".png")) {
            var2x = var2x + ".png";
         }

         Path var3x = method1834(entry.script, var2x.contains("/") ? var2x : "images/" + var2x);

         try {
            Files.createDirectories(var3x.getParent());
            entry.method1848().writeToFile(var3x);
         } catch (IOException e) {
            throw ScriptValueUtils.method6251("не удалось сохранить: " + e.getMessage());
         }

         return entry.script.getPath().relativize(var3x).toString().replace('\\', '/');
      });
      entry2.method6274("release", var1x -> {
         entry.release();
         return null;
      });
      entry.scriptable = entry2.getScriptableObject();
      return entry.scriptable;
   }

   private static void method1837(RemoteUtil.Entry entry, int n, int n2, int width, int height, int pixel) {
      for (int i = Math.max(0, n2); i < Math.min(entry.height(), n2 + height); i++) {
         for (int j = Math.max(0, n); j < Math.min(entry.width(), n + width); j++) {
            entry.method1848().setPixel(j, i, pixel);
         }
      }

      entry.dirty = true;
   }

   private static void method1838(NativeImage nativeImage, int x, int y, int pixel) {
      int n = pixel >>> 24;
      if (n == 255) {
         nativeImage.setPixel(x, y, pixel);
      } else if (n != 0) {
         int pixel2 = nativeImage.getPixel(x, y);
         float f = n / 255.0F;
         int n2 = Math.round((pixel2 >> 16 & 0xFF) * (1.0F - f) + (pixel >> 16 & 0xFF) * f);
         int n3 = Math.round((pixel2 >> 8 & 0xFF) * (1.0F - f) + (pixel >> 8 & 0xFF) * f);
         int n4 = Math.round((pixel2 & 0xFF) * (1.0F - f) + (pixel & 0xFF) * f);
         int n5 = Math.min(255, (pixel2 >>> 24) + n);
         nativeImage.setPixel(x, y, n5 << 24 | n2 << 16 | n3 << 8 | n4);
      }
   }

   private static void method1839(RemoteUtil.Entry entry, String lowerCase, double d) {
      NativeImage nativeImage = entry.method1848();
      int width = nativeImage.getWidth();
      int height = nativeImage.getHeight();
      if (lowerCase.equals("blur")) {
         int n = (int)Math.max(1.0, Math.min(16.0, d));
         int[] ints = new int[width * height];

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               ints[i * width + j] = nativeImage.getPixel(j, i);
            }
         }

         ints = method1840(ints, width, height, n, true);
         ints = method1840(ints, width, height, n, false);

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               nativeImage.setPixel(j, i, ints[i * width + j]);
            }
         }
      } else {
         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               int pixel = nativeImage.getPixel(j, i);
               int n = pixel >>> 24;
               int n2 = pixel >> 16 & 0xFF;
               int n3 = pixel >> 8 & 0xFF;
               int n4 = pixel & 0xFF;
               switch (lowerCase) {
                  case "grayscale":
                  case "gray":
                     int n5 = (int)(n2 * 0.299 + n3 * 0.587 + n4 * 0.114);
                     n2 = method1842(n2, n5, d);
                     n3 = method1842(n3, n5, d);
                     n4 = method1842(n4, n5, d);
                     break;
                  case "invert":
                     n2 = method1842(n2, 255 - n2, d);
                     n3 = method1842(n3, 255 - n3, d);
                     n4 = method1842(n4, 255 - n4, d);
                     break;
                  case "sepia":
                     int n6 = (int)Math.min(255.0, n2 * 0.393 + n3 * 0.769 + n4 * 0.189);
                     int n7 = (int)Math.min(255.0, n2 * 0.349 + n3 * 0.686 + n4 * 0.168);
                     int n8 = (int)Math.min(255.0, n2 * 0.272 + n3 * 0.534 + n4 * 0.131);
                     n2 = method1842(n2, n6, d);
                     n3 = method1842(n3, n7, d);
                     n4 = method1842(n4, n8, d);
                     break;
                  case "brightness":
                     n2 = method1843(n2 * d);
                     n3 = method1843(n3 * d);
                     n4 = method1843(n4 * d);
                     break;
                  case "contrast":
                     n2 = method1843((n2 - 128) * d + 128.0);
                     n3 = method1843((n3 - 128) * d + 128.0);
                     n4 = method1843((n4 - 128) * d + 128.0);
                     break;
                  case "threshold":
                     int n9 = (n2 + n3 + n4) / 3 >= d * 255.0 ? 255 : 0;
                     n2 = n9;
                     n3 = n9;
                     n4 = n9;
                     break;
                  case "opacity":
                     n = method1843(n * d);
                     break;
                  case "pixelate":
                     int n10 = (int)Math.max(2.0, Math.min(64.0, d));
                     int pixel2 = nativeImage.getPixel(j - j % n10, i - i % n10);
                     n = pixel2 >>> 24;
                     n2 = pixel2 >> 16 & 0xFF;
                     n3 = pixel2 >> 8 & 0xFF;
                     n4 = pixel2 & 0xFF;
                     break;
                  default:
                     throw ScriptValueUtils.method6251("фильтры: grayscale, invert, sepia, brightness, contrast, threshold, opacity, blur, pixelate");
               }

               nativeImage.setPixel(j, i, n << 24 | n2 << 16 | n3 << 8 | n4);
            }
         }
      }
   }

   private static int[] method1840(int[] ints, int width, int height, int n, boolean flag) {
      int[] ints2 = new int[ints.length];
      int n2 = flag ? height : width;
      int n3 = flag ? width : height;
      int n4 = n * 2 + 1;

      for (int i = 0; i < n2; i++) {
         long l = 0L;
         long l2 = 0L;
         long l3 = 0L;
         long l4 = 0L;

         for (int j = -n; j <= n; j++) {
            int n5 = ints[method1841(i, Math.max(0, Math.min(n3 - 1, j)), width, flag)];
            l += n5 >>> 24;
            l2 += n5 >> 16 & 0xFF;
            l3 += n5 >> 8 & 0xFF;
            l4 += n5 & 0xFF;
         }

         for (int j = 0; j < n3; j++) {
            ints2[method1841(i, j, width, flag)] = (int)(l / n4) << 24 | (int)(l2 / n4) << 16 | (int)(l3 / n4) << 8 | (int)(l4 / n4);
            int n5 = ints[method1841(i, Math.max(0, j - n), width, flag)];
            int n6 = ints[method1841(i, Math.min(n3 - 1, j + n + 1), width, flag)];
            l += (n6 >>> 24) - (n5 >>> 24);
            l2 += (n6 >> 16 & 0xFF) - (n5 >> 16 & 0xFF);
            l3 += (n6 >> 8 & 0xFF) - (n5 >> 8 & 0xFF);
            l4 += (n6 & 0xFF) - (n5 & 0xFF);
         }
      }

      return ints2;
   }

   private static int method1841(int n, int n2, int width, boolean flag) {
      return flag ? n * width + n2 : n2 * width + n;
   }

   private static int method1842(int n, int n2, double d) {
      return method1843(n + (n2 - n) * Math.max(0.0, Math.min(1.0, d)));
   }

   private static int method1843(double d) {
      return (int)Math.max(0L, Math.min(255L, Math.round(d)));
   }

   public static Scriptable method1844(Script workspace, Storage storage, Scriptable scope) {
      workspace.method1413(() -> method1830(workspace));
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("load", var3x -> {
         String str = ScriptValueUtils.method6254(var3x, 0, "");
         Path path = method1834(workspace, str);
         if (!Files.isRegularFile(path)) {
            throw ScriptValueUtils.method6251("нет файла " + str);
         } else {
            try {
               if (Files.size(path) > 16777216L) {
                  throw ScriptValueUtils.method6251("картинка больше 16 МБ");
               } else {
                  return method1836(scope, storage, method1833(workspace, Files.readAllBytes(path), path.getFileName().toString()));
               }
            } catch (IOException e) {
               throw ScriptValueUtils.method6251(e.getMessage());
            }
         }
      });
      entry2.method6274("create", var3x -> {
         int width = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 0, 64)));
         int height = Math.max(1, Math.min(2048, ScriptValueUtils.method6257(var3x, 1, 64)));
         NativeImage nativeImage = new NativeImage(width, height, false);
         int pixel = ScriptValueUtils.method6258(var3x, 2, 0);

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               nativeImage.setPixel(j, i, pixel);
            }
         }

         return method1836(scope, storage, method1829(workspace, List.of(nativeImage), List.of(0)));
      });
      entry2.method6274("list", var2x -> {
         ArrayList var3x = new ArrayList();
         if (workspace.getPath() == null) {
            return ScriptValueUtils.method6267(scope, var3x);
         } else {
            try (Stream<java.nio.file.Path> walk = Files.walk(workspace.getPath(), 3)) {
               walk.filter(var0xx -> Files.isRegularFile(var0xx)).forEach(var2xx -> {
                  String var3xx = var2xx.getFileName().toString().toLowerCase(Locale.ROOT);
                  if (var3xx.endsWith(".png") || var3xx.endsWith(".jpg") || var3xx.endsWith(".jpeg") || var3xx.endsWith(".gif") || var3xx.endsWith(".bmp")) {
                     var3x.add(workspace.getPath().relativize(var2xx).toString().replace('\\', '/'));
                  }
               });
            } catch (IOException e) {
            }

            return ScriptValueUtils.method6267(scope, var3x);
         }
      });
      entry2.method6274("screenshot", var3x -> {
         ScriptPermissions.method4(workspace, "screenshot");
         var function = ScriptValueUtils.method6260(var3x, 0);
         int downscaleFactor = Math.max(1, Math.min(8, ScriptValueUtils.method6257(var3x, 1, 2)));
         Minecraft minecraft = Minecraft.getInstance();
         Screenshot.takeScreenshot(minecraft.gameRenderer.mainRenderTarget(), downscaleFactor, var5x -> minecraft.execute(() -> {
            if (!workspace.isRunning()) {
               var5x.close();
            } else {
               try {
                  RemoteUtil.Entry var5xx = method1829(workspace, List.of(var5x), List.of(0));
                  storage.method1321(workspace, function, method1836(scope, storage, var5xx));
               } catch (RuntimeException var6x) {
                  storage.method1327(workspace, "error", "screenshot: " + var6x.getMessage());
               }
            }
         }));
         return null;
      });
      entry2.method6274("pick", var3x -> {
         var function = ScriptValueUtils.method6260(var3x, 0);
         if (workspace.getPath() == null) {
            throw ScriptValueUtils.method6251("image.pick доступен только скриптам-папкам");
         } else {
            Thread.ofPlatform().daemon(true).name("zenith-image-pick").start(() -> {
               MemoryStack stackPush = MemoryStack.stackPush();

               String var4x;
               try {
                  PointerBuffer mallocPointer = stackPush.mallocPointer(5);

                  for (String text : new String[]{"*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"}) {
                     mallocPointer.put(stackPush.UTF8(text));
                  }

                  mallocPointer.flip();
                  var4x = FileDialogs.tinyfd_openFileDialog("Выбери картинку", "", mallocPointer, "Картинки", false);
               } catch (Throwable e) {
                  if (stackPush != null) {
                     try {
                        stackPush.close();
                     } catch (Throwable ex) {
                        e.addSuppressed(ex);
                     }
                  }

                  throw e;
               }

               if (stackPush != null) {
                  stackPush.close();
               }

               if (var4x == null) {
                  method1835(storage, workspace, function, () -> new Object[]{null});
               } else {
                  try {
                     Path path = Path.of(var4x);
                     if (Files.size(path) > 16777216L) {
                        throw new IOException("картинка больше 16 МБ");
                     }

                     byte[] allBytes = Files.readAllBytes(path);
                     Path dir = method1834(workspace, "images");
                     Files.createDirectories(dir);
                     String string = path.getFileName().toString().replaceAll("[^\\p{L}\\p{N}_.-]", "_");
                     Files.copy(path, dir.resolve(string), StandardCopyOption.REPLACE_EXISTING);
                     Minecraft.getInstance().execute(() -> {
                        if (workspace.isRunning()) {
                           try {
                              RemoteUtil.Entry var6x = method1833(workspace, allBytes, string);
                              storage.method1321(workspace, function, method1836(scope, storage, var6x));
                           } catch (Exception e) {
                              storage.method1327(workspace, "error", "image.pick: " + e.getMessage());
                           }
                        }
                     });
                  } catch (IOException e) {
                     storage.method1327(workspace, "error", "image.pick: " + e.getMessage());
                  }
               }
            });
            return null;
         }
      });
      entry2.method6274("fromUrl", var3x -> {
         String str = ScriptValueUtils.method6254(var3x, 0, "");
         var function = ScriptValueUtils.method6260(var3x, 1);
         ScriptNetApi.method3279(workspace, str, 16777216L, (var5x, str2) -> Minecraft.getInstance().execute(() -> {
            if (workspace.isRunning()) {
               if (str2 != null) {
                  storage.method1321(workspace, function, null, str2);
               } else {
                  try {
                     String string = str.toLowerCase(Locale.ROOT).contains(".gif") ? "remote.gif" : "remote.png";
                     storage.method1321(workspace, function, method1836(scope, storage, method1833(workspace, var5x, string)), null);
                  } catch (Exception e) {
                     storage.method1321(workspace, function, null, e.getMessage());
                  }
               }
            }
         }));
         return null;
      });
      return entry2.getScriptableObject();
   }

   public static Scriptable method1845(Script workspace, Storage storage, Scriptable scope) {
      HashMap map = new HashMap();
      ArrayList<ScriptSoundLoader.Entry> entries = new ArrayList();
      workspace.method1413(() -> {
         for (ScriptSoundLoader.Entry var2x : entries) {
            var2x.stopped = true;
         }
      });
      java.util.function.Function function = var2x -> map.computeIfAbsent(var2x, var1xx -> {
         try {
            return ScriptSoundLoader.method735(method1834(workspace, (String)var1xx));
         } catch (IOException var3x) {
            throw ScriptValueUtils.method6251(var1xx + ": " + var3x.getMessage());
         }
      });
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      BiFunction<Object[], Boolean, Object> biFunction = (var3x, var4x) -> {
         String var5x = ScriptValueUtils.method6254(var3x, 0, "");
         float var6x = (float)Math.max(0.0, Math.min(4.0, ScriptValueUtils.method6256(var3x, var4x ? 4 : 1, 1.0)));
         float var7x = (float)Math.max(0.25, Math.min(4.0, ScriptValueUtils.method6256(var3x, var4x ? 5 : 2, 1.0)));
         String lowerCase = var5x.toLowerCase(Locale.ROOT);
         if (!lowerCase.endsWith(".ogg") && !lowerCase.endsWith(".wav")) {
            Identifier tryParse = Identifier.tryParse(var5x.contains(":") ? var5x : "minecraft:" + var5x);
            if (tryParse == null) {
               throw ScriptValueUtils.method6251("неверный id звука: " + var5x);
            } else {
               SoundEvent sound = BuiltInRegistries.SOUND_EVENT.containsKey(tryParse) ? BuiltInRegistries.SOUND_EVENT.getValue(tryParse) : SoundEvent.createVariableRangeEvent(tryParse);
               Minecraft minecraft = Minecraft.getInstance();
               SimpleSoundInstance simpleSoundInstance = var4x
                  ? new SimpleSoundInstance(
                     sound,
                     SoundSource.MASTER,
                     var6x,
                     var7x,
                     RandomSource.create(),
                     ScriptValueUtils.method6256(var3x, 1, 0.0),
                     ScriptValueUtils.method6256(var3x, 2, 0.0),
                     ScriptValueUtils.method6256(var3x, 3, 0.0)
                  )
                  : SimpleSoundInstance.forUI(sound, var7x, var6x);
               minecraft.getSoundManager().play(simpleSoundInstance);
               return ScriptValueUtils.method6247(scope).method6274("stop", var2xx -> {
                  minecraft.getSoundManager().stop(simpleSoundInstance);
                  return null;
               }).method6274("playing", var2xx -> minecraft.getSoundManager().isActive(simpleSoundInstance)).getScriptableObject();
            }
         } else {
            ScriptSoundLoader.Data data = (ScriptSoundLoader.Data)function.apply(var5x);
            ScriptSoundLoader.Entry entry = ScriptSoundLoader.method738(data, var6x, var7x, false);
            if (var4x) {
               entry.doubleValue = ScriptValueUtils.method6256(var3x, 1, 0.0);
               entry.doubleValue2 = ScriptValueUtils.method6256(var3x, 2, 0.0);
               entry.doubleValue3 = ScriptValueUtils.method6256(var3x, 3, 0.0);
            }

            entries.removeIf(var0xx -> !ScriptSoundLoader.items.containsKey(var0xx.intValue));
            entries.add(entry);
            return method1846(scope, entry);
         }
      };
      entry2.method6274("play", var1x -> biFunction.apply(var1x, false));
      entry2.method6274("play3d", var1x -> biFunction.apply(var1x, true));
      entry2.method6274(
         "loop",
         var3x -> {
            ScriptSoundLoader.Entry var4x = ScriptSoundLoader.method738(
               (ScriptSoundLoader.Data)function.apply(ScriptValueUtils.method6254(var3x, 0, "")),
               (float)ScriptValueUtils.method6256(var3x, 1, 1.0),
               (float)ScriptValueUtils.method6256(var3x, 2, 1.0),
               true
            );
            entries.removeIf(var0xx -> !ScriptSoundLoader.items.containsKey(var0xx.intValue));
            entries.add(var4x);
            return method1846(scope, var4x);
         }
      );
      entry2.method6274(
         "load",
         var2x -> {
            ScriptSoundLoader.Data var3x = (ScriptSoundLoader.Data)function.apply(ScriptValueUtils.method6254(var2x, 0, ""));
            return ScriptValueUtils.method6247(scope)
               .method6277("duration", var3x.method743())
               .method6277("channels", (double)var3x.getIntValue())
               .method6277("rate", (double)var3x.getIntValue2())
               .getScriptableObject();
         }
      );
      entry2.method6274("stopAll", var1x -> {
         for (ScriptSoundLoader.Entry var3x : entries) {
            var3x.stopped = true;
         }

         entries.clear();
         return null;
      });
      entry2.method6274("ids", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "").toLowerCase(Locale.ROOT);
         ArrayList var3x = new ArrayList();

         for (Identifier var5x : BuiltInRegistries.SOUND_EVENT.keySet()) {
            String var6x = var5x.toString();
            if (var2x.isEmpty() || var6x.contains(var2x)) {
               var3x.add(var6x);
            }

            if (var3x.size() >= 300) {
               break;
            }
         }

         var3x.sort(null);
         return ScriptValueUtils.method6267(scope, var3x);
      });
      entry2.method6274("files", var2x -> {
         ArrayList var3x = new ArrayList();
         if (workspace.getPath() == null) {
            return ScriptValueUtils.method6267(scope, var3x);
         } else {
            try (Stream<java.nio.file.Path> var4x = Files.walk(workspace.getPath(), 3)) {
               var4x.filter(var0xx -> Files.isRegularFile(var0xx)).forEach(var2xx -> {
                  String var3xx = var2xx.getFileName().toString().toLowerCase(Locale.ROOT);
                  if (var3xx.endsWith(".ogg") || var3xx.endsWith(".wav")) {
                     var3x.add(workspace.getPath().relativize(var2xx).toString().replace('\\', '/'));
                  }
               });
            } catch (IOException e) {
            }

            return ScriptValueUtils.method6267(scope, var3x);
         }
      });
      return entry2.getScriptableObject();
   }

   private static Scriptable method1846(Scriptable scope, ScriptSoundLoader.Entry entry) {
      return ScriptValueUtils.method6247(scope)
         .method6274("stop", var1x -> {
            entry.stopped = true;
            return null;
         })
         .method6274("playing", var1x -> ScriptSoundLoader.items.containsKey(entry.intValue))
         .method6274("volume", var1x -> {
            entry.floatValue = (float)Math.max(0.0, Math.min(4.0, ScriptValueUtils.method6256(var1x, 0, entry.floatValue)));
            return null;
         })
         .method6274("pitch", var1x -> (double)entry.floatValue2)
         .method6274("progress", var1x -> entry.doubleValue5)
         .method6274("duration", var1x -> entry.data.method743())
         .method6274("move", var1x -> {
            entry.doubleValue = ScriptValueUtils.method6256(var1x, 0, 0.0);
            entry.doubleValue2 = ScriptValueUtils.method6256(var1x, 1, 0.0);
            entry.doubleValue3 = ScriptValueUtils.method6256(var1x, 2, 0.0);
            return null;
         })
         .method6274("range", var1x -> {
            entry.doubleValue4 = Math.max(1.0, Math.min(256.0, ScriptValueUtils.method6256(var1x, 0, 32.0)));
            return null;
         })
         .getScriptableObject();
   }

   public static void method1847(Storage storage, Script workspace, Function function, Object... scriptableObject) {
      Minecraft.getInstance().execute(() -> {
         if (workspace.isRunning()) {
            storage.method1321(workspace, function, scriptableObject);
         }
      });
   }

   public static final class Entry {
      public final Script script;
      public final int intValue = RemoteUtil.atomicInteger2.incrementAndGet();
      public final List<NativeImage> items = new ArrayList<>();
      public final List<Integer> items2 = new ArrayList<>();
      public final List<Identifier> items3 = new ArrayList<>();
      public final List<DynamicTexture> items4 = new ArrayList<>();
      public final long longValue = System.currentTimeMillis();
      public boolean dirty = true;
      public boolean flag;
      public Scriptable scriptable;

      public Entry(Script script) {
         this.script = script;
      }

      public NativeImage method1848() {
         return this.items.get(0);
      }

      public int width() {
         return this.method1848().getWidth();
      }

      public int height() {
         return this.method1848().getHeight();
      }

      public long method1849() {
         long l = 0L;

         for (NativeImage nativeImage : this.items) {
            l += (long)nativeImage.getWidth() * nativeImage.getHeight();
         }

         return l;
      }

      public Identifier texture() {
         if (this.flag) {
            return null;
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            if (this.items4.isEmpty()) {
               for (NativeImage image : this.items) {
                  Identifier location = Identifier.fromNamespaceAndPath("zenith", "script_image/" + RemoteUtil.atomicInteger.incrementAndGet());
                  DynamicTexture dynamicTexture = new DynamicTexture(() -> "zenith-script", image);
                  minecraft.getTextureManager().register(location, dynamicTexture);
                  this.items3.add(location);
                  this.items4.add(dynamicTexture);
               }

               this.dirty = false;
            } else if (this.dirty) {
               this.items4.get(0).upload();
               this.dirty = false;
            }

            if (this.items.size() == 1) {
               return this.items3.get(0);
            } else {
               long l = 0L;

               for (int n : this.items2) {
                  l += n;
               }

               long l2 = (System.currentTimeMillis() - this.longValue) % Math.max(1L, l);

               for (int i = 0; i < this.items2.size(); i++) {
                  l2 -= this.items2.get(i).intValue();
                  if (l2 < 0L) {
                     return this.items3.get(i);
                  }
               }

               return this.items3.get(0);
            }
         }
      }

      public void release() {
         if (!this.flag) {
            this.flag = true;
            Minecraft minecraft = Minecraft.getInstance();

            for (Identifier location : this.items3) {
               try {
                  minecraft.getTextureManager().release(location);
               } catch (RuntimeException e) {
               }
            }

            if (this.items4.isEmpty()) {
               for (NativeImage nativeImage : this.items) {
                  nativeImage.close();
               }
            }

            this.items3.clear();
            this.items4.clear();
            RemoteUtil.items2.remove(this.intValue);
            RemoteUtil.method1826(this.script).remove(this);
         }
      }
   }
}
