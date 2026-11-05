package org.zenith.utility.misc;

import net.minecraft.world.phys.Vec3;

public interface Handler6 {
   double zenithDLC$getPrevServerX();

   double zenithDLC$getPrevServerY();

   double zenithDLC$getPrevServerZ();

   void zenithDLC$trackServerPos(double x, double y, double z);

   Vec3 zenithDLC$getServerDelta();
}
