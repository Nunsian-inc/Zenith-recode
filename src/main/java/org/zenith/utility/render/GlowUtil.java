package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.utility.misc.ColorUtils;

public final class GlowUtil implements MinecraftAccess {
   private static final List<GlowUtil.Data3> items = new ArrayList<>();
   private static final List<GlowUtil.Data2> items2 = new ArrayList<>();
   private static final GlowUtil.Entry2 entry2 = new GlowUtil.Entry2();
   private static final GlowUtil.Entry2 entry22 = new GlowUtil.Entry2();
   private static final GlowUtil.Entry entry = new GlowUtil.Entry();
   private static final GlowUtil.Entry entry3 = new GlowUtil.Entry();
   private static Tesselator tessellator = Tesselator.getInstance();
   private static Matrix4f matrix4f = new Matrix4f();
   private static Matrix4f matrix4f2 = new Matrix4f();
   private static Matrix4f matrix4f3 = new Matrix4f();
   public static final Identifier identifier = Zenith.method1501("textures/glow.png");
   private static float[] floatValues = new float[8];
   private static int[][] intValues = new int[8][];
   private static int[] intValues2 = new int[8];
   private static int intValue = 0;

   private static void method2223(GlowUtil.Entry2 entry2) {
      intValue = 0;
      float[] floatValues2 = entry2.floatValues2;
      int n = 0;

      for (int i = entry2.size; n < i; n++) {
         float f = floatValues2[n];
         int n2 = -1;

         for (int j = 0; j < intValue; j++) {
            if (floatValues[j] == f) {
               n2 = j;
               break;
            }
         }

         if (n2 < 0) {
            if (intValue == floatValues.length) {
               floatValues = Arrays.copyOf(floatValues, intValue * 2);
               intValues = Arrays.copyOf(intValues, intValue * 2);
               intValues2 = Arrays.copyOf(intValues2, intValue * 2);
            }

            n2 = intValue;
            floatValues[n2] = f;
            intValues2[n2] = 0;
            if (intValues[n2] == null) {
               intValues[n2] = new int[64];
            }

            intValue++;
         }

         int[] original = intValues[n2];
         int n3 = intValues2[n2];
         if (n3 == original.length) {
            original = Arrays.copyOf(original, n3 * 2);
            intValues[n2] = original;
         }

         original[n3] = n;
         intValues2[n2] = n3 + 1;
      }
   }

   private static void method2224(PoseStack matrixStack, GlowUtil.Entry2 entry2) {
      PoseStack.Pose pose = matrixStack.last();
      float[] floats = entry2.floatValues;
      int[] ints = entry2.intValues;

      for (int i = 0; i < intValue; i++) {
         RenderState.method1902(floatValues[i]);
         int[] intValue2 = intValues[i];
         BufferBuilder begin = tessellator.begin(PrimitiveTopology.LINES, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
         int n = 0;

         for (int j = intValues2[i]; n < j; n++) {
            int n2 = intValue2[n];
            int n3 = n2 * 6;
            method2237(
               pose,
               begin,
               floats[n3],
               floats[n3 + 1],
               floats[n3 + 2],
               floats[n3 + 3],
               floats[n3 + 4],
               floats[n3 + 5],
               ints[n2 * 2],
               ints[n2 * 2 + 1]
            );
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
      }

      entry2.size = 0;
   }

   private static void method2225(PoseStack.Pose pose, GlowUtil.Entry entry) {
      BufferBuilder begin = tessellator.begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      float[] floatValues = entry.floatValues;
      int[] intValues = entry.intValues;
      int n = 0;

      for (int i = entry.size; n < i; n++) {
         int n2 = n * 12;
         int n3 = n * 4;
         begin.addVertex(pose, floatValues[n2], floatValues[n2 + 1], floatValues[n2 + 2]).setColor(intValues[n3]);
         begin.addVertex(pose, floatValues[n2 + 3], floatValues[n2 + 4], floatValues[n2 + 5]).setColor(intValues[n3 + 1]);
         begin.addVertex(pose, floatValues[n2 + 6], floatValues[n2 + 7], floatValues[n2 + 8]).setColor(intValues[n3 + 2]);
         begin.addVertex(pose, floatValues[n2 + 9], floatValues[n2 + 10], floatValues[n2 + 11]).setColor(intValues[n3 + 3]);
      }

      ImmediateRenderer.method8015(begin.buildOrThrow());
      entry.size = 0;
   }

   public static void method2226(PoseStack matrixStack) {
      PoseStack.Pose pose = matrixStack.last();
      if (!entry3.isEmpty()) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/position_color");
         method2225(pose, entry3);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry3.clear();
      }

      if (!entry22.isEmpty()) {
         method2223(entry22);
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/rendertype_lines");
         method2224(matrixStack, entry22);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry22.clear();
      }

      if (!entry2.isEmpty()) {
         method2223(entry2);
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/rendertype_lines");
         method2224(matrixStack, entry2);
         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry2.clear();
      }

      if (!entry.isEmpty()) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/position_color");
         method2225(pose, entry);
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry.clear();
      }
   }

