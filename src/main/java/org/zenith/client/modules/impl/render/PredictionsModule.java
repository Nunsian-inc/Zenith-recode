package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.math.Axis;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;

@ModuleInfo(
   name = "Predictions",
   category = ModuleCategory.RENDER,
   description = "module.predictions.description"
)
public final class PredictionsModule extends Module {
   private final BooleanSetting renderItemEntity = new BooleanSetting("module.predictions.renderItemEntity", "module.predictions.renderItemEntity.desc", true);
   private final ModeSetting mode = new ModeSetting(
      "module.predictions.mode", "module.predictions.mode.desc", "module.predictions.sparks", "module.predictions.fire", "module.predictions.frost"
   );
   private static final int CONST_intValue = 1;
   private static final Identifier[] identifiers = new Identifier[]{Identifier.fromNamespaceAndPath("zenith", "projectile_fire"), Identifier.fromNamespaceAndPath("zenith", "projectile_frost")};
   private static final Identifier identifier = Zenith.method1501("visuals/jumpcircle/glowboost.png");
   private static final double CONST_doubleValue = 1.0E-4;
   private static final float CONST_floatValue = 0.11F;
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final int CONST_intValue2 = 56;
   private static final double CONST_doubleValue2 = 0.017857142857142856;
   private static final double CONST_doubleValue3 = 2.399963;
   private final List<PredictionsModule.Data2> items = new ArrayList<>();
   private final List<List<Vec3>> items2 = new ArrayList<>();
   private List<Entity> items3 = List.of();
   private final Map<Item, PredictionsModule.Data> items4 = new HashMap<>();
   private final Map<Item, Projectile> items5 = new HashMap<>();
   private final Matrix4f matrix4f2 = new Matrix4f();
   private final Matrix4f matrix4f3 = new Matrix4f();
   private final Matrix4f matrix4f4 = new Matrix4f();
   private final List<PredictionsModule.Entry> items6 = new ArrayList<>();
   private final Map<Integer, Long> items7 = new HashMap<>();
   private final Map<Integer, Vec3> items8 = new HashMap<>();
   private long longValue;
   private Map<Integer, Integer> items9 = new HashMap<>();
   private Map<Integer, Integer> items10 = new HashMap<>();
   private static final int CONST_intValue3 = 4;
   private final Map<Integer, Vec3> items11 = new HashMap<>();
   private long longValue2;
   private Level world;
   private final Random random = new Random();
   public static final PredictionsModule INSTANCE = new PredictionsModule();
   // 26.3: built on first use, ItemStacks cannot exist before item components are bound
   private static List<ItemStack> items12;

   public static List<ItemStack> snowball() {
      if (items12 == null) {
         items12 = List.of(Items.SNOWBALL.getDefaultInstance());
      }

      return items12;
   }
   private final List<LivingEntity> items13 = new ArrayList<>();
   private double[] doubleValues = new double[0];
   private double[] doubleValues2 = new double[0];
   private double[] doubleValues3 = new double[0];
   private double[] doubleValues4 = new double[0];
   private double[] doubleValues5 = new double[0];
   private double[] doubleValues6 = new double[0];
   private double[] doubleValues7 = new double[0];
   private float[] floatValues = new float[0];
   private float[] floatValues2 = new float[0];
   private float[] floatValues3 = new float[0];
   private ZenithColor[] zenithColors = new ZenithColor[0];
   private boolean[] flags = new boolean[0];
   private static final int CONST_intValue4 = 48;
   public static final int CONST_intValue5 = 0;
   private boolean flag;
   private static final EntityType<?>[] entityTypes = new EntityType[]{
      net.minecraft.world.entity.EntityTypes.ENDER_PEARL,
      net.minecraft.world.entity.EntityTypes.ARROW,
      net.minecraft.world.entity.EntityTypes.SPECTRAL_ARROW,
      net.minecraft.world.entity.EntityTypes.TRIDENT,
      net.minecraft.world.entity.EntityTypes.SNOWBALL,
      net.minecraft.world.entity.EntityTypes.EGG,
      net.minecraft.world.entity.EntityTypes.SPLASH_POTION,
      net.minecraft.world.entity.EntityTypes.LINGERING_POTION,
      net.minecraft.world.entity.EntityTypes.EXPERIENCE_BOTTLE
   };
   private static Field field;
   private static Field field2;
   private static boolean flag2;

   private PredictionsModule() {
   }

   @Override
   public void onDisable() {
      this.items.clear();
      this.items2.clear();
      this.items3 = List.of();
      this.items4.clear();
      this.items5.clear();
      this.items6.clear();
      this.items7.clear();
      this.items8.clear();
      this.items13.clear();
      this.items9.clear();
      this.items11.clear();
      this.world = null;
      super.onDisable();
   }

   private void method2886() {
      if (mc.level != this.world) {
         this.items5.clear();
         this.items4.clear();
         this.items8.clear();
         this.world = mc.level;
      }
   }

