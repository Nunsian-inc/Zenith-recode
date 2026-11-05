package org.zenith.utility.misc;

import org.zenith.utility.render.ShaderDefinition;

public final class ShaderUniform {
   private final ShaderDefinition shaderDefinition;
   private final String text;
   private float[] floatValues;

   public ShaderUniform(ShaderDefinition shaderDefinition, String text) {
      this.shaderDefinition = shaderDefinition;
      this.text = text;
   }

   public String name() {
      return this.text;
   }

   private float[] method7411(int length) {
      float[] floatValues = this.floatValues;
      if (floatValues == null || floatValues.length != length) {
         floatValues = new float[length];
         this.floatValues = floatValues;
         this.shaderDefinition.method5559(this.text, floatValues);
      }

      return floatValues;
   }

   public void method7412(float batchRange) {
      this.method7411(1)[0] = batchRange;
   }

   public void method7413(float width, float height) {
      float[] floats = this.method7411(2);
      floats[0] = width;
      floats[1] = height;
   }

   public void method7414(float floatValue, float f, float f2) {
      float[] floats = this.method7411(3);
      floats[0] = floatValue;
      floats[1] = f;
      floats[2] = f2;
   }

   public void method7415(float floatValue, float f, float f2, float f3) {
      float[] floats = this.method7411(4);
      floats[0] = floatValue;
      floats[1] = f;
      floats[2] = f2;
      floats[3] = f3;
   }

   public void set(int batchRange) {
      this.method7412(batchRange);
   }

   public void method7416(int width, int height) {
      this.method7413(width, height);
   }

   public void method7417(float[] floats) {
      System.arraycopy(floats, 0, this.method7411(floats.length), 0, floats.length);
   }
}
