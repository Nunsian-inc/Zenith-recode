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
import org.zenith.client.events.Event5;
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
      String var1 = method3888(this.homeAnarchy);
      String var2 = method3888(this.lootAnarchies);
      String var3 = var1 + "|" + var2;
      if (this.text != null && this.text.equals(var3)) {
         return this.items;
      } else {
         this.text = var3;
         this.items.clear();
         int var4;
         if ((var4 = method3889(var1)) > 0) {
            this.items.add(var4);
         }

         for (int var6 : method3890(var2)) {
            if (this.items.size() >= 10) {
               break;
            }

            if (!this.items.contains(var6)) {
               this.items.add(var6);
            }
         }

         return this.items;
      }
   }

   private static String method3888(TextSetting var0) {
      return var0.getValue() == null ? "" : var0.getValue();
   }

   private static int method3889(String var0) {
      var var1 = method3890(var0).iterator();
      return var1.hasNext() ? (Integer)var1.next() : -1;
   }

   private static List<Integer> method3890(String var0) {
      ArrayList<Integer> var1 = new ArrayList<>();

      for (String var5 : var0.split("[^0-9]+")) {
         if (!var5.isEmpty() && var5.length() <= 3) {
            try {
               int var6;
               if ((var6 = Integer.parseInt(var5)) >= 1 && var6 <= 999 && !var1.contains(var6)) {
                  var1.add(var6);
               }
            } catch (NumberFormatException var7) {
            }
         }
      }

      return var1;
   }

   private int method3891() {
      var var1 = this.method3887();
      return (var1.isEmpty()) ? -1 : (Integer)var1.getFirst();
   }

   private boolean method3892() {
      return this.method3891() >= 0 && this.method3891() == PvpDetector.method2724();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabledRaw()) {
         if (mc.player != null && mc.level != null) {
            var var10000 = this.method3887();
            if (var10000.isEmpty()) {
               int var1;
               if ((var1 = PvpDetector.method2724()) < 0) {
                  this.notify("Заполни «Анархия дома» в настройках модуля");
                  this.setToggled(false);
                  return;
               }

               this.homeAnarchy.setValue(String.valueOf(var1));
               this.text = null;
               this.notify("Анархия дома не задана — взял текущую: " + var1);
            }

            this.intValue = 1;
            this.type = EndLooterModule.Type.COLLECTING;
            this.flag2 = false;
            this.method3912();
            this.items2.clear();
            this.items3.clear();
            this.notify("Shift + Пробел — быстрое выключение функции");
            Settings var2 = BaritoneAPI.getSettings();
            if (!this.flag) {
               this.booleanValue = (Boolean)var2.avoidance.value;
               this.integer = (Integer)var2.maxFallHeightNoWater.value;
               this.booleanValue2 = (Boolean)var2.blockFreeLook.value;
               this.doubleValue = (Double)var2.randomLooking.value;
               this.doubleValue2 = (Double)var2.randomLooking113.value;
               this.flag = true;
            }

            var2.avoidance.value = Boolean.TRUE;
            var2.maxFallHeightNoWater.value = 256;
            var2.blockFreeLook.value = Boolean.TRUE;
            var2.randomLooking.value = 1.0;
            var2.randomLooking113.value = 1.0;
         } else {
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.flag) {
         Settings var1 = BaritoneAPI.getSettings();
         var1.avoidance.value = this.booleanValue;
         var1.maxFallHeightNoWater.value = this.integer;
         var1.blockFreeLook.value = this.booleanValue2;
         var1.randomLooking.value = this.doubleValue;
         var1.randomLooking113.value = this.doubleValue2;
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
   public void onUpdate(UpdateEvent var1) {
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
   public void method3893(Event5 var1) {
      Rotation var2 = this.rotation;
      if (var2 != null) {
         rotationManager.method7849(
            new RotationRequest(var2, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var2), bcCameraTestnerv.getRotationConfig()),
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
            var var1 = this.method3887();
            if (var1.size() <= 1) {
               if (mc.player.tickCount % 20 == 0) {
                  this.notify("Анархии лутания не заданы — вписать через запятую в настройках функции");
               }
            } else {
               if (this.intValue >= var1.size()) {
                  this.intValue = 1;
               }

               int var2 = (Integer)var1.get(this.intValue);
               if (PvpDetector.method2724() != var2) {
                  if (mc.player.tickCount % 10 == 0 && mc.player.tickCount > 10) {
                     mc.player.connection.sendCommand("an" + var2);
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
      MobEffectInstance var1 = mc.player.getEffect(MobEffects.INVISIBILITY);
      boolean var2;
      if (!(var2 = mc.player.hasEffect(MobEffects.GLOWING) || var1 != null && var1.getDuration() >= 400)
         && var1 == null
         && this.method3928() < 1
         && mc.player.tickCount % 5 == 0
         && !PvpDetector.method2725()) {
         this.type = EndLooterModule.Type.ESCAPE;
      } else if (!var2) {
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
         int var1;
         if ((var1 = PvpDetector.method2726()) >= 0 && var1 < 7 && !this.method3942(14.0) && this.method3927() > 7) {
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
      BlockPos var1 = null;
      double var2 = -1.0;
      int var4 = mc.player.blockPosition().getY();

      for (byte var5 = 0; var5 < 360; var5 += 30) {
         int var6 = (int)(mc.player.getX() + Math.cos(Math.toRadians(var5)) * 25.0);
         int var7 = (int)(mc.player.getZ() + Math.sin(Math.toRadians(var5)) * 25.0);
         double var8;
         if ((var8 = this.method3901(var6, var7)) > var2) {
            var2 = var8;
            var1 = new BlockPos(var6, var4, var7);
         }
      }

      this.method3944(var1);
   }

   private double method3901(int var1, int var2) {
      double var3 = Double.MAX_VALUE;

      for (Entity var6 : mc.level.entitiesForRendering()) {
         if (var6 != mc.player && var6 instanceof Player) {
            var3 = Math.min(var3, Math.hypot(var6.getX() - var1, var6.getZ() - var2));
         }
      }

      return var3;
   }

   private boolean method3902() {
      if (!mc.level.dimension().identifier().toString().equals("minecraft:the_end")) {
         return false;
      } else {
         BlockPos var1 = mc.player.blockPosition();

         for (int var2 = -3; var2 <= 1; var2++) {
            for (int var3 = -4; var3 <= 4; var3++) {
               for (int var4 = -4; var4 <= 4; var4++) {
                  if (mc.level.getBlockState(var1.offset(var3, var2, var4)).is(Blocks.END_STONE)) {
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
         long var1 = System.currentTimeMillis();
         this.items2.values().removeIf(var2 -> var1 > var2);
         if (this.stopWatch3.method7546(350L)) {
            this.stopWatch3.reset();
            this.items3 = this.method3904();
         }

         BlockPos var3 = this.method3905();
         if (var3 != null) {
            this.stopWatch4.reset();
            this.method3906(var3, var1);
         } else {
            this.blockPos = null;
            if (this.useBrush.isEnabled()) {
               BlockPos var4 = this.method3907();
               if (var4 != null) {
                  this.stopWatch4.reset();
                  this.method3908(var4);
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
      ArrayList<BlockPos> var1 = new ArrayList<>();
      int var2 = (int)this.scanRadius.getValue3();
      int var3 = Math.max(1, (int)Math.ceil(var2 / 16.0));
      ChunkPos var4 = mc.player.chunkPosition();

      for (int var5 = var4.x() - var3; var5 <= var4.x() + var3; var5++) {
         for (int var6 = var4.z() - var3; var6 <= var4.z() + var3; var6++) {
            if (mc.level.hasChunk(var5, var6)) {
               LevelChunk var7 = mc.level.getChunk(var5, var6);
               if (var7 != null) {
                  for (Entry var9 : var7.getBlockEntities().entrySet()) {
                     if (var9.getValue() instanceof DecoratedPotBlockEntity) {
                        BlockPos var10 = (BlockPos)var9.getKey();
                        if (!(mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(var10)) > (double)var2 * var2)) {
                           var1.add(var10.immutable());
                        }
                     }
                  }
               }
            }
         }
      }

      return var1;
   }

   private BlockPos method3905() {
      if (this.blockPos != null && this.method3910(this.blockPos)) {
         boolean var10000 = this.items2.containsKey(this.blockPos);

         if (!var10000 && !this.stopWatch2.method7546(6000L)) {
            return this.blockPos;
         }
      }

      if (this.blockPos != null && this.stopWatch2.method7546(6000L)) {
         this.method3911(this.blockPos, "не приблизился за 6 секунд");
      }

      BlockPos var1 = null;
      double var2 = Double.MAX_VALUE;

      for (BlockPos var5 : this.items3) {
         boolean var8 = this.items2.containsKey(var5);

         double var6;
         if (!var8
            && this.method3910(var5)
            && !this.method3943(var5, this.playerRadius.getValue3())
            && (var6 = mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(var5))) < var2) {
            var2 = var6;
            var1 = var5;
         }
      }

      if (var1 != null && !var1.equals(this.blockPos)) {
         this.blockPos = var1;
         this.intValue2 = 0;
         this.stopWatch2.reset();
      }

      return var1;
   }

   private void method3906(BlockPos var1, long var2) {
      BlockHitResult var4 = this.method3913(var1);
      if (var4 == null) {
         this.method3944(var1);
      } else {
         Rotation var5 = Rotation.method3128(var4.getLocation(), mc.player.getEyePosition());
         this.rotation = var5;
         if (!(rotationManager.getRotation2().method3130(var5) > 5.0F)) {
            if (var2 >= this.longValue) {
               if (this.intValue2 >= 2) {
                  this.method3911(var1, "не сломалась после разрешённых попыток");
               } else {
                  this.longValue = var2 + 650L + ThreadLocalRandom.current().nextLong(350L);
                  if (mc.gameMode.startDestroyBlock(var1, var4.getDirection())) {
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
         int var1 = Math.min(24, (int)this.scanRadius.getValue3());
         BlockPos var2 = mc.player.blockPosition();
         MutableBlockPos var3 = new MutableBlockPos();
         BlockPos var4 = null;
         double var5 = Double.MAX_VALUE;
         int var7 = var2.getX() - var1 >> 4;
         int var8 = var2.getY() - 12 >> 4;
         int var9 = var2.getZ() - var1 >> 4;
         int var10 = (var2.getX() + var1 >> 4) - var7 + 1;
         int var11 = (var2.getY() + 12 >> 4) - var8 + 1;
         int var12 = (var2.getZ() + var1 >> 4) - var9 + 1;
         boolean[] var13 = new boolean[var10 * var11 * var12];
         boolean var14 = false;

         for (int var15 = 0; var15 < var10; var15++) {
            for (int var16 = 0; var16 < var12; var16++) {
               int var10000 = var7 + var15;
               int var18 = var9 + var16;
               int var17 = var10000;
               if (mc.level.hasChunk(var17, var18)) {
                  LevelChunk var19 = mc.level.getChunk(var17, var18);
                  LevelChunkSection[] var20 = var19.getSections();

                  for (int var21 = 0; var21 < var11; var21++) {
                     int var22;
                     if ((var22 = var19.getSectionIndex(var8 + var21 << 4)) >= 0 && var22 < var20.length) {
                        LevelChunkSection var23 = var20[var22];
                        if (!var23.hasOnlyAir() && var23.maybeHas(var0 -> var0.is(Blocks.SUSPICIOUS_SAND))) {
                           var13[(var15 * var11 + var21) * var12 + var16] = true;
                           var14 = true;
                        }
                     }
                  }
               }
            }
         }

         for (int var24 = -var1; var14 && var24 <= var1; var24++) {
            for (int var25 = -12; var25 <= 12; var25++) {
               for (int var26 = -var1; var26 <= var1; var26++) {
                  var3.set(var2.getX() + var24, var2.getY() + var25, var2.getZ() + var26);
                  if (var13[(((var3.getX() >> 4) - var7) * var11 + ((var3.getY() >> 4) - var8)) * var12 + ((var3.getZ() >> 4) - var9)]
                     && mc.level.getBlockState(var3).is(Blocks.SUSPICIOUS_SAND)) {
                     BlockPos var27 = var3.immutable();
                     boolean var29 = this.items2.containsKey(var27);
                     double var28;
                     if (!var29 && !this.method3943(var27, this.playerRadius.getValue3()) && (var28 = mc.player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(var27))) < var5) {
                        var5 = var28;
                        var4 = var27;
                     }
                  }
               }
            }
         }

         if (var4 != null && !var4.equals(this.blockPos2)) {
            this.blockPos2 = var4;
            this.stopWatch2.reset();
         }

         return var4;
      }
   }

   private void method3908(BlockPos var1) {
      int var2;
      if ((var2 = this.method3926(var0 -> var0.is(Items.BRUSH))) < 0) {
         this.method3911(var1, "нет кисти археолога");
      } else {
         BlockHitResult var3 = this.method3913(var1);
         if (var3 == null) {
            this.method3944(var1);
         } else {
            if (var2 < 9) {
               mc.player.getInventory().setSelectedSlot(var2);
            } else if (mc.player.tickCount % 10 == 0) {
               InventoryUtils.method904(var2, mc.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, false);
               return;
            }

            Rotation var4 = Rotation.method3128(var3.getLocation(), mc.player.getEyePosition());
            this.rotation = var4;
            if (!(rotationManager.getRotation2().method3130(var4) > 5.0F)) {
               if (this.stopWatch2.method7546(15000L)) {
                  this.method3911(var1, "песок не чистится слишком долго");
               } else {
                  mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, var3);
                  org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
               }
            }
         }
      }
   }

   private void method3909() {
      if (mc.player.tickCount % 60 == 0) {
         double var1 = mc.player.tickCount / 60 * 2.4;
         this.method3944(mc.player.blockPosition().offset((int)(Math.cos(var1) * 16.0), 0, (int)(Math.sin(var1) * 16.0)));
      }
   }

   private boolean method3910(BlockPos var1) {
      return var1 != null && mc.level.getBlockEntity(var1) instanceof DecoratedPotBlockEntity;
   }

   private void method3911(BlockPos var1, String var2) {
      if (var1 != null) {
         this.items2.put(var1, System.currentTimeMillis() + 60000L);
         if (this.debug.isEnabled()) {
            this.notify("Пропускаю " + var1.toShortString() + " — " + var2);
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

   private BlockHitResult method3913(BlockPos var1) {
      Vec3 var2 = mc.player.getEyePosition();
      double var3 = mc.player.blockInteractionRange() - 0.2;
      Vec3 var5 = net.minecraft.world.phys.Vec3.atCenterOf(var1);
      if (var2.distanceToSqr(var5) > var3 * var3) {
         return null;
      } else {
         for (double var6 = -0.4; var6 <= 0.41; var6 += 0.4) {
            for (double var8 = -0.4; var8 <= 0.41; var8 += 0.4) {
               for (double var10 = -0.4; var10 <= 0.41; var10 += 0.4) {
                  Vec3 var12 = var5.add(var6, var8, var10);
                  if (!(var2.distanceToSqr(var12) > var3 * var3)) {
                     BlockHitResult var13 = mc.level.clip(new ClipContext(var2, var12, Block.COLLIDER, Fluid.NONE, mc.player));
                     if (var13.getBlockPos().equals(var1)) {
                        return var13;
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   private void method3914(boolean var1, boolean var2, EndLooterModule.Type var3) {
      if (!var1) {
         if (this.method3924()) {
            this.type = var3;
         }
      } else {
         AbstractContainerScreen var4 = this.method3921();
         if (var4 != null) {
            if (var2) {
               this.method3915(var4);
            } else {
               this.method3916(var4);
            }
         } else {
            this.method3920(this.method3919(var2));
         }
      }
   }

   private void method3915(AbstractContainerScreen<?> var1) {
      if (mc.player.tickCount % 2 == 0) {
         boolean var2 = false;
         boolean var3 = false;
         int var4 = 0;

         for (Slot var6 : var1.getMenu().slots) {
            if (var4 >= 4) {
               return;
            }

            ItemStack var7 = var6.getItem();
            if (var6.container == mc.player.getInventory()
               && !var7.isEmpty()
               && (!this.useSpeed.isEnabled() || !this.method3933(var7))
               && !var7.is(Items.BRUSH)) {
               if (!var2 && this.method3932(var7)) {
                  var2 = true;
               } else if (!var3 && var7.is(Items.GOLDEN_CARROT)) {
                  var3 = true;
               } else {
                  this.method3918(var1, var6, 0, ContainerInput.QUICK_MOVE);
                  var4++;
               }
            }
         }
      }
   }

   private void method3916(AbstractContainerScreen<?> var1) {
      if (mc.player.tickCount % 2 == 0) {
         ItemStack var2 = var1.getMenu().getCarried();
         if (!var2.isEmpty()) {
            java.util.function.Predicate<ItemStack> var3 = var1x -> var1x.isEmpty() || ItemStack.isSameItemSameComponents(var1x, var2);
            if (this.method3930(var2)) {
               this.method3918(var1, this.method3917(var1, true, var3), 1, ContainerInput.PICKUP);
            } else {
               this.method3918(var1, this.method3917(var1, false, var3), 0, ContainerInput.PICKUP);
            }
         } else {
            this.method3918(var1, this.method3917(var1, false, this::method3930), 0, ContainerInput.PICKUP);
         }
      }
   }

   private Slot method3917(AbstractContainerScreen<?> var1, boolean var2, Predicate<ItemStack> var3) {
      for (Slot var5 : var1.getMenu().slots) {
         if (var5.container == mc.player.getInventory() == var2 && var3.test(var5.getItem())) {
            return var5;
         }
      }

      return null;
   }

   private void method3918(AbstractContainerScreen<?> var1, Slot var2, int var3, ContainerInput var4) {
      if (var2 != null) {
         mc.gameMode.handleContainerInput(var1.getMenu().containerId, var2.index, var3, var4, mc.player);
      }
   }

   private BlockPos method3919(boolean var1) {
      BlockPos var2 = mc.player.blockPosition();
      MutableBlockPos var3 = new MutableBlockPos();

      for (int var4 = -4; var4 <= 4; var4++) {
         for (int var5 = -4; var5 <= 4; var5++) {
            for (int var6 = -4; var6 <= 4; var6++) {
               var3.set(var2.getX() + var4, var2.getY() + var5, var2.getZ() + var6);
               if (mc.level.getBlockState(var3).is(Blocks.CHEST)
                  && mc.level.getBlockState(var3.below()).is(Blocks.HOPPER) == var1
                  && mc.level.getBlockState(var3.above()).isAir()) {
                  BlockPos var7 = var3.immutable();
                  if (this.method3913(var7) != null) {
                     return var7;
                  }
               }
            }
         }
      }

      return null;
   }

   private void method3920(BlockPos var1) {
      if (var1 != null && !this.method3922()) {
         BlockHitResult var2 = this.method3913(var1);
         if (var2 == null) {
            this.method3944(var1);
         } else {
            Rotation var3 = Rotation.method3128(var2.getLocation(), mc.player.getEyePosition());
            this.rotation = var3;
            if (mc.player.tickCount % 2 == 0 && !(rotationManager.getRotation2().method3130(var3) > 5.0F)) {
               mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, var2);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   private AbstractContainerScreen<?> method3921() {
      return mc.gui.screen() instanceof AbstractContainerScreen var1
            && !(mc.gui.screen() instanceof InventoryScreen)
            && !(mc.player.containerMenu instanceof InventoryMenu)
         ? var1
         : null;
   }

   private boolean method3922() {
      return this.method3921() != null;
   }

   private boolean method3923() {
      AbstractContainerScreen var1 = this.method3921();
      return var1 != null && !var1.getMenu().getCarried().isEmpty();
   }

   private boolean method3924() {
      if (this.method3922() && mc.player.tickCount % 2 == 0) {
         mc.player.closeContainer();
      }

      return !this.method3922();
   }

   private void method3925() {
      if (!mc.player.getMainHandItem().isEmpty()) {
         for (int var1 = 0; var1 < 9; var1++) {
            if (mc.player.getInventory().getItem(var1).isEmpty()) {
               mc.player.getInventory().setSelectedSlot(var1);
               return;
            }
         }
      }
   }

   private int method3926(Predicate<ItemStack> var1) {
      for (int var2 = 0; var2 < 36; var2++) {
         if (var1.test(mc.player.getInventory().getItem(var2))) {
            return var2;
         }
      }

      return -1;
   }

   private int method3927() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (!mc.player.getInventory().getItem(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private int method3928() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (this.method3932(mc.player.getInventory().getItem(var2))) {
            var1++;
         }
      }

      if (this.method3932(mc.player.getOffhandItem())) {
         var1++;
      }

      return var1;
   }

   private boolean method3929() {
      return this.method3928() < 1
         || InventoryUtils.method920(Items.GOLDEN_CARROT) < 3
         || this.useBrush.isEnabled() && this.method3926(var0 -> var0.is(Items.BRUSH)) < 0
         || this.useSpeed.isEnabled() && this.method3926(this::method3933) < 0;
   }

   private boolean method3930(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else if (this.method3932(var1) && this.method3928() < 1) {
         return true;
      } else if (var1.is(Items.GOLDEN_CARROT) && InventoryUtils.method920(Items.GOLDEN_CARROT) < 3) {
         return true;
      } else {
         return this.useBrush.isEnabled() && var1.is(Items.BRUSH) && this.method3926(var0 -> var0.is(Items.BRUSH)) < 0
            ? true
            : this.useSpeed.isEnabled() && this.method3933(var1) && this.method3926(this::method3933) < 0;
      }
   }

   private boolean method3931() {
      boolean var1 = false;
      boolean var2 = false;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = mc.player.getInventory().getItem(var3);
         if (!var4.isEmpty() && !var4.is(Items.BRUSH) && (!this.useSpeed.isEnabled() || !this.method3933(var4))) {
            if (!var1 && this.method3932(var4)) {
               var1 = true;
            } else {
               if (var2 || !var4.is(Items.GOLDEN_CARROT)) {
                  return true;
               }

               var2 = true;
            }
         }
      }

      return false;
   }

   private boolean method3932(ItemStack var1) {
      return this.method3934(var1, MobEffects.INVISIBILITY);
   }

   private boolean method3933(ItemStack var1) {
      return this.method3934(var1, MobEffects.SPEED);
   }

   private boolean method3934(ItemStack var1, Holder<MobEffect> var2) {
      if (!var1.is(Items.POTION)) {
         return false;
      } else {
         PotionContents var3 = var1.get(DataComponents.POTION_CONTENTS);
         if (var3 == null) {
            return false;
         } else {
            for (MobEffectInstance var5 : var3.getAllEffects()) {
               if (var5.getEffect().equals(var2)) {
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

   private void method3936(Predicate<ItemStack> var1) {
      if (this.intValue3 == 0 && mc.player.tickCount > 20) {
         if (this.method3922()) {
            this.method3924();
         } else {
            Slot var2 = InventoryUtils.method910(var1x -> var1x.container == mc.player.getInventory() && var1.test(var1x.getItem()));
            if (var2 != null) {
               this.method3945();
               this.intValue4 = var2.index;
               InventoryUtils.method900(var2, InteractionHand.OFF_HAND, false);
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

   private boolean method3942(double var1) {
      for (Entity var4 : mc.level.entitiesForRendering()) {
         if (var4 instanceof Player var5 && var5 != mc.player && mc.player.distanceToSqr(var5) < var1 * var1) {
            return true;
         }
      }

      return false;
   }

   private boolean method3943(BlockPos var1, double var2) {
      if (var2 <= 0.0) {
         return false;
      } else {
         for (Entity var5 : mc.level.entitiesForRendering()) {
            if (var5 instanceof Player var6 && var6 != mc.player && var6.position().distanceToSqr(Vec3.atCenterOf(var1)) < var2 * var2) {
               return true;
            }
         }

         return false;
      }
   }

   private void method3944(BlockPos var1) {
      if (var1 != null) {
         if (mc.player.tickCount % 10 == 0 || !this.method3941() && mc.player.tickCount % 5 == 0) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(var1));
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
            + (this.method3926(var0 -> var0.is(Items.BRUSH)) >= 0)
      );
   }

   private void notify(String var1) {
      ChatLogger.method2019("End Looter: " + var1);
   }

   private static enum Type {
      SAVE,
      TAKE,
      COLLECTING,
      ESCAPE;
   }
}
