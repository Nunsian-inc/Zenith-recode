package org.zenith.utility.game;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.Glowboost;

public final class EmberShards {
   private static final int CONST_intValue = 2000;
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final int CONST_intValue2 = 0;
   private static final int CONST_intValue3 = 1;
   private final List<EmberShards.Entry2> items = new ArrayList<>();
   private final List<EmberShards.Entry4> items2 = new ArrayList<>();
   private final List<EmberShards.Entry> items3 = new ArrayList<>();
   private final List<EmberShards.Entry3> items4 = new ArrayList<>();
   private final Random random = new Random();
   private long longValue;
   private float floatValue;
   private EmberShards.Handler handler;

   public void setHandler(EmberShards.Handler handler) {
      this.handler = handler;
   }

   public void clear() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.floatValue = 0.0F;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   private void method7577(double doubleValue, double d, double d2, double d3, double d4, double d5) {
      this.method7581(doubleValue, d, d2, 0.6F, 0.24F);
      this.method7582(doubleValue, d, d2, d3, d4, d5, 0.8F, 0.42F, 0.17F);
      this.method7582(doubleValue, d, d2, d3, d4, d5, 1.4F, 0.55F, 0.06F);

      for (int i = 0; i < 10; i++) {
         double d6 = d3 + this.random.nextGaussian() * 0.6;
         double d7 = d4 + this.random.nextGaussian() * 0.6;
         double d8 = d5 + this.random.nextGaussian() * 0.6;
         double d9 = Math.sqrt(d6 * d6 + d7 * d7 + d8 * d8);
         if (!(d9 < 1.0E-4)) {
            double d10 = 13.0 + this.random.nextDouble() * 12.0;
            this.method7583(
               1, doubleValue, d, d2, d6 / d9 * d10, d7 / d9 * d10, d8 / d9 * d10, 0.09F + this.random.nextFloat() * 0.12F, 0.03F
            );
         }
      }

      for (int i = 0; i < 8; i++) {
         this.method7578(
            doubleValue,
            d,
            d2,
            d3 * 1.5 + this.random.nextGaussian() * 1.2,
            0.6 + this.random.nextDouble(),
            d5 * 1.5 + this.random.nextGaussian() * 1.2,
            0.018F + this.random.nextFloat() * 0.025F
         );
      }

      this.floatValue = Math.min(1.2F, Math.max(this.floatValue, 0.4F));
   }

   public void method7578(double doubleValue, double d, double d2, double d3, double d4, double d5, float size) {
      this.method7583(0, doubleValue, d, d2, d3, d4, d5, 0.7F + this.random.nextFloat() * 0.9F, size);
   }

   public void method7579(double doubleValue, double d, double d2, LivingEntity referent, float floatValue, float partialTickTime) {
      if (referent != null) {
         EmberShards.Entry3 entry3 = new EmberShards.Entry3();
         entry3.doubleValue = doubleValue;
         entry3.doubleValue2 = d;
         entry3.doubleValue3 = d2;
         entry3.weakReference = new WeakReference<>(referent);
         entry3.floatValue = floatValue;
         entry3.longValue = ++this.longValue;
         Vec3 vec = method7580(referent, partialTickTime, entry3.longValue);
         entry3.doubleValue7 = vec.x;
         entry3.doubleValue8 = vec.y;
         entry3.doubleValue9 = vec.z;
         double d3 = Math.sqrt(method7592(vec.x - doubleValue) + method7592(vec.y - d) + method7592(vec.z - d2));
         entry3.floatValue3 = (float)(0.12 + d3 * 0.022);
         entry3.doubleValue4 = this.random.nextGaussian() * 0.5;
         entry3.doubleValue5 = 0.5 + this.random.nextDouble() * 0.7;
         entry3.doubleValue6 = this.random.nextGaussian() * 0.5;
         this.items4.add(entry3);
      }
   }

