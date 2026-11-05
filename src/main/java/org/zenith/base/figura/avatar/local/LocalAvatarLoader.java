package org.zenith.base.figura.avatar.local;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.UserData;
import org.zenith.base.figura.parsers.AvatarMetadataParser;
import org.zenith.base.figura.parsers.BlockbenchCommonTypes;
import org.zenith.base.figura.parsers.BlockbenchParser2;
import org.zenith.base.figura.parsers.LuaScriptParser;
import org.zenith.base.figura.parsers.ModelParseResult;
import org.zenith.base.figura.utils.IOUtils;

public final class LocalAvatarLoader {
   private LocalAvatarLoader() {
   }

   public static void loadAvatar(Path path, UserData target) {
      loadAvatar(path, target, AvatarLayer.BODY);
   }

   public static void loadAvatar(Path path, UserData target, AvatarLayer layer) {
      if (path != null && target != null) {
         Path root;
         try {
            root = path.toRealPath();
         } catch (IOException e) {
            FiguraMod.LOGGER.error("Avatar path does not exist: {}", path, e);
            return;
         }

         try {
            CompoundTag nbt = new CompoundTag();
            loadScripts(root, nbt);
            loadSounds(root, nbt);
            Path jsonc = root.resolve("avatar.jsonc");
            Path json = root.resolve("avatar.json");
            String metadataSource = IOUtils.readFile(Files.isRegularFile(jsonc) ? jsonc : json);
            AvatarMetadataParser.Metadata metadata = AvatarMetadataParser.read(metadataSource);
            CompoundTag textures = new CompoundTag();
            ListTag animations = new ListTag();
            CompoundTag models = loadModels(root, root, new BlockbenchParser2(), textures, animations, "", metadata.loadOptions);
            models.putString("name", "models");
            CompoundTag metadataNbt = AvatarMetadataParser.parse(metadata, metadataSource, IOUtils.getFileNameOrEmpty(root));
            metadataNbt.putString("uuid", target.id.toString());
            nbt.put("metadata", metadataNbt);
            AvatarMetadataParser.injectToModels(metadata, models);
            AvatarMetadataParser.injectToTextures(metadata, textures);
            if (!models.isEmpty()) {
               nbt.put("models", models);
            }

            if (!textures.isEmpty()) {
               nbt.put("textures", textures);
            }

            if (!animations.isEmpty()) {
               nbt.put("animations", animations);
            }

            if (metadataNbt.contains("resources_paths")) {
               loadResources(nbt, metadataNbt.getListOrEmpty("resources_paths"), root);
               metadataNbt.remove("resources_paths");
            }

            target.loadAvatar(nbt, layer);
         } catch (Exception e) {
            FiguraMod.LOGGER.error("Failed to parse local avatar from {}", root, e);
         }
      }
   }

   private static void loadSounds(Path root, CompoundTag nbt) throws IOException {
      CompoundTag sounds = new CompoundTag();

      try (Stream<Path> paths = Files.walk(root)) {
         for (Path path : paths.filter(x$0 -> Files.isRegularFile(x$0)).sorted().toList()) {
            String relative = root.relativize(path).toString().replace('\\', '.').replace('/', '.');
            if (relative.toLowerCase(Locale.US).endsWith(".ogg")) {
               sounds.putByteArray(relative.substring(0, relative.length() - 4), Files.readAllBytes(path));
            }
         }
      }

      if (!sounds.isEmpty()) {
         nbt.put("sounds", sounds);
      }
   }

