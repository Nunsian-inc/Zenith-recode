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
   public void method261(MouseScrollEvent var1) {
      if (this.slot != null) {
         var1.setCancelled(true);
      }
   }

   @EventTarget
   public void method262(BlockBreakingEvent var1) {
      this.stopWatch.reset();
      if (!Objects.requireNonNull(mc.player).isCreative()) {
         Slot var2 = this.method264(var1.getBlockPos());
         if (var2 != null && var2 != InventoryUtils.method924()) {
            if (this.slot == null) {
               this.slot = var2;
            }

            if (KeyBindUtils.method2630(AutoToolModule.class)) {
               KeyBindUtils.method2627(AutoToolModule.class, () -> {
                  if (!(mc.player.containerMenu instanceof InventoryMenu)) {
                     InventoryUtils.closeScreen();
                  }

                  InventoryUtils.method900(var2, InteractionHand.MAIN_HAND, true);
               });
            }
         }
      }
   }

   @EventTarget
   public void method263(UpdateEvent var1) {
      if (KeyBindUtils.method2630(AutoToolModule.class) && this.slot != null && this.stopWatch.method317(400.0) && !mc.options.keyAttack.isDown()) {
         Slot var2 = this.slot;
         KeyBindUtils.method2627(AutoToolModule.class, () -> {
            if (!(mc.player.containerMenu instanceof InventoryMenu)) {
               InventoryUtils.closeScreen();
            }

            InventoryUtils.method900(var2, InteractionHand.MAIN_HAND, true);
         });
         this.slot = null;
      }
   }

   @Override
   public void onDisable() {
      if (this.slot != null && mc.player != null) {
         Slot var1 = this.slot;
         KeyBindUtils.method2627(AutoToolModule.class, () -> {
            if (!(mc.player.containerMenu instanceof InventoryMenu)) {
               InventoryUtils.closeScreen();
            }

            InventoryUtils.method900(var1, InteractionHand.MAIN_HAND, true);
         });
      }

      this.slot = null;
      super.onDisable();
   }

   private Slot method264(BlockPos var1) {
      BlockState var2 = mc.level.getBlockState(var1);
      return UseItemUtils.method6524(var2)
         ? InventoryUtils.method924()
         : mc.player
            .inventoryMenu
            .slots
            .stream()
            .sorted(Comparator.comparing(var0 -> var0.equals(InventoryUtils.method924())))
            .filter(var1x -> var1x.getItem().getDestroySpeed(var2) != 1.0F)
            .max(Comparator.comparingDouble(var1x -> var1x.getItem().getDestroySpeed(var2)))
            .orElse(null);
   }
}
