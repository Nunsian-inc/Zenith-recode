package org.zenith.utility.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicGpuDataStorageMapped;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;
import org.zenith.RenderState;
import org.zenith.utility.misc.ShaderUniform;

public abstract class ShaderDefinition {
   private static final Map<String, Integer> items = Map.of(
      "float", 1, "int", 1, "vec2", 2, "ivec2", 2, "vec3", 3, "ivec3", 3, "vec4", 4, "ivec4", 4, "mat4", 16
   );
   private static final Set<String> items2 = Set.of("ModelViewMat", "ProjMat", "ColorModulator", "TextureMat", "ModelOffset");
   private final Identifier identifier;
   private final VertexFormat vertexFormat;
   private final Map<String, float[]> items3 = new HashMap<>();
   private final Map<String, ShaderUniform> items4 = new HashMap<>();
   private final String[] texts;
   private Map<String, Integer> items5 = new LinkedHashMap<>();
   private boolean flag;
   private final Set<String> items6 = new HashSet<>();
   private int[] intValues = new int[0];
   private int[] intValues2 = new int[0];
   private float[][] floatValues = new float[0][];
   private Map<String, Integer> items7 = new HashMap<>();
   private float[] floatValues2 = new float[0];
   private int intValue;
   @Nullable
   private UniformStorage.Entry entry;
   private int intValue2 = -1;
   private final String text;
   private String text2;
   @Nullable
   private DynamicGpuDataStorageMapped<UniformStorage.Entry> dynamicUniformStorage;
   private int intValue3;
   @Nullable
   private String[] texts2;
   @Nullable
   public String text3;

