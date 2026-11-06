package org.zenith.utility.game;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.zenith.Zenith;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.api.BlockPosReference;
import org.zenith.utility.misc.CraftBinding;

public final class CraftBindingController {
   private final AutoCraftModule autoCraftModule;
   private CraftBinding craftBinding = CraftBinding.method1685();
   private boolean flag;

   public CraftBindingController(AutoCraftModule autoCraftModule) {
      this.autoCraftModule = autoCraftModule;
   }

   public boolean isBinding() {
      return !this.craftBinding.method1686();
   }

   public String method7223() {
      return this.craftBinding.method1686() ? "" : this.craftBinding.method1687(this.autoCraftModule);
   }

   public void method7224() {
      this.craftBinding = CraftBinding.method1685();
   }

   public void reset() {
      this.craftBinding = CraftBinding.method1685();
      this.method7231(true);
   }

   public void method7225(String str) {
      CraftRecipe craftRecipe = this.autoCraftModule.method1096();
      if (craftRecipe != null && str != null && !str.isBlank()) {
         this.craftBinding = new CraftBinding(CraftBinding.Type.SOURCE, craftRecipe.getText2(), craftRecipe.getId(), str);
         this.method7229();
         AutoCraftModule.mc.gui.setScreen(null);
      }
   }

   public void method7226() {
      CraftRecipe craftRecipe = this.autoCraftModule.method1096();
      if (craftRecipe != null) {
         this.craftBinding = new CraftBinding(CraftBinding.Type.OUTPUT, craftRecipe.getText2(), craftRecipe.getId(), "");
         this.method7229();
         AutoCraftModule.mc.gui.setScreen(null);
      }
   }

   public void method7227() {
      CraftRecipe craftRecipe = this.autoCraftModule.method1096();
      if (craftRecipe != null) {
         this.craftBinding = new CraftBinding(CraftBinding.Type.WORKBENCH, craftRecipe.getText2(), craftRecipe.getId(), "");
         this.method7229();
         AutoCraftModule.mc.gui.setScreen(null);
      }
   }

   public boolean method7228(MouseButtonEvent event) {
      if (this.craftBinding.method1686()) {
         return false;
      } else if (AutoCraftModule.mc.player != null && AutoCraftModule.mc.level != null) {
         if (event.getIntValue2() != 1 || event.getIntValue() != 1) {
            return true;
         } else if (AutoCraftModule.mc.gui.screen() != null) {
            return true;
         } else if (AutoCraftModule.mc.hitResult instanceof BlockHitResult blockHitResult && AutoCraftModule.mc.hitResult.getType() == Type.BLOCK) {
            CraftRecipe craftRecipe = this.autoCraftModule.method1094(this.craftBinding.getText(), this.craftBinding.getText2());
            if (craftRecipe == null) {
               this.craftBinding = CraftBinding.method1685();
               this.method7230();
               return true;
            } else {
               BlockPos blockPos = blockHitResult.getBlockPos();
               if (this.craftBinding.getType() == CraftBinding.Type.WORKBENCH
                  && AutoCraftModule.mc.level.getBlockState(blockPos).getBlock() != Blocks.CRAFTING_TABLE) {
                  this.autoCraftModule.method1128("Этот блок не является верстаком");
                  return true;
               } else if ((this.craftBinding.getType() == CraftBinding.Type.SOURCE || this.craftBinding.getType() == CraftBinding.Type.OUTPUT)
                  && !this.method7232(blockPos)) {
                  this.autoCraftModule.method1128("Этот тип сундуков пока не поддерживается");
                  return true;
               } else {
                  BlockPosReference class2 = BlockPosReference.fromBlockPos(blockPos);
                  if (this.craftBinding.getType() == CraftBinding.Type.SOURCE && !this.craftBinding.getText3().isBlank()) {
                     craftRecipe.getItems3().put(this.craftBinding.getText3(), class2);
                     this.autoCraftModule.method1106();
                     this.autoCraftModule.method1127("Сундук-источник сохранен для " + craftRecipe.method5397(this.craftBinding.getText3(), this.autoCraftModule));
                  } else if (this.craftBinding.getType() == CraftBinding.Type.OUTPUT) {
                     craftRecipe.method5407(class2);
                     this.autoCraftModule.method1127("Сундук-склад сохранен для " + craftRecipe.getText3());
                  } else if (this.craftBinding.getType() == CraftBinding.Type.WORKBENCH) {
                     craftRecipe.method5408(class2);
                     this.autoCraftModule.method1127("Верстак сохранен для " + craftRecipe.getText3());
                  }

                  this.craftBinding = CraftBinding.method1685();
                  this.method7230();
                  Zenith.getInstance().getManager().save();
                  return true;
               }
            }
         } else {
            this.autoCraftModule.method1128("Наведитесь на блок для привязки Автокрафта");
            return true;
         }
      } else {
         return true;
      }
   }

   public void method7229() {
      if (!this.flag && !this.autoCraftModule.isEnabledRaw()) {
         EventManager.register(this.autoCraftModule);
         this.flag = true;
      }
   }

   public void method7230() {
      this.method7231(false);
   }

   public void method7231(boolean flag) {
      if (this.flag) {
         if (!flag && this.autoCraftModule.isEnabledRaw()) {
            this.flag = false;
         } else {
            EventManager.unregister(this.autoCraftModule);
            this.flag = false;
         }
      }
   }

   private boolean method7232(BlockPos blockPos) {
      BlockEntity blockEntity = AutoCraftModule.mc.level.getBlockEntity(blockPos);
      return blockEntity instanceof ChestBlockEntity || blockEntity instanceof BarrelBlockEntity || blockEntity instanceof ShulkerBoxBlockEntity;
   }
}
