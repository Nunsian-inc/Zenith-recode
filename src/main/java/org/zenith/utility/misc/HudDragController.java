package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class HudDragController implements MinecraftAccess {
   private static final float CONST_floatValue = 5.0F;
   private static final float CONST_floatValue2 = 4.0F;
   private static final float CONST_floatValue3 = 32.0F;
   private static final float CONST_floatValue4 = 0.035F;
   private final Map<Interface, HudDragController.Entry2> items = new IdentityHashMap<>();
   private long longValue;
   private final List<HudDragController.Entry> items2 = new ArrayList<>();
   private Interface interfaceValue;
   private Interface interfaceValue2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private long last;

   public boolean method4877(Interface interfaceValue, float floatValue, float f) {
      if (interfaceValue == null) {
         return false;
      } else {
         if (this.interfaceValue2 != null && this.interfaceValue2 != interfaceValue) {
            this.method4879(this.interfaceValue2);
         }

         this.interfaceValue = interfaceValue;
         this.interfaceValue2 = null;
         this.floatValue5 = interfaceValue.method294();
         this.floatValue6 = interfaceValue.method295();
         this.floatValue3 = this.floatValue5;
         this.floatValue4 = this.floatValue6;
         this.floatValue = floatValue - this.floatValue5;
         this.floatValue2 = f - this.floatValue6;
         return true;
      }
   }

   public void end() {
      if (this.interfaceValue != null) {
         this.interfaceValue2 = this.interfaceValue;
         this.interfaceValue = null;
      }
   }

   public void cancel() {
      if (this.interfaceValue != null) {
         this.interfaceValue2 = this.interfaceValue;
         this.interfaceValue = null;
      }

      if (this.interfaceValue2 != null) {
         this.method4879(this.interfaceValue2);
      }
   }

   public Interface getInterfaceValue() {
      return this.interfaceValue;
   }

   public boolean method4878(Interface key) {
      return key != null && (key == this.interfaceValue || key == this.interfaceValue2);
   }

   private void method4879(Interface interfaceValue2) {
      float guiScaledWidth = mc.getWindow().getGuiScaledWidth();
      float guiScaledHeight = mc.getWindow().getGuiScaledHeight();
      interfaceValue2.method2067(this.floatValue3, this.floatValue4, guiScaledWidth, guiScaledHeight);
      interfaceValue2.release();
      interfaceValue2.setFloatValue14(1.0F);
      if (this.interfaceValue2 == interfaceValue2) {
         this.interfaceValue2 = null;
      }
   }

   public void method4880(CustomDrawContext customDrawContext, List<Interface> interfaceValues, float floatValue, float f, float guiScaledWidth, float guiScaledHeight, boolean flag) {
      long nanoTime = System.nanoTime();
      float f2 = this.last == 0L ? 0.016F : Math.min(0.05F, (float)(nanoTime - this.last) / 1.0E9F);
      this.last = nanoTime;
      this.floatValue8 = method4888(this.floatValue8, flag ? 1.0F : 0.0F, f2 * 7.0F);
      this.floatValue7 = method4888(this.floatValue7, this.interfaceValue != null ? 1.0F : 0.0F, f2 * 9.0F);

      for (HudDragController.Entry entry : this.items2) {
         entry.flag3 = false;
      }

      Interface interfaceValue = this.interfaceValue != null ? this.interfaceValue : this.interfaceValue2;
      boolean flag2 = false;
      boolean flag3 = false;
      if (this.interfaceValue != null) {
         float f3 = floatValue - this.floatValue;
         float f4 = f - this.floatValue2;
         boolean keyDown = org.zenith.utility.misc.KeyCompat.isKeyDown(340);
         float f5 = this.interfaceValue.method296();
         float f6 = this.interfaceValue.method297();
         HudDragController.Data data = keyDown ? null : method4886(f3, f5, this.method4884(interfaceValues, guiScaledWidth));
         HudDragController.Data data2 = keyDown ? null : method4886(f4, f6, this.method4885(interfaceValues, guiScaledHeight));
         this.floatValue3 = data != null ? data.getFloatValue() - data.getFloatValue2() * f5 : f3;
         this.floatValue4 = data2 != null ? data2.getFloatValue() - data2.getFloatValue2() * f6 : f4;
         float f7 = InterfaceModule.INSTANCE.method6786();
         if (f7 >= 1.0F) {
            if (data == null) {
               this.floatValue3 = Math.round(this.floatValue3 / f7) * f7;
            }

            if (data2 == null) {
               this.floatValue4 = Math.round(this.floatValue4 / f7) * f7;
            }
         }

         this.floatValue3 = this.interfaceValue.method2088(this.floatValue3, guiScaledWidth);
         this.floatValue4 = this.interfaceValue.method2089(this.floatValue4, guiScaledHeight);
         if (data != null) {
            this.method4887(true, data, this.floatValue4, this.floatValue4 + f6, guiScaledHeight);
            flag2 = data.isFlag();
         }

         if (data2 != null) {
            this.method4887(false, data2, this.floatValue3, this.floatValue3 + f5, guiScaledWidth);
            flag3 = data2.isFlag();
         }
      }

      this.floatValue9 = method4888(this.floatValue9, this.interfaceValue != null ? 1.0F : 0.0F, f2 * 6.0F);
      if (interfaceValue != null) {
         float f3 = 1.0F - (float)Math.exp(-f2 * 32.0F);
         this.floatValue5 = this.floatValue5 + (this.floatValue3 - this.floatValue5) * f3;
         this.floatValue6 = this.floatValue6 + (this.floatValue4 - this.floatValue6) * f3;
         interfaceValue.method2067(this.floatValue5, this.floatValue6, guiScaledWidth, guiScaledHeight);
         interfaceValue.setFloatValue14(1.0F + 0.035F * this.floatValue7);
         if (interfaceValue == this.interfaceValue2
            && Math.abs(this.floatValue3 - this.floatValue5) < 0.15F
            && Math.abs(this.floatValue4 - this.floatValue6) < 0.15F
            && this.floatValue7 < 0.02F) {
            this.method4879(interfaceValue);
         }
      }

      var iterator = this.items2.iterator();

      while (iterator.hasNext()) {
         HudDragController.Entry entry = (HudDragController.Entry)iterator.next();
         entry.alpha = method4888(entry.alpha, entry.flag3 ? 1.0F : 0.0F, f2 * (entry.flag3 ? 14.0F : 6.0F));
         if (!entry.flag3 && entry.alpha <= 0.01F) {
            iterator.remove();
         }
      }

      Interface interfaceValue2 = null;
      if (flag) {
         for (int i = interfaceValues.size() - 1; i >= 0; i--) {
            if (((Interface)interfaceValues.get(i)).method293(floatValue, f)) {
               interfaceValue2 = (Interface)interfaceValues.get(i);
               break;
            }
         }
      }

      Interface interfaceValue3 = interfaceValue != null ? interfaceValue : interfaceValue2;
      long longValue = ++this.longValue;

      for (Interface key : interfaceValues) {
         float f3 = key == interfaceValue3 && flag ? 1.0F : 0.0F;
         HudDragController.Entry2 entry2 = this.items.computeIfAbsent(key, interfaceValue4 -> new HudDragController.Entry2());
         float f4 = method4888(entry2.value, f3, f2 * 12.0F);
         entry2.value = f4;
         entry2.longValue = longValue;
         key.method2066(f4 * Math.max(this.floatValue8, 0.001F), this.method4878(key));
      }

      var iterator2 = this.items.entrySet().iterator();

      while (iterator2.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator2.next();
         if (((HudDragController.Entry2)entry.getValue()).longValue != longValue) {
            ((Interface)entry.getKey()).method2066(0.0F, false);
            iterator2.remove();
         }
      }

      if (!(this.floatValue8 <= 0.01F)) {
         this.method4881(customDrawContext, interfaceValues, guiScaledWidth, guiScaledHeight, interfaceValue2, flag2, flag3);
      }
   }

   private void method4881(CustomDrawContext customDrawContext, List<Interface> interfaceValues, float guiScaledWidth, float guiScaledHeight, Interface interfaceValue, boolean flag, boolean flag2) {
      ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = ZenithColor.INSTANCE;
      if (this.floatValue9 > 0.01F) {
         float f = guiScaledWidth / 2.0F;
         float f2 = guiScaledHeight / 2.0F;
         this.method4883(customDrawContext, true, f, 0.0F, guiScaledHeight, zenithColor2.method7292(0.1F * this.floatValue9 * this.floatValue8), flag);
         this.method4883(customDrawContext, false, f2, 0.0F, guiScaledWidth, zenithColor2.method7292(0.1F * this.floatValue9 * this.floatValue8), flag2);
         customDrawContext.drawRoundedRect(
            f - 2.0F, f2 - 2.0F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), zenithColor2.method7292(0.25F * this.floatValue9 * this.floatValue8)
         );
      }

      for (Interface key : interfaceValues) {
         HudDragController.Entry2 entry2 = this.items.get(key);
         float f = entry2 == null ? 0.0F : entry2.value;
         if (f > 0.02F && !key.method2059() && key == interfaceValue && !this.method4878(key)) {
            float width = 5.0F;
            float f2 = key.method294() + key.method296();
            float f3 = key.method295() + key.method297();
            customDrawContext.drawRoundedRect(
               f2 - width + 1.0F, f3 - width + 1.0F, width, width, ProxyDotRadiusData.method8069(1.5F), zenithColor.method7292(0.85F * f * this.floatValue8)
            );
         }
      }

      for (HudDragController.Entry entry : this.items2) {
         float f = entry.alpha * this.floatValue8;
         if (!(f <= 0.01F)) {
            ZenithColor zenithColor3 = (entry.flag2 ? zenithColor2.method7295(zenithColor, 0.35F) : zenithColor).method7292(0.9F * f);
            if (entry.flag) {
               customDrawContext.drawRect(entry.floatValue - 0.5F, entry.floatValue2, 1.0F, entry.floatValue3 - entry.floatValue2, zenithColor3);
               method4882(customDrawContext, entry.floatValue, entry.floatValue2, zenithColor3);
               method4882(customDrawContext, entry.floatValue, entry.floatValue3, zenithColor3);
            } else {
               customDrawContext.drawRect(entry.floatValue2, entry.floatValue - 0.5F, entry.floatValue3 - entry.floatValue2, 1.0F, zenithColor3);
               method4882(customDrawContext, entry.floatValue2, entry.floatValue, zenithColor3);
               method4882(customDrawContext, entry.floatValue3, entry.floatValue, zenithColor3);
            }
         }
      }
   }

   private static void method4882(CustomDrawContext customDrawContext, float floatValue, float f, ZenithColor zenithColor) {
      customDrawContext.drawRoundedRect(floatValue - 1.5F, f - 1.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithColor);
   }

   private void method4883(CustomDrawContext customDrawContext, boolean flag, float f, float f2, float guiScaledHeight, ZenithColor zenithColor, boolean flag2) {
      if (!flag2) {
         float f3 = 4.0F;
         float f4 = 4.0F;

         for (float y = f2; y < guiScaledHeight; y += f3 + f4) {
            float height = Math.min(f3, guiScaledHeight - y);
            if (flag) {
               customDrawContext.drawRect(f - 0.5F, y, 1.0F, height, zenithColor);
            } else {
               customDrawContext.drawRect(y, f - 0.5F, height, 1.0F, zenithColor);
            }
         }
      }
   }

   private List<HudDragController.Data> method4884(List<Interface> interfaceValues, float guiScaledWidth) {
      ArrayList<HudDragController.Data> datas = new ArrayList<>();
      float floatValue = this.interfaceValue.method2081(guiScaledWidth);
      float f = this.interfaceValue.method2082(guiScaledWidth);
      datas.add(new HudDragController.Data(floatValue, 0.0F, null, false));
      datas.add(new HudDragController.Data(f, 1.0F, null, false));
      datas.add(new HudDragController.Data(guiScaledWidth / 2.0F, 0.5F, null, true));

      for (Interface interfaceValue : interfaceValues) {
         if (interfaceValue != this.interfaceValue) {
            float floatValue2 = interfaceValue.method294();
            float floatValue3 = floatValue2 + interfaceValue.method296();
            float floatValue4 = (floatValue2 + floatValue3) / 2.0F;

            for (float f2 : new float[]{0.0F, 0.5F, 1.0F}) {
               datas.add(new HudDragController.Data(floatValue2, f2, interfaceValue, false));
               datas.add(new HudDragController.Data(floatValue4, f2, interfaceValue, false));
               datas.add(new HudDragController.Data(floatValue3, f2, interfaceValue, false));
            }

            datas.add(new HudDragController.Data(floatValue3 + 4.0F, 0.0F, interfaceValue, false));
            datas.add(new HudDragController.Data(floatValue2 - 4.0F, 1.0F, interfaceValue, false));
         }
      }

      return datas;
   }

   private List<HudDragController.Data> method4885(List<Interface> interfaceValues, float guiScaledHeight) {
      ArrayList<HudDragController.Data> datas = new ArrayList<>();
      float floatValue = this.interfaceValue.method2084(guiScaledHeight);
      float f = this.interfaceValue.method2085(guiScaledHeight);
      datas.add(new HudDragController.Data(floatValue, 0.0F, null, false));
      datas.add(new HudDragController.Data(f, 1.0F, null, false));
      datas.add(new HudDragController.Data(guiScaledHeight / 2.0F, 0.5F, null, true));

      for (Interface interfaceValue : interfaceValues) {
         if (interfaceValue != this.interfaceValue) {
            float floatValue2 = interfaceValue.method295();
            float floatValue3 = floatValue2 + interfaceValue.method297();
            float floatValue4 = (floatValue2 + floatValue3) / 2.0F;

            for (float f2 : new float[]{0.0F, 0.5F, 1.0F}) {
               datas.add(new HudDragController.Data(floatValue2, f2, interfaceValue, false));
               datas.add(new HudDragController.Data(floatValue4, f2, interfaceValue, false));
               datas.add(new HudDragController.Data(floatValue3, f2, interfaceValue, false));
            }

            datas.add(new HudDragController.Data(floatValue3 + 4.0F, 0.0F, interfaceValue, false));
            datas.add(new HudDragController.Data(floatValue2 - 4.0F, 1.0F, interfaceValue, false));
         }
      }

      return datas;
   }

   private static HudDragController.Data method4886(float f, float f2, List<HudDragController.Data> datas) {
      HudDragController.Data data = null;
      float f3 = 5.0F;

      for (HudDragController.Data data2 : datas) {
         float f4 = Math.abs(data2.getFloatValue() - (f + data2.getFloatValue2() * f2));
         float f5 = data2.isFlag() ? 0.5F : 0.0F;
         if (f4 - f5 < f3) {
            f3 = f4 - f5;
            data = data2;
         }
      }

      return data;
   }

   private void method4887(boolean flag, HudDragController.Data data, float f, float f2, float guiScaledHeight) {
      float f3;
      float guiScaledHeight2;
      if (data.getInterfaceValue() != null) {
         Interface interfaceValue = data.getInterfaceValue();
         float f4 = flag ? interfaceValue.method295() : interfaceValue.method294();
         float f5 = f4 + (flag ? interfaceValue.method297() : interfaceValue.method296());
         f3 = Math.min(f, f4) - 3.0F;
         guiScaledHeight2 = Math.max(f2, f5) + 3.0F;
      } else if (data.isFlag()) {
         f3 = 0.0F;
         guiScaledHeight2 = guiScaledHeight;
      } else {
         f3 = f - 6.0F;
         guiScaledHeight2 = f2 + 6.0F;
      }

      for (HudDragController.Entry entry : this.items2) {
         if (entry.flag == flag && Math.abs(entry.floatValue - data.getFloatValue()) < 0.5F) {
            entry.flag3 = true;
            entry.floatValue = data.getFloatValue();
            entry.floatValue2 = f3;
            entry.floatValue3 = guiScaledHeight2;
            return;
         }
      }

      HudDragController.Entry entry = new HudDragController.Entry(flag, data.isFlag(), data.getFloatValue());
      entry.floatValue2 = f3;
      entry.floatValue3 = guiScaledHeight2;
      entry.flag3 = true;
      this.items2.add(entry);
   }

   private static float method4888(float alpha, float f, float f2) {
      return alpha < f ? Math.min(f, alpha + f2) : Math.max(f, alpha - f2);
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final Interface interfaceValue;
      private final boolean flag;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public Interface interfaceValue() {
         return this.interfaceValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.interfaceValue, other.interfaceValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, interfaceValue, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "interfaceValue=" + this.interfaceValue + ", " + "flag=" + this.flag + "]";
      }

      private Data(float floatValue, float f, Interface interfaceValue, boolean flag) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.interfaceValue = interfaceValue;
         this.flag = flag;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public Interface getInterfaceValue() {
         return this.interfaceValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Entry {
      public final boolean flag;
      public final boolean flag2;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float alpha;
      public boolean flag3;

      public Entry(boolean flag, boolean flag2, float floatValue) {
         this.flag = flag;
         this.flag2 = flag2;
         this.floatValue = floatValue;
      }
   }

   private static final class Entry2 {
      public float value;
      public long longValue;
   }
}
