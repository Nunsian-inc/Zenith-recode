package org.zenith.utility.game.other.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class CustomScreen extends Screen implements MinecraftAccess {
   public CustomScreen() {
      super(Component.empty());
   }

   public abstract void render(MenuDrawContext menuDrawContext, float mouseX, float mouseY);

   @Override
   public final void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext menuDrawContext = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      this.render(menuDrawContext, mouseX, mouseY);
      super.extractRenderState(context, mouseX, mouseY, deltaTicks);
      menuDrawContext.extractDeferredElements(mouseX, mouseY, deltaTicks);
   }

   @Override
   public final boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.onMouseClicked(click.x(), click.y(), Enum3.method6094(org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button())));
      return super.mouseClicked(click, doubled);
   }

   @Override
   public void tick() {
   }

   @Override
   public final boolean mouseReleased(MouseButtonEvent click) {
      this.onMouseReleased(click.x(), click.y(), Enum3.method6094(org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button())));
      return super.mouseReleased(click);
   }

   @Override
   public final boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      this.onMouseDragged(click.x(), click.y(), Enum3.method6094(org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button())), offsetX, offsetY);
      return super.mouseDragged(click, offsetX, offsetY);
   }

   public void onMouseClicked(double x, double y, Enum3 enum2) {
   }

   public void onMouseReleased(double x, double y, Enum3 enum2) {
   }

   public void onMouseDragged(double x, double y, Enum3 enum2, double offsetX, double offsetY) {
   }
}
