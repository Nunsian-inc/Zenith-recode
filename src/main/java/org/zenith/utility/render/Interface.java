package org.zenith.utility.render;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gui.screens.ChatScreen;
import org.joml.Vector2f;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.HudQueuedContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.Vec2Utils;

public abstract class Interface implements MinecraftAccess {
   private final String text;
   private static final float CONST_floatValue = 12.0F;
   private static final float CONST_floatValue2 = 0.5F;
   private static final float CONST_floatValue3 = 1.2F;
   private static final float CONST_floatValue4 = 0.8F;
   private static final float CONST_floatValue5 = 1.0F;
   public float floatValue;
   public float floatValue2;
   public float width;
   public float height;
   public float floatValue3;
   public float floatValue4;
   public float floatValue5 = Float.NaN;
   public float floatValue6 = Float.NaN;
   public float floatValue7 = -1.0F;
   public float floatValue8 = -1.0F;
   public final SliderSetting scale = new SliderSetting(
      "module.interface.hudElement.scale", 100.0F, 50.0F, 250.0F, 1.0F, "module.interface.hudElement.scale.desc", "%"
   );
   public final SliderSetting opacity = new SliderSetting(
      "module.interface.hudElement.opacity", 100.0F, 10.0F, 100.0F, 1.0F, "module.interface.hudElement.opacity.desc", "%"
   );
   public final ModeSetting visibility = new ModeSetting(
      "module.interface.hudElement.visibility",
      "module.interface.hudElement.visibility.desc",
      "module.interface.hudElement.visibility.always",
      "module.interface.hudElement.visibility.combat",
      "module.interface.hudElement.visibility.peace"
   );
   public final BooleanSetting hideInScreens = new BooleanSetting(
      "module.interface.hudElement.hideInScreens", "module.interface.hudElement.hideInScreens.desc", false
   );
   public final BooleanSetting lock = new BooleanSetting("module.interface.hudElement.lock", "module.interface.hudElement.lock.desc", false);
   public final ButtonSetting reset = new ButtonSetting("module.interface.hudElement.reset", this::method2063);
   private final Interface.Type type;
   private final float floatValue9;
   private final float floatValue10;
   private float floatValue11 = 1.0F;
   private long lastFrame;
   private static final float CONST_floatValue6 = 8.0F;
   private boolean flag = true;
   private Interface.Type type2 = Interface.Type.TOP_LEFT;
   private float floatValue12 = 0.0F;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 1.0F;
   private float floatValue15;
   private boolean flag2;
   private static final float[] CONST_floatValues = new float[4];
   private static long longValue = Long.MIN_VALUE;
   private static int intValue;
   private static int intValue2;
   private static float floatValue16;
   private static final int[] CONST_intValues = new int[]{3, 2, 0, 1};
   private static final double[] CONST_doubleValues = new double[]{-Math.PI * 3.0 / 4.0, -Math.PI / 4, Math.PI * 3.0 / 4.0, Math.PI / 4};
   private static final float[] CONST_floatValues2 = new float[]{-1.0F, 1.0F, -1.0F, 1.0F};
   private static final float[] CONST_floatValues3 = new float[]{-1.0F, -1.0F, 1.0F, 1.0F};
   private static final ZenithColor zenithColor = new ZenithColor(255, 70, 90);
   private static final ZenithColor zenithColor2 = new ZenithColor(70, 170, 255);

   public void tick() {
   }

   public boolean method2056(MouseButtonEvent event) {
      return false;
   }

   public Interface(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      this.text = text;
      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.floatValue3 = f2;
      this.floatValue4 = f3;
      this.floatValue12 = f4;
      this.floatValue13 = f5;
      this.type2 = type != null ? type : Interface.Type.TOP_LEFT;
      this.type = this.type2;
      this.floatValue9 = f4;
      this.floatValue10 = f5;
   }

   public List<Setting> method2057() {
      return List.of(this.scale, this.opacity, this.visibility, this.hideInScreens, this.lock, this.reset);
   }

   public List<Setting> method2058() {
      var settings = new ArrayList<>(this.getSettings());
      settings.removeAll(this.method2057());
      return settings;
   }

   public boolean method2059() {
      return this.lock.isEnabled();
   }

   public boolean method2060() {
      return !this.visibility.isSelected(0) || this.hideInScreens.isEnabled();
   }

   public boolean method2061() {
      if (this.visibility.isSelected(1) && !InterfaceModule.INSTANCE.method6788()) {
         return false;
      } else {
         return this.visibility.isSelected(2) && InterfaceModule.INSTANCE.method6788()
            ? false
            : !this.hideInScreens.isEnabled() && !InterfaceModule.INSTANCE.method6785()
               || mc.gui.screen() == null
               || mc.gui.screen() instanceof ChatScreen
               || Zenith.getInstance().getNLMenuScreen().isRenderHud();
      }
   }

