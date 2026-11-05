package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.config.AiImageStore;
import org.zenith.utility.misc.ScriptHeaderParser;
import org.zenith.utility.misc.ShownProvider;

public final class AiChatStorage {
   private static final AiChatStorage INSTANCE = new AiChatStorage();
   private static final Pattern pattern = Pattern.compile("```[a-zA-Z]*[ \\t]*(?:file|path|title)[ \\t]*[:=][ \\t]*\"?([^\\n`\"]+)\"?[ \\t]*\\n(.*?)```", 32);
   private static final Pattern pattern2 = Pattern.compile("//\\s*@name\\s+(.+)");
   private static final Pattern pattern3 = Pattern.compile("```[\\s\\S]*?(```|$)");
   private static final int CONST_intValue = 40;
   private static final int CONST_intValue2 = 160000;
   private static final int CONST_intValue3 = 24;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 80;
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "zenith-ai-chats");
      thread.setDaemon(true);
      return thread;
   });
   private final List<AiChatStorage.Entry> items = new CopyOnWriteArrayList<>();
   private volatile AiChatStorage.Entry entry;
   private volatile AiChatStorage.Entry entry2;
   private volatile boolean busy;
   private volatile boolean flag;
   private volatile int revision;
   private volatile boolean loaded;
   private final Path path = Zenith.file.toPath().resolve("ai-chats");

   public static AiChatStorage method2741() {
      INSTANCE.method2742();
      return INSTANCE;
   }

   private synchronized void method2742() {
      if (!this.loaded) {
         this.loaded = true;

         try {
            Files.createDirectories(this.path);

            try (Stream<java.nio.file.Path> list = Files.list(this.path)) {
               for (Path path : list.filter(path2 -> path2.toString().endsWith(".json")).toList()) {
                  try {
                     JsonObject jsonObject = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
                     AiChatStorage.Entry entry = new AiChatStorage.Entry(jsonObject.get("id").getAsString(), jsonObject.get("createdAt").getAsLong());
                     entry.longValue2 = jsonObject.has("updatedAt") ? jsonObject.get("updatedAt").getAsLong() : entry.longValue;
                     entry.title = jsonObject.has("title") ? jsonObject.get("title").getAsString() : "";
                     entry.text2 = jsonObject.has("target") ? jsonObject.get("target").getAsString() : null;

                     for (JsonElement json : jsonObject.getAsJsonArray("messages")) {
                        entry.items.add(AiChatStorage.Entry2.method2777(json.getAsJsonObject()));
                     }

                     if (!entry.items.isEmpty()) {
                        this.items.add(entry);
                     }
                  } catch (Exception e) {
                  }
               }
            }
         } catch (IOException e) {
         }

         this.items.sort(Comparator.comparingLong(AiChatStorage.Entry::getLongValue2).reversed());
         this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
      }
   }

   private static String method2743() {
      return Long.toString(System.currentTimeMillis(), 36) + UUID.randomUUID().toString().substring(0, 4);
   }

   public List<AiChatStorage.Entry> method2744() {
      var entries = new ArrayList<>(this.items);
      entries.sort(Comparator.comparingLong(AiChatStorage.Entry::getLongValue2).reversed());
      return entries;
   }

   public AiChatStorage.Entry getEntry() {
      return this.entry;
   }

   public List<AiChatStorage.Entry2> method2745() {
      return this.entry.items;
   }

   public boolean isBusy() {
      return this.busy;
   }

   public boolean method2746() {
      return this.busy && this.entry2 == this.entry;
   }

   public int revision() {
      return this.revision;
   }

   public String method2747() {
      return this.entry.text2;
   }

   public void method2748(String id) {
      if (id == null ? this.entry.text2 != null : !id.equals(this.entry.text2)) {
         this.entry.text2 = id;
         if (!this.entry.empty()) {
            this.method2755(this.entry);
         }

         this.revision++;
      }
   }

   public void method2749(AiChatStorage.Entry entry) {
      if (entry != null && entry != this.entry) {
         if (this.entry.empty()) {
            this.items.remove(this.entry);
         }

         this.entry = entry;
         this.revision++;
      }
   }

   public void method2750(String id) {
      if (this.entry.empty()) {
         this.entry.text2 = id;
      } else {
         this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
         this.entry.text2 = id;
      }

      this.revision++;
   }

   public void method2751(Script script) {
      String id = script == null ? null : script.getId();

      for (AiChatStorage.Entry entry : this.method2744()) {
         if (id != null && id.equals(entry.text2)) {
            this.method2749(entry);
            return;
         }
      }

      this.method2750(id);
   }

   public synchronized void method2752(AiChatStorage.Entry sub) {
      if (sub != null) {
         sub.deleted = true;
         if (this.entry2 == sub) {
            this.flag = true;
         }

         this.items.remove(sub);
         executorService.execute(() -> {
            try {
               Files.deleteIfExists(this.path.resolve(sub.text + ".json"));
            } catch (IOException e) {
            }
         });
         ArrayList coll = new ArrayList();

         for (AiChatStorage.Entry2 entry2 : sub.items) {
            coll.addAll(entry2.items);
         }

         AiImageStore.method4640(coll);
         if (this.entry == sub) {
            this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
         }

         this.revision++;
      }
   }

   public void method2753() {
      for (AiChatStorage.Entry sub : new ArrayList<>(this.items)) {
         this.method2752(sub);
      }

      this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
      this.revision++;
   }

   public void clear() {
      this.method2750(this.entry.text2);
   }

   public void stop() {
      this.flag = true;
   }

   private void method2754(AiChatStorage.Entry entry) {
      if (!entry.deleted) {
         entry.longValue2 = System.currentTimeMillis();
         if (!this.items.contains(entry)) {
            this.items.add(entry);
         }

         while (this.items.size() > 80) {
            AiChatStorage.Entry sub = this.items.stream().min(Comparator.comparingLong(AiChatStorage.Entry::getLongValue2)).orElse(null);
            if (sub == null || sub == this.entry) {
               break;
            }

            this.method2752(sub);
         }
      }
   }

   private void method2755(AiChatStorage.Entry entry) {
      JsonObject json = new JsonObject();
      json.addProperty("id", entry.text);
      json.addProperty("createdAt", entry.longValue);
      json.addProperty("updatedAt", entry.longValue2);
      json.addProperty("title", entry.title);
      if (entry.text2 != null) {
         json.addProperty("target", entry.text2);
      }

      JsonArray jsonArray = new JsonArray();

      for (AiChatStorage.Entry2 entry2 : entry.items) {
         if (!entry2.streaming()) {
            jsonArray.add(entry2.method2776());
         }
      }

      json.add("messages", jsonArray);
      String string = json.toString();
      executorService.execute(() -> {
         try {
            Files.createDirectories(this.path);
            Files.writeString(this.path.resolve(entry.text + ".json"), string, StandardCharsets.UTF_8);
         } catch (IOException e) {
         }
      });
   }

   private static String method2756(String text) {
      String str = text.replace('\n', ' ').strip();
      if (str.length() > 42) {
         int lastIndexOf = str.lastIndexOf(32, 42);
         str = str.substring(0, lastIndexOf > 20 ? lastIndexOf : 42) + "…";
      }

      return str.isEmpty() ? "Новый чат" : Character.toUpperCase(str.charAt(0)) + str.substring(1);
   }

   public void method2757(Storage hostOrNull, String text) {
      this.method2764(hostOrNull, this.entry, text, false, List.of());
   }

   public void method2758(Storage hostOrNull, String text, List<String> strings) {
      this.method2764(hostOrNull, this.entry, text, false, strings);
   }

   public void method2759(String message) {
      AiChatStorage.Entry2 entry2 = new AiChatStorage.Entry2(false, message, System.currentTimeMillis());
      entry2.flag6 = true;
      this.entry.items.add(entry2);
      this.method2754(this.entry);
      this.method2755(this.entry);
      this.revision++;
   }

   public Path method2760() throws IOException {
      AiChatStorage.Entry entry = this.entry;
      StringBuilder builder = new StringBuilder("# ").append(entry.title.isEmpty() ? "Чат" : entry.title).append("\n\n");
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

      for (AiChatStorage.Entry2 entry2 : entry.items) {
         builder.append("### ")
            .append(entry2.flag ? "Ты" : (entry2.flag6 ? "Заметка" : (entry2.text2.isEmpty() ? "AI" : entry2.text2)))
            .append(" · ")
            .append(simpleDateFormat.format(new Date(entry2.longValue)))
            .append("\n\n");

         for (String str : entry2.items) {
            builder.append("![картинка](../images/").append(str).append(")\n\n");
         }

         builder.append(entry2.text()).append("\n\n");
         if (!entry2.error.isEmpty()) {
            builder.append("> ошибка: ").append(entry2.error).append("\n\n");
         }

         if (!entry2.text.isEmpty()) {
            builder.append("> ").append(entry2.text).append("\n\n");
         }
      }

      Path dir = this.path.resolve("exports");
      Files.createDirectories(dir);
      String str = (entry.title.isEmpty() ? "chat" : entry.title).replaceAll("[^\\p{L}\\p{N} _-]", "").strip();
      if (str.length() > 40) {
         str = str.substring(0, 40).strip();
      }

      Path path = dir.resolve((str.isEmpty() ? "chat" : str) + "-" + entry.text + ".md");
      Files.writeString(path, builder.toString(), StandardCharsets.UTF_8);
      return path;
   }

   public void method2761(Storage hostOrNull) {
      AiChatStorage.Entry entry = this.entry;
      if (!this.busy && !entry.items.isEmpty()) {
         int n = entry.items.size() - 1;
         if (!entry.items.get(n).isFlag()) {
            entry.items.remove(n);
            AiChatStorage.Entry2 entry2 = null;

            for (int i = entry.items.size() - 1; i >= 0; i--) {
               if (entry.items.get(i).isFlag()) {
                  entry2 = entry.items.remove(i);
                  break;
               }
            }

            if (entry2 != null) {
               this.method2764(hostOrNull, entry, entry2.text(), entry2.flag5, new ArrayList<>(entry2.items));
            }
         }
      }
   }

   public String method2762() {
      for (int i = this.entry.items.size() - 1; i >= 0; i--) {
         if (this.entry.items.get(i).isFlag() && !this.entry.items.get(i).isFlag5()) {
            return this.entry.items.get(i).text();
         }
      }

      return "";
   }

   private void method2763(Storage hostOrNull, AiChatStorage.Entry entry, String text, boolean flag) {
      this.method2764(hostOrNull, entry, text, flag, List.of());
   }

   private void method2764(Storage hostOrNull, AiChatStorage.Entry entry, String text, boolean flag, List<String> strings) {
      if (!this.busy && text != null && hostOrNull != null && (!text.isBlank() || !strings.isEmpty())) {
         AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
         ShownProvider shownProvider = aiSettingsStore.getShownProvider();
         if (!flag) {
            entry.intValue = 0;
         }

         if (entry.title.isEmpty()) {
            entry.title = text.isBlank() ? "Картинка" : method2756(text);
         }

         AiChatStorage.Entry2 entry2 = new AiChatStorage.Entry2(true, text.strip(), System.currentTimeMillis());
         entry2.flag5 = flag;
         entry2.items.addAll(strings);
         entry.items.add(entry2);
         AiChatStorage.Entry2 entry22 = new AiChatStorage.Entry2(false, "", System.currentTimeMillis());
         entry22.text2 = shownProvider.title() + " · " + aiSettingsStore.method4472(shownProvider);
         entry22.flag2 = true;
         entry.items.add(entry22);
         this.method2754(entry);
         this.revision++;
         if (!aiSettingsStore.method4471(shownProvider)) {
            entry22.flag2 = false;
            entry22.error = "nokey";
            this.method2755(entry);
            this.revision++;
         } else {
            AiChatStorage.Data3 data3 = method2770(hostOrNull, entry);
            ArrayList<AiChatStorage.Data2> data2s = new ArrayList();
            var entry2s = new ArrayList<>(entry.items);
            entry2s.remove(entry2s.size() - 1);
            int fromIndex = Math.max(0, entry2s.size() - 24);
            var subList = entry2s.subList(fromIndex, entry2s.size());

            for (int i = 0; i < subList.size(); i++) {
               AiChatStorage.Entry2 entry23 = (AiChatStorage.Entry2)subList.get(i);
               if (!entry23.flag6 && (entry23.flag || entry23.error.isEmpty() || !entry23.text().isEmpty())) {
                  List<String> items = entry23.flag && !entry23.items.isEmpty() && subList.size() - i <= 8 ? (List<String>)(List)List.copyOf(entry23.items) : List.<String>of();
                  data2s.add(new AiChatStorage.Data2(entry23.flag, entry23.text(), items));
               }
            }

            String str = aiSettingsStore.method4469(shownProvider);
            String string = aiSettingsStore.method4472(shownProvider);
            String str2 = aiSettingsStore.method4474(shownProvider);
            this.busy = true;
            this.flag = false;
            this.entry2 = entry;
            Thread.ofPlatform().daemon(true).name("zenith-ai").start(() -> {
               try {
                  String var10x = method2771(data3);
                  ArrayList var11x = new ArrayList();
                  int var12x = 6;

                  for (AiChatStorage.Data2 var14x : data2s) {
                     ArrayList var15x = new ArrayList();
                     var var16x = var14x.getItems().iterator();

                     while (true) {
                        if (var16x.hasNext()) {
                           String var17x = (String)var16x.next();
                           if (var12x > 0) {
                              try {
                                 var15x.add(AiImageStore.method4636(var17x));
                                 var12x--;
                              } catch (IOException e) {
                              }
                              continue;
                           }
                        }

                        if (!var14x.text().isBlank() || !var15x.isEmpty()) {
                           var11x.add(new AiStreamClient.Data(var14x.isFlag(), var14x.text(), var15x));
                        }
                        break;
                     }
                  }

                  while (!var11x.isEmpty() && !((AiStreamClient.Data)var11x.get(0)).isFlag()) {
                     var11x.remove(0);
                  }

                  if (!this.flag) {
                     AiStreamClient.method4739(shownProvider, str, string, str2, var10x, var11x, var2xx -> {
                        entry22.method2773(var2xx);
                        this.revision++;
                     }, () -> this.flag);
                  }

                  if (this.flag && entry22.text().isEmpty()) {
                     entry22.error = "остановлено";
                  }
               } catch (Exception e) {
                  entry22.error = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
               } finally {
                  entry22.flag2 = false;
                  this.busy = false;
                  this.entry2 = null;
                  synchronized (this) {
                     if (!entry.deleted) {
                        this.method2754(entry);
                        this.method2755(entry);
                     }
                  }

                  this.revision++;
                  if (!this.flag && !entry.deleted && entry22.error.isEmpty() && !entry22.method2775().isEmpty() && AiSettingsStore.method4467().isFlag2()) {
                     Minecraft.getInstance().execute(() -> {
                        if (!entry.deleted) {
                           this.method2767(hostOrNull, entry22);
                        }
                     });
                  }
               }
            });
         }
      }
   }

   public static List<AiChatStorage.Data> method2765(String text) {
      ArrayList<AiChatStorage.Data> datas = new ArrayList<>();
      Matcher matcher = pattern.matcher(text);

      while (matcher.find()) {
         String str = matcher.group(1).strip().replace('\\', '/');

         while (str.startsWith("./") || str.startsWith("/")) {
            str = str.substring(str.startsWith("./") ? 2 : 1);
         }

         if (!str.isEmpty() && !str.contains("..")) {
            String group = matcher.group(2);
            String anObject = str;
            datas.removeIf(var1x -> var1x.getText().equals(anObject));
            datas.add(new AiChatStorage.Data(anObject, group.endsWith("\n") ? group : group + "\n"));
         }
      }

      return datas;
   }

   private AiChatStorage.Entry method2766(AiChatStorage.Entry2 entry2) {
      for (AiChatStorage.Entry entry : this.items) {
         if (entry.items.contains(entry2)) {
            return entry;
         }
      }

      return this.entry;
   }

   public void method2767(Storage hostOrNull, AiChatStorage.Entry2 entry2) {
      List<AiChatStorage.Data> datas = entry2.method2775();
      if (!datas.isEmpty() && hostOrNull != null) {
         AiChatStorage.Entry entry = this.method2766(entry2);

         try {
            Script workspace = entry.text2 == null ? null : hostOrNull.method1294(entry.text2);
            if (workspace == null) {
               String string = "ai-script";

               for (AiChatStorage.Data data : datas) {
                  Matcher matcher = pattern2.matcher(data.getText2());
                  if (data.getText().endsWith("main.js") && matcher.find()) {
                     string = matcher.group(1).strip();
                     break;
                  }
               }

               String str = Storage.method1303(string);
               String argument = str;

               for (int i = 2; hostOrNull.method1294(argument) != null || Files.exists(hostOrNull.getPath().resolve(argument)); i++) {
                  argument = str + "_" + i;
               }

               workspace = hostOrNull.method1340(argument, "empty");
               if (workspace == null) {
                  throw new IOException("не удалось создать скрипт");
               }

               entry.text2 = workspace.getId();
            }

            int n = 0;

            for (AiChatStorage.Data data : datas) {
               Path path2 = workspace.getPath() == null && data.getText().endsWith(".js") ? workspace.getPath2() : hostOrNull.method1346(workspace, data.getText());
               hostOrNull.method1347(workspace, path2, data.getText2());
               n++;
            }

            if (!hostOrNull.method1297(workspace)) {
               hostOrNull.method1298(workspace, true);
            } else {
               hostOrNull.method1299(workspace);
            }

            entry2.flag4 = true;
            String error = workspace.getError();
            if (error != null && !error.isBlank()) {
               entry2.text = "записал " + method2768(n) + ", но скрипт упал: " + error;
               entry2.flag3 = true;
               if (AiSettingsStore.method4467().isFlag3() && entry.intValue < 2) {
                  entry.intValue++;
                  this.method2755(entry);
                  this.revision++;
                  this.method2763(hostOrNull, entry, "Скрипт после применения упал с ошибкой:\n" + error + "\nИсправь и пришли исправленные файлы целиком.", true);
                  return;
               }
            } else {
               entry2.text = "готово: " + method2768(n) + " → «" + workspace.method1403() + "» запущен";
               entry2.flag3 = false;
            }
         } catch (Exception e) {
            entry2.text = "не применилось: " + e.getMessage();
            entry2.flag3 = true;
         }

         this.method2755(entry);
         this.revision++;
      }
   }

   private static String method2768(int n) {
      int n2 = n % 10;
      int n3 = n % 100;
      String str = n2 == 1 && n3 != 11 ? "файл" : (n2 < 2 || n2 > 4 || n3 >= 12 && n3 <= 14 ? "файлов" : "файла");
      return n + " " + str;
   }

   public void method2769(Storage hostOrNull) {
      Script workspace = this.entry.text2 == null ? null : hostOrNull.method1294(this.entry.text2);
      if (workspace != null) {
         StringBuilder builder = new StringBuilder("Вот что пишет скрипт:\n");
         if (!workspace.getError().isBlank()) {
            builder.append("Ошибка: ").append(workspace.getError()).append('\n');
         }

         List<Storage.Data2> data2s = hostOrNull.method1349(workspace);

         for (Storage.Data2 sub : data2s.subList(Math.max(0, data2s.size() - 15), data2s.size())) {
            builder.append('[').append(sub.getText2()).append("] ").append(sub.text()).append('\n');
         }

         builder.append("Разберись и исправь.");
         this.method2757(hostOrNull, builder.toString());
      }
   }

   private static AiChatStorage.Data3 method2770(Storage hostOrNull, AiChatStorage.Entry entry) {
      Path path = hostOrNull.getPath().resolve("API.md");

      Map items;
      try {
         items = hostOrNull.method1343();
      } catch (RuntimeException e) {
         items = Map.of();
      }

      String text = ScriptHeaderParser.method1000("hud", "Пример");
      Script workspace = entry.text2 == null ? null : hostOrNull.method1294(entry.text2);
      if (workspace == null) {
         return new AiChatStorage.Data3(path, items, text, null, "", null, false, List.of(), List.of());
      } else {
         String str = "# Текущий скрипт: "
            + workspace.method1403()
            + " (папка "
            + workspace.getId()
            + ")\nСостояние: "
            + workspace.getType2()
            + (workspace.getError().isBlank() ? "" : ", ошибка: " + workspace.getError())
            + "\n\n";
         Path parent = workspace.getPath() == null ? workspace.getPath2().getParent() : workspace.getPath();
         return new AiChatStorage.Data3(path, items, text, workspace, str, parent, workspace.getPath() == null, hostOrNull.method1345(workspace), hostOrNull.method1349(workspace));
      }
   }

   private static String method2771(AiChatStorage.Data3 data3) {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "Ты — встроенный помощник Zenith Client по JS-скриптам для Minecraft. Общайся как друг: просто, на «ты», без канцелярита.\nИгрок описывает, что хочет, — ты делаешь рабочий скрипт. Если просьба шуточная или странная — всё равно делай, если это можно сделать API.\nОтвечай по-русски и коротко: одна-две фразы, что сделал и КАК ВКЛЮЧИТЬ (в какой категории меню лежит модуль, какой бинд), потом файлы.\nЕсли чего-то в API нет — честно скажи и предложи ближайший вариант. Уточняющий вопрос задавай, только если без него никак.\n\nИгрок может прикладывать картинки (скриншоты игры, рисунки, фото): внимательно посмотри на них и учитывай —\nнапример, «построй как на скрине» значит повторить форму с картинки блок в блок.\n\nКак устроено:\n- Переключатель скрипта в Scripts только запускает скрипт. Модуль из Module(...) появляется в меню клиента в своей категории\n  (combat/movement/render/player/misc) и включается там отдельно или биндом. Если игрок хочет «сразу работало» — enabled: true.\n- Движок Rhino (ES6) в песочнице: нет Java, import, fetch, DOM, Promise-API браузера. Есть setTimeout/setInterval, include(),\n  require(\"id\") для чужих exports и все объекты из справочника ниже. Используй var/let/const, стрелочные функции можно.\n\nПравила кода:\n- Каждый изменённый или новый файл присылай ЦЕЛИКОМ в отдельном блоке ровно такого вида:\n```file:main.js\n...весь код файла...\n```\n- Путь — относительно папки скрипта. Главный файл — main.js, остальные подключаются через include(\"имя.js\").\n- main.js начинается с шапки: // @name, // @author, // @version, // @description, // @api 3 (можно // @tags a,b; для скриптов в маркет — // @permissions и // @net).\n- Для http.* домены обязательно перечисли в шапке: // @net example.com, api.site.org — иначе запрос отклонится.\n- Если правишь существующий скрипт — сохрани его имя и поднимай @version. Не присылай файлы, которые не менялись.\n- Если просят объяснить или спрашивают — отвечай без блоков файлов.\n\nЧастые ошибки — не делай так:\n- Сущности (player, entity, world.players()) отдают значения МЕТОДАМИ: player.x(), e.health(), e.name(). Не e.x.\n- Предметы, блоки, эффекты — снимки с ПОЛЯМИ: item.id, item.count, item.block, block.air, block.id. Не item.id().\n- world.block(x,y,z) и world.blockAt(x,y,z) возвращают объект блока (block.air, block.solid, block.id); world.blockId(...) — строку id.\n- player.place(x,y,z) ставит блок из руки В точку и требует опору рядом (земля снизу или стена сбоку): строй снизу вверх,\n  первый слой на уровне ног Math.floor(player.y()), не выше 5 блоков от игрока. place возвращает true/false — проверяй.\n- Блок в руку: var i = player.inventory.findBlock(); if (i >= 0) player.inventory.toHotbar(i); — индексы инвентаря 0-35 (0-8 хотбар).\n- Долгие действия (постройка, копание) делай по одному шагу за тик в events.update с задержкой, а не циклом за один вызов:\n  у одного вызова лимит 250 мс, и сервер не примет 50 блоков за тик.\n- Модуль: читай настройки через .get(), проверяй mod.enabled() в обработчиках, в mod.onEnable готовь состояние.\n- render.* работает только внутри events.render2d, render3d.* — только внутри events.render3d.\n- Цвета — 0xAARRGGBB, все 8 цифр (0xFFFF0000 — красный).\n- Свои картинки и звуки игрок кладёт в папку скрипта (images/, sounds/); если их нет — используй ванильные звуки,\n  particles.text/burst с символами и image.create.\n\n"
      );

      try {
         Path path = data3.getPath();
         if (Files.isRegularFile(path)) {
            builder.append("# Справочник API\n\n").append(Files.readString(path, StandardCharsets.UTF_8)).append("\n\n");
         }
      } catch (IOException e) {
      }

      var items = data3.getItems();
      if (!items.isEmpty()) {
         builder.append("# Точный список глобалов и их членов (из движка — используй только их)\n");

         for (Map.Entry entry : items.entrySet()) {
            builder.append((String)entry.getKey()).append(": ").append(String.join(", ", (Iterable<? extends CharSequence>)entry.getValue())).append('\n');
         }

         builder.append('\n');
      }

      builder.append("# Пример рабочего скрипта\n```file:main.js\n").append(data3.getText()).append("```\n\n");
      if (data3.script() == null) {
         builder.append("# Текущий скрипт\nПока нет: игрок создаёт новый. Придумай короткое понятное @name.\n");
         return builder.toString();
      } else {
         builder.append(data3.getText2());
         int n = 0;
         int n2 = 0;
         Path path2 = data3.getPath2();

         for (Path path : data3.getItems2()) {
            if (n2++ >= 40) {
               break;
            }

            try {
               String string = Files.readString(path, StandardCharsets.UTF_8);
               if (n + string.length() > 160000) {
                  builder.append("(файл ").append(path2.relativize(path)).append(" не показан — лимит контекста)\n");
               } else {
                  n += string.length();
                  String str = data3.isFlag() ? "main.js" : path2.relativize(path).toString().replace('\\', '/');
                  builder.append("```file:").append(str).append('\n').append(string).append(string.endsWith("\n") ? "" : "\n").append("```\n\n");
               }
            } catch (IOException e) {
            }
         }

         List<Storage.Data2> data2s = data3.getItems3();
         if (!data2s.isEmpty()) {
            builder.append("# Последние строки журнала скрипта\n");

            for (Storage.Data2 sub : data2s.subList(Math.max(0, data2s.size() - 10), data2s.size())) {
               builder.append('[').append(sub.getText2()).append("] ").append(sub.text()).append('\n');
            }
         }

         return builder.toString();
      }
   }

   public static final class Data {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data(String text, String str) {
         this.text = text;
         this.text2 = str;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   private static final class Data2 {
      private final boolean flag;
      private final String text;
      private final List<String> items;

      public boolean flag() {
         return this.flag;
      }

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, text, items);
      }

      @Override
      public String toString() {
         return "Data2[" + "flag=" + this.flag + ", " + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      private Data2(boolean flag, String text, List<String> items) {
         this.flag = flag;
         this.text = text;
         this.items = items;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   private static final class Data3 {
      private final Path path;
      private final Map<String, List<String>> items;
      private final String text;
      private final Script script;
      private final String text2;
      private final Path path2;
      private final boolean flag;
      private final List<Path> items2;
      private final List<Storage.Data2> items3;

      public Path path() {
         return this.path;
      }

      public Map<String, List<String>> items() {
         return this.items;
      }

      public String text() {
         return this.text;
      }

      public Script script() {
         return this.script;
      }

      public String text2() {
         return this.text2;
      }

      public Path path2() {
         return this.path2;
      }

      public boolean flag() {
         return this.flag;
      }

      public List<Path> items2() {
         return this.items2;
      }

      public List<Storage.Data2> items3() {
         return this.items3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.path2, other.path2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.items3, other.items3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(path, items, text, script, text2, path2, flag, items2, items3);
      }

      @Override
      public String toString() {
         return "Data3[" + "path=" + this.path + ", " + "items=" + this.items + ", " + "text=" + this.text + ", " + "script=" + this.script + ", " + "text2=" + this.text2 + ", " + "path2=" + this.path2 + ", " + "flag=" + this.flag + ", " + "items2=" + this.items2 + ", " + "items3=" + this.items3 + "]";
      }

      private Data3(
         Path path, Map<String, List<String>> items, String text, Script script, String str, Path path2, boolean flag, List<Path> paths, List<Storage.Data2> data2s
      ) {
         this.path = path;
         this.items = items;
         this.text = text;
         this.script = script;
         this.text2 = str;
         this.path2 = path2;
         this.flag = flag;
         this.items2 = paths;
         this.items3 = data2s;
      }

      public Path getPath() {
         return this.path;
      }

      public Map<String, List<String>> getItems() {
         return this.items;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public Path getPath2() {
         return this.path2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<Path> getItems2() {
         return this.items2;
      }

      public List<Storage.Data2> getItems3() {
         return this.items3;
      }
   }

   public static final class Entry {
      private final String text;
      private final long longValue;
      private volatile long longValue2;
      private volatile String title = "";
      private volatile String text2;
      private final List<AiChatStorage.Entry2> items = new CopyOnWriteArrayList<>();
      private int intValue;
      private volatile boolean deleted;

      private Entry(String text, long longValue) {
         this.text = text;
         this.longValue = longValue;
         this.longValue2 = longValue;
      }

      public String getText() {
         return this.text;
      }

      public String title() {
         return this.title;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText2() {
         return this.text2;
      }

      public List<AiChatStorage.Entry2> getItems() {
         return this.items;
      }

      public boolean empty() {
         return this.items.isEmpty();
      }

      public String method2772() {
         for (int i = this.items.size() - 1; i >= 0; i--) {
            AiChatStorage.Entry2 entry2 = this.items.get(i);
            String str = entry2.method2774();
            if (!str.isEmpty()) {
               return (entry2.isFlag() ? "ты: " : "") + str;
            }
         }

         return "";
      }
   }

   public static final class Entry2 {
      private final boolean flag;
      private final StringBuilder stringBuilder = new StringBuilder();
      private final long longValue;
      private volatile boolean flag2;
      private volatile String error = "";
      private volatile String text = "";
      private volatile boolean flag3;
      private volatile boolean flag4;
      private volatile boolean flag5;
      private volatile String text2 = "";
      private volatile boolean flag6;
      private final List<String> items = new CopyOnWriteArrayList<>();
      private String text3;
      private String text4;
      private int intValue = -1;
      private List<AiChatStorage.Data> files = List.of();
      private int intValue2 = -1;

      private Entry2(boolean flag, String message, long longValue) {
         this.flag = flag;
         this.stringBuilder.append(message);
         this.longValue = longValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public synchronized String text() {
         if (this.text3 == null) {
            this.text3 = this.stringBuilder.toString();
         }

         return this.text3;
      }

      private synchronized void method2773(String str) {
         this.stringBuilder.append(str);
         this.text3 = null;
      }

      private synchronized String method2774() {
         if (this.intValue != this.stringBuilder.length()) {
            this.text4 = AiChatStorage.pattern3.matcher(this.stringBuilder).replaceAll(" [код] ").replace('\n', ' ').strip();
            this.intValue = this.stringBuilder.length();
         }

         return this.text4;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public boolean streaming() {
         return this.flag2;
      }

      public String error() {
         return this.error;
      }

      public String result() {
         return this.text;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public String getText2() {
         return this.text2;
      }

      public List<String> getItems() {
         return this.items;
      }

      public boolean note() {
         return this.flag6;
      }

      public List<AiChatStorage.Data> method2775() {
         String text;
         synchronized (this) {
            if (this.intValue2 == this.stringBuilder.length()) {
               return this.files;
            }

            text = this.text();
         }

         var files = List.copyOf(AiChatStorage.method2765(text));
         synchronized (this) {
            this.files = files;
            this.intValue2 = text.length();
            return files;
         }
      }

      private JsonObject method2776() {
         JsonObject json = new JsonObject();
         json.addProperty("user", this.flag);
         json.addProperty("text", this.text());
         json.addProperty("at", this.longValue);
         if (!this.error.isEmpty()) {
            json.addProperty("error", this.error);
         }

         if (!this.text.isEmpty()) {
            json.addProperty("result", this.text);
         }

         if (this.flag3) {
            json.addProperty("resultError", true);
         }

         if (this.flag4) {
            json.addProperty("applied", true);
         }

         if (this.flag5) {
            json.addProperty("automatic", true);
         }

         if (!this.text2.isEmpty()) {
            json.addProperty("provider", this.text2);
         }

         if (this.flag6) {
            json.addProperty("note", true);
         }

         if (!this.items.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            this.items.forEach(jsonArray::add);
            json.add("images", jsonArray);
         }

         return json;
      }

      private static AiChatStorage.Entry2 method2777(JsonObject jsonObject) {
         AiChatStorage.Entry2 entry2 = new AiChatStorage.Entry2(jsonObject.get("user").getAsBoolean(), jsonObject.get("text").getAsString(), jsonObject.get("at").getAsLong());
         if (jsonObject.has("error")) {
            entry2.error = jsonObject.get("error").getAsString();
         }

         if (jsonObject.has("result")) {
            entry2.text = jsonObject.get("result").getAsString();
         }

         entry2.flag3 = jsonObject.has("resultError") && jsonObject.get("resultError").getAsBoolean();
         entry2.flag4 = jsonObject.has("applied") && jsonObject.get("applied").getAsBoolean();
         entry2.flag5 = jsonObject.has("automatic") && jsonObject.get("automatic").getAsBoolean();
         if (jsonObject.has("provider")) {
            entry2.text2 = jsonObject.get("provider").getAsString();
         }

         entry2.flag6 = jsonObject.has("note") && jsonObject.get("note").getAsBoolean();
         if (jsonObject.has("images")) {
            for (JsonElement json : jsonObject.getAsJsonArray("images")) {
               entry2.items.add(json.getAsString());
            }
         }

         return entry2;
      }
   }
}
