package org.zenith.utility.misc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.Generated;

public class Account {
   private Account.Type type = Account.Type.OFFLINE;
   private String name = "";
   private String text = "";
   private String text2 = "";
   private String text3 = "";
   private long longValue = System.currentTimeMillis();

   public static Account method5470(String name) {
      Account account = new Account();
      account.type = Account.Type.OFFLINE;
      account.name = name;
      account.text = offlineUuid(name).toString();
      return account;
   }

   public static Account method5471(String name, UUID uuid, String str, String str2) {
      Account account = new Account();
      account.type = Account.Type.MICROSOFT;
      account.name = name;
      account.text = uuid.toString();
      account.text2 = str;
      account.text3 = str2;
      return account;
   }

   public static UUID offlineUuid(String name) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
   }

   public UUID method5472() {
      try {
         return UUID.fromString(this.text);
      } catch (Exception e) {
         return offlineUuid(this.name);
      }
   }

   public boolean method5473() {
      return this.type == Account.Type.MICROSOFT;
   }

   @Generated
   public Account.Type getType() {
      return this.type;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public String getText3() {
      return this.text3;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public void setType(Account.Type type) {
      this.type = type;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   public void setText(String text) {
      this.text = text;
   }

   @Generated
   public void setText2(String str) {
      this.text2 = str;
   }

   @Generated
   public void setText3(String str) {
      this.text3 = str;
   }

   @Generated
   public void setLongValue(long longValue) {
      this.longValue = longValue;
   }

   public static enum Type {
      OFFLINE,
      MICROSOFT;
   }
}
