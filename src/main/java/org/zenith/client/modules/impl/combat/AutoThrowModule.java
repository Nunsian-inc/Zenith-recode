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
   public void method7109(KeyEvent event) {
      if (this.trigger.isSelected(1) && this.key.getKeyCode() != -1 && event.isPress(this.key.getKeyCode())) {
         if (mc.player != null && mc.gui.screen() == null) {
            this.method7114(this.method7108() ? this.method7117() : null, true);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100() && event.getPacket3() instanceof ClientboundSoundPacket clientboundSoundPacket && this.method7131(clientboundSoundPacket)) {
         this.vec3d = new Vec3(clientboundSoundPacket.getX(), clientboundSoundPacket.getY(), clientboundSoundPacket.getZ());
      }
   }

   @EventTarget
   public void method7110(Event5 event) {
      if (this.step == 2 && this.method7108() && this.playerEntity != null && mc.player != null) {
         Rotation rotation = Rotation.method3128(this.playerEntity.getBoundingBox().getCenter(), mc.player.getEyePosition());
         rotationManager.method7849(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 8, this);
      }
   }

   @EventTarget
   public void method7111(HandleInputEvent event) {
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
                  if (this.livingEntity instanceof Player entity) {
                     if (method7134(entity)) {
                        if (!(UseItemUtils.method6527(entity) >= this.targetHealth.getValue3())) {
                           if (!(mc.player.distanceTo(entity) > this.distance.getValue3())) {
                              if (this.method7137(entity, this.distance.getValue3(), this.method7136())) {
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
                     Player player = this.method7117();
                     if (player != null && !this.method7129(player)) {
                        this.method7114(player, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void method7112() {
      LivingEntity[] livingEntities = new LivingEntity[]{AuraModule.INSTANCE.method6358(), AimAssistModule.INSTANCE.method5156(), TriggerBotModule.INSTANCE.method5598()};
      boolean flag = false;

      for (int i = 0; i < livingEntities.length; i++) {
         LivingEntity livingEntity = livingEntities[i];
         if (livingEntity != null && livingEntity != this.livingEntitys[i] && this.method7113(livingEntity)) {
            this.livingEntity = livingEntity;
         }

         if (livingEntity != null && livingEntity == this.livingEntity) {
            flag = true;
         }

         this.livingEntitys[i] = livingEntity;
      }

      if (flag) {
         this.stopWatch2.reset();
      }

      if (!this.method7113(this.livingEntity) || this.stopWatch2.method7546(5000L)) {
         this.livingEntity = null;

         for (LivingEntity livingEntity : livingEntities) {
            if (this.method7113(livingEntity)) {
               this.livingEntity = livingEntity;
               this.stopWatch2.reset();
               break;
            }
         }
      }
   }

   private boolean method7113(LivingEntity livingEntity) {
      return livingEntity instanceof Player player
         && player != mc.player
         && player.isAlive()
         && !player.isRemoved()
         && player.level() == mc.level
         && !Zenith.getInstance().getFriends().method7626(player);
   }

   private void method7114(Player player, boolean flag) {
      if (this.step < 0 && mc.gui.screen() == null && mc.player.containerMenu instanceof InventoryMenu) {
         if (!this.method7108() || player != null) {
            int n = this.method7108() ? this.method7124() : PotionUtils.method2325(AutoThrowModule::method7135);
            if (n == -1) {
               if (flag) {
                  Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Component.nullToEmpty("Нет подходящего взрывного зелья"));
               }
            } else {
               this.intValue2 = n;
               this.playerEntity = this.method7108() ? player : null;
               this.intValue3 = mc.player.getInventory().getSelectedSlot();
               this.flag = false;
               this.intValue = 0;
               this.step = n < 9 ? 2 : 0;
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
               int slot = this.intValue2 < 9 ? this.intValue2 : 8;
               ItemStack useItem = mc.player.getInventory().getItem(slot);
               if (this.method7108()) {
                  if (this.method7125(useItem) <= 0L || !this.method7120(this.playerEntity)) {
                     this.method7116();
                     return;
                  }

                  if (!this.method7137(this.playerEntity, 5.0F, rotationManager.getRotation2()) || !this.method7137(this.playerEntity, 5.0F, this.method7136())
                     )
                   {
                     return;
                  }

                  this.method7128(this.playerEntity, useItem);
               } else if (!method7135(useItem)) {
                  this.method7116();
                  return;
               }

               PotionUtils.method2329(slot);
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
         Player player = null;
         double d = 0.0;
         double d2 = 0.0;

         for (Player player2 : mc.level.players()) {
            if (player2 != mc.player
               && player2.isAlive()
               && !player2.isSpectator()
               && !AntiBotModule.INSTANCE.method2785(player2)
               && !Zenith.getInstance().getFriends().method7626(player2)) {
               double d3 = mc.player.distanceTo(player2);
               if (!(d3 > 5.0) && this.method7118(player2)) {
                  double d4 = this.method7121(player2);
                  if (player == null || d4 > d || d4 == d && d3 < d2) {
                     player = player2;
                     d = d4;
                     d2 = d3;
                  }
               }
            }
         }

         return player;
      } else {
         return null;
      }
   }

   private boolean method7118(Player player) {
      Vec3 eyePosition = mc.player.getEyePosition();
      AABB boundingBox = player.getBoundingBox();
      Vec3 vec = new Vec3(
         Mth.clamp(eyePosition.x, boundingBox.minX, boundingBox.maxX), Mth.clamp(eyePosition.y, boundingBox.minY, boundingBox.maxY), Mth.clamp(eyePosition.z, boundingBox.minZ, boundingBox.maxZ)
      );
      return this.method7119(eyePosition, vec) || this.method7119(eyePosition, player.getEyePosition()) || this.method7119(eyePosition, boundingBox.getCenter());
   }

   private boolean method7119(Vec3 eyePosition, Vec3 vec) {
      return mc.level.clip(new ClipContext(eyePosition, vec, Block.COLLIDER, Fluid.NONE, mc.player)).getType() == Type.MISS;
   }

   private boolean method7120(Player playerEntity) {
      return playerEntity != null && playerEntity.isAlive() && !playerEntity.isRemoved() && mc.player.distanceTo(playerEntity) <= 5.0F;
   }

   private double method7121(Player player) {
      double d = 0.0;

      for (EquipmentSlot slot : EquipmentSlot.VALUES) {
         if (slot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) {
            ItemStack itemBySlot = player.getItemBySlot(slot);
            d += method7122(itemBySlot) + method7123(itemBySlot, Enchantments.PROTECTION) * 2.0;
         }
      }

      ItemStack mainHandItem = player.getMainHandItem();
      return d + method7122(mainHandItem) + method7123(mainHandItem, Enchantments.SHARPNESS);
   }

   private static double method7122(ItemStack itemBySlot) {
      if (itemBySlot.isEmpty()) {
         return 0.0;
      } else {
         ItemAttributeModifiers itemAttributeModifiers = itemBySlot.get(DataComponents.ATTRIBUTE_MODIFIERS);
         if (itemAttributeModifiers == null) {
            return 0.0;
         } else {
            double d = 0.0;

            for (Entry modifier : itemAttributeModifiers.modifiers()) {
               if (modifier.attribute().equals(Attributes.ARMOR)
                  || modifier.attribute().equals(Attributes.ARMOR_TOUGHNESS)
                  || modifier.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                  d += modifier.modifier().amount();
               }
            }

            return d;
         }
      }
   }

   private static int method7123(ItemStack itemBySlot, ResourceKey<Enchantment> key) {
      if (itemBySlot.isEmpty()) {
         return 0;
      } else {
         for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry entry : itemBySlot.getEnchantments().entrySet()) {
            if (((Holder)entry.getKey()).is(key)) {
               return entry.getIntValue();
            }
         }

         return 0;
      }
   }

   private int method7124() {
      int n = -1;
      long l = 0L;

      for (int i = 0; i < 36; i++) {
         long l2 = this.method7125(mc.player.getInventory().getItem(i));
         if (l2 > l) {
            n = i;
            l = l2;
         }
      }

      return n;
   }

   private long method7125(ItemStack useItem) {
      if (!PotionUtils.method2323(useItem)) {
         return 0L;
      } else {
         List<MobEffectInstance> mobEffectInstances = PotionUtils.method2321(useItem);
         if (mobEffectInstances.isEmpty()) {
            return 0L;
         } else {
            MobEffectInstance mobEffectInstance = null;
            MobEffectInstance mobEffectInstance2 = null;
            MobEffectInstance mobEffectInstance3 = null;

            for (MobEffectInstance mobEffectInstance4 : mobEffectInstances) {
               if (mobEffectInstance4.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL
                  && !mobEffectInstance4.getEffect().equals(MobEffects.SPEED)
                  && !mobEffectInstance4.getEffect().equals(MobEffects.JUMP_BOOST)) {
                  return 0L;
               }

               if (mobEffectInstance4.getEffect().equals(MobEffects.WEAKNESS) && mobEffectInstance4.getAmplifier() >= 1 && (mobEffectInstance == null || method7127(mobEffectInstance4) > method7127(mobEffectInstance))) {
                  mobEffectInstance = mobEffectInstance4;
               }

               if (mobEffectInstance4.getEffect().equals(MobEffects.BLINDNESS) && (mobEffectInstance2 == null || method7127(mobEffectInstance4) > method7127(mobEffectInstance2))) {
                  mobEffectInstance2 = mobEffectInstance4;
               }

               if (mobEffectInstance3 == null || method7127(mobEffectInstance4) > method7127(mobEffectInstance3)) {
                  mobEffectInstance3 = mobEffectInstance4;
               }
            }

            if (mobEffectInstance != null) {
               return method7126(3, mobEffectInstance);
            } else {
               return mobEffectInstance2 != null ? method7126(2, mobEffectInstance2) : method7126(1, mobEffectInstance3);
            }
         }
      }
   }

   private static long method7126(int n, MobEffectInstance mobEffectInstance) {
      return n * 1000000000L + method7127(mobEffectInstance) * 100L + Math.min(mobEffectInstance.getAmplifier(), 99);
   }

   private static int method7127(MobEffectInstance mobEffectInstance) {
      return mobEffectInstance.isInfiniteDuration() ? 1000000 : Math.min(mobEffectInstance.getDuration(), 1000000);
   }

   private void method7128(Player playerEntity, ItemStack useItem) {
      int n = 0;

      for (MobEffectInstance mobEffectInstance : PotionUtils.method2321(useItem)) {
         n = Math.max(n, method7127(mobEffectInstance));
      }

      this.items.put(playerEntity.getUUID(), System.currentTimeMillis() + (long)((float)(n * 50L) * 0.75F));
   }

   private boolean method7129(Player player) {
      Long l = this.items.get(player.getUUID());
      return l != null && l > System.currentTimeMillis();
   }

   private void method7130() {
      long now = System.currentTimeMillis();
      Vec3 vec3d = this.vec3d;
      this.vec3d = null;
      if (!this.items.isEmpty()) {
         this.items.entrySet().removeIf(var3x -> {
            if (var3x.getValue() <= now) {
               return true;
            } else {
               Player playerByUUID = mc.level.getPlayerByUUID(var3x.getKey());
               return playerByUUID == null || vec3d != null && playerByUUID.position().distanceToSqr(vec3d) <= 256.0;
            }
         });
      }
   }

   private boolean method7131(ClientboundSoundPacket clientboundSoundPacket) {
      if (!Zenith.getInstance().getServerDetector().method1640()) {
         return false;
      } else {
         String registeredName = clientboundSoundPacket.getSound().getRegisteredName();
         return registeredName.contains("minecraft:entity.illusioner.cast_spell") && (clientboundSoundPacket.getVolume() == 1.0F || clientboundSoundPacket.getPitch() == 1.0F);
      }
   }

   private boolean method7132() {
      return mc.player.getCooldowns().isOnCooldown(Items.TOTEM_OF_UNDYING.getDefaultInstance());
   }

   private float method7133() {
      return mc.player.getHealth() + mc.player.getAbsorptionAmount();
   }

   private static boolean method7134(LivingEntity entity) {
      if (entity.isUsingItem() && entity.getTicksUsingItem() >= 10) {
         ItemStack useItem = entity.getUseItem();
         return !PotionUtils.method2323(useItem) && PotionUtils.method2322(useItem, MobEffects.INSTANT_HEALTH);
      } else {
         return false;
      }
   }

   private static boolean method7135(ItemStack useItem) {
      return PotionUtils.method2323(useItem) && PotionUtils.method2322(useItem, MobEffects.INSTANT_HEALTH);
   }

   private Rotation method7136() {
      return rotationManager.method7844();
   }

   private boolean method7137(LivingEntity playerEntity, float f, Rotation rotation2) {
      if (rotation2 == null) {
         return false;
      } else {
         Vec3 eyePosition = mc.player.getEyePosition();
         Vec3 to = eyePosition.add(rotation2.method3135().scale(f + 1.0F));
         AABB inflate = playerEntity.getBoundingBox().inflate(0.15);
         return inflate.contains(eyePosition) || inflate.clip(eyePosition, to).isPresent();
      }
   }
}
