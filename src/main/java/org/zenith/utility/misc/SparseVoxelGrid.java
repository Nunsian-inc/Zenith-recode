package org.zenith.utility.misc;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public final class SparseVoxelGrid {
   public static final int CONST_intValue = 4;
   public static final int CONST_intValue2 = 16;
   public static final int CONST_intValue3 = 64;
   public static final byte CONST_byteValue = -127;
   public static final byte CONST_byteValue2 = 127;
   public static final byte CONST_byteValue3 = 0;
   private final Long2ObjectMap<UniformByteVolume> long2ObjectMap = new Long2ObjectOpenHashMap();

   public byte getVoxel(int x, int n, int x2) {
      UniformByteVolume class2 = (UniformByteVolume)this.long2ObjectMap.get(packChunkCoordinates(voxelToChunk(x), voxelToChunk(n), voxelToChunk(x2)));
      return class2 == null ? -127 : class2.getVoxel(localVoxelCoordinate(x), localVoxelCoordinate(n), localVoxelCoordinate(x2));
   }

   public float getDensity(int x, int n, int n2) {
      return this.getVoxel(x, n, n2) / 127.0F;
   }

   public void setVoxel(int x, int n, int x2, byte b) {
      long key = packChunkCoordinates(voxelToChunk(x), voxelToChunk(n), voxelToChunk(x2));
      UniformByteVolume class2 = (UniformByteVolume)this.long2ObjectMap.get(key);
      if (class2 == null) {
         if (b == -127) {
            return;
         }

         class2 = new UniformByteVolume(64, (byte)-127);
         this.long2ObjectMap.put(key, class2);
      }

      class2.setVoxel(localVoxelCoordinate(x), localVoxelCoordinate(n), localVoxelCoordinate(x2), b);
   }

   public boolean hasChunk(int n, int n2, int n3) {
      return this.long2ObjectMap.containsKey(packChunkCoordinates(n, n2, n3));
   }

   public int chunkCount() {
      return this.long2ObjectMap.size();
   }

   public int uniformChunkCount() {
      int n = 0;
      ObjectIterator objectIterator = this.long2ObjectMap.values().iterator();

      while (objectIterator.hasNext()) {
         UniformByteVolume class2 = (UniformByteVolume)objectIterator.next();
         if (class2.isUniform()) {
            n++;
         }
      }

      return n;
   }

   public long positiveVoxelCount() {
      long l = 0L;
      ObjectIterator objectIterator = this.long2ObjectMap.values().iterator();

      while (objectIterator.hasNext()) {
         UniformByteVolume class2 = (UniformByteVolume)objectIterator.next();
         l += class2.countAbove((byte)0);
      }

      return l;
   }

   public long estimatedMemoryBytes() {
      long l = 0L;
      ObjectIterator objectIterator = this.long2ObjectMap.values().iterator();

      while (objectIterator.hasNext()) {
         UniformByteVolume class2 = (UniformByteVolume)objectIterator.next();
         l += class2.estimatedMemoryBytes();
      }

      return l;
   }

   public int compact() {
      LongArrayList longArrayList = new LongArrayList();
      int n = 0;
      ObjectIterator objectIterator = this.long2ObjectMap.long2ObjectEntrySet().iterator();

      while (objectIterator.hasNext()) {
         Entry entry = (Entry)objectIterator.next();
         UniformByteVolume class2 = (UniformByteVolume)entry.getValue();
         boolean flag = class2.isUniform();
         if (class2.tryCompact() && !flag) {
            n++;
         }

         if (class2.isUniform() && class2.getByteValue() == -127) {
            longArrayList.add(entry.getLongKey());
         }
      }

      for (int i = 0; i < longArrayList.size(); i++) {
         this.long2ObjectMap.remove(longArrayList.getLong(i));
      }

      return n;
   }

   public void clear() {
      this.long2ObjectMap.clear();
   }

   public static int blockToVoxel(int x) {
      return x * 4;
   }

   public static int voxelToChunk(int x) {
      return Math.floorDiv(x, 64);
   }

   private static int localVoxelCoordinate(int x) {
      return Math.floorMod(x, 64);
   }

   private static long packChunkCoordinates(int n, int n2, int n3) {
      return (long)(n & 2097151) << 43 | (long)(n2 & 2097151) << 22 | n3 & 4194303;
   }
}