   public static void method2227(BlockPos pos, VoxelShape voxelShape, int color, float width) {
      method2228(pos, voxelShape, color, width, true, false);
   }

   public static void method2228(BlockPos pos, VoxelShape voxelShape, int color, float width, boolean flag, boolean flag2) {
      if (ProjectionUtils.method303(voxelShape.bounds().move(pos))) {
         items2.stream()
            .filter(var1x -> var1x.voxelShape.equals(voxelShape))
            .findFirst()
            .ifPresentOrElse(
               var5x -> var5x.items.forEach(var5xx -> method2232(var5xx.move(pos), color, width, true, flag, flag2)),
               () -> items2.add(new GlowUtil.Data2(voxelShape, voxelShape.toAabbs()))
            );
      }
   }

   public static void method2229(BlockPos pos, VoxelShape voxelShape, int color, float width, boolean flag, boolean flag2) {
      Vec3 atLowerCornerOf = Vec3.atLowerCornerOf(pos);
      if (ProjectionUtils.method303(voxelShape.bounds().move(atLowerCornerOf))) {
         var aabbs = voxelShape.toAabbs();
         items.stream()
            .filter(var1x -> var1x.items2.equals(aabbs))
            .findFirst()
            .ifPresentOrElse(
               var5x -> {
                  var5x.items2.forEach(var5xx -> method2232(var5xx.move(atLowerCornerOf), color, width, false, flag, flag2));
                  var5x.items.forEach(var4xx -> method2245(var4xx.vec3d.add(atLowerCornerOf), var4xx.vec3d2.add(atLowerCornerOf), color, width, flag2));
               },
               () -> {
                  ArrayList var1x = new ArrayList();
                  voxelShape.forAllEdges(
                     (var1xx, var3x, var5x, var7x, y, z) -> var1x.add(
                        new GlowUtil.Data(new Vec3(var1xx, var3x, var5x), new Vec3(var7x, y, z), 0, 0, 0.0F)
                     )
                  );
                  items.add(new GlowUtil.Data3(voxelShape, var1x, voxelShape.toAabbs()));
               }
            );
      }
   }

   public static void method2230(AABB aabb, int rgb, float width) {
      method2231(aabb, rgb, rgb, width);
   }

   public static void method2231(AABB box, int rgb, int color, float width) {
      method2233(box, rgb, color, width, true, true, false);
   }

   public static void method2232(AABB aabb, int color, float width, boolean flag, boolean flag2, boolean flag3) {
      method2233(aabb, color, color, width, flag, flag2, flag3);
   }

