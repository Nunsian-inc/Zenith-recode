package org.zenith.utility.misc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.zenith.Zenith;
import org.zenith.utility.network.CloudPackets;

public final class CloudConnectionState {
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 100;
   private static final int CONST_intValue3 = 4096;
   private volatile CloudConnectionState.Type type = CloudConnectionState.Type.OFFLINE;
   private volatile String lastError = "";
   private volatile String text = "";
   private volatile int intValue;
   private final Map<Long, PartyPlayerInfo> items = new ConcurrentHashMap<>();
   private final Map<Long, PartyPlayerInfo> items2 = new ConcurrentHashMap<>();
   private final ArrayDeque<CloudPackets.Data13> arrayDeque = new ArrayDeque<>();
   private final Map<String, CloudPackets.Data8> items3 = new LinkedHashMap<>();
   private final Set<String> items4 = new HashSet<>();
   private final Map<String, List<CloudPackets.Data9>> items5 = new ConcurrentHashMap<>();
   private final Map<Long, String> items6 = new ConcurrentHashMap<>();
   private final Map<String, Long> items7 = new ConcurrentHashMap<>();
   private final Map<String, Long> items8 = new ConcurrentHashMap<>();

   public CloudConnectionState.Type getType() {
      return this.type;
   }

   public void setType(CloudConnectionState.Type type) {
      this.type = type;
   }

   public boolean method8035() {
      return this.type == CloudConnectionState.Type.ONLINE;
   }

   public String getLastError() {
      return this.lastError;
   }

   public void method8036(String text) {
      this.lastError = text == null ? "" : text;
   }

   public String note() {
      return this.text;
   }

