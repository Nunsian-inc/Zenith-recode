package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "ReachV3",
   description = "module.backtrack.description",
   category = ModuleCategory.COMBAT
)
public final class ReachV3Module extends Module {
   public static final ReachV3Module INSTANCE = new ReachV3Module();
   private final SliderSetting delay = new SliderSetting("module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.backtrack.delay.desc", "ms");
   private final BooleanSetting renderBox = new BooleanSetting("module.backtrack.renderBox", "module.backtrack.renderBox.desc", true);
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private final Queue<ReachV3Module.Entry> queue = new ConcurrentLinkedQueue<>();
   private Vec3 vec3d = null;
   private volatile int intValue = -32768 << 16 | 0;
   private Vec3 vec3d2 = null;
   private StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private AABB box = null;

   @Override
   public void onDisable() {
      super.onDisable();
      mc.execute(this::releaseAll);
   }

   private void releaseAll() {
      if (mc.getConnection() == null) {
         this.queue.clear();
      }

      ReachV3Module.Entry var1;
      while ((var1 = this.queue.poll()) != null) {
         this.method5536(var1);
      }

      this.method5537();
   }

   private void method5536(ReachV3Module.Entry var1) {
      try {
         var1.getPacket().handle(mc.getConnection());
      } catch (ClassCastException var3) {
      } catch (Throwable var4) {
         var4.printStackTrace();
      }
   }

   private void method5537() {
      this.vec3d = null;
      this.intValue = -32768 << 16 | 0;
      this.box = null;
      this.vec3d2 = null;
   }

   private void method5538(LivingEntity var1) {
      Vec3 var2 = var1.getPositionCodec().getBase();

      for (ReachV3Module.Entry var4 : this.queue) {
         Vec3 var5 = this.method5539(var4.getPacket(), var1, var2);
         if (var5 != null) {
            var2 = var5;
         }
      }

      this.vec3d = var2;
      this.vec3d2 = var2;
      this.intValue = var1.getId();
   }

   // 26.3: move deltas are a VecDelta decoded against a base position (was xa/ya/za / 4096)
   private static Vec3 zenith$decode(Vec3 var0, ClientboundMoveEntityPacket var1) {
      if (!var1.hasPosition()) {
         return var0;
      }

      net.minecraft.network.protocol.game.VecDeltaCodec var2 = new net.minecraft.network.protocol.game.VecDeltaCodec();
      var2.setBase(var0);
      return var1.getPositionDelta().decode(var2).endPosition();
   }

   private Vec3 method5539(Packet<?> var1, Entity var2, Vec3 var3) {
      int var4 = var2.getId();
      if (var1 instanceof ClientboundTeleportEntityPacket var5 && var5.id() == var4) {
         return var5.change().position();
      } else if (var1 instanceof ClientboundEntityPositionSyncPacket var6 && var6.id() == var4) {
         return var6.position().endPosition();
      } else {
         return var1 instanceof ClientboundMoveEntityPacket var7 && var7.entityId == var4
            ? zenith$decode(var3, var7)
            : null;
      }
   }

   public void method5540(Vec3 var1) {
      if (var1 != null && !this.queue.isEmpty()) {
         for (ReachV3Module.Entry var3 : this.queue) {
            var3.setFlag(true);
            if (var3.getVec3d() != null && var3.getVec3d().equals(var1)) {
               break;
            }
         }
      }
   }

   @EventTarget
   public void method5541(UpdateEvent var1) {
      LivingEntity var2 = this.method5542();
      if (var2 == null) {
         this.method5537();
      } else {
         if (this.vec3d == null || var2.getId() != this.intValue || this.stopWatch.method7546(1000L)) {
            this.method5538(var2);
         }

         Vec3 var3 = this.vec3d;
         this.box = var3 != null ? var2.dimensions.makeBoundingBox(var3) : null;
      }
   }

   private LivingEntity method5542() {
      return TriggerBotModule.INSTANCE.method5598() != null ? TriggerBotModule.INSTANCE.method5598() : AuraModule.INSTANCE.method6358();
   }

