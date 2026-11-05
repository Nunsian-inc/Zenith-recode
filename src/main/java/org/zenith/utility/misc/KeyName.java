package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public enum KeyName {
   KEY_SPACE("SPACE", 32),
   KEY_APOSTROPHE("APOSTROPHE", 39),
   KEY_COMMA("COMMA", 44),
   KEY_MINUS("MINUS", 45),
   KEY_PERIOD("PERIOD", 46),
   KEY_SLASH("SLASH", 47),
   KEY_0("0", 48),
   KEY_1("1", 49),
   KEY_2("2", 50),
   KEY_3("3", 51),
   KEY_4("4", 52),
   KEY_5("5", 53),
   KEY_6("6", 54),
   KEY_7("7", 55),
   KEY_8("8", 56),
   KEY_9("9", 57),
   KEY_SEMICOLON("SEMICOLON", 59),
   KEY_EQUAL("EQUAL", 61),
   KEY_A("A", 65),
   KEY_B("B", 66),
   KEY_C("C", 67),
   KEY_D("D", 68),
   KEY_E("E", 69),
   KEY_F("F", 70),
   KEY_G("G", 71),
   KEY_H("H", 72),
   KEY_I("I", 73),
   KEY_J("J", 74),
   KEY_K("K", 75),
   KEY_L("L", 76),
   KEY_M("M", 77),
   KEY_N("N", 78),
   KEY_O("O", 79),
   KEY_P("P", 80),
   KEY_Q("Q", 81),
   KEY_R("R", 82),
   KEY_S("S", 83),
   KEY_T("T", 84),
   KEY_U("U", 85),
   KEY_V("V", 86),
   KEY_W("W", 87),
   KEY_X("X", 88),
   KEY_Y("Y", 89),
   KEY_Z("Z", 90),
   KEY_LEFT_BRACKET("LEFT_BRACKET", 91),
   KEY_BACKSLASH("BACKSLASH", 92),
   KEY_RIGHT_BRACKET("RIGHT_BRACKET", 93),
   KEY_GRAVE_ACCENT("GRAVE_ACCENT", 96),
   KEY_ESCAPE("ESCAPE", 256),
   KEY_ENTER("ENTER", 257),
   KEY_TAB("TAB", 258),
   KEY_BACKSPACE("BACKSPACE", 259),
   KEY_INSERT("INSERT", 260),
   KEY_DELETE("DELETE", 261),
   KEY_RIGHT("RIGHT", 262),
   KEY_LEFT("LEFT", 263),
   KEY_DOWN("DOWN", 264),
   KEY_UP("UP", 265),
   KEY_PAGE_UP("PAGE_UP", 266),
   KEY_PAGE_DOWN("PAGE_DOWN", 267),
   KEY_HOME("HOME", 268),
   KEY_END("END", 269),
   KEY_CAPS_LOCK("CAPS_LOCK", 280),
   KEY_SCROLL_LOCK("SCROLL_LOCK", 281),
   KEY_NUM_LOCK("NUM_LOCK", 282),
   KEY_PRINT_SCREEN("PRINT_SCREEN", 283),
   KEY_PAUSE("PAUSE", 284),
   KEY_F1("F1", 290),
   KEY_F2("F2", 291),
   KEY_F3("F3", 292),
   KEY_F4("F4", 293),
   KEY_F5("F5", 294),
   KEY_F6("F6", 295),
   KEY_F7("F7", 296),
   KEY_F8("F8", 297),
   KEY_F9("F9", 298),
   KEY_F10("F10", 299),
   KEY_F11("F11", 300),
   KEY_F12("F12", 301),
   KEY_KP_0("NUMPAD_0", 320),
   KEY_KP_1("NUMPAD_1", 321),
   KEY_KP_2("NUMPAD_2", 322),
   KEY_KP_3("NUMPAD_3", 323),
   KEY_KP_4("NUMPAD_4", 324),
   KEY_KP_5("NUMPAD_5", 325),
   KEY_KP_6("NUMPAD_6", 326),
   KEY_KP_7("NUMPAD_7", 327),
   KEY_KP_8("NUMPAD_8", 328),
   KEY_KP_9("NUMPAD_9", 329),
   KEY_KP_DECIMAL("NUMPAD_DECIMAL", 330),
   KEY_KP_DIVIDE("NUMPAD_DIVIDE", 331),
   KEY_KP_MULTIPLY("NUMPAD_MULTIPLY", 332),
   KEY_KP_SUBTRACT("NUMPAD_SUBTRACT", 333),
   KEY_KP_ADD("NUMPAD_ADD", 334),
   KEY_KP_ENTER("NUMPAD_ENTER", 335),
   KEY_KP_EQUAL("NUMPAD_EQUAL", 336),
   KEY_LEFT_SUPER("LEFT_SUPER", 343),
   KEY_RIGHT_SUPER("RIGHT_SUPER", 347),
   KEY_MENU("MENU", 348),
   KEY_WORLD_1("WORLD_1", 161),
   KEY_WORLD_2("WORLD_2", 162),
   KEY_LEFT_SHIFT("LEFT_SHIFT", 340),
   KEY_LEFT_CONTROL("LEFT_CONTROL", 341),
   KEY_LEFT_ALT("LEFT_ALT", 342),
   KEY_RIGHT_SHIFT("RIGHT_SHIFT", 344),
   KEY_RIGHT_CONTROL("RIGHT_CONTROL", 345),
   KEY_RIGHT_ALT("RIGHT_ALT", 346),
   MOUSE_1("MOUSE1", 0),
   MOUSE_2("MOUSE2", 1),
   MOUSE_3("MOUSE3", 2),
   MOUSE_4("MOUSE4", 3),
   MOUSE_5("MOUSE5", 4),
   KEY_NONE("", -1);

   public final String text;
   public final int intValue;
   private static final Map<Integer, KeyName> items = new HashMap<>();
   private static final Map<String, KeyName> items2 = new HashMap<>();
   public static final int CONST_intValue = 15;

   @Override
   public String toString() {
      return this.text;
   }

   public static int method6698(int keyModifiers) {
      return keyModifiers & 15;
   }

   public static int method6699(int keyCode) {
      return switch (keyCode) {
         case 340, 344 -> 1;
         case 341, 345 -> 2;
         case 342, 346 -> 4;
         case 343, 347 -> 8;
         default -> 0;
      };
   }

   public static int method6700(int keyCode, int modifiers) {
      return method6698(modifiers) & ~method6699(keyCode);
   }

   public static String method6701(int keyCode) {
      KeyName keyName = items.get(keyCode);
      if (keyName != null && !keyName.text.isEmpty()) {
         return keyName.text;
      } else if (keyCode == -1) {
         return "";
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null && minecraft.getWindow() != null) {
            String str = org.zenith.utility.misc.KeyCompat.keyName(keyCode);
            return str != null && !str.isBlank() ? str.toUpperCase(Locale.ROOT) : "KEY " + keyCode;
         } else {
            return "KEY " + keyCode;
         }
      }
   }

   public static String method6702(int keyCode, int keyModifiers) {
      String str = method6701(keyCode);
      if (str.isEmpty()) {
         return "";
      } else {
         int n = method6698(keyModifiers);
         if (n == 0) {
            return str;
         } else {
            StringBuilder builder = new StringBuilder();
            if ((n & 2) != 0) {
               builder.append("CTRL + ");
            }

            if ((n & 1) != 0) {
               builder.append("SHIFT + ");
            }

            if ((n & 4) != 0) {
               builder.append("ALT + ");
            }

            if ((n & 8) != 0) {
               builder.append("SUPER + ");
            }

            return builder.append(str).toString();
         }
      }
   }

   public static boolean method6703(int keyCode, int keyModifiers, int n, int modifiers) {
      return keyCode != -1 && keyCode == n ? method6700(n, modifiers) == method6698(keyModifiers) : false;
   }

   public static int method6704(String string) {
      return items2.getOrDefault(string.toLowerCase(), KEY_NONE).intValue;
   }

   public static boolean method6705(int glfwKey) {
      return org.zenith.utility.misc.KeyCompat.isKeyDown(glfwKey);
   }

   @Generated
   private KeyName(String text, int intValue) {
      this.text = text;
      this.intValue = intValue;
   }

   static {
      for (KeyName keyName : values()) {
         items.put(keyName.intValue, keyName);
         items2.put(keyName.text.toLowerCase(), keyName);
      }
   }
}
