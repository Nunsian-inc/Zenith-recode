package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.screens.override.death.ZenithDeathScreen;
import org.zenith.client.settings.MultiSelectSetting;

@ModuleInfo(
   name = "BetterMinecraft",
   description = "module.betterminecraft.description",
   category = ModuleCategory.RENDER
)
public final class BetterMinecraftModule extends Module {
   public static final BetterMinecraftModule INSTANCE = new BetterMinecraftModule();
   public static final long CONST_longValue = 130L;
   public static final long CONST_longValue2 = 110L;
   public static final float CONST_floatValue = 0.86F;
   private final MultiSelectSetting animate = new MultiSelectSetting("module.betterMinecraft.animate");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.animate, "module.betterMinecraft.animate.storages", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.animate, "module.betterMinecraft.animate.inventory", true);
   private final MultiSelectSetting chat = new MultiSelectSetting("module.betterMinecraft.chat", "module.betterMinecraft.chat.desc");
   private final MultiSelectSetting.Entry antiSpam = new MultiSelectSetting.Entry(this.chat, "module.betterMinecraft.chat.antiSpam", true);
   private final MultiSelectSetting screens = new MultiSelectSetting("module.betterMinecraft.screen", "module.betterMinecraft.screen.desc");
   private final MultiSelectSetting.Entry deathScreen = new MultiSelectSetting.Entry(this.screens, "module.betterMinecraft.screen.death", true);
   private ZenithDeathScreen lastDeathScreen;

   private BetterMinecraftModule() {
      this.setEnabled(true);
      // setEnabled меняет только флаг; обработчики событий сами так не регистрируются.
      EventManager.register(this);
   }

   @Override
   public void load(JsonObject json) {
      super.load(json);
      this.setToggled(true);
   }

   @Override
   public JsonObject save() {
      JsonObject json = super.save();
      json.addProperty("enabled", true);
      return json;
   }

   public boolean method2010() {
      return this.isEnabled() && this.entry.isEnabled();
   }

   public boolean method2011() {
      return this.isEnabled() && this.entry2.isEnabled();
   }

   /** Антиспам чата: одинаковые сообщения подряд схлопываются в одно со счётчиком «(x3)» (см. MixinChatComponent). */
   public boolean isAntiSpamEnabled() {
      return this.isEnabled() && this.antiSpam.isEnabled();
   }

   public boolean isDeathScreenEnabled() {
      return this.isEnabled() && this.deathScreen.isEnabled();
   }

   @EventTarget
   public void onSetScreen(SetScreenEvent event) {
      if (this.isDeathScreenEnabled() && mc.player != null && mc.level != null
         && event.getScreen() instanceof DeathScreen vanillaScreen && !(vanillaScreen instanceof ZenithDeathScreen)) {
         event.setScreen(this.createDeathScreen(vanillaScreen.causeOfDeath, mc.level.getLevelData().isHardcore(), mc.player));
      }
   }

   public ZenithDeathScreen createDeathScreen(Component causeOfDeath, boolean hardcore, LocalPlayer player) {
      // Gui#setScreen(null) повторно создаёт смерть с null-причиной: сохраняем исходные данные и позицию.
      if (causeOfDeath != null || this.lastDeathScreen == null || !this.lastDeathScreen.isFor(player)) {
         this.lastDeathScreen = new ZenithDeathScreen(causeOfDeath, hardcore, player);
      }
      return this.lastDeathScreen;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player == null || !mc.player.isDeadOrDying() || this.lastDeathScreen != null && !this.lastDeathScreen.isFor(mc.player)) {
         this.lastDeathScreen = null;
      }
   }

   public float method2012(long zenith$openedAt) {
      float f = this.method2015((float)(System.currentTimeMillis() - zenith$openedAt) / 130.0F);
      return 0.86F + 0.13999999F * this.method2016(f);
   }

   public float method2013(long zenith$closedAt, float zenith$closeStartScale) {
      float f = this.method2015((float)(System.currentTimeMillis() - zenith$closedAt) / 110.0F);
      return zenith$closeStartScale * (1.0F - this.method2016(f));
   }

   public boolean method2014(long zenith$closedAt) {
      return System.currentTimeMillis() - zenith$closedAt >= 110L;
   }

   private float method2015(float f) {
      return Math.clamp(f, 0.0F, 1.0F);
   }

   private float method2016(float f) {
      return f < 0.5F ? 2.0F * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 2.0) / 2.0F;
   }
}
