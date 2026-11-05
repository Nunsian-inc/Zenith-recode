package org.zenith.utility.misc;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class GlyphShaderNames {
   public static final int CONST_intValue = 256;
   public static final int CONST_intValue2 = 1024;
   private static final String[] texts = new String[]{
      "position_color",
      "position_tex",
      "position_tex_color",
      "rendertype_text",
      "rendertype_text_background",
      "rendertype_text_background_see_through",
      "rendertype_text_intensity",
      "rendertype_text_intensity_see_through",
      "rendertype_text_see_through",
      "rendertype_item_entity_translucent_cull"
   };

   private GlyphShaderNames() {
   }

   public static int method1418() {
      if (!OptimizationFlags.flag17) {
         return 256;
      } else {
         return method1419() ? 256 : 1024;
      }
   }

   private static boolean method1419() {
      try {
         Minecraft var0 = Minecraft.getInstance();
         if (var0 == null) {
            return true;
         } else {
            ResourceManager var1 = var0.getResourceManager();
            VanillaPackResources var2 = var0.getVanillaPackResources();
            if (var1 != null && var2 != null) {
               for (String var6 : texts) {
                  if (method1420(var1, var2, var6 + ".vsh") || method1420(var1, var2, var6 + ".fsh")) {
                     return true;
                  }
               }

               return false;
            } else {
               return true;
            }
         }
      } catch (Throwable var7) {
         return true;
      }
   }

   private static boolean method1420(ResourceManager var0, VanillaPackResources var1, String var2) {
      var var3 = var0.getResource(Identifier.withDefaultNamespace("shaders/core/" + var2));
      if (var3.isEmpty()) {
         return false;
      } else {
         PackResources var4 = ((Resource)var3.get()).source();
         return var4 != null && var4 != var1;
      }
   }
}
