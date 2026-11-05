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
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.level != null) {
         this.flag = this.method6943(mc.player.position()) != null;
      } else {
         this.flag = false;
         this.method6959();
      }
   }

   @EventTarget
   public void method6934(PacketProcessEndEvent var1) {
      if (mc.player != null && mc.level != null) {
         this.method6956();
         if (!this.queue.isEmpty()) {
            if (this.method6941() || !this.stopWatch.method7546((long)this.recoilTime.getValue3())) {
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
   public void method6935(WorldRenderEvent var1) {
      if (this.renderBox.isEnabled() && mc.player != null) {
         Vec3 var2 = this.method6948();
         if (var2 != null) {
            GlowUtil.method2230(mc.player.getDimensions(mc.player.getPose()).makeBoundingBox(var2), -1, 1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.getPacket3() != null) {
         if (var1.method7100()) {
            this.method6938(var1.getPacket3());
         }
      }
   }

   @EventTarget(3)
   public void method6936(OutgoingPacketEvent var1) {
      if (!var1.isCancelled() && !this.flag2 && !BlinkModule.INSTANCE.method6732() && !TrapTpModule.INSTANCE.isFlag6() && var1.getPacket4() != null) {
         if (mc.player != null && mc.level != null) {
            boolean var2;
            if (var2 = mc.isSameThread()) {
               this.method6956();
            }

            Packet var3 = var1.getPacket4();
            int var4;
            if ((var4 = this.method6937(var3)) >= 0) {
               if (!var2 && !this.queue.isEmpty()) {
                  var1.cancel();
                  this.queue.add(new FakeLagModule.Data(var3, this.method6947(var3)));
               }

               this.method6955(var4);
            } else if (!this.method6939(var3)) {
               var1.cancel();
               this.queue.add(new FakeLagModule.Data(var3, this.method6947(var3)));
            }
         } else {
            this.method6959();
         }
      }
   }

   private int method6937(Packet<?> var1) {
      if (this.method6941() || !this.stopWatch.method7546((long)this.recoilTime.getValue3())) {
         return 0;
      } else if (this.method6949()) {
         return 4;
      } else if (this.method6940(var1)) {
         return 2;
      } else {
         return !this.method6951() && !this.method6942() ? -1 : 0;
      }
   }

   private void method6938(Packet<?> var1) {
      if (var1 instanceof ClientboundDisconnectPacket) {
         this.method6959();
         this.stopWatch.reset();
      } else if (var1 instanceof ClientboundPlayerPositionPacket) {
         this.queue.removeIf(var0 -> BlinkUtils.method1444(var0.getPacket()));
         this.method6952();
      } else if (var1 instanceof ClientboundSetHealthPacket) {
         this.method6952();
      } else if (!(
         var1 instanceof ClientboundSetEntityMotionPacket var2
            && mc.player != null
            && var2.id() == mc.player.getId()
            && (var2.movement().x != 0.0 || var2.movement().y != 0.0 || var2.movement().z != 0.0)
      )) {
         if (var1 instanceof ClientboundExplodePacket var3) {
            var3.playerKnockback().ifPresent(var1x -> {
               if (var1x.x != 0.0 || var1x.y != 0.0 || var1x.z != 0.0) {
                  this.method6952();
               }
            });
         }
      } else {
         this.method6952();
      }
   }

   private boolean method6939(Packet<?> var1) {
      return var1 instanceof ServerboundChatPacket || var1 instanceof ServerboundChatCommandPacket;
   }

   private boolean method6940(Packet<?> var1) {
      if (var1 instanceof ServerboundResourcePackPacket) {
         return true;
      } else if (!this.entry3.isEnabled() || !(var1 instanceof ServerboundInteractPacket) && !(var1 instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) && !(var1 instanceof ServerboundPunchPacket)) {
         return !this.entry4.isEnabled() || !(var1 instanceof ServerboundUseItemOnPacket) && !(var1 instanceof ServerboundSignUpdatePacket)
            ? this.entry5.isEnabled() && var1 instanceof ServerboundPlayerActionPacket
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
         Vec3 var1 = this.method6948();
         if (var1 == null) {
            return false;
         } else if (mc.player == null) {
            return true;
         } else {
            LivingEntity var2 = this.method6944(var1);
            if (var2 == null) {
               return true;
            } else {
               AABB var3 = mc.player.getDimensions(mc.player.getPose()).makeBoundingBox(var1);
               boolean var4 = var2.getBoundingBox().intersects(var3);
               double var5 = var2.position().distanceTo(var1);
               double var7 = var2.position().distanceTo(mc.player.position());
               return var5 < var7 || var4;
            }
         }
      } else {
         return true;
      }
   }

   private LivingEntity method6943(Vec3 var1) {
      double var2 = Math.min(this.minRange.getValue3(), this.maxRange.getValue3());
      double var4 = Math.max(this.minRange.getValue3(), this.maxRange.getValue3());
      double var6 = var2 * var2;
      double var8 = var4 * var4;
      LivingEntity var10 = null;
      double var11 = Double.MAX_VALUE;

      for (LivingEntity var14 : this.method6945(var1)) {
         double var15;
         if (!this.method6946(var14) && (var15 = var14.distanceToSqr(var1)) >= var6 && var15 <= var8 && var15 < var11) {
            var10 = var14;
            var11 = var15;
         }
      }

      return var10;
   }

   private LivingEntity method6944(Vec3 var1) {
      double var2;
      double var4 = (var2 = Math.max(this.minRange.getValue3(), this.maxRange.getValue3())) * var2;
      LivingEntity var6 = null;
      double var7 = Double.MAX_VALUE;

      for (LivingEntity var10 : this.method6945(var1)) {
         double var11;
         if (!this.method6946(var10) && (var11 = var10.position().distanceToSqr(var1)) <= var4 && var11 < var7) {
            var6 = var10;
            var7 = var11;
         }
      }

      return var6;
   }

   private Iterable<LivingEntity> method6945(Vec3 var1) {
      if (mc.level == null) {
         return List.of();
      } else {
         double var2 = Math.max(this.minRange.getValue3(), this.maxRange.getValue3()) + 2.0;
         AABB var4 = new AABB(var1.x - var2, var1.y - var2, var1.z - var2, var1.x + var2, var1.y + var2, var1.z + var2);
         return mc.level.getEntitiesOfClass(LivingEntity.class, var4, var0 -> true);
      }
   }

   private boolean method6946(LivingEntity var1) {
      return var1 instanceof ArmorStand || !AuraUtil.method7447(items, var1);
   }

   private Vec3 method6947(Packet<?> var1) {
      if (var1 instanceof ServerboundMovePlayerPacket var2 && mc.player != null) {
         double var3 = var2.getX(Double.NaN);
         double var5 = var2.getY(Double.NaN);
         double var7 = var2.getZ(Double.NaN);
         return !Double.isNaN(var3) && !Double.isNaN(var5) && !Double.isNaN(var7) ? new Vec3(var3, var5, var7) : null;
      } else {
         return null;
      }
   }

   private Vec3 method6948() {
      for (FakeLagModule.Data var2 : this.queue) {
         if (var2.getVec3d() != null) {
            return var2.getVec3d();
         }
      }

      return null;
   }

   private boolean method6949() {
      FakeLagModule.Data var1 = this.queue.peek();
      return var1 != null && System.currentTimeMillis() - var1.time() >= this.longValue;
   }

   private long method6950() {
      int var1 = Math.round(Math.min(this.minDelay.getValue3(), this.maxDelay.getValue3()));
      int var2;
      return (var2 = Math.round(Math.max(this.minDelay.getValue3(), this.maxDelay.getValue3()))) <= var1
         ? var1
         : ThreadLocalRandom.current().nextInt(var1, var2 + 1);
   }

   private boolean method6951() {
      if (mc.player != null && mc.player.isUsingItem()) {
         ItemStack var1 = mc.player.getUseItem();
         return var1.has(DataComponents.FOOD)
            || var1.is(Items.MILK_BUCKET)
            || var1.is(Items.POTION)
            || var1.is(Items.SPLASH_POTION)
            || var1.is(Items.LINGERING_POTION);
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

   private void method6955(int var1) {
      var1 |= 1;
      if (mc.isSameThread()) {
         this.method6957(var1);
      } else {
         this.atomicInteger.accumulateAndGet(var1, (var0, var1x) -> var0 | var1x);
      }
   }

   private void method6956() {
      int var1;
      if ((var1 = this.atomicInteger.getAndSet(0)) != 0) {
         this.method6957(var1);
      }
   }

   private void method6957(int var1) {
      this.method6958();
      if ((var1 & 4) != 0) {
         this.longValue = this.method6950();
      }

      if ((var1 & 2) != 0) {
         this.stopWatch.reset();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method6958() {
      if (!this.queue.isEmpty() && mc.getConnection() != null) {
         this.flag2 = true;

         FakeLagModule.Data var1;
         try {
            while ((var1 = this.queue.poll()) != null) {
               mc.getConnection().send(var1.getPacket());
            }
         } catch (Throwable var4) {
            this.flag2 = false;
            throw var4;
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

      private Data(Packet<?> var1, Vec3 var2) {
         this(var1, var2, System.currentTimeMillis());
      }

      private Data(Packet<?> var1, Vec3 var2, long var3) {
         this.packet = var1;
         this.vec3d = var2;
         this.longValue = var3;
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
