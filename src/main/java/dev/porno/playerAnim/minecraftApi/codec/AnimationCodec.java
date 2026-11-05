package dev.porno.playerAnim.minecraftApi.codec;

import dev.porno.playerAnim.api.IPlayable;
import java.io.IOException;
import java.io.OutputStream;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public interface AnimationCodec<T extends IPlayable> extends AnimationEncoder<T>, AnimationDecoder<T> {
   @Override
   default void encode(@NotNull OutputStream output, @NotNull Identifier location, @NotNull T animation) throws IOException {
      throw new UnsupportedOperationException();
   }

   @NotNull
   String getFormatName();

   @NotNull
   String getExtension();
}
