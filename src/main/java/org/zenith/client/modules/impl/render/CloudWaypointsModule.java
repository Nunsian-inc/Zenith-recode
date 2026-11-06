package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.CloudWaypointEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "CloudWaypoints",
   category = ModuleCategory.RENDER,
   description = "module.cloudwaypoints.description"
)
public final class CloudWaypointsModule extends Module {
   public static final CloudWaypointsModule INSTANCE = new CloudWaypointsModule();
   private static final float CONST_floatValue = 3000.0F;
   private static final long CONST_longValue = 280L;
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 256;
   private final BindSetting markBind = new BindSetting("module.cloudWaypoints.markBind", "module.cloudWaypoints.markBind.desc", -1);
   private final TextSetting label = new TextSetting("module.cloudWaypoints.label", "module.cloudWaypoints.label.desc", "", "module.cloudWaypoints.label.empty");
   private final SliderSetting ttl = new SliderSetting("module.cloudWaypoints.ttl", 30.0F, 5.0F, 300.0F, 5.0F, "module.cloudWaypoints.ttl.desc", " с");
   private final SliderSetting range = new SliderSetting("module.cloudWaypoints.range", 120.0F, 10.0F, 400.0F, 10.0F, "module.cloudWaypoints.range.desc", " м");
   private final ColorSetting colorSetting = new ColorSetting("module.cloudWaypoints.color", "module.cloudWaypoints.color.desc", new ZenithColor(-680437));
   private final SliderSetting markerScale = new SliderSetting(
      "module.cloudWaypoints.markerScale", 100.0F, 50.0F, 200.0F, 5.0F, "module.cloudWaypoints.markerScale.desc", " %"
   );
   private final BooleanSetting compass = new BooleanSetting("module.cloudWaypoints.compass", "module.cloudWaypoints.compass.desc", true);
   private final SliderSetting compassRadius = new SliderSetting(
      "module.cloudWaypoints.compassRadius", 38.0F, 16.0F, 120.0F, 2.0F, "module.cloudWaypoints.compassRadius.desc", " px"
   );
   private final BooleanSetting follow = new BooleanSetting("module.cloudWaypoints.follow", "module.cloudWaypoints.follow.desc", true);
   private final BooleanSetting showOwn = new BooleanSetting("module.cloudWaypoints.showOwn", "module.cloudWaypoints.showOwn.desc", true);
   private final BooleanSetting sound = new BooleanSetting("module.cloudWaypoints.sound", "module.cloudWaypoints.sound.desc", true);
   private final List<CloudWaypointsModule.Entry> items = new ArrayList<>();
   private static final long CONST_longValue2 = 50000000L;

   private CloudWaypointsModule() {
   }

   public BindSetting getMarkBind() {
      return this.markBind;
   }

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   @Override
   public void onDisable() {
      this.items.clear();
      super.onDisable();
   }

   @EventTarget
   public void method6400(KeyEvent event) {
      if (this.markBind.getKeyCode() > 0 && event.isPress(this.markBind.getKeyCode())) {
         this.method6401();
      }
   }

