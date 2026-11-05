package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.InteractionHand;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.client.events.Event10;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.misc.Friends;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class ScriptNetApi {
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(Redirect.NEVER).build();
   private static final long CONST_longValue = 2097152L;
   private static final int CONST_intValue = 30;
   private static final int CONST_intValue2 = 120;
   private static final int CONST_intValue3 = 2048;
   private static final Map<Script, Deque<Long>> items = new ConcurrentHashMap<>();
   private static final Map<Script, Deque<Long>> items2 = new ConcurrentHashMap<>();

   private ScriptNetApi() {
   }

   private static boolean method3274(Map<Script, Deque<Long>> items, Script workspace, int n) {
      Deque<Long> longs = items.computeIfAbsent(workspace, var0x -> new ArrayDeque<Long>());
      long now = System.currentTimeMillis();
      synchronized (longs) {
         while (!longs.isEmpty() && now - longs.peekFirst() > 60000L) {
            longs.pollFirst();
         }

         if (longs.size() >= n) {
            return false;
         } else {
            longs.addLast(now);
            return true;
         }
      }
   }

   public static URI method3275(Script workspace, String str) {
      URI uri;
      try {
         uri = URI.create(str.strip());
      } catch (IllegalArgumentException e) {
         throw ScriptValueUtils.method6251("неверный адрес: " + str);
      }

      String lowerCase = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
      if (!lowerCase.equals("https") && !lowerCase.equals("http")) {
         throw ScriptValueUtils.method6251("разрешены только http и https");
      } else {
         String lowerCase2 = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
         if (lowerCase2.isEmpty()) {
            throw ScriptValueUtils.method6251("в адресе нет хоста");
         } else {
            boolean flag = false;

            for (String anObject : workspace.method1407()) {
               if (lowerCase2.equals(anObject) || lowerCase2.endsWith("." + anObject)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               if (!workspace.method1408().isEmpty()) {
                  throw ScriptValueUtils.method6251(
                     "в // @net есть недопустимые записи " + workspace.method1408() + " — укажи полный домен сайта, например api.example.com, а не зону вроде com"
                  );
               } else {
                  throw ScriptValueUtils.method6251("домен " + lowerCase2 + " не объявлен: добавь в шапку скрипта строку // @net " + lowerCase2);
               }
            } else if (uri.getUserInfo() != null) {
               throw ScriptValueUtils.method6251("логин и пароль в адресе запрещены");
            } else {
               return uri;
            }
         }
      }
   }

   public static boolean method3276(InetAddress inetAddress) {
      if (!inetAddress.isLoopbackAddress() && !inetAddress.isSiteLocalAddress() && !inetAddress.isLinkLocalAddress() && !inetAddress.isAnyLocalAddress() && !inetAddress.isMulticastAddress()) {
         byte[] address = inetAddress.getAddress();
         if (address.length == 16) {
            boolean flag = true;

            for (int i = 0; i < 10; i++) {
               if (address[i] != 0) {
                  flag = false;
               }
            }

            if (flag && (address[10] & 255) == 255 && (address[11] & 255) == 255) {
               address = new byte[]{address[12], address[13], address[14], address[15]};
            } else {
               int n = address[0] & 255;
               if ((n & 254) == 252) {
                  return true;
               }

               if (n == 32 && (address[1] & 255) == 1 && (address[2] & 255) == 13 && (address[3] & 255) == 184) {
                  return true;
               }

               if (n != 0 || (address[1] & 255) != 100 || (address[2] & 255) != 255 || (address[3] & 255) != 155) {
                  return false;
               }

               address = new byte[]{address[12], address[13], address[14], address[15]};
            }
         }

         int n = address[0] & 255;
         int n2 = address[1] & 255;
         return n == 0
            || n == 10
            || n == 127
            || n >= 224
            || n == 100 && (n2 & 192) == 64
            || n == 169 && n2 == 254
            || n == 172 && (n2 & 240) == 16
            || n == 192 && n2 == 168
            || n == 192 && n2 == 0 && (address[2] & 255) == 0
            || n == 198 && (n2 & 254) == 18;
      } else {
         return true;
      }
   }

   private static void method3277(URI uri) throws IOException {
      String lowerCase = uri.getHost().toLowerCase(Locale.ROOT);
      if (!lowerCase.equals("localhost") && !lowerCase.endsWith(".localhost") && !lowerCase.endsWith(".local") && !lowerCase.endsWith(".internal")) {
         InetAddress[] allByName = InetAddress.getAllByName(lowerCase);
         if (allByName.length == 0) {
            throw new IOException("адрес не найден");
         } else {
            for (InetAddress inetAddress : allByName) {
               if (method3276(inetAddress)) {
                  throw new IOException("запросы в локальную сеть запрещены");
               }
            }
         }
      } else {
         throw new IOException("запросы в локальную сеть запрещены");
      }
   }

   public static void method3278(
      Script workspace, String str, String str2, byte[] bytes, Map<String, String> map, long l, BiConsumer<ScriptNetApi.Data, String> biConsumer
   ) {
      URI uri = method3275(workspace, str2);
      if (!method3274(items, workspace, 30)) {
         throw ScriptValueUtils.method6251("больше 30 запросов в минуту");
      } else {
         Thread.ofPlatform()
            .daemon(true)
            .name("zenith-script-http")
            .start(
               () -> {
                  try {
                     method3277(uri);
                     Builder var8x = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(15L)).header("User-Agent", "ZenithScript/" + workspace.getId());
                     int n = 0;

                     for (Entry entry : map.entrySet()) {
                        String lowerCase = ((String)entry.getKey()).toLowerCase(Locale.ROOT);
                        if (!lowerCase.equals("host")
                           && !lowerCase.equals("cookie")
                           && !lowerCase.startsWith("proxy")
                           && !lowerCase.equals("connection")
                           && !lowerCase.startsWith("x-forwarded")
                           && !lowerCase.equals("forwarded")
                           && !lowerCase.equals("x-real-ip")) {
                           n++;
                           if (n <= 32 && ((String)entry.getKey()).length() <= 64 && ((String)entry.getValue()).length() <= 4096) {
                              try {
                                 var8x.header((String)entry.getKey(), (String)entry.getValue());
                              } catch (IllegalArgumentException e) {
                              }
                           }
                        }
                     }

                     var8x.method(str, bytes == null ? BodyPublishers.noBody() : BodyPublishers.ofByteArray(bytes));
                     HttpResponse send = httpClient.send(var8x.build(), BodyHandlers.ofInputStream());

                     byte[] nBytes;
                     try (InputStream body = (InputStream)send.body()) {
                        nBytes = body.readNBytes((int)l + 1);
                     }

                     if (nBytes.length > l) {
                        biConsumer.accept(null, "ответ больше " + l / 1024L + " КБ");
                        return;
                     }

                     biConsumer.accept(new ScriptNetApi.Data(send.statusCode(), nBytes, send.headers().map()), null);
                  } catch (Exception e) {
                     biConsumer.accept(null, e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                  }
               }
            );
      }
   }

   public static void method3279(Script workspace, String str, long l, BiConsumer<byte[], String> biConsumer) {
      method3278(workspace, "GET", str, null, Map.of(), l, (var1x, var2x) -> {
         if (var2x != null) {
            biConsumer.accept(null, var2x);
         } else if (var1x.getIntValue() / 100 != 2) {
            biConsumer.accept(null, "сервер ответил " + var1x.getIntValue());
         } else {
            biConsumer.accept(var1x.getByteValues(), null);
         }
      });
   }

   private static Scriptable method3280(Scriptable scope, ScriptNetApi.Data data) {
      String str = new String(data.getByteValues(), StandardCharsets.UTF_8);
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      data.getItems().forEach((var1x, var2x) -> {
         if (var1x != null && !var2x.isEmpty()) {
            entry2.method6277(var1x.toLowerCase(Locale.ROOT), var2x.get(0));
         }
      });
      return ScriptValueUtils.method6247(scope)
         .method6277("status", (double)data.getIntValue())
         .method6277("ok", data.getIntValue() / 100 == 2)
         .method6277("body", str)
         .method6277("headers", entry2.getScriptableObject())
         .method6274("json", var2x -> {
            try {
               return ScriptValueUtils.method6273(scope, JsonParser.parseString(str));
            } catch (RuntimeException e) {
               throw ScriptValueUtils.method6251("ответ не JSON");
            }
         })
         .getScriptableObject();
   }

   public static Scriptable method3281(Script workspace, Storage storage, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      BiFunction<String, Object[], Object> biFunction = (var3x, var4x) -> {
         String str = ScriptValueUtils.method6254(var4x, 0, "");
         byte b = 1;
         byte[] bytes = null;
         HashMap map = new HashMap();
         if (!var3x.equals("GET") && !var3x.equals("DELETE")) {
            Object id = ScriptValueUtils.arg(var4x, 1);
            if (id instanceof Scriptable scriptable && !(id instanceof Function)) {
               bytes = ScriptValueUtils.method6271(scriptable, 0).toString().getBytes(StandardCharsets.UTF_8);
               map.put("Content-Type", "application/json");
            } else if (!ScriptValueUtils.method6253(id)) {
               bytes = ScriptValueUtils.method6255(id).getBytes(StandardCharsets.UTF_8);
            }

            b = 2;
         }

         Function function;
         if (ScriptValueUtils.arg(var4x, b) instanceof Function function2) {
            function = function2;
         } else {
            Scriptable scriptable2 = ScriptValueUtils.method6262(var4x, b);
            if (scriptable2 != null && ScriptValueUtils.method6263(scriptable2, "headers") instanceof Scriptable scriptable3) {
               for (Object id : scriptable3.getIds()) {
                  map.put(String.valueOf(id), ScriptValueUtils.method6255(scriptable3.get(String.valueOf(id), scriptable3)));
               }
            }

            function = ScriptValueUtils.method6261(var4x, b + 1);
         }

         var function3 = function;
         method3278(workspace, var3x, str, bytes, map, 2097152L, (var4xx, var5x) -> Minecraft.getInstance().execute(() -> {
            if (workspace.isRunning() && function3 != null) {
               if (var5x != null) {
                  storage.method1321(workspace, function3, null, var5x);
               } else {
                  storage.method1321(workspace, function3, method3280(scope, var4xx), null);
               }
            }
         }));
         return null;
      };
      entry2.method6274("get", var1x -> biFunction.apply("GET", var1x));
      entry2.method6274("post", var1x -> biFunction.apply("POST", var1x));
      entry2.method6274("put", var1x -> biFunction.apply("PUT", var1x));
      entry2.method6274("delete", var1x -> biFunction.apply("DELETE", var1x));
      entry2.method6274("domains", var2x -> ScriptValueUtils.method6267(scope, new ArrayList<>(workspace.method1407())));
      entry2.method6274(
         "webhook",
         var2x -> {
            String var3x = ScriptValueUtils.method6254(var2x, 0, "");
            JsonObject var4x = new JsonObject();
            var4x.addProperty("content", ScriptValueUtils.method6254(var2x, 1, ""));
            var4x.addProperty("username", ScriptValueUtils.method6254(var2x, 2, "Zenith"));
            method3278(
               workspace,
               "POST",
               var3x,
               var4x.toString().getBytes(StandardCharsets.UTF_8),
               Map.of("Content-Type", "application/json"),
               65536L,
               (var2xx, var3xx) -> {
                  if (var3xx != null) {
                     Minecraft.getInstance().execute(() -> storage.method1327(workspace, "warn", "webhook: " + var3xx));
                  }
               }
            );
            return null;
         }
      );
      return entry2.getScriptableObject();
   }

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   public static String method3282(Script workspace) {
      OriginUtil.Data2 data2 = OriginUtil.method6921(workspace);
      return data2 != null ? "m:" + data2.getUUID() : "l:" + workspace.getId();
   }

   public static Scriptable method3283(Script workspace, Storage storage, Scriptable scope) {
      ConcurrentHashMap map = new ConcurrentHashMap();
      String anObject = method3282(workspace);
      Runnable subscribe = EventManager.main()
         .subscribe(
            Event10.class,
            (byte)2,
            (var5x, var6x) -> {
               CloudPackets.Data18 data18 = ((Event10)var6x).getData18();
               if (data18.kind() == 50 && workspace.isRunning()) {
                  JsonObject jsonObject = data18.getJsonObject();
                  String string = jsonObject.has("s") ? jsonObject.get("s").getAsString() : "";
                  String key = jsonObject.has("c") ? jsonObject.get("c").getAsString() : "";
                  if (string.equals(anObject)) {
                     List<Function> functions = (List)map.get(key);
                     if (functions != null && !functions.isEmpty()) {
                        String json = jsonObject.has("d") ? jsonObject.get("d").getAsString() : "null";
                        Minecraft.getInstance()
                           .execute(
                              () -> {
                                 Object var6xx;
                                 try {
                                    var6xx = ScriptValueUtils.method6273(scope, JsonParser.parseString(json));
                                 } catch (RuntimeException var10x) {
                                    var6xx = json;
                                 }

                                 ScriptableObject var7x = ScriptValueUtils.method6247(scope)
                                    .method6277("uid", (double)data18.uid())
                                    .method6277("name", data18.username())
                                    .getScriptableObject();

                                 for (Function var9x : functions) {
                                    storage.method1321(workspace, var9x, var6xx, var7x);
                                 }
                              }
                           );
                     }
                  }
               }
            }
         );
      workspace.method1413(subscribe::run);
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("on", var2x -> {
         var var3x = ScriptValueUtils.method6260(var2x, 1);
         String var4x = ScriptValueUtils.method6254(var2x, 0, "default");
         if (var4x.length() > 64) {
            throw ScriptValueUtils.method6251("имя канала длиннее 64 символов");
         } else {
            java.util.List<Function> var5x = (java.util.List<Function>)map.computeIfAbsent(var4x, var0xx -> new CopyOnWriteArrayList());
            if (var5x.size() >= 32) {
               throw ScriptValueUtils.method6251("больше 32 обработчиков на канал");
            } else {
               var5x.add(var3x);
               return ScriptValueUtils.method6247(scope).method6274("off", var2xx -> var5x.remove(var3x)).getScriptableObject();
            }
         }
      });
      entry2.method6274("off", var1x -> {
         List var2x = (List)map.remove(ScriptValueUtils.method6254(var1x, 0, "default"));
         return var2x != null;
      });
      entry2.method6274("send", var2x -> {
         Cloud var3x = cloud();
         if (var3x == null || !var3x.method7860()) {
            return false;
         } else if (!method3274(items2, workspace, 120)) {
            throw ScriptValueUtils.method6251("больше 120 сообщений в минуту");
         } else {
            String var4x = ScriptValueUtils.method6271(ScriptValueUtils.arg(var2x, 1), 0).toString();
            if (var4x.length() > 2048) {
               throw ScriptValueUtils.method6251("сообщение больше 2048 символов");
            } else {
               JsonObject var5x = new JsonObject();
               String var6x = ScriptValueUtils.method6254(var2x, 0, "default");
               if (var6x.length() > 64) {
                  throw ScriptValueUtils.method6251("имя канала длиннее 64 символов");
               } else {
                  var5x.addProperty("s", anObject);
                  var5x.addProperty("c", var6x);
                  var5x.addProperty("d", var4x);
                  Object id = ScriptValueUtils.arg(var2x, 2);
                  if (ScriptValueUtils.method6253(id)) {
                     var5x.addProperty("to", "party");
                  } else if (id instanceof Number number) {
                     var5x.addProperty("to", String.valueOf(number.longValue()));
                  } else {
                     var5x.addProperty("to", ScriptValueUtils.method6255(id));
                  }

                  var3x.getPartyMember().method1179(var5x);
                  return true;
               }
            }
         }
      });
      entry2.method6274("online", var0x -> {
         Cloud var1x = cloud();
         return var1x != null && var1x.method7860();
      });
      entry2.method6274(
         "me",
         var1x -> ScriptValueUtils.method6247(scope).method6277("uid", (double)Cloud.accountUid()).method6277("name", Cloud.accountName()).getScriptableObject()
      );
      return entry2.getScriptableObject();
   }

   public static Scriptable method3284(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "send",
         var1x -> {
            Minecraft var2x = Minecraft.getInstance();
            if (var2x.player != null && var2x.getConnection() != null) {
               ClientPacketListener connection = var2x.getConnection();
               String lowerCase = ScriptValueUtils.method6254(var1x, 0, "").toLowerCase(Locale.ROOT);
               if (lowerCase.equals("chat") || lowerCase.equals("command")) {
                  ScriptPermissions.method4(workspace, "chat");
                  String str = ScriptValueUtils.method6254(var1x, 1, "");
                  if (str.isBlank() || str.length() > 256) {
                     return false;
                  }

                  ScriptScreenApi.method7062(workspace);
               }

               switch (lowerCase) {
                  case "swing":
                     connection.send(ServerboundPunchPacket.INSTANCE);
                     break;
                  case "slot":
                     connection.send(new ServerboundSetCarriedItemPacket(Math.max(0, Math.min(8, ScriptValueUtils.method6257(var1x, 1, 0)))));
                     break;
                  case "sprint":
                     connection.send(
                        new ServerboundPlayerCommandPacket(var2x.player, ScriptValueUtils.method6259(var1x, 1, true) ? Action.START_SPRINTING : Action.STOP_SPRINTING)
                     );
                     break;
                  case "look":
                     connection.send(
                        new Rot(
                           (float)ScriptValueUtils.method6256(var1x, 1, var2x.player.getYRot()),
                           (float)Math.max(-90.0, Math.min(90.0, ScriptValueUtils.method6256(var1x, 2, var2x.player.getXRot()))),
                           ScriptValueUtils.method6259(var1x, 3, var2x.player.onGround()),
                           var2x.player.horizontalCollision
                        )
                     );
                     break;
                  case "ground":
                     connection.send(new StatusOnly(ScriptValueUtils.method6259(var1x, 1, var2x.player.onGround()), var2x.player.horizontalCollision));
                     break;
                  case "swap_hands":
                     connection.send(new ServerboundPlayerActionPacket(net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                     break;
                  case "drop":
                     connection.send(
                        new ServerboundPlayerActionPacket(
                           ScriptValueUtils.method6259(var1x, 1, false) ? net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.DROP_ITEM, BlockPos.ZERO, Direction.DOWN
                        )
                     );
                     break;
                  case "release_use":
                     connection.send(new ServerboundPlayerActionPacket(net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
                     break;
                  case "close":
                     connection.send(new ServerboundContainerClosePacket(var2x.player.containerMenu.containerId));
                     break;
                  case "chat":
                     connection.sendChat(ScriptValueUtils.method6254(var1x, 1, ""));
                     break;
                  case "command":
                     connection.sendCommand(ScriptValueUtils.method6254(var1x, 1, "").replaceFirst("^/", ""));
                     break;
                  default:
                     throw ScriptValueUtils.method6251("packets.send: swing, slot, sprint, look, ground, swap_hands, drop, release_use, close, chat, command");
               }

               return true;
            } else {
               return false;
            }
         }
      );
      entry2.method6274(
         "names",
         var1x -> ScriptValueUtils.method6267(
            scope, List.of("swing", "slot", "sprint", "look", "ground", "swap_hands", "drop", "release_use", "close", "chat", "command")
         )
      );
      return entry2.getScriptableObject();
   }

   public static Scriptable method3285(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("list", var1x -> ScriptValueUtils.method6267(scope, new ArrayList<>(Zenith.getInstance().getFriends().getItems())));
      entry2.method6274("is", var0x -> Zenith.getInstance().getFriends().method7627(ScriptValueUtils.method6254(var0x, 0, "")));
      entry2.method6274("add", var1x -> {
         ScriptPermissions.method4(workspace, "friends");
         String var2x = ScriptValueUtils.method6254(var1x, 0, "").strip();
         if (!var2x.matches("[A-Za-z0-9_]{2,16}")) {
            throw ScriptValueUtils.method6251("ник: 2-16 символов, латиница, цифры и _");
         } else {
            Friends friends = Zenith.getInstance().getFriends();
            if (!friends.method7627(var2x)) {
               friends.add(var2x);
               friends.save();
            }

            return null;
         }
      });
      entry2.method6274("remove", var1x -> {
         ScriptPermissions.method4(workspace, "friends");
         Zenith.getInstance().getFriends().method7629(ScriptValueUtils.method6254(var1x, 0, ""));
         return null;
      });
      return entry2.getScriptableObject();
   }

   public static Scriptable method3286(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("list", var1x -> {
         ArrayList var2x = new ArrayList();
         File file = Manager.file;
         File[] listFiles = file == null ? null : file.listFiles();
         if (listFiles != null) {
            for (File listFile : listFiles) {
               String name = listFile.getName();
               int lastIndexOf = name.lastIndexOf(46);
               if (listFile.isFile() && lastIndexOf > 0) {
                  var2x.add(name.substring(0, lastIndexOf));
               }
            }
         }

         return ScriptValueUtils.method6267(scope, var2x);
      });
      entry2.method6274("load", var1x -> {
         ScriptPermissions.method4(workspace, "configs");
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         if (var2x.matches("[\\p{L}\\p{N} _.-]{1,64}") && !var2x.contains("..")) {
            return Zenith.getInstance().getManager().method7766(var2x);
         } else {
            throw ScriptValueUtils.method6251("неверное имя конфига");
         }
      });
      return entry2.getScriptableObject();
   }

   public static final class Data {
      private final int intValue;
      private final byte[] byteValues;
      private final Map<String, List<String>> items;

      public int intValue() {
         return this.intValue;
      }

      public byte[] byteValues() {
         return this.byteValues;
      }

      public Map<String, List<String>> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.byteValues, other.byteValues) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, byteValues, items);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "byteValues=" + this.byteValues + ", " + "items=" + this.items + "]";
      }

      public Data(int intValue, byte[] byteValues, Map<String, List<String>> items) {
         this.intValue = intValue;
         this.byteValues = byteValues;
         this.items = items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }

      public Map<String, List<String>> getItems() {
         return this.items;
      }
   }
}
