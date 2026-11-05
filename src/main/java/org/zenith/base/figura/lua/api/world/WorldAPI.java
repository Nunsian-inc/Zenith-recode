package org.zenith.base.figura.lua.api.world;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.ReadOnlyLuaTable;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "WorldAPI", value = "world")
public final class WorldAPI {
   public static final WorldAPI INSTANCE = new WorldAPI();

   private WorldAPI() {
   }

   public static ClientLevel getCurrentWorld() {
      return Minecraft.getInstance().level;
   }

   @LuaWhitelist
   public static BiomeAPI getBiome(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      FiguraVec3 pos = LuaUtils.parseVec3("getBiome", x, y, z);
      BlockPos blockPos = pos.asBlockPos();
      if (world == null) {
         throw new LuaError("World is not loaded");
      } else {
         return new BiomeAPI((Biome)world.getBiome(blockPos).value(), blockPos);
      }
   }

   @LuaWhitelist
   public static BlockStateAPI getBlockState(Object x, Double y, Double z) {
      FiguraVec3 vector = LuaUtils.parseVec3("getBlockState", x, y, z);
      BlockPos pos = vector.asBlockPos();
      ClientLevel world = getCurrentWorld();
      return world != null && world.hasChunkAt(pos) ? new BlockStateAPI(world.getBlockState(pos), pos) : new BlockStateAPI(Blocks.AIR.defaultBlockState(), pos);
   }

   @LuaWhitelist
   public static boolean isChunkLoaded(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      return world != null && world.hasChunkAt(LuaUtils.parseVec3("isChunkLoaded", x, y, z).asBlockPos());
   }

   @LuaWhitelist
   public static List<BlockStateAPI> getBlocks(Object x, Object y, Double z, Double w, Double t, Double h) {
      Pair<FiguraVec3, FiguraVec3> pair = LuaUtils.parse2Vec3("getBlocks", x, y, z, w, t, h, 1);
      List<BlockStateAPI> list = new ArrayList<>();
      BlockPos min = ((FiguraVec3)pair.getFirst()).asBlockPos();
      BlockPos max = ((FiguraVec3)pair.getSecond()).asBlockPos();
      max = new BlockPos(Math.min(min.getX() + 8, max.getX()), Math.min(min.getY() + 8, max.getY()), Math.min(min.getZ() + 8, max.getZ()));
      if (min.compareTo(max) > 0) {
         throw new LuaError("Your max value can't be smaller than your min!");
      } else {
         ClientLevel world = getCurrentWorld();
         if (world != null && world.hasChunksAt(min.getX(), min.getZ(), max.getX(), max.getZ())) {
            BlockPos.betweenClosedStream(min, max).forEach(blockPos -> list.add(new BlockStateAPI(world.getBlockState(blockPos), blockPos)));
            return list;
         } else {
            return list;
         }
      }
   }

   @LuaWhitelist
   public static int getRedstonePower(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return 0;
      }

