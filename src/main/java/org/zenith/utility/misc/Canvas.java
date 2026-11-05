package org.zenith.utility.misc;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class Canvas {
   public static final int CONST_intValue = 31;
   public static final int CONST_intValue2 = 15;
   private static final int CONST_intValue3 = 48;
   private int[] intValues = new int[961];
   private final Deque<int[]> deque = new ArrayDeque<>();
   private final Deque<int[]> deque2 = new ArrayDeque<>();

   public int method136(int n, int n2) {
      return n >= 0 && n2 >= 0 && n < 31 && n2 < 31 ? this.intValues[n2 * 31 + n] : 0;
   }

   public void method137(int n, int n2, int brushColor) {
      if (n >= 0 && n2 >= 0 && n < 31 && n2 < 31) {
         this.intValues[n2 * 31 + n] = brushColor;
      }
   }

   public boolean isEmpty() {
      for (int intValue : this.intValues) {
         if (intValue >>> 24 != 0) {
            return false;
         }
      }

      return true;
   }

   public int[] method138() {
      return (int[])this.intValues.clone();
   }

   public void method139(int[] backup) {
      if (backup != null && backup.length == this.intValues.length) {
         System.arraycopy(backup, 0, this.intValues, 0, this.intValues.length);
      }
   }

   public void method140() {
      this.method141();
      this.deque2.clear();
   }

   public void clear() {
      this.method141();
      this.deque2.clear();
      Arrays.fill(this.intValues, 0);
   }

   private void method141() {
      this.deque.push((int[])this.intValues.clone());

      while (this.deque.size() > 48) {
         this.deque.removeLast();
      }
   }

   public boolean method142() {
      return !this.deque.isEmpty();
   }

   public boolean method143() {
      return !this.deque2.isEmpty();
   }

   public void undo() {
      if (!this.deque.isEmpty()) {
         this.deque2.push((int[])this.intValues.clone());
         this.intValues = this.deque.pop();
      }
   }

   public void redo() {
      if (!this.deque2.isEmpty()) {
         this.deque.push((int[])this.intValues.clone());
         this.intValues = this.deque2.pop();
      }
   }

   public void method144(int n, int n2, int brushColor) {
      int n3 = this.method136(n, n2);
      if (n3 != brushColor) {
         this.method140();
         ArrayDeque arrayDeque = new ArrayDeque();
         arrayDeque.push(new int[]{n, n2});

         while (!arrayDeque.isEmpty()) {
            int[] ints = (int[])arrayDeque.pop();
            int n4 = ints[0];
            int n5 = ints[1];
            if (n4 >= 0 && n5 >= 0 && n4 < 31 && n5 < 31 && this.intValues[n5 * 31 + n4] == n3) {
               this.intValues[n5 * 31 + n4] = brushColor;
               arrayDeque.push(new int[]{n4 + 1, n5});
               arrayDeque.push(new int[]{n4 - 1, n5});
               arrayDeque.push(new int[]{n4, n5 + 1});
               arrayDeque.push(new int[]{n4, n5 - 1});
            }
         }
      }
   }

   public String method145() {
      StringBuilder builder = new StringBuilder();
      int intValue = this.intValues[0];
      int n = 1;

      for (int i = 1; i < this.intValues.length; i++) {
         if (this.intValues[i] == intValue) {
            n++;
         } else {
            method146(builder, n, intValue);
            intValue = this.intValues[i];
            n = 1;
         }
      }

      method146(builder, n, intValue);
      return builder.toString();
   }

   private static void method146(StringBuilder builder, int n, int intValue) {
      if (builder.length() > 0) {
         builder.append(';');
      }

      builder.append(n).append(':').append(Integer.toHexString(intValue));
   }

   public void method147(String string) {
      if (string != null && !string.isEmpty()) {
         int[] intValues = new int[this.intValues.length];
         int n = 0;

         try {
            for (String str : string.split(";")) {
               int indexOf = str.indexOf(58);
               if (indexOf <= 0) {
                  return;
               }

               int n2 = Integer.parseInt(str.substring(0, indexOf));
               int n3 = (int)Long.parseLong(str.substring(indexOf + 1), 16);

               for (int i = 0; i < n2 && n < intValues.length; i++) {
                  intValues[n++] = n3;
               }
            }
         } catch (RuntimeException e) {
            return;
         }

         if (n == intValues.length) {
            this.intValues = intValues;
            this.deque.clear();
            this.deque2.clear();
         }
      }
   }
}
