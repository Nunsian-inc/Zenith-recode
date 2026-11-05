package org.zenith.utility.misc;

public final class AnimationState {
   private static final float CONST_floatValue = 0.38F;
   private static final float CONST_floatValue2 = 0.26F;
   private final float[] floatValues = new float[9];
   private String text = "idle";
   private float floatValue;
   private String current = "idle";
   private float floatValue2;
   private String previous;
   private float floatValue3;
   private float floatValue4 = -10.0F;
   private float floatValue5 = 0.38F;
   private boolean flag;
   private float speed = 1.0F;
   private float floatValue6 = 1.0F;
   private float floatValue7 = Float.NaN;
   private float[] floatValues2 = new float[0];
   private float[] floatValues3 = new float[0];
   private boolean frozen;

   public String getCurrent() {
      return this.current;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setFloatValue6(float f) {
      this.floatValue6 = f;
   }

   public void method6539(Class15 key, String text, float floatValue) {
      String current = method6545(key, text);
      if (current != null && !current.equals(this.text)) {
         this.text = current;
         this.floatValue = 0.0F;
         if (!this.flag) {
            this.method6544(current, floatValue, 0.0F, 0.38F);
         }
      }
   }

   public boolean method6540(Class15 key, String text, float floatValue) {
      Class37 class2 = key.method2032(text);
      if (class2 == null) {
         return false;
      } else {
         this.flag = !class2.isFlag();
         this.method6544(text, floatValue, 0.0F, 0.26F);
         return true;
      }
   }

   public void method6541(float floatValue) {
      this.flag = false;
      this.method6544(this.text, floatValue, this.floatValue, 0.38F);
   }

   public void method6542(Class15 key, float floatValue) {
      this.method6543(floatValue);
      if (this.flag) {
         Class37 class2 = key.method2032(this.current);
         if (class2 == null || this.floatValue2 >= class2.getFloatValue()) {
            this.flag = false;
            this.method6544(this.text, floatValue, this.floatValue, 0.38F);
         }
      }
   }

   private void method6543(float floatValue) {
      if (Float.isNaN(this.floatValue7)) {
         this.floatValue7 = floatValue;
      } else {
         float f = Math.max(0.0F, Math.min(0.25F, floatValue - this.floatValue7));
         this.floatValue7 = floatValue;
         if (!(f <= 0.0F)) {
            this.speed = this.speed + (this.floatValue6 - this.speed) * Math.min(1.0F, f * 6.0F);
            boolean flag = !this.flag;
            float speed = flag ? this.speed : 1.0F;
            this.floatValue2 += f * speed;
            if (this.previous != null) {
               this.floatValue3 += f * speed;
            }

            if (this.flag) {
               this.floatValue += f;
            } else {
               this.floatValue = this.floatValue2;
            }
         }
      }
   }

   private void method6544(String current, float floatValue, float f, float f2) {
      if (!current.equals(this.current)) {
         float f3 = (floatValue - this.floatValue4) / this.floatValue5;
         if (f3 < 1.0F && this.floatValues2.length > 0) {
            if (this.floatValues3.length != this.floatValues2.length) {
               this.floatValues3 = new float[this.floatValues2.length];
            }

            System.arraycopy(this.floatValues2, 0, this.floatValues3, 0, this.floatValues2.length);
            this.frozen = true;
         } else {
            this.frozen = false;
         }

         this.previous = this.current;
         this.floatValue3 = this.floatValue2;
         this.current = current;
         this.floatValue2 = f;
         this.floatValue4 = floatValue;
         this.floatValue5 = f2;
      }
   }

   private static String method6545(Class15 key, String text) {
      if (key.method2032(text) != null) {
         return text;
      } else {
         return switch (text) {
            case "run" -> key.method2032("walk") != null ? "walk" : method6545(key, "idle");
            case "perch" -> key.method2032("sit") != null ? "sit" : method6545(key, "idle");
            case "walk", "fly", "sleep", "sneak", "glide", "idle2", "sit", "lie" -> method6545(key, "idle");
            case "idle" -> key.method2032("idle") != null ? "idle" : null;
            default -> method6545(key, "idle");
         };
      }
   }

   public void method6546(Class15 key, int n, float floatValue, float[] floatValues) {
      this.method6543(floatValue);
      Class37 class2 = key.method2032(this.current);
      if (class2 == null) {
         method6550(floatValues);
      } else {
         class2.method6242(n, class2.method6240(this.floatValue2), floatValues);
      }

      float f = (floatValue - this.floatValue4) / this.floatValue5;
      if (f < 1.0F && this.previous != null) {
         float f2 = method6549(Math.max(0.0F, f));
         if (this.frozen && n * 9 + 9 <= this.floatValues3.length) {
            for (int i = 0; i < 9; i++) {
               float f3 = this.floatValues3[n * 9 + i];
               floatValues[i] = f3 + (floatValues[i] - f3) * f2;
            }
         } else {
            Class37 class3 = key.method2032(this.previous);
            if (class3 != null) {
               class3.method6242(n, class3.method6240(this.floatValue3), this.floatValues);

               for (int i = 0; i < 9; i++) {
                  floatValues[i] = this.floatValues[i] + (floatValues[i] - this.floatValues[i]) * f2;
               }
            }
         }
      }

      this.method6547(n, floatValues);
   }

   private void method6547(int n, float[] floatValues) {
      int n2 = (n + 1) * 9;
      if (this.floatValues2.length < n2) {
         float[] floatValues2 = new float[Math.max(n2, this.floatValues2.length * 2)];
         System.arraycopy(this.floatValues2, 0, floatValues2, 0, this.floatValues2.length);

         for (int i = this.floatValues2.length; i < floatValues2.length; i += 9) {
            floatValues2[i + 6] = floatValues2[i + 7] = floatValues2[i + 8] = 1.0F;
         }

         this.floatValues2 = floatValues2;
      }

      System.arraycopy(floatValues, 0, this.floatValues2, n * 9, 9);
   }

   public void method6548(Class15 key, String text, int n, float powerForTime, float[] floatValues) {
      Class37 class2 = key.method2032(text);
      if (class2 == null) {
         method6550(floatValues);
      } else {
         class2.method6242(n, Math.max(0.0F, Math.min(1.0F, powerForTime)) * class2.getFloatValue(), floatValues);
      }
   }

   private static float method6549(float f) {
      return f * f * f * (f * (f * 6.0F - 15.0F) + 10.0F);
   }

   private static void method6550(float[] floatValues) {
      floatValues[0] = floatValues[1] = floatValues[2] = 0.0F;
      floatValues[3] = floatValues[4] = floatValues[5] = 0.0F;
      floatValues[6] = floatValues[7] = floatValues[8] = 1.0F;
   }
}
