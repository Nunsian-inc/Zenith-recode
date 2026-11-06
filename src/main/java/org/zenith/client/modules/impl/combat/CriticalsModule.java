package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.CriticalsState;
import org.zenith.utility.misc.TimerSpeed;
import org.zenith.utility.misc.Vec2f;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "Criticals",
   category = ModuleCategory.COMBAT,
   description = "module.criticals.description"
)
public final class CriticalsModule extends Module {
   public static final CriticalsModule INSTANCE = new CriticalsModule();
   private static final String text = "module.criticals.packet";
   private static final String text2 = "module.criticals.cooldownTimer";
   private static final String text3 = "module.criticals.levitation";
   private static final String text4 = "module.criticals.web";
   private static final double CONST_doubleValue = 120.0;
   private static final double CONST_doubleValue2 = 15.0;
   private static final double CONST_doubleValue3 = 50.0;
   private static final float CONST_floatValue = 0.3F;
   private static final float CONST_floatValue2 = 6.0F;
   private static final int CONST_intValue = 12;
   private final MultiSelectSetting mode = new MultiSelectSetting(
      "module.criticals.mode",
      "module.criticals.mode.desc",
      MultiSelectSetting.Entry.method5231("module.criticals.packet", true),
      MultiSelectSetting.Entry.method5231("module.criticals.cooldownTimer", false),
      MultiSelectSetting.Entry.method5231("module.criticals.levitation", false),
      MultiSelectSetting.Entry.method5231("module.criticals.web", false)
   );
   private boolean flag;
   private boolean flag2;
   private float floatValue = 1.0F;
   private double doubleValue;
   private double doubleValue2;
   private long longValue;

   private CriticalsModule() {
   }

