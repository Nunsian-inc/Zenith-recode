package org.zenith.client.scripting;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.process.ICustomGoalProcess;
import com.darkmagician6.eventapi.EventManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.utility.misc.ScriptPermissions;

public final class ScriptNotifyApi {
   private static final int CONST_intValue = 300;
   private static final Deque<Object[]> deque = new ArrayDeque<>();
   private static final Map<Script, Deque<String>> items = new WeakHashMap<>();
   private static final Map<Script, long[]> items2 = new WeakHashMap<>();
   private static final Map<Script, ScriptNotifyApi.Entry> items3 = new WeakHashMap<>();
   private static final Map<Script, List<ScriptNotifyApi.Entry2>> items4 = new WeakHashMap<>();
   private static final Map<Script, ScriptNotifyApi.Entry3> items5 = new WeakHashMap<>();
   private static long longValue;
   private static double doubleValue = 20.0;
   private static boolean flag;
   private static boolean flag2;
   private static final Pattern pattern = Pattern.compile("§.");
   private static final Pattern pattern2 = Pattern.compile(
      "(?iu)^\\[?\\s*(?:лс|pm|msg|от|from|whisper)\\s*\\]?\\s*:?\\s*([A-Za-z0-9_]{2,16})\\s*(?:->|→|»|>)\\s*(?:я|me|вам|you)?\\s*[:»]?\\s*(.*)$"
   );
   private static final Pattern pattern3 = Pattern.compile("^((?:\\[[^\\]]{1,24}\\]\\s*)+)([A-Za-z0-9_]{2,16})\\s*[:»>]+\\s*(.*)$");
   private static final Pattern pattern4 = Pattern.compile("^<([A-Za-z0-9_]{2,16})>\\s*(.*)$");
   private static final Pattern pattern5 = Pattern.compile("^(?:\\[[^\\]]{1,24}\\]\\s*)?([^\\s:»<>\\[\\]]{1,24})\\s+([A-Za-z0-9_]{2,16})\\s*[:»>]+\\s*(.*)$");
   private static final Pattern pattern6 = Pattern.compile("^([A-Za-z0-9_]{2,16})\\s*[:»>]+\\s*(.*)$");

   private ScriptNotifyApi() {
   }

   private static Minecraft method2186() {
      return Minecraft.getInstance();
   }

   private static LocalPlayer method2187() {
      return method2186().player;
   }

