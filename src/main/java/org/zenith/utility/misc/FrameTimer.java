package org.zenith.utility.misc;

import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;

public final class FrameTimer {
   private static final float[] CONST_floatValues = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private static final float[] CONST_floatValues2 = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private static float floatValue = 1.0F;
   private static boolean flag;
   private static final float[] CONST_floatValues3 = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private static float floatValue2 = 1.0F;
   private static boolean flag2;

   private FrameTimer() {
   }

   public static void beginFrame() {
      WorldModule worldModule = WorldModule.INSTANCE;
      WeatherModule weatherModule = WeatherModule.INSTANCE;
      weatherModule.method3108(CONST_floatValues2);
      CONST_floatValues2[3] = CONST_floatValues2[3] * worldModule.method5646();
      floatValue = worldModule.method5645() * weatherModule.method3107();
      flag = true;
   }

   public static void endFrame() {
      flag = false;
   }

   public static void method753(float f, float f2, float f3, float f4, float f5) {
      floatValue2 = f;
      CONST_floatValues3[0] = f2;
      CONST_floatValues3[1] = f3;
      CONST_floatValues3[2] = f4;
      CONST_floatValues3[3] = f5;
      flag2 = true;
   }

   public static void method754() {
      flag2 = false;
   }

   public static float method755() {
      if (flag2) {
         return floatValue2;
      } else {
         return flag ? floatValue : 1.0F;
      }
   }

   public static float[] method756() {
      if (flag2) {
         return CONST_floatValues3;
      } else {
         return flag ? CONST_floatValues2 : CONST_floatValues;
      }
   }
}
