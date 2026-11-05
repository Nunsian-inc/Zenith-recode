package org.zenith.utility.misc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.zenith.utility.network.Cloud;

public final class ServerNameUtils {
   private static final Map<String, String> items = new LinkedHashMap<>();

   private ServerNameUtils() {
   }

   public static String display(String server) {
      String str = normalize(server);
      if (str.isEmpty()) {
         return "";
      } else if (str.startsWith("singleplayer")) {
         return "одиночная игра";
      } else if (str.equals("realms")) {
         return "Realms";
      } else {
         String str2 = items.get(str);
         return str2 == null ? str : str2;
      }
   }

   public static String normalize(String ip) {
      if (ip == null) {
         return "";
      } else {
         String lowerCase = ip.trim().toLowerCase(Locale.ROOT);
         if (!lowerCase.isEmpty() && !lowerCase.startsWith("singleplayer") && !lowerCase.equals("realms")) {
            if (lowerCase.endsWith(":25565")) {
               lowerCase = lowerCase.substring(0, lowerCase.length() - 6);
            }

            return Cloud.method7912(lowerCase);
         } else {
            return lowerCase;
         }
      }
   }

   public static boolean method5640(String string) {
      if (string != null && !string.isBlank()) {
         String lowerCase = string.toLowerCase(Locale.ROOT);
         return lowerCase.contains("анарх") || lowerCase.contains("anarch");
      } else {
         return false;
      }
   }

   public static String method5641(String string) {
      if (string == null) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(string.length());
         boolean flag = false;

         for (int i = 0; i < string.length() && builder.length() < 24; i++) {
            char charAt = string.charAt(i);
            boolean flag2 = Character.isLetterOrDigit(charAt) || charAt == '-' || charAt == '_' || charAt == '.' || charAt == '#';
            if (flag2) {
               builder.append(charAt);
               flag = false;
            } else if (!flag && builder.length() > 0) {
               builder.append(' ');
               flag = true;
            }
         }

         return builder.toString().strip();
      }
   }

   public static String method5642(String ip, String string) {
      String str = normalize(ip);
      if (str.startsWith("funtime.") && method5640(string)) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < string.length(); i++) {
            char charAt = string.charAt(i);
            if (charAt >= '0' && charAt <= '9') {
               builder.append(charAt);
            } else if (builder.length() > 0) {
               break;
            }
         }

         return builder.length() != 0 && builder.length() <= 4 ? "/an" + builder : "";
      } else {
         return "";
      }
   }

   public static String method5643(String server, String mode) {
      String anotherString = display(server);
      String str = method5641(mode);
      if (anotherString.isEmpty()) {
         return str;
      } else {
         return !str.isEmpty() && !str.equalsIgnoreCase(anotherString) ? anotherString + " · " + str : anotherString;
      }
   }

   static {
      items.put("funtime.su", "FunTime");
      items.put("funtime.ru", "FunTime");
      items.put("holyworld.ru", "HolyWorld");
      items.put("reallyworld.ru", "ReallyWorld");
      items.put("masedworld.net", "MasedWorld");
      items.put("mineblaze.net", "MineBlaze");
      items.put("cheatmine.ru", "CheatMine");
      items.put("dexland.ru", "DexLand");
      items.put("simpleage.ru", "SimpleAge");
      items.put("hypixel.net", "Hypixel");
      items.put("2b2t.org", "2b2t");
   }
}
