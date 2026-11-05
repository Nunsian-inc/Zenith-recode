package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.screens.shulker.ShulkerTooltipComponent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

@ModuleInfo(
   name = "ShulkerPreview",
   category = ModuleCategory.MISC,
   description = "module.shulkerpreview.description"
)
public class ShulkerPreviewModule extends Module {
   public static final ShulkerPreviewModule INSTANCE = new ShulkerPreviewModule();
   private final BooleanSetting alwaysShow = new BooleanSetting("module.shulkerPreview.alwaysShow", "module.shulkerPreview.alwaysShow.desc", false);
   private final BindSetting previewKey = new BindSetting(
      "module.shulkerPreview.previewKey", "module.shulkerPreview.previewKey.desc", -1, () -> !this.alwaysShow.isEnabled()
   );
   private final BooleanSetting worldDisplay = new BooleanSetting("module.shulkerPreview.worldDisplay", "module.shulkerPreview.worldDisplay.desc", true);
   private final SliderSetting worldScale = new SliderSetting(
      "module.shulkerPreview.worldScale", 0.7F, 0.3F, 1.5F, 0.05F, "module.shulkerPreview.worldScale.desc", "x", this.worldDisplay::isEnabled, null
   );
   private final Map<ItemEntity, ShulkerPreviewModule.Data> items = new WeakHashMap<>();

   private ShulkerPreviewModule() {
   }

   public static Optional<TooltipComponent> method1387(ItemStack stack) {
      if (INSTANCE.isEnabled() && INSTANCE.method1389()) {
         if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
            NonNullList size = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemContainerContents itemContainerContents = stack.get(DataComponents.CONTAINER);
            if (itemContainerContents != null) {
               itemContainerContents.copyInto(size);
            }

            return Optional.of(new ShulkerPreviewModule.Entry(size));
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static ClientTooltipComponent method1388(TooltipComponent tooltipComponent) {
      return tooltipComponent instanceof ShulkerPreviewModule.Entry entry ? new ShulkerTooltipComponent(entry.getDefaultedList()) : null;
   }

   private boolean method1389() {
      if (this.alwaysShow.isEnabled()) {
         return true;
      } else {
         int keyCode = this.previewKey.getKeyCode();
         return keyCode == -1 ? false : this.previewKey.isVisible() && UseItemUtils.method6522(UseItemUtils.method6509(keyCode), keyCode);
      }
   }

   @EventTarget
   public void method1390(DrawEvent event) {
      if (this.worldDisplay.isEnabled() && this.method1389() && mc.player != null && mc.level != null && mc.getEntityRenderDispatcher().camera != null) {
         CustomDrawContext customDrawContext = event.getCustomDrawContext();
         if (customDrawContext != null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity instanceof ItemEntity player) {
                  ItemStack stack = player.getItem();
                  if (!stack.isEmpty() && method1396(stack)) {
                     this.method1391(customDrawContext, player, stack);
                  }
               }
            }
         }
      }
   }

   private void method1391(CustomDrawContext customDrawContext, ItemEntity player, ItemStack stack) {
      Vec3 vec = MathUtils.method2656(player);
      AABB move = player.getBoundingBox().move(vec.subtract(player.position()));
      Vector4d vector4d = this.method1393(move);
      if (vector4d != null) {
         ShulkerTooltipComponent tooltipComponent = this.method1392(player, stack);
         float f = this.worldScale.getValue3();
         float width = tooltipComponent.getWidth(mc.font);
         float height = tooltipComponent.getHeight(mc.font);
         float x = ((float)vector4d.x + (float)vector4d.z) / 2.0F;
         float y = (float)vector4d.y - 4.0F - height * f / 2.0F;
         float f2 = x - width / 2.0F;
         float f3 = y - height / 2.0F;
         this.pushCenteredScale(customDrawContext, x, y, f, f);
         tooltipComponent.extractImage(mc.font, Math.round(f2), Math.round(f3), tooltipComponent.getWidth(mc.font), tooltipComponent.getHeight(mc.font), customDrawContext);
         customDrawContext.popMatrix();
      }
   }

   private ShulkerTooltipComponent method1392(ItemEntity player, ItemStack stack) {
      ItemContainerContents containerComponent = stack.get(DataComponents.CONTAINER);
      ShulkerPreviewModule.Data data = this.items.get(player);
      if (data == null || data.getContainerComponent() != containerComponent) {
         data = new ShulkerPreviewModule.Data(containerComponent, new ShulkerTooltipComponent(method1397(stack)));
         this.items.put(player, data);
      }

      return data.getShulkerTooltipComponent();
   }

