package org.zenith.utility.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.zenith.utility.network.CloudPackets;

public final class VoiceState {
   public static final int CONST_intValue = 1;
   public static final int CONST_intValue2 = 2;
   public static final int CONST_intValue3 = 4;
   public static final int CONST_intValue4 = 8;
   public static final int CONST_intValue5 = 16;
   public static final int CONST_intValue6 = 29;
   private final VoiceState.Handler handler;
   private final ConcurrentHashMap<Long, VoiceSpeakerStream> concurrentHashMap = new ConcurrentHashMap<>();
   private volatile VoiceState.Data data = VoiceState.Data.INSTANCE;
   private volatile Map<Long, Float> items = Map.of();
   private volatile Map<Long, VoiceState.Data2> items2 = Map.of();
   private volatile boolean flag = true;
   private volatile boolean flag2;
   private volatile int intValue;
   private volatile long longValue;
   private volatile boolean flag3;
   private volatile boolean flag4;
   private volatile boolean flag5;
   private volatile float floatValue = 1.0F;
   private volatile boolean flag6;
   private volatile float floatValue2 = -100.0F;
   private volatile String text = "";
   private volatile String text2 = "";
   private volatile String text3 = "";
   private volatile String text4 = "";
   private VoiceCaptureThread voiceCaptureThread;
   private VoicePlaybackThread voicePlaybackThread;
   private volatile boolean flag7;

   public VoiceState(VoiceState.Handler handler) {
      this.handler = handler;
   }

   public synchronized void method5359(VoiceState.Data data) {
      if (this.voiceCaptureThread == null) {
         this.data = data;
         this.text = "";
         this.text2 = "";
         this.voiceCaptureThread = new VoiceCaptureThread(this);
         this.voicePlaybackThread = new VoicePlaybackThread(this);
         this.flag7 = true;
         this.voicePlaybackThread.start();
         this.voiceCaptureThread.start();
      }
   }

   public synchronized void stop() {
      if (this.voiceCaptureThread != null) {
         this.flag7 = false;
         this.voiceCaptureThread.stop();
         this.voicePlaybackThread.stop();
         this.voiceCaptureThread = null;
         this.voicePlaybackThread = null;
         this.concurrentHashMap.clear();
         this.flag6 = false;
         this.flag5 = false;
         this.floatValue2 = -100.0F;
      }
   }

   public synchronized boolean method5360() {
      return this.voiceCaptureThread != null;
   }

   public void setData(VoiceState.Data data) {
      this.data = data;
   }

   public VoiceState.Data getData() {
      return this.data;
   }

   public void method5361(boolean flag, int n) {
      if ((this.flag2 || this.intValue != 0) && !flag && n == 0) {
         this.longValue = System.currentTimeMillis();
      }

      this.flag2 = flag;
      this.intValue = n & 29;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setFlag3(boolean flag) {
      this.flag3 = flag;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void method5362(boolean flag) {
      this.flag4 = flag;
      if (flag) {
         for (VoiceSpeakerStream voiceSpeakerStream : this.concurrentHashMap.values()) {
            voiceSpeakerStream.voiceFrameBuffer.reset();
         }
      }
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public void setItems(Map<Long, Float> items) {
      this.items = items;
   }

   public void setItems2(Map<Long, VoiceState.Data2> map) {
      this.items2 = map;
   }

   public void method5363(boolean flag) {
      this.flag5 = flag;
      if (!flag) {
         VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(-1L);
         if (voiceSpeakerStream != null) {
            voiceSpeakerStream.voiceFrameBuffer.reset();
         }
      }
   }

   public boolean isFlag5() {
      return this.flag5;
   }

   public void setFloatValue(float floatValue) {
      this.floatValue = floatValue;
   }

   public boolean isFlag6() {
      return this.flag6;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }

   public Set<Long> method5364() {
      long now = System.currentTimeMillis();
      HashSet<Long> longs = new HashSet<>();
      this.concurrentHashMap.forEach((var3x, voiceSpeakerStream) -> {
         if (voiceSpeakerStream.voiceFrameBuffer.active() || now - voiceSpeakerStream.longValue2 < 1000L) {
            longs.add(var3x);
         }
      });
      return longs;
   }

   public boolean method5365(long uid) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(uid);
      return voiceSpeakerStream != null && voiceSpeakerStream.method1551(System.currentTimeMillis());
   }

   public float method5366(long uid) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(uid);
      return voiceSpeakerStream != null && voiceSpeakerStream.method1551(System.currentTimeMillis()) ? voiceSpeakerStream.floatValue2 : -100.0F;
   }

   public boolean method5367(long uid) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(uid);
      return voiceSpeakerStream != null && (voiceSpeakerStream.intValue & 1) != 0;
   }

