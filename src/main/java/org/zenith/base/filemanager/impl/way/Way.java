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

   public Way(String name, BlockPos pos, String server) {
      this.name = name;
      this.pos = pos;
      this.server = server;
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
      String str = this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ();
      String str2 = this.name.replace("\"", "\\\"");
      MutableComponent literal = Component.literal("? " + this.name);
      literal.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(16765286))
            .withHoverEvent(new ShowText(Component.literal("Место: " + this.name + "\nСервер: " + this.server)))
      );
      MutableComponent component = Component.literal("  ⟨" + this.pos.getX() + ", " + this.pos.getY() + ", " + this.pos.getZ() + "⟩");
      component.setStyle(
         Style.EMPTY
            .withColor(TextColor.fromRgb(1149618))
            .withHoverEvent(new ShowText(Component.literal("Нажми, чтобы скопировать координаты")))
            .withClickEvent(new CopyToClipboard(str))
      );
      MutableComponent literal2 = Component.literal("  @" + this.server);
      literal2.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
      MutableComponent literal3 = Component.literal("  [Удалить]");
      literal3.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(15681391))
            .withHoverEvent(new ShowText(Component.literal("Подставить команду удаления точки " + this.name)))
            .withClickEvent(new SuggestCommand(".way remove " + str2))
      );
      return Component.empty().append(literal).append(component).append(literal2).append(literal3);
   }
}
