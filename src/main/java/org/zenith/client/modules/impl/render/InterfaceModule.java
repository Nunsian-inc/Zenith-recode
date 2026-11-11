package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.world.entity.LivingEntity;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.hud.CooldownsHud;
import org.zenith.client.hud.EventsHud;
import org.zenith.client.hud.Hud1;
import org.zenith.client.hud.Hud2;
import org.zenith.client.hud.Hud3;
import org.zenith.client.hud.Hud4;
import org.zenith.client.hud.InfoBoxesHud;
import org.zenith.client.hud.InfoBoxesHud2;
import org.zenith.client.hud.InterfaceHud;
import org.zenith.client.hud.KeybindsHud;
import org.zenith.client.hud.MusicHud;
import org.zenith.client.hud.PartyHud;
import org.zenith.client.hud.PotionsHud;
import org.zenith.client.hud.ScoreboardHud;
import org.zenith.client.hud.SidebarHud;
import org.zenith.client.hud.StaffListHud;
import org.zenith.client.hud.StatusBarsHud;
import org.zenith.client.hud.TargetHud;
import org.zenith.client.hud.TargetPotionsHud;
import org.zenith.client.hud.TimerHud;
import org.zenith.client.hud.SessionStatsHud;
import org.zenith.client.hud.WatermarkIslandHud;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.HudDragController;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.HudQueuedContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.Vec2Utils;

