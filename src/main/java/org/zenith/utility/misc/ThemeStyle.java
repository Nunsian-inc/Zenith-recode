package org.zenith.utility.misc;

import lombok.Generated;
import org.zenith.ZenithColor;

public class ThemeStyle {
   private final String text;
   private final String text2;
   private final ThemeStyle.Data data;
   private ZenithColor zenithColor;
   private ZenithColor zenithColor2;
   private ZenithColor zenithColor3;
   private ZenithColor zenithColor4;
   private ZenithColor zenithColor5;
   private ZenithColor zenithColor6;
   private ZenithColor zenithColor7;
   private ZenithColor zenithColor8;
   private ZenithColor zenithColor9;
   private ZenithColor zenithColor10;
   private ZenithColor zenithColor11;
   private ZenithColor zenithColor12;
   private ZenithColor zenithColor13;
   private ZenithColor zenithColor14;
   private ZenithColor zenithColor15;
   private ZenithColor zenithColor16;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4 = false;
   public static final ThemeStyle INSTANCE = ThemeStyle.Entry.method2838()
      .method2839(new ZenithColor(181, 162, 255))
      .method2840(new ZenithColor(255, 203, 162))
      .method2841(ZenithColor.INSTANCE3)
      .method2842(new ZenithColor(181, 162, 255))
      .method2843(new ZenithColor(255, 203, 162))
      .method2844(new ZenithColor(88, 87, 93))
      .method2845(new ZenithColor(128, 127, 133))
      .method2846(new ZenithColor(32, 31, 37))
      .method2847(new ZenithColor(68, 67, 73))
      .method2848(new ZenithColor(48, 47, 53))
      .method2849(new ZenithColor(38, 37, 43))
      .method2850(new ZenithColor(28, 27, 33))
      .method2851(new ZenithColor(35, 34, 40))
      .method2852(new ZenithColor(25, 24, 30))
      .method2853(new ZenithColor(255, 255, 255))
      .method2854(new ZenithColor(23, 22, 28))
      .method2855(false)
      .method2856(false)
      .method2857(false)
      .method2858("Dark", "8");
   public static final ThemeStyle INSTANCE2 = ThemeStyle.Entry.method2838()
      .method2839(new ZenithColor(123, 93, 234))
      .method2840(new ZenithColor(255, 192, 121))
      .method2841(ZenithColor.INSTANCE3)
      .method2842(new ZenithColor(123, 93, 234))
      .method2843(new ZenithColor(255, 192, 121))
      .method2844(new ZenithColor(138, 137, 143))
      .method2845(new ZenithColor(148, 147, 153))
      .method2846(new ZenithColor(236, 236, 236))
      .method2847(new ZenithColor(178, 177, 183))
      .method2848(new ZenithColor(188, 187, 193))
      .method2849(new ZenithColor(229, 229, 229))
      .method2850(new ZenithColor(246, 246, 246))
      .method2851(new ZenithColor(229, 229, 229))
      .method2852(new ZenithColor(251, 251, 251))
      .method2853(new ZenithColor(23, 22, 28))
      .method2854(new ZenithColor(255, 255, 255))
      .method2855(false)
      .method2856(false)
      .method2857(false)
      .method2858("Light", "T");
   public static final ThemeStyle INSTANCE3 = ThemeStyle.Entry.method2838()
      .method2839(new ZenithColor(181, 162, 255))
      .method2840(new ZenithColor(255, 203, 162))
      .method2841(ZenithColor.INSTANCE3)
      .method2842(new ZenithColor(181, 162, 255))
      .method2843(new ZenithColor(255, 203, 162))
      .method2844(new ZenithColor(88, 87, 93))
      .method2845(new ZenithColor(128, 127, 133))
      .method2846(new ZenithColor(32, 31, 37))
      .method2847(new ZenithColor(68, 67, 73))
      .method2848(new ZenithColor(48, 47, 53))
      .method2849(new ZenithColor(38, 37, 43))
      .method2850(new ZenithColor(28, 27, 33))
      .method2851(new ZenithColor(35, 34, 40))
      .method2852(new ZenithColor(25, 24, 30))
      .method2853(new ZenithColor(255, 255, 255))
      .method2854(new ZenithColor(23, 22, 28))
      .method2855(false)
      .method2856(false)
      .method2857(false)
      .method2858("Custom", "F");

