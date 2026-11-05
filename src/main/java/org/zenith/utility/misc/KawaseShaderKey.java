package org.zenith.utility.misc;

import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.zenith.MinecraftAccess;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.LightUtil;
import org.zenith.utility.render.ShaderDefinition;

public class KawaseShaderKey extends ShaderDefinition implements MinecraftAccess {
   private static final List<KawaseShaderKey> items = new ArrayList<>();

   public KawaseShaderKey(Identifier shaderIdentifier, VertexFormat vertexFormat) {
      super(method5164(shaderIdentifier), vertexFormat, LightUtil.method6717(vertexFormat));
      items.add(this);
   }

   private static Identifier method5164(Identifier shaderIdentifier) {
      return shaderIdentifier.getPath().startsWith("core/") ? shaderIdentifier : shaderIdentifier.withPrefix("core/");
   }

   public KawaseShaderKey method5165() {
      ImmediateRenderer.method8012(this);
      return this;
   }

   public void method4659() {
   }

   @Override
   public ShaderUniform method5166(String key) {
      return super.method5166(key);
   }

   @Internal
   public static void method5167() {
      for (KawaseShaderKey kawaseShaderKey : items) {
         kawaseShaderKey.method5566();
         kawaseShaderKey.method4659();
      }
   }
}
