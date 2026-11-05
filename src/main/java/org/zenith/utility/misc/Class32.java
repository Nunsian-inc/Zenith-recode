package org.zenith.utility.misc;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public final class Class32 {
   public static final int CONST_intValue = 4;
   public static final int CONST_intValue2 = 16;
   public static final int CONST_intValue3 = 64;
   public static final byte CONST_byteValue = -127;
   public static final byte CONST_byteValue2 = 127;
   public static final byte CONST_byteValue3 = 0;
   private final Long2ObjectMap<Class5> long2ObjectMap = new Long2ObjectOpenHashMap();

   public byte method4902(int x, int n, int x2) {
      Class5 class2 = (Class5)this.long2ObjectMap.get(method4914(method4912(x), method4912(n), method4912(x2)));
      return class2 == null ? -127 : class2.method521(method4913(x), method4913(n), method4913(x2));
   }

   public float method4903(int x, int n, int n2) {
      return this.method4902(x, n, n2) / 127.0F;
   }

   public void method4904(int x, int n, int x2, byte b) {
      long key = method4914(method4912(x), method4912(n), method4912(x2));
      Class5 class2 = (Class5)this.long2ObjectMap.get(key);
      if (class2 == null) {
         if (b == -127) {
            return;
         }

         class2 = new Class5(64, (byte)-127);
         this.long2ObjectMap.put(key, class2);
      }

      class2.method522(method4913(x), method4913(n), method4913(x2), b);
   }

   public boolean method4905(int n, int n2, int n3) {
      return this.long2ObjectMap.containsKey(method4914(n, n2, n3));
   }

   public int method4906() {
      return this.long2ObjectMap.size();
   }

   public int method4907() {
      int n = 0;
      ObjectIterator objectIterator = this.long2ObjectMap.values().iterator();

      while (objectIterator.hasNext()) {
         Class5 class2 = (Class5)objectIterator.next();
         if (class2.method523()) {
            n++;
         }
      }

      return n;
   }

   public long method4908() {
      long l = 0L;
      ObjectIterator objectIterator = this.long2ObjectMap.values().iterator();

      while (objectIterator.hasNext()) {
         Class5 class2 = (Class5)objectIterator.next();
         l += class2.method525((byte)0);
      }

      return l;
   }

   public long method4909() {
      long l = 0L;
      ObjectIterator objectIterator = this.long2ObjectMap.values().iterator();

      while (objectIterator.hasNext()) {
         Class5 class2 = (Class5)objectIterator.next();
         l += class2.method526();
      }

      return l;
   }

   public int method4910() {
      LongArrayList longArrayList = new LongArrayList();
      int n = 0;
      ObjectIterator objectIterator = this.long2ObjectMap.long2ObjectEntrySet().iterator();

      while (objectIterator.hasNext()) {
         Entry entry = (Entry)objectIterator.next();
         Class5 class2 = (Class5)entry.getValue();
         boolean flag = class2.method523();
         if (class2.method524() && !flag) {
            n++;
         }

         if (class2.method523() && class2.getByteValue() == -127) {
            longArrayList.add(entry.getLongKey());
         }
      }

      for (int i = 0; i < longArrayList.size(); i++) {
         this.long2ObjectMap.remove(longArrayList.getLong(i));
      }

      return n;
   }

   public void clear() {
      this.long2ObjectMap.clear();
   }

   public static int method4911(int x) {
      return x * 4;
   }

   public static int method4912(int x) {
      return Math.floorDiv(x, 64);
   }

   private static int method4913(int x) {
      return Math.floorMod(x, 64);
   }

   private static long method4914(int n, int n2, int n3) {
      return (long)(n & 2097151) << 43 | (long)(n2 & 2097151) << 22 | n3 & 4194303;
   }
}
