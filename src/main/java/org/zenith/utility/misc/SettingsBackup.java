package org.zenith.utility.misc;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.utils.IInputOverrideHandler;
import baritone.api.utils.input.Input;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.KeyMapping;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.UpdateEvent;

public final class SettingsBackup implements MinecraftAccess {
   private static final Input[] inputs = new Input[]{
      Input.MOVE_FORWARD, Input.MOVE_BACK, Input.MOVE_LEFT, Input.MOVE_RIGHT, Input.JUMP, Input.SNEAK, Input.SPRINT
   };
   private static Boolean booleanValue;
   private static Boolean booleanValue2;
   private static Boolean booleanValue3;
   private static Integer integer;
   private static Boolean booleanValue4;
   private static Double doubleValue;
   private static Double doubleValue2;
   private static boolean flag;

   private SettingsBackup() {
   }

   public static void apply() {
      Settings settings = BaritoneAPI.getSettings();
      if (settings != null) {
         if (booleanValue == null) {
            booleanValue = (Boolean)settings.allowBreak.value;
            booleanValue2 = (Boolean)settings.allowPlace.value;
            booleanValue3 = (Boolean)settings.allowParkourPlace.value;
            integer = (Integer)settings.maxFallHeightNoWater.value;
            booleanValue4 = (Boolean)settings.remainWithExistingLookDirection.value;
            doubleValue = (Double)settings.randomLooking.value;
            doubleValue2 = (Double)settings.randomLooking113.value;
         }

         settings.allowBreak.value = Boolean.FALSE;
         settings.allowPlace.value = Boolean.FALSE;
         settings.allowParkourPlace.value = Boolean.FALSE;
         settings.maxFallHeightNoWater.value = 6;
         settings.remainWithExistingLookDirection.value = Boolean.FALSE;
         settings.randomLooking.value = 0.6;
         settings.randomLooking113.value = 0.0;
      }
   }

   public static void restore() {
      Settings settings = BaritoneAPI.getSettings();
      if (settings != null && booleanValue != null) {
         settings.allowBreak.value = booleanValue;
         settings.allowPlace.value = booleanValue2;
         settings.allowParkourPlace.value = booleanValue3;
         settings.maxFallHeightNoWater.value = integer;
         settings.remainWithExistingLookDirection.value = booleanValue4;
         settings.randomLooking.value = doubleValue;
         settings.randomLooking113.value = doubleValue2;
      }

      booleanValue = null;
      booleanValue2 = null;
      booleanValue3 = null;
      integer = null;
      booleanValue4 = null;
      doubleValue = null;
      doubleValue2 = null;
   }

   private static void method2522() {
      if (mc.player != null && mc.options != null && BaritoneAPI.getProvider() != null) {
         if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            if (flag) {
               flag = false;

               for (Input input : inputs) {
                  KeyMapping keyMapping = method2523(input);
                  if (keyMapping != null) {
                     keyMapping.setDown(false);
                  }
               }
            }
         } else {
            IInputOverrideHandler inputOverrideHandler = BaritoneAPI.getProvider().getPrimaryBaritone().getInputOverrideHandler();
            flag = true;

            for (Input input : inputs) {
               if (input != Input.SPRINT || !mc.player.isInWater()) {
                  KeyMapping keyMapping = method2523(input);
                  if (keyMapping != null) {
                     keyMapping.setDown(inputOverrideHandler.isInputForcedDown(input));
                  }
               }
            }
         }
      }
   }

   private static KeyMapping method2523(Input input) {
      return switch (input) {
         case MOVE_FORWARD -> mc.options.keyUp;
         case MOVE_BACK -> mc.options.keyDown;
         case MOVE_LEFT -> mc.options.keyLeft;
         case MOVE_RIGHT -> mc.options.keyRight;
         case JUMP -> mc.options.keyJump;
         case SNEAK -> mc.options.keyShift;
         case SPRINT -> mc.options.keySprint;
         default -> null;
      };
   }

   static {
      EventManager.register(new Object() {
         @EventTarget
         public void onUpdate(UpdateEvent event) {
            SettingsBackup.method2522();
         }
      });
   }
}
