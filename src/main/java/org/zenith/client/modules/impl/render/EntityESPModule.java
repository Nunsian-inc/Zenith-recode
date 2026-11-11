package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.ItemIntegrityTracker;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.network.Names;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.QueuedCustomDrawContext;

@ModuleInfo(
   name = "EntityESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.entityesp.description"
)
public final class EntityESPModule extends Module {
   public static final EntityESPModule INSTANCE = new EntityESPModule();
   private static final long CONST_longValue = 10000L;
   private final MultiSelectSetting targets = MultiSelectSetting.method5224(
      "module.entityESP.targets",
      "module.entityESP.targets.desc",
      Arrays.asList(
         "module.entityESP.players", "module.entityESP.noArmor", "module.entityESP.friends", "module.entityESP.droppedItems", "module.entityESP.mobs", "TNT"
      )
   );
   private final SliderSetting removeHiddenPlayerDelay = new SliderSetting(
      "module.entityESP.removeHiddenPlayerDelay",
      1.0F,
      0.0F,
      40.0F,
      1.0F,
      "module.entityESP.removeHiddenPlayerDelay.desc",
      "t",
      () -> this.targets.method5227(0),
      null
   );
   private final MultiSelectSetting elements = method554(
      MultiSelectSetting.method5224(
            "module.entityESP.elements",
            "module.entityESP.elements.desc",
            Arrays.asList(
               "module.entityESP.names",
               "module.entityESP.armor",
               "module.entityESP.armorText",
               "module.entityESP.ping",
               "module.entityESP.triangles",
               "module.entityESP.boxes",
               "module.entityESP.hands",
               "Tracers"
            )
         )
         .method5220("module.entityESP.triangles", "module.entityESP.boxes"),
      "module.entityESP.armorText"
   );
   private final BooleanSetting gradient = new BooleanSetting(
      "module.entityESP.gradient", "module.entityESP.gradientDesc", true, () -> this.elements.method5226("module.entityESP.boxes")
   );
   private final SliderSetting scaleSetting = new SliderSetting(
      "module.entityESP.scaleSetting", 0.7F, 0.5F, 1.0F, 0.1F, "module.entityESP.scaleSetting.desc", "x"
   );
   private final SliderSetting tagMinScale = new SliderSetting(
      "module.entityESP.tagMinScale", 0.5F, 0.2F, 1.0F, 0.05F, "module.entityESP.tagMinScale.desc", "x"
   );
   private final BooleanSetting blur = new BooleanSetting("module.entityESP.blur", "module.entityESP.blurDesc", false, InterfaceModule.INSTANCE::method6817);
   private final BooleanSetting glow = new BooleanSetting("module.entityESP.glow", "module.entityESP.glowDesc", false, InterfaceModule.INSTANCE::method6819);
   private static final float CONST_floatValue = 8.0F;
   private static final String text = "module.entityESP.names";
   private static final String text2 = "module.entityESP.armor";
   private static final String text3 = "module.entityESP.armorText";
   private static final String text4 = "module.entityESP.ping";
   private static final String text5 = "module.entityESP.triangles";
   private static final String text6 = "module.entityESP.boxes";
   private static final String text7 = "module.entityESP.hands";
   private static final String text8 = "Tracers";
   private static final float CONST_floatValue2 = 0.6F;
   private static final float CONST_floatValue3 = 1.8F;
   private static final float CONST_floatValue4 = 18.0F;
   private static final float CONST_floatValue5 = 28.0F;
   private final List<ItemStack> items = new ArrayList<>();
   private final List<ItemStack> items2 = new ArrayList<>();
   private float previewAlpha = 1.0F;
   private final TextSetting tagOrder = method556(new TextSetting("module.entityESP.tagOrder", "HP,PING,GEAR", ""));
   private final BooleanSetting gearReversed = method556(new BooleanSetting("module.entityESP.gearReversed", "", false));
   private List<EntityESPModule.Type> items3;
   private String text9;
   private boolean flag;
   private final Map<EntityESPModule.Type, float[]> items4 = new EnumMap<>(EntityESPModule.Type.class);
   private float floatValue = 1.0F;
   private float floatValue2;
   private float floatValue3;
   private double doubleValue = Double.NaN;
   private double doubleValue2 = Double.NaN;
   private EntityESPModule.Type type;
   private EntityESPModule.Type type2;
   private float floatValue4;
   private final Map<EntityESPModule.Type, Float> items5 = new EnumMap<>(EntityESPModule.Type.class);
   private final Map<EntityESPModule.Type, Float> items6 = new EnumMap<>(EntityESPModule.Type.class);
   private final Map<EntityESPModule.Type, Float> items7 = new EnumMap<>(EntityESPModule.Type.class);
   private static final float CONST_floatValue6 = 0.35F;
   private static final float CONST_floatValue7 = 0.2F;
   private static final float CONST_floatValue8 = 160.0F;
   private final Map<String, EntityESPModule.Data4> items8 = new HashMap<>();
   private List<EntityESPModule.Data4> items9 = List.of();
   private long longValue = Long.MIN_VALUE;
   private final Jebriksheet jebriksheet = new Jebriksheet();
   private int intValue;
   private Vec3 vec3d;
   private static final float CONST_floatValue9 = 20.0F;
   private static final Map<EntityESPModule.Data2, EntityESPModule.Data3> items10 = new HashMap<>();
   private int intValue2;
   private static final float CONST_floatValue10 = 14.0F;
   private static final float CONST_floatValue11 = 4.5F;
   private static final float CONST_floatValue12 = 9.0F;
   private static final float CONST_floatValue13 = 11.0F;
   private static final float CONST_floatValue14 = 3.5F;
   private static final float CONST_floatValue15 = 9.5F;
   private static final float CONST_floatValue16 = 1.5F;
   private static final float CONST_floatValue17 = 2.0F;
   private static final float CONST_floatValue18 = 4.0F;
   private static final float CONST_floatValue19 = 9.0F;
   private static final float CONST_floatValue20 = 2.5F;
   private static final float CONST_floatValue21 = 3.5F;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 4;
   private static final Map<String, String> items11 = Map.ofEntries(
      Map.entry("drill", "Бур"),
      Map.entry("exp", "Опыт"),
      Map.entry("foundry", "Плавка"),
      Map.entry("internal", "Внутр"),
      Map.entry("impenetrable", "Нераз"),
      Map.entry("luck", "Удача"),
      Map.entry("vampire", "Вампир"),
      Map.entry("poison", "Яд"),
      Map.entry("freeze", "Мороз"),
      Map.entry("lightning", "Молния")
   );
   private static final Map<String, String> items12 = Map.ofEntries(
      Map.entry("efficiency", "Eff"),
      Map.entry("fortune", "Fort"),
      Map.entry("silk_touch", "Silk"),
      Map.entry("unbreaking", "Unb"),
      Map.entry("mending", "Mend"),
      Map.entry("sharpness", "Sharp"),
      Map.entry("smite", "Smite"),
      Map.entry("bane_of_arthropods", "Bane"),
      Map.entry("fire_aspect", "Fire"),
      Map.entry("looting", "Loot"),
      Map.entry("knockback", "Kb"),
      Map.entry("sweeping_edge", "Sweep"),
      Map.entry("protection", "Prot"),
      Map.entry("projectile_protection", "Proj"),
      Map.entry("blast_protection", "Blast"),
      Map.entry("fire_protection", "FireProt"),
      Map.entry("feather_falling", "Feather"),
      Map.entry("depth_strider", "Depth"),
      Map.entry("thorns", "Thorns"),
      Map.entry("respiration", "Resp"),
      Map.entry("aqua_affinity", "Aqua"),
      Map.entry("soul_speed", "Soul"),
      Map.entry("swift_sneak", "Swift"),
      Map.entry("power", "Power"),
      Map.entry("punch", "Punch"),
      Map.entry("flame", "Flame"),
      Map.entry("infinity", "Inf"),
      Map.entry("loyalty", "Loyal"),
      Map.entry("riptide", "Rip"),
      Map.entry("channeling", "Chan"),
      Map.entry("impaling", "Imp"),
      Map.entry("multishot", "Multi"),
      Map.entry("quick_charge", "Quick"),
      Map.entry("piercing", "Pierce"),
      Map.entry("density", "Dens"),
      Map.entry("breach", "Breach"),
      Map.entry("wind_burst", "Wind"),
      Map.entry("luck_of_the_sea", "Luck"),
      Map.entry("lure", "Lure"),
      Map.entry("vanishing_curse", "Vanish"),
      Map.entry("binding_curse", "Bind")
   );
   private static final String[] texts = new String[]{"_helmet", "_chestplate", "_leggings", "_boots"};
   private static final String text10 = "V";
   private static final String text11 = "H";
   public static final float CONST_floatValue22 = 6.5F;
   public static final float CONST_floatValue23 = 0.65F;
   public static final float CONST_floatValue24 = 10.4F;
   private static final float CONST_floatValue25 = 2.0F;

   public float getSize() {
      return this.scaleSetting.getValue3();
   }

   private float method552(EntityESPModule.Entry entry) {
      return entry != null && entry.getBox() != null ? this.method553(entry.getBox().getCenter()) : 1.0F;
   }

   public float method553(Vec3 center) {
      float min = this.tagMinScale.getValue3();
      if (!(min >= 0.999F) && center != null && mc.gameRenderer.mainCamera() != null) {
         Vec3 position = mc.gameRenderer.mainCamera().position();
         double d = Math.sqrt(center.distanceToSqr(position));
         return d <= 8.0 ? 1.0F : (float)Mth.clamp(Math.sqrt(8.0 / d), (double)min, 1.0);
      } else {
         return 1.0F;
      }
   }

   private static MultiSelectSetting method554(MultiSelectSetting multiSelectSetting, String... strings) {
      for (String key : strings) {
         MultiSelectSetting.Entry entry = multiSelectSetting.method5222(key);
         if (entry != null) {
            entry.setEnabled(false);
         }
      }

      return multiSelectSetting;
   }

