package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import java.util.Map.Entry;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.TextSetting;

@ModuleInfo(
   name = "AutoAuth",
   category = ModuleCategory.MISC,
   description = "module.autoauth.description"
)
public final class AutoAuthModule extends Module {
   public static final AutoAuthModule INSTANCE = new AutoAuthModule();
   private final TextSetting command = (new TextSetting(
         "autoauth.passSetting.command", "autoauth.passSetting.command.desc", "BogdanSuperCoder", "Default password"
      ) {
         @Override
         public void safe(JsonObject json) {
         }

         @Override
         public void load(JsonObject json) {
         }

         @Override
         public boolean method2122(String text) {
            boolean flag = !text.equals(this.getValue());
            if (!super.method2122(text)) {
               return false;
            } else {
               if (flag) {
                  AutoAuthModule.this.method2711();
               }

               return true;
            }
         }
      })
      .method2120();
   private final BooleanSetting randomPass = new BooleanSetting("autoauth.randomPass", "autoauth.randomPass.description", true);
   private final File file = new File("autoauth_accounts.txt");
   private final BooleanSetting login = new BooleanSetting("autoauth.loginSetting.login", "autoauth.loginSetting.description", true);
   private final ButtonSetting openFile = new ButtonSetting("autoauth.openFile", "K", () -> {
      try {
         String absolutePath = this.file.getAbsolutePath();
         new ProcessBuilder("cmd", "/c", "start", "", "\"" + absolutePath + "\"").start();
      } catch (Exception e) {
         e.printStackTrace();
      }
   });
   private final Map<String, String> items = new HashMap<>();

   private AutoAuthModule() {
      this.method2710();
   }

   private void method2710() {
      try {
         if (!this.file.exists()) {
            this.file.createNewFile();
            this.method2711();
            return;
         }

         for (String allLine : Files.readAllLines(this.file.toPath())) {
            if (allLine.contains(":")) {
               String[] strings = allLine.split(":", 2);
               if (strings[0].trim().equalsIgnoreCase("default")) {
                  this.command.setValue(strings[1].trim());
               } else {
                  String[] strings2 = allLine.split(":", 3);
                  if (strings2.length >= 3) {
                     String lowerCase = strings2[0].trim().toLowerCase();
                     String str = strings2[1].trim();
                     String str2 = strings2[2].trim();
                     String str3 = lowerCase + ":" + str.toLowerCase();
                     this.items.put(str3, str2);
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void method2711() {
      try {
         StringBuilder builder = new StringBuilder();
         builder.append("default : ").append(this.command.getValue()).append("\n");

         for (Entry entry : this.items.entrySet()) {
            String key = (String)entry.getKey();
            String str = (String)entry.getValue();
            String[] strings = key.split(":", 2);
            String str2 = strings[0];
            String str3 = strings[1];
            builder.append(str2).append(" : ").append(str3).append(" : ").append(str).append("\n");
         }

         Files.writeString(this.file.toPath(), builder.toString(), StandardOpenOption.TRUNCATE_EXISTING);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private String method2712() {
      String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
      Random random = new Random();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 12; i++) {
         builder.append(str.charAt(random.nextInt(str.length())));
      }

      return builder.toString();
   }

   private String method2713(String ip) {
      if (ip != null && !ip.isEmpty()) {
         String[] strings = ip.split("\\.");
         return strings.length >= 2 ? strings[strings.length - 2] : ip;
      } else {
         return "unknown";
      }
   }

   @EventTarget
   public void method2714(PacketEvent event) {
      if (event.isIncoming()) {
         if (event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket) {
            if (mc.getConnection() != null) {
               String lowerCase = systemChatPacket.content().getString().toLowerCase();
               boolean flag = lowerCase.contains("/register") || lowerCase.contains("зарегистрируйтесь");
               boolean flag2 = this.login.isEnabled() && (lowerCase.contains("/login") || lowerCase.contains("/ʟ [пᴀᴘᴏль]"));
               if (flag || flag2) {
                  mc.execute(() -> this.method2715(flag));
               }
            }
         }
      }
   }

   private void method2715(boolean flag) {
      if (mc.getConnection() != null) {
         String ip = mc.getCurrentServer() != null ? mc.getCurrentServer().ip : "";
         String lowerCase = this.method2713(ip).toLowerCase();
         String str = mc.getUser().getName().toLowerCase();
         String key = lowerCase + ":" + str;
         String str2 = this.items.getOrDefault(key, null);
         if (flag) {
            if (str2 == null) {
               str2 = this.randomPass.isEnabled() ? this.method2712() : this.command.getValue();
            }

            this.items.put(key, str2);
            this.method2711();
            mc.getConnection().sendCommand("register %s %s".formatted(str2, str2));
         } else {
            if (str2 == null) {
               str2 = this.command.getValue();
               this.items.put(key, str2);
               this.method2711();
            }

            mc.getConnection().sendCommand("login " + str2);
         }
      }
   }
}
