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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationLock;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationConfig;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.ItemStackTags;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.PvpDetector;

@ModuleInfo(
   name = "AutoWarden",
   category = ModuleCategory.PLAYER,
   description = "module.autowarden.description"
)
public class AutoWardenModule extends Module {
   public static final AutoWardenModule INSTANCE = new AutoWardenModule();
   private final BooleanSetting useSpeed = new BooleanSetting("module.autoWarden.useSpeed", "module.autoWarden.useSpeed.desc", false);
   private final BooleanSetting reportKillers = new BooleanSetting("module.autoWarden.reportKillers", "module.autoWarden.reportKillers.desc", false);
   private final ModeSetting lootPriority = new ModeSetting(
      "module.autoWarden.lootPriority",
      "module.autoWarden.lootPriority.desc",
      "module.autoWarden.lootPriority.medium",
      "module.autoWarden.lootPriority.low",
      "module.autoWarden.lootPriority.high"
   );
   private final SliderSetting escapeLoot = new SliderSetting(
      "module.autoWarden.escapeLoot", 8.0F, 1.0F, 27.0F, 1.0F, "module.autoWarden.escapeLoot.desc", "слот"
   );
   private final BooleanSetting debug = new BooleanSetting("module.autoWarden.debug", "module.autoWarden.debug.desc", false);
   private final BooleanSetting humanLook = new BooleanSetting("module.autoWarden.humanLook", "module.autoWarden.humanLook.desc", true);
   private final TextSetting homeAnarchy = new TextSetting(
      "module.autoWarden.homeAnarchy", "module.autoWarden.homeAnarchy.desc", "", "module.autoWarden.homeAnarchy.empty"
   );
   private final TextSetting wardenAnarchies = new TextSetting(
      "module.autoWarden.wardenAnarchies", "module.autoWarden.wardenAnarchies.desc", "", "303, 304, 318"
   );
   private final GroupSetting anarchySettings = new GroupSetting(
      "module.autoWarden.anarchySettings", "module.autoWarden.anarchySettings.desc", this.homeAnarchy, this.wardenAnarchies
   );
   private final List<Integer> items = new ArrayList<>();
   private String text;
   private final Map<BlockPos, Integer> items2 = new HashMap<>();
   private final Map<BlockPos, Integer> items3 = new HashMap<>();
   private final Map<BlockPos, Integer> items4 = new HashMap<>();
   private int intValue = -32768 << 16 | 0;
   private Rotation rotation;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private static final long CONST_longValue = 30000L;
   private static final double CONST_doubleValue = 45.0;
   private static final double CONST_doubleValue2 = 16.0;
   private static final double CONST_doubleValue3 = 16.0;
   private static final double CONST_doubleValue4 = 20.0;
   private static final int CONST_intValue = 3;
   private static final int CONST_intValue2 = 600;
   private static final double CONST_doubleValue5 = 6400.0;
   private AutoWardenModule.Type type = AutoWardenModule.Type.SAVE;
   private int intValue2 = 1;
   private int intValue3;
   private boolean flag;
   private AABB box;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private String text2 = "";
   private String text3;
   private int intValue4;
   private int intValue5 = -1;
   private static final int CONST_intValue3 = 9;

   @Override
   public List<Setting> getSettings() {
      return Zenith.method1502()
         ? List.of(this.anarchySettings, this.lootPriority, this.escapeLoot, this.useSpeed, this.reportKillers, this.humanLook, this.debug)
         : List.of(this.anarchySettings, this.lootPriority, this.escapeLoot, this.useSpeed, this.reportKillers, this.humanLook);
   }

   public boolean method3949() {
      return this.humanLook.isEnabled();
   }

   private AutoWardenModule() {
   }

