package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.client.particle.ExplodeParticle.Provider;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.RenderState;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.KeyState;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.Island;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "ServerHelper",
   category = ModuleCategory.MISC,
   description = "module.serverhelper.description"
)
public final class ServerHelperModule extends Module {
   private static final long CONST_longValue = 2000L;
   private final LinkedHashMap<BlockPos, ServerHelperModule.Data5> linkedHashMap = new LinkedHashMap<>();
   private final List<ServerHelperModule.Data2> items = new ArrayList<>();
   private final List<ServerHelperModule.Data4> items2 = new ArrayList<>();
   private final List<ServerHelperModule.Data3> items3 = new ArrayList<>();
   private final Map<Integer, ServerHelperModule.Data> items4 = new ConcurrentHashMap<>();
   private final Map<BlockPos, Boolean> items5 = new HashMap<>();
   private final Map<BlockPos, Boolean> items6 = new HashMap<>();
   private static final long CONST_longValue2 = 1000L;
   private final Map<BlockPos, Long> items7 = new HashMap<>();
   private final Map<BlockPos, Long> items8 = new HashMap<>();
   public static final ServerHelperModule INSTANCE = new ServerHelperModule();
   private final ModeSetting serverMode = new ModeSetting(
      "module.serverHelper.serverMode", "module.serverHelper.serverMode.desc", "Auto", "HolyWorld", "FunTime", "ReallyWorld"
   );
   private final BooleanSetting consumablesSetting = new BooleanSetting(
      "module.serverHelper.consumablesSetting", "module.serverHelper.consumablesSetting.desc", true, () -> this.method5182() || this.method5183()
   );
   private final BooleanSetting autoPointSetting = new BooleanSetting(
      "module.serverHelper.autoPointSetting", "module.serverHelper.autoPointSetting.desc", true, this::method5182
   );
   private final BooleanSetting hitDisplaySetting = new BooleanSetting(
      "module.serverHelper.hitDisplaySetting", "module.serverHelper.hitDisplaySetting.desc", true, this::method5183
   );
   private final BindSetting shulkerKey = new BindSetting("module.serverHelper.shulkerKey", "module.serverHelper.shulkerKey.desc", -1, this::method5183);
   private final BooleanSetting closeMenu = new BooleanSetting("module.serverHelper.closeMenu", "module.serverHelper.closeMenu.desc", false, this::method5181);
   private final BooleanSetting chatFilter = new BooleanSetting(
      "module.serverHelper.chatFilter", "module.serverHelper.chatFilter.desc", false, this::method5181
   );
   private final BooleanSetting autoFixAll = new BooleanSetting(
      "module.serverHelper.autoFixAll", "module.serverHelper.autoFixAll.desc", false, this::method5181
   );
   private final BooleanSetting antiFlyRadius = new BooleanSetting(
      "module.serverHelper.antiFlyRadius", "module.serverHelper.antiFlyRadius.desc", false, this::method5181
   );
   private static final String text = "ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ";
   private static final float CONST_floatValue = 16.0F;
   private static final long CONST_longValue3 = 2000L;
   private static final Pattern pattern = Pattern.compile(
      "(?<![\\p{L}\\d])(?:акриен(?:а|у|ом|е|чик)?|рич(?:а|у|ом|ей|е)?|ньюкод(?:ом|а|у|ами|ик|е)?|экспенсив(?:ом|а|у|ами|е)?|импакт(?:ом|а|у|ами|ик|е)?|экселлент(?:ом|а|у|ами|ик|е)?|экселент(?:ом|а|у|ами|ик)?|катлаван(?:ом|а|у|ами|чик)?|катлован(?:ом|а|у|ами|чик)?|целестиал(?:ом|а|у|ами|е)?|целк(?:ой|а|у|ами|очка|е)?|матикс(?:ом|а|у|ами|е)?|инерти(?:я|ей|ю|ями|е)?|эксп(?:а|ой|ою|у|уличка|е)?|флюгер(?:ом|а|у|ами)?|рикер(?:а|у|ом|очек)?|фанпе(?:й|ю|я|ем|е|йчик)?|вексайд(?:ом|а|у|ами|ик|е)?|нурсултан(?:а|у|е|ом|чик)?|нурик(?:а|у|ом|е)?|нурлан(?:а|у|ом|чик|е)?|векс(?:ом|у|а|ами|ик|е)?|релейк(?:ом|у|а|ами|е)?|арбуз(?:ом|а|у|ами|ик|е)?|вилд(?:ом|у|а|ами|ик|е)?|фантайм(?:е|а|у)?|холик(?:е|а|у)?|холиворлд(?:а|у|е)?|рокстар(?:ом|а|у|ами|чик|е)?|рогалик(?:а|у|ом|е)?|тандерхак(?:ом|у|и|ами|а|е)?|ликвидбаунс(?:а|у|ами|е)?|expensive|celestial|newcode|arbuz|akrien|nursultan|relake|wild|wurst|catlovan|excellent|rockstar|catlavan|impact|matix|inertia|wexside|wex|nurik|nurlan|rich|funpay|fluger|riker|funtime|holyworld|wwe|hvh|rogalik|thunderhack|liquidbounce)(?![\\p{L}\\d])",
      66
   );
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag;
   private Slot slot = null;
   private final StopWatch stopWatch2 = new StopWatch();

   private ServerHelperModule() {
      this.initialize();
   }

   public boolean method5181() {
      return this.serverMode.isSelected(0) ? Zenith.getInstance().getServerDetector().method1641() : this.serverMode.isSelected(3);
   }

   public boolean method5182() {
      return this.serverMode.isSelected(0) ? Zenith.getInstance().getServerDetector().method1639() : this.serverMode.isSelected(2);
   }

   public boolean method5183() {
      return this.serverMode.isSelected(0) ? Zenith.getInstance().getServerDetector().method1642() : this.serverMode.isSelected(1);
   }

