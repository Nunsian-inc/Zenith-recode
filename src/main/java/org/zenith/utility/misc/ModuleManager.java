package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AimBotModule;
import org.zenith.client.modules.impl.combat.AntiBotModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoExplosionModule;
import org.zenith.client.modules.impl.combat.AutoMaceModule;
import org.zenith.client.modules.impl.combat.AutoPotionModule;
import org.zenith.client.modules.impl.combat.AutoSwapModule;
import org.zenith.client.modules.impl.combat.AutoThrowModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.combat.AutoTrapModule;
import org.zenith.client.modules.impl.combat.BlinkModule;
import org.zenith.client.modules.impl.combat.BowAimBotModule;
import org.zenith.client.modules.impl.combat.CriticalsModule;
import org.zenith.client.modules.impl.combat.CrystalAuraModule;
import org.zenith.client.modules.impl.combat.FakeLagModule;
import org.zenith.client.modules.impl.combat.OffHandManagerModule;
import org.zenith.client.modules.impl.combat.PvpHelperModule;
import org.zenith.client.modules.impl.combat.ReachModule;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.modules.impl.combat.RotationRecorderModule;
import org.zenith.client.modules.impl.combat.TargetPearlModule;
import org.zenith.client.modules.impl.combat.TrapTpModule;
import org.zenith.client.modules.impl.combat.TridentAimbotModule;
import org.zenith.client.modules.impl.combat.SpearAuraModule;
import org.zenith.client.modules.impl.combat.TriggerBotModule;
import org.zenith.client.modules.impl.misc.ActionMapModule;
import org.zenith.client.modules.impl.misc.AntiAFKModule;
import org.zenith.client.modules.impl.misc.AntiCheatProbeModule;
import org.zenith.client.modules.impl.misc.AuctionScanModule;
import org.zenith.client.modules.impl.misc.AutoAcceptModule;
import org.zenith.client.modules.impl.misc.AutoAuthModule;
import org.zenith.client.modules.impl.misc.AutoCapchaModule;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.client.modules.impl.misc.AutoDuelsModule;
import org.zenith.client.modules.impl.misc.AutoInventoryModule;
import org.zenith.client.modules.impl.misc.AutoLeaveModule;
import org.zenith.client.modules.impl.misc.AutoPayModule;
import org.zenith.client.modules.impl.misc.AutoRespawnModule;
import org.zenith.client.modules.impl.misc.AutoFishModule;
import org.zenith.client.modules.impl.misc.AutoToolModule;
import org.zenith.client.modules.impl.misc.AutoUseModule;
import org.zenith.client.modules.impl.misc.AutoWebModule;
import org.zenith.client.modules.impl.misc.CameraTweaksModule;
import org.zenith.client.modules.impl.misc.ChestStealerModule;
import org.zenith.client.modules.impl.misc.ClickActionModule;
import org.zenith.client.modules.impl.misc.CommandProbeModule;
import org.zenith.client.modules.impl.misc.ContainerHelperModule;
import org.zenith.client.modules.impl.misc.CrystalOptimizerModule;
import org.zenith.client.modules.impl.misc.ElytraHelperModule;
import org.zenith.client.modules.impl.misc.EmotesModule;
import org.zenith.client.modules.impl.misc.EventTrackerModule;
import org.zenith.client.modules.impl.misc.FakePlayerModule;
import org.zenith.client.modules.impl.misc.FastBreakModule;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.modules.impl.misc.ItemDebugModule;
import org.zenith.client.modules.impl.misc.ItemScrollerModule;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.NoFriendDamageModule;
import org.zenith.client.modules.impl.misc.NoInteractModule;
import org.zenith.client.modules.impl.misc.OpenWalsModule;
import org.zenith.client.modules.impl.misc.PacketAuditModule;
import org.zenith.client.modules.impl.misc.PathTeleportModule;
import org.zenith.client.modules.impl.misc.PotionThrowerModule;
import org.zenith.client.modules.impl.misc.PvpSafeModule;
import org.zenith.client.modules.impl.misc.ResourcePackSpoofModule;
import org.zenith.client.modules.impl.misc.SelfFlagModule;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.client.modules.impl.misc.ServerProbeModule;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.modules.impl.misc.TapeMouseModule;
import org.zenith.client.modules.impl.misc.ViaFabricFixModule;
import org.zenith.client.modules.impl.movement.AirStuckModule;
import org.zenith.client.modules.impl.movement.AutoElytraArmorModule;
import org.zenith.client.modules.impl.movement.AutoSprintModule;
import org.zenith.client.modules.impl.movement.StepModule;
import org.zenith.client.modules.impl.movement.ScaffoldModule;
import org.zenith.client.modules.impl.movement.BoatHighJumpModule;
import org.zenith.client.modules.impl.movement.BoatLongJumpModule;
import org.zenith.client.modules.impl.movement.CastleFlyModule;
import org.zenith.client.modules.impl.movement.ElytraBoosterModule;
import org.zenith.client.modules.impl.movement.ElytraFlyModule;
import org.zenith.client.modules.impl.movement.ElytraMotionModule;
import org.zenith.client.modules.impl.movement.ElytraTargetModule;
import org.zenith.client.modules.impl.movement.GrimGlideModule;
import org.zenith.client.modules.impl.movement.GuiWalkModule;
import org.zenith.client.modules.impl.movement.NoDelayModule;
import org.zenith.client.modules.impl.movement.NoFallModule;
import org.zenith.client.modules.impl.movement.NoPushModule;
import org.zenith.client.modules.impl.movement.NoSlowModule;
import org.zenith.client.modules.impl.movement.NoSweetSlowModule;
import org.zenith.client.modules.impl.movement.NoWebModule;
import org.zenith.client.modules.impl.movement.ShulkerJumpModule;
import org.zenith.client.modules.impl.movement.SlimeFlightModule;
import org.zenith.client.modules.impl.movement.SpeedModule;
import org.zenith.client.modules.impl.movement.SpiderModule;
import org.zenith.client.modules.impl.movement.StrafeModule;
import org.zenith.client.modules.impl.movement.TimerModule;
import org.zenith.client.modules.impl.movement.VelocityModule;
import org.zenith.client.modules.impl.movement.WallBypassModule;
import org.zenith.client.modules.impl.movement.WaterRiseModule;
import org.zenith.client.modules.impl.player.AHHelperModule;
import org.zenith.client.modules.impl.player.AncientFarmModule;
import org.zenith.client.modules.impl.player.AppleFarmModule;
import org.zenith.client.modules.impl.player.AutoBrewingModule;
import org.zenith.client.modules.impl.player.AutoFarmRW;
import org.zenith.client.modules.impl.player.AutoLootModule;
import org.zenith.client.modules.impl.player.AutoMineModule;
import org.zenith.client.modules.impl.player.AutoWardenModule;
import org.zenith.client.modules.impl.player.AutoZamokModule;
import org.zenith.client.modules.impl.player.BaseFinderModule;
import org.zenith.client.modules.impl.player.BotModule;
import org.zenith.client.modules.impl.player.CropFarmerModule;
import org.zenith.client.modules.impl.player.EndLooterModule;
import org.zenith.client.modules.impl.player.TpKeyFinderModule;
import org.zenith.client.modules.impl.player.TpLootModule;
import org.zenith.client.modules.impl.player.WarpFarmModule;
import org.zenith.client.modules.impl.player.XrayBypassModule;
import org.zenith.client.modules.impl.render.AmbientParticlesModule;
import org.zenith.client.modules.impl.render.VoidESPModule;
import org.zenith.client.modules.impl.render.ChunkBordersModule;
import org.zenith.client.modules.impl.render.DamageDirectionModule;
import org.zenith.client.modules.impl.render.AntiInvisibleModule;
import org.zenith.client.modules.impl.render.ArrowsModule;
import org.zenith.client.modules.impl.render.BetterMinecraftModule;
import org.zenith.client.modules.impl.render.BlockESPModule;
import org.zenith.client.modules.impl.render.ChamsModule;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.modules.impl.render.CrimsonEmbersModule;
import org.zenith.client.modules.impl.render.CrosshairModule;
import org.zenith.client.modules.impl.render.ESPModule;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.client.modules.impl.render.FireWorkESPModule;
import org.zenith.client.modules.impl.render.HandsModule;
import org.zenith.client.modules.impl.render.HitParticlesModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.JumpCircleModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.modules.impl.render.NoRenderModule;
import org.zenith.client.modules.impl.render.OptimizationModule;
import org.zenith.client.modules.impl.render.ParticlesModule;
import org.zenith.client.modules.impl.render.PartyMarksModule;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.client.modules.impl.render.TargetESPModule;
import org.zenith.client.modules.impl.render.TotemParticlesModule;
import org.zenith.client.modules.impl.render.ViewArmorDurabilityModule;
import org.zenith.client.modules.impl.render.ViewModelModule;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;
import org.zenith.client.modules.impl.render.WorldTweaksModule;
import org.zenith.client.screens.nlgui.BindCapture;

