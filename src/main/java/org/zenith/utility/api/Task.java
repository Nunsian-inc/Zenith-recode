package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.media.NowPlayingScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.game.FocusSlotEnum;

public final class Task {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/WardrobeShots");
   private final Path path;
   private final Path path2;
   private final List<Task.Data> items = new ArrayList<>();
   private final List<String> items2 = new ArrayList<>();
   private final List<String> items3 = new ArrayList<>();
   private boolean flag;
   private int intValue;
   private int index = -1;
   private int intValue2;
   private boolean flag2;

   private Task(Path path) {
      this.path = path.resolve("wardrobe").resolve("autoshot.task.json");
      this.path2 = path.resolve("wardrobe").resolve("shots");
   }

   public static void method5982(Path path) {
      if (System.getProperty("DevMode") != null && Boolean.getBoolean("zenith.wardrobe.autoshot")) {
         Task task = new Task(path);
         if (Files.isRegularFile(task.path)) {
            task.load();
            ClientTickEvents.END_CLIENT_TICK.register(task::method5983);
         }
      }
   }

   private void load() {
      JsonObject json = JsonIo.method1009(this.path);
      if (json != null) {
         this.flag = json.has("quit") && json.get("quit").getAsBoolean();
         if (json.has("setup")) {
            for (JsonElement jsonElement : json.getAsJsonArray("setup")) {
               this.items2.add(jsonElement.getAsString());
            }
         }

         if (json.has("modules")) {
            for (JsonElement jsonElement : json.getAsJsonArray("modules")) {
               this.items3.add(jsonElement.getAsString());
            }
         }

         for (JsonElement jsonElement : json.has("shots") ? json.getAsJsonArray("shots") : new JsonArray()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ArrayList items = new ArrayList();
            if (jsonObject.has("equip")) {
               for (JsonElement json2 : jsonObject.getAsJsonArray("equip")) {
                  items.add(json2.getAsString());
               }
            }

            this.items
               .add(
                  new Task.Data(
                     jsonObject.get("name").getAsString(),
                     items,
                     CameraType.valueOf(jsonObject.has("perspective") ? jsonObject.get("perspective").getAsString().toUpperCase(Locale.ROOT) : "THIRD_PERSON_FRONT"),
                     jsonObject.has("yaw") ? jsonObject.get("yaw").getAsFloat() : 0.0F,
                     jsonObject.has("pitch") ? jsonObject.get("pitch").getAsFloat() : 0.0F,
                     jsonObject.has("hold") ? jsonObject.get("hold").getAsString() : "",
                     jsonObject.has("wait") ? jsonObject.get("wait").getAsInt() : 30,
                     jsonObject.has("sneak") && jsonObject.get("sneak").getAsBoolean(),
                     jsonObject.has("cloud") ? jsonObject.get("cloud").getAsInt() : -1,
                     jsonObject.has("friendsAdd") ? jsonObject.get("friendsAdd").getAsString() : null,
                     jsonObject.has("wardrobeTab") ? jsonObject.get("wardrobeTab").getAsInt() : -1,
                     jsonObject.has("screen") ? jsonObject.get("screen").getAsString() : null
                  )
               );
         }

         logger.info("Автоснимки гардероба: {} кадров", this.items.size());
      }
   }

   private void method5983(Minecraft minecraft) {
      if (minecraft.player != null && minecraft.level != null) {
         minecraft.options.pauseOnLostFocus = false;
         boolean flag = this.index >= 0
            && this.index < this.items.size()
            && (this.items.get(this.index).getIntValue2() >= 0 || this.items.get(this.index).getText4() != null);
         if (minecraft.gui.screen() != null && !flag) {
            minecraft.gui.setScreen(null);
         }

         if (this.intValue >= 80) {
            if (this.index < this.items.size()) {
               if (this.index < 0) {
                  this.index = 0;
                  this.intValue2 = 0;
                  this.flag2 = false;
                  this.method5984(minecraft, this.items.get(0));
               }

               Task.Data data = this.items.get(this.index);
               this.method5985(minecraft, data);
               this.intValue2++;
               if (this.intValue2 % 20 == 0) {
                  logger.info("Автоснимки гардероба: {} fps {}", data.name(), minecraft.getFps());
               }

               if (this.intValue2 == data.getIntValue() && !this.flag2) {
                  this.flag2 = true;
                  this.method5986(minecraft, data.name());
               }

               if (this.intValue2 >= data.getIntValue() + 5) {
                  this.index++;
                  if (this.index >= this.items.size()) {
                     minecraft.gui.hud.isHidden = false;
                     logger.info("Автоснимки гардероба готовы: {}", this.path2);
                     if (this.flag) {
                        minecraft.stop();
                     }

                     return;
                  }

                  this.intValue2 = 0;
                  this.flag2 = false;
                  this.method5984(minecraft, this.items.get(this.index));
               }
            }
         } else {
            this.intValue++;
            if (this.intValue == 30) {
               for (String str : this.items3) {
                  boolean startsWith = str.startsWith("-");
                  Module moduleValue = Zenith.getInstance().getModuleManager().findByName(startsWith ? str.substring(1) : str);
                  if (moduleValue != null && moduleValue.isEnabled() == startsWith) {
                     moduleValue.toggle();
                  }
               }
            }

            if (this.intValue == 20 || this.intValue == 40) {
               for (String command : this.items2) {
                  minecraft.player.connection.sendCommand(command);
               }
            }
         }
      } else {
         this.intValue = 0;
      }
   }

