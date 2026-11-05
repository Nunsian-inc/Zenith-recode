package org.zenith.base.font;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.render.ImmediateRenderer;

public final class HintedText {
   private static final float MAX_EM_PX = 24.0F;
   private static final float MIN_EM_PX = 5.0F;
   private static final int SUBPIXEL_BINS = 4;
   private static final int ATLAS_SIZE = 1024;
   private static final int PADDING = 1;
   private static final float GAMMA = 0.8F;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int LOAD_TARGET_LIGHT = 65536;
   private static final byte[] GAMMA_LUT = new byte[256];
   private static long library;
   private static boolean broken;
   private static final Map<String, HintedText.Face> FACES;
   private static NativeImage atlasImage;
   private static DynamicTexture atlasTexture;
   private static int dirtyX0;
   private static int dirtyY0;
   private static int dirtyX1;
   private static int dirtyY1;
   private static int shelfX;
   private static int shelfY;
   private static int shelfHeight;
   private static final Long2ObjectOpenHashMap<HintedText.Glyph> GLYPHS;
   private static int nextFaceId;
   private static ByteBufferBuilder allocator;
   private static BufferBuilder builder;
   private static final float[] batchShaderColor;
   private static final long STILL_NANOS = 300000000L;
   private static final long FORGET_NANOS = 5000000000L;
   private static final long PRUNE_NANOS = 1000000000L;
   private static final Map<String, ArrayList<HintedText.Placement>> PLACEMENTS;
   private static final Object2IntOpenHashMap<String> OCCURRENCES;
   private static long lastPrune;

   private HintedText() {
   }

