package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ItemNbtUtils;

@ModuleInfo(
   name = "AH Helper",
   category = ModuleCategory.PLAYER,
   description = "module.ahhelper.description"
)
public final class AHHelperModule extends Module {
   public static final AHHelperModule INSTANCE = new AHHelperModule();
   private final ModeSetting serverMode = new ModeSetting(
      "module.ahHelper.serverMode",
      "module.ahHelper.serverMode.desc",
      "module.ahHelper.serverAuto",
      "module.ahHelper.serverHolyWorld",
      "module.ahHelper.serverFunTime"
   );
   private final BindSetting sellKeyHw = new BindSetting("module.ahHelper.sellKeyHw", "module.ahHelper.sellKeyHw.desc", -1, this::method4053);
   private final ModeSetting priceFind = new ModeSetting(
      "module.ahHelper.priceFind", "module.ahHelper.priceFind.desc", this::method4053, "module.ahHelper.priceFindAutoSell", "module.ahHelper.priceFindSearch"
   );
   private final SliderSetting discount = new SliderSetting(
      "module.ahHelper.discount", 0.95F, 0.1F, 1.0F, 0.01F, "module.ahHelper.discount.desc", "x", this::method4053, null
   );
   private final BooleanSetting autoConfirm = new BooleanSetting("module.ahHelper.autoConfirm", "module.ahHelper.autoConfirm.desc", true, this::method4053);
   private final BooleanSetting fixCount = new BooleanSetting("module.ahHelper.fixCount", "module.ahHelper.fixCount.desc", true, this::method4053);
   private final ColorSetting colorSetting = new ColorSetting(
      "module.ahHelper.cheapSlotColor", "module.ahHelper.cheapSlotColor.desc", new ZenithColor(64, 255, 64, 140)
   );
   private final ColorSetting colorSetting2 = new ColorSetting(
      "module.ahHelper.goodSlotColor", "module.ahHelper.goodSlotColor.desc", new ZenithColor(255, 255, 64, 140)
   );
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private AHHelperModule.Type type = AHHelperModule.Type.IDLE;
   private ItemStack itemStack = ItemStack.EMPTY;
   private long longValue = -1L;
   private int intValue = 1;

   private AHHelperModule() {
   }

   @Override
   public void onEnable() {
      this.method4055();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method4055();
      super.onDisable();
   }

