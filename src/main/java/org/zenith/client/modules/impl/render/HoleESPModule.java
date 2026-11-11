package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(name = "HoleESP", description = "module.holeEsp.description", category = ModuleCategory.RENDER)
public final class HoleESPModule extends Module {
   public static final HoleESPModule INSTANCE = new HoleESPModule();
   private final SliderSetting radius = new SliderSetting("module.holeEsp.radius", 10, 2, 16, 1, "module.holeEsp.radius.desc", "b");
   private final SliderSetting height = new SliderSetting("module.holeEsp.height", 4, 1, 8, 1, "module.holeEsp.height.desc", "b");
   private final SliderSetting maxHoles = new SliderSetting("module.holeEsp.maxHoles", 128, 8, 512, 8, "module.holeEsp.maxHoles.desc", "");
   private final MultiSelectSetting types = new MultiSelectSetting("module.holeEsp.types", "module.holeEsp.types.desc",
      MultiSelectSetting.Entry.method5231("module.holeEsp.bedrock", true),
      MultiSelectSetting.Entry.method5231("module.holeEsp.obsidian", true),
      MultiSelectSetting.Entry.method5231("module.holeEsp.mixed", true));
   private final ColorSetting bedrockColor = new ColorSetting("module.holeEsp.bedrockColor", new ZenithColor(90, 220, 140));
   private final ColorSetting obsidianColor = new ColorSetting("module.holeEsp.obsidianColor", new ZenithColor(240, 100, 110));
   private final ColorSetting mixedColor = new ColorSetting("module.holeEsp.mixedColor", new ZenithColor(240, 195, 80));
   private final BooleanSetting fill = new BooleanSetting("module.holeEsp.fill", "module.holeEsp.fill.desc", true);
   private final BooleanSetting outline = new BooleanSetting("module.holeEsp.outline", "module.holeEsp.outline.desc", true);
   private static final Direction[] WALLS = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private static final String[] TYPE_KEYS = {"module.holeEsp.bedrock", "module.holeEsp.obsidian", "module.holeEsp.mixed"};
   private final Map<BlockPos, Integer> holes = new HashMap<>();
   private ClientLevel cachedLevel;
   private int scanCursor;
   private int scanY = Integer.MIN_VALUE;
   private int scanHeight;
   private int scanRadius;

   private HoleESPModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.level != this.cachedLevel || mc.player == null) {
         this.clear();
         this.cachedLevel = mc.level;
      }
      if (mc.player == null || this.cachedLevel == null) return;
      int centerX = mc.player.getBlockX();
      int centerZ = mc.player.getBlockZ();
      int centerY = mc.player.getBlockY();
      int requestedHeight = (int)this.height.getValue3();
      int requestedRadius = (int)this.radius.getValue3();
      if (this.scanY != centerY || this.scanHeight != requestedHeight || this.scanRadius != requestedRadius) {
         this.holes.clear();
         this.scanCursor = 0;
         this.scanY = centerY;
         this.scanHeight = requestedHeight;
         this.scanRadius = requestedRadius;
      }
      // Уже найденные ямы проверяем каждый тик: поставленный блок сразу убирает подсветку.
      this.holes.entrySet().removeIf(entry -> Math.abs(entry.getKey().getX() - centerX) > this.scanRadius
         || Math.abs(entry.getKey().getZ() - centerZ) > this.scanRadius || this.classify(entry.getKey()) != entry.getValue());
      int diameter = this.scanRadius * 2 + 1;
      int columns = diameter * diameter;
      // ponytail: до 128 колонок за тик; при R=16 полный обход занимает 9 тиков, дальше нужен чанковый индекс.
      for (int i = 0; i < Math.min(128, columns); i++) {
         int index = this.scanCursor++ % columns;
         int x = centerX - this.scanRadius + index % diameter;
         int z = centerZ - this.scanRadius + index / diameter;
         for (int y = Math.max(this.cachedLevel.getMinY() + 1, centerY - this.scanHeight);
              y <= Math.min(this.cachedLevel.getMaxY() - 2, centerY + this.scanHeight); y++) {
            BlockPos position = new BlockPos(x, y, z);
            int type = this.classify(position);
            if (type >= 0) this.holes.put(position, type);
            else this.holes.remove(position);
         }
      }
      this.scanCursor %= columns;
   }

   private int classify(BlockPos position) {
      // Проверяем соседние чанки до чтения блоков и никогда не запрашиваем загрузку новых.
      for (Direction direction : WALLS) {
         BlockPos neighbor = position.relative(direction);
         if (!this.cachedLevel.hasChunk(neighbor.getX() >> 4, neighbor.getZ() >> 4)) return -1;
      }
      if (!this.isEmpty(position) || !this.isEmpty(position.above())) return -1;
      int bedrock = 0;
      for (Direction direction : WALLS) {
         BlockState state = this.cachedLevel.getBlockState(position.relative(direction));
         if (state.is(Blocks.BEDROCK)) bedrock++;
         else if (!state.is(Blocks.OBSIDIAN)) return -1;
      }
      return bedrock == 5 ? 0 : bedrock == 0 ? 1 : 2;
   }

   private boolean isEmpty(BlockPos position) {
      BlockState state = this.cachedLevel.getBlockState(position);
      return state.getFluidState().isEmpty() && state.getCollisionShape(this.cachedLevel, position).isEmpty();
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (mc.player == null || mc.level != this.cachedLevel) return;
      var frustum = FrustumHolder.getFrustum();
      int rendered = 0;
      for (var entry : this.holes.entrySet()) {
         int type = entry.getValue();
         if (!this.types.method5226(TYPE_KEYS[type])) continue;
         AABB box = new AABB(entry.getKey()).setMaxY(entry.getKey().getY() + 0.15);
         if (frustum != null && !frustum.isVisible(box)) continue;
         ColorSetting color = type == 0 ? this.bedrockColor : type == 1 ? this.obsidianColor : this.mixedColor;
         GlowUtil.method2232(box, color.method1445(), 1.0F, this.outline.isEnabled(), this.fill.isEnabled(), false);
         if (++rendered >= (int)this.maxHoles.getValue3()) break;
      }
   }

   private void clear() {
      this.holes.clear();
      this.cachedLevel = null;
      this.scanCursor = 0;
      this.scanY = Integer.MIN_VALUE;
   }

   @Override
   public void onDisable() {
      this.clear();
      super.onDisable();
   }
}