   public void method8037(String text) {
      this.text = text == null ? "" : text;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   public Collection<PartyPlayerInfo> method8038() {
      return this.items.values();
   }

   public PartyPlayerInfo method8039(long uid) {
      return this.items.get(uid);
   }

   public PartyPlayerInfo method8040(long uid) {
      PartyPlayerInfo partyPlayerInfo = this.items.get(uid);
      return partyPlayerInfo != null ? partyPlayerInfo : this.items2.get(uid);
   }

   public Collection<PartyPlayerInfo> method8041() {
      return this.items2.values();
   }

   public PartyPlayerInfo method8042(long uid, String username, String anotherString) {
      PartyPlayerInfo partyPlayerInfo = this.items2.get(uid);
      if (partyPlayerInfo != null && partyPlayerInfo.getText2().equalsIgnoreCase(anotherString) && partyPlayerInfo.username().equalsIgnoreCase(username)) {
         return partyPlayerInfo;
      } else {
         PartyPlayerInfo partyPlayerInfo2 = new PartyPlayerInfo(uid, username, anotherString);
         this.items2.put(uid, partyPlayerInfo2);
         return partyPlayerInfo2;
      }
   }

   public void method8043(Set<Long> longs) {
      this.items2.keySet().retainAll(longs);
   }

   public PartyPlayerInfo method8044(String selected) {
      if (selected != null && !this.items.isEmpty()) {
         for (PartyPlayerInfo partyPlayerInfo : this.items.values()) {
            if (partyPlayerInfo.username().equalsIgnoreCase(selected)) {
               return partyPlayerInfo;
            }
         }

         for (PartyPlayerInfo partyPlayerInfo : this.items.values()) {
            if (partyPlayerInfo.getText2().equalsIgnoreCase(selected)) {
               return partyPlayerInfo;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public int method8045() {
      return this.items.size();
   }

   public PartyPlayerInfo method8046(CloudPackets.Data8 data8) {
      this.items8.put(data8.username().toLowerCase(Locale.ROOT), data8.uid());
      PartyPlayerInfo partyPlayerInfo = this.items.get(data8.uid());
      if (partyPlayerInfo != null && partyPlayerInfo.getText2().equalsIgnoreCase(data8.getText2()) && partyPlayerInfo.username().equalsIgnoreCase(data8.username())) {
         return partyPlayerInfo;
      } else {
         PartyPlayerInfo partyPlayerInfo2 = new PartyPlayerInfo(data8.uid(), data8.username(), data8.getText2());
         this.items.put(data8.uid(), partyPlayerInfo2);
         return partyPlayerInfo2;
      }
   }

   public long uidOf(String displayName) {
      if (displayName == null) {
         return 0L;
      } else {
         Long l = this.items8.get(displayName.toLowerCase(Locale.ROOT));
         return l == null ? 0L : l;
      }
   }

   public PartyPlayerInfo method8047(long l) {
      return this.items.remove(l);
   }

   public void method8048() {
      this.items.clear();
   }

   public List<CloudPackets.Data13> method8049() {
      return new ArrayList<>(this.arrayDeque);
   }

   public ArrayDeque<CloudPackets.Data13> getArrayDeque() {
      return this.arrayDeque;
   }

   public void method8050(CloudPackets.Data13 data13) {
      if (this.arrayDeque.size() >= 200) {
         this.arrayDeque.pollFirst();
      }

      this.arrayDeque.addLast(data13);
   }

   public void method8051(long l) {
      this.arrayDeque.removeIf(data13 -> data13.getLongValue3() == l);
   }

   public void method8052(List<CloudPackets.Data13> data13s) {
      if (!data13s.isEmpty()) {
         ArrayList<CloudPackets.Data13> data13s2 = new ArrayList(data13s.size() + this.arrayDeque.size());
         HashSet hashSet = new HashSet();
         HashSet hashSet2 = new HashSet();

         for (CloudPackets.Data13 data13 : data13s) {
            method8053(data13s2, hashSet, hashSet2, data13);
         }

         for (CloudPackets.Data13 data13 : this.arrayDeque) {
            method8053(data13s2, hashSet, hashSet2, data13);
         }

         data13s2.sort(Comparator.comparingLong(CloudPackets.Data13::time).thenComparingLong(CloudPackets.Data13::getLongValue3));
         this.arrayDeque.clear();

         for (CloudPackets.Data13 sub : data13s2.subList(Math.max(0, data13s2.size() - 200), data13s2.size())) {
            this.arrayDeque.addLast(sub);
         }
      }
   }

   private static void method8053(List<CloudPackets.Data13> data13s, Set<Long> longs, Set<String> strings, CloudPackets.Data13 data13) {
      if (data13.getLongValue3() <= 0L || longs.add(data13.getLongValue3())) {
         if (strings.add(method8054(data13))) {
            data13s.add(data13);
         }
      }
   }

   private static String method8054(CloudPackets.Data13 data13) {
      return data13.uid() + "@" + data13.time() + "@" + data13.text();
   }

   public Collection<CloudPackets.Data8> method8055() {
      return this.items3.values();
   }

   public boolean method8056() {
      return !this.items3.isEmpty();
   }

   public boolean method8057(CloudPackets.Data8 data8) {
      String lowerCase = data8.username().toLowerCase(Locale.ROOT);
      return !this.items3.containsKey(lowerCase) && this.items3.size() >= 100 ? false : this.items3.put(lowerCase, data8) == null;
   }

   public List<CloudPackets.Data9> method8058(String str) {
      return str != null && !this.items5.isEmpty() ? this.items5.getOrDefault(str.toLowerCase(Locale.ROOT), List.of()) : List.of();
   }

   public void method8059(long uid, String text, List<CloudPackets.Data9> items) {
      if (text != null && !text.isBlank()) {
         String lowerCase = text.toLowerCase(Locale.ROOT);
         String key = this.items6.put(uid, lowerCase);
         if (key != null && !key.equals(lowerCase) && this.items7.remove(key, uid)) {
            this.items5.remove(key);
         }

         if (this.items5.containsKey(lowerCase) || this.items5.size() < 4096) {
            this.items7.put(lowerCase, uid);
            this.items5.put(lowerCase, items);
         }
      }
   }

   public void method8060(String username) {
      this.items3.remove(username.toLowerCase(Locale.ROOT));
   }

   public boolean method8061(String selected) {
      return selected != null && this.items4.contains(selected.toLowerCase(Locale.ROOT));
   }

   public int method8062() {
      return this.items4.size();
   }

   public void method8063(List<CloudPackets.Data14> data14s) {
      this.items4.clear();

      for (CloudPackets.Data14 data14 : data14s) {
         if (!data14.isFlag()) {
            this.items4.add(data14.username().toLowerCase(Locale.ROOT));
         }
      }
   }

   public void method8064() {
      this.items.clear();
      this.items2.clear();
      this.items5.clear();
      this.items6.clear();
      this.items7.clear();
      this.intValue = 0;
   }

   public static enum Type {
      OFFLINE("gui.cloud.state.offline"),
      CONNECTING("gui.cloud.state.connecting"),
      ONLINE("gui.cloud.state.online"),
      FAILED("gui.cloud.state.failed");

      private final String text;

      private Type(String text) {
         this.text = text;
      }

      public String label() {
         return Zenith.getInstance().getLocalization().translate(this.text);
      }
   }
}
