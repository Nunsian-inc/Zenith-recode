package org.zenith.base.bot.modules;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.ModuleInfo;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.modules.impl.BotAutoCapcha;
import org.zenith.base.bot.modules.impl.BotAutoMine;
import org.zenith.base.bot.modules.impl.BotWarpFarm;

public final class BotModuleManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<String> SUPPORTED_MODULE_NAMES = List.of(
      BotAutoMine.class.getAnnotation(ModuleInfo.class).name(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).name()
   );
   private static final Map<String, String> SUPPORTED_MODULE_DESCRIPTIONS = Map.of(
      BotAutoMine.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoMine.class.getAnnotation(ModuleInfo.class).description(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).name(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).description(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).description()
   );
   private final BotClient client;
   private final Map<String, BotModule> modules = new LinkedHashMap<>();

   public static List<String> supportedModuleNames() {
      return SUPPORTED_MODULE_NAMES;
   }

   @Nullable
   public static String supportedModuleDescription(String supportedModuleName) {
      return supportedModuleName == null ? null : SUPPORTED_MODULE_DESCRIPTIONS.get(supportedModuleName);
   }

   public BotModuleManager(BotClient client) {
      this.client = client;
      this.register(new BotAutoMine());
      this.register(new BotWarpFarm());
      BotAutoCapcha botAutoCapcha = new BotAutoCapcha();
      this.register(botAutoCapcha);
      botAutoCapcha.setToggled(true);
   }

   public void register(BotModule botAutoMine) {
      botAutoMine.bind(this.client);
      this.modules.put(botAutoMine.getNameLower(), botAutoMine);
   }

   public Collection<BotModule> getModules() {
      return this.modules.values();
   }

   @Nullable
   public BotModule getModule(String supportedModuleName) {
      return this.modules.get(supportedModuleName.toLowerCase(Locale.ROOT));
   }

   @Nullable
   public <T extends BotModule> T getModule(Class<T> clazz) {
      for (BotModule botModule : this.modules.values()) {
         if (clazz.isInstance(botModule)) {
            return (T)botModule;
         }
      }

      return null;
   }

   public boolean setEnabled(String supportedModuleName, boolean equalsIgnoreCase) {
      BotModule botModule = this.getModule(supportedModuleName);
      if (botModule == null) {
         return false;
      } else {
         botModule.setToggled(equalsIgnoreCase);
         return true;
      }
   }

   public void applySettings(String supportedModuleName, JsonObject jsonObject) {
      BotModule botModule = this.getModule(supportedModuleName);
      if (botModule != null) {
         botModule.load(jsonObject);
      }
   }

   public void disableAll() {
      for (BotModule botModule : this.modules.values()) {
         try {
            botModule.setToggled(false);
         } catch (RuntimeException e) {
            LOGGER.error("Bot {}: failed to disable module {}", new Object[]{this.client.name(), botModule.getName(), e});
         }
      }
   }
}
