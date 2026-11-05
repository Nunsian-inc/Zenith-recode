package org.zenith.base.figura.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapterFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.math.vector.FiguraVec4;
import org.zenith.base.figura.model.ParentType;

public class BlockbenchCommonTypes {
   public static final byte FORMATLESS = 0;
   public static final byte FORMAT_V4 = 1;
   public static final byte FORMAT_V5 = 2;
   private static final FiguraVec3 ZERO = FiguraVec3.of(0.0, 0.0, 0.0);
   private static final FiguraVec4 ZERO4 = FiguraVec4.of(0.0, 0.0, 0.0, 0.0);
   public static final JsonDeserializer<FiguraVec4> VEC4_DESERIALIZER = (json, typeOfT, context) -> {
      JsonArray four = json.getAsJsonArray();
      if (four.size() != 4) {
         throw new JsonParseException("Trying to parse a Vector4, but the list has " + four.size() + " items (expected 4)");
      } else {
         return FiguraVec4.of(floatOrZero(four.get(0)), floatOrZero(four.get(1)), floatOrZero(four.get(2)), floatOrZero(four.get(3)));
      }
   };
   public static final JsonDeserializer<FiguraVec3> VEC3_DESERIALIZER = (json, typeOfT, context) -> {
      JsonArray three = json.getAsJsonArray();
      if (three.size() != 3) {
         throw new JsonParseException("Trying to parse a Vector3, but the list has " + three.size() + " items (expected 3)");
      } else {
         return FiguraVec3.of(floatOrZero(three.get(0)), floatOrZero(three.get(1)), floatOrZero(three.get(2)));
      }
   };
   public static final JsonDeserializer<FiguraVec2> VEC2_DESERIALIZER = (json, typeOfT, context) -> {
      JsonArray two = json.getAsJsonArray();
      if (two.size() != 2) {
         throw new JsonParseException("Trying to parse a Vector2, but the list has " + two.size() + " items (expected 2)");
      } else {
         return FiguraVec2.of(floatOrZero(two.get(0)), floatOrZero(two.get(1)));
      }
   };

   public static Gson getGson() {
      return new GsonBuilder()
         .registerTypeAdapter(BlockbenchV5Model.class, BlockbenchV5Model.MODEL_DESERIALIZER)
         .registerTypeAdapter(BlockbenchV4Model.class, BlockbenchV4Model.MODEL_DESERIALIZER)
         .registerTypeAdapter(FiguraVec4.class, VEC4_DESERIALIZER)
         .registerTypeAdapter(FiguraVec3.class, VEC3_DESERIALIZER)
         .registerTypeAdapter(FiguraVec2.class, VEC2_DESERIALIZER)
         .registerTypeAdapter(BlockbenchV5Model.OutlinerItem.class, BlockbenchV5Model.OutlinerItem.DESERIALIZER)
         .registerTypeAdapter(BlockbenchV4Model.OutlinerItem.Element.class, BlockbenchV4Model.OutlinerItem.Element.DESERIALIZER)
         .registerTypeAdapterFactory(BlockbenchCommonTypes.ModelFormat.ADAPTER_FACTORY)
         .registerTypeAdapterFactory(BlockbenchCommonTypes.Element.ADAPTER_FACTORY)
         .registerTypeAdapterFactory(BlockbenchCommonTypes.Keyframe.ADAPTER_FACTORY)
         .registerTypeAdapterFactory(BlockbenchV4Model.OutlinerItem.ADAPTER_FACTORY)
         .create();
   }

   public static void parseParent(String name, CompoundTag nbt) {
      ParentType parentType = ParentType.get(name);
      if (parentType != ParentType.None) {
         nbt.putString("pt", parentType.name());
      }
   }

   public static BlockbenchCommonTypes.IntPair getPNGDimensions(byte[] png) {
      int w = png[16] & 255;
      w = (w << 8) + (png[17] & 255);
      w = (w << 8) + (png[18] & 255);
      w = (w << 8) + (png[19] & 255);
      int h = png[20] & 255;
      h = (h << 8) + (png[21] & 255);
      h = (h << 8) + (png[22] & 255);
      h = (h << 8) + (png[23] & 255);
      return new BlockbenchCommonTypes.IntPair(w, h);
   }

