package dev.porno.playerAnim.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;

public class PartKey {
   private static final Map<String, PartKey> existingKeys = Collections.synchronizedMap(new HashMap<>());
   @NotNull
   public static final PartKey HEAD = keyForId("head");
   @NotNull
   public static final PartKey BODY = keyForId("body");
   @NotNull
   public static final PartKey TORSO = keyForId("torso");
   @NotNull
   public static final PartKey RIGHT_ARM = keyForId("rightArm");
   @NotNull
   public static final PartKey LEFT_ARM = keyForId("leftArm");
   @NotNull
   public static final PartKey RIGHT_LEG = keyForId("rightLeg");
   @NotNull
   public static final PartKey LEFT_LEG = keyForId("leftLeg");
   @NotNull
   public static final PartKey RIGHT_ITEM = keyForId("rightItem");
   @NotNull
   public static final PartKey LEFT_ITEM = keyForId("leftItem");
   @NotNull
   public static final PartKey CAPE = keyForId("cape");
   @NotNull
   public static final PartKey ELYTRA = keyForId("elytra");
   @NotNull
   private final String key;

   private PartKey(@NotNull String key) {
      this.key = key;
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException("PartKey is an object key type and must not be cloned.");
   }

   @NotNull
   public static PartKey keyForId(@NotNull String id) {
      return existingKeys.computeIfAbsent(id, PartKey::new);
   }

   @NotNull
   @Generated
   public String getKey() {
      return this.key;
   }
}
