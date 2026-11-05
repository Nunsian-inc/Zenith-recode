package org.zenith.client.screens.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.zenith.MatrixUtils;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.render.FiguraGuiRenderContext;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ScissorFlusher;

public class PlayerPreview3D {
   private static final Minecraft mc = Minecraft.getInstance();
   private PlayerPreview3D.QueueOverlay queueOverlay;
   private float x;
   private float y;
   private float width;
   private float height;
   private float zoom = 1.0F;
   private float rotationY = 0.0F;
   private float rotationX = 0.0F;
   private boolean isDragging = false;
   private float maxSize = 60.0F;
   private float idleSpin = 0.0F;
   private boolean hideHeldItems = false;
   private ItemStack forcedMainHand = null;
   private boolean freePitch = false;
   private long lastFrameNanos;
   private double lastMouseX;
   private double lastMouseY;
   private boolean turnedByHand;
   private static final float MAX_PITCH = 45.0F;
   private static final float DRAG_SENSITIVITY = 1.0F;
   private float focusHeight = Float.NaN;
   private float focusZoom = 1.0F;
   private float focusYaw = Float.NaN;
   private float shownHeight = Float.NaN;
   private float shownZoom = 1.0F;

   public PlayerPreview3D(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public void setMaxSize(float maxSize) {
      this.maxSize = Math.max(12.0F, maxSize);
   }

   public void setIdleSpin(float idleSpin) {
      this.idleSpin = idleSpin;
   }

   public void setHideHeldItems(boolean hideHeldItems) {
      this.hideHeldItems = hideHeldItems;
   }

   public void setForcedMainHand(ItemStack forcedMainHand) {
      this.forcedMainHand = forcedMainHand;
   }

   public void setFreePitch(boolean freePitch) {
      this.freePitch = freePitch;
   }

   public void resetRotation() {
      this.rotationX = 0.0F;
      this.rotationY = 0.0F;
      this.turnedByHand = false;
   }

   private void advanceIdleSpin() {
      long nanoTime = System.nanoTime();
      long lastFrameNanos = this.lastFrameNanos;
      this.lastFrameNanos = nanoTime;
      if (this.idleSpin != 0.0F && !this.isDragging && !this.turnedByHand && lastFrameNanos != 0L) {
         float f = (float)((nanoTime - lastFrameNanos) / 1.0E9);
         if (!(f <= 0.0F) && !(f > 0.25F)) {
            this.rotationY = (this.rotationY + this.idleSpin * f) % 360.0F;
         }
      }
   }

   public void setFocus(float focusHeight, float focusZoom, float focusYaw) {
      if (!Float.isNaN(focusYaw) && (Float.isNaN(this.focusYaw) || Math.abs(Mth.wrapDegrees(focusYaw - this.focusYaw)) > 1.0F)) {
         this.turnedByHand = false;
      }

      this.focusHeight = focusHeight;
      this.focusZoom = focusZoom;
      this.focusYaw = focusYaw;
   }

   public void clearFocus() {
      this.setFocus(Float.NaN, 1.0F, Float.NaN);
   }

   private void advanceFocus(LivingEntity player) {
      float focusHeight = Float.isNaN(this.focusHeight) ? player.getBbHeight() / 2.0F : this.focusHeight;
      if (Float.isNaN(this.shownHeight)) {
         this.shownHeight = focusHeight;
      }

      this.shownHeight = this.shownHeight + (focusHeight - this.shownHeight) * 0.12F;
      this.shownZoom = this.shownZoom + (this.focusZoom - this.shownZoom) * 0.12F;
      if (!Float.isNaN(this.focusYaw) && !this.isDragging && !this.turnedByHand) {
         float f = (float)Math.sin(System.nanoTime() / 1.0E9 * 0.6) * 18.0F / Math.max(1.0F, this.focusZoom * this.focusZoom);
         float f2 = this.focusYaw + f;
         this.rotationY = this.rotationY + Mth.wrapDegrees(f2 - this.rotationY) * 0.1F;
      }
   }

   public void setBounds(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public void render(GuiGraphicsExtractor context, LivingEntity player, float transformX, float transformY) {
      this.render(context, player, transformX, transformY, null);
   }

   public void render(GuiGraphicsExtractor context, LivingEntity player, float transformX, float transformY, PlayerPreview3D.PreviewOverlay previewOverlay) {
      if (player != null) {
         float xo = this.x + this.width / 2.0F;
         float yo = this.y + this.height / 2.0F;
         this.advanceFocus(player);
         int n = (int)Math.max(12.0F, Math.min(this.maxSize, 60.0F * this.zoom) * this.shownZoom);
         float yBodyRot = player.yBodyRot;
         float yBodyRotO = player.yBodyRotO;
         float yRot = player.getYRot();
         float yRotO = player.yRotO;
         float xRot = player.getXRot();
         float xRotO = player.xRotO;
         float yHeadRot = player.yHeadRot;
         float yHeadRotO = player.yHeadRotO;
         ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack itemStack = player.getItemInHand(InteractionHand.OFF_HAND);
         float speed = player.walkAnimation.speed();

         try {
            player.yBodyRot = 0.0F;
            player.yBodyRotO = 0.0F;
            player.setYRot(0.0F);
            player.yRotO = 0.0F;
            player.setXRot(0.0F);
            player.xRotO = 0.0F;
            player.yHeadRot = 0.0F;
            player.yHeadRotO = 0.0F;
            player.walkAnimation.setSpeed(0.0F);
            if (this.forcedMainHand != null) {
               player.setItemInHand(InteractionHand.MAIN_HAND, this.forcedMainHand);
               player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            } else if (this.hideHeldItems) {
               player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
               player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }

            if (Float.isNaN(this.focusYaw)) {
               this.advanceIdleSpin();
            }

            this.renderEntityWithRotation(context, player, xo, yo, n, this.rotationX, this.rotationY, previewOverlay);
         } finally {
            if (this.forcedMainHand != null || this.hideHeldItems) {
               player.setItemInHand(InteractionHand.MAIN_HAND, itemInHand);
               player.setItemInHand(InteractionHand.OFF_HAND, itemStack);
            }

            player.yBodyRot = yBodyRot;
            player.yBodyRotO = yBodyRotO;
            player.setYRot(yRot);
            player.yRotO = yRotO;
            player.setXRot(xRot);
            player.xRotO = xRotO;
            player.yHeadRot = yHeadRot;
            player.yHeadRotO = yHeadRotO;
            player.walkAnimation.setSpeed(speed);
         }
      }
   }

   private void renderEntityWithRotation(
      GuiGraphicsExtractor context, LivingEntity player, float xo, float yo, int n, float rotationX, float rotationY, PlayerPreview3D.PreviewOverlay previewOverlay
   ) {
      PoseStack poseStack = MatrixUtils.method666(context.pose());
      poseStack.pushPose();
      EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();

      try {
         float scale = player.getScale();
         float xScale = n / Math.max(scale, 1.0E-4F);
         poseStack.translate(xo, yo, 50.0F);
         poseStack.scale(xScale, xScale, -xScale);
         poseStack.translate(0.0F, (Float.isNaN(this.shownHeight) ? player.getBbHeight() / 2.0F : this.shownHeight) + 0.0625F * scale, 0.0F);
         Quaternionf rotateZ = new Quaternionf().rotateX((float)Math.toRadians(rotationX)).rotateY((float)Math.toRadians(180.0F + rotationY)).rotateZ((float) Math.PI);
         poseStack.rotate(rotateZ);
         ScissorFlusher.flush();
         float gameTimeDeltaPartialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
         EntityRenderState extractEntity = entityRenderDispatcher.extractEntity(player, gameTimeDeltaPartialTick);
         extractEntity.lightCoords = 15728880;
         extractEntity.shadowPieces.clear();
         extractEntity.outlineColor = 0;
         OutlineVertexProvider.method1540(extractEntity);
         CameraRenderState camera = OutlineVertexProvider.method1541(player.position());
         camera.orientation = new Quaternionf().rotateX((float)Math.toRadians(rotationX)).conjugate().rotateY((float) Math.PI);
         FiguraGuiRenderContext.mark(player, EntityRenderMode.FIGURA_GUI);
         OutlineVertexProvider.method1542(var9x -> {
            entityRenderDispatcher.submit(extractEntity, camera, 0.0, 0.0, 0.0, poseStack, var9x);
            if (this.queueOverlay != null) {
               poseStack.pushPose();

               try {
                  this.queueOverlay.render(poseStack, var9x, 15728880, player, gameTimeDeltaPartialTick);
               } finally {
                  poseStack.popPose();
               }
            }

            if (previewOverlay != null) {
               poseStack.pushPose();

               try {
                  previewOverlay.render(poseStack, var9x, 15728880, player, gameTimeDeltaPartialTick);
               } finally {
                  poseStack.popPose();
               }
            }
         });
         ScissorFlusher.flush();
      } finally {
         poseStack.popPose();
      }
   }

   public float getPlayerCenterX() {
      return this.x + this.width / 2.0F;
   }

   public float getPlayerCenterY() {
      return this.y + this.height / 2.0F + 40.0F;
   }

   public boolean onMouseClicked(double lastMouseX, double lastMouseY, int mouseToGlfw) {
      if (this.isInBounds(lastMouseX, lastMouseY) && mouseToGlfw == 0) {
         this.isDragging = true;
         this.lastMouseX = lastMouseX;
         this.lastMouseY = lastMouseY;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseReleased(double x, double y, int mouseToGlfw) {
      if (mouseToGlfw == 0 && this.isDragging) {
         this.isDragging = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseDragged(double lastMouseX, double lastMouseY, int mouseToGlfw, double offsetX, double offsetY) {
      if (this.isDragging && mouseToGlfw == 0) {
         float f = (float)(lastMouseX - this.lastMouseX) * 1.0F;
         float f2 = (float)(lastMouseY - this.lastMouseY) * 1.0F;
         this.rotationY = (this.rotationY - f) % 360.0F;
         this.rotationX = this.freePitch ? (this.rotationX - f2) % 360.0F : Mth.clamp(this.rotationX - f2, -45.0F, 45.0F);
         this.turnedByHand = true;
         this.lastMouseX = lastMouseX;
         this.lastMouseY = lastMouseY;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseScrolled(double mouseX, double mouseY, double verticalAmount) {
      if (this.isInBounds(mouseX, mouseY)) {
         this.zoom += (float)verticalAmount * 0.1F;
         this.zoom = Math.max(0.3F, Math.min(3.0F, this.zoom));
         return true;
      } else {
         return false;
      }
   }

   public boolean isInBounds(double lastMouseX, double lastMouseY) {
      return lastMouseX >= this.x && lastMouseX <= this.x + this.width && lastMouseY >= this.y && lastMouseY <= this.y + this.height;
   }

   @Generated
   public PlayerPreview3D.QueueOverlay getQueueOverlay() {
      return this.queueOverlay;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public float getZoom() {
      return this.zoom;
   }

   @Generated
   public float getRotationY() {
      return this.rotationY;
   }

   @Generated
   public float getRotationX() {
      return this.rotationX;
   }

   @Generated
   public boolean isDragging() {
      return this.isDragging;
   }

   @Generated
   public float getMaxSize() {
      return this.maxSize;
   }

   @Generated
   public float getIdleSpin() {
      return this.idleSpin;
   }

   @Generated
   public boolean isHideHeldItems() {
      return this.hideHeldItems;
   }

   @Generated
   public ItemStack getForcedMainHand() {
      return this.forcedMainHand;
   }

   @Generated
   public boolean isFreePitch() {
      return this.freePitch;
   }

   @Generated
   public long getLastFrameNanos() {
      return this.lastFrameNanos;
   }

   @Generated
   public double getLastMouseX() {
      return this.lastMouseX;
   }

   @Generated
   public double getLastMouseY() {
      return this.lastMouseY;
   }

   @Generated
   public boolean isTurnedByHand() {
      return this.turnedByHand;
   }

   @Generated
   public float getFocusHeight() {
      return this.focusHeight;
   }

   @Generated
   public float getFocusZoom() {
      return this.focusZoom;
   }

   @Generated
   public float getFocusYaw() {
      return this.focusYaw;
   }

   @Generated
   public float getShownHeight() {
      return this.shownHeight;
   }

   @Generated
   public float getShownZoom() {
      return this.shownZoom;
   }

   @Generated
   public void setQueueOverlay(PlayerPreview3D.QueueOverlay queueOverlay) {
      this.queueOverlay = queueOverlay;
   }

   @Generated
   public void setX(float x) {
      this.x = x;
   }

   @Generated
   public void setY(float y) {
      this.y = y;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   public void setZoom(float zoom) {
      this.zoom = zoom;
   }

   @Generated
   public void setRotationY(float rotationY) {
      this.rotationY = rotationY;
   }

   @Generated
   public void setRotationX(float rotationX) {
      this.rotationX = rotationX;
   }

   @Generated
   public void setDragging(boolean isDragging) {
      this.isDragging = isDragging;
   }

   @Generated
   public void setLastFrameNanos(long lastFrameNanos) {
      this.lastFrameNanos = lastFrameNanos;
   }

   @Generated
   public void setLastMouseX(double lastMouseX) {
      this.lastMouseX = lastMouseX;
   }

   @Generated
   public void setLastMouseY(double lastMouseY) {
      this.lastMouseY = lastMouseY;
   }

   @Generated
   public void setTurnedByHand(boolean turnedByHand) {
      this.turnedByHand = turnedByHand;
   }

   @Generated
   public void setFocusHeight(float focusHeight) {
      this.focusHeight = focusHeight;
   }

   @Generated
   public void setFocusZoom(float focusZoom) {
      this.focusZoom = focusZoom;
   }

   @Generated
   public void setFocusYaw(float focusYaw) {
      this.focusYaw = focusYaw;
   }

   @Generated
   public void setShownHeight(float shownHeight) {
      this.shownHeight = shownHeight;
   }

   @Generated
   public void setShownZoom(float shownZoom) {
      this.shownZoom = shownZoom;
   }

   @FunctionalInterface
   public interface PreviewOverlay {
      void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int n, LivingEntity player, float gameTimeDeltaPartialTick);
   }

   @FunctionalInterface
   public interface QueueOverlay {
      void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int n, LivingEntity player, float gameTimeDeltaPartialTick);
   }
}
