package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.OutgoingPacketEvent;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.network.BlinkUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "FakeLag",
   description = "module.fakelag.description",
   category = ModuleCategory.COMBAT
)
public final class FakeLagModule extends Module {
   public static final FakeLagModule INSTANCE = new FakeLagModule();
   private static final List<String> items = List.of(
      "module.aura.targetPlayers", "module.aura.noarmor", "module.aura.targetHostile", "module.aura.targetPeaceful"
   );
   private final ModeSetting mode = new ModeSetting("module.fakeLag.mode", "module.fakeLag.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.fakeLag.mode.constant");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.fakeLag.mode.dynamic").method2103();
   private final SliderSetting minRange = new SliderSetting("module.fakeLag.minRange", 2.0F, 0.0F, 10.0F, 0.1F, "module.fakeLag.minRange.desc", "b");
   private final SliderSetting maxRange = new SliderSetting("module.fakeLag.maxRange", 5.0F, 0.0F, 10.0F, 0.1F, "module.fakeLag.maxRange.desc", "b");
   private final SliderSetting minDelay = new SliderSetting("module.fakeLag.minDelay", 300.0F, 0.0F, 1000.0F, 25.0F, "module.fakeLag.minDelay.desc", "ms");
   private final SliderSetting maxDelay = new SliderSetting("module.fakeLag.maxDelay", 600.0F, 0.0F, 1000.0F, 25.0F, "module.fakeLag.maxDelay.desc", "ms");
   private final SliderSetting recoilTime = new SliderSetting("module.fakeLag.recoilTime", 250.0F, 0.0F, 1000.0F, 25.0F, "module.fakeLag.recoilTime.desc", "ms");
   private final MultiSelectSetting flushOn = new MultiSelectSetting("module.fakeLag.flushOn", "module.fakeLag.flushOn.desc");
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.flushOn, "module.fakeLag.flushEntityInteract", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.flushOn, "module.fakeLag.flushBlockInteract", true);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.flushOn, "module.fakeLag.flushAction", true);
   private final BooleanSetting renderBox = new BooleanSetting("module.fakeLag.renderBox", "module.fakeLag.renderBox.desc", true);
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 2;
   private static final int CONST_intValue3 = 4;
   private final Queue<FakeLagModule.Data> queue = new ConcurrentLinkedQueue<>();
   private final StopWatch stopWatch = new StopWatch();
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private long longValue = this.method6950();
   private boolean flag;
   private volatile boolean flag2;

   private FakeLagModule() {
   }

   @Override
   public void onEnable() {
      this.queue.clear();
      this.atomicInteger.set(0);
      this.flag = false;
      this.flag2 = false;
      this.longValue = this.method6950();
      this.stopWatch.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method6956();
      this.method6958();
      this.flag = false;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         this.flag = this.method6943(mc.player.position()) != null;
      } else {
         this.flag = false;
         this.method6959();
      }
   }

   @EventTarget
   public void method6934(PacketProcessEndEvent event) {
      if (mc.player != null && mc.level != null) {
         this.method6956();
         if (!this.queue.isEmpty()) {
            if (this.method6941() || !this.stopWatch.hasElapsed((long)this.recoilTime.getValue3())) {
               this.method6953();
            } else if (this.method6949()) {
               this.method6954();
            } else {
               if (this.method6951() || this.method6942()) {
                  this.method6953();
               }
            }
         }
      } else {
         this.atomicInteger.set(0);
         this.method6959();
      }
   }

   @EventTarget
   public void method6935(WorldRenderEvent event) {
      if (this.renderBox.isEnabled() && mc.player != null) {
         Vec3 pos = this.method6948();
         if (pos != null) {
            GlowUtil.method2230(mc.player.getDimensions(mc.player.getPose()).makeBoundingBox(pos), -1, 1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.getPacket() != null) {
         if (event.isIncoming()) {
            this.method6938(event.getPacket());
         }
      }
   }

   @EventTarget(3)
   public void method6936(OutgoingPacketEvent event) {
      if (!event.isCancelled() && !this.flag2 && !BlinkModule.INSTANCE.method6732() && !TrapTpModule.INSTANCE.isFlag6() && event.getPacket4() != null) {
         if (mc.player != null && mc.level != null) {
            boolean sameThread;
            if (sameThread = mc.isSameThread()) {
               this.method6956();
            }

            Packet packet4 = event.getPacket4();
            int x;
            if ((x = this.method6937(packet4)) >= 0) {
               if (!sameThread && !this.queue.isEmpty()) {
                  event.cancel();
                  this.queue.add(new FakeLagModule.Data(packet4, this.method6947(packet4)));
               }

               this.method6955(x);
            } else if (!this.method6939(packet4)) {
               event.cancel();
               this.queue.add(new FakeLagModule.Data(packet4, this.method6947(packet4)));
            }
         } else {
            this.method6959();
         }
      }
   }

   private int method6937(Packet<?> packet4) {
      if (this.method6941() || !this.stopWatch.hasElapsed((long)this.recoilTime.getValue3())) {
         return 0;
      } else if (this.method6949()) {
         return 4;
      } else if (this.method6940(packet4)) {
         return 2;
      } else {
         return !this.method6951() && !this.method6942() ? -1 : 0;
      }
   }

   private void method6938(Packet<?> packet3) {
      if (packet3 instanceof ClientboundDisconnectPacket) {
         this.method6959();
         this.stopWatch.reset();
      } else if (packet3 instanceof ClientboundPlayerPositionPacket) {
         this.queue.removeIf(data -> BlinkUtils.method1444(data.getPacket()));
         this.method6952();
      } else if (packet3 instanceof ClientboundSetHealthPacket) {
         this.method6952();
      } else if (!(
         packet3 instanceof ClientboundSetEntityMotionPacket setEntityMotionPacket
            && mc.player != null
            && setEntityMotionPacket.id() == mc.player.getId()
            && (setEntityMotionPacket.movement().x != 0.0 || setEntityMotionPacket.movement().y != 0.0 || setEntityMotionPacket.movement().z != 0.0)
      )) {
         if (packet3 instanceof ClientboundExplodePacket explodePacket) {
            explodePacket.playerKnockback().ifPresent(var1x -> {
               if (var1x.x != 0.0 || var1x.y != 0.0 || var1x.z != 0.0) {
                  this.method6952();
               }
            });
         }
      } else {
         this.method6952();
      }
   }

   private boolean method6939(Packet<?> packet4) {
      return packet4 instanceof ServerboundChatPacket || packet4 instanceof ServerboundChatCommandPacket;
   }

   private boolean method6940(Packet<?> packet4) {
      if (packet4 instanceof ServerboundResourcePackPacket) {
         return true;
      } else if (!this.entry3.isEnabled() || !(packet4 instanceof ServerboundInteractPacket) && !(packet4 instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) && !(packet4 instanceof ServerboundPunchPacket)) {
         return !this.entry4.isEnabled() || !(packet4 instanceof ServerboundUseItemOnPacket) && !(packet4 instanceof ServerboundSignUpdatePacket)
            ? this.entry5.isEnabled() && packet4 instanceof ServerboundPlayerActionPacket
            : true;
      } else {
         return true;
      }
   }

   private boolean method6941() {
      return mc.player == null || mc.level == null || mc.player.isDeadOrDying() || mc.player.isInWater() || mc.gui.screen() != null;
   }

   private boolean method6942() {
      if (this.entry.isSelected()) {
         return false;
      } else if (this.entry2.isSelected() && this.flag) {
         Vec3 pos = this.method6948();
         if (pos == null) {
            return false;
         } else if (mc.player == null) {
            return true;
         } else {
            LivingEntity livingEntity = this.method6944(pos);
            if (livingEntity == null) {
               return true;
            } else {
               AABB boundingBox = mc.player.getDimensions(mc.player.getPose()).makeBoundingBox(pos);
               boolean intersects = livingEntity.getBoundingBox().intersects(boundingBox);
               double d = livingEntity.position().distanceTo(pos);
               double d2 = livingEntity.position().distanceTo(mc.player.position());
               return d < d2 || intersects;
            }
         }
      } else {
         return true;
      }
   }

   private LivingEntity method6943(Vec3 position) {
      double d = Math.min(this.minRange.getValue3(), this.maxRange.getValue3());
      double d2 = Math.max(this.minRange.getValue3(), this.maxRange.getValue3());
      double d3 = d * d;
      double d4 = d2 * d2;
      LivingEntity living = null;
      double d5 = Double.MAX_VALUE;

      for (LivingEntity livingEntity : this.method6945(position)) {
         double d6;
         if (!this.method6946(livingEntity) && (d6 = livingEntity.distanceToSqr(position)) >= d3 && d6 <= d4 && d6 < d5) {
            living = livingEntity;
            d5 = d6;
         }
      }

      return living;
   }

   private LivingEntity method6944(Vec3 pos) {
      double d;
      double d2 = (d = Math.max(this.minRange.getValue3(), this.maxRange.getValue3())) * d;
      LivingEntity living = null;
      double d3 = Double.MAX_VALUE;

      for (LivingEntity livingEntity : this.method6945(pos)) {
         double d4;
         if (!this.method6946(livingEntity) && (d4 = livingEntity.position().distanceToSqr(pos)) <= d2 && d4 < d3) {
            living = livingEntity;
            d3 = d4;
         }
      }

      return living;
   }

   private Iterable<LivingEntity> method6945(Vec3 position) {
      if (mc.level == null) {
         return List.of();
      } else {
         double d = Math.max(this.minRange.getValue3(), this.maxRange.getValue3()) + 2.0;
         AABB aabb = new AABB(position.x - d, position.y - d, position.z - d, position.x + d, position.y + d, position.z + d);
         return mc.level.getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> true);
      }
   }

   private boolean method6946(LivingEntity livingEntity) {
      return livingEntity instanceof ArmorStand || !AuraUtil.method7447(items, livingEntity);
   }

   private Vec3 method6947(Packet<?> packet4) {
      if (packet4 instanceof ServerboundMovePlayerPacket movePlayerPacket && mc.player != null) {
         double x = movePlayerPacket.getX(Double.NaN);
         double y = movePlayerPacket.getY(Double.NaN);
         double z = movePlayerPacket.getZ(Double.NaN);
         return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) ? new Vec3(x, y, z) : null;
      } else {
         return null;
      }
   }

   private Vec3 method6948() {
      for (FakeLagModule.Data data : this.queue) {
         if (data.getVec3d() != null) {
            return data.getVec3d();
         }
      }

      return null;
   }

   private boolean method6949() {
      FakeLagModule.Data data = this.queue.peek();
      return data != null && System.currentTimeMillis() - data.time() >= this.longValue;
   }

   private long method6950() {
      int origin = Math.round(Math.min(this.minDelay.getValue3(), this.maxDelay.getValue3()));
      int n;
      return (n = Math.round(Math.max(this.minDelay.getValue3(), this.maxDelay.getValue3()))) <= origin
         ? origin
         : ThreadLocalRandom.current().nextInt(origin, n + 1);
   }

   private boolean method6951() {
      if (mc.player != null && mc.player.isUsingItem()) {
         ItemStack useItem = mc.player.getUseItem();
         return useItem.has(DataComponents.FOOD)
            || useItem.is(Items.MILK_BUCKET)
            || useItem.is(Items.POTION)
            || useItem.is(Items.SPLASH_POTION)
            || useItem.is(Items.LINGERING_POTION);
      } else {
         return false;
      }
   }

   private void method6952() {
      this.method6955(2);
   }

   private void method6953() {
      this.method6955(0);
   }

   private void method6954() {
      this.method6955(4);
   }

   private void method6955(int x) {
      x |= 1;
      if (mc.isSameThread()) {
         this.method6957(x);
      } else {
         this.atomicInteger.accumulateAndGet(x, (n, var1x) -> n | var1x);
      }
   }

   private void method6956() {
      int andSet;
      if ((andSet = this.atomicInteger.getAndSet(0)) != 0) {
         this.method6957(andSet);
      }
   }

   private void method6957(int x) {
      this.method6958();
      if ((x & 4) != 0) {
         this.longValue = this.method6950();
      }

      if ((x & 2) != 0) {
         this.stopWatch.reset();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method6958() {
      if (!this.queue.isEmpty() && mc.getConnection() != null) {
         this.flag2 = true;

         FakeLagModule.Data data;
         try {
            while ((data = this.queue.poll()) != null) {
               mc.getConnection().send(data.getPacket());
            }
         } catch (Throwable e) {
            this.flag2 = false;
            throw e;
         }

         this.flag2 = false;
      } else {
         this.queue.clear();
      }
   }

   private void method6959() {
      this.queue.clear();
   }

   private static final class Data {
      private final Packet<?> packet;
      private final Vec3 vec3d;
      private final long longValue;

      public Packet<?> packet() {
         return this.packet;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.packet, other.packet) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(packet, vec3d, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "packet=" + this.packet + ", " + "vec3d=" + this.vec3d + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(Packet<?> packet4, Vec3 vec3d) {
         this(packet4, vec3d, System.currentTimeMillis());
      }

      private Data(Packet<?> packet, Vec3 vec3d, long longValue) {
         this.packet = packet;
         this.vec3d = vec3d;
         this.longValue = longValue;
      }

      public Packet<?> getPacket() {
         return this.packet;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public long time() {
         return this.longValue;
      }
   }
}