   private static Vec3 method7580(LivingEntity referent, float partialTickTime, long longValue) {
      Vec3 position = referent.getPosition(partialTickTime);
      double y = referent.getBbHeight() * (0.45 + 0.3 * Glowboost.method3268(longValue, 0, 1));
      double d = referent.getBbWidth() * 0.3;
      return position.add((Glowboost.method3268(longValue, 0, 2) - 0.5) * d, y, (Glowboost.method3268(longValue, 0, 3) - 0.5) * d);
   }

   private void method7581(double doubleValue, double d, double d2, float size, float f) {
      EmberShards.Entry entry = new EmberShards.Entry();
      entry.doubleValue = doubleValue;
      entry.doubleValue2 = d;
      entry.doubleValue3 = d2;
      entry.size = size;
      entry.floatValue2 = f;
      this.items3.add(entry);
   }

   private void method7582(double doubleValue, double d, double d2, double d3, double d4, double d5, float f, float f2, float width) {
      EmberShards.Entry4 entry4 = new EmberShards.Entry4();
      entry4.doubleValue = doubleValue;
      entry4.doubleValue2 = d;
      entry4.doubleValue3 = d2;
      entry4.doubleValue4 = d3;
      entry4.doubleValue5 = d4;
      entry4.doubleValue6 = d5;
      entry4.floatValue3 = f;
      entry4.floatValue2 = f2;
      entry4.width = width;
      this.items2.add(entry4);
   }

   private void method7583(int kind, double doubleValue, double d, double d2, double d3, double d4, double d5, float f, float size) {
      if (this.items.size() < 2000) {
         EmberShards.Entry2 entry2 = new EmberShards.Entry2();
         entry2.kind = kind;
         entry2.doubleValue = doubleValue;
         entry2.doubleValue2 = d;
         entry2.doubleValue3 = d2;
         entry2.doubleValue4 = d3;
         entry2.doubleValue5 = d4;
         entry2.doubleValue6 = d5;
         entry2.floatValue2 = f;
         entry2.size = size;
         entry2.longValue = ++this.longValue;
         entry2.floatValue3 = this.random.nextFloat() * (float) (Math.PI * 2);
         entry2.floatValue4 = (this.random.nextFloat() - 0.5F) * 9.0F;
         this.items.add(entry2);
      }
   }

   public void update(float f, float floatValue) {
      this.floatValue = Math.max(0.0F, this.floatValue - f * 4.2F);

      for (int i = this.items.size() - 1; i >= 0; i--) {
         EmberShards.Entry2 entry2 = this.items.get(i);
         entry2.floatValue += f;
         if (entry2.floatValue >= entry2.floatValue2) {
            method7591(this.items, i);
         } else {
            if (entry2.kind == 0) {
               double d = Math.pow(0.45, f);
               entry2.doubleValue4 = entry2.doubleValue4 * d + Mth.sin(entry2.floatValue * 6.3F + (float)entry2.longValue) * 0.9 * f;
               entry2.doubleValue6 = entry2.doubleValue6 * d + Mth.cos(entry2.floatValue * 5.1F + (float)entry2.longValue * 0.7F) * 0.9 * f;
               entry2.doubleValue5 = entry2.doubleValue5 * d + 0.9 * f;
               entry2.floatValue3 = entry2.floatValue3 + entry2.floatValue4 * f;
            } else {
               double d = Math.pow(0.03, f);
               entry2.doubleValue4 *= d;
               entry2.doubleValue5 *= d;
               entry2.doubleValue6 *= d;
            }

            entry2.doubleValue = entry2.doubleValue + entry2.doubleValue4 * f;
            entry2.doubleValue2 = entry2.doubleValue2 + entry2.doubleValue5 * f;
            entry2.doubleValue3 = entry2.doubleValue3 + entry2.doubleValue6 * f;
         }
      }

      for (int i = this.items2.size() - 1; i >= 0; i--) {
         EmberShards.Entry4 entry4 = this.items2.get(i);
         entry4.floatValue += f;
         if (entry4.floatValue >= entry4.floatValue2) {
            method7591(this.items2, i);
         }
      }

      for (int i = this.items3.size() - 1; i >= 0; i--) {
         EmberShards.Entry entry = this.items3.get(i);
         entry.floatValue += f;
         if (entry.floatValue >= entry.floatValue2) {
            method7591(this.items3, i);
         }
      }

      this.method7584(f, floatValue);
   }

