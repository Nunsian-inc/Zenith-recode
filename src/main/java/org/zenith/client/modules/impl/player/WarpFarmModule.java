package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.Pathfinder;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "WarpFarm",
   category = ModuleCategory.PLAYER,
   description = "module.warpfarm.description"
)
public final class WarpFarmModule extends Module {
   public static final WarpFarmModule INSTANCE = new WarpFarmModule();
   public static final BlockPos blockPos = new BlockPos(-320, 59, 25);
   public static final double CONST_doubleValue = 4.0;
   public static final double CONST_doubleValue2 = 40000.0;
   private static final long CONST_longValue = 5000L;
   public static final double CONST_doubleValue3 = 16384.0;
   private static final double CONST_doubleValue4 = 900.0;
   private static final long CONST_longValue2 = 500L;
   private static final int CONST_intValue = 5;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 74;
   public static Pathfinder.Data data = new Pathfinder.Data(0 << 16 | (char)-15536, 192, 1000, 3, false, true);
   private static final Pathfinder.Data data2 = new Pathfinder.Data(2 << 16 | 18928, 200, 100, 13, false, true);
   private static volatile int intValue = -1;
   private static final AtomicLong atomicLong = new AtomicLong();
   private static final String text = "Монеты";
   private static final String text2 = "Опыт";
   private final ModeSetting farmMode = new ModeSetting("module.warpFarm.farmMode", "module.warpFarm.farmMode.desc", "Монеты", "Опыт");
   private final SliderSetting deathLimit = new SliderSetting("module.warpFarm.deathLimit", 3.0F, 1.0F, 50.0F, 1.0F, "module.warpFarm.deathLimit.desc", "");
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private BlockPos walkTarget;
   private BlockPos nextRouteBlock;
   private int pathFailStreak;
   private int lastAnarchy = -32768 << 16 | 0;
   private int intValue2;
   private boolean flag;
   private int intValue3;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;

   private WarpFarmModule() {
   }

   public static int getIntValue17() {
      return intValue;
   }

   public static long method4114() {
      return atomicLong.get();
   }