   private static Storage host() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStorage();
   }

   public static synchronized void method2188() {
      if (!flag2) {
         flag2 = true;
         EventManager.main().subscribe(PacketReceiveEvent.class, (byte)4, (obj, obj2) -> method2191(((PacketReceiveEvent)obj2).getPacket()));
         EventManager.main().subscribe(ChatReceiveEvent.class, (byte)4, (obj, obj2) -> {
            ChatReceiveEvent event = (ChatReceiveEvent)obj2;
            if (event.getText() != null) {
               synchronized (deque) {
                  deque.addLast(new Object[]{System.currentTimeMillis(), event.getText().getString()});

                  while (deque.size() > 300) {
                     deque.removeFirst();
                  }
               }
            }
         });
         EventManager.main().subscribe(TickEvent.class, (byte)3, (obj, obj2) -> tick());
      }
   }

   private static Object[] method2189(Script key, Function<Scriptable, Object> function) {
      Scriptable scriptable = ScriptScreenApi.method7044(key);
      if (scriptable == null) {
         return new Object[0];
      } else {
         ScriptContextFactory.method7409();

         Object[] objects;
         try {
            objects = new Object[]{function.apply(scriptable)};
         } finally {
            Context.exit();
         }

         return objects;
      }
   }

   private static void method2190(Script.Type type, Function<Scriptable, Object> function) {
      Storage storage = host();
      if (storage != null) {
         storage.method1332(type, var1x -> method2189(var1x, function));
      }
   }

   private static void method2191(Packet<?> packet) {
      if (method2186().isSameThread()) {
         if (packet instanceof ClientboundSetTimePacket) {
            long now = System.currentTimeMillis();
            if (longValue != 0L) {
               long l = now - longValue;
               if (l > 50L) {
                  double d = Mth.clamp(20000.0 / l, 0.0, 20.0);
                  doubleValue = doubleValue * 0.7 + d * 0.3;
               }
            }

            longValue = now;
         } else {
            if (packet instanceof ClientboundSetTitleTextPacket setTitleTextPacket) {
               if (!ScriptModuleApi.method322(Script.Type.TITLE)) {
                  return;
               }

               String string = setTitleTextPacket.text() == null ? "" : setTitleTextPacket.text().getString();
               method2190(
                  Script.Type.TITLE, var1x -> ScriptValueUtils.method6247(var1x).method6277("kind", "title").method6277("text", string).getScriptableObject()
               );
            } else if (packet instanceof ClientboundSetSubtitleTextPacket setSubtitleTextPacket) {
               if (!ScriptModuleApi.method322(Script.Type.TITLE)) {
                  return;
               }

               String string = setSubtitleTextPacket.text() == null ? "" : setSubtitleTextPacket.text().getString();
               method2190(
                  Script.Type.TITLE, var1x -> ScriptValueUtils.method6247(var1x).method6277("kind", "subtitle").method6277("text", string).getScriptableObject()
               );
            } else if (packet instanceof ClientboundSetActionBarTextPacket setActionBarTextPacket) {
               if (!ScriptModuleApi.method322(Script.Type.ACTIONBAR)) {
                  return;
               }

               String string = setActionBarTextPacket.text() == null ? "" : setActionBarTextPacket.text().getString();
               method2190(Script.Type.ACTIONBAR, var1x -> ScriptValueUtils.method6247(var1x).method6277("text", string).getScriptableObject());
            } else if (packet instanceof ClientboundBossEventPacket) {
               flag = true;
            } else if (packet instanceof ClientboundSoundPacket clientboundSoundPacket) {
               if (!ScriptModuleApi.method322(Script.Type.SOUND_RECEIVED)) {
                  return;
               }

               String string = clientboundSoundPacket.getSound().value().location().toString();
               method2190(
                  Script.Type.SOUND_RECEIVED,
                  var2x -> ScriptValueUtils.method6247(var2x)
                     .method6277("id", string)
                     .method6277("category", clientboundSoundPacket.getSource().getName())
                     .method6277("x", clientboundSoundPacket.getX())
                     .method6277("y", clientboundSoundPacket.getY())
                     .method6277("z", clientboundSoundPacket.getZ())
                     .method6277("volume", (double)clientboundSoundPacket.getVolume())
                     .method6277("pitch", (double)clientboundSoundPacket.getPitch())
                     .getScriptableObject()
               );
            } else if (packet instanceof ClientboundTakeItemEntityPacket takeItemEntityPacket) {
               if (!ScriptModuleApi.method322(Script.Type.ITEM_PICKUP)) {
                  return;
               }

               if (method2187() == null || method2186().level == null || takeItemEntityPacket.getPlayerId() != method2187().getId()) {
                  return;
               }

               ItemStack mainHandItem = method2186().level.getEntity(takeItemEntityPacket.getItemId()) instanceof ItemEntity itemEntity ? itemEntity.getItem().copy() : ItemStack.EMPTY;
               int amount = takeItemEntityPacket.getAmount();
               method2190(Script.Type.ITEM_PICKUP, var2x -> {
                  Scriptable var3x = (Scriptable)ScriptEntityApi.method6579(var2x, mainHandItem);
                  var3x.put("picked", var3x, (double)amount);
                  return var3x;
               });
            } else if (packet instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
               if (method2186().level == null || !ScriptModuleApi.method322(Script.Type.BLOCK_UPDATE)) {
                  return;
               }

               BlockPos pos = blockUpdatePacket.getPos();
               String string = BuiltInRegistries.BLOCK.getKey(method2186().level.getBlockState(pos).getBlock()).toString();
               String anObject = BuiltInRegistries.BLOCK.getKey(blockUpdatePacket.getBlockState().getBlock()).toString();
               if (string.equals(anObject)) {
                  return;
               }

               method2190(
                  Script.Type.BLOCK_UPDATE,
                  var3x -> ScriptValueUtils.method6247(var3x)
                     .method6277("x", (double)pos.getX())
                     .method6277("y", (double)pos.getY())
                     .method6277("z", (double)pos.getZ())
                     .method6277("id", anObject)
                     .method6277("previous", string)
                     .getScriptableObject()
               );
            } else if (packet instanceof ClientboundOpenScreenPacket openScreenPacket) {
               if (!ScriptModuleApi.method322(Script.Type.CONTAINER_OPEN)) {
                  return;
               }

               String string = BuiltInRegistries.MENU.getKey(openScreenPacket.getType()) == null
                  ? ""
                  : BuiltInRegistries.MENU.getKey(openScreenPacket.getType()).toString();
               String str = openScreenPacket.getTitle() == null ? "" : openScreenPacket.getTitle().getString();
               method2190(
                  Script.Type.CONTAINER_OPEN,
                  var3x -> ScriptValueUtils.method6247(var3x)
                     .method6277("type", string)
                     .method6277("title", str)
                     .method6277("syncId", (double)openScreenPacket.getContainerId())
                     .getScriptableObject()
               );
            }
         }
      }
   }

   private static void tick() {
      if (method2186().level == null) {
         longValue = 0L;
         doubleValue = 20.0;
      }

      if (flag) {
         flag = false;
         method2190(
            Script.Type.BOSSBAR,
            var0x -> {
               ArrayList var1x = new ArrayList();

               for (Object obj : method2186().gui.hud.getBossOverlay().events.values()) {
                  LerpingBossEvent var4x = (LerpingBossEvent)obj;
                  var1x.add(
                     ScriptValueUtils.method6247(var0x)
                        .method6277("name", var4x.getName().getString())
                        .method6277("percent", (double)var4x.getProgress())
                        .method6277("color", var4x.getColor().getName())
                        .getScriptableObject()
                  );
               }

               return ScriptValueUtils.method6247(var0x).method6277("bars", ScriptValueUtils.method6267(var0x, var1x)).getScriptableObject();
            }
         );
      }

      Storage storage = host();
      if (storage != null) {
         long now = System.currentTimeMillis();

         for (Map.Entry entry : new ArrayList<>(items.entrySet())) {
            Script key = (Script)entry.getKey();
            Deque deque = (Deque)entry.getValue();
            if (!key.isRunning()) {
               deque.clear();
            } else {
               long[] longs = items2.computeIfAbsent(key, var0x -> new long[]{0L, 1200L});
               if (!deque.isEmpty() && now >= longs[0] && method2186().getConnection() != null) {
                  String pollFirst = (String)deque.pollFirst();

                  try {
                     ScriptScreenApi.method7062(key);
                     if (pollFirst.startsWith("/")) {
                        method2186().getConnection().sendCommand(pollFirst.substring(1));
                     } else {
                        method2186().getConnection().sendChat(pollFirst);
                     }
                  } catch (RuntimeException e) {
                     deque.addFirst(pollFirst);
                  }

                  longs[0] = now + longs[1];
               }
            }
         }

         for (Map.Entry entry : new ArrayList<>(items3.entrySet())) {
            Script key = (Script)entry.getKey();
            ScriptNotifyApi.Entry entry2 = (ScriptNotifyApi.Entry)entry.getValue();
            if (!key.isRunning()) {
               items3.remove(key);
            } else {
               boolean flag2 = method2186().gui.screen() instanceof AbstractContainerScreen && !(method2186().gui.screen() instanceof InventoryScreen);
               if (flag2 && ++entry2.intValue >= 2) {
                  items3.remove(key);
                  Scriptable scope = ScriptScreenApi.method7044(key);
                  storage.method1321(key, entry2.function, method2210(scope), null);
               } else if (--entry2.ticks <= 0) {
                  items3.remove(key);
                  storage.method1321(key, entry2.function, null, "контейнер не открылся");
               }
            }
         }

         for (Map.Entry entry : new ArrayList<>(items4.entrySet())) {
            Script key = (Script)entry.getKey();
            if (!key.isRunning()) {
               items4.remove(key);
            } else {
               for (Object obj : (Iterable<?>)(Object)(new ArrayList((Collection)entry.getValue()))) {
                  ScriptNotifyApi.Entry2 entry2 = (ScriptNotifyApi.Entry2)obj;
                  entry2.method2216(storage, key);
                  if (entry2.finished) {
                     ((List)entry.getValue()).remove(entry2);
                  }
               }
            }
         }

         for (Map.Entry entry : new ArrayList<>(items5.entrySet())) {
            Script key = (Script)entry.getKey();
            if (!key.isRunning()) {
               items5.remove(key);
            } else {
               ((ScriptNotifyApi.Entry3)entry.getValue()).method2217(storage, key);
            }
         }
      }
   }

   public static void method2192(Script workspace, double d, double d2, double d3) {
      ScriptNotifyApi.Entry3 entry3 = items5.computeIfAbsent(workspace, var0x -> new ScriptNotifyApi.Entry3());
      entry3.items.clear();
      entry3.items.add(new double[]{d, d2, d3});
      entry3.index = 0;
      entry3.flag = false;
      entry3.longValue = System.currentTimeMillis();
   }

   private static boolean method2193() {
      try {
         return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable e) {
         return false;
      }
   }

   private static void method2194(double[] doubles) {
      ICustomGoalProcess customGoalProcess = BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess();
      if (Double.isNaN(doubles[1])) {
         customGoalProcess.setGoalAndPath(new GoalXZ((int)Math.floor(doubles[0]), (int)Math.floor(doubles[2])));
      } else {
         customGoalProcess.setGoalAndPath(new GoalBlock((int)Math.floor(doubles[0]), (int)Math.floor(doubles[1]), (int)Math.floor(doubles[2])));
      }
   }

   public static void method2195(Script workspace, Scriptable scope, ScriptableObject scriptableObject) {
      method2188();
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("follow", var0x -> {
         Entity var1x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var0x, 0));
         if (var1x == null) {
            throw ScriptValueUtils.method6251("pathfind.follow(entity)");
         } else {
            int var2x = var1x.getId();
            BaritoneAPI.getProvider().getPrimaryBaritone().getFollowProcess().follow(var1xx -> var1xx.getId() == var2x);
            return null;
         }
      });
      entry2.method6274("waypoints", var1x -> {
         var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 0));
         if (!var2x.isEmpty() && var2x.size() <= 256) {
            ScriptNotifyApi.Entry3 var3x = items5.computeIfAbsent(workspace, var0xx -> new ScriptNotifyApi.Entry3());
            var3x.items.clear();

            for (Object id : var2x) {
               var3x.items.add(method2196(id));
            }

            var3x.index = 0;
            Scriptable scriptable = ScriptValueUtils.method6262(var1x, 1);
            var3x.flag = scriptable != null && ScriptValueUtils.method6265(scriptable, "loop", false);
            var3x.longValue = System.currentTimeMillis();
            method2194(var3x.items.get(0));
            return (double)var3x.items.size();
         } else {
            throw ScriptValueUtils.method6251("pathfind.waypoints([[x, y, z], ...]) — от 1 до 256 точек");
         }
      });
      entry2.method6274("onArrive", var1x -> {
         items5.computeIfAbsent(workspace, var0xx -> new ScriptNotifyApi.Entry3()).function = ScriptValueUtils.method6260(var1x, 0);
         return null;
      });
      entry2.method6274(
         "route",
         var2x -> {
            ScriptNotifyApi.Entry3 var3x = items5.get(workspace);
            return var3x == null
               ? null
               : ScriptValueUtils.method6247(scope)
                  .method6277("index", (double)var3x.index)
                  .method6277("count", (double)var3x.items.size())
                  .method6277("loop", var3x.flag)
                  .getScriptableObject();
         }
      );
   }

   private static double[] method2196(Object id) {
      var list = ScriptValueUtils.method6269(id);
      if (list.size() >= 3) {
         return new double[]{Context.toNumber(list.get(0)), Context.toNumber(list.get(1)), Context.toNumber(list.get(2))};
      } else if (list.size() == 2) {
         return new double[]{Context.toNumber(list.get(0)), Double.NaN, Context.toNumber(list.get(1))};
      } else if (id instanceof Scriptable scriptable) {
         Object obj = ScriptValueUtils.method6263(scriptable, "y");
         return new double[]{
            ScriptValueUtils.method6264(scriptable, "x", 0.0),
            ScriptValueUtils.method6253(obj) ? Double.NaN : Context.toNumber(obj),
            ScriptValueUtils.method6264(scriptable, "z", 0.0)
         };
      } else {
         throw ScriptValueUtils.method6251("точка — [x, y, z], [x, z] или {x, y, z}");
      }
   }

   public static Scriptable method2197(Script workspace, Storage storage, Scriptable scope) {
      method2188();
      workspace.method1413(() -> {
         items.remove(workspace);
         items3.remove(workspace);
         items4.remove(workspace);
         ScriptNotifyApi.Entry3 var1x = items5.remove(workspace);
         if (var1x != null) {
            var1x.items.clear();
         }
      });
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "history",
         var1x -> {
            int var2x = Mth.clamp(ScriptValueUtils.method6257(var1x, 0, 50), 1, 300);
            ArrayList var3x = new ArrayList();
            synchronized (deque) {
               ArrayList<Object[]> objectses = new ArrayList<>(deque);

               for (Object[] sub : objectses.subList(Math.max(0, objectses.size() - var2x), objectses.size())) {
                  var3x.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("time", (double)((Long)sub[0]).longValue())
                        .method6277("text", sub[1])
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(scope, var3x);
         }
      );
      entry2.method6274("parse", var1x -> method2198(scope, ScriptValueUtils.method6254(var1x, 0, "")));
      entry2.method6274("send", var1x -> {
         ScriptPermissions.method4(workspace, "chat");
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         if (!var2x.isBlank() && var2x.length() <= 256) {
            Scriptable var3x = ScriptValueUtils.method6262(var1x, 1);
            var strings = items.computeIfAbsent(workspace, var0xx -> new ArrayDeque<>());
            if (strings.size() >= 50) {
               throw ScriptValueUtils.method6251("в очереди уже 50 сообщений");
            } else {
               long[] longs = items2.computeIfAbsent(workspace, var0xx -> new long[]{0L, 1200L});
               if (var3x != null) {
                  longs[1] = (long)Mth.clamp(ScriptValueUtils.method6264(var3x, "delay", 1200.0), 500.0, 60000.0);
               }

               strings.addLast(var2x);
               return (double)strings.size();
            }
         } else {
            throw ScriptValueUtils.method6251("сообщение от 1 до 256 символов");
         }
      });
      entry2.method6274("queue", var1x -> {
         var var2x = items.get(workspace);
         return (double)(var2x == null ? 0 : var2x.size());
      });
      entry2.method6274("clearQueue", var1x -> {
         var var2x = items.get(workspace);
         if (var2x != null) {
            var2x.clear();
         }

         return null;
      });
      return entry2.getScriptableObject();
   }

   public static Scriptable method2198(Scriptable scope, String input) {
      String str = pattern.matcher(input).replaceAll("").strip();
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope).method6277("raw", str);
      Matcher matcher = pattern2.matcher(str);
      if (matcher.matches()) {
         return entry2.method6277("kind", "private")
            .method6277("rank", "")
            .method6277("name", matcher.group(1))
            .method6277("message", matcher.group(2))
            .getScriptableObject();
      } else {
         matcher = pattern3.matcher(str);
         if (matcher.matches()) {
            String string = matcher.group(1).replaceAll("[\\[\\]]", " ").strip().replaceAll("\\s+", " ");
            return entry2.method6277("kind", "public")
               .method6277("rank", string)
               .method6277("name", matcher.group(2))
               .method6277("message", matcher.group(3))
               .getScriptableObject();
         } else {
            matcher = pattern4.matcher(str);
            if (matcher.matches()) {
               return entry2.method6277("kind", "public")
                  .method6277("rank", "")
                  .method6277("name", matcher.group(1))
                  .method6277("message", matcher.group(2))
                  .getScriptableObject();
            } else {
               matcher = pattern5.matcher(str);
               if (matcher.matches()) {
                  return entry2.method6277("kind", "public")
                     .method6277("rank", matcher.group(1))
                     .method6277("name", matcher.group(2))
                     .method6277("message", matcher.group(3))
                     .getScriptableObject();
               } else {
                  matcher = pattern6.matcher(str);
                  return matcher.matches()
                     ? entry2.method6277("kind", "public")
                        .method6277("rank", "")
                        .method6277("name", matcher.group(1))
                        .method6277("message", matcher.group(2))
                        .getScriptableObject()
                     : entry2.method6277("kind", "system").method6277("rank", "").method6277("name", "").method6277("message", str).getScriptableObject();
               }
            }
         }
      }
   }

   public static Scriptable method2199(Scriptable scope) {
      method2188();
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("tps", var0x -> longValue != 0L && System.currentTimeMillis() - longValue <= 5000L ? Math.round(doubleValue * 10.0) / 10.0 : 0.0);
      entry2.method6274("lagging", var0x -> longValue != 0L && System.currentTimeMillis() - longValue > 2500L);
      entry2.method6274(
         "players",
         var1x -> {
            ArrayList arrayList = new ArrayList();
            if (method2186().getConnection() != null) {
               for (PlayerInfo onlinePlayer : method2186().getConnection().getOnlinePlayers()) {
                  if (arrayList.size() >= 1000) {
                     break;
                  }

                  arrayList.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("name", onlinePlayer.getProfile().name())
                        .method6277("ping", (double)onlinePlayer.getLatency())
                        .method6277("gamemode", onlinePlayer.getGameMode() == null ? "" : onlinePlayer.getGameMode().getSerializedName())
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(scope, arrayList);
         }
      );
      entry2.method6274("online", var0x -> method2186().getConnection() == null ? 0.0 : method2186().getConnection().getOnlinePlayers().size());
      entry2.method6274(
         "brand",
         var0x -> method2186().getConnection() != null && method2186().getConnection().serverBrand() != null
            ? method2186().getConnection().serverBrand()
            : ""
      );
      entry2.method6274("address", var0x -> method2186().getCurrentServer() == null ? "" : method2186().getCurrentServer().ip);
      entry2.method6274("name", var0x -> method2186().getCurrentServer() == null ? "" : method2186().getCurrentServer().name);
      entry2.method6274(
         "motd",
         var0x -> method2186().getCurrentServer() != null && method2186().getCurrentServer().motd != null
            ? method2186().getCurrentServer().motd.getString()
            : ""
      );
      entry2.method6274(
         "version",
         var0x -> method2186().getCurrentServer() != null && method2186().getCurrentServer().version != null
            ? method2186().getCurrentServer().version.getString()
            : ""
      );
      entry2.method6274("singleplayer", var0x -> method2186().isLocalServer());
      return entry2.getScriptableObject();
   }

   public static void method2200(Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("chunks", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method2186().level != null && method2187() != null) {
            int n = Mth.clamp(ScriptValueUtils.method6257(var1x, 0, 8), 1, 32);
            int n2 = method2187().getBlockX() >> 4;
            int n3 = method2187().getBlockZ() >> 4;

            for (int i = n2 - n; i <= n2 + n; i++) {
               for (int j = n3 - n; j <= n3 + n; j++) {
                  if (method2186().level.getChunkSource().hasChunk(i, j)) {
                     var2x.add(ScriptValueUtils.method6247(scope).method6277("x", (double)i).method6277("z", (double)j).getScriptableObject());
                  }
               }
            }

            return ScriptValueUtils.method6267(scope, var2x);
         } else {
            return ScriptValueUtils.method6267(scope, var2x);
         }
      });
      entry2.method6274(
         "isLoaded",
         var0x -> method2186().level != null
            && method2186()
               .level
               .getChunkSource()
               .hasChunk(
                  Mth.floor(ScriptValueUtils.method6256(var0x, 0, 0.0)) >> 4, Mth.floor(ScriptValueUtils.method6256(var0x, 1, 0.0)) >> 4
               )
      );
      entry2.method6274(
         "heightAt",
         var0x -> {
            if (method2186().level == null) {
               return -1.0;
            } else {
               int var1x = Mth.floor(ScriptValueUtils.method6256(var0x, 0, 0.0));
               int var2x = Mth.floor(ScriptValueUtils.method6256(var0x, 1, 0.0));
               return !method2186().level.getChunkSource().hasChunk(var1x >> 4, var2x >> 4)
                  ? -1.0
                  : (double)method2186().level.getHeight(Types.MOTION_BLOCKING, var1x, var2x);
            }
         }
      );
      entry2.method6274(
         "entitiesIn",
         var1x -> {
            ArrayList var2x = new ArrayList();
            if (method2186().level == null) {
               return ScriptValueUtils.method6267(scope, var2x);
            } else {
               AABB aabb = new AABB(
                  ScriptValueUtils.method6256(var1x, 0, 0.0),
                  ScriptValueUtils.method6256(var1x, 1, 0.0),
                  ScriptValueUtils.method6256(var1x, 2, 0.0),
                  ScriptValueUtils.method6256(var1x, 3, 0.0),
                  ScriptValueUtils.method6256(var1x, 4, 0.0),
                  ScriptValueUtils.method6256(var1x, 5, 0.0)
               );
               if (aabb.getXsize() * aabb.getYsize() * aabb.getZsize() > 1.6777216E7) {
                  throw ScriptValueUtils.method6251("область больше 256³ блоков");
               } else {
                  boolean flag = ScriptValueUtils.method6259(var1x, 6, false);

                  for (Entity entity : method2186().level.getEntities(method2187(), aabb, var1xx -> !flag || var1xx instanceof LivingEntity)) {
                     if (var2x.size() >= 512) {
                        break;
                     }

                     var2x.add(ScriptEntityApi.method6576(scope, entity));
                  }

                  return ScriptValueUtils.method6267(scope, var2x);
               }
            }
         }
      );
      entry2.method6274("nearest", var1x -> {
         if (method2186().level != null && method2187() != null) {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "living").toLowerCase(Locale.ROOT);
            double d = Mth.clamp(ScriptValueUtils.method6256(var1x, 1, 64.0), 1.0, 256.0);
            Entity entity = null;
            double d2 = d * d;

            for (Entity entity2 : method2186().level.entitiesForRendering()) {
               if (entity2 != method2187() && entity2.isAlive() && method2201(entity2, var2x)) {
                  double d3 = entity2.distanceToSqr(method2187());
                  if (d3 <= d2) {
                     d2 = d3;
                     entity = entity2;
                  }
               }
            }

            return entity == null ? null : ScriptEntityApi.method6576(scope, entity);
         } else {
            return null;
         }
      });
   }

   private static boolean method2201(Entity entity, String str) {
      return switch (str) {
         case "any", "" -> true;
         case "living" -> entity instanceof LivingEntity;
         case "player", "players" -> entity instanceof Player;
         case "hostile", "monster" -> entity instanceof Enemy;
         case "animal", "passive" -> entity instanceof Animal;
         case "item", "items" -> entity instanceof ItemEntity;
         default -> ScriptEntityApi.matches(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(), str);
      };
   }

   public static Scriptable method2202(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("predict", var1x -> {
         Entity entity = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (entity == null) {
            return null;
         } else {
            double d = Mth.clamp(ScriptValueUtils.method6256(var1x, 1, 1.0), 0.0, 40.0);
            Vec3 deltaMovement = entity.getDeltaMovement();
            Vec3 vec = new Vec3(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo);
            double x = Math.abs(deltaMovement.x) > Math.abs(vec.x) ? deltaMovement.x : vec.x;
            double z = Math.abs(deltaMovement.z) > Math.abs(vec.z) ? deltaMovement.z : vec.z;
            double y = entity.onGround() ? 0.0 : deltaMovement.y;
            double y2 = entity.getY();
            if (!entity.onGround()) {
               double y3 = y;

               for (int i = 0; i < d; i++) {
                  y2 += y3;
                  y3 = (y3 - 0.08) * 0.98;
               }
            }

            return ScriptValueUtils.method6270(scope, entity.getX() + x * d, y2, entity.getZ() + z * d);
         }
      });
      entry2.method6274(
         "canHit",
         var0x -> {
            Entity var1x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var0x, 0));
            if (var1x != null && method2187() != null) {
               double d = Mth.clamp(
                  ScriptValueUtils.method6256(var0x, 1, method2187().getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)), 0.0, 8.0
               );
               Vec3 eyePosition = method2187().getEyePosition();
               AABB boundingBox = var1x.getBoundingBox();
               double x = Mth.clamp(eyePosition.x, boundingBox.minX, boundingBox.maxX);
               double y = Mth.clamp(eyePosition.y, boundingBox.minY, boundingBox.maxY);
               double z = Mth.clamp(eyePosition.z, boundingBox.minZ, boundingBox.maxZ);
               return eyePosition.distanceToSqr(x, y, z) <= d * d && method2187().hasLineOfSight(var1x);
            } else {
               return false;
            }
         }
      );
      entry2.method6274(
         "armorValue",
         var1x -> ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0)) instanceof LivingEntity livingEntity
            ? ScriptValueUtils.method6247(scope)
               .method6277("armor", (double)livingEntity.getArmorValue())
               .method6277("toughness", livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS))
               .getScriptableObject()
            : null
      );
      entry2.method6274("attackDamage", var0x -> method2187() == null ? 0.0 : method2187().getAttributeValue(Attributes.ATTACK_DAMAGE));
      entry2.method6274(
         "damageCalc",
         var1x -> {
            if (ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0)) instanceof LivingEntity livingEntity && method2187() != null) {
               double string = ScriptValueUtils.method6252(var1x, 1)
                  ? ScriptValueUtils.method6256(var1x, 1, 1.0)
                  : method2187().getAttributeValue(Attributes.ATTACK_DAMAGE) * (0.2 + 0.8 * Math.pow(method2187().getAttackStrengthScale(0.5F), 2.0));
               double armorValue = livingEntity.getArmorValue();
               double attributeValue = livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
               double d = Mth.clamp(armorValue - string / (2.0 + attributeValue / 4.0), armorValue * 0.2, 20.0);
               double string2 = string * (1.0 - d / 25.0);
               double absorptionAmount = livingEntity.getAbsorptionAmount();
               return ScriptValueUtils.method6247(scope)
                  .method6277("raw", string)
                  .method6277("afterArmor", string2)
                  .method6277("kills", string2 >= livingEntity.getHealth() + absorptionAmount)
                  .method6277("hits", Math.ceil((livingEntity.getHealth() + absorptionAmount) / Math.max(0.01, string2)))
                  .getScriptableObject();
            } else {
               return null;
            }
         }
      );
      return entry2.getScriptableObject();
   }

   public static void method2203(Script workspace, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      ScriptValueUtils.Entry2 entry22 = ScriptValueUtils.method6247(scope);
      entry22.method6274("boost", var0x -> {
         if (method2187() != null && method2186().gameMode != null) {
            if (method2204(method2187().getOffhandItem())) {
               method2186().gameMode.useItem(method2187(), InteractionHand.OFF_HAND);
               return true;
            } else {
               int var1x = method2187().getInventory().getSelectedSlot();

               for (int var2x = 0; var2x < 9; var2x++) {
                  if (method2204(method2187().getInventory().getItem(var2x))) {
                     method2187().getInventory().setSelectedSlot(var2x);
                     method2186().gameMode.useItem(method2187(), InteractionHand.MAIN_HAND);
                     method2187().getInventory().setSelectedSlot(var1x);
                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      });
      entry22.method6274("equipped", var0x -> method2187() != null && method2187().getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA));
      entry22.method6274("durability", var0x -> {
         if (method2187() == null) {
            return 0.0;
         } else {
            ItemStack var1x = method2187().getItemBySlot(EquipmentSlot.CHEST);
            return var1x.is(Items.ELYTRA) && var1x.getMaxDamage() > 0 ? (double)(var1x.getMaxDamage() - var1x.getDamageValue()) : 0.0;
         }
      });
      entry22.method6274("gliding", var0x -> method2187() != null && method2187().isFallFlying());
      entry2.method6277("elytra", entry22.getScriptableObject());
      entry2.method6274("nearbyBlocks", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method2186().level != null && method2187() != null) {
            int var3x = Mth.clamp(ScriptValueUtils.method6257(var1x, 0, 4), 1, 8);
            ArrayList<String> var4x = new ArrayList<>();
            Object id = ScriptValueUtils.arg(var1x, 1);
            if (id instanceof NativeArray) {
               for (Object obj : ScriptValueUtils.method6269(id)) {
                  var4x.add(ScriptValueUtils.method6255(obj));
               }
            } else if (!ScriptValueUtils.method6253(id)) {
               var4x.add(ScriptValueUtils.method6255(id));
            }

            for (BlockPos pos : BlockPos.withinManhattan(method2187().blockPosition(), var3x)) {
               BlockState blockState = method2186().level.getBlockState(pos);
               if (!blockState.isAir()) {
                  String string = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
                  if (var4x.isEmpty() || !var4x.stream().noneMatch(var1xx -> ScriptEntityApi.matches(string, var1xx))) {
                     var2x.add(ScriptEntityApi.method6580(scope, pos.immutable()));
                     if (var2x.size() >= 2000) {
                        break;
                     }
                  }
               }
            }

            return ScriptValueUtils.method6267(scope, var2x);
         } else {
            return ScriptValueUtils.method6267(scope, var2x);
         }
      });
   }

   private static boolean method2204(ItemStack offhandItem) {
      return !offhandItem.isEmpty() && offhandItem.is(Items.FIREWORK_ROCKET);
   }

   private static int method2205(String path) {
      if (path.contains("netherite")) {
         return 7;
      } else if (path.contains("diamond")) {
         return 6;
      } else if (path.contains("iron")) {
         return 5;
      } else if (path.contains("chainmail") || path.contains("copper")) {
         return 4;
      } else if (path.contains("stone")) {
         return 3;
      } else if (path.contains("golden")) {
         return 2;
      } else {
         return !path.contains("wooden") && !path.contains("leather") ? 0 : 1;
      }
   }

   private static int method2206(ItemStack itemBySlot) {
      int n = 0;

      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry entry : itemBySlot.getEnchantments().entrySet()) {
         n += entry.getIntValue();
      }

      return n;
   }

   public static void method2207(Script workspace, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("moveTo", var0x -> {
         if (method2187() != null && method2186().gameMode != null) {
            int var1x = method2187().containerMenu.containerId;
            int var2x = ScriptScreenApi.method7053(ScriptValueUtils.method6257(var0x, 0, -1));
            int var3x = ScriptScreenApi.method7053(ScriptValueUtils.method6257(var0x, 1, -1));
            if (var2x >= 0 && var3x >= 0 && var2x != var3x) {
               method2186().gameMode.handleContainerInput(var1x, var2x, 0, ContainerInput.PICKUP, method2187());
               method2186().gameMode.handleContainerInput(var1x, var3x, 0, ContainerInput.PICKUP, method2187());
               if (!method2187().containerMenu.getCarried().isEmpty()) {
                  method2186().gameMode.handleContainerInput(var1x, var2x, 0, ContainerInput.PICKUP, method2187());
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      entry2.method6274(
         "quickMove",
         var0x -> {
            if (method2187() != null && method2186().gameMode != null) {
               method2186()
                  .gameMode
                  .handleContainerInput(
                     method2187().containerMenu.containerId,
                     ScriptScreenApi.method7053(ScriptValueUtils.method6257(var0x, 0, -1)),
                     0,
                     ContainerInput.QUICK_MOVE,
                     method2187()
                  );
               return true;
            } else {
               return false;
            }
         }
      );
      entry2.method6274("sort", var0x -> {
         if (method2187() != null && method2186().gameMode != null) {
            if (method2187().containerMenu != method2187().inventoryMenu) {
               throw ScriptValueUtils.method6251("закрой контейнер перед сортировкой");
            } else {
               int var1x = 0;

               for (int var2x = 9; var2x < 36 && var1x < 40; var2x++) {
                  int var3x = var2x;

                  for (int i = var2x + 1; i < 36; i++) {
                     if (method2208(method2187().getInventory().getItem(i), method2187().getInventory().getItem(var3x)) < 0) {
                        var3x = i;
                     }
                  }

                  if (var3x != var2x) {
                     int containerId = method2187().containerMenu.containerId;
                     method2186().gameMode.handleContainerInput(containerId, ScriptScreenApi.method7053(var3x), 0, ContainerInput.PICKUP, method2187());
                     method2186().gameMode.handleContainerInput(containerId, ScriptScreenApi.method7053(var2x), 0, ContainerInput.PICKUP, method2187());
                     method2186().gameMode.handleContainerInput(containerId, ScriptScreenApi.method7053(var3x), 0, ContainerInput.PICKUP, method2187());
                     var1x++;
                  }
               }

               return (double)var1x;
            }
         } else {
            return 0.0;
         }
      });
      entry2.method6274(
         "equipBest",
         var0x -> {
            if (method2187() != null && method2186().gameMode != null) {
               String var1x = ScriptValueUtils.method6254(var0x, 0, "sword").toLowerCase(Locale.ROOT);
               if (var1x.equals("armor")) {
                  return method2209();
               } else {
                  int var2x = -1;
                  int var3x = -1;

                  for (int i = 0; i < 36; i++) {
                     ItemStack itemBySlot = method2187().getInventory().getItem(i);
                     if (!itemBySlot.isEmpty()) {
                        String path = BuiltInRegistries.ITEM.getKey(itemBySlot.getItem()).getPath();
                        if (path.endsWith("_" + var1x) || path.equals(var1x)) {
                           int n = method2205(path) * 100 + method2206(itemBySlot);
                           if (n > var3x) {
                              var3x = n;
                              var2x = i;
                           }
                        }
                     }
                  }

                  if (var2x < 0) {
                     return false;
                  } else {
                     if (var2x < 9) {
                        method2187().getInventory().setSelectedSlot(var2x);
                     } else {
                        int selectedSlot = method2187().getInventory().getSelectedSlot();
                        method2186()
                           .gameMode
                           .handleContainerInput(method2187().containerMenu.containerId, ScriptScreenApi.method7053(var2x), selectedSlot, ContainerInput.SWAP, method2187());
                     }

                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      );
   }

   private static int method2208(ItemStack stack, ItemStack stack2) {
      if (stack.isEmpty() != stack2.isEmpty()) {
         return stack.isEmpty() ? 1 : -1;
      } else if (stack.isEmpty()) {
         return 0;
      } else {
         int n = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().compareTo(BuiltInRegistries.ITEM.getKey(stack2.getItem()).toString());
         return n != 0 ? n : Integer.compare(stack2.getCount(), stack.getCount());
      }
   }

   private static boolean method2209() {
      if (method2187().containerMenu != method2187().inventoryMenu) {
         throw ScriptValueUtils.method6251("закрой контейнер, чтобы надеть броню");
      } else {
         String[] strings = new String[]{"helmet", "chestplate", "leggings", "boots"};
         EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
         boolean flag = false;
         int containerId = method2187().containerMenu.containerId;

         for (int i = 0; i < 4; i++) {
            ItemStack itemBySlot = method2187().getItemBySlot(equipmentSlots[i]);
            int n = itemBySlot.isEmpty() ? -1 : method2205(BuiltInRegistries.ITEM.getKey(itemBySlot.getItem()).getPath()) * 100 + method2206(itemBySlot);
            int selected = -1;
            int n2 = n;

            for (int j = 0; j < 36; j++) {
               ItemStack stack = method2187().getInventory().getItem(j);
               if (!stack.isEmpty()) {
                  String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
                  if (path.endsWith("_" + strings[i])) {
                     int n3 = method2205(path) * 100 + method2206(stack);
                     if (n3 > n2) {
                        n2 = n3;
                        selected = j;
                     }
                  }
               }
            }

            if (selected >= 0) {
               int slotNum = 5 + i;
               method2186().gameMode.handleContainerInput(containerId, ScriptScreenApi.method7053(selected), 0, ContainerInput.PICKUP, method2187());
               method2186().gameMode.handleContainerInput(containerId, slotNum, 0, ContainerInput.PICKUP, method2187());
               if (!method2187().containerMenu.getCarried().isEmpty()) {
                  method2186().gameMode.handleContainerInput(containerId, ScriptScreenApi.method7053(selected), 0, ContainerInput.PICKUP, method2187());
               }

               flag = true;
            }
         }

         return flag;
      }
   }

   private static Scriptable method2210(Scriptable scope) {
      ArrayList arrayList = new ArrayList();
      if (method2187() != null && scope != null) {
         AbstractContainerMenu containerMenu = method2187().containerMenu;

         for (int i = 0; i < containerMenu.slots.size() && arrayList.size() < 128; i++) {
            Slot slot = containerMenu.slots.get(i);
            if (slot.container != method2187().getInventory()) {
               Scriptable scriptable = (Scriptable)ScriptEntityApi.method6579(scope, slot.getItem());
               scriptable.put("slot", scriptable, (double)i);
               arrayList.add(scriptable);
            }
         }
      }

      return ScriptValueUtils.method6267(scope, arrayList);
   }

   public static Scriptable method2211(Script workspace, Storage storage, Scriptable scope) {
      method2188();
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "open",
         var1x -> {
            org.mozilla.javascript.Function var2x = ScriptValueUtils.method6260(var1x, 3);
            if (method2187() == null) {
               throw ScriptValueUtils.method6251("нет игрока");
            } else {
               Object var3x = ScriptWorldApi.method4823(
                  ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
               );
               ScriptNotifyApi.Entry entry = new ScriptNotifyApi.Entry();
               entry.function = var2x;
               entry.ticks = Mth.clamp(ScriptValueUtils.method6257(var1x, 4, 40), 5, 200);
               items3.put(workspace, entry);
               return var3x;
            }
         }
      );
      entry2.method6274("isOpen", var0x -> method2186().gui.screen() instanceof AbstractContainerScreen && !(method2186().gui.screen() instanceof InventoryScreen));
      entry2.method6274("title", var0x -> method2186().gui.screen() == null ? "" : method2186().gui.screen().getTitle().getString());
      entry2.method6274("items", var1x -> method2210(scope));
      entry2.method6274("take", var0x -> {
         if (method2187() != null && method2186().gameMode != null) {
            int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
            if (var1x >= 0 && var1x < method2187().containerMenu.slots.size()) {
               method2186().gameMode.handleContainerInput(method2187().containerMenu.containerId, var1x, 0, ContainerInput.QUICK_MOVE, method2187());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      entry2.method6274(
         "takeAll",
         var0x -> {
            if (method2187() != null && method2186().gameMode != null) {
               String var1x = ScriptValueUtils.method6254(var0x, 0, "");
               AbstractContainerMenu var2x = method2187().containerMenu;
               int var3x = 0;

               for (int i = 0; i < var2x.slots.size() && var3x < 64; i++) {
                  Slot slot = var2x.slots.get(i);
                  if (slot.container != method2187().getInventory()
                     && !slot.getItem().isEmpty()
                     && (var1x.isEmpty() || ScriptEntityApi.matches(BuiltInRegistries.ITEM.getKey(slot.getItem().getItem()).toString(), var1x))) {
                     method2186().gameMode.handleContainerInput(var2x.containerId, i, 0, ContainerInput.QUICK_MOVE, method2187());
                     var3x++;
                  }
               }

               return (double)var3x;
            } else {
               return 0.0;
            }
         }
      );
      entry2.method6274("put", var0x -> {
         if (method2187() != null && method2186().gameMode != null) {
            int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
            AbstractContainerMenu var2x = method2187().containerMenu;

            for (int var3x = 0; var3x < var2x.slots.size(); var3x++) {
               Slot slot = var2x.slots.get(var3x);
               if (slot.container == method2187().getInventory() && slot.getContainerSlot() == var1x) {
                  method2186().gameMode.handleContainerInput(var2x.containerId, var3x, 0, ContainerInput.QUICK_MOVE, method2187());
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      });
      entry2.method6274("close", var0x -> {
         if (method2187() != null) {
            method2187().closeContainer();
         }

         return null;
      });
      entry2.method6274(
         "trades",
         var1x -> {
            ArrayList var2x = new ArrayList();
            if (method2187() != null && method2187().containerMenu instanceof MerchantMenu var3x) {
               int n = 0;

               for (MerchantOffer offer : var3x.getOffers()) {
                  var2x.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("index", (double)(n++))
                        .method6277("buy", ScriptEntityApi.method6579(scope, offer.getCostA()))
                        .method6277("buy2", ScriptEntityApi.method6579(scope, offer.getCostB()))
                        .method6277("sell", ScriptEntityApi.method6579(scope, offer.getResult()))
                        .method6277("uses", (double)offer.getUses())
                        .method6277("maxUses", (double)offer.getMaxUses())
                        .method6277("disabled", offer.isOutOfStock())
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(scope, var2x);
         }
      );
      entry2.method6274("selectTrade", var0x -> {
         if (method2187() != null && method2187().containerMenu instanceof MerchantMenu var1x && method2186().getConnection() != null) {
            int var3x = ScriptValueUtils.method6257(var0x, 0, 0);
            if (var3x >= 0 && var3x < var1x.getOffers().size()) {
               var1x.setSelectionHint(var3x);
               var1x.tryMoveItems(var3x);
               method2186().getConnection().send(new ServerboundSelectTradePacket(var3x));
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      entry2.method6274("buyTrade", var0x -> {
         if (method2187() != null && method2187().containerMenu instanceof MerchantMenu var1x && method2186().getConnection() != null) {
            int var3x = ScriptValueUtils.method6257(var0x, 0, 0);
            if (var3x >= 0 && var3x < var1x.getOffers().size()) {
               var1x.setSelectionHint(var3x);
               var1x.tryMoveItems(var3x);
               method2186().getConnection().send(new ServerboundSelectTradePacket(var3x));
               method2186().gameMode.handleContainerInput(var1x.containerId, 2, 0, ContainerInput.QUICK_MOVE, method2187());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      });
      return entry2.getScriptableObject();
   }

   private static int method2212(String text) {
      for (int i = 0; i < 36; i++) {
         ItemStack stack = method2187().getInventory().getItem(i);
         if (!stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(text)) {
            return i;
         }
      }

      return -1;
   }

   public static Scriptable method2213(Script workspace, Storage storage, Scriptable scope) {
      method2188();
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("load", var2x -> {
         if (workspace.getPath() == null) {
            throw ScriptValueUtils.method6251("схематики доступны только скриптам-папкам");
         } else {
            Path var3x = workspace.getPath().toAbsolutePath().normalize();
            Path path = var3x.resolve(ScriptValueUtils.method6254(var2x, 0, "")).toAbsolutePath().normalize();
            if (path.startsWith(var3x) && Files.isRegularFile(path)) {
               try {
                  if (Files.size(path) > 4194304L) {
                     throw ScriptValueUtils.method6251("схематика больше 4 МБ");
                  } else {
                     CompoundTag compressed = NbtIo.readCompressed(path, NbtAccounter.create(67108864L));
                     return method2215(scope, compressed);
                  }
               } catch (IOException e) {
                  throw ScriptValueUtils.method6251("не удалось прочитать .nbt: " + e.getMessage());
               }
            } else {
               throw ScriptValueUtils.method6251("нет файла " + ScriptValueUtils.method6254(var2x, 0, ""));
            }
         }
      });
      entry2.method6274(
         "fromList",
         var1x -> {
            var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 0));
            ArrayList var3x = new ArrayList();

            for (Object obj : var2x) {
               if (obj instanceof Scriptable scriptable) {
                  var3x.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("x", (double)((int)ScriptValueUtils.method6264(scriptable, "x", 0.0)))
                        .method6277("y", (double)((int)ScriptValueUtils.method6264(scriptable, "y", 0.0)))
                        .method6277("z", (double)((int)ScriptValueUtils.method6264(scriptable, "z", 0.0)))
                        .method6277("id", method2214(ScriptValueUtils.method6266(scriptable, "id", "stone")))
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6247(scope)
               .method6277("blocks", ScriptValueUtils.method6267(scope, var3x))
               .method6277("count", (double)var3x.size())
               .getScriptableObject();
         }
      );
      entry2.method6274(
         "build",
         var2x -> {
            Scriptable var3x = ScriptValueUtils.method6262(var2x, 0);
            if (var3x == null) {
               throw ScriptValueUtils.method6251("schematic.build(схема, x, y, z[, опции])");
            } else {
               ArrayList items = new ArrayList();

               for (Object obj : ScriptValueUtils.method6269(ScriptValueUtils.method6263(var3x, "blocks"))) {
                  if (obj instanceof Scriptable scriptable) {
                     String text = method2214(ScriptValueUtils.method6266(scriptable, "id", ""));
                     if (!text.isEmpty() && !text.endsWith(":air")) {
                        items.add(
                           new ScriptNotifyApi.Data(
                              (int)ScriptValueUtils.method6264(scriptable, "x", 0.0),
                              (int)ScriptValueUtils.method6264(scriptable, "y", 0.0),
                              (int)ScriptValueUtils.method6264(scriptable, "z", 0.0),
                              text
                           )
                        );
                     }
                  }
               }

               if (items.isEmpty()) {
                  throw ScriptValueUtils.method6251("в схеме нет блоков");
               } else {
                  items.sort(
                     Comparator.comparingInt(ScriptNotifyApi.Data::getIntValue2)
                        .thenComparingInt(ScriptNotifyApi.Data::getIntValue)
                        .thenComparingInt(ScriptNotifyApi.Data::getIntValue3)
                  );
                  ScriptNotifyApi.Entry2 entry22 = new ScriptNotifyApi.Entry2(
                     items,
                     Mth.floor(ScriptValueUtils.method6256(var2x, 1, 0.0)),
                     Mth.floor(ScriptValueUtils.method6256(var2x, 2, 0.0)),
                     Mth.floor(ScriptValueUtils.method6256(var2x, 3, 0.0))
                  );
                  Scriptable scriptable2 = ScriptValueUtils.method6262(var2x, 4);
                  entry22.intValue4 = scriptable2 == null ? 1 : (int)Mth.clamp(ScriptValueUtils.method6264(scriptable2, "perTick", 1.0), 1.0, 4.0);
                  entry22.intValue5 = scriptable2 == null ? 1 : (int)Mth.clamp(ScriptValueUtils.method6264(scriptable2, "delay", 1.0), 0.0, 100.0);
                  if (scriptable2 != null) {
                     Object obj = ScriptValueUtils.method6263(scriptable2, "onDone");
                     Object obj2 = ScriptValueUtils.method6263(scriptable2, "onProgress");
                     entry22.function = obj instanceof org.mozilla.javascript.Function function ? function : null;
                     entry22.function2 = obj2 instanceof org.mozilla.javascript.Function function2 ? function2 : null;
                  }

                  var entry2s = items4.computeIfAbsent(workspace, var0xx -> new ArrayList<>());
                  if (entry2s.size() >= 4) {
                     throw ScriptValueUtils.method6251("у скрипта уже 4 стройки");
                  } else {
                     entry2s.add(entry22);
                     return ScriptValueUtils.method6247(scope)
                        .method6274("pause", var1xx -> {
                           entry22.flag = true;
                           return null;
                        })
                        .method6274("resume", var1xx -> {
                           entry22.flag = false;
                           return null;
                        })
                        .method6274("stop", var1xx -> {
                           entry22.finished = true;
                           return null;
                        })
                        .method6274("done", var1xx -> entry22.finished)
                        .method6274(
                           "progress",
                           var3xx -> ScriptValueUtils.method6247(scope)
                              .method6277("placed", (double)entry22.index)
                              .method6277("total", (double)items.size())
                              .method6277("stuck", entry22.text)
                              .method6277("missing", ScriptValueUtils.method6267(scope, new ArrayList<>(entry22.items2)))
                              .getScriptableObject()
                        )
                        .getScriptableObject();
                  }
               }
            }
         }
      );
      entry2.method6274("stopAll", var1x -> {
         var var2x = items4.get(workspace);
         if (var2x != null) {
            var2x.forEach(var0xx -> var0xx.finished = true);
         }

         return null;
      });
      return entry2.getScriptableObject();
   }

   private static String method2214(String stringOr) {
      String lowerCase = stringOr.strip().toLowerCase(Locale.ROOT);
      int indexOf = lowerCase.indexOf(91);
      if (indexOf > 0) {
         lowerCase = lowerCase.substring(0, indexOf);
      }

      return lowerCase.isEmpty() ? "" : (lowerCase.contains(":") ? lowerCase : "minecraft:" + lowerCase);
   }

   private static Scriptable method2215(Scriptable scope, CompoundTag compressed) {
      ListTag listOrEmpty = compressed.getListOrEmpty("size");
      ListTag listTag = compressed.getListOrEmpty("palette");
      ListTag listOrEmpty2 = compressed.getListOrEmpty("blocks");
      if (listTag.isEmpty() && compressed.contains("palettes")) {
         ListTag listOrEmpty3 = compressed.getListOrEmpty("palettes");
         if (!listOrEmpty3.isEmpty() && listOrEmpty3.get(0) instanceof ListTag listTag2) {
            listTag = listTag2;
         }
      }

      ArrayList arrayList = new ArrayList();

      for (Tag tag : listTag) {
         arrayList.add(tag instanceof CompoundTag compoundTag ? method2214(compoundTag.getStringOr("Name", "minecraft:air")) : "minecraft:air");
      }

      ArrayList arrayList2 = new ArrayList();

      for (Tag tag : listOrEmpty2) {
         if (tag instanceof CompoundTag compoundTag2) {
            int intOr = compoundTag2.getIntOr("state", 0);
            if (intOr >= 0 && intOr < arrayList.size()) {
               String string = (String)arrayList.get(intOr);
               if (!string.endsWith(":air") && !string.endsWith(":structure_void")) {
                  ListTag listOrEmpty3 = compoundTag2.getListOrEmpty("pos");
                  if (listOrEmpty3.size() >= 3) {
                     arrayList2.add(
                        ScriptValueUtils.method6247(scope)
                           .method6277("x", (double)listOrEmpty3.getIntOr(0, 0))
                           .method6277("y", (double)listOrEmpty3.getIntOr(1, 0))
                           .method6277("z", (double)listOrEmpty3.getIntOr(2, 0))
                           .method6277("id", string)
                           .getScriptableObject()
                     );
                     if (arrayList2.size() >= 20000) {
                        break;
                     }
                  }
               }
            }
         }
      }

      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope)
         .method6277("x", (double)(listOrEmpty.size() > 0 ? listOrEmpty.getIntOr(0, 0) : 0))
         .method6277("y", (double)(listOrEmpty.size() > 1 ? listOrEmpty.getIntOr(1, 0) : 0))
         .method6277("z", (double)(listOrEmpty.size() > 2 ? listOrEmpty.getIntOr(2, 0) : 0));
      return ScriptValueUtils.method6247(scope)
         .method6277("size", entry2.getScriptableObject())
         .method6277("blocks", ScriptValueUtils.method6267(scope, arrayList2))
         .method6277("count", (double)arrayList2.size())
         .getScriptableObject();
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, text);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "text=" + this.text + "]";
      }

      private Data(int intValue, int n, int n2, String text) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.text = text;
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

      public String getText() {
         return this.text;
      }
   }

   private static final class Entry {
      public org.mozilla.javascript.Function function;
      public int ticks;
      public int intValue;
   }

   private static final class Entry2 {
      public final List<ScriptNotifyApi.Data> items;
      public final int intValue;
      public final int intValue2;
      public final int intValue3;
      public int index;
      public int intValue4;
      public int intValue5;
      public int intValue6;
      public boolean flag;
      public boolean finished;
      public String text = "";
      public final List<String> items2 = new ArrayList<>();
      public org.mozilla.javascript.Function function;
      public org.mozilla.javascript.Function function2;

      public Entry2(List<ScriptNotifyApi.Data> items, int intValue, int n, int n2) {
         this.items = items;
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
      }

      public void method2216(Storage storage, Script key) {
         if (!this.finished && !this.flag && ScriptNotifyApi.method2187() != null && ScriptNotifyApi.method2186().level != null) {
            if (this.intValue6 > 0) {
               this.intValue6--;
            } else {
               int n = 0;

               while (n < this.intValue4 && this.index < this.items.size()) {
                  ScriptNotifyApi.Data data = this.items.get(this.index);
                  BlockPos pos = new BlockPos(this.intValue + data.intValue, this.intValue2 + data.intValue2, this.intValue3 + data.intValue3);
                  String string = BuiltInRegistries.BLOCK.getKey(ScriptNotifyApi.method2186().level.getBlockState(pos).getBlock()).toString();
                  if (string.equals(data.text)) {
                     this.index++;
                  } else {
                     if (ScriptNotifyApi.method2187().getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > 20.25) {
                        this.text = "далеко: подойди к " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
                        return;
                     }

                     int selected = ScriptNotifyApi.method2212(data.text);
                     if (selected < 0) {
                        if (!this.items2.contains(data.text)) {
                           this.items2.add(data.text);
                        }

                        this.text = "нет блока " + data.text;
                        this.index++;
                     } else {
                        if (selected < 9) {
                           ScriptNotifyApi.method2187().getInventory().setSelectedSlot(selected);
                        } else {
                           ScriptNotifyApi.method2186()
                              .gameMode
                              .handleContainerInput(
                                 ScriptNotifyApi.method2187().containerMenu.containerId,
                                 ScriptScreenApi.method7053(selected),
                                 ScriptNotifyApi.method2187().getInventory().getSelectedSlot(),
                                 ContainerInput.SWAP,
                                 ScriptNotifyApi.method2187()
                              );
                        }

                        boolean flag = ScriptWorldApi.method4822(pos);
                        this.text = flag ? "" : "не встаёт " + data.text + " в " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " — нужна опора рядом";
                        this.index++;
                        n++;
                     }
                  }
               }

               this.intValue6 = this.intValue5;
               if (this.function2 != null && n > 0) {
                  storage.method1321(key, this.function2, (double)this.index, (double)this.items.size());
               }

               if (this.index >= this.items.size()) {
                  this.finished = true;
                  if (this.function != null) {
                     Scriptable scope = ScriptScreenApi.method7044(key);
                     if (scope != null) {
                        storage.method1321(key, this.function, ScriptValueUtils.method6267(scope, new ArrayList<>(this.items2)));
                     }
                  }
               }
            }
         }
      }
   }

   private static final class Entry3 {
      public final List<double[]> items = new ArrayList<>();
      public int index;
      public boolean flag;
      public long longValue;
      public org.mozilla.javascript.Function function;

      public void method2217(Storage storage, Script key) {
         if (!this.items.isEmpty() && ScriptNotifyApi.method2187() != null && this.index < this.items.size()) {
            if (System.currentTimeMillis() - this.longValue >= 600L) {
               double[] doubles = this.items.get(this.index);
               double d = Math.sqrt(
                  ScriptNotifyApi.method2187()
                     .distanceToSqr(doubles[0] + 0.5, Double.isNaN(doubles[1]) ? ScriptNotifyApi.method2187().getY() : doubles[1], doubles[2] + 0.5)
               );
               boolean flag = ScriptNotifyApi.method2193();
               if (!(d > 2.5) || !flag) {
                  if (d > 2.5 && !flag) {
                     this.items.clear();
                     if (this.function != null) {
                        storage.method1321(key, this.function, (double)this.index, false);
                     }
                  } else {
                     int n = this.index++;
                     if (this.index >= this.items.size() && this.flag) {
                        this.index = 0;
                     }

                     if (this.function != null) {
                        storage.method1321(key, this.function, (double)n, true);
                     }

                     if (this.index < this.items.size()) {
                        ScriptNotifyApi.method2194(this.items.get(this.index));
                        this.longValue = System.currentTimeMillis();
                     } else {
                        this.items.clear();
                     }
                  }
               }
            }
         }
      }
   }
}
