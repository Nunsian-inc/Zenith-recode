package org.zenith.client.scripting;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.Rotation;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.ScriptPermissions;

public final class ScriptScreenApi {
   private static final Map<Script, Scriptable> items = new WeakHashMap<>();
   private static final Map<KeyMapping, int[]> items2 = new IdentityHashMap<>();
   private static final Map<KeyMapping, Script> items3 = new IdentityHashMap<>();
   private static final Map<Script, long[]> items4 = new WeakHashMap<>();

   private ScriptScreenApi() {
   }

   public static Minecraft method7043() {
      return Minecraft.getInstance();
   }

   public static Scriptable method7044(Script key) {
      return items.get(key);
   }

   public static void method7045(Script workspace, Scriptable scope) {
      items.put(workspace, scope);
      workspace.method1413(() -> items.remove(workspace, scope));
   }

   public static void method7046(Script workspace, Script.Type key, Function function) {
      ScriptModuleApi.method321(workspace, key, function);
   }

   public static String method7047(Screen screen) {
      if (screen == null) {
         return "";
      } else if (screen instanceof ChatScreen) {
         return "chat";
      } else if (screen instanceof InventoryScreen) {
         return "inventory";
      } else if (screen instanceof CreativeModeInventoryScreen) {
         return "creative";
      } else if (screen instanceof AbstractContainerScreen) {
         return "container";
      } else if (screen instanceof PauseScreen) {
         return "pause";
      } else if (screen instanceof DeathScreen) {
         return "death";
      } else if (screen instanceof TitleScreen) {
         return "title";
      } else {
         return screen instanceof NLMenuScreen ? "clickgui" : "other";
      }
   }

   public static Entity method7048(Object id) {
      if (method7043().level != null && !ScriptValueUtils.method6253(id)) {
         double number;
         if (id instanceof Scriptable scriptable) {
            if (!(ScriptValueUtils.method6263(scriptable, "id") instanceof Function function)) {
               return null;
            }

            number = Context.toNumber(function.call(Context.getCurrentContext(), scriptable, scriptable, new Object[0]));
         } else {
            number = Context.toNumber(id);
         }

         return method7043().level.getEntity((int)number);
      } else {
         return null;
      }
   }

   public static Scriptable method7049(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("exists", var0x -> method7043().player != null);
      entry2.method6274("name", var0x -> method7043().getUser().getName());
      entry2.method6274("x", var0x -> method7051() == null ? 0.0 : method7051().getX());
      entry2.method6274("y", var0x -> method7051() == null ? 0.0 : method7051().getY());
      entry2.method6274("z", var0x -> method7051() == null ? 0.0 : method7051().getZ());
      entry2.method6274(
         "pos",
         var1x -> method7051() == null
            ? ScriptValueUtils.method6270(scope, 0.0, 0.0, 0.0)
            : ScriptValueUtils.method6270(scope, method7051().getX(), method7051().getY(), method7051().getZ())
      );
      entry2.method6274("eyeY", var0x -> method7051() == null ? 0.0 : method7051().getEyeY());
      entry2.method6274("velocity", var1x -> {
         if (method7051() == null) {
            return ScriptValueUtils.method6270(scope, 0.0, 0.0, 0.0);
         } else {
            Vec3 var2x = method7051().getDeltaMovement();
            return ScriptValueUtils.method6270(scope, var2x.x, var2x.y, var2x.z);
         }
      });
      entry2.method6274("speed", var0x -> {
         if (method7051() == null) {
            return 0.0;
         } else {
            Vec3 var1x = method7051().getDeltaMovement();
            return Math.sqrt(var1x.x * var1x.x + var1x.z * var1x.z);
         }
      });
      entry2.method6274("yaw", var0x -> method7051() == null ? 0.0 : method7051().getYRot());
      entry2.method6274("pitch", var0x -> method7051() == null ? 0.0 : method7051().getXRot());
      entry2.method6274("health", var0x -> method7051() == null ? 0.0 : method7051().getHealth());
      entry2.method6274("maxHealth", var0x -> method7051() == null ? 0.0 : method7051().getMaxHealth());
      entry2.method6274("absorption", var0x -> method7051() == null ? 0.0 : method7051().getAbsorptionAmount());
      entry2.method6274("armor", var0x -> method7051() == null ? 0.0 : method7051().getArmorValue());
      entry2.method6274("hunger", var0x -> method7051() == null ? 0.0 : method7051().getFoodData().getFoodLevel());
      entry2.method6274("saturation", var0x -> method7051() == null ? 0.0 : method7051().getFoodData().getSaturationLevel());
      entry2.method6274("level", var0x -> method7051() == null ? 0.0 : method7051().experienceLevel);
      entry2.method6274("onGround", var0x -> method7051() != null && method7051().onGround());
      entry2.method6274("sneaking", var0x -> method7051() != null && method7051().isShiftKeyDown());
      entry2.method6274("sprinting", var0x -> method7051() != null && method7051().isSprinting());
      entry2.method6274("gliding", var0x -> method7051() != null && method7051().isFallFlying());
      entry2.method6274("swimming", var0x -> method7051() != null && method7051().isSwimming());
      entry2.method6274("inWater", var0x -> method7051() != null && method7051().isInWater());
      entry2.method6274("inLava", var0x -> method7051() != null && method7051().isInLava());
      entry2.method6274("onFire", var0x -> method7051() != null && method7051().isOnFire());
      entry2.method6274("usingItem", var0x -> method7051() != null && method7051().isUsingItem());
      entry2.method6274("riding", var0x -> method7051() != null && method7051().isPassenger());
      entry2.method6274("creative", var0x -> method7051() != null && method7051().isCreative());
      entry2.method6274("flying", var0x -> method7051() != null && method7051().getAbilities().flying);
      entry2.method6274("hurtTime", var0x -> method7051() == null ? 0.0 : method7051().hurtTime);
      entry2.method6274("fallDistance", var0x -> method7051() == null ? 0.0 : method7051().fallDistance);
      entry2.method6274("attackCooldown", var0x -> method7051() == null ? 0.0 : method7051().getAttackStrengthScale(0.5F));
      entry2.method6274("horizontalCollision", var0x -> method7051() != null && method7051().horizontalCollision);
      entry2.method6274(
         "moving", var0x -> method7051() != null && (method7051().input.getMoveVector().x != 0.0F || method7051().input.getMoveVector().y != 0.0F)
      );
      entry2.method6274("mainHand", var1x -> ScriptEntityApi.method6579(scope, method7051() == null ? ItemStack.EMPTY : method7051().getMainHandItem()));
      entry2.method6274("offHand", var1x -> ScriptEntityApi.method6579(scope, method7051() == null ? ItemStack.EMPTY : method7051().getOffhandItem()));
      entry2.method6274("armorItems", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method7051() != null) {
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
               var2x.add(ScriptEntityApi.method6579(scope, method7051().getItemBySlot(slot)));
            }
         }

