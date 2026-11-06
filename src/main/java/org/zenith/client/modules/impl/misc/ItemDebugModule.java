package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "ItemDebug",
   category = ModuleCategory.MISC,
   description = "module.itemdebug.description"
)
public class ItemDebugModule extends Module {
   public static final ItemDebugModule INSTANCE = new ItemDebugModule();
   private final BooleanSetting clickedSlots = new BooleanSetting("module.itemDebug.clickedSlots", true);
   private final BooleanSetting crosshairTarget = new BooleanSetting("module.itemDebug.crosshairTarget", true);
   private final BooleanSetting nearbyPlayers = new BooleanSetting("module.itemDebug.nearbyPlayers", false);
   private final BooleanSetting drops = new BooleanSetting("module.itemDebug.drops", false);
   private final BooleanSetting equipmentPackets = new BooleanSetting("module.itemDebug.equipmentPackets", true);
   private final BooleanSetting containerPackets = new BooleanSetting("module.itemDebug.containerPackets", false);
   private final BooleanSetting chatSummary = new BooleanSetting("module.itemDebug.chatSummary", true);
   private final BooleanSetting toFile = new BooleanSetting("module.itemDebug.toFile", true);
   private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
   private static final double CONST_doubleValue = 16.0;
   private static final long CONST_longValue = 500L;
   private final Map<String, String> items = new HashMap<>();
   private PrintWriter printWriter;
   private File file;
   private long longValue;

   private ItemDebugModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.longValue = 0L;
      if (this.toFile.isEnabled()) {
         this.method4627();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.method4629();
   }

