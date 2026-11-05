package org.zenith.utility.mixin.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;
import org.zenith.utility.render.display.base.MenuDrawContext;

@Mixin({PauseScreen.class})
public class MixinGameMenuScreen {
   private static final float COLUMN = MenuTheme.px(320.0F);
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(42.0F);
   private static final float PLATE_PADDING = MenuTheme.px(32.0F);
   private static final float PLATE_RADIUS = MenuTheme.px(24.0F);

   @Inject(
      method = {"init()V"},
      at = {@At("TAIL")}
   )
   private void zenith$relayout(CallbackInfo callbackInfo) {
      Screen screen = (Screen)(Object)this;
      List<List<AbstractWidget>> zenith$rows = this.zenith$rows(screen);
      if (!zenith$rows.isEmpty()) {
         float f = COLUMN * 2.0F + GAP;
         float f2 = zenith$rows.size() * ROW_HEIGHT + (zenith$rows.size() - 1) * GAP;
         float snap = MenuTheme.snap((screen.width - f) / 2.0F);
         float snap2 = MenuTheme.snap((screen.height - f2) / 2.0F);

         for (List zenith$row : zenith$rows) {
            float f3 = zenith$row.size() == 1 ? f : COLUMN;

            for (int i = 0; i < zenith$row.size(); i++) {
               AbstractWidget abstractWidget = (AbstractWidget)zenith$row.get(i);
               abstractWidget.setX(Math.round(snap + i * (COLUMN + GAP)));
               abstractWidget.setY(Math.round(snap2));
               abstractWidget.setWidth(Math.round(f3));
               abstractWidget.setHeight(Math.round(ROW_HEIGHT));
            }

            snap2 += ROW_HEIGHT + GAP;
         }
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("HEAD")}
   )
   private void zenith$beginStyle(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks, CallbackInfo callbackInfo) {
      MenuDrawContext graphics = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      MenuSurface.backdrop(graphics, graphics.guiWidth(), graphics.guiHeight(), 0.45F);
      List<List<AbstractWidget>> zenith$rows = this.zenith$rows((Screen)(Object)this);
      if (!zenith$rows.isEmpty()) {
         float f = Float.MAX_VALUE;
         float f2 = Float.MAX_VALUE;
         float f3 = -Float.MAX_VALUE;
         float f4 = -Float.MAX_VALUE;

         for (List<AbstractWidget> zenith$row : zenith$rows) {
            for (AbstractWidget abstractWidget : zenith$row) {
               f = Math.min(f, (float)abstractWidget.getX());
               f2 = Math.min(f2, (float)abstractWidget.getY());
               f3 = Math.max(f3, (float)(abstractWidget.getX() + abstractWidget.getWidth()));
               f4 = Math.max(f4, (float)(abstractWidget.getY() + abstractWidget.getHeight()));
            }
         }

         MenuSurface.card(
            graphics, f - PLATE_PADDING, f2 - PLATE_PADDING, f3 - f + PLATE_PADDING * 2.0F, f4 - f2 + PLATE_PADDING * 2.0F, PLATE_RADIUS
         );
      }

      VanillaOptionStyle.begin();
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$endStyle(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f, CallbackInfo callbackInfo) {
      VanillaOptionStyle.end();
   }

   private List<List<AbstractWidget>> zenith$rows(Screen screen) {
      ArrayList arrayList = new ArrayList();
      int y = Integer.MIN_VALUE;

      for (GuiEventListener guiEventListener : screen.children()) {
         if (guiEventListener instanceof AbstractWidget abstractWidget && abstractWidget.visible) {
            if (abstractWidget.getY() != y || arrayList.isEmpty()) {
               arrayList.add(new ArrayList());
               y = abstractWidget.getY();
            }

            ((List)arrayList.get(arrayList.size() - 1)).add(abstractWidget);
         }
      }

      return arrayList;
   }
}
