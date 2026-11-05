package org.zenith.client.screens.override.particle;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.WindowResizeEvent;
import org.zenith.utility.misc.Handler1;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class MenuParticleRenderer implements MinecraftAccess {
   private static final Identifier GLOW = Zenith.method1501("textures/glow.png");
   private final List<MenuParticleRenderer.MenuParticle> particles = new ArrayList<>();

   public MenuParticleRenderer() {
      for (int i = 0; i < 100; i++) {
         this.particles.add(new MenuParticleRenderer.MenuParticle(1920.0F, 1080.0F, true));
      }

      EventManager.register(this);
   }

   @EventTarget
   public void resize(WindowResizeEvent event) {
      this.init();
   }

   public void init() {
      this.particles.clear();

      for (int i = 0; i < 100; i++) {
         this.particles.add(new MenuParticleRenderer.MenuParticle(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), true));
      }
   }

   public static void renderBackgroundAndParticles(MenuDrawContext graphics) {
      Window window = mc.getWindow();
      float guiScaledWidth = window.getGuiScaledWidth();
      float guiScaledHeight = window.getGuiScaledHeight();
      ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = Zenith.getInstance().getStyleManager().getCurrentStyle().getSecondaryPrimaryColor().getZenithColor();
      RenderShaders.method5261(MatrixUtils.method664(graphics.pose()), guiScaledWidth, guiScaledHeight, zenithColor, zenithColor2, 1.0F);
      Zenith.getInstance().getMenuParticleRenderer().renderParticles(graphics, getScreenFadeProgress());
   }

   public static float getScreenFadeProgress() {
      Screen screen = mc.gui.screen();
      if (screen == null) {
         return 1.0F;
      } else {
         Handler1 handler1 = (Handler1)screen;
         long zenithDLC$callGetStartTime = handler1.zenithDLC$callGetStartTime();
         long now = System.currentTimeMillis();
         float f = (float)(now - zenithDLC$callGetStartTime) / 200.0F;
         return Math.max(0.0F, Math.min(1.0F, f));
      }
   }

   public void renderParticles(MenuDrawContext graphics) {
      this.renderParticles(graphics, 1.0F);
   }

   public void renderParticles(MenuDrawContext graphics, float screenFadeProgress) {
      ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7293(255);
      Window window = mc.getWindow();
      float guiScaledWidth = window.getGuiScaledWidth();
      float guiScaledHeight = window.getGuiScaledHeight();
      ZenithColor zenithColor2 = ZenithColor.INSTANCE.method7295(zenithColor, 0.55F);

      for (MenuParticleRenderer.MenuParticle particle : this.particles) {
         particle.update(guiScaledWidth, guiScaledHeight, graphics.getDelta());
         int n = (int)(particle.alpha * 180.0F);
         ZenithColor zenithColor3 = zenithColor2.method7293((int)(n * screenFadeProgress));
         graphics.drawTexture(GLOW, particle.x, particle.y, particle.size, particle.size, zenithColor3);
      }
   }

   private class MenuParticle {
      public float x;
      public float y;
      public float size;
      public float speed;
      public float time;
      public float maxTime;
      public float alpha;

      public MenuParticle(float guiScaledWidth, float guiScaledHeight, boolean flag) {
         this.reset(guiScaledWidth, guiScaledHeight, flag);
      }

      public void reset(float guiScaledWidth, float guiScaledHeight, boolean flag) {
         this.size = 2 + (int)(Math.random() * 4.0);
         this.speed = 2.0F + (float)(Math.random() * 0.8F);
         this.maxTime = 100.0F + (float)(Math.random() * 200.0);
         this.time = flag ? (float)(Math.random() * this.maxTime) : 0.0F;
         if (flag) {
            this.x = (float)(Math.random() * guiScaledWidth);
            this.y = (float)(Math.random() * guiScaledHeight);
         } else if (Math.random() > 0.5) {
            this.x = -20.0F;
            this.y = (float)(Math.random() * guiScaledHeight);
         } else {
            this.x = (float)(Math.random() * guiScaledWidth);
            this.y = -20.0F;
         }
      }

      public void update(float guiScaledWidth, float guiScaledHeight, float delta) {
         this.x = this.x + this.speed * delta;
         this.y = this.y + this.speed * delta;
         this.time += delta;
         float f = this.maxTime * 0.2F;
         if (this.time < f) {
            this.alpha = this.time / f;
         } else if (this.time > this.maxTime - f) {
            this.alpha = (this.maxTime - this.time) / f;
         } else {
            this.alpha = 1.0F;
         }

         this.alpha = Math.max(0.0F, Math.min(1.0F, this.alpha));
         if (this.time >= this.maxTime || this.x > guiScaledWidth + 50.0F || this.y > guiScaledHeight + 50.0F) {
            this.reset(guiScaledWidth, guiScaledHeight, false);
         }
      }
   }
}
