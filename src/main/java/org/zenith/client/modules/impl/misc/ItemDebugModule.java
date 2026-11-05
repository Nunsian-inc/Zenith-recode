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
   public void method4620(SlotClickEvent var1) {
      if (this.clickedSlots.isEnabled() && mc.player != null) {
         AbstractContainerMenu var2 = mc.player.containerMenu;
         if (var2 != null) {
            int var3 = var1.getIntValue2();
            if (var2.isValidSlotIndex(var3)) {
               ItemStack var4 = var2.getSlot(var3).getItem();
               if (!var4.isEmpty()) {
                  this.method4624("click", "self", "slot" + var3, var4, true);
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof ClientboundSetEquipmentPacket var10) {
            if (this.equipmentPackets.isEnabled()) {
               int var12 = var10.getEntity();
               ArrayList<Pair> var14 = new ArrayList();

               for (Pair var19 : var10.getSlots()) {
                  ItemStack var8 = (ItemStack)var19.getSecond();
                  if (var8 != null && !var8.isEmpty()) {
                     var14.add(Pair.of((EquipmentSlot)var19.getFirst(), var8.copy()));
                  }
               }

               if (!var14.isEmpty()) {
                  this.method4621(() -> {
                     String var3x = this.method4623(var12);

                     for (Object var5x_o : (Iterable<?>)(Object)(var14)) {
                        Pair var5x = (Pair)var5x_o;
                        this.method4624("packet:equip", var3x, ((EquipmentSlot)var5x.getFirst()).getSerializedName(), (ItemStack)var5x.getSecond(), false);
                     }
                  });
               }
            }
         } else if (var2 instanceof ClientboundContainerSetSlotPacket var9) {
            if (this.containerPackets.isEnabled()) {
               ItemStack var11 = var9.getItem();
               if (var11 != null && !var11.isEmpty()) {
                  ItemStack var13 = var11.copy();
                  String var16 = "window" + var9.getContainerId();
                  String var18 = "slot" + var9.getSlot();
                  this.method4621(() -> this.method4624("packet:slot", var16, var18, var13, false));
               }
            }
         } else {
            if (var2 instanceof ClientboundContainerSetContentPacket var3) {
               if (!this.containerPackets.isEnabled()) {
                  return;
               }

               var var4 = var3.items();
               ArrayList<Pair> var5 = new ArrayList();

               for (int var6 = 0; var6 < var4.size(); var6++) {
                  ItemStack var7 = (ItemStack)var4.get(var6);
                  if (var7 != null && !var7.isEmpty()) {
                     var5.add(Pair.of("slot" + var6, var7.copy()));
                  }
               }

               if (var5.isEmpty()) {
                  return;
               }

               String var15 = "window" + var3.containerId();
               this.method4621(() -> {
                  for (Object var4x_o : (Iterable<?>)(Object)(var5)) {
                     Pair var4x = (Pair)var4x_o;
                     this.method4624("packet:inv", var15, (String)var4x.getFirst(), (ItemStack)var4x.getSecond(), false);
                  }
               });
            }
         }
      }
   }

   private void method4621(Runnable var1) {
      mc.execute(() -> {
         if (this.isEnabled()) {
            var1.run();
         }
      });
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.level != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.longValue >= 500L) {
            this.longValue = var2;
            if (this.crosshairTarget.isEnabled() && mc.crosshairPickEntity instanceof LivingEntity var4) {
               this.method4622("crosshair", var4);
            }

            if (this.nearbyPlayers.isEnabled()) {
               for (Player var10 : mc.level.players()) {
                  if (var10 != mc.player && !(var10.distanceToSqr(mc.player) > 256.0)) {
                     this.method4622("nearby", var10);
                  }
               }
            }

            if (this.drops.isEnabled()) {
               for (Entity var11 : mc.level.entitiesForRendering()) {
                  if (var11 instanceof ItemEntity var6 && !(var6.distanceToSqr(mc.player) > 256.0)) {
                     ItemStack var7 = var6.getItem();
                     if (var7 != null && !var7.isEmpty()) {
                        this.method4624("drop", "id" + var6.getId(), "stack", var7, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void method4622(String var1, LivingEntity var2) {
      String var3 = var2.getName().getString();

      for (EquipmentSlot var7 : EquipmentSlot.values()) {
         ItemStack var8;
         try {
            var8 = var2.getItemBySlot(var7);
         } catch (Exception var10) {
            continue;
         }

         if (var8 != null && !var8.isEmpty()) {
            this.method4624(var1, var3, var7.getSerializedName(), var8, false);
         }
      }
   }

   private String method4623(int var1) {
      if (mc.level == null) {
         return "id" + var1;
      } else {
         Entity var2 = mc.level.getEntity(var1);
         return var2 == null ? "id" + var1 : var2.getName().getString() + "/id" + var1;
      }
   }

   private void method4624(String var1, String var2, String var3, ItemStack var4, boolean var5) {
      String var6 = var1 + "|" + var2 + "|" + var3;
      String var7 = this.method4626(var4);
      if (var5 || !var7.equals(this.items.get(var6))) {
         this.items.put(var6, var7);
         StringBuilder var8 = new StringBuilder();
         var8.append("==================== ItemDebug [")
            .append(var1)
            .append("] ")
            .append(simpleDateFormat.format(new Date()))
            .append(" ====================\n");
         var8.append("Owner: ").append(var2).append("  Slot: ").append(var3).append("\n");
         var8.append("Item: ").append(BuiltInRegistries.ITEM.getKey(var4.getItem())).append("\n");
         var8.append("Name: ").append(var4.getHoverName().getString()).append("\n");
         var8.append("Count: ").append(var4.getCount()).append("  Damage: ").append(var4.getDamageValue()).append("\n");
         Component var9 = var4.get(DataComponents.CUSTOM_NAME);
         var8.append("CustomName: ").append(var9 == null ? "<none>" : var9.getString()).append("\n");
         Component var10 = var4.get(DataComponents.ITEM_NAME);
         var8.append("ItemName: ").append(var10 == null ? "<none>" : var10.getString()).append("\n");
         ItemLore var11 = var4.get(DataComponents.LORE);
         if (var11 != null && !var11.lines().isEmpty()) {
            var8.append("Lore:\n");

            for (Component var13 : var11.lines()) {
               var8.append("  ").append(var13.getString()).append("\n");
            }
         } else {
            var8.append("Lore: <none>\n");
         }

         ItemEnchantments var16 = var4.get(DataComponents.ENCHANTMENTS);
         if (var16 != null && !var16.isEmpty()) {
            var8.append("Enchantments:\n");

            for (Holder var14 : var16.keySet()) {
               String var15 = (String)var14.unwrapKey().map(var0 -> ((net.minecraft.resources.ResourceKey<?>)var0).identifier().toString()).orElse((Object)"unknown");
               var8.append("  ").append(var15).append(" ").append(var16.getLevel(var14)).append("\n");
            }
         } else {
            var8.append("Enchantments: <none>\n");
         }

         var8.append("ComponentChanges: ").append(var4.getComponentsPatch()).append("\n");
         var8.append("Components:\n");

         for (TypedDataComponent var19 : var4.getComponents()) {
            var8.append("  ").append(var19.type()).append(" = ").append(var19.value()).append("\n");
         }

         var8.append("===================================================");
         System.out.println(var8);
         this.method4628(var8.toString());
         if (this.chatSummary.isEnabled()) {
            ChatLogger.method2019(this.method4625(var1, var2, var3, var4));
         }
      }
   }

   private String method4625(String var1, String var2, String var3, ItemStack var4) {
      StringBuilder var5 = new StringBuilder();
      if (var4.has(DataComponents.CUSTOM_NAME)) {
         var5.append("name ");
      }

      if (var4.has(DataComponents.ITEM_NAME)) {
         var5.append("itemname ");
      }

      if (var4.has(DataComponents.LORE)) {
         var5.append("lore ");
      }

      if (var4.has(DataComponents.ENCHANTMENTS)) {
         var5.append("ench ");
      }

      if (var4.has(DataComponents.CUSTOM_DATA)) {
         var5.append("nbt ");
      }

      if (var4.has(DataComponents.CUSTOM_MODEL_DATA)) {
         var5.append("model ");
      }

      if (var4.has(DataComponents.ITEM_MODEL)) {
         var5.append("itemmodel ");
      }

      if (var4.has(DataComponents.DAMAGE)) {
         var5.append("damage ");
      }

      if (var5.isEmpty()) {
         var5.append("<empty>");
      }

      int var6 = var4.getComponentsPatch().size();
      return var1 + " " + var2 + " " + var3 + ": " + BuiltInRegistries.ITEM.getKey(var4.getItem()) + " changes=" + var6 + " [" + var5.toString().trim() + "]";
   }

   private String method4626(ItemStack var1) {
      return BuiltInRegistries.ITEM.getKey(var1.getItem()) + "x" + var1.getCount() + ":" + var1.getComponentsPatch().hashCode();
   }

   private void method4627() {
      try {
         File var1 = new File(mc.gameDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         this.file = new File(var1, "dump-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(this.file, true), true);
         ChatLogger.method2019("ItemDebug: лог " + this.file.getAbsolutePath());
      } catch (Exception var2) {
         this.printWriter = null;
         this.file = null;
         ChatLogger.method2022("ItemDebug: не открылся файл лога — " + var2.getMessage());
      }
   }

   private void method4628(String var1) {
      if (this.toFile.isEnabled()) {
         if (this.printWriter == null) {
            this.method4627();
         }

         if (this.printWriter != null) {
            this.printWriter.println(var1);
         }
      }
   }

   private void method4629() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }
}
