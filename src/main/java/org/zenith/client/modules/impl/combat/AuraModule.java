package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.base.rotation.mods.Class24;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SendPacketEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.movement.WallBypassModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.AttackUtils;
import org.zenith.utility.game.AuraRandomizer;
import org.zenith.utility.game.AuraUtil;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationConfig;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "Aura",
   category = ModuleCategory.COMBAT,
   description = "module.aura.description"
)
public final class AuraModule extends Module {
   public static final AuraModule INSTANCE = new AuraModule();
   private final GroupSetting safeTarget = new GroupSetting(
      "module.aura.targetSettingWindow",
      "module.aura.targetWindow.desc",
      () -> true,
      MultiSelectSetting.method5224(
         "module.aura.targetTypeSetting",
         "module.aura.targetTypeSetting.desc",
         List.of("module.aura.targetPlayers", "module.aura.noarmor", "module.aura.targetHostile", "module.aura.targetPeaceful")
      ),
      MultiSelectSetting.method5224(
         "module.aura.targetSortSetting",
         "module.aura.targetSortSetting.desc",
         List.of("module.aura.targetFov", "module.aura.targetArmor", "module.aura.targetHp", "module.aura.targetDistance")
      ),
      new BooleanSetting("module.aura.safeTarget", "module.aura.safeTarget.desc", true)
   );
   private final SliderSetting distance = new SliderSetting("module.aura.distance", 3.0F, 0.5F, 6.0F, 0.1F, "module.aura.distance.desc", "b");
   private final SliderSetting distanceRotation = new SliderSetting(
      "module.aura.distanceRotation", 0.1F, 0.0F, 6.0F, 0.1F, "module.aura.distanceRotation.desc", "b"
   );
   private final BooleanSetting onlyCrit = new BooleanSetting("module.aura.onlyCrit", "module.aura.onlyCrit.desc", true);
   private final BooleanSetting smartCrit = new BooleanSetting("module.aura.smartCrit", "module.aura.smartCrit.desc", false, this.onlyCrit::isEnabled);
   private final ModeSetting cooldownMode = new ModeSetting("module.aura.cooldownMode", "module.aura.cooldownMode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.cooldownMode, "module.aura.slowMode");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.cooldownMode, "module.aura.speedMode").method2103();
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.cooldownMode, "module.aura.customMode");
   private final SliderSetting timeCooldown = new SliderSetting(
      "module.aura.timeCooldown", 0.0F, -1.0F, 1.0F, 0.5F, "module.aura.timeCooldown.desc", "t", this.entry3::isSelected, null
   );
   private final SliderSetting attackCooldown = new SliderSetting(
      "module.aura.attackCooldown", 1.0F, 0.0F, 1.0F, 0.01F, "module.aura.attackCooldown.desc", "%", this.entry3::isSelected, null
   );
   private final BooleanSetting tpsSynch = new BooleanSetting("module.aura.tpsSynch", "module.aura.tpsSynch.desc", false);
   private final ModeSetting rotationMode = new ModeSetting("module.aura.rotationMode", "module.aura.rotationMode.desc");
   private final ModeSetting.Entry entry4 = new ModeSetting.Entry(this.rotationMode, "HolyWorld").method2103();
   private final ModeSetting.Entry entry5 = new ModeSetting.Entry(this.rotationMode, "FunTime");
   private final ModeSetting.Entry entry6 = new ModeSetting.Entry(this.rotationMode, "SpokyTime");
   private final ModeSetting.Entry entry7 = new ModeSetting.Entry(this.rotationMode, "module.aura.legit");
   private final ModeSetting.Entry entry8 = new ModeSetting.Entry(this.rotationMode, "module.aura.snap");
   private final ModeSetting.Entry entry9 = new ModeSetting.Entry(this.rotationMode, "module.aura.hvh");
   private final ModeSetting.Entry entry10 = new ModeSetting.Entry(this.rotationMode, "ReallyWorld");
   private final ModeSetting.Entry entry11 = new ModeSetting.Entry(this.rotationMode, "NeuroBC");
   private final ModeSetting.Entry entry12 = new ModeSetting.Entry(this.rotationMode, "NeuroBC-2");
   private final ModeSetting.Entry entry13 = new ModeSetting.Entry(this.rotationMode, "TestNerv");
   private final SliderSetting sliderSetting = new SliderSetting(
      "TestNerv удержание", 50.0F, 0.0F, 100.0F, 5.0F, "Доля времени прицела на цели (0 — слабый, 100 — профи)", "%", this::method6360
   );
   private final ModeSetting.Entry entry14 = new ModeSetting.Entry(this.rotationMode, "NeuroBC-Hand");
   private final ModeSetting legitMode = new ModeSetting("module.aura.legitMode", "module.aura.legitMode.desc", this.entry7::isSelected);
   private final ModeSetting.Entry entry15 = new ModeSetting.Entry(this.legitMode, "module.aura.legitSnap").method2103();
   private final ModeSetting.Entry entry16 = new ModeSetting.Entry(this.legitMode, "module.aura.legitJerk");
   private final ModeSetting.Entry entry17 = new ModeSetting.Entry(this.legitMode, "module.aura.legitConstant");
   private final SliderSetting legitMaxAngle = new SliderSetting(
      "module.aura.legitMaxAngle", 5.0F, 0.5F, 180.0F, 0.5F, "module.aura.legitMaxAngle.desc", "°", this.entry7::isSelected, null
   );
   private final ModeSetting sprintMode = new ModeSetting("module.aura.sprintMode", "module.aura.sprintMode.desc");
   private final ModeSetting.Entry entry18 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintHvh");
   private final ModeSetting.Entry entry19 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNormal").method2103();
   private final ModeSetting.Entry entry20 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintLegit");
   private final ModeSetting.Entry entry21 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintSuperLegit");
   private final ModeSetting.Entry entry22 = new ModeSetting.Entry(this.sprintMode, "module.aura.sprintNone");
   private final ModeSetting correction = new ModeSetting("module.aura.correction", "module.aura.correction.desc");
   private final ModeSetting.Entry entry23 = new ModeSetting.Entry(this.correction, "module.aura.correctionFocus");
   private final ModeSetting.Entry entry24 = new ModeSetting.Entry(this.correction, "module.aura.correctionGood").method2103();
   private final ModeSetting.Entry entry25 = new ModeSetting.Entry(this.correction, "module.aura.correctionNone");
   private final MultiSelectSetting settings = new MultiSelectSetting("module.aura.settings", "module.aura.settings.desc");
   private final MultiSelectSetting.Entry entry26 = new MultiSelectSetting.Entry(this.settings, "module.aura.shieldBreak", true);
   private final MultiSelectSetting.Entry entry27 = new MultiSelectSetting.Entry(this.settings, "module.aura.shielRealese", true);
   private final MultiSelectSetting.Entry entry28 = new MultiSelectSetting.Entry(this.settings, "module.aura.eatUseAttack", true);
   private final MultiSelectSetting.Entry entry29 = new MultiSelectSetting.Entry(this.settings, "module.aura.attackIgnoreWals", true);
   private final ModeSetting wallBypassMode = new ModeSetting(
      "module.aura.wallBypassMode",
      "module.aura.wallBypassMode.desc",
      this.entry29::isEnabled,
      "module.aura.wallBypassMode.realyworld",
      "module.aura.wallBypassMode.none"
   );
   private final BooleanSetting wallbypass = new BooleanSetting("module.aura.wallbypass", "module.aura.wallbypass.desc", true, () -> !this.entry29.isEnabled());
   private final AuraRandomizer auraRandomizer = new AuraRandomizer();
   private LivingEntity livingEntity = null;
   private int intValue = -1;
   private final List<Entity> items = new ArrayList<>();
   private LivingEntity livingEntity2 = null;
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private boolean flag4 = false;
   public int intValue2 = 0;

