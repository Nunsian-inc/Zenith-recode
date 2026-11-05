package org.zenith.utility.config;

import com.mojang.authlib.GameProfile;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Zenith;
import org.zenith.client.screens.entity.ImplOtherClientPlayerEntity;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.game.ArmorSlots;

public class PetEntityState {
   private static final Minecraft mc = Minecraft.getInstance();
   private final UUID uUID;
   private ImplOtherClientPlayerEntity implOtherClientPlayerEntity;
   private String text = "";
   private Vec3 vec3d;
   private Vec3 vec3d2 = Vec3.ZERO;
   private float floatValue = ThreadLocalRandom.current().nextFloat() * 360.0F;
   private float floatValue2;
   private float floatValue3;
   private Vec3 vec3d3;
   private Vec3 vec3d4;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private boolean flag;
   private boolean flag2;
   private boolean lastOnGround;
   private long longValue = System.currentTimeMillis();
   private double doubleValue;
   private double doubleValue2;
   private int intValue = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
   private long longValue2 = 0L;
   private boolean flag3 = false;
   private int intValue2 = 0;
   private static final double CONST_doubleValue = 0.12;
   private static final double CONST_doubleValue2 = 0.72;

   public PetEntityState(UUID uuid) {
      this.uUID = UUID.nameUUIDFromBytes(("zenith-pet:" + uuid.toString()).getBytes());
   }

   public void method2302(ClientLevel level, Vec3 vec3d) {
      this.method2303();
      this.text = "";
      this.implOtherClientPlayerEntity = new ImplOtherClientPlayerEntity(level, new GameProfile(this.uUID, ""));
      this.implOtherClientPlayerEntity.setId(Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 100000));
      this.implOtherClientPlayerEntity.setCustomNameVisible(false);
      this.implOtherClientPlayerEntity.setInvisible(false);
      this.implOtherClientPlayerEntity.setNoGravity(true);
      this.method2305(Zenith.getInstance().getPetManager().method4572());
      AttributeInstance attribute = this.implOtherClientPlayerEntity.getAttribute(Attributes.STEP_HEIGHT);
      if (attribute != null) {
         attribute.setBaseValue(0.6);
      }

      this.vec3d = vec3d;
      this.vec3d3 = vec3d;
      this.vec3d4 = vec3d;
      this.vec3d2 = Vec3.ZERO;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.doubleValue = vec3d.x;
      this.doubleValue2 = vec3d.z;
      this.longValue = System.currentTimeMillis();
      this.implOtherClientPlayerEntity.snapTo(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
      this.implOtherClientPlayerEntity.setYHeadRot(0.0F);
      this.implOtherClientPlayerEntity.setYBodyRot(0.0F);
      level.addEntity(this.implOtherClientPlayerEntity);
   }

   public void method2303() {
      PetManager.method4560(this);
      if (this.implOtherClientPlayerEntity != null) {
         this.implOtherClientPlayerEntity.setRemoved(RemovalReason.KILLED);
         this.implOtherClientPlayerEntity.onClientRemoval();
         this.implOtherClientPlayerEntity = null;
      }

      AvatarUtil.method7251(this.uUID);
   }

   public boolean method2304() {
      if (this.implOtherClientPlayerEntity == null || this.implOtherClientPlayerEntity.isRemoved()) {
         return false;
      } else {
         return mc.level == null ? false : mc.level.getEntity(this.implOtherClientPlayerEntity.getId()) == this.implOtherClientPlayerEntity;
      }
   }

   public void method2305(float floatValue) {
      if (this.implOtherClientPlayerEntity != null) {
         AttributeInstance attribute = this.implOtherClientPlayerEntity.getAttribute(Attributes.SCALE);
         if (attribute != null) {
            attribute.setBaseValue(floatValue);
         }
      }
   }

   public void method2306(String text) {
      if (text != null && !text.isBlank()) {
         if (!text.equals(this.text)) {
            this.text = text;
            AvatarUtil.method7239(this.uUID, text);
         }
      } else {
         AvatarUtil.method7251(this.uUID);
         this.text = "";
      }
   }

   public void method2307(Path path) {
      if (path == null) {
         AvatarUtil.method7251(this.uUID);
         this.text = "";
      } else {
         String text = AvatarUtil.method7257(path);
         if (!text.equals(this.text)) {
            this.text = text;
            AvatarUtil.method7240(this.uUID, path);
         }
      }
   }

