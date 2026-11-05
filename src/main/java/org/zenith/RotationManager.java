package org.zenith;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Relative;
import org.zenith.base.Module;
import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.utility.misc.RotationEvent;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.TickingList;

public class RotationManager implements MinecraftAccess {
   private Rotation currentRotation = new Rotation(0.0F, 0.0F);
   private Rotation rotation = new Rotation(0.0F, 0.0F);
   private final TickingList<RotationRequest> tickingList = new TickingList<>();
   private final BcCameraTestnerv bcCameraTestnerv = new BcCameraTestnerv();
   private RotationRequest rotationRequest = new RotationRequest(this.currentRotation, () -> this.currentRotation, this.bcCameraTestnerv.getRotationConfig());
   private Module module = null;
   private Rotation rotation2 = Rotation.INSTANCE;
   private int intValue = 0;
   private final RotationManager.Handler handler;

   public Rotation method7844() {
      return this.currentRotation == null ? new Rotation(mc.player.getYRot(), mc.player.getXRot(), true) : this.currentRotation;
   }

   public boolean method7845() {
      return this.currentRotation == null;
   }

   public RotationManager() {
      this(EventManager::call, true);
   }

   public RotationManager(RotationManager.Handler var1, boolean var2) {
      this.handler = var1;
      if (var2) {
         EventManager.register(this);
      }
   }

   @EventTarget(3)
   public void method7846(HandleInputPostEvent var1) {
      if (mc.player != null) {
         if (mc.player.isDeadOrDying()) {
            if (this.currentRotation != null) {
               mc.player.setYRot(this.currentRotation.getFloatValue() + Mth.wrapDegrees(mc.player.getYRot() - this.currentRotation.getFloatValue()));
               mc.player.yBob = mc.player.getYRot();
               mc.player.yBobO = mc.player.getXRot();
               this.intValue = 2;
               this.currentRotation = null;
            }
         } else {
            this.handler.call(new Event5());
            Rotation var2 = new Rotation(mc.player.getYRot(), mc.player.getXRot(), true);
            TickingList.Entry var3 = this.tickingList.method16();
            if (var3 != null) {
               RotationRequest var4 = (RotationRequest)var3.obj;
               Rotation var5 = var4.getSupplier().get();
               if (var5 == null || var5.method3137()) {
                  var5 = this.currentRotation == null ? var2 : this.currentRotation;
               }

               this.rotation = this.currentRotation == null ? var2 : this.currentRotation;
               this.currentRotation = var5;
               this.rotationRequest = var4;
               this.module = var3.module;
               this.currentRotation = new Rotation(this.currentRotation.getFloatValue(), Mth.clamp(this.currentRotation.getFloatValue2(), -90.0F, 90.0F));
               this.intValue--;
            } else if (this.currentRotation != null) {
               this.module = null;
               Rotation var6 = this.bcCameraTestnerv.method1851(this.rotationRequest.getRotationConfig(), var2);
               if (var6 == null || var6.method3137()) {
                  var6 = var2;
               }

               this.rotation = this.currentRotation;
               this.currentRotation = var6;
               if (var2.method3131(var6).method5329(9.0F)) {
                  mc.player.setYRot(var6.getFloatValue() + Mth.wrapDegrees(mc.player.getYRot() - var6.getFloatValue()));
                  mc.player.yBob = mc.player.getYRot();
                  mc.player.yBobO = mc.player.getXRot();
                  this.intValue = 2;
                  this.currentRotation = null;
               } else {
                  this.currentRotation = new Rotation(
                     this.currentRotation.getFloatValue(), Mth.clamp(this.currentRotation.getFloatValue2(), -90.0F, 90.0F)
                  );
               }
            } else {
               this.rotation = new Rotation(mc.player.yRotLast, mc.player.xRotLast);
            }

            this.tickingList.tick();
            mc.pick(1.0F);
            this.handler.call(new Event2());
            if (this.currentRotation != null && this.rotation != null) {
            }
         }
      }
   }

   @EventTarget
   public void method7847(PacketEvent var1) {
      switch (var1.getPacket3()) {
         case ServerboundMovePlayerPacket var4:
            if (var4.getYRot(2.1474836E9F) != 2.1474836E9F) {
               RotationEvent var11 = new RotationEvent(
                  this.currentRotation != null
                     ? new Rotation(this.rotation2.getFloatValue(), this.rotation2.getFloatValue2(), true)
                     : new Rotation(mc.player.getYRot(), mc.player.getXRot(), true)
               );
               this.rotation2 = new Rotation(var4.getYRot(0.0F), var4.getXRot(0.0F), true);
               this.handler.call(var11);
               this.intValue--;
            }
            break;
         case ClientboundPlayerPositionPacket var5:
            if (mc.player != null) {
               RotationEvent var12 = new RotationEvent(
                  this.currentRotation != null
                     ? new Rotation(this.rotation2.getFloatValue(), this.rotation2.getFloatValue2(), true)
                     : new Rotation(mc.player.getYRot(), mc.player.getXRot(), true)
               );
               float var13 = var5.change().yRot();
               float var14 = var5.change().xRot();
               if (var5.relatives().contains(Relative.Y_ROT)) {
                  var13 += this.rotation2.getFloatValue();
               }

               if (var5.relatives().contains(Relative.X_ROT)) {
                  var14 += this.rotation2.getFloatValue2();
               }

               this.rotation2 = new Rotation(var13, var14, true);
               this.handler.call(var12);
               this.intValue--;
            }
            break;
         case ServerboundPongPacket var6:
            var6.id = var6.id;
            break;
         case ServerboundUseItemPacket var7:
            RotationEvent var8 = new RotationEvent(new Rotation(this.rotation2.getFloatValue(), this.rotation2.getFloatValue2(), true));
            this.rotation2 = new Rotation(var7.yRot(), var7.xRot(), true);
            this.handler.call(var8);
            break;
         case ClientboundContainerSetSlotPacket var15:
            break;
         case ClientboundContainerSetContentPacket var16:
            break;
         case ServerboundContainerClickPacket var10:
            var10.stateId = mc.player.containerMenu.getStateId();
            break;
         default:
      }
   }

   public void method7848(RotationRequest var1, int var2, Module var3, int var4) {
      this.tickingList.method14(new TickingList.Entry<>(var4, var2, var3, var1));
   }

   public void method7849(RotationRequest var1, int var2, Module var3) {
      this.tickingList.method14(new TickingList.Entry<>(1, var2, var3, var1));
   }

   @Generated
   public Rotation getCurrentRotation() {
      return this.currentRotation;
   }

   @Generated
   public Rotation getRotation() {
      return this.rotation;
   }

   @Generated
   public TickingList<RotationRequest> getTickingList() {
      return this.tickingList;
   }

   @Generated
   public BcCameraTestnerv getBcCameraTestnerv() {
      return this.bcCameraTestnerv;
   }

   @Generated
   public RotationRequest getRotationRequest() {
      return this.rotationRequest;
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public Rotation getRotation2() {
      return this.rotation2;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public RotationManager.Handler getHandler() {
      return this.handler;
   }

   @Generated
   public void setCurrentRotation(Rotation var1) {
      this.currentRotation = var1;
   }

   @FunctionalInterface
   public interface Handler {
      void call(Event var1);
   }
}
