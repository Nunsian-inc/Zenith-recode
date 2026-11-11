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
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.events.PreRotationEvent;
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
   private Rotation serverRotation = Rotation.INSTANCE;
   private int intValue = 0;
   private final RotationManager.Handler handler;

   public Rotation getEffectiveRotation() {
      return this.currentRotation == null ? new Rotation(mc.player.getYRot(), mc.player.getXRot(), true) : this.currentRotation;
   }

   public boolean method7845() {
      return this.currentRotation == null;
   }

   public RotationManager() {
      this(EventManager::call, true);
   }

   public RotationManager(RotationManager.Handler handler, boolean registerEvents) {
      this.handler = handler;
      if (registerEvents) {
         EventManager.register(this);
      }
   }

   @EventTarget(3)
   public void method7846(HandleInputPostEvent event) {
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
            this.handler.call(new PreRotationEvent());
            Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot(), true);
            TickingList.Entry entry = this.tickingList.peekEntry();
            if (entry != null) {
               RotationRequest rotationRequest = (RotationRequest)entry.value;
               Rotation currentRotation = rotationRequest.getSupplier().get();
               if (currentRotation == null || currentRotation.isInvalid()) {
                  currentRotation = this.currentRotation == null ? rotation : this.currentRotation;
               }

               this.rotation = this.currentRotation == null ? rotation : this.currentRotation;
               this.currentRotation = currentRotation;
               this.rotationRequest = rotationRequest;
               this.module = entry.module;
               this.currentRotation = new Rotation(this.currentRotation.getFloatValue(), Mth.clamp(this.currentRotation.getFloatValue2(), -90.0F, 90.0F));
               this.intValue--;
            } else if (this.currentRotation != null) {
               this.module = null;
               Rotation currentRotation = this.bcCameraTestnerv.method1851(this.rotationRequest.getRotationConfig(), rotation);
               if (currentRotation == null || currentRotation.isInvalid()) {
                  currentRotation = rotation;
               }

               this.rotation = this.currentRotation;
               this.currentRotation = currentRotation;
               if (rotation.wrappedDeltaTo(currentRotation).isWithinBounds(9.0F)) {
                  mc.player.setYRot(currentRotation.getFloatValue() + Mth.wrapDegrees(mc.player.getYRot() - currentRotation.getFloatValue()));
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
            this.handler.call(new PostRotationEvent());
            if (this.currentRotation != null && this.rotation != null) {
            }
         }
      }
   }

   @EventTarget
   public void method7847(PacketEvent event) {
      switch (event.getPacket()) {
         case ServerboundMovePlayerPacket movePlayerPacket:
            if (movePlayerPacket.getYRot(2.1474836E9F) != 2.1474836E9F) {
               RotationEvent event2 = new RotationEvent(
                  this.currentRotation != null
                     ? new Rotation(this.serverRotation.getFloatValue(), this.serverRotation.getFloatValue2(), true)
                     : new Rotation(mc.player.getYRot(), mc.player.getXRot(), true)
               );
               this.serverRotation = new Rotation(movePlayerPacket.getYRot(0.0F), movePlayerPacket.getXRot(0.0F), true);
               this.handler.call(event2);
               this.intValue--;
            }
            break;
         case ClientboundPlayerPositionPacket playerPositionPacket:
            if (mc.player != null) {
               RotationEvent event2 = new RotationEvent(
                  this.currentRotation != null
                     ? new Rotation(this.serverRotation.getFloatValue(), this.serverRotation.getFloatValue2(), true)
                     : new Rotation(mc.player.getYRot(), mc.player.getXRot(), true)
               );
               float yRot = playerPositionPacket.change().yRot();
               float xRot = playerPositionPacket.change().xRot();
               if (playerPositionPacket.relatives().contains(Relative.Y_ROT)) {
                  yRot += this.serverRotation.getFloatValue();
               }

               if (playerPositionPacket.relatives().contains(Relative.X_ROT)) {
                  xRot += this.serverRotation.getFloatValue2();
               }

               this.serverRotation = new Rotation(yRot, xRot, true);
               this.handler.call(event2);
               this.intValue--;
            }
            break;
         case ServerboundPongPacket serverboundPongPacket:
            serverboundPongPacket.id = serverboundPongPacket.id;
            break;
         case ServerboundUseItemPacket useItemPacket:
            RotationEvent event2 = new RotationEvent(new Rotation(this.serverRotation.getFloatValue(), this.serverRotation.getFloatValue2(), true));
            this.serverRotation = new Rotation(useItemPacket.yRot(), useItemPacket.xRot(), true);
            this.handler.call(event2);
            break;
         case ClientboundContainerSetSlotPacket containerSetSlotPacket:
            break;
         case ClientboundContainerSetContentPacket setContentPacket:
            break;
         case ServerboundContainerClickPacket containerClickPacket:
            containerClickPacket.stateId = mc.player.containerMenu.getStateId();
            break;
         default:
      }
   }

   public void requestRotation(RotationRequest rotationRequest, int priority, Module ownerModule, int lifetimeTicks) {
      this.tickingList.addOrReplace(new TickingList.Entry<>(lifetimeTicks, priority, ownerModule, rotationRequest));
   }

   public void requestRotation(RotationRequest rotationRequest, int priority, Module ownerModule) {
      this.tickingList.addOrReplace(new TickingList.Entry<>(1, priority, ownerModule, rotationRequest));
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
      return this.serverRotation;
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
   public void setCurrentRotation(Rotation currentRotation) {
      this.currentRotation = currentRotation;
   }

   @FunctionalInterface
   public interface Handler {
      void call(Event event);
   }
}