   public static void attachCollections(BlockbenchParser2.Intermediary context, String uuid, CompoundTag target) {
      List<Integer> collections = new ArrayList<>(context.collectionsByElement.get(uuid));
      if (!collections.isEmpty()) {
         byte[] prs = new byte[collections.size()];

         for (int i = 0; i < collections.size(); i++) {
            prs[i] = collections.get(i).byteValue();
         }

         target.put("pr", new ByteArrayTag(prs));
      }
   }

   private static float floatOrZero(JsonElement it) {
      if (!it.isJsonPrimitive()) {
         return 0.0F;
      }

      JsonPrimitive prim = it.getAsJsonPrimitive();
      return !prim.isNumber() ? 0.0F : prim.getAsFloat();
   }

   public static ListTag floatArrToList(float[] fa) {
      int type = 0;

      for (float c : fa) {
         if (c % 1.0F != 0.0F) {
            type = 2;
            break;
         }

         if (c < -127.0F || c >= 128.0F) {
            type = 1;
         }

         if (c < -16383.0F || c >= 16384.0F) {
            type = 2;
            break;
         }
      }

      ListTag tag = new ListTag();

      for (float c : fa) {
         switch (type) {
            case 0:
               tag.add(ByteTag.valueOf((byte)c));
               break;
            case 1:
               tag.add(ShortTag.valueOf((short)c));
               break;
            case 2:
               tag.add(FloatTag.valueOf(c));
         }
      }

      return tag;
   }

   public static ListTag vecToList(FiguraVec3 vector) {
      return floatArrToList(new float[]{(float)vector.x, (float)vector.y, (float)vector.z});
   }

   public static ListTag vecToList(FiguraVec4 vector) {
      return floatArrToList(new float[]{(float)vector.x, (float)vector.y, (float)vector.z, (float)vector.w});
   }

   public static float parseFloatOr(@Nullable String floatLike, float fallback) {
      if (floatLike != null && !floatLike.isBlank()) {
         try {
            return Float.parseFloat(floatLike);
         } catch (NumberFormatException var3) {
            return fallback;
         }
      } else {
         return fallback;
      }
   }

   public static class Animation {
      String name;
      @Nullable
      String loop;
      @Nullable
      Boolean override;
      float length;
      @Nullable
      String anim_time_update;
      @Nullable
      String blend_weight;
      @Nullable
      String start_delay;
      @Nullable
      String loop_delay;
      @Nullable
      Map<String, BlockbenchCommonTypes.Animator> animators;
   }

   public static class Animator {
      public static final FiguraVec3 v5_ROT_TRANS = FiguraVec3.of(-1.0, -1.0, 1.0);
      public static final FiguraVec3 v5_POS_TRANS = FiguraVec3.of(-1.0, 1.0, 1.0);
      private static final FiguraVec3 DO_NOTHING = FiguraVec3.of(1.0, 1.0, 1.0);
      private static final FiguraVec3 LEFT_TIMING = FiguraVec3.of(-0.1F, -0.1F, -0.1F);
      private static final FiguraVec3 RIGHT_TIMING = FiguraVec3.of(0.1F, 0.1F, 0.1F);
      String name;
      String type;
      @Nullable
      Boolean rotation_global;
      @Nullable
      Boolean quaternion_interpolation;
      @Nullable
      List<BlockbenchCommonTypes.Keyframe> keyframes;

