package org.zenith.utility.misc;

import java.util.Arrays;

public final class UniformByteVolume {
   private final int sideLength;
   private byte uniformValue;
   private byte[] voxels;

   public UniformByteVolume(int sideLength, byte uniformValue) {
      this.sideLength = sideLength;
      this.uniformValue = uniformValue;
   }

   public byte getVoxel(int n, int n2, int n3) {
      return this.voxels == null ? this.uniformValue : this.voxels[this.voxelIndex(n, n2, n3)];
   }

   public void setVoxel(int n, int n2, int n3, byte b) {
      if (this.voxels == null) {
         if (b == this.uniformValue) {
            return;
         }

         this.voxels = new byte[this.sideLength * this.sideLength * this.sideLength];
         Arrays.fill(this.voxels, this.uniformValue);
      }

      this.voxels[this.voxelIndex(n, n2, n3)] = b;
   }

   public boolean isUniform() {
      return this.voxels == null;
   }

   public byte getByteValue() {
      return this.uniformValue;
   }

   public boolean tryCompact() {
      if (this.voxels == null) {
         return true;
      } else {
         byte uniformValue = this.voxels[0];

         for (int n : this.voxels) {
            if (n != uniformValue) {
               return false;
            }
         }

         this.uniformValue = uniformValue;
         this.voxels = null;
         return true;
      }
   }

   public int countAbove(byte b) {
      if (this.voxels == null) {
         return this.uniformValue > b ? this.sideLength * this.sideLength * this.sideLength : 0;
      } else {
         int n = 0;

         for (int uniformValue : this.voxels) {
            if (uniformValue > b) {
               n++;
            }
         }

         return n;
      }
   }

   public long estimatedMemoryBytes() {
      return this.voxels == null ? 16L : 16L + this.voxels.length;
   }

   private int voxelIndex(int n, int n2, int n3) {
      return (n2 * this.sideLength + n3) * this.sideLength + n;
   }
}
