package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Generated;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "Velocity",
   category = ModuleCategory.MOVEMENT,
   description = "module.velocity.description"
)
public final class VelocityModule extends Module {
   public static final VelocityModule INSTANCE = new VelocityModule();
   private static final long CONST_longValue = 2000L;
   private final SliderSetting delay = new SliderSetting("module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.backtrack.delay.desc", "ms");
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
   public void onPacket(PacketEvent var1) {
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
      } else if (var1.method7100()) {
         try {
            Packet var2 = var1.getPacket3();
            if (var2 instanceof ClientboundDisconnectPacket) {
               this.method2625();
               return;
            }

            if (var2 instanceof ClientboundPlayerPositionPacket) {
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
               if (!this.method2626(var2) || mc.player.onGround()) {
                  return;
               }

               this.flag = true;
               this.stopWatch.reset();
            }

            if (var2 instanceof ServerboundChatPacket
               || var2 instanceof ClientboundSetHealthPacket
               || var2 instanceof ClientboundSoundPacket
               || var2 instanceof ClientboundSystemChatPacket
               || var2 instanceof ServerboundChatCommandPacket) {
               return;
            }

            var1.cancel();
            this.queue.add(new VelocityModule.Entry(var2));
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method2624(PacketProcessEndEvent var1) {
      if (mc.level == null || mc.player == null) {
         this.queue.clear();
         this.flag = false;
      }

      if (this.flag && !this.queue.isEmpty()) {
         boolean var2 = mc.player != null && mc.player.onGround();
         boolean var3 = this.stopWatch.method7546((long)this.delay.getValue3());
         if (var2 || var3) {
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
         VelocityModule.Entry var1 = this.queue.poll();
         if (var1 != null) {
            try {
               var1.getPacket().handle(mc.getConnection());
            } catch (Throwable var3) {
               if (!(var3 instanceof ClassCastException) && !(var3 instanceof RunningOnDifferentThreadException)) {
                  var3.printStackTrace();
               }
            }
         }
      }

      this.flag = false;
   }

   private boolean method2626(Packet<?> var1) {
      return mc.player != null && var1 instanceof ClientboundSetEntityMotionPacket var2 && var2.id() == mc.player.getId();
   }

   public static class Entry {
      private final Packet<?> packet;
      private final long longValue;

      public Entry(Packet<?> var1) {
         this.packet = var1;
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
