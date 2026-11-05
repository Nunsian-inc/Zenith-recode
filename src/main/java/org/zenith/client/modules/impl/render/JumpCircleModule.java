package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.ImmediateRenderer;

@ModuleInfo(
   name = "JumpCircle",
   category = ModuleCategory.RENDER,
   description = "module.jumpcircle.description"
)
public class JumpCircleModule extends Module {
   private static final int CONST_intValue = 8;
   private static final String[][] texts = new String[][]{
      {"module.jumpCircle.texture.storm", "shtorm1", "shtorm2", "glow"},
      {"module.jumpCircle.texture.pool", "glow", "omut2", "omut3"},
      {"module.jumpCircle.texture.rings", "colso1", "omut2", "colso3"},
      {"module.jumpCircle.texture.explosion", "boom1", "boom2", "glow"}
   };
   private static final Identifier[][] identifiers = method4583();
   public static final JumpCircleModule INSTANCE = new JumpCircleModule();
   private final ModeSetting texture = new ModeSetting("module.jumpCircle.texture", "module.jumpCircle.texture.desc", method4582());
   private final SliderSetting size = new SliderSetting("module.jumpCircle.size", 1.4F, 0.5F, 3.0F, 0.1F, "module.jumpCircle.size.desc", "x");
   private final SliderSetting speed = new SliderSetting("module.jumpCircle.speed", 4.8F, 0.0F, 8.0F, 0.1F, "module.jumpCircle.speed.desc", "x");
   private final SliderSetting lifetime = new SliderSetting(
      "module.jumpCircle.lifetime", 650.0F, 250.0F, 1500.0F, 50.0F, "module.jumpCircle.lifetime.desc", "ms"
   );
   private final List<JumpCircleModule.Entry> items = new ArrayList<>();
   private boolean lastOnGround;
   private Vec3 vec3d;

