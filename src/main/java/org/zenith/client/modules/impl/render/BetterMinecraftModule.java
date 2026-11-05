package org.zenith.client.modules.impl.render;

import com.google.gson.JsonObject;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
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

   private BetterMinecraftModule() {
      this.setEnabled(true);
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
