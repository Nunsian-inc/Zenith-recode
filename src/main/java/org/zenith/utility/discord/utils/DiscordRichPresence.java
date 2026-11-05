package org.zenith.utility.discord.utils;

import com.sun.jna.Structure;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordRichPresence extends Structure {
   public String largeImageKey;
   public String largeImageText;
   public String smallImageText;
   public String partyPrivacy;
   public long startTimestamp;
   public int instance;
   public String partyId;
   public int partySize;
   public long endTimestamp;
   public String details;
   public String joinSecret;
   public String spectateSecret;
   public String smallImageKey;
   public String matchSecret;
   public String state;
   public int partyMax;
   public String button_url_1;
   public String button_label_1;
   public String button_url_2;
   public String button_label_2;

   public DiscordRichPresence() {
      this.setStringEncoding("UTF-8");
   }

   public List<String> getFieldOrder() {
      return Arrays.asList(
         "state",
         "details",
         "startTimestamp",
         "endTimestamp",
         "largeImageKey",
         "largeImageText",
         "smallImageKey",
         "smallImageText",
         "partyId",
         "partySize",
         "partyMax",
         "partyPrivacy",
         "matchSecret",
         "joinSecret",
         "spectateSecret",
         "button_label_1",
         "button_url_1",
         "button_label_2",
         "button_url_2",
         "instance"
      );
   }

   public static class Builder {
      private final DiscordRichPresence essencePresence = new DiscordRichPresence();

      public DiscordRichPresence.Builder setSmallImage(String smallImage) {
         return this.setSmallImage(smallImage, "");
      }

      public DiscordRichPresence.Builder setState(String state) {
         if (state != null && !state.isEmpty()) {
            this.essencePresence.state = state.substring(0, Math.min(state.length(), 128));
         }

         return this;
      }

      public DiscordRichPresence.Builder setDetails(String details) {
         if (details != null && !details.isEmpty()) {
            this.essencePresence.details = details.substring(0, Math.min(details.length(), 128));
         }

         return this;
      }

      public DiscordRichPresence.Builder setLargeImage(String largeImageKey, String largeImageText) {
         this.essencePresence.largeImageKey = largeImageKey;
         this.essencePresence.largeImageText = largeImageText;
         return this;
      }

      public DiscordRichPresence.Builder setInstance(boolean flag) {
         if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
            && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
            this.essencePresence.instance = flag ? 1 : 0;
         }

         return this;
      }

      public DiscordRichPresence.Builder setButtons(RPCButton buttons) {
         return this.setButtons(Collections.singletonList(buttons));
      }

      public DiscordRichPresence.Builder setSmallImage(String smallImageKey, String smallImageText) {
         this.essencePresence.smallImageKey = smallImageKey;
         this.essencePresence.smallImageText = smallImageText;
         return this;
      }

      public DiscordRichPresence.Builder setButtons(List<RPCButton> buttons) {
         if (buttons != null && !buttons.isEmpty()) {
            int n = Math.min(buttons.size(), 2);
            this.essencePresence.button_label_1 = ((RPCButton)buttons.get(0)).getLabel();
            this.essencePresence.button_url_1 = ((RPCButton)buttons.get(0)).getUrl();
            if (n == 2) {
               this.essencePresence.button_label_2 = ((RPCButton)buttons.get(1)).getLabel();
               this.essencePresence.button_url_2 = ((RPCButton)buttons.get(1)).getUrl();
            }
         }

         return this;
      }

      public DiscordRichPresence.Builder setStartTimestamp(OffsetDateTime startTimestamp) {
         this.essencePresence.startTimestamp = startTimestamp.toEpochSecond();
         return this;
      }

      public DiscordRichPresence.Builder setSecrets(String matchSecret, String joinSecret, String spectateSecret) {
         if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
            && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
            this.essencePresence.matchSecret = matchSecret;
            this.essencePresence.joinSecret = joinSecret;
            this.essencePresence.spectateSecret = spectateSecret;
         }

         return this;
      }

      public DiscordRichPresence.Builder setButtons(RPCButton rpcButton, RPCButton rpcButton2) {
         this.setButtons(Arrays.asList(rpcButton, rpcButton2));
         return this;
      }

      public DiscordRichPresence.Builder setStartTimestamp(long startTimestamp) {
         this.essencePresence.startTimestamp = startTimestamp;
         return this;
      }

      public DiscordRichPresence.Builder setSecrets(String joinSecret, String spectateSecret) {
         if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
            && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
            this.essencePresence.joinSecret = joinSecret;
            this.essencePresence.spectateSecret = spectateSecret;
         }

         return this;
      }

      public DiscordRichPresence.Builder setEndTimestamp(long endTimestamp) {
         this.essencePresence.endTimestamp = endTimestamp;
         return this;
      }

      public DiscordRichPresence.Builder setEndTimestamp(OffsetDateTime endTimestamp) {
         this.essencePresence.endTimestamp = endTimestamp.toEpochSecond();
         return this;
      }

      public DiscordRichPresence.Builder setLargeImage(String largeImage) {
         return this.setLargeImage(largeImage, "");
      }

      public DiscordRichPresence build() {
         return this.essencePresence;
      }
   }
}
