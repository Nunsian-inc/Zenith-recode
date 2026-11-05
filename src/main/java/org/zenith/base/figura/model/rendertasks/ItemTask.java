package org.zenith.base.figura.model.rendertasks;

import org.zenith.base.figura.model.rendering.FiguraBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaError;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.model.FiguraModelPart;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "ItemTask", value = "item_task")
public class ItemTask extends RenderTask {
   private ItemStack item;
   private ItemDisplayContext displayMode = ItemDisplayContext.NONE;
   private boolean left = false;
   private final ItemStackRenderState renderState = new ItemStackRenderState();
   private int cachedComplexity;

   public ItemTask(String name, Avatar owner, FiguraModelPart parent) {
      super(name, owner, parent);
   }

   @Override
   public void render(PoseStack poseStack, SubmitNodeCollector queue, FiguraBufferSource buffer, int light, int overlay) {
      poseStack.scale(-16.0F, 16.0F, -16.0F);
      LivingEntity var10000;
      if (this.owner.renderer.entity instanceof LivingEntity living) {
         var10000 = living;
      } else {
         var10000 = null;
      }

      int newLight = this.customization.light != null ? this.customization.light : light;
      int newOverlay = this.customization.overlay != null ? this.customization.overlay : overlay;
      int seed = var10000 != null ? var10000.getId() + this.displayMode.ordinal() : 0;
      Minecraft client = Minecraft.getInstance();
      client.getItemModelResolver().updateForTopItem(this.renderState, this.item, this.displayMode, client.level, var10000, seed);
      this.renderState.submit(poseStack, queue, newLight, newOverlay, 0);
   }

   @Override
   public int getComplexity() {
      return this.cachedComplexity;
   }

   @Override
   public boolean shouldRender() {
      return super.shouldRender() && this.item != null && !this.item.isEmpty();
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = String.class, argumentNames = "item"),
            @LuaMethodOverload(argumentTypes = ItemStack.class, argumentNames = "item")
      },
      aliases = "item",
      value = "item_task.set_item"
   )
   public ItemTask setItem(Object item) {
      this.item = LuaUtils.parseItemStack("item", item);
      this.cachedComplexity = this.item.isEmpty() ? 0 : 4;
      return this;
   }

   @LuaWhitelist
   public ItemTask item(Object item) {
      return this.setItem(item);
   }

   @LuaWhitelist
   @LuaMethodDoc("item_task.get_display_mode")
   public String getDisplayMode() {
      return this.displayMode.name();
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "displayMode"),
      aliases = "displayMode",
      value = "item_task.set_display_mode"
   )
   public ItemTask setDisplayMode(@LuaNotNil String mode) {
      try {
         this.displayMode = ItemDisplayContext.valueOf(mode.toUpperCase(Locale.US));
         this.left = this.displayMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || this.displayMode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         return this;
      } catch (Exception var3) {
         throw new LuaError("Illegal display mode: \"" + mode + "\".");
      }
   }

   @LuaWhitelist
   public ItemTask displayMode(@LuaNotNil String mode) {
      return this.setDisplayMode(mode);
   }

   @Override
   public String toString() {
      return this.name + " (Item Render Task)";
   }
}
