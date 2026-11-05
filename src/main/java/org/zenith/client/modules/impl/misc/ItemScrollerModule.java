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
   public void method1757(HandledScreenRenderEvent var1) {
      if (mc.player != null && mc.gameMode != null) {
         boolean var2 = UseItemUtils.method6520(mc.options.keyShift.getDefaultKey());
         boolean var3 = UseItemUtils.method6520(mc.options.keyDrop.getDefaultKey());
         boolean var4 = UseItemUtils.method6520(mc.options.keyAttack.getDefaultKey());
         ContainerInput var5 = !var2 ? null : (var3 ? ContainerInput.THROW : (var4 ? ContainerInput.QUICK_MOVE : null));
         Slot var6 = var1.getSlot();
         if (var5 == null) {
            this.flag = false;
            this.slot = null;
         } else if (!this.flag) {
            this.flag = true;
            this.slot = var6;
            this.stopWatch.reset();
         } else if (var6 != null && var6 != this.slot) {
            if (var6.hasItem() && var6.mayPickup(mc.player)) {
               if (mc.player.containerMenu.getCarried().isEmpty()) {
                  if (this.stopWatch.method317(this.scrollerSetting.getValue3())) {
                     this.slot = var6;
                     this.stopWatch.reset();
                     int var7 = mc.player.containerMenu.containerId;
                     int var8 = var5 == ContainerInput.THROW ? 1 : 0;
                     SlotClickEvent var9 = new SlotClickEvent(var7, var6.index, var8, var5);
                     EventManager.call(var9);
                     if (!var9.isCancelled()) {
                        mc.gameMode.handleContainerInput(var7, var6.index, var8, var5, mc.player);
                     }
                  }
               }
            }
         }
      }
   }
}
