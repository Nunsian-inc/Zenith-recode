package org.zenith.client.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.SettingsBackup;
import org.zenith.utility.misc.Util17;

@ModuleInfo(
   name = "AutoZamok",
   category = ModuleCategory.PLAYER,
   description = "module.autozamok.description"
)
public class AutoZamokModule extends Module {
   public static final AutoZamokModule INSTANCE = new AutoZamokModule();
   private static final double[][] CONST_doubleValues = new double[][]{
      {-12.5, 64.0, 0.4},
      {-5.5, 64.0, -5.0},
      {-0.5, 64.0, -12.5},
      {6.5, 64.0, -5.5},
      {6.3, 64.0, 6.7},
      {6.5, 83.0, 6.4},
      {15.6, 79.0, 15.6},
      {6.5, 83.0, -5.5},
      {15.6, 79.0, -14.7},
      {-5.8, 83.0, 6.6},
      {-14.5, 79.0, 15.6},
      {13.2, 64.0, 0.5},
      {-5.6, 64.0, 6.7},
      {-14.3, 79.0, -14.5},
      {-5.5, 83.0, -5.4},
      {-19.5, 54.0, 6.5},
      {-12.0, 55.0, 34.0},
      {13.0, 57.0, 26.5},
      {-2.6, 57.0, 8.7},
      {-15.3, 64.0, -15.4},
      {16.4, 64.0, -15.3}
   };
   private static final BlockPos blockPos = new BlockPos(10, 64, 10);
   private static final String[] texts = new String[]{"инвиз", "невид", "invis"};
   private static final String[] texts2 = new String[]{"кирк", "кирки", "pick"};
   private static final String[] texts3 = new String[]{"склад", "store", "sklad"};
   private static final long CONST_longValue = 70L;
   private static final long CONST_longValue2 = 4000L;
   private static final long CONST_longValue3 = 8000L;
   private static final long CONST_longValue4 = 6000L;
   private static final long CONST_longValue5 = 100L;
   private static final long CONST_longValue6 = 200L;
   private static final long CONST_longValue7 = 500L;
   private static final long CONST_longValue8 = 12000L;
   private static final long CONST_longValue9 = 15000L;
   private static final long CONST_longValue10 = 60000L;
   private static final long CONST_longValue11 = 15000L;
   private static final double CONST_doubleValue = 144.0;
   private static final long CONST_longValue12 = 300000L;
   private static final long CONST_longValue13 = 20000L;
   private static final int CONST_intValue = 3;
   private static final double CONST_doubleValue2 = 2.1;
   private static final long CONST_longValue14 = 20000L;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 200;
   private static final int CONST_intValue5 = 17;
   private static final long CONST_longValue15 = 20000L;
   private static final double CONST_doubleValue3 = 30.0;
   private static final long CONST_longValue16 = 50000L;
   private static final long CONST_longValue17 = 12000L;
   private static final long CONST_longValue18 = 120L;
   private static final long CONST_longValue19 = 180L;
   private static final float CONST_floatValue = 1.35F;
   private static final double CONST_doubleValue4 = 35.0;
   private static final long CONST_longValue20 = 3500L;
   private static final long CONST_longValue21 = 20000L;
   private final SliderSetting boxesPerTrip = new SliderSetting(
      "module.autoZamok.boxesPerTrip", 1.0F, 1.0F, 9.0F, 1.0F, "module.autoZamok.boxesPerTrip.desc", "шт"
   );
   private AutoZamokModule.Type type = AutoZamokModule.Type.TP_TO_SKLAD;
   private long longValue;
   private long longValue2;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private Vec3 vec3d;
   private int intValue;
   private long longValue3;
   private long longValue4;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private int intValue7;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private Vec3 vec3d2;
   private long longValue5;
   private long longValue6;
   private float floatValue;
   private boolean flag4;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private BlockPos blockPos4;
   private long longValue7;
   private boolean flag5;
   private float floatValue2;
   private long longValue8;
   private BlockPos blockPos5;
   private Vec3 vec3d3;
   private long longValue9;
   private long longValue10;
   private final Map<BlockPos, Long> items = new HashMap<>();
   private final Map<BlockPos, Long> items2 = new HashMap<>();
   private int intValue8;
   private int intValue9;
   private boolean flag6;
   private volatile BlockPos blockPos6;
   private volatile long longValue11;
   private BlockPos blockPos7;
   private int intValue10;
   private int intValue11;
   private int intValue12;
   private BlockPos blockPos8;
   private long longValue12;
   private long longValue13;
   private BlockPos blockPos9;
   private long longValue14;
   private long longValue15;
   private long longValue16;
   private volatile long longValue17;
   private volatile int intValue13;
   private volatile long longValue18;
   private volatile boolean flag7;
   private boolean flag8;
   private boolean flag9;
   private long longValue19;