   private void method7584(float f, float floatValue) {
      for (int i = this.items4.size() - 1; i >= 0; i--) {
         EmberShards.Entry3 entry3 = this.items4.get(i);
         entry3.floatValue2 += f;
         LivingEntity referent = entry3.weakReference.get();
         if (!entry3.flag) {
            if (referent != null && referent.isAlive()) {
               Vec3 vec = method7580(referent, floatValue, entry3.longValue);
               entry3.doubleValue7 = vec.x;
               entry3.doubleValue8 = vec.y;
               entry3.doubleValue9 = vec.z;
            }

            float f2 = (entry3.floatValue2 - entry3.floatValue) / entry3.floatValue3;
            if (!(f2 < 1.0F)) {
               entry3.flag = true;
               double[] doubles = method7586(entry3, 1.0F);
               entry3.doubleValue10 = doubles[0];
               entry3.doubleValue11 = doubles[1];
               entry3.doubleValue12 = doubles[2];
               if (referent != null) {
                  Vec3 position = referent.getPosition(floatValue);
                  entry3.doubleValue13 = entry3.doubleValue7 - position.x;
                  entry3.doubleValue14 = entry3.doubleValue8 - position.y;
                  entry3.doubleValue15 = entry3.doubleValue9 - position.z;
               }

               this.method7577(entry3.doubleValue7, entry3.doubleValue8, entry3.doubleValue9, entry3.doubleValue10, entry3.doubleValue11, entry3.doubleValue12);
               if (this.handler != null) {
                  this.handler.onShardHit(entry3.doubleValue7, entry3.doubleValue8, entry3.doubleValue9);
               }
            }
         } else {
            entry3.floatValue4 += f;
            if (referent != null && referent.isAlive()) {
               Vec3 position = referent.getPosition(floatValue);
               entry3.doubleValue7 = position.x + entry3.doubleValue13;
               entry3.doubleValue8 = position.y + entry3.doubleValue14;
               entry3.doubleValue9 = position.z + entry3.doubleValue15;
            }

            if (entry3.floatValue4 >= 0.9F) {
               method7591(this.items4, i);
            }
         }
      }
   }

   private static double[] method7585(EmberShards.Entry3 entry3, float f) {
      double d = (entry3.doubleValue + entry3.doubleValue7) * 0.5 + entry3.doubleValue4;
      double d2 = (entry3.doubleValue2 + entry3.doubleValue8) * 0.5 + entry3.doubleValue5;
      double d3 = (entry3.doubleValue3 + entry3.doubleValue9) * 0.5 + entry3.doubleValue6;
      double d4 = 1.0F - f;
      return new double[]{
         d4 * d4 * entry3.doubleValue + 2.0 * d4 * f * d + f * f * entry3.doubleValue7,
         d4 * d4 * entry3.doubleValue2 + 2.0 * d4 * f * d2 + f * f * entry3.doubleValue8,
         d4 * d4 * entry3.doubleValue3 + 2.0 * d4 * f * d3 + f * f * entry3.doubleValue9
      };
   }

