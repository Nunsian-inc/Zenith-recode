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
      AbstractContainerMenu var0 = mc.player.containerMenu;
      ItemStack var1 = BuiltInRegistries.ITEM.byId((int)MathUtils.method2651(0.0, 100.0)).getDefaultInstance();
      HashedStack var2 = HashedStack.create(var1, mc.player.connection.decoratedHashOpsGenenerator());
      mc.player
         .connection
         .send(
            new ServerboundContainerClickPacket(var0.containerId, var0.getStateId(), (short)0, (byte)0, ContainerInput.PICKUP_ALL, Int2ObjectMaps.singleton(0, var2), var2)
         );
   }

   public static void method899() {
      AbstractContainerMenu var0 = mc.player.containerMenu;
      mc.player
         .connection
         .send(
            new ServerboundContainerClickPacket(
               var0.containerId, var0.getStateId() + 1, (short)0, (byte)0, ContainerInput.PICKUP_ALL, Int2ObjectMaps.emptyMap(), HashedStack.EMPTY
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

   public static void method900(Slot var0, InteractionHand var1, boolean var2) {
      if (var0 != null
         && var0.index != -1
         && (!var1.equals(InteractionHand.OFF_HAND) || var0.container instanceof Inventory || var0.container instanceof PlayerEnderChestContainer)) {
         int var3 = var1.equals(InteractionHand.MAIN_HAND) ? mc.player.getInventory().getSelectedSlot() : 40;
         method901(var0, var3, var2);
      }
   }

   public static void method901(Slot var0, int var1, boolean var2) {
      method903(var0, var1, ContainerInput.SWAP, false);
      if (var2) {
         method897();
      }
   }

   public static void method902(Slot var0, int var1) {
      method903(var0, var1, ContainerInput.SWAP, false);
   }

   public static void method903(Slot var0, int var1, ContainerInput var2, boolean var3) {
      if (var0 != null) {
         method904(var0.index, var1, var2, var3);
      }
   }

   public static void method904(int var0, int var1, ContainerInput var2, boolean var3) {
      method905(mc.player.containerMenu.containerId, var0, var1, var2, var3);
   }

   public static void method905(int var0, int var1, int var2, ContainerInput var3, boolean var4) {
      mc.gameMode.handleContainerInput(var0, var1, var2, var3, mc.player);
      if (var4) {
         mc.player.containerMenu.clicked(var1, var2, var3, mc.player);
      }
   }

   public static Slot method906(Item var0) {
      return method907(var0, var0x -> true);
   }

   public static Slot method907(Item var0, Predicate<Slot> var1) {
      return method913(var0, Comparator.comparingInt(var0x -> var0x.index), var1);
   }

   public static Slot method908(AbstractContainerMenu var0, Item var1) {
      return var0.slots.stream().filter(var1x -> (var1x.getItem().getItem() == var1)).findFirst().orElse(null);
   }

   public static Slot method909(AbstractContainerMenu var0, Predicate<Slot> var1) {
      return var0.slots.stream().filter(var1).findFirst().orElse(null);
   }

   public static Slot method910(Predicate<Slot> var0) {
      return method926().filter(var0).findFirst().orElse(null);
   }

   public static Slot method911(Predicate<Slot> var0, Comparator<Slot> var1) {
      return method926().filter(var0).max(var1).orElse(null);
   }

   public static Slot method912(AbstractContainerMenu var0, Item var1, Comparator<Slot> var2, Predicate<Slot> var3) {
      return var0.slots.stream().filter(var1x -> var1x.getItem().getItem().equals(var1)).filter(var3).max(var2).orElse(null);
   }

   public static Slot method913(Item var0, Comparator<Slot> var1, Predicate<Slot> var2) {
      return method926().filter(var1x -> var1x.getItem().getItem().equals(var0)).filter(var2).max(var1).orElse(null);
   }

   public static Slot method914() {
      return method926()
         .filter(
            var0 -> (var0.getItem().get(DataComponents.FOOD) != null && !var0.getItem().get(DataComponents.FOOD).canAlwaysEat())
         )
         .max(Comparator.comparingDouble(var0 -> var0.getItem().get(DataComponents.FOOD).saturation()))
         .orElse(null);
   }

   public static Slot method915(List<Item> var0) {
      return method926().filter(var1 -> var0.contains(var1.getItem().getItem())).findFirst().orElse(null);
   }

   public static Slot method916(List<Item> var0, Comparator<Slot> var1) {
      return method926().filter(var1x -> var0.contains(var1x.getItem().getItem())).max(var1).orElse(null);
   }

   public static Slot method917(Holder<MobEffect> var0) {
      return method926()
         .filter(
            var1 -> {
               PotionContents var2 = var1.getItem().get(DataComponents.POTION_CONTENTS);
               return (boolean)(var2 == null
                  ? 0
                  : StreamSupport.stream(var2.getAllEffects().spliterator(), false).anyMatch(var1x -> var1x.getEffect().equals(var0)));
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static boolean method918(ItemStack var0, String var1) {
      if (var0.isEmpty()) {
         return false;
      } else {
         CustomData var2 = var0.get(DataComponents.CUSTOM_DATA);
         return (boolean)(var2 == null ? 0 : var2.copyTag().toString().contains(var1));
      }
   }

   public static Slot method919(MobEffectCategory var0) {
      return method926()
         .filter(
            var1 -> {
               ItemStack var2 = var1.getItem();
               PotionContents var3 = var2.get(DataComponents.POTION_CONTENTS);
               if (var2.getItem().equals(Items.SPLASH_POTION) && var3 != null) {
                  MobEffectCategory var4 = var0.equals(MobEffectCategory.BENEFICIAL) ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL;
                  long var5 = StreamSupport.stream(var3.getAllEffects().spliterator(), false)
                     .filter(var1x -> var1x.getEffect().value().getCategory().equals(var0))
                     .count();
                  long var7 = StreamSupport.stream(var3.getAllEffects().spliterator(), false)
                     .filter(var1x -> var1x.getEffect().value().getCategory().equals(var4))
                     .count();
                  return (var5 >= var7);
               } else {
                  return false;
               }
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static int method920(Item var0) {
      return IntStream.range(0, 45)
         .filter(var1 -> Objects.requireNonNull(mc.player).getInventory().getItem(var1).getItem().equals(var0))
         .map(var0x -> mc.player.getInventory().getItem(var0x).getCount())
         .sum();
   }

   public static int method921(List<Item> var0) {
      return IntStream.range(0, 9).filter(var1 -> var0.contains(mc.player.getInventory().getItem(var1).getItem())).findFirst().orElse(-1);
   }

   public static int method922(IntPredicate var0) {
      return IntStream.range(0, 9).filter(var0).findFirst().orElse(-1);
   }

   public static int method923(Predicate<Slot> var0) {
      return method926().filter(var0).mapToInt(var0x -> var0x.getItem().getCount()).sum();
   }

   public static Slot method924() {
      long var0;
      int var2 = (var0 = method926().count()) == 46L ? 10 : 9;
      return method926().toList().get(Math.toIntExact(var0 - var2 + mc.player.getInventory().getSelectedSlot()));
   }

   public static boolean method925() {
      return (method926().toList().size() != 46);
   }

   public static Stream<Slot> method926() {
      return mc.player.containerMenu.slots.stream();
   }

   public static void method927(Item var0) {
      method928(var0, var0x -> true);
   }

   public static void method928(Item var0, Predicate<Slot> var1) {
      method930(var0, var1, InteractionHand.MAIN_HAND);
   }

   public static void method929(Item var0, InteractionHand var1) {
      method930(var0, var0x -> true, var1);
   }

   public static void method930(Item var0, Predicate<Slot> var1, InteractionHand var2) {
      float var3;
      if ((var3 = mc.player.getCooldowns().getCooldownPercent(var0.getDefaultInstance(), 0.0F)) > 0.0F) {
         String cooldown = MathUtils.method2652(var3, 0.1) + "с";
         Zenith.getInstance()
            .getHudHolder()
            .method6972(
               "N",
               Component.translationArg(
                  var0.getName(var0.getDefaultInstance())
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
         Slot var4 = method907(var0, var1);
         if (var4 == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Component.translationArg(
                     var0.getName(var0.getDefaultInstance())
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
            TaskScheduler.Entry2 var5 = new TaskScheduler.Entry2();
            if (!InventorySettingModule.INSTANCE.method2397() && InventorySettingModule.INSTANCE.method2396()) {
               var5.method5120(HandleInputEvent.class, var2x -> {
                  if (var2x.isCancelled()) {
                     return false;
                  } else {
                     method931(var4, var2);
                     closeScreen();
                     return true;
                  }
               });
            } else {
               int var6 = Math.toIntExact(method926().count());
               int var7 = var4.index - 36;
               if (var2 == InteractionHand.MAIN_HAND && var7 >= 0 && var7 <= 8 && var6 == 46) {
                  AtomicInteger var10 = new AtomicInteger(mc.player.inventory.getSelectedSlot());
                  if (!InventorySettingModule.INSTANCE.method2396()) {
                     var5.method5120(HandleInputEvent.class, var2x -> {
                        var10.set(mc.player.inventory.getSelectedSlot());
                        mc.player.inventory.setSelectedSlot(var7);
                        return true;
                     });
                     var5.method5120(HandleInputEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        } else {
                           UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                           if (Zenith.getInstance().getServerDetector().method1642()) {
                              mc.gameMode.releaseUsingItem(mc.player);
                           }

                           mc.player.inventory.setSelectedSlot(var10.get());
                           var1x.cancel();
                           return true;
                        }
                     });
                  } else {
                     var5.method5120(HandleInputEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        } else {
                           int var2x = mc.player.inventory.getSelectedSlot();
                           mc.player.inventory.setSelectedSlot(var7);
                           UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                           mc.player.inventory.setSelectedSlot(var2x);
                           var1x.cancel();
                           return true;
                        }
                     });
                  }
               } else if (!InventorySettingModule.INSTANCE.method2396()) {
                  if (KeyBindUtils.method2630(AutoTotemModule.class) && !AutoTotemModule.INSTANCE.method725() || var2 != InteractionHand.OFF_HAND) {
                     int var8 = var2.equals(InteractionHand.MAIN_HAND) ? mc.player.getInventory().getSelectedSlot() : 40;
                     int var9 = mc.player.getInventory().getSelectedSlot();
                     var5.method5120(
                        HandleInputPostEvent.class,
                        var2x -> {
                           if (!InventorySettingModule.INSTANCE.method2397()
                              || !mc.player.lastSentInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastSentInput.forward()
                                 && !mc.player.lastSentInput.backward()
                                 && !mc.player.lastSentInput.left()
                                 && !mc.player.lastSentInput.right()) {
                              method901(var4, var8, false);
                              closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     var5.method5120(HandleInputEvent.class, var3x -> {
                        if (var3x.isCancelled()) {
                           return false;
                        } else {
                           if (var2 == InteractionHand.MAIN_HAND) {
                              mc.player.getInventory().setSelectedSlot(var8);
                              UseItemUtils.useItem(var2);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 UseItemUtils.useItem(var2);
                              }

                              mc.player.getInventory().setSelectedSlot(var9);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 mc.gameMode.releaseUsingItem(mc.player);
                              }
                           } else {
                              UseItemUtils.useItem(var2);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 UseItemUtils.useItem(var2);
                              }
                           }

                           var3x.cancel();
                           return true;
                        }
                     });
                     var5.method5120(
                        HandleInputPostEvent.class,
                        var2x -> {
                           if (!InventorySettingModule.INSTANCE.method2397()
                              || !mc.player.lastSentInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastSentInput.forward()
                                 && !mc.player.lastSentInput.backward()
                                 && !mc.player.lastSentInput.left()
                                 && !mc.player.lastSentInput.right()) {
                              method901(var4, var8, false);
                              closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     if (InventorySettingModule.INSTANCE.method2397()) {
                        var5.method5123(MoveInputEvent.class, var0x -> {
                           var0x.method3044();
                           return true;
                        });
                     }
                  }
               } else {
                  var5.method5120(
                     HandleInputEvent.class,
                     var2x -> {
                        if ((!KeyBindUtils.method2630(AutoTotemModule.class) || AutoTotemModule.INSTANCE.method725()) && var2 == InteractionHand.OFF_HAND) {
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
                           method931(var4, var2);
                           closeScreen();
                           return true;
                        } else {
                           return false;
                        }
                     }
                  );
                  if (InventorySettingModule.INSTANCE.method2397()) {
                     var5.method5123(MoveInputEvent.class, var0x -> {
                        var0x.method3044();
                        return true;
                     });
                  }
               }
            }

            Zenith.getInstance().getTaskScheduler().method5110(var5);
         }
      }
   }

   public static void method931(Slot var0, InteractionHand var1) {
      method900(var0, var1, false);
      closeScreen();
      UseItemUtils.useItem(var1);
      method900(var0, var1, true);
   }

   public static void method932(Slot var0, int var1) {
      if (var0 != null) {
         method935(var0.index, var1, false, false);
      }
   }

   public static void method933(Slot var0, int var1, boolean var2) {
      method934(var0, var1, var2, false);
   }

   public static void method934(Slot var0, int var1, boolean var2, boolean var3) {
      if (var0 != null) {
         method935(var0.index, var1, var2, var3);
      }
   }

   public static void method935(int var0, int var1, boolean var2, boolean var3) {
      if (var0 != var1 && var0 != -1) {
         int var4 = Math.toIntExact(method926().count());
         int var5;
         if ((var5 = var0 - 36) >= 0 && var5 <= 8 && var4 == 46) {
            if (var2) {
               KeyBindUtils.method2627(InventoryUtils.class, () -> method904(var1, var5, ContainerInput.SWAP, false));
            } else {
               method904(var1, var5, ContainerInput.SWAP, false);
               closeScreen();
            }
         } else if (!ElytraHelperModule.INSTANCE.isEnabled() || var1 != 6 || !ElytraHelperModule.INSTANCE.method7456()) {
            if (var2) {
               if (InventorySettingModule.INSTANCE.method2396()) {
                  KeyBindUtils.method2627(InventoryUtils.class, () -> method936(var0, var1, var3));
               } else {
                  TaskScheduler.Entry2 var6 = Zenith.getInstance()
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

                           method904(var0, 0, ContainerInput.SWAP, false);
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

                           method904(var1, 0, ContainerInput.SWAP, false);
                           closeScreen();
                           method904(var0, 0, ContainerInput.SWAP, false);
                           closeScreen();
                           return true;
                        }
                     );
                  Zenith.getInstance().getTaskScheduler().method5111(var6, 0);
               }
            } else {
               method936(var0, var1, var3);
               closeScreen();
            }
         }
      }
   }

   public static void method936(int var0, int var1, boolean var2) {
      method904(var0, 0, ContainerInput.SWAP, false);
      closeScreen();
      method904(var1, 0, ContainerInput.SWAP, false);
      method904(var0, 0, ContainerInput.SWAP, false);
      if (var2) {
         method897();
      }
   }

   public static InventoryUtils.Data method937(Item var0, Comparator<ItemStack> var1, Predicate<Slot> var2) {
      return method938(var1x -> var1x.getItem().equals(var0), var1, var2);
   }

   public static InventoryUtils.Data method938(Predicate<ItemStack> var0, Comparator<ItemStack> var1, Predicate<Slot> var2) {
      if (mc.player != null && mc.player.containerMenu != null) {
         InventoryUtils.Data var3 = null;
         InventoryUtils.Data var4 = null;

         for (Slot var6 : mc.player.containerMenu.slots) {
            if (var2.test(var6)) {
               ItemStack var7 = var6.getItem();
               if (var7.isEmpty() || !var0.test(var7)) {
                  BundleContents var8 = var7.get(DataComponents.BUNDLE_CONTENTS);
                  if (var8 != null) {
                     for (int var9 = 0; var9 < var8.size(); var9++) {
                        ItemStack var10 = var8.items().get(var9).create();
                        if (!var10.isEmpty() && var0.test(var10) && (var4 == null || var1.compare(var10, var4.getItemStack()) > 0)) {
                           var4 = new InventoryUtils.Data(var6, var9, var10);
                        }
                     }
                  }
               } else if (var3 == null || var1.compare(var7, var3.getItemStack()) > 0) {
                  var3 = InventoryUtils.Data.method944(var6);
               }
            }
         }

         return var3 != null ? var3 : var4;
      } else {
         return null;
      }
   }

   public static int method939(Predicate<ItemStack> var0, Predicate<Slot> var1) {
      if (mc.player != null && mc.player.containerMenu != null) {
         int var2 = 0;

         for (Slot var4 : mc.player.containerMenu.slots) {
            if (var1.test(var4)) {
               ItemStack var5 = var4.getItem();
               if (!var5.isEmpty() && var0.test(var5)) {
                  var2 += var5.getCount();
               } else {
                  BundleContents var6 = var5.get(DataComponents.BUNDLE_CONTENTS);
                  if (var6 != null) {
                     for (int var7 = 0; var7 < var6.size(); var7++) {
                        ItemStack var8 = var6.items().get(var7).create();
                        if (!var8.isEmpty() && var0.test(var8)) {
                           var2 += var8.getCount();
                        }
                     }
                  }
               }
            }
         }

         return var2;
      } else {
         return 0;
      }
   }

   public static boolean method940(InventoryUtils.Data var0, InteractionHand var1, boolean var2) {
      if (var0 == null) {
         return false;
      } else if (!var0.method945()) {
         method900(var0.getSlot(), var1, var2);
         return true;
      } else {
         return method941(var0.getSlot(), var0.getIntValue(), var1, var2);
      }
   }

   public static boolean method941(Slot var0, int var1, InteractionHand var2, boolean var3) {
      if (var0 != null && mc.player != null) {
         AbstractContainerMenu var4 = mc.player.containerMenu;
         if (!(var4 instanceof InventoryMenu) || !var4.getCarried().isEmpty()) {
            return false;
         } else if (!(var0.getItem().getItem() instanceof BundleItem)) {
            return false;
         } else {
            BundleContents var5 = var0.getItem().get(DataComponents.BUNDLE_CONTENTS);
            if (var5 != null && var1 >= 0 && var1 < var5.size()) {
               int var6;
               if ((var6 = var2.equals(InteractionHand.OFF_HAND) ? 45 : 36 + mc.player.getInventory().getSelectedSlot()) == var0.index) {
                  return false;
               } else {
                  method942(var0, var1);
                  method904(var0.index, 1, ContainerInput.PICKUP, false);
                  if (var4.getCarried().isEmpty()) {
                     return false;
                  } else {
                     method904(var6, 0, ContainerInput.PICKUP, false);
                     if (!var4.getCarried().isEmpty()) {
                        method904(var0.index, 0, ContainerInput.PICKUP, false);
                     }

                     if (!var4.getCarried().isEmpty()) {
                        method943(var4, var0, var6);
                     }

                     closeScreen();
                     if (var3) {
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

   private static void method942(Slot var0, int var1) {
      if (BundleItem.getSelectedItemIndex(var0.getItem()) != var1) {
         mc.player.connection.send(new ServerboundSelectBundleItemPacket(var0.index, var1));
         BundleItem.toggleSelectedItem(var0.getItem(), var1);
      }
   }

   private static void method943(AbstractContainerMenu var0, Slot var1, int var2) {
      for (Slot var4 : var0.slots) {
         if (var4.index >= 9 && var4.index <= 44 && var4.getItem().isEmpty()) {
            method904(var4.index, 0, ContainerInput.PICKUP, false);
            return;
         }
      }

      method904(var2, 0, ContainerInput.PICKUP, false);
      method904(var1.index, 0, ContainerInput.PICKUP, false);
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

      public Data(Slot var1, int var2, ItemStack var3) {
         this.slot = var1;
         this.intValue = var2;
         this.itemStack = var3;
      }

      public static InventoryUtils.Data method944(Slot var0) {
         return var0 == null ? null : new InventoryUtils.Data(var0, -1, var0.getItem());
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
