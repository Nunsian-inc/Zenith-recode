package org.zenith.utility.game;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.zenith.MinecraftAccess;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.api.CosmeticItemInfo;
import org.zenith.utility.api.Cosmetics;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.api.Share;
import org.zenith.utility.config.FiguraManager;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class ServicesTicker implements MinecraftAccess {
   private static final long CONST_longValue = 5000L;
   private final Cloud cloud;
   private final Cosmetics cosmetics;
   private final Share share;
   private final StopWatch stopWatch = new StopWatch();
   private String text;
   private String text2;
   private double lastX;
   private double lastY;
   private double doubleValue;
   private float floatValue;
   private float floatValue2;
   private long longValue;
   private float floatValue3 = -1.0F;
   private float floatValue4 = -1.0F;
   private float floatValue5 = -1.0F;
   private int intValue = -1;
   private long longValue2;
   private long longValue3;
   private long longValue4;
   private int intValue2 = -1;
   private boolean flag = true;
   private int intValue3 = -1;
   private String text3 = "";
   private long longValue5;
   private static final long CONST_longValue2 = 30000L;
   private int intValue4;
   private boolean flag2 = true;
   private boolean flag3;

   public ServicesTicker(Cloud cloud, Cosmetics cosmetics, Share share) {
      this.cloud = cloud;
      this.cosmetics = cosmetics;
      this.share = share;
      EventManager.register(this);
   }

   @EventTarget
   private void method1758(CloudStateEvent event) {
      if (event.method6858()) {
         this.method1759();
      }
   }

   private void method1759() {
      this.text = null;
      this.longValue = 0L;
      this.floatValue3 = -1.0F;
      this.longValue2 = 0L;
      this.longValue3 = 0L;
      this.longValue4 = 0L;
      this.intValue2 = -1;
      this.intValue3 = -1;
      this.text3 = "";
      this.longValue5 = 0L;
      this.flag = true;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.cloud.method7862()) {
         boolean enabled = this.share.position.isEnabled();
         if (enabled != this.flag2) {
            this.flag2 = enabled;
            this.text = null;
            this.longValue = 0L;
         }

         if (++this.intValue4 % 20 == 0 || this.text == null) {
            String text = this.share.server.isEnabled() ? this.cloud.method7911() : "";
            String str = this.share.server.isEnabled() && enabled ? this.cloud.method7913() : "";
            if (!text.equals(this.text) || !str.equals(this.text2)) {
               this.text = text;
               this.text2 = str;
               this.cloud.method7897(text, str);
            }
         }

         this.method1760();
      }
   }

   private void method1760() {
      int index = this.share.cosmeticsVisibility.getIndex();
      if (index != this.intValue3) {
         this.intValue3 = index;
         this.flag = true;
      }

      if (this.share.method949()) {
         if (this.flag) {
            this.flag = false;
            this.intValue2 = -1;
            this.cloud.method7901(List.of());
         }
      } else {
         int intValue = this.cosmetics.getIntValue();
         String str = method1761().method7678();
         String str2 = method1761().method7679();
         String str3 = method1762().method4555();
         float f = method1762().method4572();
         String str4 = str + "|" + str2 + "|" + str3 + "|" + f;
         if (this.flag || intValue != this.intValue2 || !str4.equals(this.text3)) {
            Set<String> strings = this.cosmetics.getItems10();
            if (!strings.isEmpty() && !this.cosmetics.method2972()) {
               long now = System.currentTimeMillis();
               if (this.longValue5 == 0L) {
                  this.longValue5 = now;
               }

               if (now - this.longValue5 < 30000L) {
                  return;
               }
            } else {
               this.longValue5 = 0L;
            }

            this.flag = false;
            this.intValue2 = intValue;
            this.text3 = str4;
            int floatToIntBits = this.share.colorSetting.method1445();
            List<CloudPackets.Data9> data9s = new ArrayList<>(strings.size() + 3);
            data9s.addAll(ShareCodec.method6721(str, str2, str3, f));

            for (String text : strings) {
               CosmeticItemInfo cosmeticItemInfo = this.cosmetics.method2971(text);
               if (cosmeticItemInfo != null && cosmeticItemInfo.getCosmeticType().method1682() && text.length() <= 40) {
                  data9s.add(new CloudPackets.Data9(text, cosmeticItemInfo.getCosmeticType().ordinal(), floatToIntBits));
               }
            }

            this.cloud.method7901(data9s);
         }
      }
   }

   private static FiguraManager method1761() {
      return Zenith.getInstance().getFiguraManager();
   }

   private static PetManager method1762() {
      return Zenith.getInstance().getPetManager();
   }

   @EventTarget
   private void onUpdate(UpdateEvent event) {
      if (this.cloud.method7862() && mc.player != null) {
         if (this.share.position.isEnabled()) {
            this.method1763();
         }

         if (this.share.health.isEnabled()) {
            this.method1764();
         }

         boolean enabled = this.share.inventory.isEnabled();
         if (enabled) {
            this.method1765();
         } else if (this.flag3) {
            this.longValue3 = 0L;
            this.longValue4 = 0L;
            this.cloud.method7900(List.of());
         }

         this.flag3 = enabled;
      }
   }

   private void method1763() {
      double x = mc.player.getX();
      double y = mc.player.getY();
      double z = mc.player.getZ();
      float yRot = mc.player.getYRot();
      float xRot = mc.player.getXRot();
      long now = System.currentTimeMillis();
      double d = x - this.lastX;
      double d2 = y - this.lastY;
      double d3 = z - this.doubleValue;
      boolean flag = d * d + d2 * d2 + d3 * d3 > 4.0E-4
         || Math.abs(yRot - this.floatValue) > 0.5F
         || Math.abs(xRot - this.floatValue2) > 0.5F;
      if (flag || now - this.longValue >= 2000L) {
         this.lastX = x;
         this.lastY = y;
         this.doubleValue = z;
         this.floatValue = yRot;
         this.floatValue2 = xRot;
         this.longValue = now;
         this.cloud.method7898(x, y, z, yRot, xRot);
      }
   }

   private void method1764() {
      float health = mc.player.getHealth();
      float maxHealth = mc.player.getMaxHealth();
      float absorptionAmount = mc.player.getAbsorptionAmount();
      int foodLevel = mc.player.getFoodData().getFoodLevel();
      long now = System.currentTimeMillis();
      boolean flag = health == this.floatValue3 && maxHealth == this.floatValue4 && absorptionAmount == this.floatValue5 && foodLevel == this.intValue;
      if (!flag || now - this.longValue2 >= 5000L) {
         if (this.stopWatch.hasElapsed(50L) || !(this.floatValue3 >= 0.0F)) {
            this.stopWatch.reset();
            this.floatValue3 = health;
            this.floatValue4 = maxHealth;
            this.floatValue5 = absorptionAmount;
            this.intValue = foodLevel;
            this.longValue2 = now;
            this.cloud.method7899(health, maxHealth, absorptionAmount, foodLevel);
         }
      }
   }

   private void method1765() {
      Inventory inventory = mc.player.getInventory();
      int initialCapacity = Math.min(inventory.getContainerSize(), 48);
      long l = 1469598103934665603L;

      for (int i = 0; i < initialCapacity; i++) {
         ItemStack stack = inventory.getItem(i);
         l = (l ^ (stack.isEmpty() ? 0 : System.identityHashCode(stack.getItem()))) * 1099511628211L;
         l = (l ^ stack.getCount()) * 1099511628211L;
         l = (l ^ stack.getDamageValue()) * 1099511628211L;
      }

      long now = System.currentTimeMillis();
      if (l != this.longValue3 || now - this.longValue4 >= 5000L) {
         this.longValue3 = l;
         this.longValue4 = now;
         List<CloudPackets.Data7> data7s = new ArrayList<>(initialCapacity);

         for (int i = 0; i < initialCapacity; i++) {
            ItemStack stack = inventory.getItem(i);
            data7s.add(
               stack.isEmpty()
                  ? CloudPackets.Data7.INSTANCE
                  : new CloudPackets.Data7(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), Math.min(stack.getCount(), 255), Math.max(0, stack.getDamageValue()))
            );
         }

         this.cloud.method7900(data7s);
      }
   }
}
