package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(name = "VoidESP", description = "module.voidESP.description", category = ModuleCategory.RENDER)
public final class VoidESPModule extends Module {
   public static final VoidESPModule INSTANCE = new VoidESPModule();
   private final SliderSetting radius = new SliderSetting("module.voidESP.radius", 12, 2, 24, 1, "module.voidESP.radius.desc", "b");
   private final SliderSetting depth = new SliderSetting("module.voidESP.depth", 32, 4, 128, 1, "module.voidESP.depth.desc", "b");
   private final BooleanSetting onlyAtEdge = new BooleanSetting("module.voidESP.onlyAtEdge", "module.voidESP.onlyAtEdge.desc", true);
   private final Map<Long, BlockPos> unsupportedColumns = new HashMap<>();
   private ClientLevel cachedLevel;
   private int scanCursor;
   private int scanY = Integer.MIN_VALUE;
   private int scanDepth;
   private boolean nearEdge;

   private VoidESPModule() {
   }

   @Override
   public void onDisable() {
      unsupportedColumns.clear();
      cachedLevel = null;
      scanY = Integer.MIN_VALUE;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.level != cachedLevel || mc.player == null) {
         unsupportedColumns.clear();
         cachedLevel = mc.level;
         scanY = Integer.MIN_VALUE;
         scanCursor = 0;
      }
      if (mc.player == null || cachedLevel == null) return;
      int centerX = mc.player.getBlockX();
      int centerZ = mc.player.getBlockZ();
      int currentY = (int)Math.ceil(mc.player.getY() - 0.0001);
      int scanRadius = (int)radius.getValue3();
      int requestedDepth = (int)depth.getValue3();
      if (scanY != currentY || scanDepth != requestedDepth) {
         unsupportedColumns.clear();
         scanY = currentY;
         scanDepth = requestedDepth;
         scanCursor = 0;
      }
      unsupportedColumns.values().removeIf(position -> Math.abs(position.getX() - centerX) > scanRadius
         || Math.abs(position.getZ() - centerZ) > scanRadius
         || !cachedLevel.hasChunk(position.getX() >> 4, position.getZ() >> 4));
      nearEdge = false;
      // Ближайшие колонки проверяются каждый тик: предупреждение не ждёт обхода всего радиуса.
      for (int offsetX = -1; offsetX <= 1; offsetX++) {
         for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
            nearEdge |= scanColumn(centerX + offsetX, centerZ + offsetZ);
         }
      }
      int diameter = scanRadius * 2 + 1;
      int columnCount = diameter * diameter;
      // ponytail: не более 128 дальних колонок за тик; полный проход R=24 занимает 19 тиков.
      for (int scanned = 0; scanned < Math.min(128, columnCount); scanned++) {
         int index = scanCursor++ % columnCount;
         scanColumn(centerX - scanRadius + index % diameter, centerZ - scanRadius + index / diameter);
      }
      scanCursor %= columnCount;
   }

   private boolean scanColumn(int x, int z) {
      long key = ChunkPos.pack(x, z);
      ChunkAccess chunk = cachedLevel.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false);
      if (chunk == null) {
         unsupportedColumns.remove(key);
         return false;
      }
      BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
      int bottom = Math.max(cachedLevel.getMinY(), scanY - scanDepth);
      for (int y = scanY - 1; y >= bottom; y--) {
         cursor.set(x, y, z);
         var state = chunk.getBlockState(cursor);
         // Вода тоже спасает от падения; трава и прочие блоки без коллизии — нет.
         if (!state.getCollisionShape(cachedLevel, cursor).isEmpty() || !state.getFluidState().isEmpty()) {
            unsupportedColumns.remove(key);
            return false;
         }
      }
      unsupportedColumns.put(key, new BlockPos(x, scanY, z));
      return true;
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (mc.player == null || mc.level != cachedLevel || (onlyAtEdge.isEnabled() && !nearEdge)) return;
      var frustum = FrustumHolder.getFrustum();
      for (BlockPos position : unsupportedColumns.values()) {
         if (!cachedLevel.hasChunk(position.getX() >> 4, position.getZ() >> 4)) continue;
         // Уровни постепенно затухают вниз: верх опасной колонки виден без непрозрачной стены.
         int visibleDepth = Math.min(scanDepth, 8);
         for (int layer = 0; layer < visibleDepth; layer++) {
            AABB box = new AABB(position.getX(), position.getY() - layer - 0.04, position.getZ(),
               position.getX() + 1, position.getY() - layer + 0.02, position.getZ() + 1);
            if (frustum == null || frustum.isVisible(box)) {
               int alpha = Math.round(190.0F * (1.0F - (float)layer / visibleDepth));
               GlowUtil.method2230(box, alpha << 24 | 0xFF4040, 1.0F);
            }
         }
      }
   }
}
