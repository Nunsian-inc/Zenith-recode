package org.zenith.base.figura.parsers;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapterFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.figura.math.vector.FiguraVec3;

public class BlockbenchV4Model extends BlockbenchCommonTypes.ModelFormat {
   private static final FiguraVec3 ZERO = FiguraVec3.of(0.0, 0.0, 0.0);
   BlockbenchCommonTypes.IntPair resolution;
   List<BlockbenchCommonTypes.Element> elements;
   List<BlockbenchV4Model.OutlinerItem> outliner;
   List<BlockbenchCommonTypes.Texture> textures;
   List<BlockbenchCommonTypes.Animation> animations;
   List<BlockbenchCommonTypes.Collection> collections;
   public static final IllegalStateException WRONG_FORMAT = new IllegalStateException("Tried to execute the v4 parser on a model file of a different version");
   public static final JsonDeserializer<BlockbenchV4Model> MODEL_DESERIALIZER = (json, typeOfT, context) -> {
      JsonObject obj = json.getAsJsonObject();
      BlockbenchV4Model instance = new BlockbenchV4Model();
      JsonObject meta = obj.getAsJsonObject("meta");
      instance.formatVersion = meta.get("format_version").getAsString();
      instance.modelFormat = meta.get("model_format").getAsString();
      if (!instance.formatVersion.startsWith("4.")) {
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

      instance.outliner = new ArrayList<>();
      if (obj.has("outliner")) {
         for (JsonElement item : obj.getAsJsonArray("outliner")) {
            instance.outliner.add((BlockbenchV4Model.OutlinerItem)context.deserialize(item, BlockbenchV4Model.OutlinerItem.class));
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

   @Override
   public Map<String, BlockbenchCommonTypes.UUIDReferable> getAllReferences() {
      Map<String, BlockbenchCommonTypes.UUIDReferable> refs = new HashMap<>();

      for (BlockbenchCommonTypes.Element element : this.elements) {
         refs.put(element.uuid, element);
      }

      for (BlockbenchV4Model.OutlinerItem item : this.outliner) {
         item.fillContainedRefs(refs);
      }

      return refs;
   }

   @Override
   public CompoundTag convert(BlockbenchParser2.Intermediary target) {
      target.defaultRes = this.resolution;
      target.loadTextures(this.textures);

      for (BlockbenchCommonTypes.Element element : this.elements) {
         target.elements.put(element.uuid, element);
      }

      target.referents.putAll(this.getAllReferences());
      target.loadAnimations(this.animations);
      target.loadCollections(this.collections);
      CompoundTag tag = new CompoundTag();
      tag.putString("name", target.name);
      BlockbenchCommonTypes.parseParent(target.name, tag);
      ListTag chld = new ListTag();

      for (BlockbenchV4Model.OutlinerItem item : this.outliner) {
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
      return tag;
   }

   public abstract static class OutlinerItem implements BlockbenchCommonTypes.NBTRepresentation<CompoundTag> {
      public static final TypeAdapterFactory ADAPTER_FACTORY = new GsonTypeByField<>(
            BlockbenchV4Model.OutlinerItem.class, e -> e.isJsonObject() ? "group" : "element"
         )
         .bind("group", BlockbenchV4Model.OutlinerItem.Group.class)
         .bind("element", BlockbenchV4Model.OutlinerItem.Element.class);
      final String uuid;

      @Nullable
      public abstract CompoundTag toNBT(BlockbenchParser2.Intermediary var1);

      public OutlinerItem(String uuid) {
         this.uuid = uuid;
      }

      public abstract void fillContainedRefs(Map<String, BlockbenchCommonTypes.UUIDReferable> var1);

      public static final class Element extends BlockbenchV4Model.OutlinerItem {
         public static final JsonDeserializer<BlockbenchV4Model.OutlinerItem.Element> DESERIALIZER = (json, typeOfT, context) -> new BlockbenchV4Model.OutlinerItem.Element(
            json.getAsString()
         );

         public Element(String uuid) {
            super(uuid);
         }

         @Override
         public void fillContainedRefs(Map<String, BlockbenchCommonTypes.UUIDReferable> to) {
         }

         @Nullable
         @Override
         public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
            BlockbenchCommonTypes.Element ref = context.elements.get(this.uuid);
            return ref == null ? null : ref.toNBT(context);
         }
      }

      public static final class Group extends BlockbenchV4Model.OutlinerItem implements BlockbenchCommonTypes.UUIDReferable {
         @Nullable
         final List<BlockbenchV4Model.OutlinerItem> children = new ArrayList<>();
         String name;
         @Nullable
         Boolean visibility;
         @Nullable
         Boolean export;
         FiguraVec3 origin;
         FiguraVec3 rotation;

         public Group(String uuid) {
            super(uuid);
         }

         @Override
         public void fillContainedRefs(Map<String, BlockbenchCommonTypes.UUIDReferable> to) {
            to.put(this.uuid, this);
            if (this.children != null) {
               this.children.forEach(it -> it.fillContainedRefs(to));
            }
         }

         @Override
         public String getUUID() {
            return this.uuid;
         }

         @Nullable
         @Override
         public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
            if (Boolean.FALSE.equals(this.export)) {
               return null;
            }

            CompoundTag tag = new CompoundTag();
            tag.putString("name", this.name);
            if (Boolean.FALSE.equals(this.visibility)) {
               tag.putBoolean("vsb", false);
            }

            if (this.origin != null && !this.origin.equals(BlockbenchV4Model.ZERO)) {
               tag.put("piv", BlockbenchCommonTypes.vecToList(this.origin));
            }

            if (this.rotation != null && !this.rotation.equals(BlockbenchV4Model.ZERO)) {
               tag.put("rot", BlockbenchCommonTypes.vecToList(this.rotation));
            }

            BlockbenchCommonTypes.parseParent(this.name, tag);
            if (this.children != null) {
               ListTag chld = new ListTag();

               for (BlockbenchV4Model.OutlinerItem child : this.children) {
                  CompoundTag childTag = child.toNBT(context);
                  if (childTag != null) {
                     if (childTag.contains("vsb") && Objects.equals(this.visibility, childTag.getBooleanOr("vsb", false))) {
                        childTag.remove("vsb");
                     }

                     chld.add(childTag);
                  }
               }

               if (!chld.isEmpty()) {
                  tag.put("chld", chld);
               }
            }

            Set<BlockbenchParser2.Intermediary.AnimationRepresentation> animations = context.animationsByElement.get(this.uuid);
            if (animations != null) {
               ListTag anim = new ListTag();

               for (BlockbenchParser2.Intermediary.AnimationRepresentation animation : animations) {
                  BlockbenchCommonTypes.Animator animator = animation.partAnimators.get(this.uuid);
                  if (animator == null) {
                     throw new RuntimeException("inconsistent state!! animationsByElement indicated an animator, but none actually present");
                  }

                  CompoundTag attachment = animator.getNBT(animation, false);
                  if (attachment != null) {
                     anim.add(attachment);
                  }
               }

               tag.put("anim", anim);
            }

            BlockbenchCommonTypes.attachCollections(context, this.uuid, tag);
            return tag;
         }
      }
   }
}
