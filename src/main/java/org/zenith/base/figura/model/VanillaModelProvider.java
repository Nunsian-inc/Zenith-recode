package org.zenith.base.figura.model;

import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import org.zenith.base.figura.ducks.PlayerModelCapeAccessor;
import org.zenith.base.figura.mixin.render.layers.elytra.ElytraModelAccessor;

public enum VanillaModelProvider {
   HEAD(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.head;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   BODY(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.body;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   LEFT_ARM(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.leftArm;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   RIGHT_ARM(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.rightArm;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   LEFT_LEG(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.leftLeg;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   RIGHT_LEG(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.rightLeg;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   HAT(model -> {
      ModelPart var10000;
      if (model instanceof HumanoidModel<?> m) {
         var10000 = m.hat;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   JACKET(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModel m) {
         var10000 = m.jacket;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   LEFT_SLEEVE(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModel m) {
         var10000 = m.leftSleeve;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   RIGHT_SLEEVE(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModel m) {
         var10000 = m.rightSleeve;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   LEFT_PANTS(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModel m) {
         var10000 = m.leftPants;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   RIGHT_PANTS(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModel m) {
         var10000 = m.rightPants;
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   CAPE(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModelCapeAccessor m) {
         var10000 = m.figura$getCloak();
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   FAKE_CAPE(model -> {
      ModelPart var10000;
      if (model instanceof PlayerModelCapeAccessor m) {
         var10000 = m.figura$getFakeCloak();
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   LEFT_ELYTRON(model -> {
      ModelPart var10000;
      if (model instanceof ElytraModelAccessor m) {
         var10000 = m.getLeftWing();
      } else {
         var10000 = null;
      }

      return var10000;
   }),
   RIGHT_ELYTRON(model -> {
      ModelPart var10000;
      if (model instanceof ElytraModelAccessor m) {
         var10000 = m.getRightWing();
      } else {
         var10000 = null;
      }

      return var10000;
   });

   public final Function<EntityModel<?>, ModelPart> func;

   VanillaModelProvider(Function<EntityModel<?>, ModelPart> func) {
      this.func = func;
   }
}
