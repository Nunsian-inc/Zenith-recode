package dev.porno.playerAnim.minecraftApi.codec;

import dev.porno.playerAnim.api.IPlayable;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimationCodecs {
   private static final Logger logger = LoggerFactory.getLogger(AnimationCodecs.class);
   public static final AnimationCodecs INSTANCE = new AnimationCodecs();
   private final List<AnimationCodec<?>> codecs = new ArrayList<>();

   private AnimationCodecs() {
   }

   public void registerCodec(AnimationCodec<?> codec) {
      this.codecs.add(codec);
   }

   @NotNull
   public List<AnimationCodec<?>> getCodec(@NotNull String fileExtension) {
      return this.codecs.stream().filter(it -> Objects.equals(it.getExtension(), fileExtension)).toList();
   }

   @NotNull
   public List<AnimationCodec<?>> getAllCodecs() {
      return this.codecs;
   }

   @Nullable
   public static String getExtension(@NotNull String filename) {
      if (filename.isEmpty()) {
         return null;
      }

      int i = filename.lastIndexOf(46);
      if (i > 0) {
         filename = filename.substring(i + 1);
      }

      return filename;
   }

   @NotNull
   @Experimental
   public static Collection<IPlayable> deserialize(@Nullable String extension, @NotNull InputStream inputStream) throws IOException {
      byte[] data = inputStream.readAllBytes();

      try {
         inputStream.close();
      } catch (Exception var4) {
      }

      return deserialize(extension, () -> new ByteArrayInputStream(data));
   }

   @NotNull
   public static Collection<IPlayable> deserialize(@Nullable String extension, @NotNull Supplier<InputStream> inputStreamSupplier) {
      List<IPlayable> animations = new LinkedList<>();

      for (AnimationCodec<?> deserializer : extension == null ? INSTANCE.getAllCodecs() : INSTANCE.getCodec(extension)) {
         try (InputStream reader = inputStreamSupplier.get()) {
            Collection<? extends IPlayable> result = (Collection<? extends IPlayable>)deserializer.decode(new BufferedInputStream(reader));
            if (result.isEmpty()) {
               throw new RuntimeException("Decoder is not obeying API");
            }

            animations.addAll(result);
            break;
         } catch (IOException e) {
            logger.info(String.format("Failed to apply %s", deserializer.getFormatName()), e);
         } catch (Exception e) {
            logger.error(String.format("Unknown error when trying to apply %s", deserializer.getFormatName()), e);
         }
      }

      return animations;
   }

   static {
      INSTANCE.registerCodec(EmotecraftGsonCodec.INSTANCE);
      INSTANCE.registerCodec(LegacyGeckoJsonCodec.INSTANCE);
   }
}
