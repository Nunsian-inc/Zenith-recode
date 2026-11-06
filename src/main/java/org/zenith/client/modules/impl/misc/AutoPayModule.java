package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.ScoreboardUtils;

@ModuleInfo(
   name = "AutoPay",
   category = ModuleCategory.MISC,
   description = "module.autopay.description"
)
public final class AutoPayModule extends Module {
   public static final AutoPayModule INSTANCE = new AutoPayModule();
   private static final long PAY_INTERVAL_MS = 5000L;
   private final TextSetting nick = new TextSetting("autopay.nick", "", "autopay.nick.empty", TextSetting.Entry.method2125(16));
   private final TextSetting maxAmount = new TextSetting(
      "autopay.maxAmount",
      "100000",
      "autopay.maxAmount.empty",
      TextSetting.Entry.method2126(12, amountText -> amountText.isEmpty() || amountText.chars().allMatch(Character::isDigit))
   );
   private final StopWatch paymentTimer = new StopWatch();
   private long balance = -1L;

   private AutoPayModule() {
   }

   @Override
   public void onEnable() {
      this.balance = -1L;
      this.paymentTimer.reset();
      super.onEnable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         long balance = ScoreboardUtils.method5530(mc.level.getScoreboard(), mc.player.getScoreboardName());
         if (balance >= 0L) {
            this.balance = balance;
         }

         if (this.balance >= 0L) {
            String recipientName = this.nick.getValue().trim();
            long retainedAmount = parseAmount(this.maxAmount.getValue().trim());
            if (!recipientName.isEmpty() && retainedAmount >= 0L) {
               long paymentAmount = this.balance - retainedAmount;
               if (paymentAmount > 0L && this.paymentTimer.method7546(5000L)) {
                  mc.player.connection.sendCommand("pay " + recipientName + " " + paymentAmount);
                  this.paymentTimer.reset();
               }
            }
         }
      }
   }

   private static long parseAmount(String amountText) {
      if (amountText.isEmpty()) {
         return -1L;
      } else {
         try {
            return Long.parseLong(amountText);
         } catch (NumberFormatException e) {
            return -1L;
         }
      }
   }

   @Generated
   public long getLongValue2() {
      return this.balance;
   }
}
