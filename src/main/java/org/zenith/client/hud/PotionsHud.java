package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class PotionsHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "o";
   private static final String text2 = "Potions";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, PotionsHud.Entry> items = new LinkedHashMap<>();
   private final Set<String> items2 = new HashSet<>();

   public PotionsHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      if (mc.player != null) {
         this.items2.clear();

         for (MobEffectInstance statusEffectInstance : new ArrayList<>(mc.player.getActiveEffectsMap().values())) {
            String str = this.method5489(statusEffectInstance);
            this.items2.add(str);
            if (!this.items.containsKey(str)) {
               this.items.put(str, new PotionsHud.Entry(statusEffectInstance, this.method5487(statusEffectInstance)));
            }
         }

         this.items.values().removeIf(PotionsHud.Entry::method5496);
         if (this.items.isEmpty()) {
            this.animation2.method2808(0.0F);
         } else {
            PotionsHud.Entry entry = this.items.values().iterator().next();
            this.animation2.method2808(this.items.size() == 1 && entry.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
         }

         Font font = Fonts.NEW_ICONS.getFont(5.5F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         float floatValue = this.floatValue;
         float y1 = this.floatValue2;
         float f = (float)this.items
            .values()
            .stream()
            .mapToDouble(entry -> (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue())
            .sum();
         float height = 20.0F + GuiStyle.PADDING.intValue() / 2.0F + f;
         float width = (float)this.items.values().stream().mapToDouble(PotionsHud.Entry::method5494).max().orElse(0.0);
         width = this.animation.method2808(Math.max(HudListLayout.method7638("o", "Potions"), width));
         this.width = width;
         this.height = height;
         this.animation3.method2807(mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
         float intValue = InterfaceModule.method6801();
         ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(intValue);
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + height / 2.0F);
         customDrawContext.pose().scale(this.animation3.getValue(), this.animation3.getValue());
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + height / 2.0F));
         HudListLayout.method7635(customDrawContext, floatValue, y1, width, height);
         HudListLayout.method7636(customDrawContext, floatValue, y1, width, "o", "Potions");
         if (this.animation3.getValue() == 1.0F) {
            float y12 = y1 + 20.0F;
            customDrawContext.enableScissor((int)floatValue, (int)y1, (int)(floatValue + width), (int)(y1 + height));

            for (PotionsHud.Entry entry : this.items.values()) {
               entry.method5495(customDrawContext, floatValue, y12, width);
               y12 += (entry.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * entry.animation.getValue();
            }

            customDrawContext.disableScissor();
         }

         customDrawContext.popMatrix();
      }
   }

   private boolean method5487(MobEffectInstance statusEffectInstance) {
      return statusEffectInstance.getEffect().value().getCategory().equals(MobEffectCategory.BENEFICIAL);
   }

   private String method5488(MobEffectInstance statusEffectInstance) {
      String str = I18n.get(statusEffectInstance.getEffect().value().getDescriptionId());
      String str2 = this.method5490(statusEffectInstance.getAmplifier());
      return str + " " + str2;
   }

   private String method5489(MobEffectInstance statusEffectInstance) {
      return statusEffectInstance.getEffect().value().getDescriptionId() + statusEffectInstance.getAmplifier();
   }

   private String method5490(int amplifier) {
      return String.valueOf(amplifier + 1);
   }

   private String method5491(int duration) {
      int n = duration / 20;
      int n2 = n / 60;
      int n3 = n % 60;
      return String.format("%02d:%02d", n2, n3);
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private class Entry {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private final Animation animation2 = new Animation(200L, 1.0F, Easing.INSTANCE10);
      private final boolean flag;
      private MobEffectInstance statusEffectInstance;
      private int intValue;
      private final String text;
      private Object obj;
      private String text2;
      private int intValue2;
      private String text3;

      public Entry(MobEffectInstance statusEffectInstance, boolean flag) {
         this.statusEffectInstance = statusEffectInstance;
         this.flag = flag;
         this.intValue = Math.max(1, statusEffectInstance.getDuration());
         this.text = PotionsHud.this.method5489(statusEffectInstance);
      }

      private String method5492() {
         Language language = Language.getInstance();
         if (this.text2 == null || language != this.obj) {
            this.obj = language;
            this.text2 = PotionsHud.this.method5488(this.statusEffectInstance);
         }

         return this.text2;
      }

      private String method5493() {
         int n = this.statusEffectInstance.getDuration() / 20;
         if (this.text3 == null || n != this.intValue2) {
            this.intValue2 = n;
            this.text3 = PotionsHud.this.method5491(this.statusEffectInstance.getDuration());
         }

         return this.text3;
      }

      public float method5494() {
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font font2 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String text = this.method5492();
         String str = this.method5493();
         return HudListLayout.method7651(text, str);
      }

      public float getHeight() {
         return 14.0F;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public void method5495(CustomDrawContext customDrawContext, float floatValue, float y1, float width) {
         MobEffectInstance effect = MinecraftAccess.mc.player.getEffect(this.statusEffectInstance.getEffect());
         if (effect != null && effect.getAmplifier() == this.statusEffectInstance.getAmplifier()) {
            this.statusEffectInstance = effect;
         }

         this.animation.method2808(PotionsHud.this.items2.contains(this.text) ? 1.0F : 0.0F);
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font font2 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String text = this.method5492();
         String str = this.method5493();
         int n = Math.max(0, this.statusEffectInstance.getDuration());
         if (PotionsHud.this.items2.contains(this.text)) {
            this.intValue = Math.max(this.intValue, Math.max(1, n));
         }

         float f = this.intValue <= 0 ? 0.0F : Mth.clamp((float)n / this.intValue, 0.0F, 1.0F);
         float f2 = Mth.clamp(this.animation2.method2808(f), 0.0F, 1.0F);
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(floatValue + width / 2.0F, y1 + this.getHeight() / 2.0F);
         float x = this.animation.getValue();
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y1 + this.getHeight() / 2.0F));
         float floatValue2 = HudListLayout.method7640(customDrawContext, floatValue, y1);
         float width2 = 8.0F;
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(HudListLayout.method7641(floatValue) + (10.0F - width2) / 2.0F, HudListLayout.method7642(y1) + (10.0F - width2) / 2.0F);
         customDrawContext.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.getMobEffectSprite(this.statusEffectInstance.getEffect()), 0, 0, (int)width2, (int)width2);
         customDrawContext.popMatrix();
         HudListLayout.method7644(customDrawContext, floatValue2, y1, text);
         HudListLayout.method7649(customDrawContext, floatValue, y1, width, str, f2);
         customDrawContext.popMatrix();
      }

      public boolean method5496() {
         return this.animation.getValue() == 0.0F;
      }
   }
}