      BlockPos blockPos = LuaUtils.parseVec3("getRedstonePower", x, y, z).asBlockPos();
      return !world.hasChunkAt(blockPos) ? 0 : world.getBestNeighborSignal(blockPos);
   }

   @LuaWhitelist
   public static int getStrongRedstonePower(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return 0;
      }

      BlockPos blockPos = LuaUtils.parseVec3("getStrongRedstonePower", x, y, z).asBlockPos();
      return !world.hasChunkAt(blockPos) ? 0 : world.getDirectSignalTo(blockPos);
   }

   @LuaWhitelist
   public static double getTime(double delta) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return 0.0;
      }

      long time = world.getGameTime();
      return Mth.lerp(delta, time - 1.0, time);
   }

   @LuaWhitelist
   public static double getTimeOfDay(double delta) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return 0.0;
      }

      long time = world.getDefaultClockTime();
      return Mth.lerp(delta, time - 1.0, time);
   }

   @LuaWhitelist
   public static double getDayTime(double delta) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return 0.0;
      }

      long time = world.getDefaultClockTime();
      return Mth.lerp(delta, time - 1.0, time) % 24000.0;
   }

   @LuaWhitelist
   public static double getDay(double delta) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return 0.0;
      }

      long time = world.getDefaultClockTime();
      return Math.floor(Mth.lerp(delta, time - 1.0, time) / 24000.0);
   }

   @LuaWhitelist
   public static int getMoonPhase() {
      ClientLevel world = getCurrentWorld();
      return world == null ? 0 : ((MoonPhase)world.environmentAttributes().getDimensionValue(EnvironmentAttributes.MOON_PHASE)).index();
   }

   @LuaWhitelist
   public static boolean isThundering() {
      return getCurrentWorld() != null && getCurrentWorld().isThundering();
   }

   @LuaWhitelist
   public static double getRainGradient(Float delta) {
      ClientLevel world = getCurrentWorld();
      return world == null ? 0.0 : world.getRainLevel(delta == null ? 1.0F : delta);
   }

   @LuaWhitelist
   public static Integer getLightLevel(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return null;
      }

      BlockPos blockPos = LuaUtils.parseVec3("getLightLevel", x, y, z).asBlockPos();
      return !world.hasChunkAt(blockPos) ? null : world.getMaxLocalRawBrightness(blockPos);
   }

   @LuaWhitelist
   public static Integer getSkyLightLevel(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return null;
      }

      BlockPos blockPos = LuaUtils.parseVec3("getSkyLightLevel", x, y, z).asBlockPos();
      return !world.hasChunkAt(blockPos) ? null : world.getBrightness(LightLayer.SKY, blockPos);
   }

   @LuaWhitelist
   public static Integer getBlockLightLevel(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return null;
      }

      BlockPos blockPos = LuaUtils.parseVec3("getBlockLightLevel", x, y, z).asBlockPos();
      return !world.hasChunkAt(blockPos) ? null : world.getBrightness(LightLayer.BLOCK, blockPos);
   }

   @LuaWhitelist
   public static Integer getHeight(Object x, Double z, String heightmap) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return null;
      }

      FiguraVec2 pos = LuaUtils.parseVec2("getHeight", x, z);
      BlockPos blockPos = BlockPos.containing(pos.x(), 0.0, pos.y());
      if (!world.hasChunkAt(blockPos)) {
         return null;
      }

      Types heightmapType;
      try {
         heightmapType = heightmap != null ? Types.valueOf(heightmap.toUpperCase(Locale.US)) : Types.MOTION_BLOCKING;
      } catch (IllegalArgumentException e) {
         throw new LuaError("Invalid heightmap type provided");
      }

      return world.getHeight(heightmapType, (int)pos.x(), (int)pos.y());
   }

   @LuaWhitelist
   public static Boolean isOpenSky(Object x, Double y, Double z) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return null;
      }

      BlockPos blockPos = LuaUtils.parseVec3("isOpenSky", x, y, z).asBlockPos();
      return !world.hasChunkAt(blockPos) ? null : world.canSeeSky(blockPos);
   }

   @LuaWhitelist
   public static String getDimension() {
      ClientLevel world = getCurrentWorld();
      return world == null ? "minecraft:overworld" : world.dimension().identifier().toString();
   }

   @LuaWhitelist
   public static int[] getBuildHeight() {
      ClientLevel world = getCurrentWorld();
      return world == null ? new int[]{-64, 320} : new int[]{world.getMinY(), world.getMaxY()};
   }

   @LuaWhitelist
   public static Map<String, EntityAPI<?>> getPlayers() {
      Map<String, EntityAPI<?>> result = new LinkedHashMap<>();
      ClientLevel world = getCurrentWorld();
      if (world != null) {
         for (Player player : world.players()) {
            result.put(player.getStringUUID(), EntityAPI.wrap(player));
         }
      }

      return result;
   }

   @LuaWhitelist
   public static List<EntityAPI<?>> getEntities(Object x1, Object y1, Double z1, Double x2, Double y2, Double z2) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return List.of();
      }

      Pair<FiguraVec3, FiguraVec3> pair = LuaUtils.parse2Vec3("getEntities", x1, y1, z1, x2, y2, z2, 1);
      AABB box = new AABB(((FiguraVec3)pair.getFirst()).asVec3(), ((FiguraVec3)pair.getSecond()).asVec3());
      return world.getEntitiesOfClass(Entity.class, box, entity -> true).stream().map(EntityAPI::wrap).collect(Collectors.toList());
   }

   @LuaWhitelist
   public static EntityAPI<?> getEntity(@LuaNotNil String uuid) {
      ClientLevel world = getCurrentWorld();
      if (world == null) {
         return null;
      }

      try {
         UUID id = UUID.fromString(uuid);

         for (Player player : world.players()) {
            if (player.getUUID().equals(id)) {
               return EntityAPI.wrap(player);
            }
         }
      } catch (IllegalArgumentException e) {
      }

      return null;
   }

   @LuaWhitelist
   public static Map<String, LuaTable> avatarVars() {
      HashMap<String, LuaTable> varList = new HashMap<>();

      for (Avatar avatar : AvatarManager.getAllAvatars()) {
         LuaTable tbl = avatar.luaRuntime != null && avatar.luaRuntime.avatar_meta != null ? avatar.luaRuntime.avatar_meta.storedStuff : new LuaTable();
         varList.put(avatar.owner.toString(), new ReadOnlyLuaTable(tbl));
      }

      return varList;
   }

   @LuaWhitelist
   public static boolean exists() {
      return getCurrentWorld() != null;
   }

   @LuaWhitelist
   public static FiguraVec3 getSpawnPoint() {
      ClientLevel world = getCurrentWorld();
      return FiguraVec3.fromBlockPos(world == null ? BlockPos.ZERO : world.getRespawnData().pos());
   }

   @LuaWhitelist
   public static BlockStateAPI newBlock(@LuaNotNil String id, Object x, Double y, Double z) {
      Identifier identifier = Identifier.tryParse(id);
      Block block = identifier == null ? Blocks.AIR : (Block)BuiltInRegistries.BLOCK.getValue(identifier);
      BlockPos pos = x == null ? null : LuaUtils.parseVec3("newBlock", x, y, z).asBlockPos();
      return new BlockStateAPI(block.defaultBlockState(), pos);
   }

   @LuaWhitelist
   public static ItemStackAPI newItem(@LuaNotNil String id, Integer count, Integer damage) {
      Identifier identifier = Identifier.tryParse(id);
      ItemStack stack = identifier == null ? ItemStack.EMPTY : new ItemStack((ItemLike)BuiltInRegistries.ITEM.getValue(identifier), count == null ? 1 : count);
      if (damage != null && stack.isDamageableItem()) {
         stack.setDamageValue(damage);
      }

      return ItemStackAPI.verify(stack);
   }

   @Override
   public String toString() {
      return "WorldAPI";
   }
}
