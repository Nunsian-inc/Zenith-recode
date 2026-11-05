package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.client.model.geom.builders.UVPair;
import org.zenith.MinecraftAccess;

public final class Vec2Utils implements MinecraftAccess {
   public static boolean method8065(double d, double d2, double d3, double d4, int mouseX, int mouseY) {
      return mouseX >= d && mouseX < d + d3 && mouseY >= d2 && mouseY < d2 + d4;
   }

   public static boolean method8066(double d, double d2, double d3, double d4, MenuDrawContext menuDrawContext) {
      return method8065(d, d2, d3, d4, menuDrawContext.getMouseX(), menuDrawContext.getMouseY());
   }

   public static boolean method8067(double floatValue, double d, double width, double height, double mouseX, double mouseY) {
      return mouseX >= floatValue && mouseX < floatValue + width && mouseY >= d && mouseY < d + height;
   }

   public static UVPair method8068(double guiScale) {
      return new UVPair((float)(mc.mouseHandler.xpos() / guiScale), (float)(mc.mouseHandler.ypos() / guiScale));
   }

   @Generated
   private Vec2Utils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
