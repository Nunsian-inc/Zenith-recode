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

   public abstract void render(MenuDrawContext var1, float var2, float var3);

   @Override
   public final void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext var5 = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      this.render(var5, mouseX, mouseY);
      super.extractRenderState(context, mouseX, mouseY, deltaTicks);
      var5.extractDeferredElements(mouseX, mouseY, deltaTicks);
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

   public void onMouseClicked(double var1, double var3, Enum3 var5) {
   }

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
   }

   public void onMouseDragged(double var1, double var3, Enum3 var5, double var6, double var8) {
   }
}
