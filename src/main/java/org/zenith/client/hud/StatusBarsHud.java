package org.zenith.client.hud;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Hud.HeartType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.combat.PvpHelperModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.StatusBarColors;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class StatusBarsHud extends Interface {
   private final List<StatusBarsHud.Entry> items = new ArrayList<>();
   private Animation animation = new Animation(200L, Easing.INSTANCE10);
   private MutableComponent mutableText;
   private boolean flag;

   public StatusBarsHud(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
      float height = 22.0F;
      this.width = height * 9.0F;
      this.height = height;

      for (int i = 0; i < 9; i++) {
         this.items.add(new StatusBarsHud.Entry(i));
      }
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      this.height = 22.0F;
      this.width = 198.0F;
      float x = this.getX();
      float y = this.getY();
      float intValue = InterfaceModule.method6801();
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (mc.gameMode.getPlayerMode().isCreative()) {
         this.method5546(customDrawContext, y - 35.0F);
         this.method5548(customDrawContext, mc.getDeltaTracker(), y - 35.0F - 9.0F);
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         int experienceLevel = mc.player.experienceLevel;
         customDrawContext.drawText(
            font,
            String.valueOf(experienceLevel),
            x + this.width / 2.0F - font.width(String.valueOf(experienceLevel)) / 2.0F,
            y - 15.0F + font.height() / 2.0F,
            ZenithColor.INSTANCE3
         );
         customDrawContext.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
         customDrawContext.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(intValue),
            currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         ItemStack offhandItem = mc.player.getOffhandItem();
         if (!offhandItem.isEmpty()) {
            float floatValue = x - this.height - 12.0F;
            customDrawContext.drawBlurHud(floatValue, y, this.height, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
            customDrawContext.drawRoundedRect(
               floatValue,
               y,
               this.height,
               this.height,
               ProxyDotRadiusData.method8069(intValue),
               currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
            );
            customDrawContext.pushMatrix();
            customDrawContext.pose().translate((float)(floatValue + 4.6), (float)(y + 4.6));
            customDrawContext.pose().scale(0.8F, 0.8F);
            customDrawContext.item(offhandItem, 0, 0);
            customDrawContext.renderItemBar(offhandItem, 0, 0);
            customDrawContext.renderItemCooldown(offhandItem, 0, 0);
            customDrawContext.popMatrix();
            if (offhandItem.getCount() > 1) {
               String text = "x" + offhandItem.getCount();
               float width = font.width(text);
               float f = floatValue + 22.0F - width - 1.0F;
               float centeredTextY = y + 22.0F - font.height() - 3.0F;
               customDrawContext.drawText(font, text, f, centeredTextY, currentStyle.getTextTertiary().getZenithColor());
            }
         }

         float floatValue = x;

         for (StatusBarsHud.Entry entry : this.items) {
            entry.method5558(customDrawContext, floatValue, y, currentStyle);
            floatValue += this.height;
         }
      } else {
         if (mc.gameMode.canHurtPlayer()) {
            int n = customDrawContext.guiWidth() / 2 - 91;
            int n2 = customDrawContext.guiHeight() - 39;
            float f = 0.9F;
            customDrawContext.pose().pushMatrix();
            customDrawContext.pose().translate(x, y - 12.0F);
            customDrawContext.pose().scale(f, f);
            customDrawContext.pose().translate(-n, -n2);
            if (!mc.gameMode.getPlayerMode().isCreative()) {
               this.method5549(customDrawContext);
            }

            customDrawContext.pose().popMatrix();
            this.method5546(customDrawContext, y - 35.0F);
            this.method5548(customDrawContext, mc.getDeltaTracker(), y - 35.0F - 9.0F);
            Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
            int experienceLevel = mc.player.experienceLevel;
            customDrawContext.drawText(
               font,
               String.valueOf(experienceLevel),
               x + this.width / 2.0F - font.width(String.valueOf(experienceLevel)) / 2.0F,
               y - 15.0F + font.height() / 2.0F,
               ZenithColor.INSTANCE3
            );
            customDrawContext.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
            customDrawContext.drawRoundedRect(
               this.floatValue,
               this.floatValue2,
               this.width,
               this.height,
               ProxyDotRadiusData.method8069(intValue),
               currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
            );
            ItemStack offhandItem = mc.player.getOffhandItem();
            if (!offhandItem.isEmpty()) {
               float floatValue = x - this.height - 12.0F;
               customDrawContext.drawBlurHud(floatValue, y, this.height, this.height, 21.0F, ProxyDotRadiusData.method8069(intValue), ZenithColor.INSTANCE);
               customDrawContext.drawRoundedRect(
                  floatValue,
                  y,
                  this.height,
                  this.height,
                  ProxyDotRadiusData.method8069(intValue),
                  currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
               );
               customDrawContext.pushMatrix();
               customDrawContext.pose().translate((float)(floatValue + 4.6), (float)(y + 4.6));
               customDrawContext.pose().scale(0.8F, 0.8F);
               customDrawContext.item(offhandItem, 0, 0);
               customDrawContext.renderItemBar(offhandItem, 0, 0);
               customDrawContext.renderItemCooldown(offhandItem, 0, 0);
               customDrawContext.popMatrix();
               if (offhandItem.getCount() > 1) {
                  String text = "x" + offhandItem.getCount();
                  float width = font.width(text);
                  float floatValue2 = floatValue + 22.0F - width - 3.0F;
                  float centeredTextY = y + 22.0F - font.height() - 3.0F;
                  customDrawContext.drawText(font, text, floatValue2, centeredTextY, currentStyle.getTextTertiary().getZenithColor());
               }
            }

            float floatValue = x;

            for (StatusBarsHud.Entry entry : this.items) {
               entry.method5558(customDrawContext, floatValue, y, currentStyle);
               floatValue += this.height;
            }
         } else {
            this.width = 0.0F;
            this.height = 0.0F;
         }
      }
   }

   private void method5546(CustomDrawContext customDrawContext, float f) {
      if (this.animation == null) {
         this.animation = new Animation(300L, Easing.INSTANCE10);
      }

      this.animation.setDuration(200L);
      this.animation.setEasing(Easing.INSTANCE10);
      if (mc.gui.hud.toolHighlightTimer > 0 && !mc.gui.hud.lastToolHighlight.isEmpty()) {
         this.animation.method2808(1.0F);
         this.mutableText = Component.empty().append(mc.gui.hud.lastToolHighlight.getHoverName()).withStyle(mc.gui.hud.lastToolHighlight.getRarity().color());
         if (mc.gui.hud.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
            this.mutableText.withStyle(ChatFormatting.ITALIC);
         }
      } else {
         this.animation.method2808(0.0F);
      }

      if (this.mutableText != null && this.animation.getValue() > 0.0F) {
         this.method5547(customDrawContext, this.mutableText, f);
      }
   }

   private void method5547(CustomDrawContext customDrawContext, MutableComponent mutableText, float f) {
      Font nameFont = HudListLayout.nameFont();
      float width = nameFont.width(mutableText);
      float f2 = (this.width - width) / 2.0F;
      int y = (int)f;
      if (!mc.gameMode.canHurtPlayer() || mc.gameMode.getPlayerMode().isCreative()) {
         y += 14;
      }

      short s = 255;
      if (s > 0) {
         customDrawContext.pose().pushMatrix();
         customDrawContext.pose().translate(this.floatValue + f2, y);
         customDrawContext.drawText(nameFont, mutableText, 0.0F, 0.0F, ARGB.color((int)(s * this.animation.getValue()), -1));
         customDrawContext.pose().popMatrix();
      }
   }

   public final void method5548(CustomDrawContext customDrawContext, DeltaTracker deltaTracker, float y) {
      if (mc.gui.hud.overlayMessageString != null && mc.gui.hud.overlayMessageTime > 0) {
         float f = mc.gui.hud.overlayMessageTime - deltaTracker.getGameTimeDeltaPartialTick(false);
         int alpha = (int)(f * 255.0F / 20.0F);
         if (alpha > 255) {
            alpha = 255;
         }

         if (alpha > 8) {
            Font font = Fonts.NEW_MEDIUM.getFont(8.0F);
            customDrawContext.pose().pushMatrix();
            customDrawContext.pose().translate(this.floatValue + this.width / 2.0F, y);
            int hsvToArgb;
            if (mc.gui.hud.animateOverlayMessageColor) {
               hsvToArgb = Mth.hsvToArgb(f / 50.0F, 0.7F, 0.6F, alpha);
            } else {
               hsvToArgb = ARGB.color(alpha, -1);
            }

            float width = font.width(mc.gui.hud.overlayMessageString);
            customDrawContext.pose().translate(-width / 2.0F, -font.height() / 2.0F);
            customDrawContext.drawText(font, mc.gui.hud.overlayMessageString, 0.0F, 0.0F, hsvToArgb);
            customDrawContext.pose().popMatrix();
         }
      }
   }

   @Override
   public void load(JsonObject jsonObject) {
      this.flag = jsonObject.has("windowWidth");
      super.load(jsonObject);
   }

   @Override
   public void method288(float guiScaledWidth, float guiScaledHeight) {
      if (!(guiScaledHeight <= 0.0F) && !(guiScaledWidth <= 0.0F)) {
         if (!this.method2090()) {
            float f = this.floatValue3 > 0.0F ? this.floatValue3 : guiScaledWidth;
            float guiScaledHeight2 = this.floatValue4 > 0.0F ? this.floatValue4 : guiScaledHeight;
            this.method2091(f, guiScaledHeight2);
         }

         float f = (this.method296() - this.width) / 2.0F;
         float f2 = this.method294();
         if (this.flag && this.floatValue3 > 0.0F && this.floatValue3 != guiScaledWidth) {
            float f3 = this.method294() + this.method296() / 2.0F - this.floatValue3 / 2.0F;
            f2 = guiScaledWidth / 2.0F + f3 - this.method296() / 2.0F;
         }

         this.flag = true;
         this.floatValue3 = guiScaledWidth;
         this.floatValue4 = guiScaledHeight;
         float f3 = this.method2084(guiScaledHeight) + this.floatValue6 * this.method2086(guiScaledHeight);
         this.floatValue = this.method2088(f2, guiScaledWidth) + f;
         this.floatValue2 = this.method2089(f3, guiScaledHeight) + this.method2065();
      }
   }

   @Override
   public void method2091(float guiScaledWidth, float guiScaledHeight) {
      if (!(guiScaledWidth <= 0.0F) && !(guiScaledHeight <= 0.0F)) {
         float f = this.method2083(guiScaledWidth);
         float f2 = this.method2086(guiScaledHeight);
         if (!(f <= 0.0F) && !(f2 <= 0.0F)) {
            this.floatValue6 = (this.method295() - this.method2084(guiScaledHeight)) / f2;
         }
      }
   }

   private void method5549(GuiGraphicsExtractor guiGraphicsExtractor) {
      LocalPlayer player = mc.player;
      if (player != null) {
         int displayHealth = Mth.ceil(player.getHealth());
         boolean isBlink = mc.gui.hud.healthBlinkTime > mc.gui.hud.tickCount && (mc.gui.hud.healthBlinkTime - mc.gui.hud.tickCount) / 3L % 2L == 1L;
         long millis = Util.getMillis();
         if (displayHealth < mc.gui.hud.lastHealth && player.getInvulnerableTime() > 0) {
            mc.gui.hud.lastHealthTime = millis;
            mc.gui.hud.healthBlinkTime = mc.gui.hud.tickCount + 20;
         } else if (displayHealth > mc.gui.hud.lastHealth && player.getInvulnerableTime() > 0) {
            mc.gui.hud.lastHealthTime = millis;
            mc.gui.hud.healthBlinkTime = mc.gui.hud.tickCount + 10;
         }

         if (millis - mc.gui.hud.lastHealthTime > 1000L) {
            mc.gui.hud.displayHealth = displayHealth;
            mc.gui.hud.lastHealthTime = millis;
         }

         mc.gui.hud.lastHealth = displayHealth;
         int n = mc.gui.hud.displayHealth;
         mc.gui.hud.random.setSeed(mc.gui.hud.tickCount * 312871);
         int n2 = guiGraphicsExtractor.guiWidth() / 2 - 91;
         int n3 = guiGraphicsExtractor.guiWidth() / 2 + 90 + 36;
         int n4 = guiGraphicsExtractor.guiHeight() - 39;
         float f = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(n, displayHealth));
         int n5 = Mth.ceil(player.getAbsorptionAmount());
         int n6 = Mth.ceil((f + n5) / 2.0F / 10.0F);
         int n7 = Math.max(10 - (n6 - 2), 3);
         int y = n4 - 10;
         int n8 = -1;
         if (player.hasEffect(MobEffects.REGENERATION)) {
            n8 = mc.gui.hud.tickCount % Mth.ceil(f + 5.0F);
         }

         Profiler.get().push("armor");
         method5550(guiGraphicsExtractor, player, n4, n6, n7, n2);
         Profiler.get().popPush("health");
         this.method5551(guiGraphicsExtractor, player, n2, n4, n7, n8, f, displayHealth, n, n5, isBlink);
         LivingEntity playerVehicleWithHealth = mc.gui.hud.getPlayerVehicleWithHealth();
         int vehicleMaxHearts = mc.gui.hud.getVehicleMaxHearts(playerVehicleWithHealth);
         if (vehicleMaxHearts == 0) {
            Profiler.get().popPush("food");
            this.method5552(guiGraphicsExtractor, player, n4, n3);
            y -= 10;
         }

         Profiler.get().popPush("air");
         this.method5554(guiGraphicsExtractor, player, vehicleMaxHearts, y, n3);
         Profiler.get().pop();
      }
   }

   private static void method5550(GuiGraphicsExtractor guiGraphicsExtractor, Player player, int n, int n2, int n3, int n4) {
      int armorValue = player.getArmorValue();
      if (armorValue > 0) {
         int y = n - (n2 - 1) * n3 - 10;

         for (int i = 0; i < 10; i++) {
            int x = n4 + i * 8;
            if (i * 2 + 1 < armorValue) {
               guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.ARMOR_FULL_SPRITE, x, y, 9, 9);
            }

            if (i * 2 + 1 == armorValue) {
               guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.ARMOR_HALF_SPRITE, x, y, 9, 9);
            }

            if (i * 2 + 1 > armorValue) {
               guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.ARMOR_EMPTY_SPRITE, x, y, 9, 9);
            }
         }
      }
   }

   private void method5551(
      GuiGraphicsExtractor guiGraphicsExtractor, Player player, int n, int n2, int n3, int n4, float f, int displayHealth, int n5, int n6, boolean isBlink
   ) {
      HeartType heartType = HeartType.forPlayer(player);
      boolean hardcore = player.level().getLevelData().isHardcore();
      int n7 = Mth.ceil(f / 2.0);
      int n8 = Mth.ceil(n6 / 2.0);
      int n9 = n7 * 2;

      for (int i = n7 + n8 - 1; i >= 0; i--) {
         int n10 = i / 10;
         int n11 = i % 10;
         int x = n + n11 * 8;
         int y = n2 - n10 * n3;
         if (displayHealth + n6 <= 4) {
            y += mc.gui.hud.random.nextInt(2);
         }

         if (i < n7 && i == n4) {
            y -= 2;
         }

         this.method5553(guiGraphicsExtractor, HeartType.CONTAINER, x, y, hardcore, isBlink, false);
         int n12 = i * 2;
         boolean flag = i >= n7;
         if (flag) {
            int n13 = n12 - n9;
            if (n13 < n6) {
               boolean isHalf = n13 + 1 == n6;
               this.method5553(guiGraphicsExtractor, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, x, y, hardcore, false, isHalf);
            }
         }

         if (isBlink && n12 < n5) {
            boolean isHalf = n12 + 1 == n5;
            this.method5553(guiGraphicsExtractor, heartType, x, y, hardcore, true, isHalf);
         }

         if (n12 < displayHealth) {
            boolean isHalf = n12 + 1 == displayHealth;
            this.method5553(guiGraphicsExtractor, heartType, x, y, hardcore, false, isHalf);
         }
      }
   }

   private void method5552(GuiGraphicsExtractor context, Player player, int n, int n2) {
      FoodData foodData = player.getFoodData();
      int foodLevel = foodData.getFoodLevel();

      for (int i = 0; i < 10; i++) {
         int y = n;
         Identifier location;
         Identifier id;
         Identifier location2;
         if (player.hasEffect(MobEffects.HUNGER)) {
            location = net.minecraft.client.gui.Hud.FOOD_EMPTY_HUNGER_SPRITE;
            id = net.minecraft.client.gui.Hud.FOOD_HALF_HUNGER_SPRITE;
            location2 = net.minecraft.client.gui.Hud.FOOD_FULL_HUNGER_SPRITE;
         } else {
            location = net.minecraft.client.gui.Hud.FOOD_EMPTY_SPRITE;
            id = net.minecraft.client.gui.Hud.FOOD_HALF_SPRITE;
            location2 = net.minecraft.client.gui.Hud.FOOD_FULL_SPRITE;
         }

         if (player.getFoodData().getSaturationLevel() <= 0.0F && mc.gui.hud.tickCount % (foodLevel * 3 + 1) == 0) {
            y = n + (mc.gui.hud.random.nextInt(3) - 1);
         }

         int x = n2 - i * 8 - 9;
         context.blitSprite(RenderPipelines.GUI_TEXTURED, location, x, y, 9, 9);
         if (i * 2 + 1 < foodLevel) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, location2, x, y, 9, 9);
         }

         if (i * 2 + 1 == foodLevel) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, id, x, y, 9, 9);
         }
      }

      StatusBarColors.method1755(context, player, n, n2);
   }

   private void method5553(GuiGraphicsExtractor guiGraphicsExtractor, HeartType heartType, int x, int y, boolean hardcore, boolean isBlink, boolean isHalf) {
      guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, heartType.getSprite(hardcore, isHalf, isBlink), x, y, 9, 9);
   }

   private void method5554(GuiGraphicsExtractor guiGraphicsExtractor, Player player, int vehicleMaxHearts, int y, int n) {
      int maxAirSupply = player.getMaxAirSupply();
      int n2 = Math.clamp((long)player.getAirSupply(), 0, maxAirSupply);
      boolean eyeInFluid = player.isEyeInFluid(FluidTags.WATER);
      if (eyeInFluid || n2 < maxAirSupply) {
         y = this.method5555(vehicleMaxHearts, y);
         int n3 = this.method5556(n2, maxAirSupply, -2);
         int n4 = this.method5556(n2, maxAirSupply, 0);
         int emptyAirBubbles = 10 - this.method5556(n2, maxAirSupply, this.method5557(n2, eyeInFluid));
         boolean flag = n3 != n4;
         if (!eyeInFluid) {
            mc.gui.hud.lastBubblePopSoundPlayed = 0;
         }

         for (int i = 1; i <= 10; i++) {
            int x = n - (i - 1) * 8 - 9;
            if (i <= n3) {
               guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.AIR_SPRITE, x, y, 9, 9);
            } else if (flag && i == n4 && eyeInFluid) {
               guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.AIR_POPPING_SPRITE, x, y, 9, 9);
               mc.gui.hud.playAirBubblePoppedSound(i, player, emptyAirBubbles);
            } else if (i > 10 - emptyAirBubbles) {
               int nextInt = emptyAirBubbles == 10 && mc.gui.hud.tickCount % 2 == 0 ? mc.gui.hud.random.nextInt(2) : 0;
               guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.AIR_EMPTY_SPRITE, x, y + nextInt, 9, 9);
            }
         }
      }
   }

   private int method5555(int vehicleMaxHearts, int y) {
      int n = mc.gui.hud.getVisibleVehicleHeartRows(vehicleMaxHearts) - 1;
      return y - n * 10;
   }

   private int method5556(int n, int maxAirSupply, int n2) {
      return Mth.ceil((float)((n + n2) * 10) / maxAirSupply);
   }

   private int method5557(int n, boolean eyeInFluid) {
      return n != 0 && eyeInFluid ? 1 : 0;
   }

   public class Entry {
      private final Animation animation = new Animation(260L, 1.0F, Easing.INSTANCE10);
      private final Animation animation2 = new Animation(150L, 0.0F, Easing.INSTANCE13);
      private boolean flag;
      private final int intValue;

      private Entry(int intValue) {
         this.intValue = intValue;
      }

      public void method5558(CustomDrawContext customDrawContext, float floatValue, float y, ZenithStyle currentStyle) {
         float intValue = InterfaceModule.method6801();
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(intValue);
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         boolean flag = this.intValue == MinecraftAccess.mc.player.getInventory().getSelectedSlot();
         if (flag && !this.flag) {
            this.animation.method2809(0.0F);
         }

         if (!flag) {
            this.animation.method2809(1.0F);
         }

         this.flag = flag;
         float f = flag ? this.animation.method2808(1.0F) : 1.0F;
         ZenithColor zenithColor = currentStyle.getHeaderHudBackground().method1447(flag ? 1.0F : 0.0F);
         ZenithColor zenithColor2 = flag ? currentStyle.getTextEnable().getZenithColor() : currentStyle.getTextSecondary().getZenithColor();
         ItemStack nonEquipmentItem = MinecraftAccess.mc.player.getInventory().getNonEquipmentItems().get(this.intValue);
         this.animation2.method2807(nonEquipmentItem.isEmpty());
         customDrawContext.drawRoundedRect(floatValue, y, 22.0F, 22.0F, headerRadius, zenithColor);
         if (flag) {
            customDrawContext.drawRoundedRect(floatValue, y, 22.0F, 22.0F, headerRadius, currentStyle.getPrimaryColor().getZenithColor().method7293(100));
         }

         if (PvpHelperModule.INSTANCE.method4645(this.intValue)) {
            customDrawContext.drawRoundedRect(floatValue, y, 22.0F, 22.0F, headerRadius, currentStyle.getPrimaryColor().getZenithColor().method7294(0.45F));
         }

         if (flag && f < 1.0F) {
            ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7295(ZenithColor.INSTANCE, 0.55F);
            float f2 = 1.0F - (1.0F - f) * (1.0F - f) * (1.0F - f);
            float f3 = 4.0F * (1.0F - f2);
            float width = 22.0F - f3 * 2.0F;
            customDrawContext.drawRoundedBorder(
               floatValue + f3,
               y + f3,
               width,
               width,
               1.2F,
               ProxyDotRadiusData.method8069(Math.max(0.0F, intValue - f3)),
               zenithColor3.method7292(0.8F * (1.0F - f))
            );
         }

         customDrawContext.pushMatrix();
         customDrawContext.pose().translate((float)(floatValue + 4.6), (float)(y + 4.6));
         customDrawContext.pose().scale(0.8F, 0.8F);
         customDrawContext.item(nonEquipmentItem, 0, 0);
         customDrawContext.renderItemBar(nonEquipmentItem, 0, 0);
         customDrawContext.renderItemCooldown(nonEquipmentItem, 0, 0);
         customDrawContext.popMatrix();
         customDrawContext.drawText(
            font,
            String.valueOf(this.intValue + 1),
            floatValue + (22.0F - font.width(String.valueOf(this.intValue + 1))) / 2.0F,
            y + (22.0F - font.height()) / 2.0F,
            currentStyle.getTextTertiary().method1447(this.animation2.getValue())
         );
         if (nonEquipmentItem.getCount() > 1) {
            String text = "x" + nonEquipmentItem.getCount();
            float width = font.width(text);
            float floatValue2 = floatValue + 22.0F - width - 3.0F;
            float centeredTextY = y + 22.0F - font.height() - 3.0F;
            customDrawContext.drawText(font, text, floatValue2, centeredTextY, zenithColor2);
         }
      }
   }
}
