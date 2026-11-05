package org.zenith.utility.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;

public class ParticleType {
   private static final Map<String, ParticleType.Type> items = new HashMap<>();

   public static String[] method1704() {
      ParticleType.Type[] values = ParticleType.Type.values();
      String[] strings = new String[values.length];

      for (int i = 0; i < values.length; i++) {
         strings[i] = values[i].getText();
      }

      return strings;
   }

   public static Identifier method1705(String key) {
      ParticleType.Type type = items.get(key);
      if (type == null) {
         try {
            type = ParticleType.Type.valueOf(key.toUpperCase());
         } catch (IllegalArgumentException e) {
         }
      }

      return type != null ? type.getSupplier().get() : null;
   }

   static {
      for (ParticleType.Type type : ParticleType.Type.values()) {
         items.put(type.getText(), type);
      }
   }

   public static enum Type {
      SNOWFLAKE("particle.texture.snowflake", "particles/snowflake.png"),
      STAR("particle.texture.star", "particles/star.png"),
      HEART("particle.texture.heart", "particles/heart.png"),
      FIREFLY("particle.texture.firefly", "particles/firefly.png"),
      SPACE_GLOW("particle.texture.spaceGlow", "particles/space_glow.png"),
      SPACE_STAR("particle.texture.spaceStar", "particles/space_star.png");

      private final String text;
      private final Supplier<Identifier> supplier;

      private Type(String text, String languageFlagPath) {
         this.text = text;
         this.supplier = () -> Zenith.method1501(languageFlagPath);
      }

      @Generated
      public String getText() {
         return this.text;
      }

      @Generated
      public Supplier<Identifier> getSupplier() {
         return this.supplier;
      }
   }
}
