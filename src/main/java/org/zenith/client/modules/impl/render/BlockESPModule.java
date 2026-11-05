package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.display.base.CustomDrawContext;

@ModuleInfo(
   name = "BlockESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.blockesp.description"
)
public final class BlockESPModule extends Module {
   private final Map<Long, BlockESPModule.Entry> items = new ConcurrentHashMap<>();
   private int intValue = -1;
   private ClientLevel clientWorld;
   private boolean flag;
   private final BlockListSetting blockListSetting = new BlockListSetting(
      "module.blockESP.itemSelectSetting", "module.blockESP.itemSelectSetting.desc", new ArrayList<>(), () -> true
   );
   private final ModeSetting mode = new ModeSetting("module.blockESP.mode", "module.blockESP.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.blockESP.onlyUpdate").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.blockESP.all");
   private final SliderSetting range = new SliderSetting(
      "module.blockESP.range", 8.0F, 1.0F, 32.0F, 1.0F, "module.blockESP.range.desc", "ch", this.entry2::isSelected, null
   );
   private final BooleanSetting tracers = new BooleanSetting("module.blockESP.tracers", "module.blockESP.tracers.desc", false);
   public static final BlockESPModule INSTANCE = new BlockESPModule();
   private static final float CONST_floatValue = 45.0F;
   private static final float CONST_floatValue2 = 30.0F;
   private static final float CONST_floatValue3 = 10.0F;

   private BlockESPModule() {
      this.blockListSetting.method1617(Blocks.DIAMOND_ORE);
   }

   @Override
   public void onEnable() {
      this.items.clear();
      this.intValue = this.blockListSetting.getVersion();
      this.flag = this.entry2.isSelected();
      super.onEnable();
      if (this.isEnabled() && this.entry2.isSelected()) {
         this.method7567();
      }
   }

   @Override
   public void onDisable() {
      this.items.clear();
      this.clientWorld = null;
      super.onDisable();
   }

   public void method7560(ChunkAccess returnValue, int x, int z) {
      if (this.isEnabled() && this.entry2.isSelected()) {
         this.method7569(returnValue, x, z, this.method7568(new HashMap<>()));
      }
   }

   public void method7561(int x, int z) {
      this.items.remove(ChunkPos.pack(x, z));
   }

   @EventTarget
   public void method7562(UpdateEvent event) {
      ClientLevel level = mc.level;
      if (mc.player == null || level == null) {
         this.items.clear();
         this.clientWorld = null;
      } else if (level != this.clientWorld) {
         this.clientWorld = level;
         this.items.clear();
         if (this.entry2.isSelected()) {
            this.method7567();
         }
      } else {
         boolean selected = this.entry2.isSelected();
         if (selected && !this.flag) {
            this.flag = true;
            this.method7567();
         } else {
            this.flag = selected;
            if (this.blockListSetting.getVersion() != this.intValue) {
               this.intValue = this.blockListSetting.getVersion();
               this.method7564();
               if (selected) {
                  this.method7567();
               }
            }

            this.method7563(level);
         }
      }
   }

   private void method7563(ClientLevel level) {
      if (!this.items.isEmpty()) {
         this.items.values().removeIf(var1x -> !level.hasChunk(var1x.intValue, var1x.intValue2));
      }
   }

   private void method7564() {
      for (BlockESPModule.Entry entry : this.items.values()) {
         entry.items.values().removeIf(data -> !this.blockListSetting.method1621(data.getBlock()));
      }

      this.items.values().removeIf(entry -> entry.items.isEmpty());
   }

   @EventTarget
   public void method7565(PacketEvent event) {
      if (event.method7100()) {
         if (event.getPacket3() instanceof ClientboundSectionBlocksUpdatePacket blocksUpdatePacket) {
            blocksUpdatePacket.runUpdates((var1x, var2x) -> this.method7566(var1x.immutable(), var2x.getBlock()));
         }

         if (event.getPacket3() instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
            this.method7566(blockUpdatePacket.getPos().immutable(), blockUpdatePacket.getBlockState().getBlock());
         }
      }
   }

   private void method7566(BlockPos immutable, Block block) {
      int x = immutable.getX() >> 4;
      int z = immutable.getZ() >> 4;
      long pack = ChunkPos.pack(x, z);
      if (this.method7570(block)) {
         this.items.computeIfAbsent(pack, var2x -> new BlockESPModule.Entry(x, z)).items.put(immutable, new BlockESPModule.Data(block, this.method7576(block)));
      } else {
         BlockESPModule.Entry entry = this.items.get(pack);
         if (entry != null) {
            entry.items.remove(immutable);
         }
      }
   }