   @Override
   public void onEnable() {
      super.onEnable();
      this.method4206(AutoZamokModule.Type.TP_TO_SKLAD);
      this.longValue4 = 0L;
      this.longValue17 = 0L;
      this.floatValue = 0.0F;
      this.flag4 = false;
      this.flag8 = false;
      this.flag9 = false;
      this.intValue10 = 0;
      this.vec3d3 = null;
      this.blockPos6 = null;
      this.blockPos7 = null;
      this.items2.clear();
      this.method4199();
      Util17.method3334(true);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Util17.method3334(false);
      this.method4204();
      this.method4200();
      this.method4210();
      this.method4132();
      this.method4209();
      if (mc.options != null) {
         mc.options.keyJump.setDown(false);
         if (this.flag4) {
            mc.options.keySprint.setDown(false);
            this.flag4 = false;
         }
      }

      if (mc.getConnection() != null) {
         this.method4159();
      }

      this.flag5 = false;
      this.method4165();
      this.method4140();
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.vec3d = null;
      this.blockPos4 = null;
      this.items.clear();
      this.items2.clear();
      this.blockPos6 = null;
      this.blockPos7 = null;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.getConnection() != null && mc.gameMode != null) {
         long now = System.currentTimeMillis();
         if (this.type != AutoZamokModule.Type.DRINK_INVIS && !this.flag8 && !this.flag9) {
            this.method4209();
         }

         if (this.flag5 && this.type != AutoZamokModule.Type.BREAK_SHULKER) {
            this.method4159();
         }

         if (this.flag3 && this.type != AutoZamokModule.Type.COLLECT && this.type != AutoZamokModule.Type.GO_EXIT) {
            this.method4165();
         }

         if (this.method4207()) {
            this.method4210();
            this.method4132();
            if (now - this.longValue10 >= 1000L) {
               this.longValue10 = now;
               mc.player.respawn();
               if (mc.gui.screen() instanceof DeathScreen) {
                  mc.gui.setScreen(null);
               }
            }

            if (this.type != AutoZamokModule.Type.TP_TO_SKLAD && this.type != AutoZamokModule.Type.WAIT_SKLAD) {
               this.log("Смерть — возвращаемся за снаряжением");
               this.method4204();
               this.method4206(AutoZamokModule.Type.TP_TO_SKLAD);
            }
         } else {
            float health = mc.player.getHealth();
            if (this.floatValue > 0.0F && health < this.floatValue - 0.01F && this.method4149()) {
               Player player = this.method4153(12.0);
               if (player != null) {
                  this.floatValue = health;
                  this.method4150(now, player);
                  return;
               }
            }

            this.floatValue = health;
            if (!this.method4131(now) && !this.method4135(now)) {
               this.method4142();
               if (this.method4139(now)) {
                  this.longValue9 = now;
               } else {
                  this.method4143(now);
                  switch (this.type) {
                     case TP_TO_SKLAD:
                        if (this.method4197(now) && this.method4194(now, "home sklad")) {
                           this.log("/home sklad");
                           this.method4206(AutoZamokModule.Type.WAIT_SKLAD);
                        }
                        break;
                     case WAIT_SKLAD:
                        this.method4195(now, AutoZamokModule.Type.REFILL_INVIS, AutoZamokModule.Type.TP_TO_SKLAD, true);
                        break;
                     case REFILL_INVIS:
                        this.method4129(now, texts, AutoZamokModule.Type.REFILL_PICK, true);
                        break;
                     case REFILL_PICK:
                        this.method4129(now, texts2, AutoZamokModule.Type.DRINK_INVIS, false);
                        break;
                     case DRINK_INVIS:
                        this.method4130(now);
                        break;
                     case TP_TO_ZAMOK:
                        this.closeScreen();
                        if (this.method4197(now) && this.method4194(now, "home zamok")) {
                           this.log("/home zamok");
                           this.method4206(AutoZamokModule.Type.WAIT_ZAMOK);
                        }
                        break;
                     case WAIT_ZAMOK:
                        this.method4195(now, AutoZamokModule.Type.GO_ENTRY, AutoZamokModule.Type.TP_TO_ZAMOK, false);
                        break;
                     case GO_ENTRY:
                        this.method4136(now);
                        break;
                     case GO_SHULKER:
                        this.method4138(now);
                        break;
                     case BREAK_SHULKER:
                        this.method4156(now);
                        break;
                     case COLLECT:
                        this.method4164(now);
                        break;
                     case GO_EXIT:
                        this.method4155(now);
                        break;
                     case FLEE:
                        this.method4152(now);
                        break;
                     case TP_TO_DEPOSIT:
                        mc.gameMode.stopDestroyBlock();
                        if (this.method4197(now) && this.method4194(now, "home sklad")) {
                           this.log("/home sklad (сдача)");
                           this.method4206(AutoZamokModule.Type.WAIT_DEPOSIT);
                        }
                        break;
                     case WAIT_DEPOSIT:
                        this.method4195(now, AutoZamokModule.Type.DEPOSIT, AutoZamokModule.Type.TP_TO_DEPOSIT, true);
                        break;
                     case DEPOSIT:
                        this.method4171(now);
                  }

                  if (this.method4191()) {
                     this.method4192(now);
                  }
               }
            } else {
               this.longValue9 = now;
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100() && event.getPacket3() instanceof ClientboundExplodePacket explodePacket) {
         BlockPos containing = BlockPos.containing(explodePacket.center());
         if (method4128(containing)) {
            this.blockPos6 = containing;
            this.longValue11 = System.currentTimeMillis();
            this.longValue14 = 0L;
         }
      }
   }

   private static boolean method4128(BlockPos containing) {
      for (double[] doubles : CONST_doubleValues) {
         if (containing.distToLowCornerSqr(doubles[0], doubles[1], doubles[2]) <= 144.0) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent event) {
      if (event.getText() != null && this.method4196()) {
         String lowerCase = event.getText().getString().toLowerCase(Locale.ROOT);
         long now = System.currentTimeMillis();
         if (!lowerCase.contains("времени до следующей телепортаци") && (!lowerCase.contains("ошибк") || !lowerCase.contains("телепортаци"))) {
            if (!lowerCase.contains("не двигайтесь") && !lowerCase.contains("начнется через") && !lowerCase.contains("начнётся через")) {
               if (lowerCase.contains("телепортирование") || lowerCase.contains("вы телепортированы")) {
                  this.intValue13 = 1;
                  this.longValue18 = now;
               }
            } else {
               this.flag7 = true;
            }
         } else {
            long l = method4198(lowerCase);
            this.longValue17 = now + (l > 0L ? l * 1000L : 1500L) + 700L;
            this.intValue13 = -1;
            this.longValue18 = now;
         }
      }
   }

   private void method4129(long now, String[] texts, AutoZamokModule.Type type, boolean flag) {
      if (flag ? !this.method4187() : !this.method4183()) {
         if (!(mc.player.containerMenu instanceof ChestMenu chestMenu)) {
            if (this.blockPos2 == null) {
               this.blockPos2 = this.method4177(texts, now, false);
               if (this.blockPos2 == null) {
                  if (now - this.longValue > 6000L) {
                     this.log("Не найден сундук с табличкой " + texts[0]);
                     this.method4206(type);
                  }

                  return;
               }
            }

            if (now - this.longValue2 >= 70L) {
               this.method4181(this.blockPos2);
               this.longValue2 = now;
            }
         } else if (now - this.longValue2 >= 70L) {
            Slot slot = this.method4182(chestMenu, flag ? this::method4185 : this::method4184);
            if (slot != null) {
               mc.gameMode.handleContainerInput(chestMenu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, mc.player);
               this.longValue2 = now;
               this.log("Взял предмет из сундука " + texts[0]);
               if (flag && !this.method4187() && !this.method4168()) {
                  return;
               }
            } else if (!flag || this.method4169(this::method4185) == 0) {
               slot = this.method4182(chestMenu, stack -> true);
               if (slot != null) {
                  mc.gameMode.handleContainerInput(chestMenu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, mc.player);
                  this.longValue2 = now;
                  this.log("Взял предмет из сундука " + texts[0]);
               }
            }

            this.closeScreen();
            this.method4206(type);
         }
      } else {
         this.closeScreen();
         this.method4206(type);
      }
   }

   private void method4130(long now) {
      if (mc.player.hasEffect(MobEffects.INVISIBILITY)) {
         this.method4209();
         this.method4206(AutoZamokModule.Type.TP_TO_ZAMOK);
      } else {
         int slotNum = this.method4188(this::method4185);
         if (slotNum == -1 || now - this.longValue > 5000L) {
            this.method4209();
            this.method4206(AutoZamokModule.Type.TP_TO_ZAMOK);
         } else if (slotNum > 8) {
            this.method4190(slotNum, now);
         } else {
            this.method4211(slotNum);
            this.method4208();
         }
      }
   }

   private boolean method4131(long now) {
      if (!this.method4134() || this.flag8) {
         this.method4132();
         return false;
      } else if (!this.flag9 && !this.method4133()) {
         return false;
      } else if (!this.flag9 || (this.method4133() || mc.player.isUsingItem()) && now - this.longValue19 <= 8000L) {
         int slotNum = this.method4188(this::method4185);
         if (slotNum == -1) {
            this.method4132();
            return false;
         } else {
            if (!this.flag9) {
               this.flag9 = true;
               this.longValue19 = now;
               this.log("Обновляю невидимость");
            }

            this.method4204();
            if (slotNum > 8) {
               this.method4190(slotNum, now);
               return true;
            } else {
               this.method4211(slotNum);
               this.method4208();
               return true;
            }
         }
      } else {
         this.method4132();
         return false;
      }
   }

   private void method4132() {
      if (this.flag9) {
         this.method4209();
         this.flag9 = false;
      }
   }

   private boolean method4133() {
      MobEffectInstance effect = mc.player.getEffect(MobEffects.INVISIBILITY);
      return effect == null ? true : !effect.isInfiniteDuration() && effect.getDuration() <= 200;
   }

   private boolean method4134() {
      return this.type == AutoZamokModule.Type.GO_ENTRY
         || this.type == AutoZamokModule.Type.GO_SHULKER
         || this.type == AutoZamokModule.Type.BREAK_SHULKER
         || this.type == AutoZamokModule.Type.COLLECT
         || this.type == AutoZamokModule.Type.GO_EXIT;
   }

   private boolean method4135(long now) {
      if (!this.method4134()) {
         this.method4210();
         return false;
      } else {
         boolean flag = mc.player.getFoodData().getFoodLevel() <= 17;
         if (!this.flag8 && !flag) {
            return false;
         } else if (this.flag8 && !flag && !mc.player.isUsingItem()) {
            this.method4210();
            return false;
         } else {
            int slotNum = this.method4188(this::method4186);
            if (slotNum == -1) {
               this.method4210();
               return false;
            } else {
               this.flag8 = true;
               this.method4204();
               if (slotNum > 8) {
                  this.method4190(slotNum, now);
                  return true;
               } else {
                  this.method4211(slotNum);
                  this.method4208();
                  return true;
               }
            }
         }
      }
   }

   private void method4136(long now) {
      if (!(mc.player.position().distanceToSqr(Vec3.atCenterOf(blockPos)) <= 9.0)
         && (now - this.longValue <= 2000L || this.method4173(now) == null)
         && now - this.longValue <= 20000L) {
         if (now - this.longValue > 1000L) {
            this.method4201(blockPos, now);
         }
      } else {
         this.method4137(now);
      }
   }

   private void method4137(long now) {
      this.intValue = 0;
      this.longValue3 = now;
      this.blockPos4 = null;
      this.flag = false;
      this.flag2 = false;
      this.method4206(AutoZamokModule.Type.GO_SHULKER);
   }

   private void method4138(long longValue) {
      BlockPos blockPos4 = this.method4173(longValue);
      if (blockPos4 != null) {
         this.flag = false;
         if (!blockPos4.equals(this.blockPos4)) {
            this.blockPos4 = blockPos4;
            this.longValue7 = longValue;
         }

         if (this.method4176(blockPos4) <= 4.41) {
            this.method4204();
            this.blockPos3 = blockPos4;
            this.longValue13 = 0L;
            this.flag5 = false;
            this.floatValue2 = 0.0F;
            this.longValue8 = 0L;
            this.method4160();
            this.intValue11 = this.method4169(null);
            this.intValue12 = this.method4169(this::method4170);
            this.type = AutoZamokModule.Type.BREAK_SHULKER;
            this.longValue = longValue;
         } else if (longValue - this.longValue7 > 15000L) {
            this.log("Бокс недоступен, пропускаю");
            this.items.put(blockPos4, longValue);
            this.blockPos4 = null;
            this.longValue14 = 0L;
            this.method4204();
         } else {
            this.method4202(blockPos4, longValue);
         }
      } else {
         this.blockPos4 = null;
         BlockPos blockPos6 = this.blockPos6;
         if (blockPos6 != null) {
            if (longValue - this.longValue11 <= 15000L && !(mc.player.position().distanceToSqr(Vec3.atCenterOf(blockPos6)) <= 9.0)) {
               if (!blockPos6.equals(this.blockPos7)) {
                  this.blockPos7 = blockPos6;
                  this.log("Взрыв у точки спавна — иду туда");
               }

               this.longValue3 = longValue;
               this.method4202(blockPos6, longValue);
               return;
            }

            this.blockPos6 = null;
         }

         if (this.flag2) {
            int intValue = this.method4148();
            if (intValue == -1) {
               this.longValue9 = longValue;
               if (longValue - this.longValue3 > 50000L) {
                  this.longValue3 = longValue;
                  this.flag = false;
                  this.method4145(longValue);
                  if (this.type == AutoZamokModule.Type.GO_SHULKER) {
                     this.intValue = 0;
                     this.flag2 = false;
                     this.log("Иду на новый обход");
                  }

                  return;
               }

               if (!this.flag) {
                  this.method4146();
                  this.log("Боксов нет — жду спавн в укрытии");
               }

               double d = mc.player.getX() - (this.intValue6 + 0.5);
               double d2 = mc.player.getZ() - (this.intValue7 + 0.5);
               if (d * d + d2 * d2 <= 9.0) {
                  this.method4204();
               } else {
                  this.method4203(this.intValue6, this.intValue7, longValue);
               }

               return;
            }

            if (intValue != this.intValue) {
               this.intValue = intValue;
               this.longValue3 = longValue;
            }
         }

         this.flag = false;
         double[] doubles = CONST_doubleValues[this.intValue];
         BlockPos containing = BlockPos.containing(doubles[0], doubles[1], doubles[2]);
         if (!(mc.player.position().distanceToSqr(Vec3.atCenterOf(containing)) <= 9.0) && longValue - this.longValue3 <= 12000L) {
            this.method4201(containing, longValue);
         } else {
            this.method4144(longValue);
         }
      }
   }

   private boolean method4139(long now) {
      boolean flag = this.type == AutoZamokModule.Type.GO_ENTRY
         || this.type == AutoZamokModule.Type.GO_SHULKER
         || this.type == AutoZamokModule.Type.GO_EXIT
         || this.type == AutoZamokModule.Type.FLEE;
      if (!flag) {
         this.method4140();
         return false;
      } else {
         boolean flag2 = this.method4141();
         if (this.vec3d2 == null) {
            if (!flag2 || this.blockPos8 == null) {
               return false;
            }

            this.vec3d2 = new Vec3(this.blockPos8.getX() + 0.5, mc.player.getY(), this.blockPos8.getZ() + 0.5);
            this.longValue5 = now;
            this.method4204();
         } else if (!flag2 && now - this.longValue6 > 700L) {
            this.method4140();
            return false;
         }

         if (flag2) {
            this.longValue6 = now;
         }

         if (now - this.longValue5 <= 20000L) {
            Util17.method3336(this.vec3d2);
            mc.options.keyJump.setDown(true);
            return true;
         } else {
            this.log("Застрял в паутине — повторяю заход");
            this.method4140();
            this.method4204();
            this.method4206(
               this.type != AutoZamokModule.Type.GO_EXIT && this.type != AutoZamokModule.Type.FLEE
                  ? AutoZamokModule.Type.TP_TO_ZAMOK
                  : AutoZamokModule.Type.TP_TO_DEPOSIT
            );
            return false;
         }
      }
   }

   private void method4140() {
      if (this.vec3d2 != null) {
         this.vec3d2 = null;
         Util17.method3337();
         mc.options.keyJump.setDown(false);
      }
   }

   private boolean method4141() {
      AABB inflate = mc.player.getBoundingBox().inflate(0.05);

      for (BlockPos pos : BlockPos.betweenClosed(
         Mth.floor(inflate.minX),
         Mth.floor(inflate.minY),
         Mth.floor(inflate.minZ),
         Mth.floor(inflate.maxX),
         Mth.floor(inflate.maxY),
         Mth.floor(inflate.maxZ)
      )) {
         if (mc.level.getBlockState(pos).getBlock() == Blocks.COBWEB) {
            return true;
         }
      }

      return false;
   }

   private void method4142() {
      boolean flag = mc.player.isInWater() && this.method4205();
      if (flag) {
         mc.options.keySprint.setDown(true);
         this.flag4 = true;
      } else if (this.flag4) {
         mc.options.keySprint.setDown(false);
         this.flag4 = false;
      }
   }

   private void method4143(long now) {
      boolean flag = this.type == AutoZamokModule.Type.GO_ENTRY || this.type == AutoZamokModule.Type.GO_SHULKER || this.type == AutoZamokModule.Type.GO_EXIT;
      Vec3 position = mc.player.position();
      if (flag && this.vec3d3 != null && !(position.distanceToSqr(this.vec3d3) > 0.25)) {
         if (now - this.longValue9 > 20000L) {
            this.log("Застрял — повторяю заход на замок");
            if (this.blockPos4 != null) {
               this.items.put(this.blockPos4, now);
               this.blockPos4 = null;
            }

            this.vec3d3 = null;
            this.longValue9 = now;
            this.method4204();
            this.method4206(this.type == AutoZamokModule.Type.GO_EXIT ? AutoZamokModule.Type.TP_TO_DEPOSIT : AutoZamokModule.Type.TP_TO_ZAMOK);
         }
      } else {
         this.vec3d3 = position;
         this.longValue9 = now;
      }
   }

   private void method4144(long longValue) {
      this.method4204();
      this.longValue3 = longValue;
      if (++this.intValue >= CONST_doubleValues.length) {
         this.intValue = 0;
         this.flag2 = true;
         this.method4145(longValue);
      }
   }

   private void method4145(long longValue) {
      if (this.intValue10 <= 0 && !this.method4167()) {
         this.items.clear();
      } else {
         this.log("Обход завершён, боксов выбито: " + this.intValue10);
         this.method4154(longValue);
      }
   }

   private void method4146() {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      double d = -1.0;

      for (int i = 0; i < 8; i++) {
         double nextDouble = current.nextDouble(Math.PI * 2);
         double d2 = blockPos.getX() + 0.5 + Math.cos(nextDouble) * 35.0;
         double d3 = blockPos.getZ() + 0.5 + Math.sin(nextDouble) * 35.0;
         double d4 = Double.MAX_VALUE;

         for (double[] doubles : CONST_doubleValues) {
            double d5 = d2 - doubles[0];
            double d6 = d3 - doubles[2];
            d4 = Math.min(d4, d5 * d5 + d6 * d6);
         }

         if (d4 > d) {
            d = d4;
            this.intValue6 = Mth.floor(d2);
            this.intValue7 = Mth.floor(d3);
         }
      }

      this.flag = true;
   }

   private boolean method4147(int n) {
      double[] doubles = CONST_doubleValues[n];
      return mc.level.hasChunk(Mth.floor(doubles[0]) >> 4, Mth.floor(doubles[2]) >> 4);
   }

   private int method4148() {
      for (int i = 0; i < CONST_doubleValues.length; i++) {
         int n = (this.intValue + i) % CONST_doubleValues.length;
         if (!this.method4147(n)) {
            return n;
         }
      }

      return -1;
   }

   private boolean method4149() {
      return switch (this.type) {
         case TP_TO_SKLAD, WAIT_SKLAD, TP_TO_ZAMOK, WAIT_ZAMOK, GO_ENTRY, GO_SHULKER, BREAK_SHULKER, COLLECT, GO_EXIT, FLEE, TP_TO_DEPOSIT, WAIT_DEPOSIT -> true;
         default -> false;
      };
   }

   private void method4150(long longValue, Player player) {
      this.method4210();
      this.method4132();
      this.method4159();
      if (mc.options != null) {
         mc.options.keyJump.setDown(false);
      }

      this.blockPos6 = null;
      this.method4151(player);
      if (this.type != AutoZamokModule.Type.FLEE) {
         this.log("Получил урон от игрока — убегаю");
         this.method4206(AutoZamokModule.Type.FLEE);
      } else {
         this.longValue = longValue;
      }
   }

   private void method4151(Player player) {
      Vec3 position = mc.player.position();
      Vec3 vec = new Vec3(position.x - player.getX(), 0.0, position.z - player.getZ());
      if (vec.lengthSqr() < 0.01) {
         vec = new Vec3(1.0, 0.0, 0.0);
      }

      vec = vec.normalize();
      this.intValue4 = Mth.floor(position.x + vec.x * 25.0);
      this.intValue5 = Mth.floor(position.z + vec.z * 25.0);
   }

   private void method4152(long now) {
      Player player = this.method4153(20.0);
      if (player != null && now - this.longValue <= 12000L) {
         double d = mc.player.getX() - this.intValue4;
         double d2 = mc.player.getZ() - this.intValue5;
         if (d * d + d2 * d2 <= 9.0) {
            this.method4151(player);
         }

         this.method4203(this.intValue4, this.intValue5, now);
      } else {
         this.method4204();
         this.method4206(AutoZamokModule.Type.TP_TO_DEPOSIT);
      }
   }

   private Player method4153(double d) {
      Player player = null;
      double d2 = d * d;

      for (Player player2 : mc.level.players()) {
         if (player2 != mc.player && !player2.isSpectator()) {
            double d3 = player2.distanceToSqr(mc.player);
            if (d3 < d2) {
               d2 = d3;
               player = player2;
            }
         }
      }

      return player;
   }

   private void method4154(long longValue) {
      Vec3 position = mc.player.position();
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos);
      Vec3 vec = new Vec3(position.x - atCenterOf.x, 0.0, position.z - atCenterOf.z);
      if (vec.lengthSqr() < 1.0) {
         vec = new Vec3(1.0, 0.0, 0.0);
      }

      vec = vec.normalize();
      this.intValue2 = Mth.floor(atCenterOf.x + vec.x * 30.0);
      this.intValue3 = Mth.floor(atCenterOf.z + vec.z * 30.0);
      this.log("Ухожу из замка перед телепортом");
      this.method4206(AutoZamokModule.Type.GO_EXIT);
   }

   private void method4155(long now) {
      double d = mc.player.getX() - this.intValue2;
      double d2 = mc.player.getZ() - this.intValue3;
      if (!(d * d + d2 * d2 <= 16.0) && now - this.longValue <= 20000L) {
         this.method4203(this.intValue2, this.intValue3, now);
         if (!this.method4205()) {
            this.flag3 = true;
            Util17.method3336(new Vec3(this.intValue2 + 0.5, mc.player.getY(), this.intValue3 + 0.5));
         } else if (this.flag3) {
            this.method4165();
         }
      } else {
         this.method4204();
         this.method4165();
         this.method4206(AutoZamokModule.Type.TP_TO_DEPOSIT);
      }
   }

   private void method4156(long now) {
      BlockPos blockPos3 = this.blockPos3;
      if (blockPos3 == null) {
         this.method4159();
         this.method4206(AutoZamokModule.Type.COLLECT);
      } else {
         boolean flag = now - this.longValue >= 20000L;
         if (this.method4175(blockPos3)) {
            this.longValue13 = 0L;
            double d = 2.6;
            if (this.method4176(blockPos3) > d * d) {
               this.method4159();
               this.method4206(AutoZamokModule.Type.GO_SHULKER);
               return;
            }

            if (!this.method4189(now)) {
               return;
            }

            this.method4162(this.method4161(blockPos3));
            if (!this.flag5) {
               if (now >= this.longValue8) {
                  this.method4157(blockPos3);
               }
            } else {
               this.floatValue2 = this.floatValue2 + mc.level.getBlockState(blockPos3).getDestroyProgress(mc.player, mc.level, blockPos3);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
               mc.level.destroyBlockProgress(mc.player.getId(), blockPos3, Mth.clamp((int)(this.floatValue2 * 10.0F), 0, 9));
               if (this.floatValue2 >= 1.35F) {
                  this.method4158(now);
               }
            }
         } else {
            this.flag5 = false;
            if (this.longValue13 == 0L) {
               this.longValue13 = now;
            }

            if (now - this.longValue13 >= 100L) {
               this.vec3d = Vec3.atCenterOf(blockPos3);
               this.method4206(AutoZamokModule.Type.COLLECT);
               return;
            }
         }

         if (flag) {
            this.method4159();
            if (this.method4175(blockPos3)) {
               this.log("Бокс не поддаётся — пропускаю");
               this.items.put(blockPos3, now);
               this.blockPos4 = null;
               this.longValue14 = 0L;
               this.method4206(AutoZamokModule.Type.GO_SHULKER);
            } else {
               this.vec3d = Vec3.atCenterOf(blockPos3);
               this.method4206(AutoZamokModule.Type.COLLECT);
            }
         }
      }
   }

   private void method4157(BlockPos blockPos5) {
      Direction direction = this.method4163(blockPos5);
      mc.gameMode.startPrediction(mc.level, var2x -> new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos5, direction, var2x));
      this.flag5 = true;
      this.blockPos5 = blockPos5;
      this.floatValue2 = 0.0F;
      org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
   }