   private void method5984(Minecraft minecraft, Task.Data data) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null) {
         wardrobe.method2496();

         for (String string : data.getItems()) {
            wardrobe.method2492(wardrobe.getRegistry().method1421(string));
         }
      }

      minecraft.gui.hud.isHidden = data.getPerspective() != CameraType.FIRST_PERSON;
      minecraft.options.setCameraType(data.getPerspective());
      ItemStack itemStack = ItemStack.EMPTY;
      FocusSlotEnum focusSlotEnum = FocusSlotEnum.method546(data.getText2());
      if (focusSlotEnum != null && focusSlotEnum.method543()) {
         itemStack = focusSlotEnum.method544();
      }

      minecraft.player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
      if (data.getText4() != null && data.getText4().startsWith("module:")) {
         Module moduleValue = Zenith.getInstance().getModuleManager().findByName(data.getText4().substring(7));
         if (!MenuModule.INSTANCE.isEnabled()) {
            MenuModule.INSTANCE.toggle();
         }

         if (moduleValue != null) {
            Zenith.getInstance().getNLMenuScreen().devShowModule(moduleValue);
         }
      }

      if ("music".equals(data.getText4())) {
         minecraft.gui.setScreen(new NowPlayingScreen());
      }

      if (data.getIntValue2() >= 0) {
         NLMenuScreen nlMenuScreen = Zenith.getInstance().getNLMenuScreen();
         if (!MenuModule.INSTANCE.isEnabled()) {
            MenuModule.INSTANCE.toggle();
         }

         if (data.getIntValue3() >= 0) {
            nlMenuScreen.openCosmetics(data.getIntValue3());
         } else if (data.getText3() != null) {
            nlMenuScreen.setType(NLMenuScreen.ElementsType.CLOUD);
            nlMenuScreen.getCloudSectionPanel().openFriendsAdd(data.getText3());
         } else {
            nlMenuScreen.setType(NLMenuScreen.ElementsType.CLOUD);
            nlMenuScreen.getCloudSectionPanel().open(data.getIntValue2());
         }
      }
   }

   private void method5985(Minecraft minecraft, Task.Data data) {
      minecraft.player.setYRot(data.getFloatValue());
      minecraft.player.setXRot(data.getFloatValue2());
      minecraft.player.setYHeadRot(data.getFloatValue());
      minecraft.player.setYBodyRot(data.getFloatValue());
      minecraft.player.yRotO = data.getFloatValue();
      minecraft.player.xRotO = data.getFloatValue2();
      minecraft.player.yBodyRotO = data.getFloatValue();
      minecraft.player.yHeadRotO = data.getFloatValue();
      minecraft.player.setShiftKeyDown(data.isFlag());
   }

   private void method5986(Minecraft minecraft, String name) {
      Screenshot.takeScreenshot(minecraft.gameRenderer.mainRenderTarget(), var2x -> {
         try {
            Files.createDirectories(this.path2);
            var2x.writeToFile(this.path2.resolve(name + ".png"));
         } catch (IOException e) {
            logger.error("Снимок {} не сохранён", name, e);
         } finally {
            var2x.close();
         }
      });
   }

   private static final class Data {
      private final String text;
      private final List<String> items;
      private final CameraType perspective;
      private final float floatValue;
      private final float floatValue2;
      private final String text2;
      private final int intValue;
      private final boolean flag;
      private final int intValue2;
      private final String text3;
      private final int intValue3;
      private final String text4;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      public CameraType perspective() {
         return this.perspective;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public String text3() {
         return this.text3;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public String text4() {
         return this.text4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.perspective, other.perspective) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.text4, other.text4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, perspective, floatValue, floatValue2, text2, intValue, flag, intValue2, text3, intValue3, text4);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "perspective=" + this.perspective + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + ", " + "intValue2=" + this.intValue2 + ", " + "text3=" + this.text3 + ", " + "intValue3=" + this.intValue3 + ", " + "text4=" + this.text4 + "]";
      }

      private Data(
         String text,
         List<String> items,
         CameraType perspective,
         float floatValue,
         float f,
         String string,
         int intValue,
         boolean flag,
         int n,
         String str,
         int n2,
         String string2
      ) {
         this.text = text;
         this.items = items;
         this.perspective = perspective;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.text2 = string;
         this.intValue = intValue;
         this.flag = flag;
         this.intValue2 = n;
         this.text3 = str;
         this.intValue3 = n2;
         this.text4 = string2;
      }

      public String name() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }

      public CameraType getPerspective() {
         return this.perspective;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public String getText2() {
         return this.text2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public String getText3() {
         return this.text3;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public String getText4() {
         return this.text4;
      }
   }
}
