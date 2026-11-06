package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;

public class FireflyParticle implements RenderableParticle {
   private Vec3 vec3d;
   private Vec3 vec3d2;
   private Vec3 vec3d3;
   private Vec3 vec3d4;
   private int intValue;
   private int intValue2;
   private final int intValue3;
   private final float floatValue;
   private ZenithColor zenithColor;
   private final List<FireflyParticle.Entry> items = new ArrayList<>();
   private static final int CONST_intValue = 15;
   private int intValue4 = -1;
   private float floatValue2 = 1.0F;

   public FireflyParticle(Vec3 vec3d, Vec3 vec3d4, int intValue, float floatValue, ZenithColor zenithColor) {
      this.vec3d = vec3d;
      this.vec3d2 = vec3d;
      this.vec3d3 = Vec3.ZERO;
      this.vec3d4 = vec3d4;
      this.intValue = intValue;
      this.intValue2 = intValue;
      this.intValue3 = intValue;
      this.floatValue = floatValue;
      this.zenithColor = zenithColor;
   }

   @Override
   public String getTextureKey() {
      return "particle.texture.firefly";
   }

   @Override
   public float getRotationDegrees() {
      return 0.0F;
   }

   public void method4704(Vec3 vec) {
      Minecraft minecraft = Minecraft.getInstance();
      this.intValue2 = this.intValue;
      double d = this.vec3d.distanceTo(vec);
      this.intValue -= d > 64.0 ? 8 : 1;
      if (this.intValue > 0) {
         Vec3 vec3d = this.vec3d;
         this.vec3d2 = this.vec3d;
         float f = 1.0F - (float)this.intValue / this.intValue3;
         if (f < 0.35F) {
            float scale = 1.0F - (float)Math.pow(1.0F - f / 0.35F, 3.0);
            this.vec3d3 = this.vec3d4.scale(scale);
         } else if (f > 0.6F) {
            float f2 = 1.0F - (float)Math.pow((f - 0.6F) / 0.4F, 3.0);
            this.vec3d3 = this.vec3d3.scale(0.92 + 0.08 * f2);
         } else {
            this.vec3d3 = this.vec3d3.scale(0.998);
         }

         if (minecraft.level != null) {
            this.intValue4 = -1;
            Vec3 pos = this.vec3d;

            for (int i = 1; i <= 15; i++) {
               pos = pos.add(this.vec3d3);
               if (!minecraft.level.getBlockState(BlockPos.containing(pos)).isAir()) {
                  this.intValue4 = i;
                  break;
               }
            }

            if (this.intValue4 > 0) {
               this.floatValue2 = this.intValue4 / 15.0F;
               this.floatValue2 = this.floatValue2 * this.floatValue2;
               if (this.intValue4 <= 1) {
                  this.intValue = 0;
                  return;
               }
            } else {
               this.floatValue2 = Math.min(this.floatValue2 + 0.1F, 1.0F);
            }
         }

         this.vec3d = this.vec3d.add(this.vec3d3);
         this.items.add(new FireflyParticle.Entry(vec3d, this.vec3d, this.zenithColor));
         this.items.removeIf(entry -> {
            entry.update();
            return entry.method4708();
         });
      }
   }

   @Override
   public boolean isExpired() {
      return this.intValue <= 0;
   }

   @Override
   public float getAlpha() {
      return this.getInterpolatedAlpha(1.0F);
   }

   @Override
   public float getInterpolatedAlpha(float f) {
      float f2 = this.intValue2 + (this.intValue - this.intValue2) * f;
      float f3 = f2 / this.intValue3;
      return f3 * this.floatValue2;
   }

   public void method4705(Vec3 vec3d, Vec3 vec3d3) {
      this.vec3d2 = this.vec3d;
      this.vec3d = vec3d;
      this.vec3d3 = vec3d3;
      this.vec3d4 = vec3d3;
      this.items.add(new FireflyParticle.Entry(this.vec3d2, this.vec3d, this.zenithColor));
      this.items.removeIf(entry -> {
         entry.update();
         return entry.method4708();
      });
   }

   public void method4706() {
      this.intValue = this.intValue3;
   }

   public void method4707() {
      this.intValue = this.intValue3 / 2;
   }

   @Generated
   @Override
   public Vec3 getPosition() {
      return this.vec3d;
   }

   @Generated
   @Override
   public Vec3 getPreviousPosition() {
      return this.vec3d2;
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
   @Override
   public float getSize() {
      return this.floatValue;
   }

   @Generated
   @Override
   public ZenithColor getColor() {
      return this.zenithColor;
   }

   @Generated
   public List<FireflyParticle.Entry> getItems() {
      return this.items;
   }

   @Generated
   public int getIntValue4() {
      return this.intValue4;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public void setVec3d3(Vec3 vec3d3) {
      this.vec3d3 = vec3d3;
   }

   @Generated
   public void setZenithColor(ZenithColor zenithColor) {
      this.zenithColor = zenithColor;
   }

   public static class Entry {
      private final Vec3 vec3d;
      private final Vec3 vec3d2;
      private final ZenithColor zenithColor;
      private int intValue;
      private static final int CONST_intValue = 20;

      public Entry(Vec3 vec3d, Vec3 vec3d2, ZenithColor zenithColor) {
         this.vec3d = vec3d;
         this.vec3d2 = vec3d2;
         this.zenithColor = zenithColor;
         this.intValue = 20;
      }

      public void update() {
         this.intValue--;
      }

      public boolean method4708() {
         return this.intValue <= 0;
      }

      public float getAlpha() {
         return this.intValue / 20.0F;
      }

      @Generated
      public Vec3 getVec3d() {
         return this.vec3d;
      }

      @Generated
      public Vec3 getVec3d2() {
         return this.vec3d2;
      }

      @Generated
      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }
   }
}