   private float method2062() {
      long now = System.currentTimeMillis();
      float f = this.lastFrame == 0L ? 0.0F : Math.min(250.0F, (float)(now - this.lastFrame));
      this.lastFrame = now;
      float f2 = this.method2061() ? 1.0F : 0.0F;
      if (!InterfaceModule.INSTANCE.method6783()) {
         this.floatValue11 = f2;
      } else {
         float f3 = f / Math.max(1.0F, InterfaceModule.INSTANCE.method6784());
         this.floatValue11 = f2 > this.floatValue11 ? Math.min(f2, this.floatValue11 + f3) : Math.max(f2, this.floatValue11 - f3);
      }

      return this.floatValue11;
   }

   public void method2063() {
      float guiScaledWidth = this.method2097(this.floatValue3);
      float guiScaledHeight = this.method2098(this.floatValue4);
      if (!(guiScaledWidth <= 0.0F) && !(guiScaledHeight <= 0.0F)) {
         this.type2 = this.type;
         this.floatValue12 = this.floatValue9;
         this.floatValue13 = this.floatValue10;
         float f = this.method2079(this.type, guiScaledWidth) + this.floatValue9;
         float f2 = this.method2080(this.type, guiScaledHeight) + this.floatValue10;
         this.floatValue = this.method2088(f - this.method2064(), guiScaledWidth) + this.method2064();
         this.floatValue2 = this.method2089(f2 - this.method2065(), guiScaledHeight) + this.method2065();
         this.method2094(guiScaledWidth, guiScaledHeight);
         this.method2091(guiScaledWidth, guiScaledHeight);
      }
   }

   public float method290() {
      return Math.max(0.01F, this.scale.getValue3() / 100.0F);
   }

   public float method296() {
      return this.width * this.method290();
   }

   public float method297() {
      return this.height * this.method290();
   }

   public float method294() {
      return this.floatValue - this.method2064();
   }

   public float method295() {
      return this.floatValue2 - this.method2065();
   }

   private float method2064() {
      return (this.method296() - this.width) / 2.0F;
   }

   public float method2065() {
      return (this.method297() - this.height) / 2.0F;
   }

   public void method291(MenuDrawContext menuDrawContext) {
      float f = this.method2062();
      float f2 = this.opacity.getValue3() / 100.0F * f;
      if (this.method2074()) {
         f2 = Math.max(f2, 0.35F);
      } else if (f2 <= 0.01F) {
         return;
      }

      float x = this.method290();
      float alpha = HudQueuedContext.getAlpha();
      HudQueuedContext.setAlpha(f2);

      try {
         this.method2068(menuDrawContext, x);
      } finally {
         HudQueuedContext.setAlpha(alpha);
      }
   }

   public float getFloatValue14() {
      return this.floatValue14;
   }

   public void setFloatValue14(float f) {
      this.floatValue14 = f;
   }

   public void method2066(float f, boolean flag) {
      this.floatValue15 = f;
      this.flag2 = flag;
   }

   public void method2067(float f, float f2, float guiScaledWidth, float guiScaledHeight) {
      this.floatValue = this.method2088(f, guiScaledWidth) + this.method2064();
      this.floatValue2 = this.method2089(f2, guiScaledHeight) + this.method2065();
      this.floatValue3 = guiScaledWidth;
      this.floatValue4 = guiScaledHeight;
      this.method2091(guiScaledWidth, guiScaledHeight);
   }

   private void method2068(MenuDrawContext menuDrawContext, float x) {
      x *= this.floatValue14;
      menuDrawContext.pushMatrix();

      try {
         menuDrawContext.pose().translate(this.floatValue + this.width / 2.0F, this.floatValue2 + this.height / 2.0F);
         menuDrawContext.pose().scale(x, x);
         menuDrawContext.pose().translate(-this.floatValue - this.width / 2.0F, -this.floatValue2 - this.height / 2.0F);
         float width = this.width;
         float height = this.height;
         this.width = 0.0F;
         this.height = 0.0F;
         this.method8(menuDrawContext);
         if (!(this.width <= 0.0F) && !(this.height <= 0.0F)) {
            if (this.floatValue15 > 0.01F) {
               ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
               menuDrawContext.drawRoundedRect(
                  this.floatValue,
                  this.floatValue2,
                  this.width,
                  this.height,
                  ProxyDotRadiusData.method8069(Math.max(0.0F, InterfaceModule.method6801())),
                  zenithColor.method7292((this.flag2 ? 0.12F : 0.06F) * this.floatValue15)
               );
            }

            this.method2069(menuDrawContext);
         } else {
            this.width = width;
            this.height = height;
         }
      } finally {
         menuDrawContext.popMatrix();
      }
   }

   public abstract void method8(CustomDrawContext customDrawContext);

   public void method2069(CustomDrawContext customDrawContext) {
      this.method4679(customDrawContext, this.floatValue, this.floatValue2, this.width, this.height, this.method254());
   }

