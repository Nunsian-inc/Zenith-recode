package org.zenith.client.screens.override.main.page;

import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public interface MainMenuPage {
   float contentWidth();

   float contentHeight();

   default float footerHeight() {
      return 0.0F;
   }

   default float footerWidth() {
      return this.contentWidth();
   }

   default void update() {
   }

   void layout(BoundsData boundsData);

   void render(MenuDrawContext graphics, float f, float f2, BoundsData boundsData);

   default void renderFooter(MenuDrawContext graphics, float f, float f2, BoundsData boundsData) {
   }

   default void onShow() {
   }

   default void onHide() {
   }

   default void tick() {
   }

   default boolean mouseClicked(double d, double d2, int mouseToGlfw) {
      return false;
   }

   default boolean mouseReleased(double d, double d2, int mouseToGlfw) {
      return false;
   }

   default boolean mouseDragged(double d, double d2, int mouseToGlfw) {
      return false;
   }

   default boolean mouseScrolled(double d, double d2, double verticalAmount) {
      return false;
   }

   default boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return false;
   }

   default boolean charTyped(char codepoint, int n) {
      return false;
   }
}
