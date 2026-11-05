package org.zenith.utility.render.display.base;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.HudQueuedDraw;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;

public final class HudQueuedContext extends MenuDrawContext {
   private boolean replaying;
   private static float alpha = 1.0F;
   private static final Matrix4f QUEUE_MATRIX = new Matrix4f();

   public static void setAlpha(float f) {
      alpha = Math.max(0.0F, Math.min(1.0F, f));
   }

   public static float getAlpha() {
      return alpha;
   }

   private static ZenithColor tint(ZenithColor zenithColor) {
      return !(alpha >= 0.999F) && zenithColor != null ? zenithColor.method7294(alpha) : zenithColor;
   }

   private static GlowColor tint(GlowColor glowColor) {
      return !(alpha >= 0.999F) && glowColor != null ? glowColor.method8100(alpha) : glowColor;
   }

   private static int tint(int color) {
      if (alpha >= 0.999F) {
         return color;
      } else {
         int n = Math.round((color >>> 24 & 0xFF) * alpha);
         return n << 24 | color & 16777215;
      }
   }

   private static boolean itemsHidden() {
      return alpha < 0.5F;
   }

   private HudQueuedContext(MenuDrawContext menuDrawContext) {
      super(menuDrawContext, menuDrawContext.getMouseX(), menuDrawContext.getMouseY(), menuDrawContext.getDelta());
   }

   public static HudQueuedContext of(MenuDrawContext menuDrawContext) {
      return menuDrawContext instanceof HudQueuedContext hudQueuedContext ? hudQueuedContext : new HudQueuedContext(menuDrawContext);
   }

   private HudQueuedDraw queueCommand(HudQueuedDraw.Type type) {
      HudQueuedDraw hudQueuedDraw = Jebriksheet.method2355(this);
      hudQueuedDraw.type = type;
      MatrixUtils.method665(this.pose(), hudQueuedDraw.matrix4f);
      return hudQueuedDraw;
   }

   private Matrix4f queueMatrix() {
      return MatrixUtils.method665(this.pose(), QUEUE_MATRIX);
   }

   public void replayCommand(HudQueuedDraw hudQueuedDraw) {
      Matrix3x2fStack pose = this.pose();
      boolean replaying = this.replaying;
      pose.pushMatrix();
      MatrixUtils.method669(pose, hudQueuedDraw.matrix4f);
      this.replaying = true;

      try {
         this.dispatch(hudQueuedDraw);
      } finally {
         this.replaying = replaying;
         pose.popMatrix();
      }
   }

