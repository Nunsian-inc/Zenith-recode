package org.zenith.utility.misc;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.ScriptValueUtils;

public final class ScriptPermissions {
   public static final String text = "chat";
   public static final String text2 = "ai";
   public static final String text3 = "screenshot";
   public static final String text4 = "modules";
   public static final String text5 = "friends";
   public static final String text6 = "configs";
   public static final String text7 = "clipboard";
   public static final String text8 = "microphone";
   public static final String text9 = "tts";
   public static final String text10 = "net";
   public static final Map<String, String> items = new LinkedHashMap<>();

   private ScriptPermissions() {
   }

   public static Set<String> method(Script workspace) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (String str : Script.pattern.split(workspace.text6)) {
         String lowerCase = str.strip().toLowerCase(Locale.ROOT);
         if (items.containsKey(lowerCase)) {
            linkedHashSet.add(lowerCase);
         }
      }

      if (workspace.method1406()) {
         linkedHashSet.add("net");
      }

      return linkedHashSet;
   }

   public static boolean method2(Script workspace) {
      return workspace.flag;
   }

   public static boolean method3(Script workspace, String key) {
      return !method2(workspace) || method(workspace).contains(key);
   }

   public static void method4(Script workspace, String key) {
      if (!method3(workspace, key)) {
         throw ScriptValueUtils.method6251(
            "скрипту из маркета нужно разрешение «" + items.getOrDefault(key, key) + "»: автор должен добавить в шапку // @permissions " + key
         );
      }
   }

   public static String method5(Script script) {
      var elements = method(script).stream().map(items::get).toList();
      return String.join(", ", elements);
   }

   static {
      items.put("net", "сеть");
      items.put("chat", "чат и команды");
      items.put("ai", "нейросеть игрока");
      items.put("screenshot", "скриншоты");
      items.put("modules", "модули клиента");
      items.put("friends", "друзья");
      items.put("configs", "конфиги");
      items.put("clipboard", "буфер обмена");
      items.put("microphone", "микрофон");
      items.put("tts", "озвучка текста");
   }
}
