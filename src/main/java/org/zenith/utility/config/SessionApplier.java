package org.zenith.utility.config;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.services.MinecraftServicesDiscoveryService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.server.Services;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;

public final class SessionApplier {
   // 26.3 (authlib 10): services come from MinecraftServicesDiscoveryService, the old Yggdrasil service and
   // explicit PROD Environment are gone; create(proxy, true) uses the production discovery endpoint.

   private SessionApplier() {
   }

   public static void method4657(Minecraft var0, User var1) {
      MinecraftClientAccessor var2 = (MinecraftClientAccessor)(Object)var0;
      var2.zenith$setSession(var1);

      try {
         MinecraftServicesDiscoveryService var3 = MinecraftServicesDiscoveryService.create(var2.zenith$getNetworkProxy(), true);

         try {
            Services var4 = Services.create(var3, var0.gameDirectory);
            var2.zenith$setApiServices(var4);
            System.out.println("[ZenithAlts] Обновлены ApiServices на окружение Mojang PROD.");
         } catch (Throwable var9) {
            System.err.println("[ZenithAlts] Не удалось обновить ApiServices: " + var9.getMessage());
            var9.printStackTrace();
         }

         UserApiService var11 = UserApiService.OFFLINE;

         try {
            if (var1.getAccessToken() != null && !var1.getAccessToken().isBlank()) {
               var11 = var3.createUserApiService(var1.getAccessToken());
               System.out.println("[ZenithAlts] UserApiService создан с access-токеном.");
            }
         } catch (Throwable var8) {
         }

         var2.zenith$setUserApiService(var11);

         try {
            var2.zenith$setProfileKeys(ProfileKeyPairManager.create(var11, var1, var0.gameDirectory.toPath()));
         } catch (Throwable var7) {
         }

         try {
            var2.zenith$setAbuseReportContext(ReportingContext.create(ReportEnvironment.local(), var11));
         } catch (Throwable var6) {
         }
      } catch (Throwable var10) {
         System.err.println("[ZenithAlts] Ошибка в applySession: " + var10.getMessage());
         var10.printStackTrace();
      }
   }
}
