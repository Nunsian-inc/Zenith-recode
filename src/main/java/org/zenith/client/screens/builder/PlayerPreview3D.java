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

   public PlayerPreview3D(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public void setMaxSize(float var1) {
      this.maxSize = Math.max(12.0F, var1);
   }

   public void setIdleSpin(float var1) {
      this.idleSpin = var1;
   }

   public void setHideHeldItems(boolean var1) {
      this.hideHeldItems = var1;
   }

   public void setForcedMainHand(ItemStack var1) {
      this.forcedMainHand = var1;
   }

   public void setFreePitch(boolean var1) {
      this.freePitch = var1;
   }

   public void resetRotation() {
      this.rotationX = 0.0F;
      this.rotationY = 0.0F;
      this.turnedByHand = false;
   }

   private void advanceIdleSpin() {
      long var1 = System.nanoTime();
      long var3 = this.lastFrameNanos;
      this.lastFrameNanos = var1;
      if (this.idleSpin != 0.0F && !this.isDragging && !this.turnedByHand && var3 != 0L) {
         float var5 = (float)((var1 - var3) / 1.0E9);
         if (!(var5 <= 0.0F) && !(var5 > 0.25F)) {
            this.rotationY = (this.rotationY + this.idleSpin * var5) % 360.0F;
         }
      }
   }

   public void setFocus(float var1, float var2, float var3) {
      if (!Float.isNaN(var3) && (Float.isNaN(this.focusYaw) || Math.abs(Mth.wrapDegrees(var3 - this.focusYaw)) > 1.0F)) {
         this.turnedByHand = false;
      }

      this.focusHeight = var1;
      this.focusZoom = var2;
      this.focusYaw = var3;
   }

   public void clearFocus() {
      this.setFocus(Float.NaN, 1.0F, Float.NaN);
   }

   private void advanceFocus(LivingEntity var1) {
      float var2 = Float.isNaN(this.focusHeight) ? var1.getBbHeight() / 2.0F : this.focusHeight;
      if (Float.isNaN(this.shownHeight)) {
         this.shownHeight = var2;
      }

      this.shownHeight = this.shownHeight + (var2 - this.shownHeight) * 0.12F;
      this.shownZoom = this.shownZoom + (this.focusZoom - this.shownZoom) * 0.12F;
      if (!Float.isNaN(this.focusYaw) && !this.isDragging && !this.turnedByHand) {
         float var3 = (float)Math.sin(System.nanoTime() / 1.0E9 * 0.6) * 18.0F / Math.max(1.0F, this.focusZoom * this.focusZoom);
         float var4 = this.focusYaw + var3;
         this.rotationY = this.rotationY + Mth.wrapDegrees(var4 - this.rotationY) * 0.1F;
      }
   }

   public void setBounds(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public void render(GuiGraphicsExtractor var1, LivingEntity var2, float var3, float var4) {
      this.render(var1, var2, var3, var4, null);
   }

   public void render(GuiGraphicsExtractor var1, LivingEntity var2, float var3, float var4, PlayerPreview3D.PreviewOverlay var5) {
      if (var2 != null) {
         float var6 = this.x + this.width / 2.0F;
         float var7 = this.y + this.height / 2.0F;
         this.advanceFocus(var2);
         int var8 = (int)Math.max(12.0F, Math.min(this.maxSize, 60.0F * this.zoom) * this.shownZoom);
         float var9 = var2.yBodyRot;
         float var10 = var2.yBodyRotO;
         float var11 = var2.getYRot();
         float var12 = var2.yRotO;
         float var13 = var2.getXRot();
         float var14 = var2.xRotO;
         float var15 = var2.yHeadRot;
         float var16 = var2.yHeadRotO;
         ItemStack var17 = var2.getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack var18 = var2.getItemInHand(InteractionHand.OFF_HAND);
         float var19 = var2.walkAnimation.speed();

         try {
            var2.yBodyRot = 0.0F;
            var2.yBodyRotO = 0.0F;
            var2.setYRot(0.0F);
            var2.yRotO = 0.0F;
            var2.setXRot(0.0F);
            var2.xRotO = 0.0F;
            var2.yHeadRot = 0.0F;
            var2.yHeadRotO = 0.0F;
            var2.walkAnimation.setSpeed(0.0F);
            if (this.forcedMainHand != null) {
               var2.setItemInHand(InteractionHand.MAIN_HAND, this.forcedMainHand);
               var2.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            } else if (this.hideHeldItems) {
               var2.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
               var2.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }

            if (Float.isNaN(this.focusYaw)) {
               this.advanceIdleSpin();
            }

            this.renderEntityWithRotation(var1, var2, var6, var7, var8, this.rotationX, this.rotationY, var5);
         } finally {
            if (this.forcedMainHand != null || this.hideHeldItems) {
               var2.setItemInHand(InteractionHand.MAIN_HAND, var17);
               var2.setItemInHand(InteractionHand.OFF_HAND, var18);
            }

            var2.yBodyRot = var9;
            var2.yBodyRotO = var10;
            var2.setYRot(var11);
            var2.yRotO = var12;
            var2.setXRot(var13);
            var2.xRotO = var14;
            var2.yHeadRot = var15;
            var2.yHeadRotO = var16;
            var2.walkAnimation.setSpeed(var19);
         }
      }
   }

   private void renderEntityWithRotation(
      GuiGraphicsExtractor var1, LivingEntity var2, float var3, float var4, int var5, float var6, float var7, PlayerPreview3D.PreviewOverlay var8
   ) {
      PoseStack var9 = MatrixUtils.method666(var1.pose());
      var9.pushPose();
      EntityRenderDispatcher var10 = mc.getEntityRenderDispatcher();

      try {
         float var11 = var2.getScale();
         float var12 = var5 / Math.max(var11, 1.0E-4F);
         var9.translate(var3, var4, 50.0F);
         var9.scale(var12, var12, -var12);
         var9.translate(0.0F, (Float.isNaN(this.shownHeight) ? var2.getBbHeight() / 2.0F : this.shownHeight) + 0.0625F * var11, 0.0F);
         Quaternionf var13 = new Quaternionf().rotateX((float)Math.toRadians(var6)).rotateY((float)Math.toRadians(180.0F + var7)).rotateZ((float) Math.PI);
         var9.rotate(var13);
         ScissorFlusher.flush();
         float var14 = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
         EntityRenderState var16 = var10.extractEntity(var2, var14);
         var16.lightCoords = 15728880;
         var16.shadowPieces.clear();
         var16.outlineColor = 0;
         OutlineVertexProvider.method1540(var16);
         CameraRenderState var17 = OutlineVertexProvider.method1541(var2.position());
         var17.orientation = new Quaternionf().rotateX((float)Math.toRadians(var6)).conjugate().rotateY((float) Math.PI);
         FiguraGuiRenderContext.mark(var2, EntityRenderMode.FIGURA_GUI);
         OutlineVertexProvider.method1542(var9x -> {
            var10.submit(var16, var17, 0.0, 0.0, 0.0, var9, var9x);
            if (this.queueOverlay != null) {
               var9.pushPose();

               try {
                  this.queueOverlay.render(var9, var9x, 15728880, var2, var14);
               } finally {
                  var9.popPose();
               }
            }

            if (var8 != null) {
               var9.pushPose();

               try {
                  var8.render(var9, var9x, 15728880, var2, var14);
               } finally {
                  var9.popPose();
               }
            }
         });
         ScissorFlusher.flush();
      } finally {
         var9.popPose();
      }
   }

   public float getPlayerCenterX() {
      return this.x + this.width / 2.0F;
   }

   public float getPlayerCenterY() {
      return this.y + this.height / 2.0F + 40.0F;
   }

   public boolean onMouseClicked(double var1, double var3, int var5) {
      if (this.isInBounds(var1, var3) && var5 == 0) {
         this.isDragging = true;
         this.lastMouseX = var1;
         this.lastMouseY = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseReleased(double var1, double var3, int var5) {
      if (var5 == 0 && this.isDragging) {
         this.isDragging = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.isDragging && var5 == 0) {
         float var10 = (float)(var1 - this.lastMouseX) * 1.0F;
         float var11 = (float)(var3 - this.lastMouseY) * 1.0F;
         this.rotationY = (this.rotationY - var10) % 360.0F;
         this.rotationX = this.freePitch ? (this.rotationX - var11) % 360.0F : Mth.clamp(this.rotationX - var11, -45.0F, 45.0F);
         this.turnedByHand = true;
         this.lastMouseX = var1;
         this.lastMouseY = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseScrolled(double var1, double var3, double var5) {
      if (this.isInBounds(var1, var3)) {
         this.zoom += (float)var5 * 0.1F;
         this.zoom = Math.max(0.3F, Math.min(3.0F, this.zoom));
         return true;
      } else {
         return false;
      }
   }

   public boolean isInBounds(double var1, double var3) {
      return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + this.height;
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
   public void setQueueOverlay(PlayerPreview3D.QueueOverlay var1) {
      this.queueOverlay = var1;
   }

   @Generated
   public void setX(float var1) {
      this.x = var1;
   }

   @Generated
   public void setY(float var1) {
      this.y = var1;
   }

   @Generated
   public void setWidth(float var1) {
      this.width = var1;
   }

   @Generated
   public void setHeight(float var1) {
      this.height = var1;
   }

   @Generated
   public void setZoom(float var1) {
      this.zoom = var1;
   }

   @Generated
   public void setRotationY(float var1) {
      this.rotationY = var1;
   }

   @Generated
   public void setRotationX(float var1) {
      this.rotationX = var1;
   }

   @Generated
   public void setDragging(boolean var1) {
      this.isDragging = var1;
   }

   @Generated
   public void setLastFrameNanos(long var1) {
      this.lastFrameNanos = var1;
   }

   @Generated
   public void setLastMouseX(double var1) {
      this.lastMouseX = var1;
   }

   @Generated
   public void setLastMouseY(double var1) {
      this.lastMouseY = var1;
   }

   @Generated
   public void setTurnedByHand(boolean var1) {
      this.turnedByHand = var1;
   }

   @Generated
   public void setFocusHeight(float var1) {
      this.focusHeight = var1;
   }

   @Generated
   public void setFocusZoom(float var1) {
      this.focusZoom = var1;
   }

   @Generated
   public void setFocusYaw(float var1) {
      this.focusYaw = var1;
   }

   @Generated
   public void setShownHeight(float var1) {
      this.shownHeight = var1;
   }

   @Generated
   public void setShownZoom(float var1) {
      this.shownZoom = var1;
   }

   @FunctionalInterface
   public interface PreviewOverlay {
      void render(PoseStack var1, SubmitNodeCollector var2, int var3, LivingEntity var4, float var5);
   }

   @FunctionalInterface
   public interface QueueOverlay {
      void render(PoseStack var1, SubmitNodeCollector var2, int var3, LivingEntity var4, float var5);
   }
}