   public void method2308(Vec3 vec3d, float yBodyRot, boolean shiftKeyDown, LivingEntity livingEntity) {
      this.vec3d4 = this.vec3d3;
      this.floatValue6 = this.floatValue4;
      if (this.vec3d == null) {
         this.vec3d = vec3d;
      }

      if (this.vec3d.distanceTo(vec3d) > 10.0) {
         this.vec3d = vec3d;
         this.vec3d2 = Vec3.ZERO;
         this.vec3d3 = this.vec3d;
         this.vec3d4 = this.vec3d;
         this.method2314();
      } else {
         Vec3 vec;
         if (this.flag) {
            double d = vec3d.y - this.vec3d.y;
            this.vec3d2 = new Vec3(this.vec3d2.x, d * 0.2, this.vec3d2.z);
            vec = this.vec3d.add(this.vec3d2);
            this.vec3d2 = new Vec3(this.vec3d2.x, 0.0, this.vec3d2.z);
         } else {
            this.vec3d2 = this.vec3d2.add(0.0, -0.08, 0.0);
            this.vec3d2 = this.vec3d2.multiply(1.0, 0.98, 1.0);
            if (this.vec3d2.y < -0.5) {
               this.vec3d2 = new Vec3(this.vec3d2.x, -0.5, this.vec3d2.z);
            }

            double x = this.vec3d2.x;
            double y = this.vec3d2.y;
            double z = this.vec3d2.z;
            double d = this.vec3d.x;
            double y2 = this.vec3d.y;
            double z2 = this.vec3d.z;
            boolean lastOnGround = false;
            if (!method2317(d, y2 + y, z2)) {
               y2 += y;
            } else {
               if (y < 0.0) {
                  double d2 = y2 + y;
                  double d3 = y2;

                  for (int i = 0; i < 8; i++) {
                     double y3 = (d2 + d3) * 0.5;
                     if (method2317(d, y3, z2)) {
                        d2 = y3;
                     } else {
                        d3 = y3;
                     }
                  }

                  y2 = d3;
                  lastOnGround = true;
               }

               y = 0.0;
            }

            if (!lastOnGround && method2317(d, y2 - 0.02, z2)) {
               lastOnGround = true;
            }

            if (Math.abs(x) > 1.0E-5) {
               if (!method2317(d + x, y2, z2)) {
                  d += x;
               } else {
                  boolean flag = false;
                  if (lastOnGround) {
                     for (double d2 = 0.1; d2 <= 0.6; d2 += 0.1) {
                        if (!method2317(d + x, y2 + d2, z2) && !method2317(d, y2 + d2, z2)) {
                           y2 += d2;
                           d += x;
                           flag = true;
                           break;
                        }
                     }
                  }

                  if (!flag) {
                     if (lastOnGround && y <= 0.0 && method2319(d + Math.signum(x) * 0.35, y2, z2)) {
                        y = 0.52;
                     }

                     x = 0.0;
                  }
               }
            }

            if (Math.abs(z) > 1.0E-5) {
               if (!method2317(d, y2, z2 + z)) {
                  z2 += z;
               } else {
                  boolean flag = false;
                  if (lastOnGround) {
                     for (double d2 = 0.1; d2 <= 0.6; d2 += 0.1) {
                        if (!method2317(d, y2 + d2, z2 + z) && !method2317(d, y2 + d2, z2)) {
                           y2 += d2;
                           z2 += z;
                           flag = true;
                           break;
                        }
                     }
                  }

                  if (!flag) {
                     if (lastOnGround && y <= 0.0 && method2319(d, y2, z2 + Math.signum(z) * 0.35)) {
                        y = 0.52;
                     }

                     z = 0.0;
                  }
               }
            }

            this.vec3d2 = new Vec3(x, y, z);
            vec = new Vec3(d, y2, z2);
            this.lastOnGround = lastOnGround;
         }

         double d = this.lastOnGround ? 1.0 : 0.2;
         if (livingEntity != null && livingEntity.isAlive()) {
            AABB inflate = livingEntity.getBoundingBox().inflate(0.1);
            double d2 = (inflate.minX + inflate.maxX) * 0.5;
            double d3 = (inflate.minZ + inflate.maxZ) * 0.5;
            double d4 = (inflate.maxX - inflate.minX) * 0.5;
            double d5 = (inflate.maxZ - inflate.minZ) * 0.5;
            double d6 = Math.max(d4, d5) + 0.6;
            double y = livingEntity.getY();
            double d7 = Math.abs(vec.y - y);
            double hypot = Math.hypot(vec.x - d2, vec.z - d3);
            if (d7 > 1.0 && this.lastOnGround || hypot > 4.0) {
               double d8 = Math.atan2(vec3d.z - d3, vec3d.x - d2);
               double x = d2 + Math.cos(d8) * d6;
               double z = d3 + Math.sin(d8) * d6;
               vec = new Vec3(x, y, z);
               this.vec3d2 = Vec3.ZERO;
               this.vec3d = vec;
               this.vec3d3 = vec;
               this.vec3d4 = vec;
               this.intValue2 = 0;
               this.method2314();
               return;
            }

            double d8 = Math.atan2(vec.z - d3, vec.x - d2);
            double d9 = d8 + 0.3 * this.intValue;
            double d10 = Math.atan2(vec3d.z - d3, vec3d.x - d2);
            double d11 = d9 - d10;

            while (d11 > Math.PI) {
               d11 -= Math.PI * 2;
            }

            while (d11 < -Math.PI) {
               d11 += Math.PI * 2;
            }

            double d12 = Math.PI / 2;
            if (d11 > Math.PI / 2) {
               d11 = Math.PI / 2;
               this.intValue = -this.intValue;
            } else if (d11 < -Math.PI / 2) {
               d11 = -Math.PI / 2;
               this.intValue = -this.intValue;
            }

            d9 = d10 + d11;
            double d13 = d2 + Math.cos(d9) * d6;
            double d14 = d3 + Math.sin(d9) * d6;
            double x = Mth.lerp(0.35, vec.x, d13);
            double z = Mth.lerp(0.35, vec.z, d14);
            double x2 = vec.x;
            double z2 = vec.z;
            if (!method2317(x, vec.y, vec.z) && method2318(x, vec.y, vec.z)) {
               x2 = x;
            }

            if (!method2317(x2, vec.y, z) && method2318(x2, vec.y, z)) {
               z2 = z;
            }

            vec = new Vec3(x2, vec.y, z2);
            this.vec3d2 = new Vec3(0.0, this.vec3d2.y, 0.0);
            if (this.implOtherClientPlayerEntity.tickCount % 40 == 0) {
               this.intValue = -this.intValue;
            }

            if (this.lastOnGround) {
               this.intValue2++;
               if (this.intValue2 >= 3) {
                  this.vec3d2 = new Vec3(this.vec3d2.x, 0.45, this.vec3d2.z);
                  this.intValue2 = 0;
               }
            } else {
               this.intValue2 = 0;
            }

            this.flag3 = true;
         } else {
            this.flag3 = false;
            double d2 = vec.distanceTo(vec3d);
            if (d2 > 2.0) {
               Vec3 vec2 = vec3d.subtract(vec);
               double d3 = Math.min(1.0, (d2 - 2.0) / 3.0) * d;
               Vec3 vec3 = vec2.normalize();
               this.vec3d2 = this.vec3d2.add(vec3.x * d3, 0.0, vec3.z * d3);
            }
         }

         this.method2309(vec3d, livingEntity);
         this.vec3d = vec;
         if (this.vec3d.distanceTo(vec3d) < 0.1) {
            this.floatValue = ThreadLocalRandom.current().nextFloat() * 360.0F;
            double x = -Math.sin(Math.toRadians(this.floatValue)) * 0.1;
            double z = Math.cos(Math.toRadians(this.floatValue)) * 0.1;
            this.vec3d2 = this.vec3d2.add(x, 0.0, z);
         }

         float f = this.lastOnGround ? 0.6F : 0.91F;
         this.vec3d2 = new Vec3(this.vec3d2.x * f, this.vec3d2.y, this.vec3d2.z * f);
         this.vec3d3 = this.vec3d;
         if (Math.abs(this.vec3d.x - this.doubleValue) > 0.1 || Math.abs(this.vec3d.z - this.doubleValue2) > 0.1) {
            this.longValue = System.currentTimeMillis();
         }

         this.doubleValue = this.vec3d.x;
         this.doubleValue2 = this.vec3d.z;
         this.method2314();
      }
   }