   private boolean method2887(Entity thrownTrident, Entity owner, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      int n = 0;

      for (int i = this.items13.size(); n < i; n++) {
         LivingEntity livingEntity = this.items13.get(n);
         if (livingEntity != thrownTrident && livingEntity != owner && livingEntity.getBoundingBox().intersects(minX, minY, minZ, maxX, maxY, maxZ)) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onTick(TickEvent event) {
      long now = System.currentTimeMillis();

      for (PredictionsModule.Entry entry : this.items6) {
         entry.update();
         entry.update();
         entry.update();
      }

      this.items6.removeIf(var2x -> now - var2x.longValue > 600L);
      this.items.clear();
      this.items2.clear();
      this.method2886();
      if (mc.player != null && mc.level != null) {
         method2925();
         this.method2926();
         this.items3 = this.method2908();
         if (!this.items11.isEmpty()) {
            HashSet hashSet = new HashSet();

            for (Entity entity : this.items3) {
               hashSet.add(entity.getId());
            }

            this.items11.keySet().retainAll(hashSet);
         }

         this.items13.clear();
         if (!this.items3.isEmpty()) {
            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity instanceof LivingEntity livingEntity && livingEntity != mc.player && livingEntity.isAlive()) {
                  this.items13.add(livingEntity);
               }
            }
         }

         for (Entity thrownTrident : this.items3) {
            Vec3 deltaMovement = thrownTrident.getDeltaMovement();
            Vec3 position = thrownTrident.position();
            int intValue = 0;
            ArrayList arrayList = new ArrayList();
            arrayList.add(position);
            Entity owner = thrownTrident instanceof Projectile projectile ? projectile.getOwner() : null;

            for (int i = 0; i < 300; i++) {
               Vec3 from = position;
               position = position.add(deltaMovement);
               deltaMovement = this.method2921(thrownTrident, from, deltaMovement);
               BlockHitResult blockHitResult = TargetUtils.method3208(from, position, Block.COLLIDER, thrownTrident);
               if (!blockHitResult.getType().equals(Type.MISS)) {
                  position = blockHitResult.getLocation();
               }

               arrayList.add(position);
               boolean flag = this.method2887(
                  thrownTrident,
                  owner,
                  Math.min(from.x, position.x) - 0.25,
                  Math.min(from.y, position.y) - 0.25,
                  Math.min(from.z, position.z) - 0.25,
                  Math.max(from.x, position.x) + 0.25,
                  Math.max(from.y, position.y) + 0.25,
                  Math.max(from.z, position.z) + 0.25
               );
               if (blockHitResult.getType().equals(Type.BLOCK) || position.y < -128.0 || flag || blockHitResult.getType().equals(Type.ENTITY)) {
                  this.method2922(thrownTrident, position, intValue);
                  break;
               }

               intValue++;
            }

            this.items2.add(arrayList);
         }
      } else {
         this.items3 = List.of();
         this.items13.clear();
         this.items9.clear();
         this.items11.clear();
      }
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      for (PredictionsModule.Data2 data2 : this.items) {
         if (ProjectionUtils.method302(data2.vec3d)) {
            Vec3 vec = ProjectionUtils.method301(data2.vec3d);
            EntityESPModule entityESPModule = EntityESPModule.INSTANCE;
            float x = (float)vec.x();
            float y = (float)vec.y() + EntityESPModule.method608() / 2.0F;
            entityESPModule.method609(event.getCustomDrawContext(), x, y, data2.itemStack, this.method2888(data2.intValue), entityESPModule.method553(data2.vec3d));
         }
      }
   }

