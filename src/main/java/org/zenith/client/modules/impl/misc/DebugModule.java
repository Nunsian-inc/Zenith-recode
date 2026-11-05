package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.MovementInputUtils;

@ModuleInfo(
   name = "Debug",
   category = ModuleCategory.MISC,
   description = "module.debug.description"
)
public class DebugModule extends Module {
   public static final DebugModule INSTANCE = new DebugModule();
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private final SliderSetting messageDelay = new SliderSetting(
      "module.debug.messageDelay", 250.0F, 0.0F, 2000.0F, 50.0F, "module.debug.messageDelay.desc", "ms"
   );
   private final StopWatch stopWatch = new StopWatch();
   private final File file;
   private PlayerSimulation playerSimulation;
   private Vec3 vec3d;

   private DebugModule() {
      File var1 = new File(Minecraft.getInstance().gameDirectory, "zenith");
      if (!var1.exists() && !var1.mkdirs()) {
         ChatLogger.method2021("Debug: failed to create log directory");
      }

      this.file = new File(var1, "debug_packets.log");
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.level != null) {
         this.vec3d = mc.player.position();
         this.playerSimulation = PlayerSimulation.method6031(1);
      } else {
         this.playerSimulation = null;
         this.vec3d = null;
      }
   }

   @EventTarget
   public void method4452(PlayerTickEvent var1) {
      if (mc.player != null && mc.level != null && this.playerSimulation != null && this.vec3d != null) {
         Vec3 var2 = mc.player.position();
         Vec3 var3 = this.playerSimulation.vec3d;
         Vec3 var4 = var2.subtract(var3);
         double var5 = Math.sqrt(var4.x * var4.x + var4.z * var4.z);
         double var7 = var4.length();
         if (this.stopWatch.method7546((long)this.messageDelay.getValue3())) {
            ChatLogger.method2019(
               String.format(
                  Locale.ROOT,
                  "Predict diff: total=%.5f h=%.5f x=%.5f y=%.5f z=%.5f | real=(%.3f %.3f %.3f) pred=(%.3f %.3f %.3f)",
                  var7,
                  var5,
                  var4.x,
                  var4.y,
                  var4.z,
                  var2.x,
                  var2.y,
                  var2.z,
                  var3.x,
                  var3.y,
                  var3.z
               )
            );
            this.stopWatch.reset();
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      Packet var2 = var1.getPacket3();
      if (mc.player != null) {
         if (var2 instanceof ServerboundContainerClickPacket var3) {
            short var4 = var3.slotNum();
            Object var5 = mc.player.containerMenu.isValidSlotIndex(var4) ? mc.player.containerMenu.getSlot(var4).getItem() : "";
            this.method4453(var3.containerInput().name() + ": " + var4 + " " + MovementInputUtils.method7418() + " " + var5);
         }
      }
   }

   private void method4453(String var1) {
      String var2 = LocalTime.now().format(dateTimeFormatter);

      try (BufferedWriter var3 = new BufferedWriter(new FileWriter(this.file, true))) {
         var3.write(var2 + " | " + var1);
         var3.newLine();
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }
}
