package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Util6 {
   private Util6() {
   }

   public static int method1439(int origin, int n) {
      if (origin > n) {
         throw new IllegalArgumentException("min > max");
      } else {
         return ThreadLocalRandom.current().nextInt(origin, n + 1);
      }
   }

   public static int method1440(int origin, int n) {
      return method1439(origin, n);
   }

   public static boolean method1441(int origin, int n) {
      int n2 = method1439(origin, n);
      return n2 > ThreadLocalRandom.current().nextInt(100);
   }

   public static <T> List<T> method1442(List<T> list) {
      var arrayList = new ArrayList<>(list);
      Collections.shuffle(arrayList, (Random)ThreadLocalRandom.current());
      return arrayList;
   }

   public static <T> void method1443(List<T> list) {
      Collections.shuffle(list, (Random)ThreadLocalRandom.current());
   }
}
