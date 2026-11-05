package org.zenith.utility.discord.callbacks;

import com.sun.jna.Callback;

public interface DisconnectedCallback extends Callback {
   void apply(int n, String str);
}
