package dev.porno.playerAnim.api.firstPerson;

import lombok.Generated;

public class FirstPersonConfiguration {
   boolean showRightArm = false;
   boolean showLeftArm = false;
   boolean showRightItem = true;
   boolean showLeftItem = true;
   boolean showArmor = false;

   public FirstPersonConfiguration(boolean showRightArm, boolean showLeftArm, boolean showRightItem, boolean showLeftItem) {
      this.showRightArm = showRightArm;
      this.showLeftArm = showLeftArm;
      this.showRightItem = showRightItem;
      this.showLeftItem = showLeftItem;
   }

   @Generated
   public boolean isShowRightArm() {
      return this.showRightArm;
   }

   @Generated
   public boolean isShowLeftArm() {
      return this.showLeftArm;
   }

   @Generated
   public boolean isShowRightItem() {
      return this.showRightItem;
   }

   @Generated
   public boolean isShowLeftItem() {
      return this.showLeftItem;
   }

   @Generated
   public boolean isShowArmor() {
      return this.showArmor;
   }

   @Generated
   public FirstPersonConfiguration setShowRightArm(boolean showRightArm) {
      this.showRightArm = showRightArm;
      return this;
   }

   @Generated
   public FirstPersonConfiguration setShowLeftArm(boolean showLeftArm) {
      this.showLeftArm = showLeftArm;
      return this;
   }

   @Generated
   public FirstPersonConfiguration setShowRightItem(boolean showRightItem) {
      this.showRightItem = showRightItem;
      return this;
   }

   @Generated
   public FirstPersonConfiguration setShowLeftItem(boolean showLeftItem) {
      this.showLeftItem = showLeftItem;
      return this;
   }

   @Generated
   public FirstPersonConfiguration setShowArmor(boolean showArmor) {
      this.showArmor = showArmor;
      return this;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof FirstPersonConfiguration other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isShowRightArm() != other.isShowRightArm()) {
         return false;
      } else if (this.isShowLeftArm() != other.isShowLeftArm()) {
         return false;
      } else if (this.isShowRightItem() != other.isShowRightItem()) {
         return false;
      } else {
         return this.isShowLeftItem() != other.isShowLeftItem() ? false : this.isShowArmor() == other.isShowArmor();
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof FirstPersonConfiguration;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isShowRightArm() ? 79 : 97);
      result = result * 59 + (this.isShowLeftArm() ? 79 : 97);
      result = result * 59 + (this.isShowRightItem() ? 79 : 97);
      result = result * 59 + (this.isShowLeftItem() ? 79 : 97);
      return result * 59 + (this.isShowArmor() ? 79 : 97);
   }

   @Generated
   @Override
   public String toString() {
      return "FirstPersonConfiguration(showRightArm="
         + this.isShowRightArm()
         + ", showLeftArm="
         + this.isShowLeftArm()
         + ", showRightItem="
         + this.isShowRightItem()
         + ", showLeftItem="
         + this.isShowLeftItem()
         + ", showArmor="
         + this.isShowArmor()
         + ")";
   }

   @Generated
   public FirstPersonConfiguration() {
   }

   @Generated
   public FirstPersonConfiguration(boolean showRightArm, boolean showLeftArm, boolean showRightItem, boolean showLeftItem, boolean showArmor) {
      this.showRightArm = showRightArm;
      this.showLeftArm = showLeftArm;
      this.showRightItem = showRightItem;
      this.showLeftItem = showLeftItem;
      this.showArmor = showArmor;
   }
}
