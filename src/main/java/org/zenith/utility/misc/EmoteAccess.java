package org.zenith.utility.misc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.zenith.Zenith;

public final class EmoteAccess {
   private EmoteAccess() {
   }

   public static List<String> method7916() {
      EmoteManager emoteManager = method7924();
      return emoteManager == null ? List.of() : emoteManager.getEmoteRegistry().method1592().stream().filter(EmoteAccess::method7925).toList();
   }

   public static Collection<EmoteInfo> method7917() {
      EmoteManager emoteManager = method7924();
      return emoteManager == null ? List.of() : emoteManager.getEmoteRegistry().all().stream().filter(var0x -> method7925(var0x.getText())).toList();
   }

   public static Optional<EmoteInfo> method7918(String text) {
      EmoteManager emoteManager = method7924();
      return emoteManager != null && method7925(text) ? emoteManager.getEmoteRegistry().method1591(text) : Optional.empty();
   }

   public static boolean method7919(String string) {
      EmoteManager emoteManager = method7924();
      return emoteManager != null && emoteManager.getEmoteRegistry().method1591(string).isPresent();
   }

   public static boolean method7920(String text) {
      EmoteManager emoteManager = method7924();
      return emoteManager != null && method7925(text) && emoteManager.method1688(text);
   }

   public static boolean method7921(UUID uuid, String text, int n) {
      EmoteManager emoteManager = method7924();
      return emoteManager != null && emoteManager.method1689(uuid, text, n, EmoteManager.Type.LOCAL);
   }

   public static void method7922(UUID uuid) {
      EmoteManager emoteManager = method7924();
      if (emoteManager != null) {
         emoteManager.method1690(uuid);
      }
   }

   public static boolean method7923(UUID uuid) {
      EmoteManager emoteManager = method7924();
      return emoteManager != null && emoteManager.method1691(uuid);
   }

   private static EmoteManager method7924() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getEmoteManager();
   }

   private static boolean method7925(String text) {
      return text != null && !text.isBlank();
   }
}
