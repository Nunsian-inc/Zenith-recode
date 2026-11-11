package org.zenith.utility.render;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.world.inventory.InventoryMenu;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.screens.bot.BotScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

public final class KeyBindUtils implements MinecraftAccess {
   public static final List<KeyMapping> items = List.of(mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump);
   private static final Map<Class<?>, TaskScheduler.Entry2> items2 = new HashMap<>();

   public static void method2627(Class<?> classValue, Runnable runnable) {
      method2628(classValue, runnable, 0);
   }

   public static void method2628(Class<?> classValue, Runnable runnable, int n) {
      TaskScheduler taskScheduler = Zenith.getInstance().getTaskScheduler();
      TaskScheduler.Entry2 task = taskScheduler.method5109(classValue)
         .method5121(
            MoveInputEvent.class,
            var1x -> {
               if (InventorySettingModule.INSTANCE.method2397()) {
                  var1x.clearInput();
                  if (mc.player.lastSentInput.jump()
                     || mc.player.isSprinting()
                     || mc.player.lastSentInput.forward()
                     || mc.player.lastSentInput.backward()
                     || mc.player.lastSentInput.left()
                     || mc.player.lastSentInput.right()) {
                     return false;
                  }
               }

               runnable.run();
               InventoryUtils.closeScreen();
               return true;
            },
            n
         );
      taskScheduler.method5111(task, n);
   }

   public static boolean method2629(Class<?> clazz) {
      return Zenith.getInstance().getTaskScheduler().method5117(clazz);
   }

   public static boolean method2630(Class<?> clazz) {
      return clazz == AutoTotemModule.class ? !AutoTotemModule.INSTANCE.method725() : method2629(clazz);
   }

   public static void method2631() {
      method2633();
   }

   public static void method2632() {
      method2634();
   }

   public static void method2633() {
      items.forEach(keyMapping -> keyMapping.setDown(false));
   }

   public static void method2634() {
      items.forEach(keyMapping -> keyMapping.setDown(InputConstants.isKeyDown(keyMapping.getDefaultKey().getValue())));
   }

   public static boolean method2635() {
      if (mc.gui.screen() == null) {
         return false;
      } else if (!Zenith.getInstance().getTaskScheduler().method5116()) {
         return false;
      } else if (UseItemUtils.method6525(mc.gui.screen())) {
         return false;
      } else if (mc.gui.screen() instanceof SignEditScreen) {
         return false;
      } else if (mc.gui.screen() instanceof AnvilScreen) {
         return false;
      } else if (mc.gui.screen() instanceof AbstractCommandBlockEditScreen) {
         return false;
      } else if (mc.gui.screen() instanceof StructureBlockEditScreen) {
         return false;
      } else if (mc.gui.screen() instanceof BotScreen botScreen) {
         return !botScreen.isSearch();
      } else {
         return mc.gui.screen() instanceof NLMenuScreen nlMenuScreen ? !nlMenuScreen.isSearch() : mc.player.containerMenu instanceof InventoryMenu;
      }
   }

   @Generated
   private KeyBindUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
