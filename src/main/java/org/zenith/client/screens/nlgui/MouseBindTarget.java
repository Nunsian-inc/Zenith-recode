package org.zenith.client.screens.nlgui;

import org.zenith.utility.misc.Enum3;

public interface MouseBindTarget {
   boolean bindMouse(Enum3 enum2);

   static boolean bindable(Enum3 enum2) {
      return enum2 != null && enum2.getIntValue() >= Enum3.MIDDLE.getIntValue();
   }
}
