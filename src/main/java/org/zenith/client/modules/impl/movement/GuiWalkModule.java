package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
   public void onPacket(PacketEvent event) {
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
               if (Objects.requireNonNull(event.getPacket3()) instanceof ServerboundContainerClickPacket containerClickPacket
                  && (!this.items.isEmpty() || MovementInputUtils.method7418())
                  && KeyBindUtils.method2635()) {
                  this.items.add(containerClickPacket);
                  this.slotActionType = containerClickPacket.containerInput();
                  event.setCancelled(true);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method1665(UpdateEvent event) {
      if (mc.player.containerMenu instanceof InventoryMenu) {
         if (KeyBindUtils.method2635()
            && (!this.entry.isSelected() || !this.flag2)
            && !this.flag3
            && (
               !this.items.isEmpty()
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
   public void method1666(SlotClickEvent event) {
      if (event.getIntValue2() >= 0) {
         Slot slot = mc.player.containerMenu.getSlot(event.getIntValue2());
         ItemStack stack = slot.getItem();
         if (stack.get(DataComponents.CUSTOM_DATA) != null) {
         }
      }

      if (!this.entry3.isSelected()) {
         if (mc.player.containerMenu instanceof InventoryMenu) {
            ContainerInput slotActionType = event.getSlotActionType();
            if (this.entry.isSelected()) {
               if (MovementInputUtils.method7418()) {
                  event.setCancelled(true);
               } else {
                  this.flag2 = true;
               }
            }

            if (this.items.isEmpty() && !MovementInputUtils.method7418()) {
               this.flag3 = true;
            } else if ((event.getIntValue3() != 1 || !slotActionType.equals(ContainerInput.PICKUP))
               && (this.slotActionType != ContainerInput.PICKUP || event.getSlotActionType() != ContainerInput.QUICK_MOVE)
               && (this.slotActionType != ContainerInput.QUICK_MOVE || event.getSlotActionType() != ContainerInput.PICKUP)
               && (this.slotActionType != ContainerInput.QUICK_MOVE || event.getSlotActionType() != ContainerInput.PICKUP_ALL)
               && (this.slotActionType != ContainerInput.PICKUP_ALL || event.getSlotActionType() != ContainerInput.QUICK_MOVE)) {
               this.flag = true;
            } else {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventTarget
   public void method1667(CloseHandledScreenEvent event) {
      if (!this.entry3.isSelected()) {
         if (mc.player.containerMenu instanceof InventoryMenu) {
            if (this.entry.isSelected()) {
               if (!this.flag2) {
                  event.setCancelled(true);
               }

               this.flag2 = false;
            } else if (this.flag3 && this.items.isEmpty()) {
               this.flag3 = false;
            } else {
               this.flag3 = false;
               if (MovementInputUtils.method7418() || !this.items.isEmpty()) {
                  event.setCancelled(true);
               }

               if (!this.items.isEmpty()) {
                  this.slotActionType = null;
                  this.flag = false;
                  var packets = new ArrayList<>(this.items);
                  this.items.clear();
                  TaskScheduler.Entry2 task = new TaskScheduler.Entry2(GuiWalkModule.class);
                  if (InventorySettingModule.INSTANCE.method2396()) {
                     task.method5120(
                        MoveInputEvent.class,
                        var1x -> {
                           var1x.method3044();
                           if (!mc.player.lastSentInput.jump()
                              && !mc.player.isSprinting()
                              && !mc.player.lastSentInput.forward()
                              && !mc.player.lastSentInput.backward()
                              && !mc.player.lastSentInput.left()
                              && !mc.player.lastSentInput.right()) {
                              packets.forEach(var0x -> mc.getConnection().send(var0x));
                              InventoryUtils.closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                  } else {
                     for (int i = 0; i < packets.size(); i++) {
                        Packet packet = (Packet)packets.get(i);
                        task.method5120(
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
                                 mc.getConnection().send(packet);
                                 return true;
                              } else {
                                 return false;
                              }
                           }
                        );
                     }

                     task.method5120(
                        MoveInputEvent.class,
                        event2 -> {
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
                     task.method5123(MoveInputEvent.class, event2 -> {
                        event2.method3044();
                        return true;
                     });
                     if (this.cameraLock.isEnabled()) {
                        task.method5123(
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

                  Zenith.getInstance().getTaskScheduler().method5110(task);
               }
            }
         }
      }
   }
}