   @EventTarget
   public void method4620(SlotClickEvent event) {
      if (this.clickedSlots.isEnabled() && mc.player != null) {
         AbstractContainerMenu containerMenu = mc.player.containerMenu;
         if (containerMenu != null) {
            int slotIndex = event.getIntValue2();
            if (containerMenu.isValidSlotIndex(slotIndex)) {
               ItemStack second = containerMenu.getSlot(slotIndex).getItem();
               if (!second.isEmpty()) {
                  this.method4624("click", "self", "slot" + slotIndex, second, true);
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         Packet packet3 = event.getPacket();
         if (packet3 instanceof ClientboundSetEquipmentPacket setEquipmentPacket) {
            if (this.equipmentPackets.isEnabled()) {
               int entity = setEquipmentPacket.getEntity();
               ArrayList<Pair> pairs = new ArrayList();

               for (Pair slot : setEquipmentPacket.getSlots()) {
                  ItemStack second = (ItemStack)slot.getSecond();
                  if (second != null && !second.isEmpty()) {
                     pairs.add(Pair.of((EquipmentSlot)slot.getFirst(), second.copy()));
                  }
               }

               if (!pairs.isEmpty()) {
                  this.method4621(() -> {
                     String var3x = this.method4623(entity);

                     for (Pair var5x : pairs) {
                        this.method4624("packet:equip", var3x, ((EquipmentSlot)var5x.getFirst()).getSerializedName(), (ItemStack)var5x.getSecond(), false);
                     }
                  });
               }
            }
         } else if (packet3 instanceof ClientboundContainerSetSlotPacket containerSetSlotPacket) {
            if (this.containerPackets.isEnabled()) {
               ItemStack stack = containerSetSlotPacket.getItem();
               if (stack != null && !stack.isEmpty()) {
                  ItemStack second = stack.copy();
                  String string = "window" + containerSetSlotPacket.getContainerId();
                  String serializedName = "slot" + containerSetSlotPacket.getSlot();
                  this.method4621(() -> this.method4624("packet:slot", string, serializedName, second, false));
               }
            }
         } else {
            if (packet3 instanceof ClientboundContainerSetContentPacket setContentPacket) {
               if (!this.containerPackets.isEnabled()) {
                  return;
               }

               var items = setContentPacket.items();
               ArrayList<Pair> pairs = new ArrayList();

               for (int i = 0; i < items.size(); i++) {
                  ItemStack stack = (ItemStack)items.get(i);
                  if (stack != null && !stack.isEmpty()) {
                     pairs.add(Pair.of("slot" + i, stack.copy()));
                  }
               }

               if (pairs.isEmpty()) {
                  return;
               }

               String string = "window" + setContentPacket.containerId();
               this.method4621(() -> {
                  for (Pair var4x : pairs) {
                     this.method4624("packet:inv", string, (String)var4x.getFirst(), (ItemStack)var4x.getSecond(), false);
                  }
               });
            }
         }
      }
   }

   private void method4621(Runnable runnable) {
      mc.execute(() -> {
         if (this.isEnabled()) {
            runnable.run();
         }
      });
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         long now = System.currentTimeMillis();
         if (now - this.longValue >= 500L) {
            this.longValue = now;
            if (this.crosshairTarget.isEnabled() && mc.crosshairPickEntity instanceof LivingEntity player) {
               this.method4622("crosshair", player);
            }

            if (this.nearbyPlayers.isEnabled()) {
               for (Player player2 : mc.level.players()) {
                  if (player2 != mc.player && !(player2.distanceToSqr(mc.player) > 256.0)) {
                     this.method4622("nearby", player2);
                  }
               }
            }

            if (this.drops.isEnabled()) {
               for (Entity entity : mc.level.entitiesForRendering()) {
                  if (entity instanceof ItemEntity itemEntity && !(itemEntity.distanceToSqr(mc.player) > 256.0)) {
                     ItemStack second = itemEntity.getItem();
                     if (second != null && !second.isEmpty()) {
                        this.method4624("drop", "id" + itemEntity.getId(), "stack", second, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void method4622(String str, LivingEntity player) {
      String string = player.getName().getString();

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack itemBySlot;
         try {
            itemBySlot = player.getItemBySlot(slot);
         } catch (Exception e) {
            continue;
         }

         if (itemBySlot != null && !itemBySlot.isEmpty()) {
            this.method4624(str, string, slot.getSerializedName(), itemBySlot, false);
         }
      }
   }

   private String method4623(int entity) {
      if (mc.level == null) {
         return "id" + entity;
      } else {
         Entity entity2 = mc.level.getEntity(entity);
         return entity2 == null ? "id" + entity : entity2.getName().getString() + "/id" + entity;
      }
   }

   private void method4624(String str, String string, String serializedName, ItemStack second, boolean flag) {
      String str2 = str + "|" + string + "|" + serializedName;
      String str3 = this.method4626(second);
      if (flag || !str3.equals(this.items.get(str2))) {
         this.items.put(str2, str3);
         StringBuilder x = new StringBuilder();
         x.append("==================== ItemDebug [")
            .append(str)
            .append("] ")
            .append(simpleDateFormat.format(new Date()))
            .append(" ====================\n");
         x.append("Owner: ").append(string).append("  Slot: ").append(serializedName).append("\n");
         x.append("Item: ").append(BuiltInRegistries.ITEM.getKey(second.getItem())).append("\n");
         x.append("Name: ").append(second.getHoverName().getString()).append("\n");
         x.append("Count: ").append(second.getCount()).append("  Damage: ").append(second.getDamageValue()).append("\n");
         Component component = second.get(DataComponents.CUSTOM_NAME);
         x.append("CustomName: ").append(component == null ? "<none>" : component.getString()).append("\n");
         Component component2 = second.get(DataComponents.ITEM_NAME);
         x.append("ItemName: ").append(component2 == null ? "<none>" : component2.getString()).append("\n");
         ItemLore itemLore = second.get(DataComponents.LORE);
         if (itemLore != null && !itemLore.lines().isEmpty()) {
            x.append("Lore:\n");

            for (Component line : itemLore.lines()) {
               x.append("  ").append(line.getString()).append("\n");
            }
         } else {
            x.append("Lore: <none>\n");
         }

         ItemEnchantments itemEnchantments = second.get(DataComponents.ENCHANTMENTS);
         if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
            x.append("Enchantments:\n");

            for (Holder enchantment : itemEnchantments.keySet()) {
               String str4 = (String)enchantment.unwrapKey().map(obj -> ((net.minecraft.resources.ResourceKey<?>)obj).identifier().toString()).orElse((Object)"unknown");
               x.append("  ").append(str4).append(" ").append(itemEnchantments.getLevel(enchantment)).append("\n");
            }
         } else {
            x.append("Enchantments: <none>\n");
         }

         x.append("ComponentChanges: ").append(second.getComponentsPatch()).append("\n");
         x.append("Components:\n");

         for (TypedDataComponent typedDataComponent : second.getComponents()) {
            x.append("  ").append(typedDataComponent.type()).append(" = ").append(typedDataComponent.value()).append("\n");
         }

         x.append("===================================================");
         System.out.println(x);
         this.method4628(x.toString());
         if (this.chatSummary.isEnabled()) {
            ChatLogger.method2019(this.method4625(str, string, serializedName, second));
         }
      }
   }

   private String method4625(String str, String string, String serializedName, ItemStack second) {
      StringBuilder builder = new StringBuilder();
      if (second.has(DataComponents.CUSTOM_NAME)) {
         builder.append("name ");
      }

      if (second.has(DataComponents.ITEM_NAME)) {
         builder.append("itemname ");
      }

      if (second.has(DataComponents.LORE)) {
         builder.append("lore ");
      }

      if (second.has(DataComponents.ENCHANTMENTS)) {
         builder.append("ench ");
      }

      if (second.has(DataComponents.CUSTOM_DATA)) {
         builder.append("nbt ");
      }

      if (second.has(DataComponents.CUSTOM_MODEL_DATA)) {
         builder.append("model ");
      }

      if (second.has(DataComponents.ITEM_MODEL)) {
         builder.append("itemmodel ");
      }

      if (second.has(DataComponents.DAMAGE)) {
         builder.append("damage ");
      }

      if (builder.isEmpty()) {
         builder.append("<empty>");
      }

      int size = second.getComponentsPatch().size();
      return str + " " + string + " " + serializedName + ": " + BuiltInRegistries.ITEM.getKey(second.getItem()) + " changes=" + size + " [" + builder.toString().trim() + "]";
   }

   private String method4626(ItemStack second) {
      return BuiltInRegistries.ITEM.getKey(second.getItem()) + "x" + second.getCount() + ":" + second.getComponentsPatch().hashCode();
   }

   private void method4627() {
      try {
         File file = new File(mc.gameDirectory, "zenith-itemdump");
         if (!file.exists() && !file.mkdirs()) {
            return;
         }

         this.file = new File(file, "dump-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(this.file, true), true);
         ChatLogger.method2019("ItemDebug: лог " + this.file.getAbsolutePath());
      } catch (Exception e) {
         this.printWriter = null;
         this.file = null;
         ChatLogger.method2022("ItemDebug: не открылся файл лога — " + e.getMessage());
      }
   }

   private void method4628(String string) {
      if (this.toFile.isEnabled()) {
         if (this.printWriter == null) {
            this.method4627();
         }

         if (this.printWriter != null) {
            this.printWriter.println(string);
         }
      }
   }

   private void method4629() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception e) {
         }

         this.printWriter = null;
      }
   }
}
