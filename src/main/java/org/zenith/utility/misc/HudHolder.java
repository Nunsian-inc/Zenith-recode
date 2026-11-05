package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.chat.Component;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.hud.InterfaceHud;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.utility.network.UseItemUtils;

public class HudHolder {
   private static volatile HudHolder INSTANCE;
   private InterfaceHud interfaceHud;

   public HudHolder() {
      INSTANCE = this;
      EventManager.register(this);
   }

   public static HudHolder method6969() {
      Zenith zenith = Zenith.getInstance();
      HudHolder hudHolder = zenith == null ? null : zenith.getHudHolder();
      if (hudHolder != null) {
         return hudHolder;
      } else {
         HudHolder hudHolder2 = INSTANCE;
         if (hudHolder2 == null) {
            synchronized (HudHolder.class) {
               hudHolder2 = INSTANCE;
               if (hudHolder2 == null) {
                  hudHolder2 = new HudHolder();
               }
            }
         }

         return hudHolder2;
      }
   }

   public void setInterfaceHud(InterfaceHud interfaceHud) {
      this.interfaceHud = interfaceHud;
   }

   @EventTarget
   public void method6970(ModuleToggleEvent event) {
      if (event.getModule() != MenuModule.INSTANCE && !UseItemUtils.method6526()) {
         try {
            Zenith.getInstance().getClientSounds().method1565(event.isEnabled());
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (this.interfaceHud != null && this.interfaceHud.method5083() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814()) {
            this.interfaceHud.method5084(event.getModule(), event.isEnabled());
         }
      }
   }

   public void method6971(Module moduleValue, boolean enabled) {
      if (this.interfaceHud != null
         && this.interfaceHud.method5083()
         && !UseItemUtils.method6526()
         && InterfaceModule.INSTANCE.isEnabled()
         && InterfaceModule.INSTANCE.method6814()) {
         this.interfaceHud.method5084(moduleValue, enabled);
      }
   }

   public void method6972(String icon, Component literal) {
      if (this.interfaceHud != null && !UseItemUtils.method6526() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814()) {
         this.interfaceHud.method5086(icon, literal);
      }
   }

   public void method6973(String icon, Component literal, long l) {
      if (this.interfaceHud != null && !UseItemUtils.method6526() && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6814()) {
         this.interfaceHud.method5087(icon, literal, l);
      }
   }
}
