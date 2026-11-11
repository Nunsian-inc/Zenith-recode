package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(name = "ChunkBorders", description = "module.chunkBorders.description", category = ModuleCategory.RENDER)
public final class ChunkBordersModule extends Module {
   public static final ChunkBordersModule INSTANCE = new ChunkBordersModule();
   private final SliderSetting radius = new SliderSetting("module.chunkBorders.radius", 2, 0, 8, 1, "module.chunkBorders.radius.desc", "ch");
   private final SliderSetting height = new SliderSetting("module.chunkBorders.height", 0, -32, 32, 1, "module.chunkBorders.height.desc", "b");
   private final BooleanSetting ownChunkOnly = new BooleanSetting("module.chunkBorders.ownChunkOnly", "module.chunkBorders.ownChunkOnly.desc", false);
   private final ColorSetting color = new ColorSetting("module.chunkBorders.color", "module.chunkBorders.color.desc", new ZenithColor(166, 178, 255));

   private ChunkBordersModule() {
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (mc.player == null || mc.level == null) return;
      var chunk = new net.minecraft.world.level.ChunkPos(mc.player.getBlockX() >> 4, mc.player.getBlockZ() >> 4);
      int chunkRadius = ownChunkOnly.isEnabled() ? 0 : (int)radius.getValue3();
      int minX = (chunk.x() - chunkRadius) << 4;
      int minZ = (chunk.z() - chunkRadius) << 4;
      int maxX = (chunk.x() + chunkRadius + 1) << 4;
      int maxZ = (chunk.z() + chunkRadius + 1) << 4;
      double y = mc.player.getEyePosition(event.getFloatValue()).y + height.getValue3();
      int gridColor = color.getZenithColor().method7294(0.35F).method7287();
      for (int x = minX; x <= maxX; x += 16) {
         GlowUtil.method2245(new Vec3(x, y, minZ), new Vec3(x, y, maxZ), gridColor, 1.0F, false);
      }
      for (int z = minZ; z <= maxZ; z += 16) {
         GlowUtil.method2245(new Vec3(minX, y, z), new Vec3(maxX, y, z), gridColor, 1.0F, false);
      }
      int ownX = chunk.x() << 4;
      int ownZ = chunk.z() << 4;
      int ownColor = color.method1445();
      GlowUtil.method2245(new Vec3(ownX, y, ownZ), new Vec3(ownX + 16, y, ownZ), ownColor, 2.0F, false);
      GlowUtil.method2245(new Vec3(ownX + 16, y, ownZ), new Vec3(ownX + 16, y, ownZ + 16), ownColor, 2.0F, false);
      GlowUtil.method2245(new Vec3(ownX + 16, y, ownZ + 16), new Vec3(ownX, y, ownZ + 16), ownColor, 2.0F, false);
      GlowUtil.method2245(new Vec3(ownX, y, ownZ + 16), new Vec3(ownX, y, ownZ), ownColor, 2.0F, false);
   }
}
