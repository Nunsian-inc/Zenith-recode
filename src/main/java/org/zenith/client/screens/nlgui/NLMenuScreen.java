package org.zenith.client.screens.nlgui;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.crosshair.CrosshairEditorPanel;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.panel.CategoryPanel;
import org.zenith.client.screens.nlgui.panel.ClientPanel;
import org.zenith.client.screens.nlgui.panel.CloudChatSectionPanel;
import org.zenith.client.screens.nlgui.panel.CloudSectionPanel;
import org.zenith.client.screens.nlgui.panel.FriendsSectionPanel;
import org.zenith.client.screens.nlgui.panel.GuiModulePanel;
import org.zenith.client.screens.nlgui.panel.InspectorPanel;
import org.zenith.client.screens.nlgui.panel.InterfaceSectionPanel;
import org.zenith.client.screens.nlgui.panel.ProfilePanel;
import org.zenith.client.screens.nlgui.panel.QuickMenuPanel;
import org.zenith.client.screens.nlgui.panel.ScriptsSectionPanel;
import org.zenith.client.screens.nlgui.panel.VoiceSectionPanel;
import org.zenith.client.screens.nlgui.panel.WardrobeSectionPanel;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.panel.tools.ToolsSectionPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.scripting.Script;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.RoundedRectBatch;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.ZenithDrawContext;

public class NLMenuScreen extends Screen implements MinecraftAccess {
   private final Animation animationClose = new Animation(300L, 0.0F, Easing.INSTANCE4);
   private final Animation searchFocusAnimation = new Animation(170L, 0.0F, Easing.INSTANCE12);
   private final Animation languageAnimation = new Animation(260L, 1.0F, Easing.INSTANCE13);
   private String seenLanguage;
   private float contentDip = 1.0F;
   private ProfilePanel profilePanel;
   private ClientPanel clientPanel;
   private CategoryPanel categoryPanel;
   private final InspectorPanel inspectorPanel = new InspectorPanel();
   private final ModulePreviewStage previewStage = new ModulePreviewStage();
   private final CrosshairEditorPanel crosshairEditor = new CrosshairEditorPanel();
   private GuiModulePanel guiModulePanel;
   private ToolsSectionPanel toolsSectionPanel;
   private CloudSectionPanel cloudSectionPanel;
   private WardrobeSectionPanel cosmeticsSectionPanel;
   private InterfaceSectionPanel interfaceSectionPanel;
   private ScriptsSectionPanel scriptsSectionPanel;
   private QuickMenuPanel quickMenuPanel;
   private NLMenuScreen.ElementsType type = NLMenuScreen.ElementsType.CATEGORY;
   private boolean closing = false;
   public static float widthPanel = 480.0F;
   public static float heightPanel = 320.0F;
   public static float leftWidthPanel = 100.0F;
   public static float rightWidthPanel = 380.0F;
   public static float contentBottom = 320.0F;
   private static final float PANEL_HEIGHT = 320.0F;
   private final Animation previewWidthAnimation = new Animation(220L, 0.0F, Easing.INSTANCE13);
   private final Animation extraWidthAnimation = new Animation(340L, 0.0F, Easing.INSTANCE15);
   private final Animation leftSwapAnimation = new Animation(420L, 0.0F, Easing.INSTANCE15);
   private ElementPanel leftOwner;
   public static float extraWidth;
   private final Animation crosshairWidthAnimation = new Animation(220L, 0.0F, Easing.INSTANCE13);
   private final Animation clientSwapAnimation = new Animation(460L, 0.0F, Easing.INSTANCE15);
   private static final float SWAP_SHIFT = 16.0F;
   private static final float SEAM_FADE_IN = 0.8F;
   private static final float SEAM_OVERLAP = 0.5F;
   private static final float SWAP_FADE_OUT = 0.45F;
   private static final float SWAP_FADE_IN = 0.3F;
   private float x;
   private float y;
   private float rawX;
   private float rawY;
   private boolean init = false;
   private SearchBox searchField;
   private SearchBox commandField;
   private String commandFeedback = "";
   private float smoothScale = Float.NaN;
   private float smoothBlur = Float.NaN;
   private BoundsData dragBounds;
   private BoundsData commandBarBounds;
   private boolean draggingGui;
   private boolean scaleLocked;
   private float lockedScale = 1.0F;
   private final Animation seamRetractAnimation = new Animation(160L, 0.0F, Easing.INSTANCE13);
   private final Animation setTypeAnimation = new Animation(220L, 1.0F, Easing.INSTANCE10);
   private NLMenuScreen.ElementsType prevType;
   private static final float TYPE_TAKEOVER = 0.5F;

   public NLMenuScreen() {
      super(Component.literal("NLMenu"));
   }

   @Override
   public void init() {
      this.animationClose.snapTo(0.0F);
      this.animationClose.animateTo(1.0F);
      BindCapture.reset();
      this.crosshairWidthAnimation.snapTo(0.0F);
      this.unlockGuiScale();
      this.ensurePanelsInitialized();
      this.closing = false;
      if (this.searchField == null) {
         this.searchField = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.MEDIUM.getFont(7.0F), "Search for elements...", 100.0F);
         this.searchField.setType(SearchBox.Type.ENGLISH);
         this.searchField.setType2(SearchBox.Type2.ONLY_ONE_WORLD);
      }

      if (this.commandField == null) {
         this.commandField = new SearchBox(
            new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.4F), Zenith.getInstance().getLocalization().translate("gui.command.hint"), 100.0F
         );
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void renderTop(MenuDrawContext menuDrawContext, int mouseX, int mouseY) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         HoverInfo.beginFrame();
         RoundedRectBatch.begin();

         try {
            this.renderTopDeferred(ZenithDrawContext.method8095(menuDrawContext), mouseX, mouseY, currentStyle);
         } catch (Throwable e) {
            RoundedRectBatch.method2933();
            throw e;
         }