   private void method6401() {
      Cloud cloud2 = cloud();
      if (cloud2 != null && cloud2.method7862() && mc.player != null && mc.level != null) {
         double blockInteractionRange = this.range.getValue3();
         Vec3 eyePosition = mc.player.getEyePosition();
         Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());
         BlockHitResult blockHitResult = TargetUtils.method3205(eyePosition, blockInteractionRange, rotation, false);
         double scale = blockHitResult != null && blockHitResult.getType() != Type.MISS ? eyePosition.distanceTo(blockHitResult.getLocation()) : blockInteractionRange;
         EntityHitResult entityHitResult = TargetUtils.method3210(scale, rotation, entity -> entity != mc.player && entity.isAlive() && !entity.isInvisible());
         String stringUUID = "";
         Vec3 center;
         if (entityHitResult != null) {
            center = entityHitResult.getEntity().getBoundingBox().getCenter();
            stringUUID = entityHitResult.getEntity().getStringUUID();
         } else if (blockHitResult != null && blockHitResult.getType() != Type.MISS) {
            center = blockHitResult.getLocation();
         } else {
            center = eyePosition.add(rotation.toDirection().scale(blockInteractionRange));
         }

         String str = this.label.getValue() == null ? "" : this.label.getValue().strip();
         cloud2.method7910(center.x, center.y, center.z, str, this.colorSetting.method1445(), stringUUID);
      }
   }

   public String method6402(double doubleValue, double d, double d2, String username, String str, String server, String dimension) {
      Cloud cloud2 = cloud();
      if (cloud2 == null) {
         return "gui.chat.mark.noCloud";
      } else if (mc.level == null || mc.player == null) {
         return "gui.chat.mark.notInWorld";
      } else if (!method6407(doubleValue, d, d2)) {
         return "gui.chat.attachBroken";
      } else if (!server.isEmpty() && !method6403(server, cloud2.method7911())) {
         return "gui.chat.mark.otherServer";
      } else if (!dimension.isEmpty() && !method6405(dimension, cloud2.method7913())) {
         return "gui.chat.mark.otherDimension";
      } else {
         this.setToggled(true);
         long now = System.currentTimeMillis();
         CloudPackets.Data10 data10 = new CloudPackets.Data10(
            0L, username, cloud2.method7911(), cloud2.method7913(), doubleValue, d, d2, str, this.colorSetting.method1445(), ""
         );
         this.method6409(new CloudWaypointsModule.Entry(data10, now, now + (long)this.ttl.getValue3() * 1000L));
         if (this.sound.isEnabled()) {
            mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 0.6F, 1.6F);
         }

         return "";
      }
   }

   private static boolean method6403(String server, String anotherString) {
      return server.equalsIgnoreCase(anotherString) || server.startsWith("singleplayer") && anotherString.startsWith("singleplayer");
   }

   private static boolean method6404(String server, String anObject) {
      return !anObject.isEmpty() && server.equalsIgnoreCase(anObject);
   }

   private static boolean method6405(String dimension, String str) {
      return method6406(dimension).equalsIgnoreCase(method6406(str));
   }

   private static String method6406(String dimension) {
      int indexOf = dimension.indexOf(58);
      return indexOf < 0 ? dimension : dimension.substring(indexOf + 1);
   }

   private static boolean method6407(double doubleValue, double d, double d2) {
      return Double.isFinite(doubleValue)
         && Double.isFinite(d)
         && Double.isFinite(d2)
         && Math.abs(doubleValue) <= 3.0E7
         && Math.abs(d2) <= 3.0E7
         && Math.abs(d) <= 100000.0;
   }

   @EventTarget
   public void method6408(CloudWaypointEvent event) {
      if (!event.isFlag() || this.showOwn.isEnabled()) {
         CloudPackets.Data10 data10 = event.getData10();
         Cloud cloud2 = cloud();
         if (cloud2 != null && method6407(data10.getDoubleValue(), data10.getDoubleValue2(), data10.getDoubleValue3())) {
            if (!cloud2.prefs().method3316(data10.username())) {
               if (method6404(data10.server(), cloud2.method7911()) && method6405(data10.dimension(), cloud2.method7913())) {
                  long now = System.currentTimeMillis();
                  long l = now + (long)this.ttl.getValue3() * 1000L;

                  for (CloudWaypointsModule.Entry entry : this.items) {
                     if (entry.method6414(data10)) {
                        entry.longValue2 = l;
                        return;
                     }
                  }

                  this.method6409(new CloudWaypointsModule.Entry(data10, now, l));
                  if (this.sound.isEnabled() && mc.player != null) {
                     mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 0.6F, event.isFlag() ? 1.8F : 1.4F);
                  }
               }
            }
         }
      }
   }

   private void method6409(CloudWaypointsModule.Entry entry) {
      String username = entry.data10.username();
      int n = 0;
      int n2 = -1;

      for (int i = 0; i < this.items.size(); i++) {
         if (this.items.get(i).data10.username().equalsIgnoreCase(username)) {
            if (n2 < 0) {
               n2 = i;
            }

            n++;
         }
      }

      if (n >= 64 && n2 >= 0) {
         this.items.remove(n2);
      }

      while (this.items.size() >= 256) {
         this.items.remove(0);
      }

      this.items.add(entry);
   }

   @EventTarget
   public void method6410(DrawEvent event) {
      if (!this.items.isEmpty() && mc.level != null && mc.player != null) {
         Cloud cloud2 = cloud();
         if (cloud2 != null) {
            long now = System.currentTimeMillis();
            String anObject = cloud2.method7911();
            String str = cloud2.method7913();
            CustomDrawContext customDrawContext = event.getCustomDrawContext();
            ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
            if (currentStyle != null) {
               Vec3 position = mc.getEntityRenderDispatcher().camera.position();
               float f = this.markerScale.getValue3() / 100.0F;
               float f2 = mc.getWindow().getGuiScaledWidth() / 2.0F;
               float f3 = mc.getWindow().getGuiScaledHeight() / 2.0F;

               for (int i = this.items.size() - 1; i >= 0; i--) {
                  CloudWaypointsModule.Entry entry = this.items.get(i);
                  if (entry.longValue2 <= now) {
                     this.items.remove(i);
                  } else {
                     CloudPackets.Data10 data10 = entry.data10;
                     boolean flag = data10.server().equals(anObject) || method6404(data10.server(), anObject);
                     if (flag && method6405(data10.dimension(), str)) {
                        Vec3 vec = this.follow.isEnabled() ? this.method6413(entry, event.getTickDelta()) : null;
                        Vec3 vec2 = vec == null ? new Vec3(data10.getDoubleValue(), data10.getDoubleValue2(), data10.getDoubleValue3()) : vec;
                        float previewAlpha = Math.min(1.0F, (float)(entry.longValue2 - now) / 3000.0F);
                        float f4 = Math.min(1.0F, (float)(now - entry.longValue) / 280.0F);
                        double d = position.distanceTo(vec2);
                        String text = entry.text;
                        String meters = entry.meters(d);
                        ZenithColor zenithColor = new ZenithColor(data10.getIntValue()).method7294(previewAlpha);
                        if (ProjectionUtils.method302(vec2)) {
                           Vec3 vec3 = ProjectionUtils.method301(vec2);
                           this.method6411(customDrawContext, currentStyle, (float)vec3.x, (float)vec3.y, text, meters, zenithColor, d, f, previewAlpha, f4);
                        } else if (this.compass.isEnabled()) {
                           this.method6412(customDrawContext, currentStyle, f2, f3, vec2, position, meters, zenithColor, f);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void method6411(
      CustomDrawContext customDrawContext,
      ZenithStyle currentStyle,
      float x,
      float y,
      String text,
      String meters,
      ZenithColor zenithColor,
      double d,
      float f,
      float previewAlpha,
      float f2
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
      float f3 = (1.0F - f2) * 6.0F;
      float previewAlpha2 = previewAlpha * (0.25F + 0.75F * f2);
      float f4 = Mth.clamp((float)((d - 6.0) / 140.0), 0.0F, 1.0F);
      float x2 = (1.0F - 0.18F * f4) * f;
      float intValue = GuiStyle.PADDING.intValue();
      float width = 3.0F;
      float height = font.height() + intValue;
      float width2 = intValue + width + 3.0F + font.width(text) + intValue;
      float f5 = intValue + font.width(meters) + intValue;
      float panelWidth = width2 + f5;
      float f6 = 10.0F * x2;
      float floatValue = x - panelWidth / 2.0F;
      float y2 = y - f6 - height + f3;
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y2 + height / 2.0F);
      customDrawContext.pose().scale(x2, x2);
      customDrawContext.pose().translate(-x, -(y2 + height / 2.0F));
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
      RenderShaders.method5287(
         MatrixUtils.method664(customDrawContext.pose()),
         floatValue,
         y2,
         panelWidth,
         height,
         22.0F,
         headerRadius,
         ZenithColor.INSTANCE,
         EntityESPModule.INSTANCE.getBlur().isEnabled() && EntityESPModule.INSTANCE.getBlur().isVisible(),
         EntityESPModule.INSTANCE.getGlow().isEnabled() && EntityESPModule.INSTANCE.getGlow().isVisible()
      );
      customDrawContext.drawRoundedRect(floatValue, y2, panelWidth, height, headerRadius, currentStyle.getHudBackground().getZenithColor().method7294(previewAlpha2));
      customDrawContext.drawRoundedRect(floatValue, y2, width2, height, headerRadius, currentStyle.getHudBackground().getZenithColor().method7294(previewAlpha2));
      float centeredTextY = y2 + (height - font.height()) / 2.0F;
      customDrawContext.drawRoundedRect(floatValue + intValue, y2 + (height - width) / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor.method7294(previewAlpha2));
      customDrawContext.drawText(font, text, floatValue + intValue + width + 3.0F, centeredTextY, currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha2));
      customDrawContext.drawText(font, meters, floatValue + width2 + intValue, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha2));
      customDrawContext.drawRect(x - 0.5F, y2 + height, 1.0F, f6 - 2.0F, currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha2 * 2.0F));
      float width3 = 3.0F;
      customDrawContext.drawRoundedRect(x - width3 / 2.0F, y - width3 / 2.0F + f3, width3, width3, ProxyDotRadiusData.method8069(width3 / 2.0F), zenithColor.method7294(previewAlpha2));
      customDrawContext.popMatrix();
   }

   private void method6412(CustomDrawContext customDrawContext, ZenithStyle currentStyle, float f, float f2, Vec3 vec, Vec3 position, String meters, ZenithColor zenithColor, float f3) {
      double x = vec.x - position.x;
      double y = vec.z - position.z;
      float f4 = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
      float angdeg = Mth.wrapDegrees(f4 - mc.getEntityRenderDispatcher().camera.yRot());
      double d = Math.toRadians(angdeg);
      float f5 = this.compassRadius.getValue3();
      float f6 = f + (float)Math.sin(d) * f5;
      float f7 = f2 - (float)Math.cos(d) * f5;
      Font font = Fonts.NEW_REGULAR.getFont(6.0F);
      float previewAlpha = zenithColor.getIntValue4() / 255.0F;
      float f8 = GuiStyle.PADDING.intValue() * 0.75F;
      float width = 3.0F;
      float height = font.height() + f8;
      float width2 = f8 + width + 3.0F + font.width(meters) + f8;
      float floatValue = f6 - width2 / 2.0F;
      float y2 = f7 - height / 2.0F;
      customDrawContext.drawRoundedRect(floatValue, y2, width2, height, ProxyDotRadiusData.method8069(height / 2.0F), currentStyle.getHudBackground().getZenithColor().method7294(previewAlpha));
      customDrawContext.drawRoundedRect(floatValue + f8, y2 + (height - width) / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor);
      customDrawContext.drawText(
         font, meters, floatValue + f8 + width + 3.0F, y2 + (height - font.height()) / 2.0F, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha)
      );
   }

   private Vec3 method6413(CloudWaypointsModule.Entry entry, float tickDelta) {
      String name = entry.data10.getText5();
      if (name.isEmpty()) {
         return null;
      } else {
         if (entry.entity != null && (entry.entity.isRemoved() || entry.entity.level() != mc.level)) {
            entry.entity = null;
         }

         if (entry.entity == null) {
            if (!entry.flag) {
               entry.flag = true;

               try {
                  entry.uUID = UUID.fromString(name);
               } catch (IllegalArgumentException e) {
                  entry.uUID = null;
               }
            }

            UUID uUID = entry.uUID;
            if (uUID == null) {
               return null;
            }

            long nanoTime = System.nanoTime();
            if (entry.longValue3 != 0L && nanoTime - entry.longValue3 < 50000000L) {
               return null;
            }

            entry.longValue3 = nanoTime;

            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity.getUUID().equals(uUID)) {
                  entry.entity = entity;
                  break;
               }
            }
         }

         return entry.entity == null ? null : entry.entity.getPosition(tickDelta).add(0.0, entry.entity.getBoundingBox().getYsize() * 0.5, 0.0);
      }
   }

   private static final class Entry {
      private final CloudPackets.Data10 data10;
      private final long longValue;
      private long longValue2;
      private Entity entity;
      private UUID uUID;
      private boolean flag;
      private long longValue3;
      private final String text;
      private long longValue4 = Long.MIN_VALUE;
      private String text2 = "";

      private Entry(CloudPackets.Data10 data10, long longValue, long l) {
         this.data10 = data10;
         this.longValue = longValue;
         this.longValue2 = l;
         this.text = data10.username() + (data10.label().isEmpty() ? "" : ": " + data10.label());
      }

      private String meters(double d) {
         long l = Math.round(d);
         if (l != this.longValue4) {
            this.longValue4 = l;
            this.text2 = l + " м";
         }

         return this.text2;
      }

      private boolean method6414(CloudPackets.Data10 data10) {
         return this.data10.username().equalsIgnoreCase(data10.username())
            && Math.floor(this.data10.getDoubleValue()) == Math.floor(data10.getDoubleValue())
            && Math.floor(this.data10.getDoubleValue2()) == Math.floor(data10.getDoubleValue2())
            && Math.floor(this.data10.getDoubleValue3()) == Math.floor(data10.getDoubleValue3());
      }
   }
}
