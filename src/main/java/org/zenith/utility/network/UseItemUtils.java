package org.zenith.utility.network;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import kotlin.collections.EmptyMap;
import lombok.Generated;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.misc.ColorUtils;

public final class UseItemUtils implements ZenithAccess {
   public static void method6502(PredictiveAction var0) {
      mc.gameMode.startPrediction(mc.level, var0);
   }

   public static void method6503() {
      mc.player.connection.send(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
      mc.player.startFallFlying();
   }

   public static void method6504(Packet<?> var0) {
      mc.getConnection().getConnection().send(var0, null);
   }

   public static List<BlockPos> method6505(BlockPos var0, float var1) {
      return method6507(var0, var1, var1, true);
   }

   public static List<BlockPos> method6506(BlockPos var0, float var1, float var2) {
      return method6507(var0, var1, var2, true);
   }

   public static List<BlockPos> method6507(BlockPos var0, float var1, float var2, boolean var3) {
      ArrayList<BlockPos> var4 = new ArrayList<>();
      int var5 = var0.getX();
      int var6 = var0.getY();
      int var7 = var0.getZ();
      int var8 = var3 ? var6 - (int)var2 : var6;

      for (int var9 = var5 - (int)var1; var9 <= var5 + var1; var9++) {
         for (int var10 = var7 - (int)var1; var10 <= var7 + var1; var10++) {
            for (int var11 = var8; var11 <= var6 + var2; var11++) {
               var4.add(new BlockPos(var9, var11, var10));
            }
         }
      }

      return var4;
   }

   public static List<BlockPos> method6508(BlockPos var0, BlockPos var1) {
      ArrayList<BlockPos> var2 = new ArrayList<>();

      for (int var3 = var0.getX(); var3 <= var1.getX(); var3++) {
         for (int var4 = var0.getZ(); var4 <= var1.getZ(); var4++) {
            for (int var5 = var0.getY(); var5 <= var1.getY(); var5++) {
               var2.add(new BlockPos(var3, var5, var4));
            }
         }
      }

      return var2;
   }

   public static Type method6509(int var0) {
      return var0 < 8 ? Type.MOUSE : Type.KEYBOARD;
   }

   public static Stream<Entity> method6510() {
      return StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false);
   }

   public static boolean method6511(Pose var0) {
      return mc.player.level().noCollision(mc.player, mc.player.getDimensions(var0).makeBoundingBox(mc.player.position()).deflate(1.0E-7));
   }

   public static boolean method6512(Holder<MobEffect> var0) {
      var var10000 = mc.player.getActiveEffectsMap();
      if (var10000 != null) {
         if (var10000.isEmpty()) {
            return false;
         }
      }

      return var10000.containsKey(var0);
   }

   public static boolean method6513(Block var0) {
      return method6515(mc.player.getBoundingBox().inflate(-0.001), var0);
   }

   public static void method6514(BlockHitResult var0, InteractionHand var1) {
      if (mc.gameMode.useItemOn(mc.player, var1, var0) instanceof Success var3) {
         if (var3.swingSource() == SwingSource.PREDICTED) {
            org.zenith.utility.game.SwingCompat.swing(mc.player, var1);
         }
      }
   }

   public static boolean method6515(AABB var0, Block var1) {
      return method6519(var0, var1x -> mc.level.getBlockState(var1x).getBlock().equals(var1));
   }

   public static boolean method6516(AABB var0, List<Block> var1) {
      return method6519(var0, var1x -> var1.contains(mc.level.getBlockState(var1x).getBlock()));
   }

   public static FireworkRocketEntity method6517() {
      for (Entity var1 : mc.level.entitiesForRendering()) {
         if (var1 instanceof FireworkRocketEntity var2 && var2.attachedToEntity == mc.player) {
            return var2;
         }
      }

      return null;
   }

   public static int method6518() {
      int var0 = 0;

      while (mc.player.getAttackStrengthScale(var0) < 0.9F && var0 < 20) {
         var0++;
      }

      return var0;
   }

   public static boolean method6519(AABB var0, Predicate<BlockPos> var1) {
      return BlockPos.betweenClosedStream(var0).anyMatch(var1);
   }

   public static boolean method6520(Key var0) {
      // vanilla keys carry SDL codes since 26.3; method6522 works on Zenith's GLFW codes
      int var1 = var0.getType() == Type.MOUSE ? org.zenith.utility.misc.KeyCompat.mouseToGlfw(var0.getValue()) : org.zenith.utility.misc.KeyCompat.toGlfw(var0.getValue());
      return method6522(var0.getType(), var1);
   }

   public static boolean method6521(BindSetting var0) {
      int var1 = var0.getKeyCode();
      return mc.gui.screen() == null && var0.isVisible() && method6522(method6509(var1), var1);
   }

   public static boolean method6522(Type var0, int var1) {
      if (var1 != -1) {
         switch (var0) {
            case KEYBOARD:
               return org.zenith.utility.misc.KeyCompat.isKeyDown(var1);
            case MOUSE:
               return org.zenith.utility.misc.KeyCompat.isMouseDown(var1);
         }
      }

      return false;
   }

   public static boolean method6523(BlockPos var0) {
      return method6524(mc.level.getBlockState(var0));
   }

   public static boolean method6524(BlockState var0) {
      return var0.isAir() || var0.getBlock().equals(Blocks.CAVE_AIR) || var0.getBlock().equals(Blocks.VOID_AIR);
   }

   public static boolean method6525(Screen var0) {
      return var0 instanceof ChatScreen;
   }

