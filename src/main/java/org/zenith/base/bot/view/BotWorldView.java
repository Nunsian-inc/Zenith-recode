package org.zenith.base.bot.view;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MeshBuffer;
import org.zenith.utility.render.OutlineVertexProvider;

public final class BotWorldView implements BotWorld.RenderListener, MinecraftAccess {
   // 26.3 uses reversed-Z; Projection builds the matching matrix
   private final ProjectionMatrixBuffer zenithProjection = new ProjectionMatrixBuffer("zenith bot view");
   private final ProjectionMatrixBuffer zenithHandProjection = new ProjectionMatrixBuffer("zenith bot hand");
   private final Projection projection = new Projection();
   private final Projection handProjection = new Projection();
   private static final int RENDER_DISTANCE_CHUNKS = 5;
   private static final int VERTICAL_SECTION_RADIUS = 5;
   private static final int MAX_BUILDS_PER_FRAME = 8;
   private static final int MAX_PENDING_BUILDS = 32;
   private static final double ENTITY_RENDER_DISTANCE_SQ = 9216.0;
   private static final double BLOCK_ENTITY_RENDER_DISTANCE_SQ = 2304.0;
   private static final float FOV_DEGREES = 100.0F;
   private static final float HAND_FOV_DEGREES = 70.0F;
   private static final Comparator<BotWorldView.Section> BY_SORT_KEY = (var0, var1) -> Double.compare(var0.sortKey, var1.sortKey);
   private final BotClient client;
   private final Map<Long, BotWorldView.Section> sections = new HashMap<>();
   private final Set<Long> dirty = ConcurrentHashMap.newKeySet();
   private final Queue<BotSectionMesher.MeshResult> uploads = new ConcurrentLinkedQueue<>();
   private final AtomicInteger pendingBuilds = new AtomicInteger();
   private final AtomicBoolean entitySnapshotScheduled = new AtomicBoolean();
   private volatile BotWorldView.EntitySnapshot entitySnapshot = BotWorldView.EntitySnapshot.EMPTY;
   private volatile boolean closed;
   private boolean sectionsValid;
   private int lastCenterX;
   private int lastCenterY;
   private int lastCenterZ;
   private int lastMinSection;
   private int lastMaxSection;
   private final List<BotWorldView.Section> layerSections = new ArrayList<>();
   private final List<MeshBuffer> layerMeshes = new ArrayList<>();
   private final List<Matrix4f> layerMatrices = new ArrayList<>();
   private final BotWorldView.BotCamera camera = new BotWorldView.BotCamera();
   private float eyeHeight;
   private float eyeHeightOld;
   private final BotLightmap lightmap = new BotLightmap();
   private final BotPlayerRenderer playerRenderer = new BotPlayerRenderer();
   private final BotHeldItemRenderer heldItem = new BotHeldItemRenderer();
   private final Matrix4f positionMatrix = new Matrix4f();
   private final Quaternionf rotationConjugate = new Quaternionf();
   private TextureTarget fbo;
   private BotWorld boundWorld;
   private boolean loggedRenderError;
   private volatile String lastError;
   private int lastEyeHeightAge = Integer.MIN_VALUE;

   public BotWorldView(BotClient var1) {
      this.client = var1;
   }

   public GpuTextureView getColorAttachment() {
      return BlitUtils.method6450(this.fbo);
   }