public final class ModuleManager implements MinecraftAccess {
   private final List<Module> items = new CopyOnWriteArrayList<>();

   public ModuleManager() {
      this.init();
      EventManager.register(this);
   }

   private void init() {
      this.registerCombatModules();
      this.registerMovementModules();
      this.registerRenderModules();
      this.registerPlayerModules();
      this.registerMiscModules();
      InterfaceModule.INSTANCE.setToggled(true);
   }

   private void registerCombatModules() {
      if (Zenith.method1502()) {
         this.registerBuiltinModule(RotationRecorderModule.INSTANCE);
      }

      this.registerBuiltinModule(AimAssistModule.INSTANCE);
      this.registerBuiltinModule(AuraModule.INSTANCE);
      this.registerBuiltinModule(SpearAuraModule.INSTANCE);
      this.registerBuiltinModule(ReachModule.INSTANCE);
      this.registerBuiltinModule(AutoSwapModule.INSTANCE);
      this.registerBuiltinModule(TriggerBotModule.INSTANCE);
      this.registerBuiltinModule(OffHandManagerModule.INSTANCE);
      this.registerBuiltinModule(ElytraTargetModule.INSTANCE);
      this.registerBuiltinModule(CriticalsModule.INSTANCE);
      this.registerBuiltinModule(AutoMaceModule.INSTANCE);
      this.registerBuiltinModule(TargetPearlModule.INSTANCE);
      this.registerBuiltinModule(AutoTrapModule.INSTANCE);
      this.registerBuiltinModule(CrystalAuraModule.INSTANCE);
      this.registerBuiltinModule(AutoTotemModule.INSTANCE);
      this.registerBuiltinModule(AntiBotModule.INSTANCE);
      this.registerBuiltinModule(InventorySettingModule.INSTANCE);
      this.registerBuiltinModule(BlinkModule.INSTANCE);
      this.registerBuiltinModule(TrapTpModule.INSTANCE);
      this.registerBuiltinModule(FakeLagModule.INSTANCE);
      this.registerBuiltinModule(ReachV3Module.INSTANCE);
      this.registerBuiltinModule(AutoThrowModule.INSTANCE);
      this.registerBuiltinModule(AutoPotionModule.INSTANCE);
      this.registerBuiltinModule(PvpHelperModule.INSTANCE);
   }