   public static boolean method6526() {
      return mc.player == null || mc.level == null;
   }

   public static void useItem(InteractionHand var0) {
      if (mc.gameMode.useItem(mc.player, var0) instanceof Success var2 && var2.swingSource() == SwingSource.PREDICTED) {
         org.zenith.utility.game.SwingCompat.swing(mc.player, var0);
      }
   }

   public static float method6527(LivingEntity var0) {
      float var1 = var0.getHealth() + var0.getAbsorptionAmount();
      if (var0 instanceof Player var2) {
         String var3 = Zenith.getInstance().getServerDetector().getText();
         switch (var3) {
            case "FunTime":
            case "ReallyWorld":
               Objective var5 = var2.level().getScoreboard().getDisplayObjective(DisplaySlot.BELOW_NAME);
               if (var5 != null) {
                  ReadOnlyScoreInfo var6 = var2.level().getScoreboard().getPlayerScoreInfo(var2, var5);
                  if (var6 != null) {
                     MutableComponent var7 = var6.formatValue(var5.numberFormatOrDefault(StyledFormat.NO_STYLE));
                     String var8 = ColorUtils.method6438(var7.getString());
                     if (var8 != null && method6528(var8.trim())) {
                        try {
                           var1 = Float.parseFloat(var8);
                        } catch (NumberFormatException var10) {
                        }
                     }
                  }
               }
         }
      }

      return var1;
   }

   private static boolean method6528(String var0) {
      int var1 = var0.length();
      int var2 = 0;
      if (0 < var1 && (var0.charAt(var2) == '+' || var0.charAt(var2) == '-')) {
         var2++;
      }

      boolean var3;
      for (var3 = false; var2 < var1 && var0.charAt(var2) >= '0' && var0.charAt(var2) <= '9'; var3 = true) {
         var2++;
      }

      if (var2 < var1 && var0.charAt(var2) == '.') {
         var2++;

         while (var2 < var1 && var0.charAt(var2) >= '0' && var0.charAt(var2) <= '9') {
            var2++;
            var3 = true;
         }
      }

      if (!var3) {
         return false;
      } else {
         if (var2 < var1 && (var0.charAt(var2) == 'e' || var0.charAt(var2) == 'E')) {
            var2++;
            if (var2 < var1 && (var0.charAt(var2) == '+' || var0.charAt(var2) == '-')) {
               var2++;
            }

            boolean var4;
            for (var4 = false; var2 < var1 && var0.charAt(var2) >= '0' && var0.charAt(var2) <= '9'; var4 = true) {
               var2++;
            }

            if (!var4) {
               return false;
            }
         }

         if (var2 < var1 && "fFdD".indexOf(var0.charAt(var2)) >= 0) {
            var2++;
         }

         return var2 == var1;
      }
   }

   public static float method6529(LivingEntity var0) {
      return var0 instanceof Player var1 ? (float)ArmorSlots.method5384(var1).stream().filter(var0x -> !var0x.isEmpty()).count() : var0.getArmorValue();
   }

   public static boolean method6530(LivingEntity var0) {
      Rotation var1 = Rotation.method3129(mc.player.getBoundingBox().getCenter().subtract(var0.getEyePosition()));
      boolean var2 = var0.isUsingItem() && var0.getUseItem().getItem().equals(Items.SHIELD);
      boolean var3 = Math.abs(Mth.wrapDegrees(var0.getYRot() - var1.getFloatValue())) < 60.0F;
      return var2 && var3;
   }

   public static String method6531(LivingEntity var0) {
      return method6532(method6527(var0));
   }

   public static String method6532(float var0) {
      return String.format("%.1f", var0).replace(",", ".").replace(".0", "");
   }

   public static void method6533(double var0, Rotation var2) {
      method6504(
         new PosRot(
            mc.player.getX(),
            mc.player.getY() + var0,
            mc.player.getZ(),
            var2.getFloatValue(),
            var2.getFloatValue2(),
            mc.player.onGround(),
            mc.player.horizontalCollision
         )
      );
   }

   @Nullable
   public static UseItemUtils.Data method6534(@NotNull BlockPos var0) {
      if (mc.level.getBlockState(var0.offset(0, -1, 0)).isSolid()) {
         return new UseItemUtils.Data(var0.offset(0, -1, 0), Direction.UP);
      } else if (mc.level.getBlockState(var0.offset(-1, 0, 0)).isSolid()) {
         return new UseItemUtils.Data(var0.offset(-1, 0, 0), Direction.EAST);
      } else if (mc.level.getBlockState(var0.offset(1, 0, 0)).isSolid()) {
         return new UseItemUtils.Data(var0.offset(1, 0, 0), Direction.WEST);
      } else if (mc.level.getBlockState(var0.offset(0, 0, 1)).isSolid()) {
         return new UseItemUtils.Data(var0.offset(0, 0, 1), Direction.NORTH);
      } else {
         return mc.level.getBlockState(var0.offset(0, 0, -1)).isSolid() ? new UseItemUtils.Data(var0.offset(0, 0, -1), Direction.SOUTH) : null;
      }
   }

   @Generated
   private UseItemUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final BlockPos blockPos;
      private final Direction direction;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Direction direction() {
         return this.direction;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.direction, other.direction);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, direction);
      }

      @Override
      public String toString() {
         return "Data[" + "blockPos=" + this.blockPos + ", " + "direction=" + this.direction + "]";
      }

      public Data(BlockPos var1, Direction var2) {
         this.blockPos = var1;
         this.direction = var2;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public Direction getDirection() {
         return this.direction;
      }
   }
}
