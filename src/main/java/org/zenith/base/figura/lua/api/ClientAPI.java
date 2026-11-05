package org.zenith.base.figura.lua.api;

import com.mojang.blaze3d.platform.Window;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.repository.Pack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.api.entity.EntityAPI;
import org.zenith.base.figura.lua.api.entity.ViewerAPI;
import org.zenith.base.figura.lua.api.world.WorldAPI;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.utils.LuaUtils;
import org.zenith.base.figura.utils.TextUtils;
import org.zenith.base.figura.utils.Version;

@LuaWhitelist
@LuaTypeDoc(name = "ClientAPI", value = "client")
public final class ClientAPI {
   public static final ClientAPI INSTANCE = new ClientAPI();
   private static final HashMap<String, Boolean> LOADED_MODS = new HashMap<>();

   private ClientAPI() {
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_fps")
   public static int getFPS() {
      return Minecraft.getInstance().getFps();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_fps_string")
   public static String getFPSString() {
      return String.valueOf(getFPS());
   }

   @LuaWhitelist
   @LuaMethodDoc("client.is_paused")
   public static boolean isPaused() {
      return Minecraft.getInstance().isPaused();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_version")
   public static String getVersion() {
      return SharedConstants.getCurrentVersion().name();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_version_name")
   public static String getVersionName() {
      return SharedConstants.getCurrentVersion().name();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.is_snapshot")
   public static boolean isSnapshot() {
      return !SharedConstants.getCurrentVersion().stable();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_client_brand")
   public static String getClientBrand() {
      return ClientBrandRetriever.getClientModName();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_server_brand")
   public static String getServerBrand() {
      Minecraft client = Minecraft.getInstance();
      if (client.player == null) {
         return null;
      } else {
         return client.getSingleplayerServer() != null ? "Integrated" : client.getConnection().serverBrand();
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_java_version")
   public static String getJavaVersion() {
      return System.getProperty("java.version");
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_used_memory")
   public static long getUsedMemory() {
      return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_max_memory")
   public static long getMaxMemory() {
      return Runtime.getRuntime().maxMemory();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_allocated_memory")
   public static long getAllocatedMemory() {
      return Runtime.getRuntime().totalMemory();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.is_window_focused")
   public static boolean isWindowFocused() {
      return Minecraft.getInstance().isWindowActive();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.is_hud_enabled")
   public static boolean isHudEnabled() {
      return !Minecraft.getInstance().gui.hud.isHidden();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.is_debug_overlay_enabled")
   public static boolean isDebugOverlayEnabled() {
      return Minecraft.getInstance().gui.hud.getDebugOverlay().showDebugScreen();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_entity_count")
   public static int getEntityCount() {
      Minecraft client = Minecraft.getInstance();
      return client.level == null ? 0 : client.level.getEntityCount();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_particle_count")
   public static String getParticleCount() {
      return Minecraft.getInstance().particleEngine.countParticles();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.generate_uuid")
   public static int[] generateUUID() {
      return UUIDUtil.uuidToIntArray(UUID.randomUUID());
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), value = "client.get_text_width")
   public static int getTextWidth(@LuaNotNil String text) {
      return Minecraft.getInstance().font.width(TextUtils.tryParseJson(text));
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "text"), value = "client.get_text_height")
   public static int getTextHeight(String text) {
      if (text == null) {
         text = "";
      }

      return TextUtils.getHeight(TextUtils.splitText(TextUtils.tryParseJson(text), "\n"), Minecraft.getInstance().font);
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_text_dimensions")
   public static FiguraVec2 getTextDimensions(@LuaNotNil String text, int maxWidth, Boolean wrap) {
      Component component = TextUtils.tryParseJson(text);
      Font renderer = Minecraft.getInstance().font;
      List<Component> lines = TextUtils.formatInBounds(component, renderer, maxWidth, wrap == null || wrap);
      return FiguraVec2.of(TextUtils.getWidth(lines, renderer), TextUtils.getHeight(lines, renderer));
   }

   @LuaWhitelist
   @LuaMethodDoc("client.is_integrated_server")
   public static boolean isIntegratedServer() {
      return Minecraft.getInstance().getSingleplayerServer() != null;
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_figura_version")
   public static String getFiguraVersion() {
      return FiguraMod.VERSION.toString();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_frame_time")
   public static double getFrameTime() {
      return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_system_time")
   public static long getSystemTime() {
      return System.currentTimeMillis();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_mouse_pos")
   public static FiguraVec2 getMousePos() {
      return FiguraVec2.of(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos());
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_window_size")
   public static FiguraVec2 getWindowSize() {
      Window window = Minecraft.getInstance().getWindow();
      return FiguraVec2.of(window.getScreenWidth(), window.getScreenHeight());
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_scaled_window_size")
   public static FiguraVec2 getScaledWindowSize() {
      Window window = Minecraft.getInstance().getWindow();
      return FiguraVec2.of(window.getGuiScaledWidth(), window.getGuiScaledHeight());
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_gui_scale")
   public static double getGuiScale() {
      return Minecraft.getInstance().getWindow().getGuiScale();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_fov")
   public static double getFOV() {
      return ((Integer)Minecraft.getInstance().options.fov().get()).intValue();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_camera_pos")
   public static FiguraVec3 getCameraPos() {
      return FiguraVec3.fromVec3(Minecraft.getInstance().gameRenderer.mainCamera().position());
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_camera_rot")
   public static FiguraVec3 getCameraRot() {
      Quaternionf quaternion = Minecraft.getInstance().gameRenderer.mainCamera().rotation();
      Vector3f vec = new Vector3f();
      quaternion.getEulerAnglesYXZ(vec);
      double f = 180.0 / Math.PI;
      vec.y = (float)(Math.PI - vec.y);
      if (vec.y > Math.PI) {
         vec.y -= (float) (Math.PI * 2);
      } else if (vec.y < -Math.PI) {
         vec.y += (float) (Math.PI * 2);
      }

      return FiguraVec3.of(vec.x * f, vec.y * f, vec.z * f);
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_camera_dir")
   public static FiguraVec3 getCameraDir() {
      return FiguraVec3.fromVec3f(new Vector3f(Minecraft.getInstance().gameRenderer.mainCamera().forwardVector()));
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_active_lang")
   public static String getActiveLang() {
      return Minecraft.getInstance().options.languageCode;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "modID"), value = "client.is_mod_loaded")
   public static boolean isModLoaded(String id) {
      return !"optifine".equals(id) && !"optifabric".equals(id) ? LOADED_MODS.computeIfAbsent(id, FabricLoader.getInstance()::isModLoaded) : false;
   }

   @LuaWhitelist
   @LuaMethodDoc("client.has_shader_pack_mod")
   public static boolean hasShaderPackMod() {
      return FabricLoader.getInstance().isModLoaded("iris") || FabricLoader.getInstance().isModLoaded("oculus");
   }

   @LuaWhitelist
   @LuaMethodDoc("client.has_shader_pack")
   public static boolean hasShaderPack() {
      if (!hasShaderPackMod()) {
         return false;
      }

      try {
         Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
         Object instance = apiClass.getMethod("getInstance").invoke(null);
         return (Boolean)apiClass.getMethod("isShaderPackInUse").invoke(instance);
      } catch (ReflectiveOperationException e) {
         return false;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "path"), value = "client.has_resource")
   public static boolean hasResource(@LuaNotNil String path) {
      Identifier resource = LuaUtils.parsePath(path);

      try {
         return Minecraft.getInstance().getResourceManager().getResource(resource).isPresent();
      } catch (Exception e) {
         return false;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_active_resource_packs")
   public static List<String> getActiveResourcePacks() {
      List<String> result = new ArrayList<>();

      for (Pack profile : Minecraft.getInstance().getResourcePackRepository().getSelectedPacks()) {
         result.add(profile.getTitle().getString());
      }

      return result;
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_date")
   public static Map<String, Object> getDate() {
      Map<String, Object> map = new HashMap<>();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      map.put("day", calendar.get(5));
      map.put("month", calendar.get(2) + 1);
      map.put("year", calendar.get(1));
      map.put("hour", calendar.get(11));
      map.put("minute", calendar.get(12));
      map.put("second", calendar.get(13));
      map.put("millisecond", calendar.get(14));
      map.put("week_day", calendar.get(7));
      map.put("year_day", calendar.get(6));
      map.put("week", calendar.get(3));
      map.put("timezone", calendar.getTimeZone().getID());
      map.put("timezone_offset", calendar.getTimeZone().getOffset(calendar.getTimeInMillis()) / 1000);
      map.put("daylight_saving", calendar.getTimeZone().inDaylightTime(calendar.getTime()));
      return map;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = {String.class, String.class}, argumentNames = {"first", "second"}),
      value = "client.compare_versions"
   )
   public static int compareVersions(@LuaNotNil String first, @LuaNotNil String second) {
      return new Version(first).compareTo(new Version(second));
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "uuid"), value = "client.uuid_to_int_array")
   public static int[] uuidToIntArray(@LuaNotNil String uuid) {
      return UUIDUtil.uuidToIntArray(UUID.fromString(uuid));
   }

   @LuaWhitelist
   @LuaMethodDoc("client.int_uuid_to_string")
   public static String intUUIDToString(int a, int b, int c, int d) {
      return UUIDUtil.uuidFromIntArray(new int[]{a, b, c, d}).toString();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_viewer")
   public static ViewerAPI getViewer() {
      return Minecraft.getInstance().player == null ? null : new ViewerAPI(Minecraft.getInstance().player);
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_camera_entity")
   public static EntityAPI<?> getCameraEntity() {
      return EntityAPI.wrap(Minecraft.getInstance().getCameraEntity());
   }

   @LuaWhitelist
   @LuaMethodDoc("client.list_atlases")
   public static List<String> listAtlases() {
      List<String> atlases = new ArrayList<>();

      for (Field field : AtlasIds.class.getFields()) {
         try {
            if (field.get(null) instanceof Identifier id) {
               atlases.add(id.withPath(path -> "textures/atlas/" + path + ".png").toString());
            }
         } catch (IllegalAccessException e) {
         }
      }

      return atlases;
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_atlas")
   public static TextureAtlasAPI getAtlas(@LuaNotNil String atlas) {
      try {
         return new TextureAtlasAPI((TextureAtlas)Minecraft.getInstance().getTextureManager().getTexture(LuaUtils.parsePath(atlas)));
      } catch (RuntimeException e) {
         return null;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_translated_string")
   public static String getTranslatedString(@LuaNotNil String key, LuaValue args) {
      if (args == null) {
         return Component.translatable(key).getString();
      }

      if (!args.istable()) {
         return Component.translatable(key, new Object[]{args.tojstring()}).getString();
      }

      Object[] arguments = new Object[args.length()];

      for (int i = 0; i < arguments.length; i++) {
         arguments[i] = args.get(i + 1).tojstring();
      }

      return Component.translatable(key, arguments).getString();
   }

   @LuaWhitelist
   @LuaMethodDoc("client.get_registry")
   public static List<String> getRegistry(@LuaNotNil String registryName) {
      Registry<?> registry = (Registry<?>)BuiltInRegistries.REGISTRY.getValue(Identifier.parse(registryName));
      if (registry == null) {
         throw new LuaError("Registry " + registryName + " does not exist");
      } else {
         return registry.filterFeatures(WorldAPI.getCurrentWorld().enabledFeatures())
            .listElementIds()
            .map(ResourceKey::identifier)
            .<String>map(Identifier::toString)
            .collect(Collectors.toList());
      }
   }

   @Override
   public String toString() {
      return "ClientAPI";
   }
}
