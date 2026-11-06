package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.zenith.ChatLogger;
import org.zenith.PacketEvent;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;

public class ExchangeCommand extends CommandAbstract {
   private static final Pattern EXPECTED = Pattern.compile("Ожидается:\\s*([\\d ]+)");
   private static final Pattern GIVE = Pattern.compile("Вы отдадите:\\s*([\\d ]+)");
   private static final Pattern PURCHASED = Pattern.compile("успешно закупили .+? на бюджет\\s*([\\d\\s]+)", 66);
   private static final int CONFIRM_SLOT = 13;
   private static final int DECREASE_SLOT = 12;
   private static final int INCREASE_SLOT = 14;
   private static final int STUCK_LIMIT = 40;
   private static final int ADJUST_RETRY_INTERVAL = 5;
   private int phase;
   private int ticks;
   private int retryTicks;
   private int targetAmount = -1;
   private int remainingAmount = -1;
   private int lastGive = -1;
   private int lastClickSlot = -1;
   private int capBelowGoal = -1;
   private int stuckTicks;
   private int lastConfirmedGive = -1;
   private int lastReportedExpected = -1;
   private volatile boolean waitingPurchase;

   public ExchangeCommand() {
      super("exc");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Использование: .exc <количество>");
         return 1;
      });
      literalArgumentBuilder.then(arg("amount", IntegerArgumentType.integer(1)).executes(var1x -> {
         int argument = (Integer)var1x.getArgument("amount", Integer.class);
         this.stop();
         this.targetAmount = argument;
         this.remainingAmount = argument;
         this.send();
         return 1;
      }));
   }

   private void send() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.player.containerMenu != minecraft.player.inventoryMenu) {
         minecraft.player.closeContainer();
      }

      if (minecraft.getConnection() != null) {
         minecraft.getConnection().sendCommand("exchange");
      }

      this.phase = 1;
      this.ticks = 0;
      this.waitingPurchase = false;
      this.lastConfirmedGive = -1;
      this.lastReportedExpected = -1;
      this.resetAdjustState();
   }

   private void resetAdjustState() {
      this.lastGive = -1;
      this.lastClickSlot = -1;
      this.capBelowGoal = -1;
      this.stuckTicks = 0;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.retryTicks > 0 && --this.retryTicks == 0) {
         this.send();
      }

      if (this.phase != 0) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null && minecraft.gameMode != null) {
            AbstractContainerMenu containerMenu = minecraft.player.containerMenu;
            boolean flag = containerMenu != null && containerMenu != minecraft.player.inventoryMenu;
            if (this.phase == 1) {
               if (++this.ticks > 100) {
                  this.stop();
                  return;
               }

               if (!flag) {
                  return;
               }

               int lore = -1;
               if (containerMenu.isValidSlotIndex(2)) {
                  lore = this.parseLore(containerMenu.getSlot(2).getItem(), EXPECTED);
               }

               if (lore >= 0 && lore != this.lastReportedExpected) {
                  this.lastReportedExpected = lore;
                  ChatLogger.method2019("Ожидается: " + lore + " коинов");
               }

               int goal = this.goal();
               if (goal >= 0 && lore >= 0 && goal < lore) {
                  minecraft.gameMode.handleContainerInput(containerMenu.containerId, 2, 1, ContainerInput.PICKUP, minecraft.player);
                  this.phase = 3;
                  this.ticks = 0;
                  this.resetAdjustState();
                  return;
               }

               this.phase = 2;
               this.ticks = 0;
            }

            if (this.phase == 2) {
               if (!flag) {
                  this.stop();
                  return;
               }

               if (this.isBuyScreen(minecraft)) {
                  this.phase = 3;
                  this.ticks = 0;
                  this.resetAdjustState();
                  return;
               }

               if (++this.ticks > 100) {
                  this.stop();
                  return;
               }

               if (this.ticks % 10 == 1) {
                  minecraft.gameMode.handleContainerInput(containerMenu.containerId, 2, 0, ContainerInput.PICKUP, minecraft.player);
               }
            }

            if (this.phase == 3) {
               if (++this.ticks > 200) {
                  this.stop();
               } else if (flag && this.isBuyScreen(minecraft)) {
                  int lore = -1;
                  if (containerMenu.isValidSlotIndex(13)) {
                     lore = this.parseLore(containerMenu.getSlot(13).getItem(), GIVE);
                  }

                  if (lore >= 0) {
                     int goal = this.goal();
                     if (lore == goal) {
                        this.confirmPurchase(minecraft, containerMenu, lore);
                     } else {
                        boolean flag2 = lore != this.lastGive;
                        if (flag2) {
                           if (!this.onGiveChanged(lore, goal)) {
                              return;
                           }
                        } else {
                           this.stuckTicks++;
                        }

                        if (lore > goal) {
                           if (this.stuckTicks > 40) {
                              ChatLogger.method2021("Exchange: не удалось уменьшить количество до " + goal + ", отмена");
                              this.stop();
                           } else {
                              if (this.shouldAdjust(flag2)) {
                                 this.adjust(minecraft, containerMenu, 12, lore);
                              }
                           }
                        } else if (lore > 0 && this.capBelowGoal >= 0 && lore <= goal && lore >= this.capBelowGoal) {
                           this.confirmPurchase(minecraft, containerMenu, lore);
                        } else if (this.stuckTicks > 40) {
                           if (lore > 0) {
                              this.confirmPurchase(minecraft, containerMenu, lore);
                           } else {
                              this.stop();
                           }
                        } else {
                           if (this.shouldAdjust(flag2)) {
                              this.adjust(minecraft, containerMenu, 14, lore);
                           }
                        }
                     }
                  }
               }
            } else {
               if (this.phase == 4) {
                  this.ticks++;
                  boolean flag2 = flag && this.isBuyScreen(minecraft);
                  if (!flag2 && this.ticks > 10) {
                     this.onPurchased(this.lastConfirmedGive);
                     return;
                  }

                  if (this.ticks > 200) {
                     ChatLogger.method2021("Exchange: нет подтверждения покупки, отмена");
                     this.stop();
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && this.waitingPurchase) {
         String packetText = this.packetText(event.getPacket());
         if (packetText != null) {
            Matcher matcher = PURCHASED.matcher(packetText);
            if (matcher.find()) {
               int lastConfirmedGive;
               try {
                  lastConfirmedGive = Integer.parseInt(matcher.group(1).replaceAll("\\s", ""));
               } catch (NumberFormatException e) {
                  return;
               }

               Minecraft.getInstance().execute(() -> {
                  if (this.waitingPurchase) {
                     this.onPurchased(lastConfirmedGive);
                  }
               });
            }
         }
      }
   }

   private boolean onGiveChanged(int lore, int goal) {
      if (this.lastGive >= 0) {
         if (this.lastClickSlot == 12 && lore > this.lastGive) {
            ChatLogger.method2021("Exchange: кнопка уменьшения увеличивает количество, отмена");
            this.stop();
            return false;
         }

         if (this.lastClickSlot == 14 && lore > goal && this.lastGive <= goal) {
            this.capBelowGoal = this.lastGive;
         }
      }

      this.stuckTicks = 0;
      return true;
   }

   private boolean shouldAdjust(boolean flag) {
      return flag || this.stuckTicks > 0 && this.stuckTicks % 5 == 0;
   }

   private void adjust(Minecraft minecraft, AbstractContainerMenu containerMenu, int lastClickSlot, int lastGive) {
      minecraft.gameMode.handleContainerInput(containerMenu.containerId, lastClickSlot, 0, ContainerInput.PICKUP, minecraft.player);
      this.lastClickSlot = lastClickSlot;
      this.lastGive = lastGive;
   }

   private void confirmPurchase(Minecraft minecraft, AbstractContainerMenu containerMenu, int lastConfirmedGive) {
      this.waitingPurchase = true;
      this.lastConfirmedGive = lastConfirmedGive;
      this.phase = 4;
      this.ticks = 0;
      minecraft.gameMode.handleContainerInput(containerMenu.containerId, 13, 0, ContainerInput.PICKUP, minecraft.player);
   }

   private void onPurchased(int lastConfirmedGive) {
      if (this.phase != 0 || this.waitingPurchase) {
         this.waitingPurchase = false;
         this.phase = 0;
         int n = this.lastConfirmedGive > 0 ? this.lastConfirmedGive : lastConfirmedGive;
         this.lastConfirmedGive = -1;
         if (n <= 0) {
            ChatLogger.method2021("Exchange: не удалось определить количество покупки, отмена");
            this.stop();
         } else {
            this.remainingAmount -= n;
            if (this.remainingAmount <= 0) {
               ChatLogger.method2019("Закупка завершена");
               this.stop();
            } else {
               ChatLogger.method2019("Осталось закупить: " + this.remainingAmount);
               this.retryTicks = 10;
            }
         }
      }
   }

   private String packetText(Packet<?> packet3) {
      if (packet3 instanceof ClientboundSystemChatPacket systemChatPacket) {
         return systemChatPacket.content().getString();
      } else {
         return packet3 instanceof ClientboundDisguisedChatPacket disguisedChatPacket ? disguisedChatPacket.message().getString() : null;
      }
   }

   private int goal() {
      return this.remainingAmount >= 0 ? this.remainingAmount : this.targetAmount;
   }

   private void stop() {
      this.phase = 0;
      this.ticks = 0;
      this.retryTicks = 0;
      this.targetAmount = -1;
      this.remainingAmount = -1;
      this.lastConfirmedGive = -1;
      this.lastReportedExpected = -1;
      this.waitingPurchase = false;
      this.resetAdjustState();
   }

   private boolean isBuyScreen(Minecraft minecraft) {
      return minecraft.gui.screen() != null && minecraft.gui.screen().getTitle().getString().contains("Покупка");
   }

   private int parseLore(ItemStack stack, Pattern pattern) {
      if (stack.isEmpty()) {
         return -1;
      } else {
         ItemLore itemLore = stack.get(DataComponents.LORE);
         if (itemLore == null) {
            return -1;
         } else {
            for (Component line : itemLore.lines()) {
               Matcher matcher = pattern.matcher(line.getString());
               if (matcher.find()) {
                  try {
                     return Integer.parseInt(matcher.group(1).replaceAll("\\s", ""));
                  } catch (NumberFormatException e) {
                     return -1;
                  }
               }
            }

            return -1;
         }
      }
   }
}
