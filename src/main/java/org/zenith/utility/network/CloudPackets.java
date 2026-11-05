package org.zenith.utility.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import org.zenith.utility.misc.CosmeticType;

public final class CloudPackets {
   public static final int CONST_intValue = 5;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   public static final int CONST_intValue6 = 5;
   public static final int CONST_intValue7 = 6;
   public static final int CONST_intValue8 = 16;
   public static final int CONST_intValue9 = 17;
   public static final int CONST_intValue10 = 18;
   public static final int CONST_intValue11 = 19;
   public static final int CONST_intValue12 = 20;
   public static final int CONST_intValue13 = 32;
   public static final int CONST_intValue14 = 33;
   public static final int CONST_intValue15 = 34;
   public static final int CONST_intValue16 = 35;
   public static final int CONST_intValue17 = 36;
   public static final int CONST_intValue18 = 128;
   public static final int CONST_intValue19 = 129;
   public static final int CONST_intValue20 = 130;
   public static final int CONST_intValue21 = 131;
   public static final int CONST_intValue22 = 132;
   public static final int CONST_intValue23 = 133;
   public static final int CONST_intValue24 = 134;
   public static final int CONST_intValue25 = 135;
   public static final int CONST_intValue26 = 136;
   public static final int CONST_intValue27 = 128;
   public static final int CONST_intValue28 = 0;
   public static final int CONST_intValue29 = 1;
   public static final int CONST_intValue30 = 2;
   public static final int CONST_intValue31 = 0;
   public static final int CONST_intValue32 = 1;
   public static final int CONST_intValue33 = 0;
   public static final int CONST_intValue34 = 1;
   public static final int CONST_intValue35 = 0;
   public static final int CONST_intValue36 = 1;
   public static final int CONST_intValue37 = 1;
   public static final int CONST_intValue38 = 2;
   public static final int CONST_intValue39 = 3;
   public static final int CONST_intValue40 = 4;
   public static final int CONST_intValue41 = 5;
   public static final int CONST_intValue42 = 6;
   public static final int CONST_intValue43 = 32;
   public static final int CONST_intValue44 = 16;
   public static final int CONST_intValue45 = 200;
   public static final int CONST_intValue46 = 64;
   public static final int CONST_intValue47 = 96;
   public static final int CONST_intValue48 = 40;
   public static final int CONST_intValue49 = 12;
   public static final int CONST_intValue50 = 63;
   public static final int CONST_intValue51 = 36;
   public static final int CONST_intValue52 = 256;
   public static final int CONST_intValue53 = 32;
   public static final int CONST_intValue54 = 32;
   public static final int CONST_intValue55 = 48;
   public static final int CONST_intValue56 = 32;
   public static final int CONST_intValue57 = 2048;
   public static final int CONST_intValue58 = 1;
   public static final int CONST_intValue59 = 2;
   public static final int CONST_intValue60 = 4;
   public static final int CONST_intValue61 = 37;
   public static final int CONST_intValue62 = 8192;
   public static final int CONST_intValue63 = 4;
   public static final int CONST_intValue64 = 8;
   public static final int CONST_intValue65 = 16;
   public static final int CONST_intValue66 = 1;
   public static final int CONST_intValue67 = 2;
   public static final int CONST_intValue68 = 3;
   public static final int CONST_intValue69 = 4;
   public static final int CONST_intValue70 = 5;
   public static final int CONST_intValue71 = 6;
   public static final int CONST_intValue72 = 7;
   public static final int CONST_intValue73 = 8;
   public static final int CONST_intValue74 = 9;
   public static final int CONST_intValue75 = 10;
   public static final int CONST_intValue76 = 11;
   public static final int CONST_intValue77 = 12;
   public static final int CONST_intValue78 = 13;
   public static final int CONST_intValue79 = 14;
   public static final int CONST_intValue80 = 15;
   public static final int CONST_intValue81 = 16;
   public static final int CONST_intValue82 = 17;
   public static final int CONST_intValue83 = 18;
   public static final int CONST_intValue84 = 19;
   public static final int CONST_intValue85 = 20;
   public static final int CONST_intValue86 = 21;
   public static final int CONST_intValue87 = 22;
   public static final int CONST_intValue88 = 23;
   public static final int CONST_intValue89 = 24;
   public static final int CONST_intValue90 = 40;
   public static final int CONST_intValue91 = 41;
   public static final int CONST_intValue92 = 42;
   public static final int CONST_intValue93 = 43;
   public static final int CONST_intValue94 = 44;
   public static final int CONST_intValue95 = 45;
   public static final int CONST_intValue96 = 46;
   public static final int CONST_intValue97 = 47;
   public static final int CONST_intValue98 = 48;
   public static final int CONST_intValue99 = 49;
   public static final int CONST_intValue100 = 50;
   public static final int CONST_intValue101 = 51;
   public static final int CONST_intValue102 = 1;
   public static final int CONST_intValue103 = 2;
   public static final int CONST_intValue104 = 1;
   public static final int CONST_intValue105 = 2;
   public static final int CONST_intValue106 = 400;

