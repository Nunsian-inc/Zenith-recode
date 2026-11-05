package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptGuiApi {
   private static final int CONST_intValue = 2500;
   private static final int CONST_intValue2 = 400;
   private static final int CONST_intValue3 = 64;
   private static final List<ScriptGuiApi.Entry> items = new CopyOnWriteArrayList<>();
   private static final List<ScriptGuiApi.Data> items2 = new ArrayList<>();
   private static final List<ScriptGuiApi.Data2> items3 = new CopyOnWriteArrayList<>();
   private static final Map<String, ScriptGuiApi.Entry3> items4 = new ConcurrentHashMap<>();
   private static boolean flag;
   private static final Map<Script, Long> items5 = new WeakHashMap<>();

   private ScriptGuiApi() {
   }

   public static synchronized void method5309() {
      if (!flag) {
         flag = true;
         EventManager.main().subscribe(TickEvent.class, (byte)4, (obj, obj2) -> tick());
         EventManager.main().subscribe(DrawEvent.class, (byte)4, (obj, obj2) -> method5312((DrawEvent)obj2));
         EventManager.main().subscribe(WorldRenderEvent.class, (byte)4, (obj, obj2) -> method5314());
         EventManager.main().subscribe(MouseButtonEvent.class, (byte)0, (obj, obj2) -> method5318((MouseButtonEvent)obj2));
      }
   }

   private static boolean method5310(ScriptGuiApi.Entry entry) {
      return entry.intValue2 >= entry.intValue || !entry.script.isRunning();
   }

   private static void tick() {
      boolean flag = false;

      for (ScriptGuiApi.Entry entry : items) {
         entry.intValue2++;
         if (method5310(entry)) {
            flag = true;
         } else {
            entry.doubleValue5 = entry.doubleValue5 - entry.doubleValue7;
            entry.doubleValue4 = entry.doubleValue4 * entry.doubleValue8;
            entry.doubleValue5 = entry.doubleValue5 * entry.doubleValue8;
            entry.doubleValue6 = entry.doubleValue6 * entry.doubleValue8;
            entry.doubleValue = entry.doubleValue + entry.doubleValue4;
            entry.doubleValue2 = entry.doubleValue2 + entry.doubleValue5;
            entry.doubleValue3 = entry.doubleValue3 + entry.doubleValue6;
            entry.floatValue2 = entry.floatValue2 + entry.floatValue;
         }
      }

      if (flag) {
         items.removeIf(ScriptGuiApi::method5310);
      }
   }

   private static float method5311(int guiScaledHeight, double d, double d2) {
      return (float)(guiScaledHeight / (d * Math.max(0.3, d2)));
   }

   private static void method5312(DrawEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      CustomDrawContext customDrawContext = event.getCustomDrawContext();
      if (minecraft.player != null && (!items.isEmpty() || !items2.isEmpty() || !items3.isEmpty())) {
         Vec3 position = minecraft.gameRenderer.mainCamera().position();
         float tickDelta = event.getTickDelta();
         int guiScaledHeight = minecraft.getWindow().getGuiScaledHeight();
         double d = 2.0 * Math.tan(Math.toRadians(minecraft.options.fov().get().intValue()) / 2.0);

         for (ScriptGuiApi.Entry entry : items) {
            double x = entry.doubleValue + entry.doubleValue4 * tickDelta;
            double y = entry.doubleValue2 + entry.doubleValue5 * tickDelta;
            double z = entry.doubleValue3 + entry.doubleValue6 * tickDelta;
            Vec3 vec = new Vec3(x, y, z);
            Vec3 vec2 = ProjectionUtils.method301(vec);
            if (!(vec2.z <= 0.0) && !(vec2.z >= 1.0)) {
               float size = entry.size * method5311(guiScaledHeight, d, position.distanceTo(vec));
               if (!(size < 0.5F) && !(size > 600.0F)) {
                  float f = entry.flag ? 1.0F - (entry.intValue2 + tickDelta) / entry.intValue : 1.0F;
                  ZenithColor zenithColor = new ZenithColor(entry.color).method7294(Math.max(0.0F, f));
                  if (entry.entry != null && !entry.entry.flag) {
                     Identifier texture = entry.entry.texture();
                     if (texture != null) {
                        float height = size * entry.entry.height() / Math.max(1, entry.entry.width());
                        customDrawContext.pose().pushMatrix();
                        customDrawContext.pose().translate((float)vec2.x, (float)vec2.y);
                        if (entry.floatValue2 != 0.0F) {
                           customDrawContext.pose().rotate((float)Math.toRadians(entry.floatValue2));
                        }

                        customDrawContext.drawRoundedTexture(texture, -size / 2.0F, -height / 2.0F, size, height, ProxyDotRadiusData.method8069(0.0F), zenithColor);
                        customDrawContext.pose().popMatrix();
                     }
                  } else if (entry.text != null) {
                     Font font = Fonts.NEW_SEMIBOLD.getFont(Math.max(3.0F, Math.min(48.0F, size)));
                     customDrawContext.drawText(font, entry.text, (float)vec2.x - font.width(entry.text) / 2.0F, (float)vec2.y - font.height() / 2.0F, zenithColor);
                  }
               }
            }
         }

         for (ScriptGuiApi.Data data : items2) {
            method5313(
               customDrawContext, position, guiScaledHeight, d, data.text, data.doubleValue, data.doubleValue2, data.doubleValue3, data.floatValue, data.intValue, data.intValue2
            );
         }

         items2.clear();

         for (ScriptGuiApi.Data2 data2 : items3) {
            if (data2.script.isRunning()) {
               double d2 = minecraft.player.position().distanceTo(new Vec3(data2.doubleValue, data2.doubleValue2, data2.doubleValue3));
               String text = data2.text + "  " + (int)d2 + "m";
               Vec3 vec = ProjectionUtils.method301(new Vec3(data2.doubleValue, data2.doubleValue2 + 1.2, data2.doubleValue3));
               if (!(vec.z <= 0.0) && !(vec.z >= 1.0)) {
                  Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
                  float width = font.width(text) + 10.0F;
                  customDrawContext.drawRoundedRect(
                     (float)vec.x - width / 2.0F, (float)vec.y - 6.0F, width, 12.0F, ProxyDotRadiusData.method8069(6.0F), new ZenithColor(-1341124588)
                  );
                  customDrawContext.drawRoundedRect(
                     (float)vec.x - width / 2.0F + 3.0F,
                     (float)vec.y - 1.5F,
                     3.0F,
                     3.0F,
                     ProxyDotRadiusData.method8069(1.5F),
                     new ZenithColor(data2.intValue)
                  );
                  customDrawContext.drawText(font, text, (float)vec.x - width / 2.0F + 8.0F, (float)vec.y - font.height() / 2.0F, new ZenithColor(-1));
               }
            }
         }
      } else {
         items2.clear();
      }

      method5317(customDrawContext);
   }

   private static void method5313(
      CustomDrawContext customDrawContext, Vec3 position, int guiScaledHeight, double d, String text, double doubleValue, double y, double z, float floatValue, int intValue, int n
   ) {
      Vec3 vec = new Vec3(doubleValue, y, z);
      Vec3 vec2 = ProjectionUtils.method301(vec);
      if (!(vec2.z <= 0.0) && !(vec2.z >= 1.0)) {
         float px = Math.max(3.0F, Math.min(40.0F, floatValue * method5311(guiScaledHeight, d, position.distanceTo(vec)) * 0.25F));
         Font font = Fonts.NEW_MEDIUM.getFont(px);
         float width = font.width(text);
         if (n >>> 24 != 0) {
            customDrawContext.drawRoundedRect(
               (float)vec2.x - width / 2.0F - px * 0.4F,
               (float)vec2.y - font.height() / 2.0F - px * 0.25F,
               width + px * 0.8F,
               font.height() + px * 0.5F,
               ProxyDotRadiusData.method8069(px * 0.3F),
               new ZenithColor(n)
            );
         }

         customDrawContext.drawText(font, text, (float)vec2.x - width / 2.0F, (float)vec2.y - font.height() / 2.0F, new ZenithColor(intValue));
      }
   }

   private static void method5314() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null) {
         for (ScriptGuiApi.Data2 data2 : items3) {
            if (data2.flag && data2.script.isRunning()) {
               GlowUtil.method2245(
                  new Vec3(data2.doubleValue, data2.doubleValue2, data2.doubleValue3),
                  new Vec3(data2.doubleValue, data2.doubleValue2 + 64.0, data2.doubleValue3),
                  data2.intValue,
                  2.0F,
                  false
               );
            }
         }
      }
   }

   private static float method5315() {
      Minecraft minecraft = Minecraft.getInstance();
      return (float)(minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / Math.max(1, minecraft.getWindow().getScreenWidth()));
   }

   private static float method5316() {
      Minecraft minecraft = Minecraft.getInstance();
      return (float)(minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / Math.max(1, minecraft.getWindow().getScreenHeight()));
   }

   private static void method5317(CustomDrawContext customDrawContext) {
      if (!items4.isEmpty()) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean flag = minecraft.gui.screen() instanceof ChatScreen;
         float f = method5315();
         float f2 = method5316();

         for (ScriptGuiApi.Entry3 entry3 : items4.values()) {
            if (entry3.script.isRunning() && entry3.visible) {
               if (entry3.dragging) {
                  entry3.floatValue = Math.max(0.0F, Math.min(minecraft.getWindow().getGuiScaledWidth() - entry3.floatValue3, f - entry3.floatValue5));
                  entry3.floatValue2 = Math.max(0.0F, Math.min((float)(minecraft.getWindow().getGuiScaledHeight() - 12), f2 - entry3.floatValue6));
               }

               float f3 = entry3.flag ? 12.0F : 0.0F;
               if (entry3.flag) {
                  customDrawContext.drawBlurHud(
                     entry3.floatValue,
                     entry3.floatValue2,
                     entry3.floatValue3,
                     entry3.floatValue4 + f3,
                     12.0F,
                     ProxyDotRadiusData.method8069(5.0F),
                     new ZenithColor(2047609874)
                  );
                  customDrawContext.drawRoundedBorder(
                     entry3.floatValue,
                     entry3.floatValue2,
                     entry3.floatValue3,
                     entry3.floatValue4 + f3,
                     0.5F,
                     ProxyDotRadiusData.method8069(5.0F),
                     new ZenithColor(flag ? 1728053247 : 587202559)
                  );
                  Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
                  customDrawContext.drawText(
                     font,
                     font.clip(entry3.title, entry3.floatValue3 - 10.0F),
                     entry3.floatValue + 5.0F,
                     entry3.floatValue2 + (f3 - font.height()) / 2.0F,
                     new ZenithColor(-419430401)
                  );
                  if (flag) {
                     customDrawContext.drawRoundedRect(
                        entry3.floatValue + entry3.floatValue3 - 9.0F,
                        entry3.floatValue2 + 4.5F,
                        4.0F,
                        4.0F,
                        ProxyDotRadiusData.method8069(2.0F),
                        new ZenithColor(-1426063361)
                     );
                  }
               }

               if (entry3.function != null) {
                  customDrawContext.pose().pushMatrix();
                  customDrawContext.pose().translate(entry3.floatValue, entry3.floatValue2 + f3);
                  ScriptDrawApi.method2597(customDrawContext);
                  ScriptImGui.method635(entry3.floatValue, entry3.floatValue2 + f3);

                  try {
                     customDrawContext.enableScissor(0.0F, 0.0F, entry3.floatValue3, entry3.floatValue4);
                     entry3.storage
                        .method1321(
                           entry3.script,
                           entry3.function,
                           (double)entry3.floatValue3,
                           (double)entry3.floatValue4,
                           (double)(f - entry3.floatValue),
                           (double)(f2 - entry3.floatValue2 - f3)
                        );
                  } finally {
                     ScriptImGui.method635(0.0F, 0.0F);
                     customDrawContext.disableScissor();
                     ScriptDrawApi.end();
                     customDrawContext.pose().popMatrix();
                  }
               }
            }
         }
      }
   }

   private static void method5318(MouseButtonEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.gui.screen() instanceof ChatScreen && !items4.isEmpty()) {
         float f = method5315();
         float f2 = method5316();
         boolean flag = event.getIntValue2() == 1;

         for (ScriptGuiApi.Entry3 entry3 : items4.values()) {
            if (flag) {
               if (entry3.visible && entry3.script.isRunning()) {
                  float f3 = entry3.flag ? 12.0F : 0.0F;
                  boolean flag2 = f >= entry3.floatValue
                     && f <= entry3.floatValue + entry3.floatValue3
                     && f2 >= entry3.floatValue2
                     && f2 <= entry3.floatValue2 + (entry3.flag ? f3 : entry3.floatValue4);
                  boolean flag3 = f >= entry3.floatValue
                     && f <= entry3.floatValue + entry3.floatValue3
                     && f2 >= entry3.floatValue2 + f3
                     && f2 <= entry3.floatValue2 + f3 + entry3.floatValue4;
                  if (event.getIntValue() == 0 && flag2 && (entry3.flag || entry3.function2 == null)) {
                     entry3.dragging = true;
                     entry3.floatValue5 = f - entry3.floatValue;
                     entry3.floatValue6 = f2 - entry3.floatValue2;
                     event.setCancelled(true);
                     return;
                  }

                  if (flag3 && entry3.function2 != null) {
                     entry3.storage
                        .method1321(
                           entry3.script,
                           entry3.function2,
                           (double)(f - entry3.floatValue),
                           (double)(f2 - entry3.floatValue2 - f3),
                           (double)event.getIntValue()
                        );
                     event.setCancelled(true);
                     return;
                  }
               }
            } else if (entry3.dragging) {
               entry3.dragging = false;
               method5319(entry3);
            }
         }
      } else {
         for (ScriptGuiApi.Entry3 entry3 : items4.values()) {
            entry3.dragging = false;
         }
      }
   }

   private static void method5319(ScriptGuiApi.Entry3 entry3) {
      JsonObject json = new JsonObject();
      json.addProperty("x", entry3.floatValue);
      json.addProperty("y", entry3.floatValue2);
      entry3.script.method1415().method1008("__window." + entry3.name, json);
   }

   public static void method5320(Script workspace, Storage storage, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "image",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            RemoteUtil.Entry var2x = RemoteUtil.method1827(ScriptValueUtils.arg(var0x, 0));
            Identifier var3x = var2x.texture();
            if (var3x == null) {
               return null;
            } else {
               float var4x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
               float f = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
               float width = (float)ScriptValueUtils.method6256(var0x, 3, var2x.width());
               float height = (float)ScriptValueUtils.method6256(var0x, 4, var2x.height());
               Scriptable scriptable = ScriptValueUtils.method6262(var0x, 5);
               float intValue = scriptable == null ? 0.0F : (float)ScriptValueUtils.method6264(scriptable, "radius", 0.0);
               int n = scriptable == null ? -1 : (int)((long)ScriptValueUtils.method6264(scriptable, "color", 4.294967295E9));
               double angdeg = scriptable == null ? 0.0 : ScriptValueUtils.method6264(scriptable, "rotation", 0.0);
               float f2 = scriptable == null ? 0.0F : (float)ScriptValueUtils.method6264(scriptable, "u1", 0.0);
               float f3 = scriptable == null ? 0.0F : (float)ScriptValueUtils.method6264(scriptable, "v1", 0.0);
               float f4 = scriptable == null ? 1.0F : (float)ScriptValueUtils.method6264(scriptable, "u2", 1.0);
               float f5 = scriptable == null ? 1.0F : (float)ScriptValueUtils.method6264(scriptable, "v2", 1.0);
               var1x.pose().pushMatrix();

               try {
                  var1x.pose().translate(var4x + width / 2.0F, f + height / 2.0F);
                  if (angdeg != 0.0) {
                     var1x.pose().rotate((float)Math.toRadians(angdeg));
                  }

                  var1x.drawRoundedTexture(
                     var3x, -width / 2.0F, -height / 2.0F, width, height, ProxyDotRadiusData.method8069(intValue), new ZenithColor(n), f2, f3, f4, f5
                  );
               } finally {
                  var1x.pose().popMatrix();
               }

               return null;
            }
         }
      );
      entry2.method6274(
         "head",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            Minecraft var2x = Minecraft.getInstance();
            if (var2x.getConnection() == null) {
               return false;
            } else {
               Object var3x = ScriptValueUtils.arg(var0x, 0);
               String var4x = var3x instanceof Scriptable
                  ? String.valueOf(ScriptScreenApi.method7048(var3x) == null ? "" : ScriptScreenApi.method7048(var3x).getName().getString())
                  : ScriptValueUtils.method6255(var3x);
               PlayerInfo playerInfo = var2x.getConnection().getPlayerInfo(var4x);
               if (playerInfo == null) {
                  return false;
               } else {
                  float width = (float)ScriptValueUtils.method6256(var0x, 3, 16.0);
                  var1x.drawPlayerHeadWithRoundedShader(
                     playerInfo.getSkin().body().texturePath(),
                     (float)ScriptValueUtils.method6256(var0x, 1, 0.0),
                     (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                     width,
                     ProxyDotRadiusData.method8069((float)ScriptValueUtils.method6256(var0x, 4, width * 0.2F)),
                     new ZenithColor(-1)
                  );
                  return true;
               }
            }
         }
      );
      entry2.method6274("gradient", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         int var2x = (int)ScriptValueUtils.method6256(var0x, 0, 0.0);
         int var3x = (int)ScriptValueUtils.method6256(var0x, 1, 0.0);
         int var4x = (int)ScriptValueUtils.method6256(var0x, 2, 0.0);
         int n = (int)ScriptValueUtils.method6256(var0x, 3, 0.0);
         int col1 = ScriptValueUtils.method6258(var0x, 4, -1);
         int col2 = ScriptValueUtils.method6258(var0x, 5, -16777216);
         if (ScriptValueUtils.method6259(var0x, 6, true)) {
            var1x.fillGradient(var2x, var3x, var2x + var4x, var3x + n, col1, col2);
         } else {
            int n2 = Math.max(1, Math.min(64, var4x));

            for (int i = 0; i < n2; i++) {
               int col = method5321(col1, col2, (double)i / Math.max(1, n2 - 1));
               int x0 = var2x + var4x * i / n2;
               int x1 = var2x + var4x * (i + 1) / n2;
               var1x.fill(x0, var3x, x1, var3x + n, col);
            }
         }

         return null;
      });
      entry2.method6274(
         "mouse",
         var1x -> ScriptValueUtils.method6247(scope)
            .method6277("x", (double)method5315())
            .method6277("y", (double)method5316())
            .method6277("left", org.zenith.utility.misc.KeyCompat.isMouseDown(0))
            .method6277("right", org.zenith.utility.misc.KeyCompat.isMouseDown(1))
            .getScriptableObject()
      );
   }

   private static int method5321(int col1, int col2, double d) {
      int n = (int)Math.round((col1 >>> 24) + ((col2 >>> 24) - (col1 >>> 24)) * d);
      int n2 = (int)Math.round((col1 >> 16 & 0xFF) + ((col2 >> 16 & 0xFF) - (col1 >> 16 & 0xFF)) * d);
      int n3 = (int)Math.round((col1 >> 8 & 0xFF) + ((col2 >> 8 & 0xFF) - (col1 >> 8 & 0xFF)) * d);
      int n4 = (int)Math.round((col1 & 0xFF) + ((col2 & 0xFF) - (col1 & 0xFF)) * d);
      return n << 24 | n2 << 16 | n3 << 8 | n4;
   }

   public static void method5322(Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "text",
         var0x -> {
            if (items2.size() >= 400) {
               return null;
            } else {
               Scriptable var1x = ScriptValueUtils.method6262(var0x, 4);
               float var2x = var1x == null ? (float)ScriptValueUtils.method6256(var0x, 4, 1.0) : (float)ScriptValueUtils.method6264(var1x, "scale", 1.0);
               int intValue = var1x == null ? -1 : (int)((long)ScriptValueUtils.method6264(var1x, "color", 4.294967295E9));
               int n = var1x == null ? Integer.MIN_VALUE : (int)((long)ScriptValueUtils.method6264(var1x, "background", 2.1474836E9F));
               items2.add(
                  new ScriptGuiApi.Data(
                     ScriptValueUtils.method6254(var0x, 0, ""),
                     ScriptValueUtils.method6256(var0x, 1, 0.0),
                     ScriptValueUtils.method6256(var0x, 2, 0.0),
                     ScriptValueUtils.method6256(var0x, 3, 0.0),
                     var2x,
                     intValue,
                     n
                  )
               );
               return null;
            }
         }
      );
      entry2.method6274("circle", var0x -> {
         ScriptDrawApi.method2602();
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         double y = ScriptValueUtils.method6256(var0x, 1, 0.0);
         double d = ScriptValueUtils.method6256(var0x, 2, 0.0);
         double d2 = ScriptValueUtils.method6256(var0x, 3, 1.0);
         int color = ScriptValueUtils.method6258(var0x, 4, -1);
         float width = (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var0x, 5, 2.0)));
         int n = Math.max(6, Math.min(128, ScriptValueUtils.method6257(var0x, 6, 48)));
         Vec3 vec = null;

         for (int i = 0; i <= n; i++) {
            double d3 = (Math.PI * 2) * i / n;
            Vec3 vec2 = new Vec3(var1x + Math.cos(d3) * d2, y, d + Math.sin(d3) * d2);
            if (vec != null) {
               GlowUtil.method2245(vec, vec2, color, width, false);
            }

            vec = vec2;
         }

         return null;
      });
      entry2.method6274(
         "path",
         var0x -> {
            ScriptDrawApi.method2602();
            var var1x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0));
            int var2x = ScriptValueUtils.method6258(var0x, 1, -1);
            float width = (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var0x, 2, 2.0)));
            Vec3 vec = null;
            int n = 0;

            for (Object id : var1x) {
               if (++n > 2048) {
                  break;
               }

               var list = ScriptValueUtils.method6269(id);
               Vec3 vec2;
               if (list.size() >= 3) {
                  vec2 = new Vec3(
                     ScriptValueUtils.method6256(new Object[]{list.get(0)}, 0, 0.0),
                     ScriptValueUtils.method6256(new Object[]{list.get(1)}, 0, 0.0),
                     ScriptValueUtils.method6256(new Object[]{list.get(2)}, 0, 0.0)
                  );
               } else {
                  if (!(id instanceof Scriptable scriptable)) {
                     continue;
                  }

                  vec2 = new Vec3(
                     ScriptValueUtils.method6264(scriptable, "x", 0.0), ScriptValueUtils.method6264(scriptable, "y", 0.0), ScriptValueUtils.method6264(scriptable, "z", 0.0)
                  );
               }

               if (vec != null) {
                  GlowUtil.method2245(vec, vec2, var2x, width, false);
               }

               vec = vec2;
            }

            return null;
         }
      );
   }

   public static Scriptable method5323(Script script, Storage storage, Scriptable scope) {
      method5309();
      script.method1413(() -> items.removeIf(var1x -> var1x.script == script));
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "spawn",
         var0x -> {
            Minecraft var1x = Minecraft.getInstance();
            if (var1x.level == null) {
               return false;
            } else {
               String var2x = ScriptValueUtils.method6254(var0x, 0, "flame");
               Identifier var3x = Identifier.tryParse(var2x.contains(":") ? var2x : "minecraft:" + var2x);
               if (var3x != null && BuiltInRegistries.PARTICLE_TYPE.containsKey(var3x)) {
                  if (BuiltInRegistries.PARTICLE_TYPE.getValue(var3x) instanceof SimpleParticleType var4x) {
                     int n = Math.max(1, Math.min(200, ScriptValueUtils.method6257(var0x, 7, 1)));
                     double d = ScriptValueUtils.method6256(var0x, 8, 0.0);

                     for (int i = 0; i < n; i++) {
                        double d2 = d == 0.0 ? 0.0 : (Math.random() - 0.5) * d;
                        double d3 = d == 0.0 ? 0.0 : (Math.random() - 0.5) * d;
                        double d4 = d == 0.0 ? 0.0 : (Math.random() - 0.5) * d;
                        var1x.level
                           .addParticle(
                              var4x,
                              ScriptValueUtils.method6256(var0x, 1, 0.0) + d2,
                              ScriptValueUtils.method6256(var0x, 2, 0.0) + d3,
                              ScriptValueUtils.method6256(var0x, 3, 0.0) + d4,
                              ScriptValueUtils.method6256(var0x, 4, 0.0),
                              ScriptValueUtils.method6256(var0x, 5, 0.0),
                              ScriptValueUtils.method6256(var0x, 6, 0.0)
                           );
                     }

                     return true;
                  } else {
                     throw ScriptValueUtils.method6251("частица " + var2x + " требует параметров — возьми простую");
                  }
               } else {
                  throw ScriptValueUtils.method6251("нет частицы " + var2x + " (список — particles.ids())");
               }
            }
         }
      );
      entry2.method6274("ids", var1x -> {
         ArrayList var2x = new ArrayList();

         for (Identifier var4x : BuiltInRegistries.PARTICLE_TYPE.keySet()) {
            if (BuiltInRegistries.PARTICLE_TYPE.getValue(var4x) instanceof SimpleParticleType) {
               var2x.add(var4x.getPath());
            }
         }

         var2x.sort(null);
         return ScriptValueUtils.method6267(scope, var2x);
      });
      BiFunction<Object[], Boolean, Object> biFunction = (var1x, var2x) -> {
         if (items.size() >= 2500) {
            return false;
         } else {
            ScriptGuiApi.Entry var3x = new ScriptGuiApi.Entry();
            var3x.script = script;
            if (var2x) {
               var3x.text = ScriptValueUtils.method6254(var1x, 0, "");
            } else {
               var3x.entry = RemoteUtil.method1827(ScriptValueUtils.arg(var1x, 0));
            }

            var3x.doubleValue = ScriptValueUtils.method6256(var1x, 1, 0.0);
            var3x.doubleValue2 = ScriptValueUtils.method6256(var1x, 2, 0.0);
            var3x.doubleValue3 = ScriptValueUtils.method6256(var1x, 3, 0.0);
            Scriptable var4x = ScriptValueUtils.method6262(var1x, 4);
            var3x.doubleValue4 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "vx", 0.0) / 20.0;
            var3x.doubleValue5 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "vy", 0.0) / 20.0;
            var3x.doubleValue6 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "vz", 0.0) / 20.0;
            var3x.doubleValue7 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "gravity", 0.0) / 400.0;
            var3x.doubleValue8 = var4x == null ? 1.0 : Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6264(var4x, "drag", 1.0)));
            var3x.size = var4x == null ? 0.5F : (float)Math.max(0.01, Math.min(20.0, ScriptValueUtils.method6264(var4x, "size", 0.5)));
            var3x.floatValue = var4x == null ? 0.0F : (float)(ScriptValueUtils.method6264(var4x, "spin", 0.0) / 20.0);
            var3x.intValue = var4x == null ? 40 : (int)Math.max(1.0, Math.min(1200.0, ScriptValueUtils.method6264(var4x, "life", 2.0) * 20.0));
            var3x.flag = var4x == null || ScriptValueUtils.method6265(var4x, "fade", true);
            var3x.color = var4x == null ? -1 : (int)((long)ScriptValueUtils.method6264(var4x, "color", 4.294967295E9));
            items.add(var3x);
            return true;
         }
      };
      entry2.method6274("image", var1x -> biFunction.apply(var1x, false));
      entry2.method6274("text", var1x -> biFunction.apply(var1x, true));
      entry2.method6274("burst", var1x -> {
         RemoteUtil.Entry var2x = ScriptValueUtils.arg(var1x, 0) instanceof String ? null : RemoteUtil.method1827(ScriptValueUtils.arg(var1x, 0));
         String var3x = var2x == null ? ScriptValueUtils.method6254(var1x, 0, "") : null;
         int var4x = Math.max(1, Math.min(200, ScriptValueUtils.method6257(var1x, 4, 12)));
         double d = ScriptValueUtils.method6256(var1x, 5, 3.0);
         Scriptable scriptable = ScriptValueUtils.method6262(var1x, 6);
         List<Entry> entries = new ArrayList<>(var4x);

         for (int i = 0; i < var4x && items.size() + entries.size() < 2500; i++) {
            ScriptGuiApi.Entry entry = new ScriptGuiApi.Entry();
            entry.script = script;
            entry.entry = var2x;
            entry.text = var3x;
            entry.doubleValue = ScriptValueUtils.method6256(var1x, 1, 0.0);
            entry.doubleValue2 = ScriptValueUtils.method6256(var1x, 2, 0.0);
            entry.doubleValue3 = ScriptValueUtils.method6256(var1x, 3, 0.0);
            double d2 = Math.random() * Math.PI * 2.0;
            double d3 = Math.acos(2.0 * Math.random() - 1.0);
            double d4 = d * (0.5 + Math.random() * 0.5) / 20.0;
            entry.doubleValue4 = Math.sin(d3) * Math.cos(d2) * d4;
            entry.doubleValue5 = Math.abs(Math.cos(d3)) * d4 + d4 * 0.5;
            entry.doubleValue6 = Math.sin(d3) * Math.sin(d2) * d4;
            entry.doubleValue7 = scriptable == null ? 0.02 : ScriptValueUtils.method6264(scriptable, "gravity", 8.0) / 400.0;
            entry.doubleValue8 = scriptable == null ? 0.96 : ScriptValueUtils.method6264(scriptable, "drag", 0.96);
            entry.size = scriptable == null ? 0.35F : (float)ScriptValueUtils.method6264(scriptable, "size", 0.35);
            entry.floatValue = (float)((Math.random() - 0.5) * (scriptable == null ? 20.0 : ScriptValueUtils.method6264(scriptable, "spin", 20.0)));
            entry.intValue = (int)(20.0 * (scriptable == null ? 1.5 : ScriptValueUtils.method6264(scriptable, "life", 1.5)) * (0.7 + Math.random() * 0.6));
            entry.flag = true;
            entry.color = scriptable == null ? -1 : (int)((long)ScriptValueUtils.method6264(scriptable, "color", 4.294967295E9));
            entries.add(entry);
         }

         items.addAll(entries);
         return null;
      });
      entry2.method6274("clear", var1x -> {
         items.removeIf(var1xx -> var1xx.script == script);
         return null;
      });
      entry2.method6274("count", var1x -> (double)items.stream().filter(var1xx -> var1xx.script == script).count());
      return entry2.getScriptableObject();
   }

   public static Scriptable method5324(Script workspace, Scriptable scope) {
      method5309();
      workspace.method1413(() -> items3.removeIf(var1x -> var1x.script == workspace));
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "add",
         var1x -> {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "метка");
            items3.removeIf(var2xx -> var2xx.script == workspace && var2xx.text.equals(var2x));
            if (items3.stream().filter(var1xx -> var1xx.script == workspace).count() >= 64L) {
               throw ScriptValueUtils.method6251("больше 64 меток");
            } else {
               items3.add(
                  new ScriptGuiApi.Data2(
                     workspace,
                     var2x,
                     ScriptValueUtils.method6256(var1x, 1, 0.0),
                     ScriptValueUtils.method6256(var1x, 2, 0.0),
                     ScriptValueUtils.method6256(var1x, 3, 0.0),
                     ScriptValueUtils.method6258(var1x, 4, -7686913),
                     ScriptValueUtils.method6259(var1x, 5, true)
                  )
               );
               return null;
            }
         }
      );
      entry2.method6274("remove", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         items3.removeIf(var2xx -> var2xx.script == workspace && var2xx.text.equals(var2x));
         return null;
      });
      entry2.method6274("clear", var1x -> {
         items3.removeIf(var1xx -> var1xx.script == workspace);
         return null;
      });
      entry2.method6274(
         "list",
         var2x -> {
            ArrayList arrayList = new ArrayList();

            for (ScriptGuiApi.Data2 data2 : items3) {
               if (data2.script == workspace) {
                  arrayList.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("name", data2.text)
                        .method6277("x", data2.doubleValue)
                        .method6277("y", data2.doubleValue2)
                        .method6277("z", data2.doubleValue3)
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(scope, arrayList);
         }
      );
      return entry2.getScriptableObject();
   }

   public static Scriptable method5325(Script script, Storage storage, Scriptable scope) {
      method5309();
      script.method1413(() -> {
         items4.values().removeIf(var1xx -> var1xx.script == script);
         Minecraft var1x = Minecraft.getInstance();
         if (var1x.gui.screen() instanceof ScriptGuiApi.Entry2 var2x && var2x.script == script) {
            var1x.execute(() -> var1x.gui.setScreen(null));
         }
      });
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274("toast", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, script.method1403());
         String var3x = ScriptValueUtils.method6254(var1x, 1, "");
         long var4x = (long)Math.max(500.0, Math.min(15000.0, ScriptValueUtils.method6256(var1x, 2, 3000.0)));
         MutableComponent literal = Component.literal(var3x.isEmpty() ? var2x : var2x + " — " + var3x);
         HudHolder.method6969().method6973("i", literal, var4x);
         return null;
      });
      BiFunction<Object[], Boolean, Object> biFunction = (var3x, var4x) -> {
         String name = ScriptValueUtils.method6254(var3x, 0, "window");
         Scriptable scriptable = ScriptValueUtils.method6262(var3x, 1);
         String key = script.getId() + ":" + name;
         ScriptGuiApi.Entry3 entry3 = items4.getOrDefault(key, new ScriptGuiApi.Entry3());
         entry3.script = script;
         entry3.storage = storage;
         entry3.scope = scope;
         entry3.name = name;
         entry3.flag = var4x && (scriptable == null || ScriptValueUtils.method6265(scriptable, "frame", true));
         entry3.title = scriptable == null ? name : ScriptValueUtils.method6266(scriptable, "title", name);
         entry3.floatValue3 = scriptable == null ? 120.0F : (float)Math.max(20.0, Math.min(1000.0, ScriptValueUtils.method6264(scriptable, "width", 120.0)));
         entry3.floatValue4 = scriptable == null ? 60.0F : (float)Math.max(10.0, Math.min(800.0, ScriptValueUtils.method6264(scriptable, "height", 60.0)));
         JsonElement jsonElement = script.method1415().method1006("__window." + name);
         if (jsonElement != null && jsonElement.isJsonObject()) {
            entry3.floatValue = jsonElement.getAsJsonObject().get("x").getAsFloat();
            entry3.floatValue2 = jsonElement.getAsJsonObject().get("y").getAsFloat();
         } else if (!items4.containsKey(key)) {
            entry3.floatValue = scriptable == null ? 10.0F : (float)ScriptValueUtils.method6264(scriptable, "x", 10.0);
            entry3.floatValue2 = scriptable == null ? 40.0F : (float)ScriptValueUtils.method6264(scriptable, "y", 40.0);
         }

         entry3.function = ScriptValueUtils.method6261(var3x, 2);
         entry3.function2 = ScriptValueUtils.method6261(var3x, 3);
         items4.put(key, entry3);
         return ScriptValueUtils.method6247(scope).method6274("show", var1xx -> {
            entry3.visible = true;
            return null;
         }).method6274("hide", var1xx -> {
            entry3.visible = false;
            return null;
         }).method6274("visible", var1xx -> entry3.visible).method6274("toggle", var1xx -> {
            entry3.visible = !entry3.visible;
            return entry3.visible;
         }).method6274("resize", var1xx -> {
            entry3.floatValue3 = (float)Math.max(20.0, ScriptValueUtils.method6256(var1xx, 0, entry3.floatValue3));
            entry3.floatValue4 = (float)Math.max(10.0, ScriptValueUtils.method6256(var1xx, 1, entry3.floatValue4));
            return null;
         }).method6274("move", var1xx -> {
            entry3.floatValue = (float)ScriptValueUtils.method6256(var1xx, 0, entry3.floatValue);
            entry3.floatValue2 = (float)ScriptValueUtils.method6256(var1xx, 1, entry3.floatValue2);
            method5319(entry3);
            return null;
         }).method6274("setTitle", var1xx -> {
            entry3.title = ScriptValueUtils.method6254(var1xx, 0, entry3.title);
            return null;
         }).method6274("x", var1xx -> (double)entry3.floatValue).method6274("y", var1xx -> (double)entry3.floatValue2).method6274("close", var1xx -> {
            items4.remove(key);
            return null;
         }).getScriptableObject();
      };
      entry2.method6274("window", var1x -> biFunction.apply(var1x, true));
      entry2.method6274("hud", var1x -> biFunction.apply(var1x, false));
      entry2.method6274(
         "screen",
         var2x -> {
            Long var3x = items5.get(script);
            if (var3x != null && System.currentTimeMillis() - var3x < 3000L) {
               throw ScriptValueUtils.method6251("игрок только что закрыл экран — открыть снова можно через 3 секунды");
            } else {
               Scriptable var4x = ScriptValueUtils.method6262(var2x, 0);
               if (var4x == null) {
                  throw ScriptValueUtils.method6251("ui.screen({ title, render(w, h, mx, my), click(x, y, button), key(code), char(c), close() })");
               } else {
                  ScriptGuiApi.Entry2 screen = new ScriptGuiApi.Entry2(
                     script,
                     storage,
                     ScriptValueUtils.method6266(var4x, "title", script.method1403()),
                     method5326(var4x, "render"),
                     method5326(var4x, "click"),
                     method5326(var4x, "release"),
                     method5326(var4x, "scroll"),
                     method5326(var4x, "key"),
                     method5326(var4x, "char"),
                     method5326(var4x, "close"),
                     ScriptValueUtils.method6265(var4x, "pause", false),
                     ScriptValueUtils.method6265(var4x, "blur", true)
                  );
                  Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.setScreen(screen));
                  return null;
               }
            }
         }
      );
      entry2.method6274("close", var1x -> {
         Minecraft var2x = Minecraft.getInstance();
         if (var2x.gui.screen() instanceof ScriptGuiApi.Entry2 var3x && var3x.script == script) {
            var2x.execute(() -> var2x.gui.setScreen(null));
         }

         return null;
      });
      entry2.method6274("isOpen", var1x -> Minecraft.getInstance().gui.screen() instanceof ScriptGuiApi.Entry2 var2x && var2x.script == script);
      entry2.method6274(
         "button",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
            float var4x = (float)ScriptValueUtils.method6256(var0x, 3, 60.0);
            float height = (float)ScriptValueUtils.method6256(var0x, 4, 16.0);
            float f = (float)ScriptValueUtils.method6256(var0x, 5, -1.0);
            float f2 = (float)ScriptValueUtils.method6256(var0x, 6, -1.0);
            boolean flag = f >= var2x && f <= var2x + var4x && f2 >= var3x && f2 <= var3x + height;
            int n = ScriptDrawApi.method2603();
            var1x.drawRoundedRect(
               var2x,
               var3x,
               var4x,
               height,
               ProxyDotRadiusData.method8069(Math.min(height / 2.0F, 5.0F)),
               new ZenithColor(flag ? n & 16777215 | 1711276032 : 1090519039)
            );
            Font font = Fonts.NEW_MEDIUM.getFont(Math.max(4.0F, Math.min(9.0F, height * 0.42F)));
            String text = ScriptValueUtils.method6254(var0x, 0, "");
            var1x.drawText(font, text, var2x + (var4x - font.width(text)) / 2.0F, var3x + (height - font.height()) / 2.0F, new ZenithColor(-1));
            return flag;
         }
      );
      return entry2.getScriptableObject();
   }

   private static Function method5326(Scriptable scriptable, String str) {
      return ScriptValueUtils.method6263(scriptable, str) instanceof Function function ? function : null;
   }

   public static final class Data {
      private final String text;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, doubleValue, doubleValue2, doubleValue3, floatValue, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(String text, double doubleValue, double d, double d2, float floatValue, int intValue, int n) {
         this.text = text;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.floatValue = floatValue;
         this.intValue = intValue;
         this.intValue2 = n;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public int color() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data2 {
      private final Script script;
      private final String text;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final int intValue;
      private final boolean flag;

      public Script script() {
         return this.script;
      }

      public String text() {
         return this.text;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(script, text, doubleValue, doubleValue2, doubleValue3, intValue, flag);
      }

      @Override
      public String toString() {
         return "Data2[" + "script=" + this.script + ", " + "text=" + this.text + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + "]";
      }

      public Data2(Script script, String text, double doubleValue, double d, double d2, int intValue, boolean flag) {
         this.script = script;
         this.text = text;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.intValue = intValue;
         this.flag = flag;
      }

      public Script getScript() {
         return this.script;
      }

      public String name() {
         return this.text;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public int color() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Entry {
      public Script script;
      public RemoteUtil.Entry entry;
      public String text;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public float size;
      public float floatValue;
      public float floatValue2;
      public int color;
      public int intValue;
      public int intValue2;
      public boolean flag;
   }

   public static final class Entry2 extends Screen {
      public final Script script;
      private final Storage storage;
      private final Function function;
      private final Function function2;
      private final Function function3;
      private final Function function4;
      private final Function function5;
      private final Function function6;
      private final Function function7;
      private final boolean flag;
      private final boolean flag2;

      public Entry2(
         Script script,
         Storage storage,
         String text,
         Function function,
         Function function2,
         Function function3,
         Function function4,
         Function function5,
         Function function6,
         Function function7,
         boolean flag,
         boolean flag2
      ) {
         super(Component.literal(text));
         this.script = script;
         this.storage = storage;
         this.function = function;
         this.function2 = function2;
         this.function3 = function3;
         this.function4 = function4;
         this.function5 = function5;
         this.function6 = function6;
         this.function7 = function7;
         this.flag = flag;
         this.flag2 = flag2;
      }

      @Override
      public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
         if (this.flag2) {
            super.extractBackground(context, mouseX, mouseY, deltaTicks);
         }
      }

      @Override
      public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
         if (this.function != null && this.script.isRunning()) {
            CustomDrawContext customDrawContext = CustomDrawContext.of(context);
            ScriptDrawApi.method2597(customDrawContext);
            ScriptImGui.method635(0.0F, 0.0F);

            try {
               this.storage.method1321(this.script, this.function, (double)this.width, (double)this.height, (double)mouseX, (double)mouseY);
            } finally {
               ScriptDrawApi.end();
            }
         }
      }

      @Override
      public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent click, boolean doubled) {
         if (org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()) == 0) {
            ScriptImGui.method634((float)click.x(), (float)click.y());
         }

         if (this.function2 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function2, click.x(), click.y(), (double)org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
         }

         return true;
      }

      @Override
      public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent click) {
         if (this.function3 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function3, click.x(), click.y(), (double)org.zenith.utility.misc.KeyCompat.mouseToGlfw(click.button()));
         }

         return true;
      }

      @Override
      public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
         if (this.function4 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function4, verticalAmount, mouseX, mouseY);
         }

         return true;
      }

      @Override
      public boolean keyPressed(KeyEvent input) {
         if (org.zenith.utility.misc.KeyCompat.toGlfw(input.key()) == 256) {
            ScriptGuiApi.items5.put(this.script, System.currentTimeMillis());
            this.onClose();
            return true;
         } else if (ScriptImGui.method643(this.script) && ScriptImGui.method645(this.script, org.zenith.utility.misc.KeyCompat.toGlfw(input.key()))) {
            return true;
         } else {
            if (this.function5 != null && this.script.isRunning()) {
               this.storage.method1321(this.script, this.function5, (double)org.zenith.utility.misc.KeyCompat.toGlfw(input.key()), (double)org.zenith.utility.misc.KeyCompat.modsToGlfw(input.modifiers()));
            }

            return true;
         }
      }

      @Override
      public boolean charTyped(CharacterEvent input) {
         if (ScriptImGui.method643(this.script)) {
            ScriptImGui.method644(this.script, new String(Character.toChars(input.codepoint())));
            return true;
         } else {
            if (this.function6 != null && this.script.isRunning()) {
               this.storage.method1321(this.script, this.function6, new String(Character.toChars(input.codepoint())));
            }

            return true;
         }
      }

      @Override
      public void removed() {
         if (this.function7 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function7);
         }
      }

      @Override
      public boolean isPauseScreen() {
         return this.flag;
      }
   }

   public static final class Entry3 {
      public Script script;
      public Storage storage;
      public Scriptable scope;
      public String name;
      public String title;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public boolean visible = true;
      public boolean flag = true;
      public Function function;
      public Function function2;
      public boolean dragging;
      public float floatValue5;
      public float floatValue6;
   }
}
