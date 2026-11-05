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
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft == null) {
            return true;
         } else {
            ResourceManager resourceManager = minecraft.getResourceManager();
            VanillaPackResources vanillaPackResources = minecraft.getVanillaPackResources();
            if (resourceManager != null && vanillaPackResources != null) {
               for (String text : texts) {
                  if (method1420(resourceManager, vanillaPackResources, text + ".vsh") || method1420(resourceManager, vanillaPackResources, text + ".fsh")) {
                     return true;
                  }
               }

               return false;
            } else {
               return true;
            }
         }
      } catch (Throwable e) {
         return true;
      }
   }

   private static boolean method1420(ResourceManager resourceManager, VanillaPackResources vanillaPackResources, String str) {
      var resource = resourceManager.getResource(Identifier.withDefaultNamespace("shaders/core/" + str));
      if (resource.isEmpty()) {
         return false;
      } else {
         PackResources source = ((Resource)resource.get()).source();
         return source != null && source != vanillaPackResources;
      }
   }
}
