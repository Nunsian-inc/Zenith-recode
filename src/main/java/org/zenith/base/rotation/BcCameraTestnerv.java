package org.zenith.base.rotation;

import lombok.Generated;
import org.zenith.Rotation;
import org.zenith.ZenithAccess;
import org.zenith.base.rotation.mods.Agent;
import org.zenith.base.rotation.mods.BcCamera;
import org.zenith.base.rotation.mods.BcHand;
import org.zenith.base.rotation.mods.HolyWorldRotation;
import org.zenith.base.rotation.mods.IntentReplayRotation;
import org.zenith.base.rotation.mods.InstantRotation;
import org.zenith.base.rotation.mods.TargetBoxRotation;
import org.zenith.base.rotation.mods.HumanRotation;
import org.zenith.base.rotation.mods.ExperimentalRotationModes;
import org.zenith.base.rotation.mods.HandRotationLogger;
import org.zenith.base.rotation.mods.Motor;
import org.zenith.base.rotation.mods.MotorClaude;
import org.zenith.base.rotation.mods.MotorHand2;
import org.zenith.base.rotation.mods.SlewRotationMod;
import org.zenith.base.rotation.mods.TokenMotor;
import org.zenith.utility.misc.HumanRotationConfig;
import org.zenith.utility.misc.HandRotationConfig;
import org.zenith.utility.misc.BcCamera2RotationConfig;
import org.zenith.utility.misc.GownoV3RotationConfig;
import org.zenith.utility.misc.MotorRotationConfig;
import org.zenith.utility.misc.GownoV2RotationConfig;
import org.zenith.utility.misc.InstantRotationConfig;
import org.zenith.utility.misc.BcCameraRotationConfig;
import org.zenith.utility.misc.AgentRotationConfig;
import org.zenith.utility.misc.BcTestNervRotationConfig;
import org.zenith.utility.misc.BcHandRotationConfig;
import org.zenith.utility.misc.HolyWorldRotationConfig;
import org.zenith.utility.misc.TokenRotationConfig;
import org.zenith.utility.misc.FunTimeRotationConfig;
import org.zenith.utility.misc.GownoRotationConfig;
import org.zenith.utility.misc.GownoV4RotationConfig;
import org.zenith.utility.misc.MotorClaudeRotationConfig;
import org.zenith.utility.misc.RotationConfig;

public class BcCameraTestnerv implements ZenithAccess {
   private final InstantRotation class23 = new InstantRotation();
   private final TargetBoxRotation class24 = new TargetBoxRotation();
   private IntentReplayRotation class22;
   private final SlewRotationMod slewRotationMod = new SlewRotationMod();
   private final Motor motor = new Motor();
   private final TokenMotor tokenMotor = new TokenMotor();
   private final HandRotationLogger handRotationLogger = new HandRotationLogger();
   private final Agent agent = new Agent();
   private final MotorClaude motorClaude = new MotorClaude("motor_imitation.json");
   private final MotorHand2 motorHand2 = new MotorHand2();
   private final BcCamera bcCamera = new BcCamera();
   private final BcCamera bcCamera2 = new BcCamera("bc_camera_2.json", 2, 1.0);
   private final BcCamera bcCamera3 = new BcCamera("bc_camera_testnerv.json", 4, 1.0);
   private final BcHand bcHand = new BcHand();
   private final HumanRotation class25 = new HumanRotation();
   private final ExperimentalRotationModes class26 = new ExperimentalRotationModes();
   private final HolyWorldRotation class21 = new HolyWorldRotation();
   private final RotationConfig rotationConfig = new InstantRotationConfig();
   private final RotationConfig rotationConfig2 = new FunTimeRotationConfig();
   private final RotationConfig rotationConfig3 = new HolyWorldRotationConfig();
   private final RotationConfig rotationConfig4 = GownoRotationConfig.method861().method869();
   private final RotationConfig rotationConfig5 = GownoV2RotationConfig.method3287().method3288();
   private final RotationConfig rotationConfig6 = GownoV3RotationConfig.method2737().method2738();
   private final RotationConfig rotationConfig7 = GownoV4RotationConfig.method1437().method1438();
   private final RotationConfig rotationConfig8 = TokenRotationConfig.method7548().method7549();
   private final RotationConfig rotationConfig9 = HandRotationConfig.method2017().method2018();
   private final RotationConfig rotationConfig10 = AgentRotationConfig.method6490().method6491();
   private final RotationConfig rotationConfig11 = MotorRotationConfig.method2781().method2782();
   private final RotationConfig rotationConfig12 = MotorClaudeRotationConfig.method1450().method1451();
   private final RotationConfig rotationConfig13 = BcCameraRotationConfig.method5354().method5355();
   private final RotationConfig rotationConfig14 = BcCamera2RotationConfig.method2520().method2521();
   private final RotationConfig rotationConfig15 = BcTestNervRotationConfig.method6610().method6611();
   private final RotationConfig rotationConfig16 = BcHandRotationConfig.method6647().method6648();
   private final RotationConfig rotationConfig17 = new HumanRotationConfig();

