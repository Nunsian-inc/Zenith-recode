package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.AesGcmCrypto;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "BaseFinder",
   description = "module.basefinder.description",
   category = ModuleCategory.MISC
)
public final class BaseFinderModule extends Module {
   private static final int CONST_intValue = 8192;
   private static final int CONST_intValue2 = 4096;
   private static final int CONST_intValue3 = 3072;
   private static final int CONST_intValue4 = 1536;
   public static final BaseFinderModule INSTANCE = new BaseFinderModule();
   private JsonObject jsonObject;
   private final SliderSetting range = new SliderSetting("module.baseFinder.range", 30.0F, 1.0F, 128.0F, 2.0F, "module.baseFinder.range.desc", "b");
   private final MultiSelectSetting mods = new MultiSelectSetting(
      "module.baseFinder.mods",
      "module.baseFinder.mods.desc",
      MultiSelectSetting.Entry.method5231("module.baseFinder.mods.cave", false),
      MultiSelectSetting.Entry.method5232("module.baseFinder.mods.bypass"),
      MultiSelectSetting.Entry.method5232("module.baseFinder.mods.click"),
      MultiSelectSetting.Entry.method5232("module.baseFinder.mods.packetAnalysis")
   );
   private final SliderSetting min = new SliderSetting(
      "module.baseFinder.min", 3.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.min.desc", "x", () -> this.mods.method5227(0), null
   );
   private final SliderSetting max = new SliderSetting(
      "module.baseFinder.max", 50.0F, 5.0F, 100.0F, 5.0F, "module.baseFinder.max.desc", "x", () -> this.mods.method5227(0), null
   );
   private final SliderSetting minLength = new SliderSetting(
      "module.baseFinder.minLength", 2.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.minLength.desc", "b", () -> this.mods.method5227(0), null
   );
   private final SliderSetting minWidth = new SliderSetting(
      "module.baseFinder.minWidth", 2.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.minWidth.desc", "b", () -> this.mods.method5227(0), null
   );
   private final StopWatch stopWatch = new StopWatch();
   private final Set<BlockPos> items = ConcurrentHashMap.newKeySet();
   private final Set<BlockPos> items2 = ConcurrentHashMap.newKeySet();
   private final CopyOnWriteArrayList<BaseFinderModule.Entry4> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
   private final List<BlockPos> items3 = new ArrayList<>();
   private volatile int intValue = 1;
   private volatile int intValue2 = 0;
   private volatile long longValue = 0L;
   private volatile boolean flag = false;
   private BaseFinderModule.Entry entry;
   private BaseFinderModule.Entry2 entry2;

   private BaseFinderModule() {
   }

