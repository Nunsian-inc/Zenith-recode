package org.zenith.client.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalBlock;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.PvpDetector;

@ModuleInfo(
   name = "EndLooter",
   category = ModuleCategory.PLAYER,
   description = "module.endlooter.description"
)
public class EndLooterModule extends Module {
   public static final EndLooterModule INSTANCE = new EndLooterModule();
   private final BooleanSetting useSpeed = new BooleanSetting("module.endLooter.useSpeed", "module.endLooter.useSpeed.desc", false);
   private final BooleanSetting useBrush = new BooleanSetting("module.endLooter.useBrush", "module.endLooter.useBrush.desc", true);
   private final SliderSetting scanRadius = new SliderSetting(
      "module.endLooter.scanRadius", 96.0F, 16.0F, 128.0F, 8.0F, "module.endLooter.scanRadius.desc", "b"
   );
   private final SliderSetting playerRadius = new SliderSetting(
      "module.endLooter.playerRadius", 16.0F, 0.0F, 32.0F, 1.0F, "module.endLooter.playerRadius.desc", "b"
   );
   private final BooleanSetting debug = new BooleanSetting("module.endLooter.debug", "module.endLooter.debug.desc", false);
   private final TextSetting homeAnarchy = new TextSetting(
      "module.endLooter.homeAnarchy", "module.endLooter.homeAnarchy.desc", "", "module.endLooter.homeAnarchy.hint"
   );
   private final TextSetting lootAnarchies = new TextSetting("module.endLooter.lootAnarchies", "module.endLooter.lootAnarchies.desc", "", "101, 102");
   private final TextSetting dungeonCommand = new TextSetting("module.endLooter.dungeonCommand", "module.endLooter.dungeonCommand.desc", "home", "home");
   private final GroupSetting anarchies = new GroupSetting(
      "module.endLooter.anarchies", "module.endLooter.anarchies.desc", this.homeAnarchy, this.lootAnarchies, this.dungeonCommand
   );
   private final List<Integer> items = new ArrayList<>();
   private String text;
   private final Map<BlockPos, Long> items2 = new HashMap<>();
   private Rotation rotation;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private boolean flag;
   private Boolean booleanValue;
   private Integer integer;
   private Boolean booleanValue2;
   private Double doubleValue;
   private Double doubleValue2;
   private final StopWatch stopWatch4 = new StopWatch();
   private static final long CONST_longValue = 6000L;
   private static final long CONST_longValue2 = 60000L;
   private static final long CONST_longValue3 = 650L;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 9;
   private EndLooterModule.Type type = EndLooterModule.Type.SAVE;
   private int intValue = 1;
   private boolean flag2;
   private List<BlockPos> items3 = new ArrayList<>();
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private int intValue2;
   private long longValue;
   private int intValue3;
   private int intValue4 = -1;

   @Override
   public List<Setting> getSettings() {
      return Zenith.method1502()
         ? List.of(this.anarchies, this.scanRadius, this.playerRadius, this.useBrush, this.useSpeed, this.debug)
         : List.of(this.anarchies, this.scanRadius, this.playerRadius, this.useBrush, this.useSpeed);
   }

   private EndLooterModule() {
   }

   public List<Integer> method3887() {
      String str = method3888(this.homeAnarchy);
      String str2 = method3888(this.lootAnarchies);
      String text = str + "|" + str2;
      if (this.text != null && this.text.equals(text)) {
         return this.items;
      } else {
         this.text = text;
         this.items.clear();
         int n;
         if ((n = method3889(str)) > 0) {
            this.items.add(n);
         }

         for (int n2 : method3890(str2)) {
            if (this.items.size() >= 10) {
               break;
            }

            if (!this.items.contains(n2)) {
               this.items.add(n2);
            }
         }

         return this.items;
      }
   }

   private static String method3888(TextSetting homeAnarchy) {
      return homeAnarchy.getValue() == null ? "" : homeAnarchy.getValue();
   }

   private static int method3889(String str) {
      var iterator = method3890(str).iterator();
      return iterator.hasNext() ? (Integer)iterator.next() : -1;
   }

   private static List<Integer> method3890(String str) {
      ArrayList<Integer> integers = new ArrayList<>();

      for (String str2 : str.split("[^0-9]+")) {
         if (!str2.isEmpty() && str2.length() <= 3) {
            try {
               int n;
               if ((n = Integer.parseInt(str2)) >= 1 && n <= 999 && !integers.contains(n)) {
                  integers.add(n);
               }
            } catch (NumberFormatException e) {
            }
         }
      }

      return integers;
   }

