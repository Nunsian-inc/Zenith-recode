package dev.porno.playerAnim.minecraftApi.codec;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.porno.playerAnim.core.data.KeyframeAnimation;
import dev.porno.playerAnim.core.data.gson.AnimationJson;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class EmotecraftGsonCodec extends AbstractGsonCodec<KeyframeAnimation> {
   public static final EmotecraftGsonCodec INSTANCE = new EmotecraftGsonCodec();

   @Override
   protected Gson getGson() {
      return AnimationJson.GSON;
   }

   @Override
   protected Type getListedTypeToken() {
      return AnimationJson.getListedTypeToken();
   }

   @NotNull
   @Override
   public String getFormatName() {
      return "emotecraft";
   }

   public void encode(@NotNull OutputStream output, @NotNull Identifier location, @NotNull KeyframeAnimation animation) throws IOException {
      try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
         writer.write(this.getGson().toJson(animation));
      } catch (JsonSyntaxException e) {
         throw new IOException(e);
      }
   }
}
