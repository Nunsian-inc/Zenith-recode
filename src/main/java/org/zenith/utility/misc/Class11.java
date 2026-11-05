package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;

public class Class11 implements Handler7 {
   private Vec3 vec3d;
   private Vec3 vec3d2;
   private Vec3 vec3d3;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private final int intValue4;
   private final float floatValue;
   private final ZenithColor zenithColor;
   private final String text;
   private float floatValue2;
   private final float floatValue3;
   private boolean flag;
   private static final Minecraft mc = Minecraft.getInstance();

   public Class11(Vec3 vec3d, Vec3 vec3d3, int n, float floatValue, ZenithColor zenithColor, String text, float f, float f2) {
      this.vec3d = vec3d;
      this.vec3d2 = vec3d;
      this.vec3d3 = vec3d3;
      this.intValue = 0;
      this.intValue2 = 0;
      this.intValue4 = n;
      this.floatValue = floatValue;
      this.zenithColor = zenithColor;
      this.text = text;
      this.floatValue2 = f;
      this.floatValue3 = f2;
      this.flag = false;
   }

   public void update() {
      this.intValue++;
      this.vec3d2 = this.vec3d;
      this.intValue3 = this.intValue2;
      if (this.flag) {
         this.intValue2++;
      } else if (mc.level != null) {
         float f = this.floatValue / 2.0F;
         double x = this.vec3d.x;
         double y = this.vec3d.y;
         double z = this.vec3d.z;
         Vec3 vec3d3 = this.vec3d3;
         AABB aabb = new AABB(x - f, y - f * 2.0F + vec3d3.y, z - f, x + f, y + f + vec3d3.y, z + f);
         if (!mc.level.noCollision(null, aabb)) {
            if (vec3d3.y < 0.0) {
               this.flag = true;
            }

            vec3d3 = new Vec3(vec3d3.x, 0.0, vec3d3.z);
         } else {
            y += vec3d3.y;
         }

         AABB aabb2 = new AABB(x - f + vec3d3.x, y - f, z - f, x + f + vec3d3.x, y + f, z + f);
         if (!mc.level.noCollision(null, aabb2)) {
            vec3d3 = new Vec3(0.0, vec3d3.y, vec3d3.z);
         } else {
            x += vec3d3.x;
         }

         AABB aabb3 = new AABB(x - f, y - f, z - f + vec3d3.z, x + f, y + f, z + f + vec3d3.z);
         if (!mc.level.noCollision(null, aabb3)) {
            vec3d3 = new Vec3(vec3d3.x, vec3d3.y, 0.0);
         } else {
            z += vec3d3.z;
         }

         this.vec3d3 = vec3d3;
         this.vec3d = new Vec3(x, y, z);
         if (this.flag) {
            this.vec3d3 = Vec3.ZERO;
         }
      }
   }

   @Override
   public boolean method1555() {
      return this.intValue >= this.intValue4 * 2 || this.intValue2 >= this.intValue4;
   }

   @Override
   public float getAlpha() {
      return this.method1449(1.0F);
   }

   @Override
   public float method1449(float f) {
      if (!this.flag) {
         return 1.0F;
      } else {
         float f2 = this.intValue3 + (this.intValue2 - this.intValue3) * f;
         return 1.0F - f2 / this.intValue4;
      }
   }

   @Generated
   @Override
   public Vec3 method1556() {
      return this.vec3d;
   }

   @Generated
   @Override
   public Vec3 method1557() {
      return this.vec3d2;
   }

   @Generated
   public Vec3 getVec3d3() {
      return this.vec3d3;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   public int getIntValue4() {
      return this.intValue4;
   }

   @Generated
   @Override
   public float getSize() {
      return this.floatValue;
   }

   @Generated
   @Override
   public ZenithColor method1558() {
      return this.zenithColor;
   }

   @Generated
   @Override
   public String method1559() {
      return this.text;
   }

   @Generated
   @Override
   public float method1560() {
      return this.floatValue2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue3;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public void setVec3d(Vec3 vec3d) {
      this.vec3d = vec3d;
   }

   @Generated
   public void setVec3d2(Vec3 vec3d2) {
      this.vec3d2 = vec3d2;
   }

   @Generated
   public void setVec3d3(Vec3 vec3d3) {
      this.vec3d3 = vec3d3;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   @Generated
   public void setIntValue3(int n) {
      this.intValue3 = n;
   }

   @Generated
   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }

   @Generated
   public void setFlag(boolean flag) {
      this.flag = flag;
   }
}
