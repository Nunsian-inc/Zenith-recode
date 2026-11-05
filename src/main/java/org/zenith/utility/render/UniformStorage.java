package org.zenith.utility.render;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.DynamicGpuDataStorageMapped;
import net.minecraft.client.renderer.DynamicGpuDataStorage.DynamicGpuData;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public final class UniformStorage {
   public static final String text = "ZenithData";
   private static final int CONST_intValue = 16;
   private static final List<DynamicGpuDataStorageMapped<UniformStorage.Entry>> items = new ArrayList<>();
   private static final List<DynamicGpuDataStorageMapped<UniformStorage.Entry>> items2 = new ArrayList<>();

   private UniformStorage() {
   }

   public static int method1272(List<float[]> floatses) {
      int[] intValues = new int[floatses.size()];

      for (int i = 0; i < intValues.length; i++) {
         intValues[i] = ((float[])floatses.get(i)).length;
      }

      return method1273(intValues);
   }

   public static int method1273(int[] intValues) {
      Std140SizeCalculator std140SizeCalculator = new Std140SizeCalculator();

      for (int intValue : intValues) {
         switch (intValue) {
            case 1:
               std140SizeCalculator.putFloat();
               break;
            case 2:
               std140SizeCalculator.putVec2();
               break;
            case 3:
               std140SizeCalculator.putVec3();
               break;
            case 4:
               std140SizeCalculator.putVec4();
               break;
            case 16:
               std140SizeCalculator.putMat4f();
               break;
            default:
               throw new IllegalArgumentException("Неподдерживаемый размер uniform: " + intValue);
         }
      }

      return Mth.roundToward(std140SizeCalculator.get(), 16);
   }

   public static DynamicGpuDataStorageMapped<UniformStorage.Entry> method1274(String label, int intValue) {
      DynamicGpuDataStorageMapped gpuDataStorageMapped = new DynamicGpuDataStorageMapped(label, intValue, 128, 16);
      items.add(gpuDataStorageMapped);
      return gpuDataStorageMapped;
   }

   public static void method1275(DynamicGpuDataStorageMapped<UniformStorage.Entry> dynamicUniformStorage) {
      if (items.remove(dynamicUniformStorage)) {
         items2.add(dynamicUniformStorage);
      }
   }

   public static GpuBufferSlice method1276(DynamicGpuDataStorageMapped<UniformStorage.Entry> gpuDataStorageMapped, List<float[]> floatses) {
      int[] intValues = new int[floatses.size()];
      int n = 0;

      for (int i = 0; i < intValues.length; i++) {
         intValues[i] = ((float[])floatses.get(i)).length;
         n += intValues[i];
      }

      float[] floatValues = new float[n];
      int n2 = 0;

      for (float[] floats : floatses) {
         System.arraycopy(floats, 0, floatValues, n2, floats.length);
         n2 += floats.length;
      }

      return gpuDataStorageMapped.writeData(new UniformStorage.Entry(floatValues, intValues));
   }

   public static void endFrame() {
      for (DynamicGpuDataStorageMapped gpuDataStorageMapped : items) {
         gpuDataStorageMapped.endFrame();
      }

      for (DynamicGpuDataStorageMapped gpuDataStorageMapped : items2) {
         gpuDataStorageMapped.close();
      }

      items2.clear();
   }

   public static final class Entry implements DynamicGpuData {
      private static final Matrix4f matrix4f = new Matrix4f();
      private final float[] floatValues;
      private final int[] intValues;

      public Entry(float[] floatValues, int[] intValues) {
         this.floatValues = floatValues;
         this.intValues = intValues;
      }

      public boolean method1277(int[] intValues, float[] floatValues2) {
         return this.intValues == intValues && Arrays.equals(this.floatValues, floatValues2);
      }

      @Override
      public void write(ByteBuffer buffer) {
         Std140Builder intoBuffer = Std140Builder.intoBuffer(buffer);
         float[] floatValues = this.floatValues;
         int off = 0;

         for (int intValue : this.intValues) {
            switch (intValue) {
               case 1:
                  intoBuffer.putFloat(floatValues[off]);
                  break;
               case 2:
                  intoBuffer.putVec2(floatValues[off], floatValues[off + 1]);
                  break;
               case 3:
                  intoBuffer.putVec3(floatValues[off], floatValues[off + 1], floatValues[off + 2]);
                  break;
               case 4:
                  intoBuffer.putVec4(floatValues[off], floatValues[off + 1], floatValues[off + 2], floatValues[off + 3]);
                  break;
               case 16:
                  intoBuffer.putMat4f(matrix4f.set(floatValues, off));
                  break;
               default:
                  throw new IllegalStateException();
            }

            off += intValue;
         }
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof UniformStorage.Entry entry && Arrays.equals(this.intValues, entry.intValues) && Arrays.equals(this.floatValues, entry.floatValues);
      }

      @Override
      public int hashCode() {
         return 31 * Arrays.hashCode(this.intValues) + Arrays.hashCode(this.floatValues);
      }
   }
}
