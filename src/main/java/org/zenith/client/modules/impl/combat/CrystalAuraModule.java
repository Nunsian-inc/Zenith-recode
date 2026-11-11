package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.InteractionInputEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.config.CombatLogger;
import org.zenith.utility.game.CombatDamageCalculator;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryActionState;
import org.zenith.utility.network.PlacementQueue;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "CrystalAura",
   description = "module.crystalaura.description",
   category = ModuleCategory.COMBAT
)
public final class CrystalAuraModule extends Module {
   public static final CrystalAuraModule INSTANCE = new CrystalAuraModule();
   private static final double[] CONST_doubleValues = new double[]{0.5, 0.3, 0.7, 0.15, 0.85};
   private static final double[] CONST_doubleValues2 = new double[]{0.5, 0.2, 0.8};
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final int CONST_intValue = 3;
   private static final int CONST_intValue2 = 12;
   private static final int CONST_intValue3 = 10;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 10;
   private static final int CONST_intValue6 = 6;
   private static final int CONST_intValue7 = 3;
   private static final double CONST_doubleValue = 0.42;
   private static final int CONST_intValue8 = 18;
   private static final float CONST_floatValue = 0.8F;
   private static final float CONST_floatValue2 = 4.0F;
   private static final float CONST_floatValue3 = 4.0F;
   private static final int CONST_intValue9 = 10;
   private static final int CONST_intValue10 = 10;
   private static final int CONST_intValue11 = 5;
   private static final int CONST_intValue12 = 4;
   private static final int CONST_intValue13 = 60;
   private static final float CONST_floatValue4 = 3.0F;
   private static final int CONST_intValue14 = 40;
   private static final int CONST_intValue15 = 4;
   private static final int CONST_intValue16 = 4;
   private static final float CONST_floatValue5 = 3.0F;
   private static final int CONST_intValue17 = 12;
   private static final double CONST_doubleValue2 = 3.0;
   private static final double CONST_doubleValue3 = 2.3;
   private static final int CONST_intValue18 = 3;
   private static final int CONST_intValue19 = 10;
   private static final int CONST_intValue20 = 20;
   private static final float[][] CONST_floatValues = new float[][]{{0.0F, 0.0F}, {0.25F, 0.0F}, {-0.25F, 0.0F}, {0.0F, 0.25F}, {0.0F, -0.25F}};
   private static final long CONST_longValue = 400L;
   private static final float CONST_floatValue6 = 0.03F;
   private final ModeSetting preset = new ModeSetting(
      "module.crystalAura.preset",
      "module.crystalAura.preset.desc",
      "module.crystalAura.presetCustom",
      "Grim",
      "ReallyWorld",
      "DonutSMP",
      "module.crystalAura.presetNone"
   ) {
      @Override
      public void set(String anObject) {
         super.set(this.getValues().stream().anyMatch(var1x -> var1x.getKey().equals(anObject)) ? anObject : "Grim");
      }
   };
   private final SliderSetting targetRange = new SliderSetting(
      "module.crystalAura.targetRange", 10.0F, 4.0F, 16.0F, 0.5F, "module.crystalAura.targetRange.desc", "b"
   );
   private final SliderSetting minDamage = new SliderSetting("module.crystalAura.minDamage", 6.0F, 1.0F, 20.0F, 0.5F, "module.crystalAura.minDamage.desc", "hp");
   private final SliderSetting maxSelfDamage = new SliderSetting(
      "module.crystalAura.maxSelfDamage", 8.0F, 0.0F, 20.0F, 0.5F, "module.crystalAura.maxSelfDamage.desc", "hp"
   );
   private final SliderSetting faceplace = new SliderSetting("module.crystalAura.faceplace", 8.0F, 0.0F, 36.0F, 1.0F, "module.crystalAura.faceplace.desc", "hp");
   private final SliderSetting armorBreak = new SliderSetting(
      "module.crystalAura.armorBreak", 15.0F, 0.0F, 50.0F, 5.0F, "module.crystalAura.armorBreak.desc", "%"
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.crystalAura.predictTicks", 1.0F, 0.0F, 6.0F, 1.0F, "module.crystalAura.predictTicks.desc", "t"
   );
   private final MultiSelectSetting features = new MultiSelectSetting("module.crystalAura.features", "module.crystalAura.features.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.features, "module.crystalAura.prePlace", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.timing", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.anchors", false);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.autoObsidian", true);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.autoDig", false);
   private final MultiSelectSetting.Entry entry6 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.fromInventory", true);
   private final MultiSelectSetting.Entry entry7 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.shield", false);
   private final MultiSelectSetting.Entry entry8 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiWeakness", true);
   private final MultiSelectSetting.Entry entry9 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiSuicide", true);
   private final MultiSelectSetting.Entry entry10 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.pauseOnUse", true);
   private final MultiSelectSetting.Entry entry11 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.yieldWebTrap", true);
   private final MultiSelectSetting.Entry entry12 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.render", true);
   private final MultiSelectSetting.Entry entry13 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.knockup", true);
   private final MultiSelectSetting.Entry entry14 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.cityMine", true);
   private final MultiSelectSetting.Entry entry15 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.bomber", false);
   private final MultiSelectSetting.Entry entry16 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiTotem", true);
   private final MultiSelectSetting.Entry entry17 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.fallback", true);
   private final MultiSelectSetting.Entry entry18 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.slowPlace", false);
   private final MultiSelectSetting.Entry entry19 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.explosionSync", true);
   private final MultiSelectSetting.Entry entry20 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.combatLog", true);
   private final MultiSelectSetting.Entry entry21 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.antiMine", true);
   private final MultiSelectSetting.Entry entry22 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.crystalDefense", true).method5230();
   private final MultiSelectSetting.Entry entry23 = new MultiSelectSetting.Entry(this.features, "module.crystalAura.webTrap", true).method5230();
   private final BindSetting surroundKey = new BindSetting("module.crystalAura.surroundKey", "module.crystalAura.surroundKey.desc");
   private final ModeSetting surroundMode = new ModeSetting(
      "module.crystalAura.surroundMode",
      "module.crystalAura.surroundMode.desc",
      "module.crystalAura.surroundNormal",
      "module.crystalAura.surroundDouble",
      "module.crystalAura.surroundCity",
      "module.crystalAura.surroundBunker"
   );
   private final SliderSetting mineDamage = new SliderSetting(
      "module.crystalAura.mineDamage",
      10.0F,
      4.0F,
      36.0F,
      0.5F,
      "module.crystalAura.mineDamage.desc",
      "hp",
      () -> this.entry14.isEnabled() || this.entry15.isEnabled() || this.entry5.isEnabled(),
      null
   );
   private final SliderSetting mineTime = new SliderSetting(
      "module.crystalAura.mineTime",
      3.0F,
      0.5F,
      8.0F,
      0.5F,
      "module.crystalAura.mineTime.desc",
      "s",
      () -> this.entry14.isEnabled() || this.entry15.isEnabled() || this.entry5.isEnabled(),
      null
   );
   private final SliderSetting shieldDamage = new SliderSetting(
      "module.crystalAura.shieldDamage", 6.0F, 1.0F, 36.0F, 0.5F, "module.crystalAura.shieldDamage.desc", "hp", this.entry7::isEnabled, null
   );
   private final ModeSetting mode = new ModeSetting("module.crystalAura.mode", "module.crystalAura.mode.desc", this::method5954, "Vanilla", "ReallyWorld");
   private final SliderSetting placeRange = new SliderSetting(
      "module.crystalAura.placeRange", 4.5F, 2.0F, 6.0F, 0.1F, "module.crystalAura.placeRange.desc", "b", this::method5954, null
   );
   private final SliderSetting breakRange = new SliderSetting(
      "module.crystalAura.breakRange", 3.0F, 2.0F, 6.0F, 0.1F, "module.crystalAura.breakRange.desc", "b", this::method5954, null
   );
   private final SliderSetting actions = new SliderSetting(
      "module.crystalAura.actions", 1.0F, 1.0F, 3.0F, 1.0F, "module.crystalAura.actions.desc", "", this::method5954, null
   );
   private final SliderSetting placeDelay = new SliderSetting(
      "module.crystalAura.placeDelay", 0.0F, 0.0F, 500.0F, 10.0F, "module.crystalAura.placeDelay.desc", "ms", this::method5954, null
   );
   private final SliderSetting breakDelay = new SliderSetting(
      "module.crystalAura.breakDelay", 0.0F, 0.0F, 500.0F, 10.0F, "module.crystalAura.breakDelay.desc", "ms", this::method5954, null
   );
   private final SliderSetting rotationSpeed = new SliderSetting(
      "module.crystalAura.rotationSpeed", 180.0F, 10.0F, 180.0F, 5.0F, "module.crystalAura.rotationSpeed.desc", "°", this::method5954, null
   );
   private final ModeSetting swap = new ModeSetting(
      "module.crystalAura.swap",
      "module.crystalAura.swap.desc",
      this::method5954,
      "module.crystalAura.swapKeep",
      "module.crystalAura.swapSilent",
      "module.crystalAura.swapOff"
   );
   private final BooleanSetting instantBreak = new BooleanSetting(
      "module.crystalAura.instantBreak", "module.crystalAura.instantBreak.desc", false, this::method5954
   );
   private final BooleanSetting instantPlace = new BooleanSetting(
      "module.crystalAura.instantPlace", "module.crystalAura.instantPlace.desc", false, () -> this.method5954() && this.instantBreak.isEnabled()
   );
   private final BooleanSetting varyRotation = new BooleanSetting(
      "module.crystalAura.varyRotation", "module.crystalAura.varyRotation.desc", true, this::method5954
   );
   private final BooleanSetting throughWalls = new BooleanSetting(
      "module.crystalAura.throughWalls", "module.crystalAura.throughWalls.desc", false, this::method5954
   );
   private final CombatDamageCalculator class35 = new CombatDamageCalculator();
   private final PlacementQueue placementQueue = new PlacementQueue();
   private final InventoryActionState class33 = new InventoryActionState();
   private final Map<Long, Integer> items = new HashMap<>();
   private final Map<Long, Integer> items2 = new HashMap<>();
   private final Map<Long, Integer> items3 = new HashMap<>();
   private CrystalAuraModule.Type2 type2;
   private int intValue = -32768 << 16 | 0;
   private final Map<Long, Integer> items4 = new HashMap<>();
   private final Map<Long, Integer> items5 = new HashMap<>();
   private final Map<Integer, Integer> items6 = new HashMap<>();
   private final Map<Integer, Double> items7 = new HashMap<>();
   private double doubleValue = 3.0;
   private int intValue2 = -32768 << 16 | 0;
   private final Map<Long, Integer> items8 = new HashMap<>();
   private final Map<Long, Item> items9 = new HashMap<>();
   private final Set<Integer> items10 = new HashSet<>();
   private final Map<Long, Integer> items11 = new HashMap<>();
   private final Map<Integer, Integer> items12 = new HashMap<>();
   private final Map<Long, Integer> items13 = new HashMap<>();
   private CrystalAuraModule.Type type;
   private BlockPos blockPos;
   private int intValue3;
   private final Map<Integer, CrystalAuraModule.Entry> items14 = new HashMap<>();
   private final Queue<CrystalAuraModule.Data7> queue = new ConcurrentLinkedQueue<>();
   private final List<CrystalAuraModule.Data6> items15 = new ArrayList<>();
   private final List<CrystalAuraModule.Data> items16 = new ArrayList<>();
   private final List<CrystalAuraModule.Data> items17 = new ArrayList<>();
   private org.zenith.utility.misc.Data3 data3 = org.zenith.utility.misc.Data3.INSTANCE;
   private List<CrystalAuraModule.Data5> items18 = List.of();
   private List<Player> items19 = List.of();
   private Player playerEntity;
   private CrystalAuraModule.Data data;
   private CrystalAuraModule.Data data2;
   private CrystalAuraModule.Data data4;
   private Rotation rotation;
   private Rotation rotation2;
   private Rotation rotation3;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private CrystalAuraModule.Data8 data8;
   private Vec3 vec3d = Vec3.ZERO;
   private boolean flag;
   private AABB box;
   private AABB box2;
   private float floatValue;
   private double doubleValue2;
   private double doubleValue3;
   private int intValue4 = -32768 << 16 | 0;
   private int intValue5 = -32768 << 16 | 0;
   private int intValue6 = -32768 << 16 | 0;
   private int intValue7 = -32768 << 16 | 0;
   private int intValue8;
   private int intValue9;
   private int intValue10;
   private int intValue11;
   private int intValue12 = -1;
   private int intValue13 = -1;
   private int intValue14 = -1;
   private int intValue15;
   private int intValue16 = -32768 << 16 | 0;
   private int intValue17 = -32768 << 16 | 0;
   private int intValue18 = -32768 << 16 | 0;
   private boolean flag2;
   private boolean flag3;
   private final Queue<Integer> queue2 = new ConcurrentLinkedQueue<>();
   private final Queue<CrystalAuraModule.Data9> queue3 = new ConcurrentLinkedQueue<>();
   private final Queue<CrystalAuraModule.Data2> queue4 = new ConcurrentLinkedQueue<>();
   private final Map<Long, Integer> items20 = new HashMap<>();
   private long longValue = Long.MIN_VALUE;
   private Player playerEntity2;
   private int intValue19;
   private Item item = Items.OBSIDIAN;
   private int intValue20 = -32768 << 16 | 0;
   private float floatValue2;
   private final Map<Long, Integer> items21 = new HashMap<>();
   private final Map<Long, Integer> items22 = new HashMap<>();
   private Rotation rotation4;
   private long longValue2 = Long.MIN_VALUE;
   private int intValue21 = -32768 << 16 | 0;
   private BlockPos blockPos4;
   private CrystalAuraModule.Type type3;
   private CrystalAuraModule.Data data5;
   private int intValue22 = -32768 << 16 | 0;
   private final CombatLogger combatLogger = new CombatLogger();
   private final Queue<Packet<?>> queue5 = new ConcurrentLinkedQueue<>();
   private final Map<Integer, Boolean> items23 = new ConcurrentHashMap<>();
   private int intValue23;
   private int intValue24;
   private int intValue25;
   private String text;
   private String text2;
   private JsonObject jsonObject;
   private JsonArray jsonArray;
   private JsonArray jsonArray2;
   private final Map<String, Integer> items24 = new LinkedHashMap<>();
   private String text3;
   private float floatValue3;
   private String text4;
   private int intValue26;
   private String text5;
   private volatile float floatValue4 = -1.0F;
   private Object obj;
   private Holder<Enchantment> registryEntry;
   private Holder<Enchantment> registryEntry2;
   private Holder<Enchantment> registryEntry3;
   private Player playerEntity3;
   private Rotation rotation5;
   private float floatValue5;
   private EndCrystal endCrystalEntity;
   private AABB box3;
   private boolean flag4;
   private int intValue27 = -1;
   private int intValue28 = -1;
   private int intValue29 = -1;
   private int intValue30 = -32768 << 16 | 0;
   private int intValue31 = -1;
   private int intValue32 = -32768 << 16 | 0;
   private BlockPos blockPos5;
   private BlockPos blockPos6;
   private int intValue33 = -32768 << 16 | 0;
   private String text6 = "-";
   private BlockPos blockPos7;
   private BlockPos blockPos8;
   private int intValue34 = -32768 << 16 | 0;
   private BlockPos blockPos9;
   private static final int CONST_intValue21 = 60;
   private static final double CONST_doubleValue4 = 0.15;

   private CrystalAuraModule() {
      this.preset.set("Grim");
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         this.method5863();
         this.method5860();
         this.method5861();
         this.method5886(true);
      }

      this.method5939();
      if (this.combatLogger.active()) {
         this.combatLogger.method4612("AURA", "аура выключена — журнал пишет AutoTotem, закроется через 10 с");
      }

