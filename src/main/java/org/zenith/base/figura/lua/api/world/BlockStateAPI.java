package org.zenith.base.figura.lua.api.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import org.luaj.vm2.LuaTable;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.utils.ColorUtils;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "BlockState", value = "blockstate")
public final class BlockStateAPI {
   public final BlockState blockState;
   public final String id;
   private BlockPos pos;

   public BlockStateAPI(BlockState blockState, BlockPos pos) {
      this.blockState = blockState;
      this.pos = pos;
      this.id = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
   }

   private BlockPos blockPos() {
      return this.pos == null ? BlockPos.ZERO : this.pos;
   }

   @LuaWhitelist
   public String getID() {
      return this.id;
   }

   @LuaWhitelist
   public FiguraVec3 getPos() {
      return FiguraVec3.fromBlockPos(this.blockPos());
   }

   @LuaWhitelist
   public boolean isAir() {
      return this.blockState.isAir();
   }

   @LuaWhitelist
   public int getLuminance() {
      return this.blockState.getLightEmission();
   }

   @LuaWhitelist
   public boolean hasCollision() {
      return !this.blockState.getCollisionShape(WorldAPI.getCurrentWorld(), this.blockPos()).isEmpty();
   }

   @LuaWhitelist
   public boolean isFullCube() {
      return this.blockState.isCollisionShapeFullBlock(WorldAPI.getCurrentWorld(), this.blockPos());
   }

   @LuaWhitelist
   public boolean isSolidBlock() {
      return this.blockState.isRedstoneConductor(WorldAPI.getCurrentWorld(), this.blockPos());
   }

   @LuaWhitelist
   public float getHardness() {
      return this.blockState.getDestroySpeed(WorldAPI.getCurrentWorld(), this.blockPos());
   }

   @LuaWhitelist
   public ItemStackAPI asItem() {
      return ItemStackAPI.verify(this.blockState.getBlock().asItem().getDefaultInstance());
   }

   @LuaWhitelist
   public boolean emitsRedstonePower() {
      return this.blockState.isSignalSource();
   }

   @LuaWhitelist
   public float getBlastResistance() {
      return this.blockState.getBlock().getExplosionResistance();
   }

   @LuaWhitelist
   public float getFriction() {
      return this.blockState.getBlock().getFriction();
   }

   @LuaWhitelist
   public float getJumpVelocityMultiplier() {
      return this.blockState.getBlock().getJumpFactor();
   }

   @LuaWhitelist
   public float getVelocityMultiplier() {
      return this.blockState.getBlock().getSpeedFactor();
   }

   @LuaWhitelist
   public int getOpacity() {
      return this.blockState.getLightDampening();
   }

   @LuaWhitelist
   public boolean hasBlockEntity() {
      return this.blockState.hasBlockEntity();
   }

   @LuaWhitelist
   public boolean hasEmissiveLighting() {
      return this.blockState.emissiveRendering();
   }

   @LuaWhitelist
   public boolean isOpaque() {
      return this.blockState.canOcclude();
   }

   @LuaWhitelist
   public boolean isTranslucent() {
      return this.blockState.propagatesSkylightDown();
   }

   @LuaWhitelist
   public int getComparatorOutput() {
      return this.blockState.getAnalogOutputSignal(WorldAPI.getCurrentWorld(), this.blockPos(), null);
   }

   @LuaWhitelist
   public FiguraVec3 getMapColor() {
      Level world = WorldAPI.getCurrentWorld();
      MapColor mapColor = world == null ? this.blockState.getBlock().defaultMapColor() : this.blockState.getMapColor(world, this.blockPos());
      return mapColor == null ? FiguraVec3.of(0.0, 0.0, 0.0) : ColorUtils.intToRGB(mapColor.col);
   }

   @LuaWhitelist
   public List<String> getTags() {
      List<String> result = new ArrayList<>();
      this.blockState.typeHolder().tags().<Identifier>map(TagKey::location).forEach(id -> result.add(id.toString()));
      return result;
   }

   @LuaWhitelist
   public List<String> getFluidTags() {
      List<String> result = new ArrayList<>();
      this.blockState.getFluidState().typeHolder().tags().<Identifier>map(TagKey::location).forEach(id -> result.add(id.toString()));
      return result;
   }

   @LuaWhitelist
   public Map<String, Object> getSounds() {
      SoundType soundGroup = this.blockState.getSoundType();
      Map<String, Object> sounds = new HashMap<>();
      sounds.put("pitch", soundGroup.getPitch());
      sounds.put("volume", soundGroup.getVolume());
      sounds.put("break", BuiltInRegistries.SOUND_EVENT.getKey(soundGroup.getBreakSound()).toString());
      sounds.put("fall", BuiltInRegistries.SOUND_EVENT.getKey(soundGroup.getFallSound()).toString());
      sounds.put("hit", BuiltInRegistries.SOUND_EVENT.getKey(soundGroup.getHitSound()).toString());
      sounds.put("place", BuiltInRegistries.SOUND_EVENT.getKey(soundGroup.getPlaceSound()).toString());
      sounds.put("step", BuiltInRegistries.SOUND_EVENT.getKey(soundGroup.getStepSound()).toString());
      return sounds;
   }

   @LuaWhitelist
   public LuaTable getEntityData() {
      return null;
   }

   @LuaWhitelist
   public String toStateString() {
      return BlockStateParser.serialize(this.blockState);
   }

   @LuaWhitelist
   public LuaTable getProperties() {
      LuaTable result = new LuaTable();
      this.blockState.getValues().forEach(v -> result.set(v.property().getName(), v.value().toString()));
      return result;
   }

   @LuaWhitelist
   public BlockStateAPI setPos(Object x, Double y, Double z) {
      this.pos = LuaUtils.parseVec3("setPos", x, y, z).asBlockPos();
      return this;
   }

   @LuaWhitelist
   public BlockStateAPI pos(Object x, Double y, Double z) {
      return this.setPos(x, y, z);
   }

   @LuaWhitelist
   public List<List<FiguraVec3>> getCollisionShape() {
      List<List<FiguraVec3>> result = new ArrayList<>();

      for (AABB box : this.blockState.getCollisionShape(WorldAPI.getCurrentWorld(), this.blockPos()).toAabbs()) {
         result.add(List.of(FiguraVec3.of(box.minX, box.minY, box.minZ), FiguraVec3.of(box.maxX, box.maxY, box.maxZ)));
      }

      return result;
   }

   @LuaWhitelist
   public List<List<FiguraVec3>> getOutlineShape() {
      List<List<FiguraVec3>> result = new ArrayList<>();

      for (AABB box : this.blockState.getShape(WorldAPI.getCurrentWorld(), this.blockPos()).toAabbs()) {
         result.add(List.of(FiguraVec3.of(box.minX, box.minY, box.minZ), FiguraVec3.of(box.maxX, box.maxY, box.maxZ)));
      }

      return result;
   }

   @LuaWhitelist
   public Map<String, Set<String>> getTextures() {
      if (this.blockState.isAir()) {
         return Map.of();
      }

      Map<String, Set<String>> result = new LinkedHashMap<>();
      result.put(this.id, Set.of("ALL"));
      return result;
   }

   @LuaWhitelist
   public boolean __eq(BlockStateAPI other) {
      return other != null && this.blockState.equals(other.blockState);
   }

   @LuaWhitelist
   public Object __index(String key) {
      return "id".equals(key) ? this.id : ("properties".equals(key) ? this.getProperties() : null);
   }

   @Override
   public String toString() {
      return this.id + " (BlockState)";
   }
}
