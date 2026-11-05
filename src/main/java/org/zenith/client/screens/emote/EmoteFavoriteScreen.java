package org.zenith.client.screens.emote;

import dev.porno.playerAnim.api.IPlayer;
import dev.porno.playerAnim.api.layered.AnimationContainer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.EmotesModule;
import org.zenith.client.screens.builder.PlayerPreview3D;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.EmoteAccess;
import org.zenith.utility.misc.EmoteInfo;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.PreviewEmotePlayer;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class EmoteFavoriteScreen extends Screen {
   private static final int PREVIEW_ANIMATION_PRIORITY = 2000;
   private static final int COLUMNS = 3;
   private static final float GAP = 5.0F;
   private static final float PREVIEW_GAP = 12.0F;
   private static final float CARD_HEIGHT = 72.0F;
   private static final float SCROLL_SPEED = 38.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private final EmotesModule owner;
   private final int favoriteSlot;
   private final List<EmoteInfo> allEmotes;
   private final SearchBox searchBox;
   private final Animation openAnimation = new Animation(280L, 0.0F, Easing.INSTANCE4);
   private final Animation contentAnimation = new Animation(220L, 0.0F, Easing.INSTANCE3);
   private final Animation searchAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation closeAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation scrollBarAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Map<String, Animation> cardAnimations = new HashMap<>();
   private final PlayerPreview3D playerPreview = new PlayerPreview3D(0.0F, 0.0F, 0.0F, 0.0F);
   private final AnimationContainer<PreviewEmotePlayer> previewAnimation = new AnimationContainer(null);
   private List<EmoteInfo> filteredEmotes;
   private LocalPlayer previewAnimationEntity;
   private PreviewEmotePlayer previewEmotePlayer;
   private String previewEmoteId;
   private String previousSearch = "";
   private float scroll;
   private float scrollTarget;
   private float interactiveScale = 1.0F;
   private boolean closing;
   private boolean draggingScrollBar;
   private float scrollBarDragOffset;

   public EmoteFavoriteScreen(EmotesModule owner, int favoriteSlot) {
      super(Component.literal("Favorite emote"));
      this.owner = owner;
      this.favoriteSlot = favoriteSlot;
      this.allEmotes = EmoteAccess.method7917().stream().sorted(Comparator.comparing(EmoteInfo::getText2, String.CASE_INSENSITIVE_ORDER)).toList();
      this.filteredEmotes = this.allEmotes;
      this.searchBox = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.emote.search"), 180.0F);
      this.searchBox.setMaxLength(80);

      for (EmoteInfo allEmote : this.allEmotes) {
         this.cardAnimations.put(allEmote.getText(), new Animation(140L, 0.0F, Easing.INSTANCE12));
      }
   }

   @Override
   public void init() {
      this.openAnimation.method2809(0.0F);
      this.openAnimation.method2808(1.0F);
      this.restartContentAnimation();
      this.closing = false;
      this.searchBox.setSelected(false);
   }

   @Override
   public void tick() {
      if (this.previewEmotePlayer != null) {
         this.previewEmotePlayer.tick();
      }

      super.tick();
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      float f;
      float previewAlpha = Math.clamp(f = this.openAnimation.method2808(this.closing ? 0.0F : 1.0F), 0.0F, 1.0F);
      if (this.closing && previewAlpha <= 0.001F) {
         this.finishClose();
      } else {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            MenuDrawContext menuDrawContext = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
            menuDrawContext.drawRect(0.0F, 0.0F, this.width, this.height, currentStyle.getRightBackground().getZenithColor().method7293((int)(145.0F * previewAlpha)));
            EmoteFavoriteScreen.Layout layout = this.layout();
            this.updateFilter();
            this.interactiveScale = 0.9F + 0.1F * f;
            float transformX = this.transformX(mouseX, layout);
            float transformY = this.transformY(mouseY, layout);
            menuDrawContext.pose().pushMatrix();
            menuDrawContext.pose().translate(layout.centerX(), layout.centerY());
            menuDrawContext.pose().scale(this.interactiveScale, this.interactiveScale);
            menuDrawContext.pose().translate(-layout.centerX(), -layout.centerY());
            ZenithColor zenithColor = currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor2 = currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor3 = currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor4 = currentStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor5 = currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor6 = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor7 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor8 = currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor9 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
            float blurPower;
            if ((blurPower = Zenith.getInstance().getNLMenuScreen().getBlurPower()) != 0.0F) {
               RenderShaders.method5287(
                  MatrixUtils.method664(menuDrawContext.pose()),
                  layout.panelX,
                  layout.panelY,
                  layout.panelWidth,
                  layout.panelHeight,
                  blurPower,
                  ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
                  ZenithColor.INSTANCE.method7294(previewAlpha),
                  true,
                  false
               );
            }

            menuDrawContext.drawRoundedRect(layout.panelX, layout.panelY, layout.panelWidth, layout.panelHeight, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()), zenithColor);
            menuDrawContext.drawRoundedRect(
               layout.panelX + 6.0F, layout.panelY + 6.0F, layout.panelWidth - 12.0F, 37.0F, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() - 1.0F), zenithColor2
            );
            this.renderHeader(menuDrawContext, layout, transformX, transformY, currentStyle, zenithColor3, zenithColor4, zenithColor6, zenithColor7, previewAlpha);
            this.renderCards(menuDrawContext, layout, transformX, transformY, zenithColor3, zenithColor4, zenithColor5, zenithColor6, zenithColor8, zenithColor9, previewAlpha);
            this.renderScrollBar(menuDrawContext, layout, transformX, transformY, currentStyle, previewAlpha);
            this.renderPlayerPreview(context, menuDrawContext, layout, transformX, transformY, zenithColor3, previewAlpha);
            menuDrawContext.pose().popMatrix();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderPlayerPreview(
      GuiGraphicsExtractor context, MenuDrawContext menuDrawContext, EmoteFavoriteScreen.Layout layout, float transformX, float transformY, ZenithColor zenithColor, float previewAlpha
   ) {
      menuDrawContext.drawRoundedRect(layout.previewX, layout.gridY, layout.previewWidth, layout.gridHeight, ProxyDotRadiusData.method8069(6.0F), zenithColor);
      if (this.minecraft != null && this.minecraft.player != null) {
         this.playerPreview.setBounds(layout.previewX, layout.gridY, layout.previewWidth, layout.gridHeight);
         menuDrawContext.enableScissor((int)layout.previewX, (int)layout.gridY, (int)(layout.previewX + layout.previewWidth), (int)(layout.gridY + layout.gridHeight));
         RenderState.method1883(1.0F, 1.0F, 1.0F, previewAlpha);
         boolean flag = false /* VF: Semaphore variable */;

         try {
            flag = true;
            if (this.previewEmotePlayer == null) {
               this.detachPreviewAnimationLayer();
               this.playerPreview.render(context, this.minecraft.player, transformX, transformY);
               flag = false;
            } else {
               this.attachPreviewAnimation(this.minecraft.player);
               this.previewAnimation.setAnim(this.previewEmotePlayer);

               try {
                  this.playerPreview.render(context, this.minecraft.player, transformX, transformY);
               } catch (Throwable e) {
                  this.previewAnimation.setAnim(null);
                  throw e;
               }

               this.previewAnimation.setAnim(null);
               flag = false;
            }
         } finally {
            if (flag) {
               RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
               menuDrawContext.disableScissor();
            }
         }

         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         menuDrawContext.disableScissor();
      }
   }

   private void renderHeader(
      MenuDrawContext menuDrawContext,
      EmoteFavoriteScreen.Layout layout,
      float transformX,
      float transformY,
      ZenithStyle currentStyle,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3,
      ZenithColor zenithColor4,
      float previewAlpha
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.8F);
      String text = t("gui.emote.pick");
      menuDrawContext.drawText(font, text, layout.contentX, layout.panelY + (37.0F - font.height()) / 2.0F + 6.0F, zenithColor3);
      double d = transformX;
      double transformY2 = transformY;
      float f = 18.0F;
      float f2 = 18.0F;
      float closeY = layout.closeY;
      float closeX = layout.closeX;
      double d2 = transformY2;
      double d3 = d;
      int n = d >= closeX && d2 >= closeY && d3 <= closeX + f2 && d2 <= closeY + f ? 1 : 0;
      float hover = this.closeAnimation.method2808(n != 0 ? 1.0F : 0.0F);
      menuDrawContext.drawRoundedRect(layout.closeX, layout.closeY, 18.0F, 18.0F, ProxyDotRadiusData.method8069(5.0F), zenithColor.method7295(zenithColor2, hover));
      Font font2 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
      menuDrawContext.drawText(font2, "×", layout.closeX + (18.0F - font2.width("×")) / 2.0F, layout.closeY + (18.0F - font2.height()) / 2.0F - 0.5F, zenithColor3);
      float f3 = this.searchAnimation.method2808(this.searchBox.isSelected() ? 1.0F : 0.0F);
      ZenithColor zenithColor5 = currentStyle.getFieldSurfaceBackground()
         .getZenithColor()
         .method7294(previewAlpha)
         .method7295(currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha), f3 * 0.35F);
      menuDrawContext.drawRoundedRect(layout.searchX, layout.searchY, layout.searchWidth, 20.0F, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()), zenithColor5);
      this.searchBox.setWidth(layout.searchWidth - 23.0F);
      this.searchBox.method987(menuDrawContext, layout.searchX + 7.0F, layout.searchY + (20.0F - this.searchBox.getFont().height()) / 2.0F, zenithColor3, zenithColor4);
      Font font3 = Fonts.NEW_ICONS.getFont(4.5F);
      menuDrawContext.drawText(font3, "S", layout.searchX + layout.searchWidth - font3.width("S") - 7.0F, layout.searchY + (20.0F - font3.height()) / 2.0F, zenithColor4);
   }

   private void renderCards(
      MenuDrawContext menuDrawContext,
      EmoteFavoriteScreen.Layout layout,
      float transformX,
      float transformY,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3,
      ZenithColor zenithColor4,
      ZenithColor zenithColor5,
      ZenithColor zenithColor6,
      float previewAlpha
   ) {
      float contentHeight = this.contentHeight();
      this.clampScroll(contentHeight, layout.gridHeight);
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f = Math.clamp(this.contentAnimation.method2808(1.0F), 0.0F, 1.0F);
      String anObject = this.owner.method6905(this.favoriteSlot);
      Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      menuDrawContext.enableScissor((int)layout.gridX, (int)layout.gridY, (int)(layout.gridX + layout.gridWidth), (int)(layout.gridY + layout.gridHeight));
      int n = 0;
      EmoteInfo filteredEmote = null;

      for (int i = 0; i < this.filteredEmotes.size(); i++) {
         EmoteInfo emoteInfo = this.filteredEmotes.get(i);
         int n2 = i % 3;
         int n3 = i / 3;
         float floatValue = layout.gridX + n2 * (layout.cardWidth + 5.0F);
         float f2;
         if (!((f2 = layout.gridY + n3 * 77.0F + this.scroll) + 72.0F < layout.gridY) && !(f2 > layout.gridY + layout.gridHeight)) {
            byte b;
            label82: {
               double d = transformX;
               double transformY2 = transformY;
               float gridHeight = layout.gridHeight;
               float gridWidth = layout.gridWidth;
               float gridY = layout.gridY;
               float gridX = layout.gridX;
               double d2 = transformY2;
               double d3 = d;
               if ((d >= gridX && d2 >= gridY && d3 <= gridX + gridWidth && d2 <= gridY + gridHeight ? 1 : 0) != 0) {
                  double transformX2 = transformX;
                  transformY2 = transformY;
                  gridHeight = 72.0F;
                  gridWidth = layout.cardWidth;
                  d2 = transformY2;
                  d3 = transformX2;
                  if ((transformX2 >= floatValue && d2 >= f2 && d3 <= floatValue + gridWidth && d2 <= f2 + gridHeight ? 1 : 0) != 0) {
                     b = 1;
                     break label82;
                  }
               }

               b = 0;
            }

            byte b2 = b;
            if (b != 0) {
               filteredEmote = emoteInfo;
            }

            boolean flag = emoteInfo.getText().equals(anObject);
            float hover = this.cardAnimations.get(emoteInfo.getText()).method2808(b2 != 0 ? 1.0F : 0.0F);
            float previewAlpha2 = Math.clamp(f * 1.25F - n * 0.035F, 0.0F, 1.0F);
            float y = f2 + (1.0F - previewAlpha2) * 8.0F;
            float previewAlpha3 = previewAlpha * previewAlpha2;
            n++;
            ZenithColor zenithColor7 = zenithColor.method7294(previewAlpha2).method7295(zenithColor2.method7294(previewAlpha2), hover);
            if (flag) {
               zenithColor7 = zenithColor7.method7295(zenithColor3.method7294(previewAlpha2), 0.75F);
            }

            menuDrawContext.drawRoundedRect(floatValue, y, layout.cardWidth, 72.0F, ProxyDotRadiusData.method8069(6.0F), zenithColor7);
            float guiScaledWidth = 39.0F + hover * 3.0F;
            float x = floatValue + (layout.cardWidth - guiScaledWidth) / 2.0F;
            float y2 = y + 5.0F - hover;
            menuDrawContext.drawTexture(emoteInfo.getIdentifier(), x, y2, guiScaledWidth, guiScaledWidth, ZenithColor.INSTANCE.method7294(previewAlpha3));
            String text = fit(font, emoteInfo.getText2(), layout.cardWidth - 8.0F);
            menuDrawContext.drawText(font, text, floatValue + (layout.cardWidth - font.width(text)) / 2.0F, y + 72.0F - 17.0F, zenithColor4.method7294(previewAlpha2));
            String str = emoteInfo.getText().startsWith("spemotes/") ? "SPEmotes" : "Zenith";
            ZenithColor zenithColor8 = flag ? zenithColor6.method7294(previewAlpha2) : zenithColor5.method7294(previewAlpha2);
            menuDrawContext.drawText(font2, str, floatValue + (layout.cardWidth - font2.width(str)) / 2.0F, y + 72.0F - 8.0F, zenithColor8);
         }
      }

      if (filteredEmote != null) {
         this.updatePreviewEmote(filteredEmote);
      }

      boolean empty = this.filteredEmotes.isEmpty();

      if (empty) {
         Font font3 = Fonts.NEW_MEDIUM.getFont(5.5F);
         String text = t("gui.configs.empty.query");
         menuDrawContext.drawText(font3, text, layout.gridX + (layout.gridWidth - font3.width(text)) / 2.0F, layout.gridY + (layout.gridHeight - font3.height()) / 2.0F, zenithColor5);
      }

      menuDrawContext.disableScissor();
   }

   private void renderScrollBar(MenuDrawContext menuDrawContext, EmoteFavoriteScreen.Layout layout, float transformX, float transformY, ZenithStyle currentStyle, float previewAlpha) {
      EmoteFavoriteScreen.ScrollBarMetrics scrollBarMetrics = this.scrollBarMetrics(layout);
      if (scrollBarMetrics != null) {
         double d = transformX;
         double transformY2 = transformY;
         float f = layout.scrollBarX - 2.0F;
         float gridHeight = layout.gridHeight;
         float f2 = 7.0F;
         float gridY = layout.gridY;
         float f3 = f;
         double d2 = transformY2;
         double d3 = d;
         int n = d >= f3 && d2 >= gridY && d3 <= f3 + f2 && d2 <= gridY + gridHeight ? 1 : 0;
         float f4 = this.scrollBarAnimation.method2808(n == 0 && !this.draggingScrollBar ? 0.0F : 1.0F);
         float width = 3.0F + f4;
         float floatValue = layout.scrollBarX - f4 / 2.0F;
         ZenithColor zenithColor = currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextTertiary()
            .getZenithColor()
            .method7294(previewAlpha)
            .method7295(currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha), f4 * 0.35F);
         menuDrawContext.drawRoundedRect(floatValue, layout.gridY, width, layout.gridHeight, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor);
         menuDrawContext.drawRoundedRect(floatValue, scrollBarMetrics.thumbY, width, scrollBarMetrics.thumbHeight, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor2);
      }
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseClicked(double x, double y, int mouseToGlfw) {
      if (this.closing) {
         return true;
      } else {
         EmoteFavoriteScreen.Layout layout = this.layout();
         x = this.transformX(x, layout);
         y = this.transformY(y, layout);
         float gridHeight = 18.0F;
         float gridWidth = 18.0F;
         float closeY = layout.closeY;
         float closeX = layout.closeX;
         if ((x >= closeX && y >= closeY && x <= closeX + gridWidth && y <= closeY + gridHeight ? 1 : 0) != 0) {
            this.onClose();
            return true;
         } else if (this.searchBox.onMouseClicked(x, y, Enum3.method6094(mouseToGlfw))) {
            return true;
         } else if (this.playerPreview.onMouseClicked(x, y, mouseToGlfw)) {
            return true;
         } else {
            EmoteFavoriteScreen.ScrollBarMetrics scrollBarMetrics = this.scrollBarMetrics(layout);
            if (mouseToGlfw == 0 && scrollBarMetrics != null) {
               float f = layout.scrollBarX - 2.0F;
               gridHeight = layout.gridHeight;
               gridWidth = 7.0F;
               closeY = layout.gridY;
               closeX = f;
               if ((x >= closeX && y >= closeY && x <= closeX + gridWidth && y <= closeY + gridHeight ? 1 : 0) != 0) {
                  this.draggingScrollBar = true;
                  f = layout.scrollBarX - 2.0F;
                  gridHeight = scrollBarMetrics.thumbHeight;
                  gridWidth = 7.0F;
                  closeY = scrollBarMetrics.thumbY;
                  closeX = f;
                  if ((x >= closeX && y >= closeY && x <= closeX + gridWidth && y <= closeY + gridHeight ? 1 : 0) != 0) {
                     this.scrollBarDragOffset = (float)y - scrollBarMetrics.thumbY;
                  } else {
                     this.scrollBarDragOffset = scrollBarMetrics.thumbHeight / 2.0F;
                     this.updateDraggedScrollBar(y, layout, scrollBarMetrics);
                  }

                  return true;
               }
            }

            if (mouseToGlfw == 0) {
               gridHeight = layout.gridHeight;
               gridWidth = layout.gridWidth;
               closeY = layout.gridY;
               closeX = layout.gridX;
               if ((x >= closeX && y >= closeY && x <= closeX + gridWidth && y <= closeY + gridHeight ? 1 : 0) != 0) {
                  int cardAt;
                  if ((cardAt = this.cardAt(x, y, layout)) >= 0 && cardAt < this.filteredEmotes.size()) {
                     this.owner.method6903(this.favoriteSlot, this.filteredEmotes.get(cardAt).getText());
                     this.onClose();
                     return true;
                  }

                  return false;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      return this.mouseDragged(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()), offsetX, offsetY);
   }

   public boolean mouseDragged(double x, double y, int mouseToGlfw, double offsetX, double offsetY) {
      EmoteFavoriteScreen.Layout layout = this.layout();
      double transformX = this.transformX(x, layout);
      double transformY = this.transformY(y, layout);
      if (this.playerPreview.onMouseDragged(transformX, transformY, mouseToGlfw, offsetX, offsetY)) {
         return true;
      } else {
         if (this.draggingScrollBar && mouseToGlfw == 0) {
            y = transformY;
            EmoteFavoriteScreen.ScrollBarMetrics scrollBarMetrics = this.scrollBarMetrics(layout);
            if (scrollBarMetrics != null) {
               this.updateDraggedScrollBar(transformY, layout, scrollBarMetrics);
               return true;
            }
         }

         return super.mouseDragged(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), offsetX, offsetY);
      }
   }

   @Override
   public boolean mouseReleased(MouseButtonEvent click) {
      return this.mouseReleased(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseReleased(double x, double y, int mouseToGlfw) {
      if (this.playerPreview.onMouseReleased(x, y, mouseToGlfw)) {
         return true;
      } else if (mouseToGlfw == 0 && this.draggingScrollBar) {
         this.draggingScrollBar = false;
         return true;
      } else {
         return super.mouseReleased(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))));
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.closing && verticalAmount != 0.0) {
         EmoteFavoriteScreen.Layout layout = this.layout();
         mouseX = this.transformX(mouseX, layout);
         mouseY = this.transformY(mouseY, layout);
         if (this.playerPreview.onMouseScrolled(mouseX, mouseY, verticalAmount)) {
            return true;
         } else {
            float gridHeight = layout.gridHeight;
            float gridWidth = layout.gridWidth;
            float gridY = layout.gridY;
            float gridX = layout.gridX;
            if ((mouseX >= gridX && mouseY >= gridY && mouseX <= gridX + gridWidth && mouseY <= gridY + gridHeight ? 1 : 0) == 0) {
               return false;
            } else {
               float contentHeight;
               if ((contentHeight = this.contentHeight()) <= layout.gridHeight) {
                  return false;
               } else {
                  this.scrollTarget += (float)verticalAmount * 38.0F;
                  this.clampScroll(contentHeight, layout.gridHeight);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.closing) {
         return true;
      } else if (keyCode == 256) {
         this.onClose();
         return true;
      } else {
         return !(!this.searchBox.keyPressed(keyCode, keycode, modifiers) && !super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers))));
      }
   }

   @Override
   public boolean charTyped(CharacterEvent input) {
      return this.charTyped((char)input.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      if (this.closing) {
         return true;
      } else {
         return !(!this.searchBox.charTyped(codepoint, n) && !super.charTyped(new CharacterEvent(codepoint)));
      }
   }

   @Override
   public void onClose() {
      if (!this.closing) {
         this.closing = true;
         this.draggingScrollBar = false;
         this.searchBox.setSelected(false);
      }
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public void removed() {
      this.detachPreviewAnimation();
      super.removed();
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   private void updateFilter() {
      String lowerCase = this.searchBox.getText().trim().toLowerCase(Locale.ROOT);
      if (!lowerCase.equals(this.previousSearch)) {
         this.previousSearch = lowerCase;
         this.filteredEmotes = lowerCase.isEmpty()
            ? this.allEmotes
            : this.allEmotes
               .stream()
               .filter(
                  var1x -> !(!var1x.getText2().toLowerCase(Locale.ROOT).contains(lowerCase)
                        && !var1x.getText().toLowerCase(Locale.ROOT).contains(lowerCase)
                        && !var1x.author().toLowerCase(Locale.ROOT).contains(lowerCase))
               )
               .toList();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.draggingScrollBar = false;
         this.restartContentAnimation();
      }
   }

   private int cardAt(double x, double y, EmoteFavoriteScreen.Layout layout) {
      for (int i = 0; i < this.filteredEmotes.size(); i++) {
         int n = i % 3;
         int n2 = i / 3;
         float f = layout.gridX + n * (layout.cardWidth + 5.0F);
         float f2 = layout.gridY + n2 * 77.0F + this.scroll;
         float f3 = 72.0F;
         float cardWidth = layout.cardWidth;
         if ((x >= f && y >= f2 && x <= f + cardWidth && y <= f2 + f3 ? 1 : 0) != 0) {
            return i;
         }
      }

      return -1;
   }

   private float contentHeight() {
      int n;
      return this.filteredEmotes.isEmpty() ? 0.0F : (n = (this.filteredEmotes.size() + 3 - 1) / 3) * 72.0F + Math.max(0, n - 1) * 5.0F;
   }

   private void clampScroll(float contentHeight, float gridHeight) {
      if (contentHeight <= gridHeight) {
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
      } else {
         float min = gridHeight - contentHeight;
         this.scrollTarget = Math.clamp(this.scrollTarget, min, 0.0F);
         this.scroll = Math.clamp(this.scroll, min, 0.0F);
      }
   }

   private EmoteFavoriteScreen.ScrollBarMetrics scrollBarMetrics(EmoteFavoriteScreen.Layout layout) {
      float contentHeight;
      if ((contentHeight = this.contentHeight()) <= layout.gridHeight) {
         return null;
      } else {
         float minimum = layout.gridHeight - contentHeight;
         float thumbHeight = Math.max(24.0F, layout.gridHeight * layout.gridHeight / contentHeight);
         thumbHeight = Math.min(layout.gridHeight, thumbHeight);
         float travel = layout.gridHeight - thumbHeight;
         float f = Math.clamp(this.scroll / minimum, 0.0F, 1.0F);
         return new EmoteFavoriteScreen.ScrollBarMetrics(layout.gridY + travel * f, thumbHeight, travel, minimum);
      }
   }

   private void updateDraggedScrollBar(double y, EmoteFavoriteScreen.Layout layout, EmoteFavoriteScreen.ScrollBarMetrics scrollBarMetrics) {
      float f = Math.clamp((float)y - this.scrollBarDragOffset, layout.gridY, layout.gridY + scrollBarMetrics.travel);
      float f2 = scrollBarMetrics.travel <= 0.0F ? 0.0F : (f - layout.gridY) / scrollBarMetrics.travel;
      this.scroll = scrollBarMetrics.minimum * f2;
      this.scrollTarget = this.scroll;
   }

   private void restartContentAnimation() {
      this.contentAnimation.method2809(0.0F);
      this.contentAnimation.method2808(1.0F);
   }

   private void updatePreviewEmote(EmoteInfo filteredEmote) {
      String text = filteredEmote == null ? null : filteredEmote.getText();
      if (!Objects.equals(this.previewEmoteId, text) || this.previewEmotePlayer != null && !this.previewEmotePlayer.isActive()) {
         if (this.previewEmotePlayer != null) {
            this.previewEmotePlayer.stop();
         }

         this.previewEmoteId = text;
         this.previewEmotePlayer = filteredEmote == null ? null : new PreviewEmotePlayer(filteredEmote, 0);
         if (filteredEmote == null) {
            this.detachPreviewAnimationLayer();
         }
      }
   }

   private void attachPreviewAnimation(LocalPlayer previewAnimationEntity) {
      if (this.previewAnimationEntity != previewAnimationEntity) {
         this.detachPreviewAnimationLayer();
         ((IPlayer)previewAnimationEntity).playerAnimator$getAnimationStack().addAnimLayer(2000, this.previewAnimation);
         this.previewAnimationEntity = previewAnimationEntity;
      }
   }

   private void detachPreviewAnimationLayer() {
      this.previewAnimation.setAnim(null);
      if (this.previewAnimationEntity != null) {
         ((IPlayer)this.previewAnimationEntity).playerAnimator$getAnimationStack().removeLayer(this.previewAnimation);
         this.previewAnimationEntity = null;
      }
   }

   private void detachPreviewAnimation() {
      this.detachPreviewAnimationLayer();
      if (this.previewEmotePlayer != null) {
         this.previewEmotePlayer.stop();
      }

      this.previewEmotePlayer = null;
      this.previewEmoteId = null;
   }

   private void finishClose() {
      if (this.minecraft != null && this.minecraft.gui.screen() == this) {
         this.minecraft.gui.setScreen(null);
      }
   }

   private EmoteFavoriteScreen.Layout layout() {
      float panelWidth = Math.min(this.width - 30.0F, 520.0F);
      float panelHeight = Math.min(this.height - 24.0F, 360.0F);
      float panelX = (this.width - panelWidth) / 2.0F;
      float panelY = (this.height - panelHeight) / 2.0F;
      float contentX = panelX + 14.0F;
      float f = panelWidth - 28.0F;
      float searchWidth = Math.min(190.0F, f * 0.4F);
      float searchX = panelX + panelWidth - 14.0F - searchWidth;
      float searchY = panelY + 14.5F;
      float gridY = panelY + 51.0F;
      float gridHeight = panelY + panelHeight - 14.0F - gridY;
      float previewWidth = f * 0.39F;
      float gridWidth = f - previewWidth - 12.0F;
      float scrollBarX = contentX + gridWidth + 3.0F;
      float previewX = contentX + gridWidth + 12.0F;
      float cardWidth = (gridWidth - 10.0F) / 3.0F;
      return new EmoteFavoriteScreen.Layout(
         panelX, panelY, panelWidth, panelHeight, contentX, searchX, searchY, searchWidth, contentX, gridY, gridWidth, gridHeight, cardWidth, scrollBarX, previewX, previewWidth, searchX - 24.0F, panelY + 15.5F
      );
   }

   private float transformX(double mouseX, EmoteFavoriteScreen.Layout layout) {
      return (float)((mouseX - layout.centerX()) / this.interactiveScale + layout.centerX());
   }

   private float transformY(double mouseY, EmoteFavoriteScreen.Layout layout) {
      return (float)((mouseY - layout.centerY()) / this.interactiveScale + layout.centerY());
   }

   private static String fit(Font font, String text, float f) {
      if (font.width(text) <= f) {
         return text;
      } else {
         String str = "…";
         int length = text.length();

         while (length > 0 && font.width(text.substring(0, length) + str) > f) {
            length--;
         }

         return text.substring(0, length) + str;
      }
   }

   private static boolean inside(double d, double d2, float f, float f2, float f3, float f4) {
      return (d >= f && d2 >= f2 && d <= f + f3 && d2 <= f2 + f4);
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private static final class Layout {
      private final float panelX;
      private final float panelY;
      private final float panelWidth;
      private final float panelHeight;
      private final float contentX;
      private final float searchX;
      private final float searchY;
      private final float searchWidth;
      private final float gridX;
      private final float gridY;
      private final float gridWidth;
      private final float gridHeight;
      private final float cardWidth;
      private final float scrollBarX;
      private final float previewX;
      private final float previewWidth;
      private final float closeX;
      private final float closeY;

      public float panelX() {
         return this.panelX;
      }

      public float panelY() {
         return this.panelY;
      }

      public float panelWidth() {
         return this.panelWidth;
      }

      public float panelHeight() {
         return this.panelHeight;
      }

      public float contentX() {
         return this.contentX;
      }

      public float searchX() {
         return this.searchX;
      }

      public float searchY() {
         return this.searchY;
      }

      public float searchWidth() {
         return this.searchWidth;
      }

      public float gridX() {
         return this.gridX;
      }

      public float gridY() {
         return this.gridY;
      }

      public float gridWidth() {
         return this.gridWidth;
      }

      public float gridHeight() {
         return this.gridHeight;
      }

      public float cardWidth() {
         return this.cardWidth;
      }

      public float scrollBarX() {
         return this.scrollBarX;
      }

      public float previewX() {
         return this.previewX;
      }

      public float previewWidth() {
         return this.previewWidth;
      }

      public float closeX() {
         return this.closeX;
      }

      public float closeY() {
         return this.closeY;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Layout other)) return false;
         return java.util.Objects.equals(this.panelX, other.panelX) && java.util.Objects.equals(this.panelY, other.panelY) && java.util.Objects.equals(this.panelWidth, other.panelWidth) && java.util.Objects.equals(this.panelHeight, other.panelHeight) && java.util.Objects.equals(this.contentX, other.contentX) && java.util.Objects.equals(this.searchX, other.searchX) && java.util.Objects.equals(this.searchY, other.searchY) && java.util.Objects.equals(this.searchWidth, other.searchWidth) && java.util.Objects.equals(this.gridX, other.gridX) && java.util.Objects.equals(this.gridY, other.gridY) && java.util.Objects.equals(this.gridWidth, other.gridWidth) && java.util.Objects.equals(this.gridHeight, other.gridHeight) && java.util.Objects.equals(this.cardWidth, other.cardWidth) && java.util.Objects.equals(this.scrollBarX, other.scrollBarX) && java.util.Objects.equals(this.previewX, other.previewX) && java.util.Objects.equals(this.previewWidth, other.previewWidth) && java.util.Objects.equals(this.closeX, other.closeX) && java.util.Objects.equals(this.closeY, other.closeY);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(panelX, panelY, panelWidth, panelHeight, contentX, searchX, searchY, searchWidth, gridX, gridY, gridWidth, gridHeight, cardWidth, scrollBarX, previewX, previewWidth, closeX, closeY);
      }

      @Override
      public String toString() {
         return "Layout[" + "panelX=" + this.panelX + ", " + "panelY=" + this.panelY + ", " + "panelWidth=" + this.panelWidth + ", " + "panelHeight=" + this.panelHeight + ", " + "contentX=" + this.contentX + ", " + "searchX=" + this.searchX + ", " + "searchY=" + this.searchY + ", " + "searchWidth=" + this.searchWidth + ", " + "gridX=" + this.gridX + ", " + "gridY=" + this.gridY + ", " + "gridWidth=" + this.gridWidth + ", " + "gridHeight=" + this.gridHeight + ", " + "cardWidth=" + this.cardWidth + ", " + "scrollBarX=" + this.scrollBarX + ", " + "previewX=" + this.previewX + ", " + "previewWidth=" + this.previewWidth + ", " + "closeX=" + this.closeX + ", " + "closeY=" + this.closeY + "]";
      }

      private Layout(
         float panelX,
         float panelY,
         float panelWidth,
         float panelHeight,
         float contentX,
         float searchX,
         float searchY,
         float searchWidth,
         float gridX,
         float gridY,
         float gridWidth,
         float gridHeight,
         float cardWidth,
         float scrollBarX,
         float previewX,
         float previewWidth,
         float closeX,
         float closeY
      ) {
         this.panelX = panelX;
         this.panelY = panelY;
         this.panelWidth = panelWidth;
         this.panelHeight = panelHeight;
         this.contentX = contentX;
         this.searchX = searchX;
         this.searchY = searchY;
         this.searchWidth = searchWidth;
         this.gridX = gridX;
         this.gridY = gridY;
         this.gridWidth = gridWidth;
         this.gridHeight = gridHeight;
         this.cardWidth = cardWidth;
         this.scrollBarX = scrollBarX;
         this.previewX = previewX;
         this.previewWidth = previewWidth;
         this.closeX = closeX;
         this.closeY = closeY;
      }

      private float centerX() {
         return this.panelX + this.panelWidth / 2.0F;
      }

      private float centerY() {
         return this.panelY + this.panelHeight / 2.0F;
      }
   }

   private static final class ScrollBarMetrics {
      private final float thumbY;
      private final float thumbHeight;
      private final float travel;
      private final float minimum;

      public float thumbY() {
         return this.thumbY;
      }

      public float thumbHeight() {
         return this.thumbHeight;
      }

      public float travel() {
         return this.travel;
      }

      public float minimum() {
         return this.minimum;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ScrollBarMetrics other)) return false;
         return java.util.Objects.equals(this.thumbY, other.thumbY) && java.util.Objects.equals(this.thumbHeight, other.thumbHeight) && java.util.Objects.equals(this.travel, other.travel) && java.util.Objects.equals(this.minimum, other.minimum);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(thumbY, thumbHeight, travel, minimum);
      }

      @Override
      public String toString() {
         return "ScrollBarMetrics[" + "thumbY=" + this.thumbY + ", " + "thumbHeight=" + this.thumbHeight + ", " + "travel=" + this.travel + ", " + "minimum=" + this.minimum + "]";
      }

      private ScrollBarMetrics(float thumbY, float thumbHeight, float travel, float minimum) {
         this.thumbY = thumbY;
         this.thumbHeight = thumbHeight;
         this.travel = travel;
         this.minimum = minimum;
      }
   }
}
