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
   public void setAbstractClientPlayerEntity(AbstractClientPlayer var1) {
      this.abstractClientPlayerEntity = var1;
   }

   @Generated
   public void setItemStack(ItemStack var1) {
      this.itemStack = var1;
   }

   @Generated
   public void setHand(InteractionHand var1) {
      this.hand = var1;
   }

   @Generated
   public HeldItemRenderEvent(AbstractClientPlayer var1, ItemStack var2, InteractionHand var3) {
      this.abstractClientPlayerEntity = var1;
      this.itemStack = var2;
      this.hand = var3;
   }
}