   public void method4679(CustomDrawContext customDrawContext, float floatValue, float y, float width, float height, float x) {
      float f = this.floatValue15;
      if (InterfaceModule.INSTANCE.method6822() || !(f <= 0.01F)) {
         if (!(width <= 0.0F) && !(height <= 0.0F)) {
            float x2 = Math.clamp(Math.max(x, f), 0.0F, 1.0F);
            if (!(x2 <= 0.01F)) {
               float f2 = floatValue - 1.0F;
               float f3 = y - 1.0F;
               float f4 = width + 2.0F;
               float f5 = height + 2.0F;
               float intValue = Math.max(0.0F, InterfaceModule.method6801()) + 1.0F;
               float width2 = Math.max(12.0F, intValue * 2.0F + 4.0F) + f * (this.flag2 ? 7.0F : 4.0F);
               width2 = Math.min(width2, Math.min(f4, f5) / 2.0F);
               float batchRange = 1.2F + f * (this.flag2 ? 0.9F : 0.5F);
               if (!(width2 <= intValue)) {
                  ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
                  ZenithColor zenithColor2 = InterfaceModule.INSTANCE.method6824();
                  ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(intValue);
                  float[] floats = this.method2071();
                  float f6 = f2 + f4 - width2;
                  float f7 = f3 + f5 - width2;
                  float[] floats2 = new float[]{f2, f6, f2, f6};
                  float[] floats3 = new float[]{f3, f3, f7, f7};
                  customDrawContext.pushMatrix();
                  customDrawContext.pose().translate(floatValue + width / 2.0F, y + height / 2.0F);
                  customDrawContext.pose().scale(x2, x2);
                  customDrawContext.pose().translate(-(floatValue + width / 2.0F), -(y + height / 2.0F));
                  ZenithColor zenithColor3 = zenithColor.method7295(ZenithColor.INSTANCE, 0.8F * (1.0F - f * 0.75F));

                  for (int i = 0; i < 4; i++) {
                     float f8 = floats[i] * (1.0F - f);
                     float previewAlpha = 0.5F * (0.75F + 0.5F * f8) * x2;
                     previewAlpha += (1.0F - previewAlpha) * f * 0.9F;
                     ZenithColor zenithColor4 = zenithColor3.method7295(this.method2072(zenithColor2, i), f8 * 0.7F).method7294(previewAlpha);
                     customDrawContext.drawRoundedCornerOnly(floats2[i], floats3[i], width2, width2, batchRange, proxyDotRadiusData, zenithColor4, i);
                  }

                  customDrawContext.popMatrix();
               }
            }
         }
      }
   }

   private float[] method2071() {
      long now = System.currentTimeMillis();
      int n = InterfaceModule.INSTANCE.method6826();
      int n2 = InterfaceModule.INSTANCE.method6827();
      float f = InterfaceModule.INSTANCE.method6825();
      if (now == longValue && n == intValue && n2 == intValue2 && f == floatValue16) {
         return CONST_floatValues;
      } else {
         float[] floats = method2073(now, n, f);

         for (int i = 0; i < floats.length; i++) {
            float f2 = Math.max(0.0F, Math.min(1.0F, floats[i]));

            floats[i] = switch (n2) {
               case 2 -> Math.min(1.0F, (float)Math.pow(f2, 0.7) * 0.6F + (float)Math.pow(f2, 5.0) * 0.6F);
               case 3 -> {
                  float f3 = Math.max(0.0F, Math.min(1.0F, f2 * (1.0F - f2) * 4.0F));
                  yield f3 * f3;
               }
               case 4 -> {
                  float f3 = 0.5F + 0.5F * (float)Math.cos(f2 * Math.PI * 6.0);
                  yield f2 * (0.3F + 0.7F * f3 * f3);
               }
               case 5 -> f2 * (0.5F + 0.5F * (float)Math.pow(0.5 + 0.5 * Math.sin(now / 400.0 + i * 2.1), 2.0));
               default -> f2;
            };
         }

         longValue = now;
         intValue = n;
         intValue2 = n2;
         floatValue16 = f;
         return floats;
      }
   }

   private ZenithColor method2072(ZenithColor zenithColor3, int n) {
      return switch (InterfaceModule.INSTANCE.method6827()) {
         case 1 -> zenithColor3.method7295(n % 2 == 0 ? zenithColor : zenithColor2, 0.65F);
         case 2, 4 -> zenithColor3.method7295(ZenithColor.INSTANCE, 0.55F);
         default -> zenithColor3;
      };
   }