   private int method3891() {
      var integers = this.method3887();
      return (integers.isEmpty()) ? -1 : (Integer)integers.getFirst();
   }

   private boolean method3892() {
      return this.method3891() >= 0 && this.method3891() == PvpDetector.method2724();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabledRaw()) {
         if (mc.player != null && mc.level != null) {
            var integers = this.method3887();
            if (integers.isEmpty()) {
               int n;
               if ((n = PvpDetector.method2724()) < 0) {
                  this.notify("Заполни «Анархия дома» в настройках модуля");
                  this.setToggled(false);
                  return;
               }

               this.homeAnarchy.setValue(String.valueOf(n));
               this.text = null;
               this.notify("Анархия дома не задана — взял текущую: " + n);
            }

            this.intValue = 1;
            this.type = EndLooterModule.Type.COLLECTING;
            this.flag2 = false;
            this.method3912();
            this.items2.clear();
            this.items3.clear();
            this.notify("Shift + Пробел — быстрое выключение функции");
            Settings settings = BaritoneAPI.getSettings();
            if (!this.flag) {
               this.booleanValue = (Boolean)settings.avoidance.value;
               this.integer = (Integer)settings.maxFallHeightNoWater.value;
               this.booleanValue2 = (Boolean)settings.blockFreeLook.value;
               this.doubleValue = (Double)settings.randomLooking.value;
               this.doubleValue2 = (Double)settings.randomLooking113.value;
               this.flag = true;
            }

            settings.avoidance.value = Boolean.TRUE;
            settings.maxFallHeightNoWater.value = 256;
            settings.blockFreeLook.value = Boolean.TRUE;
            settings.randomLooking.value = 1.0;
            settings.randomLooking113.value = 1.0;
         } else {
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.flag) {
         Settings settings = BaritoneAPI.getSettings();
         settings.avoidance.value = this.booleanValue;
         settings.maxFallHeightNoWater.value = this.integer;
         settings.blockFreeLook.value = this.booleanValue2;
         settings.randomLooking.value = this.doubleValue;
         settings.randomLooking113.value = this.doubleValue2;
         this.flag = false;
      }

      this.method3940();
      this.method3945();
      this.rotation = null;
      this.method3912();
      this.intValue3 = 0;
      this.intValue4 = -1;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.gui.screen() instanceof DeathScreen && mc.player != null && mc.player.deathTime >= 5) {
         mc.player.respawn();
         this.flag2 = true;
      }

