package org.zenith.client.hud;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class InterfaceHud extends Interface {
   private static final int CONST_intValue = 10;
   private static final float CONST_floatValue = 15.0F;
   private static final long CONST_longValue = 600000L;
   private final Deque<InterfaceHud.Entry> deque = new ArrayDeque<>();
   private final Set<String> items = new HashSet<>();
   private final Map<UUID, InterfaceHud.Entry4> items2 = new HashMap<>();
   private boolean flag = false;
   private final MultiSelectSetting types = new MultiSelectSetting("module.interface.notifications.types", "module.interface.notifications.types.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.modules", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.strength", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.armor", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.types, "module.interface.notifications.types.totems", true);
   private final ModeSetting direction = new ModeSetting(
      "module.interface.notifications.direction",
      "module.interface.notifications.direction.desc",
      "module.interface.notifications.direction.down",
      "module.interface.notifications.direction.up"
   );

   public InterfaceHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      EventManager.register(this);
   }

   public boolean method5083() {
      return this.entry.isEnabled();
   }

   public void method5084(Module moduleValue, boolean enabled) {
      this.method5085(moduleValue, enabled, 3000L);
   }

   public void method5085(Module moduleValue, boolean enabled, long l) {
      this.method5088(new InterfaceHud.Entry3(moduleValue, enabled, l));
   }

   public void method5086(String icon, Component literal) {
      this.method5087(icon, literal, 1500L);
   }

   public void method5087(String icon, Component literal, long l) {
      this.method5088(new InterfaceHud.Entry2(icon, literal, l));
   }

   private void method5088(InterfaceHud.Entry entry) {
      if (mc.isSameThread()) {
         this.deque.addLast(entry);
      } else {
         mc.execute(() -> this.deque.addLast(entry));
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      LocalPlayer player = mc.player;
      if (player != null && mc.level != null) {
         if (this.method5092(this.entry2)) {
            this.method5090(player);
         } else {
            this.flag = false;
         }

         if (this.method5092(this.entry3)) {
            this.method5091(player);
         } else {
            this.items.clear();
         }

         if (this.method5092(this.entry4)) {
            if (player.tickCount % 20 == 0) {
               this.items2.values().removeIf(InterfaceHud.Entry4::method5101);
            }
         } else {
            this.items2.clear();
         }
      } else {
         this.method5093();
      }
   }

   @EventTarget
   public void method5089(EntityStatusEvent event) {
      if (this.method5092(this.entry4) && mc.level != null) {
         ClientboundEntityEventPacket entityStatusS2CPacket = event.getEntityStatusS2CPacket();
         if (entityStatusS2CPacket.getEventId() == 35) {
            if (entityStatusS2CPacket.getEntity(mc.level) instanceof Player player && player != mc.player) {
               InterfaceHud.Entry4 entry4 = this.items2.computeIfAbsent(player.getUUID(), var1x -> new InterfaceHud.Entry4());
               entry4.method5100(player);
            }
         }
      }
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      long now = System.currentTimeMillis();
      var iterator = this.deque.iterator();
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      float floatValue = this.floatValue;
      float f = this.floatValue2;
      float f2 = 17.0F;
      int n = 0;

      while (iterator.hasNext()) {
         InterfaceHud.Entry entry = (InterfaceHud.Entry)iterator.next();
         if (!entry.flag && now - entry.longValue > entry.longValue2) {
            entry.flag = true;
            entry.animation2.animateTo(0.0F);
         }

         if (entry.flag && entry.animation2.getValue() < 0.01F) {
            iterator.remove();
         } else {
            if (!entry.flag) {
               if (entry.animation.isDone() && entry.animation.getValue() == 0.0F) {
                  entry.animation.reset(n);
               }

               entry.animation.animateTo(n);
               n++;
            }

            entry.animation2.animateTo(entry.flag ? 0.0F : 1.0F);
         }
      }

      for (InterfaceHud.Entry entry : this.deque) {
         float f3 = 6.0F;
         float f4 = entry.animation.getValue() * (f2 + f3);
         if (f4 < 100.0F) {
            entry.method5099(customDrawContext, floatValue, f + (this.direction.isSelected(0) ? f4 : -f4), font, currentStyle, f2, this);
         } else {
            entry.longValue = System.currentTimeMillis();
         }
      }
   }

   @Override
   public void method2069(CustomDrawContext customDrawContext) {
   }

   private void method5090(Player player) {
      MobEffectInstance effect = player.getEffect(MobEffects.STRENGTH);
      if (effect == null) {
         this.flag = false;
      } else {
         int n = effect.getDuration() / 20;
         if (n <= 10 && !this.flag) {
            this.flag = true;
            this.method5087("3", this.method5094(method5098("hud.notify.strength"), n + " " + method5098("hud.notify.sec")), 3500L);
         }

         if (n > 10) {
            this.flag = false;
         }
      }
   }

   private void method5091(Player player) {
      HashSet hashSet = new HashSet();

      for (ItemStack stack : ArmorSlots.method5384(player)) {
         if (stack != null && !stack.isEmpty() && stack.isDamageableItem()) {
            String descriptionId = stack.getItem().getDescriptionId();
            hashSet.add(descriptionId);
            int maxDamage = stack.getMaxDamage();
            if (maxDamage > 0) {
               int n = maxDamage - stack.getDamageValue();
               float f = n * 100.0F / maxDamage;
               if (f <= 15.0F && !this.items.contains(descriptionId)) {
                  this.items.add(descriptionId);
                  this.method5087(
                     "3",
                     this.method5094(method5098("hud.notify.durability"), stack.getItem().getName(stack.getItem().getDefaultInstance()).getString() + " • " + String.format("%.1f", f) + "%"),
                     3500L
                  );
               }

               if (f > 15.0F) {
                  this.items.remove(descriptionId);
               }
            }
         }
      }

      this.items.retainAll(hashSet);
   }

   private boolean method5092(MultiSelectSetting.Entry entry) {
      return entry.isEnabled() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814() && !UseItemUtils.method6526();
   }

   private void method5093() {
      this.flag = false;
      this.items.clear();
      this.items2.clear();
   }

   private Component method5094(String name, String str) {
      return this.method5096(name, Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287())
         .append(Component.literal(" "))
         .append(this.method5096(str, Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()));
   }

   private Component method5095(Player player, int intValue) {
      return this.method5096(
            player.getGameProfile().name(), Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()
         )
         .append(
            this.method5096(
               " " + method5098("hud.notify.lost") + " ",
               Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()
            )
         )
         .append(this.method5096(intValue + " ", Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
         .append(
            this.method5096(this.method5097(intValue) + ".", Zenith.getInstance().getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287())
         );
   }

   private MutableComponent method5096(String name, int color) {
      return Component.literal(name).setStyle(Style.EMPTY.withColor(color));
   }

   private String method5097(int intValue) {
      int n = intValue % 100;
      if (n >= 11 && n <= 14) {
         return method5098("hud.notify.totems.5");
      } else {
         return switch (intValue % 10) {
            case 1 -> method5098("hud.notify.totems.1");
            case 2, 3, 4 -> method5098("hud.notify.totems.2");
            default -> method5098("hud.notify.totems.5");
         };
      }
   }

   private static String method5098(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private abstract static class Entry {
      public long longValue;
      public final long longValue2;
      public boolean flag = false;
      public final Animation animation = new Animation(300L, Easing.INSTANCE13);
      public final Animation animation2 = new Animation(300L, Easing.INSTANCE13);

      public Entry(long l) {
         this.longValue = System.currentTimeMillis();
         this.longValue2 = l;
      }

      public abstract void method5099(CustomDrawContext customDrawContext, float floatValue, float f, Font font, ZenithStyle currentStyle, float f2, InterfaceHud interfaceHud);
   }

   private static class Entry2 extends InterfaceHud.Entry {
      public final String text;
      public final Component text2;

      public Entry2(String text, Component literal, long l) {
         super(l);
         this.text = text;
         this.text2 = literal;
      }

      @Override
      public void method5099(CustomDrawContext customDrawContext, float floatValue, float y, Font font, ZenithStyle zenithStyle, float height, InterfaceHud interfaceHud) {
         float intValue = InterfaceModule.method6801();
         float width = 16.0F;
         ZenithColor zenithColor = zenithStyle.getHeaderHudBackground().getZenithColor();
         ZenithColor zenithColor2 = zenithStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778());
         ZenithColor zenithColor3 = zenithStyle.getPrimaryColor().getZenithColor();
         float f = font.width(this.text2);
         float width2 = width + 4.0F + f + 8.0F;
         interfaceHud.height = height;
         interfaceHud.width = Math.max(interfaceHud.width, width2);
         floatValue += (100.0F - width2) / 2.0F;
         float x = this.animation2.getValue();
         Font font2 = Fonts.ICONS.getFont(this.text.equals("Y") ? 8.0F : 6.0F);
         customDrawContext.pose().pushMatrix();
         customDrawContext.pose().translate(floatValue + width2 / 2.0F, y + height / 2.0F);
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(floatValue + width2 / 2.0F), -(y + height / 2.0F));
         customDrawContext.drawBlurHud(floatValue, y, width2, height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
         customDrawContext.drawRoundedRect(floatValue, y, width2, height, ProxyDotRadiusData.method8069(intValue), zenithColor);
         customDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(intValue), zenithColor2);
         float floatValue2 = floatValue + (width - font2.width(this.text)) / 2.0F;
         float centeredTextY = y + (height - font2.height()) / 2.0F;
         customDrawContext.drawText(font2, this.text, floatValue2, centeredTextY, zenithColor3);
         float floatValue3 = floatValue + width + 4.0F;
         float y2 = y + (height - font.height()) / 2.0F;
         customDrawContext.drawText(font, this.text2, floatValue3, y2);
         customDrawContext.pose().popMatrix();
         interfaceHud.method4679(customDrawContext, floatValue, y, width2, height, x);
      }
   }

   private static class Entry3 extends InterfaceHud.Entry {
      public final Module module;
      public final boolean flag;

      public Entry3(Module moduleValue, boolean flag, long l) {
         super(l);
         this.module = moduleValue;
         this.flag = flag;
      }

      @Override
      public void method5099(CustomDrawContext customDrawContext, float floatValue, float y, Font font, ZenithStyle zenithStyle, float height, InterfaceHud interfaceHud) {
         float intValue = InterfaceModule.method6801();
         float width = 16.0F;
         ZenithColor zenithColor = zenithStyle.getHeaderHudBackground().getZenithColor();
         ZenithColor zenithColor2 = zenithStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778());
         ZenithColor zenithColor3 = this.flag ? zenithStyle.getPrimaryColor().getZenithColor() : zenithStyle.getTextSecondary().getZenithColor();
         ZenithColor zenithColor4 = this.flag ? zenithStyle.getTextEnable().getZenithColor() : zenithStyle.getTextTertiary().getZenithColor();
         String name = this.module.getName();
         String text = "  "
            + Zenith.getInstance()
               .getLocalization()
               .translate(this.flag ? "module.interface.notifications.state.enabled" : "module.interface.notifications.state.disabled");
         float f = font.width(name);
         float width2 = font.width(text);
         float width3 = width + 4.0F + f + width2 + 8.0F;
         interfaceHud.height = height;
         interfaceHud.width = Math.max(interfaceHud.width, width3);
         floatValue += (100.0F - width3) / 2.0F;
         float x = this.animation2.getValue();
         Font font2 = Fonts.NEW_ICONS.getFont(6.0F);
         customDrawContext.pose().pushMatrix();
         customDrawContext.pose().translate(floatValue + width3 / 2.0F, y + height / 2.0F);
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(floatValue + width3 / 2.0F), -(y + height / 2.0F));
         customDrawContext.drawBlurHud(floatValue, y, width3, height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
         customDrawContext.drawRoundedRect(floatValue, y, width3, height, ProxyDotRadiusData.method8069(intValue), zenithColor);
         customDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(intValue), zenithColor2);
         String icon = this.module.getCategory().getIcon();
         float floatValue2 = floatValue + (width - font2.width(icon)) / 2.0F;
         float centeredTextY = y + (height - font2.height()) / 2.0F;
         customDrawContext.drawText(font2, icon, floatValue2, centeredTextY, zenithColor3);
         float floatValue3 = floatValue + width + 4.0F;
         float centeredTextY2 = y + (height - font.height()) / 2.0F;
         customDrawContext.drawText(font, name, floatValue3, centeredTextY2, zenithColor3);
         customDrawContext.drawText(font, text, floatValue3 + f, centeredTextY2, zenithColor4);
         customDrawContext.pose().popMatrix();
         interfaceHud.method4679(customDrawContext, floatValue, y, width3, height, x);
      }
   }

   private class Entry4 {
      private long longValue = System.currentTimeMillis();
      private int intValue = 0;

      private void method5100(Player player) {
         this.longValue = System.currentTimeMillis();
         this.intValue++;
         InterfaceHud.this.method5087("Y", InterfaceHud.this.method5095(player, this.intValue), 3000L);
      }

      private boolean method5101() {
         return System.currentTimeMillis() - this.longValue > 600000L;
      }
   }
}
