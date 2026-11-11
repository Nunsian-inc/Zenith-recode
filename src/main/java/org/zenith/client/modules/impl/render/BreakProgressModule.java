package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.utility.mixin.client_core.AccessorMultiPlayerGameMode;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(name = "BreakProgress", description = "module.breakProgress.description", category = ModuleCategory.RENDER)
public final class BreakProgressModule extends Module {
   public static final BreakProgressModule INSTANCE = new BreakProgressModule();
   private final BooleanSetting self = new BooleanSetting("module.breakProgress.self", "module.breakProgress.self.desc", true);
   private final BooleanSetting others = new BooleanSetting("module.breakProgress.others", "module.breakProgress.others.desc", true);
   private final BooleanSetting percentages = new BooleanSetting("module.breakProgress.percentages", "module.breakProgress.percentages.desc", true);
   private final BooleanSetting fill = new BooleanSetting("module.breakProgress.fill", "module.breakProgress.fill.desc", true);
   private final ColorSetting color = new ColorSetting("module.breakProgress.color", "module.breakProgress.color.desc", new ZenithColor(110, 220, 150));
   private final BooleanSetting hideZero = new BooleanSetting("module.breakProgress.hideZero", "module.breakProgress.hideZero.desc", true);
   private final Map<Integer, BreakingBlock> breakers = new LinkedHashMap<>();
   private ClientLevel trackedLevel;

   private BreakProgressModule() {
   }

   private boolean hasWorld() {
      if (this.trackedLevel != mc.level) {
         this.breakers.clear();
         this.trackedLevel = mc.level;
      }
      return mc.level != null && mc.player != null;
   }

   @EventTarget
   public void onPacket(PacketReceiveEvent event) {
      if (event.isCancelled() || !this.hasWorld() || !(event.getPacket() instanceof ClientboundBlockDestructionPacket packet)
         || packet.getId() == mc.player.getId()) return;
      // Состояние привязано к ломателю: перенос на другой блок не оставляет старый бокс.
      this.breakers.remove(packet.getId());
      if (this.others.isEnabled() && packet.getProgress() >= 0 && packet.getProgress() <= 9
         && mc.level.getEntity(packet.getId()) instanceof Player) {
         if (this.breakers.size() >= 256) this.breakers.remove(this.breakers.keySet().iterator().next());
         this.breakers.put(packet.getId(), new BreakingBlock(packet.getPos().immutable(), packet.getProgress() / 10.0F, System.nanoTime()));
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.hasWorld()) this.prune();
   }

   private void prune() {
      long now = System.nanoTime();
      this.breakers.entrySet().removeIf(entry -> !this.others.isEnabled() || now - entry.getValue().updatedAt() > 20_000_000_000L
         || !(mc.level.getEntity(entry.getKey()) instanceof Player) || mc.level.getBlockState(entry.getValue().position()).isAir());
   }

   private Map<BlockPos, Float> activeBlocks() {
      Map<BlockPos, Float> blocks = new LinkedHashMap<>();
      if (!this.hasWorld()) return blocks;
      this.prune();
      for (BreakingBlock block : this.breakers.values()) blocks.merge(block.position(), block.progress(), Math::max);
      if (this.self.isEnabled() && mc.gameMode != null && mc.gameMode.isDestroying()) {
         BlockPos position = ((AccessorMultiPlayerGameMode)mc.gameMode).zenith$getDestroyBlockPos();
         if (position != null && !mc.level.getBlockState(position).isAir()) {
            blocks.merge(position, Mth.clamp(mc.gameMode.destroyProgress, 0.0F, 1.0F), Math::max);
         }
      }
      if (this.hideZero.isEnabled()) blocks.values().removeIf(progress -> progress <= 0.0F);
      return blocks;
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (!this.fill.isEnabled()) return;
      for (var entry : this.activeBlocks().entrySet()) {
         AABB box = new AABB(entry.getKey()).setMaxY(entry.getKey().getY() + Math.max(0.005F, entry.getValue()));
         GlowUtil.method2232(box, this.color.method1445(), 1.0F, true, true, false);
      }
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      if (!this.percentages.isEnabled() || mc.gui.hud.isHidden()) return;
      var context = event.getCustomDrawContext();
      var font = Fonts.NEW_MEDIUM.getFont(5.5F);
      var background = Zenith.getInstance().getStyleManager().getCurrentStyle().getHudBackground().getZenithColor();
      for (var entry : this.activeBlocks().entrySet()) {
         Vec3 anchor = Vec3.atCenterOf(entry.getKey()).add(0.0, 0.65, 0.0);
         if (!ProjectionUtils.method302(anchor)) continue;
         Vec3 screen = ProjectionUtils.method301(anchor);
         if (screen.z <= 0.0 || screen.z >= 1.0) continue;
         String text = Math.round(entry.getValue() * 100.0F) + "%";
         float x = (float)screen.x - font.width(text) / 2.0F;
         float y = (float)screen.y;
         context.drawRoundedRect(x - 3.0F, y - 2.0F, font.width(text) + 6.0F, font.height() + 4.0F,
            ProxyDotRadiusData.method8069(2.0F), background);
         context.drawText(font, text, x, y, this.color.getZenithColor());
      }
   }

   @Override
   public void onDisable() {
      this.breakers.clear();
      this.trackedLevel = null;
      super.onDisable();
   }

   private record BreakingBlock(BlockPos position, float progress, long updatedAt) {
   }
}
