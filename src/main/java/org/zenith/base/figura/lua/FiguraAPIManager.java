package org.zenith.base.figura.lua;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import org.zenith.base.figura.animation.Animation;
import org.zenith.base.figura.lua.api.AnimationAPI;
import org.zenith.base.figura.lua.api.AvatarAPI;
import org.zenith.base.figura.lua.api.ClientAPI;
import org.zenith.base.figura.lua.api.FileAPI;
import org.zenith.base.figura.lua.api.HostAPI;
import org.zenith.base.figura.lua.api.RaycastAPI;
import org.zenith.base.figura.lua.api.RendererAPI;
import org.zenith.base.figura.lua.api.TextureAPI;
import org.zenith.base.figura.lua.api.TextureAtlasAPI;
import org.zenith.base.figura.lua.api.data.DataAPI;
import org.zenith.base.figura.lua.api.data.FiguraBuffer;
import org.zenith.base.figura.lua.api.data.FiguraFuture;
import org.zenith.base.figura.lua.api.data.FiguraInputStream;
import org.zenith.base.figura.lua.api.data.FiguraOutputStream;
import org.zenith.base.figura.lua.api.data.ResourcesAPI;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.api.entity.LivingEntityAPI;
import org.zenith.base.figura.lua.api.entity.NullEntity;
import org.zenith.base.figura.lua.api.entity.PlayerAPI;
import org.zenith.base.figura.lua.api.entity.ViewerAPI;
import org.zenith.base.figura.lua.api.event.EventsAPI;
import org.zenith.base.figura.lua.api.event.LuaEvent;
import org.zenith.base.figura.lua.api.json.FiguraJsonArray;
import org.zenith.base.figura.lua.api.json.FiguraJsonBuilder;
import org.zenith.base.figura.lua.api.json.FiguraJsonObject;
import org.zenith.base.figura.lua.api.json.FiguraJsonSerializer;
import org.zenith.base.figura.lua.api.json.JsonAPI;
import org.zenith.base.figura.lua.api.keybind.FiguraKeybind;
import org.zenith.base.figura.lua.api.keybind.KeybindAPI;
import org.zenith.base.figura.lua.api.math.MatricesAPI;
import org.zenith.base.figura.lua.api.math.VectorsAPI;
import org.zenith.base.figura.lua.api.nameplate.EntityNameplateCustomization;
import org.zenith.base.figura.lua.api.nameplate.NameplateAPI;
import org.zenith.base.figura.lua.api.nameplate.NameplateCustomization;
import org.zenith.base.figura.lua.api.nameplate.NameplateCustomizationGroup;
import org.zenith.base.figura.lua.api.particle.LuaParticle;
import org.zenith.base.figura.lua.api.particle.ParticleAPI;
import org.zenith.base.figura.lua.api.ping.PingAPI;
import org.zenith.base.figura.lua.api.ping.PingFunction;
import org.zenith.base.figura.lua.api.sound.LuaSound;
import org.zenith.base.figura.lua.api.sound.SoundAPI;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaGroupPart;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaModelAPI;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaModelPart;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaPart;
import org.zenith.base.figura.lua.api.world.BiomeAPI;
import org.zenith.base.figura.lua.api.world.BlockStateAPI;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.lua.api.world.WorldAPI;
import org.zenith.base.figura.math.matrix.FiguraMat2;
import org.zenith.base.figura.math.matrix.FiguraMat3;
import org.zenith.base.figura.math.matrix.FiguraMat4;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.math.vector.FiguraVec4;
import org.zenith.base.figura.model.FiguraModelPart;
import org.zenith.base.figura.model.rendering.Vertex;
import org.zenith.base.figura.model.rendering.texture.FiguraTexture;
import org.zenith.base.figura.model.rendertasks.BlockTask;
import org.zenith.base.figura.model.rendertasks.EntityTask;
import org.zenith.base.figura.model.rendertasks.ItemTask;
import org.zenith.base.figura.model.rendertasks.RenderTask;
import org.zenith.base.figura.model.rendertasks.SpriteTask;
import org.zenith.base.figura.model.rendertasks.TextTask;

