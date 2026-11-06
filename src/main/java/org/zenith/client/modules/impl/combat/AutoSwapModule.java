package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Comparator;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.ClickSlotEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "AutoSwap",
   category = ModuleCategory.COMBAT,
   description = "module.autoswap.description"
)
public final class AutoSwapModule extends Module {
   public static final AutoSwapModule INSTANCE = new AutoSwapModule();
   private final ModeSetting mode = new ModeSetting(
      "module.autoSwap.mode", "module.autoSwap.mode.desc", "module.autoSwap.mode.items", "module.autoSwap.mode.wheel"
   );
   private final ModeSetting itemType = new ModeSetting(
      "module.autoSwap.itemType",
      "module.autoSwap.itemType.desc",
      () -> this.mode.isSelected(0),
      "module.autoSwap.itemHead",
      "module.autoSwap.itemTotem",
      "module.autoSwap.itemGapple",
      "module.autoSwap.itemShield"
   );
   private final ModeSetting swapType = new ModeSetting(
      "module.autoSwap.swapType",
      "module.autoSwap.swapType.desc",
      () -> this.mode.isSelected(0),
      "module.autoSwap.swapHead",
      "module.autoSwap.swapTotem",
      "module.autoSwap.swapGapple",
      "module.autoSwap.swapShield"
   );
   private final BindSetting keyToSwap = new BindSetting("module.autoSwap.keyToSwap", "module.autoSwap.keyToSwap.desc", -1, () -> this.mode.isSelected(0));
   private final BindSetting keyToSwapCerb = new BindSetting(
      "module.autoSwap.keyToSwapCerb", "module.autoSwap.keyToSwapCerb.desc", -1, () -> this.mode.isSelected(0) && zenith.getServerDetector().method1642()
   );
   private final BindSetting wheelBind = new BindSetting("module.autoSwap.wheelBind", "module.autoSwap.wheelBind.desc", -1, () -> this.mode.isSelected(1));
   private final int intValue = 3;
   private final String text = "minecraft:air";
   private final String text2 = "item";
   private final String text3 = "stack";
   private final float floatValue = 60.0F;
   private final float floatValue2 = 94.0F;
   private final float floatValue3 = 0.35F;
   private final long longValue = 250L;
   private final AutoSwapModule.Entry2[] entry2s = new AutoSwapModule.Entry2[3];
   private boolean flag;
   private boolean flag2;
   private int intValue2 = -1;
   private int intValue3 = -1;
   private int intValue4 = -1;
   private long longValue2;
   private final Animation animation = new Animation(280L, Easing.entry22);
   private final Animation[] animations = new Animation[]{
      new Animation(160L, Easing.INSTANCE18), new Animation(160L, Easing.INSTANCE18), new Animation(160L, Easing.INSTANCE18)
   };

   private AutoSwapModule() {
      for (int i = 0; i < this.entry2s.length; i++) {
         this.entry2s[i] = new AutoSwapModule.Entry2();
      }
   }

   @Override
   public void onDisable() {
      this.method7732();
      super.onDisable();
   }

   @EventTarget
   public void method7682(KeyEvent event) {
      if (mc.player != null && mc.player.containerMenu instanceof InventoryMenu) {
         if (this.mode.isSelected(1)) {
            this.method7686(event);
         } else if (mc.gui.screen() == null) {
            if (event.getIntValue() == 1) {
               Slot slot = this.method7711();
               if (event.isPress(this.keyToSwap.getKeyCode())) {
                  Slot slot2 = InventoryUtils.method913(
                     this.method7712(this.itemType),
                     Comparator.comparing(slot3 -> slot3.getItem().isEnchanted()),
                     var1x -> var1x != slot && var1x.index != 46 && var1x.index != 45
                  );
                  Slot slot3 = InventoryUtils.method913(
                     this.method7712(this.swapType),
                     Comparator.comparing(slot4 -> slot4.getItem().isEnchanted()),
                     var1x -> var1x != slot && var1x.index != 46 && var1x.index != 45
                  );
                  Slot slot4 = slot2 != null && mc.player.getOffhandItem().getItem() != slot2.getItem().getItem() ? slot2 : slot3;
                  this.method7710(slot4);
               }

               if (event.isPress(this.keyToSwapCerb.getKeyCode()) && slot != null) {
                  this.method7710(slot);
               }
            }
         }
      }
   }

