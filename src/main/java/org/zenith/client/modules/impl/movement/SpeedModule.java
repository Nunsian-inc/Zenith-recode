package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.MovementCorrectionEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Util13;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "Speed",
   description = "module.speed.description",
   category = ModuleCategory.MOVEMENT
)
public final class SpeedModule extends Module {
   public static final SpeedModule INSTANCE = new SpeedModule();
   private static final int CONST_intValue = 16;
   private final ModeSetting mode = new ModeSetting(
      "module.speed.mode",
      "module.speed.mode.desc",
      "module.speed.mode.holyWorld",
      "module.speed.mode.grimOld",
      "module.speed.mode.reallyWorld",
      "module.speed.mode.grimElytra",
      "module.speed.mode.grim72",
      "module.speed.mode.custom"
   );
   private final SliderSetting grim72SlowTimer = new SliderSetting(
      "module.speed.grim72SlowTimer", 0.4F, 0.0F, 2.0F, 0.1F, "module.speed.grim72SlowTimer.desc", "x", this::method6601
   );
   private final SliderSetting grim72FastTimer = new SliderSetting(
      "module.speed.grim72FastTimer", 3.4F, 1.0F, 5.0F, 0.1F, "module.speed.grim72FastTimer.desc", "x", this::method6601
   );
   private final SliderSetting speed = new SliderSetting("module.speed.speed", 0.1F, 0.0F, 0.5F, 0.01F, "module.speed.speed.desc", "x", this::method6607);
   private final SliderSetting collisionRadius = new SliderSetting(
      "module.speed.collisionRadius", 0.3F, 0.0F, 1.0F, 0.01F, "module.speed.collisionRadius.desc", "b", this::method6607
   );
   private final BooleanSetting onlyAura = new BooleanSetting("module.speed.onlyAura", "module.speed.onlyAura.desc", true, this::method6607);
   // Custom: скорость (блоков за тик) задаётся вручную, отдельно под эффекты Скорости/Замедления.
   private final SliderSetting customBaseSpeed = new SliderSetting(
      "module.speed.customBaseSpeed", 0.36F, 0.1F, 1.0F, 0.01F, "module.speed.customBaseSpeed.desc", "", this::isCustomMode
   );
   private final SliderSetting customSpeedI = new SliderSetting(
      "module.speed.customSpeedI", 0.41F, 0.1F, 1.0F, 0.01F, "module.speed.customSpeedI.desc", "", this::isCustomMode
   );
   private final SliderSetting customSpeedII = new SliderSetting(
      "module.speed.customSpeedII", 0.47F, 0.1F, 1.0F, 0.01F, "module.speed.customSpeedII.desc", "", this::isCustomMode
   );
   private final SliderSetting customSpeedIII = new SliderSetting(
      "module.speed.customSpeedIII", 0.53F, 0.1F, 1.0F, 0.01F, "module.speed.customSpeedIII.desc", "", this::isCustomMode
   );
   private final SliderSetting customSlowI = new SliderSetting(
      "module.speed.customSlowI", 0.85F, 0.1F, 1.0F, 0.01F, "module.speed.customSlowI.desc", "x", this::isCustomMode
   );
   private final SliderSetting customSlowII = new SliderSetting(
      "module.speed.customSlowII", 0.78F, 0.1F, 1.0F, 0.01F, "module.speed.customSlowII.desc", "x", this::isCustomMode
   );
   private final SliderSetting customSlowIII = new SliderSetting(
      "module.speed.customSlowIII", 0.7F, 0.1F, 1.0F, 0.01F, "module.speed.customSlowIII.desc", "x", this::isCustomMode
   );
   private final BooleanSetting customAutoJump = new BooleanSetting(
      "module.speed.customAutoJump", "module.speed.customAutoJump.desc", false, this::isCustomMode
   );
   private final SliderSetting customAirMultiplier = new SliderSetting(
      "module.speed.customAirMultiplier", 1.435F, 1.0F, 2.0F, 0.005F, "module.speed.customAirMultiplier.desc", "x", this::isCustomMode
   );
   private final BooleanSetting customDamageBoost = new BooleanSetting(
      "module.speed.customDamageBoost", "module.speed.customDamageBoost.desc", false, this::isCustomMode
   );
   private final SliderSetting customDamageSpeed = new SliderSetting(
      "module.speed.customDamageSpeed",
      0.5F,
      0.1F,
      2.0F,
      0.01F,
      "module.speed.customDamageSpeed.desc",
      "",
      () -> this.isCustomMode() && this.customDamageBoost.isEnabled()
   );
   private int ticks = 0;
   private int intValue = 0;
   private double doubleValue;
   public boolean flag = false;
   public int intValue2 = 0;

