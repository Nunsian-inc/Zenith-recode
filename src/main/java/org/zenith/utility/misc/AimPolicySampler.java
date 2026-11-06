package org.zenith.utility.misc;

import org.zenith.utility.api.AimPolicy;

public final class AimPolicySampler implements Resettable {
   private final AimPolicyRunner aimPolicyRunner;
   private final int[][] intValues;
   private int intValue;
   private int cursor;

   public AimPolicySampler(AimPolicy aimPolicy) {
      this.aimPolicyRunner = new AimPolicyRunner(aimPolicy);
      this.intValues = new int[aimPolicy.intValue][2];
   }

   @Override
   public int[] method2865(float[] floats) {
      if (this.cursor >= this.intValue) {
         int[][] intses = this.aimPolicyRunner.method6749(floats);

         for (int i = 0; i < this.intValues.length; i++) {
            this.intValues[i][0] = intses[i][0];
            this.intValues[i][1] = intses[i][1];
         }

         this.intValue = this.intValues.length;
         this.cursor = 0;
      }

      int[] intValue = this.intValues[this.cursor++];
      return new int[]{intValue[0], intValue[1]};
   }

   @Override
   public void reset() {
      this.aimPolicyRunner.reset();
      this.intValue = 0;
      this.cursor = 0;
   }

   @Override
   public String method2866() {
      return "policy";
   }
}
