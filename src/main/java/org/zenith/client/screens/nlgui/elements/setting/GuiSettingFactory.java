package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;

public final class GuiSettingFactory {
   private GuiSettingFactory() {
   }

   public static GuiSetting<?> create(Setting setting, float width) {
      if (setting instanceof SliderSetting sliderSetting) {
         return new GuiNumberSetting(sliderSetting, width);
      } else if (setting instanceof ModeSetting modeSetting) {
         return new GuiModeSetting(modeSetting, width);
      } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
         return new GuiMultiBooleanSetting(multiSelectSetting, width);
      } else if (setting instanceof BooleanSetting booleanSetting) {
         return new GuiBooleanSetting(booleanSetting, width);
      } else if (setting instanceof ColorSetting colorSetting) {
         return new GuiColorSetting(colorSetting, width);
      } else if (setting instanceof ButtonSetting buttonSetting) {
         return new GuiButtonSetting(buttonSetting, width);
      } else if (setting instanceof BlockListSetting blockListSetting) {
         return new GuiItemSelectSetting(blockListSetting, width);
      } else if (setting instanceof BindSetting bindSetting) {
         return new GuiKeySetting(bindSetting, width);
      } else if (setting instanceof TextSetting textSetting) {
         return new GuiStringSetting(textSetting, width);
      } else {
         return setting instanceof GroupSetting groupSetting ? new GuiWindowSetting(groupSetting, width) : null;
      }
   }
}
