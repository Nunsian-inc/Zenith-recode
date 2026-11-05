package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.ColorPicker;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ColorSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiColorSetting extends GuiSetting<ColorSetting> {
   private static final float BOX_SIZE = 4.0F;
   private final ColorPicker picker;
   private final Animation animationExpanded = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private boolean expanded;
   private float pickerHeight;
   private BoundsData rowBounds;
   private BoundsData boxBounds;

   public GuiColorSetting(ColorSetting setting) {
      this(setting, 166.0F);
   }

   public GuiColorSetting(ColorSetting setting, float width) {
      super(width, setting);
      this.picker = new ColorPicker(setting);
      this.pickerHeight = this.picker.height(width);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public float getHeight() {
      return this.rowHeight() + this.pickerHeight * this.animationExpanded.getValue();
   }

   @Override
   public boolean onMouseClicked(double x, double y, Enum3 enum2) {
      if (this.expanded && this.picker.onMouseClicked(x, y, enum2)) {
         return true;
      } else if (this.rowBounds != null && this.rowBounds.contains(x, y)) {
         this.expanded = (!this.expanded);
         if (!this.expanded) {
            this.picker.remember();
         }

         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onMouseReleased(double d, double d2, Enum3 enum2) {
      this.picker.onMouseReleased();
      super.onMouseReleased(d, d2, enum2);
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return (boolean)(this.expanded && this.picker.keyPressed(keyCode, keycode, modifiers) ? 1 : super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers))));
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      return (boolean)(this.expanded && this.picker.charTyped(codepoint, n) ? 1 : super.charTyped(new CharacterEvent(codepoint)));
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float f, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         float rowHeight = this.rowHeight();
         this.rowBounds = new BoundsData(floatValue, f, this.width, rowHeight);
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "x", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, f, maxWidth, zenithColor, zenithColor2, zenithColor3);
         this.boxBounds = new BoundsData(floatValue + this.width - 4.0F, f + (rowHeight - 4.0F) / 2.0F, 4.0F, 4.0F);
         float f2 = 1.5F;
         ZenithColor zenithColor4 = this.setting.method1447(previewAlpha);
         ZenithColor clientColor = ZenithColor.INSTANCE9.method7294(previewAlpha);
         menuDrawContext.drawRoundedBorder(
            this.boxBounds.getFloatValue() - f2,
            this.boxBounds.getFloatValue2() - f2,
            4.0F + f2 * 2.0F,
            4.0F + f2 * 2.0F,
            0.1F,
            ProxyDotRadiusData.method8069(3.0F),
            zenithColor4
         );
         menuDrawContext.drawRoundedRect(
            this.boxBounds.getFloatValue(),
            this.boxBounds.getFloatValue2(),
            4.0F,
            4.0F,
            ProxyDotRadiusData.method8069(3.0F),
            GlowColor.method8096(clientColor, zenithColor4, zenithColor4, clientColor)
         );
         float f3;
         if (!((f3 = this.animationExpanded.method2808(this.expanded ? 1.0F : 0.0F)) <= 0.001F)) {
            float y2 = f + rowHeight;
            this.pickerHeight = this.picker.height(this.width);
            float f4 = this.pickerHeight * f3;
            menuDrawContext.enableScissor(floatValue, y2, floatValue + this.width, y2 + f4);
            this.picker.render(menuDrawContext, x, y, floatValue, y2, this.width, previewAlpha);
            menuDrawContext.disableScissor();
         }
      }
   }
}
