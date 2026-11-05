package org.zenith.utility.api;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpClients {
   private static final Map<String, HttpClient> items = new ConcurrentHashMap<>();

   private HttpClients() {
   }

   public static HttpClient method2670(Duration duration) {
      return method2671(duration, Redirect.NEVER);
   }

   public static HttpClient method2671(Duration duration, Redirect redirect) {
      String key = duration.toMillis() + ":" + redirect;
      return items.computeIfAbsent(key, var2x -> HttpClient.newBuilder().connectTimeout(duration).followRedirects(redirect).build());
   }
}
