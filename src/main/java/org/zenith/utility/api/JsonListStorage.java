package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Collection;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public class JsonListStorage<T> {
   public Collection<T> items;
   private final String fileName;
   private final String shifr;
   private final Type type;
   private final Supplier<Collection<T>> collectionSupplier;

   public JsonListStorage(String fileName, String shifr, Type type, Supplier<Collection<T>> collectionSupplier) {
      this.fileName = fileName;
      this.shifr = shifr;
      this.type = type;
      this.collectionSupplier = collectionSupplier;
      File file = new File(Zenith.file, fileName);
      if (!file.exists()) {
         try {
            file.createNewFile();
            this.items = (Collection<T>)collectionSupplier.get();
         } catch (Exception e) {
            this.items = (Collection<T>)collectionSupplier.get();
         }
      } else {
         this.load();
      }
   }

   public Gson createGson() {
      return new GsonBuilder().create();
   }

   public void save() {
      Gson gson = this.createGson();
      String json = gson.toJson(this.items);

      try {
         writeAtomic(
            new File(Zenith.file, this.fileName),
            this.shifr.isEmpty() ? json : Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(json.getBytes(), this.shifr))
         );
      } catch (Exception e) {
      }
   }

   public static void writeAtomic(File file, String encodeToString) throws IOException {
      Path path = file.toPath();
      Path sibling = path.resolveSibling(path.getFileName() + ".tmp");
      Files.writeString(sibling, encodeToString, Charset.defaultCharset());

      try {
         Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
         Files.move(sibling, path, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private void keepUnreadable() {
      try {
         Path path = new File(Zenith.file, this.fileName).toPath();
         if (Files.isRegularFile(path) && Files.size(path) > 0L) {
            Files.copy(path, path.resolveSibling(this.fileName + ".broken-" + System.currentTimeMillis()), StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception e) {
      }
   }

   public void load() {
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Zenith.file, this.fileName)))) {
         Gson gson = this.createGson();
         if (!this.shifr.isEmpty()) {
            String line = bufferedReader.readLine();
            if (line != null && !line.isEmpty()) {
               byte[] allBytes = Base64.getDecoder().decode(line);
               byte[] bytes = AesGcmCrypto.method5340(allBytes, this.shifr);
               String str = new String(bytes, StandardCharsets.UTF_8);
               this.items = (Collection<T>)gson.fromJson(str, this.type);
            } else {
               this.items = this.collectionSupplier.get();
            }
         } else {
            this.items = (Collection<T>)gson.fromJson(bufferedReader, this.type);
         }

         if (this.items == null) {
            this.items = this.collectionSupplier.get();
         }
      } catch (Exception e) {
         this.keepUnreadable();
         this.items = this.collectionSupplier.get();
      }
   }

   public void add(T name) {
      this.items.add((T)name);
   }

   public void remove(T value) {
      this.items.remove(value);
   }

   @Generated
   public Collection<T> getItems() {
      return this.items;
   }
}
