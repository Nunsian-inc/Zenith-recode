package org.zenith.client.screens.nlgui.style;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.zenith.ZenithColor;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.utility.misc.ColorCycleIcon;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.display.base.GlowColor;

public class StyleManager {
   private final List<ZenithStyle> styles = new ArrayList<>();
   private final ColorCycleIcon colorCycleIcon;
   private ZenithStyle currentStyle;

   public StyleManager() {
      EventManager.register(this);
      this.colorCycleIcon = new ColorCycleIcon(1700L);
      this.styles.addAll(this.createDefaultStyles());
      if (this.currentStyle == null && !this.styles.isEmpty()) {
         this.currentStyle = this.styles.getFirst();
      }
   }

   @EventTarget
   public void eventRender(ScreenRenderEvent event) {
      this.colorCycleIcon.update();
   }

   public GlowColor getColorCycleIconGradient() {
      ZenithStyle currentStyle = this.getCurrentStyle();
      return currentStyle == null ? GlowColor.method8097(ZenithColor.INSTANCE) : this.colorCycleIcon.method807(currentStyle.getPrimaryColor().getZenithColor());
   }

   public ZenithStyle getCurrentStyle() {
      if (this.currentStyle != null) {
         return this.currentStyle;
      } else {
         return this.styles.isEmpty() ? null : this.styles.getFirst();
      }
   }

   private List<ZenithStyle> createDefaultStyles() {
      ArrayList<ZenithStyle> zenithStyles = new ArrayList<>();
      zenithStyles.add(this.createDefaultStyle("Zenith", "#9AA8FF", "#D1BBFD", "#0B0B147A"));
      zenithStyles.add(this.createDefaultStyle("Amethyst", "#CF96FB", "#F5ADFA", "#0E08117A"));
      zenithStyles.add(this.createDefaultStyle("Synthwave", "#FF83D1", "#BFC1FC", "#1609127A"));
      zenithStyles.add(this.createDefaultStyle("Rosewood", "#F98EA3", "#F8B6A5", "#140A0C7A"));
      zenithStyles.add(this.createDefaultStyle("Ember", "#FF9151", "#FABB3C", "#130A067A"));
      zenithStyles.add(this.createDefaultStyle("Goldleaf", "#D1A917", "#CACC24", "#120F087A"));
      zenithStyles.add(this.createDefaultStyle("Toxic", "#7BC005", "#37E30C", "#0D12087A"));
      zenithStyles.add(this.createDefaultStyle("Verdant", "#19C770", "#31DEB5", "#08110D7A"));
      zenithStyles.add(this.createDefaultStyle("Lagoon", "#1FC0C0", "#50DBBB", "#0811117A"));
      zenithStyles.add(this.createDefaultStyle("Glacier", "#58B5F8", "#B0C5F9", "#0A10157A"));
      zenithStyles.add(this.createDefaultStyle("Graphite", "#A9ADBB", "#C5C4CF", "#0E0E117A"));
      zenithStyles.add(this.createPorcelainStyle());
      return zenithStyles;
   }

   private ZenithStyle createDefaultStyle(String name, String str, String str2, String str3) {
      ZenithStyle zenithStyle = new ZenithStyle(name);
      zenithStyle.getPrimaryColor().setZenithColor(new ZenithColor(str));
      zenithStyle.getSecondaryPrimaryColor().setZenithColor(new ZenithColor(str2));
      zenithStyle.setDefaultsFromGuiStyle(new ZenithColor(str3));
      return zenithStyle;
   }