   public List<Integer> method3950() {
      String str = method3952(this.homeAnarchy);
      String str2 = method3952(this.wardenAnarchies);
      String text = str + "|" + str2;
      if (this.text != null && this.text.equals(text)) {
         return this.items;
      } else {
         this.text = text;
         this.items.clear();
         int n;
         if ((n = method3953(str)) > 0) {
            this.items.add(n);
         }

         for (int n2 : method3954(str2)) {
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

   public void method3951() {
      String string = this.items.isEmpty() ? "" : String.valueOf(this.items.getFirst());
      String str = this.items.stream().skip(1L).map(String::valueOf).collect(Collectors.joining(", "));
      this.homeAnarchy.setValue(string);
      this.wardenAnarchies.setValue(str);
      this.text = string + "|" + str;
   }

   private static String method3952(TextSetting homeAnarchy) {
      return homeAnarchy.getValue() == null ? "" : homeAnarchy.getValue();
   }

   private static int method3953(String str) {
      var iterator = method3954(str).iterator();
      return iterator.hasNext() ? (Integer)iterator.next() : -1;
   }

   private static List<Integer> method3954(String str) {
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

   private int method3955() {
      var integers = this.method3950();
      return (integers.isEmpty()) ? -1 : (Integer)integers.getFirst();
   }

   private boolean method3956() {
      return this.method3955() >= 0 && this.method3955() == PvpDetector.method2724();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabledRaw()) {
         if (mc.player != null && mc.level != null) {
            var integers = this.method3950();
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

            this.intValue2 = 1;
            this.type = AutoWardenModule.Type.COLLECTING;
            this.blockPos3 = null;
            this.items4.clear();
            PvpDetector.reset();
            this.notify("Shift + Пробел — быстрое выключение функции");
            Settings settings = BaritoneAPI.getSettings();
            settings.avoidance.value = Boolean.TRUE;
            settings.maxFallHeightNoWater.value = 256;
            settings.blockFreeLook.value = Boolean.TRUE;
            settings.randomLooking.value = 1.0;
            settings.randomLooking113.value = 1.0;
            this.method3957(true);
         } else {
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Settings settings = BaritoneAPI.getSettings();
      settings.allowBreak.value = Boolean.FALSE;
      settings.allowPlace.value = Boolean.FALSE;
      settings.avoidance.value = Boolean.FALSE;
      settings.maxFallHeightNoWater.value = 3;
      this.method3957(false);
      this.method4032();
      this.method3977();
      this.rotation = null;
      RotationLock.reset();
      this.blockPos3 = null;
      this.intValue4 = 0;
      this.intValue5 = -1;
   }

   private void method3957(boolean flag) {
      Settings settings = BaritoneAPI.getSettings();
      if (settings != null) {
         List list = (List)settings.blocksToAvoid.value;

         try {
            for (Block block : BuiltInRegistries.BLOCK) {
               if (block.defaultBlockState().is(BlockTags.CANDLES)) {
                  if (flag) {
                     if (!list.contains(block)) {
                        list.add(block);
                     }
                  } else {
                     list.remove(block);
                  }
               }
            }
         } catch (IllegalStateException e) {
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.gui.screen() instanceof DeathScreen && mc.player != null && mc.player.deathTime >= 5) {
         mc.player.respawn();
      }

      if (mc.player != null && mc.level != null) {
         this.rotation = null;
         if (mc.player.tickCount < this.intValue) {
            this.items4.clear();
            this.items3.clear();
         }

         this.intValue = mc.player.tickCount;
         PvpDetector.update();
         if (this.debug.isEnabled() && mc.player.tickCount % 20 == 0) {
            this.method4039();
         }

         if (mc.options.keyShift.isDown() && mc.options.keyJump.isDown()) {
            this.setToggled(false);
         } else {
            for (Warden warden : mc.level.getEntitiesOfClass(Warden.class, mc.player.getBoundingBox().inflate(256.0), warden2 -> true)) {
               this.items4.put(warden.blockPosition(), mc.player.tickCount + 100);
            }

            this.items4.values().removeIf(n -> mc.player.tickCount > n);
            if (!this.method4029()) {
               if (this.text3 != null) {
                  if (mc.player.tickCount >= 20 && mc.player.tickCount < 30) {
                     mc.player.connection.sendChat("/report " + this.text3 + " чит");
                     this.text3 = null;
                  }
               } else if (mc.player.tickCount < 5) {
                  this.intValue3 = 0;
                  this.items2.clear();
               } else if (PvpDetector.method2724() < 0) {
                  if (mc.player.tickCount % 100 == 0 && this.method3955() >= 0 && mc.player.tickCount > 300) {
                     mc.player.connection.sendCommand("an" + this.method3955());
                  }

                  this.type = AutoWardenModule.Type.SAVE;
               } else {
                  if (mc.player.tickCount % 100 == 0 && PvpDetector.method2727() && (this.box == null || !this.method3979())) {
                     this.method3978();
                  }

                  if (mc.player.hasEffect(MobEffects.GLOWING) && this.method4036(32.0)) {
                     this.method3972(false);
                  } else {
                     switch (this.type) {
                        case SAVE:
                           this.method3959();
                           break;
                        case TAKE:
                           this.method3960();
                           break;
                        case COLLECTING:
                           this.method3961();
                           break;
                        case ESCAPE:
                           this.method3964();
                     }

                     if (this.method4034() && mc.player.tickCount % 15 == 0) {
                        this.method3977();
                     }
                  }
               }
            }
         }
      } else {
         this.rotation = null;
      }
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent event) {
      if (mc.player != null && event.getText() != null) {
         String string = event.getText().getString();
         if (string.contains("Помянем. Вы погибли")) {
            this.flag = true;
            if (this.reportKillers.isEnabled() && string.contains("Вас убил")) {
               StringBuilder builder = new StringBuilder();

               for (MobEffectInstance activeEffect : mc.player.getActiveEffects()) {
                  builder.append(activeEffect.getEffect().value().getDisplayName().getString()).append(", ");
               }

               this.notify("Эффекты при смерти: " + (builder.isEmpty() ? "нет" : builder.toString().trim()));
               if (!mc.player.hasEffect(MobEffects.GLOWING) && !this.method4001(2.0)) {
                  this.text3 = string.split("Вас убил ")[1].split(",")[0].trim();
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && mc.player != null) {
         String str = null;
         if (event.getPacket() instanceof ClientboundSoundPacket clientboundSoundPacket) {
            str = this.method3958(clientboundSoundPacket.getSound());
         } else if (event.getPacket() instanceof ClientboundSoundEntityPacket soundEntityPacket) {
            str = this.method3958(soundEntityPacket.getSound());
         }

         if (str != null) {
            if (str.contains("warden.roar") || str.contains("warden.angry") || str.contains("warden.sonic")) {
               this.intValue3 = mc.player.tickCount + 100;
            }
         }
      }
   }

   private String method3958(Holder<SoundEvent> sound) {
      return sound != null && !sound.unwrapKey().isEmpty() ? ((ResourceKey)sound.unwrapKey().get()).identifier().getPath() : null;
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (mc.player != null) {
         if (!mc.player.onGround() && !mc.player.onClimbable()) {
            event.setShift(false);
         }

         if (this.humanLook.isEnabled() && mc.player.onGround() && this.method3998() > 70.0F) {
            event.setMovement(0.0F, 0.0F);
            event.setSprint(false);
         }

         if (this.method4034() && mc.player.getMainHandItem().isEmpty() && !this.method4001(3.0)) {
            int n = mc.player.tickCount % 10 <= ThreadLocalRandom.current().nextFloat(3.0F, 8.0F) ? -1 : 1;
            event.setMovement(n, n);
         }
      }
   }

   private void method3959() {
      if (this.method3955() >= 0 && PvpDetector.method2724() != this.method3955() && !PvpDetector.method2725() && mc.player.tickCount % 5 == 0 && mc.player.tickCount > 5) {
         mc.player.connection.sendCommand("an" + this.method3955());
      }

      if (this.method3956()) {
         this.method4014();
         this.method4004(this.method4020(), true, AutoWardenModule.Type.TAKE);
      }
   }

   private void method3960() {
      if (this.flag && this.method3950().size() > 1) {
         if (++this.intValue2 >= this.method3950().size()) {
            this.intValue2 = 1;
         }

         this.flag = false;
      }

      this.intValue3 = 0;
      this.items2.clear();
      this.items3.clear();
      if (mc.player.tickCount % 20 == 0) {
         StringBuilder builder = new StringBuilder("Собираем (возможно не хватает) -> ");
         if (this.method4017() < 1) {
            builder.append("зелье невидимости, ");
         }

         if (InventoryUtils.method920(Items.GOLDEN_CARROT) < 3) {
            builder.append("золотая морковь, ");
         }

         if (this.useSpeed.isEnabled() && this.method4015(this::method4025) < 0) {
            builder.append("зелье скорости ");
         }

         if (mc.player.tickCount % 200 == 0 && !builder.isEmpty() && !this.method4012()) {
            mc.player.closeContainer();
         }
      }

      if (this.method3956()) {
         this.method4004(this.method4018() || this.method4012(), false, AutoWardenModule.Type.COLLECTING);
      }
   }

   private void method3961() {
      if (!this.method3965()) {
         if (this.intValue4 != 0) {
            if (this.method4033()) {
               this.method3977();
            }
         } else {
            var integers = this.method3950();
            if (integers.size() <= 1) {
               if (mc.player.tickCount % 20 == 0) {
                  this.notify("Анархии Вардена не заданы — вписать через запятую в настройках функции");
               }
            } else {
               if (this.intValue2 >= integers.size()) {
                  this.intValue2 = 1;
               }

               int n = (Integer)integers.get(this.intValue2);
               if (PvpDetector.method2724() != n) {
                  if (mc.player.tickCount % 10 == 0 && mc.player.tickCount > 10) {
                     mc.player.connection.sendCommand("an" + n);
                  }
               } else {
                  if (mc.player.tickCount > 5) {
                     this.method3963();
                  }
               }
            }
         }
      }
   }

   private int method3962() {
      MobEffectInstance effect = mc.player.getEffect(MobEffects.INVISIBILITY);
      return effect == null ? 0 : effect.getDuration() / 20;
   }

   private void method3963() {
      MobEffectInstance effect = mc.player.getEffect(MobEffects.INVISIBILITY);
      boolean flag;
      if (!(flag = mc.player.hasEffect(MobEffects.GLOWING) || effect != null && effect.getDuration() >= 400)
         && effect == null
         && this.method4017() < 1
         && mc.player.tickCount % 5 == 0
         && !PvpDetector.method2725()) {
         this.method4000("ухожу домой: невидимости нет и зелий в инвентаре нет");
         this.type = AutoWardenModule.Type.ESCAPE;
      } else {
         if (!flag) {
            this.method4028(this::method4024);
         }

         if (!PvpDetector.method2727()) {
            if (mc.player.tickCount % 50 == 0) {
               mc.player.connection.sendCommand("home");
            }
         } else if (flag) {
            if ((this.useSpeed.isEnabled() && mc.player.getEffect(MobEffects.SPEED) == null ? this.method4015(this::method4025) : -1) < 0) {
               this.method3982();
            } else {
               this.method4028(this::method4025);
            }
         }
      }
   }

   private void method3964() {
      if (this.method3956()) {
         this.blockPos3 = null;
         this.type = AutoWardenModule.Type.SAVE;
      } else if (this.method4011()) {
         this.method4013();
      } else if (PvpDetector.method2725()) {
         this.method3972(this.method3968());
      } else {
         this.blockPos3 = null;
         this.type = AutoWardenModule.Type.SAVE;
      }
   }

   private boolean method3965() {
      boolean flag = this.method3968();
      int n = this.method4016();
      int n2;
      int n3 = Math.max(n2 = this.method3966(), this.method3967(20));
      int foodLevel = mc.player.getFoodData().getFoodLevel();
      if (mc.player.tickCount > 100) {
         String str = null;
         if (flag) {
            str = "варден агрится";
         } else if (n > n3) {
            str = "инвентарь полон: " + n + " > " + n3;
         } else if (foodLevel < 8) {
            str = "голод " + foodLevel;
         }

         if (str != null) {
            this.method4000("ухожу: " + str);
            if (flag) {
               this.flag = true;
            }

            this.type = AutoWardenModule.Type.ESCAPE;
            return true;
         }
      }

      if (!PvpDetector.method2725() && n > n2) {
         this.method4000("ухожу сдавать: " + n + " слотов > порога " + n2);
         this.type = AutoWardenModule.Type.ESCAPE;
         return true;
      } else {
         int n4;
         if ((n4 = PvpDetector.method2726()) >= 0 && n4 < 7 && !this.method4036(14.0) && n > n2 - 1) {
            this.method4000("ухожу под конец пвп: " + n + " слотов, пвп " + n4 + " сек");
            this.type = AutoWardenModule.Type.ESCAPE;
            return true;
         } else {
            return false;
         }
      }
   }

   private int method3966() {
      return Math.max(1, this.method3967((int)this.escapeLoot.getValue3()));
   }

   private int method3967(int n) {
      double d = this.lootPriority.method2101("module.autoWarden.lootPriority.low")
         ? 1.5
         : (this.lootPriority.method2101("module.autoWarden.lootPriority.high") ? 0.8 : 1.0);
      return (int)(n * d);
   }

   private boolean method3968() {
      if (mc.player.tickCount >= this.intValue3) {
         return false;
      } else {
         for (Entity entity : mc.level.entitiesForRendering()) {
            double d;
            if (entity instanceof Warden warden
               && (d = mc.player.distanceToSqr(warden)) < 900.0
               && this.method3970(warden)
               && (d < 16.0 || this.method3969(warden))) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean method3969(Warden warden) {
      return (mc.player.getX() - warden.getX()) * (warden.getX() - warden.xo) + (mc.player.getZ() - warden.getZ()) * (warden.getZ() - warden.zo) > 0.01;
   }

   private boolean method3970(Warden warden) {
      double d = Math.toDegrees(Math.atan2(-(mc.player.getX() - warden.getX()), mc.player.getZ() - warden.getZ()));
      return Math.abs(((warden.getVisualRotationYInDegrees() - d) % 360.0 + 540.0) % 360.0 - 180.0) < 10.0;
   }

   private boolean method3971(BlockPos pos) {
      for (BlockPos pos2 : this.items4.keySet()) {
         if (pos2.distSqr(pos) < 25.0) {
            return true;
         }
      }

      return false;
   }

   private void method3972(boolean flag) {
      this.method4013();
      this.method4014();
      if (this.blockPos3 == null
         || mc.player.distanceToSqr(Vec3.atCenterOf(this.blockPos3)) <= 16.0
         || this.method3974(this.blockPos3.getX(), this.blockPos3.getZ(), flag) < 16.0) {
         this.blockPos3 = this.method3973(flag);
         this.method4000("бегу в " + this.blockPos3);
      }

      this.method3975(this.blockPos3);
   }

   private BlockPos method3973(boolean flag) {
      BlockPos pos = null;
      double d = -1.0;
      int y = mc.player.blockPosition().getY();

      for (byte i = 0; i < 360; i += 30) {
         int x = this.method3980((int)(mc.player.getX() + Math.cos(Math.toRadians(i)) * 45.0));
         int z = this.method3981((int)(mc.player.getZ() + Math.sin(Math.toRadians(i)) * 45.0));
         double d2;
         if ((d2 = this.method3974(x, z, flag)) > d) {
            d = d2;
            pos = new BlockPos(x, y, z);
         }
      }

      return pos;
   }

   private double method3974(int x, int z, boolean flag) {
      double d = Double.MAX_VALUE;

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity != mc.player && (entity instanceof Player || flag && entity instanceof Warden)) {
            d = Math.min(d, Math.hypot(entity.getX() - x, entity.getZ() - z));
         }
      }

      return d;
   }

   private void method3975(BlockPos blockPos3) {
      if (blockPos3 != null) {
         BlockPos pos = new BlockPos(this.method3980(blockPos3.getX()), blockPos3.getY(), this.method3981(blockPos3.getZ()));
         if (!pos.equals(this.blockPos2) || !this.method3976()) {
            this.blockPos2 = pos;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
         }
      }
   }

   private boolean method3976() {
      boolean flag;
      try {
         flag = BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath();
      } catch (Throwable e) {
         return false;
      }

      return flag;
   }

   private void method3977() {
      this.blockPos2 = null;
      if (BaritoneAPI.getProvider() != null) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      }
   }

   private void method3978() {
      this.box = new AABB(-2070.0, mc.player.blockPosition().getY(), -2076.0, -1921.0, mc.player.blockPosition().getY(), -1929.0);
   }

   private boolean method3979() {
      return this.box != null
         && mc.player.getX() >= this.box.minX
         && mc.player.getX() <= this.box.maxX
         && mc.player.getZ() >= this.box.minZ
         && mc.player.getZ() <= this.box.maxZ;
   }

   private int method3980(int x) {
      return this.box == null ? x : (int)Math.max(this.box.minX + 10.0, Math.min(this.box.maxX - 10.0, (double)x));
   }

   private int method3981(int z) {
      return this.box == null ? z : (int)Math.max(this.box.minZ + 10.0, Math.min(this.box.maxZ - 10.0, (double)z));
   }

   private void method3982() {
      AbstractContainerScreen containerScreen = this.method4010();
      if (containerScreen != null) {
         this.method4005(containerScreen);
      } else {
         BlockPos key = this.method3983();
         if (key == null) {
            this.method4000("цели нет: подходящих сундуков не нашлось");
            if (mc.player.tickCount % 40 == 0) {
               this.type = AutoWardenModule.Type.ESCAPE;
               this.flag = true;
            }
         } else {
            double d;
            if ((d = mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(key))) > 20.0) {
               if (d > 10.0) {
                  this.method4014();
               }

               BlockPos above = this.method3993(key.above()) ? key.above() : this.method3990(key);
               BlockPos blockPos3 = above == null ? key.above() : above;
               this.method4000("иду к " + key + " (точка " + blockPos3 + ")");
               this.method3975(blockPos3);
            } else {
               if (this.method4033()) {
                  this.method3977();
               }

               long l;
               if ((l = PvpDetector.method2721(key)) > 1000L && this.method4037(key, 7.0)) {
                  this.method4000("жду " + key + ": рядом игрок");
               } else if (l > 0L) {
                  this.method4000("жду " + key + ": сами вычерпали, лут через " + l / 1000L + " сек");
               } else if (this.items2.getOrDefault(key, 0) >= 3) {
                  this.method4000("выдоен " + key + ", беру следующий");
               } else if (this.method3995(mc.player.getEyePosition(), key) == null) {
                  BlockPos blockPos3 = this.method3990(key);
                  if (blockPos3 != null) {
                     this.method4000("не вижу " + key + ", перехожу на " + blockPos3);
                     this.method3975(blockPos3);
                  } else {
                     this.method4000("не вижу " + key + " и встать негде — бросаю");
                     this.method3984(key);
                  }
               } else {
                  if (this.method3999(key, 1)) {
                     this.items2.merge(key, 1, Integer::sum);
                     PvpDetector.method2723(key);
                  }
               }
            }
         }
      }
   }

   private BlockPos method3983() {
      if (this.blockPos != null) {
         if (this.stopWatch.hasElapsed(30000L)) {
            this.method3984(this.blockPos);
         } else {
            if (this.method3985(this.blockPos)) {
               return this.blockPos;
            }

            this.method4000(
               "бросаю цель "
                  + this.blockPos
                  + " (дист "
                  + (int)Math.sqrt(mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(this.blockPos)))
                  + "): "
                  + this.method3986(this.blockPos)
            );
            this.blockPos = null;
         }
      }

      BlockPos blockPos = this.method3987();
      if (blockPos != null && !blockPos.equals(this.blockPos)) {
         this.stopWatch.reset();
      }

      this.blockPos = blockPos;
      return blockPos;
   }

   private void method3984(BlockPos key) {
      this.method4000("бросаю " + key + " на 30 сек");
      this.items3.put(key, mc.player.tickCount + 600);
      this.blockPos = null;
      this.stopWatch.reset();
   }

   private boolean method3985(BlockPos blockPos) {
      return this.method3986(blockPos) == null;
   }

   private String method3986(BlockPos blockPos) {
      if (mc.player.tickCount < this.items3.getOrDefault(blockPos, -32768 << 16 | 0)) {
         return "в списке пропущенных";
      } else if (this.items2.getOrDefault(blockPos, 0) >= 3) {
         return "уже выдоен";
      } else {
         long l;
         return (l = PvpDetector.method2721(blockPos)) <= 20000L ? null : "мы его вычерпали, лут через " + l / 1000L + " сек";
      }
   }

   private BlockPos method3987() {
      BlockPos pos = null;
      byte b = 99;
      double d = Double.MAX_VALUE;
      BlockPos pos2 = null;
      long l = Long.MAX_VALUE;

      for (BlockPos pos3 : this.method3989()) {
         if (this.method3985(pos3) && (!this.method3988(pos3) || !this.method3971(pos3) && !this.method4038(pos3))) {
            double d2 = mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos3));
            long l2;
            if ((l2 = PvpDetector.method2721(pos3)) > 0L && l2 < l) {
               pos2 = pos3;
               l = l2;
            }

            boolean flag;
            byte b2;
            if ((flag = l2 <= 0L) && d2 <= 144.0) {
               b2 = 0;
            } else if (flag && d2 <= 625.0) {
               b2 = 1;
            } else if (l2 <= 20000L) {
               b2 = 2;
            } else {
               if (!flag) {
                  continue;
               }

               b2 = 3;
            }

            double d3 = Vec3.atCenterOf(pos3).y - mc.player.getEyeY();
            double d4 = pos3.getX() + 0.5 - mc.player.getX();
            double d5 = pos3.getZ() + 0.5 - mc.player.getZ();
            double d6 = d4 * d4 + d5 * d5 + (d3 > 0.0 ? 2 : 1) * d3 * d3;
            if (b2 < b || b2 == b && d6 < d) {
               b = b2;
               d = d6;
               pos = pos3;
            }
         }
      }

      if (pos == null && pos2 != null) {
         this.method4000("нечего лутать, иду ждать " + pos2 + " (" + l / 1000L + " сек)");
         return pos2;
      } else {
         return pos;
      }
   }

   private boolean method3988(BlockPos pos) {
      return mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }

   private List<BlockPos> method3989() {
      var poses = new ArrayList<>(PvpDetector.method2728());

      for (BlockPos pos : PvpDetector.method2718()) {
         if (!poses.contains(pos) && mc.player.distanceToSqr(Vec3.atCenterOf(pos)) <= 6400.0) {
            poses.add(pos);
         }
      }

      return poses;
   }

   private BlockPos method3990(BlockPos key) {
      BlockPos pos = this.method3991(key, 1);
      return pos != null ? pos : this.method3991(key, 3);
   }

   private BlockPos method3991(BlockPos key, int n) {
      double d;
      double d2 = (d = mc.player.blockInteractionRange() + 0.4) * d;
      Vec3 atCenterOf = Vec3.atCenterOf(key);
      BlockPos offset = null;
      double d3 = Double.MAX_VALUE;

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            for (int k = -n; k <= 1; k++) {
               BlockPos pos = key.offset(i, k, j);
               double d4 = Vec3.atCenterOf(pos).distanceToSqr(atCenterOf);
               if (!pos.equals(key) && !(d4 >= d3) && !(d4 > d2) && this.method3992(pos) && this.method3994(pos, key)) {
                  d3 = d4;
                  offset = pos;
               }
            }
         }
      }

      return offset;
   }

   private boolean method3992(BlockPos above) {
      return this.method3993(above) && this.method3993(above.above()) && !this.method3993(above.below());
   }

   private boolean method3993(BlockPos above) {
      return mc.level.getBlockState(above).getCollisionShape(mc.level, above).isEmpty();
   }

   private boolean method3994(BlockPos pos, BlockPos key) {
      Vec3 eyePosition = Vec3.atCenterOf(pos).add(0.0, mc.player.getEyeHeight(mc.player.getPose()) - 0.5, 0.0);
      Vec3 vec = this.method3995(eyePosition, key);
      double d = mc.player.blockInteractionRange() - 0.2;
      return vec != null && eyePosition.distanceToSqr(vec) <= d * d;
   }

   private Vec3 method3995(Vec3 eyePosition, BlockPos key) {
      double d;
      double d2 = (d = mc.player.blockInteractionRange() - 0.15) * d;
      Vec3 atCenterOf = Vec3.atCenterOf(key);
      Vec3 vec = null;
      double d3 = Double.MAX_VALUE;

      for (double x = -0.4; x <= 0.41; x += 0.4) {
         for (double y = -0.4; y <= 0.41; y += 0.4) {
            for (double z = -0.4; z <= 0.41; z += 0.4) {
               Vec3 vec2 = atCenterOf.add(x, y, z);
               double d4;
               if (!((d4 = eyePosition.distanceToSqr(vec2)) >= d3)
                  && !(d4 > d2)
                  && this.method3996(eyePosition, Rotation.lookingAt(vec2, eyePosition), key) != null) {
                  d3 = d4;
                  vec = vec2;
               }
            }
         }
      }

      return vec;
   }

   private BlockHitResult method3996(Vec3 eyePosition, Rotation rotation2, BlockPos key) {
      BlockHitResult blockHitResult = TargetUtils.method3216(
         eyePosition,
         rotation2,
         mc.player.blockInteractionRange(),
         var1x -> var1x != null && var1x.getType() == HitResult.Type.BLOCK && var1x.getBlockPos().equals(key)
      );
      return blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK && blockHitResult.getBlockPos().equals(key) ? blockHitResult : null;
   }

   @EventTarget
   public void method3997(PreRotationEvent event) {
      Rotation rotation = this.rotation;
      boolean enabled = this.humanLook.isEnabled();
      if (rotation == null && enabled) {
         rotation = RotationLock.method2708();
      }

      if (rotation != null) {
         Rotation rotation2 = rotation;
         RotationConfig rotationConfig17 = enabled ? bcCameraTestnerv.getRotationConfig17() : bcCameraTestnerv.getRotationConfig();
         rotationManager.requestRotation(new RotationRequest(rotation2, () -> bcCameraTestnerv.method1851(rotationConfig17, rotation2), rotationConfig17), 4, this);
      }
   }

   private float method3998() {
      Rotation rotation = RotationLock.method2708();
      return rotation != null && this.rotation == null
         ? Math.abs(Mth.wrapDegrees(rotation.getFloatValue() - rotationManager.getEffectiveRotation().getFloatValue()))
         : 0.0F;
   }

   private boolean method3999(BlockPos key, int n) {
      if (key != null && !this.method4011()) {
         Vec3 eyePosition = mc.player.getEyePosition();
         Vec3 vec = this.method3995(eyePosition, key);
         if (vec == null) {
            this.rotation = null;
            this.method4000("interact: точку прицела не нашёл");
            return false;
         } else {
            Rotation rotation = Rotation.lookingAt(vec, eyePosition);
            this.rotation = rotation;
            if (mc.player.tickCount % n != 0) {
               return false;
            } else {
               Rotation rotation2 = rotationManager.getRotation2();
               float f;
               if ((f = rotation2.angularDistanceTo(rotation)) > 5.0F) {
                  this.method4000(
                     String.format(
                        "interact: ротация не доведена, промах %.1f (надо %.1f/%.1f, сервер %.1f/%.1f, current %s)",
                        f,
                        rotation.getFloatValue(),
                        rotation.getFloatValue2(),
                        rotation2.getFloatValue(),
                        rotation2.getFloatValue2(),
                        rotationManager.getCurrentRotation() == null ? "null" : "есть"
                     )
                  );
                  return false;
               } else {
                  BlockHitResult blockHit = this.method3996(eyePosition, rotation2, key);
                  if (blockHit == null) {
                     this.method4000("interact: луч не дотянулся до " + key);
                     return false;
                  } else {
                     mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
                     this.method4000("interact: ОТКРЫВАЮ " + key);
                     return true;
                  }
               }
            }
         }
      } else {
         this.rotation = null;
         this.method4000("interact: экран открыт");
         return false;
      }
   }

   private void method4000(String str) {
      if (this.debug.isEnabled() && !str.equals(this.text2)) {
         this.text2 = str;
         this.notify("[trace] " + str);
      }
   }

   private boolean method4001(double d) {
      for (BlockPos pos : PvpDetector.method2728()) {
         if (mc.player.distanceToSqr(Vec3.atCenterOf(pos)) <= d * d) {
            return true;
         }
      }

      return false;
   }

   private BlockPos method4002(boolean flag) {
      BlockPos blockPosition = mc.player.blockPosition();
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -4; i <= 4; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 4; k++) {
               mutablePos.set(blockPosition.getX() + i, blockPosition.getY() + j, blockPosition.getZ() + k);
               if (mc.level.getBlockState(mutablePos).is(Blocks.CHEST) && this.method4003(mutablePos) == flag && mc.level.getBlockState(mutablePos.above()).isAir()) {
                  BlockHitResult clip = mc.level
                     .clip(new ClipContext(mc.player.getEyePosition(), Vec3.atCenterOf(mutablePos), net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, mc.player));
                  if (clip.getBlockPos().equals(mutablePos)) {
                     return mutablePos.immutable();
                  }
               }
            }
         }
      }

      return null;
   }

