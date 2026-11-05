package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket.Entry;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "CommandProbe",
   category = ModuleCategory.MISC,
   description = "module.commandprobe.description"
)
public class CommandProbeModule extends Module {
   public static final CommandProbeModule INSTANCE = new CommandProbeModule();
   private final BooleanSetting tree = new BooleanSetting("module.commandProbe.tree", true);
   private final BooleanSetting completions = new BooleanSetting("module.commandProbe.completions", true);
   private final BooleanSetting namespaces = new BooleanSetting("module.commandProbe.namespaces", true);
   private final SliderSetting depth = new SliderSetting("module.commandProbe.depth", 2.0F, 1.0F, 3.0F, 1.0F, "module.commandProbe.depth.desc", "");
   private final SliderSetting delay = new SliderSetting("module.commandProbe.delay", 120.0F, 50.0F, 1000.0F, 10.0F, "module.commandProbe.delay.desc", "ms");
   private static final String[] texts = new String[]{
      "minecraft",
      "bukkit",
      "paper",
      "spigot",
      "purpur",
      "velocity",
      "bungee",
      "waterfall",
      "essentials",
      "essentialsx",
      "cmi",
      "luckperms",
      "lp",
      "vault",
      "placeholderapi",
      "papi",
      "worldedit",
      "fawe",
      "worldguard",
      "multiverse",
      "mv",
      "coreprotect",
      "co",
      "plan",
      "viaversion",
      "via",
      "viabackwards",
      "viarewind",
      "protocollib",
      "packetevents",
      "holographicdisplays",
      "decentholograms",
      "holograms",
      "citizens",
      "mythicmobs",
      "litebans",
      "advancedban",
      "banmanager",
      "maintenance",
      "authme",
      "nlogin",
      "libreLogin",
      "spark",
      "chunky",
      "clearlag",
      "farmlimiter",
      "epicanchors",
      "wildstacker",
      "shopgui",
      "shopguiplus",
      "quickshop",
      "chestshop",
      "playervaults",
      "enderchestplus",
      "jobs",
      "mcmmo",
      "aurelium",
      "auraskills",
      "itemsadder",
      "oraxen",
      "nexo",
      "headdatabase",
      "betterrtp",
      "huskhomes",
      "residence",
      "griefprevention",
      "lands",
      "towny",
      "factions",
      "clans",
      "simpleclans",
      "discordsrv",
      "votifier",
      "crazycrates",
      "excellentcrates",
      "tab",
      "featherboard",
      "animatedscoreboard",
      "skript",
      "denizen",
      "plugman",
      "nocheatplus",
      "matrix",
      "vulcan",
      "spartan",
      "themis",
      "negativity",
      "grim",
      "aac",
      "anticheat",
      "polar",
      "kauri",
      "horizon",
      "abyss",
      "sentinel",
      "guardian",
      "holyworld",
      "hw",
      "holy",
      "core",
      "hcore",
      "anarchy",
      "auction",
      "ah",
      "donate"
   };
   private final Deque<String> deque = new ArrayDeque<>();
   private final Set<String> items = new HashSet<>();
   private final List<String> items2 = new ArrayList<>();
   private PrintWriter printWriter;
   private String text;
   private int intValue = 9000;
   private long longValue;
   private int intValue2;

   private CommandProbeModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.deque.clear();
      this.items.clear();
      this.intValue2 = 0;
      this.text = null;
      this.method6370();
      if (this.tree.isEnabled()) {
         this.method6364();
      }

      if (this.completions.isEnabled()) {
         this.method6367();
      }

      if (this.namespaces.isEnabled()) {
         this.method6368();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.line("=== completions answered: " + this.intValue2 + " ===");
      ChatLogger.method2019("CommandProbe: ответов " + this.intValue2);
      this.method6371();
   }

   private void method6364() {
      if (mc.getConnection() != null) {
         RootCommandNode root = mc.getConnection().getCommands().getRoot();
         this.line("=== command tree ===");

         for (Object obj : (Iterable<?>)(Object)(root.getChildren())) {
            CommandNode commandNode = (CommandNode)obj;
            this.method6365(commandNode, "", 0);
         }
      }
   }

