package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.AiChatStorage;
import org.zenith.utility.api.AiSettingsStore;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.ScriptStorageFile;
import org.zenith.utility.api.Server;
import org.zenith.utility.config.ScriptApiDocs;
import org.zenith.utility.misc.ImageLoader;
import org.zenith.utility.misc.ScriptHeaderParser;

public final class Storage {
   public static final int CONST_intValue = 3;
   public static final int CONST_intValue2 = 1048576;
   public static final int CONST_intValue3 = 5;
   private static final int CONST_intValue4 = 1000;
   private static final Logger logger = LogUtils.getLogger();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private final Path path;
   private final Path path2;
   private final Map<String, Script> items = new LinkedHashMap<>();
   private final Deque<Storage.Data2> deque = new ArrayDeque<>();
   private JsonObject jsonObject = new JsonObject();
   private boolean flag;
   private int ticks;
   private int intValue;
   private boolean flag2;
   private final List<Module> items2 = new ArrayList<>();
   private final ScriptEventWatcher scriptEventWatcher = new ScriptEventWatcher(this);
   private final Map<String, Map<String, JsonElement>> items3 = new HashMap<>();
   private static final long CONST_longValue = 350000000L;
   private static final int CONST_intValue5 = 20;
   private final Map<String, long[]> items4 = new HashMap<>();
   private long longValue;
   private Script script;
   private Map<String, List<String>> items5;
   private static final Pattern pattern = Pattern.compile("\\(([^():]+):(\\d+)\\)");
   public static final List<String> items6 = List.of("name", "author", "version", "description", "tags", "link", "color", "api");
   private static final Storage.Data data = new Storage.Data(-1L, -1L, List.of());
   private final Map<Path, Storage.Data> items7 = new ConcurrentHashMap<>();

   public Storage() {
      this.path = Zenith.file.toPath().resolve("scripts");
      this.path2 = this.path.resolve(".state.json");

      try {
         Files.createDirectories(this.path);
         Files.createDirectories(this.path.resolve(".data"));
      } catch (IOException e) {
         logger.error("Не удалось создать папку скриптов", e);
      }

      this.method1338();
      ScriptApiDocs.method4768(this.path);
      EventManager.register(this);
      this.method1301(true);
      Server.getInstance().method217();
      Thread.ofPlatform().daemon(true).name("zenith-ai-preload").start(() -> {
         AiSettingsStore.method4467();
         AiChatStorage.method2741();
      });
   }

   public void method1292(String source) {
      if (source.length() > 1048576) {
         throw new IllegalArgumentException("код больше 1 МБ");
      } else {
         method1309(source, "check.js");
      }
   }

   public Path getPath() {
      return this.path;
   }

   public Collection<Script> method1293() {
      return this.items.values();
   }

   public Script method1294(String argument) {
      if (argument == null) {
         return null;
      } else {
         Script script = this.items.get(argument);
         if (script != null) {
            return script;
         } else {
            for (Script script2 : this.items.values()) {
               if (script2.getId().equalsIgnoreCase(argument) || script2.method1403().equalsIgnoreCase(argument)) {
                  return script2;
               }
            }

            return null;
         }
      }
   }

   public List<Storage.Data2> method1295() {
      synchronized (this.deque) {
         return new ArrayList<>(this.deque);
      }
   }

   public void method1296() {
      synchronized (this.deque) {
         this.deque.clear();
         this.longValue++;
      }
   }

   public boolean method1297(Script script) {
      JsonObject json = this.method1337(script.getId());
      return json.has("enabled") && json.get("enabled").getAsBoolean();
   }

   public void method1298(Script workspace, boolean flag) {
      this.method1337(workspace.getId()).addProperty("enabled", flag);
      this.flag = true;
      if (flag) {
         if (!workspace.isRunning()) {
            this.method1308(workspace, null);
         }
      } else if (workspace.type2 != Script.Type2.STOPPED || !workspace.items3.isEmpty()) {
         this.method1319(workspace, true);
         workspace.type2 = Script.Type2.STOPPED;
         workspace.error = "";
      }
   }

   public void method1299(Script workspace) {
      if (this.method1297(workspace)) {
         this.method1308(workspace, workspace.isRunning() ? workspace.source : null);
      } else {
         workspace.longValue9 = this.method1304(workspace);
         this.method1307(workspace, this.method1305(workspace));
      }
   }

   public void method1300() {
      this.method1301(false);

      for (Script workspace : new ArrayList<>(this.items.values())) {
         this.method1299(workspace);
      }
   }

