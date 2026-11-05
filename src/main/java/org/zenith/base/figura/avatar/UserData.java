package org.zenith.base.figura.avatar;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.nbt.CompoundTag;
import org.zenith.base.figura.FiguraMod;

public final class UserData {
   public final UUID id;
   private final Queue<Avatar> avatars = new ConcurrentLinkedQueue<>();

   public UserData(UUID id) {
      this.id = id;
   }

   public Avatar loadAvatar(CompoundTag nbt) {
      return this.loadAvatar(nbt, AvatarLayer.BODY);
   }

   public Avatar loadAvatar(CompoundTag nbt, AvatarLayer layer) {
      Avatar avatar = new Avatar(this.id, layer);
      this.avatars.add(avatar);
      avatar.load(nbt);
      FiguraMod.debug("Loaded local {} avatar for {}", avatar.layer, this.id);
      return avatar;
   }

   public Queue<Avatar> getAvatars() {
      return this.avatars;
   }

   public List<Avatar> getAvatars(AvatarLayer layer) {
      List<Avatar> result = new ArrayList<>(this.avatars.size());

      for (Avatar avatar : this.avatars) {
         if (avatar.layer == layer) {
            result.add(avatar);
         }
      }

      return List.copyOf(result);
   }

   public Avatar getMainAvatar() {
      for (Avatar avatar : this.avatars) {
         if (avatar.layer == AvatarLayer.BODY) {
            return avatar;
         }
      }

      return null;
   }

   public void clear() {
      for (Avatar avatar : this.avatars) {
         avatar.clean();
      }

      this.avatars.clear();
   }

   public void clear(AvatarLayer layer) {
      this.avatars.removeIf(avatar -> {
         if (avatar.layer != layer) {
            return false;
         }

         avatar.clean();
         return true;
      });
   }
}
