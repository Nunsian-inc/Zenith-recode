package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.PostRotationEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Vec2f;

@ModuleInfo(
   name = "RotationRecorder",
   category = ModuleCategory.COMBAT,
   description = "module.rotationrecorder.description"
)
public final class RotationRecorderModule extends Module {
   public static final RotationRecorderModule INSTANCE = new RotationRecorderModule();
   private static final String text = "module.rotationRecorder.mode.human";
   private static final String text2 = "module.rotationRecorder.mode.aim";
   private final ModeSetting mode = new ModeSetting(
      "module.rotationRecorder.mode", "module.rotationRecorder.mode.desc", "module.rotationRecorder.mode.human", "module.rotationRecorder.mode.aim"
   );
   private final BooleanSetting disableLogs = new BooleanSetting("module.rotationRecorder.disableLogs", "module.rotationRecorder.disableLogs.desc", false);
   private final ButtonSetting openRecordingsFolder = new ButtonSetting("module.rotationRecorder.openRecordingsFolder", "K", this::method4942);
   private static final String text3 = "rotation_dataset_v30_simpy_v2.csv";
   private static final String text4 = "rotation_aim_dataset_v2.csv";
   private static final String text5 = "rotation_world_snapshots_v7.jsonl";
   private static final int CONST_intValue = 30;
   private static final int CONST_intValue2 = 2;
   private static final int CONST_intValue3 = 1;
   private static final String text6 = "state_on_ground";
   private static final int CONST_intValue4 = 0;
   private static final int CONST_intValue5 = 1;
   private static final long CONST_longValue = -1L;
   private static final int CONST_intValue6 = 6;
   private static final int CONST_intValue7 = 3;
   private static final int CONST_intValue8 = 5;
   private static final long CONST_longValue2 = 128L;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 50.0F;
   private static final float CONST_floatValue3 = 50.0F;
   private static final float CONST_floatValue4 = 20.0F;
   private static final float CONST_floatValue5 = 50.0F;
   private static final int CONST_intValue9 = 20;
   private static final int CONST_intValue10 = 40;
   private static final int CONST_intValue11 = 10;
   private static final int CONST_intValue12 = 64;
   private static final double CONST_doubleValue = 256.0;
   private static final Path path = Path.of("rotation_recordings", "rotation_dataset_v30_simpy_v2.csv");
   private static final Path path2 = Path.of("rotation_recordings", "rotation_aim_dataset_v2.csv");
   private static final Path path3 = Path.of("rotation_recordings", "rotation_world_snapshots_v7.jsonl");
   private static final Path path4 = Path.of("rotation_recordings", "rotation_world_snapshots_v7.jsonl.next");
   private BufferedWriter bufferedWriter;
   private BufferedWriter bufferedWriter2;
   private boolean flag = false;
   private boolean flag2 = false;
   private long longValue = 0L;
   private long longValue2 = 0L;
   private long longValue3 = 1L;
   private long longValue4 = 0L;
   private int intValue = 0;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private float floatValue3 = 0.0F;
   private float floatValue4 = 0.0F;
   private float floatValue5 = 0.0F;
   private float floatValue6 = 0.0F;
   private float floatValue7 = 0.0F;
   private float floatValue8 = 0.0F;
   private float floatValue9 = 0.0F;
   private float floatValue10 = 0.0F;
   private int intValue2 = 0;
   private int intValue3 = 0;
   private float floatValue11 = 50.0F;
   private float floatValue12 = 50.0F;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 0.0F;
   private RotationRecorderModule.Entry entry = null;
   private RotationRecorderModule.Entry4 entry4 = null;
   private boolean flag5 = false;
   private final Map<Integer, RotationRecorderModule.Entry9> items = new HashMap<>();
   private RotationRecorderModule.Entry7 entry7 = null;

   private RotationRecorderModule() {
   }

