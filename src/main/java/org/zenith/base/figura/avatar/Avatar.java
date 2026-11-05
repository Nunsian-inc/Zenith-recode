package org.zenith.base.figura.avatar;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.animation.Animation;
import org.zenith.base.figura.animation.AnimationPlayer;
import org.zenith.base.figura.lua.FiguraLuaRuntime;
import org.zenith.base.figura.lua.api.data.FiguraBuffer;
import org.zenith.base.figura.lua.api.data.FiguraInputStream;
import org.zenith.base.figura.lua.api.data.FiguraOutputStream;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.api.world.BlockStateAPI;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.math.matrix.FiguraMat3;
import org.zenith.base.figura.math.matrix.FiguraMat4;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.model.FiguraModelPart;
import org.zenith.base.figura.model.ParentType;
import org.zenith.base.figura.model.PartCustomization;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.model.rendering.ImmediateAvatarRenderer;
import org.zenith.base.figura.model.rendering.PartFilterScheme;
import org.zenith.base.figura.model.rendering.texture.FiguraTexture;
import org.zenith.base.figura.runtime.RuntimeDefaults;
import org.zenith.base.figura.utils.ColorUtils;
import org.zenith.base.figura.utils.PathUtils;
import org.zenith.base.figura.utils.Version;

public final class Avatar {
   private static final int DEFAULT_COMPLEXITY = 1000000;
   private static final int DEFAULT_INSTRUCTIONS = 1000000;
   private static final PartCustomization PIVOT_RENDER_CUSTOMIZATION = new PartCustomization();
   public static boolean firstPerson;
   public final UUID owner;
   public final AvatarLayer layer;
   public CompoundTag nbt;
   public boolean loaded;
   public boolean isHost;
   public boolean hasTexture;
   public boolean scriptError;
   private boolean completedFirstTick;
   public String name = "";
   public String entityName = "";
   public String authors = "";
   public String color;
   public String id;
   public Version version = FiguraMod.VERSION;
   private final Queue<Runnable> events = new ConcurrentLinkedQueue<>();
   public org.zenith.base.figura.model.rendering.AvatarRenderer renderer;
   public FiguraLuaRuntime luaRuntime;
   public EntityRenderMode renderMode = EntityRenderMode.OTHER;
   public final Map<Integer, Animation> animations = new HashMap<>();
   public final Map<String, byte[]> resources = new HashMap<>();
   public final ArrayList<FiguraBuffer> openBuffers = new ArrayList<>();
   public final ArrayList<FiguraInputStream> openInputStreams = new ArrayList<>();
   public final ArrayList<FiguraOutputStream> openOutputStreams = new ArrayList<>();
   public int animationComplexity;
   public final Avatar.Instructions complexity = new Avatar.Instructions(1000000);
   public final Avatar.Instructions init = new Avatar.Instructions(1000000);
   public final Avatar.Instructions render = new Avatar.Instructions(1000000);
   public final Avatar.Instructions worldRender = new Avatar.Instructions(1000000);
   public final Avatar.Instructions tick = new Avatar.Instructions(1000000);
   public final Avatar.Instructions worldTick = new Avatar.Instructions(1000000);
   public final Avatar.Instructions animation = new Avatar.Instructions(1000000);

   public Avatar(UUID owner) {
      this(owner, AvatarLayer.BODY);
   }

   public Avatar(UUID owner, AvatarLayer layer) {
      this.owner = owner;
      this.layer = layer == null ? AvatarLayer.BODY : layer;
      this.isHost = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(owner);
      Entity entity = getEntityByUUID(owner);
      this.entityName = entity == null ? "" : entity.getName().getString();
   }

   private static Entity getEntityByUUID(UUID uuid) {
      if (Minecraft.getInstance().level == null) {
         return null;
      }

      for (Player player : Minecraft.getInstance().level.players()) {
         if (player.getUUID().equals(uuid)) {
            return player;
         }
      }

      return null;
   }

