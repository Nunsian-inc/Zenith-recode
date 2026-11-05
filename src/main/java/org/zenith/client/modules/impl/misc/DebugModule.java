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
      File file = new File(Minecraft.getInstance().gameDirectory, "zenith");
      if (!file.exists() && !file.mkdirs()) {
         ChatLogger.method2021("Debug: failed to create log directory");
      }

      this.file = new File(file, "debug_packets.log");
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         this.vec3d = mc.player.position();
         this.playerSimulation = PlayerSimulation.method6031(1);
      } else {
         this.playerSimulation = null;
         this.vec3d = null;
      }
   }

   @EventTarget
   public void method4452(PlayerTickEvent event) {
      if (mc.player != null && mc.level != null && this.playerSimulation != null && this.vec3d != null) {
         Vec3 position = mc.player.position();
         Vec3 vec3d = this.playerSimulation.vec3d;
         Vec3 vec = position.subtract(vec3d);
         double d = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
         double length = vec.length();
         if (this.stopWatch.method7546((long)this.messageDelay.getValue3())) {
            ChatLogger.method2019(
               String.format(
                  Locale.ROOT,
                  "Predict diff: total=%.5f h=%.5f x=%.5f y=%.5f z=%.5f | real=(%.3f %.3f %.3f) pred=(%.3f %.3f %.3f)",
                  length,
                  d,
                  vec.x,
                  vec.y,
                  vec.z,
                  position.x,
                  position.y,
                  position.z,
                  vec3d.x,
                  vec3d.y,
                  vec3d.z
               )
            );
            this.stopWatch.reset();
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      Packet packet3 = event.getPacket3();
      if (mc.player != null) {
         if (packet3 instanceof ServerboundContainerClickPacket containerClickPacket) {
            short slotNum = containerClickPacket.slotNum();
            Object obj = mc.player.containerMenu.isValidSlotIndex(slotNum) ? mc.player.containerMenu.getSlot(slotNum).getItem() : "";
            this.method4453(containerClickPacket.containerInput().name() + ": " + slotNum + " " + MovementInputUtils.method7418() + " " + obj);
         }
      }
   }

   private void method4453(String str) {
      String str2 = LocalTime.now().format(dateTimeFormatter);

      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file, true))) {
         bufferedWriter.write(str2 + " | " + str);
         bufferedWriter.newLine();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
