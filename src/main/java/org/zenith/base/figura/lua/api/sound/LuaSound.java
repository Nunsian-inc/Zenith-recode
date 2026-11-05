package org.zenith.base.figura.lua.api.sound;

import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "Sound", value = "sound")
public class LuaSound {
   private final String id;
   private float volume = 1.0F;
   private float pitch = 1.0F;
   private float attenuation = 1.0F;
   private boolean loop;
   private String subtitle;
   private FiguraVec3 pos = FiguraVec3.of();
   private boolean playing;

   public LuaSound(String id) {
      this.id = id;
   }

   @LuaWhitelist
   public String getId() {
      return this.id;
   }

   @LuaWhitelist
   @LuaMethodDoc("sound.get_pos")
   public FiguraVec3 getPos() {
      return this.pos;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = FiguraVec3.class, argumentNames = "pos"),
            @LuaMethodOverload(argumentTypes = {Double.class, Double.class, Double.class}, argumentNames = {"x", "y", "z"})
      },
      aliases = "pos",
      value = "sound.set_pos"
   )
   public LuaSound setPos(Object x, Double y, Double z) {
      this.pos = LuaUtils.parseVec3("setPos", x, y, z);
      return this;
   }

   @LuaWhitelist
   public LuaSound pos(Object x, Double y, Double z) {
      return this.setPos(x, y, z);
   }

   @LuaWhitelist
   public float getVolume() {
      return this.volume;
   }

   @LuaWhitelist
   public LuaSound setVolume(float volume) {
      this.volume = volume;
      return this;
   }

   @LuaWhitelist
   public LuaSound volume(float volume) {
      return this.setVolume(volume);
   }

   @LuaWhitelist
   public float getPitch() {
      return this.pitch;
   }

   @LuaWhitelist
   public LuaSound setPitch(float pitch) {
      this.pitch = pitch;
      return this;
   }

   @LuaWhitelist
   public LuaSound pitch(float pitch) {
      return this.setPitch(pitch);
   }

   @LuaWhitelist
   @LuaMethodDoc("sound.get_attenuation")
   public float getAttenuation() {
      return this.attenuation;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = Float.class, argumentNames = "attenuation"),
      aliases = "attenuation",
      value = "sound.set_attenuation"
   )
   public LuaSound setAttenuation(float attenuation) {
      this.attenuation = attenuation;
      return this;
   }

   @LuaWhitelist
   public LuaSound attenuation(float attenuation) {
      return this.setAttenuation(attenuation);
   }

   @LuaWhitelist
   @LuaMethodDoc("sound.is_looping")
   public boolean isLooping() {
      return this.loop;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = Boolean.class, argumentNames = "loop"), aliases = "loop", value = "sound.set_loop")
   public LuaSound setLoop(boolean loop) {
      this.loop = loop;
      return this;
   }

   @LuaWhitelist
   public LuaSound loop(boolean loop) {
      return this.setLoop(loop);
   }

   @LuaWhitelist
   @LuaMethodDoc("sound.get_subtitle")
   public String getSubtitle() {
      return this.subtitle;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "subtitle"), aliases = "subtitle", value = "sound.set_subtitle")
   public LuaSound setSubtitle(String subtitle) {
      this.subtitle = subtitle;
      return this;
   }

   @LuaWhitelist
   public LuaSound subtitle(String subtitle) {
      return this.setSubtitle(subtitle);
   }

   @LuaWhitelist
   public boolean isPlaying() {
      return this.playing;
   }

   @LuaWhitelist
   public LuaSound play() {
      this.playing = true;
      return this;
   }

   @LuaWhitelist
   public LuaSound pause() {
      this.playing = false;
      return this;
   }

   @LuaWhitelist
   public LuaSound stop() {
      this.playing = false;
      return this;
   }

   @Override
   public String toString() {
      return this.id + " (Sound)";
   }
}
