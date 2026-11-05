package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.game.PetBehavior;
import org.zenith.utility.misc.AnimationState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class16;
import org.zenith.utility.misc.HoveredData;

public final class WardrobePreview implements MinecraftAccess {
   private static final double CONST_doubleValue = 64.0;
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Vector3f vector3f = new Vector3f();
   private static final Map<Integer, PetBehavior> items = new HashMap<>();
   private static double doubleValue;
   private static final AnimationState animationState = new AnimationState();
   private static final Class16 class16 = new Class16();
   private static WeakReference<Level> weakReference = new WeakReference<>(null);
   private long longValue;

   public WardrobePreview() {
      EventManager.register(this);
   }

   @EventTarget
   public void method3143(WorldRenderEvent event) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && mc.level != null && mc.player != null) {
         if (weakReference.get() != mc.level) {
            weakReference = new WeakReference<>(mc.level);
            items.clear();
         }

         long nanoTime = System.nanoTime();
         double d = this.longValue == 0L ? 0.0 : Math.min(0.25, (nanoTime - this.longValue) / 1.0E9);
         this.longValue = nanoTime;
         float floatValue = event.getFloatValue();
         doubleValue += d;
         float f = (float)(doubleValue % 100000.0);
         Vec3 position = mc.gameRenderer.mainCamera().position();
         ArrayList<WardrobePreview.Data> datas = new ArrayList();
         HashSet hashSet = new HashSet();

         for (AbstractClientPlayer player : mc.level.players()) {
            boolean flag = player == mc.player;
            if ((flag ? wardrobe.showSelf.isEnabled() : wardrobe.showOthers.isEnabled())
               && !player.isSpectator()
               && !player.isInvisible()
               && !(player.distanceToSqr(position) > 4096.0)) {
               HoveredData hoveredData = wardrobe.method2499(wardrobe.method2498(player.getGameProfile().name(), flag), FocusSlotEnum.PET);
               if (hoveredData != null) {
                  Class15 class2 = wardrobe.getRegistry().method1427(hoveredData);
                  if (class2 != null && class2.data3 != null) {
                     PetBehavior petBehavior = items.get(player.getId());
                     if (petBehavior == null || petBehavior.hoveredData != hoveredData) {
                        petBehavior = new PetBehavior(hoveredData, class2);
                        items.put(player.getId(), petBehavior);
                     }

                     hashSet.add(player.getId());
                     petBehavior.method7926(player, flag ? method3149() : null, d, f);
                     float alpha1 = petBehavior.class3.method438();
                     Vec3 vec3d = petBehavior.class3.method435(alpha1);
                     float rotLerp = petBehavior.class3.method436(alpha1);
                     float f2 = wardrobe.petScale.getValue3() * petBehavior.data3.getFloatValue();
                     float[] floats = method3144(petBehavior, f, alpha1);
                     if (petBehavior.isFlag4()) {
                        Vec3 vec = player.getPosition(floatValue);
                        rotLerp = Mth.rotLerp(floatValue, player.yBodyRotO, player.yBodyRot);
                        Vec3 vec2 = petBehavior.method7934(player, vec, rotLerp);
                        float alpha12 = petBehavior.method7927(alpha1);
                        Vec3 vec3d4 = petBehavior.getVec3d4();
                        if (alpha12 < 1.0F && vec3d4 != null) {
                           float f3 = alpha12 * alpha12 * (3.0F - 2.0F * alpha12);
                           vec3d = vec3d4.lerp(vec2, f3).add(0.0, Math.sin(Math.PI * alpha12) * 0.55, 0.0);
                        } else {
                           vec3d = vec2;
                        }

                        f2 *= Mth.lerp(alpha12, 1.0F, petBehavior.method7935());
                        petBehavior.method7945((float)d, floats);
                        if (flag && mc.options.getCameraType().isFirstPerson() && alpha12 > 0.5F) {
                           continue;
                        }
                     } else {
                        petBehavior.method7945((float)d, floats);
                     }

                     int lightCoords = net.minecraft.util.LightCoordsUtil.getLightCoords(mc.level, BlockPos.containing(vec3d.x, vec3d.y + 0.3, vec3d.z));
                     datas.add(new WardrobePreview.Data(petBehavior, vec3d, rotLerp, petBehavior.class3.method437(alpha1), (float[])floats.clone(), lightCoords, f2));
                  }
               }
            }
         }

         items.keySet().retainAll(hashSet);
         if (!datas.isEmpty()) {
            PoseStack matrixStack = event.getMatrixStack();
            OutlineVertexProvider.method1542(var3x -> {
                  for (WardrobePreview.Data var5x : datas) {
                     matrixStack.pushPose();

                     try {
                        matrixStack.translate(var5x.vec3d.x - position.x, var5x.vec3d.y - position.y, var5x.vec3d.z - position.z);
                        method3145(matrixStack, var5x.floatValue, var5x.floatValue3, var5x.petBehavior.getFloatValue3());
                        int n = Math.round(Math.max(0.1F, Math.min(1.0F, var5x.petBehavior.getAlpha())) * 255.0F);
                        method3146(
                           var5x.petBehavior.class15,
                           var5x.floatValues,
                           matrixStack,
                           var3x,
                           var5x.intValue,
                           var5x.petBehavior.hoveredData.getRarity().isFlag(),
                           n << 24 | 16777215,
                           true
                        );
                     } finally {
                        matrixStack.popPose();
                     }
                  }
               }
            );
         }
      } else {
         items.clear();
      }
   }

   private static float[] method3144(PetBehavior petBehavior, float floatValue, float alpha1) {
      float[] floats = petBehavior.class16.method2139(petBehavior.class15, petBehavior.animationState, floatValue, null);
      int n = petBehavior.class15.method2031("head");
      if (n >= 0 && !petBehavior.isFlag3()) {
         floats[n * 9 + 1] = floats[n * 9 + 1] + (float)Math.toRadians(-petBehavior.class3.method437(alpha1));
      }

      return floats;
   }

   private static void method3145(PoseStack matrixStack, float floatValue, float f, float f2) {
      matrixStack.rotate(new Quaternionf().rotationY((float)Math.toRadians(180.0 - floatValue)));
      float f3 = 1.0F + f2 * 0.18F;
      matrixStack.scale(f * f3, f / f3, f * f3);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.translate(0.0F, -1.501F, 0.0F);
   }

   private static void method3146(Class15 key, float[] floatValues, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int intValue, boolean flag, int n, boolean flag2) {
      BoneMatrices.method4787(key, floatValues, 6, matrixStack, orderedRenderCommandQueue, intValue, OverlayTexture.NO_OVERLAY, flag2, flag, n);
   }

   public static void method3147(HoveredData hoveredData, Class15 key, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int intValue, float floatValue) {
      if (key != null) {
         animationState.method6539(key, "idle", floatValue);
         float[] floatValues = class16.method2139(key, animationState, floatValue, null);
         method3146(key, floatValues, matrixStack, orderedRenderCommandQueue, intValue, hoveredData.getRarity().isFlag(), -1, true);
      }
   }

   public static Vec3 method3148(PetBehavior petBehavior, float[] floats, int intValue, float floatValue, float f, float f2) {
      float alpha1 = petBehavior.class3.method438();
      Vec3 vec = petBehavior.class3.method435(alpha1);
      float xyz = Wardrobe.method2488() == null ? 1.0F : Wardrobe.method2488().petScale.getValue3() * petBehavior.data3.getFloatValue();
      matrix4f.identity();
      matrix4f.translate((float)vec.x, (float)vec.y, (float)vec.z);
      matrix4f.rotateY((float)Math.toRadians(180.0 - petBehavior.class3.method436(alpha1)));
      matrix4f.scale(xyz);
      matrix4f.scale(-1.0F, -1.0F, 1.0F);
      matrix4f.translate(0.0F, -1.501F, 0.0F);
      if (intValue >= 0) {
         BoneMatrices.method4808(petBehavior.class15, floats, intValue, matrix4f);
      }

      matrix4f.transformPosition(floatValue / 16.0F, f / 16.0F, f2 / 16.0F, vector3f);
      return new Vec3(vector3f.x, vector3f.y, vector3f.z);
   }

   private static LivingEntity method3149() {
      try {
         LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
         return livingEntity == null ? AimAssistModule.INSTANCE.method5156() : livingEntity;
      } catch (Throwable e) {
         return null;
      }
   }

   public static void clear() {
      items.clear();
   }

   private static final class Data {
      private final PetBehavior petBehavior;
      private final Vec3 vec3d;
      private final float floatValue;
      private final float floatValue2;
      private final float[] floatValues;
      private final int intValue;
      private final float floatValue3;

      public PetBehavior petBehavior() {
         return this.petBehavior;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.petBehavior, other.petBehavior) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(petBehavior, vec3d, floatValue, floatValue2, floatValues, intValue, floatValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "petBehavior=" + this.petBehavior + ", " + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValues=" + this.floatValues + ", " + "intValue=" + this.intValue + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data(PetBehavior petBehavior, Vec3 vec3d, float floatValue, float f, float[] floatValues, int intValue, float f2) {
         this.petBehavior = petBehavior;
         this.vec3d = vec3d;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValues = floatValues;
         this.intValue = intValue;
         this.floatValue3 = f2;
      }

      public PetBehavior getPetBehavior() {
         return this.petBehavior;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }
}