   public synchronized void load(CompoundTag avatarNbt) {
      this.clean();
      this.nbt = avatarNbt;
      this.loaded = false;
      this.scriptError = false;
      this.completedFirstTick = false;
      this.animations.clear();
      if (avatarNbt == null) {
         this.loaded = true;
      } else {
         try {
            this.readMetadata(avatarNbt.getCompoundOrEmpty("metadata"));
            if (avatarNbt.contains("resources")) {
               CompoundTag resourceNbt = avatarNbt.getCompoundOrEmpty("resources");

               for (String key : resourceNbt.keySet()) {
                  this.resources.put(key, resourceNbt.getByteArray(key).orElse(new byte[0]));
               }
            }

            this.loadAnimations();
            this.renderer = new ImmediateAvatarRenderer(this);
            this.createLuaRuntime();
            this.loaded = true;
         } catch (Exception var5) {
            FiguraMod.LOGGER.error("Failed to load local avatar {}", this.owner, var5);
            this.clean();
            this.nbt = null;
            this.loaded = true;
         }
      }
   }

   private void readMetadata(CompoundTag metadata) {
      this.name = metadata.getStringOr("name", "");
      this.authors = metadata.getStringOr("authors", "");
      String sourceVersion = metadata.getStringOr("ver", "");
      this.version = sourceVersion.isBlank() ? FiguraMod.VERSION : new Version(sourceVersion);
      this.id = metadata.contains("id") ? metadata.getStringOr("id", "") : null;
      this.color = metadata.contains("color") ? metadata.getStringOr("color", "") : null;
      if (this.entityName == null || this.entityName.isBlank()) {
         this.entityName = this.name;
      }
   }

   private void loadAnimations() {
      if (this.nbt.contains("animations")) {
         List<String> autoAnimations = new ArrayList<>();
         CompoundTag metadata = this.nbt.getCompoundOrEmpty("metadata");
         if (metadata.contains("autoAnims")) {
            for (Tag element : metadata.getListOrEmpty("autoAnims")) {
               autoAnimations.add(element.asString().orElse(""));
            }
         }

         ListTag root = this.nbt.getListOrEmpty("animations");

         for (int index = 0; index < root.size(); index++) {
            try {
               CompoundTag data = root.getCompoundOrEmpty(index);
               if (data.contains("mdl") && data.contains("name")) {
                  String model = data.getStringOr("mdl", "");
                  String animationName = data.getStringOr("name", "");
                  Animation.LoopMode loopMode = Animation.LoopMode.ONCE;
                  if (data.contains("loop")) {
                     try {
                        loopMode = Animation.LoopMode.valueOf(data.getStringOr("loop", "").toUpperCase(Locale.US));
                     } catch (IllegalArgumentException var13) {
                     }
                  }

                  Animation value = new Animation(
                     this,
                     model,
                     animationName,
                     loopMode,
                     data.contains("ovr") && data.getBooleanOr("ovr", false),
                     data.contains("len") ? data.getFloatOr("len", 0.0F) : 0.0F,
                     data.contains("off") ? data.getFloatOr("off", 0.0F) : 0.0F,
                     data.getFloatOr("bld", 1.0F),
                     data.getFloatOr("sdel", 0.0F),
                     data.getFloatOr("ldel", 0.0F)
                  );
                  if (data.contains("code")) {
                     for (Tag element : data.getListOrEmpty("code")) {
                        CompoundTag code = (CompoundTag)element;
                        value.newCode(code.getFloatOr("time", 0.0F), code.getStringOr("src", ""));
                     }
                  }

                  this.animations.put(index, value);
                  if (autoAnimations.contains(model + "." + animationName)) {
                     value.play();
                  }
               }
            } catch (RuntimeException var14) {
               FiguraMod.LOGGER.warn("Failed to load avatar animation at index {}", index, var14);
            }
         }
      }
   }

