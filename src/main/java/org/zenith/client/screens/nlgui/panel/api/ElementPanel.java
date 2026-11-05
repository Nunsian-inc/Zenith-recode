package org.zenith.client.screens.nlgui.panel.api;

import java.util.List;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class ElementPanel extends Panel {
   public abstract void renderHeader(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float floatValue, float animationPosY, float f2);

   public abstract void close();

   public abstract List<? extends Element> getElements();

   public void renderPriority(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float floatValue, float f2) {
   }

   public float getButtonWidth() {
      return 0.0F;
   }

   public float getHeaderTitleWidth() {
      return 0.0F;
   }

   public float getContentWidth() {
      return 380.0F;
   }

   public float extraWidth() {
      return 0.0F;
   }

   public boolean hasLeftColumn() {
      return false;
   }

   public void renderLeftColumn(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float f2, float y, float leftWidthPanel, float heightPanel) {
   }

   public boolean isHeaderSearchOnly() {
      return false;
   }

   public void renderHeaderButtons(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float f2, float animationPosY, float buttonWidth, float alpha1) {
   }

   public boolean onHeaderButtonsClicked(double x, double y, Enum3 enum2) {
      return false;
   }

   public boolean onMouseDragged(double doubleValue, double mouseY, int mouseToGlfw, double offsetX, double offsetY) {
      return false;
   }

   public boolean isRightDrawerOpen() {
      return false;
   }

   public void closeRightDrawer() {
   }

   public boolean isRender() {
      return false;
   }

   public void tick() {
   }

   public void renderRightPanel(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float f2, float y, float alpha1) {
   }

   public BoundsData getRightPanelBlurBounds(float f, float y, float f2, float alpha1) {
      return null;
   }

   public float getRightPanelBlurProgress(float f, float f2) {
      return f * f2;
   }
}
