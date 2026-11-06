package org.zenith.utility.misc;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.locks.LockSupport;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoicePlaybackThread implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 100;
   private static final int CONST_intValue3 = 6;
   private static final long CONST_longValue = 3000L;
   private static final long CONST_longValue2 = 3000L;
   private static final long CONST_longValue3 = 30000L;
   private final VoiceState voiceState;
   private final Thread thread;
   private volatile boolean running = true;
   private volatile boolean flag;
   private long longValue;
   private long longValue2;
   private ShortBuffer shortBuffer;
   private long longValue3;
   private String text;
   private boolean flag2;

   public VoicePlaybackThread(VoiceState voiceState) {
      this.voiceState = voiceState;
      this.thread = new Thread(this, "Zenith Voice Playback");
      this.thread.setDaemon(true);
      this.thread.setPriority(7);
   }

   public void start() {
      this.thread.start();
   }

   public void stop() {
      this.running = false;
      LockSupport.unpark(this.thread);

      try {
         this.thread.join(2000L);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   public void method1583() {
      this.flag = true;
   }

   @Override
   public void run() {
      try {
         while (this.running) {
            this.method1584();
         }
      } catch (Throwable e) {
         logger.error("Голос: поток вывода упал", e);
         this.voiceState.setText2("сбой вывода: " + e);
      } finally {
         this.close();
      }
   }

   private void method1584() {
      long now = System.currentTimeMillis();
      VoiceState.Data data = this.voiceState.getData();
      if (this.longValue2 != 0L && (this.flag || !data.getText2().equals(this.text) || data.isFlag2() != this.flag2)) {
         this.flag = false;
         this.close();
      }

      if (this.longValue2 != 0L || now >= this.longValue3 && this.method1588(data.getText2(), data.isFlag2())) {
         if (ALC10.alcGetInteger(this.longValue, 787) == 0) {
            logger.info("Голос: устройство вывода отключено");
            this.voiceState.setText2("устройство вывода отключено");
            this.close();
            this.longValue3 = now + 3000L;
         } else {
            for (VoiceSpeakerStream voiceSpeakerStream : this.voiceState.method5373()) {
               this.method1585(voiceSpeakerStream, now);
            }

            this.voiceState.method5374(now, 30000L);
            LockSupport.parkNanos(2000000L);
         }
      } else {
         this.voiceState.method5375(now);
         LockSupport.parkNanos(20000000L);
      }
   }

   private void method1585(VoiceSpeakerStream voiceSpeakerStream, long now) {
      if (voiceSpeakerStream.intValue3 != 0 || voiceSpeakerStream.voiceFrameBuffer.active() && this.method1586(voiceSpeakerStream)) {
         int source = voiceSpeakerStream.intValue3;
         int alGetSourcei = AL10.alGetSourcei(source, 4118);

         while (alGetSourcei-- > 0) {
            voiceSpeakerStream.arrayDeque.addLast(AL10.alSourceUnqueueBuffers(source));
         }

         int n = AL10.alGetSourcei(source, 4117);
         VoiceState.Data2 data2 = this.voiceState.method5376(voiceSpeakerStream);
         int n2 = 0;

         while (n < 2 && !voiceSpeakerStream.arrayDeque.isEmpty() && n2++ < 6) {
            VoiceFrameBuffer.Data data = voiceSpeakerStream.voiceFrameBuffer.method8028(now);
            if (data.getType2() == VoiceFrameBuffer.Type2.NONE) {
               break;
            }

            if (data.getType2() == VoiceFrameBuffer.Type2.END) {
               voiceSpeakerStream.method1553();
               break;
            }

            int length = voiceSpeakerStream.method1552(data);
            if (length > 0) {
               float f = VoiceAudioProcessor.method1531(voiceSpeakerStream.shortValues, length);
               voiceSpeakerStream.floatValue2 = f;
               if (data.getType2() != VoiceFrameBuffer.Type2.PLC && f > -55.0F) {
                  voiceSpeakerStream.longValue3 = now;
               }

               voiceSpeakerStream.method1550(length, data2.getFloatValue5());
               int pollFirst = voiceSpeakerStream.arrayDeque.pollFirst();
               this.shortBuffer.clear();
               this.shortBuffer.put(voiceSpeakerStream.shortValues, 0, length).flip();
               AL10.alBufferData(pollFirst, 4353, this.shortBuffer, 48000);
               AL10.alSourceQueueBuffers(source, pollFirst);
               n++;
            }
         }

         AL10.alSource3f(source, 4100, data2.getFloatValue(), data2.getFloatValue2(), data2.getFloatValue3());
         AL10.alSourcef(source, 4106, data2.getFloatValue4());
         int alGetSourcei2 = AL10.alGetSourcei(source, 4112);
         if (n > 0 && alGetSourcei2 != 4114) {
            AL10.alSourcePlay(source);
         }

         if (n == 0 && !voiceSpeakerStream.voiceFrameBuffer.active() && now - voiceSpeakerStream.longValue2 > 3000L) {
            this.method1587(voiceSpeakerStream);
         }
      }
   }

   private boolean method1586(VoiceSpeakerStream voiceSpeakerStream) {
      AL10.alGetError();
      int alGenSources = AL10.alGenSources();
      if (AL10.alGetError() == 0 && alGenSources != 0) {
         int[] intValues = new int[6];
         AL10.alGenBuffers(intValues);
         if (AL10.alGetError() != 0) {
            AL10.alDeleteSources(alGenSources);
            return false;
         } else {
            AL10.alSourcei(alGenSources, 514, 1);
            AL10.alSourcef(alGenSources, 4129, 0.0F);
            AL10.alSourcef(alGenSources, 4110, 4.0F);
            AL10.alSourcef(alGenSources, 4106, 0.0F);
            voiceSpeakerStream.intValue3 = alGenSources;
            voiceSpeakerStream.intValues = intValues;
            voiceSpeakerStream.arrayDeque.clear();

            for (int intValue : intValues) {
               voiceSpeakerStream.arrayDeque.addLast(intValue);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void method1587(VoiceSpeakerStream voiceSpeakerStream) {
      if (voiceSpeakerStream.intValue3 != 0) {
         AL10.alSourceStop(voiceSpeakerStream.intValue3);
         AL10.alSourcei(voiceSpeakerStream.intValue3, 4105, 0);
         AL10.alDeleteSources(voiceSpeakerStream.intValue3);
         AL10.alDeleteBuffers(voiceSpeakerStream.intValues);
         voiceSpeakerStream.intValue3 = 0;
         voiceSpeakerStream.intValues = new int[0];
         voiceSpeakerStream.arrayDeque.clear();
      }
   }

   private boolean method1588(String text, boolean flag) {
      long alcOpenDevice = ALC10.alcOpenDevice(text.isEmpty() ? null : text);
      if (alcOpenDevice == 0L && !text.isEmpty()) {
         logger.info("Голос: вывод «{}» недоступен, беру системный", text);
         alcOpenDevice = ALC10.alcOpenDevice((CharSequence)null);
      }

      if (alcOpenDevice == 0L) {
         this.voiceState.setText2("устройство вывода не открывается");
         this.longValue3 = System.currentTimeMillis() + 3000L;
         return false;
      } else {
         ALCCapabilities capabilities = ALC.createCapabilities(alcOpenDevice);
         if (!capabilities.ALC_EXT_thread_local_context) {
            ALC10.alcCloseDevice(alcOpenDevice);
            this.voiceState.setText2("OpenAL без thread-local контекста");
            this.longValue3 = Long.MAX_VALUE;
            return false;
         } else {
            MemoryStack stackPush = MemoryStack.stackPush();

            long alcCreateContext;
            try {
               IntBuffer mallocInt = stackPush.mallocInt(7);
               mallocInt.put(4103).put(48000);
               mallocInt.put(4104).put(100);
               if (capabilities.ALC_SOFT_HRTF) {
                  mallocInt.put(6546).put(flag ? 1 : 0);
               }

               mallocInt.put(0).flip();
               alcCreateContext = ALC10.alcCreateContext(alcOpenDevice, mallocInt);
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

            if (alcCreateContext != 0L && EXTThreadLocalContext.alcSetThreadContext(alcCreateContext)) {
               AL.setCurrentThread(AL.createCapabilities(capabilities));
               AL10.alDistanceModel(0);
               AL10.alListenerf(4106, 1.0F);
               this.longValue = alcOpenDevice;
               this.longValue2 = alcCreateContext;
               this.text = text;
               this.flag2 = flag;
               this.shortBuffer = MemoryUtil.memAllocShort(960);
               this.voiceState.setText2("");
               String alcGetString = capabilities.ALC_ENUMERATE_ALL_EXT ? ALC10.alcGetString(this.longValue, 4115) : ALC10.alcGetString(this.longValue, 4101);
               this.voiceState.method5383(alcGetString);
               logger.info("Голос: вывод открыт — {}{}", alcGetString, capabilities.ALC_SOFT_HRTF && flag ? " (HRTF)" : "");
               return true;
            } else {
               if (alcCreateContext != 0L) {
                  ALC10.alcDestroyContext(alcCreateContext);
               }

               ALC10.alcCloseDevice(alcOpenDevice);
               this.voiceState.setText2("контекст OpenAL не создан");
               this.longValue3 = System.currentTimeMillis() + 3000L;
               return false;
            }
         }
      }
   }

   private void close() {
      if (this.longValue2 != 0L) {
         for (VoiceSpeakerStream voiceSpeakerStream : this.voiceState.method5373()) {
            this.method1587(voiceSpeakerStream);
         }

         EXTThreadLocalContext.alcSetThreadContext(0L);
         AL.setCurrentThread(null);
         ALC10.alcDestroyContext(this.longValue2);
         this.longValue2 = 0L;
      }

      if (this.longValue != 0L) {
         ALC10.alcCloseDevice(this.longValue);
         this.longValue = 0L;
      }

      this.voiceState.method5383("");
      if (this.shortBuffer != null) {
         MemoryUtil.memFree(this.shortBuffer);
         this.shortBuffer = null;
      }

      this.text = null;
   }
}
