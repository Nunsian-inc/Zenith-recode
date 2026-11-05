package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.render.TotemParticlesModule;
import org.zenith.utility.game.Npc;

public class NpcCommand extends CommandAbstract {
   private static final double SPAWN_DISTANCE = 3.0;
   private Npc npc;
   private Vec3 anchor;
   private int nextEntityId = Integer.MAX_VALUE;

   public NpcCommand() {
      super("npc");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("spawn").then(literal("naked").executes(var1x -> {
         this.spawn(false);
         return 1;
      }))).then(literal("wear").executes(var1x -> {
         this.spawn(true);
         return 1;
      }))).executes(var1x -> {
         this.spawn(true);
         return 1;
      }));
      literalArgumentBuilder.then(literal("kill").executes(var1x -> {
         this.kill();
         return 1;
      }));
      literalArgumentBuilder.then(literal("totem").executes(var1x -> {
         this.totem();
         return 1;
      }));
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2019("Использование: .npc spawn [wear|naked], .npc totem и .npc kill");
         return 1;
      });
   }

   private void totem() {
      if (mc.level == null || mc.player == null) {
         ChatLogger.method2019("Тотем можно разыграть только в мире");
      } else if (!TotemParticlesModule.INSTANCE.isEnabled()) {
         ChatLogger.method2019("Раздел Totem выключен: Particles -> Тотем");
      } else {
         Object npc = this.npc != null ? this.npc : mc.player;
         TotemParticlesModule.INSTANCE.method4697((Entity)npc);
         ChatLogger.method2019(this.npc != null ? "Тотем разыгран на манекене" : "Тотем разыгран на тебе");
      }
   }

   private void spawn(boolean flag) {
      if (mc.level != null && mc.player != null) {
         if (this.npc != null) {
            this.remove(RemovalReason.DISCARDED);
         }

         float yRot = mc.player.getYRot();
         Vec3 anchor = mc.player.position().add(Vec3.directionFromRotation(0.0F, yRot).scale(3.0));
         float yBodyRot = yRot + 180.0F;
         this.npc = new Npc(mc.level, new GameProfile(UUID.randomUUID(), "NPC"));
         this.npc.setId(this.nextEntityId());
         this.npc.snapTo(anchor, yBodyRot, 0.0F);
         this.npc.setYHeadRot(yBodyRot);
         this.npc.yBodyRot = yBodyRot;
         this.npc.yBodyRotO = yBodyRot;
         this.npc.yHeadRotO = yBodyRot;
         this.npc.setHealth(this.npc.getMaxHealth());
         if (flag) {
            equipNetherite(this.npc);
         }

         mc.level.addEntity(this.npc);
         this.anchor = anchor;
         ChatLogger.method2019("Манекен поставлен, убрать: .npc kill");
      } else {
         ChatLogger.method2019("Манекен можно поставить только в мире");
      }
   }

   private static void equipNetherite(Npc npc) {
      npc.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
      npc.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
      npc.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
      npc.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
   }

   private void kill() {
      if (this.npc == null) {
         ChatLogger.method2019("Манекена нет");
      } else {
         this.remove(RemovalReason.KILLED);
         ChatLogger.method2019("Манекен убран");
      }
   }

   private void remove(RemovalReason reason) {
      if (this.npc != null) {
         if (mc.level != null && mc.level.getEntity(this.npc.getId()) == this.npc) {
            mc.level.removeEntity(this.npc.getId(), reason);
         } else {
            this.npc.setRemoved(reason);
            this.npc.onClientRemoval();
         }

         this.npc = null;
         this.anchor = null;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.npc != null) {
         if (mc.level != null && mc.level.getEntity(this.npc.getId()) == this.npc) {
            this.npc.setHealth(this.npc.getMaxHealth());
            this.npc.deathTime = 0;
            this.npc.hurtTime = Math.min(this.npc.hurtTime, 10);
            this.npc.fallDistance = 0.0;
            this.npc.setDeltaMovement(Vec3.ZERO);
            this.npc.needsSync = false;
            if (this.anchor != null) {
               this.npc.setPos(this.anchor);
               this.npc.syncPacketPositionCodec(this.anchor.x, this.anchor.y, this.anchor.z);
            }
         } else {
            this.npc = null;
            this.anchor = null;
         }
      }
   }

   private int nextEntityId() {
      for (int i = 0; i < 1000000; i++) {
         if (this.nextEntityId <= 2146483647) {
            this.nextEntityId = Integer.MAX_VALUE;
         }

         int id = --this.nextEntityId;
         if (mc.level == null || mc.level.getEntity(id) == null) {
            return id;
         }
      }

      return Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 1000000);
   }
}
