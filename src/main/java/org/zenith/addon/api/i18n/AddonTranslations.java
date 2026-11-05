package org.zenith.addon.api.i18n;

import java.util.Objects;
import net.minecraft.client.resources.language.I18n;

public final class AddonTranslations {
   private AddonTranslations() {
   }

   public static boolean has(String key) {
      return net.minecraft.locale.Language.getInstance().has(Objects.requireNonNullElse(key, ""));
   }

   public static String translate(String key, Object... arguments) {
      String checkedKey = Objects.requireNonNullElse(key, "");
      Object[] checkedArguments = arguments == null ? new Object[0] : arguments;
      return I18n.get(checkedKey, checkedArguments);
   }

   public static String resolve(String keyOrText, Object... arguments) {
      String value = Objects.requireNonNullElse(keyOrText, "");
      return has(value) ? translate(value, arguments) : value;
   }
}
