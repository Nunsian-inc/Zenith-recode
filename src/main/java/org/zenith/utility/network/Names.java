package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowItem;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.client.events.UpdateEvent;

public final class Names implements MinecraftAccess {
   private static final Type type = (new TypeToken<LinkedHashMap<String, String>>() {}).getType();
   private static final String text = "item-names.json";
   private static final long CONST_longValue = 15000L;
   private static final int CONST_intValue = 4000;
   private static final int CONST_intValue2 = 12;
   private static final Map<String, String> items = new ConcurrentHashMap<>();
   private static final Pattern pattern = Pattern.compile("§.");
   private static volatile boolean dirty;
   private static long longValue;
   private static final Names.Data data = new Names.Data(null, null, null, null, null, null, null, null, null);
   private static final Map<ItemStack, Names.Data> items2 = new WeakHashMap<>();
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "zenith-item-names");
      thread.setDaemon(true);
      return thread;
   });

   public Names() {
      load();
      EventManager.register(this);
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         Packet packet3 = event.getPacket();
         if (packet3 instanceof ClientboundContainerSetSlotPacket containerSetSlotPacket) {
            method4515(containerSetSlotPacket.getItem());
         } else if (packet3 instanceof ClientboundContainerSetContentPacket setContentPacket) {
            for (ItemStack second : setContentPacket.items()) {
               method4515(second);
            }
         } else if (packet3 instanceof ClientboundSetEquipmentPacket setEquipmentPacket) {
            for (Pair slot : setEquipmentPacket.getSlots()) {
               method4515((ItemStack)slot.getSecond());
            }
         } else if (packet3 instanceof ClientboundSystemChatPacket systemChatPacket) {
            method4514(systemChatPacket.content(), 0);
         } else if (packet3 instanceof ClientboundDisguisedChatPacket disguisedChatPacket) {
            method4514(disguisedChatPacket.message(), 0);
         } else if (packet3 instanceof ClientboundPlayerChatPacket playerChatPacket) {
            method4514(playerChatPacket.unsignedContent().orElse(null), 0);
         }
      }
   }

   private static void method4514(Component content, int n) {
      if (content != null && n <= 12) {
         if ((content.getStyle() == null ? null : content.getStyle().getHoverEvent()) instanceof ShowItem showItem) {
            method4515(showItem.item().create());
         }

         for (Component sibling : content.getSiblings()) {
            method4514(sibling, n + 1);
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null) {
         for (ItemStack nonEquipmentItem : mc.player.getInventory().getNonEquipmentItems()) {
            method4515(nonEquipmentItem);
         }

         method4515(mc.player.getOffhandItem());
      }

      method4524();
   }

   public static void method4515(ItemStack second) {
      if (!mc.isSameThread()) {
         String str = method4522(second);
         if (str != null) {
            method4521(method4520(second), str);
            method4521(method4523(second), str);
         }
      } else {
         Names.Data data = method4518(second);
         if (data.getText2() != null) {
            method4521(data.getText3(), data.getText2());
            method4521(data.getText4(), data.getText2());
         }
      }
   }

   public static String method4516(ItemStack itemStack) {
      if (!mc.isSameThread()) {
         return method4522(itemStack) != null ? null : method4517(method4520(itemStack), method4523(itemStack));
      } else {
         Names.Data data = method4518(itemStack);
         return data.getText2() != null ? null : method4517(data.getText3(), data.getText4());
      }
   }

   private static String method4517(String str, String str2) {
      for (String str3 : new String[]{str, str2}) {
         if (str3 != null) {
            String str4 = items.get(str3);
            if (str4 != null) {
               return str4;
            }
         }
      }

      return null;
   }

   private static Names.Data method4518(ItemStack second) {
      if (second != null && !second.isEmpty()) {
         CustomData nbtComponent = second.get(DataComponents.CUSTOM_DATA);
         Component text = second.get(DataComponents.CUSTOM_NAME);
         ItemEnchantments itemEnchantmentsComponent = second.get(DataComponents.ENCHANTMENTS);
         Identifier identifier = second.get(DataComponents.ITEM_MODEL);
         CustomModelData customModelDataComponent = second.get(DataComponents.CUSTOM_MODEL_DATA);
         Language language = Language.getInstance();
         Names.Data data2 = items2.get(second);
         if (data2 != null && data2.method4526(nbtComponent, text, itemEnchantmentsComponent, identifier, customModelDataComponent, language)) {
            return data2;
         } else {
            data2 = new Names.Data(nbtComponent, text, itemEnchantmentsComponent, identifier, customModelDataComponent, language, method4522(second), method4520(second), method4523(second));
            items2.put(second, data2);
            return data2;
         }
      } else {
         return data;
      }
   }

   public static List<String> method4519(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
         if (customData == null) {
            return List.of();
         } else {
            CompoundTag copyTag = customData.copyTag();
            if (!copyTag.contains("Enchantments")) {
               return List.of();
            } else {
               ListTag listOrEmpty = copyTag.getListOrEmpty("Enchantments");
               ArrayList<String> strings = new ArrayList<>();

               for (int i = 0; i < listOrEmpty.size(); i++) {
                  CompoundTag compoundOrEmpty = listOrEmpty.getCompoundOrEmpty(i);
                  String stringOr = compoundOrEmpty.getStringOr("id", "");
                  if (!stringOr.isEmpty()) {
                     strings.add(stringOr + ":" + compoundOrEmpty.getShortOr("lvl", (short)0));
                  }
               }

               return strings;
            }
         }
      } else {
         return List.of();
      }
   }

   private static String method4520(ItemStack second) {
      var list = new ArrayList<>(method4519(second));
      if (list.isEmpty()) {
         return null;
      } else {
         Collections.sort(list);
         return BuiltInRegistries.ITEM.getKey(second.getItem()) + "|nbt|" + String.join(",", list);
      }
   }

   private static void method4521(String str, String str2) {
      if (str != null) {
         String anObject = items.get(str);
         if (!str2.equals(anObject)) {
            if (anObject != null || items.size() < 4000) {
               items.put(str, str2);
               dirty = true;
            }
         }
      }
   }

   public static int size() {
      return items.size();
   }

   private static String method4522(ItemStack second) {
      if (second != null && !second.isEmpty()) {
         CustomData customData = second.get(DataComponents.CUSTOM_DATA);
         if (customData != null) {
            CompoundTag copyTag = customData.copyTag();
            if (copyTag.contains("itemServiceId")) {
               String stringOr = copyTag.getCompoundOrEmpty("itemServiceId").getStringOr("name", "");
               if (!stringOr.isEmpty()) {
                  return stringOr.replace("_", " ");
               }
            }
         }

         Component component = second.get(DataComponents.CUSTOM_NAME);
         if (component == null) {
            return null;
         } else {
            String str = pattern.matcher(component.getString()).replaceAll("").trim();
            return str.isEmpty() ? null : str;
         }
      } else {
         return null;
      }
   }

   private static String method4523(ItemStack second) {
      if (second != null && !second.isEmpty()) {
         ItemEnchantments itemEnchantments = second.get(DataComponents.ENCHANTMENTS);
         if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
            ArrayList list = new ArrayList();

            for (Holder enchantment : itemEnchantments.keySet()) {
               String str = (String)enchantment.unwrapKey().map(var0x -> ((net.minecraft.resources.ResourceKey<?>)var0x).identifier().toString()).orElse(null);
               if (str != null) {
                  list.add(str + ":" + itemEnchantments.getLevel(enchantment));
               }
            }

            if (list.isEmpty()) {
               return null;
            } else {
               Collections.sort(list);
               StringBuilder builder = new StringBuilder(BuiltInRegistries.ITEM.getKey(second.getItem()).toString());
               builder.append("|lvl|").append(String.join(",", list));
               Identifier id = second.get(DataComponents.ITEM_MODEL);
               if (id != null) {
                  builder.append("|model=").append(id);
               }

               CustomModelData customModelData = second.get(DataComponents.CUSTOM_MODEL_DATA);
               if (customModelData != null) {
                  builder.append("|cmd=").append(customModelData);
               }

               return builder.toString();
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void method4524() {
      if (dirty) {
         long now = System.currentTimeMillis();
         if (now - longValue >= 15000L) {
            longValue = now;
            dirty = false;
            save();
         }
      }
   }

   public static void save() {
      LinkedHashMap src = new LinkedHashMap<>(items);

      try {
         executorService.execute(() -> method4525(src));
      } catch (Exception e) {
      }
   }

   private static void method4525(Map<String, String> src) {
      try {
         Path path = new File(Zenith.file, "item-names.json").toPath();
         Path sibling = path.resolveSibling("item-names.json.tmp");
         Files.writeString(sibling, new GsonBuilder().create().toJson(src), Charset.defaultCharset());

         try {
            Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException e) {
            Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception e) {
      }
   }

   private static void load() {
      File file = new File(Zenith.file, "item-names.json");
      if (file.exists()) {
         try (FileReader fileReader = new FileReader(file)) {
            Map map = (Map)new GsonBuilder().create().fromJson(fileReader, type);
            if (map != null) {
               items.putAll(map);
            }
         } catch (Exception e) {
         }
      }
   }

   private static final class Data {
      private final CustomData nbtComponent;
      private final Component text;
      private final ItemEnchantments itemEnchantmentsComponent;
      private final Identifier identifier;
      private final CustomModelData customModelDataComponent;
      private final Language language;
      private final String text2;
      private final String text3;
      private final String text4;

      public CustomData nbtComponent() {
         return this.nbtComponent;
      }

      public Component text() {
         return this.text;
      }

      public ItemEnchantments itemEnchantmentsComponent() {
         return this.itemEnchantmentsComponent;
      }

      public Identifier identifier() {
         return this.identifier;
      }

      public CustomModelData customModelDataComponent() {
         return this.customModelDataComponent;
      }

      public Language language() {
         return this.language;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.nbtComponent, other.nbtComponent) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.itemEnchantmentsComponent, other.itemEnchantmentsComponent) && java.util.Objects.equals(this.identifier, other.identifier) && java.util.Objects.equals(this.customModelDataComponent, other.customModelDataComponent) && java.util.Objects.equals(this.language, other.language) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(nbtComponent, text, itemEnchantmentsComponent, identifier, customModelDataComponent, language, text2, text3, text4);
      }

      @Override
      public String toString() {
         return "Data[" + "nbtComponent=" + this.nbtComponent + ", " + "text=" + this.text + ", " + "itemEnchantmentsComponent=" + this.itemEnchantmentsComponent + ", " + "identifier=" + this.identifier + ", " + "customModelDataComponent=" + this.customModelDataComponent + ", " + "language=" + this.language + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + "]";
      }

      private Data(
         CustomData nbtComponent,
         Component text,
         ItemEnchantments itemEnchantmentsComponent,
         Identifier identifier,
         CustomModelData customModelDataComponent,
         Language language,
         String str,
         String str2,
         String str3
      ) {
         this.nbtComponent = nbtComponent;
         this.text = text;
         this.itemEnchantmentsComponent = itemEnchantmentsComponent;
         this.identifier = identifier;
         this.customModelDataComponent = customModelDataComponent;
         this.language = language;
         this.text2 = str;
         this.text3 = str2;
         this.text4 = str3;
      }

      public boolean method4526(CustomData nbtComponent, Component text, ItemEnchantments itemEnchantmentsComponent, Identifier identifier, CustomModelData customModelDataComponent, Language language) {
         return this.nbtComponent == nbtComponent
            && this.text == text
            && this.itemEnchantmentsComponent == itemEnchantmentsComponent
            && this.identifier == identifier
            && this.customModelDataComponent == customModelDataComponent
            && this.language == language;
      }

      public CustomData getNbtComponent() {
         return this.nbtComponent;
      }

      public Component getText() {
         return this.text;
      }

      public ItemEnchantments getItemEnchantmentsComponent() {
         return this.itemEnchantmentsComponent;
      }

      public Identifier getIdentifier() {
         return this.identifier;
      }

      public CustomModelData getCustomModelDataComponent() {
         return this.customModelDataComponent;
      }

      public Language getLanguage() {
         return this.language;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String getText4() {
         return this.text4;
      }
   }
}