   private static double[] method7586(EmberShards.Entry3 entry3, float f) {
      double d = (entry3.doubleValue + entry3.doubleValue7) * 0.5 + entry3.doubleValue4;
      double d2 = (entry3.doubleValue2 + entry3.doubleValue8) * 0.5 + entry3.doubleValue5;
      double d3 = (entry3.doubleValue3 + entry3.doubleValue9) * 0.5 + entry3.doubleValue6;
      double d4 = 2.0F * (1.0F - f) * (d - entry3.doubleValue) + 2.0F * f * (entry3.doubleValue7 - d);
      double d5 = 2.0F * (1.0F - f) * (d2 - entry3.doubleValue2) + 2.0F * f * (entry3.doubleValue8 - d2);
      double d6 = 2.0F * (1.0F - f) * (d3 - entry3.doubleValue3) + 2.0F * f * (entry3.doubleValue9 - d3);
      double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
      return d7 < 1.0E-6 ? new double[]{0.0, 0.0, 1.0} : new double[]{d4 / d7, d5 / d7, d6 / d7};
   }

   public void method7587(Glowboost glowboost, int color) {
      int n = method7590(color, -1, 0.55F);

      for (EmberShards.Entry2 entry2 : this.items) {
         float f = entry2.floatValue / entry2.floatValue2;
         if (entry2.kind == 0) {
            float f2 = 0.7F + 0.3F * Mth.sin(entry2.floatValue * 23.0F + (float)entry2.longValue);
            float alpha = Glowboost.method3267(f, 0.12F, 0.4F) * f2;
            float f3 = entry2.size * (1.0F - f * 0.5F);
            glowboost.method3243(entry2.doubleValue, entry2.doubleValue2, entry2.doubleValue3, f3, entry2.floatValue3, ColorUtils.method6439(n, alpha));
            glowboost.method3250(entry2.doubleValue, entry2.doubleValue2, entry2.doubleValue3, f3 * 3.6F, color, alpha * 0.55F);
         } else {
            float alpha = 1.0F - f;
            glowboost.method3236(
               entry2.doubleValue,
               entry2.doubleValue2,
               entry2.doubleValue3,
               entry2.doubleValue - entry2.doubleValue4 * 0.032,
               entry2.doubleValue2 - entry2.doubleValue5 * 0.032,
               entry2.doubleValue3 - entry2.doubleValue6 * 0.032,
               ColorUtils.method6439(n, alpha)
            );
            glowboost.method3250(entry2.doubleValue, entry2.doubleValue2, entry2.doubleValue3, 0.07F, color, alpha * 0.5F);
         }
      }

      for (EmberShards.Entry4 entry4 : this.items2) {
         float f = entry4.floatValue / entry4.floatValue2;
         float easeOut = Glowboost.easeOut(f);
         float f2 = entry4.floatValue3 * (0.12F + 0.88F * easeOut);
         float f3 = entry4.width * (1.0F - f) + 0.008F;
         float f4 = (1.0F - f) * (1.0F - f);
         method7589(
            glowboost,
            entry4.doubleValue,
            entry4.doubleValue2,
            entry4.doubleValue3,
            entry4.doubleValue4,
            entry4.doubleValue5,
            entry4.doubleValue6,
            f2,
            f3,
            ColorUtils.method6439(color, 0.95F * f4)
         );
         method7589(
            glowboost,
            entry4.doubleValue,
            entry4.doubleValue2,
            entry4.doubleValue3,
            entry4.doubleValue4,
            entry4.doubleValue5,
            entry4.doubleValue6,
            f2 * 0.8F,
            f3 * 0.35F,
            ColorUtils.method6439(n, 0.7F * f4)
         );
      }

      for (EmberShards.Entry entry : this.items3) {
         float f = entry.floatValue / entry.floatValue2;
         float f2 = (1.0F - f) * (1.0F - f);
         glowboost.method3250(entry.doubleValue, entry.doubleValue2, entry.doubleValue3, entry.size * (0.55F + f * 0.9F), color, f2 * 1.3F);
         glowboost.method3250(entry.doubleValue, entry.doubleValue2, entry.doubleValue3, entry.size * 0.35F, n, f2 * f2 * 1.4F);
      }

      var iterator = this.items4.iterator();

      while (true) {
         double d;
         double d2;
         double d3;
         float alpha;
         double doubleValue;
         double d4;
         double d5;
         label44:
         while (true) {
            if (!iterator.hasNext()) {
               return;
            }

            EmberShards.Entry3 entry3 = (EmberShards.Entry3)iterator.next();
            if (!entry3.flag) {
               float f = (entry3.floatValue2 - entry3.floatValue) / entry3.floatValue3;
               if (f < 0.0F) {
                  continue;
               }

               double[] doubles = method7585(entry3, f);
               double[] doubles2 = method7586(entry3, f);
               doubleValue = doubles[0];
               d4 = doubles[1];
               d5 = doubles[2];
               d = doubles2[0];
               d2 = doubles2[1];
               d3 = doubles2[2];
               alpha = Math.min(1.0F, f * 6.0F + 0.2F);
               int n2 = 1;

               while (true) {
                  if (n2 > 5) {
                     break label44;
                  }

                  float f2 = f - n2 * 0.06F;
                  if (f2 < 0.0F) {
                     break label44;
                  }

                  double[] doubles3 = method7585(entry3, f2);
                  glowboost.method3250(doubles3[0], doubles3[1], doubles3[2], 0.16F - n2 * 0.02F, color, alpha * (0.5F - n2 * 0.08F));
                  n2++;
               }
            }

            doubleValue = entry3.doubleValue7 - entry3.doubleValue10 * 0.12;
            d4 = entry3.doubleValue8 - entry3.doubleValue11 * 0.12;
            d5 = entry3.doubleValue9 - entry3.doubleValue12 * 0.12;
            d = entry3.doubleValue10;
            d2 = entry3.doubleValue11;
            d3 = entry3.doubleValue12;
            float f = 1.0F - entry3.floatValue4 / 0.9F;
            alpha = f * f;
            break;
         }

         method7588(glowboost, doubleValue, d4, d5, d, d2, d3, 0.5F, 0.075F, color, n, alpha);
      }
   }