   public static String method4115() {
      return INSTANCE.farmMode.get();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.stopWalking();
      this.stopWatch.setLongValue(0L);
      this.stopWatch2.reset();
      this.lastAnarchy = -32768 << 16 | 0;
      this.intValue2 = 0;
      this.flag = false;
      intValue = -1;
      this.intValue3 = 0;
      this.flag2 = false;
      this.pathFailStreak = 0;
      if (mc.options != null) {
         this.flag3 = mc.options.pauseOnLostFocus;
         this.flag4 = true;
         mc.options.pauseOnLostFocus = false;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopWalking();
      intValue = -1;
      if (this.flag4 && mc.options != null) {
         mc.options.pauseOnLostFocus = this.flag3;
      }

      this.flag4 = false;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.options != null) {
            mc.options.pauseOnLostFocus = false;
         }

         this.method4116();
         if (mc.gui.screen() instanceof DeathScreen && mc.player.deathTime > 5) {
            mc.player.respawn();
            this.stopWalking();
         } else if (mc.player.isDeadOrDying() || mc.player.getHealth() <= 0.0F) {
            this.stopWalking();
         } else if (this.scopedRCTRepository().isActive()) {
            this.stopWalking();
         } else if (this.method4117()) {
            if (this.flag) {
               this.method4121();
            } else if (this.intValue3 >= (int)this.deathLimit.getValue3()) {
               ChatLogger.method2019("WarpFarm: " + this.intValue3 + " смертей на анке, ищу другую");
               this.method4118();
            } else {
               double horizontalDistanceSq;
               if ((horizontalDistanceSq = this.horizontalDistanceSq(Vec3.atCenterOf(blockPos))) > 40000.0) {
                  this.stopWalking();
                  this.method4120();
               } else if (this.method4124()) {
                  ChatLogger.method2019("WarpFarm: посторонний игрок у центра, всем отправлен /hub");
                  this.method4119();
               } else {
                  Entity hologram = this.findHologram();
                  if (hologram != null) {
                     this.setWalkTarget(hologram.blockPosition());
                  } else {
                     if (!(horizontalDistanceSq > 4.0)) {
                        this.stopWalking();
                        return;
                     }

                     this.setWalkTarget(blockPos);
                  }

                  this.method4123();
               }
            }
         }
      }
   }

   private void method4116() {
      boolean flag;
      if ((flag = mc.player.isDeadOrDying() || mc.player.getHealth() <= 0.0F) && !this.flag2) {
         this.intValue3++;
         ChatLogger.method2019("WarpFarm: смерть " + this.intValue3 + "/" + (int)this.deathLimit.getValue3());
      }

      this.flag2 = flag;
   }

   private boolean method4117() {
      int currentAnarchyHere;
      if ((currentAnarchyHere = this.scopedRCTRepository().currentAnarchyHere()) != this.lastAnarchy) {
         this.lastAnarchy = currentAnarchyHere;
         intValue = -1;
         this.intValue3 = 0;
         this.flag2 = false;
         this.stopWalking();
         this.flag = false;
         if (currentAnarchyHere != -1) {
            mc.player.connection.sendCommand("warp pvp");
            this.stopWatch.reset();
            this.stopWatch2.reset();
            this.flag = true;
         }
      }

      if (currentAnarchyHere == -1) {
         this.method4118();
         return false;
      } else {
         return true;
      }
   }

   private void method4118() {
      this.stopWalking();
      this.flag = false;
      intValue = -1;
      this.intValue3 = 0;
      int currentAnarchyHere = this.scopedRCTRepository().currentAnarchyHere();

      int nextInt;
      do {
         while ((nextInt = ThreadLocalRandom.current().nextInt(1, 75)) == currentAnarchyHere) {
         }
      } while (nextInt == this.intValue2);

      this.intValue2 = nextInt;
      this.scopedRCTRepository().reconnect(nextInt);
   }

   private void method4119() {
      intValue = -1;
      atomicLong.incrementAndGet();
      this.stopWalking();
      mc.player.connection.sendCommand("hub");
      this.method4118();
   }

   private void method4120() {
      if (this.stopWatch.method7546(5000L)) {
         mc.player.connection.sendCommand("warp pvp");
         this.stopWatch.reset();
         this.stopWatch2.reset();
      }
   }

   private void method4121() {
      this.stopWalking();
      if (this.stopWatch2.method7546(2000L)) {
         if (this.method4124()) {
            ChatLogger.method2019("WarpFarm: посторонний игрок у центра, всем отправлен /hub");
            this.method4119();
         } else {
            Entity hologram = this.findHologram();
            if (hologram == null) {
               ChatLogger.method2019("WarpFarm: голограмма не подходит под режим «" + this.farmMode.get() + "», ищу другую анку");
               this.method4118();
            } else {
               this.flag = false;
               intValue = this.scopedRCTRepository().currentAnarchyHere();
               ChatLogger.method2019("WarpFarm: анка №" + intValue + " пустая, голограмма «" + this.farmMode.get() + "» подходит");
            }
         }
      }
   }

   @EventTarget
   public void method4122(Event5 event) {
      if (this.nextRouteBlock != null && mc.player != null) {
         Vec3 vec = Pathfinder.method761(this.nextRouteBlock);
         Vec3 eyePosition = mc.player.getEyePosition(1.0F);
         Rotation rotation = Rotation.method3128(new Vec3(vec.x, eyePosition.y, vec.z), eyePosition);
         this.scopedRotationManager().method7849(new RotationRequest(rotation, () -> {
            BcCameraTestnerv var2x = this.scopedRotationManager().getBcCameraTestnerv();
            return var2x.method1851(var2x.getRotationConfig(), rotation);
         }, this.scopedRotationManager().getBcCameraTestnerv().getRotationConfig()), 20, this);
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.nextRouteBlock != null && mc.player != null) {
         event.method3044();
         event.method3042(1.0F, 0.0F);
         event.method3039(true);
         boolean flag = mc.level != null
            && (
               mc.level.getBlockState(this.nextRouteBlock).getBlock() instanceof SlabBlock
                  || mc.level.getBlockState(this.nextRouteBlock.below()).getBlock() instanceof SlabBlock
            );
         event.method3041(!flag && this.nextRouteBlock.getY() > Mth.floor(mc.player.getY() + 0.01));
      }
   }

   private void setWalkTarget(BlockPos walkTarget) {
      if (!walkTarget.equals(this.walkTarget)) {
         this.walkTarget = walkTarget;
         this.nextRouteBlock = null;
      }
   }

   private void method4123() {
      this.nextRouteBlock = null;
      if (this.walkTarget != null) {
         data = data2;
         BlockPos blockPosition = method4126(mc.level, mc.player.position(), mc.player.blockPosition());
         Pathfinder.Entry2 entry2 = Pathfinder.method758(blockPosition, this.walkTarget, data).orElse(null);
         if (entry2 == null) {
            if (!mc.player.onGround()) {
               this.nextRouteBlock = this.walkTarget;
               this.pathFailStreak = 0;
            } else {
               if (++this.pathFailStreak >= 5) {
                  this.pathFailStreak = 0;
                  this.stopWalking();
                  this.method4120();
               }
            }
         } else {
            this.pathFailStreak = 0;
            this.nextRouteBlock = method4127(entry2, mc.player.position());
         }
      }
   }

   private void stopWalking() {
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }

   private boolean method4124() {
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos);

      for (Player player : mc.level.players()) {
         if (player != mc.player && !this.method4125(player)) {
            double d = player.getX() - atCenterOf.x;
            double d2 = player.getZ() - atCenterOf.z;
            if (d * d + d2 * d2 <= 900.0) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean method4125(Player player) {
      return HeadlessBots.get(player.getGameProfile().name()) != null;
   }

   public static BlockPos method4126(Level world, Vec3 position, BlockPos blockPosition) {
      if (Pathfinder.method762(world, blockPosition, false)) {
         return blockPosition;
      } else {
         BlockPos offset = null;
         double d = Double.MAX_VALUE;

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (i != 0 || j != 0) {
                  BlockPos pos = blockPosition.offset(i, 0, j);
                  if (Pathfinder.method762(world, pos, false)) {
                     double d2 = position.x - (pos.getX() + 0.5);
                     double d3 = position.z - (pos.getZ() + 0.5);
                     double d4;
                     if ((d4 = d2 * d2 + d3 * d3) < d) {
                        offset = pos;
                        d = d4;
                     }
                  }
               }
            }
         }

         return offset != null ? offset : blockPosition;
      }
   }

   public static BlockPos method4127(Pathfinder.Entry2 entry2, Vec3 position) {
      var items = entry2.getItems();
      if (items.size() < 2) {
         return null;
      } else {
         int n = 0;
         double d = Double.MAX_VALUE;

         for (int i = 0; i < items.size(); i++) {
            BlockPos pos = (BlockPos)items.get(i);
            double d2 = position.x - (pos.getX() + 0.5);
            double d3 = position.y - pos.getY();
            double d4 = position.z - (pos.getZ() + 0.5);
            double d5;
            if ((d5 = d2 * d2 + d3 * d3 + d4 * d4) <= d) {
               n = i;
               d = d5;
            }
         }

         int n2;
         return (n2 = n + 1) < items.size() ? (BlockPos)items.get(n2) : null;
      }
   }

   private Entity findHologram() {
      String lowerCase = this.farmMode.get().toLowerCase(Locale.ROOT);
      Entity entity = null;
      double d = 16384.0;

      for (Entity entity2 : mc.level.entitiesForRendering()) {
         double d2;
         if (entity2 != mc.player && !((d2 = mc.player.distanceToSqr(entity2)) > d)) {
            Component text = entity2 instanceof TextDisplay textDisplay ? textDisplay.getText() : entity2.getCustomName();
            if (text != null && text.getString().toLowerCase(Locale.ROOT).contains(lowerCase)) {
               entity = entity2;
               d = d2;
            }
         }
      }

      return entity;
   }

   private double horizontalDistanceSq(Vec3 atCenterOf) {
      double d = mc.player.getX() - atCenterOf.x;
      double d2 = mc.player.getZ() - atCenterOf.z;
      return d * d + d2 * d2;
   }
}
