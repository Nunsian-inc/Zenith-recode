package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ModeOptions {
   private static final float MARGIN_TOP = 1.5F;
   private static final float MARGIN_BOTTOM = 2.0F;
   private static final float INSET = 2.0F;
   private static final float ROW = 11.0F;
   private static final float ROW_GAP = 1.0F;
   private static final float ROW_H = 10.0F;
   private static final float DOT = 2.5F;
   private static final float DOT_X = 4.0F;
   private static final float TEXT_X = 5.0F;
   private static final float TEXT_SHIFT = 5.5F;
   private static final float TEXT_RIGHT = 3.0F;
   private static final float MIN_WIDTH = 12.0F;
   private static final float PANEL_ROUND = 3.0F;
   private static final float ROW_ROUND = 2.2F;
   private static final float INDICATOR_SPEED = 22.0F;
   private static final long SNAP_GAP_NANOS = 250000000L;
   private final ModeSetting setting;
   private final Map<ModeSetting.Entry, ModeOptions.Option> options = new IdentityHashMap<>();
   private final List<ModeOptions.Option> layout = new ArrayList<>();
   private float layoutWidth = Float.NaN;
   private float layoutHeight;
   private float indicatorY;
   private boolean indicatorKnown;
   private long lastFrameNanos;

   public ModeOptions(ModeSetting setting) {
      this.setting = setting;
   }

   public void prepare(float layoutWidth) {
      layoutWidth = Float.isFinite(layoutWidth) ? Math.max(0.0F, layoutWidth) : 0.0F;
      var values = this.setting.getValues();
      boolean flag = layoutWidth != this.layoutWidth || this.layout.size() != values.size();

      for (int i = 0; i < values.size(); i++) {
         ModeSetting.Entry key = (ModeSetting.Entry)values.get(i);
         ModeOptions.Option option = this.options.computeIfAbsent(key, entry -> new ModeOptions.Option());
         option.value = key;
         option.name = key.getName();
         if (i >= this.layout.size() || this.layout.get(i) != option) {
            flag = true;
         }
      }

      if (flag) {
         this.clearBounds();
         this.options.keySet().retainAll(values);
         this.layout.clear();
         this.layoutWidth = layoutWidth;
         this.indicatorKnown = false;
         if (!values.isEmpty() && !(layoutWidth <= 4.0F)) {
            for (int i = 0; i < values.size(); i++) {
               ModeOptions.Option option = this.options.get(values.get(i));
               option.y = 3.5F + i * 11.0F;
               this.layout.add(option);
            }

            this.layoutHeight = 5.5F + values.size() * 11.0F - 1.0F + 2.0F;
         } else {
            this.layoutHeight = 0.0F;
         }
      }
   }

   public float height() {
      return this.layoutHeight;
   }

   public float height(float width) {
      this.prepare(width);
      return this.layoutHeight;
   }

   public void clearBounds() {
      for (ModeOptions.Option option : this.options.values()) {
         option.bounds = null;
      }
   }

   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float floatValue, float f, float width, float f2, float f3, boolean flag, ZenithStyle currentStyle) {
      this.prepare(width);
      this.clearBounds();
      if (!(width <= 4.0F) && !this.layout.isEmpty() && !(f2 <= 0.01F) && !(f3 <= 0.01F)) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
         ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
         ZenithColor zenithColor2 = currentStyle.getTextEnable().getZenithColor();
         ZenithColor zenithColor3 = currentStyle.getTextSecondary().getZenithColor();
         ZenithColor zenithColor4 = currentStyle.getFieldBorder().getZenithColor();
         long nanoTime = System.nanoTime();
         float f4 = Mth.clamp(f2 / Math.max(this.layoutHeight, 1.0F), 0.0F, 1.0F);
         boolean mouseInsideClip = isMouseInsideClip(menuDrawContext, lastClickX, lastClickY);
         float y = f + 1.5F;
         float height = this.layoutHeight - 1.5F - 2.0F;
         float previewAlpha = f3 * Mth.clamp(f4 * 1.6F, 0.0F, 1.0F);
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(3.0F);
         menuDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawRoundedBorder(floatValue, y, width, height, 0.35F, headerRadius, zenithColor4.method7294(0.45F * previewAlpha));
         float floatValue2 = floatValue + 2.0F;
         float width2 = width - 4.0F;
         ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(2.2F);
         int n = -1;

         for (int i = 0; i < this.layout.size(); i++) {
            ModeOptions.Option option = this.layout.get(i);
            option.selection.animateTo(option.value.isSelected() ? 1.0F : 0.0F);
            if (option.value.isSelected()) {
               n = i;
            }
         }

         if (n >= 0) {
            this.updateIndicator(this.layout.get(n).y, nanoTime);
            float reveal2 = reveal(f4, n);
            float f5 = f3 * reveal2;
            if (f5 > 0.001F && this.indicatorY < f2) {
               float y2 = f + this.indicatorY + (1.0F - reveal2) * 1.5F;
               menuDrawContext.drawRoundedRect(floatValue2, y2, width2, 10.0F, proxyDotRadiusData, zenithColor.method7292(0.1F * f5));
               menuDrawContext.drawRoundedBorder(floatValue2, y2, width2, 10.0F, 0.35F, proxyDotRadiusData, zenithColor.method7292(0.3F * f5));
            }
         } else {
            this.indicatorKnown = false;
         }

         this.lastFrameNanos = nanoTime;

         for (int i = 0; i < this.layout.size(); i++) {
            ModeOptions.Option option = this.layout.get(i);
            if (option.y >= f2) {
               break;
            }

            float reveal2 = reveal(f4, i);
            float previewAlpha2 = f3 * reveal2;
            if (!(previewAlpha2 <= 0.001F)) {
               float y2 = f + option.y + (1.0F - reveal2) * 1.5F;
               if (flag && reveal2 >= 0.92F && width2 >= 12.0F && option.y + 11.0F <= f2 + 0.01F) {
                  option.bounds = new BoundsData(floatValue2, y2, width2, 10.0F);
               }

               boolean contains = mouseInsideClip && option.bounds != null && option.bounds.contains(lastClickX, lastClickY);
               float f5 = option.hover.animateTo(contains ? 1.0F : 0.0F);
               float f6 = option.selection.getValue();
               float f7 = f5 * (1.0F - f6);
               if (f7 > 0.01F) {
                  menuDrawContext.drawRoundedRect(floatValue2, y2, width2, 10.0F, proxyDotRadiusData, zenithColor2.method7292(0.04F * f7 * previewAlpha2));
               }

               float width3 = 2.5F * f6;
               if (width3 > 0.05F) {
                  menuDrawContext.drawRoundedRect(
                     floatValue2 + 4.0F + (2.5F - width3) / 2.0F,
                     y2 + (10.0F - width3) / 2.0F,
                     width3,
                     width3,
                     ProxyDotRadiusData.method8069(width3 / 2.0F),
                     zenithColor.method7294(previewAlpha2)
                  );
               }

               float floatValue3 = floatValue2 + 5.0F + 5.5F * f6;
               float labelLimit = Math.max(0.0F, floatValue2 + width2 - 3.0F - floatValue3);
               ZenithColor zenithColor5 = zenithColor3.method7295(zenithColor2, Math.max(f6, f5 * 0.7F));
               option.label.render(menuDrawContext, font, option.name, floatValue3, y2 + (10.0F - font.height()) / 2.0F, labelLimit, zenithColor5.method7294(previewAlpha2), contains);
               if (contains) {
                  String description = option.value.getDescription();
                  HoverInfo.request(option.name, description.isEmpty() ? this.setting.getDescription() : Zenith.getInstance().getLocalization().translate(description));
               }
            }
         }
      }
   }

   private static float reveal(float f, int n) {
      float f2 = Math.min(n * 0.03F, 0.2F);
      float f3 = Mth.clamp((f - f2) / (1.0F - f2), 0.0F, 1.0F);
      return f3 * f3 * (3.0F - 2.0F * f3);
   }

   private void updateIndicator(float indicatorY, long nanoTime) {
      long l = nanoTime - this.lastFrameNanos;
      if (this.indicatorKnown && l >= 0L && l <= 250000000L) {
         float f = 1.0F - (float)Math.exp(-22.0F * (float)l / 1.0E9);
         this.indicatorY = this.indicatorY + (indicatorY - this.indicatorY) * f;
      } else {
         this.indicatorY = indicatorY;
         this.indicatorKnown = true;
      }
   }

   private static boolean isMouseInsideClip(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY) {
      ScreenRectangle screenRectangle = menuDrawContext.scissorStack.peek();
      if (screenRectangle == null) {
         return true;
      } else {
         Matrix3x2fStack pose = menuDrawContext.pose();
         float f = pose.m00() * lastClickX + pose.m10() * lastClickY + pose.m20();
         float f2 = pose.m01() * lastClickX + pose.m11() * lastClickY + pose.m21();
         return f >= screenRectangle.left() && f < screenRectangle.right() && f2 >= screenRectangle.top() && f2 < screenRectangle.bottom();
      }
   }

   public boolean onMouseClicked(double x, double y, MouseButton enum2) {
      if (this.setting.isVisible() && enum2 == MouseButton.LEFT) {
         for (ModeSetting.Entry entry : this.setting.getValues()) {
            ModeOptions.Option option = this.options.get(entry);
            if (option != null && option.bounds != null && option.bounds.contains(x, y)) {
               if (!entry.isSelected()) {
                  entry.method2103();
                  Zenith.getInstance().getClientSounds().method1570(false);
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static final class Option {
      public final Animation hover = new Animation(110L, 0.0F, Easing.INSTANCE12);
      public final Animation selection = new Animation(180L, 0.0F, Easing.INSTANCE15);
      public final HoverScrollText label = new HoverScrollText();
      public ModeSetting.Entry value;
      public String name;
      public float y;
      public BoundsData bounds;
   }
}