   public ShaderDefinition(Identifier identifier, VertexFormat vertexFormat, String[] texts) {
      this.identifier = identifier;
      this.vertexFormat = vertexFormat;
      this.texts = texts;
      this.text = identifier.toString();
      this.text2 = String.join(",", texts);
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public ShaderUniform method5166(String key) {
      return this.items4.computeIfAbsent(key, var1x -> new ShaderUniform(this, var1x));
   }

   public void method5559(String text, float[] floatValues) {
      this.items3.put(text, floatValues);
      Integer n = this.items7.get(text);
      if (n != null) {
         this.floatValues[n] = floatValues;
      }
   }

   public boolean method5560(String str) {
      this.method5567();
      return this.items5.containsKey(str);
   }

   public RenderPipeline method5561(VertexFormat vertexFormat, PrimitiveTopology primitiveTopology, RenderState.Data2 data2) {
      this.method5567();
      return LightUtil.method6710(
         this.identifier, this.text, this.text2, vertexFormat, primitiveTopology, data2, this.texts2 != null ? this.texts2 : this.texts, !this.items5.isEmpty()
      );
   }

   @Nullable
   public GpuBufferSlice method5562() {
      this.method5567();
      if (this.items5.isEmpty()) {
         return null;
      } else {
         if (this.items3.size() != this.intValue2) {
            this.method5563();
            this.intValue2 = this.items3.size();
         }

         int[] intValues = this.intValues;
         float[] floatValues2 = this.floatValues2;

         for (int i = 0; i < intValues.length; i++) {
            float[] floatValue = this.floatValues[i];
            int n = intValues[i];
            int n2 = this.intValues2[i];
            int n3 = floatValue == null ? 0 : Math.min(floatValue.length, n);
            if (n3 > 0) {
               System.arraycopy(floatValue, 0, floatValues2, n2, n3);
            }

            for (int j = n3; j < n; j++) {
               floatValues2[n2 + j] = 0.0F;
            }
         }

         UniformStorage.Entry entry = this.entry;
         if (entry == null || !entry.method1277(intValues, floatValues2)) {
            entry = new UniformStorage.Entry((float[])floatValues2.clone(), intValues);
            this.entry = entry;
         }

         return this.method5565().writeData(entry);
      }
   }

   private void method5563() {
      for (String str : this.items3.keySet()) {
         if (!this.items5.containsKey(str) && this.items6.add(str)) {
            LogUtils.getLogger()
               .warn("Zenith: в блоке {} шейдера {} нет поля {} — значение никуда не попадёт", new Object[]{"ZenithData", this.identifier, str});
         }
      }
   }

   public void method5564(RenderPass renderPass, @Nullable GpuBufferSlice gpuBufferSlice) {
      if (gpuBufferSlice != null) {
         renderPass.setUniform("ZenithData", gpuBufferSlice);
      }
   }

   private DynamicGpuDataStorageMapped<UniformStorage.Entry> method5565() {
      int intValue = this.intValue;
      if (this.dynamicUniformStorage == null || this.intValue3 != intValue) {
         if (this.dynamicUniformStorage != null) {
            UniformStorage.method1275(this.dynamicUniformStorage);
         }

         this.intValue3 = intValue;
         this.dynamicUniformStorage = UniformStorage.method1274("zenith ZenithData " + this.identifier, intValue);
      }

      return this.dynamicUniformStorage;
   }

   public void method5566() {
      this.flag = false;
      this.method5567();
   }

   private void method5567() {
      if (!this.flag) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null && minecraft.getShaderManager() != null) {
            this.items5 = new LinkedHashMap<>();
            this.texts2 = null;
            this.method5573(minecraft);
            this.text2 = String.join(",", this.texts2 != null ? this.texts2 : this.texts);
            this.flag = this.method5569(minecraft);
            this.method5568();
         }
      }
   }

   private void method5568() {
      int size = this.items5.size();
      int[] intValues = new int[size];
      int[] intValues2 = new int[size];
      float[][] floatValues = new float[size][];
      HashMap map = new HashMap();
      int n = 0;
      int n2 = 0;

      for (Entry entry : this.items5.entrySet()) {
         intValues[n2] = (Integer)entry.getValue();
         intValues2[n2] = n;
         floatValues[n2] = this.items3.get(entry.getKey());
         map.put((String)entry.getKey(), n2);
         n += intValues[n2];
         n2++;
      }

      this.intValues = intValues;
      this.intValues2 = intValues2;
      this.floatValues = floatValues;
      this.items7 = map;
      this.floatValues2 = new float[n];
      this.intValue = size == 0 ? 0 : UniformStorage.method1273(intValues);
      this.entry = null;
      this.intValue2 = -1;
   }

   private boolean method5569(Minecraft minecraft) {
      String str = this.method5570(minecraft, "fragment", ShaderType.FRAGMENT);
      var map = method5571(str);
      String str2 = null;
      if (map.isEmpty()) {
         str2 = this.method5570(minecraft, "vertex", ShaderType.VERTEX);
         map = method5571(str2);
      }

      this.items5.putAll(map);
      return str != null || str2 != null;
   }

   @Nullable
   private String method5570(Minecraft minecraft, String str, ShaderType type) {
      String path = this.identifier.getPath();
      int lastIndexOf = path.lastIndexOf(47);
      String str2 = lastIndexOf < 0 ? path : path.substring(0, lastIndexOf);
      return LightUtil.shaderSource(Identifier.fromNamespaceAndPath(this.identifier.getNamespace(), str2 + "/" + str), type);
   }

   private static Map<String, Integer> method5571(@Nullable String str) {
      LinkedHashMap map = new LinkedHashMap();
      if (str == null) {
         return map;
      } else {
         int indexOf = str.indexOf("uniform ZenithData");
         if (indexOf < 0) {
            return map;
         } else {
            int fromIndex = str.indexOf(123, indexOf);
            int endIndex = str.indexOf(125, fromIndex);
            if (fromIndex >= 0 && endIndex >= 0) {
               for (String str2 : str.substring(fromIndex + 1, endIndex).split(";")) {
                  ArrayList arrayList = new ArrayList();
                  StringTokenizer stringTokenizer = new StringTokenizer(str2);

                  while (stringTokenizer.hasMoreTokens()) {
                     arrayList.add(stringTokenizer.nextToken());
                  }

                  if (arrayList.size() >= 2) {
                     Integer n = items.get(arrayList.get(arrayList.size() - 2));
                     if (n != null) {
                        map.put((String)arrayList.get(arrayList.size() - 1), n);
                     }
                  }
               }

               return method5572(str, map);
            } else {
               return map;
            }
         }
      }
   }

   private static Map<String, Integer> method5572(String str, Map<String, Integer> map) {
      LinkedHashMap map2 = new LinkedHashMap();

      for (Entry entry : map.entrySet()) {
         String key = (String)entry.getKey();

         for (String line : str.lines().toList()) {
            String str2 = line.trim();
            if (str2.startsWith("#define ")) {
               StringTokenizer stringTokenizer = new StringTokenizer(str2);
               stringTokenizer.nextToken();
               if (stringTokenizer.countTokens() >= 2) {
                  String nextToken = stringTokenizer.nextToken();
                  String nextToken2 = stringTokenizer.nextToken();
                  if (nextToken2.equals(key + ".xyz") || nextToken2.equals(key + ".rgb")) {
                     key = nextToken;
                     break;
                  }
               }
            }
         }

         map2.put(key, (Integer)entry.getValue());
      }

      return map2;
   }

   private void method5573(Minecraft minecraft) {
      if (minecraft.getResourceManager() != null) {
         Identifier location = Identifier.fromNamespaceAndPath(this.identifier.getNamespace(), "shaders/" + this.identifier.getPath() + ".json");
         var resource = minecraft.getResourceManager().getResource(location);
         if (!resource.isEmpty()) {
            try {
               try (BufferedReader openAsReader = ((Resource)resource.get()).openAsReader()) {
                  JsonObject jsonObject = JsonParser.parseReader(openAsReader).getAsJsonObject();
                  JsonArray jsonArray = jsonObject.getAsJsonArray("samplers");
                  if (jsonArray != null) {
                     String[] texts2 = new String[jsonArray.size()];

                     for (int i = 0; i < texts2.length; i++) {
                        texts2[i] = jsonArray.get(i).getAsJsonObject().get("name").getAsString();
                     }

                     this.texts2 = texts2;
                     return;
                  }
               }
            } catch (Exception e) {
               this.text3 = "не удалось прочитать сэмплеры " + location + ": " + e.getMessage();
            }
         }
      }
   }
}