   public static boolean render(MsdfFont font, String text, float size, int color, Matrix4f matrix, float floatValue, float f, float z) {
      String hintedFace = font.getHintedFace();
      if (size > 0.0F && size <= 5.0F && ImmediateRenderer.method8005() && hintedFace != null && !broken && !text.isEmpty()) {
         float m00 = matrix.m00();
         if (!(m00 <= 0.0F)
            && !(Math.abs(matrix.m01()) > 1.0E-4F)
            && !(Math.abs(matrix.m10()) > 1.0E-4F)
            && !(Math.abs(matrix.m11() - m00) > 1.0E-4F * Math.max(1.0F, m00))) {
            float guiScale = Minecraft.getInstance().getWindow().getGuiScale();
            float em = size * m00 * guiScale;
            if (inMotion(text, em)) {
               return false;
            } else if (!(em < 5.0F) && !(em > 24.0F)) {
               HintedText.Face face2 = face(hintedFace);
               if (face2 == null) {
                  return false;
               } else {
                  int sizeQuarter = Math.round(em * 4.0F);
                  float f2 = Math.round((matrix.m11() * f + matrix.m31()) * guiScale);
                  float z2 = matrix.m22() * z + matrix.m32();
                  BufferBuilder bufferBuilder = begin();
                  int charAt = -1;
                  boolean flag = false;
                  float floatValue2 = floatValue;

                  for (int i = 0; i < text.length(); i++) {
                     char c = text.charAt(i);
                     if (flag) {
                        flag = false;
                     } else if (c == 167) {
                        flag = true;
                     } else {
                        MsdfGlyph msdfGlyph = font.getGlyph(c);
                        if (msdfGlyph == null) {
                           char normalizeGlyphChar = MsdfFont.normalizeGlyphChar(c);
                           msdfGlyph = normalizeGlyphChar == c ? null : font.getGlyph(normalizeGlyphChar);
                           if (msdfGlyph == null) {
                              continue;
                           }

                           c = normalizeGlyphChar;
                        }

                        floatValue2 += font.kerning(charAt, c) * size;
                        float f3 = (m00 * floatValue2 + matrix.m30()) * guiScale;
                        float f4 = (float)Math.floor(f3);
                        int n = Math.round((f3 - f4) * 4.0F);
                        if (n == 4) {
                           f4++;
                           n = 0;
                        }

                        HintedText.Glyph glyph2 = glyph(face2, c, sizeQuarter, n);
                        if (builder != bufferBuilder) {
                           bufferBuilder = begin();
                        }

                        if (glyph2 != null && glyph2.width > 0) {
                           float x = (f4 + glyph2.left) / guiScale;
                           float y = (f2 - glyph2.top) / guiScale;
                           float x2 = x + glyph2.width / guiScale;
                           float y2 = y + glyph2.height / guiScale;
                           bufferBuilder.addVertex(x, y, z2).setUv(glyph2.u0, glyph2.v0).setColor(color);
                           bufferBuilder.addVertex(x, y2, z2).setUv(glyph2.u0, glyph2.v1).setColor(color);
                           bufferBuilder.addVertex(x2, y2, z2).setUv(glyph2.u1, glyph2.v1).setColor(color);
                           bufferBuilder.addVertex(x2, y, z2).setUv(glyph2.u1, glyph2.v0).setColor(color);
                        }

                        floatValue2 += msdfGlyph.getWidth(size);
                        charAt = c;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean inMotion(String text, float em) {
      long nanoTime = System.nanoTime();
      int addTo = OCCURRENCES.addTo(text, 1);
      var placements = PLACEMENTS.computeIfAbsent(text, var0x -> new ArrayList<>(1));
      HintedText.Placement placement;
      if (addTo < placements.size()) {
         placement = (HintedText.Placement)placements.get(addTo);
         if (Math.abs(placement.em - em) > 0.001F) {
            placement.movedAt = nanoTime;
         }
      } else {
         placement = new HintedText.Placement();
         placement.movedAt = nanoTime - 300000000L;
         placements.add(placement);
      }

      placement.em = em;
      placement.seenAt = nanoTime;
      return nanoTime - placement.movedAt < 300000000L;
   }

   public static void endFrame() {
      OCCURRENCES.clear();
      long nanoTime = System.nanoTime();
      if (nanoTime - lastPrune >= 1000000000L) {
         lastPrune = nanoTime;
         PLACEMENTS.values().removeIf(placements -> {
            placements.removeIf(var2x -> nanoTime - var2x.seenAt > 5000000000L);
            return placements.isEmpty();
         });
      }
   }

   public static boolean pending() {
      return builder != null;
   }

   public static void flush() {
      if (builder != null) {
         BufferBuilder bufferBuilder = builder;
         builder = null;
         MeshData orThrow = bufferBuilder.build();
         if (orThrow == null) {
            allocator.clear();
         } else {
            if (dirtyX1 > dirtyX0 && dirtyY1 > dirtyY0) {
               uploadDirty();
            }

            float[] floats = RenderState.method1885();
            boolean flag = floats[0] != batchShaderColor[0] || floats[1] != batchShaderColor[1] || floats[2] != batchShaderColor[2] || floats[3] != batchShaderColor[3];
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            if (flag) {
               RenderState.method1883(batchShaderColor[0], batchShaderColor[1], batchShaderColor[2], batchShaderColor[3]);
            }

            RenderState.method1888(0, atlasTexture.getTextureView(), FilterMode.NEAREST);
            RenderState.method1898("core/position_tex_color");

            try {
               ImmediateRenderer.method8015(orThrow);
            } finally {
               RenderState.method1893(0);
               if (flag) {
                  RenderState.method1883(floats[0], floats[1], floats[2], floats[3]);
               }

               RenderState.method1876();
               RenderState.method1872();
               allocator.clear();
            }
         }
      }
   }

   private static BufferBuilder begin() {
      MsdfRenderer.flushMsdfPending();
      float[] floats = RenderState.method1885();
      if (builder != null
         && (floats[0] != batchShaderColor[0] || floats[1] != batchShaderColor[1] || floats[2] != batchShaderColor[2] || floats[3] != batchShaderColor[3])) {
         flush();
      }

      if (builder == null) {
         if (allocator == null) {
            allocator = new ByteBufferBuilder(32768);
         }

         ensureAtlas();
         builder = new BufferBuilder(allocator, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         System.arraycopy(floats, 0, batchShaderColor, 0, 4);
      }

      return builder;
   }

   private static HintedText.Face face(String hintedFace) {
      HintedText.Face face = FACES.get(hintedFace);
      if (face != null) {
         return face.handle == null ? null : face;
      } else {
         face = new HintedText.Face();
         FACES.put(hintedFace, face);

         try {
            if (library == 0L) {
               MemoryStack stackPush = MemoryStack.stackPush();

               try {
                  PointerBuffer mallocPointer = stackPush.mallocPointer(1);
                  check(FreeType.FT_Init_FreeType(mallocPointer), "FT_Init_FreeType");
                  library = mallocPointer.get(0);
               } catch (Throwable e) {
                  if (stackPush != null) {
                     try {
                        stackPush.close();
                     } catch (Throwable ex) {
                        e.addSuppressed(ex);
                     }
                  }

                  throw e;
               }

               if (stackPush != null) {
                  stackPush.close();
               }
            }

            Identifier location = Zenith.method1501("fonts/ttf/" + hintedFace + ".otf");

            byte[] allBytes;
            try (InputStream inputStream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location).open()) {
               allBytes = inputStream.readAllBytes();
            }

            face.data = MemoryUtil.memAlloc(allBytes.length).put(allBytes).flip();
            MemoryStack stackPush = MemoryStack.stackPush();

            try {
               PointerBuffer mallocPointer = stackPush.mallocPointer(1);
               check(FreeType.FT_New_Memory_Face(library, face.data, 0L, mallocPointer), "FT_New_Memory_Face " + hintedFace);
               face.handle = FT_Face.create(mallocPointer.get(0));
            } catch (Throwable e) {
               if (stackPush != null) {
                  try {
                     stackPush.close();
                  } catch (Throwable ex) {
                     e.addSuppressed(ex);
                  }
               }

               throw e;
            }

            if (stackPush != null) {
               stackPush.close();
            }

            face.id = nextFaceId++;
            return face;
         } catch (Throwable e) {
            LOGGER.warn("Hinted text disabled for {}: {}", hintedFace, e.toString());
            if (library == 0L) {
               broken = true;
            }

            return null;
         }
      }
   }

   private static HintedText.Glyph glyph(HintedText.Face face, char charAt, int sizeQuarter, int n) {
      long l = (long)face.id << 40 | (long)sizeQuarter << 24 | (long)n << 16 | charAt;
      HintedText.Glyph glyph = (HintedText.Glyph)GLYPHS.get(l);
      if (glyph != null) {
         return glyph;
      } else {
         HintedText.Glyph rasterize2 = rasterize(face, charAt, sizeQuarter, n);
         if (rasterize2 == null) {
            rasterize2 = HintedText.Glyph.EMPTY;
         }

         GLYPHS.put(l, rasterize2);
         return rasterize2;
      }
   }

   private static HintedText.Glyph rasterize(HintedText.Face face, char charAt, int sizeQuarter, int n) {
      FT_Face handle = face.handle;
      if (face.sizeQuarter != sizeQuarter) {
         if (FreeType.FT_Set_Char_Size(handle, 0L, sizeQuarter * 16L, 72, 72) != 0) {
            return null;
         }

         face.sizeQuarter = sizeQuarter;
      }

      int glyph_index = FreeType.FT_Get_Char_Index(handle, charAt);
      if (glyph_index == 0) {
         return null;
      } else if (FreeType.FT_Load_Glyph(handle, glyph_index, 65544) != 0) {
         return null;
      } else {
         FT_GlyphSlot glyph = handle.glyph();
         if (glyph == null) {
            return null;
         } else {
            if (n != 0) {
               FreeType.FT_Outline_Translate(glyph.outline(), n * 16L, 0L);
            }

            if (FreeType.FT_Render_Glyph(glyph, 1) != 0) {
               return null;
            } else {
               FT_Bitmap bitmap = glyph.bitmap();
               int width = bitmap.width();
               int rows = bitmap.rows();
               HintedText.Glyph glyph2 = new HintedText.Glyph();
               glyph2.left = glyph.bitmap_left();
               glyph2.top = glyph.bitmap_top();
               if (width != 0 && rows != 0) {
                  int pitch = bitmap.pitch();
                  ByteBuffer buffer = bitmap.buffer(Math.abs(pitch) * rows);
                  if (buffer == null) {
                     return glyph2;
                  } else {
                     if (!reserve(width, rows)) {
                        resetAtlas();
                        if (!reserve(width, rows)) {
                           return null;
                        }
                     }

                     int n2 = shelfX - width - 1;
                     int shelfY2 = shelfY;

                     for (int i = 0; i < rows; i++) {
                        int n3 = pitch >= 0 ? i * pitch : (rows - 1 - i) * -pitch;

                        for (int j = 0; j < width; j++) {
                           int n4 = GAMMA_LUT[buffer.get(n3 + j) & 255] & 255;
                           atlasImage.setPixel(n2 + j, shelfY2 + i, n4 << 24 | 16777215);
                        }
                     }

                     markDirty(n2, shelfY2, width, rows);
                     glyph2.width = width;
                     glyph2.height = rows;
                     glyph2.u0 = n2 / 1024.0F;
                     glyph2.v0 = shelfY2 / 1024.0F;
                     glyph2.u1 = (n2 + width) / 1024.0F;
                     glyph2.v1 = (shelfY2 + rows) / 1024.0F;
                     return glyph2;
                  }
               } else {
                  return glyph2;
               }
            }
         }
      }
   }

   private static boolean reserve(int width, int rows) {
      if (width + 1 <= 1024 && rows + 1 <= 1024) {
         if (shelfX + width + 1 > 1024) {
            shelfY = shelfY + shelfHeight + 1;
            shelfX = 0;
            shelfHeight = 0;
         }

         if (shelfY + rows + 1 > 1024) {
            return false;
         } else {
            shelfX += width + 1;
            shelfHeight = Math.max(shelfHeight, rows);
            return true;
         }
      } else {
         return false;
      }
   }

   private static void ensureAtlas() {
      if (atlasTexture == null) {
         atlasImage = new NativeImage(1024, 1024, true);
         atlasImage.fillRect(0, 0, 1024, 1024, 16777215);
         atlasTexture = new DynamicTexture(() -> "zenith hinted text", atlasImage);
         markDirty(0, 0, 1024, 1024);
      }
   }

   private static void markDirty(int n, int n2, int width, int rows) {
      dirtyX0 = Math.min(dirtyX0, n);
      dirtyY0 = Math.min(dirtyY0, n2);
      dirtyX1 = Math.max(dirtyX1, n + width);
      dirtyY1 = Math.max(dirtyY1, n2 + rows);
   }

   private static void uploadDirty() {
      int dstX = dirtyX0;
      int dstY = dirtyY0;
      int n = dirtyX1 - dirtyX0;
      int n2 = dirtyY1 - dirtyY0;
      dirtyX0 = 1024;
      dirtyY0 = 1024;
      dirtyX1 = 0;
      dirtyY1 = 0;
      GpuTexture texture = atlasTexture.getTexture();
      if (texture != null && (dstX != 0 || dstY != 0 || n != 1024 || n2 != 1024)) {
         org.zenith.utility.render.RenderCompat.writeRegion(texture, atlasImage, 0, 0, dstX, dstY, n, n2, dstX, dstY);
      } else {
         atlasTexture.upload();
      }
   }

   private static void resetAtlas() {
      if (builder != null) {
         flush();
      }

      GLYPHS.clear();
      atlasImage.fillRect(0, 0, 1024, 1024, 16777215);
      shelfX = 0;
      shelfY = 0;
      shelfHeight = 0;
      markDirty(0, 0, 1024, 1024);
   }

   private static void check(int n, String str) {
      if (n != 0) {
         throw new IllegalStateException(str + " failed: " + n);
      }
   }

   static {
      for (int i = 0; i < 256; i++) {
         GAMMA_LUT[i] = (byte)Math.round(255.0 * Math.pow(i / 255.0, 0.8F));
      }

      FACES = new HashMap<>();
      dirtyX0 = 1024;
      dirtyY0 = 1024;
      GLYPHS = new Long2ObjectOpenHashMap();
      batchShaderColor = new float[4];
      PLACEMENTS = new HashMap<>();
      OCCURRENCES = new Object2IntOpenHashMap();
   }

   private static final class Face {
      public FT_Face handle;
      public ByteBuffer data;
      public int id;
      public int sizeQuarter = -1;
   }

   private static final class Glyph {
      public static final HintedText.Glyph EMPTY = new HintedText.Glyph();
      public int width;
      public int height;
      public int left;
      public int top;
      public float u0;
      public float v0;
      public float u1;
      public float v1;
   }

   private static final class Placement {
      public float em;
      public long movedAt;
      public long seenAt;
   }
}