   private void createLuaRuntime() {
      if (this.nbt.contains("scripts")) {
         Map<String, String> scripts = new HashMap<>();
         CompoundTag scriptNbt = this.nbt.getCompoundOrEmpty("scripts");

         for (String key : scriptNbt.keySet()) {
            scripts.put(PathUtils.computeSafeString(key), new String(scriptNbt.getByteArray(key).orElse(new byte[0]), StandardCharsets.UTF_8));
         }

         ListTag autoScripts = this.nbt.getCompoundOrEmpty("metadata").contains("autoScripts")
            ? this.nbt.getCompoundOrEmpty("metadata").getListOrEmpty("autoScripts")
            : null;
         FiguraLuaRuntime runtime = new FiguraLuaRuntime(this, scripts);
         this.luaRuntime = runtime;
         if (this.renderer != null && this.renderer.root != null) {
            runtime.setGlobal("models", this.renderer.root);
         }

         this.init.reset(1000000);
         runtime.setInstructionLimit(this.init.remaining);
         this.events.offer(() -> {
            if (runtime.init(autoScripts)) {
               this.init.use(runtime.getInstructions());
            }
         });
      }
   }

   private void updateUser(Entity entity) {
      if (!this.scriptError && this.luaRuntime != null && this.loaded && this.luaRuntime.getUser() == null) {
         Entity user = entity != null ? entity : getEntityByUUID(this.owner);
         if (user != null) {
            this.completedFirstTick = false;
            this.entityName = user.getName().getString();
            this.luaRuntime.setUser(user);
            this.run("ENTITY_INIT", this.init.post());
         }
      }
   }

   public void prepareEntity(Entity entity) {
      this.updateUser(entity);
   }

   public void tick() {
      if (!this.scriptError && this.luaRuntime != null && this.loaded) {
         this.updateUser(null);
         this.worldTick.reset(1000000);
         this.run("WORLD_TICK", this.worldTick);
         this.tick.reset(1000000);
         this.tickEvent();
         this.completedFirstTick = !this.scriptError && this.luaRuntime != null && this.luaRuntime.getUser() != null;
      }
   }

   public void clearAnimations() {
      if (this.loaded && !this.scriptError) {
         for (Animation value : this.animations.values()) {
            AnimationPlayer.clear(value);
         }
      }
   }

   public void applyAnimations() {
      if (this.loaded && !this.scriptError) {
         this.animation.reset(1000000);
         int limit = 1000000;

         for (Animation value : this.animations.values()) {
            limit = AnimationPlayer.tick(value, limit);
         }

         this.animationComplexity = 1000000 - limit;
      }
   }

   public LuaValue loadScript(String chunkName, String source) {
      return !this.scriptError && this.luaRuntime != null && this.loaded ? this.luaRuntime.load(chunkName, source) : null;
   }

   private void flushQueuedEvents() {
      Runnable event;
      while ((event = this.events.poll()) != null) {
         try {
            event.run();
         } catch (StackOverflowError | Exception var3) {
            if (this.luaRuntime != null) {
               this.luaRuntime.error(var3);
            }
         }
      }
   }

   public Varargs run(Object target, Avatar.Instructions limit, Object... arguments) {
      this.flushQueuedEvents();
      if (!this.scriptError && this.luaRuntime != null && this.loaded) {
         Varargs result = this.luaRuntime.run(target, limit, arguments);
         this.flushQueuedEvents();
         return result;
      } else {
         return null;
      }
   }

   public void punish(int amount) {
      if (this.luaRuntime != null) {
         this.luaRuntime.takeInstructions(amount);
      }
   }

   public void tickEvent() {
      if (this.loaded && this.luaRuntime != null && this.luaRuntime.getUser() != null) {
         this.run("TICK", this.tick);
      }
   }

   private boolean isReadyForEvents() {
      return this.completedFirstTick && this.loaded && !this.scriptError && this.luaRuntime != null && this.luaRuntime.getUser() != null;
   }

   public void renderEvent(float delta, FiguraMat4 poseMatrix) {
      if (this.isReadyForEvents()) {
         this.run("RENDER", this.render, delta, this.renderMode.name(), poseMatrix);
      }
   }

