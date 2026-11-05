package org.zenith.addon.api.frontend;

import java.util.List;

public interface FriendCatalog {
   List<String> friends();

   boolean isFriend(String str);

   boolean add(String str);

   boolean remove(String str);
}
