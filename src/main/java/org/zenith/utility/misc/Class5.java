package org.zenith.utility.misc;

import java.util.Arrays;

public final class Class5 {
   private final int intValue;
   private byte byteValue;
   private byte[] byteValues;

   public Class5(int intValue, byte byteValue) {
      this.intValue = intValue;
      this.byteValue = byteValue;
   }

   public byte method521(int n, int n2, int n3) {
      return this.byteValues == null ? this.byteValue : this.byteValues[this.method527(n, n2, n3)];
   }

   public void method522(int n, int n2, int n3, byte b) {
      if (this.byteValues == null) {
         if (b == this.byteValue) {
            return;
         }

         this.byteValues = new byte[this.intValue * this.intValue * this.intValue];
         Arrays.fill(this.byteValues, this.byteValue);
      }

      this.byteValues[this.method527(n, n2, n3)] = b;
   }

   public boolean method523() {
      return this.byteValues == null;
   }

   public byte getByteValue() {
      return this.byteValue;
   }

   public boolean method524() {
      if (this.byteValues == null) {
         return true;
      } else {
         byte byteValue = this.byteValues[0];

         for (int n : this.byteValues) {
            if (n != byteValue) {
               return false;
            }
         }

         this.byteValue = byteValue;
         this.byteValues = null;
         return true;
      }
   }

   public int method525(byte b) {
      if (this.byteValues == null) {
         return this.byteValue > b ? this.intValue * this.intValue * this.intValue : 0;
      } else {
         int n = 0;

         for (int byteValue : this.byteValues) {
            if (byteValue > b) {
               n++;
            }
         }

         return n;
      }
   }

   public long method526() {
      return this.byteValues == null ? 16L : 16L + this.byteValues.length;
   }

   private int method527(int n, int n2, int n3) {
      return (n2 * this.intValue + n3) * this.intValue + n;
   }
}
