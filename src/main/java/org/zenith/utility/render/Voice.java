package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.CloudStateEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.hud.WatermarkIslandHud;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Prefs;
import org.zenith.utility.api.Share;
import org.zenith.utility.api.VoiceDeviceSetting;
import org.zenith.utility.misc.AudioDevices;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.VoiceState;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.Island;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class Voice implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final double CONST_doubleValue = 4.0;
   private static final double CONST_doubleValue2 = 32.0;
   private static final double CONST_doubleValue3 = 1.62;
   private static final int[] CONST_intValues = new int[]{16, 48, 96};
   public final BooleanSetting enabled = new BooleanSetting("gui.voice.enabled", "gui.voice.enabled.desc", false);
   public final ModeSetting activation = new ModeSetting(
      "gui.voice.activation", "gui.voice.activation.desc", "gui.voice.activation.ptt", "gui.voice.activation.voice"
   );
   public final BindSetting talkKey = new BindSetting("gui.voice.talkKey", "gui.voice.talkKey.desc", 86, () -> this.activation.isSelected(0));
   public final BindSetting radioKey = new BindSetting("gui.voice.radioKey", "gui.voice.radioKey.desc", -1);
   public final SliderSetting sensitivity = new SliderSetting("gui.voice.sensitivity", 60.0F, 0.0F, 100.0F, 5.0F, "gui.voice.sensitivity.desc", " %", null);
   public final SliderSetting volume = new SliderSetting("gui.voice.volume", 100.0F, 0.0F, 200.0F, 5.0F, "gui.voice.volume.desc", " %", null);
   public final SliderSetting radioVolume = new SliderSetting("gui.voice.radioVolume", 80.0F, 0.0F, 200.0F, 5.0F, "gui.voice.radioVolume.desc", " %", null);
   public final ModeSetting range = new ModeSetting(
      "gui.voice.range", "gui.voice.range.desc", "gui.voice.range.whisper", "gui.voice.range.normal", "gui.voice.range.loud"
   );
   public final SliderSetting micGain = new SliderSetting("gui.voice.micGain", 100.0F, 0.0F, 300.0F, 5.0F, "gui.voice.micGain.desc", " %", null);
   public final VoiceDeviceSetting input = new VoiceDeviceSetting("gui.voice.input", "gui.voice.input.desc", "gui.voice.device.default", AudioDevices::display);
   public final VoiceDeviceSetting output = new VoiceDeviceSetting(
      "gui.voice.output", "gui.voice.output.desc", "gui.voice.device.default", AudioDevices::display
   );
   public final BooleanSetting denoise = new BooleanSetting("gui.voice.denoise", "gui.voice.denoise.desc", true);
   public final BooleanSetting acceptRadio = new BooleanSetting("gui.voice.acceptRadio", "gui.voice.acceptRadio.desc", true);
   public final BooleanSetting spatial = new BooleanSetting("gui.voice.spatial", "gui.voice.spatial.desc", true);
   public final BooleanSetting hrtf = new BooleanSetting("gui.voice.hrtf", "gui.voice.hrtf.desc", false, this.spatial::isEnabled);
   public final ModeSetting quality = new ModeSetting(
      "gui.voice.quality", "gui.voice.quality.desc", "gui.voice.quality.low", "gui.voice.quality.normal", "gui.voice.quality.high"
   );
   public final BooleanSetting overlay = new BooleanSetting("gui.voice.overlay", "gui.voice.overlay.desc", true);
   public final BooleanSetting heads = new BooleanSetting("gui.voice.heads", "gui.voice.heads.desc", true);
   public final BindSetting muteKey = new BindSetting("gui.voice.muteKey", "gui.voice.muteKey.desc", -1);
   public final BindSetting deafenKey = new BindSetting("gui.voice.deafenKey", "gui.voice.deafenKey.desc", -1);
   public final BindSetting partyKey = new BindSetting("gui.voice.partyKey", "gui.voice.partyKey.desc", -1);
   public final BindSetting roomKey = new BindSetting("gui.voice.roomKey", "gui.voice.roomKey.desc", -1);
   public final BindSetting whisperKey = new BindSetting("gui.voice.whisperKey", "gui.voice.whisperKey.desc", -1);
   public final ModeSetting defaultChannel = new ModeSetting(
      "gui.voice.defaultChannel", "gui.voice.defaultChannel.desc", "gui.voice.channel.near", "gui.voice.channel.party", "gui.voice.channel.room"
   );
   public final BooleanSetting rnnoise = new BooleanSetting("gui.voice.rnnoise", "gui.voice.rnnoise.desc", true, this.denoise::isEnabled);
   public final BooleanSetting muffle = new BooleanSetting("gui.voice.muffle", "gui.voice.muffle.desc", true);
   public final SliderSetting channelVolume = new SliderSetting(
      "gui.voice.channelVolume", 100.0F, 0.0F, 200.0F, 5.0F, "gui.voice.channelVolume.desc", " %", null
   );
   private final Map<Long, Voice.Entry> items = new HashMap<>();
   private Map<Long, Float> items2;
   private final File file;
   private String text;
   private int intValue;
   private final VoiceState voiceState;
   private int intValue2;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private long longValue;
   private static final long CONST_longValue = 1500L;
   private String text2;
   private String text3;
   public static final int CONST_intValue = 124;

   public Voice() {
      this.file = new File(Zenith.file, "voice.json");
      this.text = "";
      this.voiceState = new VoiceState(new VoiceState.Handler() {
         @Override
         public boolean method377() {
            Cloud cloud = Voice.cloud();
            return Voice.this.enabled.isEnabled() && cloud != null && cloud.method7863();
         }

         @Override
         public void method378(int n, int n2, int n3, byte[] src, int length) {
            Cloud cloud = Voice.cloud();
            if (cloud != null) {
               cloud.method7902(n, n2, n3, src, length);
            }
         }
      });
      this.intValue2 = -1;
      this.text2 = "";
      this.text3 = "";
      this.range.set("gui.voice.range.normal");
      this.quality.set("gui.voice.quality.normal");
      this.load();
      this.text = this.method374().toString();
      EventManager.register(this);
   }

   private static Cloud cloud() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getCloud();
   }

   public VoiceState getVoiceState() {
      return this.voiceState;
   }

   public List<Setting> method332() {
      return List.of(
         this.enabled,
         this.activation,
         this.talkKey,
         this.radioKey,
         this.sensitivity,
         this.volume,
         this.radioVolume,
         this.range,
         this.micGain,
         this.input,
         this.output,
         this.denoise,
         this.acceptRadio,
         this.spatial,
         this.hrtf,
         this.quality,
         this.overlay,
         this.heads,
         this.muteKey,
         this.deafenKey,
         this.partyKey,
         this.roomKey,
         this.whisperKey,
         this.defaultChannel,
         this.rnnoise,
         this.muffle,
         this.channelVolume
      );
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public void setEnabled(boolean enabled) {
      if (this.enabled.isEnabled() != enabled) {
         this.enabled.setEnabled(enabled);
      }

      this.apply();
   }

   private void apply() {
      boolean flag = this.enabled.isEnabled() || this.flag3;
      if (flag && !this.voiceState.method5360()) {
         if (!this.flag) {
            this.method335();
         }

         this.voiceState.method5359(this.method342());
      }

      Cloud cloud2 = cloud();
      if (this.enabled.isEnabled() && this.voiceState.method5360() && !this.flag2) {
         if (cloud2 != null) {
            cloud2.setHandler(this.voiceState::method5371);
         }

         this.flag2 = true;
         this.intValue2 = -1;
         this.method349();
      } else if (!this.enabled.isEnabled() && this.flag2) {
         if (cloud2 != null) {
            cloud2.setHandler(null);
            cloud2.method7903(0);
         }

         this.flag2 = false;
         this.intValue2 = -1;
      }

      if (!flag && this.voiceState.method5360()) {
         this.voiceState.stop();
      }
   }

   public void shutdown() {
      this.flag3 = false;
      this.method376();
      if (this.voiceState.method5360()) {
         this.voiceState.stop();
      }
   }

   public void method333(boolean flag) {
      if (flag != this.flag3) {
         this.flag3 = flag;
         this.longValue = System.currentTimeMillis();
         if (flag) {
            this.apply();
            this.voiceState.method5363(true);
         } else {
            this.voiceState.method5363(false);
            this.apply();
         }
      }
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void method334() {
      this.longValue = System.currentTimeMillis();
   }

   public void method335() {
      this.input.method9(AudioDevices.method3219());
      this.output.method9(AudioDevices.method3220());
      this.text2 = AudioDevices.method3221();
      this.text3 = AudioDevices.method3222();
      this.flag = true;
   }

   public String inputName() {
      String str = method337(this.voiceState.getText3());
      String str2 = this.input.method11();
      return AudioDevices.display(!str.isEmpty() ? str : (!str2.isEmpty() ? str2 : this.text2));
   }

   public String method336() {
      String str = method337(this.voiceState.getText4());
      String str2 = this.output.method11();
      return AudioDevices.display(!str.isEmpty() ? str : (!str2.isEmpty() ? str2 : this.text3));
   }

   private static String method337(String str) {
      return str != null && !str.equals("OpenAL Soft") ? str : "";
   }

   public String method338() {
      return AudioDevices.display(this.text2);
   }

   public String method339() {
      return AudioDevices.display(this.text3);
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int method340() {
      int index = this.range.getIndex();
      return CONST_intValues[index >= 0 && index < CONST_intValues.length ? index : 1];
   }

   public float method341() {
      return -10.0F - this.sensitivity.getValue3() * 0.6F;
   }

   private VoiceState.Data method342() {
      char intValue = switch (this.quality.getIndex()) {
         case 0 -> '丠';
         case 2 -> '뮀';
         default -> '紀';
      };
      this.input.method12();
      this.output.method12();
      return new VoiceState.Data(
         this.activation.isSelected(0),
         this.method341(),
         this.micGain.getValue3() / 100.0F,
         intValue,
         this.method340(),
         this.spatial.isEnabled() && this.hrtf.isEnabled(),
         this.input.method11(),
         this.output.method11(),
         this.denoise.isEnabled(),
         this.rnnoise.isEnabled(),
         this.method347()
      );
   }

   @EventTarget
   public void onTick(TickEvent event) {
      this.method375();
      if (this.flag3 && System.currentTimeMillis() - this.longValue > 1500L) {
         this.method333(false);
      }

      this.apply();
      if (!this.voiceState.method5360()) {
         this.items2 = null;
      } else {
         VoiceState.Data data = this.method342();
         if (!data.equals(this.voiceState.getData())) {
            this.voiceState.setData(data);
         }

         this.voiceState.setFloatValue(this.volume.getValue3() / 100.0F);
         this.voiceState.setFlag(this.acceptRadio.isEnabled());
         this.items2 = this.method350();
         this.voiceState.setItems(this.items2);
         this.method345();
         this.method349();
      }
   }

   @EventTarget
   public void method343(CloudStateEvent event) {
      this.intValue2 = -1;
      if (event.method6858() && this.voiceState.method5360()) {
         this.method349();
      }
   }

   @EventTarget
   public void method344(KeyEvent event) {
      if (this.voiceState.method5360()) {
         if (this.muteKey.getKeyCode() > 0 && event.isPress(this.muteKey.getKeyCode())) {
            this.voiceState.setFlag3(!this.voiceState.isFlag3());
         }

         if (this.deafenKey.getKeyCode() > 0 && event.isPress(this.deafenKey.getKeyCode())) {
            boolean flag = !this.voiceState.isFlag4();
            this.voiceState.method5362(flag);
            this.voiceState.setFlag3(flag);
            this.intValue2 = -1;
         }
      }
   }

   private void method345() {
      boolean flag = this.activation.isSelected(0) && UseItemUtils.method6521(this.talkKey);
      this.voiceState.method5361(flag, this.method346());
   }

   private int method346() {
      Cloud cloud2 = cloud();
      PartyMember partyMember = cloud2 == null ? null : cloud2.getPartyMember();
      if (partyMember != null && partyMember.getLongValue() != 0L && UseItemUtils.method6521(this.whisperKey)) {
         return 16;
      } else if (partyMember != null && partyMember.getData() != null && UseItemUtils.method6521(this.roomKey)) {
         return 8;
      } else if (partyMember != null && partyMember.method1136() && UseItemUtils.method6521(this.partyKey)) {
         return 4;
      } else {
         return UseItemUtils.method6521(this.radioKey) ? 1 : 0;
      }
   }

   public int method347() {
      Cloud cloud2 = cloud();
      PartyMember partyMember = cloud2 == null ? null : cloud2.getPartyMember();
      if (partyMember == null) {
         return 0;
      } else {
         return switch (this.defaultChannel.getIndex()) {
            case 1 -> partyMember.method1136() ? 4 : 0;
            case 2 -> partyMember.getData() != null ? 8 : 0;
            default -> 0;
         };
      }
   }

   public String method348(long longValue) {
      Cloud cloud2 = cloud();
      if (cloud2 == null) {
         return "#" + longValue;
      } else {
         PartyPlayerInfo partyPlayerInfo = cloud2.state().method8040(longValue);
         if (partyPlayerInfo != null) {
            return method367(partyPlayerInfo);
         } else {
            PartyMember.Data data = cloud2.getPartyMember().getData();
            if (data != null) {
               for (PartyMember.Data3 data3 : data.getItems()) {
                  if (data3.uid() == longValue) {
                     return data3.getText2().isEmpty() ? data3.username() : data3.getText2();
                  }
               }
            }

            return cloud2.getPartyMember().method1149(longValue);
         }
      }
   }

   private void method349() {
      Cloud cloud2 = cloud();
      if (cloud2 != null && cloud2.method7863()) {
         int n = this.voiceState.isFlag4() ? 0 : 1 | (this.acceptRadio.isEnabled() ? 2 : 0);
         if (n != this.intValue2) {
            cloud2.method7903(n);
            this.intValue2 = n;
         }
      }
   }

   private Map<Long, Float> method350() {
      Cloud cloud2 = cloud();
      if (cloud2 == null) {
         return Map.of();
      } else {
         Prefs prefs = cloud2.prefs();
         HashMap map = new HashMap();

         for (PartyPlayerInfo partyPlayerInfo : cloud2.state().method8038()) {
            if (!prefs.method3321(partyPlayerInfo.username()) && !prefs.method3316(partyPlayerInfo.username())) {
               map.put(partyPlayerInfo.uid(), prefs.method3323(partyPlayerInfo.username()) / 100.0F);
            }
         }

         for (PartyPlayerInfo partyPlayerInfo : cloud2.state().method8041()) {
            if (!map.containsKey(partyPlayerInfo.uid()) && !prefs.method3321(partyPlayerInfo.username()) && !prefs.method3316(partyPlayerInfo.username())) {
               map.put(partyPlayerInfo.uid(), prefs.method3323(partyPlayerInfo.username()) / 100.0F);
            }
         }

         PartyMember.Data data = cloud2.getPartyMember().getData();
         if (data != null) {
            for (PartyMember.Data3 data3 : data.getItems()) {
               if (data3.uid() != cloud2.getLongValue() && !map.containsKey(data3.uid()) && !prefs.method3321(data3.username()) && !prefs.method3316(data3.username())
                  )
                {
                  map.put(data3.uid(), prefs.method3323(data3.username()) / 100.0F);
               }
            }
         }

         return map;
      }
   }

   private void method351(float tickDelta) {
      Set<Long> longs = this.voiceState.method5364();
      if (longs.isEmpty()) {
         this.voiceState.setItems2(Map.of());
      } else {
         Cloud cloud2 = cloud();
         var map = this.items2 != null ? this.items2 : this.method350();
         float f = this.volume.getValue3() / 100.0F;
         float f2 = this.radioVolume.getValue3() / 100.0F;
         HashMap map2 = new HashMap();
         Camera mainCamera = mc.gameRenderer == null ? null : mc.gameRenderer.mainCamera();
         Vec3 position = mainCamera == null ? null : mainCamera.position();
         Vec3 eyePosition = mc.player == null ? null : mc.player.getEyePosition(tickDelta);
         double d = mainCamera == null ? 0.0 : Math.toRadians(mainCamera.yRot());
         double d2 = mainCamera == null ? 0.0 : Math.toRadians(mainCamera.xRot());
         Vec3 vec = new Vec3(-Math.sin(d) * Math.cos(d2), -Math.sin(d2), Math.cos(d) * Math.cos(d2));
         Vec3 vec2 = new Vec3(-Math.cos(d), 0.0, -Math.sin(d));
         Vec3 vec3 = vec2.cross(vec);

         for (long key : longs) {
            Float f3 = (Float)map.get(key);
            if (f3 != null && cloud2 != null) {
               int n = this.voiceState.method5368(key);
               if ((n & 1) != 0) {
                  map2.put(key, new VoiceState.Data2(0.0F, 0.0F, 0.0F, f2 * f3));
               } else if ((n & 28) != 0) {
                  float f4 = (n & 16) != 0 ? 1.1F : 1.0F;
                  map2.put(key, new VoiceState.Data2(0.0F, 0.0F, 0.0F, f * f3 * f4 * this.channelVolume.getValue3() / 100.0F));
               } else {
                  PartyPlayerInfo partyPlayerInfo = cloud2.state().method8040(key);
                  Vec3 to = partyPlayerInfo != null && eyePosition != null ? this.method354(partyPlayerInfo, eyePosition, tickDelta, cloud2) : null;
                  if (to != null && position != null) {
                     float f4 = method356(to.distanceTo(eyePosition), this.voiceState.method5370(key));
                     if (!(f4 <= 0.0F)) {
                        float f5 = f * f3 * f4;
                        float f6 = this.muffle.isEnabled() ? this.method352(key, eyePosition, to) : 0.0F;
                        if (!this.spatial.isEnabled()) {
                           map2.put(key, new VoiceState.Data2(0.0F, 0.0F, 0.0F, f5, f6));
                        } else {
                           Vec3 vec4 = to.subtract(position);
                           map2.put(
                              key,
                              new VoiceState.Data2(
                                 (float)vec4.dot(vec2), (float)vec4.dot(vec3), (float)(-vec4.dot(vec)), f5, f6
                              )
                           );
                        }
                     }
                  }
               }
            }
         }

         this.voiceState.setItems2(map2);
      }
   }

   private float method352(long key, Vec3 eyePosition, Vec3 to) {
      Voice.Entry entry = this.items.computeIfAbsent(key, l -> new Voice.Entry());
      long now = System.currentTimeMillis();
      if (now - entry.longValue > 150L) {
         entry.target = this.method353(eyePosition, to);
         entry.longValue = now;
      }

      entry.value = entry.value + (entry.target - entry.value) * 0.25F;
      return entry.value;
   }

   private float method353(Vec3 eyePosition, Vec3 to) {
      if (mc.level != null && mc.player != null) {
         ClipContext clipContext = new ClipContext(eyePosition, to, Block.COLLIDER, Fluid.NONE, mc.player);
         BlockHitResult clip = mc.level.clip(clipContext);
         if (clip != null && clip.getType() != HitResult.Type.MISS) {
            ClipContext clipContext2 = new ClipContext(to, eyePosition, Block.COLLIDER, Fluid.NONE, mc.player);
            BlockHitResult blockHitResult = mc.level.clip(clipContext2);
            double d = blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS ? Math.max(1.0, clip.getLocation().distanceTo(blockHitResult.getLocation())) : 1.0;
            return (float)Math.min(1.0, 0.55 + d * 0.12);
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private Vec3 method354(PartyPlayerInfo partyPlayerInfo, Vec3 eyePosition, float tickDelta, Cloud cloud) {
      Player player = this.method355(partyPlayerInfo.getText2());
      if (player != null) {
         return player.getEyePosition(tickDelta);
      } else if (partyPlayerInfo.method3291(cloud.method7911(), cloud.method7913()) && partyPlayerInfo.method3292()) {
         Vec3 vec = partyPlayerInfo.method3294().add(0.0, 1.62, 0.0);
         return vec.distanceTo(eyePosition) < 32.0 ? null : vec;
      } else {
         return null;
      }
   }

   private Player method355(String str) {
      if (mc.level != null && str != null && !str.isEmpty()) {
         for (Player player : mc.level.players()) {
            if (player != mc.player && str.equalsIgnoreCase(player.getGameProfile().name())) {
               return player;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static float method356(double d, int n) {
      if (d <= 4.0) {
         return 1.0F;
      } else {
         double d2 = (d - 4.0) / Math.max(1.0, n - 4.0);
         return d2 >= 1.0 ? 0.0F : (float)Math.pow(1.0 - d2, 1.6);
      }
   }

   public Voice.Type method357() {
      if (this.flag3) {
         return Voice.Type.TESTING;
      } else if (!this.voiceState.method5360()) {
         return Voice.Type.OFF;
      } else {
         Cloud cloud2 = cloud();
         if (!this.voiceState.getText().isEmpty()) {
            return Voice.Type.MIC_ERROR;
         } else if (!this.voiceState.getText2().isEmpty()) {
            return Voice.Type.OUTPUT_ERROR;
         } else if (cloud2 == null || !cloud2.method7862()) {
            return Voice.Type.OFFLINE;
         } else if (!cloud2.method7863()) {
            return Voice.Type.UNSUPPORTED;
         } else if (this.voiceState.isFlag4()) {
            return Voice.Type.DEAFENED;
         } else if (this.voiceState.isFlag3()) {
            return Voice.Type.MUTED;
         } else if (this.voiceState.isFlag6()) {
            int intValue = this.voiceState.getIntValue() != 0 ? this.voiceState.getIntValue() : this.method347();
            if ((intValue & 16) != 0) {
               return Voice.Type.WHISPER;
            } else if ((intValue & 8) != 0) {
               return Voice.Type.ROOM;
            } else if ((intValue & 4) != 0) {
               return Voice.Type.PARTY;
            } else {
               return (intValue & 1) != 0 ? Voice.Type.RADIO : Voice.Type.TALKING;
            }
         } else {
            Share share = Zenith.getInstance().getShare();
            return share == null || share.position.isEnabled() && share.server.isEnabled() ? Voice.Type.READY : Voice.Type.NO_POSITION;
         }
      }
   }

   public boolean method358() {
      if (!this.isEnabled()) {
         return false;
      } else if (this.voiceState.isFlag6()) {
         return true;
      } else {
         for (long uid : this.voiceState.method5364()) {
            if (uid != -1L && this.voiceState.method5365(uid)) {
               return true;
            }
         }

         return false;
      }
   }

   public String method359() {
      Voice.Type type = this.method357();
      String str = method373("gui.voice.status." + type.name().toLowerCase(Locale.ROOT));
      if (type == Voice.Type.MIC_ERROR) {
         return str + ": " + this.voiceState.getText();
      } else {
         return type == Voice.Type.OUTPUT_ERROR ? str + ": " + this.voiceState.getText2() : str;
      }
   }

   public ZenithColor method360() {
      return switch (this.method357()) {
         case OFF, DEAFENED, MUTED -> new ZenithColor(-495247);
         case TESTING, RADIO, TALKING, PARTY, ROOM, WHISPER -> new ZenithColor(-11870592);
         default -> new ZenithColor(-680437);
         case READY -> new ZenithColor(-10443270);
      };
   }

   public int method361() {
      Cloud cloud2 = cloud();
      return cloud2 == null ? -1 : cloud2.method7864();
   }

   public void method362() {
      Cloud cloud2 = cloud();
      if (cloud2 != null) {
         cloud2.method7865();
      }
   }

   public int latencyMs() {
      int n = this.method361();
      return n < 0 ? -1 : n + 124;
   }

   public int method363() {
      return this.method342().getIntValue() / 1000;
   }

   public static String method364(BindSetting talkKey) {
      int keyCode = talkKey.getKeyCode();
      if (keyCode < 0) {
         return "—";
      } else {
         String str = KeyName.method6701(keyCode);
         return str != null && !str.isEmpty() ? str : String.valueOf(keyCode);
      }
   }

   public List<PartyPlayerInfo> method365() {
      Cloud cloud2 = cloud();
      ArrayList<PartyPlayerInfo> partyPlayerInfos = new ArrayList<>();
      if (cloud2 != null) {
         for (PartyPlayerInfo partyPlayerInfo : cloud2.state().method8038()) {
            if (partyPlayerInfo.method3289()) {
               partyPlayerInfos.add(partyPlayerInfo);
            }
         }

         for (PartyPlayerInfo partyPlayerInfo : cloud2.state().method8041()) {
            if (partyPlayerInfo.method3289() && cloud2.state().method8039(partyPlayerInfo.uid()) == null) {
               partyPlayerInfos.add(partyPlayerInfo);
            }
         }
      }

      partyPlayerInfos.sort((partyPlayerInfo, var1x) -> method367(partyPlayerInfo).compareToIgnoreCase(method367(var1x)));
      return partyPlayerInfos;
   }

   public String method366(PartyPlayerInfo partyPlayerInfo) {
      Cloud cloud2 = cloud();
      if (cloud2 == null || !partyPlayerInfo.method3291(cloud2.method7911(), cloud2.method7913())) {
         return method373("gui.voice.friend.elsewhere");
      } else if (mc.player != null && partyPlayerInfo.method3292()) {
         double d = partyPlayerInfo.method3294().distanceTo(mc.player.position());
         return d <= this.method340() ? method373("gui.voice.friend.near") : method373("gui.voice.friend.far");
      } else {
         return method373("gui.voice.friend.sameServer");
      }
   }

   public static String method367(PartyPlayerInfo partyPlayerInfo) {
      return partyPlayerInfo.getText2() != null && !partyPlayerInfo.getText2().isEmpty() ? partyPlayerInfo.getText2() : partyPlayerInfo.username();
   }

   @EventTarget
   public void method368(DrawEvent event) {
      if (this.voiceState.method5360()) {
         this.method345();
         this.method351(event.getTickDelta());
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            if (this.heads.isEnabled()) {
               this.method372(event.getCustomDrawContext(), currentStyle, event.getTickDelta());
            }

            if (this.overlay.isEnabled()) {
               this.method369(event.getCustomDrawContext(), currentStyle);
            }
         }
      }
   }

   private void method369(CustomDrawContext customDrawContext, ZenithStyle currentStyle) {
      ArrayList<Voice.Data> datas = new ArrayList();
      Voice.Type type = this.method357();
      if (type != Voice.Type.READY) {
         boolean flag = type == Voice.Type.TALKING
            || type == Voice.Type.RADIO
            || type == Voice.Type.TESTING
            || type == Voice.Type.PARTY
            || type == Voice.Type.ROOM
            || type == Voice.Type.WHISPER;
         datas.add(new Voice.Data(this.method359(), this.method360(), flag ? this.voiceState.getFloatValue2() : -100.0F, false, true));
      }

      Cloud cloud2 = cloud();
      if (cloud2 != null) {
         for (long uid : this.voiceState.method5364()) {
            if (this.voiceState.method5365(uid) && uid != -1L) {
               boolean flag = this.voiceState.method5369(uid);
               String text = this.method348(uid);
               int n = this.voiceState.method5368(uid);
               if ((n & 16) != 0) {
                  text = text + " · " + method373("gui.voice.tag.whisper");
               } else if ((n & 4) != 0) {
                  text = text + " · " + method373("gui.voice.tag.party");
               } else if ((n & 8) != 0) {
                  text = text + " · " + method373("gui.voice.tag.room");
               }

               datas.add(
                  new Voice.Data(
                     text, flag ? new ZenithColor(-10443270) : currentStyle.getPrimaryColor().getZenithColor(), this.voiceState.method5366(uid), flag, false
                  )
               );
            }
         }
      }

      if (WatermarkIslandHud.method5668()) {
         this.method370(type, datas);
      } else if (!datas.isEmpty()) {
         Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
         float intValue = GuiStyle.PADDING.intValue();
         float height = font.height() + intValue;
         float floatValue = 6.0F;
         float y = mc.getWindow().getGuiScaledHeight() * 0.38F;
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);

         for (Voice.Data data : datas) {
            String text = data.flag ? data.text + " · " + method373("gui.voice.radioTag") : data.text;
            float width = intValue + 8.0F + font.width(text) + 5.0F + 12.0F + intValue;
            customDrawContext.drawRoundedRect(floatValue, y, width, height, headerRadius, currentStyle.getHudBackground().getZenithColor());
            customDrawContext.drawRoundedRect(floatValue + intValue, y + (height - 4.0F) / 2.0F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), data.zenithColor);
            float f = floatValue + intValue + 8.0F;
            customDrawContext.drawText(font, text, f, y + (height - font.height()) / 2.0F, currentStyle.getTextEnable().getZenithColor());
            method371(customDrawContext, f + font.width(text) + 5.0F, y + height / 2.0F, data.floatValue, data.zenithColor);
            y += height + 2.0F;
         }
      }
   }

   private void method370(Voice.Type type, List<Voice.Data> datas) {
      ArrayList<Voice.Data> datas2 = new ArrayList();
      Voice.Data data = null;

      for (Voice.Data data2 : datas) {
         if (data2.flag2) {
            data = data2;
         } else {
            datas2.add(data2);
         }
      }

      if (!datas2.isEmpty()) {
         StringBuilder builder = new StringBuilder();
         float floatValue = -100.0F;
         boolean flag = false;

         for (Voice.Data data2 : datas2) {
            if (!builder.isEmpty()) {
               builder.append(", ");
            }

            builder.append(data2.text);
            floatValue = Math.max(floatValue, data2.floatValue);
            flag |= data2.flag;
         }

         String string = flag ? builder + " · " + method373("gui.voice.radioTag") : builder.toString();
         Island.method2873("voice", string, ((Voice.Data)datas2.get(0)).zenithColor, 75, floatValue);
      } else if (data == null) {
         Island.drop("voice");
      } else {
         boolean flag = type == Voice.Type.TALKING || type == Voice.Type.RADIO || type == Voice.Type.TESTING;
         Island.method2873("voice", data.text, data.zenithColor, flag ? 75 : 35, flag ? data.floatValue : Float.NaN);
      }
   }

   public static void method371(CustomDrawContext customDrawContext, float f, float f2, float floatValue, ZenithColor zenithColor) {
      float f3 = Mth.clamp((floatValue + 60.0F) / 45.0F, 0.0F, 1.0F);
      long now = System.currentTimeMillis();

      for (int i = 0; i < 3; i++) {
         float f4 = 0.55F + 0.45F * (float)Math.sin(now / 90.0 + i * 1.7);
         float height = 2.0F + 6.0F * f3 * (i == 1 ? 1.0F : 0.7F) * (f3 > 0.0F ? f4 : 0.0F);
         customDrawContext.drawRoundedRect(
            f + i * 4.0F, f2 - height / 2.0F, 2.5F, height, ProxyDotRadiusData.method8069(1.2F), zenithColor.method7294(f3 > 0.0F ? 1.0F : 0.35F)
         );
      }
   }

   private void method372(CustomDrawContext customDrawContext, ZenithStyle currentStyle, float tickDelta) {
      Cloud cloud2 = cloud();
      if (cloud2 != null && mc.level != null) {
         for (long uid : this.voiceState.method5364()) {
            if (uid != -1L && this.voiceState.method5365(uid)) {
               PartyPlayerInfo partyPlayerInfo = cloud2.state().method8040(uid);
               Player player = partyPlayerInfo == null ? null : this.method355(partyPlayerInfo.getText2());
               if (player != null) {
                  Vec3 vec3d = player.getPosition(tickDelta).add(0.0, player.getBbHeight() + 0.75, 0.0);
                  if (ProjectionUtils.method302(vec3d)) {
                     Vec3 vec = ProjectionUtils.method301(vec3d);
                     float width = 22.0F;
                     float height = 11.0F;
                     float floatValue = (float)vec.x - width / 2.0F;
                     float y = (float)vec.y - height;
                     ZenithColor zenithColor = this.voiceState.method5367(uid) ? new ZenithColor(-10443270) : currentStyle.getPrimaryColor().getZenithColor();
                     customDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), currentStyle.getHudBackground().getZenithColor());
                     method371(customDrawContext, floatValue + 6.0F, y + height / 2.0F, this.voiceState.method5366(uid), zenithColor);
                  }
               }
            }
         }
      }
   }

   private static String method373(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private JsonObject method374() {
      JsonObject json = new JsonObject();

      for (Setting setting : this.method332()) {
         setting.safe(json);
      }

      return json;
   }

   private void method375() {
      if (++this.intValue % 20 == 0) {
         JsonObject jsonElement = this.method374();
         String string = jsonElement.toString();
         if (!string.equals(this.text)) {
            this.text = string;
            String json = gson.toJson(jsonElement);
            Util.ioPool().execute(() -> this.write(json));
         }
      }
   }

   private void method376() {
      JsonObject jsonElement = this.method374();
      this.text = jsonElement.toString();
      this.write(gson.toJson(jsonElement));
   }

   private synchronized void write(String json) {
      try {
         this.file.getParentFile().mkdirs();
         Files.writeString(this.file.toPath(), json, StandardCharsets.UTF_8);
      } catch (Exception e) {
         logger.warn("Голос: настройки не записаны: {}", e.toString());
      }
   }

   private void load() {
      if (this.file.exists()) {
         try {
            JsonObject jsonObject = JsonParser.parseString(Files.readString(this.file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();

            for (Setting setting : this.method332()) {
               if (jsonObject.has(setting.getKey())) {
                  setting.load(jsonObject);
               }
            }
         } catch (Exception e) {
            logger.warn("Голос: voice.json не прочитан: {}", e.toString());
         }
      }
   }

   private static final class Data {
      private final String text;
      private final ZenithColor zenithColor;
      private final float floatValue;
      private final boolean flag;
      private final boolean flag2;

      public String text() {
         return this.text;
      }

      public ZenithColor zenithColor() {
         return this.zenithColor;
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

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.zenithColor, other.zenithColor) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, zenithColor, floatValue, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "zenithColor=" + this.zenithColor + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data(String text, ZenithColor zenithColor, float floatValue, boolean flag, boolean flag2) {
         this.text = text;
         this.zenithColor = zenithColor;
         this.floatValue = floatValue;
         this.flag = flag;
         this.flag2 = flag2;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
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
   }

   private static final class Entry {
      public float value;
      public float target;
      public long longValue;
   }

   public static enum Type {
      OFF,
      TESTING,
      MIC_ERROR,
      OUTPUT_ERROR,
      OFFLINE,
      UNSUPPORTED,
      DEAFENED,
      MUTED,
      RADIO,
      TALKING,
      PARTY,
      ROOM,
      WHISPER,
      NO_POSITION,
      READY;
   }
}