   public void postRenderEvent(float delta, FiguraMat4 poseMatrix) {
      if (this.isReadyForEvents()) {
         this.run("POST_RENDER", this.render.post(), delta, this.renderMode.name(), poseMatrix);
      }

      this.renderMode = EntityRenderMode.OTHER;
   }

   public void worldRenderEvent(float delta) {
      this.complexity.reset(1000000);
      if (this.isReadyForEvents()) {
         this.render.reset(1000000);
         this.worldRender.reset(1000000);
         this.run("WORLD_RENDER", this.worldRender, delta);
      }
   }

   public void postWorldRenderEvent(float delta) {
      if (this.renderer != null) {
         this.renderer.allowMatrixUpdate = false;
      }

      if (this.isReadyForEvents()) {
         this.run("POST_WORLD_RENDER", this.worldRender.post(), delta);
      }
   }

   public void render(
      Entity entity,
      float yaw,
      float delta,
      float alpha,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      int overlay,
      LivingEntityRenderer<?, ?, ?> entityRenderer,
      PartFilterScheme filter,
      boolean translucent,
      boolean glowing
   ) {
      if (this.loaded && this.renderer != null) {
         this.complexity.reset(1000000);
         this.renderer.vanillaModelData.update(entityRenderer);
         this.renderer.yaw = yaw;
         this.renderer.entity = entity;
         this.renderer.setupRenderer(filter, queue, matrices, delta, light, alpha, overlay, translucent, glowing);
         this.complexity.use(this.renderer.render());
      }
   }

   public synchronized void worldRender(
      Entity entity,
      double cameraX,
      double cameraY,
      double cameraZ,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int fallbackLight,
      float tickDelta,
      EntityRenderMode mode
   ) {
      if (this.renderer != null && this.loaded) {
         EntityRenderMode previousMode = this.renderMode;
         this.renderMode = mode;
         boolean updateMatrices = previousMode != EntityRenderMode.OTHER || mode == EntityRenderMode.FIRST_PERSON_WORLD;
         this.renderer.pivotCustomizations.clear();
         this.renderer.allowMatrixUpdate = updateMatrices;
         this.renderer.updateLight = updateMatrices;
         this.renderer.entity = entity;
         this.renderer
            .setupRenderer(
               PartFilterScheme.WORLD, queue, matrices, tickDelta, fallbackLight, 1.0F, OverlayTexture.NO_OVERLAY, false, false, cameraX, cameraY, cameraZ
            );
         this.complexity.use(this.renderer.renderSpecialParts());
         this.renderMode = previousMode;
         this.renderer.updateLight = false;
      }
   }

   public void firstPersonRender(
      PoseStack matrices, SubmitNodeCollector queue, Player player, AvatarRenderer<?> playerRenderer, ModelPart arm, int light, float tickDelta
   ) {
      if (this.renderer != null && this.loaded) {
         boolean lefty = arm == ((PlayerModel)playerRenderer.getModel()).leftArm;
         PartFilterScheme filter = lefty ? PartFilterScheme.LEFT_ARM : PartFilterScheme.RIGHT_ARM;
         boolean config = RuntimeDefaults.ALLOW_FP_HANDS.value;
         this.renderer.allowHiddenTransforms = config;
         this.renderer.allowMatrixUpdate = false;
         this.renderer.ignoreVanillaVisibility = true;
         matrices.pushPose();
         if (!config) {
            matrices.rotate(Axis.ZP.rotation(arm.zRot));
            matrices.rotate(Axis.YP.rotation(arm.yRot));
            matrices.rotate(Axis.XP.rotation(arm.xRot));
         }

         this.render(player, 0.0F, tickDelta, 1.0F, matrices, queue, light, OverlayTexture.NO_OVERLAY, playerRenderer, filter, false, false);
         matrices.popPose();
         this.renderer.allowHiddenTransforms = true;
         this.renderer.ignoreVanillaVisibility = false;
      }
   }