   @EventTarget
   public void method4660(PacketEvent event) {
      if (event.isOutgoing() && mc.player != null && event.getPacket() instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
         if (this.method4667()) {
            this.doubleValue = AuraModule.INSTANCE.method6346();
            this.flag = true;
            TimerSpeed.setSpeedMultiplier(this.method4661(this.method4669().method1709(this.doubleValue, 0.0)));
         } else {
            boolean flag = this.mode.method5226("module.criticals.levitation") && mc.player.hasEffect(MobEffects.LEVITATION);
            boolean flag2 = this.mode.method5226("module.criticals.web") && UseItemUtils.method6513(Blocks.COBWEB);
            if (!flag && !flag2) {
               if (this.mode.method5226("module.criticals.packet") && !mc.player.onGround() && mc.player.fallDistance == 0.0) {
                  UseItemUtils.method6533(
                     -(mc.player.fallDistance = (float)MathUtils.method2651(1.0E-5F, 1.0E-4F)),
                     Zenith.getInstance()
                        .getRotationManager()
                        .getEffectiveRotation()
                        .add(new Vec2f((float)MathUtils.method2651(-0.001F, 0.001F), (float)MathUtils.method2651(-0.001F, 0.001F)))
                  );
               }
            } else {
               mc.player
                  .connection
                  .send(
                     new Pos(mc.player.getX(), mc.player.getY() - MathUtils.method2651(1.0E-5F, 1.0E-4F), mc.player.getZ(), false, false)
                  );
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (!this.method4667()) {
            if (this.flag) {
               TimerSpeed.setSpeedMultiplier(1.0F);
               this.method4671();
            }
         } else {
            this.method4666();
            if (this.flag) {
               CriticalsState criticalsState = this.method4669();
               this.doubleValue = AuraModule.INSTANCE.method6346();
               long l = criticalsState.method1709(this.doubleValue, 0.0);
               if (mc.player.fallDistance > 0.0 && criticalsState.method1708(this.doubleValue, 0.0) && AuraModule.INSTANCE.method6348()) {
                  this.flag = false;
                  this.method4665();
                  TimerSpeed.setSpeedMultiplier(1.0F);
               } else {
                  TimerSpeed.setSpeedMultiplier(this.method4661(l));
               }
            }
         }
      }
   }

   private float method4661(long l) {
      if (mc.player != null && !mc.player.onGround()) {
         double d = l / 1000000.0;
         if (mc.player.fallDistance > 0.0) {
            this.method4665();
            return this.method4664(d);
         } else if (mc.player.fallDistance != 0.0) {
            this.method4665();
            return 1.0F;
         } else {
            int n = AuraModule.INSTANCE.method6349(12, true);
            if (n < 0) {
               this.method4665();
               return 1.0F;
            } else if (l <= 0L) {
               float floatValue = this.method4662(n);
               if (floatValue <= 1.0F) {
                  this.method4665();
                  return 1.0F;
               } else {
                  this.flag2 = true;
                  this.floatValue = floatValue;
                  return floatValue;
               }
            } else if (this.flag2) {
               this.floatValue = Math.min(this.floatValue, this.method4662(n));
               if (this.floatValue <= 1.0F) {
                  this.method4665();
                  return 1.0F;
               } else {
                  return this.floatValue;
               }
            } else {
               long l2 = (long)Math.ceil(this.method4669().getDoubleValue());
               if (l > l2) {
                  return 0.3F;
               } else {
                  double d2 = n * 50.0 / d;
                  if (d2 <= 1.0) {
                     return 1.0F;
                  } else {
                     float floatValue = (float)Math.min(6.0, d2);
                     double d3 = n * (50.0 - 50.0 / floatValue);
                     boolean flag = d2 <= 6.0;
                     if (flag && this.method4663() + 0.001 >= d3) {
                        this.flag2 = true;
                        this.floatValue = floatValue;
                        return this.floatValue;
                     } else {
                        return 0.3F;
                     }
                  }
               }
            }
         }
      } else {
         this.method4665();
         return 1.0F;
      }
   }

   private float method4662(int n) {
      double d = n * 50.0;
      double d2 = this.method4663();
      if (d <= 0.0 || d2 <= 0.0) {
         return 1.0F;
      } else if (d2 >= d) {
         return 6.0F;
      } else {
         double d3 = 1.0 / (1.0 - d2 / d);
         return (float)Math.min(6.0, d3);
      }
   }

   private double method4663() {
      return Math.max(0.0, this.doubleValue2 - 15.0);
   }

   private float method4664(double d) {
      int n = AuraModule.INSTANCE.method6350(12);
      if (n < 0) {
         return 1.0F;
      } else {
         double d2 = n * 50.0;
         if (d <= d2) {
            return 1.0F;
         } else {
            double d3 = d2 / 0.3F;
            return d > d3 ? 1.0F : (float)Math.max(0.3F, d2 / d);
         }
      }
   }

   private void method4665() {
      this.flag2 = false;
      this.floatValue = 1.0F;
   }

   private void method4666() {
      long nanoTime = System.nanoTime();
      if (this.longValue != 0L) {
         double d = (nanoTime - this.longValue) / 1000000.0;
         this.doubleValue2 = Math.max(0.0, Math.min(120.0, this.doubleValue2 + d - 50.0));
      }

      this.longValue = nanoTime;
   }

   public boolean method4667() {
      return this.isEnabled() && this.mode.method5226("module.criticals.cooldownTimer") && AuraModule.INSTANCE.method6347();
   }

   public boolean method4668(float f) {
      if (!this.method4667()) {
         return true;
      } else {
         this.doubleValue = AuraModule.INSTANCE.method6346();
         CriticalsState criticalsState = this.method4669();
         return mc.player != null && !(mc.player.fallDistance <= 0.0) ? criticalsState.method1708(this.doubleValue, f) : false;
      }
   }

   private CriticalsState method4669() {
      return Zenith.getInstance().getServerDetector().getCriticalsState();
   }

   @Override
   public void onEnable() {
      this.method4671();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      TimerSpeed.setSpeedMultiplier(1.0F);
      this.method4671();
      super.onDisable();
   }

   private void method4671() {
      this.flag = false;
      this.method4665();
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.longValue = 0L;
   }

   public boolean method4672() {
      return mc.player != null && !this.method4667()
         ? this.mode.method5226("module.criticals.packet") && !mc.player.onGround()
            || this.mode.method5226("module.criticals.levitation") && mc.player.hasEffect(MobEffects.LEVITATION)
            || this.mode.method5226("module.criticals.web") && UseItemUtils.method6513(Blocks.COBWEB)
         : false;
   }

   @Generated
   public MultiSelectSetting getMode() {
      return this.mode;
   }

   @Generated
   public boolean isFlag7() {
      return this.flag;
   }

   @Generated
   public boolean isFlag24() {
      return this.flag2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue;
   }

   @Generated
   public double getDoubleValue() {
      return this.doubleValue;
   }

   @Generated
   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   @Generated
   public long getLongValue4() {
      return this.longValue;
   }
}
