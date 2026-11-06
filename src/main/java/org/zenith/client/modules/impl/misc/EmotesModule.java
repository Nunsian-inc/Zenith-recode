package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.client.CameraType;
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
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.screens.emote.EmoteFavoriteScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.EmoteAccess;
import org.zenith.utility.misc.EmoteInfo;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "Emotes",
   category = ModuleCategory.MISC,
   description = "module.emotes.description"
)
public final class EmotesModule extends Module {
   public static final EmotesModule INSTANCE = new EmotesModule();
   private static final int CONST_intValue = 8;
   private static final int CONST_intValue2 = 15;
   private static final float CONST_floatValue = 54.0F;
   private static final float CONST_floatValue2 = 104.0F;
   private static final float CONST_floatValue3 = 2.4F;
   private final BindSetting wheelBind = new BindSetting("module.emotes.wheelBind", "module.emotes.wheelBind.desc", -1);
   private final BooleanSetting autoF5 = new BooleanSetting("module.emotes.autoF5", "module.emotes.autoF5.desc", false);
   private final SliderSetting autoF5Duration = new SliderSetting(
      "module.emotes.autoF5Duration", 5.0F, 1.0F, 30.0F, 0.5F, "module.emotes.autoF5Duration.desc", "s"
   );
   private final String[] texts = new String[120];
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private long longValue;
   private int intValue;
   private int hovered = -1;
   private final Animation animation = new Animation(280L, 0.0F, Easing.entry22);
   private final Animation[] animations = new Animation[8];

   private EmotesModule() {
      this.autoF5Duration.setVisible(this.autoF5::isEnabled);

      for (int i = 0; i < this.animations.length; i++) {
         this.animations[i] = new Animation(160L, 0.0F, Easing.INSTANCE18);
      }
   }

