package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.display.base.GlowColor;

@ModuleInfo(
   name = "Arrows",
   description = "module.arrows.description",
   category = ModuleCategory.RENDER
)
public final class ArrowsModule extends Module {
   private final Identifier arrowTexture = Zenith.method1501("textures/arrows.png");
   private final Animation radiusAnimation = new Animation(150L, 12.0F, Easing.INSTANCE13);
   private final SliderSetting radiusSetting = new SliderSetting(
      "module.arrows.radiusSetting", 50.0F, 30.0F, 100.0F, 10.0F, "module.arrows.radiusSetting.desc", "b"
   );
   private final SliderSetting sizeSetting = new SliderSetting("module.arrows.sizeSetting", 16.0F, 8.0F, 20.0F, 1.0F, "module.arrows.sizeSetting.desc", "px");
   public static final ArrowsModule INSTANCE = new ArrowsModule();
   private final BooleanSetting ignoreGol = new BooleanSetting("module.arrows.ignoreGol", "module.arrows.ignoreGol.desc", true);

   private ArrowsModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      PoseStack poseStack = MatrixUtils.method666(event.getCustomDrawContext().pose());
      var players = Objects.requireNonNull(mc.level)
         .players()
         .stream()
         .filter(
            player -> player != mc.player && !PetManager.method4557(player.getId()) && (!this.ignoreGol.isEnabled() || UseItemUtils.method6529(player) != 0.0F)
         )
         .toList();
      float centerX = mc.getWindow().getGuiScaledWidth() / 2.0F;
      float centerY = mc.getWindow().getGuiScaledHeight() / 2.0F;
      float y = centerY
         - this.radiusSetting.getValue3()
         - this.radiusAnimation
            .animateTo(
               mc.gui.screen() instanceof InventoryScreen
                  ? 80.0F
                  : (mc.gui.screen() instanceof ContainerScreen ? 100.0F : (Objects.requireNonNull(mc.player).isSprinting() ? 12.0F : 0.0F))
            );
      float arrowSize = this.sizeSetting.getValue3();
      if (!mc.gui.hud.isHidden() && mc.options.getCameraType().equals(CameraType.FIRST_PERSON) && !players.isEmpty()) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1890(0, this.arrowTexture);
         RenderState.method1898("core/position_tex_color");
         BufferBuilder vertexBuffer = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         players.forEach(
            player -> {
               int arrowColor = zenith.getFriends().method7626(player)
                  ? zenith.getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287()
                  : Zenith.getInstance().getStyleManager().getClientColor(90).method7287();
               float relativeYaw = method3037(player) - Objects.requireNonNull(mc.player).getYRot();
               poseStack.pushPose();
               poseStack.translate(centerX, centerY, 0.0F);
               poseStack.rotate(Axis.ZP.rotationDegrees(relativeYaw));
               poseStack.translate(-centerX, -centerY, 0.0F);
               Matrix4f pose = poseStack.last().pose();
               if (zenith.getFriends().method7626(player)) {
                  vertexBuffer.addVertex(pose, centerX - arrowSize / 2.0F, y + arrowSize, 0.0F).setUv(0.0F, 1.0F).setColor(arrowColor);
                  vertexBuffer.addVertex(pose, centerX + arrowSize / 2.0F, y + arrowSize, 0.0F).setUv(1.0F, 1.0F).setColor(arrowColor);
                  vertexBuffer.addVertex(pose, centerX + arrowSize / 2.0F, y, 0.0F).setUv(1.0F, 0.0F).setColor(arrowColor);
                  vertexBuffer.addVertex(pose, centerX - arrowSize / 2.0F, y, 0.0F).setUv(0.0F, 0.0F).setColor(arrowColor);
               } else {
                  GlowColor clientColor = zenith.getStyleManager().getClientColor();
                  vertexBuffer.addVertex(pose, centerX - arrowSize / 2.0F, y + arrowSize, 0.0F).setUv(0.0F, 1.0F).setColor(clientColor.getZenithColor2().method7287());
                  vertexBuffer.addVertex(pose, centerX + arrowSize / 2.0F, y + arrowSize, 0.0F).setUv(1.0F, 1.0F).setColor(clientColor.getZenithColor4().method7287());
                  vertexBuffer.addVertex(pose, centerX + arrowSize / 2.0F, y, 0.0F).setUv(1.0F, 0.0F).setColor(clientColor.getZenithColor3().method7287());
                  vertexBuffer.addVertex(pose, centerX - arrowSize / 2.0F, y, 0.0F).setUv(0.0F, 0.0F).setColor(clientColor.getZenithColor().method7287());
               }

               poseStack.translate(centerX, centerY, 0.0F);
               poseStack.rotate(Axis.ZP.rotationDegrees(-relativeYaw));
               poseStack.translate(-centerX, -centerY, 0.0F);
               poseStack.popPose();
            }
         );
         ImmediateRenderer.method8015(vertexBuffer.buildOrThrow());
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1873();
         RenderState.method1872();
      }
   }

   public static float method3037(Entity entity) {
      double y = MathUtils.method2658(entity.xo, entity.getX()) - MathUtils.method2658(mc.player.xo, mc.player.getX());
      double x = MathUtils.method2658(entity.zo, entity.getZ()) - MathUtils.method2658(mc.player.zo, mc.player.getZ());
      return (float)(-(Math.atan2(y, x) * (180.0 / Math.PI)));
   }
}