   public void firstPersonWorldRender(Entity watcher, SubmitNodeCollector queue, PoseStack matrices, Camera camera, float tickDelta) {
      if (this.renderer != null && this.loaded) {
         int light = Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(watcher, tickDelta);
         Vec3 camPos = camera.position();
         this.worldRender(watcher, camPos.x, camPos.y, camPos.z, matrices, queue, light, tickDelta, EntityRenderMode.FIRST_PERSON_WORLD);
      }
   }

   public void updateMatrices(LivingEntityRenderer<?, ?, ?> entityRenderer, PoseStack matrices) {
      if (this.renderer != null && this.loaded) {
         this.renderer.vanillaModelData.update(entityRenderer);
         this.renderer.currentFilterScheme = PartFilterScheme.MODEL;
         this.renderer.setMatrices(matrices);
         this.renderer.updateMatrices();
      }
   }

   public void capeRender(Entity entity, SubmitNodeCollector queue, PoseStack matrices, int light, float tickDelta, ModelPart cape) {
      if (this.loaded && this.renderer != null && entity != null) {
         this.renderer.vanillaModelData.update(ParentType.Cape, cape);
         this.renderer.entity = entity;
         this.renderer
            .setupRenderer(
               PartFilterScheme.CAPE, queue, matrices, tickDelta, light, 1.0F, OverlayTexture.NO_OVERLAY, this.renderer.translucent, this.renderer.glowing
            );
         this.complexity.use(this.renderer.renderSpecialParts());
      }
   }

   public void elytraRender(Entity entity, SubmitNodeCollector queue, PoseStack matrices, int light, float tickDelta, EntityModel<?> model) {
      if (this.loaded && this.renderer != null && entity != null) {
         this.renderer.entity = entity;
         this.renderer
            .setupRenderer(
               PartFilterScheme.LEFT_ELYTRA,
               queue,
               matrices,
               tickDelta,
               light,
               1.0F,
               OverlayTexture.NO_OVERLAY,
               this.renderer.translucent,
               this.renderer.glowing
            );
         this.renderer.vanillaModelData.update(ParentType.LeftElytra, model);
         this.complexity.use(this.renderer.renderSpecialParts());
         this.renderer.vanillaModelData.update(ParentType.RightElytra, model);
         this.renderer.currentFilterScheme = PartFilterScheme.RIGHT_ELYTRA;
         this.complexity.use(this.renderer.renderSpecialParts());
      }
   }

   public synchronized boolean pivotPartRender(ParentType parent, Consumer<PoseStack> consumer) {
      if (this.renderer != null && this.loaded && parent != null && parent.isPivot && consumer != null) {
         Queue<Pair<FiguraMat4, FiguraMat3>> queue = this.renderer.pivotCustomizations.computeIfAbsent(parent, ignored -> new ConcurrentLinkedQueue<>());
         if (queue.isEmpty()) {
            return false;
         }

         int rendered = 0;

         while (!queue.isEmpty() && rendered++ < 1000) {
            Pair<FiguraMat4, FiguraMat3> matrices = queue.poll();
            if (matrices == null) {
               break;
            }

            PIVOT_RENDER_CUSTOMIZATION.setPositionMatrix((FiguraMat4)matrices.getFirst());
            PIVOT_RENDER_CUSTOMIZATION.setNormalMatrix((FiguraMat3)matrices.getSecond());
            PIVOT_RENDER_CUSTOMIZATION.needsMatrixRecalculation = false;
            consumer.accept(PIVOT_RENDER_CUSTOMIZATION.copyIntoGlobalPoseStack());
         }

         queue.clear();
         return rendered > 0;
      } else {
         return false;
      }
   }

