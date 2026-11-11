package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.ItemStackTags;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "AncientFarm",
   category = ModuleCategory.PLAYER,
   description = "module.ancientfarm.description"
)
public final class AncientFarmModule extends Module {
   public static final AncientFarmModule INSTANCE = new AncientFarmModule();
   private static final int CONST_intValue = 20;
   private static final int CONST_intValue2 = 8;
   private static final int CONST_intValue3 = 4;
   private static final double CONST_doubleValue = 26.0;
   private static final int CONST_intValue4 = 45;
   private static final long CONST_longValue = 30L;
   private static final long CONST_longValue2 = 25L;
   private static final int CONST_intValue5 = 4;
   private static final int CONST_intValue6 = 80;
   private static final long CONST_longValue3 = 9000L;
   private static final double CONST_doubleValue2 = 5.0;
   private static final int CONST_intValue7 = 24;
   private static final int CONST_intValue8 = 45;
   private final ModeSetting searchMode = new ModeSetting(
      "module.ancientFarm.searchMode",
      "module.ancientFarm.searchMode.desc",
      new String[]{"module.ancientFarm.searchMode.top", "module.ancientFarm.searchMode.bottom"}
   );
   private final SliderSetting tntPerRun = new SliderSetting("module.ancientFarm.tntPerRun", 1.0F, 1.0F, 5.0F, 1.0F, "module.ancientFarm.tntPerRun.desc", "шт");
   private final SliderSetting blastsPerArea = new SliderSetting(
      "module.ancientFarm.blastsPerArea", 6.0F, 1.0F, 20.0F, 1.0F, "module.ancientFarm.blastsPerArea.desc", "шт"
   );
   private final SliderSetting rangeWeight = new SliderSetting(
      "module.ancientFarm.rangeWeight", 35.0F, 0.0F, 100.0F, 5.0F, "module.ancientFarm.rangeWeight.desc", "%"
   );
   private final SliderSetting scanRadius = new SliderSetting(
      "module.ancientFarm.scanRadius", 24.0F, 12.0F, 48.0F, 4.0F, "module.ancientFarm.scanRadius.desc", "бл"
   );
   private final BooleanSetting autoEat = new BooleanSetting("module.ancientFarm.autoEat", "module.ancientFarm.autoEat.desc", true);
   private final BooleanSetting autoFireRes = new BooleanSetting("module.ancientFarm.autoFireRes", "module.ancientFarm.autoFireRes.desc", true);
   private final SliderSetting tolerance = new SliderSetting("module.ancientFarm.tolerance", 12.0F, 3.0F, 40.0F, 1.0F, "module.ancientFarm.tolerance.desc", "°");
   private final BooleanSetting render = new BooleanSetting("module.ancientFarm.render", "module.ancientFarm.render.desc", true);
   private final BooleanSetting debug = new BooleanSetting("module.ancientFarm.debug", "module.ancientFarm.debug.desc", false, Zenith::method1502);
   private static final Predicate<ItemStack> predicate = mainHandItem -> ItemStackTags.isPickaxe(mainHandItem) && method4258(mainHandItem) > 0.05;
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "Zenith-AncientFarm");
      thread.setDaemon(true);
      return thread;
   });
   private final Set<BlockPos> items = new LinkedHashSet<>();
   private final Set<BlockPos> items2 = new HashSet<>();
   private final ConcurrentLinkedQueue<BlockPos> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private final ConcurrentLinkedQueue<BlockPos> concurrentLinkedQueue2 = new ConcurrentLinkedQueue<>();
   private final Deque<AncientFarmModule.Entry> deque = new ArrayDeque<>();
   private final AncientFarmModule.Entry2 entry2 = new AncientFarmModule.Entry2();
   private final AncientFarmModule.Entry2 entry22 = new AncientFarmModule.Entry2();
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private final StopWatch stopWatch4 = new StopWatch();
   private final StopWatch stopWatch5 = new StopWatch();
   private AncientFarmModule.Type2 type2 = AncientFarmModule.Type2.SEARCH;
   private volatile AABB box;
   private volatile AABB box2;
   private volatile boolean flag;
   private int intValue;
   private int intValue2;
   private Vec3 vec3d;
   private Vec3 vec3d2;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private boolean flag7;
   private boolean flag8;
   private boolean flag9;
   private boolean flag10;
   private Vec3 vec3d3;
   private int stuckTicks;
   private Vec3 vec3d4;
   private int intValue3;

   private AncientFarmModule() {
   }

   public void onEnable() {
      super.onEnable();
      if (this.isEnabledRaw()) {
         if (mc.player != null && mc.level != null) {
            this.reset();
         } else {
            this.setToggled(false);
         }
      }
   }

   public void onDisable() {
      super.onDisable();
      this.method4238();
      this.flag3 = false;
      this.reset();
   }

   private void reset() {
      this.flag3 = false;
      this.method4238();
      this.type2 = AncientFarmModule.Type2.SEARCH;
      this.box = null;
      this.box2 = null;
      this.intValue = 0;
      this.intValue2 = 0;
      this.vec3d = null;
      this.vec3d2 = null;
      this.blockPos = null;
      this.blockPos2 = null;
      this.items.clear();
      this.items2.clear();
      this.concurrentLinkedQueue.clear();
      this.concurrentLinkedQueue2.clear();
      this.deque.clear();
      this.flag2 = false;
      this.entry2.method4263();
      this.vec3d3 = null;
      this.stuckTicks = 0;
      this.vec3d4 = null;
      this.intValue3 = 0;
      this.entry22.method4263();
      this.stopWatch2.reset();
   }

   @EventTarget
   public void method4220(PreRotationEvent event) {
      if (mc.player != null && mc.level != null) {
         Vec3 vec3d = this.vec3d != null ? this.vec3d : this.vec3d2;
         if (vec3d != null) {
            Rotation rotation = RotationUtils.method6912(vec3d.subtract(mc.player.getEyePosition()));
            float f = this.method4221();
            Rotation rotation2 = new Rotation(rotation.getFloatValue() + f / 2.0F, Mth.clamp(rotation.getFloatValue2() + f / 4.0F, -90.0F, 90.0F));
            rotationManager.requestRotation(
               new RotationRequest(rotation2, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig3(), rotation2), bcCameraTestnerv.getRotationConfig3()),
               7,
               this
            );
         }
      }
   }

   private float method4221() {
      float f;
      return (float)(
         (
               Math.sin((f = mc.player.tickCount + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) * 0.31F) * 0.5
                  + Math.sin(f * 0.73F + 1.1F) * 0.3
                  + Math.sin(f * 1.7F + 2.6F) * 0.2
            )
            * 8.0
      );
   }

   private boolean method4222(Vec3 vec3d2) {
      Rotation rotation = RotationUtils.method6912(vec3d2.subtract(mc.player.getEyePosition()));
      return rotationManager.getEffectiveRotation().isWithinAngle(rotation, this.tolerance.getValue3());
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         this.blockPos = null;
         this.vec3d2 = null;
         this.method4230();
         this.method4231();
         this.method4233();
         this.method4232();
         AncientFarmModule.Type3 type3 = this.method4260();
         if (type3 == null || type3 == AncientFarmModule.Type3.TNT && this.type2 != AncientFarmModule.Type2.SEARCH) {
            this.method4239();
            this.method4238();
            this.flag3 = true;
            this.vec3d = null;
            if (mc.gui.screen() == null && !this.method4240()) {
               switch (this.type2) {
                  case SEARCH:
                     this.method4223();
                     break;
                  case TNT:
                     this.method4224();
                     break;
                  case RETREAT:
                     this.method4227();
                     break;
                  case MINE:
                     this.method4228();
               }
            } else {
               this.method4237();
            }
         } else {
            if (this.stopWatch5.hasElapsedAndReset(10000L)) {
               ChatLogger.method2019("AncientFarm: " + type3.description());
            }

            this.setToggled(false);
         }
      }
   }

   private void method4223() {
      if (!this.items.isEmpty()) {
         ChatLogger.method2019("AncientFarm: вскапываем обломки, найденные по пути");
         this.entry22.method4263();
         this.type2 = AncientFarmModule.Type2.MINE;
      } else if (this.box == null) {
         this.method4236();
         if (!this.flag && mc.player.tickCount > 20) {
            this.flag = true;
            ClientLevel level = mc.level;
            this.executorService.execute(() -> {});
         }
      } else {
         BlockPos containing = BlockPos.containing(this.box.getCenter());
         BlockPos pos = this.method4255(containing);
         Vec3 atCenterOf = Vec3.atCenterOf(pos != null ? pos : containing);
         double d;
         boolean flag = (d = Math.sqrt(mc.player.position().distanceToSqr(atCenterOf))) <= 25.0;
         if (!(d <= 5.0) && (!flag || this.stuckTicks < 24)) {
            if (mc.player.tickCount % 40 == 0) {
               this.method4259(
                  "к территории %.1f бл | полёт %s | земля %s | ввод F%s B%s L%s R%s | v=%.2f",
                  d,
                  mc.player.getAbilities().flying ? "да" : "НЕТ",
                  mc.player.onGround() ? "да" : "нет",
                  this.flag4 ? "+" : "-",
                  this.flag5 ? "+" : "-",
                  this.flag6 ? "+" : "-",
                  this.flag7 ? "+" : "-",
                  mc.player.getDeltaMovement().horizontalDistance()
               );
            }

            this.method4235(atCenterOf);
         } else {
            this.stuckTicks = 0;
            this.vec3d3 = null;
            this.intValue = 0;
            this.method4259("прилетели к территории, дистанция %.1f", d);
            this.type2 = AncientFarmModule.Type2.TNT;
         }
      }
   }

   private void method4224() {
      double blockInteractionRange = mc.player.blockInteractionRange();
      List<PrimedTnt> primedTnts = this.method4250(10.0);
      boolean anyMatch = primedTnts.stream().anyMatch(primedTnt -> primedTnt.getFuse() <= 45);
      BlockPos blockPos = java.util.stream.StreamSupport.stream(BlockPos.withinManhattan(mc.player.blockPosition(), (int)blockInteractionRange).spliterator(), false)
         .filter(pos -> mc.level.getBlockState(pos).is(Blocks.TNT))
         .map(BlockPos::immutable)
         .findFirst()
         .orElse(null);
      if (blockPos == null && (this.intValue >= this.tntPerRun.getValue3() || anyMatch)) {
         if (primedTnts.isEmpty()) {
            this.intValue = 0;
            this.box = null;
            this.type2 = AncientFarmModule.Type2.SEARCH;
         } else {
            this.type2 = AncientFarmModule.Type2.RETREAT;
         }
      } else if (anyMatch && blockPos != null) {
         this.type2 = AncientFarmModule.Type2.RETREAT;
      } else if (blockPos != null && this.intValue >= this.tntPerRun.getValue3()) {
         this.method4225(blockPos, blockInteractionRange);
      } else {
         this.method4226(blockInteractionRange);
      }
   }

   private void method4225(BlockPos blockPos, double blockInteractionRange) {
      this.blockPos = blockPos;
      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos);
      Direction direction = null;
      Vec3 vec = null;
      double d = Double.MAX_VALUE;

      for (Direction direction2 : Direction.values()) {
         Vec3 vec2 = atCenterOf.add(direction2.getStepX() * 0.45, direction2.getStepY() * 0.45, direction2.getStepZ() * 0.45);
         if (!(eyePosition.distanceTo(vec2) > blockInteractionRange)) {
            BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, vec2, Block.COLLIDER, Fluid.NONE, mc.player));
            double d2;
            if (clip.getType() == HitResult.Type.BLOCK && clip.getBlockPos().equals(blockPos) && (d2 = eyePosition.distanceToSqr(vec2)) < d) {
               d = d2;
               vec = vec2;
               direction = direction2;
            }
         }
      }

      if (vec == null) {
         this.method4235(this.method4254(blockPos));
      } else {
         this.method4238();
         this.vec3d = null;
         this.vec3d2 = vec;
         if (this.method4256(stack -> stack.is(Items.FLINT_AND_STEEL))) {
            if (this.method4222(vec) && this.stopWatch3.hasElapsedAndReset(250L)) {
               UseItemUtils.method6514(new BlockHitResult(vec, direction, blockPos, false), InteractionHand.MAIN_HAND);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   private void method4226(double blockInteractionRange) {
      Vec3 eyePosition = mc.player.getEyePosition();
      BlockPos blockPos = java.util.stream.StreamSupport.stream(BlockPos.withinManhattan(mc.player.blockPosition(), (int)blockInteractionRange).spliterator(), false)
         .filter(pos -> mc.level.getBlockState(pos).canBeReplaced())
         .filter(var1x -> this.method4251(var1x.below()))
         .filter(pos -> !mc.player.getBoundingBox().expandTowards(mc.player.getDeltaMovement()).inflate(0.1).intersects(new AABB(pos)))
         .filter(var4x -> this.method4252(var4x, eyePosition, blockInteractionRange))
         .map(BlockPos::immutable)
         .min(Comparator.comparingDouble(var1x -> eyePosition.distanceToSqr(Vec3.atCenterOf(var1x))))
         .orElse(null);
      if (blockPos == null) {
         Vec3 position = mc.player.position();
         BlockPos immutable = null;
         double d = Double.MAX_VALUE;

         for (BlockPos below : BlockPos.withinManhattan(mc.player.blockPosition(), 16)) {
            if (this.method4251(below)) {
               double d2 = below.getX() + 0.5 - position.x;
               double d3 = below.getY() + 0.5 - position.y;
               double d4 = below.getZ() + 0.5 - position.z;
               double d5;
               if ((d5 = d2 * d2 + d3 * d3 + d4 * d4) < d) {
                  d = d5;
                  immutable = below.immutable();
               }
            }
         }

         if (immutable != null && this.stuckTicks < 24) {
            this.method4235(Vec3.atCenterOf(immutable.above(2)));
         } else {
            this.box = null;
            this.intValue = 0;
            this.type2 = AncientFarmModule.Type2.SEARCH;
         }
      } else {
         this.blockPos = blockPos;
         BlockPos below = blockPos.below();
         Vec3 vec = new Vec3(below.getX() + 0.5, below.getY() + 1, below.getZ() + 0.5);
         this.method4238();
         this.vec3d = null;
         this.vec3d2 = vec;
         if (this.method4256(stack -> stack.is(Items.TNT))) {
            if (this.method4222(vec) && this.stopWatch3.hasElapsedAndReset(250L)) {
               UseItemUtils.method6514(new BlockHitResult(vec, Direction.UP, below, false), InteractionHand.MAIN_HAND);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
               this.intValue++;
               this.intValue2++;
            }
         }
      }
   }

   private void method4227() {
      List<PrimedTnt> primedTnts = this.method4250(34.0);
      if (primedTnts.isEmpty()) {
         this.method4259("заряд %d/%d отработал", this.intValue2, (int)this.blastsPerArea.getValue3());
         this.method4238();
         this.vec3d = null;
         this.intValue = 0;
         this.entry22.method4263();
         this.type2 = AncientFarmModule.Type2.MINE;
      } else {
         Vec3 vec = Vec3.ZERO;

         for (PrimedTnt primedTnt : primedTnts) {
            vec = vec.add(mc.player.position().subtract(primedTnt.position()));
         }

         if (vec.lengthSqr() < 1.0E-4) {
            vec = new Vec3(1.0, 0.0, 0.0);
         }

         Vec3 vec2 = vec.normalize();
         this.method4235(mc.player.position().add(vec2.x * 26.0, 6.0, vec2.z * 26.0));
      }
   }

   private void method4228() {
      this.entry22.tick();
      if (!this.entry22.method4265(30L, 25L)) {
         this.method4236();
      } else {
         BlockPos blockPos2 = this.items
            .stream()
            .min(Comparator.comparingDouble(pos -> mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos))))
            .orElse(null);
         if (blockPos2 == null) {
            this.method4238();
            this.vec3d = null;
            this.blockPos2 = null;
            if (this.box != null && this.intValue2 < this.blastsPerArea.getValue3()) {
               this.intValue = 0;
               this.type2 = AncientFarmModule.Type2.TNT;
            } else {
               this.method4259("территория выработана за %d зарядов, ищем новую", this.intValue2);
               this.intValue2 = 0;
               this.box = null;
               this.type2 = AncientFarmModule.Type2.SEARCH;
            }
         } else {
            if (!blockPos2.equals(this.blockPos2)) {
               this.blockPos2 = blockPos2;
               this.stopWatch2.reset();
            } else if (this.stopWatch2.hasElapsed(9000L)) {
               this.items2.add(blockPos2);
               this.items.remove(blockPos2);
               this.blockPos2 = null;
               return;
            }

            this.blockPos = blockPos2;
            double blockInteractionRange = mc.player.blockInteractionRange();
            BlockHitResult blockHitResult = this.method4253(blockPos2, blockInteractionRange);
            if (blockHitResult == null) {
               this.method4235(this.method4254(blockPos2));
            } else {
               this.method4238();
               this.vec3d = null;
               this.vec3d2 = blockHitResult.getLocation();
               if (this.method4256(predicate)) {
                  if (this.method4222(this.vec3d2)) {
                     mc.gameMode.continueDestroyBlock(blockPos2, blockHitResult.getDirection());
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }

   private AABB method4229(ClientLevel level) {
      int n = (int)((Math.sqrt(level.getChunkSource().getLoadedChunksCount()) - 1.0) / 2.0) * 16;
      BlockPos blockPosition = mc.player.blockPosition();
      WorldBorder worldBorder = level.getWorldBorder();
      boolean flag = this.searchMode.method2101("module.ancientFarm.searchMode.top");
      double d = this.rangeWeight.getValue3() / 100.0;
      int n2 = flag ? 90 : 20;
      int n3 = flag ? 118 : 60;
      AABB aabb = null;
      double d2 = -1.0;
      double d3 = 0.0;
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = blockPosition.getX() - n; i <= blockPosition.getX() + n; i += 8) {
         for (int j = blockPosition.getZ() - n; j <= blockPosition.getZ() + n; j += 8) {
            if (level.hasChunk(i >> 4, j >> 4) && worldBorder.isWithinBounds(i - 20, j - 20) && worldBorder.isWithinBounds(i + 20, j + 20)) {
               for (int k = n2; k <= n3; k += 8) {
                  int n4 = 0;
                  int n5 = 0;
                  boolean flag2 = false;

                  for (byte m = -20; m <= 20 && !flag2; m += 4) {
                     for (byte b = -20; b <= 20 && !flag2; b += 4) {
                        for (byte i2 = -20; i2 <= 20; i2 += 4) {
                           mutablePos.set(i + m, k + b, j + i2);
                           Holder biome = level.getBiome(mutablePos);
                           if (biome.is(Biomes.BASALT_DELTAS) || biome.is(Biomes.WARPED_FOREST)) {
                              flag2 = true;
                              break;
                           }

                           n5++;
                           BlockState blockState = level.getBlockState(mutablePos);
                           if (!blockState.isAir() && blockState.getFluidState().isEmpty()) {
                              n4++;
                           }
                        }
                     }
                  }

                  if (!flag2 && n5 != 0) {
                     double d4 = (double)n4 / n5;
                     double d5 = Math.sqrt(blockPosition.distToLowCornerSqr(i, k, j));
                     double d6 = n > 0 ? Math.min(1.0, d5 / n) * d : 0.0;
                     double d7;
                     if (!((d7 = d4 - d6) <= d2)) {
                        BlockPos pos = java.util.stream.StreamSupport.stream(BlockPos.withinManhattan(new BlockPos(i, k, j), 20).spliterator(), false).filter(var1x -> {
                           BlockState var2x = level.getBlockState(var1x);
                           return !var2x.isAir() && var2x.getFluidState().isEmpty();
                        }).map(BlockPos::immutable).findFirst().orElse(null);
                        if (pos != null) {
                           d2 = d7;
                           d3 = d4;
                           aabb = new AABB(pos.getX() - 20, pos.getY() - 20, pos.getZ() - 20, pos.getX() + 20, pos.getY() + 20, pos.getZ() + 20);
                        }
                     }
                  }
               }
            }
         }
      }

      if (aabb != null && !aabb.equals(this.box2)) {
         this.box2 = aabb;
         long l = Math.round(d3 * 100.0);
         long l2 = Math.round(Math.sqrt(blockPosition.distToCenterSqr(aabb.getCenter())));
         String status = "AncientFarm: нашли территорию, плотность " + l + "%, до неё " + l2 + " бл";
         mc.execute(() -> ChatLogger.method2019(status));
      }

      return aabb;
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         if (event.getPacket() instanceof ClientboundSectionBlocksUpdatePacket blocksUpdatePacket) {
            blocksUpdatePacket.runUpdates((var1x, var2x) -> {
               if (var2x.is(Blocks.ANCIENT_DEBRIS)) {
                  this.concurrentLinkedQueue.add(var1x.immutable());
               } else {
                  this.concurrentLinkedQueue2.add(var1x.immutable());
               }
            });
         } else {
            if (event.getPacket() instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
               if (blockUpdatePacket.getBlockState().is(Blocks.ANCIENT_DEBRIS)) {
                  this.concurrentLinkedQueue.add(blockUpdatePacket.getPos().immutable());
               } else {
                  this.concurrentLinkedQueue2.add(blockUpdatePacket.getPos().immutable());
               }
            }
         }
      }
   }

   private void method4230() {
      BlockPos pos;
      while ((pos = this.concurrentLinkedQueue2.poll()) != null) {
         this.items.remove(pos);
      }

      while ((pos = this.concurrentLinkedQueue.poll()) != null) {
         if (!this.items2.contains(pos) && this.items.add(pos)) {
            this.flag2 = true;
            this.entry2.method4263();
         }
      }
   }

   private void method4231() {
      this.items
         .removeIf(
            pos -> mc.level.getBlockState(pos).isAir()
               || pos.distToCenterSqr(mc.player.position()) >= 6400.0
               || !mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)
         );
   }

   private void method4232() {
      this.entry2.tick();
      if (this.flag2 && this.entry2.method4264(5L)) {
         this.flag2 = false;
         ChatLogger.method2019("AncientFarm: обнаружено " + this.items.size() + " древних обломков");
         zenith.getHudHolder().method6972(this.getCategory().getIcon(), Component.nullToEmpty("AncientFarm: обломков — " + this.items.size()));
      }
   }

   private void method4233() {
      if (this.stopWatch.hasElapsedAndReset(500L)) {
         int n = (int)this.scanRadius.getValue3();
         BlockPos containing = this.box != null && this.type2 != AncientFarmModule.Type2.SEARCH ? BlockPos.containing(this.box.getCenter()) : mc.player.blockPosition();
         MutableBlockPos mutablePos = new MutableBlockPos();
         int n2 = Math.max(containing.getY() - n, mc.level.getMinY());
         int n3 = Math.min(containing.getY() + n, mc.level.getMaxY());

         for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
               int x = containing.getX() + i;
               int z = containing.getZ() + j;
               if (mc.level.hasChunk(x >> 4, z >> 4)) {
                  LevelChunk chunk = mc.level.getChunk(x >> 4, z >> 4);
                  int n4 = -32768 << 16 | 0;

                  for (int k = n2; k <= n3; k++) {
                     if (k > n4) {
                        n4 = (k & -16) + 15;
                        int sectionIndex;
                        LevelChunkSection section = (sectionIndex = chunk.getSectionIndex(k)) >= 0 && sectionIndex < chunk.getSections().length
                           ? chunk.getSection(sectionIndex)
                           : null;
                        if (section != null && (section.hasOnlyAir() || !section.maybeHas(blockState -> blockState.is(Blocks.ANCIENT_DEBRIS)))) {
                           k = n4;
                           continue;
                        }
                     }

                     mutablePos.set(x, k, z);
                     if (mc.level.getBlockState(mutablePos).is(Blocks.ANCIENT_DEBRIS)) {
                        BlockPos immutable = mutablePos.immutable();
                        if (!this.items2.contains(immutable) && this.method4234(immutable) && this.items.add(immutable)) {
                           this.flag2 = true;
                           this.entry2.method4263();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean method4234(BlockPos immutable) {
      for (Direction direction : Direction.values()) {
         BlockPos relative = immutable.relative(direction);
         BlockState blockState = mc.level.getBlockState(relative);
         if (blockState.isAir() || !blockState.getFluidState().isEmpty() || !this.method4251(relative)) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (mc.player != null && this.flag3) {
         event.setPlayerInput(new Input(this.flag4, this.flag5, this.flag6, this.flag7, this.flag8, this.flag9, this.flag10));
      }
   }

   private void method4235(Vec3 vec3d) {
      this.vec3d = vec3d;
      Vec3 vec3d4 = this.vec3d4 != null ? this.vec3d4 : vec3d;
      this.flag3 = true;
      double x = vec3d4.x - mc.player.getX();
      double y = vec3d4.z - mc.player.getZ();
      double d;
      if ((d = Math.sqrt(x * x + y * y)) > 0.75) {
         float f = Mth.wrapDegrees((float)Math.toDegrees(Math.atan2(y, x)) - 90.0F - mc.player.getYRot());
         this.flag4 = f > -67.5F && f < 67.5F;
         this.flag5 = f > 112.5F || f < -112.5F;
         this.flag6 = f < -22.5F && f > -157.5F;
         this.flag7 = f > 22.5F && f < 157.5F;
         this.flag10 = d > 6.0 && f > -30.0F && f < 30.0F;
      } else {
         this.flag4 = this.flag5 = this.flag6 = this.flag7 = this.flag10 = false;
      }

      double d2 = vec3d4.y - mc.player.getY();
      this.flag8 = d2 > 0.4;
      this.flag9 = d2 < -0.4;
      this.method4237();
   }

   private void method4236() {
      this.vec3d = null;
      this.method4238();
      this.method4237();
   }

   private void method4237() {
      Abilities abilities = mc.player.getAbilities();
      if (abilities.mayfly) {
         if (mc.player.onGround()) {
            this.flag8 = true;
         } else {
            if (!abilities.flying) {
               abilities.flying = true;
               mc.player.connection.send(new ServerboundPlayerAbilitiesPacket(abilities));
            }
         }
      }
   }

   private void method4238() {
      this.flag4 = this.flag5 = this.flag6 = this.flag7 = this.flag8 = this.flag9 = this.flag10 = false;
   }

   private void method4239() {
      if (this.intValue3 > 0) {
         this.intValue3--;
         if (this.intValue3 == 0) {
            this.vec3d4 = null;
            this.stuckTicks = 0;
            this.vec3d3 = null;
         }
      } else if (this.vec3d == null) {
         this.stuckTicks = 0;
         this.vec3d3 = null;
      } else if (this.vec3d3 != null && !(mc.player.position().distanceToSqr(this.vec3d3) > 1.0)) {
         this.stuckTicks++;
         if (this.stuckTicks >= 24) {
            this.vec3d4 = mc.player.position().add(0.0, 10.0, 0.0);
            this.intValue3 = 45;
            this.vec3d3 = null;
         }
      } else {
         this.vec3d3 = mc.player.position();
         this.stuckTicks = 0;
      }
   }

   private boolean method4240() {
      if (this.deque.isEmpty()) {
         if (this.autoEat.isEnabled() && this.method4243() && mc.player.canEat(false)) {
            this.method4241(AncientFarmModule.Type.FOOD, AncientFarmModule::method4248);
         }

         if (this.deque.isEmpty() && this.autoFireRes.isEnabled() && this.method4247()) {
            this.method4241(AncientFarmModule.Type.FIRE_RESISTANCE, AncientFarmModule::method4249);
         }
      }

      if (this.deque.isEmpty()) {
         return false;
      } else if (mc.player.tickCount <= 40) {
         return true;
      } else {
         this.method4244(this.deque.peek());
         return true;
      }
   }

   private void method4241(AncientFarmModule.Type type, Predicate<ItemStack> predicate) {
      int intValue = -1;

      for (int i = 0; i < 36; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            intValue = i;
            break;
         }
      }

      if (intValue != -1) {
         this.deque.add(new AncientFarmModule.Entry(type, intValue, mc.player.getInventory().getSelectedSlot()));
      }
   }

   private boolean method4242(AncientFarmModule.Type type) {
      return switch (type) {
         case FOOD -> this.method4243();
         case FIRE_RESISTANCE -> this.method4247();
      };
   }

   private boolean method4243() {
      return mc.player.getFoodData().getFoodLevel() <= 17;
   }

   private void method4244(AncientFarmModule.Entry entry) {
      entry.ticks++;
      if (entry.ticks == 1) {
         if (entry.method4262()) {
            this.method4246(entry.intValue, entry.intValue2);
         } else {
            this.method4257(entry.intValue);
         }
      } else {
         if (!entry.flag) {
            if (mc.player.isUsingItem()) {
               entry.flag = true;
            } else {
               if (!this.method4242(entry.type)) {
                  this.method4245(entry);
                  return;
               }

               if (entry.ticks % 4 == 0) {
                  UseItemUtils.useItem(InteractionHand.MAIN_HAND);
               }
            }
         } else if (!mc.player.isUsingItem()) {
            this.method4245(entry);
            return;
         }

         if (entry.ticks >= 80) {
            if (this.stopWatch4.hasElapsedAndReset(15000L)) {
               ChatLogger.method2019("AncientFarm: применить предмет не удалось, пропускаем");
            }

            this.method4245(entry);
         }
      }
   }

   private void method4245(AncientFarmModule.Entry entry) {
      if (entry.method4262()) {
         this.method4246(entry.intValue, entry.intValue2);
      } else {
         this.method4257(entry.intValue2);
      }

      this.deque.poll();
   }

   private void method4246(int intValue, int buttonNum) {
      mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, intValue, buttonNum, ContainerInput.SWAP, mc.player);
      this.method4257(buttonNum);
   }

   private boolean method4247() {
      return !mc.player.hasEffect(MobEffects.FIRE_RESISTANCE) || mc.player.getEffect(MobEffects.FIRE_RESISTANCE).getDuration() <= 100;
   }

   private static boolean method4248(ItemStack stack) {
      return stack.has(DataComponents.FOOD) && stack.getUseAnimation() == ItemUseAnimation.EAT;
   }

   private static boolean method4249(ItemStack stack) {
      return StreamSupport.stream(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects().spliterator(), false)
         .anyMatch(var0x -> var0x.getEffect() == MobEffects.FIRE_RESISTANCE);
   }

   private List<PrimedTnt> method4250(double amountToAddInAllDirections) {
      return mc.level.getEntitiesOfClass(PrimedTnt.class, mc.player.getBoundingBox().inflate(amountToAddInAllDirections), primedTnt -> true);
   }

   private boolean method4251(BlockPos below) {
      return mc.level.getBlockState(below).isRedstoneConductor(mc.level, below);
   }

   private boolean method4252(BlockPos pos, Vec3 eyePosition, double blockInteractionRange) {
      Vec3 vec = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
      if (!(eyePosition.distanceTo(vec) > blockInteractionRange) && !(eyePosition.subtract(vec).normalize().y <= 0.0)) {
         BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, vec, Block.COLLIDER, Fluid.NONE, mc.player));
         return clip.getType() != HitResult.Type.BLOCK || clip.getBlockPos().equals(pos.below());
      } else {
         return false;
      }
   }

   private BlockHitResult method4253(BlockPos blockPos2, double blockInteractionRange) {
      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos2);
      BlockHitResult blockHitResult = null;
      double d = Double.MAX_VALUE;

      for (Direction direction : Direction.values()) {
         Vec3 vec = atCenterOf.add(direction.getStepX() * 0.48, direction.getStepY() * 0.48, direction.getStepZ() * 0.48);
         if (!(eyePosition.distanceTo(vec) > blockInteractionRange)) {
            BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, vec, Block.COLLIDER, Fluid.NONE, mc.player));
            double d2;
            if (clip.getType() == HitResult.Type.BLOCK && clip.getBlockPos().equals(blockPos2) && (d2 = eyePosition.distanceToSqr(vec)) < d) {
               d = d2;
               blockHitResult = new BlockHitResult(vec, direction, blockPos2, false);
            }
         }
      }

      return blockHitResult;
   }

   private Vec3 method4254(BlockPos blockPos) {
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos);
      Vec3 vec = mc.player.position().subtract(atCenterOf);
      if (vec.lengthSqr() < 1.0E-4) {
         vec = new Vec3(0.0, 1.0, 0.0);
      }

      return atCenterOf.add(vec.normalize().scale(3.0)).add(0.0, 0.5, 0.0);
   }

   private BlockPos method4255(BlockPos containing) {
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (BlockPos below : BlockPos.withinManhattan(containing, 14)) {
         if (!this.method4251(below)) {
            mutablePos.set(below.getX(), below.getY() + 1, below.getZ());
            if (!this.method4251(mutablePos) && mc.level.getBlockState(below).getFluidState().isEmpty()) {
               return below.immutable();
            }
         }
      }

      return null;
   }

   private boolean method4256(Predicate<ItemStack> predicate) {
      if (predicate.test(mc.player.getMainHandItem())) {
         return true;
      } else {
         int intValue = -1;

         for (int i = 0; i < 36; i++) {
            if (predicate.test(mc.player.getInventory().getItem(i))) {
               intValue = i;
               break;
            }
         }

         if (intValue < 0) {
            return false;
         } else if (intValue < 9) {
            this.method4257(intValue);
            return predicate.test(mc.player.getMainHandItem());
         } else {
            if (this.stopWatch3.hasElapsedAndReset(250L)) {
               mc.gameMode
                  .handleContainerInput(mc.player.inventoryMenu.containerId, intValue, mc.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, mc.player);
            }

            return false;
         }
      }
   }

   private void method4257(int intValue) {
      if (intValue >= 0 && intValue <= 8 && mc.player.getInventory().getSelectedSlot() != intValue) {
         mc.player.getInventory().setSelectedSlot(intValue);
         mc.player.connection.send(new ServerboundSetCarriedItemPacket(intValue));
      }
   }

   private static double method4258(ItemStack mainHandItem) {
      return mainHandItem.isDamageableItem() && mainHandItem.getMaxDamage() > 0 ? 1.0 - (double)mainHandItem.getDamageValue() / mainHandItem.getMaxDamage() : 1.0;
   }

   private void method4259(String format, Object... objects) {
      if (Zenith.method1502() && this.debug.isEnabled()) {
         ChatLogger.method2019("AncientFarm: " + String.format(format, objects));
      }
   }

   private AncientFarmModule.Type3 method4260() {
      for (AncientFarmModule.Type3 type3 : AncientFarmModule.Type3.values()) {
         if (!type3.method4267()) {
            return type3;
         }
      }

      return null;
   }

   @EventTarget
   public void method4261(WorldRenderEvent event) {
      if (this.render.isEnabled() && mc.player != null && mc.level != null) {
         for (BlockPos pos : List.copyOf(this.items)) {
            GlowUtil.method2230(new AABB(pos), new Color(0, 190, 255).getRGB(), 1.0F);
         }

         if (this.blockPos != null) {
            GlowUtil.method2230(new AABB(this.blockPos), new Color(230, 90, 70).getRGB(), 1.5F);
         }
      }
   }

   private static final class Entry {
      private final AncientFarmModule.Type type;
      private final int intValue;
      private final int intValue2;
      private boolean flag;
      private int ticks;

      private Entry(AncientFarmModule.Type type, int intValue, int selectedSlot) {
         this.type = type;
         this.intValue = intValue;
         this.intValue2 = selectedSlot;
      }

      private boolean method4262() {
         return this.intValue > 8;
      }
   }

   private static final class Entry2 {
      private long longValue;
      private long longValue2;

      private Entry2() {
         this.method4263();
      }

      private void method4263() {
         this.longValue = 0L;
         this.longValue2 = (long)(Math.random() * 9.223372E18F);
      }

      private void tick() {
         this.longValue++;
      }

      private boolean method4264(long l) {
         return this.longValue >= l;
      }

      private boolean method4265(long l, long l2) {
         return this.longValue >= l + this.longValue2 % (l2 + 1L);
      }
   }

   private static enum Type {
      FOOD,
      FIRE_RESISTANCE;
   }

   private static enum Type2 {
      SEARCH,
      TNT,
      RETREAT,
      MINE;
   }

   private static enum Type3 implements MinecraftAccess {
      FLY("нужен режим полёта (/fly)", () -> mc.player.getAbilities().mayfly),
      NETHER("работает только в Незере", () -> mc.level.dimension() == Level.NETHER),
      FOOD(
         "в хотбаре должна быть еда",
         () -> !AncientFarmModule.INSTANCE.autoEat.isEnabled() || !AncientFarmModule.INSTANCE.method4243() || method4266(AncientFarmModule::method4248)
      ),
      TNT("в хотбаре должен быть динамит", stack -> stack.is(Items.TNT)),
      FLINT_AND_STEEL("в хотбаре должно быть огниво", stack -> stack.is(Items.FLINT_AND_STEEL)),
      PICKAXE("в хотбаре должна быть кирка с прочностью больше 5%", AncientFarmModule.predicate),
      FIRE_RESISTANCE(
         "в хотбаре должно быть зелье огнестойкости",
         () -> !AncientFarmModule.INSTANCE.autoFireRes.isEnabled() || !AncientFarmModule.INSTANCE.method4247() || method4266(AncientFarmModule::method4249)
      );

      private final String text;
      private final BooleanSupplier booleanSupplier;

      private Type3(String text, BooleanSupplier booleanSupplier) {
         this.text = text;
         this.booleanSupplier = booleanSupplier;
      }

      private Type3(String text, Predicate<ItemStack> predicate) {
         this(text, () -> method4266(predicate));
      }

      private static boolean method4266(Predicate<ItemStack> predicate) {
         return IntStream.range(0, 9).anyMatch(slot -> predicate.test(mc.player.getInventory().getItem(slot)));
      }

      public String description() {
         return this.text;
      }

      public boolean method4267() {
         return this.booleanSupplier.getAsBoolean();
      }
   }

   private void l1I1IlllIlI(net.minecraft.client.multiplayer.ClientLevel level) {
      net.minecraft.world.phys.AABB box = null;

      try {
         box = this.method4229(level);
      } finally {
         if (mc.level == level) {
            this.box = box;
         }

         this.flag = false;
      }
   }
}
