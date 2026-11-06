package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Vec2f;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "CastleFly",
   description = "module.flybypass.description",
   category = ModuleCategory.MOVEMENT
)
public final class CastleFlyModule extends Module {
   public static final CastleFlyModule INSTANCE = new CastleFlyModule();
   private UseItemUtils.Data placementData;
   private ModeSetting mode = new ModeSetting("module.flyBypass.mode", "module.flyBypass.mode.desc", "module.flyBypass.safeMode", "module.flyBypass.riskMode");
   private boolean disablePending = false;
   private int placementY;
   private final SliderSetting delay = new SliderSetting("module.flyBypass.delay", 500.0F, 50.0F, 1000.0F, 50.0F, "module.flyBypass.delay.desc", "ms");
   private final Queue<ReachV3Module.Entry> delayedPackets = new ConcurrentLinkedQueue<>();
   private BlockHitResult cachedBlockHitResult = null;
   public int placementCooldownTicks = 0;

   private CastleFlyModule() {
   }

   @Override
   public void onEnable() {
      this.disablePending = false;
      super.onEnable();
      ChatLogger.method2019("Для работы держите ЛЮБОЙ блок руках");
   }

   @Override
   public void onDisable() {
      this.disablePending = true;
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && !event.isCancelled()) {
         try {
            Packet packet = event.getPacket();
            if (this.delayedPackets.isEmpty() && mc.level == null) {
               return;
            }

            if (packet instanceof ServerboundChatPacket
               || packet instanceof ClientboundSetHealthPacket
               || packet instanceof ClientboundSystemChatPacket
               || packet instanceof ServerboundChatCommandPacket) {
               return;
            }

            event.cancel();
            Vec3 updatedBlockPosition = null;
            if (packet instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
               updatedBlockPosition = new Vec3(blockUpdatePacket.getPos());
            }

            this.delayedPackets.add(new ReachV3Module.Entry(packet, updatedBlockPosition));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void onPacketProcessEnd(PacketProcessEndEvent event) {
      long now = System.currentTimeMillis();
      if (mc.level == null || mc.player == null) {
         this.delayedPackets.clear();
      }

      try {
         this.delayedPackets.removeIf(entry -> {
            if (now - entry.getTime() < (long)(this.delay.getValue3() * 2.0F) && mc.level != null && !this.disablePending) {
               return false;
            } else {
               try {
                  entry.getPacket().handle(mc.getConnection());
               } catch (Throwable e) {
               }

               return true;
            }
         });
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (this.disablePending) {
         this.disablePending = false;
         super.onDisable();
      }
   }

   @EventTarget
   public void onRotation(PreRotationEvent event) {
      this.cachedBlockHitResult = null;
      if (mc.hitResult instanceof BlockHitResult hitBlock) {
         this.cachedBlockHitResult = hitBlock;
      }

      if (this.mode.isSelected(0)) {
         rotationManager.requestRotation(
            new RotationRequest(
               rotationManager.getEffectiveRotation().add(new Vec2f(0.0F, 900.0F)),
               () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotationManager.getEffectiveRotation().add(new Vec2f(0.0F, 900.0F))),
               bcCameraTestnerv.getRotationConfig()
            ),
            20,
            this,
            2
         );
      } else {
         this.placementData = null;
         if (mc.options.keyJump.isDown() && !MovementInputUtils.method7418()) {
            this.placementY = (int)Math.floor(mc.player.getY() - 1.0);
         }

         PlayerSimulation simulation = PlayerSimulation.method6031(2);
         BlockPos predictedBlockPos = new BlockPos((int)Math.floor(simulation.getVec3d().x), (int)Math.floor(mc.player.getY() - 1.0), (int)Math.floor(simulation.getVec3d().z));
         this.placementData = this.findNearbyPlacement(predictedBlockPos);
         if (this.placementData != null) {
            BlockHitResult placementHit = new BlockHitResult(
               new Vec3(this.placementData.getBlockPos().getX() + 0.5, this.placementData.getBlockPos().getY() + 0.5, this.placementData.getBlockPos().getZ() + 0.5),
               this.placementData.getDirection(),
               this.placementData.getBlockPos(),
               false
            );
            Rotation rotation = RotationUtils.method6913(placementHit.getLocation());
            rotationManager.requestRotation(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
               20,
               this,
               1
            );
         }
      }
   }

   @EventTarget(0)
   public void onMoveInput(MoveInputEvent event) {
      MovementInputUtils.method7426(event, Zenith.getInstance().getRotationManager().getEffectiveRotation().getFloatValue(), RotationUtils.method6911().getFloatValue());
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.placementCooldownTicks > 0) {
         this.placementCooldownTicks--;
      } else {
         if (this.mode.isSelected(0)) {
            if (mc.hitResult instanceof BlockHitResult hitBlock && hitBlock.getType() == Type.BLOCK) {
               UseItemUtils.method6533(0.0, zenith.getRotationManager().getEffectiveRotation().add((float)Math.random(), (float)Math.random()));
               UseItemUtils.method6533(0.0, zenith.getRotationManager().getEffectiveRotation().add((float)Math.random(), (float)Math.random()));
               UseItemUtils.method6514(hitBlock, InteractionHand.MAIN_HAND);
               this.placementCooldownTicks = 2;
            }
         } else if (mc.hitResult instanceof BlockHitResult hitBlock && this.placementData != null && this.placementData.getBlockPos().equals(hitBlock.getBlockPos())) {
            Vec3 eyePosition = mc.player.getEyePosition();
            BlockPos placedBlockPos = hitBlock.getBlockPos().relative(this.placementData.getDirection());
            if (!canPlaceFromEyePosition(eyePosition, placedBlockPos, this.placementData.getDirection())) {
               return;
            }

            UseItemUtils.method6514(
               new BlockHitResult(
                  net.minecraft.world.phys.Vec3.atCenterOf(this.placementData.getBlockPos()).add((float)(Math.random() / 2.0)), this.placementData.getDirection(), this.placementData.getBlockPos(), false
               ),
               InteractionHand.MAIN_HAND
            );
            this.placementCooldownTicks = 1;
         }
      }
   }

   public static boolean canPlaceFromEyePosition(Vec3 eyePosition, BlockPos placedBlockPos, Direction direction) {
      double unusedMaxDistance = Double.MAX_VALUE;
      double minHeightOffset = Double.MIN_VALUE;
      AABB eyeColumn = new AABB(eyePosition.x, mc.player.getY() - 1.0, eyePosition.z, eyePosition.x, eyePosition.y + minHeightOffset, eyePosition.z);
      AABB adjacentBlockBox = new AABB(placedBlockPos.relative(direction));
      if (eyeColumn.intersects(adjacentBlockBox)) {
         return true;
      } else {
         return switch (direction) {
            case NORTH -> eyeColumn.minZ > adjacentBlockBox.minZ;
            case SOUTH -> eyeColumn.maxZ < adjacentBlockBox.maxZ;
            case EAST -> eyeColumn.maxX < adjacentBlockBox.maxX;
            case WEST -> eyeColumn.minX > adjacentBlockBox.minX;
            case UP -> eyeColumn.maxY < adjacentBlockBox.maxY;
            case DOWN -> eyeColumn.minY > adjacentBlockBox.minY;
         };
      }
   }

   private UseItemUtils.Data findNearbyPlacement(BlockPos targetPos) {
      UseItemUtils.Data placementData = UseItemUtils.method6534(targetPos);
      if (placementData != null) {
         return placementData;
      } else {
         placementData = UseItemUtils.method6534(targetPos.offset(-1, 0, 0));
         if (placementData != null) {
            return placementData;
         } else {
            placementData = UseItemUtils.method6534(targetPos.offset(1, 0, 0));
            if (placementData != null) {
               return placementData;
            } else {
               placementData = UseItemUtils.method6534(targetPos.offset(0, 0, 1));
               if (placementData != null) {
                  return placementData;
               } else {
                  placementData = UseItemUtils.method6534(targetPos.offset(0, 0, -1));
                  if (placementData != null) {
                     return placementData;
                  } else {
                     placementData = UseItemUtils.method6534(targetPos.offset(-2, 0, 0));
                     if (placementData != null) {
                        return placementData;
                     } else {
                        placementData = UseItemUtils.method6534(targetPos.offset(2, 0, 0));
                        if (placementData != null) {
                           return placementData;
                        } else {
                           placementData = UseItemUtils.method6534(targetPos.offset(0, 0, 2));
                           if (placementData != null) {
                              return placementData;
                           } else {
                              placementData = UseItemUtils.method6534(targetPos.offset(0, 0, -2));
                              if (placementData != null) {
                                 return placementData;
                              } else {
                                 placementData = UseItemUtils.method6534(targetPos.offset(0, -1, 0));
                                 if (placementData != null) {
                                    return placementData;
                                 } else {
                                    placementData = UseItemUtils.method6534(targetPos.offset(1, -1, 0));
                                    if (placementData != null) {
                                       return placementData;
                                    } else {
                                       placementData = UseItemUtils.method6534(targetPos.offset(-1, -1, 0));
                                       if (placementData != null) {
                                          return placementData;
                                       } else {
                                          placementData = UseItemUtils.method6534(targetPos.offset(0, -1, 1));
                                          return placementData != null ? placementData : UseItemUtils.method6534(targetPos.offset(0, -1, -1));
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean hasNoGroundBelow(double offsetX, double offsetZ) {
      return !mc.level.getBlockCollisions(mc.player, mc.player.getBoundingBox().inflate(-0.1, 0.0, -0.1).move(offsetX, -2.0, offsetZ)).iterator().hasNext();
   }
}
