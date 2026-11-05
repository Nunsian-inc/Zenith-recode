package org.zenith;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.util.Mth;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.MathUtils;

public class ZenithColor {
   public static final ZenithColor INSTANCE = new ZenithColor(255, 255, 255);
   public static final ZenithColor INSTANCE2 = new ZenithColor(0, 0, 0);
   public static final ZenithColor INSTANCE3 = new ZenithColor(0, 255, 0);
   public static final ZenithColor INSTANCE4 = new ZenithColor(255, 0, 0);
   public static final ZenithColor INSTANCE5 = new ZenithColor(0, 0, 255);
   public static final ZenithColor INSTANCE6 = new ZenithColor(255, 255, 0);
   public static final ZenithColor INSTANCE7 = new ZenithColor(88, 87, 93);
   public static final ZenithColor INSTANCE8 = new ZenithColor(0, 0, 0, 0);
   public static final ZenithColor INSTANCE9 = method7289("#0E0E10");
   private transient float[] floatValues;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private static final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);

   public ZenithColor(String str) {
      if (str.startsWith("#")) {
         str = str.substring(1);
      }

      long l = Long.parseLong(str, 16);
      int intValue;
      int n;
      int n2;
      int n3;
      if (str.length() == 8) {
         intValue = (int)(l >> 24 & 255L);
         n = (int)(l >> 16 & 255L);
         n2 = (int)(l >> 8 & 255L);
         n3 = (int)(l & 255L);
      } else {
         if (str.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + str);
         }

         intValue = (int)(l >> 16 & 255L);
         n = (int)(l >> 8 & 255L);
         n2 = (int)(l & 255L);
         n3 = 255;
      }

      this.intValue = intValue;
      this.intValue2 = n;
      this.intValue3 = n2;
      this.intValue4 = n3;
   }

   public ZenithColor(int intValue) {
      this(ColorUtils.method6420(intValue), ColorUtils.method6421(intValue), ColorUtils.method6422(intValue), ColorUtils.method6423(intValue));
   }

   public ZenithColor(Color color) {
      this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
   }

   public ZenithColor(int intValue, int green, int blue) {
      this(intValue, green, blue, 255);
   }

   public ZenithColor(int intValue, int green, int blue, int alpha) {
      intValue = Mth.clamp(intValue, 0, 255);
      green = Mth.clamp(green, 0, 255);
      blue = Mth.clamp(blue, 0, 255);
      alpha = Mth.clamp(alpha, 0, 255);
      this.intValue = intValue;
      this.intValue2 = green;
      this.intValue3 = blue;
      this.intValue4 = alpha;
   }

   public int method7287() {
      int n = Math.round((float)this.method7288(this.intValue4));
      int n2 = Math.round((float)this.method7288(this.intValue));
      int n3 = Math.round((float)this.method7288(this.intValue2));
      int n4 = Math.round((float)this.method7288(this.intValue3));
      return (n & 0xFF) << 24 | (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | n4 & 0xFF;
   }

   private int method7288(float intValue) {
      return (int)Math.max(0.0F, Math.min(255.0F, intValue));
   }

   public static ZenithColor method7289(String str) {
      String str2 = str.startsWith("#") ? str.substring(1) : str;
      if (str2.length() != 6 && str2.length() != 8) {
         throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
      } else {
         int intValue = Integer.parseInt(str2.substring(0, 2), 16);
         int green = Integer.parseInt(str2.substring(2, 4), 16);
         int blue = Integer.parseInt(str2.substring(4, 6), 16);
         int alpha = str2.length() == 8 ? Integer.parseInt(str2.substring(6, 8), 16) : 255;
         return new ZenithColor(intValue, green, blue, alpha);
      }
   }

   public static ZenithColor method7290(ZenithColor zenithColor, ZenithColor zenithColor2, float f) {
      float f2 = Math.max(0.0F, Math.min(1.0F, f));
      int intValue = (int)(zenithColor.getIntValue() + (zenithColor2.getIntValue() - zenithColor.getIntValue()) * f2);
      int green = (int)(zenithColor.getIntValue2() + (zenithColor2.getIntValue2() - zenithColor.getIntValue2()) * f2);
      int blue = (int)(zenithColor.getIntValue3() + (zenithColor2.getIntValue3() - zenithColor.getIntValue3()) * f2);
      int alpha = (int)(zenithColor.getIntValue4() + (zenithColor2.getIntValue4() - zenithColor.getIntValue4()) * f2);
      return new ZenithColor(intValue, green, blue, alpha);
   }

   public static ZenithColor method7291(int shade) {
      int alpha = shade >> 24 & 0xFF;
      int intValue = shade >> 16 & 0xFF;
      int green = shade >> 8 & 0xFF;
      int blue = shade & 0xFF;
      return new ZenithColor(intValue, green, blue, alpha);
   }

   public ZenithColor method7292(float visibility) {
      return new ZenithColor(this.intValue, this.intValue2, this.intValue3, this.method7288((int)(255.0F * visibility)));
   }

   public ZenithColor method7293(int alphaValue) {
      return new ZenithColor(this.intValue, this.intValue2, this.intValue3, alphaValue);
   }

   public ZenithColor method7294(float previewAlpha) {
      return this.method7293(Math.max(0, Math.min(255, (int)(this.intValue4 * previewAlpha))));
   }

   public ZenithColor method7295(ZenithColor zenithColor, float hover) {
      hover = Math.min(1.0F, Math.max(0.0F, hover));
      return new ZenithColor(
         (int)MathUtils.method2649(this.getIntValue(), zenithColor.getIntValue(), hover),
         (int)MathUtils.method2649(this.getIntValue2(), zenithColor.getIntValue2(), hover),
         (int)MathUtils.method2649(this.getIntValue3(), zenithColor.getIntValue3(), hover),
         (int)MathUtils.method2649(this.getIntValue4(), zenithColor.getIntValue4(), hover)
      );
   }

   public ZenithColor method7296(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      return new ZenithColor((int)(this.intValue * (1.0F - f)), (int)(this.intValue2 * (1.0F - f)), (int)(this.intValue3 * (1.0F - f)), this.intValue4);
   }

   public static ZenithColor method7297(float hue, float saturation, float brightness) {
      if (saturation == 0.0F) {
         int intValue = (int)(brightness * 255.0F + 0.5F);
         return new ZenithColor(intValue, intValue, intValue);
      } else {
         float f = (hue - (float)Math.floor(hue)) * 6.0F;
         float f2 = f - (float)Math.floor(f);
         float f3 = brightness * (1.0F - saturation);
         float f4 = brightness * (1.0F - saturation * f2);
         float f5 = brightness * (1.0F - saturation * (1.0F - f2));
         float brightness2 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         switch ((int)f) {
            case 0:
               brightness2 = brightness;
               f6 = f5;
               f7 = f3;
               break;
            case 1:
               brightness2 = f4;
               f6 = brightness;
               f7 = f3;
               break;
            case 2:
               brightness2 = f3;
               f6 = brightness;
               f7 = f5;
               break;
            case 3:
               brightness2 = f3;
               f6 = f4;
               f7 = brightness;
               break;
            case 4:
               brightness2 = f5;
               f6 = f3;
               f7 = brightness;
               break;
            case 5:
               brightness2 = brightness;
               f6 = f3;
               f7 = f4;
         }

         return new ZenithColor((int)(brightness2 * 255.0F), (int)(f6 * 255.0F), (int)(f7 * 255.0F));
      }
   }

   public float method7298() {
      return this.method7301()[0];
   }

   public float method7299() {
      return this.method7301()[1];
   }

   public float method7300() {
      return this.method7301()[2];
   }

   private float[] method7301() {
      if (this.floatValues == null) {
         this.floatValues = this.method7302();
      }

      return this.floatValues;
   }

   private float[] method7302() {
      float f = this.intValue / 255.0F;
      float f2 = this.intValue2 / 255.0F;
      float f3 = this.intValue3 / 255.0F;
      float f4 = Math.max(f, Math.max(f2, f3));
      float f5 = Math.min(f, Math.min(f2, f3));
      float f6 = f4 - f5;
      float f7 = 0.0F;
      if (f6 != 0.0F) {
         if (f4 == f) {
            f7 = (f2 - f3) / f6;
         } else if (f4 == f2) {
            f7 = (f3 - f) / f6 + 2.0F;
         } else {
            f7 = (f - f2) / f6 + 4.0F;
         }

         f7 /= 6.0F;
         if (f7 < 0.0F) {
            f7++;
         }
      }

      float f8 = f4 == 0.0F ? 0.0F : f6 / f4;
      return new float[]{f7, f8, f4};
   }

   public ZenithColor method7303(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      return new ZenithColor(
         (int)(this.intValue + (255.0F - this.intValue) * f),
         (int)(this.intValue2 + (255.0F - this.intValue2) * f),
         (int)(this.intValue3 + (255.0F - this.intValue3) * f),
         this.intValue4
      );
   }

   @Override
   public boolean equals(Object applied) {
      if (this == applied) {
         return true;
      } else if (applied != null && this.getClass() == applied.getClass()) {
         ZenithColor zenithColor = (ZenithColor)applied;
         return Float.compare(this.intValue, zenithColor.intValue) == 0
            && Float.compare(this.intValue2, zenithColor.intValue2) == 0
            && Float.compare(this.intValue3, zenithColor.intValue3) == 0
            && Float.compare(this.intValue4, zenithColor.intValue4) == 0;
      } else {
         return false;
      }
   }

   public float method7304(ZenithColor zenithColor) {
      return Math.abs(this.method7298() - zenithColor.method7298())
         + Math.abs(this.method7300() - zenithColor.method7300())
         + Math.abs(this.method7299() - zenithColor.method7299());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.intValue, this.intValue2, this.intValue3, this.intValue4);
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   public int getIntValue4() {
      return this.intValue4;
   }
}
