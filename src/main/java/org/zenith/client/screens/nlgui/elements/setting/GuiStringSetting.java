package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.builders.UVPair;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiStringSetting extends GuiSetting<TextSetting> {
   private final Animation focusAnimation = new Animation(200L, Easing.INSTANCE13);
   private BoundsData bounds;
   private SearchBox textBox;

   public GuiStringSetting(TextSetting setting) {
      super(166.0F, setting);
   }

   public GuiStringSetting(TextSetting setting, float width) {
      super(width, setting);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY) && enum2 == MouseButton.LEFT) {
         this.getBox().setSelected(true);
         return true;
      } else {
         if (!this.getBox().onMouseClicked(lastClickX, lastClickY, enum2)) {
            this.setting.method2122(this.getBox().getText());
         }

         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float centeredTextY, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.animationVisible.updateVisibility(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         SearchBox box = this.getBox();
         if (!box.isSelected()) {
            box.setText(this.setting.getValue());
            box.setCursor(this.setting.getValue().length());
         }

         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "M", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, centeredTextY, maxWidth, zenithColor, zenithColor2, zenithColor3);
         float width = this.width / 2.0F;
         float height = this.getHeight();
         this.bounds = new BoundsData(floatValue + this.width - width, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
         this.focusAnimation.updateVisibility(!(!box.isSelected() && !this.bounds.contains(x, y)));
         menuDrawContext.drawRoundedRect(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            0.1F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
            currentStyle.getFieldBorder()
               .getZenithColor()
               .method7295(currentStyle.getPrimaryColor().getZenithColor().method7294(0.5F), this.focusAnimation.getValue())
               .method7294(previewAlpha)
         );
         Font font3 = Fonts.NEW_MEDIUM.getFont(5.3F);
         box.setFont(font3);
         box.setWidth(width - GuiStyle.PADDING.intValue() * 2.0F);
         box.setMaxLength(this.setting.getEntry().getMaxLength());
         box.setMasked(this.setting.isFlag());
         box.setText2(Zenith.getInstance().getLocalization().translate(this.setting.getText()));
         box.method987(
            menuDrawContext,
            this.bounds.getFloatValue() + GuiStyle.PADDING.intValue(),
            this.bounds.getFloatValue2() + (height - font3.height()) / 2.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      SearchBox box = this.getBox();
      if (box.isSelected()) {
         if (keyCode == 257) {
            this.setting.method2122(box.getText());
            box.setSelected(false);
            return true;
         }

         if (keyCode == 256) {
            box.setSelected(false);
            return true;
         }
      }

      boolean keyPressed = box.keyPressed(keyCode, keycode, modifiers);
      this.pushLive(box);
      return keyPressed;
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      SearchBox box = this.getBox();
      boolean charTyped = box.charTyped(codepoint, n);
      this.pushLive(box);
      return charTyped;
   }

   private void pushLive(SearchBox box) {
      if (this.setting.isLive()) {
         this.setting.method2122(box.getText());
      }
   }

   @Override
   public float getHeight() {
      return 14.0F;
   }

   private SearchBox getBox() {
      if (this.textBox == null) {
         this.textBox = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.3F), this.setting.getText(), 40.0F);
      }

      return this.textBox;
   }
}
