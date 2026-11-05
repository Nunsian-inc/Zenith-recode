package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.utility.misc.AnimationState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class16;
import org.zenith.utility.misc.HoveredData;

public final class Stage implements MinecraftAccess {
   private static final int CONST_intValue = 15728880;
   private static final long CONST_longValue = System.nanoTime();
   private static final Stage.Data[] datas = new Stage.Data[]{
      new Stage.Data("idle", 3.0F, false),
      new Stage.Data("play", 2.2F, true),
      new Stage.Data("sit", 3.2F, false),
      new Stage.Data("walk", 2.4F, false),
      new Stage.Data("lie", 3.4F, false),
      new Stage.Data("idle2", 2.6F, true),
      new Stage.Data("perch", 3.0F, false),
      new Stage.Data("happy", 1.8F, true)
   };
   private static final Stage.Data[] datas2 = new Stage.Data[]{
      new Stage.Data("idle", 3.0F, false),
      new Stage.Data("play", 2.2F, true),
      new Stage.Data("fly", 2.6F, false),
      new Stage.Data("perch", 3.2F, false),
      new Stage.Data("idle2", 2.6F, true),
      new Stage.Data("sit", 3.0F, false),
      new Stage.Data("happy", 1.8F, true)
   };
   private final AnimationState animationState = new AnimationState();
   private final Class16 class16 = new Class16();
   private Class15 class15;
   private int intValue = -1;
   private float floatValue;

   public void reset() {
      this.class15 = null;
      this.intValue = -1;
   }

   public void method893(GuiGraphicsExtractor context, HoveredData hoveredData, Class15 key, float stageX0, float stageY0, float stageX1, float stageY1, float stageYaw, float angdeg) {
      if (key != null && key.intValue3 != 0) {
         float floatValue = (float)(System.nanoTime() - CONST_longValue) / 1.0E9F;
         if (key != this.class15) {
            this.class15 = key;
            this.intValue = -1;
         }

         Stage.Data[] datas3 = key.data3 == null || key.data3.getType4() != Class15.Type4.FLYER && key.data3.getType4() != Class15.Type4.FLOATER
            ? datas
            : datas2;
         if (this.intValue < 0 || floatValue - this.floatValue >= datas3[this.intValue].getFloatValue()) {
            this.intValue = (this.intValue + 1) % datas3.length;
            this.floatValue = floatValue;
            Stage.Data data = datas3[this.intValue];
            if (data.isFlag()) {
               if (!this.animationState.method6540(key, data.getText(), floatValue)) {
                  this.animationState.method6539(key, "idle", floatValue);
               }
            } else {
               this.animationState.method6539(key, data.getText(), floatValue);
               this.animationState.method6541(floatValue);
            }
         }

         float[] floatValues = this.class16.method2139(key, this.animationState, floatValue, null);
         PoseStack poseStack = MatrixUtils.method666(context.pose());
         ScissorFlusher.flush();
         RenderState.method1878();
         RenderState.method1882(true);
         context.enableScissor((int)Math.floor(stageX0), (int)Math.floor(stageY0), (int)Math.ceil(stageX1), (int)Math.ceil(stageY1));
         poseStack.pushPose();

         try {
            float[] floatValues7 = key.floatValues7;
            float f = (floatValues7[0] + floatValues7[3]) / 2.0F;
            float f2 = (floatValues7[1] + floatValues7[4]) / 2.0F;
            float f3 = (floatValues7[2] + floatValues7[5]) / 2.0F;
            float f4 = Math.max(Math.max(floatValues7[3] - floatValues7[0], floatValues7[4] - floatValues7[1]), floatValues7[5] - floatValues7[2]);
            float f5 = Math.min(stageX1 - stageX0, stageY1 - stageY0) * 0.62F;
            float xScale = f5 / Math.max(0.05F, f4);
            poseStack.translate((stageX0 + stageX1) / 2.0F, (stageY0 + stageY1) / 2.0F + (stageY1 - stageY0) * 0.06F, 80.0F);
            poseStack.scale(xScale, xScale, -xScale);
            poseStack.rotate(new Quaternionf().rotationX((float)Math.toRadians(angdeg)));
            poseStack.rotate(new Quaternionf().rotationY((float)Math.toRadians(stageYaw)));
            poseStack.translate(-f, -f2, -f3);
            boolean flag = hoveredData.getRarity().isFlag();
            OutlineVertexProvider.method1542(var4x -> {
               for (int var5x = 0; var5x <= 6; var5x++) {
                  if (key.method2033(var5x)) {
                     BoneMatrices.method4787(key, floatValues, var5x, poseStack, var4x, 15728880, OverlayTexture.NO_OVERLAY, true, flag, -1);
                  }
               }
            });
         } finally {
            poseStack.popPose();
         }
         context.disableScissor();
      }
   }

   private static final class Data {
      private final String text;
      private final float floatValue;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, floatValue, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + "]";
      }

      private Data(String text, float floatValue, boolean flag) {
         this.text = text;
         this.floatValue = floatValue;
         this.flag = flag;
      }

      public String getText() {
         return this.text;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}
