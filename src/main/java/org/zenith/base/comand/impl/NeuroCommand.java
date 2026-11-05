package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.NeuroArgumentType;

public class NeuroCommand extends CommandAbstract {
   public static final File neuroDirectory = new File(Zenith.file, "neuro");

   public NeuroCommand() {
      super("neuro");
      ensureNeuroDirectory();
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(literal("load").then(arg("name", NeuroArgumentType.create()).executes(commandContext -> {
         String var1x = (String)commandContext.getArgument("name", String.class);
         File neuroFile = findNeuroFile(var1x);
         if (neuroFile == null) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Файл нейромодели не найден");
            return 1;
         } else {
            try {
               Zenith.getInstance().getJointCodebook().method2533(neuroFile);
               ChatLogger.method2020(ChatLogger.Type.INFO, "Нейромодель загружена: " + neuroFile.getName());
            } catch (IOException e) {
               ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при загрузке нейромодели");
            }

            return 1;
         }
      })));
      literalArgumentBuilder.then(literal("dir").executes(commandContext -> {
         if (!ensureNeuroDirectory()) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при создании папки нейромоделей");
            return 1;
         } else {
            try {
               Runtime.getRuntime().exec(new String[]{"explorer", neuroDirectory.getAbsolutePath()});
            } catch (IOException e) {
               ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при открытии папки нейромоделей");
            }

            return 1;
         }
      }));
   }

   public static List<String> neuroNames() {
      if (!ensureNeuroDirectory()) {
         return new ArrayList<>();
      } else {
         File[] listFiles = neuroDirectory.listFiles();
         ArrayList<String> strings = new ArrayList<>();
         if (listFiles != null) {
            for (File listFile : listFiles) {
               if (listFile.isFile()) {
                  strings.add(listFile.getName());
               }
            }
         }

         strings.sort(String.CASE_INSENSITIVE_ORDER);
         return strings;
      }
   }

   private static boolean ensureNeuroDirectory() {
      return neuroDirectory.isDirectory() || !neuroDirectory.exists() && neuroDirectory.mkdirs();
   }

   private static File findNeuroFile(String child) {
      if (child != null && !child.isBlank()) {
         try {
            File canonicalFile = neuroDirectory.getCanonicalFile();
            File file = new File(canonicalFile, child).getCanonicalFile();
            return file.toPath().startsWith(canonicalFile.toPath()) && file.isFile() ? file : null;
         } catch (IOException e) {
            return null;
         }
      } else {
         return null;
      }
   }
}
