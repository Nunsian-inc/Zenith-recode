package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Menu",
   category = ModuleCategory.RENDER,
   description = "module.menu.description",
   method896 = true
)
public final class MenuModule extends Module {
   public static final MenuModule INSTANCE = new MenuModule();
   private final ModeSetting animation = new ModeSetting(
      "module.menu.animation", "module.menu.animation.desc", "module.menu.animation.normal", "module.menu.animation.blur"
   );
   private final SliderSetting closeAnimationSpeed = new SliderSetting(
      "module.menu.closeAnimationSpeed", 300.0F, 100.0F, 500.0F, 10.0F, "module.menu.closeAnimationSpeed.desc", "ms"
   );
   private final SliderSetting elementSwitchSpeed = new SliderSetting(
      "module.menu.elementSwitchSpeed", 250.0F, 100.0F, 500.0F, 10.0F, "module.menu.elementSwitchSpeed.desc", "ms"
   );
   private final BooleanSetting moveGui = new BooleanSetting("module.menu.moveGui", "module.menu.moveGui.desc", false);
   private boolean flag;
   private float floatValue;
   private float floatValue2;
   private Screen screen;

   private MenuModule() {
      this.setKeyCode(344);
   }

   @Override
   public void onEnable() {
      if (mc.mouseHandler == null) {
         this.setEnabled(false);
      } else {
         this.screen = mc.gui.screen();
         if (mc.gui.screen() != Zenith.getInstance().getNLMenuScreen()) {
            NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
            nlMenuScreen.initialize();
            boolean botOnly = this.screen instanceof BotControlScreen;
            nlMenuScreen.getGuiModulePanel().setBotOnly(botOnly);
            if (botOnly) {
               nlMenuScreen.resetSearch();
               nlMenuScreen.setType(NLMenuScreen.ElementsType.CATEGORY);
               nlMenuScreen.getGuiModulePanel().setBotContext(((BotControlScreen)this.screen).getBotName());
               nlMenuScreen.getGuiModulePanel().forceCategory(ModuleCategory.PLAYER);
            }

            mc.gui.setScreen(nlMenuScreen);
            Zenith.getInstance().getClientSounds().method1561(Zenith.getInstance().getClientSounds().soundEvent);
            super.onEnable();
         }
      }
   }

   @Override
   public void onDisable() {
      if (mc.gui.screen() == Zenith.getInstance().getNLMenuScreen()) {
         this.setEnabled(true);
      } else {
         super.onDisable();
      }
   }

   @Override
   public void setKeyCode(int keyCode) {
      if (keyCode != -1) {
         super.setKeyCode(keyCode);
      }
   }

   @EventTarget(3)
   public void method5343(ScreenRenderEvent event) {
      MenuDrawContext menuDrawContext = event.getMenuDrawContext();
      if (this.screen != null) {
         this.screen.extractRenderState(menuDrawContext, 0, 0, mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
         ScissorFlusher.flush();
         Zenith.getInstance().getKawaseBlur().method7349(menuDrawContext);
      }

      Zenith.getInstance().getNLMenuScreen().renderTop(menuDrawContext, menuDrawContext.getMouseX(), menuDrawContext.getMouseY());
      if (Zenith.getInstance().getNLMenuScreen().isFinish()) {
         if (this.screen instanceof BotControlScreen) {
            Screen screen = this.screen;
            this.screen = null;
            if (mc.gui.screen() == Zenith.getInstance().getNLMenuScreen() || mc.gui.screen() == null) {
               mc.gui.setScreen(screen);
            }
         }

         this.toggle();
      }
   }

   public boolean method5344() {
      return this.animation.isSelected(1);
   }

   public long method5345() {
      return (long)this.closeAnimationSpeed.getValue3();
   }

   public long method5346() {
      return (long)this.elementSwitchSpeed.getValue3();
   }

   public long method5347() {
      return (long)this.elementSwitchSpeed.getValue3();
   }

   public boolean method5348() {
      return this.moveGui.isEnabled();
   }

   public List<Setting> method5349() {
      return List.of(this.moveGui);
   }

   public List<Setting> method5350() {
      return List.of(this.animation, this.closeAnimationSpeed, this.elementSwitchSpeed);
   }

   public float method5351(float f) {
      return this.flag ? this.floatValue : f;
   }

   public float method5352(float f) {
      return this.flag ? this.floatValue2 : f;
   }

   public void method5353(float floatValue, float rawY) {
      this.floatValue = floatValue;
      this.floatValue2 = rawY;
      this.flag = true;
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      JsonObject json2 = new JsonObject();
      json2.addProperty("saved", this.flag);
      json2.addProperty("x", this.floatValue);
      json2.addProperty("y", this.floatValue2);
      json.add("GuiPosition", json2);
      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null && json.has("GuiPosition") && json.get("GuiPosition").isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject("GuiPosition");
         if (jsonObject.has("saved")) {
            this.flag = jsonObject.get("saved").getAsBoolean();
         }

         if (jsonObject.has("x")) {
            this.floatValue = jsonObject.get("x").getAsFloat();
         }

         if (jsonObject.has("y")) {
            this.floatValue2 = jsonObject.get("y").getAsFloat();
         }
      }
   }
}
