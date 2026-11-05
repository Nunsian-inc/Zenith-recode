package org.zenith.base.figura.lua.api.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import org.luaj.vm2.LuaError;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.mixin.particle.ParticleManagerAccessor;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "ParticleAPI", value = "particles")
public final class ParticleAPI {
   private final Avatar owner;
   private final Set<Particle> spawnedParticles = Collections.newSetFromMap(new IdentityHashMap<>());

   public ParticleAPI(Avatar owner) {
      this.owner = owner;
   }

   private LuaParticle generate(String rawId, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      try {
         Minecraft client = Minecraft.getInstance();
         if (client.level == null) {
            throw new LuaError("Cannot create particles without a client world");
         } else {
            String id = convertOldToNewParticleFormat(rawId);
            ParticleOptions effect = ParticleArgument.readParticle(new StringReader(id), client.level.registryAccess());
            Particle particle = ((ParticleManagerAccessor)client.particleEngine).figura$createParticle(effect, x, y, z, velocityX, velocityY, velocityZ);
            if (particle == null) {
               throw new LuaError("Could not parse particle \"" + id + "\"");
            } else {
               return new LuaParticle(id, particle, this);
            }
         }
      } catch (LuaError var18) {
         throw var18;
      } catch (Exception var19) {
         String message = var19.getMessage();
         throw new LuaError(message == null ? "Could not parse particle \"" + rawId + "\"" : message);
      }
   }

   @LuaWhitelist
   public LuaParticle newParticle(@LuaNotNil String id, Object x, Object y, Double z, Object velocityX, Double velocityY, Double velocityZ) {
      Pair<FiguraVec3, FiguraVec3> vectors = LuaUtils.parse2Vec3("newParticle", x, y, z, velocityX, velocityY, velocityZ, 2);
      FiguraVec3 pos = (FiguraVec3)vectors.getFirst();
      FiguraVec3 velocity = (FiguraVec3)vectors.getSecond();
      return this.generate(id, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z).spawn();
   }

   @LuaWhitelist
   public ParticleAPI removeParticles() {
      for (Particle particle : Set.copyOf(this.spawnedParticles)) {
         particle.remove();
      }

      this.spawnedParticles.clear();
      return this;
   }

   @LuaWhitelist
   public boolean isPresent(String id) {
      try {
         this.generate(id, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
         return true;
      } catch (RuntimeException var3) {
         return false;
      }
   }

   @LuaWhitelist
   public LuaParticle __index(String id) {
      return this.generate(id, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   }

   void spawn(Particle particle) {
      if (!Minecraft.getInstance().isPaused()) {
         this.spawnedParticles.add(particle);
         Minecraft.getInstance().particleEngine.add(particle);
      }
   }

   void forget(Particle particle) {
      this.spawnedParticles.remove(particle);
   }

   private static String convertOldToNewParticleFormat(String id) {
      if (!id.contains("block ") && !id.contains("block_marker ") && !id.contains("falling_dust ")) {
         if (id.contains("dust_color_transition ")) {
            id = id.replaceFirst("minecraft:", "");
            String[] p = id.split(" ");
            if (p.length >= 8 && Objects.equals(p[0], "dust_color_transition")) {
               return "dust_color_transition{from_color:["
                  + decimal(p[1])
                  + ","
                  + decimal(p[2])
                  + ","
                  + decimal(p[3])
                  + "],scale:"
                  + p[4]
                  + ",to_color:["
                  + decimal(p[5])
                  + ","
                  + decimal(p[6])
                  + ","
                  + decimal(p[7])
                  + "]}";
            }
         } else if (id.contains("dust ")) {
            id = id.replaceFirst("minecraft:", "");
            String[] p = id.split(" ");
            if (p.length >= 5 && Objects.equals(p[0], "dust")) {
               return "dust{color:[" + decimal(p[1]) + "," + decimal(p[2]) + "," + decimal(p[3]) + "],scale:" + p[4] + "}";
            }
         } else if (id.contains("item ")) {
            id = id.replaceFirst("minecraft:", "");
            String[] p = id.split(" ");
            if (p.length >= 2 && Objects.equals(p[0], "item")) {
               return "item{item:{id:" + p[1] + "}}";
            }
         } else if (id.contains("sculk_charge ")) {
            String[] p = id.replaceFirst("minecraft:", "").split(" ");
            if (p.length >= 2) {
               return "sculk_charge{roll:" + p[1] + "}";
            }
         } else if (id.contains("shriek ")) {
            String[] p = id.replaceFirst("minecraft:", "").split(" ");
            if (p.length >= 2) {
               return "shriek{delay:" + p[1] + "}";
            }
         } else if (id.contains("vibration ")) {
            String[] p = id.replaceFirst("minecraft:", "").split(" ");
            if (p.length >= 5) {
               return "vibration{arrival_in_ticks:" + p[4] + ",destination:{type:block,pos:[" + p[1] + "," + p[2] + "," + p[3] + "]}}";
            }
         } else if (id.contains("entity_effect") && !id.contains("{")) {
            return id + "{color:[0.0,0.0,0.0,1.0]}";
         }

         return id;
      } else {
         id = id.replaceFirst("minecraft:", "");
         String kind = id.startsWith("block_marker ") ? "block_marker" : (id.startsWith("falling_dust ") ? "falling_dust" : "block");
         String blockPart = id.substring(id.indexOf(kind) + kind.length()).trim();
         String blockName = blockPart.contains("[") ? blockPart.substring(0, blockPart.indexOf(91)) : blockPart;
         StringBuilder converted = new StringBuilder(kind).append("{block_state:{Name:\"").append(blockName).append("\"");
         if (blockPart.contains("[") && blockPart.contains("]")) {
            String properties = blockPart.substring(blockPart.indexOf(91) + 1, blockPart.indexOf(93)).replaceAll("=", ":\"").replaceAll(",", "\",") + "\"";
            converted.append(",Properties:{").append(properties).append('}');
         }

         return converted.append("}}").toString();
      }
   }

   private static String decimal(String value) {
      return value.contains(".") ? value : value + ".0";
   }

   @Override
   public String toString() {
      return "ParticleAPI";
   }
}
