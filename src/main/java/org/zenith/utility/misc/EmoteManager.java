package org.zenith.utility.misc;

import dev.porno.playerAnim.api.IPlayer;
import dev.porno.playerAnim.api.layered.AnimationContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.EmoteRegistry;

public final class EmoteManager {
   private static final int CONST_intValue = 1000;
   private static final long CONST_longValue = 30000000000L;
   private final EmoteRegistry emoteRegistry = new EmoteRegistry();
   private final AtomicLong atomicLong = new AtomicLong();
   private final Map<UUID, EmoteManager.Data2> items = new ConcurrentHashMap<>();
   private final Map<UUID, EmoteManager.Data> items2 = new ConcurrentHashMap<>();
   private ClientLevel clientWorld;
   private final MultiSelectSetting render = new MultiSelectSetting("cosmetics.emotes.render");

   public EmoteManager() {
      new MultiSelectSetting.Entry(this.render, "cosmetics.emotes.render.self", true);
      new MultiSelectSetting.Entry(this.render, "cosmetics.emotes.render.friends", true);
      ClientTickEvents.END_CLIENT_TICK.register(this::method1695);
   }

   public EmoteRegistry getEmoteRegistry() {
      return this.emoteRegistry;
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(field -> {
         try {
            field.setAccessible(true);
            return field.get(this);
         } catch (ReflectiveOperationException e) {
            return null;
         }
      }).filter(Setting.class::isInstance).map(Setting.class::cast).collect(Collectors.toList());
   }

   public boolean method1688(String text) {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft.player != null && this.method1689(minecraft.player.getUUID(), text, 0, EmoteManager.Type.LOCAL);
   }

   public boolean method1689(UUID uuid, String text, int n, EmoteManager.Type type) {
      if (uuid != null && type != null) {
         EmoteInfo emoteInfo = this.emoteRegistry.method1591(text).orElse(null);
         if (emoteInfo == null) {
            return false;
         } else {
            method1702(() -> this.method1694(uuid, emoteInfo, n, type));
            return true;
         }
      } else {
         return false;
      }
   }

   public void method1690(UUID uuid) {
      if (uuid != null) {
         method1702(() -> this.method1699(uuid));
      }
   }

   public boolean method1691(UUID uuid) {
      EmoteManager.Data data = this.items2.get(uuid);
      return data != null && data.getPreviewEmotePlayer().isActive();
   }

   public String method1692(UUID uuid) {
      EmoteManager.Data2 data2 = this.items.get(uuid);
      return data2 == null ? "" : data2.getEmoteInfo().getText();
   }

   public void method1693() {
      method1702(this::method1701);
   }

