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

      ReachV3Module.Entry entry;
      while ((entry = this.queue.poll()) != null) {
         this.method5536(entry);
      }

      this.method5537();
   }

   private void method5536(ReachV3Module.Entry entry) {
      try {
         entry.getPacket().handle(mc.getConnection());
      } catch (ClassCastException e) {
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   private void method5537() {
      this.vec3d = null;
      this.intValue = -32768 << 16 | 0;
      this.box = null;
      this.vec3d2 = null;
   }

   private void method5538(LivingEntity livingEntity) {
      Vec3 base = livingEntity.getPositionCodec().getBase();

      for (ReachV3Module.Entry entry : this.queue) {
         Vec3 vec = this.method5539(entry.getPacket(), livingEntity, base);
         if (vec != null) {
            base = vec;
         }
      }

      this.vec3d = base;
      this.vec3d2 = base;
      this.intValue = livingEntity.getId();
   }

   // 26.3: move deltas are a VecDelta decoded against a base position (was xa/ya/za / 4096)
   private static Vec3 zenith$decode(Vec3 base, ClientboundMoveEntityPacket moveEntityPacket) {
      if (!moveEntityPacket.hasPosition()) {
         return base;
      }

      net.minecraft.network.protocol.game.VecDeltaCodec vecDeltaCodec = new net.minecraft.network.protocol.game.VecDeltaCodec();
      vecDeltaCodec.setBase(base);
      return moveEntityPacket.getPositionDelta().decode(vecDeltaCodec).endPosition();
   }

   private Vec3 method5539(Packet<?> packet, Entity entity, Vec3 base) {
      int id = entity.getId();
      if (packet instanceof ClientboundTeleportEntityPacket teleportEntityPacket && teleportEntityPacket.id() == id) {
         return teleportEntityPacket.change().position();
      } else if (packet instanceof ClientboundEntityPositionSyncPacket positionSyncPacket && positionSyncPacket.id() == id) {
         return positionSyncPacket.position().endPosition();
      } else {
         return packet instanceof ClientboundMoveEntityPacket moveEntityPacket && moveEntityPacket.entityId == id
            ? zenith$decode(base, moveEntityPacket)
            : null;
      }
   }

   public void method5540(Vec3 vec3d) {
      if (vec3d != null && !this.queue.isEmpty()) {
         for (ReachV3Module.Entry entry : this.queue) {
            entry.setFlag(true);
            if (entry.getVec3d() != null && entry.getVec3d().equals(vec3d)) {
               break;
            }
         }
      }
   }

   @EventTarget
   public void method5541(UpdateEvent event) {
      LivingEntity livingEntity = this.method5542();
      if (livingEntity == null) {
         this.method5537();
      } else {
         if (this.vec3d == null || livingEntity.getId() != this.intValue || this.stopWatch.method7546(1000L)) {
            this.method5538(livingEntity);
         }

         Vec3 vec3d = this.vec3d;
         this.box = vec3d != null ? livingEntity.dimensions.makeBoundingBox(vec3d) : null;
      }
   }

   private LivingEntity method5542() {
      return TriggerBotModule.INSTANCE.method5598() != null ? TriggerBotModule.INSTANCE.method5598() : AuraModule.INSTANCE.method6358();
   }

   @EventTarget
   public void method5543(PlayerTickEvent event) {
      LivingEntity livingEntity = this.method5542();
      if (mc.player != null && livingEntity != null) {
         int id = livingEntity.getId();
         mc.player.getEyePosition(1.0F);
         mc.player.getBoundingBox();
         ReachV3Module.Entry entry = null;
         int n = 0;

         for (ReachV3Module.Entry entry2 : this.queue) {
            if (entry2.getVec3d() != null && entry2.getIntValue() == id && livingEntity.dimensions.makeBoundingBox(entry2.getVec3d()).intersects(mc.player.getBoundingBox())) {
               entry = entry2;
               if (n > 0) {
                  break;
               }

               n++;
            }
         }

         ReachV3Module.Entry entry2;
         if (entry != null) {
            entry2 = entry;
         } else {
            double d = Double.POSITIVE_INFINITY;
            ReachV3Module.Entry entry3 = null;

            for (ReachV3Module.Entry entry4 : this.queue) {
               double d2;
               if (entry4.getVec3d() != null && entry4.getIntValue() == id && (d2 = mc.player.distanceToSqr(entry4.vec3d)) < d) {
                  d = d2;
                  entry3 = entry4;
               }
            }

            entry2 = entry3;
         }

         Vec3 vec3d = entry2 != null ? entry2.getVec3d() : null;
         this.vec3d2 = vec3d;
         this.method5540(vec3d);
      } else {
         this.vec3d2 = null;
      }
   }

   @EventTarget
   public void method5544(WorldRenderEvent event) {
      if (this.box != null && this.renderBox.isEnabled()) {
         GlowUtil.method2230(this.box, -1, 1.0F);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100()) {
         if (this.stopWatch == null) {
            this.stopWatch = new StopWatch();
         }

         try {
            Packet packet3 = event.getPacket3();
            if (packet3 instanceof ClientboundDisconnectPacket || packet3 instanceof ClientboundPlayerPositionPacket) {
               this.stopWatch2.reset();
               this.stopWatch3.reset();
            }

            LivingEntity livingEntity = mc.level == null ? null : this.method5542();
            Vec3 vec3d = null;
            if (livingEntity != null) {
               if (livingEntity.getId() != this.intValue || this.vec3d == null) {
                  this.method5538(livingEntity);
               }

               Vec3 base = this.vec3d;
               Vec3 vec = base == null ? null : this.method5539(packet3, livingEntity, base);
               if (vec != null) {
                  this.vec3d = vec;
                  vec3d = vec;
                  this.stopWatch.reset();
               }
            }

            if (this.queue.isEmpty() && (!this.stopWatch2.method7546(200L) || mc.level == null || livingEntity == null || !this.stopWatch3.method7546(200L))) {
               return;
            }

            if (packet3 instanceof ServerboundChatPacket
               || packet3 instanceof ClientboundSetHealthPacket
               || packet3 instanceof ClientboundSoundPacket
               || packet3 instanceof ClientboundSystemChatPacket
               || packet3 instanceof ServerboundChatCommandPacket) {
               return;
            }

            event.cancel();
            this.queue.add(new ReachV3Module.Entry(packet3, vec3d, vec3d != null ? livingEntity.getId() : -32768 << 16 | 0));
            if (!this.isEnabled()) {
               mc.execute(this::releaseAll);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method5545(PacketProcessEndEvent event) {
      long now = System.currentTimeMillis();
      if (mc.level == null || mc.player == null) {
         this.queue.clear();
      }

      try {
         this.queue.removeIf(entry -> {
            boolean flag = (float)(now - entry.getTime()) >= this.delay.getValue3();
            boolean var5x = entry.isFlag();
            if (!flag && this.stopWatch2.method7546(200L) && !var5x && mc.level != null && this.method5542() != null) {
               return false;
            } else {
               this.method5536(entry);
               return true;
            }
         });
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3 vec3d) {
      this.vec3d = vec3d;
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

      public Entry(Packet<?> packet3, Vec3 vec) {
         this(packet3, vec, Integer.MIN_VALUE);
      }

      public Entry(Packet<?> packet, Vec3 vec3d, int intValue) {
         this.packet = packet;
         this.longValue = System.currentTimeMillis();
         this.vec3d = vec3d;
         this.intValue = intValue;
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
      public void setFlag(boolean flag) {
         this.flag = flag;
      }
   }
}
