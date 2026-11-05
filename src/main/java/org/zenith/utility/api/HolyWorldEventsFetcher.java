package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.utility.misc.HolyWorldEvent;

public class HolyWorldEventsFetcher {
   private static final String text = "https://api.holyworld.me/v1/events";
   private static final String text2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
   private static final int CONST_intValue = 5000;
   private volatile List<HolyWorldEvent> items = List.of();
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

   public HolyWorldEventsFetcher() {
      this.scheduledExecutorService.scheduleAtFixedRate(this::method1718, 0L, 3L, TimeUnit.SECONDS);
   }

   public void method1718() {
      if (!Zenith.getInstance().getServerDetector().method1642()) {
         this.items = List.of();
      } else {
         HttpURLConnection openConnection = null;

         try {
            openConnection = (HttpURLConnection)URI.create("https://api.holyworld.me/v1/events").toURL().openConnection();
            openConnection.setRequestMethod("GET");
            openConnection.setConnectTimeout(5000);
            openConnection.setReadTimeout(5000);
            openConnection.setRequestProperty("Accept", "application/json");
            openConnection.setRequestProperty(
               "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
            );
            if (openConnection.getResponseCode() == 200) {
               List coll;
               try (BufferedInputStream bufferedInputStream = new BufferedInputStream(openConnection.getInputStream())) {
                  coll = this.method1719(bufferedInputStream);
               }

               this.items = List.copyOf(coll);
               return;
            }
         } catch (Exception e) {
            return;
         } finally {
            if (openConnection != null) {
               openConnection.disconnect();
            }
         }
      }
   }

   private List<HolyWorldEvent> method1719(InputStream bufferedInputStream) throws IOException {
      InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8);

      JsonElement reader;
      try {
         reader = JsonParser.parseReader(inputStreamReader);
      } catch (Throwable e) {
         try {
            inputStreamReader.close();
         } catch (Throwable ex) {
            e.addSuppressed(ex);
         }

         throw e;
      }

      inputStreamReader.close();
      if (!reader.isJsonObject()) {
         throw new IOException("HolyWorld events response must be a JSON object");
      } else {
         TreeMap map = new TreeMap();

         for (Entry entry : reader.getAsJsonObject().entrySet()) {
            int key = this.method1720((String)entry.getKey());
            if (key > 0 && ((JsonElement)entry.getValue()).isJsonArray()) {
               map.put(key, ((JsonElement)entry.getValue()).getAsJsonArray());
            }
         }

         LinkedHashMap map2 = new LinkedHashMap();

         for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
            Entry entry = (Entry)obj;
            for (JsonElement jsonElement : (JsonArray)entry.getValue()) {
               if (jsonElement.isJsonObject()) {
                  JsonObject jsonObject = jsonElement.getAsJsonObject();
                  JsonElement jsonElement2 = jsonObject.get("id");
                  if (jsonElement2 != null && jsonElement2.isJsonPrimitive()) {
                     String string = jsonElement2.getAsString();
                     if (!string.isBlank()) {
                        ((java.util.List)map2.computeIfAbsent(string, obj2 -> new TreeSet<>())).add((Integer)entry.getKey());
                     }
                  }
               }
            }
         }

         ArrayList<HolyWorldEvent> events = new ArrayList<>();

         for (Object obj : (Iterable<?>)(Object)(map2.entrySet())) {
            Entry entry = (Entry)obj;
            String text = this.method1721((TreeSet<Integer>)entry.getValue());
            HolyWorldEvent event = new HolyWorldEvent((String)entry.getKey(), text, 0L);
            if (event.method7743()) {
               events.add(event);
            }
         }

         return events;
      }
   }

   private int method1720(String key) {
      int lastIndexOf = key.lastIndexOf(95);
      if (lastIndexOf >= 0 && lastIndexOf != key.length() - 1) {
         try {
            return Integer.parseInt(key.substring(lastIndexOf + 1));
         } catch (NumberFormatException e) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private String method1721(TreeSet<Integer> integers) {
      StringBuilder builder = new StringBuilder();

      for (int n : integers) {
         if (!builder.isEmpty()) {
            builder.append(", ");
         }

         builder.append('#').append(n);
      }

      return builder.toString();
   }

   @Generated
   public List<HolyWorldEvent> getItems() {
      return this.items;
   }
}
