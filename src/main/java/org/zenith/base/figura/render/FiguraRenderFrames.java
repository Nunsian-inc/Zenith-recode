package org.zenith.base.figura.render;

import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.zenith.base.figura.avatar.Avatar;

public final class FiguraRenderFrames {
   private FiguraRenderFrames() {
   }

   public record Arm(List<Avatar> avatars, ModelPart arm) {
   }

   public static final class Elytra {
      public final List<Avatar> avatars;
      public final ElytraModel model;
      public boolean transformed;
      public boolean restored;

      public Elytra(List<Avatar> avatars, ElytraModel model) {
         this.avatars = avatars;
         this.model = model;
      }
   }

   public record Head(List<Avatar> avatars, ItemStack stack) {
   }

   public static final class Living {
      public final List<Avatar> avatars;
      public final List<Avatar> weapons;
      public final Matrix4f initialPose;
      public boolean processed;
      public boolean vanillaTransformed;

      public Living(List<Avatar> avatars, List<Avatar> weapons, Matrix4f initialPose) {
         this.avatars = avatars;
         this.weapons = weapons;
         this.initialPose = initialPose;
      }
   }
}
