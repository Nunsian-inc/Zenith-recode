package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.zenith.utility.misc.CustomEnchantMatcher;
import org.zenith.utility.misc.EnchantMatcher;
import org.zenith.utility.misc.VanillaEnchantMatcher;
import org.zenith.utility.mixin.accessors.ItemStackAccessor;

public final class ItemNbtUtils {
   private static final Pattern pattern = Pattern.compile("Цена:\\s*([\\d,\\s]+)");
   private static final Pattern pattern2 = Pattern.compile("\\$\\s*([0-9][0-9\\s,]*)");
   private static final Pattern pattern3 = Pattern.compile("\\$(\\d+(?:[\\s,]\\d{3})*(?:\\.\\d{2})?)");
   private static final Pattern pattern4 = Pattern.compile("\\$(\\d+(?:[\\s,  \u200f]\\d{3})*(?:\\.\\d{2})?)");
   private static final Pattern pattern5 = Pattern.compile("\\$\\s*([0-9][0-9\\s,   \u200f]*(?:\\.\\d{1,2})?)");
   private static final Pattern pattern6 = Pattern.compile("\\$\\s*[^0-9]{0,40}([0-9][0-9\\s,   \u200f]*(?:\\.\\d{1,2})?)");
   private static final Pattern pattern7 = Pattern.compile("Цена:(?:.*?\\{\"text\":\"([\\d ]+)\")");
   private static final Pattern pattern8 = Pattern.compile("Цена за 1 ед\\.:(?:.*?\\{\"text\":\"([\\d ]+)\")");
   private static final Pattern pattern9 = Pattern.compile("([0-9][0-9\\s,]*)");
   private static final Pattern pattern10 = Pattern.compile("(?:Количество|Кол-во)\\D{0,3}(\\d[\\d ]*)");
   private static final Pattern pattern11 = Pattern.compile("[x×х]\\s*(\\d{1,6})\\s*$", 2);
   private static final Pattern pattern12 = Pattern.compile("^(\\d{1,6})\\s*[x×х]\\s", 2);
   private static final Pattern pattern13 = Pattern.compile("Цена за 1 ед\\.?:\\s*([\\d ]+)\\s*¤");
   private static final Pattern pattern14 = Pattern.compile("Цена:\\s*([\\d ]+)\\s*¤");
   private static final String text = "literal{ $";
   private static final String text2 = "}[style={color=green}]";
   private static final Map<ItemStack, Integer> items = Collections.synchronizedMap(new LinkedHashMap<ItemStack, Integer>(128, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<ItemStack, Integer> eldest) {
         return this.size() > 256;
      }
   });
   private static final Map<ItemStack, CompoundTag> items2 = new LinkedHashMap<ItemStack, CompoundTag>(100, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<ItemStack, CompoundTag> eldest) {
         return this.size() > 100;
      }
   };
   public static List<String> items3 = new ArrayList<>();
   private static final Map<ItemNbtUtils.Data, Integer> items4 = new LinkedHashMap<ItemNbtUtils.Data, Integer>(256, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<ItemNbtUtils.Data, Integer> eldest) {
         return this.size() > 512;
      }
   };

   public static int method59(String input) {
      Matcher matcher = pattern2.matcher(input);
      if (matcher.find()) {
         return method72(matcher.group(1));
      } else {
         Matcher matcher2 = pattern.matcher(input);
         if (matcher2.find()) {
            String str = matcher2.group(1).replace(",", "").replace(" ", "");
            return method75(method74(str));
         } else {
            Matcher matcher3 = pattern7.matcher(input);
            return matcher3.find() ? method72(matcher3.group(1)) : Integer.MAX_VALUE;
         }
      }
   }

   private static long method60(String input) {
      int n = method59(input);
      return n == Integer.MAX_VALUE ? Long.MAX_VALUE : n;
   }

   public static boolean method61(ItemStack stack) {
      return stack.getCustomName().getString().contains("★");
   }

   public static int method62(ItemStack key) {
      if (key != null && !key.isEmpty()) {
         Integer n = items.get(key);
         if (n != null) {
            return n;
         } else {
            int n2 = method63(key);
            items.put(key, n2);
            return n2;
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method63(ItemStack key) {
      int n = method66(key);
      if (n == Integer.MAX_VALUE) {
         n = method67(key);
      }

      if (n == Integer.MAX_VALUE) {
         String string = method76(key);
         n = method69(string);
      }

      if (n == Integer.MAX_VALUE) {
         ItemLore itemLore = key.get(DataComponents.LORE);
         if (itemLore != null) {
            n = method59(itemLore.styledLines().stream().map(Component::getString).collect(Collectors.joining()));
         }
      }

      if (n == Integer.MAX_VALUE) {
         n = method59(method76(key));
      }

      return n;
   }

   public static int method64(ItemStack key) {
      if (key != null && !key.isEmpty()) {
         int n = method62(key);
         if (n == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
         } else {
            int n2 = Math.max(1, key.getCount());
            return Math.max(1, n / n2);
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static int method65(String input, int n) {
      Matcher matcher = pattern8.matcher(input);
      if (matcher.find()) {
         String str = matcher.group(1).replace(" ", "");
         return method75(method74(str));
      } else {
         long l = method60(input);
         int n2 = Math.max(1, n);
         return l == Long.MAX_VALUE ? Integer.MAX_VALUE : method75(Math.max(1L, l / n2));
      }
   }

   private static int method66(ItemStack key) {
      DataComponentMap components = key.getComponents();
      if (components != null) {
         String string = components.toString();
         int n = method70(method73(string, "literal{ $", "}[style={color=green}]"));
         if (n != Integer.MAX_VALUE) {
            return n;
         }

         int n2 = method69(string);
         if (n2 != Integer.MAX_VALUE) {
            return n2;
         }
      }

      if (key.getHoverName() != null) {
         int n = method69(key.getHoverName().getString());
         if (n != Integer.MAX_VALUE) {
            return n;
         }
      }

      return Integer.MAX_VALUE;
   }

   private static int method67(ItemStack key) {
      ItemLore itemLore = key.get(DataComponents.LORE);
      if (itemLore == null) {
         return Integer.MAX_VALUE;
      } else {
         int n = Integer.MAX_VALUE;

         for (Component styledLine : itemLore.styledLines()) {
            if (styledLine != null) {
               String string = styledLine.getString();
               if (string != null && !string.isEmpty()) {
                  int n2 = method68(string);
                  if (n2 != Integer.MAX_VALUE && (n == Integer.MAX_VALUE || n2 < n)) {
                     n = n2;
                  }
               }
            }
         }

         return n;
      }
   }

   private static int method68(String string) {
      String lowerCase = string.toLowerCase(Locale.ROOT);
      if (!lowerCase.contains("$") && !lowerCase.contains("цен") && !lowerCase.contains("price")) {
         return Integer.MAX_VALUE;
      } else {
         int n = method69(string);
         return n != Integer.MAX_VALUE ? n : method71(string);
      }
   }

   private static int method69(String string) {
      if (string != null && !string.isEmpty()) {
         Matcher matcher = pattern5.matcher(string);
         if (matcher.find()) {
            int n = method70(matcher.group(1));
            if (n != Integer.MAX_VALUE) {
               return n;
            }
         }

         matcher = pattern6.matcher(string);
         if (matcher.find()) {
            int n = method70(matcher.group(1));
            if (n != Integer.MAX_VALUE) {
               return n;
            }
         }

         matcher = pattern4.matcher(string);
         return matcher.find() ? method70(matcher.group(1)) : Integer.MAX_VALUE;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method70(String group) {
      if (group != null && !group.isEmpty()) {
         String str = group.replaceAll("[\\s,  \u200f]", "");
         int indexOf = str.indexOf(46);
         if (indexOf >= 0) {
            str = str.substring(0, indexOf);
         }

         if (str.isEmpty()) {
            return Integer.MAX_VALUE;
         } else {
            try {
               long l = Long.parseLong(str);
               return l > 0L && l <= 2147483647L ? (int)l : Integer.MAX_VALUE;
            } catch (NumberFormatException e) {
               return Integer.MAX_VALUE;
            }
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method71(String string) {
      if (string != null && !string.isEmpty()) {
         int n = Integer.MAX_VALUE;
         Matcher matcher = pattern9.matcher(string);

         while (matcher.find()) {
            int n2 = method72(matcher.group(1));
            if (n2 != Integer.MAX_VALUE && (n == Integer.MAX_VALUE || n2 > n)) {
               n = n2;
            }
         }

         return n;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method72(String group) {
      if (group != null && !group.isEmpty()) {
         String str = group.replaceAll("[^0-9]", "");
         if (str.isEmpty()) {
            return Integer.MAX_VALUE;
         } else {
            try {
               long l = Long.parseLong(str);
               return l > 0L && l <= 2147483647L ? (int)l : Integer.MAX_VALUE;
            } catch (NumberFormatException e) {
               return Integer.MAX_VALUE;
            }
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static String method73(String string, String str, String str2) {
      if (string != null && str != null && str2 != null) {
         int indexOf = string.indexOf(str);
         if (indexOf < 0) {
            return null;
         } else {
            int fromIndex = indexOf + str.length();
            int endIndex = string.indexOf(str2, fromIndex);
            return endIndex >= 0 && endIndex > fromIndex ? string.substring(fromIndex, endIndex) : null;
         }
      } else {
         return null;
      }
   }

   private static long method74(String str) {
      try {
         return Long.parseLong(str);
      } catch (NumberFormatException e) {
         return Long.MAX_VALUE;
      }
   }

   private static int method75(long l) {
      return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
   }

   public static String method76(ItemStack key) {
      return method78(key).toString();
   }

   public static String method77(ItemStack stack) {
      CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
      if (customData != null && customData.copyTag().contains("sphereEffect")) {
         Tag tag = customData.copyTag().get("sphereEffect");
         Minecraft.getInstance().keyboardHandler.setClipboard(tag.toString());
         return tag.toString();
      } else {
         return "";
      }
   }

   public static CompoundTag method78(ItemStack key) {
      PatchedDataComponentMap components = ((ItemStackAccessor)(Object)key).getComponents();
      DataComponentPatch patch = components.asPatch();
      ClientLevel level = Minecraft.getInstance().level;
      return level == null
         ? new CompoundTag()
         : items2.computeIfAbsent(
            key, var2x -> (CompoundTag)DataComponentPatch.CODEC.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), patch).getOrThrow()
         );
   }

   public static String method79(ItemStack key) {
      PatchedDataComponentMap components = ((ItemStackAccessor)(Object)key).getComponents();
      DataComponentPatch patch = components.asPatch();
      ClientLevel level = Minecraft.getInstance().level;
      return level == null
         ? ""
         : items2.computeIfAbsent(
               key, var2x -> (CompoundTag)DataComponentPatch.CODEC.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), patch).getOrThrow()
            )
            .toString()
            .replaceAll(",?\\s*PublicBukkitValues:\\{[^}]*\\}", "")
            .replaceAll("'\\{[^']*Истeкaeт:[^']*\\}',?", "")
            .replaceAll(",?UUID:\\[I;[-0-9]+,[-0-9]+,[-0-9]+,[-0-9]+]", "")
            .replaceAll("minecraft:[0-9a-f\\-]{36}", "minecraft:UUID");
   }

   public static ArrayList<EnchantMatcher> method80(ItemStack stack) {
      ArrayList<EnchantMatcher> enchantMatchers = new ArrayList<>();
      CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
      if (customData != null && customData.copyTag().contains("Enchantments")) {
         ListTag listOrEmpty = customData.copyTag().getListOrEmpty("Enchantments");

         for (int i = 0; i < listOrEmpty.size(); i++) {
            CompoundTag compoundOrEmpty = listOrEmpty.getCompoundOrEmpty(i);
            String stringOr = compoundOrEmpty.getStringOr("id", "");
            int intOr = compoundOrEmpty.getIntOr("lvl", 0);
            enchantMatchers.add(new CustomEnchantMatcher(stringOr, stringOr, intOr));
         }
      }

      ItemEnchantments enchantments = stack.getEnchantments();

      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry entry : enchantments.entrySet()) {
         String string = ((ResourceKey)((Holder)entry.getKey()).unwrapKey().get()).identifier().toString();
         enchantMatchers.add(new VanillaEnchantMatcher(string, string, entry.getIntValue()));
      }

      return enchantMatchers;
   }

   public static boolean method81(AbstractContainerMenu containerMenu) {
      return containerMenu == null ? false : method82(containerMenu) || method83(containerMenu);
   }

   public static boolean method82(AbstractContainerMenu containerMenu) {
      return containerMenu.slots.size() == 90 && containerMenu.getSlot(49).getItem().getItem() == Items.NETHER_STAR;
   }

   public static boolean method83(AbstractContainerMenu menu) {
      return menu.slots.size() <= 49 ? false : method85(method84(menu, 45)) || method86(method84(menu, 49));
   }

   private static ItemStack method84(AbstractContainerMenu menu, int index) {
      return menu.slots.size() <= index ? ItemStack.EMPTY : menu.getSlot(index).getItem();
   }

   private static boolean method85(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() == Items.PAPER) {
         String string = stack.getHoverName().getString();
         return string.contains("[⟲]") && string.toLowerCase(Locale.ROOT).contains("обновить");
      } else {
         return false;
      }
   }

   private static boolean method86(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() == Items.NETHER_STAR;
   }

   public static boolean method87(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String lowerCase = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
         if (!lowerCase.contains("не актуален") && !lowerCase.contains("продано")) {
            ItemLore itemLore = stack.get(DataComponents.LORE);
            if (itemLore == null) {
               return false;
            } else {
               for (Component styledLine : itemLore.styledLines()) {
                  String str = styledLine.getString().toLowerCase(Locale.ROOT);
                  if (str.contains("продано")) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static boolean method88(AbstractContainerMenu abstractContainerMenu) {
      return abstractContainerMenu.slots.size() == 63 && abstractContainerMenu.getSlot(0).getItem().getItem() == Items.STAINED_GLASS_PANE.pick(net.minecraft.world.item.DyeColor.LIME);
   }

   public static void method89(int n) {
   }

   public static int method90(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         int n = Math.max(method92(stack), method95(stack));
         return Math.max(stack.getCount(), n);
      } else {
         return 0;
      }
   }

   public static int method91(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         ItemNbtUtils.Data data = new ItemNbtUtils.Data(stack.get(DataComponents.LORE), stack.getHoverName(), Language.getInstance());
         Integer n = items4.get(data);
         if (n == null) {
            n = Math.max(method92(stack), method95(stack));
            items4.put(data, n);
         }

         return Math.max(stack.getCount(), n);
      } else {
         return 0;
      }
   }

   private static int method92(ItemStack stack) {
      ItemLore itemLore = stack.get(DataComponents.LORE);
      if (itemLore == null) {
         return 0;
      } else {
         long l = 0L;
         long l2 = 0L;

         for (Component line : itemLore.lines()) {
            String input = line.getString().replace(' ', ' ');
            int n = method96(pattern10.matcher(input));
            if (n > 0) {
               return n;
            }

            if (l2 <= 0L) {
               Matcher matcher = pattern13.matcher(input);
               if (matcher.find()) {
                  l2 = method94(matcher.group(1));
                  continue;
               }
            }

            if (l <= 0L) {
               Matcher matcher = pattern14.matcher(input);
               if (matcher.find()) {
                  l = method94(matcher.group(1));
               }
            }
         }

         return method93(l, l2);
      }
   }

   private static int method93(long l, long l2) {
      if (l > 0L && l2 > 0L && l >= l2) {
         long l3 = Math.round((double)l / l2);
         if (l3 >= 1L && l3 <= 1000000L) {
            return Math.abs(l3 * l2 - l) > l3 ? 0 : (int)l3;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static long method94(String group) {
      try {
         return Long.parseLong(group.replace(" ", ""));
      } catch (NumberFormatException e) {
         return 0L;
      }
   }

   private static int method95(ItemStack stack) {
      if (stack.getHoverName() == null) {
         return 0;
      } else {
         String input = stack.getHoverName().getString().replace(' ', ' ').trim();
         if (input.isEmpty()) {
            return 0;
         } else {
            int n = method96(pattern11.matcher(input));
            return n > 0 ? n : method96(pattern12.matcher(input));
         }
      }
   }

   private static int method96(Matcher matcher) {
      if (!matcher.find()) {
         return 0;
      } else {
         try {
            long l = Long.parseLong(matcher.group(1).replace(" ", ""));
            if (l > 0L && l <= 1000000L) {
               return (int)l;
            }
         } catch (NumberFormatException e) {
         }

         return 0;
      }
   }

   @Generated
   private ItemNbtUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static final class Data {
      private final Object obj;
      private final Object obj2;
      private final Object obj3;

      public Object obj() {
         return this.obj;
      }

      public Object obj2() {
         return this.obj2;
      }

      public Object obj3() {
         return this.obj3;
      }

      @Override
      public String toString() {
         return "Data[" + "obj=" + this.obj + ", " + "obj2=" + this.obj2 + ", " + "obj3=" + this.obj3 + "]";
      }

      private Data(Object obj, Object hoverName, Object obj2) {
         this.obj = obj;
         this.obj2 = hoverName;
         this.obj3 = obj2;
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof ItemNbtUtils.Data data && data.obj == this.obj && data.obj2 == this.obj2 && data.obj3 == this.obj3;
      }

      @Override
      public int hashCode() {
         return 31 * (31 * System.identityHashCode(this.obj) + System.identityHashCode(this.obj2)) + System.identityHashCode(this.obj3);
      }

      public Object getObj() {
         return this.obj;
      }

      public Object getObj2() {
         return this.obj2;
      }

      public Object getObj3() {
         return this.obj3;
      }
   }
}
