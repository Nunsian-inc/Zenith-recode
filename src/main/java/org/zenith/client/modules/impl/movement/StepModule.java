package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(name = "Step", description = "module.step.description", category = ModuleCategory.MOVEMENT)
public final class StepModule extends Module {
   public static final StepModule INSTANCE = new StepModule();
   private static final Identifier STEP_MODIFIER = Identifier.fromNamespaceAndPath("zenith", "step_height");

   private final SliderSetting height = new SliderSetting("module.step.height", 1.0F, 1.0F, 2.5F, 0.1F,
      "module.step.height.desc", "b");
   private final BooleanSetting onlyOnGround = new BooleanSetting("module.step.onlyOnGround", "module.step.onlyOnGround.desc", true);
   private final SliderSetting delay = new SliderSetting("module.step.delay", 0.0F, 0.0F, 20.0F, 1.0F,
      "module.step.delay.desc", "t");
   private AttributeInstance modifiedAttribute;
   private double startY;
   private double normalHeight;
   private int cooldownTicks;

   private StepModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      this.restoreStepHeight();
      if (this.cooldownTicks > 0) {
         this.cooldownTicks--;
      }
   }

   @EventTarget
   public void onMove(PlayerMoveEvent event) {
      this.restoreStepHeight();
      if (event.isCancelled() || mc.player == null || mc.level == null || mc.gui.screen() != null
         || !mc.player.isAlive() || mc.player.isSpectator() || mc.player.isShiftKeyDown()
         || mc.player.isPassenger() || mc.player.isInWater() || mc.player.isInLava()
         || mc.player.isFallFlying() || mc.player.getAbilities().flying || mc.player.onClimbable()
         || this.onlyOnGround.isEnabled() && !mc.player.onGround() || this.cooldownTicks > 0) {
         return;
      }

      Vec3 movement = event.getVec3d();
      // Подъём при прыжке не превращаем в дополнительный шаг. В воздухе решение всё равно принимает ванильная коллизия.
      if (movement.y > 0.0 || movement.horizontalDistanceSqr() < 1.0E-8) {
         return;
      }
      AttributeInstance attribute = mc.player.getAttribute(Attributes.STEP_HEIGHT);
      if (attribute == null || attribute.getValue() >= this.height.getValue3()) {
         return;
      }

      Vec3 horizontal = new Vec3(movement.x, 0.0, movement.z);
      var box = mc.player.getBoundingBox();
      Vec3 blocked = Entity.collideBoundingBox(mc.player, horizontal, box, mc.level, List.of());
      if (blocked.subtract(horizontal).horizontalDistanceSqr() < 1.0E-8) {
         return;
      }
      Vec3 rise = Entity.collideBoundingBox(mc.player, new Vec3(0.0, this.height.getValue3(), 0.0), box, mc.level, List.of());
      Vec3 raised = Entity.collideBoundingBox(mc.player, horizontal, box.move(rise), mc.level, List.of());
      if (rise.y <= attribute.getValue() || raised.horizontalDistanceSqr() <= blocked.horizontalDistanceSqr() + 1.0E-8) {
         return;
      }

      this.modifiedAttribute = attribute;
      this.normalHeight = attribute.getValue();
      this.startY = mc.player.getY();
      // Собственный временный модификатор не затирает базовое значение и серверные модификаторы атрибута.
      double amount = this.normalHeight > 0.0 ? this.height.getValue3() / this.normalHeight - 1.0 : this.height.getValue3();
      var operation = this.normalHeight > 0.0 ? AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL : AttributeModifier.Operation.ADD_VALUE;
      attribute.addTransientModifier(new AttributeModifier(STEP_MODIFIER, amount, operation));
   }

   @EventTarget
   public void onPlayerTick(PlayerTickEvent event) {
      if (this.modifiedAttribute != null && mc.player != null
         && mc.player.getAttribute(Attributes.STEP_HEIGHT) == this.modifiedAttribute
         && mc.player.getY() - this.startY > this.normalHeight + 1.0E-3) {
         this.cooldownTicks = (int)this.delay.getValue3();
      }
      this.restoreStepHeight();
   }

   private void restoreStepHeight() {
      if (this.modifiedAttribute != null) {
         this.modifiedAttribute.removeModifier(STEP_MODIFIER);
         this.modifiedAttribute = null;
      }
   }

   @Override
   public void onDisable() {
      this.restoreStepHeight();
      this.cooldownTicks = 0;
      super.onDisable();
   }
}
