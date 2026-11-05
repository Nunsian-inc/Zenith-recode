package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "PotionThrower",
   category = ModuleCategory.MISC,
   description = "module.potionThrower.description"
)
public final class PotionThrowerModule extends Module {
   private static final PotionThrowerModule.Type[] types = PotionThrowerModule.Type.values();
   public static final PotionThrowerModule INSTANCE = new PotionThrowerModule();
   private static final float CONST_floatValue = 62.0F;
   private static final float CONST_floatValue2 = 98.0F;
   private static final float CONST_floatValue3 = 0.35F;
   private static final float CONST_floatValue4 = 2.4F;
   private final ModeSetting mode = new ModeSetting(
      "module.potionThrower.mode", "module.potionThrower.mode.desc", "module.potionThrower.modeWheel", "module.potionThrower.modeKeys"
   );
   private final BindSetting wheelBind = new BindSetting(
      "module.potionThrower.wheelBind", "module.potionThrower.wheelBind.desc", -1, () -> this.mode.isSelected(0)
   );
   private final BooleanSetting drawRadius = new BooleanSetting("module.potionThrower.drawRadius", "module.potionThrower.drawRadius.desc", true);
   private final List<PotionThrowerModule.Entry> items = new ArrayList<>();
   private final Animation animation = new Animation(280L, Easing.entry22);
   private final Animation[] animations = new Animation[types.length];
   private final Slot[] slots = new Slot[types.length];
   private final int[] intValues = new int[types.length];
   private boolean flag;
   private boolean flag2;
   private int intValue = -1;

   private PotionThrowerModule() {
      for (PotionThrowerModule.Type type : types) {
         this.items.add(new PotionThrowerModule.Entry(type, new BindSetting(type.text, type.text + ".desc", -1, () -> this.mode.isSelected(1))));
      }

      for (int i = 0; i < this.animations.length; i++) {
         this.animations[i] = new Animation(160L, Easing.INSTANCE18);
      }
   }

   @Override
   public List<Setting> getSettings() {
      var settings = new ArrayList<>(List.of(this.mode, this.wheelBind, this.drawRadius));
      this.items.forEach(var1x -> settings.add(var1x.bindSetting));
      return settings;
   }

   @Override
   public void onDisable() {
      this.method2695();
      this.items.forEach(entry -> entry.flag = false);
      super.onDisable();
   }

   @EventTarget
   public void method2677(KeyEvent event) {
      if (mc.player != null && mc.player.containerMenu instanceof InventoryMenu) {
         if (this.mode.isSelected(0)) {
            this.method2678(event);
         } else if (mc.gui.screen() == null) {
            for (PotionThrowerModule.Entry entry : this.items) {
               if (entry.bindSetting.getKeyCode() != -1) {
                  if (event.isPress(entry.bindSetting.getKeyCode())) {
                     entry.flag = true;
                  }

                  if (event.isRelease(entry.bindSetting.getKeyCode())) {
                     entry.flag = false;
                     this.method2680(entry.type);
                  }
               }
            }
         }
      }
   }

   private void method2678(KeyEvent event) {
      if (this.wheelBind.getKeyCode() != -1 && event.isKey(this.wheelBind.getKeyCode())) {
         if (event.getIntValue() == 1) {
            if (mc.gui.screen() == null) {
               this.flag = true;
               this.method2694(true);
            }
         } else {
            if (event.getIntValue() == 0) {
               int intValue = this.intValue;
               this.method2695();
               if (intValue != -1) {
                  this.method2680(types[intValue]);
               }
            }
         }
      }
   }

   @EventTarget
   public void method2679(MouseButtonEvent event) {
      if (this.mode.isSelected(0) && this.flag && mc.player != null && mc.gui.screen() == null) {
         if (event.getIntValue2() == 1 && event.getIntValue() == 0) {
            int n;
            if ((
                  n = this.method2691(
                     this.method2692(), this.method2693(), mc.getWindow().getGuiScaledWidth() / 2.0F, mc.getWindow().getGuiScaledHeight() / 2.0F, 62.0F
                  )
               )
               != -1) {
               this.method2695();
               this.method2680(types[n]);
            }
         }
      }
   }

