package org.zenith.base.filemanager.impl.way;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class WayManager extends JsonListStorage<Way> implements MinecraftAccess {
   private static final Pattern CHAT_COORDS_PATTERN = Pattern.compile("(?<!\\d)(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)(?!\\d)");
   private int pendingEventMessages;

   public WayManager() {
      super("way", "", (new TypeToken<ArrayList<Way>>() {}).getType(), ArrayList::new);
      EventManager.register(this);
   }

   @Override
   public Gson createGson() {
      return new GsonBuilder().registerTypeAdapter(Way.class, new WayAdapter()).create();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void addWay(Way way) {
      this.items.add(way);
   }

   public void addWay(String name, BlockPos pos, String server) {
      this.items.add(new Way(name, pos, server));
   }

   public Way getWay(String argument) {
      return this.items.stream().filter(var1x -> var1x.name().equalsIgnoreCase(argument)).findAny().orElse(null);
   }

   public boolean hasWay(String string) {
      return this.items.stream().anyMatch(var1x -> var1x.name().equalsIgnoreCase(string));
   }

   public void deleteWay(String anotherString) {
      this.items.removeIf(var1x -> var1x.name().equalsIgnoreCase(anotherString));
   }

   public void clearList() {
      if (!this.isEmpty()) {
         this.items.clear();
      }
   }

   public void startEventCapture() {
      this.pendingEventMessages = 2;
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent event) {
      if (this.pendingEventMessages > 0) {
         this.pendingEventMessages--;
         this.onCheckMessage(event.getText().getString());
      }
   }

   public void onCheckMessage(String string) {
      Matcher matcher = CHAT_COORDS_PATTERN.matcher(string);
      if (matcher.find()) {
         int x = Integer.parseInt(matcher.group(1));
         int y = Integer.parseInt(matcher.group(2));
         int z = Integer.parseInt(matcher.group(3));
         this.deleteWay("event");
         this.addWay(new Way("event", new BlockPos(x, y, z), this.getCurrentServer()));
         ChatLogger.method2019("Точка event добавлена: " + x + " " + y + " " + z);
         this.pendingEventMessages = 0;
      }
   }

   private String getCurrentServer() {
      return mc.getConnection() != null && mc.getConnection().getServerData() != null ? mc.getConnection().getServerData().ip : "VANILLA";
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      if (!this.isEmpty() && mc.level != null) {
         this.items
            .forEach(
               way -> {
                  Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(way.pos());
                  Vec3 vec = ProjectionUtils.method301(atCenterOf);
                  if (ProjectionUtils.method302(atCenterOf)
                     && way.server()
                        .equalsIgnoreCase(
                           mc.getConnection() != null && mc.getConnection().getServerData() != null
                              ? mc.getConnection().getServerData().ip
                              : "VANILLA"
                        )) {
                     String text = (int)MathUtils.method2652(mc.getEntityRenderDispatcher().camera.position().distanceTo(atCenterOf), 1.0) + "m";
                     ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
                     Font font = Fonts.NEW_MEDIUM.getFont(8.0F);
                     float intValue = GuiStyle.PADDING.intValue();
                     float height = font.height() + GuiStyle.PADDING.intValue() + 1.0F;
                     float panelWidth = intValue + font.width(way.name()) + intValue + font.width(text) + intValue + intValue / 2.0F;
                     float floatValue = (float)(vec.x() - panelWidth / 2.0F);
                     float y = (float)(vec.y() - height / 2.0F);
                     this.pushCenteredScale(event.getCustomDrawContext(), (float)vec.x, (float)vec.y, 1.0F, 1.0F);
                     ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
                     RenderShaders.method5287(
                        MatrixUtils.method664(event.getCustomDrawContext().pose()),
                        floatValue,
                        y,
                        panelWidth,
                        height,
                        22.0F,
                        headerRadius,
                        ZenithColor.INSTANCE,
                        EntityESPModule.INSTANCE.getBlur().isEnabled() && EntityESPModule.INSTANCE.getBlur().isVisible(),
                        EntityESPModule.INSTANCE.getGlow().isEnabled() && EntityESPModule.INSTANCE.getGlow().isVisible()
                     );
                     event.getCustomDrawContext().drawRoundedRect(floatValue, y, panelWidth, height, headerRadius, currentStyle.getHudBackground().getZenithColor());
                     float centeredTextY = y + (height - 1.0F - font.height()) / 2.0F;
                     event.getCustomDrawContext()
                        .drawRoundedRect(floatValue, y, intValue + font.width(way.name()) + intValue, height, headerRadius, currentStyle.getHudBackground().getZenithColor());
                     event.getCustomDrawContext().drawText(font, way.name(), floatValue + intValue, centeredTextY, currentStyle.getTextEnable().getZenithColor());
                     event.getCustomDrawContext()
                        .drawText(font, text, floatValue + intValue + font.width(way.name()) + intValue + intValue / 2.0F, centeredTextY, currentStyle.getTextEnable().getZenithColor());
                     this.pop(event.getCustomDrawContext());
                  }
               }
            );
      }
   }

   private void pushCenteredScale(CustomDrawContext customDrawContext, float x, float y, float f, float f2) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().scale(f * EntityESPModule.INSTANCE.getSize(), f2 * EntityESPModule.INSTANCE.getSize());
      customDrawContext.pose().translate(-x, -y);
   }

   private void pop(CustomDrawContext customDrawContext) {
      customDrawContext.popMatrix();
   }
}
