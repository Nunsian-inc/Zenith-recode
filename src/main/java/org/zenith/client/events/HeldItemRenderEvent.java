package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class HeldItemRenderEvent implements Event {
   private AbstractClientPlayer abstractClientPlayerEntity;
   private ItemStack itemStack;
   private InteractionHand hand;

   @Generated
   public AbstractClientPlayer getAbstractClientPlayerEntity() {
      return this.abstractClientPlayerEntity;
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public InteractionHand getHand() {
      return this.hand;
   }

   @Generated
   public void setAbstractClientPlayerEntity(AbstractClientPlayer abstractClientPlayerEntity) {
      this.abstractClientPlayerEntity = abstractClientPlayerEntity;
   }

   @Generated
   public void setItemStack(ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   @Generated
   public void setHand(InteractionHand hand) {
      this.hand = hand;
   }

   @Generated
   public HeldItemRenderEvent(AbstractClientPlayer abstractClientPlayerEntity, ItemStack itemStack, InteractionHand hand) {
      this.abstractClientPlayerEntity = abstractClientPlayerEntity;
      this.itemStack = itemStack;
      this.hand = hand;
   }
}