   public boolean itemRenderEvent(
      ItemStackAPI item,
      String mode,
      FiguraVec3 pos,
      FiguraVec3 rot,
      FiguraVec3 scale,
      boolean leftHanded,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      int overlay
   ) {
      if (this.isReadyForEvents() && this.renderer != null && this.renderer.interceptRendersIntoFigura) {
         Varargs result = this.run("ITEM_RENDER", this.render, item, mode, pos, rot, scale, leftHanded);
         if (result == null) {
            return false;
         }

         boolean rendered = false;

         for (int index = 1; index <= result.narg(); index++) {
            LuaValue value = result.arg(index);
            if (value.isuserdata(FiguraModelPart.class)) {
               rendered |= this.renderItem(matrices, queue, (FiguraModelPart)value.checkuserdata(FiguraModelPart.class), light, overlay);
            }
         }

         return rendered;
      } else {
         return false;
      }
   }

   public boolean arrowRenderEvent(float delta, EntityAPI<?> arrow) {
      Varargs result = this.isReadyForEvents() ? this.run("ARROW_RENDER", this.render, delta, arrow) : null;
      return this.isCancelled(result);
   }

   public boolean tridentRenderEvent(float delta, EntityAPI<?> trident) {
      Varargs result = this.isReadyForEvents() ? this.run("TRIDENT_RENDER", this.render, delta, trident) : null;
      return this.isCancelled(result);
   }

   public boolean skullRenderEvent(float delta, BlockStateAPI block, ItemStackAPI item, EntityAPI<?> entity, String mode) {
      Varargs result = null;
      if (this.isReadyForEvents() && this.renderer != null && this.renderer.interceptRendersIntoFigura) {
         result = this.run("SKULL_RENDER", this.render, delta, block, item, entity, mode);
      }

      return this.isCancelled(result);
   }

   public boolean useItemEvent(ItemStackAPI stack, String type, int particleCount) {
      Varargs result = this.isReadyForEvents() ? this.run("USE_ITEM", this.tick, stack, type, particleCount) : null;
      return this.isCancelled(result);
   }

   public boolean playSoundEvent(String id, FiguraVec3 pos, float volume, float pitch, boolean loop, String category, String file) {
      Varargs result = this.isReadyForEvents() ? this.run("ON_PLAY_SOUND", this.tick, id, pos, volume, pitch, loop, category, file) : null;
      return this.isCancelled(result);
   }

   public void resourceReloadEvent() {
      if (this.isReadyForEvents()) {
         this.run("RESOURCE_RELOAD", this.tick);
      }
   }

   public void damageEvent(String sourceType, EntityAPI<?> sourceCause, EntityAPI<?> sourceDirect, FiguraVec3 sourcePosition) {
      if (this.isReadyForEvents()) {
         this.run("DAMAGE", this.tick, sourceType, sourceCause, sourceDirect, sourcePosition);
      }
   }

   public String chatSendMessageEvent(String message) {
      Varargs val = this.isReadyForEvents() ? this.run("CHAT_SEND_MESSAGE", this.tick, message) : null;
      return val != null && (val.isnil(1) || RuntimeDefaults.CHAT_MESSAGES.value) ? (val.isnil(1) ? "" : val.arg(1).tojstring()) : message;
   }

   public Pair<String, Integer> chatReceivedMessageEvent(String message, String json) {
      Varargs val = this.isReadyForEvents() ? this.run("CHAT_RECEIVE_MESSAGE", this.tick, message, json) : null;
      if (val == null) {
         return null;
      }

      if (val.arg(1).isboolean() && !val.arg(1).checkboolean()) {
         return Pair.of(null, null);
      }

      String msg = val.isnil(1) ? json : val.arg(1).tojstring();
      Integer color = null;
      if (val.arg(2).isuserdata(FiguraVec3.class)) {
         color = ColorUtils.rgbToInt((FiguraVec3)val.arg(2).checkuserdata(FiguraVec3.class));
      }

      return Pair.of(msg, color);
   }

   public boolean mouseScrollEvent(double delta) {
      Varargs result = this.isReadyForEvents() ? this.run("MOUSE_SCROLL", this.tick, delta) : null;
      return this.isCancelled(result);
   }