   private static float[] method2073(long now, int n, float f) {
      float f2 = (float)(now % 1000000L / 1000.0 * f);
      float[] floats = CONST_floatValues;
      Arrays.fill(floats, 0.0F);
      switch (n) {
         case 1:
         case 2:
            float f3 = f2 * 0.5F - (float)Math.floor(f2 * 0.5F);
            float exp = (float)Math.exp(-Math.pow((f3 - 0.25F) * 7.0F, 2.0));
            float exp2 = (float)Math.exp(-Math.pow((f3 - 0.75F) * 7.0F, 2.0));
            floats[0] = exp;
            floats[2] = exp;
            floats[1] = exp2;
            floats[3] = exp2;
            return floats;
         case 3:
            float f4 = (float)(f2 * Math.PI % (Math.PI * 2));
            double[] doubles = CONST_doubleValues;

            for (int i = 0; i < 4; i++) {
               double d = Math.abs(((doubles[i] - f4) % (Math.PI * 2) + (Math.PI * 3)) % (Math.PI * 2) - Math.PI);
               floats[i] = (float)Math.exp(-d * d * 2.5);
            }

            return floats;
         case 4:
         case 7:
         case 8:
         case 9:
         case 10:
         default:
            float f5 = f2 % 4.0F;
            int n2 = (int)f5;
            float f6 = f5 - n2;
            int[] ints = CONST_intValues;
            floats[ints[n2]] = (float)Math.sin(Math.PI * f6);
            floats[ints[(n2 + 1) % 4]] = (float)Math.sin(Math.PI * f6) * 0.25F * f6;
            return floats;
         case 5:
            float f7 = (float)Math.sin(f2 * 1.7F);
            float f8 = (float)Math.cos(f2 * 1.13F);
            float[] floats2 = CONST_floatValues2;
            float[] floats3 = CONST_floatValues3;

            for (int i = 0; i < 4; i++) {
               float f9 = f7 - floats2[i];
               float f10 = f8 - floats3[i];
               floats[i] = (float)Math.exp(-(f9 * f9 + f10 * f10) * 0.9F);
            }

            return floats;
         case 6:
            float f9 = (float)Math.sin(f2 * 0.6F) * 0.35F;
            float exp3 = (float)Math.exp(-Math.pow((-1.0F - f9) * 1.6F, 2.0));
            float exp4 = (float)Math.exp(-Math.pow((1.0F - f9) * 1.6F, 2.0));
            float f10 = 0.75F + 0.25F * (float)Math.sin(f2 * 3.0F);
            floats[0] = exp3 * f10;
            floats[1] = exp3 * f10;
            floats[2] = exp4 * f10;
            floats[3] = exp4 * f10;
            return floats;
         case 11:
            return floats;
      }
   }

   public boolean method2074() {
      return mc.gui.screen() instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud();
   }

   public float method254() {
      return 1.0F;
   }

   public boolean isEnabled() {
      return this.flag;
   }

   public void setEnabled(boolean flag) {
      this.flag = flag;
   }

   public void toggle() {
      this.flag = !this.flag;
   }

   public List<Setting> getSettings() {
      ArrayList<Setting> settings = new ArrayList<>();

      for (Class clazz = this.getClass(); clazz != null && Interface.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
         Arrays.stream(clazz.getDeclaredFields()).forEach(var2x -> {
            try {
               var2x.setAccessible(true);
               if (var2x.get(this) instanceof Setting setting) {
                  settings.add(setting);
               }
            } catch (IllegalAccessException e) {
            }
         });
         if (clazz == Interface.class) {
            break;
         }
      }

      return settings;
   }

   public boolean method293(double floatValue, double d) {
      float f = this.method294();
      float f2 = this.method295();
      return floatValue >= f && floatValue <= f + this.method296() && d >= f2 && d <= f2 + this.method297();
   }

   public boolean method2075(double floatValue, double d) {
      float f = this.method294();
      float f2 = this.method295();
      float f3 = f + this.method296();
      float f4 = f2 + this.method297();
      return floatValue >= f3 - 8.0F && floatValue <= f3 + 2.0F && d >= f4 - 8.0F && d <= f4 + 2.0F;
   }

   public void method2076(CustomDrawContext customDrawContext) {
      float batchRange = 5.5F;
      float intValue = 6.0F;
      ZenithColor zenithColor = new ZenithColor(179, 145, 255, 255);
      customDrawContext.drawRoundedBorder(this.floatValue, this.floatValue2, this.width, this.height, batchRange, ProxyDotRadiusData.method8069(intValue), zenithColor);
   }

