package org.zenith.utility.render;

import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class TextureFilterUtils {
   private static final Set<Identifier> items = ConcurrentHashMap.newKeySet();
   private static final Map<GpuTextureView, AddressMode> items2 = Collections.synchronizedMap(new WeakHashMap<>());

   private TextureFilterUtils() {
   }

   public static void smooth(@Nullable Identifier location) {
      if (location != null) {
         items.add(location);
      }
   }

   public static FilterMode method7268(@Nullable Identifier identifier) {
      return identifier != null && items.contains(identifier) ? FilterMode.LINEAR : FilterMode.NEAREST;
   }

   @Nullable
   public static GpuTextureView method7269(@Nullable Identifier identifier) {
      return identifier == null ? null : method7270(Minecraft.getInstance().getTextureManager().getTexture(identifier));
   }

   @Nullable
   public static GpuTextureView method7270(@Nullable AbstractTexture texture) {
      if (texture == null) {
         return null;
      } else {
         GpuTextureView textureView = texture.getTextureView();
         items2.put(textureView, texture.getSampler().getAddressModeU());
         return textureView;
      }
   }

   public static AddressMode method7271(@Nullable GpuTextureView gpuTextureView) {
      AddressMode addressMode = gpuTextureView == null ? null : items2.get(gpuTextureView);
      return addressMode != null ? addressMode : AddressMode.CLAMP_TO_EDGE;
   }

   @Nullable
   public static GpuTextureView method7272(int n) {
      return null;
   }

   @Nullable
   public static GpuTextureView method7273(@Nullable GpuTextureView gpuTextureView) {
      return gpuTextureView;
   }
}
