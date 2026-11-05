package org.zenith.base;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.HolyWorldServerTracker;

public class Module implements Comparable<Module>, ZenithAccess {
   public ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   private final String text;
   private String name;
   private final String nameLower;
   private final ModuleCategory category;
   private final String description;
   private final boolean botModule;
   private volatile boolean enabled;
   private boolean priority = false;
   private int keyCode;
   private int keyModifiers;

   public Module() {
      if (this.info == null) {
         throw new IllegalStateException("ModuleAnnotation is required for " + this.getClass().getName());
      } else {
         this.text = "zenith:" + this.getClass().getSimpleName().toLowerCase(Locale.ROOT);
         this.name = this.info.name();
         this.nameLower = this.name.toLowerCase(Locale.ROOT);
         this.category = this.info.category();
         this.description = this.info.description();
         this.botModule = this.info.method894();
         this.enabled = false;
         this.keyCode = -1;
      }
   }

   public Module(String text, String name, String str, ModuleCategory category) {
      this.info = null;
      this.text = text;
      this.name = name;
      this.nameLower = name.toLowerCase(Locale.ROOT);
      this.category = category;
      this.description = str == null ? "" : str;
      this.botModule = false;
      this.enabled = false;
      this.keyCode = -1;
   }

   public boolean isEnabledRaw() {
      return this.enabled;
   }

   public String getDescription() {
      return this.description != null && !this.description.isBlank() ? Zenith.getInstance().getLocalization().translate(this.description) : "";
   }

   public boolean isBotModule() {
      return this.botModule;
   }

   public String getGroup() {
      return this.info == null ? "" : this.info.method895();
   }

   public boolean isHidden() {
      return this.info != null && this.info.method896();
   }

   public List<Module> getSurfacedModules() {
      return Collections.emptyList();
   }

   public ModulePreviewStage.Kind previewKind() {
      return null;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setToggled(boolean toggled) {
      if (toggled && !this.isEnabledRaw()) {
         this.toggle();
      }

      if (!toggled && this.isEnabledRaw()) {
         this.toggle();
      }
   }

   public final void disableSelf() {
      this.setToggled(false);
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public final RotationManager scopedRotationManager() {
      return Zenith.getInstance().getRotationManager();
   }

   public final HolyWorldServerTracker scopedRCTRepository() {
      return Zenith.getInstance().getHolyWorldServerTracker();
   }

   public void onEnable() {
      if (this.isPremium() && Zenith.getInstance().getEntry().method1504() == Zenith.Entry.Type.USER) {
         this.setToggled(false);
         Zenith.getInstance()
            .getHudHolder()
            .method6972(
               this.category.getIcon(),
               Component.translationArg(
                  Component.nullToEmpty("Работает только ")
                     .copy()
                     .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                     .append(
                        Component.nullToEmpty("с Opus / Private")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                     )
               )
            );
      } else {
         EventManager.register(this);
         EventManager.call(new ModuleToggleEvent(this, this.enabled));
      }
   }

   public void onDisable() {
      EventManager.unregister(this);
      EventManager.call(new ModuleToggleEvent(this, this.enabled));
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

   public boolean isRenderSetting() {
      return true;
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      json.addProperty("enabled", this.enabled);
      json.addProperty("priority", this.priority);
      json.addProperty("keyCode", this.keyCode);
      json.addProperty("keyModifiers", this.keyModifiers);
      JsonObject jsonObject = new JsonObject();

      for (Setting setting : this.getSettings()) {
         setting.safe(jsonObject);
      }

      json.add("Settings", jsonObject);
      return json;
   }

   public void load(JsonObject json) {
      if (json != null) {
         Boolean flag = null;

         try {
            flag = json.has("enabled") ? json.get("enabled").getAsBoolean() : null;
            if (json.has("priority")) {
               this.priority = json.get("priority").getAsBoolean();
            }

            if (json.has("keyCode")) {
               this.setKeyCode(json.get("keyCode").getAsInt());
            }

            this.keyModifiers = json.has("keyModifiers") ? json.get("keyModifiers").getAsInt() : 0;
         } catch (Exception e) {
            e.printStackTrace();
         }

         JsonObject jsonObject = null;

         try {
            jsonObject = json.getAsJsonObject("Settings");
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (jsonObject != null) {
            for (Setting setting : this.getSettings()) {
               String key = setting.getKey();
               if (jsonObject.has(key) || setting instanceof GroupSetting) {
                  try {
                     setting.load(jsonObject);
                  } catch (Exception e) {
                     System.err.println("[Config] bad value " + this.name + "#" + key + ": " + e);
                  }
               }
            }
         }

         try {
            if (flag != null) {
               if (flag && !this.isEnabledRaw()) {
                  this.toggle();
               }

               if (!flag && this.isEnabledRaw()) {
                  this.toggle();
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
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

   public int compareTo(@NotNull Module moduleValue) {
      return moduleValue.getName().compareTo(this.name);
   }

   public boolean isPremium() {
      return false;
   }

   @Generated
   public ModuleInfo getInfo() {
      return this.info;
   }

   @Generated
   public String getId() {
      return this.text;
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
   public boolean isPriority() {
      return this.priority;
   }

   @Generated
   public int getKeyCode() {
      return this.keyCode;
   }

   @Generated
   public int getKeyModifiers() {
      return this.keyModifiers;
   }

   @Generated
   public void setInfo(ModuleInfo info) {
      this.info = info;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Generated
   public void setPriority(boolean priority) {
      this.priority = priority;
   }

   @Generated
   public void setKeyCode(int keyCode) {
      this.keyCode = keyCode;
   }

   @Generated
   public void setKeyModifiers(int keyModifiers) {
      this.keyModifiers = keyModifiers;
   }

   @Generated
   @Override
   public String toString() {
      return "Module(info="
         + this.getInfo()
         + ", id="
         + this.getId()
         + ", name="
         + this.getName()
         + ", nameLower="
         + this.getNameLower()
         + ", category="
         + this.getCategory()
         + ", description="
         + this.getDescription()
         + ", botModule="
         + this.isBotModule()
         + ", enabled="
         + this.isEnabled()
         + ", priority="
         + this.isPriority()
         + ", keyCode="
         + this.getKeyCode()
         + ", keyModifiers="
         + this.getKeyModifiers()
         + ")";
   }
}
