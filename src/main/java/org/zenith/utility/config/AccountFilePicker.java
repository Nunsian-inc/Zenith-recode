package org.zenith.utility.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class AccountFilePicker {
   private AccountFilePicker() {
   }

   public static CompletableFuture<Optional<Path>> method1001() {
      return CompletableFuture.supplyAsync(
         () -> {
            try {
               Path path = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Alts", "cookies");
               if (!Files.exists(path)) {
                  path = Path.of(System.getProperty("user.home"), "Desktop", "Alts", "cookies");
               }

               if (!Files.exists(path)) {
                  path = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Alts");
               }

               if (!Files.exists(path)) {
                  path = Path.of(System.getProperty("user.home"), "Desktop", "Alts");
               }

               String str = Files.exists(path) ? path.toAbsolutePath().toString().replace("'", "''") : "";
               String str2 = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;Add-Type -AssemblyName System.Windows.Forms | Out-Null;$f=New-Object System.Windows.Forms.OpenFileDialog;$f.Title='Выберите файл с куки или токеном';$f.Filter='Файлы аккаунтов (*.txt;*.json)|*.txt;*.json|Все файлы (*.*)|*.*';"
                  + (str.isEmpty() ? "" : "$f.InitialDirectory='" + str + "';")
                  + "$f.Multiselect=$false;if($f.ShowDialog() -eq 'OK'){[Console]::Write($f.FileName)}";
               Process start = new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-STA", "-Command", str2)
                  .redirectErrorStream(true)
                  .start();
               StringBuilder builder = new StringBuilder();
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream(), StandardCharsets.UTF_8));

               String line;
               try {
                  while ((line = bufferedReader.readLine()) != null) {
                     builder.append(line);
                  }
               } catch (Throwable e) {
                  try {
                     bufferedReader.close();
                  } catch (Throwable ex) {
                     e.addSuppressed(ex);
                  }

                  throw e;
               }

               bufferedReader.close();
               if (start.waitFor() != 0) {
                  throw new IllegalStateException("PowerShell file dialog process returned error.");
               } else {
                  String pathname = builder.toString().trim();
                  if (pathname.isEmpty()) {
                     return Optional.empty();
                  } else {
                     File file = new File(pathname);
                     return file.isFile() ? Optional.of(file.toPath()) : Optional.empty();
                  }
               }
            } catch (Exception e) {
               throw new RuntimeException("Не удалось открыть окно выбора файла: " + e.getMessage(), e);
            }
         }
      );
   }
}
