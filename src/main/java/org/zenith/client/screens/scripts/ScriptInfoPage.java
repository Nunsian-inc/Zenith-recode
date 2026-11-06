package org.zenith.client.screens.scripts;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.zenith.utility.misc.FileDialogs;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.panel.ConfigInfoPage;
import org.zenith.client.screens.nlgui.panel.SectionPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptInfoPage {
   private static final int[] PALETTE = new int[]{-7686913, -4809473, 36785, 40811, 53606, -8462175, -10562336, -2039584};
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private final Storage host;
   private final BiConsumer<String, Boolean> toast;
   private final Consumer<Script> reopen;
   private Script script;
   private final FormField name = new FormField(60, false);
   private final FormField author = new FormField(40, false);
   private final FormField link = new FormField(120, false);
   private final FormField tags = new FormField(80, false);
   private final FormField description = new FormField(120, true);
   private final FormField note = new FormField(120, false);
   private final FormField folder = new FormField(48, false);
   private final List<FormField> fields = List.of(this.name, this.author, this.link, this.tags, this.description, this.note, this.folder);
   private int color = -1;
   private int savedColor = -1;
   private final Map<String, BoundsData> buttons = new LinkedHashMap<>();
   private final Map<String, Animation> hover = new HashMap<>();
   private final List<Object[]> swatches = new ArrayList<>();
   private BoundsData avatarRect;
   private BoundsData bounds;
   private final Smooth scroller = new Smooth(16.0F);
   private final Map<String, Long> pressed = new HashMap<>();
   private String armed = "";
   private long armedAt;
   private final Animation appear = new Animation(320L, 0.0F, Easing.INSTANCE15);
   private Runnable onClose = () -> {};
   private int marketPrice;
   private volatile boolean marketBusy;
   private Script changesScript;
   private int changesLimit;
   private long changesAt;
   private List<String> changes = List.of();

   public void setOnClose(Runnable onClose) {
      this.onClose = onClose;
   }

   public Script script() {
      return this.script;
   }

   public void renderCompact(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float width, float height, float f, ZenithStyle currentStyle) {
      if (this.script != null) {
         float previewAlpha = f * this.appear.animateTo(1.0F);
         floatValue += (1.0F - this.appear.getValue()) * 10.0F;
         this.bounds = new BoundsData(floatValue, y, width, height);
         this.buttons.clear();
         this.swatches.clear();
         if (!this.armed.isEmpty() && System.currentTimeMillis() - this.armedAt > 3000L) {
            this.armed = "";
         }

         float update = this.scroller.update();
         float f2 = 8.0F;
         float floatValue2 = floatValue + f2;
         float shownWidth = width - f2 * 2.0F;
         Font font = Fonts.NEW_REGULAR.getFont(4.3F);
         BoundsData boundsData = new BoundsData(floatValue2 - 2.0F, y + 1.0F, 12.0F, 12.0F);
         boolean contains = boundsData.contains(lastClickX, lastClickY);
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.2F);
         menuDrawContext.drawText(
            font2,
            "‹",
            boundsData.getFloatValue() + 3.0F,
            boundsData.getFloatValue2() + (12.0F - font2.height()) / 2.0F,
            (contains ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
         );
         this.buttons.put("close", boundsData);
         Font font3 = Fonts.NEW_MEDIUM.getFont(4.2F);
         menuDrawContext.drawText(
            font3,
            t("gui.scripts.info.settings"),
            floatValue2 + 12.0F,
            y + 1.0F + (12.0F - font3.height()) / 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
         if (this.isDirty()) {
            String text = t("gui.scripts.ws.save");
            float width2 = font3.width(text) + 12.0F;
            BoundsData boundsData2 = new BoundsData(floatValue + width - f2 - width2, y, width2, 13.0F);
            boolean flag = boundsData2.contains(lastClickX, lastClickY);
            menuDrawContext.drawRoundedRect(
               boundsData2.getFloatValue(),
               boundsData2.getFloatValue2(),
               boundsData2.width(),
               boundsData2.height(),
               ProxyDotRadiusData.method8069(6.5F),
               currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * (flag ? 1.0F : 0.8F))
            );
            menuDrawContext.drawText(
               font3, text, boundsData2.getFloatValue() + 6.0F, boundsData2.getFloatValue2() + (13.0F - font3.height()) / 2.0F, ZenithColor.INSTANCE.method7294(previewAlpha)
            );
            this.buttons.put("save", boundsData2);
         }

         float y2 = y + 18.0F;
         MsdfRenderer.flushBatch();
         menuDrawContext.enableScissor(floatValue, y2, floatValue + width, y + height);
         float centeredTextY = y2 + update;
         card(menuDrawContext, currentStyle, floatValue2, centeredTextY, shownWidth, 44.0F, 0.0F, previewAlpha);
         float width2 = 32.0F;
         this.avatarRect = new BoundsData(floatValue2 + 6.0F, centeredTextY + 6.0F, width2, width2);
         boolean flag = this.avatarRect.contains(lastClickX, lastClickY);
         float hot = this.hot("avatar", flag);
         drawAvatar(menuDrawContext, currentStyle, this.script, this.avatarRect.getFloatValue(), this.avatarRect.getFloatValue2(), width2, previewAlpha, this.color);
         if (hot > 0.01F) {
            menuDrawContext.drawRoundedRect(
               this.avatarRect.getFloatValue(),
               this.avatarRect.getFloatValue2(),
               width2,
               width2,
               ProxyDotRadiusData.method8069(width2 * 0.28F),
               new ZenithColor(0, 0, 0, 150).method7294(previewAlpha * hot)
            );
            Font font4 = Fonts.NEW_MEDIUM.getFont(4.0F);
            String text = t("gui.scripts.info.avatarChange");
            menuDrawContext.drawText(
               font4,
               text,
               this.avatarRect.getFloatValue() + (width2 - font4.width(text)) / 2.0F,
               this.avatarRect.getFloatValue2() + (width2 - font4.height()) / 2.0F,
               ZenithColor.INSTANCE.method7294(previewAlpha * hot)
            );
         }

         Font font4 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         float floatValue3 = this.avatarRect.getFloatValue() + width2 + 6.0F;
         float width3 = floatValue2 + shownWidth - floatValue3 - 4.0F;
         menuDrawContext.drawText(
            font4,
            font4.clip(this.name.get().isEmpty() ? this.script.getId() : this.name.get(), width3),
            floatValue3,
            centeredTextY + 9.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
         );
         String name = (this.script.getVersion().isBlank() ? "" : "v" + this.script.getVersion() + "  ·  ")
            + (this.author.get().isEmpty() ? this.script.getId() : this.author.get());
         menuDrawContext.drawText(font, font.clip(name, width3), floatValue3, centeredTextY + 11.0F + font4.height(), currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
         centeredTextY += 50.0F;
         float shownWidth2 = (shownWidth - 4.0F) / 2.0F;
         this.button(menuDrawContext, currentStyle, "avatar.upload", t("gui.scripts.info.upload"), MenuGlyph.ACTION, floatValue2, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 1);
         this.button(menuDrawContext, currentStyle, "avatar.remove", t("gui.scripts.info.removeAvatar"), null, floatValue2 + shownWidth2 + 4.0F, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 0);
         centeredTextY += 20.0F;
         float width4 = (shownWidth - 20.0F) / 9.0F;

         for (int i = 0; i <= PALETTE.length; i++) {
            int intValue = i == PALETTE.length ? -1 : PALETTE[i];
            float floatValue4 = floatValue2 + i * (width4 + 2.5F);
            ZenithColor zenithColor = intValue == -1 ? currentStyle.getPrimaryColor().getZenithColor() : new ZenithColor(intValue);
            menuDrawContext.drawRoundedRect(
               floatValue4, centeredTextY, width4, width4, ProxyDotRadiusData.method8069(width4 / 2.0F), zenithColor.method7294(previewAlpha * (intValue == -1 ? 0.35F : 0.9F))
            );
            float hot2 = this.hot("ring:" + intValue, intValue == this.color);
            if (hot2 > 0.01F) {
               float f3 = 1.2F + (1.0F - hot2) * 2.5F;
               menuDrawContext.drawRoundedBorder(
                  floatValue4 - f3,
                  centeredTextY - f3,
                  width4 + f3 * 2.0F,
                  width4 + f3 * 2.0F,
                  0.2F,
                  ProxyDotRadiusData.method8069((width4 + f3 * 2.0F) / 2.0F),
                  currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha * hot2)
               );
            }

            float press = Smooth.press(this.pressed.getOrDefault("swatch:" + intValue, 0L), 300L);
            if (press > 0.01F) {
               menuDrawContext.drawRoundedRect(
                  floatValue4 - press * 2.0F,
                  centeredTextY - press * 2.0F,
                  width4 + press * 4.0F,
                  width4 + press * 4.0F,
                  ProxyDotRadiusData.method8069(width4 / 2.0F + press * 2.0F),
                  zenithColor.method7292(0.35F * press).method7294(previewAlpha)
               );
            }

            this.swatches.add(new Object[]{new BoundsData(floatValue4, centeredTextY, width4, width4), intValue});
         }

         centeredTextY += width4 + 9.0F;
         centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.name", this.name, "gui.scripts.info.name.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
         centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.author", this.author, "gui.scripts.info.author.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
         centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.description", this.description, "gui.scripts.info.description.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
         centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.tags", this.tags, "gui.scripts.info.tags.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
         centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.link", this.link, "gui.scripts.info.link.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
         caption(
            menuDrawContext,
            currentStyle,
            t("gui.scripts.info.caption.version") + "  ·  v" + (this.script.getVersion().isBlank() ? "-" : this.script.getVersion()),
            floatValue2,
            centeredTextY,
            previewAlpha
         );
         centeredTextY += 9.0F;
         float shownWidth3 = (shownWidth - 6.0F) / 3.0F;
         this.button(menuDrawContext, currentStyle, "bump.2", t("gui.scripts.info.bumpPatch"), null, floatValue2, centeredTextY, shownWidth3, lastClickX, lastClickY, previewAlpha, 1);
         this.button(menuDrawContext, currentStyle, "bump.1", t("gui.scripts.info.bumpMinor"), null, floatValue2 + shownWidth3 + 3.0F, centeredTextY, shownWidth3, lastClickX, lastClickY, previewAlpha, 1);
         this.button(menuDrawContext, currentStyle, "bump.0", t("gui.scripts.info.bumpMajor"), null, floatValue2 + (shownWidth3 + 3.0F) * 2.0F, centeredTextY, shownWidth3, lastClickX, lastClickY, previewAlpha, 1);
         centeredTextY += 20.0F;
         centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.note", this.note, "gui.scripts.info.note.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
         List<String> changes = this.changes(6);
         if (!changes.isEmpty()) {
            float height2 = changes.size() * (font.height() + 3.0F) + 8.0F;
            card(menuDrawContext, currentStyle, floatValue2, centeredTextY, shownWidth, height2, 0.0F, previewAlpha);
            float centeredTextY2 = centeredTextY + 5.0F;

            for (String change : changes) {
               boolean startsWith = change.startsWith("## ");
               menuDrawContext.drawText(
                  font,
                  font.clip(startsWith ? change.substring(3) : "  " + change.substring(2), shownWidth - 10.0F),
                  floatValue2 + 5.0F,
                  centeredTextY2,
                  (startsWith ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
               );
               centeredTextY2 += font.height() + 3.0F;
            }

            centeredTextY += height2 + 8.0F;
         }

         centeredTextY += this.marketSection(menuDrawContext, currentStyle, floatValue2, centeredTextY, shownWidth, lastClickX, lastClickY, previewAlpha);
         caption(menuDrawContext, currentStyle, t("gui.scripts.info.caption.manage"), floatValue2, centeredTextY, previewAlpha);
         centeredTextY += 9.0F;
         this.button(menuDrawContext, currentStyle, "duplicate", t("gui.scripts.info.duplicate"), MenuGlyph.COPY, floatValue2, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 0);
         this.button(menuDrawContext, currentStyle, "export", t("gui.scripts.info.export"), MenuGlyph.STORAGE, floatValue2 + shownWidth2 + 4.0F, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 0);
         centeredTextY += 20.0F;
         this.button(menuDrawContext, currentStyle, "resetSettings", t("gui.scripts.info.resetSettings"), MenuGlyph.RESET, floatValue2, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 0);
         this.button(menuDrawContext, currentStyle, "resetStorage", t("gui.scripts.info.resetStorage"), MenuGlyph.RESET, floatValue2 + shownWidth2 + 4.0F, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 0);
         centeredTextY += 20.0F;
         this.button(menuDrawContext, currentStyle, "folder", t("gui.scripts.openFolder"), MenuGlyph.STORAGE, floatValue2, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 0);
         this.button(menuDrawContext, currentStyle, "delete", t("gui.scripts.info.delete"), MenuGlyph.CLOSE, floatValue2 + shownWidth2 + 4.0F, centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, 2);
         centeredTextY += 22.0F;
         if (this.script.getPath() != null) {
            centeredTextY += this.field(menuDrawContext, currentStyle, "gui.scripts.info.folderName", this.folder, "gui.scripts.info.folderName.hint", floatValue2, centeredTextY, shownWidth, previewAlpha);
            this.button(menuDrawContext, currentStyle, "rename", t("gui.scripts.info.rename"), null, floatValue2, centeredTextY - 4.0F, shownWidth, lastClickX, lastClickY, previewAlpha, 1);
            centeredTextY += 18.0F;
         }

         MsdfRenderer.flushBatch();
         menuDrawContext.disableScissor();
         float f3 = centeredTextY - centeredTextY + 6.0F;
         float f4 = y + height - y2;
         this.scroller.clamp(Math.min(0.0F, f4 - f3), 0.0F);
         if (f3 > f4 + 1.0F) {
            float height2 = Math.max(14.0F, f4 * f4 / f3);
            float y3 = y2 + -update / (f3 - f4) * (f4 - height2);
            menuDrawContext.drawRoundedRect(
               floatValue + width - 3.0F, y3, 1.5F, height2, ProxyDotRadiusData.method8069(0.75F), currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.5F)
            );
         }

         if (flag) {
            HoverInfo.request(t("gui.scripts.info.avatar"), t("gui.scripts.info.avatar.desc"));
         }
      }
   }

   private float marketSection(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float floatValue, float centeredTextY, float shownWidth, int lastClickX, int lastClickY, float previewAlpha) {
      caption(menuDrawContext, currentStyle, t("gui.market.caption.publish"), floatValue, centeredTextY, previewAlpha);
      centeredTextY += 9.0F;
      Font font = Fonts.NEW_REGULAR.getFont(4.3F);
      OriginUtil.Data2 data2 = OriginUtil.method6921(this.script);
      if (data2 != null && !data2.mine()) {
         StoreApi.Data5 data5 = StoreApi.method1960(data2.getUUID());
         String description = t(data5.isFlag() ? "gui.market.script.sealedForeign" : "gui.market.script.foreign");

         for (String text : SectionPanel.wrapText(description, font, shownWidth, 3)) {
            menuDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            centeredTextY += font.height() + 2.0F;
         }

         return centeredTextY - centeredTextY + 8.0F;
      } else {
         StoreApi.Data5 data5 = data2 == null ? StoreApi.Data5.INSTANCE : StoreApi.method1960(data2.getUUID());
         centeredTextY += ConfigInfoPage.marketStatus(menuDrawContext, currentStyle, data5, floatValue, centeredTextY, shownWidth, previewAlpha);
         if (!data5.pending() && !data5.method2007()) {
            int[] ints = ConfigInfoPage.PRICES;
            float f = 3.0F;
            float shownWidth2 = (shownWidth - f * (ints.length - 1)) / ints.length;

            for (int i = 0; i < ints.length; i++) {
               String str = ints[i] == 0 ? t("gui.market.price.free") : ints[i] + " ₽";
               this.button(
                  menuDrawContext, currentStyle, "mprice:" + i, str, null, floatValue + i * (shownWidth2 + f), centeredTextY, shownWidth2, lastClickX, lastClickY, previewAlpha, i == this.marketPrice ? 1 : 0
               );
            }

            centeredTextY += 20.0F;
            this.button(
               menuDrawContext,
               currentStyle,
               "market.submit",
               this.marketBusy ? t("gui.scripts.market.busy") : t("gui.market.publish.review"),
               MenuGlyph.STORAGE,
               floatValue,
               centeredTextY,
               shownWidth,
               lastClickX,
               lastClickY,
               previewAlpha,
               1
            );
            centeredTextY += 20.0F;

            for (String text : SectionPanel.wrapText(t("gui.market.script.hint"), font, shownWidth, 3)) {
               menuDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
               centeredTextY += font.height() + 2.0F;
            }
         } else {
            this.button(
               menuDrawContext,
               currentStyle,
               "market.withdraw",
               this.marketBusy ? t("gui.scripts.market.busy") : t("gui.market.withdraw"),
               MenuGlyph.CLOSE,
               floatValue,
               centeredTextY,
               shownWidth,
               lastClickX,
               lastClickY,
               previewAlpha,
               0
            );
            centeredTextY += 20.0F;
         }

         return centeredTextY - centeredTextY + 8.0F;
      }
   }

   private void submitToMarket() {
      StoreApi storeApi = Zenith.getInstance().getStoreApi();
      if (storeApi != null && !this.marketBusy && this.script != null) {
         if (this.script.getPath() == null) {
            this.toast.accept(t("gui.market.script.folderOnly"), true);
         } else {
            Script script = this.script;
            int n = ConfigInfoPage.PRICES[Math.max(0, Math.min(ConfigInfoPage.PRICES.length - 1, this.marketPrice))];
            OriginUtil.Data2 data2 = OriginUtil.method6921(script);
            this.marketBusy = true;
            var thenApply = data2 != null && data2.mine() ? storeApi.method1968("script").thenApply(var1x -> {
               for (StoreApi.Data2 var3x : var1x) {
                  if (var3x.getUUID().equals(data2.getUUID())) {
                     boolean var4x = StoreApi.method1960(var3x.getUUID()).method2007();
                     return "PUBLIC".equals(var3x.getText7()) && !var4x ? "PRIVATE" : var3x.getText7();
                  }
               }

               return "PRIVATE";
            }) : CompletableFuture.completedFuture("PRIVATE");
            thenApply.<StoreApi.Data2>thenCompose(var3x -> OriginUtil.method6933(storeApi, this.host, script, var3x))
               .thenCompose(var2x -> storeApi.method1961(var2x.getUUID(), n))
               .whenComplete((var1x, var2x) -> Minecraft.getInstance().execute(() -> {
                  this.marketBusy = false;
                  if (var2x != null) {
                     this.toast.accept(StoreApi.reason(var2x), true);
                  } else {
                     this.toast.accept(t("gui.market.report.review"), false);
                  }
               }));
         }
      }
   }

   private void withdrawFromMarket() {
      StoreApi storeApi = Zenith.getInstance().getStoreApi();
      OriginUtil.Data2 data2 = this.script == null ? null : OriginUtil.method6921(this.script);
      if (storeApi != null && data2 != null && !this.marketBusy) {
         this.marketBusy = true;
         storeApi.method1962(data2.getUUID()).whenComplete((var1x, var2x) -> Minecraft.getInstance().execute(() -> {
            this.marketBusy = false;
            this.toast.accept(var2x != null ? StoreApi.reason(var2x) : t("gui.market.report.withdrawn"), var2x != null);
         }));
      }
   }

   public ScriptInfoPage(Storage host, BiConsumer<String, Boolean> toast, Consumer<Script> reopen) {
      this.host = host;
      this.toast = toast;
      this.reopen = reopen;
   }

   private List<String> changes(int changesLimit) {
      long now = System.currentTimeMillis();
      if (this.changesScript != this.script || this.changesLimit != changesLimit || now - this.changesAt >= 1000L) {
         this.changes = this.host.method1352(this.script, changesLimit);
         this.changesScript = this.script;
         this.changesLimit = changesLimit;
         this.changesAt = now;
      }

      return this.changes;
   }

   public void open(Script script) {
      this.script = script;
      this.changesScript = null;
      this.name.set(script.method1403().equals(script.getId()) && script.getVersion().isBlank() ? script.method1403() : script.method1403());
      this.author.set(script.getText2());
      this.link.set(script.getText4());
      this.tags.set(script.getText3());
      this.description.set(script.getDescription());
      this.note.set("");
      this.folder.set(script.getId());
      this.color = script.getColor();
      this.savedColor = this.color;
      this.scroller.reset(0.0F);
      this.armed = "";
      this.appear.reset(0.0F);
      this.appear.setTarget(1.0F);

      for (FormField field : this.fields) {
         field.setFocused(false);
      }

      this.marketPrice = 0;
      OriginUtil.Data2 data2 = OriginUtil.method6921(script);
      StoreApi storeApi = Zenith.getInstance().getStoreApi();
      if (data2 != null && storeApi != null) {
         storeApi.method1968("script").exceptionally(throwable -> List.of());
         storeApi.method1969("LIBRARY", "script").exceptionally(throwable -> List.of());
      }
   }

   public boolean isDirty() {
      return this.script != null
         && (
            this.name.changed()
               || this.author.changed()
               || this.link.changed()
               || this.tags.changed()
               || this.description.changed()
               || this.color != this.savedColor
         );
   }

   public boolean isTyping() {
      for (FormField field : this.fields) {
         if (field.isFocused()) {
            return true;
         }
      }

      return false;
   }

   public void apply() {
      if (this.script != null) {
         LinkedHashMap map = new LinkedHashMap();
         map.put("name", this.name.get());
         map.put("author", this.author.get());
         map.put("description", this.description.get());
         map.put("tags", this.tags.get());
         map.put("link", this.link.get());
         map.put("color", this.color == -1 ? "" : String.format("#%06X", this.color & 16777215));

         try {
            this.host.method1350(this.script, map);

            for (FormField formField : List.of(this.name, this.author, this.link, this.tags, this.description)) {
               formField.commit();
            }

            this.savedColor = this.color;
            this.toast.accept(t("gui.scripts.info.saved"), false);
         } catch (Exception e) {
            this.toast.accept(t("gui.scripts.toast.saveFailed") + " " + e.getMessage(), true);
         }
      }
   }

   private static String t(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   private float hot(String key, boolean contains) {
      return this.hover.computeIfAbsent(key, str -> new Animation(150L, 0.0F, Easing.INSTANCE12)).animateTo(contains ? 1.0F : 0.0F);
   }

   private static void card(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float floatValue, float centeredTextY, float shownWidth, float height, float f, float previewAlpha) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      menuDrawContext.drawRoundedRect(
         floatValue,
         centeredTextY,
         shownWidth,
         height,
         headerRadius,
         currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.35F + f * 0.55F).method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(floatValue, centeredTextY, shownWidth, height, 0.15F, headerRadius, currentStyle.getFieldBorder().getZenithColor().method7294(2.0F + f * 2.0F).method7294(previewAlpha));
   }

   private float button(
      MenuDrawContext menuDrawContext,
      ZenithStyle currentStyle,
      String key,
      String str,
      MenuGlyph menuGlyph,
      float floatValue,
      float centeredTextY,
      float shownWidth,
      int lastClickX,
      int lastClickY,
      float previewAlpha,
      int n
   ) {
      BoundsData boundsData = new BoundsData(floatValue, centeredTextY, shownWidth, 16.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.hot(key, contains);
      ZenithColor zenithColor = n == 2 ? ERROR : currentStyle.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F);
      float press = Smooth.press(this.pressed.getOrDefault(key, 0L), 320L);
      ZenithColor zenithColor2 = currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.35F + hot * 0.55F);
      if (n > 0) {
         zenithColor2 = zenithColor2.method7295(zenithColor.method7292(0.14F), 0.3F + hot * 0.35F + (this.armed.equals(key) ? 0.4F : 0.0F));
      }

      menuDrawContext.drawRoundedRect(floatValue, centeredTextY, shownWidth, 16.0F, headerRadius, zenithColor2.method7294(previewAlpha));
      if (press > 0.01F) {
         float f = (1.0F - press) * 3.0F;
         menuDrawContext.drawRoundedRect(floatValue - f * 0.5F, centeredTextY - f * 0.5F, shownWidth + f, 16.0F + f, headerRadius, zenithColor.method7292(0.35F * press).method7294(previewAlpha));
      }

      menuDrawContext.drawRoundedBorder(
         floatValue,
         centeredTextY,
         shownWidth,
         16.0F,
         0.15F,
         headerRadius,
         (n > 0 ? zenithColor.method7292(0.2F + hot * 0.25F) : currentStyle.getFieldBorder().getZenithColor().method7294(2.0F + hot * 2.0F)).method7294(previewAlpha)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      float f = floatValue + 7.0F;
      if (menuGlyph != null) {
         menuGlyph.draw(menuDrawContext, f, centeredTextY + 5.0F, (n > 0 ? zenithColor : currentStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha));
         f += 10.0F;
      }

      String name = this.armed.equals(key) ? t("gui.scripts.info.confirm") : str;
      menuDrawContext.drawText(
         font,
         font.clip(name, floatValue + shownWidth - f - 5.0F),
         f,
         centeredTextY + (16.0F - font.height()) / 2.0F,
         (n > 0 ? zenithColor : currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hot)).method7294(previewAlpha)
      );
      this.buttons.put(key, boundsData);
      return 16.0F;
   }

   private float field(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String text, FormField name, String str, float floatValue, float centeredTextY, float shownWidth, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      Font shownFont = Fonts.NEW_MEDIUM.getFont(5.0F);
      menuDrawContext.drawText(font, t(text), floatValue, centeredTextY, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha));
      float render = name.render(
         menuDrawContext,
         shownFont,
         floatValue,
         centeredTextY + font.height() + 3.0F,
         shownWidth,
         previewAlpha,
         t(str),
         currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.3F),
         currentStyle.getFieldBorder().getZenithColor().method7294(2.5F),
         currentStyle.getPrimaryColor().getZenithColor().method7292(0.6F),
         currentStyle.getTextEnable().getZenithColor(),
         currentStyle.getTextTertiary().getZenithColor()
      );
      return font.height() + 3.0F + render + 7.0F;
   }

   private static void caption(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, String text, float floatValue, float centeredTextY, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      menuDrawContext.drawText(font, text, floatValue, centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
   }

   public void render(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float floatValue, float y, float width, float height, float previewAlpha, ZenithStyle currentStyle) {
      if (this.script != null) {
         float f = previewAlpha * this.appear.animateTo(1.0F);
         this.bounds = new BoundsData(floatValue, y, width, height);
         this.buttons.clear();
         this.swatches.clear();
         if (!this.armed.isEmpty() && System.currentTimeMillis() - this.armedAt > 3000L) {
            this.armed = "";
         }

         float update = this.scroller.update();
         MsdfRenderer.flushBatch();
         menuDrawContext.enableScissor(floatValue, y, floatValue + width, y + height);
         float shownWidth = 112.0F;
         float floatValue2 = floatValue;
         float centeredTextY = y + update;
         card(menuDrawContext, currentStyle, floatValue, centeredTextY, shownWidth, 80.0F, 0.0F, f);
         float width2 = 42.0F;
         float floatValue3 = floatValue + (shownWidth - width2) / 2.0F;
         float y2 = centeredTextY + 9.0F;
         this.avatarRect = new BoundsData(floatValue3, y2, width2, width2);
         boolean contains = this.avatarRect.contains(lastClickX, lastClickY);
         float hot = this.hot("avatar", contains);
         drawAvatar(menuDrawContext, currentStyle, this.script, floatValue3, y2, width2, f, this.color);
         if (hot > 0.01F) {
            menuDrawContext.drawRoundedRect(
               floatValue3, y2, width2, width2, ProxyDotRadiusData.method8069(width2 * 0.28F), new ZenithColor(0, 0, 0, 150).method7294(f * hot)
            );
            Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
            String text = t("gui.scripts.info.avatarChange");
            menuDrawContext.drawText(
               font,
               text,
               floatValue3 + (width2 - font.width(text)) / 2.0F,
               y2 + (width2 - font.height()) / 2.0F,
               ZenithColor.INSTANCE.method7294(f * hot)
            );
         }

         Font font = Fonts.NEW_SEMIBOLD.getFont(5.6F);
         String clip = font.clip(this.name.get().isEmpty() ? this.script.getId() : this.name.get(), shownWidth - 12.0F);
         menuDrawContext.drawText(font, clip, floatValue + (shownWidth - font.width(clip)) / 2.0F, y2 + width2 + 6.0F, currentStyle.getTextEnable().getZenithColor().method7294(f));
         Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
         String text = (this.script.getVersion().isBlank() ? "" : "v" + this.script.getVersion() + "  ·  ")
            + (this.author.get().isEmpty() ? this.script.getId() : this.author.get());
         text = font2.clip(text, shownWidth - 12.0F);
         menuDrawContext.drawText(
            font2,
            text,
            floatValue + (shownWidth - font2.width(text)) / 2.0F,
            y2 + width2 + 8.0F + font.height(),
            currentStyle.getTextTertiary().getZenithColor().method7294(f)
         );
         centeredTextY += 86.0F;
         float shownWidth2 = (shownWidth - 4.0F) / 2.0F;
         this.button(menuDrawContext, currentStyle, "avatar.upload", t("gui.scripts.info.upload"), MenuGlyph.ACTION, floatValue, centeredTextY, shownWidth2, lastClickX, lastClickY, f, 1);
         this.button(menuDrawContext, currentStyle, "avatar.remove", t("gui.scripts.info.removeAvatar"), null, floatValue + shownWidth2 + 4.0F, centeredTextY, shownWidth2, lastClickX, lastClickY, f, 0);
         centeredTextY += 22.0F;
         caption(menuDrawContext, currentStyle, t("gui.scripts.info.color"), floatValue, centeredTextY, f);
         centeredTextY += 9.0F;
         float width3 = (shownWidth - 20.0F) / 9.0F;

         for (int i = 0; i <= PALETTE.length; i++) {
            int intValue = i == PALETTE.length ? -1 : PALETTE[i];
            float floatValue4 = floatValue2 + i * (width3 + 2.5F);
            BoundsData boundsData = new BoundsData(floatValue4, centeredTextY, width3, width3);
            boolean flag = intValue == this.color;
            ZenithColor zenithColor = intValue == -1 ? currentStyle.getPrimaryColor().getZenithColor() : new ZenithColor(intValue);
            menuDrawContext.drawRoundedRect(
               floatValue4, centeredTextY, width3, width3, ProxyDotRadiusData.method8069(width3 / 2.0F), zenithColor.method7294(f * (intValue == -1 ? 0.35F : 0.9F))
            );
            if (intValue == -1) {
               Font font3 = Fonts.NEW_MEDIUM.getFont(4.0F);
               menuDrawContext.drawText(
                  font3,
                  "A",
                  floatValue4 + (width3 - font3.width("A")) / 2.0F,
                  centeredTextY + (width3 - font3.height()) / 2.0F,
                  currentStyle.getTextEnable().getZenithColor().method7294(f)
               );
            }

            float hot2 = this.hot("ring:" + intValue, flag);
            if (hot2 > 0.01F) {
               float f2 = 1.5F + (1.0F - hot2) * 2.5F;
               menuDrawContext.drawRoundedBorder(
                  floatValue4 - f2,
                  centeredTextY - f2,
                  width3 + f2 * 2.0F,
                  width3 + f2 * 2.0F,
                  0.2F,
                  ProxyDotRadiusData.method8069((width3 + f2 * 2.0F) / 2.0F),
                  currentStyle.getTextEnable().getZenithColor().method7294(f * hot2)
               );
            }

            this.swatches.add(new Object[]{boundsData, intValue});
         }

         centeredTextY += width3 + 10.0F;
         caption(menuDrawContext, currentStyle, t("gui.scripts.info.preview"), floatValue2, centeredTextY, f);
         centeredTextY += 9.0F;
         card(menuDrawContext, currentStyle, floatValue2, centeredTextY, shownWidth, 26.0F, 0.0F, f);
         drawAvatar(menuDrawContext, currentStyle, this.script, floatValue2 + 5.0F, centeredTextY + 4.0F, 18.0F, f, this.color);
         Font font3 = Fonts.NEW_MEDIUM.getFont(5.0F);
         menuDrawContext.drawText(
            font3,
            font3.clip(this.name.get().isEmpty() ? this.script.getId() : this.name.get(), shownWidth - 36.0F),
            floatValue2 + 28.0F,
            centeredTextY + 5.0F,
            currentStyle.getTextEnable().getZenithColor().method7294(f)
         );
         menuDrawContext.drawText(
            font2,
            font2.clip(this.description.get().isEmpty() ? t("gui.scripts.info.noDescription") : this.description.get(), shownWidth - 36.0F),
            floatValue2 + 28.0F,
            centeredTextY + 7.0F + font3.height(),
            currentStyle.getTextTertiary().getZenithColor().method7294(f)
         );
         float f2 = centeredTextY + 26.0F;
         float floatValue4 = floatValue + shownWidth + 10.0F;
         float shownWidth3 = width - shownWidth - 10.0F;
         float centeredTextY2 = y + update;
         caption(menuDrawContext, currentStyle, t("gui.scripts.info.caption.main"), floatValue4, centeredTextY2, f);
         centeredTextY2 += 10.0F;
         float shownWidth4 = (shownWidth3 - 8.0F) / 2.0F;
         float field = this.field(menuDrawContext, currentStyle, "gui.scripts.info.name", this.name, "gui.scripts.info.name.hint", floatValue4, centeredTextY2, shownWidth4, f);
         this.field(menuDrawContext, currentStyle, "gui.scripts.info.author", this.author, "gui.scripts.info.author.hint", floatValue4 + shownWidth4 + 8.0F, centeredTextY2, shownWidth4, f);
         centeredTextY2 += field;
         centeredTextY2 += this.field(menuDrawContext, currentStyle, "gui.scripts.info.description", this.description, "gui.scripts.info.description.hint", floatValue4, centeredTextY2, shownWidth3, f);
         float field2 = this.field(menuDrawContext, currentStyle, "gui.scripts.info.tags", this.tags, "gui.scripts.info.tags.hint", floatValue4, centeredTextY2, shownWidth4, f);
         this.field(menuDrawContext, currentStyle, "gui.scripts.info.link", this.link, "gui.scripts.info.link.hint", floatValue4 + shownWidth4 + 8.0F, centeredTextY2, shownWidth4, f);
         centeredTextY2 += field2 + 2.0F;
         caption(menuDrawContext, currentStyle, t("gui.scripts.info.caption.version"), floatValue4, centeredTextY2, f);
         centeredTextY2 += 10.0F;
         Font font4 = Fonts.NEW_SEMIBOLD.getFont(7.0F);
         String str = "v" + (this.script.getVersion().isBlank() ? "—" : this.script.getVersion());
         menuDrawContext.drawText(font4, str, floatValue4, centeredTextY2 + 3.0F, currentStyle.getTextEnable().getZenithColor().method7294(f));
         float floatValue5 = floatValue4 + font4.width(str) + 10.0F;
         float shownWidth5 = (floatValue4 + shownWidth3 - floatValue5 - 8.0F) / 3.0F;
         this.button(menuDrawContext, currentStyle, "bump.2", t("gui.scripts.info.bumpPatch"), null, floatValue5, centeredTextY2, shownWidth5, lastClickX, lastClickY, f, 1);
         this.button(menuDrawContext, currentStyle, "bump.1", t("gui.scripts.info.bumpMinor"), null, floatValue5 + shownWidth5 + 4.0F, centeredTextY2, shownWidth5, lastClickX, lastClickY, f, 1);
         this.button(menuDrawContext, currentStyle, "bump.0", t("gui.scripts.info.bumpMajor"), null, floatValue5 + (shownWidth5 + 4.0F) * 2.0F, centeredTextY2, shownWidth5, lastClickX, lastClickY, f, 1);
         centeredTextY2 += 22.0F;
         centeredTextY2 += this.field(menuDrawContext, currentStyle, "gui.scripts.info.note", this.note, "gui.scripts.info.note.hint", floatValue4, centeredTextY2, shownWidth3, f);
         List<String> changes = this.changes(8);
         if (!changes.isEmpty()) {
            float height2 = changes.size() * (font2.height() + 3.0F) + 8.0F;
            card(menuDrawContext, currentStyle, floatValue4, centeredTextY2, shownWidth3, height2, 0.0F, f);
            float centeredTextY3 = centeredTextY2 + 5.0F;

            for (String change : changes) {
               boolean startsWith = change.startsWith("## ");
               menuDrawContext.drawText(
                  startsWith ? Fonts.NEW_MEDIUM.getFont(4.5F) : font2,
                  font2.clip(startsWith ? change.substring(3) : "  " + change.substring(2), shownWidth3 - 12.0F),
                  floatValue4 + 6.0F,
                  centeredTextY3,
                  (startsWith ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(f)
               );
               centeredTextY3 += font2.height() + 3.0F;
            }

            centeredTextY2 += height2 + 8.0F;
         }

         caption(menuDrawContext, currentStyle, t("gui.scripts.info.caption.manage"), floatValue4, centeredTextY2, f);
         centeredTextY2 += 10.0F;
         float shownWidth6 = (shownWidth3 - 8.0F) / 3.0F;
         this.button(menuDrawContext, currentStyle, "duplicate", t("gui.scripts.info.duplicate"), MenuGlyph.COPY, floatValue4, centeredTextY2, shownWidth6, lastClickX, lastClickY, f, 0);
         this.button(menuDrawContext, currentStyle, "export", t("gui.scripts.info.export"), MenuGlyph.STORAGE, floatValue4 + shownWidth6 + 4.0F, centeredTextY2, shownWidth6, lastClickX, lastClickY, f, 0);
         this.button(menuDrawContext, currentStyle, "folder", t("gui.scripts.openFolder"), MenuGlyph.STORAGE, floatValue4 + (shownWidth6 + 4.0F) * 2.0F, centeredTextY2, shownWidth6, lastClickX, lastClickY, f, 0);
         centeredTextY2 += 20.0F;
         this.button(menuDrawContext, currentStyle, "resetSettings", t("gui.scripts.info.resetSettings"), MenuGlyph.RESET, floatValue4, centeredTextY2, shownWidth6, lastClickX, lastClickY, f, 0);
         this.button(menuDrawContext, currentStyle, "resetStorage", t("gui.scripts.info.resetStorage"), MenuGlyph.RESET, floatValue4 + shownWidth6 + 4.0F, centeredTextY2, shownWidth6, lastClickX, lastClickY, f, 0);
         this.button(menuDrawContext, currentStyle, "delete", t("gui.scripts.info.delete"), MenuGlyph.CLOSE, floatValue4 + (shownWidth6 + 4.0F) * 2.0F, centeredTextY2, shownWidth6, lastClickX, lastClickY, f, 2);
         centeredTextY2 += 22.0F;
         if (this.script.getPath() != null) {
            float field3 = this.field(
               menuDrawContext, currentStyle, "gui.scripts.info.folderName", this.folder, "gui.scripts.info.folderName.hint", floatValue4, centeredTextY2, shownWidth3 - 80.0F, f
            );
            this.button(menuDrawContext, currentStyle, "rename", t("gui.scripts.info.rename"), null, floatValue4 + shownWidth3 - 76.0F, centeredTextY2 + 8.0F, 76.0F, lastClickX, lastClickY, f, 1);
            centeredTextY2 += field3;
         }

         MsdfRenderer.flushBatch();
         menuDrawContext.disableScissor();
         float f3 = Math.max(f2, centeredTextY2) - centeredTextY + 10.0F;
         this.scroller.clamp(Math.min(0.0F, height - f3), 0.0F);
         if (contains) {
            HoverInfo.request(t("gui.scripts.info.avatar"), t("gui.scripts.info.avatar.desc"));
         }
      }
   }

   public static void drawAvatar(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, Script script, float floatValue, float cardAvatarY, float width, float previewAlpha, int color) {
      Identifier texture = ScriptIcons.get(script);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(width * 0.28F);
      if (texture != null) {
         menuDrawContext.drawRoundedTexture(texture, floatValue, cardAvatarY, width, width, headerRadius, ZenithColor.INSTANCE.method7294(previewAlpha));
      } else {
         int intValue = color != -2 ? color : script.getColor();
         ZenithColor zenithColor = script.getType2() == Script.Type2.FAILED ? ERROR : (intValue == -1 ? currentStyle.getPrimaryColor().getZenithColor() : new ZenithColor(intValue));
         menuDrawContext.drawRoundedRect(floatValue, cardAvatarY, width, width, headerRadius, zenithColor.method7292(0.16F).method7294(previewAlpha));
         Font font = Fonts.NEW_SEMIBOLD.getFont(width * 0.38F);
         String str = script.method1403();
         String upperCase = str.isEmpty() ? "?" : str.substring(0, 1).toUpperCase();
         menuDrawContext.drawText(font, upperCase, floatValue + (width - font.width(upperCase)) / 2.0F, cardAvatarY + (width - font.height()) / 2.0F, zenithColor.method7294(previewAlpha));
      }
   }

   private void flash(String key) {
      this.pressed.put(key, System.currentTimeMillis());
   }

   public boolean mouseClicked(double lastClickX, double lastClickY) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         for (FormField field : this.fields) {
            BoundsData bounds = field.bounds();
            field.setFocused(bounds != null && bounds.contains(lastClickX, lastClickY));
         }

         if (this.avatarRect != null && this.avatarRect.contains(lastClickX, lastClickY)) {
            this.flash("avatar");
            this.pickAvatar();
            return true;
         } else {
            for (Object[] swatch : this.swatches) {
               if (((BoundsData)swatch[0]).contains(lastClickX, lastClickY)) {
                  this.color = (Integer)swatch[1];
                  this.flash("swatch:" + swatch[1]);
                  return true;
               }
            }

            for (Entry entry : this.buttons.entrySet()) {
               if (((BoundsData)entry.getValue()).contains(lastClickX, lastClickY)) {
                  this.flash((String)entry.getKey());
                  this.action((String)entry.getKey());
                  return true;
               }
            }

            return true;
         }
      } else {
         for (FormField field : this.fields) {
            field.setFocused(false);
         }

         return false;
      }
   }

   private boolean confirm(String armed) {
      if (this.armed.equals(armed) && System.currentTimeMillis() - this.armedAt < 3000L) {
         this.armed = "";
         return true;
      } else {
         this.armed = armed;
         this.armedAt = System.currentTimeMillis();
         return false;
      }
   }

   private void action(String key) {
      try {
         if (key.startsWith("mprice:")) {
            this.marketPrice = Integer.parseInt(key.substring(7));
            return;
         }

         switch (key) {
            case "market.submit":
               this.submitToMarket();
               break;
            case "market.withdraw":
               this.withdrawFromMarket();
               break;
            case "close":
               this.onClose.run();
               break;
            case "save":
               this.apply();
               break;
            case "avatar.upload":
               this.pickAvatar();
               break;
            case "avatar.remove":
               this.host.method1354(this.script);
               ScriptIcons.invalidate(this.script);
               this.toast.accept(t("gui.scripts.info.avatarRemoved"), false);
               break;
            case "bump.0":
            case "bump.1":
            case "bump.2":
               if (this.isDirty()) {
                  this.apply();
               }

               String replacement = this.host.method1351(this.script, key.charAt(5) - '0', this.note.get());
               this.changesScript = null;
               this.note.set("");
               this.toast.accept(t("gui.scripts.info.bumped").replace("{v}", replacement), false);
               break;
            case "duplicate":
               Script script = this.host.method1355(this.script);
               this.toast.accept(t("gui.scripts.info.duplicated"), false);
               if (script != null) {
                  this.reopen.accept(script);
               }
               break;
            case "export":
               Path path = this.host.method1360(this.script);
               com.mojang.blaze3d.Blaze3D.openPath((path.getParent().toFile()).toPath());
               this.toast.accept(t("gui.scripts.info.exported").replace("{f}", path.getFileName().toString()), false);
               break;
            case "folder":
               com.mojang.blaze3d.Blaze3D.openPath(((this.script.getPath() == null ? this.script.getPath2().getParent() : this.script.getPath()).toFile()).toPath());
               break;
            case "resetSettings":
               if (!this.confirm(key)) {
                  return;
               }

               this.host.method1359(this.script);
               this.toast.accept(t("gui.scripts.info.settingsReset"), false);
               break;
            case "resetStorage":
               if (!this.confirm(key)) {
                  return;
               }

               this.host.method1358(this.script);
               this.toast.accept(t("gui.scripts.info.storageReset"), false);
               break;
            case "delete":
               if (!this.confirm(key)) {
                  return;
               }

               this.host.method1357(this.script);
               this.toast.accept(t("gui.scripts.info.deleted"), false);
               this.reopen.accept(null);
               break;
            case "rename":
               Script script2 = this.host.method1356(this.script, this.folder.get());
               this.toast.accept(t("gui.scripts.info.renamed"), false);
               this.reopen.accept(script2);
         }
      } catch (Exception e) {
         this.toast.accept(e.getMessage(), true);
      }
   }

   private void pickAvatar() {
      Script script = this.script;
      Thread thread = new Thread(() -> {
         String var2x;
         try {
            MemoryStack stackPush = MemoryStack.stackPush();

            try {
               PointerBuffer mallocPointer = stackPush.mallocPointer(1);
               mallocPointer.put(stackPush.UTF8("*.png"));
               mallocPointer.flip();
               var2x = FileDialogs.tinyfd_openFileDialog(t("gui.scripts.info.pickTitle"), null, mallocPointer, "PNG", false);
            } catch (Throwable e) {
               if (stackPush != null) {
                  try {
                     stackPush.close();
                  } catch (Throwable ex) {
                     e.addSuppressed(ex);
                  }
               }

               throw e;
            }

            if (stackPush != null) {
               stackPush.close();
            }
         } catch (Throwable e) {
            var2x = null;
            Minecraft.getInstance().execute(() -> {
               this.toast.accept(t("gui.scripts.info.pickFallback"), true);
               if (script.getPath() != null) {
                  com.mojang.blaze3d.Blaze3D.openPath((script.getPath().toFile()).toPath());
               }
            });
         }

         String first = var2x;
         if (first != null) {
            Minecraft.getInstance().execute(() -> {
               try {
                  this.host.method1353(script, Path.of(first));
                  ScriptIcons.invalidate(script);
                  this.toast.accept(t("gui.scripts.info.avatarSet"), false);
               } catch (Exception var4x) {
                  this.toast.accept(var4x.getMessage(), true);
               }
            });
         }
      }, "Zenith-ScriptIconPicker");
      thread.setDaemon(true);
      thread.start();
   }

   public boolean mouseScrolled(double lastClickX, double lastClickY, double verticalAmount) {
      if (this.bounds != null && this.bounds.contains(lastClickX, lastClickY)) {
         this.scroller.add((float)verticalAmount * 28.0F);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      for (FormField field : this.fields) {
         if (field.isFocused()) {
            field.keyPressed(keyCode, modifiers);
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(char codepoint) {
      for (FormField field : this.fields) {
         if (field.isFocused()) {
            return field.charTyped(codepoint);
         }
      }

      return false;
   }
}
