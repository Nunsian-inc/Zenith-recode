package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;

public final class CrystalAnimation implements MinecraftAccess {
   private static final int CONST_intValue = -1;
   private static final int CONST_intValue2 = 6;
   private static final double CONST_doubleValue = 9216.0;
   private static final float CONST_floatValue = 2.0F;
   private static final float CONST_floatValue2 = 6.2F;
   private static volatile boolean flag;
   private static final List<BlockPos> items = new ArrayList<>();
   private static final Map<Class15, int[]> items2 = new IdentityHashMap<>();
   private int intValue;

   public CrystalAnimation() {
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)minecraft -> this.tick());
   }

   public static boolean method4460(BlockState blockState) {
      return flag && blockState.is(Blocks.RESPAWN_ANCHOR);
   }

   private static HoveredData method4461(FocusSlotEnum key) {
      Wardrobe wardrobe = Wardrobe.method2488();
      return wardrobe != null && wardrobe.showSelf.isEnabled() ? wardrobe.method2491(key) : null;
   }

   private static Class15 method4462(HoveredData hoveredData) {
      Wardrobe wardrobe = Wardrobe.method2488();
      return hoveredData != null && wardrobe != null ? wardrobe.getRegistry().method1427(hoveredData) : null;
   }

   public static boolean method4463(EndCrystalRenderState endCrystalRenderState, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue) {
      HoveredData hoveredData = method4461(FocusSlotEnum.CRYSTAL);
      Class15 key = method4462(hoveredData);
      if (key == null) {
         return false;
      } else {
         float floatValue = endCrystalRenderState.ageInTicks / 20.0F;
         int id = (int)(Double.doubleToLongBits(endCrystalRenderState.x * 31.0 + endCrystalRenderState.z) ^ Double.doubleToLongBits(endCrystalRenderState.y));
         AnimationCache.Entry entry = AnimationCache.method7621(id, hoveredData, false);
         entry.animationState.method6539(key, "idle", floatValue);
         float[] floatValues = entry.class16.method2139(key, entry.animationState, floatValue, null);
         poseStack.pushPose();

         try {
            poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.0F, 0.775F, 0.0F);
            poseStack.rotate(Axis.XP.rotationDegrees(180.0F));
            poseStack.scale(2.0F, 2.0F, 2.0F);
            BoneMatrices.method4787(key, floatValues, 6, poseStack, orderedRenderCommandQueue, endCrystalRenderState.lightCoords, OverlayTexture.NO_OVERLAY, true, hoveredData.getRarity().isFlag(), -1);
         } finally {
            poseStack.popPose();
         }

         return true;
      }
   }

   private void tick() {
      boolean flag2 = mc.level != null && mc.player != null && method4462(method4461(FocusSlotEnum.ANCHOR)) != null;
      if (!flag2) {
         if (flag) {
            flag = false;
            if (mc.level != null && mc.player != null) {
               method4464(Math.max(6, mc.options.getEffectiveRenderDistance() + 1));
            }

            rebuild();
         }

         items.clear();
      } else if (!flag) {
         flag = true;
         method4464(6);
         rebuild();
         this.intValue = 0;
      } else {
         if (++this.intValue >= 5) {
            this.intValue = 0;
            method4464(6);
         }
      }
   }

   private static void rebuild() {
      if (mc.levelExtractor != null && mc.level != null) {
         for (BlockPos pos : items) {
            mc.levelExtractor.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
         }
      }
   }

   private static void method4464(int n) {
      items.clear();
      if (mc.level != null && mc.player != null) {
         ChunkPos chunkPosition = mc.player.chunkPosition();

         for (int i = chunkPosition.x() - n; i <= chunkPosition.x() + n; i++) {
            for (int j = chunkPosition.z() - n; j <= chunkPosition.z() + n; j++) {
               LevelChunk chunk = mc.level.getChunkSource().getChunk(i, j, ChunkStatus.FULL, false);
               if (chunk != null) {
                  LevelChunkSection[] sections = chunk.getSections();

                  for (int k = 0; k < sections.length; k++) {
                     LevelChunkSection levelChunkSection = sections[k];
                     if (levelChunkSection != null && !levelChunkSection.hasOnlyAir() && levelChunkSection.maybeHas(var0x -> var0x.is(Blocks.RESPAWN_ANCHOR))) {
                        int n2 = chunk.getSectionYFromSectionIndex(k) << 4;

                        for (int m = 0; m < 16; m++) {
                           for (int sectionZ = 0; sectionZ < 16; sectionZ++) {
                              for (int sectionX = 0; sectionX < 16; sectionX++) {
                                 if (levelChunkSection.getBlockState(sectionX, m, sectionZ).is(Blocks.RESPAWN_ANCHOR)) {
                                    items.add(new BlockPos((i << 4) + sectionX, n2 + m, (j << 4) + sectionZ));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int[] method4465(Class15 key) {
      return items2.computeIfAbsent(key, class2 -> {
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < key.texts.length; i++) {
            if (key.texts[i].startsWith("charge")) {
               arrayList.add(i);
            }
         }

         return arrayList.stream().mapToInt(x -> (Integer)x).toArray();
      });
   }

   @EventTarget
   public void method4466(WorldRenderEvent event) {
      if (flag && !items.isEmpty() && mc.level != null) {
         HoveredData hoveredData = method4461(FocusSlotEnum.ANCHOR);
         Class15 key = method4462(hoveredData);
         if (key != null) {
            Vec3 position = mc.gameRenderer.mainCamera().position();
            float floatValue = ((float)mc.level.getGameTime() + event.getFloatValue()) / 20.0F;
            int[] ints = method4465(key);
            Pose pose = event.getMatrixStack().last();
            Matrix3f matrix = new Matrix3f(pose.pose());
            // 26.3: no immediate buffer source, submit all anchors into one collector and draw it right away
            OutlineVertexProvider.method1542(orderedRenderCommandQueue -> {
            for (BlockPos pos : items) {
               double xo = pos.getX() + 0.5 - position.x;
               double yo = pos.getY() + 0.5 - position.y;
               double zo = pos.getZ() + 0.5 - position.z;
               if (!(xo * xo + yo * yo + zo * zo > 9216.0)) {
                  BlockState blockState = mc.level.getBlockState(pos);
                  if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
                     AnimationCache.Entry entry = AnimationCache.method7621(pos.hashCode(), hoveredData, false);
                     entry.animationState.method6539(key, "idle", floatValue);
                     float[] floatValues = entry.class16.method2139(key, entry.animationState, floatValue, null);
                     int n = blockState.getValue(RespawnAnchorBlock.CHARGE);

                     for (int i = n; i < ints.length; i++) {
                        int n2 = ints[i] * 9;
                        floatValues[n2 + 6] = 0.0F;
                        floatValues[n2 + 7] = 0.0F;
                        floatValues[n2 + 8] = 0.0F;
                     }

                     int lightCoords = net.minecraft.util.LightCoordsUtil.getLightCoords(mc.level, pos.above());
                     PoseStack poseStack = new PoseStack();
                     poseStack.last().pose().set(pose.pose());
                     poseStack.last().normal().set(matrix);
                     poseStack.translate(xo, yo, zo);
                     poseStack.rotate(Axis.XP.rotationDegrees(180.0F));
                     BoneMatrices.method4787(key, floatValues, 6, poseStack, orderedRenderCommandQueue, lightCoords, OverlayTexture.NO_OVERLAY, true, hoveredData.getRarity().isFlag(), -1);
                  }
               }
            }
            });
         }
      }
   }
}
