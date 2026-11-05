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

   public static void method1540(EntityRenderState extractEntity) {
      if (extractEntity instanceof HumanoidRenderState humanoidRenderState) {
         humanoidRenderState.headEquipment = ItemStack.EMPTY;
         humanoidRenderState.chestEquipment = ItemStack.EMPTY;
         humanoidRenderState.legsEquipment = ItemStack.EMPTY;
         humanoidRenderState.feetEquipment = ItemStack.EMPTY;
      }

      if (extractEntity instanceof LivingEntityRenderState entityRenderState) {
         entityRenderState.headItem.clear();
         entityRenderState.wornHeadType = null;
      }
   }

   public static CameraRenderState method1541(Vec3 pos) {
      CameraRenderState cameraRenderState = new CameraRenderState();
      cameraRenderState.pos = pos;
      cameraRenderState.blockPos = BlockPos.containing(pos);
      cameraRenderState.initialized = true;
      return cameraRenderState;
   }

   public static void method1542(Consumer<SubmitNodeCollector> consumer) {
      method1542(Minecraft.getInstance().gameRenderer.mainRenderTarget(), consumer);
   }

   // target replaces the removed RenderSystem.outputColor/DepthTextureOverride
   public static void method1542(RenderTarget fbo, Consumer<SubmitNodeCollector> consumer) {
      Minecraft minecraft = Minecraft.getInstance();
      boolean flag2 = ImmediateRenderer.method8005() && !flag;
      if (flag2) {
         method1543(minecraft);
      }

      try {
         // 26.2+: submissions go through the game's FeatureRenderDispatcher into an explicit render pass
         SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
         consumer.accept(submitNodeStorage);

         try (
            FeatureRenderDispatcher.PreparedFrame prepareFrame = minecraft.gameRenderer.featureRenderDispatcher().prepareFrame(submitNodeStorage);
            RenderPass renderPass = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith entity preview", fbo.getColorTextureView(), Optional.empty(), fbo.getDepthTextureView(), OptionalDouble.empty());
         ) {
            RenderSystem.bindDefaultUniforms(renderPass);
            FeatureRenderDispatcher.renderAllFeatures(renderPass, prepareFrame);
         }
      } finally {
         if (flag2) {
            method1544(minecraft);
         }

         RenderState.method1881();
      }
   }

   private static void method1543(Minecraft minecraft) {
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(ImmediateRenderer.method8019(), ProjectionType.ORTHOGRAPHIC);
      RenderSystem.getModelViewStack().pushMatrix().set(ImmediateRenderer.matrix4f2);
      minecraft.gameRenderer.lighting().setupFor(Entry.ENTITY_IN_UI);
      RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
      if (mainRenderTarget.getDepthTexture() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainRenderTarget.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
      }
   }

   private static void method1544(Minecraft minecraft) {
      minecraft.gameRenderer.lighting().setupFor(Entry.ITEMS_3D);
      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.restoreProjectionMatrix();
   }

   public static <S extends EntityRenderState> void method1545(EntityRenderer<?, S> entityRenderer, S state, PoseStack poseStack, CameraRenderState camera) {
      method1542(var4x -> entityRenderer.submit(state, poseStack, var4x, camera));
   }
}
