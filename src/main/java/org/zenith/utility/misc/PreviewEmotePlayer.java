package org.zenith.utility.misc;

import dev.porno.playerAnim.api.layered.KeyframeAnimationPlayer;

public final class PreviewEmotePlayer extends KeyframeAnimationPlayer {
   private final EmoteInfo emoteInfo;

   public PreviewEmotePlayer(EmoteInfo emoteInfo, int n) {
      super(emoteInfo.getKeyframeAnimation(), Math.max(0, n));
      this.emoteInfo = emoteInfo;
   }

   public EmoteInfo getEmoteInfo() {
      return this.emoteInfo;
   }
}