   public void initialize() {
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FIREWORK_STAR, new BindSetting("module.serverHelper.antiFly", "module.serverHelper.antiFly.desc", this::method5181), 0.0F, new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FLOWER_BANNER_PATTERN,
               new BindSetting("module.serverHelper.scrollExp", "module.serverHelper.scrollExp.desc", this::method5181),
               0.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.HEART_OF_THE_SEA,
               new BindSetting("module.serverHelper.enderTrap", "module.serverHelper.enderTrap.desc", this::method5181),
               0.0F,
               new KeyState(),
               slot -> slot.getItem().getHoverName().getString().equals("Эндер Ловушка")
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.HEART_OF_THE_SEA,
               new BindSetting("module.serverHelper.reallyWorldTrap", "module.serverHelper.reallyWorldTrap.desc", this::method5181),
               0.0F,
               new KeyState(),
               slot -> slot.getItem().getHoverName().getString().equals("Ловушка")
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.PRISMARINE_SHARD,
               new BindSetting("module.serverHelper.explosiveTrap", "module.serverHelper.explosiveTrap.desc", this::method5183),
               5.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.POPPED_CHORUS_FRUIT,
               new BindSetting("module.serverHelper.normalTrap", "module.serverHelper.normalTrap.desc", this::method5183),
               0.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.NETHER_STAR, new BindSetting("module.serverHelper.stun", "module.serverHelper.stun.desc", this::method5183), 30.0F, new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FIRE_CHARGE,
               new BindSetting("module.serverHelper.explosiveThing", "module.serverHelper.explosiveThing.desc", this::method5183),
               0.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.SNOWBALL,
               new BindSetting("module.serverHelper.snowball", "module.serverHelper.snowball.desc", () -> this.method5182() || this.method5183()),
               0.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.PHANTOM_MEMBRANE,
               new BindSetting("module.serverHelper.holyAura", "module.serverHelper.holyAura.desc", this::method5182),
               0.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.NETHERITE_SCRAP, new BindSetting("module.serverHelper.trap", "module.serverHelper.trap.desc", this::method5182), 0.0F, new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.DRIED_KELP, new BindSetting("module.serverHelper.plast", "module.serverHelper.plast.desc", this::method5182), 0.0F, new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.SUGAR, new BindSetting("module.serverHelper.clearDust", "module.serverHelper.clearDust.desc", this::method5182), 10.0F, new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.FIRE_CHARGE,
               new BindSetting("module.serverHelper.fireTornado", "module.serverHelper.fireTornado.desc", this::method5182),
               10.0F,
               new KeyState()
            )
         );
      this.items3
         .add(
            new ServerHelperModule.Data3(
               Items.ENDER_EYE, new BindSetting("module.serverHelper.disorient", "module.serverHelper.disorient.desc", this::method5182), 10.0F, new KeyState()
            )
         );
   }

   @Override
   public List<Setting> getSettings() {
      var settings = new ArrayList<>(
         List.of(
            this.serverMode,
            this.consumablesSetting,
            this.autoPointSetting,
            this.hitDisplaySetting,
            this.shulkerKey,
            this.closeMenu,
            this.chatFilter,
            this.autoFixAll,
            this.antiFlyRadius
         )
      );
      settings.addAll(this.items3.stream().map(ServerHelperModule.Data3::getBindSetting).toList());
      return settings;
   }

   @EventTarget
   public void method5184(KeyEvent event) {
      this.items3
         .stream()
         .filter(var1x -> event.isPress(var1x.bindSetting.getKeyCode()) && var1x.bindSetting.isVisible() && var1x.method5218() != null)
         .forEach(data3 -> data3.class27.setFlag(true));
      this.items3.stream().filter(var1x -> event.isRelease(var1x.bindSetting.getKeyCode()) && var1x.bindSetting.isVisible()).forEach(var1x -> {
         if (this.method5208(var1x.floatValue)) {
            InventoryUtils.method928(var1x.getItem(), var1x.method5219());
         }

         var1x.class27.setFlag(false);
      });
      if (event.isRelease(this.shulkerKey.getKeyCode())) {
         Slot slot = InventoryUtils.method910(slot2 -> slot2.getItem().getItem() instanceof BlockItem var1x && var1x.getBlock() instanceof ShulkerBoxBlock);
         if (slot == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Component.translationArg(
                     Component.nullToEmpty("Шалкер")
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().method1445()))
                        .append(
                           Component.nullToEmpty(" не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
            return;
         }

         this.slot = slot;
         TaskScheduler.Entry2 task = new TaskScheduler.Entry2(ServerHelperModule.class);
         task.method5120(
            HandleInputPostEvent.class,
            var1x -> {
               if (!mc.player.lastSentInput.jump()
                  && !mc.player.isSprinting()
                  && !mc.player.lastSentInput.forward()
                  && !mc.player.lastSentInput.backward()
                  && !mc.player.lastSentInput.left()
                  && !mc.player.lastSentInput.right()) {
                  InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, false);
                  InventoryUtils.closeScreen();
                  return true;
               } else {
                  return false;
               }
            }
         );
         task.method5120(HandleInputPostEvent.class, event2 -> true);
         task.method5120(
            HandleInputEvent.class,
            var1x -> {
               if (var1x.isCancelled()) {
                  return false;
               } else if (!mc.player.lastSentInput.jump()
                  && !mc.player.isSprinting()
                  && !mc.player.lastSentInput.forward()
                  && !mc.player.lastSentInput.backward()
                  && !mc.player.lastSentInput.left()
                  && !mc.player.lastSentInput.right()) {
                  UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                  var1x.cancel();
                  this.stopWatch2.reset();
                  return true;
               } else {
                  return false;
               }
            }
         );
         task.method5120(
            HandleInputPostEvent.class,
            var2x -> {
               if (!mc.player.lastSentInput.jump()
                  && !mc.player.isSprinting()
                  && !mc.player.lastSentInput.forward()
                  && !mc.player.lastSentInput.backward()
                  && !mc.player.lastSentInput.left()
                  && !mc.player.lastSentInput.right()) {
                  if (mc.player.containerMenu instanceof InventoryMenu) {
                     if (this.stopWatch2.hasElapsed(1000L)) {
                        InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true);
                        InventoryUtils.closeScreen();
                        return true;
                     }
                  } else {
                     this.stopWatch2.setStartTime(0L);
                  }

                  return false;
               } else {
                  return false;
               }
            }
         );
         task.method5123(MoveInputEvent.class, event2 -> {
            event2.clearInput();
            return true;
         });
         Zenith.getInstance().getTaskScheduler().method5110(task);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.autoFixAll.isEnabled() && this.method5181()) {
         if (Zenith.getInstance().getServerDetector().method1635()) {
            this.flag = true;
            this.stopWatch.reset();
         } else if (this.flag && this.stopWatch.hasElapsed(2000L)) {
            this.flag = false;
            mc.player.connection.sendCommand("fix all");
         }
      } else {
         this.flag = false;
      }
   }

   @EventTarget
   public void method5185(ChatSendEvent event) {
      if (event.method4603() && this.chatFilter.isEnabled() && this.method5181()) {
         String message = event.getMessage();
         if (message != null && !message.startsWith(".")) {
            if (pattern.matcher(message).find()) {
               event.cancel();
               ChatLogger.method2021("В сообщении запретное для RW слово — отправка отменена");
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()
         && event.getPacket() instanceof ClientboundOpenScreenPacket openScreenPacket
         && this.closeMenu.isEnabled()
         && this.method5181()
         && mc.player != null
         && mc.player.tickCount < 100
         && openScreenPacket.getTitle().getString().contains("ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ")) {
         event.cancel();
         int containerId = openScreenPacket.getContainerId();
         mc.execute(() -> {
            if (mc.getConnection() != null) {
               mc.getConnection().send(new ServerboundContainerClosePacket(containerId));
            }
         });
      } else {
         this.method5190(event);
         if (this.consumablesSetting.isEnabled()) {
            if (Zenith.getInstance().getServerDetector().method1639() && event.getPacket() instanceof ClientboundSectionBlocksUpdatePacket blocksUpdatePacket) {
               mc.execute(() -> this.method5186(blocksUpdatePacket));
            }

            if (event.getPacket() instanceof ClientboundSoundPacket clientboundSoundPacket
               && Zenith.getInstance().getServerDetector().method1642()
               && clientboundSoundPacket.getSound().unwrapKey().isPresent()
               && clientboundSoundPacket.getSound().unwrapKey().get().identifier().getPath().equals("block.beacon.deactivate")) {
               this.method5206(Items.NETHER_STAR, new Vec3(clientboundSoundPacket.getX(), clientboundSoundPacket.getY(), clientboundSoundPacket.getZ()), System.currentTimeMillis() + 15000L);
            }

            if (Zenith.getInstance().getServerDetector().method1642() && event.getPacket() instanceof ClientboundLevelParticlesPacket levelParticlesPacket) {
               Class clazz = ((ParticleProvider)mc.particleEngine.resourceManager
                     .getProviders()
                     .get(BuiltInRegistries.PARTICLE_TYPE.getId(levelParticlesPacket.particle().getType())))
                  .getClass();
               if (clazz == Provider.class) {
                  this.method5206(Items.PRISMARINE_SHARD, new Vec3(levelParticlesPacket.x(), levelParticlesPacket.y(), levelParticlesPacket.z()), System.currentTimeMillis() + 11000L);
               }
            }
         }

         if (event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket && this.autoPointSetting.isEnabled() && this.autoPointSetting.getVisible().get()) {
            Component content = systemChatPacket.content();
            String string = content.toString();
            String str = content.getString();
            String substringBetween = StringUtils.substringBetween(str, "|||   [", "]   ");
            if (substringBetween != null) {
               String substringBetween2 = StringUtils.substringBetween(string, "value='/gps ", "'");
               String substringBetween3 = StringUtils.substringBetween(str, "Уровень лута: ", "\n ║");
               String substringBetween4 = StringUtils.substringBetween(str, "Призван игроком: ", "\n ║");
               if (substringBetween2 != null) {
                  String[] strings = substringBetween2.split(" ");
                  Vec3 atCenterOf = Vec3.atCenterOf(BlockPos.containing(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2])));
                  switch (substringBetween) {
                     case "Мистический сундук":
                        this.method5205(substringBetween, substringBetween3, substringBetween4, atCenterOf, "overworld", 300, 0);
                        break;
                     case "Вулкан":
                        this.method5205(substringBetween, substringBetween3, substringBetween4, atCenterOf, "overworld", 300, 120);
                        break;
                     case "Метеоритный дождь":
                     case "Маяк убийца":
                     case "Мистический Алтарь":
                        this.method5205(substringBetween, substringBetween3, substringBetween4, atCenterOf, "overworld", 360, 0);
                        break;
                     case "Загадочный маяк":
                        this.method5205(substringBetween, substringBetween3, substringBetween4, atCenterOf, "overworld", 60, 180);
                  }
               } else {
                  switch (substringBetween) {
                     case "Сундук смерти":
                        this.method5205(substringBetween, substringBetween3, substringBetween4, net.minecraft.world.phys.Vec3.atCenterOf(BlockPos.containing(-155.0, 64.0, 205.0)), "lobby", 300, 0);
                        break;
                     case "Адская резня":
                        this.method5205(substringBetween, substringBetween3, substringBetween4, net.minecraft.world.phys.Vec3.atCenterOf(BlockPos.containing(48.0, 87.0, 73.0)), "lobby", 180, 120);
                  }
               }
            }
         }
      }
   }

   private void method5186(ClientboundSectionBlocksUpdatePacket blocksUpdatePacket) {
      long now = System.currentTimeMillis();
      this.method5187(now);
      blocksUpdatePacket.runUpdates((pos, blockState) -> {
         BlockPos immutable = pos.immutable();
         this.linkedHashMap.remove(immutable);
         this.linkedHashMap.put(immutable, new ServerHelperModule.Data5(blockState, now));
      });
      blocksUpdatePacket.runUpdates((var1x, var2x) -> {
         Vec3 atCenterOf = net.minecraft.world.phys.Vec3.atCenterOf(var1x.offset(0, 0, 0));
         if (this.linkedHashMap.size() > 50 && this.linkedHashMap.size() < 600) {
            if (this.method5209(var1x.above(2))) {
               this.method5206(Items.NETHERITE_SCRAP, atCenterOf, System.currentTimeMillis() + 15000L);
            } else if (this.method5211(var1x.above(3))) {
               this.method5206(Items.NETHERITE_SCRAP, atCenterOf, System.currentTimeMillis() + 30000L);
            }
         }
      });
   }

   private void method5187(long now) {
      var iterator = this.linkedHashMap.values().iterator();

      while (iterator.hasNext() && now - ((ServerHelperModule.Data5)iterator.next()).time() > 2000L) {
         iterator.remove();
      }
   }

   private BlockState method5188(BlockPos key) {
      ServerHelperModule.Data5 data5 = this.linkedHashMap.get(key);
      return data5 == null ? null : data5.state();
   }

   @EventTarget
   public void method5189(WorldRenderEvent event) {
      if (System.currentTimeMillis() % 5000L < 16L) {
         this.method5214();
      }

      BlockPos blockPosition = mc.player.blockPosition();
      Vec3 vec = Vec3.ZERO;
      PoseStack matrixStack = event.getMatrixStack();
      this.items3.stream().filter(data3 -> data3.class27.isValue()).forEach(var4x -> {
         String var5x = var4x.bindSetting.getKey();
         switch (var5x) {
            case "module.serverHelper.trap":
            case "module.serverHelper.normalTrap":
               this.method5197(blockPosition, vec, 1.99F, Zenith.getInstance().getStyleManager().getClientColor(90).method7287());
               break;
            case "module.serverHelper.disorient":
            case "module.serverHelper.fireTornado":
            case "module.serverHelper.clearDust":
               this.method5198(matrixStack, var4x.floatValue, ColorUtils.CONST_intValue);
               break;
            case "module.serverHelper.explosiveThing":
               this.method5198(matrixStack, 5.0F, Zenith.getInstance().getStyleManager().getClientColor(90).method7287());
               break;
            case "module.serverHelper.plast":
               float f = Mth.wrapDegrees(mc.player.getYRot());
               if (Math.abs(mc.player.getXRot()) > 60.0F) {
                  BlockPos relative = blockPosition.above().relative(mc.player.getNearestViewDirection(), 3);
                  Vec3 begin = Vec3.atLowerCornerOf(relative.east(3).south(3).below()).add(vec);
                  Vec3 end = Vec3.atLowerCornerOf(relative.west(2).north(2).above()).add(vec);
                  GlowUtil.method2232(new AABB(begin, end), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (f <= -157.5F || f >= 157.5F) {
                  BlockPos above = blockPosition.north(3).above();
                  Vec3 begin = Vec3.atLowerCornerOf(above.below(2).east(3)).add(vec);
                  Vec3 end = Vec3.atLowerCornerOf(above.above(3).west(2).south(2)).add(vec);
                  GlowUtil.method2232(new AABB(begin, end), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (f <= -112.5F) {
                  this.method5200(blockPosition.east(5).south().below(), vec, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), -1, true);
               } else if (f <= -67.5F) {
                  BlockPos above = blockPosition.east(2).above();
                  Vec3 begin = Vec3.atLowerCornerOf(above.below(2).south(3)).add(vec);
                  Vec3 end = Vec3.atLowerCornerOf(above.above(3).north(2).east(2)).add(vec);
                  GlowUtil.method2232(new AABB(begin, end), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (f <= -22.5F) {
                  this.method5200(blockPosition.east(5).below(), vec, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 1, false);
               } else if (f >= -22.5 && f <= 22.5) {
                  BlockPos above = blockPosition.south(2).above();
                  Vec3 begin = Vec3.atLowerCornerOf(above.below(2).east(3)).add(vec);
                  Vec3 end = Vec3.atLowerCornerOf(above.above(3).west(2).south(2)).add(vec);
                  GlowUtil.method2232(new AABB(begin, end), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (f <= 67.5F) {
                  this.method5200(blockPosition.west(4).below(), vec, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 1, true);
               } else if (f <= 112.5F) {
                  BlockPos above = blockPosition.west(3).above();
                  Vec3 begin = Vec3.atLowerCornerOf(above.below(2).south(3)).add(vec);
                  Vec3 end = Vec3.atLowerCornerOf(above.above(3).north(2).east(2)).add(vec);
                  GlowUtil.method2232(new AABB(begin, end), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 3.0F, true, true, true);
               } else if (f <= 157.5F) {
                  this.method5200(blockPosition.west(4).south().below(), vec, Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), -1, false);
               }
               break;
            case "module.serverHelper.explosiveTrap":
               this.method5197(blockPosition, vec, 3.99F, ColorUtils.CONST_intValue);
               break;
            case "module.serverHelper.stun":
               this.method5197(blockPosition, vec, 15.01F, ColorUtils.CONST_intValue);
               break;
            case "module.serverHelper.snowball":
               PredictionsModule.INSTANCE.method2902(matrixStack, PredictionsModule.snowball());
         }
      });
      if (this.antiFlyRadius.isEnabled()
         && this.method5181()
         && (mc.player.getMainHandItem().is(Items.FIREWORK_STAR) || mc.player.getOffhandItem().is(Items.FIREWORK_STAR))) {
         this.method5198(matrixStack, 16.0F, ColorUtils.CONST_intValue);
      }

      this.items2.forEach(data4 -> {
         if (data4.item == Items.NETHER_STAR) {
            this.method5197(BlockPos.containing(data4.vec3d), vec, 15.01F, Zenith.getInstance().getStyleManager().getClientColor(90).method7287());
         }
      });
   }

   @EventTarget
   public void onDraw(DrawEvent event) {
      CustomDrawContext customDrawContext = event.getCustomDrawContext();
      MatrixUtils.method666(customDrawContext.pose());
      if (this.hitDisplaySetting.isEnabled() && this.hitDisplaySetting.getVisible().get()) {
         this.method5194(customDrawContext);
      }

      this.items2
         .forEach(
            var1x -> {
               double var2x = (var1x.doubleValue - System.currentTimeMillis()) / 1000.0;
               Vec3 vec = ProjectionUtils.method301(var1x.vec3d);
               String text = MathUtils.method2652(var2x, 0.1F) + "с";
               Font font = Fonts.MEDIUM.getFont(10.0F);
               float width = font.width(text);
               float x = (float)(vec.x - width / 2.0F);
               float y = (float)vec.y;
               if (ProjectionUtils.method302(var1x.vec3d)
                  && var1x.intValue == Zenith.getInstance().getServerDetector().getAnarchy()
                  && Zenith.getInstance().getServerDetector().method1638().equals(var1x.text)) {
                  RenderShaders.method5286(
                     MatrixUtils.method664(event.getCustomDrawContext().pose()),
                     x - 4.0F,
                     y - 4.0F,
                     16.8F + font.width(text) + 8.0F,
                     20.8F,
                     22.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     ZenithColor.INSTANCE
                  );
                  event.getCustomDrawContext()
                     .drawRoundedRect(
                        x - 4.0F,
                        y - 4.0F,
                        16.8F + font.width(text) + 8.0F,
                        20.8F,
                        ProxyDotRadiusData.method8069(4.0F),
                        Zenith.getInstance().getStyleManager().getCurrentStyle().getHudBackground().getZenithColor()
                     );
                  RenderShaders.method5278(
                     MatrixUtils.method664(event.getCustomDrawContext().pose()),
                     x - 4.0F,
                     y - 4.0F,
                     16.8F + font.width(text) + 8.0F,
                     20.8F,
                     0.1F,
                     10.0F,
                     Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor(),
                     ProxyDotRadiusData.method8069(4.0F)
                  );
                  event.getCustomDrawContext()
                     .drawText(
                        font,
                        text,
                        x + 12.8F + 4.0F,
                        y + 2.5F,
                        Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor()
                     );
                  event.getCustomDrawContext().pose().pushMatrix();
                  event.getCustomDrawContext().pose().translate(x, y);
                  event.getCustomDrawContext().pose().scale(0.8F, 0.8F);
                  event.getCustomDrawContext().item(var1x.item.getDefaultInstance(), 0, 0);
                  event.getCustomDrawContext().pose().popMatrix();
               }
            }
         );
      this.items2.removeIf(data4 -> data4.doubleValue - System.currentTimeMillis() <= 0.0);
      this.items.removeIf(data2 -> data2.doubleValue2 + 90000.0 - System.currentTimeMillis() <= 0.0);
   }

   private void method5190(PacketEvent event) {
      if (this.hitDisplaySetting.isEnabled() && this.hitDisplaySetting.getVisible().get() && mc.level != null) {
         long now = System.currentTimeMillis();
         if (event.getPacket() instanceof ClientboundSetEntityDataPacket setEntityDataPacket) {
            this.method5191(setEntityDataPacket, now);
         }
      } else {
         if (!this.items4.isEmpty()) {
            this.items4.clear();
         }
      }
   }

   private void method5191(ClientboundSetEntityDataPacket setEntityDataPacket, long now) {
      for (DataValue packedItem : setEntityDataPacket.packedItems()) {
         if (packedItem.id() == 10) {
            if (this.method5192(packedItem.value())) {
               ServerHelperModule.Data data = this.items4.get(setEntityDataPacket.id());
               if (data == null || data.getLongValue2() <= now) {
                  this.items4.put(setEntityDataPacket.id(), new ServerHelperModule.Data(now, now + 10000L));
               }
            }

            return;
         }
      }
   }

   private boolean method5192(Object obj) {
      return obj instanceof List list
         ? list.stream().filter(ColorParticleOption.class::isInstance).map(ColorParticleOption.class::cast).anyMatch(x -> this.method5193((ColorParticleOption)x))
         : false;
   }

   private boolean method5193(ColorParticleOption x) {
      return Math.abs(x.getRed() - 0.29411766F) <= 1.0E-4F
         && Math.abs(x.getGreen() - 0.34509805F) <= 1.0E-4F
         && Math.abs(x.getBlue() - 0.4117647F) <= 1.0E-4F;
   }

   private void method5194(CustomDrawContext customDrawContext) {
      if (mc.player != null && mc.level != null) {
         long now = System.currentTimeMillis();
         this.items4
            .entrySet()
            .removeIf(var2x -> var2x.getValue().getLongValue2() <= now || !(mc.level.getEntity(var2x.getKey()) instanceof Player));

         for (Player player : mc.level.players()) {
            if (player != mc.player) {
               ServerHelperModule.Data data = this.items4.get(player.getId());
               if (data != null && data.getLongValue2() > now) {
                  Vec3 vec3d = MathUtils.method2656(player).add(0.0, player.getBbHeight() * 0.5, 0.0);
                  if (ProjectionUtils.method302(vec3d)) {
                     Vec3 vec = ProjectionUtils.method301(vec3d);
                     if (!(vec.z <= 0.0) && !(vec.z >= 1.0)) {
                        float width = 18.0F;
                        float f = Mth.clamp((float)(data.getLongValue2() - now) / 10000.0F, 0.0F, 1.0F);
                        float previewAlpha = this.method5195(data, now);
                        float floatValue = (float)vec.x - width / 2.0F;
                        float y = (float)vec.y - width / 2.0F;
                        this.method5196(customDrawContext, floatValue, y, width, f, previewAlpha);
                     }
                  }
               }
            }
         }
      }
   }

   private float method5195(ServerHelperModule.Data data, long now) {
      float f = Mth.clamp((float)(now - data.getLongValue()) / 250.0F, 0.0F, 1.0F);
      float f2 = Mth.clamp((float)(data.getLongValue2() - now) / 250.0F, 0.0F, 1.0F);
      return Math.min(f, f2);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method5196(CustomDrawContext customDrawContext, float floatValue, float y, float width, float f, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      RenderState.method1883(1.0F, 1.0F, 1.0F, previewAlpha);

      try {
         customDrawContext.drawArcBorder(floatValue, y, width, width, 1.0F, 360.0F, 0.5F, currentStyle.getFieldBorder().getZenithColor());
         customDrawContext.drawArcBorder(floatValue, y, width, width, 1.0F, 360.0F * f, 0.5F, currentStyle.getPrimaryColor().getZenithColor());
         float x = 0.65F;
         float f2 = (width - 16.0F * x) / 2.0F;
         customDrawContext.pose().pushMatrix();
         customDrawContext.pose().translate(floatValue + f2, y + f2);
         customDrawContext.pose().scale(x, x);
         customDrawContext.item(Items.SNOWBALL.getDefaultInstance(), 0, 0);
         customDrawContext.pose().popMatrix();
      } catch (Throwable e) {
         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         throw e;
      }

      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void method5197(BlockPos blockPosition, Vec3 vec, float amountToAddInAllDirections, int n) {
      AABB inflate = new AABB(blockPosition.above()).move(vec).deflate(0.0, 0.2F, 0.0).inflate(amountToAddInAllDirections);
      boolean anyMatch = mc.level
         .players()
         .stream()
         .anyMatch(
            var1x -> var1x != mc.player
               && inflate.intersects(var1x.getBoundingBox())
               && !Zenith.getInstance().getFriends().method7627(var1x.getGameProfile().name())
         );
      GlowUtil.method2232(
         inflate, anyMatch ? Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287() : n, 3.0F, true, true, true
      );
   }

   private void method5198(PoseStack matrixStack, float floatValue, int n) {
      float x = mc.player.getBbWidth() / 2.0F;
      int color = this.method5208(floatValue) ? Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287() : n;
      Vec3 vec = MathUtils.method2656(mc.player).add(x, 0.02, x);
      Vec3 vec2 = vec.subtract(mc.getEntityRenderDispatcher().camera.position());
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderState.method1898("core/position_color");
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      int n2 = 0;

      for (byte i = 90; n2 <= i; n2++) {
         Vec3 vec3 = MathUtils.method2653(n2, i, floatValue);
         Vec3 vec4 = MathUtils.method2653(n2 + 1, i, floatValue);
         GlowUtil.method2235(
            matrixStack, begin, vec2.add(vec3), vec2.add(vec3.x, vec3.y + 2.0, vec3.z), ColorUtils.method6439(color, 0.2F), ColorUtils.method6439(color, 0.0F)
         );
         GlowUtil.method2245(vec.add(vec3), vec.add(vec4), color, 2.0F, true);
      }

      n2 = 0;

      for (byte i = 90; n2 <= i; n2++) {
         Vec3 vec3 = MathUtils.method2653(n2, i, floatValue);
         GlowUtil.method2235(
            matrixStack, begin, vec2.add(vec3), vec2.add(vec3.x, vec3.y - 2.0, vec3.z), ColorUtils.method6439(color, 0.2F), ColorUtils.method6439(color, 0.0F)
         );
      }

      ImmediateRenderer.method8015(begin.buildOrThrow());
      RenderState.method1879();
      RenderState.method1882(true);
      RenderState.method1872();
      GL11.glDisable(2881);
   }

   private void method5199(PoseStack poseStack, Font font, List<String> strings, Vec3 vec) {
      float f = 0.0F;

      for (int i = 0; i < strings.size(); i++) {
         String text = (String)strings.get(i);
         float width = font.width(text);
         float f2 = (float)(vec.x - width / 2.0F);
         f += 10.0F;
      }
   }

   public void method5200(BlockPos below, Vec3 vec, int color, int z, boolean flag) {
      Vec3 vec2 = Vec3.atLowerCornerOf(below).add(vec);
      float width = 2.0F;
      int n = ColorUtils.method6439(color, 0.15F);
      this.method5201(vec2, color, width, z, flag);
      this.method5201(vec2, color, width, z, flag);
      this.method5202(vec2, color, width, z, flag);
      this.method5203(vec2, n, z, flag);
      this.method5203(vec2, n, z, flag);
      this.method5204(vec2, n, z, flag);
   }

   private void method5201(Vec3 vec, int color, float width, int z, boolean flag) {
      float x = flag ? z : -z;
      Vec3 vec2;
      GlowUtil.method2245(vec, vec2 = vec.add(x, 0.0, 0.0), color, width, true);

      for (int i = 0; i < 4; i++) {
         Vec3 vec3;
         GlowUtil.method2245(vec2, vec3 = vec2.add(0.0, 0.0, z), color, width, true);
         GlowUtil.method2245(vec3, vec2 = vec3.add(x, 0.0, 0.0), color, width, true);
      }

      Vec3 vec3;
      GlowUtil.method2245(vec2, vec3 = vec2.add(0.0, 0.0, z), color, width, true);
      GlowUtil.method2245(vec3, vec2 = vec3.add(x * -2.0F, 0.0, 0.0), color, width, true);

      for (int i = 0; i < 3; i++) {
         Vec3 vec4;
         GlowUtil.method2245(vec2, vec4 = vec2.add(0.0, 0.0, z * -1), color, width, true);
         GlowUtil.method2245(vec4, vec2 = vec4.add(x * -1.0F, 0.0, 0.0), color, width, true);
      }

      GlowUtil.method2245(vec2, vec2.add(0.0, 0.0, z * -2), color, width, true);
   }

   private void method5202(Vec3 vec, int color, float width, int z, boolean flag) {
      float x = flag ? z : -z;
      GlowUtil.method2245(vec, vec.add(0.0, 5.0, 0.0), color, width, true);
      Vec3 vec2;
      GlowUtil.method2245(vec2 = vec.add(x, 0.0, 0.0), vec2.add(0.0, 5.0, 0.0), color, width, true);

      for (int i = 0; i < 4; i++) {
         GlowUtil.method2245(vec2 = vec2.add(x, 0.0, z), vec2.add(0.0, 5.0, 0.0), color, width, true);
      }

      GlowUtil.method2245(vec = vec2.add(0.0, 0.0, z), vec.add(0.0, 5.0, 0.0), color, width, true);
      Vec3 vec3;
      GlowUtil.method2245(vec3 = vec.add(x * -2.0F, 0.0, 0.0), vec3.add(0.0, 5.0, 0.0), color, width, true);

      for (int i = 0; i < 3; i++) {
         GlowUtil.method2245(vec3 = vec3.add(x * -1.0F, 0.0, z * -1), vec3.add(0.0, 5.0, 0.0), color, width, true);
      }
   }

   private void method5203(Vec3 vec, int n, int z, boolean flag) {
      vec = vec.add(0.0, 0.001, 0.0);
      float x = flag ? z : -z;
      GlowUtil.method2247(vec, vec.add(x, 0.0, 0.0), vec.add(x, 0.0, z * 2), vec.add(0.0, 0.0, z * 2), n, true);

      for (int i = 0; i < 3; i++) {
         GlowUtil.method2247(
            vec = vec.add(x, 0.0, z), vec.add(x, 0.0, 0.0), vec.add(x, 0.0, z * 2), vec.add(0.0, 0.0, z * 2), n, true
         );
      }

      Vec3 vec2;
      GlowUtil.method2247(vec2 = vec.add(x, 0.0, z), vec2.add(x, 0.0, 0.0), vec2.add(x, 0.0, z), vec2.add(0.0, 0.0, z), n, true);
   }

   private void method5204(Vec3 vec, int n, int z, boolean flag) {
      float x = flag ? z : -z;
      GlowUtil.method2247(vec, vec.add(x, 0.0, 0.0), vec.add(x, 5.0, 0.0), vec.add(0.0, 5.0, 0.0), n, true);

      for (int i = 0; i < 4; i++) {
         Vec3 vec2;
         GlowUtil.method2247(vec2 = vec.add(x, 0.0, 0.0), vec2.add(0.0, 0.0, z), vec2.add(0.0, 5.0, z), vec2.add(0.0, 5.0, 0.0), n, true);
         GlowUtil.method2247(vec = vec2.add(0.0, 0.0, z), vec.add(x, 0.0, 0.0), vec.add(x, 5.0, 0.0), vec.add(0.0, 5.0, 0.0), n, true);
      }

      Vec3 vec2;
      GlowUtil.method2247(vec2 = vec.add(x, 0.0, 0.0), vec2.add(0.0, 0.0, z), vec2.add(0.0, 5.0, z), vec2.add(0.0, 5.0, 0.0), n, true);
      GlowUtil.method2247(
         vec = vec2.add(0.0, 0.0, z), vec.add(x * -2.0F, 0.0, 0.0), vec.add(x * -2.0F, 5.0, 0.0), vec.add(0.0, 5.0, 0.0), n, true
      );
      vec = vec.add(x * -1.0F, 0.0, 0.0);

      for (int i = 0; i < 3; i++) {
         Vec3 vec3;
         GlowUtil.method2247(
            vec3 = vec.add(x * -1.0F, 0.0, 0.0), vec3.add(0.0, 0.0, z * -1), vec3.add(0.0, 5.0, z * -1), vec3.add(0.0, 5.0, 0.0), n, true
         );
         GlowUtil.method2247(
            vec = vec3.add(0.0, 0.0, z * -1), vec.add(x * -1.0F, 0.0, 0.0), vec.add(x * -1.0F, 5.0, 0.0), vec.add(0.0, 5.0, 0.0), n, true
         );
      }

      Vec3 vec3;
      GlowUtil.method2247(
         vec3 = vec.add(x * -1.0F, 0.0, 0.0), vec3.add(0.0, 0.0, z * -2), vec3.add(0.0, 5.0, z * -2), vec3.add(0.0, 5.0, 0.0), n, true
      );
   }

   private void method5205(String substringBetween, String str, String str2, Vec3 atCenterOf, String str3, int n, int n2) {
      if (!mc.isSameThread()) {
         mc.execute(() -> this.method5205(substringBetween, str, str2, atCenterOf, str3, n, n2));
      } else {
         if (this.items.stream().noneMatch(var1x -> var1x.vec3d.equals(atCenterOf))) {
            Island.method2880(substringBetween, n * 1000L);
            long doubleValue;
            long l = (doubleValue = System.currentTimeMillis() + n * 1000L) + n2 * 1000L;
            this.items.add(new ServerHelperModule.Data2(substringBetween, str, str2, atCenterOf, str3, Zenith.getInstance().getServerDetector().getAnarchy(), doubleValue, l));
         }
      }
   }

   private void method5206(Item item, Vec3 vec, double doubleValue) {
      if (!mc.isSameThread()) {
         mc.execute(() -> this.method5206(item, vec, doubleValue));
      } else {
         if (this.items2.stream().noneMatch(var1x -> var1x.vec3d.equals(vec))) {
            long l = (long)(doubleValue - System.currentTimeMillis());
            String str = item == Items.NETHER_STAR ? "beacon" : (item == Items.PRISMARINE_SHARD ? "blast" : (l > 20000L ? "bigTrap" : "trap"));
            Island.method2879(str, l);
            this.items2
               .add(
                  new ServerHelperModule.Data4(
                     item, vec, Zenith.getInstance().getServerDetector().method1638(), Zenith.getInstance().getServerDetector().getAnarchy(), doubleValue
                  )
               );
         }
      }
   }

   private Vector4f method5207(Font font, List<String> strings, int n, float f) {
      if (n == 0) {
         return font.width((String)strings.get(n + 1)) >= f ? new Vector4f(2.0F, 0.0F, 2.0F, 0.0F) : new Vector4f(2.0F);
      } else if (n == strings.size() - 1) {
         return font.width((String)strings.get(n - 1)) >= f ? new Vector4f(0.0F, 2.0F, 0.0F, 2.0F) : new Vector4f(2.0F);
      } else {
         float width = font.width((String)strings.get(n - 1));
         float width2 = font.width((String)strings.get(n + 1));
         return width >= f ? (width2 >= f ? new Vector4f() : new Vector4f(0.0F, 2.0F, 0.0F, 2.0F)) : new Vector4f(2.0F);
      }
   }

   private boolean method5208(float floatValue) {
      return floatValue == 0.0F
         || mc.level
            .players()
            .stream()
            .anyMatch(
               var1x -> var1x != mc.player
                  && !Zenith.getInstance().getFriends().method7627(var1x.getGameProfile().name())
                  && mc.player.distanceTo(var1x) <= floatValue
            );
   }

   private boolean method5209(BlockPos above) {
      long now = System.currentTimeMillis();
      boolean containsKey = this.items7.containsKey(above);
      if (containsKey && now - this.items7.get(above) < 1000L) {
         return this.items5.get(above);
      } else {
         boolean flag = this.method5210(above);
         this.items5.put(above, flag);
         this.items7.put(above, now);
         return flag;
      }
   }

   private boolean method5210(BlockPos above) {
      int n = 0;

      for (BlockPos key : UseItemUtils.method6505(above, 2.0F)) {
         if (MathUtils.method2662(net.minecraft.world.phys.Vec3.atCenterOf(key), net.minecraft.world.phys.Vec3.atCenterOf(above)) < 2.0) {
            BlockState blockState = this.method5188(key);
            if (blockState != null && !blockState.isAir()) {
               n++;
            }
         } else if (!key.equals(above.above(2).north().east())
            && !key.equals(above.above(2).north().west())
            && !key.equals(above.above(2).south().east())
            && !key.equals(above.above(2).south().west())) {
            BlockState blockState = this.method5188(key);
            if (blockState == null || blockState.isAir()) {
               n++;
            }
         }

         if (n > 1) {
            return false;
         }
      }

      return true;
   }

   private boolean method5211(BlockPos above) {
      long now = System.currentTimeMillis();
      boolean containsKey = this.items8.containsKey(above);
      if (containsKey && now - this.items8.get(above) < 1000L) {
         return this.items6.get(above);
      } else {
         boolean flag = this.method5212(above);
         this.items6.put(above, flag);
         this.items8.put(above, now);
         return flag;
      }
   }

   private boolean method5212(BlockPos above) {
      int n = 0;

      for (BlockPos key : UseItemUtils.method6505(above, 3.0F)) {
         if (Math.abs(key.getX() - above.getX()) <= 2 && Math.abs(key.getY() - above.getY()) <= 2 && Math.abs(key.getZ() - above.getZ()) <= 2) {
            BlockState blockState = this.method5188(key);
            if (blockState != null && !blockState.isAir()) {
               n++;
            }
         } else if (!key.equals(above.above(3))) {
            BlockState blockState = this.method5188(key);
            if (blockState == null || blockState.isAir()) {
               n++;
            }
         }

         if (n > 1) {
            return false;
         }
      }

      return true;
   }

   private static boolean method5213(BlockState blockState) {
      return blockState != null && !blockState.isAir();
   }

   private void method5214() {
      long now = System.currentTimeMillis();
      this.items7.entrySet().removeIf(entry -> now - entry.getValue() > 1000L);
      this.items5.entrySet().removeIf(var1x -> {
         var map = this.items7;
         Object key = var1x.getKey();
         if (map != null) {
            if (map.isEmpty()) {
               return true;
            }
         }

         return !map.containsKey(key);
      });
      this.items8.entrySet().removeIf(entry -> now - entry.getValue() > 1000L);
      this.items6.entrySet().removeIf(var1x -> {
         var map = this.items8;
         Object key = var1x.getKey();
         if (map != null) {
            if (map.isEmpty()) {
               return true;
            }
         }

         return !map.containsKey(key);
      });
      if (this.items5.size() > 1000) {
         this.items5.clear();
         this.items7.clear();
      }

      if (this.items6.size() > 1000) {
         this.items6.clear();
         this.items8.clear();
      }
   }

   private boolean method5215(BlockPos pos, byte[][][] byteseses, int n, int n2) {
      int n3 = 0;
      MutableBlockPos mutablePos = new MutableBlockPos();

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            for (int k = -n; k <= n; k++) {
               byte b;
               if ((b = byteseses[i + n][j + n][k + n]) != -1) {
                  mutablePos.set(pos.getX() + k, pos.getY() + i, pos.getZ() + j);
                  BlockState blockState = this.method5188(mutablePos);
                  boolean flag = method5213(blockState);
                  if (b != 0 && !flag || b == 0 && flag) {
                     if (++n3 > n2) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public List<ServerHelperModule.Data3> method5216() {
      return Collections.unmodifiableList(this.items3);
   }

   public BindSetting getShulkerKey() {
      return this.shulkerKey;
   }

   public Slot method5217() {
      return InventoryUtils.method910(slot -> slot.getItem().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock);
   }

   private static final class Data {
      private final long longValue;
      private final long longValue2;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      private Data(long longValue, long l) {
         this.longValue = longValue;
         this.longValue2 = l;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;
      private final String text3;
      private final Vec3 vec3d;
      private final String text4;
      private final int intValue;
      private final double doubleValue;
      private final double doubleValue2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public String text4() {
         return this.text4;
      }

      public int intValue() {
         return this.intValue;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, vec3d, text4, intValue, doubleValue, doubleValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "vec3d=" + this.vec3d + ", " + "text4=" + this.text4 + ", " + "intValue=" + this.intValue + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + "]";
      }

      public Data2(String text, String str, String str2, Vec3 vec3d, String str3, int intValue, double doubleValue, double d) {
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.vec3d = vec3d;
         this.text4 = str3;
         this.intValue = intValue;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public String getText4() {
         return this.text4;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }
   }

   public static final class Data3 {
      private final Item item;
      private final BindSetting bindSetting;
      private final float floatValue;
      private final KeyState class27;
      private final Predicate<Slot> predicate;

      public Item item() {
         return this.item;
      }

      public BindSetting bindSetting() {
         return this.bindSetting;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public KeyState class27() {
         return this.class27;
      }

      public Predicate<Slot> predicate() {
         return this.predicate;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.bindSetting, other.bindSetting) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.class27, other.class27) && java.util.Objects.equals(this.predicate, other.predicate);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(item, bindSetting, floatValue, class27, predicate);
      }

      @Override
      public String toString() {
         return "Data3[" + "item=" + this.item + ", " + "bindSetting=" + this.bindSetting + ", " + "floatValue=" + this.floatValue + ", " + "class27=" + this.class27 + ", " + "predicate=" + this.predicate + "]";
      }

      public Data3(Item item, BindSetting bindSetting, float floatValue, KeyState class2) {
         this(item, bindSetting, floatValue, class2, null);
      }

      public Data3(Item item, BindSetting bindSetting, float floatValue, KeyState class2, Predicate<Slot> predicate) {
         this.item = item;
         this.bindSetting = bindSetting;
         this.floatValue = floatValue;
         this.class27 = class2;
         this.predicate = predicate;
      }

      public Slot method5218() {
         return this.predicate != null ? InventoryUtils.method907(this.item, this.predicate) : InventoryUtils.method906(this.item);
      }

      public Predicate<Slot> method5219() {
         return this.predicate != null ? this.predicate : slot -> true;
      }

      public Item getItem() {
         return this.item;
      }

      public BindSetting getBindSetting() {
         return this.bindSetting;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public KeyState getClass27() {
         return this.class27;
      }

      public Predicate<Slot> getPredicate() {
         return this.predicate;
      }
   }

   public static final class Data4 {
      private final Item item;
      private final Vec3 vec3d;
      private final String text;
      private final int intValue;
      private final double doubleValue;

      public Item item() {
         return this.item;
      }

      public Vec3 vec3d() {
         return this.vec3d;
      }

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(item, vec3d, text, intValue, doubleValue);
      }

      @Override
      public String toString() {
         return "Data4[" + "item=" + this.item + ", " + "vec3d=" + this.vec3d + ", " + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "doubleValue=" + this.doubleValue + "]";
      }

      public Data4(Item item, Vec3 vec3d, String text, int intValue, double doubleValue) {
         this.item = item;
         this.vec3d = vec3d;
         this.text = text;
         this.intValue = intValue;
         this.doubleValue = doubleValue;
      }

      public Item getItem() {
         return this.item;
      }

      public Vec3 getVec3d() {
         return this.vec3d;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public double time() {
         return this.doubleValue;
      }
   }

   private static final class Data5 {
      private final BlockState blockState;
      private final long longValue;

      public BlockState blockState() {
         return this.blockState;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.blockState, other.blockState) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockState, longValue);
      }

      @Override
      public String toString() {
         return "Data5[" + "blockState=" + this.blockState + ", " + "longValue=" + this.longValue + "]";
      }

      private Data5(BlockState blockState, long longValue) {
         this.blockState = blockState;
         this.longValue = longValue;
      }

      public BlockState state() {
         return this.blockState;
      }

      public long time() {
         return this.longValue;
      }
   }
}