   @EventTarget
   public void method4042(KeyEvent event) {
      if (this.isEnabled() && this.method4053() && this.sellKeyHw.getKeyCode() != -1) {
         if (event.isPress(this.sellKeyHw.getKeyCode())) {
            this.method4045();
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.isEnabled() && mc.player != null && mc.getConnection() != null) {
         switch (this.type) {
            case WAIT_PRICE_SCREEN:
               this.method4047();
               break;
            case WAIT_SELL_COMMAND:
               this.method4050();
               break;
            case WAIT_CONFIRM:
               if (this.stopWatch.method7546(5000L)) {
                  this.method4055();
               }
         }
      }
   }

   @EventTarget
   public void getInstance(ChatReceiveEvent event) {
      if ((this.autoConfirm.isVisible() && this.autoConfirm.isEnabled() || this.type == AHHelperModule.Type.WAIT_CONFIRM)
         && event.getText().getString().startsWith("▶ Введите /ah sell auto confirm,")) {
         mc.getConnection().sendCommand("ah sell auto confirm");
         this.type = AHHelperModule.Type.IDLE;
      }
   }

   public void method4043(GuiGraphicsExtractor guiGraphicsExtractor, Slot lowSumSlotId) {
      this.method4044(guiGraphicsExtractor, 0, 0, lowSumSlotId, true);
   }

   public void getInstance(GuiGraphicsExtractor guiGraphicsExtractor, Slot lowSumSlotId) {
      this.method4044(guiGraphicsExtractor, 0, 0, lowSumSlotId, false);
   }

   public void method4044(GuiGraphicsExtractor guiGraphicsExtractor, int leftPos, int topPos, Slot lowSumSlotId, boolean flag) {
      int col = flag ? this.colorSetting.method1445() : this.colorSetting2.method1445();
      int x0 = leftPos + lowSumSlotId.x;
      int y0 = topPos + lowSumSlotId.y;
      guiGraphicsExtractor.fill(x0, y0, x0 + 16, y0 + 16, col);
   }

   private void method4045() {
      if (this.type == AHHelperModule.Type.IDLE && this.method4053()) {
         ItemStack mainHandItem = mc.player.getMainHandItem();
         if (mainHandItem != null && !mainHandItem.isEmpty()) {
            this.itemStack = mainHandItem.copy();
            this.longValue = -1L;
            this.intValue = 1;
            this.method4046();
            this.stopWatch.reset();
            this.stopWatch2.reset();
         } else {
            ChatLogger.method2022("Возьми предмет в руку");
         }
      }
   }

   private void method4046() {
      if (mc.getConnection() != null && this.method4053()) {
         if (this.priceFind.isSelected(0)) {
            mc.getConnection().sendCommand("ah sell auto");
            this.type = AHHelperModule.Type.WAIT_CONFIRM;
         } else {
            mc.getConnection().sendCommand("ah search");
            this.type = AHHelperModule.Type.WAIT_PRICE_SCREEN;
         }
      }
   }

   private void method4047() {
      if (this.stopWatch2.method7546(250L)) {
         if (!this.method4049()) {
            if (this.stopWatch.method7546(5000L)) {
               this.method4048("Аукцион не открылся");
            }
         } else {
            Long l = this.method4051();
            if (l == null) {
               if (this.stopWatch.method7546(5000L)) {
                  this.method4048("Не удалось найти цену на аукционе");
               }
            } else {
               long l2 = l;
               this.longValue = Math.max(
                  1L, l2 <= 2147483647L ? Math.round((int)l2 * this.discount.getValue3()) : Math.round((double)l2 * this.discount.getValue3())
               );
               this.closeScreen();
               ChatLogger.method2019("Найдена цена: " + this.longValue);
               this.type = AHHelperModule.Type.WAIT_SELL_COMMAND;
               this.stopWatch.reset();
               this.stopWatch2.reset();
            }
         }
      }
   }

   private void method4048(String status) {
      ChatLogger.method2022(status);
      this.closeScreen();
      this.method4055();
   }

   private void closeScreen() {
      if (mc.player != null && mc.gui.screen() != null) {
         mc.player.closeContainer();
      }
   }

   private boolean method4049() {
      return mc.player != null && mc.player.containerMenu != null && ItemNbtUtils.method81(mc.player.containerMenu);
   }

   private void method4050() {
      if (this.longValue > 0L && mc.getConnection() != null) {
         ItemStack mainHandItem = mc.player.getMainHandItem();
         if (mainHandItem.isEmpty() || !ItemStack.isSameItemSameComponents(mainHandItem, this.itemStack)) {
            this.method4048("Держи тот же предмет в руке");
         } else if (mc.gui.screen() == null || this.stopWatch.method7546(1500L)) {
            if (this.stopWatch2.method7546(250L)) {
               mc.getConnection().sendCommand("ah sell " + this.longValue);
               this.method4055();
            }
         }
      } else {
         this.method4055();
      }
   }

   private Long method4051() {
      if (!this.method4049()) {
         return null;
      } else {
         ArrayList arrayList = new ArrayList();
         int n = Math.min(45, mc.player.containerMenu.slots.size());

         for (int i = 0; i < n; i++) {
            Slot slot = mc.player.containerMenu.getSlot(i);
            ItemStack stack = slot.getItem();
            long l;
            if (!stack.isEmpty() && !ItemNbtUtils.method87(stack) && (l = this.method4052(stack)) != Long.MAX_VALUE) {
               arrayList.add((int)Math.min(2147483647L, l));
            }
         }

         if (arrayList.isEmpty()) {
            return null;
         } else {
            arrayList.sort(Comparator.naturalOrder());
            int index = Math.min(Math.max(0, this.intValue - 1), arrayList.size() - 1);
            return (long)((Integer)arrayList.get(index)).intValue() * Math.max(1, this.itemStack.getCount());
         }
      }
   }

   private long method4052(ItemStack stack) {
      int n;
      if ((n = ItemNbtUtils.method62(stack)) == (32767 << 16 | (char)-1)) {
         return Long.MAX_VALUE;
      } else {
         ItemLore itemLore = stack.get(DataComponents.LORE);
         return itemLore != null && itemLore.styledLines().stream().map(Component::getString).collect(Collectors.joining()).contains("только полностью.")
            ? Long.MAX_VALUE
            : n / stack.getCount();
      }
   }

   private boolean method4053() {
      return zenith.getServerDetector().method1642();
   }

   public boolean method4054() {
      return this.fixCount.isEnabled() && this.fixCount.isVisible();
   }

   private void method4055() {
      this.type = AHHelperModule.Type.IDLE;
      this.itemStack = ItemStack.EMPTY;
      this.longValue = -1L;
      this.intValue = 1;
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   @Generated
   public ModeSetting getServerMode() {
      return this.serverMode;
   }

   @Generated
   public BindSetting getSellKeyHw() {
      return this.sellKeyHw;
   }

   @Generated
   public ModeSetting getPriceFind() {
      return this.priceFind;
   }

   @Generated
   public SliderSetting getDiscount() {
      return this.discount;
   }

   @Generated
   public BooleanSetting getAutoConfirm() {
      return this.autoConfirm;
   }

   @Generated
   public BooleanSetting getFixCount() {
      return this.fixCount;
   }

   @Generated
   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   @Generated
   public ColorSetting getColorSetting2() {
      return this.colorSetting2;
   }

   @Generated
   public StopWatch getStopWatch2() {
      return this.stopWatch;
   }

   @Generated
   public StopWatch getStopWatch22() {
      return this.stopWatch2;
   }

   @Generated
   public AHHelperModule.Type getType() {
      return this.type;
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public long getLongValue3() {
      return this.longValue;
   }

   @Generated
   public int getIntValue16() {
      return this.intValue;
   }

   private static enum Type {
      IDLE,
      WAIT_PRICE_SCREEN,
      WAIT_SELL_COMMAND,
      WAIT_CONFIRM;
   }
}
