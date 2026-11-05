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
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2019("Использование: .route <x> <y> <z> или .route stop");
         return 1;
      });
      var1.then(literal("stop").executes(var1x -> {
         this.stop();
         ChatLogger.method2019("Маршрут остановлен");
         return 1;
      }));
      var1.then(
         arg("x", IntegerArgumentType.integer()).then(arg("y", IntegerArgumentType.integer()).then(arg("z", IntegerArgumentType.integer()).executes(var1x -> {
            int var2 = IntegerArgumentType.getInteger(var1x, "x");
            int var3 = IntegerArgumentType.getInteger(var1x, "y");
            int var4 = IntegerArgumentType.getInteger(var1x, "z");
            this.start(new BlockPos(var2, var3, var4));
            return 1;
         })))
      );
   }

   private void start(BlockPos var1) {
      if (mc.player != null && mc.level != null) {
         LocalPlayer var2 = mc.player;
         Pathfinder.Entry2 var3 = this.findRoute(var2, var1);
         if (var3 == null) {
            this.stop();
            ChatLogger.method2022("Не удалось построить путь до " + var1.getX() + " " + var1.getY() + " " + var1.getZ());
         } else {
            this.route = var3;
            this.destination = var1.immutable();
            this.nextBlock = null;
            this.lastPosition = var2.position();
            this.stuckTicks = 0;
            ChatLogger.method2019("Иду к " + var1.getX() + " " + var1.getY() + " " + var1.getZ() + " (" + this.route.getItems().size() + " блоков)");
         }
      } else {
         ChatLogger.method2022("Сначала зайди в мир");
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.route != null) {
         LocalPlayer var2 = mc.player;
         if (var2 != null && mc.level != null && this.destination != null) {
            this.nextBlock = this.route.method779(var2.position());
            if (this.nextBlock == null) {
               this.stop();
               ChatLogger.method2019("Маршрут завершён");
            } else {
               this.rotateTo(var2, this.nextBlock);
               this.updateStuckState(var2);
            }
         } else {
            this.stop();
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      LocalPlayer var2 = mc.player;
      if (this.route != null && this.nextBlock != null && var2 != null) {
         var1.method3044();
         float var3 = this.yawTo(var2, this.nextBlock);
         float var4 = Math.abs(Mth.wrapDegrees(var3 - var2.getYRot()));
         if (var4 <= 55.0F) {
            var1.method3038(true);
         }

         int var5 = Mth.floor(var2.getY() + 0.01);
         boolean var6 = this.nextBlock.getY() > var5 || var2.horizontalCollision;
         var1.method3041(var6);
      }
   }

   private void rotateTo(LocalPlayer var1, BlockPos var2) {
      float var3 = this.yawTo(var1, var2);
      float var4 = Mth.wrapDegrees(var3 - var1.getYRot());
      var1.setYRot(var1.getYRot() + Mth.clamp(var4, -35.0F, 35.0F));
   }

   private float yawTo(LocalPlayer var1, BlockPos var2) {
      Vec3 var3 = Pathfinder.method761(var2);
      Vec3 var4 = new Vec3(var3.x, var1.getEyeY(), var3.z);
      return Rotation.method3128(var4, var1.getEyePosition()).getFloatValue();
   }

   private void updateStuckState(LocalPlayer var1) {
      Vec3 var2 = var1.position();
      if (this.lastPosition != null && var2.distanceToSqr(this.lastPosition) < 4.0E-4) {
         this.stuckTicks++;
      } else {
         this.stuckTicks = 0;
      }

      this.lastPosition = var2;
      if (this.stuckTicks >= 40 && var1.onGround()) {
         Pathfinder.Entry2 var3 = this.findRoute(var1, this.destination);
         if (var3 == null) {
            this.stop();
            ChatLogger.method2022("Не удалось перестроить маршрут");
         } else {
            this.route = var3;
            this.nextBlock = null;
            this.stuckTicks = 0;
            ChatLogger.method2021("Маршрут перестроен");
         }
      }
   }

   private Pathfinder.Entry2 findRoute(LocalPlayer var1, BlockPos var2) {
      return Pathfinder.method757(var1.blockPosition(), var2).orElse(null);
   }

   private void stop() {
      this.route = null;
      this.destination = null;
      this.nextBlock = null;
      this.lastPosition = null;
      this.stuckTicks = 0;
   }
}
