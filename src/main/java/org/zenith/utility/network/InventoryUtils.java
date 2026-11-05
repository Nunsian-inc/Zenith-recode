package org.zenith.utility.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntPredicate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Generated;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.HashedStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.misc.ElytraHelperModule;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.MathUtils;

public final class InventoryUtils implements ZenithAccess {
   public static final List<KeyMapping> items = List.of(mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump);
   private static final int CONST_intValue = 9;
   private static final int CONST_intValue2 = 36;
   private static final int CONST_intValue3 = 44;
   private static final int CONST_intValue4 = 45;

   public static void method897() {
      if (InventorySettingModule.INSTANCE.method2398()) {
         method898();
      }
   }

   public static void method898() {
      AbstractContainerMenu containerMenu = mc.player.containerMenu;
      ItemStack defaultInstance = BuiltInRegistries.ITEM.byId((int)MathUtils.method2651(0.0, 100.0)).getDefaultInstance();
      HashedStack carriedItem = HashedStack.create(defaultInstance, mc.player.connection.decoratedHashOpsGenenerator());
      mc.player
         .connection
         .send(
            new ServerboundContainerClickPacket(containerMenu.containerId, containerMenu.getStateId(), (short)0, (byte)0, ContainerInput.PICKUP_ALL, Int2ObjectMaps.singleton(0, carriedItem), carriedItem)
         );
   }

   public static void method899() {
      AbstractContainerMenu containerMenu = mc.player.containerMenu;
      mc.player
         .connection
         .send(
            new ServerboundContainerClickPacket(
               containerMenu.containerId, containerMenu.getStateId() + 1, (short)0, (byte)0, ContainerInput.PICKUP_ALL, Int2ObjectMaps.emptyMap(), HashedStack.EMPTY
            )
         );
   }

