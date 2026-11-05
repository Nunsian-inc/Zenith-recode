package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "FireWorkESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.fireworkesp.description"
)
public final class FireWorkESPModule extends Module {
   public static final FireWorkESPModule INSTANCE = new FireWorkESPModule();
   private final Map<Vec3, FireWorkESPModule.Entry> items = new ConcurrentHashMap<>();
   private ClientLevel clientWorld;
   private final SliderSetting scale = new SliderSetting("module.fireworkESP.scale", 0.7F, 0.4F, 1.6F, 0.1F, "module.fireworkESP.scale.desc", "x");
   private final SliderSetting lifetime = new SliderSetting("module.fireworkESP.lifetime", 10.0F, 1.0F, 30.0F, 1.0F, "module.fireworkESP.lifetime.desc", "s");
   private final BooleanSetting throughWalls = new BooleanSetting("module.fireworkESP.throughWalls", "module.fireworkESP.throughWalls.desc", false);
   private final List<FireWorkESPModule.Data> items2 = new ArrayList<>();
   private long longValue;
   private final Random random = new Random();

   private FireWorkESPModule() {
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.FIREWORK;
   }

   private long method237() {
      return (long)(this.lifetime.getValue3() * 1000.0F);
   }

   @EventTarget
   public void method238(EntityAddEvent event) {
      if (mc.player != null && mc.level != null) {
         if (event.getEntity() instanceof FireworkRocketEntity fireworkRocketEntity) {
            this.method239();
            this.items.put(fireworkRocketEntity.position(), new FireWorkESPModule.Entry(System.currentTimeMillis()));
         }
      }
   }

   private void method239() {
      if (mc.level != this.clientWorld) {
         this.items.clear();
         this.clientWorld = mc.level;
      }
   }

   @EventTarget
   public void method240(DrawEvent event) {
      if (mc.player != null && mc.level != null) {
         this.method239();
         long now = System.currentTimeMillis();
         long l = this.method237();
         var iterator = this.items.entrySet().iterator();

         while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            long l2 = now - ((FireWorkESPModule.Entry)entry.getValue()).longValue;
            if (l2 > l) {
               iterator.remove();
            } else {
               this.method241(event.getCustomDrawContext(), (Vec3)entry.getKey(), (FireWorkESPModule.Entry)entry.getValue(), l2);
            }
         }
      }
   }

   private void method241(CustomDrawContext customDrawContext, Vec3 key, FireWorkESPModule.Entry entry, long l) {
      if (customDrawContext != null) {
         Vec3 vec = ProjectionUtils.method301(key);
         if (vec != null && !(vec.z <= 0.0) && !(vec.z >= 1.0)) {
            if (this.throughWalls.isEnabled() || this.method242(key, entry)) {
               this.method245(customDrawContext, (float)vec.x, (float)vec.y, l);
            }
         }
      }
   }

   private boolean method242(Vec3 key, FireWorkESPModule.Entry entry) {
      if (mc.level != null && mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null) {
         long gameTime = mc.level.getGameTime();
         if (entry.longValue2 != gameTime) {
            entry.longValue2 = gameTime;
            Vec3 position = mc.gameRenderer.mainCamera().position();
            BlockHitResult clip = mc.level.clip(new ClipContext(position, key, Block.COLLIDER, Fluid.NONE, mc.player));
            entry.visible = clip.getType() == Type.MISS || clip.getLocation().distanceToSqr(position) + 1.0E-4 >= key.distanceToSqr(position);
         }

         return entry.visible;
      } else {
         return true;
      }
   }

   public void method243(CustomDrawContext customDrawContext, float floatValue, float y, float width, float height) {
      if (customDrawContext != null) {
         long now = System.currentTimeMillis();
         long l = this.method237();
         this.items2.removeIf(var4x -> now - var4x.getLongValue() > l);
         long l2 = Math.max(300L, l / 5L);
         if (now - this.longValue >= l2) {
            this.longValue = now;
            this.items2.add(new FireWorkESPModule.Data(0.18F + this.random.nextFloat() * 0.64F, 0.12F + this.random.nextFloat() * 0.76F, now));
         }

         for (FireWorkESPModule.Data data : this.items2) {
            this.method245(customDrawContext, floatValue + width * data.getFloatValue(), y + height * data.getFloatValue2(), now - data.getLongValue());
         }
      }
   }

   public void method244() {
      this.items2.clear();
      this.longValue = 0L;
   }

   private void method245(CustomDrawContext customDrawContext, float x, float y, long l) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font font = Fonts.NEW_MEDIUM.getFont(6.5F);
      String text = String.format("%.1fс", l / 1000.0);
      float intValue = GuiStyle.PADDING.intValue();
      float f = 10.4F;
      float width = intValue + f + intValue + font.width(text) + intValue;
      float panelHeight = Math.max(font.height(), f) + GuiStyle.PADDING.intValue();
      float floatValue = x - width / 2.0F;
      float y2 = y - panelHeight / 2.0F;
      float f2 = this.scale.getValue3();
      this.pushCenteredScale(customDrawContext, x, y, f2, f2);
      EntityESPModule entityESPModule = EntityESPModule.INSTANCE;
      ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() / 2.0F);
      customDrawContext.drawBlurHudBooleanCheck(
         floatValue,
         y2,
         width,
         panelHeight,
         22.0F,
         moduleMenuRadius,
         ZenithColor.INSTANCE,
         entityESPModule.getBlur().isEnabled() && entityESPModule.getBlur().isVisible(),
         entityESPModule.getGlow().isEnabled() && entityESPModule.getGlow().isVisible()
      );
      customDrawContext.drawRoundedRect(floatValue, y2, width, panelHeight, moduleMenuRadius, currentStyle.getHudBackground().getZenithColor());
      customDrawContext.drawText(font, text, floatValue + intValue + f + intValue, y2 + (panelHeight - font.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor());
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(floatValue + intValue, y2 + (panelHeight - f) / 2.0F);
      customDrawContext.pose().scale(0.65F, 0.65F);
      customDrawContext.item(Items.FIREWORK_ROCKET.getDefaultInstance(), 0, 0);
      customDrawContext.popMatrix();
      this.pop(customDrawContext);
   }

   private void pushCenteredScale(CustomDrawContext customDrawContext, float x, float y, float f, float y2) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().scale(f, y2);
      customDrawContext.pose().translate(-x, -y);
   }

   private void pop(CustomDrawContext customDrawContext) {
      customDrawContext.popMatrix();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.clientWorld = mc.level;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.clientWorld = null;
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final long longValue;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(float floatValue, float f, long longValue) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.longValue = longValue;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private final long longValue;
      private long longValue2 = Long.MIN_VALUE;
      private boolean visible;

      private Entry(long longValue) {
         this.longValue = longValue;
      }
   }
}
