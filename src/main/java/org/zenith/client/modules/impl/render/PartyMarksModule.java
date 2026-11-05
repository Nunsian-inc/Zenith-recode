package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "PartyMarks",
   category = ModuleCategory.RENDER,
   description = "module.partymarks.description"
)
public final class PartyMarksModule extends Module {
   public static final PartyMarksModule INSTANCE = new PartyMarksModule();
   public static final List<PartyMarksModule.Data2> items = List.of(
      new PartyMarksModule.Data2("base", MenuGlyph.STORAGE, -10443270),
      new PartyMarksModule.Data2("spawner", MenuGlyph.PARTICLES, -757066),
      new PartyMarksModule.Data2("mine", MenuGlyph.CRAFT, -6052957),
      new PartyMarksModule.Data2("farm", MenuGlyph.HAND, -11870592),
      new PartyMarksModule.Data2("portal", MenuGlyph.WORLD, -5796870),
      new PartyMarksModule.Data2("loot", MenuGlyph.CHESTPLATE, -278748),
      new PartyMarksModule.Data2("danger", MenuGlyph.CRIT, -495247),
      new PartyMarksModule.Data2("pin", MenuGlyph.PIN, -13058568)
   );
   private static final ZenithColor zenithColor = new ZenithColor(-1096636);
   private final BooleanSetting showMarks = new BooleanSetting("module.partyMarks.showMarks", "module.partyMarks.showMarks.desc", true);
   private final BooleanSetting showDeaths = new BooleanSetting("module.partyMarks.showDeaths", "module.partyMarks.showDeaths.desc", true);
   private final SliderSetting deathMinutes = new SliderSetting(
      "module.partyMarks.deathMinutes", 30.0F, 0.0F, 240.0F, 5.0F, "module.partyMarks.deathMinutes.desc", " min", null, null
   );
   private final SliderSetting maxDistance = new SliderSetting(
      "module.partyMarks.maxDistance", 2000.0F, 50.0F, 10000.0F, 50.0F, "module.partyMarks.maxDistance.desc", " m", null, null
   );
   private final SliderSetting scale = new SliderSetting(
      "module.partyMarks.scale", 100.0F, 50.0F, 200.0F, 5.0F, "module.partyMarks.scale.desc", " %", null, null
   );
   private final BooleanSetting compass = new BooleanSetting("module.partyMarks.compass", "module.partyMarks.compass.desc", true);
   private final SliderSetting compassRadius = new SliderSetting(
      "module.partyMarks.compassRadius", 46.0F, 16.0F, 140.0F, 2.0F, "module.partyMarks.compassRadius.desc", " px", null, null
   );
   public final BindSetting markKey = new BindSetting("module.partyMarks.markKey", "module.partyMarks.markKey.desc", -1);
   public final TextSetting markName = new TextSetting(
      "module.partyMarks.markName", "module.partyMarks.markName.desc", "", "module.partyMarks.markName.empty", TextSetting.Entry.method2125(32)
   );
   private volatile long longValue;
   private volatile long longValue2;
   private long longValue3;

   private PartyMarksModule() {
   }

   public static PartyMarksModule.Data2 method5419(String text) {
      for (PartyMarksModule.Data2 data2 : items) {
         if (data2.key().equals(text)) {
            return data2;
         }
      }

      return items.get(items.size() - 1);
   }

   public void method5420(long longValue) {
      this.longValue = longValue;
      this.longValue2 = 0L;
      this.setToggled(true);
   }

   public void method5421(long longValue) {
      this.longValue2 = longValue;
      this.longValue = 0L;
      this.setToggled(true);
   }

   public long getLongValue5() {
      return this.longValue;
   }

   public long getLongValue22() {
      return this.longValue2;
   }

   public void clearFocus() {
      this.longValue = 0L;
      this.longValue2 = 0L;
   }

