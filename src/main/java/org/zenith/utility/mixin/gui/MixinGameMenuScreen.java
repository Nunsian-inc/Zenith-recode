package org.zenith.utility.mixin.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.MenuTheme;

@Mixin({PauseScreen.class})
public abstract class MixinGameMenuScreen extends Screen {
   protected MixinGameMenuScreen(Component title) {
      super(title);
   }
   private static final float COLUMN = MenuTheme.px(320.0F);
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(42.0F);

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

         for (List<AbstractWidget> row : zenith$rows) {
            boolean icons = row.stream().allMatch(widget -> widget.getClass() != Button.Plain.class || widget.getWidth() <= widget.getHeight());
            float rowWidth = icons ? row.stream().mapToInt(AbstractWidget::getWidth).sum() + (row.size() - 1) * GAP : f;
            float cursorX = icons ? (screen.width - rowWidth) / 2.0F : snap;
            for (AbstractWidget widget : row) {
               widget.setX(Math.round(cursorX));
               widget.setY(Math.round(snap2));
               if (!icons && widget.getClass() == Button.Plain.class && widget.getWidth() > widget.getHeight()) {
                  widget.setWidth(Math.round(row.size() == 1 ? f : COLUMN));
                  widget.setHeight(Math.round(ROW_HEIGHT));
               }
               cursorX += widget.getWidth() + GAP;
            }
            snap2 += ROW_HEIGHT + GAP;
         }

      }
   }

   private List<List<AbstractWidget>> zenith$rows(Screen screen) {
      List<List<AbstractWidget>> rows = new ArrayList<>();
      int y = Integer.MIN_VALUE;

      for (GuiEventListener guiEventListener : screen.children()) {
         if (guiEventListener instanceof AbstractButton abstractWidget && abstractWidget.visible) {
            if (abstractWidget.getY() != y || rows.isEmpty()) {
               rows.add(new ArrayList<>());
               y = abstractWidget.getY();
            }

            rows.get(rows.size() - 1).add(abstractWidget);
         }
      }

      return rows;
   }
}
