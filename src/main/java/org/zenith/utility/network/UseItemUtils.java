package org.zenith.utility.network;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
   public static void method6502(PredictiveAction predictiveAction) {
      mc.gameMode.startPrediction(mc.level, predictiveAction);
   }

   public static void method6503() {
      mc.player.connection.send(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
      mc.player.startFallFlying();
   }

   public static void method6504(Packet<?> posRot) {
      mc.getConnection().getConnection().send(posRot, null);
   }

   public static List<BlockPos> method6505(BlockPos above, float f) {
      return method6507(above, f, f, true);
   }

   public static List<BlockPos> method6506(BlockPos blockPosition, float f, float f2) {
      return method6507(blockPosition, f, f2, true);
   }

   public static List<BlockPos> method6507(BlockPos above, float f, float f2, boolean flag) {
      ArrayList<BlockPos> poses = new ArrayList<>();
      int x = above.getX();
      int y = above.getY();
      int z = above.getZ();
      int n = flag ? y - (int)f2 : y;

      for (int i = x - (int)f; i <= x + f; i++) {
         for (int j = z - (int)f; j <= z + f; j++) {
            for (int k = n; k <= y + f2; k++) {
               poses.add(new BlockPos(i, k, j));
            }
         }
      }

      return poses;
   }

   public static List<BlockPos> method6508(BlockPos pos, BlockPos pos2) {
      ArrayList<BlockPos> poses = new ArrayList<>();

      for (int i = pos.getX(); i <= pos2.getX(); i++) {
         for (int j = pos.getZ(); j <= pos2.getZ(); j++) {
            for (int k = pos.getY(); k <= pos2.getY(); k++) {
               poses.add(new BlockPos(i, k, j));
            }
         }
      }

      return poses;
   }

   public static Type method6509(int keyCode) {
      return keyCode < 8 ? Type.MOUSE : Type.KEYBOARD;
   }

   public static Stream<Entity> method6510() {
      return StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false);
   }

   public static boolean method6511(Pose pose) {
      return mc.player.level().noCollision(mc.player, mc.player.getDimensions(pose).makeBoundingBox(mc.player.position()).deflate(1.0E-7));
   }

   public static boolean method6512(Holder<MobEffect> holder) {
      var activeEffectsMap = mc.player.getActiveEffectsMap();
      if (activeEffectsMap != null) {
         if (activeEffectsMap.isEmpty()) {
            return false;
         }
      }

      return activeEffectsMap.containsKey(holder);
   }

   public static boolean method6513(Block block) {
      return method6515(mc.player.getBoundingBox().inflate(-0.001), block);
   }

   public static void method6514(BlockHitResult blockHitResult, InteractionHand hand) {
      if (mc.gameMode.useItemOn(mc.player, hand, blockHitResult) instanceof Success success) {
         if (success.swingSource() == SwingSource.PREDICTED) {
            org.zenith.utility.game.SwingCompat.swing(mc.player, hand);
         }
      }
   }

   public static boolean method6515(AABB inflate, Block block) {
      return method6519(inflate, var1x -> mc.level.getBlockState(var1x).getBlock().equals(block));
   }

   public static boolean method6516(AABB inflate, List<Block> blocks) {
      return method6519(inflate, var1x -> blocks.contains(mc.level.getBlockState(var1x).getBlock()));
   }

   public static FireworkRocketEntity method6517() {
      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof FireworkRocketEntity fireworkRocketEntity && fireworkRocketEntity.attachedToEntity == mc.player) {
            return fireworkRocketEntity;
         }
      }

      return null;
   }

   public static int method6518() {
      int n = 0;

      while (mc.player.getAttackStrengthScale(n) < 0.9F && n < 20) {
         n++;
      }

      return n;
   }

   public static boolean method6519(AABB inflate, Predicate<BlockPos> predicate) {
      return BlockPos.betweenClosedStream(inflate).anyMatch(predicate);
   }

   public static boolean method6520(Key boundKeyOf) {
      // vanilla keys carry SDL codes since 26.3; method6522 works on Zenith's GLFW codes
      int mouseToGlfw = boundKeyOf.getType() == Type.MOUSE ? org.zenith.utility.misc.KeyCompat.mouseToGlfw(boundKeyOf.getValue()) : org.zenith.utility.misc.KeyCompat.toGlfw(boundKeyOf.getValue());
      return method6522(boundKeyOf.getType(), mouseToGlfw);
   }

   public static boolean method6521(BindSetting freeLookSetting) {
      int keyCode = freeLookSetting.getKeyCode();
      return mc.gui.screen() == null && freeLookSetting.isVisible() && method6522(method6509(keyCode), keyCode);
   }

   public static boolean method6522(Type type, int keyCode) {
      if (keyCode != -1) {
         switch (type) {
            case KEYBOARD:
               return org.zenith.utility.misc.KeyCompat.isKeyDown(keyCode);
            case MOUSE:
               return org.zenith.utility.misc.KeyCompat.isMouseDown(keyCode);
         }
      }

      return false;
   }

   public static boolean method6523(BlockPos containing) {
      return method6524(mc.level.getBlockState(containing));
   }

   public static boolean method6524(BlockState blockState) {
      return blockState.isAir() || blockState.getBlock().equals(Blocks.CAVE_AIR) || blockState.getBlock().equals(Blocks.VOID_AIR);
   }

   public static boolean method6525(Screen screen) {
      return screen instanceof ChatScreen;
   }

   public static boolean method6526() {
      return mc.player == null || mc.level == null;
   }

   public static void useItem(InteractionHand hand) {
      if (mc.gameMode.useItem(mc.player, hand) instanceof Success success && success.swingSource() == SwingSource.PREDICTED) {
         org.zenith.utility.game.SwingCompat.swing(mc.player, hand);
      }
   }

   public static float method6527(LivingEntity livingEntity) {
      float f = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
      if (livingEntity instanceof Player name) {
         String text = Zenith.getInstance().getServerDetector().getText();
         switch (text) {
            case "FunTime":
            case "ReallyWorld":
               Objective displayObjective = name.level().getScoreboard().getDisplayObjective(DisplaySlot.BELOW_NAME);
               if (displayObjective != null) {
                  ReadOnlyScoreInfo playerScoreInfo = name.level().getScoreboard().getPlayerScoreInfo(name, displayObjective);
                  if (playerScoreInfo != null) {
                     MutableComponent formatValue = playerScoreInfo.formatValue(displayObjective.numberFormatOrDefault(StyledFormat.NO_STYLE));
                     String str = ColorUtils.method6438(formatValue.getString());
                     if (str != null && method6528(str.trim())) {
                        try {
                           f = Float.parseFloat(str);
                        } catch (NumberFormatException e) {
                        }
                     }
                  }
               }
         }
      }

      return f;
   }

   private static boolean method6528(String str) {
      int length = str.length();
      int index = 0;
      if (0 < length && (str.charAt(index) == '+' || str.charAt(index) == '-')) {
         index++;
      }

      boolean flag;
      for (flag = false; index < length && str.charAt(index) >= '0' && str.charAt(index) <= '9'; flag = true) {
         index++;
      }

      if (index < length && str.charAt(index) == '.') {
         index++;

         while (index < length && str.charAt(index) >= '0' && str.charAt(index) <= '9') {
            index++;
            flag = true;
         }
      }

      if (!flag) {
         return false;
      } else {
         if (index < length && (str.charAt(index) == 'e' || str.charAt(index) == 'E')) {
            index++;
            if (index < length && (str.charAt(index) == '+' || str.charAt(index) == '-')) {
               index++;
            }

            boolean flag2;
            for (flag2 = false; index < length && str.charAt(index) >= '0' && str.charAt(index) <= '9'; flag2 = true) {
               index++;
            }

            if (!flag2) {
               return false;
            }
         }

         if (index < length && "fFdD".indexOf(str.charAt(index)) >= 0) {
            index++;
         }

         return index == length;
      }
   }

   public static float method6529(LivingEntity player) {
      return player instanceof Player player2 ? (float)ArmorSlots.method5384(player2).stream().filter(var0x -> !var0x.isEmpty()).count() : player.getArmorValue();
   }

   public static boolean method6530(LivingEntity livingEntity) {
      Rotation rotation = Rotation.method3129(mc.player.getBoundingBox().getCenter().subtract(livingEntity.getEyePosition()));
      boolean flag = livingEntity.isUsingItem() && livingEntity.getUseItem().getItem().equals(Items.SHIELD);
      boolean flag2 = Math.abs(Mth.wrapDegrees(livingEntity.getYRot() - rotation.getFloatValue())) < 60.0F;
      return flag && flag2;
   }

   public static String method6531(LivingEntity livingEntity) {
      return method6532(method6527(livingEntity));
   }

   public static String method6532(float floatValue) {
      return String.format("%.1f", floatValue).replace(",", ".").replace(".0", "");
   }

   public static void method6533(double d, Rotation rotation) {
      method6504(
         new PosRot(
            mc.player.getX(),
            mc.player.getY() + d,
            mc.player.getZ(),
            rotation.getFloatValue(),
            rotation.getFloatValue2(),
            mc.player.onGround(),
            mc.player.horizontalCollision
         )
      );
   }

   @Nullable
   public static UseItemUtils.Data method6534(@NotNull BlockPos offset) {
      if (mc.level.getBlockState(offset.offset(0, -1, 0)).isSolid()) {
         return new UseItemUtils.Data(offset.offset(0, -1, 0), Direction.UP);
      } else if (mc.level.getBlockState(offset.offset(-1, 0, 0)).isSolid()) {
         return new UseItemUtils.Data(offset.offset(-1, 0, 0), Direction.EAST);
      } else if (mc.level.getBlockState(offset.offset(1, 0, 0)).isSolid()) {
         return new UseItemUtils.Data(offset.offset(1, 0, 0), Direction.WEST);
      } else if (mc.level.getBlockState(offset.offset(0, 0, 1)).isSolid()) {
         return new UseItemUtils.Data(offset.offset(0, 0, 1), Direction.NORTH);
      } else {
         return mc.level.getBlockState(offset.offset(0, 0, -1)).isSolid() ? new UseItemUtils.Data(offset.offset(0, 0, -1), Direction.SOUTH) : null;
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

      public Data(BlockPos blockPos, Direction direction) {
         this.blockPos = blockPos;
         this.direction = direction;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public Direction getDirection() {
         return this.direction;
      }
   }
}