   private void pushCenteredScale(CustomDrawContext customDrawContext, float x, float y, float f, float y2) {
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().scale(f, y2);
      customDrawContext.pose().translate(-x, -y);
   }

   private Vector4d method1393(AABB move) {
      Vector4d vector4d = null;

      for (Vec3 vec : this.method1394(move)) {
         if (this.method1395(vec)) {
            Vec3 vec2 = ProjectionUtils.method301(vec);
            if (vec2 != null && !(vec2.z <= 0.0)) {
               if (vector4d == null) {
                  vector4d = new Vector4d(vec2.x, vec2.y, vec2.x, vec2.y);
               } else {
                  vector4d.x = Math.min(vector4d.x, vec2.x);
                  vector4d.y = Math.min(vector4d.y, vec2.y);
                  vector4d.z = Math.max(vector4d.z, vec2.x);
                  vector4d.w = Math.max(vector4d.w, vec2.y);
               }
            }
         }
      }

      return vector4d;
   }

   private Vec3[] method1394(AABB move) {
      return new Vec3[]{
         new Vec3(move.minX, move.minY, move.minZ),
         new Vec3(move.minX, move.maxY, move.minZ),
         new Vec3(move.maxX, move.minY, move.minZ),
         new Vec3(move.maxX, move.maxY, move.minZ),
         new Vec3(move.minX, move.minY, move.maxZ),
         new Vec3(move.minX, move.maxY, move.maxZ),
         new Vec3(move.maxX, move.minY, move.maxZ),
         new Vec3(move.maxX, move.maxY, move.maxZ)
      };
   }

   private boolean method1395(Vec3 vec) {
      if (mc.gameRenderer != null && mc.gameRenderer.mainCamera() != null) {
         Vec3 position = mc.gameRenderer.mainCamera().position();
         Vec3 vec2 = vec.subtract(position);
         float xRot = mc.gameRenderer.mainCamera().xRot();
         float yRot = mc.gameRenderer.mainCamera().yRot();
         double d = Math.toRadians(xRot);
         double d2 = Math.toRadians(yRot);
         Vec3 vec3 = new Vec3(-Math.sin(d2) * Math.cos(d), -Math.sin(d), Math.cos(d2) * Math.cos(d));
         return vec2.dot(vec3) > 0.0;
      } else {
         return false;
      }
   }

   private static boolean method1396(ItemStack stack) {
      return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
   }

   private static NonNullList<ItemStack> method1397(ItemStack stack) {
      NonNullList size = NonNullList.withSize(27, ItemStack.EMPTY);
      ItemContainerContents itemContainerContents = stack.get(DataComponents.CONTAINER);
      if (itemContainerContents != null) {
         itemContainerContents.copyInto(size);
      }

      return size;
   }

   private static final class Data {
      private final ItemContainerContents containerComponent;
      private final ShulkerTooltipComponent shulkerTooltipComponent;

      public ItemContainerContents containerComponent() {
         return this.containerComponent;
      }

      public ShulkerTooltipComponent shulkerTooltipComponent() {
         return this.shulkerTooltipComponent;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.containerComponent, other.containerComponent) && java.util.Objects.equals(this.shulkerTooltipComponent, other.shulkerTooltipComponent);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(containerComponent, shulkerTooltipComponent);
      }

      @Override
      public String toString() {
         return "Data[" + "containerComponent=" + this.containerComponent + ", " + "shulkerTooltipComponent=" + this.shulkerTooltipComponent + "]";
      }

      private Data(ItemContainerContents containerComponent, ShulkerTooltipComponent shulkerTooltipComponent) {
         this.containerComponent = containerComponent;
         this.shulkerTooltipComponent = shulkerTooltipComponent;
      }

      public ItemContainerContents getContainerComponent() {
         return this.containerComponent;
      }

      public ShulkerTooltipComponent getShulkerTooltipComponent() {
         return this.shulkerTooltipComponent;
      }
   }

   public static final class Entry implements TooltipComponent {
      private final NonNullList<ItemStack> defaultedList = NonNullList.withSize(27, ItemStack.EMPTY);

      public Entry(NonNullList<ItemStack> size) {
         for (int i = 0; i < 27 && i < size.size(); i++) {
            ItemStack stack = (ItemStack)size.get(i);
            this.defaultedList.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
         }
      }

      public NonNullList<ItemStack> getDefaultedList() {
         return this.defaultedList;
      }
   }
}