   @Override
   public void onEnable() {
      this.flag3 = this.mode.method2101("module.rotationRecorder.mode.aim");
      this.method4940();
      this.method4941();
      this.method4945();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method4935();
      this.method4944();
      this.method4948();
      this.method4940();
      if (!this.disableLogs.isEnabled()) {
         ChatLogger.method2019("RotationRecorder: saved " + this.intValue + " rows");
      }

      super.onDisable();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> {
               if (this.isEnabled()) {
                  this.method4936();
                  this.method4940();
               }
            });
         }
      }
   }

   @EventTarget
   public void method4915(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST && mc.player != null && event.getEntity() instanceof LivingEntity livingEntity) {
         this.flag5 = true;
         int tickCount = mc.player.tickCount;
         RotationRecorderModule.Entry9 entry9 = this.items.get(livingEntity.getId());
         if (this.entry7 != null && tickCount > this.entry7.intValue4) {
            this.method4935();
         }

         if (this.entry7 != null) {
            if (!this.entry7.method5026(livingEntity)) {
               this.method4937();
            } else {
               this.entry7.intValue3 = tickCount;
               this.entry7.intValue4 = Math.max(this.entry7.intValue4, tickCount + 40);
            }
         } else {
            if (entry9 != null && entry9.method5031(livingEntity) && entry9.deque.size() >= 21) {
               this.method4932(livingEntity, entry9, tickCount);
            }
         }
      }
   }

   @EventTarget
   public void method4916(PostRotationEvent event) {
      if (!mc.isPaused() && mc.player != null && mc.level != null && !mc.player.isSpectator()) {
         if (this.entry7 != null && mc.player.tickCount > this.entry7.intValue4) {
            this.method4935();
         }

         boolean flag = this.flag5;
         this.flag5 = false;
         float floatValue = this.flag3 ? rotationManager.getEffectiveRotation().getFloatValue() : mc.player.getYRot();
         float xRot = this.flag3 ? rotationManager.getEffectiveRotation().getFloatValue2() : mc.player.getXRot();
         Rotation rotation = new Rotation(floatValue, xRot);
         if (!this.flag4) {
            this.floatValue = floatValue;
            this.floatValue2 = xRot;
            this.flag4 = true;
         }

         float f = Mth.wrapDegrees(mc.player.getYRot() - this.floatValue);
         float f2 = mc.player.getXRot() - this.floatValue2;
         int n = 0;
         if (this.flag3) {
            Rotation rotation2 = new Rotation(mc.player.yRotLast, mc.player.xRotLast);
            Vec2f vec2f = rotation2.wrappedDeltaTo(zenith.getRotationManager().getEffectiveRotation());
            Vec2f vec2f2 = new Vec2f(f, f2);
            f = vec2f.getFloatValue();
            f2 = vec2f.getFloatValue2();
            n = vec2f.getFloatValue() == vec2f2.getFloatValue()
                  && vec2f.getFloatValue2() == vec2f2.getFloatValue2()
                  && mc.player.getYRot() == zenith.getRotationManager().getEffectiveRotation().getFloatValue()
                  && mc.player.getXRot() == zenith.getRotationManager().getEffectiveRotation().getFloatValue2()
               ? 0
               : 1;
         }

         Input keyPresses = this.method4970();
         AABB boundingBox = mc.player.getBoundingBox();
         Vec3 eyePosition = mc.player.getEyePosition();
         byte b = 0;
         RotationRecorderModule.Entry10 entry10 = null;
         RotationRecorderModule.Entry3 entry3 = null;
         Vec3 vec = null;
         HashSet hashSet = new HashSet();

         for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity livingEntity && livingEntity != mc.player && livingEntity.isAlive() && !(mc.player.distanceToSqr(livingEntity) > 256.0)) {
               AABB move = this.method4931(livingEntity);
               Vec3 vec3d = this.method4920(livingEntity);
               if (move != null && vec3d != null) {
                  if (b == 0) {
                     b = 1;
                     entry10 = this.method4957(boundingBox);
                     entry3 = this.method4975(floatValue, xRot, keyPresses);
                     vec = this.method4919(rotation, eyePosition, keyPresses);
                  }

                  RotationRecorderModule.Entry9 entry9 = this.items
                     .compute(
                        livingEntity.getId(),
                        (var1x, var2x) -> (RotationRecorderModule.Entry9)(var2x != null && var2x.method5031(livingEntity)
                           ? var2x
                           : new RotationRecorderModule.Entry9(livingEntity))
                     );
                  hashSet.add(livingEntity.getId());
                  this.method4938(entry9);
                  RotationRecorderModule.Entry entry = this.entry;
                  RotationRecorderModule.Entry entry2 = this.method4955(
                     rotation, eyePosition, livingEntity, move, vec3d, f, f2, keyPresses, flag, (n != 0), entry10, entry3, vec
                  );
                  this.method4939(entry9);
                  if (entry2 != null) {
                     entry9.method5032(entry2);
                     if (this.entry7 != null && this.entry7.method5026(livingEntity) && entry != null) {
                        this.method4934(entry, entry2);
                     }
                  }
               }
            }
         }

         this.items
            .entrySet()
            .removeIf(var2x -> !(hashSet.contains(var2x.getKey()) || this.entry7 != null && this.entry7.intValue == var2x.getKey()));
         if (this.entry7 != null && !hashSet.contains(this.entry7.intValue)) {
            this.method4937();
         }

         this.floatValue = floatValue;
         this.floatValue2 = xRot;
      } else {
         this.method4935();
         this.method4940();
      }
   }

   private RotationRecorderModule.Data2 method4917(
      Rotation rotation,
      Vec3 eyePosition,
      Vec3 vec,
      LivingEntity livingEntity,
      AABB move,
      Vec3 vec3d,
      float f,
      float f2,
      Vec3 vec2,
      Vec3 vec3,
      float floatValue,
      Input keyPresses,
      boolean flag,
      boolean flag2
   ) {
      Vec3[] vecs = new Vec3[]{
         new Vec3(move.minX, move.minY, move.minZ),
         new Vec3(move.minX, move.minY, move.maxZ),
         new Vec3(move.minX, move.maxY, move.minZ),
         new Vec3(move.minX, move.maxY, move.maxZ),
         new Vec3(move.maxX, move.minY, move.minZ),
         new Vec3(move.maxX, move.minY, move.maxZ),
         new Vec3(move.maxX, move.maxY, move.minZ),
         new Vec3(move.maxX, move.maxY, move.maxZ)
      };
      Vec3 vec4 = new Vec3((move.minX + move.maxX) * 0.5, (move.minY + move.maxY) * 0.5, (move.minZ + move.maxZ) * 0.5);
      Rotation rotation2 = this.method4918(eyePosition, vec4);
      Vec2f vec2f = rotation.wrappedDeltaTo(rotation2);
      float floatValue2 = vec2f.getFloatValue();
      float floatValue3 = vec2f.getFloatValue2();
      if (this.isFinite(floatValue2) && this.isFinite(floatValue3)) {
         AABB aabb = move.move(vec3d.subtract(livingEntity.position()));
         Vec2f vec2f2 = rotation.wrappedDeltaTo(this.method4918(vec, this.method4923(aabb)));
         float floatValue4 = vec2f2.getFloatValue();
         float floatValue5 = vec2f2.getFloatValue2();
         if (this.isFinite(floatValue4) && this.isFinite(floatValue5)) {
            float f3;
            float f4 = (float)Math.sin(f3 = (float)Math.toRadians(floatValue4));
            float f5 = (float)Math.cos(f3);
            Vec3 eyePosition2 = vec3d.add(livingEntity.getEyePosition().subtract(livingEntity.position()));
            Rotation rotation3 = new Rotation(livingEntity.getYRot(), livingEntity.getXRot());
            Vec2f vec2f3 = rotation3.wrappedDeltaTo(this.method4918(eyePosition2, eyePosition));
            float floatValue6 = vec2f3.getFloatValue();
            float floatValue7 = vec2f3.getFloatValue2();
            Vec3 vec3d2 = eyePosition.subtract(eyePosition2);
            if (this.isFinite(floatValue6) && this.isFinite(floatValue7) && this.method4977(vec3d2) && !(vec3d2.lengthSqr() <= 1.0E-10)) {
               float f6 = (float)Math.toRadians(floatValue6);
               float f7 = (float)Math.toRadians(floatValue7);
               float f8 = (float)Mth.clamp(rotation3.toDirection().dot(vec3d2.normalize()), -1.0, 1.0);
               float f9 = Float.MAX_VALUE;
               float f10 = -Float.MAX_VALUE;
               float f11 = Float.MAX_VALUE;
               float f12 = -Float.MAX_VALUE;

               for (Vec3 vec5 : vecs) {
                  Rotation rotation4 = this.method4918(eyePosition, vec5);
                  Vec2f vec2f4 = rotation.wrappedDeltaTo(rotation4);
                  float floatValue8 = vec2f4.getFloatValue();
                  float floatValue9 = vec2f4.getFloatValue2();
                  if (this.isFinite(floatValue8) && this.isFinite(floatValue9)) {
                     floatValue8 = method4953(floatValue8, floatValue2);
                     f9 = Math.min(f9, floatValue8);
                     f10 = Math.max(f10, floatValue8);
                     f11 = Math.min(f11, floatValue9);
                     f12 = Math.max(f12, floatValue9);
                  }
               }

               if (f9 == Float.MAX_VALUE) {
                  f9 = floatValue2;
                  f10 = floatValue2;
                  f11 = floatValue3;
                  f12 = floatValue3;
               }

               floatValue2 = method4953(floatValue2, floatValue2);
               float f13 = f10 - f9;
               float f14 = f12 - f11;
               float f15 = (float)(vec4.x - eyePosition.x);
               float f16 = (float)(vec4.y - eyePosition.y);
               float f17 = (float)(vec4.z - eyePosition.z);
               float floatValue8 = (float)eyePosition.distanceTo(vec4);
               float hypot = this.hypot(f15, f17);
               Vec3 vec5 = vec4.add(vec2);
               Vec2f vec2f4 = rotation2.wrappedDeltaTo(this.method4918(eyePosition, vec5));
               float floatValue9 = (float)eyePosition.distanceTo(vec5) - floatValue8;
               Vec3 eyePosition3 = eyePosition.add(vec3);
               Vec2f vec2f5 = rotation2.wrappedDeltaTo(this.method4918(eyePosition3, vec4));
               float floatValue10 = (float)eyePosition3.distanceTo(vec4) - floatValue8;
               if (this.isFinite(vec2f4.getFloatValue())
                  && this.isFinite(vec2f4.getFloatValue2())
                  && this.isFinite(floatValue9)
                  && this.isFinite(vec2f5.getFloatValue())
                  && this.isFinite(vec2f5.getFloatValue2())
                  && this.isFinite(floatValue10)) {
                  int n = f9 <= 0.0F && f10 >= 0.0F ? 1 : 0;
                  int n2 = f11 <= 0.0F && f12 >= 0.0F ? 1 : 0;
                  float f18 = 0.0F;
                  if (n != 0 && Math.abs(f13) > 1.0E-6F) {
                     f18 = Mth.clamp((0.0F - f9) / f13, 0.0F, 1.0F);
                  }

                  float f19 = 0.0F;
                  if (n2 != 0 && Math.abs(f14) > 1.0E-6F) {
                     f19 = Mth.clamp((0.0F - f11) / f14, 0.0F, 1.0F);
                  }

                  int n3 = n != 0 && n2 != 0 ? 1 : 0;
                  this.floatValue7 = n3 != 0 ? Mth.clamp(this.floatValue7 + 1.0F, 0.0F, 50.0F) : 0.0F;
                  this.floatValue8 = n3 != 0 ? 0.0F : Mth.clamp(this.floatValue8 + 1.0F, 0.0F, 50.0F);
                  float f20 = f - this.floatValue3;
                  float f21 = f2 - this.floatValue4;
                  float f22 = f20 - this.floatValue5;
                  float f23 = f21 - this.floatValue6;
                  float f24 = this.entry != null ? this.method4926(this.entry.data24.getData3().getFloatValue(), this.entry.floatValue2) : floatValue2;
                  float f25 = this.entry != null ? this.method4926(this.entry.data24.getData3().getFloatValue2(), this.entry.floatValue2) : floatValue3;
                  float floatValue11 = this.entry != null ? this.entry.data2.getData17().getFloatValue() : floatValue8;
                  float floatValue12 = Mth.wrapDegrees(floatValue2 - f24);
                  float floatValue13 = floatValue3 - f25;
                  float f26 = floatValue8 - floatValue11;
                  float f27 = Mth.clamp(floatValue3 / Math.max(Math.abs(f14), 0.1F), -20.0F, 20.0F);
                  float floatValue14 = (float)Math.sqrt(floatValue2 * floatValue2 + floatValue3 * floatValue3);
                  Vec3 vec6 = vec4.subtract(eyePosition).normalize();
                  Vec3 vec7 = rotation.toDirection().normalize();
                  Vec3 vec8 = new Rotation(rotation.getFloatValue() + 90.0F, 0.0F).toDirection().normalize();
                  Vec3 vec9 = vec8.cross(vec7).normalize();
                  float f28 = (float)vec6.dot(vec8);
                  float f29 = (float)vec6.dot(vec9);
                  float f30 = (float)vec6.dot(vec7);
                  float floatValue15 = this.method4925(f, floatValue);
                  float f31 = this.method4925(f2, floatValue);
                  float f32 = this.method4924(floatValue3, floatValue);
                  float f33 = 0.0F;
                  float f34 = 0.0F;
                  float f35 = 0.0F;
                  float f36 = 0.0F;
                  if (this.entry != null) {
                     float floatValue16 = this.entry.data24.getData3().getFloatValue();
                     float f37 = this.entry.data24.getData3().getFloatValue2();
                     float floatValue17 = floatValue16 - floatValue15;
                     float f38 = f37 - f31;
                     float floatValue18 = Mth.wrapDegrees(this.method4926(floatValue17, floatValue));
                     float f39 = this.method4924(floatValue18, floatValue);
                     f33 = Math.abs(floatValue16) - Math.abs(f39);
                     f34 = Math.abs(f37) - Math.abs(f38);
                     f35 = this.method4924(Mth.wrapDegrees(floatValue2 - floatValue18), floatValue);
                     f36 = f32 - f38;
                  }

                  this.floatValue9 = floatValue15 != 0.0F ? this.floatValue9 + 1.0F : 0.0F;
                  this.floatValue10 = f31 != 0.0F ? this.floatValue10 + 1.0F : 0.0F;
                  int n4 = this.method4927(floatValue15);
                  int n5 = this.method4927(f31);
                  this.floatValue13 = this.method4929(floatValue15, n4, this.intValue2, this.floatValue13);
                  this.floatValue14 = this.method4929(f31, n5, this.intValue3, this.floatValue14);
                  float f37 = this.method4930(floatValue15, this.floatValue13);
                  float f38 = this.method4930(f31, this.floatValue14);
                  this.floatValue11 = this.method4928(n4, this.intValue2, this.floatValue11);
                  this.floatValue12 = this.method4928(n5, this.intValue3, this.floatValue12);
                  this.intValue2 = n4 != 0 ? n4 : this.intValue2;
                  this.intValue3 = n5 != 0 ? n5 : this.intValue3;
                  float floatValue16 = keyPresses.forward() ? 1.0F : 0.0F;
                  float f39 = keyPresses.backward() ? 1.0F : 0.0F;
                  float f40 = keyPresses.left() ? 1.0F : 0.0F;
                  float f41 = keyPresses.right() ? 1.0F : 0.0F;
                  float f42 = keyPresses.jump() ? 1.0F : 0.0F;
                  RotationRecorderModule.Data2 data2 = new RotationRecorderModule.Data2(
                     new RotationRecorderModule.Data6(
                        this.method4924(f9 - floatValue2, floatValue),
                        this.method4924(f10 - floatValue2, floatValue),
                        this.method4924(f11 - floatValue3, floatValue),
                        this.method4924(f12 - floatValue3, floatValue),
                        f27,
                        this.method4924(floatValue14, floatValue),
                        f28,
                        f29,
                        f30,
                        f18,
                        f19,
                        (n3 != 0)
                     ),
                     new RotationRecorderModule.Data17(floatValue8, hypot, f16, this.floatValue7, this.floatValue8),
                     new RotationRecorderModule.Data7(
                        this.method4924(floatValue12, floatValue),
                        this.method4924(floatValue13, floatValue),
                        f26,
                        f35,
                        f36,
                        f4,
                        f5,
                        this.method4924(floatValue5, floatValue),
                        new RotationRecorderModule.Data5(this.method4924(vec2f4.getFloatValue(), floatValue), this.method4924(vec2f4.getFloatValue2(), floatValue), floatValue9)
                     ),
                     new RotationRecorderModule.Data12((float)Math.sin(f6), (float)Math.cos(f6), (float)Math.sin(f7), (float)Math.cos(f7), f8),
                     new RotationRecorderModule.Data13(
                        floatValue15,
                        f31,
                        this.method4925(f20, floatValue),
                        this.method4925(f21, floatValue),
                        this.method4925(f22, floatValue),
                        this.method4925(f23, floatValue),
                        this.floatValue9,
                        this.floatValue10,
                        this.floatValue11,
                        this.floatValue12,
                        f33,
                        f34,
                        f37,
                        f38,
                        this.floatValue13,
                        this.floatValue14
                     ),
                     new RotationRecorderModule.Data16(floatValue16, f39, f40, f41, f42),
                     new RotationRecorderModule.Data5(this.method4924(vec2f5.getFloatValue(), floatValue), this.method4924(vec2f5.getFloatValue2(), floatValue), floatValue10),
                     new RotationRecorderModule.Data22(
                        mc.player.getAttackStrengthScale(0.0F), Mth.clamp((float)mc.player.attackStrengthTicker, 0.0F, 50.0F), flag, flag2
                     )
                  );
                  if (!data2.method4982()) {
                     return null;
                  } else {
                     this.floatValue3 = f;
                     this.floatValue4 = f2;
                     this.floatValue5 = f20;
                     this.floatValue6 = f21;
                     return data2;
                  }
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Rotation method4918(Vec3 eyePosition, Vec3 vec) {
      return RotationUtils.method6912(vec.subtract(eyePosition));
   }

   private Vec3 method4919(Rotation rotation, Vec3 eyePosition, Input keyPresses) {
      Vec3 position = mc.player.position();
      Vec3 vec = eyePosition.subtract(position);

      Vec3 vec2;
      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(keyPresses));
         playerSimulation.floatValue = rotation.getFloatValue();
         playerSimulation.floatValue2 = rotation.getFloatValue2();
         playerSimulation.tick();
         Vec3 vec3d = playerSimulation.vec3d.add(vec);
         vec2 = this.method4977(vec3d) ? vec3d : eyePosition;
      } catch (Throwable e) {
         return eyePosition;
      }

      return vec2;
   }

   private Vec3 method4920(LivingEntity livingEntity) {
      if (livingEntity != null && livingEntity.getPositionCodec() != null) {
         Vec3 base = livingEntity.getPositionCodec().getBase();
         return this.method4977(base) ? base : null;
      } else {
         return null;
      }
   }

   private Vec3 method4921(Vec3 position) {
      if (this.entry != null && position != null) {
         Vec3 vec3d = position.subtract(this.entry.vec3d);
         return this.method4977(vec3d) ? vec3d : Vec3.ZERO;
      } else {
         return Vec3.ZERO;
      }
   }

   private Vec3 method4922(AABB move) {
      if (this.entry != null && move != null) {
         RotationRecorderModule.Data20 data20 = this.entry.data24.getData3().getData20();
         Vec3 vec = this.method4923(move);
         Vec3 vec3d = vec.subtract(new Vec3(data20.getFloatValue(), data20.getFloatValue2(), data20.getFloatValue3()));
         return this.method4977(vec3d) ? vec3d : Vec3.ZERO;
      } else {
         return Vec3.ZERO;
      }
   }

   private Vec3 method4923(AABB move) {
      return new Vec3((move.minX + move.maxX) * 0.5, (move.minY + move.maxY) * 0.5, (move.minZ + move.maxZ) * 0.5);
   }

   private float hypot(float f, float f2) {
      return (float)Math.sqrt(f * f + f2 * f2);
   }

   private float method4924(float floatValue, float f) {
      return floatValue / f;
   }

   private float method4925(float f, float floatValue) {
      return Math.round(f / floatValue);
   }

   private float method4926(float floatValue, float f) {
      return this.isFinite(f) && f > 0.0F ? floatValue * f : floatValue;
   }

   private int method4927(float f) {
      if (f > 0.0F) {
         return 1;
      } else {
         return f < 0.0F ? -1 : 0;
      }
   }

   private float method4928(int n, int n2, float f) {
      return n != 0 && n2 != 0 && n != n2 ? 0.0F : Math.min(f + 1.0F, 50.0F);
   }

   private float method4929(float f, int n, int n2, float f2) {
      float f3 = Math.abs(f);
      if (n == 0) {
         return 0.0F;
      } else {
         return n2 != 0 && n == n2 ? Math.max(f2, f3) : f3;
      }
   }

   private float method4930(float f, float f2) {
      return f2 > 0.0F ? Math.abs(f) / f2 : 0.0F;
   }

   private AABB method4931(LivingEntity livingEntity) {
      return livingEntity == null ? null : livingEntity.getBoundingBox();
   }

   private void method4932(LivingEntity livingEntity, RotationRecorderModule.Entry9 entry9, int tickCount) {
      var entries = new ArrayList<>(entry9.deque);
      int n;
      if ((n = entries.size() - 20 - 1) >= 0) {
         for (int i = n; i < entries.size() - 1; i++) {
            if (((RotationRecorderModule.Entry)entries.get(i + 1)).intValue2 != ((RotationRecorderModule.Entry)entries.get(i)).intValue2 + 1) {
               return;
            }
         }

         this.entry4 = new RotationRecorderModule.Entry4();

         for (int i = n; i < entries.size() - 1; i++) {
            if (!this.entry4.method4999(this.method4933((RotationRecorderModule.Entry)entries.get(i), (RotationRecorderModule.Entry)entries.get(i + 1)))) {
               this.method4937();
               return;
            }
         }

         RotationRecorderModule.Entry entry = (RotationRecorderModule.Entry)entries.get(entries.size() - 1);
         this.entry7 = new RotationRecorderModule.Entry7(livingEntity.getId(), livingEntity.getUUID(), entry.intValue2, tickCount, tickCount + 40);
      }
   }

   private RotationRecorderModule.Entry5 method4933(RotationRecorderModule.Entry entry, RotationRecorderModule.Entry entry2) {
      RotationRecorderModule.Entry3 entry3 = this.method4974(entry, entry, this.method4972(entry), this.method4973(entry));
      return RotationRecorderModule.Entry5.method5006(entry, entry2, entry3, false);
   }

   private void method4934(RotationRecorderModule.Entry entry, RotationRecorderModule.Entry entry2) {
      if (this.entry7 != null && this.entry4 != null) {
         if (entry.intValue2 >= this.entry7.intValue2) {
            if (entry.intValue2 == this.entry7.intValue2 && entry2.intValue2 == this.entry7.intValue2 + 1) {
               if (!this.entry4.method4999(this.method4933(entry, entry2))) {
                  this.method4937();
               } else {
                  this.entry7.intValue2 = entry2.intValue2;
               }
            } else {
               this.method4937();
            }
         }
      }
   }

   private void method4935() {
      if (this.entry7 != null && this.entry4 != null && this.entry7.intValue2 >= this.entry7.intValue4) {
         int n = Math.max(0, this.entry7.intValue2 - this.entry7.intValue3);
         this.entry4.method5000(n);
         this.entry7 = null;
      } else {
         this.method4937();
      }
   }

   private void method4936() {
      if (this.entry7 != null && this.entry4 != null) {
         int n = Math.max(0, this.entry7.intValue2 - this.entry7.intValue3);
         this.entry4.method5000(n);
         this.entry7 = null;
      } else {
         this.method4937();
      }
   }

   private void method4937() {
      if (this.entry4 != null) {
         this.entry4.method5001();
      }

      this.entry7 = null;
   }

   private void method4938(RotationRecorderModule.Entry9 entry9) {
      this.floatValue3 = entry9.floatValue;
      this.floatValue4 = entry9.floatValue2;
      this.floatValue5 = entry9.floatValue3;
      this.floatValue6 = entry9.floatValue4;
      this.floatValue7 = entry9.floatValue5;
      this.floatValue8 = entry9.floatValue6;
      this.floatValue9 = entry9.floatValue7;
      this.floatValue10 = entry9.floatValue8;
      this.intValue2 = entry9.intValue2;
      this.intValue3 = entry9.intValue3;
      this.floatValue11 = entry9.floatValue9;
      this.floatValue12 = entry9.floatValue10;
      this.floatValue13 = entry9.floatValue11;
      this.floatValue14 = entry9.floatValue12;
      this.entry = entry9.deque.peekLast();
   }

   private void method4939(RotationRecorderModule.Entry9 entry9) {
      entry9.floatValue = this.floatValue3;
      entry9.floatValue2 = this.floatValue4;
      entry9.floatValue3 = this.floatValue5;
      entry9.floatValue4 = this.floatValue6;
      entry9.floatValue5 = this.floatValue7;
      entry9.floatValue6 = this.floatValue8;
      entry9.floatValue7 = this.floatValue9;
      entry9.floatValue8 = this.floatValue10;
      entry9.intValue2 = this.intValue2;
      entry9.intValue3 = this.intValue3;
      entry9.floatValue9 = this.floatValue11;
      entry9.floatValue10 = this.floatValue12;
      entry9.floatValue11 = this.floatValue13;
      entry9.floatValue12 = this.floatValue14;
   }

   private void method4940() {
      this.flag4 = false;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.floatValue11 = 50.0F;
      this.floatValue12 = 50.0F;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.entry = null;
      this.entry4 = null;
      this.entry7 = null;
      this.items.clear();
      this.flag5 = false;
   }

   private void method4941() {
      this.flag = false;
      this.longValue = 0L;
      this.intValue = 0;

      try {
         Path path = this.method4943();
         Files.createDirectories(path.getParent());
         int n = (!Files.exists(path) || Files.size(path) == 0L) && Zenith.getInstance().getEntry().getUsername().equals("Bogdan") ? 1 : 0;
         this.bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         this.method4952((n != 0));
      } catch (IOException e) {
         this.bufferedWriter = null;
         this.flag = true;
         System.err.println("[RotationRecorder] Failed to open dataset writer: " + e.getMessage());
      }
   }

   private void method4942() {
      Path absolutePath = path.getParent().toAbsolutePath();

      try {
         Files.createDirectories(absolutePath);
         new ProcessBuilder("explorer.exe", absolutePath.toString()).start();
      } catch (IOException e) {
         System.err.println("[RotationRecorder] Failed to open recordings folder: " + e.getMessage());
      }
   }

   private Path method4943() {
      return this.flag3 ? path2 : path;
   }

   private void method4944() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:166)
      //
      // Bytecode:
      // 00: nop
      // 01: aload 0
      // 02: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 05: ifnonnull 09
      // 08: return
      // 09: nop
      // 0a: aload 0
      // 0b: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 0e: invokevirtual java/io/BufferedWriter.flush ()V
      // 11: aload 0
      // 12: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 15: invokevirtual java/io/BufferedWriter.close ()V
      // 18: aload 0
      // 19: aconst_null
      // 1a: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 1d: goto 33
      // 20: nop
      // 21: astore 1
      // 22: aload 0
      // 23: aconst_null
      // 24: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 27: goto 33
      // 2a: nop
      // 2b: astore 2
      // 2c: aload 0
      // 2d: aconst_null
      // 2e: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 31: aload 2
      // 32: athrow
      // 33: nop
      // 34: return
   }

   private void method4945() {
      this.flag2 = false;
      this.longValue2 = 0L;

      try {
         Files.createDirectories(path3.getParent());
         this.method4946();
         this.bufferedWriter2 = Files.newBufferedWriter(path3, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      } catch (IOException e) {
         this.bufferedWriter2 = null;
         this.flag2 = true;
         System.err.println("[RotationRecorder] Failed to open snapshot writer: " + e.getMessage());
      }
   }

   private void method4946() {
      this.longValue3 = 1L;
      if (Files.exists(path3)) {
         long l;
         if ((l = this.method4950()) > 0L) {
            this.longValue3 = l;
         } else {
            long l2 = 0L;

            try (BufferedReader bufferedReader = Files.newBufferedReader(path3, StandardCharsets.UTF_8)) {
               String line;
               while ((line = bufferedReader.readLine()) != null) {
                  long l3;
                  if ((l3 = this.method4947(line)) > l2) {
                     l2 = l3;
                  }
               }

               this.longValue3 = l2 + 1L;
            } catch (IOException e) {
               System.err.println("[RotationRecorder] Failed to scan existing snapshots; starting from snapshot_id=1: " + e.getMessage());
               this.longValue3 = 1L;
            }
         }
      }
   }

   private long method4947(String line) {
      int indexOf;
      if ((indexOf = line.indexOf("\"snapshot_id\"")) < 0) {
         return -1L;
      } else {
         int n;
         if ((n = line.indexOf(58, indexOf)) < 0) {
            return -1L;
         } else {
            int index = n + 1;

            while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
               index++;
            }

            int beginIndex = index;
            if (index < line.length() && line.charAt(index) == '-') {
               index++;
            }

            while (index < line.length() && Character.isDigit(line.charAt(index))) {
               index++;
            }

            if (index <= beginIndex) {
               return -1L;
            } else {
               try {
                  return Long.parseLong(line.substring(beginIndex, index));
               } catch (NumberFormatException e) {
                  return -1L;
               }
            }
         }
      }
   }

   private void method4948() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:166)
      //
      // Bytecode:
      // 00: nop
      // 01: aload 0
      // 02: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 05: ifnonnull 09
      // 08: return
      // 09: nop
      // 0a: aload 0
      // 0b: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 0e: invokevirtual java/io/BufferedWriter.flush ()V
      // 11: aload 0
      // 12: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 15: invokevirtual java/io/BufferedWriter.close ()V
      // 18: aload 0
      // 19: aconst_null
      // 1a: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 1d: goto 33
      // 20: nop
      // 21: astore 1
      // 22: aload 0
      // 23: aconst_null
      // 24: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 27: goto 33
      // 2a: nop
      // 2b: astore 2
      // 2c: aload 0
      // 2d: aconst_null
      // 2e: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 31: aload 2
      // 32: athrow
      // 33: nop
      // 34: aload 0
      // 35: invokevirtual org/zenith/client/modules/impl/combat/RotationRecorderModule.method4949 ()V
      // 38: return
   }

   private void method4949() {
      try {
         if (!Files.exists(path3)) {
            return;
         }

         String csq = this.longValue3 + " " + Files.size(path3) + " " + Files.getLastModifiedTime(path3).toMillis();
         Files.writeString(path4, csq, StandardCharsets.UTF_8);
      } catch (IOException e) {
         try {
            Files.deleteIfExists(path4);
         } catch (IOException ex) {
         }
      }
   }

   private long method4950() {
      try {
         if (!Files.exists(path4)) {
            return -1L;
         } else {
            String[] strings = Files.readString(path4, StandardCharsets.UTF_8).trim().split("\\s+");
            if (strings.length != 3) {
               return -1L;
            } else {
               long l = Long.parseLong(strings[0]);
               long l2 = Long.parseLong(strings[1]);
               long l3 = Long.parseLong(strings[2]);
               return l >= 1L && l2 == Files.size(path3) && l3 == Files.getLastModifiedTime(path3).toMillis() ? l : -1L;
            }
         }
      } catch (RuntimeException | IOException e) {
         return -1L;
      }
   }

   private void method4951(long longValue, RotationRecorderModule.Entry5 entry5) {
      if (this.bufferedWriter != null && !this.flag) {
         if (entry5 != null && entry5.isValid()) {
            try {
               StringBuilder stringBuilder = new StringBuilder(16384);
               stringBuilder.append(longValue).append(',');
               stringBuilder.append(entry5.longValue).append(',');
               stringBuilder.append(entry5.intValue);
               stringBuilder.append(',').append(entry5.floatValue);
               if (this.flag3) {
                  entry5.method5008(stringBuilder);
               } else {
                  entry5.method5007(stringBuilder);
               }

               this.bufferedWriter.write(stringBuilder.toString());
               this.bufferedWriter.newLine();
               this.longValue++;
               this.intValue++;
               if (this.longValue % 128L == 0L) {
                  this.bufferedWriter.flush();
               }
            } catch (IllegalStateException | IOException e) {
               this.flag = true;
               System.err.println("[RotationRecorder] Failed to write dataset row: " + e.getMessage());
            }
         }
      }
   }

   private void method4952(boolean flag) throws IOException {
      if (flag && this.bufferedWriter != null) {
         StringBuilder stringBuilder = new StringBuilder(16384);
         stringBuilder.append("session_id,timestamp_ms,player_age,mouse_degrees_per_count");
         if (this.flag3) {
            RotationRecorderModule.Entry5.method5003(new RotationRecorderModule.Entry8(stringBuilder));
         } else {
            RotationRecorderModule.Entry5.method5002(new RotationRecorderModule.Entry8(stringBuilder));
         }

         this.bufferedWriter.write(stringBuilder.toString());
         this.bufferedWriter.newLine();
         this.bufferedWriter.flush();
      }
   }

   private static float method4953(float f, float f2) {
      float angle;
      return !Float.isFinite(angle = f - f2) ? f : f2 + Mth.wrapDegrees(angle);
   }

   private boolean isFinite(float floatValue) {
      return (!Float.isNaN(floatValue) && !Float.isInfinite(floatValue));
   }

   private long method4954() {
      this.longValue4 = Math.max(this.longValue4 + 1L, Math.max(1L, Instant.now().toEpochMilli()));
      return this.longValue4;
   }

   private RotationRecorderModule.Entry method4955(
      Rotation rotation,
      Vec3 eyePosition,
      LivingEntity livingEntity,
      AABB move,
      Vec3 vec3d,
      float f,
      float f2,
      Input keyPresses,
      boolean flag,
      boolean flag2,
      RotationRecorderModule.Entry10 entry10,
      RotationRecorderModule.Entry3 entry3,
      Vec3 vec
   ) {
      Vec3 position = mc.player.position();
      Vec3 vec3d2 = this.method4922(move);
      Vec3 vec2 = this.method4921(position);
      float floatValue = Rotation.sensitivityStep();
      if (this.isFinite(floatValue) && !(floatValue <= 0.0F)) {
         boolean flag3 = mc.player.onGround();
         RotationRecorderModule.Data2 data2 = this.method4917(rotation, eyePosition, vec, livingEntity, move, vec3d, f, f2, vec3d2, vec2, floatValue, keyPresses, flag3, flag);
         if (data2 != null && data2.method4982()) {
            float floatValue2 = rotation.getFloatValue();
            float f3 = rotation.getFloatValue2();
            Vec3 eyePosition2 = mc.player.getEyePosition();
            Vec3 deltaMovement = mc.player.getDeltaMovement();
            AABB boundingBox = mc.player.getBoundingBox();
            Vec3 vec3d3 = this.entry == null ? position : this.entry.vec3d;
            Vec3 vec3d4 = this.method4923(move);
            float floatValue3 = this.method4924(rotation.wrappedDeltaTo(this.method4918(eyePosition, vec3d4)).getFloatValue(), floatValue);
            float f4 = this.method4924(rotation.wrappedDeltaTo(this.method4918(eyePosition, vec3d4)).getFloatValue2(), floatValue);
            boolean flag4 = this.method4971();
            RotationRecorderModule.Data10 data10 = this.method4956(mc.player.isSprinting(), boundingBox, entry10);
            if (!data10.method4982()) {
               return null;
            } else {
               RotationRecorderModule.Data24 data24 = new RotationRecorderModule.Data24(
                  new RotationRecorderModule.Data19(
                     RotationRecorderModule.Data20.method5034(position),
                     RotationRecorderModule.Data20.method5034(eyePosition2),
                     floatValue2,
                     f3,
                     RotationRecorderModule.Data20.method5034(deltaMovement),
                     RotationRecorderModule.Data20.method5034(vec3d3),
                     RotationRecorderModule.Data9.method5011(boundingBox)
                  ),
                  RotationRecorderModule.Data21.method5036(keyPresses),
                  new RotationRecorderModule.Data4(
                     mc.player.horizontalCollision,
                     mc.player.verticalCollision,
                     mc.player.isInWater(),
                     mc.player.isSwimming(),
                     mc.player.isUnderWater(),
                     mc.player.isFallFlying(),
                     mc.player.isCrouching(),
                     mc.player.isVisuallyCrawling(),
                     mc.player.getPose() == null ? -1.0F : mc.player.getPose().ordinal(),
                     (float)mc.player.fallDistance,
                     (float)mc.player.noJumpDelay,
                     mc.player.jumping,
                     mc.player.isPassenger(),
                     mc.player.getAbilities().flying,
                     mc.player.isSpectator()
                  ),
                  new RotationRecorderModule.Data3(
                     RotationRecorderModule.Data9.method5011(move),
                     RotationRecorderModule.Data20.method5034(vec3d4),
                     RotationRecorderModule.Data20.method5034(vec3d),
                     RotationRecorderModule.Data20.method5034(vec3d2),
                     floatValue3,
                     f4
                  ),
                  RotationRecorderModule.Data.method4980(entry3)
               );
               return !data24.method4982()
                  ? null
                  : new RotationRecorderModule.Entry(
                     data2,
                     data24,
                     data10,
                     position,
                     deltaMovement,
                     boundingBox,
                     keyPresses,
                     flag3,
                     flag4,
                     mc.player.isSprinting(),
                     (float)mc.player.fallDistance,
                     mc.player.noJumpDelay,
                     mc.player.jumping,
                     mc.player.isFallFlying(),
                     mc.player.horizontalCollision,
                     mc.player.verticalCollision,
                     mc.player.isInWater(),
                     mc.player.isSwimming(),
                     mc.player.isUnderWater(),
                     mc.player.getPose(),
                     mc.player.isCrouching(),
                     mc.player.isVisuallyCrawling(),
                     flag2,
                     floatValue,
                     System.currentTimeMillis(),
                     mc.player.tickCount
                  );
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private RotationRecorderModule.Data10 method4956(boolean sprinting, AABB boundingBox, RotationRecorderModule.Entry10 entry10) {
      MobEffectInstance mobEffectInstance = this.method4966(MobEffects.LEVITATION);
      MobEffectInstance mobEffectInstance2 = this.method4966(MobEffects.JUMP_BOOST);
      boolean flag = this.method4967(MobEffects.SLOW_FALLING);
      boolean flag2 = this.method4967(MobEffects.BLINDNESS);
      boolean flag3 = this.method4967(MobEffects.DOLPHINS_GRACE);
      int intValue = entry10 == null ? (boundingBox == null ? 0 : Mth.floor(boundingBox.minX)) : entry10.intValue;
      int n = entry10 == null ? (boundingBox == null ? 0 : Mth.floor(boundingBox.minY)) : entry10.intValue2;
      int n2 = entry10 == null ? (boundingBox == null ? 0 : Mth.floor(boundingBox.minZ)) : entry10.intValue3;
      int n3 = entry10 == null ? 0 : entry10.intValue4;
      int n4 = entry10 == null ? 0 : entry10.intValue5;
      int n5 = entry10 == null ? 0 : entry10.intValue6;
      long longValue = entry10 == null ? -1L : entry10.longValue;
      int n6 = entry10 == null ? 0 : entry10.intValue7;
      int n7 = entry10 != null && longValue >= 0L ? 1 : 0;
      return new RotationRecorderModule.Data10(
         new RotationRecorderModule.Data14(30.0F, false, 2.0F, (float)n7),
         new RotationRecorderModule.Data8(
            sprinting,
            mc.player.getAbilities().mayfly,
            mc.player.isNoGravity(),
            mc.player.shouldDiscardFriction(),
            mc.player.isUsingItem(),
            this.method4969(),
            mc.player.maxUpStep(),
            this.method4968(Attributes.MOVEMENT_SPEED, 0.1F),
            this.method4968(Attributes.GRAVITY, 0.08F),
            this.method4968(Attributes.JUMP_STRENGTH, 0.42F),
            this.method4968(Attributes.SNEAKING_SPEED, 0.3F),
            this.method4968(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0F)
         ),
         new RotationRecorderModule.Data18(
            flag, mobEffectInstance != null, mobEffectInstance2 != null, flag2, flag3, mobEffectInstance == null ? -1.0F : mobEffectInstance.getAmplifier(), mobEffectInstance2 == null ? -1.0F : mobEffectInstance2.getAmplifier()
         ),
         new RotationRecorderModule.Data15((float)longValue, (float)intValue, (float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6)
      );
   }

   private RotationRecorderModule.Entry10 method4957(AABB boundingBox) {
      if (boundingBox != null && this.bufferedWriter2 != null && !this.flag2) {
         int intValue = Mth.floor(boundingBox.minX) - 6;
         int n = Mth.floor(boundingBox.minY) - 3;
         int n2 = Mth.floor(boundingBox.minZ) - 6;
         int n3 = Mth.floor(boundingBox.maxX) + 6;
         int n4 = Mth.floor(boundingBox.maxY) + 5;
         int n5 = Mth.floor(boundingBox.maxZ) + 6;
         int intValue2 = n3 - intValue + 1;
         int n6 = n4 - n + 1;
         int n7 = n5 - n2 + 1;
         if (intValue2 > 0 && n6 > 0 && n7 > 0) {
            long lng = this.longValue3++;
            IdentityHashMap map = new IdentityHashMap();
            HashMap map2 = null;
            ArrayList arrayList = new ArrayList();
            int[] ints = new int[intValue2 * n6 * n7];
            int n8 = 0;
            arrayList.add(this.method4959());
            MutableBlockPos mutablePos = new MutableBlockPos();

            for (int i = n; i <= n4; i++) {
               for (int j = n2; j <= n5; j++) {
                  for (int k = intValue; k <= n3; k++) {
                     mutablePos.set(k, i, j);
                     BlockState blockState = mc.level.getBlockState(mutablePos);
                     FluidState fluidState = mc.level.getFluidState(mutablePos);
                     Integer size;
                     if (blockState != null && !blockState.isAir() || fluidState != null && !fluidState.isEmpty()) {
                        if (blockState != null && fluidState == blockState.getFluidState()) {
                           size = (Integer)map.get(blockState);
                           if (size == null) {
                              size = arrayList.size();
                              map.put(blockState, size);
                              arrayList.add(this.method4960(blockState, fluidState, mutablePos));
                           }
                        } else {
                           if (map2 == null) {
                              map2 = new HashMap();
                           }

                           String key = this.method4958(blockState, fluidState);
                           size = (Integer)map2.get(key);
                           if (size == null) {
                              size = arrayList.size();
                              map2.put(key, size);
                              arrayList.add(this.method4960(blockState, fluidState, mutablePos));
                           }
                        }
                     } else {
                        size = 0;
                     }

                     ints[n8++] = size;
                  }
               }
            }

            try {
               StringBuilder builder = new StringBuilder(32768);
               builder.append('{');
               builder.append("\"snapshot_id\":").append(lng).append(',');
               builder.append("\"snapshot_version\":").append(1).append(',');
               builder.append("\"schema\":\"rotation_world_snapshot_v7\",");
               builder.append("\"session_hint\":").append(this.entry4 == null ? 0L : this.entry4.longValue).append(',');
               builder.append("\"player_age\":").append(mc.player.tickCount).append(',');
               this.method4964(builder, "origin", intValue, n, n2).append(',');
               this.method4964(builder, "size", intValue2, n6, n7).append(',');
               builder.append("\"palette\":[");

               for (int i = 0; i < arrayList.size(); i++) {
                  if (i > 0) {
                     builder.append(',');
                  }

                  builder.append((String)arrayList.get(i));
               }

               builder.append("],\"blocks_rle\":");
               this.method4965(builder, ints, n8);
               builder.append('}');
               this.bufferedWriter2.write(builder.toString());
               this.bufferedWriter2.newLine();
               this.longValue2++;
               if (this.longValue2 % 128L == 0L) {
                  this.bufferedWriter2.flush();
               }
            } catch (Throwable e) {
               this.flag2 = true;
               return null;
            }

            return new RotationRecorderModule.Entry10(lng, intValue, n, n2, intValue2, n6, n7, 1);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private String method4958(BlockState blockState, FluidState fluidState) {
      return blockState != null && !blockState.isAir() || fluidState != null && !fluidState.isEmpty() ? blockState + "|" + fluidState : "minecraft:air|empty";
   }

   private String method4959() {
      return "{\"name\":\"minecraft:air\",\"state\":\"minecraft:air\",\"slipperiness\":0.6,\"velocity_multiplier\":1.0,\"jump_velocity_multiplier\":1.0,\"fluid_type\":\"none\",\"fluid_height\":0.0,\"fluid_velocity\":[0.0,0.0,0.0],\"climbable\":false,\"powder_snow\":false,\"collision_boxes\":[]}";
   }

   private String method4960(BlockState blockState, FluidState fluidState, BlockPos mutablePos) {
      String string = blockState == null ? "minecraft:air" : String.valueOf(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
      String str = blockState == null ? string : blockState.toString();
      float friction = blockState == null ? 0.6F : blockState.getBlock().getFriction();
      float speedFactor = blockState == null ? 1.0F : blockState.getBlock().getSpeedFactor();
      float jumpFactor = blockState == null ? 1.0F : blockState.getBlock().getJumpFactor();
      int n = blockState != null && blockState.is(BlockTags.CLIMBABLE) ? 1 : 0;
      boolean flag = string.equals("minecraft:powder_snow");
      String string2 = "none";
      float height = 0.0F;
      Vec3 flow = Vec3.ZERO;
      if (fluidState != null && !fluidState.isEmpty()) {
         if (fluidState.is(FluidTags.WATER)) {
            string2 = "water";
         } else if (fluidState.is(FluidTags.LAVA)) {
            string2 = "lava";
         } else {
            string2 = "other";
         }

         height = fluidState.getHeight(mc.level, mutablePos);
         flow = fluidState.getFlow(mc.level, mutablePos);
      }

      StringBuilder builder = new StringBuilder(1024);
      builder.append('{');
      this.method4962(builder, "name", string).append(',');
      this.method4962(builder, "state", str).append(',');
      builder.append("\"slipperiness\":").append(friction).append(',');
      builder.append("\"velocity_multiplier\":").append(speedFactor).append(',');
      builder.append("\"jump_velocity_multiplier\":").append(jumpFactor).append(',');
      this.method4962(builder, "fluid_type", string2).append(',');
      builder.append("\"fluid_height\":").append(height).append(',');
      builder.append("\"fluid_velocity\":[").append((float)flow.x).append(',').append((float)flow.y).append(',').append((float)flow.z).append("],");
      builder.append("\"climbable\":").append((n != 0)).append(',');
      builder.append("\"powder_snow\":").append(flag).append(',');
      builder.append("\"collision_boxes\":");
      this.method4961(builder, blockState, mutablePos);
      builder.append('}');
      return builder.toString();
   }

   private void method4961(StringBuilder builder, BlockState blockState, BlockPos mutablePos) {
      builder.append('[');
      if (blockState != null) {
         try {
            VoxelShape collisionShape = blockState.getCollisionShape(mc.level, mutablePos);
            var aabbs = collisionShape.toAabbs();

            for (int i = 0; i < aabbs.size(); i++) {
               if (i > 0) {
                  builder.append(',');
               }

               AABB aabb = (AABB)aabbs.get(i);
               builder.append('[')
                  .append((float)aabb.minX)
                  .append(',')
                  .append((float)aabb.minY)
                  .append(',')
                  .append((float)aabb.minZ)
                  .append(',')
                  .append((float)aabb.maxX)
                  .append(',')
                  .append((float)aabb.maxY)
                  .append(',')
                  .append((float)aabb.maxZ)
                  .append(']');
            }
         } catch (Throwable e) {
         }
      }

      builder.append(']');
   }

   private StringBuilder method4962(StringBuilder builder, String str, String string) {
      builder.append('"').append(str).append("\":\"").append(this.method4963(string)).append('"');
      return builder;
   }

   private String method4963(String string) {
      if (string == null) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(string.length() + 16);

         for (int i = 0; i < string.length(); i++) {
            char charAt;
            if ((charAt = string.charAt(i)) == '\\' || charAt == '"') {
               builder.append('\\').append(charAt);
            } else if (charAt == '\n') {
               builder.append("\\n");
            } else if (charAt == '\r') {
               builder.append("\\r");
            } else if (charAt == '\t') {
               builder.append("\\t");
            } else {
               builder.append(charAt);
            }
         }

         return builder.toString();
      }
   }

   private StringBuilder method4964(StringBuilder builder, String str, int intValue, int n, int n2) {
      builder.append('"').append(str).append("\":[").append(intValue).append(',').append(n).append(',').append(n2).append(']');
      return builder;
   }

   private void method4965(StringBuilder builder, int[] ints, int n) {
      builder.append('[');
      if (n > 0) {
         int n2 = ints[0];
         int n3 = 1;
         byte b = 1;

         for (int i = 1; i < n; i++) {
            int n4;
            if ((n4 = ints[i]) == n2) {
               n3++;
            } else {
               if (b == 0) {
                  builder.append(',');
               }

               builder.append('[').append(n2).append(',').append(n3).append(']');
               b = 0;
               n2 = n4;
               n3 = 1;
            }
         }

         if (b == 0) {
            builder.append(',');
         }

         builder.append('[').append(n2).append(',').append(n3).append(']');
      }

      builder.append(']');
   }

   private MobEffectInstance method4966(Holder<MobEffect> effect) {
      if (effect == null) {
         return null;
      } else {
         MobEffectInstance mobEffectInstance;
         try {
            MobEffectInstance effect2 = mc.player.getEffect(effect);
            mobEffectInstance = effect2 != null && effect2.getDuration() > 0 ? effect2 : null;
         } catch (Throwable e) {
            return null;
         }

         return mobEffectInstance;
      }
   }

   private boolean method4967(Holder<MobEffect> effect) {
      return (this.method4966(effect) != null);
   }

   private float method4968(Holder<Attribute> attribute, float f) {
      if (attribute == null) {
         return f;
      } else {
         float f2;
         try {
            double d;
            f2 = Double.isFinite(d = mc.player.getAttributes().getValue(attribute)) ? (float)d : f;
         } catch (Throwable e) {
            return f;
         }

         return f2;
      }
   }

   private float method4969() {
      return mc.player.getFoodData() == null ? 20.0F : mc.player.getFoodData().getFoodLevel();
   }

   private Input method4970() {
      return new Input(
         mc.options.keyUp.isDown(),
         mc.options.keyDown.isDown(),
         mc.options.keyLeft.isDown(),
         mc.options.keyRight.isDown(),
         mc.options.keyJump.isDown(),
         mc.options.keyShift.isDown(),
         mc.options.keySprint.isDown()
      );
   }

   private boolean method4971() {
      return !(!mc.player.isPassenger()
            && !mc.player.getAbilities().flying
            && !mc.player.isSpectator()
            && !mc.player.isInWater()
            && !mc.player.isSwimming()
            && !mc.player.isUnderWater()
            && !mc.player.isFallFlying());
   }

   private float method4972(RotationRecorderModule.Entry entry) {
      return entry.data24.getData19().getFloatValue();
   }

   private float method4973(RotationRecorderModule.Entry entry) {
      return entry.data24.getData19().getFloatValue2();
   }

   private RotationRecorderModule.Entry3 method4974(RotationRecorderModule.Entry entry, RotationRecorderModule.Entry entry2, float floatValue, float f) {
      return this.method4976(entry, entry2, floatValue, f);
   }

   private RotationRecorderModule.Entry3 method4975(float floatValue, float xRot, Input keyPresses) {
      Vec3 position = mc.player.position();
      Vec3 deltaMovement = mc.player.getDeltaMovement();

      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(keyPresses));
         playerSimulation.floatValue = floatValue;
         playerSimulation.floatValue2 = xRot;
         playerSimulation.tick();
         Vec3 vec3d = playerSimulation.vec3d.subtract(position);
         return this.method4977(vec3d) && this.method4977(playerSimulation.vec3d) && this.method4977(playerSimulation.vec3d2)
            ? new RotationRecorderModule.Entry3(true, vec3d, playerSimulation.vec3d, playerSimulation.vec3d2, playerSimulation.flag4, playerSimulation.flag5, playerSimulation.flag6)
            : RotationRecorderModule.Entry3.method4998(position, deltaMovement);
      } catch (Throwable e) {
         return RotationRecorderModule.Entry3.method4998(position, deltaMovement);
      }
   }

   private RotationRecorderModule.Entry3 method4976(RotationRecorderModule.Entry entry, RotationRecorderModule.Entry entry2, float floatValue, float f) {
      try {
         PlayerSimulation playerSimulation = PlayerSimulation.method6033(new PlayerSimulation.Entry(entry2.playerInput));
         playerSimulation.vec3d = entry.vec3d;
         playerSimulation.vec3d2 = entry.vec3d2;
         playerSimulation.box = entry.box;
         playerSimulation.floatValue = floatValue;
         playerSimulation.floatValue2 = f;
         playerSimulation.flag = entry2.flag3;
         playerSimulation.floatValue3 = entry.floatValue;
         playerSimulation.intValue = entry.intValue;
         playerSimulation.flag2 = entry.flag4;
         playerSimulation.flag3 = entry.flag5;
         playerSimulation.flag4 = entry.flag;
         playerSimulation.flag5 = entry.flag6;
         playerSimulation.flag6 = entry.flag7;
         playerSimulation.flag7 = entry.flag8;
         playerSimulation.flag8 = entry.flag9;
         playerSimulation.flag9 = entry.flag10;
         playerSimulation.entityPose = entry.entityPose;
         playerSimulation.inSneakingPose = entry.flag11;
         playerSimulation.flag10 = entry.flag12;
         playerSimulation.tick();
         Vec3 vec3d = playerSimulation.vec3d.subtract(entry.vec3d);
         return this.method4977(vec3d) && this.method4977(playerSimulation.vec3d) && this.method4977(playerSimulation.vec3d2)
            ? new RotationRecorderModule.Entry3(true, vec3d, playerSimulation.vec3d, playerSimulation.vec3d2, playerSimulation.flag4, playerSimulation.flag5, playerSimulation.flag6)
            : RotationRecorderModule.Entry3.method4998(entry.vec3d, entry.vec3d2);
      } catch (Throwable e) {
         return RotationRecorderModule.Entry3.method4998(entry.vec3d, entry.vec3d2);
      }
   }

   private boolean method4977(Vec3 vec3d) {
      return (vec3d != null && this.isFinite((float)vec3d.x) && this.isFinite((float)vec3d.y) && this.isFinite((float)vec3d.z));
   }

   private static boolean method4978(float... floats) {
      float[] floats2 = floats;
      int length = floats.length;

      for (int i = 0; i < length; i++) {
         float f;
         if (!Float.isFinite(f = floats2[i])) {
            return false;
         }
      }

      return true;
   }

   private static final class Data implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final RotationRecorderModule.Data20 data20;
      private final RotationRecorderModule.Data20 data202;
      private final RotationRecorderModule.Data20 data203;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;

      public boolean flag() {
         return this.flag;
      }

      public RotationRecorderModule.Data20 data20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 data202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 data203() {
         return this.data203;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.data20, other.data20) && java.util.Objects.equals(this.data202, other.data202) && java.util.Objects.equals(this.data203, other.data203) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, data20, data202, data203, flag2, flag3, flag4);
      }

      @Override
      public String toString() {
         return "Data[" + "flag=" + this.flag + ", " + "data20=" + this.data20 + ", " + "data202=" + this.data202 + ", " + "data203=" + this.data203 + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + "]";
      }

      private Data(
         boolean flag,
         RotationRecorderModule.Data20 data20,
         RotationRecorderModule.Data20 data202,
         RotationRecorderModule.Data20 data203,
         boolean flag2,
         boolean flag3,
         boolean flag4
      ) {
         this.flag = flag;
         this.data20 = data20;
         this.data202 = data202;
         this.data203 = data203;
         this.flag2 = flag2;
         this.flag3 = flag3;
         this.flag4 = flag4;
      }

      private static void method4979(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029("base_sim_ok");
         RotationRecorderModule.Data20.method5035(entry8, "base_sim_motion");
         RotationRecorderModule.Data20.method5035(entry8, "base_sim_pos");
         RotationRecorderModule.Data20.method5035(entry8, "base_sim_velocity");
         entry8.method5029("base_sim_on_ground", "base_sim_horizontal_collision", "base_sim_vertical_collision");
      }

      private static RotationRecorderModule.Data method4980(RotationRecorderModule.Entry3 entry3) {
         return new RotationRecorderModule.Data(
            entry3.flag,
            RotationRecorderModule.Data20.method5034(entry3.vec3d),
            RotationRecorderModule.Data20.method5034(entry3.vec3d2),
            RotationRecorderModule.Data20.method5034(entry3.vec3d3),
            entry3.flag2,
            entry3.flag3,
            entry3.flag4
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5022(this.flag);
         this.data20.method4981(entry6);
         this.data202.method4981(entry6);
         this.data203.method4981(entry6);
         entry6.method5022(this.flag2, this.flag3, this.flag4);
      }

      @Override
      public boolean method4982() {
         return this.data20.method4982() && this.data202.method4982() && this.data203.method4982();
      }

      public boolean isFlag() {
         return this.flag;
      }

      public RotationRecorderModule.Data20 getData20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 getData202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 getData203() {
         return this.data203;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }
   }

   private static final class Data10 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data14 data14;
      private final RotationRecorderModule.Data8 data8;
      private final RotationRecorderModule.Data18 data18;
      private final RotationRecorderModule.Data15 data15;

      public RotationRecorderModule.Data14 data14() {
         return this.data14;
      }

      public RotationRecorderModule.Data8 data8() {
         return this.data8;
      }

      public RotationRecorderModule.Data18 data18() {
         return this.data18;
      }

      public RotationRecorderModule.Data15 data15() {
         return this.data15;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.data14, other.data14) && java.util.Objects.equals(this.data8, other.data8) && java.util.Objects.equals(this.data18, other.data18) && java.util.Objects.equals(this.data15, other.data15);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data14, data8, data18, data15);
      }

      @Override
      public String toString() {
         return "Data10[" + "data14=" + this.data14 + ", " + "data8=" + this.data8 + ", " + "data18=" + this.data18 + ", " + "data15=" + this.data15 + "]";
      }

      private Data10(
         RotationRecorderModule.Data14 data14, RotationRecorderModule.Data8 data8, RotationRecorderModule.Data18 data18, RotationRecorderModule.Data15 data15
      ) {
         this.data14 = data14;
         this.data8 = data8;
         this.data18 = data18;
         this.data15 = data15;
      }

      private static void method5013(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data14.method5019(entry8);
         RotationRecorderModule.Data8.method4996(entry8);
         RotationRecorderModule.Data18.method5028(entry8);
         RotationRecorderModule.Data15.method5023(entry8);
      }

      private RotationRecorderModule.Data10 method5014(RotationRecorderModule.Data10 data10, boolean flag, boolean flag2) {
         return new RotationRecorderModule.Data10(this.data14.method5020(flag2, data10.data14.floatValue3), this.data8.method4997(flag), this.data18, data10.data15);
      }

      private boolean method5015(boolean flag) {
         return !this.data8.flag3
            && !this.data8.flag4
            && !this.data8.flag5
            && !this.data18.flag
            && !this.data18.flag2
            && !this.data18.flag3
            && !this.data18.flag4
            && !this.data18.flag5
            && (!flag || this.data8.floatValue > 6.0F);
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         this.data14.method4981(entry6);
         this.data8.method4981(entry6);
         this.data18.method4981(entry6);
         this.data15.method4981(entry6);
      }

      @Override
      public boolean method4982() {
         return this.data14.method4982() && this.data8.method4982() && this.data18.method4982() && this.data15.method4982();
      }

      public RotationRecorderModule.Data14 getData14() {
         return this.data14;
      }

      public RotationRecorderModule.Data8 getData8() {
         return this.data8;
      }

      public RotationRecorderModule.Data18 getData18() {
         return this.data18;
      }

      public RotationRecorderModule.Data15 getData15() {
         return this.data15;
      }
   }

   private static final class Data11 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data11[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data11(float floatValue, float f) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }

      private static void method5016(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029("target_yaw_mouse_count", "target_pitch_mouse_count");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data12 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data12[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }

      private Data12(float floatValue, float f, float f2, float f3, float f4) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
      }

      private static void method5017(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "target_look_at_player_yaw_error_sin",
            "target_look_at_player_yaw_error_cos",
            "target_look_at_player_pitch_error_sin",
            "target_look_at_player_pitch_error_cos",
            "target_look_at_player_alignment_cos"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   private static final class Data13 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final float floatValue13;
      private final float floatValue14;
      private final float floatValue15;
      private final float floatValue16;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      public float floatValue10() {
         return this.floatValue10;
      }

      public float floatValue11() {
         return this.floatValue11;
      }

      public float floatValue12() {
         return this.floatValue12;
      }

      public float floatValue13() {
         return this.floatValue13;
      }

      public float floatValue14() {
         return this.floatValue14;
      }

      public float floatValue15() {
         return this.floatValue15;
      }

      public float floatValue16() {
         return this.floatValue16;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.floatValue12, other.floatValue12) && java.util.Objects.equals(this.floatValue13, other.floatValue13) && java.util.Objects.equals(this.floatValue14, other.floatValue14) && java.util.Objects.equals(this.floatValue15, other.floatValue15) && java.util.Objects.equals(this.floatValue16, other.floatValue16);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, floatValue15, floatValue16);
      }

      @Override
      public String toString() {
         return "Data13[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "floatValue12=" + this.floatValue12 + ", " + "floatValue13=" + this.floatValue13 + ", " + "floatValue14=" + this.floatValue14 + ", " + "floatValue15=" + this.floatValue15 + ", " + "floatValue16=" + this.floatValue16 + "]";
      }

      private Data13(
         float floatValue,
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         float f6,
         float f7,
         float f8,
         float f9,
         float f10,
         float f11,
         float f12,
         float f13,
         float f14,
         float f15
      ) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
         this.floatValue10 = f9;
         this.floatValue11 = f10;
         this.floatValue12 = f11;
         this.floatValue13 = f12;
         this.floatValue14 = f13;
         this.floatValue15 = f14;
         this.floatValue16 = f15;
      }

      private static void method5018(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "yaw_mouse_count",
            "pitch_mouse_count",
            "yaw_acceleration_mouse_counts",
            "pitch_acceleration_mouse_counts",
            "yaw_jerk_mouse_counts",
            "pitch_jerk_mouse_counts",
            "mouse_dx_nonzero_streak_ticks",
            "mouse_dy_nonzero_streak_ticks",
            "yaw_sign_flip_ticks_ago",
            "pitch_sign_flip_ticks_ago",
            "yaw_mouse_static_help",
            "pitch_mouse_static_help",
            "yaw_current_to_peak_abs_ratio",
            "pitch_current_to_peak_abs_ratio",
            "yaw_peak_mouse_count_abs",
            "pitch_peak_mouse_count_abs"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            this.floatValue14,
            this.floatValue15,
            this.floatValue16
         );
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            this.floatValue14,
            this.floatValue15,
            this.floatValue16
         );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public float getFloatValue12() {
         return this.floatValue12;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }

      public float getFloatValue15() {
         return this.floatValue15;
      }

      public float getFloatValue16() {
         return this.floatValue16;
      }
   }

   private static final class Data14 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final boolean flag;
      private final float floatValue2;
      private final float floatValue3;

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, flag, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data14[" + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data14(float floatValue, boolean flag, float f, float f2) {
         this.floatValue = floatValue;
         this.flag = flag;
         this.floatValue2 = f;
         this.floatValue3 = f2;
      }

      private static void method5019(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029("recorder_schema_version", "simpy_supported_v0", "simpy_schema_version", "simpy_world_mode");
      }

      private RotationRecorderModule.Data14 method5020(boolean flag, float f) {
         return new RotationRecorderModule.Data14(this.floatValue, flag, this.floatValue2, f);
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue);
         entry6.method5022(this.flag);
         entry6.method5021(this.floatValue2, this.floatValue3);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data15 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data15 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8);
      }

      @Override
      public String toString() {
         return "Data15[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + "]";
      }

      private Data15(float floatValue, float intValue, float f, float f2, float f3, float f4, float f5, float f6) {
         this.floatValue = floatValue;
         this.floatValue2 = intValue;
         this.floatValue3 = f;
         this.floatValue4 = f2;
         this.floatValue5 = f3;
         this.floatValue6 = f4;
         this.floatValue7 = f5;
         this.floatValue8 = f6;
      }

      private static void method5023(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "snapshot_id",
            "snapshot_origin_x",
            "snapshot_origin_y",
            "snapshot_origin_z",
            "snapshot_size_x",
            "snapshot_size_y",
            "snapshot_size_z",
            "snapshot_version"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
         );
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
         );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }
   }

   private static final class Data16 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data16 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data16[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }

      private Data16(float floatValue, float f, float f2, float f3, float f4) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
      }

      private static RotationRecorderModule.Data16 method5024(Input playerInput) {
         return new RotationRecorderModule.Data16(
            playerInput.forward() ? 1.0F : 0.0F, playerInput.backward() ? 1.0F : 0.0F, playerInput.left() ? 1.0F : 0.0F, playerInput.right() ? 1.0F : 0.0F, playerInput.jump() ? 1.0F : 0.0F
         );
      }

      private static void method5025(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029("movement_input_forward", "movement_input_backward", "movement_input_left", "movement_input_right", "movement_input_jump");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float left() {
         return this.floatValue3;
      }

      public float right() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   private static final class Data17 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data17 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data17[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }

      private Data17(float floatValue, float hypot, float f, float f2, float f3) {
         this.floatValue = floatValue;
         this.floatValue2 = hypot;
         this.floatValue3 = f;
         this.floatValue4 = f2;
         this.floatValue5 = f3;
      }

      private static void method5027(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "target_center_distance",
            "target_center_horizontal_distance",
            "target_center_vertical_distance",
            "aim_inside_target_box_ticks_clamped",
            "aim_outside_target_box_ticks_clamped"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   private static final class Data18 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final float floatValue;
      private final float floatValue2;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data18 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data18[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data18(boolean flag, boolean flag2, boolean flag3, boolean flag4, boolean flag5, float floatValue, float amplifier) {
         this.flag = flag;
         this.flag2 = flag2;
         this.flag3 = flag3;
         this.flag4 = flag4;
         this.flag5 = flag5;
         this.floatValue = floatValue;
         this.floatValue2 = amplifier;
      }

      private static void method5028(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "effect_slow_falling",
            "effect_levitation",
            "effect_jump_boost",
            "effect_blindness",
            "effect_dolphins_grace",
            "effect_levitation_amp",
            "effect_jump_boost_amp"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5);
         entry6.method5021(this.floatValue, this.floatValue2);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data19 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data20 data20;
      private final RotationRecorderModule.Data20 data202;
      private final float floatValue;
      private final float floatValue2;
      private final RotationRecorderModule.Data20 data203;
      private final RotationRecorderModule.Data20 data204;
      private final RotationRecorderModule.Data9 data9;

      public RotationRecorderModule.Data20 data20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 data202() {
         return this.data202;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public RotationRecorderModule.Data20 data203() {
         return this.data203;
      }

      public RotationRecorderModule.Data20 data204() {
         return this.data204;
      }

      public RotationRecorderModule.Data9 data9() {
         return this.data9;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data19 other)) return false;
         return java.util.Objects.equals(this.data20, other.data20) && java.util.Objects.equals(this.data202, other.data202) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.data203, other.data203) && java.util.Objects.equals(this.data204, other.data204) && java.util.Objects.equals(this.data9, other.data9);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data20, data202, floatValue, floatValue2, data203, data204, data9);
      }

      @Override
      public String toString() {
         return "Data19[" + "data20=" + this.data20 + ", " + "data202=" + this.data202 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "data203=" + this.data203 + ", " + "data204=" + this.data204 + ", " + "data9=" + this.data9 + "]";
      }

      private Data19(
         RotationRecorderModule.Data20 data20,
         RotationRecorderModule.Data20 data202,
         float floatValue,
         float f,
         RotationRecorderModule.Data20 data203,
         RotationRecorderModule.Data20 data204,
         RotationRecorderModule.Data9 data9
      ) {
         this.data20 = data20;
         this.data202 = data202;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.data203 = data203;
         this.data204 = data204;
         this.data9 = data9;
      }

      private static void method5033(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data20.method5035(entry8, "player_pos");
         RotationRecorderModule.Data20.method5035(entry8, "player_eye");
         entry8.method5029("player_yaw", "player_pitch");
         RotationRecorderModule.Data20.method5035(entry8, "player_velocity");
         RotationRecorderModule.Data20.method5035(entry8, "player_prev_pos");
         RotationRecorderModule.Data9.method5012(entry8, "player_bbox");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         this.data20.method4981(entry6);
         this.data202.method4981(entry6);
         entry6.method5021(this.floatValue, this.floatValue2);
         this.data203.method4981(entry6);
         this.data204.method4981(entry6);
         this.data9.method4981(entry6);
      }

      @Override
      public boolean method4982() {
         return this.data20.method4982()
            && this.data202.method4982()
            && RotationRecorderModule.method4978(this.floatValue, this.floatValue2)
            && this.data203.method4982()
            && this.data204.method4982()
            && this.data9.method4982();
      }

      public RotationRecorderModule.Data20 getData20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 getData202() {
         return this.data202;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public RotationRecorderModule.Data20 getData203() {
         return this.data203;
      }

      public RotationRecorderModule.Data20 getData204() {
         return this.data204;
      }

      public RotationRecorderModule.Data9 getData9() {
         return this.data9;
      }
   }

   private static final class Data2 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data6 data6;
      private final RotationRecorderModule.Data17 data17;
      private final RotationRecorderModule.Data7 data7;
      private final RotationRecorderModule.Data12 data12;
      private final RotationRecorderModule.Data13 data13;
      private final RotationRecorderModule.Data16 data16;
      private final RotationRecorderModule.Data5 data5;
      private final RotationRecorderModule.Data22 data22;

      public RotationRecorderModule.Data6 data6() {
         return this.data6;
      }

      public RotationRecorderModule.Data17 data17() {
         return this.data17;
      }

      public RotationRecorderModule.Data7 data7() {
         return this.data7;
      }

      public RotationRecorderModule.Data12 data12() {
         return this.data12;
      }

      public RotationRecorderModule.Data13 data13() {
         return this.data13;
      }

      public RotationRecorderModule.Data16 data16() {
         return this.data16;
      }

      public RotationRecorderModule.Data5 data5() {
         return this.data5;
      }

      public RotationRecorderModule.Data22 data22() {
         return this.data22;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.data6, other.data6) && java.util.Objects.equals(this.data17, other.data17) && java.util.Objects.equals(this.data7, other.data7) && java.util.Objects.equals(this.data12, other.data12) && java.util.Objects.equals(this.data13, other.data13) && java.util.Objects.equals(this.data16, other.data16) && java.util.Objects.equals(this.data5, other.data5) && java.util.Objects.equals(this.data22, other.data22);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data6, data17, data7, data12, data13, data16, data5, data22);
      }

      @Override
      public String toString() {
         return "Data2[" + "data6=" + this.data6 + ", " + "data17=" + this.data17 + ", " + "data7=" + this.data7 + ", " + "data12=" + this.data12 + ", " + "data13=" + this.data13 + ", " + "data16=" + this.data16 + ", " + "data5=" + this.data5 + ", " + "data22=" + this.data22 + "]";
      }

      private Data2(
         RotationRecorderModule.Data6 data6,
         RotationRecorderModule.Data17 data17,
         RotationRecorderModule.Data7 data7,
         RotationRecorderModule.Data12 data12,
         RotationRecorderModule.Data13 data13,
         RotationRecorderModule.Data16 data16,
         RotationRecorderModule.Data5 data5,
         RotationRecorderModule.Data22 data22
      ) {
         this.data6 = data6;
         this.data17 = data17;
         this.data7 = data7;
         this.data12 = data12;
         this.data13 = data13;
         this.data16 = data16;
         this.data5 = data5;
         this.data22 = data22;
      }

      private static void method4983(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data6.method4994(entry8);
         RotationRecorderModule.Data17.method5027(entry8);
         RotationRecorderModule.Data7.method4995(entry8);
         RotationRecorderModule.Data12.method5017(entry8);
         RotationRecorderModule.Data13.method5018(entry8);
         RotationRecorderModule.Data16.method5025(entry8);
         RotationRecorderModule.Data5.method4993(entry8, "player_motion_predicted_1t");
         RotationRecorderModule.Data22.method5038(entry8);
      }

      private RotationRecorderModule.Data2 method4984(RotationRecorderModule.Entry entry) {
         return new RotationRecorderModule.Data2(
            this.data6, this.data17, this.data7, this.data12, this.data13, RotationRecorderModule.Data16.method5024(entry.playerInput), this.data5, this.data22
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         this.data6.method4981(entry6);
         this.data17.method4981(entry6);
         this.data7.method4981(entry6);
         this.data12.method4981(entry6);
         this.data13.method4981(entry6);
         this.data16.method4981(entry6);
         this.data5.method4981(entry6);
         this.data22.method4981(entry6);
      }

      @Override
      public boolean method4982() {
         return this.data6.method4982()
            && this.data17.method4982()
            && this.data7.method4982()
            && this.data12.method4982()
            && this.data13.method4982()
            && this.data16.method4982()
            && this.data5.method4982()
            && this.data22.method4982();
      }

      private float[] method4985(RotationRecorderModule.Data3 data3) {
         return new float[]{
            this.data6.floatValue,
            this.data6.floatValue2,
            this.data6.floatValue3,
            this.data6.floatValue4,
            this.data6.floatValue5,
            this.data6.floatValue6,
            this.data6.floatValue7,
            this.data6.floatValue8,
            this.data6.floatValue9,
            this.data6.floatValue10,
            this.data6.floatValue11,
            this.data6.flag ? 1.0F : 0.0F,
            this.data17.floatValue,
            this.data17.floatValue2,
            this.data17.floatValue3,
            this.data17.floatValue4,
            this.data17.floatValue5,
            this.data7.floatValue,
            this.data7.floatValue2,
            this.data7.floatValue3,
            this.data7.floatValue4,
            this.data7.floatValue5,
            data3.floatValue,
            data3.floatValue2,
            this.data7.data5.floatValue,
            this.data7.data5.floatValue2,
            this.data7.data5.floatValue3,
            this.data13.floatValue,
            this.data13.floatValue2,
            this.data13.floatValue3,
            this.data13.floatValue4,
            this.data13.floatValue5,
            this.data13.floatValue6,
            this.data13.floatValue7,
            this.data13.floatValue8,
            this.data13.floatValue9,
            this.data13.floatValue10,
            this.data13.floatValue11,
            this.data13.floatValue12,
            this.data13.floatValue13,
            this.data13.floatValue14,
            this.data13.floatValue15,
            this.data13.floatValue16,
            this.data16.floatValue,
            this.data16.floatValue2,
            this.data16.floatValue3,
            this.data16.floatValue4,
            this.data5.floatValue,
            this.data5.floatValue2,
            this.data5.floatValue3,
            this.data22.floatValue,
            this.data22.floatValue2,
            this.data22.flag ? 1.0F : 0.0F,
            this.data22.flag2 ? 1.0F : 0.0F
         };
      }

      private float[] method4986(RotationRecorderModule.Data3 data3) {
         float[] floats = this.method4985(data3);
         float[] floats2 = new float[floats.length + 1];
         System.arraycopy(floats, 0, floats2, 0, 47);
         floats2[47] = this.data16.floatValue5;
         System.arraycopy(floats, 47, floats2, 48, floats.length - 47);
         return floats2;
      }

      public RotationRecorderModule.Data6 getData6() {
         return this.data6;
      }

      public RotationRecorderModule.Data17 getData17() {
         return this.data17;
      }

      public RotationRecorderModule.Data7 getData7() {
         return this.data7;
      }

      public RotationRecorderModule.Data12 getData12() {
         return this.data12;
      }

      public RotationRecorderModule.Data13 getData13() {
         return this.data13;
      }

      public RotationRecorderModule.Data16 getData16() {
         return this.data16;
      }

      public RotationRecorderModule.Data5 getData5() {
         return this.data5;
      }

      public RotationRecorderModule.Data22 getData22() {
         return this.data22;
      }
   }

   private static final class Data20 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data20 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data20[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data20(float floatValue, float y, float z) {
         this.floatValue = floatValue;
         this.floatValue2 = y;
         this.floatValue3 = z;
      }

      private static RotationRecorderModule.Data20 method5034(Vec3 vec3d) {
         return new RotationRecorderModule.Data20((float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
      }

      private static void method5035(RotationRecorderModule.Entry8 entry8, String str) {
         entry8.method5029(str + "_x", str + "_y", str + "_z");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data21 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public boolean flag6() {
         return this.flag6;
      }

      public boolean flag7() {
         return this.flag7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data21 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.flag6, other.flag6) && java.util.Objects.equals(this.flag7, other.flag7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, flag6, flag7);
      }

      @Override
      public String toString() {
         return "Data21[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "flag6=" + this.flag6 + ", " + "flag7=" + this.flag7 + "]";
      }

      private Data21(boolean flag, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint) {
         this.flag = flag;
         this.flag2 = backward;
         this.flag3 = left;
         this.flag4 = right;
         this.flag5 = jump;
         this.flag6 = shift;
         this.flag7 = sprint;
      }

      private static RotationRecorderModule.Data21 method5036(Input keyPresses) {
         return new RotationRecorderModule.Data21(keyPresses.forward(), keyPresses.backward(), keyPresses.left(), keyPresses.right(), keyPresses.jump(), keyPresses.shift(), keyPresses.sprint());
      }

      private static void method5037(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "input_forward_raw", "input_backward_raw", "input_left_raw", "input_right_raw", "input_jump_raw", "input_sneak_raw", "input_sprint_raw"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5, this.flag6, this.flag7);
      }

      @Override
      public boolean method4982() {
         return true;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public boolean isFlag6() {
         return this.flag6;
      }

      public boolean isFlag7() {
         return this.flag7;
      }
   }

   private static final class Data22 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;
      private final boolean flag2;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data22 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data22[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data22(float floatValue, float f, boolean flag, boolean flag2) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.flag = flag;
         this.flag2 = flag2;
      }

      private static void method5038(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029("attack_cooldown_progress", "ticks_since_last_attack_clamped", "state_on_ground", "did_attack_previous_tick");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2);
         entry6.method5022(this.flag, this.flag2);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }

   private static final class Data23 implements RotationRecorderModule.Handler {
      private final boolean flag;

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data23 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag);
      }

      @Override
      public String toString() {
         return "Data23[" + "flag=" + this.flag + "]";
      }

      private Data23(boolean flag) {
         this.flag = flag;
      }

      private static void method5039(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029("dirty_physics_flag");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5022(this.flag);
      }

      @Override
      public boolean method4982() {
         return true;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data24 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data19 data19;
      private final RotationRecorderModule.Data21 data21;
      private final RotationRecorderModule.Data4 data4;
      private final RotationRecorderModule.Data3 data3;
      private final RotationRecorderModule.Data data;

      public RotationRecorderModule.Data19 data19() {
         return this.data19;
      }

      public RotationRecorderModule.Data21 data21() {
         return this.data21;
      }

      public RotationRecorderModule.Data4 data4() {
         return this.data4;
      }

      public RotationRecorderModule.Data3 data3() {
         return this.data3;
      }

      public RotationRecorderModule.Data data() {
         return this.data;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data24 other)) return false;
         return java.util.Objects.equals(this.data19, other.data19) && java.util.Objects.equals(this.data21, other.data21) && java.util.Objects.equals(this.data4, other.data4) && java.util.Objects.equals(this.data3, other.data3) && java.util.Objects.equals(this.data, other.data);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data19, data21, data4, data3, data);
      }

      @Override
      public String toString() {
         return "Data24[" + "data19=" + this.data19 + ", " + "data21=" + this.data21 + ", " + "data4=" + this.data4 + ", " + "data3=" + this.data3 + ", " + "data=" + this.data + "]";
      }

      private Data24(
         RotationRecorderModule.Data19 data19,
         RotationRecorderModule.Data21 data21,
         RotationRecorderModule.Data4 data4,
         RotationRecorderModule.Data3 data3,
         RotationRecorderModule.Data data
      ) {
         this.data19 = data19;
         this.data21 = data21;
         this.data4 = data4;
         this.data3 = data3;
         this.data = data;
      }

      private static void method5040(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data19.method5033(entry8);
         RotationRecorderModule.Data21.method5037(entry8);
         RotationRecorderModule.Data4.method4992(entry8);
         RotationRecorderModule.Data3.method4987(entry8);
         RotationRecorderModule.Data.method4979(entry8);
      }

      private RotationRecorderModule.Data24 method5041(Input playerInput, RotationRecorderModule.Entry3 entry3) {
         return new RotationRecorderModule.Data24(
            this.data19,
            RotationRecorderModule.Data21.method5036(playerInput),
            this.data4.method4991(playerInput.jump()),
            this.data3,
            RotationRecorderModule.Data.method4980(entry3)
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         this.data19.method4981(entry6);
         this.data21.method4981(entry6);
         this.data4.method4981(entry6);
         this.data3.method4981(entry6);
         this.data.method4981(entry6);
      }

      @Override
      public boolean method4982() {
         return this.data19.method4982() && this.data4.method4982() && this.data3.method4982() && this.data.method4982();
      }

      public RotationRecorderModule.Data19 getData19() {
         return this.data19;
      }

      public RotationRecorderModule.Data21 getData21() {
         return this.data21;
      }

      public RotationRecorderModule.Data4 getData4() {
         return this.data4;
      }

      public RotationRecorderModule.Data3 getData3() {
         return this.data3;
      }

      public RotationRecorderModule.Data getData() {
         return this.data;
      }
   }

   private static final class Data3 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data9 data9;
      private final RotationRecorderModule.Data20 data20;
      private final RotationRecorderModule.Data20 data202;
      private final RotationRecorderModule.Data20 data203;
      private final float floatValue;
      private final float floatValue2;

      public RotationRecorderModule.Data9 data9() {
         return this.data9;
      }

      public RotationRecorderModule.Data20 data20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 data202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 data203() {
         return this.data203;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.data9, other.data9) && java.util.Objects.equals(this.data20, other.data20) && java.util.Objects.equals(this.data202, other.data202) && java.util.Objects.equals(this.data203, other.data203) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data9, data20, data202, data203, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data3[" + "data9=" + this.data9 + ", " + "data20=" + this.data20 + ", " + "data202=" + this.data202 + ", " + "data203=" + this.data203 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data3(
         RotationRecorderModule.Data9 data9,
         RotationRecorderModule.Data20 data20,
         RotationRecorderModule.Data20 data202,
         RotationRecorderModule.Data20 data203,
         float floatValue,
         float f
      ) {
         this.data9 = data9;
         this.data20 = data20;
         this.data202 = data202;
         this.data203 = data203;
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }

      private static void method4987(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data9.method5012(entry8, "target_box");
         RotationRecorderModule.Data20.method5035(entry8, "target_center");
         RotationRecorderModule.Data20.method5035(entry8, "target_tracked_pos");
         RotationRecorderModule.Data20.method5035(entry8, "target_visual_motion_raw");
         entry8.method5029("target_center_delta_yaw_count_units", "target_center_delta_pitch_count_units");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         this.data9.method4981(entry6);
         this.data20.method4981(entry6);
         this.data202.method4981(entry6);
         this.data203.method4981(entry6);
         entry6.method5021(this.floatValue, this.floatValue2);
      }

      @Override
      public boolean method4982() {
         return this.data9.method4982()
            && this.data20.method4982()
            && this.data202.method4982()
            && this.data203.method4982()
            && RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public RotationRecorderModule.Data9 getData9() {
         return this.data9;
      }

      public RotationRecorderModule.Data20 getData20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 getData202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 getData203() {
         return this.data203;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data4 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final boolean flag8;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final boolean flag9;
      private final boolean flag10;
      private final boolean flag11;
      private final boolean flag12;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public boolean flag6() {
         return this.flag6;
      }

      public boolean flag7() {
         return this.flag7;
      }

      public boolean flag8() {
         return this.flag8;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public boolean flag9() {
         return this.flag9;
      }

      public boolean flag10() {
         return this.flag10;
      }

      public boolean flag11() {
         return this.flag11;
      }

      public boolean flag12() {
         return this.flag12;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.flag6, other.flag6) && java.util.Objects.equals(this.flag7, other.flag7) && java.util.Objects.equals(this.flag8, other.flag8) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.flag9, other.flag9) && java.util.Objects.equals(this.flag10, other.flag10) && java.util.Objects.equals(this.flag11, other.flag11) && java.util.Objects.equals(this.flag12, other.flag12);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, flag6, flag7, flag8, floatValue, floatValue2, floatValue3, flag9, flag10, flag11, flag12);
      }

      @Override
      public String toString() {
         return "Data4[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "flag6=" + this.flag6 + ", " + "flag7=" + this.flag7 + ", " + "flag8=" + this.flag8 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "flag9=" + this.flag9 + ", " + "flag10=" + this.flag10 + ", " + "flag11=" + this.flag11 + ", " + "flag12=" + this.flag12 + "]";
      }

      private Data4(
         boolean flag,
         boolean verticalCollision,
         boolean inWater,
         boolean swimming,
         boolean underWater,
         boolean fallFlying,
         boolean crouching,
         boolean visuallyCrawling,
         float floatValue,
         float fallDistance,
         float noJumpDelay,
         boolean jumping,
         boolean passenger,
         boolean flying,
         boolean spectator
      ) {
         this.flag = flag;
         this.flag2 = verticalCollision;
         this.flag3 = inWater;
         this.flag4 = swimming;
         this.flag5 = underWater;
         this.flag6 = fallFlying;
         this.flag7 = crouching;
         this.flag8 = visuallyCrawling;
         this.floatValue = floatValue;
         this.floatValue2 = fallDistance;
         this.floatValue3 = noJumpDelay;
         this.flag9 = jumping;
         this.flag10 = passenger;
         this.flag11 = flying;
         this.flag12 = spectator;
      }

      private RotationRecorderModule.Data4 method4991(boolean jump) {
         return new RotationRecorderModule.Data4(
            this.flag,
            this.flag2,
            this.flag3,
            this.flag4,
            this.flag5,
            this.flag6,
            this.flag7,
            this.flag8,
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            jump,
            this.flag10,
            this.flag11,
            this.flag12
         );
      }

      private static void method4992(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "state_horizontal_collision",
            "state_vertical_collision",
            "state_touching_water",
            "state_swimming",
            "state_submerged_water",
            "state_fall_flying",
            "state_in_sneaking_pose",
            "state_crawling",
            "state_pose_id",
            "state_fall_distance",
            "state_jumping_cooldown",
            "state_is_jumping",
            "state_has_vehicle",
            "state_flying",
            "state_spectator"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5, this.flag6, this.flag7, this.flag8);
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3);
         entry6.method5022(this.flag9, this.flag10, this.flag11, this.flag12);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public boolean isFlag6() {
         return this.flag6;
      }

      public boolean isFlag7() {
         return this.flag7;
      }

      public boolean isFlag8() {
         return this.flag8;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public boolean isFlag9() {
         return this.flag9;
      }

      public boolean isFlag10() {
         return this.flag10;
      }

      public boolean isFlag11() {
         return this.flag11;
      }

      public boolean isFlag12() {
         return this.flag12;
      }
   }

   private static final class Data5 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data5[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data5(float floatValue, float f, float f2) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
      }

      private static void method4993(RotationRecorderModule.Entry8 entry8, String str) {
         entry8.method5029(str + "_yaw_change_count_units", str + "_pitch_change_count_units", str + "_distance_change");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data6 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final boolean flag;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      public float floatValue10() {
         return this.floatValue10;
      }

      public float floatValue11() {
         return this.floatValue11;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, flag);
      }

      @Override
      public String toString() {
         return "Data6[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "flag=" + this.flag + "]";
      }

      private Data6(
         float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, boolean flag
      ) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
         this.floatValue10 = f9;
         this.floatValue11 = f10;
         this.flag = flag;
      }

      private static void method4994(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "target_box_min_yaw_from_center_count_units",
            "target_box_max_yaw_from_center_count_units",
            "target_box_min_pitch_from_center_count_units",
            "target_box_max_pitch_from_center_count_units",
            "target_center_pitch_by_box_height",
            "target_center_angular_distance_count_units",
            "target_direction_right",
            "target_direction_up",
            "target_direction_forward",
            "aim_x_norm_in_target_box",
            "aim_y_norm_in_target_box",
            "aim_inside_target_box"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11
         );
         entry6.method5022(this.flag);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11
         );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data7 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final RotationRecorderModule.Data5 data5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public RotationRecorderModule.Data5 data5() {
         return this.data5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.data5, other.data5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, data5);
      }

      @Override
      public String toString() {
         return "Data7[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "data5=" + this.data5 + "]";
      }

      private Data7(float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, RotationRecorderModule.Data5 data5) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.data5 = data5;
      }

      private static void method4995(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "target_center_delta_yaw_change_count_units",
            "target_center_delta_pitch_change_count_units",
            "target_center_distance_change",
            "yaw_target_drift_count_units",
            "pitch_target_drift_count_units",
            "player_sim_1t_tracked_target_center_delta_yaw_sin",
            "player_sim_1t_tracked_target_center_delta_yaw_cos",
            "player_sim_1t_tracked_target_center_delta_pitch_count_units"
         );
         RotationRecorderModule.Data5.method4993(entry8, "target_motion_predicted_1t");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
         );
         this.data5.method4981(entry6);
      }

      @Override
      public boolean method4982() {
         return this.data5.method4982()
            && RotationRecorderModule.method4978(
               this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
            );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public RotationRecorderModule.Data5 getData5() {
         return this.data5;
      }
   }

   private static final class Data8 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7);
      }

      @Override
      public String toString() {
         return "Data8[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + "]";
      }

      private Data8(
         boolean flag,
         boolean mayfly,
         boolean noGravity,
         boolean shouldDiscardFriction,
         boolean usingItem,
         float floatValue,
         float maxUpStep,
         float f,
         float f2,
         float f3,
         float f4,
         float f5
      ) {
         this.flag = flag;
         this.flag2 = mayfly;
         this.flag3 = noGravity;
         this.flag4 = shouldDiscardFriction;
         this.flag5 = usingItem;
         this.floatValue = floatValue;
         this.floatValue2 = maxUpStep;
         this.floatValue3 = f;
         this.floatValue4 = f2;
         this.floatValue5 = f3;
         this.floatValue6 = f4;
         this.floatValue7 = f5;
      }

      private static void method4996(RotationRecorderModule.Entry8 entry8) {
         entry8.method5029(
            "state_sprinting",
            "state_allow_flying",
            "state_no_gravity",
            "state_no_drag",
            "state_using_item",
            "state_hunger_food_level",
            "state_step_height",
            "attr_movement_speed",
            "attr_gravity",
            "attr_jump_strength",
            "attr_sneaking_speed",
            "attr_water_movement_efficiency"
         );
      }

      private RotationRecorderModule.Data8 method4997(boolean flag) {
         return new RotationRecorderModule.Data8(
            flag,
            this.flag2,
            this.flag3,
            this.flag4,
            this.flag5,
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5);
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7
         );
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }
   }

   private static final class Data9 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6);
      }

      @Override
      public String toString() {
         return "Data9[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + "]";
      }

      private Data9(float floatValue, float minY, float minZ, float maxX, float maxY, float maxZ) {
         this.floatValue = floatValue;
         this.floatValue2 = minY;
         this.floatValue3 = minZ;
         this.floatValue4 = maxX;
         this.floatValue5 = maxY;
         this.floatValue6 = maxZ;
      }

      private static RotationRecorderModule.Data9 method5011(AABB boundingBox) {
         return new RotationRecorderModule.Data9((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ, (float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
      }

      private static void method5012(RotationRecorderModule.Entry8 entry8, String str) {
         entry8.method5029(str + "_min_x", str + "_min_y", str + "_min_z", str + "_max_x", str + "_max_y", str + "_max_z");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 entry6) {
         entry6.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }
   }

   private static final class Entry {
      private final RotationRecorderModule.Data2 data2;
      private final RotationRecorderModule.Data24 data24;
      private final RotationRecorderModule.Data10 data10;
      private final Vec3 vec3d;
      private final Vec3 vec3d2;
      private final AABB box;
      private final Input playerInput;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final float floatValue;
      private final int intValue;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final boolean flag8;
      private final boolean flag9;
      private final boolean flag10;
      private final Pose entityPose;
      private final boolean flag11;
      private final boolean flag12;
      private final boolean flag13;
      private final float floatValue2;
      private final long longValue;
      private final int intValue2;

      private Entry(
         RotationRecorderModule.Data2 data2,
         RotationRecorderModule.Data24 data24,
         RotationRecorderModule.Data10 data10,
         Vec3 vec3d,
         Vec3 vec3d2,
         AABB box,
         Input playerInput,
         boolean flag,
         boolean flag2,
         boolean sprinting,
         float floatValue,
         int intValue,
         boolean jumping,
         boolean fallFlying,
         boolean horizontalCollision,
         boolean verticalCollision,
         boolean inWater,
         boolean swimming,
         boolean underWater,
         Pose entityPose,
         boolean crouching,
         boolean visuallyCrawling,
         boolean flag3,
         float f,
         long longValue,
         int tickCount
      ) {
         this.data2 = data2;
         this.data24 = data24;
         this.data10 = data10;
         this.vec3d = vec3d;
         this.vec3d2 = vec3d2;
         this.box = box;
         this.playerInput = playerInput;
         this.flag = flag;
         this.flag2 = flag2;
         this.flag3 = sprinting;
         this.floatValue = floatValue;
         this.intValue = intValue;
         this.flag4 = jumping;
         this.flag5 = fallFlying;
         this.flag6 = horizontalCollision;
         this.flag7 = verticalCollision;
         this.flag8 = inWater;
         this.flag9 = swimming;
         this.flag10 = underWater;
         this.entityPose = entityPose;
         this.flag11 = crouching;
         this.flag12 = visuallyCrawling;
         this.flag13 = flag3;
         this.floatValue2 = f;
         this.longValue = longValue;
         this.intValue2 = tickCount;
      }
   }

   private static final class Entry10 {
      private final long longValue;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;
      private final int intValue7;

      private Entry10(long longValue, int intValue, int n, int n2, int n3, int n4, int n5, int n6) {
         this.longValue = longValue;
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = n3;
         this.intValue5 = n4;
         this.intValue6 = n5;
         this.intValue7 = n6;
      }
   }

   public static final class Entry2 {
      private final RotationRecorderModule rotationRecorderModule = new RotationRecorderModule();
      private int intValue = Integer.MIN_VALUE;
      private UUID uUID = null;
      private int intValue2 = Integer.MIN_VALUE;

      public float[] method4988(LivingEntity livingEntity, Rotation rotation, boolean flag) {
         return this.method4990(livingEntity, rotation, flag, false);
      }

      public float[] method4989(LivingEntity livingEntity, Rotation rotation, boolean flag) {
         return this.method4990(livingEntity, rotation, flag, true);
      }

      private float[] method4990(LivingEntity livingEntity, Rotation rotation, boolean flag, boolean flag2) {
         if (RotationRecorderModule.mc.player != null
            && RotationRecorderModule.mc.level != null
            && livingEntity != null
            && livingEntity.isAlive()
            && rotation != null
            && !rotation.isInvalid()) {
            if (livingEntity.getId() != this.intValue
               || !livingEntity.getUUID().equals(this.uUID)
               || this.intValue2 != Integer.MIN_VALUE && RotationRecorderModule.mc.player.tickCount != this.intValue2 + 1) {
               this.reset();
               this.intValue = livingEntity.getId();
               this.uUID = livingEntity.getUUID();
            }

            float floatValue = rotation.getFloatValue();
            float xRot = rotation.getFloatValue2();
            if (!this.rotationRecorderModule.flag4) {
               this.rotationRecorderModule.floatValue = floatValue;
               this.rotationRecorderModule.floatValue2 = xRot;
               this.rotationRecorderModule.flag4 = true;
            }

            float f = Mth.wrapDegrees(floatValue - this.rotationRecorderModule.floatValue);
            float f2 = xRot - this.rotationRecorderModule.floatValue2;
            Input keyPresses = this.rotationRecorderModule.method4970();
            AABB move = this.rotationRecorderModule.method4931(livingEntity);
            Vec3 vec3d = this.rotationRecorderModule.method4920(livingEntity);
            if (move != null && vec3d != null) {
               RotationRecorderModule.Entry3 entry3 = this.rotationRecorderModule.method4975(floatValue, xRot, keyPresses);
               Vec3 eyePosition = RotationRecorderModule.mc.player.getEyePosition();
               RotationRecorderModule.Entry entry = this.rotationRecorderModule
                  .method4955(
                     rotation, eyePosition, livingEntity, move, vec3d, f, f2, keyPresses, flag, false, null, entry3, this.rotationRecorderModule.method4919(rotation, eyePosition, keyPresses)
                  );
               this.rotationRecorderModule.floatValue = floatValue;
               this.rotationRecorderModule.floatValue2 = xRot;
               this.intValue2 = RotationRecorderModule.mc.player.tickCount;
               if (entry == null) {
                  return null;
               } else {
                  this.rotationRecorderModule.entry = entry;
                  return flag2 ? entry.data2.method4986(entry.data24.getData3()) : entry.data2.method4985(entry.data24.getData3());
               }
            } else {
               this.reset();
               return null;
            }
         } else {
            this.reset();
            return null;
         }
      }

      public void reset() {
         this.rotationRecorderModule.method4940();
         this.intValue = Integer.MIN_VALUE;
         this.uUID = null;
         this.intValue2 = Integer.MIN_VALUE;
      }
   }

   private static final class Entry3 {
      private final boolean flag;
      private final Vec3 vec3d;
      private final Vec3 vec3d2;
      private final Vec3 vec3d3;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;

      private Entry3(boolean flag, Vec3 vec3d, Vec3 vec3d2, Vec3 vec3d3, boolean flag2, boolean flag3, boolean flag4) {
         this.flag = flag;
         this.vec3d = vec3d;
         this.vec3d2 = vec3d2;
         this.vec3d3 = vec3d3;
         this.flag2 = flag2;
         this.flag3 = flag3;
         this.flag4 = flag4;
      }

      private static RotationRecorderModule.Entry3 method4998(Vec3 position, Vec3 deltaMovement) {
         return new RotationRecorderModule.Entry3(false, Vec3.ZERO, position, deltaMovement, false, false, false);
      }
   }

   private final class Entry4 {
      private final List<RotationRecorderModule.Entry5> items = new ArrayList<>();
      private final long longValue = RotationRecorderModule.this.method4954();

      private boolean method4999(RotationRecorderModule.Entry5 entry5) {
         if (entry5 != null && entry5.isValid()) {
            this.items.add(entry5);
            return true;
         } else {
            return false;
         }
      }

      private void method5000(int n) {
         int n2 = Math.max(0, this.items.size() - n);
         if (n2 <= 0) {
            this.items.clear();
            RotationRecorderModule.this.entry4 = null;
         } else {
            for (int i = 0; i < n2; i++) {
               RotationRecorderModule.this.method4951(this.longValue, this.items.get(i));
            }

            if (!RotationRecorderModule.this.disableLogs.isEnabled()) {
               ChatLogger.method2019("RotationRecorder: wrote " + n2);
            }

            this.items.clear();
            RotationRecorderModule.this.entry4 = null;
         }
      }

      private void method5001() {
         this.items.clear();
         RotationRecorderModule.this.entry4 = null;
      }
   }

   private static final class Entry5 {
      private static final int CONST_intValue = method5004();
      private static final int CONST_intValue2 = method5005();
      private final RotationRecorderModule.Data2 data2;
      private final RotationRecorderModule.Data24 data24;
      private final RotationRecorderModule.Data10 data10;
      private final RotationRecorderModule.Data23 data23;
      private final RotationRecorderModule.Data11 data11;
      private final boolean flag;
      private final long longValue;
      private final int intValue;
      private final float floatValue;

      private static void method5002(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data2.method4983(entry8);
         RotationRecorderModule.Data24.method5040(entry8);
         RotationRecorderModule.Data10.method5013(entry8);
         RotationRecorderModule.Data23.method5039(entry8);
         RotationRecorderModule.Data11.method5016(entry8);
      }

      private static void method5003(RotationRecorderModule.Entry8 entry8) {
         RotationRecorderModule.Data2.method4983(entry8);
         RotationRecorderModule.Data24.method5040(entry8);
         RotationRecorderModule.Data10.method5013(entry8);
         RotationRecorderModule.Data23.method5039(entry8);
         entry8.method5029("isAim");
         RotationRecorderModule.Data11.method5016(entry8);
      }

      private static int method5004() {
         RotationRecorderModule.Entry8 entry8 = new RotationRecorderModule.Entry8(new StringBuilder());
         method5002(entry8);
         return entry8.method5030();
      }

      private static int method5005() {
         RotationRecorderModule.Entry8 entry8 = new RotationRecorderModule.Entry8(new StringBuilder());
         method5003(entry8);
         return entry8.method5030();
      }

      private Entry5(
         RotationRecorderModule.Data2 data2,
         RotationRecorderModule.Data24 data24,
         RotationRecorderModule.Data10 data10,
         RotationRecorderModule.Data23 data23,
         RotationRecorderModule.Data11 data11,
         boolean flag,
         long longValue,
         int intValue,
         float floatValue
      ) {
         this.data2 = data2;
         this.data24 = data24;
         this.data10 = data10;
         this.data23 = data23;
         this.data11 = data11;
         this.flag = flag;
         this.longValue = longValue;
         this.intValue = intValue;
         this.floatValue = floatValue;
      }

      private static RotationRecorderModule.Entry5 method5006(
         RotationRecorderModule.Entry entry, RotationRecorderModule.Entry entry2, RotationRecorderModule.Entry3 entry3, boolean flag
      ) {
         boolean flag2 = flag || entry.flag2 || entry2.flag2 || !entry3.flag;
         boolean flag3 = !flag2 && method5009(entry) && method5009(entry2);
         return new RotationRecorderModule.Entry5(
            entry.data2.method4984(entry),
            entry.data24.method5041(entry.playerInput, entry3),
            entry.data10.method5014(entry.data10, entry.flag3, flag3),
            new RotationRecorderModule.Data23(flag2),
            new RotationRecorderModule.Data11(entry2.data2.getData13().getFloatValue(), entry2.data2.getData13().getFloatValue2()),
            entry2.flag13,
            entry.longValue,
            entry.intValue2,
            entry.floatValue2
         );
      }

      private void method5007(StringBuilder stringBuilder) {
         RotationRecorderModule.Entry6 entry6 = new RotationRecorderModule.Entry6(stringBuilder);
         this.data2.method4981(entry6);
         this.data24.method4981(entry6);
         this.data10.method4981(entry6);
         this.data23.method4981(entry6);
         this.data11.method4981(entry6);
         if (entry6.getIntValue() != CONST_intValue) {
            throw new IllegalStateException("CSV schema mismatch: header has " + CONST_intValue + " columns, data tick has " + entry6.getIntValue());
         }
      }

      private void method5008(StringBuilder stringBuilder) {
         RotationRecorderModule.Entry6 entry6 = new RotationRecorderModule.Entry6(stringBuilder);
         this.data2.method4981(entry6);
         this.data24.method4981(entry6);
         this.data10.method4981(entry6);
         this.data23.method4981(entry6);
         entry6.method5022(this.flag);
         this.data11.method4981(entry6);
         if (entry6.getIntValue() != CONST_intValue2) {
            throw new IllegalStateException("Aim CSV schema mismatch: header has " + CONST_intValue2 + " columns, data tick has " + entry6.getIntValue());
         }
      }

      private static boolean method5009(RotationRecorderModule.Entry entry) {
         return entry != null
            && !entry.flag2
            && !entry.flag5
            && !entry.flag8
            && !entry.flag9
            && !entry.flag10
            && !entry.flag6
            && !entry.flag11
            && !entry.flag12
            && !entry.playerInput.shift()
            && method5010(entry.entityPose)
            && entry.data10 != null
            && entry.data10.method5015(entry.flag3);
      }

      private static boolean method5010(Pose entityPose) {
         return entityPose == null || entityPose == Pose.STANDING || entityPose == Pose.CROUCHING;
      }

      private boolean isValid() {
         return this.data2 != null
            && this.data24 != null
            && this.data10 != null
            && this.data23 != null
            && this.data11 != null
            && this.floatValue > 0.0F
            && !Float.isNaN(this.floatValue)
            && !Float.isInfinite(this.floatValue)
            && this.data2.method4982()
            && this.data24.method4982()
            && this.data10.method4982()
            && this.data23.method4982()
            && this.data11.method4982();
      }
   }

   private static final class Entry6 {
      private final StringBuilder stringBuilder;
      private int intValue;

      private Entry6(StringBuilder stringBuilder) {
         this.stringBuilder = stringBuilder;
      }

      private void method5021(float... floatValue) {
         for (float f : floatValue) {
            this.stringBuilder.append(',').append(f);
            this.intValue++;
         }
      }

      private void method5022(boolean... flag) {
         for (boolean flag2 : flag) {
            this.stringBuilder.append(',').append(flag2 ? 1.0F : 0.0F);
            this.intValue++;
         }
      }

      private int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Entry7 {
      private final int intValue;
      private final UUID uUID;
      private int intValue2;
      private int intValue3;
      private int intValue4;

      private Entry7(int intValue, UUID uUID, int n, int tickCount, int n2) {
         this.intValue = intValue;
         this.uUID = uUID;
         this.intValue2 = n;
         this.intValue3 = tickCount;
         this.intValue4 = n2;
      }

      private boolean method5026(LivingEntity livingEntity) {
         return livingEntity != null && this.intValue == livingEntity.getId() && this.uUID.equals(livingEntity.getUUID());
      }
   }

   private static final class Entry8 {
      private final StringBuilder stringBuilder;
      private final Map<String, Boolean> items = new LinkedHashMap<>();

      private Entry8(StringBuilder stringBuilder) {
         this.stringBuilder = stringBuilder;
      }

      private void method5029(String... strings) {
         for (String str : strings) {
            if (str == null || str.isBlank()) {
               throw new IllegalArgumentException("CSV column name must not be blank");
            }

            if (this.items.put(str, Boolean.TRUE) != null) {
               throw new IllegalArgumentException("Duplicate CSV column: " + str);
            }

            this.stringBuilder.append(',').append(str);
         }
      }

      private int method5030() {
         return this.items.size();
      }
   }

   private static final class Entry9 {
      private final int intValue;
      private final UUID uUID;
      private final Deque<RotationRecorderModule.Entry> deque = new ArrayDeque<>();
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;
      private int intValue2;
      private int intValue3;
      private float floatValue9 = 50.0F;
      private float floatValue10 = 50.0F;
      private float floatValue11;
      private float floatValue12;

      private Entry9(LivingEntity livingEntity) {
         this.intValue = livingEntity.getId();
         this.uUID = livingEntity.getUUID();
      }

      private boolean method5031(LivingEntity livingEntity) {
         return livingEntity != null && this.intValue == livingEntity.getId() && this.uUID.equals(livingEntity.getUUID());
      }

      private void method5032(RotationRecorderModule.Entry entry) {
         this.deque.addLast(entry);

         while (this.deque.size() > 64) {
            this.deque.removeFirst();
         }
      }
   }

   private interface Handler {
      void method4981(RotationRecorderModule.Entry6 entry6);

      boolean method4982();
   }
}