   private CloudPackets() {
   }

   public static ByteBuffer method1783(long l, String str, String str2, String string, String str3, byte[] src, String str4) {
      boolean flag = str4 != null && !str4.isEmpty() && str4.length() <= 2048;
      FriendlyByteBuf friendlyByteBuf = method1820(1, 128 + (flag ? str4.length() + 3 : 0));
      friendlyByteBuf.writeVarInt(5);
      friendlyByteBuf.writeVarLong(l);
      friendlyByteBuf.writeUtf(str, 32);
      friendlyByteBuf.writeUtf(str2, 16);
      friendlyByteBuf.writeUtf(string, 36);
      friendlyByteBuf.writeUtf(str3, 32);
      friendlyByteBuf.writeBytes(src, 0, 32);
      if (flag) {
         friendlyByteBuf.writeUtf(str4, 2048);
      }

      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1784(Collection<String> all) {
      ArrayList<String> strings = new ArrayList(Math.min(all.size(), 200));

      for (String str : all) {
         if (strings.size() >= 200) {
            break;
         }

         if (str != null && !str.isEmpty() && str.length() <= 32) {
            strings.add(str);
         }
      }

      int size = strings.size();
      FriendlyByteBuf friendlyByteBuf = method1820(2, 16 + size * 20);
      friendlyByteBuf.writeVarInt(size);

      for (String str : strings) {
         friendlyByteBuf.writeUtf(str, 32);
      }

      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1785(int n, String username) {
      FriendlyByteBuf friendlyByteBuf = method1820(3, 32);
      friendlyByteBuf.writeByte(n);
      friendlyByteBuf.writeUtf(username, 32);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1786(String str) {
      FriendlyByteBuf friendlyByteBuf = method1820(4, 24);
      friendlyByteBuf.writeUtf(str, 32);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1787(String username) {
      FriendlyByteBuf friendlyByteBuf = method1820(5, 40);
      friendlyByteBuf.writeUtf(username, 32);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1788(String username) {
      FriendlyByteBuf friendlyByteBuf = method1820(6, 40);
      friendlyByteBuf.writeUtf(username, 32);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1789(String text, String str) {
      FriendlyByteBuf friendlyByteBuf = method1820(16, 128);
      friendlyByteBuf.writeUtf(method1819(text, 64), 64);
      friendlyByteBuf.writeUtf(method1819(str, 64), 64);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1790(double x, double y, double z, float yRot, float xRot) {
      FriendlyByteBuf friendlyByteBuf = method1820(17, 40);
      friendlyByteBuf.writeDouble(x);
      friendlyByteBuf.writeDouble(y);
      friendlyByteBuf.writeDouble(z);
      friendlyByteBuf.writeFloat(yRot);
      friendlyByteBuf.writeFloat(xRot);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1791(float health, float maxHealth, float absorptionAmount, int foodLevel) {
      FriendlyByteBuf friendlyByteBuf = method1820(18, 16);
      friendlyByteBuf.writeFloat(health);
      friendlyByteBuf.writeFloat(maxHealth);
      friendlyByteBuf.writeFloat(absorptionAmount);
      friendlyByteBuf.writeByte(foodLevel);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1792(List<CloudPackets.Data7> data7s) {
      FriendlyByteBuf friendlyByteBuf = method1820(19, 16 + data7s.size() * 24);
      friendlyByteBuf.writeVarInt(data7s.size());

      for (CloudPackets.Data7 data7 : data7s) {
         friendlyByteBuf.writeUtf(data7.getText().length() > 96 ? "" : data7.getText(), 96);
         friendlyByteBuf.writeByte(data7.count());
         friendlyByteBuf.writeVarInt(data7.getIntValue2());
      }

      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1793(String text, List<CloudPackets.Data9> data9s) {
      int n = Math.min(data9s.size(), 12);
      FriendlyByteBuf friendlyByteBuf = method1820(20, 16 + n * 48);
      friendlyByteBuf.writeUtf(method1819(text, 16), 16);
      friendlyByteBuf.writeVarInt(n);

      for (int i = 0; i < n; i++) {
         CloudPackets.Data9 data9 = (CloudPackets.Data9)data9s.get(i);
         friendlyByteBuf.writeUtf(data9.getText(), 40);
         friendlyByteBuf.writeVarInt(data9.getIntValue());
         friendlyByteBuf.writeInt(data9.getIntValue2());
      }

      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1794(int n, String str) {
      FriendlyByteBuf friendlyByteBuf = method1820(32, 16 + str.length() * 3);
      friendlyByteBuf.writeByte(n);
      friendlyByteBuf.writeUtf(str, 256);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1795(long deleteMenuMessage) {
      FriendlyByteBuf friendlyByteBuf = method1820(34, 10);
      friendlyByteBuf.writeVarLong(deleteMenuMessage);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1796(String text, String str, double x, double y, double z, String str2, int n, String str3) {
      FriendlyByteBuf friendlyByteBuf = method1820(33, 232);
      friendlyByteBuf.writeUtf(method1819(text, 64), 64);
      friendlyByteBuf.writeUtf(method1819(str, 64), 64);
      friendlyByteBuf.writeDouble(x);
      friendlyByteBuf.writeDouble(y);
      friendlyByteBuf.writeDouble(z);
      friendlyByteBuf.writeUtf(str2, 32);
      friendlyByteBuf.writeInt(n);
      friendlyByteBuf.writeUtf(str3, 36);
      return method1821(friendlyByteBuf);
   }

   public static ByteBuffer method1797(int n, int n2, int n3, byte[] src, int length) {
      ByteBuffer allocate = ByteBuffer.allocate(5 + length);
      allocate.put((byte)35);
      allocate.put((byte)n);
      allocate.put((byte)Math.max(0, Math.min(255, n2)));
      allocate.putShort((short)n3);
      allocate.put(src, 0, length);
      return allocate.flip();
   }

   public static ByteBuffer method1798(int n) {
      return ByteBuffer.wrap(new byte[]{36, (byte)n});
   }

   public static ByteBuffer method1799(int n, String string) {
      FriendlyByteBuf friendlyByteBuf = method1820(37, 16 + string.length() * 3);
      friendlyByteBuf.writeVarInt(n);
      friendlyByteBuf.writeUtf(string, 8192);
      return method1821(friendlyByteBuf);
   }

   public static CloudPackets.Data4 method1800(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data4(friendlyByteBuf.readVarInt(), friendlyByteBuf.readUtf(64), friendlyByteBuf.readBoolean());
   }

   public static CloudPackets.Data12 method1801(FriendlyByteBuf friendlyByteBuf) {
      long varLong = friendlyByteBuf.readVarLong();
      String utf = friendlyByteBuf.readUtf(32);
      long l = friendlyByteBuf.readLong();
      int varInt = friendlyByteBuf.readVarInt();
      int n = friendlyByteBuf.isReadable() ? friendlyByteBuf.readVarInt() : 0;
      return new CloudPackets.Data12(varLong, utf, l, varInt, n);
   }

   public static CloudPackets.Data16 method1802(FriendlyByteBuf friendlyByteBuf) {
      long varLong = friendlyByteBuf.readVarLong();
      short unsignedByte = friendlyByteBuf.readUnsignedByte();
      short s = friendlyByteBuf.readUnsignedByte();
      int unsignedShort = friendlyByteBuf.readUnsignedShort();
      int readableBytes = friendlyByteBuf.readableBytes();
      if (readableBytes > 400) {
         throw new IllegalStateException("голос: " + readableBytes + " байт");
      } else {
         byte[] dst = new byte[readableBytes];
         friendlyByteBuf.readBytes(dst);
         return new CloudPackets.Data16(varLong, unsignedByte, s, unsignedShort, dst);
      }
   }

   public static CloudPackets.Data6 method1803(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data6(friendlyByteBuf.readVarLong(), friendlyByteBuf.readUnsignedByte());
   }

   public static CloudPackets.Data18 method1804(FriendlyByteBuf friendlyByteBuf) {
      int varInt = friendlyByteBuf.readVarInt();
      long varLong = friendlyByteBuf.readVarLong();
      String utf = friendlyByteBuf.readUtf(32);
      String str = friendlyByteBuf.readUtf(1048576);
      JsonElement string = JsonParser.parseString(str.isEmpty() ? "{}" : str);
      return new CloudPackets.Data18(varInt, varLong, utf, string.isJsonObject() ? string.getAsJsonObject() : new JsonObject());
   }

   public static CloudPackets.Data method1805(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data(friendlyByteBuf.readUnsignedByte(), friendlyByteBuf.readUtf(256));
   }

   public static CloudPackets.Data8 method1806(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data8(friendlyByteBuf.readVarLong(), friendlyByteBuf.readUtf(32), friendlyByteBuf.readUtf(16));
   }

   public static long method1807(FriendlyByteBuf friendlyByteBuf) {
      return friendlyByteBuf.readVarLong();
   }

   public static List<CloudPackets.Data14> method1808(FriendlyByteBuf friendlyByteBuf) {
      int varInt = friendlyByteBuf.readVarInt();
      if (varInt == 0) {
         return List.of();
      } else if (varInt >= 0 && varInt <= 200) {
         List<Data14> data14s = new ArrayList<>(varInt);

         for (int i = 0; i < varInt; i++) {
            data14s.add(new CloudPackets.Data14(friendlyByteBuf.readUtf(32), friendlyByteBuf.readUnsignedByte() == 1));
         }

         return data14s;
      } else {
         return null;
      }
   }

   public static CloudPackets.Data17 method1809(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data17(friendlyByteBuf.readUtf(32), friendlyByteBuf.readUnsignedByte());
   }

   public static CloudPackets.Data5 method1810(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data5(friendlyByteBuf.readVarLong(), friendlyByteBuf.readUtf(64), friendlyByteBuf.readUtf(64));
   }

   public static CloudPackets.Data2 method1811(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data2(friendlyByteBuf.readVarLong(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
   }

   public static CloudPackets.Data3 method1812(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data3(friendlyByteBuf.readVarLong(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readUnsignedByte());
   }

   public static CloudPackets.Data11 method1813(FriendlyByteBuf friendlyByteBuf) {
      long varLong = friendlyByteBuf.readVarLong();
      int varInt = friendlyByteBuf.readVarInt();
      if (varInt >= 0 && varInt <= 48) {
         CloudPackets.Data7[] data7s = new CloudPackets.Data7[varInt];

         for (int i = 0; i < varInt; i++) {
            String utf = friendlyByteBuf.readUtf(96);
            short unsignedByte = friendlyByteBuf.readUnsignedByte();
            int n = friendlyByteBuf.readVarInt();
            data7s[i] = utf.isEmpty() ? CloudPackets.Data7.INSTANCE : new CloudPackets.Data7(utf, unsignedByte, n);
         }

         return new CloudPackets.Data11(varLong, data7s);
      } else {
         throw new IllegalStateException("инвентарь: " + varInt + " слотов");
      }
   }

   public static CloudPackets.Data15 method1814(FriendlyByteBuf friendlyByteBuf) {
      long varLong = friendlyByteBuf.readVarLong();
      String utf = friendlyByteBuf.readUtf(16);
      int varInt = friendlyByteBuf.readVarInt();
      if (varInt >= 0 && varInt <= 12) {
         CloudPackets.Data9[] elements = new CloudPackets.Data9[varInt];

         for (int i = 0; i < varInt; i++) {
            elements[i] = new CloudPackets.Data9(friendlyByteBuf.readUtf(40), friendlyByteBuf.readVarInt(), friendlyByteBuf.readInt());
         }

         return new CloudPackets.Data15(varLong, utf, List.of(elements));
      } else {
         throw new IllegalStateException("косметика: " + varInt + " слотов");
      }
   }

   public static CloudPackets.Data13 method1815(FriendlyByteBuf friendlyByteBuf) {
      long varLong = friendlyByteBuf.readVarLong();
      String utf = friendlyByteBuf.readUtf(32);
      short unsignedByte = friendlyByteBuf.readUnsignedByte();
      long time = friendlyByteBuf.readLong();
      String str = friendlyByteBuf.readUtf(256);
      long l = friendlyByteBuf.isReadable() ? friendlyByteBuf.readVarLong() : 0L;
      return new CloudPackets.Data13(varLong, utf, unsignedByte, time, str, l);
   }

   public static long method1816(FriendlyByteBuf friendlyByteBuf) {
      return friendlyByteBuf.readVarLong();
   }

   public static CloudPackets.Data10 method1817(FriendlyByteBuf friendlyByteBuf) {
      return new CloudPackets.Data10(
         friendlyByteBuf.readVarLong(),
         friendlyByteBuf.readUtf(32),
         friendlyByteBuf.readUtf(64),
         friendlyByteBuf.readUtf(64),
         friendlyByteBuf.readDouble(),
         friendlyByteBuf.readDouble(),
         friendlyByteBuf.readDouble(),
         friendlyByteBuf.readUtf(32),
         friendlyByteBuf.readInt(),
         friendlyByteBuf.readUtf(36)
      );
   }

   public static FriendlyByteBuf method1818(ByteBuffer buffer) {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(buffer));
   }

   private static String method1819(String text, int endIndex) {
      if (text == null) {
         return "";
      } else {
         return text.length() > endIndex ? text.substring(0, endIndex) : text;
      }
   }

   private static FriendlyByteBuf method1820(int n, int initialCapacity) {
      FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer(initialCapacity));
      friendlyByteBuf.writeByte(n);
      return friendlyByteBuf;
   }

   private static ByteBuffer method1821(FriendlyByteBuf friendlyByteBuf) {
      return friendlyByteBuf.nioBuffer();
   }

   public static final class Data {
      private final int intValue;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + "]";
      }

      public Data(int intValue, String text) {
         this.intValue = intValue;
         this.text = text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }
   }

   public static final class Data10 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final String text4;
      private final int intValue;
      private final String text5;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public String text4() {
         return this.text4;
      }

      public int intValue() {
         return this.intValue;
      }

      public String text5() {
         return this.text5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text5, other.text5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3, doubleValue, doubleValue2, doubleValue3, text4, intValue, text5);
      }

      @Override
      public String toString() {
         return "Data10[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "text4=" + this.text4 + ", " + "intValue=" + this.intValue + ", " + "text5=" + this.text5 + "]";
      }

      public Data10(long longValue, String text, String utf, String str, double doubleValue, double d, double d2, String utf2, int intValue, String utf3) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = utf;
         this.text3 = str;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.text4 = utf2;
         this.intValue = intValue;
         this.text5 = utf3;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public String server() {
         return this.text2;
      }

      public String dimension() {
         return this.text3;
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

      public String label() {
         return this.text4;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText5() {
         return this.text5;
      }
   }

   public static final class Data11 {
      private final long longValue;
      private final CloudPackets.Data7[] data7s;

      public long longValue() {
         return this.longValue;
      }

      public CloudPackets.Data7[] data7s() {
         return this.data7s;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.data7s, other.data7s);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, data7s);
      }

      @Override
      public String toString() {
         return "Data11[" + "longValue=" + this.longValue + ", " + "data7s=" + this.data7s + "]";
      }

      public Data11(long longValue, CloudPackets.Data7[] data7s) {
         this.longValue = longValue;
         this.data7s = data7s;
      }

      public long uid() {
         return this.longValue;
      }

      public CloudPackets.Data7[] getData7s() {
         return this.data7s;
      }
   }

   public static final class Data12 {
      private final long longValue;
      private final String text;
      private final long longValue2;
      private final int intValue;
      private final int intValue2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, longValue2, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data12[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "longValue2=" + this.longValue2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data12(long longValue, String text, long l, int intValue, int n) {
         this.longValue = longValue;
         this.text = text;
         this.longValue2 = l;
         this.intValue = intValue;
         this.intValue2 = n;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data13 {
      private final long longValue;
      private final String text;
      private final int intValue;
      private final long longValue2;
      private final String text2;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, intValue, longValue2, text2, longValue3);
      }

      @Override
      public String toString() {
         return "Data13[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "longValue2=" + this.longValue2 + ", " + "text2=" + this.text2 + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data13(long longValue, String text, int intValue, long time, String str, long l) {
         this.longValue = longValue;
         this.text = text;
         this.intValue = intValue;
         this.longValue2 = time;
         this.text2 = str;
         this.longValue3 = l;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long time() {
         return this.longValue2;
      }

      public String text() {
         return this.text2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }
   }

   public static final class Data14 {
      private final String text;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, flag);
      }

      @Override
      public String toString() {
         return "Data14[" + "text=" + this.text + ", " + "flag=" + this.flag + "]";
      }

      public Data14(String text, boolean flag) {
         this.text = text;
         this.flag = flag;
      }

      public String username() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data15 {
      private final long longValue;
      private final String text;
      private final List<CloudPackets.Data9> items;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public List<CloudPackets.Data9> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data15 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, items);
      }

      @Override
      public String toString() {
         return "Data15[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data15(long longValue, String text, List<CloudPackets.Data9> items) {
         this.longValue = longValue;
         this.text = text;
         this.items = items;
      }

      public long uid() {
         return this.longValue;
      }

      public String getText() {
         return this.text;
      }

      public List<CloudPackets.Data9> getItems() {
         return this.items;
      }
   }

   public static final class Data16 {
      private final long longValue;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final byte[] byteValues;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public byte[] byteValues() {
         return this.byteValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data16 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.byteValues, other.byteValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, intValue, intValue2, intValue3, byteValues);
      }

      @Override
      public String toString() {
         return "Data16[" + "longValue=" + this.longValue + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "byteValues=" + this.byteValues + "]";
      }

      public Data16(long longValue, int intValue, int n, int unsignedShort, byte[] byteValues) {
         this.longValue = longValue;
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = unsignedShort;
         this.byteValues = byteValues;
      }

      public boolean method1823() {
         return (this.intValue & 1) != 0;
      }

      public boolean method1824() {
         return (this.intValue & 2) != 0;
      }

      public long uid() {
         return this.longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }
   }

   public static final class Data17 {
      private final String text;
      private final int intValue;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data17 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue);
      }

      @Override
      public String toString() {
         return "Data17[" + "text=" + this.text + ", " + "intValue=" + this.intValue + "]";
      }

      public Data17(String text, int intValue) {
         this.text = text;
         this.intValue = intValue;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data18 {
      private final int intValue;
      private final long longValue;
      private final String text;
      private final JsonObject jsonObject;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public JsonObject jsonObject() {
         return this.jsonObject;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data18 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.jsonObject, other.jsonObject);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue, text, jsonObject);
      }

      @Override
      public String toString() {
         return "Data18[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "jsonObject=" + this.jsonObject + "]";
      }

      public Data18(int intValue, long longValue, String text, JsonObject jsonObject) {
         this.intValue = intValue;
         this.longValue = longValue;
         this.text = text;
         this.jsonObject = jsonObject;
      }

      public int kind() {
         return this.intValue;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public JsonObject getJsonObject() {
         return this.jsonObject;
      }
   }

   public static final class Data2 {
      private final long longValue;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final float floatValue2;

      public long longValue() {
         return this.longValue;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, doubleValue, doubleValue2, doubleValue3, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      public Data2(long longValue, double doubleValue, double d, double d2, float floatValue, float f) {
         this.longValue = longValue;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }

      public long uid() {
         return this.longValue;
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
   }

   public static final class Data3 {
      private final long longValue;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final int intValue;

      public long longValue() {
         return this.longValue;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, floatValue, floatValue2, floatValue3, intValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "intValue=" + this.intValue + "]";
      }

      public Data3(long longValue, float floatValue, float f, float f2, int intValue) {
         this.longValue = longValue;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.intValue = intValue;
      }

      public long uid() {
         return this.longValue;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data4 {
      private final int intValue;
      private final String text;
      private final boolean flag;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, flag);
      }

      @Override
      public String toString() {
         return "Data4[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "flag=" + this.flag + "]";
      }

      public Data4(int intValue, String text, boolean flag) {
         this.intValue = intValue;
         this.text = text;
         this.flag = flag;
      }

      public int protocol() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data5 {
      private final long longValue;
      private final String text;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2);
      }

      @Override
      public String toString() {
         return "Data5[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data5(long longValue, String text, String utf) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = utf;
      }

      public long uid() {
         return this.longValue;
      }

      public String server() {
         return this.text;
      }

      public String dimension() {
         return this.text2;
      }
   }

   public static final class Data6 {
      private final long longValue;
      private final int intValue;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, intValue);
      }

      @Override
      public String toString() {
         return "Data6[" + "longValue=" + this.longValue + ", " + "intValue=" + this.intValue + "]";
      }

      public Data6(long longValue, int intValue) {
         this.longValue = longValue;
         this.intValue = intValue;
      }

      public long uid() {
         return this.longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data7 {
      private final String text;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data7[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }
      public static final CloudPackets.Data7 INSTANCE = new CloudPackets.Data7("", 0, 0);

      public Data7(String text, int intValue, int n) {
         this.text = text;
         this.intValue = intValue;
         this.intValue2 = n;
      }

      public boolean isEmpty() {
         return this.text.isEmpty() || this.intValue <= 0;
      }

      public String getText() {
         return this.text;
      }

      public int count() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data8 {
      private final long longValue;
      private final String text;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2);
      }

      @Override
      public String toString() {
         return "Data8[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data8(long longValue, String text, String utf) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = utf;
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
   }

   public static final class Data9 {
      private final String text;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data9[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data9(String text, int intValue, int floatToIntBits) {
         this.text = text;
         this.intValue = intValue;
         this.intValue2 = floatToIntBits;
      }

      public CosmeticType method1822() {
         CosmeticType[] values = CosmeticType.values();
         return this.intValue >= 0 && this.intValue < values.length ? values[this.intValue] : CosmeticType.UNKNOWN;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}
