package org.zenith.utility.mixin.screen;

import com.darkmagician6.eventapi.EventManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.render.CrosshairModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.NoRenderModule;
import org.zenith.utility.game.StatusBarColors;
import org.zenith.utility.game.ScriptRenderOverrides;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

// 26.2 split the in-game HUD out of Gui into Hud, and render* became extract*
@Mixin({net.minecraft.client.gui.Hud.class})
public abstract class MixinInGameHud {
   @Unique
   private final Map<Component, Component> zenith$protectedRows = new HashMap<>();
   @Unique
   private String zenith$protectedRowsSelf;
   @Unique
   private String zenith$protectedRowsName;
   @Unique
   private Component zenith$protectedRowsPrefix;
   @Unique
   private Language zenith$protectedRowsLanguage;

   @Inject(
      method = {"extractEffects(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onRenderStatusEffectOverlay(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      if (InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6828()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("TAIL")}
   )
   public void onRender(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      CustomDrawContext customDrawContext = new CustomDrawContext(context);
      context.pose().pushMatrix();

      try {
         EventManager.call(new DrawEvent(customDrawContext, deltaTracker.getGameTimeDeltaPartialTick(false)));
      } catch (Exception e) {
         e.printStackTrace();
      }

      context.pose().popMatrix();
   }

   @Inject(
      method = {"extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void removeVanillaCrosshair(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      if (ScriptRenderOverrides.isImGuiOpen()) {
         callbackInfo.cancel();
      } else {
         try {
            CrosshairModule crosshairModule = CrosshairModule.INSTANCE;
            if (crosshairModule.isEnabled()) {
               guiGraphicsExtractor.pose().translate(0.0F, 0.0F);
               callbackInfo.cancel();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Inject(
      method = {"extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderMainHud(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      if (MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
         if (interfaceModule.isEnabled() && interfaceModule.method6812()) {
            callbackInfo.cancel();
         }
      }
   }

   @Redirect(
      method = {"extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"
      )
   )
   private void zenith$skipExperienceLevel(GuiGraphicsExtractor graphics, Font font, int experienceLevel) {
      InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
      if (MinecraftAccess.mc.gameMode.getPlayerMode() == GameType.SPECTATOR || !interfaceModule.isEnabled() || !interfaceModule.method6812()) {
         ContextualBar.extractExperienceLevel(graphics, font, experienceLevel);
      }
   }

   @Inject(
      method = {"extractTabList(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void inject(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
      if (interfaceModule.isEnabled() && interfaceModule.method6813()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractOverlayMessage(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectRenderOverlayMessage(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      if (MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
         if (interfaceModule.isEnabled() && interfaceModule.method6812()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"extractScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectRenderScoreboardSidebar(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
      if (NoRenderModule.INSTANCE.method4528() || interfaceModule.isEnabled() && interfaceModule.method6811()) {
         callbackInfo.cancel();
      }
   }

   @ModifyArg(
      method = {"displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"
      ),
      index = 1
   )
   private Component zenith$modifyScoreboardName(Component header) {
      Component name = ScoreboardParser.method7076(header);
      if (name == null) {
         return Component.empty();
      } else {
         if (NameProtectModule.INSTANCE.isEnabled() && MinecraftAccess.mc.player != null) {
            String scoreboardName = MinecraftAccess.mc.player.getScoreboardName();
            Component text = NameProtectModule.INSTANCE.getText();
            String string = name.getString();
            boolean contains = string.contains(scoreboardName);
            String str = null;
            if (!contains && text != null) {
               if (string.contains("Группа:")) {
                  str = "Группа:";
               } else if (string.contains("Ранг:")) {
                  str = "Ранг:";
               }
            }

            if (contains || str != null) {
               String zenith$protectedRowsName = NameProtectModule.method5141();
               this.zenith$validateProtectedRows(scoreboardName, zenith$protectedRowsName, text);
               Object zenith$protectedRow = this.zenith$protectedRows.get(name);
               if (zenith$protectedRow == null) {
                  zenith$protectedRow = contains ? TextUtils.method2110(name, scoreboardName, zenith$protectedRowsName) : TextUtils.method2114(name, str, text);
                  if (this.zenith$protectedRows.size() >= 32) {
                     this.zenith$protectedRows.clear();
                  }

                  this.zenith$protectedRows.put(name, (Component)zenith$protectedRow);
               }

               return (Component)zenith$protectedRow;
            }
         }

         return name;
      }
   }

   @Unique
   private void zenith$validateProtectedRows(String zenith$protectedRowsSelf, String zenith$protectedRowsName, Component zenith$protectedRowsPrefix) {
      Language zenith$protectedRowsLanguage = Language.getInstance();
      if (!zenith$protectedRowsSelf.equals(this.zenith$protectedRowsSelf)
         || !Objects.equals(zenith$protectedRowsName, this.zenith$protectedRowsName)
         || zenith$protectedRowsPrefix != this.zenith$protectedRowsPrefix
         || zenith$protectedRowsLanguage != this.zenith$protectedRowsLanguage) {
         this.zenith$protectedRows.clear();
         this.zenith$protectedRowsSelf = zenith$protectedRowsSelf;
         this.zenith$protectedRowsName = zenith$protectedRowsName;
         this.zenith$protectedRowsPrefix = zenith$protectedRowsPrefix;
         this.zenith$protectedRowsLanguage = zenith$protectedRowsLanguage;
      }
   }

   @Inject(
      method = {"extractPlayerHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$saturation(GuiGraphicsExtractor context, CallbackInfo callbackInfo) {
      if (MinecraftAccess.mc.player != null
         && MinecraftAccess.mc.gameMode != null
         && MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
         if (!interfaceModule.isEnabled() || !interfaceModule.method6812()) {
            int n = context.guiWidth() / 2 + 91;
            int n2 = context.guiHeight() - 39;
            StatusBarColors.method1755(context, MinecraftAccess.mc.player, n2, n);
         }
      }
   }

   @ModifyVariable(
      method = {"extractPlayerHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"},
      at = @At("STORE"),
      ordinal = 3
   )
   private int modifyM(int n, GuiGraphicsExtractor guiGraphicsExtractor) {
      if (MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule interfaceModule = InterfaceModule.INSTANCE;
         if (interfaceModule.isEnabled() && interfaceModule.method6812()) {
            return guiGraphicsExtractor.guiWidth() / 2 + 90 + 36;
         }
      }

      return n;
   }
}
