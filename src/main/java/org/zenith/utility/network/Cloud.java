package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.CloudWaypointEvent;
import org.zenith.client.events.CloudMessageEvent;
import org.zenith.client.events.PartyUpdateEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.utility.api.CloudJsonIo;
import org.zenith.utility.api.CloudTicket;
import org.zenith.utility.api.CloudWebSocket;
import org.zenith.utility.api.Cosmetics;
import org.zenith.utility.api.Friends;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Prefs;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.api.Util18;
import org.zenith.utility.misc.CloudConnectionState;
import org.zenith.utility.misc.Data1;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.misc.StoreEntry;

public final class Cloud implements MinecraftAccess, Friends.Handler {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final String text = "nexus-cloud.json";
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 2;
   private static final String text2 = System.getProperty("nexus.cloud.secret", "bJjl3_Q0sUH26wj4XWcgGeWjXY9nwEoPcgOFjBsC-r-SUcXWY66UDDmB_D7YqhJj");
   private static final int CONST_intValue3 = 1;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 3;
   private static final int CONST_intValue6 = 4;
   private static final int CONST_intValue7 = 5;
   private static final int CONST_intValue8 = 6;
   private static final long[] CONST_longValues = new long[]{2000L, 4000L, 8000L, 15000L, 30000L};
   private static final long CONST_longValue = 45000L;
   private static final long CONST_longValue2 = 12000L;
   private static final long CONST_longValue3 = 300000L;
   private static final int CONST_intValue9 = 3;
   private static final long CONST_longValue4 = 600000L;
   private static final long[] CONST_longValues2 = new long[]{60000L, 120000L, 300000L, 600000L};
   private static final int CONST_intValue10 = 6;
   private static final int CONST_intValue11 = 3;
   private static final long CONST_longValue5 = 2000L;
   private static final long CONST_longValue6 = 15000L;
   private static final long CONST_longValue7 = 15000L;
   private final Path path;
   private final Friends friends;
   private final Prefs prefs;
   private final CloudConnectionState cloudConnectionState = new CloudConnectionState();
   private final PartyMember partyMember;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private String text3 = "";
   private volatile String url = RegionUtil.method2040().get(0);
   private volatile String text4;
   private int intValue;
   private volatile boolean flag;
   private volatile String text5;
   private volatile CloudWebSocket cloudWebSocket;
   private volatile long longValue;
   private volatile String text6 = "";
   private boolean flag2;
   private int intValue2;
   private long longValue2;
   private int intValue3;
   private long longValue3 = CONST_longValues[0];
   private int intValue4;
   private int intValue5;
   private long longValue4;
   private final Object obj = new Object();
   private volatile int intValue6;
   private volatile Boolean booleanValue;
   private volatile boolean flag3;
   private volatile Cloud.Handler handler;
   private final Set<Long> items = new HashSet<>();
   private long longValue5;
   private volatile Cloud.Data data;
   private volatile Cloud.Data data2;
   private volatile Cloud.Data data3;

   public Cloud(Path path, Friends friends) {
      this.path = path.resolve("nexus-cloud.json");
      this.friends = friends;
      this.prefs = new Prefs(path);
      this.partyMember = new PartyMember(this, path);
      this.load();
      friends.setHandler(this);
      EventManager.register(this);
      RegionUtil.method2042(() -> mc.execute(() -> {
         if (!this.cloudConnectionState.method8035()) {
            this.text4 = null;
            this.intValue = 0;
            if (this.cloudWebSocket == null) {
               this.url = this.method7869();
            }
         }
      }));
   }

   public CloudConnectionState state() {
      return this.cloudConnectionState;
   }

   public Friends getFriends() {
      return this.friends;
   }

   public Prefs prefs() {
      return this.prefs;
   }

   public PartyMember getPartyMember() {
      return this.partyMember;
   }

   public boolean method7860() {
      return this.cloudConnectionState.method8035() && (this.intValue6 & 2) != 0;
   }

   public void method7861(int n, JsonObject json) {
      if (this.method7860()) {
         ByteBuffer byteBuffer;
         try {
            byteBuffer = CloudPackets.method1799(n, json.toString());
         } catch (RuntimeException e) {
            logger.warn("Облако: соц-кадр {} не отправлен: {}", n, CloudWebSocket.method1956(e));
            return;
         }

         this.method7896(byteBuffer, 0);
      }
   }

   public boolean method7862() {
      return this.cloudConnectionState.method8035();
   }

   public boolean method7863() {
      return this.cloudConnectionState.method8035() && (this.intValue6 & 1) != 0;
   }

   public int method7864() {
      CloudWebSocket cloudWebSocket = this.cloudWebSocket;
      return cloudWebSocket != null && this.cloudConnectionState.method8035() ? cloudWebSocket.getIntValue() : -1;
   }

   public void method7865() {
      CloudWebSocket cloudWebSocket = this.cloudWebSocket;
      if (cloudWebSocket != null && this.cloudConnectionState.method8035()) {
         cloudWebSocket.ping();
      }
   }

   public void setHandler(Cloud.Handler handler) {
      this.handler = handler;
   }