   private void method1301(boolean flag) {
      ArrayList<Path> paths = new ArrayList();

      try (Stream<java.nio.file.Path> list = Files.list(this.path)) {
         list.sorted().forEach(paths::add);
      } catch (UncheckedIOException | IOException e) {
         return;
      }

      LinkedHashMap map = new LinkedHashMap();

      for (Path path : paths) {
         String string = path.getFileName().toString();
         if (!string.startsWith(".")) {
            if (Files.isDirectory(path)) {
               Path path2 = path.resolve("main.js");
               if (!Files.isRegularFile(path2)) {
                  path2 = path.resolve("src").resolve("main.js");
               }

               if (Files.isRegularFile(path2)) {
                  map.put(this.method1302(map, method1303(string), path), new Path[]{path, path2});
               }
            } else if (string.toLowerCase(Locale.ROOT).endsWith(".js")) {
               String key = method1303(string.substring(0, string.length() - 3));
               if (!map.containsKey(key)) {
                  map.put(key, new Path[]{null, path});
               }
            }
         }
      }

      var iterator = this.items.entrySet().iterator();

      while (iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         if (!map.containsKey(entry.getKey())) {
            this.method1319((Script)entry.getValue(), true);
            iterator.remove();
            this.method1327((Script)entry.getValue(), "info", "скрипт удалён с диска");
         }
      }

      for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
         Map.Entry entry = (Map.Entry)obj;
         if (!this.items.containsKey(entry.getKey())) {
            Script script = new Script(this, (String)entry.getKey(), ((Path[])entry.getValue())[0], ((Path[])entry.getValue())[1]);
            this.items.put(script.getId(), script);
            script.longValue9 = this.method1304(script);
            this.method1307(script, this.method1305(script));
            script.type2 = Script.Type2.STOPPED;
            if (!flag) {
               this.method1327(script, "info", "найден новый скрипт");
            }

            if (this.method1297(script)) {
               this.method1308(script, null);
            }
         }
      }
   }

   private String method1302(Map<String, Path[]> map, String str, Path path) {
      Script script = this.items.get(str);
      if (map.containsKey(str) || script != null && !path.equals(script.getPath())) {
         int n = 2;

         while (true) {
            String str2 = str + "~" + n;
            Script script2 = this.items.get(str2);
            if (!map.containsKey(str2) && (script2 == null || path.equals(script2.getPath()))) {
               return str2;
            }

            n++;
         }
      } else {
         return str;
      }
   }

   public static String method1303(String string) {
      String str = string.replaceAll("[^\\p{L}\\p{N}_.-]", "_");
      return str.length() > 48 ? str.substring(0, 48) : str;
   }

   private long method1304(Script workspace) {
      long l = 17L;

      try {
         if (workspace.getPath() == null) {
            BasicFileAttributes attributes = Files.readAttributes(workspace.getPath2(), BasicFileAttributes.class);
            l = l * 31L + attributes.lastModifiedTime().toMillis();
            return l * 31L + attributes.size();
         }

         final TreeMap map = new TreeMap();
         Files.walkFileTree(workspace.getPath(), EnumSet.noneOf(FileVisitOption.class), 4, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes var2x) {
               if (!file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".js")) {
                  return FileVisitResult.CONTINUE;
               } else {
                  BasicFileAttributes attributes = var2x;
                  if (var2x.isSymbolicLink()) {
                     try {
                        attributes = Files.readAttributes(file, BasicFileAttributes.class);
                     } catch (IOException e) {
                        return FileVisitResult.CONTINUE;
                     }
                  }

                  if (attributes.isRegularFile()) {
                     map.put(file, new long[]{attributes.lastModifiedTime().toMillis(), attributes.size()});
                  }

                  return FileVisitResult.CONTINUE;
               }
            }

            public FileVisitResult visitFileFailed(Path file, IOException var2x) {
               return FileVisitResult.CONTINUE;
            }

            public FileVisitResult postVisitDirectory(Path dir, IOException var2x) {
               return FileVisitResult.CONTINUE;
            }
         });

         for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
            Map.Entry entry = (Map.Entry)obj;
            l = l * 31L + ((Path)entry.getKey()).toString().hashCode();
            l = l * 31L + ((long[])entry.getValue())[0];
            l = l * 31L + ((long[])entry.getValue())[1];
         }
      } catch (UncheckedIOException | IOException e) {
      }

      return l;
   }

   private String method1305(Script workspace) {
      try {
         return this.method1306(workspace.getPath2());
      } catch (Exception e) {
         return "";
      }
   }

   public String method1306(Path path2) throws IOException {
      if (Files.size(path2) > 1048576L) {
         throw new IOException("файл больше 1 МБ");
      } else {
         String string = Files.readString(path2, StandardCharsets.UTF_8);
         return string.startsWith("\ufeff") ? string.substring(1) : string;
      }
   }

   private void method1307(Script workspace, String source) {
      workspace.name = workspace.getId();
      workspace.text2 = "";
      workspace.version = "";
      workspace.description = "";
      workspace.text3 = "";
      workspace.text4 = "";
      workspace.text5 = "";
      workspace.text6 = "";
      workspace.color = -1;
      String[] strings = source.split("\n", 40);

      for (int i = 0; i < Math.min(strings.length, 30); i++) {
         String str = strings[i].trim();
         if (str.startsWith("//")) {
            str = str.substring(2).trim();
            if (str.startsWith("@")) {
               int indexOf = str.indexOf(32);
               if (indexOf >= 0) {
                  String lowerCase = str.substring(1, indexOf).toLowerCase(Locale.ROOT);
                  String name = str.substring(indexOf + 1).trim();
                  if (name.length() > 120) {
                     name = name.substring(0, 120);
                  }

                  switch (lowerCase) {
                     case "name":
                        workspace.name = name;
                        break;
                     case "author":
                        workspace.text2 = name;
                        break;
                     case "version":
                        workspace.version = name;
                        break;
                     case "description":
                        workspace.description = name;
                        break;
                     case "tags":
                        workspace.text3 = name;
                        break;
                     case "link":
                        workspace.text4 = name;
                        break;
                     case "net":
                        workspace.text5 = name;
                        break;
                     case "permissions":
                     case "perms":
                        workspace.text6 = name;
                        break;
                     case "color":
                        try {
                           workspace.color = (int)Long.parseLong(name.replace("#", ""), 16) | 0xFF000000;
                        } catch (NumberFormatException e) {
                        }
                        break;
                     case "api":
                        try {
                           workspace.intValue3 = Integer.parseInt(name);
                        } catch (NumberFormatException e) {
                        }
                  }
               }
            }
         }
      }
   }

   private void method1308(Script workspace, String source) {
      String str;
      try {
         str = this.method1306(workspace.getPath2());
      } catch (Exception e) {
         this.method1323(workspace, "не удалось прочитать файл: " + e.getMessage(), true);
         return;
      }

      workspace.longValue9 = this.method1304(workspace);

      org.mozilla.javascript.Script script;
      try {
         script = method1309(str, workspace.getId() + "/main.js");
      } catch (RhinoException e) {
         String error = method1325(e);
         if (source != null && workspace.isRunning()) {
            workspace.error = error;
            this.method1327(workspace, "error", "не собрался, работает прошлая версия: " + error);
            this.method1324(workspace, error);
            return;
         }

         this.method1323(workspace, error, true);
         return;
      }

      if (workspace.isRunning() || !workspace.items3.isEmpty()) {
         this.method1319(workspace, true);
      }

      OriginUtil.Data2 data2 = OriginUtil.method6921(workspace);
      workspace.flag = data2 != null && !data2.mine();
      this.method1307(workspace, str);
      if (workspace.intValue3 > 3) {
         this.method1323(workspace, "скрипту нужна версия API " + workspace.intValue3 + ", у клиента 3", true);
      } else {
         String error = this.method1310(workspace, script, str);
         if (error != null) {
            if (source != null) {
               this.method1327(workspace, "error", "ошибка при запуске, возвращаю прошлую версию: " + error);
               this.method1324(workspace, error);

               try {
                  org.mozilla.javascript.Script script2 = method1309(source, workspace.getId() + "/main.js");
                  this.method1307(workspace, source);
                  String str2 = this.method1310(workspace, script2, source);
                  if (str2 == null) {
                     workspace.error = error;
                     return;
                  }
               } catch (RhinoException e) {
               }
            }

            this.method1323(workspace, error, true);
         }
      }
   }

   public static org.mozilla.javascript.Script method1309(String source, String str) {
      Context context = ScriptContextFactory.method7409();

      org.mozilla.javascript.Script compileString;
      try {
         compileString = context.compileString(source, str, 1, null);
      } finally {
         Context.exit();
      }

      return compileString;
   }

   private String method1310(Script workspace, org.mozilla.javascript.Script script, String source) {
      workspace.error = "";
      workspace.intValue4 = 0;
      workspace.jsonObject = this.method1337(workspace.getId());
      workspace.scriptStorageFile = new ScriptStorageFile(this.method1311(workspace));
      Context context = ScriptContextFactory.method7409();

      try {
         workspace.scope = ScriptContextFactory.method7408(context);
         ScriptModuleApi.method325(this, workspace, workspace.scope);
      } catch (RuntimeException e) {
         Context.exit();
         this.method1320(workspace);
         return "не удалось подготовить окружение: " + e.getMessage();
      }

      Context.exit();
      workspace.type2 = Script.Type2.RUNNING;

      try {
         ScriptContextFactory.method7410(3000L, (var1x, var2x, var3x, var4x) -> script.exec(var1x, var2x), workspace.scope, new Object[0]);
      } catch (Throwable e) {
         workspace.type2 = Script.Type2.FAILED;
         this.method1319(workspace, false);
         return method1325(e);
      }

      workspace.source = source;
      this.method1312(workspace);
      this.method1331(workspace, Script.Type.ENABLE);
      this.flag2 = true;
      this.method1327(workspace, "info", "запущен" + (workspace.version.isBlank() ? "" : " v" + workspace.version));
      return null;
   }

   private Path method1311(Script workspace) {
      return workspace.getPath() != null ? workspace.getPath().resolve("storage.json") : this.path.resolve(".data").resolve(workspace.getId() + ".json");
   }

   private void method1312(Script workspace) {
      JsonObject jsonObject = workspace.jsonObject.has("modules") && workspace.jsonObject.get("modules").isJsonObject()
         ? workspace.jsonObject.getAsJsonObject("modules")
         : new JsonObject();

      for (Module1 module1 : workspace.items3.values()) {
         JsonObject json = jsonObject.has(module1.getName()) && jsonObject.get(module1.getName()).isJsonObject() ? jsonObject.getAsJsonObject(module1.getName()) : null;
         if (json == null) {
            if (module1.flag2 && !module1.isEnabledRaw()) {
               module1.toggle();
            }
         } else {
            if (json.has("keyCode")) {
               module1.setKeyCode(json.get("keyCode").getAsInt());
            }

            if (json.has("keyModifiers")) {
               module1.setKeyModifiers(json.get("keyModifiers").getAsInt());
            }

            if (json.has("enabled") && json.get("enabled").getAsBoolean() && !module1.isEnabledRaw()) {
               module1.toggle();
            }
         }
      }
   }

   public JsonObject method1313(Script workspace, String name) {
      JsonObject jsonObject = workspace.jsonObject.has("modules") && workspace.jsonObject.get("modules").isJsonObject() ? workspace.jsonObject.getAsJsonObject("modules") : null;
      return jsonObject != null && jsonObject.has(name) && jsonObject.get(name).isJsonObject() ? jsonObject.getAsJsonObject(name) : null;
   }

   public JsonObject method1314(Script workspace) {
      return workspace.jsonObject.has("settings") && workspace.jsonObject.get("settings").isJsonObject() ? workspace.jsonObject.getAsJsonObject("settings") : null;
   }

   private void method1315(Script workspace) {
      if (!workspace.items3.isEmpty() || !workspace.items6.isEmpty()) {
         JsonObject json = this.method1337(workspace.getId());
         boolean flag = false;
         JsonObject jsonObject;
         if (json.has("modules") && json.get("modules").isJsonObject()) {
            jsonObject = json.getAsJsonObject("modules");
         } else {
            jsonObject = new JsonObject();
            json.add("modules", jsonObject);
            flag = true;
         }

         for (Module1 module1 : workspace.items3.values()) {
            JsonObject json2 = module1.save();
            if (!json2.equals(jsonObject.get(module1.getName()))) {
               jsonObject.add(module1.getName(), json2);
               flag = true;
            }
         }

         if (!workspace.items6.isEmpty()) {
            JsonObject json2 = new JsonObject();

            for (Setting setting : workspace.items6.values()) {
               setting.safe(json2);
            }

            if (!json2.equals(json.get("settings"))) {
               json.add("settings", json2);
               flag = true;
            }
         }

         if (flag) {
            this.flag = true;
         }
      }
   }

   public JsonElement method1316(Script workspace, String str) {
      var map = this.items3.get(workspace.getId());
      return map == null ? null : (JsonElement)map.get(str);
   }

   public void method1317(Script workspace, String str) {
      var map = this.items3.get(workspace.getId());
      if (map != null) {
         map.remove(str);
      }
   }

   private void method1318(Script script) {
      if (!script.items.isEmpty()) {
         var map = this.items3.computeIfAbsent(script.getId(), str -> new HashMap<>());

         try {
            ScriptContextFactory.method7410(250L, (var2x, scriptable, var4x, objects) -> {
               for (Map.Entry entry : script.items.entrySet()) {
                  try {
                     map.put((String)entry.getKey(), ScriptValueUtils.method6271(entry.getValue(), 0));
                  } catch (Throwable e) {
                  }
               }

               return null;
            }, script.scope, new Object[0]);
         } catch (Throwable e) {
         }

         script.items.clear();
      }
   }

   public void method1319(Script workspace, boolean flag) {
      if (flag && workspace.isRunning()) {
         this.method1331(workspace, Script.Type.DISABLE);
      }

      this.method1318(workspace);
      this.method1315(workspace);

      for (Module1 module1 : workspace.items3.values()) {
         module1.release();
         if (module1.isEnabledRaw()) {
            module1.toggle();
         }

         Zenith.getInstance().getModuleManager().method2483(module1);
         this.items2.add(module1);
      }

      if (!workspace.items3.isEmpty()) {
         this.flag2 = true;
      }

      workspace.items3.clear();
      this.method1320(workspace);
      workspace.items5.clear();
      workspace.items4.clear();
      workspace.items6.clear();
      workspace.items7.clear();
      workspace.items8.clear();
      if (workspace.scriptStorageFile != null) {
         workspace.scriptStorageFile.flush();
      }

      workspace.scope = null;
      if (workspace.type2 == Script.Type2.RUNNING) {
         workspace.type2 = Script.Type2.STOPPED;
      }
   }

   private void method1320(Script workspace) {
      for (int i = workspace.items2.size() - 1; i >= 0; i--) {
         try {
            workspace.items2.get(i).close();
         } catch (Throwable e) {
            logger.warn("Скрипт {}: ошибка при снятии ресурса", workspace.getId(), e);
         }
      }

      workspace.items2.clear();
   }

   public Object method1321(Script workspace, Function function, Object... scriptableObject) {
      if (workspace != null && function != null && workspace.isRunning() && workspace.scope != null) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null && !minecraft.isSameThread()) {
            return null;
         } else {
            long nanoTime = System.nanoTime();

            try {
               Object obj = ScriptContextFactory.method7410(250L, function, workspace.scope, scriptableObject);
               workspace.intValue4 = 0;
               return obj;
            } catch (ScriptContextFactory.Entry e) {
               long now = System.currentTimeMillis();
               if (e.getMessage() != null && e.getMessage().contains("памят")) {
                  this.method1323(workspace, e.getMessage(), true);
               } else {
                  if (now - workspace.longValue > 30000L) {
                     workspace.longValue = now;
                     workspace.intValue = 0;
                  }

                  workspace.intValue++;
                  workspace.error = e.getMessage();
                  if (workspace.intValue >= 3) {
                     this.method1323(workspace, "трижды за 30 секунд превысил лимит времени — " + e.getMessage(), true);
                  } else {
                     this.method1327(workspace, "warn", "вызов прерван: " + e.getMessage() + " (" + workspace.intValue + "/3)");
                  }
               }
            } catch (StackOverflowError e) {
               this.method1323(workspace, "переполнение стека (бесконечная рекурсия?)", true);
            } catch (Throwable e) {
               String error = method1325(e);
               workspace.intValue4++;
               workspace.error = error;
               this.method1327(workspace, "error", error);
               if (workspace.intValue4 >= 5) {
                  this.method1323(workspace, "5 ошибок подряд, последняя: " + error, true);
               } else if (workspace.intValue4 == 1) {
                  this.method1324(workspace, error);
               }
            } finally {
               long l = System.nanoTime() - nanoTime;
               workspace.longValue4 += l;
               workspace.longValue5++;
               if (l > workspace.longValue6) {
                  workspace.longValue6 = l;
               }

               this.method1322(workspace, l);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private void method1322(Script workspace, long l) {
      long nanoTime = System.nanoTime();
      if (nanoTime - workspace.longValue2 >= 1000000000L) {
         if (workspace.longValue2 != 0L) {
            if (workspace.longValue3 > 350000000L) {
               workspace.intValue2++;
               if (workspace.intValue2 == 2) {
                  this.method1327(workspace, "warn", "скрипт занимает " + workspace.longValue3 / 1000000L + " мс в секунду — игра тормозит");
               }

               if (workspace.intValue2 >= 5 && workspace.isRunning()) {
                  workspace.longValue2 = nanoTime;
                  workspace.longValue3 = 0L;
                  workspace.intValue2 = 0;
                  this.method1323(workspace, "пять секунд подряд занимал больше 350 мс в секунду и тормозил игру", true);
                  return;
               }
            } else {
               workspace.intValue2 = 0;
            }
         }

         workspace.longValue2 = nanoTime;
         workspace.longValue3 = 0L;
      }

      workspace.longValue3 += l;
   }

   public void method1323(Script workspace, String error, boolean flag) {
      boolean running = workspace.isRunning();
      workspace.error = error;
      workspace.longValue8 = System.currentTimeMillis();
      this.method1327(workspace, "error", error);
      if (running || !workspace.items3.isEmpty()) {
         workspace.type2 = Script.Type2.FAILED;
         this.method1319(workspace, false);
      }

      workspace.type2 = Script.Type2.FAILED;
      if (flag) {
         this.method1324(workspace, "остановлен: " + error);
      }
   }

   private void method1324(Script workspace, String error) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.player != null) {
         String str = error.length() > 160 ? error.substring(0, 160) + "…" : error;
         ChatLogger.method2020(ChatLogger.Type.ERROR, "[JS] " + workspace.method1403() + ": " + str);
      }
   }

   public static String method1325(Throwable throwable) {
      if (throwable instanceof JavaScriptException javaScriptException) {
         Object obj = javaScriptException.getValue();

         String str;
         try {
            str = String.valueOf(ScriptContextFactory.method7410(250L, (var1x, var2x, var3x, objects) -> {
               if (obj instanceof Scriptable var5x) {
                  Object var6x = ScriptValueUtils.method6263(var5x, "message");
                  return ScriptValueUtils.method6253(var6x) ? Context.toString(obj) : Context.toString(var6x);
               } else {
                  return Context.toString(obj);
               }
            }, null, new Object[0]));
         } catch (Throwable e) {
            str = obj instanceof Scriptable scriptable ? "[object " + scriptable.getClassName() + "]" : String.valueOf(obj);
         }

         return str + method1326(javaScriptException);
      } else if (throwable instanceof EcmaError ecmaError) {
         return ecmaError.getName() + ": " + ecmaError.getErrorMessage() + method1326(ecmaError);
      } else if (throwable instanceof EvaluatorException evaluatorException) {
         return evaluatorException.details() + method1326(evaluatorException);
      } else if (throwable instanceof RhinoException rhinoException) {
         return rhinoException.details() + method1326(rhinoException);
      } else if (throwable instanceof ScriptContextFactory.Entry) {
         return throwable.getMessage();
      } else {
         String message = throwable.getMessage();
         return throwable.getClass().getSimpleName() + (message == null ? "" : ": " + message);
      }
   }

   private static String method1326(RhinoException rhinoException) {
      String sourceName = rhinoException.sourceName();
      int lineNumber = rhinoException.lineNumber();
      if (sourceName != null && lineNumber > 0) {
         int lastIndexOf = sourceName.lastIndexOf(47);
         return " (" + (lastIndexOf >= 0 ? sourceName.substring(lastIndexOf + 1) : sourceName) + ":" + lineNumber + ")";
      } else {
         return "";
      }
   }

   public void method1327(Script workspace, String anObject, String error) {
      String text = workspace == null ? "js" : workspace.method1403();
      if (error.length() > 2000) {
         error = error.substring(0, 2000) + "…";
      }

      boolean flag = "error".equals(anObject) || "warn".equals(anObject);
      boolean flag2 = flag;
      long l = 0L;
      long now = System.currentTimeMillis();
      synchronized (this.deque) {
         this.deque.addLast(new Storage.Data2(now, text, anObject, error));

         while (this.deque.size() > 1000) {
            this.deque.removeFirst();
         }

         this.longValue++;
         if (!flag) {
            long[] longs = this.items4.computeIfAbsent(text, str -> new long[3]);
            if (now - longs[0] >= 1000L) {
               l = longs[2];
               longs[0] = now;
               longs[1] = 0L;
               longs[2] = 0L;
            }

            if (longs[1] < 20L) {
               longs[1]++;
               flag2 = true;
            } else {
               longs[2]++;
            }
         }
      }

      if (l > 0L) {
         logger.info("[JS:{}] … ещё {} строк не продублировано в лог игры (смотри консоль скриптов)", text, l);
      }

      if (flag2) {
         if (flag) {
            logger.warn("[JS:{}] {}", text, error);
         } else {
            logger.info("[JS:{}] {}", text, error);
         }
      }
   }

   public long method1328() {
      synchronized (this.deque) {
         return this.longValue;
      }
   }

   public int method1329(Script workspace, Function function, long l, boolean flag) {
      if (workspace.items5.size() >= 256) {
         throw ScriptValueUtils.method6251("у скрипта больше 256 таймеров");
      } else {
         Storage.Entry entry = new Storage.Entry(++this.intValue, function, flag ? Math.max(50L, l) : l, flag);
         workspace.items5.add(entry);
         return entry.intValue;
      }
   }

   public void method1330(Script workspace, int n) {
      for (Storage.Entry entry : workspace.items5) {
         if (entry.intValue == n) {
            entry.cancelled = true;
         }
      }
   }

   public void method1331(Script workspace, Script.Type type, Object... scriptableObject) {
      for (Function function : new ArrayList<>(workspace.method1417(type))) {
         if (!workspace.isRunning() && type != Script.Type.DISABLE) {
            return;
         }

         if (type == Script.Type.DISABLE) {
            Script.Type2 type2 = workspace.type2;
            workspace.type2 = Script.Type2.RUNNING;
            this.method1321(workspace, function, scriptableObject);
            if (workspace.type2 == Script.Type2.RUNNING) {
               workspace.type2 = type2;
            }
         } else {
            this.method1321(workspace, function, scriptableObject);
         }
      }
   }

   public void method1332(Script.Type type, java.util.function.Function<Script, Object[]> function) {
      for (Script workspace : new ArrayList<>(this.items.values())) {
         if (workspace.isRunning() && !workspace.method1417(type).isEmpty()) {
            this.method1331(workspace, type, (Object[])function.apply(workspace));
         }
      }
   }

   @EventTarget(0)
   public void onTick(TickEvent event) {
      this.ticks++;
      if (this.ticks % 20 == 0) {
         this.method1334();
      }

      this.method1335();
      this.scriptEventWatcher.tick();
      if (this.ticks % 100 == 0) {
         this.flush();
      }

      if (this.flag2) {
         this.flag2 = false;
         this.method1336();
      }
   }

   @EventTarget(0)
   public void method1333(ChatSendEvent event) {
      if (event.method4603() && event.getMessage() != null) {
         String str = event.getMessage().trim();
         String prefix = Zenith.getInstance().getCommandManager().getPrefix();
         if (str.startsWith(prefix) && str.length() > prefix.length()) {
            String str2 = str.substring(prefix.length());
            int indexOf = str2.indexOf(32);
            String lowerCase = (indexOf < 0 ? str2 : str2.substring(0, indexOf)).toLowerCase(Locale.ROOT);
            String str3 = indexOf < 0 ? "" : str2.substring(indexOf + 1).trim();

            for (Script workspace : new ArrayList<>(this.items.values())) {
               var function = workspace.isRunning() ? workspace.items7.get(lowerCase) : null;
               if (function != null) {
                  event.cancel();
                  this.method1321(workspace, function, str3, ScriptValueUtils.method6268(workspace.scope, str3.isEmpty() ? new Object[0] : str3.split("\\s+")));
                  return;
               }
            }
         }
      }
   }

   private void method1334() {
      this.method1301(false);

      for (Script workspace : new ArrayList<>(this.items.values())) {
         long l = this.method1304(workspace);
         if (l != workspace.longValue9) {
            workspace.longValue9 = l;
            if (this.method1297(workspace)) {
               this.method1327(workspace, "info", "файл изменён, перезагружаю");
               this.method1308(workspace, workspace.isRunning() ? workspace.source : null);
            } else {
               this.method1307(workspace, this.method1305(workspace));
            }
         }
      }
   }

   private void method1335() {
      long now = System.currentTimeMillis();

      for (Script workspace : new ArrayList<>(this.items.values())) {
         if (workspace.isRunning() && !workspace.items5.isEmpty()) {
            for (Storage.Entry entry : new ArrayList<>(workspace.items5)) {
               if (!entry.cancelled && now >= entry.longValue2) {
                  if (entry.flag) {
                     entry.longValue2 = now + entry.longValue;
                  } else {
                     entry.cancelled = true;
                  }

                  this.method1321(workspace, entry.function);
                  if (!workspace.isRunning()) {
                     break;
                  }
               }
            }

            workspace.items5.removeIf(entry -> entry.cancelled);
         }
      }
   }

   private void method1336() {
      var moduleValues = new ArrayList<>(this.items2);
      this.items2.clear();

      try {
         if (Zenith.getInstance().getNLMenuScreen() != null) {
            Zenith.getInstance().getNLMenuScreen().refreshModules(moduleValues);
         }
      } catch (Throwable e) {
         logger.warn("Не удалось обновить меню после скриптов", e);
      }
   }

   public void flush() {
      for (Script workspace : this.items.values()) {
         if (workspace.isRunning()) {
            this.method1315(workspace);
         }

         if (workspace.scriptStorageFile != null) {
            workspace.scriptStorageFile.flush();
         }
      }

      if (this.flag) {
         this.flag = false;
         this.method1339();
      }
   }

   public JsonObject method1337(String id) {
      JsonElement jsonElement = this.jsonObject.get(id);
      if (jsonElement != null && jsonElement.isJsonObject()) {
         return jsonElement.getAsJsonObject();
      } else {
         JsonObject json = new JsonObject();
         this.jsonObject.add(id, json);
         return json;
      }
   }

   private void method1338() {
      try {
         if (Files.isRegularFile(this.path2)) {
            JsonElement string = JsonParser.parseString(Files.readString(this.path2, StandardCharsets.UTF_8));
            if (string.isJsonObject()) {
               this.jsonObject = string.getAsJsonObject();
            }
         }
      } catch (Exception e) {
         logger.warn("Не удалось прочитать состояние скриптов", e);
      }
   }

   private void method1339() {
      try {
         Path sibling = this.path2.resolveSibling(".state.json.tmp");
         Files.writeString(sibling, gson.toJson(this.jsonObject), StandardCharsets.UTF_8);
         Files.move(sibling, this.path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception e) {
         logger.warn("Не удалось сохранить состояние скриптов", e);
      }
   }

   public Script method1340(String argument, String string) throws IOException {
      String str = method1303(argument);
      if (!str.isBlank() && this.method1294(str) == null && !Files.exists(this.path.resolve(str))) {
         Path dir = this.path.resolve(str);
         Files.createDirectories(dir);
         Files.writeString(dir.resolve("main.js"), ScriptHeaderParser.method1000(string, argument), StandardCharsets.UTF_8);
         this.method1301(false);
         return this.method1294(str);
      } else {
         throw new IOException("скрипт с таким именем уже есть");
      }
   }

   public Object method1341(Script workspace, String source) {
      if (source.length() > 32768) {
         throw new IllegalArgumentException("код слишком длинный");
      } else {
         ScriptableObject scope;
         if (workspace != null && workspace.isRunning()) {
            scope = workspace.scope;
         } else {
            Script script = this.method1342();
            scope = script.scope;
         }

         org.mozilla.javascript.Script script = method1309(source, "console");
         return ScriptContextFactory.method7410(250L, (var1x, var2x, var3x, var4x) -> {
            Object var5x = script.exec(var1x, var2x);
            if (var5x instanceof Scriptable id && !(var5x instanceof Function)) {
               try {
                  return gson.toJson(ScriptValueUtils.method6271(id, 0));
               } catch (RuntimeException e) {
                  return Context.toString(var5x);
               }
            } else {
               return ScriptValueUtils.method6253(var5x) ? "undefined" : Context.toString(var5x);
            }
         }, scope, new Object[0]);
      }
   }

   private Script method1342() {
      if (this.script != null && this.script.isRunning()) {
         return this.script;
      } else {
         this.script = new Script(this, "console", null, this.path.resolve(".console.js"));
         this.script.jsonObject = new JsonObject();
         this.script.scriptStorageFile = new ScriptStorageFile(this.path.resolve(".data").resolve(".console.json"));
         Context context = ScriptContextFactory.method7409();

         try {
            this.script.scope = ScriptContextFactory.method7408(context);
            ScriptModuleApi.method325(this, this.script, this.script.scope);
         } finally {
            Context.exit();
         }

         this.script.type2 = Script.Type2.RUNNING;
         return this.script;
      }
   }

   public Map<String, List<String>> method1343() {
      if (this.items5 != null) {
         return this.items5;
      } else {
         LinkedHashMap map = new LinkedHashMap();

         try {
            Script script = this.method1342();
            ScriptableObject scope = script.scope;

            for (String text : ScriptModuleApi.texts) {
               Object property = ScriptableObject.getProperty(scope, text);
               map.put(text, method1344(property));
               if (property instanceof ScriptableObject scriptableObject) {
                  for (Object allId : scriptableObject.getAllIds()) {
                     Object obj = scriptableObject.get(String.valueOf(allId), scriptableObject);
                     if (obj instanceof ScriptableObject property2 && !(obj instanceof Function)) {
                        map.put(text + "." + allId, method1344(property2));
                     }
                  }
               }
            }
         } catch (Throwable e) {
            logger.warn("Не удалось собрать подсказки API", e);
         }

         this.items5 = map;
         return map;
      }
   }

   private static List<String> method1344(Object property) {
      ArrayList<String> strings = new ArrayList<>();
      if (property instanceof ScriptableObject scriptableObject) {
         for (Object allId : scriptableObject.getAllIds()) {
            strings.add(String.valueOf(allId));
         }
      }

      strings.sort(String.CASE_INSENSITIVE_ORDER);
      return strings;
   }

   public List<Path> method1345(Script workspace) {
      ArrayList<Path> paths = new ArrayList<>();
      if (workspace.getPath() == null) {
         paths.add(workspace.getPath2());
         return paths;
      } else {
         try (Stream<java.nio.file.Path> walk = Files.walk(workspace.getPath(), 4)) {
            walk.filter(path -> Files.isRegularFile(path))
               .filter(
                  path -> {
                     String var1x = path.getFileName().toString().toLowerCase(Locale.ROOT);
                     return !var1x.equals("storage.json")
                        && !var1x.endsWith(".tmp")
                        && !var1x.startsWith(".")
                        && (var1x.endsWith(".js") || var1x.endsWith(".json") || var1x.endsWith(".md") || var1x.endsWith(".txt"));
                  }
               )
               .sorted((java.nio.file.Path var1x, java.nio.file.Path var2x) -> var1x.equals(workspace.getPath2()) ? -1 : (var2x.equals(workspace.getPath2()) ? 1 : var1x.compareTo(var2x)))
               .limit(200L)
               .forEach(paths::add);
         } catch (UncheckedIOException | IOException e) {
         }

         return paths;
      }
   }

   public Path method1346(Script workspace, String text) throws IOException {
      if (workspace.getPath() == null) {
         Path path = workspace.getPath2().toAbsolutePath().normalize();
         if (!path.getFileName().toString().equals(Path.of(text).getFileName().toString())) {
            throw new IOException("у одиночного скрипта есть только " + path.getFileName());
         } else {
            return path;
         }
      } else {
         Path path = workspace.getPath().toAbsolutePath().normalize();
         Path path2 = path.resolve(text).toAbsolutePath().normalize();
         if (!path2.startsWith(path)) {
            throw new IOException("путь вне папки скрипта");
         } else {
            for (Path path3 : path.relativize(path2)) {
               if (path3.toString().startsWith(".")) {
                  throw new IOException("служебные файлы с точкой в начале имени трогать нельзя");
               }
            }

            return path2;
         }
      }
   }

   public void method1347(Script workspace, Path file, String text) throws IOException {
      Path path = (workspace.getPath() == null ? this.path : workspace.getPath()).toAbsolutePath().normalize();
      Path target = file.toAbsolutePath().normalize();
      if (!target.startsWith(path)) {
         throw new IOException("путь вне папки скрипта");
      } else {
         for (Path path2 : path.relativize(target)) {
            if (path2.toString().startsWith(".")) {
               throw new IOException("служебные файлы с точкой в начале имени трогать нельзя");
            }
         }

         if (ScriptStorageFile.method1005(text) > 1048576) {
            throw new IOException("файл больше 1 МБ");
         } else {
            Files.createDirectories(target.getParent());
            Path sibling = target.resolveSibling(target.getFileName() + ".tmp");
            Files.writeString(sibling, text, StandardCharsets.UTF_8);
            Files.move(sibling, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            if (target.getFileName().toString().equalsIgnoreCase("CHANGELOG.md")) {
               this.items7.clear();
            }
         }
      }
   }

   public static int method1348(Script workspace, Path file) {
      String error = workspace.getError();
      if (error != null && !error.isEmpty() && file != null) {
         Matcher matcher = pattern.matcher(error);
         int n = -1;

         while (matcher.find()) {
            if (file.getFileName().toString().equals(matcher.group(1))) {
               n = Integer.parseInt(matcher.group(2));
            }
         }

         return n;
      } else {
         return -1;
      }
   }

   public List<Storage.Data2> method1349(Script workspace) {
      ArrayList<Storage.Data2> data2s = new ArrayList<>();
      String anObject = workspace == null ? null : workspace.method1403();
      synchronized (this.deque) {
         for (Storage.Data2 data2 : this.deque) {
            if (anObject == null || data2.getText().equals(anObject)) {
               data2s.add(data2);
            }
         }

         return data2s;
      }
   }

   public void method1350(Script script, Map<String, String> map) throws IOException {
      String str = this.method1306(script.getPath2());
      String[] strings = str.split("\n", -1);
      LinkedHashMap map2 = new LinkedHashMap(map);
      ArrayList elements = new ArrayList();
      int size = 0;

      for (int i = 0; i < strings.length; i++) {
         String str2 = strings[i];
         String str3 = str2.trim();
         if (i < 30 && str3.startsWith("//")) {
            String str4 = str3.substring(2).trim();
            if (str4.startsWith("@")) {
               int indexOf = str4.indexOf(32);
               String lowerCase = (indexOf < 0 ? str4.substring(1) : str4.substring(1, indexOf)).toLowerCase(Locale.ROOT);
               if (map2.containsKey(lowerCase)) {
                  String clean2 = clean((String)map2.remove(lowerCase));
                  if (!clean2.isEmpty()) {
                     elements.add("// @" + lowerCase + " " + clean2);
                  }

                  size = elements.size();
               } else {
                  elements.add(str2);
                  size = elements.size();
               }
               continue;
            }
         }

         elements.add(str2);
      }

      ArrayList arrayList = new ArrayList();

      for (Object obj : (Iterable<?>)(Object)(map2.entrySet())) {
         Map.Entry entry = (Map.Entry)obj;
         String clean2 = clean((String)entry.getValue());
         if (!clean2.isEmpty()) {
            arrayList.add("// @" + (String)entry.getKey() + " " + clean2);
         }
      }

      elements.addAll(size, arrayList);
      this.method1347(script, script.getPath2(), String.join("\n", elements));
      this.method1307(script, this.method1306(script.getPath2()));
      script.longValue9 = this.method1304(script);
   }

   private static String clean(String str) {
      if (str == null) {
         return "";
      } else {
         String str2 = str.replace("\r", " ").replace("\n", " ").trim();
         return str2.length() > 120 ? str2.substring(0, 120) : str2;
      }
   }

   public String method1351(Script script, int n, String str) throws IOException {
      String version = script.getVersion().isBlank() ? "1.0.0" : script.getVersion();
      String[] strings = version.split("\\.");
      int[] ints = new int[3];

      for (int i = 0; i < 3; i++) {
         try {
            ints[i] = i < strings.length ? Integer.parseInt(strings[i].replaceAll("[^0-9]", "")) : 0;
         } catch (NumberFormatException e) {
            ints[i] = 0;
         }
      }

      ints[n]++;

      for (int i = n + 1; i < 3; i++) {
         ints[i] = 0;
      }

      String str2 = ints[0] + "." + ints[1] + "." + ints[2];
      this.method1350(script, Map.of("version", str2));
      if (script.getPath() != null) {
         Path path = script.getPath().resolve("CHANGELOG.md");
         String str3 = Files.isRegularFile(path) ? this.method1306(path) : "# " + script.method1403() + "\n";
         String str4 = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
         String str5 = "\n## v" + str2 + " — " + str4 + "\n" + (str != null && !str.isBlank() ? "- " + clean(str) : "- обновление") + "\n";
         int indexOf = str3.indexOf("\n## ");
         String text = indexOf < 0 ? str3 + str5 : str3.substring(0, indexOf) + str5 + str3.substring(indexOf);
         this.method1347(script, path, text);
      }

      this.method1299(script);
      return str2;
   }

   public List<String> method1352(Script script, int changesLimit) {
      ArrayList<String> strings = new ArrayList<>();
      if (script.getPath() == null) {
         return strings;
      } else {
         Path path = script.getPath().resolve("CHANGELOG.md");
         Storage.Data data2 = this.items7.get(path);

         Storage.Data data3;
         try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            if (!attributes.isRegularFile()) {
               data3 = data;
            } else {
               long millis = attributes.lastModifiedTime().toMillis();
               if (data2 != null && data2.modified() == millis && data2.size() == attributes.size()) {
                  data3 = data2;
               } else {
                  ArrayList coll = new ArrayList();

                  for (String str : this.method1306(path).split("\n")) {
                     String str2 = str.trim();
                     if (str2.startsWith("## ") || str2.startsWith("- ")) {
                        coll.add(str2);
                     }
                  }

                  data3 = new Storage.Data(millis, attributes.size(), List.copyOf(coll));
               }
            }
         } catch (IOException e) {
            data3 = data;
         }

         if (data3 != data2) {
            this.items7.put(path, data3);
         }

         for (String line : data3.lines()) {
            strings.add(line);
            if (strings.size() >= changesLimit) {
               break;
            }
         }

         return strings;
      }
   }

   public void method1353(Script script, Path path) throws IOException {
      if (script.getPath() == null) {
         throw new IOException("у одиночного .js нет папки для аватарки");
      } else if (Files.size(path) > 4194304L) {
         throw new IOException("картинка больше 4 МБ");
      } else {
         byte[] allBytes = Files.readAllBytes(path);
         int[] ints = ImageLoader.method231(allBytes);
         if (ints[0] <= 2048 && ints[1] <= 2048) {
            try (NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(allBytes))) {
               if (nativeImage.getWidth() > 2048 || nativeImage.getHeight() > 2048) {
                  throw new IOException("картинка больше 2048×2048");
               }
            } catch (IOException e) {
               throw e;
            } catch (Exception e) {
               throw new IOException("нужен PNG");
            }

            Files.write(script.method1412(), allBytes);
         } else {
            throw new IOException("картинка больше 2048×2048");
         }
      }
   }

   public void method1354(Script script) throws IOException {
      if (script.method1412() != null) {
         Files.deleteIfExists(script.method1412());
      }
   }

   public Script method1355(Script script) throws IOException {
      if (script.getPath() == null) {
         throw new IOException("одиночный .js дублируется копированием файла");
      } else {
         String str = script.getId() + "_copy";
         String argument = str;

         for (int i = 2; this.method1294(argument) != null || Files.exists(this.path.resolve(argument)); i++) {
            argument = str + i;
         }

         Path path = this.path.resolve(argument);

         try (Stream<java.nio.file.Path> walk = Files.walk(script.getPath())) {
            for (Path path2 : (Iterable<Path>)walk::iterator) {
               String string = path2.getFileName().toString();
               if (!string.equals("storage.json") && !string.endsWith(".tmp") && !string.equals(".origin.json")) {
                  Path dir = path.resolve(script.getPath().relativize(path2).toString());
                  if (Files.isDirectory(path2)) {
                     Files.createDirectories(dir);
                  } else {
                     Files.copy(path2, dir);
                  }
               }
            }
         }

         this.method1301(false);
         Script script2 = this.method1294(argument);
         if (script2 != null) {
            this.method1350(script2, Map.of("name", script.method1403() + " (копия)"));
         }

         return script2;
      }
   }

   public Script method1356(Script script, String string) throws IOException {
      String argument = method1303(string);
      if (script.getPath() == null) {
         throw new IOException("переименовать можно только скрипт-папку");
      } else if (!argument.isBlank() && !argument.equals(script.getId())) {
         if (this.method1294(argument) == null && !Files.exists(this.path.resolve(argument))) {
            boolean flag = this.method1297(script);
            this.method1319(script, true);
            script.type2 = Script.Type2.STOPPED;

            try {
               Files.move(script.getPath(), this.path.resolve(argument));
            } catch (IOException e) {
               if (flag) {
                  this.method1308(script, null);
               }

               this.flag2 = true;
               throw e;
            }

            JsonElement jsonElement = this.jsonObject.remove(script.getId());
            this.items.remove(script.getId());
            if (jsonElement != null) {
               this.jsonObject.add(argument, jsonElement);
            }

            this.flag = true;
            this.method1301(false);
            Script workspace = this.method1294(argument);
            if (workspace != null && flag && !workspace.isRunning()) {
               this.method1308(workspace, null);
            }

            this.flag2 = true;
            return workspace;
         } else {
            throw new IOException("папка " + argument + " уже есть");
         }
      } else {
         return script;
      }
   }

   public void method1357(Script script) throws IOException {
      this.method1319(script, true);
      script.type2 = Script.Type2.STOPPED;
      Path dir = this.path.resolve(".trash");
      Files.createDirectories(dir);
      String str = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
      Path path2 = script.getPath() == null ? script.getPath2() : script.getPath();
      Files.move(path2, dir.resolve(script.getId() + "-" + str + (script.getPath() == null ? ".js" : "")));
      this.jsonObject.remove(script.getId());
      this.flag = true;
      this.items.remove(script.getId());
      this.flag2 = true;
   }

   public void method1358(Script script) throws IOException {
      Path path = this.method1311(script);
      script.scriptStorageFile = null;
      Files.deleteIfExists(path);
      this.method1299(script);
   }

   public void method1359(Script script) {
      JsonObject json = this.method1337(script.getId());
      boolean running = script.isRunning();
      if (running) {
         this.method1319(script, true);
         script.type2 = Script.Type2.STOPPED;
      }

      json.remove("modules");
      json.remove("settings");
      this.flag = true;
      if (running) {
         this.method1308(script, null);
      }
   }

   public Path method1360(Script script) throws IOException {
      Path dir = this.path.resolve(".exports");
      Files.createDirectories(dir);
      String str = script.getVersion().isBlank() ? "" : "-v" + script.getVersion();
      Path path = dir.resolve(script.getId() + str + ".zjs");
      Files.write(path, OriginUtil.method6919(script));
      return path;
   }

   public void shutdown() {
      Server.getInstance().stop();

      for (Script workspace : this.items.values()) {
         if (workspace.isRunning()) {
            this.method1315(workspace);
         }

         if (workspace.scriptStorageFile != null) {
            workspace.scriptStorageFile.flush();
         }
      }

      this.flag = true;
      this.flush();
   }

   private static final class Data {
      private final long longValue;
      private final long longValue2;
      private final List<String> items;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, items);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "items=" + this.items + "]";
      }

      private Data(long longValue, long size, List<String> items) {
         this.longValue = longValue;
         this.longValue2 = size;
         this.items = items;
      }

      public long modified() {
         return this.longValue;
      }

      public long size() {
         return this.longValue2;
      }

      public List<String> lines() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;

      public long longValue() {
         return this.longValue;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      public Data2(long longValue, String text, String anObject, String error) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = anObject;
         this.text3 = error;
      }

      public long time() {
         return this.longValue;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String text() {
         return this.text3;
      }
   }

   public static final class Entry {
      public final Function function;
      public final long longValue;
      public long longValue2;
      public final boolean flag;
      public final int intValue;
      public boolean cancelled;

      public Entry(int intValue, Function function, long l, boolean flag) {
         this.intValue = intValue;
         this.function = function;
         this.longValue = Math.max(0L, l);
         this.longValue2 = System.currentTimeMillis() + this.longValue;
         this.flag = flag;
      }
   }
}
