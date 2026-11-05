package org.zenith.utility.config;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.client.events.TickEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.network.Cloud;

public class FiguraManager implements MinecraftAccess {
   private Path path = null;
   private Path path2 = null;
   private Path path3 = null;
   private Path path4 = null;
   private static final long CONST_longValue = 1000L;
   private final Map<FiguraManager.Data2, FiguraManager.Data> items = new HashMap<>();
   private boolean flag;
   private final Map<UUID, FiguraManager.Data3> items2 = new HashMap<>();
   private final MultiSelectSetting render = new MultiSelectSetting("cosmetics.figura.render");

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(field -> {
         try {
            field.setAccessible(true);
            return field.get(this);
         } catch (Exception e) {
            return null;
         }
      }).filter(obj -> obj instanceof Setting).map(obj -> (Setting)obj).collect(Collectors.toList());
   }

   public boolean method7664() {
      return !this.render.method5222("cosmetics.figura.render.self").isEnabled() && !this.render.method5222("cosmetics.figura.render.friends").isEnabled();
   }

   public boolean method7665() {
      return this.render.method5222("cosmetics.figura.render.self").isEnabled();
   }

   public boolean method7666() {
      return this.render.method5222("cosmetics.figura.render.friends").isEnabled();
   }

   public FiguraManager() {
      new MultiSelectSetting.Entry(this.render, "cosmetics.figura.render.self", true);
      new MultiSelectSetting.Entry(this.render, "cosmetics.figura.render.friends", true);
      EventManager.register(this);
   }

   @EventTarget
   public void onTick(TickEvent event) {
      if (mc.player != null) {
         this.method7674();
         this.method7667();
         AvatarManager.tick();
      } else {
         this.path2 = null;
         this.path4 = null;
         this.items.clear();
         if (!this.items2.isEmpty()) {
            this.method7676(null);
         }
      }
   }

   private void method7667() {
      UUID uuid = mc.player.getUUID();
      this.path2 = this.method7668(uuid, AvatarLayer.BODY, this.path, this.path2);
      this.path4 = this.method7668(uuid, AvatarLayer.WEAPON, this.path3, this.path4);
   }

   private Path method7668(UUID uuid, AvatarLayer avatarLayer, Path path, Path path2) {
      boolean flag;
      boolean flag2 = (flag = avatarLayer == AvatarLayer.WEAPON) ? AvatarUtil.method7250(uuid) : AvatarUtil.method7249(uuid);
      if (this.method7665() && path != null && this.method7680(path)) {
         if (flag2 && method7681(path, path2)) {
            return path2;
         } else if (!flag2 && !this.method7670(uuid, avatarLayer, path)) {
            return path;
         } else {
            if (flag) {
               AvatarUtil.method7246(uuid, path);
            } else {
               AvatarUtil.method7244(uuid, path);
            }

            return path;
         }
      } else {
         if (flag2) {
            method7669(uuid, avatarLayer);
         }

         return null;
      }
   }

   private static void method7669(UUID uuid, AvatarLayer avatarLayer) {
      if (avatarLayer == AvatarLayer.WEAPON) {
         AvatarUtil.method7253(uuid);
      } else {
         AvatarUtil.method7252(uuid);
      }
   }

   private boolean method7670(UUID uuid, AvatarLayer avatarLayer, Object path) {
      long now = System.currentTimeMillis();
      FiguraManager.Data2 data2 = new FiguraManager.Data2(uuid, avatarLayer);
      FiguraManager.Data data = this.items.get(data2);
      if (data != null && data.getObj().equals(path) && now - data.getLongValue() < 1000L) {
         return false;
      } else {
         this.items.put(data2, new FiguraManager.Data(path, now));
         return true;
      }
   }

   private void method7671(UUID key) {
      if (key != null && !this.items.isEmpty()) {
         this.items.remove(new FiguraManager.Data2(key, AvatarLayer.BODY));
         this.items.remove(new FiguraManager.Data2(key, AvatarLayer.WEAPON));
      }
   }

   public void method7672(Path path) {
      if (!method7681(this.path, path)) {
         this.path = path;
         this.path2 = null;
         if (mc.player != null) {
            AvatarUtil.method7255(mc.player.getUUID());
            this.items.remove(new FiguraManager.Data2(mc.player.getUUID(), AvatarLayer.BODY));
            if (path == null) {
               AvatarUtil.method7252(mc.player.getUUID());
            }
         }
      }
   }

   public void method7673(Path path3) {
      if (!method7681(this.path3, path3)) {
         this.path3 = path3;
         this.path4 = null;
         if (mc.player != null) {
            AvatarUtil.method7256(mc.player.getUUID());
            this.items.remove(new FiguraManager.Data2(mc.player.getUUID(), AvatarLayer.WEAPON));
            if (path3 == null) {
               AvatarUtil.method7253(mc.player.getUUID());
            }
         }
      }
   }

   private void method7674() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.level != null && minecraft.player != null) {
         if (!this.method7666()) {
            if (!this.flag) {
               this.flag = true;
               this.method7676(null);
            }
         } else {
            if (this.flag) {
               this.flag = false;
               this.items.clear();
            }

            Zenith zenith = Zenith.getInstance();
            Cloud cloud = zenith == null ? null : zenith.getCloud();
            String str = cloud != null && cloud.method7862() ? cloud.method7911() : "";
            HashSet hashSet = new HashSet();
            HashSet hashSet2 = new HashSet();
            if (!str.isEmpty()) {
               for (PartyPlayerInfo partyPlayerInfo : cloud.state().method8038()) {
                  hashSet2.add(partyPlayerInfo.uid());
                  if (!cloud.prefs().method3316(partyPlayerInfo.username()) && str.equals(partyPlayerInfo.server())) {
                     ShareCodec.Data2 data2 = ShareCodec.method6724(partyPlayerInfo.uid(), partyPlayerInfo.getItems());
                     if (!data2.body().isEmpty() || !data2.getText2().isEmpty()) {
                        UUID key = method7677(minecraft, partyPlayerInfo.getText2());
                        if (key != null) {
                           hashSet.add(key);
                           FiguraManager.Data3 data3 = this.items2.getOrDefault(key, new FiguraManager.Data3("", ""));
                           this.method7675(key, AvatarLayer.BODY, data2.body(), data3.body());
                           this.method7675(key, AvatarLayer.WEAPON, data2.getText2(), data3.getText2());
                           this.items2.put(key, new FiguraManager.Data3(data2.body(), data2.getText2()));
                        }
                     }
                  }
               }
            }

            ShareCodec.method6725(hashSet2);
            this.method7676(hashSet);
         }
      }
   }

   private void method7675(UUID key, AvatarLayer avatarLayer, String body, String anObject) {
      boolean flag;
      boolean flag2 = (flag = avatarLayer == AvatarLayer.WEAPON) ? AvatarUtil.method7250(key) : AvatarUtil.method7249(key);
      if (body.isEmpty()) {
         if (!anObject.isEmpty() && flag2) {
            method7669(key, avatarLayer);
         }
      } else if (!flag2 || !body.equals(anObject)) {
         if (this.method7670(key, avatarLayer, body)) {
            if (flag) {
               AvatarUtil.method7245(key, body);
            } else {
               AvatarUtil.method7243(key, body);
            }
         }
      }
   }

   private void method7676(Set<UUID> uuids) {
      var iterator = this.items2.entrySet().iterator();

      while (iterator.hasNext()) {
         UUID key = (UUID)((Entry)iterator.next()).getKey();
         if (uuids == null || !uuids.contains(key)) {
            AvatarUtil.method7251(key);
            this.method7671(key);
            iterator.remove();
         }
      }
   }

   private static UUID method7677(Minecraft minecraft, String str) {
      if (str != null && !str.isBlank()) {
         for (AbstractClientPlayer player : minecraft.level.players()) {
            if (player != minecraft.player && str.equalsIgnoreCase(player.getGameProfile().name())) {
               return player.getUUID();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String method7678() {
      return this.method7680(this.path) ? AvatarUtil.method7257(this.path) : "";
   }

   public String method7679() {
      return this.method7680(this.path3) ? AvatarUtil.method7257(this.path3) : "";
   }

   public boolean method7680(Path path) {
      return path != null && !AvatarUtil.method7257(path).isBlank();
   }

   private static boolean method7681(Path path, Path path2) {
      return path != null && path2 != null ? path.toAbsolutePath().normalize().equals(path2.toAbsolutePath().normalize()) : path == path2;
   }

   @Generated
   public Path getPath() {
      return this.path;
   }

   @Generated
   public Path getPath2() {
      return this.path2;
   }

   @Generated
   public Path getPath3() {
      return this.path3;
   }

   @Generated
   public Path getPath4() {
      return this.path4;
   }

   @Generated
   public MultiSelectSetting getRender() {
      return this.render;
   }

   private static final class Data {
      private final Object obj;
      private final long longValue;

      public Object obj() {
         return this.obj;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.obj, other.obj) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(obj, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "obj=" + this.obj + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(Object path, long longValue) {
         this.obj = path;
         this.longValue = longValue;
      }

      public Object getObj() {
         return this.obj;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final UUID uUID;
      private final AvatarLayer avatarLayer;

      public UUID uUID() {
         return this.uUID;
      }

      public AvatarLayer avatarLayer() {
         return this.avatarLayer;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.avatarLayer, other.avatarLayer);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, avatarLayer);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "avatarLayer=" + this.avatarLayer + "]";
      }

      private Data2(UUID uUID, AvatarLayer avatarLayer) {
         this.uUID = uUID;
         this.avatarLayer = avatarLayer;
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public AvatarLayer getAvatarLayer() {
         return this.avatarLayer;
      }
   }

   private static final class Data3 {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      private Data3(String text, String str) {
         this.text = text;
         this.text2 = str;
      }

      public String body() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }
}