   @EventTarget(4)
   public void method7683(ScreenRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.mode.isSelected(1)) {
            if (mc.gui.screen() == null) {
               if (this.intValue2 != -1) {
                  this.method7732();
               } else {
                  this.method7693(event);
               }
            }
         } else {
            if (this.flag || this.flag2) {
               this.method7732();
            }
         }
      }
   }

   @EventTarget
   public void method7684(MouseButtonEvent event) {
      if (this.mode.isSelected(1) && this.flag && mc.player != null && mc.level != null && mc.gui.screen() == null) {
         if (event.getIntValue2() == 1) {
            float mouseX = this.method7729();
            float mouseY = this.method7730();
            float floatValue = mc.getWindow().getGuiScaledWidth() / 2.0F;
            float f = mc.getWindow().getGuiScaledHeight() / 2.0F;
            if (event.getIntValue() == 1 && this.method7727(mouseX, mouseY, floatValue, f)) {
               this.method7690();
            } else {
               int n;
               if ((n = this.method7726(mouseX, mouseY, floatValue, f, 60.0F)) != -1) {
                  this.method7688(event.getIntValue(), n);
               }
            }
         }
      }
   }

   @EventTarget
   public void method7685(ClickSlotEvent event) {
      if (this.mode.isSelected(1) && this.intValue2 != -1 && mc.player != null) {
         if (mc.gui.screen() instanceof InventoryScreen) {
            if (event.getSlotActionType() == ContainerInput.PICKUP) {
               this.method7692(event);
            }
         }
      }
   }

   private void method7686(KeyEvent event) {
      if (event.isKey(this.wheelBind.getKeyCode())) {
         if (event.getIntValue() == 1) {
            this.method7687();
         } else if (event.getIntValue() == 0) {
            if (this.intValue2 != -1 && mc.gui.screen() instanceof InventoryScreen) {
               return;
            }

            this.method7732();
         }
      }
   }

   private void method7687() {
      if (mc.gui.screen() == null) {
         this.flag = true;
         this.method7731(true);
      }
   }

   private void method7688(int intValue, int n) {
      if (intValue == 1) {
         this.method7689(n);
      } else {
         if (intValue == 0) {
            this.method7691(n);
         }
      }
   }

   private void method7689(int intValue) {
      AutoSwapModule.Entry2 entry2 = this.method7723(intValue);
      if (entry2 != null) {
         entry2.clear();
         this.intValue3 = intValue;
         this.longValue2 = System.currentTimeMillis() + 250L;
      }
   }

   private void method7690() {
      for (AutoSwapModule.Entry2 entry2 : this.entry2s) {
         entry2.clear();
      }

      this.intValue3 = -1;
      this.longValue2 = System.currentTimeMillis() + 250L;
   }

   private void method7691(int n) {
      this.intValue2 = n;
      mc.gui.setScreen(new InventoryScreen(mc.player));
   }

   private void method7692(ClickSlotEvent event) {
      NonNullList slots = mc.player.containerMenu.slots;
      if (event.getIntValue2() >= 0 && event.getIntValue2() < slots.size()) {
         ItemStack itemStack = ((Slot)slots.get(event.getIntValue2())).getItem();
         if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
            AutoSwapModule.Entry2 entry2 = this.method7723(this.intValue2);
            if (entry2 != null) {
               entry2.method7733(itemStack);
               event.setCancelled(true);
               mc.gui.setScreen(null);
               this.method7732();
            }
         }
      }
   }

   private void method7693(ScreenRenderEvent event) {
      float f;
      if ((f = this.animation.animateTo(this.flag ? 1.0F : 0.0F)) <= 0.005F && !this.flag) {
         for (Animation animation : this.animations) {
            animation.snapTo(0.0F);
         }
      } else {
         if (this.flag) {
            this.method7731(true);
         }

         AutoSwapModule.Entry entry = this.method7694(event.getMenuDrawContext(), f);
         if (entry != null) {
            if (this.flag && entry.floatValue6 > 0.4F) {
               this.method7713(entry.intValue);
            }

            this.method7695(entry);
            this.method7696(entry);
            this.method7702(entry);
            this.method7706(entry);
            this.method7708(entry);
         }
      }
   }

   private AutoSwapModule.Entry method7694(MenuDrawContext menuDrawContext, float f) {
      float floatValue = mc.getWindow().getGuiScaledWidth() / 2.0F;
      float f2 = mc.getWindow().getGuiScaledHeight() / 2.0F;
      float f3 = 0.85F + 0.15F * f;
      float f4 = 60.0F * f3;
      float f5 = 94.0F * f3;
      int intValue = this.flag ? this.method7726(menuDrawContext.getMouseX(), menuDrawContext.getMouseY(), floatValue, f2, f4) : -1;
      boolean flag = this.flag && this.method7727(menuDrawContext.getMouseX(), menuDrawContext.getMouseY(), floatValue, f2);
      ZenithStyle currentStyle = zenith.getStyleManager().getCurrentStyle();
      return currentStyle == null
         ? null
         : new AutoSwapModule.Entry(menuDrawContext, MatrixUtils.method666(menuDrawContext.pose()), floatValue, f2, f4, f5, f3, f, intValue, flag, currentStyle);
   }

   private void method7695(AutoSwapModule.Entry entry) {
      float blurPower;
      if (!((blurPower = zenith.getNLMenuScreen().getBlurPower()) <= 0.0F)) {
         float intValue = entry.floatValue4 + 7.0F;
         RenderShaders.method5287(
            entry.matrixStack.last().pose(),
            entry.floatValue - intValue,
            entry.floatValue2 - intValue,
            intValue * 2.0F,
            intValue * 2.0F,
            blurPower,
            ProxyDotRadiusData.method8069(intValue),
            ZenithColor.INSTANCE.method7294(entry.floatValue6 * 0.92F),
            true,
            false
         );
      }
   }

   private void method7696(AutoSwapModule.Entry entry) {
      float f = this.method7697();
      float f2 = this.method7698();
      float f3 = f - f2;
      boolean flag = this.intValue3 != -1 && System.currentTimeMillis() <= this.longValue2;

      for (int i = 0; i < 3; i++) {
         float hover = this.animations[i].animateTo(i == entry.intValue ? 1.0F : 0.0F);
         boolean flag2 = this.method7714(i);
         AutoSwapModule.Entry3 entry3 = this.method7699(entry, hover, flag && i == this.intValue3, flag2);
         float f4 = entry.floatValue4 + hover * 6.0F;
         this.method7700(entry, i * f + f2 * 0.5F);
         this.method7701(entry, f4, f3, entry3.zenithColor, entry3.zenithColor2);
         if (hover > 0.01F) {
            RenderShaders.method5275(
               entry.matrixStack.last().pose(),
               entry.floatValue - f4,
               entry.floatValue2 - f4,
               f4 * 2.0F,
               f4 * 2.0F,
               1.35F + hover,
               f3,
               0.0F,
               entry.zenithStyle.getPrimaryColor().getZenithColor().method7294(entry.floatValue6 * hover * 0.8F)
            );
         }

         entry.matrixStack.popPose();
      }
   }

   private float method7697() {
      return 120.0F;
   }

   private float method7698() {
      return 2.4F;
   }

   private AutoSwapModule.Entry3 method7699(AutoSwapModule.Entry entry, float hover, boolean flag, boolean flag2) {
      if (flag) {
         return new AutoSwapModule.Entry3(
            new ZenithColor(255, 90, 90, (int)(220.0F * entry.floatValue6)), new ZenithColor(210, 40, 40, (int)(170.0F * entry.floatValue6))
         );
      } else if (flag2) {
         return new AutoSwapModule.Entry3(
            entry.zenithStyle
               .getDisableActiveBg()
               .getZenithColor()
               .method7294(entry.floatValue6)
               .method7295(entry.zenithStyle.getPrimaryColor().getZenithColor().method7294(entry.floatValue6), hover * 0.45F),
            entry.zenithStyle
               .getRightBackground()
               .getZenithColor()
               .method7294(entry.floatValue6 * 0.9F)
               .method7295(entry.zenithStyle.getPrimaryColor().getZenithColor().method7296(0.55F).method7294(entry.floatValue6), hover * 0.4F)
         );
      } else {
         ZenithColor zenithColor = entry.zenithStyle.getSurfaceDisableBackground().getZenithColor().method7294(entry.floatValue6);
         ZenithColor zenithColor2 = entry.zenithStyle.getRightBackground().getZenithColor().method7294(entry.floatValue6 * 0.92F);
         if (hover <= 0.001F) {
            return new AutoSwapModule.Entry3(zenithColor, zenithColor2);
         } else {
            ZenithColor zenithColor3 = entry.zenithStyle
               .getSurfaceEnableBackground()
               .getZenithColor()
               .method7294(entry.floatValue6)
               .method7295(entry.zenithStyle.getPrimaryColor().getZenithColor().method7294(entry.floatValue6), hover * 0.65F);
            ZenithColor zenithColor4 = entry.zenithStyle.getPrimaryColor().getZenithColor().method7296(0.48F).method7294(entry.floatValue6);
            return new AutoSwapModule.Entry3(zenithColor.method7295(zenithColor3, hover), zenithColor2.method7295(zenithColor4, hover));
         }
      }
   }

   private void method7700(AutoSwapModule.Entry entry, float angle) {
      entry.matrixStack.pushPose();
      entry.matrixStack.translate(entry.floatValue, entry.floatValue2, 0.0F);
      entry.matrixStack.rotate(Axis.ZP.rotationDegrees(angle));
      entry.matrixStack.translate(-entry.floatValue, -entry.floatValue2, 0.0F);
   }

   private void method7701(AutoSwapModule.Entry entry, float f, float f2, ZenithColor zenithColor, ZenithColor zenithColor2) {
      RenderShaders.method5276(
         entry.matrixStack.last().pose(),
         entry.floatValue - f,
         entry.floatValue2 - f,
         f * 2.0F,
         f * 2.0F,
         f - entry.floatValue3,
         f2,
         0.0F,
         zenithColor,
         zenithColor2,
         zenithColor2,
         zenithColor
      );
   }

   private void method7702(AutoSwapModule.Entry entry) {
      for (int i = 0; i < 3; i++) {
         this.method7703(entry, i, false);
      }

      ScissorFlusher.flush();

      for (int i = 0; i < 3; i++) {
         this.method7703(entry, i, true);
      }
   }

   private void method7703(AutoSwapModule.Entry entry, int intValue, boolean flag) {
      ItemStack itemStack = this.method7717(intValue);
      float f = this.animations[intValue].getValue();
      float f2 = this.method7728(intValue);
      float f3 = (entry.floatValue3 + entry.floatValue4) / 2.0F + f * 4.0F;
      float f4 = entry.floatValue + (float)Math.cos(f2) * f3;
      float f5 = entry.floatValue2 + (float)Math.sin(f2) * f3;
      if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
         if (flag) {
            this.method7705(entry, itemStack, f4, f5);
         } else {
            entry.menuDrawContext.item(itemStack, (int)(f4 - 8.0F), (int)(f5 - 8.0F));
         }
      } else {
         if (!flag) {
            this.method7704(entry, f4, f5, f);
         }
      }
   }

   private void method7704(AutoSwapModule.Entry entry, float f, float f2, float f3) {
      ZenithColor zenithColor = entry.zenithStyle.getTextTertiary().getZenithColor().method7294(entry.floatValue6 * (0.62F + f3 * 0.38F));
      entry.menuDrawContext.drawRoundedRect(f - 5.0F, f2 - 0.7F, 10.0F, 1.4F, ProxyDotRadiusData.method8069(0.7F), zenithColor);
      entry.menuDrawContext.drawRoundedRect(f - 0.7F, f2 - 5.0F, 1.4F, 10.0F, ProxyDotRadiusData.method8069(0.7F), zenithColor);
   }

   private void method7705(AutoSwapModule.Entry entry, ItemStack itemStack, float f, float f2) {
      int n;
      if ((n = this.method7716(itemStack)) > 1) {
         Font font = Fonts.NEW_SEMIBOLD.getFont(6.0F);
         String text = String.valueOf(n);
         float floatValue = f + 8.0F - font.width(text);
         float centeredTextY = f2 + 5.5F;
         entry.menuDrawContext
            .drawText(font, text, floatValue + 0.5F, centeredTextY + 0.5F, entry.zenithStyle.getRightBackground().getZenithColor().method7294(entry.floatValue6));
         entry.menuDrawContext.drawText(font, text, floatValue, centeredTextY, entry.zenithStyle.getTextEnable().getZenithColor().method7294(entry.floatValue6));
      }
   }

   private void method7706(AutoSwapModule.Entry entry) {
      this.method7707(entry);
      if (mc.player != null) {
         ItemStack offhandItem = mc.player.getOffhandItem();
         if (!offhandItem.isEmpty() && offhandItem.getItem() != Items.AIR) {
            entry.menuDrawContext.item(offhandItem, (int)(entry.floatValue - 8.0F), (int)(entry.floatValue2 - 8.0F));
         }
      }
   }

   private void method7707(AutoSwapModule.Entry entry) {
      float intValue = 32.0F * entry.floatValue5;
      entry.menuDrawContext
         .drawRoundedRect(
            entry.floatValue - intValue,
            entry.floatValue2 - intValue,
            intValue * 2.0F,
            intValue * 2.0F,
            ProxyDotRadiusData.method8069(intValue),
            entry.zenithStyle.getLeftBackground().getZenithColor().method7294(entry.floatValue6 * 0.97F)
         );
      if (entry.flag) {
         entry.menuDrawContext
            .drawRoundedBorder(
               entry.floatValue - intValue - 1.0F,
               entry.floatValue2 - intValue - 1.0F,
               (intValue + 1.0F) * 2.0F,
               (intValue + 1.0F) * 2.0F,
               1.2F,
               ProxyDotRadiusData.method8069(intValue + 1.0F),
               entry.zenithStyle.getPrimaryColor().getZenithColor().method7294(entry.floatValue6 * 0.75F)
            );
      }
   }

   private void method7708(AutoSwapModule.Entry entry) {
      if (!(entry.floatValue6 <= 0.5F)) {
         String string;
         String text;
         if (entry.flag) {
            string = "Вторая рука";
            text = "ПКМ очистить всё";
         } else if (entry.intValue != -1) {
            ItemStack stack = this.method7717(entry.intValue);
            boolean flag;
            string = (flag = stack.isEmpty() || stack.getItem() == Items.AIR) ? "Пустой слот" : stack.getHoverName().getString();
            text = this.method7709(entry.intValue, flag);
         } else {
            string = "AutoSwap";
            text = "Наведитесь на нужный предмет";
         }

         Font font = Fonts.NEW_SEMIBOLD.getFont(7.5F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float panelWidth = Math.max(font.width(string), font2.width(text)) + 20.0F;
         float floatValue = entry.floatValue - panelWidth / 2.0F;
         float y = entry.floatValue2 - entry.floatValue4 - 38.0F;
         float blurPower;
         if ((blurPower = zenith.getNLMenuScreen().getBlurPower()) > 0.0F) {
            RenderShaders.method5287(
               entry.matrixStack.last().pose(),
               floatValue,
               y,
               panelWidth,
               27.0F,
               blurPower,
               ProxyDotRadiusData.method8069(7.0F),
               ZenithColor.INSTANCE.method7294(entry.floatValue6),
               true,
               false
            );
         }

         entry.menuDrawContext
            .drawRoundedRect(
               floatValue,
               y,
               panelWidth,
               27.0F,
               ProxyDotRadiusData.method8069(7.0F),
               entry.zenithStyle.getLeftBackground().getZenithColor().method7294(entry.floatValue6 * 0.92F)
            );
         entry.menuDrawContext
            .drawText(
               font,
               string,
               entry.floatValue - font.width(string) / 2.0F,
               y + 5.0F,
               entry.zenithStyle.getTextEnable().getZenithColor().method7294(entry.floatValue6)
            );
         entry.menuDrawContext
            .drawText(
               font2,
               text,
               entry.floatValue - font2.width(text) / 2.0F,
               y + 16.0F,
               entry.zenithStyle.getTextSecondary().getZenithColor().method7294(entry.floatValue6)
            );
      }
   }

   private String method7709(int intValue, boolean flag) {
      if (flag) {
         return "ЛКМ привязать";
      } else {
         return this.method7714(intValue) ? "Нет в инвентаре" : "ПКМ очистить";
      }
   }

   private void method7710(Slot slot) {
      if (slot != null) {
         if (KeyBindUtils.method2630(AutoTotemModule.class) && KeyBindUtils.method2630(AutoSwapModule.class)) {
            KeyBindUtils.method2627(AutoSwapModule.class, () -> {
               if (KeyBindUtils.method2630(AutoTotemModule.class)) {
                  InventoryUtils.method900(slot, InteractionHand.OFF_HAND, true);
               }
            });
            OffHandManagerModule.INSTANCE.reset();
         }
      }
   }

   private Slot method7711() {
      return this.keyToSwapCerb.getKeyCode() == -1
         ? null
         : InventoryUtils.method907(
            Items.PLAYER_HEAD,
            slot -> {
               if (slot.getItem().isEmpty()) {
                  return false;
               } else {
                  CustomData customData = slot.getItem().get(DataComponents.CUSTOM_DATA);
                  return customData != null
                     && customData.copyTag().contains("SkullOwner")
                     && customData.copyTag()
                        .get("SkullOwner")
                        .toString()
                        .contains(
                           "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0="
                        );
               }
            }
         );
   }

   private Item method7712(ModeSetting itemType) {
      return switch (itemType.getIndex()) {
         case 0 -> Items.PLAYER_HEAD;
         case 1 -> Items.TOTEM_OF_UNDYING;
         case 2 -> Items.GOLDEN_APPLE;
         case 3 -> Items.SHIELD;
         default -> Items.AIR;
      };
   }

   private void method7713(int intValue) {
      if (intValue == -1) {
         this.intValue4 = -1;
      } else if (intValue != this.intValue4) {
         ItemStack itemStack = this.method7717(intValue);
         if (itemStack.isEmpty() || itemStack.getItem() == Items.AIR) {
            this.intValue4 = -1;
         } else if (this.method7721(mc.player.getOffhandItem(), itemStack)) {
            this.intValue4 = intValue;
         } else {
            Slot slot = this.method7715(itemStack);
            if (slot == null) {
               this.intValue4 = intValue;
            } else {
               this.method7710(slot);
               this.intValue4 = intValue;
            }
         }
      }
   }

   private boolean method7714(int intValue) {
      ItemStack itemStack = this.method7717(intValue);
      return !this.method7718(itemStack) && this.method7715(itemStack) == null && !this.method7722(itemStack);
   }

   private Slot method7715(ItemStack itemStack) {
      return this.method7718(itemStack)
         ? null
         : InventoryUtils.method913(
            itemStack.getItem(),
            Comparator.comparing(slot -> slot.getItem().isEnchanted()),
            slot -> slot.index != 46 && slot.index != 45 && this.method7721(slot.getItem(), itemStack)
         );
   }

   private int method7716(ItemStack itemStack) {
      if (!this.method7718(itemStack) && mc.player != null && mc.player.containerMenu != null) {
         int n = 0;

         for (Slot slot : mc.player.containerMenu.slots) {
            if (slot.index != 46) {
               ItemStack offhandItem = slot.getItem();
               if (this.method7721(offhandItem, itemStack)) {
                  n += offhandItem.getCount();
               }
            }
         }

         return n;
      } else {
         return 0;
      }
   }

   private ItemStack method7717(int intValue) {
      AutoSwapModule.Entry2 entry2 = this.method7723(intValue);
      return entry2 == null ? ItemStack.EMPTY : entry2.method7734();
   }

   private boolean method7718(ItemStack itemStack) {
      return itemStack == null || itemStack.isEmpty() || itemStack.getItem() == Items.AIR;
   }

   private ItemStack method7719(ItemStack itemStack) {
      ItemStack stack = itemStack.copy();
      stack.setCount(1);
      return stack;
   }

   private ItemStack method7720(String text) {
      if (text != null && !text.isBlank()) {
         Identifier tryParse = Identifier.tryParse(text);
         if (tryParse != null && BuiltInRegistries.ITEM.containsKey(tryParse)) {
            Item item = BuiltInRegistries.ITEM.getValue(tryParse);
            return item == Items.AIR ? ItemStack.EMPTY : item.getDefaultInstance();
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private boolean method7721(ItemStack offhandItem, ItemStack itemStack) {
      if (!offhandItem.isEmpty() && !itemStack.isEmpty()) {
         return itemStack.getItem() != Items.PLAYER_HEAD ? offhandItem.getItem() == itemStack.getItem() : ItemStack.isSameItemSameComponents(offhandItem, itemStack);
      } else {
         return false;
      }
   }

   private boolean method7722(ItemStack itemStack) {
      return this.method7721(mc.player.getOffhandItem(), itemStack);
   }

   private AutoSwapModule.Entry2 method7723(int intValue) {
      return intValue >= 0 && intValue < this.entry2s.length ? this.entry2s[intValue] : null;
   }

   private String method7724(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty() && mc.level != null) {
         try {
            RegistryOps serializationContext = mc.level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
            DataResult encodeStart = ItemStack.CODEC.encodeStart(serializationContext, itemStack);
            return (String)encodeStart.result().map(e -> String.valueOf(e)).orElse(null);
         } catch (Exception ex) {
            return null;
         }
      } else {
         return null;
      }
   }

   private ItemStack method7725(String json) {
      if (json != null && !json.isBlank() && mc.level != null) {
         try {
            JsonElement string = JsonParser.parseString(json);
            RegistryOps serializationContext = mc.level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
            DataResult dataResult = ItemStack.CODEC.decode(serializationContext, string);
            if (dataResult.result().isEmpty()) {
               return ItemStack.EMPTY;
            } else {
               ItemStack stack = (ItemStack)((Pair)dataResult.result().get()).getFirst();
               if (stack.isEmpty()) {
                  return ItemStack.EMPTY;
               } else {
                  stack.setCount(1);
                  return stack;
               }
            }
         } catch (Exception e) {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      JsonArray jsonArray = new JsonArray();

      for (AutoSwapModule.Entry2 entry2 : this.entry2s) {
         jsonArray.add(entry2.save());
      }

      json.add("WheelItems", jsonArray);
      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null && json.has("WheelItems") && json.get("WheelItems").isJsonArray()) {
         for (AutoSwapModule.Entry2 entry2 : this.entry2s) {
            entry2.clear();
         }

         JsonArray jsonArray = json.getAsJsonArray("WheelItems");

         for (int i = 0; i < this.entry2s.length && i < jsonArray.size(); i++) {
            this.entry2s[i].method7735(jsonArray.get(i));
         }
      }
   }

   private int method7726(float mouseX, float mouseY, float floatValue, float f, float f2) {
      float x = mouseX - floatValue;
      float y = mouseY - f;
      if ((float)Math.sqrt(x * x + y * y) < f2 * 0.35F) {
         return -1;
      } else {
         double d;
         if ((d = Math.atan2(y, x) + (Math.PI / 2)) < 0.0) {
            d += Math.PI * 2;
         }

         int n;
         return (n = (int)Math.floor(d / (Math.PI * 2) * 3.0)) >= 0 && n < 3 ? n : -1;
      }
   }

   private boolean method7727(float mouseX, float mouseY, float floatValue, float f) {
      float f2 = mouseX - floatValue;
      float f3 = mouseY - f;
      return Math.sqrt(f2 * f2 + f3 * f3) <= 21.0;
   }

   private float method7728(int intValue) {
      return (float)((-Math.PI / 2) + (Math.PI * 2) * ((intValue + 0.5) / 3.0));
   }

   private float method7729() {
      return (float)(mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
   }

   private float method7730() {
      return (float)(mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
   }

   private void method7731(boolean flag) {
      if (mc != null && mc.mouseHandler != null) {
         if (flag) {
            if (!this.flag2) {
               mc.mouseHandler.releaseMouse();
               this.flag2 = true;
            }
         } else {
            if (this.flag2) {
               if (mc.gui.screen() == null) {
                  mc.mouseHandler.grabMouse();
               }

               this.flag2 = false;
            }
         }
      }
   }

   private void method7732() {
      this.flag = false;
      this.intValue2 = -1;
      this.intValue3 = -1;
      this.intValue4 = -1;
      this.longValue2 = 0L;
      this.method7731(false);
   }

   private static final class Entry {
      private final MenuDrawContext menuDrawContext;
      private final PoseStack matrixStack;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final int intValue;
      private final boolean flag;
      private final ZenithStyle zenithStyle;

      private Entry(
         MenuDrawContext menuDrawContext,
         PoseStack matrixStack,
         float floatValue,
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         int intValue,
         boolean flag,
         ZenithStyle zenithStyle
      ) {
         this.menuDrawContext = menuDrawContext;
         this.matrixStack = matrixStack;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.intValue = intValue;
         this.flag = flag;
         this.zenithStyle = zenithStyle;
      }
   }

   private final class Entry2 {
      private String text = "minecraft:air";
      private String text2;
      private ItemStack itemStack;

      private void clear() {
         this.text = "minecraft:air";
         this.text2 = null;
         this.itemStack = null;
      }

      private void method7733(ItemStack itemStack) {
         if (AutoSwapModule.this.method7718(itemStack)) {
            this.clear();
         } else {
            ItemStack stack = AutoSwapModule.this.method7719(itemStack);
            this.text = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            this.text2 = AutoSwapModule.this.method7724(stack);
            this.itemStack = stack;
         }
      }

      private ItemStack method7734() {
         if (this.itemStack != null) {
            return this.itemStack;
         } else {
            ItemStack itemStack = AutoSwapModule.this.method7725(this.text2);
            if (itemStack.isEmpty()) {
               itemStack = AutoSwapModule.this.method7720(this.text);
            }

            this.itemStack = itemStack;
            return itemStack;
         }
      }

      private JsonObject save() {
         JsonObject json = new JsonObject();
         json.addProperty("item", this.text == null ? "minecraft:air" : this.text);
         String str = this.text2;
         if ((str == null || str.isBlank()) && this.itemStack != null && !this.itemStack.isEmpty()) {
            str = AutoSwapModule.this.method7724(this.itemStack);
         }

         if (str != null && !str.isBlank()) {
            json.addProperty("stack", str);
         }

         return json;
      }

      private void method7735(JsonElement jsonElement) {
         this.clear();
         if (jsonElement != null) {
            if (jsonElement.isJsonPrimitive()) {
               this.text = jsonElement.getAsString();
            } else if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               this.text = jsonObject.has("item") ? jsonObject.get("item").getAsString() : "minecraft:air";
               this.text2 = jsonObject.has("stack") ? jsonObject.get("stack").getAsString() : null;
               ItemStack itemStack = AutoSwapModule.this.method7725(this.text2);
               if (!itemStack.isEmpty()) {
                  this.itemStack = itemStack;
                  this.text = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
               }
            }
         }
      }
   }

   private static final class Entry3 {
      private final ZenithColor zenithColor;
      private final ZenithColor zenithColor2;

      private Entry3(ZenithColor zenithColor, ZenithColor zenithColor2) {
         this.zenithColor = zenithColor;
         this.zenithColor2 = zenithColor2;
      }
   }
}
