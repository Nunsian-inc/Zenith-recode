package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.input.KeyEvent;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.elements.BindLabel;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class GuiKeySetting extends GuiSetting<BindSetting> implements MouseBindTarget {
   private BoundsData bounds;
   private boolean binding;
   private final BindLabel bindLabel = new BindLabel();

   public GuiKeySetting(BindSetting setting) {
      super(166.0F, setting);
   }

   public GuiKeySetting(BindSetting setting, float width) {
      super(width, setting);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMousePriorityClicked(double d, double d2, MouseButton enum2) {
      return this.bindMouse(enum2);
   }

   @Override
   public boolean bindMouse(MouseButton enum2) {
      if (this.binding && MouseBindTarget.bindable(enum2)) {
         this.setting.setKeyCode(enum2.getGlfwButton());
         this.stopBinding();
         this.bindLabel.set();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (this.bindMouse(enum2)) {
         return true;
      } else if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         if (enum2 == MouseButton.RIGHT) {
            this.setting.setKeyCode(-1);
            this.stopBinding();
            this.bindLabel.cleared();
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.binding = true;
            this.bindLabel.waiting();
            BindCapture.begin(this);
            Zenith.getInstance().getClientSounds().method1570(false);
         }

         return true;
      } else {
         if (this.binding) {
            this.stopBinding();
         }

         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.binding) {
         if (keyCode == 256) {
            this.setting.setBind(-1, 0);
            this.bindLabel.cleared();
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.setting.setBind(keyCode, KeyName.method6700(keyCode, modifiers));
            this.bindLabel.set();
            Zenith.getInstance().getClientSounds().method1566();
         }

         this.stopBinding();
         return true;
      } else {
         return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
      }
   }

   private void stopBinding() {
      this.binding = false;
      BindCapture.end(this);
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float f, float previewAlpha) {
      try {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle == null) {
            return;
         }

         this.animationVisible.updateVisibility(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "N", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, f, maxWidth, zenithColor, zenithColor2, zenithColor3);
         Font font3 = Fonts.NEW_MEDIUM.getFont(5.01F);
         float f2 = floatValue + this.width;
         int n = x >= floatValue && x <= f2 && y >= f && y <= f + this.getHeight() ? 1 : 0;
         this.bindLabel
            .render(
               menuDrawContext,
               x,
               y,
               f,
               this.getHeight(),
               f2,
               BindLabel.text(this.binding, this.setting.getKeyCode(), this.setting.getModifiers()),
               this.binding,
               font3,
               floatValue + maxWidth,
               (n != 0),
               previewAlpha,
               null,
               currentStyle
            );
         this.bounds = new BoundsData(floatValue, f, this.width, this.getHeight());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