   public void method289(CustomDrawContext customDrawContext, float f, float f2, InterfaceModule interfaceModule, float guiScaledWidth, float guiScaledHeight) {
      float f3 = this.method2088(f, guiScaledWidth);
      float f4 = this.method2089(f2, guiScaledHeight);
      float f5 = this.method296();
      float f6 = this.method297();
      Vector2f vec = interfaceModule.method6809(f3, f4);
      Interface.Entry entry = new Interface.Entry(vec.x(), 0.0F);
      Interface.Entry entry2 = new Interface.Entry(vec.y(), 0.0F);
      Vector2f vec2 = interfaceModule.method6809(f3 + f5, f4 + f6);
      Interface.Entry entry3 = new Interface.Entry(vec2.x(), -f5);
      Interface.Entry entry4 = new Interface.Entry(vec2.y(), -f6);
      Vector2f vec3 = interfaceModule.method6809(f3 + f5 / 2.0F, f4 + f6 / 2.0F);
      Interface.Entry entry5 = new Interface.Entry(vec3.x(), -f5 / 2.0F);
      Interface.Entry entry6 = new Interface.Entry(vec3.y(), -f6 / 2.0F);
      float f7 = InterfaceModule.INSTANCE.method6786();
      if (f7 >= 1.0F) {
         f3 = this.method2088(Math.round(f3 / f7) * f7, guiScaledWidth);
         f4 = this.method2089(Math.round(f4 / f7) * f7, guiScaledHeight);
      }

      this.floatValue = f3 + this.method2064();
      this.floatValue2 = f4 + this.method2065();
      this.floatValue3 = guiScaledWidth;
      this.floatValue4 = guiScaledHeight;
      this.method2091(guiScaledWidth, guiScaledHeight);
      Interface.Entry entry7 = this.method2077(entry, entry3, entry5);
      Interface.Entry entry8 = this.method2077(entry2, entry4, entry6);
      this.method285(customDrawContext, entry7);
      this.method286(customDrawContext, entry8);
   }

   private Interface.Entry method2077(Interface.Entry entry, Interface.Entry entry2, Interface.Entry entry3) {
      if (entry.floatValue != -1.0F) {
         return entry;
      } else {
         return entry2.floatValue != -1.0F ? entry2 : entry3;
      }
   }

   public void method286(CustomDrawContext customDrawContext, Interface.Entry entry) {
      if (entry.floatValue == -1.0F) {
         this.floatValue8 = entry.floatValue;
      } else {
         float guiWidth = this.floatValue3 > 0.0F ? this.floatValue3 : customDrawContext.guiWidth();
         customDrawContext.drawRoundedRect(
            this.method2081(guiWidth),
            entry.floatValue,
            this.method2083(guiWidth),
            1.0F,
            ProxyDotRadiusData.INSTANCE,
            Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor()
         );
         this.floatValue8 = entry.floatValue + entry.offset;
      }
   }

   public void method285(CustomDrawContext customDrawContext, Interface.Entry entry) {
      if (entry.floatValue == -1.0F) {
         this.floatValue7 = entry.floatValue;
      } else {
         float guiHeight = this.floatValue4 > 0.0F ? this.floatValue4 : customDrawContext.guiHeight();
         customDrawContext.drawRoundedRect(
            entry.floatValue,
            this.method2084(guiHeight),
            1.0F,
            this.method2086(guiHeight),
            ProxyDotRadiusData.INSTANCE,
            Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor()
         );
         this.floatValue7 = entry.floatValue + entry.offset;
      }
   }

   public void method287(float floatValue, float f) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
      float guiScaledWidth = this.method2097(this.floatValue3);
      float guiScaledHeight = this.method2098(this.floatValue4);
      if (guiScaledWidth > 0.0F && guiScaledHeight > 0.0F) {
         this.update(guiScaledWidth, guiScaledHeight);
         this.method2094(guiScaledWidth, guiScaledHeight);
      }