   private void method7567() {
      ClientLevel level = mc.level;
      LocalPlayer player = mc.player;
      if (level != null && player != null) {
         int n = Math.max((int)this.range.getValue3(), mc.options.renderDistance().get() + 3);
         int n2 = player.getBlockX() >> 4;
         int n3 = player.getBlockZ() >> 4;
         var predicate = this.method7568(new HashMap<>());

         for (int i = n2 - n; i <= n2 + n; i++) {
            for (int j = n3 - n; j <= n3 + n; j++) {
               ChunkAccess chunk = level.getChunk(i, j, ChunkStatus.FULL, false);
               if (chunk != null) {
                  this.method7569(chunk, i, j, predicate);
               }
            }
         }
      }
   }

   private Predicate<BlockState> method7568(Map<Block, Boolean> map) {
      return blockState -> map.computeIfAbsent(blockState.getBlock(), this::method7570);
   }

   private void method7569(ChunkAccess returnValue, int x, int z, Predicate<BlockState> predicate) {
      BlockESPModule.Entry entry = new BlockESPModule.Entry(x, z);
      long pack = ChunkPos.pack(x, z);

      try {
         LevelChunkSection[] sections = returnValue.getSections();
         int minSectionY = returnValue.getMinSectionY();
         int n = x << 4;
         int n2 = z << 4;

         for (int i = 0; i < sections.length; i++) {
            LevelChunkSection levelChunkSection = sections[i];
            if (levelChunkSection != null && !levelChunkSection.hasOnlyAir() && levelChunkSection.maybeHas(predicate)) {
               int n3 = minSectionY + i << 4;

               for (int j = 0; j < 16; j++) {
                  for (int k = 0; k < 16; k++) {
                     for (int m = 0; m < 16; m++) {
                        BlockState blockState = levelChunkSection.getBlockState(j, m, k);
                        if (predicate.test(blockState)) {
                           Block block = blockState.getBlock();
                           entry.items.put(new BlockPos(n + j, n3 + m, n2 + k), new BlockESPModule.Data(block, this.method7576(block)));
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         return;
      }

      if (entry.items.isEmpty()) {
         this.items.remove(pack);
      } else {
         this.items.put(pack, entry);
      }
   }

   private boolean method7570(Block block) {
      return !(block instanceof AirBlock) && this.blockListSetting.method1621(block);
   }

   @EventTarget
   public void method7571(WorldRenderEvent event) {
      if (mc.level != null && mc.player != null) {
         boolean selected = this.entry2.isSelected();
         int n = (int)this.range.getValue3();
         int n2 = mc.player.getBlockX() >> 4;
         int n3 = mc.player.getBlockZ() >> 4;
         Vec3 vec = this.tracers.isEnabled() ? this.method7572() : null;
         Frustum frustum = FrustumHolder.getFrustum();
         int minY = mc.level.getMinY();
         int maxY = mc.level.getMaxY() + 1;

         for (BlockESPModule.Entry entry : this.items.values()) {
            if (!entry.items.isEmpty() && (!selected || Math.abs(entry.intValue - n2) <= n && Math.abs(entry.intValue2 - n3) <= n)) {
               int minX = entry.intValue << 4;
               int minZ = entry.intValue2 << 4;
               boolean flag = frustum == null || frustum.isVisible(new AABB(minX, minY, minZ, minX + 16, maxY, minZ + 16));
               if (flag || vec != null) {
                  for (Map.Entry entry2 : entry.items.entrySet()) {
                     int color = ((BlockESPModule.Data)entry2.getValue()).color();
                     if (color != 0) {
                        BlockPos key = (BlockPos)entry2.getKey();
                        if (flag) {
                           GlowUtil.method2230(new AABB(key), color, 1.0F);
                        }

                        if (vec != null) {
                           GlowUtil.method2245(vec, net.minecraft.world.phys.Vec3.atCenterOf(key), color, 1.0F, false);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Vec3 method7572() {
      return new Vec3(0.0, 0.0, 75.0)
         .xRot(-((float)Math.toRadians(mc.gameRenderer.mainCamera().xRot())))
         .yRot(-((float)Math.toRadians(mc.gameRenderer.mainCamera().yRot())))
         .add(FreeCamModule.INSTANCE.method1660());
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.BLOCK;
   }

   public void method7573(CustomDrawContext customDrawContext, float x, float f, float f2, float y) {
      if (customDrawContext != null) {
         int shade = this.method7576(Blocks.DIRT);
         if (shade != 0) {
            if (this.tracers.isEnabled()) {
               this.method7575(customDrawContext, x, y, x, f, 1.2F, ZenithColor.method7291(shade).method7294(0.9F));
            }

            float x2 = f2 / 10.0F;
            float f3 = 16.0F * x2;
            customDrawContext.pushMatrix();
            customDrawContext.pose().translate(x - f3 / 2.0F, f - f3 / 2.0F);
            customDrawContext.pose().scale(x2, x2);
            customDrawContext.item(Items.DIRT.getDefaultInstance(), 0, 0);
            customDrawContext.popMatrix();
            this.method7574(customDrawContext, x, f, f2, ZenithColor.method7291(shade));
         }
      }
   }

   private void method7574(CustomDrawContext customDrawContext, float x, float f, float f2, ZenithColor zenithColor) {
      float f3 = (float)Math.toRadians(45.0);
      float f4 = (float)Math.toRadians(30.0);
      float f5 = (float)Math.cos(f3);
      float f6 = (float)Math.sin(f3);
      float f7 = (float)Math.cos(f4);
      float f8 = (float)Math.sin(f4);
      float f9 = 0.5F;
      float[][] floatses = new float[][]{
         {-f9, -f9, -f9},
         {f9, -f9, -f9},
         {f9, -f9, f9},
         {-f9, -f9, f9},
         {-f9, f9, -f9},
         {f9, f9, -f9},
         {0.5F, 0.5F, 0.5F},
         {-f9, f9, f9}
      };
      float[][] floatses2 = new float[8][2];

      for (int i = 0; i < 8; i++) {
         float f10 = floatses[i][0] * f5 + floatses[i][2] * f6;
         float f11 = -floatses[i][0] * f6 + floatses[i][2] * f5;
         floatses2[i][0] = x + f10 * f2;
         floatses2[i][1] = f - (floatses[i][1] * f7 - f11 * f8) * f2;
      }

      for (int i = 0; i < 4; i++) {
         int n = (i + 1) % 4;
         this.method7575(customDrawContext, floatses2[i][0], floatses2[i][1], floatses2[n][0], floatses2[n][1], 1.0F, zenithColor);
         this.method7575(customDrawContext, floatses2[i + 4][0], floatses2[i + 4][1], floatses2[n + 4][0], floatses2[n + 4][1], 1.0F, zenithColor);
         this.method7575(customDrawContext, floatses2[i][0], floatses2[i][1], floatses2[i + 4][0], floatses2[i + 4][1], 1.0F, zenithColor);
      }
   }

   private void method7575(CustomDrawContext customDrawContext, float x, float y, float f, float f2, float height, ZenithColor zenithColor) {
      float x2 = f - x;
      float y2 = f2 - y;
      float hypot = (float)Math.hypot(x2, y2);
      if (!(hypot < 0.01F)) {
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(x, y);
         customDrawContext.pose().rotate((float)Math.atan2(y2, x2));
         customDrawContext.drawRect(0.0F, -height / 2.0F, hypot, height, zenithColor);
         customDrawContext.popMatrix();
      }
   }

   private int method7576(Block block) {
      if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
         return Color.cyan.getRGB();
      } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
         return 55040;
      } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
         return -16711859;
      } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
         return -2763307;
      } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
         return -65536;
      } else if (block == Blocks.ANCIENT_DEBRIS) {
         return -1;
      } else {
         MapColor defaultMapColor = block.defaultMapColor();
         return defaultMapColor != null ? new Color(defaultMapColor.col).getRGB() : 0;
      }
   }

   private static final class Data {
      private final Block block;
      private final int intValue;

      public Block block() {
         return this.block;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.block, other.block) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(block, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "block=" + this.block + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(Block block, int intValue) {
         this.block = block;
         this.intValue = intValue;
      }

      public Block getBlock() {
         return this.block;
      }

      public int color() {
         return this.intValue;
      }
   }

   private static final class Entry {
      private final int intValue;
      private final int intValue2;
      private final Map<BlockPos, BlockESPModule.Data> items = new ConcurrentHashMap<>();

      private Entry(int intValue, int z) {
         this.intValue = intValue;
         this.intValue2 = z;
      }
   }
}
