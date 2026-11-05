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
      LivingEntityRenderLayerRegistrationCallback.EVENT.register((LivingEntityRenderLayerRegistrationCallback)(entityType, livingEntityRenderer, registrationHelper, context) -> {
         if (livingEntityRenderer instanceof AvatarRenderer renderer) {
            registrationHelper.register(new WardrobeFeatureRenderer(renderer));
         }
      });
      ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
         public Identifier getFabricId() {
            return Zenith.method1501("after_shader_load");
         }

         public void onResourceManagerReload(ResourceManager resourceManager) {
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
         Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("baritone").resolve("settings.txt");
         if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
         }

         ensureSettingPresent(path);
      } catch (IOException e) {
      }
   }

   private static void ensureSettingPresent(Path path) throws IOException {
      List<String> allLines = Files.readAllLines(path);

      for (String allLine : allLines) {
         if (allLine.trim().equalsIgnoreCase("chunkCaching false")) {
            return;
         }
      }

      String lineSeparator = allLines.isEmpty() ? "" : System.lineSeparator();
      Files.writeString(path, lineSeparator + "chunkCaching false", StandardOpenOption.APPEND);
   }
}