@ModuleInfo(
   name = "Interface",
   category = ModuleCategory.RENDER,
   description = "module.interface.description",
   method896 = true
)
public final class InterfaceModule extends Module {
   private final List<Interface> items = new ArrayList<>();
   private final Map<String, Interface> items2 = new HashMap<>();
   private final Jebriksheet jebriksheet = new Jebriksheet();
   private final HudDragController class31 = new HudDragController();
   public static final InterfaceModule INSTANCE = new InterfaceModule();
   private Interface interfaceValue = null;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private long longValue = 0L;
   private boolean flag = false;
   private final MultiSelectSetting cosmetics = new MultiSelectSetting("module.interface.cosmetics");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.blur", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.glass", false);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.glow", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.whiteNoise", false);
   private final ModeSetting glowMode = new ModeSetting(
      "module.interface.glowMode", "module.interface.glowMode.desc", () -> true, "module.interface.glowMode.outline", "module.interface.fill"
   );
   private final SliderSetting glowRadius = new SliderSetting(
      "module.interface.glowRadius", 8.0F, 5.0F, 15.0F, 1.0F, "module.interface.glowRadius.desc", "px", () -> true, null
   );
   private final GroupSetting glowSettings = new GroupSetting(
      "module.interface.glowSettings", "module.interface.glowSettings.desc", this.entry3::isEnabled, this.glowMode, this.glowRadius
   );
   private final SliderSetting glareSpeed = new SliderSetting(
      "module.interface.glareSpeed", 0.2F, 0.05F, 1.0F, 0.05F, "module.interface.glareSpeed.desc", "x", () -> true, null
   );
   private final ModeSetting glareMode = new ModeSetting(
      "module.interface.glareMode",
      "module.interface.glareMode.desc",
      () -> true,
      "module.interface.glare.diagonal",
      "module.interface.glare.sweep",
      "module.interface.glare.double",
      "module.interface.glare.edge",
      "module.interface.glare.spotlight",
      "module.interface.glare.aurora",
      "module.interface.glare.off"
   );
   private static final int[] CONST_intValues = new int[]{0, 1, 2, 3, 5, 6, 11};
   private final ModeSetting glareTexture = new ModeSetting(
      "module.interface.glareTexture",
      "module.interface.glareTexture.desc",
      () -> true,
      "module.interface.glareTexture.plain",
      "module.interface.glareTexture.prism",
      "module.interface.glareTexture.bloom",
      "module.interface.glareTexture.neon",
      "module.interface.glareTexture.chrome",
      "module.interface.glareTexture.rays"
   );
   private final GroupSetting glassSettings = new GroupSetting(
      "module.interface.glassSettings", "module.interface.glassSettings.desc", this.entry2::isEnabled, this.glareMode, this.glareTexture, this.glareSpeed
   );
   public final SliderSetting volume = new SliderSetting(
      "module.interface.volume", 0.5F, 0.1F, 1.0F, 0.1F, "module.interface.volume.desc", "%", this::method6814, null
   );
   private final SliderSetting round = new SliderSetting("module.interface.round", 6.0F, 0.0F, 6.0F, 0.1F, "module.interface.round.desc", "px");
   private final BooleanSetting corners = new BooleanSetting("module.interface.corners", "module.interface.corners.desc", true);
   private final BooleanSetting saturation = new BooleanSetting("module.interface.saturation", "module.interface.saturation.desc", true);
   private final BooleanSetting hideScoreBoard = new BooleanSetting("module.interface.hideScoreBoard", "module.interface.hideScoreBoard.desc", false);
   private final SliderSetting density = new SliderSetting("module.interface.hud.density", 100.0F, 0.0F, 150.0F, 5.0F, "module.interface.hud.density.desc", "%");
   private final SliderSetting blur = new SliderSetting("module.interface.hud.blur", 21.0F, 0.0F, 40.0F, 1.0F, "module.interface.hud.blur.desc", "px");
   private final ModeSetting outline = new ModeSetting(
      "module.interface.hud.outline",
      "module.interface.hud.outline.desc",
      "module.interface.hud.outline.none",
      "module.interface.hud.outline.soft",
      "module.interface.hud.outline.accent",
      "module.interface.hud.outline.custom"
   );
   private final SliderSetting outlineWidth = new SliderSetting(
      "module.interface.hud.outlineWidth", 0.6F, 0.2F, 2.0F, 0.1F, "module.interface.hud.outlineWidth.desc", "px", () -> !this.outline.isSelected(0)
   );
   private final ColorSetting colorSetting = new ColorSetting(
      "module.interface.hud.outlineColor", "module.interface.hud.outlineColor.desc", new ZenithColor(255, 255, 255, 90), () -> this.outline.isSelected(3)
   );
   private final BooleanSetting fade = new BooleanSetting("module.interface.hud.fade", "module.interface.hud.fade.desc", true);
   private final SliderSetting fadeTime = new SliderSetting(
      "module.interface.hud.fadeTime", 220.0F, 60.0F, 800.0F, 20.0F, "module.interface.hud.fadeTime.desc", "ms", this.fade::isEnabled
   );
   private final BooleanSetting hideInScreens = new BooleanSetting("module.interface.hud.hideInScreens", "module.interface.hud.hideInScreens.desc", false);
   private final SliderSetting combatTime = new SliderSetting(
      "module.interface.hud.combatTime", 6.0F, 2.0F, 20.0F, 1.0F, "module.interface.hud.combatTime.desc", "s"
   );
   private final BooleanSetting guides = new BooleanSetting("module.interface.hud.guides", "module.interface.hud.guides.desc", true);
   private final SliderSetting grid = new SliderSetting("module.interface.hud.grid", 0.0F, 0.0F, 16.0F, 1.0F, "module.interface.hud.grid.desc", "px");
   private final SliderSetting margin = new SliderSetting("module.interface.hud.margin", 0.0F, 0.0F, 24.0F, 1.0F, "module.interface.hud.margin.desc", "px");
   private final ButtonSetting editor = new ButtonSetting("module.interface.hud.editor", this::method6792);
   private final ButtonSetting showAll = new ButtonSetting("module.interface.hud.showAll", () -> this.method6790(true));
   private final ButtonSetting hideAll = new ButtonSetting("module.interface.hud.hideAll", () -> this.method6790(false));
   private final ButtonSetting resetLayout = new ButtonSetting("module.interface.hud.resetLayout", this::method6791);
   private long longValue2 = -4611686018427387904L;
   private final SliderSetting scale = new SliderSetting(
      "module.interface.hudElement.scale", 100.0F, 50.0F, 250.0F, 1.0F, "module.interface.hudElement.scale.desc", "%", () -> true, (var1x, floatValue) -> {
         for (Interface interfaceValue : this.items) {
            interfaceValue.getScale().method2400(floatValue);
         }
      }
   );
   private static final int CONST_intValue = 64;

   public boolean method6770() {
      return this.hideScoreBoard.isEnabled();
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.cosmetics, this.glowSettings, this.glassSettings, this.round, this.corners, this.saturation, this.hideScoreBoard, this.volume);
   }

   public List<Setting> method6771() {
      return List.of(this.cosmetics, this.glowSettings, this.glassSettings, this.round, this.corners);
   }

   public List<Setting> method6772() {
      return List.of(this.saturation, this.hideScoreBoard, this.volume);
   }

   public List<Setting> method6773() {
      return List.of(this.editor, this.showAll, this.hideAll, this.resetLayout);
   }

   public List<Setting> method6774() {
      return List.of(this.density, this.blur, this.outline, this.outlineWidth, this.colorSetting, this.scale);
   }

   public List<Setting> method6775() {
      return List.of(this.guides, this.grid, this.margin);
   }

   public List<Setting> method6776() {
      return List.of(this.fade, this.fadeTime, this.hideInScreens, this.combatTime);
   }

   private List<Setting> method6777() {
      var settings = new ArrayList<>(this.method6774());
      settings.remove(this.scale);
      settings.addAll(this.method6775());
      settings.addAll(this.method6776());
      return settings;
   }

   public float method6778() {
      return this.density.getValue3() / 100.0F;
   }

   public float method6779(float blurPower) {
      float f = this.blur.getValue3();
      return f <= 0.0F ? 0.0F : blurPower * f / 21.0F;
   }

   public boolean method6780() {
      return !this.outline.isSelected(0);
   }

   public float method6781() {
      return this.outlineWidth.getValue3();
   }

   public ZenithColor method6782() {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();

      return switch (this.outline.getIndex()) {
         case 1 -> currentStyle.getTextTertiary().getZenithColor().method7293(70);
         case 2 -> currentStyle.getPrimaryColor().getZenithColor().method7293(150);
         default -> this.colorSetting.getZenithColor();
      };
   }

   public boolean method6783() {
      return this.fade.isEnabled();
   }

   public float method6784() {
      return this.fadeTime.getValue3();
   }

   public boolean method6785() {
      return this.hideInScreens.isEnabled();
   }

   public float method6786() {
      return this.grid.getValue3();
   }

   public float method6787() {
      return this.margin.getValue3();
   }

   public boolean method6788() {
      return (float)(System.currentTimeMillis() - this.longValue2) < this.combatTime.getValue3() * 1000.0F;
   }

   private void method6789() {
      if (mc.player != null) {
         boolean flag = mc.player.hurtTime > 0;
         boolean flag2 = mc.player.isSwinging() && mc.crosshairPickEntity instanceof LivingEntity;
         if (flag || flag2) {
            this.longValue2 = System.currentTimeMillis();
         }
      }
   }

   public void method6790(boolean flag) {
      for (Interface interfaceValue : this.items) {
         interfaceValue.setEnabled(flag);
      }
   }

   public void method6791() {
      for (Interface interfaceValue : this.items) {
         interfaceValue.method2063();
      }
   }

   private void method6792() {
      mc.gui.setScreen(new ChatScreen("", false));
   }

   private InterfaceModule() {
      this.method6793(new Hud3("ItemBinds", 349.0F, 0.0F, 960.0F, 495.5F, -11.5F, 146.0F, Interface.Type.TOP_RIGHT));
      this.method6793(new WatermarkIslandHud("Watermark", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 10.0F, Interface.Type.TOP_LEFT));
      this.method6793(new PotionsHud("Potions", 0.0F, 0.0F, 960.0F, 495.5F, 119.15234F, 73.0F, Interface.Type.TOP_LEFT));
      this.method6793(new StaffListHud("Staffs", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 73.0F, Interface.Type.TOP_LEFT));
      InterfaceHud interfaceHud = new InterfaceHud("Notifications", 181.80615F, 135.5F, 960.0F, 495.5F, 157.03516F, -72.5F, Interface.Type.CENTER);
      this.method6793(interfaceHud);
      Zenith.getInstance().getHudHolder().setInterfaceHud(interfaceHud);
      this.method6793(new Hud4("Inventory", 269.0F, 229.0F, 960.0F, 495.5F, -11.5F, -74.0F, Interface.Type.BOTTOM_RIGHT));
      this.method6793(new CooldownsHud("Cooldowns", 349.0F, 0.0F, 960.0F, 495.5F, -11.5F, 73.0F, Interface.Type.TOP_RIGHT));
      this.method6793(new InfoBoxesHud("Information", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 41.5F, Interface.Type.TOP_LEFT));
      this.method6793(new TimerHud("TimerBar", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 57.5F, Interface.Type.TOP_LEFT));
      this.method6793(new InfoBoxesHud2("Coordinates", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 41.5F, Interface.Type.TOP_LEFT));
      this.method6793(new KeybindsHud("Keybinds", 349.0F, 0.0F, 960.0F, 495.5F, -122.0F, 73.0F, Interface.Type.TOP_RIGHT));
      this.method6793(new TargetHud("TargetHud", 166.5F, 128.5F, 960.0F, 495.5F, 0.0F, 31.75F, Interface.Type.CENTER));
      this.method6793(new MusicHud("MusicInfo", 342.0F, 257.0F, 960.0F, 495.5F, -11.5F, -16.5F, Interface.Type.BOTTOM_RIGHT));
      this.method6793(new StatusBarsHud("HootBar", 116.5F, 265.0F, 960.0F, 495.5F, 0.0F, -16.5F, Interface.Type.BOTTOM_CENTER));
      this.method6793(new ScoreboardHud("ScoreBoard", 0.0F, 0.0F, 960.0F, 495.5F, -10.0F, 10.0F, Interface.Type.CENTER_RIGHT));
      this.method6793(new SidebarHud("AnimatedTab"));
      this.method6793(new Hud2("ArmorHud", 0.0F, 0.0F, 960.0F, 495.5F, -10.0F, 10.0F, Interface.Type.CENTER_RIGHT));
      this.method6793(new Hud1("Totems", 0.0F, 0.0F, 960.0F, 495.5F, 0.0F, -40.0F, Interface.Type.BOTTOM_CENTER));
      this.method6793(new TargetPotionsHud("TargetPotions", 0.0F, 0.0F, 960.0F, 495.5F, 119.15234F, 90.0F, Interface.Type.TOP_LEFT));
      this.method6793(new EventsHud("Events", 116.5F, 265.0F, 960.0F, 495.5F, 0.0F, -16.5F, Interface.Type.BOTTOM_CENTER));
      this.method6793(new SessionStatsHud("SessionStats", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 182.0F, Interface.Type.TOP_LEFT));
      this.method6793(new PartyHud("Party", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 150.0F, Interface.Type.TOP_LEFT));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void toggle() {
      if (!this.isEnabledRaw()) {
         super.toggle();
      }
   }

   @Override
   public void setKeyCode(int keyCode) {
      super.setKeyCode(-1);
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      JsonObject jsonObject = new JsonObject();

      for (Setting setting : this.method6777()) {
         setting.safe(jsonObject);
      }

      json.add("HudLook", jsonObject);
      JsonObject json2 = new JsonObject();

      for (Interface interfaceValue : this.items) {
         json2.add(interfaceValue.getName(), interfaceValue.save());
      }

      json.add("HudElements", json2);
      return json;
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      if (json != null) {
         if (json.has("HudLook") && json.get("HudLook").isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject("HudLook");

            for (Setting setting : this.method6777()) {
               if (jsonObject.has(setting.getKey())) {
                  setting.load(jsonObject);
               }
            }
         }

         if (json.has("HudElements") && json.get("HudElements").isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject("HudElements");

            for (Interface interfaceValue : this.items) {
               String name = interfaceValue.getName();
               if (jsonObject.has(name) && jsonObject.get(name).isJsonObject()) {
                  interfaceValue.load(jsonObject.getAsJsonObject(name));
               }
            }
         }
      }
   }

   private void method6793(Interface hud3) {
      this.items.add(hud3);
      this.items2.put(hud3.getName(), hud3);
   }

   public List<Interface> method6794() {
      return Collections.unmodifiableList(this.items);
   }

   public boolean method6795(Interface key) {
      return this.class31.method4878(key) || key != null && key == this.interfaceValue;
   }

   public boolean method6796() {
      return this.saturation.isEnabled();
   }

   @Override
   public boolean isRenderSetting() {
      return false;
   }

   @EventTarget
   public void method6797(ScreenRenderEvent event) {
      if (mc.level != null && mc.gameMode != null && mc.player != null && !mc.gui.hud.isHidden()) {
         if (!(mc.gui.screen() instanceof ChatScreen)) {
            this.class31.cancel();
            if (this.interfaceValue != null) {
               this.interfaceValue = null;
            }

            this.method6804();
         }

         MenuDrawContext menuDrawContext = event.getMenuDrawContext();
         HudQueuedContext hudQueuedContext = HudQueuedContext.of(menuDrawContext);
         float guiScaledWidth = mc.getWindow().getGuiScaledWidth();
         float guiScaledHeight = mc.getWindow().getGuiScaledHeight();
         this.method6789();
         menuDrawContext.pushMatrix();
         Jebriksheet.method2346(this.jebriksheet);

         try {
            for (Interface key : this.items) {
               if (!this.class31.method4878(key)) {
                  key.method288(guiScaledWidth, guiScaledHeight);
               }

               if (this.method6800(key)) {
                  try {
                     key.method291(hudQueuedContext);
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }

            ArrayList arrayList = new ArrayList();

            for (Interface key : this.items) {
               if (this.method6800(key) && key.method296() > 0.0F && key.method297() > 0.0F) {
                  arrayList.add(key);
               }
            }

            UVPair uvPair = this.method6808();
            this.class31.method4880(hudQueuedContext, arrayList, uvPair.u(), uvPair.v(), guiScaledWidth, guiScaledHeight, mc.gui.screen() instanceof ChatScreen);
            this.jebriksheet.flush();
         } finally {
            Jebriksheet.method2347(this.jebriksheet);
         }

         boolean flag = mc.gui.screen() instanceof ChatScreen;
         UVPair uvPair = this.method6808();
         if (flag) {
            float floatValue = uvPair.u();
            float f = uvPair.v();
            if (this.interfaceValue != null) {
               float f2 = this.floatValue - this.floatValue3;
               float f3 = this.floatValue2 - this.floatValue4;
               float f4 = f2 * f2 + f3 * f3;
               if (f4 > 1.0F) {
                  float f5 = ((floatValue - this.floatValue3) * f2 + (f - this.floatValue4) * f3) / f4;
                  float floatValue2 = this.floatValue5 * f5;
                  floatValue2 = Math.clamp(floatValue2, this.interfaceValue.getScale().getMin(), this.interfaceValue.getScale().getMax());
                  this.interfaceValue.getScale().method2400(floatValue2);
               }
            }

            Interface interfaceValue = this.method6805(floatValue, f);
            if (this.interfaceValue == null && (interfaceValue == null || interfaceValue.method2059() || !interfaceValue.method2075(floatValue, f))) {
               this.method6804();
            } else {
               this.method6803();
            }
         } else {
            this.method6804();
         }

         menuDrawContext.popMatrix();
      }
   }

   private float method6798(Interface key) {
      return key.method294();
   }

   private float method6799(Interface key) {
      return key.method295();
   }

   private boolean method6800(Interface key) {
      return key.isEnabled();
   }

   public static float method6801() {
      return INSTANCE.round.getValue3();
   }

   @EventTarget
   public void method6802(MouseButtonEvent event) {
      if (!(mc.gui.screen() instanceof ChatScreen)) {
         this.class31.cancel();
         if (this.interfaceValue != null) {
            this.interfaceValue = null;
            this.method6804();
         }
      } else {
         UVPair uvPair = this.method6808();
         double floatValue = uvPair.u();
         double d = uvPair.v();
         if (event.getIntValue2() == 0 && event.getIntValue() == 1) {
            Interface selectedElement = this.method6805(floatValue, d);
            if (selectedElement != null) {
               this.class31.cancel();
               this.method6806(selectedElement);
            }
         } else {
            if (event.getIntValue2() == 1 && event.getIntValue() == 0) {
               Interface interfaceValue = this.method6805(floatValue, d);
               if (interfaceValue != null) {
                  if (interfaceValue.method2059()) {
                     interfaceValue.method2056(event);
                  } else if (interfaceValue.method2075(floatValue, d)) {
                     this.interfaceValue = interfaceValue;
                     this.floatValue = (float)floatValue;
                     this.floatValue2 = (float)d;
                     this.floatValue3 = interfaceValue.method294() + interfaceValue.method296() / 2.0F;
                     this.floatValue4 = interfaceValue.method295() + interfaceValue.method297() / 2.0F;
                     this.floatValue5 = interfaceValue.getScale().getValue3();
                  } else if (!interfaceValue.method2056(event)) {
                     this.class31.method4877(interfaceValue, (float)floatValue, (float)d);
                  }
               }
            } else if (event.getIntValue2() == 0) {
               if (this.interfaceValue != null) {
                  this.interfaceValue = null;
                  this.method6804();
               }

               this.class31.end();
            }
         }
      }
   }

   private void method6803() {
      if (!this.flag) {
         if (this.longValue == 0L) {
            this.longValue = org.lwjgl.sdl.SDLMouse.SDL_CreateSystemCursor(org.lwjgl.sdl.SDLMouse.SDL_SYSTEM_CURSOR_NWSE_RESIZE);
         }

         if (this.longValue != 0L) {
            org.lwjgl.sdl.SDLMouse.SDL_SetCursor(this.longValue);
            this.flag = true;
         }
      }
   }

   private void method6804() {
      if (this.flag) {
         org.lwjgl.sdl.SDLMouse.SDL_SetCursor(org.lwjgl.sdl.SDLMouse.SDL_GetDefaultCursor());
         this.flag = false;
      }
   }

   private Interface method6805(double floatValue, double d) {
      for (int i = this.items.size() - 1; i >= 0; i--) {
         Interface key = this.items.get(i);
         if (this.method6800(key) && key.method293(floatValue, d)) {
            return key;
         }
      }

      return null;
   }

   private void method6806(Interface selectedElement) {
      NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
      if (nlMenuScreen != null && selectedElement != null) {
         if (mc.gui.screen() != nlMenuScreen) {
            if (!MenuModule.INSTANCE.isEnabled()) {
               MenuModule.INSTANCE.toggle();
            } else {
               mc.gui.setScreen(nlMenuScreen);
            }
         }

         nlMenuScreen.openHudElementSettings(selectedElement);
      }
   }

   public float method6807() {
      return mc.getWindow().getGuiScale();
   }

   private UVPair method6808() {
      return Vec2Utils.method8068(this.method6807());
   }

   public org.joml.Vector2f method6809(float f, float f2) {
      float f3 = Float.MAX_VALUE;
      float f4 = Float.MAX_VALUE;
      float f5 = 2.0F;
      org.joml.Vector2f vec = new org.joml.Vector2f(-1.0F, -1.0F);
      float x = mc.getWindow().getGuiScaledWidth() / 2.0F;
      float x2 = mc.getWindow().getGuiScaledHeight() / 2.0F;
      float y = this.method6810(x, x, x, f);
      float y2 = this.method6810(x2, x2, x2, f2);
      float y3 = MathUtils.method2650(y, f);
      float y4 = MathUtils.method2650(y2, f2);
      boolean flag = false;
      if (y3 < f3 && y3 < f5) {
         vec.x = y;
         flag = true;
      }

      if (y4 < f4 && y4 < f5) {
         vec.y = y2;
         flag = true;
      }

      if (flag) {
         return vec;
      } else if (!this.guides.isEnabled()) {
         return vec;
      } else {
         for (Interface key : this.items) {
            if (!key.equals(this.class31.getInterfaceValue()) && this.method6800(key)) {
               y = this.method6798(key);
               y2 = this.method6799(key);
               y3 = y + key.method296();
               y4 = y2 + key.method297();
               float f6 = y + key.method296() / 2.0F;
               float f7 = y2 + key.method297() / 2.0F;
               float x3 = this.method6810(y, y3, f6, f);
               float y5 = this.method6810(y2, y4, f7, f2);
               float f8 = MathUtils.method2650(x3, f);
               float f9 = MathUtils.method2650(y5, f2);
               if (f8 < f3) {
                  f3 = f8;
                  if (f8 < f5) {
                     vec.x = x3;
                  }
               }

               if (f9 < f4) {
                  f4 = f9;
                  if (f9 < f5) {
                     vec.y = y5;
                  }
               }
            }
         }

         return vec;
      }
   }

   public float method6810(float x, float y, float f, float f2) {
      float x2 = x;
      if (MathUtils.method2650(y, f2) < MathUtils.method2650(x, f2)) {
         x2 = y;
      }

      if (MathUtils.method2650(f, f2) < MathUtils.method2650(x2, f2)) {
         x2 = f;
      }

      return x2;
   }

   public boolean method6811() {
      return this.method6815("ScoreBoard");
   }

   public boolean method6812() {
      return this.method6815("HootBar");
   }

   public boolean method6813() {
      return this.method6815("AnimatedTab");
   }

   public boolean method6814() {
      return this.method6815("Notifications");
   }

   private boolean method6815(String str) {
      Interface interfaceValue = this.items2.get(str);
      return interfaceValue != null && interfaceValue.isEnabled();
   }

   @EventTarget
   public void method6816(UpdateEvent event) {
      if (ShapeRenderer.hashMap.size() > 64) {
         ShapeRenderer.hashMap.values().removeIf(entry2 -> {
            if (entry2.method7214()) {
               entry2.method7215();
               return true;
            } else {
               return false;
            }
         });
      }

      for (Interface key : this.items) {
         if (this.method6800(key)) {
            try {
               key.tick();
            } catch (Throwable e) {
               System.out.println(key.getName());
               e.printStackTrace();
            }
         }
      }
   }

   public boolean method6817() {
      return this.entry.isEnabled();
   }

   public boolean method6818() {
      return this.entry2.isEnabled();
   }

   public boolean method6819() {
      return this.entry3.isEnabled();
   }

   public boolean method6820() {
      return this.entry4.isEnabled();
   }

   public boolean method6821() {
      return this.entry3.isEnabled() && this.glowMode.isSelected(0);
   }

   public boolean method6822() {
      return this.corners.isEnabled();
   }

   public int method6823() {
      return (int)this.glowRadius.getValue3();
   }

   public ZenithColor method6824() {
      return Zenith.getInstance().getStyleManager().getCurrentStyle().getGlareColor().getZenithColor();
   }

   public float method6825() {
      return this.glareSpeed.getValue3();
   }

   public int method6826() {
      int index = this.glareMode.getIndex();
      return index >= 0 && index < CONST_intValues.length ? CONST_intValues[index] : 0;
   }

   public int method6827() {
      return Math.max(0, this.glareTexture.getIndex());
   }

   public boolean method6828() {
      return this.method6815("Potions");
   }
}
