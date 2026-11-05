package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.sdl.SDLMouse;

// 26.3 switched input from GLFW to SDL: KeyEvent#key is an SDL scancode, mouse buttons are SDL_BUTTON_*,
// modifiers are SDL_KMOD_*. Zenith (decompiled) compares against inlined GLFW literals and stores GLFW codes
// in its configs, so Zenith keeps GLFW codes internally and converts at the vanilla boundary.
// Table generated from InputConstants KEY_* of 1.21.11 (GLFW) and 26.3 (SDL).
public final class KeyCompat {
   private static final int[] PAIRS = {
      32, 44, // KEY_SPACE
      39, 52, // KEY_APOSTROPHE
      44, 54, // KEY_COMMA
      45, 45, // KEY_MINUS
      46, 55, // KEY_PERIOD
      47, 56, // KEY_SLASH
      48, 39, // KEY_0
      49, 30, // KEY_1
      50, 31, // KEY_2
      51, 32, // KEY_3
      52, 33, // KEY_4
      53, 34, // KEY_5
      54, 35, // KEY_6
      55, 36, // KEY_7
      56, 37, // KEY_8
      57, 38, // KEY_9
      59, 51, // KEY_SEMICOLON
      61, 46, // KEY_EQUALS
      65, 4, // KEY_A
      66, 5, // KEY_B
      67, 6, // KEY_C
      68, 7, // KEY_D
      69, 8, // KEY_E
      70, 9, // KEY_F
      71, 10, // KEY_G
      72, 11, // KEY_H
      73, 12, // KEY_I
      74, 13, // KEY_J
      75, 14, // KEY_K
      76, 15, // KEY_L
      77, 16, // KEY_M
      78, 17, // KEY_N
      79, 18, // KEY_O
      80, 19, // KEY_P
      81, 20, // KEY_Q
      82, 21, // KEY_R
      83, 22, // KEY_S
      84, 23, // KEY_T
      85, 24, // KEY_U
      86, 25, // KEY_V
      87, 26, // KEY_W
      88, 27, // KEY_X
      89, 28, // KEY_Y
      90, 29, // KEY_Z
      91, 47, // KEY_LBRACKET
      92, 49, // KEY_BACKSLASH
      93, 48, // KEY_RBRACKET
      96, 53, // KEY_GRAVE
      256, 41, // KEY_ESCAPE
      257, 40, // KEY_RETURN
      258, 43, // KEY_TAB
      259, 42, // KEY_BACKSPACE
      260, 73, // KEY_INSERT
      261, 76, // KEY_DELETE
      262, 79, // KEY_RIGHT
      263, 80, // KEY_LEFT
      264, 81, // KEY_DOWN
      265, 82, // KEY_UP
      266, 75, // KEY_PAGEUP
      267, 78, // KEY_PAGEDOWN
      268, 74, // KEY_HOME
      269, 77, // KEY_END
      280, 57, // KEY_CAPSLOCK
      281, 71, // KEY_SCROLLLOCK
      282, 83, // KEY_NUMLOCK
      283, 70, // KEY_PRINTSCREEN
      284, 72, // KEY_PAUSE
      290, 58, // KEY_F1
      291, 59, // KEY_F2
      292, 60, // KEY_F3
      293, 61, // KEY_F4
      294, 62, // KEY_F5
      295, 63, // KEY_F6
      296, 64, // KEY_F7
      297, 65, // KEY_F8
      298, 66, // KEY_F9
      299, 67, // KEY_F10
      300, 68, // KEY_F11
      301, 69, // KEY_F12
      302, 104, // KEY_F13
      303, 105, // KEY_F14
      304, 106, // KEY_F15
      305, 107, // KEY_F16
      306, 108, // KEY_F17
      307, 109, // KEY_F18
      308, 110, // KEY_F19
      309, 111, // KEY_F20
      310, 112, // KEY_F21
      311, 113, // KEY_F22
      312, 114, // KEY_F23
      313, 115, // KEY_F24
      320, 98, // KEY_NUMPAD0
      321, 89, // KEY_NUMPAD1
      322, 90, // KEY_NUMPAD2
      323, 91, // KEY_NUMPAD3
      324, 92, // KEY_NUMPAD4
      325, 93, // KEY_NUMPAD5
      326, 94, // KEY_NUMPAD6
      327, 95, // KEY_NUMPAD7
      328, 96, // KEY_NUMPAD8
      329, 97, // KEY_NUMPAD9
      330, 220, // KEY_NUMPADCOMMA
      332, 85, // KEY_MULTIPLY
      334, 87, // KEY_ADD
      335, 88, // KEY_NUMPADENTER
      336, 103, // KEY_NUMPADEQUALS
      340, 225, // KEY_LSHIFT
      341, 224, // KEY_LCONTROL
      342, 226, // KEY_LALT
      343, 227, // KEY_LGUI
      344, 229, // KEY_RSHIFT
      345, 228, // KEY_RCONTROL
      346, 230, // KEY_RALT
      347, 231, // KEY_RGUI
   };
   private static final int[] TO_GLFW = new int[512];
   private static final int[] TO_SDL = new int[512];

