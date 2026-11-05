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
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() != null) {
         if (mc.player != null && !mc.player.isDeadOrDying()) {
            if (var1.getPacket3() instanceof ClientboundDisconnectPacket) {
               this.method6746();
            } else if (var1.getPacket3() instanceof ClientboundPlayerPositionPacket) {
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

         BlinkModule.Data var1;
         while ((var1 = this.queue.poll()) != null) {
            this.method6740(var1);
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
   public void method6730(PacketProcessEndEvent var1) {
      if (this.flag) {
         this.method6736();
      }
   }

   @EventTarget(0)
   public void method6731(OutgoingPacketEvent var1) {
      if (!var1.isCancelled() && !this.flag2 && var1.getPacket4() != null) {
         Packet var2 = var1.getPacket4();
         if (this.flag) {
            var1.cancel();
            this.method6734(var2);
         } else if (mc.player == null || mc.level == null) {
            this.method6746();
         } else if (!this.method6733(var2)) {
            var1.cancel();
            this.method6734(var2);
         }
      }
   }

   public boolean method6732() {
      return this.flag || this.flag2;
   }

   private boolean method6733(Packet<?> var1) {
      return var1 instanceof ServerboundChatPacket || var1 instanceof ServerboundChatCommandPacket || var1 instanceof ServerboundResourcePackPacket;
   }

   private void method6734(Packet<?> var1) {
      this.queue.add(new BlinkModule.Data(var1, this.method6744(var1)));
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
            long var1 = System.currentTimeMillis();
            if (this.longValue > var1) {
               this.longValue2 = var1;
            } else {
               if (this.longValue != 0L) {
                  this.longValue = 0L;
                  this.longValue2 = var1;
                  this.doubleValue = Math.max(this.doubleValue, 1.0);
               } else {
                  this.method6737(var1);
               }

               while (this.doubleValue >= 1.0 && !this.queue.isEmpty()) {
                  BlinkModule.Data var3 = this.queue.poll();
                  this.method6740(var3);
                  this.doubleValue--;
                  if (var3.getVec3d() != null && this.method6741(var3.getVec3d())) {
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

   private void method6737(long var1) {
      long var3 = Math.max(0L, var1 - this.longValue2);
      this.longValue2 = var1;
      this.doubleValue = this.doubleValue + this.method6738() * (var3 / 1000.0);
      this.doubleValue = Math.min(this.doubleValue, this.method6738());
   }

   private double method6738() {
      return Math.max(1.0, (double)(this.packetsPerSecond.getValue3() * 2.0F));
   }

   private double method6739() {
      return this.method6738() / 20.0;
   }

   private void method6740(BlinkModule.Data var1) {
      if (mc.getConnection() != null) {
         this.flag2 = true;

         try {
            mc.getConnection().send(var1.getPacket());
         } finally {
            this.flag2 = false;
         }
      }
   }

   private boolean method6741(Vec3 var1) {
      Vec3 var2 = this.vec3d == null ? var1 : this.vec3d;
      this.vec3d = var1;
      return this.method6742(var2, var1);
   }

   private boolean method6742(Vec3 var1, Vec3 var2) {
      if (mc.level == null) {
         return false;
      } else {
         AABB var3 = new AABB(
               Math.min(var1.x, var2.x),
               Math.min(var1.y, var2.y),
               Math.min(var1.z, var2.z),
               Math.max(var1.x, var2.x),
               Math.max(var1.y, var2.y),
               Math.max(var1.z, var2.z)
            )
            .inflate(1.5);
         boolean var4 = false;
         double var5 = 2.25;

         for (ItemEntity var8 : mc.level.getEntitiesOfClass(ItemEntity.class, var3, var0 -> !var0.isRemoved())) {
            if (!this.items.contains(var8.getId()) && this.method6743(var8.position(), var1, var2) <= var5) {
               this.items.add(var8.getId());
               var4 = true;
            }
         }

         return var4;
      }
   }

   private double method6743(Vec3 var1, Vec3 var2, Vec3 var3) {
      double var4 = var3.x - var2.x;
      double var6 = var3.y - var2.y;
      double var8 = var3.z - var2.z;
      double var10 = var4 * var4 + var6 * var6 + var8 * var8;
      if (var10 <= 1.0E-6) {
         return var1.distanceToSqr(var3);
      } else {
         double var12 = ((var1.x - var2.x) * var4 + (var1.y - var2.y) * var6 + (var1.z - var2.z) * var8) / var10;
         var12 = Math.max(0.0, Math.min(1.0, var12));
         double var14 = var2.x + var4 * var12;
         double var16 = var2.y + var6 * var12;
         double var18 = var2.z + var8 * var12;
         double var20 = var1.x - var14;
         double var22 = var1.y - var16;
         double var24 = var1.z - var18;
         return var20 * var20 + var22 * var22 + var24 * var24;
      }
   }

   private Vec3 method6744(Packet<?> var1) {
      if (var1 instanceof ServerboundMovePlayerPacket var2 && mc.player != null) {
         double var3 = var2.getX(Double.NaN);
         double var5 = var2.getY(Double.NaN);
         double var7 = var2.getZ(Double.NaN);
         return !Double.isNaN(var3) && !Double.isNaN(var5) && !Double.isNaN(var7) ? new Vec3(var3, var5, var7) : null;
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
      this.queue.removeIf(var0 -> BlinkUtils.method1444(var0.getPacket()));
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

      private Data(Packet<?> var1, Vec3 var2) {
         this.packet = var1;
         this.vec3d = var2;
      }

      public Packet<?> getPacket() {
         return this.packet;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }
   }
}
