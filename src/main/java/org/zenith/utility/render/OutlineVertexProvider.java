package org.zenith.utility.render;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import com.mojang.renderpearl.api.commands.RenderPass;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.zenith.RenderState;

public final class OutlineVertexProvider {
   private static boolean flag;

   public static void method1538() {
      if (!flag && ImmediateRenderer.method8005()) {
         method1543(Minecraft.getInstance());
         flag = true;
      }
   }

   public static void method1539() {
      if (flag) {
         flag = false;
         method1544(Minecraft.getInstance());
      }
   }

   private OutlineVertexProvider() {
   }

   public static void method1540(EntityRenderState var0) {
      if (var0 instanceof HumanoidRenderState var1) {
         var1.headEquipment = ItemStack.EMPTY;
         var1.chestEquipment = ItemStack.EMPTY;
         var1.legsEquipment = ItemStack.EMPTY;
         var1.feetEquipment = ItemStack.EMPTY;
      }

      if (var0 instanceof LivingEntityRenderState var2) {
         var2.headItem.clear();
         var2.wornHeadType = null;
      }
   }

   public static CameraRenderState method1541(Vec3 var0) {
      CameraRenderState var1 = new CameraRenderState();
      var1.pos = var0;
      var1.blockPos = BlockPos.containing(var0);
      var1.initialized = true;
      return var1;
   }

   public static void method1542(Consumer<SubmitNodeCollector> var1) {
      method1542(Minecraft.getInstance().gameRenderer.mainRenderTarget(), var1);
   }

   // target replaces the removed RenderSystem.outputColor/DepthTextureOverride
   public static void method1542(RenderTarget var6, Consumer<SubmitNodeCollector> var1) {
      Minecraft var2 = Minecraft.getInstance();
      boolean var3 = ImmediateRenderer.method8005() && !flag;
      if (var3) {
         method1543(var2);
      }

      try {
         // 26.2+: submissions go through the game's FeatureRenderDispatcher into an explicit render pass
         SubmitNodeStorage var4 = new SubmitNodeStorage();
         var1.accept(var4);

         try (
            FeatureRenderDispatcher.PreparedFrame var5 = var2.gameRenderer.featureRenderDispatcher().prepareFrame(var4);
            RenderPass var7 = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith entity preview", var6.getColorTextureView(), Optional.empty(), var6.getDepthTextureView(), OptionalDouble.empty());
         ) {
            RenderSystem.bindDefaultUniforms(var7);
            FeatureRenderDispatcher.renderAllFeatures(var7, var5);
         }
      } finally {
         if (var3) {
            method1544(var2);
         }

         RenderState.method1881();
      }
   }

   private static void method1543(Minecraft var0) {
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(ImmediateRenderer.method8019(), ProjectionType.ORTHOGRAPHIC);
      RenderSystem.getModelViewStack().pushMatrix().set(ImmediateRenderer.matrix4f2);
      var0.gameRenderer.lighting().setupFor(Entry.ENTITY_IN_UI);
      RenderTarget var1 = var0.gameRenderer.mainRenderTarget();
      if (var1.getDepthTexture() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var1.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
      }
   }

   private static void method1544(Minecraft var0) {
      var0.gameRenderer.lighting().setupFor(Entry.ITEMS_3D);
      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.restoreProjectionMatrix();
   }

   public static <S extends EntityRenderState> void method1545(EntityRenderer<?, S> var0, S var1, PoseStack var2, CameraRenderState var4) {
      method1542(var4x -> var0.submit(var1, var2, var4x, var4));
   }
}
