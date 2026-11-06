package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.KeyState;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "ClickAction",
   description = "module.clickaction.description",
   category = ModuleCategory.MISC
)
public final class ClickActionModule extends Module {
   private final BindSetting friendBind = new BindSetting("module.clickAction.friendBind", "module.clickAction.friendBind.desc");
   private final BindSetting expBind = new BindSetting("module.clickAction.expBind", "module.clickAction.expBind.desc");
   private final List<ClickActionModule.Data> items = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   public static final ClickActionModule INSTANCE = new ClickActionModule();
   private Slot slot = null;
   private boolean flag = false;

   private ClickActionModule() {
      this.items
         .add(
            new ClickActionModule.Data(Items.ENDER_PEARL, new BindSetting("module.clickAction.enderPearl", "module.clickAction.enderPearl.desc"), new KeyState())
         );
      this.items
         .add(
            new ClickActionModule.Data(Items.WIND_CHARGE, new BindSetting("module.clickAction.windCharge", "module.clickAction.windCharge.desc"), new KeyState())
         );
   }

   @Override
   public List<Setting> getSettings() {
      ArrayList<Setting> settings = new ArrayList<>();
      settings.add(this.expBind);
      settings.add(this.friendBind);
      settings.addAll(this.items.stream().map(ClickActionModule.Data::getBindSetting).toList());
      return settings;
   }

   @EventTarget
   public void method5483(KeyEvent event) {
      if (event.isPress(this.friendBind.getKeyCode()) && mc.player != null && mc.level != null) {
         EntityHitResult entityHitResult = TargetUtils.method3211(
            new Rotation(mc.player.getYRot(), mc.player.getXRot()), entity -> entity instanceof Player && entity != mc.player
         );
         if (entityHitResult != null && entityHitResult.getEntity() instanceof Player player) {
            String name = player.getGameProfile().name();
            if (Zenith.getInstance().getFriends().getItems().contains(name)) {
               Zenith.getInstance().getFriends().method7629(name);
            } else {
               Zenith.getInstance().getFriends().add(name);
            }

            Zenith.getInstance().getFriends().save();
         }
      }

      this.items
         .stream()
         .filter(var1x -> event.isPress(var1x.bindSetting.getKeyCode()) && InventoryUtils.method906(var1x.item) != null)
         .forEach(data -> data.class27.setFlag(true));
      this.items.stream().filter(var1x -> event.isRelease(var1x.bindSetting.getKeyCode())).forEach(data -> {
         InventoryUtils.method927(data.item);
         data.class27.setFlag(false);
      });
      if (event.isPress(this.expBind.getKeyCode())) {
         Slot slot = InventoryUtils.method906(Items.EXPERIENCE_BOTTLE);
         if (slot == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Component.translationArg(
                     Items.EXPERIENCE_BOTTLE
                        .getName(Items.EXPERIENCE_BOTTLE.getDefaultInstance())
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                        .append(
                           Component.nullToEmpty("не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
            return;
         }
      }
   }

   @EventTarget
   public void method5484(WorldRenderEvent event) {
      PredictionsModule.INSTANCE
         .method2902(event.getMatrixStack(), this.items.stream().filter(data -> data.class27.isValue()).map(data -> data.item.getDefaultInstance()).toList());
   }

   @EventTarget(4)
   public void method5485(HandleInputEvent event) {
      if (!event.isCancelled() && mc.player != null) {
         if (this.flag) {
            this.flag = false;
         } else {
            boolean flag = mc.player.getMainHandItem().getItem().equals(Items.EXPERIENCE_BOTTLE);
            Slot slot = InventoryUtils.method906(Items.EXPERIENCE_BOTTLE);
            if (UseItemUtils.method6521(this.expBind) && slot != null) {
               PlayerSimulation playerSimulation = PlayerSimulation.method6031(3);
               Rotation rotation = new Rotation(mc.player.getYRot(), RotationUtils.method6913(playerSimulation.box.getCenter()).getFloatValue2());
               rotationManager.requestRotation(
                  new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()),
                  5,
                  this
               );
               if (!flag) {
                  if (KeyBindUtils.method2630(ClickActionModule.class)) {
                     KeyBindUtils.method2627(ClickActionModule.class, () -> {
                        if (this.slot == null) {
                           this.slot = slot;
                        }

                        InventoryUtils.method900(slot, InteractionHand.MAIN_HAND, true);
                        this.flag = true;
                     });
                  }
               } else if (this.stopWatch.method7546(70L) && rotationManager.getEffectiveRotation().wrappedDeltaTo(rotation).isWithinBounds(180.0F, 10.0F)) {
                  UseItemUtils.useItem(InteractionHand.MAIN_HAND);
                  event.cancel();
                  this.stopWatch.reset();
               }
            } else if (this.slot != null) {
               KeyBindUtils.method2627(ClickActionModule.class, () -> {
                  if (!UseItemUtils.method6521(this.expBind)) {
                     InventoryUtils.method900(this.slot, InteractionHand.MAIN_HAND, true);
                     this.slot = null;
                  }
               });
            }
         }
      }
   }

   public List<ClickActionModule.Data> method5486() {
      return Collections.unmodifiableList(this.items);
   }

   public BindSetting getExpBind() {
      return this.expBind;
   }

   public static final class Data {
      private final Item item;
      private final BindSetting bindSetting;
      private final KeyState class27;

      public Item item() {
         return this.item;
      }

      public BindSetting bindSetting() {
         return this.bindSetting;
      }

      public KeyState class27() {
         return this.class27;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.bindSetting, other.bindSetting) && java.util.Objects.equals(this.class27, other.class27);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(item, bindSetting, class27);
      }

      @Override
      public String toString() {
         return "Data[" + "item=" + this.item + ", " + "bindSetting=" + this.bindSetting + ", " + "class27=" + this.class27 + "]";
      }

      public Data(Item item, BindSetting bindSetting, KeyState class2) {
         this.item = item;
         this.bindSetting = bindSetting;
         this.class27 = class2;
      }

      public Item getItem() {
         return this.item;
      }

      public BindSetting getBindSetting() {
         return this.bindSetting;
      }

      public KeyState getClass27() {
         return this.class27;
      }
   }
}
