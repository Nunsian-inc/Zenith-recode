package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MenuDrawContext extends CustomDrawContext {
   private final int mouseX;
   private final int mouseY;
   private final float delta;

   public MenuDrawContext(GuiGraphicsExtractor menuDrawContext, int mouseX, int mouseY, float delta) {
      super(menuDrawContext);
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.delta = delta;
   }

   public static MenuDrawContext method8080(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      return new MenuDrawContext(context, mouseX, mouseY, deltaTicks);
   }

   @Generated
   public int getMouseX() {
      return this.mouseX;
   }

   @Generated
   public int getMouseY() {
      return this.mouseY;
   }

   @Generated
   public float getDelta() {
      return this.delta;
   }
}
