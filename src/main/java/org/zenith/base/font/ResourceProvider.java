package org.zenith.base.font;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;

public final class ResourceProvider implements MinecraftAccess {
   private static final ResourceManager RESOURCE_MANAGER = mc.getResourceManager();
   private static final Gson GSON = new Gson();

   public static Identifier getShaderIdentifier(String str) {
      return Zenith.method1501("core/" + str);
   }

   public static <T> T fromJsonToInstance(Identifier dataIdentifer, Class<T> classOfT) {
      return (T)GSON.fromJson(toString(dataIdentifer), classOfT);
   }

   public static String toString(Identifier dataIdentifer) {
      return toString(dataIdentifer, "\n");
   }

   public static String toString(Identifier dataIdentifer, String delimiter) {
      try {
         String str;
         try (
            InputStream inputStream = RESOURCE_MANAGER.open(dataIdentifer);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         ) {
            str = bufferedReader.lines().collect(Collectors.joining(delimiter));
         }

         return str;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