      if (mc.player != null && mc.level != null) {
         this.rotation = null;
         if (this.debug.isEnabled() && mc.player.tickCount % 20 == 0) {
            this.method3946();
         }

         if (mc.options.keyShift.isDown() && mc.options.keyJump.isDown()) {
            this.setToggled(false);
         } else if (!this.method3937()) {
            if (mc.player.tickCount >= 5) {
               if (PvpDetector.method2724() < 0) {
                  if (mc.player.tickCount % 100 == 0 && this.method3891() >= 0 && mc.player.tickCount > 300) {
                     mc.player.connection.sendCommand("an" + this.method3891());
                  }

                  this.type = EndLooterModule.Type.SAVE;
               } else {
                  switch (this.type) {
                     case SAVE:
                        this.method3894();
                        break;
                     case TAKE:
                        this.method3895();
                        break;
                     case COLLECTING:
                        this.method3896();
                        break;
                     case ESCAPE:
                        this.method3898();
                  }
               }
            }
         }
      } else {
         this.rotation = null;
      }
   }

   @EventTarget
   public void method3893(PreRotationEvent event) {
      Rotation rotation = this.rotation;
      if (rotation != null) {
         rotationManager.requestRotation(
            new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
            4,
            this
         );
      }
   }

   private void method3894() {
      if (this.method3891() >= 0 && PvpDetector.method2724() != this.method3891() && !PvpDetector.method2725() && mc.player.tickCount % 5 == 0 && mc.player.tickCount > 5) {
         mc.player.connection.sendCommand("an" + this.method3891());
      }

      if (this.method3892()) {
         this.method3925();
         this.method3914(this.method3931(), true, EndLooterModule.Type.TAKE);
      }
   }

   private void method3895() {
      if (this.flag2 && this.method3887().size() > 1) {
         if (++this.intValue >= this.method3887().size()) {
            this.intValue = 1;
         }

         this.flag2 = false;
      }

      if (this.method3892()) {
         this.method3914(this.method3929() || this.method3923(), false, EndLooterModule.Type.COLLECTING);
      }
   }

   private void method3896() {
      if (!this.method3899()) {
         if (this.intValue3 != 0) {
            if (this.method3941()) {
               this.method3945();
            }
         } else {
            var integers = this.method3887();
            if (integers.size() <= 1) {
               if (mc.player.tickCount % 20 == 0) {
                  this.notify("Анархии лутания не заданы — вписать через запятую в настройках функции");
               }
            } else {
               if (this.intValue >= integers.size()) {
                  this.intValue = 1;
               }

               int n = (Integer)integers.get(this.intValue);
               if (PvpDetector.method2724() != n) {
                  if (mc.player.tickCount % 10 == 0 && mc.player.tickCount > 10) {
                     mc.player.connection.sendCommand("an" + n);
                  }
               } else {
                  if (mc.player.tickCount > 5) {
                     this.method3897();
                  }
               }
            }
         }
      }
   }

   private void method3897() {
      MobEffectInstance effect = mc.player.getEffect(MobEffects.INVISIBILITY);
      boolean flag;
      if (!(flag = mc.player.hasEffect(MobEffects.GLOWING) || effect != null && effect.getDuration() >= 400)
         && effect == null
         && this.method3928() < 1
         && mc.player.tickCount % 5 == 0
         && !PvpDetector.method2725()) {
         this.type = EndLooterModule.Type.ESCAPE;
      } else if (!flag) {
         this.method3936(this::method3932);
      } else if (!this.method3902()) {
         if (mc.player.tickCount % 50 == 0) {
            mc.player.connection.sendCommand(method3888(this.dungeonCommand).replace("/", ""));
         }
      } else if (this.useSpeed.isEnabled() && mc.player.getEffect(MobEffects.SPEED) == null && this.method3926(this::method3933) >= 0) {
         this.method3936(this::method3933);
      } else {
         this.method3903();
      }
   }

   private void method3898() {
      if (this.method3892()) {
         this.type = EndLooterModule.Type.SAVE;
      } else if (PvpDetector.method2725()) {
         this.method3900();
      } else {
         this.type = EndLooterModule.Type.SAVE;
      }
   }

   private boolean method3899() {
      if (this.method3927() > 20 || mc.player.getFoodData().getFoodLevel() < 8) {
         this.type = EndLooterModule.Type.ESCAPE;
         return true;
      } else if (!PvpDetector.method2725() && this.method3927() > 8) {
         this.type = EndLooterModule.Type.ESCAPE;
         return true;
      } else {
         int n;
         if ((n = PvpDetector.method2726()) >= 0 && n < 7 && !this.method3942(14.0) && this.method3927() > 7) {
            this.type = EndLooterModule.Type.ESCAPE;
            return true;
         } else {
            return false;
         }
      }
   }

   private void method3900() {
      this.method3924();
      this.method3925();
      BlockPos pos = null;
      double d = -1.0;
      int y = mc.player.blockPosition().getY();

      for (byte i = 0; i < 360; i += 30) {
         int x = (int)(mc.player.getX() + Math.cos(Math.toRadians(i)) * 25.0);
         int z = (int)(mc.player.getZ() + Math.sin(Math.toRadians(i)) * 25.0);
         double d2;
         if ((d2 = this.method3901(x, z)) > d) {
            d = d2;
            pos = new BlockPos(x, y, z);
         }
      }

      this.method3944(pos);
   }

   private double method3901(int x, int z) {
      double d = Double.MAX_VALUE;

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity != mc.player && entity instanceof Player) {
            d = Math.min(d, Math.hypot(entity.getX() - x, entity.getZ() - z));
         }
      }

      return d;
   }

   private boolean method3902() {
      if (!mc.level.dimension().identifier().toString().equals("minecraft:the_end")) {
         return false;
      } else {
         BlockPos blockPosition = mc.player.blockPosition();

         for (int i = -3; i <= 1; i++) {
            for (int j = -4; j <= 4; j++) {
               for (int k = -4; k <= 4; k++) {
                  if (mc.level.getBlockState(blockPosition.offset(j, i, k)).is(Blocks.END_STONE)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private void method3903() {
      if (this.method3922()) {
         this.method3924();
      } else {
         long now = System.currentTimeMillis();
         this.items2.values().removeIf(l -> now > l);
         if (this.stopWatch3.method7546(350L)) {
            this.stopWatch3.reset();
            this.items3 = this.method3904();
         }

         BlockPos pos = this.method3905();
         if (pos != null) {
            this.stopWatch4.reset();
            this.method3906(pos, now);
         } else {
            this.blockPos = null;
            if (this.useBrush.isEnabled()) {
               BlockPos blockPos = this.method3907();
               if (blockPos != null) {
                  this.stopWatch4.reset();
                  this.method3908(blockPos);
                  return;
               }
            }

            this.blockPos2 = null;
            if (this.stopWatch4.method7546(30000L)) {
               this.stopWatch4.reset();
               this.notify("Ваз не видно — продолжаю обход данжа.");
            }

            this.method3909();
         }
      }
   }

   private List<BlockPos> method3904() {
      ArrayList<BlockPos> poses = new ArrayList<>();
      int n = (int)this.scanRadius.getValue3();
      int n2 = Math.max(1, (int)Math.ceil(n / 16.0));
      ChunkPos chunkPosition = mc.player.chunkPosition();

      for (int i = chunkPosition.x() - n2; i <= chunkPosition.x() + n2; i++) {
         for (int j = chunkPosition.z() - n2; j <= chunkPosition.z() + n2; j++) {
            if (mc.level.hasChunk(i, j)) {
               LevelChunk chunk = mc.level.getChunk(i, j);
               if (chunk != null) {
                  for (Entry entry : chunk.getBlockEntities().entrySet()) {
                     if (entry.getValue() instanceof DecoratedPotBlockEntity) {
                        BlockPos key = (BlockPos)entry.getKey();
                        if (!(mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(key)) > (double)n * n)) {
                           poses.add(key.immutable());
                        }
                     }
                  }
               }
            }
         }
      }

      return poses;
   }

   private BlockPos method3905() {
      if (this.blockPos != null && this.method3910(this.blockPos)) {
         boolean containsKey = this.items2.containsKey(this.blockPos);

         if (!containsKey && !this.stopWatch2.method7546(6000L)) {
            return this.blockPos;
         }
      }

      if (this.blockPos != null && this.stopWatch2.method7546(6000L)) {
         this.method3911(this.blockPos, "не приблизился за 6 секунд");
      }

      BlockPos pos = null;
      double d = Double.MAX_VALUE;

      for (BlockPos pos2 : this.items3) {
         boolean containsKey = this.items2.containsKey(pos2);

         double d2;
         if (!containsKey
            && this.method3910(pos2)
            && !this.method3943(pos2, this.playerRadius.getValue3())
            && (d2 = mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos2))) < d) {
            d = d2;
            pos = pos2;
         }
      }

      if (pos != null && !pos.equals(this.blockPos)) {
         this.blockPos = pos;
         this.intValue2 = 0;
         this.stopWatch2.reset();
      }

      return pos;
   }

   private void method3906(BlockPos pos, long now) {
      BlockHitResult blockHitResult = this.method3913(pos);
      if (blockHitResult == null) {
         this.method3944(pos);
      } else {
         Rotation rotation = Rotation.lookingAt(blockHitResult.getLocation(), mc.player.getEyePosition());
         this.rotation = rotation;
         if (!(rotationManager.getRotation2().angularDistanceTo(rotation) > 5.0F)) {
            if (now >= this.longValue) {
               if (this.intValue2 >= 2) {
                  this.method3911(pos, "не сломалась после разрешённых попыток");
               } else {
                  this.longValue = now + 650L + ThreadLocalRandom.current().nextLong(350L);
                  if (mc.gameMode.startDestroyBlock(pos, blockHitResult.getDirection())) {
                     this.intValue2++;
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }

   private BlockPos method3907() {
      if (this.blockPos2 != null && mc.level.getBlockState(this.blockPos2).is(Blocks.SUSPICIOUS_SAND) && !this.stopWatch2.method7546(6000L)) {
         return this.blockPos2;
      } else {
         int n = Math.min(24, (int)this.scanRadius.getValue3());
         BlockPos blockPosition = mc.player.blockPosition();
         MutableBlockPos mutablePos = new MutableBlockPos();
         BlockPos immutable = null;
         double d = Double.MAX_VALUE;
         int n2 = blockPosition.getX() - n >> 4;
         int n3 = blockPosition.getY() - 12 >> 4;
         int n4 = blockPosition.getZ() - n >> 4;
         int n5 = (blockPosition.getX() + n >> 4) - n2 + 1;
         int n6 = (blockPosition.getY() + 12 >> 4) - n3 + 1;
         int n7 = (blockPosition.getZ() + n >> 4) - n4 + 1;
         boolean[] flags = new boolean[n5 * n6 * n7];
         boolean flag = false;

         for (int i = 0; i < n5; i++) {
            for (int j = 0; j < n7; j++) {
               int n8 = n2 + i;
               int chunkZ = n4 + j;
               int chunkX = n8;
               if (mc.level.hasChunk(chunkX, chunkZ)) {
                  LevelChunk chunk = mc.level.getChunk(chunkX, chunkZ);
                  LevelChunkSection[] sections = chunk.getSections();

                  for (int k = 0; k < n6; k++) {
                     int sectionIndex;
                     if ((sectionIndex = chunk.getSectionIndex(n3 + k << 4)) >= 0 && sectionIndex < sections.length) {
                        LevelChunkSection levelChunkSection = sections[sectionIndex];
                        if (!levelChunkSection.hasOnlyAir() && levelChunkSection.maybeHas(blockState -> blockState.is(Blocks.SUSPICIOUS_SAND))) {
                           flags[(i * n6 + k) * n7 + j] = true;
                           flag = true;
                        }
                     }
                  }
               }
            }
         }

         for (int i = -n; flag && i <= n; i++) {
            for (int j = -12; j <= 12; j++) {
               for (int k = -n; k <= n; k++) {
                  mutablePos.set(blockPosition.getX() + i, blockPosition.getY() + j, blockPosition.getZ() + k);
                  if (flags[(((mutablePos.getX() >> 4) - n2) * n6 + ((mutablePos.getY() >> 4) - n3)) * n7 + ((mutablePos.getZ() >> 4) - n4)]
                     && mc.level.getBlockState(mutablePos).is(Blocks.SUSPICIOUS_SAND)) {
                     BlockPos pos = mutablePos.immutable();
                     boolean containsKey = this.items2.containsKey(pos);
                     double d2;
                     if (!containsKey && !this.method3943(pos, this.playerRadius.getValue3()) && (d2 = mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos))) < d) {
                        d = d2;
                        immutable = pos;
                     }
                  }
               }
            }
         }

         if (immutable != null && !immutable.equals(this.blockPos2)) {
            this.blockPos2 = immutable;
            this.stopWatch2.reset();
         }

         return immutable;
      }
   }

   private void method3908(BlockPos blockPos) {
      int selected;
      if ((selected = this.method3926(stack -> stack.is(Items.BRUSH))) < 0) {
         this.method3911(blockPos, "нет кисти археолога");
      } else {
         BlockHitResult blockHit = this.method3913(blockPos);
         if (blockHit == null) {
            this.method3944(blockPos);
         } else {
            if (selected < 9) {
               mc.player.getInventory().setSelectedSlot(selected);
            } else if (mc.player.tickCount % 10 == 0) {
               InventoryUtils.method904(selected, mc.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, false);
               return;
            }

            Rotation rotation = Rotation.lookingAt(blockHit.getLocation(), mc.player.getEyePosition());
            this.rotation = rotation;
            if (!(rotationManager.getRotation2().angularDistanceTo(rotation) > 5.0F)) {
               if (this.stopWatch2.method7546(15000L)) {
                  this.method3911(blockPos, "песок не чистится слишком долго");
               } else {
                  mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
                  org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
               }
            }
         }
      }
   }

   private void method3909() {
      if (mc.player.tickCount % 60 == 0) {
         double d = mc.player.tickCount / 60 * 2.4;
         this.method3944(mc.player.blockPosition().offset((int)(Math.cos(d) * 16.0), 0, (int)(Math.sin(d) * 16.0)));
      }
   }

   private boolean method3910(BlockPos blockPos) {
      return blockPos != null && mc.level.getBlockEntity(blockPos) instanceof DecoratedPotBlockEntity;
   }

   private void method3911(BlockPos blockPos, String str) {
      if (blockPos != null) {
         this.items2.put(blockPos, System.currentTimeMillis() + 60000L);
         if (this.debug.isEnabled()) {
            this.notify("Пропускаю " + blockPos.toShortString() + " — " + str);
         }

         this.method3912();
      }
   }

   private void method3912() {
      this.blockPos = null;
      this.blockPos2 = null;
      this.intValue2 = 0;
      this.stopWatch2.reset();
   }

   private BlockHitResult method3913(BlockPos pos) {
      Vec3 eyePosition = mc.player.getEyePosition();
      double d = mc.player.blockInteractionRange() - 0.2;
      Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(pos);
      if (eyePosition.distanceToSqr(atCenterOf) > d * d) {
         return null;
      } else {
         for (double x = -0.4; x <= 0.41; x += 0.4) {
            for (double y = -0.4; y <= 0.41; y += 0.4) {
               for (double z = -0.4; z <= 0.41; z += 0.4) {
                  Vec3 vec = atCenterOf.add(x, y, z);
                  if (!(eyePosition.distanceToSqr(vec) > d * d)) {
                     BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, vec, Block.COLLIDER, Fluid.NONE, mc.player));
                     if (clip.getBlockPos().equals(pos)) {
                        return clip;
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   private void method3914(boolean flag, boolean flag2, EndLooterModule.Type type) {
      if (!flag) {
         if (this.method3924()) {
            this.type = type;
         }
      } else {
         AbstractContainerScreen containerScreen = this.method3921();
         if (containerScreen != null) {
            if (flag2) {
               this.method3915(containerScreen);
            } else {
               this.method3916(containerScreen);
            }
         } else {
            this.method3920(this.method3919(flag2));
         }
      }
   }

   private void method3915(AbstractContainerScreen<?> containerScreen) {
      if (mc.player.tickCount % 2 == 0) {
         boolean flag = false;
         boolean flag2 = false;
         int n = 0;

         for (Slot slot : containerScreen.getMenu().slots) {
            if (n >= 4) {
               return;
            }

            ItemStack carried = slot.getItem();
            if (slot.container == mc.player.getInventory()
               && !carried.isEmpty()
               && (!this.useSpeed.isEnabled() || !this.method3933(carried))
               && !carried.is(Items.BRUSH)) {
               if (!flag && this.method3932(carried)) {
                  flag = true;
               } else if (!flag2 && carried.is(Items.GOLDEN_CARROT)) {
                  flag2 = true;
               } else {
                  this.method3918(containerScreen, slot, 0, ContainerInput.QUICK_MOVE);
                  n++;
               }
            }
         }
      }
   }

   private void method3916(AbstractContainerScreen<?> containerScreen) {
      if (mc.player.tickCount % 2 == 0) {
         ItemStack carried = containerScreen.getMenu().getCarried();
         if (!carried.isEmpty()) {
            java.util.function.Predicate<ItemStack> predicate = var1x -> var1x.isEmpty() || ItemStack.isSameItemSameComponents(var1x, carried);
            if (this.method3930(carried)) {
               this.method3918(containerScreen, this.method3917(containerScreen, true, predicate), 1, ContainerInput.PICKUP);
            } else {
               this.method3918(containerScreen, this.method3917(containerScreen, false, predicate), 0, ContainerInput.PICKUP);
            }
         } else {
            this.method3918(containerScreen, this.method3917(containerScreen, false, this::method3930), 0, ContainerInput.PICKUP);
         }
      }
   }

   private Slot method3917(AbstractContainerScreen<?> containerScreen, boolean flag, Predicate<ItemStack> predicate) {
      for (Slot slot : containerScreen.getMenu().slots) {
         if (slot.container == mc.player.getInventory() == flag && predicate.test(slot.getItem())) {
            return slot;
         }
      }

      return null;
   }

   private void method3918(AbstractContainerScreen<?> containerScreen, Slot slot, int buttonNum, ContainerInput containerInput) {
      if (slot != null) {
         mc.gameMode.handleContainerInput(containerScreen.getMenu().containerId, slot.index, buttonNum, containerInput, mc.player);
      }
   }

   private BlockPos method3919(boolean flag) {
      BlockPos blockPosition = mc.player.blockPosition();
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -4; i <= 4; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 4; k++) {
               mutablePos.set(blockPosition.getX() + i, blockPosition.getY() + j, blockPosition.getZ() + k);
               if (mc.level.getBlockState(mutablePos).is(Blocks.CHEST)
                  && mc.level.getBlockState(mutablePos.below()).is(Blocks.HOPPER) == flag
                  && mc.level.getBlockState(mutablePos.above()).isAir()) {
                  BlockPos immutable = mutablePos.immutable();
                  if (this.method3913(immutable) != null) {
                     return immutable;
                  }
               }
            }
         }
      }

      return null;
   }

   private void method3920(BlockPos pos) {
      if (pos != null && !this.method3922()) {
         BlockHitResult blockHit = this.method3913(pos);
         if (blockHit == null) {
            this.method3944(pos);
         } else {
            Rotation rotation = Rotation.lookingAt(blockHit.getLocation(), mc.player.getEyePosition());
            this.rotation = rotation;
            if (mc.player.tickCount % 2 == 0 && !(rotationManager.getRotation2().angularDistanceTo(rotation) > 5.0F)) {
               mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   private AbstractContainerScreen<?> method3921() {
      return mc.gui.screen() instanceof AbstractContainerScreen containerScreen
            && !(mc.gui.screen() instanceof InventoryScreen)
            && !(mc.player.containerMenu instanceof InventoryMenu)
         ? containerScreen
         : null;
   }

   private boolean method3922() {
      return this.method3921() != null;
   }

   private boolean method3923() {
      AbstractContainerScreen containerScreen = this.method3921();
      return containerScreen != null && !containerScreen.getMenu().getCarried().isEmpty();
   }

   private boolean method3924() {
      if (this.method3922() && mc.player.tickCount % 2 == 0) {
         mc.player.closeContainer();
      }

      return !this.method3922();
   }

   private void method3925() {
      if (!mc.player.getMainHandItem().isEmpty()) {
         for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).isEmpty()) {
               mc.player.getInventory().setSelectedSlot(i);
               return;
            }
         }
      }
   }

   private int method3926(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 36; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private int method3927() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (!mc.player.getInventory().getItem(i).isEmpty()) {
            n++;
         }
      }

      return n;
   }

   private int method3928() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (this.method3932(mc.player.getInventory().getItem(i))) {
            n++;
         }
      }

      if (this.method3932(mc.player.getOffhandItem())) {
         n++;
      }

      return n;
   }

   private boolean method3929() {
      return this.method3928() < 1
         || InventoryUtils.method920(Items.GOLDEN_CARROT) < 3
         || this.useBrush.isEnabled() && this.method3926(stack -> stack.is(Items.BRUSH)) < 0
         || this.useSpeed.isEnabled() && this.method3926(this::method3933) < 0;
   }

   private boolean method3930(ItemStack carried) {
      if (carried.isEmpty()) {
         return false;
      } else if (this.method3932(carried) && this.method3928() < 1) {
         return true;
      } else if (carried.is(Items.GOLDEN_CARROT) && InventoryUtils.method920(Items.GOLDEN_CARROT) < 3) {
         return true;
      } else {
         return this.useBrush.isEnabled() && carried.is(Items.BRUSH) && this.method3926(stack -> stack.is(Items.BRUSH)) < 0
            ? true
            : this.useSpeed.isEnabled() && this.method3933(carried) && this.method3926(this::method3933) < 0;
      }
   }

   private boolean method3931() {
      boolean flag = false;
      boolean flag2 = false;

      for (int i = 0; i < 36; i++) {
         ItemStack carried = mc.player.getInventory().getItem(i);
         if (!carried.isEmpty() && !carried.is(Items.BRUSH) && (!this.useSpeed.isEnabled() || !this.method3933(carried))) {
            if (!flag && this.method3932(carried)) {
               flag = true;
            } else {
               if (flag2 || !carried.is(Items.GOLDEN_CARROT)) {
                  return true;
               }

               flag2 = true;
            }
         }
      }

      return false;
   }

   private boolean method3932(ItemStack carried) {
      return this.method3934(carried, MobEffects.INVISIBILITY);
   }

   private boolean method3933(ItemStack carried) {
      return this.method3934(carried, MobEffects.SPEED);
   }

   private boolean method3934(ItemStack carried, Holder<MobEffect> holder) {
      if (!carried.is(Items.POTION)) {
         return false;
      } else {
         PotionContents potionContents = carried.get(DataComponents.POTION_CONTENTS);
         if (potionContents == null) {
            return false;
         } else {
            for (MobEffectInstance allEffect : potionContents.getAllEffects()) {
               if (allEffect.getEffect().equals(holder)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private boolean method3935() {
      return this.intValue3 != 0;
   }

   private void method3936(Predicate<ItemStack> predicate) {
      if (this.intValue3 == 0 && mc.player.tickCount > 20) {
         if (this.method3922()) {
            this.method3924();
         } else {
            Slot slot = InventoryUtils.method910(var1x -> var1x.container == mc.player.getInventory() && predicate.test(var1x.getItem()));
            if (slot != null) {
               this.method3945();
               this.intValue4 = slot.index;
               InventoryUtils.method900(slot, InteractionHand.OFF_HAND, false);
               this.intValue3 = 1;
               this.stopWatch.reset();
            }
         }
      }
   }

   private boolean method3937() {
      if (this.intValue3 == 0) {
         return false;
      } else if (this.intValue3 == 3) {
         return this.method3939();
      } else if (this.method3922()) {
         this.method3938();
         return true;
      } else {
         this.method3945();
         if (this.intValue3 == 1) {
            if (!this.stopWatch.method7546(150L)) {
               return true;
            }

            this.intValue3 = 2;
            this.stopWatch.reset();
         }

         mc.options.keyShift.setDown(true);
         mc.options.keyUse.setDown(true);
         if (this.stopWatch.method7546(2200L)) {
            this.method3938();
         }

         return true;
      }
   }

   private void method3938() {
      this.method3940();
      this.intValue3 = this.intValue4 >= 0 ? 3 : 0;
      this.stopWatch.reset();
   }

   private boolean method3939() {
      if (this.method3922()) {
         this.method3924();
         return true;
      } else if (!mc.player.getOffhandItem().isEmpty() && !this.stopWatch.method7546(3000L)) {
         if (this.stopWatch.method7546(100L)) {
            InventoryUtils.method904(this.intValue4, 40, ContainerInput.SWAP, false);
            this.intValue4 = -1;
            this.intValue3 = 0;
            this.stopWatch.reset();
         }

         return true;
      } else {
         this.intValue4 = -1;
         this.intValue3 = 0;
         this.stopWatch.reset();
         return false;
      }
   }

   private void method3940() {
      mc.options.keyUse.setDown(false);
      mc.options.keyShift.setDown(false);
   }

   private boolean method3941() {
      return mc.player.getDeltaMovement().horizontalDistanceSqr() > 0.0025;
   }

   private boolean method3942(double d) {
      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof Player player && player != mc.player && mc.player.distanceToSqr(player) < d * d) {
            return true;
         }
      }

      return false;
   }

   private boolean method3943(BlockPos pos, double d) {
      if (d <= 0.0) {
         return false;
      } else {
         for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof Player player && player != mc.player && player.position().distanceToSqr(Vec3.atCenterOf(pos)) < d * d) {
               return true;
            }
         }

         return false;
      }
   }

   private void method3944(BlockPos pos) {
      if (pos != null) {
         if (mc.player.tickCount % 10 == 0 || !this.method3941() && mc.player.tickCount % 5 == 0) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
         }
      }
   }

   private void method3945() {
      if (BaritoneAPI.getProvider() != null) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      }
   }

   private void method3946() {
      this.notify(
         "состояние "
            + this.type
            + " | анархия "
            + PvpDetector.method2724()
            + ", нужна "
            + this.method3891()
            + " (список: "
            + this.method3887().size()
            + ") | в данже "
            + this.method3902()
            + " | ваз "
            + this.items3.size()
            + " | в чёрном списке "
            + this.items2.size()
            + " | пвп "
            + PvpDetector.method2725()
            + " ("
            + PvpDetector.method2726()
            + ")"
      );
      this.notify(
         "цель "
            + (this.blockPos == null ? "нет" : this.blockPos.toShortString())
            + " | песок "
            + (this.blockPos2 == null ? "нет" : this.blockPos2.toShortString())
            + " | лут "
            + this.method3931()
            + " | кисть "
            + (this.method3926(stack -> stack.is(Items.BRUSH)) >= 0)
      );
   }

   private void notify(String str) {
      ChatLogger.method2019("End Looter: " + str);
   }

   private static enum Type {
      SAVE,
      TAKE,
      COLLECTING,
      ESCAPE;
   }
}
