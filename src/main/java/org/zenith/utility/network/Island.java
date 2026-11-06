package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.AttackEvent;

public final class Island implements MinecraftAccess {
   public static final Island INSTANCE = new Island();
   public static final int CONST_intValue = 100;
   public static final int CONST_intValue2 = 90;
   public static final int CONST_intValue3 = 80;
   public static final int CONST_intValue4 = 70;
   public static final int CONST_intValue5 = 40;
   public static final int CONST_intValue6 = 60;
   public static final int CONST_intValue7 = 75;
   public static final int CONST_intValue8 = 35;
   private static final long CONST_longValue = 15000L;
   private static final long CONST_longValue2 = 3000L;
   private static final long CONST_longValue3 = 4000L;
   private static final ZenithColor zenithColor = new ZenithColor(255, 107, 90, 255);
   private static final ZenithColor zenithColor2 = new ZenithColor(216, 222, 232, 255);
   private static final ZenithColor zenithColor3 = new ZenithColor(154, 123, 255, 255);
   private static final ZenithColor zenithColor4 = new ZenithColor(255, 196, 74, 255);
   private static final ZenithColor zenithColor5 = new ZenithColor(255, 138, 61, 255);
   private static final ZenithColor zenithColor6 = new ZenithColor(90, 216, 208, 255);
   private static final ZenithColor zenithColor7 = new ZenithColor(110, 180, 255, 255);
   private final List<Island.Entry> items = new ArrayList<>();
   private boolean flag;
   private WeakReference<Player> weakReference = new WeakReference<>(null);

   private Island() {
   }

   public static void method2867() {
      if (!INSTANCE.flag) {
         INSTANCE.flag = true;
         EventManager.register(INSTANCE);
      }
   }

   private static Island.Entry method2868(String anObject) {
      for (Island.Entry entry : INSTANCE.items) {
         if (entry.text.equals(anObject)) {
            return entry;
         }
      }

      return null;
   }

   private static Island.Entry method2869(String anObject, int intValue) {
      Island.Entry entry = method2868(anObject);
      if (entry == null) {
         entry = new Island.Entry(anObject);
         entry.intValue = intValue;
         entry.startedAt = System.currentTimeMillis();
         INSTANCE.items.add(entry);
      }

      return entry;
   }

   public static void method2870(String anObject, String title, ZenithColor zenithColor, int intValue, long l, boolean flag) {
      if (!mc.isSameThread()) {
         mc.execute(() -> method2870(anObject, title, zenithColor, intValue, l, flag));
      } else {
         long now = System.currentTimeMillis();
         Island.Entry entry = method2869(anObject, intValue);
         entry.title = title;
         entry.zenithColor = zenithColor;
         entry.intValue = intValue;
         entry.startedAt = now;
         entry.longValue = l <= 0L ? 0L : now + l;
         entry.flag = flag;
         entry.longValue2 = now;
      }
   }

   public static void method2871(String anObject, String title, ZenithColor zenithColor, int intValue) {
      if (!mc.isSameThread()) {
         mc.execute(() -> method2871(anObject, title, zenithColor, intValue));
      } else {
         Island.Entry entry = method2869(anObject, intValue);
         long now = System.currentTimeMillis();
         entry.title = title;
         entry.zenithColor = zenithColor;
         entry.intValue = intValue;
         entry.longValue = 0L;
         entry.flag = false;
         entry.longValue2 = now;
      }
   }

   public static void method2872(String title, ZenithColor zenithColor, float floatValue, String str) {
      if (!mc.isSameThread()) {
         mc.execute(() -> method2872(title, zenithColor, floatValue, str));
      } else {
         method2871("boss", title, zenithColor, 100);
         Island.Entry entry = method2868("boss");
         entry.floatValue = floatValue;
         entry.text2 = str;
      }
   }

   public static void method2873(String anObject, String title, ZenithColor zenithColor, int intValue, float floatValue) {
      if (!mc.isSameThread()) {
         mc.execute(() -> method2873(anObject, title, zenithColor, intValue, floatValue));
      } else {
         Island.Entry entry = method2869(anObject, intValue);
         long now = System.currentTimeMillis();
         entry.title = title;
         entry.zenithColor = zenithColor;
         entry.intValue = intValue;
         entry.longValue = now + 350L;
         entry.flag = false;
         entry.floatValue = -1.0F;
         entry.floatValue2 = floatValue;
         entry.flag2 = true;
         entry.longValue2 = now;
      }
   }

   public static void method2874(String anObject, float floatValue) {
      if (!mc.isSameThread()) {
         mc.execute(() -> method2874(anObject, floatValue));
      } else {
         Island.Entry entry = method2868(anObject);
         if (entry != null) {
            entry.floatValue = floatValue;
         }
      }
   }

   public static void drop(String anObject) {
      if (!mc.isSameThread()) {
         mc.execute(() -> drop(anObject));
      } else {
         INSTANCE.items.removeIf(entry -> entry.text.equals(anObject));
      }
   }

