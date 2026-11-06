package org.zenith.utility.misc;

import lombok.Generated;

public enum MouseButton {
   LEFT(0),
   RIGHT(1),
   MIDDLE(2),
   BUTTON_4(3),
   BUTTON_5(4),
   BUTTON_6(5),
   BUTTON_7(6);

   private final int glfwButton;

   public static MouseButton fromGlfwButton(int glfwButton) {
      for (MouseButton button : values()) {
         if (button.getGlfwButton() == glfwButton) {
            return button;
         }
      }

      return LEFT;
   }

   @Generated
   private MouseButton(int glfwButton) {
      this.glfwButton = glfwButton;
   }

   @Generated
   public int getGlfwButton() {
      return this.glfwButton;
   }
}
