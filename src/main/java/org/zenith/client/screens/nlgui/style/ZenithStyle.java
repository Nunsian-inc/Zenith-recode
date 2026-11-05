package org.zenith.client.screens.nlgui.style;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.zenith.ZenithColor;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.Setting;

public class ZenithStyle {
   private static final String HUD_COLORS_VERSION_KEY = "hudColorsVersion";
   private static final int HUD_COLORS_VERSION = 2;
   private String name;
   private final ColorSetting primaryColor = new ColorSetting("nlgui.style.color.primaryColor", "nlgui.style.color.primaryColor.desc", GuiStyle.PRIMARY_COLOR);
   private final ColorSetting secondaryPrimaryColor = new ColorSetting(
      "nlgui.style.color.secondaryPrimaryColor", "nlgui.style.color.secondaryPrimaryColor.desc", GuiStyle.SECONDARY_PRIMARY_COLOR
   );
   private final ColorSetting glowColor1 = new ColorSetting(
      "nlgui.style.color.glowColor1", "nlgui.style.color.glowColor1.desc", GuiStyle.PRIMARY_COLOR.method7293(70)
   );
   private final ColorSetting glowColor2 = new ColorSetting(
      "nlgui.style.color.glowColor2", "nlgui.style.color.glowColor2.desc", GuiStyle.SECONDARY_PRIMARY_COLOR.method7293(70)
   );
   private final ColorSetting friendColor = new ColorSetting("nlgui.style.color.friendColor", "nlgui.style.color.friendColor.desc", GuiStyle.FRIEND_COLOR);
   private final ColorSetting glareColor = new ColorSetting("nlgui.style.color.glareColor", "nlgui.style.color.glareColor.desc", GuiStyle.GLARE_COLOR);
   private final ColorSetting leftBackground = new ColorSetting(
      "nlgui.style.color.leftBackground", "nlgui.style.color.leftBackground.desc", GuiStyle.LEFT_BACKGROUND
   );
   private final ColorSetting rightBackground = new ColorSetting(
      "nlgui.style.color.rightBackground", "nlgui.style.color.rightBackground.desc", GuiStyle.RIGHT_BACKGROUND
   );
   private final ColorSetting panelLeftBackground = new ColorSetting(
      "nlgui.style.color.panelLeftBackground", "nlgui.style.color.panelLeftBackground.desc", GuiStyle.PANEL_LEFT_BACKGROUND
   );
   private final ColorSetting surfaceEnableBackground = new ColorSetting(
      "nlgui.style.color.surfaceEnableBackground", "nlgui.style.color.surfaceEnableBackground.desc", GuiStyle.SURFACE_ENABLE_BACKGROUND
   );
   private final ColorSetting headerDisableBackground = new ColorSetting(
      "nlgui.style.color.headerDisableBackground", "nlgui.style.color.headerDisableBackground.desc", GuiStyle.HEADER_DISABLE_BACKGROUND
   );
   private final ColorSetting surfaceDisableBackground = new ColorSetting(
      "nlgui.style.color.surfaceDisableBackground", "nlgui.style.color.surfaceDisableBackground.desc", GuiStyle.SURFACE_DISABLE_BACKGROUND
   );
   private final ColorSetting fieldSurfaceBackground = new ColorSetting(
      "nlgui.style.color.fieldSurfaceBackground", "nlgui.style.color.fieldSurfaceBackground.desc", GuiStyle.FIELD_SURFACE_BACKGROUND
   );
   private final ColorSetting fieldBorder = new ColorSetting("nlgui.style.color.fieldBorder", "nlgui.style.color.fieldBorder.desc", GuiStyle.FIELD_BORDER);
   private final ColorSetting disableActiveBg = new ColorSetting(
      "nlgui.style.color.disableActiveBg", "nlgui.style.color.disableActiveBg.desc", GuiStyle.DISABLE_ACTIVE_BG
   );
   private final ColorSetting textEnable = new ColorSetting("nlgui.style.color.textEnable", "nlgui.style.color.textEnable.desc", GuiStyle.TEXT_ENABLE);
   private final ColorSetting textTertiary = new ColorSetting("nlgui.style.color.textTertiary", "nlgui.style.color.textTertiary.desc", GuiStyle.TEXT_TERTIARY);
   private final ColorSetting textSecondary = new ColorSetting(
      "nlgui.style.color.textSecondary", "nlgui.style.color.textSecondary.desc", GuiStyle.TEXT_SECONDARY
   );
   private final ColorSetting heartActiveBg = new ColorSetting(
      "nlgui.style.color.heartActiveBg", "nlgui.style.color.heartActiveBg.desc", GuiStyle.HEART_ACTIVE_BG
   );
   private final ColorSetting heartIcon = new ColorSetting("nlgui.style.color.heartIcon", "nlgui.style.color.heartIcon.desc", GuiStyle.HEART_ICON);
   private final ColorSetting hudBackground = new ColorSetting(
      "nlgui.style.color.hudBackground", "nlgui.style.color.hudBackground.desc", GuiStyle.HUD_BACKGROUND
   );
   private final ColorSetting headerHudBackground = new ColorSetting(
      "nlgui.style.color.headerHudBackground", "nlgui.style.color.headerHudBackground.desc", GuiStyle.HEADER_HUD_BACKGROUND
   );

