package org.zenith.base.figura.avatar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public final class AvatarManager {
   private static final ConcurrentHashMap<UUID, UserData> LOADED_USERS = new ConcurrentHashMap<>();

   private AvatarManager() {
   }

   public static UserData getOrCreate(UUID owner) {
      if (owner == null) {
         throw new IllegalArgumentException("Avatar owner UUID cannot be null");
      } else {
         return LOADED_USERS.computeIfAbsent(owner, UserData::new);
      }
   }

   public static UserData getUser(UUID owner) {
      return owner == null ? null : LOADED_USERS.get(owner);
   }

   public static boolean contains(UUID owner) {
      if (owner == null) {
         return false;
      }

      UserData user = LOADED_USERS.get(owner);
      return user != null && !user.getAvatars().isEmpty();
   }

   public static boolean contains(UUID owner, AvatarLayer layer) {
      UserData user = owner == null ? null : LOADED_USERS.get(owner);
      return user != null && !user.getAvatars(layer).isEmpty();
   }

   public static Avatar getAvatarForPlayer(UUID owner) {
      UserData user = owner == null ? null : LOADED_USERS.get(owner);
      return user == null ? null : user.getMainAvatar();
   }

   public static Collection<Avatar> getAvatars(UUID owner) {
      return getAvatars(owner, AvatarLayer.BODY);
   }

   public static List<Avatar> getAvatars(UUID owner, AvatarLayer layer) {
      UserData user = owner == null ? null : LOADED_USERS.get(owner);
      return user == null ? List.of() : user.getAvatars(layer);
   }

   public static List<Avatar> getWeaponAvatars(UUID owner) {
      return getAvatars(owner, AvatarLayer.WEAPON);
   }

   public static List<Avatar> getItemAvatars(UUID owner) {
      List<Avatar> weapons = getAvatars(owner, AvatarLayer.WEAPON);
      return weapons.isEmpty() ? getAvatars(owner, AvatarLayer.BODY) : weapons;
   }

   public static List<Avatar> getLayers(UUID owner) {
      UserData user = owner == null ? null : LOADED_USERS.get(owner);
      return user == null ? List.of() : List.copyOf(user.getAvatars());
   }

   public static List<Avatar> getAllAvatars() {
      List<Avatar> result = new ArrayList<>();

      for (UserData user : LOADED_USERS.values()) {
         result.addAll(user.getAvatars());
      }

      return List.copyOf(result);
   }

   public static Avatar getAvatar(Entity entity) {
      return entity == null ? null : getAvatarForPlayer(entity.getUUID());
   }

   public static Avatar getAvatar(EntityRenderState state) {
      if (state instanceof AvatarRenderState playerState) {
         Entity entity = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(playerState.id);
         return getAvatar(entity);
      } else {
         return null;
      }
   }

   public static void clear(UUID owner) {
      if (owner != null) {
         UserData user = LOADED_USERS.remove(owner);
         if (user != null) {
            user.clear();
         }
      }
   }

   public static void clear(UUID owner, AvatarLayer layer) {
      UserData user = owner == null ? null : LOADED_USERS.get(owner);
      if (user != null) {
         user.clear(layer);
         if (user.getAvatars().isEmpty()) {
            LOADED_USERS.remove(owner, user);
         }
      }
   }

   public static void tick() {
      for (UserData user : LOADED_USERS.values()) {
         for (Avatar avatar : user.getAvatars()) {
            avatar.tick();
         }
      }
   }

   public static void applyAnimations() {
      for (UserData user : LOADED_USERS.values()) {
         for (Avatar avatar : user.getAvatars()) {
            avatar.applyAnimations();
         }
      }
   }

   public static void clearAnimations() {
      for (UserData user : LOADED_USERS.values()) {
         for (Avatar avatar : user.getAvatars()) {
            avatar.clearAnimations();
         }
      }
   }

   public static void clearAll() {
      for (UserData user : LOADED_USERS.values()) {
         user.clear();
      }

      LOADED_USERS.clear();
   }
}