   private boolean method4003(BlockPos mutablePos) {
      if (mc.level.getBlockState(mutablePos.below()).is(Blocks.HOPPER)) {
         return true;
      } else {
         BlockState blockState = mc.level.getBlockState(mutablePos);
         if (blockState.hasProperty(BlockStateProperties.CHEST_TYPE) && blockState.getValue(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
               BlockPos relative = mutablePos.relative(direction);
               BlockState blockState2 = mc.level.getBlockState(relative);
               if (blockState2.is(Blocks.CHEST)
                  && blockState2.getValue(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE
                  && blockState2.getValue(BlockStateProperties.CHEST_TYPE) != blockState.getValue(BlockStateProperties.CHEST_TYPE)
                  && blockState2.getValue(BlockStateProperties.HORIZONTAL_FACING) == blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                  && mc.level.getBlockState(relative.below()).is(Blocks.HOPPER)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private void method4004(boolean flag, boolean flag2, AutoWardenModule.Type type) {
      if (!flag) {
         if (this.method4013()) {
            if (flag2 && this.type == AutoWardenModule.Type.SAVE) {
            }

            this.type = type;
         }
      } else {
         AbstractContainerScreen containerScreen = this.method4010();
         if (containerScreen != null) {
            if (flag2) {
               this.method4006(containerScreen);
            } else {
               this.method4007(containerScreen);
            }
         } else {
            this.method3999(this.method4002(flag2), 2);
         }
      }
   }

   private void method4005(AbstractContainerScreen<?> containerScreen) {
      if (this.method4033()) {
         this.method3977();
      } else if (mc.player.tickCount % 2 == 0) {
         Slot slot = this.method4008(containerScreen, false, var1x -> !var1x.isEmpty() && !this.method4021(var1x));
         if (slot == null) {
            this.method4013();
         } else {
            this.method4009(containerScreen, slot, 0, ContainerInput.QUICK_MOVE);
         }
      }
   }

   private void method4006(AbstractContainerScreen<?> containerScreen) {
      if (mc.player.tickCount % 2 == 0) {
         boolean flag = false;
         boolean flag2 = false;
         int n = 0;

         for (Slot slot : containerScreen.getMenu().slots) {
            if (n >= 4) {
               return;
            }

            ItemStack carried = slot.getItem();
            if (slot.container == mc.player.getInventory() && !carried.isEmpty() && (!this.useSpeed.isEnabled() || !this.method4025(carried))) {
               if (!flag && this.method4024(carried)) {
                  flag = true;
               } else if (!flag2 && carried.is(Items.GOLDEN_CARROT)) {
                  flag2 = true;
               } else {
                  this.method4009(containerScreen, slot, 0, ContainerInput.QUICK_MOVE);
                  n++;
               }
            }
         }
      }
   }

   private void method4007(AbstractContainerScreen<?> containerScreen) {
      if (mc.player.tickCount % 2 == 0) {
         ItemStack carried = containerScreen.getMenu().getCarried();
         if (!carried.isEmpty()) {
            java.util.function.Predicate<ItemStack> predicate = var1x -> var1x.isEmpty() || ItemStack.isSameItemSameComponents(var1x, carried);
            if (this.method4019(carried)) {
               this.method4009(containerScreen, this.method4008(containerScreen, true, predicate), 1, ContainerInput.PICKUP);
            } else {
               this.method4009(containerScreen, this.method4008(containerScreen, false, predicate), 0, ContainerInput.PICKUP);
            }
         } else {
            this.method4009(containerScreen, this.method4008(containerScreen, false, this::method4019), 0, ContainerInput.PICKUP);
         }
      }
   }

   private Slot method4008(AbstractContainerScreen<?> containerScreen, boolean flag, Predicate<ItemStack> predicate) {
      for (Slot slot : containerScreen.getMenu().slots) {
         if (slot.container == mc.player.getInventory() == flag && predicate.test(slot.getItem())) {
            return slot;
         }
      }

      return null;
   }

   private void method4009(AbstractContainerScreen<?> containerScreen, Slot slot, int buttonNum, ContainerInput containerInput) {
      if (slot != null) {
         mc.gameMode.handleContainerInput(containerScreen.getMenu().containerId, slot.index, buttonNum, containerInput, mc.player);
      }
   }

   private AbstractContainerScreen<?> method4010() {
      return mc.gui.screen() instanceof AbstractContainerScreen containerScreen
            && !(mc.gui.screen() instanceof InventoryScreen)
            && !(mc.player.containerMenu instanceof InventoryMenu)
         ? containerScreen
         : null;
   }

   private boolean method4011() {
      return this.method4010() != null;
   }

   private boolean method4012() {
      AbstractContainerScreen containerScreen = this.method4010();
      return containerScreen != null && !containerScreen.getMenu().getCarried().isEmpty();
   }

   private boolean method4013() {
      if (this.method4011() && mc.player.tickCount % 2 == 0) {
         mc.player.closeContainer();
      }

      return !this.method4011();
   }

   private void method4014() {
      if (!mc.player.getMainHandItem().isEmpty()) {
         for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).isEmpty()) {
               mc.player.getInventory().setSelectedSlot(i);
               return;
            }
         }

         for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getItem(i).isEmpty()) {
               if (mc.player.tickCount % 10 >= 2 && this.method4033()) {
                  this.method3977();
               }

               if (mc.player.tickCount % 10 == 4) {
                  InventoryUtils.method904(i, mc.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, false);
               }

               return;
            }
         }
      }
   }

   private int method4015(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 36; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private int method4016() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (!mc.player.getInventory().getItem(i).isEmpty()) {
            n++;
         }
      }

      return n;
   }