   private void dispatch(HudQueuedDraw hudQueuedDraw) {
      switch (hudQueuedDraw.type) {
         case TEXT_FONT_STRING_RGBA:
            super.drawText((Font)hudQueuedDraw.obj, (String)hudQueuedDraw.obj2, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, (ZenithColor)hudQueuedDraw.obj3);
            break;
         case TEXT_FONT_STRING_RGBA_FADEOUT:
            super.drawText(
               (Font)hudQueuedDraw.obj,
               (String)hudQueuedDraw.obj2,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               (ZenithColor)hudQueuedDraw.obj3,
               hudQueuedDraw.flag,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               hudQueuedDraw.floatValue5
            );
            break;
         case TEXT_FONT_STRING_INT_FADEOUT:
            super.drawText(
               (Font)hudQueuedDraw.obj,
               (String)hudQueuedDraw.obj2,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.intValue,
               hudQueuedDraw.flag,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               hudQueuedDraw.floatValue5
            );
            break;
         case TEXT_FONT_STRING_GRADIENT:
            super.drawText((Font)hudQueuedDraw.obj, (String)hudQueuedDraw.obj2, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, (GlowColor)hudQueuedDraw.obj3);
            break;
         case TEXT_FONT_TEXT_INT:
            super.drawText((Font)hudQueuedDraw.obj, (Component)hudQueuedDraw.obj2, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.intValue);
            break;
         case TEXT_FONT_TEXT:
            super.drawText((Font)hudQueuedDraw.obj, (Component)hudQueuedDraw.obj2, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2);
            break;
         case TEXT_SHADOW_STRING:
            super.text((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (String)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case TEXT_STRING:
            super.text((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (String)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.flag);
            break;
         case TEXT_SHADOW_ORDERED:
            super.text((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (FormattedCharSequence)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case TEXT_ORDERED:
            super.text((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (FormattedCharSequence)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.flag);
            break;
         case TEXT_SHADOW_TEXT:
            super.text((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (Component)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case TEXT_TEXT:
            super.text((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (Component)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.flag);
            break;
         case WRAPPED_TEXT:
            super.textWithWordWrap((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (FormattedText)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.flag);
            break;
         case STACK_OVERLAY:
            super.itemDecorations((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (ItemStack)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2);
            break;
         case STACK_OVERLAY_TEXT:
            super.itemDecorations((net.minecraft.client.gui.Font)hudQueuedDraw.obj, (ItemStack)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, (String)hudQueuedDraw.obj3);
            break;
         case SPRITE:
            super.drawSprite((CategoryIcon)hudQueuedDraw.obj, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, (ZenithColor)hudQueuedDraw.obj2);
            break;
         case ROUNDED_RECT_GRADIENT:
            super.drawRoundedRect(hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, (ProxyDotRadiusData)hudQueuedDraw.obj, (GlowColor)hudQueuedDraw.obj2);
            break;
         case SQUIRCLE:
            super.drawSquircle(
               hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, hudQueuedDraw.floatValue5, (ProxyDotRadiusData)hudQueuedDraw.obj, (ZenithColor)hudQueuedDraw.obj2
            );
            break;
         case ROUNDED_BORDER_GRADIENT:
            super.drawRoundedBorder(
               hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, hudQueuedDraw.floatValue5, (ProxyDotRadiusData)hudQueuedDraw.obj, (GlowColor)hudQueuedDraw.obj2
            );
            break;
         case ROUNDED_BORDER_RGBA:
            super.drawRoundedBorder(
               hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, hudQueuedDraw.floatValue5, (ProxyDotRadiusData)hudQueuedDraw.obj, (ZenithColor)hudQueuedDraw.obj2
            );
            break;
         case ROUNDED_CORNER_ONLY:
            super.drawRoundedCornerOnly(
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               hudQueuedDraw.floatValue5,
               (ProxyDotRadiusData)hudQueuedDraw.obj,
               (ZenithColor)hudQueuedDraw.obj2,
               hudQueuedDraw.floatValue6
            );
            break;
         case TEXTURE_RGBA:
            super.drawTexture((Identifier)hudQueuedDraw.obj, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, (ZenithColor)hudQueuedDraw.obj2);
            break;
         case ARC_BORDER:
            super.drawArcBorder(
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               hudQueuedDraw.floatValue5,
               hudQueuedDraw.floatValue6,
               hudQueuedDraw.floatValue7,
               (ZenithColor)hudQueuedDraw.obj
            );
            break;
         case ROUNDED_TEXTURE:
            super.drawRoundedTexture(
               (Identifier)hudQueuedDraw.obj,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               (ProxyDotRadiusData)hudQueuedDraw.obj2,
               (ZenithColor)hudQueuedDraw.obj3
            );
            break;
         case ROUNDED_TEXTURE_RGBA:
            super.drawRoundedTexture(
               (Identifier)hudQueuedDraw.obj,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               (ProxyDotRadiusData)hudQueuedDraw.obj2,
               (ZenithColor)hudQueuedDraw.obj3
            );
            break;
         case PLAYER_HEAD:
            super.drawPlayerHeadWithRoundedShader(
               (Identifier)hudQueuedDraw.obj, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, (ProxyDotRadiusData)hudQueuedDraw.obj2, (ZenithColor)hudQueuedDraw.obj3
            );
            break;
         case DRAW:
            ScissorFlusher.flush();
            break;
         case FILL_GRADIENT:
            super.fillGradient(hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue5, hudQueuedDraw.intValue6);
            break;
         case FILL_GRADIENT_Z:
            super.fillGradient(hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue6, hudQueuedDraw.intValue7);
            break;
         case FILL_GRADIENT_LAYER:
            super.fillGradient(hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue5, hudQueuedDraw.intValue6);
            break;
         case SPRITE_STRETCHED:
            super.blitSprite(
               (RenderPipeline)hudQueuedDraw.obj, (TextureAtlasSprite)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue5
            );
            break;
         case SPRITE_STRETCHED_COLOR:
            super.blitSprite(
               (RenderPipeline)hudQueuedDraw.obj, (TextureAtlasSprite)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue5
            );
            break;
         case GUI_TEXTURE:
            super.blitSprite((RenderPipeline)hudQueuedDraw.obj, (Identifier)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue5);
            break;
         case GUI_TEXTURE_COLOR:
            super.blitSprite((RenderPipeline)hudQueuedDraw.obj, (Identifier)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4, hudQueuedDraw.intValue5);
            break;
         case GUI_TEXTURE_REGION:
            super.blitSprite(
               (RenderPipeline)hudQueuedDraw.obj,
               (Identifier)hudQueuedDraw.obj2,
               hudQueuedDraw.intValue,
               hudQueuedDraw.intValue2,
               hudQueuedDraw.intValue3,
               hudQueuedDraw.intValue4,
               hudQueuedDraw.intValue5,
               hudQueuedDraw.intValue6,
               hudQueuedDraw.intValue7,
               hudQueuedDraw.intValue8,
               hudQueuedDraw.intValue9
            );
            break;
         case TEXTURE_UV:
            super.blit(
               (RenderPipeline)hudQueuedDraw.obj,
               (Identifier)hudQueuedDraw.obj2,
               hudQueuedDraw.intValue,
               hudQueuedDraw.intValue2,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.intValue3,
               hudQueuedDraw.intValue4,
               hudQueuedDraw.intValue5,
               hudQueuedDraw.intValue6,
               hudQueuedDraw.intValue7
            );
            break;
         case TEXTURE_UV_COLOR:
            super.blit(
               (RenderPipeline)hudQueuedDraw.obj,
               (Identifier)hudQueuedDraw.obj2,
               hudQueuedDraw.intValue,
               hudQueuedDraw.intValue2,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.intValue3,
               hudQueuedDraw.intValue4,
               hudQueuedDraw.intValue5,
               hudQueuedDraw.intValue6,
               hudQueuedDraw.intValue7
            );
            break;
         case TEXTURE_REGION:
            super.blit(
               (RenderPipeline)hudQueuedDraw.obj,
               (Identifier)hudQueuedDraw.obj2,
               hudQueuedDraw.intValue,
               hudQueuedDraw.intValue2,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.intValue3,
               hudQueuedDraw.intValue4,
               hudQueuedDraw.intValue5,
               hudQueuedDraw.intValue6,
               hudQueuedDraw.intValue7,
               hudQueuedDraw.intValue8,
               hudQueuedDraw.intValue9
            );
            break;
         case TEXTURE_REGION_COLOR:
            super.blit(
               (RenderPipeline)hudQueuedDraw.obj,
               (Identifier)hudQueuedDraw.obj2,
               hudQueuedDraw.intValue,
               hudQueuedDraw.intValue2,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.intValue3,
               hudQueuedDraw.intValue4,
               hudQueuedDraw.intValue5,
               hudQueuedDraw.intValue6,
               hudQueuedDraw.intValue7,
               hudQueuedDraw.intValue8,
               hudQueuedDraw.intValue9
            );
            break;
         case ITEM:
            super.item((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2);
            break;
         case ITEM_BAR:
            super.renderItemBar((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2);
            break;
         case COOLDOWN_PROGRESS:
            super.renderItemCooldown((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2);
            break;
         case ITEM_SEED:
            super.item((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case ITEM_SEED_Z:
            super.item((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case ITEM_WITHOUT_ENTITY:
            super.fakeItem((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2);
            break;
         case ITEM_WITHOUT_ENTITY_SEED:
            super.fakeItem((ItemStack)hudQueuedDraw.obj, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case ITEM_ENTITY:
            super.item((LivingEntity)hudQueuedDraw.obj, (ItemStack)hudQueuedDraw.obj2, hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3);
            break;
         case SCISSOR_ENABLE:
            super.enableScissor(hudQueuedDraw.intValue, hudQueuedDraw.intValue2, hudQueuedDraw.intValue3, hudQueuedDraw.intValue4);
            break;
         case SCISSOR_DISABLE:
            super.disableScissor();
      }
   }

   @Override
   public void drawText(Font font, String text, float floatValue, float centeredTextY, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawText(font, text, floatValue, centeredTextY, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_RGBA);
         queueCommand.obj = font;
         queueCommand.obj2 = text;
         queueCommand.obj3 = tint(zenithColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = centeredTextY;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void drawText(Font font, String name, float floatValue, float centeredTextY, ZenithColor zenithColor, boolean flag, float f, float f2, float maxWidth) {
      if (this.replaying) {
         super.drawText(font, name, floatValue, centeredTextY, zenithColor, flag, f, f2, maxWidth);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_RGBA_FADEOUT);
         queueCommand.obj = font;
         queueCommand.obj2 = name;
         queueCommand.obj3 = tint(zenithColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = centeredTextY;
         queueCommand.flag = flag;
         queueCommand.floatValue3 = f;
         queueCommand.floatValue4 = f2;
         queueCommand.floatValue5 = maxWidth;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void drawText(Font font, String name, float floatValue, float y, int intValue, boolean flag, float f, float f2, float maxWidth) {
      if (this.replaying) {
         super.drawText(font, name, floatValue, y, intValue, flag, f, f2, maxWidth);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_INT_FADEOUT);
         queueCommand.obj = font;
         queueCommand.obj2 = name;
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.intValue = tint(intValue);
         queueCommand.flag = flag;
         queueCommand.floatValue3 = f;
         queueCommand.floatValue4 = f2;
         queueCommand.floatValue5 = maxWidth;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void drawText(Font font, String text, float floatValue, float centeredTextY, GlowColor glowColor) {
      if (this.replaying) {
         super.drawText(font, text, floatValue, centeredTextY, glowColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_GRADIENT);
         queueCommand.obj = font;
         queueCommand.obj2 = text;
         queueCommand.obj3 = tint(glowColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = centeredTextY;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void drawText(Font font, Component text, float floatValue, float y, int color) {
      if (this.replaying) {
         super.drawText(font, text, floatValue, y, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_TEXT_INT);
         queueCommand.obj = font;
         queueCommand.obj2 = text;
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.intValue = tint(color);
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void drawText(Font font, Component text, float floatValue, float y) {
      if (this.replaying) {
         super.drawText(font, text, floatValue, y);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_TEXT);
         queueCommand.obj = font;
         queueCommand.obj2 = text;
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      if (this.replaying) {
         super.enableScissor(x1, y1, x2, y2);
      } else {
         Jebriksheet.method2361(this, this.queueMatrix(), x1, y1, x2, y2);
      }
   }

   @Override
   public void disableScissor() {
      if (this.replaying) {
         super.disableScissor();
      } else {
         Jebriksheet.method2362();
      }
   }

   @Override
   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawRoundedRect(floatValue, y, width, height, headerRadius, zenithColor);
      } else {
         Jebriksheet.method2364(this.queueMatrix(), floatValue, y, width, height, headerRadius, tint(zenithColor));
      }
   }

   @Override
   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      if (this.replaying) {
         super.drawRoundedRect(floatValue, y, width, height, proxyDotRadiusData, glowColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ROUNDED_RECT_GRADIENT);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         queueCommand.obj = proxyDotRadiusData;
         queueCommand.obj2 = tint(glowColor);
         Jebriksheet.method2356(queueCommand);
      }
   }

   @Override
   public void drawRect(float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawRect(floatValue, y, width, height, zenithColor);
      } else {
         Jebriksheet.method2365(this.queueMatrix(), floatValue, y, width, height, tint(zenithColor));
      }
   }

   @Override
   public void drawSquircle(float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawSquircle(floatValue, y, width, height, batchRange, proxyDotRadiusData, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.SQUIRCLE);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         queueCommand.floatValue5 = batchRange;
         queueCommand.obj = proxyDotRadiusData;
         queueCommand.obj2 = tint(zenithColor);
         Jebriksheet.method2356(queueCommand);
      }
   }

   @Override
   public void drawSprite(CategoryIcon categoryIcon, float floatValue, float y, float f, float f2, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawSprite(categoryIcon, floatValue, y, f, f2, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.SPRITE);
         queueCommand.obj = categoryIcon;
         queueCommand.obj2 = tint(zenithColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = f;
         queueCommand.floatValue4 = f2;
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawRoundedBorder(float floatValue, float lastY, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      if (this.replaying) {
         super.drawRoundedBorder(floatValue, lastY, width, height, batchRange, proxyDotRadiusData, glowColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ROUNDED_BORDER_GRADIENT);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = lastY;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         queueCommand.floatValue5 = batchRange;
         queueCommand.obj = proxyDotRadiusData;
         queueCommand.obj2 = tint(glowColor);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawRoundedBorder(float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData moduleMenuRadius, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawRoundedBorder(floatValue, y, width, height, batchRange, moduleMenuRadius, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ROUNDED_BORDER_RGBA);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         queueCommand.floatValue5 = batchRange;
         queueCommand.obj = moduleMenuRadius;
         queueCommand.obj2 = tint(zenithColor);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawRoundedCornerOnly(float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f) {
      if (this.replaying) {
         super.drawRoundedCornerOnly(floatValue, y, width, height, batchRange, proxyDotRadiusData, zenithColor, f);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ROUNDED_CORNER_ONLY);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         queueCommand.floatValue5 = batchRange;
         queueCommand.floatValue6 = f;
         queueCommand.obj = proxyDotRadiusData;
         queueCommand.obj2 = tint(zenithColor);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawTexture(Identifier identifier, float floatValue, float y, float guiScaledWidth, float guiScaledHeight, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawTexture(identifier, floatValue, y, guiScaledWidth, guiScaledHeight, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXTURE_RGBA);
         queueCommand.obj = identifier;
         queueCommand.obj2 = tint(zenithColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = guiScaledWidth;
         queueCommand.floatValue4 = guiScaledHeight;
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawBlurHud(float floatValue, float y, float width, float height, float blurPower, ProxyDotRadiusData moduleMenuRadius, ZenithColor zenithColor) {
      this.drawBlurHudBooleanCheck(
         floatValue,
         y,
         width,
         height,
         blurPower,
         moduleMenuRadius,
         zenithColor,
         (InterfaceModule.INSTANCE.method6817() || InterfaceModule.INSTANCE.method6818()) && RenderShaders.flag,
         InterfaceModule.INSTANCE.method6819()
      );
   }

   @Override
   public void drawBlurHudBooleanCheck(
      float floatValue, float y, float width, float height, float blurPower, ProxyDotRadiusData moduleMenuRadius, ZenithColor zenithColor, boolean flag, boolean flag2
   ) {
      if (this.replaying) {
         super.drawBlurHudBooleanCheck(floatValue, y, width, height, blurPower, moduleMenuRadius, zenithColor, flag, flag2);
      } else {
         float f = InterfaceModule.INSTANCE.method6779(blurPower);
         Jebriksheet.method2363(this.queueMatrix(), floatValue, y, width, height, f > 0.0F ? f : blurPower, moduleMenuRadius, tint(zenithColor), flag && f > 0.0F, flag2);
         if (InterfaceModule.INSTANCE.method6780()) {
            this.drawRoundedBorder(floatValue, y, width, height, InterfaceModule.INSTANCE.method6781(), moduleMenuRadius, InterfaceModule.INSTANCE.method6782());
         }
      }
   }

   @Override
   public void drawArcBorder(float floatValue, float y, float width, float height, float batchRange, float f, float f2, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawArcBorder(floatValue, y, width, height, batchRange, f, f2, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ARC_BORDER);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         queueCommand.floatValue5 = batchRange;
         queueCommand.floatValue6 = f;
         queueCommand.floatValue7 = f2;
         queueCommand.obj = tint(zenithColor);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawRoundedTexture(Identifier prewarm, float floatValue, float y, float width, float height, ProxyDotRadiusData avatarRadius) {
      if (this.replaying) {
         super.drawRoundedTexture(prewarm, floatValue, y, width, height, avatarRadius);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ROUNDED_TEXTURE);
         queueCommand.obj = prewarm;
         queueCommand.obj2 = avatarRadius;
         queueCommand.obj3 = tint(ZenithColor.INSTANCE);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         queueCommand.floatValue4 = height;
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawRoundedTexture(Identifier texture, float floatValue, float cardAvatarY, float size, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawRoundedTexture(texture, floatValue, cardAvatarY, size, height, headerRadius, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ROUNDED_TEXTURE_RGBA);
         queueCommand.obj = texture;
         queueCommand.obj2 = headerRadius;
         queueCommand.obj3 = tint(zenithColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = cardAvatarY;
         queueCommand.floatValue3 = size;
         queueCommand.floatValue4 = height;
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void drawPlayerHeadWithRoundedShader(Identifier texturePath, float floatValue, float y, float width, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawPlayerHeadWithRoundedShader(texturePath, floatValue, y, width, headerRadius, zenithColor);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.PLAYER_HEAD);
         queueCommand.obj = texturePath;
         queueCommand.obj2 = headerRadius;
         queueCommand.obj3 = tint(zenithColor);
         queueCommand.floatValue = floatValue;
         queueCommand.floatValue2 = y;
         queueCommand.floatValue3 = width;
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void item(ItemStack item, int x, int y) {
      if (this.replaying) {
         super.item(item, x, y);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM);
         queueCommand.obj = item;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void renderItemBar(ItemStack itemStack, int intValue, int n) {
      if (this.replaying) {
         super.renderItemBar(itemStack, intValue, n);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM_BAR);
         queueCommand.obj = itemStack;
         queueCommand.intValue = intValue;
         queueCommand.intValue2 = n;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void renderItemCooldown(ItemStack itemStack, int intValue, int n) {
      if (this.replaying) {
         super.renderItemCooldown(itemStack, intValue, n);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.COOLDOWN_PROGRESS);
         queueCommand.obj = itemStack;
         queueCommand.intValue = intValue;
         queueCommand.intValue2 = n;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void item(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.item(stack, x, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM_SEED);
         queueCommand.obj = stack;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = seed;
         Jebriksheet.method2358(queueCommand);
      }
   }

   public void drawItem(ItemStack itemStack, int intValue, int y, int seed, int n) {
      if (this.replaying) {
         super.item(itemStack, intValue, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM_SEED_Z);
         queueCommand.obj = itemStack;
         queueCommand.intValue = intValue;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = seed;
         queueCommand.intValue4 = n;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void fakeItem(ItemStack stack, int x, int y) {
      if (this.replaying) {
         super.fakeItem(stack, x, y);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM_WITHOUT_ENTITY);
         queueCommand.obj = stack;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void fakeItem(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.fakeItem(stack, x, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM_WITHOUT_ENTITY_SEED);
         queueCommand.obj = stack;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = seed;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void item(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.item(entity, stack, x, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.ITEM_ENTITY);
         queueCommand.obj = entity;
         queueCommand.obj2 = stack;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = seed;
         Jebriksheet.method2358(queueCommand);
      }
   }

   @Override
   public void itemDecorations(net.minecraft.client.gui.Font textRenderer, ItemStack stack, int x, int y) {
      if (this.replaying) {
         super.itemDecorations(textRenderer, stack, x, y);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.STACK_OVERLAY);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = stack;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void itemDecorations(net.minecraft.client.gui.Font textRenderer, ItemStack stack, int x, int y, String stackCountText) {
      if (this.replaying) {
         super.itemDecorations(textRenderer, stack, x, y, stackCountText);
      } else if (!itemsHidden()) {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.STACK_OVERLAY_TEXT);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = stack;
         queueCommand.obj3 = stackCountText;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void draw() {
      if (this.replaying) {
         ScissorFlusher.flush();
      } else {
         Jebriksheet.method2357(this.queueCommand(HudQueuedDraw.Type.DRAW));
      }
   }

   @Override
   public void fill(int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(int x0, int y0, int x1, int y1, int n, int col) {
      if (this.replaying) {
         super.fill(x0, y0, x1, y1, col);
      } else {
         this.queueRect(x0, y0, x1, y1, col);
      }
   }

   @Override
   public void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(pipeline, x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(RenderType renderType, int x0, int y0, int x1, int y1, int n, int col) {
      if (this.replaying) {
         super.fill(x0, y0, x1, y1, col);
      } else {
         this.queueRect(x0, y0, x1, y1, col);
      }
   }

   private void queueRect(int x1, int y1, int x2, int y2, int color) {
      int floatValue = Math.min(x1, x2);
      int y = Math.min(y1, y2);
      int n = Math.max(x1, x2);
      int n2 = Math.max(y1, y2);
      Jebriksheet.method2365(this.queueMatrix(), floatValue, y, n - floatValue, n2 - y, tint(ZenithColor.method7291(color)));
   }

   @Override
   public void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
      if (this.replaying) {
         super.fillGradient(startX, startY, endX, endY, colorStart, colorEnd);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.FILL_GRADIENT);
         queueCommand.intValue = startX;
         queueCommand.intValue2 = startY;
         queueCommand.intValue3 = endX;
         queueCommand.intValue4 = endY;
         queueCommand.intValue5 = tint(colorStart);
         queueCommand.intValue6 = tint(colorEnd);
         Jebriksheet.method2357(queueCommand);
      }
   }

   public void fillGradient(int intValue, int y0, int x1, int y1, int n, int col1, int col2) {
      if (this.replaying) {
         super.fillGradient(intValue, y0, x1, y1, col1, col2);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.FILL_GRADIENT_Z);
         queueCommand.intValue = intValue;
         queueCommand.intValue2 = y0;
         queueCommand.intValue3 = x1;
         queueCommand.intValue4 = y1;
         queueCommand.intValue5 = n;
         queueCommand.intValue6 = tint(col1);
         queueCommand.intValue7 = tint(col2);
         Jebriksheet.method2357(queueCommand);
      }
   }

   public void fillGradient(RenderType renderType, int intValue, int y0, int x1, int y1, int col1, int col2, int n) {
      if (this.replaying) {
         super.fillGradient(intValue, y0, x1, y1, col1, col2);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.FILL_GRADIENT_LAYER);
         queueCommand.obj = renderType;
         queueCommand.intValue = intValue;
         queueCommand.intValue2 = y0;
         queueCommand.intValue3 = x1;
         queueCommand.intValue4 = y1;
         queueCommand.intValue5 = tint(col1);
         queueCommand.intValue6 = tint(col2);
         queueCommand.intValue7 = n;
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void text(net.minecraft.client.gui.Font textRenderer, String text, int x, int y, int color) {
      if (this.replaying) {
         super.text(textRenderer, text, x, y, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_SHADOW_STRING);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = tint(color);
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void text(net.minecraft.client.gui.Font textRenderer, String text, int x, int y, int color, boolean shadow) {
      if (this.replaying) {
         super.text(textRenderer, text, x, y, color, shadow);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_STRING);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = tint(color);
         queueCommand.flag = shadow;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void text(net.minecraft.client.gui.Font textRenderer, FormattedCharSequence text, int x, int y, int color) {
      if (this.replaying) {
         super.text(textRenderer, text, x, y, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_SHADOW_ORDERED);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = tint(color);
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void text(net.minecraft.client.gui.Font textRenderer, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
      if (this.replaying) {
         super.text(textRenderer, text, x, y, color, shadow);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_ORDERED);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = tint(color);
         queueCommand.flag = shadow;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void text(net.minecraft.client.gui.Font textRenderer, Component text, int x, int y, int color) {
      if (this.replaying) {
         super.text(textRenderer, text, x, y, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_SHADOW_TEXT);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = tint(color);
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public void text(net.minecraft.client.gui.Font textRenderer, Component text, int x, int y, int color, boolean shadow) {
      if (this.replaying) {
         super.text(textRenderer, text, x, y, color, shadow);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXT_TEXT);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = tint(color);
         queueCommand.flag = shadow;
         Jebriksheet.method2359(queueCommand);
      }
   }

   @Override
   public int textWithWordWrap(net.minecraft.client.gui.Font textRenderer, FormattedText text, int x, int y, int width, int color, boolean shadow) {
      if (this.replaying) {
         return super.textWithWordWrap(textRenderer, text, x, y, width, color, shadow);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.WRAPPED_TEXT);
         queueCommand.obj = textRenderer;
         queueCommand.obj2 = text;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = tint(color);
         queueCommand.flag = shadow;
         Jebriksheet.method2359(queueCommand);
         // 26.1: returns the y below the last line, like vanilla
         return y + textRenderer.split(text, width).size() * textRenderer.lineHeight;
      }
   }

   @Override
   public void blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int x, int y, int width, int height) {
      if (this.replaying) {
         super.blitSprite(pipeline, sprite, x, y, width, height);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.SPRITE_STRETCHED);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = tint(-1);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blitSprite(RenderPipeline pipeline, TextureAtlasSprite sprite, int x, int y, int width, int height, int color) {
      if (this.replaying) {
         super.blitSprite(pipeline, sprite, x, y, width, height, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.SPRITE_STRETCHED_COLOR);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = tint(color);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blitSprite(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
      if (this.replaying) {
         super.blitSprite(pipeline, sprite, x, y, width, height);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.GUI_TEXTURE);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = tint(-1);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blitSprite(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color) {
      if (this.replaying) {
         super.blitSprite(pipeline, sprite, x, y, width, height, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.GUI_TEXTURE_COLOR);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = tint(color);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blitSprite(
      RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height
   ) {
      if (this.replaying) {
         super.blitSprite(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.GUI_TEXTURE_REGION);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = textureWidth;
         queueCommand.intValue2 = textureHeight;
         queueCommand.intValue3 = u;
         queueCommand.intValue4 = v;
         queueCommand.intValue5 = x;
         queueCommand.intValue6 = y;
         queueCommand.intValue7 = width;
         queueCommand.intValue8 = height;
         queueCommand.intValue9 = tint(-1);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blit(
      RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color
   ) {
      if (this.replaying) {
         super.blit(pipeline, sprite, x, y, u, v, width, height, textureWidth, textureHeight, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXTURE_UV_COLOR);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.floatValue = u;
         queueCommand.floatValue2 = v;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = textureWidth;
         queueCommand.intValue6 = textureHeight;
         queueCommand.intValue7 = tint(color);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blit(
      RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight
   ) {
      if (this.replaying) {
         super.blit(pipeline, sprite, x, y, u, v, width, height, textureWidth, textureHeight);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXTURE_UV);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.floatValue = u;
         queueCommand.floatValue2 = v;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = textureWidth;
         queueCommand.intValue6 = textureHeight;
         queueCommand.intValue7 = tint(-1);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blit(
      RenderPipeline pipeline,
      Identifier sprite,
      int x,
      int y,
      float u,
      float v,
      int width,
      int height,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight
   ) {
      if (this.replaying) {
         super.blit(pipeline, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXTURE_REGION);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.floatValue = u;
         queueCommand.floatValue2 = v;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = regionWidth;
         queueCommand.intValue6 = regionHeight;
         queueCommand.intValue7 = textureWidth;
         queueCommand.intValue8 = textureHeight;
         queueCommand.intValue9 = tint(-1);
         Jebriksheet.method2357(queueCommand);
      }
   }

   @Override
   public void blit(
      RenderPipeline pipeline,
      Identifier sprite,
      int x,
      int y,
      float u,
      float v,
      int width,
      int height,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight,
      int color
   ) {
      if (this.replaying) {
         super.blit(pipeline, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, color);
      } else {
         HudQueuedDraw queueCommand = this.queueCommand(HudQueuedDraw.Type.TEXTURE_REGION_COLOR);
         queueCommand.obj = pipeline;
         queueCommand.obj2 = sprite;
         queueCommand.intValue = x;
         queueCommand.intValue2 = y;
         queueCommand.floatValue = u;
         queueCommand.floatValue2 = v;
         queueCommand.intValue3 = width;
         queueCommand.intValue4 = height;
         queueCommand.intValue5 = regionWidth;
         queueCommand.intValue6 = regionHeight;
         queueCommand.intValue7 = textureWidth;
         queueCommand.intValue8 = textureHeight;
         queueCommand.intValue9 = tint(color);
         Jebriksheet.method2357(queueCommand);
      }
   }
}
