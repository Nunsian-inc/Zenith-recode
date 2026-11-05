package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.zenith.Zenith;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;

public final class View {
   private static final String text = "view.json";
   public final BooleanSetting renderSelf = new BooleanSetting("cosmetics.renderSelf", "cosmetics.renderSelf.desc", true);
   public final BooleanSetting renderOthers = new BooleanSetting("cosmetics.renderOthers", "cosmetics.renderOthers.desc", true);
   public final SliderSetting petScale = new SliderSetting("cosmetics.petScale", 1.35F, 0.4F, 4.0F, 0.05F, "cosmetics.petScale.desc", "x", null, null);
   private final Cosmetics cosmetics;
   private final Path path;
   private String lastSignature = "";

   public View(Cosmetics cosmetics, Path path) {
      this.cosmetics = Objects.requireNonNull(cosmetics, "manager");
      this.path = path.resolve("cosmetics").resolve("view.json");
      this.load();
      this.lastSignature = this.method7331();
      EventManager.register(this);
   }

   public List<Setting> method7327() {
      return List.of(this.renderSelf, this.renderOthers, this.petScale);
   }

   public static boolean method7328() {
      View view = method7330();
      return view == null || view.renderSelf.isEnabled();
   }

   public static boolean method7329() {
      View view = method7330();
      return view == null || view.renderOthers.isEnabled();
   }

   private static View method7330() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getView();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      float f = this.petScale.getValue3();
      if (Math.abs(f - this.cosmetics.getFloatValue()) > 0.001F) {
         this.cosmetics.method3003(f);
      }

      String lastSignature = this.method7331();
      if (!lastSignature.equals(this.lastSignature)) {
         this.lastSignature = lastSignature;
         this.save();
      }
   }

   private String method7331() {
      return this.renderSelf.isEnabled() + "|" + this.renderOthers.isEnabled() + "|" + this.petScale.getValue3();
   }

   private void load() {
      JsonObject json = JsonIo.method1009(this.path);
      if (json == null) {
         this.petScale.method2400(this.cosmetics.getFloatValue());
      } else {
         if (json.has("renderSelf")) {
            this.renderSelf.setEnabled(json.get("renderSelf").getAsBoolean());
         }

         if (json.has("renderOthers")) {
            this.renderOthers.setEnabled(json.get("renderOthers").getAsBoolean());
         }

         this.petScale.method2400(json.has("petScale") ? json.get("petScale").getAsFloat() : this.cosmetics.getFloatValue());
      }
   }

   private void save() {
      JsonObject json = new JsonObject();
      json.addProperty("renderSelf", this.renderSelf.isEnabled());
      json.addProperty("renderOthers", this.renderOthers.isEnabled());
      json.addProperty("petScale", this.petScale.getValue3());
      JsonIo.method1010(this.path, json);
   }
}
