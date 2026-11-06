package org.zenith.utility.render;

import net.minecraft.client.renderer.fog.FogData;
import org.joml.Vector4f;

public final class FogStateCache {
   private static final Vector4f fogColor = new Vector4f();
   private static float environmentalStart = Float.MAX_VALUE;
   private static float environmentalEnd = Float.MAX_VALUE;
   private static float renderDistanceStart = Float.MAX_VALUE;
   private static float renderDistanceEnd = Float.MAX_VALUE;

   private FogStateCache() {
   }

   public static void captureFog(Vector4f color, FogData fogData) {
      fogColor.set(color);
      environmentalStart = fogData.environmentalStart;
      environmentalEnd = fogData.environmentalEnd;
      renderDistanceStart = fogData.renderDistanceStart;
      renderDistanceEnd = fogData.renderDistanceEnd;
   }

   public static Vector4f getFogColor() {
      return fogColor;
   }

   public static float getEnvironmentalStart() {
      return environmentalStart;
   }

   public static float getEnvironmentalEnd() {
      return environmentalEnd;
   }

   public static float getRenderDistanceStart() {
      return renderDistanceStart;
   }

   public static float getRenderDistanceEnd() {
      return renderDistanceEnd;
   }
}
