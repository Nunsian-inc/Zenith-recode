package org.zenith.utility.mixin.accessors;

import com.mojang.authlib.minecraft.UserApiService;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.server.Services;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Minecraft.class})
public interface MinecraftClientAccessor {
   @Invoker("updateLevelInEngines")
   void zenith$setWorld(@Nullable ClientLevel level);

   @Invoker("startUseItem")
   void zenith$doItemUse();

   @Invoker("startAttack")
   boolean zenith$doAttack();

   @Accessor("user")
   @Mutable
   void zenith$setSession(User user);

   @Accessor("proxy")
   Proxy zenith$getNetworkProxy();

   @Accessor("services")
   @Mutable
   void zenith$setApiServices(Services services);

   @Accessor("userApiService")
   @Mutable
   void zenith$setUserApiService(UserApiService userApiService);

   @Accessor("profileKeyPairManager")
   @Mutable
   void zenith$setProfileKeys(ProfileKeyPairManager profileKeyPairManager);

   @Accessor("reportingContext")
   @Mutable
   void zenith$setAbuseReportContext(ReportingContext reportingContext);

   @Accessor("missTime")
   int zenith$getAttackCooldown();

   @Accessor("missTime")
   void zenith$setAttackCooldown(int n);

   @Accessor("levelRenderer")
   LevelRenderer zenith$getWorldRenderer();

   @Accessor("levelRenderer")
   @Mutable
   void zenith$setWorldRenderer(LevelRenderer levelRenderer);
}
