package org.zenith.addon.api.frontend;

import java.util.List;

public interface FriendCatalog {
   List<String> friends();

   boolean isFriend(String var1);

   boolean add(String var1);

   boolean remove(String var1);
}