   private AuraModule() {
   }

   @EventTarget
   public void method6313(EntityAddEvent event) {
      if (event.getEntity() instanceof LocalPlayer) {
         this.livingEntity = null;
         this.livingEntity2 = null;
         this.flag4 = false;
      }
   }

   @EventTarget
   public void method6314(SendPacketEvent event) {
   }

   @EventTarget
   public void method6315(Event2 event) {
      if (this.livingEntity != null) {
         if (this.method6316()
            && zenith.getRotationManager().method7844().method3131(new Rotation(mc.player.yRotLast, mc.player.xRotLast)).method5329(0.0F)) {
            zenith.getRotationManager()
               .setCurrentRotation(new Rotation((float)(mc.player.yRotLast + MathUtils.method2651(-1.0, 1.0)), mc.player.xRotLast));
         }

         if (this.flag2 && this.method6321()) {
            this.flag = false;
            this.flag2 = false;
            this.flag4 = true;
         }
      }
   }

   public boolean method6316() {
      return false;
   }

   @EventTarget
   public void method6317(PostMotionEvent event) {
      if (this.livingEntity != null
         && !this.method6321()
         && this.method6316()
         && mc.player != null
         && this.livingEntity != null
         && (this.flag4 || this.flag2 || this.flag)) {
         Vec3 vec = mc.player.position().subtract(this.livingEntity.position());
         if (vec.horizontalDistanceSqr() > 1.0E-5) {
            Rotation rotation = rotationManager.method7844();
            float floatValue = Rotation.method3129(vec).getFloatValue();
            UseItemUtils.method6533(0.0, new Rotation(rotation.getFloatValue() + Mth.wrapDegrees(floatValue - rotation.getFloatValue()), rotation.getFloatValue2()));
         } else {
            Rotation rotation = rotationManager.method7844();
            UseItemUtils.method6533(0.0, new Rotation(rotation.getFloatValue() + 179.0F, rotation.getFloatValue2()));
         }
      }
   }

