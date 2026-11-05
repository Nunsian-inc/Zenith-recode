package org.zenith.utility.misc;

import io.github.jaredmdobson.concentus.OpusDecoder;
import io.github.jaredmdobson.concentus.OpusException;
import java.util.ArrayDeque;

public final class VoiceSpeakerStream {
   public final long longValue;
   public final VoiceFrameBuffer voiceFrameBuffer = new VoiceFrameBuffer();
   public volatile int intValue;
   public float floatValue;
   public volatile int intValue2 = 48;
   public volatile long longValue2;
   public volatile long longValue3;
   public volatile float floatValue2 = -100.0F;
   private OpusDecoder opusDecoder;
   public final short[] shortValues = new short[960];
   public int intValue3;
   public final ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
   public int[] intValues = new int[0];
   public boolean flag;

   public VoiceSpeakerStream(long longValue) {
      this.longValue = longValue;
   }

   public void method1549(int intValue, byte[] byteValues, boolean flag, int n, int n2, long now) {
      this.intValue = n;
      this.intValue2 = n2;
      this.longValue2 = now;
      this.voiceFrameBuffer.method8027(intValue, byteValues, flag, now);
   }

   public void method1550(int length, float f) {
      if (f <= 0.01F) {
         if (length > 0) {
            this.floatValue = this.shortValues[length - 1];
         }
      } else {
         float f2 = 1.0F - f * 0.9F;
         float f3 = 1.0F - f * 0.35F;
         float floatValue = this.floatValue;

         for (int i = 0; i < length; i++) {
            floatValue += f2 * (this.shortValues[i] - floatValue);
            this.shortValues[i] = (short)Math.max(32768, Math.min(32767, Math.round(floatValue * f3)));
         }

         this.floatValue = floatValue;
      }
   }

   public boolean method1551(long now) {
      return now - this.longValue3 < 250L;
   }

   public int method1552(VoiceFrameBuffer.Data data) {
      try {
         OpusDecoder opusDecoder = this.method1554();

         return switch (data.getType2()) {
            case NORMAL -> opusDecoder.decode(data.getByteValues(), 0, data.getByteValues().length, this.shortValues, 0, 960, false);
            case FEC -> opusDecoder.decode(data.getByteValues(), 0, data.getByteValues().length, this.shortValues, 0, 960, true);
            case PLC -> opusDecoder.decode(null, 0, 0, this.shortValues, 0, 960, false);
            default -> 0;
         };
      } catch (RuntimeException | OpusException e) {
         this.method1553();
         return 0;
      }
   }

   public void method1553() {
      if (this.opusDecoder != null) {
         this.opusDecoder.resetState();
      }
   }

   private OpusDecoder method1554() throws OpusException {
      if (this.opusDecoder == null) {
         this.opusDecoder = new OpusDecoder(48000, 1);
      }

      return this.opusDecoder;
   }
}
