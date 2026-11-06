package org.zenith.client.screens.nlgui;

import org.zenith.utility.misc.MouseButton;

public interface MouseBindTarget {
   boolean bindMouse(MouseButton enum2);

   static boolean bindable(MouseButton enum2) {
      return enum2 != null && enum2.getGlfwButton() >= MouseButton.MIDDLE.getGlfwButton();
   }
}
