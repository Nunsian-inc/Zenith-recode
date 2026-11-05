package org.zenith.client.screens.nlgui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.Setting;

public final class SettingIcons {
   private SettingIcons() {
   }

   public static SvgIcon resolve(Setting setting, String key, String simpleName) {
      if (setting == null) {
         return SvgIcon.MODE;
      } else {
         SvgIcon svgIcon = explicit(setting.getKey(), key, simpleName);
         if (svgIcon != null) {
            return svgIcon;
         } else if (setting instanceof ColorSetting) {
            return SvgIcon.COLOR;
         } else {
            return setting instanceof BindSetting ? SvgIcon.KEY : MenuGlyph.forSettingPurpose(setting).svg();
         }
      }
   }

   public static SvgIcon explicit(String key, String str, String simpleName) {
      if (key != null && !key.isBlank()) {
         if (simpleName != null && !simpleName.isBlank()) {
            var map = SettingIcons.Registry.MODULES.get(simpleName);
            SvgIcon svgIcon = find(map, key, str);
            if (svgIcon != null) {
               return svgIcon;
            }
         }

         return find(SettingIcons.Registry.SHARED, key, str);
      } else {
         return null;
      }
   }

   private static SvgIcon find(Map<String, SvgIcon> map, String key, String str) {
      if (map == null) {
         return null;
      } else {
         if (str != null && !str.isBlank()) {
            SvgIcon svgIcon = (SvgIcon)map.get(str + ">" + key);
            if (svgIcon != null) {
               return svgIcon;
            }
         }

         return (SvgIcon)map.get(key);
      }
   }

   private static Map<String, SvgIcon> readEntries(JsonObject jsonObject) {
      HashMap map = new HashMap();
      if (jsonObject != null) {
         for (Entry entry : jsonObject.entrySet()) {
            String name = ((JsonElement)entry.getValue()).getAsString().toUpperCase(Locale.ROOT).replace('-', '_');
            map.put((String)entry.getKey(), SvgIcon.valueOf(name));
         }
      }

      return Map.copyOf(map);
   }

   private static final class Registry {
      private static final Map<String, SvgIcon> SHARED;
      private static final Map<String, Map<String, SvgIcon>> MODULES;

      static {
         String name = "/assets/zenith/icons/setting-icons.json";

         try {
            try (InputStream resourceAsStream = SettingIcons.class.getResourceAsStream(name)) {
               if (resourceAsStream == null) {
                  throw new IllegalStateException("Missing setting icon registry: " + name);
               }

               JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)).getAsJsonObject();
               SHARED = SettingIcons.readEntries(jsonObject.getAsJsonObject("keys"));
               HashMap map = new HashMap();
               JsonObject json = jsonObject.getAsJsonObject("modules");
               if (json != null) {
                  for (Entry entry : json.entrySet()) {
                     map.put((String)entry.getKey(), SettingIcons.readEntries(((JsonElement)entry.getValue()).getAsJsonObject()));
                  }
               }

               MODULES = Map.copyOf(map);
            }
         } catch (Exception e) {
            throw new IllegalStateException("Invalid setting icon registry: " + name, e);
         }
      }
   }
}
