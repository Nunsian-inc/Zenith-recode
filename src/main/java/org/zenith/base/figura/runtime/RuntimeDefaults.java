package org.zenith.base.figura.runtime;

import java.util.List;
import java.util.stream.IntStream;

public final class RuntimeDefaults {
   public static final RuntimeDefaults.Value<Boolean> FORCE_SMOOTH_AVATAR = new RuntimeDefaults.Value<>(false);
   public static final RuntimeDefaults.EnumValue FORMAT_SCRIPT = new RuntimeDefaults.EnumValue(1, 4);
   public static final RuntimeDefaults.EnumValue IRIS_COMPATIBILITY_FIX = new RuntimeDefaults.EnumValue(1, 3);
   public static final RuntimeDefaults.EnumValue RENDER_DEBUG_PARTS_PIVOT = new RuntimeDefaults.EnumValue(1, 3);
   public static final RuntimeDefaults.Value<Boolean> ALLOW_FP_HANDS = new RuntimeDefaults.Value<>(false);
   public static final RuntimeDefaults.Value<Boolean> FIRST_PERSON_MATRICES = new RuntimeDefaults.Value<>(true);
   public static final RuntimeDefaults.Value<Boolean> REDUCED_MOTION = new RuntimeDefaults.Value<>(false);
   public static final RuntimeDefaults.EnumValue LOG_LOCATION = new RuntimeDefaults.EnumValue(0, 2);
   public static final RuntimeDefaults.Value<Integer> LOG_NUMBER_LENGTH = new RuntimeDefaults.Value<>(5);
   public static final RuntimeDefaults.Value<Boolean> LOG_OTHERS = new RuntimeDefaults.Value<>(false);
   public static final RuntimeDefaults.Value<Boolean> CHAT_MESSAGES = new RuntimeDefaults.Value<>(true);

   private RuntimeDefaults() {
   }

   public static final class EnumValue extends RuntimeDefaults.Value<Integer> {
      public final List<Integer> enumList;

      private EnumValue(int value, int entries) {
         super(value);
         this.enumList = IntStream.range(0, entries).boxed().toList();
      }
   }

   public static class Value<T> {
      public T value;

      public Value(T value) {
         this.value = value;
      }
   }
}