   public static void closeScreen() {
      if (mc.player.containerMenu instanceof InventoryMenu) {
         mc.player.connection.send(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
      } else {
         mc.player.closeContainer();
      }
   }

   public static void method900(Slot slot, InteractionHand hand, boolean flag) {
      if (slot != null
         && slot.index != -1
         && (!hand.equals(InteractionHand.OFF_HAND) || slot.container instanceof Inventory || slot.container instanceof PlayerEnderChestContainer)) {
         int selectedSlot = hand.equals(InteractionHand.MAIN_HAND) ? mc.player.getInventory().getSelectedSlot() : 40;
         method901(slot, selectedSlot, flag);
      }
   }

   public static void method901(Slot slot, int selectedSlot, boolean flag) {
      method903(slot, selectedSlot, ContainerInput.SWAP, false);
      if (flag) {
         method897();
      }
   }

   public static void method902(Slot slot, int selectedSlot) {
      method903(slot, selectedSlot, ContainerInput.SWAP, false);
   }

   public static void method903(Slot slot, int selectedSlot, ContainerInput containerInput, boolean flag) {
      if (slot != null) {
         method904(slot.index, selectedSlot, containerInput, flag);
      }
   }

   public static void method904(int index, int selectedSlot, ContainerInput containerInput, boolean flag) {
      method905(mc.player.containerMenu.containerId, index, selectedSlot, containerInput, flag);
   }

   public static void method905(int containerId, int index, int selectedSlot, ContainerInput containerInput, boolean flag) {
      mc.gameMode.handleContainerInput(containerId, index, selectedSlot, containerInput, mc.player);
      if (flag) {
         mc.player.containerMenu.clicked(index, selectedSlot, containerInput, mc.player);
      }
   }

   public static Slot method906(Item item) {
      return method907(item, var0x -> true);
   }

   public static Slot method907(Item item, Predicate<Slot> predicate) {
      return method913(item, Comparator.comparingInt(var0x -> var0x.index), predicate);
   }

   public static Slot method908(AbstractContainerMenu inventoryMenu, Item item) {
      return inventoryMenu.slots.stream().filter(var1x -> (var1x.getItem().getItem() == item)).findFirst().orElse(null);
   }

   public static Slot method909(AbstractContainerMenu inventoryMenu, Predicate<Slot> predicate) {
      return inventoryMenu.slots.stream().filter(predicate).findFirst().orElse(null);
   }

   public static Slot method910(Predicate<Slot> predicate) {
      return method926().filter(predicate).findFirst().orElse(null);
   }

   public static Slot method911(Predicate<Slot> predicate, Comparator<Slot> comparator) {
      return method926().filter(predicate).max(comparator).orElse(null);
   }

   public static Slot method912(AbstractContainerMenu inventoryMenu, Item item, Comparator<Slot> comparingInt, Predicate<Slot> predicate) {
      return inventoryMenu.slots.stream().filter(var1x -> var1x.getItem().getItem().equals(item)).filter(predicate).max(comparingInt).orElse(null);
   }

   public static Slot method913(Item item, Comparator<Slot> comparing, Predicate<Slot> predicate) {
      return method926().filter(var1x -> var1x.getItem().getItem().equals(item)).filter(predicate).max(comparing).orElse(null);
   }

   public static Slot method914() {
      return method926()
         .filter(
            slot -> (slot.getItem().get(DataComponents.FOOD) != null && !slot.getItem().get(DataComponents.FOOD).canAlwaysEat())
         )
         .max(Comparator.comparingDouble(slot -> slot.getItem().get(DataComponents.FOOD).saturation()))
         .orElse(null);
   }

   public static Slot method915(List<Item> items) {
      return method926().filter(slot -> items.contains(slot.getItem().getItem())).findFirst().orElse(null);
   }

   public static Slot method916(List<Item> items, Comparator<Slot> comparingInt) {
      return method926().filter(var1x -> items.contains(var1x.getItem().getItem())).max(comparingInt).orElse(null);
   }

   public static Slot method917(Holder<MobEffect> holder) {
      return method926()
         .filter(
            slot -> {
               PotionContents potionContents = slot.getItem().get(DataComponents.POTION_CONTENTS);
               return (boolean)(potionContents == null
                  ? 0
                  : StreamSupport.stream(potionContents.getAllEffects().spliterator(), false).anyMatch(var1x -> var1x.getEffect().equals(holder)));
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static boolean method918(ItemStack stack, String str) {
      if (stack.isEmpty()) {
         return false;
      } else {
         CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
         return (boolean)(customData == null ? 0 : customData.copyTag().toString().contains(str));
      }
   }

   public static Slot method919(MobEffectCategory mobEffectCategory) {
      return method926()
         .filter(
            slot -> {
               ItemStack stack = slot.getItem();
               PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
               if (stack.getItem().equals(Items.SPLASH_POTION) && potionContents != null) {
                  MobEffectCategory mobEffectCategory2 = mobEffectCategory.equals(MobEffectCategory.BENEFICIAL) ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL;
                  long count = StreamSupport.stream(potionContents.getAllEffects().spliterator(), false)
                     .filter(var1x -> var1x.getEffect().value().getCategory().equals(mobEffectCategory))
                     .count();
                  long l = StreamSupport.stream(potionContents.getAllEffects().spliterator(), false)
                     .filter(var1x -> var1x.getEffect().value().getCategory().equals(mobEffectCategory2))
                     .count();
                  return (count >= l);
               } else {
                  return false;
               }
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static int method920(Item item) {
      return IntStream.range(0, 45)
         .filter(slot -> Objects.requireNonNull(mc.player).getInventory().getItem(slot).getItem().equals(item))
         .map(var0x -> mc.player.getInventory().getItem(var0x).getCount())
         .sum();
   }

   public static int method921(List<Item> items) {
      return IntStream.range(0, 9).filter(slot -> items.contains(mc.player.getInventory().getItem(slot).getItem())).findFirst().orElse(-1);
   }

   public static int method922(IntPredicate intPredicate) {
      return IntStream.range(0, 9).filter(intPredicate).findFirst().orElse(-1);
   }

   public static int method923(Predicate<Slot> predicate) {
      return method926().filter(predicate).mapToInt(var0x -> var0x.getItem().getCount()).sum();
   }

   public static Slot method924() {
      long count;
      int n = (count = method926().count()) == 46L ? 10 : 9;
      return method926().toList().get(Math.toIntExact(count - n + mc.player.getInventory().getSelectedSlot()));
   }

   public static boolean method925() {
      return (method926().toList().size() != 46);
   }

   public static Stream<Slot> method926() {
      return mc.player.containerMenu.slots.stream();
   }

   public static void method927(Item item) {
      method928(item, var0x -> true);
   }

   public static void method928(Item item, Predicate<Slot> predicate) {
      method930(item, predicate, InteractionHand.MAIN_HAND);
   }

   public static void method929(Item item, InteractionHand hand) {
      method930(item, var0x -> true, hand);
   }

   public static void method930(Item item, Predicate<Slot> predicate, InteractionHand hand) {
      float cooldownPercent;
      if ((cooldownPercent = mc.player.getCooldowns().getCooldownPercent(item.getDefaultInstance(), 0.0F)) > 0.0F) {
         String cooldown = MathUtils.method2652(cooldownPercent, 0.1) + "с";
         Zenith.getInstance()
            .getHudHolder()
            .method6972(
               "N",
               Component.translationArg(
                  item.getName(item.getDefaultInstance())
                     .copy()
                     .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                     .append(
                        Component.nullToEmpty(" находиться в кд")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                     )
               )
            );
      } else {
         Slot slot = method907(item, predicate);
         if (slot == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Component.translationArg(
                     item.getName(item.getDefaultInstance())
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                        .append(
                           Component.nullToEmpty(" не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
         } else if (mc.player.isUsingItem() && Zenith.getInstance().getServerDetector().method1642()) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Component.translationArg(
                     Component.nullToEmpty("Хавать")
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                        .append(
                           Component.nullToEmpty(" нельзя")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
         } else {
            TaskScheduler.Entry2 task = new TaskScheduler.Entry2();
            if (!InventorySettingModule.INSTANCE.method2397() && InventorySettingModule.INSTANCE.method2396()) {
               task.method5120(HandleInputEvent.class, var2x -> {
                  if (var2x.isCancelled()) {
                     return false;
                  } else {
                     method931(slot, hand);
                     closeScreen();
                     return true;
                  }
               });
            } else {
               int intExact = Math.toIntExact(method926().count());
               int selected = slot.index - 36;
               if (hand == InteractionHand.MAIN_HAND && selected >= 0 && selected <= 8 && intExact == 46) {
                  AtomicInteger counter = new AtomicInteger(mc.player.inventory.getSelectedSlot());
                  if (!InventorySettingModule.INSTANCE.method2396()) {
                     task.method5120(HandleInputEvent.class, var2x -> {
                        counter.set(mc.player.inventory.getSelectedSlot());
                        mc.player.inventory.setSelectedSlot(selected);
                        return true;
                     });
                     task.method5120(HandleInputEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        } else {
                           UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                           if (Zenith.getInstance().getServerDetector().method1642()) {
                              mc.gameMode.releaseUsingItem(mc.player);
                           }

                           mc.player.inventory.setSelectedSlot(counter.get());
                           var1x.cancel();
                           return true;
                        }
                     });
                  } else {
                     task.method5120(HandleInputEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        } else {
                           int var2x = mc.player.inventory.getSelectedSlot();
                           mc.player.inventory.setSelectedSlot(selected);
                           UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                           mc.player.inventory.setSelectedSlot(var2x);
                           var1x.cancel();
                           return true;
                        }
                     });
                  }
               } else if (!InventorySettingModule.INSTANCE.method2396()) {
                  if (KeyBindUtils.method2630(AutoTotemModule.class) && !AutoTotemModule.INSTANCE.method725() || hand != InteractionHand.OFF_HAND) {
                     int selectedSlot = hand.equals(InteractionHand.MAIN_HAND) ? mc.player.getInventory().getSelectedSlot() : 40;
                     int n = mc.player.getInventory().getSelectedSlot();
                     task.method5120(
                        HandleInputPostEvent.class,
                        var2x -> {
                           if (!InventorySettingModule.INSTANCE.method2397()
                              || !mc.player.lastSentInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastSentInput.forward()
                                 && !mc.player.lastSentInput.backward()
                                 && !mc.player.lastSentInput.left()
                                 && !mc.player.lastSentInput.right()) {
                              method901(slot, selectedSlot, false);
                              closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     task.method5120(HandleInputEvent.class, var3x -> {
                        if (var3x.isCancelled()) {
                           return false;
                        } else {
                           if (hand == InteractionHand.MAIN_HAND) {
                              mc.player.getInventory().setSelectedSlot(selectedSlot);
                              UseItemUtils.useItem(hand);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 UseItemUtils.useItem(hand);
                              }

                              mc.player.getInventory().setSelectedSlot(n);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 mc.gameMode.releaseUsingItem(mc.player);
                              }
                           } else {
                              UseItemUtils.useItem(hand);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 UseItemUtils.useItem(hand);
                              }
                           }

                           var3x.cancel();
                           return true;
                        }
                     });
                     task.method5120(
                        HandleInputPostEvent.class,
                        var2x -> {
                           if (!InventorySettingModule.INSTANCE.method2397()
                              || !mc.player.lastSentInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastSentInput.forward()
                                 && !mc.player.lastSentInput.backward()
                                 && !mc.player.lastSentInput.left()
                                 && !mc.player.lastSentInput.right()) {
                              method901(slot, selectedSlot, false);
                              closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     if (InventorySettingModule.INSTANCE.method2397()) {
                        task.method5123(MoveInputEvent.class, var0x -> {
                           var0x.method3044();
                           return true;
                        });
                     }
                  }
               } else {
                  task.method5120(
                     HandleInputEvent.class,
                     var2x -> {
                        if ((!KeyBindUtils.method2630(AutoTotemModule.class) || AutoTotemModule.INSTANCE.method725()) && hand == InteractionHand.OFF_HAND) {
                           return true;
                        } else if (var2x.isCancelled()) {
                           return false;
                        } else if (!InventorySettingModule.INSTANCE.method2397()
                           || !mc.player.lastSentInput.jump()
                              && !mc.player.isSprinting()
                              && !mc.player.lastSentInput.forward()
                              && !mc.player.lastSentInput.backward()
                              && !mc.player.lastSentInput.left()
                              && !mc.player.lastSentInput.right()) {
                           method931(slot, hand);
                           closeScreen();
                           return true;
                        } else {
                           return false;
                        }
                     }
                  );
                  if (InventorySettingModule.INSTANCE.method2397()) {
                     task.method5123(MoveInputEvent.class, var0x -> {
                        var0x.method3044();
                        return true;
                     });
                  }
               }
            }

            Zenith.getInstance().getTaskScheduler().method5110(task);
         }
      }
   }

   public static void method931(Slot slot, InteractionHand hand) {
      method900(slot, hand, false);
      closeScreen();
      UseItemUtils.useItem(hand);
      method900(slot, hand, true);
   }

   /**
    * Запуск фейерверка в полёте на элитре так, чтобы предмет во второй руке (шар, тотем и т.п.) остался на месте.
    * <p>
    * Фейерверк уже во второй руке — используется оттуда. Иначе на ближайшем {@link HandleInputEvent} за один тик
    * уходят пакеты: SWAP слота с фейерверком и второй руки (кнопка 40) → использование второй руки → обратный SWAP.
    * Фейерверк возвращается в свой слот, предмет второй руки — обратно во вторую руку. В отличие от {@link #method930}
    * движение не останавливается и ничего не ждёт, поэтому способ подходит для полёта.
    */
   public static void useFireworkInFlight() {
      if (mc.player == null) {
         return;
      }

      ItemStack firework = Items.FIREWORK_ROCKET.getDefaultInstance();
      if (mc.player.getCooldowns().isOnCooldown(firework)) {
         showItemNotice("N", firework.getHoverName(), " находится в кд");
      } else if (!mc.player.getOffhandItem().is(Items.FIREWORK_ROCKET) && findFireworkSlot() == null) {
         showItemNotice("M", firework.getHoverName(), " не найден");
      } else if (mc.player.isUsingItem() && Zenith.getInstance().getServerDetector().method1642()) {
         showItemNotice("M", Component.literal("Хавать"), " нельзя");
      } else {
         TaskScheduler scheduler = Zenith.getInstance().getTaskScheduler();
         if (scheduler.method5117(FireworkRocketItem.class)) {
            TaskScheduler.Entry2 task = new TaskScheduler.Entry2(FireworkRocketItem.class);
            task.method5120(HandleInputEvent.class, event -> {
               if (event.isCancelled()) {
                  return false;
               } else {
                  launchFireworkFromOffhand();
                  event.cancel();
                  return true;
               }
            });
            scheduler.method5110(task);
         }
      }
   }

   private static void launchFireworkFromOffhand() {
      if (mc.player == null || mc.gameMode == null || !mc.player.isFallFlying()) {
         return;
      }

      if (mc.player.getOffhandItem().is(Items.FIREWORK_ROCKET)) {
         UseItemUtils.useItem(InteractionHand.OFF_HAND);
         return;
      }

      Slot slot = findFireworkSlot();
      if (slot != null && mc.player.containerMenu instanceof InventoryMenu && !AutoTotemModule.INSTANCE.method725()) {
         method931(slot, InteractionHand.OFF_HAND);
         closeScreen();
      }
   }

   /** Слот инвентаря игрока с фейерверком (кроме второй руки); хотбар в приоритете. */
   private static Slot findFireworkSlot() {
      return method926()
         .filter(slot -> slot.container instanceof Inventory && slot.getContainerSlot() != Inventory.SLOT_OFFHAND)
         .filter(slot -> slot.getItem().is(Items.FIREWORK_ROCKET))
         .max(Comparator.comparingInt(slot -> slot.index))
         .orElse(null);
   }

   private static void showItemNotice(String icon, Component subject, String suffix) {
      Zenith.getInstance()
         .getHudHolder()
         .method6972(
            icon,
            subject.copy()
               .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
               .append(
                  Component.literal(suffix)
                     .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
               )
         );
   }

   public static void method932(Slot slot, int n) {
      if (slot != null) {
         method935(slot.index, n, false, false);
      }
   }

   public static void method933(Slot slot, int index, boolean flag) {
      method934(slot, index, flag, false);
   }

   public static void method934(Slot slot, int index, boolean flag, boolean flag2) {
      if (slot != null) {
         method935(slot.index, index, flag, flag2);
      }
   }

   public static void method935(int index, int n, boolean flag, boolean flag2) {
      if (index != n && index != -1) {
         int intExact = Math.toIntExact(method926().count());
         int selectedSlot;
         if ((selectedSlot = index - 36) >= 0 && selectedSlot <= 8 && intExact == 46) {
            if (flag) {
               KeyBindUtils.method2627(InventoryUtils.class, () -> method904(n, selectedSlot, ContainerInput.SWAP, false));
            } else {
               method904(n, selectedSlot, ContainerInput.SWAP, false);
               closeScreen();
            }
         } else if (!ElytraHelperModule.INSTANCE.isEnabled() || n != 6 || !ElytraHelperModule.INSTANCE.method7456()) {
            if (flag) {
               if (InventorySettingModule.INSTANCE.method2396()) {
                  KeyBindUtils.method2627(InventoryUtils.class, () -> method936(index, n, flag2));
               } else {
                  TaskScheduler.Entry2 task = Zenith.getInstance()
                     .getTaskScheduler()
                     .method5109(InventoryUtils.class)
                     .method5121(
                        MoveInputEvent.class,
                        var1x -> {
                           if (InventorySettingModule.INSTANCE.method2397()) {
                              var1x.method3044();
                              if (mc.player.lastSentInput.jump()
                                 || mc.player.isSprinting()
                                 || mc.player.lastSentInput.forward()
                                 || mc.player.lastSentInput.backward()
                                 || mc.player.lastSentInput.left()
                                 || mc.player.lastSentInput.right()) {
                                 return false;
                              }
                           }

                           method904(index, 0, ContainerInput.SWAP, false);
                           closeScreen();
                           return true;
                        },
                        0
                     )
                     .method5120(MoveInputEvent.class, var0x -> true)
                     .method5120(
                        MoveInputEvent.class,
                        var2x -> {
                           if (InventorySettingModule.INSTANCE.method2397()) {
                              var2x.method3044();
                              if (mc.player.lastSentInput.jump()
                                 || mc.player.isSprinting()
                                 || mc.player.lastSentInput.forward()
                                 || mc.player.lastSentInput.backward()
                                 || mc.player.lastSentInput.left()
                                 || mc.player.lastSentInput.right()) {
                                 return false;
                              }
                           }

                           method904(n, 0, ContainerInput.SWAP, false);
                           closeScreen();
                           method904(index, 0, ContainerInput.SWAP, false);
                           closeScreen();
                           return true;
                        }
                     );
                  Zenith.getInstance().getTaskScheduler().method5111(task, 0);
               }
            } else {
               method936(index, n, flag2);
               closeScreen();
            }
         }
      }
   }

   public static void method936(int index, int n, boolean flag) {
      method904(index, 0, ContainerInput.SWAP, false);
      closeScreen();
      method904(n, 0, ContainerInput.SWAP, false);
      method904(index, 0, ContainerInput.SWAP, false);
      if (flag) {
         method897();
      }
   }

   public static InventoryUtils.Data method937(Item item, Comparator<ItemStack> comparator, Predicate<Slot> predicate) {
      return method938(var1x -> var1x.getItem().equals(item), comparator, predicate);
   }

   public static InventoryUtils.Data method938(Predicate<ItemStack> predicate, Comparator<ItemStack> comparator, Predicate<Slot> predicate2) {
      if (mc.player != null && mc.player.containerMenu != null) {
         InventoryUtils.Data data = null;
         InventoryUtils.Data data2 = null;

         for (Slot slot : mc.player.containerMenu.slots) {
            if (predicate2.test(slot)) {
               ItemStack stack = slot.getItem();
               if (stack.isEmpty() || !predicate.test(stack)) {
                  BundleContents bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
                  if (bundleContents != null) {
                     for (int i = 0; i < bundleContents.size(); i++) {
                        ItemStack itemStack = bundleContents.items().get(i).create();
                        if (!itemStack.isEmpty() && predicate.test(itemStack) && (data2 == null || comparator.compare(itemStack, data2.getItemStack()) > 0)) {
                           data2 = new InventoryUtils.Data(slot, i, itemStack);
                        }
                     }
                  }
               } else if (data == null || comparator.compare(stack, data.getItemStack()) > 0) {
                  data = InventoryUtils.Data.method944(slot);
               }
            }
         }

         return data != null ? data : data2;
      } else {
         return null;
      }
   }

   public static int method939(Predicate<ItemStack> predicate, Predicate<Slot> predicate2) {
      if (mc.player != null && mc.player.containerMenu != null) {
         int n = 0;

         for (Slot slot : mc.player.containerMenu.slots) {
            if (predicate2.test(slot)) {
               ItemStack stack = slot.getItem();
               if (!stack.isEmpty() && predicate.test(stack)) {
                  n += stack.getCount();
               } else {
                  BundleContents bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
                  if (bundleContents != null) {
                     for (int i = 0; i < bundleContents.size(); i++) {
                        ItemStack stack2 = bundleContents.items().get(i).create();
                        if (!stack2.isEmpty() && predicate.test(stack2)) {
                           n += stack2.getCount();
                        }
                     }
                  }
               }
            }
         }

         return n;
      } else {
         return 0;
      }
   }

   public static boolean method940(InventoryUtils.Data data, InteractionHand hand, boolean flag) {
      if (data == null) {
         return false;
      } else if (!data.method945()) {
         method900(data.getSlot(), hand, flag);
         return true;
      } else {
         return method941(data.getSlot(), data.getIntValue(), hand, flag);
      }
   }

   public static boolean method941(Slot slot, int intValue, InteractionHand hand, boolean flag) {
      if (slot != null && mc.player != null) {
         AbstractContainerMenu containerMenu = mc.player.containerMenu;
         if (!(containerMenu instanceof InventoryMenu) || !containerMenu.getCarried().isEmpty()) {
            return false;
         } else if (!(slot.getItem().getItem() instanceof BundleItem)) {
            return false;
         } else {
            BundleContents bundleContents = slot.getItem().get(DataComponents.BUNDLE_CONTENTS);
            if (bundleContents != null && intValue >= 0 && intValue < bundleContents.size()) {
               int index;
               if ((index = hand.equals(InteractionHand.OFF_HAND) ? 45 : 36 + mc.player.getInventory().getSelectedSlot()) == slot.index) {
                  return false;
               } else {
                  method942(slot, intValue);
                  method904(slot.index, 1, ContainerInput.PICKUP, false);
                  if (containerMenu.getCarried().isEmpty()) {
                     return false;
                  } else {
                     method904(index, 0, ContainerInput.PICKUP, false);
                     if (!containerMenu.getCarried().isEmpty()) {
                        method904(slot.index, 0, ContainerInput.PICKUP, false);
                     }

                     if (!containerMenu.getCarried().isEmpty()) {
                        method943(containerMenu, slot, index);
                     }

                     closeScreen();
                     if (flag) {
                        method897();
                     }

                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static void method942(Slot slot, int intValue) {
      if (BundleItem.getSelectedItemIndex(slot.getItem()) != intValue) {
         mc.player.connection.send(new ServerboundSelectBundleItemPacket(slot.index, intValue));
         BundleItem.toggleSelectedItem(slot.getItem(), intValue);
      }
   }

   private static void method943(AbstractContainerMenu containerMenu, Slot slot, int index) {
      for (Slot slot2 : containerMenu.slots) {
         if (slot2.index >= 9 && slot2.index <= 44 && slot2.getItem().isEmpty()) {
            method904(slot2.index, 0, ContainerInput.PICKUP, false);
            return;
         }
      }

      method904(index, 0, ContainerInput.PICKUP, false);
      method904(slot.index, 0, ContainerInput.PICKUP, false);
   }

   @Generated
   private InventoryUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final Slot slot;
      private final int intValue;
      private final ItemStack itemStack;

      public Slot slot() {
         return this.slot;
      }

      public int intValue() {
         return this.intValue;
      }

      public ItemStack itemStack() {
         return this.itemStack;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.slot, other.slot) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.itemStack, other.itemStack);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(slot, intValue, itemStack);
      }

      @Override
      public String toString() {
         return "Data[" + "slot=" + this.slot + ", " + "intValue=" + this.intValue + ", " + "itemStack=" + this.itemStack + "]";
      }

      public Data(Slot slot, int intValue, ItemStack itemStack) {
         this.slot = slot;
         this.intValue = intValue;
         this.itemStack = itemStack;
      }

      public static InventoryUtils.Data method944(Slot slot) {
         return slot == null ? null : new InventoryUtils.Data(slot, -1, slot.getItem());
      }

      public boolean method945() {
         return this.intValue >= 0;
      }

      public Slot getSlot() {
         return this.slot;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }
   }
}