      this.reset();
      super.onDisable();
   }

   public Player getPlayerEntity() {
      return this.playerEntity;
   }

   public float method5742() {
      if (this.items18.isEmpty()) {
         return 0.0F;
      } else {
         float f = this.data == null ? 0.0F : this.data.getFloatValue2();
         if (this.data4 != null && method5957(this.data4.getType())) {
            f = Math.max(f, this.data4.getFloatValue2());
         }

         return Math.max(f, this.floatValue2);
      }
   }

   public boolean method5743() {
      return this.isEnabled() && this.blockPos3 != null;
   }

   public boolean method5744() {
      return this.isEnabled() && this.entry13.isEnabled();
   }

   public boolean method5745(Entity livingEntity) {
      if (this.isEnabled() && this.entry13.isEnabled() && this.longValue != Long.MIN_VALUE && livingEntity == this.playerEntity2 && mc.player != null) {
         var items = this.items;
         Long longValue = this.longValue;
         if (items != null) {
            if (items.isEmpty()) {
               return false && mc.player.tickCount - this.intValue19 <= this.intValue15 + 4;
            }
         }

         return items.containsKey(longValue) && mc.player.tickCount - this.intValue19 <= this.intValue15 + 4;
      } else {
         return false;
      }
   }

   @EventTarget
   public void method5746(KeyEvent event) {
      if (mc.player != null && this.surroundKey.getKeyCode() != -1 && event.isPress(this.surroundKey.getKeyCode())) {
         this.method5748();
      }
   }

   public void method5747(int n) {
      this.surroundMode.set(this.surroundMode.getValues().get(n).getKey());
   }

   public void method5748() {
      if (mc.player != null) {
         this.blockPos9 = mc.player.blockPosition();
         this.intValue34 = mc.player.tickCount + 60;
         if (this.method5925()) {
            this.combatLogger.method4612("SURROUND", "бинд: застраиваемся в обсу вокруг %s", method5948(this.blockPos9));
         }
      }
   }

   @EventTarget
   public void method5749(PreRotationEvent event) {
      if (!UseItemUtils.method6526() && mc.gameMode != null) {
         int tickCount = mc.player.tickCount;
         if (this.intValue5 != (-32768 << 16 | 0) && tickCount < this.intValue5) {
            this.reset();
         }

         if (this.intValue5 != tickCount) {
            this.intValue5 = tickCount;
            this.box3 = null;
            this.method5756(tickCount);
         }

         Rotation rotation = this.rotation;
         if (rotation != null) {
            rotationManager.requestRotation(
               new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
               8,
               this
            );
         }
      }
   }

   @EventTarget
   public void method5750(InteractionInputEvent event) {
      if (mc.player != null && mc.gameMode != null && !this.data3.isFlag() && this.intValue5 == mc.player.tickCount) {
         int n = this.intValue8;
         this.method5760(this.intValue5);
         if (this.intValue8 > n) {
            this.method5751(this.intValue5);
         }
      }
   }

   private void method5751(int n) {
      Vec3 eyePosition = mc.player.getEyePosition();
      Rotation rotation = this.method5897();
      this.flag4 = true;

      try {
         this.items16.removeIf(var1x -> {
            if (var1x.getEndCrystalEntity().isAlive()) {
               boolean var2x;
               label20: {
                  var map = this.items6;
                  Integer id = var1x.getEndCrystalEntity().getId();
                  if (map != null) {
                     if (map.isEmpty()) {
                        var2x = false;
                        break label20;
                     }
                  }

                  var2x = map.containsKey(id);
               }

               if (!var2x) {
                  return false;
               }
            }

            return true;
         });
         this.data = this.items16.isEmpty() ? null : this.items16.getFirst();
         this.items17.clear();
         this.data4 = this.method5771(eyePosition, rotation);
         Rotation rotation2 = this.method5758(eyePosition, rotation);
         if (rotation2 != null) {
            this.rotation = this.method5876(rotation2);
            this.rotation2 = this.rotation;
            this.intValue4 = n;
            boolean flag = this.data4 != null && rotation2 == this.data4.rotation();
            this.blockPos4 = flag ? this.data4.pos() : null;
            this.type3 = flag ? this.data4.getType() : null;
            Rotation rotation3 = this.rotation;
            rotationManager.requestRotation(
               new RotationRequest(rotation3, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation3), bcCameraTestnerv.getRotationConfig()),
               8,
               this
            );
            return;
         }
      } finally {
         this.flag4 = false;
      }
   }

   @EventTarget
   public void method5752(PostMotionEvent event) {
      if (mc.player != null && mc.gameMode != null) {
         this.intValue8 = 0;
         this.intValue9 = 0;
         if (this.flag2) {
            this.flag2 = false;
            this.method5760(this.intValue5);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         this.placementQueue.method2815(event.getPacket());
         LocalPlayer player = mc.player;
         if (player != null && this.combatLogger.active()) {
            this.method5928(event.getPacket(), player);
         }

         if (player != null && !player.isDeadOrDying()) {
            ClientLevel level = mc.level;
            if (event.getPacket() instanceof ClientboundBlockUpdatePacket blockUpdatePacket) {
               this.queue.add(new CrystalAuraModule.Data7(blockUpdatePacket.getPos().asLong(), blockUpdatePacket.getBlockState(), level == null ? null : level.getBlockState(blockUpdatePacket.getPos())));
            } else if (event.getPacket() instanceof ClientboundSectionBlocksUpdatePacket blocksUpdatePacket) {
               blocksUpdatePacket.runUpdates(
                  (var2x, var3x) -> this.queue.add(new CrystalAuraModule.Data7(var2x.asLong(), var3x, level == null ? null : level.getBlockState(var2x)))
               );
            } else if (event.getPacket() instanceof ClientboundEntityEventPacket entityEventPacket && entityEventPacket.getEventId() == 35) {
               this.queue2.add(entityEventPacket.entityId);
            } else if (event.getPacket() instanceof ClientboundExplodePacket explodePacket) {
               this.queue3.add(new CrystalAuraModule.Data9(explodePacket.center(), explodePacket.radius()));
            } else if (event.getPacket() instanceof ClientboundBlockDestructionPacket blockDestructionPacket && blockDestructionPacket.getId() != player.getId()) {
               this.queue4.add(new CrystalAuraModule.Data2(blockDestructionPacket.getPos().asLong(), blockDestructionPacket.getProgress()));
            }
         }
      }
   }

   @EventTarget
   public void method5753(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.PRE && event.getEntity() instanceof Player playerEntity && mc.player != null && mc.level != null && mc.isSameThread()) {
         CrystalAuraModule.Entry entry = this.items14.get(playerEntity.getId());
         if (entry != null) {
            float f = this.method5892();
            entry.method5967(mc.player.tickCount, f);
            entry.method5968(mc.player.tickCount + this.method5818(), f);
         }

         Vec3 vec3d = this.entry13.isEnabled() && entry != null ? this.method5879(playerEntity, entry.method5971()) : null;
         if (this.method5925()) {
            this.combatLogger.intValue6++;
            this.combatLogger
               .method4612(
                  "SWORD",
                  "удар по %s (hp %.1f) в %.2f б, спринт %s, кулдаун %.2f%s",
                  method5947(playerEntity),
                  method5902(playerEntity),
                  playerEntity.distanceTo(mc.player),
                  mc.player.isSprinting() ? "да" : "нет",
                  mc.player.getAttackStrengthScale(0.5F),
                  vec3d == null
                     ? ", подлёта нет (цель не на земле)"
                     : String.format(Locale.ROOT, ", подлёт vy=%.2f, пинг цели %d т", vec3d.y, this.method5883(playerEntity))
               );
         }

         if (vec3d != null) {
            entry.method5965(mc.player.tickCount, this.method5903(playerEntity), vec3d, this.method5883(playerEntity));
         }
      }
   }

   @EventTarget
   public void method5754(EntityAddEvent event) {
      if (event.getEntity() instanceof EndCrystal endCrystalEntity && !this.flag3 && !UseItemUtils.method6526() && mc.gameMode != null) {
         BlockPos below = endCrystalEntity.blockPosition().below();
         boolean flag;
         if (flag = this.items.remove(below.asLong()) != null) {
            this.items10.add(endCrystalEntity.getId());
         }

         if (flag) {
            this.placementQueue.method2813(method5958(below, 0));
            if (this.entry12.isEnabled()) {
               this.items15.add(new CrystalAuraModule.Data6(below.immutable(), System.currentTimeMillis(), false));
            }
         }

         if (this.method5925() && endCrystalEntity.position().closerThan(mc.player.position(), 12.0)) {
            float f = this.class35.method5452(endCrystalEntity.position(), 6.0F, mc.player, mc.player.getBoundingBox(), null, null);
            if (!flag) {
               this.combatLogger.intValue9++;
            }

            this.combatLogger
               .method4612(
                  flag ? "SPAWN" : "ENEMY",
                  "%s кристалл #%d на %s, до нас %.1f б, урон по нам %.1f",
                  flag ? "наш" : "чужой",
                  endCrystalEntity.getId(),
                  method5948(below),
                  endCrystalEntity.position().distanceTo(mc.player.position()),
                  f
               );
         }

         if (!flag && endCrystalEntity.position().closerThan(mc.player.position(), 12.0)) {
            this.intValue2 = mc.player.tickCount;
            this.items4.put(below.asLong(), mc.player.tickCount);
         }

         if (below.asLong() == this.longValue) {
            this.longValue = Long.MIN_VALUE;
         }

         if (this.data3.isFlag2()
            && !this.items18.isEmpty()
            && this.intValue7 != mc.player.tickCount
            && !this.method5870()
            && this.method5917()
            && this.method5920()
            && this.method5919()) {
            int tickCount = mc.player.tickCount;
            Vec3 eyePosition = mc.player.getEyePosition();
            Rotation rotation = this.method5897();
            if (this.method5835(eyePosition, rotation, endCrystalEntity) != null) {
               CrystalAuraModule.Data12 data12 = this.method5821(endCrystalEntity.position(), 6.0F, null, null, true);
               if (data12 != null && data12.isFlag() && this.method5871(data12.getFloatValue2()) && !this.method5894(data12, endCrystalEntity.position())) {
                  this.flag3 = true;

                  try {
                     if (!this.method5842(endCrystalEntity, tickCount)) {
                        return;
                     }

                     if (this.method5925()) {
                        this.combatLogger.intValue3++;
                        this.combatLogger
                           .method4612(
                              "BREAK",
                              "мгновенно по спавну #%d: урон %.1f по %s, по нам %.1f, до кристалла %.2f б",
                              endCrystalEntity.getId(),
                              data12.getFloatValue(),
                              method5947(data12.getPlayerEntity()),
                              data12.getFloatValue2(),
                              Math.sqrt(method5962(eyePosition, endCrystalEntity.getBoundingBox()))
                           );
                     }

                     if (!this.data3.isFlag3()
                        || !this.method5921()
                        || !this.method5919()
                        || this.method5923(below)
                        || this.method5908(below)
                        || !method5956(mc.level.getBlockState(below))
                        || !this.method5865(below, endCrystalEntity)
                        || this.data3.isFlag4() && method5899(rotation, this.rotation3)) {
                        return;
                     }

                     CrystalAuraModule.Data12 data122 = this.method5821(method5959(below), 6.0F, null, null, false);
                     if (data122 != null && data122.isFlag()) {
                        CrystalAuraModule.Data data = new CrystalAuraModule.Data(
                           CrystalAuraModule.Type.CRYSTAL,
                           null,
                           below.immutable(),
                           rotation,
                           data122.getFloatValue(),
                           data122.getFloatValue2(),
                           data122.value(),
                           true,
                           data122.getPlayerEntity()
                        );
                        BlockHitResult blockHit = this.method5832(data, eyePosition, rotation);
                        if (blockHit != null) {
                           this.method5844(data, blockHit, rotation, tickCount);
                        }

                        return;
                     }
                  } finally {
                     this.flag3 = false;
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      this.class33.onMoveInput(event);
      if (mc.player == null || this.blockPos9 == null || !this.method5773(event)) {
         if (this.rotation != null && mc.player != null) {
            MovementInputUtils.correctMovement(event, rotationManager.getEffectiveRotation().getFloatValue(), mc.player.getYRot());
         }
      }
   }

   @EventTarget
   public void method5755(WorldRenderEvent event) {
      if (this.entry12.isEnabled() && mc.player != null) {
         int n = zenith.getStyleManager().getClientColor(0).method7287();
         CrystalAuraModule.Data data = this.data4;
         if (data != null) {
            if (data.getType() == CrystalAuraModule.Type.DIG) {
               GlowUtil.method2230(method5964(data.pos(), 0.4 + 0.6 * this.floatValue), ColorUtils.method6439(-1 << 16 | 23100, 0.6F), 1.5F);
            } else {
               int color = method5957(data.getType()) ? -77 << 16 | 27647 : (data.isFlag() ? n : -15812);
               GlowUtil.method2230(new AABB(data.pos()), ColorUtils.method6439(color, 0.35F), 1.5F);
            }
         }

         CrystalAuraModule.Data data2 = this.data2;
         if (data2 != null && this.data == null) {
            GlowUtil.method2230(new AABB(data2.pos()), ColorUtils.method6439(-15812, 0.3F), 1.5F);
         }

         long now = System.currentTimeMillis();
         var iterator = this.items15.iterator();

         while (iterator.hasNext()) {
            CrystalAuraModule.Data6 data6 = (CrystalAuraModule.Data6)iterator.next();
            float f;
            if ((f = (float)(now - data6.time()) / 400.0F) >= 1.0F) {
               iterator.remove();
            } else {
               int color = data6.isFlag() ? -77 << 16 | 27647 : n;
               GlowUtil.method2230(method5964(data6.pos(), 1.0 - 0.4 * f), ColorUtils.method6439(color, 1.0F - f), 1.5F);
            }
         }
      } else {
         this.items15.clear();
      }
   }

   private void method5756(int tickCount) {
      this.data = null;
      this.data2 = null;
      this.data4 = null;
      this.items16.clear();
      this.items17.clear();
      this.rotation = null;
      this.flag2 = false;
      this.data3 = this.method5953();
      this.intValue15 = Math.round(this.method5910() / 50.0F);
      this.floatValue2 = 0.0F;
      this.combatLogger.setIntValue(tickCount);
      this.method5931(tickCount);
      this.class35.reset();
      this.placementQueue.method2816(tickCount);
      this.method5924(tickCount);
      this.method5951(tickCount);
      this.method5929();
      this.method5952(tickCount);
      String str = this.method5918();
      if (str != null) {
         if (this.method5925()) {
            this.combatLogger.method4615("ПАУЗА: " + str);
         }

         if (this.jsonObject != null) {
            this.jsonObject.addProperty("pause", str);
         }

         this.class33.cancel();
         this.method5863();
         this.method5955();
         this.method5862();
      } else {
         if (!this.method5869() && this.class33.tryCompleteInventoryMove()) {
            this.intValue7 = tickCount;
         }

         Vec3 eyePosition = mc.player.getEyePosition();
         this.vec3d = eyePosition.add(Entity.collideBoundingBox(mc.player, mc.player.getDeltaMovement(), mc.player.getBoundingBox(), mc.level, List.of()));
         this.items18 = this.method5829() ? this.method5900(eyePosition, tickCount) : List.of();
         if (this.entry20.isEnabled()) {
            this.combatLogger
               .method4611(
                  !this.items18.isEmpty(),
                  this.items18.isEmpty() ? "" : this.method5757()
               );
         } else if (this.combatLogger.active()) {
            this.combatLogger.method4616("журнал выключен");
         }

         this.method5932();
         if (this.items18.isEmpty()) {
            this.class33.cancel();
            this.method5863();
            this.method5955();
            CrystalAuraModule.Data data = this.method5775(this.method5897());
            if (data != null) {
               this.items17.add(data);
               this.data4 = data;
               this.rotation = this.method5876(data.rotation());
               this.rotation2 = this.rotation;
               this.intValue4 = tickCount;
               this.blockPos4 = data.pos();
               this.type3 = data.getType();
               this.method5938();
            } else {
               this.method5862();
            }
         } else {
            this.items19 = this.method5905(eyePosition);
            this.method5913();
            this.box = this.method5820(this.method5818());
            this.box2 = this.method5820(this.method5819());
            Rotation rotation = this.method5897();
            if (this.method5920()) {
               this.method5763(eyePosition, rotation);
            }

            this.data4 = this.method5771(eyePosition, rotation);
            this.method5885();
            this.method5886(false);
            if (this.blockPos3 != null && (tickCount - this.intValue20 > 10 || mc.level.getBlockState(this.blockPos3).canBeReplaced())) {
               boolean canBeReplaced;
               if (canBeReplaced = mc.level.getBlockState(this.blockPos3).canBeReplaced()) {
                  this.combatLogger.intValue5++;
               }

               if (this.method5925()) {
                  this.combatLogger
                     .method4612(canBeReplaced ? "MINED" : "DIG-STOP", "%s %s", method5948(this.blockPos3), canBeReplaced ? "выкопан" : "брошен: блок больше не нужен");
               }

               this.method5863();
            }

            if (this.data == null
               && this.data4 == null
               && this.data2 == null
               && this.items.isEmpty()) {
               this.method5862();
            } else {
               this.intValue10 = 0;
            }

            CrystalAuraModule.Data data = this.data != null ? this.data : (this.data4 != null ? this.data4 : this.data2);
            this.playerEntity = data == null ? this.items18.getFirst().getPlayerEntity() : data.getPlayerEntity();
            Rotation rotation2 = this.method5758(eyePosition, rotation);
            boolean flag = rotation2 != null && this.data4 != null && rotation2 == this.data4.rotation();
            this.blockPos4 = flag ? this.data4.pos() : null;
            this.type3 = flag ? this.data4.getType() : null;
            if (rotation2 != null) {
               this.rotation = this.method5876(rotation2);
               this.rotation2 = this.rotation;
               this.intValue4 = tickCount;
            } else if (this.rotation2 != null && tickCount - this.intValue4 <= 6) {
               this.rotation = this.method5876(this.rotation2);
            }

            this.flag2 = this.data3.isFlag();
            this.method5930();
            this.method5938();
         }
      }
   }

   private String method5757() {
      CrystalAuraModule.Data5 data5 = this.items18.getFirst();
      return String.format(
         Locale.ROOT,
         "цель %s в %.1f б; пресет %s, дальность установки %.2f / удара %.2f, действий %d, пинг %d тиков",
         method5947(data5.getPlayerEntity()),
         data5.getVec3d().distanceTo(mc.player.position()),
         this.preset.get(),
         this.method5878(),
         this.method5877(),
         this.data3.getIntValue(),
         this.intValue15
      );
   }

   private Rotation method5758(Vec3 eyePosition, Rotation rotation) {
      if (this.data == null || !this.data3.isFlag() && this.method5835(eyePosition, rotation, this.data.getEndCrystalEntity()) != null) {
         if (this.data4 != null && this.data4.getType() == CrystalAuraModule.Type.SHIELD && this.data4.isFlag()) {
            return this.data4.rotation();
         } else if (this.rotation4 != null) {
            return this.rotation4;
         } else if (this.rotation5 != null && this.data == null) {
            return this.rotation5;
         } else {
            Rotation rotation2 = this.method5759(eyePosition);
            if (rotation2 != null) {
               return rotation2;
            } else if (this.data4 != null && this.data4.isFlag()) {
               return this.data4.rotation();
            } else if (this.data2 != null) {
               return this.data2.rotation();
            } else if (this.data4 != null) {
               return this.data4.rotation();
            } else if (this.data == null && this.data5 != null) {
               return this.data5.rotation();
            } else {
               return this.data == null ? null : this.data.rotation();
            }
         }
      } else {
         Rotation rotation2 = this.method5837(this.data4, this.data.getEndCrystalEntity());
         return rotation2 != null ? rotation2 : this.data.rotation();
      }
   }

   private Rotation method5759(Vec3 eyePosition) {
      if (this.rotation3 != null && !this.items.isEmpty()) {
         if (this.data4 != null && this.data4.isFlag()) {
            Rotation rotation = this.method5897();
            var iterator = this.items.keySet().iterator();

            while (iterator.hasNext()) {
               long blockNode;
               if ((blockNode = (Long)iterator.next()) != this.data4.pos().asLong()) {
                  this.box3 = method5961(BlockPos.of(blockNode));

                  Rotation rotation2;
                  try {
                     rotation2 = this.method5831(this.data4.getType(), this.data4.pos(), this.vec3d, rotation);
                  } finally {
                     this.box3 = null;
                  }

                  if (rotation2 != null) {
                     return rotation2;
                  }
               }
            }

            if (this.method5896(this.intValue16) > this.intValue15 + 1 || method5957(this.data4.getType())) {
               return null;
            }
         }

         Vec3 to = eyePosition.add(this.rotation3.toDirection().scale(this.method5877()));
         var iterator = this.items.keySet().iterator();

         while (iterator.hasNext()) {
            if (method5961(BlockPos.of((Long)iterator.next())).clip(eyePosition, to).isPresent()) {
               return this.rotation3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void method5760(int tickCount) {
      if (this.intValue6 != tickCount) {
         this.intValue6 = tickCount;
         CrystalAuraModule.Data data = this.data4 != null && this.data4.isFlag() && method5761(this.data4.getType()) ? this.data4 : null;
         boolean flag = this.method5869();
         String str = this.intValue7 == tickCount
            ? "пополняем хотбар из инвентаря"
            : (this.method5870() ? "AutoTotem кликает по инвентарю" : (this.method5872() ? "уступаем AutoTrap" : null));
         if (str != null) {
            if (this.data != null && this.method5925()) {
               this.combatLogger.method4612("MISS", "break %s не сделали: %s", this.method5944(this.data), str);
            }

            if (data != null) {
               this.method5943(data, str);
            } else if (this.data4 != null && this.method5925() && !this.data4.pos().equals(this.blockPos8)) {
               this.blockPos8 = this.data4.pos();
               this.combatLogger.method4612("MISS", "%s %s не сделали: %s", this.data4.getType(), method5948(this.data4.pos()), str);
            }
         } else {
            Vec3 eyePosition = mc.player.getEyePosition();
            Rotation rotation = this.method5897();
            String str2 = null;
            this.flag = flag;
            this.method5890(eyePosition, rotation);

            for (CrystalAuraModule.Data data2 : this.items16) {
               if (!this.method5919()) {
                  str2 = "бюджет действий кончился";
                  break;
               }

               EndCrystal endCrystalEntity = data2.getEndCrystalEntity();
               if (endCrystalEntity.isAlive()) {
                  boolean containsKey;
                  label302: {
                     var map = this.items6;
                     Integer id = endCrystalEntity.getId();
                     if (map != null) {
                        if (map.isEmpty()) {
                           containsKey = false;
                           break label302;
                        }
                     }

                     containsKey = map.containsKey(id);
                  }

                  if (!containsKey) {
                     if (!this.method5871(data2.getFloatValue2())) {
                        str2 = String.format(Locale.ROOT, "ждём тотем в руке (урон по нам %.1f)", data2.getFloatValue2());
                     } else {
                        if (this.method5835(eyePosition, rotation, endCrystalEntity) != null) {
                           if (this.method5842(endCrystalEntity, tickCount) && this.method5925()) {
                              this.combatLogger.intValue3++;
                              this.combatLogger
                                 .method4612(
                                    "BREAK",
                                    "кристалл #%d %s: урон %.1f по %s (hp %.1f), по нам %.1f, до кристалла %.2f б, цель в %.2f б",
                                    endCrystalEntity.getId(),
                                    method5949(endCrystalEntity.position()),
                                    data2.getFloatValue(),
                                    method5947(data2.getPlayerEntity()),
                                    data2.getPlayerEntity() == null ? 0.0F : method5902(data2.getPlayerEntity()),
                                    data2.getFloatValue2(),
                                    Math.sqrt(method5962(eyePosition, endCrystalEntity.getBoundingBox())),
                                    data2.getPlayerEntity() == null ? -1.0 : data2.getPlayerEntity().distanceTo(mc.player)
                                 );
                           }

                           str2 = null;
                           break;
                        }

                        str2 = "луч сервера не попадает в кристалл (поворот ещё не довёл)";
                     }
                  }
               }
            }

            if (str2 != null && this.data != null && this.method5925()) {
               this.combatLogger.method4612("MISS", "break %s не сделали: %s", this.method5944(this.data), str2);
            }

            boolean flag2 = false;
            this.text = null;
            String text = null;

            try {
               for (int i = 0; i < this.items17.size(); i++) {
                  if (!this.method5919()) {
                     this.text = this.text == null ? "бюджет действий потрачен на break" : this.text;
                     break;
                  }

                  CrystalAuraModule.Data data2 = this.items17.get(i);
                  if (i <= 0 || data2.isFlag()) {
                     if (method5957(data2.getType()) && !this.method5871(data2.getFloatValue2())) {
                        this.text = "якорь: ждём тотем в руке";
                     } else if (this.method5762(data2, eyePosition, rotation, tickCount)) {
                        flag2 = true;
                        break;
                     }

                     if (i == 0) {
                        text = this.text;
                     }

                     if ((data2.getType() == CrystalAuraModule.Type.SHIELD || data2.getType() == CrystalAuraModule.Type.DIG)
                        && i == 0
                        && this.text != null
                        && this.method5925()
                        && !data2.pos().equals(this.blockPos8)) {
                        this.blockPos8 = data2.pos();
                        this.combatLogger.method4612("MISS", "%s %s не сделали: %s", data2.getType(), method5948(data2.pos()), this.text);
                     }
                  }
               }

               if (data != null) {
                  String text2 = text != null ? text : this.text;
                  this.method5943(data, flag2 ? null : (text2 == null ? "неизвестно" : text2));
               }

               if (!flag2 && this.data5 != null && this.method5919()) {
                  this.method5762(this.data5, eyePosition, rotation, tickCount);
               }
            } finally {
               this.flag = false;
            }

            this.method5860();
         }
      }
   }

   private static boolean method5761(CrystalAuraModule.Type type) {
      return type == CrystalAuraModule.Type.CRYSTAL
         || type == CrystalAuraModule.Type.OBSIDIAN
         || type == CrystalAuraModule.Type.ANCHOR_PLACE
         || type == CrystalAuraModule.Type.ANCHOR_CHARGE
         || type == CrystalAuraModule.Type.ANCHOR_EXPLODE;
   }

   private boolean method5762(CrystalAuraModule.Data data, Vec3 eyePosition, Rotation rotation, int tickCount) {
      CrystalAuraModule.Type type = data.getType();

      boolean flag;
      for (flag = false;
         type != null && this.method5919();
         type = type == CrystalAuraModule.Type.ANCHOR_CHARGE && data.isFlag() ? CrystalAuraModule.Type.ANCHOR_EXPLODE : null
      ) {
         CrystalAuraModule.Data data2 = type == data.getType()
            ? data
            : new CrystalAuraModule.Data(
               type, null, data.pos(), rotation, data.getFloatValue(), data.getFloatValue2(), data.getFloatValue3(), data.isFlag(), data.getPlayerEntity()
            );
         BlockHitResult blockHit = this.method5832(data2, eyePosition, rotation);
         if (blockHit == null) {
            this.text = "луч сервера не попадает в нужную грань (поворот ещё не довёл / закрыто)";
            return flag;
         }

         if (this.method5908(data2.getType() == CrystalAuraModule.Type.CRYSTAL ? data2.pos() : blockHit.getBlockPos())) {
            this.text = "блок под действие поставлен в этом же тике";
            return flag;
         }

         if (method5761(type) && type != CrystalAuraModule.Type.ANCHOR_CHARGE && type != CrystalAuraModule.Type.ANCHOR_EXPLODE && !this.method5921()) {
            this.text = "задержка установки";
            return flag;
         }

         if (type == CrystalAuraModule.Type.CRYSTAL && !this.method5895(data, tickCount)) {
            this.text = "медленная установка на мелком уроне";
            return flag;
         }

         if (type == CrystalAuraModule.Type.CRYSTAL && this.data3.isFlag4() && method5899(rotation, this.rotation3)) {
            this.text = "тот же поворот, что у прошлой установки (разброс поворота)";
            return flag;
         }

         if (type == CrystalAuraModule.Type.CRYSTAL && !this.method5865(data.pos(), null)) {
            this.text = "место занято сущностью / над основанием не воздух";
            return flag;
         }

         this.text = "нет предмета в руке или смена слота запрещена (свитч)";

         boolean flag2 = switch (type) {
            case BREAK -> false;
            case CRYSTAL -> this.method5844(data2, blockHit, rotation, tickCount);
            case OBSIDIAN -> this.method5921() && this.method5845(data2, blockHit, Items.OBSIDIAN, rotation, tickCount);
            case DIG -> this.method5848(data2, blockHit);
            case SHIELD -> this.method5921() && this.method5845(data2, blockHit, this.items9.getOrDefault(data2.pos().asLong(), this.item), rotation, tickCount);
            case ANCHOR_PLACE -> {
               boolean flag3;
               if (flag3 = this.method5921() && this.method5845(data2, blockHit, Items.RESPAWN_ANCHOR, rotation, tickCount)) {
                  this.items11.put(data.pos().asLong(), tickCount + 100);
               }

               yield flag3;
            }
            case ANCHOR_CHARGE -> this.method5846(data2, blockHit, tickCount);
            case ANCHOR_EXPLODE -> this.method5847(data2, blockHit, tickCount);
         };

         if (!flag2) {
            return flag;
         }

         flag = true;
      }

      return flag;
   }

   private void method5763(Vec3 eyePosition, Rotation rotation) {
      double d = this.method5877();

      for (EndCrystal endCrystalEntity : mc.level.getEntitiesOfClass(EndCrystal.class, mc.player.getBoundingBox().inflate(d + 1.0), endCrystal -> {
         if (endCrystal.isAlive()) {
            boolean containsKey;
            label22: {
               var map = this.items6;
               Integer id = endCrystal.getId();
               if (map != null) {
                  if (map.isEmpty()) {
                     containsKey = false;
                     break label22;
                  }
               }

               containsKey = map.containsKey(id);
            }

            if (!containsKey && method5962(eyePosition, endCrystal.getBoundingBox()) <= d * d) {
               return true;
            }
         }

         return false;
      })) {
         CrystalAuraModule.Data12 data12 = this.method5821(endCrystalEntity.position(), 6.0F, null, null, true);
         this.method5935(endCrystalEntity, data12, data12 == null ? this.text2 : null);
         if (data12 != null && (data12.isFlag() || this.data2 == null || !(data12.value() <= this.data2.getFloatValue3()))) {
            boolean flag;
            if ((flag = data12.isFlag() && !this.method5894(data12, endCrystalEntity.position()))
               && endCrystalEntity.blockPosition().below().equals(this.blockPos3)
               && data12.getFloatValue() < this.mineDamage.getValue3()) {
               flag = false;
            }

            Rotation rotation2 = this.method5835(this.vec3d, rotation, endCrystalEntity) != null ? rotation : this.method5834(this.vec3d, rotation, endCrystalEntity);
            if (rotation2 != null) {
               if (!flag) {
                  int n = this.items12.computeIfAbsent(endCrystalEntity.getId(), n2 -> mc.player.tickCount);
                  if (mc.player.tickCount - n > 20) {
                     float floatValue;
                     if ((floatValue = this.method5770(endCrystalEntity.position())) <= 0.0F || data12.getPlayerEntity() == null) {
                        continue;
                     }

                     float f;
                     data12 = new CrystalAuraModule.Data12(data12.getPlayerEntity(), floatValue, f = data12.getFloatValue2(), floatValue - f * 1.2F, true);
                     flag = true;
                  }
               }

               CrystalAuraModule.Data data = new CrystalAuraModule.Data(
                  CrystalAuraModule.Type.BREAK,
                  endCrystalEntity,
                  endCrystalEntity.blockPosition().below(),
                  rotation2,
                  data12.getFloatValue(),
                  data12.getFloatValue2(),
                  data12.value(),
                  flag,
                  data12.getPlayerEntity()
               );
               if (flag) {
                  this.items16.add(data);
               } else {
                  this.data2 = data;
               }
            }
         }
      }

      this.items12.keySet().removeIf(id -> mc.level.getEntity(id) == null);
      this.items16.sort(Comparator.comparingDouble(data -> -data.getFloatValue3()));
      this.data = this.items16.isEmpty() ? null : this.items16.getFirst();
   }

   private CrystalAuraModule.Data method5764(Rotation rotation) {
      AABB inflate = mc.player.getBoundingBox().inflate(0.3, 0.0, 0.3);
      MutableBlockPos mutablePos = new MutableBlockPos();
      BlockPos immutable = null;
      double d = Double.MAX_VALUE;

      for (int i = Mth.floor(inflate.minX); i <= Mth.floor(inflate.maxX); i++) {
         for (int j = Mth.floor(inflate.minZ); j <= Mth.floor(inflate.maxZ); j++) {
            for (int k = Mth.floor(inflate.minY); k <= Mth.floor(inflate.minY + 1.0); k++) {
               mutablePos.set(i, k, j);
               double d2;
               if (mc.level.getBlockState(mutablePos).is(BlockTags.FIRE)
                  && inflate.intersects(new AABB(mutablePos))
                  && (d2 = mc.player.position().distanceToSqr(Vec3.atCenterOf(mutablePos))) < d) {
                  d = d2;
                  immutable = mutablePos.immutable();
               }
            }
         }
      }

      if (immutable == null) {
         return null;
      } else {
         Rotation rotation2 = this.method5836(immutable, null, this.vec3d, rotation);
         if (rotation2 == null) {
            return null;
         } else {
            if (this.method5925() && !immutable.equals(this.blockPos5)) {
               this.blockPos5 = immutable;
               this.combatLogger.method4612("FIRE", "огонь у ног в %s — гасим", method5948(immutable));
            }

            return new CrystalAuraModule.Data(CrystalAuraModule.Type.DIG, null, immutable, rotation2, 0.0F, 0.0F, 30.0F, true, this.items18.getFirst().getPlayerEntity());
         }
      }
   }

   private CrystalAuraModule.Data method5765(Vec3 eyePosition, Rotation rotation, CrystalAuraModule.Data data) {
      if (this.method5921() && !this.items18.isEmpty()) {
         Item rawType = this.method5767();
         if (rawType == null) {
            return null;
         } else {
            this.items8.values().removeIf(n -> n < mc.player.tickCount);
            AABB box2 = this.box2 != null ? this.box2 : mc.player.getBoundingBox();
            float f = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            BlockPos containing = BlockPos.containing((box2.minX + box2.maxX) * 0.5, box2.minY + 0.001, (box2.minZ + box2.maxZ) * 0.5);
            Vec3 vec = this.method5804(containing);
            CrystalAuraModule.Data data2 = null;
            float f2 = 0.0F;
            int n = 0;

            for (int i = -4; i <= 4; i++) {
               for (int j = -4; j <= 4; j++) {
                  for (int k = -2; k <= 1; k++) {
                     BlockPos offset = containing.offset(i, k, j);
                     if (method5956(mc.level.getBlockState(offset))) {
                        BlockPos above;
                        boolean containsKey;
                        label172: {
                           above = offset.above();
                           var map = this.items8;
                           Long l = offset.asLong();
                           if (map != null) {
                              if (map.isEmpty()) {
                                 containsKey = false;
                                 break label172;
                              }
                           }

                           containsKey = map.containsKey(l);
                        }

                        boolean flag = containsKey;
                        if (containsKey
                           || Math.abs(above.getX() - containing.getX()) > 1
                           || Math.abs(above.getZ() - containing.getZ()) > 1
                           || above.getY() < containing.getY()
                           || above.getY() > containing.getY() + 1) {
                           if (!flag && vec != null && above.distSqr(containing) <= 9.0) {
                              double d = above.getX() - containing.getX();
                              double d2 = above.getZ() - containing.getZ();
                              double d3;
                              if ((d3 = Math.sqrt(d * d + d2 * d2)) > 0.0 && (d * vec.x + d2 * vec.z) / d3 > 0.3) {
                                 continue;
                              }
                           }

                           if (mc.level.isEmptyBlock(above)
                              && !this.method5923(above)
                              && this.method5867(above)
                              && !this.method5777(above)
                              && this.method5811(offset)
                              && (flag || this.method5812(offset))) {
                              float f3 = this.class35.method5452(method5959(offset), 6.0F, mc.player, box2, null, null);
                              float f4 = flag ? 3.0F : Math.max(this.shieldDamage.getValue3(), f * 0.4F);
                              float f5;
                              if (!(f3 < f4)
                                 && (!(f3 < f - 1.0F) || !this.method5766(above))
                                 && (data == null || !data.pos().equals(offset))
                                 && !this.method5803(offset)
                                 && !this.method5805(above, eyePosition, data)
                                 && !((f5 = f3 + (flag ? 8.0F : 0.0F)) <= f2)
                                 && n++ <= 8) {
                                 Rotation rotation2 = this.method5831(CrystalAuraModule.Type.SHIELD, above, this.vec3d, rotation);
                                 if (rotation2 != null) {
                                    f2 = f5;
                                    boolean flag2 = flag || this.method5806() && f3 >= Math.min(f - 1.0F, f * 0.6F);
                                    data2 = new CrystalAuraModule.Data(
                                       CrystalAuraModule.Type.SHIELD,
                                       null,
                                       above.immutable(),
                                       rotation2,
                                       0.0F,
                                       f3,
                                       f5,
                                       flag2,
                                       this.items18.getFirst().getPlayerEntity()
                                    );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (data2 != null) {
               this.items9.put(data2.pos().asLong(), rawType);
               if (this.method5925() && !data2.pos().equals(this.blockPos6)) {
                  CombatLogger combatLogger;
                  boolean containsKey;
                  Object[] string;
                  label107: {
                     this.blockPos6 = data2.pos();
                     combatLogger = this.combatLogger;
                     string = new Object[]{method5948(data2.pos()), method5950(rawType), data2.getFloatValue2(), null};
                     var map = this.items8;
                     Long l = data2.pos().below().asLong();
                     if (map != null) {
                        if (map.isEmpty()) {
                           containsKey = false;
                           break label107;
                        }
                     }

                     containsKey = map.containsKey(l);
                  }

                  string[3] = containsKey ? ", обсу только что поставил враг" : "";
                  combatLogger.method4612("CDEF", "закрываем основание под %s (%s): кристалл там дал бы по нам %.1f%s", string);
               }
            }

            return data2;
         }
      } else {
         return null;
      }
   }

   private boolean method5766(BlockPos above) {
      Vec3 atCenterOf = Vec3.atCenterOf(above);

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         if (method5962(atCenterOf, data5.getPlayerEntity().getBoundingBox()) <= 2.25) {
            return true;
         }
      }

      return false;
   }

   private Item method5767() {
      return this.method5915(stack -> stack.is(Items.COBWEB)) ? Items.COBWEB : this.method5810();
   }

   private CrystalAuraModule.Data method5768(Rotation rotation) {
      if (this.method5921() && this.method5915(stack -> stack.is(Items.COBWEB)) && this.method5896(this.intValue33) >= 20) {
         for (CrystalAuraModule.Data5 data5 : this.items18) {
            Player playerEntity = data5.getPlayerEntity();
            BlockPos containing = BlockPos.containing(playerEntity.getX(), playerEntity.getY() + 0.1, playerEntity.getZ());
            if (!mc.level.getBlockState(containing).is(Blocks.COBWEB)
               && mc.level.getBlockState(containing).canBeReplaced()
               && playerEntity.onGround()
               && !(method5962(this.vec3d, new AABB(containing)) > this.method5878() * this.method5878())
               && !this.method5923(containing)) {
               boolean containsKey;
               label50: {
                  var map = this.items2;
                  Long l = containing.asLong();
                  if (map != null) {
                     if (map.isEmpty()) {
                        containsKey = false;
                        break label50;
                     }
                  }

                  containsKey = map.containsKey(l);
               }

               if (!containsKey) {
                  Rotation rotation2 = this.method5831(CrystalAuraModule.Type.SHIELD, containing, this.vec3d, rotation);
                  if (rotation2 != null) {
                     this.items9.put(containing.asLong(), Items.COBWEB);
                     if (this.method5925() && !containing.equals(this.blockPos7)) {
                        this.blockPos7 = containing;
                        this.combatLogger.method4612("WEB", "паутина под ноги %s в %s", method5947(playerEntity), method5948(containing));
                     }

                     return new CrystalAuraModule.Data(CrystalAuraModule.Type.SHIELD, null, containing.immutable(), rotation2, 0.0F, 0.0F, 1.0F, true, playerEntity);
                  }

                  this.text6 = "не навестись на " + method5948(containing);
                  continue;
               }
            }

            this.text6 = "клетка "
               + method5948(containing)
               + ": "
               + mc.level.getBlockState(containing).getBlock()
               + " земля "
               + playerEntity.onGround()
               + " дист "
               + String.format(Locale.ROOT, "%.2f", Math.sqrt(method5962(this.vec3d, new AABB(containing))));
         }

         return null;
      } else {
         this.text6 = !this.method5921() ? "задержка установки" : (this.method5896(this.intValue33) < 20 ? "пауза" : "нет паутины в хотбаре");
         return null;
      }
   }

   public boolean method5769() {
      return this.isEnabled() && (this.entry22.isEnabled() || this.entry23.isEnabled());
   }

   private float method5770(Vec3 position) {
      float f = 0.0F;

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         f = Math.max(f, this.method5823(data5, position, 6.0F, data5.getBox(), null, null, this.method5818()));
      }

      return f;
   }

   private CrystalAuraModule.Data method5771(Vec3 eyePosition, Rotation rotation) {
      this.data5 = null;
      this.rotation4 = null;
      CrystalAuraModule.Data data = this.method5775(rotation);
      if (data != null) {
         this.items17.add(data);
         return data;
      } else {
         CrystalAuraModule.Data data2 = this.entry21.isEnabled() ? this.method5776(rotation) : null;
         if (data2 != null && data2.isFlag()) {
            this.items17.add(data2);
            return data2;
         } else {
            CrystalAuraModule.Data3 data3 = this.entry7.isEnabled() ? this.method5801() : null;
            if (data3 != null && data3.isFlag()) {
               CrystalAuraModule.Data data4 = this.method5781(eyePosition, rotation);
               CrystalAuraModule.Data data5 = this.method5802(data3, rotation, data4);
               if (data5 != null) {
                  this.items17.addFirst(data5);
                  return data5;
               }

               if (data4 != null) {
                  return data4;
               }
            }

            CrystalAuraModule.Data data4 = this.method5764(rotation);
            if (data4 != null) {
               this.items17.add(data4);
               return data4;
            } else {
               CrystalAuraModule.Data data5 = this.method5781(eyePosition, rotation);
               CrystalAuraModule.Data data6 = this.entry22.isEnabled() ? this.method5765(eyePosition, rotation, data5) : null;
               if (data6 != null && data6.isFlag()) {
                  this.items17.addFirst(data6);
                  return data6;
               } else {
                  if (data6 != null && this.data5 == null) {
                     this.data5 = data6;
                  }

                  if (data5 == null && this.entry23.isEnabled()) {
                     CrystalAuraModule.Data data7 = this.method5768(rotation);
                     if (data7 != null) {
                        this.items17.add(data7);
                        return data7;
                     }
                  }

                  if (data3 != null && !data3.isFlag() && this.method5896(this.intValue22) >= 4) {
                     this.data5 = this.method5802(data3, rotation, data5);
                  }

                  if (this.data5 == null && data2 != null) {
                     this.data5 = data2;
                  }

                  return data5;
               }
            }
         }
      }
   }

   private Set<BlockPos> method5772() {
      AABB boundingBox = mc.player.getBoundingBox();
      double y = boundingBox.minY + 0.5;
      HashSet<BlockPos> poses = new HashSet();
      poses.add(BlockPos.containing(boundingBox.minX + 0.001, y, boundingBox.minZ + 0.001));
      poses.add(BlockPos.containing(boundingBox.maxX - 0.001, y, boundingBox.minZ + 0.001));
      poses.add(BlockPos.containing(boundingBox.minX + 0.001, y, boundingBox.maxZ - 0.001));
      poses.add(BlockPos.containing(boundingBox.maxX - 0.001, y, boundingBox.maxZ - 0.001));
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (BlockPos pos : poses) {
         for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos relative = pos.relative(direction);
            if (!poses.contains(relative)) {
               linkedHashSet.add(relative);
            }
         }
      }

      return linkedHashSet;
   }

   private boolean method5773(MoveInputEvent event) {
      if (!mc.player.onGround()) {
         return false;
      } else {
         Vec3 deltaMovement = mc.player.getDeltaMovement();
         double d = this.blockPos9.getX() + 0.5 - mc.player.getX();
         double d2 = this.blockPos9.getZ() + 0.5 - mc.player.getZ();
         double d3 = d - deltaMovement.x * 2.2;
         double d4 = d2 - deltaMovement.z * 2.2;
         if (Math.abs(d3) <= 0.15 && Math.abs(d4) <= 0.15) {
            event.clearInput();
            return true;
         } else {
            double d5 = Math.abs(d3) > 0.15 ? d3 : 0.0;
            double x = Math.abs(d4) > 0.15 ? d4 : 0.0;
            float f = (float)Math.toDegrees(Math.atan2(-d5, x));
            float floatValue = this.rotation != null ? rotationManager.getEffectiveRotation().getFloatValue() : mc.player.getYRot();
            float f2 = 0.0F;
            float f3 = 0.0F;
            double d6 = Double.MAX_VALUE;

            for (float f4 = -1.0F; f4 <= 1.0F; f4++) {
               for (float f5 = -1.0F; f5 <= 1.0F; f5++) {
                  double d7;
                  if ((f4 != 0.0F || f5 != 0.0F)
                     && (d7 = Math.abs(Mth.wrapDegrees(Math.toDegrees(MovementInputUtils.movementYawRadians(floatValue, f4, f5)) - f))) < d6) {
                     d6 = d7;
                     f2 = f4;
                     f3 = f5;
                  }
               }
            }

            event.clearInput();
            event.setMovement(f2, f3);
            return true;
         }
      }
   }

   private List<BlockPos> method5774() {
      ArrayList<BlockPos> poses = new ArrayList<>();
      BlockPos blockPos9 = this.blockPos9;

      for (Direction direction : Direction.Plane.HORIZONTAL) {
         poses.add(blockPos9.relative(direction));
      }

      int index;
      if ((index = this.surroundMode.getIndex()) == 1 || index == 3) {
         for (Direction direction : Direction.Plane.HORIZONTAL) {
            poses.add(blockPos9.above().relative(direction));
         }
      }

      if (index == 2) {
         for (Direction direction : Direction.Plane.HORIZONTAL) {
            poses.add(blockPos9.relative(direction, 2));
         }
      }

      if (index == 3) {
         poses.add(blockPos9.above(2));
         poses.add(blockPos9.below());
      }

      return poses;
   }

   private CrystalAuraModule.Data method5775(Rotation rotation) {
      if (this.blockPos9 == null) {
         return null;
      } else {
         BlockPos blockPosition = mc.player.blockPosition();
         boolean flag = blockPosition.getX() != this.blockPos9.getX() || blockPosition.getZ() != this.blockPos9.getZ() || Math.abs(blockPosition.getY() - this.blockPos9.getY()) > 1;
         if (mc.player.tickCount <= this.intValue34 && !flag && this.method5915(stack -> stack.is(Items.OBSIDIAN))) {
            boolean flag2 = false;

            for (BlockPos pos : this.method5774()) {
               if (mc.level.getBlockState(pos).canBeReplaced()) {
                  boolean containsKey;
                  label108: {
                     flag2 = true;
                     var map = this.items2;
                     Long l = pos.asLong();
                     if (map != null) {
                        if (map.isEmpty()) {
                           containsKey = false;
                           break label108;
                        }
                     }

                     containsKey = map.containsKey(l);
                  }

                  if (!containsKey && !new AABB(pos).intersects(mc.player.getBoundingBox().expandTowards(mc.player.getDeltaMovement().scale(2.0)))) {
                     BlockPos pos2 = pos;
                     if (!this.method5867(pos)) {
                        if (!this.items18.isEmpty() && this.data == null) {
                           this.method5780(pos, rotation);
                        }

                        BlockPos below = pos.below();
                        if (!mc.level.getBlockState(below).canBeReplaced() || !this.method5867(below)) {
                           continue;
                        }

                        pos2 = below;
                     }

                     Rotation rotation2 = this.method5831(CrystalAuraModule.Type.SHIELD, pos2, this.vec3d, rotation);
                     if (rotation2 != null) {
                        this.items9.put(pos2.asLong(), Items.OBSIDIAN);
                        return new CrystalAuraModule.Data(
                           CrystalAuraModule.Type.SHIELD,
                           null,
                           pos2.immutable(),
                           rotation2,
                           0.0F,
                           0.0F,
                           200.0F,
                           true,
                           this.items18.isEmpty()
                              ? null
                              : this.items18.getFirst().getPlayerEntity()
                        );
                     }
                  }
               }
            }

            if (!flag2) {
               if (this.method5925()) {
                  this.combatLogger.method4612("SURROUND", "застроились: %s закрыт со всех сторон", method5948(this.blockPos9));
               }

               this.blockPos9 = null;
            }

            return null;
         } else {
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     "SURROUND",
                     "застройка %s: %s",
                     method5948(this.blockPos9),
                     mc.player.tickCount > this.intValue34 ? "время вышло" : (flag ? "ушли из клетки" : "обсидиана нет")
                  );
            }

            this.blockPos9 = null;
            return null;
         }
      }
   }

   private CrystalAuraModule.Data method5776(Rotation rotation) {
      Item item = this.method5810();
      if (item == null) {
         return null;
      } else {
         Set<BlockPos> poses = this.method5772();

         for (BlockPos pos : poses) {
            boolean containsKey;
            label114: {
               var map = this.items22;
               Long l = pos.asLong();
               if (map != null) {
                  if (map.isEmpty()) {
                     containsKey = false;
                     break label114;
                  }
               }

               containsKey = map.containsKey(l);
            }

            if (containsKey && mc.level.getBlockState(pos).canBeReplaced() && !this.method5777(pos)) {
               if (!this.method5867(pos)) {
                  this.method5780(pos, rotation);
               } else {
                  Rotation rotation2 = this.method5831(CrystalAuraModule.Type.SHIELD, pos, this.vec3d, rotation);
                  if (rotation2 != null) {
                     this.item = item;
                     return new CrystalAuraModule.Data(
                        CrystalAuraModule.Type.SHIELD, null, pos.immutable(), rotation2, 0.0F, 0.0F, 100.0F, true, this.items18.getFirst().getPlayerEntity()
                     );
                  }
               }
            }
         }

         CrystalAuraModule.Data data = null;

         for (BlockPos pos : poses) {
            Integer n = this.items21.get(pos.asLong());
            if (n != null && !mc.level.getBlockState(pos).canBeReplaced()) {
               if (n >= 6 && this.rotation4 == null) {
                  this.rotation4 = this.method5779(pos);
               }

               for (Direction direction : Direction.Plane.HORIZONTAL) {
                  BlockPos relative = pos.relative(direction);
                  if (!poses.contains(relative)
                     && this.method5778(pos.relative(direction.getOpposite()))
                     && mc.level.isEmptyBlock(relative)
                     && method5956(mc.level.getBlockState(relative.below()))
                     && !this.method5923(relative)
                     && this.method5867(relative)
                     && !this.method5777(relative)) {
                     Rotation rotation2 = this.method5831(CrystalAuraModule.Type.SHIELD, relative, this.vec3d, rotation);
                     if (rotation2 != null) {
                        boolean flag = n >= 7;
                        if (data == null || flag && !data.isFlag()) {
                           this.item = item;
                           data = new CrystalAuraModule.Data(
                              CrystalAuraModule.Type.SHIELD,
                              null,
                              relative.immutable(),
                              rotation2,
                              0.0F,
                              0.0F,
                              50.0F + n.intValue(),
                              flag,
                              this.items18.getFirst().getPlayerEntity()
                           );
                        }
                     }
                  }
               }
            }
         }

         if (data != null && this.method5925() && data.pos().asLong() != this.longValue2) {
            this.longValue2 = data.pos().asLong();
            this.combatLogger.method4612("ANTIMINE", "закрываем city-спот %s%s", method5948(data.pos()), data.isFlag() ? " срочно" : "");
         }

         return data;
      }
   }

   private boolean method5777(BlockPos above) {
      AABB boundingBox = mc.player.getBoundingBox();
      return new AABB(above).intersects(boundingBox.expandTowards(mc.player.getDeltaMovement().scale(2.0)).inflate(0.1));
   }

   private boolean method5778(BlockPos relative) {
      AABB boundingBox = mc.player.getBoundingBox();
      return boundingBox.intersects(new AABB(relative).deflate(0.001))
         && relative.getY() == BlockPos.containing(boundingBox.getCenter().x, boundingBox.minY + 0.5, boundingBox.getCenter().z).getY();
   }

   private Rotation method5779(BlockPos pos) {
      BlockPos below = pos.below();
      double scale = this.method5878();
      Rotation rotation = null;
      double d = Double.MAX_VALUE;
      Rotation rotation2 = this.method5897();

      for (double d2 : CONST_doubleValues) {
         for (double d3 : CONST_doubleValues) {
            Vec3 vec = new Vec3(below.getX() + d2, below.getY() + 1.0, below.getZ() + d3);
            if (!(this.vec3d.distanceToSqr(vec) > scale * scale)) {
               Rotation rotation3 = method5898(this.vec3d, vec);
               Vec3 to = this.vec3d.add(rotation3.toDirection().scale(scale));
               BlockHitResult traverseBlocks = BlockGetter.traverseBlocks(this.vec3d, to, pos, (var2x, var3x) -> {
                  if (var3x.equals(var2x)) {
                     return null;
                  } else {
                     BlockState blockState = mc.level.getBlockState(var3x);
                     return blockState.getShape(mc.level, var3x).clip(this.vec3d, to, var3x);
                  }
               }, pos2 -> null);
               double d4;
               if (traverseBlocks != null && traverseBlocks.getBlockPos().equals(below) && traverseBlocks.getDirection() == Direction.UP && (d4 = rotation2.wrappedDeltaTo(rotation3).length()) < d
                  )
                {
                  d = d4;
                  rotation = rotation3;
               }
            }
         }
      }

      return rotation;
   }

   private void method5780(BlockPos pos, Rotation rotation) {
      if (this.data == null && this.method5920()) {
         for (EndCrystal endCrystalEntity : mc.level.getEntitiesOfClass(EndCrystal.class, new AABB(pos), var1x -> {
            if (var1x.isAlive()) {
               boolean var2x;
               label20: {
                  var map = this.items6;
                  Integer id = var1x.getId();
                  if (map != null) {
                     if (map.isEmpty()) {
                        var2x = false;
                        break label20;
                     }
                  }

                  var2x = map.containsKey(id);
               }

               if (!var2x) {
                  return true;
               }
            }

            return false;
         })) {
            AABB box = this.box != null ? this.box : mc.player.getBoundingBox();
            float f;
            if (!((f = this.class35.method5452(endCrystalEntity.position(), 6.0F, mc.player, box, null, null)) >= this.method5830())
               && this.method5871(f)
               && !(method5962(mc.player.getEyePosition(), endCrystalEntity.getBoundingBox()) > this.method5877() * this.method5877())) {
               Rotation rotation2 = this.method5835(this.vec3d, rotation, endCrystalEntity) != null ? rotation : this.method5834(this.vec3d, rotation, endCrystalEntity);
               if (rotation2 != null) {
                  this.data = new CrystalAuraModule.Data(
                     CrystalAuraModule.Type.BREAK, endCrystalEntity, endCrystalEntity.blockPosition().below(), rotation2, 0.0F, f, 100.0F, true, this.items18.getFirst().getPlayerEntity()
                  );
                  this.items16.addFirst(this.data);
                  if (this.method5925()) {
                     this.combatLogger.method4612("ANTIMINE", "в дыре %s кристалл #%d — сбиваем, урон по нам %.1f", method5948(pos), endCrystalEntity.getId(), f);
                  }

                  return;
               }
            }
         }
      }
   }

   private CrystalAuraModule.Data method5781(Vec3 eyePosition, Rotation rotation) {
      this.intValue23 = 0;
      this.intValue24 = 0;
      this.intValue25 = 0;
      EndCrystal endCrystalEntity = this.data == null ? null : this.data.getEndCrystalEntity();
      boolean flag = this.method5915(stack -> stack.is(Items.END_CRYSTAL));
      boolean flag2 = this.entry4.isEnabled() && this.method5915(stack -> stack.is(Items.OBSIDIAN));
      boolean flag3;
      boolean flag4 = (flag3 = this.method5873()) && this.method5915(stack -> stack.is(Items.GLOWSTONE));
      boolean flag5 = flag3 && flag4 && this.method5915(stack -> stack.is(Items.RESPAWN_ANCHOR));
      boolean flag6 = this.method5921();
      boolean flag7 = this.entry.isEnabled() && this.data2 == null && this.data == null;
      boolean flag8 = this.method5782();
      boolean flag9 = this.entry14.isEnabled() || this.entry15.isEnabled();
      double d = this.method5878();
      double d2 = this.method5877();
      BlockPos blockPos3 = this.blockPos3 == null
            || !(mc.gameMode.destroyProgress >= 0.8F)
            || !mc.player.getOffhandItem().is(Items.END_CRYSTAL) && this.data3.getType() != org.zenith.utility.misc.Data3.Type.SILENT
         ? null
         : this.blockPos3;
      HashSet hashSet = new HashSet();
      var set = this.entry14.isEnabled() ? this.method5793(hashSet) : Set.of();
      var set2 = this.entry15.isEnabled() ? this.method5796() : Set.of();
      ArrayList<CrystalAuraModule.Data13> data13s = new ArrayList();
      ArrayList arrayList = new ArrayList();

      for (BlockPos pos : this.method5816(eyePosition, d)) {
         if (this.method5923(pos)) {
            this.method5937("спот в чёрном списке (сервер отказал / кристалл не появился)");
         } else {
            BlockState blockState = mc.level.getBlockState(pos);
            if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
               if (flag3) {
                  int n;
                  CrystalAuraModule.Type type = (n = blockState.getValue(RespawnAnchorBlock.CHARGE)) > 0
                     ? CrystalAuraModule.Type.ANCHOR_EXPLODE
                     : (flag4 ? CrystalAuraModule.Type.ANCHOR_CHARGE : null);
                  if (type != null) {
                     this.method5814(data13s, pos, type, Vec3.atCenterOf(pos), 5.0F, pos, null, n > 0 ? 3.0F : 1.5F);
                  }
               }
            } else if (!flag6) {
               this.method5937("задержка установки");
            } else {
               if (flag && set.contains(pos.asLong())) {
                  this.method5794(data13s, pos, blockState, hashSet, endCrystalEntity, eyePosition, d2);
               }

               boolean flag10 = method5962(eyePosition, method5961(pos)) <= d2 * d2;
               if (!method5956(blockState)) {
                  if (set2.contains(pos.asLong())
                     && flag2
                     && this.method5867(pos)
                     && mc.level.isEmptyBlock(pos.above())
                     && this.method5850(pos, Blocks.OBSIDIAN.defaultBlockState()) > 0) {
                     this.method5814(data13s, pos, CrystalAuraModule.Type.OBSIDIAN, method5959(pos), 6.0F, pos, null, -6.0F);
                  } else {
                     if (flag2 && flag10 && this.method5867(pos) && mc.level.isEmptyBlock(pos.above()) && this.method5866(method5960(pos), endCrystalEntity)) {
                        this.method5814(
                           data13s, pos, CrystalAuraModule.Type.OBSIDIAN, method5959(pos), 6.0F, null, pos, pos.equals(this.blockPos2) ? -2.5F : -4.0F
                        );
                     }

                     if (flag5 && this.method5867(pos)) {
                        this.method5814(data13s, pos, CrystalAuraModule.Type.ANCHOR_PLACE, Vec3.atCenterOf(pos), 5.0F, pos, null, -3.0F);
                     }
                  }
               } else if (!flag) {
                  this.method5937("нет кристаллов");
               } else if (!flag10) {
                  this.method5937("основание: кристалл был бы дальше дальности удара");
               } else {
                  boolean containsKey;
                  label578: {
                     var items = this.items;
                     Long l = pos.asLong();
                     if (items != null) {
                        if (items.isEmpty()) {
                           containsKey = false;
                           break label578;
                        }
                     }

                     containsKey = items.containsKey(l);
                  }

                  if (containsKey) {
                     this.method5937("основание: уже ставим, ждём подтверждения");
                  } else {
                     BlockPos above = pos.above();
                     BlockState blockState2 = mc.level.getBlockState(above);
                     if (blockState2.isAir()) {
                        if (!this.method5865(pos, endCrystalEntity)) {
                           this.method5937("основание: место занято сущностью или целью");
                           EndCrystal endCrystal = this.entry17.isEnabled() ? this.method5800(pos, endCrystalEntity) : null;
                           if (endCrystal != null) {
                              arrayList.add(new CrystalAuraModule.Data11(pos.immutable(), endCrystal));
                           }
                        } else {
                           BlockPos pos2 = blockPos3 != null && blockPos3.closerThan(above, 2.5) ? blockPos3 : null;
                           this.method5814(
                              data13s, pos, CrystalAuraModule.Type.CRYSTAL, method5959(pos), 6.0F, pos2, null, pos.equals(this.blockPos2) ? 1.5F : 0.0F
                           );
                           if (set2.contains(pos.asLong())) {
                              this.method5814(data13s, pos, CrystalAuraModule.Type.CRYSTAL, method5959(pos), 6.0F, pos, null, -3.0F);
                           }
                        }
                     } else if (!this.entry5.isEnabled()) {
                        this.method5937("основание: над ним блок");
                     } else {
                        int intValue;
                        if (this.method5866(method5960(pos), endCrystalEntity) && (intValue = this.method5850(above, blockState2)) > 0) {
                           this.method5815(data13s, above, CrystalAuraModule.Type.DIG, method5959(pos), 6.0F, above, null, this.method5798(above, intValue), intValue);
                        }
                     }
                  }
               }
            }
         }
      }

      if (flag6 && flag9) {
         this.method5797(data13s, eyePosition, d);
      }

      this.intValue23 = data13s.size();
      if (data13s.isEmpty()) {
         return null;
      } else {
         data13s.sort(Comparator.comparingDouble(data13 -> -data13.getFloatValue3()));
         ArrayList<CrystalAuraModule.Data14> data14s = new ArrayList();
         ArrayList<CrystalAuraModule.Data14> data14s2 = new ArrayList();
         EnumMap map = new EnumMap<>(CrystalAuraModule.Type.class);
         this.items13.values().removeIf(n -> n < mc.player.tickCount);

         for (CrystalAuraModule.Data13 data13 : data13s) {
            boolean flag10;
            if (!(flag10 = data13.getType() == this.type3 && data13.pos().equals(this.blockPos4) || this.method5789(data13))) {
               boolean containsKey;
               label530: {
                  var map2 = this.items13;
                  Long l = data13.pos().asLong() * 8L + data13.getType().ordinal();
                  if (map2 != null) {
                     if (map2.isEmpty()) {
                        containsKey = false;
                        break label530;
                     }
                  }

                  containsKey = map2.containsKey(l);
               }

               if (containsKey) {
                  this.method5937(data13.getType() + ": грань не видна из глаз (недавно проверяли)");
                  continue;
               }
            }

            if ((flag10 ? 0 : (Integer)map.merge(data13.getType(), 1, (a, b) -> (Integer)a + (Integer)b)) > method5792(data13.getType())) {
               this.method5937(data13.getType() + ": за пределами квоты точного счёта (" + method5792(data13.getType()) + ")");
            } else {
               boolean flag11 = data13.getType() == CrystalAuraModule.Type.ANCHOR_CHARGE || data13.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE;
               CrystalAuraModule.Data12 data12 = this.method5822(
                  data13.getVec3d(),
                  data13.getFloatValue(),
                  data13.getBlockPos2(),
                  data13.getBlockPos3(),
                  flag11,
                  data13.getType() == CrystalAuraModule.Type.DIG ? Math.max(0, data13.getIntValue()) : 0
               );
               if (data12 == null) {
                  this.method5934(data13, null, 0.0F, this.text2);
               } else if (data13.getType() == CrystalAuraModule.Type.DIG && data13.getIntValue() > 2 && !this.method5795(data12.getPlayerEntity())) {
                  this.method5934(data13, data12, 0.0F, "цель двигается — копать бессмысленно");
               } else {
                  boolean flag12 = !data12.isFlag();
                  boolean flag13 = data13.getType() == CrystalAuraModule.Type.CRYSTAL || data13.getType() == CrystalAuraModule.Type.OBSIDIAN;
                  if (flag12 && flag8 && method5783(data13.getType()) == CrystalAuraModule.Type2.CRYSTALS) {
                     this.method5934(data13, data12, 0.0F, "рано, а к этому окну уже стоит наш якорь");
                  } else if (!flag12 || flag7 && flag13) {
                     if (data13.getIntValue() > 10 && data12.getFloatValue() < this.mineDamage.getValue3()) {
                        this.method5934(
                           data13,
                           data12,
                           0.0F,
                           String.format(
                              Locale.ROOT,
                              "копать %d т ради %.1f < порога копания %.1f",
                              data13.getIntValue(),
                              data12.getFloatValue(),
                              this.mineDamage.getValue3()
                           )
                        );
                        if (data12.isFlag() && (data12.getFloatValue() >= Math.max(6.0F, this.minDamage.getValue3()) || this.method5790(data12))) {
                           data14s2.add(new CrystalAuraModule.Data14(data13, data12, data12.value() + data13.getFloatValue2()));
                        }
                     } else {
                        float floatValue = data12.value() + data13.getFloatValue2() - (flag12 ? 6.0F : 0.0F);
                        boolean flag14 = !data12.isFlag()
                           && data13.getType() != CrystalAuraModule.Type.OBSIDIAN
                           && this.method5788(data12.getPlayerEntity())
                           && floatValue + 6.0F > 0.0F;
                        if (!(floatValue <= 0.0F) || flag14 || data12.isFlag() && this.method5790(data12)) {
                           floatValue -= this.method5785(data13, data12, floatValue);
                           if (data13.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE) {
                              boolean containsKey;
                              label480: {
                                 var map2 = this.items11;
                                 Long l = data13.pos().asLong();
                                 if (map2 != null) {
                                    if (map2.isEmpty()) {
                                       containsKey = false;
                                       break label480;
                                    }
                                 }

                                 containsKey = map2.containsKey(l);
                              }

                              if (containsKey) {
                                 floatValue += 0.8F * data12.getFloatValue2() * this.method5828();
                              }
                           }

                           if (flag10) {
                              floatValue += Math.max(3.0F, 0.2F * Math.abs(floatValue));
                           }

                           this.method5934(data13, data12, floatValue, null);
                           data14s.add(new CrystalAuraModule.Data14(data13, data12, floatValue));
                        } else {
                           this.method5934(data13, data12, floatValue, "в минус: по нам больше, чем по цели");
                        }
                     }
                  } else {
                     this.method5934(
                        data13,
                        data12,
                        0.0F,
                        flag7 ? "рано, а предустановка только для кристалла/обсы/якоря" : "рано, предустановка выключена или уже есть ожидание/break"
                     );
                  }
               }
            }
         }

         data14s.sort(Comparator.comparingDouble(data14 -> -data14.value()));
         this.intValue24 = data14s.size();
         CrystalAuraModule.Data data = null;
         int n = 0;
         HashMap map2 = new HashMap();

         for (CrystalAuraModule.Data14 data14 : data14s) {
            CrystalAuraModule.Data13 data13;
            long key;
            boolean containsKey;
            label453: {
               data13 = data14.getData13();
               key = data13.pos().asLong() * 8L + data13.getType().ordinal();
               Long l = key;
               if (map2 != null) {
                  if (map2.isEmpty()) {
                     containsKey = false;
                     break label453;
                  }
               }

               containsKey = map2.containsKey(l);
            }

            Rotation rotation2;
            if (containsKey) {
               rotation2 = (Rotation)map2.get(key);
            } else {
               label446: {
                  var map3 = this.items13;
                  Long l = key;
                  if (map3 != null) {
                     if (map3.isEmpty()) {
                        containsKey = false;
                        break label446;
                     }
                  }

                  containsKey = map3.containsKey(l);
               }

               if (containsKey && !this.method5789(data13)) {
                  continue;
               }

               if (n++ >= 12) {
                  break;
               }

               rotation2 = this.method5831(data13.getType(), data13.pos(), this.vec3d, rotation);
               map2.put(key, rotation2);
               if (rotation2 == null) {
                  this.items13.put(key, mc.player.tickCount + 3);
               }
            }

            if (rotation2 != null) {
               if (data13.getType() == CrystalAuraModule.Type.CRYSTAL || data13.getType() == CrystalAuraModule.Type.OBSIDIAN) {
                  this.blockPos2 = data13.pos();
               }

               if (data13.getType() == CrystalAuraModule.Type.DIG && data13.pos().equals(this.blockPos3)) {
                  this.intValue20 = mc.player.tickCount;
               }

               CrystalAuraModule.Data12 data12;
               data = new CrystalAuraModule.Data(
                  data13.getType(),
                  null,
                  data13.pos(),
                  rotation2,
                  (data12 = data14.getData12()).getFloatValue(),
                  data12.getFloatValue2(),
                  data14.value(),
                  data12.isFlag(),
                  data12.getPlayerEntity()
               );
               break;
            }

            this.intValue25++;
            this.method5936(
               String.format(
                  Locale.ROOT,
                  "%s %s: урон %.1f по %s, но грань не видна из глаз / дальше %.2f б",
                  data13.getType(),
                  method5948(data13.pos()),
                  data14.getData12().getFloatValue(),
                  method5947(data14.getData12().getPlayerEntity()),
                  this.method5878()
               ),
               data14.getData12().getFloatValue()
            );
            if (this.jsonArray2 != null) {
               this.jsonArray2.add(method5940("aimFail", method5948(data13.pos()), "kind", data13.getType().name()));
            }
         }

         if (data == null && this.data == null && (!data14s2.isEmpty())) {
            data14s2.sort(Comparator.comparingDouble(data14 -> -data14.value()));

            for (CrystalAuraModule.Data14 data14 : data14s2) {
               Rotation rotation2 = this.method5831(CrystalAuraModule.Type.DIG, data14.getData13().pos(), this.vec3d, rotation);
               if (rotation2 != null) {
                  if (data14.getData13().pos().equals(this.blockPos3)) {
                     this.intValue20 = mc.player.tickCount;
                  }

                  CrystalAuraModule.Data12 data12;
                  data = new CrystalAuraModule.Data(
                     CrystalAuraModule.Type.DIG,
                     null,
                     data14.getData13().pos(),
                     rotation2,
                     (data12 = data14.getData12()).getFloatValue(),
                     data12.getFloatValue2(),
                     data14.value(),
                     true,
                     data12.getPlayerEntity()
                  );
                  break;
               }
            }
         }

         if (data != null) {
            this.method5787(data);
            if (data.getType() != this.type || !data.pos().equals(this.blockPos)) {
               this.type = data.getType();
               this.blockPos = data.pos().immutable();
            }

            this.intValue3 = mc.player.tickCount;
         } else if (this.blockPos != null && mc.player.tickCount - this.intValue3 > 10) {
            this.blockPos = null;
            this.type = null;
         }

         if (!arrayList.isEmpty()) {
            this.method5799(arrayList, data == null ? 0.0F : data.getFloatValue3(), rotation);
         }

         if (data == null) {
            return null;
         } else {
            this.items17.add(data);
            if (data.isFlag() && data.getType() != CrystalAuraModule.Type.DIG) {
               for (CrystalAuraModule.Data14 data14 : data14s) {
                  if (this.items17.size() >= 10) {
                     break;
                  }

                  CrystalAuraModule.Data13 data13 = data14.getData13();
                  if (data14.getData12().isFlag()
                     && data13.getType() != CrystalAuraModule.Type.DIG
                     && !data13.pos().equals(data.pos())
                     && !this.method5786(data13, data14.getData12(), data)) {
                     CrystalAuraModule.Data12 data12;
                     this.items17
                        .add(
                           new CrystalAuraModule.Data(
                              data13.getType(),
                              null,
                              data13.pos(),
                              null,
                              (data12 = data14.getData12()).getFloatValue(),
                              data12.getFloatValue2(),
                              data14.value(),
                              true,
                              data12.getPlayerEntity()
                           )
                        );
                  }
               }
            }

            if (this.blockPos4 != null && !data.pos().equals(this.blockPos4)) {
               for (CrystalAuraModule.Data14 data14 : data14s) {
                  CrystalAuraModule.Data13 data13 = data14.getData13();
                  if (data13.getType() == this.type3
                     && data13.pos().equals(this.blockPos4)
                     && data14.getData12().isFlag()
                     && !this.method5786(data13, data14.getData12(), data)) {
                     this.items17.removeIf(var1x -> var1x.getType() == data13.getType() && var1x.pos().equals(data13.pos()));
                     CrystalAuraModule.Data12 data12;
                     this.items17
                        .add(
                           1,
                           new CrystalAuraModule.Data(
                              data13.getType(),
                              null,
                              data13.pos(),
                              null,
                              (data12 = data14.getData12()).getFloatValue(),
                              data12.getFloatValue2(),
                              data14.value(),
                              true,
                              data12.getPlayerEntity()
                           )
                        );
                     break;
                  }
               }
            }

            return data;
         }
      }
   }

   private boolean method5782() {
      double d = this.method5878();
      var iterator = this.items11.keySet().iterator();

      while (iterator.hasNext()) {
         BlockPos pos = BlockPos.of((Long)iterator.next());
         if (mc.level.getBlockState(pos).is(Blocks.RESPAWN_ANCHOR) && method5962(this.vec3d, new AABB(pos)) <= d * d) {
            return true;
         }
      }

      return false;
   }

   private static CrystalAuraModule.Type2 method5783(CrystalAuraModule.Type type) {
      return switch (type) {
         case CRYSTAL, OBSIDIAN, DIG -> CrystalAuraModule.Type2.CRYSTALS;
         default -> null;
         case ANCHOR_PLACE, ANCHOR_CHARGE, ANCHOR_EXPLODE -> CrystalAuraModule.Type2.ANCHOR;
      };
   }

   private CrystalAuraModule.Type2 method5784() {
      return this.type2 != null && this.method5896(this.intValue) <= 40 ? this.type2 : null;
   }

   private float method5785(CrystalAuraModule.Data13 data13, CrystalAuraModule.Data12 data12, float floatValue) {
      CrystalAuraModule.Type2 type2 = this.method5784();
      CrystalAuraModule.Type2 type22 = method5783(data13.getType());
      if (type2 != null && type22 != null && type22 != type2 && (!data12.isFlag() || !this.method5790(data12))) {
         if (data13.getType() == CrystalAuraModule.Type.ANCHOR_CHARGE || data13.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE) {
            boolean containsKey;
            label25: {
               var map = this.items11;
               Long l = data13.pos().asLong();
               if (map != null) {
                  if (map.isEmpty()) {
                     containsKey = false;
                     break label25;
                  }
               }

               containsKey = map.containsKey(l);
            }

            if (containsKey) {
               return 0.0F;
            }
         }

         return Math.max(3.0F, 0.25F * Math.abs(floatValue));
      } else {
         return 0.0F;
      }
   }

   private boolean method5786(CrystalAuraModule.Data13 data13, CrystalAuraModule.Data12 data12, CrystalAuraModule.Data data) {
      CrystalAuraModule.Type2 type2 = method5783(data.getType());
      CrystalAuraModule.Type2 type22 = method5783(data13.getType());
      return type2 != null && type22 != null && type2 != type22 && !this.method5790(data12);
   }

   private void method5787(CrystalAuraModule.Data data) {
      CrystalAuraModule.Type2 type2 = method5783(data.getType());
      if (type2 != null) {
         CrystalAuraModule.Type2 type22 = this.method5784();
         if (type2 != type22 && this.method5925() && type22 != null) {
            this.combatLogger
               .method4612(
                  "WEAPON",
                  "%s -> %s: %s %s, ценность %.1f по %s",
                  type22 == CrystalAuraModule.Type2.CRYSTALS ? "кристаллы" : "якорь",
                  type2 == CrystalAuraModule.Type2.CRYSTALS ? "кристаллы" : "якорь",
                  data.getType(),
                  method5948(data.pos()),
                  data.getFloatValue3(),
                  method5947(data.getPlayerEntity())
               );
         }

         this.type2 = type2;
         this.intValue = mc.player.tickCount;
      }
   }

   private boolean method5788(Player playerEntity) {
      if (playerEntity == null) {
         return false;
      } else if (playerEntity.getInvulnerableTime() > 10) {
         return true;
      } else {
         CrystalAuraModule.Entry entry = this.items14.get(playerEntity.getId());
         return entry != null && entry.method5969(mc.player.tickCount + this.method5818());
      }
   }

   private boolean method5789(CrystalAuraModule.Data13 data13) {
      return this.blockPos != null && data13.pos().equals(this.blockPos)
         ? data13.getType() == this.type
            || this.type == CrystalAuraModule.Type.OBSIDIAN && data13.getType() == CrystalAuraModule.Type.CRYSTAL
            || this.type == CrystalAuraModule.Type.ANCHOR_PLACE
               && (data13.getType() == CrystalAuraModule.Type.ANCHOR_CHARGE || data13.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE)
            || this.type == CrystalAuraModule.Type.ANCHOR_CHARGE && data13.getType() == CrystalAuraModule.Type.ANCHOR_EXPLODE
         : false;
   }

   private boolean method5790(CrystalAuraModule.Data12 data12) {
      Player playerEntity = data12.getPlayerEntity();
      return playerEntity != null && data12.getFloatValue() >= method5902(playerEntity) && !method5791(playerEntity);
   }

   private static boolean method5791(Player playerEntity) {
      return playerEntity.getOffhandItem().is(Items.TOTEM_OF_UNDYING) || playerEntity.getMainHandItem().is(Items.TOTEM_OF_UNDYING);
   }

   private static int method5792(CrystalAuraModule.Type type) {
      return switch (type) {
         case BREAK, SHIELD -> 0;
         case CRYSTAL -> 40;
         case OBSIDIAN -> 16;
         case DIG -> 16;
         case ANCHOR_PLACE -> 28;
         case ANCHOR_CHARGE, ANCHOR_EXPLODE -> 8;
      };
   }

   private Set<Long> method5793(Set<Long> longs) {
      HashSet<Long> longs2 = new HashSet<>();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         AABB box = data5.getBox();

         for (int i = 0; i <= 1; i++) {
            double y = box.minY + 0.001 + i;
            longs.add(BlockPos.containing(box.minX + 0.001, y, box.minZ + 0.001).asLong());
            longs.add(BlockPos.containing(box.maxX - 0.001, y, box.minZ + 0.001).asLong());
            longs.add(BlockPos.containing(box.minX + 0.001, y, box.maxZ - 0.001).asLong());
            longs.add(BlockPos.containing(box.maxX - 0.001, y, box.maxZ - 0.001).asLong());
         }
      }

      var iterator = longs.iterator();

      while (iterator.hasNext()) {
         BlockPos pos = BlockPos.of((Long)iterator.next());

         for (Direction direction : Direction.Plane.HORIZONTAL) {
            long l = pos.relative(direction).asLong();
            if (!longs.contains(l)) {
               longs2.add(l);
            }
         }
      }

      return longs2;
   }

   private void method5794(List<CrystalAuraModule.Data13> data13s, BlockPos pos, BlockState blockState, Set<Long> longs, EndCrystal endCrystalEntity, Vec3 eyePosition, double d) {
      if (!blockState.canBeReplaced()) {
         boolean containsKey;
         label49: {
            var map = this.items2;
            Long l = pos.asLong();
            if (map != null) {
               if (map.isEmpty()) {
                  containsKey = false;
                  break label49;
               }
            }

            containsKey = map.containsKey(l);
         }

         if (!containsKey) {
            int intValue;
            if ((intValue = this.method5850(pos, blockState)) <= 0) {
               return;
            }

            float f = this.method5798(pos, intValue);
            BlockPos below = pos.below();
            if (method5956(mc.level.getBlockState(below)) && this.method5866(method5960(below), endCrystalEntity) && method5962(eyePosition, method5961(below)) <= d * d) {
               this.method5815(data13s, pos, CrystalAuraModule.Type.DIG, method5959(below), 6.0F, pos, null, f, intValue);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
               BlockPos relative = pos.relative(direction);
               if (!longs.contains(relative.asLong())) {
                  BlockPos below2 = relative.below();
                  if (method5956(mc.level.getBlockState(below2))
                     && mc.level.isEmptyBlock(relative)
                     && this.method5865(below2, endCrystalEntity)
                     && method5962(eyePosition, method5961(below2)) <= d * d) {
                     this.method5815(data13s, pos, CrystalAuraModule.Type.DIG, method5959(below2), 6.0F, pos, null, f - 1.0F, intValue);
                  }
               }
            }

            return;
         }
      }
   }

   private boolean method5795(Player playerEntity) {
      if (playerEntity == null) {
         return false;
      } else if (this.method5945(this.method5903(playerEntity))) {
         return true;
      } else {
         CrystalAuraModule.Entry entry = this.items14.get(playerEntity.getId());
         return entry != null && playerEntity.onGround() && entry.method5971().horizontalDistanceSqr() <= 0.0064;
      }
   }

   private Set<Long> method5796() {
      HashSet<Long> longs = new HashSet<>();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         CrystalAuraModule.Entry entry = this.items14.get(data5.getPlayerEntity().getId());
         if (entry != null && !(entry.method5971().horizontalDistanceSqr() > 0.0064) && data5.getPlayerEntity().onGround()) {
            AABB box = data5.getBox();
            longs.add(BlockPos.containing((box.minX + box.maxX) * 0.5, box.minY + 2.0 + 0.001, (box.minZ + box.maxZ) * 0.5).asLong());
         }
      }

      return longs;
   }

   private void method5797(List<CrystalAuraModule.Data13> data13s, Vec3 eyePosition, double d) {
      HashSet<Integer> integers = new HashSet<>();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         for (EndCrystal endCrystal : mc.level.getEntitiesOfClass(EndCrystal.class, data5.getBox().inflate(3.0), var1x -> {
            if (var1x.isAlive()) {
               boolean var2x;
               label20: {
                  var map = this.items6;
                  Integer id = var1x.getId();
                  if (map != null) {
                     if (map.isEmpty()) {
                        var2x = false;
                        break label20;
                     }
                  }

                  var2x = map.containsKey(id);
               }

               if (!var2x) {
                  return true;
               }
            }

            return false;
         })) {
            if (integers.add(endCrystal.getId())) {
               BlockPos below = endCrystal.blockPosition().below();
               BlockState blockState = mc.level.getBlockState(below);
               int intValue;
               if (!blockState.canBeReplaced()
                  && !this.method5923(below)
                  && !(method5962(eyePosition, new AABB(below)) > d * d)
                  && (intValue = this.method5850(below, blockState)) > 0) {
                  this.method5815(data13s, below, CrystalAuraModule.Type.DIG, endCrystal.position(), 6.0F, below, null, this.method5798(below, intValue) + 1.0F, intValue);
               }
            }
         }
      }
   }

   private float method5798(BlockPos above, int intValue) {
      float f = -3.0F - intValue * 0.08F;
      if (above.equals(this.blockPos3)) {
         f += 4.0F + 8.0F * mc.gameMode.destroyProgress;
      }

      return f;
   }

   private void method5799(List<CrystalAuraModule.Data11> data11s, float f, Rotation rotation) {
      if (this.data == null && this.method5920()) {
         double d = this.method5877();
         Vec3 eyePosition = mc.player.getEyePosition();

         for (int i = 0; i < data11s.size() && i < 6; i++) {
            CrystalAuraModule.Data11 data11 = (CrystalAuraModule.Data11)data11s.get(i);
            CrystalAuraModule.Data12 data12 = this.method5821(method5959(data11.pos()), 6.0F, null, null, false);
            if (data12 != null && data12.isFlag() && !(data12.value() - f < 4.0F)) {
               EndCrystal endCrystalEntity = data11.getEndCrystalEntity();
               if (!endCrystalEntity.blockPosition().below().equals(data11.pos())
                  && !(method5962(eyePosition, endCrystalEntity.getBoundingBox()) > d * d)
                  && (!this.items10.contains(endCrystalEntity.getId()) || this.method5821(endCrystalEntity.position(), 6.0F, null, null, true) == null)) {
                  AABB box = this.box != null ? this.box : mc.player.getBoundingBox();
                  float f2;
                  if (!((f2 = this.class35.method5452(endCrystalEntity.position(), 6.0F, mc.player, box, null, null)) > this.maxSelfDamage.getValue3())
                     && !(f2 >= this.method5830())) {
                     Rotation rotation2 = this.method5835(this.vec3d, rotation, endCrystalEntity) != null ? rotation : this.method5834(this.vec3d, rotation, endCrystalEntity);
                     if (rotation2 != null) {
                        this.data = new CrystalAuraModule.Data(
                           CrystalAuraModule.Type.BREAK,
                           endCrystalEntity,
                           endCrystalEntity.blockPosition().below(),
                           rotation2,
                           0.0F,
                           f2,
                           data12.value() - 4.0F,
                           true,
                           data12.getPlayerEntity()
                        );
                        this.items16.addFirst(this.data);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private EndCrystal method5800(BlockPos pos, Entity endCrystalEntity) {
      AABB aabb = method5960(pos);

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         if (data5.getBox().intersects(aabb)) {
            return null;
         }
      }

      EndCrystal endCrystal = null;

      for (Entity entity : mc.level.getEntities((Entity) null, aabb, var2x -> {
         if (!var2x.isSpectator() && var2x != endCrystalEntity && !this.method5868(var2x)) {
            if (!(var2x instanceof EndCrystal)) {
               return true;
            }

            boolean var3x;
            label33: {
               var map = this.items6;
               Integer id = var2x.getId();
               if (map != null) {
                  if (map.isEmpty()) {
                     var3x = false;
                     break label33;
                  }
               }

               var3x = map.containsKey(id);
            }

            if (!var3x) {
               return true;
            }
         }

         return false;
      })) {
         if (!(entity instanceof EndCrystal endCrystal2) || endCrystal != null) {
            return null;
         }

         endCrystal = endCrystal2;
      }

      return endCrystal;
   }

   private CrystalAuraModule.Data3 method5801() {
      AABB box2 = this.box2 != null ? this.box2 : mc.player.getBoundingBox();
      float f = mc.player.getHealth() + mc.player.getAbsorptionAmount();
      BlockPos containing = BlockPos.containing((box2.minX + box2.maxX) * 0.5, box2.minY + 0.001, (box2.minZ + box2.maxZ) * 0.5);
      ArrayList<CrystalAuraModule.Data10> items = new ArrayList();

      for (int i = -5; i <= 5; i++) {
         for (int j = -5; j <= 5; j++) {
            for (int k = -3; k <= 2; k++) {
               BlockPos offset = containing.offset(i, k, j);
               if (method5956(mc.level.getBlockState(offset))) {
                  BlockPos above = offset.above();
                  BlockPos blockPos2 = null;
                  if (!mc.level.isEmptyBlock(above)) {
                     boolean containsKey;
                     label148: {
                        var map = this.items20;
                        Long l = above.asLong();
                        if (map != null) {
                           if (map.isEmpty()) {
                              containsKey = false;
                              break label148;
                           }
                        }

                        containsKey = map.containsKey(l);
                     }

                     if (!containsKey) {
                        continue;
                     }

                     blockPos2 = above;
                  } else if (!this.method5811(offset)) {
                     continue;
                  }

                  if (this.method5812(offset)) {
                     Vec3 position = method5959(offset);
                     float floatValue = this.class35.method5452(position, 6.0F, mc.player, box2, blockPos2, null);
                     if (blockPos2 == null) {
                        BlockPos pos = this.method5813(position);
                        if (pos != null) {
                           floatValue = Math.max(floatValue, this.class35.method5452(position, 6.0F, mc.player, box2, pos, null) * 0.9F);
                        }
                     }

                     if (!(floatValue < 1.0F)) {
                        this.floatValue2 = Math.max(this.floatValue2, floatValue);
                        boolean flag = this.items4.getOrDefault(offset.asLong(), -32768 << 16 | 0) >= mc.player.tickCount - 60;
                        items.add(new CrystalAuraModule.Data10(offset.immutable(), position, blockPos2, floatValue, 6.0F, flag));
                     }
                  }
               }
            }
         }
      }

      this.method5808(items, box2, containing, this.method5807());

      for (EndCrystal endCrystal : mc.level.getEntitiesOfClass(EndCrystal.class, box2.inflate(6.0), var1x -> {
         if (var1x.isAlive() && !this.items10.contains(var1x.getId())) {
            boolean var2x;
            label21: {
               var var10000x = this.items6;
               Integer var10001x = var1x.getId();
               if (var10000x != null) {
                  if (var10000x.isEmpty()) {
                     var2x = false;
                     break label21;
                  }
               }

               var2x = var10000x.containsKey(var10001x);
            }

            if (!var2x) {
               return true;
            }
         }

         return false;
      })) {
         Vec3 position = endCrystal.position();
         float floatValue;
         if (!((floatValue = this.class35.method5452(position, 6.0F, mc.player, box2, null, null)) < 1.0F)) {
            this.floatValue2 = Math.max(this.floatValue2, floatValue);
            items.add(new CrystalAuraModule.Data10(endCrystal.blockPosition().below().immutable(), position, null, floatValue, 6.0F, true));
         }
      }

      Item item = this.method5810();
      if (item != null && this.method5921() && (!items.isEmpty())) {
         items.sort(Comparator.comparingDouble(data10 -> -data10.getFloatValue()));
         float floatValue = ((CrystalAuraModule.Data10)items.getFirst()).getFloatValue();
         float f2 = 0.0F;

         for (CrystalAuraModule.Data10 data10 : items) {
            if (data10.isFlag()) {
               f2 = Math.max(f2, data10.getFloatValue());
            }
         }

         float f3 = 0.0F;

         for (CrystalAuraModule.Data10 data10 : items) {
            if (!data10.isFlag()) {
               f3 = Math.max(f3, data10.getFloatValue());
            }
         }

         boolean flag = this.method5806() && f3 >= f - 1.0F || f2 >= Math.max(this.shieldDamage.getValue3(), f * 0.4F) || f2 >= f - 1.0F;
         this.method5933(items, flag);
         return !flag && floatValue < this.shieldDamage.getValue3()
            ? null
            : new CrystalAuraModule.Data3(items, box2, containing, item, flag, f2 >= Math.max(this.shieldDamage.getValue3(), f * 0.4F));
      } else {
         return null;
      }
   }

   private CrystalAuraModule.Data method5802(CrystalAuraModule.Data3 data3, Rotation rotation, CrystalAuraModule.Data data) {
      var items = data3.getItems();
      if (data3.isFlag2()) {
         items = items.stream().filter(CrystalAuraModule.Data10::isFlag).toList();
      }

      List<CrystalAuraModule.Data10> subList = items.subList(0, Math.min(4, items.size()));
      float floatValue = ((CrystalAuraModule.Data10)items.getFirst()).getFloatValue();
      float f = items.size() > 4 ? ((CrystalAuraModule.Data10)items.get(4)).getFloatValue() : 0.0F;
      boolean flag = data3.isFlag();
      AABB box = data3.getBox();
      Vec3 eyePosition = mc.player.getEyePosition();
      LinkedHashSet<BlockPos> poses = new LinkedHashSet();

      for (CrystalAuraModule.Data10 sub : subList) {
         if (sub.getBlockPos2() == null && (flag || !this.method5803(sub.getBlockPos()))) {
            poses.add(sub.getBlockPos().above());
         }
      }

      Vec3 vec = this.method5804(data3.getBlockPos());
      BlockPos above = data3.getBlockPos().above();

      for (Direction direction : Direction.Plane.HORIZONTAL) {
         if (flag || vec == null || !(direction.getStepX() * vec.x + direction.getStepZ() * vec.z > 0.3)) {
            poses.add(data3.getBlockPos().relative(direction));
            poses.add(above.relative(direction));
         }
      }

      BlockPos pos = null;
      Rotation rotation2 = null;
      float floatValue2 = floatValue;
      float f2 = 0.0F;

      for (BlockPos pos2 : poses) {
         boolean flag2;
         if (!this.method5923(pos2)
            && this.method5867(pos2)
            && !this.method5777(pos2)
            && (!(flag2 = this.method5805(pos2, eyePosition, data)) || flag && data3.isFlag2())) {
            float f3 = f;
            float f4 = 0.0F;

            for (CrystalAuraModule.Data10 sub : subList) {
               float f5;
               if (sub.getBlockPos2() == null && pos2.equals(sub.getBlockPos().above())) {
                  f5 = data3.getItem() == Items.OBSIDIAN && mc.level.isEmptyBlock(pos2.above()) && this.method5812(pos2)
                     ? this.class35.method5452(method5959(pos2), 6.0F, mc.player, box, null, pos2)
                     : 0.0F;
               } else {
                  f5 = this.class35.method5452(sub.getVec3d(), sub.getFloatValue2(), mc.player, box, sub.getBlockPos2(), pos2);
               }

               f3 = Math.max(f3, f5);
               f4 += Math.max(0.0F, sub.getFloatValue() - f5);
            }

            float f5 = floatValue - f3 + f4 * 0.1F - (flag2 ? 2.5F : 0.0F);
            if (!(floatValue - f3 < 0.5F) && !(f5 <= f2)) {
               Rotation rotation3 = this.method5831(CrystalAuraModule.Type.SHIELD, pos2, this.vec3d, rotation);
               if (rotation3 != null) {
                  f2 = f5;
                  floatValue2 = f3;
                  pos = pos2;
                  rotation2 = rotation3;
               }
            }
         }
      }

      if (pos == null) {
         return null;
      } else {
         float f3 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (flag && floatValue2 >= f3 - 1.0F && floatValue - floatValue2 < 3.0F) {
            return null;
         } else {
            this.item = data3.getItem();
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     flag ? "DEF!" : "DEF",
                     "план %s %s: худший урон %.1f -> %.1f (спотов %d)",
                     method5948(pos),
                     method5950(data3.getItem()),
                     floatValue,
                     floatValue2,
                     items.size()
                  );
            }

            return new CrystalAuraModule.Data(
               CrystalAuraModule.Type.SHIELD, null, pos.immutable(), rotation2, 0.0F, floatValue, f2, flag, this.items18.getFirst().getPlayerEntity()
            );
         }
      }
   }

   private boolean method5803(BlockPos offset) {
      CrystalAuraModule.Data12 data12 = this.method5821(method5959(offset), 6.0F, null, null, false);
      return data12 != null && data12.isFlag();
   }

   private Vec3 method5804(BlockPos containing) {
      Vec3 atBottomCenterOf = Vec3.atBottomCenterOf(containing);
      Vec3 vec = null;
      double horizontalDistanceSqr = Double.MAX_VALUE;

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         Vec3 vec2 = data5.getVec3d().subtract(atBottomCenterOf);
         double d;
         if ((d = vec2.horizontalDistanceSqr()) > 1.0E-4 && d < horizontalDistanceSqr) {
            horizontalDistanceSqr = d;
            vec = new Vec3(vec2.x, 0.0, vec2.z).normalize();
         }
      }

      return vec;
   }

   private boolean method5805(BlockPos above, Vec3 eyePosition, CrystalAuraModule.Data data) {
      AABB aabb = new AABB(above);

      for (CrystalAuraModule.Data data2 : this.items17) {
         if (data2.getType() != CrystalAuraModule.Type.SHIELD && (data2.pos().equals(above) || method5960(data2.pos()).intersects(aabb))) {
            return true;
         }
      }

      if (this.data != null && aabb.clip(eyePosition, this.data.getEndCrystalEntity().getBoundingBox().getCenter()).isPresent()) {
         return true;
      } else if (data == null) {
         return false;
      } else if (!data.pos().equals(above) && aabb.clip(eyePosition, Vec3.atCenterOf(data.pos())).isPresent()) {
         return true;
      } else {
         if ((data.getType() == CrystalAuraModule.Type.CRYSTAL || data.getType() == CrystalAuraModule.Type.OBSIDIAN)
            && data.isFlag()
            && data.getPlayerEntity() != null) {
            for (CrystalAuraModule.Data5 data5 : this.items18) {
               if (data5.getPlayerEntity() == data.getPlayerEntity()) {
                  return this.class35.method5452(method5959(data.pos()), 6.0F, data5.getPlayerEntity(), data5.getBox2(), null, above)
                     < data.getFloatValue() * 0.85F;
               }
            }
         }

         return false;
      }
   }

   private boolean method5806() {
      if (this.method5896(this.intValue2) <= 100) {
         return true;
      } else {
         for (CrystalAuraModule.Data5 data5 : this.items18) {
            if (data5.getPlayerEntity().getMainHandItem().is(Items.END_CRYSTAL) || data5.getPlayerEntity().getOffhandItem().is(Items.END_CRYSTAL)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean method5807() {
      if (this.method5896(this.intValue21) <= 100) {
         return true;
      } else {
         for (CrystalAuraModule.Data5 data5 : this.items18) {
            ItemStack mainHandItem = data5.getPlayerEntity().getMainHandItem();
            if ((mainHandItem.is(Items.RESPAWN_ANCHOR) || mainHandItem.is(Items.GLOWSTONE)) && data5.getVec3d().distanceTo(mc.player.position()) <= 7.0) {
               return true;
            }
         }

         return false;
      }
   }

   private void method5808(List<CrystalAuraModule.Data10> items, AABB box2, BlockPos containing, boolean flag) {
      if (mc.level.dimension() != Level.NETHER) {
         this.items11.values().removeIf(n -> n < mc.player.tickCount);

         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               for (int k = -1; k <= 2; k++) {
                  BlockPos offset;
                  BlockState blockState;
                  boolean containsKey;
                  label80: {
                     offset = containing.offset(i, k, j);
                     blockState = mc.level.getBlockState(offset);
                     if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
                        label77: {
                           var map = this.items11;
                           Long l = offset.asLong();
                           if (map != null) {
                              if (map.isEmpty()) {
                                 containsKey = false;
                                 break label77;
                              }
                           }

                           containsKey = map.containsKey(l);
                        }

                        if (!containsKey) {
                           containsKey = true;
                           break label80;
                        }
                     }

                     containsKey = false;
                  }

                  boolean containsKey2 = containsKey;
                  if ((containsKey || flag && blockState.canBeReplaced() && this.method5811(offset.below()))
                     && !box2.intersects(new AABB(offset))
                     && (containsKey2 || this.method5809(offset) && this.method5812(offset))) {
                     Vec3 atCenterOf = Vec3.atCenterOf(offset);
                     float floatValue;
                     if (!((floatValue = this.class35.method5452(atCenterOf, 5.0F, mc.player, box2, offset, null)) < 2.0F)) {
                        this.floatValue2 = Math.max(this.floatValue2, floatValue);
                        items.add(new CrystalAuraModule.Data10(offset.below().immutable(), atCenterOf, containsKey2 ? offset.immutable() : null, floatValue, 5.0F, containsKey2));
                     }
                  }
               }
            }
         }
      }
   }

   private boolean method5809(BlockPos offset) {
      for (Direction direction : Direction.values()) {
         if (!mc.level.getBlockState(offset.relative(direction)).canBeReplaced()) {
            return true;
         }
      }

      return false;
   }

   private Item method5810() {
      for (Item rawType : new Item[]{Items.CRYING_OBSIDIAN, Items.ENDER_CHEST, Items.OBSIDIAN}) {
         if (this.method5915(stack -> stack.is(rawType))) {
            return rawType;
         }
      }

      return null;
   }

   private boolean method5811(BlockPos offset) {
      var entities = mc.level.getEntities((Entity) null, method5960(offset), entity -> !entity.isSpectator() && !(entity instanceof EndCrystal));
      return entities.isEmpty();
   }

   private boolean method5812(BlockPos offset) {
      AABB aabb = new AABB(offset);

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         AABB box = data5.getBox();
         Vec3 vec = new Vec3((box.minX + box.maxX) * 0.5, box.minY + 1.62, (box.minZ + box.maxZ) * 0.5);
         if (method5962(vec, aabb) <= 25.0) {
            return true;
         }
      }

      return false;
   }

   private BlockPos method5813(Vec3 position) {
      BlockPos pos = null;
      double d = 3.24;
      var iterator = this.items20.keySet().iterator();

      while (iterator.hasNext()) {
         BlockPos pos2 = BlockPos.of((Long)iterator.next());
         double d2;
         if ((d2 = Vec3.atCenterOf(pos2).distanceToSqr(position)) < d) {
            d = d2;
            pos = pos2;
         }
      }

      return pos;
   }

   private void method5814(
      List<CrystalAuraModule.Data13> data13s, BlockPos pos, CrystalAuraModule.Type type, Vec3 atCenterOf, float floatValue, BlockPos pos2, BlockPos pos3, float f
   ) {
      this.method5815(data13s, pos, type, atCenterOf, floatValue, pos2, pos3, f, 0);
   }

   private void method5815(
      List<CrystalAuraModule.Data13> data13s,
      BlockPos pos,
      CrystalAuraModule.Type type,
      Vec3 position,
      float floatValue,
      BlockPos pos2,
      BlockPos pos3,
      float f,
      int intValue
   ) {
      float f2 = 0.0F;

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         float f3;
         if ((
               f3 = Math.max(
                  this.class35.method5453(position, floatValue, data5.getPlayerEntity(), data5.getBox2()),
                  this.class35.method5453(position, floatValue, data5.getPlayerEntity(), data5.getBox3())
               )
            )
            >= Math.min(this.method5827(data5), data5.getFloatValue())) {
            f2 = Math.max(f2, f3);
         }
      }

      if (f2 > 0.0F) {
         data13s.add(
            new CrystalAuraModule.Data13(
               pos.immutable(), type, position, floatValue, pos2 == null ? null : pos2.immutable(), pos3 == null ? null : pos3.immutable(), f, f2, intValue
            )
         );
      } else {
         this.method5937(type + ": даже без укрытий урон ниже порога");
      }
   }

   private Set<BlockPos> method5816(Vec3 eyePosition, double d) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         method5817(linkedHashSet, data5.getBox(), eyePosition, d);
         method5817(linkedHashSet, data5.getBox2(), eyePosition, d);
         method5817(linkedHashSet, data5.getBox3(), eyePosition, d);
      }

      return linkedHashSet;
   }

   private static void method5817(Set<BlockPos> poses, AABB box, Vec3 eyePosition, double d) {
      BlockPos containing = BlockPos.containing((box.minX + box.maxX) * 0.5, box.minY + 0.001, (box.minZ + box.maxZ) * 0.5);

      for (int i = -3; i <= 3; i++) {
         for (int j = -3; j <= 3; j++) {
            for (int k = -3; k <= 2; k++) {
               BlockPos offset = containing.offset(i, k, j);
               if (method5962(eyePosition, new AABB(offset)) <= d * d) {
                  poses.add(offset);
               }
            }
         }
      }
   }

   private int method5818() {
      return Math.max(1, (this.intValue15 + 1) / 2);
   }

   private int method5819() {
      return this.intValue15 + this.method5818() + 1;
   }

   private AABB method5820(int scale) {
      AABB boundingBox = mc.player.getBoundingBox();
      Vec3 deltaMovement = mc.player.getDeltaMovement();
      return mc.player.isFallFlying() ? boundingBox.move(deltaMovement.scale(scale)) : this.method5906(mc.player, boundingBox, deltaMovement, mc.player.onGround(), Math.min(scale, 12));
   }

   private CrystalAuraModule.Data12 method5821(Vec3 position, float floatValue, BlockPos blockPos2, BlockPos blockPos3, boolean flag) {
      return this.method5822(position, floatValue, blockPos2, blockPos3, flag, 0);
   }

   private CrystalAuraModule.Data12 method5822(Vec3 vec3d, float floatValue, BlockPos blockPos2, BlockPos blockPos3, boolean flag, int n) {
      int n2 = (flag ? this.method5818() : this.method5819()) + n;
      Player playerEntity = null;
      float f = 0.0F;
      float f2 = -Float.MAX_VALUE;
      boolean flag2 = false;
      Player player = null;
      float f3 = -Float.MAX_VALUE;
      this.text2 = null;
      float f4 = -1.0F;
      float f5 = 0.0F;
      Player playerEntity2 = null;

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         float f6 = this.method5827(data5);
         AABB box = flag ? data5.getBox() : data5.getBox2();
         float f7 = Math.min(f6, data5.getFloatValue());
         boolean flag3 = this.class35.method5453(vec3d, floatValue, data5.getPlayerEntity(), box) >= f7;
         boolean flag4 = this.class35.method5453(vec3d, floatValue, data5.getPlayerEntity(), data5.getBox3()) >= f7;
         if (flag3 || flag4) {
            float f8;
            if ((f8 = flag3 ? this.method5823(data5, vec3d, floatValue, box, blockPos2, blockPos3, n2) : 0.0F) > f4) {
               f4 = f8;
               f5 = f7;
               playerEntity2 = data5.getPlayerEntity();
            }

            boolean flag5 = f8 >= data5.getFloatValue();
            if (!(f8 >= f6) && !flag5) {
               if (playerEntity == null) {
                  float f9;
                  if (flag3
                     && !flag
                     && this.entry2.isEnabled()
                     && this.method5788(data5.getPlayerEntity())
                     && (f9 = this.method5824(data5, vec3d, floatValue, box, blockPos2, blockPos3, n2, true)) >= f6
                     && f9 * 0.8F > f3) {
                     f3 = f9 * 0.8F;
                     player = data5.getPlayerEntity();
                  }

                  if (flag4
                     && (
                           f9 = method5826(data5.getPlayerEntity(), vec3d)
                              ? 0.0F
                              : this.class35.method5452(vec3d, floatValue, data5.getPlayerEntity(), data5.getBox3(), blockPos2, blockPos3)
                        )
                        >= f6
                     && f9 * 0.7F > f3) {
                     f3 = f9 * 0.7F;
                     player = data5.getPlayerEntity();
                  }
               }
            } else {
               float f9;
               if ((f9 = f8 + (flag5 ? (data5.isFlag() ? 6.0F : 15.0F) : 0.0F) + this.method5825(data5, f8, flag5)) > f2) {
                  f2 = f9;
                  f = f8;
                  playerEntity = data5.getPlayerEntity();
                  flag2 = flag5 && !data5.isFlag();
               }
            }
         }
      }

      boolean flag3;
      if (!(flag3 = playerEntity != null) && player == null) {
         this.text2 = playerEntity2 == null
            ? "ни одна цель не достаёт даже по верхней оценке"
            : String.format(
               Locale.ROOT,
               "мало урона: %.1f по %s, нужно %.1f%s",
               f4,
               method5947(playerEntity2),
               f5,
               f4 == 0.0F ? " (сейчас не достаёт / щит / i-frames)" : ""
            );
         return null;
      } else {
         AABB box = flag ? this.box : this.box2;
         if (box == null) {
            box = mc.player.getBoundingBox();
         }

         float f6;
         if ((f6 = this.class35.method5452(vec3d, floatValue, mc.player, box, blockPos2, blockPos3)) >= this.method5830()) {
            this.text2 = String.format(Locale.ROOT, "по нам %.1f — летально (у нас %.1f)", f6, this.method5830() + 0.5F);
            return null;
         } else if (!(f6 > this.maxSelfDamage.getValue3()) || flag3 && flag2) {
            float f7 = 0.0F;

            for (Player playerEntity3 : this.items19) {
               float f8;
               if ((f8 = this.class35.method5452(vec3d, floatValue, playerEntity3, playerEntity3.getBoundingBox(), blockPos2, blockPos3)) > this.maxSelfDamage.getValue3()
                  || f8 >= method5902(playerEntity3) - 0.5F) {
                  this.text2 = String.format(Locale.ROOT, "задевает друга %s на %.1f", method5947(playerEntity3), f8);
                  return null;
               }

               f7 = Math.max(f7, f8);
            }

            float f8 = this.method5828();
            return flag3
               ? new CrystalAuraModule.Data12(playerEntity, f, f6, f2 - f6 * f8 - f7, true)
               : new CrystalAuraModule.Data12(player, 0.0F, f6, f3 - f6 * f8 - f7, false);
         } else {
            this.text2 = String.format(Locale.ROOT, "по нам %.1f > макс %.1f (по цели %.1f)", f6, this.maxSelfDamage.getValue3(), f);
            return null;
         }
      }
   }

   private float method5823(CrystalAuraModule.Data5 data5, Vec3 position, float floatValue, AABB box, BlockPos blockPos2, BlockPos blockPos3, int n) {
      return this.method5824(data5, position, floatValue, box, blockPos2, blockPos3, n, false);
   }

   private float method5824(CrystalAuraModule.Data5 data5, Vec3 vec3d, float floatValue, AABB box, BlockPos blockPos2, BlockPos blockPos3, int n, boolean flag) {
      if (method5826(data5.getPlayerEntity(), vec3d)) {
         return 0.0F;
      } else {
         float f = this.class35.method5454(vec3d, floatValue, data5.getPlayerEntity(), box, blockPos2, blockPos3);
         if (this.entry2.isEnabled() && !flag) {
            float f2 = data5.getIntValue() - n - (this.intValue15 + 1) / 2 > 10 ? data5.getFloatValue2() : 0.0F;
            CrystalAuraModule.Entry entry = this.items14.get(data5.getPlayerEntity().getId());
            if (entry != null && entry.method5969(mc.player.tickCount + n)) {
               f2 = Math.max(f2, entry.floatValue8);
            }

            if ((f -= f2) <= 0.0F) {
               return 0.0F;
            }
         }

         float f2 = this.class35.method5456(data5.getPlayerEntity(), f);
         if (data5.isFlag3() && !data5.getPlayerEntity().hasEffect(MobEffects.RESISTANCE)) {
            f2 *= 0.8F;
         }

         return f2;
      }
   }

   private float method5825(CrystalAuraModule.Data5 data5, float f, boolean flag) {
      if (!this.entry16.isEnabled() || !data5.isFlag()) {
         return 0.0F;
      } else if (flag) {
         float f2 = -Math.min(4.0F, (f - data5.getFloatValue()) * 0.5F);
         return data5.getIntValue2() <= 10 ? f2 + 4.0F : f2;
      } else {
         float f2;
         return (f2 = data5.getFloatValue() - f) > 0.0F && f2 < 2.0F ? 2.5F : 0.0F;
      }
   }

   private static boolean method5826(Player playerEntity, Vec3 vec3d) {
      if (!playerEntity.isBlocking()) {
         return false;
      } else {
         Vec3 viewVector = playerEntity.calculateViewVector(0.0F, playerEntity.getYHeadRot());
         Vec3 vec = vec3d.subtract(playerEntity.position());
         vec = new Vec3(vec.x, 0.0, vec.z);
         return vec.lengthSqr() < 1.0E-6 ? false : Math.acos(Mth.clamp(vec.normalize().dot(viewVector), -1.0, 1.0)) <= Math.PI / 2;
      }
   }

   private float method5827(CrystalAuraModule.Data5 data5) {
      float f = this.minDamage.getValue3();
      return !(data5.getFloatValue() <= this.faceplace.getValue3()) && !data5.isFlag2() ? f : Math.min(f, 2.0F);
   }

   private float method5828() {
      float f = mc.player.getHealth() + mc.player.getAbsorptionAmount();
      return 1.2F * Mth.clamp(16.0F / Math.max(1.0F, f), 1.0F, 3.0F);
   }

   private boolean method5829() {
      Inventory inventory = mc.player.getInventory();

      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (stack.is(Items.END_CRYSTAL) || stack.is(Items.RESPAWN_ANCHOR) || stack.is(Items.COBWEB) && this.entry23.isEnabled()) {
            return true;
         }
      }

      return mc.player.getOffhandItem().is(Items.END_CRYSTAL);
   }

   private float method5830() {
      return !this.entry9.isEnabled() ? Float.MAX_VALUE : Math.max(0.0F, mc.player.getHealth() + mc.player.getAbsorptionAmount() - 2.0F);
   }

   private Rotation method5831(CrystalAuraModule.Type type, BlockPos pos, Vec3 vec3d, Rotation rotation) {
      return switch (type) {
         case BREAK -> null;
         case CRYSTAL -> this.method5836(pos, method5961(pos), vec3d, rotation);
         case OBSIDIAN, SHIELD, ANCHOR_PLACE -> this.method5841(pos, vec3d, rotation);
         case DIG, ANCHOR_CHARGE, ANCHOR_EXPLODE -> this.method5836(pos, null, vec3d, rotation);
      };
   }

   private BlockHitResult method5832(CrystalAuraModule.Data data, Vec3 eyePosition, Rotation rotation) {
      BlockHitResult blockHitResult = this.method5875(eyePosition, rotation, this.method5878());
      if (blockHitResult.getType() == HitResult.Type.BLOCK && !blockHitResult.isInside()) {
         return switch (data.getType()) {
            case BREAK -> null;
            case CRYSTAL, DIG, ANCHOR_CHARGE, ANCHOR_EXPLODE -> blockHitResult.getBlockPos().equals(data.pos()) ? blockHitResult : null;
            case OBSIDIAN, SHIELD, ANCHOR_PLACE -> this.method5833(blockHitResult, data.pos()) ? blockHitResult : null;
         };
      } else {
         return null;
      }
   }

   private boolean method5833(BlockHitResult blockHitResult, BlockPos pos) {
      BlockState blockState = mc.level.getBlockState(blockHitResult.getBlockPos());
      return !blockState.canBeReplaced() && blockHitResult.getBlockPos().relative(blockHitResult.getDirection()).equals(pos)
         ? mc.player.isShiftKeyDown() || blockState.getMenuProvider(mc.level, blockHitResult.getBlockPos()) == null
         : false;
   }

   private Rotation method5834(Vec3 vec3d, Rotation rotation, EndCrystal endCrystalEntity) {
      AABB boundingBox = endCrystalEntity.getBoundingBox();
      Rotation rotation2 = null;
      double d = Double.MAX_VALUE;

      for (double alpha1 : CONST_doubleValues2) {
         for (double alpha12 : CONST_doubleValues2) {
            for (double alpha13 : CONST_doubleValues2) {
               Vec3 vec = new Vec3(
                  Mth.lerp(alpha1, boundingBox.minX, boundingBox.maxX), Mth.lerp(alpha12, boundingBox.minY, boundingBox.maxY), Mth.lerp(alpha13, boundingBox.minZ, boundingBox.maxZ)
               );
               Rotation rotation3 = method5898(vec3d, vec);
               double d2;
               if (this.method5835(vec3d, rotation3, endCrystalEntity) != null && (d2 = rotation.wrappedDeltaTo(rotation3).length()) < d) {
                  d = d2;
                  rotation2 = rotation3;
               }
            }
         }
      }

      return rotation2;
   }

   private Vec3 method5835(Vec3 eyePosition, Rotation rotation, EndCrystal endCrystalEntity) {
      AABB boundingBox = endCrystalEntity.getBoundingBox();
      if (boundingBox.contains(eyePosition)) {
         return eyePosition;
      } else {
         var clip = boundingBox.clip(eyePosition, eyePosition.add(rotation.toDirection().scale(this.method5877())));
         if (clip.isEmpty()) {
            return null;
         } else {
            return !this.data3.isFlag5() && this.method5874(eyePosition, (Vec3)clip.get()) ? null : (Vec3)clip.get();
         }
      }
   }

   private Rotation method5836(BlockPos pos, AABB aabb, Vec3 vec3d, Rotation rotation) {
      double scale = this.method5878();
      BlockState blockState = mc.level.getBlockState(pos);
      VoxelShape shape = blockState.getShape(mc.level, pos);
      List<AABB> aabbs = shape.isEmpty() ? List.of(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)) : shape.toAabbs();
      ArrayList arrayList = new ArrayList();

      for (AABB aabb2 : aabbs) {
         AABB move = aabb2.move(pos);

         for (Direction opposite : Direction.values()) {
            Vec3 vec = new Vec3(opposite.getStepX(), opposite.getStepY(), opposite.getStepZ());

            for (double d : CONST_doubleValues) {
               for (double d2 : CONST_doubleValues) {
                  Vec3 vec2 = method5963(move, opposite, d + this.doubleValue2, d2 + this.doubleValue3);
                  if (!(vec3d.subtract(vec2).dot(vec) <= 0.0) && !(vec3d.distanceToSqr(vec2) > scale * scale)) {
                     Rotation rotation2 = method5898(vec3d, vec2);
                     BlockHitResult blockHitResult = this.method5875(vec3d, rotation2, scale);
                     if (blockHitResult.getType() == HitResult.Type.BLOCK && !blockHitResult.isInside() && blockHitResult.getBlockPos().equals(pos) && !this.method5838(vec3d, rotation2)
                        )
                      {
                        double doubleValue = rotation.wrappedDeltaTo(rotation2).length()
                           + (opposite == Direction.UP ? 0.0 : 2.0)
                           + (Math.abs(d - 0.5) + Math.abs(d2 - 0.5)) * 4.0;
                        if (aabb != null && aabb.clip(vec3d, vec3d.add(rotation2.toDirection().scale(this.method5877()))).isEmpty()) {
                           doubleValue += 6.0;
                        }

                        arrayList.add(new CrystalAuraModule.Data4(rotation2, doubleValue, pos, null));
                     }
                  }
               }
            }
         }
      }

      return this.method5839(arrayList);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Rotation method5837(CrystalAuraModule.Data data, EndCrystal endCrystalEntity) {
      if (data != null && data.isFlag() && data.getType() != CrystalAuraModule.Type.BREAK && data.getType() != CrystalAuraModule.Type.DIG) {
         this.box3 = endCrystalEntity.getBoundingBox();

         Rotation rotation;
         try {
            rotation = this.method5831(data.getType(), data.pos(), this.vec3d, this.method5897());
         } catch (Throwable e) {
            this.box3 = null;
            throw e;
         }

         this.box3 = null;
         return rotation;
      } else {
         return null;
      }
   }

   private boolean method5838(Vec3 vec3d, Rotation rotation) {
      return this.box3 != null && this.box3.clip(vec3d, vec3d.add(rotation.toDirection().scale(this.method5877()))).isEmpty();
   }

   private Rotation method5839(List<CrystalAuraModule.Data4> data4s) {
      if (data4s.isEmpty()) {
         return null;
      } else {
         data4s.sort(Comparator.comparingDouble(CrystalAuraModule.Data4::getDoubleValue));

         for (int i = 0; i < data4s.size() && i < 6; i++) {
            CrystalAuraModule.Data4 data4 = (CrystalAuraModule.Data4)data4s.get(i);
            if (this.method5840(data4.rotation(), data4.pos(), data4.getDirection())) {
               return data4.rotation();
            }
         }

         return ((CrystalAuraModule.Data4)data4s.getFirst()).rotation();
      }
   }

   private boolean method5840(Rotation rotation, BlockPos pos, Direction direction) {
      double scale = this.method5878();

      for (Vec3 vec3d : new Vec3[]{this.vec3d, mc.player.getEyePosition()}) {
         for (float[] floats : CONST_floatValues) {
            Rotation rotation2 = new Rotation(rotation.getFloatValue() + floats[0], Mth.clamp(rotation.getFloatValue2() + floats[1], -90.0F, 90.0F));
            BlockHitResult blockHitResult = this.method5875(vec3d, rotation2, scale);
            if (blockHitResult.getType() != HitResult.Type.BLOCK
               || blockHitResult.isInside()
               || !blockHitResult.getBlockPos().equals(pos)
               || direction != null && blockHitResult.getDirection() != direction) {
               return false;
            }
         }
      }

      return true;
   }

   private Rotation method5841(BlockPos pos, Vec3 vec3d, Rotation rotation) {
      double scale = this.method5878();
      AABB aabb = method5961(pos);
      ArrayList arrayList = new ArrayList();

      for (Direction direction : Direction.values()) {
         BlockPos relative = pos.relative(direction);
         BlockState blockState = mc.level.getBlockState(relative);
         if (!blockState.canBeReplaced() && (mc.player.isShiftKeyDown() || blockState.getMenuProvider(mc.level, relative) == null)) {
            VoxelShape shape = blockState.getShape(mc.level, relative);
            if (!shape.isEmpty()) {
               Direction opposite = direction.getOpposite();

               for (AABB aabb2 : shape.toAabbs()) {
                  AABB move = aabb2.move(relative);

                  for (double d : CONST_doubleValues) {
                     for (double d2 : CONST_doubleValues) {
                        Vec3 vec = method5963(move, opposite, d + this.doubleValue2, d2 + this.doubleValue3);
                        if (!(vec3d.distanceToSqr(vec) > scale * scale)) {
                           Rotation rotation2 = method5898(vec3d, vec);
                           BlockHitResult blockHitResult = this.method5875(vec3d, rotation2, scale);
                           if (blockHitResult.getType() == HitResult.Type.BLOCK
                              && !blockHitResult.isInside()
                              && blockHitResult.getBlockPos().equals(relative)
                              && blockHitResult.getDirection() == opposite
                              && !this.method5838(vec3d, rotation2)) {
                              double doubleValue = rotation.wrappedDeltaTo(rotation2).length()
                                 + vec3d.distanceTo(vec)
                                 + (Math.abs(d - 0.5) + Math.abs(d2 - 0.5)) * 6.0;
                              if (direction != Direction.DOWN) {
                                 doubleValue += 2.0;
                              }

                              if (aabb.clip(vec3d, vec3d.add(rotation2.toDirection().scale(this.method5877()))).isEmpty()) {
                                 doubleValue += 4.0;
                              }

                              arrayList.add(new CrystalAuraModule.Data4(rotation2, doubleValue, relative.immutable(), opposite));
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return this.method5839(arrayList);
   }

   private boolean method5842(EndCrystal endCrystalEntity, int tickCount) {
      int selectedSlot;
      if (!this.entry8.isEnabled() || !this.method5911() || (selectedSlot = this.method5853()) != -1 && this.method5859(selectedSlot) != null) {
         mc.gameMode.attack(mc.player, endCrystalEntity);
         org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         this.items6.put(endCrystalEntity.getId(), tickCount + this.method5909());
         this.items7.put(endCrystalEntity.getId(), Math.sqrt(method5962(mc.player.getEyePosition(), endCrystalEntity.getBoundingBox())));
         this.data8 = new CrystalAuraModule.Data8(endCrystalEntity.position(), 6.0F, null, tickCount);
         this.method5843(endCrystalEntity.position(), 6.0F, null, tickCount);
         this.intValue17 = tickCount;
         this.intValue8++;
         this.method5860();
         return true;
      } else {
         return false;
      }
   }

   private void method5843(Vec3 position, float floatValue, BlockPos immutable, int tickCount) {
      int n = tickCount + this.method5818();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         CrystalAuraModule.Entry entry = this.items14.get(data5.getPlayerEntity().getId());
         if (entry != null && !method5826(data5.getPlayerEntity(), position)) {
            entry.method5968(n, this.class35.method5454(position, floatValue, data5.getPlayerEntity(), data5.getBox(), immutable, null));
         }
      }
   }

   private boolean method5844(CrystalAuraModule.Data data, BlockHitResult blockHit, Rotation rotation, int tickCount) {
      InteractionHand hand = this.method5858(stack -> stack.is(Items.END_CRYSTAL), true);
      if (hand == null) {
         return false;
      } else {
         this.method5856(hand, blockHit, Items.END_CRYSTAL, method5958(data.pos(), 0), tickCount, true);
         this.items.put(data.pos().asLong(), tickCount + this.method5909());
         this.method5857(rotation, tickCount);
         if (this.method5925()) {
            this.combatLogger.intValue2++;
            this.combatLogger
               .method4612(
                  "PLACE",
                  "кристалл на %s%s: урон %.1f по %s (hp %.1f), по нам %.1f, до места %.2f б, цель в %.2f б, рука %s",
                  method5948(data.pos()),
                  data.isFlag() ? "" : " (на вырост)",
                  data.getFloatValue(),
                  method5947(data.getPlayerEntity()),
                  data.getPlayerEntity() == null ? 0.0F : method5902(data.getPlayerEntity()),
                  data.getFloatValue2(),
                  Math.sqrt(method5962(mc.player.getEyePosition(), new AABB(data.pos()))),
                  data.getPlayerEntity() == null ? -1.0 : data.getPlayerEntity().distanceTo(mc.player),
                  hand == InteractionHand.OFF_HAND ? "левая" : "правая"
               );
         }

         if (data.getFloatValue() <= 4.0F) {
            this.intValue18 = tickCount;
         }

         if (!data.isFlag() && this.entry13.isEnabled() && data.getPlayerEntity() != null && mc.player.distanceTo(data.getPlayerEntity()) <= 3.5F) {
            this.longValue = data.pos().asLong();
            this.playerEntity2 = data.getPlayerEntity();
            this.intValue19 = tickCount;
         }

         return true;
      }
   }

   private boolean method5845(CrystalAuraModule.Data data, BlockHitResult blockHit, Item rawType, Rotation rotation, int tickCount) {
      InteractionHand hand = this.method5858(var1x -> var1x.is(rawType), true);
      if (hand == null) {
         return false;
      } else {
         boolean flag = this.method5856(hand, blockHit, rawType, method5958(data.pos(), 1), tickCount, true);
         if (rawType == Items.COBWEB) {
            this.intValue33 = tickCount;
         }

         this.items2.put(data.pos().asLong(), tickCount + this.method5909() * 2);
         this.items3.put(data.pos().asLong(), tickCount);
         this.method5857(rotation, tickCount);
         if (data.getType() == CrystalAuraModule.Type.SHIELD) {
            this.intValue22 = tickCount;
            if (flag) {
               this.combatLogger.intValue4++;
            }
         }

         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  data.getType() == CrystalAuraModule.Type.SHIELD ? (data.isFlag() ? "DEF!" : "DEF") : "BLOCK+",
                  "%s %s на %s%s, до места %.2f б%s",
                  data.getType(),
                  method5950(rawType),
                  method5948(data.pos()),
                  flag ? "" : " — клиент отклонил",
                  Math.sqrt(method5962(mc.player.getEyePosition(), new AABB(data.pos()))),
                  data.getType() == CrystalAuraModule.Type.SHIELD
                     ? String.format(Locale.ROOT, ", худший урон был %.1f", data.getFloatValue2())
                     : String.format(Locale.ROOT, ", под урон %.1f по %s", data.getFloatValue(), method5947(data.getPlayerEntity()))
               );
         }

         BlockState blockState = mc.level.getBlockState(data.pos());
         return flag && (method5956(blockState) || blockState.is(Blocks.RESPAWN_ANCHOR)) && data.getType() != CrystalAuraModule.Type.SHIELD;
      }
   }

   private boolean method5846(CrystalAuraModule.Data data, BlockHitResult blockHit, int tickCount) {
      BlockState blockState = mc.level.getBlockState(data.pos());
      if (blockState.is(Blocks.RESPAWN_ANCHOR) && blockState.getValue(RespawnAnchorBlock.CHARGE) < 4) {
         InteractionHand hand = this.method5858(stack -> stack.is(Items.GLOWSTONE), true);
         if (hand == null) {
            return false;
         } else {
            this.method5856(hand, blockHit, Items.GLOWSTONE, method5958(data.pos(), 2), tickCount, true);
            if (this.method5925()) {
               this.combatLogger.method4612("ANCHOR", "зарядили якорь %s (было зарядов %d)", method5948(data.pos()), blockState.getValue(RespawnAnchorBlock.CHARGE));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean method5847(CrystalAuraModule.Data data, BlockHitResult blockHit, int intValue) {
      if (!mc.player.isShiftKeyDown() && !mc.player.getOffhandItem().is(Items.GLOWSTONE) && mc.level.getBlockState(data.pos()).is(Blocks.RESPAWN_ANCHOR)) {
         int selectedSlot = this.intValue14 != -1 ? this.intValue14 : mc.player.getInventory().getSelectedSlot();
         InteractionHand hand = !mc.player.getMainHandItem().is(Items.GLOWSTONE)
            ? InteractionHand.MAIN_HAND
            : (
               !mc.player.getInventory().getItem(selectedSlot).is(Items.GLOWSTONE)
                  ? this.method5859(selectedSlot)
                  : this.method5858(stack -> !stack.is(Items.GLOWSTONE), false)
            );
         if (hand == null) {
            return false;
         } else {
            this.method5856(hand, blockHit, null, 0L, 0, false);
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     "ANCHOR",
                     "взорвали якорь %s: урон %.1f по %s, по нам %.1f, до якоря %.2f б",
                     method5948(data.pos()),
                     data.getFloatValue(),
                     method5947(data.getPlayerEntity()),
                     data.getFloatValue2(),
                     Math.sqrt(method5962(mc.player.getEyePosition(), new AABB(data.pos())))
                  );
            }

            this.data8 = new CrystalAuraModule.Data8(Vec3.atCenterOf(data.pos()), 5.0F, data.pos().immutable(), intValue);
            this.method5843(Vec3.atCenterOf(data.pos()), 5.0F, data.pos().immutable(), intValue);
            this.items5.put(data.pos().asLong(), intValue + 2);
            if (this.entry12.isEnabled()) {
               this.items15.add(new CrystalAuraModule.Data6(data.pos().immutable(), System.currentTimeMillis(), true));
            }

            this.type2 = CrystalAuraModule.Type2.CRYSTALS;
            this.intValue = intValue;
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean method5848(CrystalAuraModule.Data data, BlockHitResult blockHit) {
      BlockPos pos = data.pos();
      BlockState blockState = mc.level.getBlockState(pos);
      if (blockState.is(BlockTags.FIRE)) {
         mc.gameMode.startDestroyBlock(pos, blockHit.getDirection());
         org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         this.intValue8++;
         return true;
      } else if (blockState.canBeReplaced()) {
         return false;
      } else {
         int selected = this.method5912(blockState);
         if (!pos.equals(this.blockPos3)) {
            this.method5864();
         }

         if (selected != -1) {
            this.method5849(selected);
         }

         if (!pos.equals(this.blockPos3)) {
            this.blockPos3 = pos.immutable();
            if (this.method5925()) {
               this.combatLogger
                  .method4612(
                     "DIG",
                     "копаем %s (%s) ради урона %.1f по %s, до блока %.2f б",
                     method5948(pos),
                     method5950(blockState.getBlock().asItem()),
                     data.getFloatValue(),
                     method5947(data.getPlayerEntity()),
                     Math.sqrt(method5962(mc.player.getEyePosition(), new AABB(pos)))
                  );
            }
         }

         this.intValue20 = mc.player.tickCount;
         if (!mc.gameMode.isDestroying() || this.intValue32 == (-32768 << 16 | 0)) {
            this.intValue32 = mc.player.tickCount;
         }

         if (mc.gameMode.continueDestroyBlock(pos, blockHit.getDirection())) {
            org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
         }

         if (mc.gameMode.isDestroying() && mc.gameMode.destroyProgress < 1.0F) {
            float f = this.method5851(pos, blockState, mc.player.getMainHandItem());
            int n = mc.player.tickCount - this.intValue32;
            if (f > 0.0F && f * (n + 1) >= 1.0F) {
               Direction direction = blockHit.getDirection();
               float destroyProgress = mc.gameMode.destroyProgress;
               mc.gameMode.startPrediction(mc.level, var2x -> new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, pos, direction, var2x));
               mc.gameMode.destroyBlock(pos);
               mc.gameMode.isDestroying = false;
               mc.gameMode.destroyProgress = 0.0F;
               if (this.method5925()) {
                  this.combatLogger
                     .method4612(
                        "DIG",
                        "%s докопан по серверному времени: %d т (клиент насчитал бы %.0f%%)",
                        method5948(pos),
                        n + 1,
                        Math.min(100.0F, destroyProgress * 100.0F)
                     );
               }

               this.intValue32 = -32768 << 16 | 0;
            }
         }

         this.floatValue = mc.gameMode.isDestroying() ? Mth.clamp(mc.gameMode.destroyProgress, 0.0F, 1.0F) : 0.0F;
         this.intValue8++;
         return true;
      }
   }

   private void method5849(int selected) {
      int selectedSlot = mc.player.getInventory().getSelectedSlot();
      int n;
      if ((n = this.intValue14 != -1 ? this.intValue14 : selectedSlot) != selected) {
         if (this.intValue27 == -1) {
            this.intValue27 = n;
         }

         if (selectedSlot != selected) {
            mc.player.getInventory().setSelectedSlot(selected);
            mc.gameMode.ensureHasSentCarriedItem();
            this.intValue9++;
         }

         this.intValue31 = selected;
         if (this.intValue14 != -1) {
            this.intValue14 = selected;
         }

         if (this.method5925()) {
            this.combatLogger.method4612("SWAP", "кирку в руку на копание: слот %d -> %d", n, selected);
         }
      }
   }

   private int method5850(BlockPos pos, BlockState blockState) {
      if (!this.method5923(pos) && mc.level.getFluidState(pos).isEmpty() && !blockState.is(Blocks.RESPAWN_ANCHOR)) {
         float destroySpeed;
         if ((destroySpeed = blockState.getDestroySpeed(mc.level, pos)) < 0.0F) {
            return -1;
         } else if (destroySpeed == 0.0F) {
            return 1;
         } else {
            int slot;
            ItemStack mainHandItem = (slot = this.method5912(blockState)) == -1 ? mc.player.getMainHandItem() : mc.player.getInventory().getItem(slot);
            float f;
            if ((f = this.method5851(pos, blockState, mainHandItem)) <= 0.0F) {
               return -1;
            } else {
               int n;
               return (n = f >= 1.0F ? 1 : (int)Math.ceil(1.0F / f)) <= this.mineTime.getValue3() * 20.0F ? n : -1;
            }
         }
      } else {
         return -1;
      }
   }

   private float method5851(BlockPos pos, BlockState blockState, ItemStack mainHandItem) {
      float destroySpeed;
      if ((destroySpeed = blockState.getDestroySpeed(mc.level, pos)) < 0.0F) {
         return 0.0F;
      } else if (destroySpeed == 0.0F) {
         return 1.0F;
      } else {
         float f;
         if ((f = mainHandItem.getDestroySpeed(blockState)) > 1.0F) {
            this.method5852();
            int itemEnchantmentLevel;
            if ((itemEnchantmentLevel = this.registryEntry == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry, mainHandItem)) > 0) {
               f += itemEnchantmentLevel * itemEnchantmentLevel + 1;
            }
         }

         MobEffectInstance effect = mc.player.getEffect(MobEffects.HASTE);
         if (effect != null) {
            f *= 1.0F + (effect.getAmplifier() + 1) * 0.2F;
         }

         MobEffectInstance mobEffectInstance = mc.player.getEffect(MobEffects.MINING_FATIGUE);
         if (mobEffectInstance != null) {
            f *= switch (mobEffectInstance.getAmplifier()) {
               case 0 -> 0.3F;
               case 1 -> 0.09F;
               case 2 -> 0.0027F;
               default -> 8.1E-4F;
            };
         }

         f *= (float)mc.player.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
         if (mc.player.isEyeInFluid(FluidTags.WATER)) {
            f *= (float)mc.player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
         }

         if (!mc.player.onGround()) {
            f /= 5.0F;
         }

         boolean flag = !blockState.requiresCorrectToolForDrops() || mainHandItem.isCorrectToolForDrops(blockState);
         return f / destroySpeed / (flag ? 30.0F : 100.0F);
      }
   }

   private void method5852() {
      RegistryAccess registryAccess = mc.level.registryAccess();
      if (registryAccess != this.obj) {
         Registry<net.minecraft.world.item.enchantment.Enchantment> orThrow = mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
         this.registryEntry = orThrow.get(Enchantments.EFFICIENCY).orElse(null);
         this.registryEntry2 = orThrow.get(Enchantments.KNOCKBACK).orElse(null);
         this.registryEntry3 = orThrow.get(Enchantments.SHARPNESS).orElse(null);
         this.obj = registryAccess;
      }
   }

   private int method5853() {
      double d = this.method5854();
      int n = -1;
      double d2 = 0.0;

      for (int i = 0; i < 9; i++) {
         ItemStack mainHandItem = mc.player.getInventory().getItem(i);
         double d3;
         if ((mainHandItem.is(ItemTags.SWORDS) || mainHandItem.is(ItemTags.AXES)) && (d3 = d + method5855(mainHandItem)) > d2) {
            d2 = d3;
            n = i;
         }
      }

      return n;
   }

   private double method5854() {
      AttributeInstance attribute = mc.player.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attribute == null) {
         return 1.0;
      } else {
         double d = attribute.getValue();
         if (attribute.hasModifier(Item.BASE_ATTACK_DAMAGE_ID)) {
            d -= method5855(mc.player.getMainHandItem());
         }

         return d;
      }
   }

   private static double method5855(ItemStack mainHandItem) {
      double[] doubles = new double[]{0.0};
      mainHandItem.forEachModifier(EquipmentSlot.MAINHAND, (var1x, attributeModifier) -> {
         if (var1x.equals(Attributes.ATTACK_DAMAGE) && attributeModifier.operation() == Operation.ADD_VALUE) {
            doubles[0] += attributeModifier.amount();
         }
      });
      return doubles[0];
   }

   private boolean method5856(InteractionHand hand, BlockHitResult blockHit, Item rawType, long l, int tickCount, boolean flag) {
      int intValue = PlacementQueue.method2811(hand);
      InteractionResult useItemOn = mc.gameMode.useItemOn(mc.player, hand, blockHit);
      this.intValue8++;
      boolean consumesAction;
      if ((consumesAction = useItemOn.consumesAction()) && rawType != null) {
         this.placementQueue.method2812(l, intValue, rawType, tickCount + this.method5909() + 2);
      }

      if (useItemOn instanceof Success success && success.swingSource() == SwingSource.PREDICTED || flag) {
         org.zenith.utility.game.SwingCompat.swing(mc.player, hand);
      }

      return consumesAction;
   }

   private void method5857(Rotation rotation3, int tickCount) {
      this.rotation3 = rotation3;
      this.intValue16 = tickCount;
      if (this.data3.isFlag4()) {
         ThreadLocalRandom current = ThreadLocalRandom.current();
         this.doubleValue2 = current.nextDouble(-0.08, 0.08);
         this.doubleValue3 = current.nextDouble(-0.08, 0.08);
      }
   }

   private InteractionHand method5858(Predicate<ItemStack> predicate, boolean flag) {
      if (flag && !this.flag && predicate.test(mc.player.getOffhandItem())) {
         return InteractionHand.OFF_HAND;
      } else if (predicate.test(mc.player.getMainHandItem())) {
         return InteractionHand.MAIN_HAND;
      } else {
         int selectedSlot;
         return (selectedSlot = this.method5916(predicate)) == -1 ? null : this.method5859(selectedSlot);
      }
   }

   private InteractionHand method5859(int selectedSlot) {
      int n;
      if ((n = mc.player.getInventory().getSelectedSlot()) == selectedSlot) {
         return InteractionHand.MAIN_HAND;
      } else {
         org.zenith.utility.misc.Data3.Type type = this.data3.getType();
         if (type != org.zenith.utility.misc.Data3.Type.OFF && (type != org.zenith.utility.misc.Data3.Type.KEEP || this.intValue9 < 1)) {
            if (type == org.zenith.utility.misc.Data3.Type.KEEP && this.intValue12 == -1) {
               this.intValue12 = n;
            }

            if (type == org.zenith.utility.misc.Data3.Type.SILENT && this.intValue14 == -1) {
               this.intValue14 = n;
            }

            mc.player.getInventory().setSelectedSlot(selectedSlot);
            mc.gameMode.ensureHasSentCarriedItem();
            this.intValue9++;
            this.intValue13 = selectedSlot;
            if (this.method5925()) {
               this.combatLogger
                  .method4612("SWAP", "слот %d -> %d (%s), режим %s", n, selectedSlot, method5950(mc.player.getInventory().getItem(selectedSlot).getItem()), type);
            }

            return InteractionHand.MAIN_HAND;
         } else {
            return null;
         }
      }
   }

   private void method5860() {
      if (this.intValue14 != -1 && mc.player != null) {
         int selected = this.intValue14;
         this.intValue14 = -1;
         if (mc.player.getInventory().getSelectedSlot() != selected) {
            mc.player.getInventory().setSelectedSlot(selected);
            mc.gameMode.ensureHasSentCarriedItem();
         }
      }
   }

   private void method5861() {
      if (this.intValue12 != -1 && mc.player != null) {
         int selected = this.intValue12;
         this.intValue12 = -1;
         if (mc.player.getInventory().getSelectedSlot() == this.intValue13 && this.intValue13 != selected) {
            mc.player.getInventory().setSelectedSlot(selected);
            mc.gameMode.ensureHasSentCarriedItem();
            this.intValue9++;
         }

         this.intValue13 = -1;
      }
   }

   private void method5862() {
      this.intValue10++;
      if (this.intValue12 != -1 && this.intValue10 >= 10 && this.intValue9 == 0) {
         this.method5861();
      }
   }

   private void method5863() {
      this.method5864();
      int selectedSlot = mc.player == null ? -1 : (this.intValue14 != -1 ? this.intValue14 : mc.player.getInventory().getSelectedSlot());
      if (this.intValue27 != -1 && mc.player != null && mc.gameMode != null && selectedSlot == this.intValue31) {
         if (this.intValue14 != -1) {
            this.intValue14 = this.intValue27;
         } else if (mc.player.getInventory().getSelectedSlot() != this.intValue27) {
            mc.player.getInventory().setSelectedSlot(this.intValue27);
            mc.gameMode.ensureHasSentCarriedItem();
         }
      }

      this.intValue27 = -1;
   }

   private void method5864() {
      this.intValue32 = -32768 << 16 | 0;
      if (this.blockPos3 != null && mc.gameMode != null) {
         mc.gameMode.stopDestroyBlock();
      }

      this.blockPos3 = null;
      this.floatValue = 0.0F;
   }

   private boolean method5865(BlockPos pos, Entity endCrystalEntity) {
      if (!mc.level.isEmptyBlock(pos.above())) {
         return false;
      } else {
         AABB aabb = method5960(pos);
         var iterator = this.items.keySet().iterator();

         while (iterator.hasNext()) {
            long blockNode;
            if ((blockNode = (Long)iterator.next()) != pos.asLong() && method5961(BlockPos.of(blockNode)).intersects(aabb)) {
               return false;
            }
         }

         return this.method5866(aabb, endCrystalEntity);
      }
   }

   private boolean method5866(AABB aabb, Entity endCrystalEntity) {
      int tickCount = mc.player.tickCount;
      Vec3 eyePosition = mc.player.getEyePosition();
      double d = this.doubleValue - 0.15;
      var entities = mc.level.getEntities((Entity) null, aabb, playerEntity -> {
         if (!playerEntity.isSpectator() && playerEntity != endCrystalEntity && !this.method5868(playerEntity)) {
            if (!(playerEntity instanceof EndCrystal)) {
               return true;
            }

            boolean containsKey;
            label32: {
               var var10000x = this.items6;
               Integer id = playerEntity.getId();
               if (var10000x != null) {
                  if (var10000x.isEmpty()) {
                     containsKey = false;
                     break label32;
                  }
               }

               containsKey = var10000x.containsKey(id);
            }

            if (!containsKey || this.items6.get(playerEntity.getId()) - this.method5909() < tickCount || !(method5962(eyePosition, playerEntity.getBoundingBox()) <= d * d)) {
               return true;
            }
         }

         return false;
      });
      if (!entities.isEmpty()) {
         return false;
      } else {
         AABB inflate = aabb.inflate(0.1, 0.0, 0.1);

         for (CrystalAuraModule.Data5 data5 : this.items18) {
            if (data5.getBox().intersects(inflate) || data5.getBox2().intersects(inflate) || this.method5903(data5.getPlayerEntity()).intersects(aabb)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean method5867(BlockPos pos) {
      if (mc.level.isInWorldBounds(pos) && mc.level.getWorldBorder().isWithinBounds(pos)) {
         boolean containsKey;
         label61: {
            var map = this.items2;
            Long l = pos.asLong();
            if (map != null) {
               if (map.isEmpty()) {
                  containsKey = false;
                  break label61;
               }
            }

            containsKey = map.containsKey(l);
         }

         if (!containsKey) {
            if (mc.level.getBlockState(pos).canBeReplaced() && mc.level.getFluidState(pos).isEmpty()) {
               AABB aabb = new AABB(pos);
               var entities = mc.level
                  .getEntities((Entity) null, aabb, var1x -> var1x.blocksBuilding && !var1x.isRemoved() && !var1x.isSpectator() && !this.method5868(var1x));
               if (!entities.isEmpty()) {
                  return false;
               }

               for (CrystalAuraModule.Data5 data5 : this.items18) {
                  if (data5.getBox().intersects(aabb)) {
                     return false;
                  }
               }

               for (Direction direction : Direction.values()) {
                  if (!mc.level.getBlockState(pos.relative(direction)).canBeReplaced()) {
                     return true;
                  }
               }

               return false;
            }

            return false;
         }
      }

      return false;
   }

   private boolean method5868(Entity playerEntity) {
      for (CrystalAuraModule.Data5 data5 : this.items18) {
         if (data5.getPlayerEntity() == playerEntity) {
            return true;
         }
      }

      return false;
   }

   private boolean method5869() {
      return AutoTotemModule.INSTANCE.isEnabled() && AutoTotemModule.INSTANCE.method704();
   }

   private boolean method5870() {
      return AutoTotemModule.INSTANCE.isEnabled() && AutoTotemModule.INSTANCE.method705();
   }

   private boolean method5871(float f) {
      if (!(f <= 0.0F)
         && AutoTotemModule.INSTANCE.isEnabled()
         && !mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)
         && !AutoTotemModule.INSTANCE.method706()) {
         float f2 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         float f3 = Math.max(AutoTotemModule.INSTANCE.getHealthSetting().getValue3(), AutoTotemModule.INSTANCE.getCrystalMargin().getValue3());
         return f2 - f > f3 || !AutoTotemModule.INSTANCE.method703();
      } else {
         return true;
      }
   }

   private boolean method5872() {
      if (this.entry11.isEnabled() && AutoTrapModule.INSTANCE.isEnabled() && AutoTrapModule.INSTANCE.method2427()) {
         this.intValue11++;
         return this.intValue11 % 3 != 0;
      } else {
         this.intValue11 = 0;
         return false;
      }
   }

   private boolean method5873() {
      return this.entry3.isEnabled() && mc.level.dimension() != Level.NETHER && !mc.player.isShiftKeyDown();
   }

   private boolean method5874(Vec3 eyePosition, Vec3 center) {
      BlockHitResult clip = mc.level.clip(new ClipContext(eyePosition, center, Block.OUTLINE, Fluid.NONE, mc.player));
      return clip.getType() == HitResult.Type.BLOCK && eyePosition.distanceToSqr(clip.getLocation()) + 1.0E-4 < eyePosition.distanceToSqr(center);
   }

   private BlockHitResult method5875(Vec3 vec3d, Rotation rotation, double scale) {
      Vec3 to = vec3d.add(rotation.toDirection().scale(scale));
      return mc.level.clip(new ClipContext(vec3d, to, Block.OUTLINE, Fluid.NONE, mc.player));
   }

   private Rotation method5876(Rotation rotation) {
      float max;
      if ((max = this.data3.getFloatValue3()) >= 180.0F) {
         return rotation;
      } else {
         Rotation rotation2 = rotationManager.getEffectiveRotation();
         float f = Mth.clamp(Mth.wrapDegrees(rotation.getFloatValue() - rotation2.getFloatValue()), -max, max);
         float f2 = Mth.clamp(rotation.getFloatValue2() - rotation2.getFloatValue2(), -max, max);
         return new Rotation(rotation2.getFloatValue() + f, Mth.clamp(rotation2.getFloatValue2() + f2, -90.0F, 90.0F));
      }
   }

   private double method5877() {
      double d = mc.player == null
         ? this.data3.getFloatValue2()
         : mc.player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + (this.data3.isFlag6() ? 0.0 : 3.0);
      return Math.max(1.0, Math.min(Math.min((double)this.data3.getFloatValue2(), d), this.doubleValue) - 0.03F);
   }

   private double method5878() {
      double floatValue = mc.player == null
         ? this.data3.getFloatValue()
         : mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + (this.data3.isFlag6() ? 0.0 : 1.0);
      return Math.max(1.0, Math.min((double)this.data3.getFloatValue(), floatValue) - 0.03F);
   }

   private Vec3 method5879(Player playerEntity, Vec3 vec) {
      if (!playerEntity.onGround()) {
         return null;
      } else {
         double d;
         if ((d = 1.0 - method5881(playerEntity)) <= 0.0) {
            return null;
         } else {
            double[] doubles = new double[]{vec.x, -0.0784, vec.z};
            method5880(doubles, 0.4 * d, mc.player.getX() - playerEntity.getX(), mc.player.getZ() - playerEntity.getZ());
            float f = this.method5882(mc.player.getMainHandItem()) * 0.5F;
            if (mc.player.isSprinting() && mc.player.getAttackStrengthScale(0.5F) > 0.9F) {
               f += 0.5F;
            }

            if (f > 0.0F) {
               float f2 = this.method5897().getFloatValue() * (float) (Math.PI / 180.0);
               method5880(doubles, f * d, Mth.sin(f2), -Mth.cos(f2));
            }

            return new Vec3(doubles[0], doubles[1], doubles[2]);
         }
      }
   }

   private static void method5880(double[] doubles, double d, double d2, double d3) {
      double d4;
      if ((d4 = Math.sqrt(d2 * d2 + d3 * d3)) < 1.0E-5) {
         d2 = 0.0;
         d3 = 0.0;
      } else {
         d2 /= d4;
         d3 /= d4;
      }

      doubles[0] = doubles[0] / 2.0 - d2 * d;
      doubles[1] = Math.min(0.4, doubles[1] / 2.0 + d);
      doubles[2] = doubles[2] / 2.0 - d3 * d;
   }

   private static double method5881(Player playerEntity) {
      double d = 0.0;

      for (EquipmentSlot equipmentSlot : equipmentSlots) {
         ItemStack itemBySlot = playerEntity.getItemBySlot(equipmentSlot);
         if (itemBySlot.is(Items.NETHERITE_HELMET)
            || itemBySlot.is(Items.NETHERITE_CHESTPLATE)
            || itemBySlot.is(Items.NETHERITE_LEGGINGS)
            || itemBySlot.is(Items.NETHERITE_BOOTS)) {
            d += 0.1;
         }
      }

      return d;
   }

   private float method5882(ItemStack mainHandItem) {
      this.method5852();
      return this.registryEntry2 == null ? 0.0F : EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry2, mainHandItem);
   }

   private int method5883(Player playerEntity) {
      if (mc.getConnection() == null) {
         return this.intValue15;
      } else {
         PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(playerEntity.getUUID());
         return playerInfo == null ? this.intValue15 : Math.round(Math.max(0, playerInfo.getLatency()) / 50.0F);
      }
   }

   private AABB[] method5884(Player playerEntity, AABB aabb, Vec3 vec3d, int n) {
      AABB[] aabbs = new AABB[n + 1];
      aabbs[0] = aabb;
      AABB boundingBox = aabb;
      double x = vec3d.x;
      double y = vec3d.y;
      double z = vec3d.z;
      boolean flag = false;

      for (int i = 1; i <= n; i++) {
         if (!flag) {
            Vec3 collideBoundingBox = Entity.collideBoundingBox(playerEntity, new Vec3(x, y, z), boundingBox, mc.level, List.of());
            flag = y < 0.0 && collideBoundingBox.y > y + 1.0E-4;
            if (Math.abs(collideBoundingBox.x - x) > 1.0E-4) {
               x = 0.0;
            }

            if (Math.abs(collideBoundingBox.z - z) > 1.0E-4) {
               z = 0.0;
            }

            boundingBox = boundingBox.move(collideBoundingBox);
            y = (y - 0.08) * 0.98;
            x *= 0.91;
            z *= 0.91;
         }

         aabbs[i] = boundingBox;
      }

      return aabbs;
   }

   private void method5885() {
      this.playerEntity3 = null;
      this.rotation5 = null;
      this.floatValue5 = 0.0F;
      this.endCrystalEntity = null;
      if (this.entry13.isEnabled()
         && !AuraModule.INSTANCE.isEnabled()
         && (this.data4 == null || this.data4.getType() != CrystalAuraModule.Type.SHIELD || !this.data4.isFlag())) {
         int selected;
         if ((selected = this.method5891() ? -2 : this.method5853()) != -1) {
            this.method5889();
            if (this.playerEntity3 == null) {
               this.method5888();
            }

            if (this.playerEntity3 != null) {
               this.intValue30 = mc.player.tickCount;
               if (selected != -2) {
                  this.method5887(selected);
               }

               if (selected == -2 && !(mc.player.getAttackStrengthScale(0.5F) < 0.9F)) {
                  if (this.floatValue5 > 0.0F) {
                     EndCrystal endCrystalEntity = this.endCrystalEntity;
                     this.items16.removeIf(var1x -> var1x.getEndCrystalEntity() == endCrystalEntity);
                     this.data = this.items16.isEmpty()
                        ? null
                        : this.items16.getFirst();
                     if (this.method5925()) {
                        this.combatLogger
                           .method4612("COMBO", "план удара мечом по %s: кристалл на пике подлёта даст +%.1f", method5947(this.playerEntity3), this.floatValue5);
                     }
                  }
               } else {
                  this.playerEntity3 = null;
                  this.rotation5 = null;
                  this.endCrystalEntity = null;
               }
            }
         }
      }
   }

   private void method5886(boolean flag) {
      if (this.intValue28 != -1 && mc.player != null && mc.gameMode != null && this.blockPos3 == null && this.intValue14 == -1) {
         if (flag || this.method5896(this.intValue30) > 60) {
            if (mc.player.getInventory().getSelectedSlot() == this.intValue29 && this.intValue28 != this.intValue29) {
               mc.player.getInventory().setSelectedSlot(this.intValue28);
               mc.gameMode.ensureHasSentCarriedItem();
               if (this.method5925()) {
                  this.combatLogger.method4612("SWAP", "меч больше не нужен — возвращаем слот %d", this.intValue28);
               }
            }

            this.intValue28 = -1;
            this.intValue29 = -1;
         }
      }
   }

   private void method5887(int selected) {
      if (selected >= 0 && mc.player.getInventory().getSelectedSlot() != selected && this.intValue14 == -1 && this.blockPos3 == null) {
         if (this.intValue28 == -1) {
            this.intValue28 = mc.player.getInventory().getSelectedSlot();
         }

         mc.player.getInventory().setSelectedSlot(selected);
         mc.gameMode.ensureHasSentCarriedItem();
         this.intValue29 = selected;
         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  "SWORD", "берём %s в руку (слот %d) — ударим, когда наберётся кулдаун", method5950(mc.player.getInventory().getItem(selected).getItem()), selected
               );
         }
      }
   }

   private void method5888() {
      boolean flag = this.data != null
         || this.data4 != null && this.data4.isFlag()
         || this.data2 != null
         || !this.items.isEmpty();
      double d = this.method5877();
      float f = this.method5893(1.0F);
      int n = mc.player.tickCount + this.method5818();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         Player playerEntity = data5.getPlayerEntity();
         AABB aabb = this.method5903(playerEntity);
         if (!(method5962(this.vec3d, aabb) > d * d)) {
            CrystalAuraModule.Entry entry = this.items14.get(playerEntity.getId());
            boolean flag2 = this.class35.method5457(playerEntity, f) >= data5.getFloatValue() && !data5.isFlag();
            if (!flag || flag2) {
               float f2 = data5.getIntValue() > 10 ? data5.getFloatValue2() : 0.0F;
               if (entry != null && entry.method5969(n)) {
                  f2 = Math.max(f2, entry.floatValue8);
               }

               if (!(f <= f2)) {
                  Vec3 vec = new Vec3(
                     Mth.clamp(this.vec3d.x, aabb.minX + 0.05, aabb.maxX - 0.05),
                     Mth.clamp(this.vec3d.y, aabb.minY + 0.2, aabb.maxY - 0.2),
                     Mth.clamp(this.vec3d.z, aabb.minZ + 0.05, aabb.maxZ - 0.05)
                  );
                  this.playerEntity3 = playerEntity;
                  this.rotation5 = method5898(this.vec3d, vec);
                  this.floatValue5 = 0.0F;
                  return;
               }
            }
         }
      }
   }

   private void method5889() {
      double d = this.method5877();

      for (CrystalAuraModule.Data5 data5 : this.items18) {
         Player playerEntity = data5.getPlayerEntity();
         CrystalAuraModule.Entry entry = this.items14.get(playerEntity.getId());
         AABB aabb = this.method5903(playerEntity);
         if (entry != null && playerEntity.onGround() && !entry.method5966(mc.player.tickCount) && data5.getIntValue() <= 10 && !(method5962(this.vec3d, aabb) > d * d)) {
            Vec3 vec3d = this.method5879(playerEntity, entry.method5971());
            if (vec3d != null && !(vec3d.y < 0.15)) {
               AABB[] aabbs = this.method5884(playerEntity, aabb, vec3d, 14);
               float f = this.method5892();

               for (EndCrystal endCrystalEntity : mc.level.getEntitiesOfClass(EndCrystal.class, aabb.inflate(6.0), var1x -> {
                  if (var1x.isAlive()) {
                     boolean containsKey;
                     label20: {
                        var map = this.items6;
                        Integer id = var1x.getId();
                        if (map != null) {
                           if (map.isEmpty()) {
                              containsKey = false;
                              break label20;
                           }
                        }

                        containsKey = map.containsKey(id);
                     }

                     if (!containsKey) {
                        return true;
                     }
                  }

                  return false;
               })) {
                  if (!(method5962(this.vec3d, endCrystalEntity.getBoundingBox()) > d * d)) {
                     Vec3 position = endCrystalEntity.position();
                     float f2 = this.class35.method5452(position, 6.0F, playerEntity, aabb, null, null);
                     float f3 = 0.0F;

                     for (int i = 2; i < aabbs.length; i++) {
                        float f4 = this.class35.method5454(position, 6.0F, playerEntity, aabbs[i], null, null) - (i < 10 ? f : 0.0F);
                        f3 = Math.max(f3, f4 <= 0.0F ? 0.0F : this.class35.method5456(playerEntity, f4));
                     }

                     float f4 = f3 - f2;
                     if (f3 >= this.minDamage.getValue3() && f4 >= 4.0F && f4 > this.floatValue5) {
                        Vec3 vec = new Vec3(
                           Mth.clamp(this.vec3d.x, aabb.minX + 0.05, aabb.maxX - 0.05),
                           Mth.clamp(this.vec3d.y, aabb.minY + 0.2, aabb.maxY - 0.2),
                           Mth.clamp(this.vec3d.z, aabb.minZ + 0.05, aabb.maxZ - 0.05)
                        );
                        this.playerEntity3 = playerEntity;
                        this.rotation5 = method5898(this.vec3d, vec);
                        this.floatValue5 = f4;
                        this.endCrystalEntity = endCrystalEntity;
                     }
                  }
               }
            }
         }
      }
   }

   private void method5890(Vec3 eyePosition, Rotation rotation) {
      Player playerEntity3 = this.playerEntity3;
      if (playerEntity3 != null && this.method5919() && playerEntity3.isAlive() && !(mc.player.getAttackStrengthScale(0.5F) < 0.9F)) {
         if (this.items16.isEmpty() && this.method5891()) {
            AABB inflate = playerEntity3.getBoundingBox().inflate(playerEntity3.getPickRadius());
            double scale = this.method5877();
            if (!inflate.clip(eyePosition, eyePosition.add(rotation.toDirection().scale(scale))).isEmpty() && (this.data3.isFlag5() || !this.method5874(eyePosition, inflate.getCenter()))
               )
             {
               float f = this.method5892();
               boolean flag = this.floatValue5 > 0.0F;
               mc.gameMode.attack(mc.player, playerEntity3);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
               this.intValue8++;
               if (this.method5925()) {
                  this.combatLogger
                     .method4612(
                        flag ? "COMBO" : "SWORD",
                        "удар мечом по %s в %.2f б, raw %.1f, спринт %s%s",
                        method5947(playerEntity3),
                        Math.sqrt(method5962(eyePosition, inflate)),
                        f,
                        mc.player.isSprinting() ? "да" : "нет",
                        flag ? " — ждём подлёт под кристалл" : " — кристаллам сейчас нечего делать"
                     );
               }

               this.playerEntity3 = null;
               this.rotation5 = null;
               this.method5860();
            }
         }
      }
   }

   private boolean method5891() {
      ItemStack mainHandItem = mc.player.getMainHandItem();
      return mainHandItem.is(ItemTags.SWORDS) || mainHandItem.is(ItemTags.AXES);
   }

   private float method5892() {
      return this.method5893(mc.player.getAttackStrengthScale(0.5F));
   }

   private float method5893(float attackStrengthScale) {
      ItemStack mainHandItem = mc.player.getMainHandItem();
      int slot;
      if (!this.method5891() && (slot = this.method5853()) != -1) {
         mainHandItem = mc.player.getInventory().getItem(slot);
      }

      double d = this.method5854() + method5855(mainHandItem);
      this.method5852();
      int itemEnchantmentLevel = this.registryEntry3 == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(this.registryEntry3, mainHandItem);
      double d2 = d * (0.2 + attackStrengthScale * attackStrengthScale * 0.8);
      if (attackStrengthScale > 0.9F
         && mc.player.fallDistance > 0.0
         && !mc.player.onGround()
         && !mc.player.onClimbable()
         && !mc.player.isInWater()
         && !mc.player.isSprinting()) {
         d2 *= 1.5;
      }

      d2 += (itemEnchantmentLevel > 0 ? 0.5 * itemEnchantmentLevel + 0.5 : 0.0) * attackStrengthScale;
      return (float)Math.max(0.0, d2);
   }

   private boolean method5894(CrystalAuraModule.Data12 data12, Vec3 position) {
      if (this.entry13.isEnabled() && data12.getPlayerEntity() != null) {
         for (CrystalAuraModule.Data5 data5 : this.items18) {
            AABB[] boxs = data5.getBoxs();
            if (data5.getPlayerEntity() == data12.getPlayerEntity() && boxs != null) {
               int n;
               if ((n = data5.getIntValue3()) < 0 || n < boxs.length && !(boxs[n].minY <= boxs[0].minY + 0.1)) {
                  if (data12.getFloatValue() >= data5.getFloatValue()) {
                     return false;
                  }

                  float f = 0.0F;

                  for (int i = 1; i <= 12 && data5.getIntValue3() + i < boxs.length; i++) {
                     float f2 = this.method5823(data5, position, 6.0F, boxs[Math.max(0, data5.getIntValue3() + i)], null, null, this.method5818() + i)
                        * (1.0F - 0.02F * i);
                     f = Math.max(f, f2);
                  }

                  return f > data12.getFloatValue() * 1.15F + 1.5F;
               }

               return false;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean method5895(CrystalAuraModule.Data data, int tickCount) {
      return this.entry18.isEnabled()
            && !(data.getFloatValue() > 4.0F)
            && data.getPlayerEntity() != null
            && !(data.getFloatValue() >= method5902(data.getPlayerEntity()))
            && !(this.floatValue2 >= this.shieldDamage.getValue3())
         ? this.method5896(this.intValue18) >= 10
         : true;
   }

   private int method5896(int intValue) {
      return intValue != (-32768 << 16 | 0) && mc.player != null ? mc.player.tickCount - intValue : 32767 << 16 | (char)-1;
   }

   private Rotation method5897() {
      Rotation rotation2 = rotationManager.getRotation2();
      return rotation2 == null ? new Rotation(mc.player.getYRot(), mc.player.getXRot()) : rotation2;
   }

   private static Rotation method5898(Vec3 vec3d, Vec3 vec) {
      return RotationUtils.method6912(vec.subtract(vec3d));
   }

   private static boolean method5899(Rotation rotation, Rotation rotation3) {
      return rotation != null
         && rotation3 != null
         && Math.abs(Mth.wrapDegrees(rotation.getFloatValue() - rotation3.getFloatValue())) < 0.001F
         && Math.abs(rotation.getFloatValue2() - rotation3.getFloatValue2()) < 0.001F;
   }

   private List<CrystalAuraModule.Data5> method5900(Vec3 eyePosition, int tickCount) {
      double d = this.targetRange.getValue3();
      ArrayList<Player> players = new ArrayList<>();

      for (Player player : mc.level.players()) {
         if (player != mc.player
            && method5901(player)
            && !Zenith.getInstance().getFriends().method7626(player)
            && !AntiBotModule.INSTANCE.method2785(player)
            && method5962(eyePosition, this.method5903(player)) <= d * d) {
            players.add(player);
         }
      }

      players.sort(Comparator.comparingDouble((Player var2x) -> method5962(eyePosition, this.method5903(var2x))));
      this.items14.keySet().removeIf(var1x -> players.stream().noneMatch((Player var1xx) -> var1xx.getId() == var1x));
      int scale = Math.max(1, (this.intValue15 + 1) / 2);
      int n = this.predictTicks.getValue3() <= 0.0F ? 0 : Mth.clamp(Math.round(this.predictTicks.getValue3()) + this.intValue15 + scale, 0, 12);
      List<Data5> data5s = new ArrayList<>(Math.min(players.size(), 3));

      for (int i = 0; i < Math.min(players.size(), 3); i++) {
         Player playerEntity = (Player)players.get(i);
         CrystalAuraModule.Entry entry = this.items14.computeIfAbsent(playerEntity.getId(), n2 -> new CrystalAuraModule.Entry());
         Vec3 base = playerEntity.getPositionCodec().getBase();
         entry.method5970(playerEntity, base, tickCount, this.data8, this.class35, this.items23.remove(playerEntity.getId()), this.method5818());
         if (entry.floatValue5 > 0.0F && this.method5925()) {
            this.combatLogger.floatValue2 = this.combatLogger.floatValue2 + entry.floatValue5;
            this.combatLogger.method4612("THP", "%s hp -%.1f -> %.1f", method5947(playerEntity), entry.floatValue5, method5902(playerEntity));
         }

         AABB boundingBox = this.method5903(playerEntity);
         Vec3 deltaMovement = entry.method5971();
         boolean flag = playerEntity.onGround();
         AABB box4 = null;
         AABB[] boxs = null;
         int n2 = 0;
         int n3 = 0;
         if (this.entry13.isEnabled() && entry.method5966(tickCount)) {
            n3 = (n2 = tickCount - entry.intValue5 - entry.intValue6) + this.intValue15 + 1;
            boxs = this.method5884(playerEntity, entry.box, entry.vec3d, Math.max(n3, 0) + 12);
            int n4;
            if ((n4 = n2 - this.intValue15) >= 1 && n4 < boxs.length) {
               AABB aabb = boxs[n4];
               Vec3 center = boundingBox.getCenter();
               if (Math.abs(boundingBox.minY - aabb.minY) > 0.45 || center.subtract(aabb.getCenter()).horizontalDistanceSqr() > 0.64) {
                  entry.box = null;
                  boxs = null;
                  if (this.method5925()) {
                     this.combatLogger
                        .method4612(
                           "KNOCK",
                           "%s: подлёт не подтвердился (ждали y+%.2f, видим y+%.2f) — прогноз сброшен",
                           method5947(playerEntity),
                           aabb.minY - entry.doubleValue,
                           boundingBox.minY - entry.doubleValue
                        );
                  }
               }
            }
         }

         AABB move;
         AABB box2;
         AABB box3;
         if (boxs != null) {
            move = boxs[Mth.clamp(n2, 0, boxs.length - 1)];
            box2 = boxs[Mth.clamp(n3, 0, boxs.length - 1)];
            box3 = box2;

            for (int j = Math.max(n3, 0); j < boxs.length; j++) {
               if (boxs[j].minY > box3.minY) {
                  box3 = boxs[j];
               }
            }

            if (n2 >= 0 && n2 + 2 < boxs.length && boxs[n2 + 2].minY > move.minY + 0.1) {
               box4 = boxs[n2 + 2];
            }
         } else {
            move = playerEntity.isFallFlying() ? boundingBox.move(deltaMovement.scale(scale)) : this.method5906(playerEntity, boundingBox, deltaMovement, flag, scale);
            box2 = playerEntity.isFallFlying() ? boundingBox.move(deltaMovement.scale(n)) : this.method5906(playerEntity, boundingBox, deltaMovement, flag, n);
            box3 = flag && this.method5945(boundingBox)
               ? move
               : (flag ? this.method5906(playerEntity, move, new Vec3(deltaMovement.x, 0.42, deltaMovement.z), false, 6) : this.method5907(playerEntity, move, deltaMovement));
         }

         float floatValue = method5902(playerEntity);
         boolean flag2 = playerEntity.getOffhandItem().is(Items.TOTEM_OF_UNDYING) || playerEntity.getMainHandItem().is(Items.TOTEM_OF_UNDYING);
         int invulnerableTime = playerEntity.getInvulnerableTime();
         int n4 = entry.intValue3 == (-32768 << 16 | 0) ? 32767 << 16 | (char)-1 : tickCount - entry.intValue3;
         data5s.add(
            new CrystalAuraModule.Data5(
               playerEntity,
               base,
               move,
               box2,
               box3,
               floatValue,
               flag2,
               this.method5904(playerEntity),
               invulnerableTime,
               entry.floatValue3,
               box4,
               n4,
               entry.intValue4 > tickCount,
               boxs,
               n2
            )
         );
      }

      return data5s;
   }

   public static boolean method5901(Player player) {
      return !player.isRemoved() && !player.isSpectator() && (player.getHealth() > 0.0F || player.deathTime == 0);
   }

   public static float method5902(Player playerEntity) {
      float health;
      if ((health = playerEntity.getHealth()) <= 0.0F || Float.isNaN(health)) {
         health = playerEntity.getMaxHealth();
      }

      return health + playerEntity.getAbsorptionAmount();
   }

   private AABB method5903(Player playerEntity) {
      Vec3 base = playerEntity.getPositionCodec().getBase();
      Vec3 pos = base.subtract(playerEntity.position());
      return pos.lengthSqr() > 64.0 ? playerEntity.getBoundingBox() : playerEntity.getBoundingBox().move(pos);
   }

   private boolean method5904(Player playerEntity) {
      float f;
      if ((f = this.armorBreak.getValue3() / 100.0F) <= 0.0F) {
         return false;
      } else {
         for (EquipmentSlot equipmentSlot : equipmentSlots) {
            ItemStack itemBySlot = playerEntity.getItemBySlot(equipmentSlot);
            if (itemBySlot.isDamageableItem() && itemBySlot.getMaxDamage() > 0 && (float)(itemBySlot.getMaxDamage() - itemBySlot.getDamageValue()) / itemBySlot.getMaxDamage() <= f) {
               return true;
            }
         }

         return false;
      }
   }

   private List<Player> method5905(Vec3 eyePosition) {
      double d = this.targetRange.getValue3() + 6.0;
      ArrayList<Player> players = new ArrayList<>();

      for (Player player : mc.level.players()) {
         if (player != mc.player
            && method5901(player)
            && Zenith.getInstance().getFriends().method7626(player)
            && method5962(eyePosition, player.getBoundingBox()) <= d * d) {
            players.add(player);
         }
      }

      return players;
   }

   private AABB method5906(Player playerEntity, AABB boundingBox, Vec3 deltaMovement, boolean flag, int scale) {
      AABB aabb = boundingBox;
      double x = deltaMovement.x;
      double y = flag && deltaMovement.y <= 0.0 ? -0.0784 : deltaMovement.y;
      double z = deltaMovement.z;
      if (Math.hypot(x, z) < 0.03) {
         x = 0.0;
         z = 0.0;
      }

      for (int i = 0; i < scale; i++) {
         Vec3 collideBoundingBox = Entity.collideBoundingBox(playerEntity, new Vec3(x, y, z), aabb, mc.level, List.of());
         boolean flag2 = y < 0.0 && collideBoundingBox.y > y + 1.0E-4;
         boolean flag3 = y > 0.0 && collideBoundingBox.y < y - 1.0E-4;
         if (Math.abs(collideBoundingBox.x - x) > 1.0E-4) {
            x = 0.0;
         }

         if (Math.abs(collideBoundingBox.z - z) > 1.0E-4) {
            z = 0.0;
         }

         aabb = aabb.move(collideBoundingBox);
         y = flag2 ? -0.0784 : ((flag3 ? 0.0 : y) - 0.08) * 0.98;
      }

      return aabb;
   }

   private AABB method5907(Player playerEntity, AABB move, Vec3 deltaMovement) {
      AABB boundingBox = move;
      double x = deltaMovement.x;
      double y = deltaMovement.y;
      double z = deltaMovement.z;

      for (int i = 0; i < 30; i++) {
         Vec3 collideBoundingBox = Entity.collideBoundingBox(playerEntity, new Vec3(x, y, z), boundingBox, mc.level, List.of());
         boundingBox = boundingBox.move(collideBoundingBox);
         if (y < 0.0 && collideBoundingBox.y > y + 1.0E-4) {
            return boundingBox;
         }

         if (Math.abs(collideBoundingBox.x - x) > 1.0E-4) {
            x = 0.0;
         }

         if (Math.abs(collideBoundingBox.z - z) > 1.0E-4) {
            z = 0.0;
         }

         y = (y - 0.08) * 0.98;
      }

      return boundingBox;
   }

   private boolean method5908(BlockPos below) {
      Integer n = this.items3.get(below.asLong());
      return n != null && mc.player != null && n >= mc.player.tickCount;
   }

   private int method5909() {
      return Mth.clamp(this.intValue15 + 3, 3, 20);
   }

   private int method5910() {
      if (mc.getConnection() == null) {
         return 0;
      } else {
         PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(mc.player.getUUID());
         return playerInfo == null ? 0 : Math.max(0, playerInfo.getLatency());
      }
   }

   private boolean method5911() {
      return mc.player.getAttributeValue(Attributes.ATTACK_DAMAGE) <= 0.0;
   }

   private int method5912(BlockState blockState) {
      int n = -1;
      float destroySpeed = 1.0F;

      for (int i = 0; i < 9; i++) {
         float f;
         if ((f = mc.player.getInventory().getItem(i).getDestroySpeed(blockState)) > destroySpeed) {
            destroySpeed = f;
            n = i;
         }
      }

      return n;
   }

   private void method5913() {
      if (this.entry6.isEnabled() && !this.class33.pending() && this.data3.getType() != org.zenith.utility.misc.Data3.Type.OFF) {
         this.method5914(stack -> stack.is(Items.END_CRYSTAL), true);
         this.method5914(stack -> stack.is(Items.OBSIDIAN), this.entry4.isEnabled() || this.entry7.isEnabled());
         this.method5914(stack -> stack.is(Items.RESPAWN_ANCHOR), this.method5873());
         this.method5914(stack -> stack.is(Items.GLOWSTONE), this.method5873());
      }
   }

   private void method5914(Predicate<ItemStack> predicate, boolean flag) {
      if (flag && !this.class33.pending() && !this.method5915(predicate) && this.class33.hasMatchingInventoryItem(predicate)) {
         this.class33.requestInventoryMove(predicate);
      }
   }

   private boolean method5915(Predicate<ItemStack> predicate) {
      return !predicate.test(mc.player.getOffhandItem()) && !predicate.test(mc.player.getMainHandItem())
         ? this.data3.getType() != org.zenith.utility.misc.Data3.Type.OFF && this.method5916(predicate) != -1
         : true;
   }

   private int method5916(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 9; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private boolean method5917() {
      return this.method5918() == null;
   }

   private String method5918() {
      if (mc.player.isSpectator() || mc.player.isDeadOrDying() || mc.player.isPassenger()) {
         return "наблюдатель / мёртв / в транспорте";
      } else if (mc.player.containerMenu != mc.player.inventoryMenu) {
         return "открыт контейнер";
      } else {
         if (this.entry10.isEnabled() && mc.player.isUsingItem()) {
            ItemUseAnimation useAnimation = mc.player.getUseItem().getUseAnimation();
            if (useAnimation == ItemUseAnimation.EAT || useAnimation == ItemUseAnimation.DRINK) {
               return "едим/пьём (пауза при использовании)";
            }
         }

         return FreeCamModule.INSTANCE.isEnabled() ? "FreeCam" : null;
      }
   }

   private boolean method5919() {
      return this.intValue8 < this.data3.getIntValue();
   }

   private boolean method5920() {
      return this.method5922(this.intValue17, this.data3.getIntValue3());
   }

   private boolean method5921() {
      return this.method5922(this.intValue16, this.data3.getIntValue2());
   }

   private boolean method5922(int n, int n2) {
      return n2 > 0 && n != (-32768 << 16 | 0) && mc.player != null ? mc.player.tickCount - n >= Math.max(1, Math.round(n2 / 50.0F)) : true;
   }

   private boolean method5923(BlockPos below) {
      var map = this.items5;
      Long l = below.asLong();
      if (map != null) {
         if (map.isEmpty()) {
            return false;
         }
      }

      return map.containsKey(l);
   }

   private void method5924(int tickCount) {
      ArrayList arrayList = new ArrayList();

      CrystalAuraModule.Data7 data7;
      while ((data7 = this.queue.poll()) != null) {
         long longValue = data7.getLongValue();
         BlockState state = data7.state();
         boolean flag;
         if (!(flag = data7.getBlockState2() != null && data7.getBlockState2().getBlock() == state.getBlock())) {
            boolean containsKey;
            label202: {
               var map = this.items2;
               Long l = longValue;
               if (map != null) {
                  if (map.isEmpty()) {
                     containsKey = false;
                     break label202;
                  }
               }

               containsKey = map.containsKey(l);
            }

            if (!containsKey
               && method5956(state)
               && !this.items18.isEmpty()
               && BlockPos.of(longValue).closerThan(mc.player.blockPosition(), 5.0)) {
               arrayList.add(longValue);
            }
         }

         label190:
         if (this.method5925()) {
            if (flag) {
               boolean containsKey;
               label186: {
                  var map = this.items2;
                  Long l = longValue;
                  if (map != null) {
                     if (map.isEmpty()) {
                        containsKey = false;
                        break label186;
                     }
                  }

                  containsKey = map.containsKey(l);
               }

               if (!containsKey) {
                  break label190;
               }
            }

            if (BlockPos.of(longValue).closerThan(mc.player.blockPosition(), 4.0)) {
               CombatLogger combatLogger;
               boolean containsKey;
               Object[] string;
               label177: {
                  combatLogger = this.combatLogger;
                  string = new Object[]{method5948(BlockPos.of(longValue)), state.isAir() ? "воздух" : method5950(state.getBlock().asItem()), null};
                  var map = this.items2;
                  Long l = longValue;
                  if (map != null) {
                     if (map.isEmpty()) {
                        containsKey = false;
                        break label177;
                     }
                  }

                  containsKey = map.containsKey(l);
               }

               string[2] = containsKey ? " (наш)" : "";
               combatLogger.method4612("BLOCK", "%s -> %s%s", string);
            }
         }

         boolean containsKey;
         label172: {
            var map = this.items2;
            Long l = longValue;
            if (map != null) {
               if (map.isEmpty()) {
                  containsKey = false;
                  break label172;
               }
            }

            containsKey = map.containsKey(l);
         }

         if (containsKey) {
            if (!state.canBeReplaced()) {
               this.items2.remove(longValue);
               this.placementQueue.method2813(longValue * 4L + 1L);
            } else if (state.canBeReplaced()) {
               this.items2.remove(longValue);
               this.placementQueue.method2814(longValue * 4L + 1L);
               this.items5.put(longValue, tickCount + 10 + this.intValue15);
            }
         }

         if (state.is(Blocks.RESPAWN_ANCHOR) && state.getValue(RespawnAnchorBlock.CHARGE) > 0) {
            this.placementQueue.method2813(longValue * 4L + 2L);
         }

         if (state.canBeReplaced()) {
            label164: {
               var map = this.items20;
               Long l = longValue;
               if (map != null) {
                  if (map.isEmpty()) {
                     containsKey = false;
                     break label164;
                  }
               }

               containsKey = map.containsKey(l);
            }

            if (containsKey && this.method5772().contains(BlockPos.of(longValue))) {
               label156: {
                  var map = this.items2;
                  Long l = longValue;
                  if (map != null) {
                     if (map.isEmpty()) {
                        containsKey = false;
                        break label156;
                     }
                  }

                  containsKey = map.containsKey(l);
               }

               if (!containsKey) {
                  this.items22.put(longValue, tickCount + 40);
                  if (this.method5925()) {
                     Object[] string;
                     boolean containsKey2;
                     org.zenith.utility.config.CombatLogger combatLogger;
                     label149: {
                        combatLogger = this.combatLogger;
                        string = new Object[]{method5948(BlockPos.of(longValue)), null};
                        var map = this.items20;
                        Long l = longValue;
                        if (map != null) {
                           if (map.isEmpty()) {
                              containsKey2 = false;
                              break label149;
                           }
                        }

                        containsKey2 = map.containsKey(l);
                     }

                     string[1] = containsKey2 ? " (его копали)" : "";
                     combatLogger.method4612("ANTIMINE", "сурраунд %s выбит%s", string);
                  }
               }
            }

            this.items20.remove(longValue);
            this.items21.remove(longValue);
         }
      }

      if ((!arrayList.isEmpty()) && arrayList.size() <= 3) {
         var iterator = arrayList.iterator();

         while (iterator.hasNext()) {
            long blockNode;
            Vec3 atCenterOf = Vec3.atCenterOf(BlockPos.of(blockNode = (Long)iterator.next()));
            if (this.items18.stream().anyMatch(var1x -> var1x.getPlayerEntity().getEyePosition().distanceTo(atCenterOf) <= 6.0)) {
               this.items8.put(blockNode, tickCount + 60);
               if (this.method5925()) {
                  this.combatLogger.method4612("ENEMY", "поставил основание %s рядом с нами", method5948(BlockPos.of(blockNode)));
               }
            }
         }
      }
   }

   private boolean method5925() {
      return !this.flag4 && this.entry20.isEnabled() && this.combatLogger.active();
   }

   public void method5926() {
      if (!this.isEnabled() && this.combatLogger.active()) {
         this.combatLogger.method4611(false, "");
      }
   }

   public void method5927(String str) {
      if (this.entry20.isEnabled() && this.combatLogger.active()) {
         this.combatLogger.method4612("TOTEM", "%s (в руке у клиента: %s)", str, mc.player == null ? "?" : method5950(mc.player.getOffhandItem().getItem()));
      }
   }

   private void method5928(Packet<?> packet3, Player player) {
      if (packet3 instanceof ClientboundPlayerCombatKillPacket playerCombatKillPacket && playerCombatKillPacket.playerId() == player.getId()) {
         this.combatLogger.method4612("DEATH", "%s", playerCombatKillPacket.message().getString());
         this.combatLogger.method4616("смерть");
      } else if (packet3 instanceof ClientboundSetHealthPacket setHealthPacket) {
         float f = this.floatValue4;
         float health = setHealthPacket.getHealth();
         this.floatValue4 = health;
         if (f >= 0.0F && Math.abs(health - f) > 0.01F) {
            if (health < f) {
               this.combatLogger.floatValue += f - health;
            }

            this.combatLogger.method4612("HP", "наше hp %.1f -> %.1f (%+.1f), absorption %.1f", f, health, health - f, player.getAbsorptionAmount());
         }
      } else if (packet3 instanceof ClientboundPlayerPositionPacket playerPositionPacket) {
         Vec3 position = playerPositionPacket.change().position();
         CombatLogger combatLogger = this.combatLogger;
         Object[] string = new Object[]{method5949(position), null, null, null, null, null, null};
         var relatives = playerPositionPacket.relatives();
         string[1] = (relatives.isEmpty())
            ? ""
            : " (относительно: " + playerPositionPacket.relatives() + ")";
         string[2] = method5949(player.position());
         string[3] = method5949(player.getDeltaMovement());
         string[4] = player.onGround() ? "да" : "нет";
         string[5] = player.fallDistance;
         string[6] = this.rotation != null ? "да" : "нет";
         combatLogger.method4612("SETBACK", "сервер переставил нас в %s%s; были в %s, скорость %s, на земле %s, падение %.2f, держим поворот %s", string);
      } else if (packet3 instanceof ClientboundDamageEventPacket || packet3 instanceof net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket || packet3 instanceof ClientboundSetEquipmentPacket) {
         if (packet3 instanceof ClientboundDamageEventPacket damageEventPacket) {
            this.items23.put(damageEventPacket.entityId(), damageEventPacket.sourceType().is(DamageTypeTags.IS_EXPLOSION));
         }

         this.queue5.add(packet3);
      }
   }

   private void method5929() {
      Packet packet;
      while ((packet = this.queue5.poll()) != null) {
         if (this.method5925()) {
            if (packet instanceof ClientboundDamageEventPacket damageEventPacket) {
               Entity entity = mc.level.getEntity(damageEventPacket.entityId());
               Entity playerEntity = damageEventPacket.sourceCauseId() >= 0 ? mc.level.getEntity(damageEventPacket.sourceCauseId()) : null;
               String registeredName = damageEventPacket.sourceType().getRegisteredName();
               if (entity == mc.player) {
                  this.combatLogger
                     .method4612(
                        "HURT",
                        "по нам: %s от %s%s",
                        registeredName,
                        method5947(playerEntity),
                        damageEventPacket.sourcePosition()
                           .map(position -> " из " + method5949(position) + String.format(Locale.ROOT, " (%.1f б)", position.distanceTo(mc.player.position())))
                           .orElse("")
                     );
               } else if (entity instanceof Player player && this.method5868(player)) {
                  this.combatLogger
                     .method4612(
                        "HIT", "по цели %s: %s от %s, hp %.1f", method5947(player), registeredName, playerEntity == mc.player ? "нас" : method5947(playerEntity), method5902(player)
                     );
               }
            } else if (packet instanceof net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket swingAnimationPacket) {
               // 26.3: swings arrive as their own packet instead of ClientboundAnimatePacket actions 0/3
               if (mc.level.getEntity(swingAnimationPacket.entityId()) instanceof Player playerEntity2 && this.method5868(playerEntity2)) {
                  this.combatLogger
                     .method4612(
                        "ENEMY",
                        "%s взмах %s рукой, в руке %s, дист %.1f",
                        method5947(playerEntity2),
                        swingAnimationPacket.hand() == net.minecraft.world.InteractionHand.MAIN_HAND ? "правой" : "левой",
                        method5950(playerEntity2.getMainHandItem().getItem()),
                        playerEntity2.distanceTo(mc.player)
                     );
               }
            } else if (packet instanceof ClientboundSetEquipmentPacket setEquipmentPacket
               && mc.level.getEntity(setEquipmentPacket.getEntity()) instanceof Player playerEntity3
               && this.method5868(playerEntity3)) {
               StringBuilder builder = new StringBuilder();
               setEquipmentPacket.getSlots()
                  .forEach(
                     var1x -> builder.append(' ')
                        .append(((EquipmentSlot)var1x.getFirst()).getName())
                        .append('=')
                        .append(
                           ((ItemStack)var1x.getSecond()).isEmpty()
                              ? "пусто"
                              : method5950(((ItemStack)var1x.getSecond()).getItem()) + "x" + ((ItemStack)var1x.getSecond()).getCount()
                        )
                  );
               this.combatLogger.method4612("ENEMY", "%s сменил экипировку:%s", method5947(playerEntity3), builder);
            }
         }
      }
   }

   private void method5930() {
      if (this.method5925()) {
         StringBuilder builder = new StringBuilder();
         builder.append(
            String.format(
               Locale.ROOT,
               "мы hp %.1f%s%s нора=%s кристаллов %d обсы %d",
               mc.player.getHealth(),
               mc.player.getAbsorptionAmount() > 0.0F ? String.format(Locale.ROOT, "+%.0f", mc.player.getAbsorptionAmount()) : "",
               mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING) ? " тотем" : " БЕЗ ТОТЕМА",
               this.method5945(mc.player.getBoundingBox()) ? "да" : "нет",
               this.method5946(Items.END_CRYSTAL),
               this.method5946(Items.OBSIDIAN)
            )
         );

         for (CrystalAuraModule.Data5 data5 : this.items18) {
            builder.append(
               String.format(
                  Locale.ROOT,
                  " | %s hp %.1f%s дист %.1f нора=%s%s%s",
                  method5947(data5.getPlayerEntity()),
                  data5.getFloatValue(),
                  data5.isFlag() ? " тотем" : "",
                  data5.getVec3d().distanceTo(mc.player.position()),
                  this.method5945(data5.getBox()) ? "да" : "нет",
                  data5.getBox4() != null ? " ПОДЛЁТ" : "",
                  data5.getIntValue2() <= 10 ? " ПОПНУЛСЯ" : ""
               )
            );
         }

         builder.append(" || break ").append(this.method5944(this.data));
         builder.append(" | план ").append(this.method5944(this.data4));
         if (this.data2 != null) {
            builder.append(" | ждём ").append(this.method5944(this.data2));
         }

         if (this.data5 != null) {
            builder.append(" | деф ").append(this.method5944(this.data5));
         }

         builder.append(
            String.format(Locale.ROOT, " | опасность %.1f | спотов %d/%d без прицела %d", this.floatValue2, this.intValue24, this.intValue23, this.intValue25)
         );
         if (this.blockPos3 != null) {
            builder.append(String.format(Locale.ROOT, " | копаем %s %.0f%%", method5948(this.blockPos3), this.floatValue * 100.0F));
         }

         this.combatLogger.method4615(builder.toString());
      }
   }

   private void method5931(int tickCount) {
      this.method5939();
      if (!this.method5925()) {
         this.jsonObject = null;
      } else {
         this.jsonObject = new JsonObject();
         this.jsonObject.addProperty("t", tickCount);
         this.jsonObject.addProperty("ms", System.currentTimeMillis());
         this.jsonArray = new JsonArray();
         this.jsonArray2 = new JsonArray();
         this.items24.clear();
         this.text3 = null;
         this.floatValue3 = -1.0F;
         JsonObject json = new JsonObject();
         json.addProperty("pos", method5941(mc.player.position()));
         json.addProperty("vel", method5941(mc.player.getDeltaMovement()));
         json.addProperty("yaw", round(mc.player.getYRot()));
         json.addProperty("pitch", round(mc.player.getXRot()));
         Rotation rotation = this.method5897();
         json.addProperty("srvYaw", round(rotation.getFloatValue()));
         json.addProperty("srvPitch", round(rotation.getFloatValue2()));
         json.addProperty("hp", round(mc.player.getHealth()));
         json.addProperty("abs", round(mc.player.getAbsorptionAmount()));
         json.addProperty("ground", mc.player.onGround());
         json.addProperty("sprint", mc.player.isSprinting());
         json.addProperty("sneak", mc.player.isShiftKeyDown());
         json.addProperty("using", mc.player.isUsingItem());
         json.addProperty("hurt", mc.player.hurtTime);
         json.addProperty("fall", round(mc.player.fallDistance));
         json.addProperty("slot", mc.player.getInventory().getSelectedSlot());
         json.addProperty("main", method5950(mc.player.getMainHandItem().getItem()));
         json.addProperty("off", method5950(mc.player.getOffhandItem().getItem()));
         json.addProperty("crystals", this.method5946(Items.END_CRYSTAL));
         json.addProperty("obby", this.method5946(Items.OBSIDIAN));
         json.addProperty("totems", this.method5946(Items.TOTEM_OF_UNDYING));
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            builder.append(i == 0 ? "" : ",").append(stack.isEmpty() ? "-" : stack.getItem().toString().replace("minecraft:", ""));
         }

         json.addProperty("hotbar", builder.toString());
         json.addProperty("ping", this.intValue15);
         json.addProperty("hole", this.method5945(mc.player.getBoundingBox()));
         json.addProperty("preset", this.preset.get());
         this.jsonObject.add("me", json);
      }
   }

   private void method5932() {
      if (this.jsonObject != null) {
         JsonArray jsonArray = new JsonArray();

         for (CrystalAuraModule.Data5 data5 : this.items18) {
            Player playerEntity = data5.getPlayerEntity();
            CrystalAuraModule.Entry entry = this.items14.get(playerEntity.getId());
            JsonObject json = new JsonObject();
            json.addProperty("id", playerEntity.getId());
            json.addProperty("name", playerEntity.getName().getString());
            json.addProperty("pos", method5941(data5.getVec3d()));
            json.addProperty("vel", entry == null ? "0,0,0" : method5941(entry.method5971()));
            json.addProperty("dist", round(data5.getVec3d().distanceTo(mc.player.position())));
            json.addProperty("hp", round(data5.getFloatValue()));
            json.addProperty("totem", data5.isFlag());
            json.addProperty("hole", this.method5945(data5.getBox()));
            json.addProperty("ground", playerEntity.onGround());
            json.addProperty("iframes", data5.getIntValue());
            json.addProperty("lastRaw", round(data5.getFloatValue2()));
            json.addProperty("popAge", data5.getIntValue2() == (32767 << 16 | (char)-1) ? -1 : data5.getIntValue2());
            json.addProperty("resist", data5.isFlag3());
            json.addProperty("weakArmor", data5.isFlag2());
            json.addProperty("blocking", playerEntity.isBlocking());
            json.addProperty("main", method5950(playerEntity.getMainHandItem().getItem()));
            json.addProperty("off", method5950(playerEntity.getOffhandItem().getItem()));
            json.addProperty("arrival", method5942(data5.getBox()));
            json.addProperty("place", method5942(data5.getBox2()));
            json.addProperty("potential", method5942(data5.getBox3()));
            if (data5.getBox4() != null) {
               json.addProperty("rising", method5942(data5.getBox4()));
            }

            if (entry != null && entry.box != null) {
               json.addProperty("knockAge", mc.player.tickCount - entry.intValue5);
               json.addProperty("knockVy", round(entry.vec3d.y));
            }

            jsonArray.add(json);
         }

         this.jsonObject.add("targets", jsonArray);
      }
   }

   private void method5933(List<CrystalAuraModule.Data10> items, boolean flag) {
      if (this.jsonObject != null) {
         JsonArray jsonArray = new JsonArray();

         for (int i = 0; i < items.size() && i < 8; i++) {
            CrystalAuraModule.Data10 data10 = (CrystalAuraModule.Data10)items.get(i);
            JsonObject json = method5940("base", method5948(data10.getBlockPos()));
            json.addProperty("dmg", round(data10.getFloatValue()));
            if (data10.getBlockPos2() != null) {
               json.addProperty("opensAfterMining", method5948(data10.getBlockPos2()));
            }

            jsonArray.add(json);
         }

         this.jsonObject.add("dangers", jsonArray);
         this.jsonObject.addProperty("dangerUrgent", flag);
      }
   }

   private void method5934(CrystalAuraModule.Data13 data13, CrystalAuraModule.Data12 data12, float floatValue, String str) {
      if (this.jsonObject != null) {
         JsonObject json = method5940("pos", method5948(data13.pos()), "kind", data13.getType().name());
         json.addProperty("bound", round(data13.getFloatValue3()));
         json.addProperty("bonus", round(data13.getFloatValue2()));
         json.addProperty("dist", round(Math.sqrt(method5962(mc.player.getEyePosition(), new AABB(data13.pos())))));
         if (data13.getBlockPos2() != null) {
            json.addProperty("air", method5948(data13.getBlockPos2()));
         }

         if (data13.getBlockPos3() != null) {
            json.addProperty("solid", method5948(data13.getBlockPos3()));
         }

         if (data13.getIntValue() > 0) {
            json.addProperty("mineTicks", data13.getIntValue());
         }

         if (data12 != null) {
            json.addProperty("dmg", round(data12.getFloatValue()));
            json.addProperty("self", round(data12.getFloatValue2()));
            json.addProperty("value", round(floatValue));
            json.addProperty("ready", data12.isFlag());
            json.addProperty("target", method5947(data12.getPlayerEntity()));
         }

         if (str != null) {
            json.addProperty("reject", str);
            this.method5936(
               String.format(
                  Locale.ROOT,
                  "%s %s (до места %.2f б, верхняя оценка %.1f): %s",
                  data13.getType(),
                  method5948(data13.pos()),
                  json.get("dist").getAsDouble(),
                  data13.getFloatValue3(),
                  str
               ),
               data13.getFloatValue3()
            );
         }

         this.jsonArray2.add(json);
      }
   }

   private void method5935(EndCrystal endCrystalEntity, CrystalAuraModule.Data12 data12, String str) {
      if (this.jsonObject != null) {
         JsonObject json = method5940("pos", method5941(endCrystalEntity.position()));
         json.addProperty("id", endCrystalEntity.getId());
         json.addProperty("dist", round(Math.sqrt(method5962(mc.player.getEyePosition(), endCrystalEntity.getBoundingBox()))));
         json.addProperty("serverHit", this.method5835(mc.player.getEyePosition(), this.method5897(), endCrystalEntity) != null);
         json.addProperty("age", endCrystalEntity.tickCount);
         if (data12 != null) {
            json.addProperty("dmg", round(data12.getFloatValue()));
            json.addProperty("self", round(data12.getFloatValue2()));
            json.addProperty("value", round(data12.value()));
            json.addProperty("ready", data12.isFlag());
            json.addProperty("target", method5947(data12.getPlayerEntity()));
         }

         if (str != null) {
            json.addProperty("reject", str);
            this.method5936(
               String.format(
                  Locale.ROOT, "кристалл #%d %s (до него %.2f б): %s", endCrystalEntity.getId(), method5949(endCrystalEntity.position()), json.get("dist").getAsDouble(), str
               ),
               50.0F
            );
         }

         this.jsonArray.add(json);
      }
   }

   private void method5936(String str, float floatValue) {
      if (this.jsonObject != null && floatValue > this.floatValue3) {
         this.text3 = str;
         this.floatValue3 = floatValue;
      }
   }

   private void method5937(String key) {
      if (this.jsonObject != null) {
         this.items24.merge(key, 1, Integer::sum);
      }
   }

   private void method5938() {
      if (this.jsonObject != null) {
         this.jsonObject.addProperty("break", this.method5944(this.data));
         this.jsonObject.addProperty("action", this.method5944(this.data4));
         this.jsonObject.addProperty("wait", this.method5944(this.data2));
         this.jsonObject.addProperty("defense", this.method5944(this.data5));
         this.jsonObject.addProperty("danger", round(this.floatValue2));
         this.jsonObject.addProperty("breakOptions", this.items16.size());
         this.jsonObject.addProperty("actionOptions", this.items17.size());
         this.jsonObject.addProperty("spots", this.intValue23);
         this.jsonObject.addProperty("ranked", this.intValue24);
         this.jsonObject.addProperty("aimFails", this.intValue25);
         if (this.rotation != null) {
            this.jsonObject.addProperty("aimYaw", round(this.rotation.getFloatValue()));
            this.jsonObject.addProperty("aimPitch", round(this.rotation.getFloatValue2()));
         }

         if (this.rotation4 != null) {
            this.jsonObject.addProperty("preAim", true);
         }

         if (this.blockPos3 != null) {
            this.jsonObject.addProperty("digging", method5948(this.blockPos3));
            this.jsonObject.addProperty("digProgress", round(this.floatValue));
         }

         this.jsonObject.add("crystals", this.jsonArray);
         this.jsonObject.add("spotsEval", this.jsonArray2);
         JsonObject json = new JsonObject();
         this.items24.forEach(json::addProperty);
         this.jsonObject.add("filtered", json);
         if (this.data == null && (this.data4 == null || !this.data4.isFlag()) && this.text3 != null && !this.text3.equals(this.text4)) {
            this.text4 = this.text3;
            this.combatLogger.method4612("REJECT", "%s", this.text3);
         }
      }
   }

   private void method5939() {
      if (this.jsonObject != null) {
         JsonArray jsonArray = new JsonArray();
         this.combatLogger.method4614().forEach(jsonArray::add);
         this.jsonObject.add("events", jsonArray);
         this.jsonObject.addProperty("windowActions", this.intValue8);
         this.jsonObject.addProperty("unreachable", this.items13.size());
         this.jsonObject.addProperty("unreachableMax", this.items13.values().stream().mapToInt(Integer::intValue).max().orElse(-1));
         this.jsonObject.addProperty("age", mc.player.tickCount);
         this.jsonObject.addProperty("mustCross", this.box3 != null);
         this.jsonObject.addProperty("webTrap", this.text6);
         this.jsonObject.addProperty("commit", this.blockPos == null ? "-" : this.type + " " + method5948(this.blockPos));
         this.jsonObject.addProperty("weapon", this.method5784() == null ? "-" : this.method5784().name());
         this.combatLogger.method4613(this.jsonObject.toString());
         this.jsonObject = null;
      }
   }

   private static JsonObject method5940(String... strings) {
      JsonObject json = new JsonObject();

      for (byte i = 0; i + 1 < strings.length; i += 2) {
         json.addProperty(strings[i], strings[i + 1]);
      }

      return json;
   }

   private static double round(double floatValue) {
      return Math.round(floatValue * 1000.0) / 1000.0;
   }

   private static String method5941(Vec3 position) {
      return String.format(Locale.ROOT, "%.3f,%.3f,%.3f", position.x, position.y, position.z);
   }

   private static String method5942(AABB box) {
      return String.format(Locale.ROOT, "%.3f,%.3f,%.3f", (box.minX + box.maxX) * 0.5, box.minY, (box.minZ + box.maxZ) * 0.5);
   }

   private void method5943(CrystalAuraModule.Data data, String str) {
      if (str == null) {
         if (this.intValue26 >= 3 && this.method5925()) {
            this.combatLogger.method4612("STALL", "простой %d тиков закончился, причина была: %s", this.intValue26, this.text5);
         }

         this.intValue26 = 0;
         this.text5 = null;
      } else {
         this.intValue26++;
         this.text5 = str;
         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  "MISS",
                  "%s не сделали: %s (тик простоя %d) — урон %.1f по %s, до места %.2f б, цель в %.2f б",
                  this.method5944(data),
                  str,
                  this.intValue26,
                  data.getFloatValue(),
                  method5947(data.getPlayerEntity()),
                  Math.sqrt(method5962(mc.player.getEyePosition(), new AABB(data.pos()))),
                  data.getPlayerEntity() == null ? -1.0 : data.getPlayerEntity().distanceTo(mc.player)
               );
         }
      }
   }

   private String method5944(CrystalAuraModule.Data data) {
      return data == null
         ? "-"
         : String.format(
            Locale.ROOT,
            "%s %s %.1f/%.1f%s",
            data.getType(),
            method5948(data.pos()),
            data.getFloatValue(),
            data.getFloatValue2(),
            data.isFlag() ? "" : " (рано)"
         );
   }

   private boolean method5945(AABB boundingBox) {
      BlockPos containing = BlockPos.containing((boundingBox.minX + boundingBox.maxX) * 0.5, boundingBox.minY + 0.001, (boundingBox.minZ + boundingBox.maxZ) * 0.5);

      for (Direction direction : Direction.Plane.HORIZONTAL) {
         if (mc.level.getBlockState(containing.relative(direction)).getBlock().getExplosionResistance() < 600.0F) {
            return false;
         }
      }

      return true;
   }

   private int method5946(Item rawType) {
      int n = 0;

      for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
         ItemStack stack = mc.player.getInventory().getItem(i);
         if (stack.is(rawType)) {
            n += stack.getCount();
         }
      }

      return n;
   }

   private static String method5947(Entity playerEntity) {
      return playerEntity == null ? "?" : playerEntity.getName().getString() + "#" + playerEntity.getId();
   }

   private static String method5948(BlockPos pos) {
      return pos.getX() + "," + pos.getY() + "," + pos.getZ();
   }

   private static String method5949(Vec3 position) {
      return String.format(Locale.ROOT, "%.1f,%.1f,%.1f", position.x, position.y, position.z);
   }

   private static String method5950(Item rawType) {
      return rawType.toString();
   }

   private void method5951(int tickCount) {
      Integer id;
      while ((id = this.queue2.poll()) != null) {
         CrystalAuraModule.Entry entry = this.items14.get(id);
         if (entry != null) {
            entry.setIntValue3(tickCount);
         }

         if (this.method5925()) {
            Entity entity = mc.level.getEntity(id);
            if (entity == mc.player) {
               this.combatLogger.intValue7++;
               this.combatLogger.method4612("POP", "НАШ тотем сработал, тотемов осталось %d", this.method5946(Items.TOTEM_OF_UNDYING));
            } else if (entity instanceof Player playerEntity) {
               if (entry != null) {
                  this.combatLogger.intValue8++;
               }

               this.combatLogger.method4612("POP", "тотем у %s, дист %.1f", method5947(playerEntity), playerEntity.distanceTo(mc.player));
            }
         }
      }

      CrystalAuraModule.Data9 data9;
      while ((data9 = this.queue3.poll()) != null) {
         if (this.entry19.isEnabled()) {
            Vec3 vec3d = data9.getVec3d();
            double amountToAddInAllDirections = data9.getFloatValue() * 2.0;

            for (EndCrystal endCrystal : mc.level.getEntitiesOfClass(EndCrystal.class, new AABB(vec3d, vec3d).inflate(amountToAddInAllDirections), var1x -> {
               if (var1x.isAlive()) {
                  boolean var2x;
                  label20: {
                     var map = this.items6;
                     Integer n = var1x.getId();
                     if (map != null) {
                        if (map.isEmpty()) {
                           var2x = false;
                           break label20;
                        }
                     }

                     var2x = map.containsKey(n);
                  }

                  if (!var2x) {
                     return true;
                  }
               }

               return false;
            })) {
               if (endCrystal.position().distanceTo(vec3d) <= amountToAddInAllDirections - 0.5) {
                  this.items6.put(endCrystal.getId(), tickCount + this.method5909());
               }
            }
         }

         if (this.method5925()) {
            this.combatLogger
               .method4612(
                  "BOOM",
                  "взрыв %s сила %.0f, до нас %.1f б",
                  method5949(data9.getVec3d()),
                  data9.getFloatValue(),
                  data9.getVec3d().distanceTo(mc.player.position())
               );
         }

         if (data9.getFloatValue() < 5.5F && data9.getVec3d().distanceTo(mc.player.position()) <= 6.0) {
            this.intValue21 = tickCount;
         }

         if (this.data8 == null || this.data8.getIntValue() != tickCount) {
            BlockPos containing = data9.getFloatValue() < 5.5F ? BlockPos.containing(data9.getVec3d()) : null;
            this.data8 = new CrystalAuraModule.Data8(data9.getVec3d(), data9.getFloatValue(), containing, tickCount);
         }
      }

      CrystalAuraModule.Data2 data2;
      while ((data2 = this.queue4.poll()) != null) {
         if (data2.getIntValue() >= 0 && data2.getIntValue() <= 9) {
            if (BlockPos.of(data2.getLongValue()).closerThan(mc.player.blockPosition(), 4.0)) {
               this.items21.put(data2.getLongValue(), data2.getIntValue());
               if (this.items20.put(data2.getLongValue(), tickCount + 40) == null && this.method5925()) {
                  this.combatLogger
                     .method4612(
                        "ENEMY",
                        "копает блок рядом с нами %s (%s)",
                        method5948(BlockPos.of(data2.getLongValue())),
                        method5950(mc.level.getBlockState(BlockPos.of(data2.getLongValue())).getBlock().asItem())
                     );
               }
            }
         } else {
            this.items20.remove(data2.getLongValue());
            this.items21.remove(data2.getLongValue());
         }
      }
   }

   private void method5952(int tickCount) {
      var iterator = this.items.entrySet().iterator();

      while (iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         if ((Integer)entry.getValue() < tickCount) {
            this.items5.put((Long)entry.getKey(), tickCount + 4 + this.intValue15);
            iterator.remove();
         }
      }

      if (this.longValue != Long.MIN_VALUE) {
         boolean containsKey;
         label76: {
            var items = this.items;
            Long longValue = this.longValue;
            if (items != null) {
               if (items.isEmpty()) {
                  containsKey = false;
                  break label76;
               }
            }

            containsKey = items.containsKey(longValue);
         }

         if (!containsKey) {
            this.longValue = Long.MIN_VALUE;
            this.playerEntity2 = null;
         }
      }

      this.items20.values().removeIf(var1x -> var1x < tickCount);
      this.items21.keySet().retainAll(this.items20.keySet());
      this.items22.values().removeIf(var1x -> var1x < tickCount);
      this.items3.values().removeIf(var1x -> var1x < tickCount - 40);
      this.items4.values().removeIf(var1x -> var1x < tickCount - 60);
      var iterator2 = this.items2.entrySet().iterator();

      while (iterator2.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator2.next();
         if ((Integer)entry.getValue() < tickCount) {
            if (mc.level.getBlockState(BlockPos.of((Long)entry.getKey())).canBeReplaced()) {
               this.items5.put((Long)entry.getKey(), tickCount + 10 + this.intValue15);
            }

            iterator2.remove();
         }
      }

      this.items5.values().removeIf(var1x -> var1x < tickCount);
      var iterator3 = this.items6.entrySet().iterator();

      while (iterator3.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator3.next();
         if ((Integer)entry.getValue() < tickCount) {
            iterator3.remove();
            Double d = this.items7.remove(entry.getKey());
            if (d != null) {
               Entity entity = mc.level.getEntity((Integer)entry.getKey());
               if (entity instanceof EndCrystal && entity.isAlive()) {
                  if (d > 2.3 && d - 0.05 < this.doubleValue) {
                     this.doubleValue = Math.max(2.3, d - 0.05);
                     if (this.method5925()) {
                        this.combatLogger
                           .method4612("REACH", "кристалл #%d пережил удар с %.2f б — предел удара теперь %.2f б", entry.getKey(), d, this.doubleValue);
                     }
                  }
               } else if (d >= this.doubleValue - 0.2) {
                  this.doubleValue = Math.min(3.0, this.doubleValue + 0.02);
               }
            }
         }
      }

      this.items7.keySet().retainAll(this.items6.keySet());
      this.items10.removeIf(id -> mc.level.getEntity(id) == null);
      if (this.data8 != null && tickCount - this.data8.getIntValue() > 40) {
         this.data8 = null;
      }
   }

   private org.zenith.utility.misc.Data3 method5953() {
      int n;
      org.zenith.utility.misc.Data3 data3 = (n = Math.max(0, this.preset.getIndex())) < org.zenith.utility.misc.Data3.data3s.length
         ? org.zenith.utility.misc.Data3.data3s[n]
         : null;
      if (data3 != null) {
         return data3;
      } else {
         org.zenith.utility.misc.Data3.Type type = this.swap.getIndex() == 1
            ? org.zenith.utility.misc.Data3.Type.SILENT
            : (this.swap.getIndex() == 2 ? org.zenith.utility.misc.Data3.Type.OFF : org.zenith.utility.misc.Data3.Type.KEEP);
         return new org.zenith.utility.misc.Data3(
            this.mode.getIndex() == 1,
            this.placeRange.getValue3(),
            this.breakRange.getValue3(),
            Math.max(1, Math.round(this.actions.getValue3())),
            Math.round(this.placeDelay.getValue3()),
            Math.round(this.breakDelay.getValue3()),
            this.rotationSpeed.getValue3(),
            type,
            this.instantBreak.isEnabled(),
            this.instantBreak.isEnabled() && this.instantPlace.isEnabled(),
            this.varyRotation.isEnabled(),
            this.throughWalls.isEnabled(),
            false
         );
      }
   }

   private boolean method5954() {
      return this.preset.getIndex() == 0;
   }

   private void method5955() {
      this.method5886(true);
      this.playerEntity = null;
      this.items18 = List.of();
      this.items19 = List.of();
      this.blockPos2 = null;
      this.rotation2 = null;
      this.box = null;
      this.box2 = null;
      this.playerEntity3 = null;
      this.rotation5 = null;
   }

   private void reset() {
      this.method5955();
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.type2 = null;
      this.intValue = -32768 << 16 | 0;
      this.items5.clear();
      this.items6.clear();
      this.items14.clear();
      this.queue.clear();
      this.items15.clear();
      this.items16.clear();
      this.items17.clear();
      this.placementQueue.clear();
      this.class33.cancel();
      this.class35.reset();
      this.data = null;
      this.data2 = null;
      this.data4 = null;
      this.rotation = null;
      this.rotation3 = null;
      this.data8 = null;
      this.items23.clear();
      this.items7.clear();
      this.box3 = null;
      this.items13.clear();
      this.items11.clear();
      this.items10.clear();
      this.items8.clear();
      this.items9.clear();
      this.intValue2 = -32768 << 16 | 0;
      this.items12.clear();
      this.intValue27 = -1;
      this.intValue31 = -1;
      this.intValue28 = -1;
      this.intValue29 = -1;
      this.blockPos = null;
      this.type = null;
      this.blockPos3 = null;
      this.floatValue = 0.0F;
      this.intValue4 = -32768 << 16 | 0;
      this.intValue5 = -32768 << 16 | 0;
      this.intValue6 = -32768 << 16 | 0;
      this.intValue7 = -32768 << 16 | 0;
      this.intValue8 = 0;
      this.intValue9 = 0;
      this.intValue10 = 0;
      this.intValue11 = 0;
      this.intValue12 = -1;
      this.intValue13 = -1;
      this.intValue14 = -1;
      this.intValue16 = -32768 << 16 | 0;
      this.intValue17 = -32768 << 16 | 0;
      this.intValue18 = -32768 << 16 | 0;
      this.flag2 = false;
      this.flag3 = false;
      this.queue2.clear();
      this.queue3.clear();
      this.queue4.clear();
      this.items20.clear();
      this.longValue = Long.MIN_VALUE;
      this.playerEntity2 = null;
      this.intValue20 = -32768 << 16 | 0;
      this.floatValue2 = 0.0F;
      this.data5 = null;
      this.intValue22 = -32768 << 16 | 0;
      this.queue5.clear();
      this.jsonObject = null;
      this.text4 = null;
      if (mc.player != null && mc.isSameThread()) {
         this.combatLogger.method4614();
      }

      this.items21.clear();
      this.items22.clear();
      this.rotation4 = null;
      this.blockPos4 = null;
      this.type3 = null;
      this.intValue26 = 0;
      this.text5 = null;
      this.floatValue4 = -1.0F;
   }

   private static boolean method5956(BlockState blockState) {
      return blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK);
   }

   private static boolean method5957(CrystalAuraModule.Type type) {
      return type == CrystalAuraModule.Type.ANCHOR_PLACE || type == CrystalAuraModule.Type.ANCHOR_CHARGE || type == CrystalAuraModule.Type.ANCHOR_EXPLODE;
   }

   private static long method5958(BlockPos pos, int n) {
      return pos.asLong() * 4L + n;
   }

   private static Vec3 method5959(BlockPos pos) {
      return new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
   }

   private static AABB method5960(BlockPos pos) {
      return new AABB(pos.getX(), pos.getY() + 1.0, pos.getZ(), pos.getX() + 1.0, pos.getY() + 3.0, pos.getZ() + 1.0);
   }

   private static AABB method5961(BlockPos pos) {
      return new AABB(pos.getX() - 0.5, pos.getY() + 1.0, pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 3.0, pos.getZ() + 1.5);
   }

   private static double method5962(Vec3 eyePosition, AABB aabb) {
      return eyePosition.distanceToSqr(
         Mth.clamp(eyePosition.x, aabb.minX, aabb.maxX), Mth.clamp(eyePosition.y, aabb.minY, aabb.maxY), Mth.clamp(eyePosition.z, aabb.minZ, aabb.maxZ)
      );
   }

   private static Vec3 method5963(AABB move, Direction opposite, double alpha1, double d) {
      alpha1 = Mth.clamp(alpha1, 0.05, 0.95);
      d = Mth.clamp(d, 0.05, 0.95);
      double x = Mth.lerp(alpha1, move.minX, move.maxX);
      double y = Mth.lerp(d, move.minY, move.maxY);
      double z = Mth.lerp(d, move.minZ, move.maxZ);
      double z2 = Mth.lerp(alpha1, move.minZ, move.maxZ);

      return switch (opposite) {
         case UP -> new Vec3(x, move.maxY, z);
         case DOWN -> new Vec3(x, move.minY, z);
         case NORTH -> new Vec3(x, y, move.minZ);
         case SOUTH -> new Vec3(x, y, move.maxZ);
         case WEST -> new Vec3(move.minX, y, z2);
         case EAST -> new Vec3(move.maxX, y, z2);
      };
   }

   private static AABB method5964(BlockPos pos, double d) {
      double x = 0.5 * Mth.clamp(d, 0.05, 1.0);
      Vec3 atCenterOf = Vec3.atCenterOf(pos);
      return new AABB(atCenterOf.subtract(x, x, x), atCenterOf.add(x, x, x));
   }

   private static final class Data {
      private final CrystalAuraModule.Type type;
      private final EndCrystal endCrystalEntity;
      private final BlockPos blockPos;
      private final Rotation rotation;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final boolean flag;
      private final Player playerEntity;

      public CrystalAuraModule.Type type() {
         return this.type;
      }

      public EndCrystal endCrystalEntity() {
         return this.endCrystalEntity;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Rotation rotation() {
         return this.rotation;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      public Player playerEntity() {
         return this.playerEntity;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.endCrystalEntity, other.endCrystalEntity) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.playerEntity, other.playerEntity);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type, endCrystalEntity, blockPos, rotation, floatValue, floatValue2, floatValue3, flag, playerEntity);
      }

      @Override
      public String toString() {
         return "Data[" + "type=" + this.type + ", " + "endCrystalEntity=" + this.endCrystalEntity + ", " + "blockPos=" + this.blockPos + ", " + "rotation=" + this.rotation + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "flag=" + this.flag + ", " + "playerEntity=" + this.playerEntity + "]";
      }

      private Data(
         CrystalAuraModule.Type type, EndCrystal endCrystalEntity, BlockPos blockPos, Rotation rotation, float floatValue, float f, float f2, boolean flag, Player playerEntity
      ) {
         this.type = type;
         this.endCrystalEntity = endCrystalEntity;
         this.blockPos = blockPos;
         this.rotation = rotation;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.flag = flag;
         this.playerEntity = playerEntity;
      }

      public CrystalAuraModule.Type getType() {
         return this.type;
      }

      public EndCrystal getEndCrystalEntity() {
         return this.endCrystalEntity;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public Player getPlayerEntity() {
         return this.playerEntity;
      }
   }

   private static final class Data10 {
      private final BlockPos blockPos;
      private final Vec3 vec3d;
      private final BlockPos blockPos2;
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public BlockPos blockPos2() {
         return this.blockPos2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.blockPos2, other.blockPos2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, vec3d, blockPos2, floatValue, floatValue2, flag);
      }

      @Override
      public String toString() {
         return "Data10[" + "blockPos=" + this.blockPos + ", " + "vec3d=" + this.vec3d + ", " + "blockPos2=" + this.blockPos2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + "]";
      }

      private Data10(BlockPos blockPos, Vec3 vec3d, BlockPos blockPos2, float floatValue, float f, boolean flag) {
         this.blockPos = blockPos;
         this.vec3d = vec3d;
         this.blockPos2 = blockPos2;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.flag = flag;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public BlockPos getBlockPos2() {
         return this.blockPos2;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data11 {
      private final BlockPos blockPos;
      private final EndCrystal endCrystalEntity;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public EndCrystal endCrystalEntity() {
         return this.endCrystalEntity;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.endCrystalEntity, other.endCrystalEntity);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, endCrystalEntity);
      }

      @Override
      public String toString() {
         return "Data11[" + "blockPos=" + this.blockPos + ", " + "endCrystalEntity=" + this.endCrystalEntity + "]";
      }

      private Data11(BlockPos blockPos, EndCrystal endCrystalEntity) {
         this.blockPos = blockPos;
         this.endCrystalEntity = endCrystalEntity;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public EndCrystal getEndCrystalEntity() {
         return this.endCrystalEntity;
      }
   }

   private static final class Data12 {
      private final Player playerEntity;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final boolean flag;

      public Player playerEntity() {
         return this.playerEntity;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.playerEntity, other.playerEntity) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(playerEntity, floatValue, floatValue2, floatValue3, flag);
      }

      @Override
      public String toString() {
         return "Data12[" + "playerEntity=" + this.playerEntity + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "flag=" + this.flag + "]";
      }

      private Data12(Player playerEntity, float floatValue, float f, float f2, boolean flag) {
         this.playerEntity = playerEntity;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.flag = flag;
      }

      public Player getPlayerEntity() {
         return this.playerEntity;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float value() {
         return this.floatValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data13 {
      private final BlockPos blockPos;
      private final CrystalAuraModule.Type type;
      private final Vec3 vec3d;
      private final float floatValue;
      private final BlockPos blockPos2;
      private final BlockPos blockPos3;
      private final float floatValue2;
      private final float floatValue3;
      private final int intValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public CrystalAuraModule.Type type() {
         return this.type;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public BlockPos blockPos2() {
         return this.blockPos2;
      }

      public BlockPos blockPos3() {
         return this.blockPos3;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.blockPos2, other.blockPos2) && java.util.Objects.equals(this.blockPos3, other.blockPos3) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, type, vec3d, floatValue, blockPos2, blockPos3, floatValue2, floatValue3, intValue);
      }

      @Override
      public String toString() {
         return "Data13[" + "blockPos=" + this.blockPos + ", " + "type=" + this.type + ", " + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + ", " + "blockPos2=" + this.blockPos2 + ", " + "blockPos3=" + this.blockPos3 + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "intValue=" + this.intValue + "]";
      }

      private Data13(BlockPos blockPos, CrystalAuraModule.Type type, Vec3 vec3d, float floatValue, BlockPos blockPos2, BlockPos blockPos3, float f, float f2, int intValue) {
         this.blockPos = blockPos;
         this.type = type;
         this.vec3d = vec3d;
         this.floatValue = floatValue;
         this.blockPos2 = blockPos2;
         this.blockPos3 = blockPos3;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.intValue = intValue;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public CrystalAuraModule.Type getType() {
         return this.type;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public BlockPos getBlockPos2() {
         return this.blockPos2;
      }

      public BlockPos getBlockPos3() {
         return this.blockPos3;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data14 {
      private final CrystalAuraModule.Data13 data13;
      private final CrystalAuraModule.Data12 data12;
      private final float floatValue;

      public CrystalAuraModule.Data13 data13() {
         return this.data13;
      }

      public CrystalAuraModule.Data12 data12() {
         return this.data12;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.data13, other.data13) && java.util.Objects.equals(this.data12, other.data12) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data13, data12, floatValue);
      }

      @Override
      public String toString() {
         return "Data14[" + "data13=" + this.data13 + ", " + "data12=" + this.data12 + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data14(CrystalAuraModule.Data13 data13, CrystalAuraModule.Data12 data12, float floatValue) {
         this.data13 = data13;
         this.data12 = data12;
         this.floatValue = floatValue;
      }

      public CrystalAuraModule.Data13 getData13() {
         return this.data13;
      }

      public CrystalAuraModule.Data12 getData12() {
         return this.data12;
      }

      public float value() {
         return this.floatValue;
      }
   }

   private static final class Data2 {
      private final long longValue;
      private final int intValue;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "intValue=" + this.intValue + "]";
      }

      private Data2(long longValue, int intValue) {
         this.longValue = longValue;
         this.intValue = intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data3 {
      private final List<CrystalAuraModule.Data10> items;
      private final AABB box;
      private final BlockPos blockPos;
      private final Item item;
      private final boolean flag;
      private final boolean flag2;

      public List<CrystalAuraModule.Data10> items() {
         return this.items;
      }

      public AABB box() {
         return this.box;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Item item() {
         return this.item;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.box, other.box) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, box, blockPos, item, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data3[" + "items=" + this.items + ", " + "box=" + this.box + ", " + "blockPos=" + this.blockPos + ", " + "item=" + this.item + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data3(List<CrystalAuraModule.Data10> items, AABB box, BlockPos blockPos, Item item, boolean flag, boolean flag2) {
         this.items = items;
         this.box = box;
         this.blockPos = blockPos;
         this.item = item;
         this.flag = flag;
         this.flag2 = flag2;
      }

      public List<CrystalAuraModule.Data10> getItems() {
         return this.items;
      }

      public AABB getBox() {
         return this.box;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public Item getItem() {
         return this.item;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }

   private static final class Data4 {
      private final Rotation rotation;
      private final double doubleValue;
      private final BlockPos blockPos;
      private final Direction direction;

      public Rotation rotation() {
         return this.rotation;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Direction direction() {
         return this.direction;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.direction, other.direction);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation, doubleValue, blockPos, direction);
      }

      @Override
      public String toString() {
         return "Data4[" + "rotation=" + this.rotation + ", " + "doubleValue=" + this.doubleValue + ", " + "blockPos=" + this.blockPos + ", " + "direction=" + this.direction + "]";
      }

      private Data4(Rotation rotation, double doubleValue, BlockPos blockPos, Direction direction) {
         this.rotation = rotation;
         this.doubleValue = doubleValue;
         this.blockPos = blockPos;
         this.direction = direction;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public Direction getDirection() {
         return this.direction;
      }
   }

   private static final class Data5 {
      private final Player playerEntity;
      private final Vec3 vec3d;
      private final AABB box;
      private final AABB box2;
      private final AABB box3;
      private final float floatValue;
      private final boolean flag;
      private final boolean flag2;
      private final int intValue;
      private final float floatValue2;
      private final AABB box4;
      private final int intValue2;
      private final boolean flag3;
      private final AABB[] boxs;
      private final int intValue3;

      public Player playerEntity() {
         return this.playerEntity;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public AABB box() {
         return this.box;
      }

      public AABB box2() {
         return this.box2;
      }

      public AABB box3() {
         return this.box3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public AABB box4() {
         return this.box4;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public AABB[] boxs() {
         return this.boxs;
      }

      public int intValue3() {
         return this.intValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.playerEntity, other.playerEntity) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.box, other.box) && java.util.Objects.equals(this.box2, other.box2) && java.util.Objects.equals(this.box3, other.box3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.box4, other.box4) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.boxs, other.boxs) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(playerEntity, vec3d, box, box2, box3, floatValue, flag, flag2, intValue, floatValue2, box4, intValue2, flag3, boxs, intValue3);
      }

      @Override
      public String toString() {
         return "Data5[" + "playerEntity=" + this.playerEntity + ", " + "vec3d=" + this.vec3d + ", " + "box=" + this.box + ", " + "box2=" + this.box2 + ", " + "box3=" + this.box3 + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "intValue=" + this.intValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "box4=" + this.box4 + ", " + "intValue2=" + this.intValue2 + ", " + "flag3=" + this.flag3 + ", " + "boxs=" + this.boxs + ", " + "intValue3=" + this.intValue3 + "]";
      }

      private Data5(
         Player playerEntity,
         Vec3 vec3d,
         AABB box,
         AABB box2,
         AABB box3,
         float floatValue,
         boolean flag,
         boolean flag2,
         int intValue,
         float f,
         AABB box4,
         int n,
         boolean flag3,
         AABB[] boxs,
         int n2
      ) {
         this.playerEntity = playerEntity;
         this.vec3d = vec3d;
         this.box = box;
         this.box2 = box2;
         this.box3 = box3;
         this.floatValue = floatValue;
         this.flag = flag;
         this.flag2 = flag2;
         this.intValue = intValue;
         this.floatValue2 = f;
         this.box4 = box4;
         this.intValue2 = n;
         this.flag3 = flag3;
         this.boxs = boxs;
         this.intValue3 = n2;
      }

      public Player getPlayerEntity() {
         return this.playerEntity;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public AABB getBox() {
         return this.box;
      }

      public AABB getBox2() {
         return this.box2;
      }

      public AABB getBox3() {
         return this.box3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public AABB getBox4() {
         return this.box4;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public AABB[] getBoxs() {
         return this.boxs;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   private static final class Data6 {
      private final BlockPos blockPos;
      private final long longValue;
      private final boolean flag;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public long longValue() {
         return this.longValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, longValue, flag);
      }

      @Override
      public String toString() {
         return "Data6[" + "blockPos=" + this.blockPos + ", " + "longValue=" + this.longValue + ", " + "flag=" + this.flag + "]";
      }

      private Data6(BlockPos blockPos, long longValue, boolean flag) {
         this.blockPos = blockPos;
         this.longValue = longValue;
         this.flag = flag;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public long time() {
         return this.longValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data7 {
      private final long longValue;
      private final BlockState blockState;
      private final BlockState blockState2;

      public long longValue() {
         return this.longValue;
      }

      public BlockState blockState() {
         return this.blockState;
      }

      public BlockState blockState2() {
         return this.blockState2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.blockState, other.blockState) && java.util.Objects.equals(this.blockState2, other.blockState2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, blockState, blockState2);
      }

      @Override
      public String toString() {
         return "Data7[" + "longValue=" + this.longValue + ", " + "blockState=" + this.blockState + ", " + "blockState2=" + this.blockState2 + "]";
      }

      private Data7(long longValue, BlockState blockState, BlockState blockState2) {
         this.longValue = longValue;
         this.blockState = blockState;
         this.blockState2 = blockState2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public BlockState state() {
         return this.blockState;
      }

      public BlockState getBlockState2() {
         return this.blockState2;
      }
   }

   private static final class Data8 {
      private final Vec3 vec3d;
      private final float floatValue;
      private final BlockPos blockPos;
      private final int intValue;

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, floatValue, blockPos, intValue);
      }

      @Override
      public String toString() {
         return "Data8[" + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + ", " + "blockPos=" + this.blockPos + ", " + "intValue=" + this.intValue + "]";
      }

      private Data8(Vec3 vec3d, float floatValue, BlockPos blockPos, int intValue) {
         this.vec3d = vec3d;
         this.floatValue = floatValue;
         this.blockPos = blockPos;
         this.intValue = intValue;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data9 {
      private final Vec3 vec3d;
      private final float floatValue;

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, floatValue);
      }

      @Override
      public String toString() {
         return "Data9[" + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data9(Vec3 vec3d, float floatValue) {
         this.vec3d = vec3d;
         this.floatValue = floatValue;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }

   private static final class Entry {
      private final ArrayDeque<Vec3> arrayDeque = new ArrayDeque<>();
      private int intValue;
      private int intValue2 = Integer.MIN_VALUE;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      public float floatValue4;
      private int intValue3 = Integer.MIN_VALUE;
      private int intValue4 = Integer.MIN_VALUE;
      private float floatValue5;
      private float floatValue6;
      private int intValue5 = Integer.MIN_VALUE;
      private AABB box;
      private Vec3 vec3d;
      private int intValue6;
      private double doubleValue;
      private int intValue7 = Integer.MIN_VALUE;
      private float floatValue7;
      private boolean flag;
      private int intValue8 = Integer.MIN_VALUE;
      private float floatValue8;
      private boolean flag2;
      private boolean flag3;

      private void method5965(int tickCount, AABB box, Vec3 vec3d, int n) {
         this.intValue5 = tickCount;
         this.box = box;
         this.doubleValue = box.minY;
         this.vec3d = vec3d;
         this.intValue6 = n;
      }

      private boolean method5966(int tickCount) {
         if (this.box == null) {
            return false;
         } else if (tickCount - this.intValue5 > 18 + this.intValue6) {
            this.box = null;
            return false;
         } else {
            return true;
         }
      }

      private void setIntValue3(int tickCount) {
         this.intValue3 = tickCount;
      }

      private void method5967(int tickCount, float f) {
         this.intValue7 = tickCount;
         this.floatValue7 = f;
      }

      private void method5968(int n, float f) {
         if (!(f <= 0.0F)) {
            if (this.method5969(n)) {
               this.floatValue8 = Math.max(this.floatValue8, f);
            } else {
               this.intValue8 = n;
               this.floatValue8 = f;
               this.flag2 = true;
               this.flag3 = false;
            }
         }
      }

      private boolean method5969(int n) {
         return this.intValue8 != Integer.MIN_VALUE && n - this.intValue8 >= 0 && n - this.intValue8 < 10;
      }

      private void method5970(Player playerEntity, Vec3 base, int tickCount, CrystalAuraModule.Data8 data8, CombatDamageCalculator class2, Boolean flag, int n) {
         this.arrayDeque.addLast(base);

         while (this.arrayDeque.size() > 3) {
            this.arrayDeque.removeFirst();
         }

         float absorptionAmount = playerEntity.getAbsorptionAmount();
         if (absorptionAmount >= 15.9F && this.floatValue6 < 15.9F) {
            this.intValue4 = tickCount + 6000;
         }

         this.floatValue6 = absorptionAmount;
         float f = playerEntity.getHealth() + playerEntity.getAbsorptionAmount();
         if (playerEntity.getInvulnerableTime() > this.intValue) {
            this.intValue2 = tickCount;
            this.floatValue = this.floatValue2;
            this.floatValue3 = 0.0F;
            this.flag = flag == Boolean.TRUE || flag == null && data8 != null && tickCount - data8.getIntValue() <= 20;
            int n2 = tickCount - n;
            if (this.intValue8 == Integer.MIN_VALUE || Math.abs(n2 - this.intValue8) > 2) {
               this.intValue8 = n2;
               this.floatValue8 = 0.0F;
               this.flag2 = false;
            }

            this.flag3 = true;
            if (flag != Boolean.TRUE && this.intValue7 != Integer.MIN_VALUE && tickCount - this.intValue7 <= 12) {
               this.floatValue3 = this.floatValue7;
            } else if (flag != Boolean.FALSE && data8 != null && tickCount - data8.getIntValue() <= 20) {
               this.floatValue3 = class2.method5454(data8.getVec3d(), data8.getFloatValue(), playerEntity, playerEntity.getBoundingBox(), data8.getBlockPos(), null);
            }

            this.floatValue4 = data8 != null && tickCount - data8.getIntValue() <= 2 ? this.floatValue3 : 0.0F;
         }

         if (this.intValue2 != Integer.MIN_VALUE && tickCount - this.intValue2 <= 3) {
            float f2 = this.floatValue - f;
            if (this.flag) {
               if (f2 > 0.05F && this.intValue3 < this.intValue2 && playerEntity.getHealth() > 0.0F) {
                  this.floatValue3 = Math.max(this.floatValue4, class2.method5458(playerEntity, f2));
               }
            } else {
               this.floatValue3 = Math.max(this.floatValue3, f2);
            }

            if (!this.flag2) {
               this.floatValue8 = this.floatValue3;
            }
         }

         if (this.flag2 && !this.flag3 && this.intValue8 != Integer.MIN_VALUE && tickCount - n - this.intValue8 > 3) {
            this.intValue8 = Integer.MIN_VALUE;
            this.flag2 = false;
         }

         this.intValue = playerEntity.getInvulnerableTime();
         this.floatValue5 = this.floatValue2 > 0.0F ? Math.max(0.0F, this.floatValue2 - f) : 0.0F;
         this.floatValue2 = f;
      }

      private Vec3 method5971() {
         if (this.arrayDeque.size() < 2) {
            return Vec3.ZERO;
         } else {
            Vec3 vec = this.arrayDeque.getFirst();
            Vec3 vec2 = this.arrayDeque.getLast();
            return vec2.subtract(vec).scale(1.0 / (this.arrayDeque.size() - 1));
         }
      }
   }

   private static enum Type {
      BREAK,
      CRYSTAL,
      OBSIDIAN,
      DIG,
      SHIELD,
      ANCHOR_PLACE,
      ANCHOR_CHARGE,
      ANCHOR_EXPLODE;
   }

   private static enum Type2 {
      CRYSTALS,
      ANCHOR;
   }
}
