package org.zenith.client.scripting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public final class ScriptEventWatcher {
   private final Storage storage;
   private Level world;
   private float previousHealth = -1.0F;
   private boolean wasDead;
   private String serverAddress = "";
   private Screen screen;

   public ScriptEventWatcher(Storage storage) {
      this.storage = storage;
   }

   public void tick() {
      ScriptScreenApi.method7058();
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      if (level != this.world) {
         boolean joinedWorld = this.world == null && level != null;
         boolean leftWorld = this.world != null && level == null;
         this.world = level;
         if (joinedWorld) {
            String address = minecraft.getCurrentServer() == null ? "" : minecraft.getCurrentServer().ip;
            this.serverAddress = address;
            this.storage.method1332(Script.Type.SERVER_JOIN, script -> new Object[]{createEventData(script, "address", address)});
         }

         if (leftWorld) {
            String address = this.serverAddress;
            this.storage.method1332(Script.Type.SERVER_LEAVE, script -> new Object[]{createEventData(script, "address", address)});
         }

         if (level != null) {
            this.storage
               .method1332(Script.Type.WORLD_CHANGE, script -> new Object[]{createEventData(script, "dimension", level.dimension().identifier().toString())});
         }
      }

      if (minecraft.player != null) {
         float health = minecraft.player.getHealth();
         if (this.previousHealth >= 0.0F && health != this.previousHealth) {
            float oldHealth = this.previousHealth;
            this.storage.method1332(Script.Type.HEALTH_CHANGE, script -> new Object[]{createHealthChangeData(script, oldHealth, health)});
         }

         this.previousHealth = health;
         boolean dead = minecraft.player.isDeadOrDying() || health <= 0.0F;
         if (dead && !this.wasDead) {
            this.storage.method1332(Script.Type.DEATH, script -> new Object[]{createEventData(script, "health", 0.0)});
         }

         this.wasDead = dead;
      } else {
         this.previousHealth = -1.0F;
         this.wasDead = false;
      }

      Screen currentScreen = minecraft.gui.screen();
      if (currentScreen != this.screen) {
         String previousScreenName = ScriptScreenApi.method7047(this.screen);
         String screenName = ScriptScreenApi.method7047(currentScreen);
         this.screen = currentScreen;
         if (!previousScreenName.equals(screenName)) {
            this.storage.method1332(Script.Type.SCREEN, script -> {
               Object eventData = createEventData(script, "screen", screenName);
               ((Scriptable)eventData).put("previous", (Scriptable)eventData, previousScreenName);
               return new Object[]{eventData};
            });
         }
      }
   }

   private static Object createEventData(Script script, String name, Object value) {
      ScriptContextFactory.method7409();

      ScriptableObject eventData;
      try {
         eventData = ScriptValueUtils.method6247(script.scope).method6277(name, value).getScriptableObject();
      } finally {
         Context.exit();
      }

      return eventData;
   }

   private static Object createHealthChangeData(Script script, double previousHealth, double health) {
      ScriptContextFactory.method7409();

      ScriptableObject eventData;
      try {
         eventData = ScriptValueUtils.method6247(script.scope).method6277("from", previousHealth).method6277("to", health).getScriptableObject();
      } finally {
         Context.exit();
      }

      return eventData;
   }
}