   public IntentReplayRotation method1850() {
      if (this.class22 == null) {
         this.class22 = new IntentReplayRotation();
      }

      return this.class22;
   }

   public Rotation method1851(RotationConfig rotationConfig, Rotation rotation) {
      Rotation rotation2;
      switch (rotationConfig.method853()) {
         case INSTANT:
            rotation2 = this.class23.method3534(rotation);
            break;
         case FUNTIME:
            rotation2 = this.motor.method3659();
            break;
         case GOWNO:
            rotation2 = this.motor.method3659();
            break;
         case GOWNO_V2:
            rotation2 = this.motor.method3659();
            break;
         case GOWNO_V3:
            rotation2 = this.motor.method3659();
            break;
         case GOWNO_V4:
            rotation2 = this.motor.method3659();
            break;
         case HOLYWORLD:
            rotation2 = this.class21.method3431(rotation);
            break;
         case HUMAN:
            rotation2 = this.class25.method3653(rotation);
            break;
         case HAND:
            return this.handRotationLogger.method3727();
         case AGENT:
            return this.agent.method3462();
         case MOTOR:
            return this.motorClaude.method3425(rotation);
         case MOTOR_CLAUDE:
            return this.motorHand2.method3430(rotation);
         case BC_CAMERA:
            return this.bcCamera.method3554(rotation);
         case BC_CAMERA_2:
            return this.bcCamera2.method3554(rotation);
         case BC_TEST_NERV:
            return this.bcCamera3.method3554(rotation);
         case BC_HAND:
            return this.bcHand.method3874(rotation);
         case TOKEN:
            return this.tokenMotor.method3467();
         default:
            rotation2 = zenith.getRotationManager().getEffectiveRotation();
      }

      return rotationManager.getEffectiveRotation().equals(rotation2)
         ? rotation2
         : rotationManager.getEffectiveRotation().add(rotationManager.getEffectiveRotation().wrappedDeltaTo(rotation2)).quantizeRelativeTo(rotationManager.getEffectiveRotation());
   }

   @Generated
   public TargetBoxRotation getClass24() {
      return this.class24;
   }

   @Generated
   public SlewRotationMod getSlewRotationMod() {
      return this.slewRotationMod;
   }

   @Generated
   public Motor getMotor() {
      return this.motor;
   }

   @Generated
   public TokenMotor getTokenMotor() {
      return this.tokenMotor;
   }

   @Generated
   public HandRotationLogger getHandRotationLogger() {
      return this.handRotationLogger;
   }

   @Generated
   public Agent getAgent() {
      return this.agent;
   }

   @Generated
   public MotorClaude getMotorClaude() {
      return this.motorClaude;
   }

   @Generated
   public MotorHand2 getMotorHand2() {
      return this.motorHand2;
   }

   @Generated
   public BcCamera getBcCamera() {
      return this.bcCamera;
   }

   @Generated
   public BcCamera getBcCamera2() {
      return this.bcCamera2;
   }

   @Generated
   public BcCamera getBcCamera3() {
      return this.bcCamera3;
   }

   @Generated
   public BcHand getBcHand() {
      return this.bcHand;
   }

   @Generated
   public HumanRotation getClass25() {
      return this.class25;
   }

   @Generated
   public RotationConfig getRotationConfig() {
      return this.rotationConfig;
   }

   @Generated
   public RotationConfig getRotationConfig2() {
      return this.rotationConfig2;
   }

   @Generated
   public RotationConfig getRotationConfig3() {
      return this.rotationConfig3;
   }

   @Generated
   public RotationConfig getRotationConfig4() {
      return this.rotationConfig4;
   }

   @Generated
   public RotationConfig getRotationConfig5() {
      return this.rotationConfig5;
   }

   @Generated
   public RotationConfig getRotationConfig6() {
      return this.rotationConfig6;
   }

   @Generated
   public RotationConfig getRotationConfig7() {
      return this.rotationConfig7;
   }

   @Generated
   public RotationConfig getRotationConfig8() {
      return this.rotationConfig8;
   }

   @Generated
   public RotationConfig getRotationConfig9() {
      return this.rotationConfig9;
   }

   @Generated
   public RotationConfig getRotationConfig10() {
      return this.rotationConfig10;
   }

   @Generated
   public RotationConfig getRotationConfig11() {
      return this.rotationConfig11;
   }

   @Generated
   public RotationConfig getRotationConfig12() {
      return this.rotationConfig12;
   }

   @Generated
   public RotationConfig getRotationConfig13() {
      return this.rotationConfig13;
   }

   @Generated
   public RotationConfig getRotationConfig14() {
      return this.rotationConfig14;
   }

   @Generated
   public RotationConfig getRotationConfig15() {
      return this.rotationConfig15;
   }

   @Generated
   public RotationConfig getRotationConfig16() {
      return this.rotationConfig16;
   }

   @Generated
   public RotationConfig getRotationConfig17() {
      return this.rotationConfig17;
   }
}
