package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import org.zenith.MinecraftAccess;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class2;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class WardrobeFeatureRenderer extends RenderLayer<AvatarRenderState, PlayerModel> implements MinecraftAccess {
   private static final int CONST_intValue = -1;
   private static final Map<String, String> items = new HashMap<>();
   private static final Map<String, Object> items2 = new HashMap<>();
   private static final Object obj = new Object();

   public WardrobeFeatureRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
      super(renderer);
   }

   public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && !state.isInvisible) {
         List<HoveredData> hoveredDatas = method4444(state);
         if (!hoveredDatas.isEmpty()) {
            PlayerModel parentModel = this.getParentModel();
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            HoveredData hoveredData = wardrobe.method2499(hoveredDatas, FocusSlotEnum.CHARACTER);
            WardrobeSetInfo wardrobeSetInfo = hoveredData == null ? wardrobe.method2500(hoveredDatas) : null;
            Util3.Entry entry = null;
            long nanoTime = System.nanoTime();

            for (HoveredData hoveredData2 : hoveredDatas) {
               if (hoveredData2.method1546() != WardrobeCategory.PET
                  && !hoveredData2.getFocusSlotEnum().method543()
                  && (hoveredData == null || hoveredData2 == hoveredData || hoveredData2.method1546() == WardrobeCategory.ACCESSORY)) {
                  Class15 key = wardrobe.getRegistry().method1427(hoveredData2);
                  if (key != null) {
                     AnimationCache.Entry entry2 = AnimationCache.method7621(state.id, hoveredData2, wardrobe.isFlag());
                     float f = entry2.method7624(nanoTime);
                     float[] floats = method4441(key, entry2, state, parentModel, f);
                     if (hoveredData2 == hoveredData && !entry2.animationState.isFlag()) {
                        entry2.class16.method2140(key);
                     }

                     boolean flag = state.distanceToCameraSq < key.floatValue * key.floatValue;
                     boolean flag2 = hoveredData2.getRarity().isFlag();
                     float[] floats2 = null;

                     for (int i = 0; i < 6; i++) {
                        if (key.method2033(i)) {
                           if (floats2 == null && floats != null) {
                              floats2 = (float[])floats.clone();
                           }

                           ModelPart modelPart = Util19.method5047(parentModel, i);
                           poseStack.pushPose();

                           try {
                              parentModel.root().translateAndRotate(poseStack);
                              modelPart.translateAndRotate(poseStack);
                              if (hoveredData2 != hoveredData) {
                                 if (entry == null) {
                                    entry = Util3.method826(state, wardrobe, hoveredDatas, hoveredData);
                                 }

                                 entry.method846(poseStack, hoveredData2, i);
                              }

                              BoneMatrices.method4788(key, floats2, i, poseStack, submitNodeCollector, lightCoords, overlayCoords, flag, flag2, -1);
                           } finally {
                              poseStack.popPose();
                           }
                        }
                     }

                     Util19.method5044(key, entry2, state, parentModel, floats, f, entry2.class2.flag3, entry2.class2.flag2, wardrobeSetInfo, key.items3);
                  }
               }
            }

            if (wardrobeSetInfo != null) {
               AnimationCache.Entry entry2 = AnimationCache.method7622(state.id, method4439(wardrobeSetInfo), wardrobe.isFlag());
               Util19.method5045(wardrobeSetInfo, state, entry2, entry2.method7624(nanoTime), method4440(wardrobeSetInfo));
            }
         }
      }
   }

   private static String method4439(WardrobeSetInfo wardrobeSetInfo) {
      return items.computeIfAbsent(wardrobeSetInfo.getText(), var0x -> "set:" + var0x);
   }

   private static ParticleOptions method4440(WardrobeSetInfo wardrobeSetInfo) {
      String key = wardrobeSetInfo.getText2();
      if (key == null) {
         return null;
      } else {
         Object obj2 = items2.computeIfAbsent(key, var0x -> {
            ParticleOptions var1x = ModelJsonParser.method120(var0x);
            return var1x == null ? obj : var1x;
         });
         return obj2 == obj ? null : (ParticleOptions)obj2;
      }
   }

   private static float[] method4441(Class15 key, AnimationCache.Entry entry, AvatarRenderState state, PlayerModel parentModel, float f) {
      Class2 class2 = entry.class2;
      class2.floatValue = state.capeFlap;
      class2.floatValue2 = state.capeLean;
      class2.floatValue3 = state.capeLean2;
      class2.floatValue4 = parentModel.leftLeg.xRot;
      class2.floatValue5 = parentModel.rightLeg.xRot;
      class2.floatValue6 = state.walkAnimationSpeed;
      class2.floatValue8 = f;
      class2.flag = state.isCrouching;
      class2.flag3 = state.walkAnimationSpeed > 0.15F;
      class2.flag2 = method4442(state);
      float bodyRot = state.bodyRot;
      if (!Float.isNaN(entry.floatValue2) && !(f <= 0.0F)) {
         float f2 = bodyRot - entry.floatValue2;

         while (f2 > 180.0F) {
            f2 -= 360.0F;
         }

         while (f2 < -180.0F) {
            f2 += 360.0F;
         }

         class2.floatValue7 = f2 / f;
      } else {
         class2.floatValue7 = 0.0F;
      }

      entry.floatValue2 = bodyRot;
      float floatValue = state.ageInTicks / 20.0F;
      String text = state.isFallFlying
         ? "glide"
         : (class2.flag2 ? "fly" : (state.isCrouching ? "sneak" : (state.walkAnimationSpeed > 0.6F ? "run" : (class2.flag3 ? "walk" : "idle"))));
      entry.animationState.method6539(key, text, floatValue);
      entry.animationState.setFloatValue6(!text.equals("walk") && !text.equals("run") ? 1.0F : 0.6F + Math.min(1.2F, state.walkAnimationSpeed));
      return entry.class16.method2139(key, entry.animationState, floatValue, class2);
   }

   private static boolean method4442(AvatarRenderState state) {
      if (state.isFallFlying) {
         return true;
      } else if (mc.level == null) {
         return false;
      } else {
         Entity entity = mc.level.getEntity(state.id);
         return entity != null && !entity.onGround() && !entity.isPassenger() && !entity.isInWater();
      }
   }

   public static boolean method4443(AvatarRenderState state) {
      return mc.player != null && state.id == mc.player.getId();
   }

   public static List<HoveredData> method4444(AvatarRenderState state) {
      List zenith$wardrobeLoadout = ((Handler4)state).zenith$wardrobeLoadout();
      if (zenith$wardrobeLoadout != null) {
         return zenith$wardrobeLoadout;
      } else {
         Wardrobe wardrobe = Wardrobe.method2488();
         if (wardrobe == null) {
            return List.of();
         } else {
            boolean flag = method4443(state);
            return (flag ? wardrobe.showSelf.isEnabled() : wardrobe.showOthers.isEnabled()) ? List.copyOf(wardrobe.method2498(Handler4.method3142(state), flag)) : List.of();
         }
      }
   }

   public static HoveredData method4445(AvatarRenderState state) {
      Wardrobe wardrobe = Wardrobe.method2488();
      return wardrobe == null ? null : wardrobe.method2499(method4444(state), FocusSlotEnum.CHARACTER);
   }

   public static void method4446(AvatarRenderState state) {
      ((Handler4)state).zenith$setWardrobeLoadout(null);
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null) {
         List<HoveredData> hoveredDatas = method4444(state);
         ((Handler4)state).zenith$setWardrobeLoadout(hoveredDatas);
         if (!hoveredDatas.isEmpty()) {
            if (wardrobe.method2499(hoveredDatas, FocusSlotEnum.CHARACTER) != null) {
               state.showCape = false;
            } else {
               for (HoveredData hoveredData : hoveredDatas) {
                  if (hoveredData.method1546() == WardrobeCategory.CLOTHING
                     || hoveredData.method1546() == WardrobeCategory.CAPE
                     || hoveredData.method1546() == WardrobeCategory.ACCESSORY) {
                     Class15 class2 = wardrobe.getRegistry().method1427(hoveredData);
                     if (class2 != null) {
                        int n = class2.intValue4;
                        if ((n & 1) != 0) {
                           state.showHat = false;
                        }

                        if ((n & 2) != 0) {
                           state.showJacket = false;
                        }

                        if ((n & 4) != 0) {
                           state.showLeftSleeve = false;
                           state.showRightSleeve = false;
                        }

                        if ((n & 8) != 0) {
                           state.showLeftPants = false;
                           state.showRightPants = false;
                        }

                        if ((n & 16) != 0 || hoveredData.getFocusSlotEnum() == FocusSlotEnum.BACK || hoveredData.getFocusSlotEnum() == FocusSlotEnum.CAPE) {
                           state.showCape = false;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
