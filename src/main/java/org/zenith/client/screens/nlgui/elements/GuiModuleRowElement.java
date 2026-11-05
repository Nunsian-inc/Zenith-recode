package org.zenith.client.screens.nlgui.elements;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.TextureFilterUtils;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiModuleRowElement extends Element implements MouseBindTarget {
   private static final float MARKER_WIDTH = 2.0F;
   private static final float TEXT_INSET = 8.0F;
   private static final float ROW_GAP = 5.0F;
   private static final float HEART_SIZE = 6.0F;
   private static final Identifier HEART_OUTLINE = Zenith.method1501("textures/heart_outline.png");
   private static final Identifier HEART_FILLED = Zenith.method1501("textures/heart_filled.png");
   private static final Set<AbstractTexture> SMOOTHED = Collections.newSetFromMap(new WeakHashMap<>());
   private final BindLabel bindLabel = new BindLabel();
   private final Animation labelSlot = new Animation(240L, 0.0F, Easing.INSTANCE12);
   private boolean labelSlotKnown;
   private final Module module;
   private final Animation animationEnable = new Animation(160L, Easing.INSTANCE12);
   private final Animation animationHovered = new Animation(120L, Easing.INSTANCE12);
   private final Animation animationSelected = new Animation(120L, Easing.INSTANCE12);
   private final Animation searchVisibleAnimation = new Animation(180L, 1.0F, Easing.INSTANCE13);
   private static final long FAVOURITE_MS = 700L;
   private static final long UNFAVOURITE_MS = 320L;
   private static final int SPARKS = 10;
   private static final Random SPARK_RNG = new Random();
   private long favouriteAt = -1L;
   private long unfavouriteAt = -1L;
   private static final long TOGGLE_MS = 560L;
   private static final long TOGGLE_OFF_MS = 300L;
   private Boolean seenEnabled;
   private long toggledOnAt = -1L;
   private long toggledOffAt = -1L;
   private final float[] sparkAngle = new float[10];
   private final float[] sparkReach = new float[10];
   private static final float SLIDE_SPEED = 0.35F;
   private static final float SLIDE_SNAP = 0.15F;
   private float slideOffset;
   private boolean slideKnown;
   private BoundsData bounds;
   private BoundsData bindBounds;
   private BoundsData heartBounds;
   private boolean binding;
   private boolean searchVisible = true;
   private float width = 236.0F;
   private static final Map<String, String> LOWER_NAMES = new ConcurrentHashMap<>();
   private boolean showCategoryIcon;

   private static void smooth(Identifier location) {
      try {
         AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
         if (texture != null && SMOOTHED.add(texture)) {
            TextureFilterUtils.smooth(location);
         }
      } catch (Exception e) {
      }
   }

   public GuiModuleRowElement(Module surfacedModule) {
      this.module = surfacedModule;
      if (surfacedModule.isEnabled()) {
         this.animationEnable.method2809(1.0F);
      }
   }

   @Override
   public String getName() {
      return this.module.getName();
   }

   @Override
   public float getHeight() {
      return 16.0F;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   public boolean isEnable() {
      return this.module.isEnabled();
   }

   public boolean isSelected() {
      return Zenith.getInstance().getNLMenuScreen().getInspectorPanel().isShowing(this.module);
   }

   public boolean isBinding() {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      return this.binding;
   }

   public void setSearchVisible(boolean searchVisible) {
      this.searchVisible = searchVisible;
      if (!searchVisible) {
         this.cancelBinding();
      }
   }

   public void cancelBinding() {
      this.binding = false;
      BindCapture.end(this);
   }

   public void resetSearchVisible(boolean searchVisible) {
      this.searchVisible = searchVisible;
      this.searchVisibleAnimation.method2809(searchVisible ? 1.0F : 0.0F);
      this.forgetSlide();
   }

   public float slideTo(float slideOffset) {
      if (!this.slideKnown) {
         this.slideKnown = true;
         this.slideOffset = slideOffset;
         return this.slideOffset;
      } else {
         float f;
         if (Math.abs((f = Mth.lerp(0.35F, this.slideOffset, slideOffset)) - slideOffset) < 0.15F) {
            f = slideOffset;
         }

         this.slideOffset = f;
         return f;
      }
   }

   public void forgetSlide() {
      this.slideKnown = false;
   }

   public float updateSearchVisible() {
      return this.searchVisibleAnimation.method2808(this.searchVisible ? 1.0F : 0.0F);
   }

   public float getSearchVisibleProgress() {
      return this.searchVisibleAnimation.getValue();
   }

   public boolean shouldRenderInSearch() {
      return this.searchVisible || this.searchVisibleAnimation.getValue() > 0.01F;
   }

   public boolean matchesOwnSearch(String searchQuery) {
      return lowerName(this.module).contains(searchQuery);
   }

   public boolean matchesSearch(String searchQuery) {
      if (this.matchesOwnSearch(searchQuery)) {
         return true;
      } else {
         for (Module surfacedModule : this.module.getSurfacedModules()) {
            if (lowerName(surfacedModule).contains(searchQuery)) {
               return true;
            }
         }

         return false;
      }
   }

   private static String lowerName(Module surfacedModule) {
      String name = surfacedModule.getName();
      return LOWER_NAMES.computeIfAbsent(name, String::toLowerCase);
   }

   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float floatValue, float y, float previewAlpha, BoundsData boundsData) {
      this.isBinding();
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.bounds = new BoundsData(floatValue, y, this.width, 16.0F);
         boolean flag = this.bounds.contains(lastClickX, lastClickY) && (boundsData == null || boundsData.contains(lastClickX, lastClickY));
         float f = this.animationEnable.method2808(this.module.isEnabled() ? 1.0F : 0.0F);
         float f2 = this.animationHovered.method2808(flag ? 1.0F : 0.0F);
         float f3 = this.animationSelected.method2808(this.isSelected() ? 1.0F : 0.0F);
         ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
         float f4;
         if ((f4 = Math.max(f3, f2 * 0.5F)) > 0.002F) {
            ZenithColor zenithColor2 = f3 >= f2 * 0.5F
               ? currentStyle.getDisableActiveBg().getZenithColor()
               : currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(2.0F);
            menuDrawContext.drawRect(floatValue, y, this.width, 16.0F, zenithColor2.method7294(f4 * previewAlpha));
         }

         if (f3 > 0.01F) {
            menuDrawContext.drawRect(floatValue, y + 2.0F, 2.0F, 12.0F, currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * f3));
         }

         float f5 = floatValue + 8.0F;
         float f6 = y + 5.0F;
         this.heartBounds = new BoundsData(f5 - 2.0F, y, 10.0F, 16.0F);
         boolean flag2 = this.heartBounds.contains(lastClickX, lastClickY) && (boundsData == null || boundsData.contains(lastClickX, lastClickY));
         boolean priority = this.module.isPriority();
         long now = System.currentTimeMillis();
         float progressOf2 = progressOf(this.favouriteAt, 700L, now);
         float progressOf3 = progressOf(this.unfavouriteAt, 320L, now);
         if (progressOf2 >= 1.0F) {
            this.favouriteAt = -1L;
         }

         if (progressOf3 >= 1.0F) {
            this.unfavouriteAt = -1L;
         }

         if (progressOf2 < 1.0F) {
            this.renderFavouriteSweep(menuDrawContext, floatValue, y, progressOf2, zenithColor, previewAlpha);
         }

         boolean enabled = this.module.isEnabled();
         if (this.seenEnabled != null && this.seenEnabled != enabled) {
            if (enabled) {
               this.toggledOnAt = now;
               this.toggledOffAt = -1L;
            } else {
               this.toggledOffAt = now;
               this.toggledOnAt = -1L;
            }
         }

         this.seenEnabled = enabled;
         float progressOf4 = progressOf(this.toggledOnAt, 560L, now);
         float progressOf5 = progressOf(this.toggledOffAt, 300L, now);
         if (progressOf4 >= 1.0F) {
            this.toggledOnAt = -1L;
         }

         if (progressOf5 >= 1.0F) {
            this.toggledOffAt = -1L;
         }

         if (progressOf4 < 1.0F && progressOf2 >= 1.0F) {
            this.renderSweep(menuDrawContext, floatValue, y, progressOf4, zenithColor, previewAlpha, 0.55F);
         }

         if (progressOf5 < 1.0F) {
            menuDrawContext.drawRect(floatValue, y, this.width, 16.0F, currentStyle.getTextTertiary().getZenithColor().method7294(0.1F * (1.0F - progressOf5) * (1.0F - progressOf5) * previewAlpha));
         }

         ZenithColor zenithColor2 = priority ? zenithColor : currentStyle.getTextTertiary().getZenithColor().method7294(flag2 ? 0.85F : 0.45F);
         if (progressOf2 < 1.0F) {
            zenithColor2 = zenithColor2.method7295(ZenithColor.INSTANCE, 0.7F * (float)Math.exp(-6.0F * progressOf2) * Math.min(1.0F, progressOf2 * 12.0F));
         }

         float heartScale2 = heartScale(progressOf2, progressOf3);
         float f7 = f5 + 3.0F;
         float f8 = f6 + 3.0F;
         if (progressOf2 < 1.0F) {
            this.renderFavouriteBurst(menuDrawContext, f7, f8, progressOf2, zenithColor, previewAlpha);
         }

         if (progressOf3 < 1.0F) {
            renderRing(menuDrawContext, f7, f8, progressOf3, 5.0F, currentStyle.getTextTertiary().getZenithColor().method7294(0.6F), previewAlpha);
         }

         Identifier location = priority ? HEART_FILLED : HEART_OUTLINE;
         smooth(location);
         float guiScaledWidth = 6.0F * heartScale2;
         menuDrawContext.drawTexture(location, f7 - guiScaledWidth / 2.0F, f8 - guiScaledWidth / 2.0F, guiScaledWidth, guiScaledWidth, zenithColor2.method7294(previewAlpha));
         float f9 = floatValue + this.width - 8.0F + 5.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
         String text = BindLabel.text(this.binding, this.module.getKeyCode(), this.module.getKeyModifiers());
         float floatValue2 = text == null ? 0.0F : font.width(text) + 5.0F;
         if (!this.labelSlotKnown) {
            this.labelSlotKnown = true;
            this.labelSlot.method2809(floatValue2);
         }

         float f10 = f9 - this.labelSlot.method2808(floatValue2) - 5.0F;
         Font font2 = (f > 0.5F ? Fonts.NEW_SEMIBOLD : Fonts.NEW_MEDIUM).getFont(5.5F);
         ZenithColor zenithColor3 = currentStyle.getTextTertiary()
            .getZenithColor()
            .method7295(currentStyle.getTextEnable().getZenithColor(), Math.max(f, f2 * 0.45F))
            .method7294(previewAlpha);
         float floatValue3 = f5 + 6.0F + 5.0F;
         float centeredTextY = y + (16.0F - font2.height()) / 2.0F;
         if (this.showCategoryIcon) {
            MenuGlyph.forCategory(this.module.getCategory()).draw(menuDrawContext, floatValue3, y + 5.0F, zenithColor3);
            floatValue3 += 10.0F;
         }

         String clip = font2.clip(this.module.getName(), f10 - floatValue3);
         menuDrawContext.drawText(font2, clip, floatValue3, centeredTextY, zenithColor3);
         float f11 = floatValue3 + font2.width(clip);
         this.bindBounds = this.bindLabel.render(menuDrawContext, lastClickX, lastClickY, y, 16.0F, f9 - 5.0F, text, this.binding, font, f11 + 5.0F, flag, previewAlpha, boundsData, currentStyle);
         if (flag) {
            String description = this.module.getDescription();
            if (description != null && !description.isBlank()) {
               HoverInfo.request(this.module.getName(), description);
            }
         }
      }
   }

   private void startFavouriteBurst() {
      this.unfavouriteAt = -1L;
      this.favouriteAt = System.currentTimeMillis();
      float f = (float) (Math.PI / 5);

      for (int i = 0; i < 10; i++) {
         this.sparkAngle[i] = i * f + (SPARK_RNG.nextFloat() - 0.5F) * f * 0.8F;
         this.sparkReach[i] = 7.0F + SPARK_RNG.nextFloat() * 6.0F;
      }
   }

   private static float progressOf(long favouriteAt, long l, long now) {
      return favouriteAt < 0L ? 1.0F : Mth.clamp((float)(now - favouriteAt) / (float)l, 0.0F, 1.0F);
   }

   private static float heartScale(float f, float f2) {
      if (f < 1.0F) {
         if (f < 0.16F) {
            float f3 = f / 0.16F;
            return 1.0F + 0.85F * (1.0F - (1.0F - f3) * (1.0F - f3));
         } else {
            float f3 = f - 0.16F;
            return 1.0F + 0.85F * (float)(Math.exp(-6.5F * f3) * Math.cos(17.0F * f3));
         }
      } else {
         return f2 < 1.0F ? 1.0F - 0.35F * Mth.sin(f2 * (float) Math.PI) : 1.0F;
      }
   }

   private void renderFavouriteSweep(MenuDrawContext menuDrawContext, float floatValue, float y, float f, ZenithColor zenithColor, float previewAlpha) {
      this.renderSweep(menuDrawContext, floatValue, y, f, zenithColor, previewAlpha, 1.0F);
   }

   private void renderSweep(MenuDrawContext menuDrawContext, float floatValue, float y, float f, ZenithColor zenithColor, float previewAlpha, float f2) {
      float f3 = (1.0F - f) * previewAlpha * f2;
      menuDrawContext.drawRect(floatValue, y, this.width, 16.0F, zenithColor.method7294(0.1F * f3));
      float f4 = f < 0.5F ? 2.0F * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 2.0) / 2.0F;
      float f5 = floatValue - 30.0F + (this.width + 60.0F) * f4;
      float f6 = 26.0F;
      float previewAlpha2;
      if (!((previewAlpha2 = 0.3F * f2 * Mth.sin(f * (float) Math.PI) * previewAlpha) <= 0.002F)) {
         ZenithColor zenithColor2 = zenithColor.method7294(0.0F);
         ZenithColor zenithColor3 = zenithColor.method7295(ZenithColor.INSTANCE, 0.35F).method7294(previewAlpha2);
         this.drawBand(menuDrawContext, f5 - f6, f5, y, floatValue, zenithColor2, zenithColor3);
         this.drawBand(menuDrawContext, f5, f5 + f6, y, floatValue, zenithColor3, zenithColor2);
      }
   }

   private void drawBand(MenuDrawContext menuDrawContext, float f, float f2, float y, float floatValue, ZenithColor zenithColor, ZenithColor zenithColor2) {
      float floatValue2 = Math.max(f, floatValue);
      float f3;
      if (!((f3 = Math.min(f2, floatValue + this.width)) - floatValue2 <= 0.1F)) {
         float f4 = f2 - f;
         ZenithColor clientColor = zenithColor.method7295(zenithColor2, (floatValue2 - f) / f4);
         ZenithColor zenithColor3 = zenithColor.method7295(zenithColor2, (f3 - f) / f4);
         menuDrawContext.drawRoundedRect(floatValue2, y, f3 - floatValue2, 16.0F, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(clientColor, clientColor, zenithColor3, zenithColor3));
      }
   }

   private void renderFavouriteBurst(MenuDrawContext menuDrawContext, float f, float f2, float f3, ZenithColor zenithColor, float previewAlpha) {
      float f4;
      if ((f4 = Math.min(1.0F, f3 / 0.45F)) < 1.0F) {
         float intValue = 3.0F + f4 * 6.0F;
         menuDrawContext.drawRoundedRect(
            f - intValue, f2 - intValue, intValue * 2.0F, intValue * 2.0F, ProxyDotRadiusData.method8069(intValue), zenithColor.method7294(0.35F * (1.0F - f4) * previewAlpha)
         );
      }

      renderRing(menuDrawContext, f, f2, Math.min(1.0F, f3 / 0.6F), 10.0F, zenithColor, previewAlpha);
      float f5 = 1.0F - (1.0F - f3) * (1.0F - f3) * (1.0F - f3);
      float previewAlpha2 = (1.0F - f3) * previewAlpha;

      for (int i = 0; i < 10; i++) {
         float f6 = this.sparkReach[i] * f5;
         float f7 = f + Mth.cos(this.sparkAngle[i]) * f6;
         float f8 = f2 + Mth.sin(this.sparkAngle[i]) * f6 + 3.5F * f3 * f3;
         ZenithColor zenithColor2 = (i % 2 == 0 ? zenithColor : zenithColor.method7295(ZenithColor.INSTANCE, 0.6F)).method7294(previewAlpha2);
         if (i % 3 == 0) {
            float guiScaledWidth = 2.6F * (1.0F - f3 * 0.6F);
            smooth(HEART_FILLED);
            menuDrawContext.drawTexture(HEART_FILLED, f7 - guiScaledWidth / 2.0F, f8 - guiScaledWidth / 2.0F, guiScaledWidth, guiScaledWidth, zenithColor2);
         } else {
            float width;
            if (!((width = 1.5F * (1.0F - f3)) <= 0.05F)) {
               menuDrawContext.drawRoundedRect(f7 - width / 2.0F, f8 - width / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor2);
            }
         }
      }
   }

   private static void renderRing(MenuDrawContext menuDrawContext, float f, float f2, float f3, float f4, ZenithColor zenithColor, float previewAlpha) {
      if (!(f3 >= 1.0F)) {
         float intValue = 3.0F + f3 * f4;
         float previewAlpha2 = (1.0F - f3) * (1.0F - f3) * 0.8F * previewAlpha;
         menuDrawContext.drawRoundedBorder(
            f - intValue, f2 - intValue, intValue * 2.0F, intValue * 2.0F, 0.3F + 0.6F * (1.0F - f3), ProxyDotRadiusData.method8069(intValue), zenithColor.method7294(previewAlpha2)
         );
      }
   }

   private void beginBinding() {
      this.binding = true;
      this.bindLabel.waiting();
      BindCapture.begin(this);
   }

   private void onBindSet() {
      this.bindLabel.set();
   }

   private void onBindCleared(boolean flag) {
      this.bindLabel.cleared();
   }

   @Override
   public boolean bindMouse(Enum3 enum2) {
      if (this.isBinding() && this.module != null && MouseBindTarget.bindable(enum2)) {
         this.module.setKeyCode(enum2.getIntValue());
         this.module.setKeyModifiers(0);
         this.binding = false;
         BindCapture.end(this);
         this.onBindSet();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      if (this.bounds == null || !this.bounds.contains(lastClickX, lastClickY)) {
         return false;
      } else if (this.bindMouse(enum2)) {
         return true;
      } else if (this.heartBounds != null && this.heartBounds.contains(lastClickX, lastClickY) && enum2 == Enum3.LEFT) {
         boolean priority = !this.module.isPriority();
         this.module.setPriority(priority);
         if (priority) {
            this.startFavouriteBurst();
            Zenith.getInstance().getClientSounds().method1562();
         } else {
            this.favouriteAt = -1L;
            this.unfavouriteAt = System.currentTimeMillis();
            Zenith.getInstance().getClientSounds().method1570(true);
         }

         return true;
      } else if (this.bindBounds != null && this.bindBounds.contains(lastClickX, lastClickY)) {
         if (enum2 == Enum3.RIGHT) {
            boolean flag = this.module.getKeyCode() != -1 && this.module.getKeyCode() != 0;
            this.module.setKeyCode(-1);
            this.module.setKeyModifiers(0);
            this.binding = false;
            BindCapture.end(this);
            this.onBindCleared(flag);
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.beginBinding();
            Zenith.getInstance().getClientSounds().method1570(false);
         }

         return true;
      } else if (enum2 == Enum3.MIDDLE) {
         this.beginBinding();
         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else if (enum2 == Enum3.RIGHT) {
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().toggleFor(this.module);
         Zenith.getInstance().getClientSounds().method1570(true);
         return true;
      } else {
         this.module.toggle();
         return true;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (!this.isBinding()) {
         return false;
      } else {
         if (keyCode == 256) {
            boolean flag = this.module.getKeyCode() != -1 && this.module.getKeyCode() != 0;
            this.module.setKeyCode(-1);
            this.module.setKeyModifiers(0);
            this.onBindCleared(flag);
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.module.setKeyCode(keyCode);
            this.module.setKeyModifiers(KeyName.method6700(keyCode, modifiers));
            this.onBindSet();
            Zenith.getInstance().getClientSounds().method1566();
         }

         this.binding = false;
         BindCapture.end(this);
         return true;
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setShowCategoryIcon(boolean showCategoryIcon) {
      this.showCategoryIcon = showCategoryIcon;
   }
}
