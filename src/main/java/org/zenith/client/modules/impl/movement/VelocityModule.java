package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Generated;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "Velocity",
   category = ModuleCategory.MOVEMENT,
   description = "module.velocity.description"
)
public final class VelocityModule extends Module {
   public static final VelocityModule INSTANCE = new VelocityModule();
   private static final long CORRECTION_COOLDOWN_MILLIS = 2000L;
   private static final String MODE_DELAY = "module.velocity.mode.delay";
   private static final String MODE_VANILLA = "module.velocity.mode.vanilla";
   /** Delay — прежняя логика (придержать пакеты после отбрасывания в воздухе), Vanilla — отменить отбрасывание целиком. */
   private final ModeSetting mode = new ModeSetting("module.velocity.mode", "module.velocity.mode.desc", MODE_DELAY, MODE_VANILLA);
   private final SliderSetting delay = new SliderSetting(
      "module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.velocity.delay.desc", "ms", this::isDelayMode
   );
   private final Queue<VelocityModule.Entry> delayedPackets = new ConcurrentLinkedQueue<>();
   private final StopWatch delayTimer = new StopWatch();
   private boolean delayingPackets;
   private boolean disablePending;
   private long correctionCooldownEndTime;
   private final AtomicBoolean reachConflictCheckPending = new AtomicBoolean();

   private VelocityModule() {
   }

   @Override
   public void onEnable() {
      this.disablePending = false;
      if (ReachV3Module.INSTANCE.isEnabled()) {
         ChatLogger.method2019("Не работает с ReachV3");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.disablePending = true;
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (ReachV3Module.INSTANCE.isEnabled()) {
         if (this.reachConflictCheckPending.compareAndSet(false, true)) {
            mc.execute(() -> {
               this.reachConflictCheckPending.set(false);
               if (this.isEnabled() && ReachV3Module.INSTANCE.isEnabled()) {
                  ChatLogger.method2019("Не работает с ReachV3");
                  this.setToggled(false);
               }
            });
         }
      } else if (event.isIncoming()) {
         if (!this.isDelayMode()) {
            this.handleVanilla(event);
            return;
         }

         try {
            Packet packet = event.getPacket();
            if (packet instanceof ClientboundDisconnectPacket) {
               this.flushDelayedPackets();
               return;
            }

            if (packet instanceof ClientboundPlayerPositionPacket) {
               this.flushDelayedPackets();
               this.correctionCooldownEndTime = System.currentTimeMillis() + 2000L;
               return;
            }

            if (mc.player == null || mc.level == null) {
               return;
            }

            if (System.currentTimeMillis() < this.correctionCooldownEndTime) {
               return;
            }

            if (!this.delayingPackets) {
               if (!this.isLocalPlayerVelocityPacket(packet) || mc.player.onGround()) {
                  return;
               }

               this.delayingPackets = true;
               this.delayTimer.reset();
            }

            if (packet instanceof ServerboundChatPacket
               || packet instanceof ClientboundSetHealthPacket
               || packet instanceof ClientboundSoundPacket
               || packet instanceof ClientboundSystemChatPacket
               || packet instanceof ServerboundChatCommandPacket) {
               return;
            }

            event.cancel();
            this.delayedPackets.add(new VelocityModule.Entry(packet));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void onPacketProcessEnd(PacketProcessEndEvent event) {
      if (mc.level == null || mc.player == null) {
         this.delayedPackets.clear();
         this.delayingPackets = false;
      }

      if (this.delayingPackets && !this.delayedPackets.isEmpty()) {
         boolean playerOnGround = mc.player != null && mc.player.onGround();
         boolean delayElapsed = this.delayTimer.hasElapsed((long)this.delay.getValue3());
         if (playerOnGround || delayElapsed || !this.isDelayMode()) {
            this.flushDelayedPackets();
         }
      }

      if (this.disablePending) {
         this.disablePending = false;
         this.flushDelayedPackets();
         super.onDisable();
      }
   }

   private void flushDelayedPackets() {
      while (!this.delayedPackets.isEmpty()) {
         VelocityModule.Entry entry = this.delayedPackets.poll();
         if (entry != null) {
            try {
               entry.getPacket().handle(mc.getConnection());
            } catch (Throwable e) {
               if (!(e instanceof ClassCastException) && !(e instanceof RunningOnDifferentThreadException)) {
                  e.printStackTrace();
               }
            }
         }
      }

      this.delayingPackets = false;
   }

   private boolean isDelayMode() {
      return !this.mode.method2101(MODE_VANILLA);
   }

   /**
    * Vanilla: отбрасывание своего игрока (SetEntityMotion) отменяется целиком; у взрыва (в т.ч. заряда ветра)
    * отменяется только толчок — сам пакет выполняется копией без playerKnockback, звук и частицы остаются.
    */
   private void handleVanilla(PacketEvent event) {
      Packet<?> packet = event.getPacket();
      if (mc.player != null) {
         if (this.isLocalPlayerVelocityPacket(packet)) {
            event.cancel();
         } else if (packet instanceof ClientboundExplodePacket explodePacket && explodePacket.playerKnockback().isPresent()) {
            event.cancel();
            ClientboundExplodePacket explosionWithoutKnockback = new ClientboundExplodePacket(
               explodePacket.center(),
               explodePacket.radius(),
               explodePacket.blockCount(),
               Optional.empty(),
               explodePacket.explosionParticle(),
               explodePacket.explosionSound(),
               explodePacket.blockParticles(),
               explodePacket.playSound()
            );
            PacketListener packetListener = event.getPacketListener();
            ClientGamePacketListener connection = packetListener instanceof ClientGamePacketListener gamePacketListener ? gamePacketListener : mc.getConnection();
            if (connection != null) {
               try {
                  explosionWithoutKnockback.handle(connection);
               } catch (RunningOnDifferentThreadException e) {
                  // пакет поставлен в очередь главного потока, как обычный
               }
            }
         }
      }
   }

   private boolean isLocalPlayerVelocityPacket(Packet<?> packet) {
      return mc.player != null && packet instanceof ClientboundSetEntityMotionPacket setEntityMotionPacket && setEntityMotionPacket.id() == mc.player.getId();
   }

   public static class Entry {
      private final Packet<?> packet;
      private final long enqueueTime;

      public Entry(Packet<?> packet) {
         this.packet = packet;
         this.enqueueTime = System.currentTimeMillis();
      }

      public Packet<ClientGamePacketListener> getPacket() {
         return (Packet<ClientGamePacketListener>)this.packet;
      }

      @Generated
      public Packet<?> getPacket2() {
         return this.packet;
      }

      @Generated
      public long getTime() {
         return this.enqueueTime;
      }
   }
}
