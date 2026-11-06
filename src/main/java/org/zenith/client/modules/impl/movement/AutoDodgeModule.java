package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "AutoDodge",
   category = ModuleCategory.MOVEMENT,
   description = "module.autoDodge.desc"
)
public final class AutoDodgeModule extends Module {
   public static final AutoDodgeModule INSTANCE = new AutoDodgeModule();
   private static final int CONST_intValue = -13447886;
   private static final int CONST_intValue2 = -16776961;
   private static final String text = "На этой анархии этот предмет не работает";
   private static final int CONST_intValue3 = -50;
   private static final int CONST_intValue4 = 70;
   private static final double CONST_doubleValue = 9.99999773128142E-7;
   private static final double CONST_doubleValue2 = 0.8000000016738433;
   private static final double CONST_doubleValue3 = 0.9900000205305426;
   private static final double CONST_doubleValue4 = 0.0500000024232657;
   private static final double CONST_doubleValue5 = 0.11999995180429479;
   private static final double CONST_doubleValue6 = 2.0;
   private static final double CONST_doubleValue7 = 2.300000381469741;
   private static final double CONST_doubleValue8 = 25.0;
   private static final double CONST_doubleValue9 = 2.0;
   private static final double CONST_doubleValue10 = 15.0;
   private static final double CONST_doubleValue11 = 0.10000000396251493;
   private static final double CONST_doubleValue12 = 400.0;
   private static final double CONST_doubleValue13 = 20.0;
   private static final float CONST_floatValue = 3.0F;
   private final Map<Integer, AutoDodgeModule.Data2> items = new HashMap<>();
   private int intValue;

   private AutoDodgeModule() {
   }

   private static boolean predictsPotionTrajectoryIntersect(AbstractThrownPotion entity, AABB inflate) {
      Vec3 deltaMovement = entity.getDeltaMovement();
      Vec3 position = entity.position();

      for (int i = 0;
         i < 70 && deltaMovement.lengthSqr() >= 9.99999773128142E-7 && position.y >= mc.level.getMinY() && position.y <= mc.level.getMinY() + mc.level.getHeight();
         i++
      ) {
         double d = mc.level.getFluidState(BlockPos.containing(position)).is(FluidTags.WATER) ? 0.8000000016738433 : 0.9900000205305426;
         deltaMovement = new Vec3(deltaMovement.x * d, (deltaMovement.y - 0.0500000024232657) * d, deltaMovement.z * d);
         Vec3 to = position.add(deltaMovement);
         BlockHitResult clip = mc.level.clip(new ClipContext(position, to, Block.COLLIDER, Fluid.NONE, entity));
         if (clip.getType() == Type.BLOCK) {
            return segmentIntersectsBounds(inflate, position, clip.getLocation());
         }

         if (segmentIntersectsBounds(inflate, position, to)) {
            return true;
         }

         position = to;
      }

      return false;
   }

   private static boolean segmentIntersectsBounds(AABB inflate, Vec3 position, Vec3 location) {
      return new AABB(
            Math.min(position.x, location.x),
            Math.min(position.y, location.y),
            Math.min(position.z, location.z),
            Math.max(position.x, location.x),
            Math.max(position.y, location.y),
            Math.max(position.z, location.z)
         )
         .inflate(0.11999995180429479)
         .intersects(inflate);
   }

   private static int getPotionColor(ItemStack mainHandItem) {
      PotionContents potionContents = mainHandItem.get(DataComponents.POTION_CONTENTS);
      return potionContents != null ? potionContents.getColor() & 16777215 : 0;
   }

   @Override
   public void onDisable() {
      this.items.clear();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         var iterator = this.items.entrySet().iterator();

         while (iterator.hasNext()) {
            if (mc.level.getEntity((Integer)((Entry)iterator.next()).getKey()) == null) {
               iterator.remove();
            }
         }

         boolean empty = this.items.isEmpty();

         if (empty) {
            this.intValue++;
         } else {
            AABB inflate = mc.player.getBoundingBox().inflate(2.0);
            double amountToAddInAllDirections = mc.options.renderDistance().get() * 16;

            for (AbstractThrownPotion entity : mc.level.getEntitiesOfClass(AbstractThrownPotion.class, mc.player.getBoundingBox().inflate(amountToAddInAllDirections), abstractThrownPotion -> true)) {
               AutoDodgeModule.Data2 data2 = this.items.get(entity.getId());
               if (data2 != null) {
                  boolean flag = predictsPotionTrajectoryIntersect(entity, inflate);
                  int n = 0xFF000000 | data2.color() & 16777215;
                  if (!flag || Zenith.getInstance().getFriends().method7627(data2.getText())) {
                     return;
                  }

                  if (n != -13447886 && n != -16776961 || mc.player.distanceTo(entity) <= 2.300000381469741 || this.intValue < 0) {
                     return;
                  }

                  ItemStack defaultInstance = Items.DRIED_KELP.getDefaultInstance();
                  if (!mc.player.getCooldowns().isOnCooldown(defaultInstance) && InventoryUtils.method906(Items.DRIED_KELP) != null) {
                     if (Zenith.getInstance().getTaskScheduler().method5116()) {
                        if (Zenith.getInstance().getRotationManager().getEffectiveRotation().angularDistanceTo(new Rotation(mc.player.getYRot(), mc.player.getXRot())) < 20.0) {
                           this.intValue++;
                        }

                        if (this.intValue >= 2) {
                           this.intValue = 5;
                           InventoryUtils.method927(Items.DRIED_KELP);
                        }
                     }

                     Rotation rotation = Rotation.lookingAt(entity.getEyePosition(), mc.player.getEyePosition());
                     this.requestRotation(rotation.add((float)MathUtils.method2651(-3.0, 3.0), (float)MathUtils.method2651(-3.0, 3.0)));
                     break;
                  }
               }
            }

            this.intValue++;
            return;
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null && mc.level != null && event.isIncoming()) {
         if (event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket && systemChatPacket.content().getString().equals("На этой анархии этот предмет не работает")) {
            this.intValue = -50;
         }
      }
   }

