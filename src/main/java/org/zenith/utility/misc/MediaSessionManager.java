package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.NativeImage;
import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.BufferUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.utility.api.LyricsFetcher;
import org.zenith.utility.config.MediaHelperProcess;
import org.zenith.utility.render.TextureFilterUtils;

public final class MediaSessionManager {
   private static final Identifier[] identifiers = new Identifier[]{Zenith.method1501("media/art_a"), Zenith.method1501("media/art_b")};
   private static final Identifier[] identifiers2 = new Identifier[]{Zenith.method1501("media/blur_a"), Zenith.method1501("media/blur_b")};
   private static final Identifier[] identifiers3 = new Identifier[]{Zenith.method1501("media/small_a"), Zenith.method1501("media/small_b")};
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 96;
   private static final ZenithColor[] zenithColors = new ZenithColor[]{new ZenithColor(-8758017), new ZenithColor(-12965270), new ZenithColor(-5022465)};
   private static final MediaSessionManager INSTANCE = new MediaSessionManager();
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "zenith-media");
      thread.setDaemon(true);
      return thread;
   });
   private volatile IMediaSession iMediaSession;
   private volatile MediaInfo mediaInfo;
   private volatile boolean flag;
   private volatile long longValue;
   private long longValue2;
   private long seenAt;
   private String text = "";
   private long longValue3;
   private int intValue = -1;
   private int intValue2 = -1;
   private byte[] byteValues = new byte[0];
   private final boolean[] flags = new boolean[2];
   private final boolean[] flags2 = new boolean[2];
   private ZenithColor[] zenithColors2 = zenithColors;
   private ZenithColor[] zenithColors3 = zenithColors;
   private long longValue4;
   private double doubleValue = -1.0;
   private long longValue5;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private long longValue6;
   private long longValue7;
   private boolean flag2;
   private long longValue8;
   private long longValue9;
   private long longValue10;
   private boolean flag3;
   private double doubleValue5;
   private long longValue11;
   private boolean flag4;
   private double doubleValue6;
   private boolean flag5;
   private long longValue12;

   private MediaSessionManager() {
   }

   public static MediaSessionManager getInstance() {
      return INSTANCE;
   }

   public void method6000() {
      if (!this.flag) {
         this.flag = true;
         this.executorService.execute(() -> {
            try {
               long now = System.currentTimeMillis();
               ArrayList arrayList = new ArrayList();
               ArrayList arrayList2 = new ArrayList();

               for (IMediaSession mediaSession : MediaPlayerInfo.Instance.getMediaSessions()) {
                  MediaInfo media = mediaSession.getMedia();
                  if (media != null && (!media.getTitle().isEmpty() || !media.getArtist().isEmpty())) {
                     arrayList.add(mediaSession);
                     arrayList2.add(media);
                  }
               }

               IMediaSession iMediaSession = this.iMediaSession;
               int index = -1;

               for (int i = 0; i < arrayList.size() && index < 0; i++) {
                  if (((MediaInfo)arrayList2.get(i)).getPlaying()) {
                     index = i;
                  }
               }

               for (int i = 0; i < arrayList.size() && index < 0 && iMediaSession != null; i++) {
                  if (((IMediaSession)arrayList.get(i)).getOwner().equals(iMediaSession.getOwner())) {
                     index = i;
                  }
               }

               if (index >= 0) {
                  IMediaSession iMediaSession2 = (IMediaSession)arrayList.get(index);
                  MediaInfo mediaInfo = (MediaInfo)arrayList2.get(index);
                  String text = mediaInfo.getTitle() + "\u0000" + mediaInfo.getArtist();
                  boolean flag = !text.equals(this.text);
                  boolean flag2 = flag || (this.intValue < 0 ? now - this.longValue > 3000L : now - this.longValue > 20000L);
                  byte[] artworkPng = flag2 ? mediaInfo.getArtworkPng() : null;
                  if (flag2) {
                     this.longValue = now;
                  }

                  boolean flag3 = artworkPng != null && artworkPng.length > 0 && (flag || !Arrays.equals(artworkPng, this.byteValues));
                  BufferedImage bufferedImage = flag3 ? method6023(artworkPng) : null;
                  if (bufferedImage == null) {
                     flag3 = false;
                  }

                  boolean flag4 = flag3;
                  ZenithColor[] zenithColors = bufferedImage == null ? null : method6026(bufferedImage);
                  int[] ints = bufferedImage == null ? null : method6024(bufferedImage);
                  int[] ints2 = bufferedImage == null ? null : method6016(bufferedImage, 96);
                  NativeImage image = flag3 ? method6021(artworkPng) : null;
                  Minecraft.getInstance().execute(() -> this.method6001(iMediaSession2, mediaInfo, text, artworkPng, flag4, image, zenithColors, ints, ints2));
                  return;
               }

               Minecraft.getInstance().execute(() -> this.iMediaSession = null);
            } catch (Throwable e) {
               return;
            } finally {
               this.flag = false;
            }
         });
      }
   }

   private void method6001(
      IMediaSession iMediaSession, MediaInfo mediaInfo, String text, byte[] byteValues, boolean flag, NativeImage image, ZenithColor[] zenithColors2, int[] ints, int[] ints2
   ) {
      if (!flag && image != null) {
         image.close();
      }

      this.iMediaSession = iMediaSession;
      long now = System.currentTimeMillis();
      if (!text.equals(this.text)) {
         boolean flag2 = this.flag5;
         this.flag5 = true;
         this.text = text;
         this.longValue3 = now;
         this.doubleValue = -1.0;
         this.longValue7 = 0L;
         this.longValue8 = 0L;
         this.doubleValue6 = 0.0;
         this.flag3 = flag2;
         this.doubleValue5 = 0.0;
         this.longValue11 = now;
         this.flag4 = mediaInfo.getPlaying();
         MediaPlayerState.getInstance().method1669(iMediaSession.getOwner());
         if (!flag && this.intValue >= 0) {
            this.intValue2 = this.intValue;
            this.intValue = -1;
            this.byteValues = new byte[0];
            this.zenithColors3 = this.zenithColors2;
            this.zenithColors2 = zenithColors;
            this.longValue4 = now;
         }
      }

      if (mediaInfo.getPlaying()) {
         this.longValue9 = now;
      }

      if (this.flag3 && mediaInfo.getPlaying() != this.flag4) {
         this.doubleValue5 = this.method6003(now);
         this.longValue11 = now;
         this.flag4 = mediaInfo.getPlaying();
      }

      if (flag) {
         this.byteValues = byteValues;
         this.intValue2 = this.intValue;
         this.intValue = this.intValue2 == 0 ? 1 : 0;
         method6022(identifiers[this.intValue], image);
         TextureFilterUtils.smooth(identifiers[this.intValue]);
         this.flags[this.intValue] = ints != null && method6015(identifiers2[this.intValue], ints, 64);
         this.flags2[this.intValue] = ints2 != null && method6015(identifiers3[this.intValue], ints2, 96);
         this.zenithColors3 = this.zenithColors2;
         this.zenithColors2 = zenithColors2 == null ? zenithColors : zenithColors2;
         this.longValue4 = now;
      }

      this.mediaInfo = mediaInfo;
      this.longValue2 = now;
      this.seenAt = now;
      if (now - this.longValue8 > 900L && MediaHelperProcess.getInstance().method6859()) {
         this.longValue8 = now;
         MediaHelperProcess.getInstance().method6866(iMediaSession.getOwner());
         if (now - this.longValue10 > 3000L) {
            this.longValue10 = now;
            MediaHelperProcess.getInstance().method6868(iMediaSession.getOwner());
            MediaHelperProcess.getInstance().method6863(iMediaSession.getOwner());
         }
      }
   }

   public void method6002(double d, double d2, long l, boolean flag) {
      long now = System.currentTimeMillis();
      this.doubleValue3 = d;
      this.doubleValue4 = d2;
      this.longValue6 = l > 0L && l <= now ? l : now;
      this.longValue7 = now;
      this.longValue12 = now;
      this.flag2 = flag;
      this.doubleValue6 = d2;
      this.flag3 = true;
      this.doubleValue5 = d + (flag ? (now - this.longValue6) / 1000.0 : 0.0);
      this.longValue11 = now;
      this.flag4 = flag;
   }

   private double method6003(long now) {
      return this.doubleValue5 + (this.flag4 ? (now - this.longValue11) / 1000.0 : 0.0);
   }

   private boolean method6004() {
      return System.currentTimeMillis() - this.longValue12 < 15000L;
   }

   private boolean method6005() {
      return this.doubleValue4 > 0.0 && System.currentTimeMillis() - this.longValue7 < 4000L;
   }

   public boolean active() {
      return this.iMediaSession != null && this.mediaInfo != null;
   }

   public long getSeenAt() {
      return this.seenAt;
   }

   public long getLongValue9() {
      return this.longValue9;
   }

   public IMediaSession getIMediaSession() {
      return this.iMediaSession;
   }

   public String title() {
      return this.mediaInfo == null ? "" : this.mediaInfo.getTitle();
   }

   public String method6006() {
      return this.mediaInfo == null ? "" : this.mediaInfo.getArtist();
   }

   public boolean method6007() {
      return this.mediaInfo != null && this.mediaInfo.getPlaying();
   }

   public double method6008() {
      if (this.doubleValue6 > 0.0) {
         return this.doubleValue6;
      } else {
         double d = LyricsFetcher.method529(this.title(), this.method6006());
         if (d > 0.0) {
            return d;
         } else if (!this.method6004() && !this.flag3) {
            return this.mediaInfo == null ? 0.0 : this.mediaInfo.getDuration();
         } else {
            return 0.0;
         }
      }
   }

   public double method6009() {
      if (this.mediaInfo == null) {
         return 0.0;
      } else {
         long now = System.currentTimeMillis();
         if (this.doubleValue >= 0.0 && now - this.longValue5 < 2500L) {
            double d = this.method6007() ? (now - this.longValue5) / 1000.0 : 0.0;
            return this.method6010(this.doubleValue + d);
         } else {
            double d;
            if (this.method6005()) {
               double d2 = this.flag2 ? (now - this.longValue6) / 1000.0 : 0.0;
               d = this.method6010(this.doubleValue3 + d2);
            } else if (this.flag3) {
               d = this.method6010(this.method6003(now));
            } else if (this.method6004()) {
               d = 0.0;
            } else {
               double position = this.mediaInfo.getPosition();
               double d2 = this.method6007() ? Math.min(2.0, (now - this.longValue2) / 1000.0) : 0.0;
               d = this.method6010(position + d2);
            }

            if (Math.abs(d - this.doubleValue2) < 1.2 && d < this.doubleValue2 && this.method6007()) {
               d = this.doubleValue2;
            }

            this.doubleValue2 = d;
            return d;
         }
      }
   }

   private double method6010(double d) {
      double d2 = this.method6008();
      return d2 > 0.0 ? Math.max(0.0, Math.min(d2, d)) : Math.max(0.0, d);
   }

   public String getText() {
      return this.text;
   }

   public long getLongValue3() {
      return this.longValue3;
   }

   public Identifier method6011() {
      return this.intValue < 0 ? null : identifiers[this.intValue];
   }

   public Identifier method6012() {
      return this.intValue2 < 0 ? null : identifiers[this.intValue2];
   }

   public Identifier method6013() {
      return this.intValue < 0 ? null : (this.flags2[this.intValue] ? identifiers3[this.intValue] : identifiers[this.intValue]);
   }

   public Identifier method6014() {
      return this.intValue2 < 0 ? null : (this.flags2[this.intValue2] ? identifiers3[this.intValue2] : identifiers[this.intValue2]);
   }

   private static boolean method6015(Identifier location, int[] ints, int width) {
      try {
         NativeImage nativeImage = new NativeImage(width, width, false);

         for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
               nativeImage.setPixel(j, i, ints[i * width + j]);
            }
         }

         Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(location::toString, nativeImage));
         TextureFilterUtils.smooth(location);
         return true;
      } catch (Throwable e) {
         return false;
      }
   }

   private static int[] method6016(BufferedImage bufferedImage, int n) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int[] ints = new int[n * n];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            float f = (float)(j * width) / n;
            float f2 = (float)((j + 1) * width) / n;
            float f3 = (float)(i * height) / n;
            float f4 = (float)((i + 1) * height) / n;
            float f5 = 0.0F;
            float f6 = 0.0F;
            float f7 = 0.0F;
            float f8 = 0.0F;

            for (int k = (int)f3; k < Math.ceil(f4) && k < height; k++) {
               float f9 = Math.min((float)(k + 1), f4) - Math.max((float)k, f3);

               for (int m = (int)f; m < Math.ceil(f2) && m < width; m++) {
                  float f10 = f9 * (Math.min((float)(m + 1), f2) - Math.max((float)m, f));
                  if (!(f10 <= 0.0F)) {
                     int rgb = bufferedImage.getRGB(m, k);
                     f5 += (rgb >> 16 & 0xFF) * f10;
                     f6 += (rgb >> 8 & 0xFF) * f10;
                     f7 += (rgb & 0xFF) * f10;
                     f8 += f10;
                  }
               }
            }

            if (f8 <= 0.0F) {
               f8 = 1.0F;
            }

            ints[i * n + j] = 0xFF000000 | (int)(f5 / f8) << 16 | (int)(f6 / f8) << 8 | (int)(f7 / f8);
         }
      }

      return ints;
   }

   public Identifier method6017() {
      return this.intValue >= 0 && this.flags[this.intValue] ? identifiers2[this.intValue] : null;
   }

   public Identifier method6018() {
      return this.intValue2 >= 0 && this.flags[this.intValue2] ? identifiers2[this.intValue2] : null;
   }

   public long getLongValue4() {
      return this.longValue4;
   }

   public ZenithColor[] getZenithColors2() {
      return this.zenithColors2;
   }

   public ZenithColor color(int n) {
      float hover = Math.min(1.0F, (float)(System.currentTimeMillis() - this.longValue4) / 700.0F);
      ZenithColor zenithColor = this.zenithColors3[Math.min(n, this.zenithColors3.length - 1)];
      ZenithColor zenithColor2 = this.zenithColors2[Math.min(n, this.zenithColors2.length - 1)];
      return zenithColor.method7295(zenithColor2, hover);
   }

   public void playPause() {
      this.method6020(IMediaSession::playPause);
   }

   public void next() {
      this.method6020(IMediaSession::next);
   }

   public void previous() {
      this.method6020(IMediaSession::previous);
   }

   public void method6019(double d) {
      IMediaSession iMediaSession = this.iMediaSession;
      if (iMediaSession != null && !(this.method6008() <= 0.0)) {
         this.doubleValue = this.method6010(d);
         this.longValue5 = System.currentTimeMillis();
         this.flag3 = true;
         this.doubleValue5 = this.doubleValue;
         this.longValue11 = this.longValue5;
         this.flag4 = this.method6007();
         MediaHelperProcess.getInstance().method6860(iMediaSession.getOwner(), this.doubleValue);
      }
   }

   private void method6020(Consumer<IMediaSession> consumer) {
      IMediaSession iMediaSession = this.iMediaSession;
      if (iMediaSession != null) {
         this.executorService.execute(() -> {
            try {
               consumer.accept(iMediaSession);
            } catch (Throwable e) {
            }

            Minecraft.getInstance().execute(this::method6000);
         });
      }
   }

   private static NativeImage method6021(byte[] artworkPng) {
      try {
         ByteBuffer bytes = BufferUtils.createByteBuffer(artworkPng.length).put(artworkPng);
         bytes.flip();
         return NativeImage.read(bytes);
      } catch (Exception e) {
         return null;
      }
   }

   private static void method6022(Identifier identifier, NativeImage image) {
      if (image != null) {
         try {
            Minecraft.getInstance().getTextureManager().register(identifier, new DynamicTexture(identifier::toString, image));
         } catch (Exception e) {
         }
      }
   }

   private static BufferedImage method6023(byte[] artworkPng) {
      try {
         return ImageIO.read(new ByteArrayInputStream(artworkPng));
      } catch (Throwable e) {
         return null;
      }
   }

   private static int[] method6024(BufferedImage bufferedImage) {
      byte b = 64;
      float[][] floatses = new float[3][b * b];
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();

      for (int i = 0; i < b; i++) {
         for (int j = 0; j < b; j++) {
            int n = j * width / b;
            int n2 = Math.max(n + 1, (j + 1) * width / b);
            int n3 = i * height / b;
            int n4 = Math.max(n3 + 1, (i + 1) * height / b);
            int n5 = Math.max(1, (n2 - n) / 4);
            int n6 = Math.max(1, (n4 - n3) / 4);
            float f = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            int n7 = 0;

            for (int k = n3; k < n4; k += n6) {
               for (int m = n; m < n2; m += n5) {
                  int rgb = bufferedImage.getRGB(Math.min(width - 1, m), Math.min(height - 1, k));
                  f += rgb >> 16 & 0xFF;
                  f2 += rgb >> 8 & 0xFF;
                  f3 += rgb & 0xFF;
                  n7++;
               }
            }

            int n8 = i * b + j;
            floatses[0][n8] = f / n7;
            floatses[1][n8] = f2 / n7;
            floatses[2][n8] = f3 / n7;
         }
      }

      for (int i = 0; i < 3; i++) {
         for (float[] floats : floatses) {
            method6025(floats, b, 5);
         }
      }

      int[] ints = new int[b * b];

      for (int i = 0; i < ints.length; i++) {
         float f = floatses[0][i];
         float f2 = floatses[1][i];
         float f3 = floatses[2][i];
         float[] floats = Color.RGBtoHSB((int)f, (int)f2, (int)f3, null);
         int n = Color.HSBtoRGB(floats[0], Math.min(1.0F, floats[1] * 1.25F), Math.min(1.0F, floats[2] * 0.95F));
         float f4 = (i % b + 0.5F) / b - 0.5F;
         float f5 = (i / b + 0.5F) / b - 0.5F;
         float f6 = (float)Math.sqrt(f4 * f4 + f5 * f5);
         float f7 = Math.max(0.0F, Math.min(1.0F, (0.5F - f6) / 0.2F));
         f7 = f7 * f7 * (3.0F - 2.0F * f7);
         ints[i] = (int)(f7 * 255.0F) << 24 | n & 16777215;
      }

      return ints;
   }

   private static void method6025(float[] floats, int n, int n2) {
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            float f = 0.0F;

            for (int k = -n2; k <= n2; k++) {
               f += floats[i * n + Math.max(0, Math.min(n - 1, j + k))];
            }

            floats2[i * n + j] = f / (n2 * 2 + 1);
         }
      }

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            float f = 0.0F;

            for (int k = -n2; k <= n2; k++) {
               f += floats2[Math.max(0, Math.min(n - 1, i + k)) * n + j];
            }

            floats[i * n + j] = f / (n2 * 2 + 1);
         }
      }
   }

   private static ZenithColor[] method6026(BufferedImage bufferedImage) {
      try {
         byte b = 32;
         float[][] floatses = new float[12][5];
         float[] floats = new float[4];

         for (int i = 0; i < b; i++) {
            for (int j = 0; j < b; j++) {
               int rgb = bufferedImage.getRGB(j * bufferedImage.getWidth() / b, i * bufferedImage.getHeight() / b);
               int n = rgb >> 16 & 0xFF;
               int n2 = rgb >> 8 & 0xFF;
               int n3 = rgb & 0xFF;
               float[] floats2 = Color.RGBtoHSB(n, n2, n3, null);
               floats[0] += n;
               floats[1] += n2;
               floats[2] += n3;
               floats[3]++;
               float f = floats2[1] * floats2[1] * (0.25F + floats2[2]);
               if (!(floats2[2] < 0.12F)) {
                  int n4 = Math.min(11, (int)(floats2[0] * 12.0F));
                  floatses[n4][0] = floatses[n4][0] + n * f;
                  floatses[n4][1] = floatses[n4][1] + n2 * f;
                  floatses[n4][2] = floatses[n4][2] + n3 * f;
                  floatses[n4][3] = floatses[n4][3] + f;
                  floatses[n4][4]++;
               }
            }
         }

         ArrayList<float[]> floatses2 = new ArrayList<>();

         for (float[] floats2 : floatses) {
            if (floats2[3] > 1.0E-4F) {
               floatses2.add(floats2);
            }
         }

         floatses2.sort((var0x, var1x) -> Float.compare(var1x[3], var0x[3]));
         ZenithColor zenithColor = new ZenithColor((int)(floats[0] / floats[3]), (int)(floats[1] / floats[3]), (int)(floats[2] / floats[3]));
         ZenithColor[] zenithColors = new ZenithColor[3];

         for (int i = 0; i < 3; i++) {
            if (i < floatses2.size()) {
               float[] floats2 = (float[])floatses2.get(i);
               zenithColors[i] = method6027(new ZenithColor((int)(floats2[0] / floats2[3]), (int)(floats2[1] / floats2[3]), (int)(floats2[2] / floats2[3])));
            } else {
               zenithColors[i] = i == 0 ? method6027(zenithColor) : zenithColors[0].method7295(zenithColor, 0.5F * i);
            }
         }

         return zenithColors;
      } catch (Throwable e) {
         return null;
      }
   }

   private static ZenithColor method6027(ZenithColor zenithColor) {
      float[] floats = Color.RGBtoHSB(zenithColor.getIntValue(), zenithColor.getIntValue2(), zenithColor.getIntValue3(), null);
      float saturation = Math.min(1.0F, Math.max(0.35F, floats[1] * 1.15F));
      float brightness = Math.min(0.95F, Math.max(0.55F, floats[2]));
      return new ZenithColor(0xFF000000 | Color.HSBtoRGB(floats[0], saturation, brightness));
   }
}
