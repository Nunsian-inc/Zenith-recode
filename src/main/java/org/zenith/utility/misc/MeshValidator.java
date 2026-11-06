package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class MeshValidator {
   private MeshValidator() {
   }

   public static List<String> method6095(SparseVoxelGrid class2) {
      ArrayList<String> strings = new ArrayList<>();
      method6097(strings);
      method6098(strings);
      method6099(strings);
      method6100(strings);
      method6101(strings);
      method6102(class2, strings);
      method6096(strings);
      return strings;
   }

   private static void method6096(List<String> strings) {
      SparseVoxelGrid class2 = new SparseVoxelGrid();
      byte b = 24;
      int n = b / 2;
      float f = 7.0F;

      for (int i = 0; i < b; i++) {
         for (int j = 0; j < b; j++) {
            for (int k = 0; k < b; k++) {
               float f2 = i - n;
               float f3 = j - n;
               float f4 = k - n;
               float f5 = (float)Math.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
               float f6 = (f - f5) / f * 127.0F;
               class2.setVoxel(i, j, k, (byte)Math.clamp((long)Math.round(f6), -127, 127));
            }
         }
      }

      FloatArrayBuffer floatArrayBuffer = new FloatArrayBuffer();
      MeshImporter.Entry entry = MeshImporter.method658(class2, floatArrayBuffer, 0, 0, 0, b, b, b);
      if (floatArrayBuffer.isEmpty()) {
         strings.add("сетка пустая на заведомо непустом поле");
      } else {
         if (entry.intValue == 0) {
            strings.add("вершин ноль при непустой сетке");
         }

         for (int i = 0; i < floatArrayBuffer.getIntValue(); i++) {
            int n2 = 0;

            while (n2 < 4) {
               float f2 = floatArrayBuffer.method380(i, n2);
               float f3 = floatArrayBuffer.method381(i, n2);
               float f4 = floatArrayBuffer.method382(i, n2);
               if (!Float.isNaN(f2) && !Float.isNaN(f3) && !Float.isNaN(f4)) {
                  if (!(f2 < -1.0F) && !(f3 < -1.0F) && !(f4 < -1.0F) && !(f2 > b + 1.0F) && !(f3 > b + 1.0F) && !(f4 > b + 1.0F)) {
                     float f5 = floatArrayBuffer.method383(i, n2);
                     float f6 = floatArrayBuffer.method384(i, n2);
                     float f7 = floatArrayBuffer.method385(i, n2);
                     float f8 = (float)Math.sqrt(f5 * f5 + f6 * f6 + f7 * f7);
                     if (Math.abs(f8 - 1.0F) > 0.01F) {
                        strings.add("нормаль не единичная: длина " + f8);
                        return;
                     }

                     n2++;
                     continue;
                  }

                  strings.add("вершина вне области: " + f2 + " " + f3 + " " + f4);
                  return;
               }

               strings.add("в сетке NaN на квадрате " + i);
               return;
            }
         }

         SparseVoxelGrid class3 = new SparseVoxelGrid();
         FloatArrayBuffer floatArrayBuffer2 = new FloatArrayBuffer();
         MeshImporter.method658(class3, floatArrayBuffer2, 0, 0, 0, b, b, b);
         if (!floatArrayBuffer2.isEmpty()) {
            strings.add("на пустом поле построилась сетка");
         }
      }
   }

   private static void method6097(List<String> strings) {
      SparseVoxelGrid class2 = new SparseVoxelGrid();
      Random random = new Random(20260911L);

      for (int i = 0; i < 4000; i++) {
         int x = random.nextInt(4096) - 2048;
         int n = random.nextInt(512) - 256;
         int n2 = random.nextInt(4096) - 2048;
         byte b = (byte)(random.nextInt(255) - 127);
         class2.setVoxel(x, n, n2, b);
         byte b2 = class2.getVoxel(x, n, n2);
         if (b2 != b) {
            strings.add("запись не читается: " + x + " " + n + " " + n2 + " записано " + b + ", прочитано " + b2);
            return;
         }
      }
   }

   private static void method6098(List<String> strings) {
      SparseVoxelGrid class2 = new SparseVoxelGrid();

      try {
         if (class2.getVoxel(1000000, 64536, 999999) != -127) {
            strings.add("пустое пространство отдаёт не воздух");
         }

         class2.setVoxel(5, 5, 5, (byte)-127);
         if (class2.chunkCount() != 0) {
            strings.add("запись воздуха создала чанк");
         }
      } catch (RuntimeException e) {
         strings.add("выход за границы бросает исключение: " + e);
      }
   }

   private static void method6099(List<String> strings) {
      SparseVoxelGrid class2 = new SparseVoxelGrid();
      byte x = 64;
      class2.setVoxel(x - 1, x - 1, x - 1, (byte)40);
      class2.setVoxel(x, x, x, (byte)-40);
      if (class2.getVoxel(x - 1, x - 1, x - 1) != 40 || class2.getVoxel(x, x, x) != -40) {
         strings.add("соседние чанки перетирают друг друга на границе");
      }

      if (class2.chunkCount() != 2) {
         strings.add("на границе чанков создано " + class2.chunkCount() + " вместо 2");
      }
   }

   private static void method6100(List<String> strings) {
      SparseVoxelGrid class2 = new SparseVoxelGrid();
      class2.setVoxel(-1, -1, -1, (byte)77);
      if (class2.getVoxel(-1, -1, -1) != 77) {
         strings.add("отрицательные координаты читаются неверно");
      }

      if (class2.getVoxel(-65, -1, -1) != -127) {
         strings.add("отрицательные координаты заворачиваются в чужой чанк");
      }
   }

   private static void method6101(List<String> strings) {
      SparseVoxelGrid class2 = new SparseVoxelGrid();
      class2.setVoxel(3, 3, 3, (byte)127);
      class2.setVoxel(3, 3, 3, (byte)-127);
      long l = class2.estimatedMemoryBytes();
      class2.compact();
      long l2 = class2.estimatedMemoryBytes();
      if (class2.chunkCount() != 0) {
         strings.add("пустой чанк не убран при сжатии");
      }

      if (l2 > l) {
         strings.add("сжатие увеличило память: " + l + " -> " + l2);
      }
   }

   private static void method6102(SparseVoxelGrid class2, List<String> strings) {
      if (class2 != null && class2.chunkCount() != 0) {
         long l = class2.positiveVoxelCount();
         if (l == 0L) {
            strings.add("чанки есть, а заполненных вокселей нет");
         }

         long l2 = class2.estimatedMemoryBytes();
         long l3 = class2.chunkCount() * 64L * 64L * 64L;
         if (l2 > l3 + class2.chunkCount() * 16L) {
            strings.add("память больше плотного хранения: " + l2 + " > " + l3);
         }
      }
   }
}
