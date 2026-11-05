package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.misc.KeyName;

public class BindsCommand extends CommandAbstract {
   public BindsCommand() {
      super("binds");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(var1x -> {
         this.sendHelp();
         return 1;
      });
      literalArgumentBuilder.then(
         literal("list")
            .executes(
               var1x -> {
                  boolean flag = false;
                  int n = 0;

                  for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
                     List<BindSetting> bindSettings = moduleValue.getSettings()
                        .stream()
                        .filter(setting -> setting instanceof BindSetting)
                        .map(setting -> (BindSetting)setting)
                        .filter(var1xx -> this.isBound(var1xx.getKeyCode()))
                        .toList();
                     boolean bound = this.isBound(moduleValue.getKeyCode());
                     if (bound || !bindSettings.isEmpty()) {
                        if (!flag) {
                           this.sendTitle("Назначенные бинды");
                           flag = true;
                        }

                        if (bound) {
                           this.sendBindLine(moduleValue.getName(), KeyName.method6702(moduleValue.getKeyCode(), moduleValue.getKeyModifiers()), false);
                           n++;
                        } else {
                           this.sendModuleLine(moduleValue.getName());
                        }

                        for (BindSetting bindSetting : bindSettings) {
                           this.sendBindLine(bindSetting.getName(), KeyName.method6701(bindSetting.getKeyCode()), true);
                           n++;
                        }
                     }
                  }

                  if (!flag) {
                     ChatLogger.method2019("Список биндов пуст!");
                  } else {
                     this.sendFooter("Всего биндов: " + n);
                  }

                  return 1;
               }
            )
      );
      literalArgumentBuilder.then(literal("clear").executes(var1x -> {
         int clearBinds = this.clearBinds();
         if (clearBinds == 0) {
            ChatLogger.method2019("Бинды уже пусты!");
         } else {
            this.sendTitle("Бинды очищены");
            this.sendFooter("Сброшено биндов: " + clearBinds);
         }

         return 1;
      }));
      literalArgumentBuilder.then(literal("help").executes(var1x -> {
         this.sendHelp();
         return 1;
      }));
   }

   private boolean isBound(int keyCode) {
      return keyCode != KeyName.KEY_NONE.intValue && !KeyName.method6701(keyCode).isEmpty();
   }

   private int clearBinds() {
      int n = 0;

      for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
         if (this.isBound(moduleValue.getKeyCode())) {
            moduleValue.setKeyCode(KeyName.KEY_NONE.intValue);
            n++;
         }

         for (BindSetting keySetting : this.getKeySettings(moduleValue)) {
            if (this.isBound(keySetting.getKeyCode())) {
               keySetting.setKeyCode(KeyName.KEY_NONE.intValue);
               n++;
            }
         }
      }

      return n;
   }

   private List<BindSetting> getKeySettings(Module moduleValue) {
      return moduleValue.getSettings().stream().filter(setting -> setting instanceof BindSetting).map(setting -> (BindSetting)setting).toList();
   }

   private void sendHelp() {
      this.sendTitle("Binds");
      this.sendDescription();
      this.sendUsage(".binds list", "вывести все назначенные бинды");
      this.sendUsage(".binds clear", "очистить все бинды");
   }

   private void sendTitle(String name) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(this.part(name, currentStyle.getPrimaryColor().getZenithColor(), true));
   }

   private void sendDescription() {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(
         this.part("Показывает назначенные клавиши модулей и KeySetting, а также может очистить их.", currentStyle.getTextEnable().getZenithColor(), false)
      );
   }

   private void sendUsage(String name, String str) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      MutableComponent part = Component.empty()
         .append(this.part(name, currentStyle.getPrimaryColor().getZenithColor(), true))
         .append(this.part(" - ", currentStyle.getTextSecondary().getZenithColor(), false))
         .append(this.part(str, currentStyle.getTextEnable().getZenithColor(), false));
      this.sendStyled(part);
   }

   private void sendModuleLine(String name) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(this.part(name, currentStyle.getTextEnable().getZenithColor(), true));
   }

   private void sendBindLine(String name, String str, boolean flag) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      MutableComponent empty = Component.empty();
      if (flag) {
         empty.append(this.part("  - ", currentStyle.getTextSecondary().getZenithColor(), false));
      }

      empty.append(this.part(name, flag ? currentStyle.getTextSecondary().getZenithColor() : currentStyle.getTextEnable().getZenithColor(), !flag))
         .append(this.part(" -> ", currentStyle.getTextSecondary().getZenithColor(), false))
         .append(this.part(str, currentStyle.getPrimaryColor().getZenithColor(), true));
      this.sendStyled(empty);
   }

   private void sendFooter(String name) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(this.part(name, currentStyle.getTextSecondary().getZenithColor(), false));
   }

   private void sendStyled(MutableComponent part) {
      if (mc.player != null) {
         mc.player.sendSystemMessage(this.buildPrefix().append(Component.literal(" ")).append(part));
      }
   }

   private MutableComponent buildPrefix() {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      return Component.empty()
         .append(this.part("[ ", currentStyle.getTextSecondary().getZenithColor(), false))
         .append(this.part("Zenith", currentStyle.getPrimaryColor().getZenithColor(), true))
         .append(this.part(" ]", currentStyle.getTextSecondary().getZenithColor(), false));
   }

   private MutableComponent part(String name, ZenithColor zenithColor, boolean bold) {
      return Component.literal(name).setStyle(Style.EMPTY.withColor(zenithColor.method7287()).withBold(bold));
   }
}