   private void method6365(CommandNode<ClientSuggestionProvider> commandNode, String str, int count) {
      if (count <= 4) {
         String str2 = this.method6366(commandNode);
         this.line("  ".repeat(count) + str + str2);

         for (CommandNode commandNode2 : commandNode.getChildren()) {
            this.method6365(commandNode2, "", count + 1);
         }

         if (commandNode.getRedirect() != null) {
            this.line("  ".repeat(count + 1) + "-> redirect " + commandNode.getRedirect().getName());
         }
      }
   }

   private String method6366(CommandNode<ClientSuggestionProvider> commandNode) {
      if (commandNode instanceof LiteralCommandNode literalCommandNode) {
         return literalCommandNode.getLiteral() + (commandNode.getCommand() != null ? " *" : "");
      } else if (commandNode instanceof ArgumentCommandNode argumentCommandNode) {
         String str = argumentCommandNode.getCustomSuggestions() == null ? "" : " suggest=" + argumentCommandNode.getCustomSuggestions().getClass().getSimpleName();
         return "<" + argumentCommandNode.getName() + ":" + argumentCommandNode.getType().getClass().getSimpleName() + ">" + str + (commandNode.getCommand() != null ? " *" : "");
      } else {
         return commandNode.getName();
      }
   }

   private void method6367() {
      if (mc.getConnection() != null) {
         for (CommandNode commandNode : mc.getConnection().getCommands().getRoot().getChildren()) {
            if (commandNode instanceof LiteralCommandNode literalCommandNode) {
               this.deque.add(literalCommandNode.getLiteral() + " ");
            }
         }

         this.line("=== completions: в очереди " + this.deque.size() + " ===");
      }
   }

   private void method6368() {
      for (String text : texts) {
         this.deque.add(text + ":");
      }

      this.line("=== namespace probe: " + texts.length + " ===");
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.getConnection() != null) {
         if (this.text == null || System.currentTimeMillis() - this.longValue >= 1500L) {
            if (System.currentTimeMillis() - this.longValue >= (long)this.delay.getValue3()) {
               String text = this.deque.poll();
               if (text == null) {
                  if (this.text == null) {
                     this.setToggled(false);
                  }
               } else if (this.items.add(text)) {
                  this.text = text;
                  this.longValue = System.currentTimeMillis();
                  mc.getConnection().send(new ServerboundCommandSuggestionPacket(++this.intValue, "/" + text));
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100()) {
         if (event.getPacket3() instanceof ClientboundCommandSuggestionsPacket suggestionsPacket) {
            mc.execute(() -> this.method6369(suggestionsPacket));
         }
      }
   }

   private void method6369(ClientboundCommandSuggestionsPacket suggestionsPacket) {
      if (this.isEnabled()) {
         String text = this.text;
         this.text = null;
         if (text != null) {
            ArrayList<String> elements = new ArrayList();

            for (Entry suggestion : suggestionsPacket.suggestions()) {
               elements.add(suggestion.text());
            }

            if (!elements.isEmpty()) {
               this.intValue2++;
               this.line("suggest| /" + text.trim() + " -> " + String.join(", ", elements));
               int length = text.trim().split("\\s+").length;
               if (length < (int)this.depth.getValue3()) {
                  int n = 0;

                  for (String str : elements) {
                     if (!str.startsWith("@") && str.length() <= 24) {
                        if (n++ >= 12) {
                           break;
                        }

                        this.deque.add(text + str + " ");
                     }
                  }
               }
            }
         }
      }
   }

   private void method6370() {
      try {
         File file = new File(Minecraft.getInstance().gameDirectory, "zenith-itemdump");
         if (!file.exists() && !file.mkdirs()) {
            return;
         }

         File file2 = new File(file, "commandprobe-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(file2, true), true);
         ChatLogger.method2019("CommandProbe: " + file2.getName());
      } catch (Exception e) {
         this.printWriter = null;
      }
   }

   private void line(String x) {
      if (this.printWriter != null) {
         this.printWriter.println(x);
      }
   }

   private void method6371() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception e) {
         }

         this.printWriter = null;
      }
   }
}
