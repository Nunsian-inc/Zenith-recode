package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.MapPatch;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.CaptchaLogger;
import org.zenith.utility.mixin.accessors.ItemFrameEntityAccessor;
import org.zenith.utility.network.Cloud;

@ModuleInfo(
   name = "AutoCapcha",
   category = ModuleCategory.MISC,
   description = "module.autocapcha.description"
)
public final class AutoCapchaModule extends Module {
   public static final AutoCapchaModule INSTANCE = new AutoCapchaModule();
   private static final int CONST_intValue = 128;
   private static final int CONST_intValue2 = 250;
   private static final int CONST_intValue3 = 150;
   private static final int CONST_intValue4 = 3;
   private static final long CONST_longValue = 15000L;
   private static final long CONST_longValue2 = 10000L;
   private static final long CONST_longValue3 = 700L;
   private static final long CONST_longValue4 = 5000L;
   private static final long CONST_longValue5 = 6000L;
   private static final String[] texts = new String[]{
      "капч", "captcha", "введите код", "введи код", "код с карты", "введите номер", "номер с картин", "с картинки", "картинке", "введите число"
   };
   private static final String[] texts2 = new String[]{"успеш", "passed", "verified"};
   private static final ExecutorService executorService = Executors.newCachedThreadPool(task -> {
      Thread thread = new Thread(task, "auto-capcha");
      thread.setDaemon(true);
      return thread;
   });
   private final Map<Integer, int[]> items = new ConcurrentHashMap<>();
   private final Map<Integer, double[]> items2 = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> items3 = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> items4 = new ConcurrentHashMap<>();
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private final AtomicLong atomicLong = new AtomicLong();
   private final Object obj = new Object();
   private volatile double[] look;
   private volatile long joinTime;
   private volatile long keywordTime;
   private volatile long lastTileTime;
   private volatile long lastAttemptTime;
   private volatile int lastMapId = -1;
   private volatile int lastAttemptHash;

