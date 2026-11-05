package org.zenith.client.screens.nlgui.elements;

import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.SettingIcons;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.misc.ModuleHeaderSetting;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class InspectorRow implements MouseBindTarget {
   private static final float INSET = 8.0F;
   private static final float INDENT = 8.0F;
   private static final float VALUE_COLUMN = 30.0F;
   private static final float TRACK_WIDTH = 34.0F;
   private static final float TRACK_MIN = 18.0F;
   private static final float SWATCH = 7.0F;
   private static final float SWATCH_FILL_INSET = 0.5F;
   private static final float ICON_COLUMN = 9.0F;
   private static final float SWITCH_WIDTH = 9.0F;
   private static final float SWITCH_HEIGHT = 5.0F;
   private static final float CHEVRON_SIZE = 5.0F;
   private static final float CHEVRON_COLUMN = 7.0F;
   private static final float TRACK_GRAB = 4.0F;
   private static final float HANDLE_GRAB = 5.0F;
   private final Setting setting;
   private final int depth;
   private final List<InspectorRow> children = new ArrayList<>();
   private final Animation expandAnimation = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final Animation visibleAnimation;
   private final Animation toggleAnimation;
   private final RollingText valueRoll = new RollingText();
   private final RollingText hexRoll = new RollingText();
   private long lastFrameAt;
   private float frameDt;
   private final Animation hoverAnimation = new Animation(120L, 0.0F, Easing.INSTANCE12);
   private HoverScrollText labelScroll;
   private float springPos = Float.NaN;
   private float springVel;
   private float lastNumber = Float.NaN;
   private int numberDirection = 1;
   private float numberTextValue = Float.NaN;
   private SliderSetting.Handler numberTextDisplay;
   private String numberText;
   private int hexKey = -1;
   private String hexText;
   private final Animation dragAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private long edgeHitAt = -1L;
   private int edgeSide;
   private boolean edgePinned;
   private long flashAt = -1L;
   private long shakeAt = -1L;
   private long editStartAt = -1L;
   private long typedAt = -1L;
   private ZenithColor shownColor;
   private long pickerToggledAt = -1L;
   private static final int KEY_SET = 1;
   private static final int KEY_CLEAR = 2;
   private static final long KEY_ANIM_MS = 720L;
   private int keyAnim;
   private long keyAnimAt;
   private String keyFrom;
   private String keyBeforeBinding;
   private final Animation keyCapWidth = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private boolean keyCapKnown;
   private final Animation keyHover = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private static final long FLASH_MS = 520L;
   private static final long SHAKE_MS = 360L;
   private static final long EDGE_MS = 380L;
   private float trackLeftPx = Float.NaN;
   private float trackSpanPx = 34.0F;
   private float sharedTrackSpan = Float.NaN;
   private float valueLeftPx = Float.NaN;
   private boolean expanded;
   private boolean dragging;
   private float dragOffset;
   private boolean binding;
   private final BindSetting bindSetting;
   private final BindLabel bindLabel = new BindLabel();
   private boolean editing;
   private StringBuilder editBuffer;
   private String editOriginal = "";
   private final SvgIcon subjectGlyph;
   private final ColorPicker picker;
   private final ModeOptions modeOptions;
   private boolean modeHoverAllowed = true;
   private float pickerHeight;
   private BoundsData bounds;
   private BoundsData valueBounds;
   private String lastCounter;
   private static final int LETTERS_RISE = 0;
   private static final int LETTERS_DROP = 1;
   private static final int LETTERS_CRUMBLE = 2;

   public InspectorRow(Setting newNameSetting, int depth) {
      this(newNameSetting, depth, "");
   }

   public InspectorRow(Setting newNameSetting, int depth, String simpleName) {
      this(newNameSetting, depth, "", simpleName);
   }

   private InspectorRow(Setting setting, int depth, String key, String simpleName) {
      this.setting = setting;
      this.bindSetting = (BindSetting)(setting instanceof ModuleHeaderSetting moduleHeaderSetting
         ? moduleHeaderSetting.getHeaderBindSetting()
         : (setting instanceof BindSetting bindSetting ? bindSetting : null));
      this.depth = depth;
      this.subjectGlyph = SettingIcons.resolve(setting, key, simpleName);
      this.picker = setting instanceof ColorSetting colorSetting ? new ColorPicker(colorSetting) : null;
      this.modeOptions = setting instanceof ModeSetting modeSetting ? new ModeOptions(modeSetting) : null;
      this.pickerHeight = this.picker == null ? 0.0F : this.picker.height(this.pickerWidth(144.0F));
      this.visibleAnimation = new Animation(160L, setting != null && setting.isVisible() ? 1.0F : 0.0F, Easing.INSTANCE13);
      this.toggleAnimation = new Animation(220L, setting instanceof BooleanSetting booleanSetting && booleanSetting.isEnabled() ? 1.0F : 0.0F, Easing.INSTANCE10);
      if (setting instanceof GroupSetting groupSetting) {
         for (Setting setting2 : groupSetting.getSettings()) {
            this.children.add(new InspectorRow(setting2, depth + 1, setting.getKey(), simpleName));
         }
      } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
         for (MultiSelectSetting.Entry entry : multiSelectSetting.getItems2()) {
            this.children.add(new InspectorRow(new InspectorRow.MultiValueAdapter(multiSelectSetting, entry), depth + 1, setting.getKey(), simpleName));
         }
      }
   }

   public boolean isVisible() {
      return this.visibleAnimation.getValue() > 0.001F || this.setting.isVisible();
   }

   public static void alignTracks(List<InspectorRow> settingRows, float width) {
      float sharedTrackSpan = 34.0F;

      for (InspectorRow settingRow : settingRows) {
         settingRow.prepareLayout(width);
         sharedTrackSpan = Math.min(sharedTrackSpan, settingRow.narrowestTrack(width));
      }

      for (InspectorRow settingRow : settingRows) {
         settingRow.shareTrack(sharedTrackSpan);
      }
   }

   private void prepareLayout(float width) {
      if (this.modeOptions != null) {
         this.modeOptions.prepare(Math.max(0.0F, width - 16.0F - this.depth * 8.0F));
      }

      for (InspectorRow inspectorRow : this.children) {
         inspectorRow.prepareLayout(width);
      }
   }

   private float narrowestTrack(float width) {
      float f = 34.0F;
      if (this.setting instanceof SliderSetting && this.setting.isVisible()) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
         float f2 = this.labelOffset() + font.width(this.label());
         f = Mth.clamp(width - 8.0F - 30.0F - f2 - GuiStyle.PADDING.intValue(), 18.0F, 34.0F);
      }

      for (InspectorRow inspectorRow : this.children) {
         f = Math.min(f, inspectorRow.narrowestTrack(width));
      }

      return f;
   }

   private void shareTrack(float sharedTrackSpan) {
      this.sharedTrackSpan = sharedTrackSpan;

      for (InspectorRow inspectorRow : this.children) {
         inspectorRow.shareTrack(sharedTrackSpan);
      }
   }

   private float labelOffset() {
      float f = 8.0F + this.depth * 8.0F;
      if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
         f += 9.0F;
      }

      return f;
   }

   private boolean expandable() {
      return !this.children.isEmpty()
         || this.picker != null
         || this.modeOptions != null;
   }

   private float ownHeight() {
      return 16.0F;
   }

   private float pickerWidth(float width) {
      return Math.max(40.0F, width - 16.0F - this.depth * 8.0F);
   }

   public float height() {
      float f = this.ownHeight() * this.visibleAnimation.getValue();
      if (this.modeOptions != null) {
         return f + this.modeOptions.height() * this.expandAnimation.getValue() * this.visibleAnimation.getValue();
      } else if (this.picker != null) {
         return f + this.pickerHeight * this.expandAnimation.getValue() * this.visibleAnimation.getValue();
      } else if (this.children.isEmpty()) {
         return f;
      } else {
         float f2 = 0.0F;

         for (InspectorRow inspectorRow : this.children) {
            if (inspectorRow.isVisible()) {
               f2 += inspectorRow.height();
            }
         }

         return f + f2 * this.expandAnimation.getValue();
      }
   }

   public void render(MenuDrawContext menuDrawContext, float mouseX, float mouseY, float floatValue, float y, float width, float previewAlpha) {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      if (this.modeOptions != null) {
         this.modeOptions.clearBounds();
      }

      this.visibleAnimation.method2808(this.setting.isVisible() ? 1.0F : 0.0F);
      float f;
      if (!((f = this.visibleAnimation.getValue()) <= 0.001F)) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            long now = System.currentTimeMillis();
            this.frameDt = this.lastFrameAt == 0L ? 0.0F : Math.min(0.05F, (float)(now - this.lastFrameAt) / 1000.0F);
            this.lastFrameAt = now;
            float previewAlpha2 = previewAlpha * f;
            float height = this.ownHeight() * f;
            this.bounds = new BoundsData(floatValue, y, width, height);
            boolean contains = this.bounds.contains(mouseX, mouseY);
            float f2;
            if ((f2 = this.hoverAnimation.method2808(contains ? 1.0F : 0.0F)) > 0.01F) {
               menuDrawContext.drawRect(floatValue, y, width, height, currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(2.0F).method7294(previewAlpha2 * f2));
            }

            if (contains) {
               HoverInfo.request(this.label(), this.setting.getDescription());
            }

            this.renderFlash(menuDrawContext, floatValue, y, width, height, now, previewAlpha2, currentStyle);
            float floatValue2;
            float floatValue3 = (floatValue2 = floatValue + (1.0F - f) * 8.0F) + 8.0F + this.depth * 8.0F;
            Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
            float centeredTextY = y + (height - font.height()) / 2.0F;
            ZenithColor zenithColor = currentStyle.getTextSecondary()
               .getZenithColor()
               .method7295(currentStyle.getTextEnable().getZenithColor(), contains ? 0.6F : 0.0F)
               .method7294(previewAlpha2);
            if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
               this.subjectGlyph.draw(menuDrawContext, floatValue3, y + (height - 6.0F) / 2.0F, 6.0F, zenithColor);
            }

            float floatValue4 = floatValue2 + this.labelOffset();
            String label = this.label();
            float labelLimit = this.labelLimit(floatValue2, width, floatValue4);
            if (this.labelScroll == null) {
               this.labelScroll = new HoverScrollText();
            }

            float render = this.labelScroll.render(menuDrawContext, font, label, floatValue4, centeredTextY, labelLimit, zenithColor, contains);
            float f3 = floatValue4 + render;
            this.valueLeftPx = Float.NaN;
            this.renderValue(menuDrawContext, mouseX, mouseY, floatValue2, y, width, height, previewAlpha2, currentStyle, this.expandable() ? f3 + 7.0F : f3);
            if (this.expandable() && !Float.isNaN(this.valueLeftPx)) {
               MenuGlyph.drawChevron(menuDrawContext, this.valueLeftPx - GuiStyle.PADDING.intValue() - 5.0F, y + height / 2.0F, this.expandAnimation.getValue(), zenithColor);
            }

            float f4;
            if (!((f4 = this.expandAnimation.method2808(this.expanded ? 1.0F : 0.0F)) <= 0.001F)) {
               if (this.modeOptions != null) {
                  float width2 = Math.max(0.0F, width - 16.0F - this.depth * 8.0F);
                  float y2 = y + height;
                  float f5 = this.modeOptions.height(width2) * f4 * f;
                  menuDrawContext.enableScissor(floatValue2, y2, floatValue2 + width, y2 + f5);

                  try {
                     this.modeOptions
                        .render(
                           menuDrawContext,
                           this.modeHoverAllowed ? mouseX : Float.NaN,
                           this.modeHoverAllowed ? mouseY : Float.NaN,
                           floatValue2 + 8.0F + this.depth * 8.0F,
                           y2,
                           width2,
                           f5,
                           previewAlpha2 * f4,
                           this.expanded && f > 0.99F && previewAlpha > 0.99F,
                           currentStyle
                        );
                  } finally {
                     menuDrawContext.disableScissor();
                  }
               } else if (this.picker != null) {
                  float pickerWidth = this.pickerWidth(width);
                  this.pickerHeight = this.picker.height(pickerWidth);
                  float f5 = this.pickerHeight * f4 * f;
                  float y2 = y + height;
                  menuDrawContext.enableScissor(floatValue2, y2, floatValue2 + width, y2 + f5);
                  this.picker.render(menuDrawContext, mouseX, mouseY, floatValue2 + 8.0F + this.depth * 8.0F, y2, pickerWidth, previewAlpha2);
                  menuDrawContext.disableScissor();
               } else if (!this.children.isEmpty()) {
                  float f5 = 0.0F;

                  for (InspectorRow inspectorRow : this.children) {
                     if (inspectorRow.isVisible()) {
                        f5 += inspectorRow.height();
                     }
                  }

                  float f6 = f5 * f4;
                  float y2 = y + height;
                  menuDrawContext.enableScissor(floatValue2, y2, floatValue2 + width, y2 + f6);
                  boolean flag = mouseX >= floatValue2 && mouseX <= floatValue2 + width && mouseY >= y2 && mouseY <= y2 + f6;
                  float y3 = y2;
                  int n = 0;

                  for (InspectorRow inspectorRow : this.children) {
                     if (inspectorRow.isVisible()) {
                        float f7 = Mth.clamp(f4 * 1.5F - n * 0.07F, 0.0F, 1.0F);
                        float f8 = 1.0F - (1.0F - f7) * (1.0F - f7);
                        inspectorRow.modeHoverAllowed = this.modeHoverAllowed && flag;
                        inspectorRow.render(menuDrawContext, mouseX, mouseY, floatValue2 + (1.0F - f8) * 7.0F, y3, width, previewAlpha2 * f8);
                        y3 += inspectorRow.height();
                        n++;
                     }
                  }

                  menuDrawContext.disableScissor();
               }
            }
         }
      }
   }

   private void renderModeValue(
      MenuDrawContext menuDrawContext, ModeSetting modeSetting, float f, float centeredTextY, float y, float height, Font font, float previewAlpha, ZenithStyle currentStyle, float f2
   ) {
      String name = this.expanded ? String.valueOf(modeSetting.getValues().size()) : (modeSetting.getValue2() == null ? "—" : modeSetting.getValue2().getName());
      String current = fit(font, name, Math.max(0.0F, f2));
      float draw = this.valueRoll
         .draw(
            menuDrawContext,
            font,
            current,
            f + this.shakeOffset(),
            centeredTextY,
            (this.expanded ? currentStyle.getTextTertiary().getZenithColor() : currentStyle.getTextEnable().getZenithColor()).method7294(previewAlpha),
            1
         );
      this.valueLeftPx = f - draw;
      this.valueBounds = new BoundsData(this.valueLeftPx, y, draw + 8.0F, height);
   }

   private void renderValue(
      MenuDrawContext menuDrawContext, float mouseX, float mouseY, float f, float y, float width, float height, float previewAlpha, ZenithStyle currentStyle, float f2
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
      float f3;
      float f4 = (f3 = f + width - 8.0F) - f2 - GuiStyle.PADDING.intValue();
      float centeredTextY = y + (height - font.height()) / 2.0F;
      if (this.setting instanceof SliderSetting sliderSetting) {
         float lastNumber = sliderSetting.getValue3();
         if (!Float.isNaN(this.lastNumber) && lastNumber != this.lastNumber) {
            this.numberDirection = lastNumber > this.lastNumber ? 1 : -1;
         }

         this.lastNumber = lastNumber;
         this.renderTrack(menuDrawContext, mouseX, mouseY, y, height, previewAlpha, this.springNumber(sliderSetting), this.numberText(sliderSetting, lastNumber), font, centeredTextY, f3, f2, currentStyle);
         this.valueBounds = this.trackBounds(y, height);
      } else if (this.setting instanceof ColorSetting colorSetting) {
         boolean expanded = this.expanded;
         ZenithColor zenithColor = colorSetting.getZenithColor();
         this.shownColor = this.shownColor == null ? zenithColor : this.shownColor.method7295(zenithColor, 1.0F - (float)Math.exp(-this.frameDt * 18.0F));
         int hexKey = zenithColor.getIntValue() << 16 | zenithColor.getIntValue2() << 8 | zenithColor.getIntValue3();
         if (this.hexText == null || hexKey != this.hexKey) {
            this.hexKey = hexKey;
            this.hexText = String.format("%02X%02X%02X", zenithColor.getIntValue(), zenithColor.getIntValue2(), zenithColor.getIntValue3());
         }

         String hexText = this.hexText;
         float draw = this.hexRoll.draw(menuDrawContext, font, hexText, f3, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha), 1);
         float hover = this.expandAnimation.getValue();
         float width2 = 7.0F + hover;
         float valueLeftPx = f3 - draw - GuiStyle.PADDING.intValue() - width2;
         float y2 = y + (height - width2) / 2.0F;
         float f5;
         if ((f5 = this.pickerToggledAt < 0L ? 1.0F : Mth.clamp((float)(System.currentTimeMillis() - this.pickerToggledAt) / 420.0F, 0.0F, 1.0F))
               < 1.0F
            && expanded) {
            float f6 = f5 * 4.0F;
            menuDrawContext.drawRoundedBorder(
               valueLeftPx - f6,
               y2 - f6,
               width2 + f6 * 2.0F,
               width2 + f6 * 2.0F,
               0.5F,
               ProxyDotRadiusData.method8069(f6 * 0.6F),
               this.shownColor.method7292(1.0F).method7294((1.0F - f5) * (1.0F - f5) * previewAlpha)
            );
         }

         menuDrawContext.drawRect(valueLeftPx + 0.5F, y2 + 0.5F, width2 - 1.0F, width2 - 1.0F, this.shownColor.method7294(previewAlpha));
         menuDrawContext.drawRoundedBorder(
            valueLeftPx,
            y2,
            width2,
            width2,
            0.1F,
            ProxyDotRadiusData.INSTANCE,
            currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 3.0F).method7295(currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha), hover)
         );
         this.valueBounds = new BoundsData(valueLeftPx, y, f3 - valueLeftPx, height);
         this.valueLeftPx = valueLeftPx;
      } else if (this.setting instanceof BooleanSetting booleanSetting) {
         String valueText = this.valueText();
         float draw = this.valueRoll
            .draw(
               menuDrawContext,
               font,
               valueText,
               f3,
               centeredTextY,
               booleanSetting.isEnabled() ? currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha) : currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha),
               booleanSetting.isEnabled() ? 1 : -1
            );
         float floatValue = f3 - draw - GuiStyle.PADDING.intValue() - 9.0F;
         this.renderSwitch(menuDrawContext, floatValue, y + (height - 5.0F) / 2.0F, this.toggleAnimation.method2808(booleanSetting.isEnabled() ? 1.0F : 0.0F), previewAlpha, currentStyle);
         this.valueBounds = new BoundsData(floatValue, y, f3 - floatValue, height);
      } else if (this.bindSetting == null) {
         if (this.setting instanceof ModeSetting modeSetting) {
            this.renderModeValue(menuDrawContext, modeSetting, f3, centeredTextY, y, height, font, previewAlpha, currentStyle, f4);
         } else if (this.editing && this.setting instanceof TextSetting) {
            this.renderEditing(menuDrawContext, font, f3, centeredTextY, y, height, previewAlpha, currentStyle, f4);
         } else {
            ZenithColor zenithColor = this.valueStrong() ? currentStyle.getTextEnable().getZenithColor() : currentStyle.getTextTertiary().getZenithColor();
            float flashProgress;
            if ((flashProgress = this.flashProgress()) < 1.0F) {
               zenithColor = zenithColor.method7295(currentStyle.getPrimaryColor().getZenithColor(), (1.0F - flashProgress) * (1.0F - flashProgress));
            }

            float f5 = f3 + this.shakeOffset();
            if (this.setting instanceof ButtonSetting) {
               f5 += 1.5F * this.hoverAnimation.getValue();
            }

            String current = fit(font, this.valueText(), f4);
            float draw = this.valueRoll.draw(menuDrawContext, font, current, f5, centeredTextY, zenithColor.method7294(previewAlpha), this.counterDirection(current));
            this.valueBounds = new BoundsData(f3 - Math.max(draw, 30.0F), y, Math.max(draw, 30.0F) + 8.0F, height);
            this.valueLeftPx = f3 - draw;
         }
      } else {
         boolean flag = this.bounds != null && this.bounds.contains(mouseX, mouseY);
         ScreenRectangle screenRectangle = menuDrawContext.scissorStack.peek();
         if (screenRectangle != null) {
            Matrix3x2fStack pose = menuDrawContext.pose();
            float f5 = pose.m00() * mouseX + pose.m10() * mouseY + pose.m20();
            float f6 = pose.m01() * mouseX + pose.m11() * mouseY + pose.m21();
            flag &= f5 >= screenRectangle.left() && f5 < screenRectangle.right() && f6 >= screenRectangle.top() && f6 < screenRectangle.bottom();
         }

         String text = BindLabel.text(this.binding, this.bindSetting.getKeyCode(), this.bindSetting.getModifiers());
         if (text == null && this.setting instanceof ModuleHeaderSetting) {
            text = "+";
         }

         this.valueBounds = this.bindLabel
            .render(
               menuDrawContext,
               flag ? mouseX : Float.NaN,
               flag ? mouseY : Float.NaN,
               y,
               height,
               f3,
               text,
               this.binding,
               font,
               f2 + GuiStyle.PADDING.intValue(),
               flag,
               previewAlpha,
               null,
               currentStyle
            );
         if (flag && this.setting instanceof ModuleHeaderSetting && this.valueBounds != null && this.valueBounds.contains(mouseX, mouseY)) {
            HoverInfo.request(this.bindSetting.getName(), this.bindSetting.getDescription());
         }
      }
   }

   private int counterDirection(String lastCounter) {
      byte b = 1;
      if (this.lastCounter != null && !this.lastCounter.equals(lastCounter)) {
         int n = leadingNumber(this.lastCounter);
         int leadingNumber2 = leadingNumber(lastCounter);
         if (n >= 0 && leadingNumber2 >= 0 && leadingNumber2 < n) {
            b = -1;
         }
      }

      this.lastCounter = lastCounter;
      return b;
   }

   private static int leadingNumber(String lastCounter) {
      int index = 0;

      while (index < lastCounter.length() && Character.isDigit(lastCounter.charAt(index))) {
         index++;
      }

      return index != 0 && index <= 9 ? Integer.parseInt(lastCounter.substring(0, index)) : -1;
   }

   private void renderEditing(MenuDrawContext menuDrawContext, Font font, float f, float centeredTextY, float y, float height, float previewAlpha, ZenithStyle currentStyle, float f2) {
      long now = System.currentTimeMillis();
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
      float f3 = 1.5F;
      String string = this.editBuffer.toString();
      String text = string;

      while (!text.isEmpty() && font.width(text) > f2 - f3 - 2.0F) {
         text = text.substring(1);
      }

      float f4 = f - f3 - 1.0F;
      float width = font.width(text);
      float floatValue = f4 - width;
      float f5;
      if ((f5 = this.typedAt < 0L ? 1.0F : Mth.clamp((float)(now - this.typedAt) / 160.0F, 0.0F, 1.0F)) < 1.0F && !text.isEmpty()) {
         String str = text.substring(0, text.length() - 1);
         String text2 = text.substring(text.length() - 1);
         menuDrawContext.drawText(font, str, floatValue, centeredTextY, zenithColor2);
         float previewAlpha2 = 1.0F - (1.0F - f5) * (1.0F - f5);
         menuDrawContext.drawText(
            font, text2, floatValue + font.width(str), centeredTextY + (1.0F - previewAlpha2) * 2.5F, zenithColor2.method7295(zenithColor.method7294(previewAlpha), 1.0F - previewAlpha2).method7294(previewAlpha2)
         );
      } else {
         menuDrawContext.drawText(font, text, floatValue, centeredTextY, zenithColor2);
      }

      float f6 = 0.35F + 0.65F * (0.5F + 0.5F * Mth.cos((float)now / 170.0F));
      menuDrawContext.drawRect(f4 + f3 - 0.5F, centeredTextY - 0.5F, 0.6F, font.height() + 1.0F, zenithColor.method7294(f6 * previewAlpha));
      float f7 = this.editStartAt < 0L ? 1.0F : Mth.clamp((float)(now - this.editStartAt) / 240.0F, 0.0F, 1.0F);
      float width2 = Math.max(width + f3 + 2.0F, 14.0F) * (1.0F - (1.0F - f7) * (1.0F - f7));
      menuDrawContext.drawRect(f - width2, centeredTextY + font.height() + 1.0F, width2, 0.5F, zenithColor.method7294(0.7F * previewAlpha));
      this.valueBounds = new BoundsData(f - Math.max(width, 30.0F), y, Math.max(width, 30.0F) + 8.0F, height);
   }

   private void renderKeyCap(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, Font font, float f, float y, float height, float previewAlpha, ZenithStyle zenithStyle) {
      long now = System.currentTimeMillis();
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = zenithStyle.getTextEnable().getZenithColor();
      String valueText = this.binding ? null : this.valueText();
      boolean flag = valueText != null && !"-".equals(valueText);
      float floatValue = this.binding ? 17.0F : Math.max(font.width(valueText) + 6.0F, 10.0F);
      if (!this.keyCapKnown) {
         this.keyCapKnown = true;
         this.keyCapWidth.method2809(floatValue);
      }

      float width = this.keyCapWidth.method2808(floatValue);
      float height2 = 8.0F;
      float f2 = 1.1F;
      float floatValue2 = f - width + this.shakeOffset();
      float f3 = y + (height - height2 - f2) / 2.0F;
      BoundsData boundsData = new BoundsData(floatValue2, y, width, height);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float f4 = this.keyHover.method2808(contains && !this.binding ? 1.0F : 0.0F);
      long l;
      if ((l = this.keyAnim == 0 ? Long.MAX_VALUE : now - this.keyAnimAt) >= 720L) {
         this.keyAnim = 0;
         this.keyFrom = null;
      }

      float f5 = 0.0F;
      if (this.keyAnim == 1 && l < 320L) {
         float f6;
         f5 = (f6 = (float)l / 320.0F) < 0.3F ? f6 / 0.3F : 1.0F - (f6 - 0.3F) / 0.7F;
      }

      float y2 = f3 - f4 * 0.6F + f5 * f2;
      if (this.binding) {
         float f6;
         float f7 = (f6 = (float)(now % 950L) / 950.0F) * 4.0F;
         menuDrawContext.drawRoundedBorder(
            floatValue2 - f7,
            y2 - f7,
            width + f7 * 2.0F,
            height2 + f7 * 2.0F,
            0.5F,
            ProxyDotRadiusData.method8069(1.5F + f7),
            zenithColor.method7294((1.0F - f6) * (1.0F - f6) * 0.6F * previewAlpha)
         );
      }

      if (this.keyAnim == 1 && l < 480L) {
         float f6 = (float)l / 480.0F;
         float f7 = (1.0F - (1.0F - f6) * (1.0F - f6)) * 5.0F;
         menuDrawContext.drawRoundedBorder(
            floatValue2 - f7,
            y2 - f7,
            width + f7 * 2.0F,
            height2 + f7 * 2.0F,
            0.6F,
            ProxyDotRadiusData.method8069(1.5F + f7),
            zenithColor.method7295(ZenithColor.INSTANCE, 0.3F).method7294((1.0F - f6) * (1.0F - f6) * previewAlpha)
         );
      }

      menuDrawContext.drawRoundedRect(
         floatValue2, f3 + f2, width, height2, ProxyDotRadiusData.method8069(1.5F), ZenithColor.INSTANCE2.method7294((0.35F + 0.15F * f4) * previewAlpha)
      );
      ZenithColor zenithColor3 = zenithStyle.getFieldSurfaceBackground().getZenithColor().method7294(2.4F).method7295(zenithColor2, 0.06F * f4);
      ZenithColor zenithColor4 = zenithStyle.getFieldBorder().getZenithColor().method7294(4.0F).method7295(zenithColor2.method7294(0.5F), 0.4F * f4);
      if (this.binding) {
         float f6 = 0.5F + 0.5F * Mth.sin((float)now / 170.0F);
         zenithColor3 = zenithColor3.method7295(zenithColor.method7294(0.35F), 0.5F + 0.25F * f6);
         zenithColor4 = zenithColor.method7294(0.5F + 0.5F * f6);
      }

      if (this.keyAnim == 1 && l < 520L) {
         float hover = 1.0F - (float)l / 520.0F;
         zenithColor3 = zenithColor3.method7295(zenithColor.method7295(ZenithColor.INSTANCE, 0.35F), 0.6F * hover * hover);
         zenithColor4 = zenithColor4.method7295(zenithColor, hover);
      }

      menuDrawContext.drawRoundedRect(floatValue2, y2, width, height2, ProxyDotRadiusData.method8069(1.5F), zenithColor3.method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(floatValue2, y2, width, height2, 0.1F, ProxyDotRadiusData.method8069(1.5F), zenithColor4.method7294(previewAlpha));
      float f6 = floatValue2 + width / 2.0F;
      float f7 = y2 + (height2 - font.height()) / 2.0F;
      if (this.binding) {
         for (int i = 0; i < 3; i++) {
            float f8 = Math.max(0.0F, Mth.sin((float)now / 130.0F - i * 0.9F)) * 1.2F;
            menuDrawContext.drawRoundedRect(
               f6 + (i - 1) * 2.8F - 0.6F,
               y2 + height2 / 2.0F - 0.6F - f8,
               1.2F,
               1.2F,
               ProxyDotRadiusData.method8069(0.6F),
               zenithColor.method7295(ZenithColor.INSTANCE, 0.3F).method7294(previewAlpha)
            );
         }
      } else {
         ZenithColor zenithColor5 = (flag ? zenithColor2 : zenithStyle.getTextTertiary().getZenithColor()).method7295(zenithColor2, 0.3F * f4);
         if (this.keyAnim == 2 && this.keyFrom != null) {
            drawKeyLetters(menuDrawContext, font, this.keyFrom, f6, f7, l, zenithColor5.method7294(previewAlpha), zenithColor, 2);
         }

         long l2 = this.keyAnim == 2 ? l - 150L : (this.keyAnim == 1 ? l - 60L : Long.MAX_VALUE);
         drawKeyLetters(menuDrawContext, font, valueText, f6, f7, l2, zenithColor5.method7294(previewAlpha), zenithColor, this.keyAnim == 1 ? 1 : 0);
      }

      if (this.keyAnim == 1 && l < 460L) {
         float f8 = (float)l / 460.0F;
         float f9 = 1.0F - (1.0F - f8) * (1.0F - f8) * (1.0F - f8);
         float f10 = y2 + height2 / 2.0F;

         for (int i = 0; i < 6; i++) {
            float f11 = (i * 60.0F + 30.0F) * (float) (Math.PI / 180.0);
            float f12 = f9 * (width / 2.0F + 4.0F + i % 2 * 2.0F);
            float width2;
            if (!((width2 = 1.2F * (1.0F - f8)) < 0.05F)) {
               menuDrawContext.drawRoundedRect(
                  f6 + Mth.cos(f11) * f12 - width2 / 2.0F,
                  f10 + Mth.sin(f11) * f12 * 0.7F - width2 / 2.0F,
                  width2,
                  width2,
                  ProxyDotRadiusData.method8069(width2 / 2.0F),
                  (i % 2 == 0 ? zenithColor : zenithColor.method7295(ZenithColor.INSTANCE, 0.5F)).method7294((1.0F - f8) * previewAlpha)
               );
            }
         }
      }

      this.valueBounds = new BoundsData(f - width, y, width, height);
   }

   private static void drawKeyLetters(
      MenuDrawContext menuDrawContext, Font font, String keyFrom, float f, float f2, long l, ZenithColor zenithColor, ZenithColor zenithColor2, int n
   ) {
      float f3 = f - font.width(keyFrom) / 2.0F;
      int length = keyFrom.length();
      boolean flag = n == 1;
      boolean flag2 = n == 2;

      for (int i = 0; i < length; i++) {
         String text = String.valueOf(keyFrom.charAt(i));
         float width = font.width(text);
         float f4 = 0.0F;
         float f5 = 0.0F;
         float previewAlpha = 1.0F;
         ZenithColor zenithColor3 = zenithColor;
         if (l != Long.MAX_VALUE) {
            if (flag) {
               float f6 = Mth.clamp((float)(l - i * 35L) / 420.0F, 0.0F, 1.0F);
               f5 = -4.0F * (float)(Math.exp(-7.0F * f6) * Math.cos(11.0F * f6));
               previewAlpha = Math.min(1.0F, f6 * 4.0F);
               zenithColor3 = zenithColor.method7295(zenithColor2.method7295(ZenithColor.INSTANCE, 0.5F).method7293(zenithColor.getIntValue4()), (1.0F - f6) * (1.0F - f6));
            } else if (flag2) {
               float f6 = Mth.clamp((float)(l - (length - 1 - i) * 28L) / 340.0F, 0.0F, 1.0F);
               f5 = 4.5F * f6 * f6;
               int n2;
               int n3;
               f4 = (
                     (
                              (
                                    (n3 = ((n2 = (i * 31 + length) * (10196 << 16 | (char)-5331)) ^ n2 >>> 15) * (-31253 << 16 | (char)-13717))
                                       ^ n3 >>> 13
                                 )
                                 >>> 8
                           )
                           / 1.6777216E7F
                        - 0.5F
                  )
                  * 3.5F
                  * f6;
               previewAlpha = 1.0F - f6;
            } else {
               float f6 = Mth.clamp((float)(l - i * 18L) / 200.0F, 0.0F, 1.0F);
               float f7;
               f5 = (f7 = 1.0F - f6) * f7 * f7 * 3.0F;
               previewAlpha = f6;
            }
         }

         if (previewAlpha > 0.01F) {
            menuDrawContext.drawText(font, text, f3 + f4, f2 + f5, zenithColor3.method7294(previewAlpha));
         }

         f3 += width;
      }
   }

   private static float hash01(int n) {
      int n2;
      int n3;
      return (((n3 = ((n2 = n * (10196 << 16 | (char)-5331)) ^ n2 >>> 15) * (-31253 << 16 | (char)-13717)) ^ n3 >>> 13) >>> 8) / 1.6777216E7F;
   }

   private void renderTrack(
      MenuDrawContext menuDrawContext,
      float mouseX,
      float mouseY,
      float y,
      float height,
      float previewAlpha,
      float springNumber,
      String numberText,
      Font font,
      float centeredTextY,
      float f,
      float f2,
      ZenithStyle currentStyle
   ) {
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      float f3 = this.dragAnimation.method2808(this.dragging ? 1.0F : 0.0F);
      ZenithColor zenithColor2 = currentStyle.getTextEnable().getZenithColor().method7295(zenithColor, f3 * 0.6F);
      this.valueRoll.draw(menuDrawContext, font, numberText, f, centeredTextY, zenithColor2.method7294(previewAlpha), this.numberDirection);
      float f4 = f - 30.0F;
      this.trackSpanPx = Float.isNaN(this.sharedTrackSpan) ? Mth.clamp(f4 - f2 - GuiStyle.PADDING.intValue(), 18.0F, 34.0F) : this.sharedTrackSpan;
      this.trackLeftPx = f4 - this.trackSpanPx;
      boolean flag = this.bounds != null && this.trackBounds(y, height).method8092(mouseX, mouseY, 4.0F);
      float f5 = Math.max(f3, flag ? 0.4F : 0.0F);
      float height2 = 1.0F + 0.5F * f3;
      float y2 = y + height / 2.0F - height2 / 2.0F;
      menuDrawContext.drawRect(this.trackLeftPx, y2, this.trackSpanPx, height2, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 3.0F));
      float f6;
      if ((f6 = this.edgeHitAt < 0L ? 1.0F : Mth.clamp((float)(System.currentTimeMillis() - this.edgeHitAt) / 380.0F, 0.0F, 1.0F)) >= 1.0F) {
         this.edgeHitAt = -1L;
      }

      float f7 = f6 >= 1.0F ? 0.0F : Mth.sin(f6 * (float) Math.PI * 3.0F) * (1.0F - f6) * 2.2F * this.edgeSide;
      float f8 = Mth.clamp(springNumber, 0.0F, 1.0F);
      float f9 = this.trackLeftPx + this.trackSpanPx * f8 + f7;
      menuDrawContext.drawRect(this.trackLeftPx, y2, Math.max(0.0F, f9 - this.trackLeftPx), height2, zenithColor.method7294(previewAlpha));
      if (f6 < 1.0F) {
         float trackLeftPx = this.edgeSide < 0 ? this.trackLeftPx : this.trackLeftPx + this.trackSpanPx;
         float intValue = 3.5F * (1.0F - f6) + 1.0F;
         menuDrawContext.drawRoundedRect(
            trackLeftPx - intValue,
            y + height / 2.0F - intValue,
            intValue * 2.0F,
            intValue * 2.0F,
            ProxyDotRadiusData.method8069(intValue),
            zenithColor.method7294(0.35F * (1.0F - f6) * previewAlpha)
         );
      }

      float height3 = 5.0F + 2.0F * f5;
      float width = 1.5F + 0.5F * f5;
      if (f3 > 0.01F) {
         float intValue = 3.0F + 1.5F * f3;
         menuDrawContext.drawRoundedRect(
            f9 - intValue + width / 2.0F,
            y + height / 2.0F - intValue,
            intValue * 2.0F,
            intValue * 2.0F,
            ProxyDotRadiusData.method8069(intValue),
            zenithColor.method7294(0.22F * f3 * previewAlpha)
         );
      }

      menuDrawContext.drawRect(
         f9 - width / 2.0F + 0.25F, y + height / 2.0F - height3 / 2.0F, width, height3, zenithColor.method7295(ZenithColor.INSTANCE, 0.25F * f3).method7294(previewAlpha)
      );
   }

   private float springNumber(SliderSetting sliderSetting) {
      float f;
      float springPos = (f = sliderSetting.getMax() - sliderSetting.getMin()) <= 0.0F ? 0.0F : (sliderSetting.getValue3() - sliderSetting.getMin()) / f;
      if (Float.isNaN(this.springPos)) {
         this.springPos = springPos;
         this.springVel = 0.0F;
         return springPos;
      } else {
         float frameDt = this.frameDt;
         float f2 = 0.004166667F;

         while (frameDt > 0.0F) {
            float f3 = Math.min(f2, frameDt);
            this.springVel = this.springVel + (380.0F * (springPos - this.springPos) - 30.0F * this.springVel) * f3;
            this.springPos = this.springPos + this.springVel * f3;
            frameDt -= f3;
         }

         if (Math.abs(springPos - this.springPos) < 5.0E-4F && Math.abs(this.springVel) < 0.002F) {
            this.springPos = springPos;
            this.springVel = 0.0F;
         }

         return this.springPos;
      }
   }

   private float labelLimit(float f, float width, float f2) {
      float f3 = f + width - 8.0F - f2;
      Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
      float f4;
      if (this.setting instanceof SliderSetting) {
         float sharedTrackSpan = Float.isNaN(this.sharedTrackSpan) ? 18.0F : this.sharedTrackSpan;
         f4 = 30.0F + sharedTrackSpan;
      } else if (this.setting instanceof BooleanSetting) {
         Localization localization = Zenith.getInstance().getLocalization();
         f4 = Math.max(font.width(localization.translate("gui.inspector.on")), font.width(localization.translate("gui.inspector.off"))) + GuiStyle.PADDING.intValue() + 9.0F;
      } else if (this.setting instanceof ColorSetting) {
         f4 = font.width("FFFFFF") + GuiStyle.PADDING.intValue() + 7.0F + 1.0F;
      } else if (this.bindSetting != null) {
         String text = BindLabel.text(this.binding, this.bindSetting.getKeyCode(), this.bindSetting.getModifiers());
         f4 = Math.max(30.0F, font.width(text == null ? "+" : text));
      } else if (this.setting instanceof ModeSetting modeSetting) {
         String name = this.expanded ? String.valueOf(modeSetting.getValues().size()) : (modeSetting.getValue2() == null ? "—" : modeSetting.getValue2().getName());
         f4 = Math.min(Math.max(0.0F, f3 * 0.45F), Math.max(10.0F, font.width(name)));
      } else {
         f4 = Math.max(30.0F, Math.min(font.width(this.valueText()), f3 * 0.45F));
      }

      return Math.max(0.0F, f3 - f4 - GuiStyle.PADDING.intValue() - (this.expandable() ? 7.0F : 0.0F));
   }

   private void renderSwitch(MenuDrawContext menuDrawContext, float floatValue, float y, float hover, float previewAlpha, ZenithStyle currentStyle) {
      menuDrawContext.drawRect(floatValue, y, 9.0F, 5.0F, currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha * 3.0F));
      menuDrawContext.drawRoundedBorder(floatValue, y, 9.0F, 5.0F, 0.1F, ProxyDotRadiusData.INSTANCE, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 4.0F));
      float alpha1 = hover < 0.5F ? 2.0F * hover * hover : 1.0F - (float)Math.pow(-2.0F * hover + 2.0F, 2.0) / 2.0F;
      float width = 3.0F + 1.8F * Mth.sin(hover * (float) Math.PI);
      float f = Mth.lerp(alpha1, floatValue + 1.0F, floatValue + 9.0F - width - 1.0F);
      ZenithColor zenithColor = currentStyle.getTextTertiary().getZenithColor();
      ZenithColor zenithColor2 = currentStyle.getPrimaryColor().getZenithColor();
      menuDrawContext.drawRect(f, y + 1.0F, width, 3.0F, zenithColor.method7295(zenithColor2, hover).method7294(previewAlpha));
      float flashProgress;
      if ((flashProgress = this.flashProgress()) < 1.0F && hover > 0.5F) {
         float f2 = flashProgress * 4.0F;
         menuDrawContext.drawRoundedBorder(
            floatValue - f2,
            y - f2,
            9.0F + f2 * 2.0F,
            5.0F + f2 * 2.0F,
            0.5F,
            ProxyDotRadiusData.method8069(f2 * 0.7F),
            zenithColor2.method7294((1.0F - flashProgress) * (1.0F - flashProgress) * 0.8F * previewAlpha)
         );
      }
   }

   private float flashProgress() {
      if (this.flashAt < 0L) {
         return 1.0F;
      } else {
         float f;
         if ((f = Mth.clamp((float)(System.currentTimeMillis() - this.flashAt) / 520.0F, 0.0F, 1.0F)) >= 1.0F) {
            this.flashAt = -1L;
         }

         return f;
      }
   }

   private float shakeOffset() {
      if (this.shakeAt < 0L) {
         return 0.0F;
      } else {
         float f;
         if ((f = Mth.clamp((float)(System.currentTimeMillis() - this.shakeAt) / 360.0F, 0.0F, 1.0F)) >= 1.0F) {
            this.shakeAt = -1L;
            return 0.0F;
         } else {
            return Mth.sin(f * (float) Math.PI * 6.0F) * 2.0F * (1.0F - f);
         }
      }
   }

   private void renderFlash(MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, long now, float f, ZenithStyle currentStyle) {
      if (this.flashAt >= 0L) {
         float f2;
         if (!((f2 = Mth.clamp((float)(now - this.flashAt) / 520.0F, 0.0F, 1.0F)) >= 1.0F)) {
            ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
            menuDrawContext.drawRect(floatValue, y, width, height, zenithColor.method7294(0.07F * (1.0F - f2) * f));
            float f3 = 1.0F - (1.0F - f2) * (1.0F - f2);
            float f4 = floatValue - 20.0F + (width + 40.0F) * f3;
            float f5 = 18.0F;
            float previewAlpha;
            if (!((previewAlpha = 0.16F * Mth.sin(f2 * (float) Math.PI) * f) <= 0.002F)) {
               ZenithColor zenithColor2 = zenithColor.method7294(0.0F);
               ZenithColor zenithColor3 = zenithColor.method7295(ZenithColor.INSTANCE, 0.3F).method7294(previewAlpha);
               band(menuDrawContext, f4 - f5, f4, floatValue, y, width, height, zenithColor2, zenithColor3);
               band(menuDrawContext, f4, f4 + f5, floatValue, y, width, height, zenithColor3, zenithColor2);
            }
         }
      }
   }

   private static void band(MenuDrawContext menuDrawContext, float f, float f2, float floatValue, float y, float width, float height, ZenithColor zenithColor, ZenithColor zenithColor2) {
      float floatValue2 = Math.max(f, floatValue);
      float f3;
      if (!((f3 = Math.min(f2, floatValue + width)) - floatValue2 <= 0.1F)) {
         float f4 = f2 - f;
         ZenithColor clientColor = zenithColor.method7295(zenithColor2, (floatValue2 - f) / f4);
         ZenithColor zenithColor3 = zenithColor.method7295(zenithColor2, (f3 - f) / f4);
         menuDrawContext.drawRoundedRect(floatValue2, y, f3 - floatValue2, height, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(clientColor, clientColor, zenithColor3, zenithColor3));
      }
   }

   private BoundsData trackBounds(float y, float height) {
      return new BoundsData(this.trackLeftPx, y, this.trackSpanPx, height);
   }

   private String label() {
      return this.setting.getName();
   }

   private boolean valueStrong() {
      if (this.setting instanceof BooleanSetting booleanSetting) {
         return booleanSetting.isEnabled();
      } else {
         return this.setting instanceof BindSetting bindSetting ? bindSetting.getKeyCode() != -1 : true;
      }
   }

   private String valueText() {
      if (this.setting instanceof BooleanSetting booleanSetting) {
         return Zenith.getInstance().getLocalization().translate(booleanSetting.isEnabled() ? "gui.inspector.on" : "gui.inspector.off");
      } else if (this.setting instanceof ModeSetting modeSetting) {
         return modeSetting.getValue2() == null ? "-" : modeSetting.getValue2().getName().toLowerCase(Locale.ROOT);
      } else if (this.setting instanceof BindSetting bindSetting) {
         if (this.binding) {
            return "...";
         } else {
            int keyCode;
            if ((keyCode = bindSetting.getKeyCode()) != -1 && keyCode != 0) {
               try {
                  String str = KeyName.method6702(keyCode, bindSetting.getModifiers());
                  return str != null && !str.isBlank() ? str.toUpperCase() : "-";
               } catch (Exception e) {
                  return "-";
               }
            } else {
               return "-";
            }
         }
      } else if (this.setting instanceof MultiSelectSetting multiSelectSetting) {
         int n = 0;

         for (MultiSelectSetting.Entry entry : multiSelectSetting.getItems2()) {
            if (entry.isEnabled()) {
               n++;
            }
         }

         return n + "/" + multiSelectSetting.getItems2().size();
      } else if (this.setting instanceof GroupSetting groupSetting) {
         return String.valueOf(groupSetting.getSettings().size());
      } else if (this.setting instanceof TextSetting textSetting) {
         if (this.editing) {
            return this.editBuffer + "_";
         } else {
            String str = textSetting.getValue();
            return str != null && !str.isBlank() ? str : "-";
         }
      } else if (this.setting instanceof BlockListSetting blockListSetting) {
         return String.valueOf(blockListSetting.getItems().size());
      } else {
         return this.setting instanceof ButtonSetting ? "›" : "";
      }
   }

   private static String fit(Font font, String name, float f) {
      if (name != null && !name.isEmpty() && !(font.width(name) <= f)) {
         String text = "...";
         float width = font.width(text);
         int length = name.length();

         while (length > 0 && font.width(name.substring(0, length)) + width > f) {
            length--;
         }

         return length <= 0 ? "" : name.substring(0, length).stripTrailing() + text;
      } else {
         return name == null ? "" : name;
      }
   }

   private String numberText(SliderSetting sliderSetting, float numberTextValue) {
      SliderSetting.Handler handler = sliderSetting.getHandler();
      if (this.numberText == null || Float.floatToIntBits(numberTextValue) != Float.floatToIntBits(this.numberTextValue) || handler != this.numberTextDisplay) {
         this.numberText = formatNumber(sliderSetting, numberTextValue) + sliderSetting.getSuffix();
         this.numberTextValue = numberTextValue;
         this.numberTextDisplay = handler;
      }

      return this.numberText;
   }

   private static String formatNumber(SliderSetting sliderSetting, float numberTextValue) {
      SliderSetting.Handler handler = sliderSetting.getHandler();
      if (handler != null) {
         return handler.text(numberTextValue);
      } else {
         // Знаков после запятой — столько же, сколько у шага (0.1 → 1, 0.05 → 2, 0.005 → 3);
         // раньше смотрелись только две первые цифры шага, и 0.005 давал 0 знаков («1.435» → «1»).
         float f = Math.abs(sliderSetting.getFloatValue2());
         int n = Float.isFinite(f) && f != 0.0F ? Mth.clamp(new BigDecimal(Float.toString(f)).stripTrailingZeros().scale(), 0, 8) : 0;
         return String.format(Locale.US, "%." + n + "f", numberTextValue);
      }
   }

   @Override
   public boolean bindMouse(Enum3 enum2) {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      if (this.binding && this.bindSetting != null && this.setting.isVisible() && MouseBindTarget.bindable(enum2)) {
         this.bindSetting.setBind(enum2.getIntValue(), 0);
         this.binding = false;
         BindCapture.end(this);
         this.bindLabel.set();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   private void startKeyAnim(int keyAnim, String anObject) {
      this.keyAnim = keyAnim;
      this.keyAnimAt = System.currentTimeMillis();
      this.keyFrom = anObject != null && !"-".equals(anObject) ? anObject : null;
   }

   public void cancelBinding() {
      this.binding = false;
      BindCapture.end(this);

      for (InspectorRow inspectorRow : this.children) {
         inspectorRow.cancelBinding();
      }
   }

   public void cancelModuleBindingOutside(double x, double y) {
      if (this.setting instanceof ModuleHeaderSetting && (this.valueBounds == null || !this.valueBounds.contains(x, y))) {
         this.cancelBinding();
      }
   }

   public boolean onMouseClicked(double x, double y, Enum3 enum2) {
      if (!this.setting.isVisible()) {
         this.cancelBinding();
         return false;
      } else {
         if (!MouseBindTarget.bindable(enum2)) {
            this.cancelModuleBindingOutside(x, y);
         }

         if (this.modeOptions != null) {
            if (!this.setting.isVisible() || this.visibleAnimation.getValue() < 0.99F) {
               return false;
            }

            if (this.expanded && this.modeOptions.onMouseClicked(x, y, enum2)) {
               return true;
            }
         }

         if (this.expanded && this.picker != null && this.picker.onMouseClicked(x, y, enum2)) {
            return true;
         } else {
            if (this.expanded
               && this.bounds != null
               && x >= this.bounds.getFloatValue()
               && x <= this.bounds.getFloatValue() + this.bounds.width()
               && y >= this.bounds.getFloatValue2() + this.bounds.height()
               && y <= this.bounds.getFloatValue2() + this.height()) {
               for (InspectorRow inspectorRow : this.children) {
                  if (inspectorRow.isVisible() && inspectorRow.onMouseClicked(x, y, enum2)) {
                     return true;
                  }
               }
            }

            if (this.bounds == null || !this.bounds.contains(x, y)) {
               return false;
            } else if (this.picker != null) {
               this.expanded = !this.expanded;
               if (!this.expanded) {
                  this.picker.remember();
               } else {
                  this.pickerToggledAt = System.currentTimeMillis();
               }

               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            } else if (this.setting instanceof SliderSetting sliderSetting) {
               if (this.valueBounds != null && this.valueBounds.method8092(x, y, 4.0F)) {
                  this.dragging = true;
                  this.dragOffset = this.grabOffset(sliderSetting, x);
                  Zenith.getInstance().getNLMenuScreen().lockGuiScale();
                  this.applyDrag(x);
                  return true;
               } else {
                  return true;
               }
            } else if (this.setting instanceof BooleanSetting booleanSetting) {
               booleanSetting.toggle();
               this.flashAt = booleanSetting.isEnabled() ? System.currentTimeMillis() : -1L;
               playToggleSound(booleanSetting.isEnabled());
               return true;
            } else if (this.modeOptions != null) {
               if (enum2 != Enum3.LEFT && enum2 != Enum3.RIGHT) {
                  return false;
               } else {
                  this.expanded = !this.expanded;
                  this.modeOptions.clearBounds();
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            } else if (this.bindSetting == null
               || this.setting instanceof ModuleHeaderSetting && (this.valueBounds == null || !this.valueBounds.contains(x, y))) {
               if (this.setting instanceof ButtonSetting buttonSetting) {
                  buttonSetting.toggle();
                  Zenith.getInstance().getClientSounds().method1562();
                  return true;
               } else if (this.setting instanceof TextSetting textSetting) {
                  this.editing = true;
                  this.editBuffer = new StringBuilder(textSetting.getValue() == null ? "" : textSetting.getValue());
                  this.editOriginal = this.editBuffer.toString();
                  this.editStartAt = System.currentTimeMillis();
                  this.typedAt = -1L;
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               } else if (!this.children.isEmpty()) {
                  this.expanded = !this.expanded;
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               } else {
                  return true;
               }
            } else if (this.bindMouse(enum2)) {
               return true;
            } else {
               if (enum2 == Enum3.RIGHT) {
                  this.cancelBinding();
                  this.bindSetting.setBind(-1, 0);
                  this.bindLabel.cleared();
                  Zenith.getInstance().getClientSounds().method1567();
               } else {
                  this.keyBeforeBinding = this.binding
                     ? this.keyBeforeBinding
                     : BindLabel.text(false, this.bindSetting.getKeyCode(), this.bindSetting.getModifiers());
                  this.binding = true;
                  this.bindLabel.waiting();
                  BindCapture.begin(this);
                  Zenith.getInstance().getClientSounds().method1570(false);
               }

               return true;
            }
         }
      }
   }

   public boolean isEditing() {
      return this.editing;
   }

   public void beginEdit() {
      if (!this.editing && this.setting instanceof TextSetting textSetting) {
         this.editing = true;
         this.editBuffer = new StringBuilder(textSetting.getValue() == null ? "" : textSetting.getValue());
         this.editOriginal = this.editBuffer.toString();
         this.editStartAt = System.currentTimeMillis();
         this.typedAt = -1L;
      }
   }

   public void endEdit() {
      this.editing = false;
   }

   public void onMouseReleased(double x, double y, Enum3 enum2) {
      this.dragging = false;
      this.dragOffset = 0.0F;
      this.edgePinned = false;
      if (this.picker != null) {
         this.picker.onMouseReleased();
      }

      for (InspectorRow inspectorRow : this.children) {
         inspectorRow.onMouseReleased(x, y, enum2);
      }
   }

   public boolean onMouseDragged(double mouseX, double mouseY) {
      if (this.dragging) {
         this.applyDrag(mouseX);
         return true;
      } else {
         boolean flag = false;
         if (this.expanded) {
            for (InspectorRow inspectorRow : this.children) {
               if (inspectorRow.onMouseDragged(mouseX, mouseY)) {
                  flag = true;
               }
            }
         }

         return flag;
      }
   }

   private float pointerAt(double x) {
      return this.bounds != null && !Float.isNaN(this.trackLeftPx) && !(this.trackSpanPx <= 0.0F)
         ? Mth.clamp((float)(x - this.trackLeftPx) / this.trackSpanPx, 0.0F, 1.0F)
         : 0.0F;
   }

   private float grabOffset(SliderSetting sliderSetting, double x) {
      float f;
      float f2 = (f = sliderSetting.getMax() - sliderSetting.getMin()) <= 0.0F ? 0.0F : (sliderSetting.getValue3() - sliderSetting.getMin()) / f;
      float pointerAt;
      return Math.abs((pointerAt = this.pointerAt(x)) - f2) * this.trackSpanPx <= 5.0F ? f2 - pointerAt : 0.0F;
   }

   private void applyDrag(double x) {
      if (this.bounds != null) {
         float progress = Mth.clamp(this.pointerAt(x) + this.dragOffset, 0.0F, 1.0F);
         if (!Float.isNaN(this.trackLeftPx) && this.trackSpanPx > 0.0F) {
            float f;
            int edgeSide;
            if ((edgeSide = (f = (float)(x - this.trackLeftPx) / this.trackSpanPx + this.dragOffset) < -0.02F ? -1 : (f > 1.02F ? 1 : 0)) != 0
               && !this.edgePinned) {
               this.edgePinned = true;
               this.edgeSide = edgeSide;
               this.edgeHitAt = System.currentTimeMillis();
            } else if (edgeSide == 0) {
               this.edgePinned = false;
            }
         }

         if (this.setting instanceof SliderSetting sliderSetting) {
            float f = sliderSetting.getMin() + (sliderSetting.getMax() - sliderSetting.getMin()) * progress;
            float f2;
            if ((f2 = sliderSetting.getFloatValue2()) > 0.0F) {
               f = Math.round(f / f2) * f2;
            }

            float f3 = sliderSetting.getValue3();
            sliderSetting.method2400(Mth.clamp(f, sliderSetting.getMin(), sliderSetting.getMax()));
            if (sliderSetting.getValue3() != f3) {
               Zenith.getInstance().getClientSounds().method1571(progress);
            }
         }
      }
   }

   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      if (this.binding && this.bindSetting != null) {
         if (keyCode == 256) {
            this.bindSetting.setBind(-1, 0);
            this.bindLabel.cleared();
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.bindSetting.setBind(keyCode, KeyName.method6700(keyCode, modifiers));
            this.bindLabel.set();
            Zenith.getInstance().getClientSounds().method1566();
         }

         this.binding = false;
         BindCapture.end(this);
         return true;
      } else if (this.editing && this.setting instanceof TextSetting textSetting) {
         if (keyCode == 259) {
            if (!this.editBuffer.isEmpty()) {
               this.editBuffer.deleteCharAt(this.editBuffer.length() - 1);
               this.publishEdit(textSetting);
            }

            return true;
         } else if (keyCode == 257 || keyCode == 335) {
            textSetting.method2122(this.editBuffer.toString());
            this.editing = false;
            this.flashAt = System.currentTimeMillis();
            Zenith.getInstance().getClientSounds().method1568();
            return true;
         } else if (keyCode == 256) {
            textSetting.setValue(this.editOriginal);
            this.editBuffer.setLength(0);
            this.editBuffer.append(this.editOriginal);
            this.editing = false;
            this.shakeAt = System.currentTimeMillis();
            Zenith.getInstance().getClientSounds().method1569();
            return true;
         } else {
            return true;
         }
      } else if (this.expanded && this.picker != null && this.picker.keyPressed(keyCode, keycode, modifiers)) {
         return true;
      } else {
         if (this.expanded) {
            for (InspectorRow inspectorRow : this.children) {
               if (inspectorRow.isVisible() && inspectorRow.keyPressed(keyCode, keycode, modifiers)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      if (this.editing) {
         this.editBuffer.append(codepoint);
         this.typedAt = System.currentTimeMillis();
         if (this.setting instanceof TextSetting textSetting) {
            this.publishEdit(textSetting);
         }

         return true;
      } else if (this.expanded && this.picker != null && this.picker.charTyped(codepoint, n)) {
         return true;
      } else {
         if (this.expanded) {
            for (InspectorRow inspectorRow : this.children) {
               if (inspectorRow.isVisible() && inspectorRow.charTyped(codepoint, n)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void publishEdit(TextSetting textSetting) {
      textSetting.setValue(this.editBuffer.toString());
   }

   private static void playToggleSound(boolean enabled) {
      Zenith.getInstance()
         .getClientSounds()
         .method1561(enabled ? Zenith.getInstance().getClientSounds().soundEvent5 : Zenith.getInstance().getClientSounds().soundEvent6);
   }

   private static final class MultiValueAdapter extends BooleanSetting {
      private final MultiSelectSetting.Entry value;

      private MultiValueAdapter(MultiSelectSetting multiSelectSetting, MultiSelectSetting.Entry entry) {
         super(entry.getKey(), multiSelectSetting.getDescription(), entry.isEnabled());
         this.value = entry;
      }

      @Override
      public boolean isEnabled() {
         return this.value.isEnabled();
      }

      @Override
      public void setEnabled(boolean enabled) {
         this.value.setEnabled(enabled);
      }

      @Override
      public void toggle() {
         this.value.setEnabled(!this.value.isEnabled());
      }

      @Override
      public String getName() {
         return this.value.getName();
      }

      @Override
      public void safe(JsonObject json) {
      }

      @Override
      public void load(JsonObject json) {
      }
   }
}
