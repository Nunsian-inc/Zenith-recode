package org.zenith.base.figura.model.rendertasks;

import org.zenith.base.figura.model.rendering.FiguraBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.api.world.BlockStateAPI;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.model.FiguraModelPart;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "BlockTask", value = "block_task")
public class BlockTask extends RenderTask {
   private BlockState block;
   private int cachedComplexity;
   private final BlockModelRenderState renderState = new BlockModelRenderState();

   public BlockTask(String name, Avatar owner, FiguraModelPart parent) {
      super(name, owner, parent);
   }

   @Override
   public void render(PoseStack poseStack, SubmitNodeCollector queue, FiguraBufferSource buffer, int light, int overlay) {
      poseStack.scale(16.0F, 16.0F, 16.0F);
      int newLight = this.customization.light != null ? this.customization.light : light;
      int newOverlay = this.customization.overlay != null ? this.customization.overlay : overlay;
      this.renderState.submit(poseStack, queue, newLight, newOverlay, 0);
   }

   @Override
   public int getComplexity() {
      return this.cachedComplexity;
   }

   @Override
   public boolean shouldRender() {
      return super.shouldRender() && this.block != null && !this.block.isAir();
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "block"),
            @LuaMethodOverload(argumentTypes = BlockStateAPI.class, argumentNames = "block")
      },
      aliases = "block",
      value = "block_task.set_block"
   )
   public BlockTask setBlock(Object block) {
      this.block = LuaUtils.parseBlockState("block", block);
      Minecraft client = Minecraft.getInstance();
      // 26.3: block states are drawn through a BlockModelRenderState, like the enderman carried block
      new BlockModelResolver(client.getModelManager()).update(this.renderState, this.block, EndermanRenderer.BLOCK_DISPLAY_CONTEXT);
      this.cachedComplexity = 0;
      List<BlockStateModelPart> parts = new ArrayList<>();
      client.getModelManager().getBlockStateModelSet().get(this.block).collectParts(RandomSource.create(), parts);

      for (BlockStateModelPart part : parts) {
         this.cachedComplexity = this.cachedComplexity + part.getQuads(null).size();

         for (Direction dir : Direction.values()) {
            this.cachedComplexity = this.cachedComplexity + part.getQuads(dir).size();
         }
      }

      return this;
   }

   @LuaWhitelist
   public BlockTask block(Object block) {
      return this.setBlock(block);
   }

   @Override
   public String toString() {
      return this.name + " (Block Render Task)";
   }
}