   @EventTarget
   public void method6318(UpdateEvent event) {
      if (mc.player.isDeadOrDying() || this.livingEntity == null) {
         this.flag4 = false;
      }

      if (this.flag4 && this.livingEntity != null && !mc.player.isDeadOrDying()) {
         if (this.entry18.isSelected() && Zenith.getInstance().getServerDetector().isFlag()) {
            mc.player.setSprinting(false);
            mc.player.sendIsSprintingIfNeeded();
         }

         this.method6328();
         AttackUtils.method816(this.livingEntity);
      }
   }

   @EventTarget
   public void method6319(Event5 event) {
      this.flag3 = false;
      this.flag4 = false;
      if (mc.player.isDeadOrDying()) {
         this.livingEntity = null;
         bcCameraTestnerv.getClass24().method3537();
      } else {
         this.livingEntity = this.method6356();
         if (this.livingEntity != null) {
            if (mc.level.getEntity(this.livingEntity.getId()) instanceof LivingEntity livingEntity) {
               this.livingEntity = livingEntity;
            } else {
               this.livingEntity = null;
            }

            this.livingEntity2 = this.livingEntity;
         }

         this.flag = false;
         this.flag2 = false;
         if (this.livingEntity == null) {
            bcCameraTestnerv.getClass24().method3537();
         } else {
            this.flag = this.method6341();
            this.flag2 = this.method6344();
            Rotation rotation = this.method6334();
            int selected = InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).is(net.minecraft.tags.ItemTags.AXES));
            boolean flag;
            if ((
                  flag = this.entry26.isEnabled()
                     && this.stopWatch.method7546(300L)
                     && Zenith.getInstance().getTaskScheduler().method5116()
                     && selected != -1
                     && UseItemUtils.method6530(this.livingEntity)
               )
               && mc.player.getMainHandItem().is(net.minecraft.tags.ItemTags.AXES)
               && this.method6321()) {
               if (this.entry27.isEnabled() && mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof ShieldItem) {
                  mc.gameMode.releaseUsingItem(mc.player);
                  this.flag3 = true;
               }

               this.method6328();
               AttackUtils.method816(this.livingEntity);
               this.stopWatch.reset();
               this.flag = false;
               this.flag2 = false;
            }

            this.method6337(rotation, this.flag || flag, this.flag2 || flag);
            if (flag) {
               if (!(mc.player.getMainHandItem().is(net.minecraft.tags.ItemTags.AXES))) {
                  if (this.intValue == -1) {
                     this.intValue = mc.player.getInventory().getSelectedSlot();
                  }

                  mc.player.getInventory().setSelectedSlot(selected);
               }
            } else if (this.intValue != -1 && this.entry26.isEnabled() && !UseItemUtils.method6530(this.livingEntity)) {
               mc.player.getInventory().setSelectedSlot(this.intValue);
               this.intValue = -1;
            }

            if (this.flag && this.entry27.isEnabled() && mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof ShieldItem) {
               mc.gameMode.releaseUsingItem(mc.player);
               this.flag3 = true;
            }
         }
      }
   }

   public RotationConfig method6320() {
      return bcCameraTestnerv.getRotationConfig();
   }

   public boolean method6321() {
      return this.method6322(rotationManager.method7844());
   }

   public boolean method6322(Rotation rotation) {
      return this.method6325(rotation, this.method6333(), this.livingEntity.getBoundingBox()) || this.method6325(rotation, this.method6333(), this.method6336());
   }

   public boolean method6323() {
      return TargetUtils.method3212(
         zenith.getRotationManager().method7844(),
         PlayerSimulation.method6031(1).vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0),
         this.method6324(),
         6.0,
         false
      );
   }

   private AABB method6324() {
      return this.livingEntity instanceof Player player ? PlayerSimulation.method6032(player, 1).box : this.livingEntity.getBoundingBox();
   }

   public boolean method6325(Rotation rotation, Vec3 vec3d, AABB boundingBox) {
      return this.method6326() ? this.method6332(boundingBox, vec3d) : TargetUtils.method3212(rotation, vec3d, boundingBox, this.method6330(), !this.method6327());
   }

   private boolean method6326() {
      return WallBypassModule.INSTANCE.method7501();
   }

   private boolean method6327() {
      return this.entry29.isEnabled() || this.method6326();
   }

   private void method6328() {
      if (this.livingEntity != null
         && this.entry29.isEnabled()
         && this.wallBypassMode.method2101("module.aura.wallBypassMode.realyworld")
         && !mc.player.hasLineOfSight(this.livingEntity)) {
         this.method6329();
      }
   }

   private void method6329() {
      if (this.livingEntity != null) {
         Vec3 eyePosition = mc.player.getEyePosition();
         Vec3 vec = this.livingEntity.getEyePosition();
         Vec3 vec2 = vec.subtract(eyePosition);
         double length;
         if (!((length = vec2.length()) < 0.01)) {
            int n = Math.max(1, (int)Math.ceil(length * 2.0));
            HashSet hashSet = new HashSet();

            for (int i = 0; i <= n; i++) {
               double d = (double)i / n;
               BlockPos containing = BlockPos.containing(eyePosition.x + vec2.x * d, eyePosition.y + vec2.y * d, eyePosition.z + vec2.z * d);
               if (hashSet.add(containing) && !mc.level.getBlockState(containing).isAir()) {
                  Vec3 vec3 = eyePosition.subtract(net.minecraft.world.phys.Vec3.atCenterOf(containing));
                  double d2 = Math.abs(vec3.x);
                  double d3 = Math.abs(vec3.y);
                  double d4 = Math.abs(vec3.z);
                  Direction direction = d3 >= d2 && d3 >= d4
                     ? (vec3.y > 0.0 ? Direction.UP : Direction.DOWN)
                     : (d2 >= d4 ? (vec3.x > 0.0 ? Direction.EAST : Direction.WEST) : (vec3.z > 0.0 ? Direction.SOUTH : Direction.NORTH));
                  mc.player.connection.send(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, containing, direction, 0));
                  mc.player.connection.send(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, containing, direction, 0));
               }
            }
         }
      }
   }

   private float method6330() {
      return this.method6326() ? WallBypassModule.INSTANCE.method7502() : this.distance.getValue3();
   }

   public boolean method6331(LivingEntity livingEntity) {
      return AuraUtil.method7447(((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(), livingEntity);
   }

   public boolean method6332(AABB boundingBox, Vec3 vec3d) {
      Vec3 vec = new Vec3(
         Mth.clamp(vec3d.x, boundingBox.minX, boundingBox.maxX), Mth.clamp(vec3d.y, boundingBox.minY, boundingBox.maxY), Mth.clamp(vec3d.z, boundingBox.minZ, boundingBox.maxZ)
      );
      return vec.closerThan(vec3d, this.method6330());
   }

   public Vec3 method6333() {
      return mc.player.getEyePosition(1.0F);
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100()) {
         Packet packet3 = event.getPacket3();
         if (packet3 instanceof ClientboundPlayerPositionPacket) {
            this.intValue2 = 3;
         }
      }
   }

   public Rotation method6334() {
      Vec3 eyePosition = mc.player.getEyePosition(1.0F);
      if (this.livingEntity instanceof Player player) {
         AABB boundingBox = ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d() != null
            ? this.livingEntity.dimensions.makeBoundingBox(ReachV3Module.INSTANCE.getVec3d())
            : PlayerSimulation.method6032(player, 1).box;
         Rotation rotation = RotationUtils.method6912(
            this.method6335(boundingBox, 0.5F).subtract(PlayerSimulation.method6031(1).vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0))
         );
         if (this.method6322(rotation) || !this.flag2) {
            return rotation;
         }
      }

      Vec3 vec = this.auraRandomizer.method4673(eyePosition, this.method6336(), this.method6330(), new Vec3(0.0, 0.0, 0.0), this.method6327());
      return RotationUtils.method6912(vec.subtract(eyePosition));
   }

   public Vec3 method6335(AABB boundingBox, float alpha1) {
      return new Vec3(
         Mth.lerp(0.5, boundingBox.minX, boundingBox.maxX), Mth.lerp((double)alpha1, boundingBox.minY, boundingBox.maxY), Mth.lerp(0.5, boundingBox.minZ, boundingBox.maxZ)
      );
   }

   public AABB method6336() {
      LivingEntity livingEntity = this.livingEntity;
      return livingEntity instanceof Player ? this.livingEntity.getBoundingBox() : this.livingEntity.getBoundingBox();
   }

   public void method6337(Rotation rotation, boolean flag, boolean flag2) {
      if (this.entry7.isSelected()) {
         this.method6338(flag2);
      } else {
         bcCameraTestnerv.getClass24().method3537();
         if (this.entry14.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(
                  rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig16(), rotation), bcCameraTestnerv.getRotationConfig16()
               ),
               1,
               this
            );
         } else if (this.method6360()) {
            rotationManager.method7849(
               new RotationRequest(
                  rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig15(), rotation), bcCameraTestnerv.getRotationConfig15()
               ),
               1,
               this
            );
         } else if (this.entry12.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(
                  rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig14(), rotation), bcCameraTestnerv.getRotationConfig14()
               ),
               1,
               this
            );
         } else if (this.entry11.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(
                  rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig13(), rotation), bcCameraTestnerv.getRotationConfig13()
               ),
               1,
               this
            );
         } else if (this.entry6.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig2(), rotation), bcCameraTestnerv.getRotationConfig2()),
               1,
               this
            );
         } else if (this.entry4.isSelected()) {
            rotationManager.method7849(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig4(), rotation), bcCameraTestnerv.getRotationConfig4()),
               1,
               this
            );
         } else {
            if (flag2 && this.entry8.isSelected() || this.entry9.isSelected() || this.entry10.isSelected()) {
               rotationManager.method7849(
                  new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
                  flag2 ? 3 : 3,
                  this
               );
            }
         }
      }
   }

   private void method6338(boolean flag) {
      boolean selected = this.entry17.isSelected();
      Class24 class2 = bcCameraTestnerv.getClass24();
      Class24.Data data;
      if (selected) {
         data = class2.method3536(
            new Rotation(mc.player.getYRot(), mc.player.getXRot(), true),
            rotationManager.method7844(),
            this.method6333(),
            this.method6336(),
            this.legitMaxAngle.getValue3(),
            this.livingEntity.getId(),
            mc.player.tickCount
         );
      } else {
         class2.method3537();
         data = class2.method3535(rotationManager.method7844(), this.method6333(), this.method6336(), this.legitMaxAngle.getValue3(), false);
      }

      if (data.isFlag()) {
         if (this.entry15.isSelected()) {
            if (!flag) {
               return;
            }
         } else if (!this.entry16.isSelected() && !selected) {
            return;
         }

         if (selected || data.isFlag3()) {
            int priority = this.entry15.isSelected() ? 3 : 1;
            rotationManager.method7849(new RotationRequest(data.rotation(), data::rotation, bcCameraTestnerv.getRotationConfig()), priority, this);
         }
      }
   }

   @EventTarget
   public void method6339(MoveInputEvent event) {
      if (this.entry23.isSelected() && this.livingEntity != null) {
         PlayerSimulation playerSimulation = PlayerSimulation.method6031(1);
         AABB boundingBox = ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d2() != null
            ? this.livingEntity.dimensions.makeBoundingBox(ReachV3Module.INSTANCE.getVec3d2())
            : (this.livingEntity instanceof Player player ? PlayerSimulation.method6032(player, 1).box : this.livingEntity.getBoundingBox());
         Rotation rotation = RotationUtils.method6912(boundingBox.getCenter().subtract(playerSimulation.vec3d));
         MovementInputUtils.method7426(event, rotationManager.method7844().getFloatValue(), rotation.getFloatValue());
      } else if (this.entry24.isSelected() || this.livingEntity == null) {
         MovementInputUtils.method7426(event, rotationManager.method7844().getFloatValue(), mc.player.getYRot());
      }

      if (this.livingEntity != null) {
         this.method6353(event);
      }
   }

   @EventTarget(0)
   public void method6340(Event9 event) {
      if (this.flag4 || this.flag3) {
         event.cancel();
      }
   }

   private boolean method6341() {
      return this.method6342(1);
   }

   public boolean method6342(int n) {
      PlayerSimulation playerSimulation = PlayerSimulation.method6031(n);
      if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (CrystalAuraModule.INSTANCE.method5745(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem()
         && (!this.entry27.isEnabled() || !(mc.player.getUseItem().getItem() instanceof ShieldItem))
         && !this.entry28.isEnabled()) {
         return false;
      } else if (AutoMaceModule.INSTANCE.method309(this.livingEntity) != AutoMaceModule.Type.NONE) {
         return AutoMaceModule.INSTANCE.getIntValue() <= n + 1;
      } else {
         return !this.onlyCrit.isEnabled()
               || AttackUtils.method818(playerSimulation)
               || AttackUtils.method822(playerSimulation)
               || this.smartCrit.isEnabled() && !mc.options.keyJump.isDown()
            ? this.method6345(1 + n, !this.method6343())
            : false;
      }
   }

   public boolean method6343() {
      if (mc.player.isFallFlying() || AttackUtils.method817()) {
         return false;
      } else {
         return !this.onlyCrit.isEnabled() ? false : !this.smartCrit.isEnabled() || mc.options.keyJump.isDown();
      }
   }

   private boolean method6344() {
      if (this.intValue2 > 0) {
         this.intValue2--;
         return false;
      } else if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem() && !this.entry28.isEnabled()) {
         return false;
      } else if (CrystalAuraModule.INSTANCE.method5745(this.livingEntity)) {
         return false;
      } else {
         AutoMaceModule.Type type = AutoMaceModule.INSTANCE.method309(this.livingEntity);
         if (type == AutoMaceModule.Type.HIT) {
            return true;
         } else if (type == AutoMaceModule.Type.HOLD) {
            return false;
         } else {
            return !this.method6343() || AttackUtils.method819() && (!mc.player.wasSprinting || this.entry22.isSelected() || this.entry18.isSelected())
               ? this.method6345(0.0F, !this.method6343() || this.flag)
               : false;
         }
      }
   }

   public boolean method6345(float f, boolean flag) {
      if (this.tpsSynch.isEnabled()) {
         return CriticalsModule.INSTANCE.method4667()
            ? CriticalsModule.INSTANCE.method4668(f)
            : Zenith.getInstance().getServerDetector().getCriticalsState().method1708(this.method6346(), f);
      } else if (this.cooldownMode.isSelected(1)) {
         return !AttackUtils.method817() && !mc.player.onGround()
            ? mc.player.getAttackStrengthScale(0.5F) > 0.9
            : mc.player.getAttackStrengthScale(0.5F) >= 1.0F;
      } else {
         if (!this.entry.isSelected()) {
            flag = false;
         }

         float f2 = this.entry3.isSelected() ? this.timeCooldown.getValue3() : 1.0F;
         float f3 = this.entry3.isSelected() ? this.attackCooldown.getValue3() : 1.0F;
         return flag && !mc.player.isUnderWater()
            ? mc.player.getAttackStrengthScale(0.0F + f) == 1.0F
            : mc.player.getAttackStrengthScale(f2 + f) >= f3;
      }
   }

   public double method6346() {
      if (mc.player == null) {
         return 0.0;
      } else {
         double attributeValue;
         if (!Double.isFinite(attributeValue = mc.player.getAttributeValue(Attributes.ATTACK_SPEED)) || attributeValue <= 0.0) {
            attributeValue = 4.0;
         }

         double d = 20.0 / attributeValue;
         double d2;
         double nextUp;
         if (this.cooldownMode.isSelected(1)) {
            d2 = 0.5;
            nextUp = Math.nextUp(0.9F);
         } else {
            d2 = this.entry3.isSelected() ? this.timeCooldown.getValue3() : 1.0;
            nextUp = this.entry3.isSelected() ? this.attackCooldown.getValue3() : 1.0;
         }

         return Math.max(0.0, d * nextUp - d2);
      }
   }

   public boolean method6347() {
      return this.tpsSynch.isEnabled();
   }

   public boolean method6348() {
      if (mc.player == null || mc.level == null || this.livingEntity == null || this.intValue2 > 0) {
         return false;
      } else if (UseItemUtils.method6530(this.livingEntity)) {
         return false;
      } else if (mc.player.isUsingItem() && !this.entry28.isEnabled()) {
         return false;
      } else {
         return !this.method6343() || AttackUtils.method819() && (!mc.player.wasSprinting || this.entry22.isSelected() || this.entry18.isSelected())
            ? this.method6321()
            : false;
      }
   }

   public int method6349(int n, boolean flag) {
      AABB boundingBox = this.method6351();
      if (boundingBox == null) {
         return -1;
      } else {
         int n2 = Mth.clamp(n, 1, 20);

         for (int i = 1; i <= n2; i++) {
            PlayerSimulation playerSimulation = PlayerSimulation.method6031(i);
            if (this.method6352(playerSimulation, boundingBox, flag)) {
               return i;
            }
         }

         return -1;
      }
   }

   public int method6350(int n) {
      if (mc.player != null && !mc.player.onGround() && !(mc.player.fallDistance <= 0.0)) {
         AABB boundingBox = this.method6351();
         if (boundingBox == null) {
            return -1;
         } else {
            int n2 = 0;
            int n3 = Mth.clamp(n, 1, 20);

            for (int i = 1; i <= n3; n2 = i++) {
               PlayerSimulation playerSimulation = PlayerSimulation.method6031(i);
               if (!this.method6352(playerSimulation, boundingBox, true)) {
                  break;
               }
            }

            return n2 > 0 ? n2 : -1;
         }
      } else {
         return -1;
      }
   }

   private AABB method6351() {
      return mc.player != null
            && mc.level != null
            && this.livingEntity != null
            && !UseItemUtils.method6530(this.livingEntity)
            && (!mc.player.isUsingItem() || this.entry28.isEnabled())
            && (this.method6327() || mc.player.hasLineOfSight(this.livingEntity))
         ? this.method6324()
         : null;
   }

   private boolean method6352(PlayerSimulation playerSimulation, AABB boundingBox, boolean flag) {
      if (!flag || !playerSimulation.flag4 && !(playerSimulation.floatValue3 <= 0.0F)) {
         if (!this.method6343() || AttackUtils.method822(playerSimulation) && !playerSimulation.flag4) {
            Vec3 vec3d = playerSimulation.vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
            return this.method6332(boundingBox, vec3d);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.intValue != -1 && mc.player != null) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.intValue = -1;
      this.flag4 = false;
      this.livingEntity = null;
      super.onDisable();
   }

   public void method6353(MoveInputEvent event) {
      if (this.method6355()) {
         boolean down = mc.options.keySprint.isDown();
         boolean flag = mc.options.keyUp.isDown();
         if (this.entry21.isSelected()) {
            if (!mc.player.onGround() && (!(mc.player.fallDistance > 0.0) || this.method6345(0.0F, false))) {
               down = false;
               if (mc.player.isSprinting()) {
                  flag = false;
               }
            }
         } else if (!this.flag4 && !mc.player.onGround() && this.method6341()) {
            if (this.entry20.isSelected()) {
               down = false;
               if (mc.player.isSprinting()) {
                  flag = false;
               }
            }

            if (this.entry19.isSelected()) {
               if (mc.player.isSprinting()) {
                  mc.player.setSprinting(false);
               }

               down = false;
            }
         }

         if (!down) {
            mc.options.keySprint.setDown(false);
            event.method3039(false);
         }

         if (!flag) {
            event.method3038(false);
         }
      }
   }

   @EventTarget
   public void method6354(SprintUpdateEvent event) {
      if (this.livingEntity != null && !this.flag4 && !mc.player.onGround() && this.method6341() && this.entry19.isSelected()) {
         event.setFlag2(false);
      }
   }

   public boolean method6355() {
      return !this.entry22.isSelected() && !AttackUtils.method817();
   }

   private LivingEntity method6356() {
      float f = this.method6330() + this.distanceRotation.getValue3();
      LivingEntity livingEntity = AuraUtil.method7446(
         this.method6357(f),
         f,
         this.method6327(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(0)).method5229(),
         ((MultiSelectSetting)this.safeTarget.getSettings().get(1)).method5229(),
         ((BooleanSetting)this.safeTarget.getSettings().get(2)).isEnabled()
      );
      this.items.clear();
      return livingEntity;
   }

   private List<Entity> method6357(float f) {
      this.items.clear();
      Vec3 eyePosition = mc.player.getEyePosition();
      double d = (f + 0.01) * (f + 0.01);

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof LivingEntity && entity.getBoundingBox().distanceToSqr(eyePosition) <= d) {
            this.items.add(entity);
         }
      }

      return this.items;
   }

   public LivingEntity method6358() {
      return this.isEnabled() ? this.livingEntity : null;
   }

   public boolean method6359(int intValue) {
      return intValue == 4 ? this.method6360() : (intValue == 3 ? this.entry14.isSelected() : (intValue == 2 ? this.entry12.isSelected() : this.entry11.isSelected()));
   }

   private boolean method6360() {
      return this.entry13.isSelected() || this.entry5.isSelected();
   }

   public double method6361() {
      return this.sliderSetting.getValue3() / 100.0;
   }

   public boolean method6362(BlockHitResult blockHitResult) {
      if (blockHitResult != null && this.wallbypass.isEnabled()) {
         BlockState blockState = mc.level.getBlockState(blockHitResult.getBlockPos());
         return !(blockState.getBlock() instanceof StairBlock)
            && blockState.getBlock() != Blocks.KELP
            && blockState.getBlock() != Blocks.KELP_PLANT
            && blockState.getBlock() != Blocks.TALL_SEAGRASS
            && blockState.getBlock() != Blocks.TALL_GRASS
            && !(blockState.getBlock() instanceof GrassBlock)
            && !(blockState.getBlock() instanceof TrapDoorBlock)
            && blockState.getBlock() != Blocks.COBWEB
            && !(blockState.getBlock() instanceof DoorBlock);
      } else {
         return true;
      }
   }

   @Generated
   public LivingEntity getLivingEntity22() {
      return this.livingEntity2;
   }

   @Generated
   public boolean isFlag9() {
      return this.flag;
   }

   @Generated
   public boolean isFlag26() {
      return this.flag2;
   }

   public static class Entry {
      private double doubleValue;
      private double doubleValue2;
      private double doubleValue3;
      private int ticks;

      public Entry(double doubleValue, double d, double d2) {
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
      }

      public boolean method6363() {
         return this.ticks++ > 2;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }
   }
}
