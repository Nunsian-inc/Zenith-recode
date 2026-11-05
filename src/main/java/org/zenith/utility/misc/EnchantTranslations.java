package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnchantTranslations {
   public static final Map<String, String> items = new HashMap<>();

   public static List<EnchantMatcher> method4710(String str) {
      ArrayList<EnchantMatcher> enchantMatchers = new ArrayList<>();
      String input = str.trim();
      if (input.startsWith("[") && input.endsWith("]")) {
         input = input.substring(1, input.length() - 1);
      }

      Pattern compile = Pattern.compile("(EnchantCustom|EnchantVanilla) \\[checked=([^,]+), level=(\\d+)]");
      Matcher matcher = compile.matcher(input);

      while (matcher.find()) {
         String group = matcher.group(1);
         String key = matcher.group(2);
         int intOr = Integer.parseInt(matcher.group(3));
         String stringOr = items.getOrDefault(key, key);
         EnchantMatcher customEnchantMatcher;
         if (group.equals("EnchantCustom")) {
            customEnchantMatcher = new CustomEnchantMatcher(stringOr, key, intOr);
         } else {
            customEnchantMatcher = new VanillaEnchantMatcher(stringOr, key, intOr);
         }

         enchantMatchers.add(customEnchantMatcher);
      }

      return enchantMatchers;
   }

   static {
      items.put("oxidation", "Окисление");
      items.put("detection", "Детекция");
      items.put("poison", "Яд");
      items.put("vampirism", "Вампиризм");
      items.put("skilled", "Опытный");
      items.put("smelting", "Автоплавка");
      items.put("magnet", "Магнит");
      items.put("pinger", "Пингер");
      items.put("web", "Паутина");
      items.put("buldozing", "Бульдозер");
      items.put("pulling", "Притягивание");
      items.put("stupor", "Ступор");
      items.put("demolishing", "Разрушение");
      items.put("returning", "Возврат");
      items.put("scout", "Скаут");
      items.put("minecraft:protection", "Защита");
      items.put("minecraft:fire_protection", "Огнеупорность");
      items.put("minecraft:feather_falling", "Невесомость (Падение)");
      items.put("minecraft:blast_protection", "Взрывоустойчивость");
      items.put("minecraft:projectile_protection", "Защита от снарядов");
      items.put("minecraft:thorns", "Шипы");
      items.put("minecraft:soul_speed", "Скорость души");
      items.put("minecraft:respiration", "Подводное дыхание");
      items.put("minecraft:depth_strider", "Подводник (Подводная ходьба)");
      items.put("minecraft:aqua_affinity", "Подводник (Ускорение добычи под водой)");
      items.put("minecraft:frost_walker", "Ледоход");
      items.put("minecraft:sharpness", "Острота");
      items.put("minecraft:smite", "Небесная кара");
      items.put("minecraft:bane_of_arthropods", "Бич членистоногих");
      items.put("minecraft:knockback", "Отбрасывание");
      items.put("minecraft:fire_aspect", "Заговор огня");
      items.put("minecraft:looting", "Добыча");
      items.put("minecraft:sweeping_edge", "Разящий клинок");
      items.put("minecraft:efficiency", "Эффективность");
      items.put("minecraft:silk_touch", "Шёлковое касание");
      items.put("minecraft:unbreaking", "Прочность");
      items.put("minecraft:fortune", "Удача");
      items.put("minecraft:mending", "Починка");
      items.put("minecraft:impaling", "Пронзатель");
      items.put("minecraft:power", "Сила");
      items.put("minecraft:punch", "Откидывание (Удар стрелой)");
      items.put("minecraft:flame", "Воспламенение");
      items.put("minecraft:infinity", "Бесконечность");
      items.put("minecraft:piercing", "Точность");
      items.put("minecraft:multishot", "Многострельность");
      items.put("minecraft:quick_charge", "Быстрая перезарядка");
      items.put("minecraft:riptide", "Замедление");
      items.put("minecraft:loyalty", "Верность");
      items.put("minecraft:channeling", "Громовержец");
      items.put("minecraft:luck_of_the_sea", "Везучий рыбак");
      items.put("minecraft:lure", "Приманка");
      items.put("minecraft:binding_curse", "Проклятие несъёмности");
      items.put("minecraft:vanishing_curse", "Проклятие утраты");
   }
}
