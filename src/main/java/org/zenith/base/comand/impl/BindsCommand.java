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
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> var1) {
      var1.executes(var1x -> {
         this.sendHelp();
         return 1;
      });
      var1.then(
         literal("list")
            .executes(
               var1x -> {
                  boolean var2 = false;
                  int var3 = 0;

                  for (Module var5 : Zenith.getInstance().getModuleManager().getItems()) {
                     List<BindSetting> var6 = var5.getSettings()
                        .stream()
                        .filter(var0 -> var0 instanceof BindSetting)
                        .map(var0 -> (BindSetting)var0)
                        .filter(var1xx -> this.isBound(var1xx.getKeyCode()))
                        .toList();
                     boolean var7 = this.isBound(var5.getKeyCode());
                     if (var7 || !var6.isEmpty()) {
                        if (!var2) {
                           this.sendTitle("Назначенные бинды");
                           var2 = true;
                        }

                        if (var7) {
                           this.sendBindLine(var5.getName(), KeyName.method6702(var5.getKeyCode(), var5.getKeyModifiers()), false);
                           var3++;
                        } else {
                           this.sendModuleLine(var5.getName());
                        }

                        for (Object var9_o : (Iterable<?>)(Object)(var6)) {
                           BindSetting var9 = (BindSetting)var9_o;
                           this.sendBindLine(var9.getName(), KeyName.method6701(var9.getKeyCode()), true);
                           var3++;
                        }
                     }
                  }

                  if (!var2) {
                     ChatLogger.method2019("Список биндов пуст!");
                  } else {
                     this.sendFooter("Всего биндов: " + var3);
                  }

                  return 1;
               }
            )
      );
      var1.then(literal("clear").executes(var1x -> {
         int var2 = this.clearBinds();
         if (var2 == 0) {
            ChatLogger.method2019("Бинды уже пусты!");
         } else {
            this.sendTitle("Бинды очищены");
            this.sendFooter("Сброшено биндов: " + var2);
         }

         return 1;
      }));
      var1.then(literal("help").executes(var1x -> {
         this.sendHelp();
         return 1;
      }));
   }

   private boolean isBound(int var1) {
      return var1 != KeyName.KEY_NONE.intValue && !KeyName.method6701(var1).isEmpty();
   }

   private int clearBinds() {
      int var1 = 0;

      for (Module var3 : Zenith.getInstance().getModuleManager().getItems()) {
         if (this.isBound(var3.getKeyCode())) {
            var3.setKeyCode(KeyName.KEY_NONE.intValue);
            var1++;
         }

         for (BindSetting var5 : this.getKeySettings(var3)) {
            if (this.isBound(var5.getKeyCode())) {
               var5.setKeyCode(KeyName.KEY_NONE.intValue);
               var1++;
            }
         }
      }

      return var1;
   }

   private List<BindSetting> getKeySettings(Module var1) {
      return var1.getSettings().stream().filter(var0 -> var0 instanceof BindSetting).map(var0 -> (BindSetting)var0).toList();
   }

   private void sendHelp() {
      this.sendTitle("Binds");
      this.sendDescription();
      this.sendUsage(".binds list", "вывести все назначенные бинды");
      this.sendUsage(".binds clear", "очистить все бинды");
   }

   private void sendTitle(String var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(this.part(var1, var2.getPrimaryColor().getZenithColor(), true));
   }

   private void sendDescription() {
      ZenithStyle var1 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(
         this.part("Показывает назначенные клавиши модулей и KeySetting, а также может очистить их.", var1.getTextEnable().getZenithColor(), false)
      );
   }

   private void sendUsage(String var1, String var2) {
      ZenithStyle var3 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      MutableComponent var4 = Component.empty()
         .append(this.part(var1, var3.getPrimaryColor().getZenithColor(), true))
         .append(this.part(" - ", var3.getTextSecondary().getZenithColor(), false))
         .append(this.part(var2, var3.getTextEnable().getZenithColor(), false));
      this.sendStyled(var4);
   }

   private void sendModuleLine(String var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(this.part(var1, var2.getTextEnable().getZenithColor(), true));
   }

   private void sendBindLine(String var1, String var2, boolean var3) {
      ZenithStyle var4 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      MutableComponent var5 = Component.empty();
      if (var3) {
         var5.append(this.part("  - ", var4.getTextSecondary().getZenithColor(), false));
      }

      var5.append(this.part(var1, var3 ? var4.getTextSecondary().getZenithColor() : var4.getTextEnable().getZenithColor(), !var3))
         .append(this.part(" -> ", var4.getTextSecondary().getZenithColor(), false))
         .append(this.part(var2, var4.getPrimaryColor().getZenithColor(), true));
      this.sendStyled(var5);
   }

   private void sendFooter(String var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.sendStyled(this.part(var1, var2.getTextSecondary().getZenithColor(), false));
   }

   private void sendStyled(MutableComponent var1) {
      if (mc.player != null) {
         mc.player.sendSystemMessage(this.buildPrefix().append(Component.literal(" ")).append(var1));
      }
   }

   private MutableComponent buildPrefix() {
      ZenithStyle var1 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      return Component.empty()
         .append(this.part("[ ", var1.getTextSecondary().getZenithColor(), false))
         .append(this.part("Zenith", var1.getPrimaryColor().getZenithColor(), true))
         .append(this.part(" ]", var1.getTextSecondary().getZenithColor(), false));
   }

   private MutableComponent part(String var1, ZenithColor var2, boolean var3) {
      return Component.literal(var1).setStyle(Style.EMPTY.withColor(var2.method7287()).withBold(var3));
   }
}
