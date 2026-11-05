package org.zenith.base.figura.parsers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class GsonTypeByField<T> implements TypeAdapterFactory {
   private final Class<T> targetType;
   private final Function<JsonElement, String> discriminator;
   private Class<? extends T> fallbackType = null;
   private final Map<String, Class<? extends T>> understoodTypes = new HashMap<>();

   public GsonTypeByField(Class<T> targetType, Function<JsonElement, String> discriminator) {
      this.targetType = targetType;
      this.discriminator = discriminator;
   }

   public GsonTypeByField(Class<T> targetType, String discriminatorField) {
      this(targetType, element -> {
         JsonElement field = element.getAsJsonObject().get(discriminatorField);
         return field != null && field.isJsonPrimitive() && field.getAsJsonPrimitive().isString() ? field.getAsString() : null;
      });
   }

   public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
      if (type == null) {
         return null;
      }

      Class<?> klass = type.getRawType();
      if (!klass.equals(this.targetType)) {
         return null;
      }

      final TypeAdapter<JsonElement> element = gson.getAdapter(JsonElement.class);
      final Map<String, TypeAdapter<? extends T>> subtypes = new HashMap<>();

      for (Entry<String, Class<? extends T>> entry : this.understoodTypes.entrySet()) {
         TypeAdapter<? extends T> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
         subtypes.put(entry.getKey(), delegate);
      }

      final TypeAdapter<? extends T> fallbackDelegate = this.fallbackType != null ? gson.getDelegateAdapter(this, TypeToken.get(this.fallbackType)) : null;
      return (TypeAdapter<R>)(new TypeAdapter<T>() {
         public void write(JsonWriter out, T value) {
            throw new IllegalArgumentException("Cannot serialize elements using this adapter");
         }

         private T fallback(JsonElement contents, Exception reason) {
            if (fallbackDelegate == null) {
               throw new JsonParseException("No match: " + reason.toString(), reason);
            } else {
               return (T)fallbackDelegate.fromJsonTree(contents);
            }
         }

         private T fallback(JsonElement contents, String attempted) {
            if (fallbackDelegate == null) {
               throw new JsonParseException("No match on '" + attempted + "'");
            } else {
               return (T)fallbackDelegate.fromJsonTree(contents);
            }
         }

         public T read(JsonReader in) throws IOException {
            JsonElement it = (JsonElement)element.read(in);

            String typex;
            try {
               typex = GsonTypeByField.this.discriminator.apply(it);
            } catch (RuntimeException e) {
               return (T)this.fallback(it, e);
            }

            if (typex == null) {
               return (T)this.fallback(it, "<unknown>");
            }

            TypeAdapter<? extends T> delegate = subtypes.get(typex);
            return (T)(delegate == null ? this.fallback(it, typex) : delegate.fromJsonTree(it));
         }
      });
   }

   public GsonTypeByField<T> bind(String value, Class<? extends T> target) {
      this.understoodTypes.put(value, target);
      return this;
   }

   public GsonTypeByField<T> withFallback(Class<? extends T> fallback) {
      this.fallbackType = fallback;
      return this;
   }
}