   @EventTarget
   public void method5543(PlayerTickEvent var1) {
      LivingEntity var2 = this.method5542();
      if (mc.player != null && var2 != null) {
         int var3 = var2.getId();
         mc.player.getEyePosition(1.0F);
         mc.player.getBoundingBox();
         ReachV3Module.Entry var7 = null;
         int var8 = 0;

         for (ReachV3Module.Entry var10 : this.queue) {
            if (var10.getVec3d() != null && var10.getIntValue() == var3 && var2.dimensions.makeBoundingBox(var10.getVec3d()).intersects(mc.player.getBoundingBox())) {
               var7 = var10;
               if (var8 > 0) {
                  break;
               }

               var8++;
            }
         }

         ReachV3Module.Entry var6;
         if (var7 != null) {
            var6 = var7;
         } else {
            double var16 = Double.POSITIVE_INFINITY;
            ReachV3Module.Entry var11 = null;

            for (ReachV3Module.Entry var13 : this.queue) {
               double var14;
               if (var13.getVec3d() != null && var13.getIntValue() == var3 && (var14 = mc.player.distanceToSqr(var13.vec3d)) < var16) {
                  var16 = var14;
                  var11 = var13;
               }
            }

            var6 = var11;
         }

         Vec3 var17 = var6 != null ? var6.getVec3d() : null;
         this.vec3d2 = var17;
         this.method5540(var17);
      } else {
         this.vec3d2 = null;
      }
   }

   @EventTarget
   public void method5544(WorldRenderEvent var1) {
      if (this.box != null && this.renderBox.isEnabled()) {
         GlowUtil.method2230(this.box, -1, 1.0F);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (this.stopWatch == null) {
            this.stopWatch = new StopWatch();
         }

         try {
            Packet var2 = var1.getPacket3();
            if (var2 instanceof ClientboundDisconnectPacket || var2 instanceof ClientboundPlayerPositionPacket) {
               this.stopWatch2.reset();
               this.stopWatch3.reset();
            }

            LivingEntity var3 = mc.level == null ? null : this.method5542();
            Vec3 var4 = null;
            if (var3 != null) {
               if (var3.getId() != this.intValue || this.vec3d == null) {
                  this.method5538(var3);
               }

               Vec3 var5 = this.vec3d;
               Vec3 var6 = var5 == null ? null : this.method5539(var2, var3, var5);
               if (var6 != null) {
                  this.vec3d = var6;
                  var4 = var6;
                  this.stopWatch.reset();
               }
            }

            if (this.queue.isEmpty() && (!this.stopWatch2.method7546(200L) || mc.level == null || var3 == null || !this.stopWatch3.method7546(200L))) {
               return;
            }

            if (var2 instanceof ServerboundChatPacket
               || var2 instanceof ClientboundSetHealthPacket
               || var2 instanceof ClientboundSoundPacket
               || var2 instanceof ClientboundSystemChatPacket
               || var2 instanceof ServerboundChatCommandPacket) {
               return;
            }

            var1.cancel();
            this.queue.add(new ReachV3Module.Entry(var2, var4, var4 != null ? var3.getId() : -32768 << 16 | 0));
            if (!this.isEnabled()) {
               mc.execute(this::releaseAll);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method5545(PacketProcessEndEvent var1) {
      long var2 = System.currentTimeMillis();
      if (mc.level == null || mc.player == null) {
         this.queue.clear();
      }

      try {
         this.queue.removeIf(var3 -> {
            boolean var4 = (float)(var2 - var3.getTime()) >= this.delay.getValue3();
            boolean var5x = var3.isFlag();
            if (!var4 && this.stopWatch2.method7546(200L) && !var5x && mc.level != null && this.method5542() != null) {
               return false;
            } else {
               this.method5536(var3);
               return true;
            }
         });
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3 var1) {
      this.vec3d = var1;
   }

   @Generated
   public Vec3 getVec3d2() {
      return this.vec3d2;
   }

   public static class Entry {
      private final Packet<?> packet;
      private final long longValue;
      private final Vec3 vec3d;
      private final int intValue;
      private boolean flag;

      public Entry(Packet<?> var1, Vec3 var2) {
         this(var1, var2, Integer.MIN_VALUE);
      }

      public Entry(Packet<?> var1, Vec3 var2, int var3) {
         this.packet = var1;
         this.longValue = System.currentTimeMillis();
         this.vec3d = var2;
         this.intValue = var3;
         this.flag = false;
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

      @Generated
      public Vec3 getVec3d() {
         return this.vec3d;
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }

      @Generated
      public boolean isFlag() {
         return this.flag;
      }

      @Generated
      public void setFlag(boolean var1) {
         this.flag = var1;
      }
   }
}
