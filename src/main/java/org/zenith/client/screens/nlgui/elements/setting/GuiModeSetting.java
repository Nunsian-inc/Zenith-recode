package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.HoverScrollText;
import org.zenith.client.screens.nlgui.elements.ModeOptions;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class GuiModeSetting extends GuiSetting<ModeSetting> {
   private final Animation animationExpanded = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final ModeOptions options;
   private final HoverScrollText labelScroll = new HoverScrollText();
   private final HoverScrollText valueScroll = new HoverScrollText();
   private BoundsData bounds;
   private boolean expanded;

   public GuiModeSetting(ModeSetting setting) {
      this(setting, 166.0F);
   }

   public GuiModeSetting(ModeSetting setting, float width) {
      super(width, setting);
      this.options = new ModeOptions(setting);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double x, double y, MouseButton enum2) {
      if (this.setting.isVisible() && (enum2 == MouseButton.LEFT || enum2 == MouseButton.RIGHT)) {
         if (this.expanded && this.options.onMouseClicked(x, y, enum2)) {
            return true;
         } else if (this.bounds != null && this.bounds.contains(x, y)) {
            this.expanded = (!this.expanded);
            this.options.clearBounds();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float floatValue, float y, float previewAlpha) {
      this.options.clearBounds();
      this.bounds = null;
      this.animationVisible.updateVisibility(this.setting.isVisible());
      previewAlpha *= this.animationVisible.getValue();
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null && !(previewAlpha <= 0.01F)) {
         float f = this.animationExpanded.animateTo(this.expanded ? 1.0F : 0.0F);
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.01F);
         this.bounds = new BoundsData(floatValue, y, this.width, this.rowHeight());
         boolean contains = this.bounds.contains(lastClickX, lastClickY);
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         String name = this.expanded
            ? String.valueOf(this.setting.getValues().size())
            : (this.setting.getValue2() == null ? "—" : this.setting.getValue2().getName());
         float labelLimit = Math.min(this.width * 0.4F, Math.max(10.0F, font2.width(name)));
         float floatValue2 = floatValue + this.width - labelLimit - 10.0F;
         float floatValue3 = floatValue;
         if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
            MenuGlyph.MODE.draw(menuDrawContext, floatValue, y + (this.rowHeight() - 6.0F) / 2.0F, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha));
            floatValue3 = floatValue + (6.0F + GuiStyle.PADDING.intValue());
         }

         this.labelScroll
            .render(
               menuDrawContext,
               font,
               this.setting.getName(),
               floatValue3,
               y + (this.rowHeight() - font.height()) / 2.0F,
               Math.max(0.0F, floatValue2 - GuiStyle.PADDING.intValue() - floatValue3),
               zenithColor,
               contains
            );
         this.valueScroll
            .render(
               menuDrawContext,
               font2,
               name,
               floatValue2,
               y + (this.rowHeight() - font2.height()) / 2.0F,
               labelLimit,
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha),
               contains
            );
         MenuGlyph.drawChevron(menuDrawContext, floatValue + this.width - 5.0F, y + this.rowHeight() / 2.0F, f, zenithColor);
         if (contains) {
            HoverInfo.request(this.setting.getName(), this.setting.getDescription());
         }

         float f2;
         if ((f2 = this.options.height(this.width) * f) > 0.01F) {
            float y2 = y + this.rowHeight();
            menuDrawContext.enableScissor(floatValue, y2, floatValue + this.width, y2 + f2);

            try {
               this.options.render(menuDrawContext, lastClickX, lastClickY, floatValue, y2, this.width, f2, previewAlpha * f, (this.expanded && previewAlpha > 0.99F), currentStyle);
            } finally {
               menuDrawContext.disableScissor();
            }
         }
      }
   }

   @Override
   public float getHeight() {
      return this.rowHeight() + this.options.height(this.width) * this.animationExpanded.getValue();
   }

   @Override
   public float rowHeight() {
      return 14.0F;
   }
}
