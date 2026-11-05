package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "XrayBypass",
   category = ModuleCategory.MISC,
   description = "module.xraybypass.description"
)
public final class XrayBypassModule extends Module {
   public static final XrayBypassModule INSTANCE = new XrayBypassModule();
   private final SliderSetting range = new SliderSetting("module.xrayBypass.range", 30.0F, 1.0F, 128.0F, 2.0F, "module.xrayBypass.range.desc", "b");
   private final SliderSetting height = new SliderSetting("module.xrayBypass.height", 20.0F, 1.0F, 255.0F, 2.0F, "module.xrayBypass.height.desc", "b");
   private final SliderSetting delay = new SliderSetting("module.xrayBypass.delay", 3.0F, 1.0F, 5.0F, 1.0F, "module.xrayBypass.delay.desc", "t");
   private BlockPos blockPos;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private boolean flag = true;
   private final LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
   private StopWatch stopWatch = new StopWatch();

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onEnable() {
      if (mc.player != null && mc.level != null) {
         this.method4213(mc.player.blockPosition(), this.range.getValue3(), this.height.getValue3());
         this.stopWatch = new StopWatch();
         this.stopWatch.reset();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   private void method4213(BlockPos blockPosition, float f, float f2) {
      int x = blockPosition.getX();
      int y = blockPosition.getY();
      int z = blockPosition.getZ();
      int intValue = x - (int)f;
      this.intValue5 = z - (int)f;
      this.intValue4 = y;
      this.floatValue = x + f;
      this.floatValue3 = z + f;
      this.floatValue2 = y + f2;
      this.intValue = intValue;
      this.intValue3 = this.intValue5;
      this.intValue2 = this.intValue4;
      this.flag = !(this.intValue <= this.floatValue) || !(this.intValue3 <= this.floatValue3) || !(this.intValue2 <= this.floatValue2);
      this.longOpenHashSet.clear();
      if (!this.flag) {
         int n = intValue;

         while (n + 1 <= this.floatValue) {
            n++;
         }

         int n2 = this.intValue5;

         while (n2 + 1 <= this.floatValue3) {
            n2++;
         }

         for (int i = intValue >> 4; i <= n >> 4; i++) {
            for (int j = this.intValue5 >> 4; j <= n2 >> 4; j++) {
               if (mc.level.hasChunk(i, j)) {
                  this.longOpenHashSet.add(ChunkPos.pack(i, j));
               }
            }
         }
      }

      this.blockPos = this.method4214();
   }

   private BlockPos method4214() {
      while (!this.flag) {
         int intValue = this.intValue;
         int y = this.intValue2;
         int z = this.intValue3;
         if (++this.intValue2 > this.floatValue2) {
            this.intValue2 = this.intValue4;
            if (++this.intValue3 > this.floatValue3) {
               this.intValue3 = this.intValue5;
               if (++this.intValue > this.floatValue) {
                  this.flag = true;
               }
            }
         }

         if (this.longOpenHashSet.contains(ChunkPos.pack(intValue >> 4, z >> 4))) {
            return new BlockPos(intValue, y, z);
         }
      }

      return null;
   }

   @EventTarget
   public void method4215(WorldRenderEvent event) {
      BlockHitResult blockHitResult = TargetUtils.method3216(mc.player.getEyePosition(1.0F), rotationManager.method7844(), 5.0, blockHitResult2 -> true);
      if (blockHitResult.getType() != Type.MISS) {
         GlowUtil.method2230(new AABB(blockHitResult.getBlockPos()), Color.RED.getRGB(), 1.0F);
      }

      if (this.blockPos != null) {
         GlowUtil.method2230(new AABB(this.blockPos), Color.GREEN.getRGB(), 1.0F);
         GlowUtil.method2230(
            mc.player
               .getBoundingBox()
               .inflate(this.range.getValue3(), 0.0, this.range.getValue3())
               .setMaxY(mc.player.position().y + this.height.getValue3()),
            Zenith.getInstance().getStyleManager().getClientColor(90).method7287(),
            1.0F
         );
      }
   }

   @EventTarget
   public void method4216(PacketEvent event) {
      if (event.getPacket3() instanceof ClientboundBlockUpdatePacket) {
         this.stopWatch.reset();
      }
   }

   @EventTarget
   public void method4217(Event5 event) {
      Rotation rotation = new Rotation(rotationManager.method7844().getFloatValue(), -90.0F);
      rotationManager.method7849(new RotationRequest(rotation, () -> rotation, rotationManager.getBcCameraTestnerv().getRotationConfig3()), 50, this);
   }

   @EventTarget(4)
   public void method4218(MoveInputEvent event) {
      event.method3044();
   }

   @EventTarget
   public void method4219(UpdateEvent event) {
      BlockHitResult blockHitResult = TargetUtils.method3216(mc.player.getEyePosition(1.0F), rotationManager.method7844(), 5.0, blockHitResult2 -> true);
      if (blockHitResult.getType() != Type.MISS) {
         if (mc.player.tickCount % 80 == 0) {
            Zenith.getInstance().getHudHolder().method6973("4", Component.nullToEmpty("Сломайте блок над  головой"), 4000L);
         }

         this.stopWatch.reset();
      } else {
         if (this.stopWatch.method7546(20000L)) {
            this.stopWatch.reset();
         }

         if (!this.stopWatch.method7546(1000L)) {
            for (int i = 0; i < this.delay.getValue3(); i++) {
               if (this.blockPos != null) {
                  BlockPos blockPos = this.blockPos;
                  UseItemUtils.method6502(var1x -> new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos, Direction.UP, var1x));
                  UseItemUtils.method6502(var1x -> new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP, var1x));
                  this.blockPos = this.method4214();
               }
            }
         }
      }
   }
}
