package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.HandledScreenRenderEvent;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.StopWatch;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "ItemScroller",
   description = "module.itemscroller.description",
   category = ModuleCategory.MISC
)
public final class ItemScrollerModule extends Module {
   public static final ItemScrollerModule INSTANCE = new ItemScrollerModule();
   private final SliderSetting scrollerSetting = new SliderSetting(
      "module.itemScroller.scrollerSetting", 100.0F, 0.0F, 200.0F, 10.0F, "module.itemScroller.scrollerSetting.desc", "ms"
   );
   private final StopWatch stopWatch = new StopWatch();
   private Slot slot = null;
   private boolean flag = false;

   private ItemScrollerModule() {
   }

   @Override
   public void onDisable() {
      this.slot = null;
      this.flag = false;
      super.onDisable();
   }

   @EventTarget
   public void method1757(HandledScreenRenderEvent event) {
      if (mc.player != null && mc.gameMode != null) {
         boolean flag = UseItemUtils.method6520(mc.options.keyShift.getDefaultKey());
         boolean flag2 = UseItemUtils.method6520(mc.options.keyDrop.getDefaultKey());
         boolean flag3 = UseItemUtils.method6520(mc.options.keyAttack.getDefaultKey());
         ContainerInput slotActionType = !flag ? null : (flag2 ? ContainerInput.THROW : (flag3 ? ContainerInput.QUICK_MOVE : null));
         Slot slot = event.getSlot();
         if (slotActionType == null) {
            this.flag = false;
            this.slot = null;
         } else if (!this.flag) {
            this.flag = true;
            this.slot = slot;
            this.stopWatch.reset();
         } else if (slot != null && slot != this.slot) {
            if (slot.hasItem() && slot.mayPickup(mc.player)) {
               if (mc.player.containerMenu.getCarried().isEmpty()) {
                  if (this.stopWatch.method317(this.scrollerSetting.getValue3())) {
                     this.slot = slot;
                     this.stopWatch.reset();
                     int containerId = mc.player.containerMenu.containerId;
                     int count = slotActionType == ContainerInput.THROW ? 1 : 0;
                     SlotClickEvent event2 = new SlotClickEvent(containerId, slot.index, count, slotActionType);
                     EventManager.call(event2);
                     if (!event2.isCancelled()) {
                        mc.gameMode.handleContainerInput(containerId, slot.index, count, slotActionType, mc.player);
                     }
                  }
               }
            }
         }
      }
   }
}
