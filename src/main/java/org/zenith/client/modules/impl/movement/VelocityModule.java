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
   private static final long CONST_longValue = 2000L;
   private static final String MODE_DELAY = "module.velocity.mode.delay";
   private static final String MODE_VANILLA = "module.velocity.mode.vanilla";
   /** Delay — прежняя логика (придержать пакеты после отбрасывания в воздухе), Vanilla — отменить отбрасывание целиком. */
   private final ModeSetting mode = new ModeSetting("module.velocity.mode", "module.velocity.mode.desc", MODE_DELAY, MODE_VANILLA);
   private final SliderSetting delay = new SliderSetting(
      "module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.velocity.delay.desc", "ms", this::isDelayMode
   );
   private final Queue<VelocityModule.Entry> queue = new ConcurrentLinkedQueue<>();
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag;
   private boolean flag2;
   private long longValue;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();

   private VelocityModule() {
   }

   @Override
   public void onEnable() {
      this.flag2 = false;
      if (ReachV3Module.INSTANCE.isEnabled()) {
         ChatLogger.method2019("Не работает с ReachV3");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag2 = true;
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (ReachV3Module.INSTANCE.isEnabled()) {
         if (this.atomicBoolean.compareAndSet(false, true)) {
            mc.execute(() -> {
               this.atomicBoolean.set(false);
               if (this.isEnabled() && ReachV3Module.INSTANCE.isEnabled()) {
                  ChatLogger.method2019("Не работает с ReachV3");
                  this.setToggled(false);
               }
            });
         }
      } else if (event.method7100()) {
         if (!this.isDelayMode()) {
            this.handleVanilla(event);
            return;
         }

         try {
            Packet packet3 = event.getPacket3();
            if (packet3 instanceof ClientboundDisconnectPacket) {
               this.method2625();
               return;
            }

            if (packet3 instanceof ClientboundPlayerPositionPacket) {
               this.method2625();
               this.longValue = System.currentTimeMillis() + 2000L;
               return;
            }

            if (mc.player == null || mc.level == null) {
               return;
            }

            if (System.currentTimeMillis() < this.longValue) {
               return;
            }

            if (!this.flag) {
               if (!this.method2626(packet3) || mc.player.onGround()) {
                  return;
               }

               this.flag = true;
               this.stopWatch.reset();
            }

            if (packet3 instanceof ServerboundChatPacket
               || packet3 instanceof ClientboundSetHealthPacket
               || packet3 instanceof ClientboundSoundPacket
               || packet3 instanceof ClientboundSystemChatPacket
               || packet3 instanceof ServerboundChatCommandPacket) {
               return;
            }

            event.cancel();
            this.queue.add(new VelocityModule.Entry(packet3));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method2624(PacketProcessEndEvent event) {
      if (mc.level == null || mc.player == null) {
         this.queue.clear();
         this.flag = false;
      }

      if (this.flag && !this.queue.isEmpty()) {
         boolean flag = mc.player != null && mc.player.onGround();
         boolean flag2 = this.stopWatch.method7546((long)this.delay.getValue3());
         if (flag || flag2 || !this.isDelayMode()) {
            this.method2625();
         }
      }

      if (this.flag2) {
         this.flag2 = false;
         this.method2625();
         super.onDisable();
      }
   }

   private void method2625() {
      while (!this.queue.isEmpty()) {
         VelocityModule.Entry entry = this.queue.poll();
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

      this.flag = false;
   }

   private boolean isDelayMode() {
      return !this.mode.method2101(MODE_VANILLA);
   }

   /**
    * Vanilla: отбрасывание своего игрока (SetEntityMotion) отменяется целиком; у взрыва (в т.ч. заряда ветра)
    * отменяется только толчок — сам пакет выполняется копией без playerKnockback, звук и частицы остаются.
    */
   private void handleVanilla(PacketEvent event) {
      Packet<?> packet3 = event.getPacket3();
      if (mc.player != null) {
         if (this.method2626(packet3)) {
            event.cancel();
         } else if (packet3 instanceof ClientboundExplodePacket explodePacket && explodePacket.playerKnockback().isPresent()) {
            event.cancel();
            ClientboundExplodePacket explodePacket2 = new ClientboundExplodePacket(
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
                  explodePacket2.handle(connection);
               } catch (RunningOnDifferentThreadException e) {
                  // пакет поставлен в очередь главного потока, как обычный
               }
            }
         }
      }
   }

   private boolean method2626(Packet<?> packet3) {
      return mc.player != null && packet3 instanceof ClientboundSetEntityMotionPacket setEntityMotionPacket && setEntityMotionPacket.id() == mc.player.getId();
   }

   public static class Entry {
      private final Packet<?> packet;
      private final long longValue;

      public Entry(Packet<?> packet) {
         this.packet = packet;
         this.longValue = System.currentTimeMillis();
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
         return this.longValue;
      }
   }
}