   public boolean mouseMoveEvent(double x, double y) {
      Varargs result = this.isReadyForEvents() ? this.run("MOUSE_MOVE", this.tick, x, y) : null;
      return this.isCancelled(result);
   }

   public boolean mousePressEvent(int button, int action, int modifiers) {
      Varargs result = this.isReadyForEvents() ? this.run("MOUSE_PRESS", this.tick, button, action, modifiers) : null;
      return this.isCancelled(result);
   }

   public boolean keyPressEvent(int key, int action, int modifiers) {
      Varargs result = this.isReadyForEvents() ? this.run("KEY_PRESS", this.tick, key, action, modifiers) : null;
      return this.isCancelled(result);
   }

   public void charTypedEvent(String chars, int modifiers, int codePoint) {
      if (this.isReadyForEvents()) {
         this.run("CHAR_TYPED", this.tick, chars, modifiers, codePoint);
      }
   }

   public boolean totemEvent() {
      return this.isCancelled(this.isReadyForEvents() ? this.run("TOTEM", this.tick) : null);
   }

   private boolean isCancelled(Varargs result) {
      if (result == null) {
         return false;
      }

      for (int index = 1; index <= result.narg(); index++) {
         LuaValue value = result.arg(index);
         if (value.isboolean() && value.checkboolean()) {
            return true;
         }
      }

      return false;
   }

   public boolean renderArrow(PoseStack matrices, SubmitNodeCollector queue, float tickDelta, int light) {
      if (this.renderer != null && this.loaded) {
         matrices.pushPose();
         Quaternionf rotation = Axis.XP.rotationDegrees(135.0F);
         rotation.mul(Axis.YP.rotationDegrees(-90.0F));
         matrices.rotate(rotation);
         this.renderer.setupRenderer(PartFilterScheme.ARROW, queue, matrices, tickDelta, light, 1.0F, OverlayTexture.NO_OVERLAY, false, false);
         int used = this.renderer.renderSpecialParts();
         matrices.popPose();
         this.complexity.use(used);
         return used > 0;
      } else {
         return false;
      }
   }

   public boolean renderTrident(PoseStack matrices, SubmitNodeCollector queue, float tickDelta, int light) {
      if (this.renderer != null && this.loaded) {
         matrices.pushPose();
         Quaternionf rotation = Axis.ZP.rotationDegrees(90.0F);
         rotation.mul(Axis.YP.rotationDegrees(90.0F));
         matrices.rotate(rotation);
         this.renderer.setupRenderer(PartFilterScheme.TRIDENT, queue, matrices, tickDelta, light, 1.0F, OverlayTexture.NO_OVERLAY, false, false);
         int used = this.renderer.renderSpecialParts();
         matrices.popPose();
         this.complexity.use(used);
         return used > 0;
      } else {
         return false;
      }
   }

   public boolean skullRender(PoseStack matrices, SubmitNodeCollector queue, int light, Direction direction, float yaw) {
      if (this.renderer != null && this.loaded && this.renderer.interceptRendersIntoFigura) {
         matrices.pushPose();
         if (direction == null) {
            matrices.translate(0.5, 0.0, 0.5);
         } else {
            matrices.translate(0.5 - direction.getStepX() * 0.25, 0.25, 0.5 - direction.getStepZ() * 0.25);
         }

         matrices.scale(-1.0F, -1.0F, 1.0F);
         matrices.rotate(Axis.YP.rotationDegrees(yaw));
         boolean previousPivotParts = this.renderer.allowPivotParts;
         this.renderer.allowPivotParts = false;
         this.renderer.setupRenderer(PartFilterScheme.SKULL, queue, matrices, 1.0F, light, 1.0F, OverlayTexture.NO_OVERLAY, false, false);
         int used = this.renderer.renderSpecialParts();
         this.complexity.use(used);
         boolean replaced = used > 0 || this.headRender(matrices, queue, light, true);
         this.renderer.allowPivotParts = previousPivotParts;
         matrices.popPose();
         return replaced;
      } else {
         return false;
      }
   }

