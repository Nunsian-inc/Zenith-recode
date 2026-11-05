package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.mozilla.javascript.Function;
import org.zenith.ModuleCategory;
import org.zenith.base.Module;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.settings.Setting;

public final class Module1 extends Module {
   private final Script script;
   private final List<Setting> items = new ArrayList<>();
   private Function function;
   private Function function2;
   private boolean flag;
   public boolean flag2;
   public boolean flag3;

   public Module1(Script script, String name, String str, ModuleCategory category) {
      super(method7805(script, name), name, str, category);
      this.script = script;
   }

   public static Module1 method7802(Script workspace, String name, String str, ModuleCategory category) {
      return new Module1(workspace, name, str, category);
   }

   public static Module1 method7803(Script workspace, String str) {
      Module1 module1 = new Module1(workspace, "__" + str, "", ModuleCategory.MISC);
      module1.flag3 = true;
      return module1;
   }

   public void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   public void setFunction(Function function) {
      this.function = function;
   }

   public void setFunction2(Function function2) {
      this.function2 = function2;
   }

   public void method7804(Setting setting) {
      this.items.add(setting);
   }

   public static String method7805(Script script, String name) {
      return "script:" + script.getId() + ":" + name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9а-яё_-]", "_");
   }

   public Script getScript() {
      return this.script;
   }

   public void release() {
      this.flag = true;
   }

   @Override
   public String getDescription() {
      String description = super.getDescription();
      return description.isBlank() ? "Скрипт " + this.script.method1403() : description;
   }

   @Override
   public List<Setting> getSettings() {
      return Collections.unmodifiableList(this.items);
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && this.script.isRunning();
   }

   @Override
   public boolean isHidden() {
      return this.flag3;
   }

   @Override
   public void onEnable() {
      if (!this.flag3) {
         EventManager.call(new ModuleToggleEvent(this, true));
      }

      if (!this.flag && this.function != null) {
         this.script.getStorage().method1321(this.script, this.function);
      }
   }

   @Override
   public void onDisable() {
      if (!this.flag3) {
         EventManager.call(new ModuleToggleEvent(this, false));
      }

      if (!this.flag && this.function2 != null) {
         this.script.getStorage().method1321(this.script, this.function2);
      }
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this);
   }

   @Override
   public String toString() {
      return this.getId();
   }
}