   private ZenithStyle createPorcelainStyle() {
      ZenithStyle zenithStyle = new ZenithStyle("Porcelain");
      zenithStyle.getPrimaryColor().setZenithColor(new ZenithColor("#45618A"));
      zenithStyle.getSecondaryPrimaryColor().setZenithColor(new ZenithColor("#6E86A8"));
      zenithStyle.setDefaultsFromGuiStyle();
      zenithStyle.getGlowColor1().setZenithColor(new ZenithColor("#45618A3D"));
      zenithStyle.getGlowColor2().setZenithColor(new ZenithColor("#6E86A814"));
      zenithStyle.getLeftBackground().setZenithColor(new ZenithColor("#F4F6FAC4"));
      zenithStyle.getRightBackground().setZenithColor(new ZenithColor("#FAFBFDE0"));
      zenithStyle.getPanelLeftBackground().setZenithColor(new ZenithColor("#F4F6FAC4"));
      zenithStyle.getSurfaceEnableBackground().setZenithColor(new ZenithColor("#FFFFFF8F"));
      zenithStyle.getHeaderDisableBackground().setZenithColor(new ZenithColor("#E9EDF47A"));
      zenithStyle.getSurfaceDisableBackground().setZenithColor(new ZenithColor("#EDF1F76B"));
      zenithStyle.getFieldSurfaceBackground().setZenithColor(new ZenithColor("#1E293B0F"));
      zenithStyle.getFieldBorder().setZenithColor(new ZenithColor("#1E293B24"));
      zenithStyle.getDisableActiveBg().setZenithColor(new ZenithColor("#64748B2E"));
      zenithStyle.getHudBackground().setZenithColor(new ZenithColor("#E7ECF5D6"));
      zenithStyle.getHeaderHudBackground().setZenithColor(new ZenithColor("#D3DCE9D6"));
      zenithStyle.getTextEnable().setZenithColor(new ZenithColor("#1E293B"));
      zenithStyle.getTextSecondary().setZenithColor(new ZenithColor("#0F172AC4"));
      zenithStyle.getTextTertiary().setZenithColor(new ZenithColor("#33415594"));
      zenithStyle.getFriendColor().setZenithColor(new ZenithColor("#0E7A50"));
      zenithStyle.getGlareColor().setZenithColor(new ZenithColor("#1E293B1F"));
      zenithStyle.getHeartIcon().setZenithColor(new ZenithColor("#C93F58"));
      zenithStyle.getHeartActiveBg().setZenithColor(new ZenithColor("#C93F5829"));
      return zenithStyle;
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (ZenithStyle style : this.styles) {
         JsonObject json2 = new JsonObject();
         style.safe(json2);
         jsonArray.add(json2);
      }

      json.add("array", jsonArray);
      ZenithStyle currentStyle = this.getCurrentStyle();
      if (currentStyle != null) {
         json.addProperty("select", currentStyle.getName());
      }

      return json;
   }

   public void load(JsonObject jsonObject) {
      try {
         if (jsonObject.has("select")) {
            this.currentStyle = this.getStyleByName(jsonObject.get("select").getAsString());
         }

         if (jsonObject.has("array")) {
            for (JsonElement json : jsonObject.getAsJsonArray("array")) {
               if (json.isJsonObject()) {
                  JsonObject jsonObject2 = json.getAsJsonObject();
                  if (jsonObject2.has("name")) {
                     ZenithStyle styleByName = this.getStyleByName(jsonObject2.get("name").getAsString());
                     if (styleByName != null) {
                        styleByName.load(jsonObject2);
                     }
                  }
               }
            }
         }

         if (this.currentStyle == null && !this.styles.isEmpty()) {
            this.currentStyle = this.styles.getFirst();
         }
      } catch (Exception e) {
      }
   }

   public ZenithStyle getStyleByName(String string) {
      if (string == null) {
         return null;
      } else {
         for (ZenithStyle style : this.styles) {
            if (string.equals(style.getName())) {
               return style;
            }
         }

         return null;
      }
   }

   public GlowColor getClientColor() {
      return GlowColor.method8096(this.getClientColor(0), this.getClientColor(90), this.getClientColor(180), this.getClientColor(270));
   }

   public ZenithColor getClientColor(int intValue) {
      return ColorUtils.method6435(
         4, intValue, this.getCurrentStyle().getPrimaryColor().getZenithColor(), this.getCurrentStyle().getSecondaryPrimaryColor().getZenithColor()
      );
   }

   public ZenithColor getGlowColor(int intValue) {
      return ColorUtils.method6435(4, intValue, this.getCurrentStyle().getGlowColor1().getZenithColor(), this.getCurrentStyle().getGlowColor2().getZenithColor());
   }

   public GlowColor getGlowColor() {
      return GlowColor.method8096(this.getGlowColor(0), this.getGlowColor(90), this.getGlowColor(180), this.getGlowColor(270));
   }

   @Generated
   public List<ZenithStyle> getStyles() {
      return this.styles;
   }

   @Generated
   public ColorCycleIcon getColorCycleIcon() {
      return this.colorCycleIcon;
   }

   @Generated
   public void setCurrentStyle(ZenithStyle currentStyle) {
      this.currentStyle = currentStyle;
   }
}
