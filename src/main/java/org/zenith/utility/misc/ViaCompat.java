package org.zenith.utility.misc;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;

public final class ViaCompat {
   private static final boolean CONST_flag = FabricLoader.getInstance().isModLoaded("viafabricplus");

   private ViaCompat() {
   }

   public static float method4846(ClientInput input) {
      return input.getMoveVector().y;
   }

   public static float method4847(ClientInput input) {
      return input.getMoveVector().x;
   }

   public static void method4848(ClientInput input, float movementMultiplier) {
      method4850(input, movementMultiplier, method4847(input));
   }

   public static void method4849(ClientInput input, float movementMultiplier) {
      method4850(input, method4846(input), movementMultiplier);
   }

   public static void method4850(ClientInput input, float movementMultiplier, float x) {
      Input keyPresses = input.keyPresses;
      input.keyPresses = new Input(movementMultiplier > 0.0F, movementMultiplier < 0.0F, x > 0.0F, x < 0.0F, keyPresses.jump(), keyPresses.shift(), keyPresses.sprint());
      ((ViaCompat.Handler)input).zenith$setMovementVector(new net.minecraft.world.phys.Vec2(x, movementMultiplier));
   }

   public static net.minecraft.world.phys.Vec2 method4851(net.minecraft.world.phys.Vec2 vec) {
      return method4852() ? vec : vec.normalized();
   }

   private static boolean method4852() {
      if (!CONST_flag) {
         return false;
      } else {
         try {
            return ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4);
         } catch (Throwable e) {
            return false;
         }
      }
   }

   public interface Handler {
      void zenith$setMovementVector(net.minecraft.world.phys.Vec2 vec);
   }
}