   private void registerMovementModules() {
      this.registerBuiltinModule(StepModule.INSTANCE);
      this.registerBuiltinModule(ScaffoldModule.INSTANCE);
      this.registerBuiltinModule(AutoSprintModule.INSTANCE);
      this.registerBuiltinModule(ElytraBoosterModule.INSTANCE);
      this.registerBuiltinModule(CastleFlyModule.INSTANCE);
      this.registerBuiltinModule(GrimGlideModule.INSTANCE);
      this.registerBuiltinModule(SlimeFlightModule.INSTANCE);
      this.registerBuiltinModule(ElytraFlyModule.INSTANCE);
      this.registerBuiltinModule(VelocityModule.INSTANCE);
      this.registerBuiltinModule(NoWebModule.INSTANCE);
      this.registerBuiltinModule(GuiWalkModule.INSTANCE);
      this.registerBuiltinModule(NoSlowModule.INSTANCE);
      this.registerBuiltinModule(NoFallModule.INSTANCE);
      this.registerBuiltinModule(WaterRiseModule.INSTANCE);
      this.registerBuiltinModule(SpiderModule.INSTANCE);
      this.registerBuiltinModule(SpeedModule.INSTANCE);
      this.registerBuiltinModule(TimerModule.INSTANCE);
      this.registerBuiltinModule(ElytraMotionModule.INSTANCE);
      this.registerBuiltinModule(AutoElytraArmorModule.INSTANCE);
      this.registerBuiltinModule(BoatHighJumpModule.INSTANCE);
      this.registerBuiltinModule(BoatLongJumpModule.INSTANCE);
      this.registerBuiltinModule(ShulkerJumpModule.INSTANCE);
      this.registerBuiltinModule(StrafeModule.INSTANCE);
      this.registerBuiltinModule(AirStuckModule.INSTANCE);
      this.registerBuiltinModule(WallBypassModule.INSTANCE);
   }

