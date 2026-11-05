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
   public void onUpdate(UpdateEvent var1) {
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
   public void method1519(WorldRenderEvent var1) {
      if (this.vec3d != null && mc.player != null) {
         int var2 = Zenith.getInstance().getStyleManager().getClientColor(90).method7287();
         this.method1526(var1.getMatrixStack(), this.method1523(), var2);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() != null) {
         if (var1.getPacket3() instanceof ClientboundDisconnectPacket) {
            mc.execute(() -> this.method1520(false));
         } else if (var1.getPacket3() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> this.method1520(true));
         }
      }
   }

   private void method1520(boolean var1) {
      if (this.isEnabled()) {
         if (var1) {
            this.queue.removeIf(BlinkUtils::method1444);
         } else {
            this.queue.clear();
         }

         this.disableSelf();
      }
   }

   @EventTarget(0)
   public void method1521(OutgoingPacketEvent var1) {
      if (!var1.isCancelled() && !this.flag && !BlinkModule.INSTANCE.method6732() && var1.getPacket4() != null) {
         if (mc.player != null && mc.level != null) {
            var1.cancel();
            this.queue.add(var1.getPacket4());
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
         double var1 = mc.player.getX() - this.vec3d.x;
         double var3 = mc.player.getZ() - this.vec3d.z;
         double var5 = this.method1523() + 1.0;
         return var1 * var1 + var3 * var3 > var5 * var5;
      } else {
         return true;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method1525() {
      if (!this.queue.isEmpty() && mc.getConnection() != null) {
         this.flag = true;

         Packet var1;
         try {
            while ((var1 = this.queue.poll()) != null) {
               mc.getConnection().send(var1);
            }
         } catch (Throwable var4) {
            this.flag = false;
            this.queue.clear();
            throw var4;
         }

         this.flag = false;
         this.queue.clear();
      } else {
         this.queue.clear();
         this.flag = false;
      }
   }

   private void method1526(PoseStack var1, double var2, int var4) {
      Vec3 var5 = this.vec3d.add(0.0, 0.02, 0.0);
      Vec3 var6 = var5.subtract(mc.getEntityRenderDispatcher().camera.position());
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderState.method1898("core/position_color");
      BufferBuilder var7 = Tesselator.getInstance().begin(PrimitiveTopology.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      int var8 = 0;

      for (byte var9 = 90; var8 <= var9; var8++) {
         Vec3 var10 = MathUtils.method2653(var8, var9, var2);
         Vec3 var11 = MathUtils.method2653(var8 + 1, var9, var2);
         GlowUtil.method2235(
            var1, var7, var6.add(var10), var6.add(var10.x, var10.y + 2.0, var10.z), ColorUtils.method6439(var4, 0.2F), ColorUtils.method6439(var4, 0.0F)
         );
         GlowUtil.method2245(var5.add(var10), var5.add(var11), var4, 2.0F, true);
      }

      var8 = 0;

      for (byte var13 = 90; var8 <= var13; var8++) {
         Vec3 var14 = MathUtils.method2653(var8, var13, var2);
         GlowUtil.method2235(
            var1, var7, var6.add(var14), var6.add(var14.x, var14.y - 2.0, var14.z), ColorUtils.method6439(var4, 0.2F), ColorUtils.method6439(var4, 0.0F)
         );
      }

      ImmediateRenderer.method8015(var7.buildOrThrow());
      RenderState.method1876();
      RenderState.method1879();
      RenderState.method1882(true);
      RenderState.method1872();
      GL11.glDisable(2881);
   }
}
