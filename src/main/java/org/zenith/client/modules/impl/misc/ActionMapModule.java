package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "ActionMap",
   category = ModuleCategory.MISC,
   description = "module.actionmap.description"
)
public class ActionMapModule extends Module {
   public static final ActionMapModule INSTANCE = new ActionMapModule();
   private final BooleanSetting hideNoise = new BooleanSetting("module.actionMap.hideNoise", true);
   private final BooleanSetting gapMark = new BooleanSetting("module.actionMap.gapMark", true);
   private PrintWriter printWriter;
   private long longValue;
   private long longValue2;
   private String text = "";
   private int intValue;
   private float floatValue;
   private float floatValue2;
   private long longValue3;
   private boolean flag;

   private ActionMapModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.longValue = System.currentTimeMillis();
      this.longValue2 = this.longValue;
      this.text = "";
      this.intValue = 0;
      this.method1366();
      this.method1367(0L, "=== ActionMap start — выполняй ОДНО действие, пауза 2с, следующее ===");
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method1361();
      ChatLogger.method2019("ActionMap: остановлен");
      this.method1368();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (!event.method7100()) {
         Packet packet3 = event.getPacket3();
         if (packet3 != null) {
            long now = System.currentTimeMillis();
            long l = now - this.longValue;
            if (packet3 instanceof ServerboundClientTickEndPacket && mc.player != null) {
               this.floatValue = mc.player.getYRot();
               this.floatValue2 = mc.player.getXRot();
               this.flag = true;
            }

            if (!this.hideNoise.isEnabled()
               || !(packet3 instanceof ServerboundClientTickEndPacket) && !(packet3 instanceof ServerboundPongPacket) && !(packet3 instanceof ServerboundKeepAlivePacket)) {
               if (packet3 instanceof ServerboundMovePlayerPacket movePlayerPacket) {
                  String text = this.method1362(movePlayerPacket);
                  if (text.equals(this.text)) {
                     this.intValue++;
                  } else {
                     this.method1361();
                     this.text = text;
                     this.intValue = 1;
                  }
               } else {
                  this.method1361();
                  if (this.gapMark.isEnabled() && now - this.longValue2 > 900L) {
                     this.method1367(l, "· · · пауза " + (now - this.longValue2) + "мс · · ·");
                  }

                  this.longValue2 = now;
                  this.method1367(l, this.method1363(packet3));
               }
            }
         }
      }
   }

   private void method1361() {
      if (this.intValue > 0) {
         this.method1367(System.currentTimeMillis() - this.longValue, "MOVE:" + this.text + " ×" + this.intValue);
         this.intValue = 0;
         this.text = "";
      }
   }

   private String method1362(ServerboundMovePlayerPacket movePlayerPacket) {
      boolean hasPosition = movePlayerPacket.hasPosition();
      boolean hasRotation = movePlayerPacket.hasRotation();
      if (hasPosition && hasRotation) {
         return "Full";
      } else if (hasPosition) {
         return "PositionAndOnGround";
      } else {
         return hasRotation ? "LookAndOnGround" : "OnGroundOnly";
      }
   }

   private String method1363(Packet<?> packet3) {
      if (packet3 instanceof ServerboundInteractPacket || packet3 instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
         return this.method1364();
      } else if (packet3 instanceof ServerboundPunchPacket) {
         return "HandSwing  (взмах рукой)";
      } else if (packet3 instanceof ServerboundUseItemOnPacket useItemOnPacket) {
         return "PlayerInteractBlock  hand=" + useItemOnPacket.hand() + " seq=" + useItemOnPacket.sequence() + "  (ПКМ по блоку / постановка)";
      } else if (packet3 instanceof ServerboundUseItemPacket useItemPacket) {
         return "PlayerInteractItem  hand=" + useItemPacket.hand() + " seq=" + useItemPacket.sequence() + "  (ПКМ предметом / еда, зелье)";
      } else if (packet3 instanceof ServerboundContainerClickPacket containerClickPacket) {
         return "ClickSlot  syncId=" + containerClickPacket.containerId() + " slot=" + containerClickPacket.slotNum() + " btn=" + containerClickPacket.buttonNum() + " type=" + containerClickPacket.containerInput() + "  (клик в окне)";
      } else if (packet3 instanceof ServerboundContainerClosePacket containerClosePacket) {
         return "CloseHandledScreen  syncId=" + containerClosePacket.getContainerId() + "  (закрытие окна)";
      } else if (packet3 instanceof ServerboundSetCarriedItemPacket setCarriedItemPacket) {
         return "UpdateSelectedSlot  slot=" + setCarriedItemPacket.getSlot() + "  (смена слота хотбара)";
      } else if (packet3 instanceof ServerboundPlayerCommandPacket playerCommandPacket) {
         return "ClientCommand  " + playerCommandPacket.getAction() + "  (спринт/шифт/респавн)";
      } else if (packet3 instanceof ServerboundChatCommandPacket chatCommandPacket) {
         return "CommandExecution  /" + chatCommandPacket.command() + "  (ввод команды)";
      } else if (packet3 instanceof ServerboundChatPacket serverboundChatPacket) {
         return "ChatMessage  \"" + serverboundChatPacket.message() + "\"  (сообщение в чат)";
      } else if (packet3 instanceof ServerboundPlayerActionPacket playerActionPacket) {
         String str = switch (playerActionPacket.getAction()) {
            case START_DESTROY_BLOCK -> "начало ломки блока";
            case STOP_DESTROY_BLOCK -> "конец ломки блока";
            case ABORT_DESTROY_BLOCK -> "отмена ломки";
            case DROP_ITEM -> "выброс 1 предмета (Q)";
            case DROP_ALL_ITEMS -> "выброс стака (Ctrl+Q)";
            case RELEASE_USE_ITEM -> "отпуск использования (лук, еда)";
            case SWAP_ITEM_WITH_OFFHAND -> "перекладка в оффхенд (F)";
            default -> playerActionPacket.getAction().name();
         };
         return "PlayerAction  " + playerActionPacket.getAction() + "  (" + str + ")";
      } else {
         return packet3.getClass().getSimpleName();
      }
   }

   private String method1364() {
      long now = System.currentTimeMillis();
      long lng = this.longValue3 == 0L ? -1L : now - this.longValue3;
      this.longValue3 = now;
      StringBuilder builder = new StringBuilder("ATTACK  ");
      if (mc.player != null) {
         float yRot = mc.player.getYRot();
         float xRot = mc.player.getXRot();
         float f = this.flag ? this.method1365(yRot - this.floatValue) : 0.0F;
         float f2 = xRot - this.floatValue2;
         builder.append(String.format("yaw=%.1f pitch=%.1f  Δyaw=%.1f Δpitch=%.1f  ", yRot, xRot, f, f2));
      }

      builder.append("interval=").append(lng).append("ms  ");
      boolean flag = mc.crosshairPickEntity != null;
      builder.append(flag ? "цель=ПОД ПРИЦЕЛОМ" : "цель=НЕ под прицелом");
      return builder.toString();
   }

   private float method1365(float f) {
      while (f > 180.0F) {
         f -= 360.0F;
      }

      while (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   private void method1366() {
      try {
         File file = new File(Minecraft.getInstance().gameDirectory, "zenith-itemdump");
         if (!file.exists() && !file.mkdirs()) {
            return;
         }

         File file2 = new File(file, "actionmap-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(file2, true), true);
         ChatLogger.method2019("ActionMap: " + file2.getName());
      } catch (Exception e) {
         this.printWriter = null;
      }
   }

   private void method1367(long l, String str) {
      if (this.printWriter != null) {
         this.printWriter.printf("%6d  %s%n", l, str);
      }
   }

   private void method1368() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception e) {
         }

         this.printWriter = null;
      }
   }
}
