package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.collections.EmptyList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.CloseHandledScreenEvent;
import org.zenith.client.events.Event1;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "Gui Walk",
   category = ModuleCategory.MOVEMENT,
   description = "module.guiwalk.description"
)
public final class GuiWalkModule extends Module {
   public static final GuiWalkModule INSTANCE = new GuiWalkModule();
   private final ModeSetting mode = new ModeSetting("module.guiWalk.mode", "module.guiWalk.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.guiWalk.mode.visual").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.guiWalk.mode.grim");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "HolyWorld");
   private final BooleanSetting cameraLock = new BooleanSetting("module.guiWalk.cameraLock", "module.guiWalk.cameraLock.desc", true, this.entry2::isSelected);
   private ContainerInput slotActionType = null;
   private final List<Packet<?>> items = new ArrayList<>();
   public boolean flag = false;
   public boolean flag2 = false;
   public boolean flag3 = false;

   private GuiWalkModule() {
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (!this.entry3.isSelected()) {
         try {
            if (UseItemUtils.method6526()) {
               return;
            }

            if (mc.player.containerMenu instanceof InventoryMenu) {
               if (!this.flag) {
                  return;
               }

               this.flag = false;
               if (Objects.requireNonNull(var1.getPacket3()) instanceof ServerboundContainerClickPacket var2
                  && ((this.items != null && this.items.getClass() == EmptyList.class ? false : !this.items.isEmpty()) || MovementInputUtils.method7418())
                  && KeyBindUtils.method2635()) {
                  this.items.add(var2);
                  this.slotActionType = var2.containerInput();
                  var1.setCancelled(true);
               }
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method1665(UpdateEvent var1) {
      if (mc.player.containerMenu instanceof InventoryMenu) {
         if (KeyBindUtils.method2635()
            && (!this.entry.isSelected() || !this.flag2)
            && !this.flag3
            && (
               (this.items != null && this.items.getClass() == EmptyList.class ? false : !this.items.isEmpty())
                  || mc.player.containerMenu.getCarried().isEmpty()
            )) {
            KeyBindUtils.method2634();
         }
      } else {
         this.flag2 = false;
         this.flag3 = false;
      }
   }

   @EventTarget
   public void method1666(SlotClickEvent var1) {
      if (var1.getIntValue2() >= 0) {
         Slot var2 = mc.player.containerMenu.getSlot(var1.getIntValue2());
         ItemStack var3 = var2.getItem();
         if (var3.get(DataComponents.CUSTOM_DATA) != null) {
         }
      }

      if (!this.entry3.isSelected()) {
         if (mc.player.containerMenu instanceof InventoryMenu) {
            ContainerInput var4 = var1.getSlotActionType();
            if (this.entry.isSelected()) {
               if (MovementInputUtils.method7418()) {
                  var1.setCancelled(true);
               } else {
                  this.flag2 = true;
               }
            }

            if ((this.items != null && this.items.getClass() == EmptyList.class ? true : this.items.isEmpty()) && !MovementInputUtils.method7418()) {
               this.flag3 = true;
            } else if ((var1.getIntValue3() != 1 || !var4.equals(ContainerInput.PICKUP))
               && (this.slotActionType != ContainerInput.PICKUP || var1.getSlotActionType() != ContainerInput.QUICK_MOVE)
               && (this.slotActionType != ContainerInput.QUICK_MOVE || var1.getSlotActionType() != ContainerInput.PICKUP)
               && (this.slotActionType != ContainerInput.QUICK_MOVE || var1.getSlotActionType() != ContainerInput.PICKUP_ALL)
               && (this.slotActionType != ContainerInput.PICKUP_ALL || var1.getSlotActionType() != ContainerInput.QUICK_MOVE)) {
               this.flag = true;
            } else {
               var1.setCancelled(true);
            }
         }
      }
   }

   @EventTarget
   public void method1667(CloseHandledScreenEvent var1) {
      if (!this.entry3.isSelected()) {
         if (mc.player.containerMenu instanceof InventoryMenu) {
            if (this.entry.isSelected()) {
               if (!this.flag2) {
                  var1.setCancelled(true);
               }

               this.flag2 = false;
            } else if (this.flag3 && (this.items != null && this.items.getClass() == EmptyList.class ? true : this.items.isEmpty())) {
               this.flag3 = false;
            } else {
               this.flag3 = false;
               if (MovementInputUtils.method7418() || (this.items != null && this.items.getClass() == EmptyList.class ? false : !this.items.isEmpty())) {
                  var1.setCancelled(true);
               }

               if (this.items != null && this.items.getClass() == EmptyList.class ? false : !this.items.isEmpty()) {
                  this.slotActionType = null;
                  this.flag = false;
                  var var2 = new ArrayList<>(this.items);
                  this.items.clear();
                  TaskScheduler.Entry2 var3 = new TaskScheduler.Entry2(GuiWalkModule.class);
                  if (InventorySettingModule.INSTANCE.method2396()) {
                     var3.method5120(
                        MoveInputEvent.class,
                        var1x -> {
                           var1x.method3044();
                           if (!mc.player.lastSentInput.jump()
                              && !mc.player.isSprinting()
                              && !mc.player.lastSentInput.forward()
                              && !mc.player.lastSentInput.backward()
                              && !mc.player.lastSentInput.left()
                              && !mc.player.lastSentInput.right()) {
                              var2.forEach(var0x -> mc.getConnection().send(var0x));
                              InventoryUtils.closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                  } else {
                     for (int var4 = 0; var4 < var2.size(); var4++) {
                        Packet var5 = (Packet)var2.get(var4);
                        var3.method5120(
                           Event1.class,
                           var1x -> {
                              if (!(mc.player.containerMenu instanceof InventoryMenu)) {
                                 InventoryUtils.closeScreen();
                              }

                              if (!mc.player.lastSentInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastSentInput.forward()
                                 && !mc.player.lastSentInput.backward()
                                 && !mc.player.lastSentInput.left()
                                 && !mc.player.lastSentInput.right()) {
                                 mc.getConnection().send(var5);
                                 return true;
                              } else {
                                 return false;
                              }
                           }
                        );
                     }

                     var3.method5120(
                        MoveInputEvent.class,
                        var0 -> {
                           if (!mc.player.lastSentInput.jump()
                              && !mc.player.isSprinting()
                              && !mc.player.lastSentInput.forward()
                              && !mc.player.lastSentInput.backward()
                              && !mc.player.lastSentInput.left()
                              && !mc.player.lastSentInput.right()) {
                              InventoryUtils.closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     var3.method5123(MoveInputEvent.class, var0 -> {
                        var0.method3044();
                        return true;
                     });
                     if (this.cameraLock.isEnabled()) {
                        var3.method5123(
                           Event5.class,
                           var1x -> {
                              rotationManager.method7848(
                                 new RotationRequest(
                                    rotationManager.method7844(), rotationManager::method7844, rotationManager.getBcCameraTestnerv().getRotationConfig()
                                 ),
                                 999,
                                 this,
                                 1
                              );
                              return true;
                           }
                        );
                     }
                  }

                  Zenith.getInstance().getTaskScheduler().method5110(var3);
               }
            }
         }
      }
   }
}
