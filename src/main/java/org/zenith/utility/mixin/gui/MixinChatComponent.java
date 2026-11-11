package org.zenith.utility.mixin.gui;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.client.modules.impl.render.BetterMinecraftModule;
import org.zenith.utility.misc.RussianDurationFormatter;

/**
 * Антиспам чата (BetterMinecraft → Чат → Антиспам): если пришло сообщение, которое уже было в чате
 * (не обязательно подряд), старая копия убирается из истории, а сообщение выводится внизу как новое
 * со счётчиком повторов в скобках: «Сообщение (x3)».
 */
@Mixin(ChatComponent.class)
public abstract class MixinChatComponent {
   /** Сколько разных сообщений помнить для счётчика: история чата хранит только последние 100. */
   @Unique
   private static final int ZENITH$MAX_TRACKED = 512;
   @Shadow
   @Final
   private List<GuiMessage> allMessages;
   @Shadow
   @Final
   private List<GuiMessage.Line> trimmedMessages;
   @Shadow
   private int chatScrollbarPos;
   /** Текст сообщения (без счётчика) → сколько раз оно приходило; порядок доступа — чтобы вытеснять самые старые. */
   @Unique
   private final Map<Component, Integer> zenith$repeatCounts = new LinkedHashMap<>(64, 0.75F, true);

   /**
    * Срабатывает перед logChatMessage: сюда доходят только сообщения, прошедшие фильтр видимости чата,
    * и подменённое сообщение дальше попадает и в историю (allMessages), и на экран (trimmedMessages).
    */
   @ModifyVariable(
      method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/components/ChatComponent;logChatMessage(Lnet/minecraft/client/multiplayer/chat/GuiMessage;)V"
      )
   )
   private GuiMessage zenith$stackRepeatedMessage(GuiMessage message) {
      // Серверные ответы меняем только после сетевой обработки; сообщения игроков и подписи не трогаем.
      if (BetterMinecraftModule.INSTANCE.isReadableDurationsEnabled()
         && message.source() == GuiMessageSource.SYSTEM_SERVER && message.signature() == null) {
         Component formatted = RussianDurationFormatter.format(message.content());
         if (formatted != message.content()) {
            message = new GuiMessage(message.addedTime(), formatted, message.signature(), message.source(), message.tag());
         }
      }

      if (!BetterMinecraftModule.INSTANCE.isAntiSpamEnabled()) {
         this.zenith$repeatCounts.clear();
         return message;
      }

      Component content = message.content();
      Integer seen = this.zenith$repeatCounts.get(content);
      int previousIndex = this.zenith$findPreviousCopy(message, seen);
      // если старая копия есть в истории, а счётчика нет (пришла при выключенном антиспаме) — она считается за первое появление
      int count = (seen != null ? seen : previousIndex >= 0 ? 1 : 0) + 1;
      this.zenith$repeatCounts.put(content, count);
      if (this.zenith$repeatCounts.size() > ZENITH$MAX_TRACKED) {
         Iterator<Component> eldest = this.zenith$repeatCounts.keySet().iterator();
         eldest.next();
         eldest.remove();
      }

      if (previousIndex >= 0) {
         this.zenith$removeMessage(previousIndex);
      }

      return count == 1 ? message : new GuiMessage(message.addedTime(), zenith$withCounter(content, count), message.signature(), message.source(), message.tag());
   }

   /** Индекс прошлой копии в истории: то же сообщение того же источника — как есть или с текущим счётчиком. */
   @Unique
   private int zenith$findPreviousCopy(GuiMessage message, Integer seen) {
      Component counted = seen == null ? null : zenith$withCounter(message.content(), seen);
      for (int i = 0; i < this.allMessages.size(); i++) {
         GuiMessage old = this.allMessages.get(i);
         if (old.source() == message.source() && (old.content().equals(message.content()) || old.content().equals(counted))) {
            return i;
         }
      }

      return -1;
   }

   /** Убирает сообщение из истории и его строки с экрана. */
   @Unique
   private void zenith$removeMessage(int index) {
      GuiMessage old = this.allMessages.remove(index);
      int removedBelowView = 0;
      for (int i = this.trimmedMessages.size() - 1; i >= 0; i--) {
         if (this.trimmedMessages.get(i).parent() == old) {
            this.trimmedMessages.remove(i);
            if (i < this.chatScrollbarPos) {
               removedBelowView++;
            }
         }
      }

      // чат прокручен вверх: не даём ему съехать из-за строк, удалённых ниже видимой области
      this.chatScrollbarPos = Math.max(0, this.chatScrollbarPos - removedBelowView);
   }

   @Unique
   private static Component zenith$withCounter(Component content, int count) {
      return content.copy().append(Component.literal(" (x" + count + ")").withStyle(ChatFormatting.GRAY));
   }
}
