package org.zenith.utility.misc;

import java.util.List;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.network.CloudPackets;

public final class PartyPlayerInfo {
   public static final long CONST_longValue = 15000L;
   private static final double CONST_doubleValue = 3.2E7;
   private static final double CONST_doubleValue2 = 1000000.0;
   private final long longValue;
   private final String text;
   private final String text2;
   private volatile String text3 = "";
   private volatile String text4 = "";
   private volatile double doubleValue;
   private volatile double doubleValue2;
   private volatile double doubleValue3;
   private volatile float floatValue;
   private volatile float floatValue2;
   private volatile long longValue2;
   private volatile double doubleValue4;
   private volatile double doubleValue5;
   private volatile double doubleValue6;
   private volatile long longValue3;
   private volatile float floatValue3;
   private volatile float floatValue4 = 20.0F;
   private volatile float floatValue5;
   private volatile int intValue = 20;
   private volatile CloudPackets.Data7[] data7s = new CloudPackets.Data7[0];
   private volatile List<CloudPackets.Data9> items = List.of();
   private volatile int intValue2;

   public PartyPlayerInfo(long longValue, String text, String anotherString) {
      this.longValue = longValue;
      this.text = text;
      this.text2 = anotherString;
   }

   public long uid() {
      return this.longValue;
   }

   public String username() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public boolean method3289() {
      return (this.intValue2 & 1) != 0;
   }

   public void setIntValue2(int intValue) {
      this.intValue2 = intValue;
   }

   public String server() {
      return this.text3;
   }

   public String dimension() {
      return this.text4;
   }

   public String method3290() {
      return this.text3.startsWith("singleplayer") ? "одиночная игра" : this.text3;
   }

   public boolean method3291(String anObject, String str) {
      return this.method3292() && this.text3.equals(anObject) && this.text4.equals(str);
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public boolean method3292() {
      long l = this.longValue2;
      return l != 0L && System.currentTimeMillis() - l <= 15000L;
   }

   public boolean method3293() {
      return this.longValue2 != 0L;
   }

   public Vec3 method3294() {
      if (this.longValue2 == 0L) {
         return null;
      } else if (this.longValue3 == 0L) {
         return new Vec3(this.doubleValue, this.doubleValue2, this.doubleValue3);
      } else {
         long l = Math.max(1L, this.longValue2 - this.longValue3);
         double d = (double)(System.currentTimeMillis() - this.longValue2) / l;
         if (d <= 0.0) {
            return new Vec3(this.doubleValue4, this.doubleValue5, this.doubleValue6);
         } else {
            return d >= 1.0
               ? new Vec3(this.doubleValue, this.doubleValue2, this.doubleValue3)
               : new Vec3(
                  this.doubleValue4 + (this.doubleValue - this.doubleValue4) * d,
                  this.doubleValue5 + (this.doubleValue2 - this.doubleValue5) * d,
                  this.doubleValue6 + (this.doubleValue3 - this.doubleValue6) * d
               );
         }
      }
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue4() {
      return this.floatValue4;
   }

   public float getFloatValue5() {
      return this.floatValue5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public CloudPackets.Data7[] getData7s() {
      return this.data7s;
   }

   public List<CloudPackets.Data9> getItems() {
      return this.items;
   }

   public void method3295(CloudPackets.Data5 data5) {
      this.text3 = data5.server();
      this.text4 = data5.dimension();
   }

   public void method3296(CloudPackets.Data2 data2) {
      if (method3300(data2.getDoubleValue(), 3.2E7)
         && method3300(data2.getDoubleValue2(), 3.2E7)
         && method3300(data2.getDoubleValue3(), 3.2E7)
         && Float.isFinite(data2.getFloatValue())
         && Float.isFinite(data2.getFloatValue2())) {
         long now = System.currentTimeMillis();
         double d = data2.getDoubleValue() - this.doubleValue;
         double d2 = data2.getDoubleValue2() - this.doubleValue2;
         double d3 = data2.getDoubleValue3() - this.doubleValue3;
         boolean flag = this.longValue2 == 0L || d * d + d2 * d2 + d3 * d3 > 64.0;
         this.doubleValue4 = flag ? data2.getDoubleValue() : this.doubleValue;
         this.doubleValue5 = flag ? data2.getDoubleValue2() : this.doubleValue2;
         this.doubleValue6 = flag ? data2.getDoubleValue3() : this.doubleValue3;
         this.longValue3 = this.longValue2;
         this.doubleValue = data2.getDoubleValue();
         this.doubleValue2 = data2.getDoubleValue2();
         this.doubleValue3 = data2.getDoubleValue3();
         this.floatValue = data2.getFloatValue();
         this.floatValue2 = data2.getFloatValue2();
         this.longValue2 = now;
      }
   }

   public void method3297(CloudPackets.Data3 data3) {
      if (method3300(data3.getFloatValue(), 1000000.0)
         && method3300(data3.getFloatValue3(), 1000000.0)
         && method3300(data3.getFloatValue2(), 1000000.0)
         && !(data3.getFloatValue2() <= 0.0F)) {
         this.floatValue3 = data3.getFloatValue();
         this.floatValue4 = data3.getFloatValue2();
         this.floatValue5 = data3.getFloatValue3();
         this.intValue = data3.getIntValue();
      }
   }

   public void method3298(CloudPackets.Data11 data11) {
      this.data7s = data11.getData7s();
   }

   public void method3299(CloudPackets.Data15 data15) {
      this.items = data15.getItems();
   }

   private static boolean method3300(double doubleValue, double d) {
      return Double.isFinite(doubleValue) && Math.abs(doubleValue) <= d;
   }

   @Override
   public String toString() {
      return this.text + " (" + this.text2 + ")";
   }
}
