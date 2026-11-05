package org.zenith.utility.misc;

import net.minecraft.resources.Identifier;
import org.zenith.utility.api.CosmeticAnimation;
import org.zenith.utility.api.CosmeticItemInfo;
import org.zenith.utility.api.Overrides;

public final class CosmeticModel {
   private final CosmeticItemInfo cosmeticItemInfo;
   private final CosmeticSkeleton cosmeticSkeleton;
   private final Identifier[] identifiers;
   private final CosmeticAnimation cosmeticAnimation;
   private final CosmeticType cosmeticType;
   private final Util31.Data data;
   private final boolean flag;
   private static final float CONST_floatValue = 0.5F;
   private static final float CONST_floatValue2 = 0.75F;

   public CosmeticModel(CosmeticItemInfo cosmeticItemInfo, CosmeticSkeleton cosmeticSkeleton, Identifier[] identifiers, CosmeticAnimation cosmeticAnimation, Overrides overrides) {
      this.cosmeticItemInfo = cosmeticItemInfo;
      this.cosmeticSkeleton = cosmeticSkeleton;
      this.identifiers = identifiers;
      this.cosmeticAnimation = cosmeticAnimation;
      this.cosmeticType = overrides == null ? cosmeticItemInfo.getCosmeticType() : overrides.method6462(cosmeticItemInfo);
      Util31.Data data = Util31.method7852(cosmeticItemInfo.getData(), cosmeticItemInfo.getType(), this.cosmeticType);
      Overrides.Data data2 = overrides == null ? null : overrides.method6458(cosmeticItemInfo.getText());
      this.data = data2 != null ? overrides.method6463(cosmeticItemInfo.getText(), data) : Util23.method5990(data, this.cosmeticType, cosmeticSkeleton.method7145());
      this.flag = cosmeticItemInfo.method7751() && this.cosmeticType.method1683() != null && method5977(cosmeticSkeleton, this.data, this.cosmeticType);
   }

   private static boolean method5977(CosmeticSkeleton cosmeticSkeleton, Util31.Data data, CosmeticType cosmeticType) {
      float[] floats = cosmeticSkeleton.method7145();
      float f = Math.abs((floats[3] - floats[0]) * data.getFloatValue7());
      boolean flag = cosmeticType == CosmeticType.LEFT_ARM || cosmeticType == CosmeticType.RIGHT_ARM;
      return f < (flag ? 0.75F : 0.5F);
   }

   public CosmeticItemInfo getCosmeticItemInfo() {
      return this.cosmeticItemInfo;
   }

   public CosmeticType getCosmeticType() {
      return this.cosmeticType;
   }

   public CosmeticSkeleton getCosmeticSkeleton() {
      return this.cosmeticSkeleton;
   }

   public Identifier[] getIdentifiers() {
      return this.identifiers;
   }

   public Identifier texture() {
      return this.identifiers[0];
   }

   public CosmeticAnimation getCosmeticAnimation() {
      return this.cosmeticAnimation;
   }

   public Util31.Data getData() {
      return this.data;
   }

   public boolean isFlag() {
      return this.flag;
   }
}
