package org.zenith.base.bot.modules;

import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.client.settings.Setting;

@ModuleInfo(
   name = "BotModuleUiAdapter",
   category = ModuleCategory.PLAYER,
   description = "",
   method894 = true
)
public final class BotModuleUiAdapter extends Module {
   private final String botName;
   private final BotModule module;

   public BotModuleUiAdapter(String botName, BotModule botModule) {
      this.botName = botName;
      this.module = botModule;
      this.setName(botModule.getName());
   }

   @Override
   public boolean isEnabled() {
      return this.module.isEnabled();
   }

   @Override
   public boolean isEnabledRaw() {
      return this.module.isEnabled();
   }

   @Override
   public void toggle() {
      HeadlessBots.setModuleEnabled(this.botName, this.module.getName(), !this.module.isEnabled());
   }

   @Override
   public void setToggled(boolean toggled) {
      if (toggled != this.module.isEnabled()) {
         this.toggle();
      }
   }

   @Override
   public List<Setting> getSettings() {
      return this.module.getUiSettings();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof BotModuleUiAdapter botModuleUiAdapter && botModuleUiAdapter.module == this.module;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.module);
   }
}
