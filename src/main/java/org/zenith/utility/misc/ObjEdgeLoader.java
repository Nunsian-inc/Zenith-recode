package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;

public final class ObjEdgeLoader {
   private ObjEdgeLoader() {
   }

   public static float[] loadObjEdges(String resourcePath, float verticalOffset, boolean normalize) {
      InputStream resourceStream = ObjEdgeLoader.class.getResourceAsStream(resourcePath);
      if (resourceStream == null) {
         return null;
      } else {
         try {
            float[] floats;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
               ArrayList vertices = new ArrayList();
               HashMap edges = new HashMap();
               float minX = Float.POSITIVE_INFINITY;
               float minY = Float.POSITIVE_INFINITY;
               float minZ = Float.POSITIVE_INFINITY;
               float maxX = Float.NEGATIVE_INFINITY;
               float maxY = Float.NEGATIVE_INFINITY;
               float maxZ = Float.NEGATIVE_INFINITY;

               String line;
               while ((line = reader.readLine()) != null) {
                  line = line.trim();
                  if (line.startsWith("v ")) {
                     String[] faceTokens = line.split("\\s+");
                     float x = Float.parseFloat(faceTokens[1]);
                     float y = Float.parseFloat(faceTokens[2]) - verticalOffset;
                     float z = Float.parseFloat(faceTokens[3]);
                     vertices.add(new Vector3f(x, y, z));
                     minX = Math.min(minX, x);
                     maxX = Math.max(maxX, x);
                     minY = Math.min(minY, y);
                     maxY = Math.max(maxY, y);
                     minZ = Math.min(minZ, z);
                     maxZ = Math.max(maxZ, z);
                  } else if (line.startsWith("f ")) {
                     addFaceEdges(line.split("\\s+"), vertices, edges);
                  }
               }

               float centerX = 0.0F;
               float centerY = 0.0F;
               float centerZ = 0.0F;
               float scale = 1.0F;
               if (normalize) {
                  centerX = (minX + maxX) * 0.5F;
                  centerY = (minY + maxY) * 0.5F;
                  centerZ = (minZ + maxZ) * 0.5F;
                  float maxDimension;
                  scale = (maxDimension = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ))) > 1.0E-5F ? 1.0F / maxDimension : 1.0F;
               }

               ArrayList coordinates = new ArrayList();

               for (Object obj : (Iterable<?>)(Object)(edges.values())) {
                  ObjEdgeLoader.Entry entry = (ObjEdgeLoader.Entry)obj;
                  if (entry.faceCount == 1 || !entry.sameNormal) {
                     coordinates.add((entry.startX - centerX) * scale);
                     coordinates.add((entry.startY - centerY) * scale);
                     coordinates.add((entry.startZ - centerZ) * scale);
                     coordinates.add((entry.endX - centerX) * scale);
                     coordinates.add((entry.endY - centerY) * scale);
                     coordinates.add((entry.endZ - centerZ) * scale);
                  }
               }

               float[] floats2 = new float[coordinates.size()];

               for (int i = 0; i < coordinates.size(); i++) {
                  floats2[i] = (Float)coordinates.get(i);
               }

               floats = floats2;
            }

            return floats;
         } catch (Exception e) {
            return null;
         }
      }
   }

   private static void addFaceEdges(String[] faceTokens, List<Vector3f> vertices, Map<Long, ObjEdgeLoader.Entry> edges) {
      for (int i = 1; i < faceTokens.length; i++) {
         int nextIndex = i == faceTokens.length - 1 ? 1 : i + 1;
         int startIndex = Integer.parseInt(faceTokens[i].split("/")[0]) - 1;
         int endIndex = Integer.parseInt(faceTokens[nextIndex].split("/")[0]) - 1;
         if (startIndex != endIndex) {
            String[] vertexParts = faceTokens[i].split("/");
            int normalIndex = vertexParts.length > 2 && !vertexParts[2].isEmpty() ? Integer.parseInt(vertexParts[2]) - 1 : -1;
            long edgeKey = (long)Math.min(startIndex, endIndex) << 32 | Math.max(startIndex, endIndex) & 4294967295L;
            ObjEdgeLoader.Entry entry = (ObjEdgeLoader.Entry)edges.get(edgeKey);
            if (entry == null) {
               Vector3f start = (Vector3f)vertices.get(startIndex);
               Vector3f end = (Vector3f)vertices.get(endIndex);
               edges.put(edgeKey, new ObjEdgeLoader.Entry(start.x, start.y, start.z, end.x, end.y, end.z, normalIndex));
            } else {
               entry.faceCount++;
               if (entry.normalIndex != normalIndex) {
                  entry.sameNormal = false;
               }
            }
         }
      }
   }

   private static final class Entry {
      private final float startX;
      private final float startY;
      private final float startZ;
      private final float endX;
      private final float endY;
      private final float endZ;
      private final int normalIndex;
      private int faceCount = 1;
      private boolean sameNormal = true;

      private Entry(float startX, float startY, float startZ, float endX, float endY, float endZ, int normalIndex) {
         this.startX = startX;
         this.startY = startY;
         this.startZ = startZ;
         this.endX = endX;
         this.endY = endY;
         this.endZ = endZ;
         this.normalIndex = normalIndex;
      }
   }
}