      @Nullable
      public CompoundTag getNBT(BlockbenchParser2.Intermediary.AnimationRepresentation animContext, boolean isV5) {
         if ("effect".equals(this.type)) {
            throw new RuntimeException("Shouldn't be attaching FX animators to parts!");
         }

         FiguraVec3 rotTrans = isV5 ? v5_ROT_TRANS : DO_NOTHING;
         FiguraVec3 posTrans = isV5 ? v5_POS_TRANS : DO_NOTHING;
         ListTag rot = new ListTag();
         ListTag pos = new ListTag();
         ListTag scale = new ListTag();
         if (this.keyframes != null) {
            for (BlockbenchCommonTypes.Keyframe kf : this.keyframes) {
               if (kf instanceof BlockbenchCommonTypes.Keyframe.Keyframe3 kf3) {
                  CompoundTag kfTag = new CompoundTag();
                  kfTag.putFloat("time", kf3.time);
                  kfTag.putString("int", kf3.interpolation != null ? kf3.interpolation : "linear");
                  BlockbenchCommonTypes.Keyframe.Keyframe3.Data pre = kf3.data_points[0];
                  boolean transX = isV5 && (kf3.channel.equals("rotation") || kf3.channel.equals("position"));
                  boolean transY = isV5 && kf3.channel.equals("rotation");
                  String post = kf3.channel;

                  FiguraVec3 trans = switch (post) {
                     case "rotation" -> rotTrans;
                     case "position" -> posTrans;
                     default -> DO_NOTHING;
                  };
                  kfTag.put("pre", pre.toNBT(kf3.channel, transX, transY));
                  if (kf3.data_points.length > 1) {
                     BlockbenchCommonTypes.Keyframe.Keyframe3.Data postPoint = kf3.data_points[1];
                     kfTag.put("end", postPoint.toNBT(kf3.channel, transX, transY));
                  }

                  if (kf3.bezier_left_value != null && !kf3.bezier_left_value.equals(BlockbenchCommonTypes.ZERO)) {
                     kfTag.put("bl", BlockbenchCommonTypes.vecToList(kf3.bezier_left_value.copy().multiply(trans)));
                  }

                  if (kf3.bezier_right_value != null && !kf3.bezier_right_value.equals(BlockbenchCommonTypes.ZERO)) {
                     kfTag.put("br", BlockbenchCommonTypes.vecToList(kf3.bezier_right_value.copy().multiply(trans)));
                  }

                  if (kf3.bezier_left_time != null && !kf3.bezier_left_time.equals(LEFT_TIMING)) {
                     kfTag.put("blt", BlockbenchCommonTypes.vecToList(kf3.bezier_left_time));
                  }

                  if (kf3.bezier_right_time != null && !kf3.bezier_right_time.equals(RIGHT_TIMING)) {
                     kfTag.put("brt", BlockbenchCommonTypes.vecToList(kf3.bezier_right_time));
                  }

                  post = kf3.channel;
                  switch (post) {
                     case "position":
                        pos.add(kfTag);
                        break;
                     case "rotation":
                        rot.add(kfTag);
                        break;
                     case "scale":
                        scale.add(kfTag);
                  }
               }
            }
         }

         CompoundTag animatorTag = new CompoundTag();
         CompoundTag channels = new CompoundTag();
         if (!rot.isEmpty()) {
            if (Boolean.TRUE.equals(this.rotation_global)) {
               channels.put("grot", rot);
            } else {
               channels.put("rot", rot);
            }
         }

         if (!pos.isEmpty()) {
            channels.put("pos", pos);
         }

         if (!scale.isEmpty()) {
            channels.put("scl", scale);
         }

         if (!channels.isEmpty()) {
            animatorTag.putInt("id", animContext.globalID);
            animatorTag.put("data", channels);
            return animatorTag;
         } else {
            return null;
         }
      }
   }

   public static class Collection implements BlockbenchCommonTypes.UUIDReferable {
      String uuid;
      String name;
      List<String> children;

      @Override
      public String getUUID() {
         return this.uuid;
      }
   }

   public static class CubeElement extends BlockbenchCommonTypes.Element {
      FiguraVec3 from;
      FiguraVec3 to;
      float inflate;
      Map<String, BlockbenchCommonTypes.CubeFace> faces;

      @Nullable
      @Override
      public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
         CompoundTag tag = super.toNBT(context);
         if (tag == null) {
            return null;
         }

         if (this.from != null && !this.from.equals(BlockbenchCommonTypes.ZERO)) {
            tag.put("f", BlockbenchCommonTypes.vecToList(this.from));
         }

         if (this.to != null && !this.to.equals(BlockbenchCommonTypes.ZERO)) {
            tag.put("t", BlockbenchCommonTypes.vecToList(this.to));
         }

         if (this.inflate != 0.0F) {
            tag.putFloat("inf", this.inflate);
         }

         CompoundTag cubeData = new CompoundTag();

         for (String face : BlockbenchCommonTypes.CubeFace.FACES) {
            BlockbenchCommonTypes.CubeFace faceData;
            if ((faceData = this.faces.get(face)) != null) {
               CompoundTag faceNBT = faceData.toNBT(context);
               if (faceNBT != null) {
                  cubeData.put(String.valueOf(face.charAt(0)), faceNBT);
               }
            }
         }

