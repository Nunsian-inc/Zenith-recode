package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.CrystalAuraModule;
import org.zenith.client.modules.impl.combat.SpearAuraModule;
import org.zenith.client.modules.impl.combat.TriggerBotModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.PacketUtils;

@ModuleInfo(
   name = "AntiAFK",
   description = "module.antiafk.description",
   category = ModuleCategory.MISC
)
public final class AntiAFKModule extends Module {
   public static final AntiAFKModule INSTANCE = new AntiAFKModule();
   private final SliderSetting interval = new SliderSetting(
      "module.antiAfk.interval", 60.0F, 20.0F, 60.0F, 1.0F, "module.antiAfk.interval.desc", " s"
   );
   private final ModeSetting action = new ModeSetting(
      "module.antiAfk.action", "module.antiAfk.action.desc",
      "module.antiAfk.action.rotate", "module.antiAfk.action.step", "module.antiAfk.action.jump"
   );
   private final BooleanSetting onlyStanding = new BooleanSetting("module.antiAfk.onlyStanding", "module.antiAfk.onlyStanding.desc", true);
   private final BooleanSetting pauseInCombat = new BooleanSetting("module.antiAfk.pauseInCombat", "module.antiAfk.pauseInCombat.desc", true);
   private final StopWatch actionTimer = new StopWatch();
   private ClientLevel world;
   private LocalPlayer player;
   private long nextDelayMillis;
   private int stepTicksRemaining;
   private boolean jumpPending;
   private float stepYaw;

   private AntiAFKModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      this.world = null;
      this.player = null;
      super.onDisable();
   }

   private void resetState() {
      this.world = mc.level;
      this.player = mc.player;
      this.stepTicksRemaining = 0;
      this.jumpPending = false;
      this.scheduleNextAction();
   }

   private void scheduleNextAction() {
      // Интервал задаёт верхнюю границу; по умолчанию получаем случайные 20–60 секунд.
      float configuredInterval = this.interval.getValue3();
      double maximumSeconds = Float.isFinite(configuredInterval) ? Math.clamp(configuredInterval, 20.0F, 60.0F) : 60.0;
      this.nextDelayMillis = ThreadLocalRandom.current().nextLong(20000L, (long)(maximumSeconds * 1000.0) + 1L);
      this.actionTimer.reset();
   }

   @EventTarget
   public void onTick(TickEvent event) {
      // UpdateEvent не приходит в главном меню: сбрасываем незавершённый шаг и при выходе.
      if (this.world != mc.level || this.player != mc.player) {
         this.resetState();
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.world != mc.level || this.player != mc.player) {
         this.resetState();
      }
      if (!this.canAct()) {
         this.cancelAction();
         return;
      }
      if (this.stepTicksRemaining > 0 || this.jumpPending) {
         return;
      }
      if (this.onlyStanding.isEnabled() && !this.isStanding()) {
         this.scheduleNextAction();
         return;
      }
      if (!this.actionTimer.hasElapsed(this.nextDelayMillis)) {
         return;
      }

      if (this.action.isSelected(0)) {
         float yaw = mc.player.getYRot() + (ThreadLocalRandom.current().nextBoolean() ? 2.0F : -2.0F);
         mc.player.setYRot(yaw);
         PacketUtils.sendPacket(new ServerboundMovePlayerPacket.Rot(yaw, mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision));
      } else if (this.action.isSelected(1)) {
         this.stepYaw = mc.player.getYRot();
         this.stepTicksRemaining = 6;
      } else if (this.action.isSelected(2)) {
         this.jumpPending = true;
      }
      this.scheduleNextAction();
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.stepTicksRemaining == 0 && !this.jumpPending) {
         return;
      }
      Input input = event.getPlayerInput();
      // Меняем только текущий Input, не KeyMapping: после отмены/выключения клавиши не залипают.
      if (this.world != mc.level || this.player != mc.player || !this.canAct() || input == null
         || input.forward() || input.backward() || input.left() || input.right() || input.jump() || input.shift()
         || Math.abs(mc.player.getYRot() - this.stepYaw) > 5.0F && this.stepTicksRemaining > 0) {
         this.cancelAction();
         return;
      }
      if (this.jumpPending) {
         if (mc.level.noCollision(mc.player, mc.player.getBoundingBox().expandTowards(0.0, 1.25, 0.0))) {
            event.setJump(true);
         }
         this.jumpPending = false;
         return;
      }

      boolean forward = this.stepTicksRemaining > 3;
      double direction = forward ? 1.0 : -1.0;
      double yaw = Math.toRadians(this.stepYaw);
      Vec3 offset = new Vec3(-Math.sin(yaw) * 0.5 * direction, 0.0, Math.cos(yaw) * 0.5 * direction);
      AABB destination = mc.player.getBoundingBox().move(offset);
      // Не шагаем в стену, жидкость или за край без опоры под будущей позицией.
      AABB support = new AABB(destination.minX, destination.minY - 0.1, destination.minZ,
         destination.maxX, destination.minY, destination.maxZ);
      if (!mc.level.noCollision(mc.player, mc.player.getBoundingBox().expandTowards(offset))
         || mc.level.containsAnyLiquid(destination)
         || mc.level.noCollision(mc.player, support)) {
         this.cancelAction();
         return;
      }
      event.setForward(forward);
      event.setBackward(!forward);
      event.setSprint(false);
      this.stepTicksRemaining--;
   }

   private boolean canAct() {
      return mc.player != null && mc.level != null && mc.getConnection() != null
         && mc.player.isAlive() && !mc.player.isSpectator() && mc.gui.screen() == null
         && !mc.player.isPassenger() && !mc.player.isFallFlying() && !mc.player.getAbilities().flying
         && mc.player.onGround() && !mc.player.isInWater() && !mc.player.isInLava()
         && !mc.player.isUsingItem() && !this.hasManualInput()
         && (!this.pauseInCombat.isEnabled() || !this.isCombatModuleEnabled());
   }

   private boolean hasManualInput() {
      return mc.options.keyUp.isDown() || mc.options.keyDown.isDown()
         || mc.options.keyLeft.isDown() || mc.options.keyRight.isDown()
         || mc.options.keyJump.isDown() || mc.options.keyShift.isDown()
         || mc.options.keyAttack.isDown() || mc.options.keyUse.isDown();
   }

   private boolean isStanding() {
      Vec3 motion = mc.player.getDeltaMovement();
      return motion.x * motion.x + motion.z * motion.z < 1.0E-4;
   }

   private boolean isCombatModuleEnabled() {
      return AuraModule.INSTANCE.isEnabled() || CrystalAuraModule.INSTANCE.isEnabled()
         || SpearAuraModule.INSTANCE.isEnabled() || TriggerBotModule.INSTANCE.isEnabled()
         || AimAssistModule.INSTANCE.isEnabled() || AutoDuelsModule.INSTANCE.isEnabled();
   }

   private void cancelAction() {
      this.stepTicksRemaining = 0;
      this.jumpPending = false;
      this.scheduleNextAction();
   }
}
