package org.zenith.utility.misc;

import java.util.function.Supplier;
import org.zenith.Rotation;

public final class RotationRequest {
   private final Rotation rotation;
   private final Supplier<Rotation> supplier;
   private final RotationConfig rotationConfig;

   public Rotation rotation() {
      return this.rotation;
   }

   public Supplier<Rotation> supplier() {
      return this.supplier;
   }

   public RotationConfig rotationConfig() {
      return this.rotationConfig;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof RotationRequest other)) return false;
      return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.supplier, other.supplier) && java.util.Objects.equals(this.rotationConfig, other.rotationConfig);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(rotation, supplier, rotationConfig);
   }

   @Override
   public String toString() {
      return "RotationRequest[" + "rotation=" + this.rotation + ", " + "supplier=" + this.supplier + ", " + "rotationConfig=" + this.rotationConfig + "]";
   }

   public RotationRequest(Rotation rotation, Supplier<Rotation> supplier, RotationConfig rotationConfig) {
      this.rotation = rotation;
      this.supplier = supplier;
      this.rotationConfig = rotationConfig;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public Supplier<Rotation> getSupplier() {
      return this.supplier;
   }

   public RotationConfig getRotationConfig() {
      return this.rotationConfig;
   }
}