   public ThemeStyle(String text, String str, ThemeStyle.Entry entry) {
      this.text = text;
      this.text2 = str;
      this.zenithColor = entry.zenithColor;
      this.zenithColor2 = entry.zenithColor2;
      this.zenithColor3 = entry.zenithColor3 != null ? entry.zenithColor3 : ZenithColor.INSTANCE3;
      this.zenithColor4 = entry.zenithColor4 != null ? entry.zenithColor4 : this.zenithColor2;
      this.zenithColor5 = entry.zenithColor5 != null ? entry.zenithColor5 : this.zenithColor2;
      this.zenithColor6 = entry.zenithColor6;
      this.zenithColor7 = entry.zenithColor7;
      this.zenithColor8 = entry.zenithColor8;
      this.zenithColor9 = entry.zenithColor9;
      this.zenithColor10 = entry.zenithColor10;
      this.zenithColor11 = entry.zenithColor11;
      this.zenithColor12 = entry.zenithColor12;
      this.zenithColor13 = entry.zenithColor13;
      this.zenithColor14 = entry.zenithColor14;
      this.zenithColor15 = entry.zenithColor15;
      this.zenithColor16 = entry.zenithColor16;
      this.flag = entry.flag;
      this.flag2 = entry.flag2;
      this.flag3 = entry.flag3;
      this.data = new ThemeStyle.Data(
         entry.zenithColor,
         entry.zenithColor2,
         entry.zenithColor3,
         entry.zenithColor6,
         entry.zenithColor7,
         entry.zenithColor8,
         entry.zenithColor9,
         entry.zenithColor10,
         entry.zenithColor11,
         entry.zenithColor12,
         entry.zenithColor13,
         entry.zenithColor14,
         entry.zenithColor15,
         entry.zenithColor16,
         entry.flag,
         entry.flag2,
         entry.flag3
      );
   }

   public void reset() {
      this.zenithColor = this.data.zenithColor;
      this.zenithColor2 = this.data.zenithColor2;
      this.zenithColor3 = this.data.zenithColor3;
      this.zenithColor6 = this.data.zenithColor4;
      this.zenithColor7 = this.data.zenithColor5;
      this.zenithColor8 = this.data.zenithColor6;
      this.zenithColor9 = this.data.zenithColor7;
      this.zenithColor10 = this.data.zenithColor8;
      this.zenithColor11 = this.data.zenithColor9;
      this.zenithColor12 = this.data.zenithColor10;
      this.zenithColor13 = this.data.zenithColor11;
      this.zenithColor14 = this.data.zenithColor12;
      this.zenithColor15 = this.data.zenithColor13;
      this.zenithColor16 = this.data.zenithColor14;
      this.flag = this.data.flag;
      this.flag2 = this.data.flag2;
      this.flag3 = this.data.flag3;
      this.flag4 = true;
   }