   private void method2309(Vec3 vec3d, LivingEntity livingEntity) {
      Vec3 position = livingEntity != null && livingEntity.isAlive() ? livingEntity.position() : vec3d;
      double x = position.x - this.vec3d.x;
      double y = position.z - this.vec3d.z;
      float f = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
      this.floatValue2 = f;
      this.floatValue3 = f;
      this.floatValue4 = f;
      this.floatValue5 = f;
   }

   public void method2310() {
   }

   public void method2311(Vec3 vec3d, float f, boolean flag) {
      this.vec3d4 = this.vec3d3;
      this.floatValue6 = this.floatValue4;
      this.vec3d = vec3d;
      this.vec3d3 = vec3d;
      this.floatValue4 = f;
      this.floatValue5 = f;
      this.floatValue3 = f;
      this.method2314();
   }

   public void method2312(Player player) {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved() && player != null) {
         this.implOtherClientPlayerEntity.getInventory().setSelectedSlot(player.getInventory().getSelectedSlot());
         int selectedSlot = player.getInventory().getSelectedSlot();
         this.implOtherClientPlayerEntity.getInventory().getNonEquipmentItems().set(selectedSlot, player.getInventory().getNonEquipmentItems().get(selectedSlot).copy());
         this.implOtherClientPlayerEntity.setItemSlot(EquipmentSlot.OFFHAND, player.getItemBySlot(EquipmentSlot.OFFHAND).copy());

         for (int i = 0; i < 4; i++) {
            this.implOtherClientPlayerEntity.setItemSlot(ArmorSlots.equipmentSlots[i], player.getItemBySlot(ArmorSlots.equipmentSlots[i]).copy());
         }
      }
   }

   public Vec3 method2313(float floatValue) {
      return this.vec3d4 != null && this.vec3d3 != null
         ? new Vec3(
            Mth.lerp((double)floatValue, this.vec3d4.x, this.vec3d3.x),
            Mth.lerp((double)floatValue, this.vec3d4.y, this.vec3d3.y),
            Mth.lerp((double)floatValue, this.vec3d4.z, this.vec3d3.z)
         )
         : this.vec3d3;
   }

   private void method2314() {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved()) {
         this.implOtherClientPlayerEntity.moveOrInterpolateTo(new Vec3(this.vec3d3.x, this.vec3d3.y, this.vec3d3.z), this.floatValue4, 0.0F);
         this.implOtherClientPlayerEntity.lerpHeadTo(this.floatValue5, 3);
         this.implOtherClientPlayerEntity.syncPacketPositionCodec(this.vec3d3.x, this.vec3d3.y, this.vec3d3.z);
         this.implOtherClientPlayerEntity.setOnGround(this.lastOnGround);
         this.implOtherClientPlayerEntity.setDeltaMovement(this.vec3d2);
      }
   }

   private static boolean method2315(double x, double y, double z) {
      if (mc.level == null) {
         return false;
      } else {
         BlockPos containing = BlockPos.containing(x, y, z);
         return mc.level.getBlockState(containing).isSolid();
      }
   }

   private static AABB method2316(double x, double y, double z) {
      return new AABB(x - 0.12, y, z - 0.12, x + 0.12, y + 0.72, z + 0.12);
   }

   private static boolean method2317(double x, double y, double z) {
      return mc.level != null && !mc.level.noCollision(method2316(x, y, z));
   }

   private static boolean method2318(double x, double y, double z) {
      if (mc.level == null) {
         return true;
      } else {
         for (double d = 0.01; d <= 1.5; d += 0.25) {
            if (!mc.level.noCollision(method2316(x, y - d, z))) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean method2319(double x, double d, double z) {
      return mc.level == null ? false : !method2317(x, d + 1.0, z);
   }

   private static float method2320(float f, float f2, float f3) {
      return f + Mth.wrapDegrees(f2 - f) * f3;
   }

   @Generated
   public UUID getUUID() {
      return this.uUID;
   }

   @Generated
   public ImplOtherClientPlayerEntity getImplOtherClientPlayerEntity() {
      return this.implOtherClientPlayerEntity;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public Vec3 getVec3d3() {
      return this.vec3d3;
   }

   @Generated
   public Vec3 getVec3d4() {
      return this.vec3d4;
   }

   @Generated
   public float getFloatValue4() {
      return this.floatValue4;
   }

   @Generated
   public float getFloatValue5() {
      return this.floatValue5;
   }

   @Generated
   public float getFloatValue6() {
      return this.floatValue6;
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