   @Override
   public void onDisable() {
      this.method6888(false);
      this.method6890();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.flag3 && (!this.autoF5.isEnabled() || System.nanoTime() >= this.longValue)) {
         this.method6890();
      }
   }

   @EventTarget
   public void method6882(KeyEvent event) {
      if (event.isKey(this.wheelBind.getKeyCode()) && this.wheelBind.getKeyCode() != -1) {
         if (event.getIntValue() == 1) {
            this.method6886();
         } else if (event.getIntValue() == 0) {
            this.method6888(true);
         }
      }
   }

   @EventTarget
   public void method6883(MouseButtonEvent event) {
      if (this.flag && event.getIntValue2() == 1) {
         if (event.getIntValue() == 0 || event.getIntValue() == 1) {
            event.setCancelled(true);
            int hovered;
            if ((hovered = this.method6897(this.method6907(), this.method6908(), this.method6906())) != -1) {
               this.hovered = hovered;
               int favoriteSlot = this.intValue * 8 + hovered;
               EmoteInfo emoteInfo = this.method6900(hovered);
               if (emoteInfo == null) {
                  this.method6902(favoriteSlot);
               } else if (event.getIntValue() == 1) {
                  this.method6904(favoriteSlot);
               } else {
                  this.method6889(emoteInfo);
                  this.method6888(false);
               }
            } else {
               int n;
               if ((n = this.method6898(this.method6907(), this.method6908(), this.method6906())) != 0 && event.getIntValue() == 0) {
                  this.method6899(n, true);
               } else if (event.getIntValue() == 1 && mc.player != null) {
                  EmoteAccess.method7922(mc.player.getUUID());
               }
            }
         }
      }
   }

   @EventTarget
   public void method6884(MouseScrollEvent event) {
      if (this.flag && event.getDoubleValue2() != 0.0) {
         this.method6899(event.getDoubleValue2() < 0.0 ? 1 : -1, false);
         event.setCancelled(true);
      }
   }

   @EventTarget(4)
   public void method6885(ScreenRenderEvent event) {
      if (mc.player != null && mc.level != null && mc.gui.screen() == null) {
         if (this.flag) {
            this.method6909(true);
            EmotesModule.Data data = this.method6906();
            this.hovered = this.method6897(event.getMenuDrawContext().getMouseX(), event.getMenuDrawContext().getMouseY(), data);
            this.method6891(event.getMenuDrawContext(), data);
         }
      } else {
         if (this.flag) {
            this.method6888(false);
         }
      }
   }

   private void method6886() {
      if (mc.player != null && mc.level != null && mc.gui.screen() == null) {
         var emoteInfos = EmoteAccess.method7917();
         if (emoteInfos != null && !emoteInfos.isEmpty()) {
            this.flag = true;
            this.hovered = -1;

            for (Animation animation : this.animations) {
               animation.snapTo(0.0F);
            }

            this.animation.snapTo(0.0F);
            this.animation.animateTo(1.0F);
            this.method6909(true);
            return;
         }
      }
   }

   private void method6887() {
      this.method6888(false);
   }

   private void method6888(boolean flag) {
      if (flag && this.flag) {
         EmoteInfo emoteInfo = this.method6900(this.hovered);
         if (emoteInfo != null) {
            this.method6889(emoteInfo);
         }
      }

      this.flag = false;
      this.hovered = -1;
      this.method6909(false);
   }

   private void method6889(EmoteInfo emoteInfo) {
      EmoteAccess.method7920(emoteInfo.getText());
      if (this.autoF5.isEnabled() && mc.options.getCameraType().isFirstPerson()) {
         this.flag3 = true;
         mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
         this.longValue = System.nanoTime() + (long)(this.autoF5Duration.getValue3() * 1.0E9F);
      }
   }

   private void method6890() {
      if (this.flag3) {
         mc.options.setCameraType(CameraType.FIRST_PERSON);
         this.flag3 = false;
         this.longValue = 0L;
      }
   }

   private void method6891(MenuDrawContext menuDrawContext, EmotesModule.Data data) {
      ZenithStyle currentStyle = zenith.getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         float previewAlpha = Math.clamp(this.animation.animateTo(1.0F), 0.0F, 1.0F);
         float x = 0.82F + 0.18F * previewAlpha;
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(data.floatValue, data.floatValue2);
         menuDrawContext.pose().scale(x, x);
         menuDrawContext.pose().translate(-data.floatValue, -data.floatValue2);
         this.method6892(menuDrawContext, data, previewAlpha);
         this.method6893(menuDrawContext, data, currentStyle, previewAlpha);
         this.method6894(menuDrawContext, data, currentStyle, previewAlpha);
         this.method6895(menuDrawContext, data, currentStyle, previewAlpha);
         menuDrawContext.pose().popMatrix();
         this.method6896(menuDrawContext, data, currentStyle, previewAlpha);
      }
   }

   private void method6892(MenuDrawContext menuDrawContext, EmotesModule.Data data, float previewAlpha) {
      float blurPower;
      if (!((blurPower = zenith.getNLMenuScreen().getBlurPower()) <= 0.0F)) {
         float intValue = data.floatValue4 + 7.0F;
         RenderShaders.method5287(
            MatrixUtils.method664(menuDrawContext.pose()),
            data.floatValue - intValue,
            data.floatValue2 - intValue,
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

   private void method6893(MenuDrawContext menuDrawContext, EmotesModule.Data data, ZenithStyle currentStyle, float previewAlpha) {
      float f = 45.0F;
      float f2 = 45.0F - 2.4F;
      ZenithColor zenithColor = currentStyle.getSurfaceDisableBackground().getZenithColor().method7294(previewAlpha);
      ZenithColor zenithColor2 = currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha * 0.92F);
      ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);

      for (int i = 0; i < 8; i++) {
         float hover = this.animations[i].animateTo(this.hovered == i ? 1.0F : 0.0F);
         float f3 = data.floatValue4 + hover * 6.0F;
         ZenithColor zenithColor4 = zenithColor.method7295(currentStyle.getSurfaceEnableBackground().getZenithColor().method7294(previewAlpha), hover);
         ZenithColor zenithColor22 = zenithColor2.method7295(zenithColor3.method7296(0.48F), hover * 0.86F);
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(data.floatValue, data.floatValue2);
         menuDrawContext.pose().rotate((float)Math.toRadians(i * f + 1.2F));
         menuDrawContext.pose().translate(-data.floatValue, -data.floatValue2);
         RenderShaders.method5276(
            MatrixUtils.method664(menuDrawContext.pose()),
            data.floatValue - f3,
            data.floatValue2 - f3,
            f3 * 2.0F,
            f3 * 2.0F,
            f3 - data.floatValue3,
            f2,
            0.0F,
            zenithColor4,
            zenithColor22,
            zenithColor22,
            zenithColor4
         );
         if (hover > 0.01F) {
            RenderShaders.method5275(
               MatrixUtils.method664(menuDrawContext.pose()),
               data.floatValue - f3,
               data.floatValue2 - f3,
               f3 * 2.0F,
               f3 * 2.0F,
               1.35F + hover,
               f2,
               0.0F,
               zenithColor3.method7294(hover * 0.8F)
            );
         }

         menuDrawContext.pose().popMatrix();
      }
   }

   private void method6894(MenuDrawContext menuDrawContext, EmotesModule.Data data, ZenithStyle currentStyle, float previewAlpha) {
      float f = (data.floatValue3 + data.floatValue4) / 2.0F;

      for (int i = 0; i < 8; i++) {
         EmoteInfo emoteInfo = this.method6900(i);
         float f2 = this.animations[i].getValue();
         float f3 = method6910(i);
         float f4 = f + f2 * 4.0F;
         float f5 = data.floatValue + (float)Math.cos(f3) * f4;
         float f6 = data.floatValue2 + (float)Math.sin(f3) * f4;
         float width = 36.0F + f2 * 4.0F;
         ZenithColor zenithColor = currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha * (0.72F + f2 * 0.22F));
         menuDrawContext.drawRoundedRect(f5 - width / 2.0F, f6 - width / 2.0F, width, width, ProxyDotRadiusData.method8069(9.0F + f2 * 2.0F), zenithColor);
         if (emoteInfo != null) {
            float guiScaledWidth = 31.0F + f2 * 4.0F;
            menuDrawContext.drawTexture(emoteInfo.getIdentifier(), f5 - guiScaledWidth / 2.0F, f6 - guiScaledWidth / 2.0F, guiScaledWidth, guiScaledWidth, ZenithColor.INSTANCE.method7294(previewAlpha));
         } else {
            float f7 = 5.0F + f2 * 1.5F;
            ZenithColor zenithColor2 = currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * (0.62F + f2 * 0.38F));
            menuDrawContext.drawRoundedRect(f5 - f7, f6 - 0.75F, f7 * 2.0F, 1.5F, ProxyDotRadiusData.method8069(0.75F), zenithColor2);
            menuDrawContext.drawRoundedRect(f5 - 0.75F, f6 - f7, 1.5F, f7 * 2.0F, ProxyDotRadiusData.method8069(0.75F), zenithColor2);
         }
      }
   }

   private void method6895(MenuDrawContext menuDrawContext, EmotesModule.Data data, ZenithStyle currentStyle, float previewAlpha) {
      float intValue = 36.0F;
      ZenithColor zenithColor = currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha * 0.97F);
      ZenithColor zenithColor2 = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
      ZenithColor zenithColor3 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
      menuDrawContext.drawRoundedRect(data.floatValue - intValue, data.floatValue2 - intValue, intValue * 2.0F, intValue * 2.0F, ProxyDotRadiusData.method8069(intValue), zenithColor);
      Font font = Fonts.NEW_SEMIBOLD.getFont(9.0F);
      String string = Integer.toString(this.intValue + 1);
      menuDrawContext.drawText(font, string, data.floatValue - font.width(string) / 2.0F, data.floatValue2 - font.height() / 2.0F - 3.0F, zenithColor2);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
      String text = "СТРАНИЦА";
      menuDrawContext.drawText(font2, text, data.floatValue - font2.width(text) / 2.0F, data.floatValue2 + 9.0F, zenithColor3);
      Font font3 = Fonts.NEW_SEMIBOLD.getFont(7.0F);
      ZenithColor zenithColor4 = this.intValue > 0 ? zenithColor2 : zenithColor3.method7294(0.45F);
      ZenithColor zenithColor5 = this.intValue < 14 ? zenithColor2 : zenithColor3.method7294(0.45F);
      menuDrawContext.drawText(font3, "‹", data.floatValue - 24.0F - font3.width("‹") / 2.0F, data.floatValue2 - font3.height() / 2.0F, zenithColor4);
      menuDrawContext.drawText(font3, "›", data.floatValue + 24.0F - font3.width("›") / 2.0F, data.floatValue2 - font3.height() / 2.0F, zenithColor5);
   }

   private void method6896(MenuDrawContext menuDrawContext, EmotesModule.Data data, ZenithStyle currentStyle, float previewAlpha) {
      EmoteInfo emoteInfo = this.method6900(this.hovered);
      String text = this.hovered == -1 ? "Колесо эмоций" : (emoteInfo == null ? "Пустой слот" : emoteInfo.getText2());
      String str = this.hovered == -1
         ? "Колесо мыши — сменить страницу"
         : (emoteInfo == null ? "ЛКМ — выбрать эмоцию" : "Отпустите бинт — воспроизвести  •  ПКМ — удалить");
      Font font = Fonts.NEW_SEMIBOLD.getFont(7.5F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float panelWidth = Math.max(font.width(text), font2.width(str)) + 20.0F;
      float floatValue = data.floatValue - panelWidth / 2.0F;
      float y = data.floatValue2 - data.floatValue4 - 38.0F;
      ZenithColor zenithColor = currentStyle.getLeftBackground().getZenithColor().method7294(previewAlpha * 0.92F);
      float blurPower;
      if ((blurPower = zenith.getNLMenuScreen().getBlurPower()) > 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(menuDrawContext.pose()),
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

      menuDrawContext.drawRoundedRect(floatValue, y, panelWidth, 27.0F, ProxyDotRadiusData.method8069(7.0F), zenithColor);
      menuDrawContext.drawText(font, text, data.floatValue - font.width(text) / 2.0F, y + 5.0F, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      menuDrawContext.drawText(font2, str, data.floatValue - font2.width(str) / 2.0F, y + 16.0F, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
   }

   private int method6897(float mouseX, float mouseY, EmotesModule.Data data) {
      float x = mouseX - data.floatValue;
      float y = mouseY - data.floatValue2;
      double d;
      if (!((d = Math.sqrt(x * x + y * y)) < data.floatValue3) && !(d > data.floatValue4 + 7.0F)) {
         double d2;
         if ((d2 = Math.atan2(y, x) + (Math.PI / 2)) < 0.0) {
            d2 += Math.PI * 2;
         }

         return (int)Math.floor(d2 / (Math.PI * 2) * 8.0);
      } else {
         return -1;
      }
   }

   private int method6898(float f, float f2, EmotesModule.Data data) {
      if (f2 < data.floatValue2 - 11.0F || f2 > data.floatValue2 + 11.0F) {
         return 0;
      } else if (f >= data.floatValue - 33.0F && f <= data.floatValue - 15.0F) {
         return -1;
      } else {
         return f >= data.floatValue + 15.0F && f <= data.floatValue + 33.0F ? 1 : 0;
      }
   }

   private void method6899(int n, boolean flag) {
      if (n != 0) {
         int x = this.intValue + Integer.signum(n);
         this.intValue = flag ? Math.floorMod(x, 15) : Math.clamp((long)x, 0, 14);
         this.hovered = -1;
      }
   }

   private EmoteInfo method6900(int hovered) {
      if (hovered >= 0 && hovered < 8) {
         String text = this.texts[this.intValue * 8 + hovered];
         return text == null ? null : EmoteAccess.method7918(text).orElse(null);
      } else {
         return null;
      }
   }

   private int method6901(int n) {
      return this.intValue * 8 + n;
   }

   private void method6902(int favoriteSlot) {
      this.method6887();
      mc.gui.setScreen(new EmoteFavoriteScreen(this, favoriteSlot));
   }

   public void method6903(int favoriteSlot, String text) {
      if (favoriteSlot >= 0 && favoriteSlot < this.texts.length && !EmoteAccess.method7916().stream().noneMatch(text::equals)) {
         for (int i = 0; i < this.texts.length; i++) {
            if (text.equals(this.texts[i])) {
               this.texts[i] = null;
            }
         }

         this.texts[favoriteSlot] = text;
      }
   }

   public void method6904(int favoriteSlot) {
      if (favoriteSlot >= 0 && favoriteSlot < this.texts.length) {
         this.texts[favoriteSlot] = null;
      }
   }

   public String method6905(int favoriteSlot) {
      return favoriteSlot >= 0 && favoriteSlot < this.texts.length ? this.texts[favoriteSlot] : null;
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      JsonArray jsonArray = new JsonArray();

      for (String text : this.texts) {
         jsonArray.add((JsonElement)(text == null ? JsonNull.INSTANCE : new JsonPrimitive(text)));
      }

      json.add("FavoriteEmotes", jsonArray);
      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null && json.has("FavoriteEmotes") && json.get("FavoriteEmotes").isJsonArray()) {
         JsonArray jsonArray = json.getAsJsonArray("FavoriteEmotes");
         Arrays.fill(this.texts, null);

         for (int i = 0; i < jsonArray.size() && i < this.texts.length; i++) {
            JsonElement jsonElement = jsonArray.get(i);
            if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
               String string = jsonElement.getAsString();
               if (EmoteAccess.method7919(string)) {
                  this.texts[i] = string;
               }
            }
         }
      }
   }

   private EmotesModule.Data method6906() {
      float f = Math.min(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
      float f2 = Math.min(1.0F, f / 270.0F);
      float floatValue = mc.getWindow().getGuiScaledWidth() / 2.0F;
      float f3 = mc.getWindow().getGuiScaledHeight() / 2.0F;
      return new EmotesModule.Data(floatValue, f3, 54.0F * f2, 104.0F * f2);
   }

   private float method6907() {
      return (float)(mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
   }

   private float method6908() {
      return (float)(mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
   }

   private void method6909(boolean flag) {
      if (mc.mouseHandler != null) {
         if (flag && !this.flag2) {
            mc.mouseHandler.releaseMouse();
            this.flag2 = true;
         } else if (!flag && this.flag2) {
            if (mc.gui.screen() == null) {
               mc.mouseHandler.grabMouse();
            }

            this.flag2 = false;
         }
      }
   }

   private static float method6910(int n) {
      return (float)((-Math.PI / 2) + (Math.PI * 2) * ((n + 0.5) / 8.0));
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + "]";
      }

      private Data(float floatValue, float f, float f2, float f3) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
      }

      public float centerX() {
         return this.floatValue;
      }

      public float centerY() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }
   }
}