   public boolean headRender(PoseStack matrices, SubmitNodeCollector queue, int light, boolean useComplexity) {
      if (this.renderer != null && this.loaded) {
         boolean previousMatrixUpdate = this.renderer.allowMatrixUpdate;
         boolean previousHiddenTransforms = this.renderer.allowHiddenTransforms;
         boolean previousIgnoreVisibility = this.renderer.ignoreVanillaVisibility;
         this.renderer.setupRenderer(PartFilterScheme.HEAD, queue, matrices, 1.0F, light, 1.0F, OverlayTexture.NO_OVERLAY, false, false);
         this.renderer.allowHiddenTransforms = false;
         this.renderer.allowMatrixUpdate = false;
         this.renderer.ignoreVanillaVisibility = true;
         int used = this.renderer.render();
         if (useComplexity) {
            this.complexity.use(used);
         }

         this.renderer.allowMatrixUpdate = previousMatrixUpdate;
         this.renderer.allowHiddenTransforms = previousHiddenTransforms;
         this.renderer.ignoreVanillaVisibility = previousIgnoreVisibility;
         return used > 0 && this.luaRuntime != null && !this.luaRuntime.vanilla_model.HEAD.checkVisible();
      } else {
         return false;
      }
   }

   public boolean renderItem(PoseStack matrices, SubmitNodeCollector queue, FiguraModelPart part, int light, int overlay) {
      if (this.loaded && this.renderer != null && part != null && part.parentType == ParentType.Item) {
         matrices.pushPose();
         matrices.rotate(Axis.ZP.rotationDegrees(180.0F));
         this.renderer.setupRenderer(PartFilterScheme.ITEM, queue, matrices, 1.0F, light, 1.0F, overlay, false, false);
         this.renderer.itemToRender = part;
         int used = this.renderer.renderSpecialParts();
         matrices.popPose();
         return used > 0;
      } else {
         return false;
      }
   }

   public FiguraTexture registerTexture(String textureName, NativeImage image, boolean ignoredSizeCheck) {
      FiguraTexture previous = this.renderer.customTextures.remove(textureName);
      if (previous != null) {
         previous.close();
      }

      FiguraTexture texture = new FiguraTexture(this, textureName, image);
      this.renderer.customTextures.put(textureName, texture);
      return texture;
   }

   public void clean() {
      if (this.luaRuntime != null) {
         this.luaRuntime.particles.removeParticles();
      }

      if (this.renderer != null) {
         this.renderer.invalidate();
      }

      this.renderer = null;
      this.luaRuntime = null;

      for (FiguraBuffer buffer : new ArrayList<>(this.openBuffers)) {
         buffer.baseClose();
      }

      this.openBuffers.clear();

      for (FiguraInputStream stream : new ArrayList<>(this.openInputStreams)) {
         try {
            stream.close();
         } catch (IOException var5) {
         }
      }

      this.openInputStreams.clear();

      for (FiguraOutputStream stream : new ArrayList<>(this.openOutputStreams)) {
         try {
            stream.close();
         } catch (IOException var4) {
         }
      }

      this.openOutputStreams.clear();
      this.resources.clear();
      this.completedFirstTick = false;
      this.events.clear();
   }

   public static class Instructions {
      public int max;
      public int remaining;
      private int currPre;
      private int currPost;
      public int pre;
      public int post;
      private boolean inverted;

      public Instructions(int remaining) {
         this.reset(remaining);
      }

      public Avatar.Instructions post() {
         this.inverted = true;
         return this;
      }

      public int getTotal() {
         return this.pre + this.post;
      }

      public void reset(int remaining) {
         this.max = this.remaining = remaining;
         this.currPre = this.currPost = 0;
      }

      public void use(int amount) {
         this.remaining -= amount;
         if (!this.inverted) {
            this.currPre += amount;
            this.pre = this.currPre;
         } else {
            this.currPost += amount;
            this.post = this.currPost;
            this.inverted = false;
         }
      }
   }
}
