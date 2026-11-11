package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.network.Names;

@ModuleInfo(
   name = "AuctionScan",
   category = ModuleCategory.MISC,
   description = "module.auctionscan.description"
)
public class AuctionScanModule extends Module {
   public static final AuctionScanModule INSTANCE = new AuctionScanModule();
   private final BooleanSetting openSelf = new BooleanSetting("module.auctionScan.openSelf", true);
   private final TextSetting command = new TextSetting("module.auctionScan.command", "ah", "ah");
   private final BooleanSetting categories = new BooleanSetting("module.auctionScan.categories", true);
   private final SliderSetting pageLimit = new SliderSetting("module.auctionScan.pageLimit", 60.0F, 5.0F, 300.0F, 5.0F, "module.auctionScan.pageLimit.desc", "");
   private final SliderSetting delay = new SliderSetting("module.auctionScan.delay", 60.0F, 20.0F, 1000.0F, 10.0F, "module.auctionScan.delay.desc", "ms");
   private static final String[] texts = new String[]{"следующая", "next", "вперёд", "вперед", "▶", "»"};
   private static final String[] texts2 = new String[]{"категори", "categor"};
   private static final String[] texts3 = new String[]{"назад", "back", "◀", "«"};
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final Set<String> items = new HashSet<>();
   private final Set<Integer> items2 = new LinkedHashSet<>();
   private final Set<Integer> items3 = new HashSet<>();
   private volatile boolean flag;
   private AuctionScanModule.Type type = AuctionScanModule.Type.IDLE;
   private int intValue;
   private int intValue2;
   private int intValue3 = -1;

   private AuctionScanModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.intValue = 0;
      this.intValue3 = -1;
      this.intValue2 = Names.size();
      this.stopWatch.reset();
      this.stopWatch2.reset();
      this.flag = true;
      this.type = this.openSelf.isEnabled() ? AuctionScanModule.Type.OPEN : AuctionScanModule.Type.PAGES;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.type = AuctionScanModule.Type.IDLE;
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && this.type != AuctionScanModule.Type.IDLE && this.type != AuctionScanModule.Type.DONE) {
         Packet packet3 = event.getPacket();
         if (packet3 instanceof ClientboundContainerSetContentPacket || packet3 instanceof ClientboundContainerSetSlotPacket) {
            this.flag = true;
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      this.method1649();
   }

   @EventTarget
   public void method1648(DrawEvent event) {
      this.method1649();
   }

   private void method1649() {
      if (mc.player != null && mc.getConnection() != null) {
         if (this.stopWatch.hasElapsed((long)this.delay.getValue3())) {
            if (this.type != AuctionScanModule.Type.PAGES || this.flag || this.stopWatch2.hasElapsed(6000L)) {
               this.flag = false;
               switch (this.type) {
                  case OPEN:
                     this.method1650();
                     break;
                  case PAGES:
                     this.method1651();
                     break;
                  case CATEGORY_LIST:
                     this.method1654();
                     break;
                  case DONE:
                     this.method1655();
               }
            }
         }
      }
   }

   private void method1650() {
      String command = this.command.getValue().trim();
      if (command.isEmpty()) {
         command = "ah";
      }

      mc.getConnection().sendCommand(command);
      this.type = AuctionScanModule.Type.PAGES;
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   private void method1651() {
      AbstractContainerMenu containerMenu = mc.player.containerMenu;
      if (!this.method1657(containerMenu)) {
         if (this.stopWatch2.hasElapsed(6000L)) {
            ChatLogger.method2022("AuctionScan: окно аукциона не открылось");
            this.method1655();
         }
      } else {
         this.stopWatch2.reset();
         String str = this.method1658(containerMenu);
         boolean flag = !this.items.add(str);
         if (!flag) {
            this.intValue++;
         }

         if (this.categories.isEnabled() && this.intValue3 < 0 && this.items2.isEmpty()) {
            this.method1653(containerMenu);
         }

         if (this.intValue >= (int)this.pageLimit.getValue3()) {
            this.method1652();
         } else {
            int slotNum = this.method1659(containerMenu, texts);
            if (slotNum >= 0 && !flag) {
               this.method1656(containerMenu, slotNum);
            } else {
               this.method1652();
            }
         }
      }
   }

   private void method1652() {
      if (this.categories.isEnabled() && !this.items2.isEmpty()) {
         this.type = AuctionScanModule.Type.CATEGORY_LIST;
         this.stopWatch.reset();
      } else {
         this.method1655();
      }
   }

   private void method1653(AbstractContainerMenu containerMenu) {
      int n = this.method1659(containerMenu, texts2);
      if (n >= 0) {
         this.items2.add(n);
      }
   }

   private void method1654() {
      AbstractContainerMenu containerMenu = mc.player.containerMenu;
      if (!this.method1657(containerMenu)) {
         this.method1655();
      } else {
         Integer slotNum = this.items2.stream().filter(var1x -> !this.items3.contains(var1x)).findFirst().orElse(null);
         if (slotNum == null) {
            this.method1655();
         } else {
            this.items3.add(slotNum);
            this.items2.remove(slotNum);
            this.intValue3 = slotNum;
            this.items.clear();
            this.method1656(containerMenu, slotNum);
            this.type = AuctionScanModule.Type.PAGES;
         }
      }
   }

   private void method1655() {
      int n = Names.size() - this.intValue2;
      ChatLogger.method2019("AuctionScan: страниц " + this.intValue + ", новых записей " + n);
      Names.save();
      this.type = AuctionScanModule.Type.DONE;
      this.setToggled(false);
   }

   private void method1656(AbstractContainerMenu containerMenu, int slotNum) {
      mc.gameMode.handleContainerInput(containerMenu.containerId, slotNum, 0, ContainerInput.PICKUP, mc.player);
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   private boolean method1657(AbstractContainerMenu containerMenu) {
      return containerMenu != null && containerMenu.containerId != 0 && containerMenu.slots.size() > 36;
   }

   private String method1658(AbstractContainerMenu containerMenu) {
      StringBuilder builder = new StringBuilder();
      int n = containerMenu.slots.size() - 36;

      for (int i = 0; i < n; i++) {
         ItemStack stack = containerMenu.getSlot(i).getItem();
         if (!stack.isEmpty()) {
            builder.append(i).append(stack.getItem()).append(stack.getHoverName().getString()).append(';');
         }
      }

      return this.intValue3 + "#" + builder;
   }

   private int method1659(AbstractContainerMenu containerMenu, String[] texts) {
      int n = containerMenu.slots.size() - 36;

      for (int i = 0; i < n; i++) {
         Slot slot = containerMenu.getSlot(i);
         ItemStack stack = slot.getItem();
         if (!stack.isEmpty()) {
            String lowerCase = stack.getHoverName().getString().toLowerCase(Locale.ROOT);

            for (String text : texts) {
               if (lowerCase.contains(text)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   private static enum Type {
      IDLE,
      OPEN,
      PAGES,
      CATEGORY_LIST,
      DONE;
   }
}
