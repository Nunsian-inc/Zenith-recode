package dev.porno.playerAnim.minecraftApi.codec;

import dev.porno.playerAnim.api.IPlayable;
import java.io.IOException;
import java.io.OutputStream;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AnimationEncoder<T extends IPlayable> {
   void encode(@NotNull OutputStream outputStream, @NotNull Identifier id, @NotNull T value) throws IOException;
}
