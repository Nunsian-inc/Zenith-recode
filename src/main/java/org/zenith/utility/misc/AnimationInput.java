package org.zenith.utility.misc;

public final class AnimationInput {
   public float capeFlap;
   public float capeLean;
   public float capeLeanSideways;
   public float leftLegPitch;
   public float rightLegPitch;
   public float walkSpeed;
   public float bodyYawVelocity;
   public float deltaSeconds;
   public boolean crouching;
   public boolean airborne;
   public boolean walking;

   public void clear() {
      this.capeFlap = 0.0F;
      this.capeLean = 0.0F;
      this.capeLeanSideways = 0.0F;
      this.leftLegPitch = 0.0F;
      this.rightLegPitch = 0.0F;
      this.walkSpeed = 0.0F;
      this.bodyYawVelocity = 0.0F;
      this.deltaSeconds = 0.0F;
      this.crouching = false;
      this.airborne = false;
      this.walking = false;
   }
}
