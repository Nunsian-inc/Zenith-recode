package org.zenith.client.hud;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.gui.screens.ChatScreen;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class KeybindsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "n";
   private static final String text2 = "Keybinds";
   private final LinkedHashSet<KeybindsHud.Entry> linkedHashSet = new LinkedHashSet<>();
   private final Set<Module> items = Collections.newSetFromMap(new IdentityHashMap<>());
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);

   public KeybindsHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
         if (moduleValue.isEnabled() && moduleValue.getKeyCode() != -1 && !this.items.contains(moduleValue)) {
            this.linkedHashSet.addLast(new KeybindsHud.Entry(moduleValue));
            this.items.add(moduleValue);
         }
      }

      var iterator = this.linkedHashSet.iterator();

      while (iterator.hasNext()) {
         KeybindsHud.Entry entry = (KeybindsHud.Entry)iterator.next();
         if ((!entry.module.isEnabled() || entry.module.getKeyCode() == -1) && entry.method6488()) {
            iterator.remove();
            this.items.remove(entry.module);
         }
      }

      if (this.linkedHashSet.isEmpty()) {
         this.animation2.method2808(0.0F);
      } else {
         this.animation2.method2808(this.linkedHashSet.size() == 1 && this.linkedHashSet.getFirst().animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
      }

      float floatValue = this.floatValue;
      float y1 = this.floatValue2;
      float height = (float)(
         20.0F + GuiStyle.PADDING.intValue() / 2.0F
            + this.linkedHashSet.stream().mapToDouble(entry -> (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue()).sum()
      );
      float width = (float)this.linkedHashSet.stream().mapToDouble(KeybindsHud.Entry::method6486).max().orElse(0.0);
      width = this.animation.method2808(Math.max(HudListLayout.method7638("n", "Keybinds"), width));
      this.width = width;
      this.height = height;
      this.animation3
         .method2807(mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.linkedHashSet.isEmpty());
      customDrawContext.pushMatrix();
      float f = InterfaceModule.method6801();
      customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + height / 2.0F);
      customDrawContext.pose().scale(this.animation3.getValue(), this.animation3.getValue());
      customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + height / 2.0F));
      HudListLayout.method7635(customDrawContext, floatValue, y1, width, height);
      HudListLayout.method7636(customDrawContext, floatValue, y1, width, "n", "Keybinds");
      if (this.animation3.getValue() == 1.0F) {
         float f2 = y1 + 20.0F;
         int n = 0;
         customDrawContext.enableScissor((int)floatValue, (int)y1, (int)(floatValue + width), (int)(y1 + height));

         for (KeybindsHud.Entry entry : this.linkedHashSet) {
            entry.method6487(customDrawContext, floatValue, f2, width, n, f);
            f2 += (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue();
            n++;
         }

         customDrawContext.disableScissor();
      }

      customDrawContext.popMatrix();
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   public class Entry implements Comparable<KeybindsHud.Entry> {
      private final Animation animation = new Animation(150L, Easing.INSTANCE13);
      private final Module module;

      public Entry(Module moduleValue) {
         this.module = moduleValue;
      }

      private String method6485() {
         return KeyName.method6702(this.module.getKeyCode(), this.module.getKeyModifiers());
      }

      public float method6486() {
         return HudListLayout.method7648(this.module.getName(), this.method6485());
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method6487(CustomDrawContext customDrawContext, float floatValue, float y1, float width, int n, float f) {
         this.animation.method2808(this.module.isEnabled() && this.module.getKeyCode() != -1 ? 1.0F : 0.0F);
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + this.getHeight() / 2.0F);
         float x = this.animation.getValue();
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + this.getHeight() / 2.0F));
         float floatValue2 = HudListLayout.method7639(customDrawContext, floatValue, y1, this.module.getCategory().getIcon());
         HudListLayout.method7644(customDrawContext, floatValue2, y1, this.module.getName());
         HudListLayout.method7646(customDrawContext, floatValue, y1, width, this.method6485());
         customDrawContext.popMatrix();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            KeybindsHud.Entry entry = (KeybindsHud.Entry)obj;
            return Objects.equals(this.module.getId(), entry.module.getId());
         } else {
            return false;
         }
      }

      public boolean method6488() {
         return this.animation.getValue() == 0.0F;
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.module.getId());
      }

      public int compareTo(KeybindsHud.Entry entry) {
         return this.module.getName().compareTo(entry.module.getName());
      }
   }
}