   public static void method2233(AABB box, int rgb, int color, float width, boolean flag, boolean flag2, boolean flag3) {
      box = box.inflate(0.001);
      if (ProjectionUtils.method303(box)) {
         double minX = box.minX;
         double minY = box.minY;
         double minZ = box.minZ;
         double maxX = box.maxX;
         double maxY = box.maxY;
         double maxZ = box.maxZ;
         boolean flag4 = EntityESPModule.INSTANCE.method567();
         int n = flag4 ? ColorUtils.method6439(rgb, 0.9F) : ColorUtils.method6439(rgb, 0.1F);
         int n2 = flag4 ? ColorUtils.method6439(color, 0.1F) : n;
         int color2 = flag4 ? ColorUtils.method6439(rgb, 0.5F) : rgb;
         int color3 = flag4 ? ColorUtils.method6439(color, 1.0F) : rgb;
         Vec3 position = mc.getEntityRenderDispatcher().camera.position();
         float f = (float)(minX - position.x);
         float f2 = (float)(minY - position.y);
         float f3 = (float)(minZ - position.z);
         float f4 = (float)(maxX - position.x);
         float f5 = (float)(maxY - position.y);
         float f6 = (float)(maxZ - position.z);
         if (flag2) {
            GlowUtil.Entry entry4 = flag3 ? entry : entry3;
            entry4.method2253(f, f2, f3, f4, f2, f3, f4, f2, f6, f, f2, f6, n, n, n, n);
            entry4.method2253(f, f2, f3, f, f5, f3, f4, f5, f3, f4, f2, f3, n, n2, n2, n);
            entry4.method2253(f4, f2, f3, f4, f5, f3, f4, f5, f6, f4, f2, f6, n, n2, n2, n);
            entry4.method2253(f, f2, f6, f4, f2, f6, f4, f5, f6, f, f5, f6, n, n, n2, n2);
            entry4.method2253(f, f2, f3, f, f2, f6, f, f5, f6, f, f5, f3, n, n, n2, n2);
            entry4.method2253(f, f5, f3, f, f5, f6, f4, f5, f6, f4, f5, f3, n2, n2, n2, n2);
         }

         if (flag) {
            GlowUtil.Entry2 entry23 = flag3 ? entry2 : entry22;
            entry23.method2254(f, f2, f3, f4, f2, f3, color2, color2, width);
            entry23.method2254(f4, f2, f3, f4, f2, f6, color2, color2, width);
            entry23.method2254(f4, f2, f6, f, f2, f6, color2, color2, width);
            entry23.method2254(f, f2, f6, f, f2, f3, color2, color2, width);
            entry23.method2254(f, f2, f6, f, f5, f6, color2, color3, width);
            entry23.method2254(f, f2, f3, f, f5, f3, color2, color3, width);
            entry23.method2254(f4, f2, f6, f4, f5, f6, color2, color3, width);
            entry23.method2254(f4, f2, f3, f4, f5, f3, color2, color3, width);
            entry23.method2254(f, f5, f3, f4, f5, f3, color3, color3, width);
            entry23.method2254(f4, f5, f3, f4, f5, f6, color3, color3, width);
            entry23.method2254(f4, f5, f6, f, f5, f6, color3, color3, width);
            entry23.method2254(f, f5, f6, f, f5, f3, color3, color3, width);
         }
      }
   }

   public static void method2234(@NotNull PoseStack matrixStack, @NotNull VertexConsumer begin, Vec3 vec, Vec3 vec2, int color) {
      method2236(matrixStack, begin, vec.toVector3f(), vec2.toVector3f(), color, color);
   }

   public static void method2235(@NotNull PoseStack matrixStack, @NotNull VertexConsumer begin, Vec3 vec, Vec3 vec2, int color, int n) {
      method2236(matrixStack, begin, vec.toVector3f(), vec2.toVector3f(), color, n);
   }

   public static void method2236(@NotNull PoseStack matrixStack, @NotNull VertexConsumer begin, Vector3f vector3f, Vector3f vec, int color, int n) {
      method2237(matrixStack.last(), begin, vector3f.x, vector3f.y, vector3f.z, vec.x, vec.y, vec.z, color, n);
   }

   private static void method2237(
      PoseStack.Pose pose, VertexConsumer begin, float x, float y, float z, float f, float y2, float z2, int color, int n
   ) {
      float f2 = f - x;
      float f3 = y2 - y;
      float f4 = z2 - z;
      float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
      float x2 = f2 / f5;
      float y3 = f3 / f5;
      float z3 = f4 / f5;
      float width = RenderState.getFloatValue4();
      begin.addVertex(pose, x, y, z).setColor(color).setNormal(pose, x2, y3, z3).setLineWidth(width);
      begin.addVertex(pose, f, y2, z2).setColor(n).setNormal(pose, x2, y3, z3).setLineWidth(width);
   }

   public static void method2238(@NotNull PoseStack.Pose pose, @NotNull VertexConsumer consumer, Vec3 vec, Vec3 vec2, Vec3 vec3, Vec3 vec4, int color) {
      method2241(pose, consumer, vec.toVector3f(), vec2.toVector3f(), vec3.toVector3f(), vec4.toVector3f(), color, color, color, color);
   }

   public static void method2239(
      @NotNull PoseStack.Pose pose, @NotNull VertexConsumer consumer, Vector3f vector3f, Vector3f position, Vector3f vec, Vector3f vec2, int color
   ) {
      method2241(pose, consumer, vector3f, position, vec, vec2, color, color, color, color);
   }

   public static void method2240(
      @NotNull PoseStack.Pose pose, @NotNull VertexConsumer consumer, Vec3 vec, Vec3 vec2, Vec3 vec3, Vec3 vec4, int color, int n, int n2, int n3
   ) {
      method2241(pose, consumer, vec.toVector3f(), vec2.toVector3f(), vec3.toVector3f(), vec4.toVector3f(), color, n, n2, n3);
   }

