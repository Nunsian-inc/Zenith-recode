package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.api.Class1;

public class CraftRecipe {
   private final String text;
   private final String text2;
   private String text3;
   private String text4 = "";
   private String text5 = "";
   private String text6 = "";
   private boolean flag;
   private boolean flag2;
   private boolean locked;
   private final List<String> items = new ArrayList<>();
   private final List<String> items2 = new ArrayList<>();
   private final Map<String, Class1> items3 = new HashMap<>();
   private Class1 class1 = Class1.INSTANCE;
   private Class1 class12 = Class1.INSTANCE;
   private ItemStack itemStack;
   private String text7;
   private boolean flag3;
   private transient Map<String, Integer> items4;

   public CraftRecipe(String text, String str, String string) {
      this.text = text;
      this.text2 = str;
      this.text3 = string;

      for (int i = 0; i < 9; i++) {
         this.items.add("");
         this.items2.add("");
      }
   }

   public boolean method5386() {
      return this.items.stream().allMatch(String::isBlank);
   }

   public List<String> method5387() {
      ArrayList<String> strings = new ArrayList<>();

      for (String str : this.items) {
         if (!str.isBlank() && !strings.contains(str)) {
            strings.add(str);
         }
      }

      return strings;
   }

   public Map<String, Integer> method5388() {
      var unmodifiableMap = this.items4;
      if (unmodifiableMap != null) {
         return unmodifiableMap;
      } else {
         HashMap map = new HashMap();

         for (int i = 0; i < this.items.size(); i++) {
            String str = this.items.get(i);
            if (!str.isBlank()) {
               String key = this.method5394(i);
               map.put(key, ((Integer)map.getOrDefault(key, 0)) + 1);
            }
         }

         unmodifiableMap = Collections.unmodifiableMap(map);
         this.items4 = unmodifiableMap;
         return unmodifiableMap;
      }
   }

   public int method5389(String key) {
      return this.method5388().getOrDefault(key, 0);
   }

   public void method5390(int n, String text) {
      this.items.set(n, text == null ? "" : text);
      this.items4 = null;
   }

   public String method5391(int n) {
      return this.items.get(n);
   }

   public void method5392(int n, String str) {
      this.items2.set(n, str == null ? "" : str);
      this.items4 = null;
   }

   public String method5393(int n) {
      return this.items2.get(n);
   }

   public String method5394(int n) {
      String str = this.method5391(n);
      String str2 = this.method5393(n);
      return str2.isBlank() ? str : str + "#" + str2;
   }

   public String method5395(String key) {
      int indexOf = key.indexOf(35);
      return indexOf == -1 ? key : key.substring(0, indexOf);
   }

   public String method5396(String key) {
      int indexOf = key.indexOf(35);
      return indexOf == -1 ? "" : key.substring(indexOf + 1);
   }

   public String method5397(String key, AutoCraftModule autoCraftModule) {
      return autoCraftModule.method1107(this.method5395(key), this.method5396(key));
   }

   public Class1 method5398(String key) {
      return this.items3.get(key);
   }

   public boolean method5399(ItemStack carried, AutoCraftModule autoCraftModule) {
      return autoCraftModule.method1073(carried, this.text4, this.text5);
   }

   public String method5400(AutoCraftModule autoCraftModule) {
      return autoCraftModule.method1107(this.text4, this.text5);
   }

   public ItemStack method5401(AutoCraftModule autoCraftModule) {
      String identifier = this.text6.isBlank() ? this.text4 : this.text6;
      ItemStack itemStack = this.itemStack;
      if (itemStack != null && identifier.equals(this.text7) && this.flag == this.flag3) {
         return itemStack;
      } else {
         Item item = autoCraftModule.method1117(identifier);
         ItemStack defaultInstance = item == Items.AIR ? ItemStack.EMPTY : item.getDefaultInstance();
         if (!defaultInstance.isEmpty() && this.flag) {
            defaultInstance.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
         }

         this.itemStack = defaultInstance;
         this.text7 = identifier;
         this.flag3 = this.flag;
         return defaultInstance;
      }
   }

   public String getId() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public void method5402(String str) {
      this.text3 = str == null ? "" : str;
   }

   public String getText4() {
      return this.text4;
   }

   public void method5403(String str) {
      this.text4 = str == null ? "" : str;
   }

   public String getText5() {
      return this.text5;
   }

   public void method5404(String str) {
      this.text5 = str == null ? "" : str;
   }

   public void method5405(String str) {
      this.text6 = str == null ? "" : str;
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean method5406() {
      return !this.flag2;
   }

   public void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public List<String> getItems() {
      return this.items;
   }

   public List<String> getItems2() {
      return this.items2;
   }

   public Map<String, Class1> getItems3() {
      return this.items3;
   }

   public Class1 getClass1() {
      return this.class1;
   }

   public void method5407(Class1 class2) {
      this.class1 = class2 == null ? Class1.INSTANCE : class2;
   }

   public Class1 getClass12() {
      return this.class12;
   }

   public void method5408(Class1 class2) {
      this.class12 = class2 == null ? Class1.INSTANCE : class2;
   }
}
