package org.zenith.utility.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;

public final class PvpDetector implements MinecraftAccess {
   public static final int CONST_intValue = -2070;
   public static final int CONST_intValue2 = -1921;
   public static final int CONST_intValue3 = -2076;
   public static final int CONST_intValue4 = -1929;
   public static final int CONST_intValue5 = -60;
   public static final int CONST_intValue6 = -35;
   private static final Pattern pattern = Pattern.compile("(\\d{1,3}):(\\d{2})");
   private static final Pattern pattern2 = Pattern.compile("(\\d+):(\\d+)");
   private static final Pattern pattern3 = Pattern.compile("(\\d+)");
   private static final int CONST_intValue7 = 3;
   private static final long CONST_longValue = 600000L;
   public static final long CONST_longValue2 = 20000L;
   private static final Map<Integer, Map<BlockPos, PvpDetector.Entry>> items = new HashMap<>();
   private static List<BlockPos> items2 = Collections.emptyList();
   private static int intValue = Integer.MIN_VALUE;

   private PvpDetector() {
   }

   public static void reset() {
      items2 = Collections.emptyList();
      intValue = Integer.MIN_VALUE;
   }

   private static Map<BlockPos, PvpDetector.Entry> method2716() {
      int n = method2724();
      if (n < 0) {
         return Collections.emptyMap();
      } else {
         var map = items.get(n);
         return map == null ? Collections.emptyMap() : map;
      }
   }

   private static Map<BlockPos, PvpDetector.Entry> method2717() {
      int key = method2724();
      return key < 0 ? null : items.computeIfAbsent(key, var0x -> new HashMap<>());
   }

   public static List<BlockPos> method2718() {
      return new ArrayList<>(method2716().keySet());
   }

   public static boolean method2719(BlockPos pos) {
      return pos != null && !method2716().containsKey(pos);
   }

   public static boolean method2720(BlockPos key) {
      PvpDetector.Entry entry = key == null ? null : method2716().get(key);
      return entry == null || entry.longValue2 == 0L || entry.method2734() <= 0L;
   }

   public static long method2721(BlockPos key) {
      return method2720(key) ? 0L : method2722(key);
   }

   public static long method2722(BlockPos key) {
      PvpDetector.Entry entry = key == null ? null : method2716().get(key);
      return entry == null ? -1L : entry.method2734();
   }

   public static void method2723(BlockPos key) {
      var map = method2717();
      if (key != null && map != null) {
         map.computeIfAbsent(key.immutable(), PvpDetector.Entry::new).method2733();
      }
   }

