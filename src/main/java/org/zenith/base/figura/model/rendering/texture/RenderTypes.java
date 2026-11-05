package org.zenith.base.figura.model.rendering.texture;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public enum RenderTypes {
   NONE(null),
   CUTOUT(net.minecraft.client.renderer.rendertype.RenderTypes::entityCutout),
   CUTOUT_CULL(net.minecraft.client.renderer.rendertype.RenderTypes::entityCutoutCull),
   CUTOUT_EMISSIVE_SOLID(id -> RenderTypes.Layers.CUTOUT_EMISSIVE_SOLID.apply(id, true)),
   TRANSLUCENT(net.minecraft.client.renderer.rendertype.RenderTypes::entityTranslucent),
   TRANSLUCENT_CULL(net.minecraft.client.renderer.rendertype.RenderTypes::entityTranslucentCull),
   EMISSIVE(net.minecraft.client.renderer.rendertype.RenderTypes::eyes),
   EMISSIVE_SOLID(id -> net.minecraft.client.renderer.rendertype.RenderTypes.beaconBeam(id, false)),
   EYES(net.minecraft.client.renderer.rendertype.RenderTypes::eyes),
   END_PORTAL(t -> net.minecraft.client.renderer.rendertype.RenderTypes.endPortal(), false),
   END_GATEWAY(t -> net.minecraft.client.renderer.rendertype.RenderTypes.endGateway(), false),
   TEXTURED_PORTAL(RenderTypes.Layers.TEXTURED_PORTAL),
   GLINT(t -> net.minecraft.client.renderer.rendertype.RenderTypes.patternedShieldGlint(), false, false),
   GLINT2(t -> RenderTypes.Layers.GLINT, false, false),
   TEXTURED_GLINT(RenderTypes.Layers.TEXTURED_GLINT, true, false),
   LINES(t -> net.minecraft.client.renderer.rendertype.RenderTypes.lines(), false),
   LINES_STRIP(t -> net.minecraft.client.renderer.rendertype.RenderTypes.lines(), false),
   SOLID(t -> RenderTypes.Layers.SOLID, false),
   BLURRY(RenderTypes.Layers.BLURRY);

   private final Function<Identifier, RenderType> func;
   private final boolean texture;
   private final boolean offset;

   RenderTypes(Function<Identifier, RenderType> func) {
      this(func, true);
   }

   RenderTypes(Function<Identifier, RenderType> func, boolean texture) {
      this(func, texture, true);
   }

   RenderTypes(Function<Identifier, RenderType> func, boolean texture, boolean offset) {
      this.func = func;
      this.texture = texture;
      this.offset = offset;
   }

   public boolean isOffset() {
      return this.offset;
   }

   public RenderType get(Identifier id) {
      if (!this.texture) {
         return this.func.apply(id);
      } else {
         return id != null && this.func != null ? this.func.apply(id) : null;
      }
   }

   private static final class Layers {
      private static final RenderPipeline SOLID_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("zenith_figura", "pipeline/solid"))
         .withCull(false)
         .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
         .build();
      static final RenderType SOLID = RenderType.create(
         "figura_solid",
         RenderSetup.builder(SOLID_PIPELINE)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup()
      );
      // 1.21.11 RenderTypes.glint(): standalone item glint overlay (26.3 folds glint into item pipelines)
      static final RenderType GLINT = RenderType.create(
         "figura_glint",
         RenderSetup.builder(RenderPipelines.GLINT)
            .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
            .setTextureTransform(TextureTransform.GLINT_TEXTURING)
            .withForcedSolidModelPhase()
            .createRenderSetup()
      );
      static final BiFunction<Identifier, Boolean, RenderType> CUTOUT_EMISSIVE_SOLID = Util.memoize(
         (texture, affectsOutline) -> RenderType.create(
            "figura_cutout_emissive_solid",
            RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
               .withTexture("Sampler0", texture)
               .affectsCrumbling()
               .sortOnUpload()
               .useOverlay()
               .setOutline(affectsOutline ? OutlineProperty.AFFECTS_OUTLINE : OutlineProperty.NONE)
               .createRenderSetup()
         )
      );
      static final Function<Identifier, RenderType> TEXTURED_PORTAL = Util.memoize(
         texture -> RenderType.create(
            "figura_textured_portal",
            RenderSetup.builder(RenderPipelines.END_GATEWAY)
               .withTexture("Sampler0", texture)
               .withTexture("Sampler1", texture)
               .createRenderSetup()
         )
      );
      static final Function<Identifier, RenderType> BLURRY = Util.memoize(
         texture -> RenderType.create(
            "figura_blurry",
            RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
               .affectsCrumbling()
               .sortOnUpload()
               .useLightmap()
               .useOverlay()
               .withTexture("Sampler0", texture, () -> linearSampler(texture))
               .setOutline(OutlineProperty.AFFECTS_OUTLINE)
               .createRenderSetup()
         )
      );
      static final Function<Identifier, RenderType> TEXTURED_GLINT = Util.memoize(
         texture -> RenderType.create(
            "figura_textured_glint_direct",
            RenderSetup.builder(RenderPipelines.GLINT)
               .withTexture("Sampler0", texture)
               .setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING)
               .createRenderSetup()
         )
      );

      private static GpuSampler linearSampler(Identifier texture) {
         AbstractTexture source = Minecraft.getInstance().getTextureManager().getTexture(texture);
         GpuSampler sampler = source.getSampler();
         return RenderSystem.getSamplerCache().getSampler(sampler.getAddressModeU(), sampler.getAddressModeV(), FilterMode.LINEAR, FilterMode.LINEAR, false);
      }
   }
}
