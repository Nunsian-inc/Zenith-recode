package org.zenith.base.figura.parsers;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.math.vector.FiguraVec3;

public class BlockbenchV5Model extends BlockbenchCommonTypes.ModelFormat {
   private static final FiguraVec3 ZERO = FiguraVec3.of(0.0, 0.0, 0.0);
   BlockbenchCommonTypes.IntPair resolution;
   List<BlockbenchCommonTypes.Element> elements;
   List<BlockbenchV5Model.Group> groups;
   List<BlockbenchV5Model.OutlinerItem> outliner;
   List<BlockbenchCommonTypes.Texture> textures;
   List<BlockbenchCommonTypes.Animation> animations;
   List<BlockbenchCommonTypes.Collection> collections;
   public static final IllegalStateException WRONG_FORMAT = new IllegalStateException("Tried to execute the v5 parser on a model file of a different version");
   public static final JsonDeserializer<BlockbenchV5Model> MODEL_DESERIALIZER = (json, typeOfT, context) -> {
      JsonObject obj = json.getAsJsonObject();
      BlockbenchV5Model instance = new BlockbenchV5Model();
      JsonObject meta = obj.getAsJsonObject("meta");
      instance.formatVersion = meta.get("format_version").getAsString();
      instance.modelFormat = meta.get("model_format").getAsString();
      if (!instance.formatVersion.startsWith("5.")) {
         throw WRONG_FORMAT;
      }

      JsonObject resolution = obj.getAsJsonObject("resolution");
      instance.resolution = new BlockbenchCommonTypes.IntPair(resolution.get("width").getAsInt(), resolution.get("height").getAsInt());
      instance.elements = new ArrayList<>();
      if (obj.has("elements")) {
         for (JsonElement item : obj.getAsJsonArray("elements")) {
            instance.elements.add((BlockbenchCommonTypes.Element)context.deserialize(item, BlockbenchCommonTypes.Element.class));
         }
      }

      instance.groups = new ArrayList<>();
      if (obj.has("groups")) {
         for (JsonElement item : obj.getAsJsonArray("groups")) {
            instance.groups.add((BlockbenchV5Model.Group)context.deserialize(item, BlockbenchV5Model.Group.class));
         }
      }

      instance.outliner = new ArrayList<>();
      if (obj.has("outliner")) {
         for (JsonElement item : obj.getAsJsonArray("outliner")) {
            instance.outliner.add((BlockbenchV5Model.OutlinerItem)context.deserialize(item, BlockbenchV5Model.OutlinerItem.class));
         }
      }

      instance.textures = new ArrayList<>();
      if (obj.has("textures")) {
         for (JsonElement item : obj.getAsJsonArray("textures")) {
            instance.textures.add((BlockbenchCommonTypes.Texture)context.deserialize(item, BlockbenchCommonTypes.Texture.class));
         }
      }

      instance.animations = new ArrayList<>();
      if (obj.has("animations")) {
         for (JsonElement item : obj.getAsJsonArray("animations")) {
            instance.animations.add((BlockbenchCommonTypes.Animation)context.deserialize(item, BlockbenchCommonTypes.Animation.class));
         }
      }

      instance.collections = new ArrayList<>();
      if (obj.has("collections")) {
         for (JsonElement item : obj.getAsJsonArray("collections")) {
            instance.collections.add((BlockbenchCommonTypes.Collection)context.deserialize(item, BlockbenchCommonTypes.Collection.class));
         }
      }

      return instance;
   };
   public static final Pattern DEFINITELY_STMT = Pattern.compile("^\\s*([:;]|break|goto|do|while|repeat|if|for|function\\s*[^\\s(]|local)");
   public static final Pattern RETURN_BOUNDARY = Pattern.compile("\\breturn\\b");

   @Override
   public Map<String, BlockbenchCommonTypes.UUIDReferable> getAllReferences() {
      Map<String, BlockbenchCommonTypes.UUIDReferable> refs = new HashMap<>();

      for (BlockbenchCommonTypes.Element element : this.elements) {
         refs.put(element.uuid, element);
      }

      for (BlockbenchV5Model.Group group : this.groups) {
         refs.put(group.uuid, group);
      }

      return refs;
   }

   public static String negateLua(String source) {
      return !RETURN_BOUNDARY.matcher(source).find() && !DEFINITELY_STMT.matcher(source).find() ? String.format("-(%s)", source) : source;
   }

   @Override
   public CompoundTag convert(BlockbenchParser2.Intermediary target) {
      target.defaultRes = this.resolution;
      target.loadTextures(this.textures);
      target.loadAnimations(this.animations);

      for (BlockbenchCommonTypes.Element element : this.elements) {
         target.elements.put(element.uuid, element);
      }

      target.referents.putAll(this.getAllReferences());
      target.loadCollections(this.collections);
      CompoundTag tag = new CompoundTag();
      tag.putString("name", target.name);
      BlockbenchCommonTypes.parseParent(target.name, tag);
      ListTag chld = new ListTag();

      for (BlockbenchV5Model.OutlinerItem item : this.outliner) {
         CompoundTag itemTag = item.toNBT(target);
         if (itemTag != null) {
            chld.add(itemTag);
         }
      }

      tag.put("chld", chld);
      ListTag cn = new ListTag();

      for (BlockbenchParser2.Intermediary.CollectionRepresentation collRep : target.collections) {
         cn.add(StringTag.valueOf(collRep.name));
      }

      tag.put("cn", cn);
      tag.put("_v", ByteTag.valueOf((byte)2));
      return tag;
   }

