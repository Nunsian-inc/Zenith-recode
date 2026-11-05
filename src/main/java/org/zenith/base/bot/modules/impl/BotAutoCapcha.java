package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
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
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.client.events.PacketEvent;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.config.CaptchaLogger;
import org.zenith.utility.mixin.accessors.ItemFrameEntityAccessor;
import org.zenith.utility.network.Cloud;

@ModuleInfo(
   name = "BotAutoCapcha",
   category = ModuleCategory.MISC,
   description = "module.botautocapcha.description"
)
public final class BotAutoCapcha extends BotModule {
   private static final int MAP_SIZE = 128;
   private static final int API_WIDTH = 250;
   private static final int API_HEIGHT = 150;
   private static final int MAX_RETRIES = 3;
   private static final long POST_JOIN_WINDOW_MS = 15000L;
   private static final long KEYWORD_WINDOW_MS = 10000L;
   private static final long SETTLE_MS = 700L;
   private static final long WALL_WAIT_MS = 5000L;
   private static final long COOLDOWN_MS = 6000L;
   private static final int MAX_SPARE_BUFFERS = 128;
   private static final String[] KEYWORDS = new String[]{
      "капч", "captcha", "введите код", "введи код", "код с карты", "введите номер", "номер с картин", "с картинки", "картинке", "введите число"
   };
   private static final String[] SUCCESS_MARKERS = new String[]{"успеш", "passed", "verified"};
   private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(task -> {
      Thread thread = new Thread(task, "bot-auto-capcha");
      thread.setDaemon(true);
      return thread;
   });
   private final Map<Integer, byte[]> buffers = new ConcurrentHashMap<>();
   private final LinkedHashSet<Integer> bufferOrder = new LinkedHashSet<>();
   private final Map<Integer, double[]> frames = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> frameItems = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> frameRotations = new ConcurrentHashMap<>();
   private final AtomicBoolean pending = new AtomicBoolean();
   private final AtomicBoolean solving = new AtomicBoolean();
   private final AtomicLong generation = new AtomicLong();
   private final Object bufferLock = new Object();
   private volatile double[] look;
   private volatile long joinTime;
   private volatile long keywordTime;
   private volatile long lastTileTime;
   private volatile long lastAttemptTime;
   private volatile int lastMapId = -1;
   private volatile int lastAttemptHash;

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
      Packet packet = event.getPacket();
      BotAutoCapcha.SolveTarget currentTarget = this.currentTarget();
      if (packet instanceof ClientboundLoginPacket || packet instanceof ClientboundRespawnPacket) {
         this.resetState(true);
         this.log("join detected, armed");
      } else if (packet instanceof ClientboundPlayerPositionPacket playerPositionPacket) {
         PositionMoveRotation change = playerPositionPacket.change();
         this.look = new double[]{change.position().x, change.position().y, change.position().z, change.yRot()};
      } else if (packet instanceof ClientboundAddEntityPacket addEntityPacket
         && (addEntityPacket.getType() == net.minecraft.world.entity.EntityTypes.ITEM_FRAME || addEntityPacket.getType() == net.minecraft.world.entity.EntityTypes.GLOW_ITEM_FRAME)) {
         this.frames.put(addEntityPacket.getId(), new double[]{addEntityPacket.getX(), addEntityPacket.getY(), addEntityPacket.getZ(), addEntityPacket.getData()});
      } else if (packet instanceof ClientboundSetEntityDataPacket setEntityDataPacket) {
         this.handleTrackedValues(setEntityDataPacket, currentTarget);
      } else if (packet instanceof ClientboundRemoveEntitiesPacket removeEntitiesPacket) {
         for (int i = 0; i < removeEntitiesPacket.entityIds().size(); i++) {
            int n = removeEntitiesPacket.entityIds().getInt(i);
            this.frames.remove(n);
            this.frameItems.remove(n);
            this.frameRotations.remove(n);
         }
      } else if (packet instanceof ClientboundMapItemDataPacket mapItemDataPacket) {
         this.handleMapUpdate(mapItemDataPacket.mapId().id(), mapItemDataPacket.colorPatch().orElse(null), currentTarget);
      } else {
         String content = packetText(packet);
         if (content != null) {
            this.handleText(content, currentTarget);
         }
      }
   }

   private void handleTrackedValues(ClientboundSetEntityDataPacket setEntityDataPacket, BotAutoCapcha.SolveTarget currentTarget) {
      boolean containsKey;
      label37: {
         var frames = this.frames;
         Integer id = setEntityDataPacket.id();
         if (frames != null) {
            if (frames.isEmpty()) {
               containsKey = false;
               break label37;
            }
         }

         containsKey = frames.containsKey(id);
      }

      if (containsKey) {
         for (DataValue packedItem : setEntityDataPacket.packedItems()) {
            if (packedItem.value() instanceof ItemStack stack) {
               MapId mapId = stack.get(DataComponents.MAP_ID);
               if (mapId != null) {
                  this.frameItems.put(setEntityDataPacket.id(), mapId.id());
                  this.lastTileTime = System.currentTimeMillis();
                  this.tryTrigger(currentTarget);
               }
            } else if (packedItem.id() == ItemFrameEntityAccessor.getRotationData().id() && packedItem.value() instanceof Integer n) {
               this.frameRotations.put(setEntityDataPacket.id(), n);
               this.lastTileTime = System.currentTimeMillis();
            }
         }
      }
   }

   private void handleMapUpdate(int lastMapId, MapPatch mapPatch, BotAutoCapcha.SolveTarget currentTarget) {
      if (mapPatch != null && mapPatch.width() > 0 && mapPatch.height() > 0) {
         byte[] mapColors = mapPatch.mapColors();
         int width = mapPatch.width();
         int height = mapPatch.height();
         if (mapColors.length >= width * height) {
            synchronized (this.bufferLock) {
               byte[] buffer = this.buffers.get(lastMapId);
               if (buffer == null) {
                  buffer = new byte[16384];
                  this.buffers.put(lastMapId, buffer);
                  this.bufferOrder.add(lastMapId);
                  this.trimBuffers(lastMapId);
               }

               for (int i = 0; i < height; i++) {
                  int n;
                  if ((n = mapPatch.startY() + i) >= 0 && n < 128) {
                     for (int j = 0; j < width; j++) {
                        int n2;
                        if ((n2 = mapPatch.startX() + j) >= 0 && n2 < 128) {
                           buffer[n2 + n * 128] = mapColors[j + i * width];
                        }
                     }
                  }
               }
            }

            this.lastMapId = lastMapId;
            this.lastTileTime = System.currentTimeMillis();
            this.tryTrigger(currentTarget);
         }
      }
   }

   private void handleText(String content, BotAutoCapcha.SolveTarget currentTarget) {
      if (!content.isBlank()) {
         String lowerCase = content.toLowerCase(Locale.ROOT);

         for (String str : KEYWORDS) {
            if (lowerCase.contains(str)) {
               this.keywordTime = System.currentTimeMillis();
               this.log("captcha prompt: " + CaptchaLogger.trim(content));
               this.tryTrigger(currentTarget);
               return;
            }
         }

         for (String str : SUCCESS_MARKERS) {
            if (lowerCase.contains(str)) {
               this.log("captcha accepted: " + CaptchaLogger.trim(content));
               this.resetState(false);
               return;
            }
         }
      }
   }

   private void tryTrigger(BotAutoCapcha.SolveTarget currentTarget) {
      if (currentTarget != null) {
         if (this.lastMapId < 0) {
            boolean empty = this.frameItems.isEmpty();

            if (empty) {
               return;
            }
         }

         long now = System.currentTimeMillis();
         int n = this.joinTime != 0L && now - this.joinTime <= 15000L ? 1 : 0;
         int n2 = this.keywordTime != 0L && now - this.keywordTime <= 10000L ? 1 : 0;
         if (n != 0 || n2 != 0) {
            if (this.pending.compareAndSet(false, true)) {
               long l = this.generation.get();
               EXECUTOR.submit(() -> {
                  label62: {
                     label63: {
                        try {
                           try {
                              while (true) {
                                 long var4x;
                                 if (l == this.generation.get() && (var4x = System.currentTimeMillis() - this.lastTileTime) < 700L) {
                                    Thread.sleep(700L - var4x);
                                 } else {
                                    if (l != this.generation.get()) {
                                       break label62;
                                    }

                                    this.awaitFullWall(l);
                                    this.launch(currentTarget, l);
                                    break label63;
                                 }
                              }
                           } catch (InterruptedException e) {
                              Thread.currentThread().interrupt();
                           }
                        } catch (Throwable e) {
                           this.pending.set(false);
                           throw e;
                        }

                        this.pending.set(false);
                        return;
                     }

                     this.pending.set(false);
                     return;
                  }

                  this.pending.set(false);
               });
            }
         }
      }
   }

   private void awaitFullWall(long l) throws InterruptedException {
      long l2 = System.currentTimeMillis() + 5000L;

      while (l == this.generation.get() && System.currentTimeMillis() < l2) {
         BotAutoCapcha.Stitched stitchCaptchaWall = this.stitchCaptchaWall();
         if (stitchCaptchaWall == null || stitchCaptchaWall.tiles() >= stitchCaptchaWall.cols() * stitchCaptchaWall.rows()) {
            return;
         }

         Thread.sleep(250L);
      }
   }

   private void launch(BotAutoCapcha.SolveTarget currentTarget, long l) {
      if (l == this.generation.get() && this.isEnabled() && this.solving.compareAndSet(false, true)) {
         BotAutoCapcha.Stitched stitchCaptchaWall = this.stitchCaptchaWall();
         BufferedImage image;
         int lastAttemptHash;
         if (stitchCaptchaWall != null) {
            image = stitchCaptchaWall.image();
            lastAttemptHash = stitchCaptchaWall.hash();
            log(currentTarget, "prepared wall " + stitchCaptchaWall.cols() + "x" + stitchCaptchaWall.rows() + " tiles=" + stitchCaptchaWall.tiles());
         } else {
            int[] cloneBuffer = this.cloneBuffer(this.lastMapId);
            if (cloneBuffer == null || isUniform(cloneBuffer)) {
               this.solving.set(false);
               return;
            }

            image = render(cloneBuffer);
            lastAttemptHash = Arrays.hashCode(cloneBuffer);
            log(currentTarget, "prepared map " + this.lastMapId);
         }

         long now = System.currentTimeMillis();
         if (lastAttemptHash == this.lastAttemptHash && now - this.lastAttemptTime < 6000L) {
            this.solving.set(false);
         } else {
            this.lastAttemptHash = lastAttemptHash;
            BufferedImage im = resizeForApi(image);
            EXECUTOR.submit(() -> this.solve(im, currentTarget, l));
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void solve(BufferedImage im, BotAutoCapcha.SolveTarget currentTarget, long l) {
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
                                 if (l != this.generation.get()) {
                                    flag = false;
                                    break label177;
                                 }

                                 if (!this.isEnabled()) {
                                    flag = false;
                                    break label177;
                                 }

                                 if (!currentTarget.isConnected()) {
                                    flag = false;
                                    break label177;
                                 }

                                 StoreApi storeApi = Zenith.getInstance().getStoreApi();
                                 if (storeApi == null || Cloud.accountUid() <= 0L) {
                                    str = "Cloud account is not ready";
                                    log(currentTarget, str);
                                    feedback(currentTarget, "облако ещё не готово для решения капчи");
                                    flag = false;
                                    break label176;
                                 }

                                 try {
                                    log(currentTarget, "Cloud captcha attempt " + n + "/3");
                                    int expectedLength = this.expectedLength(currentTarget);
                                    byte[] src = encodePng(im);
                                    String content = storeApi.method1984(src, expectedLength, expectedLength).join();
                                    if (isSafeAnswer(content)) {
                                       String content2 = content.trim();
                                       if (expectedLength > 0 && content2.length() != expectedLength) {
                                          str = "answer length " + content2.length() + " != " + expectedLength;
                                          log(currentTarget, str + ": " + CaptchaLogger.trim(content2));
                                          Thread.sleep(1000L * n);
                                          break label182;
                                       }

                                       if (this.sendAnswer(currentTarget, content2)) {
                                          log(currentTarget, "answer sent: " + content2);
                                          feedback(currentTarget, "капча решена -> " + content2);
                                          this.resetState(false);
                                          flag = false;
                                       } else {
                                          feedback(currentTarget, "не удалось отправить ответ капчи");
                                          flag = false;
                                       }
                                       break label175;
                                    }

                                    if (content != null && !content.isBlank()) {
                                       str = "Cloud returned an unsafe answer";
                                       log(currentTarget, str + ": " + CaptchaLogger.trim(content));
                                       break label161;
                                    }

                                    log(currentTarget, "Cloud returned no answer on attempt " + n);
                                 } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    flag = false;
                                    break label189;
                                 } catch (Exception e) {
                                    str = rootMessage(e);
                                    log(currentTarget, "Cloud attempt " + n + " failed: " + str);
                                    if (isFinalFailure(e)) {
                                       break label161;
                                    }
                                 }

                                 Thread.sleep(1000L * n);
                                 break label182;
                              }

                              feedback(currentTarget, "капча не решена" + (str == null ? "" : " (" + str + ")"));
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
                           this.solving.set(false);
                        }
                     }

                     this.lastAttemptTime = System.currentTimeMillis();
                     this.solving.set(false);
                     return;
                  }

                  this.lastAttemptTime = System.currentTimeMillis();
                  this.solving.set(false);
                  return;
               }

               this.lastAttemptTime = System.currentTimeMillis();
               this.solving.set(false);
               return;
            }

            this.lastAttemptTime = System.currentTimeMillis();
            this.solving.set(false);
            return;
         }

         this.lastAttemptTime = System.currentTimeMillis();
         this.solving.set(false);
         return;
      }

      this.lastAttemptTime = System.currentTimeMillis();
      this.solving.set(false);
   }

   private int expectedLength(BotAutoCapcha.SolveTarget currentTarget) {
      String address = currentTarget.bot().getAddress();
      return address.toLowerCase(Locale.ROOT).contains("funtime.su") ? 5 : 0;
   }

   private boolean sendAnswer(BotAutoCapcha.SolveTarget currentTarget, String text) {
      return currentTarget.bot().sendChat(text);
   }

   private BotAutoCapcha.Stitched stitchCaptchaWall() {
      HashMap map = new HashMap();

      for (Entry entry : this.frameItems.entrySet()) {
         double[] frame = this.frames.get(entry.getKey());
         if (frame != null) {
            boolean containsKey;
            label122: {
               var buffers = this.buffers;
               Object obj = entry.getValue();
               if (buffers != null) {
                  if (buffers.isEmpty()) {
                     containsKey = false;
                     break label122;
                  }
               }

               containsKey = buffers.containsKey(obj);
            }

            int n;
            if (containsKey && (n = (int)frame[3]) >= 2 && n <= 5) {
               double d = n >= 4 ? frame[0] : frame[2];
               long key = (long)n << 40 ^ Math.round(d * 4.0) & 1099511627775L;
               ((java.util.List)map.computeIfAbsent(key, obj -> new ArrayList<>())).add(new int[]{(Integer)entry.getKey(), (Integer)entry.getValue()});
            }
         }
      }

      List<int[]> plane = this.choosePlane(map);
      if (plane != null) {
         boolean empty;
         label113: {
            if (plane != null) {
               if (plane.isEmpty()) {
                  empty = true;
                  break label113;
               }
            }

            empty = plane.isEmpty();
         }

         if (!empty) {
            double[] frame = this.frames.get(((int[])plane.getFirst())[0]);
            if (frame == null) {
               return null;
            }

            int n;
            int n2 = (n = (int)frame[3]) >= 4 ? 1 : 0;
            double d = Double.MAX_VALUE;
            double d2 = -Double.MAX_VALUE;
            double d3 = Double.MAX_VALUE;
            double d4 = -Double.MAX_VALUE;

            for (int[] ints : plane) {
               double[] doubles = this.frames.get(ints[0]);
               if (doubles != null) {
                  double d5 = n2 != 0 ? doubles[2] : doubles[0];
                  d = Math.min(d, d5);
                  d2 = Math.max(d2, d5);
                  d3 = Math.min(d3, doubles[1]);
                  d4 = Math.max(d4, doubles[1]);
               }
            }

            int cols = (int)Math.round(d2 - d) + 1;
            int rows = (int)Math.round(d4 - d3) + 1;
            if (cols >= 1 && rows >= 1 && cols <= 8 && rows <= 8 && cols * rows >= 2) {
               BufferedImage bufferedImage = new BufferedImage(cols * 128, rows * 128, 1);
               Graphics2D graphics = bufferedImage.createGraphics();
               graphics.setColor(Color.WHITE);
               graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
               int hash = 1;
               int tiles = 0;

               for (int[] ints : plane) {
                  int[] cloneBuffer = this.cloneBuffer(ints[1]);
                  double[] doubles = this.frames.get(ints[0]);
                  if (cloneBuffer != null && doubles != null) {
                     int n3 = this.frameRotations.getOrDefault(ints[0], 0) & 3;
                     cloneBuffer = rotateClockwise(cloneBuffer, n3);
                     double d5 = n2 != 0 ? doubles[2] : doubles[0];

                     int n4 = switch (n) {
                        case 3, 4 -> (int)Math.round(d5 - d);
                        default -> (int)Math.round(d2 - d5);
                     };
                     int n5 = (int)Math.round(d4 - doubles[1]);
                     if (n4 >= 0 && n4 < cols && n5 >= 0 && n5 < rows) {
                        graphics.drawImage(render(cloneBuffer), n4 * 128, n5 * 128, null);
                        hash = ((hash * 31 + ints[1]) * 31 + Arrays.hashCode(cloneBuffer)) * 31 + n4 * 17 + n5;
                        tiles++;
                     }
                  }
               }

               graphics.dispose();
               return tiles == 0 ? null : new BotAutoCapcha.Stitched(bufferedImage, hash, cols, rows, tiles);
            }

            return null;
         }
      }

      return null;
   }

   private List<int[]> choosePlane(Map<Long, List<int[]>> map) {
      boolean empty;
      label75: {
         if (map != null) {
            if (map.isEmpty()) {
               empty = true;
               break label75;
            }
         }

         empty = map.isEmpty();
      }

      if (empty) {
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
                  double[] frame = this.frames.get(ints[0]);
                  if (frame != null) {
                     d5 += frame[0];
                     d6 += frame[2];
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
      synchronized (this.bufferLock) {
         byte[] buffer = this.buffers.get(lastMapId);
         if (buffer == null) {
            return null;
         } else {
            int[] ints = new int[buffer.length];

            for (int i = 0; i < buffer.length; i++) {
               ints[i] = buffer[i] & 255;
            }

            return ints;
         }
      }
   }

   private void trimBuffers(int lastMapId) {
      if (this.buffers.size() > 128) {
         var integers = new HashSet<>(this.frameItems.values());
         integers.add(lastMapId);
         integers.add(this.lastMapId);
         int n = 0;

         for (Integer n2 : this.bufferOrder) {
            if (!integers.contains(n2)) {
               n++;
            }
         }

         var iterator = this.bufferOrder.iterator();

         while (n > 128 && iterator.hasNext()) {
            Integer n2 = (Integer)iterator.next();
            if (!integers.contains(n2)) {
               iterator.remove();
               this.buffers.remove(n2);
               n--;
            }
         }
      }
   }

   private void resetState(boolean flag) {
      this.generation.incrementAndGet();
      synchronized (this.bufferLock) {
         this.buffers.clear();
         this.bufferOrder.clear();
      }

      this.frames.clear();
      this.frameItems.clear();
      this.frameRotations.clear();
      this.pending.set(false);
      this.solving.set(false);
      this.look = null;
      this.lastMapId = -1;
      this.lastAttemptHash = 0;
      this.lastAttemptTime = 0L;
      this.lastTileTime = System.currentTimeMillis();
      this.keywordTime = 0L;
      this.joinTime = flag ? System.currentTimeMillis() : 0L;
   }

   private BotAutoCapcha.SolveTarget currentTarget() {
      BotClient bot = this.bot();
      return bot != null && bot.isJoined() ? new BotAutoCapcha.SolveTarget(bot) : null;
   }

   private void log(String str) {
      CaptchaLogger.log("[" + this.botLabel() + "] " + str);
   }

   private static void log(BotAutoCapcha.SolveTarget currentTarget, String str) {
      CaptchaLogger.log("[" + currentTarget.label() + "] " + str);
   }

   private String botLabel() {
      BotClient bot = this.bot();
      return bot == null ? "bot" : bot.name();
   }

   private static void feedback(BotAutoCapcha.SolveTarget currentTarget, String str) {
      currentTarget.bot().execute(() -> currentTarget.bot().systemMessage("AutoCapcha: " + str));
   }

   private static String packetText(Packet<?> packet) {
      if (packet instanceof ClientboundSystemChatPacket systemChatPacket) {
         return systemChatPacket.content().getString();
      } else if (packet instanceof ClientboundDisguisedChatPacket disguisedChatPacket) {
         return disguisedChatPacket.message().getString();
      } else if (packet instanceof ClientboundPlayerChatPacket playerChatPacket) {
         return playerChatPacket.unsignedContent().map(net.minecraft.network.chat.Component::getString).orElse(playerChatPacket.body().content());
      } else if (packet instanceof ClientboundSetActionBarTextPacket setActionBarTextPacket) {
         return setActionBarTextPacket.text().getString();
      } else if (packet instanceof ClientboundSetSubtitleTextPacket setSubtitleTextPacket) {
         return setSubtitleTextPacket.text().getString();
      } else {
         return packet instanceof ClientboundSetTitleTextPacket setTitleTextPacket ? setTitleTextPacket.text().getString() : null;
      }
   }

   private static boolean isSafeAnswer(String content) {
      if (content == null) {
         return false;
      } else {
         String str = content.trim();
         return !str.isEmpty() && str.length() <= 64
            && str.codePoints().allMatch(var0x -> Character.isLetterOrDigit(var0x) || var0x == 95 || var0x == 45);
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

      if (!(throwable2 instanceof StoreApi.Entry entry)) {
         return false;
      } else {
         int intValue;
         return !((intValue = entry.getIntValue()) != 503 && (intValue < 400 || intValue >= 500 || intValue == 429));
      }
   }

   private static BufferedImage render(int[] cloneBuffer) {
      BufferedImage bufferedImage = new BufferedImage(128, 128, 1);

      for (int i = 0; i < 128; i++) {
         for (int j = 0; j < 128; j++) {
            int colorFromPackedId;
            if ((colorFromPackedId = MapColor.getColorFromPackedId(cloneBuffer[j + i * 128])) >>> 24 == 0) {
               colorFromPackedId = 16777215;
            }

            bufferedImage.setRGB(j, i, colorFromPackedId & 16777215);
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

   private static final class SolveTarget {
      private final BotClient bot;

      public BotClient bot() {
         return this.bot;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SolveTarget other)) return false;
         return java.util.Objects.equals(this.bot, other.bot);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bot);
      }

      @Override
      public String toString() {
         return "SolveTarget[" + "bot=" + this.bot + "]";
      }

      private SolveTarget(BotClient bot) {
         this.bot = bot;
      }

      private boolean isConnected() {
         return this.bot.isJoined();
      }

      private String label() {
         return this.bot.name();
      }
   }

   private static final class Stitched {
      private final BufferedImage image;
      private final int hash;
      private final int cols;
      private final int rows;
      private final int tiles;

      public BufferedImage image() {
         return this.image;
      }

      public int hash() {
         return this.hash;
      }

      public int cols() {
         return this.cols;
      }

      public int rows() {
         return this.rows;
      }

      public int tiles() {
         return this.tiles;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Stitched other)) return false;
         return java.util.Objects.equals(this.image, other.image) && java.util.Objects.equals(this.hash, other.hash) && java.util.Objects.equals(this.cols, other.cols) && java.util.Objects.equals(this.rows, other.rows) && java.util.Objects.equals(this.tiles, other.tiles);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(image, hash, cols, rows, tiles);
      }

      @Override
      public String toString() {
         return "Stitched[" + "image=" + this.image + ", " + "hash=" + this.hash + ", " + "cols=" + this.cols + ", " + "rows=" + this.rows + ", " + "tiles=" + this.tiles + "]";
      }

      private Stitched(BufferedImage image, int hash, int cols, int rows, int tiles) {
         this.image = image;
         this.hash = hash;
         this.cols = cols;
         this.rows = rows;
         this.tiles = tiles;
      }
   }
}
