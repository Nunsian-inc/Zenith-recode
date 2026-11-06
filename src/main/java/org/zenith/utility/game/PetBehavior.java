package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.AnimationState;
import org.zenith.utility.misc.WardrobeModel;
import org.zenith.utility.misc.ModelPoseEvaluator;
import org.zenith.utility.misc.PetMotionState;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.render.WardrobeParticleEmitter;
import org.zenith.utility.render.WardrobePreview;

public final class PetBehavior {
   private static final Minecraft mc = Minecraft.getInstance();
   private static final int CONST_intValue = 600;
   private static final double CONST_doubleValue = 12.0;
   public final HoveredData hoveredData;
   public final WardrobeModel class15;
   public final WardrobeModel.Data3 data3;
   public final PetMotionState class3;
   public final AnimationState animationState = new AnimationState();
   private double doubleValue;
   private boolean flag;
   private boolean running;
   public final ModelPoseEvaluator class16 = new ModelPoseEvaluator();
   public final RandomSource random = RandomSource.create();
   private int ticks;
   private int intValue;
   private Vec3 vec3d;
   private float floatValue;
   private float floatValue2;
   private int intValue2;
   private int intValue3 = 60;
   private int intValue4 = 40;
   private int intValue5;
   private int intValue6;
   private int intValue7;
   private int intValue8;
   private boolean flag2;
   private int intValue9;
   private int intValue10;
   private boolean flag3;
   private Vec3 vec3d2;
   private int intValue11;
   private float alpha = 1.0F;
   private float floatValue3;
   private float[] floatValues = new float[0];
   private float floatValue4;
   private float floatValue5;
   private String text = "";
   private int intValue12;
   private int intValue13 = 120;
   private Vec3 vec3d3;
   private double doubleValue2;
   private boolean flag4;
   private float floatValue6;
   private float floatValue7;
   private Vec3 vec3d4;
   public long longValue;

   public PetBehavior(HoveredData hoveredData, WardrobeModel class2) {
      this.hoveredData = hoveredData;
      this.class15 = class2;
      this.data3 = class2.data3;
      double d = Math.max(0.2, (double)this.data3.getFloatValue());
      this.class3 = new PetMotionState(this.data3.getType4(), 0.18 * d, 0.6 * d);
      this.intValue2 = this.random.nextBoolean() ? 1 : -1;
      this.intValue5 = 200 + this.random.nextInt(200);
      this.intValue6 = 160 + this.random.nextInt(160);
   }