         return ScriptValueUtils.method6267(scope, var2x);
      });
      entry2.method6274("effects", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method7051() != null) {
            for (MobEffectInstance activeEffect : method7051().getActiveEffects()) {
               var2x.add(ScriptEntityApi.method6578(scope, activeEffect));
            }
         }

         return ScriptValueUtils.method6267(scope, var2x);
      });
      entry2.method6274("effect", var1x -> {
         if (method7051() == null) {
            return null;
         } else {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");

            for (MobEffectInstance activeEffect : method7051().getActiveEffects()) {
               if (ScriptEntityApi.matches(activeEffect.getEffect().getRegisteredName(), var2x)) {
                  return ScriptEntityApi.method6578(scope, activeEffect);
               }
            }

            return null;
         }
      });
      entry2.method6274("lookingAt", var1x -> ScriptEntityApi.method6581(scope, method7043().hitResult));
      entry2.method6274("wrap", var1x -> ScriptEntityApi.method6576(scope, method7051()));
      entry2.method6274("slot", var0x -> {
         if (method7051() == null) {
            return -1.0;
         } else {
            if (ScriptValueUtils.method6252(var0x, 0)) {
               int var1x = Mth.clamp(ScriptValueUtils.method6257(var0x, 0, 0), 0, 8);
               method7051().getInventory().setSelectedSlot(var1x);
            }

            return (double)method7051().getInventory().getSelectedSlot();
         }
      });
      entry2.method6274("attack", var0x -> {
         Entity var1x = method7048(ScriptValueUtils.arg(var0x, 0));
         if (method7051() != null && var1x != null && method7043().gameMode != null && var1x != method7051()) {
            if (method7051().distanceTo(var1x) > 6.5) {
               return false;
            } else {
               method7043().gameMode.attack(method7051(), var1x);
               org.zenith.utility.game.SwingCompat.swing(method7051(), InteractionHand.MAIN_HAND);
               return true;
            }
         } else {
            return false;
         }
      });
      entry2.method6274("interact", var0x -> {
         Entity var1x = method7048(ScriptValueUtils.arg(var0x, 0));
         if (method7051() != null && var1x != null && method7043().gameMode != null) {
            method7043().gameMode.interact(method7051(), var1x, new net.minecraft.world.phys.EntityHitResult(var1x), method7050(var0x, 1));
            return true;
         } else {
            return false;
         }
      });
      entry2.method6274("useItem", var0x -> {
         if (method7051() != null && method7043().gameMode != null) {
            method7043().gameMode.useItem(method7051(), method7050(var0x, 0));
            return true;
         } else {
            return false;
         }
      });
      entry2.method6274("stopUsing", var0x -> {
         if (method7051() != null && method7043().gameMode != null) {
            method7043().gameMode.releaseUsingItem(method7051());
            return true;
         } else {
            return false;
         }
      });
      entry2.method6274("swing", var0x -> {
         if (method7051() != null) {
            org.zenith.utility.game.SwingCompat.swing(method7051(), method7050(var0x, 0));
         }

         return null;
      });
      entry2.method6274("jump", var0x -> {
         if (method7051() != null && method7051().onGround()) {
            method7051().jumpFromGround();
         }

         return null;
      });
      entry2.method6274("setSprinting", var0x -> {
         if (method7051() != null) {
            method7051().setSprinting(ScriptValueUtils.method6259(var0x, 0, true));
         }

         return null;
      });
      entry2.method6274(
         "setVelocity",
         var0x -> {
            if (method7051() != null) {
               method7051()
                  .setDeltaMovement(
                     ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
                  );
            }

            return null;
         }
      );
      entry2.method6274(
         "addVelocity",
         var0x -> {
            if (method7051() != null) {
               method7051()
                  .push(
                     ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
                  );
            }

            return null;
         }
      );
      entry2.method6274("setLook", var0x -> {
         if (method7051() != null) {
            method7051().setYRot((float)ScriptValueUtils.method6256(var0x, 0, method7051().getYRot()));
            method7051().setXRot(Mth.clamp((float)ScriptValueUtils.method6256(var0x, 1, method7051().getXRot()), -90.0F, 90.0F));
         }

         return null;
      });
      entry2.method6277("inventory", method7052(scope));
      return entry2.getScriptableObject();
   }

   private static InteractionHand method7050(Object[] objects, int n) {
      return "off".equalsIgnoreCase(ScriptValueUtils.method6254(objects, n, "main")) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
   }

   public static LocalPlayer method7051() {
      return method7043().player;
   }

   private static Scriptable method7052(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "slot",
         var1x -> {
            if (method7051() == null) {
               return ScriptEntityApi.method6579(scope, ItemStack.EMPTY);
            } else {
               int slot = ScriptValueUtils.method6257(var1x, 0, 0);
               return slot >= 0 && slot < method7051().getInventory().getContainerSize()
                  ? ScriptEntityApi.method6579(scope, method7051().getInventory().getItem(slot))
                  : ScriptEntityApi.method6579(scope, ItemStack.EMPTY);
            }
         }
      );
      entry2.method6274("size", var0x -> method7051() == null ? 0.0 : method7051().getInventory().getContainerSize());
      entry2.method6274("hotbar", var1x -> {
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < 9; i++) {
            arrayList.add(ScriptEntityApi.method6579(scope, method7051() == null ? ItemStack.EMPTY : method7051().getInventory().getItem(i)));
         }

         return ScriptValueUtils.method6267(scope, arrayList);
      });
      entry2.method6274("find", var0x -> {
         if (method7051() == null) {
            return -1.0;
         } else {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");

            for (int i = 0; i < 36; i++) {
               ItemStack stack = method7051().getInventory().getItem(i);
               if (!stack.isEmpty() && ScriptEntityApi.matches(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), var1x)) {
                  return (double)i;
               }
            }

            return -1.0;
         }
      });
      entry2.method6274("count", var0x -> {
         if (method7051() == null) {
            return 0.0;
         } else {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            int n = 0;

            for (int i = 0; i < method7051().getInventory().getContainerSize(); i++) {
               ItemStack stack = method7051().getInventory().getItem(i);
               if (!stack.isEmpty() && ScriptEntityApi.matches(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), var1x)) {
                  n += stack.getCount();
               }
            }

            return (double)n;
         }
      });
      entry2.method6274("screenSlot", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, 0);
         if (var1x < 9) {
            return 36.0 + var1x;
         } else if (var1x < 36) {
            return (double)var1x;
         } else {
            return var1x == 40 ? 45.0 : 44.0 - var1x;
         }
      });
      entry2.method6274(
         "click",
         var0x -> {
            if (method7051() != null && method7043().gameMode != null) {
               ContainerInput var1x = method7054(ScriptValueUtils.method6254(var0x, 2, "pickup"));
               method7043()
                  .gameMode
                  .handleContainerInput(
                     method7051().containerMenu.containerId,
                     ScriptValueUtils.method6257(var0x, 0, 0),
                     ScriptValueUtils.method6257(var0x, 1, 0),
                     var1x,
                     method7051()
                  );
               return true;
            } else {
               return false;
            }
         }
      );
      entry2.method6274("swap", var0x -> {
         if (method7051() != null && method7043().gameMode != null) {
            int var1x = Mth.clamp(ScriptValueUtils.method6257(var0x, 1, 0), 0, 8);
            int selected = ScriptValueUtils.method6257(var0x, 0, 0);
            if (method7051().containerMenu == method7051().inventoryMenu && selected == var1x) {
               return true;
            } else {
               method7043().gameMode.handleContainerInput(method7051().containerMenu.containerId, method7053(selected), var1x, ContainerInput.SWAP, method7051());
               return true;
            }
         } else {
            return false;
         }
      });
      entry2.method6274(
         "drop",
         var0x -> {
            if (method7051() != null && method7043().gameMode != null) {
               method7043()
                  .gameMode
                  .handleContainerInput(
                     method7051().containerMenu.containerId,
                     method7053(ScriptValueUtils.method6257(var0x, 0, 0)),
                     ScriptValueUtils.method6259(var0x, 1, false) ? 1 : 0,
                     ContainerInput.THROW,
                     method7051()
                  );
               return true;
            } else {
               return false;
            }
         }
      );
      entry2.method6274("findBlock", var0x -> {
         if (method7051() == null) {
            return -1.0;
         } else {
            for (int var1x = 0; var1x < 36; var1x++) {
               ItemStack stack = method7051().getInventory().getItem(var1x);
               if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                  return (double)var1x;
               }
            }

            return -1.0;
         }
      });
      entry2.method6274(
         "toHotbar",
         var0x -> {
            if (method7051() != null && method7043().gameMode != null) {
               int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
               if (var1x < 0 || var1x >= 36) {
                  return -1.0;
               } else if (var1x < 9) {
                  method7051().getInventory().setSelectedSlot(var1x);
                  return (double)var1x;
               } else {
                  int selectedSlot = ScriptValueUtils.method6252(var0x, 1)
                     ? Mth.clamp(ScriptValueUtils.method6257(var0x, 1, 0), 0, 8)
                     : method7051().getInventory().getSelectedSlot();
                  method7043()
                     .gameMode
                     .handleContainerInput(method7051().containerMenu.containerId, method7053(var1x), selectedSlot, ContainerInput.SWAP, method7051());
                  method7051().getInventory().setSelectedSlot(selectedSlot);
                  return (double)selectedSlot;
               }
            } else {
               return -1.0;
            }
         }
      );
      entry2.method6274("close", var0x -> {
         if (method7051() != null) {
            method7051().closeContainer();
         }

         return null;
      });
      entry2.method6274("containerOpen", var0x -> method7043().gui.screen() instanceof AbstractContainerScreen && !(method7043().gui.screen() instanceof InventoryScreen));
      entry2.method6274("containerTitle", var0x -> method7043().gui.screen() == null ? "" : method7043().gui.screen().getTitle().getString());
      entry2.method6274("containerSlots", var1x -> {
         ArrayList arrayList = new ArrayList();
         if (method7051() != null) {
            AbstractContainerMenu containerMenu = method7051().containerMenu;

            for (int i = 0; i < containerMenu.slots.size() && i < 128; i++) {
               Slot slot = containerMenu.slots.get(i);
               Scriptable scriptable = (Scriptable)ScriptEntityApi.method6579(scope, slot.getItem());
               scriptable.put("slot", scriptable, (double)i);
               scriptable.put("playerInventory", scriptable, slot.container == method7051().getInventory());
               arrayList.add(scriptable);
            }
         }

         return ScriptValueUtils.method6267(scope, arrayList);
      });
      return entry2.getScriptableObject();
   }

   public static int method7053(int selected) {
      if (method7051() == null || method7051().containerMenu != method7051().inventoryMenu) {
         return selected;
      } else if (selected < 9) {
         return 36 + selected;
      } else if (selected < 36) {
         return selected;
      } else if (selected == 40) {
         return 45;
      } else {
         return selected < 40 ? 44 - selected : selected;
      }
   }

   private static ContainerInput method7054(String str) {
      String lowerCase = str.toLowerCase(Locale.ROOT);

      return switch (lowerCase) {
         case "quick_move", "quickmove", "shift" -> ContainerInput.QUICK_MOVE;
         case "swap" -> ContainerInput.SWAP;
         case "clone" -> ContainerInput.CLONE;
         case "throw" -> ContainerInput.THROW;
         case "quick_craft" -> ContainerInput.QUICK_CRAFT;
         case "pickup_all" -> ContainerInput.PICKUP_ALL;
         default -> ContainerInput.PICKUP;
      };
   }

   public static Scriptable method7055(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("exists", var0x -> method7043().level != null);
      entry2.method6274("time", var0x -> method7043().level == null ? 0.0 : method7043().level.getDefaultClockTime());
      entry2.method6274("day", var0x -> method7043().level == null ? 0.0 : method7043().level.getDefaultClockTime() / 24000L);
      entry2.method6274("dimension", var0x -> method7043().level == null ? "" : method7043().level.dimension().identifier().toString());
      entry2.method6274("raining", var0x -> method7043().level != null && method7043().level.isRaining());
      entry2.method6274("thundering", var0x -> method7043().level != null && method7043().level.isThundering());
      entry2.method6274("players", var1x -> {
         ArrayList arrayList = new ArrayList();
         if (method7043().level != null) {
            for (Player player : method7043().level.players()) {
               if (player != method7043().player && arrayList.size() < 512) {
                  arrayList.add(ScriptEntityApi.method6576(scope, player));
               }
            }
         }

         return ScriptValueUtils.method6267(scope, arrayList);
      });
      entry2.method6274("entities", var1x -> {
         ArrayList arrayList = new ArrayList();
         boolean flag = !ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true);
         double d = ScriptValueUtils.method6256(var1x, 1, 128.0);
         if (method7043().level != null && method7051() != null) {
            for (Entity entity : method7043().level.entitiesForRendering()) {
               if (entity != method7051() && (!flag || entity instanceof LivingEntity) && !(entity.distanceToSqr(method7051()) > d * d)) {
                  if (arrayList.size() >= 1024) {
                     break;
                  }

                  arrayList.add(ScriptEntityApi.method6576(scope, entity));
               }
            }
         }

         return ScriptValueUtils.method6267(scope, arrayList);
      });
      entry2.method6274(
         "entity",
         var1x -> method7043().level == null
            ? null
            : ScriptEntityApi.method6576(scope, method7043().level.getEntity(ScriptValueUtils.method6257(var1x, 0, -1)))
      );
      entry2.method6274(
         "block",
         var1x -> ScriptEntityApi.method6580(
            scope,
            BlockPos.containing(
               ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            )
         )
      );
      entry2.method6274(
         "blockAt",
         var1x -> ScriptEntityApi.method6580(
            scope,
            BlockPos.containing(
               ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            )
         )
      );
      entry2.method6274(
         "blockId",
         var0x -> {
            if (method7043().level == null) {
               return "minecraft:air";
            } else {
               BlockPos var1x = BlockPos.containing(
                  ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
               );
               return BuiltInRegistries.BLOCK.getKey(method7043().level.getBlockState(var1x).getBlock()).toString();
            }
         }
      );
      entry2.method6274("findBlocks", var1x -> {
         ArrayList arrayList = new ArrayList();
         if (method7043().level != null && method7051() != null) {
            String str = ScriptValueUtils.method6254(var1x, 0, "");
            int reach = Mth.clamp(ScriptValueUtils.method6257(var1x, 1, 8), 1, 24);
            int n = Mth.clamp(ScriptValueUtils.method6257(var1x, 2, 256), 1, 2048);
            BlockPos blockPosition = method7051().blockPosition();

            for (BlockPos pos : BlockPos.withinManhattan(blockPosition, reach)) {
               if (ScriptEntityApi.matches(BuiltInRegistries.BLOCK.getKey(method7043().level.getBlockState(pos).getBlock()).toString(), str)) {
                  arrayList.add(ScriptValueUtils.method6270(scope, pos.getX(), pos.getY(), pos.getZ()));
                  if (arrayList.size() >= n) {
                     break;
                  }
               }
            }

            return ScriptValueUtils.method6267(scope, arrayList);
         } else {
            return ScriptValueUtils.method6267(scope, arrayList);
         }
      });
      entry2.method6274("raycast", var1x -> {
         if (method7051() == null) {
            return ScriptEntityApi.method6581(scope, null);
         } else {
            float rotY = (float)ScriptValueUtils.method6256(var1x, 0, method7051().getYRot());
            float rotX = (float)ScriptValueUtils.method6256(var1x, 1, method7051().getXRot());
            double scale = Mth.clamp(ScriptValueUtils.method6256(var1x, 2, 4.5), 0.1, 256.0);
            Vec3 eyePosition = method7051().getEyePosition();
            Vec3 to = eyePosition.add(Vec3.directionFromRotation(rotX, rotY).scale(scale));
            return ScriptEntityApi.method6581(scope, ScriptEntityApi.method6582(eyePosition, to, ScriptValueUtils.method6259(var1x, 3, false)));
         }
      });
      entry2.method6274(
         "raycastBetween",
         var1x -> {
            Vec3 vec = new Vec3(
               ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            );
            Vec3 vec2 = new Vec3(
               ScriptValueUtils.method6256(var1x, 3, 0.0), ScriptValueUtils.method6256(var1x, 4, 0.0), ScriptValueUtils.method6256(var1x, 5, 0.0)
            );
            if (vec.distanceToSqr(vec2) > 65536.0) {
               throw ScriptValueUtils.method6251("луч длиннее 256 блоков");
            } else {
               return ScriptEntityApi.method6581(scope, ScriptEntityApi.method6582(vec, vec2, ScriptValueUtils.method6259(var1x, 6, false)));
            }
         }
      );
      return entry2.getScriptableObject();
   }

   public static Scriptable method7056(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("press", var1x -> {
         KeyMapping var2x = method7059(ScriptValueUtils.method6254(var1x, 0, ""));
         int n = Mth.clamp(ScriptValueUtils.method6257(var1x, 1, 1), 1, 1200);
         var2x.setDown(true);
         if (n == 1 && !var2x.isUnbound()) {
            KeyMapping.click(InputConstants.getKey(var2x.saveString()));
         }

         items2.put(var2x, new int[]{n});
         items3.put(var2x, workspace);
         return null;
      });
      entry2.method6274("hold", var1x -> {
         KeyMapping var2x = method7059(ScriptValueUtils.method6254(var1x, 0, ""));
         var2x.setDown(true);
         items2.put(var2x, new int[]{Mth.clamp(ScriptValueUtils.method6257(var1x, 1, 1), 1, 1200)});
         items3.put(var2x, workspace);
         return null;
      });
      entry2.method6274("release", var0x -> {
         KeyMapping var1x = method7059(ScriptValueUtils.method6254(var0x, 0, ""));
         method7057(var1x);
         return null;
      });
      entry2.method6274("isDown", var0x -> method7059(ScriptValueUtils.method6254(var0x, 0, "")).isDown());
      entry2.method6274("keyDown", var0x -> org.zenith.utility.misc.KeyCompat.isKeyDown(ScriptValueUtils.method6257(var0x, 0, -1)));
      entry2.method6274("mouseDown", var0x -> org.zenith.utility.misc.KeyCompat.isMouseDown(ScriptValueUtils.method6257(var0x, 0, 0)));
      entry2.method6274("keyName", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
         return var1x < 0 ? "" : Type.KEYBOARD.getOrCreate(org.zenith.utility.misc.KeyCompat.toSdl(var1x)).getDisplayName().getString();
      });
      workspace.method1413(() -> {
         for (Entry var2x : new HashMap<>(items3).entrySet()) {
            if (var2x.getValue() == workspace) {
               method7057((KeyMapping)var2x.getKey());
            }
         }
      });
      return entry2.getScriptableObject();
   }

   private static void method7057(KeyMapping key) {
      items2.remove(key);
      items3.remove(key);
      long handle = method7043().getWindow().handle();
      Key key2 = InputConstants.getKey(key.saveString());
      boolean mouseDown = key2.getType() == Type.MOUSE
         ? org.zenith.utility.misc.KeyCompat.isMouseDown(key2.getValue())
         : key2.getValue() >= 0 && org.zenith.utility.misc.KeyCompat.isKeyDown(key2.getValue());
      key.setDown(mouseDown);
   }

   public static void method7058() {
      if (!items2.isEmpty()) {
         for (Entry entry : new HashMap<>(items2).entrySet()) {
            int[] ints = (int[])entry.getValue();
            ints[0]--;
            if (ints[0] <= 0) {
               method7057((KeyMapping)entry.getKey());
            } else {
               ((KeyMapping)entry.getKey()).setDown(true);
            }
         }
      }
   }

   private static KeyMapping method7059(String str) {
      Options options = method7043().options;
      String lowerCase = str.toLowerCase(Locale.ROOT);

      return switch (lowerCase) {
         case "forward" -> options.keyUp;
         case "back" -> options.keyDown;
         case "left" -> options.keyLeft;
         case "right" -> options.keyRight;
         case "jump" -> options.keyJump;
         case "sneak" -> options.keyShift;
         case "sprint" -> options.keySprint;
         case "attack" -> options.keyAttack;
         case "use" -> options.keyUse;
         case "drop" -> options.keyDrop;
         case "swap" -> options.keySwapOffhand;
         case "inventory" -> options.keyInventory;
         case "pick" -> options.keyPickItem;
         default -> throw ScriptValueUtils.method6251("неизвестная клавиша действия: " + str);
      };
   }

   public static Scriptable method7060(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      Module[] moduleValues = new Module[]{null};
      Supplier supplier = () -> {
         if (moduleValues[0] == null) {
            moduleValues[0] = ScriptModuleApi.method324(workspace, "rotation");
         }

         return moduleValues[0];
      };
      BiFunction<org.zenith.Rotation, Scriptable, Object> biFunction = (var2x, var3x) -> {
         if (method7051() == null) {
            return null;
         } else {
            float var4x = (float)Mth.clamp(ScriptValueUtils.method6264(var3x, "speed", 180.0), 1.0, 180.0);
            float var5x = (float)Mth.clamp(ScriptValueUtils.method6264(var3x, "pitchSpeed", var4x), 1.0, 180.0);
            int priority = (int)Mth.clamp(ScriptValueUtils.method6264(var3x, "priority", 3.0), 0.0, 20.0);
            int intValue = (int)Mth.clamp(ScriptValueUtils.method6264(var3x, "ticks", 1.0), 1.0, 100.0);
            Rotation rotation = new Rotation(var2x.getFloatValue(), Mth.clamp(var2x.getFloatValue2(), -90.0F, 90.0F));
            if (rotation.isInvalid()) {
               return null;
            } else {
               RotationManager rotationManager = Zenith.getInstance().getRotationManager();
               rotationManager.requestRotation(
                  new RotationRequest(rotation, () -> rotationManager.getEffectiveRotation().stepTowards(rotation, var4x, var5x), rotationManager.getBcCameraTestnerv().getRotationConfig()),
                  priority,
                  (Module)supplier.get(),
                  intValue
               );
               return ScriptValueUtils.method6247(scope)
                  .method6277("yaw", (double)rotation.getFloatValue())
                  .method6277("pitch", (double)rotation.getFloatValue2())
                  .getScriptableObject();
            }
         }
      };
      entry2.method6274(
         "set",
         var1x -> biFunction.apply(
            new Rotation((float)ScriptValueUtils.method6256(var1x, 0, 0.0), (float)ScriptValueUtils.method6256(var1x, 1, 0.0)),
            ScriptValueUtils.method6262(var1x, 2)
         )
      );
      entry2.method6274(
         "lookAt",
         var1x -> {
            if (method7051() == null) {
               return null;
            } else {
               Rotation var2x = Rotation.lookingAt(
                  new Vec3(ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)),
                  method7051().getEyePosition()
               );
               return biFunction.apply(var2x, ScriptValueUtils.method6262(var1x, 3));
            }
         }
      );
      entry2.method6274(
         "angleTo",
         var1x -> {
            if (method7051() == null) {
               return ScriptValueUtils.method6247(scope).method6277("yaw", 0.0).method6277("pitch", 0.0).getScriptableObject();
            } else {
               Rotation var2x = Rotation.lookingAt(
                  new Vec3(ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)),
                  method7051().getEyePosition()
               );
               return ScriptValueUtils.method6247(scope)
                  .method6277("yaw", (double)var2x.getFloatValue())
                  .method6277("pitch", (double)var2x.getFloatValue2())
                  .getScriptableObject();
            }
         }
      );
      entry2.method6274(
         "server",
         var1x -> {
            if (method7051() == null) {
               return ScriptValueUtils.method6247(scope).method6277("yaw", 0.0).method6277("pitch", 0.0).getScriptableObject();
            } else {
               Rotation var2x = Zenith.getInstance().getRotationManager().getEffectiveRotation();
               return ScriptValueUtils.method6247(scope)
                  .method6277("yaw", (double)var2x.getFloatValue())
                  .method6277("pitch", (double)var2x.getFloatValue2())
                  .getScriptableObject();
            }
         }
      );
      entry2.method6274(
         "camera",
         var1x -> method7051() == null
            ? ScriptValueUtils.method6247(scope).method6277("yaw", 0.0).method6277("pitch", 0.0).getScriptableObject()
            : ScriptValueUtils.method6247(scope)
               .method6277("yaw", (double)method7051().getYRot())
               .method6277("pitch", (double)method7051().getXRot())
               .getScriptableObject()
      );
      return entry2.getScriptableObject();
   }

   public static Scriptable method7061(Script workspace, Storage storage, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("msg", var1x -> {
         if (method7051() != null) {
            method7051().sendSystemMessage(Component.literal("§7[§b" + workspace.method1403() + "§7]§r " + ScriptValueUtils.method6254(var1x, 0, "")));
         }

         return null;
      });
      entry2.method6274("info", var1x -> method7063(workspace, "i", ScriptValueUtils.method6254(var1x, 0, "")));
      entry2.method6274("success", var1x -> method7063(workspace, "b", ScriptValueUtils.method6254(var1x, 0, "")));
      entry2.method6274("warn", var1x -> method7063(workspace, "d", ScriptValueUtils.method6254(var1x, 0, "")));
      entry2.method6274("error", var1x -> method7063(workspace, "d", ScriptValueUtils.method6254(var1x, 0, "")));
      entry2.method6274("say", var1x -> {
         ScriptPermissions.method4(workspace, "chat");
         if (method7043().getConnection() == null) {
            return false;
         } else {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");
            if (!var2x.isBlank() && var2x.length() <= 256) {
               method7062(workspace);
               if (var2x.startsWith("/")) {
                  method7043().getConnection().sendCommand(var2x.substring(1));
               } else {
                  method7043().getConnection().sendChat(var2x);
               }

               return true;
            } else {
               return false;
            }
         }
      });
      entry2.method6274("command", var1x -> {
         ScriptPermissions.method4(workspace, "chat");
         if (method7043().getConnection() == null) {
            return false;
         } else {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");
            if (var2x.startsWith("/")) {
               var2x = var2x.substring(1);
            }

            if (!var2x.isBlank() && var2x.length() <= 256) {
               method7062(workspace);
               method7043().getConnection().sendCommand(var2x);
               return true;
            } else {
               return false;
            }
         }
      });
      entry2.method6274("username", var0x -> method7043().getUser().getName());
      entry2.method6274("server", var0x -> method7043().getCurrentServer() == null ? "" : method7043().getCurrentServer().ip);
      entry2.method6274("fps", var0x -> (double)method7043().getFps());
      entry2.method6274("ping", var0x -> {
         if (method7051() != null && method7043().getConnection() != null) {
            PlayerInfo var1x = method7043().getConnection().getPlayerInfo(method7051().getUUID());
            return var1x == null ? 0.0 : var1x.getLatency();
         } else {
            return 0.0;
         }
      });
      entry2.method6274("inGame", var0x -> method7043().level != null && method7051() != null);
      entry2.method6274("screen", var0x -> method7047(method7043().gui.screen()));
      entry2.method6274("screenOpen", var0x -> method7043().gui.screen() != null);
      entry2.method6274("width", var0x -> (double)method7043().getWindow().getGuiScaledWidth());
      entry2.method6274("height", var0x -> (double)method7043().getWindow().getGuiScaledHeight());
      entry2.method6274("isFriend", var0x -> Zenith.getInstance().getFriends().method7627(ScriptValueUtils.method6254(var0x, 0, "")));
      entry2.method6274("setClipboard", var1x -> {
         ScriptPermissions.method4(workspace, "clipboard");
         method7043().keyboardHandler.setClipboard(ScriptValueUtils.method6254(var1x, 0, ""));
         return null;
      });
      entry2.method6274("modules", var1x -> {
         ArrayList var2x = new ArrayList();

         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            if (!moduleValue.isHidden()) {
               var2x.add(moduleValue.getName());
            }
         }

         return ScriptValueUtils.method6267(scope, var2x);
      });
      entry2.method6274("module", var2x -> {
         Module var3x = Zenith.getInstance().getModuleManager().findByName(ScriptValueUtils.method6254(var2x, 0, ""));
         if (var3x == null) {
            return null;
         } else {
            boolean flag = var3x instanceof Module1 module1 && module1.getScript() != workspace;
            return method7064(workspace, scope, var3x, flag);
         }
      });
      return entry2.getScriptableObject();
   }

   public static void method7062(Script workspace) {
      long now = System.currentTimeMillis();
      long[] longs = items4.computeIfAbsent(workspace, script -> new long[]{now, 0L});
      if (now - longs[0] > 3000L) {
         longs[0] = now;
         longs[1] = 0L;
      }

      if (++longs[1] > 6L) {
         throw ScriptValueUtils.method6251("слишком много сообщений на сервер: не больше 6 за 3 секунды");
      }
   }

   private static Object method7063(Script workspace, String icon, String str) {
      HudHolder hudHolder = Zenith.getInstance().getHudHolder();
      if (hudHolder != null) {
         hudHolder.method6972(icon, Component.literal(workspace.method1403() + ": " + str));
      }

      return null;
   }

   public static Scriptable method7064(Script workspace, Scriptable scope, Module moduleValue, boolean flag) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6277("name", moduleValue.getName());
      entry2.method6277("category", moduleValue.getCategory().getName());
      Runnable runnable = () -> {
         if (flag) {
            throw ScriptValueUtils.method6251("модулями другого скрипта управлять нельзя");
         } else {
            if (!(moduleValue instanceof Module1)) {
               ScriptPermissions.method4(workspace, "modules");
            }
         }
      };
      entry2.method6274("enabled", var1x -> moduleValue.isEnabled());
      entry2.method6274("toggle", var2x -> {
         runnable.run();
         moduleValue.toggle();
         return moduleValue.isEnabled();
      });
      entry2.method6274("setEnabled", var2x -> {
         runnable.run();
         moduleValue.setToggled(ScriptValueUtils.method6259(var2x, 0, true));
         return null;
      });
      entry2.method6274("key", var1x -> (double)moduleValue.getKeyCode());
      entry2.method6274("settings", var2x -> {
         ArrayList var3x = new ArrayList();

         for (Setting var5x : moduleValue.getSettings()) {
            var3x.add(var5x.getKey());
         }

         return ScriptValueUtils.method6267(scope, var3x);
      });
      entry2.method6274("setting", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");

         for (Setting setting : moduleValue.getSettings()) {
            if (setting.getKey().equalsIgnoreCase(var4x) || setting.getName().equalsIgnoreCase(var4x)) {
               runnable.run();
               return ScriptSettingsApi.method1223(scope, setting);
            }
         }

         return null;
      });
      return entry2.getScriptableObject();
   }

   public static Scriptable method7065(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("fps", var0x -> (double)method7043().getFps());
      entry2.method6274("paused", var0x -> method7043().isPaused());
      entry2.method6274("width", var0x -> (double)method7043().getWindow().getGuiScaledWidth());
      entry2.method6274("height", var0x -> (double)method7043().getWindow().getGuiScaledHeight());
      entry2.method6274("windowWidth", var0x -> (double)method7043().getWindow().getScreenWidth());
      entry2.method6274("windowHeight", var0x -> (double)method7043().getWindow().getScreenHeight());
      entry2.method6274("fov", var0x -> (double)method7043().options.fov().get().intValue());
      entry2.method6274("sensitivity", var0x -> method7043().options.sensitivity().get());
      entry2.method6274("renderDistance", var0x -> (double)method7043().options.renderDistance().get().intValue());
      entry2.method6274("time", var0x -> (double)System.currentTimeMillis());
      return entry2.getScriptableObject();
   }
}
