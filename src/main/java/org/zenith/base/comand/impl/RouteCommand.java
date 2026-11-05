package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.Rotation;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.game.Pathfinder;

public final class RouteCommand extends CommandAbstract {
   private static final int STUCK_TICKS_BEFORE_REPATH = 40;
   private static final float MAX_YAW_STEP = 35.0F;
   private static final float MOVE_YAW_TOLERANCE = 55.0F;
   private Pathfinder.Entry2 route;
   private BlockPos destination;
   private BlockPos nextBlock;
   private Vec3 lastPosition;
   private int stuckTicks;

   public RouteCommand() {
      super("route");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Использование: .route <x> <y> <z> или .route stop");
         return 1;
      });
      literalArgumentBuilder.then(literal("stop").executes(var1x -> {
         this.stop();
         ChatLogger.method2019("Маршрут остановлен");
         return 1;
      }));
      literalArgumentBuilder.then(
         arg("x", IntegerArgumentType.integer()).then(arg("y", IntegerArgumentType.integer()).then(arg("z", IntegerArgumentType.integer()).executes(var1x -> {
            int integer = IntegerArgumentType.getInteger(var1x, "x");
            int y = IntegerArgumentType.getInteger(var1x, "y");
            int z = IntegerArgumentType.getInteger(var1x, "z");
            this.start(new BlockPos(integer, y, z));
            return 1;
         })))
      );
   }

   private void start(BlockPos pos) {
      if (mc.player != null && mc.level != null) {
         LocalPlayer player = mc.player;
         Pathfinder.Entry2 route = this.findRoute(player, pos);
         if (route == null) {
            this.stop();
            ChatLogger.method2022("Не удалось построить путь до " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
         } else {
            this.route = route;
            this.destination = pos.immutable();
            this.nextBlock = null;
            this.lastPosition = player.position();
            this.stuckTicks = 0;
            ChatLogger.method2019("Иду к " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " (" + this.route.getItems().size() + " блоков)");
         }
      } else {
         ChatLogger.method2022("Сначала зайди в мир");
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.route != null) {
         LocalPlayer player = mc.player;
         if (player != null && mc.level != null && this.destination != null) {
            this.nextBlock = this.route.method779(player.position());
            if (this.nextBlock == null) {
               this.stop();
               ChatLogger.method2019("Маршрут завершён");
            } else {
               this.rotateTo(player, this.nextBlock);
               this.updateStuckState(player);
            }
         } else {
            this.stop();
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      LocalPlayer player = mc.player;
      if (this.route != null && this.nextBlock != null && player != null) {
         event.method3044();
         float yawTo = this.yawTo(player, this.nextBlock);
         float f = Math.abs(Mth.wrapDegrees(yawTo - player.getYRot()));
         if (f <= 55.0F) {
            event.method3038(true);
         }

         int n = Mth.floor(player.getY() + 0.01);
         boolean jump = this.nextBlock.getY() > n || player.horizontalCollision;
         event.method3041(jump);
      }
   }

   private void rotateTo(LocalPlayer player, BlockPos nextBlock) {
      float yawTo = this.yawTo(player, nextBlock);
      float f = Mth.wrapDegrees(yawTo - player.getYRot());
      player.setYRot(player.getYRot() + Mth.clamp(f, -35.0F, 35.0F));
   }

   private float yawTo(LocalPlayer player, BlockPos nextBlock) {
      Vec3 vec = Pathfinder.method761(nextBlock);
      Vec3 vec2 = new Vec3(vec.x, player.getEyeY(), vec.z);
      return Rotation.method3128(vec2, player.getEyePosition()).getFloatValue();
   }

   private void updateStuckState(LocalPlayer player) {
      Vec3 position = player.position();
      if (this.lastPosition != null && position.distanceToSqr(this.lastPosition) < 4.0E-4) {
         this.stuckTicks++;
      } else {
         this.stuckTicks = 0;
      }

      this.lastPosition = position;
      if (this.stuckTicks >= 40 && player.onGround()) {
         Pathfinder.Entry2 route = this.findRoute(player, this.destination);
         if (route == null) {
            this.stop();
            ChatLogger.method2022("Не удалось перестроить маршрут");
         } else {
            this.route = route;
            this.nextBlock = null;
            this.stuckTicks = 0;
            ChatLogger.method2021("Маршрут перестроен");
         }
      }
   }

   private Pathfinder.Entry2 findRoute(LocalPlayer player, BlockPos pos) {
      return Pathfinder.method757(player.blockPosition(), pos).orElse(null);
   }

   private void stop() {
      this.route = null;
      this.destination = null;
      this.nextBlock = null;
      this.lastPosition = null;
      this.stuckTicks = 0;
   }
}
