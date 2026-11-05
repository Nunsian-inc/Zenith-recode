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
import org.zenith.utility.misc.Util2;
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

   public boolean method4584(Entity var1) {
      if (!this.isEnabled() || mc.player == null || mc.level == null || var1 == null) {
         return false;
      } else if (this.method4597(var1) || this.method4598(var1)) {
         return false;
      } else {
         return !this.method4599(var1) ? false : this.throughWalls.isEnabled() || this.method4585(var1);
      }
   }

   private boolean method4585(Entity var1) {
      if (mc.level != null && mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null && mc.isSameThread()) {
         Vec3 var2 = mc.gameRenderer.mainCamera().position();
         long var3 = mc.level.getGameTime() << 32 ^ Float.floatToIntBits(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) & 4294967295L;
         if (var3 != this.longValue || var2.x != this.doubleValue || var2.y != this.doubleValue2 || var2.z != this.doubleValue3) {
            this.int2BooleanOpenHashMap.clear();
            this.longValue = var3;
            this.doubleValue = var2.x;
            this.doubleValue2 = var2.y;
            this.doubleValue3 = var2.z;
         }

         int var5 = var1.getId();
         if (this.int2BooleanOpenHashMap.containsKey(var5)) {
            return this.int2BooleanOpenHashMap.get(var5);
         } else {
            boolean var6 = this.method4600(var1);
            this.int2BooleanOpenHashMap.put(var5, var6);
            return var6;
         }
      } else {
         return this.method4600(var1);
      }
   }

   public boolean method4586() {
      return this.throughWalls.isEnabled();
   }

   public int method4587(Entity var1) {
      return method4589(this.method4595(var1)) & 16777215;
   }

   public int method4588() {
      int var1 = this.method4595(mc.player);
      return var1 & 0xFF000000 | method4589(var1) & 16777215;
   }

   private static int method4589(int var0) {
      int var1 = var0 >> 16 & 0xFF;
      int var2 = var0 >> 8 & 0xFF;
      int var3 = var0 & 0xFF;
      return Math.max(var1, Math.max(var2, var3)) >= 8 ? var0 : var0 & 0xFF000000 | 524288 | 2048 | 8;
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
         double var1 = System.currentTimeMillis() * 0.0013 % (Math.PI * 2);
         return 0.86F + 0.14F * (float)Math.sin(var1);
      }
   }

   private int method4595(Entity var1) {
      return switch (this.colorMode.getIndex()) {
         case 1 -> this.colorSetting.method1445();
         case 2 -> this.method4596(var1);
         case 3 -> Util2.method814(var1 == null ? 0 : var1.getId() * 150, 0.7F, 1.0F);
         default -> Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287();
      };
   }

   private int method4596(Entity var1) {
      if (var1 instanceof LivingEntity var2) {
         float var3 = Math.max(var2.getMaxHealth(), 1.0F);
         return Util2.method813(-50384, -13316008, Math.clamp(var2.getHealth() / var3, 0.0F, 1.0F));
      } else {
         return this.colorSetting.method1445();
      }
   }

   private boolean method4597(Entity var1) {
      if (var1.isRemoved()) {
         return true;
      } else {
         return this.ignoreSelf.isEnabled() && var1 == mc.player ? true : var1 == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson();
      }
   }

   private boolean method4598(Entity var1) {
      float var2 = this.maxDistance.getValue3();
      return mc.player.distanceToSqr(var1) > var2 * var2;
   }

   private boolean method4599(Entity var1) {
      if (var1 instanceof Player var2) {
         return UseItemUtils.method6529(var2) == 0.0F ? this.targets.method5227(1) : this.targets.method5227(0);
      } else if (var1 instanceof EndCrystal) {
         return this.targets.method5227(5);
      } else if (var1 instanceof ItemEntity) {
         return this.targets.method5227(4);
      } else if (var1 instanceof Monster) {
         return this.targets.method5227(2);
      } else {
         return var1 instanceof Animal ? this.targets.method5227(3) : var1 instanceof LivingEntity && this.targets.method5227(2);
      }
   }

   private boolean method4600(Entity var1) {
      if (mc.level != null && mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null) {
         Vec3 var2 = mc.gameRenderer.mainCamera().position();
         AABB var3 = var1.getBoundingBox();
         Vec3 var4 = var3.getCenter();
         Vec3 var5 = new Vec3(var4.x, Math.min(var3.maxY - 0.05, var1.getEyeY()), var4.z);
         Vec3 var6 = new Vec3(var4.x, var3.minY + 0.1, var4.z);
         return this.method4601(var2, var4) || this.method4601(var2, var5) || this.method4601(var2, var6);
      } else {
         return true;
      }
   }

   private boolean method4601(Vec3 var1, Vec3 var2) {
      BlockHitResult var3 = mc.level.clip(new ClipContext(var1, var2, Block.COLLIDER, Fluid.NONE, mc.player));
      return var3.getType() == Type.MISS || var3.getLocation().distanceToSqr(var1) + 1.0E-4 >= var2.distanceToSqr(var1);
   }
}
