package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PotionUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoThrow",
   category = ModuleCategory.COMBAT,
   description = "module.autothrow.description"
)
public final class AutoThrowModule extends Module {
   public static final AutoThrowModule INSTANCE = new AutoThrowModule();
   private static final int CONST_intValue = 8;
   private static final float CONST_floatValue = 5.0F;
   private final ModeSetting spec = new ModeSetting(
      "module.autoThrow.spec", "module.autoThrow.spec.desc", "module.autoThrow.spec.heal", "module.autoThrow.spec.debuff"
   );
   private final ModeSetting trigger = new ModeSetting(
      "module.autoThrow.trigger", "module.autoThrow.trigger.desc", "module.autoThrow.trigger.auto", "module.autoThrow.trigger.bind"
   );
   private final BindSetting key = new BindSetting("module.autoThrow.key", "module.autoThrow.key.desc", () -> this.trigger.isSelected(1));
   private final BooleanSetting totemCooldown = new BooleanSetting(
      "module.autoThrow.totemCooldown", "module.autoThrow.totemCooldown.desc", false, () -> this.trigger.isSelected(0) && this.spec.isSelected(1)
   );
   private final SliderSetting targetHealth = new SliderSetting(
      "module.autoThrow.targetHealth",
      15.0F,
      1.0F,
      20.0F,
      0.5F,
      "module.autoThrow.targetHealth.desc",
      "hp",
      () -> this.trigger.isSelected(0) && this.spec.isSelected(0)
   );
   private final SliderSetting selfHealth = new SliderSetting(
      "module.autoThrow.selfHealth",
      15.0F,
      1.0F,
      20.0F,
      0.5F,
      "module.autoThrow.selfHealth.desc",
      "hp",
      () -> this.trigger.isSelected(0) && this.spec.isSelected(1)
   );
   private final SliderSetting distance = new SliderSetting(
      "module.autoThrow.distance", 3.0F, 1.0F, 6.0F, 0.1F, "module.autoThrow.distance.desc", "b", () -> this.trigger.isSelected(0) && this.spec.isSelected(0)
   );
   private final StopWatch stopWatch = new StopWatch();
   private final Map<UUID, Long> items = new ConcurrentHashMap<>();
   private volatile Vec3 vec3d;
   private Level world;
   private int step = -1;
   private int intValue;
   private int intValue2 = -1;
   private int intValue3 = -1;
   private boolean flag;
   private Player playerEntity;
   private static final long CONST_longValue = 5000L;
   private final LivingEntity[] livingEntitys = new LivingEntity[3];
   private final StopWatch stopWatch2 = new StopWatch();
   private LivingEntity livingEntity;

   private AutoThrowModule() {
   }

   @Override
   public void onEnable() {
      this.reset();
      this.resetTargets();
      this.items.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.step >= 0 && mc.player != null) {
         this.method7116();
      }

