package org.zenith.utility.misc;

import dev.redstones.mediaplayerinfo.IMediaSession;
import org.zenith.utility.config.MediaHelperProcess;

public final class MediaPlayerState {
   private static final MediaPlayerState INSTANCE = new MediaPlayerState();
   private static final long CONST_longValue = 650L;
   private String owner = "";
   private float floatValue = -1.0F;
   private float floatValue2 = -1.0F;
   private long changedAt;
   private boolean flag;
   private float floatValue3 = 1.0F;
   private float floatValue4 = -1.0F;
   private long longValue;
   private volatile float floatValue5;
   private volatile long longValue2;

   private MediaPlayerState() {
   }

   public static MediaPlayerState getInstance() {
      return INSTANCE;
   }

   public void method1669(String owner) {
      String str = owner == null ? "" : owner;
      if (!str.equals(this.owner)) {
         this.owner = str;
         this.floatValue = -1.0F;
         this.flag = false;
         this.floatValue4 = -1.0F;
         MediaHelperProcess.getInstance().method6863(this.owner);
         MediaHelperProcess.getInstance().method6868(this.owner);
         MediaHelperProcess.getInstance().method6867(this.owner);
      }
   }

   public void method1670(float floatValue) {
      if (!(floatValue < 0.0F)) {
         if (!this.flag) {
            this.floatValue = floatValue;
         }
      }
   }

   public void method1671(float f) {
      if (f >= 0.0F) {
         this.floatValue5 = f;
         this.longValue2 = System.currentTimeMillis();
      }
   }

   public float method1672() {
      if (System.currentTimeMillis() - this.longValue2 > 600L) {
         return -1.0F;
      } else {
         float f = this.flag && this.floatValue4 > 0.01F ? this.floatValue5 / Math.max(0.05F, this.floatValue3) : this.floatValue5;
         return Math.min(1.0F, f);
      }
   }

   public boolean method1673() {
      return this.floatValue >= 0.0F;
   }

   public float method1674() {
      return this.flag && this.floatValue4 >= 0.0F ? this.floatValue4 : this.floatValue;
   }

   public long getChangedAt() {
      return this.changedAt;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void method1675(float clamp01) {
      IMediaSession iMediaSession = MediaSessionManager.getInstance().getIMediaSession();
      if (iMediaSession != null) {
         float floatValue = Math.max(0.0F, Math.min(1.0F, clamp01));
         this.changedAt = System.currentTimeMillis();
         if (this.flag) {
            this.floatValue4 = floatValue;
         } else {
            this.floatValue = floatValue;
            MediaHelperProcess.getInstance().method6861(iMediaSession.getOwner(), floatValue);
         }
      }
   }

   public void method1676(float f) {
      if (!this.method1673()) {
         IMediaSession iMediaSession = MediaSessionManager.getInstance().getIMediaSession();
         if (iMediaSession != null) {
            MediaHelperProcess.getInstance().method6863(iMediaSession.getOwner());
         }

         this.floatValue = 0.5F;
      }

      this.method1675(this.method1674() + f);
   }

   public void method1677(boolean flag, float f, boolean enabled) {
      IMediaSession iMediaSession = MediaSessionManager.getInstance().getIMediaSession();
      long now = System.currentTimeMillis();
      if (enabled && iMediaSession != null) {
         if (flag) {
            this.longValue = now;
            if (!this.flag && this.method1673()) {
               this.floatValue4 = this.floatValue;
               this.floatValue3 = f;
               this.flag = true;
               MediaHelperProcess.getInstance().method6862(iMediaSession.getOwner(), Math.max(0.0F, this.floatValue4 * f), 260);
            }
         } else {
            if (this.flag && now - this.longValue > 650L) {
               this.method1678(iMediaSession);
            }
         }
      } else {
         if (this.flag) {
            this.method1678(iMediaSession);
         }
      }
   }

   private void method1678(IMediaSession iMediaSession) {
      this.flag = false;
      if (this.floatValue4 >= 0.0F) {
         this.floatValue = this.floatValue4;
         if (iMediaSession != null) {
            MediaHelperProcess.getInstance().method6862(iMediaSession.getOwner(), this.floatValue4, 900);
         }
      }

      this.floatValue4 = -1.0F;
   }

   public float method1679() {
      float f = this.method1674();
      if (this.floatValue2 < 0.0F) {
         this.floatValue2 = f;
      }

      this.floatValue2 = this.floatValue2 + (f - this.floatValue2) * 0.25F;
      return this.floatValue2;
   }
}
