package org.zenith.client.screens.nlgui;


public final class BindCapture {
   private static Object owner;

   private BindCapture() {
   }

   public static void begin(Object obj) {
      owner = obj;
   }

   public static void end(Object obj) {
      if (owner == obj) {
         owner = null;
      }
   }

   public static void reset() {
      owner = null;
   }

   public static boolean isActive() {
      return owner != null;
   }

   public static Object owner() {
      return owner;
   }
}
