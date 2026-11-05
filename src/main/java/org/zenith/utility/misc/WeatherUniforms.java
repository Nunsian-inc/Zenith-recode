package org.zenith.utility.misc;

import org.zenith.utility.render.PostEffectManager;

public final class WeatherUniforms {
   private WeatherUniforms() {
   }

   public static void method799(
      WeatherUniforms.Handler data,
      WeatherFlakeChannel weatherFlakeChannel,
      float floatValue,
      float f,
      float floatValue2,
      double x,
      double y,
      double z,
      float floatValue3,
      float floatValue4,
      float floatValue5,
      float floatValue6
   ) {
      data.method259("Intensity", floatValue);
      data.method259("time", floatValue2);
      data.method260("CameraPosition", (float)x, (float)y, (float)z);
      data.method259("SkyPitch", floatValue3);
      data.method259("SkyYaw", floatValue4);
      data.method259("TanHalfFov", floatValue5);
      data.method259("Aspect", floatValue6);
      method800(data, "SkyZenith", weatherFlakeChannel.floatValues);
      method800(data, "SkyHorizon", weatherFlakeChannel.floatValues2);
      method800(data, "SkyNadir", weatherFlakeChannel.floatValues3);
      method800(data, "SunDir", weatherFlakeChannel.floatValues4);
      method800(data, "SunColor", weatherFlakeChannel.floatValues5);
      data.method259("SunSize", weatherFlakeChannel.floatValue2);
      data.method259("SunGlare", weatherFlakeChannel.floatValue3);
      data.method259("CloudCover", weatherFlakeChannel.floatValue4);
      data.method259("CloudDark", weatherFlakeChannel.floatValue5);
      data.method259("CloudSpeed", weatherFlakeChannel.floatValue6);
      data.method259("CloudScale", weatherFlakeChannel.floatValue7);
      method800(data, "CloudLit", weatherFlakeChannel.floatValues7);
      method800(data, "CloudShadow", weatherFlakeChannel.floatValues8);
      data.method259("StarAmount", weatherFlakeChannel.floatValue8);
      data.method259("BandAmount", weatherFlakeChannel.floatValue9);
      data.method259("AuroraAmount", weatherFlakeChannel.floatValue10);
      data.method259("MeteorAmount", weatherFlakeChannel.floatValue11);
      data.method259("HazeAmount", weatherFlakeChannel.floatValue12);
      method800(data, "HazeColor", weatherFlakeChannel.floatValues9);
      method800(data, "GlowDir", weatherFlakeChannel.floatValues6);
      data.method259("GlowFocus", weatherFlakeChannel.floatValue);
      data.method259("FrostAmount", weatherFlakeChannel.flag ? Math.clamp(weatherFlakeChannel.floatValue23, 0.0F, 1.0F) * Math.clamp(floatValue, 0.0F, 1.0F) * 0.55F : 0.0F);
      data.method259("Flash", f);
      method800(data, "FlashColor", weatherFlakeChannel.floatValues10);
   }

   private static void method800(WeatherUniforms.Handler data, String str, float[] floatValues) {
      data.method260(str, floatValues[0], floatValues[1], floatValues[2]);
   }

   public static final class Data implements WeatherUniforms.Handler {
      private final PostEffectManager.Entry entry;

      public PostEffectManager.Entry entry() {
         return this.entry;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.entry, other.entry);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(entry);
      }

      @Override
      public String toString() {
         return "Data[" + "entry=" + this.entry + "]";
      }

      public Data(PostEffectManager.Entry entry) {
         this.entry = entry;
      }

      @Override
      public void method259(String str, float floatValue) {
         this.entry.method2828(str, floatValue);
      }

      @Override
      public void method260(String str, float f, float f2, float f3) {
         this.entry.method2830(str, f, f2, f3);
      }

      public PostEffectManager.Entry getEntry() {
         return this.entry;
      }
   }

   public interface Handler {
      void method259(String str, float floatValue);

      void method260(String str, float x, float y, float z);
   }
}
