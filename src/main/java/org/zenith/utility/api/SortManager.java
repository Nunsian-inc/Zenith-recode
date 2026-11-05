package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.zenith.Zenith;

public final class SortManager {
   private static final Logger logger = LogUtils.getLogger();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final Type type = (new TypeToken<List<SortManager.Entry2>>() {}).getType();
   private final Path path = Zenith.file.toPath().resolve("sort-layouts.json");
   private final List<SortManager.Entry2> items = new ArrayList<>();

   public SortManager() {
      this.load();
   }

   public List<SortManager.Entry2> method7002() {
      return List.copyOf(this.items);
   }

   public SortManager.Entry2 method7003(String string) {
      return string == null ? null : this.items.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
   }

   public boolean method7004(String activeLayoutName, Collection<SortManager.Entry> entries) {
      var entry2s = new ArrayList<>(this.items);
      this.items.removeIf(var1x -> var1x.getName().equalsIgnoreCase(activeLayoutName));
      this.items.add(new SortManager.Entry2(activeLayoutName, System.currentTimeMillis(), entries));
      if (this.method7006()) {
         return true;
      } else {
         this.items.clear();
         this.items.addAll(entry2s);
         return false;
      }
   }

   public boolean method7005(String string) {
      var entry2s = new ArrayList<>(this.items);
      if (!this.items.removeIf(var1x -> var1x.getName().equalsIgnoreCase(string))) {
         return false;
      } else if (this.method7006()) {
         return true;
      } else {
         this.items.clear();
         this.items.addAll(entry2s);
         return false;
      }
   }

   private void load() {
      this.items.clear();
      if (Files.isRegularFile(this.path)) {
         try {
            String string = Files.readString(this.path, StandardCharsets.UTF_8);
            if (string.isBlank()) {
               return;
            }

            List<SortManager.Entry2> entry2s = (List)gson.fromJson(string, type);
            if (entry2s == null) {
               return;
            }

            for (SortManager.Entry2 entry2 : entry2s) {
               if (entry2 != null && entry2.method7010()) {
                  this.items.add(entry2);
               }
            }
         } catch (Exception e) {
            logger.warn("Не удалось загрузить раскладки сортировки из {}", this.path, e);
         }
      }
   }

   private boolean method7006() {
      Path sibling = this.path.resolveSibling(this.path.getFileName() + ".tmp");

      try {
         Files.createDirectories(this.path.getParent());
         Files.writeString(sibling, gson.toJson(this.items), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

         try {
            Files.move(sibling, this.path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException e) {
            Files.move(sibling, this.path, StandardCopyOption.REPLACE_EXISTING);
         }

         return true;
      } catch (IOException e) {
         logger.warn("Не удалось сохранить раскладки сортировки в {}", this.path, e);
         return false;
      }
   }

   public static final class Entry {
      private int slot;
      private String text;
      private boolean flag;
      private Set<String> items;

      public Entry(int slot, String text, boolean flag, Collection<String> customDataKeys) {
         this.slot = slot;
         this.text = text;
         this.flag = flag;
         this.items = new LinkedHashSet<>(customDataKeys);
      }

      public int getSlot() {
         return this.slot;
      }

      public String getText() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public Set<String> method7007() {
         return Set.copyOf(this.items);
      }

      private boolean method7008() {
         if (this.text != null && !this.text.isBlank()) {
            if (this.items == null) {
               this.items = new LinkedHashSet<>();
            } else {
               this.items.removeIf(Objects::isNull);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static final class Entry2 {
      private String name;
      private long longValue;
      private List<SortManager.Entry> items;

      private Entry2(String name, long longValue, Collection<SortManager.Entry> entries) {
         this.name = name;
         this.longValue = longValue;
         this.items = new ArrayList<>(entries);
      }

      public String getName() {
         return this.name;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public List<SortManager.Entry> method7009() {
         return List.copyOf(this.items);
      }

      private boolean method7010() {
         if (this.name != null && !this.name.isBlank()) {
            if (this.items == null) {
               this.items = new ArrayList<>();
            } else {
               this.items.removeIf(entry -> entry == null || !entry.method7008());
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