         tag.put("cube_data", cubeData);
         return tag;
      }
   }

   public static class CubeFace implements BlockbenchCommonTypes.NBTRepresentation<CompoundTag> {
      static final String[] FACES = new String[]{"north", "south", "west", "east", "up", "down"};
      FiguraVec4 uv;
      float rotation;
      @Nullable
      Integer texture;

      @Nullable
      public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
         if (this.texture == null) {
            return null;
         }

         CompoundTag tag = new CompoundTag();
         Integer textureID = context.getTextureGlobalID(this.texture);
         if (textureID == null) {
            return null;
         }

         tag.putInt("tex", textureID);
         if (this.rotation != 0.0F) {
            tag.putFloat("rot", this.rotation);
         }

         if (this.uv != null && !this.uv.equals(BlockbenchCommonTypes.ZERO4)) {
            FiguraVec2 size = context.getTextureFixedSize(this.texture);
            FiguraVec4 sizeTwice = FiguraVec4.of(size.x, size.y, size.x, size.y);
            FiguraVec4 corrected = sizeTwice.multiply(this.uv);
            tag.put("uv", BlockbenchCommonTypes.vecToList(corrected));
         }

         return tag;
      }
   }

   public abstract static class Element implements BlockbenchCommonTypes.UUIDReferable, BlockbenchCommonTypes.NBTRepresentation<CompoundTag> {
      public static final TypeAdapterFactory ADAPTER_FACTORY = new GsonTypeByField<>(BlockbenchCommonTypes.Element.class, "type")
         .bind("cube", BlockbenchCommonTypes.CubeElement.class)
         .bind("mesh", BlockbenchCommonTypes.MeshElement.class)
         .bind("locator", BlockbenchCommonTypes.PointElement.class)
         .bind("null_object", BlockbenchCommonTypes.PointElement.class)
         .withFallback(BlockbenchCommonTypes.UnknownElement.class);
      String name;
      String type;
      String uuid;
      FiguraVec3 rotation;
      FiguraVec3 origin;
      @Nullable
      Boolean visibility;
      @Nullable
      Boolean export;

      @Override
      public String getUUID() {
         return this.uuid;
      }

      @Nullable
      public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
         CompoundTag tag = new CompoundTag();
         if (Boolean.FALSE.equals(this.export)) {
            return null;
         }

         tag.putString("name", this.name);
         if (context.options.partsWithUUIDs) {
            try {
               tag.putIntArray("nr", UUIDUtil.uuidToIntArray(UUID.fromString(this.uuid)));
            } catch (IllegalArgumentException var4) {
               tag.putString("nr", this.uuid);
            }
         }

         if (this.rotation != null && !this.rotation.equals(BlockbenchCommonTypes.ZERO)) {
            tag.put("rot", BlockbenchCommonTypes.vecToList(this.rotation));
         }

         if (this.origin != null && !this.origin.equals(BlockbenchCommonTypes.ZERO)) {
            tag.put("piv", BlockbenchCommonTypes.vecToList(this.origin));
         }

         if (Boolean.FALSE.equals(this.visibility)) {
            tag.putBoolean("vsb", false);
         }

         BlockbenchCommonTypes.attachCollections(context, this.uuid, tag);
         return tag;
      }
   }

   public static class IntPair {
      public int x;
      public int y;

      public IntPair(int x, int y) {
         this.x = x;
         this.y = y;
      }
   }

   public static class Keyframe {
      public static final TypeAdapterFactory ADAPTER_FACTORY = new GsonTypeByField<>(BlockbenchCommonTypes.Keyframe.class, "channel")
         .bind("position", BlockbenchCommonTypes.Keyframe.Keyframe3.class)
         .bind("rotation", BlockbenchCommonTypes.Keyframe.Keyframe3.class)
         .bind("scale", BlockbenchCommonTypes.Keyframe.Keyframe3.class)
         .bind("timeline", BlockbenchCommonTypes.Keyframe.InstructionKeyframe.class)
         .withFallback(BlockbenchCommonTypes.Keyframe.class);
      String channel;
      @Nullable
      String interpolation;
      float time;
      FiguraVec3 bezier_left_time;
      FiguraVec3 bezier_left_value;
      FiguraVec3 bezier_right_time;
      FiguraVec3 bezier_right_value;

      public static Object kfData(String in, float fallback) {
         if (in != null && !in.isBlank()) {
            try {
               return Float.parseFloat(in);
            } catch (NumberFormatException var3) {
               return in;
            }
         } else {
            return fallback;
         }
      }

      public static class InstructionKeyframe extends BlockbenchCommonTypes.Keyframe {
         BlockbenchCommonTypes.Keyframe.InstructionKeyframe.Data[] data_points;

         public static class Data {
            String script;
         }
      }

      public static class Keyframe3 extends BlockbenchCommonTypes.Keyframe {
         BlockbenchCommonTypes.Keyframe.Keyframe3.Data[] data_points;

         public static class Data {
            String x;
            String y;
            String z;

            public ListTag toNBT(String channel, boolean invX, boolean invY) {
               float fallback = channel.equals("scale") ? 1.0F : 0.0F;
               Object x = BlockbenchCommonTypes.Keyframe.kfData(this.x, fallback);
               Object y = BlockbenchCommonTypes.Keyframe.kfData(this.y, fallback);
               Object z = BlockbenchCommonTypes.Keyframe.kfData(this.z, fallback);
               ListTag tag = new ListTag();
               if (x instanceof Float xf && y instanceof Float yf && z instanceof Float zf) {
                  float xs = invX ? -1.0F : 1.0F;
                  float ys = invY ? -1.0F : 1.0F;
                  tag.add(FloatTag.valueOf(xf * xs));
                  tag.add(FloatTag.valueOf(yf * ys));
                  tag.add(FloatTag.valueOf(zf));
                  return tag;
               } else {
                  if (invX) {
                     tag.add(StringTag.valueOf(BlockbenchV5Model.negateLua(String.valueOf(x))));
                  } else {
                     tag.add(StringTag.valueOf(String.valueOf(x)));
                  }

                  if (invY) {
                     tag.add(StringTag.valueOf(BlockbenchV5Model.negateLua(String.valueOf(y))));
                  } else {
                     tag.add(StringTag.valueOf(String.valueOf(y)));
                  }

                  tag.add(StringTag.valueOf(String.valueOf(z)));
                  return tag;
               }
            }
         }
      }
   }

   public static class MeshElement extends BlockbenchCommonTypes.Element {
      Map<String, BlockbenchCommonTypes.MeshFace> faces;
      Map<String, FiguraVec3> vertices;
      String shading;

      @Nullable
      @Override
      public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
         CompoundTag tag = super.toNBT(context);
         if (tag == null) {
            return null;
         }

         if (Objects.equals(this.shading, "smooth")) {
            tag.putBoolean("smo", true);
         }

         CompoundTag meshData = new CompoundTag();
         HashMap<String, Integer> vert2idx = new HashMap<>();
         HashMap<String, FiguraVec3> vert2pos = new HashMap<>();
         ListTag vtx = new ListTag();
         int i = 0;

         for (Entry<String, FiguraVec3> entry : this.vertices.entrySet()) {
            FiguraVec3 combined = entry.getValue().copy().add(this.origin);
            vert2idx.put(entry.getKey(), i++);
            vert2pos.put(entry.getKey(), combined);
            vtx.add(FloatTag.valueOf((float)combined.x));
            vtx.add(FloatTag.valueOf((float)combined.y));
            vtx.add(FloatTag.valueOf((float)combined.z));
         }

         ListTag tex = new ListTag();
         ListTag uvs = new ListTag();
         ListTag fac = new ListTag();
         int intType = 0;
         if (i > 255) {
            intType = 1;
         }

         if (i > 65535) {
            intType = 2;
         }

         for (Entry<String, BlockbenchCommonTypes.MeshFace> entry : this.faces.entrySet()) {
            BlockbenchCommonTypes.MeshFace face = entry.getValue();
            if (face.texture != null && face.vertices != null && face.uv != null && face.vertices.length >= 3 && face.vertices.length <= 4) {
               Integer textureID = context.getTextureGlobalID(face.texture);
               if (textureID != null) {
                  short texBase = (short)((textureID << 4) + face.vertices.length);
                  tex.add(ShortTag.valueOf(texBase));
                  if (face.vertices.length == 4) {
                     face.reorder(vert2pos);
                  }

                  for (String vertID : face.vertices) {
                     Tag value = (Tag)(switch (intType) {
                        case 0 -> ByteTag.valueOf(vert2idx.get(vertID).byteValue());
                        case 1 -> ShortTag.valueOf(vert2idx.get(vertID).shortValue());
                        case 2 -> IntTag.valueOf(vert2idx.get(vertID));
                        default -> throw new IllegalStateException("bad int size key " + intType);
                     });
                     fac.add(value);
                     FiguraVec2 uv = face.uv.get(vertID);
                     FiguraVec2 fixedSize = context.getTextureFixedSize(face.texture);
                     uvs.add(FloatTag.valueOf((float)(uv.x * fixedSize.x)));
                     uvs.add(FloatTag.valueOf((float)(uv.y * fixedSize.y)));
                  }
               }
            }
         }

         meshData.put("vtx", vtx);
         meshData.put("tex", tex);
         meshData.put("fac", fac);
         meshData.put("uvs", uvs);
         tag.put("mesh_data", meshData);
         return tag;
      }
   }

   public static class MeshFace {
      Map<String, FiguraVec2> uv;
      String[] vertices;
      Integer texture;
      private static final FiguraVec3 temp1 = new FiguraVec3();
      private static final FiguraVec3 temp2 = new FiguraVec3();
      private static final FiguraVec3 temp3 = new FiguraVec3();
      private static final FiguraVec3 temp4 = new FiguraVec3();

      public void reorder(Map<String, FiguraVec3> vertexIDtoPos) {
         if (this.vertices.length == 4) {
            FiguraVec3 v1 = vertexIDtoPos.get(this.vertices[0]);
            FiguraVec3 v2 = vertexIDtoPos.get(this.vertices[1]);
            FiguraVec3 v3 = vertexIDtoPos.get(this.vertices[2]);
            FiguraVec3 v4 = vertexIDtoPos.get(this.vertices[3]);
            if (testOppositeSides(v2, v3, v1, v4)) {
               this.vertices = new String[]{this.vertices[2], this.vertices[0], this.vertices[1], this.vertices[3]};
            } else if (testOppositeSides(v1, v2, v3, v4)) {
               this.vertices = new String[]{this.vertices[0], this.vertices[2], this.vertices[1], this.vertices[3]};
            }
         }
      }

      private static boolean testOppositeSides(FiguraVec3 line1, FiguraVec3 line2, FiguraVec3 point1, FiguraVec3 point2) {
         temp1.set(line1);
         temp2.set(line2);
         temp3.set(point1);
         temp4.set(point2);
         temp2.subtract(temp1);
         temp3.subtract(temp1);
         temp4.subtract(temp1);
         temp1.set(temp2);
         temp1.cross(temp3);
         temp2.cross(temp4);
         return temp1.dot(temp2) < 0.0;
      }
   }

   public abstract static class ModelFormat {
      String formatVersion;
      public static final Pattern FORMAT_VERSION_MATCHER = Pattern.compile("^(\\d+?)\\.");
      public static final TypeAdapterFactory ADAPTER_FACTORY = new GsonTypeByField<>(BlockbenchCommonTypes.ModelFormat.class, obj -> {
         String version = obj.getAsJsonObject().getAsJsonObject("meta").get("format_version").getAsString();
         Matcher matcher = FORMAT_VERSION_MATCHER.matcher(version);
         if (!matcher.find()) {
            throw new IllegalStateException("No match on version number");
         } else {
            return matcher.group(1);
         }
      }).bind("5", BlockbenchV5Model.class).bind("4", BlockbenchV4Model.class);
      String modelFormat;

      public abstract Map<String, BlockbenchCommonTypes.UUIDReferable> getAllReferences();

      public abstract CompoundTag convert(BlockbenchParser2.Intermediary var1);
   }

   public interface NBTRepresentation<T extends Tag> {
      T toNBT(BlockbenchParser2.Intermediary var1);
   }

   public static class PointElement extends BlockbenchCommonTypes.Element {
      FiguraVec3 position;

      @Nullable
      @Override
      public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
         CompoundTag tag = super.toNBT(context);
         if (tag == null) {
            return null;
         }

         if (this.position != null && this.origin == null && !this.position.equals(BlockbenchCommonTypes.ZERO)) {
            tag.put("piv", BlockbenchCommonTypes.vecToList(this.position));
         }

         BlockbenchCommonTypes.parseParent(this.name, tag);
         return tag;
      }
   }

   public static class Texture {
      String name;
      @Nullable
      String source;
      @Nullable
      String relative_path;
      Float width;
      Float height;
      Float uv_width;
      Float uv_height;
   }

   public interface UUIDReferable {
      String getUUID();
   }

   public static class UnknownElement extends BlockbenchCommonTypes.Element {
      @Nullable
      @Override
      public CompoundTag toNBT(BlockbenchParser2.Intermediary context) {
         return null;
      }
   }
}
