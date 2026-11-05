package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.PotionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "PvpHelper",
   category = ModuleCategory.COMBAT,
   description = "module.pvphelper.description"
)
public final class PvpHelperModule extends Module {
   public static final PvpHelperModule INSTANCE = new PvpHelperModule();
   private static final int CONST_intValue = 32;
   private final BooleanSetting healHelper = new BooleanSetting("module.pvpHelper.healHelper", "module.pvpHelper.healHelper.desc", true);
   private final BooleanSetting reduceCooldown = new BooleanSetting("module.pvpHelper.reduceCooldown", "module.pvpHelper.reduceCooldown.desc", false);
   private final BooleanSetting autoChorus = new BooleanSetting("module.pvpHelper.autoChorus", "module.pvpHelper.autoChorus.desc", false);
   private final BooleanSetting warnArmor = new BooleanSetting("module.pvpHelper.warnArmor", "module.pvpHelper.warnArmor.desc", true);
   private final Set<Integer> items = new HashSet<>();
   private long longValue;
   private boolean flag;
   private int intValue;
   private volatile long longValue2;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private volatile int intValue2 = -1;
   private boolean flag2;
   private boolean flag3;
   private int intValue3 = -1;
   private int intValue4 = -1;
   private boolean flag4 = true;

   private PvpHelperModule() {
   }

   @Override
   public void onEnable() {
      this.items.clear();
      this.longValue = 0L;
      this.flag = false;
      this.longValue2 = 0L;
      this.flag4 = true;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method4651();
      this.items.clear();
      super.onDisable();
   }

   public int method4641(Identifier key, int n) {
      if (this.isEnabled() && this.reduceCooldown.isEnabled() && n > 0) {
         Item item = BuiltInRegistries.ITEM.getValue(key);
         return item != Items.ENCHANTED_GOLDEN_APPLE && item != Items.GOLDEN_APPLE && item != Items.POTION && item != Items.CHORUS_FRUIT
            ? n
            : Math.max(1, n - 32);
      } else {
         return n;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         this.method4642();
         this.method4643();
         this.method4649();
         this.method4646();
      }
   }

   private void method4642() {
      ItemStack useItem = mc.player.getUseItem();
      if (mc.player.isUsingItem() && PotionUtils.method2324(useItem) && PotionUtils.method2322(useItem, MobEffects.INSTANT_HEALTH)) {
         this.flag = true;
         this.intValue = mc.player.getUseItemRemainingTicks();
      } else if (this.flag) {
         if (this.intValue <= 2) {
            this.longValue = System.currentTimeMillis();
         }

         this.flag = false;
      }
   }

   private void method4643() {
      this.items.clear();
      if (this.healHelper.isEnabled() && Zenith.getInstance().getServerDetector().method1635()) {
         if (!mc.player.hasEffect(MobEffects.STRENGTH)) {
            this.method4644(useItem -> PotionUtils.method2324(useItem) && PotionUtils.method2322(useItem, MobEffects.STRENGTH));
         }

         if (!(mc.player.getHealth() + mc.player.getAbsorptionAmount() > 19.0F)) {
            long l = Zenith.getInstance().getServerDetector().method1642() ? 10000L : 20000L;
            if (System.currentTimeMillis() - this.longValue >= l) {
               this.method4644(useItem -> PotionUtils.method2324(useItem) && PotionUtils.method2322(useItem, MobEffects.INSTANT_HEALTH));
            } else if (mc.player.getFoodData().getFoodLevel() < 20) {
               this.method4644(stack -> stack.is(Items.GOLDEN_CARROT));
            } else if (!mc.player.getCooldowns().isOnCooldown(Items.GOLDEN_APPLE.getDefaultInstance())) {
               this.method4644(stack -> stack.is(Items.GOLDEN_APPLE));
            } else {
               this.method4644(stack -> stack.is(Items.ENCHANTED_GOLDEN_APPLE));
            }
         }
      }
   }

   private void method4644(Predicate<ItemStack> predicate) {
      int n = PotionUtils.method2326(predicate);
      if (n != -1) {
         this.items.add(n);
      }
   }

   public boolean method4645(int intValue) {
      return this.isEnabled() && this.items.contains(intValue);
   }

