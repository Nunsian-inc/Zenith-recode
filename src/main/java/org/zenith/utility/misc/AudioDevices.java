package org.zenith.utility.misc;

import java.util.List;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALUtil;

public final class AudioDevices {
   private static final String text = "OpenAL Soft on ";

   private AudioDevices() {
   }

   public static List<String> method3219() {
      try {
         if (!ALC10.alcIsExtensionPresent(0L, "ALC_EXT_CAPTURE")) {
            return List.of();
         } else {
            var stringList = ALUtil.getStringList(0L, 784);
            return stringList == null ? List.of() : List.copyOf(stringList);
         }
      } catch (RuntimeException e) {
         return List.of();
      }
   }

   public static List<String> method3220() {
      try {
         List stringList;
         if (ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
            stringList = ALUtil.getStringList(0L, 4115);
         } else if (ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATION_EXT")) {
            stringList = ALUtil.getStringList(0L, 4101);
         } else {
            stringList = null;
         }

         return stringList == null ? List.of() : List.copyOf(stringList);
      } catch (RuntimeException e) {
         return List.of();
      }
   }

   public static String method3221() {
      try {
         String alcGetString = ALC10.alcGetString(0L, 785);
         return alcGetString == null ? "" : alcGetString;
      } catch (RuntimeException e) {
         return "";
      }
   }

   public static String method3222() {
      try {
         String alcGetString = ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT") ? ALC10.alcGetString(0L, 4114) : ALC10.alcGetString(0L, 4100);
         return alcGetString == null ? "" : alcGetString;
      } catch (RuntimeException e) {
         return "";
      }
   }

   public static String display(String key) {
      return key.startsWith("OpenAL Soft on ") ? key.substring("OpenAL Soft on ".length()) : key;
   }
}
