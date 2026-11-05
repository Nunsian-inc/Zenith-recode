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
   private static final Comparator<BotWorldView.Section> BY_SORT_KEY = (section, section2) -> Double.compare(section.sortKey, section2.sortKey);
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

   public BotWorldView(BotClient client) {
      this.client = client;
   }

   public GpuTextureView getColorAttachment() {
      return BlitUtils.method6450(this.fbo);
   }

   public boolean renderToFbo(int width, int height) {
      BotWorld world = this.client.getWorld();
      BotPlayer player = this.client.getPlayer();
      if (world != null && player != null && this.client.isJoined() && width > 0 && height > 0) {
         this.bindWorld(world);
         this.drainUploads();
         this.refreshSections(world, player);
         this.scheduleBuilds(world, player);
         this.scheduleEntitySnapshot(world);
         this.ensureFbo(width, height);
         if (this.fbo == null) {
            return false;
         } else {
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            RenderSystem.backupProjectionMatrix();
            modelViewStack.pushMatrix();
            modelViewStack.identity();
            int n = ImmediateRenderer.method8007();
            int width2 = mc.getWindow().getWidth();
            int height2 = mc.getWindow().getHeight();
            RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
            synchronized (this.client.getRenderStateLock()) {
               float tickDelta = this.client.getTickDelta();
               this.updateCamera(world, player, tickDelta);
               this.lightmap.update(world);

               try {
                  mc.gameRenderer.mainRenderTarget = this.fbo;
                  float f = (float)width / height;
                  float zFar = 128.0F;
                  this.projection.setupPerspective(0.05F, zFar, 100.0F, width, height);
                  this.positionMatrix.rotation(this.camera.rotation().conjugate(this.rotationConjugate));
                  RenderSystem.setProjectionMatrix(this.zenithProjection.getBuffer(this.projection), ProjectionType.PERSPECTIVE);
                  float[] floats = skyColor(world, player);
                  BlitUtils.method6456(this.fbo, ARGB.colorFromFloat(1.0F, floats[0], floats[1], floats[2]));
                  Vec3 position = this.camera.position();
                  this.drawTerrainLayer(ChunkSectionLayer.SOLID, position, false);
                  this.drawTerrainLayer(ChunkSectionLayer.CUTOUT, position, false);
                  this.renderEntities(world, player, position, tickDelta);
                  this.renderBlockEntities(world, position, tickDelta);
                  this.drawTerrainLayer(ChunkSectionLayer.TRANSLUCENT, position, true);
                  this.renderHand(world, player, tickDelta, width, height, zFar);
               } catch (Throwable e) {
                  this.lastError = e.toString();
                  if (!this.loggedRenderError) {
                     this.loggedRenderError = true;
                     System.err.println("[BotWorldView] render failed:");
                     e.printStackTrace();
                  }
               } finally {
                  mc.gameRenderer.mainRenderTarget = mainRenderTarget;
                  RenderSystem.restoreProjectionMatrix();
                  modelViewStack.popMatrix();
                  this.restoreDispatchers();
                  ImmediateRenderer.method8008(n);
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
   private void updateCamera(BotWorld world, BotPlayer player, float tickDelta) {
      if (this.camera.entity() != player) {
         this.camera.setEntity(player);
         this.eyeHeight = this.eyeHeightOld = player.getEyeHeight();
         this.heldItem.snap(player);
      } else if (player.tickCount != this.lastEyeHeightAge) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight = this.eyeHeight + (player.getEyeHeight() - this.eyeHeight) * 0.5F;
         this.heldItem.tick(player);
      }

      this.lastEyeHeightAge = player.tickCount;
      this.camera.place(
         new Vec3(
            Mth.lerp((double)tickDelta, player.xo, player.getX()),
            Mth.lerp((double)tickDelta, player.yo, player.getY()) + Mth.lerp(tickDelta, this.eyeHeightOld, this.eyeHeight),
            Mth.lerp((double)tickDelta, player.zo, player.getZ())
         ),
         player.getViewYRot(tickDelta),
         player.getViewXRot(tickDelta)
      );
   }

   public String getDebugStatus() {
      int n = 0;

      for (BotWorldView.Section section : this.sections.values()) {
         if (!section.buffers.isEmpty()) {
            n++;
         }
      }

      StringBuilder builder = new StringBuilder("mesh ")
         .append(n)
         .append('/')
         .append(this.sections.size())
         .append(" | dirty ")
         .append(this.dirty.size())
         .append(" | pending ")
         .append(this.pendingBuilds.get());
      String lastError = this.lastError;
      if (lastError != null) {
         builder.append(" | err: ").append(lastError);
      }

      return builder.toString();
   }

   public void close() {
      this.closed = true;

      for (BotWorldView.Section section : this.sections.values()) {
         section.closeBuffers();
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
   public void onBlockChanged(BlockPos pos) {
      int blockToSectionCoord = SectionPos.blockToSectionCoord(pos.getX() - 1);
      int n = SectionPos.blockToSectionCoord(pos.getX() + 1);
      int blockToSectionCoord2 = SectionPos.blockToSectionCoord(pos.getY() - 1);
      int blockToSectionCoord3 = SectionPos.blockToSectionCoord(pos.getY() + 1);
      int blockToSectionCoord4 = SectionPos.blockToSectionCoord(pos.getZ() - 1);
      int blockToSectionCoord5 = SectionPos.blockToSectionCoord(pos.getZ() + 1);

      for (int i = blockToSectionCoord; i <= n; i++) {
         for (int j = blockToSectionCoord2; j <= blockToSectionCoord3; j++) {
            for (int k = blockToSectionCoord4; k <= blockToSectionCoord5; k++) {
               this.dirty.add(SectionPos.asLong(i, j, k));
            }
         }
      }
   }

   @Override
   public void onChunkChanged(int x, int z) {
      BotWorld world = this.client.getWorld();
      if (world != null) {
         int blockToSectionCoord = SectionPos.blockToSectionCoord(world.getMinY());
         int n = SectionPos.blockToSectionCoord(world.getMaxY());

         for (int i = blockToSectionCoord; i <= n; i++) {
            this.dirty.add(SectionPos.asLong(x, i, z));
            this.dirty.add(SectionPos.asLong(x - 1, i, z));
            this.dirty.add(SectionPos.asLong(x + 1, i, z));
            this.dirty.add(SectionPos.asLong(x, i, z - 1));
            this.dirty.add(SectionPos.asLong(x, i, z + 1));
         }
      }
   }

   @Override
   public void onSectionChanged(int x, int y, int z) {
      this.dirty.add(SectionPos.asLong(x, y, z));
      this.dirty.add(SectionPos.asLong(x - 1, y, z));
      this.dirty.add(SectionPos.asLong(x + 1, y, z));
      this.dirty.add(SectionPos.asLong(x, y - 1, z));
      this.dirty.add(SectionPos.asLong(x, y + 1, z));
      this.dirty.add(SectionPos.asLong(x, y, z - 1));
      this.dirty.add(SectionPos.asLong(x, y, z + 1));
   }

   private void bindWorld(BotWorld boundWorld) {
      if (this.boundWorld != boundWorld) {
         if (this.boundWorld != null) {
            this.boundWorld.removeRenderListener(this);
         }

         for (BotWorldView.Section section : this.sections.values()) {
            section.closeBuffers();
         }

         this.sections.clear();
         this.sectionsValid = false;
         this.dirty.clear();
         this.drainUploadsDiscarding();
         this.closed = false;
         this.boundWorld = boundWorld;
         boundWorld.addRenderListener(this);
      }
   }

   private void drainUploads() {
      BotSectionMesher.MeshResult upload;
      while ((upload = this.uploads.poll()) != null) {
         BotWorldView.Section section = this.sections.get(upload.sectionPos());
         if (section == null) {
            discardResult(upload);
         } else {
            section.building = false;
            section.closeBuffers();
            section.blockEntities = upload.blockEntities();

            for (BotSectionMesher.LayerMesh layer : upload.layers()) {
               MeshBuffer meshBuffer = new MeshBuffer("zenith bot section");
               meshBuffer.method6583(layer.buffer());
               layer.allocator().close();
               section.buffers.put(layer.layer(), meshBuffer);
            }
         }
      }
   }

   private void drainUploadsDiscarding() {
      BotSectionMesher.MeshResult upload;
      while ((upload = this.uploads.poll()) != null) {
         discardResult(upload);
      }
   }

   private static void discardResult(BotSectionMesher.MeshResult upload) {
      for (BotSectionMesher.LayerMesh layer : upload.layers()) {
         layer.buffer().close();
         layer.allocator().close();
      }
   }

   private void refreshSections(BotWorld world, BotPlayer player) {
      int blockToSectionCoord = SectionPos.blockToSectionCoord(Mth.floor(player.getX()));
      int lastCenterY = SectionPos.blockToSectionCoord(Mth.floor(player.getEyeY()));
      int lastCenterZ = SectionPos.blockToSectionCoord(Mth.floor(player.getZ()));
      int lastMinSection = SectionPos.blockToSectionCoord(world.getMinY());
      int lastMaxSection = SectionPos.blockToSectionCoord(world.getMaxY());
      if (!this.sectionsValid
         || blockToSectionCoord != this.lastCenterX
         || lastCenterY != this.lastCenterY
         || lastCenterZ != this.lastCenterZ
         || lastMinSection != this.lastMinSection
         || lastMaxSection != this.lastMaxSection) {
         this.sectionsValid = true;
         this.lastCenterX = blockToSectionCoord;
         this.lastCenterY = lastCenterY;
         this.lastCenterZ = lastCenterZ;
         this.lastMinSection = lastMinSection;
         this.lastMaxSection = lastMaxSection;
         this.sections
            .entrySet()
            .removeIf(
               var3x -> {
                  long var4x = var3x.getKey();
                  boolean var6x = Math.abs(SectionPos.x(var4x) - blockToSectionCoord) > 5
                     || Math.abs(SectionPos.z(var4x) - lastCenterZ) > 5
                     || Math.abs(SectionPos.y(var4x) - lastCenterY) > 5;
                  if (var6x) {
                     var3x.getValue().closeBuffers();
                  }

                  return var6x;
               }
            );

         for (int i = blockToSectionCoord - 5; i <= blockToSectionCoord + 5; i++) {
            for (int j = lastCenterZ - 5; j <= lastCenterZ + 5; j++) {
               int n = Math.max(lastCenterY - 5, lastMinSection);
               int n2 = Math.min(lastCenterY + 5, lastMaxSection);

               for (int k = n; k <= n2; k++) {
                  long pos = SectionPos.asLong(i, k, j);
                  if (!this.sections.containsKey(pos)) {
                     this.sections.put(pos, new BotWorldView.Section(pos));
                     this.dirty.add(pos);
                  }
               }
            }
         }
      }
   }

   private void scheduleBuilds(BotWorld world, BotPlayer player) {
      if (!this.dirty.isEmpty() && this.pendingBuilds.get() < 32) {
         ArrayList<BotWorldView.Section> sections = new ArrayList();

         for (Long l : this.dirty) {
            BotWorldView.Section section = this.sections.get(l);
            if (section == null) {
               this.dirty.remove(l);
            } else if (!section.building) {
               sections.add(section);
            }
         }

         if (!sections.isEmpty()) {
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            for (BotWorldView.Section section : sections) {
               section.sortKey = section.squaredDistanceTo(x, y, z);
            }

            sections.sort(BY_SORT_KEY);
            int n = 0;

            for (BotWorldView.Section section : sections) {
               if (n >= 8 || this.pendingBuilds.get() >= 32) {
                  break;
               }

               long pos = section.pos;
               if (this.dirty.remove(pos)) {
                  section.building = true;
                  this.pendingBuilds.incrementAndGet();
                  n++;
                  this.client.execute(() -> {
                     try {
                        if (this.closed) {
                           return;
                        }

                        this.uploads.add(BotSectionMesher.build(world, pos));
                     } catch (Throwable var8x) {
                        this.lastError = "mesh: " + var8x;
                        this.uploads.add(new BotSectionMesher.MeshResult(pos, List.of(), List.of(), true));
                        this.dirty.add(pos);
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

   private void scheduleEntitySnapshot(BotWorld world) {
      BotWorldView.EntitySnapshot entitySnapshot = this.entitySnapshot;
      if (entitySnapshot.world() != world || entitySnapshot.version() != world.getEntitySetVersion()) {
         if (this.entitySnapshotScheduled.compareAndSet(false, true)) {
            this.client.execute(() -> {
               try {
                  int var2x = world.getEntitySetVersion();
                  ArrayList entities = new ArrayList();

                  for (Entity entity : world.entitiesForRendering()) {
                     entities.add(entity);
                  }

                  this.entitySnapshot = new BotWorldView.EntitySnapshot(world, var2x, entities);
               } catch (Throwable e) {
               } finally {
                  this.entitySnapshotScheduled.set(false);
               }
            });
         }
      }
   }

   private void ensureFbo(int width, int height) {
      if (this.fbo == null || this.fbo.width != width || this.fbo.height != height) {
         if (this.fbo != null) {
            this.fbo.destroyBuffers();
         }

         this.fbo = BlitUtils.method6448("zenith bot view", width, height, true);
      }
   }

   private void drawTerrainLayer(ChunkSectionLayer chunkSectionLayer, Vec3 position, boolean flag) {
      double x = position.x;
      double y = position.y;
      double z = position.z;
      List<BotWorldView.Section> layerSections = this.layerSections;
      layerSections.clear();

      for (BotWorldView.Section section : this.sections.values()) {
         if (section.buffers.containsKey(chunkSectionLayer)) {
            section.sortKey = section.squaredDistanceTo(x, y, z);
            layerSections.add(section);
         }
      }

      if (!layerSections.isEmpty()) {
         layerSections.sort(BY_SORT_KEY);
         if (flag) {
            Collections.reverse(layerSections);
         }

         RenderState.method1887(2, this.lightmap.getGlTextureView());
         RenderPipeline pipeline = chunkSectionLayer.pipeline(false);
         var layerMeshes = this.layerMeshes;
         layerMeshes.clear();
         int n = 0;

         for (BotWorldView.Section layerSection : layerSections) {
            MeshBuffer buffer = layerSection.buffers.get(chunkSectionLayer);
            if (buffer != null) {
               if (n == this.layerMatrices.size()) {
                  this.layerMatrices.add(new Matrix4f());
               }

               this.layerMatrices
                  .get(n)
                  .set(this.positionMatrix)
                  .translate((float)(layerSection.originX() - x), (float)(layerSection.originY() - y), (float)(layerSection.originZ() - z));
               layerMeshes.add(buffer);
               n++;
            }
         }

         MeshBuffer.method6588(layerMeshes, this.layerMatrices.subList(0, n), pipeline, this.fbo);
         layerMeshes.clear();
         layerSections.clear();
         RenderState.method1893(2);
      }
   }

   private void renderEntities(BotWorld world, BotPlayer player, Vec3 position, float tickDelta) {
      EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
      entityRenderDispatcher.prepare(this.camera, null);
      BotPlayHandler playHandler = this.client.getPlayHandler();
      boolean flag = mc.player == null;
      CameraRenderState camera = OutlineVertexProvider.method1541(position);

      for (Entity entity : this.entitySnapshot.entities()) {
         if (entity != player && !entity.isRemoved() && entity.level() == world && !(entity.distanceToSqr(position) > 9216.0)) {
            try {
               PoseStack poseStack = new PoseStack();
               poseStack.mulPose(this.positionMatrix);
               if (entity instanceof Player player2) {
                  int lightCoords = LightCoordsUtil.getLightCoords(world, player2.blockPosition());
                  OutlineVertexProvider.method1542(this.fbo, submitNodeCollector -> this.playerRenderer.render(player2, playHandler, position.x, position.y, position.z, tickDelta, poseStack, submitNodeCollector, lightCoords, true));
               } else if (!flag || !(entity instanceof LivingEntity)) {
                  double d = Mth.lerp((double)tickDelta, entity.xOld, entity.getX());
                  double d2 = Mth.lerp((double)tickDelta, entity.yOld, entity.getY());
                  double d3 = Mth.lerp((double)tickDelta, entity.zOld, entity.getZ());
                  EntityRenderState extractEntity = entityRenderDispatcher.extractEntity(entity, tickDelta);
                  extractEntity.outlineColor = 0;
                  // ponytail: one feature pass per entity keeps a broken renderer from taking the rest down; batch if the bot view gets slow
                  OutlineVertexProvider.method1542(this.fbo, submitNodeCollector -> entityRenderDispatcher.submit(extractEntity, camera, d - position.x, d2 - position.y, d3 - position.z, poseStack, submitNodeCollector));
               }
            } catch (Throwable e) {
               this.lastError = "entity: " + e;
               if (!this.loggedRenderError) {
                  this.loggedRenderError = true;
                  System.err.println("[BotWorldView] entity render failed for " + entity.getType() + ":");
                  e.printStackTrace();
               }
            }
         }
      }

   }

   private void renderBlockEntities(BotWorld world, Vec3 position, float tickDelta) {
      BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
      blockEntityRenderDispatcher.prepare(this.camera.position());
      CameraRenderState camera = OutlineVertexProvider.method1541(position);

      for (BotWorldView.Section section : this.sections.values()) {
         for (BlockEntity blockEntity : section.blockEntities) {
            if (!blockEntity.isRemoved() && blockEntity.getLevel() == world) {
               BlockPos blockPos = blockEntity.getBlockPos();
               if (!(blockPos.distToCenterSqr(position) > 2304.0)) {
                  try {
                     PoseStack poseStack = new PoseStack();
                     poseStack.mulPose(this.positionMatrix);
                     poseStack.pushPose();
                     poseStack.translate(blockPos.getX() - position.x, blockPos.getY() - position.y, blockPos.getZ() - position.z);
                     BlockEntityRenderState tryExtractRenderState = blockEntityRenderDispatcher.tryExtractRenderState(blockEntity, tickDelta, null, false);
                     if (tryExtractRenderState != null) {
                        OutlineVertexProvider.method1542(this.fbo, submitNodeCollector -> blockEntityRenderDispatcher.submit(tryExtractRenderState, poseStack, submitNodeCollector, camera));
                     }

                     poseStack.popPose();
                  } catch (Throwable e) {
                  }
               }
            }
         }
      }

   }

   private void renderHand(BotWorld world, BotPlayer player, float tickDelta, int width, int height, float zFar) {
      if (this.fbo.getDepthTexture() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.fbo.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
      }

      this.handProjection.setupPerspective(0.05F, zFar, 70.0F, width, height);
      RenderSystem.setProjectionMatrix(this.zenithHandProjection.getBuffer(this.handProjection), ProjectionType.PERSPECTIVE);
      RenderState.method1887(2, this.lightmap.getGlTextureView());
      PoseStack poseStack = new PoseStack();
      int lightCoords = LightCoordsUtil.getLightCoords(world, BlockPos.containing(player.getEyePosition()));
      OutlineVertexProvider.method1542(this.fbo, submitNodeCollector -> this.heldItem.render(player, this.client.getPlayHandler(), tickDelta, poseStack, submitNodeCollector, lightCoords));
   }

   private void restoreDispatchers() {
      if (mc.level != null) {
         Camera mainCamera = mc.gameRenderer.mainCamera();
         mc.getEntityRenderDispatcher().prepare(mainCamera, mc.crosshairPickEntity);
         mc.getBlockEntityRenderDispatcher().prepare(mainCamera.position());
      }
   }

   private static float[] skyColor(BotWorld world, BotPlayer player) {
      float skyBrightness = BotLightmap.getSkyBrightness(world);

      org.joml.Vector3fc dimensionValue;
      try {
         dimensionValue = world.environmentAttributes().getDimensionValue(EnvironmentAttributes.SKY_COLOR);
      } catch (Exception e) {
         dimensionValue = new org.joml.Vector3f(0.47F, 0.65F, 1.0F);
      }

      return new float[]{dimensionValue.x() * skyBrightness, dimensionValue.y() * skyBrightness, dimensionValue.z() * skyBrightness};
   }

   private static final class BotCamera extends Camera {
      void place(Vec3 vec, float viewYRot, float viewXRot) {
         this.setPosition(vec);
         this.setRotation(viewYRot, viewXRot);
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

      private EntitySnapshot(BotWorld world, int version, List<Entity> entities) {
         this.world = world;
         this.version = version;
         this.entities = entities;
      }
   }

   private static final class Section {
      public final long pos;
      public final Map<ChunkSectionLayer, MeshBuffer> buffers = new HashMap<>();
      public List<BlockEntity> blockEntities = List.of();
      public boolean building;
      public double sortKey;

      public Section(long pos) {
         this.pos = pos;
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

      public double squaredDistanceTo(double x, double y, double z) {
         double d = this.originX() + 8.0 - x;
         double d2 = this.originY() + 8.0 - y;
         double d3 = this.originZ() + 8.0 - z;
         return d * d + d2 * d2 + d3 * d3;
      }

      public void closeBuffers() {
         for (MeshBuffer meshBuffer : this.buffers.values()) {
            meshBuffer.close();
         }

         this.buffers.clear();
         this.blockEntities = List.of();
      }
   }
}
