package org.zenith.client.modules.impl.render;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ArgbColors;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "ShaderESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.shaderesp.description"
)
public final class ShaderESPModule extends Module {
   public static final ShaderESPModule INSTANCE = new ShaderESPModule();
   private static final int CONST_intValue = -50384;
   private static final int CONST_intValue2 = -13316008;
   private static final int CONST_intValue3 = 8;
   private static final int CONST_intValue4 = 0;
   private static final int CONST_intValue5 = 1;
   private static final int CONST_intValue6 = 2;
   private static final int CONST_intValue7 = 3;
   private static final int CONST_intValue8 = 4;
   private static final int CONST_intValue9 = 5;
   private final MultiSelectSetting targets = MultiSelectSetting.method5224(
      "module.shaderESP.targets",
      "module.shaderESP.targets.desc",
      List.of(
         "module.shaderESP.players",
         "module.shaderESP.noArmor",
         "module.shaderESP.hostiles",
         "module.shaderESP.passives",
         "module.shaderESP.items",
         "module.shaderESP.crystals"
      )
   );
   private final BooleanSetting ignoreSelf = new BooleanSetting("module.shaderESP.ignoreSelf", true);
   private final SliderSetting maxDistance = new SliderSetting(
      "module.shaderESP.maxDistance", 90.0F, 6.0F, 240.0F, 1.0F, "module.shaderESP.maxDistance.desc", "m"
   );
   private final BooleanSetting throughWalls = new BooleanSetting("module.shaderESP.throughWalls", true);
   private final ModeSetting colorMode = new ModeSetting(
      "module.shaderESP.colorMode",
      "module.shaderESP.colorMode.desc",
      "module.shaderESP.sync",
      "module.shaderESP.custom",
      "module.shaderESP.health",
      "module.shaderESP.rainbow"
   );
   private final ColorSetting colorSetting = new ColorSetting("module.shaderESP.color", new ZenithColor(139, 92, 246), () -> this.colorMode.isSelected(1));
   private final SliderSetting outlineWidth = new SliderSetting(
      "module.shaderESP.outlineWidth", 2.4F, 0.0F, 8.0F, 0.2F, "module.shaderESP.outlineWidth.desc", "px"
   );
   private final SliderSetting glowRadius = new SliderSetting("module.shaderESP.glowRadius", 14.0F, 0.0F, 48.0F, 1.0F, "module.shaderESP.glowRadius.desc", "px");
   private final SliderSetting glowStrength = new SliderSetting(
      "module.shaderESP.glowStrength", 1.15F, 0.0F, 3.0F, 0.05F, "module.shaderESP.glowStrength.desc", "x"
   );
   private final SliderSetting fillOpacity = new SliderSetting(
      "module.shaderESP.fillOpacity", 0.35F, 0.0F, 1.0F, 0.05F, "module.shaderESP.fillOpacity.desc", "x"
   );
   private final BooleanSetting pulse = new BooleanSetting("module.shaderESP.pulse", true);
   private final Int2BooleanOpenHashMap int2BooleanOpenHashMap = new Int2BooleanOpenHashMap();
   private long longValue = Long.MIN_VALUE;
   private double doubleValue = Double.NaN;
   private double doubleValue2 = Double.NaN;
   private double doubleValue3 = Double.NaN;

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.SHADER;
   }

   private ShaderESPModule() {
   }

   public boolean method4584(Entity entity) {
      if (!this.isEnabled() || mc.player == null || mc.level == null || entity == null) {
         return false;
      } else if (this.method4597(entity) || this.method4598(entity)) {
         return false;
      } else {
         return !this.method4599(entity) ? false : this.throughWalls.isEnabled() || this.method4585(entity);
      }
   }

   private boolean method4585(Entity entity) {
      if (mc.level != null && mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null && mc.isSameThread()) {
         Vec3 position = mc.gameRenderer.mainCamera().position();
         long longValue = mc.level.getGameTime() << 32 ^ Float.floatToIntBits(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) & 4294967295L;
         if (longValue != this.longValue || position.x != this.doubleValue || position.y != this.doubleValue2 || position.z != this.doubleValue3) {
            this.int2BooleanOpenHashMap.clear();
            this.longValue = longValue;
            this.doubleValue = position.x;
            this.doubleValue2 = position.y;
            this.doubleValue3 = position.z;
         }

         int id = entity.getId();
         if (this.int2BooleanOpenHashMap.containsKey(id)) {
            return this.int2BooleanOpenHashMap.get(id);
         } else {
            boolean flag = this.method4600(entity);
            this.int2BooleanOpenHashMap.put(id, flag);
            return flag;
         }
      } else {
         return this.method4600(entity);
      }
   }

   public boolean method4586() {
      return this.throughWalls.isEnabled();
   }

   public int method4587(Entity entity) {
      return method4589(this.method4595(entity)) & 16777215;
   }

   public int method4588() {
      int n = this.method4595(mc.player);
      return n & 0xFF000000 | method4589(n) & 16777215;
   }

   private static int method4589(int n) {
      int n2 = n >> 16 & 0xFF;
      int n3 = n >> 8 & 0xFF;
      int n4 = n & 0xFF;
      return Math.max(n2, Math.max(n3, n4)) >= 8 ? n : n & 0xFF000000 | 524288 | 2048 | 8;
   }

   public float method4590() {
      return this.outlineWidth.getValue3();
   }

   public float method4591() {
      return this.glowStrength.getValue3();
   }

   public float method4592() {
      return this.fillOpacity.getValue3();
   }

   public float method4593() {
      return Math.max(this.glowRadius.getValue3(), this.outlineWidth.getValue3() + 1.0F);
   }

   public float method4594() {
      if (!this.pulse.isEnabled()) {
         return 1.0F;
      } else {
         double d = System.currentTimeMillis() * 0.0013 % (Math.PI * 2);
         return 0.86F + 0.14F * (float)Math.sin(d);
      }
   }

   private int method4595(Entity entity) {
      return switch (this.colorMode.getIndex()) {
         case 1 -> this.colorSetting.method1445();
         case 2 -> this.method4596(entity);
         case 3 -> ArgbColors.rainbow(entity == null ? 0 : entity.getId() * 150, 0.7F, 1.0F);
         default -> Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287();
      };
   }

   private int method4596(Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         float f = Math.max(livingEntity.getMaxHealth(), 1.0F);
         return ArgbColors.lerpArgb(-50384, -13316008, Math.clamp(livingEntity.getHealth() / f, 0.0F, 1.0F));
      } else {
         return this.colorSetting.method1445();
      }
   }

   private boolean method4597(Entity entity) {
      if (entity.isRemoved()) {
         return true;
      } else {
         return this.ignoreSelf.isEnabled() && entity == mc.player ? true : entity == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson();
      }
   }

   private boolean method4598(Entity entity) {
      float f = this.maxDistance.getValue3();
      return mc.player.distanceToSqr(entity) > f * f;
   }

   private boolean method4599(Entity entity) {
      if (entity instanceof Player player) {
         return UseItemUtils.method6529(player) == 0.0F ? this.targets.method5227(1) : this.targets.method5227(0);
      } else if (entity instanceof EndCrystal) {
         return this.targets.method5227(5);
      } else if (entity instanceof ItemEntity) {
         return this.targets.method5227(4);
      } else if (entity instanceof Monster) {
         return this.targets.method5227(2);
      } else {
         return entity instanceof Animal ? this.targets.method5227(3) : entity instanceof LivingEntity && this.targets.method5227(2);
      }
   }

   private boolean method4600(Entity entity) {
      if (mc.level != null && mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null) {
         Vec3 position = mc.gameRenderer.mainCamera().position();
         AABB boundingBox = entity.getBoundingBox();
         Vec3 center = boundingBox.getCenter();
         Vec3 vec = new Vec3(center.x, Math.min(boundingBox.maxY - 0.05, entity.getEyeY()), center.z);
         Vec3 vec2 = new Vec3(center.x, boundingBox.minY + 0.1, center.z);
         return this.method4601(position, center) || this.method4601(position, vec) || this.method4601(position, vec2);
      } else {
         return true;
      }
   }

   private boolean method4601(Vec3 position, Vec3 center) {
      BlockHitResult clip = mc.level.clip(new ClipContext(position, center, Block.COLLIDER, Fluid.NONE, mc.player));
      return clip.getType() == Type.MISS || clip.getLocation().distanceToSqr(position) + 1.0E-4 >= center.distanceToSqr(position);
   }
}
