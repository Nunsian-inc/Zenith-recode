package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.PacketUtils;

@ModuleInfo(name = "AntiVoid", description = "module.antiVoid.description", category = ModuleCategory.MOVEMENT)
public final class AntiVoidModule extends Module {
   public static final AntiVoidModule INSTANCE = new AntiVoidModule();
   private final ModeSetting mode = new ModeSetting("module.antiVoid.mode", "module.antiVoid.mode.desc",
      "module.antiVoid.mode.back", "module.antiVoid.mode.jump", "module.antiVoid.mode.freeze");
   private final SliderSetting depth = new SliderSetting("module.antiVoid.depth", 4, 1, 32, 1,
      "module.antiVoid.depth.desc", "b");
   private final SliderSetting distance = new SliderSetting("module.antiVoid.distance", 64, 4, 128, 1,
      "module.antiVoid.distance.desc", "b", () -> mode.isSelected(0));
   private final SliderSetting jumpOffset = new SliderSetting("module.antiVoid.jumpOffset", 8, 1, 32, 1,
      "module.antiVoid.jumpOffset.desc", "b", () -> mode.isSelected(1));
   private final BooleanSetting notify = new BooleanSetting("module.antiVoid.notify", "module.antiVoid.notify.desc", true);
   private final Deque<SafePosition> history = new ArrayDeque<>();
   private ClientLevel level;
   private LocalPlayer player;
   private boolean triggered;
   private boolean frozen;
   private int cooldown;

   private AntiVoidModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (level != mc.level || player != mc.player) {
         reset();
         level = mc.level;
         player = mc.player;
      }
      if (!canProtect()) {
         reset();
         return;
      }
      if (cooldown > 0) cooldown--;
      history.removeIf(position -> player.tickCount - position.tick() > 100);
      boolean grounded = (player.onGround() || frozen) && isSafe(player.position());
      if (grounded || player.isInWater() || player.isInLava() || player.onClimbable()) {
         triggered = false;
         frozen = false;
         if (grounded) {
            if (history.size() >= 100) history.removeFirst();
            history.addLast(new SafePosition(player.position(), player.tickCount));
         }
         return;
      }
      if (frozen) {
         stopFalling();
         return;
      }
      if (triggered || cooldown > 0 || player.getDeltaMovement().y >= 0.0
         || player.fallDistance < depth.getValue3() && player.getY() > level.getMinY() + depth.getValue3()
         || hasFloor()) {
         return;
      }

      triggered = true;
      cooldown = 20;
      Vec3 destination = null;
      if (mode.isSelected(0)) {
         var positions = history.descendingIterator();
         while (positions.hasNext()) {
            Vec3 candidate = positions.next().position();
            if (candidate.distanceToSqr(player.position()) <= distance.getValue3() * distance.getValue3() && isSafe(candidate)) {
               destination = candidate;
               break;
            }
         }
      } else if (mode.isSelected(1)) {
         Vec3 candidate = player.position().add(0.0, jumpOffset.getValue3(), 0.0);
         AABB box = player.getBoundingBox().move(candidate.subtract(player.position()));
         if (loaded(box) && level.getWorldBorder().isWithinBounds(box)
            && !level.containsAnyLiquid(box) && level.noCollision(player, box)) {
            destination = candidate;
         }
      }
      // Без подходящей точки не отправляем игрока в блоки: остаётся ручное спасение из заморозки.
      frozen = destination == null;
      stopFalling();
      if (destination != null) {
         player.setPos(destination);
         PacketUtils.method3150(new ServerboundMovePlayerPacket.Pos(destination, false, player.horizontalCollision));
      }
      if (notify.isEnabled()) {
         String key = frozen && !mode.isSelected(2) ? "module.antiVoid.noSafePosition" : "module.antiVoid.rescued";
         zenith.getHudHolder().method6972(getCategory().getIcon(), Component.literal(zenith.getLocalization().translate(key)));
      }
   }

   private boolean canProtect() {
      return mc.player != null && mc.level != null && mc.gameMode != null && mc.getConnection() != null
         && mc.gui.screen() == null && mc.player.isAlive() && !mc.player.isSpectator() && !mc.player.isCreative()
         && !mc.player.isPassenger() && !mc.player.isFallFlying() && !mc.player.getAbilities().flying;
   }

   private void stopFalling() {
      player.setDeltaMovement(Vec3.ZERO);
      player.resetFallDistance();
   }

   @EventTarget
   public void onMove(PlayerMoveEvent event) {
      if (frozen && level == mc.level && player == mc.player && canProtect()) {
         stopFalling();
         event.setVec3d(Vec3.ZERO);
      }
   }

   private boolean loaded(AABB box) {
      for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX - 1.0E-7); x++) {
         for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ - 1.0E-7); z++) {
            if (!level.hasChunk(x >> 4, z >> 4)) return false;
         }
      }
      return true;
   }

   private boolean isSafe(Vec3 position) {
      AABB box = player.getBoundingBox().move(position.subtract(player.position()));
      return position.y >= level.getMinY() && loaded(box) && level.getWorldBorder().isWithinBounds(box)
         && !level.containsAnyLiquid(box) && level.noCollision(player, box)
         // Контекст без сущности исключает искусственный пол Jesus из истории безопасных точек.
         && level.getBlockCollisions(null, box.move(0.0, -0.05, 0.0)).iterator().hasNext();
   }

   private boolean hasFloor() {
      AABB box = player.getBoundingBox();
      // Незагруженный чанк — неизвестность, а не пустота. Проверяем всю площадь ног до дна измерения.
      if (!loaded(box)) return true;
      if (box.minY <= level.getMinY()) return false;
      var shaft = Shapes.create(new AABB(box.minX, level.getMinY(), box.minZ, box.maxX, box.minY, box.maxZ));
      BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
      for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX - 1.0E-7); x++) {
         for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ - 1.0E-7); z++) {
            for (int y = Math.min(Mth.floor(box.minY - 1.0E-7), level.getMaxY()); y >= level.getMinY(); y--) {
               cursor.set(x, y, z);
               var state = level.getBlockState(cursor);
               if (!state.getFluidState().isEmpty()
                  || Shapes.joinIsNotEmpty(state.getCollisionShape(level, cursor).move(cursor), shaft, BooleanOp.AND)) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   @EventTarget
   public void onScreen(SetScreenEvent event) {
      if (event.getScreen() != null) reset();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && (event.getPacket() instanceof ClientboundLoginPacket
         || event.getPacket() instanceof ClientboundRespawnPacket || event.getPacket() instanceof ClientboundDisconnectPacket)) {
         mc.execute(this::reset);
      }
   }

   private void reset() {
      history.clear();
      level = null;
      player = null;
      triggered = false;
      frozen = false;
      cooldown = 0;
   }

   @Override
   public void onEnable() {
      reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      reset();
      super.onDisable();
   }

   private record SafePosition(Vec3 position, int tick) {
   }
}
