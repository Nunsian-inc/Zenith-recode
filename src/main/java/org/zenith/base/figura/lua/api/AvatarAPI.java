package org.zenith.base.figura.lua.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.animation.Animation;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.NbtToLua;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.utils.ColorUtils;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "AvatarAPI", value = "avatar")
public final class AvatarAPI {
   private static final int UNLIMITED = 999999;
   private final Avatar avatar;
   public final LuaTable storedStuff = new LuaTable();

   public AvatarAPI(Avatar avatar) {
      this.avatar = avatar;
   }

   @LuaWhitelist
   public LuaTable getNBT() {
      return (LuaTable)NbtToLua.convert(this.avatar.nbt);
   }

   @LuaWhitelist
   public String getUUID() {
      return this.avatar.owner.toString();
   }

   @LuaWhitelist
   public int getCurrentInstructions() {
      return this.avatar.luaRuntime == null ? 0 : this.avatar.luaRuntime.getInstructions();
   }

   @LuaWhitelist
   public String getColor() {
      return this.avatar.color;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "color"),
            @LuaMethodOverload(argumentTypes = FiguraVec3.class, argumentNames = "color"),
            @LuaMethodOverload(argumentTypes = {Double.class, Double.class, Double.class}, argumentNames = {"r", "g", "b"})
      },
      aliases = "color",
      value = "avatar.set_color"
   )
   public AvatarAPI setColor(Object r, Double g, Double b) {
      if (r instanceof String s) {
         this.avatar.color = s;
      } else {
         FiguraVec3 vec = LuaUtils.parseOneArgVec("setColor", r, g, b, 1.0);
         this.avatar.color = ColorUtils.rgbToHex(vec);
      }

      return this;
   }

   @LuaWhitelist
   public AvatarAPI color(Object r, Double g, Double b) {
      return this.setColor(r, g, b);
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_current_animations")
   public List<String> getCurrentAnimations() {
      List<String> list = new ArrayList<>();

      for (Animation animation : this.avatar.animations.values()) {
         if (animation.playState == Animation.PlayState.PLAYING) {
            list.add(animation.name);
         }
      }

      return list;
   }

   @LuaWhitelist
   public String getVersion() {
      return this.avatar.version.toString();
   }

   @LuaWhitelist
   public String getAuthors() {
      return this.avatar.authors;
   }

   @LuaWhitelist
   public String getName() {
      return this.avatar.name;
   }

   @LuaWhitelist
   public boolean hasTexture() {
      return this.avatar.hasTexture;
   }

   @LuaWhitelist
   public boolean hasScriptError() {
      return this.avatar.scriptError;
   }

   @LuaWhitelist
   public int getComplexity() {
      return this.avatar.complexity.getTotal();
   }

   @LuaWhitelist
   public int getMaxComplexity() {
      return this.avatar.complexity.max;
   }

   @LuaWhitelist
   public int getTickCount() {
      return this.avatar.tick.getTotal();
   }

   @LuaWhitelist
   public int getMaxTickCount() {
      return this.avatar.tick.max;
   }

   @LuaWhitelist
   public int getRenderCount() {
      return this.avatar.render.getTotal();
   }

   @LuaWhitelist
   public int getMaxRenderCount() {
      return this.avatar.render.max;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_world_tick_count")
   public int getWorldTickCount() {
      return this.avatar.worldTick.getTotal();
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_world_tick_count")
   public int getMaxWorldTickCount() {
      return this.avatar.worldTick.max;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_permission_level")
   public String getPermissionLevel() {
      return "max";
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_size")
   public double getSize() {
      return 0.0;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_volume")
   public int getVolume() {
      return 1;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_animation_complexity")
   public int getAnimationComplexity() {
      return this.avatar.animationComplexity;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_animation_complexity")
   public int getMaxAnimationComplexity() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_animation_count")
   public int getAnimationCount() {
      return this.avatar.animation.pre;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_animation_count")
   public int getMaxAnimationCount() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_remaining_particles")
   public int getRemainingParticles() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_particles")
   public int getMaxParticles() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_remaining_sounds")
   public int getRemainingSounds() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_sounds")
   public int getMaxSounds() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.can_edit_vanilla_model")
   public boolean canEditVanillaModel() {
      return true;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.can_edit_nameplate")
   public boolean canEditNameplate() {
      return true;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.can_have_custom_skull")
   public boolean canHaveCustomSkull() {
      return true;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.can_render_offscreen")
   public boolean canRenderOffscreen() {
      return true;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.can_use_custom_sounds")
   public boolean canUseCustomSounds() {
      return true;
   }

   @LuaWhitelist
   public AvatarAPI store(@LuaNotNil String key, LuaValue value) {
      this.storedStuff.set(key, value == null ? LuaValue.NIL : value);
      return this;
   }

   @LuaWhitelist
   public String getEntityName() {
      if (Minecraft.getInstance().level != null) {
         for (Player player : Minecraft.getInstance().level.players()) {
            if (player.getUUID().equals(this.avatar.owner)) {
               return player.getName().getString();
            }
         }
      }

      return this.avatar.name;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_badges")
   public List<String> getBadges() {
      return List.of();
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_init_count")
   public int getInitCount() {
      return this.avatar.init.pre;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_entity_init_count")
   public int getEntityInitCount() {
      return this.avatar.init.post;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_init_count")
   public int getMaxInitCount() {
      return this.avatar.init.max;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_world_render_count")
   public int getWorldRenderCount() {
      return this.avatar.worldRender.getTotal();
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_world_render_count")
   public int getMaxWorldRenderCount() {
      return this.avatar.worldRender.max;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_buffers_count")
   public int getBuffersCount() {
      return 0;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_buffers_count")
   public int getMaxBuffersCount() {
      return 999999;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_texture_size")
   public int getMaxTextureSize() {
      return 2048;
   }

   @LuaWhitelist
   @LuaMethodDoc("avatar.get_max_buffer_size")
   public int getMaxBufferSize() {
      return 0;
   }

   @Override
   public String toString() {
      return this.avatar.name + " (AvatarAPI)";
   }
}