   private void method4646() {
      if (this.warnArmor.isEnabled()) {
         float f = 1.0F;

         for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() == Type.HUMANOID_ARMOR) {
               ItemStack itemBySlot = mc.player.getItemBySlot(slot);
               if (!itemBySlot.isEmpty() && itemBySlot.isDamageableItem()) {
                  float maxDamage = itemBySlot.getMaxDamage();
                  f = Math.min(f, (maxDamage - itemBySlot.getDamageValue()) / maxDamage);
               }
            }
         }

         if (f < 0.36F) {
            if (this.flag4) {
               Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Component.nullToEmpty("Броня почти сломана!"));
               this.flag4 = false;
            }
         } else {
            this.flag4 = true;
         }
      }
   }

   @EventTarget
   public void method4647(DrawEvent event) {
      if (!this.items.isEmpty() && mc.player != null && !mc.gui.hud.isHidden()) {
         if (!InterfaceModule.INSTANCE.isEnabled() || !InterfaceModule.INSTANCE.method6812()) {
            CustomDrawContext customDrawContext = event.getCustomDrawContext();
            int n = customDrawContext.guiWidth() / 2 - 91;
            int n2 = customDrawContext.guiHeight() - 22;
            ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7294(0.45F);

            for (int n3 : this.items) {
               customDrawContext.drawRoundedRect((float)(n + 1 + n3 * 20), (float)(n2 + 1), 20.0F, 20.0F, ProxyDotRadiusData.method8069(3.0F), zenithColor);
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100()) {
         if (this.flag2 && event.getPacket3() instanceof ClientboundMoveEntityPacket moveEntityPacket) {
            mc.execute(() -> {
               if (mc.level != null && moveEntityPacket.getEntity(mc.level) != null && moveEntityPacket.getEntity(mc.level).getId() == this.intValue2) {
                  this.stopWatch2.reset();
               }
            });
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
   }

   @EventTarget
   public void method4648(AttackEvent event) {
      if (this.autoChorus.isEnabled() && this.flag2) {
         event.cancel();
      }
   }

   private void method4649() {
      if (!this.autoChorus.isEnabled()) {
         this.method4651();
         this.stopWatch.reset();
      } else {
         LivingEntity entity = AuraModule.INSTANCE.method6358();
         if (entity == null) {
            this.stopWatch.reset();
            if (this.flag2 && !mc.player.isUsingItem()) {
               this.method4651();
            }
         } else if (!entity.isAlive()) {
            this.stopWatch.reset();
         } else {
            this.intValue2 = entity.getId();
            boolean flag = entity.isUsingItem() && entity.getUseItem().is(Items.CHORUS_FRUIT);
            if (!flag || mc.player.distanceToSqr(entity) > 25.0) {
               this.stopWatch.reset();
            } else if (!this.flag2 && mc.gui.screen() == null && !mc.player.isUsingItem() && this.stopWatch.method7546(200L)) {
               this.method4650();
            }

            if (this.flag2) {
               if (mc.player.isUsingItem()) {
                  this.flag3 = true;
               } else if (this.flag3) {
                  this.method4651();
                  return;
               }

               if (this.stopWatch2.method7546(2500L)) {
                  this.method4651();
               }
            }
         }
      }
   }

   private void method4650() {
      if (mc.player.containerMenu instanceof InventoryMenu) {
         int slot = PotionUtils.method2325(stack -> stack.is(Items.CHORUS_FRUIT));
         if (slot != -1) {
            int selectedSlot = mc.player.getInventory().getSelectedSlot();
            this.intValue3 = selectedSlot;
            this.intValue4 = -1;
            if (slot < 9) {
               PotionUtils.method2329(slot);
            } else {
               this.intValue4 = slot;
               PotionUtils.method2328(slot, selectedSlot);
            }

            mc.options.keyUse.setDown(true);
            this.flag2 = true;
            this.flag3 = false;
            this.stopWatch2.reset();
         }
      }
   }

   private void method4651() {
      if (this.flag2) {
         mc.options.keyUse.setDown(false);
         if (mc.player != null && mc.gameMode != null) {
            if (this.intValue4 != -1 && this.intValue3 != -1) {
               PotionUtils.method2328(this.intValue4, this.intValue3);
            }

            if (this.intValue3 != -1) {
               PotionUtils.method2329(this.intValue3);
            }
         }

         this.stopWatch.reset();
         this.stopWatch2.reset();
      }

      this.flag2 = false;
      this.flag3 = false;
      this.intValue3 = -1;
      this.intValue4 = -1;
   }
}