   @EventTarget
   public void onPotionAdded(EntityAddEvent event) {
      if (mc.player != null && mc.level != null) {
         if (event.getEntity() instanceof AbstractThrownPotion abstractThrownPotion) {
            Vec3 position = abstractThrownPotion.position();
            Vec3 deltaMovement = abstractThrownPotion.getDeltaMovement();
            double d = Double.MAX_VALUE;
            String key = null;
            int color = 0;

            for (Entry entry : this.findNearbyPotionThrowers().entrySet()) {
               AutoDodgeModule.Data data = (AutoDodgeModule.Data)entry.getValue();
               double d2;
               if (!((d2 = position.distanceTo(data.getVec3d())) > 25.0) && !(d2 >= d)) {
                  int n;
                  if (data.getVec3d().y - position.y > 2.0) {
                     Vec3 vec3d;
                     n = new Vec3(position.x - (vec3d = data.getVec3d()).x, 0.0, position.z - vec3d.z).length() < 15.0 ? 1 : 0;
                  } else {
                     n = !(deltaMovement.lengthSqr() <= 9.99999773128142E-7)
                           && !(deltaMovement.normalize().dot(data.getVec3d2().normalize()) > 0.10000000396251493)
                        ? 0
                        : 1;
                  }

                  if (n != 0) {
                     d = d2;
                     color = data.color();
                     key = (String)entry.getKey();
                  }
               }
            }

            if (key != null) {
               this.intValue = 0;
               this.items.put(abstractThrownPotion.getId(), new AutoDodgeModule.Data2(color, key));
            }
         }
      }
   }

   private void requestRotation(Rotation rotation) {
      Rotation rotation2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig2(), rotation);
      Zenith.getInstance().getRotationManager().requestRotation(new RotationRequest(rotation2, () -> rotation2, bcCameraTestnerv.getRotationConfig2()), 20, this);
   }

   private Map<String, AutoDodgeModule.Data> findNearbyPotionThrowers() {
      HashMap map = new HashMap();

      for (AbstractClientPlayer player : mc.level.players()) {
         ItemStack mainHandItem = player.getMainHandItem();
         ItemStack offhandItem = player.getOffhandItem();
         int intValue;
         if ((intValue = mainHandItem.getItem() == Items.SPLASH_POTION ? getPotionColor(mainHandItem) : (offhandItem.getItem() == Items.SPLASH_POTION ? getPotionColor(offhandItem) : -1)) >= 0
            && player != mc.player
            && mc.player.distanceToSqr(player) <= 400.0) {
            map.put(player.getName().getString(), new AutoDodgeModule.Data(intValue, player.position(), player.getViewVector(1.0F)));
         }
      }

      return map;
   }

   private static final class Data {
      private final int intValue;
      private final Vec3 vec3d;
      private final Vec3 vec3d2;

      public int intValue() {
         return this.intValue;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public Vec3 vec3d2() {
         return this.vec3d2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.vec3d2, other.vec3d2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, vec3d, vec3d2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "vec3d=" + this.vec3d + ", " + "vec3d2=" + this.vec3d2 + "]";
      }

      private Data(int intValue, Vec3 vec3d, Vec3 vec3d2) {
         this.intValue = intValue;
         this.vec3d = vec3d;
         this.vec3d2 = vec3d2;
      }

      public int color() {
         return this.intValue;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public Vec3 getVec3d2() {
         return this.vec3d2;
      }
   }

   private static final class Data2 {
      private final int intValue;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text);
      }

      @Override
      public String toString() {
         return "Data2[" + "intValue=" + this.intValue + ", " + "text=" + this.text + "]";
      }

      private Data2(int intValue, String text) {
         this.intValue = intValue;
         this.text = text;
      }

      public int color() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }
   }
}
