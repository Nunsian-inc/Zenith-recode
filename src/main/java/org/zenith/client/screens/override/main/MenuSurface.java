package org.zenith.client.screens.override.main;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.render.KawaseBlur;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MenuSurface {
   private static final float BLUR = MenuTheme.px(72.0F);
   private static final float GLASS_SQUIRT = 10.0F;
   private static final float GLASS_POWER = 0.08F;
   private static MainMenuBlurRenderer backgroundBlur;
   private static GpuTextureView menuSource;

   public static void useBackgroundBlur(MainMenuBlurRenderer mainMenuBlurRenderer) {
      backgroundBlur = mainMenuBlurRenderer;
   }

   public static void clearBackgroundBlur() {
      backgroundBlur = null;
   }

   private MenuSurface() {
   }

   public static void card(MenuDrawContext graphics, float floatValue, float y, float width, float height, float intValue) {
      card(graphics, floatValue, y, width, height, intValue, MenuTheme.panel(), null);
   }

   public static void plate(MenuDrawContext menuDrawContext, float x, float y, float width, float height, float intValue, ZenithColor plate) {
      menuDrawContext.drawRoundedRect(x, y, width, height, ProxyDotRadiusData.method8069(intValue), plate);
   }

   public static void card(MenuDrawContext graphics, float floatValue, float y, float width, float height, float intValue, ZenithColor panel, ZenithColor border) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(intValue);
      blur(graphics, floatValue, y, width, height, headerRadius, 1.0F);
      graphics.drawRoundedRect(floatValue, y, width, height, headerRadius, panel);
      if (border != null) {
         graphics.drawRoundedBorder(floatValue, y, width, height, -0.1F, headerRadius, border);
      }
   }

   public static void setMenuSource(GpuTextureView texture) {
      menuSource = texture;
   }

   public static void blur(MenuDrawContext graphics, float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, float previewAlpha) {
      if (!(previewAlpha <= 0.01F) && !(width <= 0.0F) && !(height <= 0.0F)) {
         MsdfRenderer.flushBatch();
         ZenithColor zenithColor = ZenithColor.INSTANCE.method7294(previewAlpha);
         Matrix4f pose = MatrixUtils.method664(graphics.pose());
         if (Minecraft.getInstance().level == null && menuSource != null) {
            RenderShaders.gpuTextureView = menuSource;

            try {
               RenderShaders.method5264(pose, floatValue, y, width, height, 10.0F, 0.08F, headerRadius, zenithColor);
            } finally {
               RenderShaders.gpuTextureView = null;
            }
         } else if (backgroundBlur != null) {
            backgroundBlur.render(pose, floatValue, y, width, height, headerRadius, zenithColor);
         } else {
            KawaseBlur kawaseBlur = Zenith.getInstance().getKawaseBlur();
            if (kawaseBlur != null) {
               if (Minecraft.getInstance().level != null) {
                  kawaseBlur.method7353(pose, floatValue, y, width, height, headerRadius, zenithColor);
               } else {
                  kawaseBlur.method7355(pose, floatValue, y, width, height, headerRadius, zenithColor);
               }
            }
         }
      }
   }

   public static void backdrop(MenuDrawContext graphics, float guiWidth, float guiHeight, float previewAlpha) {
      blur(graphics, 0.0F, 0.0F, guiWidth, guiHeight, ProxyDotRadiusData.INSTANCE, 1.0F);
      if (previewAlpha > 0.0F) {
         graphics.drawRect(0.0F, 0.0F, guiWidth, guiHeight, ZenithColor.INSTANCE2.method7294(previewAlpha));
      }
   }

   public static void glow(MenuDrawContext graphics, float x, float y, float width, float height, float radius, ZenithColor accent, float previewAlpha) {
      if (!(previewAlpha <= 0.01F)) {
         ShapeRenderer.method7167(
            MatrixUtils.method664(graphics.pose()),
            x,
            y,
            width,
            height,
            (int)MenuTheme.px(24.0F),
            ProxyDotRadiusData.method8069(radius),
            GlowColor.method8097(accent.method7294(previewAlpha))
         );
      }
   }
}
