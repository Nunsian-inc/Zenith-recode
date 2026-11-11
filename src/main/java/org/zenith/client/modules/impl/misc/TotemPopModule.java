package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.world.entity.player.Player;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.EntityStatusEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(name = "TotemPop", description = "module.totemPop.description", category = ModuleCategory.MISC)
public final class TotemPopModule extends Module {
   public static final TotemPopModule INSTANCE = new TotemPopModule();
   private final BooleanSetting chat = new BooleanSetting("module.totemPop.chat", "module.totemPop.chat.desc", true);
   private final BooleanSetting notifications = new BooleanSetting("module.totemPop.notifications", "module.totemPop.notifications.desc", false);
   private final BooleanSetting hud = new BooleanSetting("module.totemPop.hud", "module.totemPop.hud.desc", true);
   private final BooleanSetting resetOnDeath = new BooleanSetting("module.totemPop.resetOnDeath", "module.totemPop.resetOnDeath.desc", true);
   private final SliderSetting resetAfter = new SliderSetting("module.totemPop.resetAfter", 0, 0, 3600, 30, "module.totemPop.resetAfter.desc", "s");
   private final BooleanSetting self = new BooleanSetting("module.totemPop.self", "module.totemPop.self.desc", true);
   private final BooleanSetting others = new BooleanSetting("module.totemPop.others", "module.totemPop.others.desc", true);
   private final Map<UUID, PopCount> counts = new LinkedHashMap<>();
   private ClientLevel trackedLevel;

   private TotemPopModule() {
   }

   private boolean hasWorld() {
      if (this.trackedLevel != mc.level) {
         this.counts.clear();
         this.trackedLevel = mc.level;
      }
      return mc.level != null && mc.player != null;
   }

   private boolean includes(Player player) {
      return player == mc.player ? this.self.isEnabled() : this.others.isEnabled();
   }

   @EventTarget
   public void onEntityStatus(EntityStatusEvent event) {
      if (!this.hasWorld()) return;
      var packet = event.getEntityStatusS2CPacket();
      if (!(packet.getEntity(mc.level) instanceof Player player) || !this.includes(player)) return;
      if (packet.getEventId() == 35) {
         this.prune();
         PopCount previous = this.counts.remove(player.getUUID());
         if (this.counts.size() >= 256) this.counts.remove(this.counts.keySet().iterator().next());
         PopCount current = new PopCount(player.getGameProfile().name(), previous == null ? 1 : previous.count() + 1, System.nanoTime(), false);
         this.counts.put(player.getUUID(), current);
         this.notify("module.totemPop.popped", current);
      } else if (packet.getEventId() == 3) {
         this.onDeath(player);
      }
   }

   @EventTarget
   public void onEntityDeath(EntityDeathEvent event) {
      if (mc.isSameThread() && this.hasWorld() && event.getLivingEntity() instanceof Player player) this.onDeath(player);
   }

   private void onDeath(Player player) {
      PopCount count = this.counts.get(player.getUUID());
      if (count == null || count.dead()) return;
      this.notify("module.totemPop.died", count);
      if (this.resetOnDeath.isEnabled()) this.counts.remove(player.getUUID());
      else this.counts.put(player.getUUID(), new PopCount(count.name(), count.count(), count.lastPop(), true));
   }

   @EventTarget
   public void onPacket(PacketReceiveEvent event) {
      if (!event.isCancelled() && this.hasWorld() && event.getPacket() instanceof ClientboundPlayerInfoRemovePacket packet) {
         // Выход из дальности прорисовки не считается выходом с сервера.
         for (UUID id : packet.profileIds()) this.counts.remove(id);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (!this.hasWorld()) return;
      this.prune();
      for (Player player : mc.level.players()) {
         if (player.isDeadOrDying() || player.getHealth() <= 0.0F) this.onDeath(player);
      }
   }

   private void prune() {
      long now = System.nanoTime();
      double timeout = this.resetAfter.getValue3() * 1_000_000_000.0;
      this.counts.entrySet().removeIf(entry -> (entry.getKey().equals(mc.player.getUUID()) ? !this.self.isEnabled() : !this.others.isEnabled())
         || timeout > 0 && now - entry.getValue().lastPop() >= timeout || this.resetOnDeath.isEnabled() && entry.getValue().dead());
   }

   private void notify(String key, PopCount count) {
      String message = Zenith.getInstance().getLocalization().translate(key)
         .replace("{player}", count.name()).replace("{count}", Integer.toString(count.count()));
      if (this.chat.isEnabled()) ChatLogger.method2019(message);
      if (this.notifications.isEnabled()) Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Component.literal(message));
   }

   public boolean isHudVisible() {
      return this.isEnabled() && this.hud.isEnabled() && this.hasWorld();
   }

   public List<PopCount> getCounts() {
      if (!this.isEnabled() || !this.hasWorld()) return List.of();
      this.prune();
      return this.counts.values().stream().sorted(Comparator.comparingLong(PopCount::lastPop).reversed()).toList();
   }

   @Override
   public void onDisable() {
      this.counts.clear();
      this.trackedLevel = null;
      super.onDisable();
   }

   public record PopCount(String name, int count, long lastPop, boolean dead) {
   }
}
