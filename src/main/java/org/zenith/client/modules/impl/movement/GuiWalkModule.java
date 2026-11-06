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
import org.zenith.client.events.SchedulerPulseEvent;
import org.zenith.client.events.PreRotationEvent;
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
   private final ModeSetting.Entry visualMode = new ModeSetting.Entry(this.mode, "module.guiWalk.mode.visual").method2103();
   private final ModeSetting.Entry grimMode = new ModeSetting.Entry(this.mode, "module.guiWalk.mode.grim");
   private final ModeSetting.Entry holyWorldMode = new ModeSetting.Entry(this.mode, "HolyWorld");
   private final BooleanSetting cameraLock = new BooleanSetting("module.guiWalk.cameraLock", "module.guiWalk.cameraLock.desc", true, this.grimMode::isSelected);
   private ContainerInput lastClickAction = null;
   private final List<Packet<?>> queuedClickPackets = new ArrayList<>();
   public boolean queueNextClick = false;
   public boolean clickedWhileStationary = false;
   public boolean stationaryClick = false;

   private GuiWalkModule() {
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (!this.holyWorldMode.isSelected()) {
         try {
            if (UseItemUtils.method6526()) {
               return;
            }

            if (mc.player.containerMenu instanceof InventoryMenu) {
               if (!this.queueNextClick) {
                  return;
               }

               this.queueNextClick = false;
               if (Objects.requireNonNull(event.getPacket()) instanceof ServerboundContainerClickPacket containerClickPacket
                  && (!this.queuedClickPackets.isEmpty() || MovementInputUtils.method7418())
                  && KeyBindUtils.method2635()) {
                  this.queuedClickPackets.add(containerClickPacket);
                  this.lastClickAction = containerClickPacket.containerInput();
                  event.setCancelled(true);
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player.containerMenu instanceof InventoryMenu) {
         if (KeyBindUtils.method2635()
            && (!this.visualMode.isSelected() || !this.clickedWhileStationary)
            && !this.stationaryClick
            && (
               !this.queuedClickPackets.isEmpty()
                  || mc.player.containerMenu.getCarried().isEmpty()
            )) {
            KeyBindUtils.method2634();
         }
      } else {
         this.clickedWhileStationary = false;
         this.stationaryClick = false;
      }
   }

   @EventTarget
   public void onSlotClick(SlotClickEvent event) {
      if (event.getIntValue2() >= 0) {
         Slot slot = mc.player.containerMenu.getSlot(event.getIntValue2());
         ItemStack stack = slot.getItem();
         if (stack.get(DataComponents.CUSTOM_DATA) != null) {
         }
      }

      if (!this.holyWorldMode.isSelected()) {
         if (mc.player.containerMenu instanceof InventoryMenu) {
            ContainerInput lastClickAction = event.getSlotActionType();
            if (this.visualMode.isSelected()) {
               if (MovementInputUtils.method7418()) {
                  event.setCancelled(true);
               } else {
                  this.clickedWhileStationary = true;
               }
            }

            if (this.queuedClickPackets.isEmpty() && !MovementInputUtils.method7418()) {
               this.stationaryClick = true;
            } else if ((event.getIntValue3() != 1 || !lastClickAction.equals(ContainerInput.PICKUP))
               && (this.lastClickAction != ContainerInput.PICKUP || event.getSlotActionType() != ContainerInput.QUICK_MOVE)
               && (this.lastClickAction != ContainerInput.QUICK_MOVE || event.getSlotActionType() != ContainerInput.PICKUP)
               && (this.lastClickAction != ContainerInput.QUICK_MOVE || event.getSlotActionType() != ContainerInput.PICKUP_ALL)
               && (this.lastClickAction != ContainerInput.PICKUP_ALL || event.getSlotActionType() != ContainerInput.QUICK_MOVE)) {
               this.queueNextClick = true;
            } else {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventTarget
   public void onCloseScreen(CloseHandledScreenEvent event) {
      if (!this.holyWorldMode.isSelected()) {
         if (mc.player.containerMenu instanceof InventoryMenu) {
            if (this.visualMode.isSelected()) {
               if (!this.clickedWhileStationary) {
                  event.setCancelled(true);
               }

               this.clickedWhileStationary = false;
            } else if (this.stationaryClick && this.queuedClickPackets.isEmpty()) {
               this.stationaryClick = false;
            } else {
               this.stationaryClick = false;
               if (MovementInputUtils.method7418() || !this.queuedClickPackets.isEmpty()) {
                  event.setCancelled(true);
               }

               if (!this.queuedClickPackets.isEmpty()) {
                  this.lastClickAction = null;
                  this.queueNextClick = false;
                  var packets = new ArrayList<>(this.queuedClickPackets);
                  this.queuedClickPackets.clear();
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
                              packets.forEach(queuedPacket -> mc.getConnection().send(queuedPacket));
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
                           SchedulerPulseEvent.class,
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
                           PreRotationEvent.class,
                           var1x -> {
                              rotationManager.requestRotation(
                                 new RotationRequest(
                                    rotationManager.getEffectiveRotation(), rotationManager::getEffectiveRotation, rotationManager.getBcCameraTestnerv().getRotationConfig()
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
