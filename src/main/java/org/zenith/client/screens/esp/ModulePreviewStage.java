package org.zenith.client.screens.esp;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.render.AmbientParticlesModule;
import org.zenith.client.modules.impl.render.BlockESPModule;
import org.zenith.client.modules.impl.render.ChamsModule;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.client.modules.impl.render.FireWorkESPModule;
import org.zenith.client.modules.impl.render.HitParticlesModule;
import org.zenith.client.modules.impl.render.TargetESPModule;
import org.zenith.client.modules.impl.render.TotemParticlesModule;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.render.EspScreen;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class ModulePreviewStage implements MinecraftAccess {
   private static final float PX_PER_BLOCK = 96.0F;
   private static final float ARRIVAL_SPIN = 28.0F;
   /**
    * Отрицательный ID: сервер такие не выдаёт, поэтому с реальными сущностями не пересечётся.
    * Без ID {@link net.minecraft.world.entity.Entity#getId()} в 26.x бросает исключение
    * (его вызывает, например, рендер-миксин figura), и превью не рисуется.
    */
   private static final int PREVIEW_ENTITY_ID = -1_000_000;
   private ModulePreviewStage.Kind kind = ModulePreviewStage.Kind.MODEL;
   private Module owner;
   private boolean open;
   private RemotePlayer dummy;
   private ClientLevel dummyWorld;
   private float modelYaw;
   private float modelPitch;
   private boolean draggingModel;
   private float arrival = 1.0F;
   private BoundsData bounds;

   public boolean isOpen() {
      return this.open;
   }

   public ModulePreviewStage.Kind kind() {
      return this.kind;
   }

   public boolean isLive() {
      return this.owner != null && this.owner.isEnabled();
   }

   public void show(ModulePreviewStage.Kind kind, Module owner) {
      if (!this.open || this.kind != kind) {
         this.arrival = 0.0F;
      }

      this.owner = owner;
      this.kind = kind;
      this.open = true;
      if (kind != ModulePreviewStage.Kind.SHADER) {
         EspScreen.release();
      }

      if (kind != ModulePreviewStage.Kind.FIREWORK) {
         FireWorkESPModule.INSTANCE.method244();
      }
   }

   public void hide() {
      this.open = false;
      this.bounds = null;
      this.draggingModel = false;
      EspScreen.release();
      FireWorkESPModule.INSTANCE.method244();
   }

   public void releaseModel() {
      this.dummy = null;
      this.dummyWorld = null;
   }

   public void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float floatValue, float y, float width, float height, float previewAlpha, float f) {
      if (previewAlpha <= 0.001F) {
         this.bounds = null;
      } else {
         this.bounds = this.open ? new BoundsData(floatValue, y, width, height) : null;
         this.arrival = Mth.lerp(0.12F, this.arrival, 1.0F);
         if (this.arrival > 0.999F) {
            this.arrival = 1.0F;
         }

         boolean live = this.isLive();
         if (this.kind == ModulePreviewStage.Kind.WORLD_WEATHER) {
            if (live) {
               customDrawContext.enableScissor(floatValue, y, floatValue + width, y + height);

               try {
                  WeatherModule.INSTANCE.method3119(customDrawContext, floatValue, y, width, height, previewAlpha);
               } finally {
                  customDrawContext.disableScissor();
               }
            }
         } else if (this.kind != ModulePreviewStage.Kind.FIREWORK && this.kind != ModulePreviewStage.Kind.BLOCK) {
            LivingEntity previewEntity = this.previewEntity();
            if (previewEntity != null) {
               float xo = floatValue + width / 2.0F;
               float yo;
               float yo2 = (yo = y + height / 2.0F + 8.0F) + 86.399994F;
               float f2 = height / 2.0F;
               float angle = viewAngle(-this.modelYaw) + 28.0F * (1.0F - this.arrival);
               float viewAngle2 = viewAngle(-this.modelPitch);
               boolean flag;
               if (flag = this.open && live && this.kind == ModulePreviewStage.Kind.SHADER && f >= 0.995F) {
                  EspScreen.method6551(customDrawContext, () -> this.drawModel(customDrawContext, floatValue, width, yo, f2, angle, viewAngle2, previewEntity));
               }

               customDrawContext.enableScissor(floatValue, y, floatValue + width, y + height);

               try {
                  RenderState.method1883(1.0F, 1.0F, 1.0F, previewAlpha);

                  try {
                     this.drawModel(customDrawContext, floatValue, width, yo, f2, angle, viewAngle2, previewEntity);
                  } finally {
                     RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
                  }

                  if (!live) {
                     return;
                  }

                  if (this.kind == ModulePreviewStage.Kind.ENTITY) {
                     EntityESPModule.INSTANCE.method619(customDrawContext, xo, yo2, 96.0F, y + height - 4.0F, "Player", previewAlpha, mouseX, mouseY);
                  } else if (this.kind == ModulePreviewStage.Kind.PARTICLES_AMBIENT) {
                     AmbientParticlesModule.INSTANCE.method2392(customDrawContext, xo, yo2, 96.0F, previewAlpha);
                  } else if (this.kind == ModulePreviewStage.Kind.PARTICLES_TOTEM) {
                     TotemParticlesModule.INSTANCE.method4699(customDrawContext, xo, yo2, 96.0F, previewAlpha);
                  } else if (this.kind == ModulePreviewStage.Kind.PARTICLES_HIT) {
                     HitParticlesModule.INSTANCE.method7795(customDrawContext, xo, yo2, 96.0F, previewAlpha);
                  } else if (this.kind == ModulePreviewStage.Kind.WORLD_VISUALS) {
                     WorldModule.INSTANCE.method5650(customDrawContext, floatValue, y, width, height, previewAlpha);
                  } else if (this.kind == ModulePreviewStage.Kind.TARGET) {
                     TargetESPModule.INSTANCE.method2160(customDrawContext, xo, yo2, 96.0F, previewAlpha, angle, viewAngle2);
                  }
               } finally {
                  customDrawContext.disableScissor();
               }

               if (flag) {
                  EspScreen.method6552(customDrawContext, floatValue, y, width, height, previewAlpha);
               }

               RenderState.method1881();
            }
         } else if (live) {
            customDrawContext.enableScissor(floatValue, y, floatValue + width, y + height);

            try {
               if (this.kind == ModulePreviewStage.Kind.FIREWORK) {
                  FireWorkESPModule.INSTANCE.method243(customDrawContext, floatValue, y, width, height);
               } else {
                  BlockESPModule.INSTANCE.method7573(customDrawContext, floatValue + width / 2.0F, y + height / 2.0F, 96.0F, y + height - 4.0F);
               }
            } finally {
               customDrawContext.disableScissor();
            }
         }
      }
   }

   private static float viewAngle(float f) {
      return (float)Math.atan(f / 40.0F) * 20.0F;
   }

   private void drawModel(MenuDrawContext customDrawContext, float floatValue, float width, float yo, float f, float angle, float f2, LivingEntity previewEntity) {
      ChamsModule.flag3 = this.kind == ModulePreviewStage.Kind.CHAMS && this.isLive();
      boolean flag;
      if (flag = this.kind == ModulePreviewStage.Kind.WORLD_VISUALS && this.isLive()) {
         WorldModule worldModule = WorldModule.INSTANCE;
         FrameTimer.method753(worldModule.method5651(), 1.0F, 1.0F, 1.0F, worldModule.method5652());
      }

      float yBodyRot = previewEntity.yBodyRot;
      float yRot = previewEntity.getYRot();
      float xRot = previewEntity.getXRot();
      float yHeadRot = previewEntity.yHeadRot;
      float yHeadRotO = previewEntity.yHeadRotO;

      try {
         float yBodyRot2 = 180.0F + angle;
         previewEntity.yBodyRot = yBodyRot2;
         previewEntity.setYRot(yBodyRot2);
         previewEntity.setXRot(0.0F);
         previewEntity.yHeadRot = yBodyRot2;
         previewEntity.yHeadRotO = yBodyRot2;
         Quaternionf rotateX = new Quaternionf().rotateX(f2 * (float) (Math.PI / 180.0));
         Quaternionf by = new Quaternionf().rotateZ((float) Math.PI).mul(rotateX);
         float scale = previewEntity.getScale();
         Vector3f vec = new Vector3f(0.0F, previewEntity.getBbHeight() / 2.0F, 0.0F);
         float xScale = 96.0F / scale;
         PoseStack poseStack = MatrixUtils.method666(customDrawContext.pose());
         poseStack.translate(floatValue + width / 2.0F, yo, 50.0F);
         poseStack.scale(xScale, xScale, -xScale);
         poseStack.translate(vec.x, vec.y, vec.z);
         poseStack.rotate(by);
         ScissorFlusher.flush();
         EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
         EntityRenderState extractEntity = entityRenderDispatcher.extractEntity(previewEntity, 1.0F);
         extractEntity.lightCoords = 240 << 16 | 240;
         extractEntity.shadowPieces.clear();
         extractEntity.outlineColor = 0;
         OutlineVertexProvider.method1540(extractEntity);
         CameraRenderState camera = OutlineVertexProvider.method1541(previewEntity.position());
         camera.orientation = rotateX.conjugate(new Quaternionf()).rotateY((float) Math.PI);
         OutlineVertexProvider.method1542(var4x -> entityRenderDispatcher.submit(extractEntity, camera, 0.0, 0.0, 0.0, poseStack, var4x));
         ScissorFlusher.flush();
      } finally {
         previewEntity.yBodyRot = yBodyRot;
         previewEntity.setYRot(yRot);
         previewEntity.setXRot(xRot);
         previewEntity.yHeadRot = yHeadRot;
         previewEntity.yHeadRotO = yHeadRotO;
         ChamsModule.flag3 = false;
         if (flag) {
            FrameTimer.method754();
         }
      }
   }

   private LivingEntity previewEntity() {
      if (mc.level == null) {
         return null;
      } else {
         if (this.dummy == null || this.dummyWorld != mc.level) {
            this.dummyWorld = mc.level;
            this.dummy = new RemotePlayer(mc.level, new GameProfile(UUID.nameUUIDFromBytes("zenith-esp-preview".getBytes()), "Player"));
            this.dummy.setId(PREVIEW_ENTITY_ID);
         }

         return this.dummy;
      }
   }

   public boolean onMouseClicked(double x, double y, MouseButton enum2) {
      if (!this.open || this.bounds == null || !this.bounds.contains(x, y)) {
         return false;
      } else if (this.kind == ModulePreviewStage.Kind.ENTITY && this.isLive() && EntityESPModule.INSTANCE.method561(x, y, enum2)) {
         return true;
      } else {
         this.draggingModel = true;
         return true;
      }
   }

   public boolean onMouseDragged(double doubleValue, double mouseY, double offsetX, double offsetY) {
      if (!this.open) {
         return false;
      } else if (this.kind == ModulePreviewStage.Kind.ENTITY && EntityESPModule.INSTANCE.method562(doubleValue, mouseY)) {
         return true;
      } else if (!this.draggingModel) {
         return false;
      } else {
         this.modelYaw += (float)offsetX * 1.4F;
         this.modelPitch = Mth.clamp(this.modelPitch + (float)offsetY * 1.4F, -55.0F, 55.0F);
         return true;
      }
   }

   public void onMouseReleased() {
      this.draggingModel = false;
      EntityESPModule.INSTANCE.method563();
   }

   public static enum Kind {
      MODEL,
      CHAMS,
      TARGET,
      SHADER,
      ENTITY,
      FIREWORK,
      BLOCK,
      WORLD_WEATHER,
      WORLD_VISUALS,
      PARTICLES_AMBIENT,
      PARTICLES_TOTEM,
      PARTICLES_HIT;
   }
}
