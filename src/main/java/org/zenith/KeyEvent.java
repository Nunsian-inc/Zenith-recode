package org.zenith;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;

public final class KeyEvent implements Event {
   private final int intValue;
   private final int keyCode;
   private final int modifiers;

   public boolean isKey(int keyCode) {
      return keyCode == this.keyCode;
   }

   public boolean isPress(int keyCode) {
      return this.isPress(keyCode, MinecraftAccess.mc.gui.screen() == null);
   }

   public boolean isPress(int keyCode, boolean flag) {
      return this.keyCode == keyCode && this.intValue == 1 && flag;
   }

   public boolean isRelease(int keyCode) {
      return this.isRelease(keyCode, MinecraftAccess.mc.gui.screen() == null);
   }

   public boolean isRelease(int keyCode, boolean flag) {
      return this.keyCode == keyCode && this.intValue == 0 && flag;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getKeyCode() {
      return this.keyCode;
   }

   @Generated
   public int getModifiers() {
      return this.modifiers;
   }

   @Generated
   public KeyEvent(int intValue, int keyCode, int modifiers) {
      this.intValue = intValue;
      this.keyCode = keyCode;
      this.modifiers = modifiers;
   }
}
