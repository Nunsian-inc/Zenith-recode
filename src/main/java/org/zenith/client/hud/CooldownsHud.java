package org.zenith.client.hud;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class CooldownsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "n";
   private static final String text2 = "Cooldown";
   private final Map<String, CooldownsHud.Entry> items = new LinkedHashMap<>();
   private final Map<String, CooldownsHud.Entry2> items2 = new LinkedHashMap<>();
   private final Map<String, CooldownsHud.Entry2> items3 = new LinkedHashMap<>();
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);

   public CooldownsHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      EventManager.register(this);
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc != null && mc.level != null) {
         if (event.getPacket3() instanceof ClientboundCooldownPacket cooldownPacket) {
            this.method6829(() -> this.method6830(cooldownPacket));
         } else if (event.getPacket3() instanceof ClientboundRespawnPacket) {
            this.method6829(() -> {
               this.items3.clear();
               this.items2.clear();
               this.items.clear();
            });
         }
      }
   }

   private void method6829(Runnable command) {
      if (mc.isSameThread()) {
         command.run();
      } else {
         mc.execute(command);
      }
   }

   private void method6830(ClientboundCooldownPacket cooldownPacket) {
      if (mc.level != null) {
         Identifier cooldownGroup = cooldownPacket.cooldownGroup();
         int duration = cooldownPacket.duration();
         String text = "vanilla:" + cooldownGroup;
         if (duration <= 0) {
            this.items3.remove(text);
            this.items.remove(text);
         } else {
            String string;
            ItemStack defaultInstance;
            if (BuiltInRegistries.ITEM.containsKey(cooldownGroup)) {
               Item item = BuiltInRegistries.ITEM.getValue(cooldownGroup);
               string = item.getName(item.getDefaultInstance()).getString();
               defaultInstance = item.getDefaultInstance();
            } else {
               ItemStack stack = this.method6831(cooldownGroup);
               if (stack != null) {
                  string = stack.getHoverName().getString();
                  defaultInstance = stack.copyWithCount(1);
               } else {
                  string = cooldownGroup.getPath();
                  defaultInstance = ItemStack.EMPTY;
               }
            }

            long gameTime = mc.level.getGameTime();
            this.items3.put(text, CooldownsHud.Entry2.method6848(text, string, defaultInstance, gameTime, duration));
         }
      }
   }

   private ItemStack method6831(Identifier cooldownGroup) {
      if (mc.player == null) {
         return null;
      } else {
         Inventory inventory = mc.player.getInventory();

         for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.has(DataComponents.USE_COOLDOWN) && cooldownGroup.equals(mc.player.getCooldowns().getCooldownGroup(stack))) {
               return stack;
            }
         }

         return null;
      }
   }

   @EventTarget
   public void method6832(PacketEvent event) {
      if (mc != null && mc.player != null) {
         ItemStack useItem = mc.player.getUseItem();
         if (useItem != null && !useItem.isEmpty()) {
            if (mc.player.getTicksUsingItem() >= useItem.getUseDuration(mc.player)) {
               PotionContents potionContents = useItem.get(DataComponents.POTION_CONTENTS);
               if (potionContents != null && (potionContents.getColor() == 33461 || potionContents.getColor() == -515037)) {
                  ItemStack defaultInstance = Items.POTION.getDefaultInstance();
                  defaultInstance.set(
                     DataComponents.POTION_CONTENTS,
                     new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(potionContents.getColor()), List.of(), Optional.empty())
                  );
                  this.method6834(defaultInstance, "Исцел", 10000L);
               }
            }
         }
      }
   }

   public void method6833(ItemStack defaultInstance, long l) {
      String text = "custom:" + defaultInstance.getItem().getDescriptionId();
      this.method6835(text, defaultInstance.getItem().getName(defaultInstance.getItem().getDefaultInstance()).getString(), defaultInstance, l);
   }

   public void method6834(ItemStack defaultInstance, String string, long l) {
      this.method6835("custom:" + string, string, defaultInstance, l);
   }

   public void method6835(String text, String string, ItemStack defaultInstance, long l) {
      if (mc != null) {
         if (!mc.isSameThread()) {
            mc.execute(() -> this.method6835(text, string, defaultInstance, l));
         } else if (!this.items.containsKey(text)) {
            long nanoTime = System.nanoTime();
            this.items2.put(text, CooldownsHud.Entry2.method6849(text, string, defaultInstance, nanoTime, Math.max(1L, l) * 1000000L));
         }
      }
   }

   public void method6836(String text, String string, ItemStack defaultInstance, long l) {
      if (mc != null) {
         if (!mc.isSameThread()) {
            mc.execute(() -> this.method6836(text, string, defaultInstance, l));
         } else if (mc.level != null && !this.items.containsKey(text)) {
            long gameTime = mc.level.getGameTime();
            this.items2.put(text, CooldownsHud.Entry2.method6848(text, string, defaultInstance, gameTime, Math.max(1L, l)));
         }
      }
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      if (mc != null) {
         this.method6837();
         if (this.items.isEmpty()) {
            this.animation2.method2808(0.0F);
         } else {
            CooldownsHud.Entry entry = this.items.values().iterator().next();
            this.animation2.method2808(this.items.size() == 1 && entry.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
         }

         float floatValue = this.floatValue;
         float y1 = this.floatValue2;
         float height = (float)(
            20.0F + GuiStyle.PADDING.intValue() / 2.0F
               + this.items.values().stream().mapToDouble(entry -> (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue()).sum()
         );
         float width = (float)this.items.values().stream().mapToDouble(CooldownsHud.Entry::method6842).max().orElse(0.0);
         width = this.animation.method2808(Math.max(HudListLayout.method7638("n", "Cooldown"), width));
         this.width = width;
         this.height = height;
         float f = InterfaceModule.method6801();
         this.animation3.method2807(mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + height / 2.0F);
         customDrawContext.pose().scale(this.animation3.getValue(), this.animation3.getValue());
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + height / 2.0F));
         HudListLayout.method7635(customDrawContext, floatValue, y1, width, height);
         HudListLayout.method7636(customDrawContext, floatValue, y1, width, "n", "Cooldown");
         if (this.animation3.getValue() == 1.0F) {
            float y12 = y1 + 20.0F;
            customDrawContext.enableScissor((int)floatValue, (int)y1, (int)(floatValue + width), (int)(y1 + height));

            for (CooldownsHud.Entry entry : this.items.values()) {
               entry.method6843(customDrawContext, floatValue, y12, width);
               y12 += (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue();
            }

            customDrawContext.disableScissor();
         }

         this.items.entrySet().removeIf(entry -> entry.getValue().method6844());
         this.method6839();
         customDrawContext.popMatrix();
      }
   }

   private void method6837() {
      long l = this.method6840();

      for (CooldownsHud.Entry2 entry2 : this.items3.values()) {
         if (!entry2.method6851(l)) {
            this.items.computeIfAbsent(entry2.text, str -> this.method2070(entry2));
         }
      }

      for (CooldownsHud.Entry2 entry2 : this.items2.values()) {
         long l2 = this.method6841(entry2.type);
         if (!entry2.method6851(l2)) {
            this.items.computeIfAbsent(entry2.text, str -> this.method2070(entry2));
         }
      }
   }

   private CooldownsHud.Entry method2070(CooldownsHud.Entry2 entry2) {
      long l = this.method6841(entry2.type);
      return new CooldownsHud.Entry(
         entry2.itemStack,
         entry2.text2,
         entry2.method6850(l),
         () -> entry2.method6850(this.method6841(entry2.type)),
         () -> entry2.method6851(this.method6841(entry2.type)),
         () -> entry2.method6852(this.method6841(entry2.type))
      );
   }

   private void method6839() {
      long l = this.method6840();
      this.items3.entrySet().removeIf(entry -> entry.getValue().method6851(l));
      this.items2.entrySet().removeIf(var1x -> var1x.getValue().method6851(this.method6841(var1x.getValue().type)));
   }

   private long method6840() {
      return mc != null && mc.level != null ? mc.level.getGameTime() : 0L;
   }

   private long method6841(CooldownsHud.Type type) {
      return type == CooldownsHud.Type.TICKS ? this.method6840() : 0L;
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private class Entry {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private final Animation animation2;
      private final ItemStack itemStack;
      private final String text;
      private final Supplier<Float> supplier;
      private final BooleanSupplier booleanSupplier;
      private final Supplier<Integer> supplier2;
      private int intValue;
      private String text2;

      public Entry(ItemStack itemStack, String text, float floatValue, Supplier<Float> supplier, BooleanSupplier booleanSupplier, Supplier<Integer> supplier2) {
         this.itemStack = itemStack;
         this.text = text;
         this.supplier = supplier;
         this.booleanSupplier = booleanSupplier;
         this.supplier2 = supplier2;
         this.animation2 = new Animation(200L, floatValue, Easing.INSTANCE10);
      }

      public float method6842() {
         return HudListLayout.method7651(this.text, this.method6846());
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method6843(CustomDrawContext customDrawContext, float floatValue, float y1, float width) {
         float f = this.method6845();
         this.animation.method2808(f > 0.0F ? 1.0F : 0.0F);
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + this.getHeight() / 2.0F);
         customDrawContext.pose().scale(this.animation.getValue(), this.animation.getValue());
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + this.getHeight() / 2.0F));
         float floatValue2 = HudListLayout.method7640(customDrawContext, floatValue, y1);
         float f2 = 8.0F;
         float x = f2 / 16.0F;
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(HudListLayout.method7641(floatValue) + (10.0F - f2) / 2.0F, HudListLayout.method7642(y1) + (10.0F - f2) / 2.0F);
         customDrawContext.pose().scale(x, x);
         customDrawContext.item(this.itemStack, 0, 0);
         customDrawContext.popMatrix();
         HudListLayout.method7644(customDrawContext, floatValue2, y1, this.text);
         HudListLayout.method7649(customDrawContext, floatValue, y1, width, this.method6846(), f);
         customDrawContext.popMatrix();
      }

      public boolean method6844() {
         return this.animation.getValue() == 0.0F && this.booleanSupplier.getAsBoolean();
      }

      private float method6845() {
         float floatValue = Mth.clamp(this.supplier.get(), 0.0F, 1.0F);
         this.animation2.method2808(floatValue);
         return Mth.clamp(this.animation2.getValue(), 0.0F, 1.0F);
      }

      private String method6846() {
         int intValue = this.supplier2.get();
         if (this.text2 == null || intValue != this.intValue) {
            this.intValue = intValue;
            this.text2 = this.method6847(intValue);
         }

         return this.text2;
      }

      private String method6847(int intValue) {
         if (intValue < 0) {
            intValue = 0;
         }

         int n = intValue / 60;
         int n2 = intValue % 60;
         return String.format("%d:%02d", n, n2);
      }
   }

   private static class Entry2 {
      public final String text;
      public final String text2;
      public final ItemStack itemStack;
      public final CooldownsHud.Type type;
      public final long longValue;
      public final long longValue2;

      private Entry2(String text, String string, ItemStack itemStack, CooldownsHud.Type type, long longValue, long duration) {
         this.text = text;
         this.text2 = string;
         this.itemStack = itemStack;
         this.type = type;
         this.longValue = longValue;
         this.longValue2 = duration;
      }

      public static CooldownsHud.Entry2 method6848(String text, String string, ItemStack defaultInstance, long gameTime, long duration) {
         return new CooldownsHud.Entry2(text, string, defaultInstance, CooldownsHud.Type.TICKS, gameTime, duration);
      }

      public static CooldownsHud.Entry2 method6849(String text, String string, ItemStack defaultInstance, long nanoTime, long duration) {
         return new CooldownsHud.Entry2(text, string, defaultInstance, CooldownsHud.Type.REAL, nanoTime, duration);
      }

      public float method6850(long l) {
         long l2 = switch (this.type) {
            case TICKS -> Math.max(0L, this.longValue + this.longValue2 - l);
            case REAL -> Math.max(0L, this.longValue + this.longValue2 - System.nanoTime());
         };
         return this.longValue2 <= 0L ? 0.0F : (float)l2 / (float)this.longValue2;
      }

      public boolean method6851(long l) {
         return switch (this.type) {
            case TICKS -> l >= this.longValue + this.longValue2;
            case REAL -> System.nanoTime() >= this.longValue + this.longValue2;
         };
      }

      public int method6852(long l) {
         long l2 = switch (this.type) {
            case TICKS -> Math.max(0L, this.longValue + this.longValue2 - l);
            case REAL -> Math.max(0L, this.longValue + this.longValue2 - System.nanoTime());
         };
         return this.type == CooldownsHud.Type.TICKS ? (int)(l2 / 20L) : (int)(l2 / 1000000000L);
      }
   }

   private static enum Type {
      TICKS,
      REAL;
   }
}