   private ZenithColor method555(ZenithColor zenithColor) {
      return zenithColor != null && !(this.previewAlpha >= 0.999F) ? zenithColor.method7294(this.previewAlpha) : zenithColor;
   }

   private static <T extends Setting> T method556(T textSetting) {
      textSetting.setVisible(() -> false);
      return (T)textSetting;
   }

   private List<EntityESPModule.Type> method557() {
      String str = this.tagOrder.getValue();
      if (this.items3 == null || !Objects.equals(str, this.text9)) {
         this.text9 = str;
         this.items3 = method558(str);
      }

      return this.items3;
   }

   private static List<EntityESPModule.Type> method558(String str) {
      List<Type> coll = new ArrayList<>(EntityESPModule.Type.values().length);
      if (str != null) {
         for (String str2 : str.split(",")) {
            try {
               EntityESPModule.Type type = EntityESPModule.Type.valueOf(str2.strip().toUpperCase(Locale.ROOT));
               if (!coll.contains(type)) {
                  coll.add(type);
               }
            } catch (IllegalArgumentException e) {
            }
         }
      }

      for (EntityESPModule.Type type : EntityESPModule.Type.values()) {
         if (!coll.contains(type)) {
            coll.add(type);
         }
      }

      return List.copyOf(coll);
   }

   private float method559(EntityESPModule.Type type, float f) {
      Float f2 = this.items6.get(type);
      if (f2 != null && !(Math.abs(f2 - f) > 160.0F)) {
         float f3 = Mth.lerp(0.35F, f2, f);
         if (Math.abs(f3 - f) < 0.2F) {
            f3 = f;
         }

         this.items6.put(type, f3);
         return f3;
      } else {
         this.items6.put(type, f);
         return f;
      }
   }

