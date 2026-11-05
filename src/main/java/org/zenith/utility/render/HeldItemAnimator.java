package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.mixin.accessors.HeldItemRendererAccessor;

public final class HeldItemAnimator implements MinecraftAccess {
   private static final Vector3f vector3f = new Vector3f(0.73597F, -0.43693F, -0.85587F);
   private static final Vector3f vector3f2 = new Vector3f(0.0F, -0.12F, -0.5F);
   private static final Quaternionf quaternionf = new Quaternionf(0.12325F, 0.18889F, 0.71204F, 0.66493F);
   private static final Vector3f vector3f3 = new Vector3f(-0.8F, 0.25F, -0.45F).normalize();
   private static final Vector3f vector3f4 = method4770();
   private static final float CONST_floatValue = 0.15F;
   private static final float CONST_floatValue2 = 0.75F;
   private static final float CONST_floatValue3 = 0.6F;
   private static final float CONST_floatValue4 = 0.55F;
   private static final float[][] CONST_floatValues = new float[][]{
      {0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
      {0.45F, -10.0F, 0.004F, 0.018F, 0.008F},
      {0.85F, 0.0F, 0.0F, 0.006F, 0.0F},
      {1.55F, 58.0F, 0.012F, 0.024F, 0.022F},
      {2.05F, 52.0F, 0.01F, 0.02F, 0.02F},
      {2.7F, -24.0F, -0.006F, 0.01F, 0.0F},
      {3.25F, 2.0F, 0.0F, 0.003F, 0.0F},
      {3.7F, 0.0F, 0.0F, 0.0F, 0.0F},
      {4.2F, 0.0F, 0.0F, 0.0F, 0.0F}
   };
   private static HeldItemAnimator INSTANCE;
   private long longValue = -1L;
   private boolean flag;
   private int slot = -1;

   public HeldItemAnimator() {
      INSTANCE = this;
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)minecraft -> this.tick());
   }

   private static Vector3f method4770() {
      Vector3f vec = new Vector3f(vector3f2).negate().normalize();
      return vec.sub(new Vector3f(vector3f3).mul(vec.dot(vector3f3))).normalize();
   }

   @EventTarget
   private void method4771(KeyEvent event) {
      if (event.isPress(74) && this.longValue < 0L && method4772()) {
         InteractionHand hand = method4773();
         this.flag = hand != null && !mc.player.getItemInHand(hand).isEmpty();
         this.slot = mc.player.getInventory().getSelectedSlot();
         this.longValue = System.nanoTime();
      }
   }

   private void tick() {
      if (this.longValue >= 0L) {
         if (!method4772()
            || mc.player.isSwinging()
            || mc.player.isUsingItem()
            || mc.player.getInventory().getSelectedSlot() != this.slot
            || this.method4774() >= CONST_floatValues[CONST_floatValues.length - 1][0]) {
            this.stop();
         }
      }
   }

   private void stop() {
      this.longValue = -1L;
      InteractionHand hand = method4773();
      if (this.flag && hand != null && mc.gameRenderer != null) {
         mc.player.itemUsed(hand);
      }

      this.flag = false;
   }

   private static boolean method4772() {
      if (mc.player != null && mc.gui.screen() == null && mc.options.getCameraType().isFirstPerson() && !mc.player.isInvisible()) {
         Wardrobe wardrobe = Wardrobe.method2488();
         return wardrobe != null && wardrobe.showSelf.isEnabled()
            ? wardrobe.method2499(wardrobe.method2498(mc.player.getGameProfile().name(), true), FocusSlotEnum.WATCH) != null
            : false;
      } else {
         return false;
      }
   }

   private static InteractionHand method4773() {
      if (mc.player == null) {
         return null;
      } else {
         return mc.player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
      }
   }

   private float method4774() {
      return (float)(System.nanoTime() - this.longValue) / 1.0E9F;
   }

   public static boolean method4775(
      FirstPersonHandsAndItemsRenderer handsAndItemsRenderer, AbstractClientPlayer abstractClientPlayerEntity, InteractionHand hand, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int n
   ) {
      HeldItemAnimator heldItemAnimator = INSTANCE;
      if (heldItemAnimator != null && heldItemAnimator.longValue >= 0L && abstractClientPlayerEntity == mc.player && hand == method4773()) {
         float f = CONST_floatValues[CONST_floatValues.length - 1][0];
         float f2 = Math.min(heldItemAnimator.method4774(), f);
         float alpha = method4776(f2 / 0.6F) * method4776((f - f2) / 0.55F);
         float f3 = method4777(f2, 1);
         float f4 = (float)Math.sin(f2 * 2.4F) * 0.0035F * alpha;
         Vector3f vec = new Vector3f(vector3f2).add(method4777(f2, 2), method4777(f2, 3) + f4, method4777(f2, 4));
         Vector3f vec2 = new Vector3f(vector3f);
         if (heldItemAnimator.flag) {
            vec2.y -= 0.75F;
         }

         Vector3f vec3 = vec2.lerp(vec, alpha);
         Quaternionf slerp = new Quaternionf().slerp(quaternionf, alpha);
         Vector3f vec4 = new Vector3f(vector3f4).mul(-0.15F);
         poseStack.pushPose();
         poseStack.translate(vec3.x, vec3.y, vec3.z);
         poseStack.translate(vec4.x, vec4.y, vec4.z);
         poseStack.rotate(new Quaternionf().rotationAxis(f3 * (float) (Math.PI / 180.0), vector3f3));
         poseStack.translate(-vec4.x, -vec4.y, -vec4.z);
         poseStack.rotate(slerp);
         poseStack.translate(-vector3f.x, -vector3f.y, -vector3f.z);
         ((HeldItemRendererAccessor)(Object)handsAndItemsRenderer).zenith$renderArmHoldingItem(poseStack, submitNodeCollector, n, 0.0F, 0.0F, HumanoidArm.RIGHT, mc.gameRenderer.gameRenderState().levelRenderState.playerRenderState);
         poseStack.popPose();
         return true;
      } else {
         return false;
      }
   }

   private static float method4776(float f) {
      float f2 = Mth.clamp(f, 0.0F, 1.0F);
      return f2 * f2 * f2 * (f2 * (f2 * 6.0F - 15.0F) + 10.0F);
   }

   private static float method4777(float f, int n) {
      int n2 = 0;

      while (n2 < CONST_floatValues.length - 2 && f > CONST_floatValues[n2 + 1][0]) {
         n2++;
      }

      float f2 = CONST_floatValues[n2][0];
      float f3 = CONST_floatValues[n2 + 1][0];
      float f4 = f3 - f2;
      float f5 = Mth.clamp((f - f2) / f4, 0.0F, 1.0F);
      float f6 = CONST_floatValues[n2][n];
      float f7 = CONST_floatValues[n2 + 1][n];
      float f8 = method4778(n2, n) * f4;
      float f9 = method4778(n2 + 1, n) * f4;
      float f10 = f5 * f5;
      float f11 = f10 * f5;
      return (2.0F * f11 - 3.0F * f10 + 1.0F) * f6
         + (f11 - 2.0F * f10 + f5) * f8
         + (-2.0F * f11 + 3.0F * f10) * f7
         + (f11 - f10) * f9;
   }

   private static float method4778(int n, int n2) {
      if (n > 0 && n < CONST_floatValues.length - 1) {
         float[] floats = CONST_floatValues[n - 1];
         float[] floats2 = CONST_floatValues[n + 1];
         return (floats2[n2] - floats[n2]) / (floats2[0] - floats[0]);
      } else {
         return 0.0F;
      }
   }
}