   private SpeedModule() {
   }

   @Override
   public void onEnable() {
      this.intValue2 = 0;
      this.flag = false;
      this.ticks = 0;
      this.intValue = 0;
      Util13.method2404(1.0F);
      if (this.method6606() && mc.player != null && !mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         this.method6608();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      Util13.method2404(1.0F);
      if (this.method6606() && mc.player != null && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         this.method6608();
      }

      super.onDisable();
   }

   @EventTarget
   public void method6592(SprintUpdateEvent event) {
      if (this.flag) {
         event.setFlag2(false);
         this.flag = false;
      }
   }

   @EventTarget(4)
   public void method6593(UpdateEvent event) {
      if (this.intValue2 > 0) {
         this.intValue2--;
      }

      if (this.method6607()) {
         if (AuraModule.INSTANCE.isEnabled() || !this.onlyAura.isEnabled()) {
            this.method6602(null);
         }
      }
   }

   @EventTarget
   public void method6594(MovementCorrectionEvent event) {
      if (this.method6603() && mc.player != null) {
         Util13.method2404(1.7F);
         if (this.ticks > 3) {
            double d = 0.03;
            if (this.ticks % 2 == 0) {
               mc.player.push(0.0, 0.03F, 0.0);
               d = mc.player.onGround() ? 0.085 : 0.03;
            }

            if (this.method6609()) {
               double d2 = MovementInputUtils.method7427(mc.player.getYRot(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input));
               mc.player.push(-Math.sin(d2) * d, 0.0, Math.cos(d2) * d);
            }
         }

         this.ticks++;
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.method6603() && mc.player != null) {
         if (mc.player.verticalCollision) {
            this.intValue++;
         } else {
            this.intValue = 0;
         }

         if (this.intValue >= 1) {
            mc.player.jumpFromGround();
         }
      }
   }

   @EventTarget
   public void method6595(PostMotionEvent event) {
      if (this.method6603() && mc.player != null && mc.getConnection() != null) {
         if (this.ticks % 2 == 0) {
            Util13.method2404(0.3F);
            PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (this.method6603()) {
         if (event.getPacket3() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> {
               if (this.ticks % 2 == 1) {
                  this.ticks++;
               }

               Util13.method2404(1.0F);
            });
         }
      }
   }

   @EventTarget
   public void method6596(PostMotionEvent event) {
      if (this.method6601()) {
         if (this.method6599()) {
            this.method6600();
         } else {
            if (this.ticks % 2 == 0) {
               Util13.method2404(this.grim72SlowTimer.getValue3());
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
            }
         }
      }
   }

   @EventTarget
   public void method6597(MovementCorrectionEvent event) {
      if (this.method6601()) {
         if (this.method6599()) {
            this.method6600();
         } else {
            Util13.method2404(this.grim72FastTimer.getValue3());
            if (this.ticks > 3) {
               double d = 0.03;
               if (this.ticks % 2 == 0) {
                  mc.player.push(0.0, 0.03, 0.0);
                  d = mc.player.onGround() ? 0.085 : 0.03;
               }

               double d2 = MovementInputUtils.method7427(mc.player.getYRot(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input));
               mc.player.push(-Math.sin(d2) * d, 0.0, Math.cos(d2) * d);
            }

            this.ticks++;
         }
      }
   }

   @EventTarget
   public void method6598(PacketEvent event) {
      if (this.method6601() && event.method7100() && event.getPacket3() instanceof ClientboundPlayerPositionPacket) {
         mc.execute(() -> {
            if (!this.method6599()) {
               if (this.ticks % 2 == 1) {
                  this.ticks++;
               }

               Util13.method2404(1.0F);
            }
         });
      }
   }

   private boolean method6599() {
      return mc.player == null
         || mc.level == null
         || mc.getConnection() == null
         || !this.method6609()
         || mc.player.isPassenger()
         || mc.player.getAbilities().flying;
   }

   private void method6600() {
      this.ticks = 0;
      Util13.method2404(1.0F);
   }

   public boolean method6601() {
      return this.mode.isSelected(4);
   }

   private void method6602(MoveInputEvent event) {
      boolean sprinting;
      if (sprinting = mc.player.isSprinting()) {
         mc.player.setSprinting(false);
      }

      PlayerSimulation playerSimulation = PlayerSimulation.method6031(1);
      if (sprinting) {
         mc.player.setSprinting(true);
      }

      AABB inflate = playerSimulation.box.inflate(this.collisionRadius.getValue3(), this.collisionRadius.getValue3(), this.collisionRadius.getValue3());
      Object player = null;
      if (!this.onlyAura.isEnabled()) {
         if (AuraModule.INSTANCE.method6358() == null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity != mc.player && (entity instanceof LivingEntity || entity instanceof Boat) && inflate.intersects(entity.getBoundingBox())) {
                  player = entity;
                  break;
               }
            }
         }
      } else {
         if (AuraModule.INSTANCE.method6358() == null) {
            return;
         }

         if (inflate.intersects(AuraModule.INSTANCE.method6358().getBoundingBox())) {
            player = AuraModule.INSTANCE.method6358();
         }
      }

      if (player instanceof Player) {
         Vec3 vec3d = playerSimulation.vec3d;
         Vec3 center = (ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d2() != null
               ? ((Entity)player).dimensions.makeBoundingBox(ReachV3Module.INSTANCE.getVec3d2())
               : (player instanceof Player ? PlayerSimulation.method6032((Player)player, 2).box : ((Entity)player).getBoundingBox()))
            .getCenter();
         double d = center.x - vec3d.x;
         double d2 = center.z - vec3d.z;
         double d3 = Math.sqrt(d * d + d2 * d2);
         double d4 = this.speed.getValue3();
         double d5 = 0.0;
         double d6 = Math.max(0.0, d3 - d5);
         double d7 = Math.min(d4, d6);
         double[] doubles = new double[]{d * d7, d2 * d7};
         mc.player.push(doubles[0], 0.0, doubles[1]);
         if (this.mode.isSelected(0)) {
            if (mc.player.wasSprinting) {
               mc.player.push(-doubles[0], 0.0, -doubles[1]);
            }

            mc.player.setSprinting(false);
            mc.options.keySprint.setDown(false);
            this.flag = true;
         }

         if (mc.player.getBoundingBox().intersects(((Entity)player).getBoundingBox())) {
            this.intValue2 = 3;
            return;
         }
      } else {
         this.intValue2 = 0;
      }
   }

