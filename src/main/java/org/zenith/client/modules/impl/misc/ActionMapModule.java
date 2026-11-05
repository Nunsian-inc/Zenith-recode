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
   public void onPacket(PacketEvent var1) {
      if (!var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 != null) {
            long var3 = System.currentTimeMillis();
            long var5 = var3 - this.longValue;
            if (var2 instanceof ServerboundClientTickEndPacket && mc.player != null) {
               this.floatValue = mc.player.getYRot();
               this.floatValue2 = mc.player.getXRot();
               this.flag = true;
            }

            if (!this.hideNoise.isEnabled()
               || !(var2 instanceof ServerboundClientTickEndPacket) && !(var2 instanceof ServerboundPongPacket) && !(var2 instanceof ServerboundKeepAlivePacket)) {
               if (var2 instanceof ServerboundMovePlayerPacket var7) {
                  String var8 = this.method1362(var7);
                  if (var8.equals(this.text)) {
                     this.intValue++;
                  } else {
                     this.method1361();
                     this.text = var8;
                     this.intValue = 1;
                  }
               } else {
                  this.method1361();
                  if (this.gapMark.isEnabled() && var3 - this.longValue2 > 900L) {
                     this.method1367(var5, "· · · пауза " + (var3 - this.longValue2) + "мс · · ·");
                  }

                  this.longValue2 = var3;
                  this.method1367(var5, this.method1363(var2));
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

   private String method1362(ServerboundMovePlayerPacket var1) {
      boolean var2 = var1.hasPosition();
      boolean var3 = var1.hasRotation();
      if (var2 && var3) {
         return "Full";
      } else if (var2) {
         return "PositionAndOnGround";
      } else {
         return var3 ? "LookAndOnGround" : "OnGroundOnly";
      }
   }

   private String method1363(Packet<?> var1) {
      if (var1 instanceof ServerboundInteractPacket || var1 instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
         return this.method1364();
      } else if (var1 instanceof ServerboundPunchPacket) {
         return "HandSwing  (взмах рукой)";
      } else if (var1 instanceof ServerboundUseItemOnPacket var11) {
         return "PlayerInteractBlock  hand=" + var11.hand() + " seq=" + var11.sequence() + "  (ПКМ по блоку / постановка)";
      } else if (var1 instanceof ServerboundUseItemPacket var10) {
         return "PlayerInteractItem  hand=" + var10.hand() + " seq=" + var10.sequence() + "  (ПКМ предметом / еда, зелье)";
      } else if (var1 instanceof ServerboundContainerClickPacket var9) {
         return "ClickSlot  syncId=" + var9.containerId() + " slot=" + var9.slotNum() + " btn=" + var9.buttonNum() + " type=" + var9.containerInput() + "  (клик в окне)";
      } else if (var1 instanceof ServerboundContainerClosePacket var8) {
         return "CloseHandledScreen  syncId=" + var8.getContainerId() + "  (закрытие окна)";
      } else if (var1 instanceof ServerboundSetCarriedItemPacket var7) {
         return "UpdateSelectedSlot  slot=" + var7.getSlot() + "  (смена слота хотбара)";
      } else if (var1 instanceof ServerboundPlayerCommandPacket var6) {
         return "ClientCommand  " + var6.getAction() + "  (спринт/шифт/респавн)";
      } else if (var1 instanceof ServerboundChatCommandPacket var5) {
         return "CommandExecution  /" + var5.command() + "  (ввод команды)";
      } else if (var1 instanceof ServerboundChatPacket var4) {
         return "ChatMessage  \"" + var4.message() + "\"  (сообщение в чат)";
      } else if (var1 instanceof ServerboundPlayerActionPacket var2) {
         String var3 = switch (var2.getAction()) {
            case START_DESTROY_BLOCK -> "начало ломки блока";
            case STOP_DESTROY_BLOCK -> "конец ломки блока";
            case ABORT_DESTROY_BLOCK -> "отмена ломки";
            case DROP_ITEM -> "выброс 1 предмета (Q)";
            case DROP_ALL_ITEMS -> "выброс стака (Ctrl+Q)";
            case RELEASE_USE_ITEM -> "отпуск использования (лук, еда)";
            case SWAP_ITEM_WITH_OFFHAND -> "перекладка в оффхенд (F)";
            default -> var2.getAction().name();
         };
         return "PlayerAction  " + var2.getAction() + "  (" + var3 + ")";
      } else {
         return var1.getClass().getSimpleName();
      }
   }

   private String method1364() {
      long var1 = System.currentTimeMillis();
      long var3 = this.longValue3 == 0L ? -1L : var1 - this.longValue3;
      this.longValue3 = var1;
      StringBuilder var5 = new StringBuilder("ATTACK  ");
      if (mc.player != null) {
         float var6 = mc.player.getYRot();
         float var7 = mc.player.getXRot();
         float var8 = this.flag ? this.method1365(var6 - this.floatValue) : 0.0F;
         float var9 = var7 - this.floatValue2;
         var5.append(String.format("yaw=%.1f pitch=%.1f  Δyaw=%.1f Δpitch=%.1f  ", var6, var7, var8, var9));
      }

      var5.append("interval=").append(var3).append("ms  ");
      boolean var10 = mc.crosshairPickEntity != null;
      var5.append(var10 ? "цель=ПОД ПРИЦЕЛОМ" : "цель=НЕ под прицелом");
      return var5.toString();
   }

   private float method1365(float var1) {
      while (var1 > 180.0F) {
         var1 -= 360.0F;
      }

      while (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private void method1366() {
      try {
         File var1 = new File(Minecraft.getInstance().gameDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         File var2 = new File(var1, "actionmap-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(var2, true), true);
         ChatLogger.method2019("ActionMap: " + var2.getName());
      } catch (Exception var3) {
         this.printWriter = null;
      }
   }

   private void method1367(long var1, String var3) {
      if (this.printWriter != null) {
         this.printWriter.printf("%6d  %s%n", var1, var3);
      }
   }

   private void method1368() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }
}