public final class FiguraAPIManager {
   public static final Set<Class<?>> WHITELISTED_CLASSES = new LinkedHashSet<Class<?>>() {
      {
         this.add(FiguraVec2.class);
         this.add(FiguraVec3.class);
         this.add(FiguraVec4.class);
         this.add(FiguraMat2.class);
         this.add(FiguraMat3.class);
         this.add(FiguraMat4.class);
         this.add(NullEntity.class);
         this.add(EntityAPI.class);
         this.add(LivingEntityAPI.class);
         this.add(PlayerAPI.class);
         this.add(ViewerAPI.class);
         this.add(EventsAPI.class);
         this.add(LuaEvent.class);
         this.add(Vertex.class);
         this.add(FiguraModelPart.class);
         this.add(RenderTask.class);
         this.add(ItemTask.class);
         this.add(BlockTask.class);
         this.add(EntityTask.class);
         this.add(TextTask.class);
         this.add(SpriteTask.class);
         this.add(SoundAPI.class);
         this.add(LuaSound.class);
         this.add(ParticleAPI.class);
         this.add(LuaParticle.class);
         this.add(VanillaModelAPI.class);
         this.add(VanillaPart.class);
         this.add(VanillaGroupPart.class);
         this.add(VanillaModelPart.class);
         this.add(KeybindAPI.class);
         this.add(FiguraKeybind.class);
         this.add(NameplateAPI.class);
         this.add(NameplateCustomization.class);
         this.add(EntityNameplateCustomization.class);
         this.add(NameplateCustomizationGroup.class);
         this.add(VectorsAPI.class);
         this.add(MatricesAPI.class);
         this.add(WorldAPI.class);
         this.add(BlockStateAPI.class);
         this.add(ItemStackAPI.class);
         this.add(BiomeAPI.class);
         this.add(PingAPI.class);
         this.add(PingFunction.class);
         this.add(TextureAPI.class);
         this.add(FiguraTexture.class);
         this.add(TextureAtlasAPI.class);
         this.add(DataAPI.class);
         this.add(FiguraBuffer.class);
         this.add(FiguraInputStream.class);
         this.add(FiguraOutputStream.class);
         this.add(FiguraFuture.class);
         this.add(FileAPI.class);
         this.add(ResourcesAPI.class);
         this.add(AnimationAPI.class);
         this.add(Animation.class);
         this.add(HostAPI.class);
         this.add(RendererAPI.class);
         this.add(ClientAPI.class);
         this.add(AvatarAPI.class);
         this.add(JsonAPI.class);
         this.add(FiguraJsonBuilder.class);
         this.add(FiguraJsonSerializer.class);
         this.add(FiguraJsonArray.class);
         this.add(FiguraJsonObject.class);
         this.add(RaycastAPI.class);
      }
   };
   public static final Map<String, Function<FiguraLuaRuntime, Object>> API_GETTERS = new LinkedHashMap<String, Function<FiguraLuaRuntime, Object>>() {
      {
         this.put("events", r -> r.events = new EventsAPI());
         this.put("sounds", r -> new SoundAPI(r.owner));
         this.put("vanilla_model", r -> r.vanilla_model = new VanillaModelAPI(r.owner));
         this.put("keybinds", r -> r.keybinds = new KeybindAPI(r.owner));
         this.put("host", r -> r.host = new HostAPI(r.owner));
         this.put("nameplate", r -> r.nameplate = new NameplateAPI());
         this.put("renderer", r -> r.renderer = new RendererAPI(r.owner));
         this.put("animations", r -> r.animations = new AnimationAPI(r.owner));
         this.put("client", r -> ClientAPI.INSTANCE);
         this.put("particles", r -> r.particles = new ParticleAPI(r.owner));
         this.put("avatar", r -> r.avatar_meta = new AvatarAPI(r.owner));
         this.put("vectors", r -> VectorsAPI.INSTANCE);
         this.put("matrices", r -> MatricesAPI.INSTANCE);
         this.put("world", r -> WorldAPI.INSTANCE);
         this.put("pings", r -> r.ping = new PingAPI(r.owner));
         this.put("textures", r -> r.texture = new TextureAPI(r.owner));
         this.put("data", r -> new DataAPI(r.owner));
         this.put("file", r -> new FileAPI(r.owner));
         this.put("json", r -> JsonAPI.INSTANCE);
         this.put("resources", r -> new ResourcesAPI(r.owner));
         this.put("raycast", r -> r.raycast = new RaycastAPI(r.owner));
      }
   };

   private FiguraAPIManager() {
   }

   public static void setupTypesAndAPIs(FiguraLuaRuntime runtime) {
      for (Class<?> clazz : WHITELISTED_CLASSES) {
         runtime.registerClass(clazz);
      }

      for (Entry<String, Function<FiguraLuaRuntime, Object>> entry : API_GETTERS.entrySet()) {
         runtime.setGlobal(entry.getKey(), entry.getValue().apply(runtime));
      }
   }
}
