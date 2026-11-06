package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.client.input.CharacterEvent;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.MouseButton;

public abstract class Widget implements MinecraftAccess {
   public float floatValue;
   public float floatValue2;
   public float width;
   public float height;

   public Widget(float floatValue, float f, float width, float height) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.width = width;
      this.height = height;
   }

   public Widget() {
      this(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void method8081(MenuDrawContext menuDrawContext) {
      this.method8084(menuDrawContext);
      this.method8082(menuDrawContext);
   }

   public abstract void method8082(MenuDrawContext menuDrawContext);

   public void method8083() {
   }

   public void method8084(MenuDrawContext menuDrawContext) {
   }

   public void onMouseClicked(double d, double d2, MouseButton enum2) {
   }

   public void onMouseReleased(double d, double d2, MouseButton enum2) {
   }

   public void method8085(int n, int n2, int n3) {
   }

   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      return false;
   }

   public void method8086(double d, double d2, double d3, double d4) {
   }

   public void method8087(float floatValue, float f) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
   }

   public void method8088(float floatValue, float f, float width, float height) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.width = width;
      this.height = height;
   }

   public boolean method8089(float mouseX, float mouseY) {
      return Vec2Utils.method8067(this.floatValue, this.floatValue2, this.width, this.height, mouseX, mouseY);
   }

   public boolean method8090(double mouseX, double mouseY) {
      return Vec2Utils.method8067(this.floatValue, this.floatValue2, this.width, this.height, mouseX, mouseY);
   }

   public boolean method8091(MenuDrawContext menuDrawContext) {
      return this.method8089(menuDrawContext.getMouseX(), menuDrawContext.getMouseY());
   }

   @Generated
   public float getX() {
      return this.floatValue;
   }

   @Generated
   public float getY() {
      return this.floatValue2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public void setX(float floatValue) {
      this.floatValue = floatValue;
   }

   @Generated
   public void setY(float y) {
      this.floatValue2 = y;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }
}