   public static void method2241(
      @NotNull PoseStack.Pose pose,
      @NotNull VertexConsumer consumer,
      Vector3f vector3f,
      Vector3f position,
      Vector3f vec,
      Vector3f vector3f2,
      int color,
      int n,
      int color2,
      int color3
   ) {
      consumer.addVertex(pose, vector3f).setColor(color);
      consumer.addVertex(pose, position).setColor(n);
      consumer.addVertex(pose, vec).setColor(color2);
      consumer.addVertex(pose, vector3f2).setColor(color3);
   }

   @NotNull
   public static Vector3f method2242(float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = f4 - f;
      float f8 = f5 - f2;
      float f9 = f6 - f3;
      float f10 = Mth.sqrt(f7 * f7 + f8 * f8 + f9 * f9);
      return new Vector3f(f7 / f10, f8 / f10, f9 / f10);
   }

   public static void method2243(double x, double y, double z, double d, double d2, double d3, int color, float width, boolean flag) {
      method2244(x, y, z, d, d2, d3, color, color, width, flag);
   }

   public static void method2244(
      double x, double y, double z, double d, double y2, double z2, int color, int n, float width, boolean flag
   ) {
      Vec3 position = mc.getEntityRenderDispatcher().camera.position();
      (flag ? entry2 : entry22)
         .method2254(
            (float)(x - position.x),
            (float)(y - position.y),
            (float)(z - position.z),
            (float)(d - position.x),
            (float)(y2 - position.y),
            (float)(z2 - position.z),
            color,
            n,
            width
         );
   }

   public static void method2245(Vec3 vec, Vec3 vec2, int color, float width, boolean flag) {
      method2244(vec.x, vec.y, vec.z, vec2.x, vec2.y, vec2.z, color, color, width, flag);
   }

   public static void method2246(Vec3 vec, Vec3 vec2, int color, int n, float width, boolean flag) {
      method2244(vec.x, vec.y, vec.z, vec2.x, vec2.y, vec2.z, color, n, width, flag);
   }

   public static void method2247(Vec3 vec, Vec3 vec2, Vec3 vec3, Vec3 vec4, int n, boolean flag) {
      method2248(vec, vec2, vec3, vec4, n, n, n, n, flag);
   }

   public static void method2248(Vec3 vec, Vec3 vec2, Vec3 vec3, Vec3 vec4, int n, int n2, int n3, int n4, boolean flag) {
      Vec3 position = mc.getEntityRenderDispatcher().camera.position();
      double x = position.x;
      double y = position.y;
      double z = position.z;
      (flag ? entry : entry3)
         .method2253(
            (float)(vec.x - x),
            (float)(vec.y - y),
            (float)(vec.z - z),
            (float)(vec2.x - x),
            (float)(vec2.y - y),
            (float)(vec2.z - z),
            (float)(vec3.x - x),
            (float)(vec3.y - y),
            (float)(vec3.z - z),
            (float)(vec4.x - x),
            (float)(vec4.y - y),
            (float)(vec4.z - z),
            n,
            n2,
            n3,
            n4
         );
   }

   public static void method2249(float f, float f2, float f3, float f4, Runnable runnable) {
      RenderState.method1883(
         Mth.clamp(f, 0.0F, 1.0F), Mth.clamp(f2, 0.0F, 1.0F), Mth.clamp(f3, 0.0F, 1.0F), Mth.clamp(f4, 0.0F, 1.0F)
      );
      runnable.run();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float getTickDelta() {
      return mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
   }

   @Generated
   private GlowUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static void method2250(Matrix4f matrix) {
      matrix4f = matrix;
   }

   @Generated
   public static void method2251(Matrix4f modelViewMatrixCopy) {
      matrix4f2 = modelViewMatrixCopy;
   }

   @Generated
   public static void method2252(Matrix4f matrix) {
      matrix4f3 = matrix;
   }

   @Generated
   public static Matrix4f getMatrix4f() {
      return matrix4f;
   }

   @Generated
   public static Matrix4f getMatrix4f2() {
      return matrix4f2;
   }

   @Generated
   public static Matrix4f getMatrix4f3() {
      return matrix4f3;
   }

   public static final class Data {
      private final Vec3 vec3d;
      private final Vec3 vec3d2;
      private final int intValue;
      private final int intValue2;
      private final float floatValue;

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public Vec3 vec3d2() {
         return this.vec3d2;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.vec3d2, other.vec3d2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, vec3d2, intValue, intValue2, floatValue);
      }

      @Override
      public String toString() {
         return "Data[" + "vec3d=" + this.vec3d + ", " + "vec3d2=" + this.vec3d2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + "]";
      }

      public Data(Vec3 vec3d, Vec3 vec3d2, int intValue, int n, float floatValue) {
         this.vec3d = vec3d;
         this.vec3d2 = vec3d2;
         this.intValue = intValue;
         this.intValue2 = n;
         this.floatValue = floatValue;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public Vec3 getVec3d2() {
         return this.vec3d2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float width() {
         return this.floatValue;
      }
   }

   public static final class Data2 {
      private final VoxelShape voxelShape;
      private final List<AABB> items;

      public VoxelShape voxelShape() {
         return this.voxelShape;
      }

      public List<AABB> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.voxelShape, other.voxelShape) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(voxelShape, items);
      }

      @Override
      public String toString() {
         return "Data2[" + "voxelShape=" + this.voxelShape + ", " + "items=" + this.items + "]";
      }

      public Data2(VoxelShape voxelShape, List<AABB> items) {
         this.voxelShape = voxelShape;
         this.items = items;
      }

      public VoxelShape getVoxelShape() {
         return this.voxelShape;
      }

      public List<AABB> getItems() {
         return this.items;
      }
   }

