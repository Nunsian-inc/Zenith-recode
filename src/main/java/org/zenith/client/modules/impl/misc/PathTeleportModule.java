package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "PathTeleport",
   description = "module.pathteleport.description",
   category = ModuleCategory.MISC
)
public final class PathTeleportModule extends Module {
   public static final PathTeleportModule INSTANCE = new PathTeleportModule();
   private static final float CONST_floatValue = 10.0F;
   private static final float CONST_floatValue2 = 20.0F;
   private static final float CONST_floatValue3 = 15.0F;
   private final BindSetting recordingKey = new BindSetting("module.pathTeleport.recordingKey", "module.pathTeleport.recordingKey.desc", -1);
   private final BindSetting moveKey = new BindSetting("module.pathTeleport.moveKey", "module.pathTeleport.moveKey.desc", -1);
   private final List<PathTeleportModule.Data> items = new ArrayList<>();
   private boolean flag = false;
   private boolean flag2 = false;
   private int intValue = 0;

   private PathTeleportModule() {
   }

   @Override
   public void onEnable() {
      this.flag = false;
      this.flag2 = false;
      this.intValue = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.flag2 = false;
      this.intValue = 0;
      super.onDisable();
   }

   @EventTarget
   public void method6874(KeyEvent event) {
      if (mc.player != null) {
         if (event.isPress(this.recordingKey.getKeyCode())) {
            this.method6876();
         }

         if (event.isPress(this.moveKey.getKeyCode())) {
            this.method6877();
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.flag2) {
         event.method3044();
      }
   }

   @EventTarget
   public void method6875(ScreenRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         this.method6881(event, "Recording " + this.flag + "  " + KeyName.method6701(this.recordingKey.getKeyCode()), 20.0F);
         this.method6881(event, "Moving " + this.flag2 + "  " + KeyName.method6701(this.moveKey.getKeyCode()), 35.0F);
         this.method6881(event, "Points " + this.items.size(), 50.0F);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (this.flag) {
            this.method6878();
         } else if (!this.flag2) {
            this.intValue = 0;
         } else if (this.items.isEmpty()) {
            this.flag2 = false;
            this.intValue = 0;
         } else if (this.intValue >= this.items.size()) {
            this.flag2 = false;
            this.intValue = 0;
         } else {
            this.method6880(this.items.get(this.intValue));
            this.intValue++;
         }
      }
   }

   private void method6876() {
      this.flag = !this.flag;
      if (this.flag) {
         this.flag2 = false;
         this.intValue = 0;
         this.items.clear();
         this.method6878();
      }
   }

   private void method6877() {
      if (this.flag2) {
         this.flag2 = false;
         this.intValue = 0;
      } else if (this.items.isEmpty()) {
         ChatLogger.method2021("PathTeleport: сначала запиши маршрут");
      } else {
         this.flag = false;
         this.flag2 = true;
         this.intValue = 0;
      }
   }

   private void method6878() {
      PathTeleportModule.Data data = new PathTeleportModule.Data(
         mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround()
      );
      this.items.add(data);
   }

   private boolean method6879(PathTeleportModule.Data data, PathTeleportModule.Data data2) {
      return Math.abs(data.doubleValue - data2.doubleValue) > 0.001
         || Math.abs(data.doubleValue2 - data2.doubleValue2) > 0.001
         || Math.abs(data.doubleValue3 - data2.doubleValue3) > 0.001
         || Math.abs(data.floatValue - data2.floatValue) > 0.05F
         || Math.abs(data.floatValue2 - data2.floatValue2) > 0.05F
         || data.flag != data2.flag;
   }

   private void method6880(PathTeleportModule.Data data) {
      mc.player.setDeltaMovement(0.0, 0.0, 0.0);
      mc.player.setOnGround(data.flag);
      mc.player.setPos(data.doubleValue, data.doubleValue2, data.doubleValue3);
      UseItemUtils.method6504(
         new PosRot(data.doubleValue, data.doubleValue2, data.doubleValue3, data.floatValue, data.floatValue2, data.flag, mc.player.horizontalCollision)
      );
   }

   private void method6881(ScreenRenderEvent event, String text, float centeredTextY) {
      float floatValue = mc.getWindow().getGuiScaledWidth() - Fonts.NEW_MEDIUM.getWidth(text, 10.0F) - 20.0F;
      event.getMenuDrawContext().drawText(Fonts.NEW_MEDIUM.getFont(10.0F), text, floatValue, centeredTextY, ZenithColor.INSTANCE);
   }

   private static final class Data {
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;

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

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, doubleValue2, doubleValue3, floatValue, floatValue2, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + "]";
      }

      private Data(double doubleValue, double y, double z, float floatValue, float xRot, boolean flag) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = y;
         this.doubleValue3 = z;
         this.floatValue = floatValue;
         this.floatValue2 = xRot;
         this.flag = flag;
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

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}
