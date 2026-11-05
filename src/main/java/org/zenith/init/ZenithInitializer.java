package org.zenith.init;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.zenith.Zenith;
import org.zenith.release.ReleaseStartup;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.LightUtil;
import org.zenith.utility.render.PostEffectManager;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.WardrobeFeatureRenderer;

public class ZenithInitializer implements ClientModInitializer {
   public void onInitializeClient() {
      ensureBaritoneSettingsExist();
      new Zenith();
      Zenith.getInstance().init();
      LivingEntityRenderLayerRegistrationCallback.EVENT.register((LivingEntityRenderLayerRegistrationCallback)(var0, var1, var2, var3) -> {
         if (var1 instanceof AvatarRenderer var4) {
            var2.register(new WardrobeFeatureRenderer(var4));
         }
      });
      ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
         public Identifier getFabricId() {
            return Zenith.method1501("after_shader_load");
         }

         public void onResourceManagerReload(ResourceManager var1) {
            LightUtil.invalidate();
            PostEffectManager.method2823();
            KawaseShaderKey.method5167();
         }
      });
      RenderShaders.method5255();
      drainEarlyGlfwError();
      ReleaseStartup.openLinks();
   }

   private static void drainEarlyGlfwError() {
      // deviation: 26.3 has no GLFW (SDL), there is no early GLFW error to drain
   }

   private static void ensureBaritoneSettingsExist() {
      try {
         Path var0 = Minecraft.getInstance().gameDirectory.toPath().resolve("baritone").resolve("settings.txt");
         if (Files.notExists(var0)) {
            Files.createDirectories(var0.getParent());
            Files.createFile(var0);
         }

         ensureSettingPresent(var0);
      } catch (IOException var1) {
      }
   }

   private static void ensureSettingPresent(Path var0) throws IOException {
      List<String> var1 = Files.readAllLines(var0);

      for (String var3 : var1) {
         if (var3.trim().equalsIgnoreCase("chunkCaching false")) {
            return;
         }
      }

      String var4 = var1.isEmpty() ? "" : System.lineSeparator();
      Files.writeString(var0, var4 + "chunkCaching false", StandardOpenOption.APPEND);
   }
}
