package org.zenith.base.filemanager.impl.way;

import com.google.gson.annotations.SerializedName;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent.CopyToClipboard;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class Way {
   @SerializedName("name")
   private final String name;
   @SerializedName("pos")
   private final BlockPos pos;
   @SerializedName("server")
   private final String server;

   public Way(String var1, BlockPos var2, String var3) {
      this.name = var1;
      this.pos = var2;
      this.server = var3;
   }

   public String name() {
      return this.name;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public String server() {
      return this.server;
   }

   public Component toText() {
      String var1 = this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ();
      String var2 = this.name.replace("\"", "\\\"");
      MutableComponent var3 = Component.literal("? " + this.name);
      var3.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(16765286))
            .withHoverEvent(new ShowText(Component.literal("Место: " + this.name + "\nСервер: " + this.server)))
      );
      MutableComponent var4 = Component.literal("  ⟨" + this.pos.getX() + ", " + this.pos.getY() + ", " + this.pos.getZ() + "⟩");
      var4.setStyle(
         Style.EMPTY
            .withColor(TextColor.fromRgb(1149618))
            .withHoverEvent(new ShowText(Component.literal("Нажми, чтобы скопировать координаты")))
            .withClickEvent(new CopyToClipboard(var1))
      );
      MutableComponent var5 = Component.literal("  @" + this.server);
      var5.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
      MutableComponent var6 = Component.literal("  [Удалить]");
      var6.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(15681391))
            .withHoverEvent(new ShowText(Component.literal("Подставить команду удаления точки " + this.name)))
            .withClickEvent(new SuggestCommand(".way remove " + var2))
      );
      return Component.empty().append(var3).append(var4).append(var5).append(var6);
   }
}
