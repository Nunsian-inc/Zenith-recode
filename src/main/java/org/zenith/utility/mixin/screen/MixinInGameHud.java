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
import org.zenith.utility.game.Util4;
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
   public void onRenderStatusEffectOverlay(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      if (InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6828()) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("TAIL")}
   )
   public void onRender(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      CustomDrawContext var4 = new CustomDrawContext(var1);
      var1.pose().pushMatrix();

      try {
         EventManager.call(new DrawEvent(var4, var2.getGameTimeDeltaPartialTick(false)));
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      var1.pose().popMatrix();
   }

   @Inject(
      method = {"extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void removeVanillaCrosshair(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      if (Util4.method1287()) {
         var3.cancel();
      } else {
         try {
            CrosshairModule var4 = CrosshairModule.INSTANCE;
            if (var4.isEnabled()) {
               var1.pose().translate(0.0F, 0.0F);
               var3.cancel();
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }
   }

   @Inject(
      method = {"extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderMainHud(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      if (MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule var4 = InterfaceModule.INSTANCE;
         if (var4.isEnabled() && var4.method6812()) {
            var3.cancel();
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
   private void zenith$skipExperienceLevel(GuiGraphicsExtractor var1, Font var2, int var3) {
      InterfaceModule var4 = InterfaceModule.INSTANCE;
      if (MinecraftAccess.mc.gameMode.getPlayerMode() == GameType.SPECTATOR || !var4.isEnabled() || !var4.method6812()) {
         ContextualBar.extractExperienceLevel(var1, var2, var3);
      }
   }

   @Inject(
      method = {"extractTabList(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void inject(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      InterfaceModule var4 = InterfaceModule.INSTANCE;
      if (var4.isEnabled() && var4.method6813()) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"extractOverlayMessage(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectRenderOverlayMessage(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      if (MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule var4 = InterfaceModule.INSTANCE;
         if (var4.isEnabled() && var4.method6812()) {
            var3.cancel();
         }
      }
   }

   @Inject(
      method = {"extractScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectRenderScoreboardSidebar(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      InterfaceModule var4 = InterfaceModule.INSTANCE;
      if (NoRenderModule.INSTANCE.method4528() || var4.isEnabled() && var4.method6811()) {
         var3.cancel();
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
   private Component zenith$modifyScoreboardName(Component var1) {
      Component var2 = ScoreboardParser.method7076(var1);
      if (var2 == null) {
         return Component.empty();
      } else {
         if (NameProtectModule.INSTANCE.isEnabled() && MinecraftAccess.mc.player != null) {
            String var3 = MinecraftAccess.mc.player.getScoreboardName();
            Component var4 = NameProtectModule.INSTANCE.getText();
            String var5 = var2.getString();
            boolean var6 = var5.contains(var3);
            String var7 = null;
            if (!var6 && var4 != null) {
               if (var5.contains("Группа:")) {
                  var7 = "Группа:";
               } else if (var5.contains("Ранг:")) {
                  var7 = "Ранг:";
               }
            }

            if (var6 || var7 != null) {
               String var8 = NameProtectModule.method5141();
               this.zenith$validateProtectedRows(var3, var8, var4);
               Object var9 = this.zenith$protectedRows.get(var2);
               if (var9 == null) {
                  var9 = var6 ? TextUtils.method2110(var2, var3, var8) : TextUtils.method2114(var2, var7, var4);
                  if (this.zenith$protectedRows.size() >= 32) {
                     this.zenith$protectedRows.clear();
                  }

                  this.zenith$protectedRows.put(var2, (Component)var9);
               }

               return (Component)var9;
            }
         }

         return var2;
      }
   }

   @Unique
   private void zenith$validateProtectedRows(String var1, String var2, Component var3) {
      Language var4 = Language.getInstance();
      if (!var1.equals(this.zenith$protectedRowsSelf)
         || !Objects.equals(var2, this.zenith$protectedRowsName)
         || var3 != this.zenith$protectedRowsPrefix
         || var4 != this.zenith$protectedRowsLanguage) {
         this.zenith$protectedRows.clear();
         this.zenith$protectedRowsSelf = var1;
         this.zenith$protectedRowsName = var2;
         this.zenith$protectedRowsPrefix = var3;
         this.zenith$protectedRowsLanguage = var4;
      }
   }

   @Inject(
      method = {"extractPlayerHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$saturation(GuiGraphicsExtractor var1, CallbackInfo var2) {
      if (MinecraftAccess.mc.player != null
         && MinecraftAccess.mc.gameMode != null
         && MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule var3 = InterfaceModule.INSTANCE;
         if (!var3.isEnabled() || !var3.method6812()) {
            int var4 = var1.guiWidth() / 2 + 91;
            int var5 = var1.guiHeight() - 39;
            StatusBarColors.method1755(var1, MinecraftAccess.mc.player, var5, var4);
         }
      }
   }

   @ModifyVariable(
      method = {"extractPlayerHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"},
      at = @At("STORE"),
      ordinal = 3
   )
   private int modifyM(int var1, GuiGraphicsExtractor var2) {
      if (MinecraftAccess.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         InterfaceModule var3 = InterfaceModule.INSTANCE;
         if (var3.isEnabled() && var3.method6812()) {
            return var2.guiWidth() / 2 + 90 + 36;
         }
      }

      return var1;
   }
}
