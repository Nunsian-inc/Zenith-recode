package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.filemanager.impl.way.Way;
import org.zenith.base.filemanager.impl.way.WayManager;
import org.zenith.client.events.ChatReceiveEvent;

@ModuleInfo(
   name = "EventTracker",
   category = ModuleCategory.MISC,
   description = "module.eventtracker.description"
)
public final class EventTrackerModule extends Module implements MinecraftAccess {
   public static final EventTrackerModule INSTANCE = new EventTrackerModule();
   private static final Pattern pattern = Pattern.compile("(?:Координаты:|на координатах)\\s*\\[?\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");
   private static final Pattern pattern2 = Pattern.compile("\\[(.+?)]");
   private static final Pattern pattern3 = Pattern.compile("▶\\s*(.+?)(?:\\s+(?:находится|уже))?\\s+на\\s+координатах");
   private static final Pattern pattern4 = Pattern.compile("^\\s*\\[\\d+]\\s*(.+?):?\\s*$");
   private String text;
   private String text2;

   private EventTrackerModule() {
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent event) {
      String string = event.getText().getString();
      Matcher matcher = pattern2.matcher(string);
      String text = null;
      if (matcher.find()) {
         String str = matcher.group(1).trim();
         if (!str.matches("[-\\d\\s]*")) {
            text = str;
         }
      }

      if (text == null) {
         Matcher matcher2 = pattern3.matcher(string);
         if (matcher2.find()) {
            text = matcher2.group(1).trim();
         }
      }

      if (text == null) {
         Matcher matcher2 = pattern4.matcher(string);
         if (matcher2.find()) {
            text = matcher2.group(1).trim();
         }
      }

      Matcher matcher2 = pattern.matcher(string);
      if (matcher2.find()) {
         String anotherString = text != null ? text : this.text;
         if (anotherString != null) {
            int x = Integer.parseInt(matcher2.group(1));
            int y = Integer.parseInt(matcher2.group(2));
            int z = Integer.parseInt(matcher2.group(3));
            this.method952(anotherString, x, y, z);
            this.text = null;
         }
      } else if (text != null) {
         this.text = text;
      } else if (string.contains("▍")) {
         String str = string.replace("▍", "").trim();
         if (!str.isEmpty() && !str.startsWith("Редкость") && !str.startsWith("Уровень лута")) {
            this.text = str;
         }
      }
   }

   private void method952(String anotherString, int x, int y, int z) {
      WayManager wayManager = Zenith.getInstance().getWayManager();
      if (this.text2 != null) {
         wayManager.deleteWay(this.text2);
      }

      wayManager.deleteWay(anotherString);
      String ip = mc.getConnection() != null && mc.getConnection().getServerData() != null
         ? mc.getConnection().getServerData().ip
         : "VANILLA";
      wayManager.addWay(new Way(anotherString, new BlockPos(x, y, z), ip));
      this.text2 = anotherString;
   }

   @Override
   public void onDisable() {
      this.text = null;
      super.onDisable();
   }
}