   private EntityESPModule.Type method560(double doubleValue, double y) {
      if (!this.items4.isEmpty() && !Double.isNaN(doubleValue) && !Double.isNaN(y)) {
         float f = this.floatValue2 + (float)(doubleValue - this.floatValue2) / this.floatValue;
         float f2 = this.floatValue3 + (float)(y - this.floatValue3) / this.floatValue;

         for (Map.Entry entry : this.items4.entrySet()) {
            float[] floats = (float[])entry.getValue();
            if (f >= floats[0] && f <= floats[0] + floats[2] && f2 >= floats[1] && f2 <= floats[1] + 14.0F) {
               return (EntityESPModule.Type)entry.getKey();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public boolean method561(double x, double y, MouseButton enum2) {
      EntityESPModule.Type type = this.method560(x, y);
      if (type == null) {
         return false;
      } else if (enum2 == MouseButton.RIGHT) {
         if (type != EntityESPModule.Type.GEAR) {
            return false;
         } else {
            this.gearReversed.toggle();
            return true;
         }
      } else {
         this.type = type;
         return true;
      }
   }

   public boolean method562(double doubleValue, double mouseY) {
      if (this.type == null) {
         return false;
      } else {
         EntityESPModule.Type type = this.method560(doubleValue, mouseY);
         if (type != null && type != this.type) {
            var types = new ArrayList<>(this.method557());
            int indexOf = types.indexOf(this.type);
            int index = types.indexOf(type);
            if (indexOf >= 0 && index >= 0) {
               types.set(indexOf, type);
               types.set(index, this.type);
               this.method566(types);
            }
         }

         return true;
      }
   }

   public void method563() {
      this.type = null;
   }

   private void method564(CustomDrawContext customDrawContext, float f, float f2) {
      if (!(this.floatValue4 <= 0.01F)) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();

         for (Map.Entry entry : this.items4.entrySet()) {
            float[] floats = (float[])entry.getValue();
            boolean flag = entry.getKey() == this.type2;
            float f3 = Mth.lerp(0.25F, this.items5.getOrDefault(entry.getKey(), 0.0F), flag ? 1.0F : 0.35F);
            this.items5.put((EntityESPModule.Type)entry.getKey(), f3);
            float previewAlpha = f3 * this.floatValue4 * this.previewAlpha;
            if (!(previewAlpha <= 0.01F)) {
               float f4 = this.items7.getOrDefault(entry.getKey(), floats[0]);
               customDrawContext.drawRect(f4 + 3.5F, f + 14.0F - 1.0F, Math.max(2.0F, floats[2] - 3.5F), 0.8F, zenithColor.method7294(previewAlpha));
               this.method565(customDrawContext, f4 + floats[2] / 2.0F, f2 - 1.5F - 1.5F * f3, zenithColor.method7294(previewAlpha));
            }
         }
      }
   }

   private void method565(CustomDrawContext customDrawContext, float f, float f2, ZenithColor zenithColor) {
      float width = 0.8F;
      float f3 = 1.5F;

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 3; j++) {
            customDrawContext.drawRect(f - f3 / 2.0F - width / 2.0F + i * f3, f2 - (2 - j) * f3 - width, width, width, zenithColor);
         }
      }
   }

   private void method566(List<EntityESPModule.Type> types) {
      StringBuilder builder = new StringBuilder();

      for (EntityESPModule.Type type : types) {
         if (!builder.isEmpty()) {
            builder.append(',');
         }

         builder.append(type.name());
      }

      this.tagOrder.setValue(builder.toString());
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.ENTITY;
   }

   private EntityESPModule() {
   }

   public boolean method567() {
      return this.gradient.isEnabled();
   }

   public boolean method568() {
      return this.isEnabled() && this.elements.method5226("module.entityESP.names");
   }

   private void pushCenteredScale(CustomDrawContext customDrawContext, float x, float y, float f, float f2) {
      this.method569(customDrawContext, x, y, f, f2, 1.0F);
   }

   private void method569(CustomDrawContext customDrawContext, float x, float y, float f, float f2, float f3) {
      if (customDrawContext != null) {
         try {
            float f4 = this.scaleSetting.getValue3() * f3;
            customDrawContext.pushMatrix();
            customDrawContext.pose().translate(x, y);
            customDrawContext.pose().scale(f * f4, f2 * f4);
            customDrawContext.pose().translate(-x, -y);
         } catch (Exception e) {
            System.out.println("Строка  82 " + e.getMessage());
         }
      }
   }

   private boolean method570(float floatValue, float y, float f, float y2, float x, float y3, float f2) {
      float f3 = this.scaleSetting.getValue3() * f2;
      float f4 = x + (floatValue - 20.0F - x) * f3;
      float f5 = x + (f + 20.0F - x) * f3;
      float f6 = y3 + (y - 20.0F - y3) * f3;
      float f7 = y3 + (y2 + 20.0F - y3) * f3;
      return f5 < 0.0F || f7 < 0.0F || f4 > mc.getWindow().getGuiScaledWidth() || f6 > mc.getWindow().getGuiScaledHeight();
   }

   private void pop(CustomDrawContext customDrawContext) {
      if (customDrawContext != null) {
         try {
            customDrawContext.popMatrix();
         } catch (Exception e) {
            System.out.println("Строка  91 " + e.getMessage());
         }
      }
   }

   @EventTarget
   public void method571(WorldRenderEvent event) {
      boolean flag = this.elements.method5226("module.entityESP.boxes");
      boolean flag2 = this.elements.method5226("Tracers");
      if (flag || flag2) {
         try {
            Vec3 vec = flag2
               ? new Vec3(0.0, 0.0, 75.0)
                  .xRot(-((float)Math.toRadians(mc.gameRenderer.mainCamera().xRot())))
                  .yRot(-((float)Math.toRadians(mc.gameRenderer.mainCamera().yRot())))
                  .add(FreeCamModule.INSTANCE.method1660())
               : null;

            for (EntityESPModule.Data4 data4 : this.method574()) {
               EntityESPModule.Entry entry = data4.getEntry();
               if ((entry.isFlag2() || entry.isFlag3()) && (!entry.isFlag2() || !entry.isFlag7())) {
                  try {
                     if (flag) {
                        if (entry.isFlag()) {
                           GlowUtil.method2230(
                              entry.getBox(), Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(), 1.0F
                           );
                        } else {
                           GlowUtil.method2231(
                              entry.getBox(),
                              Zenith.getInstance().getStyleManager().getClientColor(0).method7294(0.7F).method7287(),
                              Zenith.getInstance().getStyleManager().getClientColor(180).method7287(),
                              1.0F
                           );
                        }
                     }

                     if (flag2) {
                        GlowUtil.method2245(vec, entry.getBox().getCenter(), Zenith.getInstance().getStyleManager().getClientColor(0).method7287(), 1.5F, false);
                     }
                  } catch (Exception e) {
                     System.out.println("ESP 3D render error: " + e.getMessage());
                  }
               }
            }
         } catch (Exception e) {
            System.out.println("ESP 3D loop error: " + e.getMessage());
         }
      }
   }

   @EventTarget
   public void method572(DrawEvent event) {
      if (mc.player != null && mc.level != null && mc.getEntityRenderDispatcher().camera != null) {
         CustomDrawContext customDrawContext = event.getCustomDrawContext();
         if (customDrawContext != null) {
            this.intValue++;
            this.vec3d = mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null ? this.method615() : null;

            try {
               ShapeRenderer.method7157(customDrawContext);
               QueuedCustomDrawContext customDrawContext2 = QueuedCustomDrawContext.of(customDrawContext);
               Jebriksheet.method2346(this.jebriksheet);

               try {
                  for (EntityESPModule.Data4 data4 : this.method574()) {
                     try {
                        EntityESPModule.Entry entry = data4.getEntry();
                        if (!entry.isFlag2() && !entry.isFlag3()) {
                           if (entry.isFlag4()) {
                              if (this.targets.method5226("module.entityESP.droppedItems")) {
                                 this.method607(customDrawContext2, entry);
                              }
                           } else if (entry.isFlag5() && this.elements.method5226("module.entityESP.names")) {
                              this.method585(customDrawContext2, entry);
                           }
                        } else {
                           if (this.elements.method5226("module.entityESP.hands")) {
                              Vector4d vector4d = this.method616(entry);
                              if (vector4d != null) {
                                 this.method583(customDrawContext2, entry, vector4d);
                              }
                           }

                           if (this.elements.method5226("module.entityESP.triangles")) {
                              this.method606(customDrawContext2, entry);
                           }

                           if (this.elements.method5226("module.entityESP.names")) {
                              this.method585(customDrawContext2, entry);
                           }
                        }
                     } catch (Exception e) {
                        System.out.println("ESP 2D render error: " + e.getMessage());
                     }
                  }

                  this.jebriksheet.flush();
               } finally {
                  Jebriksheet.method2347(this.jebriksheet);
               }
            } catch (Exception e) {
               System.out.println("Строка  145 " + e.getMessage());
            } finally {
               this.vec3d = null;
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      this.method576();
      if (++this.intValue2 >= 20) {
         this.intValue2 = 0;
         items10.clear();
      }
   }

   private EntityESPModule.Data3 method573(ItemStack itemStack) {
      EntityESPModule.Data2 data2 = EntityESPModule.Data2.method630(itemStack);
      EntityESPModule.Data3 data3 = items10.get(data2);
      if (data3 == null) {
         String text = this.method593(itemStack);
         if (text == null) {
            text = Names.method4516(itemStack);
         }

         data3 = new EntityESPModule.Data3(text, this.method594(itemStack));
         if (items10.size() > 512) {
            items10.clear();
         }

         items10.put(data2, data3);
      }

      return data3;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         this.method576();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items8.clear();
      this.items9 = List.of();
   }

   private List<EntityESPModule.Data4> method574() {
      if (mc.level == null) {
         if (!this.items9.isEmpty()) {
            this.items8.clear();
            this.items9 = List.of();
         }

         return this.items9;
      } else {
         long longValue = this.method575();
         if (longValue != this.longValue) {
            this.longValue = longValue;
            int n = 0;

            for (int i = this.items9.size(); n < i; n++) {
               this.items9.get(n).getEntry().method631();
            }
         }

         return this.items9;
      }
   }

   private long method575() {
      long gameTime = mc.level == null ? 0L : mc.level.getGameTime();
      long l = Float.floatToIntBits(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) & 4294967295L;
      return gameTime << 32 ^ l;
   }

   private void method576() {
      long now = System.currentTimeMillis();
      if (mc.level != null) {
         for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof Player player) {
               if (!PetManager.method4557(player.getId())) {
                  EntityESPModule.Entry entry = this.method577(player);
                  if (entry != null) {
                     this.items8.put(entry.key(), new EntityESPModule.Data4(entry, now));
                  }
               }
            } else if (entity instanceof Mob mob) {
               EntityESPModule.Entry entry = this.method578(mob);
               if (entry != null) {
                  this.items8.put(entry.key(), new EntityESPModule.Data4(entry, now));
               }
            } else if (entity instanceof PrimedTnt primedTnt) {
               EntityESPModule.Entry entry = this.method579(primedTnt);
               if (entry != null) {
                  this.items8.put(entry.key(), new EntityESPModule.Data4(entry, now));
               }
            } else if (entity instanceof ItemEntity itemEntity) {
               EntityESPModule.Entry entry = this.method580(itemEntity);
               if (entry != null) {
                  this.items8.put(entry.key(), new EntityESPModule.Data4(entry, now));
               }
            }
         }
      }

      this.method581(now);
      long l = (long)(this.removeHiddenPlayerDelay.getValue3() * 50.0F);
      this.items8.values().removeIf(var4x -> now - var4x.getLongValue() > (var4x.entry.flag2 ? l : 200L));
      this.items9 = new ArrayList<>(this.items8.values());
      this.longValue = Long.MIN_VALUE;
   }

   private EntityESPModule.Entry method577(Player player) {
      boolean flag = Zenith.getInstance().getFriends().method7627(player.getName().getString());
      if (flag && !this.targets.method5226("module.entityESP.friends")) {
         return null;
      } else {
         if (!flag) {
            boolean flag2 = UseItemUtils.method6529(player) == 0.0F;
            if (flag2 && !this.targets.method5226("module.entityESP.noArmor")) {
               return null;
            }

            if (!flag2 && !this.targets.method5226("module.entityESP.players")) {
               return null;
            }
         }

         Vec3 vec = MathUtils.method2656(player);
         AABB move = player.getBoundingBox().move(vec.subtract(player.position()));
         float floatValue = UseItemUtils.method6527(player);
         ArrayList items = new ArrayList();
         ArmorSlots.method5385(player).forEach(items::add);
         ArrayList<ItemStack> stacks = new ArrayList();
         stacks.add(player.getOffhandItem());
         stacks.addAll(ArmorSlots.method5384(player));
         stacks.add(player.getMainHandItem());

         for (ItemStack stack : stacks) {
            ItemIntegrityTracker.recordSuspiciousStack(player, stack);
         }

         return new EntityESPModule.Entry(
            "player:" + player.getStringUUID(),
            StreamerModeModule.INSTANCE.method6292(player.getDisplayName() != null ? player.getDisplayName() : player.getName(), player.getGameProfile().name()),
            flag,
            true,
            false,
            false,
            false,
            false,
            player == mc.player,
            move,
            floatValue,
            0,
            items,
            stacks,
            null,
            player,
            null
         );
      }
   }

   private EntityESPModule.Entry method578(Mob player) {
      if (!this.targets.method5226("module.entityESP.mobs")) {
         return null;
      } else {
         Vec3 vec = MathUtils.method2656(player);
         AABB move = player.getBoundingBox().move(vec.subtract(player.position()));
         float floatValue = UseItemUtils.method6527(player);
         return new EntityESPModule.Entry(
            "mob:" + player.getStringUUID(),
            player.getDisplayName() != null ? player.getDisplayName() : player.getName(),
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            move,
            floatValue,
            0,
            List.of(),
            List.of(),
            null,
            player,
            null
         );
      }
   }

   private EntityESPModule.Entry method579(PrimedTnt player) {
      if (!this.targets.method5226("TNT")) {
         return null;
      } else {
         Vec3 vec = MathUtils.method2656(player);
         AABB move = player.getBoundingBox().move(vec.subtract(player.position()));
         return new EntityESPModule.Entry(
            "tnt:" + player.getStringUUID(),
            player.getDisplayName(),
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            move,
            -1.0F,
            player.getFuse(),
            List.of(),
            List.of(),
            null,
            player,
            null
         );
      }
   }

   private EntityESPModule.Entry method580(ItemEntity player) {
      if (player.getItem().isEmpty()) {
         return null;
      } else {
         Names.method4515(player.getItem());
         if (!this.targets.method5226("module.entityESP.droppedItems")) {
            return null;
         } else {
            Vec3 vec = MathUtils.method2656(player);
            AABB move = player.getBoundingBox().move(vec.subtract(player.position()));
            return new EntityESPModule.Entry(
               "item:" + player.getStringUUID(),
               player.getItem().getCustomName() != null ? player.getItem().getCustomName() : player.getName(),
               false,
               false,
               false,
               true,
               false,
               false,
               false,
               move,
               -1.0F,
               0,
               List.of(),
               List.of(),
               player.getItem().copy(),
               player,
               null
            );
         }
      }
   }

   private void method581(long now) {
      this.items8.entrySet().removeIf(entry -> entry.getValue().getEntry().isFlag6());
      if (this.targets.method5226("module.entityESP.friends") && mc.level != null && mc.getConnection() != null) {
         Cloud cloud = Zenith.getInstance().getCloud();
         if (cloud != null && cloud.method7862()) {
            String anObject = cloud.method7911();
            String str = cloud.method7913();
            HashSet hashSet = null;

            for (PartyPlayerInfo partyPlayerInfo : cloud.state().method8038()) {
               if (partyPlayerInfo.method3291(anObject, str) && now - partyPlayerInfo.getLongValue2() <= 10000L) {
                  String text = partyPlayerInfo.getText2();
                  if (text != null && !text.isBlank() && !cloud.prefs().method3316(partyPlayerInfo.username())) {
                     if (hashSet == null) {
                        hashSet = new HashSet();

                        for (Player player : mc.level.players()) {
                           hashSet.add(player.getGameProfile().name().toLowerCase(Locale.ROOT));
                        }
                     }

                     if (!hashSet.contains(text.toLowerCase(Locale.ROOT))) {
                        Vec3 vec = partyPlayerInfo.method3294();
                        if (vec != null) {
                           AABB aabb = new AABB(vec.x - 0.3, vec.y, vec.z - 0.3, vec.x + 0.3, vec.y + 1.8, vec.z + 0.3);
                           EntityESPModule.Entry entry = new EntityESPModule.Entry(
                              "cloud-player:" + partyPlayerInfo.uid(),
                              Component.literal(text),
                              true,
                              true,
                              false,
                              false,
                              false,
                              true,
                              false,
                              aabb,
                              partyPlayerInfo.getFloatValue3(),
                              0,
                              List.of(),
                              method582(partyPlayerInfo),
                              null,
                              null,
                              partyPlayerInfo
                           );
                           this.items8.put(entry.key(), new EntityESPModule.Data4(entry, now));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static List<ItemStack> method582(PartyPlayerInfo partyPlayerInfo) {
      CloudPackets.Data7[] data7s = partyPlayerInfo.getData7s();
      if (data7s.length <= 40) {
         return List.of();
      } else {
         ArrayList arrayList = new ArrayList(5);
         arrayList.add(stackOf(data7s[40]));

         for (int i = 36; i <= 39; i++) {
            arrayList.add(stackOf(data7s[i]));
         }

         return arrayList;
      }
   }

   private static ItemStack stackOf(CloudPackets.Data7 data7) {
      if (data7 != null && !data7.isEmpty()) {
         Identifier tryParse = Identifier.tryParse(data7.getText());
         if (tryParse != null && BuiltInRegistries.ITEM.containsKey(tryParse)) {
            Item item = BuiltInRegistries.ITEM.getValue(tryParse);
            if (item == Items.AIR) {
               return ItemStack.EMPTY;
            } else {
               ItemStack stack = new ItemStack(item, Math.max(1, Math.min(127, data7.count())));
               if (data7.getIntValue2() > 0) {
                  stack.setDamageValue(data7.getIntValue2());
               }

               return stack;
            }
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private void method583(CustomDrawContext customDrawContext, EntityESPModule.Entry entry, Vector4d vector4d) {
      if (entry != null && customDrawContext != null && vector4d != null) {
         if (!entry.isFlag7() || !mc.options.getCameraType().isFirstPerson()) {
            if (!ProjectionUtils.method304(vector4d)) {
               if (!entry.getItems().isEmpty()) {
                  try {
                     float f = this.method552(entry);
                     float x = (float)ProjectionUtils.method305(vector4d);
                     ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
                     Font font = Fonts.NEW_MEDIUM.getFont(6.5F);
                     float panelHeight = font.height() + GuiStyle.PADDING.intValue() + 1.0F;
                     float f2 = this.scaleSetting.getValue3() * f;
                     float f3 = (panelHeight + 2.0F) * f2;
                     int n = 0;

                     for (ItemStack stack : entry.getItems()) {
                        if (stack != null && !stack.isEmpty()) {
                           n++;
                        }
                     }

                     float f4 = mc.getWindow().getGuiScaledHeight() - n * f3 - 2.0F;
                     double d = Math.min(vector4d.w + 2.0, (double)f4);
                     var items = entry.getItems();
                     EntityESPModule.Data[] datas = entry.datas;
                     if (datas == null || datas.length != items.size()) {
                        datas = entry.datas = new EntityESPModule.Data[items.size()];
                     }

                     for (int i = 0; i < items.size(); i++) {
                        ItemStack itemStack = (ItemStack)items.get(i);
                        if (itemStack != null && !itemStack.isEmpty()) {
                           try {
                              EntityESPModule.Data data = datas[i];
                              if (data == null || data.getItemStack() != itemStack) {
                                 data = datas[i] = this.method584(itemStack);
                              }

                              String text = data.getText2();
                              MutableComponent setStyle;
                              if (text != null) {
                                 setStyle = Component.literal(text)
                                    .setStyle(Style.EMPTY.withColor(Zenith.getInstance().getStyleManager().getClientColor(90).method7287()));
                              } else {
                                 setStyle = data.name().copy();
                              }

                              String str = data.getText3();
                              if (str != null) {
                                 setStyle.append(
                                    Component.nullToEmpty(" " + str).copy().setStyle(Style.EMPTY.withColor(currentStyle.getTextSecondary().getZenithColor().method7287()))
                                 );
                              }

                              boolean flag = !(entry.getEntity() instanceof Player player && !ItemIntegrityTracker.isStackCountValid(player));
                              if ((!Zenith.getInstance().getServerDetector().method1642() || entry.flag7) && flag && itemStack.getCount() > 1) {
                                 setStyle.append(
                                    Component.nullToEmpty(" x" + itemStack.getCount())
                                       .copy()
                                       .setStyle(Style.EMPTY.withColor(currentStyle.getPrimaryColor().getZenithColor().method7287()))
                                 );
                              }

                              ZenithColor zenithColor = currentStyle.getHudBackground().getZenithColor();
                              float width = 19.5F + font.width(setStyle);
                              float floatValue = x - width / 2.0F;
                              float y = (float)d;
                              d += f3;
                              if (!this.method570(floatValue, y, floatValue + width, y + panelHeight, x, y, f)) {
                                 this.method569(customDrawContext, x, y, 1.0F, 1.0F, f);
                                 ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
                                 customDrawContext.drawBlurHudBooleanCheck(
                                    floatValue,
                                    y,
                                    width,
                                    panelHeight,
                                    22.0F,
                                    moduleMenuRadius,
                                    ZenithColor.INSTANCE,
                                    this.blur.isEnabled() && this.blur.isVisible(),
                                    this.glow.isEnabled() && this.glow.isVisible()
                                 );
                                 customDrawContext.drawRoundedRect(floatValue, y, width, panelHeight, moduleMenuRadius, zenithColor);
                                 customDrawContext.pushMatrix();
                                 customDrawContext.pose().translate(floatValue + 4.0F, y + (panelHeight - 9.0F) / 2.0F);
                                 customDrawContext.pose().scale(0.5625F, 0.5625F);
                                 customDrawContext.item(itemStack, 0, 0);
                                 customDrawContext.popMatrix();
                                 customDrawContext.pushMatrix();
                                 customDrawContext.pose().translate(floatValue + 4.0F + 9.0F + 2.5F, y + (panelHeight - 1.0F - font.height()) / 2.0F);
                                 customDrawContext.drawText(font, setStyle, 0.0F, 0.0F, currentStyle.getTextEnable().getZenithColor().method7287());
                                 customDrawContext.popMatrix();
                                 this.pop(customDrawContext);
                              }
                           } catch (Exception e) {
                              System.out.println("drawHands err: " + e.getMessage());
                           }
                        }
                     }
                  } catch (Exception e) {
                     System.out.println("drawHands outer err: " + e.getMessage());
                  }
               }
            }
         }
      }
   }

   private EntityESPModule.Data method584(ItemStack itemStack) {
      EntityESPModule.Data3 data3 = this.method573(itemStack);
      String text = data3.getText();
      MutableComponent component = null;
      if (text == null) {
         MutableComponent text2 = itemStack.getHoverName().copy();
         String string = text2.getString();
         if (!string.isEmpty() && string.endsWith(" ")) {
            text2 = TextUtils.method2116(text2, "");
         }

         component = text2;
      }

      return new EntityESPModule.Data(itemStack, component, text, data3.getText2());
   }

   private void method585(CustomDrawContext customDrawContext, EntityESPModule.Entry entry) {
      if (entry != null && customDrawContext != null) {
         if (!entry.isFlag7() || !mc.options.getCameraType().isFirstPerson()) {
            try {
               AABB box = entry.getBox();
               Vec3 vec = new Vec3(box.getCenter().x, box.maxY + 0.15F, box.getCenter().z);
               if (!this.method614(vec)) {
                  return;
               }

               Vec3 vec2 = ProjectionUtils.method301(vec);
               if (vec2 == null || vec2.z <= 0.0) {
                  return;
               }

               ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
               boolean flag = entry.isFlag();
               Component name = entry.name() != null ? entry.name() : Component.nullToEmpty("unknown");
               Object literal = (!flag || !NameProtectModule.INSTANCE.isEnabled() || !NameProtectModule.INSTANCE.method5140()) && !entry.flag7
                  ? name
                  : Component.literal(NameProtectModule.method5141());
               float floatValue = entry.getFloatValue();
               if (!entry.flag10) {
                  entry.text4 = entry.isFlag5() ? this.method618(entry.getIntValue()) : (floatValue >= 0.0F ? UseItemUtils.method6532(floatValue) : null);
                  entry.flag10 = true;
               }

               String str = entry.text4;
               ZenithColor zenithColor = entry.isFlag5()
                  ? currentStyle.getPrimaryColor().getZenithColor()
                  : (floatValue >= 0.0F ? this.method617(floatValue) : currentStyle.getTextEnable().getZenithColor());
               ZenithColor zenithColor2 = flag ? currentStyle.getFriendColor().getZenithColor().method7296(0.5F) : currentStyle.getHudBackground().getZenithColor();
               float f = this.method552(entry);
               this.method587(
                  customDrawContext,
                  (float)vec2.x,
                  (float)vec2.y - 1.0F,
                  (Component)literal,
                  this.method604(entry),
                  str,
                  zenithColor,
                  this.elements.method5226("module.entityESP.ping") ? this.method605(entry) : null,
                  this.elements.method5226("module.entityESP.armor") ? this.method603(method602(entry.getItems2())) : List.of(),
                  zenithColor2,
                  f,
                  this.elements.method5226("module.entityESP.armorText") ? this.method600(entry) : null
               );
            } catch (Exception e) {
               System.out.println("renderNameTag err: " + e.getMessage());
            }
         }
      }
   }

   private float method586(
      CustomDrawContext customDrawContext,
      float x,
      float y,
      Component literal,
      Identifier texturePath,
      String str,
      ZenithColor zenithColor,
      String str2,
      List<ItemStack> stacks,
      ZenithColor zenithColor2
   ) {
      return this.method587(customDrawContext, x, y, literal, texturePath, str, zenithColor, str2, stacks, zenithColor2, 1.0F, null);
   }

   private float method587(
      CustomDrawContext customDrawContext,
      float x,
      float y,
      Component literal,
      Identifier texturePath,
      String str,
      ZenithColor zenithColor,
      String str2,
      List<ItemStack> stacks,
      ZenithColor zenithColor2,
      float f,
      Component text
   ) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font font = Fonts.NEW_MEDIUM.getFont(6.5F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font font3 = Fonts.NEW_ICONS.getFont(5.0F);
      List<EntityESPModule.Type> types = this.method557();
      float width = 4.5F;
      if (texturePath != null) {
         width += 9.0F + GuiStyle.PADDING.intValue() * 0.75F;
      }

      width += font.width(literal);

      for (EntityESPModule.Type type : types) {
         width += this.method589(type, font3, font2, str, str2, stacks);
      }

      width += 4.5F;
      float floatValue = x - width / 2.0F;
      float y2 = y - 14.0F;
      if (!this.flag) {
         float f2 = width / 2.0F;
         float y3 = y2;
         if (text != null) {
            f2 = Math.max(f2, (Fonts.NEW_MEDIUM.getFont(5.5F).width(text) + 7.0F) / 2.0F);
            y3 = y2 - 1.5F - 9.5F;
         }

         if (this.method570(x - f2, y3, x + f2, y, x, y, f)) {
            float f3 = text == null ? 14.0F : 25.0F;
            return y - f3 * this.scaleSetting.getValue3() * f;
         }
      }

      this.method569(customDrawContext, x, y, 1.0F, 1.0F, f);
      if (this.flag) {
         this.floatValue = Math.max(1.0E-4F, this.scaleSetting.getValue3() * f);
         this.floatValue2 = x;
         this.floatValue3 = y;
      }

      if (text != null) {
         Font font4 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float width2 = font4.width(text) + 7.0F;
         float floatValue2 = x - width2 / 2.0F;
         float y3 = y2 - 1.5F - 9.5F;
         ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 4.75F));
         customDrawContext.drawBlurHudBooleanCheck(
            floatValue2, y3, width2, 9.5F, 22.0F, moduleMenuRadius, ZenithColor.INSTANCE, this.method627(), this.glow.isEnabled() && this.glow.isVisible()
         );
         customDrawContext.drawRoundedRect(floatValue2, y3, width2, 9.5F, moduleMenuRadius, zenithColor2);
         customDrawContext.drawText(font4, text, floatValue2 + 3.5F, y3 + (9.5F - font4.height()) / 2.0F, this.method555(ZenithColor.INSTANCE).method7287());
      }

      ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 7.0F));
      customDrawContext.drawBlurHudBooleanCheck(
         floatValue, y2, width, 14.0F, 22.0F, moduleMenuRadius, this.method555(ZenithColor.INSTANCE), this.method627(), this.glow.isEnabled() && this.glow.isVisible()
      );
      customDrawContext.drawRoundedRect(floatValue, y2, width, 14.0F, moduleMenuRadius, this.method555(zenithColor2));
      float floatValue2 = floatValue + 4.5F;
      if (texturePath != null) {
         customDrawContext.drawPlayerHeadWithRoundedShader(texturePath, floatValue2, y2 + 2.5F, 9.0F, ProxyDotRadiusData.method8069(4.5F), this.method555(ZenithColor.INSTANCE));
         floatValue2 += 9.0F + GuiStyle.PADDING.intValue() * 0.75F;
      }

      customDrawContext.drawText(font, literal, floatValue2, y2 + (14.0F - font.height()) / 2.0F, this.method555(currentStyle.getTextEnable().getZenithColor()).method7287());
      floatValue2 += font.width(literal);
      if (this.flag) {
         this.items4.clear();
      }

      float f2 = floatValue2;

      for (EntityESPModule.Type type : types) {
         float f3 = this.method589(type, font3, font2, str, str2, stacks);
         if (!(f3 <= 0.0F)) {
            float floatValue3 = floatValue2;
            if (this.flag) {
               floatValue3 = f2 + this.method559(type, floatValue2 - f2);
               this.items7.put(type, floatValue3);
            }

            switch (type) {
               case HP:
                  this.method591(customDrawContext, floatValue3, y2, font3, font2, "V", str, zenithColor, currentStyle);
                  break;
               case PING:
                  this.method591(customDrawContext, floatValue3, y2, font3, font2, "H", str2, currentStyle.getTextSecondary().getZenithColor(), currentStyle);
                  break;
               case GEAR:
                  this.method588(customDrawContext, floatValue3, y2, stacks, currentStyle);
            }

            if (this.flag) {
               this.items4.put(type, new float[]{floatValue2, y2, f3});
            }

            floatValue2 += f3;
         }
      }

      if (this.flag) {
         this.type2 = this.type != null ? this.type : this.method560(this.doubleValue, this.doubleValue2);
         this.floatValue4 = Mth.lerp(0.25F, this.floatValue4, this.type2 != null ? 1.0F : 0.0F);
         this.method564(customDrawContext, y2, text == null ? y2 : y2 - 1.5F - 9.5F);
      }

      this.pop(customDrawContext);
      float f3 = text == null ? 14.0F : 25.0F;
      return y - f3 * this.scaleSetting.getValue3() * f;
   }

   private float method588(CustomDrawContext customDrawContext, float x, float f, List<ItemStack> stacks, ZenithStyle currentStyle) {
      x += 3.5F;
      this.method592(customDrawContext, x, f, currentStyle);
      x += 4.5F;
      boolean flag = this.previewAlpha < 0.999F;
      if (flag) {
         RenderState.method1883(1.0F, 1.0F, 1.0F, this.previewAlpha);
      }

      try {
         for (ItemStack itemStack : stacks) {
            customDrawContext.pushMatrix();
            customDrawContext.pose().translate(x, f + 1.5F);
            customDrawContext.pose().scale(0.6875F, 0.6875F);
            customDrawContext.item(itemStack, 0, 0);
            customDrawContext.popMatrix();
            x += 11.0F;
         }
      } finally {
         if (flag) {
            RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      return x;
   }

   private float method589(EntityESPModule.Type type, Font font, Font font2, String str, String str2, List<ItemStack> stacks) {
      return switch (type) {
         case HP -> str == null ? 0.0F : this.method590(font, font2, "V", str);
         case PING -> str2 == null ? 0.0F : this.method590(font, font2, "H", str2);
         case GEAR -> stacks.isEmpty() ? 0.0F : 8.0F + stacks.size() * 11.0F;
      };
   }

   private float method590(Font font, Font font2, String text, String str) {
      return 8.0F + font.width(text) + 2.0F + font2.width(str);
   }

   private float method591(CustomDrawContext customDrawContext, float floatValue, float f, Font font, Font font2, String text, String str, ZenithColor zenithColor, ZenithStyle currentStyle) {
      floatValue += 3.5F;
      this.method592(customDrawContext, floatValue, f, currentStyle);
      floatValue += 4.5F;
      customDrawContext.drawText(font, text, floatValue, f + (14.0F - font.height()) / 2.0F, this.method555(zenithColor));
      floatValue += font.width(text) + 2.0F;
      customDrawContext.drawText(font2, str, floatValue, f + (14.0F - font2.height()) / 2.0F, this.method555(zenithColor));
      return floatValue + font2.width(str);
   }

   private void method592(CustomDrawContext customDrawContext, float x, float f, ZenithStyle currentStyle) {
      float height = 7.0F;
      customDrawContext.drawRect(x, f + (14.0F - height) / 2.0F, 1.0F, height, this.method555(currentStyle.getTextTertiary().getZenithColor().method7294(0.45F)));
   }

   private String method593(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
         if (customData == null) {
            return null;
         } else {
            CompoundTag copyTag = customData.copyTag();
            if (!copyTag.contains("itemServiceId")) {
               return null;
            } else {
               String str = copyTag.getCompoundOrEmpty("itemServiceId").getStringOr("name", "").replace("_", " ");
               if (str.isEmpty()) {
                  return null;
               } else {
                  if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
                     str = "Талисман Eternity";
                  }

                  str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
                  str = str.replace("2", " 2").replace("1", " 1");
                  if (str.equals("Мифическая сфера 1")) {
                     str = "Мифка у3 б2";
                  } else if (str.equals("Мифическая сфера 2")) {
                     str = "Мифка б3 у2";
                  }

                  return str;
               }
            }
         }
      } else {
         return null;
      }
   }

   private String method594(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemEnchantments itemEnchantments = itemStack.get(DataComponents.ENCHANTMENTS);
         if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
            ArrayList<Holder> holders = new ArrayList<>(itemEnchantments.keySet());
            holders.sort(Comparator.<Holder>comparingInt(e -> itemEnchantments.getLevel(e)).reversed());
            StringBuilder builder = new StringBuilder();
            int n = 0;

            for (Holder enchantment : holders) {
               if (n == 4) {
                  builder.append(" +").append(holders.size() - n);
                  break;
               }

               if (n > 0) {
                  builder.append(' ');
               }

               builder.append(method597(enchantment)).append(itemEnchantments.getLevel(enchantment));
               n++;
            }

            for (String str : method595(itemStack)) {
               builder.append(' ').append(str);
            }

            return builder.isEmpty() ? null : builder.toString();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static List<String> method595(ItemStack itemStack) {
      ArrayList<String> strings = new ArrayList<>();

      for (String str : Names.method4519(itemStack)) {
         int lastIndexOf = str.lastIndexOf(58);
         if (lastIndexOf > 0) {
            String str2 = str.substring(0, lastIndexOf);
            if (!str2.startsWith("minecraft:")) {
               int n = 1;

               try {
                  n = Integer.parseInt(str.substring(lastIndexOf + 1));
               } catch (NumberFormatException e) {
               }

               strings.add(method596(str2) + (n > 1 ? String.valueOf(n) : ""));
            }
         }
      }

      return strings;
   }

   private static String method596(String str) {
      String str2 = str.contains(":") ? str.substring(str.indexOf(58) + 1) : str;
      if (str2.endsWith("-enchant-custom")) {
         str2 = str2.substring(0, str2.length() - "-enchant-custom".length());
      }

      String str3 = items11.get(str2);
      if (str3 != null) {
         return str3;
      } else {
         str2 = str2.replace('-', ' ').replace('_', ' ');
         return str2.isEmpty() ? "?" : Character.toUpperCase(str2.charAt(0)) + str2.substring(1);
      }
   }

   private static String method597(Holder<Enchantment> enchantment) {
      String str = enchantment.unwrapKey().map(var0x -> var0x.identifier().getPath()).orElse("?");
      String str2 = items12.get(str);
      if (str2 != null) {
         return str2;
      } else {
         String str3 = str.replace("_", "");
         String str4 = str3.length() <= 4 ? str3 : str3.substring(0, 4);
         return str4.isEmpty() ? "?" : Character.toUpperCase(str4.charAt(0)) + str4.substring(1);
      }
   }

   private String method598(ItemStack itemStack) {
      Component component = itemStack.get(DataComponents.CUSTOM_NAME);
      if (component == null) {
         return null;
      } else {
         String str = component.getString().replaceAll("§.", "").trim();
         return str.isEmpty() ? null : str;
      }
   }

   private static String method599(ItemStack itemStack) {
      String path = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();

      for (String text : texts) {
         if (path.endsWith(text)) {
            path = path.substring(0, path.length() - text.length());
            break;
         }
      }

      path = path.replace('_', ' ').trim();
      return path.isEmpty() ? null : Character.toUpperCase(path.charAt(0)) + path.substring(1);
   }

   private Component method600(EntityESPModule.Entry entry) {
      if (entry != null && entry.isFlag2()) {
         if (!entry.flag8) {
            this.method601(entry);
            entry.flag8 = true;
         }

         if (entry.text3 == null) {
            return null;
         } else {
            ZenithColor zenithColor = !entry.flag9
               ? Zenith.getInstance().getStyleManager().getCurrentStyle().getTextSecondary().getZenithColor()
               : Zenith.getInstance().getStyleManager().getClientColor(90);
            return Component.literal(entry.text3).setStyle(Style.EMPTY.withColor(zenithColor.method7287()));
         }
      } else {
         return null;
      }
   }

   private void method601(EntityESPModule.Entry entry) {
      entry.text3 = null;
      entry.flag9 = false;
      var stacks = entry.getItems2();
      if (stacks != null && stacks.size() >= 5) {
         LinkedHashSet linkedHashSet = new LinkedHashSet();
         LinkedHashSet linkedHashSet2 = new LinkedHashSet();

         for (int i = 1; i <= 4; i++) {
            ItemStack itemStack = (ItemStack)stacks.get(i);
            if (itemStack != null && !itemStack.isEmpty()) {
               String str = this.method593(itemStack);
               if (str == null) {
                  str = this.method598(itemStack);
               }

               if (str == null) {
                  str = Names.method4516(itemStack);
               }

               if (str != null) {
                  linkedHashSet.add(str);
               } else {
                  String str2 = method599(itemStack);
                  if (str2 != null) {
                     linkedHashSet2.add(str2);
                  }
               }
            }
         }

         LinkedHashSet<String> strings = linkedHashSet.isEmpty() ? linkedHashSet2 : linkedHashSet;
         if (!strings.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int n = 0;

            for (String str : strings) {
               if (n == 2) {
                  builder.append(" …");
                  break;
               }

               if (n > 0) {
                  builder.append(" · ");
               }

               builder.append(str);
               n++;
            }

            int n2 = 0;

            for (int i = 1; i <= 4; i++) {
               ItemStack stack = (ItemStack)stacks.get(i);
               if (stack != null && !stack.isEmpty()) {
                  ItemEnchantments itemEnchantments = stack.get(DataComponents.ENCHANTMENTS);
                  if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
                     for (Holder enchantment : itemEnchantments.keySet()) {
                        if (enchantment.unwrapKey().map(obj -> ((net.minecraft.resources.ResourceKey<?>)obj).identifier().getPath()).orElse("").equals("protection")) {
                           n2 += itemEnchantments.getLevel(enchantment);
                        }
                     }
                  }
               }
            }

            if (n2 > 0) {
               builder.append(" · Prot").append(n2);
            }

            entry.text3 = builder.toString();
            entry.flag9 = !linkedHashSet.isEmpty();
         }
      }
   }

   private static List<ItemStack> method602(List<ItemStack> stacks) {
      return stacks != null && stacks.size() >= 5 ? stacks.subList(1, 5) : List.of();
   }

   private List<ItemStack> method603(List<ItemStack> stacks) {
      ArrayList<ItemStack> list = new ArrayList<>();

      for (ItemStack stack : stacks) {
         if (stack != null && !stack.isEmpty()) {
            list.add(stack);
         }
      }

      if (this.gearReversed.isEnabled()) {
         Collections.reverse(list);
      }

      return list;
   }

   private Identifier method604(EntityESPModule.Entry entry) {
      if (entry.getEntity() instanceof AbstractClientPlayer player && player.getSkin() != null) {
         return player.getSkin().body().texturePath();
      } else {
         return entry.isFlag2() ? DefaultPlayerSkin.getDefaultSkin().body().texturePath() : null;
      }
   }

   private String method605(EntityESPModule.Entry entry) {
      if (entry.isFlag2() && mc.getConnection() != null) {
         if (entry.getEntity() instanceof Player player) {
            PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(player.getUUID());
            if (playerInfo == null) {
               return null;
            } else {
               int n = Math.max(0, playerInfo.getLatency());
               if (entry.pingText == null || entry.intValue2 != n) {
                  entry.intValue2 = n;
                  entry.pingText = n + "ms";
               }

               return entry.pingText;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void method606(CustomDrawContext customDrawContext, EntityESPModule.Entry entry) {
      if (entry != null && customDrawContext != null) {
         if (!entry.isFlag7() || !mc.options.getCameraType().isFirstPerson()) {
            try {
               Vector4d vector4d = this.method616(entry);
               if (vector4d == null) {
                  return;
               }

               this.method610(customDrawContext, entry.isFlag(), vector4d);
            } catch (Exception e) {
               System.out.println("renderEntityBox err: " + e.getMessage());
            }
         }
      }
   }

   private void method607(CustomDrawContext customDrawContext, EntityESPModule.Entry entry) {
      if (entry != null && customDrawContext != null && entry.getItemStack() != null && !entry.getItemStack().isEmpty()) {
         try {
            Vec3[] vecs = this.method612(entry.getBox());
            Vector4d vector4d = null;

            for (Vec3 vec : vecs) {
               try {
                  if (this.method614(vec)) {
                     Vec3 vec2 = ProjectionUtils.method301(vec);
                     if (vec2 != null && vec2.z > 0.0) {
                        if (vector4d == null) {
                           vector4d = new Vector4d(vec2.x, vec2.y, vec2.x, vec2.y);
                        }

                        vector4d.x = Math.min(vector4d.x, vec2.x);
                        vector4d.y = Math.min(vector4d.y, vec2.y);
                        vector4d.z = Math.max(vector4d.z, vec2.x);
                        vector4d.w = Math.max(vector4d.w, vec2.y);
                     }
                  }
               } catch (Exception e) {
               }
            }

            if (vector4d == null) {
               return;
            }

            MutableComponent text = entry.getItemStack().getHoverName().copy();
            if (text.getString().endsWith(" ")) {
               text = TextUtils.method2116(text, "");
            }

            if (entry.getItemStack().getCount() > 1) {
               text = text.copy()
                  .append(
                     Component.nullToEmpty(" x" + entry.getItemStack().getCount())
                        .copy()
                        .setStyle(
                           Style.EMPTY.withColor(Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287())
                        )
                  );
            }

            float x = (float)vector4d.x;
            float z = (float)vector4d.z;
            float y = (float)vector4d.y;
            float f = this.method552(entry);
            float y2 = y - 2.0F * this.scaleSetting.getValue3() * f;
            this.method609(customDrawContext, x + (z - x) / 2.0F, y2, entry.getItemStack(), text, f);
         } catch (Exception e) {
            System.out.println("renderItemTarget err: " + e.getMessage());
         }
      }
   }

   public static float method608() {
      return Math.max(Fonts.NEW_MEDIUM.getFont(6.5F).height(), 10.4F) + GuiStyle.PADDING.intValue();
   }

   public void method609(CustomDrawContext customDrawContext, float x, float y, ItemStack itemStack, Component text, float f) {
      if (customDrawContext != null && text != null) {
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font font = Fonts.NEW_MEDIUM.getFont(6.5F);
         boolean flag = itemStack != null && !itemStack.isEmpty();
         float intValue = GuiStyle.PADDING.intValue();
         float panelHeight = method608();
         float width = intValue + (flag ? 10.4F + intValue : 0.0F) + font.width(text) + intValue;
         float floatValue = x - width / 2.0F;
         float y2 = y - panelHeight;
         if (!this.method570(floatValue, y2, floatValue + width, y, x, y, f)) {
            this.method569(customDrawContext, x, y, 1.0F, 1.0F, f);
            ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() / 2.0F);
            customDrawContext.drawBlurHudBooleanCheck(
               floatValue,
               y2,
               width,
               panelHeight,
               22.0F,
               moduleMenuRadius,
               ZenithColor.INSTANCE,
               this.blur.isEnabled() && this.blur.isVisible(),
               this.glow.isEnabled() && this.glow.isVisible()
            );
            customDrawContext.drawRoundedRect(floatValue, y2, width, panelHeight, moduleMenuRadius, currentStyle.getHudBackground().getZenithColor());
            customDrawContext.drawText(
               font,
               text,
               floatValue + intValue + (flag ? 10.4F + intValue : 0.0F),
               y2 + (panelHeight - font.height()) / 2.0F,
               currentStyle.getTextEnable().getZenithColor().method7287()
            );
            if (flag) {
               customDrawContext.pushMatrix();
               customDrawContext.pose().translate(floatValue + intValue, y2 + (panelHeight - 10.4F) / 2.0F);
               customDrawContext.pose().scale(0.65F, 0.65F);
               customDrawContext.item(itemStack, 0, 0);
               customDrawContext.popMatrix();
            }

            this.pop(customDrawContext);
         }
      }
   }

   private void method610(CustomDrawContext customDrawContext, boolean flag, Vector4d vector4d) {
      if (vector4d != null) {
         try {
            int[] ints = flag
               ? new int[]{
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(),
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(),
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287(),
                  Zenith.getInstance().getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287()
               }
               : new int[]{
                  Zenith.getInstance().getStyleManager().getClientColor(0).method7287(),
                  Zenith.getInstance().getStyleManager().getClientColor(90).method7287(),
                  Zenith.getInstance().getStyleManager().getClientColor(180).method7287(),
                  Zenith.getInstance().getStyleManager().getClientColor(270).method7287()
               };
            float x = (float)vector4d.x;
            float y = (float)vector4d.y;
            float z = (float)vector4d.z;
            float f = (float)vector4d.w;
            float width = (z - x) / 3.0F;
            customDrawContext.drawRect(x - 1.0F, y - 1.0F, width, 1.0F, this.method555(ZenithColor.method7291(ints[0])));
            customDrawContext.drawRect(x - 1.0F, y, 1.0F, width + 1.0F, this.method555(ZenithColor.method7291(ints[0])));
            customDrawContext.drawRect(x - 1.0F, f - width - 1.0F, 1.0F, width, this.method555(ZenithColor.method7291(ints[1])));
            customDrawContext.drawRect(x - 1.0F, f - 1.0F, width, 1.0F, this.method555(ZenithColor.method7291(ints[1])));
            customDrawContext.drawRect(z - width + 2.0F, y - 1.0F, width, 1.0F, this.method555(ZenithColor.method7291(ints[2])));
            customDrawContext.drawRect(z + 1.0F, y, 1.0F, width + 1.0F, this.method555(ZenithColor.method7291(ints[2])));
            customDrawContext.drawRect(z + 1.0F, f - width - 1.0F, 1.0F, width, this.method555(ZenithColor.method7291(ints[3])));
            customDrawContext.drawRect(z - width + 2.0F, f - 1.0F, width, 1.0F, this.method555(ZenithColor.method7291(ints[3])));
         } catch (Exception e) {
            System.out.println("drawFlatBox err: " + e.getMessage());
         }
      }
   }

   private Vector4d method611(AABB box) {
      Vec3[] vecs = this.method612(box);
      Vector4d vector4d = null;

      for (Vec3 vec : vecs) {
         try {
            if (this.method614(vec)) {
               Vec3 vec2 = ProjectionUtils.method301(vec);
               if (vec2 != null && vec2.z > 0.0) {
                  if (vector4d == null) {
                     vector4d = new Vector4d(vec2.x, vec2.y, vec2.x, vec2.y);
                  }

                  vector4d.x = Math.min(vector4d.x, vec2.x);
                  vector4d.y = Math.min(vector4d.y, vec2.y);
                  vector4d.z = Math.max(vector4d.z, vec2.x);
                  vector4d.w = Math.max(vector4d.w, vec2.y);
               }
            }
         } catch (Exception e) {
         }
      }

      return vector4d;
   }

   private Vec3[] method612(AABB box) {
      try {
         return new Vec3[]{
            new Vec3(box.minX, box.minY, box.minZ),
            new Vec3(box.minX, box.maxY, box.minZ),
            new Vec3(box.maxX, box.minY, box.minZ),
            new Vec3(box.maxX, box.maxY, box.minZ),
            new Vec3(box.minX, box.minY, box.maxZ),
            new Vec3(box.minX, box.maxY, box.maxZ),
            new Vec3(box.maxX, box.minY, box.maxZ),
            new Vec3(box.maxX, box.maxY, box.maxZ)
         };
      } catch (Exception e) {
         System.out.println("getPoints err: " + e.getMessage());
         return new Vec3[8];
      }
   }

   private float method613(AABB aabb) {
      return (float)aabb.getYsize();
   }

   private boolean method614(Vec3 vec) {
      if (mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null) {
         Vec3 position = mc.gameRenderer.mainCamera().position();
         Vec3 vec3d = this.vec3d;
         if (vec3d == null) {
            vec3d = this.method615();
         }

         return (vec.x - position.x) * vec3d.x + (vec.y - position.y) * vec3d.y + (vec.z - position.z) * vec3d.z > 0.0;
      } else {
         return false;
      }
   }

   private Vec3 method615() {
      float xRot = mc.gameRenderer.mainCamera().xRot();
      float yRot = mc.gameRenderer.mainCamera().yRot();
      double d = Math.toRadians(xRot);
      double d2 = Math.toRadians(yRot);
      return new Vec3(-Math.sin(d2) * Math.cos(d), -Math.sin(d), Math.cos(d2) * Math.cos(d));
   }

   private Vector4d method616(EntityESPModule.Entry entry) {
      if (entry.intValue3 != this.intValue || entry.box2 != entry.getBox()) {
         entry.intValue3 = this.intValue;
         entry.box2 = entry.getBox();
         entry.vector4d = this.method611(entry.getBox());
      }

      return entry.vector4d;
   }

   private ZenithColor method617(float floatValue) {
      try {
         if (floatValue <= 7.0F) {
            return new ZenithColor(255, 0, 0, 255);
         } else {
            return floatValue <= 15.0F ? new ZenithColor(255, 255, 0, 255) : new ZenithColor(0, 255, 0, 255);
         }
      } catch (Exception e) {
         return ZenithColor.INSTANCE;
      }
   }

   private String method618(int intValue) {
      return String.format("%.1fs", Math.max(intValue, 0) / 20.0F);
   }

   public void method619(CustomDrawContext customDrawContext, float xo, float yo, float f, float y, String text, float previewAlpha, double doubleValue, double mouseY) {
      if (customDrawContext != null) {
         this.previewAlpha = Math.clamp(previewAlpha, 0.0F, 1.0F);
         this.doubleValue = doubleValue;
         this.doubleValue2 = mouseY;
         if (!this.elements.method5226("module.entityESP.names")) {
            this.items4.clear();
            this.items6.clear();
            this.items7.clear();
            this.type = null;
         }

         try {
            float f2 = 1.8F * f;
            float f3 = 0.3F * f;
            float y2 = yo - f2;
            Vector4d vector4d = new Vector4d(xo - f3, y2, xo + f3, yo);
            if (this.elements.method5226("Tracers")) {
               this.method626(
                  customDrawContext, xo, y, xo, yo - f2 / 2.0F, 1.2F, this.method555(Zenith.getInstance().getStyleManager().getClientColor(0).method7294(0.9F))
               );
            }

            if (this.elements.method5226("module.entityESP.boxes")) {
               this.method625(customDrawContext, xo, yo, f);
            }

            if (this.elements.method5226("module.entityESP.triangles")) {
               this.method610(customDrawContext, false, vector4d);
            }

            if (this.elements.method5226("module.entityESP.hands")) {
               this.method624(customDrawContext, xo, yo);
            }

            if (this.elements.method5226("module.entityESP.names")) {
               this.method621(customDrawContext, xo, y2 - this.method620(f), text);
            }
         } catch (Exception e) {
            System.out.println("ESP preview error: " + e.getMessage());
         } finally {
            this.previewAlpha = 1.0F;
         }
      }
   }

   public float method620(float f) {
      return 0.15F * f + 1.0F;
   }

   private float method621(CustomDrawContext customDrawContext, float xo, float y, String text) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.flag = true;

      float f;
      try {
         f = this.method587(
            customDrawContext,
            xo,
            y,
            Component.literal(text),
            this.method623(),
            UseItemUtils.method6532(20.0F),
            this.method617(20.0F),
            this.elements.method5226("module.entityESP.ping") ? "12ms" : null,
            this.elements.method5226("module.entityESP.armor") ? this.method603(method602(this.method628())) : List.of(),
            currentStyle.getHudBackground().getZenithColor(),
            1.0F,
            this.elements.method5226("module.entityESP.armorText") ? this.method622() : null
         );
      } finally {
         this.flag = false;
      }

      return f;
   }

   private Component method622() {
      ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getCurrentStyle().getTextSecondary().getZenithColor();
      return Component.literal("Diamond · Prot16").setStyle(Style.EMPTY.withColor(zenithColor.method7287()));
   }

   private Identifier method623() {
      LocalPlayer player = mc.player;
      return player instanceof AbstractClientPlayer && player.getSkin() != null
         ? player.getSkin().body().texturePath()
         : DefaultPlayerSkin.getDefaultSkin().body().texturePath();
   }

   private void method624(CustomDrawContext customDrawContext, float xo, float yo) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font font = Fonts.NEW_MEDIUM.getFont(6.5F);
      double d = yo - 10.0F;

      for (ItemStack stack : this.method629()) {
         Component hoverName = stack.getHoverName();
         d += (font.height() / 2.0F + 12.0F + 2.0F) * this.scaleSetting.getValue3();
         float x = xo - font.width(hoverName) / 2.0F;
         float width = font.width(hoverName) + 8.0F;
         float panelHeight = font.height() + GuiStyle.PADDING.intValue() + 1.0F;
         float floatValue = x - 4.0F;
         float y = (float)d;
         this.pushCenteredScale(customDrawContext, floatValue + width / 2.0F, y + panelHeight / 2.0F, 1.0F, 1.0F);
         ProxyDotRadiusData moduleMenuRadius = ProxyDotRadiusData.method8069(InterfaceModule.method6801() * 0.6F);
         customDrawContext.drawBlurHudBooleanCheck(
            floatValue, y, width, panelHeight, 22.0F, moduleMenuRadius, this.method555(ZenithColor.INSTANCE), this.method627(), this.glow.isEnabled() && this.glow.isVisible()
         );
         customDrawContext.drawRoundedRect(floatValue, y, width, panelHeight, moduleMenuRadius, this.method555(currentStyle.getHudBackground().getZenithColor()));
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(x, y + (panelHeight - 1.0F - font.height()) / 2.0F);
         customDrawContext.drawText(font, hoverName, 0.0F, 0.0F, this.method555(currentStyle.getTextEnable().getZenithColor()).method7287());
         customDrawContext.popMatrix();
         this.pop(customDrawContext);
      }
   }

   private void method625(CustomDrawContext customDrawContext, float xo, float yo, float f) {
      float f2 = (float)Math.toRadians(28.0);
      float f3 = (float)Math.toRadians(18.0);
      float f4 = (float)Math.cos(f2);
      float f5 = (float)Math.sin(f2);
      float f6 = (float)Math.cos(f3);
      float f7 = (float)Math.sin(f3);
      float f8 = 0.3F;
      float[][] floatses = new float[][]{
         {-f8, 0.0F, -f8},
         {f8, 0.0F, -f8},
         {0.3F, 0.0F, 0.3F},
         {-f8, 0.0F, f8},
         {-f8, 1.8F, -f8},
         {f8, 1.8F, -f8},
         {0.3F, 1.8F, 0.3F},
         {-f8, 1.8F, f8}
      };
      float[][] floatses2 = new float[8][2];

      for (int i = 0; i < 8; i++) {
         float f9 = floatses[i][0] * f4 + floatses[i][2] * f5;
         float f10 = -floatses[i][0] * f5 + floatses[i][2] * f4;
         floatses2[i][0] = xo + f9 * f;
         floatses2[i][1] = yo - (floatses[i][1] * f6 - f10 * f7) * f;
      }

      ZenithColor zenithColor = this.method555(Zenith.getInstance().getStyleManager().getClientColor(0).method7294(0.7F));
      ZenithColor zenithColor2 = this.gradient.isEnabled() ? this.method555(Zenith.getInstance().getStyleManager().getClientColor(180).method7294(0.7F)) : zenithColor;
      ZenithColor zenithColor3 = this.gradient.isEnabled() ? this.method555(Zenith.getInstance().getStyleManager().getClientColor(90).method7294(0.7F)) : zenithColor;

      for (int i = 0; i < 4; i++) {
         int n = (i + 1) % 4;
         this.method626(customDrawContext, floatses2[i][0], floatses2[i][1], floatses2[n][0], floatses2[n][1], 1.0F, zenithColor);
         this.method626(customDrawContext, floatses2[i + 4][0], floatses2[i + 4][1], floatses2[n + 4][0], floatses2[n + 4][1], 1.0F, zenithColor2);
         this.method626(customDrawContext, floatses2[i][0], floatses2[i][1], floatses2[i + 4][0], floatses2[i + 4][1], 1.0F, zenithColor3);
      }
   }

   private void method626(CustomDrawContext customDrawContext, float xo, float y, float f, float f2, float height, ZenithColor zenithColor) {
      float x = f - xo;
      float y2 = f2 - y;
      float hypot = (float)Math.hypot(x, y2);
      if (!(hypot < 0.01F)) {
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(xo, y);
         customDrawContext.pose().rotate((float)Math.atan2(y2, x));
         customDrawContext.drawRect(0.0F, -height / 2.0F, hypot, height, zenithColor);
         customDrawContext.popMatrix();
      }
   }

   private boolean method627() {
      return this.blur.isEnabled() && this.blur.isVisible();
   }

   private List<ItemStack> method628() {
      if (this.items.isEmpty()) {
         this.items.add(new ItemStack(Items.TOTEM_OF_UNDYING));
         this.items.add(new ItemStack(Items.DIAMOND_BOOTS));
         this.items.add(new ItemStack(Items.DIAMOND_LEGGINGS));
         this.items.add(new ItemStack(Items.DIAMOND_CHESTPLATE));
         this.items.add(new ItemStack(Items.DIAMOND_HELMET));
         this.items.add(new ItemStack(Items.DIAMOND_SWORD));
      }

      return this.items;
   }

   private List<ItemStack> method629() {
      if (this.items2.isEmpty()) {
         this.items2.add(new ItemStack(Items.DIAMOND_SWORD));
         this.items2.add(new ItemStack(Items.TOTEM_OF_UNDYING));
      }

      return this.items2;
   }

   @Generated
   public BooleanSetting getBlur() {
      return this.blur;
   }

   @Generated
   public BooleanSetting getGlow() {
      return this.glow;
   }

   private static final class Data {
      private final ItemStack itemStack;
      private final Component text;
      private final String text2;
      private final String text3;

      public ItemStack itemStack() {
         return this.itemStack;
      }

      public Component text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.itemStack, other.itemStack) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(itemStack, text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "itemStack=" + this.itemStack + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      private Data(ItemStack itemStack, Component text, String str, String str2) {
         this.itemStack = itemStack;
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }

      public Component name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }

   private static final class Data2 {
      private final Item item;
      private final Object obj;
      private final Object obj2;
      private final Object obj3;
      private final Object obj4;
      private final Object obj5;

      public Item item() {
         return this.item;
      }

      public Object obj() {
         return this.obj;
      }

      public Object obj2() {
         return this.obj2;
      }

      public Object obj3() {
         return this.obj3;
      }

      public Object obj4() {
         return this.obj4;
      }

      public Object obj5() {
         return this.obj5;
      }

      @Override
      public String toString() {
         return "Data2[" + "item=" + this.item + ", " + "obj=" + this.obj + ", " + "obj2=" + this.obj2 + ", " + "obj3=" + this.obj3 + ", " + "obj4=" + this.obj4 + ", " + "obj5=" + this.obj5 + "]";
      }

      private Data2(Item item, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
         this.item = item;
         this.obj = obj;
         this.obj2 = obj2;
         this.obj3 = obj3;
         this.obj4 = obj4;
         this.obj5 = obj5;
      }

      public static EntityESPModule.Data2 method630(ItemStack itemStack) {
         return new EntityESPModule.Data2(
            itemStack.getItem(),
            itemStack.get(DataComponents.CUSTOM_DATA),
            itemStack.get(DataComponents.ENCHANTMENTS),
            itemStack.get(DataComponents.CUSTOM_NAME),
            itemStack.get(DataComponents.ITEM_MODEL),
            itemStack.get(DataComponents.CUSTOM_MODEL_DATA)
         );
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof EntityESPModule.Data2 data2
            && data2.item == this.item
            && data2.obj == this.obj
            && data2.obj2 == this.obj2
            && data2.obj3 == this.obj3
            && data2.obj4 == this.obj4
            && data2.obj5 == this.obj5;
      }

      @Override
      public int hashCode() {
         int identityHashCode = System.identityHashCode(this.item);
         identityHashCode = 31 * identityHashCode + System.identityHashCode(this.obj);
         identityHashCode = 31 * identityHashCode + System.identityHashCode(this.obj2);
         identityHashCode = 31 * identityHashCode + System.identityHashCode(this.obj3);
         identityHashCode = 31 * identityHashCode + System.identityHashCode(this.obj4);
         return 31 * identityHashCode + System.identityHashCode(this.obj5);
      }

      public Item getItem() {
         return this.item;
      }

      public Object getObj() {
         return this.obj;
      }

      public Object getObj2() {
         return this.obj2;
      }

      public Object getObj3() {
         return this.obj3;
      }

      public Object getObj4() {
         return this.obj4;
      }

      public Object getObj5() {
         return this.obj5;
      }
   }

   private static final class Data3 {
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
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      private Data3(String text, String str) {
         this.text = text;
         this.text2 = str;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   private static final class Data4 {
      private final EntityESPModule.Entry entry;
      private final long longValue;

      public EntityESPModule.Entry entry() {
         return this.entry;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.entry, other.entry) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(entry, longValue);
      }

      @Override
      public String toString() {
         return "Data4[" + "entry=" + this.entry + ", " + "longValue=" + this.longValue + "]";
      }

      private Data4(EntityESPModule.Entry entry, long longValue) {
         this.entry = entry;
         this.longValue = longValue;
      }

      public EntityESPModule.Entry getEntry() {
         return this.entry;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private final String text;
      private final Component text2;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final float floatValue;
      private final int intValue;
      private final List<ItemStack> items;
      private final List<ItemStack> items2;
      private final ItemStack itemStack;
      private final Entity entity;
      private final PartyPlayerInfo partyPlayerInfo;
      private AABB box;
      private EntityESPModule.Data[] datas;
      private boolean flag8;
      private String text3;
      private boolean flag9;
      private boolean flag10;
      private String text4;
      private int intValue2;
      private String pingText;
      private int intValue3 = -1;
      private AABB box2;
      private Vector4d vector4d;

      private Entry(
         String text,
         Component displayName,
         boolean flag,
         boolean flag2,
         boolean flag3,
         boolean flag4,
         boolean flag5,
         boolean flag6,
         boolean flag7,
         AABB box,
         float floatValue,
         int intValue,
         List<ItemStack> items,
         List<ItemStack> stacks,
         ItemStack itemStack,
         Entity entity,
         PartyPlayerInfo partyPlayerInfo
      ) {
         this.text = text;
         this.text2 = displayName;
         this.flag = flag;
         this.flag2 = flag2;
         this.flag3 = flag3;
         this.flag4 = flag4;
         this.flag5 = flag5;
         this.flag6 = flag6;
         this.flag7 = flag7;
         this.box = box;
         this.floatValue = floatValue;
         this.intValue = intValue;
         this.items = items;
         this.items2 = stacks;
         this.itemStack = itemStack;
         this.entity = entity;
         this.partyPlayerInfo = partyPlayerInfo;
      }

      private void method631() {
         if (this.entity != null) {
            if (!this.entity.isRemoved()) {
               Vec3 vec = MathUtils.method2656(this.entity);
               this.box = this.entity.getBoundingBox().move(vec.subtract(this.entity.position()));
            }
         } else {
            if (this.partyPlayerInfo != null) {
               Vec3 vec = this.partyPlayerInfo.method3294();
               if (vec == null) {
                  return;
               }

               this.box = new AABB(vec.x - 0.3, vec.y, vec.z - 0.3, vec.x + 0.3, vec.y + 1.8, vec.z + 0.3);
            }
         }
      }

      private String key() {
         return this.text;
      }

      private Component name() {
         return this.text2;
      }

      private boolean isFlag() {
         return this.flag;
      }

      private boolean isFlag2() {
         return this.flag2;
      }

      private boolean isFlag3() {
         return this.flag3;
      }

      private boolean isFlag4() {
         return this.flag4;
      }

      private boolean isFlag5() {
         return this.flag5;
      }

      private boolean isFlag6() {
         return this.flag6;
      }

      private boolean isFlag7() {
         return this.flag7;
      }

      private AABB getBox() {
         return this.box;
      }

      private float getFloatValue() {
         return this.floatValue;
      }

      private int getIntValue() {
         return this.intValue;
      }

      private List<ItemStack> getItems() {
         return this.items;
      }

      private List<ItemStack> getItems2() {
         return this.items2;
      }

      private ItemStack getItemStack() {
         return this.itemStack;
      }

      private Entity getEntity() {
         return this.entity;
      }
   }

   public static enum Type {
      HP,
      PING,
      GEAR;
   }
}