      this.reset();
      this.resetTargets();
      super.onDisable();
   }

   private void reset() {
      this.step = -1;
      this.intValue = 0;
      this.intValue2 = -1;
      this.intValue3 = -1;
      this.flag = false;
      this.playerEntity = null;
   }

   private void resetTargets() {
      this.livingEntity = null;
      Arrays.fill(this.livingEntitys, null);
   }

   private boolean method7107() {
      return this.spec.isSelected(0);
   }

   private boolean method7108() {
      return this.spec.isSelected(1);
   }

   @EventTarget
   public void method7109(KeyEvent var1) {
      if (this.trigger.isSelected(1) && this.key.getKeyCode() != -1 && var1.isPress(this.key.getKeyCode())) {
         if (mc.player != null && mc.gui.screen() == null) {
            this.method7114(this.method7108() ? this.method7117() : null, true);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() instanceof ClientboundSoundPacket var2 && this.method7131(var2)) {
         this.vec3d = new Vec3(var2.getX(), var2.getY(), var2.getZ());
      }
   }

   @EventTarget
   public void method7110(Event5 var1) {
      if (this.step == 2 && this.method7108() && this.playerEntity != null && mc.player != null) {
         Rotation var2 = Rotation.method3128(this.playerEntity.getBoundingBox().getCenter(), mc.player.getEyePosition());
         rotationManager.method7849(new RotationRequest(var2, () -> var2, bcCameraTestnerv.getRotationConfig()), 8, this);
      }
   }

   @EventTarget
   public void method7111(HandleInputEvent var1) {
      if (mc.player != null && mc.level != null && mc.gameMode != null && mc.getConnection() != null) {
         if (mc.level != this.world) {
            this.world = mc.level;
            this.resetTargets();
            this.items.clear();
            this.vec3d = null;
         }

         if (this.method7108()) {
            this.method7130();
         }

         if (this.step >= 0) {
            this.method7115();
         } else if (this.trigger.isSelected(0)) {
            if (!this.method7108()) {
               this.method7112();
               if (this.stopWatch.method7546(5000L)) {
                  if (this.livingEntity instanceof Player var4) {
                     if (method7134(var4)) {
                        if (!(UseItemUtils.method6527(var4) >= this.targetHealth.getValue3())) {
                           if (!(mc.player.distanceTo(var4) > this.distance.getValue3())) {
                              if (this.method7137(var4, this.distance.getValue3(), this.method7136())) {
                                 this.method7114(null, false);
                              }
                           }
                        }
                     }
                  }
               }
            } else if (this.stopWatch.method7546(500L)) {
               if (!(this.method7133() >= this.selfHealth.getValue3())) {
                  if (!this.totemCooldown.isEnabled() || this.method7132()) {
                     Player var2 = this.method7117();
                     if (var2 != null && !this.method7129(var2)) {
                        this.method7114(var2, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void method7112() {
      LivingEntity[] var1 = new LivingEntity[]{AuraModule.INSTANCE.method6358(), AimAssistModule.INSTANCE.method5156(), TriggerBotModule.INSTANCE.method5598()};
      boolean var2 = false;

      for (int var3 = 0; var3 < var1.length; var3++) {
         LivingEntity var4 = var1[var3];
         if (var4 != null && var4 != this.livingEntitys[var3] && this.method7113(var4)) {
            this.livingEntity = var4;
         }

         if (var4 != null && var4 == this.livingEntity) {
            var2 = true;
         }

         this.livingEntitys[var3] = var4;
      }

      if (var2) {
         this.stopWatch2.reset();
      }

      if (!this.method7113(this.livingEntity) || this.stopWatch2.method7546(5000L)) {
         this.livingEntity = null;

         for (LivingEntity var6 : var1) {
            if (this.method7113(var6)) {
               this.livingEntity = var6;
               this.stopWatch2.reset();
               break;
            }
         }
      }
   }

   private boolean method7113(LivingEntity var1) {
      return var1 instanceof Player var2
         && var2 != mc.player
         && var2.isAlive()
         && !var2.isRemoved()
         && var2.level() == mc.level
         && !Zenith.getInstance().getFriends().method7626(var2);
   }

   private void method7114(Player var1, boolean var2) {
      if (this.step < 0 && mc.gui.screen() == null && mc.player.containerMenu instanceof InventoryMenu) {
         if (!this.method7108() || var1 != null) {
            int var3 = this.method7108() ? this.method7124() : PotionUtils.method2325(AutoThrowModule::method7135);
            if (var3 == -1) {
               if (var2) {
                  Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Component.nullToEmpty("Нет подходящего взрывного зелья"));
               }
            } else {
               this.intValue2 = var3;
               this.playerEntity = this.method7108() ? var1 : null;
               this.intValue3 = mc.player.getInventory().getSelectedSlot();
               this.flag = false;
               this.intValue = 0;
               this.step = var3 < 9 ? 2 : 0;
            }
         }
      }
   }

   private void method7115() {
      if (this.intValue2 != -1 && !mc.player.isDeadOrDying() && ++this.intValue <= 60) {
         switch (this.step) {
            case 0:
               PotionUtils.method2328(this.intValue2, 8);
               this.flag = true;
               this.step = 1;
               break;
            case 1:
               this.step = 2;
               break;
            case 2:
               int var1 = this.intValue2 < 9 ? this.intValue2 : 8;
               ItemStack var2 = mc.player.getInventory().getItem(var1);
               if (this.method7108()) {
                  if (this.method7125(var2) <= 0L || !this.method7120(this.playerEntity)) {
                     this.method7116();
                     return;
                  }

                  if (!this.method7137(this.playerEntity, 5.0F, rotationManager.getRotation2()) || !this.method7137(this.playerEntity, 5.0F, this.method7136())
                     )
                   {
                     return;
                  }

                  this.method7128(this.playerEntity, var2);
               } else if (!method7135(var2)) {
                  this.method7116();
                  return;
               }

               PotionUtils.method2329(var1);
               UseItemUtils.useItem(InteractionHand.MAIN_HAND);
               this.stopWatch.reset();
               this.step = 3;
               break;
            case 3:
               this.method7116();
               break;
            default:
               this.method7116();
         }
      } else {
         this.method7116();
      }
   }

   private void method7116() {
      if (this.flag && this.intValue2 != -1) {
         PotionUtils.method2328(this.intValue2, 8);
      }

      if (this.intValue3 >= 0) {
         PotionUtils.method2329(this.intValue3);
      }

      this.reset();
   }

   private Player method7117() {
      if (mc.player != null && mc.level != null) {
         Player var1 = null;
         double var2 = 0.0;
         double var4 = 0.0;

         for (Player var7 : mc.level.players()) {
            if (var7 != mc.player
               && var7.isAlive()
               && !var7.isSpectator()
               && !AntiBotModule.INSTANCE.method2785(var7)
               && !Zenith.getInstance().getFriends().method7626(var7)) {
               double var8 = mc.player.distanceTo(var7);
               if (!(var8 > 5.0) && this.method7118(var7)) {
                  double var10 = this.method7121(var7);
                  if (var1 == null || var10 > var2 || var10 == var2 && var8 < var4) {
                     var1 = var7;
                     var2 = var10;
                     var4 = var8;
                  }
               }
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   private boolean method7118(Player var1) {
      Vec3 var2 = mc.player.getEyePosition();
      AABB var3 = var1.getBoundingBox();
      Vec3 var4 = new Vec3(
         Mth.clamp(var2.x, var3.minX, var3.maxX), Mth.clamp(var2.y, var3.minY, var3.maxY), Mth.clamp(var2.z, var3.minZ, var3.maxZ)
      );
      return this.method7119(var2, var4) || this.method7119(var2, var1.getEyePosition()) || this.method7119(var2, var3.getCenter());
   }

   private boolean method7119(Vec3 var1, Vec3 var2) {
      return mc.level.clip(new ClipContext(var1, var2, Block.COLLIDER, Fluid.NONE, mc.player)).getType() == Type.MISS;
   }

   private boolean method7120(Player var1) {
      return var1 != null && var1.isAlive() && !var1.isRemoved() && mc.player.distanceTo(var1) <= 5.0F;
   }

   private double method7121(Player var1) {
      double var2 = 0.0;

      for (EquipmentSlot var5 : EquipmentSlot.VALUES) {
         if (var5.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) {
            ItemStack var6 = var1.getItemBySlot(var5);
            var2 += method7122(var6) + method7123(var6, Enchantments.PROTECTION) * 2.0;
         }
      }

      ItemStack var7 = var1.getMainHandItem();
      return var2 + method7122(var7) + method7123(var7, Enchantments.SHARPNESS);
   }

   private static double method7122(ItemStack var0) {
      if (var0.isEmpty()) {
         return 0.0;
      } else {
         ItemAttributeModifiers var1 = var0.get(DataComponents.ATTRIBUTE_MODIFIERS);
         if (var1 == null) {
            return 0.0;
         } else {
            double var2 = 0.0;

            for (Entry var5 : var1.modifiers()) {
               if (var5.attribute().equals(Attributes.ARMOR)
                  || var5.attribute().equals(Attributes.ARMOR_TOUGHNESS)
                  || var5.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                  var2 += var5.modifier().amount();
               }
            }

            return var2;
         }
      }
   }

   private static int method7123(ItemStack var0, ResourceKey<Enchantment> var1) {
      if (var0.isEmpty()) {
         return 0;
      } else {
         for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var3 : var0.getEnchantments().entrySet()) {
            if (((Holder)var3.getKey()).is(var1)) {
               return var3.getIntValue();
            }
         }

         return 0;
      }
   }

   private int method7124() {
      int var1 = -1;
      long var2 = 0L;

      for (int var4 = 0; var4 < 36; var4++) {
         long var5 = this.method7125(mc.player.getInventory().getItem(var4));
         if (var5 > var2) {
            var1 = var4;
            var2 = var5;
         }
      }

      return var1;
   }

   private long method7125(ItemStack var1) {
      if (!PotionUtils.method2323(var1)) {
         return 0L;
      } else {
         List<MobEffectInstance> var2 = PotionUtils.method2321(var1);
         if (var2.isEmpty()) {
            return 0L;
         } else {
            MobEffectInstance var3 = null;
            MobEffectInstance var4 = null;
            MobEffectInstance var5 = null;

            for (MobEffectInstance var7 : var2) {
               if (var7.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL
                  && !var7.getEffect().equals(MobEffects.SPEED)
                  && !var7.getEffect().equals(MobEffects.JUMP_BOOST)) {
                  return 0L;
               }

               if (var7.getEffect().equals(MobEffects.WEAKNESS) && var7.getAmplifier() >= 1 && (var3 == null || method7127(var7) > method7127(var3))) {
                  var3 = var7;
               }

               if (var7.getEffect().equals(MobEffects.BLINDNESS) && (var4 == null || method7127(var7) > method7127(var4))) {
                  var4 = var7;
               }

               if (var5 == null || method7127(var7) > method7127(var5)) {
                  var5 = var7;
               }
            }

            if (var3 != null) {
               return method7126(3, var3);
            } else {
               return var4 != null ? method7126(2, var4) : method7126(1, var5);
            }
         }
      }
   }

   private static long method7126(int var0, MobEffectInstance var1) {
      return var0 * 1000000000L + method7127(var1) * 100L + Math.min(var1.getAmplifier(), 99);
   }

   private static int method7127(MobEffectInstance var0) {
      return var0.isInfiniteDuration() ? 1000000 : Math.min(var0.getDuration(), 1000000);
   }

   private void method7128(Player var1, ItemStack var2) {
      int var3 = 0;

      for (MobEffectInstance var5 : PotionUtils.method2321(var2)) {
         var3 = Math.max(var3, method7127(var5));
      }

      this.items.put(var1.getUUID(), System.currentTimeMillis() + (long)((float)(var3 * 50L) * 0.75F));
   }

   private boolean method7129(Player var1) {
      Long var2 = this.items.get(var1.getUUID());
      return var2 != null && var2 > System.currentTimeMillis();
   }

   private void method7130() {
      long var1 = System.currentTimeMillis();
      Vec3 var3 = this.vec3d;
      this.vec3d = null;
      if (!this.items.isEmpty()) {
         this.items.entrySet().removeIf(var3x -> {
            if (var3x.getValue() <= var1) {
               return true;
            } else {
               Player var4 = mc.level.getPlayerByUUID(var3x.getKey());
               return var4 == null || var3 != null && var4.position().distanceToSqr(var3) <= 256.0;
            }
         });
      }
   }

   private boolean method7131(ClientboundSoundPacket var1) {
      if (!Zenith.getInstance().getServerDetector().method1640()) {
         return false;
      } else {
         String var2 = var1.getSound().getRegisteredName();
         return var2.contains("minecraft:entity.illusioner.cast_spell") && (var1.getVolume() == 1.0F || var1.getPitch() == 1.0F);
      }
   }

   private boolean method7132() {
      return mc.player.getCooldowns().isOnCooldown(Items.TOTEM_OF_UNDYING.getDefaultInstance());
   }

   private float method7133() {
      return mc.player.getHealth() + mc.player.getAbsorptionAmount();
   }

   private static boolean method7134(LivingEntity var0) {
      if (var0.isUsingItem() && var0.getTicksUsingItem() >= 10) {
         ItemStack var1 = var0.getUseItem();
         return !PotionUtils.method2323(var1) && PotionUtils.method2322(var1, MobEffects.INSTANT_HEALTH);
      } else {
         return false;
      }
   }

   private static boolean method7135(ItemStack var0) {
      return PotionUtils.method2323(var0) && PotionUtils.method2322(var0, MobEffects.INSTANT_HEALTH);
   }

   private Rotation method7136() {
      return rotationManager.method7844();
   }

   private boolean method7137(LivingEntity var1, float var2, Rotation var3) {
      if (var3 == null) {
         return false;
      } else {
         Vec3 var4 = mc.player.getEyePosition();
         Vec3 var5 = var4.add(var3.method3135().scale(var2 + 1.0F));
         AABB var6 = var1.getBoundingBox().inflate(0.15);
         return var6.contains(var4) || var6.clip(var4, var5).isPresent();
      }
   }
}
