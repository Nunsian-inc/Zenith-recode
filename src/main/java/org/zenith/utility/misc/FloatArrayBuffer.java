package org.zenith.utility.misc;

public final class FloatArrayBuffer {
   private float[] floatValues = new float[3072];
   private float[] floatValues2 = new float[3072];
   private int intValue;

   public void clear() {
      this.intValue = 0;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isEmpty() {
      return this.intValue == 0;
   }

   public void method379(float[] floats, float[] floats2) {
      this.ensure(this.intValue + 1);
      System.arraycopy(floats, 0, this.floatValues, this.intValue * 12, 12);
      System.arraycopy(floats2, 0, this.floatValues2, this.intValue * 12, 12);
      this.intValue++;
   }

   public float method380(int n, int n2) {
      return this.floatValues[n * 12 + n2 * 3];
   }

   public float method381(int n, int n2) {
      return this.floatValues[n * 12 + n2 * 3 + 1];
   }

   public float method382(int n, int n2) {
      return this.floatValues[n * 12 + n2 * 3 + 2];
   }

   public float method383(int n, int n2) {
      return this.floatValues2[n * 12 + n2 * 3];
   }

   public float method384(int n, int n2) {
      return this.floatValues2[n * 12 + n2 * 3 + 1];
   }

   public float method385(int n, int n2) {
      return this.floatValues2[n * 12 + n2 * 3 + 2];
   }

   public long method386() {
      return this.floatValues.length * 4L + this.floatValues2.length * 4L;
   }

   private void ensure(int n) {
      if (n * 12 > this.floatValues.length) {
         int n2 = Math.max(this.floatValues.length * 2, n * 12);
         float[] floatValues = new float[n2];
         float[] floatValues2 = new float[n2];
         System.arraycopy(this.floatValues, 0, floatValues, 0, this.intValue * 12);
         System.arraycopy(this.floatValues2, 0, floatValues2, 0, this.intValue * 12);
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
      }
   }
}
