package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.ChatLogger;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.utility.network.InventoryUtils;

public class JoinCommand extends CommandAbstract {
   private static final long CLICK_DELAY_MS = 100L;
   private static final long REOPEN_DELAY_MS = 1000L;
   private static final long TRANSFER_WAIT_MS = 5000L;
   private static final long TIMEOUT_MS = 30000L;
   private final StopWatch clickTimer = new StopWatch();
   private final StopWatch reopenTimer = new StopWatch();
   private final StopWatch timeoutTimer = new StopWatch();
   private boolean active;
   private boolean targetClicked;
   private int grief = -1;

   public JoinCommand() {
      super("join");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Используйте: .join <номер грифа> или .join stop");
         return 1;
      });
      literalArgumentBuilder.then(literal("stop").executes(var1x -> {
         if (!this.active) {
            ChatLogger.method2019("Joiner сейчас не запущен");
         } else {
            this.stop();
            ChatLogger.method2019("Joiner остановлен");
         }

         return 1;
      }));
      literalArgumentBuilder.then(arg("grief", IntegerArgumentType.integer(1)).executes(var1x -> {
         this.start((Integer)var1x.getArgument("grief", Integer.class));
         return 1;
      }));
   }

   private void start(int grief) {
      this.grief = grief;
      this.active = true;
      this.targetClicked = false;
      this.clickTimer.reset();
      this.reopenTimer.reset();
      this.timeoutTimer.reset();
      ChatLogger.method2019("Пробую зайти на гриф #" + grief);
      if (!this.useCompass()) {
         this.stop();
         ChatLogger.method2022("Компас не найден в хотбаре");
      }
   }

   private void stop() {
      this.active = false;
      this.targetClicked = false;
      this.grief = -1;
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (this.active && event.method7100() && event.getPacket3() instanceof ClientboundLoginPacket) {
         mc.execute(() -> {
            if (this.active) {
               if (this.targetClicked) {
                  ChatLogger.method2019("Зашёл на гриф #" + this.grief);
                  this.stop();
               } else {
                  this.reopenTimer.reset();
               }
            }
         });
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.active && mc.gameMode != null) {
         if (this.timeoutTimer.method7546(30000L)) {
            int grief = this.grief;
            this.stop();
            ChatLogger.method2022("Не удалось зайти на гриф #" + grief + " за 30 секунд");
         } else if (mc.gui.screen() == null) {
            if (this.reopenTimer.method7546(this.targetClicked ? 5000L : 1000L)) {
               this.reopenTimer.reset();
               this.targetClicked = false;
               this.useCompass();
            }
         } else {
            this.reopenTimer.reset();
            if (mc.gui.screen() instanceof ContainerScreen containerScreen && this.clickTimer.method7546(100L)) {
               this.handleMenu(containerScreen);
            }
         }
      }
   }

   private void handleMenu(ContainerScreen containerScreen) {
      int index = -1;
      int n = -1;
      int index2 = -1;
      boolean flag = false;
      String str = "гриф #" + this.grief;
      int containerSize = containerScreen.getMenu().getContainer().getContainerSize();

      for (Slot slot : containerScreen.getMenu().slots) {
         if (slot.index >= containerSize) {
            break;
         }

         ItemStack stack = slot.getItem();
         if (!stack.isEmpty()) {
            String str2 = normalize(stack.getHoverName().getString());
            if (isTargetGrief(str2, str)) {
               index = slot.index;
            }

            if (str2.contains("гриф #")) {
               flag = true;
            }

            if (str2.contains("гриферское выживание") && !str2.contains("ru")) {
               n = slot.index;
            }

            if (str2.contains("следующ")) {
               index2 = slot.index;
            }
         }
      }

      if (index != -1) {
         this.click(index);
         this.targetClicked = true;
      } else if (n != -1 && !flag && ServerHelperModule.INSTANCE.method5181()) {
         this.click(n);
      } else if (flag) {
         if (index2 != -1) {
            this.click(index2);
         } else {
            int grief = this.grief;
            this.stop();
            mc.player.closeContainer();
            ChatLogger.method2022("Гриф #" + grief + " не найден");
         }
      }
   }

   private static boolean isTargetGrief(String str, String str2) {
      for (int i = str.indexOf(str2); i != -1; i = str.indexOf(str2, i + 1)) {
         int index = i + str2.length();
         if (index >= str.length() || !Character.isDigit(str.charAt(index))) {
            return true;
         }
      }

      return false;
   }

   private void click(int index) {
      InventoryUtils.method904(index, 0, ContainerInput.PICKUP, false);
      this.clickTimer.reset();
   }

   private boolean useCompass() {
      if (mc.player != null && mc.gameMode != null && mc.getConnection() != null) {
         for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).is(Items.COMPASS)) {
               mc.player.getInventory().setSelectedSlot(i);
               mc.getConnection().send(new ServerboundSetCarriedItemPacket(i));
               mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String normalize(String string) {
      String stripFormatting = ChatFormatting.stripFormatting(string);
      return (stripFormatting == null ? string : stripFormatting).toLowerCase(Locale.ROOT);
   }
}
