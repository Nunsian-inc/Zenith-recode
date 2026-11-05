package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.state.BlockState;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BlockBreakingEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.StopWatch;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoTool",
   category = ModuleCategory.MISC,
   description = "module.autotool.description"
)
public final class AutoToolModule extends Module {
   public static final AutoToolModule INSTANCE = new AutoToolModule();
   private final StopWatch stopWatch = new StopWatch();
   private Slot slot = null;

   private AutoToolModule() {
   }

   @EventTarget
   public void method261(MouseScrollEvent event) {
      if (this.slot != null) {
         event.setCancelled(true);
      }
   }

   @EventTarget
   public void method262(BlockBreakingEvent event) {
      this.stopWatch.reset();
      if (!Objects.requireNonNull(mc.player).isCreative()) {
         Slot slot = this.method264(event.getBlockPos());
         if (slot != null && slot != InventoryUtils.method924()) {
            if (this.slot == null) {
               this.slot = slot;
            }

            if (KeyBindUtils.method2630(AutoToolModule.class)) {
               KeyBindUtils.method2627(AutoToolModule.class, () -> {
                  if (!(mc.player.containerMenu instanceof InventoryMenu)) {
                     InventoryUtils.closeScreen();
                  }

                  InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true);
               });
            }
         }
      }
   }

   @EventTarget
   public void method263(UpdateEvent event) {
      if (KeyBindUtils.method2630(AutoToolModule.class) && this.slot != null && this.stopWatch.method317(400.0) && !mc.options.keyAttack.isDown()) {
         Slot slot = this.slot;
         KeyBindUtils.method2627(AutoToolModule.class, () -> {
            if (!(mc.player.containerMenu instanceof InventoryMenu)) {
               InventoryUtils.closeScreen();
            }

            InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true);
         });
         this.slot = null;
      }
   }

   @Override
   public void onDisable() {
      if (this.slot != null && mc.player != null) {
         Slot slot = this.slot;
         KeyBindUtils.method2627(AutoToolModule.class, () -> {
            if (!(mc.player.containerMenu instanceof InventoryMenu)) {
               InventoryUtils.closeScreen();
            }

            InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true);
         });
      }

      this.slot = null;
      super.onDisable();
   }

   private Slot method264(BlockPos blockPos) {
      BlockState blockState = mc.level.getBlockState(blockPos);
      return UseItemUtils.method6524(blockState)
         ? InventoryUtils.method924()
         : mc.player
            .inventoryMenu
            .slots
            .stream()
            .sorted(Comparator.comparing(slot -> slot.equals(InventoryUtils.method924())))
            .filter(var1x -> var1x.getItem().getDestroySpeed(blockState) != 1.0F)
            .max(Comparator.comparingDouble(var1x -> var1x.getItem().getDestroySpeed(blockState)))
            .orElse(null);
   }
}
