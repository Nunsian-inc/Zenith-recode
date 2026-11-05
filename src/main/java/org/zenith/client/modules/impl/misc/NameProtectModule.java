package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.ModuleHookDefinitions;
import org.zenith.utility.misc.TextUtils;

@ModuleInfo(
   name = "NameProtect",
   category = ModuleCategory.MISC,
   description = "module.nameprotect.description"
)
public final class NameProtectModule extends Module {
   public static final NameProtectModule INSTANCE = new NameProtectModule();
   private final TextSetting nameSetting = new TextSetting(
      "module.nameProtect.nameSetting",
      "module.nameProtect.nameSetting.desc",
      "Zenith",
      "module.nameProtect.nameSetting.empty",
      TextSetting.Entry.method2125(16)
   );
   private final TextSetting gowno = new TextSetting("gowno", "gowno", "hownp", () -> false);
   public Component text = null;
   public Component text2;
   private int intValue = Integer.MIN_VALUE;
   private String text3;
   private Runnable runnable;
   private final BooleanSetting hideFriends = new BooleanSetting("module.nameProtect.hideFriends", "module.nameProtect.hideFriends.desc", false);
   private final ButtonSetting openCommand = new ButtonSetting(
      "module.nameProtect.openCommand", "K", "module.nameProtect.openCommand.desc", () -> mc.gui.setScreen(new ChatScreen(".nameprotect ", false))
   );

   private NameProtectModule() {
   }

   @Override
   public void onEnable() {
      if (this.runnable == null) {
         this.runnable = ModuleHookDefinitions.class124.method1646(0, entry5 -> entry5.text = method5149(entry5.text));
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.runnable != null) {
         this.runnable.run();
         this.runnable = null;
      }

      super.onDisable();
   }

   public boolean method5140() {
      return this.hideFriends.isEnabled();
   }

   public static String method5141() {
      return INSTANCE.isEnabled() ? INSTANCE.getNameSetting().getValue() : mc.player.getScoreboardName();
   }

   public Component method5142(Component decorateName) {
      String scoreboardName = mc.player.getScoreboardName();
      String text = method5141();
      this.setEnabled(false);

      MutableComponent component;
      try {
         Component component2 = this.method5143(decorateName, scoreboardName, text);
         if (this.text != null) {
            return TextUtils.method2107(decorateName, scoreboardName, this.text.copy().append(component2.copy()));
         }

         component = TextUtils.method2111(decorateName, scoreboardName, component2);
      } finally {
         this.setEnabled(true);
      }

      return component;
   }

   private Component method5143(Component decorateName, String scoreboardName, String text) {
      if (text != null && !text.isEmpty()) {
         var styles = this.method5144(decorateName, scoreboardName);
         if (styles.isEmpty()) {
            return Component.literal(text);
         } else {
            MutableComponent empty = Component.empty();
            int index = 0;

            for (int i = 0; index < text.length(); i++) {
               int codePointAt = text.codePointAt(index);
               Style style = (Style)styles.get(Math.min(i, styles.size() - 1));
               empty.append(Component.literal(new String(Character.toChars(codePointAt))).setStyle(style));
               index += Character.charCount(codePointAt);
            }

            return empty;
         }
      } else {
         return Component.empty();
      }
   }

   private List<Style> method5144(Component decorateName, String scoreboardName) {
      if (decorateName != null && scoreboardName != null && !scoreboardName.isEmpty()) {
         ArrayList<Style> styles = new ArrayList<>();
         ArrayList arrayList = new ArrayList();
         StringBuilder builder = new StringBuilder();
         decorateName.getVisualOrderText().accept((var2x, var3x, var4x) -> {
            builder.appendCodePoint(var4x);
            arrayList.add(var3x != null ? var3x : Style.EMPTY);
            return true;
         });
         String string = builder.toString();
         int indexOf = string.indexOf(scoreboardName);
         if (indexOf == -1) {
            return styles;
         } else {
            int codePointCount = string.codePointCount(0, indexOf);
            int n = scoreboardName.codePointCount(0, scoreboardName.length());
            int n2 = Math.min(codePointCount + n, arrayList.size());

            for (int i = codePointCount; i < n2; i++) {
               styles.add((Style)arrayList.get(i));
            }

            return styles;
         }
      } else {
         return List.of();
      }
   }