   private void registerRenderModules() {
      this.registerBuiltinModule(InterfaceModule.INSTANCE);
      this.registerBuiltinModule(BetterMinecraftModule.INSTANCE);
      this.registerBuiltinModule(OptimizationModule.INSTANCE);
      this.registerBuiltinModule(AntiInvisibleModule.INSTANCE);
      this.registerBuiltinModule(ArrowsModule.INSTANCE);
      this.registerBuiltinModule(MenuModule.INSTANCE);
      this.registerBuiltinModule(NoRenderModule.INSTANCE);
      this.registerBuiltinModule(PredictionsModule.INSTANCE);
      this.registerBuiltinModule(ESPModule.INSTANCE);
      this.registerBuiltinModule(BlockESPModule.INSTANCE);
      this.registerBuiltinModule(HandsModule.INSTANCE);
      this.registerBuiltinModule(SwingAnimationModule.INSTANCE);
      this.registerBuiltinModule(CrosshairModule.INSTANCE);
      this.registerBuiltinModule(ViewModelModule.INSTANCE);
      this.registerBuiltinModule(WorldTweaksModule.INSTANCE);
      this.registerBuiltinModule(WeatherModule.INSTANCE);
      this.registerBuiltinModule(WorldModule.INSTANCE);
      this.registerBuiltinModule(ChamsModule.INSTANCE);
      this.registerBuiltinModule(EntityESPModule.INSTANCE);
      this.registerBuiltinModule(CloudWaypointsModule.INSTANCE);
      this.registerBuiltinModule(PartyMarksModule.INSTANCE);
      this.registerBuiltinModule(TargetESPModule.INSTANCE);
      this.registerBuiltinModule(CrimsonEmbersModule.INSTANCE);
      this.registerBuiltinModule(ShaderHandModule.INSTANCE);
      this.registerBuiltinModule(AutoExplosionModule.INSTANCE);
      this.registerBuiltinModule(VoidESPModule.INSTANCE);
      this.registerBuiltinModule(ChunkBordersModule.INSTANCE);
      this.registerBuiltinModule(DamageDirectionModule.INSTANCE);
      this.registerBuiltinModule(JumpCircleModule.INSTANCE);
      this.registerBuiltinModule(AmbientParticlesModule.INSTANCE);
      this.registerBuiltinModule(HitParticlesModule.INSTANCE);
      this.registerBuiltinModule(TotemParticlesModule.INSTANCE);
      this.registerBuiltinModule(ParticlesModule.INSTANCE);
      this.registerBuiltinModule(ViewArmorDurabilityModule.INSTANCE);
      this.registerBuiltinModule(FireWorkESPModule.INSTANCE);
   }

