package org.zenith.utility.misc;

import com.google.gson.reflect.TypeToken;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.zenith.Zenith;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.network.Cloud;

public class Friends extends JsonListStorage<String> {
   public Friends() {
      super("friends.json", "", (new TypeToken<Set<String>>() {}).getType(), ConcurrentHashMap::newKeySet);
      KeySetView keySet = ConcurrentHashMap.newKeySet();
      keySet.addAll(this.items);
      this.items = keySet;
   }

   public boolean method7626(Entity player) {
      return player instanceof Player player2 ? this.method7627(player2.getGameProfile().name()) : false;
   }

   public boolean method7627(String name) {
      return this.getItems().contains(name) || this.method7628(name);
   }

   public boolean method7628(String name) {
      if (name != null && !name.isEmpty()) {
         Zenith zenith = Zenith.getInstance();
         Cloud cloud = zenith == null ? null : zenith.getCloud();
         if (cloud != null && cloud.method7862()) {
            for (PartyPlayerInfo partyPlayerInfo : cloud.state().method8038()) {
               if (name.equalsIgnoreCase(partyPlayerInfo.getText2())) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void method7629(String name) {
      this.getItems().remove(name);
   }
}