   public static final class Data3 {
      private final VoxelShape voxelShape;
      private final List<GlowUtil.Data> items;
      private final List<AABB> items2;

      public VoxelShape voxelShape() {
         return this.voxelShape;
      }

      public List<GlowUtil.Data> items() {
         return this.items;
      }

      public List<AABB> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.voxelShape, other.voxelShape) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(voxelShape, items, items2);
      }

      @Override
      public String toString() {
         return "Data3[" + "voxelShape=" + this.voxelShape + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      public Data3(VoxelShape voxelShape, List<GlowUtil.Data> items, List<AABB> aabbs) {
         this.voxelShape = voxelShape;
         this.items = items;
         this.items2 = aabbs;
      }

      public VoxelShape getVoxelShape() {
         return this.voxelShape;
      }

      public List<GlowUtil.Data> lines() {
         return this.items;
      }

      public List<AABB> getItems2() {
         return this.items2;
      }
   }

   private static final class Entry {
      private float[] floatValues = new float[3072];
      private int[] intValues = new int[1024];
      private int size;

      private void method2253(
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         float f6,
         float f7,
         float f8,
         float f9,
         float f10,
         float f11,
         float f12,
         int n,
         int n2,
         int n3,
         int n4
      ) {
         if (this.size * 4 == this.intValues.length) {
            int n5 = this.size * 2;
            this.floatValues = Arrays.copyOf(this.floatValues, n5 * 12);
            this.intValues = Arrays.copyOf(this.intValues, n5 * 4);
         }

         int n5 = this.size * 12;
         this.floatValues[n5] = f;
         this.floatValues[n5 + 1] = f2;
         this.floatValues[n5 + 2] = f3;
         this.floatValues[n5 + 3] = f4;
         this.floatValues[n5 + 4] = f5;
         this.floatValues[n5 + 5] = f6;
         this.floatValues[n5 + 6] = f7;
         this.floatValues[n5 + 7] = f8;
         this.floatValues[n5 + 8] = f9;
         this.floatValues[n5 + 9] = f10;
         this.floatValues[n5 + 10] = f11;
         this.floatValues[n5 + 11] = f12;
         int n6 = this.size * 4;
         this.intValues[n6] = n;
         this.intValues[n6 + 1] = n2;
         this.intValues[n6 + 2] = n3;
         this.intValues[n6 + 3] = n4;
         this.size++;
      }

      private boolean isEmpty() {
         return this.size == 0;
      }

      private void clear() {
         this.size = 0;
      }
   }

   private static final class Entry2 {
      private float[] floatValues = new float[1536];
      private int[] intValues = new int[512];
      private float[] floatValues2 = new float[256];
      private int size;

      private void method2254(float f, float f2, float f3, float f4, float f5, float f6, int color, int n, float width) {
         if (this.size == this.floatValues2.length) {
            int newLength = this.size * 2;
            this.floatValues = Arrays.copyOf(this.floatValues, newLength * 6);
            this.intValues = Arrays.copyOf(this.intValues, newLength * 2);
            this.floatValues2 = Arrays.copyOf(this.floatValues2, newLength);
         }

         int n2 = this.size * 6;
         this.floatValues[n2] = f;
         this.floatValues[n2 + 1] = f2;
         this.floatValues[n2 + 2] = f3;
         this.floatValues[n2 + 3] = f4;
         this.floatValues[n2 + 4] = f5;
         this.floatValues[n2 + 5] = f6;
         this.intValues[this.size * 2] = color;
         this.intValues[this.size * 2 + 1] = n;
         this.floatValues2[this.size] = width;
         this.size++;
      }

      private boolean isEmpty() {
         return this.size == 0;
      }

      private void clear() {
         this.size = 0;
      }
   }
}
