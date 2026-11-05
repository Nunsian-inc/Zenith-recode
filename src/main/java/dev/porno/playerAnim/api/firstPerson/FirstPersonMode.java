package dev.porno.playerAnim.api.firstPerson;

import lombok.Generated;
import org.jetbrains.annotations.ApiStatus.Internal;

public enum FirstPersonMode {
   NONE(false),
   VANILLA(true),
   THIRD_PERSON_MODEL(true),
   DISABLED(false);

   private final boolean enabled;
   private static final ThreadLocal<Boolean> firstPersonPass = ThreadLocal.withInitial(() -> false);

   FirstPersonMode(boolean enabled) {
      this.enabled = enabled;
   }

   public static boolean isFirstPersonPass() {
      return firstPersonPass.get();
   }

   @Internal
   public static void setFirstPersonPass(boolean newValue) {
      firstPersonPass.set(newValue);
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }
}