   private static void method7588(
      Glowboost glowboost,
      double doubleValue,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      float f,
      float f2,
      int color,
      int n,
      float alpha
   ) {
      if (!(alpha < 0.02F)) {
         double d6 = -d5;
         double d7 = 0.0;
         double d8 = d3;
         double d9 = Math.sqrt(d6 * d6 + d3 * d3);
         if (d9 < 1.0E-4) {
            d6 = 1.0;
            d8 = 0.0;
            d9 = 1.0;
         }

         d6 /= d9;
         d8 /= d9;
         double d10 = d4 * d8 - d5 * d7;
         double d11 = d5 * d6 - d3 * d8;
         double d12 = d3 * d7 - d4 * d6;
         double doubleValue2 = doubleValue + d3 * f * 0.6;
         double d13 = d + d4 * f * 0.6;
         double d14 = d2 + d5 * f * 0.6;
         double d15 = doubleValue - d3 * f * 0.4;
         double d16 = d - d4 * f * 0.4;
         double d17 = d2 - d5 * f * 0.4;
         int n2 = ColorUtils.method6439(color, 0.75F * alpha);
         glowboost.method3238(
            doubleValue2,
            d13,
            d14,
            doubleValue + d6 * f2,
            d + d7 * f2,
            d2 + d8 * f2,
            d15,
            d16,
            d17,
            doubleValue - d6 * f2,
            d - d7 * f2,
            d2 - d8 * f2,
            n2
         );
         glowboost.method3238(
            doubleValue2,
            d13,
            d14,
            doubleValue + d10 * f2,
            d + d11 * f2,
            d2 + d12 * f2,
            d15,
            d16,
            d17,
            doubleValue - d10 * f2,
            d - d11 * f2,
            d2 - d12 * f2,
            n2
         );
         glowboost.method3236(doubleValue2, d13, d14, d15, d16, d17, ColorUtils.method6439(n, alpha));
         glowboost.method3250(doubleValue, d, d2, f * 0.55F, color, alpha * 0.7F);
      }
   }

