package org.zenith.utility.mixin.optimization;

import net.minecraft.client.gui.font.FontTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.zenith.utility.misc.GlyphShaderNames;

@Mixin({FontTexture.class})
public abstract class MixinOptGlyphAtlasTexture {
   @Unique
   private int zenith$atlasSize;

   @ModifyConstant(
      method = {"<init>(Ljava/util/function/Supplier;Lnet/minecraft/client/gui/font/GlyphRenderTypes;Z)V"},
      constant = {@Constant(
         intValue = 256
      )}
   )
   private int zenith$atlasSize(int var1) {
      if (this.zenith$atlasSize == 0) {
         this.zenith$atlasSize = GlyphShaderNames.method1418();
      }

      return this.zenith$atlasSize;
   }

   @ModifyConstant(
      method = {"add(Lcom/mojang/blaze3d/font/GlyphInfo;Lcom/mojang/blaze3d/font/GlyphBitmap;)Lnet/minecraft/client/gui/font/glyphs/BakedSheetGlyph;"},
      constant = {@Constant(
         floatValue = 256.0F
      )}
   )
   private float zenith$atlasUvScale(float var1) {
      return this.zenith$atlasSize == 0 ? var1 : this.zenith$atlasSize;
   }
}
