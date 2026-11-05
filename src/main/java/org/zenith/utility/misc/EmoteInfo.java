package org.zenith.utility.misc;

import dev.porno.playerAnim.core.data.KeyframeAnimation;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.resources.Identifier;

public final class EmoteInfo {
   private final String text;
   private final UUID uUID;
   private final String text2;
   private final String text3;
   private final Identifier identifier;
   private final KeyframeAnimation keyframeAnimation;

   public String text() {
      return this.text;
   }

   public UUID uUID() {
      return this.uUID;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   public Identifier identifier() {
      return this.identifier;
   }

   public KeyframeAnimation keyframeAnimation() {
      return this.keyframeAnimation;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof EmoteInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.identifier, other.identifier) && java.util.Objects.equals(this.keyframeAnimation, other.keyframeAnimation);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, uUID, text2, text3, identifier, keyframeAnimation);
   }

   @Override
   public String toString() {
      return "EmoteInfo[" + "text=" + this.text + ", " + "uUID=" + this.uUID + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "identifier=" + this.identifier + ", " + "keyframeAnimation=" + this.keyframeAnimation + "]";
   }

   public EmoteInfo(String text, UUID uUID, String str, String str2, Identifier identifier, KeyframeAnimation keyframeAnimation) {
      Objects.requireNonNull(text, "id");
      Objects.requireNonNull(uUID, "animationUuid");
      Objects.requireNonNull(str, "displayName");
      Objects.requireNonNull(str2, "author");
      Objects.requireNonNull(identifier, "icon");
      Objects.requireNonNull(keyframeAnimation, "animation");
      this.text = text;
      this.uUID = uUID;
      this.text2 = str;
      this.text3 = str2;
      this.identifier = identifier;
      this.keyframeAnimation = keyframeAnimation;
   }

   public String getText() {
      return this.text;
   }

   public UUID getUUID() {
      return this.uUID;
   }

   public String getText2() {
      return this.text2;
   }

   public String author() {
      return this.text3;
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public KeyframeAnimation getKeyframeAnimation() {
      return this.keyframeAnimation;
   }
}
