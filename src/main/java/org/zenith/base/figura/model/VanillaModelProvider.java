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
      ModelPart head;
      if (model instanceof HumanoidModel<?> m) {
         head = m.head;
      } else {
         head = null;
      }

      return head;
   }),
   BODY(model -> {
      ModelPart body;
      if (model instanceof HumanoidModel<?> m) {
         body = m.body;
      } else {
         body = null;
      }

      return body;
   }),
   LEFT_ARM(model -> {
      ModelPart leftArm;
      if (model instanceof HumanoidModel<?> m) {
         leftArm = m.leftArm;
      } else {
         leftArm = null;
      }

      return leftArm;
   }),
   RIGHT_ARM(model -> {
      ModelPart rightArm;
      if (model instanceof HumanoidModel<?> m) {
         rightArm = m.rightArm;
      } else {
         rightArm = null;
      }

      return rightArm;
   }),
   LEFT_LEG(model -> {
      ModelPart leftLeg;
      if (model instanceof HumanoidModel<?> m) {
         leftLeg = m.leftLeg;
      } else {
         leftLeg = null;
      }

      return leftLeg;
   }),
   RIGHT_LEG(model -> {
      ModelPart rightLeg;
      if (model instanceof HumanoidModel<?> m) {
         rightLeg = m.rightLeg;
      } else {
         rightLeg = null;
      }

      return rightLeg;
   }),
   HAT(model -> {
      ModelPart hat;
      if (model instanceof HumanoidModel<?> m) {
         hat = m.hat;
      } else {
         hat = null;
      }

      return hat;
   }),
   JACKET(model -> {
      ModelPart jacket;
      if (model instanceof PlayerModel m) {
         jacket = m.jacket;
      } else {
         jacket = null;
      }

      return jacket;
   }),
   LEFT_SLEEVE(model -> {
      ModelPart leftSleeve;
      if (model instanceof PlayerModel m) {
         leftSleeve = m.leftSleeve;
      } else {
         leftSleeve = null;
      }

      return leftSleeve;
   }),
   RIGHT_SLEEVE(model -> {
      ModelPart rightSleeve;
      if (model instanceof PlayerModel m) {
         rightSleeve = m.rightSleeve;
      } else {
         rightSleeve = null;
      }

      return rightSleeve;
   }),
   LEFT_PANTS(model -> {
      ModelPart leftPants;
      if (model instanceof PlayerModel m) {
         leftPants = m.leftPants;
      } else {
         leftPants = null;
      }

      return leftPants;
   }),
   RIGHT_PANTS(model -> {
      ModelPart rightPants;
      if (model instanceof PlayerModel m) {
         rightPants = m.rightPants;
      } else {
         rightPants = null;
      }

      return rightPants;
   }),
   CAPE(model -> {
      ModelPart figura$getCloak;
      if (model instanceof PlayerModelCapeAccessor m) {
         figura$getCloak = m.figura$getCloak();
      } else {
         figura$getCloak = null;
      }

      return figura$getCloak;
   }),
   FAKE_CAPE(model -> {
      ModelPart figura$getFakeCloak;
      if (model instanceof PlayerModelCapeAccessor m) {
         figura$getFakeCloak = m.figura$getFakeCloak();
      } else {
         figura$getFakeCloak = null;
      }

      return figura$getFakeCloak;
   }),
   LEFT_ELYTRON(model -> {
      ModelPart leftWing;
      if (model instanceof ElytraModelAccessor m) {
         leftWing = m.getLeftWing();
      } else {
         leftWing = null;
      }

      return leftWing;
   }),
   RIGHT_ELYTRON(model -> {
      ModelPart rightWing;
      if (model instanceof ElytraModelAccessor m) {
         rightWing = m.getRightWing();
      } else {
         rightWing = null;
      }

      return rightWing;
   });

   public final Function<EntityModel<?>, ModelPart> func;

   VanillaModelProvider(Function<EntityModel<?>, ModelPart> func) {
      this.func = func;
   }
}