   public int method5368(long key) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(key);
      return voiceSpeakerStream == null ? 0 : voiceSpeakerStream.intValue;
   }

   public boolean method5369(long uid) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(uid);
      return voiceSpeakerStream != null && (voiceSpeakerStream.intValue & 29) != 0;
   }

   public int method5370(long key) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(key);
      return voiceSpeakerStream == null ? this.data.getIntValue2() : voiceSpeakerStream.intValue2;
   }

   public void method5371(CloudPackets.Data16 data16) {
      if (this.flag7 && !this.flag4) {
         if (this.items.containsKey(data16.uid())) {
            if (!data16.method1823() || this.flag) {
               this.method5372(
                  data16.uid(),
                  data16.getIntValue3(),
                  data16.getByteValues(),
                  data16.method1824(),
                  data16.getIntValue() & 29,
                  Math.max(8, Math.min(128, data16.getIntValue2()))
               );
            }
         }
      }
   }

   private void method5372(long uid, int intValue, byte[] byteValues, boolean flag, int n, int n2) {
      VoiceSpeakerStream voiceSpeakerStream = this.concurrentHashMap.get(uid);
      if (voiceSpeakerStream == null) {
         if (flag || this.concurrentHashMap.size() >= 16) {
            return;
         }

         voiceSpeakerStream = this.concurrentHashMap.computeIfAbsent(uid, VoiceSpeakerStream::new);
      }

      voiceSpeakerStream.method1549(intValue, byteValues, flag, n, n2, System.currentTimeMillis());
   }

   public Collection<VoiceSpeakerStream> method5373() {
      return this.concurrentHashMap.values();
   }

   public void method5374(long now, long l) {
      this.concurrentHashMap.values().removeIf(voiceSpeakerStream -> voiceSpeakerStream.intValue3 == 0 && !voiceSpeakerStream.voiceFrameBuffer.active() && now - voiceSpeakerStream.longValue2 > l);
   }

   public void method5375(long now) {
      for (VoiceSpeakerStream voiceSpeakerStream : this.concurrentHashMap.values()) {
         if (voiceSpeakerStream.voiceFrameBuffer.active()) {
            voiceSpeakerStream.voiceFrameBuffer.reset();
         }
      }
   }

   public VoiceState.Data2 method5376(VoiceSpeakerStream voiceSpeakerStream) {
      if (voiceSpeakerStream.longValue == -1L) {
         return new VoiceState.Data2(0.0F, 0.0F, 0.0F, this.floatValue);
      } else if (this.flag4) {
         return VoiceState.Data2.INSTANCE;
      } else {
         VoiceState.Data2 data2 = this.items2.get(voiceSpeakerStream.longValue);
         return data2 == null ? VoiceState.Data2.INSTANCE : data2;
      }
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean method5377() {
      return this.intValue != 0;
   }

   public boolean method5378(long now, long l) {
      if (this.flag3) {
         return false;
      } else if (this.flag5) {
         return true;
      } else if (!this.handler.method377()) {
         return false;
      } else {
         return !this.data.isFlag() ? true : this.flag2 || this.intValue != 0 || now - this.longValue < l;
      }
   }

   public boolean method5379() {
      return !this.flag3 && this.handler.method377();
   }

   public void method5380(int n, int n2, int n3, byte[] byteValues, int n4) {
      if (this.handler.method377()) {
         this.handler.method378(n, n2, n3, byteValues, n4);
      }
   }

   public void method5381(int intValue, byte[] byteValues, int newLength) {
      if (this.flag5) {
         byte[] bytes = Arrays.copyOf(byteValues, newLength);
         this.method5372(-1L, intValue, bytes, false, 1, 8);
      }
   }

   public void setFlag6(boolean flag) {
      this.flag6 = flag;
   }

   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setText2(String str) {
      this.text2 = str;
   }

   public void method5382(String alcGetString) {
      this.text3 = alcGetString == null ? "" : alcGetString;
   }

   public void method5383(String alcGetString) {
      this.text4 = alcGetString == null ? "" : alcGetString;
   }

   public static final class Data {
      private final boolean flag;
      private final float floatValue;
      private final float floatValue2;
      private final int intValue;
      private final int intValue2;
      private final boolean flag2;
      private final String text;
      private final String text2;
      private final boolean flag3;
      private final boolean flag4;
      private final int intValue3;

      public boolean flag() {
         return this.flag;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public int intValue3() {
         return this.intValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, floatValue, floatValue2, intValue, intValue2, flag2, text, text2, flag3, flag4, intValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "flag=" + this.flag + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "flag2=" + this.flag2 + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "intValue3=" + this.intValue3 + "]";
      }
      public static final VoiceState.Data INSTANCE = new VoiceState.Data(true, -45.0F, 1.0F, 32000, 48, false, "", "", true, true, 0);

      public Data(boolean flag, float floatValue, float f, int intValue, int n, boolean flag2, String text, String str, boolean enabled, boolean enabled2, int n2) {
         this.flag = flag;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.intValue = intValue;
         this.intValue2 = n;
         this.flag2 = flag2;
         this.text = text;
         this.text2 = str;
         this.flag3 = enabled;
         this.flag4 = enabled2;
         this.intValue3 = n2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   public static final class Data2 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }
      public static final VoiceState.Data2 INSTANCE = new VoiceState.Data2(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

      public Data2(float floatValue, float f, float f2, float floatValue2) {
         this(floatValue, f, f2, floatValue2, 0.0F);
      }

      public Data2(float floatValue, float f, float f2, float f3, float f4) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   public interface Handler {
      boolean method377();

      void method378(int n, int n2, int n3, byte[] byteValues, int n4);
   }
}
