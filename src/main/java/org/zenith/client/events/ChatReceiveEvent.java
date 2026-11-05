package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.network.chat.Component;
import org.zenith.CancellableEvent;

public class ChatReceiveEvent extends CancellableEvent {
   private Component text;
   private boolean flag;
   private boolean flag2;

   public ChatReceiveEvent(Component var1) {
      this.text = var1;
   }

   public void method7858(Component var1) {
      this.flag = true;
      this.text = var1;
      super.cancel();
   }

   public boolean method7859() {
      return this.flag && !this.flag2;
   }

   @Override
   public void setCancelled(boolean var1) {
      super.setCancelled(var1);
      this.flag2 = var1;
   }

   @Override
   public void cancel() {
      super.cancel();
      this.flag2 = true;
   }

   @Generated
   public Component getText() {
      return this.text;
   }

   @Generated
   public boolean isHidden() {
      return this.flag2;
   }
}
