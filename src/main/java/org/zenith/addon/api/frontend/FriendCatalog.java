package org.zenith.addon.api.frontend;

import java.util.List;

public interface FriendCatalog {
   List<String> friends();

   boolean isFriend(String playerName);

   boolean add(String playerName);

   boolean remove(String playerName);
}
