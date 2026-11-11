package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.HealthUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(name = "DamageDirection", description = "module.damageDirection.description", category = ModuleCategory.RENDER)
public final class DamageDirectionModule extends Module {
   public static final DamageDirectionModule INSTANCE = new DamageDirectionModule();
   private final SliderSetting duration = new SliderSetting("module.damageDirection.duration", 1500, 250, 5000, 50, "module.damageDirection.duration.desc", "ms");
   private final SliderSetting size = new SliderSetting("module.damageDirection.size", 18, 8, 40, 1, "module.damageDirection.size.desc", "px");
   private final ColorSetting playerColor = new ColorSetting("module.damageDirection.playerColor", "module.damageDirection.playerColor.desc", new ZenithColor(255, 75, 75));
   private final ColorSetting environmentColor = new ColorSetting("module.damageDirection.environmentColor", "module.damageDirection.environmentColor.desc", new ZenithColor(255, 180, 65));
   private final Identifier arrowTexture = Zenith.method1501("textures/arrows.png");
   private ClientLevel trackedLevel;
   private LocalPlayer trackedPlayer;
   private float previousHealth;
   private DamageSource consumedSource;
   private Vec3 sourcePosition;
   private boolean fromPlayer;
   private long shownAt;
   private long pendingUntil;

   private DamageDirectionModule() {
   }

   @Override
   public void onEnable() {
      resetTracking();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      resetTracking();
      super.onDisable();
   }

   private void resetTracking() {
      trackedLevel = mc.level;
      trackedPlayer = mc.player;
      previousHealth = mc.player == null ? 0 : mc.player.getHealth();
      consumedSource = mc.player == null ? null : mc.player.getLastDamageSource();
      sourcePosition = null;
      pendingUntil = 0;
      shownAt = 0;
   }

   private boolean hasPlayer() {
      if (trackedLevel != mc.level || trackedPlayer != mc.player) resetTracking();
      return mc.player != null && mc.level != null;
   }

   @EventTarget
   public void onHealthUpdate(HealthUpdateEvent event) {
      if (!hasPlayer()) return;
      float health = mc.player.getHealth();
      if (health < previousHealth) {
         // HealthUpdateEvent вызывается после setHealth; damage-пакет может прийти чуть позже.
         pendingUntil = System.currentTimeMillis() + 150;
         resolvePendingDamage();
      }
      previousHealth = health;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (hasPlayer()) resolvePendingDamage();
   }

   private void resolvePendingDamage() {
      if (pendingUntil == 0) return;
      long now = System.currentTimeMillis();
      // В 26.3 handleDamageEvent действительно сохраняет источник на клиенте.
      // Ограничиваем его возраст: ванильный getter без аргумента хранит старый удар 40 тиков.
      DamageSource source = mc.player.getLastDamageSource(2);
      boolean freshSource = source != null && source != consumedSource;
      if (!freshSource && now < pendingUntil) return;
      sourcePosition = freshSource ? source.sourcePositionRaw() : null;
      if (freshSource && sourcePosition == null) {
         // У стрелы directEntity уже у жертвы; направление берём на стрелявшего, если он загружен.
         var attacker = source.getEntity();
         sourcePosition = attacker != null && attacker != mc.player ? attacker.position() : source.getSourcePosition();
      }
      fromPlayer = freshSource && source.getEntity() instanceof Player;
      if (freshSource) consumedSource = source;
      if (sourcePosition != null && sourcePosition.subtract(mc.player.position()).horizontalDistanceSqr() < 0.0001) {
         sourcePosition = null;
      }
      // У падения, огня и неизвестного источника нет азимута: честное кольцо вместо выдуманной стрелки.
      shownAt = now;
      pendingUntil = 0;
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      if (!hasPlayer()) return;
      resolvePendingDamage();
      long elapsed = System.currentTimeMillis() - shownAt;
      if (shownAt == 0 || elapsed >= duration.getValue3() || mc.gui.hud.isHidden()) return;
      float alpha = Math.max(0, 1.0F - elapsed / duration.getValue3());
      ZenithColor tint = (fromPlayer ? playerColor : environmentColor).getZenithColor().method7294(alpha);
      float centerX = mc.getWindow().getGuiScaledWidth() / 2.0F;
      float centerY = mc.getWindow().getGuiScaledHeight() / 2.0F;
      float arrowSize = size.getValue3();
      float radius = arrowSize + 16;
      var context = event.getCustomDrawContext();
      if (sourcePosition == null) {
         context.drawArcBorder(centerX - radius, centerY - radius, radius * 2, radius * 2, 2, 360, 0.5F, tint);
         return;
      }
      Vec3 offset = sourcePosition.subtract(mc.player.getPosition(event.getTickDelta()));
      float yaw = (float)-Math.toDegrees(Math.atan2(offset.x, offset.z));
      float relativeYaw = yaw - mc.gameRenderer.mainCamera().yRot();
      context.pushMatrix();
      context.pose().translate(centerX, centerY);
      context.pose().rotate((float)Math.toRadians(relativeYaw));
      context.drawTexture(arrowTexture, -arrowSize / 2, -radius - arrowSize / 2, arrowSize, arrowSize, tint);
      context.popMatrix();
   }
}
