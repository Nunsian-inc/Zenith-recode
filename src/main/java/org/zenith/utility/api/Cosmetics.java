package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.utility.config.CosmeticFileLoader;
import org.zenith.utility.misc.CosmeticModel;
import org.zenith.utility.misc.CosmeticSkeleton;
import org.zenith.utility.misc.CosmeticTextureCache;
import org.zenith.utility.misc.CosmeticType;
import org.zenith.utility.misc.Data1;
import org.zenith.utility.misc.ObjParser;
import org.zenith.utility.misc.StoreEntry;
import org.zenith.utility.network.Cloud;

public final class Cosmetics implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final String text = "cosmetics.json";
   private static final String text2 = "cache";
   private static final String text3 = "zenith.cosmetics.url";
   private static final int CONST_intValue = 64;
   private static final long CONST_longValue = 250000000L;
   private static final int CONST_intValue2 = 128;
   private static final int CONST_intValue3 = 2;
   private static final String text4 = "прочее";
   private final Path path;
   private final Path path2;
   private final Index index;
   private final CosmeticTextureCache cosmeticTextureCache = new CosmeticTextureCache();
   private final Overrides overrides;
   private final Presets presets;
   private final LinkedBlockingDeque<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>() {
      public boolean offer(Runnable runnable) {
         if (runnable instanceof Cosmetics.Entry && this.size() >= 128) {
            Runnable pollLast = this.pollLast();
            if (pollLast instanceof Cosmetics.Entry entry) {
               entry.method3019();
            } else if (pollLast != null) {
               this.offerLast(pollLast);
            }
         }

         return this.offerFirst(runnable);
      }
   };
   private final ExecutorService executorService;
   private volatile List<CosmeticItemInfo> items = List.of();
   private volatile Map<String, CosmeticItemInfo> items2 = Map.of();
   private volatile List<String> items3 = List.of();
   private volatile Map<String, List<CosmeticItemInfo>> items4 = Map.of();
   private final LinkedHashMap<String, CosmeticModel> linkedHashMap = new LinkedHashMap<>(32, 0.75F, true);
   private final Map<String, Long> items5 = new HashMap<>();
   private final Set<String> items6 = ConcurrentHashMap.newKeySet();
   private final Set<String> items7 = ConcurrentHashMap.newKeySet();
   private final Map<String, Cosmetics.Data> items8 = new ConcurrentHashMap<>();
   private static final int CONST_intValue4 = 3;
   private static final long CONST_longValue2 = 4000L;
   private final Set<String> items9 = new LinkedHashSet<>();
   private volatile Set<String> items10 = Set.of();
   private volatile List<String> items11 = List.of();
   private volatile int intValue;
   private String url = "";
   private String text5 = "";
   private float floatValue = 1.35F;
   private final Map<String, StoreEntry> items12 = new ConcurrentHashMap<>();
   private final Map<String, StoreEntry> items13 = new ConcurrentHashMap<>();
   private final Set<String> items14 = ConcurrentHashMap.newKeySet();

   public Cosmetics(Path path) {
      this.path = path.resolve("cosmetics");
      this.path2 = this.path.resolve("cosmetics.json");
      this.index = new Index(this.path.resolve("cache"), this::method2997);
      this.overrides = new Overrides(this.path);
      this.presets = new Presets(this.path);
      AtomicInteger counter = new AtomicInteger();
      this.executorService = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, this.linkedBlockingDeque, var1x -> {
         Thread var2x = new Thread(var1x, "Nexus-Cosmetics-" + counter.incrementAndGet());
         var2x.setDaemon(true);
         var2x.setPriority(1);
         return var2x;
      });
      this.method3001();
   }

   public void method2959() {
      this.method3009(this::reload);
   }

   public static Cosmetics method2960() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getCosmetics();
   }

   public void method2961(StoreEntry storeEntry) {
      this.method2962(storeEntry, false);
   }

   public void method2962(StoreEntry storeEntry, boolean flag) {
      if (storeEntry != null) {
         StoreEntry storeEntry2 = this.items13.get(storeEntry.userId());
         if (flag || storeEntry2 == null || storeEntry.method1681(storeEntry2)) {
            this.items13.put(storeEntry.userId(), storeEntry);
            if (storeEntry2 != null && storeEntry2.name() != null && !storeEntry2.name().isBlank()) {
               this.items12.remove(storeEntry2.name().toLowerCase(Locale.ROOT));
            }

            if (storeEntry.name() != null && !storeEntry.name().isBlank()) {
               this.items12.put(storeEntry.name().toLowerCase(Locale.ROOT), storeEntry);
            }

            for (Data1 data1 : storeEntry.getItems()) {
               this.method2990(data1.getText());
            }
         }
      }
   }

   public List<Data1> method2963(String str) {
      if (str != null && !str.isBlank()) {
         String lowerCase = str.toLowerCase(Locale.ROOT);
         if (this.items14.contains(lowerCase)) {
            return List.of();
         } else {
            StoreEntry storeEntry = this.items12.get(lowerCase);
            return storeEntry == null ? List.of() : storeEntry.getItems();
         }
      } else {
         return List.of();
      }
   }

   public boolean method2964(String str) {
      return str != null && this.items12.containsKey(str.toLowerCase(Locale.ROOT));
   }

   public StoreEntry method2965(String str) {
      return str == null ? null : this.items13.get(str);
   }

   public void method2966(String str, boolean flag) {
      if (str != null && !str.isBlank()) {
         String lowerCase = str.toLowerCase(Locale.ROOT);
         if (flag ? this.items14.add(lowerCase) : this.items14.remove(lowerCase)) {
            this.intValue++;
         }
      }
   }

   public boolean method2967(String str) {
      return str != null && this.items14.contains(str.toLowerCase(Locale.ROOT));
   }

   public void method2968() {
      this.items12.clear();
      this.items13.clear();
   }

   public List<CosmeticItemInfo> all() {
      return this.items;
   }

   public List<String> getItems3() {
      return this.items3;
   }

   public List<CosmeticItemInfo> method2969(String str) {
      var cosmeticItemInfos = this.items4.get(str);
      return cosmeticItemInfos == null ? List.of() : cosmeticItemInfos;
   }

   public List<CosmeticItemInfo> method2970(String str) {
      if (str != null && !str.isBlank()) {
         String lowerCase = str.strip().toLowerCase(Locale.ROOT);
         ArrayList coll = new ArrayList();

         for (CosmeticItemInfo cosmeticItemInfo : this.items) {
            if (!this.overrides.method6459(cosmeticItemInfo.getText())
               && (
                  this.overrides.method6460(cosmeticItemInfo).toLowerCase(Locale.ROOT).contains(lowerCase)
                     || cosmeticItemInfo.method7749().toLowerCase(Locale.ROOT).contains(lowerCase)
                     || method3011(cosmeticItemInfo).toLowerCase(Locale.ROOT).contains(lowerCase)
               )) {
               coll.add(cosmeticItemInfo);
            }
         }

         return List.copyOf(coll);
      } else {
         return this.items;
      }
   }

   public CosmeticItemInfo method2971(String text) {
      return text == null ? null : this.items2.get(text);
   }

   public boolean method2972() {
      return !this.items.isEmpty();
   }

   public void method2973() {
      this.index.method1454();
      this.method3009(this::reload);
   }

   public Index getIndex() {
      return this.index;
   }

   public Overrides getOverrides() {
      return this.overrides;
   }

   public Presets getPresets() {
      return this.presets;
   }

   public boolean method2974(String text) {
      return this.overrides.method6459(text);
   }

   public void method2975(String text, boolean flag) {
      Overrides.Data data = this.overrides.method6458(text);
      this.overrides.method6464(text, method2978(data, data == null ? null : data.getCosmeticType(), flag, method2979(data), method2980(data)));
      if (flag) {
         this.method2994(text);
      }

      this.method2981(text);
   }

   public int method2976() {
      int n = 0;

      for (CosmeticItemInfo cosmeticItemInfo : this.items) {
         if (this.overrides.method6459(cosmeticItemInfo.getText())) {
            this.method2975(cosmeticItemInfo.getText(), false);
            n++;
         }
      }

      if (n > 0) {
         this.intValue++;
      }

      return n;
   }

   public void rename(String text, String str) {
      Overrides.Data data = this.overrides.method6458(text);
      this.overrides
         .method6464(
            text,
            method2978(data, data == null ? null : data.getCosmeticType(), data != null && data.isFlag(), str == null ? "" : str.strip(), method2980(data))
         );
      this.intValue++;
   }

   public void method2977(String text, String str) {
      Overrides.Data data = this.overrides.method6458(text);
      this.overrides
         .method6464(
            text,
            method2978(data, data == null ? null : data.getCosmeticType(), data != null && data.isFlag(), method2979(data), str == null ? "" : str.strip())
         );
      this.intValue++;
   }

   private static Overrides.Data method2978(Overrides.Data data, CosmeticType cosmeticType, boolean flag, String text, String str) {
      return new Overrides.Data(
         cosmeticType,
         data == null ? new float[3] : data.getFloatValues(),
         data == null ? new float[3] : data.getFloatValues2(),
         data == null ? new float[]{1.0F, 1.0F, 1.0F} : data.getFloatValues3(),
         flag,
         text,
         str
      );
   }

   private static String method2979(Overrides.Data data) {
      return data != null && data.name() != null ? data.name() : "";
   }

   private static String method2980(Overrides.Data data) {
      return data != null && data.getText2() != null ? data.getText2() : "";
   }

   public void method2981(String text) {
      if (text != null) {
         synchronized (this.linkedHashMap) {
            this.linkedHashMap.remove(text);
            this.items5.remove(text);
         }

         this.items7.remove(text);
         this.method2990(text);
      }
   }

   public long method2982() {
      long l = this.index.method1461();
      this.items7.clear();
      this.items8.clear();
      synchronized (this.linkedHashMap) {
         this.linkedHashMap.clear();
         this.items5.clear();
      }

      this.cosmeticTextureCache.releaseAll();
      this.method3009(this::reload);
      return l;
   }

   public boolean method2983(CosmeticItemInfo cosmeticItemInfo) {
      return cosmeticItemInfo != null && this.index.method1459(cosmeticItemInfo);
   }

   public CosmeticModel method2984(String key) {
      if (key == null) {
         return null;
      } else {
         synchronized (this.linkedHashMap) {
            CosmeticModel cosmeticModel = this.linkedHashMap.get(key);
            if (cosmeticModel != null) {
               this.items5.put(key, System.nanoTime());
            }

            return cosmeticModel;
         }
      }
   }

   public boolean method2985(String str) {
      return str != null && this.items6.contains(str);
   }

   public int method2986() {
      synchronized (this.linkedHashMap) {
         return this.linkedHashMap.size();
      }
   }

   public int method2987() {
      return this.items6.size();
   }

   public int method2988() {
      return this.items7.size();
   }

   public boolean method2989(String str) {
      return str != null && this.items7.contains(str);
   }

   public void method2990(String text) {
      if (this.method2972() && text != null && !text.isBlank() && !this.items7.contains(text)) {
         Cosmetics.Data data = this.items8.get(text);
         if (data == null || System.currentTimeMillis() >= data.getLongValue()) {
            synchronized (this.linkedHashMap) {
               if (this.linkedHashMap.containsKey(text)) {
                  return;
               }
            }

            if (this.items6.add(text)) {
               if (!this.method3009(new Cosmetics.Entry(text))) {
                  this.items6.remove(text);
               }
            }
         }
      }
   }

   public Set<String> getItems10() {
      return this.items10;
   }

   public List<String> getItems11() {
      return this.items11;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean method2991(String str) {
      return str != null && this.items10.contains(str);
   }

   public void method2992(String text) {
      if (this.method2971(text) != null) {
         if (this.items9.add(text)) {
            this.method2996();
         }

         this.method2990(text);
      }
   }

   public void method2993(Collection<String> strings) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      if (strings != null) {
         for (String text : strings) {
            if (text != null && !text.isBlank() && this.method2971(text) != null) {
               linkedHashSet.add(text);
            }
         }
      }

      if (!linkedHashSet.equals(this.items9)) {
         this.items9.clear();
         this.items9.addAll(linkedHashSet);
         this.method2996();
      }
   }

   public void method2994(String text) {
      if (text != null && this.items9.remove(text)) {
         this.method2996();
      }
   }

   public void method2995() {
      if (!this.items9.isEmpty()) {
         this.items9.clear();
         this.method2996();
      }
   }

   private void method2996() {
      this.items10 = Collections.unmodifiableSet(new LinkedHashSet<>(this.items9));
      this.items11 = List.copyOf(this.items9);
      this.intValue++;

      for (String text : this.items9) {
         this.method2990(text);
      }

      this.method3002();
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (String string : this.items9) {
         jsonArray.add(string);
      }

      json.add("equipped", jsonArray);
      return json;
   }

   public void load(JsonObject json) {
      this.items9.clear();
      if (json != null && json.has("equipped") && json.get("equipped").isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray("equipped")) {
            if (jsonElement.isJsonPrimitive()) {
               this.items9.add(jsonElement.getAsString());
            }
         }
      }

      this.items10 = Collections.unmodifiableSet(new LinkedHashSet<>(this.items9));
      this.items11 = List.copyOf(this.items9);
      this.intValue++;
      if (this.method2972()) {
         for (String text : this.items9) {
            this.method2990(text);
         }
      }
   }

   public String url() {
      String str = System.getProperty("zenith.cosmetics.url", "").strip();
      if (!str.isEmpty()) {
         return str;
      } else {
         return !this.url.isBlank() ? this.url : this.method2997().get(0);
      }
   }

   public List<String> method2997() {
      String str = System.getProperty("zenith.cosmetics.url", "").strip();
      if (!str.isEmpty()) {
         return List.of(str);
      } else if (!this.url.isBlank()) {
         return List.of(this.url);
      } else {
         ArrayList<String> strings = new ArrayList<>();
         Zenith zenith = Zenith.getInstance();
         Cloud cloud = zenith == null ? null : zenith.getCloud();
         if (cloud != null) {
            String str2 = StoreApi.method1994(cloud.method7870());
            if (!str2.isEmpty()) {
               strings.add(str2);
            }
         }

         for (String str2 : RegionUtil.method2040()) {
            String str3 = StoreApi.method1994(str2);
            if (!str3.isEmpty() && !strings.contains(str3)) {
               strings.add(str3);
            }
         }

         return strings;
      }
   }

   public String method2998() {
      return this.index.getLastError();
   }

   public String method2999() {
      if (!System.getProperty("zenith.cosmetics.url", "").strip().isEmpty()) {
         return "-Dzenith.cosmetics.url";
      } else {
         return !this.url.isBlank() ? "cosmetics.json" : "узел облака Nexus";
      }
   }

   public void method3000(String str) {
      this.url = str == null ? "" : str.strip();
      this.method3002();
      this.method2973();
   }

   private void method3001() {
      JsonObject json = JsonIo.method1009(this.path2);
      if (json != null) {
         if (json.has("url")) {
            this.url = json.get("url").getAsString();
         }

         if (json.has("secret")) {
            this.text5 = json.get("secret").getAsString();
         }

         if (json.has("petScale")) {
            this.floatValue = method3004(json.get("petScale").getAsFloat());
         }

         if (json.has("equipped")) {
            this.load(json);
         }
      }
   }

   private void method3002() {
      JsonObject jsonObject2 = this.save();
      jsonObject2.addProperty("url", this.url);
      jsonObject2.addProperty("secret", this.text5);
      jsonObject2.addProperty("petScale", this.floatValue);
      JsonIo.method1010(this.path2, jsonObject2);
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public void method3003(float f) {
      this.floatValue = method3004(f);
      this.method3002();
   }

   private static float method3004(float f) {
      return Math.max(0.4F, Math.min(4.0F, f));
   }

   public String getText5() {
      return this.text5;
   }

   public void method3005(String str) {
      this.text5 = str == null ? "" : str.strip();
      this.method3002();
   }

   public void method3006(Consumer<String> consumer) {
      JsonObject jsonObject2 = this.overrides.getJsonObject2();
      if (jsonObject2.isEmpty()) {
         consumer.accept("Править нечего: своих правок нет");
      } else if (this.text5.isBlank()) {
         consumer.accept("Нужен секрет узла: .cosmetics secret <секрет>");
      } else {
         int size = jsonObject2.size();
         byte[] bytes = jsonObject2.toString().getBytes(StandardCharsets.UTF_8);
         this.method3009(() -> {
            String var4x = this.index.method1456(bytes, this.text5);
            byte[] bytes2 = var4x == null ? null : this.index.method1455();
            mc.execute(() -> {
               if (var4x == null) {
                  consumer.accept("Не отправилось: " + this.index.getLastError());
               } else {
                  if (bytes2 != null) {
                     this.overrides.method6466(bytes2);
                  }

                  consumer.accept("Применено всем: " + size + " " + (size == 1 ? "вещь" : "вещей"));
               }
            });
         });
      }
   }

   private void method3007() {
      byte[] bytes = this.index.method1455();
      if (bytes != null) {
         mc.execute(() -> {
            int n = this.overrides.method6466(bytes);
            if (n >= 0) {
               this.method3008();
            }
         });
      }
   }

   private void method3008() {
      List<String> strings = List.copyOf(this.items9);
      synchronized (this.linkedHashMap) {
         this.linkedHashMap.clear();
         this.items5.clear();
      }

      this.items7.clear();

      for (String text : strings) {
         this.method2990(text);
      }

      this.intValue++;
   }

   public void shutdown() {
      this.executorService.shutdownNow();
      this.cosmeticTextureCache.releaseAll();
      synchronized (this.linkedHashMap) {
         this.linkedHashMap.clear();
         this.items5.clear();
      }
   }

   private boolean method3009(Runnable entry) {
      try {
         this.executorService.execute(entry);
         return true;
      } catch (RejectedExecutionException e) {
         return false;
      }
   }

   private void reload() {
      var cosmeticItemInfos = this.index.method1453();
      mc.execute(() -> this.method3010(cosmeticItemInfos));
      this.method3007();
   }

   private void method3010(List<CosmeticItemInfo> cosmeticItemInfos) {
      if (cosmeticItemInfos.isEmpty()) {
         logger.warn("Косметика: каталог пуст — узел недоступен и кеша нет");
         String lastError = this.index.getLastError();
         logger.warn("Косметика: каталог не приехал — " + (lastError.isEmpty() ? "узел недоступен" : lastError));
      } else {
         LinkedHashMap map = new LinkedHashMap();
         HashMap map2 = new HashMap(cosmeticItemInfos.size() * 2);
         int n = 0;

         for (CosmeticItemInfo cosmeticItemInfo : cosmeticItemInfos) {
            if (!cosmeticItemInfo.method7750()) {
               n++;
            } else {
               map2.putIfAbsent(cosmeticItemInfo.getText(), cosmeticItemInfo);
               ((java.util.List)map.computeIfAbsent(method3011(cosmeticItemInfo), obj -> new ArrayList<>())).add(cosmeticItemInfo);
            }
         }

         if (map.isEmpty()) {
            logger.warn("Косметика: в каталоге {} записей, и ни одной пригодной", cosmeticItemInfos.size());
         } else {
            var thenComparing = Comparator.comparing(CosmeticItemInfo::method7749, String.CASE_INSENSITIVE_ORDER).thenComparing(CosmeticItemInfo::getText);
            List<CosmeticItemInfo> coll = new ArrayList<>(cosmeticItemInfos.size());
            LinkedHashMap map3 = new LinkedHashMap();

            for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
               Map.Entry entry = (Map.Entry)obj;
               ((List)entry.getValue()).sort(thenComparing);
               map3.put((String)entry.getKey(), List.copyOf((Collection)entry.getValue()));
               coll.addAll((Collection)entry.getValue());
            }

            this.items7.clear();
            this.items8.clear();
            this.items = List.copyOf(coll);
            this.items2 = Map.copyOf(map2);
            this.items4 = Collections.unmodifiableMap(map3);
            this.items3 = List.copyOf(map3.keySet());
            logger.info("Косметика: каталог {} элементов, {} категорий (пропущено непригодных: {})", new Object[]{this.items.size(), this.items3.size(), n});
            this.intValue++;

            for (String text : this.items9) {
               this.method2990(text);
            }
         }
      }
   }

   private static String method3011(CosmeticItemInfo cosmeticItemInfo) {
      if (cosmeticItemInfo.getText3() != null && !cosmeticItemInfo.getText3().isBlank()) {
         return cosmeticItemInfo.getText3();
      } else {
         return cosmeticItemInfo.getText4() != null && !cosmeticItemInfo.getText4().isBlank() ? cosmeticItemInfo.getText4() : "прочее";
      }
   }

   private void load(String text) {
      Cosmetics.Data2 data2 = null;
      boolean flag = false;

      try {
         data2 = this.method3012(this.method2971(text));
      } catch (Exception e) {
         flag = true;
         logger.warn("Косметика: {} не загрузился: {}", text, e.toString());
      }

      Cosmetics.Data2 data22 = data2;
      boolean flag2 = flag;
      mc.execute(() -> this.method3013(text, data22, flag2));
   }

   private Cosmetics.Data2 method3012(CosmeticItemInfo cosmeticItemInfo) throws Exception {
      if (cosmeticItemInfo == null) {
         return null;
      } else {
         CosmeticFileLoader cosmeticFileLoader = CosmeticFileLoader.method7345(new ByteArrayInputStream(this.index.method1458(cosmeticItemInfo)));

         CosmeticSkeleton cosmeticSkeleton = switch (cosmeticFileLoader.getType()) {
            case WAVEFRONT -> ObjParser.method8021(cosmeticFileLoader.method7339(cosmeticFileLoader.getText()), cosmeticFileLoader.method7339(cosmeticFileLoader.getText3()));
            case BEDROCK -> GeometryParser.method2261(cosmeticFileLoader.method7339(cosmeticFileLoader.getText()), cosmeticItemInfo.getText7());
            case TECHNE -> TechneParser.method7431(cosmeticFileLoader.method7339(cosmeticFileLoader.getText()), cosmeticItemInfo.getText7());
            case EMPTY -> null;
         };
         if (cosmeticSkeleton != null && cosmeticSkeleton.method7138() != 0) {
            String[] texts = cosmeticSkeleton.getTexts();
            byte[][] byteValues = new byte[Math.max(1, texts.length)][];

            for (int i = 0; i < byteValues.length; i++) {
               String key = i < texts.length && !texts[i].isBlank() ? texts[i] : cosmeticItemInfo.getText7();
               byteValues[i] = cosmeticFileLoader.method7340(key);
            }

            String json = cosmeticFileLoader.method7339(cosmeticFileLoader.getText2());
            CosmeticAnimation cosmeticAnimation = json == null ? null : CosmeticAnimation.method7814(json);
            return new Cosmetics.Data2(cosmeticItemInfo, cosmeticSkeleton, byteValues, cosmeticAnimation);
         } else {
            return null;
         }
      }
   }

   private void method3013(String text, Cosmetics.Data2 data2, boolean flag) {
      this.items6.remove(text);
      if (data2 == null) {
         if (!flag || !this.method3014(text)) {
            this.items7.add(text);
            this.items8.remove(text);
         }
      } else {
         String str = data2.getCosmeticItemInfo().getText6();
         String[] texts = data2.getCosmeticSkeleton().getTexts();
         Identifier[] identifiers = new Identifier[data2.getByteValues().length];

         for (int i = 0; i < identifiers.length; i++) {
            String str2 = i < texts.length ? texts[i] : "";
            identifiers[i] = this.cosmeticTextureCache.method2616(CosmeticTextureCache.method2623(str, str2), data2.getByteValues()[i]);
         }

         if (identifiers[0] == null) {
            this.items7.add(text);
            this.items8.remove(text);
         } else {
            for (int i = 1; i < identifiers.length; i++) {
               if (identifiers[i] == null) {
                  identifiers[i] = identifiers[0];
               }
            }

            this.items8.remove(text);
            CosmeticModel cosmeticModel = new CosmeticModel(data2.getCosmeticItemInfo(), data2.getCosmeticSkeleton(), identifiers, data2.getCosmeticAnimation(), this.overrides);
            synchronized (this.linkedHashMap) {
               this.linkedHashMap.put(text, cosmeticModel);
               this.method3015();
            }
         }
      }
   }

   private boolean method3014(String text) {
      Cosmetics.Data data = this.items8.get(text);
      int intValue = data == null ? 1 : data.getIntValue() + 1;
      if (intValue >= 3) {
         return false;
      } else {
         this.items8.put(text, new Cosmetics.Data(intValue, System.currentTimeMillis() + 4000L * intValue));
         return true;
      }
   }

   private void method3015() {
      if (this.linkedHashMap.size() > 64) {
         long nanoTime = System.nanoTime();
         var iterator = this.linkedHashMap.entrySet().iterator();

         while (this.linkedHashMap.size() > 64 && iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String)entry.getKey();
            if (!this.items9.contains(key) && !this.method3016(key, nanoTime)) {
               String anObject = ((CosmeticModel)entry.getValue()).getCosmeticItemInfo().getText6();
               iterator.remove();
               this.items5.remove(key);
               if (!this.method3017(anObject)) {
                  this.cosmeticTextureCache.method2622(anObject);
               }
            }
         }
      }
   }

   private boolean method3016(String key, long nanoTime) {
      Long l = this.items5.get(key);
      return l != null && nanoTime - l < 250000000L;
   }

   private boolean method3017(String anObject) {
      for (CosmeticModel cosmeticModel : this.linkedHashMap.values()) {
         if (cosmeticModel.getCosmeticItemInfo().getText6().equals(anObject)) {
            return true;
         }
      }

      return false;
   }

   private static final class Data {
      private final int intValue;
      private final long longValue;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(int intValue, long longValue) {
         this.intValue = intValue;
         this.longValue = longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final CosmeticItemInfo cosmeticItemInfo;
      private final CosmeticSkeleton cosmeticSkeleton;
      private final byte[][] byteValues;
      private final CosmeticAnimation cosmeticAnimation;

      public CosmeticItemInfo cosmeticItemInfo() {
         return this.cosmeticItemInfo;
      }

      public CosmeticSkeleton cosmeticSkeleton() {
         return this.cosmeticSkeleton;
      }

      public byte[][] byteValues() {
         return this.byteValues;
      }

      public CosmeticAnimation cosmeticAnimation() {
         return this.cosmeticAnimation;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.cosmeticItemInfo, other.cosmeticItemInfo) && java.util.Objects.equals(this.cosmeticSkeleton, other.cosmeticSkeleton) && java.util.Objects.equals(this.byteValues, other.byteValues) && java.util.Objects.equals(this.cosmeticAnimation, other.cosmeticAnimation);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(cosmeticItemInfo, cosmeticSkeleton, byteValues, cosmeticAnimation);
      }

      @Override
      public String toString() {
         return "Data2[" + "cosmeticItemInfo=" + this.cosmeticItemInfo + ", " + "cosmeticSkeleton=" + this.cosmeticSkeleton + ", " + "byteValues=" + this.byteValues + ", " + "cosmeticAnimation=" + this.cosmeticAnimation + "]";
      }

      private Data2(CosmeticItemInfo cosmeticItemInfo, CosmeticSkeleton cosmeticSkeleton, byte[][] byteValues, CosmeticAnimation cosmeticAnimation) {
         this.cosmeticItemInfo = cosmeticItemInfo;
         this.cosmeticSkeleton = cosmeticSkeleton;
         this.byteValues = byteValues;
         this.cosmeticAnimation = cosmeticAnimation;
      }

      public CosmeticItemInfo getCosmeticItemInfo() {
         return this.cosmeticItemInfo;
      }

      public CosmeticSkeleton getCosmeticSkeleton() {
         return this.cosmeticSkeleton;
      }

      public byte[][] getByteValues() {
         return this.byteValues;
      }

      public CosmeticAnimation getCosmeticAnimation() {
         return this.cosmeticAnimation;
      }
   }

   private final class Entry implements Runnable {
      private final String text;

      private Entry(String text) {
         this.text = text;
      }

      @Override
      public void run() {
         Cosmetics.this.load(this.text);
      }

      private void method3019() {
         Cosmetics.this.items6.remove(this.text);
      }
   }
}