   private void method1694(UUID uuid, EmoteInfo emoteInfo, int n, EmoteManager.Type type) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != this.clientWorld) {
         this.method1701();
         this.clientWorld = minecraft.level;
      }

      if (this.method1698(minecraft, uuid, type)) {
         this.method1699(uuid);
      } else {
         EmoteManager.Data2 data2 = new EmoteManager.Data2(emoteInfo, Math.max(0, n), System.nanoTime(), this.atomicLong.incrementAndGet(), type);
         this.items.put(uuid, data2);
         this.method1696(minecraft, uuid, data2);
      }
   }

   private void method1695(Minecraft minecraft) {
      if (minecraft.level != this.clientWorld) {
         this.method1701();
         this.clientWorld = minecraft.level;
      }

      if (minecraft.level != null) {
         long nanoTime = System.nanoTime();

         for (Entry entry : new ArrayList<>(this.items.entrySet())) {
            UUID key = (UUID)entry.getKey();
            EmoteManager.Data2 data2 = (EmoteManager.Data2)entry.getValue();
            if (this.method1698(minecraft, key, data2.getType())) {
               this.method1699(key);
            } else {
               AbstractClientPlayer abstractClientPlayerEntity = method1703(minecraft.level, key);
               if (abstractClientPlayerEntity == null) {
                  if (nanoTime - data2.getLongValue() > 30000000000L) {
                     this.method1699(key);
                  }
               } else {
                  this.method1697(abstractClientPlayerEntity, data2, nanoTime);
                  EmoteManager.Data data = this.items2.get(key);
                  if (data != null && data.getLongValue() == data2.getLongValue2() && !data.getPreviewEmotePlayer().isActive()) {
                     this.method1699(key);
                  }
               }
            }
         }
      }
   }

   private void method1696(Minecraft minecraft, UUID uuid, EmoteManager.Data2 data2) {
      if (minecraft.level != null) {
         AbstractClientPlayer abstractClientPlayerEntity = method1703(minecraft.level, uuid);
         if (abstractClientPlayerEntity != null) {
            this.method1697(abstractClientPlayerEntity, data2, System.nanoTime());
         }
      }
   }

   private void method1697(AbstractClientPlayer abstractClientPlayerEntity, EmoteManager.Data2 data2, long nanoTime) {
      UUID uuid = abstractClientPlayerEntity.getUUID();
      EmoteManager.Data data = this.items2.get(uuid);
      if (data == null || data.getAbstractClientPlayerEntity() != abstractClientPlayerEntity || data.getLongValue() != data2.getLongValue2()) {
         if (data != null) {
            method1700(data);
         }

         AnimationContainer animationContainer = new AnimationContainer(null);
         ((IPlayer)abstractClientPlayerEntity).playerAnimator$getAnimationStack().addAnimLayer(1000, animationContainer);
         long l = Math.max(0L, (nanoTime - data2.getLongValue()) / 50000000L);
         int n = (int)Math.min(2147483647L, data2.getIntValue() + l);
         PreviewEmotePlayer previewEmotePlayer = new PreviewEmotePlayer(data2.getEmoteInfo(), n);
         animationContainer.setAnim(previewEmotePlayer);
         this.items2.put(uuid, new EmoteManager.Data(abstractClientPlayerEntity, animationContainer, previewEmotePlayer, data2.getLongValue2()));
      }
   }

   private boolean method1698(Minecraft minecraft, UUID uuid, EmoteManager.Type type) {
      if (type == EmoteManager.Type.CLOUD) {
         return !this.render.method5222("cosmetics.emotes.render.friends").isEnabled();
      } else {
         boolean flag = minecraft.player != null && minecraft.player.getUUID().equals(uuid);
         return flag && !this.render.method5222("cosmetics.emotes.render.self").isEnabled();
      }
   }

   private void method1699(UUID key) {
      this.items.remove(key);
      EmoteManager.Data data = this.items2.remove(key);
      if (data != null) {
         method1700(data);
      }
   }

   private static void method1700(EmoteManager.Data data) {
      data.getPreviewEmotePlayer().stop();
      data.getAnimationContainer().setAnim(null);
      ((IPlayer)data.getAbstractClientPlayerEntity()).playerAnimator$getAnimationStack().removeLayer(data.getAnimationContainer());
   }

   private void method1701() {
      for (EmoteManager.Data data : this.items2.values()) {
         method1700(data);
      }

      this.items2.clear();
      this.items.clear();
   }

   private static void method1702(Runnable command) {
      Minecraft minecraft = Minecraft.getInstance();
      if (!minecraft.isSameThread()) {
         minecraft.execute(command);
      } else {
         command.run();
      }
   }

   public void close() {
      this.method1693();
   }

   private static AbstractClientPlayer method1703(ClientLevel level, UUID key) {
      return level.getPlayerByUUID(key) instanceof AbstractClientPlayer player ? player : null;
   }

   private static final class Data {
      private final AbstractClientPlayer abstractClientPlayerEntity;
      private final AnimationContainer<PreviewEmotePlayer> animationContainer;
      private final PreviewEmotePlayer previewEmotePlayer;
      private final long longValue;

      public AbstractClientPlayer abstractClientPlayerEntity() {
         return this.abstractClientPlayerEntity;
      }

      public AnimationContainer<PreviewEmotePlayer> animationContainer() {
         return this.animationContainer;
      }

      public PreviewEmotePlayer previewEmotePlayer() {
         return this.previewEmotePlayer;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.abstractClientPlayerEntity, other.abstractClientPlayerEntity) && java.util.Objects.equals(this.animationContainer, other.animationContainer) && java.util.Objects.equals(this.previewEmotePlayer, other.previewEmotePlayer) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(abstractClientPlayerEntity, animationContainer, previewEmotePlayer, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "abstractClientPlayerEntity=" + this.abstractClientPlayerEntity + ", " + "animationContainer=" + this.animationContainer + ", " + "previewEmotePlayer=" + this.previewEmotePlayer + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(AbstractClientPlayer abstractClientPlayerEntity, AnimationContainer<PreviewEmotePlayer> animationContainer, PreviewEmotePlayer previewEmotePlayer, long longValue) {
         this.abstractClientPlayerEntity = abstractClientPlayerEntity;
         this.animationContainer = animationContainer;
         this.previewEmotePlayer = previewEmotePlayer;
         this.longValue = longValue;
      }

      public AbstractClientPlayer getAbstractClientPlayerEntity() {
         return this.abstractClientPlayerEntity;
      }

      public AnimationContainer<PreviewEmotePlayer> getAnimationContainer() {
         return this.animationContainer;
      }

      public PreviewEmotePlayer getPreviewEmotePlayer() {
         return this.previewEmotePlayer;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final EmoteInfo emoteInfo;
      private final int intValue;
      private final long longValue;
      private final long longValue2;
      private final EmoteManager.Type type;

      public EmoteInfo emoteInfo() {
         return this.emoteInfo;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public EmoteManager.Type type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.emoteInfo, other.emoteInfo) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(emoteInfo, intValue, longValue, longValue2, type);
      }

      @Override
      public String toString() {
         return "Data2[" + "emoteInfo=" + this.emoteInfo + ", " + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "type=" + this.type + "]";
      }

      private Data2(EmoteInfo emoteInfo, int intValue, long longValue, long incrementAndGet, EmoteManager.Type type) {
         this.emoteInfo = emoteInfo;
         this.intValue = intValue;
         this.longValue = longValue;
         this.longValue2 = incrementAndGet;
         this.type = type;
      }

      public EmoteInfo getEmoteInfo() {
         return this.emoteInfo;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public EmoteManager.Type getType() {
         return this.type;
      }
   }

   public static enum Type {
      LOCAL,
      CLOUD;
   }
}