   private Component method2888(int intValue) {
      String text = String.format(Locale.ROOT, "%.1f", intValue * 50 / 1000.0);
      String str = Zenith.getInstance().getLocalization().translate("module.predictions.seconds");
      return Component.literal(text)
         .append(
            Component.literal(" " + str)
               .setStyle(Style.EMPTY.withColor(Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
         );
   }

   @EventTarget
   public void method2889(WorldRenderEvent event) {
      if (mc.player != null && mc.level != null) {
         this.method2902(event.getMatrixStack(), ArmorSlots.method5385(mc.player));
         if (!this.items2.isEmpty()) {
            this.method2890(this.items2, event.getFloatValue());
         }

         if (this.mode.isSelected(0) && !this.items3.isEmpty()) {
            this.method2897(this.items3, System.currentTimeMillis());
         }
      }
   }

   private void method2890(List<List<Vec3>> vecses, float floatValue) {
      Camera camera = mc.getEntityRenderDispatcher().camera;
      Vec3 position = camera.position();
      matrix4f.identity().rotateX(camera.xRot() * (float) (Math.PI / 180.0)).rotateY((camera.yRot() + 180.0F) * (float) (Math.PI / 180.0));
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
      RenderState.method1890(0, GlowUtil.identifier);
      RenderState.method1898("core/position_tex_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      float f = (float)(System.nanoTime() / 1.0E9 * 5.0 % (Math.PI * 2));

      for (int i = 0; i < vecses.size(); i++) {
         List list = (List)vecses.get(i);
         if (list.size() >= 2) {
            List list2 = list;
            if (i < this.items3.size()) {
               list2 = new ArrayList(list.size() + 1);
               list2.add(this.items3.get(i).getPosition(floatValue));
               list2.addAll(list);
            }

            this.method2892((List<Vec3>)list2, Vec3.ZERO, position, f);
            this.method2893(begin, list2.size(), 0.28599998F, 0.3F);
            this.method2893(begin, list2.size(), 0.11F, 0.9F);
         }
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }

      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1876();
      RenderState.method1872();
      GL11.glDisable(2881);
   }

   private void method2891(int size) {
      if (this.doubleValues.length < size) {
         int n = Math.max(size, this.doubleValues.length * 2);
         this.doubleValues = new double[n];
         this.doubleValues2 = new double[n];
         this.doubleValues3 = new double[n];
         this.doubleValues4 = new double[n];
         this.doubleValues5 = new double[n];
         this.doubleValues6 = new double[n];
         this.doubleValues7 = new double[n];
         this.floatValues = new float[n];
         this.floatValues2 = new float[n];
         this.floatValues3 = new float[n];
         this.zenithColors = new ZenithColor[n];
         this.flags = new boolean[n];
      }
   }

   private void method2892(List<Vec3> vecs, Vec3 vec, Vec3 position, float f) {
      int size = vecs.size();
      this.method2891(size);

      for (int i = 0; i < size; i++) {
         Vec3 vec2 = ((Vec3)vecs.get(i)).add(vec).subtract(position);
         Vec3 vec3 = this.method2894(vecs, i, size);
         if (vec3 == null) {
            this.flags[i] = true;
            this.zenithColors[i] = null;
         } else {
            this.flags[i] = false;
            double length = vec2.length();
            this.doubleValues7[i] = Math.max(1.0, length / 26.0);
            Vec3 vec4 = this.method2895(vec3, vec2);
            this.doubleValues[i] = vec2.x;
            this.doubleValues2[i] = vec2.y;
            this.doubleValues3[i] = vec2.z;
            this.doubleValues4[i] = vec4.x;
            this.doubleValues5[i] = vec4.y;
            this.doubleValues6[i] = vec4.z;
            float f2 = (float)i / (size - 1);
            this.floatValues[i] = Mth.clamp(i / 3.0F, 0.0F, 1.0F);
            this.floatValues2[i] = Mth.clamp((size - 1 - i) / 6.0F, 0.0F, 1.0F);
            this.floatValues3[i] = 0.78F + 0.22F * Mth.sin(f2 * 11.0F - f);
            this.zenithColors[i] = Zenith.getInstance().getStyleManager().getClientColor((int)(f2 * 180.0F));
         }
      }
   }

   private void method2893(BufferBuilder begin, int size, float f, float f2) {
      Matrix4f pose = matrix4f;
      boolean flag = false;
      float x = 0.0F;
      float y = 0.0F;
      float z = 0.0F;
      float x2 = 0.0F;
      float y2 = 0.0F;
      float z2 = 0.0F;
      float f3 = 0.0F;
      ZenithColor zenithColor = null;

      for (int i = 0; i < size; i++) {
         if (!this.flags[i]) {
            float f4 = (float)(f * this.doubleValues7[i]);
            double d = this.doubleValues4[i] * f4;
            double d2 = this.doubleValues5[i] * f4;
            double d3 = this.doubleValues6[i] * f4;
            float x3 = (float)(this.doubleValues[i] + d);
            float y3 = (float)(this.doubleValues2[i] + d2);
            float z3 = (float)(this.doubleValues3[i] + d3);
            float x4 = (float)(this.doubleValues[i] - d);
            float y4 = (float)(this.doubleValues2[i] - d2);
            float z4 = (float)(this.doubleValues3[i] - d3);
            float f5 = f2 * this.floatValues[i] * this.floatValues2[i] * this.floatValues3[i];
            ZenithColor clientColor = this.zenithColors[i];
            if (flag) {
               int color = this.method2896(zenithColor, f3);
               int n = this.method2896(clientColor, f5);
               begin.addVertex(pose, x, y, z).setUv(0.0F, 0.5F).setColor(color);
               begin.addVertex(pose, x2, y2, z2).setUv(1.0F, 0.5F).setColor(color);
               begin.addVertex(pose, x4, y4, z4).setUv(1.0F, 0.5F).setColor(n);
               begin.addVertex(pose, x3, y3, z3).setUv(0.0F, 0.5F).setColor(n);
            }

            flag = true;
            x = x3;
            y = y3;
            z = z3;
            x2 = x4;
            y2 = y4;
            z2 = z4;
            f3 = f5;
            zenithColor = clientColor;
         }
      }
   }

   private Vec3 method2894(List<Vec3> vecs, int n, int size) {
      Vec3 vec = Vec3.ZERO;
      if (n > 0) {
         vec = vec.add(((Vec3)vecs.get(n)).subtract((Vec3)vecs.get(n - 1)));
      }

      if (n < size - 1) {
         vec = vec.add(((Vec3)vecs.get(n + 1)).subtract((Vec3)vecs.get(n)));
      }

      return vec.lengthSqr() < 1.0E-8 ? null : vec.normalize();
   }

   private Vec3 method2895(Vec3 vec, Vec3 vec2) {
      Vec3 vec3 = vec2.lengthSqr() < 1.0E-8 ? new Vec3(0.0, 0.0, 1.0) : vec2.normalize().reverse();
      Vec3 vec4 = vec.cross(vec3);
      if (vec4.lengthSqr() < 1.0E-8) {
         vec4 = vec.cross(new Vec3(0.0, 1.0, 0.0));
         if (vec4.lengthSqr() < 1.0E-8) {
            vec4 = vec.cross(new Vec3(1.0, 0.0, 0.0));
         }
      }

      return vec4.normalize();
   }

   private int method2896(ZenithColor clientColor, float f) {
      int n = (int)(Mth.clamp(f, 0.0F, 1.0F) * 255.0F);
      return n << 24 | clientColor.getIntValue() << 16 | clientColor.getIntValue2() << 8 | clientColor.getIntValue3();
   }

   private void method2897(List<Entity> entities, long now) {
      Camera camera = mc.getEntityRenderDispatcher().camera;
      Vec3 position = camera.position();
      float xRot = camera.xRot();
      float yRot = camera.yRot();
      float ang = xRot * (float) (Math.PI / 180.0);
      float f = yRot * (float) (Math.PI / 180.0);
      this.matrix4f2.identity().rotateX(ang).rotateY((yRot + 180.0F) * (float) (Math.PI / 180.0));
      this.matrix4f3.identity().rotateY(-f).rotateX(ang);
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
      RenderState.method1890(0, identifier);
      RenderState.method1898("core/position_tex_color");
      byte b = 8;
      ZenithColor[] zenithColors = new ZenithColor[8];

      for (int i = 0; i < 8; i++) {
         zenithColors[i] = Zenith.getInstance().getStyleManager().getClientColor(i * 45);
      }

      float gameTimeDeltaPartialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
      double d = System.nanoTime() % 1000000000000L / 1.0E9;
      long nanoTime = System.nanoTime();
      float f2 = this.longValue2 == 0L ? 0.05F : Mth.clamp((float)(nanoTime - this.longValue2) / 1.0E9F, 0.0F, 0.1F);
      this.longValue2 = nanoTime;
      float f3 = 1.0F - (float)Math.exp(-f2 * 14.0);
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

      for (Entity entity : entities) {
         Vec3 deltaMovement = entity.getDeltaMovement();
         Vec3 vec = this.items11.get(entity.getId());
         if (vec != null && !(vec.distanceToSqr(deltaMovement) > 1.0)) {
            vec = vec.lerp(deltaMovement, f3);
         } else {
            vec = deltaMovement;
         }

         this.items11.put(entity.getId(), vec);
         double d2 = vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
         if (!(d2 < 1.0E-4)) {
            Vec3 position2 = entity.getPosition(gameTimeDeltaPartialTick);
            double x = position2.x;
            double d3 = position2.y + entity.getBbHeight() * 0.5;
            double z = position2.z;
            double d4 = Math.sqrt(position2.distanceToSqr(position));
            float f4 = (float)Mth.clamp(d4 / 22.0, 1.0, 6.0);
            int n = d4 > 96.0 ? 28 : 56;
            double d5 = 1.0 / Math.sqrt(d2);
            double d6 = vec.x * d5;
            double d7 = vec.y * d5;
            double d8 = vec.z * d5;
            double d9 = -d8;
            double d10 = 0.0;
            double d11 = d6;
            double d12 = d9 * d9 + d6 * d6;
            if (d12 < 1.0E-4) {
               d9 = 0.0;
               d10 = d8;
               d11 = -d7;
               d12 = d8 * d8 + d11 * d11;
            }

            double d13 = 1.0 / Math.sqrt(d12);
            d9 *= d13;
            d10 *= d13;
            d11 *= d13;
            double d14 = d10 * d8 - d11 * d7;
            double d15 = d11 * d6 - d9 * d8;
            double d16 = d9 * d7 - d10 * d6;
            float f5 = Mth.clamp(entity.getBbWidth() * 0.5F, 0.09F, 0.22F) * f4;
            double d17 = Math.min(2.4, Math.sqrt(d2) * 4.5);
            double d18 = (0.45 + d17 * 0.3) * f4;
            double d19 = (0.34 + d17 * 0.26) * f4;
            double d20 = Math.PI * 3;
            double d21 = d * 0.3 + entity.getId() * 0.071;

            for (int i = 0; i < n; i++) {
               double d22 = d21 + i * (1.0 / n);
               d22 -= Math.floor(d22);
               float f6 = (float)Math.min(1.0, d22 / 0.22);
               float f7 = (float)(1.0 - d22);
               float f8 = f6 * f6 * (3.0F - 2.0F * f6);
               float f9 = f7 * f7 * (3.0F - 2.0F * f7);
               float f10 = f8 * f9;
               if (!(f10 < 0.02F)) {
                  double d23 = d22 * (Math.PI * 3) + i * 2.399963 * 0.25 + d * 0.3;
                  double d24 = d18 * d22;
                  double d25 = i * 0.6180339887 % 1.0;
                  double d26 = d19 * (0.35 + d25 * 0.75) * (0.25 + d22 * 0.75);
                  double d27 = Math.cos(d23) * d26;
                  double d28 = Math.sin(d23) * d26;
                  double x2 = x - d6 * d24 + d9 * d27 + d14 * d28 - position.x;
                  double y = d3 - d7 * d24 + d10 * d27 + d15 * d28 - position.y;
                  double z2 = z - d8 * d24 + d11 * d27 + d16 * d28 - position.z;
                  float f11 = f5 * (1.0F - (float)d22 * 0.55F);
                  ZenithColor zenithColor = zenithColors[i % 8];
                  this.method2898(begin, x2, y, z2, f11 * 2.2F, f10 * 0.7F, zenithColor, false);
                  if (f10 > 0.3F) {
                     this.method2898(begin, x2, y, z2, f11, f10 * 1.9F, zenithColor, true);
                  }
               }
            }
         }
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }

      RenderState.method1882(true);
      RenderState.method1876();
      RenderState.method1872();
      RenderState.method1873();
   }

   private void method2898(BufferBuilder begin, double x, double y, double z, float f, float f2, ZenithColor zenithColor, boolean flag) {
      this.matrix4f4.set(this.matrix4f2).translate((float)x, (float)y, (float)z).mul(this.matrix4f3);
      int color = this.method2899(zenithColor, flag ? f2 : f2 * 0.7F);
      float x2 = f * (flag ? 0.72F : 0.9F);
      begin.addVertex(this.matrix4f4, x2, -x2, 0.0F).setUv(0.0F, 1.0F).setColor(color);
      begin.addVertex(this.matrix4f4, -x2, -x2, 0.0F).setUv(1.0F, 1.0F).setColor(color);
      begin.addVertex(this.matrix4f4, -x2, x2, 0.0F).setUv(1.0F, 0.0F).setColor(color);
      begin.addVertex(this.matrix4f4, x2, x2, 0.0F).setUv(0.0F, 0.0F).setColor(color);
   }

   private int method2899(ZenithColor zenithColor, float f) {
      int n = Mth.clamp((int)(f * 255.0F), 0, 255);
      return n << 24 | zenithColor.getIntValue() << 16 | zenithColor.getIntValue2() << 8 | zenithColor.getIntValue3();
   }

   private void method2900(Entity entity) {
      int id = entity.getId();
      long now = System.currentTimeMillis();
      if (!this.items7.containsKey(id) || now - this.items7.get(id) >= 20L) {
         this.items7.put(id, now);
         Vec3 position = entity.position();
         Vec3 vec = entity.getDeltaMovement().normalize().scale(-0.05);

         for (int i = 0; i < 3; i++) {
            double d = (this.random.nextDouble() - 0.5) * 0.35;
            double d2 = (this.random.nextDouble() - 0.5) * 0.35;
            double d3 = (this.random.nextDouble() - 0.5) * 0.35;
            double d4 = vec.x + (this.random.nextDouble() - 0.5) * 0.06;
            double d5 = vec.y + (this.random.nextDouble() - 0.5) * 0.06 - 0.01;
            double d6 = vec.z + (this.random.nextDouble() - 0.5) * 0.06;
            this.items6.add(new PredictionsModule.Entry(position.x + d, position.y + d2, position.z + d3, d4, d5, d6, this.random.nextInt(90), now));
         }

         this.items7.entrySet().removeIf(var2x -> now - var2x.getValue() > 5000L);
      }
   }

   private void method2901(PoseStack poseStack) {
      if (!this.items6.isEmpty()) {
         long now = System.currentTimeMillis();
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
         RenderState.method1890(0, GlowUtil.identifier);
         RenderState.method1898("core/position_tex_color");
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         Camera camera = mc.getEntityRenderDispatcher().camera;

         for (PredictionsModule.Entry entry : this.items6) {
            long l = now - entry.longValue;
            float f = (float)l / 600.0F;
            float previewAlpha;
            if (f < 0.2F) {
               previewAlpha = f / 0.2F;
            } else {
               previewAlpha = 1.0F - (f - 0.2F) / 0.8F;
            }

            previewAlpha = Mth.clamp(previewAlpha, 0.0F, 1.0F);
            float f2 = 0.27F * (1.0F - f * 0.6F);
            double xo = entry.doubleValue - camera.position().x;
            double yo = entry.doubleValue2 - camera.position().y;
            double zo = entry.doubleValue3 - camera.position().z;
            PoseStack poseStack2 = new PoseStack();
            poseStack2.rotate(Axis.XP.rotationDegrees(camera.xRot()));
            poseStack2.rotate(Axis.YP.rotationDegrees(camera.yRot() + 180.0F));
            poseStack2.translate(xo, yo, zo);
            poseStack2.rotate(Axis.YP.rotationDegrees(-camera.yRot()));
            poseStack2.rotate(Axis.XP.rotationDegrees(camera.xRot()));
            Matrix4f pose = poseStack2.last().pose();
            int color = Zenith.getInstance().getStyleManager().getClientColor(entry.intValue).method7294(previewAlpha).method7287();
            begin.addVertex(pose, -f2 / 2.0F, -f2 / 2.0F, 0.0F).setUv(0.0F, 0.0F).setColor(color);
            begin.addVertex(pose, -f2 / 2.0F, f2 / 2.0F, 0.0F).setUv(0.0F, 1.0F).setColor(color);
            begin.addVertex(pose, f2 / 2.0F, f2 / 2.0F, 0.0F).setUv(1.0F, 1.0F).setColor(color);
            begin.addVertex(pose, f2 / 2.0F, -f2 / 2.0F, 0.0F).setUv(1.0F, 0.0F).setColor(color);
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
      }
   }

   public void method2902(PoseStack matrixStack, List<ItemStack> snowball) {
      this.method2886();
      if (mc.player != null && mc.level != null && !snowball.isEmpty()) {
         ItemStack mainHandItem = (ItemStack)snowball.getFirst();
         PredictionsModule.Data data = this.items4.get(mainHandItem.getItem());
         if (data == null || data.getIntValue() != mc.player.tickCount || data.getWorld() != mc.level) {
            var items = this.method2903(mainHandItem, mc.player.getUseItem().getItem(), RotationUtils.method6911());
            items = items == null ? List.of() : items.stream().filter(Objects::nonNull).toList();
            data = new PredictionsModule.Data(mc.player.tickCount, mc.level, items);
            this.items4.put(mainHandItem.getItem(), data);
         }

         if (!data.getItems().isEmpty()) {
            this.method2905(matrixStack, data.getItems());
         }
      }
   }

   public List<HitResult> method2903(ItemStack mainHandItem, Item item, Rotation rotation) {
      this.method2886();
      Object obj = mainHandItem.getItem();
      Objects.requireNonNull(obj);

      return (List<HitResult>)(switch (obj) {
         case ExperienceBottleItem experienceBottleItem -> this.method2914(this.method2904(mainHandItem, () -> new ThrownExperienceBottle(mc.level, mc.player, mainHandItem)), rotation, 0.8);
         case SplashPotionItem splashPotionItem -> this.method2914(this.method2904(mainHandItem, () -> new ThrownSplashPotion(mc.level, mc.player, mainHandItem)), rotation, 0.55);
         case TridentItem tridentItem when tridentItem.equals(item) && mc.player.getTicksUsingItem() >= 10 -> this.method2914(
            this.method2904(mainHandItem, () -> new ThrownTrident(mc.level, mc.player, mainHandItem)), rotation, 2.5
         );
         case SnowballItem snowballItem -> this.method2914(this.method2904(mainHandItem, () -> new Snowball(mc.level, mc.player, mainHandItem)), rotation, 1.5);
         case EggItem eggItem -> this.method2914(this.method2904(mainHandItem, () -> new ThrownEgg(mc.level, mc.player, mainHandItem)), rotation, 1.5);
         case EnderpearlItem enderpearlItem -> this.method2914(this.method2904(mainHandItem, () -> new ThrownEnderpearl(mc.level, mc.player, mainHandItem)), rotation, 1.5);
         case BowItem bowItem when bowItem.equals(item) && mc.player.isUsingItem() -> this.method2914(
            this.method2904(mainHandItem, () -> new Arrow(mc.level, mc.player, mainHandItem, mainHandItem)),
            rotation,
            3.0F * Mth.clamp((mc.player.getTicksUsingItem() + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) / 20.0F, 0.0F, 1.0F)
         );
         case CrossbowItem crossbowItem when CrossbowItem.isCharged(mainHandItem) -> {
            ChargedProjectiles chargedProjectiles = mainHandItem.get(DataComponents.CHARGED_PROJECTILES);
            ArrayList arrayList = new ArrayList();
            if (chargedProjectiles != null) {
               float f = chargedProjectiles.items().getFirst().is(Items.FIREWORK_ROCKET) ? 100.0F : 3.0F;
               arrayList.add(this.method2917(rotation.method3135(), this.method2904(mainHandItem, () -> new Arrow(mc.level, mc.player, mainHandItem, mainHandItem)), f));
               if (chargedProjectiles.items().size() > 2) {
                  float f2 = mc.player.getXRot() / 90.0F;
                  float alpha1 = f2 * f2 * f2 * f2 * f2;
                  float lerpInt = Mth.lerpInt(Math.abs(alpha1), 10, 90);
                  float lerpInt2 = Mth.lerpInt(alpha1, 0, 10);
                  arrayList.add(
                     this.method2917(
                        new Rotation(mc.player.getYRot() - lerpInt, mc.player.getXRot() - lerpInt2).method3135(),
                        this.method2904(mainHandItem, () -> new Arrow(mc.level, mc.player, mainHandItem, mainHandItem)),
                        f
                     )
                  );
                  arrayList.add(
                     this.method2917(
                        new Rotation(mc.player.getYRot() + lerpInt, mc.player.getXRot() - lerpInt2).method3135(),
                        this.method2904(mainHandItem, () -> new Arrow(mc.level, mc.player, mainHandItem, mainHandItem)),
                        f
                     )
                  );
               }
            }

            yield arrayList;
         }
         default -> null;
      });
   }

   private Projectile method2904(ItemStack mainHandItem, Supplier<? extends Projectile> supplier) {
      Projectile projectile = this.items5.get(mainHandItem.getItem());
      if (projectile != null && projectile.level() == mc.level && projectile.getOwner() == mc.player) {
         projectile.setPos(mc.player.getX(), mc.player.getEyeY() - 0.1F, mc.player.getZ());
         projectile.setDeltaMovement(Vec3.ZERO);
         projectile.setOnGround(false);
         if (projectile instanceof ThrowableItemProjectile itemProjectile) {
            itemProjectile.setItem(mainHandItem);
         }
      } else {
         projectile = (Projectile)supplier.get();
         this.items5.put(mainHandItem.getItem(), projectile);
      }

      return projectile;
   }

   public void method2905(PoseStack matrixStack, List<HitResult> items) {
      Camera camera = mc.getEntityRenderDispatcher().camera;
      long nanoTime = System.nanoTime();
      float f = this.longValue == 0L ? 0.05F : Mth.clamp((float)(nanoTime - this.longValue) / 1.0E9F, 0.0F, 0.1F);
      this.longValue = nanoTime;
      float f2 = 1.0F - (float)Math.exp(-f * 20.0);
      double d = nanoTime / 1.0E9;
      float f3 = (float)(d * 3.2 % (Math.PI * 2));
      float f4 = (float)(d * 0.6 % (Math.PI / 2));
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
      RenderState.method1890(0, GlowUtil.identifier);
      RenderState.method1898("core/position_tex_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

      for (int i = 0; i < items.size(); i++) {
         HitResult hitResult = (HitResult)items.get(i);
         Vec3 location = hitResult.getLocation();
         Vec3 vec = this.items8.get(i);
         if (vec != null && !(vec.distanceToSqr(location) > 64.0)) {
            vec = vec.lerp(location, f2);
         } else {
            vec = location;
         }

         this.items8.put(i, vec);
         Vec3 vec2 = vec.subtract(camera.position());
         boolean flag = hitResult.getType().equals(Type.ENTITY);
         ZenithColor clientColor = flag ? ZenithColor.INSTANCE4 : Zenith.getInstance().getStyleManager().getClientColor(90);
         matrixStack.pushPose();
         matrixStack.translate(vec2.x, vec2.y, vec2.z);
         switch (this.method2923(hitResult)) {
            case UP:
            default:
               break;
            case DOWN:
               matrixStack.rotate(Axis.XP.rotationDegrees(180.0F));
               break;
            case NORTH:
               matrixStack.rotate(Axis.XP.rotationDegrees(-90.0F));
               break;
            case SOUTH:
               matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
               break;
            case WEST:
               matrixStack.rotate(Axis.ZP.rotationDegrees(-90.0F));
               break;
            case EAST:
               matrixStack.rotate(Axis.ZP.rotationDegrees(90.0F));
         }

         matrixStack.translate(0.0, 0.01, 0.0);
         Matrix4f pose = matrixStack.last().pose();
         float f5 = 0.85F + 0.15F * Mth.sin(f3);
         this.method2906(begin, pose, 0.62F * f5, this.method2896(clientColor, 0.22F));
         this.method2907(begin, pose, 0.3F * f5, 0.045F, this.method2896(clientColor, 0.95F), 0.0F, 48, 1.0F);
         this.method2907(begin, pose, 0.17F, 0.018F, this.method2896(clientColor, 0.55F), 0.0F, 48, 1.0F);
         this.method2907(begin, pose, 0.4F, 0.05F, this.method2896(clientColor, 0.8F), f4, 4, 0.22F);
         matrixStack.popPose();
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }

      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1876();
      RenderState.method1872();
   }

   private void method2906(BufferBuilder begin, Matrix4f pose, float z, int color) {
      begin.addVertex(pose, -z, 0.0F, -z).setUv(0.0F, 0.0F).setColor(color);
      begin.addVertex(pose, -z, 0.0F, z).setUv(0.0F, 1.0F).setColor(color);
      begin.addVertex(pose, z, 0.0F, z).setUv(1.0F, 1.0F).setColor(color);
      begin.addVertex(pose, z, 0.0F, -z).setUv(1.0F, 0.0F).setColor(color);
   }

   private void method2907(BufferBuilder begin, Matrix4f pose, float f, float f2, int color, float f3, int n, float f4) {
      float f5 = f - f2;
      float f6 = f + f2;
      float f7 = (float) (Math.PI * 2) / n;

      for (int i = 0; i < n; i++) {
         float f8 = f3 + i * f7;
         float f9 = f8 + f7 * f4;
         float f10 = Mth.cos(f8);
         float f11 = Mth.sin(f8);
         float f12 = Mth.cos(f9);
         float f13 = Mth.sin(f9);
         begin.addVertex(pose, f10 * f5, 0.0F, f11 * f5).setUv(0.0F, 0.5F).setColor(color);
         begin.addVertex(pose, f10 * f6, 0.0F, f11 * f6).setUv(1.0F, 0.5F).setColor(color);
         begin.addVertex(pose, f12 * f6, 0.0F, f13 * f6).setUv(1.0F, 0.5F).setColor(color);
         begin.addVertex(pose, f12 * f5, 0.0F, f13 * f5).setUv(0.0F, 0.5F).setColor(color);
      }
   }

   public List<Entity> method2908() {
      return UseItemUtils.method6510()
         .filter(
            entity -> (
                  entity instanceof AbstractArrow
                     || entity instanceof ThrowableItemProjectile
                     || entity instanceof ItemEntity && this.renderItemEntity.isEnabled()
               )
               && !this.method2924(entity)
         )
         .toList();
   }

   public boolean method2909() {
      return this.isEnabled() && this.mode.getIndex() >= 1;
   }

   public Identifier method2910() {
      int n = this.mode.getIndex() - 1;
      return n >= 0 && n < identifiers.length ? identifiers[n] : null;
   }

   public boolean method2911(Entity entity) {
      if (this.method2909() && entity != null) {
         return entity instanceof AbstractArrow
               || entity instanceof ThrowableItemProjectile
               || entity instanceof ItemEntity && this.renderItemEntity.isEnabled()
            ? !this.method2924(entity)
            : false;
      } else {
         return false;
      }
   }

   public int method2912() {
      this.flag = true;
      return 0;
   }

   public boolean method2913() {
      boolean flag = this.flag;
      this.flag = false;
      return flag;
   }

   public List<HitResult> method2914(Projectile thrownTrident, Rotation rotation, double d) {
      return new ArrayList<>(Collections.singleton(this.method2917(rotation.method3135(), thrownTrident, d)));
   }

   public List<HitResult> method2915(Projectile thrownTrident, double d) {
      return this.method2914(thrownTrident, RotationUtils.method6911(), d);
   }

   public List<HitResult> method2916(ItemStack useItem, Rotation rotation, Vec3 position, Vec3 vec3d2, int n) {
      if (useItem.getItem() instanceof BowItem && mc.player.isUsingItem()) {
         float f = mc.player.getTicksUsingItem() + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false) + n;
         double d = 3.0F * Mth.clamp(f / 20.0F, 0.0F, 1.0F);
         HitResult hitResult = this.method2918(position, vec3d2, rotation.method3135(), this.method2904(useItem, () -> new Arrow(mc.level, mc.player, useItem, useItem)), d);
         return (List<HitResult>)(hitResult == null ? List.of() : new ArrayList<>(Collections.singleton(hitResult)));
      } else {
         return null;
      }
   }

   public HitResult method2917(Vec3 vec, Projectile thrownTrident, double d) {
      Vec3 vec3d2 = mc.player.position().subtract(mc.player.xo, mc.player.yo, mc.player.zo);
      if (thrownTrident instanceof Arrow arrow && arrow.getPickupItemStackOrigin().getItem() instanceof CrossbowItem) {
         vec3d2 = Vec3.ZERO;
      }

      return this.method2918(mc.player.getEyePosition().add(MathUtils.method2656(mc.player).subtract(mc.player.position())), vec3d2, vec, thrownTrident, d);
   }

   public HitResult method2918(Vec3 position, Vec3 vec3d2, Vec3 vec, Projectile thrownTrident, double d) {
      double length = vec.length();
      return length <= 1.0E-6 ? null : this.method2920(position, vec.scale(d / length).add(vec3d2), thrownTrident);
   }

   public HitResult method2919(Projectile thrownTrident) {
      return this.method2920(thrownTrident.position(), thrownTrident.getDeltaMovement(), thrownTrident);
   }

   public HitResult method2920(Vec3 position, Vec3 deltaMovement, Projectile thrownTrident) {
      for (int i = 0; i < 300; i++) {
         Vec3 from = position;
         position = position.add(deltaMovement);
         deltaMovement = this.method2921(thrownTrident, from, deltaMovement);
         BlockHitResult blockHitResult = TargetUtils.method3208(from, position, Block.COLLIDER, thrownTrident);
         if (!blockHitResult.getType().equals(Type.MISS)) {
            return blockHitResult;
         }

         var entities = mc.level
            .getEntities(
               thrownTrident,
               new AABB(from, position).inflate(0.3),
               var1x -> var1x != thrownTrident.getOwner() && var1x instanceof LivingEntity var2x && var2x != mc.player && var2x.isAlive()
            );
         if (!entities.isEmpty()) {
            return new EntityHitResult((Entity)entities.getFirst(), position);
         }

         if (position.y < -128.0) {
            break;
         }
      }

      return null;
   }

   public Vec3 method2921(Entity thrownTrident, Vec3 from, Vec3 deltaMovement) {
      boolean is = Objects.requireNonNull(mc.level).getBlockState(BlockPos.containing(from)).getFluidState().is(FluidTags.WATER);
      Objects.requireNonNull(thrownTrident);

      float scale = switch (thrownTrident) {
         case ThrownTrident thrownTrident2 -> 0.99F;
         case AbstractArrow abstractArrow when is -> 0.6F;
         default -> is ? 0.8F : 0.99F;
      };
      return deltaMovement.scale(scale).add(0.0, -thrownTrident.getGravity(), 0.0);
   }

   private void method2922(Entity thrownTrident, Vec3 position, int intValue) {
      switch (thrownTrident) {
         case ItemEntity itemEntity:
            this.items.add(new PredictionsModule.Data2(itemEntity.getItem(), position, intValue));
            break;
         case ThrowableItemProjectile itemProjectile:
            this.items.add(new PredictionsModule.Data2(itemProjectile.getItem(), position, intValue));
            break;
         case AbstractArrow abstractArrow:
            this.items.add(new PredictionsModule.Data2(abstractArrow.getPickupItemStackOrigin(), position, intValue));
            break;
         default:
      }
   }

   private Direction method2923(HitResult hitResult) {
      if (hitResult instanceof BlockHitResult blockHitResult) {
         return blockHitResult.getDirection();
      } else {
         Vec3 vec = hitResult.getLocation().subtract(mc.player.getEyePosition()).normalize();
         return Direction.getApproximateNearest(vec.x, vec.y, vec.z);
      }
   }

   private boolean method2924(Entity entity) {
      boolean flag = this.items9.getOrDefault(entity.getId(), 0) >= 4;
      boolean flag2 = entity instanceof ItemEntity && (entity.onGround() || UseItemUtils.method6515(entity.getBoundingBox().inflate(2.0), Blocks.WATER));
      return flag || flag2;
   }

   private static void method2925() {
      if (!flag2) {
         flag2 = true;

         try {
            Class clazz = Class.forName("dev.tr7zw.entityculling.EntityCullingModBase");
            field = clazz.getField("instance");
            field2 = clazz.getField("tickCullWhitelists");
         } catch (Throwable e) {
            field = null;
            field2 = null;
         }
      }

      if (field2 != null) {
         try {
            Object obj = field.get(null);
            if (obj == null) {
               return;
            }

            Set set = (Set)field2.get(obj);
            if (set == null || set.contains(net.minecraft.world.entity.EntityTypes.ENDER_PEARL)) {
               return;
            }

            Collections.addAll(set, entityTypes);
         } catch (Throwable e) {
            field2 = null;
         }
      }
   }

   private void method2926() {
      var map = this.items10;
      map.clear();
      UseItemUtils.method6510()
         .filter(entity -> entity instanceof AbstractArrow || entity instanceof ThrowableItemProjectile || entity instanceof ItemEntity)
         .forEach(entity -> {
            boolean flag = entity.getX() != entity.xo || entity.getY() != entity.yo || entity.getZ() != entity.zo;
            map.put(entity.getId(), flag ? 0 : this.items9.getOrDefault(entity.getId(), 0) + 1);
         });
      this.items10 = this.items9;
      this.items9 = map;
   }

   private static final class Data {
      private final int intValue;
      private final Level world;
      private final List<HitResult> items;

      public int intValue() {
         return this.intValue;
      }

      public Level world() {
         return this.world;
      }

      public List<HitResult> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.world, other.world) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, world, items);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "world=" + this.world + ", " + "items=" + this.items + "]";
      }

      private Data(int intValue, Level world, List<HitResult> items) {
         this.intValue = intValue;
         this.world = world;
         this.items = items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public Level getWorld() {
         return this.world;
      }

      public List<HitResult> getItems() {
         return this.items;
      }
   }

   private static final class Data2 {
      private final ItemStack itemStack;
      private final Vec3 vec3d;
      private final int intValue;

      public ItemStack itemStack() {
         return this.itemStack;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.itemStack, other.itemStack) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(itemStack, vec3d, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "itemStack=" + this.itemStack + ", " + "vec3d=" + this.vec3d + ", " + "intValue=" + this.intValue + "]";
      }

      private Data2(ItemStack itemStack, Vec3 vec3d, int intValue) {
         this.itemStack = itemStack;
         this.vec3d = vec3d;
         this.intValue = intValue;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public int intValue;
      public long longValue;

      public Entry(double doubleValue, double d, double d2, double d3, double d4, double d5, int intValue, long longValue) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.doubleValue4 = d3;
         this.doubleValue5 = d4;
         this.doubleValue6 = d5;
         this.intValue = intValue;
         this.longValue = longValue;
      }

      public void update() {
         this.doubleValue = this.doubleValue + this.doubleValue4;
         this.doubleValue2 = this.doubleValue2 + this.doubleValue5;
         this.doubleValue3 = this.doubleValue3 + this.doubleValue6;
         this.doubleValue5 -= 0.002;
         this.doubleValue4 *= 0.98;
         this.doubleValue5 *= 0.98;
         this.doubleValue6 *= 0.98;
      }
   }
}
