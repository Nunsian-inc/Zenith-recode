package org.zenith.client.screens.nlgui.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.VoiceDeviceSetting;
import org.zenith.utility.misc.AudioDevices;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class VoiceSectionPanel extends SectionPanel {
   private static final float HERO_HEIGHT = 40.0F;
   private static final float PICKER_HEIGHT = 34.0F;
   private static final float TEST_HEIGHT = 50.0F;
   private static final float TILE_HEIGHT = 28.0F;
   private static final float FRIEND_HEIGHT = 18.0F;
   private static final float DEVICE_ROW_HEIGHT = 15.0F;
   private static final int METER_SEGMENTS = 32;
   private static final long REOPEN_GAP_MS = 400L;
   private static final long PING_EVERY_MS = 2000L;
   private static final int PICKER_NONE = -1;
   private static final int PICKER_INPUT = 0;
   private static final ZenithColor RADIO = new ZenithColor(-10443270);
   private static final ZenithColor LIVE = new ZenithColor(-495247);
   private final Animation enterWave = new Animation(560L, 1.0F, Easing.INSTANCE12);
   private final Animation asideWave = new Animation(560L, 1.0F, Easing.INSTANCE12);
   private final Animation listWave = new Animation(380L, 1.0F, Easing.INSTANCE12);
   private final Animation toggleKnob = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation heroOn = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private final Animation testOn = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private final Animation meterFill = new Animation(70L, 0.0F, Easing.INSTANCE12);
   private final TextSetting roomName = new TextSetting(
      "gui.voice.room.name", "gui.voice.room.name.desc", "", "gui.voice.room.name.empty", TextSetting.Entry.method2125(24)
   );
   private final TextSetting roomCode = new TextSetting(
      "gui.voice.room.code", "gui.voice.room.code.desc", "", "gui.voice.room.code.empty", TextSetting.Entry.method2125(8)
   );
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Animation> pick = new HashMap<>();
   private final Map<String, Integer> hoveredTile = new HashMap<>();
   private int openPicker = -1;
   private boolean advanced;
   private String lastSignature = "";
   private long lastTickAt;
   private long lastPingAt;
   private int order;
   private int asideOrder;
   private float breath;
   private float meterPeak;
   private long meterPeakAt;

   private static Voice voice() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getVoice();
   }

   @Override
   public String headerTitle() {
      return "Voice";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.MIC;
   }

   @Override
   public String sectionDescription() {
      return "gui.voice.desc";
   }

   @Override
   public void build() {
      this.order = 0;
      this.asideOrder = 0;
      Voice voice2 = voice();
      if (voice2 == null) {
         this.hint(t("gui.voice.missing"));
      } else {
         if (!voice2.isFlag()) {
            voice2.method335();
         }

         this.hero(voice2);
         if (!voice2.isEnabled()) {
            this.hint(t("gui.voice.off.hint"));
         }

         this.caption("gui.voice.caption.devices");
         this.pickers(voice2);
         if (this.openPicker != -1) {
            boolean flag = this.openPicker == 0;
            this.deviceList(flag ? voice2.input : voice2.output, flag ? voice2.method338() : voice2.method339());
         }

         this.setting(voice2.micGain);
         this.setting(voice2.volume);
         this.setting(voice2.denoise);
         if (voice2.denoise.isEnabled()) {
            this.setting(voice2.rnnoise);
         }

         this.micTest(voice2);
         this.caption("gui.voice.caption.talk");
         this.choice(
            "activation",
            List.of(
               new VoiceSectionPanel.Tile("gui.voice.activation.ptt", "gui.voice.activation.ptt.note"),
               new VoiceSectionPanel.Tile("gui.voice.activation.voice", "gui.voice.activation.voice.note")
            ),
            voice2.activation::getIndex,
            var1x -> select(voice2.activation, var1x)
         );
         if (voice2.activation.isSelected(0)) {
            this.setting(voice2.talkKey);
         } else {
            this.setting(voice2.sensitivity);
         }

         this.setting(voice2.radioKey);
         this.caption("gui.voice.caption.hear");
         this.choice(
            "range",
            List.of(
               new VoiceSectionPanel.Tile("gui.voice.range.whisper", "gui.voice.range.whisper.note"),
               new VoiceSectionPanel.Tile("gui.voice.range.normal", "gui.voice.range.normal.note"),
               new VoiceSectionPanel.Tile("gui.voice.range.loud", "gui.voice.range.loud.note")
            ),
            voice2.range::getIndex,
            var1x -> select(voice2.range, var1x)
         );
         this.setting(voice2.radioVolume);
         this.setting(voice2.acceptRadio);
         this.setting(voice2.muffle);
         this.channels(voice2);
         this.row("gui.voice.caption.more").icon(MenuGlyph.MODE).value(() -> t(this.advanced ? "gui.voice.more.hide" : "gui.voice.more.show")).onClick(() -> {
            this.advanced = !this.advanced;
            this.invalidate();
         }).description("gui.voice.more.desc").add();
         if (this.advanced) {
            this.setting(voice2.quality);
            this.setting(voice2.spatial);
            if (voice2.spatial.isEnabled()) {
               this.setting(voice2.hrtf);
            }

            this.setting(voice2.overlay);
            this.setting(voice2.heads);
            this.setting(voice2.muteKey);
            this.setting(voice2.deafenKey);
            this.action("gui.voice.refresh", MenuGlyph.RESET, () -> t("gui.voice.refresh.value"), () -> {
               voice2.method335();
               this.invalidate();
            }, "gui.voice.refresh.desc");
         }

         this.buildAside(voice2);
      }
   }

   private void channels(Voice voice) {
      Cloud cloud = Zenith.getInstance().getCloud();
      if (cloud != null) {
         PartyMember partyMember = cloud.getPartyMember();
         this.caption("gui.voice.caption.channels");
         if (!cloud.method7860()) {
            this.hint(t("gui.voice.channels.unsupported"));
         } else {
            this.choice(
               "channel",
               List.of(
                  new VoiceSectionPanel.Tile("gui.voice.channel.near", "gui.voice.channel.near.note"),
                  new VoiceSectionPanel.Tile("gui.voice.channel.party", "gui.voice.channel.party.note"),
                  new VoiceSectionPanel.Tile("gui.voice.channel.room", "gui.voice.channel.room.note")
               ),
               voice.defaultChannel::getIndex,
               var1x -> select(voice.defaultChannel, var1x)
            );
            this.setting(voice.partyKey);
            this.setting(voice.roomKey);
            this.setting(voice.whisperKey);
            this.setting(voice.channelVolume);
            PartyMember.Data data = partyMember.getData();
            if (data == null) {
               this.setting(this.roomName);
               this.action("gui.voice.room.create", MenuGlyph.ACTION, () -> t("gui.voice.room.create.value"), () -> {
                  partyMember.method1171(this.roomName.getValue() == null ? "" : this.roomName.getValue());
                  this.invalidate();
               }, "gui.voice.room.create.desc");
               this.setting(this.roomCode);
               this.action("gui.voice.room.join", MenuGlyph.ACTION, () -> t("gui.voice.room.join.value"), () -> {
                  String var2x = this.roomCode.getValue() == null ? "" : this.roomCode.getValue().strip();
                  if (var2x.length() == 5) {
                     partyMember.method1172(var2x);
                  }

                  this.invalidate();
               }, "gui.voice.room.join.desc");

               for (PartyMember.Data16 data16 : partyMember.method1145()) {
                  this.row(t("gui.voice.room.invite").replace("{n}", data16.getText3()).replace("{r}", data16.name()))
                     .icon(MenuGlyph.SOUND)
                     .value(() -> t("gui.voice.room.join.value"))
                     .onClick(() -> {
                        partyMember.method1172(data16.getText());
                        this.invalidate();
                     })
                     .onRightClick(() -> {
                        partyMember.method1173(data16);
                        this.invalidate();
                     })
                     .description("gui.voice.room.invite.desc")
                     .add();
               }
            } else {
               this.info("gui.voice.room.current", () -> data.name() + " · " + data.getText(), "gui.voice.room.current.desc");
               this.action("gui.voice.room.copy", MenuGlyph.COPY, () -> data.getText(), () -> {
                  if (mc.keyboardHandler != null) {
                     mc.keyboardHandler.setClipboard(data.getText());
                  }
               }, "gui.voice.room.copy.desc");

               for (PartyMember.Data3 data3 : data.getItems()) {
                  boolean flag = voice.getVoiceState().method5365(data3.uid());
                  this.info(
                     data3.uid() == data.getLongValue() ? "★ " + data3.username() : data3.username(),
                     () -> voice.getVoiceState().method5365(data3.uid())
                        ? t("gui.voice.room.speaking")
                        : (data3.uid() == cloud.getLongValue() ? t("gui.party.you") : ""),
                     flag ? "gui.voice.room.speaking" : "gui.voice.room.member.desc"
                  );
               }

               for (PartyPlayerInfo partyPlayerInfo : cloud.state().method8038()) {
                  boolean flag = false;

                  for (PartyMember.Data3 data3 : data.getItems()) {
                     if (data3.uid() == partyPlayerInfo.uid()) {
                        flag = true;
                        break;
                     }
                  }

                  if (!flag && partyPlayerInfo.method3289()) {
                     this.action(
                        t("gui.voice.room.inviteFriend").replace("{n}", Voice.method367(partyPlayerInfo)),
                        MenuGlyph.FRIENDS,
                        () -> t("gui.voice.room.inviteFriend.value"),
                        () -> partyMember.method1175(partyPlayerInfo.uid()),
                        "gui.voice.room.inviteFriend.desc"
                     );
                  }
               }

               this.action("gui.voice.room.leave", MenuGlyph.CLOSE, () -> t("gui.voice.room.leave.value"), () -> {
                  partyMember.method1174();
                  this.invalidate();
               }, "gui.voice.room.leave.desc");
            }

            long longValue = partyMember.getLongValue();
            if (longValue != 0L) {
               this.action(t("gui.voice.whisper.target").replace("{n}", voice.method348(longValue)), MenuGlyph.SOUND, () -> t("gui.voice.whisper.clear"), () -> {
                  partyMember.method1176(0L);
                  this.invalidate();
               }, "gui.voice.whisper.target.desc");
            } else {
               this.hint(t("gui.voice.whisper.hint"));
            }
         }
      }
   }

   private static void select(ModeSetting activation, int n) {
      activation.getValues().get(n).method2103();
   }

   private static float wave(Animation enterWave, int n) {
      float f = enterWave.method2808(1.0F);
      return Mth.clamp((f - n * 0.07F) / 0.5F, 0.0F, 1.0F);
   }

   private static void restart(Animation listWave) {
      listWave.reset(0.0F);
      listWave.method2810(1.0F);
   }

   private void hero(Voice voice) {
      int n = this.order++;
      this.custom(
         40.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.enterWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float floatValue = f + 8.0F;
               float f4 = f + f3 - 8.0F;
               float y = f2 + 3.0F;
               float height = 34.0F;
               boolean contains = new BoundsData(floatValue, y, f4 - floatValue, height).contains(lastClickX, lastClickY);
               boolean enabled = voice.isEnabled();
               float hover = this.heroOn.method2808(enabled ? 1.0F : 0.0F);
               plate(menuDrawContext, floatValue, y, f4 - floatValue, height, this.hover("hero", contains), 0.0F, previewAlpha, zenithStyle);
               menuDrawContext.drawRoundedRect(
                  floatValue,
                  y,
                  f4 - floatValue,
                  height,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.07F * hover)
               );
               if (contains) {
                  HoverInfo.request(t("gui.voice.enabled"), t("gui.voice.enabled.desc"));
               }

               Voice.Type type = voice.method357();
               boolean flag = type == Voice.Type.TALKING || type == Voice.Type.RADIO || type == Voice.Type.TESTING;
               float f5 = flag ? voice.getVoiceState().getFloatValue2() : -100.0F;
               this.breath = this.breath + (Mth.clamp((f5 + 60.0F) / 45.0F, 0.0F, 1.0F) - this.breath) * 0.25F;
               ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover);
               float f6 = floatValue + 8.0F;
               float f7 = y + (height - 12.0F) / 2.0F;
               if (this.breath > 0.01F) {
                  float width = 18.0F + this.breath * 5.0F;
                  float f8 = f6 + 6.0F;
                  float f9 = f7 + 6.0F;
                  menuDrawContext.drawRoundedRect(
                     f8 - width / 2.0F,
                     f9 - width / 2.0F,
                     width,
                     width,
                     ProxyDotRadiusData.method8069(width / 2.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.18F * this.breath)
                  );
               }

               bigGlyph(menuDrawContext, f6, f7, 1.0F + this.breath * 0.12F, zenithColor.method7294(previewAlpha));
               Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.6F);
               float floatValue2 = floatValue + 8.0F + 12.0F + 8.0F;
               float centeredTextY = y + (height - font.height() - 3.0F - font2.height()) / 2.0F;
               menuDrawContext.drawText(font, t("gui.voice.enabled"), floatValue2, centeredTextY, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               float width = 3.0F;
               float centeredTextY2 = centeredTextY + font.height() + 3.0F;
               float f8 = flag ? 0.55F + 0.45F * (float)Math.sin(System.currentTimeMillis() / 160.0) : 1.0F;
               menuDrawContext.drawRoundedRect(
                  floatValue2,
                  centeredTextY2 + font2.height() / 2.0F - width / 2.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  voice.method360().method7294(previewAlpha * f8)
               );
               String str = enabled ? hintLine(voice) : "";
               String inputName = str.isEmpty() ? voice.method359() : voice.method359() + " · " + str;
               float width2 = 20.0F;
               float f9 = f4 - 8.0F - width2 - 8.0F - (floatValue2 + width + 4.0F);
               menuDrawContext.drawText(font2, fitText(font2, inputName, f9), floatValue2 + width + 4.0F, centeredTextY2, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               this.drawToggle(menuDrawContext, f4 - 8.0F - width2, y + (height - 10.0F) / 2.0F, width2, enabled, previewAlpha, zenithStyle);
            }
         },
         () -> {
            voice.setEnabled(!voice.isEnabled());
            Zenith.getInstance().getClientSounds().method1562();
            this.invalidate();
         },
         null
      );
   }

   private static String hintLine(Voice voice) {
      if (voice.method357() != Voice.Type.READY) {
         return "";
      } else {
         return voice.activation.isSelected(0) ? t("gui.voice.hint.hold") + " " + Voice.method364(voice.talkKey) : t("gui.voice.hint.speak");
      }
   }

   private void drawToggle(MenuDrawContext menuDrawContext, float floatValue, float y, float width, boolean enabled, float previewAlpha, ZenithStyle zenithStyle) {
      float height = 10.0F;
      float hover = this.toggleKnob.method2808(enabled ? 1.0F : 0.0F);
      ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(0.35F).method7295(zenithStyle.getPrimaryColor().getZenithColor(), hover);
      menuDrawContext.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor.method7294(previewAlpha));
      float f = height - 3.0F;
      float floatValue2 = floatValue + 1.5F + (width - f - 3.0F) * hover;
      menuDrawContext.drawRoundedRect(floatValue2, y + 1.5F, f, f, ProxyDotRadiusData.method8069(f / 2.0F), new ZenithColor(255, 255, 255).method7294(previewAlpha));
   }

   private void pickers(Voice voice) {
      int n = this.order++;
      this.custom(
         34.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.enterWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float f4 = 4.0F;
               float f5 = f + 8.0F;
               float width = (f3 - 16.0F - f4) / 2.0F;
               int n2 = -1;

               for (int i = 0; i < 2; i++) {
                  boolean flag = i == 0;
                  float floatValue = f5 + i * (width + f4);
                  float y = f2 + 2.0F;
                  float height = 30.0F;
                  boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
                  if (contains) {
                     n2 = i;
                     HoverInfo.request(t(flag ? "gui.voice.input" : "gui.voice.output"), t(flag ? "gui.voice.input.desc" : "gui.voice.output.desc"));
                  }

                  float hover = this.hover("picker:" + i, contains);
                  float pick = this.pick("picker:" + i, this.openPicker == i);
                  plate(menuDrawContext, floatValue, y, width, height, hover, pick, previewAlpha, zenithStyle);
                  Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
                  Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
                  Font font3 = Fonts.NEW_REGULAR.getFont(4.2F);
                  float floatValue2 = floatValue + 6.0F;
                  ZenithColor zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha);
                  (flag ? MenuGlyph.MIC : MenuGlyph.SOUND).draw(menuDrawContext, floatValue2, y + 4.5F, zenithColor);
                  menuDrawContext.drawText(
                     font, HoverInfo.spaced(t(flag ? "gui.voice.input" : "gui.voice.output").toUpperCase(Locale.ROOT)), floatValue2 + 9.0F, y + 5.5F, zenithColor
                  );
                  VoiceDeviceSetting input = flag ? voice.input : voice.output;
                  String inputName = flag ? voice.inputName() : voice.method336();
                  if (inputName.isEmpty()) {
                     inputName = t("gui.voice.device.none");
                  }

                  float f6 = width - 12.0F - 10.0F;
                  menuDrawContext.drawText(
                     font2, fitText(font2, inputName, f6), floatValue2 + hover * 1.5F, y + 13.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
                  );
                  String str = t(input.method11().isEmpty() ? "gui.voice.device.auto" : "gui.voice.device.manual");
                  menuDrawContext.drawText(font3, fitText(font3, str, f6), floatValue2 + hover * 1.5F, y + 13.0F + font2.height() + 2.0F, zenithColor);
                  chevron(
                     menuDrawContext,
                     floatValue + width - 10.0F,
                     y + height / 2.0F,
                     pick,
                     zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(hover, pick)).method7294(previewAlpha)
                  );
               }

               this.hoveredTile.put("pickers", n2);
            }
         },
         () -> {
            int var2x = this.hoveredTile.getOrDefault("pickers", -1);
            if (var2x >= 0) {
               if (this.openPicker == var2x) {
                  this.openPicker = -1;
               } else {
                  voice.method335();
                  this.openPicker = var2x;
                  restart(this.listWave);
               }

               this.invalidate();
            }
         },
         null
      );
   }

   private void deviceList(VoiceDeviceSetting input, String str) {
      String display = t("gui.voice.device.default") + (str.isEmpty() ? "" : " · " + str);
      this.deviceRow(0, display, "", input, t("gui.voice.device.auto.desc"));
      var values = input.getValues();

      for (int i = 1; i < values.size(); i++) {
         String key = ((ModeSetting.Entry)values.get(i)).getKey();
         this.deviceRow(i, AudioDevices.display(key), key, input, "");
      }

      this.gap(GuiStyle.PADDING.intValue());
   }

   private void deviceRow(int n, String display, String key, VoiceDeviceSetting input, String description) {
      String str = "device:" + key;
      this.custom(
         15.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.listWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               float f4 = (1.0F - wave2) * -4.0F;
               float floatValue = f + 8.0F + 4.0F;
               float width = f3 - 16.0F - 4.0F;
               float y = f2 + 1.0F + f4;
               float height = 13.0F;
               boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
               boolean flag = key.equals(input.method11());
               float hover = this.hover(str, contains);
               float pick = this.pick(str, flag);
               menuDrawContext.drawRoundedRect(
                  floatValue,
                  y,
                  width,
                  height,
                  ProxyDotRadiusData.method8069(3.0F),
                  zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * Math.max(pick * 0.8F, hover * 0.45F))
               );
               if (contains && !description.isEmpty()) {
                  HoverInfo.request(display, description);
               }

               Font font = Fonts.NEW_MEDIUM.getFont(5.01F);
               float width2 = 4.0F;
               float f5 = y + height / 2.0F;
               menuDrawContext.drawRoundedRect(
                  floatValue + 5.0F,
                  f5 - width2 / 2.0F,
                  width2,
                  width2,
                  ProxyDotRadiusData.method8069(width2 / 2.0F),
                  zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.5F)
               );
               if (pick > 0.01F) {
                  float width3 = width2 * pick;
                  menuDrawContext.drawRoundedRect(
                     floatValue + 5.0F + (width2 - width3) / 2.0F,
                     f5 - width3 / 2.0F,
                     width3,
                     width3,
                     ProxyDotRadiusData.method8069(width3 / 2.0F),
                     zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
                  );
               }

               menuDrawContext.drawText(
                  font,
                  fitText(font, display, width - 18.0F),
                  floatValue + 13.0F + hover * 1.5F,
                  f5 - font.height() / 2.0F,
                  zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(pick, hover)).method7294(previewAlpha)
               );
            }
         },
         () -> {
            input.method10(key);
            this.openPicker = -1;
            this.invalidate();
         },
         null
      );
   }

   private void micTest(Voice voice) {
      int n = this.order++;
      this.custom(
         50.0F,
         (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.enterWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float floatValue = f + 8.0F;
               float f4 = f + f3 - 8.0F;
               float y = f2 + 3.0F;
               float height = 44.0F;
               boolean flag = voice.isFlag3();
               float hover = this.testOn.method2808(flag ? 1.0F : 0.0F);
               boolean contains = new BoundsData(floatValue, y, f4 - floatValue, height).contains(lastClickX, lastClickY);
               if (contains) {
                  HoverInfo.request(t("gui.voice.micTest"), t("gui.voice.micTest.desc"));
               }

               plate(menuDrawContext, floatValue, y, f4 - floatValue, height, this.hover("test", contains), 0.0F, previewAlpha, zenithStyle);
               Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.4F);
               Font font3 = Fonts.NEW_MEDIUM.getFont(5.01F);
               String text = t(flag ? "gui.voice.micTest.stop" : "gui.voice.micTest.start");
               float f5 = font3.width(text) + 16.0F;
               float f6 = 13.0F;
               float f7 = f4 - 8.0F - f5;
               float f8 = y + 7.0F;
               float hover2 = this.hover("test:button", contains);
               ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor().method7295(zenithStyle.getDisableActiveBg().getZenithColor(), hover);
               float f9 = hover2 * 1.0F;
               menuDrawContext.drawRoundedRect(
                  f7 - f9, f8 - f9 * 0.5F, f5 + f9 * 2.0F, f6 + f9, ProxyDotRadiusData.method8069(f6 / 2.0F), zenithColor.method7294(previewAlpha)
               );
               menuDrawContext.drawText(
                  font3,
                  text,
                  f7 + (f5 - font3.width(text)) / 2.0F,
                  f8 + (f6 - font3.height()) / 2.0F,
                  new ZenithColor(255, 255, 255).method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha)
               );
               float floatValue2 = floatValue + 8.0F;
               if (hover > 0.01F) {
                  float width = 3.0F * hover;
                  float f10 = 0.6F + 0.4F * (float)Math.sin(System.currentTimeMillis() / 180.0);
                  menuDrawContext.drawRoundedRect(
                     floatValue2,
                     y + 7.0F + font.height() / 2.0F - width / 2.0F,
                     width,
                     width,
                     ProxyDotRadiusData.method8069(width / 2.0F),
                     LIVE.method7294(previewAlpha * f10)
                  );
                  floatValue2 += 6.0F * hover;
               }

               float f10 = f7 - 6.0F - floatValue2;
               menuDrawContext.drawText(font, fitText(font, t("gui.voice.micTest"), f10), floatValue2, y + 7.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
               String inputName = t(flag ? "gui.voice.micTest.live" : "gui.voice.micTest.note");
               menuDrawContext.drawText(
                  font2, fitText(font2, inputName, f10), floatValue2, y + 7.0F + font.height() + 3.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
               this.drawMeter(menuDrawContext, voice, floatValue + 8.0F, y + height - 11.0F, f4 - 8.0F - (floatValue + 8.0F), previewAlpha, zenithStyle);
            }
         },
         () -> {
            voice.method333(!voice.isFlag3());
            this.invalidate();
         },
         null
      );
   }

   private void drawMeter(MenuDrawContext menuDrawContext, Voice voice, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      float f4 = voice.getVoiceState().getFloatValue2();
      float meterPeak = this.meterFill.method2808(Mth.clamp((f4 + 70.0F) / 60.0F, 0.0F, 1.0F));
      long now = System.currentTimeMillis();
      if (meterPeak >= this.meterPeak) {
         this.meterPeak = meterPeak;
         this.meterPeakAt = now;
      } else if (now - this.meterPeakAt > 500L) {
         this.meterPeak = Math.max(meterPeak, this.meterPeak - 0.012F);
      }

      boolean selected = voice.activation.isSelected(1);
      float f5 = Mth.clamp((voice.method341() + 70.0F) / 60.0F, 0.0F, 1.0F);
      float f6 = 1.5F;
      float width = (f3 - f6 * 31.0F) / 32.0F;
      float f7 = 5.0F;
      int n = this.meterPeak > 0.02F ? Math.min(31, (int)(this.meterPeak * 32.0F)) : -1;

      for (int i = 0; i < 32; i++) {
         float f8 = (i + 0.5F) / 32.0F;
         boolean flag = f8 <= meterPeak;
         ZenithColor zenithColor;
         if (i == n && !flag) {
            zenithColor = zenithStyle.getPrimaryColor().getZenithColor().method7294(0.55F);
         } else if (!flag) {
            zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(0.22F);
         } else if (selected && f8 < f5) {
            zenithColor = zenithStyle.getTextTertiary().getZenithColor().method7294(0.8F);
         } else {
            zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
         }

         float height = flag ? f7 : f7 - 1.0F;
         menuDrawContext.drawRoundedRect(
            f + i * (width + f6), f2 + (f7 - height) / 2.0F, width, height, ProxyDotRadiusData.method8069(1.0F), zenithColor.method7294(previewAlpha)
         );
      }

      if (selected) {
         float f8 = f + f3 * f5;
         menuDrawContext.drawRect(f8 - 0.5F, f2 - 2.0F, 1.0F, f7 + 4.0F, zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha));
      }
   }

   private void choice(String key, List<VoiceSectionPanel.Tile> tiles, IntSupplier intSupplier, IntConsumer intConsumer) {
      int n = this.order++;
      this.custom(
         28.0F,
         (var5x, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.enterWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 8.0F;
               float f4 = 4.0F;
               float f5 = f + 8.0F;
               float width = (f3 - 16.0F - f4 * (tiles.size() - 1)) / tiles.size();
               int n2 = -1;

               for (int i = 0; i < tiles.size(); i++) {
                  VoiceSectionPanel.Tile tile = (VoiceSectionPanel.Tile)tiles.get(i);
                  float floatValue = f5 + i * (width + f4);
                  float y = f2 + 2.0F;
                  float height = 24.0F;
                  boolean contains = new BoundsData(floatValue, y, width, height).contains(lastClickX, lastClickY);
                  if (contains) {
                     n2 = i;
                     HoverInfo.request(t(tile.title()), t(tile.note()));
                  }

                  String str = key + ":" + i;
                  float pick = this.pick(str, intSupplier.getAsInt() == i);
                  float hover = this.hover(str, contains);
                  plate(var5x, floatValue, y, width, height, hover, pick, previewAlpha, zenithStyle);
                  Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
                  Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
                  float f6 = width - 10.0F;
                  float f7 = hover * 1.5F;
                  var5x.drawText(
                     font,
                     fitText(font, t(tile.title()), f6),
                     floatValue + 5.0F + f7,
                     y + 4.0F,
                     zenithStyle.getTextSecondary()
                        .getZenithColor()
                        .method7295(zenithStyle.getTextEnable().getZenithColor(), Math.max(pick, hover * 0.6F))
                        .method7294(previewAlpha)
                  );
                  var5x.drawText(
                     font2,
                     fitText(font2, t(tile.note()), f6),
                     floatValue + 5.0F + f7,
                     y + 4.0F + font.height() + 2.0F,
                     zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
                  );
               }

               this.hoveredTile.put(key, n2);
            }
         },
         () -> {
            int var4x = this.hoveredTile.getOrDefault(key, -1);
            if (var4x >= 0 && var4x != intSupplier.getAsInt()) {
               intConsumer.accept(var4x);
               this.invalidate();
            }
         },
         null
      );
   }

   private void buildAside(Voice voice) {
      this.asideCaption("gui.voice.caption.now");
      this.asideInfo("gui.voice.aside.status", voice::method359, "gui.voice.aside.status.desc");
      this.asideInfo("gui.voice.aside.ping", () -> {
         int var1x = voice.method361();
         return var1x < 0 ? "—" : var1x + " " + t("gui.voice.ms");
      }, "gui.voice.aside.ping.desc");
      this.asideInfo("gui.voice.aside.latency", () -> {
         int var1x = voice.latencyMs();
         return var1x < 0 ? "—" : "≈ " + var1x + " " + t("gui.voice.ms");
      }, "gui.voice.aside.latency.desc");
      this.asideInfo("gui.voice.aside.range", () -> voice.method340() + " " + t("gui.voice.blocks"), "gui.voice.aside.range.desc");
      this.asideCaption("gui.voice.caption.devices");
      this.asideInfo("gui.voice.input", voice::inputName, "gui.voice.input.desc");
      this.asideInfo("gui.voice.output", voice::method336, "gui.voice.output.desc");
      this.asideCaption("gui.voice.caption.keys");
      this.asideInfo(
         "gui.voice.aside.key.talk",
         () -> voice.activation.isSelected(0) ? Voice.method364(voice.talkKey) : t("gui.voice.aside.key.byVoice"),
         "gui.voice.talkKey.desc"
      );
      this.asideInfo("gui.voice.aside.key.radio", () -> Voice.method364(voice.radioKey), "gui.voice.radioKey.desc");
      this.asideInfo("gui.voice.aside.key.mute", () -> Voice.method364(voice.muteKey), "gui.voice.muteKey.desc");
      this.asideInfo("gui.voice.aside.key.deafen", () -> Voice.method364(voice.deafenKey), "gui.voice.deafenKey.desc");
      this.asideCaption("gui.voice.caption.sound");
      this.asideInfo("gui.voice.quality", () -> t(voice.quality.get()) + " · " + voice.method363() + " " + t("gui.voice.kbps"), "gui.voice.quality.desc");
      this.asideInfo(
         "gui.voice.denoise",
         () -> !voice.denoise.isEnabled() ? onOff(false) : (voice.rnnoise.isEnabled() ? "RNNoise" : t("gui.voice.denoise.gate")),
         "gui.voice.denoise.desc"
      );
      this.asideInfo("gui.voice.muffle", () -> onOff(voice.muffle.isEnabled()), "gui.voice.muffle.desc");
      this.asideInfo("gui.voice.spatial", () -> onOff(voice.spatial.isEnabled()), "gui.voice.spatial.desc");
      List<PartyPlayerInfo> partyPlayerInfos = voice.method365();
      this.asideCaption(t("gui.voice.caption.friends") + " · " + partyPlayerInfos.size());

      for (PartyPlayerInfo partyPlayerInfo : partyPlayerInfos) {
         this.friendRow(voice, partyPlayerInfo);
      }

      if (partyPlayerInfos.isEmpty()) {
         this.asideHint(t("gui.voice.friends.empty"));
         this.asideCaption("gui.voice.caption.howto");
         this.step(1, t("gui.voice.howto.1"));
         this.step(2, t("gui.voice.howto.2"));
         this.step(3, t("gui.voice.howto.3"));
      } else {
         this.asideHint(t("gui.voice.friends.hint"));
      }
   }

   private static String onOff(boolean enabled) {
      return t(enabled ? "gui.value.enabled" : "gui.value.disabled");
   }

   private void friendRow(Voice voice, PartyPlayerInfo partyPlayerInfo) {
      int n = this.asideOrder++;
      String inputName = Voice.method367(partyPlayerInfo);
      this.asideCustom(
         18.0F,
         (customDrawContext, n2, n3, f, f2, f3, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.asideWave, n);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f += (1.0F - wave2) * 6.0F;
               boolean flag = voice.getVoiceState().method5365(partyPlayerInfo.uid());
               boolean flag2 = voice.getVoiceState().method5367(partyPlayerInfo.uid());
               ZenithColor zenithColor = flag2 ? RADIO : zenithStyle.getPrimaryColor().getZenithColor();
               float pick = this.pick("talk:" + partyPlayerInfo.uid(), flag);
               float f4 = f2 + 9.0F;
               float width = 3.0F + pick;
               customDrawContext.drawRoundedRect(
                  f + 8.0F + (4.0F - width) / 2.0F,
                  f4 - width / 2.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  zenithStyle.getTextTertiary().getZenithColor().method7295(zenithColor, pick).method7294(previewAlpha)
               );
               Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
               Font font2 = Fonts.NEW_REGULAR.getFont(4.2F);
               String text = voice.method366(partyPlayerInfo);
               float floatValue = f + 8.0F + 8.0F;
               float f5 = f3 - 16.0F - 8.0F - font2.width(text) - 18.0F;
               customDrawContext.drawText(
                  font,
                  fitText(font, inputName, f5),
                  floatValue,
                  f4 - font.height() / 2.0F,
                  zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), 0.5F + 0.5F * pick).method7294(previewAlpha)
               );
               float floatValue2 = f + f3 - 8.0F - font2.width(text);
               if (pick > 0.01F) {
                  Voice.method371(customDrawContext, floatValue2 - 14.0F, f4, voice.getVoiceState().method5366(partyPlayerInfo.uid()), zenithColor.method7294(previewAlpha * pick));
               }

               customDrawContext.drawText(font2, text, floatValue2, f4 - font2.height() / 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            }
         }
      );
   }

   private void step(int n, String description) {
      int n2 = this.asideOrder++;
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      List<String> strings = wrapText(description, font, this.asideWidth() - 16.0F - 11.0F, 3);
      float f = font.height() + 2.0F;
      float hintsHeight = Math.max(11.0F, strings.size() * f) + 4.0F;
      this.asideCustom(
         hintsHeight,
         (var6x, var7x, n3, f2, f3, f4, previewAlpha, zenithStyle) -> {
            float wave2 = wave(this.asideWave, n2);
            if (!(wave2 <= 0.002F)) {
               previewAlpha *= wave2;
               f2 += (1.0F - wave2) * 6.0F;
               float width = 8.0F;
               var6x.drawRoundedRect(
                  f2 + 8.0F,
                  f3 + 1.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * 0.85F)
               );
               Font font2 = Fonts.NEW_MEDIUM.getFont(4.4F);
               String text = String.valueOf(n);
               var6x.drawText(
                  font2,
                  text,
                  f2 + 8.0F + (width - font2.width(text)) / 2.0F,
                  f3 + 1.0F + (width - font2.height()) / 2.0F,
                  new ZenithColor(255, 255, 255).method7294(previewAlpha)
               );
               float centeredTextY = f3 + 1.0F + (width - font.height()) / 2.0F;

               for (String str : strings) {
                  var6x.drawText(font, str, f2 + 8.0F + width + 4.0F, centeredTextY, zenithStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
                  centeredTextY += f;
               }
            }
         }
      );
   }

   private static void plate(MenuDrawContext menuDrawContext, float floatValue, float y, float width, float height, float hover, float pick, float previewAlpha, ZenithStyle zenithStyle) {
      menuDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         height,
         ProxyDotRadiusData.method8069(4.0F),
         zenithStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * (0.45F + 0.25F * hover + 0.3F * pick))
      );
      if (pick > 0.002F) {
         float f = (width - 8.0F) * pick;
         menuDrawContext.drawRoundedRect(
            floatValue + (width - f) / 2.0F,
            y + height - 2.0F,
            f,
            1.5F,
            ProxyDotRadiusData.method8069(0.75F),
            zenithStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * pick)
         );
      }
   }

   private static void bigGlyph(MenuDrawContext menuDrawContext, float f, float f2, float f3, ZenithColor zenithColor) {
      float f4 = 12.0F;
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(f + f4 / 2.0F, f2 + f4 / 2.0F);
      menuDrawContext.pose().scale(2.0F * f3, 2.0F * f3);
      MenuGlyph.MIC.draw(menuDrawContext, -3.0F, -3.0F, zenithColor);
      menuDrawContext.popMatrix();
   }

   private static void chevron(MenuDrawContext menuDrawContext, float f, float y, float pick, ZenithColor zenithColor) {
      float height = 3.2F;
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(f + 2.5F, y);
      menuDrawContext.pose().rotate((float)(Math.PI * pick));

      for (byte i = -1; i <= 1; i += 2) {
         menuDrawContext.pushMatrix();
         menuDrawContext.pose().rotate((float)Math.toRadians(45 * i));
         menuDrawContext.drawRect(-0.5F, -height + 0.6F, 1.0F, height, zenithColor);
         menuDrawContext.popMatrix();
      }

      menuDrawContext.popMatrix();
   }

   private float hover(String key, boolean contains) {
      return this.hover.computeIfAbsent(key, str -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(contains ? 1.0F : 0.0F);
   }

   private float pick(String key, boolean flag) {
      return this.pick.computeIfAbsent(key, str -> new Animation(180L, 0.0F, Easing.INSTANCE12)).method2808(flag ? 1.0F : 0.0F);
   }

   private void hint(String description) {
      Font font = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> strings = wrapText(description, font, this.mainWidth() - 16.0F, 4);
      float f = font.height() + 2.0F;
      int n = this.order++;
      this.custom(strings.size() * f + GuiStyle.PADDING.intValue(), (var5x, n2, n3, f2, f3, f4, f5, zenithStyle) -> {
         float wave2 = wave(this.enterWave, n);
         float centeredTextY = f3 + 2.0F;

         for (String text : strings) {
            var5x.drawText(font, text, f2 + 8.0F + (1.0F - wave2) * 8.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(f5 * wave2));
            centeredTextY += f;
         }
      });
   }

   private void asideHint(String description) {
      Font font = Fonts.NEW_REGULAR.getFont(4.4F);
      List<String> strings = wrapText(description, font, this.asideWidth() - 16.0F, 5);
      float f = font.height() + 2.0F;
      int n = this.asideOrder++;
      this.asideCustom(strings.size() * f + GuiStyle.PADDING.intValue(), (var5x, n2, n3, f2, f3, f4, f5, zenithStyle) -> {
         float wave2 = wave(this.asideWave, n);
         float centeredTextY = f3 + 2.0F;

         for (String text : strings) {
            var5x.drawText(font, text, f2 + 8.0F + (1.0F - wave2) * 6.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(f5 * wave2));
            centeredTextY += f;
         }
      });
   }

   private static String fitText(Font font, String inputName, float f) {
      if (f <= 0.0F) {
         return "";
      } else if (font.width(inputName) <= f) {
         return inputName;
      } else {
         String str = "…";
         int length = inputName.length();

         while (length > 0 && font.width(inputName.substring(0, length) + str) > f) {
            length--;
         }

         return length == 0 ? "" : inputName.substring(0, length) + str;
      }
   }

   @Override
   public void tick() {
      Voice voice2 = voice();
      if (voice2 != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastTickAt > 400L) {
            restart(this.enterWave);
            restart(this.asideWave);
         }

         this.lastTickAt = now;
         if (now - this.lastPingAt > 2000L) {
            this.lastPingAt = now;
            voice2.method362();
         }

         if (voice2.isFlag3()) {
            voice2.method334();
         }

         StringBuilder builder = new StringBuilder()
            .append(voice2.isEnabled())
            .append(voice2.activation.getIndex())
            .append(voice2.spatial.isEnabled())
            .append(voice2.isFlag3())
            .append(voice2.input.getValues().size())
            .append(voice2.output.getValues().size());

         for (PartyPlayerInfo partyPlayerInfo : voice2.method365()) {
            builder.append(';').append(partyPlayerInfo.uid());
         }

         Cloud cloud = Zenith.getInstance().getCloud();
         if (cloud != null) {
            PartyMember.Data data = cloud.getPartyMember().getData();
            builder.append('|')
               .append(cloud.method7860())
               .append(voice2.denoise.isEnabled())
               .append(data == null ? "-" : data.getText() + data.getItems().size())
               .append(cloud.getPartyMember().getLongValue())
               .append(cloud.getPartyMember().method1145().size());
         }

         String string = builder.toString();
         if (!string.equals(this.lastSignature)) {
            this.lastSignature = string;
            this.invalidate();
         }
      }
   }

   @Override
   public void close() {
      Voice voice2 = voice();
      if (voice2 != null) {
         voice2.method333(false);
      }

      this.openPicker = -1;
      super.close();
   }

   private static final class Tile {
      private final String title;
      private final String note;

      public String title() {
         return this.title;
      }

      public String note() {
         return this.note;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Tile other)) return false;
         return java.util.Objects.equals(this.title, other.title) && java.util.Objects.equals(this.note, other.note);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(title, note);
      }

      @Override
      public String toString() {
         return "Tile[" + "title=" + this.title + ", " + "note=" + this.note + "]";
      }

      private Tile(String title, String note) {
         this.title = title;
         this.note = note;
      }
   }
}
