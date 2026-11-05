package org.zenith.base.figura.lua.api.entity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.PlayerTeam;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.ReadOnlyLuaTable;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.mixin.HungerManagerAccessor;

@LuaWhitelist
@LuaTypeDoc(name = "PlayerAPI", value = "player")
public class PlayerAPI extends LivingEntityAPI<Player> {
   private PlayerInfo playerInfo;

   public PlayerAPI(Player entity) {
      super(entity);
   }

   private boolean checkPlayerInfo() {
      if (this.playerInfo != null) {
         return true;
      }

      if (Minecraft.getInstance().getConnection() == null) {
         return false;
      }

      this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.entityUUID);
      return this.playerInfo != null;
   }

   @LuaWhitelist
   public int getFood() {
      this.checkEntity();
      return this.entity.getFoodData().getFoodLevel();
   }

   @LuaWhitelist
   public float getSaturation() {
      this.checkEntity();
      return this.entity.getFoodData().getSaturationLevel();
   }

   @LuaWhitelist
   public float getExhaustion() {
      this.checkEntity();
      return ((HungerManagerAccessor)this.entity.getFoodData()).figura$getExhaustion();
   }

   @LuaWhitelist
   public float getExperienceProgress() {
      this.checkEntity();
      return this.entity.experienceProgress;
   }

   @LuaWhitelist
   public int getExperienceLevel() {
      this.checkEntity();
      return this.entity.experienceLevel;
   }

   @LuaWhitelist
   public String getModelType() {
      this.checkEntity();
      return this.entity instanceof AbstractClientPlayer clientPlayer ? clientPlayer.getSkin().model().getSerializedName() : "wide";
   }

   @LuaWhitelist
   public String getGamemode() {
      this.checkEntity();
      if (Minecraft.getInstance().getConnection() == null) {
         return null;
      }

      PlayerInfo entry = Minecraft.getInstance().getConnection().getPlayerInfo(this.entityUUID);
      return entry != null && entry.getGameMode() != null ? entry.getGameMode().name() : null;
   }

   @LuaWhitelist
   public boolean hasCape() {
      this.checkEntity();
      return this.entity instanceof AbstractClientPlayer clientPlayer
         ? clientPlayer.getSkin().cape() != null
         : this.checkPlayerInfo() && this.playerInfo.getSkin().cape() != null;
   }

   @LuaWhitelist
   public boolean hasSkin() {
      this.checkEntity();
      return this.entity instanceof AbstractClientPlayer clientPlayer
         ? clientPlayer.getSkin().body() != null
         : !this.checkPlayerInfo() || this.playerInfo.getSkin().body() != null;
   }

   @LuaWhitelist
   public boolean isSkinLayerVisible(@LuaNotNil String part) {
      this.checkEntity();

      try {
         if (part.equalsIgnoreCase("left_pants") || part.equalsIgnoreCase("right_pants")) {
            part = part + "_leg";
         }

         return this.entity.isModelPartShown(PlayerModelPart.valueOf(part.toUpperCase(Locale.US)));
      } catch (Exception e) {
         throw new LuaError("Invalid player model part: " + part);
      }
   }

   @LuaWhitelist
   public boolean isFishing() {
      this.checkEntity();
      return this.entity.fishing != null;
   }

   @LuaWhitelist
   public float getChargedAttackDelay() {
      this.checkEntity();
      return this.entity.getCurrentItemAttackStrengthDelay();
   }

   @LuaWhitelist
   public float getCooldownPercent(@LuaNotNil ItemStackAPI stack, Float delta) {
      this.checkEntity();
      return this.entity.getCooldowns().getCooldownPercent(stack.itemStack, delta == null ? 0.0F : delta);
   }

   @LuaWhitelist
   public LuaTable getShoulderEntity(boolean right) {
      this.checkEntity();
      LuaTable table = new LuaTable();
      (right ? this.entity.getShoulderParrotRight() : this.entity.getShoulderParrotLeft()).ifPresent(variant -> {
         table.set("id", LuaValue.valueOf("minecraft:parrot"));
         table.set("Variant", LuaValue.valueOf(variant.getId()));
      });
      return new ReadOnlyLuaTable(table);
   }

   @LuaWhitelist
   public Map<String, Object> getTeamInfo() {
      this.checkEntity();
      PlayerTeam team = this.entity.getTeam();
      if (team == null && this.checkPlayerInfo()) {
         team = this.playerInfo.getTeam();
      }

      if (team == null) {
         return null;
      }

      Map<String, Object> map = new HashMap<>();
      map.put("name", team.getName());
      map.put("display_name", team.getDisplayName().getString());
      map.put("color", team.getColor().map(net.minecraft.world.scores.TeamColor::getSerializedName).orElse("reset"));
      map.put("prefix", team.getPlayerPrefix().getString());
      map.put("suffix", team.getPlayerSuffix().getString());
      map.put("friendly_fire", team.isAllowFriendlyFire());
      map.put("see_friendly_invisibles", team.canSeeFriendlyInvisibles());
      map.put("nametag_visibility", team.getNameTagVisibility().name());
      map.put("death_message_visibility", team.getDeathMessageVisibility().name());
      map.put("collision_rule", team.getCollisionRule().name());
      return map;
   }

   @Override
   public String toString() {
      return this.getName() + " (PlayerAPI)";
   }
}