   private static void loadScripts(Path root, CompoundTag nbt) throws IOException {
      CompoundTag scripts = new CompoundTag();

      try (Stream<Path> paths = Files.walk(root)) {
         paths.filter(x$0 -> Files.isRegularFile(x$0))
            .filter(path -> path.getFileName().toString().toLowerCase(Locale.US).endsWith(".lua"))
            .sorted()
            .forEach(path -> {
               try {
                  String relative = root.relativize(path).toString().replace('\\', '.').replace('/', '.');
                  String name = relative.substring(0, relative.length() - 4);
                  scripts.put(name, LuaScriptParser.parseScript(name, IOUtils.readFile(path)));
               } catch (IOException e) {
                  throw new LocalAvatarLoader.AvatarReadException(e);
               }
            });
      } catch (LocalAvatarLoader.AvatarReadException e) {
         throw e.ioException;
      }

      if (!scripts.isEmpty()) {
         nbt.put("scripts", scripts);
      }
   }

   private static CompoundTag loadModels(
      Path avatarRoot, Path current, BlockbenchParser2 parser, CompoundTag textures, ListTag animations, String folders, BlockbenchParser2.LoadOptions options
   ) throws Exception {
      CompoundTag result = new CompoundTag();
      ListTag children = new ListTag();

      List<Path> entries;
      try (Stream<Path> paths = Files.list(current)) {
         entries = paths.sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
      }

      for (Path file : entries) {
         if (!isHidden(file)) {
            String name = IOUtils.getFileNameOrEmpty(file);
            if (Files.isDirectory(file)) {
               CompoundTag folder = loadModels(avatarRoot, file, parser, textures, animations, folders + name + ".", options);
               if (!folder.isEmpty()) {
                  folder.putString("name", name);
                  BlockbenchCommonTypes.parseParent(name, folder);
                  children.add(folder);
               }
            } else if (name.toLowerCase(Locale.US).endsWith(".bbmodel")) {
               ModelParseResult data = parser.parseModel(avatarRoot, file, IOUtils.readFile(file), name.substring(0, name.length() - 8), folders, options);
               children.add(data.modelNbt());
               animations.addAll(data.animationList());
               CompoundTag modelTextures = data.textures();
               if (!modelTextures.isEmpty()) {
                  if (textures.isEmpty()) {
                     textures.put("data", new ListTag());
                     textures.put("src", new CompoundTag());
                  }

                  textures.getListOrEmpty("data").addAll(modelTextures.getListOrEmpty("data"));
                  textures.getCompoundOrEmpty("src").merge(modelTextures.getCompoundOrEmpty("src"));
               }
            }
         }
      }

      if (!children.isEmpty()) {
         result.put("chld", children);
      }

      return result;
   }

   private static void loadResources(CompoundTag nbt, ListTag patterns, Path root) throws IOException {
      FileSystem fileSystem = FileSystems.getDefault();
      List<PathMatcher> matchers = new ArrayList<>();

      for (int i = 0; i < patterns.size(); i++) {
         matchers.add(fileSystem.getPathMatcher("glob:" + patterns.getStringOr(i, "")));
      }

      CompoundTag resources = new CompoundTag();

      try (Stream<Path> paths = Files.walk(root)) {
         for (Path file : paths.filter(x$0 -> Files.isRegularFile(x$0)).toList()) {
            Path relative = root.relativize(file).normalize();
            if (!matchers.stream().noneMatch(matcher -> matcher.matches(relative))) {
               try (
                  InputStream input = Files.newInputStream(file);
                  ByteArrayOutputStream output = new ByteArrayOutputStream();
                  GZIPOutputStream gzip = new GZIPOutputStream(output);
               ) {
                  input.transferTo(gzip);
                  gzip.finish();
                  resources.put(relative.toString().replace('\\', '/'), new ByteArrayTag(output.toByteArray()));
               }
            }
         }
      }

      nbt.put("resources", resources);
   }

   private static boolean isHidden(Path path) {
      try {
         return Files.isHidden(path) || path.getFileName().toString().startsWith(".");
      } catch (IOException e) {
         return false;
      }
   }

   private static final class AvatarReadException extends RuntimeException {
      private final IOException ioException;

      private AvatarReadException(IOException ioException) {
         super(ioException);
         this.ioException = ioException;
      }
   }
}
