package org.zenith.base.figura.parsers;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.utils.IOUtils;

public class BlockbenchParser2 {
   private static final Gson GSON = BlockbenchCommonTypes.getGson();
   private int nextTexture = 0;
   private int nextAnimation = 0;

   public ModelParseResult parseModel(
      Path avatarRoot, Path modelPath, String json, String modelName, String locatedWithin, @Nullable BlockbenchParser2.LoadOptions options
   ) throws Exception {
      try {
         BlockbenchCommonTypes.ModelFormat model = (BlockbenchCommonTypes.ModelFormat)GSON.fromJson(json, BlockbenchCommonTypes.ModelFormat.class);
         boolean validFormat = model.modelFormat.equals("free") || model.modelFormat.contains("figura");
         if (!validFormat) {
            throw new Exception(
               String.format(
                  "Model \"%s\" has an incompatible model format: \"%s\".\nCompatibility is limited to the \"Generic Model\" format and third-party %s-specific formats.",
                  modelName,
                  model.modelFormat,
                  "Figura"
               )
            );
         }

         BlockbenchParser2.Intermediary container = new BlockbenchParser2.Intermediary(this, options == null ? BlockbenchParser2.LoadOptions.DEFAULT : options);
         container.name = modelName;
         container.avatarRoot = avatarRoot;
         container.modelPath = modelPath;
         container.locatedWithin = locatedWithin;
         CompoundTag nbt = model.convert(container);
         return new ModelParseResult(container.getTexturesNBT(), container.getAnimationsNBT(), nbt);
      } catch (Exception var11) {
         throw new Exception(String.format("model \"%s\": %s", modelName, var11.getMessage()), var11);
      }
   }

   public static class Intermediary {
      private final BlockbenchParser2 parser;
      public final BlockbenchParser2.LoadOptions options;
      public String name;
      public Path modelPath;
      public Path avatarRoot;
      public String locatedWithin;
      public BlockbenchCommonTypes.IntPair defaultRes;
      private final List<BlockbenchParser2.Intermediary.TextureRepresentation> textures = new ArrayList<>();
      private final HashMap<String, Map<String, BlockbenchParser2.Intermediary.TextureRepresentation>> textureNames = new HashMap<>();
      public final Map<String, BlockbenchCommonTypes.Element> elements = new HashMap<>();
      public final Map<String, BlockbenchCommonTypes.UUIDReferable> referents = new HashMap<>();
      public final List<BlockbenchParser2.Intermediary.AnimationRepresentation> animations = new ArrayList<>();
      public final Map<String, Set<BlockbenchParser2.Intermediary.AnimationRepresentation>> animationsByElement = new HashMap<>();
      public final List<BlockbenchParser2.Intermediary.CollectionRepresentation> collections = new ArrayList<>();
      public final Multimap<String, Integer> collectionsByElement = MultimapBuilder.hashKeys().hashSetValues().build();

      public Intermediary(BlockbenchParser2 parser, BlockbenchParser2.LoadOptions options) {
         this.parser = parser;
         this.options = options;
      }

      public void loadTextures(List<BlockbenchCommonTypes.Texture> textures) {
         int i = 0;

         for (BlockbenchCommonTypes.Texture texture : textures) {
            BlockbenchParser2.Intermediary.TextureRepresentation texRep = new BlockbenchParser2.Intermediary.TextureRepresentation(texture);
            texRep.localID = i++;
            this.textureNames.compute(texRep.name, (k, v) -> {
               if (v == null) {
                  texRep.globalID = this.parser.nextTexture++;
                  v = new HashMap<>();
               } else {
                  texRep.globalID = v.values().iterator().next().globalID;
               }

               v.put(texRep.textureType, texRep);
               return v;
            });
            this.textures.add(texRep);
         }
      }

      public void loadAnimations(List<BlockbenchCommonTypes.Animation> animations) {
         for (BlockbenchCommonTypes.Animation animation : animations) {
            BlockbenchParser2.Intermediary.AnimationRepresentation animRep = new BlockbenchParser2.Intermediary.AnimationRepresentation(
               this.parser.nextAnimation++, animation
            );
            this.animations.add(animRep);
         }
      }

      public void loadCollections(List<BlockbenchCommonTypes.Collection> collections) {
         int i = 0;

         for (BlockbenchCommonTypes.Collection collection : collections) {
            BlockbenchParser2.Intermediary.CollectionRepresentation coll = new BlockbenchParser2.Intermediary.CollectionRepresentation(i++, collection);
            this.collections.add(coll);
         }
      }

      @Nullable
      public Integer getTextureGlobalID(int localID) {
         return localID >= 0 && this.textures.size() > localID ? this.textures.get(localID).globalID : null;
      }