   private static void method7589(
      Glowboost glowboost, double doubleValue, double d, double d2, double d3, double d4, double d5, float f, float f2, int n
   ) {
      if (n >>> 24 != 0 && !(f <= 0.001F)) {
         double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
         if (!(d6 < 1.0E-6)) {
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = -d5;
            double d8 = 0.0;
            double d9 = d3;
            double d10 = Math.sqrt(d7 * d7 + d3 * d3);
            if (d10 < 1.0E-4) {
               d7 = 1.0;
               d9 = 0.0;
               d10 = 1.0;
            }

            d7 /= d10;
            d9 /= d10;
            double d11 = d4 * d9 - d5 * d8;
            double d12 = d5 * d7 - d3 * d9;
            double d13 = d3 * d8 - d4 * d7;
            double d14 = Math.max(0.0, f - f2 * 0.5);
            double d15 = f + f2 * 0.5;
            byte b = 32;
            double d16 = 1.0;
            double d17 = 0.0;

            for (int i = 1; i <= b; i++) {
               double d18 = i * (Math.PI * 2) / b;
               double d19 = Math.cos(d18);
               double d20 = Math.sin(d18);
               double d21 = d7 * d16 + d11 * d17;
               double d22 = d8 * d16 + d12 * d17;
               double d23 = d9 * d16 + d13 * d17;
               double d24 = d7 * d19 + d11 * d20;
               double d25 = d8 * d19 + d12 * d20;
               double d26 = d9 * d19 + d13 * d20;
               glowboost.method3238(
                  doubleValue + d21 * d14,
                  d + d22 * d14,
                  d2 + d23 * d14,
                  doubleValue + d24 * d14,
                  d + d25 * d14,
                  d2 + d26 * d14,
                  doubleValue + d24 * d15,
                  d + d25 * d15,
                  d2 + d26 * d15,
                  doubleValue + d21 * d15,
                  d + d22 * d15,
                  d2 + d23 * d15,
                  n
               );
               d16 = d19;
               d17 = d20;
            }
         }
      }
   }

   public static int method7590(int color, int n, float f) {
      float f2 = Mth.clamp(f, 0.0F, 1.0F);
      int n2 = color >>> 24 & 0xFF;
      int n3 = color >> 16 & 0xFF;
      int n4 = color >> 8 & 0xFF;
      int n5 = color & 0xFF;
      int n6 = n >>> 24 & 0xFF;
      int n7 = n >> 16 & 0xFF;
      int n8 = n >> 8 & 0xFF;
      int n9 = n & 0xFF;
      return Math.round(n2 + (n6 - n2) * f2) << 24
         | Math.round(n3 + (n7 - n3) * f2) << 16
         | Math.round(n4 + (n8 - n4) * f2) << 8
         | Math.round(n5 + (n9 - n5) * f2);
   }

   private static <T> void method7591(List<T> items, int n) {
      int n2 = items.size() - 1;
      if (n != n2) {
         items.set(n, items.get(n2));
      }

      items.remove(n2);
   }

   private static double method7592(double d) {
      return d * d;
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public float floatValue;
      public float floatValue2;
      public float size;
   }

   private static final class Entry2 {
      public int kind;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public float floatValue;
      public float floatValue2;
      public float size;
      public float floatValue3;
      public float floatValue4;
      public long longValue;
   }

   private static final class Entry3 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public WeakReference<LivingEntity> weakReference;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public boolean flag;
      public float floatValue4;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;
      public double doubleValue15;
      public long longValue;
   }

   private static final class Entry4 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float width;
   }

   public interface Handler {
      void onShardHit(double d, double d2, double d3);
   }
}
