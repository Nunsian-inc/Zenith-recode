package org.zenith.addon.api.render;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public final class AddonRender {
   private static volatile AddonRenderBackend backend = new AddonRender.VanillaFallback();

   private AddonRender() {
   }

   public static void installBackend(AddonRenderBackend backend) {
      AddonRender.backend = Objects.requireNonNull(backend, "backend");
   }

   public static AddonFont msdfFont(String namespace, String basePath) {
      return AddonFont.msdf(namespace, basePath);
   }

   public static AddonFont msdfFont(Identifier data, Identifier atlas) {
      return AddonFont.msdf(data, atlas);
   }

   public static void prepareBlur(GuiGraphicsExtractor context, float strength) {
      backend.prepareBlur(Objects.requireNonNull(context, "context"), Math.max(0.0F, finiteOrZero(strength)));
   }

   public static void blur(GuiGraphicsExtractor context, float x, float y, float width, float height, float radius, int argb) {
      if (hasDrawableDimensions(width, height)) {
         backend.blur(
            Objects.requireNonNull(context, "context"), x, y, width, height, Math.max(0.0F, Math.min(finiteOrZero(radius), Math.min(width, height) / 2.0F)), argb
         );
      }
   }

   public static void rect(GuiGraphicsExtractor context, float x, float y, float width, float height, int argb) {
      if (hasDrawableDimensions(width, height)) {
         backend.rect(Objects.requireNonNull(context, "context"), x, y, width, height, argb);
      }
   }

   public static void roundedRect(GuiGraphicsExtractor context, float x, float y, float width, float height, float radius, int argb) {
      if (hasDrawableDimensions(width, height)) {
         backend.roundedRect(
            Objects.requireNonNull(context, "context"), x, y, width, height, Math.max(0.0F, Math.min(finiteOrZero(radius), Math.min(width, height) / 2.0F)), argb
         );
      }
   }

   public static void text(GuiGraphicsExtractor context, String text, float x, float y, float size, int argb) {
      backend.text(Objects.requireNonNull(context, "context"), Objects.requireNonNullElse(text, ""), x, y, Math.max(1.0F, finiteOrZero(size)), argb);
   }

   public static void text(GuiGraphicsExtractor context, AddonFont font, String text, float x, float y, float size, int argb) {
      backend.text(
         Objects.requireNonNull(context, "context"),
         Objects.requireNonNull(font, "font"),
         Objects.requireNonNullElse(text, ""),
         x,
         y,
         Math.max(1.0F, finiteOrZero(size)),
         argb
      );
   }

   public static void centeredText(GuiGraphicsExtractor context, String text, float centerX, float y, float size, int argb) {
      text(context, text, centerX - textWidth(text, size) / 2.0F, y, size, argb);
   }

   public static void centeredText(GuiGraphicsExtractor context, AddonFont font, String text, float centerX, float y, float size, int argb) {
      text(context, font, text, centerX - textWidth(font, text, size) / 2.0F, y, size, argb);
   }

   public static float textWidth(String text, float size) {
      return backend.textWidth(Objects.requireNonNullElse(text, ""), Math.max(1.0F, finiteOrZero(size)));
   }

   public static float textWidth(AddonFont font, String text, float size) {
      return backend.textWidth(Objects.requireNonNull(font, "font"), Objects.requireNonNullElse(text, ""), Math.max(1.0F, finiteOrZero(size)));
   }

   public static float textHeight(float size) {
      return backend.textHeight(Math.max(1.0F, finiteOrZero(size)));
   }

   public static float textHeight(AddonFont font, float size) {
      return backend.textHeight(Objects.requireNonNull(font, "font"), Math.max(1.0F, finiteOrZero(size)));
   }

   public static void pushClip(GuiGraphicsExtractor context, float x, float y, float width, float height) {
      if (hasDrawableDimensions(width, height)) {
         context.enableScissor((int)Math.floor(x), (int)Math.floor(y), (int)Math.ceil(x + width), (int)Math.ceil(y + height));
      }
   }

   public static void popClip(GuiGraphicsExtractor context) {
      Objects.requireNonNull(context, "context").disableScissor();
   }

   private static boolean hasDrawableDimensions(float width, float height) {
      return Float.isFinite(width) && Float.isFinite(height) && width > 0.0F && height > 0.0F;
   }

   private static float finiteOrZero(float value) {
      return Float.isFinite(value) ? value : 0.0F;
   }

   private static final class VanillaFallback implements AddonRenderBackend {
      @Override
      public void rect(GuiGraphicsExtractor context, float x, float y, float width, float height, int argb) {
         context.fill(floor(x), floor(y), ceil(x + width), ceil(y + height), argb);
      }

      @Override
      public void roundedRect(GuiGraphicsExtractor context, float x, float y, float width, float height, float radius, int argb) {
         int left = floor(x);
         int top = floor(y);
         int right = ceil(x + width);
         int bottom = ceil(y + height);
         int roundedRadius = Math.max(0, Math.round(radius));
         if (roundedRadius == 0) {
            context.fill(left, top, right, bottom, argb);
         } else {
            for (int row = 0; row < bottom - top; row++) {
               int distanceToEdge = Math.min(row, bottom - top - row - 1);
               int inset = 0;
               if (distanceToEdge < roundedRadius) {
                  double verticalDistance = roundedRadius - distanceToEdge - 0.5;
                  inset = (int)Math.ceil(roundedRadius - Math.sqrt(roundedRadius * roundedRadius - verticalDistance * verticalDistance));
               }

               context.fill(left + inset, top + row, right - inset, top + row + 1, argb);
            }
         }
      }

      @Override
      public void text(GuiGraphicsExtractor context, String text, float x, float y, float size, int argb) {
         Font renderer = Minecraft.getInstance().font;
         float scale = size / 9.0F;
         context.pose().pushMatrix();
         context.pose().translate(x, y);
         context.pose().scale(scale, scale);
         context.text(renderer, text, 0, 0, argb, false);
         context.pose().popMatrix();
      }

      @Override
      public float textWidth(String text, float size) {
         Font renderer = Minecraft.getInstance().font;
         return renderer.width(text) * size / 9.0F;
      }

      @Override
      public float textHeight(float size) {
         return size;
      }

      private static int floor(float value) {
         return (int)Math.floor(value);
      }

      private static int ceil(float value) {
         return (int)Math.ceil(value);
      }
   }
}