      public FiguraVec2 getTextureFixedSize(int localID) {
         return this.textures.get(localID).fixedSize;
      }

      public CompoundTag getTexturesNBT() {
         CompoundTag tag = new CompoundTag();
         CompoundTag sources = new CompoundTag();
         LinkedHashMap<String, CompoundTag> buildData = new LinkedHashMap<>();

         for (BlockbenchParser2.Intermediary.TextureRepresentation texture : this.textures) {
            sources.put(texture.path, texture.getSourceNBT());
            buildData.compute(texture.name, (k, it) -> {
               if (it == null) {
                  it = new CompoundTag();
               }

               if (it.contains(texture.textureType)) {
                  throw new RuntimeException("Model \"" + this.name + "\" contains texture with duplicate name \"" + texture.name + "\"");
               }

               it.putString(texture.textureType, texture.path);
               return (CompoundTag)it;
            });
         }

         ListTag data = new ListTag();
         data.addAll(buildData.values());
         tag.put("src", sources);
         tag.put("data", data);
         return tag;
      }

      public List<CompoundTag> getAnimationsNBT() {
         List<CompoundTag> list = new ArrayList<>();

         for (BlockbenchParser2.Intermediary.AnimationRepresentation animation : this.animations) {
            list.add(animation.getAnimNBT());
         }

         return list;
      }

      public class AnimationRepresentation {
         public final int globalID;
         final String mdl;
         final String name;
         final String loop;
         final boolean override;
         final float length;
         final float offset;
         final float blend;
         final float startDelay;
         final float loopDelay;
         public final Map<String, BlockbenchCommonTypes.Animator> partAnimators;
         @Nullable
         public final BlockbenchCommonTypes.Animator fxAnimator;

         public AnimationRepresentation(int globalID, BlockbenchCommonTypes.Animation animation) {
            this.mdl = Intermediary.this.locatedWithin.isBlank() ? Intermediary.this.name : Intermediary.this.locatedWithin + Intermediary.this.name;
            this.globalID = globalID;
            this.name = animation.name;
            this.loop = animation.loop == null ? "once" : animation.loop;
            this.override = Boolean.TRUE.equals(animation.override);
            this.length = animation.length;
            this.offset = BlockbenchCommonTypes.parseFloatOr(animation.anim_time_update, 0.0F);
            this.blend = BlockbenchCommonTypes.parseFloatOr(animation.blend_weight, 1.0F);
            this.startDelay = BlockbenchCommonTypes.parseFloatOr(animation.start_delay, 0.0F);
            this.loopDelay = BlockbenchCommonTypes.parseFloatOr(animation.loop_delay, 0.0F);
            if (animation.animators != null) {
               Map<String, BlockbenchCommonTypes.Animator> filtered = new HashMap<>(animation.animators);
               this.fxAnimator = filtered.remove("effects");
               this.partAnimators = filtered;
            } else {
               this.fxAnimator = null;
               this.partAnimators = new HashMap<>();
            }

            for (Entry<String, BlockbenchCommonTypes.Animator> entry : this.partAnimators.entrySet()) {
               Intermediary.this.animationsByElement.compute(entry.getKey(), (k, v) -> {
                  if (v == null) {
                     v = new LinkedHashSet<>();
                  }

                  v.add(this);
                  return v;
               });
            }
         }

         public CompoundTag getAnimNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("mdl", this.mdl);
            tag.putString("name", this.name);
            if (!this.loop.equals("once")) {
               tag.putString("loop", this.loop);
            }

            if (this.override) {
               tag.putBoolean("ovr", true);
            }

            if (this.length != 0.0F) {
               tag.putFloat("len", this.length);
            }

            if (this.offset != 0.0F) {
               tag.putFloat("off", this.offset);
            }

            if (this.blend != 1.0F) {
               tag.putFloat("bld", this.blend);
            }

            if (this.startDelay != 0.0F) {
               tag.putFloat("sdel", this.startDelay);
            }

            if (this.loopDelay != 0.0F) {
               tag.putFloat("ldel", this.loopDelay);
            }

            ListTag code = this.getEffectsNBT();
            if (code != null) {
               tag.put("code", code);
            }

            return tag;
         }

         @Nullable
         private ListTag getEffectsNBT() {
            if (this.fxAnimator == null) {
               return null;
            }

            ListTag fxData = new ListTag();
            if (this.fxAnimator.keyframes != null) {
               for (BlockbenchCommonTypes.Keyframe keyframe : this.fxAnimator.keyframes) {
                  if (keyframe.channel.equalsIgnoreCase("timeline") && keyframe instanceof BlockbenchCommonTypes.Keyframe.InstructionKeyframe ik) {
                     CompoundTag kfTag = new CompoundTag();
                     kfTag.putFloat("time", ik.time);
                     kfTag.putString("src", ik.data_points[0].script);
                     fxData.add(kfTag);
                  }
               }
            }

            return fxData;
         }
      }

