package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.OutgoingPacketEvent;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.BlinkUtils;

@ModuleInfo(
   name = "Blink",
   description = "module.blink.description",
   category = ModuleCategory.COMBAT
)
public final class BlinkModule extends Module {
   public static final BlinkModule INSTANCE = new BlinkModule();
   private static final long CONST_longValue = 1000L;
   private static final double CONST_doubleValue = 1.5;
   private final SliderSetting packetsPerSecond = new SliderSetting(
      "module.blink.packetsPerSecond", 283.0F, 1.0F, 500.0F, 1.0F, "module.blink.packetsPerSecond.desc", "pps"
   );
   private final Queue<BlinkModule.Data> queue = new ConcurrentLinkedQueue<>();
   private final Set<Integer> items = new HashSet<>();
   private volatile boolean flag;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private boolean flag2;
   private boolean flag3;
   private long longValue;
   private long longValue2;
   private double doubleValue;
   private Vec3 vec3d;

   private BlinkModule() {
   }

   @Override
   public void onEnable() {
      this.queue.clear();
      this.method6748();
      if (!this.flag3) {
         this.flag3 = true;
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      if (this.flag3) {
         if (!this.queue.isEmpty() && mc.getConnection() != null) {
            this.method6735();
            this.method6736();
         } else {
            this.method6745();
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && event.getPacket() != null) {
         if (mc.player != null && !mc.player.isDeadOrDying()) {
            if (event.getPacket() instanceof ClientboundDisconnectPacket) {
               this.method6746();
            } else if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
               this.method6747();
            }
         } else {
            if (this.atomicBoolean.compareAndSet(false, true)) {
               mc.execute(this::method6729);
            }
         }
      }
   }

   private void method6729() {
      this.atomicBoolean.set(false);
      if (mc.player != null && mc.getConnection() != null) {
         this.method6747();

         BlinkModule.Data data;
         while ((data = this.queue.poll()) != null) {
            this.method6740(data);
         }
      } else {
         this.method6746();
      }

      if (this.isEnabled()) {
         this.setToggled(false);
      } else if (this.flag) {
         this.method6745();
      }
   }

   @EventTarget
   public void method6730(PacketProcessEndEvent event) {
      if (this.flag) {
         this.method6736();
      }
   }

   @EventTarget(0)
   public void method6731(OutgoingPacketEvent event) {
      if (!event.isCancelled() && !this.flag2 && event.getPacket4() != null) {
         Packet packet4 = event.getPacket4();
         if (this.flag) {
            event.cancel();
            this.method6734(packet4);
         } else if (mc.player == null || mc.level == null) {
            this.method6746();
         } else if (!this.method6733(packet4)) {
            event.cancel();
            this.method6734(packet4);
         }
      }
   }

   public boolean method6732() {
      return this.flag || this.flag2;
   }

   private boolean method6733(Packet<?> packet4) {
      return packet4 instanceof ServerboundChatPacket || packet4 instanceof ServerboundChatCommandPacket || packet4 instanceof ServerboundResourcePackPacket;
   }

   private void method6734(Packet<?> packet4) {
      this.queue.add(new BlinkModule.Data(packet4, this.method6744(packet4)));
   }

   private void method6735() {
      this.flag = true;
      this.flag2 = false;
      this.longValue2 = System.currentTimeMillis();
      this.longValue = 0L;
      this.doubleValue = Math.max(1.0, this.method6739());
      this.vec3d = null;
      this.items.clear();
   }

   private void method6736() {
      if (this.flag) {
         if (mc.getConnection() == null || mc.level == null) {
            this.method6745();
         } else if (this.queue.isEmpty()) {
            this.method6745();
         } else {
            long now = System.currentTimeMillis();
            if (this.longValue > now) {
               this.longValue2 = now;
            } else {
               if (this.longValue != 0L) {
                  this.longValue = 0L;
                  this.longValue2 = now;
                  this.doubleValue = Math.max(this.doubleValue, 1.0);
               } else {
                  this.method6737(now);
               }

               while (this.doubleValue >= 1.0 && !this.queue.isEmpty()) {
                  BlinkModule.Data data = this.queue.poll();
                  this.method6740(data);
                  this.doubleValue--;
                  if (data.getVec3d() != null && this.method6741(data.getVec3d())) {
                     this.longValue = System.currentTimeMillis() + 200L;
                     this.longValue2 = System.currentTimeMillis();
                     this.doubleValue = 0.0;
                     break;
                  }
               }

               if (this.queue.isEmpty()) {
                  this.method6745();
               }
            }
         }
      }
   }

   private void method6737(long now) {
      long l = Math.max(0L, now - this.longValue2);
      this.longValue2 = now;
      this.doubleValue = this.doubleValue + this.method6738() * (l / 1000.0);
      this.doubleValue = Math.min(this.doubleValue, this.method6738());
   }

   private double method6738() {
      return Math.max(1.0, (double)(this.packetsPerSecond.getValue3() * 2.0F));
   }

   private double method6739() {
      return this.method6738() / 20.0;
   }

   private void method6740(BlinkModule.Data data) {
      if (mc.getConnection() != null) {
         this.flag2 = true;

         try {
            mc.getConnection().send(data.getPacket());
         } finally {
            this.flag2 = false;
         }
      }
   }

   private boolean method6741(Vec3 vec3d) {
      Vec3 vec = this.vec3d == null ? vec3d : this.vec3d;
      this.vec3d = vec3d;
      return this.method6742(vec, vec3d);
   }

   private boolean method6742(Vec3 vec, Vec3 vec3d) {
      if (mc.level == null) {
         return false;
      } else {
         AABB inflate = new AABB(
               Math.min(vec.x, vec3d.x),
               Math.min(vec.y, vec3d.y),
               Math.min(vec.z, vec3d.z),
               Math.max(vec.x, vec3d.x),
               Math.max(vec.y, vec3d.y),
               Math.max(vec.z, vec3d.z)
            )
            .inflate(1.5);
         boolean flag = false;
         double d = 2.25;

         for (ItemEntity itemEntity : mc.level.getEntitiesOfClass(ItemEntity.class, inflate, itemEntity2 -> !itemEntity2.isRemoved())) {
            if (!this.items.contains(itemEntity.getId()) && this.method6743(itemEntity.position(), vec, vec3d) <= d) {
               this.items.add(itemEntity.getId());
               flag = true;
            }
         }

         return flag;
      }
   }

   private double method6743(Vec3 position, Vec3 vec, Vec3 vec3d) {
      double d = vec3d.x - vec.x;
      double d2 = vec3d.y - vec.y;
      double d3 = vec3d.z - vec.z;
      double d4 = d * d + d2 * d2 + d3 * d3;
      if (d4 <= 1.0E-6) {
         return position.distanceToSqr(vec3d);
      } else {
         double d5 = ((position.x - vec.x) * d + (position.y - vec.y) * d2 + (position.z - vec.z) * d3) / d4;
         d5 = Math.max(0.0, Math.min(1.0, d5));
         double d6 = vec.x + d * d5;
         double d7 = vec.y + d2 * d5;
         double d8 = vec.z + d3 * d5;
         double d9 = position.x - d6;
         double d10 = position.y - d7;
         double d11 = position.z - d8;
         return d9 * d9 + d10 * d10 + d11 * d11;
      }
   }

   private Vec3 method6744(Packet<?> packet4) {
      if (packet4 instanceof ServerboundMovePlayerPacket movePlayerPacket && mc.player != null) {
         double x = movePlayerPacket.getX(Double.NaN);
         double y = movePlayerPacket.getY(Double.NaN);
         double z = movePlayerPacket.getZ(Double.NaN);
         return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) ? new Vec3(x, y, z) : null;
      } else {
         return null;
      }
   }

   private void method6745() {
      this.method6746();
      this.method6748();
      if (this.flag3) {
         this.flag3 = false;
         super.onDisable();
      }
   }

   private void method6746() {
      this.queue.clear();
   }

   private void method6747() {
      this.queue.removeIf(data -> BlinkUtils.method1444(data.getPacket()));
   }

   private void method6748() {
      this.flag = false;
      this.flag2 = false;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.doubleValue = 0.0;
      this.vec3d = null;
      this.items.clear();
   }

   private static final class Data {
      private final Packet<?> packet;
      private final Vec3 vec3d;

      public Packet<?> packet() {
         return this.packet;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.packet, other.packet) && java.util.Objects.equals(this.vec3d, other.vec3d);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(packet, vec3d);
      }

      @Override
      public String toString() {
         return "Data[" + "packet=" + this.packet + ", " + "vec3d=" + this.vec3d + "]";
      }

      private Data(Packet<?> packet, Vec3 vec3d) {
         this.packet = packet;
         this.vec3d = vec3d;
      }

      public Packet<?> getPacket() {
         return this.packet;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }
   }
}
