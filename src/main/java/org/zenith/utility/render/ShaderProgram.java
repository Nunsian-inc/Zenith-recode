package org.zenith.utility.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
   private final int intValue;
   private final Map<String, Integer> items = new HashMap<>();
   private final String text;
   private final String text2;
   private static int current;
   private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

   public ShaderProgram(String str, String anObject) {
      this(str, anObject, anObject);
   }

   public ShaderProgram(String str, String anObject, String anObject2) {
      this("/assets/gl/shaders/" + str + "/" + anObject2 + ".vsh", "/assets/gl/shaders/" + str + "/" + anObject + ".fsh", true);
   }

   public static ShaderProgram method5512(String text, String str) {
      return new ShaderProgram(text, str, true);
   }

   private ShaderProgram(String text, String str, boolean flag) {
      this.text = text;
      this.text2 = str;
      int shader = this.method5513(this.text, 35633);
      int n = this.method5513(this.text2, 35632);
      this.intValue = GL20.glCreateProgram();
      GL20.glAttachShader(this.intValue, shader);
      GL20.glAttachShader(this.intValue, n);
      GL20.glBindAttribLocation(this.intValue, 0, "Position");
      GL20.glLinkProgram(this.intValue);
      if (GL20.glGetProgrami(this.intValue, 35714) == 0) {
         throw new RuntimeException("Failed to link shader program: " + GL20.glGetProgramInfoLog(this.intValue));
      } else {
         GL20.glDeleteShader(shader);
         GL20.glDeleteShader(n);
      }
   }

   private int method5513(String text, int type) {
      try {
         String string = this.method5514(text);
         int glCreateShader = GL20.glCreateShader(type);
         GL20.glShaderSource(glCreateShader, string);
         GL20.glCompileShader(glCreateShader);
         if (GL20.glGetShaderi(glCreateShader, 35713) == 0) {
            throw new RuntimeException("Failed to compile shader: " + text + "\n" + GL20.glGetShaderInfoLog(glCreateShader));
         } else {
            return glCreateShader;
         }
      } catch (IOException e) {
         throw new RuntimeException("Failed to load shader: " + text, e);
      }
   }

   private String method5514(String text) throws IOException {
      return this.method5515(text, 0);
   }

   private String method5515(String text, int n) throws IOException {
      if (n > 8) {
         throw new IOException("Shader import loop: " + text);
      } else {
         InputStream resourceAsStream = ShaderProgram.class.getResourceAsStream(text);
         if (resourceAsStream == null) {
            throw new IOException("Shader file not found: " + text);
         } else {
            StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));

            try {
               String line;
               while ((line = bufferedReader.readLine()) != null) {
                  String str = method5516(line);
                  builder.append(str == null ? line : this.method5515(str, n + 1)).append("\n");
               }

               bufferedReader.close();
            } catch (Throwable e) {
               try {
                  bufferedReader.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            return builder.toString();
         }
      }
   }

   private static String method5516(String line) {
      String str = line.trim();
      if (!str.startsWith("#moj_import")) {
         return null;
      } else {
         int indexOf = str.indexOf(60);
         int endIndex = str.indexOf(62);
         if (indexOf >= 0 && endIndex >= indexOf) {
            String str2 = str.substring(indexOf + 1, endIndex);
            int n = str2.indexOf(58);
            String str3 = n < 0 ? "minecraft" : str2.substring(0, n);
            String str4 = n < 0 ? str2 : str2.substring(n + 1);
            return "/assets/" + str3 + "/shaders/include/" + str4;
         } else {
            return null;
         }
      }
   }

   public static int getCurrent() {
      return current;
   }

   public void method5517() {
      current = this.intValue;
      GL20.glUseProgram(this.intValue);
      FramebufferUtils.method5653();
   }

   public void unbind() {
      current = 0;
      GL20.glUseProgram(0);
      FramebufferUtils.method5653();
   }

   public void method5518(String key, int n) {
      int location = this.method5529(key);
      if (location != -1) {
         GL20.glUniform1i(location, n);
      }
   }

   public void method5519(String key, float previewAlpha) {
      int location = this.method5529(key);
      if (location != -1) {
         GL20.glUniform1f(location, previewAlpha);
      }
   }

   public void method5520(String key, FloatBuffer floatBuffer) {
      int location = this.method5529(key);
      if (location != -1) {
         GL20.glUniform1fv(location, floatBuffer);
      }
   }

   public void method5521(String key, float width, float height) {
      int location = this.method5529(key);
      if (location != -1) {
         GL20.glUniform2f(location, width, height);
      }
   }

   public void method5522(String key, Vector2f vec) {
      this.method5521(key, vec.x, vec.y);
   }

   public void method5523(String key, float f, float f2, float f3, float f4) {
      int location = this.method5529(key);
      if (location != -1) {
         GL20.glUniform4f(location, f, f2, f3, f4);
      }
   }

   public void method5524(String key, float x, float y, float z) {
      int location = this.method5529(key);
      if (location != -1) {
         GL20.glUniform3f(location, x, y, z);
      }
   }

   public void method5525(String key, Vector3f vec) {
      this.method5524(key, vec.x, vec.y, vec.z);
   }

   public void method5526(String key, boolean flag) {
      this.method5518(key, flag ? 1 : 0);
   }

   public void method5527(String key, Matrix4f matrix) {
      int location = this.method5529(key);
      if (location != -1) {
         matrix.get(floatBuffer);
         GL20.glUniformMatrix4fv(location, false, floatBuffer);
      }
   }

   public void method5528() {
      GL20.glDeleteProgram(this.intValue);
   }

   private int method5529(String key) {
      return this.items.computeIfAbsent(key, var1x -> GL20.glGetUniformLocation(this.intValue, var1x));
   }
}