   private void method4158(long now) {
      BlockPos blockPos5 = this.blockPos5;
      Direction direction = this.method4163(blockPos5);
      mc.gameMode.startPrediction(mc.level, sequence -> new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, blockPos5, direction, sequence));
      mc.level.destroyBlockProgress(mc.player.getId(), blockPos5, -1);
      this.flag5 = false;
      this.longValue8 = now + 120L + ThreadLocalRandom.current().nextLong(180L);
      this.method4160();
   }

   private void method4159() {
      if (this.flag5) {
         mc.getConnection().send(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.blockPos5, Direction.DOWN));
         mc.level.destroyBlockProgress(mc.player.getId(), this.blockPos5, -1);
         this.flag5 = false;
      }
   }

   private void method4160() {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      this.doubleValue = current.nextDouble(-0.18, 0.18);
      this.doubleValue2 = current.nextDouble(-0.18, 0.18);
      this.doubleValue3 = current.nextDouble(-0.18, 0.18);
   }

   private Vec3 method4161(BlockPos blockPos3) {
      return new Vec3(blockPos3.getX() + 0.5 + this.doubleValue, blockPos3.getY() + 0.5 + this.doubleValue2, blockPos3.getZ() + 0.5 + this.doubleValue3);
   }

   private void method4162(Vec3 atCenterOf) {
      Util17.method3329(atCenterOf);
   }

   private Direction method4163(BlockPos blockPos5) {
      Vec3 vec = mc.player.getEyePosition().subtract(Vec3.atCenterOf(blockPos5));
      double d = Math.abs(vec.x);
      double d2 = Math.abs(vec.y);
      double d3 = Math.abs(vec.z);
      if (d >= d2 && d >= d3) {
         return vec.x >= 0.0 ? Direction.EAST : Direction.WEST;
      } else if (d3 >= d && d3 >= d2) {
         return vec.z >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      } else {
         return vec.y >= 0.0 ? Direction.UP : Direction.DOWN;
      }
   }

   private void method4164(long now) {
      this.method4204();
      boolean flag = this.method4169(this::method4170) > this.intValue12 || this.method4169(null) > this.intValue11;
      if (flag) {
         this.intValue10++;
         this.log("Бокс выбит (" + this.intValue10 + "/" + (int)this.boxesPerTrip.getValue3() + ")");
         this.method4166(now, true);
      } else if (now - this.longValue >= 3500L) {
         this.log("Бокс достался не нам, продолжаю обход");
         this.method4166(now, false);
      } else if (this.vec3d != null) {
         double d = this.vec3d.x - mc.player.getX();
         double d2 = this.vec3d.z - mc.player.getZ();
         if (d * d + d2 * d2 > 0.64) {
            this.flag3 = true;
            Util17.method3336(this.vec3d);
         } else {
            this.method4165();
         }
      }
   }

   private void method4165() {
      if (this.flag3) {
         this.flag3 = false;
         Util17.method3337();
      }
   }

   private void method4166(long now, boolean flag) {
      this.method4165();
      if ((!flag || this.intValue10 < (int)this.boxesPerTrip.getValue3()) && !this.method4168() && !this.method4167()) {
         this.longValue3 = now;
         this.blockPos4 = null;
         this.method4206(AutoZamokModule.Type.GO_SHULKER);
      } else {
         this.method4154(now);
      }
   }

   private boolean method4167() {
      return this.method4188(this::method4184) == -1 ? true : this.method4133() && this.method4188(this::method4185) == -1;
   }

   private boolean method4168() {
      for (int i = 0; i < 36; i++) {
         if (mc.player.getInventory().getItem(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private int method4169(Predicate<ItemStack> predicate) {
      int n = 0;
      int containerSize = mc.player.getInventory().getContainerSize();

      for (int i = 0; i < containerSize; i++) {
         ItemStack stack = mc.player.getInventory().getItem(i);
         if (!stack.isEmpty() && (predicate == null || predicate.test(stack))) {
            n += stack.getCount();
         }
      }

      return n;
   }

   private boolean method4170(ItemStack stack) {
      return stack != null && !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().endsWith("shulker_box");
   }

   private void method4171(long now) {
      if (mc.player.containerMenu instanceof ChestMenu chestMenu) {
         if (now - this.longValue2 >= 70L) {
            int n = this.method4169(null);
            if (this.flag6) {
               this.flag6 = false;
               if (n == this.intValue9) {
                  if (++this.intValue8 >= 2) {
                     this.log("Сундук забит — ищу другой");
                     this.method4180(this.blockPos2, now);
                     this.closeScreen();
                     this.method4206(AutoZamokModule.Type.DEPOSIT);
                     return;
                  }
               } else {
                  this.intValue8 = 0;
               }
            }

            int containerSize = chestMenu.getContainer().getContainerSize();

            for (Slot slot : chestMenu.slots) {
               if (slot.index >= containerSize) {
                  ItemStack stack = slot.getItem();
                  if (!stack.isEmpty() && !this.method4184(stack) && !this.method4185(stack) && !this.method4186(stack)) {
                     mc.gameMode.handleContainerInput(chestMenu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, mc.player);
                     this.longValue2 = now;
                     this.intValue9 = n;
                     this.flag6 = true;
                     return;
                  }
               }
            }

            this.log("Ресурсы сложены");
            this.closeScreen();
            this.method4172();
         }
      } else {
         if (this.blockPos2 == null) {
            this.blockPos2 = this.method4177(texts3, now, true);
            if (this.blockPos2 == null) {
               if (now - this.longValue > 6000L) {
                  this.log(this.items2.isEmpty() ? "Не найден сундук 'склад'" : "Все сундуки 'склад' забиты");
                  this.method4172();
               }

               return;
            }
         }

         if (now - this.longValue2 >= 70L) {
            this.method4181(this.blockPos2);
            this.longValue2 = now;
         }
      }
   }

   private void method4172() {
      this.closeScreen();
      this.intValue10 = 0;
      this.items.clear();
      this.method4206(AutoZamokModule.Type.REFILL_INVIS);
   }

   private BlockPos method4173(long now) {
      if (now - this.longValue14 >= 200L) {
         this.longValue14 = now;
         this.items.values().removeIf(l -> now - l > 60000L);
         this.blockPos9 = this.method4174();
      }

      return this.blockPos9;
   }

   private BlockPos method4174() {
      byte b = 3;
      double d = b * 1.7321 + 1.0;
      Vec3 eyePosition = mc.player.getEyePosition();
      MutableBlockPos mutablePos = new MutableBlockPos();
      BlockPos immutable = null;
      double d2 = Double.MAX_VALUE;

      for (double[] doubles : CONST_doubleValues) {
         double d3 = Math.sqrt(eyePosition.distanceToSqr(doubles[0], doubles[1], doubles[2])) - d;
         if (!(d3 > 0.0) || !(d3 * d3 >= d2)) {
            int n = Mth.floor(doubles[0]);
            int n2 = Mth.floor(doubles[1]);
            int n3 = Mth.floor(doubles[2]);

            for (int i = -b; i <= b; i++) {
               for (int j = -b; j <= b; j++) {
                  for (int k = -b; k <= b; k++) {
                     mutablePos.set(n + i, n2 + j, n3 + k);
                     if (mc.level.getBlockState(mutablePos).getBlock() instanceof ShulkerBoxBlock && !this.items.containsKey(mutablePos)) {
                        double d4 = eyePosition.distanceToSqr(mutablePos.getX() + 0.5, mutablePos.getY() + 0.5, mutablePos.getZ() + 0.5);
                        if (d4 < d2) {
                           d2 = d4;
                           immutable = mutablePos.immutable();
                        }
                     }
                  }
               }
            }
         }
      }

      return immutable;
   }

   private boolean method4175(BlockPos blockPos3) {
      return mc.level.getBlockState(blockPos3).getBlock() instanceof ShulkerBoxBlock;
   }

   private double method4176(BlockPos blockPos4) {
      Vec3 eyePosition = mc.player.getEyePosition();
      double d = Math.max(Math.max(blockPos4.getX() - eyePosition.x, 0.0), eyePosition.x - (blockPos4.getX() + 1));
      double d2 = Math.max(Math.max(blockPos4.getY() - eyePosition.y, 0.0), eyePosition.y - (blockPos4.getY() + 1));
      double d3 = Math.max(Math.max(blockPos4.getZ() - eyePosition.z, 0.0), eyePosition.z - (blockPos4.getZ() + 1));
      return d * d + d2 * d2 + d3 * d3;
   }

   private BlockPos method4177(String[] texts, long now, boolean flag) {
      if (now - this.longValue15 < 500L) {
         return null;
      } else {
         this.longValue15 = now;
         this.items2.values().removeIf(var2x -> now - var2x > 300000L);
         byte b = 10;
         double d = b * b;
         BlockPos blockPosition = mc.player.blockPosition();
         BlockPos pos = null;
         double distSqr = Double.MAX_VALUE;
         int n = Integer.MAX_VALUE;
         int n2 = (b >> 4) + 1;
         int x = mc.player.chunkPosition().x();
         int z = mc.player.chunkPosition().z();

         for (int i = x - n2; i <= x + n2; i++) {
            for (int j = z - n2; j <= z + n2; j++) {
               if (mc.level.hasChunk(i, j)) {
                  LevelChunk chunk = mc.level.getChunk(i, j);
                  if (chunk != null) {
                     for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                        if (blockEntity instanceof SignBlockEntity signBlockEntity && !(blockPosition.distSqr(signBlockEntity.getBlockPos()) > d)) {
                           int n3 = this.method4178(signBlockEntity, texts);
                           if (n3 >= 0) {
                              if (!flag) {
                                 n3 = 0;
                              }

                              BlockPos pos2 = this.method4179(signBlockEntity.getBlockPos());
                              if (pos2 != null && !this.items2.containsKey(pos2)) {
                                 double distSqr2 = blockPosition.distSqr(pos2);
                                 if (n3 < n || n3 == n && distSqr2 < distSqr) {
                                    n = n3;
                                    distSqr = distSqr2;
                                    pos = pos2;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return pos;
      }
   }

   private int method4178(SignBlockEntity signBlockEntity, String[] texts) {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 4; i++) {
         builder.append(signBlockEntity.getText(net.minecraft.world.level.block.entity.SignTextSlot.FRONT).getMessages(false).get(i).getString()).append(' ');
         builder.append(signBlockEntity.getText(net.minecraft.world.level.block.entity.SignTextSlot.BACK).getMessages(false).get(i).getString()).append(' ');
      }

      String lowerCase = builder.toString().toLowerCase(Locale.ROOT);

      for (String text : texts) {
         int indexOf = lowerCase.indexOf(text);
         if (indexOf != -1) {
            int index = indexOf + text.length();

            while (index < lowerCase.length() && !Character.isDigit(lowerCase.charAt(index)) && !Character.isLetter(lowerCase.charAt(index))) {
               index++;
            }

            int n = 0;

            boolean flag;
            for (flag = false; index < lowerCase.length() && Character.isDigit(lowerCase.charAt(index)); index++) {
               n = n * 10 + (lowerCase.charAt(index) - '0');
               flag = true;
            }

            return flag ? n : 1;
         }
      }

      return -1;
   }

   private BlockPos method4179(BlockPos blockPos) {
      BlockState blockState = mc.level.getBlockState(blockPos);
      if (blockState.getBlock() instanceof WallSignBlock) {
         BlockPos relative = blockPos.relative(blockState.getValue(WallSignBlock.FACING).getOpposite());
         if (mc.level.getBlockState(relative).getBlock() instanceof ChestBlock) {
            return relative;
         }
      } else if (blockState.getBlock() instanceof StandingSignBlock) {
         BlockPos below = blockPos.below();
         if (mc.level.getBlockState(below).getBlock() instanceof ChestBlock) {
            return below;
         }
      }

      if (blockState.getBlock() instanceof ChestBlock) {
         return blockPos.immutable();
      } else {
         for (Direction direction : Direction.values()) {
            BlockPos relative = blockPos.relative(direction);
            if (mc.level.getBlockState(relative).getBlock() instanceof ChestBlock) {
               return relative;
            }

            BlockPos below = relative.below();
            if (mc.level.getBlockState(below).getBlock() instanceof ChestBlock) {
               return below;
            }
         }

         return null;
      }
   }

   private void method4180(BlockPos blockPos2, long now) {
      this.items2.put(blockPos2, now);
      BlockState blockState = mc.level.getBlockState(blockPos2);
      if (blockState.getBlock() instanceof ChestBlock && blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
         this.items2.put(blockPos2.relative(ChestBlock.getConnectedDirection(blockState)), now);
      }
   }

   private void method4181(BlockPos blockPos2) {
      Vec3 atCenterOf = Vec3.atCenterOf(blockPos2);
      this.method4212(atCenterOf);
      mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(atCenterOf, Direction.UP, blockPos2, false));
      org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
   }

   private Slot method4182(ChestMenu chestMenu, Predicate<ItemStack> predicate) {
      int containerSize = chestMenu.getContainer().getContainerSize();

      for (Slot slot : chestMenu.slots) {
         if (slot.index >= containerSize) {
            break;
         }

         if (slot.hasItem() && predicate.test(slot.getItem())) {
            return slot;
         }
      }

      return null;
   }

   private boolean method4183() {
      return this.method4188(this::method4184) != -1;
   }

   private boolean method4184(ItemStack stack) {
      return stack != null && !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("pickaxe");
   }

   private boolean method4185(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof PotionItem) {
         PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
         if (potionContents == null) {
            return false;
         } else {
            for (MobEffectInstance allEffect : potionContents.getAllEffects()) {
               if (allEffect.getEffect().equals(MobEffects.INVISIBILITY)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method4186(ItemStack stack) {
      return stack != null
         && !stack.isEmpty()
         && !stack.is(Items.CHORUS_FRUIT)
         && !stack.is(Items.SPIDER_EYE)
         && !stack.is(Items.POISONOUS_POTATO)
         && !stack.is(Items.PUFFERFISH)
         && !stack.is(Items.ROTTEN_FLESH)
         && stack.getUseAnimation() == ItemUseAnimation.EAT;
   }

   private boolean method4187() {
      int n = 2;
      if (!mc.player.hasEffect(MobEffects.INVISIBILITY)) {
         n++;
      }

      return this.method4169(this::method4185) >= n;
   }

   private int method4188(Predicate<ItemStack> predicate) {
      byte b = 36;

      for (int i = 0; i < 9 && i < b; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      for (int i = 9; i < b; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private boolean method4189(long now) {
      int slotNum = this.method4188(this::method4184);
      if (slotNum == -1) {
         return false;
      } else if (slotNum <= 8) {
         this.method4211(slotNum);
         return true;
      } else {
         this.method4190(slotNum, now);
         return false;
      }
   }

   private void method4190(int slotNum, long now) {
      if (now - this.longValue2 >= 70L) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, slotNum, 8, ContainerInput.SWAP, mc.player);
         this.longValue2 = now;
      }
   }

   private boolean method4191() {
      if (!this.flag8 && !this.flag9) {
         return switch (this.type) {
            case TP_TO_SKLAD, WAIT_SKLAD, TP_TO_ZAMOK, WAIT_ZAMOK, GO_ENTRY, GO_SHULKER, COLLECT, GO_EXIT, FLEE, TP_TO_DEPOSIT, WAIT_DEPOSIT -> true;
            default -> false;
         };
      } else {
         return false;
      }
   }

   private void method4192(long now) {
      if (!mc.player.getInventory().getItem(mc.player.getInventory().getSelectedSlot()).isEmpty()) {
         for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).isEmpty()) {
               this.method4211(i);
               return;
            }
         }

         if (now - this.longValue2 >= 70L && this.method4193()) {
            int n = -1;

            for (int i = 0; i < 9; i++) {
               ItemStack stack = mc.player.getInventory().getItem(i);
               if (!this.method4184(stack) && !this.method4185(stack) && !this.method4186(stack)) {
                  n = i;
                  break;
               }
            }

            if (n == -1) {
               for (int i = 0; i < 9; i++) {
                  if (!this.method4184(mc.player.getInventory().getItem(i))) {
                     n = i;
                     break;
                  }
               }
            }

            if (n != -1) {
               mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, 36 + n, 0, ContainerInput.QUICK_MOVE, mc.player);
               this.longValue2 = now;
            }
         }
      }
   }

   private boolean method4193() {
      for (int i = 9; i < 36; i++) {
         if (mc.player.getInventory().getItem(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private boolean method4194(long now, String command) {
      if (now >= this.longValue17 && now - this.longValue4 >= 4000L) {
         this.intValue13 = 0;
         this.longValue18 = 0L;
         this.flag7 = false;
         this.longValue16 = now;
         this.longValue4 = now;
         mc.getConnection().sendCommand(command);
         return true;
      } else {
         return false;
      }
   }

   private void method4195(long now, AutoZamokModule.Type type, AutoZamokModule.Type type2, boolean flag) {
      if (this.intValue13 == 1) {
         if (flag) {
            mc.options.keyJump.setDown(now - this.longValue18 < 300L);
         }

         if (now - this.longValue18 >= 1000L) {
            this.method4206(type);
         }
      } else if (this.intValue13 == -1) {
         if (now >= this.longValue17 && now - this.longValue4 >= 4000L) {
            this.method4206(type2);
         }
      } else {
         if (now - this.longValue16 > (this.flag7 ? 12000L : 8000L)) {
            this.method4206(type2);
         }
      }
   }

   private boolean method4196() {
      return this.type == AutoZamokModule.Type.WAIT_SKLAD || this.type == AutoZamokModule.Type.WAIT_ZAMOK || this.type == AutoZamokModule.Type.WAIT_DEPOSIT;
   }

   private boolean method4197(long now) {
      this.method4204();
      Vec3 deltaMovement = mc.player.getDeltaMovement();
      return Math.abs(deltaMovement.x) < 0.05 && Math.abs(deltaMovement.z) < 0.05 || now - this.longValue > 2000L;
   }

   private static long method4198(String lowerCase) {
      long l = 0L;
      boolean flag = false;

      for (int i = 0; i < lowerCase.length(); i++) {
         char charAt = lowerCase.charAt(i);
         if (charAt >= '0' && charAt <= '9') {
            l = l * 10L + (charAt - '0');
            flag = true;
         } else if (flag) {
            break;
         }
      }

      return flag ? l : 0L;
   }

   private void method4199() {
      SettingsBackup.apply();
   }

   private void method4200() {
      SettingsBackup.restore();
   }

   private void method4201(BlockPos blockPos8, long now) {
      if (!blockPos8.equals(this.blockPos8) || !this.method4205() && now - this.longValue12 >= 6000L) {
         this.method4199();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalNear(blockPos8, 2));
         this.blockPos8 = blockPos8;
         this.longValue12 = now;
      }
   }

   private void method4202(BlockPos blockPos8, long longValue) {
      if (!blockPos8.equals(this.blockPos8) || !this.method4205() && longValue - this.longValue12 >= 6000L) {
         this.method4199();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(blockPos8));
         this.blockPos8 = blockPos8;
         this.longValue12 = longValue;
      }
   }

   private void method4203(int x, int z, long now) {
      BlockPos pos = new BlockPos(x, 0, z);
      if (!pos.equals(this.blockPos8) || !this.method4205() && now - this.longValue12 >= 6000L) {
         this.method4199();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(x, z));
         this.blockPos8 = pos;
         this.longValue12 = now;
      }
   }

   private void method4204() {
      if (this.blockPos8 != null || this.method4205()) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().onLostControl();
         this.blockPos8 = null;
      }
   }

   private boolean method4205() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   private void method4206(AutoZamokModule.Type type) {
      this.type = type;
      this.longValue = System.currentTimeMillis();
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.longValue14 = 0L;
      this.longValue15 = 0L;
      this.intValue8 = 0;
      this.flag6 = false;
   }

   private boolean method4207() {
      return mc.gui.screen() instanceof DeathScreen || !mc.player.isAlive();
   }

   private void method4208() {
      mc.options.keyShift.setDown(true);
      mc.options.keyUse.setDown(true);
      if (!mc.player.isUsingItem() || mc.player.getUsedItemHand() != InteractionHand.MAIN_HAND) {
         mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
      }
   }

   private void method4209() {
      if (mc.options != null) {
         mc.options.keyUse.setDown(false);
         mc.options.keyShift.setDown(false);
      }
   }

   private void method4210() {
      if (this.flag8) {
         this.method4209();
         this.flag8 = false;
      }
   }

   private void closeScreen() {
      if (mc.player.containerMenu != mc.player.inventoryMenu) {
         mc.player.closeContainer();
      }
   }

   private void method4211(int slotNum) {
      if (slotNum >= 0 && slotNum <= 8 && mc.player.getInventory().getSelectedSlot() != slotNum) {
         mc.player.getInventory().setSelectedSlot(slotNum);
         mc.getConnection().send(new ServerboundSetCarriedItemPacket(slotNum));
      }
   }

   private void method4212(Vec3 atCenterOf) {
      Util17.method3329(atCenterOf);
   }

   private void log(String str) {
      ChatLogger.method2019("AutoZamok: " + str);
   }

   private static enum Type {
      TP_TO_SKLAD,
      WAIT_SKLAD,
      REFILL_INVIS,
      REFILL_PICK,
      DRINK_INVIS,
      TP_TO_ZAMOK,
      WAIT_ZAMOK,
      GO_ENTRY,
      GO_SHULKER,
      BREAK_SHULKER,
      COLLECT,
      GO_EXIT,
      FLEE,
      TP_TO_DEPOSIT,
      WAIT_DEPOSIT,
      DEPOSIT;
   }
}
