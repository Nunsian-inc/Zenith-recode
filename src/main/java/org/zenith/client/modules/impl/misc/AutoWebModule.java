package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.InteractionInputEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.combat.AutoTrapModule;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoWeb",
   description = "module.autoweb.description",
   category = ModuleCategory.MISC
)
public final class AutoWebModule extends Module {
   public static final AutoWebModule INSTANCE = new AutoWebModule();
   private final SliderSetting distance = new SliderSetting("module.autoWeb.distance", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoWeb.distance.desc", "b");
   private final SliderSetting searchXZ = new SliderSetting("module.autoWeb.searchXZ", 4.0F, 0.0F, 6.0F, 0.1F, "module.autoWeb.searchXZ.desc", "b");
   private final SliderSetting search = new SliderSetting("module.autoWeb.search", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoWeb.search.desc", "b");
   private final ModeSetting hand = new ModeSetting("module.autoWeb.hand", "module.autoWeb.hand.desc", "module.autoWeb.rightHand", "module.autoWeb.leftHand");
   private Player playerEntity;
   private Slot slot = null;
   private Rotation rotation = null;
   private Rotation rotation2 = null;
   private AutoWebModule.Handler handler = null;
   private StopWatch stopWatch = new StopWatch();
   public List<BlockPos> list;
   private Set<BlockPos> items = Set.of();

   private AutoWebModule() {
   }

   @Override
   public void onEnable() {
      if (UseItemUtils.method6526()) {
         this.setEnabled(false);
      } else {
         super.onEnable();
      }
   }

   @EventTarget
   public void method278(InteractionInputEvent event) {
      if (mc.player != null && mc.level != null) {
         if (!AutoTrapModule.INSTANCE.isEnabled() || !AutoTrapModule.INSTANCE.method2427()) {
            if (!event.isCancelled() && mc.player.tickCount % 2 == 0) {
               BlockHitResult rotate = this.handler != null ? this.handler.isRotate(Zenith.getInstance().getRotationManager().getEffectiveRotation()) : null;
               if (rotate != null && mc.player.getItemInHand(this.hand.isSelected(0) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).getItem() == Items.COBWEB) {
                  UseItemUtils.method6514(rotate, this.hand.isSelected(0) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                  GlowUtil.method2230(new AABB(rotate.getBlockPos()), -1, 1.0F);
                  this.rotation = null;
                  this.handler = null;
               }
            }
         }
      }
   }

   @EventTarget
   public void method279(PreRotationEvent event) {
      if (!AutoTrapModule.INSTANCE.isEnabled() || !AutoTrapModule.INSTANCE.method2427()) {
         this.playerEntity = this.method284();
         if (this.playerEntity != null) {
            this.method280();
            Rotation rotation = this.rotation;
            if (rotation != null) {
               rotationManager.requestRotation(
                  new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
                  5,
                  this
               );
            }
         } else {
            this.method281();
            if (KeyBindUtils.method2630(AutoWebModule.class) && (KeyBindUtils.method2630(AutoTotemModule.class) || this.hand.isSelected(0))) {
               this.method282();
            }
         }
      }
   }

   public void method280() {
      if (!AutoTrapModule.INSTANCE.isEnabled() || !AutoTrapModule.INSTANCE.method2427()) {
         this.method281();

         try {
            Slot slot = InventoryUtils.method909(mc.player.inventoryMenu, slot2 -> slot2.getItem().getItem() == Items.COBWEB);
            byte b = 0;
            if (slot != null) {
               BlockPos blockPosition = this.playerEntity.blockPosition();
               AABB inflate = this.playerEntity.getBoundingBox().inflate(this.searchXZ.getValue3(), this.search.getValue3(), this.searchXZ.getValue3());
               this.list = UseItemUtils.method6506(blockPosition, (int)Math.ceil(this.searchXZ.getValue3()), (int)Math.ceil(this.search.getValue3()))
                  .stream()
                  .filter(var1x -> {
                     BlockState var2x = mc.level.getBlockState(var1x);
                     return var2x.isAir() && inflate.intersects(var1x.getX(), var1x.getY(), var1x.getZ(), var1x.getX() + 1, var1x.getY() + 1, var1x.getZ() + 1);
                  })
                  .sorted(Comparator.comparingDouble(var1x -> var1x.distSqr(blockPosition)))
                  .toList();
               this.items = new HashSet<>(this.list);
               Vec3 eyePosition = mc.player.getEyePosition(1.0F);

               label84:
               for (BlockPos pos : this.list) {
                  BlockState blockState = mc.level.getBlockState(pos);
                  if (blockState.canBeReplaced() && !blockState.isAir()) {
                     Rotation rotation = RotationUtils.method6912(new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F));
                     BlockHitResult blockHitResult = TargetUtils.method3216(eyePosition, rotation, 3.0, var1x -> var1x != null && this.items.contains(var1x.getBlockPos()));
                     if (blockHitResult != null && blockHitResult.getType() != Type.MISS) {
                        this.handler = var1x -> {
                           BlockHitResult var2x = TargetUtils.method3216(
                              mc.player.getEyePosition(1.0F),
                              var1x,
                              this.distance.getValue3(),
                              var1xx -> var1xx != null
                                 && !mc.level.getBlockState(var1xx.getBlockPos()).canBeReplaced()
                                 && this.items.contains(var1xx.getBlockPos().relative(var1xx.getDirection()))
                           );
                           return var2x != null && var2x.getType() != Type.MISS ? var2x : null;
                        };
                        this.rotation = rotation;
                     }
                  } else {
                     Direction[] values = Direction.values();
                     int length = values.length;
                     int n = 0;

                     while (true) {
                        if (n < length) {
                           Direction direction = values[n];
                           Vec3 location = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(direction.getUnitVec3i()).scale(0.5));
                           Rotation rotation = RotationUtils.method6913(location);
                           BlockHitResult blockHitResult = TargetUtils.method3216(
                              eyePosition,
                              rotation,
                              this.distance.getValue3(),
                              var1x -> var1x != null
                                 && !mc.level.getBlockState(var1x.getBlockPos()).canBeReplaced()
                                 && this.items.contains(var1x.getBlockPos().relative(var1x.getDirection()))
                           );
                           if (blockHitResult == null || blockHitResult.getType() == Type.MISS) {
                              n++;
                              continue;
                           }

                           if (b == 0) {
                              this.handler = var1x -> {
                                 BlockHitResult var2x = TargetUtils.method3216(
                                    mc.player.getEyePosition(1.0F),
                                    var1x,
                                    this.distance.getValue3(),
                                    var1xx -> var1xx != null
                                       && !mc.level.getBlockState(var1xx.getBlockPos()).canBeReplaced()
                                       && this.items.contains(var1xx.getBlockPos().relative(var1xx.getDirection()))
                                 );
                                 return var2x != null && var2x.getType() != Type.MISS ? var2x : null;
                              };
                              this.rotation = rotation;
                           } else {
                              this.rotation2 = rotation;
                           }

                           b++;
                        }

                        if (b == 2) {
                           break label84;
                        }
                        break;
                     }
                  }
               }

               if (KeyBindUtils.method2630(AutoWebModule.class) && (KeyBindUtils.method2630(AutoTotemModule.class) || this.hand.isSelected(0))) {
                  if (b > 0) {
                     if (mc.player.getItemInHand(this.hand.isSelected(0) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).getItem() != Items.COBWEB) {
                        KeyBindUtils.method2627(
                           AutoWebModule.class, () -> InventoryUtils.method900(slot, this.hand.isSelected(0) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true)
                        );
                        if (this.slot == null) {
                           this.slot = slot;
                        }
                     }
                  } else if (this.slot != null) {
                     this.method282();
                     return;
                  }
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void method281() {
      this.rotation = null;
      this.rotation2 = null;
      this.handler = null;
   }

   private void method282() {
      if (this.slot != null) {
         Slot slot = this.slot;
         KeyBindUtils.method2627(AutoWebModule.class, () -> InventoryUtils.method900(slot, this.hand.isSelected(0) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true));
         this.slot = null;
      }
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         this.method282();
      }

      this.slot = null;
      this.method281();
      this.playerEntity = null;
      super.onDisable();
   }

   @EventTarget
   private void method283(MoveInputEvent event) {
      if (this.handler != null) {
         MovementInputUtils.method7426(event, zenith.getRotationManager().getEffectiveRotation().getFloatValue(), mc.player.getYRot());
      }
   }

   public Player method284() {
      return mc.level
         .players()
         .stream()
         .filter(
            player -> player != mc.player
               && !Zenith.getInstance().getFriends().method7626(player)
               && mc.player.distanceToSqr(player) < (this.distance.getValue3() + 0.1) * (this.distance.getValue3() + 0.1)
         )
         .min(Comparator.comparingDouble(entity -> mc.player.distanceToSqr(entity) - (entity == AuraModule.INSTANCE.getLivingEntity22() ? 100 : 0)))
         .orElse(null);
   }

   public interface Handler {
      BlockHitResult isRotate(Rotation rotation);
   }
}
