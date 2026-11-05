package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.CameraPosEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.EmberField;
import org.zenith.utility.game.EmberShards;
import org.zenith.utility.render.Glowboost;

@ModuleInfo(
   name = "Crimson Embers",
   category = ModuleCategory.RENDER,
   description = "module.crimsonEmbers.description"
)
public final class CrimsonEmbersModule extends Module {
   public static final CrimsonEmbersModule INSTANCE = new CrimsonEmbersModule();
   private static final int CONST_intValue = 8;
   private static final double CONST_doubleValue = 24.0;
   private static final int CONST_intValue2 = -53700;
   private final SliderSetting density = new SliderSetting("module.crimsonEmbers.density", 1.0F, 0.2F, 3.0F, 0.1F, "module.crimsonEmbers.density.desc", "x");
   private final SliderSetting span = new SliderSetting("module.crimsonEmbers.span", 1.0F, 0.5F, 2.0F, 0.05F, "module.crimsonEmbers.span.desc", "x");
   private final SliderSetting glow = new SliderSetting("module.crimsonEmbers.glow", 1.0F, 0.0F, 2.0F, 0.05F, "module.crimsonEmbers.glow.desc", "x");
   private final ModeSetting color = new ModeSetting(
      "module.crimsonEmbers.color",
      "module.crimsonEmbers.color.desc",
      () -> true,
      "module.crimsonEmbers.color.crimson",
      "module.particles.sync",
      "module.particles.custom"
   );
   private final ColorSetting colorSetting = new ColorSetting(
      "module.crimsonEmbers.customColor", "module.crimsonEmbers.customColor.desc", new ZenithColor(255, 46, 60), () -> this.color.isSelected(2)
   );
   private final BooleanSetting shards = new BooleanSetting("module.crimsonEmbers.shards", "module.crimsonEmbers.shards.desc", true);
   private final SliderSetting volley = new SliderSetting(
      "module.crimsonEmbers.volley", 2.0F, 1.0F, 4.0F, 1.0F, "module.crimsonEmbers.volley.desc", "", this.shards::isEnabled
   );
   private final BooleanSetting shake = new BooleanSetting("module.crimsonEmbers.shake", "module.crimsonEmbers.shake.desc", true, this.shards::isEnabled);
   private final BooleanSetting sounds = new BooleanSetting("module.crimsonEmbers.sounds", "module.crimsonEmbers.sounds.desc", true, this.shards::isEnabled);
   private final EmberShards emberShards = new EmberShards();
   private final Glowboost glowboost = new Glowboost();
   private final Queue<LivingEntity> queue = new ConcurrentLinkedQueue<>();
   private final Random random = new Random();
   private final long longValue = System.nanoTime();
   private EmberField emberField;
   private ClientLevel clientWorld;
   private long longValue2;

   @Override
   public List<Setting> getSettings() {
      return List.of(this.density, this.span, this.glow, this.color, this.colorSetting, this.shards, this.volley, this.shake, this.sounds);
   }

   private CrimsonEmbersModule() {
      this.emberShards.setHandler((emberShard, y, z) -> this.method7765(emberShard, y, z, SoundEvents.TRIDENT_HIT, 0.5F, 1.35F));
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.reset();
   }

   private void reset() {
      this.emberField = null;
      this.emberShards.clear();
      this.queue.clear();
      this.longValue2 = 0L;
   }

   @EventTarget
   public void method7761(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.POST && this.shards.isEnabled()) {
         Entity entity = event.getEntity();
         if (entity instanceof LivingEntity livingEntity && entity != mc.player) {
            if (this.queue.size() < 8) {
               this.queue.offer(livingEntity);
            }
         }
      }
   }

   @EventTarget
   public void method7762(CameraPosEvent event) {
      if (this.shake.isEnabled()) {
         float floatValue = this.emberShards.getFloatValue();
         if (!(floatValue <= 0.001F)) {
            double d = (System.nanoTime() - this.longValue) / 1.0E9;
            double d2 = 0.075 * floatValue * floatValue;
            event.setVec3d(event.getVec3d().add(Math.sin(d * 61.0) * d2, Math.sin(d * 53.0 + 1.3) * d2, Math.sin(d * 67.0 + 2.1) * d2));
         }
      }
   }

   @EventTarget
   public void method7763(WorldRenderEvent event) {
      LocalPlayer player = mc.player;
      if (player != null && mc.level != null) {
         if (mc.level != this.clientWorld) {
            this.reset();
            this.clientWorld = mc.level;
         }

         long nanoTime = System.nanoTime();
         float f = this.longValue2 == 0L ? 0.0F : Math.min(0.1F, (float)(nanoTime - this.longValue2) / 1.0E9F);
         this.longValue2 = nanoTime;
         float floatValue = event.getFloatValue();
         Camera camera = mc.getEntityRenderDispatcher().camera;
         if (camera != null) {
            if (this.emberField == null) {
               this.emberField = new EmberField();
            }

            boolean flag = player.isAlive() && !player.isSpectator() && !player.isInvisible();
            if (flag) {
               this.emberField.method7018(player, floatValue, f, (nanoTime - this.longValue) / 1.0E9, this.span.getValue3());
               this.method7764(player, floatValue);
               this.emberField.method7019(this.emberShards, f, this.density.getValue3());
            } else {
               this.queue.clear();
            }

            this.emberShards.update(f, floatValue);
            this.glowboost.method3232(camera, Math.max(0.03F, this.glow.getValue3()));
            this.emberShards.method7587(this.glowboost, this.color());
            this.glowboost.method3253(event.getMatrixStack());
         }
      } else {
         this.reset();
      }
   }

   private void method7764(LocalPlayer player, float floatValue) {
      LivingEntity referent;
      while ((referent = this.queue.poll()) != null) {
         if (!referent.isRemoved()
            && !(referent.distanceToSqr(player) > 576.0)
            && this.emberField.method7020(this.emberShards, referent, Math.round(this.volley.getValue3()), floatValue)) {
            this.method7765(player.getX(), player.getY() + 1.4, player.getZ(), SoundEvents.TRIDENT_THROW.value(), 0.6F, 1.6F);
         }
      }
   }

   private int color() {
      if (this.color.isSelected(1)) {
         return Zenith.getInstance().getStyleManager().getClientColor(0).method7287() | 0xFF000000;
      } else {
         return this.color.isSelected(2) ? this.colorSetting.getZenithColor().method7287() | 0xFF000000 : -53700;
      }
   }

   private void method7765(double emberShard, double y, double z, SoundEvent sound, float volume, float f) {
      if (this.sounds.isEnabled() && mc.level != null) {
         mc.level.playLocalSound(emberShard, y, z, sound, SoundSource.PLAYERS, volume, f * (0.92F + this.random.nextFloat() * 0.16F), false);
      }
   }
}