   @Override
   public void onEnable() {
      if (mc.player != null && mc.level != null) {
         this.stopWatch.setStartTime(0L);
         this.copyOnWriteArrayList.clear();
         this.items.clear();
         this.items2.clear();
         this.items3.clear();
         this.entry = null;
         this.entry2 = null;
         this.flag = false;
         this.intValue2 = 0;
         this.intValue = 1;
         this.method4076();
         this.method4064();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onDisable() {
      this.entry = null;
      this.entry2 = null;
      this.flag = false;
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.jsonObject = null;
      super.onDisable();
   }

   @EventTarget
   public void method4060(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.stopWatch.hasElapsed(1000L)) {
            this.method4064();
            this.stopWatch.reset();
         }

         if (this.mods.method5225(1).isEnabled()) {
            this.method4065();
         } else {
            this.entry = null;
            this.items.clear();
            this.items3.clear();
         }

         if (this.mods.method5227(0)) {
            this.method4066();
         } else {
            this.method4067();
            this.items2.clear();
         }

         if (this.mods.method5227(2)) {
            for (int i = 0; i < 5 && !this.items3.isEmpty(); i++) {
               BlockPos pos = this.items3.getFirst();
               UseItemUtils.method6514(new BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos), Direction.UP, pos, false), InteractionHand.OFF_HAND);
               this.items3.removeFirst();
            }
         }
      }
   }

   @EventTarget
   public void method4061(DrawEvent event) {
      if (this.flag && this.mods.method5227(0)) {
         double d = this.intValue2 * 100.0 / Math.max(this.intValue, 1);
         long l;
         long l2;
         long l3 = (
               l2 = Math.max((long)((l = System.currentTimeMillis() - this.longValue) * ((double)this.intValue / Math.max(this.intValue2, 1))) - l, 0L)
                  / 1000L
            )
            / 60L;
         l2 %= 60L;
         String text = String.format("Поиск: %.1f%% (%d/%d) | Осталось: %02d:%02d", d, this.intValue2, this.intValue, l3, l2);
         event.getCustomDrawContext().drawText(Fonts.MEDIUM.getFont(20.0F), text, 0.0F, 0.0F, ZenithColor.INSTANCE);
      }
   }

   @EventTarget
   public void method4062(WorldRenderEvent event) {
      if (this.mods.method5227(1)) {
         this.items.forEach(var1x -> this.method4075(var1x, -1));
      }

      if (this.mods.method5227(0)) {
         this.items2.forEach(var1x -> this.method4075(var1x, Color.GREEN.getRGB()));
      }

      this.copyOnWriteArrayList.forEach(var1x -> {
         Block block = var1x.block;
         if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            this.method4075(var1x.blockPos, Color.cyan.getRGB());
         } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
            this.method4075(var1x.blockPos, -10496);
         } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            this.method4075(var1x.blockPos, -256 << 16 | (char)-179);
         } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            this.method4075(var1x.blockPos, -43 << 16 | (char)-10795);
         } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            this.method4075(var1x.blockPos, -1 << 16 | 0);
         } else if (block == Blocks.ANCIENT_DEBRIS) {
            this.method4075(var1x.blockPos, -1);
         } else if (block == Blocks.CHEST) {
            this.method4075(var1x.blockPos, Color.ORANGE.getRGB());
         } else {
            MapColor defaultMapColor = block.defaultMapColor();
            if (defaultMapColor != null) {
               this.method4075(var1x.blockPos, new Color(defaultMapColor.col).getRGB());
            }
         }
      });
   }

   @EventTarget
   public void method4063(PacketEvent event) {
      if (this.mods.method5227(3)) {
      }
   }

   private void method4064() {
      if (mc.player != null && mc.level != null) {
         BaseFinderModule.Entry5 entry5 = this.method4068();
         if (this.mods.method5225(1).isEnabled() && this.entry == null) {
            this.entry = new BaseFinderModule.Entry(entry5);
         }

         if (this.mods.method5227(0) && this.entry2 == null) {
            this.entry2 = new BaseFinderModule.Entry2(entry5);
            this.flag = true;
            this.longValue = System.currentTimeMillis();
            this.intValue2 = 0;
            this.intValue = Math.max(1, entry5.method4081());
         }
      }
   }

   private void method4065() {
      if (this.entry != null && mc.level != null) {
         int n = 0;
         int n2 = this.mods.method5227(0) ? 4096 : 8192;
         MutableBlockPos mutablePos = new MutableBlockPos();
         BaseFinderModule.Entry entry = this.entry;

         for (boolean flag = Zenith.getInstance().getServerDetector().method1642(); n < n2 && !entry.finished; n++) {
            mutablePos.set(entry.intValue, entry.intValue2, entry.intValue3);
            if (mc.level.hasChunk(entry.intValue >> 4, entry.intValue3 >> 4)) {
               BlockState blockState = mc.level.getBlockState(mutablePos);
               if (this.method4070(blockState, mutablePos, flag)) {
                  entry.items.add(mutablePos.immutable());
               }
            }

            entry.advance();
         }

         if (entry.finished) {
            var poses = this.method4069(entry.items);
            this.items.clear();
            this.items.addAll(poses);
            this.items3.clear();
            this.items3.addAll(poses);
            this.entry = null;
         }
      }
   }

   private void method4066() {
      if (this.entry2 != null && mc.level != null) {
         int n = 0;
         int n2 = this.mods.method5225(1).isEnabled() ? 1536 : 3072;
         BaseFinderModule.Entry2 entry2 = this.entry2;

         for (MutableBlockPos mutablePos = new MutableBlockPos(); n < n2 && !entry2.finished; n++) {
            mutablePos.set(entry2.intValue, entry2.intValue2, entry2.intValue3);
            this.intValue2++;
            if (mc.level.hasChunkAt(mutablePos) && !entry2.bitSet.get(entry2.entry5.method4082(entry2.intValue, entry2.intValue2, entry2.intValue3)) && mc.level.isEmptyBlock(mutablePos)
               )
             {
               BaseFinderModule.Entry3 entry3 = this.method4072(mutablePos.immutable(), entry2.bitSet, mc.level, entry2.entry5);
               if (this.method4071(entry3)) {
                  entry2.items.addAll(entry3.items);
                  if (entry3.flag) {
                     entry2.items.addAll(entry3.items2);
                  }
               }
            }

            entry2.advance();
         }

         if (entry2.finished) {
            this.items2.clear();
            this.items2.addAll(entry2.items);
            this.method4067();
         }
      }
   }

   private void method4067() {
      this.entry2 = null;
      this.flag = false;
   }

   private BaseFinderModule.Entry5 method4068() {
      int n = (int)this.range.getValue3();
      return new BaseFinderModule.Entry5(
         (int)Math.floor(mc.player.getX() - n),
         (int)Math.ceil(mc.player.getX() + n),
         mc.level.getMinY() + 1,
         mc.level.getMaxY(),
         (int)Math.floor(mc.player.getZ() - n),
         (int)Math.ceil(mc.player.getZ() + n)
      );
   }

   private Set<BlockPos> method4069(Set<BlockPos> items) {
      return !Zenith.getInstance().getServerDetector().method1642()
         ? items
         : method4077(items, 20).stream().flatMap(Collection::stream).collect(Collectors.toSet());
   }

   private boolean method4070(BlockState blockState, BlockPos mutablePos, boolean flag) {
      if (!flag) {
         Block block = blockState.getBlock();
         return block != Blocks.NETHERRACK && block != Blocks.STONE ? false : mc.level.getBrightness(LightLayer.BLOCK, mutablePos) != 0;
      } else {
         return blockState.getBlock() != Blocks.LAVA && mc.level.getBrightness(LightLayer.BLOCK, mutablePos) > 5;
      }
   }

   private boolean method4071(BaseFinderModule.Entry3 entry3) {
      if (entry3.size < this.min.getValue3() || entry3.size > this.max.getValue3()) {
         return false;
      } else if (entry3.height() < this.minLength.getValue3()) {
         return false;
      } else {
         return entry3.width() < this.minWidth.getValue3() ? false : entry3.flag || entry3.flag2;
      }
   }

   private BaseFinderModule.Entry3 method4072(BlockPos immutable, BitSet bitSet, Level level, BaseFinderModule.Entry5 entry5) {
      ArrayDeque arrayDeque = new ArrayDeque();
      BaseFinderModule.Entry3 entry3 = new BaseFinderModule.Entry3();
      arrayDeque.add(immutable);
      bitSet.set(entry5.method4083(immutable));

      while (!arrayDeque.isEmpty()) {
         BlockPos pos = (BlockPos)arrayDeque.poll();
         entry3.items.add(pos);
         entry3.size++;
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         entry3.intValue = Math.min(entry3.intValue, x);
         entry3.intValue4 = Math.max(entry3.intValue4, x);
         entry3.intValue2 = Math.min(entry3.intValue2, y);
         entry3.intValue5 = Math.max(entry3.intValue5, y);
         entry3.intValue3 = Math.min(entry3.intValue3, z);
         entry3.intValue6 = Math.max(entry3.intValue6, z);
         if (entry3.size > this.max.getValue3()) {
            break;
         }

         for (Direction direction : Direction.values()) {
            BlockPos relative = pos.relative(direction);
            if (!entry5.method4080(relative)) {
               entry3.flag = true;
               entry3.items2.add(pos);
            } else if (!level.hasChunkAt(relative)) {
               entry3.flag = true;
               entry3.items2.add(pos);
            } else {
               int bitIndex = entry5.method4083(relative);
               if (!bitSet.get(bitIndex)) {
                  if (level.isEmptyBlock(relative)) {
                     bitSet.set(bitIndex);
                     arrayDeque.add(relative);
                  } else if (this.method4074(level, relative)) {
                     entry3.flag = true;
                     entry3.items2.add(relative);
                  }
               }
            }
         }
      }

      entry3.flag2 = this.method4073(entry3.items, level);
      return entry3;
   }

   private boolean method4073(List<BlockPos> items, Level level) {
      var poses = new HashSet<>(items);

      for (BlockPos pos : items) {
         for (Direction direction : Direction.values()) {
            BlockPos relative = pos.relative(direction);
            if (!level.hasChunkAt(relative)) {
               return false;
            }

            if (!poses.contains(relative) && level.isEmptyBlock(relative)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean method4074(Level level, BlockPos relative) {
      return level.hasChunkAt(relative) && level.getBlockState(relative).canBeReplaced();
   }

   private void method4075(BlockPos blockPos, int rgb) {
      GlowUtil.method2230(new AABB(blockPos), rgb, 1.0F);
   }

   private void method4076() {
      try (InputStream resourceAsStream = Localization.class.getResourceAsStream("/assets/zenith/fonts/msdf/hold.json")) {
         if (resourceAsStream == null) {
            this.jsonObject = null;
            return;
         }

         byte[] array = Base64.getDecoder().decode(resourceAsStream.readAllBytes());
         byte[] buf = AesGcmCrypto.method5342(array, "1.21.4");

         try (InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(buf), StandardCharsets.UTF_8)) {
            this.jsonObject = (JsonObject)new Gson().fromJson(inputStreamReader, JsonObject.class);
         }
      } catch (Exception e) {
         this.jsonObject = null;
         e.printStackTrace();
      }
   }

   public static List<List<BlockPos>> method4077(Collection<BlockPos> items, int n) {
      ArrayList<List<BlockPos>> poseses = new ArrayList<>();
      var poses = new HashSet<>(items);

      while (!poses.isEmpty()) {
         BlockPos pos = (BlockPos)poses.iterator().next();
         ArrayList arrayList = new ArrayList();
         ArrayDeque arrayDeque = new ArrayDeque();
         arrayDeque.add(pos);
         poses.remove(pos);

         while (!arrayDeque.isEmpty()) {
            BlockPos pos2 = (BlockPos)arrayDeque.poll();
            arrayList.add(pos2);

            for (BlockPos pos3 : method4078(pos2)) {
               if (poses.contains(pos3)) {
                  poses.remove(pos3);
                  arrayDeque.add(pos3);
               }
            }
         }

         if (arrayList.size() <= n) {
            poseses.add(arrayList);
         }
      }

      return poseses;
   }

   private static List<BlockPos> method4078(BlockPos pos) {
      ArrayList arrayList = new ArrayList(6);

      for (Direction direction : Direction.values()) {
         arrayList.add(pos.relative(direction));
      }

      return arrayList;
   }

   public static CopyOnWriteArrayList<BaseFinderModule.Entry4> method4079(InputStream inputStream, BlockPos pos, double d) {
      CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
      double d2 = d * d;

      String line;
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
         while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split(" ");
            if (strings.length == 4) {
               try {
                  int x = Integer.parseInt(strings[0]);
                  int y = Integer.parseInt(strings[1]);
                  int z = Integer.parseInt(strings[2]);
                  double d3 = x - pos.getX();
                  double d4 = z - pos.getZ();
                  if (!(d3 * d3 + d4 * d4 > d2)) {
                     String str = new String(Base64.getDecoder().decode(strings[3]), StandardCharsets.UTF_8);
                     if (str.startsWith("block.minecraft.")) {
                        str = str.substring("block.minecraft.".length());
                     }

                     Identifier key = Identifier.fromNamespaceAndPath("minecraft", str);
                     Block block = BuiltInRegistries.BLOCK.getValue(key);
                     if (block != Blocks.AIR) {
                        copyOnWriteArrayList.add(new BaseFinderModule.Entry4(new BlockPos(x, y, z), block));
                     }
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return copyOnWriteArrayList;
   }

   private static class Entry {
      public final BaseFinderModule.Entry5 entry5;
      public final Set<BlockPos> items = new HashSet<>();
      public int intValue;
      public int intValue2;
      public int intValue3;
      public boolean finished;

      private Entry(BaseFinderModule.Entry5 entry5) {
         this.entry5 = entry5;
         this.intValue = entry5.intValue;
         this.intValue2 = entry5.intValue3;
         this.intValue3 = entry5.intValue5;
      }

      public void advance() {
         if (!this.finished) {
            if (++this.intValue3 > this.entry5.intValue6) {
               this.intValue3 = this.entry5.intValue5;
               if (++this.intValue2 > this.entry5.intValue4) {
                  this.intValue2 = this.entry5.intValue3;
                  if (++this.intValue > this.entry5.intValue2) {
                     this.finished = true;
                  }
               }
            }
         }
      }
   }

   private static final class Entry2 extends BaseFinderModule.Entry {
      private final BitSet bitSet;

      private Entry2(BaseFinderModule.Entry5 entry5) {
         super(entry5);
         this.bitSet = new BitSet(Math.max(1, entry5.method4081()));
      }
   }

   private static class Entry3 {
      public final List<BlockPos> items = new ArrayList<>();
      public final List<BlockPos> items2 = new ArrayList<>();
      public boolean flag = false;
      public boolean flag2 = true;
      public int size = 0;
      public int intValue = Integer.MAX_VALUE;
      public int intValue2 = Integer.MAX_VALUE;
      public int intValue3 = Integer.MAX_VALUE;
      public int intValue4 = Integer.MIN_VALUE;
      public int intValue5 = Integer.MIN_VALUE;
      public int intValue6 = Integer.MIN_VALUE;

      public int width() {
         return Math.max(this.intValue4 - this.intValue + 1, this.intValue6 - this.intValue3 + 1);
      }

      public int height() {
         return this.intValue5 - this.intValue2 + 1;
      }
   }

   public static class Entry4 {
      public final BlockPos blockPos;
      public final Block block;

      public Entry4(BlockPos blockPos, Block block) {
         this.blockPos = blockPos;
         this.block = block;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            return !(obj instanceof BaseFinderModule.Entry4 entry4) ? false : this.blockPos.equals(entry4.blockPos) && this.block.equals(entry4.block);
         }
      }

      @Override
      public int hashCode() {
         return this.blockPos.hashCode() * 31 + this.block.hashCode();
      }
   }

   private static final class Entry5 {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;

      private Entry5(int intValue, int n, int n2, int maxY, int n3, int n4) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = maxY;
         this.intValue5 = n3;
         this.intValue6 = n4;
      }

      private boolean method4080(BlockPos relative) {
         return relative.getX() >= this.intValue
            && relative.getX() <= this.intValue2
            && relative.getY() >= this.intValue3
            && relative.getY() <= this.intValue4
            && relative.getZ() >= this.intValue5
            && relative.getZ() <= this.intValue6;
      }

      private int method4081() {
         return (this.intValue2 - this.intValue + 1) * (this.intValue4 - this.intValue3 + 1) * (this.intValue6 - this.intValue5 + 1);
      }

      private int method4082(int intValue, int y, int z) {
         return ((intValue - this.intValue) * (this.intValue4 - this.intValue3 + 1) + (y - this.intValue3)) * (this.intValue6 - this.intValue5 + 1)
            + (z - this.intValue5);
      }

      private int method4083(BlockPos immutable) {
         return this.method4082(immutable.getX(), immutable.getY(), immutable.getZ());
      }
   }
}