   public ThemeStyle method2837(ThemeStyle themeStyle2, float hover) {
      return ThemeStyle.Entry.method2838()
         .method2839(this.zenithColor.method7295(themeStyle2.getZenithColor(), hover))
         .method2840(this.zenithColor2.method7295(themeStyle2.getZenithColor2(), hover))
         .method2841(this.zenithColor3.method7295(themeStyle2.getZenithColor3(), hover))
         .method2844(this.zenithColor6.method7295(themeStyle2.getZenithColor6(), hover))
         .method2845(this.zenithColor7.method7295(themeStyle2.getZenithColor7(), hover))
         .method2846(this.zenithColor8.method7295(themeStyle2.getZenithColor8(), hover))
         .method2847(this.zenithColor9.method7295(themeStyle2.getZenithColor9(), hover))
         .method2848(this.zenithColor10.method7295(themeStyle2.getZenithColor10(), hover))
         .method2849(this.zenithColor11.method7295(themeStyle2.getZenithColor11(), hover))
         .method2850(this.zenithColor12.method7295(themeStyle2.getZenithColor12(), hover))
         .method2851(this.zenithColor13.method7295(themeStyle2.getZenithColor13(), hover))
         .method2852(this.zenithColor14.method7295(themeStyle2.getZenithColor14(), hover))
         .method2853(this.zenithColor15.method7295(themeStyle2.getZenithColor15(), hover))
         .method2854(this.zenithColor16.method7295(themeStyle2.getZenithColor16(), hover))
         .method2855(this.flag)
         .method2856(this.flag2)
         .method2857(this.flag3)
         .method2858(themeStyle2.text, themeStyle2.text2);
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public String getIcon() {
      return this.text2;
   }

   @Generated
   public ThemeStyle.Data getData() {
      return this.data;
   }

   @Generated
   public ZenithColor getZenithColor() {
      return this.zenithColor;
   }

   @Generated
   public ZenithColor getZenithColor2() {
      return this.zenithColor2;
   }

   @Generated
   public ZenithColor getZenithColor3() {
      return this.zenithColor3;
   }

   @Generated
   public ZenithColor getZenithColor4() {
      return this.zenithColor4;
   }

   @Generated
   public ZenithColor getZenithColor5() {
      return this.zenithColor5;
   }

   @Generated
   public ZenithColor getZenithColor6() {
      return this.zenithColor6;
   }

   @Generated
   public ZenithColor getZenithColor7() {
      return this.zenithColor7;
   }

   @Generated
   public ZenithColor getZenithColor8() {
      return this.zenithColor8;
   }

   @Generated
   public ZenithColor getZenithColor9() {
      return this.zenithColor9;
   }

   @Generated
   public ZenithColor getZenithColor10() {
      return this.zenithColor10;
   }

   @Generated
   public ZenithColor getZenithColor11() {
      return this.zenithColor11;
   }

   @Generated
   public ZenithColor getZenithColor12() {
      return this.zenithColor12;
   }

   @Generated
   public ZenithColor getZenithColor13() {
      return this.zenithColor13;
   }

   @Generated
   public ZenithColor getZenithColor14() {
      return this.zenithColor14;
   }

   @Generated
   public ZenithColor getZenithColor15() {
      return this.zenithColor15;
   }

   @Generated
   public ZenithColor getZenithColor16() {
      return this.zenithColor16;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag2;
   }

   @Generated
   public boolean isFlag3() {
      return this.flag3;
   }

   @Generated
   public boolean isFlag4() {
      return this.flag4;
   }

   @Generated
   public void setZenithColor(ZenithColor zenithColor) {
      this.zenithColor = zenithColor;
   }

   @Generated
   public void setZenithColor2(ZenithColor zenithColor2) {
      this.zenithColor2 = zenithColor2;
   }

   @Generated
   public void setZenithColor3(ZenithColor zenithColor3) {
      this.zenithColor3 = zenithColor3;
   }

   @Generated
   public void setZenithColor4(ZenithColor zenithColor4) {
      this.zenithColor4 = zenithColor4;
   }

   @Generated
   public void setZenithColor5(ZenithColor zenithColor5) {
      this.zenithColor5 = zenithColor5;
   }

   @Generated
   public void setZenithColor6(ZenithColor zenithColor6) {
      this.zenithColor6 = zenithColor6;
   }

   @Generated
   public void setZenithColor7(ZenithColor zenithColor7) {
      this.zenithColor7 = zenithColor7;
   }

   @Generated
   public void setZenithColor8(ZenithColor zenithColor8) {
      this.zenithColor8 = zenithColor8;
   }

   @Generated
   public void setZenithColor9(ZenithColor zenithColor9) {
      this.zenithColor9 = zenithColor9;
   }

   @Generated
   public void setZenithColor10(ZenithColor zenithColor10) {
      this.zenithColor10 = zenithColor10;
   }

   @Generated
   public void setZenithColor11(ZenithColor zenithColor11) {
      this.zenithColor11 = zenithColor11;
   }

   @Generated
   public void setZenithColor12(ZenithColor zenithColor12) {
      this.zenithColor12 = zenithColor12;
   }

   @Generated
   public void setZenithColor13(ZenithColor zenithColor13) {
      this.zenithColor13 = zenithColor13;
   }

   @Generated
   public void setZenithColor14(ZenithColor zenithColor14) {
      this.zenithColor14 = zenithColor14;
   }

   @Generated
   public void setZenithColor15(ZenithColor zenithColor15) {
      this.zenithColor15 = zenithColor15;
   }

   @Generated
   public void setZenithColor16(ZenithColor zenithColor16) {
      this.zenithColor16 = zenithColor16;
   }

   @Generated
   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   @Generated
   public void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   @Generated
   public void setFlag3(boolean flag) {
      this.flag3 = flag;
   }

   @Generated
   public void setFlag4(boolean flag) {
      this.flag4 = flag;
   }

   private static final class Data {
      private final ZenithColor zenithColor;
      private final ZenithColor zenithColor2;
      private final ZenithColor zenithColor3;
      private final ZenithColor zenithColor4;
      private final ZenithColor zenithColor5;
      private final ZenithColor zenithColor6;
      private final ZenithColor zenithColor7;
      private final ZenithColor zenithColor8;
      private final ZenithColor zenithColor9;
      private final ZenithColor zenithColor10;
      private final ZenithColor zenithColor11;
      private final ZenithColor zenithColor12;
      private final ZenithColor zenithColor13;
      private final ZenithColor zenithColor14;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      public ZenithColor zenithColor2() {
         return this.zenithColor2;
      }

      public ZenithColor zenithColor3() {
         return this.zenithColor3;
      }

      public ZenithColor zenithColor4() {
         return this.zenithColor4;
      }

      public ZenithColor zenithColor5() {
         return this.zenithColor5;
      }

      public ZenithColor zenithColor6() {
         return this.zenithColor6;
      }

      public ZenithColor zenithColor7() {
         return this.zenithColor7;
      }

      public ZenithColor zenithColor8() {
         return this.zenithColor8;
      }

      public ZenithColor zenithColor9() {
         return this.zenithColor9;
      }

      public ZenithColor zenithColor10() {
         return this.zenithColor10;
      }

      public ZenithColor zenithColor11() {
         return this.zenithColor11;
      }

      public ZenithColor zenithColor12() {
         return this.zenithColor12;
      }

      public ZenithColor zenithColor13() {
         return this.zenithColor13;
      }

      public ZenithColor zenithColor14() {
         return this.zenithColor14;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.zenithColor, other.zenithColor) && java.util.Objects.equals(this.zenithColor2, other.zenithColor2) && java.util.Objects.equals(this.zenithColor3, other.zenithColor3) && java.util.Objects.equals(this.zenithColor4, other.zenithColor4) && java.util.Objects.equals(this.zenithColor5, other.zenithColor5) && java.util.Objects.equals(this.zenithColor6, other.zenithColor6) && java.util.Objects.equals(this.zenithColor7, other.zenithColor7) && java.util.Objects.equals(this.zenithColor8, other.zenithColor8) && java.util.Objects.equals(this.zenithColor9, other.zenithColor9) && java.util.Objects.equals(this.zenithColor10, other.zenithColor10) && java.util.Objects.equals(this.zenithColor11, other.zenithColor11) && java.util.Objects.equals(this.zenithColor12, other.zenithColor12) && java.util.Objects.equals(this.zenithColor13, other.zenithColor13) && java.util.Objects.equals(this.zenithColor14, other.zenithColor14) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(zenithColor, zenithColor2, zenithColor3, zenithColor4, zenithColor5, zenithColor6, zenithColor7, zenithColor8, zenithColor9, zenithColor10, zenithColor11, zenithColor12, zenithColor13, zenithColor14, flag, flag2, flag3);
      }

      @Override
      public String toString() {
         return "Data[" + "zenithColor=" + this.zenithColor + ", " + "zenithColor2=" + this.zenithColor2 + ", " + "zenithColor3=" + this.zenithColor3 + ", " + "zenithColor4=" + this.zenithColor4 + ", " + "zenithColor5=" + this.zenithColor5 + ", " + "zenithColor6=" + this.zenithColor6 + ", " + "zenithColor7=" + this.zenithColor7 + ", " + "zenithColor8=" + this.zenithColor8 + ", " + "zenithColor9=" + this.zenithColor9 + ", " + "zenithColor10=" + this.zenithColor10 + ", " + "zenithColor11=" + this.zenithColor11 + ", " + "zenithColor12=" + this.zenithColor12 + ", " + "zenithColor13=" + this.zenithColor13 + ", " + "zenithColor14=" + this.zenithColor14 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + "]";
      }

      private Data(
         ZenithColor zenithColor,
         ZenithColor zenithColor2,
         ZenithColor zenithColor3,
         ZenithColor zenithColor4,
         ZenithColor zenithColor5,
         ZenithColor zenithColor6,
         ZenithColor zenithColor7,
         ZenithColor zenithColor8,
         ZenithColor zenithColor9,
         ZenithColor zenithColor10,
         ZenithColor zenithColor11,
         ZenithColor zenithColor12,
         ZenithColor zenithColor13,
         ZenithColor zenithColor14,
         boolean flag,
         boolean flag2,
         boolean flag3
      ) {
         this.zenithColor = zenithColor;
         this.zenithColor2 = zenithColor2;
         this.zenithColor3 = zenithColor3;
         this.zenithColor4 = zenithColor4;
         this.zenithColor5 = zenithColor5;
         this.zenithColor6 = zenithColor6;
         this.zenithColor7 = zenithColor7;
         this.zenithColor8 = zenithColor8;
         this.zenithColor9 = zenithColor9;
         this.zenithColor10 = zenithColor10;
         this.zenithColor11 = zenithColor11;
         this.zenithColor12 = zenithColor12;
         this.zenithColor13 = zenithColor13;
         this.zenithColor14 = zenithColor14;
         this.flag = flag;
         this.flag2 = flag2;
         this.flag3 = flag3;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      public ZenithColor getZenithColor2() {
         return this.zenithColor2;
      }

      public ZenithColor getZenithColor3() {
         return this.zenithColor3;
      }

      public ZenithColor getZenithColor4() {
         return this.zenithColor4;
      }

      public ZenithColor getZenithColor5() {
         return this.zenithColor5;
      }

      public ZenithColor getZenithColor6() {
         return this.zenithColor6;
      }

      public ZenithColor getZenithColor7() {
         return this.zenithColor7;
      }

      public ZenithColor getZenithColor8() {
         return this.zenithColor8;
      }

      public ZenithColor getZenithColor9() {
         return this.zenithColor9;
      }

      public ZenithColor getZenithColor10() {
         return this.zenithColor10;
      }

      public ZenithColor getZenithColor11() {
         return this.zenithColor11;
      }

      public ZenithColor getZenithColor12() {
         return this.zenithColor12;
      }

      public ZenithColor getZenithColor13() {
         return this.zenithColor13;
      }

      public ZenithColor getZenithColor14() {
         return this.zenithColor14;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }
   }

   public static class Entry {
      private ZenithColor zenithColor;
      private ZenithColor zenithColor2;
      private ZenithColor zenithColor3;
      private ZenithColor zenithColor4;
      private ZenithColor zenithColor5;
      private ZenithColor zenithColor6;
      private ZenithColor zenithColor7;
      private ZenithColor zenithColor8;
      private ZenithColor zenithColor9;
      private ZenithColor zenithColor10;
      private ZenithColor zenithColor11;
      private ZenithColor zenithColor12;
      private ZenithColor zenithColor13;
      private ZenithColor zenithColor14;
      private ZenithColor zenithColor15;
      private ZenithColor zenithColor16;
      private boolean flag = false;
      private boolean flag2 = false;
      private boolean flag3 = false;

      public static ThemeStyle.Entry method2838() {
         return new ThemeStyle.Entry();
      }

      public ThemeStyle.Entry method2839(ZenithColor zenithColor) {
         this.zenithColor = zenithColor;
         return this;
      }

      public ThemeStyle.Entry method2840(ZenithColor zenithColor2) {
         this.zenithColor2 = zenithColor2;
         return this;
      }

      public ThemeStyle.Entry method2841(ZenithColor zenithColor3) {
         this.zenithColor3 = zenithColor3;
         return this;
      }

      public ThemeStyle.Entry method2842(ZenithColor zenithColor4) {
         this.zenithColor4 = zenithColor4;
         return this;
      }

      public ThemeStyle.Entry method2843(ZenithColor zenithColor5) {
         this.zenithColor5 = zenithColor5;
         return this;
      }

      public ThemeStyle.Entry method2844(ZenithColor zenithColor6) {
         this.zenithColor6 = zenithColor6;
         return this;
      }

      public ThemeStyle.Entry method2845(ZenithColor zenithColor7) {
         this.zenithColor7 = zenithColor7;
         return this;
      }

      public ThemeStyle.Entry method2846(ZenithColor zenithColor8) {
         this.zenithColor8 = zenithColor8;
         return this;
      }

      public ThemeStyle.Entry method2847(ZenithColor zenithColor9) {
         this.zenithColor9 = zenithColor9;
         return this;
      }

      public ThemeStyle.Entry method2848(ZenithColor zenithColor10) {
         this.zenithColor10 = zenithColor10;
         return this;
      }

      public ThemeStyle.Entry method2849(ZenithColor zenithColor11) {
         this.zenithColor11 = zenithColor11;
         return this;
      }

      public ThemeStyle.Entry method2850(ZenithColor zenithColor12) {
         this.zenithColor12 = zenithColor12;
         return this;
      }

      public ThemeStyle.Entry method2851(ZenithColor zenithColor13) {
         this.zenithColor13 = zenithColor13;
         return this;
      }

      public ThemeStyle.Entry method2852(ZenithColor zenithColor14) {
         this.zenithColor14 = zenithColor14;
         return this;
      }

      public ThemeStyle.Entry method2853(ZenithColor zenithColor15) {
         this.zenithColor15 = zenithColor15;
         return this;
      }

      public ThemeStyle.Entry method2854(ZenithColor zenithColor16) {
         this.zenithColor16 = zenithColor16;
         return this;
      }

      public ThemeStyle.Entry method2855(boolean flag) {
         this.flag = flag;
         return this;
      }

      public ThemeStyle.Entry method2856(boolean flag) {
         this.flag2 = flag;
         return this;
      }

      public ThemeStyle.Entry method2857(boolean flag) {
         this.flag3 = flag;
         return this;
      }

      public ThemeStyle method2858(String text, String str) {
         return new ThemeStyle(text, str, this);
      }
   }
}