   public static int method2724() {
      if (mc.player != null && mc.level != null) {
         try {
            return Zenith.getInstance().getServerDetector().getAnarchy();
         } catch (Exception e) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean method2725() {
      if (mc.player != null && mc.gui != null) {
         for (LerpingBossEvent event : mc.gui.hud.getBossOverlay().events.values()) {
            String lowerCase = event.getName().getString().toLowerCase(Locale.ROOT);
            if (lowerCase.contains("pvp") || lowerCase.contains("пвп") || lowerCase.contains("дуэль")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static int method2726() {
      if (mc.player != null && mc.gui != null) {
         for (LerpingBossEvent event : mc.gui.hud.getBossOverlay().events.values()) {
            String lowerCase = event.getName().getString().toLowerCase(Locale.ROOT);
            if (lowerCase.contains("pvp") || lowerCase.contains("пвп")) {
               Matcher matcher = pattern2.matcher(lowerCase);
               if (matcher.find()) {
                  try {
                     return Integer.parseInt(matcher.group(1)) * 60 + Integer.parseInt(matcher.group(2));
                  } catch (NumberFormatException e) {
                     return -1;
                  }
               }

               Matcher matcher2 = pattern3.matcher(lowerCase);
               if (matcher2.find()) {
                  try {
                     return Integer.parseInt(matcher2.group(1));
                  } catch (NumberFormatException e) {
                     return -1;
                  }
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static boolean method2727() {
      return mc.player != null && mc.level != null
         ? mc.level.dimension().identifier().toString().equals("minecraft:overworld")
            && mc.player.getX() <= -1921.0
            && mc.player.getX() >= -2070.0
            && mc.player.getZ() <= -1929.0
            && mc.player.getZ() >= -2076.0
         : false;
   }

   public static List<BlockPos> method2728() {
      if (mc.player != null && mc.level != null) {
         if (intValue == mc.player.tickCount) {
            return items2;
         } else {
            intValue = mc.player.tickCount;
            ArrayList<BlockPos> poses = new ArrayList<>();

            for (int i = 65406; i <= -121; i++) {
               for (int j = 65406; j <= -121; j++) {
                  if (mc.level.hasChunk(i, j)) {
                     LevelChunk chunk = mc.level.getChunk(i, j);
                     if (chunk != null) {
                        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                           BlockPos blockPos = blockEntity.getBlockPos();
                           if (blockPos.getY() >= -60
                              && blockPos.getY() <= -35
                              && blockPos.getX() >= 63466
                              && blockPos.getX() <= 63615
                              && blockPos.getZ() >= 63460
                              && blockPos.getZ() <= 63607) {
                              BlockEntityType type = blockEntity.getType();
                              if (type == net.minecraft.world.level.block.entity.BlockEntityTypes.CHEST || type == net.minecraft.world.level.block.entity.BlockEntityTypes.TRAPPED_CHEST) {
                                 poses.add(blockPos.immutable());
                              }
                           }
                        }
                     }
                  }
               }
            }

            items2 = poses;
            return poses;
         }
      } else {
         return Collections.emptyList();
      }
   }

   public static void update() {
      if (mc.player != null && mc.level != null) {
         var poses = method2728();
         var map = method2717();
         if (!poses.isEmpty() && map != null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
               long l = method2732(entity);
               if (l >= 0L) {
                  BlockPos pos = method2730(poses, entity.blockPosition());
                  if (pos != null) {
                     map.computeIfAbsent(pos.immutable(), PvpDetector.Entry::new).method2735(l);
                  }
               }
            }
         }
      }
   }

   public static long method2729(BlockPos blockPos) {
      if (blockPos != null && mc.level != null) {
         long l = method2722(blockPos);
         return l <= 0L ? -1L : l;
      } else {
         return -1L;
      }
   }

   private static BlockPos method2730(List<BlockPos> poses, BlockPos blockPosition) {
      for (BlockPos pos : poses) {
         if (method2731(blockPosition, pos)) {
            return pos;
         }
      }

      return null;
   }

   private static boolean method2731(BlockPos blockPosition, BlockPos pos) {
      return blockPosition.getX() == pos.getX() && blockPosition.getZ() == pos.getZ() && blockPosition.getY() >= pos.getY() && blockPosition.getY() <= pos.getY() + 3;
   }

   private static long method2732(Entity entity) {
      if (!(entity instanceof ArmorStand) && !(entity instanceof TextDisplay)) {
         return -1L;
      } else {
         String string = null;
         if (entity instanceof TextDisplay textDisplay && textDisplay.getText() != null) {
            string = textDisplay.getText().getString();
         }

         if ((string == null || string.isEmpty()) && entity.getCustomName() != null) {
            string = entity.getCustomName().getString();
         }

         if (string != null && !string.isEmpty()) {
            Matcher matcher = pattern.matcher(string);
            if (!matcher.find()) {
               return -1L;
            } else {
               try {
                  return (Integer.parseInt(matcher.group(1)) * 60L + Integer.parseInt(matcher.group(2))) * 1000L;
               } catch (NumberFormatException e) {
                  return -1L;
               }
            }
         } else {
            return -1L;
         }
      }
   }

   private static final class Entry {
      private long longValue = 0L;
      private long longValue2;

      private Entry(BlockPos pos) {
      }

      private void method2733() {
         this.longValue2 = System.currentTimeMillis();
         this.longValue = this.longValue2 + 600000L;
      }

      private long method2734() {
         return Math.max(0L, this.longValue - System.currentTimeMillis());
      }

      private void method2735(long l) {
         if (Math.abs(l / 1000L - this.method2734() / 1000L) > 5L) {
            this.longValue = System.currentTimeMillis() + l;
         }
      }
   }
}
