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
import org.zenith.client.events.Event5;
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
   private UseItemUtils.Data data;
   private ModeSetting mode = new ModeSetting("module.flyBypass.mode", "module.flyBypass.mode.desc", "module.flyBypass.safeMode", "module.flyBypass.riskMode");
   private boolean flag = false;
   private int intValue;
   private final SliderSetting delay = new SliderSetting("module.flyBypass.delay", 500.0F, 50.0F, 1000.0F, 50.0F, "module.flyBypass.delay.desc", "ms");
   private final Queue<ReachV3Module.Entry> queue = new ConcurrentLinkedQueue<>();
   private BlockHitResult blockHitResult = null;
   public int intValue2 = 0;

   private CastleFlyModule() {
   }

   @Override
   public void onEnable() {
      this.flag = false;
      super.onEnable();
      ChatLogger.method2019("Для работы держите ЛЮБОЙ блок руках");
   }

   @Override
   public void onDisable() {
      this.flag = true;
   }

   @EventTarget
   public void method7307(WorldRenderEvent event) {
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100() && !event.isCancelled()) {
         try {
            Packet packet3 = event.getPacket3();
            if (this.queue.isEmpty() && mc.level == null) {
               return;
            }

            if (packet3 instanceof ServerboundChatPacket
               || packet3 instanceof ClientboundSetHealthPacket
               || packet3 instanceof ClientboundSystemChatPacket
               || packet3 instanceof ServerboundChatCommandPacket) {
               return;
            }

            event.cancel();
            Vec3 vec = null;
            if (packet3 instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
               vec = new Vec3(blockUpdatePacket.getPos());
            }

            this.queue.add(new ReachV3Module.Entry(packet3, vec));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method7308(PacketProcessEndEvent event) {
      long now = System.currentTimeMillis();
      if (mc.level == null || mc.player == null) {
         this.queue.clear();
      }

      try {
         this.queue.removeIf(entry -> {
            if (now - entry.getTime() < (long)(this.delay.getValue3() * 2.0F) && mc.level != null && !this.flag) {
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

      if (this.flag) {
         this.flag = false;
         super.onDisable();
      }
   }

   @EventTarget
   public void method7309(Event5 event) {
      this.blockHitResult = null;
      if (mc.hitResult instanceof BlockHitResult blockHitResult) {
         this.blockHitResult = blockHitResult;
      }

      if (this.mode.isSelected(0)) {
         rotationManager.method7848(
            new RotationRequest(
               rotationManager.method7844().method3141(new Vec2f(0.0F, 900.0F)),
               () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotationManager.method7844().method3141(new Vec2f(0.0F, 900.0F))),
               bcCameraTestnerv.getRotationConfig()
            ),
            20,
            this,
            2
         );
      } else {
         this.data = null;
         if (mc.options.keyJump.isDown() && !MovementInputUtils.method7418()) {
            this.intValue = (int)Math.floor(mc.player.getY() - 1.0);
         }

         PlayerSimulation playerSimulation = PlayerSimulation.method6031(2);
         BlockPos pos = new BlockPos((int)Math.floor(playerSimulation.getVec3d().x), (int)Math.floor(mc.player.getY() - 1.0), (int)Math.floor(playerSimulation.getVec3d().z));
         this.data = this.method7313(pos);
         if (this.data != null) {
            BlockHitResult blockHitResult2 = new BlockHitResult(
               new Vec3(this.data.getBlockPos().getX() + 0.5, this.data.getBlockPos().getY() + 0.5, this.data.getBlockPos().getZ() + 0.5),
               this.data.getDirection(),
               this.data.getBlockPos(),
               false
            );
            Rotation rotation = RotationUtils.method6913(blockHitResult2.getLocation());
            rotationManager.method7848(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
               20,
               this,
               1
            );
         }
      }
   }

   @EventTarget(0)
   public void method7310(MoveInputEvent event) {
      MovementInputUtils.method7426(event, Zenith.getInstance().getRotationManager().method7844().getFloatValue(), RotationUtils.method6911().getFloatValue());
   }

   @EventTarget
   public void method7311(UpdateEvent event) {
      if (this.intValue2 > 0) {
         this.intValue2--;
      } else {
         if (this.mode.isSelected(0)) {
            if (mc.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
               UseItemUtils.method6533(0.0, zenith.getRotationManager().method7844().method3140((float)Math.random(), (float)Math.random()));
               UseItemUtils.method6533(0.0, zenith.getRotationManager().method7844().method3140((float)Math.random(), (float)Math.random()));
               UseItemUtils.method6514(blockHitResult, InteractionHand.MAIN_HAND);
               this.intValue2 = 2;
            }
         } else if (mc.hitResult instanceof BlockHitResult blockHitResult2 && this.data != null && this.data.getBlockPos().equals(blockHitResult2.getBlockPos())) {
            Vec3 eyePosition = mc.player.getEyePosition();
            BlockPos relative = blockHitResult2.getBlockPos().relative(this.data.getDirection());
            if (!method7312(eyePosition, relative, this.data.getDirection())) {
               return;
            }

            UseItemUtils.method6514(
               new BlockHitResult(
                  net.minecraft.world.phys.Vec3.atCenterOf(this.data.getBlockPos()).add((float)(Math.random() / 2.0)), this.data.getDirection(), this.data.getBlockPos(), false
               ),
               InteractionHand.MAIN_HAND
            );
            this.intValue2 = 1;
         }
      }
   }

   public static boolean method7312(Vec3 eyePosition, BlockPos relative, Direction direction) {
      double d = Double.MAX_VALUE;
      double d2 = Double.MIN_VALUE;
      AABB aabb = new AABB(eyePosition.x, mc.player.getY() - 1.0, eyePosition.z, eyePosition.x, eyePosition.y + d2, eyePosition.z);
      AABB aabb2 = new AABB(relative.relative(direction));
      if (aabb.intersects(aabb2)) {
         return true;
      } else {
         return switch (direction) {
            case NORTH -> aabb.minZ > aabb2.minZ;
            case SOUTH -> aabb.maxZ < aabb2.maxZ;
            case EAST -> aabb.maxX < aabb2.maxX;
            case WEST -> aabb.minX > aabb2.minX;
            case UP -> aabb.maxY < aabb2.maxY;
            case DOWN -> aabb.minY > aabb2.minY;
         };
      }
   }

   private UseItemUtils.Data method7313(BlockPos pos) {
      UseItemUtils.Data data = UseItemUtils.method6534(pos);
      if (data != null) {
         return data;
      } else {
         data = UseItemUtils.method6534(pos.offset(-1, 0, 0));
         if (data != null) {
            return data;
         } else {
            data = UseItemUtils.method6534(pos.offset(1, 0, 0));
            if (data != null) {
               return data;
            } else {
               data = UseItemUtils.method6534(pos.offset(0, 0, 1));
               if (data != null) {
                  return data;
               } else {
                  data = UseItemUtils.method6534(pos.offset(0, 0, -1));
                  if (data != null) {
                     return data;
                  } else {
                     data = UseItemUtils.method6534(pos.offset(-2, 0, 0));
                     if (data != null) {
                        return data;
                     } else {
                        data = UseItemUtils.method6534(pos.offset(2, 0, 0));
                        if (data != null) {
                           return data;
                        } else {
                           data = UseItemUtils.method6534(pos.offset(0, 0, 2));
                           if (data != null) {
                              return data;
                           } else {
                              data = UseItemUtils.method6534(pos.offset(0, 0, -2));
                              if (data != null) {
                                 return data;
                              } else {
                                 data = UseItemUtils.method6534(pos.offset(0, -1, 0));
                                 if (data != null) {
                                    return data;
                                 } else {
                                    data = UseItemUtils.method6534(pos.offset(1, -1, 0));
                                    if (data != null) {
                                       return data;
                                    } else {
                                       data = UseItemUtils.method6534(pos.offset(-1, -1, 0));
                                       if (data != null) {
                                          return data;
                                       } else {
                                          data = UseItemUtils.method6534(pos.offset(0, -1, 1));
                                          return data != null ? data : UseItemUtils.method6534(pos.offset(0, -1, -1));
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

   private boolean method7314(double xa, double za) {
      return !mc.level.getBlockCollisions(mc.player, mc.player.getBoundingBox().inflate(-0.1, 0.0, -0.1).move(xa, -2.0, za)).iterator().hasNext();
   }
}