   private int method4017() {
      int n = 0;

      for (int i = 0; i < 36; i++) {
         if (this.method4024(mc.player.getInventory().getItem(i))) {
            n++;
         }
      }

      if (this.method4024(mc.player.getOffhandItem())) {
         n++;
      }

      return n;
   }

   private boolean method4018() {
      return this.method4017() < 1 || InventoryUtils.method920(Items.GOLDEN_CARROT) < 3 || this.useSpeed.isEnabled() && this.method4015(this::method4025) < 0;
   }

   private boolean method4019(ItemStack carried) {
      if (carried.isEmpty()) {
         return false;
      } else if (this.method4024(carried) && this.method4017() < 1) {
         return true;
      } else {
         return carried.is(Items.GOLDEN_CARROT) && InventoryUtils.method920(Items.GOLDEN_CARROT) < 3
            ? true
            : this.useSpeed.isEnabled() && this.method4025(carried) && this.method4015(this::method4025) < 0;
      }
   }

   private boolean method4020() {
      boolean flag = false;
      boolean flag2 = false;

      for (int i = 0; i < 36; i++) {
         ItemStack carried = mc.player.getInventory().getItem(i);
         if (!carried.isEmpty() && (!this.useSpeed.isEnabled() || !this.method4025(carried))) {
            if (!flag && this.method4024(carried)) {
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

   private boolean method4021(ItemStack stack) {
      return this.lootPriority.method2101("module.autoWarden.lootPriority.low")
         ? false
         : this.method4023(stack) || this.lootPriority.method2101("module.autoWarden.lootPriority.high") && this.method4022(stack);
   }

   private boolean method4022(ItemStack stack) {
      Item item = stack.getItem();
      return item instanceof ArrowItem
         || ItemStackTags.isPickaxe(item.getDefaultInstance())
         || item.builtInRegistryHolder().is(net.minecraft.tags.ItemTags.AXES)
         || stack.is(Items.CHORUS_FRUIT)
         || stack.is(Items.DISC_FRAGMENT_5)
         || stack.is(Items.NAUTILUS_SHELL)
         || stack.is(Items.BOOKSHELF)
         || stack.is(Items.COOKED_MUTTON)
         || stack.is(Items.SKELETON_SPAWN_EGG)
         || stack.is(Items.CREEPER_SPAWN_EGG)
         || stack.is(Items.ZOMBIE_SPAWN_EGG)
         || stack.is(Items.VINDICATOR_SPAWN_EGG)
         || stack.is(Items.PIGLIN_SPAWN_EGG)
         || stack.is(Items.FIRE_CHARGE)
         || stack.is(Items.LEATHER)
         || stack.is(Items.SHULKER_SHELL)
         || stack.is(Items.EXPERIENCE_BOTTLE)
         || stack.is(Items.WITHER_ROSE)
         || stack.is(Items.EMERALD)
         || stack.is(Items.SUGAR)
         || InventoryUtils.method918(stack, "potion-popper")
         || stack.has(DataComponents.JUKEBOX_PLAYABLE)
         || stack.is(Items.GHAST_TEAR)
         || stack.is(Items.DRAGON_BREATH)
         || stack.is(Items.VEX_SPAWN_EGG)
         || stack.is(Items.ENDERMITE_SPAWN_EGG)
         || stack.is(Items.CAT_SPAWN_EGG)
         || stack.is(Items.ENCHANTING_TABLE)
         || stack.is(Items.DIAMOND_HELMET)
         || stack.is(Items.DIAMOND_CHESTPLATE)
         || stack.is(Items.DIAMOND_LEGGINGS)
         || stack.is(Items.DIAMOND_BOOTS);
   }

   private boolean method4023(ItemStack stack) {
      Item item = stack.getItem();
      return item.builtInRegistryHolder().is(net.minecraft.tags.ItemTags.SHOVELS)
         || item.builtInRegistryHolder().is(net.minecraft.tags.ItemTags.AXES)
         || item instanceof BannerItem
         || item instanceof SmithingTemplateItem && !stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
         || stack.is(Items.BLAZE_ROD)
         || stack.is(Items.ENCHANTED_BOOK)
         || stack.is(Items.TRIDENT)
         || stack.is(Items.NAME_TAG)
         || stack.is(Items.SCULK)
         || stack.is(Items.SCULK_SENSOR)
         || stack.is(Items.ENDER_CHEST)
         || stack.is(Items.REINFORCED_DEEPSLATE)
         || stack.is(Items.PUFFERFISH)
         || stack.is(Items.HONEY_BOTTLE)
         || stack.is(Items.FERMENTED_SPIDER_EYE)
         || stack.is(Items.ANVIL)
         || stack.is(Items.COOKED_PORKCHOP);
   }

   private boolean method4024(ItemStack carried) {
      return this.method4026(carried, MobEffects.INVISIBILITY);
   }

   private boolean method4025(ItemStack carried) {
      return this.method4026(carried, MobEffects.SPEED);
   }

   private boolean method4026(ItemStack carried, Holder<MobEffect> holder) {
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

   private boolean method4027() {
      return this.intValue4 != 0;
   }

   private void method4028(Predicate<ItemStack> predicate) {
      if (this.intValue4 == 0 && mc.player.tickCount > 20) {
         if (this.method4011()) {
            this.method4013();
         } else {
            Slot slot = InventoryUtils.method910(var1x -> var1x.container == mc.player.getInventory() && predicate.test(var1x.getItem()));
            if (slot != null) {
               this.method3977();
               this.intValue5 = slot.index;
               InventoryUtils.method900(slot, InteractionHand.OFF_HAND, false);
               this.intValue4 = 1;
               this.stopWatch2.reset();
            }
         }
      }
   }

   private boolean method4029() {
      if (this.intValue4 == 0) {
         return false;
      } else if (this.intValue4 == 3) {
         return this.method4031();
      } else if (this.method4011()) {
         this.method4030();
         return true;
      } else {
         this.method3977();
         if (this.intValue4 == 1) {
            if (!this.stopWatch2.hasElapsed(150L)) {
               return true;
            }

            this.intValue4 = 2;
            this.stopWatch2.reset();
         }

         mc.options.keyShift.setDown(true);
         mc.options.keyUse.setDown(true);
         if (this.stopWatch2.hasElapsed(2200L)) {
            this.method4030();
         }

         return true;
      }
   }

   private void method4030() {
      this.method4032();
      this.intValue4 = this.intValue5 >= 0 ? 3 : 0;
      this.stopWatch2.reset();
   }

   private boolean method4031() {
      if (this.method4011()) {
         this.method4013();
         return true;
      } else if (!mc.player.getOffhandItem().isEmpty() && !this.stopWatch2.hasElapsed(3000L)) {
         if (this.stopWatch2.hasElapsed(100L)) {
            InventoryUtils.method904(this.intValue5, 40, ContainerInput.SWAP, false);
            this.intValue5 = -1;
            this.intValue4 = 0;
            this.stopWatch2.reset();
         }

         return true;
      } else {
         this.intValue5 = -1;
         this.intValue4 = 0;
         this.stopWatch2.reset();
         return false;
      }
   }

   private void method4032() {
      mc.options.keyUse.setDown(false);
      mc.options.keyShift.setDown(false);
   }

   private boolean method4033() {
      return mc.player.getDeltaMovement().horizontalDistanceSqr() > 0.0025;
   }

   private boolean method4034() {
      return !mc.level.getBlockState(mc.player.blockPosition()).is(BlockTags.CANDLES)
            && !mc.level.getBlockState(mc.player.blockPosition().below()).is(BlockTags.CANDLES)
         ? !this.method3968()
            && this.type == AutoWardenModule.Type.COLLECTING
            && PvpDetector.method2727()
            && mc.gui.screen() == null
            && !this.method4033()
            && this.intValue4 == 0
            && this.method4035()
         : true;
   }

   private boolean method4035() {
      AABB inflate = mc.player.getBoundingBox().inflate(0.05, 0.0, 0.05);

      for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(inflate.minX, inflate.minY, inflate.minZ), BlockPos.containing(inflate.maxX, inflate.maxY, inflate.maxZ))) {
         if (!mc.level.getBlockState(pos).isAir()) {
            return true;
         }
      }

      return false;
   }

   private boolean method4036(double d) {
      for (Player player : mc.level.players()) {
         if (player != mc.player && mc.player.distanceToSqr(player) < d * d) {
            return true;
         }
      }

      return false;
   }

   private boolean method4037(BlockPos key, double d) {
      for (Player player : mc.level.players()) {
         if (player != mc.player && player.position().distanceToSqr(Vec3.atCenterOf(key)) < d * d) {
            return true;
         }
      }

      return false;
   }

   private boolean method4038(BlockPos pos) {
      for (Player player : mc.level.players()) {
         if (player != mc.player
            && !(player.position().distanceToSqr(Vec3.atCenterOf(pos)) >= 20.0)
            && Stream.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
               .anyMatch(var1x -> ItemStackTags.isArmor(player.getItemBySlot(var1x)))) {
            return true;
         }
      }

      return false;
   }

   private void method4039() {
      this.notify(
         "состояние "
            + this.type
            + " | анархия "
            + PvpDetector.method2724()
            + ", нужна "
            + this.method3955()
            + " (список: "
            + this.method3950().size()
            + ") | в зоне фермы "
            + PvpDetector.method2727()
            + " | сундуков "
            + PvpDetector.method2728().size()
            + " (в памяти "
            + PvpDetector.method2718().size()
            + ") | варден "
            + this.method3968()
            + " | пвп "
            + PvpDetector.method2725()
            + " ("
            + PvpDetector.method2726()
            + ") | зелий инвиза "
            + this.method4017()
            + ", эффект "
            + this.method3962()
            + " сек, морковь "
            + InventoryUtils.method920(Items.GOLDEN_CARROT)
            + " | слотов "
            + this.method4016()
            + "/"
            + this.method3966()
            + ", голод "
            + mc.player.getFoodData().getFoodLevel()
      );
      this.notify(
         "застрял "
            + this.method4034()
            + " | двигаюсь "
            + this.method4033()
            + " | в блоке "
            + this.method4035()
            + " | лут "
            + this.method4020()
            + " | цель "
            + (this.blockPos == null ? "нет" : this.blockPos)
            + " | "
            + this.method4040()
            + " | приёмник в руке "
            + (this.method4002(true) == null ? "нет" : "есть")
            + " | ближайший приёмник "
            + (this.method4041() == null ? "нет" : "есть")
      );
   }

   private String method4040() {
      BlockPos blockPos = this.blockPos;
      if (blockPos == null) {
         return "цели нет";
      } else {
         Vec3 eyePosition = mc.player.getEyePosition();
         Vec3 vec = this.method3995(eyePosition, blockPos);
         String str = "-";
         if (vec != null) {
            Rotation rotation = Rotation.lookingAt(vec, eyePosition);
            str = String.format("%.1f", rotationManager.getRotation2().angularDistanceTo(rotation));
         }

         return "вижу "
            + (vec != null)
            + " | откр "
            + this.items2.getOrDefault(blockPos, 0)
            + "/3 | ост "
            + PvpDetector.method2729(blockPos)
            + " | дист "
            + (int)Math.sqrt(eyePosition.distanceToSqr(Vec3.atCenterOf(blockPos)))
            + " | угол "
            + str;
      }
   }

   private BlockPos method4041() {
      BlockPos blockPosition = mc.player.blockPosition();
      MutableBlockPos mutablePos = new MutableBlockPos();
      BlockPos immutable = null;
      double d = Double.MAX_VALUE;

      for (int i = -16; i <= 16; i++) {
         for (int j = -6; j <= 6; j++) {
            for (int k = -16; k <= 16; k++) {
               mutablePos.set(blockPosition.getX() + i, blockPosition.getY() + j, blockPosition.getZ() + k);
               double d2;
               if (mc.level.getBlockState(mutablePos).is(Blocks.CHEST)
                  && this.method4003(mutablePos)
                  && mc.level.getBlockState(mutablePos.above()).isAir()
                  && (d2 = mc.player.distanceToSqr(Vec3.atCenterOf(mutablePos))) < d) {
                  d = d2;
                  immutable = mutablePos.immutable();
               }
            }
         }
      }

      return immutable;
   }

   private void notify(String str) {
      ChatLogger.method2019("Auto Warden: " + str);
   }

   private static enum Type {
      SAVE,
      TAKE,
      COLLECTING,
      ESCAPE;
   }
}
