package org.zenith.client.hud;

import java.util.List;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.Util14;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class TargetHud extends Interface {
   private final Animation animation = new Animation(200L, Easing.INSTANCE10);
   private final Animation animation2 = new Animation(200L, Easing.INSTANCE10);
   private final Animation animation3 = new Animation(200L, Easing.INSTANCE13);
   private final Animation animation4 = new Animation(150L, Easing.INSTANCE25);
   private LivingEntity livingEntity;
   private BoundsData bounds;
   private String text = "";
   private static final float CONST_floatValue = 7.0F;
   private static final float CONST_floatValue2 = 1.0F;
   private static final float CONST_floatValue3 = 7.0F;
   private static final float CONST_floatValue4 = 62.0F;
   private static final float CONST_floatValue5 = 10.0F;

   public TargetHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public boolean method2056(MouseButtonEvent event) {
      UVPair uvPair = this.method2099();
      if (this.bounds != null && this.bounds.contains(uvPair.u(), uvPair.v()) && mc.player != null) {
         mc.keyboardHandler.setClipboard(mc.player.getGameProfile().name());
         return true;
      } else {
         return super.method2056(event);
      }
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      AuraModule auraModule = AuraModule.INSTANCE;
      Object player = !(mc.gui.screen() instanceof ChatScreen) && !Zenith.getInstance().getNLMenuScreen().isRenderHud()
         ? (auraModule.method6358() == null ? AimAssistModule.INSTANCE.method5156() : auraModule.method6358())
         : mc.player;
      this.method7235((LivingEntity)player);
      if (this.animation3.getValue() != 0.0F && this.livingEntity != null) {
         this.method7233((MenuDrawContext)customDrawContext, this.livingEntity, this.animation3.getValue());
      }
   }

   private void method7233(MenuDrawContext customDrawContext, LivingEntity livingEntity, float x) {
      ZenithStyle style = HudListLayout.style();
      boolean flag = livingEntity instanceof Player;
      float f = Mth.clamp(UseItemUtils.method6527(livingEntity), 0.0F, 999.0F);
      float f2 = Math.max(1.0F, livingEntity.getMaxHealth());
      float floatValue = Mth.clamp(f / f2, 0.0F, 1.0F);
      float f3 = Mth.clamp(this.animation.method2808(floatValue), 0.0F, 1.0F);
      float f4 = Math.max(0.0F, f - f2);
      float f5 = Mth.clamp(this.animation2.method2808(f4 / f2), 0.0F, 1.0F);
      String text = String.valueOf(Math.round(f));
      Font nameFont = HudListLayout.nameFont();
      Font font = Fonts.NEW_SEMIBOLD.getFont(7.5F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.0F);
      String str = NameProtectModule.method5149(livingEntity.getName().getString());
      float f6 = 49.0F;
      float width = Math.max(f6, Math.min(70.0F, nameFont.width(str)));
      float f7 = Math.max(18.0F, font.width("999") + 4.0F);
      float width2 = 27.0F + width + 4.0F + f7 + 4.0F;
      float height = 26.0F;
      this.width = width2;
      this.height = height;
      float floatValue2 = this.floatValue;
      float y1 = this.floatValue2;
      float floatValue3 = floatValue2 + 27.0F;
      float f8 = flag ? y1 + 4.0F : y1 + (height - nameFont.height()) / 2.0F;
      this.bounds = new BoundsData(floatValue3, f8 - 1.0F, width, nameFont.height() + 2.0F);
      UVPair uvPair = this.method2099();
      boolean flag2 = InterfaceModule.INSTANCE.method6795(this);
      boolean flag3 = !flag2
         && (mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud())
         && this.bounds.contains(uvPair.u(), uvPair.v());
      if (flag2 || !str.equals(this.text)) {
         this.animation4.method2809(0.0F);
         this.text = str;
      }

      float previewAlpha = this.animation4.method2808(flag3 ? 1.0F : 0.0F);
      String clip = nameFont.clip(str, width);
      String clip2 = nameFont.clip(method7236("hud.target.copy"), width);
      ZenithColor zenithColor = style.getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = style.getTextEnable().getZenithColor();
      ZenithColor zenithColor3 = style.getTextTertiary().getZenithColor();
      float hover = Mth.clamp((0.35F - f3) / 0.35F, 0.0F, 1.0F);
      ZenithColor zenithColor4 = zenithColor.method7295(new ZenithColor(255, 104, 119), hover);
      ZenithColor zenithColor5 = new ZenithColor(245, 201, 112);
      customDrawContext.pushMatrix();

      try {
         customDrawContext.pose().translate(floatValue2 + width2 / 2.0F, y1 + height / 2.0F);
         customDrawContext.pose().scale(x, x);
         customDrawContext.pose().translate(-(floatValue2 + width2 / 2.0F), -(y1 + height / 2.0F));
         HudListLayout.method7635(customDrawContext, floatValue2, y1, width2, height);
         float floatValue4 = floatValue2 + 4.0F;
         float y = y1 + 4.0F;
         float width3 = 18.0F;
         float width4 = 13.0F;
         float f9 = (width3 - width4) / 2.0F;
         float f10 = Mth.clamp(livingEntity.hurtTime / 10.0F, 0.0F, 1.0F);
         ZenithColor zenithColor6 = zenithColor4.method7295(new ZenithColor(255, 104, 119), f10 * 0.6F);
         customDrawContext.drawRoundedRect(floatValue4 + 1.5F, y + 1.5F, width3 - 3.0F, width3 - 3.0F, ProxyDotRadiusData.method8069(7.5F), zenithColor.method7294(0.08F));
         customDrawContext.drawArcBorder(floatValue4, y, width3, width3, 1.1F, 360.0F, 0.5F, zenithColor3.method7294(0.2F));
         if (f3 > 0.002F) {
            customDrawContext.drawArcBorder(floatValue4, y, width3, width3, 1.1F, 360.0F * f3, 0.5F, zenithColor6);
         }

         if (f5 > 0.002F) {
            customDrawContext.drawArcBorder(floatValue4 - 1.5F, y - 1.5F, width3 + 3.0F, width3 + 3.0F, 0.8F, 360.0F * f5, 0.5F, zenithColor5);
         }

         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(4.0F);
         if (livingEntity instanceof AbstractClientPlayer player) {
            customDrawContext.drawPlayerHeadWithRoundedShader(
               player.getSkin().body().texturePath(),
               floatValue4 + f9,
               y + f9,
               width4,
               headerRadius,
               ZenithColor.INSTANCE.method7295(new ZenithColor(255, 125, 135), f10 * 0.65F)
            );
         } else {
            customDrawContext.drawRoundedRect(floatValue4 + f9, y + f9, width4, width4, headerRadius, zenithColor.method7294(0.25F));
            customDrawContext.drawText(nameFont, "?", floatValue4 + (width3 - nameFont.width("?")) / 2.0F, y + (width3 - nameFont.height()) / 2.0F, zenithColor2);
         }

         float floatValue5 = flag ? floatValue3 : floatValue3 + (width - nameFont.width(clip)) / 2.0F;
         if (previewAlpha < 0.999F) {
            customDrawContext.drawText(nameFont, clip, floatValue5, f8 - previewAlpha * 2.0F, zenithColor2.method7294(1.0F - previewAlpha));
         }

         if (previewAlpha > 0.001F) {
            float floatValue6 = flag ? floatValue3 : floatValue3 + (width - nameFont.width(clip2)) / 2.0F;
            customDrawContext.drawText(nameFont, clip2, floatValue6, f8 + (1.0F - previewAlpha) * 2.0F, zenithColor.method7294(previewAlpha));
         }

         float f11 = floatValue2 + width2 - 4.0F - f7;
         customDrawContext.drawRect(f11 - 2.0F, y1 + 6.0F, 0.5F, height - 12.0F, zenithColor3.method7294(0.2F));
         customDrawContext.drawText(font, text, f11 + (f7 - font.width(text)) / 2.0F, y1 + 5.0F, zenithColor4.method7295(zenithColor2, 0.3F));
         String text2 = f4 > 0.0F ? "+" + Math.round(f4) : "HP";
         customDrawContext.drawText(font2, text2, f11 + (f7 - font2.width(text2)) / 2.0F, y1 + 16.0F, f4 > 0.0F ? zenithColor5 : zenithColor3);
         if (flag) {
            this.method7234(customDrawContext, (Player)livingEntity, floatValue3, y1 + 12.0F, width);
         }
      } finally {
         customDrawContext.popMatrix();
      }
   }

   private void method7234(CustomDrawContext customDrawContext, Player livingEntity, float f, float f2, float width) {
      var stacks = ArmorSlots.method5384(livingEntity);
      ItemStack[] stacks2 = new ItemStack[]{
         livingEntity.getMainHandItem(), livingEntity.getOffhandItem(), (ItemStack)stacks.get(3), (ItemStack)stacks.get(2), (ItemStack)stacks.get(1), (ItemStack)stacks.get(0)
      };
      ZenithStyle style = HudListLayout.style();
      float f3 = 49.0F;
      float x = f + (width - f3) / 2.0F;
      float y = f2 + 1.5F;
      Font font = Fonts.ICONS.getFont(4.5F);
      boolean flag = Util14.method2639(livingEntity);

      for (int i = 0; i < stacks2.length; i++) {
         if (i == 2) {
            customDrawContext.drawRect(x + 0.5F, y + 2.0F, 0.5F, 3.0F, style.getTextTertiary().getZenithColor().method7294(0.22F));
            x += 2.0F;
         }

         ItemStack itemStack = stacks2[i];
         if (itemStack.isEmpty()) {
            customDrawContext.drawText(font, "M", x + (7.0F - font.width("M")) / 2.0F, y + (7.0F - font.height()) / 2.0F, style.getTextTertiary().getZenithColor());
         } else {
            customDrawContext.pushMatrix();

            try {
               customDrawContext.pose().translate(x, y);
               customDrawContext.pose().scale(0.4375F, 0.4375F);
               customDrawContext.item(itemStack, 0, 0);
               if (flag) {
                  customDrawContext.renderItemBar(itemStack, 0, 0);
               }

               customDrawContext.renderItemCooldown(itemStack, 0, 0);
            } finally {
               customDrawContext.popMatrix();
            }
         }

         x += 8.0F;
      }
   }

   public void method7235(LivingEntity livingEntity) {
      if (livingEntity == null) {
         this.animation3.method2808(0.0F);
         if (this.animation3.getValue() == 0.0F) {
            this.livingEntity = null;
         }
      } else if (livingEntity != this.livingEntity) {
         this.animation3.method2808(0.0F);
         if (this.animation3.getValue() == 0.0F) {
            this.livingEntity = livingEntity;
         }
      } else {
         this.animation3.method2808(1.0F);
      }
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   private static String method7236(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }
}