   public static Island.Entry method2875(long now) {
      if (INSTANCE.weakReference.get() != mc.player) {
         INSTANCE.weakReference = new WeakReference<>(mc.player);
         INSTANCE.items.removeIf(var0x -> !var0x.text.equals("boss"));
      }

      INSTANCE.items.removeIf(var2x -> !var2x.method2883(now) || !var2x.text.equals("boss") && !var2x.flag2);
      Island.Entry entry = null;

      for (Island.Entry entry2 : INSTANCE.items) {
         if (entry == null || entry2.intValue > entry.intValue || entry2.intValue == entry.intValue && entry2.longValue2 > entry.longValue2) {
            entry = entry2;
         }
      }

      return entry;
   }

   public static void method2876(long l) {
      method2867();
      if (mc.player != null && mc.level != null) {
         if (INSTANCE.weakReference.get() != mc.player) {
            INSTANCE.weakReference = new WeakReference<>(mc.player);
            drop("combat");
            drop("totem");
            drop("stun");
            drop("trap");
         }

         if (mc.player.isDeadOrDying()) {
            drop("combat");
            drop("totem");
         }

         if (!mc.player.isDeadOrDying() && mc.player.hurtTime > 0 && mc.player.hurtTime == mc.player.hurtDuration) {
            method2877();
         }

         BlockPos above = mc.player.blockPosition().above();
         boolean flag = mc.level.getBlockState(mc.player.blockPosition()).is(Blocks.COBWEB) || mc.level.getBlockState(above).is(Blocks.COBWEB);
         if (flag) {
            method2871("stun", method2882("hud.island.web"), zenithColor2, 90);
         } else {
            drop("stun");
         }
      } else {
         INSTANCE.items.clear();
         INSTANCE.weakReference.clear();
      }
   }

   public static void method2877() {
      method2870("combat", method2882("hud.island.combat"), zenithColor, 40, 15000L, true);
   }

   public static void method2878(String title) {
      method2870("trap", title, zenithColor3, 80, 4000L, true);
   }

   public static void method2879(String str, long l) {
      if (l > 0L) {
         switch (str) {
            case "trap":
               method2870("helper.trap", method2882("hud.island.trap.small"), zenithColor3, 80, l, true);
               break;
            case "bigTrap":
               method2870("helper.bigTrap", method2882("hud.island.trap.big"), zenithColor5, 81, l, true);
               break;
            case "beacon":
               method2870("helper.beacon", method2882("hud.island.beacon"), zenithColor6, 60, l, true);
               break;
            case "blast":
               method2870("helper.blast", method2882("hud.island.blast"), zenithColor5, 82, l, true);
         }
      }
   }

   public static void method2880(String substringBetween, long l) {
      if (l > 0L && substringBetween != null && !substringBetween.isBlank()) {
         method2870("helper.event", substringBetween, zenithColor7, 60, l, true);
      }
   }

   @EventTarget
   public void method2881(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST && event.getEntity() instanceof Player) {
         method2877();
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null) {
         if (event.getPacket() instanceof ClientboundEntityEventPacket entityEventPacket && entityEventPacket.getEventId() == 35) {
            mc.execute(() -> {
               if (mc.player != null && mc.level != null) {
                  Entity var1x = entityEventPacket.getEntity(mc.level);
                  if (var1x == mc.player) {
                     method2870("totem", method2882("hud.island.totem"), zenithColor4, 70, 3000L, true);
                  }
               }
            });
         }
      }
   }

   private static String method2882(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   public static final class Entry {
      public final String text;
      public String title;
      public ZenithColor zenithColor;
      public int intValue;
      public long startedAt;
      public long longValue;
      public float floatValue = -1.0F;
      public boolean flag;
      public long longValue2;
      public float floatValue2 = Float.NaN;
      public boolean flag2;
      public String text2;

      private Entry(String text) {
         this.text = text;
      }

      public boolean method2883(long now) {
         return this.longValue == 0L || now < this.longValue;
      }

      public float method2884(long now) {
         if (this.flag && this.longValue != 0L) {
            float f = Math.max(1.0F, (float)(this.longValue - this.startedAt));
            return Math.clamp((float)(this.longValue - now) / f, 0.0F, 1.0F);
         } else {
            return this.floatValue;
         }
      }

      public String method2885(long now) {
         if (this.text2 != null) {
            return this.text2;
         } else if (this.flag && this.longValue != 0L) {
            long l = Math.max(0L, this.longValue - now);
            long l2 = (l + 999L) / 1000L;
            return l2 >= 60L ? l2 / 60L + ":" + String.format("%02d", l2 % 60L) : l2 + "s";
         } else {
            return this.floatValue >= 0.0F ? Math.round(this.floatValue * 100.0F) + "%" : "";
         }
      }
   }
}