   public String url() {
      return this.url;
   }

   public boolean method7866() {
      return !this.text3.isEmpty();
   }

   public void method7867(String string) {
      String str = string.trim();

      while (str.endsWith("/")) {
         str = str.substring(0, str.length() - 1);
      }

      if (!str.isEmpty() && !str.endsWith("/ws")) {
         int indexOf = str.indexOf(47, str.indexOf("//") + 2);
         str = (indexOf < 0 ? str : str.substring(0, indexOf)) + "/ws";
      }

      this.text3 = str;
      this.text5 = null;
      this.url = this.method7869();
      this.save();
   }

   public void method7868() {
      this.text3 = "";
      this.text5 = null;
      this.url = this.method7869();
      this.save();
   }

   private String method7869() {
      if (!this.text3.isEmpty()) {
         return this.text3;
      } else {
         var strings = RegionUtil.method2040();
         String str = this.text4;
         return str != null && strings.contains(str) ? str : (String)strings.get(0);
      }
   }

   public String method7870() {
      if (!this.text3.isEmpty()) {
         return this.text3;
      } else {
         String str = this.text5;
         return str != null && RegionUtil.method2040().contains(str) ? str : RegionUtil.method2040().get(0);
      }
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String method7871() {
      return mc.getUser() == null ? "" : mc.getUser().getName();
   }

   public static long accountUid() {
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getEntry() != null) {
         String str = zenith.getEntry().getText2();
         if (str != null && !str.isBlank()) {
            try {
               return Long.parseLong(str.trim());
            } catch (NumberFormatException e) {
               return 0L;
            }
         } else {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   public static String accountName() {
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getEntry() != null) {
         String username = zenith.getEntry().getUsername();
         return username == null ? "" : username;
      } else {
         return "";
      }
   }

   public static String getText2() {
      return text2;
   }

   public void method7872() {
      this.flag2 = true;
      if (accountUid() <= 0L) {
         logger.info("Облако: аккаунт ещё не готов, подключение отложено");
         this.intValue2 = 1;
         this.method7873();
      } else {
         this.open();
      }
   }

   private void method7873() {
      int n = Math.max(0, Math.min(this.intValue2, CONST_longValues.length) - 1);
      long l = this.longValue2 > 0L ? this.longValue2 : CONST_longValues[n];
      this.longValue3 = method7874(l);
      this.stopWatch.reset();
   }

   public static long method7874(long l) {
      double d = 0.8 + ThreadLocalRandom.current().nextDouble() * 0.4;
      return Math.max(250L, (long)(l * d));
   }

   private void method7875() {
      this.intValue5++;
      String str = method7876();
      if (this.intValue5 >= 3) {
         logger.warn("Облако: uid аккаунта не распознан: «{}» — подключение остановлено", str);
         this.flag2 = false;
         this.intValue2 = 0;
         this.cloudConnectionState.method8037("uid аккаунта не распознан: " + (str.isEmpty() ? "пусто" : str));
         this.method7882(CloudConnectionState.Type.FAILED);
      } else {
         logger.debug("Облако: аккаунт ещё не готов (попытка {})", this.intValue5);
         this.intValue2++;
         this.method7873();
      }
   }

   private static String method7876() {
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getEntry() != null && zenith.getEntry().getText2() != null) {
         String str = zenith.getEntry().getText2().trim();
         return str.length() > 40 ? str.substring(0, 40) + "…" : str;
      } else {
         return "";
      }
   }

   public void connect() {
      if (this.cloudConnectionState.getType() != CloudConnectionState.Type.CONNECTING && !this.cloudConnectionState.method8035()) {
         this.intValue5 = 0;
         this.intValue4 = 0;
         if (accountUid() <= 0L) {
            this.flag2 = true;
            this.longValue2 = 0L;
            this.intValue3 = 0;
            this.intValue2 = 1;
            this.method7873();
            this.cloudConnectionState.method8037("аккаунт ещё не готов — подключусь сам");
         } else {
            this.flag2 = true;
            this.intValue2 = 0;
            this.longValue2 = 0L;
            this.intValue3 = 0;
            this.open();
         }
      }
   }

   public void disconnect() {
      this.flag2 = false;
      this.intValue2 = 0;
      this.method7877("клиент отключился");
      this.cloudConnectionState.method8037("отключено вручную");
      this.method7882(CloudConnectionState.Type.OFFLINE);
   }

   public void shutdown() {
      this.prefs.method3325();
      this.flag2 = false;
      this.intValue2 = 0;
      EventManager.unregister(this);
      this.method7877("клиент закрывается");
   }

   private void open() {
      if (accountUid() <= 0L) {
         this.method7875();
      } else {
         this.intValue5 = 0;
         CloudTicket.method1374();
         this.cloudConnectionState.method8036("");
         this.url = this.method7869();
         this.flag = false;

         URI uri;
         try {
            uri = new URI(this.url);
         } catch (URISyntaxException e) {
            logger.error("Облако: неверный адрес {}", this.url);
            this.flag2 = false;
            this.intValue2 = 0;
            this.cloudConnectionState.method8037("неверный адрес узла: " + this.url);
            this.method7882(CloudConnectionState.Type.FAILED);
            return;
         }

         this.method7877("переподключение");
         this.method7882(CloudConnectionState.Type.CONNECTING);
         this.stopWatch2.reset();
         logger.info("Облако: подключение к {} как {}#{} ({}), протокол {}", new Object[]{this.url, accountName(), accountUid(), this.method7871(), 5});
         CloudWebSocket cloudWebSocket = new CloudWebSocket(uri, this::method7883, () -> logger.debug("Облако: сокет открыт, ждём CHALLENGE"), this::method7878);
         this.cloudWebSocket = cloudWebSocket;
         cloudWebSocket.connect();
      }
   }

   private void method7877(String str) {
      this.prefs.method3308();
      CloudWebSocket cloudWebSocket;
      synchronized (this.obj) {
         cloudWebSocket = this.cloudWebSocket;
         this.cloudWebSocket = null;
         this.longValue = 0L;
         this.intValue6 = 0;
         this.flag3 = false;
         this.cloudConnectionState.method8064();
      }

      if (cloudWebSocket != null) {
         cloudWebSocket.method1954(str);
      }

      this.partyMember.method1151();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      CloudWebSocket cloudWebSocket = this.cloudWebSocket;
      if (cloudWebSocket != null && this.cloudConnectionState.method8035() && cloudWebSocket.method1947() > 45000L) {
         cloudWebSocket.method1950("узел не отвечает 45 с");
      } else if (cloudWebSocket != null && this.cloudConnectionState.getType() == CloudConnectionState.Type.CONNECTING && this.stopWatch2.method7546(15000L)) {
         cloudWebSocket.method1950("узел не ответил за 15 с");
      } else {
         this.prefs.flush();
         if (cloudWebSocket != null && this.cloudConnectionState.method8035() && (cloudWebSocket.method1948() > 12000L || cloudWebSocket.method1947() > 12000L && cloudWebSocket.method1949() > 3000L)) {
            cloudWebSocket.ping();
         }

         String anObject = this.method7871();
         if (this.cloudConnectionState.method8035() && !anObject.isEmpty() && !this.text6.equals(anObject)) {
            logger.info("Облако: ник сменился {} → {}, перезаход", this.text6, anObject);
            this.open();
         } else {
            if (this.intValue3 > 0
               && this.cloudConnectionState.method8035()
               && this.longValue4 != 0L
               && System.currentTimeMillis() - this.longValue4 >= 600000L) {
               this.intValue3 = 0;
            }

            if (this.intValue2 != 0 && this.flag2) {
               if (cloudWebSocket == null || this.cloudConnectionState.getType() != CloudConnectionState.Type.CONNECTING) {
                  if (this.stopWatch.method7546(this.longValue3)) {
                     this.longValue2 = 0L;
                     this.stopWatch.reset();
                     logger.info("Облако: повтор подключения #{} к {}", this.intValue2, this.method7869());
                     this.open();
                  }
               }
            }
         }
      }
   }

   private void method7878(CloudWebSocket cloudWebSocket, String str) {
      mc.execute(() -> {
         synchronized (this.obj) {
            if (cloudWebSocket != this.cloudWebSocket) {
               logger.debug("Облако: старое соединение закрылось ({})", str);
               return;
            }

            this.longValue = 0L;
            this.intValue6 = 0;
            this.cloudConnectionState.method8064();
            if (this.flag3) {
               this.flag3 = false;
               this.booleanValue = Boolean.FALSE;
               logger.info("Облако: узел оборвал вход с билетом сайта — следующий вход без билета");
            }
         }

         this.longValue4 = 0L;
         this.partyMember.method1151();
         String lastError = this.cloudConnectionState.getLastError();
         this.method7882(lastError.isEmpty() ? CloudConnectionState.Type.OFFLINE : CloudConnectionState.Type.FAILED);
         logger.info("Облако: {}", str);
         if (this.flag2 && this.longValue2 == 0L) {
            this.cloudConnectionState.method8037(lastError.isEmpty() ? str : lastError);
         }

         if (!this.flag && lastError.isEmpty() && this.longValue2 == 0L) {
            this.method7879();
         }

         if (this.flag2) {
            this.intValue2++;
            this.method7873();
         }
      });
   }

   private void method7879() {
      if (this.text3.isEmpty() && ++this.intValue >= 2) {
         this.intValue = 0;
         var strings = RegionUtil.method2040();
         int indexOf = strings.indexOf(this.url);
         this.text4 = (String)strings.get(indexOf < 0 ? 0 : (indexOf + 1) % strings.size());
         if (!this.text4.equals(this.url)) {
            logger.info("Облако: {} не пускает {} раза подряд — дальше {}", new Object[]{this.url, 2, this.text4});
         }
      }
   }

   private void method7880() {
      this.intValue3++;
      if (this.intValue3 >= 3) {
         this.flag2 = false;
         this.longValue2 = 0L;
         this.cloudConnectionState.method8037("под аккаунтом играет второй клиент — закрой его и нажми «Переподключиться»");
         this.method7882(CloudConnectionState.Type.FAILED);
         logger.warn("Облако: второй клиент под тем же аккаунтом, автоповтор выключен");
         method7914("Облако выключено: под аккаунтом играет второй клиент");
      } else {
         this.longValue2 = 300000L;
         this.cloudConnectionState.method8037("под аккаунтом зашли с другого клиента — вернусь через 5 мин");
         logger.info("Облако: выбиты вторым клиентом, повтор через {} мин", 5L);
      }
   }

   private void method7881(String text) {
      CloudTicket.invalidate();
      this.intValue4++;
      if (this.intValue4 >= 6) {
         this.flag2 = false;
         this.longValue2 = 0L;
         this.cloudConnectionState.method8037("узел не пускает: " + text + " — нажми «Переподключиться»");
         this.method7882(CloudConnectionState.Type.FAILED);
         logger.warn("Облако: {} отказов во входе подряд, автоповтор выключен", this.intValue4);
      } else {
         long l = CONST_longValues2[Math.min(this.intValue4, CONST_longValues2.length) - 1];
         this.longValue2 = l;
         this.cloudConnectionState.method8037("узел не пустил: " + text + " — повтор через " + l / 60000L + " мин");
         logger.info("Облако: отказ во входе #{}, повтор через {} мин", this.intValue4, l / 60000L);
      }
   }

   private void method7882(CloudConnectionState.Type type) {
      this.cloudConnectionState.setType(type);
      if (type == CloudConnectionState.Type.ONLINE) {
         this.cloudConnectionState.method8036("");
         this.cloudConnectionState.method8037("");
      }

      EventManager.call(new CloudStateEvent(type));
   }

   private void method7883(CloudWebSocket cloudWebSocket, ByteBuffer buffer) {
      if (cloudWebSocket == this.cloudWebSocket) {
         FriendlyByteBuf friendlyByteBuf = CloudPackets.method1818(buffer);
         if (friendlyByteBuf.isReadable()) {
            short unsignedByte = friendlyByteBuf.readUnsignedByte();
            if ((unsignedByte & 128) != 0 && (unsignedByte & 'ｿ') >= 16) {
               try {
                  this.method7884(cloudWebSocket, unsignedByte, friendlyByteBuf);
               } catch (RuntimeException e) {
                  logger.debug("Облако: битый кадр 0x{} пропущен: {}", Integer.toHexString(unsignedByte), CloudWebSocket.method1956(e));
               }
            } else {
               synchronized (this.obj) {
                  if (cloudWebSocket == this.cloudWebSocket) {
                     this.method7886(cloudWebSocket, unsignedByte, friendlyByteBuf);
                  }
               }
            }
         }
      }
   }

   private void method7884(CloudWebSocket cloudWebSocket, int unsignedByte, FriendlyByteBuf friendlyByteBuf) {
      if (unsignedByte == 163) {
         this.method7885(CloudPackets.method1802(friendlyByteBuf));
      } else if (unsignedByte == 165) {
         CloudPackets.Data18 data18 = CloudPackets.method1804(friendlyByteBuf);
         mc.execute(() -> {
            if (cloudWebSocket == this.cloudWebSocket) {
               this.partyMember.method1180(data18);
            }
         });
      } else {
         synchronized (this.obj) {
            if (cloudWebSocket == this.cloudWebSocket) {
               this.method7886(cloudWebSocket, unsignedByte, friendlyByteBuf);
            }
         }
      }
   }

   private void method7885(CloudPackets.Data16 data16) {
      PartyPlayerInfo partyPlayerInfo = this.cloudConnectionState.method8040(data16.uid());
      Cloud.Handler handler = this.handler;
      boolean flag = partyPlayerInfo != null ? !this.prefs.method3316(partyPlayerInfo.username()) : this.partyMember.method1148(data16.uid());
      if (flag && handler != null) {
         try {
            handler.onVoice(data16);
         } catch (LinkageError | RuntimeException e) {
            logger.debug("Облако: голос не принят: {}", e.toString());
         }
      }
   }

   private void method7886(CloudWebSocket cloudWebSocket, int unsignedByte, FriendlyByteBuf friendlyByteBuf) {
      switch (unsignedByte) {
         case 128:
            this.method7887(cloudWebSocket, CloudPackets.method1800(friendlyByteBuf));
            break;
         case 129:
            this.method7889(CloudPackets.method1801(friendlyByteBuf));
            break;
         case 130:
            CloudPackets.Data data = CloudPackets.method1805(friendlyByteBuf);
            this.cloudConnectionState.method8036(data.getText());
            this.flag3 = false;
            if (data.getIntValue() == 2 && Boolean.FALSE.equals(this.booleanValue)) {
               this.booleanValue = null;
            }

            if (data.getIntValue() == 1) {
               mc.execute(() -> {
                  this.flag2 = false;
                  this.cloudConnectionState.method8037(data.getText());
               });
            } else if (data.getIntValue() == 2) {
               mc.execute(() -> this.method7881(data.getText()));
            } else if (data.getIntValue() == 3) {
               mc.execute(this::method7880);
            }

            logger.warn("Облако: отказ {} — {}", data.getIntValue(), data.getText());
            break;
         case 131:
            this.method7891(CloudPackets.method1806(friendlyByteBuf));
            break;
         case 132:
            this.method7892(CloudPackets.method1807(friendlyByteBuf));
            break;
         case 133:
         case 134:
            this.method7893(CloudPackets.method1806(friendlyByteBuf));
            break;
         case 135:
            this.method7894(CloudPackets.method1808(friendlyByteBuf));
            break;
         case 136:
            this.method7895(CloudPackets.method1809(friendlyByteBuf));
            break;
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 163:
         default:
            logger.debug("Облако: кадр 0x{} не обрабатывается", Integer.toHexString(unsignedByte));
            break;
         case 144:
            CloudPackets.Data5 data5 = CloudPackets.method1810(friendlyByteBuf);
            PartyPlayerInfo partyPlayerInfo = this.cloudConnectionState.method8040(data5.uid());
            if (partyPlayerInfo != null) {
               partyPlayerInfo.method3295(data5);
            }
            break;
         case 145:
            CloudPackets.Data2 data2 = CloudPackets.method1811(friendlyByteBuf);
            PartyPlayerInfo partyPlayerInfo2 = this.cloudConnectionState.method8040(data2.uid());
            if (partyPlayerInfo2 != null) {
               partyPlayerInfo2.method3296(data2);
            }
            break;
         case 146:
            CloudPackets.Data3 data3 = CloudPackets.method1812(friendlyByteBuf);
            PartyPlayerInfo partyPlayerInfo3 = this.cloudConnectionState.method8040(data3.uid());
            if (partyPlayerInfo3 != null) {
               partyPlayerInfo3.method3297(data3);
            }
            break;
         case 147:
            CloudPackets.Data11 data11 = CloudPackets.method1813(friendlyByteBuf);
            PartyPlayerInfo partyPlayerInfo4 = this.cloudConnectionState.method8040(data11.uid());
            if (partyPlayerInfo4 != null) {
               partyPlayerInfo4.method3298(data11);
            }
            break;
         case 148:
            CloudPackets.Data15 data15 = CloudPackets.method1814(friendlyByteBuf);
            this.cloudConnectionState.method8059(data15.uid(), data15.getText(), data15.getItems());
            PartyPlayerInfo partyPlayerInfo5 = this.cloudConnectionState.method8040(data15.uid());
            if (partyPlayerInfo5 != null) {
               partyPlayerInfo5.method3299(data15);
            }

            mc.execute(() -> this.method7888(data15));
            break;
         case 160:
            CloudPackets.Data13 data13 = CloudPackets.method1815(friendlyByteBuf);
            mc.execute(() -> {
               this.cloudConnectionState.method8050(data13);
               EventManager.call(new CloudMessageEvent(data13, data13.uid() == this.longValue));
            });
            break;
         case 161:
            CloudPackets.Data10 data10 = CloudPackets.method1817(friendlyByteBuf);
            mc.execute(() -> EventManager.call(new CloudWaypointEvent(data10, data10.uid() == this.longValue)));
            break;
         case 162:
            long l = CloudPackets.method1816(friendlyByteBuf);
            mc.execute(() -> this.cloudConnectionState.method8051(l));
            break;
         case 164:
            CloudPackets.Data6 data6 = CloudPackets.method1803(friendlyByteBuf);
            PartyPlayerInfo partyPlayerInfo6 = this.cloudConnectionState.method8040(data6.uid());
            if (partyPlayerInfo6 != null) {
               partyPlayerInfo6.setIntValue2(data6.getIntValue());
            }
      }
   }

   private void method7887(CloudWebSocket cloudWebSocket, CloudPackets.Data4 data4) {
      if (data4.protocol() != 5) {
         String text = data4.protocol() > 5
            ? "узел новее клиента (протокол " + data4.protocol() + " против 5) — обнови сборку"
            : "узел старее клиента (протокол " + data4.protocol() + " против 5) — обнови узел";
         this.cloudConnectionState.method8036(text);
         logger.error("Облако: {}", text);
         mc.execute(() -> {
            this.flag2 = false;
            this.cloudConnectionState.method8037(text);
            if (this.cloudWebSocket == cloudWebSocket) {
               this.method7877("протокол");
            }

            this.method7882(CloudConnectionState.Type.FAILED);
            method7914("Облако: " + text);
         });
         cloudWebSocket.method1954("протокол");
      } else {
         long l = accountUid();
         String str = accountName();
         String str2 = this.method7871();
         UUID profileId = mc.getUser() == null ? null : mc.getUser().getProfileId();
         byte[] src = method7915(l, str, str2, data4.getText());
         String string = profileId == null ? "" : profileId.toString();
         this.text6 = str2;
         var exceptionally = CloudTicket.method1374().completeOnTimeout(null, 2000L, TimeUnit.MILLISECONDS).exceptionally(throwable -> null);
         CompletableFuture completedFuture;
         if (!data4.isFlag()) {
            completedFuture = CompletableFuture.completedFuture(null);
         } else {
            completedFuture = CompletableFuture.runAsync(() -> {
               try {
                  mc.services().sessionService().joinServer(profileId, mc.getUser().getAccessToken(), data4.getText());
               } catch (Exception var3x) {
                  logger.warn("Облако: Mojang не подтвердил сессию: {}", CloudWebSocket.method1956(var3x));
               }
            }, Util.ioPool()).exceptionally(throwable -> null);
         }

         exceptionally.<Void, String>thenCombine(completedFuture, (str3, var1x) -> (String)str3).whenComplete((var8x, var9x) -> {
            if (cloudWebSocket == this.cloudWebSocket) {
               String var10x = var9x == null && !Boolean.FALSE.equals(this.booleanValue) ? (String)var8x : null;

               ByteBuffer var11x;
               try {
                  var11x = CloudPackets.method1783(l, str, str2, string, "3.5", src, var10x);
               } catch (RuntimeException e) {
                  String text = "вход не собран: " + CloudWebSocket.method1956(e);
                  logger.error("Облако: {}", text);
                  this.cloudConnectionState.method8036(text);
                  cloudWebSocket.method1950(text);
                  return;
               }

               this.flag3 = var10x != null;
               logger.debug("Облако: HELLO {}", var10x != null ? "с билетом сайта" : "без билета");
               cloudWebSocket.method1951(var11x, 0);
            }
         });
      }
   }

   private void method7888(CloudPackets.Data15 data15) {
      Zenith zenith = Zenith.getInstance();
      Cosmetics cosmetics = zenith == null ? null : zenith.getCosmetics();
      if (cosmetics != null && data15.getText() != null && !data15.getText().isBlank()) {
         List<Data1> items = new ArrayList<>(data15.getItems().size());

         for (CloudPackets.Data9 data9 : data15.getItems()) {
            if (data9.getText() != null && !data9.getText().isBlank() && !ShareCodec.method6723(data9.getText())) {
               items.add(new Data1(data9.getText(), data9.method1822().name(), data9.getIntValue2()));
            }
         }

         cosmetics.method2962(new StoreEntry(Long.toString(data15.uid()), data15.getText(), "PUBLIC", 0, System.currentTimeMillis(), items), true);
      }
   }

   private void method7889(CloudPackets.Data12 data12) {
      String url = this.url;
      this.flag = true;
      this.text5 = url;
      if (this.text3.isEmpty()) {
         this.text4 = url;
      }

      this.longValue = data12.uid();
      this.intValue6 = data12.getIntValue2();
      this.flag3 = false;
      this.booleanValue = (data12.getIntValue2() & 4) != 0;
      this.cloudConnectionState.setIntValue(data12.getIntValue());
      Util18.method4490(data12.getLongValue2());
      logger.info(
         "Облако: вошли как {}#{}, всего {} онлайн{}",
         new Object[]{data12.username(), data12.uid(), data12.getIntValue(), (data12.getIntValue2() & 4) != 0 ? ", узел проверяет билеты сайта" : ""}
      );
      mc.execute(() -> {
         this.intValue2 = 0;
         this.intValue = 0;
         this.longValue2 = 0L;
         this.intValue4 = 0;
         this.intValue5 = 0;
         long var1x = System.currentTimeMillis();
         this.longValue4 = var1x;
         this.longValue5 = var1x;
         this.method7882(CloudConnectionState.Type.ONLINE);
         this.method7896(CloudPackets.method1784(this.friends.all()), 0);
         this.method7890();
         this.partyMember.method1150();
      });
   }

   private void method7890() {
      StoreApi storeApi = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
      if (storeApi != null) {
         storeApi.method1965(200).whenComplete((var1x, error) -> {
            if (error != null) {
               logger.warn("Облако: история чата не пришла: {}", CloudWebSocket.method1956(error));
            } else if (!var1x.isEmpty()) {
               List<CloudPackets.Data13> data13s = new ArrayList<>(var1x.size());

               for (StoreApi.Data3 data3 : var1x) {
                  data13s.add(new CloudPackets.Data13(data3.getLongValue2(), data3.getText(), 0, data3.time(), data3.text(), data3.getLongValue()));
               }

               mc.execute(() -> this.cloudConnectionState.method8052(data13s));
            }
         });
      }
   }

   private void method7891(CloudPackets.Data8 data8) {
      boolean flag = this.cloudConnectionState.method8039(data8.uid()) == null;
      PartyPlayerInfo partyPlayerInfo = this.cloudConnectionState.method8046(data8);
      if (flag) {
         mc.execute(() -> {
            this.cloudConnectionState.method8060(data8.username());
            this.prefs.method3306(partyPlayerInfo.username());
            this.prefs.method3312(partyPlayerInfo.username(), data8.uid());
            this.prefs.method3314(partyPlayerInfo.username(), partyPlayerInfo.getText2());
            boolean var3x = !this.items.add(data8.uid()) && System.currentTimeMillis() - this.longValue5 < 15000L;
            if (!var3x && !this.prefs.method3315(partyPlayerInfo.username())) {
               method7914(partyPlayerInfo.username() + " в сети");
            }

            EventManager.call(new PartyUpdateEvent(partyPlayerInfo, true));
         });
      }
   }

   private void method7892(long l) {
      PartyPlayerInfo partyPlayerInfo = this.cloudConnectionState.method8047(l);
      if (partyPlayerInfo != null) {
         mc.execute(() -> {
            this.items.remove(l);
            this.prefs.method3307(partyPlayerInfo.username());
            this.prefs.save();
            if (!this.prefs.method3315(partyPlayerInfo.username())) {
               method7914(partyPlayerInfo.username() + " вышел");
            }

            EventManager.call(new PartyUpdateEvent(partyPlayerInfo, false));
         });
      }
   }

   private void method7893(CloudPackets.Data8 data8) {
      mc.execute(() -> {
         if (!this.friends.method6442(data8.username())) {
            if (this.cloudConnectionState.method8057(data8)) {
               method7914(data8.username() + " (" + data8.getText2() + ") просится в друзья");
            }
         }
      });
   }

   private void method7894(List<CloudPackets.Data14> data14s) {
      if (data14s == null) {
         logger.warn("Облако: негодный FRIEND_STATUS, список друзей оставлен как есть");
      } else {
         mc.execute(() -> {
            this.cloudConnectionState.method8063(data14s);
            List<String> strings = new ArrayList<>(data14s.size());

            for (CloudPackets.Data14 data14 : data14s) {
               strings.add(data14.username());
            }

            if (this.friends.method6444(strings)) {
               this.friends.save();
            }
         });
      }
   }

   private void method7895(CloudPackets.Data17 data17) {
      logger.info("Облако: {} — {}", data17.getText(), data17.getIntValue() == 1 ? "это ты сам" : "сейчас не в сети");
   }

   private void method7896(ByteBuffer byteBuffer, int intValue) {
      CloudWebSocket cloudWebSocket = this.cloudWebSocket;
      if (cloudWebSocket != null && this.cloudConnectionState.method8035()) {
         cloudWebSocket.method1951(byteBuffer, intValue);
      }
   }

   public void method7897(String text, String str) {
      this.method7896(CloudPackets.method1789(text, str), 4);
   }

   public void method7898(double x, double y, double z, float yRot, float xRot) {
      this.method7896(CloudPackets.method1790(x, y, z, yRot, xRot), 1);
   }

   public void method7899(float health, float maxHealth, float absorptionAmount, int foodLevel) {
      this.method7896(CloudPackets.method1791(health, maxHealth, absorptionAmount, foodLevel), 2);
   }

   public void method7900(List<CloudPackets.Data7> data7s) {
      this.method7896(CloudPackets.method1792(data7s), 3);
   }

   public void method7901(List<CloudPackets.Data9> data9s) {
      this.method7896(CloudPackets.method1793(this.method7871(), data9s), 5);
   }

   public void method7902(int n, int n2, int n3, byte[] src, int length) {
      if (this.method7863()) {
         this.method7896(CloudPackets.method1797(n, n2, n3, src, length), -1);
      }
   }

   public void method7903(int n) {
      if (this.method7863()) {
         this.method7896(CloudPackets.method1798(n), 6);
      }
   }

   public boolean method7904(int n, String name) {
      String username = name.strip();
      if (username.isEmpty() || username.length() > 32) {
         return false;
      } else if (this.friends.isFull()) {
         return false;
      } else if (n == 0) {
         if (this.friends.method6442(username)) {
            return false;
         } else {
            this.friends.add(username);
            this.friends.save();
            return true;
         }
      } else if (!this.method7862()) {
         return false;
      } else {
         this.method7896(CloudPackets.method1785(n, username), 0);
         return true;
      }
   }

   public boolean method7905(String username) {
      if (!this.cloudConnectionState.method8056()) {
         return false;
      } else if (this.friends.isFull() && !this.friends.method6442(username)) {
         return false;
      } else {
         this.cloudConnectionState.method8060(username);
         if (this.friends.add(username)) {
            this.friends.save();
         }

         this.method7896(CloudPackets.method1787(username), 0);
         return true;
      }
   }

   public boolean method7906(String username) {
      this.cloudConnectionState.method8060(username);
      this.method7896(CloudPackets.method1788(username), 0);
      return true;
   }

   public boolean method7907(String selected) {
      boolean flag = this.friends.method6443(selected);
      if (flag) {
         this.friends.save();
      }

      return flag;
   }

   public boolean method7908(int n, String text) {
      String str = text.strip();
      if (str.isEmpty()) {
         return false;
      } else if (!this.method7862()) {
         return false;
      } else {
         if (str.length() > 256) {
            str = str.substring(0, 256);
         }

         this.method7896(CloudPackets.method1794(n, str), 0);
         return true;
      }
   }

   public void method7909(long deleteMenuMessage) {
      if (deleteMenuMessage > 0L && this.method7862()) {
         this.method7896(CloudPackets.method1795(deleteMenuMessage), 0);
      }
   }

   public void method7910(double x, double y, double z, String str, int n, String stringUUID) {
      if (this.method7862()) {
         String str2 = str.length() > 32 ? str.substring(0, 32) : str;
         this.method7896(CloudPackets.method1796(this.method7911(), this.method7913(), x, y, z, str2, n, stringUUID == null ? "" : stringUUID), 0);
      }
   }

   @Override
   public void method6445(String username) {
      this.cloudConnectionState.method8060(username);
      this.method7896(CloudPackets.method1785(0, username), 0);
   }

   @Override
   public void method6446(String str) {
      this.method7896(CloudPackets.method1786(str), 0);
   }

   @Override
   public void method6447() {
      this.method7896(CloudPackets.method1784(this.friends.all()), 0);
   }

   public String method7911() {
      ServerData currentServer = mc.getCurrentServer();
      if (currentServer != null && currentServer.isRealm()) {
         return "realms";
      } else {
         String ip = currentServer == null ? null : currentServer.ip;
         if (ip != null && !ip.isBlank()) {
            Cloud.Data data = this.data;
            if (data != null && data.getObj() == ip) {
               return data.value();
            } else {
               String lowerCase = ip.trim().toLowerCase(Locale.ROOT);
               if (lowerCase.endsWith(":25565")) {
                  lowerCase = lowerCase.substring(0, lowerCase.length() - 6);
               }

               String text = method7912(lowerCase);
               this.data = new Cloud.Data(ip, 0L, text);
               return text;
            }
         } else if (!mc.isLocalServer()) {
            return "";
         } else {
            long longValue = accountUid();
            Cloud.Data data = this.data2;
            if (data != null && data.uid() == longValue) {
               return data.value();
            } else {
               String text = "singleplayer:" + longValue;
               this.data2 = new Cloud.Data(null, longValue, text);
               return text;
            }
         }
      }
   }

   public static String method7912(String lowerCase) {
      if (lowerCase != null && !lowerCase.isEmpty()) {
         boolean flag = true;

         for (int i = 0; i < lowerCase.length(); i++) {
            char charAt = lowerCase.charAt(i);
            if (charAt != '.' && (charAt < '0' || charAt > '9')) {
               flag = false;
               break;
            }
         }

         if (flag) {
            return lowerCase;
         } else {
            int lastIndexOf = lowerCase.lastIndexOf(46);
            if (lastIndexOf <= 0) {
               return lowerCase;
            } else {
               int n = lowerCase.lastIndexOf(46, lastIndexOf - 1);
               return n < 0 ? lowerCase : lowerCase.substring(n + 1);
            }
         }
      } else {
         return "";
      }
   }

   public String method7913() {
      ClientLevel level = mc.level;
      if (level == null) {
         return "";
      } else {
         ResourceKey dimension = level.dimension();
         Cloud.Data data = this.data3;
         if (data != null && data.getObj() == dimension) {
            return data.value();
         } else {
            String string = dimension.identifier().toString();
            this.data3 = new Cloud.Data(dimension, 0L, string);
            return string;
         }
      }
   }

   public static void method7914(String str) {
      Zenith zenith = Zenith.getInstance();
      HudHolder hudHolder = zenith == null ? null : zenith.getHudHolder();
      if (hudHolder != null) {
         hudHolder.method6972("0", Component.literal(" " + str));
      }
   }

   private static byte[] method7915(long l, String str, String str2, String text) {
      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(new SecretKeySpec(text2.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         return mac.doFinal((l + "\n" + str + "\n" + str2 + "\n" + text).getBytes(StandardCharsets.UTF_8));
      } catch (GeneralSecurityException e) {
         throw new IllegalStateException("HmacSHA256 недоступен", e);
      }
   }

   private void load() {
      JsonObject json = CloudJsonIo.method5992(this.path);

      try {
         if (json != null && json.has("url") && json.has("custom") && json.get("custom").getAsBoolean()) {
            this.method7867(json.get("url").getAsString());
         }
      } catch (RuntimeException e) {
         logger.warn("Облако: {} испорчен, адрес — встроенный: {}", "nexus-cloud.json", CloudWebSocket.method1956(e));
      }
   }

   private void save() {
      JsonObject json = new JsonObject();
      if (!this.text3.isEmpty()) {
         json.addProperty("url", this.text3);
         json.addProperty("custom", true);
      }

      CloudJsonIo.method5993(this.path, json);
   }

   private static final class Data {
      private final Object obj;
      private final long longValue;
      private final String text;

      public Object obj() {
         return this.obj;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.obj, other.obj) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(obj, longValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "obj=" + this.obj + ", " + "longValue=" + this.longValue + ", " + "text=" + this.text + "]";
      }

      private Data(Object ip, long longValue, String text) {
         this.obj = ip;
         this.longValue = longValue;
         this.text = text;
      }

      public Object getObj() {
         return this.obj;
      }

      public long uid() {
         return this.longValue;
      }

      public String value() {
         return this.text;
      }
   }

   public interface Handler {
      void onVoice(CloudPackets.Data16 data16);
   }
}
