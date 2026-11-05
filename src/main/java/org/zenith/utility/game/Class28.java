package org.zenith.utility.game;

import java.lang.ref.WeakReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.Class42;

public final class Class28 {
   public final int intValue;
   public final long longValue = System.currentTimeMillis();
   public final int intValue2;
   public final int intValue3;
   public final long longValue2;
   public final float floatValue;
   public final double doubleValue;
   public final double doubleValue2;
   public final double doubleValue3;
   public final double doubleValue4;
   public final double doubleValue5;
   public final double doubleValue6;
   public final double doubleValue7;
   public final double doubleValue8;
   public final double doubleValue9;
   public final double doubleValue10;
   public final double doubleValue11;
   public final double doubleValue12;
   public final double doubleValue13;
   public final double doubleValue14;
   public final double doubleValue15;
   private WeakReference<LivingEntity> weakReference;
   private Class42 class42;

   public Class28(int intValue, Vec3 vec, Vec3 vec2, double bbWidth, int n, int n2, long l) {
      this(intValue, vec, vec2, bbWidth, 0.0, vec.y, n, n2, l);
   }

   public Class28(int intValue, Vec3 vec, Vec3 vec2, double bbWidth, double bbHeight, double minY, int n, int n2, long l) {
      this.intValue = intValue;
      this.intValue2 = Math.max(60, n);
      this.intValue3 = n2;
      this.longValue2 = l;
      this.floatValue = (float)(l % 628L) / 100.0F;
      this.doubleValue13 = bbWidth;
      this.doubleValue14 = bbHeight;
      this.doubleValue15 = minY;
      this.doubleValue = vec.x;
      this.doubleValue2 = vec.y;
      this.doubleValue3 = vec.z;
      Vec3 vec3 = vec2.lengthSqr() > 1.0E-6 ? vec2.normalize() : new Vec3(0.0, 0.0, 1.0);
      Vec3 vec4 = vec3.cross(new Vec3(0.0, 1.0, 0.0));
      if (vec4.lengthSqr() < 1.0E-6) {
         vec4 = new Vec3(1.0, 0.0, 0.0);
      }

      vec4 = vec4.normalize();
      Vec3 vec5 = vec4.cross(vec3).normalize();
      this.doubleValue4 = vec3.x;
      this.doubleValue5 = vec3.y;
      this.doubleValue6 = vec3.z;
      this.doubleValue7 = vec4.x;
      this.doubleValue8 = vec4.y;
      this.doubleValue9 = vec4.z;
      this.doubleValue10 = vec5.x;
      this.doubleValue11 = vec5.y;
      this.doubleValue12 = vec5.z;
   }

   public Class28 method4652(LivingEntity entity) {
      this.weakReference = entity == null ? null : new WeakReference<>(entity);
      return this;
   }

   public LivingEntity method4653() {
      return this.weakReference == null ? null : this.weakReference.get();
   }

   public Class42 method4654(int intValue, int n, float floatValue) {
      if (this.class42 == null || !this.class42.method7381(intValue, n, floatValue)) {
         this.class42 = Class42.method7380(intValue, this, n, floatValue);
      }

      return this.class42;
   }

   public float method4655(long now) {
      return (float)(now - this.longValue) / this.intValue2;
   }

   public boolean method4656(long now) {
      return now - this.longValue >= this.intValue2;
   }
}