   private void registerPlayerModules() {
      this.registerBuiltinModule(AutoToolModule.INSTANCE);
      this.registerBuiltinModule(NoDelayModule.INSTANCE);
      this.registerBuiltinModule(NoSweetSlowModule.INSTANCE);
      this.registerBuiltinModule(NoPushModule.INSTANCE);
      this.registerBuiltinModule(OpenWalsModule.INSTANCE);
      this.registerBuiltinModule(BaseFinderModule.INSTANCE);
      this.registerBuiltinModule(AppleFarmModule.INSTANCE);
      this.registerBuiltinModule(AncientFarmModule.INSTANCE);
      this.registerBuiltinModule(FastBreakModule.INSTANCE);
      this.registerBuiltinModule(AutoLootModule.INSTANCE);
      this.registerBuiltinModule(AutoMineModule.INSTANCE);
      this.registerBuiltinModule(CropFarmerModule.INSTANCE);
      this.registerBuiltinModule(AutoZamokModule.INSTANCE);
      this.registerBuiltinModule(AutoBrewingModule.INSTANCE);
      this.registerBuiltinModule(BotModule.INSTANCE);
      this.registerBuiltinModule(AutoWardenModule.INSTANCE);
      this.registerBuiltinModule(EndLooterModule.INSTANCE);
      this.registerBuiltinModule(WarpFarmModule.INSTANCE);
      this.registerBuiltinModule(AutoFarmRW.INSTANCE);
      this.registerBuiltinModule(TpLootModule.INSTANCE);
      this.registerBuiltinModule(TpKeyFinderModule.INSTANCE);
   }

