package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;

public final class Thumbnails implements MinecraftAccess {
   private static final int CONST_intValue = 15728880;
   private static final long CONST_longValue = System.nanoTime();
   private static final float CONST_floatValue = 12.0F;
   private static final int CONST_intValue2 = 30;
   private static final float[][] CONST_floatValues = new float[][]{
      {0.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F}, {-5.0F, 2.0F, 0.0F}, {5.0F, 2.0F, 0.0F}, {-1.9F, 12.0F, 0.0F}, {1.9F, 12.0F, 0.0F}
   };
   private final List<Thumbnails.Data> items = new ArrayList<>();
   private final Quaternionf quaternionf = new Quaternionf();

   public boolean method7855(HoveredData hoveredData, Class15 class2, float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
      if (class2 != null && class2.intValue3 != 0 && this.items.size() < 30) {
         this.items.add(new Thumbnails.Data(hoveredData, class2, floatValue, f, f2, f3, f4, f5, f6, f7));
         return true;
      } else {
         return false;
      }
   }

   public void method7856() {
      this.items.clear();
   }

   public void method7857(GuiGraphicsExtractor guiGraphicsExtractor) {
      if (!this.items.isEmpty()) {
         float f = (float)(System.nanoTime() - CONST_longValue) / 1.0E9F;
         PoseStack poseStack = MatrixUtils.method666(guiGraphicsExtractor.pose());
         ScissorFlusher.flush();
         RenderState.method1878();
         RenderState.method1882(true);
         OutlineVertexProvider.method1538();

         try {
            for (Thumbnails.Data data : this.items) {
               guiGraphicsExtractor.enableScissor(
                  (int)Math.floor(data.floatValue4), (int)Math.floor(data.floatValue5), (int)Math.ceil(data.floatValue6), (int)Math.ceil(data.floatValue7)
               );
               poseStack.pushPose();

               try {
                  Class15 key = data.class15;
                  float[] floatValues7 = key.floatValues7;
                  float f2 = (floatValues7[0] + floatValues7[3]) / 2.0F;
                  float f3 = (floatValues7[1] + floatValues7[4]) / 2.0F;
                  float f4 = (floatValues7[2] + floatValues7[5]) / 2.0F;
                  float f5 = 0.0F;

                  for (int i = 0; i < 4; i++) {
                     float f6 = (i & 1) == 0 ? floatValues7[0] - f2 : floatValues7[3] - f2;
                     float f7 = (i & 2) == 0 ? floatValues7[2] - f4 : floatValues7[5] - f4;
                     f5 = Math.max(f5, (float)Math.sqrt(f6 * f6 + f7 * f7));
                  }

                  float f6 = Math.max((floatValues7[4] - floatValues7[1]) / 2.0F, f5);
                  float xScale = data.floatValue3 / 2.0F / Math.max(0.02F, f6);
                  poseStack.translate(data.floatValue, data.floatValue2, 50.0F);
                  poseStack.scale(xScale, xScale, -xScale);
                  poseStack.rotate(new Quaternionf().rotationX((float)Math.toRadians(-12.0)));
                  poseStack.rotate(this.quaternionf.rotationY((float)Math.toRadians(200.0 + f / 12.0F * 360.0 + data.floatValue8 * 25.0)));
                  poseStack.translate(-f2, -f3, -f4);
                  OutlineVertexProvider.method1542(var3x -> {
                        for (int var4x = 0; var4x <= 6; var4x++) {
                           if (key.method2033(var4x)) {
                              poseStack.pushPose();
                              if (var4x < 6) {
                                 float[] floats = CONST_floatValues[var4x];
                                 poseStack.translate(floats[0] / 16.0F, floats[1] / 16.0F, floats[2] / 16.0F);
                              }

                              BoneMatrices.method4787(
                                 key, null, var4x, poseStack, var3x, 15728880, OverlayTexture.NO_OVERLAY, true, data.hoveredData.getRarity().isFlag(), -1
                              );
                              poseStack.popPose();
                           }
                        }
                     }
                  );
               } finally {
                  poseStack.popPose();
               }
               guiGraphicsExtractor.disableScissor();
            }
         } finally {
            OutlineVertexProvider.method1539();
         }

         this.items.clear();
      }
   }

   private static final class Data {
      private final HoveredData hoveredData;
      private final Class15 class15;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;

      public HoveredData hoveredData() {
         return this.hoveredData;
      }

      public Class15 class15() {
         return this.class15;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.hoveredData, other.hoveredData) && java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(hoveredData, class15, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8);
      }

      @Override
      public String toString() {
         return "Data[" + "hoveredData=" + this.hoveredData + ", " + "class15=" + this.class15 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + "]";
      }

      private Data(HoveredData hoveredData, Class15 class2, float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
         this.hoveredData = hoveredData;
         this.class15 = class2;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
      }

      public HoveredData getHoveredData() {
         return this.hoveredData;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float size() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }
   }
}
