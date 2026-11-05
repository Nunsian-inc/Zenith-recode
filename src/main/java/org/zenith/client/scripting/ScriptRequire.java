package org.zenith.client.scripting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.utility.api.AiSettingsStore;
import org.zenith.utility.api.AiStreamClient;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.misc.ShownProvider;

public final class ScriptRequire {
   private static final int CONST_intValue = 10;
   private static final Map<String, Deque<Long>> items = new ConcurrentHashMap<>();
   private static final int CONST_intValue2 = 150;
   private static final Map<String, long[]> items2 = new ConcurrentHashMap<>();

   private ScriptRequire() {
   }

   public static void method4889(Storage storage, Script workspace, ScriptableObject scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scope);
      entry2.method6277("exports", ScriptValueUtils.method6247(scope).getScriptableObject());
      entry2.method6274("require", var3x -> {
         String argument = ScriptValueUtils.method6254(var3x, 0, "");
         Script script = storage.method1294(argument);
         if (script == null) {
            throw ScriptValueUtils.method6251("скрипт «" + argument + "» не установлен");
         } else if (script == workspace) {
            throw ScriptValueUtils.method6251("нельзя подключить самого себя");
         } else if (!script.isRunning()) {
            throw ScriptValueUtils.method6251("скрипт «" + script.method1403() + "» выключен — включи его");
         } else {
            Scriptable id = script.method1411();
            if (id == null) {
               throw ScriptValueUtils.method6251("скрипт «" + script.method1403() + "» ничего не экспортирует");
            } else {
               return method4890(storage, scope, script, id, 0);
            }
         }
      });
      entry2.method6277("i18n", method4893(workspace, scope));
      entry2.method6277("anim", method4895(scope));
      entry2.method6277("util", method4896(scope));
      entry2.method6277("ai", method4897(workspace, storage, scope));
   }

   private static Object method4890(Storage storage, Scriptable scope, Script script, Object id, int n) {
      if (n > 6 || ScriptValueUtils.method6253(id)) {
         return null;
      } else if (id instanceof Function function) {
         return ScriptValueUtils.method6249(scope, "exported", var4x -> {
            if (script.isRunning() && script.getScope() != null) {
               ScriptableObject var5x = script.getScope();
               Object[] var6x = new Object[var4x.length];

               for (int var7x = 0; var7x < var4x.length; var7x++) {
                  var6x[var7x] = var4x[var7x] instanceof Function ? null : ScriptValueUtils.method6273(var5x, ScriptValueUtils.method6271(var4x[var7x], 0));
               }

               Object obj = storage.method1321(script, function, var6x);
               return obj instanceof Function ? null : ScriptValueUtils.method6273(scope, ScriptValueUtils.method6271(obj, 0));
            } else {
               throw ScriptValueUtils.method6251("скрипт «" + script.method1403() + "» выключен");
            }
         });
      } else if (id instanceof Scriptable scriptable && !(id instanceof NativeArray)) {
         ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
         Object[] ids = scriptable.getIds();
         if (ids.length > 2000) {
            throw ScriptValueUtils.method6251("exports больше 2000 ключей");
         } else {
            for (Object obj : ids) {
               String name = ScriptValueUtils.method6255(obj);
               Object id2 = obj instanceof Number number ? scriptable.get(number.intValue(), scriptable) : scriptable.get(name, scriptable);
               entry2.method6277(name, method4890(storage, scope, script, id2, n + 1));
            }

            return entry2.getScriptableObject();
         }
      } else {
         return ScriptValueUtils.method6273(scope, ScriptValueUtils.method6271(id, 0));
      }
   }

   public static void method4891(Storage storage, Script workspace, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "stats",
         var2x -> {
            long[] var3x = workspace.method1409();
            double var4x = Math.max(0.001, (System.currentTimeMillis() - var3x[3]) / 1000.0);
            return ScriptValueUtils.method6247(scope)
               .method6277("totalMs", var3x[0] / 1000000.0)
               .method6277("calls", (double)var3x[1])
               .method6277("avgMs", var3x[1] == 0L ? 0.0 : var3x[0] / 1000000.0 / var3x[1])
               .method6277("peakMs", var3x[2] / 1000000.0)
               .method6277("msPerSecond", var3x[0] / 1000000.0 / var4x)
               .method6277("seconds", var4x)
               .getScriptableObject();
         }
      );
      entry2.method6274("resetStats", var1x -> {
         workspace.method1410();
         return null;
      });
      entry2.method6274("dir", var1x -> workspace.getPath() == null ? "" : workspace.getId());
      entry2.method6274(
         "checkUpdate",
         var3x -> {
            var var4x = ScriptValueUtils.method6260(var3x, 0);
            OriginUtil.Data2 data2 = OriginUtil.method6921(workspace);
            StoreApi storeApi = Zenith.getInstance().getStoreApi();
            if (data2 != null && storeApi != null) {
               method4892(
                  storeApi,
                  data2,
                  var5x -> Minecraft.getInstance()
                     .execute(
                        () -> {
                           if (workspace.isRunning()) {
                              ScriptValueUtils.Entry2 var6x = ScriptValueUtils.method6247(scope);
                              if (var5x == null) {
                                 var6x.method6277("available", false).method6277("reason", "запись не найдена в маркете");
                              } else {
                                 var6x.method6277("available", !data2.getText().equals(var5x.getText6()))
                                    .method6277("version", OriginUtil.method6916(var5x).version())
                                    .method6277("revision", (double)var5x.getLongValue2());
                              }

                              storage.method1321(workspace, var4x, var6x.getScriptableObject());
                           }
                        }
                     )
               );
               return null;
            } else {
               RemoteUtil.method1847(
                  storage,
                  workspace,
                  var4x,
                  ScriptValueUtils.method6247(scope).method6277("available", false).method6277("reason", "скрипт не из маркета").getScriptableObject()
               );
               return null;
            }
         }
      );
      entry2.method6274("update", var2x -> {
         OriginUtil.Data2 var3x = OriginUtil.method6921(workspace);
         StoreApi var4x = Zenith.getInstance().getStoreApi();
         if (var3x != null && var4x != null) {
            method4892(var4x, var3x, var4xx -> {
               if (var4xx != null && !var3x.getText().equals(var4xx.getText6())) {
                  var4x.method1970(var4xx.getUUID()).whenComplete((var3xxx, var4xxx) -> {
                     if (var4xxx == null) {
                        Minecraft.getInstance().execute(() -> {
                           try {
                              OriginUtil.method6928(storage, var4xx, var3xxx);
                           } catch (Exception e) {
                              storage.method1327(workspace, "error", "обновление не удалось: " + e.getMessage());
                           }
                        });
                     }
                  });
               }
            });
            return null;
         } else {
            throw ScriptValueUtils.method6251("скрипт не из маркета");
         }
      });
   }

   private static void method4892(StoreApi storeApi, OriginUtil.Data2 data2, Consumer<StoreApi.Data2> consumer) {
      storeApi.method1969("PUBLIC", "script").whenComplete((data2s, throwable) -> {
         if (data2s != null) {
            for (StoreApi.Data2 data22 : data2s) {
               if (data22.getUUID().equals(data2.getUUID())) {
                  consumer.accept(data22);
                  return;
               }
            }
         }

         storeApi.method1969("LIBRARY", "script").whenComplete((var2xx, var3x) -> {
            if (var2xx != null) {
               for (StoreApi.Data2 data22 : var2xx) {
                  if (data22.getUUID().equals(data2.getUUID())) {
                     consumer.accept(data22);
                     return;
                  }
               }
            }

            consumer.accept(null);
         });
      });
   }

   private static Scriptable method4893(Script workspace, Scriptable scope) {
      HashMap map = new HashMap();
      java.util.function.Function function = var2x -> map.computeIfAbsent(var2x, var1xx -> {
         if (workspace.getPath() == null) {
            return new JsonObject();
         } else {
            Path var2xx = workspace.getPath().resolve("lang").resolve(var1xx + ".json");

            try {
               if (Files.isRegularFile(var2xx) && Files.size(var2xx) < 524288L) {
                  JsonElement var3x = JsonParser.parseString(Files.readString(var2xx, StandardCharsets.UTF_8));
                  if (var3x.isJsonObject()) {
                     return var3x.getAsJsonObject();
                  }
               }
            } catch (Exception var4x) {
            }

            return new JsonObject();
         }
      });
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("lang", var0x -> Zenith.getInstance().getLocalization().getLanguageName().toLowerCase(Locale.ROOT));
      entry2.method6274("t", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         String var3x = Zenith.getInstance().getLocalization().getLanguageName().toLowerCase(Locale.ROOT);
         String var4x = null;

         for (String str : new String[]{var3x, "en", "ru"}) {
            JsonObject json = (JsonObject)function.apply(str);
            if (json.has(var2x) && json.get(var2x).isJsonPrimitive()) {
               var4x = json.get(var2x).getAsString();
               break;
            }
         }

         Scriptable scriptable = ScriptValueUtils.method6262(var1x, 1);
         if (var4x == null) {
            var4x = scriptable == null ? ScriptValueUtils.method6254(var1x, 1, var2x) : var2x;
         }

         if (scriptable != null) {
            for (Object id : scriptable.getIds()) {
               var4x = var4x.replace("{" + id + "}", ScriptValueUtils.method6255(scriptable.get(String.valueOf(id), scriptable)));
            }
         }

         return var4x;
      });
      return entry2.getScriptableObject();
   }

   public static double method4894(String str, double d) {
      d = Math.max(0.0, Math.min(1.0, d));
      String lowerCase = str.toLowerCase(Locale.ROOT);

      return switch (lowerCase) {
         case "in", "quadin" -> d * d;
         case "out", "quadout" -> 1.0 - (1.0 - d) * (1.0 - d);
         case "inout", "quadinout" -> d < 0.5 ? 2.0 * d * d : 1.0 - Math.pow(-2.0 * d + 2.0, 2.0) / 2.0;
         case "cubicout" -> 1.0 - Math.pow(1.0 - d, 3.0);
         case "cubicin" -> d * d * d;
         case "expoout" -> d == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * d);
         case "backout" -> {
            double d2 = 1.70158;
            double d3 = d2 + 1.0;
            yield 1.0 + d3 * Math.pow(d - 1.0, 3.0) + d2 * Math.pow(d - 1.0, 2.0);
         }
         case "elastic", "elasticout" -> d != 0.0 && d != 1.0
            ? Math.pow(2.0, -10.0 * d) * Math.sin((d * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0
            : d;
         case "bounce", "bounceout" -> {
            double d2 = 7.5625;
            double d3 = 2.75;
            if (d < 1.0 / d3) {
               yield d2 * d * d;
            } else if (d < 2.0 / d3) {
               d -= 1.5 / d3;
               yield d2 * d * d + 0.75;
            } else if (d < 2.5 / d3) {
               d -= 2.25 / d3;
               yield d2 * d * d + 0.9375;
            } else {
               d -= 2.625 / d3;
               yield d2 * d * d + 0.984375;
            }
         }
         default -> d;
      };
   }

   private static Scriptable method4895(Scriptable scope) {
      HashMap map = new HashMap();
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("to", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         double d = ScriptValueUtils.method6256(var1x, 1, 0.0);
         double d2 = Math.max(1.0, ScriptValueUtils.method6256(var1x, 2, 250.0));
         String str = ScriptValueUtils.method6254(var1x, 3, "cubicOut");
         long now = System.currentTimeMillis();
         double[] doubles = ((double[])map.computeIfAbsent(var2x, obj -> new double[]{d, d, d, now}));
         double d3 = doubles[0] + (doubles[1] - doubles[0]) * method4894(str, (now - doubles[3]) / d2);
         if (doubles[1] != d) {
            doubles[0] = d3;
            doubles[1] = d;
            doubles[3] = now;
         }

         doubles[2] = doubles[0] + (doubles[1] - doubles[0]) * method4894(str, (now - doubles[3]) / d2);
         return doubles[2];
      });
      entry2.method6274("ease", var0x -> method4894(ScriptValueUtils.method6254(var0x, 0, "linear"), ScriptValueUtils.method6256(var0x, 1, 0.0)));
      entry2.method6274(
         "pulse", var0x -> 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / Math.max(1.0, ScriptValueUtils.method6256(var0x, 0, 500.0)) * Math.PI)
      );
      entry2.method6274("reset", var1x -> {
         map.remove(ScriptValueUtils.method6254(var1x, 0, ""));
         return null;
      });
      return entry2.getScriptableObject();
   }

   private static Scriptable method4896(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "lerp",
         var0x -> ScriptValueUtils.method6256(var0x, 0, 0.0)
            + (ScriptValueUtils.method6256(var0x, 1, 0.0) - ScriptValueUtils.method6256(var0x, 0, 0.0)) * ScriptValueUtils.method6256(var0x, 2, 0.0)
      );
      entry2.method6274(
         "clamp",
         var0x -> Math.max(
            ScriptValueUtils.method6256(var0x, 1, 0.0), Math.min(ScriptValueUtils.method6256(var0x, 2, 1.0), ScriptValueUtils.method6256(var0x, 0, 0.0))
         )
      );
      entry2.method6274(
         "random",
         var0x -> ScriptValueUtils.method6256(var0x, 0, 0.0)
            + Math.random() * (ScriptValueUtils.method6256(var0x, 1, 1.0) - ScriptValueUtils.method6256(var0x, 0, 0.0))
      );
      entry2.method6274(
         "randomInt",
         var0x -> (double)(
            (int)Math.floor(
               ScriptValueUtils.method6256(var0x, 0, 0.0)
                  + Math.random() * (ScriptValueUtils.method6256(var0x, 1, 1.0) - ScriptValueUtils.method6256(var0x, 0, 0.0) + 1.0)
            )
         )
      );
      entry2.method6274("pick", var0x -> {
         var var1x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0));
         return var1x.isEmpty() ? null : var1x.get((int)(Math.random() * var1x.size()));
      });
      entry2.method6274("now", var0x -> (double)System.currentTimeMillis());
      entry2.method6274("format", var0x -> {
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         int n = Math.max(0, Math.min(6, ScriptValueUtils.method6257(var0x, 1, 1)));
         return String.format(Locale.ROOT, "%." + n + "f", var1x);
      });
      entry2.method6274("time", var0x -> new SimpleDateFormat(ScriptValueUtils.method6254(var0x, 0, "HH:mm:ss")).format(new Date()));
      entry2.method6274("distance", var0x -> {
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0) - ScriptValueUtils.method6256(var0x, 3, 0.0);
         double d = ScriptValueUtils.method6256(var0x, 1, 0.0) - ScriptValueUtils.method6256(var0x, 4, 0.0);
         double d2 = ScriptValueUtils.method6256(var0x, 2, 0.0) - ScriptValueUtils.method6256(var0x, 5, 0.0);
         return Math.sqrt(var1x * var1x + d * d + d2 * d2);
      });
      entry2.method6274("uuid", var0x -> UUID.randomUUID().toString());
      entry2.method6274("base64", var0x -> Base64.getEncoder().encodeToString(ScriptValueUtils.method6254(var0x, 0, "").getBytes(StandardCharsets.UTF_8)));
      entry2.method6274("unbase64", var0x -> {
         try {
            return new String(Base64.getDecoder().decode(ScriptValueUtils.method6254(var0x, 0, "")), StandardCharsets.UTF_8);
         } catch (IllegalArgumentException e) {
            throw ScriptValueUtils.method6251("это не base64");
         }
      });
      return entry2.getScriptableObject();
   }

   private static Scriptable method4897(Script workspace, Storage storage, Scriptable scope) {
      var longs = items.computeIfAbsent(workspace.getId(), var0x -> new ArrayDeque<>());
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("ready", var0x -> AiSettingsStore.method4467().method4471(AiSettingsStore.method4467().getShownProvider()));
      entry2.method6274("usage", var2x -> {
         long[] var3x = items2.get(workspace.getId());
         long var4x = var3x != null && var3x[0] == System.currentTimeMillis() / 86400000L ? var3x[1] : 0L;
         return ScriptValueUtils.method6247(scope).method6277("today", (double)var4x).method6277("limit", 150.0).getScriptableObject();
      });
      entry2.method6274(
         "ask",
         var3x -> {
            String var4x = ScriptValueUtils.method6254(var3x, 0, "");
            var function = ScriptValueUtils.method6260(var3x, 1);
            Scriptable scriptable = ScriptValueUtils.method6262(var3x, 2);
            ScriptPermissions.method4(workspace, "ai");
            if (var4x.isBlank()) {
               throw ScriptValueUtils.method6251("ai.ask: пустой запрос");
            } else if (var4x.length() > 8000) {
               throw ScriptValueUtils.method6251("ai.ask: запрос длиннее 8000 символов");
            } else {
               long now = System.currentTimeMillis();

               while (!longs.isEmpty() && now - longs.peekFirst() > 60000L) {
                  longs.pollFirst();
               }

               if (longs.size() >= 10) {
                  throw ScriptValueUtils.method6251("ai.ask: не чаще 10 раз в минуту");
               } else {
                  long l = now / 86400000L;
                  long[] longs2 = items2.computeIfAbsent(workspace.getId(), var2xx -> new long[]{l, 0L});
                  if (longs2[0] != l) {
                     longs2[0] = l;
                     longs2[1] = 0L;
                  }

                  if (longs2[1] >= 150L) {
                     throw ScriptValueUtils.method6251("ai.ask: дневной лимит 150 запросов исчерпан — он защищает твой ключ");
                  } else {
                     longs2[1]++;
                     longs.addLast(now);
                     if (longs2[1] == 1L || longs2[1] % 25L == 0L) {
                        storage.method1327(workspace, "info", "ai.ask: запрос " + longs2[1] + " из 150 за сутки к ключу игрока");
                     }

                     AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
                     ShownProvider shownProvider = aiSettingsStore.getShownProvider();
                     if (!aiSettingsStore.method4471(shownProvider)) {
                        RemoteUtil.method1847(storage, workspace, function, null, "нет ключа нейросети — вставь его во вкладке Scripts → AI");
                        return null;
                     } else {
                        String str = scriptable == null ? "Отвечай кратко и по делу." : ScriptValueUtils.method6266(scriptable, "system", "Отвечай кратко и по делу.");
                        String str2 = aiSettingsStore.method4469(shownProvider);
                        String string = aiSettingsStore.method4472(shownProvider);
                        String str3 = aiSettingsStore.method4474(shownProvider);
                        Thread.ofPlatform()
                           .daemon(true)
                           .name("zenith-script-ai")
                           .start(
                              () -> {
                                 StringBuilder var9x = new StringBuilder();
                                 String simpleName = null;

                                 try {
                                    AiStreamClient.method4739(
                                       shownProvider, str2, string, str3, str, List.of(new AiStreamClient.Data(true, var4x)), var9x::append, () -> !workspace.isRunning()
                                    );
                                 } catch (Exception var13x) {
                                    simpleName = var13x.getMessage() == null ? var13x.getClass().getSimpleName() : var13x.getMessage();
                                 }

                                 String var11x = var9x.toString();
                                 RemoteUtil.method1847(storage, workspace, function, simpleName == null ? var11x : null, simpleName);
                              }
                           );
                        return null;
                     }
                  }
               }
            }
         }
      );
      return entry2.getScriptableObject();
   }
}