         RoundedRectBatch.method2933();
      }
   }

   private void renderTopDeferred(MenuDrawContext customDrawContext, int mouseX, int mouseY, ZenithStyle currentStyle) {
      this.updateSmoothValues();
      float intValue = GuiStyle.PANEL_ROUND.intValue();
      this.animationClose.setDuration(MenuModule.INSTANCE.method5345());
      this.setTypeAnimation.setDuration(MenuModule.INSTANCE.method5346());
      float previewAlpha = this.animationClose.animateTo(this.closing ? 0.0F : 1.0F);
      this.contentDip = 0.32F + 0.68F * this.languageSwitch();
      float f = previewAlpha * this.contentDip;
      ElementPanel leftOwner = this.type.getPanelSupplier().get();
      ElementPanel elementPanel = this.prevType != null ? this.prevType.getPanelSupplier().get() : null;
      float alpha1 = this.setTypeAnimation.getValue();
      float guiScaledWidth = mc.getWindow().getGuiScaledWidth();
      float guiScaledHeight = mc.getWindow().getGuiScaledHeight();
      boolean active = leftOwner == this.guiModulePanel;
      leftWidthPanel = 100.0F;
      boolean flag;
      if (flag = active && this.inspectorPanel.hasPreview()) {
         this.previewStage.show(this.inspectorPanel.previewKind(), this.inspectorPanel.previewOwner());
      } else if (this.previewStage.isOpen()) {
         this.previewStage.hide();
      }

      float f2 = this.previewWidthAnimation.animateTo(flag ? 128.0F : 0.0F);
      float f3 = this.crosshairWidthAnimation.animateTo(this.crosshairEditor.isOpen() ? 176.0F : 0.0F);
      extraWidth = this.extraWidthAnimation.animateTo(leftOwner.extraWidth());
      widthPanel = leftWidthPanel + 380.0F + f2 + f3 + extraWidth;
      heightPanel = 320.0F;
      float f4 = guiScaledWidth / 2.0F - widthPanel / 2.0F;
      float f5 = guiScaledHeight / 1.9F - heightPanel / 2.0F;
      this.rawX = MenuModule.INSTANCE.method5348() ? MenuModule.INSTANCE.method5351(f4) : f4;
      this.rawY = MenuModule.INSTANCE.method5348() ? MenuModule.INSTANCE.method5352(f5) : f5;
      this.x = this.clampRenderPosition(this.rawX, widthPanel, guiScaledWidth);
      this.y = this.clampRenderPosition(this.rawY, heightPanel, guiScaledHeight);
      float guiScale = this.getGuiScale();
      float x = this.x + widthPanel / 2.0F;
      float y = this.y + heightPanel / 2.0F;
      mouseX = (int)((mouseX - x) / guiScale + x);
      mouseY = (int)((mouseY - y) / guiScale + y);
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().scale(guiScale, guiScale);
      customDrawContext.pose().translate(-x, -y);
      float floatValue;
      float floatValue2 = (floatValue = this.x + leftWidthPanel) + 236.0F;
      float f6 = this.x + widthPanel + GuiStyle.PADDING * 2;
      float blurPower;
      if ((blurPower = this.getBlurPower() * previewAlpha) != 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(customDrawContext.pose()),
            this.x,
            this.y,
            widthPanel,
            heightPanel,
            blurPower,
            ProxyDotRadiusData.method8069(intValue),
            ZenithColor.INSTANCE.method7294(previewAlpha),
            true,
            false
         );
      }

      customDrawContext.drawRoundedRect(
         this.x,
         this.y,
         leftWidthPanel + 0.5F,
         heightPanel,
         ProxyDotRadiusData.method8078(intValue, intValue),
         currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha)
      );
      customDrawContext.drawRoundedRect(
         floatValue - 0.5F,
         this.y,
         380.0F + f2 + f3 + extraWidth + 0.5F,
         heightPanel,
         ProxyDotRadiusData.method8079(intValue, intValue),
         currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
      );
      this.dragBounds = new BoundsData(this.x, this.y, widthPanel, 23.0F);
      float f7 = this.clientSwapAnimation.animateTo(this.quickMenuPanel.isExpanded() ? 1.0F : 0.0F);
      boolean expanded = this.quickMenuPanel.isExpanded();
      float swapOut2 = swapOut(f7, expanded);
      float swapIn2 = swapIn(f7, expanded);
      boolean hasLeftColumn;
      if (hasLeftColumn = leftOwner.hasLeftColumn()) {
         this.leftOwner = leftOwner;
      }

      float f8 = this.leftSwapAnimation.animateTo(hasLeftColumn ? 1.0F : 0.0F);
      float f9 = 1.0F - Mth.clamp(f8 * 1.6F, 0.0F, 1.0F);
      boolean flag2;
      if (flag2 = f7 > 0.001F && f7 < 0.999F || f8 > 0.001F && f8 < 0.999F) {
         customDrawContext.enableScissor(this.x, this.y, this.x + leftWidthPanel, this.y + heightPanel);
      }

      try {
         if (f9 > 0.001F) {
            this.categoryPanel
               .render(
                  customDrawContext,
                  mouseX,
                  mouseY,
                  f * (expanded ? swapOut2 : swapIn2) * f9,
                  this.x - (expanded ? 16.0F * f7 : 0.0F) - 16.0F * f8,
                  this.y,
                  expanded ? 1.0F : swapIn2
               );
         }

         if (f8 > 0.001F && this.leftOwner != null) {
            float f10 = Mth.clamp((f8 - 0.25F) / 0.75F, 0.0F, 1.0F);
            this.leftOwner.renderLeftColumn(customDrawContext, mouseX, mouseY, f * f10, this.x + 16.0F * (1.0F - f8), this.y, leftWidthPanel, heightPanel);
         }

         if (hasLeftColumn && this.quickMenuPanel.isExpanded()) {
            this.quickMenuPanel.setExpanded(false);
         }

         this.quickMenuPanel
            .renderColumn(customDrawContext, mouseX, mouseY, f * (expanded ? swapIn2 : swapOut2) * f9, this.x + (expanded ? 0.0F : 16.0F * (1.0F - f7)), this.y, leftWidthPanel);
      } finally {
         if (flag2) {
            customDrawContext.disableScissor();
         }
      }

      if (this.setTypeAnimation.isDone() && this.prevType != null) {
         this.prevType.getPanelSupplier().get().close();
         this.prevType = null;
      } else {
         this.setTypeAnimation.animateTo(1.0F);
      }

      float y2 = this.y
         + (elementPanel != null ? Mth.lerp(alpha1, this.headerContentOffset(elementPanel), this.headerContentOffset(leftOwner)) : this.headerContentOffset(leftOwner));
      contentBottom = this.y + heightPanel;
      float y3 = Mth.lerp(this.seamRetract(), this.y + GuiStyle.PADDING.intValue(), y2);
      this.renderColumnSeams(customDrawContext, currentStyle, floatValue, floatValue2, y3, previewAlpha, active, alpha1);
      this.renderContentHeader(customDrawContext, mouseX, mouseY, f, floatValue, this.y, leftOwner, elementPanel, alpha1, active);
      if (elementPanel != null) {
         rightWidthPanel = elementPanel.getContentWidth();
         elementPanel.render(customDrawContext, mouseX, mouseY, f * (1.0F - alpha1), floatValue, y2);
      }

      rightWidthPanel = leftOwner.getContentWidth();
      leftOwner.render(customDrawContext, mouseX, mouseY, f * alpha1, floatValue, y2);
      this.inspectorPanel.setActive(active);
      if (active) {
         this.inspectorPanel.render(customDrawContext, mouseX, mouseY, f * alpha1, floatValue2, this.y);
         if (f2 > 1.0F) {
            this.renderPreviewColumn(customDrawContext, mouseX, mouseY, floatValue2 + 144.0F, this.y, f2, previewAlpha * alpha1, currentStyle);
         }

         this.renderCommandBar(customDrawContext, mouseX, mouseY, floatValue, this.y + heightPanel - 20.0F, 236.0F, f * alpha1);
      } else {
         this.commandBarBounds = null;
         if (this.commandField != null) {
            this.commandField.setSelected(false);
         }
      }

      if (elementPanel != null && elementPanel.isRender()) {
         elementPanel.renderRightPanel(customDrawContext, mouseX, mouseY, previewAlpha * (1.0F - alpha1), f6, this.y, 1.0F - alpha1);
      }

      if (leftOwner.isRender()) {
         leftOwner.renderRightPanel(customDrawContext, mouseX, mouseY, previewAlpha * alpha1, f6, this.y, alpha1);
      }

      ScissorFlusher.flush();
      Zenith.getInstance().getKawaseBlur().method7350(customDrawContext);
      if (elementPanel != null) {
         elementPanel.renderPriority(customDrawContext, mouseX, mouseY, f * (1.0F - alpha1), floatValue, y2);
      }

      leftOwner.renderPriority(customDrawContext, mouseX, mouseY, f * alpha1, floatValue, y2);
      if (!this.closing && this.prevType != null) {
         float contentWidth = elementPanel != null ? Math.max(elementPanel.getContentWidth(), leftOwner.getContentWidth()) : leftOwner.getContentWidth();
         this.renderElementSwapBlur(customDrawContext, floatValue, y2, contentWidth, contentBottom - y2, ProxyDotRadiusData.method8076(0.0F, intValue), 1.0F - alpha1);
      }

      this.renderMenuBlurLayers(customDrawContext, previewAlpha, leftOwner, elementPanel, alpha1, f6, this.y, intValue);
      if (f3 > 1.0F) {
         this.renderCrosshairColumn(customDrawContext, mouseX, mouseY, this.x + widthPanel - f3, this.y, f3, previewAlpha);
      }

      customDrawContext.popMatrix();
   }

   private float languageSwitch() {
      Zenith zenith = Zenith.getInstance();
      String languageName = zenith == null ? null : zenith.getLocalization().getLanguageName();
      if (languageName == null) {
         return 1.0F;
      } else {
         if (this.seenLanguage == null) {
            this.seenLanguage = languageName;
         } else if (!this.seenLanguage.equals(languageName)) {
            this.seenLanguage = languageName;
            this.languageAnimation.reset(0.0F);
            this.languageAnimation.setTarget(1.0F);
         }

         return this.languageAnimation.update();
      }
   }

   private static float swapOut(float f, boolean expanded) {
      float f2 = expanded ? f : 1.0F - f;
      return Mth.clamp(1.0F - f2 / 0.45F, 0.0F, 1.0F);
   }

   private static float swapIn(float f, boolean expanded) {
      return Mth.clamp(((expanded ? f : 1.0F - f) - 0.3F) / 0.7F, 0.0F, 1.0F);
   }

   private void renderPreviewColumn(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float f, float f2, ZenithStyle currentStyle) {
      customDrawContext.drawRect(
         floatValue,
         y + GuiStyle.PADDING.intValue(),
         1.0F,
         heightPanel - GuiStyle.PADDING.intValue() * 2.0F,
         currentStyle.getFieldBorder().getZenithColor().method7294(f2 * 2.0F)
      );
      float f3 = Mth.clamp(f / 128.0F, 0.0F, 1.0F);
      float previewAlpha;
      if (!((previewAlpha = f2 * f3 * f3) <= 0.01F)) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
         float floatValue2 = floatValue + GuiStyle.PADDING * 2;
         float f4 = y + GuiStyle.PADDING * 2;
         customDrawContext.drawText(
            font,
            Zenith.getInstance().getLocalization().translate("esp.preview"),
            floatValue2,
            f4 + 1.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
         customDrawContext.drawText(
            font2,
            Zenith.getInstance().getLocalization().translate("esp.preview.desc"),
            floatValue2,
            f4 + font.height() + 3.0F,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
         if (!this.previewStage.isLive()) {
            Font font3 = Fonts.NEW_REGULAR.getFont(4.6F);
            String text = Zenith.getInstance().getLocalization().translate("esp.preview.disabled");
            customDrawContext.drawText(
               font3,
               text,
               floatValue + (f - font3.width(text)) / 2.0F,
               y + 23.0F + GuiStyle.PADDING * 2,
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         }

         boolean flag;
         if (flag = f3 < 0.995F) {
            customDrawContext.enableScissor(floatValue, y, floatValue + f, y + heightPanel);
         }

         try {
            this.previewStage.render(customDrawContext, mouseX, mouseY, floatValue, y + 23.0F, 128.0F, heightPanel - 23.0F, previewAlpha, f3);
         } finally {
            if (flag) {
               customDrawContext.disableScissor();
            }
         }
      }
   }

   private void renderCrosshairColumn(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float f, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         customDrawContext.drawRect(
            floatValue,
            y + GuiStyle.PADDING.intValue(),
            1.0F,
            heightPanel - GuiStyle.PADDING.intValue() * 2.0F,
            currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F)
         );
         float f2 = Mth.clamp(f / 176.0F, 0.0F, 1.0F);
         this.crosshairEditor.render(customDrawContext, mouseX, mouseY, floatValue, y, 176.0F, heightPanel, previewAlpha, f2);
      }
   }

   private void renderColumnSeams(MenuDrawContext customDrawContext, ZenithStyle currentStyle, float floatValue, float f, float y, float previewAlpha, boolean active, float alpha1) {
      float height;
      if (!((height = this.y + heightPanel - GuiStyle.PADDING.intValue() - y) <= 0.0F)) {
         customDrawContext.drawRect(floatValue, y, 1.0F, height, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
         if (active) {
            float f2;
            if ((f2 = Mth.clamp((alpha1 - 0.8F) / 0.19999999F, 0.0F, 1.0F)) > 0.001F) {
               customDrawContext.drawRect(f, y, 1.0F, height, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * f2 * 2.0F));
            }
         }
      }
   }

   private float seamRetract() {
      boolean flag = !this.closing && this.prevType != null && MenuModule.INSTANCE.method5344() && this.getBlurPower() != 0.0F;
      return this.seamRetractAnimation.animateTo(flag ? 1.0F : 0.0F);
   }

   private void renderContentHeader(
      MenuDrawContext customDrawContext, int mouseX, int mouseY, float previewAlpha, float floatValue, float y, ElementPanel leftOwner, ElementPanel elementPanel, float alpha1, boolean active
   ) {
      float animationPosY = y + GuiStyle.PADDING.intValue();
      float headerSearchWidth = this.headerSearchWidth(leftOwner);
      float headerSearchX;
      float f = (headerSearchX = this.headerSearchX(floatValue, leftOwner, headerSearchWidth)) + headerSearchWidth;
      float headerSearchX2 = headerSearchX;
      float headerSearchWidth2 = headerSearchWidth;
      float headerBandHeight = this.headerBandHeight(leftOwner);
      if (elementPanel != null) {
         float headerSearchWidth3 = this.headerSearchWidth(elementPanel);
         float headerSearchX3;
         float f2 = (headerSearchX3 = this.headerSearchX(floatValue, elementPanel, headerSearchWidth3)) + headerSearchWidth3;
         headerBandHeight = Mth.lerp(alpha1, this.headerBandHeight(elementPanel), headerBandHeight);
         headerSearchX2 = Mth.lerp(alpha1, headerSearchX3, headerSearchX);
         headerSearchWidth2 = Mth.lerp(alpha1, f2, f) - headerSearchX2;
      }

      if (elementPanel != null) {
         this.renderPanelHeader(customDrawContext, mouseX, mouseY, elementPanel, floatValue, animationPosY, previewAlpha * (1.0F - alpha1), 1.0F - alpha1);
      }

      this.renderPanelHeader(customDrawContext, mouseX, mouseY, leftOwner, floatValue, animationPosY, previewAlpha * alpha1, alpha1);
      this.renderSearch(customDrawContext, headerSearchX2, animationPosY, headerBandHeight, headerSearchWidth2, previewAlpha, active, active ? alpha1 : 0.0F);
   }

   private void renderPanelHeader(MenuDrawContext customDrawContext, int mouseX, int mouseY, ElementPanel leftOwner, float floatValue, float animationPosY, float f, float alpha1) {
      if (!(f <= 0.001F)) {
         float f2 = Math.max(0.0F, leftOwner.getContentWidth() - this.headerSearchWidth(leftOwner) - GuiStyle.PADDING.intValue());
         leftOwner.renderHeader(customDrawContext, mouseX, mouseY, f, floatValue, animationPosY, f2);
         float buttonWidth;
         if ((buttonWidth = leftOwner.getButtonWidth()) > 0.01F) {
            leftOwner.renderHeaderButtons(customDrawContext, mouseX, mouseY, f, floatValue + leftOwner.getHeaderTitleWidth() + GuiStyle.PADDING.intValue() * 2.0F, animationPosY, buttonWidth, alpha1);
         }
      }
   }

   private float headerSearchWidth(ElementPanel leftOwner) {
      return leftOwner.isHeaderSearchOnly() ? 236.0F : Math.min(120.0F, leftOwner.getContentWidth());
   }

   private float headerSearchX(float floatValue, ElementPanel leftOwner, float headerSearchWidth) {
      return leftOwner.isHeaderSearchOnly() ? floatValue : floatValue + leftOwner.getContentWidth() - headerSearchWidth;
   }

   private float headerBandHeight(ElementPanel leftOwner) {
      return leftOwner.isHeaderSearchOnly() ? 23.0F - GuiStyle.PADDING.intValue() * 2.0F : 23.0F;
   }

   private float headerContentOffset(ElementPanel leftOwner) {
      return leftOwner.isHeaderSearchOnly() ? 23.0F : 23.0F + GuiStyle.PADDING.intValue() * 2.0F;
   }

   private void renderSearch(MenuDrawContext customDrawContext, float f, float animationPosY, float headerBandHeight, float f2, float previewAlpha, boolean active, float f3) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         boolean flag = this.searchField != null && this.searchField.isSelected();
         float f4 = 8.0F;
         float hover = this.searchFocusAnimation.animateTo(flag ? 1.0F : 0.0F);
         ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(headerBandHeight / 2.0F);
         customDrawContext.drawRoundedRectBatched(
            f + GuiStyle.PADDING.intValue(),
            animationPosY,
            f2 - GuiStyle.PADDING.intValue() * 2.0F,
            headerBandHeight,
            proxyDotRadiusData,
            currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
         );
         customDrawContext.flushRoundedRects();
         if (hover > 0.01F) {
            customDrawContext.drawRoundedBorder(
               f + GuiStyle.PADDING.intValue(),
               animationPosY,
               f2 - GuiStyle.PADDING.intValue() * 2.0F,
               headerBandHeight,
               0.15F,
               proxyDotRadiusData,
               currentStyle.getPrimaryColor().getZenithColor().method7292(0.34F).method7294(previewAlpha * hover)
            );
         }

         Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
         float centeredTextY = animationPosY + (headerBandHeight - font.height()) / 2.0F;
         MenuGlyph.SEARCH
            .draw(
               customDrawContext,
               f + f4 + GuiStyle.PADDING.intValue(),
               animationPosY + (headerBandHeight - 6.0F) / 2.0F,
               currentStyle.getTextTertiary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha)
            );
         this.searchField.setFont(font);
         this.searchField.setWidth(f2 - f4 * 2.0F - 30.0F);
         this.searchField
            .method987(
               customDrawContext,
               f + f4 + GuiStyle.PADDING.intValue() + 8.0F,
               centeredTextY,
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         if (active && this.guiModulePanel != null && f3 > 0.01F) {
            String text = String.valueOf(this.guiModulePanel.getVisibleCount());
            Font font2 = Fonts.NEW_REGULAR.getFont(4.8F);
            customDrawContext.drawText(
               font2,
               text,
               f + f2 - f4 - GuiStyle.PADDING.intValue() - font2.width(text),
               animationPosY + (headerBandHeight - font2.height()) / 2.0F,
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * f3)
            );
         }
      }
   }

   private void renderCommandBar(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float width, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         customDrawContext.drawRect(
            floatValue + GuiStyle.PADDING.intValue() * 2.0F,
            y,
            width - GuiStyle.PADDING.intValue() * 4.0F,
            1.0F,
            currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F)
         );
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         float centeredTextY = y + (20.0F - font.height()) / 2.0F + 1.0F;
         float f = 8.0F;
         boolean flag = this.commandField != null && this.commandField.isSelected();
         customDrawContext.drawText(font, ":", floatValue + f, centeredTextY, (flag ? currentStyle.getPrimaryColor() : currentStyle.getTextTertiary()).getZenithColor().method7294(previewAlpha));
         this.commandField.setFont(font);
         this.commandField.setWidth(width - f * 2.0F - 12.0F);
         this.commandField
            .method987(
               customDrawContext,
               floatValue + f + 8.0F,
               centeredTextY,
               currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         this.commandBarBounds = new BoundsData(floatValue, y, width, 20.0F);
         if (!this.commandFeedback.isEmpty() && !flag) {
            Font font2 = Fonts.NEW_REGULAR.getFont(4.8F);
            customDrawContext.drawText(
               font2,
               this.commandFeedback,
               floatValue + width - f - font2.width(this.commandFeedback),
               centeredTextY,
               currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            );
         }
      }
   }

   private void runCommand() {
      String text = this.commandField.getText();
      if (text != null && !text.isBlank()) {
         String str;
         try {
            Zenith.getInstance().getCommandManager().getDispatcher().execute(text.trim(), Zenith.getInstance().getCommandManager().getSource());
            str = "gui.command.ok";
         } catch (Exception e) {
            str = "gui.command.error";
         }

         this.commandFeedback = Zenith.getInstance().getLocalization().translate(str);
         this.commandField.setText("");
         this.commandField.setCursor(0);
      }
   }

   private void renderMenuBlurLayers(MenuDrawContext customDrawContext, float previewAlpha, ElementPanel leftOwner, ElementPanel elementPanel, float alpha1, float f, float y, float intValue) {
      if (MenuModule.INSTANCE.method5344()) {
         float blurPower;
         if ((blurPower = this.getBlurPower()) != 0.0F) {
            if (this.closing) {
               this.drawBlurLayer(customDrawContext, this.x, this.y, widthPanel, heightPanel, intValue, previewAlpha, blurPower);
               this.drawRightPanelBlur(customDrawContext, elementPanel, previewAlpha * (1.0F - alpha1), 1.0F - alpha1, f, y, previewAlpha, blurPower);
               this.drawRightPanelBlur(customDrawContext, leftOwner, previewAlpha * alpha1, alpha1, f, y, previewAlpha, blurPower);
            }
         }
      }
   }

   private void drawRightPanelBlur(MenuDrawContext customDrawContext, ElementPanel leftOwner, float f, float alpha1, float f2, float y, float previewAlpha, float blurPower) {
      if (leftOwner != null && leftOwner.isRender()) {
         BoundsData rightPanelBlurBounds = leftOwner.getRightPanelBlurBounds(f2, y, f, alpha1);
         if (rightPanelBlurBounds != null) {
            this.drawBlurLayer(customDrawContext, rightPanelBlurBounds.getFloatValue(), rightPanelBlurBounds.getFloatValue2(), rightPanelBlurBounds.width(), rightPanelBlurBounds.height(), GuiStyle.ROUND.intValue(), previewAlpha, blurPower);
         }
      }
   }

   private void drawBlurLayer(MenuDrawContext customDrawContext, float x, float y, float widthPanel, float heightPanel, float intValue, float previewAlpha, float blurPower) {
      this.drawBlurLayer(customDrawContext, x, y, widthPanel, heightPanel, ProxyDotRadiusData.method8069(intValue), previewAlpha, blurPower);
   }

   private void drawBlurLayer(MenuDrawContext customDrawContext, float x, float y, float widthPanel, float heightPanel, ProxyDotRadiusData headerRadius, float previewAlpha, float blurPower) {
      float blurLayerAlpha;
      if (!((blurLayerAlpha = this.getBlurLayerAlpha(previewAlpha)) <= 0.001F)) {
         RenderShaders.method5289(MatrixUtils.method664(customDrawContext.pose()), x, y, widthPanel, heightPanel, blurPower, headerRadius, ZenithColor.INSTANCE.method7294(blurLayerAlpha));
      }
   }

   public void renderElementSwapBlur(MenuDrawContext customDrawContext, float floatValue, float y, float contentWidth, float heightPanel, ProxyDotRadiusData headerRadius, float previewAlpha) {
      if (MenuModule.INSTANCE.method5344()) {
         float blurPower;
         if ((blurPower = this.getBlurPower()) != 0.0F) {
            this.drawBlurLayer(customDrawContext, floatValue, y, contentWidth, heightPanel, headerRadius, previewAlpha, blurPower);
         }
      }
   }

   private float getBlurLayerAlpha(float previewAlpha) {
      float f;
      if ((f = Mth.clamp(previewAlpha, 0.0F, 1.0F)) >= 0.75F) {
         float f2;
         return (f2 = (1.0F - f) / 0.25F) * f2 * (3.0F - 2.0F * f2);
      } else {
         float f2;
         return f >= 0.3F ? 1.0F : (float)Math.sqrt(f2 = f / 0.3F);
      }
   }

   public void resetSearch() {
      if (this.searchField != null) {
         this.searchField.setText("");
         this.searchField.setSelected(false);
         this.searchField.setCursor(0);
      }
   }

   public String getSearchValue() {
      return this.searchField == null ? "" : this.searchField.getText();
   }

   public void openTool(int active) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.TOOLS);
      this.toolsSectionPanel.open(active);
      if (mc.gui.screen() != this) {
         mc.gui.setScreen(this);
      }
   }

   public void openCosmetics(int activeTab) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.COSMETICS);
      if (activeTab >= 0) {
         this.cosmeticsSectionPanel.devTab(activeTab);
      }

      if (mc.gui.screen() != this) {
         mc.gui.setScreen(this);
      }
   }

   public void openMarket(int n) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.SCRIPTS);
      this.scriptsSectionPanel.openMarket(n);
      if (mc.gui.screen() != this) {
         mc.gui.setScreen(this);
      }
   }

   public void openScriptSettings(Script workspace) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.SCRIPTS);
      this.scriptsSectionPanel.openScriptSettings(workspace);
      if (mc.gui.screen() != this) {
         mc.gui.setScreen(this);
      }
   }

   public void devShowModule(Module moduleValue) {
      this.ensurePanelsInitialized();
      this.setType(NLMenuScreen.ElementsType.CATEGORY);
      this.inspectorPanel.showFor(moduleValue);
   }

   public void openHudElementSettings(Interface selectedElement) {
      if (selectedElement != null) {
         this.ensurePanelsInitialized();
         this.resetSearch();
         this.setType(NLMenuScreen.ElementsType.INTERFACE);
         this.interfaceSectionPanel.openHudElementSettings(selectedElement);
      }
   }

   public ClientPanel getClientPanel() {
      return this.clientPanel;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      float guiScale = this.getGuiScale();
      float f = this.x + widthPanel / 2.0F;
      float f2 = this.y + heightPanel / 2.0F;
      mouseX = (int)((mouseX - f) / guiScale + f);
      mouseY = (int)((mouseY - f2) / guiScale + f2);
      if (this.crosshairEditor.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else if (this.quickMenuPanel != null && this.quickMenuPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else if (this.inspectorPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else {
         this.type.getPanelSupplier().get().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.crosshairEditor.keyPressed(keyCode, keycode, modifiers)) {
         return true;
      } else {
         if (BindCapture.isActive()) {
            if (BindCapture.owner() == this.inspectorPanel && this.inspectorPanel.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }

            if (this.getType().getPanelSupplier().get().keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }

            if (this.inspectorPanel.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }

            BindCapture.reset();
         }

         if (this.commandField != null && this.commandField.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.runCommand();
               return true;
            }

            if (keyCode == 256) {
               this.commandField.setSelected(false);
               return true;
            }

            if (this.commandField.keyPressed(keyCode, keycode, modifiers)) {
               return true;
            }
         }

         if (this.searchField.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         } else if (this.getType().getPanelSupplier().get().keyPressed(keyCode, keycode, modifiers)) {
            return true;
         } else if (this.inspectorPanel.keyPressed(keyCode, keycode, modifiers)) {
            return true;
         } else if (this.handleNavigation(keyCode)) {
            return true;
         } else {
            if (keyCode == 256 && !this.closing) {
               if (this.quickMenuPanel != null) {
                  this.quickMenuPanel.collapseOpenRow();
                  this.quickMenuPanel.setExpanded(false);
               }

               if (this.searchField != null) {
                  this.searchField.setSelected(false);
               }

               BindCapture.reset();
               this.closing = true;
               this.mouseReleased(0.0, 0.0, 0);
               this.mouseReleased(0.0, 0.0, 0);
               this.mouseReleased(0.0, 0.0, 0);
               Zenith.getInstance().getClientSounds().method1561(Zenith.getInstance().getClientSounds().soundEvent2);
            }

            return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
         }
      }
   }

   private boolean handleNavigation(int keyCode) {
      if (this.type != NLMenuScreen.ElementsType.CATEGORY || this.guiModulePanel == null) {
         return false;
      } else if (keyCode == 70 && WindowUtils.method5475()) {
         this.searchField.setSelected(true);
         return true;
      } else {
         boolean flag = this.searchField != null && this.searchField.isSelected();
         switch (keyCode) {
            case 257:
            case 335:
               return this.guiModulePanel.toggleCaret();
            case 262:
               if (flag) {
                  return false;
               }

               return this.guiModulePanel.openCaret();
            case 263:
               if (flag) {
                  return false;
               } else {
                  if (this.inspectorPanel.isOpen()) {
                     this.inspectorPanel.close();
                     return true;
                  }

                  return false;
               }
            case 264:
               this.guiModulePanel.moveCaret(1);
               return true;
            case 265:
               this.guiModulePanel.moveCaret(-1);
               return true;
            default:
               return false;
         }
      }
   }

   @Override
   public boolean charTyped(CharacterEvent input) {
      return this.charTyped((char)input.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      if (this.crosshairEditor.charTyped(codepoint, n)) {
         return true;
      } else if (this.commandField != null && this.commandField.isSelected() && this.commandField.charTyped(codepoint, n)) {
         return true;
      } else if (this.searchField.charTyped(codepoint, n)) {
         return true;
      } else if (this.inspectorPanel.charTyped(codepoint, n)) {
         return true;
      } else {
         return this.getType().getPanelSupplier().get().charTyped(codepoint, n) ? true : super.charTyped(new CharacterEvent(codepoint));
      }
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseClicked(double x, double y, int mouseToGlfw) {
      float guiScale = this.getGuiScale();
      float f = this.x + widthPanel / 2.0F;
      float f2 = this.y + heightPanel / 2.0F;
      x = (int)((x - f) / guiScale + f);
      y = (int)((y - f2) / guiScale + f2);
      MouseButton enum2 = MouseButton.fromGlfwButton(mouseToGlfw);
      if (BindCapture.owner() == this.inspectorPanel && !MouseBindTarget.bindable(enum2)) {
         this.inspectorPanel.cancelHeaderBindingOutside(x, y);
      }

      if (BindCapture.owner() instanceof InspectorRow inspectorRow && !MouseBindTarget.bindable(enum2)) {
         inspectorRow.cancelModuleBindingOutside(x, y);
      }

      if (this.crosshairEditor.onMouseClicked(x, y, enum2)) {
         return true;
      } else if (BindCapture.owner() instanceof MouseBindTarget mouseBindTarget && MouseBindTarget.bindable(enum2) && mouseBindTarget.bindMouse(enum2)) {
         return true;
      } else if (this.quickMenuPanel != null
         && !this.getType().getPanelSupplier().get().hasLeftColumn()
         && this.quickMenuPanel.onMouseClicked(x, y, enum2)) {
         return true;
      } else if (this.previewStage.onMouseClicked(x, y, enum2)) {
         return true;
      } else if (this.searchField.onMouseClicked(x, y, enum2)) {
         this.commandField.setSelected(false);
         return true;
      } else if (this.commandBarBounds != null && this.commandBarBounds.contains(x, y)) {
         this.searchField.setSelected(false);
         this.commandField.setSelected(true);
         this.commandFeedback = "";
         return true;
      } else if (this.commandField.onMouseClicked(x, y, enum2)) {
         return true;
      } else {
         try {
            ElementPanel elementPanel = this.getType().getPanelSupplier().get();
            if (elementPanel.onHeaderButtonsClicked(x, y, enum2)) {
               return true;
            }

            if (MenuModule.INSTANCE.method5348() && mouseToGlfw == 2 && this.dragBounds != null && this.dragBounds.contains(x, y)) {
               this.draggingGui = true;
               return true;
            }

            if (this.inspectorPanel.onMouseClicked(x, y, enum2)) {
               return true;
            }

            if (elementPanel.onMouseClicked(x, y, enum2)) {
               return true;
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return !this.getType().getPanelSupplier().get().hasLeftColumn() && this.categoryPanel.onMouseClicked(x, y, enum2)
            ? true
            : super.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), false);
      }
   }

   @Override
   public boolean mouseReleased(MouseButtonEvent click) {
      return this.mouseReleased(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseReleased(double x, double y, int mouseToGlfw) {
      if (mouseToGlfw == 2) {
         this.draggingGui = false;
      }

      this.unlockGuiScale();
      float guiScale = this.getGuiScale();
      float f = this.x + widthPanel / 2.0F;
      float f2 = this.y + heightPanel / 2.0F;
      x = (int)((x - f) / guiScale + f);
      y = (int)((y - f2) / guiScale + f2);
      this.crosshairEditor.onMouseReleased(x, y, MouseButton.fromGlfwButton(mouseToGlfw));
      this.previewStage.onMouseReleased();
      if (this.quickMenuPanel != null) {
         this.quickMenuPanel.onMouseReleased(x, y, MouseButton.fromGlfwButton(mouseToGlfw));
      }

      if (this.getType().getPanelSupplier().get().onMouseReleased(x, y, MouseButton.fromGlfwButton(mouseToGlfw))) {
         return true;
      } else {
         this.inspectorPanel.onMouseReleased(x, y, MouseButton.fromGlfwButton(mouseToGlfw));
         return super.mouseReleased(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))));
      }
   }

   @Override
   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      return this.mouseDragged(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()), offsetX, offsetY);
   }

   public boolean mouseDragged(double x, double y, int mouseToGlfw, double offsetX, double offsetY) {
      if (MenuModule.INSTANCE.method5348() && this.draggingGui && mouseToGlfw == 2) {
         this.rawX += (float)offsetX;
         this.rawY += (float)offsetY;
         MenuModule.INSTANCE.method5353(this.rawX, this.rawY);
         return true;
      } else {
         float guiScale = this.getGuiScale();
         float f = this.x + widthPanel / 2.0F;
         float f2 = this.y + heightPanel / 2.0F;
         double doubleValue = (int)((x - f) / guiScale + f);
         double mouseY = (int)((y - f2) / guiScale + f2);
         if (this.quickMenuPanel != null && this.quickMenuPanel.onMouseDragged(offsetX, guiScale)) {
            return true;
         } else if (this.inspectorPanel.onMouseDragged(doubleValue, mouseY)) {
            return true;
         } else if (this.crosshairEditor.onMouseDragged(offsetX, offsetY)) {
            return true;
         } else if (this.previewStage.onMouseDragged(doubleValue, mouseY, offsetX, offsetY)) {
            return true;
         } else {
            return this.getType().getPanelSupplier().get().onMouseDragged(doubleValue, mouseY, mouseToGlfw, offsetX, offsetY)
               ? true
               : super.mouseDragged(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), offsetX, offsetY);
         }
      }
   }

   @Override
   public void tick() {
      if (this.closing && this.animationClose.getValue() == 0.0F) {
         this.onClose();
      }

      this.getType().getPanelSupplier().get().tick();
      super.tick();
   }

   @Override
   public void onFilesDrop(List<Path> paths) {
      if (this.scriptsSectionPanel != null) {
         this.scriptsSectionPanel.filesDropped(paths);
      }
   }

   @Override
   public void removed() {
      this.closing = true;
      this.crosshairEditor.close();
      BindCapture.reset();
      this.previewStage.releaseModel();
      if (this.searchField != null) {
         this.searchField.setSelected(false);
      }

      this.draggingGui = false;
      this.mouseReleased(0.0, 0.0, 0);
      this.mouseReleased(0.0, 0.0, 0);
      this.mouseReleased(0.0, 0.0, 0);
      super.removed();
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   public boolean isRenderHud() {
      return mc.gui.screen() == this && !this.closing && this.getType() == NLMenuScreen.ElementsType.INTERFACE && this.interfaceSectionPanel.isHudTab();
   }

   private float clampRenderPosition(float rawX, float widthPanel, float guiScaledWidth) {
      float max = Math.max(0.0F, guiScaledWidth - widthPanel);
      return Mth.clamp(rawX, 0.0F, max);
   }

   public boolean isFinish() {
      return this.animationClose.getValue() == 0.0F && this.closing;
   }

   public void initialize() {
      this.ensurePanelsInitialized();
   }

   public boolean isShortMode() {
      return this.clientPanel != null && !this.clientPanel.getRenderDescription().getSetting().isEnabled();
   }

   public void setRenderIcon(boolean renderIcon) {
      this.clientPanel.getRenderIcon().getSetting().setEnabled(renderIcon);
   }

   public boolean isRenderIcon() {
      return this.clientPanel.getRenderIcon().getSetting().isEnabled();
   }

   public boolean isStreamerMode() {
      return StreamerModeModule.INSTANCE.isEnabled();
   }

   public void toggleStreamerMode() {
      StreamerModeModule.INSTANCE.toggle();
   }

   public void lockGuiScale() {
      if (!this.scaleLocked) {
         this.lockedScale = this.getGuiScale();
         this.scaleLocked = true;
      }
   }

   public void unlockGuiScale() {
      this.scaleLocked = false;
   }

   public float getGuiScale() {
      if (this.scaleLocked) {
         return this.lockedScale;
      } else if (this.clientPanel == null) {
         return 1.0F;
      } else {
         float f = (Float.isNaN(this.smoothScale) ? this.clientPanel.getGuiScale().getApplayValue() : this.smoothScale) / 100.0F;
         float min = 0.35F;
         float f2 = 100.0F;
         float guiScaledWidth = mc.getWindow().getGuiScaledWidth();
         float guiScaledHeight = mc.getWindow().getGuiScaledHeight();
         float f3 = guiScaledWidth / (widthPanel + f2);
         float f4 = guiScaledHeight / (heightPanel + f2);
         float max = Math.min(f3, f4);
         if (Float.isNaN(f) || Float.isInfinite(f)) {
            return 1.0F;
         } else if (max <= 0.0F) {
            return min;
         } else {
            return max < min ? max : Mth.clamp(f, min, max);
         }
      }
   }

   public void safe(JsonObject json) {
      this.ensurePanelsInitialized();
      JsonObject json2 = new JsonObject();
      json2.addProperty("type", this.type.name());
      JsonObject json3 = new JsonObject();
      this.profilePanel.safe(json3);
      json2.add("profilePanel", json3);
      JsonObject json4 = new JsonObject();
      this.clientPanel.safe(json4);
      json2.add("clientPanel", json4);
      json.add("NLMenuScreen", json2);
   }

   public void load(JsonObject json) {
      this.ensurePanelsInitialized();
      if (json.has("NLMenuScreen") && json.get("NLMenuScreen").isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject("NLMenuScreen");
         if (jsonObject.has("type")) {
            try {
               this.setType(NLMenuScreen.ElementsType.valueOf(jsonObject.get("type").getAsString()));
            } catch (IllegalArgumentException e) {
            }
         }

         if (jsonObject.has("profilePanel") && jsonObject.get("profilePanel").isJsonObject()) {
            this.profilePanel.load(jsonObject.getAsJsonObject("profilePanel"));
         }

         if (jsonObject.has("clientPanel") && jsonObject.get("clientPanel").isJsonObject()) {
            this.clientPanel.load(jsonObject.getAsJsonObject("clientPanel"));
         }
      }
   }

   public void refreshModules(Collection<Module> moduleValues) {
      for (Module moduleValue : moduleValues) {
         this.inspectorPanel.forget(moduleValue);
      }

      if (this.init && this.guiModulePanel != null) {
         this.guiModulePanel.rebuild();
      }
   }

   private void ensurePanelsInitialized() {
      if (!this.init) {
         this.guiModulePanel = new GuiModulePanel();
         this.toolsSectionPanel = new ToolsSectionPanel();
         this.cloudSectionPanel = new CloudSectionPanel(new FriendsSectionPanel(), new CloudChatSectionPanel(), new VoiceSectionPanel());
         this.cosmeticsSectionPanel = new WardrobeSectionPanel();
         this.cosmeticsSectionPanel.setEmbedded(true);
         this.interfaceSectionPanel = new InterfaceSectionPanel();
         this.scriptsSectionPanel = new ScriptsSectionPanel();
         this.profilePanel = new ProfilePanel();
         this.clientPanel = new ClientPanel();
         this.categoryPanel = new CategoryPanel();
         this.init = true;
         this.quickMenuPanel = new QuickMenuPanel();
      }
   }

   public float getBlurPower() {
      if (this.clientPanel == null) {
         return 0.0F;
      } else {
         return Float.isNaN(this.smoothBlur) ? this.clientPanel.getBlurStrength().getSetting().getValue3() : this.smoothBlur;
      }
   }

   private void updateSmoothValues() {
      if (this.clientPanel != null) {
         this.smoothBlur = approach(this.smoothBlur, this.clientPanel.getBlurStrength().getSetting().getValue3(), 0.3F, 0.05F);
         this.smoothScale = approach(this.smoothScale, this.clientPanel.getGuiScale().getApplayValue(), 0.3F, 0.15F);
      }
   }

   private static float approach(float smoothBlur, float applayValue, float alpha1, float f) {
      if (Float.isNaN(smoothBlur)) {
         return applayValue;
      } else {
         float f2 = Mth.lerp(alpha1, smoothBlur, applayValue);
         return Math.abs(applayValue - f2) < f ? applayValue : f2;
      }
   }

   public boolean isElementSwapBlurActive() {
      return this.prevType != null || this.guiModulePanel != null && this.guiModulePanel.isCategorySwitching();
   }

   public void setType(NLMenuScreen.ElementsType type) {
      if (type != this.type) {
         if (this.prevType != null && !this.setTypeAnimation.isDone()) {
            float f = this.setTypeAnimation.getValue();
            NLMenuScreen.ElementsType prevType = this.type;
            if (this.prevType == type) {
               this.prevType = prevType;
               this.type = type;
               this.setTypeAnimation.reset(1.0F - f);
               this.setTypeAnimation.setTarget(1.0F);
               return;
            }

            if (f < 0.5F) {
               prevType.getPanelSupplier().get().close();
               this.type = type;
               return;
            }

            this.prevType.getPanelSupplier().get().close();
         }

         this.setTypeAnimation.reset(0.0F);
         this.setTypeAnimation.setTarget(1.0F);
         this.prevType = this.type;
         this.type = type;
      }
   }

   public boolean isSearch() {
      return this.searchField != null && this.searchField.isSelected();
   }

   @Generated
   public float getContentDip() {
      return this.contentDip;
   }

   @Generated
   public ProfilePanel getProfilePanel() {
      return this.profilePanel;
   }

   @Generated
   public InspectorPanel getInspectorPanel() {
      return this.inspectorPanel;
   }

   @Generated
   public ModulePreviewStage getPreviewStage() {
      return this.previewStage;
   }

   @Generated
   public CrosshairEditorPanel getCrosshairEditor() {
      return this.crosshairEditor;
   }

   @Generated
   public GuiModulePanel getGuiModulePanel() {
      return this.guiModulePanel;
   }

   @Generated
   public ToolsSectionPanel getToolsSectionPanel() {
      return this.toolsSectionPanel;
   }

   @Generated
   public CloudSectionPanel getCloudSectionPanel() {
      return this.cloudSectionPanel;
   }

   @Generated
   public WardrobeSectionPanel getCosmeticsSectionPanel() {
      return this.cosmeticsSectionPanel;
   }

   @Generated
   public InterfaceSectionPanel getInterfaceSectionPanel() {
      return this.interfaceSectionPanel;
   }

   @Generated
   public QuickMenuPanel getQuickMenuPanel() {
      return this.quickMenuPanel;
   }

   @Generated
   public NLMenuScreen.ElementsType getType() {
      return this.type;
   }

   @Generated
   public boolean isClosing() {
      return this.closing;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   public static enum ElementsType {
      CATEGORY("Category", "0", () -> Zenith.getInstance().getNLMenuScreen().guiModulePanel),
      TOOLS("Tools", "7", () -> Zenith.getInstance().getNLMenuScreen().toolsSectionPanel),
      CLOUD("Cloud", "8", () -> Zenith.getInstance().getNLMenuScreen().cloudSectionPanel),
      COSMETICS("Cosmetics", "9", () -> Zenith.getInstance().getNLMenuScreen().cosmeticsSectionPanel),
      INTERFACE("Interface", ":", () -> Zenith.getInstance().getNLMenuScreen().interfaceSectionPanel),
      SCRIPTS("Scripts & Configs", ";", () -> Zenith.getInstance().getNLMenuScreen().scriptsSectionPanel);

      public final String name;
      public final String icon;
      public final Supplier<ElementPanel> panelSupplier;

      @Generated
      private ElementsType(String name, String icon, Supplier<ElementPanel> panelSupplier) {
         this.name = name;
         this.icon = icon;
         this.panelSupplier = panelSupplier;
      }

      @Generated
      public String getName() {
         return this.name;
      }

      @Generated
      public String getIcon() {
         return this.icon;
      }

      @Generated
      public Supplier<ElementPanel> getPanelSupplier() {
         return this.panelSupplier;
      }
   }
}
