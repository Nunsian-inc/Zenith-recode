package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

// 26.3 no longer ships lwjgl-tinyfd. Same contract as TinyFileDialogs.tinyfd_openFileDialog
// (filters as a PointerBuffer of UTF-8 patterns, multiple results joined with '|', null on cancel),
// implemented with the Windows file dialog through PowerShell like Zenith's other pickers.
public final class FileDialogs {
   private FileDialogs() {
   }

   @Nullable
   public static String tinyfd_openFileDialog(String title, @Nullable String defaultPath, @Nullable PointerBuffer patterns, @Nullable String description, boolean multiple) {
      List<String> exts = new ArrayList<>();
      if (patterns != null) {
         for (int i = patterns.position(); i < patterns.limit(); i++) {
            exts.add(MemoryUtil.memUTF8(patterns.get(i)));
         }
      }

      String filter = exts.isEmpty() ? "*.*" : String.join(";", exts);
      String label = description == null || description.isEmpty() ? filter : description;
      String script = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;Add-Type -AssemblyName System.Windows.Forms | Out-Null;"
         + "$f=New-Object System.Windows.Forms.OpenFileDialog;"
         + "$f.Title='" + ps(title) + "';"
         + "$f.Filter='" + ps(label) + " (" + ps(filter) + ")|" + ps(filter) + "';"
         + (defaultPath == null || defaultPath.isEmpty() ? "" : "$f.InitialDirectory='" + ps(defaultPath) + "';")
         + "$f.Multiselect=$" + multiple + ";"
         + "if($f.ShowDialog() -eq 'OK'){[Console]::Write(($f.FileNames -join '|'))}";

      try {
         Process process = new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-STA", "-Command", script)
            .redirectErrorStream(true)
            .start();
         StringBuilder out = new StringBuilder();
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
               out.append(line);
            }
         }

         String result = out.toString().trim();
         return process.waitFor() != 0 || result.isEmpty() ? null : result;
      } catch (Exception e) {
         return null;
      }
   }

   private static String ps(String s) {
      return s == null ? "" : s.replace("'", "''");
   }
}