   private boolean method6603() {
      return this.mode.isSelected(2);
   }

   @EventTarget
   public void method6604(UpdateEvent event) {
      if (this.method6606() && mc.player != null && mc.level != null) {
         if (this.method6609()) {
            mc.player.setSprinting(true);
         }

         if (!mc.player.onGround() && !mc.player.isFallFlying() && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
            mc.player.startFallFlying();
         }

         if (mc.player.onGround()) {
            this.ticks = 16;
            this.doubleValue = mc.player.getY();
         }
      }
   }

   @EventTarget
   public void method6605(Event5 event) {
      if (this.method6606()
         && mc.player != null
         && mc.level != null
         && this.method6609()
         && !mc.player.onGround()
         && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         float yRot = (float)Math.toDegrees(
            MovementInputUtils.method7427(mc.player.getYRot(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input))
         );
         if (this.ticks > 0) {
            double d = Math.abs(mc.player.getY() - this.doubleValue);
            Math.max(0.0, 1.0 - d);
            float xRot = 75.0F;
            Rotation rotation = new Rotation(yRot, xRot);
            zenith.getRotationManager().method7849(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 2, this);
         }
      }
   }

   public boolean method6606() {
      return this.mode.isSelected(3);
   }

   private boolean method6607() {
      return this.mode.isSelected(0) || this.mode.isSelected(1);
   }

