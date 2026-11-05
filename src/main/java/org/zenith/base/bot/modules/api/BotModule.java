package org.zenith.base.bot.modules.api;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotEventBus;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.settings.Setting;

public abstract class BotModule {
   public final ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   private final String name = this.info.name();
   private final String nameLower = this.name.toLowerCase(Locale.ROOT);
   private final ModuleCategory category = this.info.category();
   private final String description = this.info.description();
   private volatile boolean enabled;
   private BotClient bot;
   private BotEventBus eventBus;

   public final void bind(BotClient bot) {
      this.bot = bot;
      this.eventBus = bot.getEventBus();
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public void setToggled(boolean toggled) {
      if (toggled && !this.enabled) {
         this.toggle();
      }

      if (!toggled && this.enabled) {
         this.toggle();
      }
   }

   public final void disableSelf() {
      this.setToggled(false);
   }

   public void onEnable() {
      this.eventBus.register(this);
   }

   public void onDisable() {
      this.eventBus.unregister(this);
   }

   public final BotClient bot() {
      return this.bot;
   }

   @Nullable
   public final BotWorld world() {
      return this.bot.getWorld();
   }

   @Nullable
   public final BotPlayer player() {
      return this.bot.getPlayer();
   }

   @Nullable
   public final BotPlayHandler handler() {
      return this.bot.getPlayHandler();
   }

   @Nullable
   public final BotInteractionManager interaction() {
      BotPlayHandler playHandler = this.bot.getPlayHandler();
      return playHandler != null && playHandler.getWorld() != null ? playHandler.getInteractionManager() : null;
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(field -> {
         try {
            field.setAccessible(true);
            return field.get(this);
         } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
         }
      }).filter(obj -> obj instanceof Setting).map(obj -> (Setting)obj).collect(Collectors.toList());
   }

   public List<Setting> getUiSettings() {
      return this.getSettings();
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      json.addProperty("enabled", this.enabled);
      JsonObject jsonObject = new JsonObject();

      for (Setting setting : this.getSettings()) {
         setting.safe(jsonObject);
      }

      json.add("Settings", jsonObject);
      return json;
   }

   public void load(JsonObject jsonObject) {
      if (jsonObject != null) {
         JsonObject json = null;

         try {
            json = jsonObject.getAsJsonObject("Settings");
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (json != null) {
            for (Setting setting : this.getSettings()) {
               if (json.has(setting.getKey())) {
                  try {
                     setting.load(json);
                  } catch (Exception e) {
                     System.err.println("[BotConfig] bad value " + this.name + "#" + setting.getKey() + ": " + e);
                  }
               }
            }
         }

         try {
            if (jsonObject.has("enabled")) {
               this.setToggled(jsonObject.get("enabled").getAsBoolean());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Generated
   public ModuleInfo getInfo() {
      return this.info;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getNameLower() {
      return this.nameLower;
   }

   @Generated
   public ModuleCategory getCategory() {
      return this.category;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }

   @Generated
   public BotClient getBot() {
      return this.bot;
   }

   @Generated
   public BotEventBus getEventBus() {
      return this.eventBus;
   }
}