   public static class Group implements BlockbenchCommonTypes.UUIDReferable {
      String name;
      String uuid;
      @Nullable
      Boolean visibility;
      @Nullable
      Boolean export;
      FiguraVec3 origin;
      FiguraVec3 rotation;

      @Override
      public String getUUID() {
         return this.uuid;
      }
   }

   public abstract static class OutlinerItem implements BlockbenchCommonTypes.NBTRepresentation<CompoundTag> {
      public static final JsonDeserializer<BlockbenchV5Model.OutlinerItem> DESERIALIZER = (json, typeOfT, context) -> {
         if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return new BlockbenchV5Model.OutlinerItem.Element(json.getAsString());
         }

         if (!json.isJsonObject()) {
            throw new JsonParseException("Expecting an outliner item (string or object), but found " + json.getClass().getSimpleName() + " instead");
         }

         JsonObject theObject = json.getAsJsonObject();
         BlockbenchV5Model.OutlinerItem.Container instance = new BlockbenchV5Model.OutlinerItem.Container(theObject.get("uuid").getAsString());

         for (JsonElement child : theObject.getAsJsonArray("children")) {
            instance.children.add((BlockbenchV5Model.OutlinerItem)context.deserialize(child, BlockbenchV5Model.OutlinerItem.class));
         }

         return instance;
      };
      final String uuid;

      @Nullable
      public abstract CompoundTag toNBT(BlockbenchParser2.Intermediary var1);

      public OutlinerItem(String uuid) {
         this.uuid = uuid;
      }

      public static final class Container extends BlockbenchV5Model.OutlinerItem {
         final List<BlockbenchV5Model.OutlinerItem> children = new ArrayList<>();

         public Container(String uuid) {
            super(uuid);
         }

         @Nullable
         @Override
         public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
            BlockbenchCommonTypes.UUIDReferable groupProbably = context.referents.get(this.uuid);
            if (!(groupProbably instanceof BlockbenchV5Model.Group group)) {
               FiguraMod.LOGGER
                  .warn(
                     "Broken reference (in model '{}'): expected a group at UUID {} but found {} instead",
                     new Object[]{context.name, this.uuid, groupProbably == null ? "(nothing with that UUID!)" : groupProbably.getClass().getSimpleName()}
                  );
               return null;
            } else {
               if (Boolean.FALSE.equals(group.export)) {
                  return null;
               }

               CompoundTag tag = new CompoundTag();
               tag.putString("name", group.name);
               if (Boolean.FALSE.equals(group.visibility)) {
                  tag.putBoolean("vsb", false);
               }

               if (group.origin != null && !group.origin.equals(BlockbenchV5Model.ZERO)) {
                  tag.put("piv", BlockbenchCommonTypes.vecToList(group.origin));
               }

               if (group.rotation != null && !group.rotation.equals(BlockbenchV5Model.ZERO)) {
                  tag.put("rot", BlockbenchCommonTypes.vecToList(group.rotation));
               }

               BlockbenchCommonTypes.parseParent(group.name, tag);
               ListTag chld = new ListTag();

               for (BlockbenchV5Model.OutlinerItem child : this.children) {
                  CompoundTag childTag = child.toNBT(context);
                  if (childTag != null) {
                     if (childTag.contains("vsb") && Objects.equals(group.visibility, childTag.getBooleanOr("vsb", false))) {
                        childTag.remove("vsb");
                     }

                     chld.add(childTag);
                  }
               }

               if (!chld.isEmpty()) {
                  tag.put("chld", chld);
               }

               Set<BlockbenchParser2.Intermediary.AnimationRepresentation> animations = context.animationsByElement.get(this.uuid);
               if (animations != null) {
                  ListTag anim = new ListTag();

                  for (BlockbenchParser2.Intermediary.AnimationRepresentation animation : animations) {
                     BlockbenchCommonTypes.Animator animator = animation.partAnimators.get(this.uuid);
                     if (animator == null) {
                        throw new RuntimeException("inconsistent state!! animationsByElement indicated an animator, but none actually present");
                     }

                     CompoundTag attachment = animator.getNBT(animation, true);
                     if (attachment != null) {
                        anim.add(attachment);
                     }
                  }

                  tag.put("anim", anim);
               }

               BlockbenchCommonTypes.attachCollections(context, group.uuid, tag);
               return tag;
            }
         }
      }

      public static final class Element extends BlockbenchV5Model.OutlinerItem {
         public Element(String uuid) {
            super(uuid);
         }

         @Override
         public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
            return context.elements.get(this.uuid).toNBT(context);
         }
      }
   }
}