   public ZenithStyle() {
      this("");
   }

   public ZenithStyle(String name) {
      this.name = name;
   }

   public void setDefaultsFromGuiStyle(ZenithColor defaultsFromGuiStyle) {
      this.setDefaultsFromGuiStyle();
      this.leftBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.leftBackground.getZenithColor().getIntValue4()));
      this.rightBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.rightBackground.getZenithColor().getIntValue4()));
      this.panelLeftBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.panelLeftBackground.getZenithColor().getIntValue4()));
      this.surfaceEnableBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.surfaceEnableBackground.getZenithColor().getIntValue4()));
      this.headerDisableBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.headerDisableBackground.getZenithColor().getIntValue4()));
      this.fieldSurfaceBackground.setZenithColor(GuiStyle.FIELD_SURFACE_BACKGROUND);
      this.surfaceDisableBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.surfaceDisableBackground.getZenithColor().getIntValue4()));
      this.hudBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.hudBackground.getZenithColor().getIntValue4()));
      this.headerHudBackground.setZenithColor(defaultsFromGuiStyle.method7293(this.headerHudBackground.getZenithColor().getIntValue4()));
   }

   public void setDefaultsFromGuiStyle() {
      this.leftBackground.setZenithColor(GuiStyle.LEFT_BACKGROUND);
      this.rightBackground.setZenithColor(GuiStyle.RIGHT_BACKGROUND);
      this.panelLeftBackground.setZenithColor(GuiStyle.PANEL_LEFT_BACKGROUND);
      this.surfaceEnableBackground.setZenithColor(GuiStyle.SURFACE_ENABLE_BACKGROUND);
      this.headerDisableBackground.setZenithColor(GuiStyle.HEADER_DISABLE_BACKGROUND);
      this.fieldSurfaceBackground.setZenithColor(GuiStyle.FIELD_SURFACE_BACKGROUND);
      this.fieldBorder.setZenithColor(GuiStyle.FIELD_BORDER);
      this.surfaceDisableBackground.setZenithColor(GuiStyle.SURFACE_DISABLE_BACKGROUND);
      this.textEnable.setZenithColor(GuiStyle.TEXT_ENABLE);
      this.textTertiary.setZenithColor(GuiStyle.TEXT_TERTIARY);
      this.textSecondary.setZenithColor(GuiStyle.TEXT_SECONDARY);
      this.glowColor1.setZenithColor(this.primaryColor.getZenithColor().method7293(70));
      this.glowColor2.setZenithColor(this.secondaryPrimaryColor.getZenithColor().method7293(70));
      this.friendColor.setZenithColor(GuiStyle.FRIEND_COLOR);
      this.glareColor.setZenithColor(GuiStyle.GLARE_COLOR);
      this.disableActiveBg.setZenithColor(GuiStyle.DISABLE_ACTIVE_BG);
      this.heartActiveBg.setZenithColor(GuiStyle.HEART_ACTIVE_BG);
      this.heartIcon.setZenithColor(GuiStyle.HEART_ICON);
   }

   public void safe(JsonObject json) {
      json.addProperty("name", this.name);
      this.leftBackground.safe(json);
      this.rightBackground.safe(json);
      this.panelLeftBackground.safe(json);
      this.surfaceEnableBackground.safe(json);
      this.headerDisableBackground.safe(json);
      this.fieldSurfaceBackground.safe(json);
      this.fieldBorder.safe(json);
      this.surfaceDisableBackground.safe(json);
      this.textEnable.safe(json);
      this.textTertiary.safe(json);
      this.textSecondary.safe(json);
      this.primaryColor.safe(json);
      this.secondaryPrimaryColor.safe(json);
      this.glowColor1.safe(json);
      this.glowColor2.safe(json);
      this.friendColor.safe(json);
      this.glareColor.safe(json);
      this.disableActiveBg.safe(json);
      this.heartActiveBg.safe(json);
      this.heartIcon.safe(json);
      this.hudBackground.safe(json);
      this.headerHudBackground.safe(json);
      json.addProperty("hudColorsVersion", 2);
   }

   public void load(JsonObject json) {
      if (json.has("name")) {
         this.name = json.get("name").getAsString();
      }

      this.loadColor(json, this.leftBackground, "leftBackground");
      this.loadColor(json, this.rightBackground, "rightBackground");
      this.loadColor(json, this.panelLeftBackground, "panelLeftBackground");
      this.loadColor(json, this.surfaceEnableBackground, "surfaceEnableBackground");
      this.loadColor(json, this.headerDisableBackground, "headerDisableBackground");
      this.loadColor(json, this.fieldSurfaceBackground, "fieldSurfaceBackground");
      this.loadColor(json, this.fieldBorder, "fieldBorder");
      this.loadColor(json, this.surfaceDisableBackground, "surfaceDisableBackground");
      this.loadColor(json, this.textEnable, "textEnable");
      this.loadColor(json, this.textTertiary, "textTertiary");
      this.loadColor(json, this.textSecondary, "textSecondary");
      this.loadColor(json, this.primaryColor, "primaryColor");
      this.loadColor(json, this.secondaryPrimaryColor);
      this.loadColor(json, this.glowColor1);
      this.loadColor(json, this.glowColor2);
      this.loadColor(json, this.friendColor);
      this.loadColor(json, this.glareColor, "glareColor");
      this.loadColor(json, this.disableActiveBg, "disableActiveBg");
      this.loadColor(json, this.heartActiveBg, "heartActiveBg");
      this.loadColor(json, this.heartIcon, "heartIcon");
      if (json.has("hudColorsVersion") && json.get("hudColorsVersion").getAsInt() >= 2) {
         this.loadColor(json, this.hudBackground, "hudBackground");
         this.loadColor(json, this.headerHudBackground, "headerHudBackground");
      }
   }

   private void loadColor(JsonObject json, ColorSetting leftBackground, String... strings) {
      if (json.has(leftBackground.getKey())) {
         leftBackground.method1446(json.get(leftBackground.getKey()).getAsInt());
      } else {
         for (String memberName : strings) {
            if (json.has(memberName)) {
               leftBackground.method1446(json.get(memberName).getAsInt());
               return;
            }
         }
      }
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(field -> {
         try {
            field.setAccessible(true);
            return field.get(this);
         } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
         }
      }).filter(obj -> obj instanceof Setting).map(obj -> (Setting)obj).collect(Collectors.toList());
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public ColorSetting getPrimaryColor() {
      return this.primaryColor;
   }

   @Generated
   public ColorSetting getSecondaryPrimaryColor() {
      return this.secondaryPrimaryColor;
   }

   @Generated
   public ColorSetting getGlowColor1() {
      return this.glowColor1;
   }

   @Generated
   public ColorSetting getGlowColor2() {
      return this.glowColor2;
   }

   @Generated
   public ColorSetting getFriendColor() {
      return this.friendColor;
   }

   @Generated
   public ColorSetting getGlareColor() {
      return this.glareColor;
   }

   @Generated
   public ColorSetting getLeftBackground() {
      return this.leftBackground;
   }

   @Generated
   public ColorSetting getRightBackground() {
      return this.rightBackground;
   }

   @Generated
   public ColorSetting getPanelLeftBackground() {
      return this.panelLeftBackground;
   }

   @Generated
   public ColorSetting getSurfaceEnableBackground() {
      return this.surfaceEnableBackground;
   }

   @Generated
   public ColorSetting getHeaderDisableBackground() {
      return this.headerDisableBackground;
   }

   @Generated
   public ColorSetting getSurfaceDisableBackground() {
      return this.surfaceDisableBackground;
   }

   @Generated
   public ColorSetting getFieldSurfaceBackground() {
      return this.fieldSurfaceBackground;
   }

   @Generated
   public ColorSetting getFieldBorder() {
      return this.fieldBorder;
   }

   @Generated
   public ColorSetting getDisableActiveBg() {
      return this.disableActiveBg;
   }

   @Generated
   public ColorSetting getTextEnable() {
      return this.textEnable;
   }

   @Generated
   public ColorSetting getTextTertiary() {
      return this.textTertiary;
   }

   @Generated
   public ColorSetting getTextSecondary() {
      return this.textSecondary;
   }

   @Generated
   public ColorSetting getHeartActiveBg() {
      return this.heartActiveBg;
   }

   @Generated
   public ColorSetting getHeartIcon() {
      return this.heartIcon;
   }

   @Generated
   public ColorSetting getHudBackground() {
      return this.hudBackground;
   }

   @Generated
   public ColorSetting getHeaderHudBackground() {
      return this.headerHudBackground;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }
}