   private void method2680(PotionThrowerModule.Type type) {
      if (mc.player != null && this.method2681(type.floatValue)) {
         InventoryUtils.method928(Items.SPLASH_POTION, type.method2700());
      }
   }

   private boolean method2681(float floatValue) {
      return floatValue == 0.0F
         ? true
         : mc.level != null
            && mc.level
               .players()
               .stream()
               .anyMatch(
                  var1x -> var1x != mc.player
                     && !Zenith.getInstance().getFriends().method7627(var1x.getGameProfile().name())
                     && mc.player.distanceTo(var1x) <= floatValue
               );
   }

   @EventTarget(4)
   public void method2682(ScreenRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.mode.isSelected(0)) {
            if (mc.gui.screen() == null) {
               this.method2683(event.getMenuDrawContext());
            }
         } else {
            if (this.flag || this.flag2) {
               this.method2695();
            }
         }
      }
   }

   private void method2683(MenuDrawContext menuDrawContext) {
      float previewAlpha;
      if ((previewAlpha = this.animation.method2808(this.flag ? 1.0F : 0.0F)) <= 0.005F && !this.flag) {
         for (Animation animation : this.animations) {
            animation.method2809(0.0F);
         }

         this.intValue = -1;
      } else {
         if (this.flag) {
            this.method2694(true);
         }

         ZenithStyle currentStyle = zenith.getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            float xo = mc.getWindow().getGuiScaledWidth() / 2.0F;
            float yo = mc.getWindow().getGuiScaledHeight() / 2.0F;
            float f = 0.85F + 0.15F * previewAlpha;
            float f2 = 62.0F * f;
            float f3 = 98.0F * f;
            int n = this.flag ? this.method2691(menuDrawContext.getMouseX(), menuDrawContext.getMouseY(), xo, yo, f2) : -1;
            this.intValue = this.flag ? n : -1;
            this.method2684();
            PoseStack poseStack = MatrixUtils.method666(menuDrawContext.pose());
            this.method2685(poseStack, xo, yo, f3, previewAlpha);
            this.method2686(poseStack, currentStyle, xo, yo, f2, f3, previewAlpha, n);
            this.method2687(menuDrawContext, currentStyle, xo, yo, f2, f3, previewAlpha);
            this.method2688(menuDrawContext, currentStyle, xo, yo, previewAlpha, f);
            this.method2690(menuDrawContext, poseStack, currentStyle, xo, yo, f3, previewAlpha, n);
         }
      }
   }

   private void method2684() {
      Arrays.fill(this.slots, null);
      Arrays.fill(this.intValues, 0);

      for (Slot slot : mc.player.containerMenu.slots) {
         ItemStack stack = slot.getItem();
         if (!stack.isEmpty()) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
               String string = customData.copyTag().toString();
               boolean flag = stack.getItem().equals(Items.SPLASH_POTION);

               for (int i = 0; i < types.length; i++) {
                  if (string.contains(types[i].text2)) {
                     if (slot.index != 46) {
                        this.intValues[i] = this.intValues[i] + stack.getCount();
                     }

                     if (flag && (this.slots[i] == null || slot.index > this.slots[i].index)) {
                        this.slots[i] = slot;
                     }
                  }
               }
            }
         }
      }
   }

   private void method2685(PoseStack poseStack, float xo, float yo, float f, float previewAlpha) {
      float blurPower;
      if (!((blurPower = zenith.getNLMenuScreen().getBlurPower()) <= 0.0F)) {
         float intValue = f + 7.0F;
         RenderShaders.method5287(
            poseStack.last().pose(),
            xo - intValue,
            yo - intValue,
            intValue * 2.0F,
            intValue * 2.0F,
            blurPower,
            ProxyDotRadiusData.method8069(intValue),
            ZenithColor.INSTANCE.method7294(previewAlpha * 0.92F),
            true,
            false
         );
      }
   }

   private void method2686(PoseStack poseStack, ZenithStyle currentStyle, float xo, float yo, float f, float f2, float previewAlpha, int n) {
      float f3;
      float f4 = (f3 = 360.0F / types.length) - 2.4F;

      for (int i = 0; i < types.length; i++) {
         float hover = this.animations[i].method2808(i == n ? 1.0F : 0.0F);
         boolean flag = this.slots[i] == null;
         float f5 = f2 + hover * 6.0F;
         ZenithColor zenithColor;
         ZenithColor zenithColor2;
         if (flag) {
            zenithColor = currentStyle.getDisableActiveBg()
               .getZenithColor()
               .method7294(previewAlpha)
               .method7295(currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha), hover * 0.45F);
            zenithColor2 = currentStyle.getRightBackground()
               .getZenithColor()
               .method7294(previewAlpha * 0.9F)
               .method7295(currentStyle.getPrimaryColor().getZenithColor().method7296(0.55F).method7294(previewAlpha), hover * 0.4F);
         } else {
            zenithColor = currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha);
            zenithColor2 = currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha * 0.92F);
            if (hover > 0.001F) {
               ZenithColor zenithColor3 = currentStyle.getSurfaceEnableBackground()
                  .getZenithColor()
                  .method7294(previewAlpha)
                  .method7295(currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha), hover * 0.65F);
               ZenithColor zenithColor4 = currentStyle.getPrimaryColor().getZenithColor().method7296(0.48F).method7294(previewAlpha);
               zenithColor = zenithColor.method7295(zenithColor3, hover);
               zenithColor2 = zenithColor2.method7295(zenithColor4, hover);
            }
         }

         poseStack.pushPose();
         poseStack.translate(xo, yo, 0.0F);
         poseStack.rotate(Axis.ZP.rotationDegrees(i * f3 + 1.2F));
         poseStack.translate(-xo, -yo, 0.0F);
         RenderShaders.method5276(
            poseStack.last().pose(), xo - f5, yo - f5, f5 * 2.0F, f5 * 2.0F, f5 - f, f4, 0.0F, zenithColor, zenithColor2, zenithColor2, zenithColor
         );
         if (hover > 0.01F) {
            RenderShaders.method5275(
               poseStack.last().pose(),
               xo - f5,
               yo - f5,
               f5 * 2.0F,
               f5 * 2.0F,
               1.35F + hover,
               f4,
               0.0F,
               currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * hover * 0.8F)
            );
         }

         poseStack.popPose();
      }
   }

   private void method2687(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float xo, float yo, float f, float f2, float previewAlpha) {
      for (int i = 0; i < 2; i++) {
         if (i == 1) {
            ScissorFlusher.flush();
         }

         for (int j = 0; j < types.length; j++) {
            PotionThrowerModule.Type type = types[j];
            float f3 = this.animations[j].getValue();
            float f4 = (float)((-Math.PI / 2) + (Math.PI * 2) * ((j + 0.5) / types.length));
            float f5 = (f + f2) / 2.0F + f3 * 4.0F;
            float f6 = xo + (float)Math.cos(f4) * f5;
            float f7 = yo + (float)Math.sin(f4) * f5;
            if (i == 0) {
               Slot slot = this.slots[j];
               ItemStack mainHandItem = slot == null ? type.method2699() : slot.getItem();
               this.method2689(menuDrawContext, mainHandItem, f6 - 8.0F, f7 - 8.0F, slot == null ? previewAlpha * 0.45F : previewAlpha);
            } else {
               int intValue;
               if ((intValue = this.intValues[j]) > 1) {
                  Font font = Fonts.NEW_SEMIBOLD.getFont(6.0F);
                  String text = String.valueOf(intValue);
                  float floatValue = f6 + 8.0F - font.width(text);
                  float centeredTextY = f7 + 5.5F;
                  menuDrawContext.drawText(font, text, floatValue + 0.5F, centeredTextY + 0.5F, currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha));
                  menuDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               }
            }
         }
      }
   }

   private void method2688(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float xo, float yo, float previewAlpha, float f) {
      float intValue = 32.0F * f;
      menuDrawContext.drawRoundedRect(
         xo - intValue,
         yo - intValue,
         intValue * 2.0F,
         intValue * 2.0F,
         ProxyDotRadiusData.method8069(intValue),
         currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha * 0.97F)
      );
      ItemStack mainHandItem = mc.player.getMainHandItem();
      if (!mainHandItem.isEmpty() && mainHandItem.getItem() != Items.AIR) {
         this.method2689(menuDrawContext, mainHandItem, xo - 8.0F, yo - 8.0F, previewAlpha);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method2689(MenuDrawContext menuDrawContext, ItemStack mainHandItem, float x, float y, float previewAlpha) {
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, Math.clamp(previewAlpha, 0.0F, 1.0F));

      try {
         menuDrawContext.item(mainHandItem, (int)x, (int)y);
      } catch (Throwable e) {
         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         throw e;
      }

      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void method2690(MenuDrawContext menuDrawContext, PoseStack poseStack, ZenithStyle currentStyle, float xo, float yo, float f, float previewAlpha, int n) {
      if (!(previewAlpha <= 0.5F)) {
         String text;
         String str;
         if (n != -1) {
            PotionThrowerModule.Type type = types[n];
            text = type.method2702();
            if (this.slots[n] == null) {
               str = translate("module.potionThrower.missing");
            } else if (!this.method2681(type.floatValue)) {
               str = translate("module.potionThrower.noTarget");
            } else {
               str = translate("module.potionThrower.release");
            }
         } else {
            text = translate("module.potionThrower.title");
            str = translate("module.potionThrower.hint");
         }

         Font font = Fonts.NEW_SEMIBOLD.getFont(7.5F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float panelWidth = Math.max(font.width(text), font2.width(str)) + 20.0F;
         float floatValue = xo - panelWidth / 2.0F;
         float y = yo - f - 38.0F;
         float blurPower;
         if ((blurPower = zenith.getNLMenuScreen().getBlurPower()) > 0.0F) {
            RenderShaders.method5287(
               poseStack.last().pose(),
               floatValue,
               y,
               panelWidth,
               27.0F,
               blurPower,
               ProxyDotRadiusData.method8069(7.0F),
               ZenithColor.INSTANCE.method7294(previewAlpha),
               true,
               false
            );
         }

         menuDrawContext.drawRoundedRect(
            floatValue, y, panelWidth, 27.0F, ProxyDotRadiusData.method8069(7.0F), currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha * 0.92F)
         );
         menuDrawContext.drawText(font, text, xo - font.width(text) / 2.0F, y + 5.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
         menuDrawContext.drawText(font2, str, xo - font2.width(str) / 2.0F, y + 16.0F, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      }
   }

   private int method2691(float mouseX, float mouseY, float xo, float yo, float f) {
      float x = mouseX - xo;
      float y = mouseY - yo;
      if (Math.sqrt(x * x + y * y) < f * 0.35F) {
         return -1;
      } else {
         double d;
         if ((d = Math.atan2(y, x) + (Math.PI / 2)) < 0.0) {
            d += Math.PI * 2;
         }

         int n;
         return (n = (int)Math.floor(d / (Math.PI * 2) * types.length)) >= 0 && n < types.length ? n : -1;
      }
   }

   private float method2692() {
      return (float)(mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
   }

   private float method2693() {
      return (float)(mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
   }

   private void method2694(boolean flag) {
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

   private void method2695() {
      this.flag = false;
      this.intValue = -1;
      this.method2694(false);
   }

   private static String translate(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   @EventTarget
   public void method2696(WorldRenderEvent event) {
      if (this.drawRadius.isEnabled() && mc.player != null && mc.level != null) {
         if (this.mode.isSelected(0)) {
            if (this.flag && this.intValue != -1) {
               this.method2697(event.getMatrixStack(), types[this.intValue]);
            }
         } else {
            for (PotionThrowerModule.Entry entry : this.items) {
               if (entry.flag) {
                  this.method2697(event.getMatrixStack(), entry.type);
               }
            }
         }
      }
   }

   private void method2697(PoseStack matrixStack, PotionThrowerModule.Type type) {
      if (!(type.floatValue <= 0.0F)) {
         this.method2698(matrixStack, type.floatValue, ColorUtils.CONST_intValue);
      }
   }

   private void method2698(PoseStack matrixStack, float floatValue, int n) {
      float x = mc.player.getBbWidth() / 2.0F;
      int color = this.method2681(floatValue) ? Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287() : n;
      Vec3 vec = MathUtils.method2656(mc.player).add(x, 0.02, x);
      Vec3 vec2 = vec.subtract(mc.getEntityRenderDispatcher().camera.position());
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderState.method1898("core/position_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      int n2 = 0;

      for (byte i = 90; n2 <= i; n2++) {
         Vec3 vec3 = MathUtils.method2653(n2, i, floatValue);
         Vec3 vec4 = MathUtils.method2653(n2 + 1, i, floatValue);
         GlowUtil.method2235(
            matrixStack, begin, vec2.add(vec3), vec2.add(vec3.x, vec3.y + 2.0, vec3.z), ColorUtils.method6439(color, 0.2F), ColorUtils.method6439(color, 0.0F)
         );
         GlowUtil.method2245(vec.add(vec3), vec.add(vec4), color, 2.0F, true);
      }

      n2 = 0;

      for (byte i = 90; n2 <= i; n2++) {
         Vec3 vec3 = MathUtils.method2653(n2, i, floatValue);
         GlowUtil.method2235(
            matrixStack, begin, vec2.add(vec3), vec2.add(vec3.x, vec3.y - 2.0, vec3.z), ColorUtils.method6439(color, 0.2F), ColorUtils.method6439(color, 0.0F)
         );
      }

      ImmediateRenderer.method8015(begin.buildOrThrow());
      RenderState.method1879();
      RenderState.method1882(true);
      RenderState.method1872();
      GL11.glDisable(2881);
   }

   private static final class Entry {
      private final PotionThrowerModule.Type type;
      private final BindSetting bindSetting;
      private boolean flag;

      private Entry(PotionThrowerModule.Type type, BindSetting bindSetting) {
         this.type = type;
         this.bindSetting = bindSetting;
      }
   }

   private static enum Type {
      RADIATION("module.potionThrower.radiation", "potion-radiation", 10.0F, 3329330),
      DROWSINESS("module.potionThrower.drowsiness", "potion-drowsiness", 10.0F, 4737096),
      POPPER("module.potionThrower.popper", "potion-popper", 10.0F, 16711680),
      ASSASSIN("module.potionThrower.assassin", "potion-assassin", 0.0F, 3355443),
      PALADIN("module.potionThrower.paladin", "potion-paladin", 0.0F, 65535),
      RAGE("module.potionThrower.rage", "potion-rage", 0.0F, 10040115),
      HOLY_WATER("module.potionThrower.holyWater", "potion-holy-water", 0.0F, 16777215);

      private final String text;
      private final String text2;
      private final float floatValue;
      private final int intValue;
      private ItemStack itemStack;

      private Type(String text, String str, float floatValue, int intValue) {
         this.text = text;
         this.text2 = str;
         this.floatValue = floatValue;
         this.intValue = intValue;
      }

      private ItemStack method2699() {
         if (this.itemStack == null) {
            ItemStack stack = new ItemStack(Items.SPLASH_POTION);
            stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(this.intValue), List.of(), Optional.empty()));
            this.itemStack = stack;
         }

         return this.itemStack;
      }

      private Predicate<Slot> method2700() {
         return slot -> InventoryUtils.method918(slot.getItem(), this.text2);
      }

      private Slot method2701() {
         return InventoryUtils.method907(Items.SPLASH_POTION, this.method2700());
      }

      private int count() {
         return InventoryUtils.method923(slot -> slot.index != 46 && InventoryUtils.method918(slot.getItem(), this.text2));
      }

      private String method2702() {
         return PotionThrowerModule.translate(this.text);
      }
   }
}
