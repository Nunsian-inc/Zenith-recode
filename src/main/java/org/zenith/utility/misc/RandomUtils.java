package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {
   private RandomUtils() {
   }

   public static int nextIntInclusive(int min, int max) {
      if (min > max) {
         throw new IllegalArgumentException("min > max");
      } else {
         return ThreadLocalRandom.current().nextInt(min, max + 1);
      }
   }

   public static int randomIntInclusive(int min, int max) {
      return nextIntInclusive(min, max);
   }

   public static boolean rollRandomChance(int min, int max) {
      int chancePercent = nextIntInclusive(min, max);
      return chancePercent > ThreadLocalRandom.current().nextInt(100);
   }

   public static <T> List<T> shuffledCopy(List<T> list) {
      var shuffled = new ArrayList<>(list);
      Collections.shuffle(shuffled, (Random)ThreadLocalRandom.current());
      return shuffled;
   }

   public static <T> void shuffleInPlace(List<T> list) {
      Collections.shuffle(list, (Random)ThreadLocalRandom.current());
   }
}