   static {
      java.util.Arrays.fill(TO_GLFW, -1);
      java.util.Arrays.fill(TO_SDL, -1);
      for (int i = 0; i < PAIRS.length; i += 2) {
         TO_SDL[PAIRS[i]] = PAIRS[i + 1];
         TO_GLFW[PAIRS[i + 1]] = PAIRS[i];
      }
   }

   private KeyCompat() {
   }

   public static int toGlfw(int sdlScancode) {
      return sdlScancode >= 0 && sdlScancode < TO_GLFW.length ? TO_GLFW[sdlScancode] : -1;
   }

   public static int toSdl(int glfwKey) {
      int sdl = glfwKey >= 0 && glfwKey < TO_SDL.length ? TO_SDL[glfwKey] : -1;
      return sdl < 0 ? 0 : sdl; // 0 = SDL_SCANCODE_UNKNOWN (InputConstants.UNKNOWN)
   }

   // GLFW: 0 left, 1 right, 2 middle, 3.. extra. SDL: 1 left, 2 middle, 3 right, 4.. extra.
   public static int mouseToGlfw(int sdlButton) {
      return switch (sdlButton) {
         case 1 -> 0;
         case 2 -> 2;
         case 3 -> 1;
         default -> sdlButton - 1;
      };
   }

   public static int mouseToSdl(int glfwButton) {
      return switch (glfwButton) {
         case 0 -> 1;
         case 1 -> 3;
         case 2 -> 2;
         default -> glfwButton + 1;
      };
   }

   // GLFW_MOD_SHIFT 1, CONTROL 2, ALT 4, SUPER 8, CAPS_LOCK 16, NUM_LOCK 32
   public static int modsToGlfw(int sdlMods) {
      int m = 0;
      if ((sdlMods & 0x0003) != 0) m |= 1;
      if ((sdlMods & 0x00C0) != 0) m |= 2;
      if ((sdlMods & 0x0300) != 0) m |= 4;
      if ((sdlMods & 0x0C00) != 0) m |= 8;
      if ((sdlMods & 0x2000) != 0) m |= 16;
      if ((sdlMods & 0x1000) != 0) m |= 32;
      return m;
   }

   public static int modsToSdl(int glfwMods) {
      int m = 0;
      if ((glfwMods & 1) != 0) m |= 0x0001;
      if ((glfwMods & 2) != 0) m |= 0x0040;
      if ((glfwMods & 4) != 0) m |= 0x0100;
      if ((glfwMods & 8) != 0) m |= 0x0400;
      if ((glfwMods & 16) != 0) m |= 0x2000;
      if ((glfwMods & 32) != 0) m |= 0x1000;
      return m;
   }

   public static String keyName(int glfwKey) {
      int sdl = toSdl(glfwKey);
      return sdl <= 0 ? null : InputConstants.Type.KEYBOARD.getOrCreate(sdl).getDisplayName().getString();
   }

   public static boolean isKeyDown(int glfwKey) {
      int sdl = toSdl(glfwKey);
      return sdl > 0 && InputConstants.isKeyDown(sdl);
   }

   public static boolean isMouseDown(int glfwButton) {
      int sdl = mouseToSdl(glfwButton);
      return (SDLMouse.SDL_GetMouseState(null, null) & (1 << (sdl - 1))) != 0;
   }

   public static void main(String[] args) {
      assert toSdl(256) == 41 && toGlfw(41) == 256 : "escape";
      assert toSdl(65) == 4 && toGlfw(4) == 65 : "A";
      assert toSdl(341) == 224 && toSdl(340) == 225 : "lctrl/lshift";
      assert mouseToSdl(mouseToGlfw(3)) == 3 && mouseToGlfw(1) == 0 && mouseToGlfw(3) == 1 : "mouse";
      assert modsToGlfw(modsToSdl(15)) == 15 : "mods";
      for (int i = 0; i < PAIRS.length; i += 2) assert toGlfw(toSdl(PAIRS[i])) == PAIRS[i] : "roundtrip " + PAIRS[i];
      System.out.println("KeyCompat ok");
   }
}
