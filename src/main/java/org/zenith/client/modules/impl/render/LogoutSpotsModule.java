package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(name = "LogoutSpots", description = "module.logoutSpots.description", category = ModuleCategory.RENDER)
public final class LogoutSpotsModule extends Module {
   public static final LogoutSpotsModule INSTANCE = new LogoutSpotsModule();
   private final SliderSetting lifetime = new SliderSetting("module.logoutSpots.lifetime", 300, 0, 3600, 30, "module.logoutSpots.lifetime.desc", "s");
   private final SliderSetting maxSpots = new SliderSetting("module.logoutSpots.maxSpots", 32, 1, 128, 1, "module.logoutSpots.maxSpots.desc", "");
   private final BooleanSetting showName = new BooleanSetting("module.logoutSpots.showName", "module.logoutSpots.showName.desc", true);
   private final BooleanSetting showTime = new BooleanSetting("module.logoutSpots.showTime", "module.logoutSpots.showTime.desc", true);
   private final BooleanSetting box = new BooleanSetting("module.logoutSpots.box", "module.logoutSpots.box.desc", true);
   private final ColorSetting color = new ColorSetting("module.logoutSpots.color", "module.logoutSpots.color.desc", new ZenithColor(240, 160, 85));
   private final Map<UUID, PlayerSnapshot> snapshots = new LinkedHashMap<>();
   private final Map<UUID, LogoutSpot> spots = new LinkedHashMap<>();
   private ClientLevel trackedLevel;

   private LogoutSpotsModule() {
   }

   private boolean hasWorld() {
      if (this.trackedLevel != mc.level) {
         this.snapshots.clear();
         this.spots.clear();
         this.trackedLevel = mc.level;
      }
      return mc.level != null && mc.player != null;
   }

   private PlayerSnapshot snapshot(Player player) {
      return new PlayerSnapshot(player.getUUID(), player.getGameProfile().name(), player.getBoundingBox(), mc.level.dimension(), System.nanoTime());
   }

   private void remember(Player player) {
      if (player == mc.player) return;
      this.snapshots.remove(player.getUUID());
      if (!player.isAlive()) return;
      if (this.snapshots.size() >= 256) this.snapshots.remove(this.snapshots.keySet().iterator().next());
      this.snapshots.put(player.getUUID(), this.snapshot(player));
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (!this.hasWorld()) return;
      this.snapshots.values().removeIf(snapshot -> System.nanoTime() - snapshot.observedAt() > 5_000_000_000L);
      for (Player player : mc.level.players()) this.remember(player);
      this.prune();
   }

   @EventTarget
   public void onPacket(PacketReceiveEvent event) {
      if (event.isCancelled() || !this.hasWorld()) return;
      if (event.getPacket() instanceof ClientboundRemoveEntitiesPacket packet) {
         // Удаление сущности бывает и при выходе из дальности прорисовки: нужен выход из списка игроков.
         for (int id : packet.entityIds()) {
            if (mc.level.getEntity(id) instanceof Player player && player != mc.player) {
               this.remember(player);
               if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(player.getUUID()) == null) this.addSpot(player.getUUID());
            }
         }
      } else if (event.getPacket() instanceof ClientboundPlayerInfoRemovePacket packet) {
         // Событие идёт до применения пакета; читаем последнюю позицию ещё живой сущности.
         for (UUID id : packet.profileIds()) {
            for (Player player : mc.level.players()) {
               if (player.getUUID().equals(id)) this.remember(player);
            }
            this.addSpot(id);
         }
      } else if (event.getPacket() instanceof ClientboundPlayerInfoUpdatePacket packet) {
         for (var entry : packet.newEntries()) {
            this.removeReturningPlayer(entry.profileId(), entry.profile() == null ? null : entry.profile().name());
         }
      }
   }

   private void addSpot(UUID id) {
      PlayerSnapshot snapshot = this.snapshots.remove(id);
      if (snapshot == null || System.nanoTime() - snapshot.observedAt() > 5_000_000_000L || this.spots.containsKey(id)) return;
      this.spots.put(id, new LogoutSpot(snapshot, System.nanoTime()));
      this.prune();
   }

   private void removeReturningPlayer(UUID id, String name) {
      this.spots.values().removeIf(spot -> spot.player().id().equals(id) || name != null && spot.player().name().equalsIgnoreCase(name));
      this.snapshots.remove(id);
   }

   @EventTarget
   public void onEntityAdd(EntityAddEvent event) {
      if (this.hasWorld() && event.getEntity() instanceof Player player && player != mc.player) {
         this.removeReturningPlayer(player.getUUID(), player.getGameProfile().name());
         this.remember(player);
      }
   }

   private void prune() {
      long now = System.nanoTime();
      double ttl = this.lifetime.getValue3() * 1_000_000_000.0;
      this.spots.values().removeIf(spot -> !spot.player().dimension().equals(mc.level.dimension()) || ttl > 0 && now - spot.loggedAt() >= ttl);
      int limit = Math.max(1, (int)this.maxSpots.getValue3());
      while (this.spots.size() > limit) this.spots.remove(this.spots.keySet().iterator().next());
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (!this.hasWorld()) return;
      this.prune();
      if (!this.box.isEnabled()) return;
      for (LogoutSpot spot : this.spots.values()) GlowUtil.method2232(spot.player().box(), this.color.method1445(), 1.0F, true, true, false);
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      if (!this.hasWorld()) return;
      this.prune();
      if (mc.gui.hud.isHidden() || !this.showName.isEnabled() && !this.showTime.isEnabled()) return;
      var font = Fonts.NEW_MEDIUM.getFont(5.5F);
      var context = event.getCustomDrawContext();
      var background = Zenith.getInstance().getStyleManager().getCurrentStyle().getHudBackground().getZenithColor();
      String timeFormat = Zenith.getInstance().getLocalization().translate("module.logoutSpots.ago");
      for (LogoutSpot spot : this.spots.values()) {
         AABB bounds = spot.player().box();
         Vec3 anchor = new Vec3((bounds.minX + bounds.maxX) / 2.0, bounds.maxY + 0.25, (bounds.minZ + bounds.maxZ) / 2.0);
         if (!ProjectionUtils.method302(anchor)) continue;
         Vec3 screen = ProjectionUtils.method301(anchor);
         if (screen.z <= 0.0 || screen.z >= 1.0) continue;
         String text = this.showName.isEnabled() ? spot.player().name() : "";
         if (this.showTime.isEnabled()) {
            long seconds = Math.max(0, (System.nanoTime() - spot.loggedAt()) / 1_000_000_000L);
            text += (text.isEmpty() ? "" : "  ") + timeFormat.replace("{seconds}", Long.toString(seconds));
         }
         float x = (float)screen.x - font.width(text) / 2.0F;
         float y = (float)screen.y;
         context.drawRoundedRect(x - 4.0F, y - 3.0F, font.width(text) + 8.0F, font.height() + 6.0F,
            ProxyDotRadiusData.method8069(3.0F), background);
         context.drawText(font, text, x, y, this.color.getZenithColor());
      }
   }

   @Override
   public void onDisable() {
      this.snapshots.clear();
      this.spots.clear();
      this.trackedLevel = null;
      super.onDisable();
   }

   private record PlayerSnapshot(UUID id, String name, AABB box, ResourceKey<Level> dimension, long observedAt) {
   }

   private record LogoutSpot(PlayerSnapshot player, long loggedAt) {
   }
}