      this.method2091(guiScaledWidth, guiScaledHeight);
   }

   public void method288(float guiScaledWidth, float guiScaledHeight) {
      if (!(guiScaledHeight <= 0.0F) && !(guiScaledWidth <= 0.0F)) {
         if (!this.method2090()) {
            float f = this.floatValue3 > 0.0F ? this.floatValue3 : guiScaledWidth;
            float guiScaledHeight2 = this.floatValue4 > 0.0F ? this.floatValue4 : guiScaledHeight;
            this.method2091(f, guiScaledHeight2);
         }

         this.floatValue3 = guiScaledWidth;
         this.floatValue4 = guiScaledHeight;
         float f;
         float f2;
         if (this.method2090()) {
            f = this.method2081(guiScaledWidth) + this.floatValue5 * this.method2083(guiScaledWidth) - this.method2092() * this.method296();
            f2 = this.method2084(guiScaledHeight) + this.floatValue6 * this.method2086(guiScaledHeight) - this.method2093() * this.method297();
         } else if (this.type2 != null) {
            float f3 = this.method2079(this.type2, guiScaledWidth) + this.floatValue12;
            float f4 = this.method2080(this.type2, guiScaledHeight) + this.floatValue13;
            f = f3 - this.method2064();
            f2 = f4 - this.method2065();
         } else {
            f = this.method294();
            f2 = this.method295();
         }

         this.floatValue = this.method2088(f, guiScaledWidth) + this.method2064();
         this.floatValue2 = this.method2089(f2, guiScaledHeight) + this.method2065();
      }
   }

   public void update(float guiScaledWidth, float guiScaledHeight) {
      if (!(guiScaledWidth <= 0.0F) && !(guiScaledHeight <= 0.0F)) {
         float f = this.method2088(this.method294(), guiScaledWidth);
         float f2 = this.method2089(this.method295(), guiScaledHeight);
         this.floatValue = f + this.method2064();
         this.floatValue2 = f2 + this.method2065();
      }
   }

   public void release() {
      if (this.floatValue7 != -1.0F) {
         this.floatValue = this.floatValue7 + this.method2064();
      }

      if (this.floatValue8 != -1.0F) {
         this.floatValue2 = this.floatValue8 + this.method2065();
      }

      float guiScaledWidth = this.method2097(this.floatValue3);
      float guiScaledHeight = this.method2098(this.floatValue4);
      if (guiScaledWidth > 0.0F && guiScaledHeight > 0.0F) {
         this.update(guiScaledWidth, guiScaledHeight);
         this.method2094(guiScaledWidth, guiScaledHeight);
      }

      this.floatValue7 = -1.0F;
      this.floatValue8 = -1.0F;
      this.method2091(guiScaledWidth, guiScaledHeight);
   }

   private Interface.Type method2078(float x, float y, float guiScaledWidth, float guiScaledHeight) {
      float f = this.method2081(guiScaledWidth);
      float f2 = this.method2084(guiScaledHeight);
      float f3 = this.method2083(guiScaledWidth);
      float f4 = this.method2086(guiScaledHeight);
      float f5 = x - this.method2064();
      float f6 = y - this.method2065();
      float f7 = f5 + this.method296() / 2.0F;
      float f8 = f6 + this.method297() / 2.0F;
      boolean flag = f7 < f + f3 / 3.0F;
      boolean flag2 = f7 > f + f3 * 2.0F / 3.0F;
      boolean flag3 = !flag && !flag2;
      boolean flag4 = f8 < f2 + f4 / 3.0F;
      boolean flag5 = f8 > f2 + f4 * 2.0F / 3.0F;
      boolean flag6 = !flag4 && !flag5;
      if (flag4) {
         if (flag) {
            return Interface.Type.TOP_LEFT;
         } else {
            return flag3 ? Interface.Type.TOP_CENTER : Interface.Type.TOP_RIGHT;
         }
      } else if (flag6) {
         if (flag) {
            return Interface.Type.CENTER_LEFT;
         } else {
            return flag3 ? Interface.Type.CENTER : Interface.Type.CENTER_RIGHT;
         }
      } else if (flag) {
         return Interface.Type.BOTTOM_LEFT;
      } else {
         return flag3 ? Interface.Type.BOTTOM_CENTER : Interface.Type.BOTTOM_RIGHT;
      }
   }

   private float method2079(Interface.Type type2, float guiScaledWidth) {
      float f = this.method2081(guiScaledWidth);
      float f2 = this.method2082(guiScaledWidth);
      float f3 = f + this.method2083(guiScaledWidth) / 2.0F;
      float f4 = this.method296();

      float f5 = switch (type2) {
         case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> f;
         case TOP_CENTER, CENTER, BOTTOM_CENTER -> f3 - f4 / 2.0F;
         case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> f2 - f4;
      };
      return f5 + this.method2064();
   }

   private float method2080(Interface.Type type2, float guiScaledHeight) {
      float f = this.method2084(guiScaledHeight);
      float f2 = this.method2085(guiScaledHeight);
      float f3 = f + this.method2086(guiScaledHeight) / 2.0F;
      float f4 = this.method297();

      float f5 = switch (type2) {
         case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> f;
         case CENTER_LEFT, CENTER, CENTER_RIGHT -> f3 - f4 / 2.0F;
         case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> f2 - f4;
      };
      return f5 + this.method2065();
   }

   public float method2081(float guiScaledWidth) {
      return Math.min(InterfaceModule.INSTANCE.method6787(), guiScaledWidth / 4.0F);
   }

   public float method2082(float guiScaledWidth) {
      return this.method2081(guiScaledWidth) + this.method2083(guiScaledWidth);
   }

   public float method2083(float guiScaledWidth) {
      return Math.max(1.0F, guiScaledWidth - this.method2081(guiScaledWidth) * 2.0F);
   }

   public float method2084(float guiScaledHeight) {
      return Math.min(InterfaceModule.INSTANCE.method6787(), guiScaledHeight / 4.0F);
   }

   public float method2085(float guiScaledHeight) {
      return this.method2084(guiScaledHeight) + this.method2086(guiScaledHeight);
   }

   public float method2086(float guiScaledHeight) {
      return Math.max(1.0F, guiScaledHeight - this.method2084(guiScaledHeight) * 2.0F);
   }

   public JsonObject save() {
      if (!this.method2090()) {
         this.method2091(this.method2097(this.floatValue3), this.method2098(this.floatValue4));
      }

      JsonObject json = new JsonObject();
      json.addProperty("enable", this.flag);
      json.addProperty("enabled", this.flag);
      json.addProperty("x", this.floatValue);
      json.addProperty("y", this.floatValue2);
      json.addProperty("width", this.width);
      json.addProperty("height", this.height);
      json.addProperty("windowWidth", this.floatValue3);
      json.addProperty("windowHeight", this.floatValue4);
      json.addProperty("offsetX", this.floatValue12);
      json.addProperty("offsetY", this.floatValue13);
      json.addProperty("align", this.type2.name());
      json.addProperty("anchored", true);
      if (Float.isFinite(this.floatValue5)) {
         json.addProperty("relativeX", this.floatValue5);
      }

      if (Float.isFinite(this.floatValue6)) {
         json.addProperty("relativeY", this.floatValue6);
      }

      JsonObject jsonObject = new JsonObject();

      for (Setting setting : this.getSettings()) {
         setting.safe(jsonObject);
      }

      json.add("Settings", jsonObject);
      return json;
   }

   public void load(JsonObject jsonObject) {
      boolean flag = false;
      boolean flag2 = false;
      boolean flag3 = false;
      if (jsonObject.has("enable")) {
         this.flag = jsonObject.get("enable").getAsBoolean();
      } else if (jsonObject.has("enabled")) {
         this.flag = jsonObject.get("enabled").getAsBoolean();
      }

      if (jsonObject.has("x")) {
         this.floatValue = jsonObject.get("x").getAsFloat();
      }

      if (jsonObject.has("y")) {
         this.floatValue2 = jsonObject.get("y").getAsFloat();
      }

      if (jsonObject.has("width")) {
         this.width = jsonObject.get("width").getAsFloat();
      }

      if (jsonObject.has("height")) {
         this.height = jsonObject.get("height").getAsFloat();
      }

      if (jsonObject.has("windowWidth")) {
         this.floatValue3 = jsonObject.get("windowWidth").getAsFloat();
      }

      if (jsonObject.has("windowHeight")) {
         this.floatValue4 = jsonObject.get("windowHeight").getAsFloat();
      }

      if (jsonObject.has("offsetX")) {
         this.floatValue12 = jsonObject.get("offsetX").getAsFloat();
         flag2 = true;
      }

      if (jsonObject.has("offsetY")) {
         this.floatValue13 = jsonObject.get("offsetY").getAsFloat();
         flag3 = true;
      }

      if (jsonObject.has("align")) {
         try {
            this.type2 = Interface.Type.valueOf(jsonObject.get("align").getAsString());
            flag = true;
         } catch (IllegalArgumentException e) {
            this.type2 = Interface.Type.TOP_LEFT;
         }
      }

      if (jsonObject.has("relativeX")) {
         this.floatValue5 = jsonObject.get("relativeX").getAsFloat();
      }

      if (jsonObject.has("relativeY")) {
         this.floatValue6 = jsonObject.get("relativeY").getAsFloat();
      }

      boolean flag4 = this.method2090() && !jsonObject.has("anchored");
      if (jsonObject.has("Settings") && jsonObject.get("Settings").isJsonObject()) {
         JsonObject json = jsonObject.getAsJsonObject("Settings");

         for (Setting setting : this.getSettings()) {
            if (json.has(setting.getKey()) || setting instanceof GroupSetting) {
               setting.load(json);
            }
         }
      }

      if (!flag || !flag2 || !flag3) {
         float guiScaledWidth = this.method2097(this.floatValue3);
         float guiScaledHeight = this.method2098(this.floatValue4);
         this.method2094(guiScaledWidth, guiScaledHeight);
      }

      if (!this.method2090()) {
         this.method2091(this.method2097(this.floatValue3), this.method2098(this.floatValue4));
      }

      if (flag4 && mc != null && mc.getWindow() != null) {
         float guiScaledWidth = mc.getWindow().getGuiScaledWidth();
         float guiScaledHeight = mc.getWindow().getGuiScaledHeight();
         float f = this.method2081(guiScaledWidth) + this.floatValue5 * this.method2083(guiScaledWidth);
         float f2 = this.method2084(guiScaledHeight) + this.floatValue6 * this.method2086(guiScaledHeight);
         this.floatValue = this.method2088(f, guiScaledWidth) + this.method2064();
         this.floatValue2 = this.method2089(f2, guiScaledHeight) + this.method2065();
         this.floatValue3 = guiScaledWidth;
         this.floatValue4 = guiScaledHeight;
         this.method2094(guiScaledWidth, guiScaledHeight);
         this.method2091(guiScaledWidth, guiScaledHeight);
      }

      if (mc != null && mc.getWindow() != null) {
         this.method288(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
      }
   }

   public void method2087(float floatValue, float f) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
      float guiScaledWidth = this.method2097(this.floatValue3);
      float guiScaledHeight = this.method2098(this.floatValue4);
      if (guiScaledWidth > 0.0F && guiScaledHeight > 0.0F) {
         this.update(guiScaledWidth, guiScaledHeight);
         this.method2094(guiScaledWidth, guiScaledHeight);
      }

      this.method2091(guiScaledWidth, guiScaledHeight);
   }

   public float method2088(float f, float guiScaledWidth) {
      float f2 = this.method2081(guiScaledWidth);
      float f3 = this.method2082(guiScaledWidth) - this.method296();
      if (f3 < f2) {
         f3 = f2;
      }

      return Math.max(f2, Math.min(f, f3));
   }

   public float method2089(float f, float guiScaledHeight) {
      float f2 = this.method2084(guiScaledHeight);
      float f3 = this.method2085(guiScaledHeight) - this.method297();
      if (f3 < f2) {
         f3 = f2;
      }

      return Math.max(f2, Math.min(f, f3));
   }

   public boolean method2090() {
      return Float.isFinite(this.floatValue5) && Float.isFinite(this.floatValue6);
   }

   public void method2091(float guiScaledWidth, float guiScaledHeight) {
      if (!(guiScaledWidth <= 0.0F) && !(guiScaledHeight <= 0.0F)) {
         float f = this.method2083(guiScaledWidth);
         float f2 = this.method2086(guiScaledHeight);
         if (!(f <= 0.0F) && !(f2 <= 0.0F)) {
            float f3 = this.method2088(this.method294(), guiScaledWidth) + this.method2092() * this.method296();
            float f4 = this.method2089(this.method295(), guiScaledHeight) + this.method2093() * this.method297();
            float f5 = (f3 - this.method2081(guiScaledWidth)) / f;
            float f6 = (f4 - this.method2084(guiScaledHeight)) / f2;
            this.floatValue5 = Math.max(0.0F, Math.min(1.0F, f5));
            this.floatValue6 = Math.max(0.0F, Math.min(1.0F, f6));
         }
      }
   }

   private float method2092() {
      return switch (this.type2) {
         case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0.0F;
         case TOP_CENTER, CENTER, BOTTOM_CENTER -> 0.5F;
         case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> 1.0F;
      };
   }

   private float method2093() {
      return switch (this.type2) {
         case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> 0.0F;
         case CENTER_LEFT, CENTER, CENTER_RIGHT -> 0.5F;
         case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> 1.0F;
      };
   }

   public void method2094(float guiScaledWidth, float guiScaledHeight) {
      if (!(guiScaledWidth <= 0.0F) && !(guiScaledHeight <= 0.0F)) {
         Interface.Type type2 = this.method2078(this.getX(), this.getY(), guiScaledWidth, guiScaledHeight);
         float f = this.method2079(type2, guiScaledWidth);
         float f2 = this.method2080(type2, guiScaledHeight);
         this.type2 = type2;
         this.floatValue12 = this.getX() - f;
         this.floatValue13 = this.getY() - f2;
      }
   }

   public float method2095(float guiScaledWidth) {
      return Math.max(0.0F, this.method2083(guiScaledWidth) - this.method296());
   }

   public float method2096(float guiScaledHeight) {
      return Math.max(0.0F, this.method2086(guiScaledHeight) - this.method297());
   }

   public float method2097(float f) {
      if (f > 0.0F) {
         return f;
      } else if (this.floatValue3 > 0.0F) {
         return this.floatValue3;
      } else {
         return mc != null && mc.getWindow() != null ? mc.getWindow().getGuiScaledWidth() : f;
      }
   }

   public float method2098(float f) {
      if (f > 0.0F) {
         return f;
      } else if (this.floatValue4 > 0.0F) {
         return this.floatValue4;
      } else {
         return mc != null && mc.getWindow() != null ? mc.getWindow().getGuiScaledHeight() : f;
      }
   }

   public net.minecraft.client.model.geom.builders.UVPair method2099() {
      net.minecraft.client.model.geom.builders.UVPair uvPair = Vec2Utils.method8068(mc.getWindow().getGuiScale());
      float f = this.method290();
      float f2 = this.floatValue + this.width / 2.0F;
      float f3 = this.floatValue2 + this.height / 2.0F;
      float f4 = f2 + (uvPair.u() - f2) / f;
      float f5 = f3 + (uvPair.v() - f3) / f;
      return new net.minecraft.client.model.geom.builders.UVPair(f4, f5);
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public float getX() {
      return this.floatValue;
   }

   @Generated
   public float getY() {
      return this.floatValue2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public SliderSetting getScale() {
      return this.scale;
   }

   public class Entry {
      private float floatValue;
      private float offset;

      public Entry(float floatValue, float offset) {
         this.floatValue = floatValue;
         this.offset = offset;
      }

      @Generated
      public float getFloatValue() {
         return this.floatValue;
      }

      @Generated
      public float getOffset() {
         return this.offset;
      }

      @Generated
      public void setFloatValue(float floatValue) {
         this.floatValue = floatValue;
      }

      @Generated
      public void setOffset(float offset) {
         this.offset = offset;
      }
   }

   public static enum Type {
      TOP_LEFT,
      TOP_CENTER,
      TOP_RIGHT,
      CENTER_LEFT,
      CENTER,
      CENTER_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_CENTER,
      BOTTOM_RIGHT;
   }
}
