package org.zenith.utility.misc;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.Identifier;

public class BlurTintShaderKey extends KawaseShaderKey implements WindowAccess {
   private ShaderUniform shaderUniform;
   private ShaderUniform shaderUniform2;
   private ShaderUniform shaderUniform3;
   private ShaderUniform shaderUniform4;
   private ShaderUniform shaderUniform5;

   public BlurTintShaderKey(Identifier shaderIdentifier) {
      super(shaderIdentifier, DefaultVertexFormat.POSITION_TEX_COLOR);
   }

   public void method4658(float batchRange) {
      this.shaderUniform2.method7412(batchRange);
      this.shaderUniform.method7413(1.0F / window.getScreenWidth(), 1.0F / window.getScreenHeight());
      this.shaderUniform3.method7412(1.0F);
      this.shaderUniform4.method7412(0.0F);
      this.shaderUniform5.method7414(1.0F, 1.0F, 1.0F);
   }

   @Override
   public void method4659() {
      this.shaderUniform = this.method5166("Resolution");
      this.shaderUniform2 = this.method5166("Offset");
      this.shaderUniform3 = this.method5166("Saturation");
      this.shaderUniform4 = this.method5166("TintIntensity");
      this.shaderUniform5 = this.method5166("TintColor");
      super.method4659();
   }
}
