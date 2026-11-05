package org.zenith.base.figura.model.rendertasks;

import org.zenith.base.figura.model.rendering.FiguraBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.OptionalInt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.luaj.vm2.LuaError;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.ducks.LivingEntityRendererAccessor;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.mixin.LivingEntityAccessor;
import org.zenith.base.figura.model.FiguraModelPart;

@LuaWhitelist
@LuaTypeDoc(name = "EntityTask", value = "entity_task")
public class EntityTask extends RenderTask {
   @Nullable
   Entity entity;
   long ticksSinceEntity;

   public EntityTask(String name, Avatar owner, FiguraModelPart parent) {
      super(name, owner, parent);
   }

   @Override
   public void render(PoseStack stack, SubmitNodeCollector queue, FiguraBufferSource buffer, int light, int overlay) {
      stack.scale(16.0F, 16.0F, 16.0F);
      label47:
      if (this.entity != null) {
         assert Minecraft.getInstance().level != null;
         this.entity.tickCount = (int)(Minecraft.getInstance().level.getGameTime() - this.ticksSinceEntity);
         OptionalInt prev = LivingEntityRendererAccessor.overrideOverlay;
         LivingEntityRendererAccessor.overrideOverlay = OptionalInt.of(this.customization.overlay != null ? this.customization.overlay : overlay);

         try {
            Minecraft client = Minecraft.getInstance();
            EntityRenderDispatcher manager = client.getEntityRenderDispatcher();
            EntityRenderState state = manager.extractEntity(this.entity, client.getDeltaTracker().getGameTimeDeltaPartialTick(true));
            state.lightCoords = this.customization.light != null ? this.customization.light : light;
            Camera camera = client.gameRenderer.mainCamera();
            CameraRenderState cameraState = new CameraRenderState();
            cameraState.initialized = camera.isInitialized();
            cameraState.pos = camera.position();
            cameraState.blockPos = camera.blockPosition();
            cameraState.orientation = new Quaternionf(camera.rotation());
            manager.submit(state, cameraState, 0.0, 0.0, 0.0, stack, queue);
         } finally {
            LivingEntityRendererAccessor.overrideOverlay = prev;
         }
      }
   }

   @Override
   public int getComplexity() {
      return 20;
   }

   @Override
   public boolean shouldRender() {
      return super.shouldRender() && this.entity != null;
   }

   @LuaWhitelist
   @LuaMethodDoc("entity_task.as_entity")
   public EntityAPI<?> asEntity() {
      return this.entity == null ? null : new EntityAPI<>(this.entity);
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "nbt"),
            @LuaMethodOverload(argumentTypes = {String.class, String.class}, argumentNames = {"id", "nbt"})
      },
      value = "entity_task.set_nbt"
   )
   @Contract("_,_->this")
   public EntityTask setNbt(String nbtOrId, String nullOrNbt) {
      try {
         CompoundTag finalNbt;
         if (nullOrNbt == null) {
            finalNbt = TagParser.parseCompoundFully(nbtOrId);
            if (!finalNbt.contains("id")) {
               throw new LuaError("Nbt must contain id");
            }
         } else {
            finalNbt = TagParser.parseCompoundFully(nullOrNbt);
            finalNbt.put("id", StringTag.valueOf(nbtOrId));
         }

         assert Minecraft.getInstance().level != null;
         this.entity = EntityType.loadEntityRecursive(finalNbt, Minecraft.getInstance().level, new net.minecraft.world.entity.EntitySpawnRequest(EntitySpawnReason.SPAWN_ITEM_USE, false), EntityProcessor.NOP);
         if (this.entity == null) {
            throw new LuaError("Could not create entity");
         }

         this.ticksSinceEntity = Minecraft.getInstance().level.getGameTime() - this.entity.tickCount;
         return this;
      } catch (CommandSyntaxException var4) {
         throw new LuaError(var4.getMessage());
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = Double.class, argumentNames = "distance"), value = "entity_task.update_walking_distance")
   @Contract("_->this")
   public EntityTask updateWalkingDistance(float distance) {
      if (this.entity != null && this.entity instanceof LivingEntity living) {
         ((LivingEntityAccessor)living).figura$updateWalkAnimation(distance);
      }

      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = FiguraVec2.class, argumentNames = "rotation"), value = "entity_task.set_head_rotation")
   @Contract("_->this")
   public EntityTask setHeadRotation(FiguraVec2 vec2) {
      if (this.entity != null && this.entity instanceof LivingEntity living) {
         living.yHeadRot = (float)vec2.y;
         living.yHeadRotO = (float)vec2.y;
         living.setXRot((float)vec2.x);
         living.xRotO = (float)vec2.x;
      }

      return this;
   }
}
