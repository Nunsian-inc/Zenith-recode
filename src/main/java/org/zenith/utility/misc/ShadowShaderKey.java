package org.zenith.utility.misc;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.Identifier;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ShadowShaderKey extends KawaseShaderKey implements Handler2 {
   private ShaderUniform shaderUniform;
   private ShaderUniform shaderUniform2;
   private ShaderUniform shaderUniform3;
   private ShaderUniform shaderUniform4;
   private ShaderUniform shaderUniform5;
   private ShaderUniform shaderUniform6;

   public ShadowShaderKey(Identifier shaderIdentifier) {
      super(shaderIdentifier, DefaultVertexFormat.POSITION_COLOR);
   }

   public void method5497(float width, float height, float f, float height2, float batchRange, float f2, float batchRange2, ProxyDotRadiusData panelRadius) {
      this.shaderUniform.method7413(width, height);
      this.shaderUniform2.method7413(f, height2);
      this.shaderUniform3.method7415(panelRadius.getFloatValue(), panelRadius.getFloatValue4(), panelRadius.getFloatValue2(), panelRadius.getFloatValue3());
      this.shaderUniform4.method7412(batchRange);
      this.shaderUniform5.method7412(f2 * 0.5F);
      this.shaderUniform6.method7412(batchRange2);
   }

   @Override
   public void method4659() {
      this.shaderUniform = this.method5166("Size");
      this.shaderUniform2 = this.method5166("ShapeSize");
      this.shaderUniform3 = this.method5166("Radius");
      this.shaderUniform4 = this.method5166("Padding");
      this.shaderUniform5 = this.method5166("Sigma");
      this.shaderUniform6 = this.method5166("Spread");
      super.method4659();
   }
}
