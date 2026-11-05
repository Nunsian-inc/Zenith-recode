package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.JumpEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.config.PetEntityState;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.network.Cloud;

public class PetManager implements MinecraftAccess {
   public static final float CONST_floatValue = 0.4F;
   private static final int CONST_intValue = 8;
   private final MultiSelectSetting render = new MultiSelectSetting("cosmetics.pet.render");
   private final SliderSetting scale;
   private Path path;
   private PetEntityState petEntityState;
   private final Map<UUID, PetEntityState> items;
   private static final Set<UUID> items2 = ConcurrentHashMap.newKeySet();
   private static final Set<Integer> items3 = ConcurrentHashMap.newKeySet();

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(field -> {
         try {
            field.setAccessible(true);
            return field.get(this);
         } catch (Exception e) {
            return null;
         }
      }).filter(obj -> obj instanceof Setting).map(obj -> (Setting)obj).collect(Collectors.toList());
   }

   public boolean method4551() {
      return !this.render.method5222("cosmetics.pet.render.self").isEnabled() && !this.render.method5222("cosmetics.pet.render.friends").isEnabled();
   }

   public boolean method4552() {
      return this.render.method5222("cosmetics.pet.render.self").isEnabled();
   }

   public boolean method4553() {
      return this.render.method5222("cosmetics.pet.render.friends").isEnabled();
   }

   public PetManager() {
      new MultiSelectSetting.Entry(this.render, "cosmetics.pet.render.self", true);
      new MultiSelectSetting.Entry(this.render, "cosmetics.pet.render.friends", true);
      this.scale = new SliderSetting("cosmetics.pet.scale", 0.4F, 0.1F, 2.0F, 0.05F, (f, f2) -> this.method4573());
      this.items = new ConcurrentHashMap<>();
      EventManager.register(this);
   }

   public void method4554(Path path) {
      this.path = path;
      if (path != null && this.method4566(path) && mc.player != null && mc.level != null) {
         this.method4570();
         this.petEntityState.method2307(path);
      } else if (path == null && this.petEntityState != null) {
         this.method4571();
      }
   }

   public String method4555() {
      return this.method4566(this.path) ? org.zenith.utility.config.AvatarUtil.method7257(this.path) : "";
   }

   public static boolean method4556(UUID uuid) {
      return uuid != null && items2.contains(uuid);
   }

   public static boolean method4557(int id) {
      return items3.contains(id);
   }

   public static void method4558(PetEntityState petEntityState) {
      if (petEntityState != null && petEntityState.getImplOtherClientPlayerEntity() != null) {
         items2.add(petEntityState.getUUID());
         items3.add(petEntityState.getImplOtherClientPlayerEntity().getId());
      }
   }

   public static boolean method4559() {
      return !items3.isEmpty();
   }

   public static void method4560(PetEntityState petEntityState) {
      if (petEntityState != null) {
         items2.remove(petEntityState.getUUID());
         if (petEntityState.getImplOtherClientPlayerEntity() != null) {
            items3.remove(petEntityState.getImplOtherClientPlayerEntity().getId());
         }
      }
   }

   @EventTarget
   public void method4561(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         this.method4565();
         this.method4567();
      } else {
         this.method4574();
      }
   }

   @EventTarget
   public void method4562(TickEvent event) {
      if (mc.level == null && (this.petEntityState != null || !this.items.isEmpty())) {
         try {
            this.method4574();
         } catch (Throwable e) {
            this.petEntityState = null;
            this.items.clear();
            items2.clear();
            items3.clear();
         }
      }
   }

   @EventTarget
   public void method4563(JumpEvent event) {
      if (this.petEntityState != null && this.petEntityState.method2304()) {
         this.petEntityState.method2310();
      }
   }

   @EventTarget
   public void method4564(WorldRenderEvent event) {
      if (this.petEntityState != null && this.petEntityState.method2304()) {
         Vec3 vec = this.petEntityState.method2313(event.getFloatValue());
         if (vec != null && this.petEntityState.getImplOtherClientPlayerEntity() != null) {
            this.petEntityState.getImplOtherClientPlayerEntity().syncPacketPositionCodec(vec.x, vec.y, vec.z);
         }
      }

      for (PetEntityState petEntityState : this.items.values()) {
         if (petEntityState.method2304()) {
            Vec3 vec = petEntityState.method2313(event.getFloatValue());
            if (vec != null && petEntityState.getImplOtherClientPlayerEntity() != null) {
               petEntityState.getImplOtherClientPlayerEntity().syncPacketPositionCodec(vec.x, vec.y, vec.z);
            }
         }
      }
   }

   private void method4565() {
      if (this.path != null && this.method4552() && this.method4566(this.path)) {
         this.method4570();
         if (!this.petEntityState.method2304()) {
            Vec3 vec3d = mc.player.position().add(2.0, 0.0, 0.0);
            this.petEntityState.method2302(mc.level, vec3d);
            this.petEntityState.method2307(this.path);
            method4558(this.petEntityState);
         }

         this.petEntityState.setFlag(this.method4569());
         LivingEntity livingEntity = null;

         try {
            AuraModule auraModule = AuraModule.INSTANCE;
            livingEntity = auraModule.method6358() == null ? AimAssistModule.INSTANCE.method5156() : auraModule.method6358();
         } catch (Exception e) {
         }

         this.petEntityState.method2308(mc.player.position(), mc.player.yBodyRot, mc.player.isShiftKeyDown(), livingEntity);
         this.petEntityState.method2312(mc.player);
         if (this.petEntityState.getImplOtherClientPlayerEntity() != null) {
            LivingEntity.SwingState swingState = this.petEntityState.getImplOtherClientPlayerEntity().swingState;
            LivingEntity.SwingState swingState2 = mc.player.swingState;
            swingState.currentSwing = swingState2.currentSwing;
            swingState.ticks = swingState2.ticks;
            swingState.oldAnimation = swingState2.oldAnimation;
            swingState.animation = swingState2.animation;
            this.petEntityState.getImplOtherClientPlayerEntity().setShiftKeyDown(mc.player.isShiftKeyDown());
            this.petEntityState.getImplOtherClientPlayerEntity().setPose(mc.player.isShiftKeyDown() ? Pose.CROUCHING : Pose.STANDING);
         }
      } else {
         if (this.petEntityState != null) {
            this.method4571();
         }
      }
   }

   private boolean method4566(Path path) {
      return path != null && !org.zenith.utility.config.AvatarUtil.method7257(path).isBlank();
   }

   private void method4567() {
      if (mc.level != null && mc.player != null) {
         if (!this.method4553()) {
            for (PetEntityState petEntityState : this.items.values()) {
               method4560(petEntityState);
               petEntityState.method2303();
            }

            this.items.clear();
         } else {
            HashSet hashSet = new HashSet();
            int n = 0;
            Cloud cloud = Zenith.getInstance().getCloud();
            String str = cloud != null && cloud.method7862() ? cloud.method7911() : "";
            if (!str.isEmpty()) {
               for (PartyPlayerInfo partyPlayerInfo : cloud.state().method8038()) {
                  if (n >= 8) {
                     break;
                  }

                  if (!cloud.prefs().method3316(partyPlayerInfo.username()) && str.equals(partyPlayerInfo.server())) {
                     ShareCodec.Data2 data2 = ShareCodec.method6724(partyPlayerInfo.uid(), partyPlayerInfo.getItems());
                     String text = data2.getText3();
                     if (!text.isBlank()) {
                        Player player = this.method4568(partyPlayerInfo.getText2());
                        if (player != null) {
                           UUID uuid = player.getUUID();
                           hashSet.add(uuid);
                           PetEntityState petEntityState = this.items.computeIfAbsent(uuid, PetEntityState::new);
                           if (!petEntityState.method2304()) {
                              petEntityState.method2302(mc.level, player.position().add(2.0, 0.0, 0.0));
                              method4558(petEntityState);
                           }

                           petEntityState.method2306(text);
                           petEntityState.method2305(data2.getFloatValue());
                           petEntityState.setFlag(player.getAbilities().flying || player.isFallFlying());
                           petEntityState.method2308(player.position(), player.yBodyRot, player.isShiftKeyDown(), null);
                           petEntityState.method2312(player);
                           n++;
                        }
                     }
                  }
               }
            }

            var iterator = this.items.entrySet().iterator();

            while (iterator.hasNext()) {
               Entry entry = (Entry)iterator.next();
               if (!hashSet.contains(entry.getKey())) {
                  method4560((PetEntityState)entry.getValue());
                  ((PetEntityState)entry.getValue()).method2303();
                  iterator.remove();
               }
            }
         }
      }
   }

   private Player method4568(String str) {
      if (str != null && !str.isBlank()) {
         for (AbstractClientPlayer player : mc.level.players()) {
            if (player != mc.player && str.equalsIgnoreCase(player.getGameProfile().name())) {
               return player;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean method4569() {
      return mc.player == null ? false : mc.player.getAbilities().flying || mc.player.isFallFlying();
   }

   private void method4570() {
      if (this.petEntityState == null) {
         this.petEntityState = new PetEntityState(mc.player.getUUID());
      }
   }

   private void method4571() {
      if (this.petEntityState != null) {
         method4560(this.petEntityState);
         this.petEntityState.method2303();
         this.petEntityState = null;
      }
   }

   public float method4572() {
      return this.scale.getValue3();
   }

   private void method4573() {
      if (this.petEntityState != null) {
         this.petEntityState.method2305(this.method4572());
      }
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      json.addProperty("name", org.zenith.utility.config.AvatarUtil.method7257(this.path));
      JsonObject jsonObject = new JsonObject();

      for (Setting setting : this.getSettings()) {
         setting.safe(jsonObject);
      }

      json.add("settings", jsonObject);
      return json;
   }

   public void load(JsonObject jsonObject) {
      if (jsonObject != null) {
         if (jsonObject.has("settings") && jsonObject.get("settings").isJsonObject()) {
            JsonObject json = jsonObject.getAsJsonObject("settings");

            for (Setting setting : this.getSettings()) {
               if (json.has(setting.getKey())) {
                  setting.load(json);
               }
            }
         }

         if (jsonObject.has("name")) {
            String string = jsonObject.get("name").isJsonNull() ? "" : jsonObject.get("name").getAsString();
            this.method4554(string != null && !string.isBlank() ? org.zenith.utility.config.AvatarUtil.method7258(string) : null);
         }
      }
   }

   public void method4574() {
      this.method4571();

      for (PetEntityState petEntityState : this.items.values()) {
         method4560(petEntityState);
         petEntityState.method2303();
      }

      this.items.clear();
   }

   @Generated
   public MultiSelectSetting getRender() {
      return this.render;
   }

   @Generated
   public SliderSetting getScale() {
      return this.scale;
   }

   @Generated
   public Path getPath() {
      return this.path;
   }
}
