package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;

public final class ChatSendEvent extends CancellableEvent {
   private String message;
   private ChatSendEvent.Type type;
   private ChatSendEvent.Type2 type2;

   public boolean method4602() {
      return this.type2 == ChatSendEvent.Type2.COMMAND;
   }

   public boolean method4603() {
      return this.type == ChatSendEvent.Type.PRE;
   }

   @Generated
   public String getMessage() {
      return this.message;
   }

   @Generated
   public ChatSendEvent.Type getType() {
      return this.type;
   }

   @Generated
   public ChatSendEvent.Type2 getType2() {
      return this.type2;
   }

   @Generated
   public ChatSendEvent(String message, ChatSendEvent.Type type, ChatSendEvent.Type2 type2) {
      this.message = message;
      this.type = type;
      this.type2 = type2;
   }

   public static enum Type {
      PRE,
      POST;
   }

   public static enum Type2 {
      COMMAND,
      MESSAGE;
   }
}