   private JumpCircleModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.lastOnGround = mc.player != null && mc.player.onGround();
      this.vec3d = mc.player == null ? null : mc.player.position();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.vec3d = null;
   }

   @EventTarget
   public void method4575(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         boolean lastOnGround = mc.player.onGround();
         if (lastOnGround) {
            this.vec3d = mc.player.position();
         }

         if (this.lastOnGround && !lastOnGround && mc.player.getDeltaMovement().y > 0.05) {
            this.items.add(new JumpCircleModule.Entry(this.vec3d == null ? mc.player.position() : this.vec3d, System.currentTimeMillis()));
            int n = this.items.size() - 8;
            if (n > 0) {
               this.items.subList(0, n).clear();
            }
         }

         this.lastOnGround = lastOnGround;
      }
   }

   @EventTarget
   public void method4576(WorldRenderEvent event) {
      if (mc.player != null && mc.level != null && !this.items.isEmpty()) {
         long now = System.currentTimeMillis();
         float f = this.lifetime.getValue3();

         for (int i = this.items.size() - 1; i >= 0; i--) {
            if ((float)(now - this.items.get(i).longValue) > f) {
               this.items.remove(i);
            }
         }

         if (!this.items.isEmpty()) {
            this.method4577(event.getMatrixStack(), now, f);
         }
      }
   }

   private void method4577(PoseStack matrixStack, long now, float f) {
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
      RenderState.method1898("core/position_tex_color");
      RenderState.method1878();
      RenderState.method1882(false);
      int index = this.texture.getIndex();
      if (index < 0 || index >= identifiers.length) {
         index = 0;
      }

      Identifier[] identifier = identifiers[index];
      Camera camera = mc.getEntityRenderDispatcher().camera;
      Vec3 position = camera.position();
      Matrix4f pose = matrixStack.last().pose();
      float f2 = this.size.getValue3();
      float f3 = this.speed.getValue3();
      int color = zenith.getStyleManager().getClientColor(0).method7287();
      int n = zenith.getStyleManager().getClientColor(180).method7287();

      for (JumpCircleModule.Entry entry : this.items) {
         this.method4578(entry, now, position, f3, f, f2, color, n);
      }

      for (Identifier id : identifier) {
         RenderState.method1890(0, id);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for (JumpCircleModule.Entry entry : this.items) {
            this.method4579(pose, begin, entry);
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
      }

      RenderState.method1882(true);
      RenderState.method1876();
      RenderState.method1872();
      RenderState.method1873();
   }

   private void method4578(JumpCircleModule.Entry entry, long now, Vec3 position, float f, float f2, float f3, int color, int n) {
      long l = now - entry.longValue;
      entry.floatValue = (float)(entry.vec3d.x - position.x);
      entry.floatValue2 = (float)(entry.vec3d.y - position.y) + 0.04F;
      entry.floatValue3 = (float)(entry.vec3d.z - position.z);
      float f4 = (float)l * 0.001F * (float) (Math.PI * 2) * f;
      entry.floatValue4 = Mth.cos(f4);
      entry.floatValue5 = Mth.sin(f4);
      float alpha1 = Mth.clamp((float)l / f2, 0.0F, 1.0F);
      float f5 = 1.0F - alpha1;
      float alpha = f5 * f5;
      float f6 = Mth.lerp(alpha1, 0.15F, f3);
      entry.floatValue6 = f6 * 1.22F;
      entry.floatValue7 = f6;
      entry.intValue = ColorUtils.method6439(color, alpha * 0.45F);
      entry.intValue2 = ColorUtils.method6439(n, alpha);
   }

   private void method4579(Matrix4f pose, BufferBuilder begin, JumpCircleModule.Entry entry) {
      this.method4580(pose, begin, entry.floatValue, entry.floatValue2, entry.floatValue3, entry.floatValue6, entry.floatValue4, entry.floatValue5, entry.intValue);
      this.method4580(
         pose, begin, entry.floatValue, entry.floatValue2 + 0.002F, entry.floatValue3, entry.floatValue7, entry.floatValue4, entry.floatValue5, entry.intValue2
      );
   }

   private void method4580(Matrix4f pose, BufferBuilder begin, float floatValue, float y, float f, float f2, float f3, float f4, int intValue) {
      float f5 = f2 * f3;
      float f6 = f2 * f4;
      this.method4581(pose, begin, floatValue - f5 + f6, y, f - f6 - f5, 0.0F, 0.0F, intValue);
      this.method4581(pose, begin, floatValue - f5 - f6, y, f - f6 + f5, 0.0F, 1.0F, intValue);
      this.method4581(pose, begin, floatValue + f5 - f6, y, f + f6 + f5, 1.0F, 1.0F, intValue);
      this.method4581(pose, begin, floatValue + f5 + f6, y, f + f6 - f5, 1.0F, 0.0F, intValue);
   }

   private void method4581(Matrix4f pose, BufferBuilder begin, float x, float y, float z, float f, float f2, int intValue) {
      begin.addVertex(pose, x, y, z).setUv(f, f2).setColor(intValue);
   }

   private static String[] method4582() {
      String[] strings = new String[texts.length];

      for (int i = 0; i < texts.length; i++) {
         strings[i] = texts[i][0];
      }

      return strings;
   }

   private static Identifier[][] method4583() {
      Identifier[][] idses = new Identifier[texts.length][];

      for (int i = 0; i < texts.length; i++) {
         String[] text = texts[i];
         int n = text.length == 1 ? 0 : 1;
         idses[i] = new Identifier[text.length - n];

         for (int j = n; j < text.length; j++) {
            idses[i][j - n] = Zenith.method1501("visuals/jumpcircle/" + text[j] + ".png");
         }
      }

      return idses;
   }

   private static class Entry {
      private final Vec3 vec3d;
      private final long longValue;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private int intValue;
      private int intValue2;

      private Entry(Vec3 vec3d, long longValue) {
         this.vec3d = vec3d;
         this.longValue = longValue;
      }
   }
}
