package org.zenith.utility.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import javax.sound.sampled.AudioFormat.Encoding;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class ScriptSoundLoader {
   public static final long CONST_longValue = 8388608L;
   public static final int CONST_intValue = 10584000;
   public static final int CONST_intValue2 = 24;
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   public static final Map<Integer, ScriptSoundLoader.Entry> items = new ConcurrentHashMap<>();

   private ScriptSoundLoader() {
   }

   public static ScriptSoundLoader.Data method735(Path path) throws IOException {
      long size = Files.size(path);
      if (size > 8388608L) {
         throw new IOException("звук больше 8 МБ");
      } else {
         String lowerCase = path.getFileName().toString().toLowerCase(Locale.ROOT);
         if (lowerCase.endsWith(".ogg")) {
            return method736(Files.readAllBytes(path));
         } else if (lowerCase.endsWith(".wav")) {
            return method737(path);
         } else {
            throw new IOException("поддерживаются .ogg и .wav");
         }
      }
   }

   private static ScriptSoundLoader.Data method736(byte[] allBytes) throws IOException {
      ByteBuffer memAlloc = MemoryUtil.memAlloc(allBytes.length);

      ScriptSoundLoader.Data data;
      try {
         MemoryStack stackPush = MemoryStack.stackPush();

         try {
            memAlloc.put(allBytes).flip();
            IntBuffer mallocInt = stackPush.mallocInt(1);
            IntBuffer intBuffer = stackPush.mallocInt(1);
            IntBuffer error = stackPush.mallocInt(1);
            long stb_vorbis_open_memory = STBVorbis.stb_vorbis_open_memory(memAlloc, error, null);
            if (stb_vorbis_open_memory == 0L) {
               throw new IOException("не удалось разобрать ogg");
            }

            try {
               STBVorbisInfo malloc = STBVorbisInfo.malloc(stackPush);
               STBVorbis.stb_vorbis_get_info(stb_vorbis_open_memory, malloc);
               int channels = Math.max(1, Math.min(2, malloc.channels()));
               int sampleRate = Math.max(8000, Math.min(192000, malloc.sample_rate()));
               int n = 10584000 - 10584000 % channels;
               int stb_vorbis_stream_length_in_samples = STBVorbis.stb_vorbis_stream_length_in_samples(stb_vorbis_open_memory);
               short[] original = new short[stb_vorbis_stream_length_in_samples > 0 ? (int)Math.min((long)stb_vorbis_stream_length_in_samples * channels, (long)n) : n];
               ShortBuffer memAllocShort = MemoryUtil.memAllocShort(4096 * channels);

               try {
                  int offset = 0;

                  while (offset < n) {
                     memAllocShort.clear();
                     int stb_vorbis_get_samples_short_interleaved = STBVorbis.stb_vorbis_get_samples_short_interleaved(stb_vorbis_open_memory, channels, memAllocShort);
                     if (stb_vorbis_get_samples_short_interleaved <= 0) {
                        break;
                     }

                     int length = Math.min(stb_vorbis_get_samples_short_interleaved * channels, n - offset);
                     if (offset + length > original.length) {
                        original = Arrays.copyOf(original, (int)Math.min((long)n, Math.max(original.length * 2L, (long)(offset + length))));
                     }

                     memAllocShort.get(original, offset, length);
                     offset += length;
                  }

                  if (offset == 0) {
                     throw new IOException("пустой ogg");
                  }

                  data = new ScriptSoundLoader.Data(offset == original.length ? original : Arrays.copyOf(original, offset), channels, sampleRate);
               } finally {
                  MemoryUtil.memFree(memAllocShort);
               }
            } finally {
               STBVorbis.stb_vorbis_close(stb_vorbis_open_memory);
            }
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
      } finally {
         MemoryUtil.memFree(memAlloc);
      }

      return data;
   }

   private static ScriptSoundLoader.Data method737(Path path) throws IOException {
      try {
         ScriptSoundLoader.Data data;
         try (
            BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(path));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
         ) {
            AudioFormat format = audioInputStream.getFormat();
            int channels = Math.max(1, Math.min(2, format.getChannels()));
            AudioFormat audioFormat = new AudioFormat(Encoding.PCM_SIGNED, format.getSampleRate(), 16, channels, channels * 2, format.getSampleRate(), false);

            try (AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(audioFormat, audioInputStream)) {
               byte[] nBytes = audioInputStream2.readNBytes(21168000);
               short[] shortValues = new short[nBytes.length / 2];

               for (int i = 0; i < shortValues.length; i++) {
                  shortValues[i] = (short)(nBytes[i * 2] & 255 | nBytes[i * 2 + 1] << 8);
               }

               data = new ScriptSoundLoader.Data(shortValues, channels, (int)format.getSampleRate());
            }
         }

         return data;
      } catch (UnsupportedAudioFileException e) {
         throw new IOException("неподдерживаемый wav");
      }
   }

   public static ScriptSoundLoader.Entry method738(ScriptSoundLoader.Data data, float floatValue, float f, boolean flag) {
      if (items.size() >= 24) {
         throw new IllegalStateException("одновременно играет больше 24 звуков");
      } else {
         ScriptSoundLoader.Entry entry = new ScriptSoundLoader.Entry(atomicInteger.incrementAndGet(), data, floatValue, f);
         entry.flag = flag;
         items.put(entry.intValue, entry);
         Thread.ofPlatform().daemon(true).name("zenith-script-sound").start(() -> method742(entry));
         return entry;
      }
   }

   public static void method739() {
      for (ScriptSoundLoader.Entry entry : items.values()) {
         entry.stopped = true;
      }
   }

   private static float method740() {
      try {
         return Minecraft.getInstance().options.getFinalSoundSourceVolume(SoundSource.MASTER);
      } catch (RuntimeException e) {
         return 1.0F;
      }
   }

   private static float[] method741(ScriptSoundLoader.Entry entry) {
      float f = entry.floatValue * method740();
      if (Double.isNaN(entry.doubleValue)) {
         return new float[]{f, f};
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player == null) {
            return new float[]{0.0F, 0.0F};
         } else {
            double x = entry.doubleValue - minecraft.player.getX();
            double d = entry.doubleValue2 - minecraft.player.getEyeY();
            double y = entry.doubleValue3 - minecraft.player.getZ();
            double d2 = Math.sqrt(x * x + d * d + y * y);
            float f2 = (float)Math.max(0.0, 1.0 - d2 / Math.max(1.0, entry.doubleValue4));
            double angdeg = Math.toDegrees(Math.atan2(y, x)) - 90.0 - minecraft.player.getYRot();
            double d3 = d2 < 0.5 ? 0.0 : -Math.sin(Math.toRadians(angdeg));
            float f3 = (float)Math.min(1.0, 1.0 - d3) * 0.5F + 0.5F * (float)(1.0 - Math.abs(d3));
            float f4 = (float)Math.min(1.0, 1.0 + d3) * 0.5F + 0.5F * (float)(1.0 - Math.abs(d3));
            return new float[]{f * f2 * f3, f * f2 * f4};
         }
      }
   }

   private static void method742(ScriptSoundLoader.Entry entry) {
      ScriptSoundLoader.Data data = entry.data;
      int sampleRate = Math.max(4000, Math.min(192000, Math.round(data.getIntValue2() * Math.max(0.25F, Math.min(4.0F, entry.floatValue2)))));
      AudioFormat audioFormat = new AudioFormat(sampleRate, 16, 2, true, false);

      try (SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat)) {
         sourceDataLine.open(audioFormat, 8192);
         sourceDataLine.start();
         int n = data.getShortValues().length / data.getIntValue();
         short s = 1024;
         byte[] bytes = new byte[s * 4];

         for (int i = 0; !entry.stopped; entry.doubleValue5 = (double)i / n) {
            if (i >= n) {
               if (!entry.flag) {
                  break;
               }

               i = 0;
            }

            float[] floats = method741(entry);
            int n2 = Math.min(s, n - i);

            for (int j = 0; j < n2; j++) {
               int n3 = (i + j) * data.getIntValue();
               short shortValue = data.getShortValues()[n3];
               short shortValue2 = data.getIntValue() > 1 ? data.getShortValues()[n3 + 1] : shortValue;
               int n4 = Math.max(32768, Math.min(32767, Math.round(shortValue * floats[0])));
               int n5 = Math.max(32768, Math.min(32767, Math.round(shortValue2 * floats[1])));
               bytes[j * 4] = (byte)n4;
               bytes[j * 4 + 1] = (byte)(n4 >> 8);
               bytes[j * 4 + 2] = (byte)n5;
               bytes[j * 4 + 3] = (byte)(n5 >> 8);
            }

            sourceDataLine.write(bytes, 0, n2 * 4);
            i += n2;
         }

         if (!entry.stopped) {
            sourceDataLine.drain();
         }

         sourceDataLine.stop();
      } catch (Exception e) {
      } finally {
         items.remove(entry.intValue);
      }
   }

   public static final class Data {
      private final short[] shortValues;
      private final int intValue;
      private final int intValue2;

      public short[] shortValues() {
         return this.shortValues;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.shortValues, other.shortValues) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shortValues, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "shortValues=" + this.shortValues + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(short[] shortValues, int intValue, int sampleRate) {
         this.shortValues = shortValues;
         this.intValue = intValue;
         this.intValue2 = sampleRate;
      }

      public double method743() {
         return (double)this.shortValues.length / this.intValue / this.intValue2;
      }

      public short[] getShortValues() {
         return this.shortValues;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Entry {
      public final int intValue;
      public final ScriptSoundLoader.Data data;
      public volatile float floatValue;
      public volatile float floatValue2;
      public volatile boolean flag;
      public volatile boolean stopped;
      public volatile double doubleValue = Double.NaN;
      public volatile double doubleValue2;
      public volatile double doubleValue3;
      public volatile double doubleValue4 = 32.0;
      public volatile double doubleValue5;

      public Entry(int intValue, ScriptSoundLoader.Data data, float floatValue, float f) {
         this.intValue = intValue;
         this.data = data;
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }
   }
}
