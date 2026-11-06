package org.zenith.utility.misc;

import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import io.github.jaredmdobson.concentus.OpusSignal;
import java.util.concurrent.locks.LockSupport;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoiceCaptureThread implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static final int CONST_intValue = 24000;
   private static final long CONST_longValue = 3000L;
   private static final long CONST_longValue2 = 300L;
   private static final int CONST_intValue2 = 3;
   private static final int CONST_intValue3 = 20;
   private static final int CONST_intValue4 = 2;
   private final VoiceState voiceState;
   private volatile boolean running = true;
   private final Thread thread;
   private long longValue;
   private String text;
   private boolean flag;
   private long longValue2;
   private final VoiceAudioProcessor class9 = new VoiceAudioProcessor();
   private final VoiceDenoiser voiceDenoiser = new VoiceDenoiser();
   private OpusEncoder opusEncoder;
   private int intValue;
   private final short[] shortValues = new short[960];
   private final byte[] byteValues = new byte[1275];
   private final short[][] shortValues2 = new short[2][960];
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private boolean flag2;
   private int intValue5;
   private int intValue6;
   private int intValue7;

   public VoiceCaptureThread(VoiceState voiceState) {
      this.voiceState = voiceState;
      this.thread = new Thread(this, "Zenith Voice Capture");
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

   @Override
   public void run() {
      try {
         while (this.running) {
            this.method267();
         }
      } catch (Throwable e) {
         logger.error("Голос: поток микрофона упал", e);
         this.voiceState.setText("сбой захвата: " + e);
      } finally {
         this.method272();
         this.method277();
         this.voiceDenoiser.close();
      }
   }

   private void method267() {
      VoiceState.Data data = this.voiceState.getData();
      long now = System.currentTimeMillis();
      if (!this.voiceState.method5378(now, 300L)) {
         this.method272();
         if (this.flag) {
            ALC11.alcCaptureStop(this.longValue);
            this.flag = false;
         }

         this.voiceState.setFloatValue2(-100.0F);
         LockSupport.parkNanos(10000000L);
      } else {
         String text = data.getText();
         if (this.longValue != 0L && !text.equals(this.text)) {
            this.method277();
         }

         if (this.longValue != 0L || now >= this.longValue2 && this.method275(text)) {
            if (!this.flag) {
               ALC11.alcCaptureStart(this.longValue);
               this.flag = true;
               this.class9.reset();
               this.method276();
            }

            if (ALC10.alcGetInteger(this.longValue, 787) == 0) {
               logger.info("Голос: микрофон отключён");
               this.voiceState.setText("микрофон отключён");
               this.method277();
               this.longValue2 = now + 3000L;
            } else {
               int alcGetInteger = ALC10.alcGetInteger(this.longValue, 786);
               if (alcGetInteger < 960) {
                  LockSupport.parkNanos(2000000L);
               } else {
                  ALC11.alcCaptureSamples(this.longValue, this.shortValues, 960);
                  this.method268(this.shortValues, data);
               }
            }
         } else {
            LockSupport.parkNanos(20000000L);
         }
      }
   }

   private void method268(short[] shortValues, VoiceState.Data data) {
      boolean flag = data.isFlag3() && data.isFlag4() && this.voiceDenoiser.method802(shortValues, shortValues.length);
      float f = this.class9.method1530(shortValues, data.getFloatValue2(), data.isFlag3() && !flag);
      this.voiceState.setFloatValue2(f);
      this.method274(data.getIntValue());
      if (this.opusEncoder != null) {
         if (this.voiceState.isFlag5()) {
            this.method272();
            int newLength = this.method273(shortValues);
            if (newLength > 0) {
               this.voiceState.method5381(this.intValue4, this.byteValues, newLength);
               this.intValue4 = this.intValue4 + 1 & 65535;
            }

            this.voiceState.setFlag6(false);
         } else {
            boolean flag2 = this.voiceState.isFlag2();
            int intValue = this.voiceState.getIntValue();
            boolean flag3 = intValue != 0;
            boolean flag4;
            if (data.isFlag()) {
               if (flag2 || flag3) {
                  this.intValue6 = 3;
               }

               flag4 = this.intValue6 > 0;
               if (this.intValue6 > 0 && !flag2 && !flag3) {
                  this.intValue6--;
               }
            } else {
               if (flag3 || f > data.getFloatValue()) {
                  this.intValue7 = 20;
               }

               flag4 = this.intValue7 > 0;
               if (this.intValue7 > 0) {
                  this.intValue7--;
               }
            }

            int n = data.getIntValue3() & 29;
            int n2 = flag3 ? intValue : (this.flag2 && this.intValue5 != n && !flag2 ? this.intValue5 : n);
            if (!this.voiceState.method5379()) {
               flag4 = false;
            }

            if (flag4 && this.flag2 && n2 != this.intValue5) {
               this.method272();
            }

            if (flag4 && !this.flag2) {
               this.flag2 = true;
               this.intValue5 = n2;
               this.opusEncoder.resetState();
               if (!data.isFlag()) {
                  this.method269(data);
               }
            }

            if (flag4) {
               this.method271(shortValues, data, 0);
            } else {
               this.method272();
            }

            this.method270(shortValues);
            this.voiceState.setFlag6(this.flag2);
         }
      }
   }

   private void method269(VoiceState.Data data) {
      for (int i = 0; i < this.intValue2; i++) {
         int n = (this.intValue3 - this.intValue2 + i + 2) % 2;
         this.method271(this.shortValues2[n], data, 0);
      }

      this.intValue2 = 0;
   }

   private void method270(short[] shortValues) {
      System.arraycopy(shortValues, 0, this.shortValues2[this.intValue3], 0, shortValues.length);
      this.intValue3 = (this.intValue3 + 1) % 2;
      this.intValue2 = Math.min(2, this.intValue2 + 1);
   }

   private void method271(short[] shortValues, VoiceState.Data data, int n) {
      int n2 = this.method273(shortValues);
      if (n2 > 0) {
         int n3 = this.intValue5 | n;
         this.voiceState.method5380(n3, data.getIntValue2(), this.intValue4, this.byteValues, n2);
         this.intValue4 = this.intValue4 + 1 & 65535;
      }
   }

   private void method272() {
      if (this.flag2) {
         this.flag2 = false;
         this.intValue6 = 0;
         this.intValue7 = 0;
         int n = 2 | this.intValue5;
         this.voiceState.method5380(n, this.voiceState.getData().getIntValue2(), this.intValue4, this.byteValues, 0);
         this.intValue4 = this.intValue4 + 1 & 65535;
         this.voiceState.setFlag6(false);
      }
   }

   private int method273(short[] shortValues) {
      try {
         return this.opusEncoder.encode(shortValues, 0, 960, this.byteValues, 0, this.byteValues.length);
      } catch (RuntimeException | OpusException e) {
         logger.debug("Голос: кадр не закодирован: {}", e.toString());
         this.opusEncoder.resetState();
         return 0;
      }
   }

   private void method274(int intValue) {
      try {
         if (this.opusEncoder == null) {
            this.opusEncoder = new OpusEncoder(48000, 1, OpusApplication.OPUS_APPLICATION_VOIP);
            this.opusEncoder.setSignalType(OpusSignal.OPUS_SIGNAL_VOICE);
            this.opusEncoder.setComplexity(8);
            this.opusEncoder.setUseVBR(true);
            this.opusEncoder.setUseInbandFEC(true);
            this.opusEncoder.setPacketLossPercent(3);
            this.opusEncoder.setUseDTX(false);
            this.intValue = 0;
         }

         if (this.intValue != intValue) {
            this.opusEncoder.setBitrate(intValue);
            this.intValue = intValue;
         }
      } catch (OpusException e) {
         logger.error("Голос: кодер Opus не создан: {}", e.toString());
         this.voiceState.setText("кодек недоступен");
         this.opusEncoder = null;
      }
   }

   private boolean method275(String text) {
      long alcCaptureOpenDevice = ALC11.alcCaptureOpenDevice(text.isEmpty() ? null : text, 48000, 4353, 24000);
      if (alcCaptureOpenDevice == 0L && !text.isEmpty()) {
         logger.info("Голос: микрофон «{}» недоступен, беру системный", text);
         alcCaptureOpenDevice = ALC11.alcCaptureOpenDevice((CharSequence)null, 48000, 4353, 24000);
      }

      if (alcCaptureOpenDevice == 0L) {
         this.voiceState.setText("микрофон не открывается");
         this.longValue2 = System.currentTimeMillis() + 3000L;
         return false;
      } else {
         this.longValue = alcCaptureOpenDevice;
         this.text = text;
         this.flag = false;
         this.voiceState.setText("");
         String alcGetString = ALC10.alcGetString(this.longValue, 784);
         this.voiceState.method5382(alcGetString);
         logger.info("Голос: микрофон открыт — {}", alcGetString);
         return true;
      }
   }

   private void method276() {
      for (int i = ALC10.alcGetInteger(this.longValue, 786); i >= 960; i -= 960) {
         ALC11.alcCaptureSamples(this.longValue, this.shortValues, 960);
      }
   }

   private void method277() {
      if (this.longValue != 0L) {
         if (this.flag) {
            ALC11.alcCaptureStop(this.longValue);
            this.flag = false;
         }

         ALC11.alcCaptureCloseDevice(this.longValue);
         this.longValue = 0L;
         this.text = null;
         this.voiceState.method5382("");
      }
   }
}
