package org.zenith.utility.misc;

import de.maxhenkel.rnnoise4j.Denoiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoiceDenoiser {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static volatile boolean broken;
   private Denoiser denoiser;
   private short[] shortValues = new short[0];
   private float floatValue;

   public static boolean method801() {
      return !broken;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public boolean method802(short[] shortValues, int length) {
      if (broken) {
         return false;
      } else {
         try {
            if (this.denoiser == null) {
               this.denoiser = new Denoiser();
               this.shortValues = new short[this.denoiser.getFrameSize()];
            }

            int n = this.shortValues.length;
            float floatValue = 0.0F;

            for (int i = 0; i + n <= length; i += n) {
               System.arraycopy(shortValues, i, this.shortValues, 0, n);
               floatValue = Math.max(floatValue, this.denoiser.denoiseInPlace(this.shortValues));
               System.arraycopy(this.shortValues, 0, shortValues, i, n);
            }

            this.floatValue = floatValue;
            return true;
         } catch (Throwable e) {
            broken = true;
            logger.warn("Голос: RNNoise недоступен, остаётся простой шумодав: {}", e.toString());
            this.close();
            return false;
         }
      }
   }

   public void close() {
      if (this.denoiser != null) {
         try {
            this.denoiser.close();
         } catch (Throwable e) {
         }

         this.denoiser = null;
      }
   }
}
