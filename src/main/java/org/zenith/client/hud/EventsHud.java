package org.zenith.client.hud;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.screens.ChatScreen;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.utility.misc.HolyWorldEvent;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class EventsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "L";
   private static final String text2 = "Events";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, EventsHud.Entry> items = new LinkedHashMap<>();
   private final Set<String> items2 = new HashSet<>();

   public EventsHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      List<HolyWorldEvent> items = Zenith.getInstance().getHolyWorldEventsFetcher().getItems();
      this.items2.clear();

      for (HolyWorldEvent holyWorldEvent : items) {
         String key = this.method7013(holyWorldEvent);
         this.items2.add(key);
         this.items.computeIfAbsent(key, var2x -> new EventsHud.Entry(holyWorldEvent)).setHolyWorldEvent(holyWorldEvent);
      }

      this.items.values().removeIf(EventsHud.Entry::method7017);
      if (this.items.isEmpty()) {
         this.animation2.animateTo(0.0F);
      } else {
         EventsHud.Entry entry = this.items.values().iterator().next();
         this.animation2.animateTo(this.items.size() == 1 && entry.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
      }

      float floatValue = this.floatValue;
      float y1 = this.floatValue2;
      float height = (float)(
         20.0F + GuiStyle.PADDING.intValue() / 2.0F
            + this.items.values().stream().mapToDouble(entry -> (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue()).sum()
      );
      float width = (float)this.items.values().stream().mapToDouble(EventsHud.Entry::method7014).max().orElse(0.0);
      width = this.animation.animateTo(Math.max(HudListLayout.method7638("L", "Events"), width));
      this.width = width;
      this.height = height;
      this.animation3.updateVisibility(mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
      float f = InterfaceModule.method6801();
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + height / 2.0F);
      customDrawContext.pose().scale(this.animation3.getValue(), this.animation3.getValue());
      customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + height / 2.0F));
      HudListLayout.method7635(customDrawContext, floatValue, y1, width, height);
      HudListLayout.method7636(customDrawContext, floatValue, y1, width, "L", "Events");
      if (this.animation3.getValue() == 1.0F) {
         float f2 = y1 + 20.0F;
         customDrawContext.enableScissor((int)floatValue, (int)y1, (int)(floatValue + width), (int)(y1 + height));

         for (Map.Entry entry : this.items.entrySet()) {
            EventsHud.Entry entry2 = (EventsHud.Entry)entry.getValue();
            entry2.method7016(customDrawContext, floatValue, f2, width, this.items2.contains(entry.getKey()));
            f2 += (entry2.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry2.animation.getValue();
         }

         customDrawContext.disableScissor();
      }

      customDrawContext.popMatrix();
   }

   private String method7013(HolyWorldEvent holyWorldEvent) {
      return holyWorldEvent.getType().name() + "|" + holyWorldEvent.getLongValue() + "|" + holyWorldEvent.getMessage();
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private class Entry {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private HolyWorldEvent holyWorldEvent;

      public Entry(HolyWorldEvent holyWorldEvent) {
         this.holyWorldEvent = holyWorldEvent;
      }

      public void setHolyWorldEvent(HolyWorldEvent holyWorldEvent) {
         this.holyWorldEvent = holyWorldEvent;
      }

      public float method7014() {
         return HudListLayout.method7648(this.holyWorldEvent.method7742(), this.method7015());
      }

      private String method7015() {
         String str = this.holyWorldEvent.getText2();
         return str != null && !str.isEmpty() ? str : "-";
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method7016(CustomDrawContext customDrawContext, float floatValue, float y1, float width, boolean contains) {
         this.animation.animateTo(contains ? 1.0F : 0.0F);
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + this.getHeight() / 2.0F);
         customDrawContext.pose().scale(this.animation.getValue(), this.animation.getValue());
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + this.getHeight() / 2.0F));
         float f = HudListLayout.method7639(customDrawContext, floatValue, y1, this.holyWorldEvent.getIcon());
         HudListLayout.method7644(customDrawContext, f, y1, this.holyWorldEvent.method7742());
         HudListLayout.method7646(customDrawContext, floatValue, y1, width, this.method7015());
         customDrawContext.popMatrix();
      }

      public boolean method7017() {
         return this.animation.getValue() == 0.0F;
      }
   }
}