   private void registerMiscModules() {
      this.registerBuiltinModule(ResourcePackSpoofModule.INSTANCE);
      this.registerBuiltinModule(ViaFabricFixModule.INSTANCE);
      this.registerBuiltinModule(CrystalOptimizerModule.INSTANCE);
      this.registerBuiltinModule(EmotesModule.INSTANCE);
      this.registerBuiltinModule(AimBotModule.INSTANCE);
      this.registerBuiltinModule(BowAimBotModule.INSTANCE);
      this.registerBuiltinModule(TridentAimbotModule.INSTANCE);
      this.registerBuiltinModule(AuctionScanModule.INSTANCE);
      if (Zenith.method1502()) {
         this.registerBuiltinModule(ItemDebugModule.INSTANCE);
         this.registerBuiltinModule(ServerProbeModule.INSTANCE);
         this.registerBuiltinModule(PacketAuditModule.INSTANCE);
         this.registerBuiltinModule(CommandProbeModule.INSTANCE);
         this.registerBuiltinModule(AntiCheatProbeModule.INSTANCE);
         this.registerBuiltinModule(ActionMapModule.INSTANCE);
         this.registerBuiltinModule(SelfFlagModule.INSTANCE);
         this.registerBuiltinModule(FakePlayerModule.INSTANCE);
         this.registerBuiltinModule(PathTeleportModule.INSTANCE);
      }

      this.registerBuiltinModule(ShaderESPModule.INSTANCE);
      this.registerBuiltinModule(ShulkerPreviewModule.INSTANCE);
      this.registerBuiltinModule(ServerHelperModule.INSTANCE);
      this.registerBuiltinModule(PotionThrowerModule.INSTANCE);
      this.registerBuiltinModule(ElytraHelperModule.INSTANCE);
      this.registerBuiltinModule(ItemScrollerModule.INSTANCE);
      this.registerBuiltinModule(ClickActionModule.INSTANCE);
      this.registerBuiltinModule(FreeCamModule.INSTANCE);
      this.registerBuiltinModule(PvpSafeModule.INSTANCE);
      this.registerBuiltinModule(CameraTweaksModule.INSTANCE);
      this.registerBuiltinModule(AutoAuthModule.INSTANCE);
      this.registerBuiltinModule(AutoCapchaModule.INSTANCE);
      this.registerBuiltinModule(AutoPayModule.INSTANCE);
      this.registerBuiltinModule(AutoDuelsModule.INSTANCE);
      this.registerBuiltinModule(AntiAFKModule.INSTANCE);
      this.registerBuiltinModule(AutoLeaveModule.INSTANCE);
      this.registerBuiltinModule(AHHelperModule.INSTANCE);
      this.registerBuiltinModule(AutoInventoryModule.INSTANCE);
      this.registerBuiltinModule(AutoCraftModule.INSTANCE);
      this.registerBuiltinModule(ContainerHelperModule.INSTANCE);
      this.registerBuiltinModule(NoInteractModule.INSTANCE);
      this.registerBuiltinModule(NoFriendDamageModule.INSTANCE);
      this.registerBuiltinModule(ChestStealerModule.INSTANCE);
      this.registerBuiltinModule(AutoAcceptModule.INSTANCE);
      this.registerBuiltinModule(EventTrackerModule.INSTANCE);
      this.registerBuiltinModule(AutoUseModule.INSTANCE);
      this.registerBuiltinModule(TapeMouseModule.INSTANCE);
      this.registerBuiltinModule(AutoRespawnModule.INSTANCE);
      this.registerBuiltinModule(AutoFishModule.INSTANCE);
      this.registerBuiltinModule(NameProtectModule.INSTANCE);
      this.registerBuiltinModule(AutoWebModule.INSTANCE);
      this.registerBuiltinModule(XrayBypassModule.INSTANCE);
      this.registerBuiltinModule(StreamerModeModule.INSTANCE);
   }

   private void registerBuiltinModule(Module moduleValue) {
      this.registerModule(moduleValue);
   }

   public void registerModule(Module moduleValue) {
      Objects.requireNonNull(moduleValue, "module");
      if (this.items.stream().anyMatch(var1x -> var1x.getId().equals(moduleValue.getId()))) {
         throw new IllegalArgumentException("Duplicate module id: " + moduleValue.getId());
      } else {
         this.items.add(moduleValue);
      }
   }

   public boolean unregisterModule(Module moduleValue) {
      if (moduleValue == null) {
         return false;
      } else {
         moduleValue.setToggled(false);
         return this.items.remove(moduleValue);
      }
   }

   public Module findByName(String string) {
      return this.items.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
   }

   public Set<Module> getEnabledModules() {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (Module moduleValue : this.items) {
         if (moduleValue.isEnabled()) {
            linkedHashSet.add(moduleValue);
         }
      }

      return linkedHashSet;
   }

   @EventTarget
   public void onKey(KeyEvent event) {
      if (event.getIntValue() == 1) {
         if (!BindCapture.isActive() || mc.gui.screen() == null || mc.gui.screen() != Zenith.getInstance().getNLMenuScreen()) {
            if (KeyName.method6703(MenuModule.INSTANCE.getKeyCode(), MenuModule.INSTANCE.getKeyModifiers(), event.getKeyCode(), event.getModifiers())) {
               MenuModule.INSTANCE.toggle();
            } else if (mc.gui.screen() == null) {
               for (Module moduleValue : this.items) {
                  if (KeyName.method6703(moduleValue.getKeyCode(), moduleValue.getKeyModifiers(), event.getKeyCode(), event.getModifiers())) {
                     moduleValue.toggle();
                  }
               }
            }
         }
      }
   }

   @Generated
   public List<Module> getItems() {
      return this.items;
   }
}