   private AutoCapchaModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState(true);
      this.log("enabled and armed");
   }

   @Override
   public void onDisable() {
      this.resetState(false);
      super.onDisable();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100()) {
         Packet packet3 = event.getPacket3();
         AutoCapchaModule.Data data = this.method157();
         if (packet3 instanceof ClientboundLoginPacket || packet3 instanceof ClientboundRespawnPacket) {
            this.resetState(true);
            this.log("join detected, armed");
         } else if (packet3 instanceof ClientboundPlayerPositionPacket playerPositionPacket) {
            PositionMoveRotation change = playerPositionPacket.change();
            this.look = new double[]{change.position().x, change.position().y, change.position().z, change.yRot()};
         } else if (packet3 instanceof ClientboundAddEntityPacket addEntityPacket
            && (addEntityPacket.getType() == net.minecraft.world.entity.EntityTypes.ITEM_FRAME || addEntityPacket.getType() == net.minecraft.world.entity.EntityTypes.GLOW_ITEM_FRAME)) {
            this.items2.put(addEntityPacket.getId(), new double[]{addEntityPacket.getX(), addEntityPacket.getY(), addEntityPacket.getZ(), addEntityPacket.getData()});
         } else if (packet3 instanceof ClientboundSetEntityDataPacket setEntityDataPacket) {
            this.method148(setEntityDataPacket, data);
         } else if (packet3 instanceof ClientboundMapItemDataPacket mapItemDataPacket) {
            this.method149(mapItemDataPacket.mapId().id(), mapItemDataPacket.colorPatch().orElse(null), data);
         } else {
            String content = packetText(packet3);
            if (content != null) {
               this.method150(content, data);
            }
         }
      }
   }

   private void method148(ClientboundSetEntityDataPacket setEntityDataPacket, AutoCapchaModule.Data data) {
      for (DataValue packedItem : setEntityDataPacket.packedItems()) {
         if (packedItem.value() instanceof ItemStack stack) {
            MapId mapId = stack.get(DataComponents.MAP_ID);
            if (mapId != null) {
               this.items3.put(setEntityDataPacket.id(), mapId.id());
               this.lastTileTime = System.currentTimeMillis();
               this.method151(data);
            }
         } else if (packedItem.id() == ItemFrameEntityAccessor.getRotationData().id() && packedItem.value() instanceof Integer n) {
            this.items4.put(setEntityDataPacket.id(), n);
            this.lastTileTime = System.currentTimeMillis();
         }
      }
   }

   private void method149(int lastMapId, MapPatch mapPatch, AutoCapchaModule.Data data) {
      if (mapPatch != null && mapPatch.width() > 0 && mapPatch.height() > 0) {
         byte[] mapColors = mapPatch.mapColors();
         int width = mapPatch.width();
         int height = mapPatch.height();
         if (mapColors.length >= width * height) {
            synchronized (this.obj) {
               int[] ints = this.items.computeIfAbsent(lastMapId, n -> new int[16384]);

               for (int i = 0; i < height; i++) {
                  int n;
                  if ((n = mapPatch.startY() + i) >= 0 && n < 128) {
                     for (int j = 0; j < width; j++) {
                        int n2;
                        if ((n2 = mapPatch.startX() + j) >= 0 && n2 < 128) {
                           ints[n2 + n * 128] = mapColors[j + i * width] & 255;
                        }
                     }
                  }
               }
            }

            this.lastMapId = lastMapId;
            this.lastTileTime = System.currentTimeMillis();
            this.method151(data);
         }
      }
   }

   private void method150(String content, AutoCapchaModule.Data data) {
      if (!content.isBlank()) {
         String lowerCase = content.toLowerCase(Locale.ROOT);

         for (String text : texts) {
            if (lowerCase.contains(text)) {
               this.keywordTime = System.currentTimeMillis();
               this.log("captcha prompt: " + CaptchaLogger.trim(content));
               this.method151(data);
               return;
            }
         }

         for (String str : texts2) {
            if (lowerCase.contains(str)) {
               this.log("captcha accepted: " + CaptchaLogger.trim(content));
               this.resetState(false);
               return;
            }
         }
      }
   }

   private void method151(AutoCapchaModule.Data data) {
      if (data != null && (this.lastMapId >= 0 || !this.items3.isEmpty())) {
         long now = System.currentTimeMillis();
         boolean flag = this.joinTime != 0L && now - this.joinTime <= 15000L;
         boolean flag2 = this.keywordTime != 0L && now - this.keywordTime <= 10000L;
         if (flag || flag2) {
            if (this.atomicBoolean.compareAndSet(false, true)) {
               long l = this.atomicLong.get();
               executorService.submit(() -> {
                  label62: {
                     label63: {
                        try {
                           try {
                              while (true) {
                                 long var4x;
                                 if (l == this.atomicLong.get() && (var4x = System.currentTimeMillis() - this.lastTileTime) < 700L) {
                                    Thread.sleep(700L - var4x);
                                 } else {
                                    if (l != this.atomicLong.get()) {
                                       break label62;
                                    }

                                    this.awaitFullWall(l);
                                    this.method152(data, l);
                                    break label63;
                                 }
                              }
                           } catch (InterruptedException e) {
                              Thread.currentThread().interrupt();
                           }
                        } catch (Throwable e) {
                           this.atomicBoolean.set(false);
                           throw e;
                        }

                        this.atomicBoolean.set(false);
                        return;
                     }

                     this.atomicBoolean.set(false);
                     return;
                  }

                  this.atomicBoolean.set(false);
               });
            }
         }
      }
   }

   private void awaitFullWall(long l) throws InterruptedException {
      long l2 = System.currentTimeMillis() + 5000L;

      while (l == this.atomicLong.get() && System.currentTimeMillis() < l2) {
         AutoCapchaModule.Data2 data2 = this.method156();
         if (data2 == null || data2.tiles() >= data2.cols() * data2.rows()) {
            return;
         }

         Thread.sleep(250L);
      }
   }

   private void method152(AutoCapchaModule.Data data, long l) {
      if (l == this.atomicLong.get() && this.isEnabledRaw() && this.atomicBoolean2.compareAndSet(false, true)) {
         AutoCapchaModule.Data2 data2 = this.method156();
         BufferedImage image;
         int lastAttemptHash;
         if (data2 != null) {
            image = data2.image();
            lastAttemptHash = data2.hash();
            method158(data, "prepared wall " + data2.cols() + "x" + data2.rows() + " tiles=" + data2.tiles());
         } else {
            int[] cloneBuffer = this.cloneBuffer(this.lastMapId);
            if (cloneBuffer == null || isUniform(cloneBuffer)) {
               this.atomicBoolean2.set(false);
               return;
            }

            image = render(cloneBuffer);
            lastAttemptHash = Arrays.hashCode(cloneBuffer);
            method158(data, "prepared map " + this.lastMapId);
         }

         long now = System.currentTimeMillis();
         if (lastAttemptHash == this.lastAttemptHash && now - this.lastAttemptTime < 6000L) {
            this.atomicBoolean2.set(false);
         } else {
            this.lastAttemptHash = lastAttemptHash;
            BufferedImage im = resizeForApi(image);
            executorService.submit(() -> this.method153(im, data, l));
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method153(BufferedImage im, AutoCapchaModule.Data data, long l) {
      String str = null;
      boolean flag = false /* VF: Semaphore variable */;

      label177: {
         label176: {
            label175: {
               label189: {
                  label190: {
                     try {
                        flag = true;
                        int n = 1;

                        while (true) {
                           label182: {
                              label161:
                              if (n <= 3) {
                                 if (l != this.atomicLong.get()) {
                                    flag = false;
                                    break label177;
                                 }

                                 if (!this.isEnabledRaw()) {
                                    flag = false;
                                    break label177;
                                 }

                                 if (!data.isConnected()) {
                                    flag = false;
                                    break label177;
                                 }

                                 StoreApi storeApi = Zenith.getInstance().getStoreApi();
                                 if (storeApi == null || Cloud.accountUid() <= 0L) {
                                    str = "Cloud account is not ready";
                                    method158(data, str);
                                    method159(data.label() + ": облако ещё не готово для решения капчи", true);
                                    flag = false;
                                    break label176;
                                 }

                                 try {
                                    method158(data, "Cloud captcha attempt " + n + "/3");
                                    int expectedLength = this.method154(data);
                                    byte[] src = encodePng(im);
                                    String content = storeApi.method1984(src, expectedLength, expectedLength).join();
                                    if (isSafeAnswer(content)) {
                                       String content2 = content.trim();
                                       if (expectedLength > 0 && content2.length() != expectedLength) {
                                          str = "answer length " + content2.length() + " != " + expectedLength;
                                          method158(data, str + ": " + CaptchaLogger.trim(content2));
                                          Thread.sleep(1000L * n);
                                          break label182;
                                       }

                                       if (this.method155(data, content2)) {
                                          method158(data, "answer sent: " + content2);
                                          method159(data.label() + ": капча решена -> " + content2, false);
                                          this.resetState(false);
                                          flag = false;
                                       } else {
                                          method159(data.label() + ": не удалось отправить ответ капчи", true);
                                          flag = false;
                                       }
                                       break label175;
                                    }

                                    if (content != null && !content.isBlank()) {
                                       str = "Cloud returned an unsafe answer";
                                       method158(data, str + ": " + CaptchaLogger.trim(content));
                                       break label161;
                                    }

                                    method158(data, "Cloud returned no answer on attempt " + n);
                                 } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    flag = false;
                                    break label189;
                                 } catch (Exception e) {
                                    str = rootMessage(e);
                                    method158(data, "Cloud attempt " + n + " failed: " + str);
                                    if (isFinalFailure(e)) {
                                       break label161;
                                    }
                                 }

                                 Thread.sleep(1000L * n);
                                 break label182;
                              }

                              method159(data.label() + ": капча не решена" + (str == null ? "" : " (" + str + ")"), true);
                              flag = false;
                              break label190;
                           }

                           n++;
                        }
                     } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        flag = false;
                     } finally {
                        if (flag) {
                           this.lastAttemptTime = System.currentTimeMillis();
                           this.atomicBoolean2.set(false);
                        }
                     }

                     this.lastAttemptTime = System.currentTimeMillis();
                     this.atomicBoolean2.set(false);
                     return;
                  }

                  this.lastAttemptTime = System.currentTimeMillis();
                  this.atomicBoolean2.set(false);
                  return;
               }

               this.lastAttemptTime = System.currentTimeMillis();
               this.atomicBoolean2.set(false);
               return;
            }

            this.lastAttemptTime = System.currentTimeMillis();
            this.atomicBoolean2.set(false);
            return;
         }

         this.lastAttemptTime = System.currentTimeMillis();
         this.atomicBoolean2.set(false);
         return;
      }

      this.lastAttemptTime = System.currentTimeMillis();
      this.atomicBoolean2.set(false);
   }

   private int method154(AutoCapchaModule.Data data) {
      ClientPacketListener clientPlayNetworkHandler = data.getClientPlayNetworkHandler();
      return clientPlayNetworkHandler != null && clientPlayNetworkHandler.getServerData() != null && clientPlayNetworkHandler.getServerData().ip.toLowerCase(Locale.ROOT).contains("funtime.su") ? 5 : 0;
   }

   private boolean method155(AutoCapchaModule.Data data, String content) {
      ClientPacketListener clientPlayNetworkHandler = data.getClientPlayNetworkHandler();
      if (clientPlayNetworkHandler == null) {
         return false;
      } else {
         Minecraft.getInstance().execute(() -> {
            if (content.startsWith("/")) {
               clientPlayNetworkHandler.sendCommand(content.substring(1));
            } else {
               clientPlayNetworkHandler.sendChat(content);
            }

            System.out.println(content);
         });
         return true;
      }
   }

   private AutoCapchaModule.Data2 method156() {
      HashMap map = new HashMap();

      for (Entry entry : this.items3.entrySet()) {
         double[] doubles = this.items2.get(entry.getKey());
         if (doubles != null) {
            boolean containsKey;
            label122: {
               var items = this.items;
               Object obj = entry.getValue();
               if (items != null) {
                  if (items.isEmpty()) {
                     containsKey = false;
                     break label122;
                  }
               }

               containsKey = items.containsKey(obj);
            }

            int n;
            if (containsKey && (n = (int)doubles[3]) >= 2 && n <= 5) {
               double d = n >= 4 ? doubles[0] : doubles[2];
               long key = (long)n << 40 ^ Math.round(d * 4.0) & 1099511627775L;
               ((java.util.List)map.computeIfAbsent(key, obj -> new ArrayList<>())).add(new int[]{(Integer)entry.getKey(), (Integer)entry.getValue()});
            }
         }
      }

      List<int[]> plane = this.choosePlane(map);
      if (plane != null && (!plane.isEmpty())) {
         double[] doubles = this.items2.get(((int[])plane.getFirst())[0]);
         if (doubles == null) {
            return null;
         } else {
            int n;
            boolean flag = (n = (int)doubles[3]) >= 4;
            double d = Double.MAX_VALUE;
            double d2 = -Double.MAX_VALUE;
            double d3 = Double.MAX_VALUE;
            double d4 = -Double.MAX_VALUE;

            for (int[] ints : plane) {
               double[] doubles2 = this.items2.get(ints[0]);
               if (doubles2 != null) {
                  double d5 = flag ? doubles2[2] : doubles2[0];
                  d = Math.min(d, d5);
                  d2 = Math.max(d2, d5);
                  d3 = Math.min(d3, doubles2[1]);
                  d4 = Math.max(d4, doubles2[1]);
               }
            }

            int n2 = (int)Math.round(d2 - d) + 1;
            int n3 = (int)Math.round(d4 - d3) + 1;
            if (n2 >= 1 && n3 >= 1 && n2 <= 8 && n3 <= 8 && n2 * n3 >= 2) {
               BufferedImage bufferedImage = new BufferedImage(n2 * 128, n3 * 128, 1);
               Graphics2D graphics = bufferedImage.createGraphics();
               graphics.setColor(Color.WHITE);
               graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
               int intValue = 1;
               int n4 = 0;

               for (int[] ints : plane) {
                  int[] cloneBuffer = this.cloneBuffer(ints[1]);
                  double[] doubles2 = this.items2.get(ints[0]);
                  if (cloneBuffer != null && doubles2 != null) {
                     int n5 = this.items4.getOrDefault(ints[0], 0) & 3;
                     cloneBuffer = rotateClockwise(cloneBuffer, n5);
                     double d5 = flag ? doubles2[2] : doubles2[0];

                     int n6 = switch (n) {
                        case 3, 4 -> (int)Math.round(d5 - d);
                        default -> (int)Math.round(d2 - d5);
                     };
                     int n7 = (int)Math.round(d4 - doubles2[1]);
                     if (n6 >= 0 && n6 < n2 && n7 >= 0 && n7 < n3) {
                        graphics.drawImage(render(cloneBuffer), n6 * 128, n7 * 128, null);
                        intValue = ((intValue * 31 + ints[1]) * 31 + Arrays.hashCode(cloneBuffer)) * 31 + n6 * 17 + n7;
                        n4++;
                     }
                  }
               }

               graphics.dispose();
               return n4 == 0 ? null : new AutoCapchaModule.Data2(bufferedImage, intValue, n2, n3, n4);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private List<int[]> choosePlane(Map<Long, List<int[]>> map) {
      if (map.isEmpty()) {
         return null;
      } else {
         List list = null;
         double d = -1.0;
         double[] look = this.look;
         if (look != null) {
            double d2;
            double d3 = -Math.sin(d2 = Math.toRadians(look[3]));
            double d4 = Math.cos(d2);

            for (List list2 : map.values()) {
               double d5 = 0.0;
               double d6 = 0.0;
               int n = 0;

               for (Object obj : (Iterable<?>)(Object)(list2)) {
                  int[] ints = (int[])obj;
                  double[] doubles = this.items2.get(ints[0]);
                  if (doubles != null) {
                     d5 += doubles[0];
                     d6 += doubles[2];
                     n++;
                  }
               }

               if (n != 0) {
                  d5 /= n;
                  d6 /= n;
                  double d7 = d5 - look[0];
                  double d8 = d6 - look[2];
                  double d9;
                  double d10;
                  double d11;
                  if (!((d9 = Math.sqrt(d7 * d7 + d8 * d8)) < 0.01)
                     && !(d9 > 32.0)
                     && (d11 = (d10 = d7 / d9 * d3 + d8 / d9 * d4) < 0.2 ? -1.0 : d10 * 100.0 / (1.0 + d9)) > d) {
                     d = d11;
                     list = list2;
                  }
               }
            }
         }

         if (list == null) {
            for (List list2 : map.values()) {
               if (list == null || list2.size() > list.size()) {
                  list = list2;
               }
            }
         }

         return list;
      }
   }

   private int[] cloneBuffer(int lastMapId) {
      synchronized (this.obj) {
         int[] ints = this.items.get(lastMapId);
         return ints == null ? null : (int[])ints.clone();
      }
   }

   private void resetState(boolean flag) {
      this.atomicLong.incrementAndGet();
      synchronized (this.obj) {
         this.items.clear();
      }

      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.atomicBoolean.set(false);
      this.atomicBoolean2.set(false);
      this.look = null;
      this.lastMapId = -1;
      this.lastAttemptHash = 0;
      this.lastAttemptTime = 0L;
      this.lastTileTime = System.currentTimeMillis();
      this.keywordTime = 0L;
      this.joinTime = flag ? System.currentTimeMillis() : 0L;
   }

   private AutoCapchaModule.Data method157() {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      return connection == null ? null : new AutoCapchaModule.Data(connection, "main");
   }

   private void log(String str) {
      CaptchaLogger.log("[main] " + str);
   }

   private static void method158(AutoCapchaModule.Data data, String str) {
      CaptchaLogger.log("[" + data.label() + "] " + str);
   }

   private static String packetText(Packet<?> packet3) {
      if (packet3 instanceof ClientboundSystemChatPacket systemChatPacket) {
         return systemChatPacket.content().getString();
      } else if (packet3 instanceof ClientboundDisguisedChatPacket disguisedChatPacket) {
         return disguisedChatPacket.message().getString();
      } else if (packet3 instanceof ClientboundPlayerChatPacket playerChatPacket) {
         return playerChatPacket.unsignedContent().map(net.minecraft.network.chat.Component::getString).orElse(playerChatPacket.body().content());
      } else if (packet3 instanceof ClientboundSetActionBarTextPacket setActionBarTextPacket) {
         return setActionBarTextPacket.text().getString();
      } else if (packet3 instanceof ClientboundSetSubtitleTextPacket setSubtitleTextPacket) {
         return setSubtitleTextPacket.text().getString();
      } else {
         return packet3 instanceof ClientboundSetTitleTextPacket setTitleTextPacket ? setTitleTextPacket.text().getString() : null;
      }
   }

   private static boolean isSafeAnswer(String content) {
      if (content == null) {
         return false;
      } else {
         String str = content.trim();
         return !str.isEmpty() && str.length() <= 64
            ? str.codePoints().allMatch(var0x -> Character.isLetterOrDigit(var0x) || var0x == 95 || var0x == 45)
            : false;
      }
   }

   private static int[] rotateClockwise(int[] cloneBuffer, int n) {
      int[] ints = cloneBuffer;

      for (int i = 0; i < n; i++) {
         int[] ints2 = new int[16384];

         for (int j = 0; j < 128; j++) {
            for (int k = 0; k < 128; k++) {
               ints2[k + j * 128] = ints[j + (127 - k) * 128];
            }
         }

         ints = ints2;
      }

      return ints;
   }

   private static BufferedImage resizeForApi(BufferedImage image) {
      if (image.getWidth() == 250 && image.getHeight() == 150) {
         return image;
      } else {
         BufferedImage bufferedImage = new BufferedImage(250, 150, 1);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics.drawImage(image, 0, 0, 250, 150, null);
         graphics.dispose();
         return bufferedImage;
      }
   }

   private static byte[] encodePng(BufferedImage im) throws Exception {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      if (!ImageIO.write(im, "png", byteArrayOutputStream)) {
         throw new IllegalStateException("PNG encoder is unavailable");
      } else {
         return byteArrayOutputStream.toByteArray();
      }
   }

   private static String rootMessage(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      return message != null && !message.isBlank() ? message : throwable2.getClass().getSimpleName();
   }

   private static boolean isFinalFailure(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      int intValue;
      return !(throwable2 instanceof StoreApi.Entry entry) ? false : (intValue = entry.getIntValue()) == 503 || intValue >= 400 && intValue < 500 && intValue != 429;
   }

   private static BufferedImage render(int[] cloneBuffer) {
      BufferedImage bufferedImage = new BufferedImage(128, 128, 1);

      for (int i = 0; i < 128; i++) {
         for (int j = 0; j < 128; j++) {
            int colorFromPackedId;
            if ((colorFromPackedId = MapColor.getColorFromPackedId(cloneBuffer[j + i * 128])) >>> 24 == 0) {
               colorFromPackedId = 255 << 16 | (char)-1;
            }

            bufferedImage.setRGB(j, i, colorFromPackedId & (255 << 16 | (char)-1));
         }
      }

      return bufferedImage;
   }

   private static boolean isUniform(int[] cloneBuffer) {
      int n = cloneBuffer[0];
      int[] ints = cloneBuffer;
      int length = cloneBuffer.length;

      for (int i = 0; i < length; i++) {
         int n2;
         if ((n2 = ints[i]) != n) {
            return false;
         }
      }

      return true;
   }

   private static void method159(String string, boolean flag) {
      Minecraft.getInstance().execute(() -> {
         if (flag) {
            ChatLogger.method2021(string);
         } else {
            ChatLogger.method2019(string);
         }
      });
   }

   private static final class Data {
      private final ClientPacketListener clientPlayNetworkHandler;
      private final String text;

      public ClientPacketListener clientPlayNetworkHandler() {
         return this.clientPlayNetworkHandler;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.clientPlayNetworkHandler, other.clientPlayNetworkHandler) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(clientPlayNetworkHandler, text);
      }

      @Override
      public String toString() {
         return "Data[" + "clientPlayNetworkHandler=" + this.clientPlayNetworkHandler + ", " + "text=" + this.text + "]";
      }

      private Data(ClientPacketListener clientPlayNetworkHandler, String text) {
         this.clientPlayNetworkHandler = clientPlayNetworkHandler;
         this.text = text;
      }

      private boolean isConnected() {
         return this.clientPlayNetworkHandler != null && this.clientPlayNetworkHandler.getConnection().isConnected();
      }

      public ClientPacketListener getClientPlayNetworkHandler() {
         return this.clientPlayNetworkHandler;
      }

      public String label() {
         return this.text;
      }
   }

   private static final class Data2 {
      private final BufferedImage bufferedImage;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public BufferedImage bufferedImage() {
         return this.bufferedImage;
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

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.bufferedImage, other.bufferedImage) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bufferedImage, intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data2[" + "bufferedImage=" + this.bufferedImage + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      private Data2(BufferedImage bufferedImage, int intValue, int n, int n2, int n3) {
         this.bufferedImage = bufferedImage;
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = n3;
      }

      public BufferedImage image() {
         return this.bufferedImage;
      }

      public int hash() {
         return this.intValue;
      }

      public int cols() {
         return this.intValue2;
      }

      public int rows() {
         return this.intValue3;
      }

      public int tiles() {
         return this.intValue4;
      }
   }
}
