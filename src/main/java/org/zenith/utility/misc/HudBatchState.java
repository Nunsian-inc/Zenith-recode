package org.zenith.utility.misc;

import org.zenith.utility.render.ScissorFlusher;

public final class HudBatchState {
   public static boolean enabled = true;
   private static int depth;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static boolean flag4;

   private HudBatchState() {
   }

   public static boolean isActive() {
      return enabled && depth > 0 && !flag4;
   }

   public static void begin() {
      if (enabled) {
         depth++;
      }
   }

   public static void end() {
      if (depth != 0) {
         depth--;
         if (depth <= 0) {
            method7759();
            method7760();
            ScissorFlusher.flush();
         }
      }
   }

   public static void method7758() {
      flag = true;
   }

   public static void method7759() {
      if (flag && !flag4) {
         // 26.3 has no shared immediate BufferSource to flush; text is drawn by MsdfRenderer itself
         flag = false;

         method7760();
      }
   }

   private static void method7760() {
      if (flag3) {
         flag3 = false;
      }
   }
}
