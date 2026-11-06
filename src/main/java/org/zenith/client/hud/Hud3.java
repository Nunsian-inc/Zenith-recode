package org.zenith.client.hud;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector2f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.misc.ClickActionModule;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.Vec2Utils;

public class Hud3 extends Interface {
   private static final float CONST_floatValue = 16.0F;
   private static final float CONST_floatValue2 = 7.0F;
   private static final float CONST_floatValue3 = 11.0F;
   private static final float CONST_floatValue4 = 3.0F;
   private static final float CONST_floatValue5 = 8.0F;
   private static final float CONST_floatValue6 = 25.0F;
   private static final Vector2f vector2f = new Vector2f(0.0F, 0.0F);
   private final Animation animation = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 25.0F, Easing.INSTANCE4);
   private final List<Hud3.Data> items = new ArrayList<>();
   private final Map<String, Vector2f> items2 = new LinkedHashMap<>();
   private final Map<String, float[]> items3 = new LinkedHashMap<>();
   private String text;
   private float floatValue;
   private float floatValue2;

   public Hud3(String text, float floatValue, float f, float f2, float f3, float f4, float f5, Interface.Type type) {
      super(text, floatValue, f, f2, f3, f4, f5, type);
   }

   @Override
   public void method8(CustomDrawContext customDrawContext) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font font = Fonts.NEW_SEMIBOLD.getFont(5.0F);
      boolean flag = this.method2074();
      List<Hud3.Data> items = this.method2280();
      boolean visible = flag || !items.isEmpty();
      if (visible) {
         this.items.clear();
         this.items.addAll(items);
      } else {
         items = this.items;
      }

      ArrayList<Hud3.Data> datas = new ArrayList();
      ArrayList<Hud3.Data> datas2 = new ArrayList();

      for (Hud3.Data data : items) {
         (this.items2.containsKey(data.getText()) ? datas2 : datas).add(data);
      }

      float width = this.method2275(datas, font);
      this.method2279(items, width, flag);
      this.width = this.animation2.animateTo(this.method2276(datas, datas2, font, width));
      this.height = this.method2277(datas2);
      this.animation.updateVisibility(visible);
      if (this.animation.getValue() <= 0.01F) {
         this.items.clear();
      } else {
         float x = this.getX();
         float y = this.getY();
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(x + this.width / 2.0F, y + this.height / 2.0F);
         customDrawContext.pose().scale(this.animation.getValue(), this.animation.getValue());
         customDrawContext.pose().translate(-(x + this.width / 2.0F), -(y + this.height / 2.0F));
         this.items3.clear();
         if (!datas.isEmpty()) {
            this.method2272(customDrawContext, x, y, width, currentStyle);
            float f = x + 7.0F;

            for (Hud3.Data data : datas) {
               float f2 = this.method2274(data, font);
               this.method2273(customDrawContext, data, f, y, font, currentStyle);
               this.items3.put(data.getText(), new float[]{f, y, f2});
               f += f2 + 8.0F;
            }
         }

         for (Hud3.Data data : datas2) {
            Vector2f vec = this.method2278(data);
            float f = 14.0F + this.method2274(data, font);
            float x2 = x + vec.x();
            float y2 = y + vec.y();
            this.method2272(customDrawContext, x2, y2, f, currentStyle);
            this.method2273(customDrawContext, data, x2 + 7.0F, y2, font, currentStyle);
            this.items3.put(data.getText(), new float[]{x2 + 7.0F, y2, this.method2274(data, font)});
         }

         customDrawContext.popMatrix();
      }
   }

   private void method2272(CustomDrawContext customDrawContext, float x, float y, float width, ZenithStyle currentStyle) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 8.0F));
      customDrawContext.drawBlurHud(x, y, width, 16.0F, 21.0F, headerRadius, ZenithColor.INSTANCE);
      customDrawContext.drawRoundedRect(x, y, width, 16.0F, headerRadius, currentStyle.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778()));
      this.method4679(customDrawContext, x, y, width, 16.0F, this.animation.getValue());
   }

   private void method2273(CustomDrawContext customDrawContext, Hud3.Data data, float x, float y, Font font, ZenithStyle currentStyle) {
      try {
         float f = 0.6875F;
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(x, y + 2.5F);
         customDrawContext.pose().scale(f, f);
         customDrawContext.item(data.getItemStack(), 0, 0);
         customDrawContext.renderItemBar(data.getItemStack(), 0, 0);
         customDrawContext.renderItemCooldown(data.getItemStack(), 0, 0);
         customDrawContext.popMatrix();
         customDrawContext.drawText(font, data.getText2(), x + 11.0F + 3.0F, y + (16.0F - font.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private float method2274(Hud3.Data data, Font font) {
      return 14.0F + font.width(data.getText2());
   }

   private float method2275(List<Hud3.Data> datas, Font font) {
      if (datas.isEmpty()) {
         return 0.0F;
      } else {
         float f = 0.0F;

         for (Hud3.Data data : datas) {
            f += this.method2274(data, font) + 8.0F;
         }

         return 14.0F + f - 8.0F;
      }
   }

   private float method2276(List<Hud3.Data> datas, List<Hud3.Data> datas2, Font font, float width) {
      float f = Math.max(25.0F, width);

      for (Hud3.Data data : datas2) {
         f = Math.max(f, this.method2278(data).x + 14.0F + this.method2274(data, font));
      }

      return f;
   }

   private float method2277(List<Hud3.Data> datas) {
      float f = 16.0F;

      for (Hud3.Data data : datas) {
         f = Math.max(f, this.method2278(data).y + 16.0F);
      }

      return f;
   }

   private Vector2f method2278(Hud3.Data data) {
      return this.items2.getOrDefault(data.getText(), vector2f);
   }

   @Override
   public boolean method2056(MouseButtonEvent event) {
      if (this.method2074() && event.getIntValue() == 0 && event.getIntValue2() == 1) {
         net.minecraft.client.model.geom.builders.UVPair uvPair = Vec2Utils.method8068(InterfaceModule.INSTANCE.method6807());

         for (Map.Entry entry : this.items3.entrySet()) {
            float[] floats = (float[])entry.getValue();
            if (Vec2Utils.method8067(floats[0], floats[1], floats[2], 16.0, uvPair.u(), uvPair.v())) {
               this.text = (String)entry.getKey();
               this.floatValue = uvPair.u() - floats[0];
               this.floatValue2 = uvPair.v() - floats[1];
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void method2279(List<Hud3.Data> items, float width, boolean flag) {
      if (this.text != null) {
         boolean flag2 = mc.getWindow() != null && org.zenith.utility.misc.KeyCompat.isMouseDown(0);
         boolean anyMatch = items.stream().anyMatch(var1x -> var1x.getText().equals(this.text));
         if (flag && flag2 && anyMatch) {
            net.minecraft.client.model.geom.builders.UVPair uvPair = Vec2Utils.method8068(InterfaceModule.INSTANCE.method6807());
            float x = Math.max(0.0F, uvPair.u() - this.floatValue - 7.0F - this.floatValue);
            float y = Math.max(0.0F, uvPair.v() - this.floatValue2 - this.floatValue2);
            if (y < 12.0F && x < Math.max(width, 25.0F)) {
               this.items2.remove(this.text);
            } else {
               this.items2.put(this.text, new Vector2f(x, y));
            }
         } else {
            this.text = null;
         }
      }
   }

   @Override
   public void method2069(CustomDrawContext customDrawContext) {
   }

   @Override
   public float method254() {
      return this.animation.getValue();
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      if (!this.items2.isEmpty()) {
         JsonObject json2 = new JsonObject();
         this.items2.forEach((var1x, var2x) -> {
            JsonObject json3 = new JsonObject();
            json3.addProperty("x", var2x.x());
            json3.addProperty("y", var2x.y());
            json2.add(var1x, json3);
         });
         json.add("detached", json2);
      }

      return json;
   }

   @Override
   public void load(JsonObject jsonObject) {
      super.load(jsonObject);
      this.items2.clear();
      if (jsonObject != null && jsonObject.has("detached") && jsonObject.get("detached").isJsonObject()) {
         JsonObject json = jsonObject.getAsJsonObject("detached");

         for (String memberName : json.keySet()) {
            if (json.get(memberName).isJsonObject()) {
               JsonObject jsonObject2 = json.getAsJsonObject(memberName);
               this.items2.put(memberName, new Vector2f(jsonObject2.has("x") ? jsonObject2.get("x").getAsFloat() : 0.0F, jsonObject2.has("y") ? jsonObject2.get("y").getAsFloat() : 0.0F));
            }
         }
      }
   }

   private List<Hud3.Data> method2280() {
      ArrayList<Hud3.Data> datas = new ArrayList<>();
      if (mc != null && mc.player != null) {
         ServerHelperModule serverHelperModule = ServerHelperModule.INSTANCE;
         if (method2281(serverHelperModule.getShulkerKey())) {
            this.method2282(datas, serverHelperModule.getShulkerKey(), serverHelperModule.method5217());
         }

         for (ServerHelperModule.Data3 data3 : serverHelperModule.method5216()) {
            if (method2281(data3.getBindSetting())) {
               this.method2282(datas, data3.getBindSetting(), data3.method5218());
            }
         }

         ClickActionModule clickActionModule = ClickActionModule.INSTANCE;
         if (method2281(clickActionModule.getExpBind())) {
            this.method2282(datas, clickActionModule.getExpBind(), InventoryUtils.method906(Items.EXPERIENCE_BOTTLE));
         }

         for (ClickActionModule.Data data : clickActionModule.method5486()) {
            if (method2281(data.getBindSetting())) {
               this.method2282(datas, data.getBindSetting(), InventoryUtils.method906(data.getItem()));
            }
         }

         return datas;
      } else {
         return datas;
      }
   }

   private static boolean method2281(BindSetting bindSetting) {
      return bindSetting != null && bindSetting.getKeyCode() != -1 && bindSetting.isVisible();
   }

   private void method2282(List<Hud3.Data> datas, BindSetting bindSetting, Slot slot) {
      if (bindSetting != null && slot != null && bindSetting.getKeyCode() != -1 && bindSetting.isVisible()) {
         ItemStack itemStack = slot.getItem().copy();
         if (!itemStack.isEmpty()) {
            datas.add(new Hud3.Data(bindSetting.getName(), KeyName.method6701(bindSetting.getKeyCode()), bindSetting.getKeyCode(), itemStack));
         }
      }
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final int intValue;
      private final ItemStack itemStack;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue() {
         return this.intValue;
      }

      public ItemStack itemStack() {
         return this.itemStack;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.itemStack, other.itemStack);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, intValue, itemStack);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + ", " + "itemStack=" + this.itemStack + "]";
      }

      private Data(String text, String str, int intValue, ItemStack itemStack) {
         this.text = text;
         this.text2 = str;
         this.intValue = intValue;
         this.itemStack = itemStack;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }
   }
}
