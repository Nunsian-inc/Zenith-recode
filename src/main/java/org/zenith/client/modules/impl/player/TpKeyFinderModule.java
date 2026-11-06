package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.SpawnTracker;

@ModuleInfo(
   name = "TpKeyFinder",
   category = ModuleCategory.PLAYER,
   description = "module.tpkeyfinder.description"
)
public final class TpKeyFinderModule extends Module {
   public static final TpKeyFinderModule INSTANCE = new TpKeyFinderModule();
   private static final double CONST_doubleValue = -266.0;
   private static final double CONST_doubleValue2 = 134.0;
   private static final double CONST_doubleValue3 = 80.0;
   private static final double CONST_doubleValue4 = 36.0;
   private static final int CONST_intValue = 512;
   private static final int CONST_intValue2 = 4096;
   private static final int CONST_intValue3 = 60;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 40;
   private static final int CONST_intValue6 = 3;
   private static final int CONST_intValue7 = 40;
   private static final int CONST_intValue8 = 3;
   private static final int CONST_intValue9 = 64;
   private static final int CONST_intValue10 = 256;
   private static final int CONST_intValue11 = 256;
   private static final int CONST_intValue12 = 10;
   private static final long CONST_longValue = 1000L;
   private static final int[] CONST_intValues = new int[]{1, 0, -1, 0};
   private static final int[] CONST_intValues2 = new int[]{0, 1, 0, -1};
   private static final Pattern pattern = Pattern.compile("лечен\\S{0,8}[^0-9]{0,40}(\\d+)\\s*сек");
   private final BooleanSetting autoHeal = new BooleanSetting("module.tpKeyFinder.autoHeal", "module.tpKeyFinder.autoHeal.desc", true);
   private final SliderSetting healHp = new SliderSetting(
      "module.tpKeyFinder.healHp", 10.0F, 0.0F, 20.0F, 1.0F, "module.tpKeyFinder.healHp.desc", "", this.autoHeal::isEnabled
   );
   private final SliderSetting healFood = new SliderSetting(
      "module.tpKeyFinder.healFood", 8.0F, 0.0F, 20.0F, 1.0F, "module.tpKeyFinder.healFood.desc", "", this.autoHeal::isEnabled
   );
   private final List<TpKeyFinderModule.Entry> items = new ArrayList<>();
   private final Set<BlockPos> items2 = new HashSet<>();
   private final Set<UUID> items3 = new HashSet<>();
   private final LinkedHashSet<BlockPos> linkedHashSet = new LinkedHashSet<>();
   private final LinkedHashSet<UUID> linkedHashSet2 = new LinkedHashSet<>();
   private final LinkedHashMap<Object, Integer> linkedHashMap = new LinkedHashMap<>();
   private ClientLevel clientWorld;
   private LocalPlayer player;
   private int ticks;
   private TpKeyFinderModule.Entry entry;
   private Vec3 vec3d;
   private boolean flag;
   private boolean flag2;
   private int intValue;
   private int intValue2;
   private ChestMenu genericContainerScreenHandler;
   private int intValue3;
   private ItemStack itemStack = ItemStack.EMPTY;
   private int intValue4 = -1;
   private int intValue5;
   private boolean flag3;
   private int intValue6;
   private int intValue7;
   private int intValue8 = -1;
   private int intValue9;
   private int intValue10;
   private boolean flag4;
   private Iterator<Map.Entry<BlockPos, BlockEntity>> iterator;
   private Iterator<Entity> iterator2;
   private boolean flag5;
   private double doubleValue;
   private double doubleValue2;
   private int intValue11;
   private int intValue12 = 1;
   private int intValue13 = 1;
   private int intValue14;
   private volatile long longValue;