   @EventTarget
   public void method5145(UpdateEvent event) {
      String str = this.gowno.getValue();
      if (!str.equals(this.text3)) {
         this.text3 = str;
         if (str.isEmpty()) {
            this.text = null;
            this.intValue = Integer.MIN_VALUE;
         } else {
            try {
               int indexOf = str.indexOf(32);
               if (indexOf <= 0 || indexOf >= str.length() - 1) {
                  this.text = null;
                  this.intValue = Integer.MIN_VALUE;
                  return;
               }

               this.intValue = Integer.parseInt(str.substring(0, indexOf));
               JsonElement string = JsonParser.parseString(str.substring(indexOf + 1));
               this.text = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, string).result().orElse(Component.empty());
            } catch (Exception e) {
               this.text = null;
               this.intValue = Integer.MIN_VALUE;
               e.printStackTrace();
            }
         }
      }
   }

   public Component method5146(PlayerInfo playerInfo) {
      return playerInfo.getTabListDisplayName() != null
         ? this.method5147(playerInfo, playerInfo.getTabListDisplayName().copy())
         : this.method5147(playerInfo, PlayerTeam.formatNameForTeam(playerInfo.getTeam(), Component.literal(playerInfo.getProfile().name())));
   }

   private Component method5147(PlayerInfo playerInfo, MutableComponent formatNameForTeam) {
      return playerInfo.getGameMode() == GameType.SPECTATOR ? formatNameForTeam.withStyle(ChatFormatting.ITALIC) : formatNameForTeam;
   }

   @EventTarget
   public void method5148(ChatReceiveEvent event) {
      String scoreboardName = this.method5150(event.getText().getString());
      if (scoreboardName != null) {
         event.method7858(TextUtils.method2110(event.getText(), scoreboardName, method5141()));
      }
   }

   public static String method5149(String text) {
      NameProtectModule nameProtectModule = INSTANCE;
      if (text != null && !text.isEmpty() && nameProtectModule != null && nameProtectModule.isEnabled() && mc.player != null) {
         String scoreboardName = mc.player.getScoreboardName();
         if (text.contains(scoreboardName)) {
            return text.replace(scoreboardName, INSTANCE.getNameSetting().getValue());
         } else {
            if (nameProtectModule instanceof NameProtectModule nameProtectModule2 && nameProtectModule2.hideFriends.isEnabled()) {
               for (String target : Zenith.getInstance().getFriends().getItems()) {
                  if (text.contains(target)) {
                     return text.replace(target, INSTANCE.getNameSetting().getValue());
                  }
               }
            }

            return text;
         }
      } else {
         return text;
      }
   }

   private String method5150(String string) {
      String scoreboardName = mc.player.getScoreboardName();
      if (string.contains(scoreboardName)) {
         return scoreboardName;
      } else {
         if (this.hideFriends.isEnabled()) {
            for (String str : Zenith.getInstance().getFriends().getItems()) {
               if (string.contains(str)) {
                  return str;
               }
            }
         }

         return null;
      }
   }

   public void method5151(JsonElement jsonElement, int intValue) {
      this.gowno.setValue(intValue + " " + jsonElement.toString());
      this.intValue = intValue;
   }

   public void method5152() {
      this.gowno.setValue("");
      this.intValue = Integer.MIN_VALUE;
   }

   @Generated
   public TextSetting getNameSetting() {
      return this.nameSetting;
   }

   @Generated
   public Component getText() {
      return this.text;
   }

   @Generated
   public int getIntValue18() {
      return this.intValue;
   }
}