   private void method6608() {
      if (mc.player != null) {
         Slot slot;
         if (mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            slot = InventoryUtils.method916(
               List.of(
                  Items.NETHERITE_CHESTPLATE,
                  Items.DIAMOND_CHESTPLATE,
                  Items.CHAINMAIL_CHESTPLATE,
                  Items.IRON_CHESTPLATE,
                  Items.GOLDEN_CHESTPLATE,
                  Items.LEATHER_CHESTPLATE
               ),
               Comparator.comparingInt(slot2 -> slot2.index)
            );
         } else {
            slot = InventoryUtils.method912(mc.player.inventoryMenu, Items.ELYTRA, Comparator.comparingInt(slot2 -> slot2.index), slot2 -> true);
         }

         if (slot != null) {
            InventoryUtils.method934(slot, 6, true, false);
         }
      }
   }

   private boolean isCustomMode() {
      return this.mode.isSelected(5);
   }

   @EventTarget
   public void onCustomUpdate(UpdateEvent event) {
      if (this.isCustomMode() && mc.player != null && mc.level != null) {
         this.handleCustomMove(mc.player);
      }
   }

   /** Custom: каждый тик ставит горизонтальную скорость по вводу; в воздухе — с множителем, без ввода — стоп. */
   private void handleCustomMove(LocalPlayer player) {
      float f = this.customBaseSpeed.getValue3();
      MobEffectInstance effect = player.getEffect(MobEffects.SPEED);
      if (effect != null) {
         f = switch (effect.getAmplifier()) {
            case 0 -> this.customSpeedI.getValue3();
            case 1 -> this.customSpeedII.getValue3();
            default -> this.customSpeedIII.getValue3();
         };
      }

      MobEffectInstance mobEffectInstance = player.getEffect(MobEffects.SLOWNESS);
      if (mobEffectInstance != null) {
         f *= switch (mobEffectInstance.getAmplifier()) {
            case 0 -> this.customSlowI.getValue3();
            case 1 -> this.customSlowII.getValue3();
            default -> this.customSlowIII.getValue3();
         };
      }

      if (this.customDamageBoost.isEnabled() && player.hurtTime > 0) {
         f += this.customDamageSpeed.getValue3();
      }

      if (this.customAutoJump.isEnabled() && this.method6609() && player.onGround() && !mc.options.keyJump.isDown()) {
         player.jumpFromGround();
      }

      if (!player.isInWater() && !player.onGround() && !player.isFallFlying()) {
         this.setHorizontalSpeed(player, f * this.customAirMultiplier.getValue3());
      }

      if (player.onGround()) {
         this.setHorizontalSpeed(player, f);
      }
   }

   private void setHorizontalSpeed(LocalPlayer player, double d) {
      Vec3 deltaMovement = player.getDeltaMovement();
      if (!this.method6609()) {
         player.setDeltaMovement(0.0, deltaMovement.y, 0.0);
      } else {
         double d2 = MovementInputUtils.method7427(player.getYRot(), ViaCompat.method4846(player.input), ViaCompat.method4847(player.input));
         player.setDeltaMovement(-Math.sin(d2) * d, deltaMovement.y, Math.cos(d2) * d);
      }
   }

   private boolean method6609() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F;
   }

   @Generated
   public int getIntValue23() {
      return this.intValue2;
   }
}
