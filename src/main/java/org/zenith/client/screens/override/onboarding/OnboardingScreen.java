package org.zenith.client.screens.override.onboarding;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.MainMenuScreen;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.utility.api.OnboardingUtil;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class OnboardingScreen extends Screen implements MinecraftAccess {
   private static final float GUTTER = MenuTheme.px(96.0F);
   private static final float NUMBER_HEIGHT = MenuTheme.px(40.0F);
   private static final float TITLE_BLOCK = MenuTheme.px(42.0F);
   private static final float BUTTON_WIDTH = MenuTheme.px(384.0F);
   private static final float STEPS_HEIGHT = MenuTheme.px(40.0F);
   private static final float STEP_BAR_WIDTH = MenuTheme.px(45.0F);
   private static final float STEP_BAR_HEIGHT = MenuTheme.px(2.0F);
   private static final float STEP_BAR_GAP = MenuTheme.px(4.0F);
   private static final float DESCRIPTION_WIDTH = MenuTheme.px(852.0F);
   private final List<OnboardingScreen.Step> steps = new ArrayList<>();
   private final MenuButton primaryButton = MenuButton.accent(MenuIcons.ACCEPT, "Start Setup");
   private final Animation openTransition = new Animation(400L, 0.0F, Easing.INSTANCE7);
   private final Animation stepTransition = new Animation(280L, 1.0F, Easing.INSTANCE7);
   private int index;
   private boolean prepared;

   public OnboardingScreen() {
      super(Component.literal("Zenith Setup"));
      this.steps.add(new OnboardingScreen.WelcomeStep());
      this.steps.add(new OnboardingScreen.ThemeStep());
      this.steps.add(new OnboardingScreen.ServerStep());
      this.steps.add(new OnboardingScreen.HotkeyStep());
      this.primaryButton.onClick(this::advance);
      this.syncButton();
   }

   private OnboardingScreen.Step step() {
      return this.steps.get(this.index);
   }

   private void syncButton() {
      this.primaryButton.setIcon(this.step().buttonIcon());
      this.primaryButton.setLabel(this.step().buttonLabel());
   }

   private void advance() {
      this.step().commit();
      if (this.index + 1 < this.steps.size()) {
         this.index++;
         this.syncButton();
         this.stepTransition.method2809(0.0F);
         this.step().enter();
      } else {
         OnboardingUtil.complete();
         boolean flag = false;

         for (OnboardingScreen.Step step : this.steps) {
            if (step instanceof OnboardingScreen.ServerStep serverStep && serverStep.picked() != null) {
               flag = true;
               break;
            }
         }

         MainMenuScreen mainMenuScreen = Zenith.getInstance().getMainMenuScreen();
         mc.gui.setScreen(mainMenuScreen);
         if (flag) {
            mainMenuScreen.select(2);
         }
      }
   }

   @Override
   public void init() {
      Zenith.getInstance().getMenuParticleRenderer().init();
      if (!this.prepared) {
         for (OnboardingScreen.Step step : this.steps) {
            step.prepare();
         }

         this.prepared = true;
         this.openTransition.method2809(0.0F);
         this.stepTransition.method2809(0.0F);
         this.step().enter();
      }

      super.init();
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext graphics = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      Window window = mc.getWindow();
      float guiScaledWidth = window.getGuiScaledWidth();
      float guiScaledHeight = window.getGuiScaledHeight();
      graphics.drawTexture(Zenith.method1501("menu/background.png"), 0.0F, 0.0F, guiScaledWidth, guiScaledHeight, ZenithColor.INSTANCE);
      Zenith.getInstance().getMenuParticleRenderer().renderParticles(graphics);
      this.openTransition.method2808(1.0F);
      this.stepTransition.method2808(1.0F);
      OnboardingScreen.Step step = this.step();
      float bodyHeight = step.bodyHeight();
      float f = NUMBER_HEIGHT + GUTTER + TITLE_BLOCK + GUTTER + bodyHeight + GUTTER + MenuButton.HEIGHT + GUTTER + STEPS_HEIGHT;
      float snap = MenuTheme.snap(Math.max(MenuTheme.px(12.0F), (guiScaledHeight - f) / 2.0F));
      float snap2 = MenuTheme.snap(guiScaledWidth / 2.0F);
      float width = Math.min(DESCRIPTION_WIDTH, guiScaledWidth - MenuTheme.px(64.0F));
      float y = (1.0F - this.openTransition.getValue()) * MenuTheme.px(40.0F);
      graphics.pose().pushMatrix();
      graphics.pose().translate(0.0F, y);
      this.drawNumber(graphics, snap2, snap);
      float y2 = (1.0F - this.stepTransition.getValue()) * MenuTheme.px(24.0F);
      graphics.pose().pushMatrix();
      graphics.pose().translate(0.0F, y2);
      float f2 = snap + NUMBER_HEIGHT + GUTTER;
      this.drawTitle(graphics, step, snap2, f2, width);
      float y3 = f2 + TITLE_BLOCK + GUTTER;
      BoundsData boundsData = new BoundsData(snap2 - width / 2.0F, y3, width, bodyHeight);
      step.layout(boundsData);
      step.render(graphics, mouseX, mouseY, boundsData);
      this.primaryButton.setBounds(snap2 - BUTTON_WIDTH / 2.0F, y3 + bodyHeight + GUTTER + y + y2, BUTTON_WIDTH, MenuButton.HEIGHT);
      this.primaryButton.setEnabled(step.canAdvance());
      graphics.pose().pushMatrix();
      graphics.pose().translate(0.0F, -(y + y2));
      this.primaryButton.render(graphics, mouseX, mouseY);
      graphics.pose().popMatrix();
      graphics.pose().popMatrix();
      this.drawSteps(graphics, snap2, y3 + bodyHeight + GUTTER + MenuButton.HEIGHT + GUTTER);
      graphics.pose().popMatrix();
   }

   private void drawNumber(MenuDrawContext graphics, float f, float snap) {
      Font semibold = MenuTheme.semibold(32.0F);
      String text = String.valueOf(this.index + 1);
      graphics.drawText(semibold, text, f - semibold.width(text) / 2.0F, snap + (NUMBER_HEIGHT - semibold.height()) / 2.0F, MenuTheme.text().method7294(0.16F));
   }

   private void drawTitle(MenuDrawContext graphics, OnboardingScreen.Step step, float f, float f2, float width) {
      Font semibold = MenuTheme.semibold(16.0F);
      step.drawTitle(graphics, semibold, f, f2);
      Font regular = MenuTheme.regular(12.0F);
      float centeredTextY = f2 + MenuTheme.px(28.0F);

      for (String text : MenuTheme.wrap(regular, step.description(), width)) {
         graphics.drawText(regular, text, f - regular.width(text) / 2.0F, centeredTextY, MenuTheme.textSecondary());
         centeredTextY += MenuTheme.px(16.0F);
      }
   }

   private void drawSteps(MenuDrawContext graphics, float f, float f2) {
      float f3 = this.steps.size() * STEP_BAR_WIDTH + (this.steps.size() - 1) * STEP_BAR_GAP;
      float floatValue = f - f3 / 2.0F;
      float y = f2 + (STEPS_HEIGHT - STEP_BAR_HEIGHT) / 2.0F;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(STEP_BAR_HEIGHT / 2.0F);

      for (int i = 0; i < this.steps.size(); i++) {
         graphics.drawRoundedRect(floatValue, y, STEP_BAR_WIDTH, STEP_BAR_HEIGHT, headerRadius, i <= this.index ? MenuTheme.accent() : MenuTheme.track());
         floatValue += STEP_BAR_WIDTH + STEP_BAR_GAP;
      }
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
   }

   public boolean mouseClicked(double x, double y, int mouseToGlfw) {
      if (this.primaryButton.mouseClicked(x, y, mouseToGlfw)) {
         return true;
      } else {
         return this.step().mouseClicked(x, y, mouseToGlfw) ? true : super.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(org.zenith.utility.misc.KeyCompat.mouseToSdl(mouseToGlfw), org.zenith.utility.misc.KeyCompat.modsToSdl(0))), false);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.step().mouseScrolled(verticalAmount) ? true : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public boolean keyPressed(KeyEvent input) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), input.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (this.step().keyPressed(keyCode)) {
         return true;
      } else if (keyCode == 257 && this.step().canAdvance()) {
         this.advance();
         return true;
      } else {
         return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
      }
   }

   @Override
   public void removed() {
      for (OnboardingScreen.Step step : this.steps) {
         step.close();
      }

      super.removed();
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void extractTransparentBackground(GuiGraphicsExtractor context) {
   }

   private static final class Appear {
      private static final float DURATION = 0.3F;
      private static final float STAGGER = 0.05F;
      private long since = Util.getMillis();

      private void reset() {
         this.since = Util.getMillis();
      }

      private float of(int n) {
         float f = (float)(Util.getMillis() - this.since) / 1000.0F - n * 0.05F;
         float f2 = Math.min(1.0F, Math.max(0.0F, f / 0.3F));
         return f2 * f2 * (3.0F - 2.0F * f2);
      }

      private float lift(int n) {
         return (1.0F - this.of(n)) * MenuTheme.px(16.0F);
      }
   }

   private static final class HotkeyStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(92.0F);
      private static final float ITEM_GAP = MenuTheme.px(24.0F);
      private static final float KEY_BOX = MenuTheme.px(72.0F);
      private static final float KEY_RADIUS = MenuTheme.px(16.0F);
      private static final List<String> WANTED = List.of("Aura", "Speed", "ESP", "TriggerBot", "Menu");
      private final List<Module> modules = new ArrayList<>();
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();
      private BoundsData[] bounds = new BoundsData[0];
      private int binding = -1;

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void prepare() {
         this.modules.clear();

         for (String string : WANTED) {
            Module moduleValue = Zenith.getInstance().getModuleManager().method2484(string);
            if (moduleValue != null) {
               this.modules.add(moduleValue);
            }
         }

         if (this.modules.isEmpty()) {
            var items = Zenith.getInstance().getModuleManager().getItems();
            this.modules.addAll(items.subList(0, Math.min(5, items.size())));
         }
      }

      @Override
      public void drawTitle(MenuDrawContext menuDrawContext, Font font, float f, float centeredTextY) {
         String text = "Set your primary hotkeys.";
         menuDrawContext.drawText(font, text, f - font.width(text) / 2.0F, centeredTextY, MenuTheme.text());
      }

      @Override
      public String description() {
         return "Pick your preferred keys for the most used modules. Click a tile, then press a key.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(118.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.LAUNCH;
      }

      @Override
      public String buttonLabel() {
         return "Launch Zenith";
      }

      @Override
      public void render(MenuDrawContext menuDrawContext, float f, float f2, BoundsData boundsData) {
         this.bounds = new BoundsData[this.modules.size()];
         float f3 = this.modules.size() * ITEM_WIDTH + Math.max(0, this.modules.size() - 1) * ITEM_GAP;
         float floatValue = boundsData.getFloatValue() + (boundsData.width() - f3) / 2.0F;

         for (int i = 0; i < this.modules.size(); i++) {
            this.bounds[i] = new BoundsData(floatValue, boundsData.getFloatValue2(), ITEM_WIDTH, boundsData.height());
            this.drawModule(menuDrawContext, this.modules.get(i), i, floatValue, boundsData.getFloatValue2() + this.appear.lift(i), this.appear.of(i));
            floatValue += ITEM_WIDTH + ITEM_GAP;
         }
      }

      private void drawModule(MenuDrawContext menuDrawContext, Module moduleValue, int n, float floatValue, float y, float previewAlpha) {
         boolean flag = moduleValue.getKeyCode() > 0;
         boolean flag2 = this.binding == n;
         float snap = MenuTheme.snap(floatValue + (ITEM_WIDTH - KEY_BOX) / 2.0F);
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(KEY_RADIUS);
         if (flag && !flag2) {
            menuDrawContext.drawRoundedRect(snap, y, KEY_BOX, KEY_BOX, headerRadius, MenuTheme.accent().method7294(previewAlpha));
         } else {
            menuDrawContext.drawRoundedRect(snap, y, KEY_BOX, KEY_BOX, headerRadius, MenuTheme.surface().method7294(previewAlpha));
            menuDrawContext.drawRoundedBorder(snap, y, KEY_BOX, KEY_BOX, -0.1F, headerRadius, (flag2 ? MenuTheme.accent() : MenuTheme.border()).method7294(previewAlpha));
         }

         Font semibold = MenuTheme.semibold(16.0F);
         String text = flag2 ? "..." : keyName(moduleValue.getKeyCode());
         menuDrawContext.drawText(
            semibold,
            text,
            snap + (KEY_BOX - semibold.width(text)) / 2.0F,
            y + (KEY_BOX - semibold.height()) / 2.0F,
            (flag && !flag2 ? MenuTheme.onAccent() : MenuTheme.textSecondary()).method7294(previewAlpha)
         );
         ModuleCategory category = moduleValue.getCategory();
         MenuIcon menuIcon = MenuIcons.forCategory(category);
         Font medium = MenuTheme.medium(12.0F);
         Font font = menuIcon.font(12.0F);
         float f = menuIcon.width(12.0F) + MenuTheme.px(4.0F) + medium.width(moduleValue.getName());
         float floatValue2 = floatValue + (ITEM_WIDTH - f) / 2.0F;
         float centeredTextY = y + KEY_BOX + MenuTheme.px(16.0F);
         menuDrawContext.drawText(font, menuIcon.glyph(), floatValue2, centeredTextY, MenuTheme.accent().method7294(previewAlpha));
         menuDrawContext.drawText(medium, moduleValue.getName(), floatValue2 + menuIcon.width(12.0F) + MenuTheme.px(4.0F), centeredTextY, MenuTheme.text().method7294(previewAlpha));
         Font regular = MenuTheme.regular(10.0F);
         String name = category.getName();
         menuDrawContext.drawText(regular, name, floatValue + (ITEM_WIDTH - regular.width(name)) / 2.0F, centeredTextY + MenuTheme.px(18.0F), MenuTheme.textSecondary().method7294(previewAlpha));
      }

      private static String keyName(int keyCode) {
         if (keyCode <= 0) {
            return "-";
         } else {
            String keyName = org.zenith.utility.misc.KeyCompat.keyName(keyCode);
            return keyName != null && !keyName.isBlank() ? keyName.toUpperCase() : Type.KEYBOARD.getOrCreate(org.zenith.utility.misc.KeyCompat.toSdl(keyCode)).getDisplayName().getString().toUpperCase();
         }
      }

      @Override
      public boolean mouseClicked(double lastClickX, double lastClickY, int n) {
         if (n != 0) {
            return false;
         } else {
            for (int i = 0; i < this.bounds.length; i++) {
               if (this.bounds[i] != null && this.bounds[i].contains(lastClickX, lastClickY)) {
                  this.binding = this.binding == i ? -1 : i;
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean keyPressed(int n) {
         if (this.binding >= 0 && this.binding < this.modules.size()) {
            this.modules.get(this.binding).setKeyCode(n != 256 && n != 261 ? n : -1);
            this.binding = -1;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void commit() {
         this.binding = -1;
         if (Zenith.getInstance().getManager() != null) {
            Zenith.getInstance().getManager().save();
         }
      }
   }

   private static final class ServerStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(92.0F);
      private static final float ITEM_GAP = MenuTheme.px(16.0F);
      private static final float CIRCLE = MenuTheme.px(72.0F);
      private static final float ICON = MenuTheme.px(64.0F);
      private static final float BADGE = MenuTheme.px(16.0F);
      private final MenuScroll scroll = new MenuScroll();
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();
      private final List<OnboardingScreen.ServerStep.Entry> entries = new ArrayList<>();
      private BoundsData[] bounds = new BoundsData[0];
      private int selected = -1;

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void prepare() {
         this.close();
         ServerList serverList = new ServerList(mcStatic());
         serverList.load();

         for (int i = 0; i < serverList.size(); i++) {
            this.entries.add(new OnboardingScreen.ServerStep.Entry(serverList.get(i)));
         }

         this.entries.add(new OnboardingScreen.ServerStep.Entry(null));
         this.selected = this.entries.size() - 1;
      }

      private static Minecraft mcStatic() {
         return Minecraft.getInstance();
      }

      private ServerData picked() {
         return this.selected >= 0 && this.selected < this.entries.size() ? this.entries.get(this.selected).info : null;
      }

      @Override
      public void drawTitle(MenuDrawContext menuDrawContext, Font font, float f, float centeredTextY) {
         String text = "Where are you playing today?";
         menuDrawContext.drawText(font, text, f - font.width(text) / 2.0F, centeredTextY, MenuTheme.text());
      }

      @Override
      public String description() {
         return "Selecting a server opens its section in the menu with the modules made for it.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(110.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.CONFIGURE;
      }

      @Override
      public String buttonLabel() {
         return "Configure";
      }

      @Override
      public void render(MenuDrawContext menuDrawContext, float f, float f2, BoundsData boundsData) {
         this.bounds = new BoundsData[this.entries.size()];
         float content = this.entries.size() * ITEM_WIDTH + (this.entries.size() - 1) * ITEM_GAP;
         this.scroll.update(boundsData.width(), content);
         menuDrawContext.enableScissor(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.getFloatValue() + boundsData.width(), boundsData.getFloatValue2() + boundsData.height());
         float floatValue = boundsData.getFloatValue() + Math.max(0.0F, (boundsData.width() - content) / 2.0F) - this.scroll.offset();

         for (int i = 0; i < this.entries.size(); i++) {
            this.bounds[i] = new BoundsData(floatValue, boundsData.getFloatValue2(), ITEM_WIDTH, boundsData.height());
            this.entries.get(i).render(menuDrawContext, floatValue, boundsData.getFloatValue2() + this.appear.lift(i), i == this.selected, this.appear.of(i));
            floatValue += ITEM_WIDTH + ITEM_GAP;
         }

         menuDrawContext.disableScissor();
      }

      @Override
      public void close() {
         for (OnboardingScreen.ServerStep.Entry entry : this.entries) {
            entry.close();
         }

         this.entries.clear();
      }

      @Override
      public boolean mouseClicked(double lastClickX, double lastClickY, int n) {
         if (n != 0) {
            return false;
         } else {
            for (int i = 0; i < this.bounds.length; i++) {
               if (this.bounds[i] != null && this.bounds[i].contains(lastClickX, lastClickY)) {
                  this.selected = i;
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean mouseScrolled(double d) {
         return this.scroll.scroll(d, ITEM_WIDTH + ITEM_GAP);
      }

      private static final class Entry {
         private final ServerData info;
         private final FaviconTexture icon;
         private byte[] lastFavicon;

         private Entry(ServerData info) {
            this.info = info;
            this.icon = info == null ? null : FaviconTexture.forServer(OnboardingScreen.ServerStep.mcStatic().getTextureManager(), info.ip);
         }

         private void render(MenuDrawContext menuDrawContext, float floatValue, float y, boolean flag, float previewAlpha) {
            float snap = MenuTheme.snap(floatValue + (OnboardingScreen.ServerStep.ITEM_WIDTH - OnboardingScreen.ServerStep.CIRCLE) / 2.0F);
            if (flag) {
               menuDrawContext.drawRoundedBorder(
                  snap,
                  y,
                  OnboardingScreen.ServerStep.CIRCLE,
                  OnboardingScreen.ServerStep.CIRCLE,
                  -0.1F,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.CIRCLE / 2.0F),
                  ZenithColor.INSTANCE.method7294(previewAlpha)
               );
            }

            float f = snap + (OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.ICON) / 2.0F;
            float y2 = y + (OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.ICON) / 2.0F;
            if (this.info == null) {
               menuDrawContext.drawRoundedRect(
                  f,
                  y2,
                  OnboardingScreen.ServerStep.ICON,
                  OnboardingScreen.ServerStep.ICON,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.ICON / 2.0F),
                  MenuTheme.accent().method7294(previewAlpha)
               );
               Font font = MenuIcons.SPARKLE.font(28.0F);
               menuDrawContext.drawText(
                  font,
                  MenuIcons.SPARKLE.glyph(),
                  f + (OnboardingScreen.ServerStep.ICON - MenuIcons.SPARKLE.width(28.0F)) / 2.0F,
                  y2 + (OnboardingScreen.ServerStep.ICON - font.height()) / 2.0F,
                  MenuTheme.onAccent().method7294(previewAlpha)
               );
            } else {
               this.syncFavicon();
               RenderShaders.method5294(
                  MatrixUtils.method664(menuDrawContext.pose()),
                  this.icon.textureLocation(),
                  f,
                  y2,
                  OnboardingScreen.ServerStep.ICON,
                  OnboardingScreen.ServerStep.ICON,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.ICON / 2.0F),
                  ZenithColor.INSTANCE.method7294(previewAlpha)
               );
            }

            if (flag) {
               float floatValue2 = snap + OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.BADGE;
               float y3 = y + OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.BADGE;
               menuDrawContext.drawRoundedRect(
                  floatValue2,
                  y3,
                  OnboardingScreen.ServerStep.BADGE,
                  OnboardingScreen.ServerStep.BADGE,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.BADGE / 2.0F),
                  MenuTheme.accent().method7294(previewAlpha)
               );
               menuDrawContext.drawRoundedBorder(
                  floatValue2,
                  y3,
                  OnboardingScreen.ServerStep.BADGE,
                  OnboardingScreen.ServerStep.BADGE,
                  -0.1F,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.BADGE / 2.0F),
                  ZenithColor.INSTANCE.method7294(previewAlpha)
               );
               Font font = MenuIcons.ACCEPT.font(10.0F);
               menuDrawContext.drawText(
                  font,
                  MenuIcons.ACCEPT.glyph(),
                  floatValue2 + (OnboardingScreen.ServerStep.BADGE - MenuIcons.ACCEPT.width(10.0F)) / 2.0F,
                  y3 + (OnboardingScreen.ServerStep.BADGE - font.height()) / 2.0F,
                  ZenithColor.INSTANCE.method7294(previewAlpha)
               );
            }

            Font medium = MenuTheme.medium(12.0F);
            String name = this.info == null ? "Universal" : this.info.name;
            float y3 = y + OnboardingScreen.ServerStep.CIRCLE + MenuTheme.px(8.0F);
            menuDrawContext.enableScissor(floatValue, y3, floatValue + OnboardingScreen.ServerStep.ITEM_WIDTH, y3 + medium.height());
            menuDrawContext.drawText(medium, name, floatValue + (OnboardingScreen.ServerStep.ITEM_WIDTH - medium.width(name)) / 2.0F, y3, MenuTheme.text().method7294(previewAlpha));
            menuDrawContext.disableScissor();
            Font regular = MenuTheme.regular(10.0F);
            String string = this.info == null ? "?" : (this.info.status == null ? "-" : this.info.status.getString());
            Font font = MenuIcons.ONLINE.font(10.0F);
            float f2 = MenuIcons.ONLINE.width(10.0F) + MenuTheme.px(4.0F) + regular.width(string);
            float floatValue2 = floatValue + (OnboardingScreen.ServerStep.ITEM_WIDTH - f2) / 2.0F;
            float centeredTextY = y + OnboardingScreen.ServerStep.CIRCLE + MenuTheme.px(26.0F);
            menuDrawContext.drawText(font, MenuIcons.ONLINE.glyph(), floatValue2, centeredTextY, MenuTheme.accent().method7294(previewAlpha));
            menuDrawContext.drawText(regular, string, floatValue2 + MenuIcons.ONLINE.width(10.0F) + MenuTheme.px(4.0F), centeredTextY, MenuTheme.text().method7294(previewAlpha));
         }

         private void syncFavicon() {
            byte[] iconBytes = this.info.getIconBytes();
            if (iconBytes != null && !Arrays.equals(iconBytes, this.lastFavicon)) {
               try {
                  this.icon.upload(NativeImage.read(iconBytes));
                  this.lastFavicon = iconBytes;
               } catch (Exception e) {
               }
            }
         }

         private void close() {
            if (this.icon != null) {
               this.icon.close();
            }
         }
      }
   }

   private interface Step {
      String description();

      float bodyHeight();

      MenuIcon buttonIcon();

      String buttonLabel();

      void render(MenuDrawContext graphics, float mouseX, float mouseY, BoundsData boundsData);

      default void drawTitle(MenuDrawContext graphics, Font semibold, float f, float f2) {
      }

      default void layout(BoundsData boundsData) {
      }

      default void prepare() {
      }

      default void enter() {
      }

      default void commit() {
      }

      default void close() {
      }

      default boolean canAdvance() {
         return true;
      }

      default boolean mouseClicked(double x, double y, int mouseToGlfw) {
         return false;
      }

      default boolean mouseScrolled(double verticalAmount) {
         return false;
      }

      default boolean keyPressed(int keyCode) {
         return false;
      }
   }

   private static final class ThemeStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(92.0F);
      private static final float ITEM_GAP = MenuTheme.px(16.0F);
      private static final float CIRCLE = MenuTheme.px(72.0F);
      private static final float FILL = MenuTheme.px(64.0F);
      private static final float BADGE = MenuTheme.px(16.0F);
      private final MenuScroll scroll = new MenuScroll();
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();
      private BoundsData[] bounds = new BoundsData[0];
      private int selected;

      @Override
      public void prepare() {
         var styles = Zenith.getInstance().getStyleManager().getStyles();
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         this.selected = Math.max(0, styles.indexOf(currentStyle));
      }

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void drawTitle(MenuDrawContext menuDrawContext, Font font, float f, float centeredTextY) {
         String text = "Select your visual theme.";
         menuDrawContext.drawText(font, text, f - font.width(text) / 2.0F, centeredTextY, MenuTheme.text());
      }

      @Override
      public String description() {
         return "Choose a primary aesthetic for your client. This defines the palette, accents and highlights across all modules and menus.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(94.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.NEXT;
      }

      @Override
      public String buttonLabel() {
         return "Next Step";
      }

      @Override
      public void render(MenuDrawContext menuDrawContext, float f, float f2, BoundsData boundsData) {
         var styles = Zenith.getInstance().getStyleManager().getStyles();
         this.bounds = new BoundsData[styles.size()];
         float content = styles.size() * ITEM_WIDTH + (styles.size() - 1) * ITEM_GAP;
         this.scroll.update(boundsData.width(), content);
         menuDrawContext.enableScissor(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.getFloatValue() + boundsData.width(), boundsData.getFloatValue2() + boundsData.height());
         float floatValue = boundsData.getFloatValue() + Math.max(0.0F, (boundsData.width() - content) / 2.0F) - this.scroll.offset();

         for (int i = 0; i < styles.size(); i++) {
            this.bounds[i] = new BoundsData(floatValue, boundsData.getFloatValue2(), ITEM_WIDTH, boundsData.height());
            this.drawTheme(menuDrawContext, (ZenithStyle)styles.get(i), i == this.selected, floatValue, boundsData.getFloatValue2() + this.appear.lift(i), this.appear.of(i));
            floatValue += ITEM_WIDTH + ITEM_GAP;
         }

         menuDrawContext.disableScissor();
      }

      private void drawTheme(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, boolean flag, float floatValue, float y, float previewAlpha) {
         float snap = MenuTheme.snap(floatValue + (ITEM_WIDTH - CIRCLE) / 2.0F);
         if (flag) {
            menuDrawContext.drawRoundedBorder(snap, y, CIRCLE, CIRCLE, -0.1F, ProxyDotRadiusData.method8069(CIRCLE / 2.0F), ZenithColor.INSTANCE.method7294(previewAlpha));
         }

         float f = snap + (CIRCLE - FILL) / 2.0F;
         float y2 = y + (CIRCLE - FILL) / 2.0F;
         ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = zenithStyle.getSecondaryPrimaryColor().getZenithColor().method7294(previewAlpha);
         menuDrawContext.drawRoundedRect(
            f,
            y2,
            FILL,
            FILL,
            ProxyDotRadiusData.method8069(FILL / 2.0F),
            GlowColor.method8096(zenithColor, zenithColor.method7295(zenithColor3, 0.5F), zenithColor3, zenithColor3.method7295(zenithColor, 0.5F))
         );
         if (flag) {
            float floatValue2 = snap + CIRCLE - BADGE;
            float y3 = y + CIRCLE - BADGE;
            menuDrawContext.drawRoundedRect(floatValue2, y3, BADGE, BADGE, ProxyDotRadiusData.method8069(BADGE / 2.0F), zenithColor);
            menuDrawContext.drawRoundedBorder(floatValue2, y3, BADGE, BADGE, -0.1F, ProxyDotRadiusData.method8069(BADGE / 2.0F), ZenithColor.INSTANCE.method7294(previewAlpha));
            Font font = MenuIcons.ACCEPT.font(10.0F);
            menuDrawContext.drawText(
               font,
               MenuIcons.ACCEPT.glyph(),
               floatValue2 + (BADGE - MenuIcons.ACCEPT.width(10.0F)) / 2.0F,
               y3 + (BADGE - font.height()) / 2.0F,
               ZenithColor.INSTANCE.method7294(previewAlpha)
            );
         }

         Font medium = MenuTheme.medium(12.0F);
         String name = zenithStyle.getName();
         menuDrawContext.drawText(medium, name, floatValue + (ITEM_WIDTH - medium.width(name)) / 2.0F, y + CIRCLE + MenuTheme.px(8.0F), MenuTheme.text().method7294(previewAlpha));
      }

      @Override
      public void commit() {
         var styles = Zenith.getInstance().getStyleManager().getStyles();
         if (this.selected >= 0 && this.selected < styles.size()) {
            Zenith.getInstance().getStyleManager().setCurrentStyle((ZenithStyle)styles.get(this.selected));
         }
      }

      @Override
      public boolean mouseClicked(double lastClickX, double lastClickY, int n) {
         if (n != 0) {
            return false;
         } else {
            for (int i = 0; i < this.bounds.length; i++) {
               if (this.bounds[i] != null && this.bounds[i].contains(lastClickX, lastClickY)) {
                  this.selected = i;
                  this.commit();
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean mouseScrolled(double d) {
         return this.scroll.scroll(d, ITEM_WIDTH + ITEM_GAP);
      }
   }

   private static final class WelcomeStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(48.0F);
      private static final float ITEM_GAP = MenuTheme.px(96.0F);
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void drawTitle(MenuDrawContext menuDrawContext, Font font, float f, float centeredTextY) {
         String text = "Welcome back, ";
         String str = username();
         float f2 = font.width(text) + font.width(str);
         float floatValue = f - f2 / 2.0F;
         menuDrawContext.drawText(font, text, floatValue, centeredTextY, MenuTheme.text());
         menuDrawContext.drawText(font, str, floatValue + font.width(text), centeredTextY, MenuTheme.accent());
      }

      private static String username() {
         String username = Zenith.getInstance().getEntry().getUsername();
         return username != null && !username.isEmpty() ? username + "." : "Guest";
      }

      @Override
      public String description() {
         return "Everything is ready to go. Let's take a second to adjust the client to your liking.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(98.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.ACCEPT;
      }

      @Override
      public String buttonLabel() {
         return "Start Setup";
      }

      @Override
      public void render(MenuDrawContext menuDrawContext, float f, float f2, BoundsData boundsData) {
         Zenith.Entry entry = Zenith.getInstance().getEntry();
         var stringses = List.of(
            new String[]{entry.getText2() == null ? "-" : entry.getText2(), "UID"},
            new String[]{entry.method1504().getName(), "Role"},
            new String[]{"3.5", "Version"}
         );
         var menuIcons = List.of(MenuIcons.UID, MenuIcons.ROLE, MenuIcons.VERSION);
         float f3 = stringses.size() * ITEM_WIDTH + (stringses.size() - 1) * ITEM_GAP;
         float f4 = boundsData.getFloatValue() + (boundsData.width() - f3) / 2.0F;

         for (int i = 0; i < stringses.size(); i++) {
            this.drawItem(
               menuDrawContext,
               (MenuIcon)menuIcons.get(i),
               ((String[])stringses.get(i))[0],
               ((String[])stringses.get(i))[1],
               f4,
               boundsData.getFloatValue2() + this.appear.lift(i),
               this.appear.of(i)
            );
            f4 += ITEM_WIDTH + ITEM_GAP;
         }
      }

      private void drawItem(MenuDrawContext menuDrawContext, MenuIcon menuIcon, String text, String str, float f, float f2, float previewAlpha) {
         Font font = menuIcon.font(48.0F);
         Font medium = MenuTheme.medium(14.0F);
         Font regular = MenuTheme.regular(11.0F);
         menuDrawContext.drawText(
            font,
            menuIcon.glyph(),
            f + (ITEM_WIDTH - menuIcon.width(48.0F)) / 2.0F,
            f2 + (MenuTheme.px(48.0F) - font.height()) / 2.0F,
            MenuTheme.accent().method7294(previewAlpha)
         );
         float centeredTextY = f2 + MenuTheme.px(64.0F);
         menuDrawContext.drawText(medium, text, f + (ITEM_WIDTH - medium.width(text)) / 2.0F, centeredTextY, MenuTheme.text().method7294(previewAlpha));
         menuDrawContext.drawText(regular, str, f + (ITEM_WIDTH - regular.width(str)) / 2.0F, centeredTextY + MenuTheme.px(20.0F), MenuTheme.textTertiary().method7294(previewAlpha));
      }
   }
}
