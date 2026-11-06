package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.OutgoingPacketEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.network.BlinkUtils;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "TrapTp",
   description = "module.traptp.description",
   category = ModuleCategory.COMBAT
)
public final class TrapTpModule extends Module {
   public static final TrapTpModule INSTANCE = new TrapTpModule();
   private static final long CONST_longValue = 2000L;
   private static final double CONST_doubleValue = 2.0;
   private static final double CONST_doubleValue2 = 5.0;
   private final ModeSetting mode = new ModeSetting(
      "module.trapTp.mode", "module.trapTp.mode.desc", "module.serverHelper.normalTrap", "module.serverHelper.explosiveTrap"
   );
   private final Queue<Packet<?>> queue = new ConcurrentLinkedQueue<>();
   private Vec3 vec3d;
   private long longValue;
   private boolean flag;

   private TrapTpModule() {
   }

   @Override
   public void onEnable() {
      this.queue.clear();
      this.flag = false;
      if (mc.player != null && mc.level != null && mc.getConnection() != null) {
         this.vec3d = mc.player.position();
         this.longValue = System.currentTimeMillis();
         super.onEnable();
         InventoryUtils.method927(this.method1522());
      } else {
         super.onEnable();
         this.disableSelf();
      }
   }

   @Override
   public void onDisable() {
      this.method1525();
      this.vec3d = null;
      this.longValue = 0L;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player == null || mc.level == null) {
         this.queue.clear();
         this.disableSelf();
      } else if (mc.player.isDeadOrDying()) {
         this.method1520(true);
      } else {
         if (this.vec3d == null || this.method1524() || System.currentTimeMillis() - this.longValue >= 2000L) {
            this.disableSelf();
         }
      }
   }

   @EventTarget
   public void method1519(WorldRenderEvent event) {
      if (this.vec3d != null && mc.player != null) {
         int color = Zenith.getInstance().getStyleManager().getClientColor(90).method7287();
         this.method1526(event.getMatrixStack(), this.method1523(), color);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && event.getPacket() != null) {
         if (event.getPacket() instanceof ClientboundDisconnectPacket) {
            mc.execute(() -> this.method1520(false));
         } else if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> this.method1520(true));
         }
      }
   }

   private void method1520(boolean flag) {
      if (this.isEnabled()) {
         if (flag) {
            this.queue.removeIf(BlinkUtils::method1444);
         } else {
            this.queue.clear();
         }

         this.disableSelf();
      }
   }

   @EventTarget(0)
   public void method1521(OutgoingPacketEvent event) {
      if (!event.isCancelled() && !this.flag && !BlinkModule.INSTANCE.method6732() && event.getPacket4() != null) {
         if (mc.player != null && mc.level != null) {
            event.cancel();
            this.queue.add(event.getPacket4());
         } else {
            this.queue.clear();
         }
      }
   }

   public boolean isFlag6() {
      return this.flag;
   }

   private Item method1522() {
      return this.mode.isSelected(1) ? Items.PRISMARINE_SHARD : Items.POPPED_CHORUS_FRUIT;
   }

   private double method1523() {
      return this.mode.isSelected(1) ? 7.0 : 2.0;
   }

   private boolean method1524() {
      if (mc.player != null && this.vec3d != null) {
         double d = mc.player.getX() - this.vec3d.x;
         double d2 = mc.player.getZ() - this.vec3d.z;
         double d3 = this.method1523() + 1.0;
         return d * d + d2 * d2 > d3 * d3;
      } else {
         return true;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method1525() {
      if (!this.queue.isEmpty() && mc.getConnection() != null) {
         this.flag = true;

         Packet packet;
         try {
            while ((packet = this.queue.poll()) != null) {
               mc.getConnection().send(packet);
            }
         } catch (Throwable e) {
            this.flag = false;
            this.queue.clear();
            throw e;
         }

         this.flag = false;
         this.queue.clear();
      } else {
         this.queue.clear();
         this.flag = false;
      }
   }

   private void method1526(PoseStack matrixStack, double floatValue, int color) {
      Vec3 vec = this.vec3d.add(0.0, 0.02, 0.0);
      Vec3 vec2 = vec.subtract(mc.getEntityRenderDispatcher().camera.position());
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderState.method1898("core/position_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      int n = 0;

      for (byte i = 90; n <= i; n++) {
         Vec3 vec3 = MathUtils.method2653(n, i, floatValue);
         Vec3 vec4 = MathUtils.method2653(n + 1, i, floatValue);
         GlowUtil.method2235(
            matrixStack, begin, vec2.add(vec3), vec2.add(vec3.x, vec3.y + 2.0, vec3.z), ColorUtils.method6439(color, 0.2F), ColorUtils.method6439(color, 0.0F)
         );
         GlowUtil.method2245(vec.add(vec3), vec.add(vec4), color, 2.0F, true);
      }

      n = 0;

      for (byte i = 90; n <= i; n++) {
         Vec3 vec3 = MathUtils.method2653(n, i, floatValue);
         GlowUtil.method2235(
            matrixStack, begin, vec2.add(vec3), vec2.add(vec3.x, vec3.y - 2.0, vec3.z), ColorUtils.method6439(color, 0.2F), ColorUtils.method6439(color, 0.0F)
         );
      }

      ImmediateRenderer.method8015(begin.buildOrThrow());
      RenderState.method1876();
      RenderState.method1879();
      RenderState.method1882(true);
      RenderState.method1872();
      GL11.glDisable(2881);
   }
}
