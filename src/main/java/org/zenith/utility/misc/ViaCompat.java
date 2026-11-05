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

   public static float method4846(ClientInput var0) {
      return var0.getMoveVector().y;
   }

   public static float method4847(ClientInput var0) {
      return var0.getMoveVector().x;
   }

   public static void method4848(ClientInput var0, float var1) {
      method4850(var0, var1, method4847(var0));
   }

   public static void method4849(ClientInput var0, float var1) {
      method4850(var0, method4846(var0), var1);
   }

   public static void method4850(ClientInput var0, float var1, float var2) {
      Input var3 = var0.keyPresses;
      var0.keyPresses = new Input(var1 > 0.0F, var1 < 0.0F, var2 > 0.0F, var2 < 0.0F, var3.jump(), var3.shift(), var3.sprint());
      ((ViaCompat.Handler)var0).zenith$setMovementVector(new net.minecraft.world.phys.Vec2(var2, var1));
   }

   public static net.minecraft.world.phys.Vec2 method4851(net.minecraft.world.phys.Vec2 var0) {
      return method4852() ? var0 : var0.normalized();
   }

   private static boolean method4852() {
      if (!CONST_flag) {
         return false;
      } else {
         try {
            return ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4);
         } catch (Throwable var1) {
            return false;
         }
      }
   }

   public interface Handler {
      void zenith$setMovementVector(net.minecraft.world.phys.Vec2 var1);
   }
}