   public boolean renderToFbo(int var1, int var2) {
      BotWorld var3 = this.client.getWorld();
      BotPlayer var4 = this.client.getPlayer();
      if (var3 != null && var4 != null && this.client.isJoined() && var1 > 0 && var2 > 0) {
         this.bindWorld(var3);
         this.drainUploads();
         this.refreshSections(var3, var4);
         this.scheduleBuilds(var3, var4);
         this.scheduleEntitySnapshot(var3);
         this.ensureFbo(var1, var2);
         if (this.fbo == null) {
            return false;
         } else {
            Matrix4fStack var5 = RenderSystem.getModelViewStack();
            RenderSystem.backupProjectionMatrix();
            var5.pushMatrix();
            var5.identity();
            int var6 = ImmediateRenderer.method8007();
            int var7 = mc.getWindow().getWidth();
            int var8 = mc.getWindow().getHeight();
            RenderTarget var9 = mc.gameRenderer.mainRenderTarget();
            synchronized (this.client.getRenderStateLock()) {
               float var11 = this.client.getTickDelta();
               this.updateCamera(var3, var4, var11);
               this.lightmap.update(var3);

               try {
                  mc.gameRenderer.mainRenderTarget = this.fbo;
                  float var12 = (float)var1 / var2;
                  float var13 = 128.0F;
                  this.projection.setupPerspective(0.05F, var13, 100.0F, var1, var2);
                  this.positionMatrix.rotation(this.camera.rotation().conjugate(this.rotationConjugate));
                  RenderSystem.setProjectionMatrix(this.zenithProjection.getBuffer(this.projection), ProjectionType.PERSPECTIVE);
                  float[] var14 = skyColor(var3, var4);
                  BlitUtils.method6456(this.fbo, ARGB.colorFromFloat(1.0F, var14[0], var14[1], var14[2]));
                  Vec3 var15 = this.camera.position();
                  this.drawTerrainLayer(ChunkSectionLayer.SOLID, var15, false);
                  this.drawTerrainLayer(ChunkSectionLayer.CUTOUT, var15, false);
                  this.renderEntities(var3, var4, var15, var11);
                  this.renderBlockEntities(var3, var15, var11);
                  this.drawTerrainLayer(ChunkSectionLayer.TRANSLUCENT, var15, true);
                  this.renderHand(var3, var4, var11, var1, var2, var13);
               } catch (Throwable var21) {
                  this.lastError = var21.toString();
                  if (!this.loggedRenderError) {
                     this.loggedRenderError = true;
                     System.err.println("[BotWorldView] render failed:");
                     var21.printStackTrace();
                  }
               } finally {
                  mc.gameRenderer.mainRenderTarget = var9;
                  RenderSystem.restoreProjectionMatrix();
                  var5.popMatrix();
                  this.restoreDispatchers();
                  ImmediateRenderer.method8008(var6);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   // 26.3 Camera only follows the main client (ClientLevel + LocalPlayer), so the bot view places it by hand,
   // same as the old Camera.setup(level, entity, false, false, partialTicks) in first person
   private void updateCamera(BotWorld var1, BotPlayer var2, float var3) {
      if (this.camera.entity() != var2) {
         this.camera.setEntity(var2);
         this.eyeHeight = this.eyeHeightOld = var2.getEyeHeight();
         this.heldItem.snap(var2);
      } else if (var2.tickCount != this.lastEyeHeightAge) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight = this.eyeHeight + (var2.getEyeHeight() - this.eyeHeight) * 0.5F;
         this.heldItem.tick(var2);
      }

      this.lastEyeHeightAge = var2.tickCount;
      this.camera.place(
         new Vec3(
            Mth.lerp((double)var3, var2.xo, var2.getX()),
            Mth.lerp((double)var3, var2.yo, var2.getY()) + Mth.lerp(var3, this.eyeHeightOld, this.eyeHeight),
            Mth.lerp((double)var3, var2.zo, var2.getZ())
         ),
         var2.getViewYRot(var3),
         var2.getViewXRot(var3)
      );
   }

   public String getDebugStatus() {
      int var1 = 0;

      for (BotWorldView.Section var3 : this.sections.values()) {
         if (!var3.buffers.isEmpty()) {
            var1++;
         }
      }

      StringBuilder var4 = new StringBuilder("mesh ")
         .append(var1)
         .append('/')
         .append(this.sections.size())
         .append(" | dirty ")
         .append(this.dirty.size())
         .append(" | pending ")
         .append(this.pendingBuilds.get());
      String var5 = this.lastError;
      if (var5 != null) {
         var4.append(" | err: ").append(var5);
      }

      return var4.toString();
   }

   public void close() {
      this.closed = true;

      for (BotWorldView.Section var2 : this.sections.values()) {
         var2.closeBuffers();
      }

      this.sections.clear();
      this.sectionsValid = false;
      this.dirty.clear();
      this.drainUploadsDiscarding();
      if (this.fbo != null) {
         this.fbo.destroyBuffers();
         this.fbo = null;
      }

      this.lightmap.close();
      if (this.boundWorld != null) {
         this.boundWorld.removeRenderListener(this);
         this.boundWorld = null;
      }
   }

   @Override
   public void onBlockChanged(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX() - 1);
      int var3 = SectionPos.blockToSectionCoord(var1.getX() + 1);
      int var4 = SectionPos.blockToSectionCoord(var1.getY() - 1);
      int var5 = SectionPos.blockToSectionCoord(var1.getY() + 1);
      int var6 = SectionPos.blockToSectionCoord(var1.getZ() - 1);
      int var7 = SectionPos.blockToSectionCoord(var1.getZ() + 1);

      for (int var8 = var2; var8 <= var3; var8++) {
         for (int var9 = var4; var9 <= var5; var9++) {
            for (int var10 = var6; var10 <= var7; var10++) {
               this.dirty.add(SectionPos.asLong(var8, var9, var10));
            }
         }
      }
   }

   @Override
   public void onChunkChanged(int var1, int var2) {
      BotWorld var3 = this.client.getWorld();
      if (var3 != null) {
         int var4 = SectionPos.blockToSectionCoord(var3.getMinY());
         int var5 = SectionPos.blockToSectionCoord(var3.getMaxY());

         for (int var6 = var4; var6 <= var5; var6++) {
            this.dirty.add(SectionPos.asLong(var1, var6, var2));
            this.dirty.add(SectionPos.asLong(var1 - 1, var6, var2));
            this.dirty.add(SectionPos.asLong(var1 + 1, var6, var2));
            this.dirty.add(SectionPos.asLong(var1, var6, var2 - 1));
            this.dirty.add(SectionPos.asLong(var1, var6, var2 + 1));
         }
      }
   }

   @Override
   public void onSectionChanged(int var1, int var2, int var3) {
      this.dirty.add(SectionPos.asLong(var1, var2, var3));
      this.dirty.add(SectionPos.asLong(var1 - 1, var2, var3));
      this.dirty.add(SectionPos.asLong(var1 + 1, var2, var3));
      this.dirty.add(SectionPos.asLong(var1, var2 - 1, var3));
      this.dirty.add(SectionPos.asLong(var1, var2 + 1, var3));
      this.dirty.add(SectionPos.asLong(var1, var2, var3 - 1));
      this.dirty.add(SectionPos.asLong(var1, var2, var3 + 1));
   }

   private void bindWorld(BotWorld var1) {
      if (this.boundWorld != var1) {
         if (this.boundWorld != null) {
            this.boundWorld.removeRenderListener(this);
         }

         for (BotWorldView.Section var3 : this.sections.values()) {
            var3.closeBuffers();
         }

         this.sections.clear();
         this.sectionsValid = false;
         this.dirty.clear();
         this.drainUploadsDiscarding();
         this.closed = false;
         this.boundWorld = var1;
         var1.addRenderListener(this);
      }
   }

   private void drainUploads() {
      BotSectionMesher.MeshResult var1;
      while ((var1 = this.uploads.poll()) != null) {
         BotWorldView.Section var2 = this.sections.get(var1.sectionPos());
         if (var2 == null) {
            discardResult(var1);
         } else {
            var2.building = false;
            var2.closeBuffers();
            var2.blockEntities = var1.blockEntities();

            for (BotSectionMesher.LayerMesh var4 : var1.layers()) {
               MeshBuffer var5 = new MeshBuffer("zenith bot section");
               var5.method6583(var4.buffer());
               var4.allocator().close();
               var2.buffers.put(var4.layer(), var5);
            }
         }
      }
   }

   private void drainUploadsDiscarding() {
      BotSectionMesher.MeshResult var1;
      while ((var1 = this.uploads.poll()) != null) {
         discardResult(var1);
      }
   }

   private static void discardResult(BotSectionMesher.MeshResult var0) {
      for (BotSectionMesher.LayerMesh var2 : var0.layers()) {
         var2.buffer().close();
         var2.allocator().close();
      }
   }

   private void refreshSections(BotWorld var1, BotPlayer var2) {
      int var3 = SectionPos.blockToSectionCoord(Mth.floor(var2.getX()));
      int var4 = SectionPos.blockToSectionCoord(Mth.floor(var2.getEyeY()));
      int var5 = SectionPos.blockToSectionCoord(Mth.floor(var2.getZ()));
      int var6 = SectionPos.blockToSectionCoord(var1.getMinY());
      int var7 = SectionPos.blockToSectionCoord(var1.getMaxY());
      if (!this.sectionsValid
         || var3 != this.lastCenterX
         || var4 != this.lastCenterY
         || var5 != this.lastCenterZ
         || var6 != this.lastMinSection
         || var7 != this.lastMaxSection) {
         this.sectionsValid = true;
         this.lastCenterX = var3;
         this.lastCenterY = var4;
         this.lastCenterZ = var5;
         this.lastMinSection = var6;
         this.lastMaxSection = var7;
         this.sections
            .entrySet()
            .removeIf(
               var3x -> {
                  long var4x = var3x.getKey();
                  boolean var6x = Math.abs(SectionPos.x(var4x) - var3) > 5
                     || Math.abs(SectionPos.z(var4x) - var5) > 5
                     || Math.abs(SectionPos.y(var4x) - var4) > 5;
                  if (var6x) {
                     var3x.getValue().closeBuffers();
                  }

                  return var6x;
               }
            );

         for (int var8 = var3 - 5; var8 <= var3 + 5; var8++) {
            for (int var9 = var5 - 5; var9 <= var5 + 5; var9++) {
               int var10 = Math.max(var4 - 5, var6);
               int var11 = Math.min(var4 + 5, var7);

               for (int var12 = var10; var12 <= var11; var12++) {
                  long var13 = SectionPos.asLong(var8, var12, var9);
                  if (!this.sections.containsKey(var13)) {
                     this.sections.put(var13, new BotWorldView.Section(var13));
                     this.dirty.add(var13);
                  }
               }
            }
         }
      }
   }

   private void scheduleBuilds(BotWorld var1, BotPlayer var2) {
      if (!this.dirty.isEmpty() && this.pendingBuilds.get() < 32) {
         ArrayList<BotWorldView.Section> var3 = new ArrayList();

         for (Long var5 : this.dirty) {
            BotWorldView.Section var6 = this.sections.get(var5);
            if (var6 == null) {
               this.dirty.remove(var5);
            } else if (!var6.building) {
               var3.add(var6);
            }
         }

         if (!var3.isEmpty()) {
            double var15 = var2.getX();
            double var16 = var2.getY();
            double var8 = var2.getZ();

            for (Object var11_o : (Iterable<?>)(Object)(var3)) {
               BotWorldView.Section var11 = (BotWorldView.Section)var11_o;
               var11.sortKey = var11.squaredDistanceTo(var15, var16, var8);
            }

            var3.sort(BY_SORT_KEY);
            int var17 = 0;

            for (Object var12_o : (Iterable<?>)(Object)(var3)) {
               BotWorldView.Section var12 = (BotWorldView.Section)var12_o;
               if (var17 >= 8 || this.pendingBuilds.get() >= 32) {
                  break;
               }

               long var13 = var12.pos;
               if (this.dirty.remove(var13)) {
                  var12.building = true;
                  this.pendingBuilds.incrementAndGet();
                  var17++;
                  this.client.execute(() -> {
                     try {
                        if (this.closed) {
                           return;
                        }

                        this.uploads.add(BotSectionMesher.build(var1, var13));
                     } catch (Throwable var8x) {
                        this.lastError = "mesh: " + var8x;
                        this.uploads.add(new BotSectionMesher.MeshResult(var13, List.of(), List.of(), true));
                        this.dirty.add(var13);
                     } finally {
                        this.pendingBuilds.decrementAndGet();
                     }

                     if (this.closed) {
                        this.drainUploadsDiscarding();
                     }
                  });
               }
            }
         }
      }
   }

   private void scheduleEntitySnapshot(BotWorld var1) {
      BotWorldView.EntitySnapshot var2 = this.entitySnapshot;
      if (var2.world() != var1 || var2.version() != var1.getEntitySetVersion()) {
         if (this.entitySnapshotScheduled.compareAndSet(false, true)) {
            this.client.execute(() -> {
               try {
                  int var2x = var1.getEntitySetVersion();
                  ArrayList var3 = new ArrayList();

                  for (Entity var5 : var1.entitiesForRendering()) {
                     var3.add(var5);
                  }

                  this.entitySnapshot = new BotWorldView.EntitySnapshot(var1, var2x, var3);
               } catch (Throwable var9) {
               } finally {
                  this.entitySnapshotScheduled.set(false);
               }
            });
         }
      }
   }

   private void ensureFbo(int var1, int var2) {
      if (this.fbo == null || this.fbo.width != var1 || this.fbo.height != var2) {
         if (this.fbo != null) {
            this.fbo.destroyBuffers();
         }

         this.fbo = BlitUtils.method6448("zenith bot view", var1, var2, true);
      }
   }

   private void drawTerrainLayer(ChunkSectionLayer var1, Vec3 var2, boolean var3) {
      double var4 = var2.x;
      double var6 = var2.y;
      double var8 = var2.z;
      List<BotWorldView.Section> var10 = this.layerSections;
      var10.clear();

      for (BotWorldView.Section var12 : this.sections.values()) {
         if (var12.buffers.containsKey(var1)) {
            var12.sortKey = var12.squaredDistanceTo(var4, var6, var8);
            var10.add(var12);
         }
      }

      if (!var10.isEmpty()) {
         var10.sort(BY_SORT_KEY);
         if (var3) {
            Collections.reverse(var10);
         }

         RenderState.method1887(2, this.lightmap.getGlTextureView());
         RenderPipeline var17 = var1.pipeline(false);
         var var18 = this.layerMeshes;
         var18.clear();
         int var13 = 0;

         for (Object var15_o : (Iterable<?>)(Object)(var10)) {
            BotWorldView.Section var15 = (BotWorldView.Section)var15_o;
            MeshBuffer var16 = var15.buffers.get(var1);
            if (var16 != null) {
               if (var13 == this.layerMatrices.size()) {
                  this.layerMatrices.add(new Matrix4f());
               }

               this.layerMatrices
                  .get(var13)
                  .set(this.positionMatrix)
                  .translate((float)(var15.originX() - var4), (float)(var15.originY() - var6), (float)(var15.originZ() - var8));
               var18.add(var16);
               var13++;
            }
         }

         MeshBuffer.method6588(var18, this.layerMatrices.subList(0, var13), var17, this.fbo);
         var18.clear();
         var10.clear();
         RenderState.method1893(2);
      }
   }

   private void renderEntities(BotWorld var1, BotPlayer var2, Vec3 var3, float var4) {
      EntityRenderDispatcher var5 = mc.getEntityRenderDispatcher();
      var5.prepare(this.camera, null);
      BotPlayHandler var7 = this.client.getPlayHandler();
      boolean var8 = mc.player == null;
      CameraRenderState var10 = OutlineVertexProvider.method1541(var3);

      for (Entity var12 : this.entitySnapshot.entities()) {
         if (var12 != var2 && !var12.isRemoved() && var12.level() == var1 && !(var12.distanceToSqr(var3) > 9216.0)) {
            try {
               PoseStack var13 = new PoseStack();
               var13.mulPose(this.positionMatrix);
               if (var12 instanceof Player var14) {
                  int var23 = LightCoordsUtil.getLightCoords(var1, var14.blockPosition());
                  OutlineVertexProvider.method1542(this.fbo, var6 -> this.playerRenderer.render(var14, var7, var3.x, var3.y, var3.z, var4, var13, var6, var23, true));
               } else if (!var8 || !(var12 instanceof LivingEntity)) {
                  double var15 = Mth.lerp((double)var4, var12.xOld, var12.getX());
                  double var17 = Mth.lerp((double)var4, var12.yOld, var12.getY());
                  double var19 = Mth.lerp((double)var4, var12.zOld, var12.getZ());
                  EntityRenderState var21 = var5.extractEntity(var12, var4);
                  var21.outlineColor = 0;
                  // ponytail: one feature pass per entity keeps a broken renderer from taking the rest down; batch if the bot view gets slow
                  OutlineVertexProvider.method1542(this.fbo, var6 -> var5.submit(var21, var10, var15 - var3.x, var17 - var3.y, var19 - var3.z, var13, var6));
               }
            } catch (Throwable var22) {
               this.lastError = "entity: " + var22;
               if (!this.loggedRenderError) {
                  this.loggedRenderError = true;
                  System.err.println("[BotWorldView] entity render failed for " + var12.getType() + ":");
                  var22.printStackTrace();
               }
            }
         }
      }

   }

   private void renderBlockEntities(BotWorld var1, Vec3 var2, float var3) {
      BlockEntityRenderDispatcher var4 = mc.getBlockEntityRenderDispatcher();
      var4.prepare(this.camera.position());
      CameraRenderState var7 = OutlineVertexProvider.method1541(var2);

      for (BotWorldView.Section var9 : this.sections.values()) {
         for (BlockEntity var11 : var9.blockEntities) {
            if (!var11.isRemoved() && var11.getLevel() == var1) {
               BlockPos var12 = var11.getBlockPos();
               if (!(var12.distToCenterSqr(var2) > 2304.0)) {
                  try {
                     PoseStack var13 = new PoseStack();
                     var13.mulPose(this.positionMatrix);
                     var13.pushPose();
                     var13.translate(var12.getX() - var2.x, var12.getY() - var2.y, var12.getZ() - var2.z);
                     BlockEntityRenderState var14 = var4.tryExtractRenderState(var11, var3, null, false);
                     if (var14 != null) {
                        OutlineVertexProvider.method1542(this.fbo, var6 -> var4.submit(var14, var13, var6, var7));
                     }

                     var13.popPose();
                  } catch (Throwable var15) {
                  }
               }
            }
         }
      }

   }

   private void renderHand(BotWorld var1, BotPlayer var2, float var3, int var9, int var10, float var5) {
      if (this.fbo.getDepthTexture() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.fbo.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
      }

      this.handProjection.setupPerspective(0.05F, var5, 70.0F, var9, var10);
      RenderSystem.setProjectionMatrix(this.zenithHandProjection.getBuffer(this.handProjection), ProjectionType.PERSPECTIVE);
      RenderState.method1887(2, this.lightmap.getGlTextureView());
      PoseStack var7 = new PoseStack();
      int var8 = LightCoordsUtil.getLightCoords(var1, BlockPos.containing(var2.getEyePosition()));
      OutlineVertexProvider.method1542(this.fbo, var6 -> this.heldItem.render(var2, this.client.getPlayHandler(), var3, var7, var6, var8));
   }

   private void restoreDispatchers() {
      if (mc.level != null) {
         Camera var1 = mc.gameRenderer.mainCamera();
         mc.getEntityRenderDispatcher().prepare(var1, mc.crosshairPickEntity);
         mc.getBlockEntityRenderDispatcher().prepare(var1.position());
      }
   }

   private static float[] skyColor(BotWorld var0, BotPlayer var1) {
      float var2 = BotLightmap.getSkyBrightness(var0);

      org.joml.Vector3fc var3;
      try {
         var3 = var0.environmentAttributes().getDimensionValue(EnvironmentAttributes.SKY_COLOR);
      } catch (Exception var5) {
         var3 = new org.joml.Vector3f(0.47F, 0.65F, 1.0F);
      }

      return new float[]{var3.x() * var2, var3.y() * var2, var3.z() * var2};
   }

   private static final class BotCamera extends Camera {
      void place(Vec3 var1, float var2, float var3) {
         this.setPosition(var1);
         this.setRotation(var2, var3);
      }
   }

   private static final class EntitySnapshot {
      private final BotWorld world;
      private final int version;
      private final List<Entity> entities;

      public BotWorld world() {
         return this.world;
      }

      public int version() {
         return this.version;
      }

      public List<Entity> entities() {
         return this.entities;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof EntitySnapshot other)) return false;
         return java.util.Objects.equals(this.world, other.world) && java.util.Objects.equals(this.version, other.version) && java.util.Objects.equals(this.entities, other.entities);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(world, version, entities);
      }

      @Override
      public String toString() {
         return "EntitySnapshot[" + "world=" + this.world + ", " + "version=" + this.version + ", " + "entities=" + this.entities + "]";
      }
      public static final BotWorldView.EntitySnapshot EMPTY = new BotWorldView.EntitySnapshot(null, 0, List.of());

      private EntitySnapshot(BotWorld var1, int var2, List<Entity> var3) {
         this.world = var1;
         this.version = var2;
         this.entities = var3;
      }
   }

   private static final class Section {
      public final long pos;
      public final Map<ChunkSectionLayer, MeshBuffer> buffers = new HashMap<>();
      public List<BlockEntity> blockEntities = List.of();
      public boolean building;
      public double sortKey;

      public Section(long var1) {
         this.pos = var1;
      }

      public int originX() {
         return SectionPos.x(this.pos) << 4;
      }

      public int originY() {
         return SectionPos.y(this.pos) << 4;
      }

      public int originZ() {
         return SectionPos.z(this.pos) << 4;
      }

      public double squaredDistanceTo(double var1, double var3, double var5) {
         double var7 = this.originX() + 8.0 - var1;
         double var9 = this.originY() + 8.0 - var3;
         double var11 = this.originZ() + 8.0 - var5;
         return var7 * var7 + var9 * var9 + var11 * var11;
      }

      public void closeBuffers() {
         for (MeshBuffer var2 : this.buffers.values()) {
            var2.close();
         }

         this.buffers.clear();
         this.blockEntities = List.of();
      }
   }
}
