package org.zenith.addon.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ModuleDefinition {
   private final String id;
   private final String name;
   private final String description;
   private final ModuleCategory category;
   private final List<SettingDefinition> settings;
   private final Supplier<? extends ModuleBehavior> behaviorFactory;

   private ModuleDefinition(ModuleDefinition.Builder builder) {
      this.id = builder.id;
      this.name = builder.name;
      this.description = builder.description;
      this.category = builder.category;
      this.settings = List.copyOf(builder.settings);
      this.behaviorFactory = builder.behaviorFactory;
   }

   public static ModuleDefinition.Builder builder(String id) {
      return new ModuleDefinition.Builder(id);
   }

   public String id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public String description() {
      return this.description;
   }

   public ModuleCategory category() {
      return this.category;
   }

   public List<SettingDefinition> settings() {
      return this.settings;
   }

   public Supplier<? extends ModuleBehavior> behaviorFactory() {
      return this.behaviorFactory;
   }

   public static final class Builder {
      private final String id;
      private String name;
      private String description = "";
      private ModuleCategory category = ModuleCategory.MISC;
      private final List<SettingDefinition> settings = new ArrayList<>();
      private Supplier<? extends ModuleBehavior> behaviorFactory = () -> new ModuleBehavior() {};

      private Builder(String id) {
         this.id = Objects.requireNonNull(id, "id");
         this.name = id;
      }

      public ModuleDefinition.Builder name(String name) {
         this.name = Objects.requireNonNull(name, "name");
         return this;
      }

      public ModuleDefinition.Builder description(String description) {
         this.description = Objects.requireNonNullElse(description, "");
         return this;
      }

      public ModuleDefinition.Builder category(ModuleCategory category) {
         this.category = Objects.requireNonNull(category, "category");
         return this;
      }

      public ModuleDefinition.Builder setting(SettingDefinition setting) {
         this.settings.add(Objects.requireNonNull(setting, "setting"));
         return this;
      }

      public ModuleDefinition.Builder behavior(Supplier<? extends ModuleBehavior> factory) {
         this.behaviorFactory = Objects.requireNonNull(factory, "factory");
         return this;
      }

      public ModuleDefinition build() {
         return new ModuleDefinition(this);
      }
   }
}
