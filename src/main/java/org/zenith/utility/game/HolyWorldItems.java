package org.zenith.utility.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.zenith.utility.misc.EnchantTranslations;

public class HolyWorldItems {
   private final ArrayList<ItemMatcher> arrayList = new ArrayList<>();
   private final ArrayList<ItemMatcher> arrayList2 = new ArrayList<>();
   private final ArrayList<ItemMatcher> arrayList3 = new ArrayList<>();
   private final ArrayList<ItemMatcher> arrayList4 = new ArrayList<>();

   public HolyWorldItems() {
      this.init();
   }

   private void init() {
      var enchantMatchers = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:respiration, level=3], EnchantVanilla [checked=minecraft:mending, level=1], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:aqua_affinity, level=1]]"
      );
      EnchantedItemMatcher enchantedItemMatcher = new EnchantedItemMatcher(Items.NETHERITE_HELMET.getDefaultInstance(), "Шлем крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers.forEach(enchantedItemMatcher::method4844);
      var enchantMatchers2 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher enchantedItemMatcher2 = new EnchantedItemMatcher(Items.NETHERITE_CHESTPLATE.getDefaultInstance(), "Нагрудник Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers2.forEach(enchantedItemMatcher2::method4844);
      EnchantedItemMatcher enchantedItemMatcher3 = new EnchantedItemMatcher(Items.NETHERITE_LEGGINGS.getDefaultInstance(), "Поножи Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers2.forEach(enchantedItemMatcher3::method4844);
      var enchantMatchers3 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:soul_speed, level=3], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:depth_strider, level=3], EnchantVanilla [checked=minecraft:feather_falling, level=4], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher enchantedItemMatcher4 = new EnchantedItemMatcher(Items.NETHERITE_BOOTS.getDefaultInstance(), "Ботинки Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers3.forEach(enchantedItemMatcher4::method4844);
      var enchantMatchers4 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=oxidation, level=2], EnchantCustom [checked=detection, level=3], EnchantCustom [checked=poison, level=3], EnchantCustom [checked=vampirism, level=2], EnchantCustom [checked=skilled, level=3], EnchantVanilla [checked=minecraft:looting, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:fire_aspect, level=2], EnchantVanilla [checked=minecraft:sweeping_edge, level=3], EnchantVanilla [checked=minecraft:smite, level=7], EnchantVanilla [checked=minecraft:sharpness, level=7], EnchantVanilla [checked=minecraft:bane_of_arthropods, level=7], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher enchantedItemMatcher5 = new EnchantedItemMatcher(Items.NETHERITE_SWORD.getDefaultInstance(), "Меч Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers4.forEach(enchantedItemMatcher5::method4844);
      var enchantMatchers5 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=skilled, level=3], EnchantCustom [checked=smelting, level=1], EnchantCustom [checked=magnet, level=1], EnchantCustom [checked=pinger, level=1], EnchantCustom [checked=web, level=1], EnchantCustom [checked=buldozing, level=2], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:efficiency, level=10], EnchantVanilla [checked=minecraft:mending, level=1], EnchantVanilla [checked=minecraft:fortune, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher6 = new EnchantedItemMatcher(Items.NETHERITE_PICKAXE.getDefaultInstance(), "Кирка Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers5.forEach(enchantedItemMatcher6::method4844);
      var enchantMatchers6 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=3], EnchantVanilla [checked=minecraft:mending, level=1], EnchantVanilla [checked=minecraft:multishot, level=1], EnchantVanilla [checked=minecraft:piercing, level=5], EnchantVanilla [checked=minecraft:quick_charge, level=3]]"
      );
      EnchantedItemMatcher enchantedItemMatcher7 = new EnchantedItemMatcher(Items.CROSSBOW.getDefaultInstance(), "Арбалет Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers6.forEach(enchantedItemMatcher7::method4844);
      var enchantMatchers7 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=detection, level=3], EnchantCustom [checked=poison, level=3], EnchantCustom [checked=demolishing, level=1], EnchantCustom [checked=returning, level=1], EnchantCustom [checked=oxidation, level=2], EnchantCustom [checked=pulling, level=2], EnchantCustom [checked=stupor, level=3], EnchantCustom [checked=vampirism, level=2], EnchantCustom [checked=skilled, level=3], EnchantCustom [checked=scout, level=3], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:fire_aspect, level=2], EnchantVanilla [checked=minecraft:loyalty, level=3], EnchantVanilla [checked=minecraft:impaling, level=5], EnchantVanilla [checked=minecraft:channeling, level=1], EnchantVanilla [checked=minecraft:sharpness, level=7], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher enchantedItemMatcher8 = new EnchantedItemMatcher(Items.TRIDENT.getDefaultInstance(), "Трезубец Крушителя", ItemMatcher.Type.FUNTIME);
      enchantMatchers7.forEach(enchantedItemMatcher8::method4844);
      ItemStack defaultInstance = Items.SPLASH_POTION.getDefaultInstance();
      defaultInstance.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(214, 0, 191).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher = new PotionItemMatcher(
         defaultInstance,
         "Зелье Медика",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:2b,duration:900,id:\"minecraft:health_boost\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:900,id:\"minecraft:regeneration\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack stack = Items.SPLASH_POTION.getDefaultInstance();
      stack.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(11, 188, 4).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher2 = new PotionItemMatcher(
         stack,
         "Зелье Победителя",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:1b,duration:3600,id:\"minecraft:health_boost\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:18000,id:\"minecraft:invisibility\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:1b,duration:1200,id:\"minecraft:regeneration\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:1200,id:\"minecraft:resistance\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack defaultInstance2 = Items.SPLASH_POTION.getDefaultInstance();
      defaultInstance2.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(246, 250, 78).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher3 = new PotionItemMatcher(
         defaultInstance2,
         "Зелье Агента",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:0b,duration:18000,id:\"minecraft:fire_resistance\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:3600,id:\"minecraft:haste\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:18000,id:\"minecraft:invisibility\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:18000,id:\"minecraft:speed\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:6000,id:\"minecraft:strength\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack defaultInstance3 = Items.SPLASH_POTION.getDefaultInstance();
      defaultInstance3.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(135, 0, 0).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher4 = new PotionItemMatcher(
         defaultInstance3,
         "Зелье Киллера",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:0b,duration:3600,id:\"minecraft:resistance\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:3b,duration:1800,id:\"minecraft:strength\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack defaultInstance4 = Items.SPLASH_POTION.getDefaultInstance();
      defaultInstance4.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(164, 252, 76).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher5 = new PotionItemMatcher(
         defaultInstance4,
         "Серная кислота",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:1b,duration:1000,id:\"minecraft:poison\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:3b,duration:1800,id:\"minecraft:slowness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:1800,id:\"minecraft:weakness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:4b,duration:600,id:\"minecraft:wither\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack defaultInstance5 = Items.SPLASH_POTION.getDefaultInstance();
      defaultInstance5.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(243, 59, 128).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher6 = new PotionItemMatcher(
         defaultInstance5,
         "Исцел",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:1b,amplifier:1b,duration:1,id:\"minecraft:instant_health\",show_icon:1b,show_particles:1b},{ambient:1b,amplifier:0b,duration:600,id:\"minecraft:regeneration\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack defaultInstance6 = Items.SPLASH_POTION.getDefaultInstance();
      defaultInstance6.set(
         DataComponents.POTION_CONTENTS,
         new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(16737792).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher7 = new PotionItemMatcher(defaultInstance6, "Отрыжка", ItemMatcher.Type.FUNTIME, "CustomPotionColor", "16737792");
      ItemStack defaultInstance7 = Items.TIPPED_ARROW.getDefaultInstance();
      defaultInstance7.set(
         DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(65535), List.of(), Optional.empty())
      );
      PotionItemMatcher potionItemMatcher8 = new PotionItemMatcher(
         defaultInstance7,
         "Ледяная стрела",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:50b,duration:100,id:\"minecraft:slowness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:120,id:\"minecraft:weakness\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack defaultInstance8 = Items.TIPPED_ARROW.getDefaultInstance();
      defaultInstance8.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.SWIFTNESS), Optional.of(0), List.of(), Optional.empty()));
      PotionItemMatcher potionItemMatcher9 = new PotionItemMatcher(
         defaultInstance8,
         "Проклятая стрела",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:0b,duration:40,id:\"minecraft:blindness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:100,id:\"minecraft:nausea\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:200,id:\"minecraft:weakness\",show_icon:1b,show_particles:1b}]"
      );
      PotionItemMatcher potionItemMatcher10 = new PotionItemMatcher(Items.DRIED_KELP.getDefaultInstance(), "Пласт", ItemMatcher.Type.FUNTIME, "stratum", "1b");
      PotionItemMatcher potionItemMatcher11 = new PotionItemMatcher(Items.NETHERITE_SCRAP.getDefaultInstance(), "Трапка", ItemMatcher.Type.FUNTIME, "trap", "1b");
      PotionItemMatcher potionItemMatcher12 = new PotionItemMatcher(Items.ENDER_EYE.getDefaultInstance(), "Дезориентация", ItemMatcher.Type.FUNTIME, "desorientation", "1b");
      PotionItemMatcher potionItemMatcher13 = new PotionItemMatcher(Items.SUGAR.getDefaultInstance(), "Явная пыль", ItemMatcher.Type.FUNTIME, "sheerdust", "1b");
      PotionItemMatcher potionItemMatcher14 = new PotionItemMatcher(Items.PHANTOM_MEMBRANE.getDefaultInstance(), "Божья аура", ItemMatcher.Type.FUNTIME, "godsaura", "1b");
      SkullItemMatcher skullItemMatcher = new SkullItemMatcher(
         "Сфера Андромеды",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NTEwODQzNywKICAicHJvZmlsZUlkIiA6ICIzMjNiYjlkYzkwZWU0Nzk5YjUxYzE3NjRmZDRhNjI3OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOcGllIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ0ZmZlM2YzNThmMjA5YmFkOGZmZjRkYzQ4MjQ1ZDliYWYwYTAzMWIzYzFlZTZiNzU4NDYwYTMzOWIxNTE5ZTIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      );
      SkullItemMatcher skullItemMatcher2 = new SkullItemMatcher(
         "Сфера Пандоры",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NTY2NTExNCwKICAicHJvZmlsZUlkIiA6ICJkNzJlNGJjZDIyZGI0NjQ4OTUxNTc0M2UyYTRmMWFjMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJhdnZheSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZTUxZTY1ZWI0MDUyNzcyMzgyYzllNTA3YTU0YmRlZDQzZTM5Zjc1NWI1ZGRmNTViM2YzOTQ0M2NlZDQ2N2Y0IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
      );
      SkullItemMatcher skullItemMatcher3 = new SkullItemMatcher(
         "Сфера Титана",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTc1MDM1NDQ1NTE5MiwKICAicHJvZmlsZUlkIiA6ICJkOTcwYzEzZTM4YWI0NzlhOTY1OGM1ZDQ1MjZkMTM0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcmltcHlMYWNlODUxMjciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFlOTY5ODQ1OGI3ODQxYzk2YWU0ZjI0ZWM4NGFlMDE3MjQxMDA2NDFjNTY0ZTJhN2IxODVmNDA2ZThlZDIzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
      );
      SkullItemMatcher skullItemMatcher4 = new SkullItemMatcher(
         "Сфера Аполлона",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjYyNTM0NywKICAicHJvZmlsZUlkIiA6ICJhMjk1ODZmYmU1ZDk0Nzk2OWZjOGQ4ZGE0NzlhNDNlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMZXZlMjQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQxMTdiNjAxOGZlZjBkNTE1NjcyMTczZTNiMjZlNjYwZDY1MWU1ODc2YmE2ZDAzZTUzNDIyNzBjNDliZWM4MCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
      );
      SkullItemMatcher skullItemMatcher5 = new SkullItemMatcher(
         "Сфера Астрея",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NTA2MjQwNywKICAicHJvZmlsZUlkIiA6ICJlMzcxMWU2Y2E0ZmY0NzA4YjY5ZjhiNGZlYzNhZjdhMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNckJ1cnN0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFhNWFhZGQ1MmE1ZmFiOTcwODgxNDUxYWRmNTZmYmI0OTNhMzU4NTZlYTk2ZjU0ZTMyZWVhNjYyZDc4N2VkMjAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      );
      SkullItemMatcher skullItemMatcher6 = new SkullItemMatcher(
         "Сфера Осириса",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjY2Mzg3NiwKICAicHJvZmlsZUlkIiA6ICI3NGEwMzQxNWY1OTI0ZTA4YjMyMGM2MmU1NGE3ZjJhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZXp6aXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgxMzYzNWJkODZiMTcxYmJlMTQzYWQ3MWUwOTAyMjkyNjQ5Y2IzYWI4NDQwZWQwMGY4NWNhNmNhMzgyOTkzNiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
      );
      SkullItemMatcher skullItemMatcher7 = new SkullItemMatcher(
         "Сфера Химеры",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjE4MTEwOSwKICAicHJvZmlsZUlkIiA6ICJiNzRiMGQzNTBkNTk0NTU4YmYyYjBlMDJlYmE4NjE4NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcmFuZG9uYnBtMjg0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlmYWJlZWQ0MjRiMjUyYTg5NDVhNjQ0MmI0NjJkNWYzMTQ3MDFhODE2ZGEyZDBhNjljY2RmY2ZkNzQ2ZTU4OGUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      );
      PotionItemMatcher potionItemMatcher15 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Грани",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;-1243469000,-221950864,-1992446485,-2021417908]},{Amount:0.15d,AttributeName:\"minecraft:generic.movement_speed\",Name:\"скорость\",Operation:1,Slot:\"offhand\",UUID:[I;421185474,-1675802507,-1302866561,493632362]},{Amount:3.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;1519226946,1096501307,-1211766788,1772678932]}]"
      );
      PotionItemMatcher potionItemMatcher16 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Дедала",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;359257458,1778207792,-1338766924,848576712]},{Amount:5.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;-1723975978,-172144109,-1549361344,1422474545]}]"
      );
      PotionItemMatcher potionItemMatcher17 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Тритона",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:2.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;1523385351,-602324415,-1760945561,-270836422]},{Amount:-2.0d,AttributeName:\"minecraft:generic.armor_toughness\",Name:\"твёрдость брони\",Operation:0,Slot:\"offhand\",UUID:[I;1017626540,670256083,-1935748919,-786940866]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;1409389356,857884230,-1271577328,-932163262]}]"
      );
      PotionItemMatcher potionItemMatcher18 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Гармонии",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:2.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;1253805553,182601342,-1805352146,-1397322752]},{Amount:2.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;1648014473,711805426,-1962672765,-537567144]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;-432853915,2131774585,-1617568671,-621826558]}]"
      );
      PotionItemMatcher potionItemMatcher19 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Феникса",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:6.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;587791282,-2058138192,-2013554380,1529824133]},{Amount:0.1d,AttributeName:\"minecraft:generic.attack_speed\",Name:\"скорость атаки\",Operation:1,Slot:\"offhand\",UUID:[I;233538602,1321813146,-1704635325,1580943681]}]"
      );
      PotionItemMatcher potionItemMatcher20 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Ехидны",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;951469257,944390782,-1639200535,1882164017]},{Amount:6.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;955551735,1646086503,-2027411701,279678977]},{Amount:-2.0d,AttributeName:\"minecraft:generic.armor_toughness\",Name:\"твёрдость брони\",Operation:0,Slot:\"offhand\",UUID:[I;1301352305,-411349527,-1975888927,1331290909]},{Amount:-2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;-1591608852,-1821816734,-1777269705,-1095126461]}]"
      );
      PotionItemMatcher potionItemMatcher21 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Крушителя",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;-2029680951,-1707392264,-1958707995,-389772071]},{Amount:3.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;430632003,-1617212859,-1216331408,-1833541566]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor_toughness\",Name:\"твёрдость брони\",Operation:0,Slot:\"offhand\",UUID:[I;-1348659832,-1401469259,-1365362331,376666792]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;897201681,1928088706,-1491637188,-648449433]}]"
      );
      PotionItemMatcher potionItemMatcher22 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Карателя",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:7.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;-1651993925,-1873589036,-2067416271,-818067434]},{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;-473822740,-120697814,-1795206788,-16471115]},{Amount:0.1d,AttributeName:\"minecraft:generic.movement_speed\",Name:\"скорость\",Operation:1,Slot:\"offhand\",UUID:[I;1026697411,-1939651121,-2079096398,-1055446047]}]"
      );
      this.arrayList2.add(enchantedItemMatcher);
      this.arrayList2.add(enchantedItemMatcher2);
      this.arrayList2.add(enchantedItemMatcher3);
      this.arrayList2.add(enchantedItemMatcher4);
      this.arrayList2.add(enchantedItemMatcher5);
      this.arrayList2.add(enchantedItemMatcher6);
      this.arrayList2.add(enchantedItemMatcher7);
      this.arrayList2.add(enchantedItemMatcher8);
      this.arrayList2.add(potionItemMatcher);
      this.arrayList2.add(potionItemMatcher2);
      this.arrayList2.add(potionItemMatcher3);
      this.arrayList2.add(potionItemMatcher4);
      this.arrayList2.add(potionItemMatcher5);
      this.arrayList2.add(potionItemMatcher6);
      this.arrayList2.add(potionItemMatcher8);
      this.arrayList2.add(potionItemMatcher9);
      this.arrayList2.add(potionItemMatcher7);
      this.arrayList2.add(potionItemMatcher10);
      this.arrayList2.add(potionItemMatcher11);
      this.arrayList2.add(potionItemMatcher12);
      this.arrayList2.add(potionItemMatcher13);
      this.arrayList2.add(potionItemMatcher14);
      this.arrayList2.add(skullItemMatcher);
      this.arrayList2.add(skullItemMatcher2);
      this.arrayList2.add(skullItemMatcher3);
      this.arrayList2.add(skullItemMatcher4);
      this.arrayList2.add(skullItemMatcher5);
      this.arrayList2.add(skullItemMatcher6);
      this.arrayList2.add(skullItemMatcher7);
      this.arrayList2.add(potionItemMatcher15);
      this.arrayList2.add(potionItemMatcher16);
      this.arrayList2.add(potionItemMatcher17);
      this.arrayList2.add(potionItemMatcher18);
      this.arrayList2.add(potionItemMatcher19);
      this.arrayList2.add(potionItemMatcher20);
      this.arrayList2.add(potionItemMatcher21);
      this.arrayList2.add(potionItemMatcher22);
      this.arrayList.add(new ItemMatcher(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), "Зачарованное золотое яблоко", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.GOLDEN_APPLE.getDefaultInstance(), "Золотое яблоко", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.FIREWORK_ROCKET.getDefaultInstance(), "Фейерверк", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.GOLDEN_CARROT.getDefaultInstance(), "Золотая морковь", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.ENDER_PEARL.getDefaultInstance(), "Эндер жемчуг", "Эндер-жемчуг", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.CHORUS_FRUIT.getDefaultInstance(), "Плод хоруса", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.TOTEM_OF_UNDYING.getDefaultInstance(), "Тотем бессмертия", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.EXPERIENCE_BOTTLE.getDefaultInstance(), "Пузырёк опыта", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.ELYTRA.getDefaultInstance(), "Элитры", ItemMatcher.Type.ANY));
      this.arrayList.add(new PotionTypeMatcher(Items.POTION, Potions.TURTLE_MASTER, "Зелье черепашьей мощи", ItemMatcher.Type.HOLLYWORLD));
      this.arrayList.add(new ItemMatcher(Items.CONDUIT.getDefaultInstance(), "Артефакт", ItemMatcher.Type.HOLLYWORLD));
      enchantMatchers = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:aqua_affinity, level=1], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:respiration, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:aqua_affinity, level=1], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:respiration, level=3], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      enchantedItemMatcher = new EnchantedItemMatcher(Items.NETHERITE_HELMET.getDefaultInstance(), "Шлем infinity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers.forEach(enchantedItemMatcher::method4844);
      enchantMatchers2 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5]]"
      );
      enchantedItemMatcher2 = new EnchantedItemMatcher(Items.NETHERITE_CHESTPLATE.getDefaultInstance(), "Нагрудник infinity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers2.forEach(enchantedItemMatcher2::method4844);
      var enchantMatchers8 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher9 = new EnchantedItemMatcher(Items.NETHERITE_LEGGINGS.getDefaultInstance(), "Штаны infinity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers8.forEach(enchantedItemMatcher9::method4844);
      var enchantMatchers9 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:depth_strider, level=3], EnchantCustom [checked=minecraft:feather_falling, level=4], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:soul_speed, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:depth_strider, level=3], EnchantVanilla [checked=minecraft:feather_falling, level=4], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:soul_speed, level=3], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher10 = new EnchantedItemMatcher(Items.NETHERITE_BOOTS.getDefaultInstance(), "Ботинки infinity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers9.forEach(enchantedItemMatcher10::method4844);
      var enchantMatchers10 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=1], EnchantCustom [checked=minecraft:aqua_affinity, level=1], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:respiration, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:aqua_affinity, level=1], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:respiration, level=3], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher11 = new EnchantedItemMatcher(Items.NETHERITE_HELMET.getDefaultInstance(), "Шлем eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers10.forEach(enchantedItemMatcher11::method4844);
      var enchantMatchers11 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=1], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher12 = new EnchantedItemMatcher(Items.NETHERITE_CHESTPLATE.getDefaultInstance(), "Нагрудник eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers11.forEach(enchantedItemMatcher12::method4844);
      enchantedItemMatcher7 = new EnchantedItemMatcher(Items.NETHERITE_LEGGINGS.getDefaultInstance(), "Штаны eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers11.forEach(enchantedItemMatcher7::method4844);
      enchantMatchers7 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:depth_strider, level=3], EnchantCustom [checked=minecraft:feather_falling, level=4], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:soul_speed, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:depth_strider, level=3], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:soul_speed, level=3], EnchantVanilla [checked=minecraft:feather_falling, level=4]]"
      );
      enchantedItemMatcher8 = new EnchantedItemMatcher(Items.NETHERITE_BOOTS.getDefaultInstance(), "Ботинки eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers7.forEach(enchantedItemMatcher8::method4844);
      PotionItemMatcher potionItemMatcher23 = new PotionItemMatcher(
         Items.GOLDEN_HELMET.getDefaultInstance(), "Шлем Солнца", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "SunHelmet"
      );
      var enchantMatchers12 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:critical-enchant-custom, level=2], EnchantCustom [checked=enchantments:destroyer-enchant-custom, level=2], EnchantCustom [checked=enchantments:rich-enchant-custom, level=1], EnchantCustom [checked=minecraft:bane_of_arthropods, level=7], EnchantCustom [checked=minecraft:fire_aspect, level=2], EnchantCustom [checked=minecraft:looting, level=5], EnchantCustom [checked=minecraft:sharpness, level=7], EnchantCustom [checked=minecraft:smite, level=7], EnchantCustom [checked=minecraft:sweeping, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:sweeping_edge, level=3], EnchantVanilla [checked=minecraft:bane_of_arthropods, level=7], EnchantVanilla [checked=minecraft:looting, level=5], EnchantVanilla [checked=minecraft:sharpness, level=7], EnchantVanilla [checked=minecraft:fire_aspect, level=2], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:smite, level=7]]"
      );
      EnchantedItemMatcher enchantedItemMatcher13 = new EnchantedItemMatcher(Items.NETHERITE_SWORD.getDefaultInstance(), "Меч eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers12.forEach(enchantedItemMatcher13::method4844);
      var enchantMatchers13 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:drill-enchant-custom, level=2], EnchantCustom [checked=enchantments:exp-enchant-custom, level=3], EnchantCustom [checked=enchantments:filter-enchant-custom, level=1], EnchantCustom [checked=enchantments:foundry-enchant-custom, level=1], EnchantCustom [checked=enchantments:internal-enchant-custom, level=1], EnchantCustom [checked=enchantments:magnet-enchant-custom, level=1], EnchantCustom [checked=minecraft:efficiency, level=10], EnchantCustom [checked=minecraft:fortune, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:efficiency, level=10], EnchantVanilla [checked=minecraft:fortune, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher14 = new EnchantedItemMatcher(Items.NETHERITE_PICKAXE.getDefaultInstance(), "Кирка eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers13.forEach(enchantedItemMatcher14::method4844);
      var enchantMatchers14 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:stun-enchant-custom, level=2], EnchantCustom [checked=minecraft:multishot, level=1], EnchantCustom [checked=minecraft:piercing, level=5], EnchantCustom [checked=minecraft:quick_charge, level=3], EnchantCustom [checked=minecraft:unbreaking, level=3], EnchantVanilla [checked=minecraft:multishot, level=1], EnchantVanilla [checked=minecraft:piercing, level=5], EnchantVanilla [checked=minecraft:quick_charge, level=3], EnchantVanilla [checked=minecraft:unbreaking, level=3]]"
      );
      EnchantedItemMatcher enchantedItemMatcher15 = new EnchantedItemMatcher(Items.CROSSBOW.getDefaultInstance(), "Арбалет eternity", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers14.forEach(enchantedItemMatcher15::method4844);
      var enchantMatchers15 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=minecraft:impaling, level=5], EnchantCustom [checked=minecraft:looting, level=5], EnchantCustom [checked=minecraft:loyalty, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:loyalty, level=3], EnchantVanilla [checked=minecraft:looting, level=5], EnchantVanilla [checked=minecraft:impaling, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher enchantedItemMatcher16 = new EnchantedItemMatcher(Items.TRIDENT.getDefaultInstance(), "Громовержец", ItemMatcher.Type.HOLLYWORLD);
      enchantMatchers15.forEach(enchantedItemMatcher16::method4844);
      potionItemMatcher5 = new PotionItemMatcher(Items.NETHERITE_SWORD.getDefaultInstance(), "Фармер", ItemMatcher.Type.HOLLYWORLD, "RepairCost", "Фармер");
      PotionItemMatcher potionItemMatcher24 = new PotionItemMatcher(
         Items.ELYTRA.getDefaultInstance(), "Броневая элитра", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "ArmorElytra"
      );
      potionItemMatcher6 = new PotionItemMatcher(
         Items.DYED_SHULKER_BOX.pick(net.minecraft.world.item.DyeColor.MAGENTA).getDefaultInstance(), "Рюкзак 4 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      PotionItemMatcher potionItemMatcher25 = new PotionItemMatcher(
         Items.DYED_SHULKER_BOX.pick(net.minecraft.world.item.DyeColor.RED).getDefaultInstance(), "Рюкзак 3 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      potionItemMatcher7 = new PotionItemMatcher(
         Items.DYED_SHULKER_BOX.pick(net.minecraft.world.item.DyeColor.LIGHT_BLUE).getDefaultInstance(), "Рюкзак 2 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      PotionItemMatcher potionItemMatcher26 = new PotionItemMatcher(
         Items.DYED_SHULKER_BOX.pick(net.minecraft.world.item.DyeColor.PINK).getDefaultInstance(), "Рюкзак 1 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      potionItemMatcher8 = new PotionItemMatcher(
         Items.EXPERIENCE_BOTTLE.getDefaultInstance(), "Пузырек с 50 уровнем", "50", ItemMatcher.Type.HOLLYWORLD, "holy-exp-bottle-value", "5345"
      );
      PotionItemMatcher potionItemMatcher27 = new PotionItemMatcher(
         Items.EXPERIENCE_BOTTLE.getDefaultInstance(), "Пузырь опыта", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "ExpBottle"
      );
      potionItemMatcher9 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман Сатиры",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-rush\"},{\"lvl\":3,\"nbtName\":\"hms-damage\"}"
      );
      potionItemMatcher10 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман infinity",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-health\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      potionItemMatcher11 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман eternity",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      potionItemMatcher12 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Талисман stinger",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"},{\"lvl\":1,\"nbtName\":\"hms-speed\"}"
      );
      potionItemMatcher13 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Мифический талисман на урон 3 броню 2",
         "Мифический талисман",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      potionItemMatcher14 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Мифический талисман на броню 3 урон 2",
         "Мифический талисман",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      PotionItemMatcher potionItemMatcher28 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Мифический талисман на броню 3 скорость 2",
         "Мифический талисман",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-speed\"}"
      );
      PotionItemMatcher potionItemMatcher29 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Легендарный талисман на урон 3",
         "Сфера на урон 3",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-damage\"}"
      );
      PotionItemMatcher potionItemMatcher30 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultInstance(),
         "Легендарный талисман на скорость 3",
         "Сфера на скорость 3",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-speed\"}"
      );
      skullItemMatcher4 = new SkullItemMatcher(
         "Сфера Цербера",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0="
      );
      skullItemMatcher5 = new SkullItemMatcher(
         "Сфера Флеша",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc0MDBlYTE5ZGJkODRmNzVjMzlhZDY4MjNhYzRlZjc4NmYzOWY0OGZjNmY4NDYwMjM2NmFjMjliODM3NDIyIn19fQ=="
      );
      skullItemMatcher6 = new SkullItemMatcher(
         "Сфера Имморталити",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0="
      );
      skullItemMatcher7 = new SkullItemMatcher(
         "Сфера Арморталити",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE2MmI5ZGU2YTI2Yjg2ODY5Y2EyMmVhNDBmMWJkZTgwYTA0MzBhNTQ1NDdiZWNjZThmZGE4NzA3Nzc3MjU4ZiJ9fX0="
      );
      ItemStack itemStack = new SkullItemMatcher(
            "",
            ItemMatcher.Type.HOLLYWORLD,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
         )
         .getItemStack();
      potionItemMatcher16 = new PotionItemMatcher(itemStack, "Сфера на урон 3", ItemMatcher.Type.HOLLYWORLD, "sphereEffect", "{\"lvl\":3,\"nbtName\":\"hms-damage\"}");
      potionItemMatcher17 = new PotionItemMatcher(itemStack, "Сфера на Скорость 3", ItemMatcher.Type.HOLLYWORLD, "sphereEffect", "{\"lvl\":3,\"nbtName\":\"hms-speed\"}");
      potionItemMatcher18 = new PotionItemMatcher(
         itemStack,
         "Сфера eternity",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      potionItemMatcher19 = new PotionItemMatcher(
         itemStack,
         "Сфера stinger",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":1,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      ItemStack itemStack2 = new SkullItemMatcher(
            "",
            ItemMatcher.Type.HOLLYWORLD,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ=="
         )
         .getItemStack();
      potionItemMatcher21 = new PotionItemMatcher(
         itemStack2,
         "Мифическая сфера на урон 3 броню 2",
         "Мифическая сфера",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      potionItemMatcher22 = new PotionItemMatcher(
         itemStack2,
         "Мифическая сфера на броню 3 урон 2",
         "Мифическая сфера",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      PotionItemMatcher potionItemMatcher31 = new PotionItemMatcher(
         itemStack2,
         "Мифическая сфера на броню 3 скорость 2",
         "Мифическая сфера",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-speed\"}"
      );
      PotionItemMatcher potionItemMatcher32 = new PotionItemMatcher(
         Items.POTION.getDefaultInstance(), "Улучшенное зелье силы", ItemMatcher.Type.HOLLYWORLD, "custom_potion_effects", "minecraft:strength@2"
      );
      PotionItemMatcher potionItemMatcher33 = new PotionItemMatcher(
         Items.POTION.getDefaultInstance(), "Улучшенное зелье скорости", ItemMatcher.Type.HOLLYWORLD, "custom_potion_effects", "minecraft:speed@2"
      );
      PotionItemMatcher potionItemMatcher34 = new PotionItemMatcher(
         Items.POTION.getDefaultInstance(), "Зелье Победителя", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "win-potion"
      );
      PotionItemMatcher potionItemMatcher35 = new PotionItemMatcher(
         Items.POTION.getDefaultInstance(), "Зелье исцеления 2", "Зелье исцеление", ItemMatcher.Type.HOLLYWORLD, "Potion", "minecraft:strong_healing"
      );
      PotionItemMatcher potionItemMatcher36 = new PotionItemMatcher(
         Items.POPPED_CHORUS_FRUIT.getDefaultInstance(), "Трапка", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "ALTERNATIVE_TRAP"
      );
      PotionItemMatcher potionItemMatcher37 = new PotionItemMatcher(
         Items.PRISMARINE_SHARD.getDefaultInstance(), "Взрывная Трапка", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "EXPLOSIVE_TRAP"
      );
      PotionItemMatcher potionItemMatcher38 = new PotionItemMatcher(Items.NETHER_STAR.getDefaultInstance(), "Стан", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "STUN_STAR");
      PotionItemMatcher potionItemMatcher39 = new PotionItemMatcher(Items.SNOWBALL.getDefaultInstance(), "Ком снега", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "SnowBall");
      PotionItemMatcher potionItemMatcher40 = new PotionItemMatcher(
         Items.FIRE_CHARGE.getDefaultInstance(), "Взрывная штучка", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "ExplosiveStuff"
      );
      PotionItemMatcher potionItemMatcher41 = new PotionItemMatcher(
         Items.DYE.pick(net.minecraft.world.item.DyeColor.ORANGE).getDefaultInstance(), "Руна Бессмертие", "Бессмертие", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "literunes:rune-id"
      );
      PotionItemMatcher potionItemMatcher42 = new PotionItemMatcher(
         Items.DIAMOND_SWORD.getDefaultInstance(), "Охотник", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "EXP_DROPPER"
      );
      PotionItemMatcher potionItemMatcher43 = new PotionItemMatcher(Items.SNOW_BLOCK.getDefaultInstance(), "Снеговик", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "BLINDNESS");
      PotionItemMatcher potionItemMatcher44 = new PotionItemMatcher(
         Items.SEA_LANTERN.getDefaultInstance(), "Иллюминатор", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "PORTHOLE"
      );
      PotionItemMatcher potionItemMatcher45 = new PotionItemMatcher(Items.ENDER_PEARL.getDefaultInstance(), "Эндермен", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "ENDERMAN");
      PotionItemMatcher potionItemMatcher46 = new PotionItemMatcher(
         Items.PHANTOM_MEMBRANE.getDefaultInstance(), "Анти Фантом", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "ANTI_PHANTOM"
      );
      PotionItemMatcher potionItemMatcher47 = new PotionItemMatcher(
         Items.HONEY_BLOCK.getDefaultInstance(), "Телекинез", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "TELEKINESIS"
      );
      PotionItemMatcher potionItemMatcher48 = new PotionItemMatcher(Items.FEATHER.getDefaultInstance(), "Гравитация", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "GRAVITY");
      PotionItemMatcher potionItemMatcher49 = new PotionItemMatcher(
         Items.WITHER_SKELETON_SKULL.getDefaultInstance(), "Вампиризм", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "VAMPIRISM"
      );
      PotionItemMatcher potionItemMatcher50 = new PotionItemMatcher(Items.POTION.getDefaultInstance(), "Справедливость", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "JUSTICE");
      PotionItemMatcher potionItemMatcher51 = new PotionItemMatcher(
         Items.TRIPWIRE_HOOK.getDefaultInstance(), "Универсальный ключ", ItemMatcher.Type.HOLLYWORLD, "CustomModelData", "123433"
      );
      this.arrayList3.add(enchantedItemMatcher);
      this.arrayList3.add(enchantedItemMatcher2);
      this.arrayList3.add(enchantedItemMatcher9);
      this.arrayList3.add(enchantedItemMatcher10);
      this.arrayList3.add(enchantedItemMatcher11);
      this.arrayList3.add(enchantedItemMatcher12);
      this.arrayList3.add(enchantedItemMatcher7);
      this.arrayList3.add(enchantedItemMatcher8);
      this.arrayList3.add(potionItemMatcher23);
      this.arrayList3.add(potionItemMatcher24);
      this.arrayList3.add(enchantedItemMatcher13);
      this.arrayList3.add(enchantedItemMatcher14);
      this.arrayList3.add(enchantedItemMatcher15);
      this.arrayList3.add(enchantedItemMatcher16);
      this.arrayList3.add(skullItemMatcher4);
      this.arrayList3.add(skullItemMatcher5);
      this.arrayList3.add(skullItemMatcher6);
      this.arrayList3.add(skullItemMatcher7);
      this.arrayList3.add(potionItemMatcher21);
      this.arrayList3.add(potionItemMatcher22);
      this.arrayList3.add(potionItemMatcher31);
      this.arrayList3.add(potionItemMatcher16);
      this.arrayList3.add(potionItemMatcher17);
      this.arrayList3.add(potionItemMatcher18);
      this.arrayList3.add(potionItemMatcher19);
      this.arrayList3.add(potionItemMatcher9);
      this.arrayList3.add(potionItemMatcher10);
      this.arrayList3.add(potionItemMatcher11);
      this.arrayList3.add(potionItemMatcher12);
      this.arrayList3.add(potionItemMatcher13);
      this.arrayList3.add(potionItemMatcher14);
      this.arrayList3.add(potionItemMatcher28);
      this.arrayList3.add(potionItemMatcher29);
      this.arrayList3.add(potionItemMatcher30);
      this.arrayList3.add(potionItemMatcher32);
      this.arrayList3.add(potionItemMatcher33);
      this.arrayList3.add(potionItemMatcher35);
      this.arrayList3.add(potionItemMatcher34);
      this.arrayList3.add(potionItemMatcher8);
      this.arrayList3.add(potionItemMatcher6);
      this.arrayList3.add(potionItemMatcher25);
      this.arrayList3.add(potionItemMatcher7);
      this.arrayList3.add(potionItemMatcher26);
      this.arrayList3.add(potionItemMatcher36);
      this.arrayList3.add(potionItemMatcher37);
      this.arrayList3.add(potionItemMatcher38);
      this.arrayList3.add(potionItemMatcher40);
      this.arrayList3.add(potionItemMatcher39);
      this.arrayList3.add(potionItemMatcher41);
      this.arrayList3.add(potionItemMatcher27);
      this.arrayList3.add(potionItemMatcher42);
      this.arrayList3.add(potionItemMatcher43);
      this.arrayList3.add(potionItemMatcher44);
      this.arrayList3.add(potionItemMatcher45);
      this.arrayList3.add(potionItemMatcher46);
      this.arrayList3.add(potionItemMatcher47);
      this.arrayList3.add(potionItemMatcher48);
      this.arrayList3.add(potionItemMatcher49);
      this.arrayList3.add(potionItemMatcher50);
      this.arrayList3.add(potionItemMatcher51);
      this.arrayList3.add(potionItemMatcher5);
      this.arrayList4.add(new ItemMatcher(Items.GUNPOWDER.getDefaultInstance(), "Порох", ItemMatcher.Type.ANY));
      this.arrayList4
         .add(
            new PotionItemMatcher(Items.PRISMARINE_CRYSTALS.getDefaultInstance(), "Боевой фрагмент", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "BattleFragment")
         );
      this.arrayList4
         .add(
            new PotionItemMatcher(Items.CLAY.getDefaultInstance(), "Взрывчатое вещество", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "EXPLOSIVE_SUBSTANCE")
         );
      this.arrayList4.add(new PotionItemMatcher(Items.TNT.getDefaultInstance(), "Динамит A", "динамит а", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "A"));
      this.arrayList4
         .add(new PotionItemMatcher(Items.GOLDEN_PICKAXE.getDefaultInstance(), "Золотая кирка Джейка", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "jake-pickaxe"));
      this.arrayList4.add(new ItemMatcher(Items.NETHERITE_INGOT.getDefaultInstance(), "Незеритовый слиток", ItemMatcher.Type.HOLLYWORLD));
      this.arrayList4.add(new PotionItemMatcher(Items.TNT.getDefaultInstance(), "Динамит B", "динамит б", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "B"));
      this.arrayList4
         .add(
            new SkullItemMatcher(
               "Осколок сферы",
               ItemMatcher.Type.HOLLYWORLD,
               "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY3YmJjZTIzZTgxNjJlNDJkMjA3MDU1YjBjZTkwZjBlZDU3YjAxNWU1MjEyMTM5YWM4ZmM3ZTZkNDVkZGZjYSJ9fX0="
            )
         );
      this.arrayList4
         .add(new PotionItemMatcher(Items.TNT.getDefaultInstance(), "Динамит B2", "динамит б2", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "B2"));
      this.arrayList4
         .add(new PotionItemMatcher(Items.TNT.getDefaultInstance(), "С4 ВзРыВчАтКа", "с4 взрывчатка", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "C4"));
   }

   private String method1766(ItemMatcher itemMatcher) {
      return itemMatcher.getText22() + itemMatcher.getText2() + itemMatcher.getType().name();
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList() {
      return this.arrayList;
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList2() {
      return this.arrayList2;
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList3() {
      return this.arrayList3;
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList4() {
      return this.arrayList4;
   }
}
