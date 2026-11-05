package org.zenith.utility.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.zenith.utility.mixin.accessors.PostEffectPassAccessor;
import org.zenith.utility.mixin.accessors.PostEffectProcessorAccessor;

public final class PostEffectManager {
   public static final String text = "ZenithPost";
   private static final Map<Identifier, List<List<PostEffectManager.Data>>> items = new HashMap<>();
   private static final Map<PostPass, MappableRingBuffer> items2 = new IdentityHashMap<>();

   private PostEffectManager() {
   }

   public static void method2819(PostChain postChain, Identifier identifier, PostEffectManager.Entry entry) {
      if (postChain != null && !entry.isEmpty()) {
         var datases = method2822(identifier);
         if (!datases.isEmpty()) {
            List passes = ((PostEffectProcessorAccessor)(Object)postChain).getPasses();
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

            for (int i = 0; i < passes.size() && i < datases.size(); i++) {
               List<PostEffectManager.Data> datas = (List)datases.get(i);
               if (!datas.isEmpty()) {
                  PostPass postPass = (PostPass)passes.get(i);
                  Map zenith$uniformBuffers = ((PostEffectPassAccessor)(Object)postPass).zenith$uniformBuffers();
                  GpuBuffer gpuBuffer = (GpuBuffer)zenith$uniformBuffers.get("ZenithPost");
                  if (gpuBuffer != null) {
                     GpuBuffer currentBuffer = method2820(postPass, gpuBuffer).currentBuffer();
                     zenith$uniformBuffers.put("ZenithPost", currentBuffer);

                     try (GpuBufferSlice.MappedView mappedView = currentBuffer.slice().map(false, true)) {
                        ByteBuffer data = mappedView.data();
                        Std140Builder intoBuffer = Std140Builder.intoBuffer(data);

                        for (PostEffectManager.Data data2 : datas) {
                           method2821(intoBuffer, data2.intValue, entry.items.getOrDefault(data2.text, data2.floatValues));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static MappableRingBuffer method2820(PostPass postPass, GpuBuffer gpuBuffer) {
      MappableRingBuffer mappableRingBuffer = items2.get(postPass);
      if (mappableRingBuffer == null) {
         mappableRingBuffer = new MappableRingBuffer(() -> "zenith ZenithPost", 130, (int)gpuBuffer.size());
         items2.put(postPass, mappableRingBuffer);
         gpuBuffer.close();
      } else {
         mappableRingBuffer.rotate();
      }

      return mappableRingBuffer;
   }

   private static void method2821(Std140Builder intoBuffer, int intValue, float[] floats) {
      float x = floats.length > 0 ? floats[0] : 0.0F;
      float y = floats.length > 1 ? floats[1] : 0.0F;
      float z = floats.length > 2 ? floats[2] : 0.0F;
      float f = floats.length > 3 ? floats[3] : 0.0F;
      switch (intValue) {
         case 1:
            intoBuffer.putFloat(x);
            break;
         case 2:
            intoBuffer.putVec2(x, y);
            break;
         case 3:
            intoBuffer.putVec3(x, y, z);
            break;
         default:
            intoBuffer.putVec4(x, y, z, f);
      }
   }

   private static List<List<PostEffectManager.Data>> method2822(Identifier identifier) {
      var datases = items.get(identifier);
      if (datases != null) {
         return datases;
      } else {
         var datases2 = method2824(identifier);
         items.put(identifier, datases2);
         return datases2;
      }
   }

   public static void method2823() {
      items.clear();

      for (MappableRingBuffer mappableRingBuffer : items2.values()) {
         mappableRingBuffer.close();
      }

      items2.clear();
   }

   private static List<List<PostEffectManager.Data>> method2824(Identifier identifier) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.getResourceManager() != null) {
         Identifier location = Identifier.fromNamespaceAndPath(identifier.getNamespace(), "post_effect/" + identifier.getPath() + ".json");
         var resource = minecraft.getResourceManager().getResource(location);
         if (resource.isEmpty()) {
            return List.of();
         } else {
            try {
               ArrayList arrayList;
               try (BufferedReader openAsReader = ((Resource)resource.get()).openAsReader()) {
                  JsonObject jsonObject = JsonParser.parseReader(openAsReader).getAsJsonObject();
                  JsonArray jsonArray = jsonObject.getAsJsonArray("passes");
                  if (jsonArray == null) {
                     return List.of();
                  }

                  var arrayList2 = new ArrayList<>(jsonArray.size());

                  for (int i = 0; i < jsonArray.size(); i++) {
                     arrayList2.add(method2825(jsonArray.get(i).getAsJsonObject()));
                  }

                  arrayList = arrayList2;
               }

               return arrayList;
            } catch (Exception e) {
               return List.of();
            }
         }
      } else {
         return List.of();
      }
   }

   private static List<PostEffectManager.Data> method2825(JsonObject jsonObject) {
      JsonObject json = jsonObject.getAsJsonObject("uniforms");
      if (json == null) {
         return List.of();
      } else {
         JsonArray jsonArray = json.getAsJsonArray("ZenithPost");
         if (jsonArray == null) {
            return List.of();
         } else {
            List<Data> datas = new ArrayList<>(jsonArray.size());

            for (int i = 0; i < jsonArray.size(); i++) {
               JsonObject jsonObject2 = jsonArray.get(i).getAsJsonObject();
               String string = jsonObject2.get("name").getAsString();
               int intValue = method2826(jsonObject2.get("type").getAsString());
               datas.add(new PostEffectManager.Data(string, intValue, method2827(jsonObject2.get("value"), intValue)));
            }

            return datas;
         }
      }
   }

   private static int method2826(String string) {
      return switch (string) {
         case "vec2", "ivec2" -> 2;
         case "vec3", "ivec3" -> 3;
         case "vec4", "ivec4" -> 4;
         default -> 1;
      };
   }

   private static float[] method2827(JsonElement jsonElement, int intValue) {
      float[] floats = new float[intValue];
      if (jsonElement == null) {
         return floats;
      } else {
         if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (int i = 0; i < intValue && i < jsonArray.size(); i++) {
               floats[i] = jsonArray.get(i).getAsFloat();
            }
         } else {
            floats[0] = jsonElement.getAsFloat();
         }

         return floats;
      }
   }

   private static final class Data {
      private final String text;
      private final int intValue;
      private final float[] floatValues;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValues, other.floatValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, floatValues);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "floatValues=" + this.floatValues + "]";
      }

      private Data(String text, int intValue, float[] floatValues) {
         this.text = text;
         this.intValue = intValue;
         this.floatValues = floatValues;
      }

      public String name() {
         return this.text;
      }

      public int size() {
         return this.intValue;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }
   }

   public static final class Entry {
      private final Map<String, float[]> items = new LinkedHashMap<>();

      public PostEffectManager.Entry method2828(String str, float floatValue) {
         this.items.put(str, new float[]{floatValue});
         return this;
      }

      public PostEffectManager.Entry method2829(String str, float f, float f2) {
         this.items.put(str, new float[]{f, f2});
         return this;
      }

      public PostEffectManager.Entry method2830(String str, float f, float f2, float f3) {
         this.items.put(str, new float[]{f, f2, f3});
         return this;
      }

      public PostEffectManager.Entry method2831(String str, float x, float y, float z, float f) {
         this.items.put(str, new float[]{x, y, z, f});
         return this;
      }

      public boolean isEmpty() {
         return this.items.isEmpty();
      }
   }
}
