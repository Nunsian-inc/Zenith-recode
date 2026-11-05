package org.zenith.base.bot.client;

import net.minecraft.network.chat.Component;

public final class ChatMessage {
   private final String time;
   private final Component text;

   public String time() {
      return this.time;
   }

   public Component text() {
      return this.text;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ChatMessage other)) return false;
      return java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.text, other.text);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(time, text);
   }

   @Override
   public String toString() {
      return "ChatMessage[" + "time=" + this.time + ", " + "text=" + this.text + "]";
   }

   public ChatMessage(String var1, Component var2) {
      this.time = var1;
      this.text = var2;
   }
}
