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

   public static void method4657(Minecraft minecraft, User user) {
      MinecraftClientAccessor clientAccessor = (MinecraftClientAccessor)(Object)minecraft;
      clientAccessor.zenith$setSession(user);

      try {
         MinecraftServicesDiscoveryService serviceAccess = MinecraftServicesDiscoveryService.create(clientAccessor.zenith$getNetworkProxy(), true);

         try {
            Services services = Services.create(serviceAccess, minecraft.gameDirectory);
            clientAccessor.zenith$setApiServices(services);
            System.out.println("[ZenithAlts] Обновлены ApiServices на окружение Mojang PROD.");
         } catch (Throwable e) {
            System.err.println("[ZenithAlts] Не удалось обновить ApiServices: " + e.getMessage());
            e.printStackTrace();
         }

         UserApiService userApiService = UserApiService.OFFLINE;

         try {
            if (user.getAccessToken() != null && !user.getAccessToken().isBlank()) {
               userApiService = serviceAccess.createUserApiService(user.getAccessToken());
               System.out.println("[ZenithAlts] UserApiService создан с access-токеном.");
            }
         } catch (Throwable e) {
         }

         clientAccessor.zenith$setUserApiService(userApiService);

         try {
            clientAccessor.zenith$setProfileKeys(ProfileKeyPairManager.create(userApiService, user, minecraft.gameDirectory.toPath()));
         } catch (Throwable e) {
         }

         try {
            clientAccessor.zenith$setAbuseReportContext(ReportingContext.create(ReportEnvironment.local(), userApiService));
         } catch (Throwable e) {
         }
      } catch (Throwable e) {
         System.err.println("[ZenithAlts] Ошибка в applySession: " + e.getMessage());
         e.printStackTrace();
      }
   }
}
