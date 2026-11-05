package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class EmberField {
   private static final int CONST_intValue = 5;
   private static final int CONST_intValue2 = 12;
   private final EmberField.Entry[] entrys;
   private final long longValue = System.currentTimeMillis();
   private final Random random = new Random();
   private float floatValue;
   private float floatValue2;
   private float floatValue3;

   public EmberField() {
      ArrayList arrayList = new ArrayList();

      for (byte i = -1; i <= 1; i += 2) {
         for (int j = 0; j < 5; j++) {
            float f = j / 4.0F;
            double d = -0.15 + f * 1.45;
            EmberField.Entry entry = new EmberField.Entry();
            entry.doubleValue = 1.36 - f * 0.05;
            entry.doubleValue2 = 0.15;
            entry.doubleValue3 = i * 0.12;
            entry.doubleValue4 = 0.55;
            entry.doubleValue5 = i * (Math.sin(d) * 1.15 + 0.25);
            entry.doubleValue6 = Math.cos(d) * 0.95;
            entry.doubleValue7 = -0.45 - f * 0.25;
            entry.doubleValue8 = 2.15 - f * 0.45;
            entry.doubleValue9 = 0.085 - f * 0.01;
            entry.doubleValue10 = 0.16;
            entry.doubleValue11 = 0.1;
            entry.doubleValue12 = 2.6;
            entry.floatValue = i * 1.7F + j * 0.9F;
            arrayList.add(entry);
         }
      }

      this.entrys = (EmberField.Entry[])arrayList.toArray(new EmberField.Entry[0]);
   }

   public void method7018(Player player, float floatValue, float f, double d, float f2) {
      Vec3 position = player.getPosition(floatValue);
      float rotLerp = Mth.rotLerp(floatValue, player.yBodyRotO, player.yBodyRot);
      double d2 = Math.toRadians(rotLerp);
      double d3 = -Math.sin(d2);
      double d4 = Math.cos(d2);
      double d5 = -Math.cos(d2);
      double d6 = -Math.sin(d2);
      double d7 = player.isCrouching() ? -0.3 : 0.0;
      float f3 = (float)(System.currentTimeMillis() - this.longValue) / 1000.0F;
      this.floatValue = Mth.clamp(f3 / 0.6F, 0.0F, 1.0F);
      this.floatValue2 = Math.max(0.0F, this.floatValue2 - f * 1.4F);
      this.floatValue3 = f;

      for (EmberField.Entry entry : this.entrys) {
         System.arraycopy(entry.doubleValues, 0, entry.doubleValues4, 0, 12);
         System.arraycopy(entry.doubleValues2, 0, entry.doubleValues5, 0, 12);
         System.arraycopy(entry.doubleValues3, 0, entry.doubleValues6, 0, 12);
         double d8 = position.x - d3 * entry.doubleValue2 + d5 * entry.doubleValue3;
         double d9 = position.y + entry.doubleValue + d7;
         double d10 = position.z - d4 * entry.doubleValue2 + d6 * entry.doubleValue3;
         double d11 = entry.doubleValue5 * (1.0 + 0.4 * this.floatValue2);
         double d12 = -d3 * entry.doubleValue4 + d5 * d11;
         double d13 = entry.doubleValue6 + 0.25 * this.floatValue2;
         double d14 = -d4 * entry.doubleValue4 + d6 * d11;
         double d15 = Math.sqrt(d12 * d12 + d13 * d13 + d14 * d14);
         d12 /= d15;
         d13 /= d15;
         d14 /= d15;
         double d16 = -d14;
         double d17 = Math.sqrt(d16 * d16 + d12 * d12);
         double d18;
         if (d17 < 1.0E-5) {
            d16 = d5;
            d18 = d6;
         } else {
            d16 /= d17;
            d18 = d12 / d17;
         }

         double d19 = entry.doubleValue8 * f2 * this.floatValue / 11.0;
         double d20 = d8;
         double d21 = d9;
         double d22 = d10;
         entry.doubleValues[0] = d8;
         entry.doubleValues2[0] = d9;
         entry.doubleValues3[0] = d10;

         for (int i = 1; i < 12; i++) {
            double d23 = i / 11.0;
            double d24 = Math.pow(d23, 1.15);
            double d25 = entry.doubleValue10 * Math.sin(d23 * 5.2 - d * 2.1 * entry.doubleValue12 + entry.floatValue) * d24;
            double d26 = entry.doubleValue11 * Math.sin(d23 * 4.1 - d * 1.6 * entry.doubleValue12 + entry.floatValue * 1.9) * d24;
            double d27 = entry.doubleValue7 * Math.pow(d23, 1.3) + d25;
            double d28 = Math.cos(d27);
            double d29 = Math.sin(d27);
            double d30 = d12 * d28 + -d18 * d13 * d29;
            double d31 = d13 * d28 + (d18 * d12 - d16 * d14) * d29;
            double d32 = d14 * d28 + d16 * d13 * d29;
            double d33 = Math.cos(d26);
            double d34 = Math.sin(d26);
            double d35 = d30 * d33 + d32 * d34;
            double d36 = -d30 * d34 + d32 * d33;
            d20 += d35 * d19;
            d21 += d31 * d19;
            d22 += d36 * d19;
            entry.doubleValues[i] = d20;
            entry.doubleValues2[i] = d21;
            entry.doubleValues3[i] = d22;
         }

         double d23 = method7022(entry.doubleValues[0] - entry.doubleValues4[0])
            + method7022(entry.doubleValues2[0] - entry.doubleValues5[0])
            + method7022(entry.doubleValues3[0] - entry.doubleValues6[0]);
         if (!entry.initialized || d23 > 9.0) {
            System.arraycopy(entry.doubleValues, 0, entry.doubleValues4, 0, 12);
            System.arraycopy(entry.doubleValues2, 0, entry.doubleValues5, 0, 12);
            System.arraycopy(entry.doubleValues3, 0, entry.doubleValues6, 0, 12);
            entry.initialized = true;
         }
      }
   }

   public void method7019(EmberShards emberShards, float f, float f2) {
      if (!(f2 <= 0.0F)) {
         float f3 = this.floatValue3 > 1.0E-4F ? 1.0F / this.floatValue3 : 0.0F;

         for (EmberField.Entry entry : this.entrys) {
            entry.floatValue2 = entry.floatValue2 + f * 9.0F * f2 * this.floatValue;

            while (entry.floatValue2 >= 1.0F) {
               entry.floatValue2--;
               int n = 3 + this.random.nextInt(9);
               double d = entry.doubleValue9;
               double d2 = method7021((entry.doubleValues[n] - entry.doubleValues4[n]) * f3) * 0.25 + this.random.nextGaussian() * 0.25;
               double d3 = method7021((entry.doubleValues2[n] - entry.doubleValues5[n]) * f3) * 0.25 + 0.35 + this.random.nextDouble() * 0.3;
               double d4 = method7021((entry.doubleValues3[n] - entry.doubleValues6[n]) * f3) * 0.25 + this.random.nextGaussian() * 0.25;
               emberShards.method7578(
                  entry.doubleValues[n] + this.random.nextGaussian() * d,
                  entry.doubleValues2[n] + this.random.nextGaussian() * d,
                  entry.doubleValues3[n] + this.random.nextGaussian() * d,
                  d2,
                  d3,
                  d4,
                  0.016F + this.random.nextFloat() * 0.03F
               );
            }
         }
      }
   }

   public boolean method7020(EmberShards emberShards, LivingEntity referent, int n, float floatValue) {
      if (this.floatValue < 0.6F) {
         return false;
      } else {
         var list = new ArrayList<>(List.of(this.entrys));
         Collections.shuffle(list, this.random);
         int n2 = Math.min(list.size(), 2 + n * 2);

         for (int i = 0; i < n2; i++) {
            EmberField.Entry entry = (EmberField.Entry)list.get(i);
            emberShards.method7579(entry.doubleValues[11], entry.doubleValues2[11], entry.doubleValues3[11], referent, i * 0.045F, floatValue);
         }

         this.floatValue2 = 1.0F;
         return true;
      }
   }

   private static double method7021(double d) {
      return Math.max(-20.0, Math.min(20.0, d));
   }

   private static double method7022(double d) {
      return d * d;
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public float floatValue;
      public final double[] doubleValues = new double[12];
      public final double[] doubleValues2 = new double[12];
      public final double[] doubleValues3 = new double[12];
      public final double[] doubleValues4 = new double[12];
      public final double[] doubleValues5 = new double[12];
      public final double[] doubleValues6 = new double[12];
      public boolean initialized;
      public float floatValue2;
   }
}