   private static Cloud cloud() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getCloud();
   }

   @EventTarget
   public void method5422(KeyEvent event) {
      if (this.markKey.getKeyCode() > 0 && event.isPress(this.markKey.getKeyCode())) {
         this.method5424();
      } else {
         int keyCode = CloudWaypointsModule.INSTANCE.getMarkBind().getKeyCode();
         if (keyCode > 0 && event.isPress(keyCode)) {
            long now = System.currentTimeMillis();
            if (now - this.longValue3 < 350L) {
               this.longValue3 = 0L;
               this.method5424();
            } else {
               this.longValue3 = now;
            }
         }
      }
   }

   public PartyMarksModule.Data method5423(BlockHitResult blockHitResult) {
      if (blockHitResult != null && blockHitResult.getType() != Type.MISS && mc.level != null) {
         Block block = mc.level.getBlockState(blockHitResult.getBlockPos()).getBlock();
         String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
         String text;
         if (path.contains("spawner")) {
            text = "spawner";
         } else if (path.contains("chest") || path.equals("barrel") || path.contains("shulker_box") || path.equals("hopper")) {
            text = "loot";
         } else if (path.contains("portal") || path.equals("end_gateway") || path.contains("obsidian")) {
            text = "portal";
         } else if (path.endsWith("_ore") || path.equals("ancient_debris") || path.contains("amethyst")) {
            text = "mine";
         } else if (path.equals("farmland")
            || path.equals("wheat")
            || path.equals("carrots")
            || path.equals("potatoes")
            || path.equals("beetroots")
            || path.contains("melon")
            || path.contains("pumpkin")
            || path.equals("sugar_cane")
            || path.equals("nether_wart")
            || path.equals("cactus")
            || path.equals("bamboo")) {
            text = "farm";
         } else if (path.endsWith("_bed")
            || path.equals("crafting_table")
            || path.contains("furnace")
            || path.equals("enchanting_table")
            || path.equals("anvil")
            || path.equals("respawn_anchor")) {
            text = "base";
         } else if (!path.equals("lava") && !path.equals("magma_block") && !path.equals("tnt") && !path.contains("sculk")) {
            text = "pin";
         } else {
            text = "danger";
         }

         String string = text.equals("pin") ? block.getName().getString() : method5431("cloud.mark.icon." + text);
         return new PartyMarksModule.Data(text, string.length() > 32 ? string.substring(0, 32) : string);
      } else {
         return new PartyMarksModule.Data("pin", method5431("cloud.mark.icon.pin"));
      }
   }

   public boolean method5424() {
      Cloud cloud2 = cloud();
      if (cloud2 != null && mc.player != null && mc.level != null && cloud2.getPartyMember().method1136()) {
         Vec3 eyePosition = mc.player.getEyePosition();
         Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());
         BlockHitResult blockHitResult = TargetUtils.method3205(eyePosition, 256.0, rotation, false);
         Vec3 atBottomCenterOf = blockHitResult != null && blockHitResult.getType() != Type.MISS ? Vec3.atBottomCenterOf(blockHitResult.getBlockPos().above()) : mc.player.position();
         PartyMarksModule.Data data = this.method5423(blockHitResult);
         PartyMarksModule.Data2 data2 = method5419(data.getText());
         String name = this.markName.getValue() != null && !this.markName.getValue().isBlank() ? this.markName.getValue().strip() : data.name();
         cloud2.getPartyMember().method1160(0L, cloud2.method7911(), cloud2.method7913(), atBottomCenterOf.x, atBottomCenterOf.y, atBottomCenterOf.z, name, data2.key(), data2.color());
         if (mc.player != null) {
            mc.player.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 0.6F, 1.4F);
         }

         return true;
      } else {
         return false;
      }
   }

   @EventTarget
   public void method5425(DrawEvent event) {
      Cloud cloud2 = cloud();
      if (cloud2 != null && mc.level != null && mc.player != null) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            CustomDrawContext customDrawContext = event.getCustomDrawContext();
            MenuDrawContext menuDrawContext = MenuDrawContext.method8080(customDrawContext, 0, 0, event.getTickDelta());
            String anObject = cloud2.method7911();
            String str = cloud2.method7913();
            Vec3 position = mc.getEntityRenderDispatcher().camera.position();
            float f = this.scale.getValue3() / 100.0F;
            double d = this.maxDistance.getValue3();
            PartyMember partyMember = cloud2.getPartyMember();
            if (this.showMarks.isEnabled()) {
               for (PartyMember.Data12 data12 : partyMember.method1138()) {
                  if (data12.server().equals(anObject)
                     && data12.dimension().equals(str)
                     && method5429(data12.getDoubleValue(), data12.getDoubleValue2(), data12.getDoubleValue3())) {
                     Vec3 vec = new Vec3(data12.getDoubleValue(), data12.getDoubleValue2() + 0.5, data12.getDoubleValue3());
                     double d2 = position.distanceTo(vec);
                     boolean flag = data12.getLongValue() == this.longValue;
                     if (!(d2 > d) || flag) {
                        PartyMarksModule.Data2 data2 = method5419(data12.getText4());
                        ZenithColor zenithColor2 = new ZenithColor(data12.color() == -1 ? data2.color() : data12.color());
                        this.method5426(menuDrawContext, currentStyle, vec, position, data2.glyph(), method5430(data12.name()), meters(d2), zenithColor2, d2, f, flag);
                     }
                  }
               }
            }

            if (this.showDeaths.isEnabled()) {
               long l = this.deathMinutes.getValue3() <= 0.0F ? 0L : System.currentTimeMillis() - (long)this.deathMinutes.getValue3() * 60000L;

               for (PartyMember.Data4 data4 : partyMember.method1139()) {
                  boolean flag = data4.getLongValue() == this.longValue2;
                  if (data4.server().equals(anObject)
                     && data4.dimension().equals(str)
                     && (data4.time() >= l || flag)
                     && method5429(data4.getDoubleValue(), data4.getDoubleValue2(), data4.getDoubleValue3())) {
                     Vec3 vec = new Vec3(data4.getDoubleValue(), data4.getDoubleValue2() + 0.5, data4.getDoubleValue3());
                     double d2 = position.distanceTo(vec);
                     if (!(d2 > d) || flag) {
                        String text = (data4.uid() == cloud2.getLongValue() ? method5431("cloud.death.mine") : method5430(data4.name()))
                           + " · "
                           + data4.getItems().size();
                        this.method5426(
                           menuDrawContext,
                           currentStyle,
                           vec,
                           position,
                           MenuGlyph.CLOSE,
                           text,
                           meters(d2),
                           zenithColor,
                           d2,
                           f,
                           flag || data4.uid() == cloud2.getLongValue() && System.currentTimeMillis() - data4.time() < 600000L
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private void method5426(
      MenuDrawContext menuDrawContext,
      ZenithStyle currentStyle,
      Vec3 vec,
      Vec3 position,
      MenuGlyph glyph,
      String text,
      String meters,
      ZenithColor zenithColor,
      double d,
      float f,
      boolean flag
   ) {
      if (ProjectionUtils.method302(vec)) {
         Vec3 vec2 = ProjectionUtils.method301(vec);
         this.method5427(menuDrawContext, currentStyle, (float)vec2.x, (float)vec2.y, glyph, text, meters, zenithColor, d, f);
      } else if (flag && this.compass.isEnabled()) {
         this.method5428(menuDrawContext, currentStyle, vec, position, glyph, meters, zenithColor, f);
      }
   }

   private void method5427(
      MenuDrawContext menuDrawContext, ZenithStyle currentStyle, float x, float y, MenuGlyph glyph, String text, String meters, ZenithColor zenithColor, double d, float f
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(6.5F);
      float f2 = Mth.clamp((float)((d - 8.0) / 200.0), 0.0F, 1.0F);
      float x2 = (1.0F - 0.25F * f2) * f;
      float intValue = GuiStyle.PADDING.intValue();
      float width = 9.0F;
      float height = Math.max(font.height() + intValue, width + 3.0F);
      float f3 = intValue + width + 3.0F + font.width(text) + intValue;
      float f4 = intValue + font.width(meters) + intValue;
      float width2 = f3 + f4;
      float f5 = 9.0F;
      float floatValue = x - width2 / 2.0F;
      float y2 = y - f5 - height;
      menuDrawContext.pushMatrix();
      menuDrawContext.pose().translate(x, y);
      menuDrawContext.pose().scale(x2, x2);
      menuDrawContext.pose().translate(-x, -y);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(height / 2.0F);
      menuDrawContext.drawRoundedRect(floatValue, y2, width2, height, headerRadius, currentStyle.getHudBackground().getZenithColor());
      menuDrawContext.drawRoundedRect(floatValue + 1.5F, y2 + (height - width) / 2.0F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor.method7294(0.9F));
      glyph.draw(menuDrawContext, floatValue + 1.5F + (width - 6.0F) / 2.0F, y2 + (height - 6.0F) / 2.0F, new ZenithColor(255, 255, 255));
      float centeredTextY = y2 + (height - font.height()) / 2.0F;
      menuDrawContext.drawText(font, text, floatValue + 1.5F + width + 4.0F, centeredTextY, currentStyle.getTextEnable().getZenithColor());
      menuDrawContext.drawText(font, meters, floatValue + f3 + intValue * 0.5F, centeredTextY, currentStyle.getTextTertiary().getZenithColor());
      menuDrawContext.drawRect(x - 0.5F, y2 + height, 1.0F, f5 - 2.0F, zenithColor.method7294(0.8F));
      menuDrawContext.drawRoundedRect(x - 1.5F, y - 1.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithColor);
      menuDrawContext.popMatrix();
   }

   private void method5428(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, Vec3 vec, Vec3 position, MenuGlyph glyph, String meters, ZenithColor zenithColor, float f) {
      double x = vec.x - position.x;
      double y = vec.z - position.z;
      float f2 = (float)Math.toDegrees(Math.atan2(y, x)) - 90.0F;
      float angdeg = Mth.wrapDegrees(f2 - mc.getEntityRenderDispatcher().camera.yRot());
      double d = Math.toRadians(angdeg);
      float f3 = this.compassRadius.getValue3() * f;
      float f4 = mc.getWindow().getGuiScaledWidth() / 2.0F + (float)Math.sin(d) * f3;
      float f5 = mc.getWindow().getGuiScaledHeight() / 2.0F - (float)Math.cos(d) * f3;
      Font font = Fonts.NEW_REGULAR.getFont(6.0F);
      float width = 8.0F;
      float height = width + 3.0F;
      float width2 = 1.5F + width + 3.0F + font.width(meters) + 4.0F;
      float floatValue = f4 - width2 / 2.0F;
      float y2 = f5 - height / 2.0F;
      menuDrawContext.drawRoundedRect(floatValue, y2, width2, height, ProxyDotRadiusData.method8069(height / 2.0F), currentStyle.getHudBackground().getZenithColor());
      menuDrawContext.drawRoundedRect(floatValue + 1.5F, y2 + 1.5F, width, width, ProxyDotRadiusData.method8069(width / 2.0F), zenithColor);
      glyph.draw(menuDrawContext, floatValue + 1.5F + (width - 6.0F) / 2.0F, y2 + 1.5F + (width - 6.0F) / 2.0F, new ZenithColor(255, 255, 255));
      menuDrawContext.drawText(font, meters, floatValue + 1.5F + width + 3.0F, y2 + (height - font.height()) / 2.0F, currentStyle.getTextSecondary().getZenithColor());
   }

   private static String meters(double d) {
      return d >= 1000.0 ? Math.round(d / 100.0) / 10.0 + "k" : Math.round(d) + "m";
   }

   private static boolean method5429(double doubleValue, double d, double d2) {
      return Double.isFinite(doubleValue)
         && Double.isFinite(d)
         && Double.isFinite(d2)
         && Math.abs(doubleValue) <= 3.0E7
         && Math.abs(d2) <= 3.0E7
         && Math.abs(d) <= 100000.0;
   }

   private static String method5430(String name) {
      return name == null ? "" : (name.length() > 32 ? name.substring(0, 32) : name);
   }

   private static String method5431(String text) {
      return Zenith.getInstance().getLocalization().translate(text);
   }

   public static final class Data {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data(String text, String str) {
         this.text = text;
         this.text2 = str;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final MenuGlyph menuGlyph;
      private final int intValue;

      public String text() {
         return this.text;
      }

      public MenuGlyph menuGlyph() {
         return this.menuGlyph;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.menuGlyph, other.menuGlyph) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, menuGlyph, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "menuGlyph=" + this.menuGlyph + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(String text, MenuGlyph menuGlyph, int intValue) {
         this.text = text;
         this.menuGlyph = menuGlyph;
         this.intValue = intValue;
      }

      public String key() {
         return this.text;
      }

      public MenuGlyph glyph() {
         return this.menuGlyph;
      }

      public int color() {
         return this.intValue;
      }
   }
}