   public float getAlpha() {
      return this.alpha;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void method7926(Player player, LivingEntity livingEntity, double d, float floatValue) {
      Vec3 position = player.position();
      if (!this.class3.flag2) {
         this.class3.method425(this.method7939(player, position));
         this.class3.floatValue = this.class3.floatValue2 = player.yBodyRot;
         this.class3.flag2 = true;
         this.vec3d = position;
         this.floatValue5 = player.yBodyRot;
      }

      int n = this.class3.method424(d);

      for (int i = 0; i < n; i++) {
         this.method7928(player, position, livingEntity, floatValue);
      }
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public float method7927(float alpha1) {
      return Mth.clamp(Mth.lerp(alpha1, this.floatValue7, this.floatValue6), 0.0F, 1.0F);
   }

   public Vec3 getVec3d4() {
      return this.vec3d4;
   }

   private void method7928(Player player, Vec3 vec3d, LivingEntity livingEntity, float floatValue) {
      this.ticks++;
      this.class3.begin();
      this.method7936();
      this.floatValue7 = this.floatValue6;
      double x = vec3d.x - player.xo;
      double d = vec3d.y - player.yo;
      double y = vec3d.z - player.zo;
      double d2 = this.vec3d == null ? 0.0 : Math.sqrt(x * x + y * y);
      if (d2 > 0.02 && this.vec3d != null) {
         float f = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
         this.floatValue5 = this.floatValue5 + Mth.wrapDegrees(f - this.floatValue5) * 0.35F;
      }

      boolean flag = this.vec3d != null
         && x * x + d * d + y * y < 4.0E-4
         && Math.abs(Mth.wrapDegrees(player.getYRot() - this.floatValue)) < 1.0F
         && Math.abs(player.getXRot() - this.floatValue2) < 1.0F;
      this.vec3d = vec3d;
      this.floatValue = player.getYRot();
      this.floatValue2 = player.getXRot();
      this.intValue = flag ? this.intValue + 1 : 0;
      boolean flag2 = player.isSprinting() || d2 > 0.2;
      boolean flag3 = d2 > 0.03;
      if (player.hurtTime <= 0 && (livingEntity == null || !livingEntity.isAlive())) {
         if (this.intValue8 > 0) {
            this.intValue8--;
         }
      } else {
         this.intValue8 = 60;
      }

      boolean flag4 = this.intValue8 > 0 || "hide_in_shell".equals(this.data3.getText()) && this.method7946(vec3d, 8.0);
      if (this.flag4) {
         this.method7933(player, vec3d, flag2, flag4, floatValue);
      } else {
         double d3 = method7951(this.class3.doubleValue3, this.class3.doubleValue5, vec3d.x, vec3d.z);
         if (!(d3 > 12.0) && !(Math.abs(this.class3.doubleValue4 - vec3d.y) > 12.0)) {
            boolean flag5 = this.flag3;
            this.flag3 = this.intValue > 600 && !flag4;
            if (flag5 && !this.flag3) {
               this.animationState.method6540(this.class15, "react", floatValue);
               this.intValue3 = 80;
            }

            boolean flag6 = this.text.equals("shoulder") || this.text.equals("head");
            if (this.flag3 || flag4) {
               this.method7930();
            } else if (!this.text.isEmpty() && flag3 && !this.text.equals("orbit") && !flag6) {
               this.method7930();
            } else if (!this.text.isEmpty() && --this.intValue12 <= 0) {
               this.method7930();
            }

            if (this.text.isEmpty() && !this.flag3 && !flag4 && !flag3 && this.intValue13 == 0 && d3 < 4.0 && this.vec3d2 == null) {
               this.method7929(player, vec3d, floatValue);
            }

            flag6 = this.text.equals("shoulder") || this.text.equals("head");
            Vec3 vec = this.method7937(player, vec3d, livingEntity, flag4, flag3, flag2, d3);
            double d4 = this.data3.getFloatValue2();
            double d5 = method7951(this.class3.doubleValue3, this.class3.doubleValue5, vec.x, vec.z);
            if (d5 > 5.0) {
               d4 *= 1.9;
            } else if (d5 > 2.5 || flag2) {
               d4 *= 1.35;
            }

            double hover = this.data3.hover();
            if (this.class3.method423() && (this.text.equals("sit") || this.text.equals("lie"))) {
               hover = 0.0;
            }

            if (!this.flag3 && (!(d5 < 0.3) || this.vec3d2 != null)) {
               this.class3.method426(vec.x, vec.y, vec.z, d4, hover);
            } else {
               this.class3.method426(this.class3.doubleValue3, vec.y, this.class3.doubleValue5, 0.0, hover);
            }

            if (flag6 && method7951(this.class3.doubleValue3, this.class3.doubleValue5, vec3d.x, vec3d.z) < 1.1) {
               this.method7932();
            }

            if (this.data3.getType4() == WardrobeModel.Type4.HOPPER && this.class3.flag && d5 > 0.6 && this.intValue10 == 0) {
               this.class3.method427(0.42);
               this.intValue10 = 7;
               this.floatValue3 = 1.0F;
            }

            if (this.class3.flag3) {
               this.floatValue3 = 1.0F;
               if ("rain_cloud".equals(this.data3.getText())) {
                  this.method7948(ParticleTypes.CLOUD, 4, 0.2, 0.02);
               }
            }

            this.floatValue3 = Math.max(0.0F, this.floatValue3 - 0.12F);
            if (this.flag && !this.flag3) {
               this.class3.method428(vec.x, vec.z, 14.0F);
            } else if (!this.text.equals("lie") && !this.text.equals("sit")) {
               this.class3.method428(vec3d.x, vec3d.z, 6.0F);
            } else {
               this.class3.method428(vec3d.x, vec3d.z, 3.0F);
            }

            this.class3.method429(vec3d.x, vec3d.z);
            this.method7943(player, vec3d, flag4, floatValue);
            this.method7940(player, vec3d, floatValue);
            this.method7941(floatValue);
            this.method7942();
         } else {
            this.method7949(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5);
            this.class3.method425(this.method7939(player, vec3d));
            this.method7949(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5);
            this.method7930();
         }
      }
   }

   private void method7929(Player player, Vec3 vec3d, float floatValue) {
      ArrayList arrayList = new ArrayList();

      for (String str : this.method7931()) {
         switch (str) {
            case "sit":
            case "lie":
            case "wander":
            case "play":
            case "orbit":
               arrayList.add(str);
               break;
            case "shoulder":
            case "head":
               if (player.onGround() && !player.isSprinting()) {
                  arrayList.add(str);
               }
         }
      }

      if (arrayList.isEmpty()) {
         this.intValue13 = 200;
      } else {
         this.text = (String)arrayList.get(this.random.nextInt(arrayList.size()));
         String text = this.text;
         switch (text) {
            case "sit":
               this.intValue12 = 100 + this.random.nextInt(140);
               break;
            case "lie":
               this.intValue12 = 160 + this.random.nextInt(180);
               break;
            case "orbit":
               this.intValue12 = 120 + this.random.nextInt(100);
               this.doubleValue2 = Math.atan2(this.class3.doubleValue5 - vec3d.z, this.class3.doubleValue3 - vec3d.x);
               break;
            case "wander":
               this.intValue12 = 140;
               double d = this.random.nextDouble() * Math.PI * 2.0;
               double d2 = 1.3 + this.random.nextDouble() * 1.6;
               this.vec3d3 = new Vec3(vec3d.x + Math.cos(d) * d2, vec3d.y, vec3d.z + Math.sin(d) * d2);
               break;
            case "play":
               this.intValue12 = 50;
               if (!this.animationState.method6540(this.class15, "play", floatValue)) {
                  this.animationState.method6540(this.class15, "happy", floatValue);
               }
               break;
            default:
               this.intValue12 = 260 + this.random.nextInt(340);
         }
      }
   }

   private void method7930() {
      if (!this.text.isEmpty()) {
         this.text = "";
         this.vec3d3 = null;
         this.intValue13 = 70 + this.random.nextInt(160);
      }
   }

   private List<String> method7931() {
      if (!this.data3.getItems().isEmpty()) {
         return this.data3.getItems();
      } else {
         return switch (this.data3.getType4()) {
            case FLYER -> List.of("orbit", "sit", "play");
            case FLOATER -> List.of("orbit", "wander", "play");
            case HOPPER -> List.of("pace", "sit", "wander", "play");
            case CRAWLER -> List.of("sit", "lie", "wander");
            default -> List.of("pace", "sit", "lie", "wander", "play");
         };
      }
   }

   private void method7932() {
      this.flag4 = true;
      this.vec3d4 = new Vec3(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5);
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.class3.doubleValue9 = this.class3.doubleValue10 = this.class3.doubleValue11 = 0.0;
   }

   private void method7933(Player player, Vec3 vec3d, boolean flag, boolean flag2, float floatValue) {
      this.floatValue6 = Math.min(1.0F, this.floatValue6 + 0.16F);
      this.intValue12--;
      Vec3 vec = this.method7934(player, vec3d, player.yBodyRot);
      this.class3.doubleValue3 = this.class3.doubleValue6 = vec.x;
      this.class3.doubleValue4 = this.class3.doubleValue7 = vec.y;
      this.class3.doubleValue5 = this.class3.doubleValue8 = vec.z;
      this.class3.floatValue = this.class3.floatValue2 = player.yBodyRot;
      this.class3.doubleValue13 = 0.0;
      boolean flag3 = !player.onGround() && !player.isPassenger() && !player.isInWater();
      if (this.intValue12 > 0 && !flag && !flag2 && !flag3 && !player.isDeadOrDying()) {
         this.class3.floatValue3 = this.class3.floatValue3 + (0.0F - this.class3.floatValue3) * 0.2F;
         this.method7941(floatValue);
      } else {
         this.flag4 = false;
         this.floatValue6 = 0.0F;
         this.floatValue7 = 0.0F;
         double d = Math.toRadians(player.yBodyRot + (this.intValue2 > 0 ? -90.0 : 90.0));
         this.class3.doubleValue9 = -Math.sin(d) * 0.12;
         this.class3.doubleValue11 = Math.cos(d) * 0.12;
         this.class3.doubleValue10 = 0.18;
         this.class3.flag = false;
         this.method7930();
         this.animationState.method6540(this.class15, "react", floatValue);
      }
   }

   public Vec3 method7934(Player player, Vec3 vec3d, float yBodyRot) {
      double d = Math.toRadians(yBodyRot);
      boolean flag = this.text.equals("head");
      double d2 = flag ? (player.isShiftKeyDown() ? 1.72 : 2.0) : (player.isShiftKeyDown() ? 1.24 : 1.5);
      double d3 = flag ? 0.0 : 0.31 * this.intValue2;
      double d4 = -Math.cos(d);
      double d5 = -Math.sin(d);
      double d6 = Math.sin(d) * 0.03;
      double d7 = -Math.cos(d) * 0.03;
      return new Vec3(vec3d.x + d4 * d3 + d6, vec3d.y + d2, vec3d.z + d5 * d3 + d7);
   }

   public float method7935() {
      return this.text.equals("head") ? 0.5F : 0.55F;
   }

   private void method7936() {
      if (this.intValue13 > 0) {
         this.intValue13--;
      }

      if (this.intValue3 > 0) {
         this.intValue3--;
      }

      if (this.intValue4 > 0) {
         this.intValue4--;
      }

      if (this.intValue5 > 0) {
         this.intValue5--;
      }

      if (this.intValue6 > 0) {
         this.intValue6--;
      }

      if (this.intValue7 > 0) {
         this.intValue7--;
      }

      if (this.intValue10 > 0) {
         this.intValue10--;
      }

      if (this.intValue11 > 0) {
         this.intValue11--;
         if (this.intValue11 == 0) {
            this.vec3d2 = null;
         }
      }
   }

   private Vec3 method7937(Player player, Vec3 vec3d, LivingEntity livingEntity, boolean flag, boolean flag2, boolean flag3, double d) {
      if (this.vec3d2 != null) {
         return this.vec3d2;
      } else if (!flag) {
         String text = this.text;
         switch (text) {
            case "wander":
               if (this.vec3d3 != null) {
                  if (method7951(this.class3.doubleValue3, this.class3.doubleValue5, this.vec3d3.x, this.vec3d3.z) < 0.4) {
                     this.intValue12 = Math.min(this.intValue12, 20);
                  }

                  return this.vec3d3;
               }
               break;
            case "orbit":
               this.doubleValue2 += 0.055;
               double d2 = 1.7 + 0.2 * Math.sin(this.ticks * 0.07);
               return new Vec3(vec3d.x + Math.cos(this.doubleValue2) * d2, vec3d.y, vec3d.z + Math.sin(this.doubleValue2) * d2);
            case "shoulder":
            case "head":
               return vec3d;
            case "sit":
            case "lie":
            case "play":
               if (!(d > 3.5)) {
                  return new Vec3(this.class3.doubleValue3, vec3d.y, this.class3.doubleValue5);
               }

               this.method7930();
         }

         if (flag2) {
            double d2 = Math.toRadians(this.floatValue5);
            double d3 = -Math.sin(d2);
            double d4 = Math.cos(d2);
            double d5 = -Math.cos(d2);
            double d6 = -Math.sin(d2);
            return flag3 && this.method7931().contains("pace")
               ? new Vec3(vec3d.x + d5 * 1.15 * this.intValue2 + d3 * 0.35, vec3d.y, vec3d.z + d6 * 1.15 * this.intValue2 + d4 * 0.35)
               : new Vec3(vec3d.x - d3 * 1.3 + d5 * 0.9 * this.intValue2, vec3d.y, vec3d.z - d4 * 1.3 + d6 * 0.9 * this.intValue2);
         } else {
            return this.method7938(vec3d, d, 0.9, 3.0);
         }
      } else {
         Vec3 position = livingEntity != null && livingEntity.isAlive() ? livingEntity.position() : null;
         double d2 = 2.2;
         if (position != null) {
            double d3 = vec3d.x - position.x;
            double d4 = vec3d.z - position.z;
            double d5 = Math.max(0.001, Math.sqrt(d3 * d3 + d4 * d4));
            return new Vec3(vec3d.x + d3 / d5 * d2, vec3d.y, vec3d.z + d4 / d5 * d2);
         } else {
            return this.method7938(vec3d, d, 1.6, 2.4);
         }
      }
   }

   private Vec3 method7938(Vec3 vec3d, double d, double d2, double d3) {
      if (d >= d2 && d <= d3) {
         return new Vec3(this.class3.doubleValue3, vec3d.y, this.class3.doubleValue5);
      } else {
         double d4 = this.class3.doubleValue3 - vec3d.x;
         double d5 = this.class3.doubleValue5 - vec3d.z;
         double d6 = Math.sqrt(d4 * d4 + d5 * d5);
         if (d6 < 0.05) {
            double d7 = Math.toRadians(this.floatValue5 + 90.0 * this.intValue2);
            d4 = -Math.sin(d7);
            d5 = Math.cos(d7);
            d6 = 1.0;
         }

         double d7 = (d2 + d3) / 2.0 * 0.85;
         return new Vec3(vec3d.x + d4 / d6 * d7, vec3d.y, vec3d.z + d5 / d6 * d7);
      }
   }

   private Vec3 method7939(Player player, Vec3 vec3d) {
      double d = Math.toRadians(player.yBodyRot);
      double x = vec3d.x + Math.sin(d) * 1.25 - Math.cos(d) * 0.9 * this.intValue2;
      double z = vec3d.z - Math.cos(d) * 1.25 - Math.sin(d) * 0.9 * this.intValue2;
      return new Vec3(x, vec3d.y + (this.class3.method423() ? this.data3.hover() : 0.0), z);
   }

   private void method7940(Player player, Vec3 vec3d, float floatValue) {
      if (!this.flag3) {
         double d = this.class3.doubleValue3 - vec3d.x;
         double d2 = this.class3.doubleValue4 - vec3d.y;
         double d3 = this.class3.doubleValue5 - vec3d.z;
         double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
         if (this.intValue4 == 0 && player.isShiftKeyDown() && d4 < 2.6 && this.animationState.method6540(this.class15, "happy", floatValue)) {
            this.method7948(ParticleTypes.HEART, 3, 0.35, 0.05);
            this.intValue4 = 120;
            this.intValue3 = Math.max(this.intValue3, 40);
         } else {
            if (this.intValue3 == 0 && d4 < 5.5) {
               Vec3 eyePosition = player.getEyePosition();
               Vec3 viewVector = player.getViewVector(1.0F);
               Vec3 vec = new Vec3(
                     this.class3.doubleValue3 - eyePosition.x,
                     this.class3.doubleValue4 + this.class3.doubleValue2 * 0.5 - eyePosition.y,
                     this.class3.doubleValue5 - eyePosition.z
                  )
                  .normalize();
               if (viewVector.dot(vec) > 0.975 && this.animationState.method6540(this.class15, "react", floatValue)) {
                  this.intValue3 = 160;
               }
            }
         }
      }
   }

   private void method7941(float floatValue) {
      this.doubleValue = this.doubleValue + (this.class3.doubleValue13 - this.doubleValue) * 0.3;
      if (this.flag ? this.doubleValue < 0.3 : this.doubleValue > 0.85) {
         this.flag = !this.flag;
      }

      if (this.running ? this.doubleValue < 3.4 : this.doubleValue > 4.4) {
         this.running = !this.running;
      }

      this.running = this.running & this.flag;
      String text;
      if (this.flag4) {
         text = "perch";
      } else if (this.flag3) {
         text = "sleep";
      } else if (this.text.equals("sit") && !this.flag) {
         text = "sit";
      } else if (this.text.equals("lie") && !this.flag) {
         text = "lie";
      } else if (this.class3.method423()) {
         text = !this.flag && !this.text.equals("orbit") ? "idle" : "fly";
      } else if (!this.class3.flag && Math.abs(this.class3.doubleValue10) > 0.12) {
         text = "jump";
      } else if (this.running) {
         text = "run";
      } else if (this.flag) {
         text = "walk";
      } else {
         text = "idle";
      }

      if (text.equals("walk")) {
         this.animationState.setFloatValue6((float)Mth.clamp(this.doubleValue / 2.4, 0.55, 1.7));
      } else if (text.equals("run")) {
         this.animationState.setFloatValue6((float)Mth.clamp(this.doubleValue / 5.2, 0.75, 1.6));
      } else if (text.equals("fly")) {
         this.animationState.setFloatValue6((float)Mth.clamp(0.8 + this.doubleValue / 8.0, 0.8, 1.5));
      } else {
         this.animationState.setFloatValue6(1.0F);
      }

      this.animationState.method6539(this.class15, text, floatValue);
      if (text.equals("idle") && this.intValue6 == 0 && !this.animationState.isFlag()) {
         this.animationState.method6540(this.class15, "idle2", floatValue);
         this.intValue6 = 200 + this.random.nextInt(240);
      }
   }

   private void method7942() {
      if (this.flag3) {
         if (this.intValue7 == 0) {
            this.method7950(
               ParticleTypes.NOTE, this.class3.doubleValue3, this.class3.doubleValue4 + this.class3.doubleValue2 + 0.1, this.class3.doubleValue5, 0.3, 0.0, 0.0
            );
            this.intValue7 = 70 + this.random.nextInt(30);
         }
      }
   }

   private void method7943(Player player, Vec3 vec3d, boolean flag, float floatValue) {
      String text = this.data3.getText() == null ? "" : this.data3.getText();
      switch (text) {
         case "hide_in_shell":
            if (flag && !this.animationState.getCurrent().equals("hide")) {
               this.animationState.method6540(this.class15, "hide", floatValue);
            } else if (!flag && this.animationState.getCurrent().equals("hide")) {
               this.animationState.method6541(floatValue);
            }
            break;
         case "ghost_phase":
            float f = this.intValue11 > 0 ? 0.35F + 0.25F * Mth.sin(this.ticks * 0.6F) : 0.85F;
            this.alpha = this.alpha + (f - this.alpha) * 0.2F;
            break;
         case "shadow_step":
            double d = method7951(this.class3.doubleValue3, this.class3.doubleValue5, vec3d.x, vec3d.z);
            if (d > 6.0 && this.intValue5 == 0) {
               this.method7948(ParticleTypes.LARGE_SMOKE, 8, 0.3, 0.02);
               this.class3.method425(this.method7939(player, vec3d));
               this.method7948(ParticleTypes.SMOKE, 10, 0.3, 0.02);
               this.animationState.method6540(this.class15, "quirk", floatValue);
               this.intValue5 = 60;
            }
            break;
         case "ink_splash":
            if (player.hurtTime == 9 && this.intValue5 == 0) {
               this.method7948(ParticleTypes.SQUID_INK, 12, 0.35, 0.08);
               this.animationState.method6540(this.class15, "quirk", floatValue);
               this.intValue5 = 40;
            }

            if (this.ticks % 30 == 0 && !this.flag3) {
               this.method7950(
                  ParticleTypes.BUBBLE_POP,
                  this.class3.doubleValue3,
                  this.class3.doubleValue4 + this.class3.doubleValue2,
                  this.class3.doubleValue5,
                  0.0,
                  0.02,
                  0.0
               );
            }
            break;
         default:
            this.method7944(text, player, vec3d, flag, floatValue);
      }
   }

   private void method7944(String text, Player player, Vec3 vec3d, boolean flag, float floatValue) {
      if (this.intValue5 <= 0 && !this.flag3 && !flag && !(this.class3.doubleValue13 > 3.0) && !text.isEmpty()) {
         this.intValue5 = 260 + this.random.nextInt(260);
         boolean flag2 = this.animationState.method6540(this.class15, "quirk", floatValue);
         switch (text) {
            case "sneeze_sparks":
               this.method7947(ParticleTypes.FLAME, 6, 0.12);
               this.method7947(ParticleTypes.LAVA, 2, 0.05);
               break;
            case "smoke_rings":
               for (int i = 0; i < 10; i++) {
                  double d = i / 10.0 * Math.PI * 2.0;
                  this.method7950(
                     ParticleTypes.SMOKE,
                     this.class3.doubleValue3 + Math.cos(d) * 0.2,
                     this.class3.doubleValue4 + this.class3.doubleValue2 + 0.2,
                     this.class3.doubleValue5 + Math.sin(d) * 0.2,
                     0.0,
                     0.03,
                     0.0
                  );
               }
               break;
            case "firefly_dance":
               for (int i = 0; i < 8; i++) {
                  double d = i / 8.0 * Math.PI * 2.0;
                  this.method7950(
                     ParticleTypes.END_ROD,
                     this.class3.doubleValue3 + Math.cos(d) * 0.6,
                     this.class3.doubleValue4 + 0.4,
                     this.class3.doubleValue5 + Math.sin(d) * 0.6,
                     -Math.sin(d) * 0.03,
                     0.02,
                     Math.cos(d) * 0.03
                  );
               }
               break;
            case "scan_beam":
               for (int i = 0; i < 10; i++) {
                  double d = Math.toRadians(this.class3.floatValue + 90.0F) + (i - 5) * 0.12;
                  this.method7950(
                     ParticleTypes.ELECTRIC_SPARK,
                     this.class3.doubleValue3 + Math.cos(d) * 1.2,
                     this.class3.doubleValue4 + 0.2,
                     this.class3.doubleValue5 + Math.sin(d) * 1.2,
                     0.0,
                     0.0,
                     0.0
                  );
               }
               break;
            case "ghost_phase":
               this.intValue11 = 60;
               break;
            case "spore_puff":
               this.method7948(ParticleTypes.SPORE_BLOSSOM_AIR, 10, 0.4, 0.01);
               this.method7948(ParticleTypes.MYCELIUM, 8, 0.3, 0.01);
            case "head_spin":
               break;
            case "slime_bounce":
               this.class3.method427(0.75);
               this.floatValue3 = 1.0F;
               this.method7948(ParticleTypes.ITEM_SLIME, 8, 0.3, 0.05);
               break;
            case "catch_stars":
               double d = this.random.nextDouble() * Math.PI * 2.0;
               this.vec3d2 = new Vec3(vec3d.x + Math.cos(d) * 2.2, vec3d.y, vec3d.z + Math.sin(d) * 2.2);
               this.intValue11 = 40;

               for (int i = 0; i < 6; i++) {
                  this.method7950(ParticleTypes.END_ROD, this.vec3d2.x, this.vec3d2.y + 1.6 + i * 0.2, this.vec3d2.z, 0.0, -0.08, 0.0);
               }
               break;
            case "offer_flower":
               this.vec3d2 = new Vec3(vec3d.x + (this.class3.doubleValue3 - vec3d.x) * 0.3, vec3d.y, vec3d.z + (this.class3.doubleValue5 - vec3d.z) * 0.3);
               this.intValue11 = 50;
               this.method7948(ParticleTypes.HAPPY_VILLAGER, 4, 0.3, 0.02);
               break;
            case "bear_hug":
               this.vec3d2 = new Vec3(vec3d.x + (this.class3.doubleValue3 - vec3d.x) * 0.2, vec3d.y, vec3d.z + (this.class3.doubleValue5 - vec3d.z) * 0.2);
               this.intValue11 = 55;
               this.method7947(ParticleTypes.HEART, 3, 0.15);
               break;
            case "spooky_grin":
               this.method7947(ParticleTypes.SOUL_FIRE_FLAME, 6, 0.08);
               break;
            case "rain_cloud":
               for (int i = 0; i < 12; i++) {
                  this.method7950(
                     ParticleTypes.FALLING_WATER,
                     this.class3.doubleValue3 + (this.random.nextDouble() - 0.5) * 0.5,
                     this.class3.doubleValue4 + this.class3.doubleValue2 + 0.3,
                     this.class3.doubleValue5 + (this.random.nextDouble() - 0.5) * 0.5,
                     0.0,
                     0.0,
                     0.0
                  );
               }
               break;
            default:
               if (!flag2) {
                  this.intValue5 = 400;
               }
         }
      }
   }

   public void method7945(float f, float[] floats) {
      if (WardrobeParticleEmitter.enabled()
         && mc.player != null
         && !(mc.player.distanceToSqr(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5) > 1600.0)) {
         if (this.data3.getParticleEffect() != null && this.class3.doubleValue13 > 1.2) {
            this.floatValue4 += f * 8.0F;

            while (this.floatValue4 >= 1.0F) {
               this.floatValue4--;
               this.method7950(
                  this.data3.getParticleEffect(),
                  this.class3.doubleValue3 + (this.random.nextDouble() - 0.5) * 0.2,
                  this.class3.doubleValue4 + 0.1,
                  this.class3.doubleValue5 + (this.random.nextDouble() - 0.5) * 0.2,
                  0.0,
                  0.01,
                  0.0
               );
            }
         }

         if (!this.class15.items2.isEmpty()) {
            if (this.floatValues.length != this.class15.items2.size()) {
               this.floatValues = new float[this.class15.items2.size()];
            }

            for (int i = 0; i < this.class15.items2.size(); i++) {
               WardrobeModel.Data4 data4 = this.class15.items2.get(i);

               boolean flag = switch (data4.getType3()) {
                  case MOVING -> this.class3.doubleValue13 > 0.6;
                  case IDLE -> this.class3.doubleValue13 <= 0.6;
                  case AIRBORNE -> !this.class3.flag;
                  default -> true;
               };
               if (flag && !this.flag3) {
                  this.floatValues[i] = this.floatValues[i] + data4.getFloatValue4() * f;

                  while (this.floatValues[i] >= 1.0F) {
                     this.floatValues[i]--;
                     Vec3 vec = WardrobePreview.method3148(this, floats, data4.getIntValue(), data4.getFloatValue(), data4.getFloatValue2(), data4.getFloatValue3());
                     double d = data4.getFloatValue8();
                     this.method7950(
                        data4.getParticleEffect(),
                        vec.x + (this.random.nextDouble() - 0.5) * d,
                        vec.y + (this.random.nextDouble() - 0.5) * d,
                        vec.z + (this.random.nextDouble() - 0.5) * d,
                        data4.getFloatValue5(),
                        data4.getFloatValue6(),
                        data4.getFloatValue7()
                     );
                  }
               }
            }
         }
      }
   }

   private boolean method7946(Vec3 vec3d, double amountToAddInAllDirections) {
      if (mc.level == null) {
         this.flag2 = false;
         return false;
      } else {
         if (this.ticks >= this.intValue9) {
            this.intValue9 = this.ticks + 10;
            this.flag2 = !mc.level.getEntitiesOfClass(Monster.class, new AABB(vec3d, vec3d).inflate(amountToAddInAllDirections), LivingEntity::isAlive).isEmpty();
         }

         return this.flag2;
      }
   }

   private void method7947(ParticleOptions particleEffect, int n, double d) {
      double d2 = Math.toRadians(this.class3.floatValue + 90.0);
      double x = this.class3.doubleValue3 + Math.cos(d2) * 0.25;
      double z = this.class3.doubleValue5 + Math.sin(d2) * 0.25;

      for (int i = 0; i < n; i++) {
         this.method7950(
            particleEffect,
            x,
            this.class3.doubleValue4 + this.class3.doubleValue2 * 0.6,
            z,
            Math.cos(d2) * d + (this.random.nextDouble() - 0.5) * d,
            this.random.nextDouble() * d * 0.5,
            Math.sin(d2) * d + (this.random.nextDouble() - 0.5) * d
         );
      }
   }

   private void method7948(ParticleOptions particleEffect, int n, double d, double d2) {
      for (int i = 0; i < n; i++) {
         this.method7950(
            particleEffect,
            this.class3.doubleValue3 + (this.random.nextDouble() - 0.5) * d,
            this.class3.doubleValue4 + this.class3.doubleValue2 * 0.5 + (this.random.nextDouble() - 0.5) * d,
            this.class3.doubleValue5 + (this.random.nextDouble() - 0.5) * d,
            (this.random.nextDouble() - 0.5) * d2,
            this.random.nextDouble() * d2,
            (this.random.nextDouble() - 0.5) * d2
         );
      }
   }

   private void method7949(double d, double d2, double d3) {
      for (int i = 0; i < 8; i++) {
         this.method7950(
            ParticleTypes.POOF,
            d + (this.random.nextDouble() - 0.5) * 0.4,
            d2 + this.random.nextDouble() * 0.4,
            d3 + (this.random.nextDouble() - 0.5) * 0.4,
            0.0,
            0.02,
            0.0
         );
      }
   }

   private void method7950(ParticleOptions particleEffect, double x, double y, double z, double xd, double yd, double zd) {
      if (WardrobeParticleEmitter.enabled()) {
         WardrobeParticleEmitter.spawnParticle(particleEffect, x, y, z, xd, yd, zd);
      }
   }

   private static double method7951(double d, double d2, double x, double z) {
      double d3 = d - x;
      double d4 = d2 - z;
      return Math.sqrt(d3 * d3 + d4 * d4);
   }
}
