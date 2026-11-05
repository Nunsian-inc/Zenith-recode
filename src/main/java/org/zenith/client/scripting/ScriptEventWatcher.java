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
   private float floatValue = -1.0F;
   private boolean flag;
   private String text = "";
   private Screen screen;

   public ScriptEventWatcher(Storage storage) {
      this.storage = storage;
   }

   public void tick() {
      ScriptScreenApi.method7058();
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      if (level != this.world) {
         boolean flag = this.world == null && level != null;
         boolean flag2 = this.world != null && level == null;
         this.world = level;
         if (flag) {
            String ip = minecraft.getCurrentServer() == null ? "" : minecraft.getCurrentServer().ip;
            this.text = ip;
            this.storage.method1332(Script.Type.SERVER_JOIN, var1x -> new Object[]{method6415(var1x, "address", ip)});
         }

         if (flag2) {
            String text = this.text;
            this.storage.method1332(Script.Type.SERVER_LEAVE, var1x -> new Object[]{method6415(var1x, "address", text)});
         }

         if (level != null) {
            this.storage
               .method1332(Script.Type.WORLD_CHANGE, var1x -> new Object[]{method6415(var1x, "dimension", level.dimension().identifier().toString())});
         }
      }

      if (minecraft.player != null) {
         float health = minecraft.player.getHealth();
         if (this.floatValue >= 0.0F && health != this.floatValue) {
            float floatValue = this.floatValue;
            this.storage.method1332(Script.Type.HEALTH_CHANGE, var2x -> new Object[]{method6416(var2x, floatValue, health)});
         }

         this.floatValue = health;
         boolean flag = minecraft.player.isDeadOrDying() || health <= 0.0F;
         if (flag && !this.flag) {
            this.storage.method1332(Script.Type.DEATH, script -> new Object[]{method6415(script, "health", 0.0)});
         }

         this.flag = flag;
      } else {
         this.floatValue = -1.0F;
         this.flag = false;
      }

      Screen screen = minecraft.gui.screen();
      if (screen != this.screen) {
         String str = ScriptScreenApi.method7047(this.screen);
         String anObject = ScriptScreenApi.method7047(screen);
         this.screen = screen;
         if (!str.equals(anObject)) {
            this.storage.method1332(Script.Type.SCREEN, var2x -> {
               Object var3x = method6415(var2x, "screen", anObject);
               ((Scriptable)var3x).put("previous", (Scriptable)var3x, str);
               return new Object[]{var3x};
            });
         }
      }
   }

   private static Object method6415(Script script, String name, Object ip) {
      ScriptContextFactory.method7409();

      ScriptableObject scriptableObject;
      try {
         scriptableObject = ScriptValueUtils.method6247(script.scope).method6277(name, ip).getScriptableObject();
      } finally {
         Context.exit();
      }

      return scriptableObject;
   }

   private static Object method6416(Script script, double floatValue, double health) {
      ScriptContextFactory.method7409();

      ScriptableObject scriptableObject;
      try {
         scriptableObject = ScriptValueUtils.method6247(script.scope).method6277("from", floatValue).method6277("to", health).getScriptableObject();
      } finally {
         Context.exit();
      }

      return scriptableObject;
   }
}