   private TpKeyFinderModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         this.resetAll();
         if (mc.player != null && !mc.player.getAbilities().flying) {
            ChatLogger.method2021("TpKeyFinder: включи /fly — без полёта модуль ждёт");
         }
      }
   }

   @Override
   public void onDisable() {
      this.resetAll();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.clientWorld != mc.level || this.player != mc.player) {
         this.resetAll();
         this.clientWorld = mc.level;
         this.player = mc.player;
      }

      if (mc.gameMode != null && !mc.isPaused() && mc.player.isAlive()) {
         this.ticks++;
         if (!mc.player.getAbilities().flying) {
            this.method4332();
         } else {
            this.method4338();
            this.method4324();
            if (this.genericContainerScreenHandler != null && !this.method4341()) {
               this.method4331(false, false);
            }

            if (!this.method4342()) {
               this.method4332();
            } else if (this.method4328() && mc.player.containerMenu.getCarried().isEmpty()) {
               mc.player.setXRot(90.0F);
               mc.player.xRotO = 90.0F;
               this.method4335();
               if (!SpawnTracker.isActive()) {
                  if (this.flag) {
                     this.method4323();
                  } else {
                     if (this.entry == null) {
                        this.entry = this.method4334();
                     }

                     if (this.entry != null) {
                        this.method4327();
                     } else if (this.flag4) {
                        this.method4330();
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         if (this.autoHeal.isEnabled() && event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket) {
            int n = method4339(systemChatPacket.content().getString());
            if (n > 0) {
               this.longValue = Math.max(this.longValue, System.currentTimeMillis() + n * 1000L + 500L);
            }
         }
      } else if (mc.isSameThread() && !this.flag3) {
         if (this.genericContainerScreenHandler == null
            || !(event.getPacket() instanceof ServerboundContainerClickPacket containerClickPacket && containerClickPacket.containerId() != this.genericContainerScreenHandler.containerId)
               && !(event.getPacket() instanceof ServerboundContainerClosePacket)) {
            if (this.flag && (event.getPacket() instanceof ServerboundUseItemOnPacket || event.getPacket() instanceof ServerboundInteractPacket || event.getPacket() instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket)) {
               this.method4331(true, false);
            }
         } else {
            this.method4331(false, false);
         }
      }
   }

   private void method4323() {
      if (this.entry != null && this.entry.method4346() != null) {
         if (this.method4341()) {
            this.method4325();
         } else if (!this.flag2) {
            if (mc.player.isShiftKeyDown() || ++this.intValue <= 2) {
               return;
            }

            if (this.entry.open()) {
               this.flag2 = true;
               this.intValue2++;
               this.intValue = 0;
            } else if (this.intValue >= 42) {
               this.method4331(false, false);
            }
         } else if (++this.intValue >= 40) {
            if (this.intValue2 < 3) {
               this.flag2 = false;
               this.intValue = 0;
            } else {
               this.method4331(false, false);
            }
         }
      } else {
         this.method4331(true, false);
      }
   }

   private void method4324() {
      if (this.flag2 && this.genericContainerScreenHandler == null && this.entry != null) {
         if (mc.player.containerMenu instanceof ChestMenu genericContainerScreenHandler && this.entry.method4348() && this.entry.method4349(genericContainerScreenHandler)) {
            this.genericContainerScreenHandler = genericContainerScreenHandler;
            this.intValue4 = -1;
            this.intValue = 0;
         }
      }
   }

   private void method4325() {
      if (this.intValue4 == -1) {
         if (this.intValue3 > 0) {
            this.intValue3--;
         } else {
            int containerSize = this.genericContainerScreenHandler.getContainer().getContainerSize();
            boolean flag = false;

            for (int i = 0; i < containerSize; i++) {
               Slot slot = this.genericContainerScreenHandler.getSlot(i);
               ItemStack itemStack = slot.getItem();
               if (method4340(itemStack) != null) {
                  flag = true;
                  if (slot.mayPickup(mc.player) && this.method4326(itemStack, containerSize)) {
                     this.itemStack = itemStack.copy();
                     this.intValue5 = itemStack.getCount();
                     this.intValue4 = i;
                     this.intValue = 0;
                     this.flag3 = true;

                     try {
                        mc.gameMode.handleContainerInput(this.genericContainerScreenHandler.containerId, i, 0, ContainerInput.QUICK_MOVE, mc.player);
                     } finally {
                        this.flag3 = false;
                     }

                     return;
                  }
               }
            }

            if (flag) {
               ChatLogger.method2021("TpKeyFinder: нет места в инвентаре — сундук оставлен открытым");
            }

            this.method4331(true, !flag);
         }
      } else {
         ItemStack stack = this.genericContainerScreenHandler.getSlot(this.intValue4).getItem();
         if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, this.itemStack) || stack.getCount() < this.intValue5) {
            ChatLogger.method2019("Успешно подобрано: " + method4340(this.itemStack));
            this.intValue4 = -1;
            this.intValue3 = 3;
         } else if (++this.intValue >= 40) {
            this.method4331(true, false);
         }
      }
   }

   private boolean method4326(ItemStack itemStack, int containerSize) {
      for (int i = containerSize; i < this.genericContainerScreenHandler.slots.size(); i++) {
         Slot slot = this.genericContainerScreenHandler.getSlot(i);
         if (slot.container == mc.player.getInventory() && slot.mayPlace(itemStack)) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()
               || ItemStack.isSameItemSameComponents(stack, itemStack) && stack.getCount() < Math.min(stack.getMaxStackSize(), slot.getMaxStackSize(stack))) {
               return true;
            }
         }
      }

      return false;
   }

   private void method4327() {
      Vec3 vec3d = this.entry.method4347();
      if (vec3d == null) {
         this.method4331(false, false);
      } else if (SpawnTracker.method6210(vec3d) && SpawnTracker.method6211(this, vec3d)) {
         this.vec3d = vec3d;
         this.flag = true;
         this.flag2 = false;
         this.intValue = this.intValue2 = 0;
      } else if (Math.hypot(vec3d.x - mc.player.getX(), vec3d.z - mc.player.getZ()) > 80.0) {
         this.method4329(vec3d.x, vec3d.z);
      } else {
         this.method4331(false, false);
      }
   }

   private boolean method4328() {
      if (this.vec3d == null) {
         return true;
      } else if (SpawnTracker.isActive()) {
         if (SpawnTracker.method6207(this)) {
            return true;
         } else {
            this.method4333("маршрут перехватил другой модуль");
            return false;
         }
      } else if (mc.player.distanceToSqr(this.vec3d) > 1.0) {
         this.method4333("телепорт сорвался");
         return false;
      } else {
         this.vec3d = null;
         return true;
      }
   }

   private boolean method4329(double doubleValue, double z) {
      if (!SpawnTracker.method6213(this, doubleValue, z, true)) {
         return false;
      } else {
         this.vec3d = new Vec3(doubleValue, -66.0, z);
         return true;
      }
   }

   private void method4330() {
      if (!this.flag5) {
         this.doubleValue = mc.player.getX();
         this.doubleValue2 = mc.player.getZ();
         this.flag5 = true;
      }

      if (Math.hypot(mc.player.getX() - this.doubleValue, mc.player.getZ() - this.doubleValue2) > 0.5) {
         this.method4329(this.doubleValue, this.doubleValue2);
      } else {
         double doubleValue = this.doubleValue + CONST_intValues[this.intValue11] * 80.0;
         double z = this.doubleValue2 + CONST_intValues2[this.intValue11] * 80.0;
         if (this.method4329(doubleValue, z)) {
            this.doubleValue = doubleValue;
            this.doubleValue2 = z;
            if (--this.intValue13 <= 0) {
               this.intValue11 = this.intValue11 + 1 & 3;
               if (++this.intValue14 == 2) {
                  this.intValue14 = 0;
                  this.intValue12++;
               }

               this.intValue13 = this.intValue12;
            }
         }
      }
   }

   private void method4331(boolean flag, boolean flag2) {
      if (flag2 && this.method4341() && this.genericContainerScreenHandler.getCarried().isEmpty()) {
         this.flag3 = true;

         try {
            mc.player.closeContainer();
         } finally {
            this.flag3 = false;
         }
      }

      if (this.entry != null) {
         Object key = this.entry.key();
         if (flag) {
            this.linkedHashMap.remove(key);
         } else {
            this.linkedHashMap.put(key, this.ticks + 60);
            if (this.linkedHashMap.size() > 4096) {
               this.linkedHashMap.remove(this.linkedHashMap.keySet().iterator().next());
            }
         }

         if (this.entry.blockPos != null) {
            if (flag) {
               method4344(this.linkedHashSet, this.entry.blockPos);
            }

            this.items2.remove(this.entry.blockPos);
         } else {
            if (flag) {
               method4344(this.linkedHashSet2, this.entry.uUID);
            }

            this.items3.remove(this.entry.uUID);
         }
      }

      SpawnTracker.method6209(this);
      this.vec3d = null;
      this.entry = null;
      this.genericContainerScreenHandler = null;
      this.flag = this.flag2 = false;
      this.intValue = this.intValue3 = this.intValue2 = 0;
      this.intValue4 = -1;
      this.itemStack = ItemStack.EMPTY;
   }

   private void method4332() {
      if (this.vec3d != null) {
         SpawnTracker.method6209(this);
         this.vec3d = null;
         this.flag = this.flag2 = false;
         this.intValue = this.intValue2 = 0;
      }
   }

   private void method4333(String str) {
      ChatLogger.method2021("TpKeyFinder: " + str);
      this.setToggled(false);
   }

   private TpKeyFinderModule.Entry method4334() {
      TpKeyFinderModule.Entry entry = null;
      double d = Double.MAX_VALUE;

      for (int i = this.items.size() - 1; i >= 0; i--) {
         TpKeyFinderModule.Entry entry2 = this.items.get(i);
         if (this.linkedHashMap.getOrDefault(entry2.key(), 0) <= this.ticks) {
            Vec3 pos = entry2.method4346();
            if (pos != null && method4343(pos.y)) {
               double d2 = mc.player.distanceToSqr(pos);
               if (d2 < d) {
                  d = d2;
                  entry = entry2;
               }
            } else {
               this.items.remove(i);
               entry2.method4345();
            }
         }
      }

      if (entry != null) {
         this.items.remove(entry);
      }

      return entry;
   }

   private void method4335() {
      int n = Mth.clamp(mc.options.getEffectiveRenderDistance(), 2, 32);
      int n2 = Mth.floor(mc.player.getX()) >> 4;
      int n3 = Mth.floor(mc.player.getZ()) >> 4;
      if (n2 != this.intValue6 || n3 != this.intValue7 || n != this.intValue8) {
         this.intValue6 = n2;
         this.intValue7 = n3;
         this.intValue8 = n;
         this.intValue9 = this.intValue10 = 0;
         this.flag4 = false;
         this.iterator = null;
         this.iterator2 = null;
         double d = (n + 6) * 16;
         this.items.removeIf(var2x -> {
            Vec3 var3x = var2x.method4346();
            if (var3x != null && Math.abs(var3x.x - mc.player.getX()) <= d && Math.abs(var3x.z - mc.player.getZ()) <= d) {
               return false;
            } else {
               var2x.method4345();
               return true;
            }
         });
      }

      if (this.intValue10 <= 0 || --this.intValue10 <= 0) {
         int n4 = n * 2 + 1;
         int n5 = 0;
         int n6 = 0;

         while (n6 < 256 && n5 < 64 && this.intValue9 < n4 * n4) {
            int x = n2 - n + this.intValue9 % n4;
            int z = n3 - n + this.intValue9 / n4;
            LevelChunk chunk = mc.level.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false);
            n5++;
            if (chunk == null) {
               this.iterator = null;
               this.intValue9++;
            } else {
               if (this.iterator == null) {
                  this.iterator = new ArrayList<>(chunk.getBlockEntities().entrySet()).iterator();
               }

               while (this.iterator.hasNext() && n6 < 256) {
                  Map.Entry entry = this.iterator.next();
                  n6++;
                  BlockPos key = (BlockPos)entry.getKey();
                  if (entry.getValue() instanceof ChestBlockEntity
                     && method4343(key.getY())
                     && this.items.size() < 512
                     && !this.linkedHashSet.contains(key)
                     && !this.items2.contains(key)
                     && method4337(key)) {
                     BlockPos immutable = key.immutable();
                     this.items2.add(immutable);
                     this.items.add(new TpKeyFinderModule.Entry(immutable, null));
                  }
               }

               if (this.iterator.hasNext()) {
                  break;
               }

               this.iterator = null;
               this.intValue9++;
            }
         }

         this.method4336();
         if (this.intValue9 >= n4 * n4) {
            this.flag4 = true;
            this.intValue9 = 0;
            this.intValue10 = 10;
         }
      }
   }

   private void method4336() {
      if (this.iterator2 == null) {
         ArrayList arrayList = new ArrayList();
         mc.level.entitiesForRendering().forEach(arrayList::add);
         this.iterator2 = arrayList.iterator();
      }

      for (int i = 0; i < 256 && this.iterator2.hasNext(); i++) {
         if (this.iterator2.next() instanceof MinecartChest chestMinecartEntity
            && chestMinecartEntity.isAlive()
            && !chestMinecartEntity.isRemoved()
            && mc.level.getEntity(chestMinecartEntity.getId()) == chestMinecartEntity
            && method4343(chestMinecartEntity.getY())
            && this.items.size() < 512
            && !this.linkedHashSet2.contains(chestMinecartEntity.getUUID())
            && this.items3.add(chestMinecartEntity.getUUID())) {
            this.items.add(new TpKeyFinderModule.Entry(null, chestMinecartEntity));
         }
      }

      if (!this.iterator2.hasNext()) {
         this.iterator2 = null;
      }
   }

   private static boolean method4337(BlockPos key) {
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -3; i <= 3; i++) {
         for (int j = -3; j <= 3; j++) {
            LevelChunk chunk = mc.level.getChunkSource().getChunk(key.getX() + i >> 4, key.getZ() + j >> 4, ChunkStatus.FULL, false);
            if (chunk != null) {
               for (int k = -1; k <= 1; k++) {
                  mutablePos.set(key.getX() + i, key.getY() + k, key.getZ() + j);
                  if (chunk.getBlockEntities().get(mutablePos) instanceof SpawnerBlockEntity) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void method4338() {
      if (this.autoHeal.isEnabled() && System.currentTimeMillis() >= this.longValue && mc.getConnection() != null) {
         float f = Mth.clamp(this.healHp.getValue3(), 0.0F, 20.0F);
         float f2 = Mth.clamp(this.healFood.getValue3(), 0.0F, 20.0F);
         if (f > 0.0F && mc.player.getHealth() < f || f2 > 0.0F && mc.player.getFoodData().getFoodLevel() < f2) {
            mc.getConnection().sendCommand("heal");
            this.longValue = System.currentTimeMillis() + 1000L;
         }
      }
   }

   private static int method4339(String string) {
      String lowerCase = string.replaceAll("§.", "").trim().toLowerCase(Locale.ROOT);
      if (lowerCase.contains("лечен") && (lowerCase.contains("ошибка") || lowerCase.contains("следующ") || lowerCase.contains("секунд"))) {
         Matcher matcher = pattern.matcher(lowerCase);
         if (!matcher.find()) {
            return 60;
         } else {
            try {
               return Math.clamp(Long.parseLong(matcher.group(1)), 1, 86400);
            } catch (NumberFormatException e) {
               return 86400;
            }
         }
      } else {
         return 0;
      }
   }

   private static String method4340(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return null;
      } else if (itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
         return "зачарованное золотое яблоко";
      } else if (itemStack.is(Items.GOLDEN_APPLE)) {
         return "золотое яблоко";
      } else {
         String stripFormatting = ChatFormatting.stripFormatting(itemStack.getHoverName().getString());
         if (stripFormatting != null && stripFormatting.toLowerCase(Locale.ROOT).contains("ключ-карта")) {
            return "ключ-карта";
         } else {
            ItemEnchantments itemEnchantments = itemStack.get(DataComponents.STORED_ENCHANTMENTS);
            return itemEnchantments != null && itemEnchantments.keySet().stream().anyMatch(var0x -> var0x.is(Enchantments.MENDING)) ? "книга на починку" : null;
         }
      }
   }

   private boolean method4341() {
      return this.genericContainerScreenHandler != null && mc.player != null && mc.player.containerMenu == this.genericContainerScreenHandler;
   }

   private boolean method4342() {
      if (mc.player.containerMenu != mc.player.inventoryMenu && !this.method4341()) {
         return false;
      } else {
         return mc.gui.screen() != null && !(mc.gui.screen() instanceof PauseScreen)
            ? this.method4341() && mc.gui.screen() instanceof AbstractContainerScreen containerScreen && containerScreen.getMenu() == this.genericContainerScreenHandler
            : true;
      }
   }

   private static boolean method4343(double y) {
      return y >= -266.0 && y <= 134.0;
   }

   private static <T> void method4344(LinkedHashSet<T> linkedHashSet, T blockPos) {
      linkedHashSet.add(blockPos);
      if (linkedHashSet.size() > 4096) {
         linkedHashSet.removeFirst();
      }
   }

   private void resetAll() {
      SpawnTracker.method6209(this);
      this.method4331(false, false);
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.linkedHashSet.clear();
      this.linkedHashSet2.clear();
      this.linkedHashMap.clear();
      this.ticks = 0;
      this.clientWorld = null;
      this.player = null;
      this.vec3d = null;
      this.flag5 = false;
      this.doubleValue = this.doubleValue2 = 0.0;
      this.intValue11 = this.intValue14 = 0;
      this.intValue12 = this.intValue13 = 1;
      this.intValue8 = -1;
      this.flag4 = false;
      this.iterator = null;
      this.iterator2 = null;
   }

   private final class Entry {
      private final BlockPos blockPos;
      private final MinecartChest chestMinecartEntity;
      private final UUID uUID;
      private int intValue;

      private Entry(BlockPos blockPos, MinecartChest chestMinecartEntity) {
         this.blockPos = blockPos;
         this.chestMinecartEntity = chestMinecartEntity;
         this.uUID = chestMinecartEntity == null ? null : chestMinecartEntity.getUUID();
      }

      private Object key() {
         return this.blockPos != null ? this.blockPos : this.uUID;
      }

      private void method4345() {
         if (this.blockPos != null) {
            TpKeyFinderModule.this.items2.remove(this.blockPos);
         } else {
            TpKeyFinderModule.this.items3.remove(this.uUID);
         }
      }

      private Vec3 method4346() {
         if (this.blockPos != null) {
            return this.method4351(this.blockPos) != null ? Vec3.atBottomCenterOf(this.blockPos).add(0.0, 1.0, 0.0) : null;
         } else {
            return this.chestMinecartEntity.isAlive()
                  && !this.chestMinecartEntity.isRemoved()
                  && MinecraftAccess.mc.level.getEntity(this.chestMinecartEntity.getId()) == this.chestMinecartEntity
               ? this.chestMinecartEntity.position()
               : null;
         }
      }

      private Vec3 method4347() {
         Vec3 vec3d = this.method4346();
         return vec3d == null ? null : SpawnTracker.method6215(vec3d);
      }

      private boolean method4348() {
         Vec3 pos = this.method4346();
         return pos != null && MinecraftAccess.mc.player.distanceToSqr(pos) <= 36.0;
      }

      private boolean method4349(ChestMenu genericContainerScreenHandler) {
         return this.intValue > 0 && this.method4346() != null && genericContainerScreenHandler.getContainer().getContainerSize() == this.intValue;
      }

      private boolean open() {
         if (this.method4348()
            && TpKeyFinderModule.this.method4342()
            && MinecraftAccess.mc.player.containerMenu == MinecraftAccess.mc.player.inventoryMenu) {
            TpKeyFinderModule.this.flag3 = true;

            try {
               if (this.blockPos != null) {
                  Container container = this.method4350();
                  if (container == null || !MinecraftAccess.mc.player.isWithinBlockInteractionRange(this.blockPos, 0.0)) {
                     return false;
                  }

                  BlockHitResult clip = MinecraftAccess.mc
                     .level
                     .clip(
                        new ClipContext(
                           MinecraftAccess.mc.player.getEyePosition(),
                           Vec3.atCenterOf(this.blockPos),
                           Block.OUTLINE,
                           Fluid.NONE,
                           MinecraftAccess.mc.player
                        )
                     );
                  if (clip.getType() != Type.BLOCK || !clip.getBlockPos().equals(this.blockPos)) {
                     return false;
                  }

                  this.intValue = container.getContainerSize();
                  MinecraftAccess.mc.gameMode.useItemOn(MinecraftAccess.mc.player, InteractionHand.MAIN_HAND, clip);
               } else {
                  if (!MinecraftAccess.mc.player.isWithinEntityInteractionRange(this.chestMinecartEntity, 0.0)) {
                     return false;
                  }

                  this.intValue = this.chestMinecartEntity.getContainerSize();
                  MinecraftAccess.mc.gameMode.interact(MinecraftAccess.mc.player, this.chestMinecartEntity, new net.minecraft.world.phys.EntityHitResult(this.chestMinecartEntity), InteractionHand.MAIN_HAND);
               }

               return true;
            } finally {
               TpKeyFinderModule.this.flag3 = false;
            }
         } else {
            return false;
         }
      }

      private Container method4350() {
         if (this.method4351(this.blockPos) == null) {
            return null;
         } else {
            BlockState blockState = MinecraftAccess.mc.level.getBlockState(this.blockPos);
            if (!(blockState.getBlock() instanceof ChestBlock block)) {
               return null;
            } else {
               if (blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                  BlockPos relative = this.blockPos.relative(ChestBlock.getConnectedDirection(blockState));
                  if (this.method4351(relative) == null) {
                     return null;
                  }

                  BlockState state = MinecraftAccess.mc.level.getBlockState(relative);
                  if (state.getBlock() != block
                     || state.getValue(ChestBlock.TYPE) == ChestType.SINGLE
                     || state.getValue(ChestBlock.TYPE) == blockState.getValue(ChestBlock.TYPE)
                     || state.getValue(ChestBlock.FACING) != blockState.getValue(ChestBlock.FACING)
                     || !relative.relative(ChestBlock.getConnectedDirection(state)).equals(this.blockPos)) {
                     return null;
                  }
               }

               return ChestBlock.getContainer(block, blockState, MinecraftAccess.mc.level, this.blockPos, false);
            }
         }
      }

      private ChestBlockEntity method4351(BlockPos blockPos) {
         LevelChunk chunk = MinecraftAccess.mc.level.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
         if (chunk == null) {
            return null;
         } else {
            BlockState blockState = chunk.getBlockState(blockPos);
            return blockState.getBlock() instanceof ChestBlock
                  && chunk.getBlockEntities().get(blockPos) instanceof ChestBlockEntity chestBlockEntity
                  && !chestBlockEntity.isRemoved()
                  && chestBlockEntity.getLevel() == MinecraftAccess.mc.level
                  && chestBlockEntity.isValidBlockState(blockState)
               ? chestBlockEntity
               : null;
         }
      }
   }
}