      public class CollectionRepresentation {
         public final String name;
         public final int localID;

         public CollectionRepresentation(int localID, BlockbenchCommonTypes.Collection source) {
            this.name = source.name;
            this.localID = localID;
            source.children.forEach(it -> Intermediary.this.collectionsByElement.put(it, localID));
         }
      }

      public class TextureRepresentation {
         public String name;
         public int globalID;
         public int localID;
         public final String textureType;
         public String path;
         public byte[] source;
         public final FiguraVec2 fixedSize;

         public TextureRepresentation(BlockbenchCommonTypes.Texture texture) {
            this.name = texture.name;
            if (this.name.endsWith(".png")) {
               this.name = this.name.substring(0, this.name.length() - 4);
            }

            if (this.name.endsWith("_e")) {
               this.textureType = "e";
            } else if (this.name.endsWith("_n")) {
               this.textureType = "n";
            } else if (this.name.endsWith("_s")) {
               this.textureType = "s";
            } else {
               this.textureType = "d";
            }

            try {
               if (texture.relative_path == null) {
                  throw new RuntimeException("load from source");
               }

               Path p = Intermediary.this.modelPath.getParent().resolve(texture.relative_path);
               if (p.getFileSystem() == FileSystems.getDefault()) {
                  p = p.toFile().getCanonicalFile().toPath();
               }

               p = p.normalize();
               if ((!Files.exists(p) || Intermediary.this.avatarRoot.getNameCount() > 1 && !p.startsWith(Intermediary.this.avatarRoot))
                  && texture.relative_path.startsWith("../")) {
                  p = Intermediary.this.modelPath.resolve(texture.relative_path);
                  if (p.getFileSystem() == FileSystems.getDefault()) {
                     p = p.toFile().getCanonicalFile().toPath();
                  }

                  p = p.normalize();
               }

               if (!Files.exists(p)) {
                  throw new FileNotFoundException("Could not locate texture '" + texture.name + "'");
               }

               if (Intermediary.this.avatarRoot.getNameCount() > 1 && !p.startsWith(Intermediary.this.avatarRoot)
                  || p.getFileSystem() != Intermediary.this.avatarRoot.getFileSystem()) {
                  throw new IllegalStateException("Texture '" + texture.name + "' is a reference outside the avatar folder");
               }

               FiguraMod.debug("Loading texture {}: path is {}", texture.name, p.toString());
               this.source = IOUtils.readFileBytes(p);
               this.path = Intermediary.this.avatarRoot.relativize(p).toString().replace(p.getFileSystem().getSeparator(), ".");
               this.path = this.path.substring(0, this.path.length() - 4);
               this.name = Intermediary.this.locatedWithin + this.name;
               FiguraMod.debug("Loaded {} texture \"{}\" as path {} (from {})", this.textureType, this.name, this.path, p);
            } catch (Exception var4) {
               if (var4 instanceof IOException || var4 instanceof NullPointerException) {
                  String var4x = "";
               }

               if (texture.source == null || !texture.source.startsWith("data:image/png;base64,")) {
                  throw new IllegalStateException(
                     String.format("Failed to find the %s texture: %s\n(and the bundled texture data was missing or bad)", texture.name, var4)
                  );
               }

               this.source = Base64.getDecoder().decode(texture.source.substring("data:image/png;base64,".length()));
               this.path = Intermediary.this.locatedWithin + Intermediary.this.name + "." + this.name;
               FiguraMod.debug("Loaded {} texture \"{}\" as path {} (bundled)", this.textureType, this.name, this.path);
            }

            if (!this.textureType.equals("d")) {
               this.name = this.name.substring(0, this.name.length() - 2);
            }

            if (texture.width != null) {
               this.fixedSize = FiguraVec2.of(texture.width / texture.uv_width, texture.height / texture.uv_height);
            } else {
               BlockbenchCommonTypes.IntPair imageSize = BlockbenchCommonTypes.getPNGDimensions(this.source);
               this.fixedSize = FiguraVec2.of((float)imageSize.x / Intermediary.this.defaultRes.x, (float)imageSize.y / Intermediary.this.defaultRes.y);
            }
         }

         public ByteArrayTag getSourceNBT() {
            return new ByteArrayTag(this.source);
         }
      }
   }

   public static class LoadOptions {
      public static final BlockbenchParser2.LoadOptions DEFAULT = new BlockbenchParser2.LoadOptions();
      public boolean partsWithUUIDs;
   }
}
