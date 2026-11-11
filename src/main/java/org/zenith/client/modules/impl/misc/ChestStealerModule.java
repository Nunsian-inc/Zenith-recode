package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "ChestStealer",
   category = ModuleCategory.MISC,
   description = "module.cheststealer.description"
)
public final class ChestStealerModule extends Module {
   private final ModeSetting mode = new ModeSetting(
      "module.chestStealer.mode",
      "module.chestStealer.mode.desc",
      "module.chestStealer.mode.funTime",
      "module.chestStealer.mode.holyWorld",
      "module.chestStealer.mode.reallyWorld",
      "module.chestStealer.mode.custom"
   );
   public static final ChestStealerModule INSTANCE = new ChestStealerModule();
   private final SliderSetting startDelay = new SliderSetting(
      "module.chestStealer.startDelay", 4.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.startDelay.desc", "t"
   );
   private final SliderSetting delay = new SliderSetting("module.chestStealer.delay", 0.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.delay.desc", "t");
   private final SliderSetting maxDelay = new SliderSetting("module.chestStealer.maxDelay", 6.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.maxDelay.desc", "t");
   private final BooleanSetting closeScreen = new BooleanSetting("module.chestStealer.closeScreen", "module.chestStealer.closeScreen.desc", true);
   private final SliderSetting closeDelay = new SliderSetting(
      "module.chestStealer.closeDelay", 4.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.closeDelay.desc", "t"
   );
   private final StopWatch stealTimer = new StopWatch();
   private final StopWatch startTimer = new StopWatch();
   private final StopWatch closeTimer = new StopWatch();
   private int lastContainerId = -1;
   private boolean startDelayElapsed = false;
   private boolean waitingToClose = false;

   private ChestStealerModule() {
      this.startDelay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom"));
      this.delay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom"));
      this.maxDelay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom"));
      this.closeDelay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom") && this.closeScreen.isEnabled());
   }

   private void resetState() {
      this.lastContainerId = -1;
      this.startDelayElapsed = false;
      this.waitingToClose = false;
      this.startTimer.reset();
      this.closeTimer.reset();
   }

   private float getStartDelayTicks() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 8.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 5.0F : this.startDelay.getValue3();
      }
   }

   private float getMinStealDelayTicks() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 4.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 1.0F : this.delay.getValue3();
      }
   }

   private float getMaxStealDelayTicks() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 8.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 4.0F : this.maxDelay.getValue3();
      }
   }

   private float getCloseDelayTicks() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 7.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 0.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 2.0F : this.closeDelay.getValue3();
      }
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (mc.player.containerMenu instanceof ChestMenu chestMenu && mc.gui.screen() != null) {
         if (this.lastContainerId != chestMenu.containerId) {
            this.lastContainerId = chestMenu.containerId;
            this.startTimer.reset();
            this.closeTimer.reset();
            this.stealTimer.reset();
            this.startDelayElapsed = false;
            this.waitingToClose = false;
         }

         if (!this.startDelayElapsed) {
            if (!this.startTimer.hasElapsed((long)this.getStartDelayTicks() * 50L)) {
               return;
            }

            this.startDelayElapsed = true;
         }

         String screenTitle = mc.gui.screen().getTitle().getString();
         String auctionTitle = Zenith.getInstance().getLocalization().translate("module.chestStealer.title.auction");
         String purchasesTitle = Zenith.getInstance().getLocalization().translate("module.chestStealer.title.purchases");
         boolean isAuctionScreen = screenTitle.contains(auctionTitle) || screenTitle.contains(purchasesTitle);
         int containerSize = chestMenu.getContainer().getContainerSize();
         List<Integer> slotIndices = IntStream.range(0, containerSize).boxed().collect(Collectors.toList());
         Collections.shuffle(slotIndices);
         if (!isAuctionScreen) {
            for (int index : slotIndices) {
               Slot slot = chestMenu.getSlot(index);
               if (slot.hasItem()) {
                  long stealDelayTicks = (long)MathUtils.method2651(Math.min(this.getMinStealDelayTicks(), this.getMaxStealDelayTicks()), Math.max(this.getMinStealDelayTicks(), this.getMaxStealDelayTicks()));
                  if (this.stealTimer.hasElapsed(stealDelayTicks * 50L)) {
                     mc.gameMode.handleContainerInput(chestMenu.containerId, index, 0, ContainerInput.QUICK_MOVE, mc.player);
                     this.stealTimer.reset();
                     this.waitingToClose = false;
                     break;
                  }
               }
            }
         }

         boolean chestEmpty = this.isChestEmpty(chestMenu);
         if (this.closeScreen.isEnabled() && chestEmpty) {
            if (!this.waitingToClose) {
               this.waitingToClose = true;
               this.closeTimer.reset();
               return;
            }

            if (this.closeTimer.hasElapsed((long)this.getCloseDelayTicks() * 50L)) {
               mc.player.closeContainer();
               this.waitingToClose = false;
            }
         } else {
            this.waitingToClose = false;
         }
      } else {
         this.resetState();
      }
   }

   private boolean isChestEmpty(ChestMenu chestMenu) {
      for (int slotIndex = 0; slotIndex < (chestMenu.getContainer().getContainerSize() == 90 ? 54 : 27); slotIndex++) {
         if (chestMenu.getSlot(slotIndex).hasItem()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState();
      this.stealTimer.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetState();
      this.stealTimer.reset();
   }
}
