package dev.porno.playerAnim.core.util;

import java.util.function.Supplier;

public class SetableSupplier<T> implements Supplier<T> {
   T object;

   public SetableSupplier(T initialValue) {
      this.object = initialValue;
   }

   public void set(T object) {
      this.object = object;
   }

   @Override
   public T get() {
      return this.object;
   }
}
