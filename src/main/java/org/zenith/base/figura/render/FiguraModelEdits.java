package org.zenith.base.figura.render;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaPart;

public final class FiguraModelEdits {
   private static final Map<Object, FiguraModelEdits.Edit> EDITS = new IdentityHashMap<>();
   private static FiguraModelEdits.Edit pending;

   private FiguraModelEdits() {
   }

   public static void submitWith(FiguraModelEdits.Edit edit, Runnable submit) {
      FiguraModelEdits.Edit previous = pending;
      pending = edit;

      try {
         submit.run();
      } finally {
         pending = previous;
      }
   }

   public static void capture(Object command) {
      if (pending != null) {
         EDITS.put(command, pending);
      }
   }

   public static FiguraModelEdits.Edit get(Object command) {
      return EDITS.isEmpty() ? null : EDITS.get(command);
   }

   public static void endFrame() {
      EDITS.clear();
   }

   public static FiguraModelEdits.Edit vanillaParts(List<Avatar> avatars, Function<Avatar, VanillaPart> part, EntityModel<?> model) {
      return new FiguraModelEdits.Edit() {
         @Override
         public void apply() {
            for (Avatar avatar : avatars) {
               if (avatar.luaRuntime != null) {
                  VanillaPart vanilla = part.apply(avatar);
                  vanilla.save(model);
                  vanilla.preTransform(model);
               }
            }

            for (Avatar avatar : avatars) {
               if (avatar.luaRuntime != null) {
                  part.apply(avatar).posTransform(model);
               }
            }
         }

         @Override
         public void restore() {
            for (int i = avatars.size() - 1; i >= 0; i--) {
               Avatar avatar = avatars.get(i);
               if (avatar.luaRuntime != null) {
                  part.apply(avatar).restore(model);
               }
            }
         }
      };
   }

   public interface Edit {
      void apply();

      void restore();
   }
}
