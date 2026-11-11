package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.UpdateEvent;

/** Session counters persist between restarts until explicitly reset. */
public final class SessionStats extends JsonListStorage<SessionStats.Counters> implements MinecraftAccess {
   private final Map<UUID, Long> recentAttacks = new HashMap<>();
   private final Set<UUID> countedDeaths = new HashSet<>();
   private ClientLevel trackedLevel;
   private boolean playerDead;
   private long lastSaveNanos = System.nanoTime();
   private long sessionTimeBaseline;
   private Counters counters;

   public SessionStats() {
      super("session-stats.json", "", new TypeToken<ArrayList<Counters>>() {}.getType(), ArrayList::new);
      counters = items.stream().filter(value -> value != null).findFirst().orElseGet(Counters::new);
      counters.kills = Math.max(0, counters.kills);
      counters.deaths = Math.max(0, counters.deaths);
      counters.seconds = Math.max(0, counters.seconds);
      items.clear();
      items.add(counters);
      sessionTimeBaseline = currentPlaySeconds();
      EventManager.register(this);
   }

   @EventTarget
   public void onAttack(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST && !event.isCancelled()
         && mc.player != null && event.getEntity() instanceof Player player && player != mc.player) {
         recentAttacks.put(player.getUUID(), System.nanoTime());
      }
   }

   @EventTarget
   public void onEntityDeath(EntityDeathEvent event) {
      if (mc.player == null || !(event.getLivingEntity() instanceof Player player) || player == mc.player) {
         return;
      }
      Long attackedAt = recentAttacks.remove(player.getUUID());
      // The client cannot prove server-side kill ownership; count only a recently attacked player's death.
      if (attackedAt != null && System.nanoTime() - attackedAt <= 10_000_000_000L
         && countedDeaths.add(player.getUUID())) {
         counters.kills++;
         save();
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (trackedLevel != mc.level) {
         recentAttacks.clear();
         countedDeaths.clear();
         trackedLevel = mc.level;
         playerDead = false;
      }
      long now = System.nanoTime();
      recentAttacks.values().removeIf(time -> now - time > 10_000_000_000L);
      if (mc.player != null) {
         boolean dead = mc.player.isDeadOrDying() || mc.player.getHealth() <= 0.0F;
         if (dead && !playerDead) {
            counters.deaths++;
            save();
         }
         playerDead = dead;
         if (!dead) {
            countedDeaths.removeIf(id -> mc.level != null && mc.level.players().stream()
               .anyMatch(player -> player.getUUID().equals(id) && player.isAlive()));
         }
      }
      if (now - lastSaveNanos >= 30_000_000_000L) {
         save();
         lastSaveNanos = now;
      }
   }

   public int getKills() { return counters.kills; }
   public int getDeaths() { return counters.deaths; }
   public long getSeconds() { return counters.seconds + Math.max(0, currentPlaySeconds() - sessionTimeBaseline); }

   public void resetCounters() {
      counters.kills = 0;
      counters.deaths = 0;
      counters.seconds = 0;
      sessionTimeBaseline = currentPlaySeconds();
      recentAttacks.clear();
      save();
   }

   @Override
   public void save() {
      if (counters == null) return;
      counters.seconds = getSeconds();
      sessionTimeBaseline = currentPlaySeconds();
      super.save();
   }

   private long currentPlaySeconds() {
      PlayTimeTracker tracker = Zenith.getInstance().getPlayTimeTracker();
      return tracker == null ? 0 : tracker.method1399();
   }

   public static final class Counters {
      public int kills;
      public int deaths;
      public long seconds;
   }
}
