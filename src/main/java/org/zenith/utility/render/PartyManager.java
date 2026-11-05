package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.Event10;
import org.zenith.client.events.Event11;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.render.PartyMarksModule;
import org.zenith.client.screens.nlgui.chat.ChatCompose;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.CloudChatClient;
import org.zenith.utility.api.CloudDmClient;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class PartyManager implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final long CONST_longValue = 1500L;
   private static final long CONST_longValue2 = 15000L;
   private static final long CONST_longValue3 = 6000L;
   private static final long CONST_longValue4 = 60000L;
   private static final long CONST_longValue5 = 4000L;
   private static final long CONST_longValue6 = 90000L;
   private static final long CONST_longValue7 = 10000L;
   private final Cloud cloud;
   private final Share share;
   private long longValue = System.currentTimeMillis();
   private double lastX;
   private double lastY;
   private double doubleValue;
   private float floatValue;
   private float floatValue2;
   private long longValue2;
   private String text = "";
   private long longValue3;
   private long longValue4 = System.currentTimeMillis();
   private String lastStatus = "";
   private int intValue = -1;
   private final Map<Integer, Long> items = new HashMap<>();
   private final Map<Long, Long> items2 = new HashMap<>();
   private List<PartyMember.Data11> items3 = List.of();
   private long longValue5;
   private boolean flag;
   private long longValue6;
   private int intValue2;
   private int intValue3;
   private long longValue7 = System.currentTimeMillis();
   private int intValue4 = -1;
   private int intValue5 = -1;
   private String text2 = "";
   private String text3 = "";
   private long longValue8;
   private PartyMember.Data6 data6;
   private PartyManager.Data data;
   private String text4 = "";
   private boolean flag2;
   private int intValue6;

   public PartyManager(Cloud cloud, Share share) {
      this.cloud = cloud;
      this.share = share;
      EventManager.register(this);
   }

   public static PartyManager method17() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getPartyManager();
   }

   private PartyMember method18() {
      return this.cloud.getPartyMember();
   }

   @EventTarget
   private void method19(CloudStateEvent event) {
      if (event.method6858()) {
         this.text = "";
         this.intValue4 = -1;
         this.intValue5 = -1;
         this.data6 = null;
      }
   }

   @EventTarget
   private void setLongValue(KeyEvent longValue) {
      this.longValue = System.currentTimeMillis();
   }

   @EventTarget
   private void method20(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.PRE) {
         Entity entity = event.getEntity();
         if (entity instanceof Player && entity != mc.player) {
            long now = System.currentTimeMillis();
            this.longValue2 = now;
            this.longValue = now;
            this.items.put(entity.getId(), now);
         }
      }
   }

   @EventTarget
   private void method21(EntityDeathEvent event) {
      if (mc.isSameThread()) {
         if (event.getLivingEntity() == mc.player) {
            this.method29();
         }
      }
   }

   @EventTarget
   private void method22(EntityStatusEvent event) {
      if (event.getEntityStatusS2CPacket().getEventId() == 3 && mc.level != null && !this.items.isEmpty()) {
         if (event.getEntityStatusS2CPacket().getEntity(mc.level) instanceof Player player && player != mc.player) {
            Long l = this.items.remove(player.getId());
            if (l != null && System.currentTimeMillis() - l < 6000L) {
               this.intValue2++;
            }
         }
      }
   }

   private void method23() {
      CloudChatClient.getInstance().method2165(60000L);
      if (this.share.notifyMessages.isEnabled()) {
         CloudDmClient.getInstance().method4860();
      } else {
         CloudDmClient.getInstance().method4861(60000L);
      }
   }

   private void method24() {
      if (++this.intValue6 >= 40) {
         this.intValue6 = 0;
         this.method23();
         JsonObject json = new JsonObject();

         for (Setting setting : this.share.method946()) {
            setting.safe(json);
         }

         String string = json.toString();
         if (!this.text4.isEmpty() && !string.equals(this.text4)) {
            this.share.save();
         }

         this.text4 = string;
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      long now = System.currentTimeMillis();
      this.method24();
      this.method25(now);
      this.method26(now);
      this.method27();
      this.method28();
      this.method30(now);
      this.items.values().removeIf(var2x -> now - var2x > 6000L);
      if (this.cloud.method7862()) {
         PartyMember partyMember = this.method18();
         if (partyMember.version() != this.intValue) {
            this.intValue = partyMember.version();
            this.method37(partyMember);
         }

         this.method34(now);
         this.method35();
         this.method36(now);
         this.method46(now);
      }
   }

   private void method25(long longValue) {
      if (mc.player != null) {
         double d = mc.player.getX() - this.lastX;
         double d2 = mc.player.getZ() - this.doubleValue;
         double d3 = mc.player.getY() - this.lastY;
         if (d * d + d3 * d3 + d2 * d2 > 0.01
            || Math.abs(mc.player.getYRot() - this.floatValue) > 1.0F
            || Math.abs(mc.player.getXRot() - this.floatValue2) > 1.0F) {
            this.longValue = longValue;
         }

         this.lastX = mc.player.getX();
         this.lastY = mc.player.getY();
         this.doubleValue = mc.player.getZ();
         this.floatValue = mc.player.getYRot();
         this.floatValue2 = mc.player.getXRot();
      }
   }

   private void method26(long now) {
      if (mc.player != null && mc.player.hurtTime > 0) {
         DamageSource lastDamageSource = mc.player.getLastDamageSource();
         if (lastDamageSource != null && lastDamageSource.getEntity() instanceof Player player && player != mc.player) {
            this.longValue2 = now;
         }
      }
   }

   private void method27() {
      if (mc.player != null && !mc.player.isDeadOrDying() && !(mc.player.getHealth() <= 0.0F)) {
         Inventory inventory = mc.player.getInventory();
         int n = Math.min(inventory.getContainerSize(), 64);
         long l = 1469598103934665603L;

         for (int i = 0; i < n; i++) {
            ItemStack stack = inventory.getItem(i);
            l = (l ^ (stack.isEmpty() ? 0 : System.identityHashCode(stack.getItem()))) * 1099511628211L;
            l = (l ^ stack.getCount()) * 1099511628211L;
            l = (l ^ stack.getDamageValue()) * 1099511628211L;
         }

         if (l != this.longValue5) {
            this.longValue5 = l;
            ArrayList coll = new ArrayList();

            for (int i = 0; i < n; i++) {
               ItemStack stack = inventory.getItem(i);
               if (!stack.isEmpty()) {
                  coll.add(
                     new PartyMember.Data11(
                        BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), Math.min(stack.getCount(), 127), Math.max(0, stack.getDamageValue()), i, stack.hasFoil()
                     )
                  );
               }
            }

            this.items3 = List.copyOf(coll);
         }
      }
   }

   private void method28() {
      if (mc.player == null) {
         this.flag = false;
      } else {
         boolean flag = mc.player.isDeadOrDying() || mc.player.getHealth() <= 0.0F || mc.gui.screen() instanceof DeathScreen;
         if (flag && !this.flag) {
            this.method29();
         }

         this.flag = flag;
      }
   }

   private void method29() {
      long now = System.currentTimeMillis();
      if (mc.player != null && now - this.longValue6 >= 3000L) {
         this.longValue6 = now;
         this.intValue3++;
         this.data = new PartyManager.Data(
            this.cloud.method7911(),
            this.cloud.method7913(),
            mc.player.getX(),
            mc.player.getY(),
            mc.player.getZ(),
            this.items3,
            mc.player.experienceLevel,
            now
         );
      }
   }

   private void method30(long now) {
      PartyManager.Data data = this.data;
      if (data != null) {
         String string = "";
         if (mc.gui.screen() instanceof DeathScreen deathScreen && deathScreen.causeOfDeath != null) {
            string = deathScreen.causeOfDeath.getString();
         } else if (now - data.time() < 1500L) {
            return;
         }

         this.data = null;
         if (!this.flag2) {
            this.flag2 = true;
            PartyMarksModule.INSTANCE.setToggled(true);
         }

         if (this.share.deaths.isEnabled() && this.cloud.method7860()) {
            this.method18()
               .method1162(
                  data.server(),
                  data.dimension(),
                  data.getDoubleValue(),
                  data.getDoubleValue2(),
                  data.getDoubleValue3(),
                  string,
                  data.getItems(),
                  data.getIntValue()
               );
         } else {
            this.method18()
               .method1163(
                  data.server(),
                  data.dimension(),
                  data.getDoubleValue(),
                  data.getDoubleValue2(),
                  data.getDoubleValue3(),
                  string,
                  data.getItems(),
                  data.getIntValue()
               );
         }
      }
   }

   public String method31() {
      long now = System.currentTimeMillis();
      String anObject = this.share.method947();
      if ("dnd".equals(anObject)) {
         return "dnd";
      } else {
         float f = this.share.autoAfk.getValue3();
         if (f > 0.0F && (float)(now - this.longValue) > f * 60000.0F) {
            return "afk";
         } else if (this.share.autoPvp.isEnabled() && now - this.longValue2 < 15000L) {
            return "pvp";
         } else if (!"auto".equals(anObject)) {
            return anObject;
         } else {
            return mc.level == null ? "menu" : "online";
         }
      }
   }

   public boolean method32() {
      return "dnd".equals(this.method31());
   }

   public String method33() {
      if (this.share.allowJoin.isEnabled() && this.share.server.isEnabled()) {
         ServerData currentServer = mc.getCurrentServer();
         return currentServer != null && !currentServer.isRealm() && currentServer.ip != null && !mc.isLocalServer() ? currentServer.ip.trim() : "";
      } else {
         return "";
      }
   }

   private void method34(long now) {
      String lastStatus = this.method31();
      if (!lastStatus.equals(this.lastStatus)) {
         this.lastStatus = lastStatus;
         this.longValue4 = now;
      }

      String str = this.share.server.isEnabled() ? this.cloud.method7911() : "";
      String mode = this.share.server.isEnabled() && mc.level != null ? ChatCompose.mode() : "";
      String str2 = this.share.text.getValue() == null ? "" : this.share.text.getValue().strip();
      String str3 = this.method33();
      String text = lastStatus + "|" + str2 + "|" + str3 + "|" + mode + "|" + str;
      if (!text.equals(this.text) && now - this.longValue3 >= 1500L && this.cloud.method7860()) {
         this.text = text;
         this.longValue3 = now;
         this.method18().method1165(new PartyMember.Data2(lastStatus, str2, str3, mode, str, this.longValue4));
      }
   }

   private void method35() {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && this.cloud.method7860()) {
         int index = this.share.cosmeticsVisibility.getIndex();
         if (wardrobe.version() != this.intValue4 || index != this.intValue5) {
            this.intValue4 = wardrobe.version();
            this.intValue5 = index;
            ArrayList arrayList = new ArrayList();
            if (!this.share.method949()) {
               var map = wardrobe.method2504();
               arrayList.addAll(map.values());
            }

            this.method18().method1166(arrayList);
            logger.info(
               "Гардероб: образ отправлен узлу — {} вещей, ник {}{}",
               new Object[]{arrayList.size(), this.cloud.method7871(), this.share.method949() ? " (скрыт настройкой видимости косметики)" : ""}
            );
         }
      }
   }

   private void method36(long now) {
      if (now - this.longValue7 >= 60000L && this.cloud.method7860()) {
         this.longValue7 = now;
         if (!this.share.stats.isEnabled()) {
            this.intValue2 = 0;
            this.intValue3 = 0;
         } else {
            if (this.intValue2 > 0 || this.intValue3 > 0) {
               this.method18().method1169(Math.min(this.intValue2, 20), Math.min(this.intValue3, 10));
               this.intValue2 = Math.max(0, this.intValue2 - 20);
               this.intValue3 = Math.max(0, this.intValue3 - 10);
            }
         }
      }
   }

   private void method37(PartyMember partyMember) {
      PartyMember.Data6 data6 = partyMember.getData6();
      PartyMember.Data6 data62 = this.data6;
      this.data6 = data6;
      if (data6 != null && data62 == null && !this.flag2) {
         this.flag2 = true;
         PartyMarksModule.INSTANCE.setToggled(true);
      }

      if (data62 != null && data6 != null && data62.getLongValue() == data6.getLongValue() && !this.method32()) {
         for (PartyMember.Data3 data3 : data6.getItems()) {
            if (data3.uid() != this.cloud.getLongValue()) {
               PartyMember.Data3 data32 = data62.method1216(data3.uid());
               if (data32 == null) {
                  method53(String.format(method54("cloud.party.toast.joined"), data3.username()));
               } else if (data32.isFlag() != data3.isFlag() && !this.cloud.prefs().method3315(data3.username())) {
                  method53(String.format(method54(data3.isFlag() ? "cloud.party.toast.online" : "cloud.party.toast.offline"), data3.username()));
               }
            }
         }

         for (PartyMember.Data3 data3 : data62.getItems()) {
            if (data3.uid() != this.cloud.getLongValue() && data6.method1216(data3.uid()) == null) {
               method53(String.format(method54("cloud.party.toast.left"), data3.username()));
            }
         }
      }
   }

   @EventTarget
   private void method38(Event10 event) {
      CloudPackets.Data18 data18 = event.getData18();
      boolean flag = this.method32() || !this.share.notifyInvites.isEnabled();
      if (!flag && method39(data18.kind()) && !this.method40(data18)) {
         flag = true;
      }

      switch (data18.kind()) {
         case 12:
            long l = data18.getJsonObject().has("uid") ? data18.getJsonObject().get("uid").getAsLong() : 0L;
            if (l != 0L && l != this.cloud.getLongValue() && !this.method32() && !this.cloud.prefs().method3316(data18.username())) {
               method53(
                  String.format(
                     method54("cloud.death.toast.mate"),
                     data18.username(),
                     (int)Math.floor(data18.getJsonObject().get("x").getAsDouble()),
                     (int)Math.floor(data18.getJsonObject().get("y").getAsDouble()),
                     (int)Math.floor(data18.getJsonObject().get("z").getAsDouble())
                  )
               );
            }
            break;
         case 21:
            if (!flag && !this.cloud.prefs().method3316(data18.username())) {
               String str = ServerNameUtils.method5643(
                  PartyMember.method1209(data18.getJsonObject(), "server"), PartyMember.method1209(data18.getJsonObject(), "mode")
               );
               method53(
                  String.format(
                     method54("cloud.join.toast.invited"), data18.username(), str.isEmpty() ? PartyMember.method1209(data18.getJsonObject(), "join") : str
                  )
               );
            }
            break;
         case 41:
            if (!flag && !this.cloud.prefs().method3316(data18.username())) {
               method53(String.format(method54("cloud.party.toast.invited"), data18.username(), PartyMember.method1209(data18.getJsonObject(), "name")));
            }
            break;
         case 46:
            if (!flag && !this.cloud.prefs().method3316(data18.username())) {
               method53(String.format(method54("cloud.room.toast.invited"), data18.username(), PartyMember.method1209(data18.getJsonObject(), "name")));
            }
            break;
         case 49:
            String anObject = PartyMember.method1209(data18.getJsonObject(), "code");
            if (!anObject.isEmpty() && !"mate_gone".equals(anObject)) {
               method53(method54("cloud.notice." + anObject));
            }
            break;
         case 51:
            if (this.share.notifyMessages.isEnabled() && !this.method32() && !this.cloud.prefs().method3316(data18.username())) {
               String str = PartyMember.method1209(data18.getJsonObject(), "text");
               method53(String.format(method54("cloud.dm.toast"), data18.username(), str.length() > 60 ? str.substring(0, 60) + "…" : str));
            }
      }
   }

   private static boolean method39(int kind) {
      return kind == 41 || kind == 46 || kind == 21;
   }

   private boolean method40(CloudPackets.Data18 data18) {
      long now = System.currentTimeMillis();
      this.items2.values().removeIf(var2x -> now - var2x > 10000L);
      return this.items2.putIfAbsent(data18.uid() * 64L + data18.kind(), now) == null;
   }

   @EventTarget
   private void method41(Event11 event) {
      CloudPackets.Data13 data13 = event.getData13();
      if (!event.isFlag()
         && data13.getIntValue() == 1
         && this.share.notifyMessages.isEnabled()
         && !this.method32()
         && !this.cloud.prefs().method3316(data13.username())) {
         String text = data13.text().length() > 60 ? data13.text().substring(0, 60) + "…" : data13.text();
         method53(data13.username() + ": " + text);
      }
   }

   public boolean method42(long uid) {
      PartyMember.Data2 data2 = this.method18().method1141(uid);
      return !data2.getText3().isEmpty();
   }

   public void method43(long uid) {
      PartyMember.Data2 data2 = this.method18().method1141(uid);
      if (!data2.getText3().isEmpty()) {
         this.method45(data2.getText3(), data2.server(), data2.mode());
      }
   }

   public void method44(PartyMember.Data13 data13) {
      this.method18().method1178(data13);
      if (!data13.getText3().isEmpty()) {
         this.method45(data13.getText3(), data13.server(), data13.mode());
      }
   }

   public void method45(String str, String server, String mode) {
      String ip = str.trim();
      if (!ip.isEmpty()) {
         String str2 = this.cloud.method7911();
         String anObject = server.isEmpty() ? Cloud.method7912(ip.toLowerCase(Locale.ROOT)) : server;
         this.text2 = anObject;
         this.text3 = mode;
         this.longValue8 = System.currentTimeMillis();
         if (str2.isEmpty() || !str2.equals(anObject) || mc.level == null) {
            mc.execute(() -> {
               if (mc.level != null) {
                  mc.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE);
               }

               ServerData var2x = new ServerData(ServerNameUtils.display(anObject), ip, Type.OTHER);
               ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), mc, ServerAddress.parseString(ip), var2x, false, null);
            });
         }
      }
   }

   private void method46(long now) {
      if (!this.text2.isEmpty() && mc.level != null && mc.player != null) {
         if (now - this.longValue8 > 90000L) {
            this.text2 = "";
         } else if (now - this.longValue8 >= 4000L && this.text2.equals(this.cloud.method7911())) {
            String string = this.text3;
            String ip = this.text2;
            this.text2 = "";
            if (!string.isEmpty()) {
               String mode = ChatCompose.mode();
               if (mode.isEmpty() || !ServerNameUtils.method5641(mode).equalsIgnoreCase(ServerNameUtils.method5641(string))) {
                  String initial = ServerNameUtils.method5642(ip, string);
                  if (initial.isEmpty()) {
                     method53(String.format(method54("cloud.join.toast.mode"), ServerNameUtils.method5641(string)));
                  } else {
                     method53(String.format(method54("cloud.join.toast.command"), ServerNameUtils.method5641(string), initial));
                     if (mc.gui.screen() == null) {
                        mc.gui.setScreen(new ChatScreen(initial, false));
                     }
                  }
               }
            }
         }
      }
   }

   public boolean method47() {
      return !this.method33().isEmpty() && this.cloud.method7860();
   }

   public void method48(long uid) {
      String string = this.method33();
      if (!string.isEmpty()) {
         String mode = mc.level == null ? "" : ChatCompose.mode();
         this.method18().method1177(uid, string, mode, this.cloud.method7911(), "");
         method53(String.format(method54("cloud.join.toast.sent"), this.method18().method1149(uid)));
      }
   }

   public boolean method49(String name, String key, int color) {
      if (mc.player != null && this.method18().method1136()) {
         this.method18()
            .method1160(
               0L,
               this.cloud.method7911(),
               this.cloud.method7913(),
               Math.floor(mc.player.getX()) + 0.5,
               Math.floor(mc.player.getY()),
               Math.floor(mc.player.getZ()) + 0.5,
               name,
               key,
               color
            );
         return true;
      } else {
         return false;
      }
   }

   public static String method50(String anObject) {
      return method54("cloud.presence." + (anObject != null && !anObject.isEmpty() ? anObject : "online"));
   }

   public static ZenithColor method51(String text) {
      String str = text == null ? "" : text;

      return switch (str) {
         case "pvp" -> new ZenithColor(-495247);
         case "farm" -> new ZenithColor(-11870592);
         case "build" -> new ZenithColor(-278748);
         case "trade" -> new ZenithColor(-13058568);
         case "afk" -> new ZenithColor(-7035976);
         case "dnd" -> new ZenithColor(-1096636);
         case "menu" -> new ZenithColor(-5796870);
         default -> new ZenithColor(-14498466);
      };
   }

   public static String method52(PartyMember.Data2 data2) {
      String str = method50(data2.getText());
      return data2.text().isEmpty() ? str : str + " · " + data2.text();
   }

   private static void method53(String str) {
      Cloud.method7914(str);
   }

   public static String method54(String text) {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? text : zenith.getLocalization().translate(text);
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final List<PartyMember.Data11> items;
      private final int intValue;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
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

      public List<PartyMember.Data11> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, doubleValue, doubleValue2, doubleValue3, items, intValue, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(String text, String str, double doubleValue, double y, double z, List<PartyMember.Data11> items, int intValue, long longValue) {
         this.text = text;
         this.text2 = str;
         this.doubleValue = doubleValue;
         this.doubleValue2 = y;
         this.doubleValue3 = z;
         this.items = items;
         this.intValue = intValue;
         this.longValue = longValue;
      }

      public String server() {
         return this.text;
      }

      public String dimension() {
         return this.text2;
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

      public List<PartyMember.Data11> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long time() {
         return this.longValue;
      }
   }
}
