package org.zenith.client.screens.nlgui.elements.setting;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.misc.ScrollHandler;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiItemSelectSetting extends GuiSetting<BlockListSetting> {
   private final Animation animationExpanded = new Animation(200L, Easing.INSTANCE13);
   private SearchBox searchBox;
   private final ScrollHandler scrollHandler = new ScrollHandler();
   private boolean sortBySelected = false;
   private BoundsData bounds;
   private BoundsData rectBounds;
   private BoundsData exitBounds;
   private BoundsData searchBounds;
   private BoundsData sortBounds;
   private BoundsData listScissorBounds;
   private final Map<Object, BoundsData> itemBounds = new HashMap<>();
   private boolean expanded;

   public GuiItemSelectSetting(BlockListSetting setting) {
      this(setting, 166.0F);
   }

   public GuiItemSelectSetting(BlockListSetting setting, float width) {
      super(width, setting);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double anchorMouseX, double lastClickY, MouseButton enum2) {
      if (this.bounds != null && this.bounds.method8092(anchorMouseX, lastClickY, 2.0F)) {
         this.expanded = (!this.expanded);
         return true;
      } else if (this.expanded && this.rectBounds != null && this.rectBounds.contains(anchorMouseX, lastClickY)) {
         return true;
      } else {
         this.expanded = false;
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext menuDrawContext, float x, float y, float floatValue, float centeredTextY, float previewAlpha) {
      ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (currentStyle != null) {
         this.animationVisible.updateVisibility(this.setting.isVisible());
         previewAlpha *= this.animationVisible.getValue();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_REGULAR.getFont(5.4F);
         float maxWidth = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
         ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
         this.drawDefault(menuDrawContext, x, y, "i", this.setting.getName(), this.setting.getDescription(), font, font2, floatValue, centeredTextY, maxWidth, zenithColor, zenithColor2, zenithColor3);
         float width = 6.0F;
         float height = 6.0F;
         this.bounds = new BoundsData(floatValue + this.width - width, centeredTextY + (this.getHeight() - height) / 2.0F, width, height);
         menuDrawContext.drawRoundedRect(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            ProxyDotRadiusData.method8069(1.0F),
            currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
         );
         menuDrawContext.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            width,
            height,
            -0.5F,
            ProxyDotRadiusData.method8069(1.0F),
            currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha)
         );
         Font font3 = Fonts.NEW_ICONS.getFont(5.5F);
         menuDrawContext.drawText(
            font3,
            "v",
            this.bounds.getFloatValue() + (width - font3.width("v")) / 2.0F,
            this.bounds.getFloatValue2() + (height - font3.height()) / 2.0F,
            currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
         );
      }
   }

   @Override
   public void renderPriority(MenuDrawContext menuDrawContext, float x, float y, float f, float f2, float previewAlpha, float f3) {
      this.animationExpanded.updateVisibility(this.expanded);
      if (!(this.animationExpanded.getValue() <= 0.0F) && this.bounds != null) {
         if (this.searchBox == null) {
            this.searchBox = new SearchBox(new UVPair(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.3F), "Search...", 0.0F);
            this.searchBox.setType(SearchBox.Type.ENGLISH);
         }

         float f4;
         float height = (f4 = GuiStyle.PADDING.intValue() * 2.0F) * 2.0F + this.getHeight();
         float height2 = 17.0F;
         float f5 = 115.0F;
         float width = 126.0F;
         float f6 = f4 * 2.0F;
         float height3 = height + f4 + height2 + f6 + f5;
         float floatValue = this.bounds.getFloatValue() + GuiStyle.PADDING.intValue() * 2.0F;
         float y2 = this.bounds.getFloatValue2() - height3 / 2.0F;
         this.rectBounds = new BoundsData(floatValue, y2, width, height3);
         previewAlpha *= this.animationExpanded.getValue();
         menuDrawContext.pose().pushMatrix();
         menuDrawContext.pose().translate(floatValue, this.bounds.getFloatValue2());
         menuDrawContext.pose().scale(this.animationExpanded.getValue(), this.animationExpanded.getValue());
         menuDrawContext.pose().translate(-floatValue, -this.bounds.getFloatValue2());
         ZenithStyle currentStyle = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (currentStyle != null) {
            RenderShaders.method5289(
               MatrixUtils.method664(menuDrawContext.pose()),
               floatValue,
               y2,
               width,
               height3,
               12.0F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               ZenithColor.INSTANCE.method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedRect(
               floatValue,
               y2,
               width,
               height,
               ProxyDotRadiusData.method8074(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedRect(
               floatValue, y2 + height, width, f4 + height2 + f6, ProxyDotRadiusData.INSTANCE, currentStyle.getPanelLeftBackground().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedRect(
               floatValue,
               y2 + height + height2 + f6,
               width,
               f5 + f4,
               ProxyDotRadiusData.method8076(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getRightBackground().getZenithColor().method7294(previewAlpha)
            );
            Font font = Fonts.NEW_ICONS.getFont(4.0F);
            float floatValue2 = floatValue + width - font.width("2") - f4;
            float y3 = y2 + f4 + font.height();
            this.exitBounds = new BoundsData(floatValue2, y3, 5.0F, 5.0F);
            menuDrawContext.drawText(font, "2", floatValue2, y3, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font3 = Fonts.NEW_REGULAR.getFont(5.3F);
            float maxWidth = width / 1.4F - GuiStyle.PADDING.intValue();
            ZenithColor zenithColor = currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor2 = currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha);
            ZenithColor zenithColor3 = currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha);
            this.drawDefault(
               menuDrawContext,
               x,
               y,
               "i",
               this.setting.getName(),
               this.setting.getDescription(),
               font2,
               font3,
               floatValue + f4,
               y2 + f4,
               maxWidth,
               zenithColor,
               zenithColor2,
               zenithColor3
            );
            float y4 = y2 + height + f4;
            this.searchBounds = new BoundsData(floatValue + f4, y4, width - f4 * 2.0F, height2);
            menuDrawContext.drawRoundedRect(
               this.searchBounds.getFloatValue(),
               this.searchBounds.getFloatValue2(),
               this.searchBounds.width(),
               this.searchBounds.height(),
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
            );
            menuDrawContext.drawRoundedBorder(
               this.searchBounds.getFloatValue(),
               this.searchBounds.getFloatValue2(),
               this.searchBounds.width(),
               this.searchBounds.height(),
               0.1F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha)
            );
            float width2 = 14.0F;
            float width3 = this.searchBounds.width() - f4 * 2.0F - width2 - f4;
            this.searchBox.setWidth(width3);
            this.searchBox.setMaxLength(35);
            this.searchBox
               .method987(
                  menuDrawContext,
                  this.searchBounds.getFloatValue() + f4,
                  this.searchBounds.getFloatValue2() + (height2 - Fonts.NEW_MEDIUM.getFont(5.3F).height()) / 2.0F,
                  currentStyle.getTextEnable().getZenithColor().method7294(previewAlpha),
                  currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
               );
            this.sortBounds = new BoundsData(
               this.searchBounds.getFloatValue() + this.searchBounds.width() - f4 - width2, y4 + (height2 - width2) / 2.0F, width2, width2
            );
            menuDrawContext.drawRoundedRect(
               this.sortBounds.getFloatValue(),
               this.sortBounds.getFloatValue2(),
               width2,
               width2,
               ProxyDotRadiusData.method8069(2.0F),
               currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
            );
            Font font4 = Fonts.NEW_ICONS.getFont(5.01F);
            menuDrawContext.drawText(
               font4,
               "W",
               this.sortBounds.getFloatValue() + (width2 - font4.width("W")) / 2.0F,
               this.sortBounds.getFloatValue2() + (width2 - font4.height()) / 2.0F,
               currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha)
            );

            List<Object> filtered = this.getFilteredAndSortedEntries();
            float height4 = 13.0F;
            float f7 = 13.0F + GuiStyle.PADDING.intValue();
            float f8 = filtered.size() * f7;
            this.scrollHandler.setDoubleValue(Math.max(0.0F, f8 - f5));
            this.scrollHandler.update();
            float y5 = y4 + height2 + f6;
            this.listScissorBounds = new BoundsData(floatValue, y5, width, f5 - f4);
            float f9 = y5 - (float)this.scrollHandler.method6103();
            float floatValue3 = floatValue + f4;
            float width4 = 105.0F;
            this.itemBounds.clear();
            menuDrawContext.enableScissor((int)floatValue, (int)y5, (int)(floatValue + width), (int)(y5 + f5));
            Font font5 = Fonts.NEW_MEDIUM.getFont(5.3F);
            float f10 = 8.0F;
            float x2 = 0.5F;
            int n = 0;
            ZenithColor zenithColor4 = currentStyle.getFieldSurfaceBackground().method1447(previewAlpha);
            ZenithColor zenithColor5 = currentStyle.getPrimaryColor().getZenithColor().method7294(0.25F * previewAlpha);
            ZenithColor zenithColor6 = currentStyle.getFieldBorder().method1447(previewAlpha);

            for (Object entryObj : filtered) {
               if (f9 + n * f7 < y5 - f7) {
                  n++;
               } else {
                  float y6;
                  if ((y6 = f9 + n * f7) > y5 + f5) {
                     break;
                  }

                  boolean flag = this.isSelected(entryObj);
                  BoundsData boundsData = new BoundsData(floatValue3, y6, width4, height4);
                  this.itemBounds.put(entryObj, boundsData);
                  menuDrawContext.drawRoundedRect(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     boundsData.height(),
                     ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
                     flag ? zenithColor5 : zenithColor4
                  );
                  menuDrawContext.drawRoundedBorder(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     boundsData.height(),
                     0.1F,
                     ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
                     zenithColor6
                  );
                  menuDrawContext.pose().pushMatrix();
                  menuDrawContext.pose().translate(floatValue3 + 4.0F, y6 + (height4 - f10) / 2.0F);
                  menuDrawContext.pose().scale(x2, x2);
                  menuDrawContext.item(this.getStack(entryObj), 0, 0);
                  menuDrawContext.pose().popMatrix();
                  String name = this.getDisplayName(entryObj);
                  float floatValue4 = floatValue3 + GuiStyle.PADDING.intValue() + f10 + GuiStyle.PADDING.intValue() / 2.0F;
                  menuDrawContext.drawText(
                     font5,
                     font5.clip(name, boundsData.getFloatValue() + boundsData.width() - GuiStyle.PADDING.intValue() - floatValue4),
                     floatValue4,
                     y6 + (height4 - font5.height()) / 2.0F,
                     (flag ? currentStyle.getTextEnable() : currentStyle.getTextSecondary()).getZenithColor().method7294(previewAlpha)
                  );
                  n++;
               }
            }

            menuDrawContext.disableScissor();
            float width5 = 1.5F;
            float floatValue4b = floatValue + width - f4 - width5;
            float height5 = f5 - f4 / 2.0F;
            if (this.scrollHandler.getDoubleValue() > 0.0) {
               menuDrawContext.drawRoundedRect(
                  floatValue4b, y5, width5, height5, ProxyDotRadiusData.method8069(0.5F), currentStyle.getFieldSurfaceBackground().getZenithColor().method7294(previewAlpha)
               );
               float f11 = (float)(this.scrollHandler.method6103() / this.scrollHandler.getDoubleValue());
               float height6 = Math.max(height5 * (height5 / (float)(height5 + this.scrollHandler.getDoubleValue())), 12.0F);
               float f12 = Math.max(1.0F, height5 - height6);
               float y6b = y5 + f12 * f11;
               y6b = Math.min(y5 + height5 - height6, y6b);
               menuDrawContext.drawRoundedRect(floatValue4b, y6b, width5, height6, ProxyDotRadiusData.method8069(0.5F), currentStyle.getFieldBorder().getZenithColor().method7294(previewAlpha));
            }

            menuDrawContext.pose().popMatrix();
         }
      }
   }

   private boolean isItemsMode() {
      return this.setting.isItemsMode();
   }

   private boolean isSelected(Object obj) {
      if (this.isItemsMode() && obj instanceof Item item) {
         return this.setting.method1622(item);
      }
      if (obj instanceof Block block) {
         return this.setting.method1621(block);
      }
      return false;
   }

   private void toggle(Object obj) {
      if (this.isItemsMode() && obj instanceof Item item) {
         if (this.setting.method1622(item)) {
            this.setting.method1620(item);
         } else {
            this.setting.method1618(item);
         }
      } else if (obj instanceof Block block) {
         if (this.setting.method1621(block)) {
            this.setting.method1619(block);
         } else {
            this.setting.method1617(block);
         }
      }
   }

   private ItemStack getStack(Object obj) {
      if (obj instanceof Item item) {
         return item.getDefaultInstance();
      }
      if (obj instanceof Block block) {
         return block.asItem().getDefaultInstance();
      }
      return ItemStack.EMPTY;
   }

   private String getDisplayName(Object obj) {
      String id;
      if (obj instanceof Item item) {
         id = item.getDescriptionId().replaceFirst("^item\\.minecraft\\.", "").replaceFirst("^block\\.minecraft\\.", "");
      } else if (obj instanceof Block block) {
         id = block.getDescriptionId().replaceFirst("^block\\.minecraft\\.", "").replaceFirst("^item\\.minecraft\\.", "");
      } else {
         id = obj.toString();
      }
      String name = id.replaceAll("_", " ");
      if (!name.isEmpty()) {
         name = name.substring(0, 1).toUpperCase() + name.substring(1);
      }
      return name;
   }

   private String getSearchName(Object obj) {
      String id;
      if (obj instanceof Item item) {
         id = item.getDescriptionId().replaceFirst("^item\\.minecraft\\.", "").replaceFirst("^block\\.minecraft\\.", "");
      } else if (obj instanceof Block block) {
         id = block.getDescriptionId().replaceFirst("^block\\.minecraft\\.", "").replaceFirst("^item\\.minecraft\\.", "");
      } else {
         id = obj.toString();
      }
      return id.replaceAll("_", " ").toLowerCase();
   }

   private List<Object> getFilteredAndSortedEntries() {
      String str = this.searchBox != null && !this.searchBox.isEmpty() ? this.searchBox.getText().toLowerCase().trim() : "";
      Stream<Object> stream = this.isItemsMode() ? getAllItems() : getAllBlocks().map(b -> (Object) b);
      if (!str.isEmpty()) {
         final String s = str;
         stream = stream.filter(obj -> this.getSearchName(obj).contains(s) || this.getSearchName(obj).replace(" ", "_").contains(s.replace(" ", "_")));
      }
      var list = new ArrayList<>(stream.toList());
      if (this.sortBySelected || str.isEmpty()) {
         list.sort((a, b) -> Boolean.compare(!this.isSelected(a), !this.isSelected(b)));
      }
      return list;
   }

   private static Stream<Block> getAllBlocks() {
      return Stream.of(Blocks.class.getDeclaredFields())
         .filter(field -> Modifier.isStatic(field.getModifiers()))
         .filter(field -> Modifier.isPublic(field.getModifiers()))
         .filter(field -> Block.class.isAssignableFrom(field.getType()))
         .map(field -> {
            try {
               return (Block)field.get(null);
            } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
            }
         })
         .filter(block -> block != Blocks.AIR);
   }

   private static Stream<Object> getAllItems() {
      return BuiltInRegistries.ITEM.stream()
         .filter(item -> item != Items.AIR)
         .map(item -> (Object) item);
   }

   @Override
   public boolean onMousePriorityClicked(double lastClickX, double lastClickY, MouseButton enum2) {
      if (!this.expanded || this.rectBounds == null) {
         return false;
      } else if (!this.rectBounds.contains(lastClickX, lastClickY)) {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }
         return false;
      } else if (this.exitBounds != null && this.exitBounds.method8092(lastClickX, lastClickY, 2.0F)) {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }
         return true;
      } else if (this.searchBounds != null && this.searchBounds.contains(lastClickX, lastClickY)) {
         if (this.searchBox != null) {
            this.searchBox.setSelected(true);
         }
         return true;
      } else {
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }

         if (this.sortBounds != null && this.sortBounds.method8092(lastClickX, lastClickY, 2.0F)) {
            this.sortBySelected = (!this.sortBySelected);
            this.scrollHandler.setDoubleValue2(0.0);
            return true;
         } else {
            if (this.listScissorBounds != null && this.listScissorBounds.contains(lastClickX, lastClickY) && enum2.getGlfwButton() == 0) {
               for (Entry<Object, BoundsData> entry : this.itemBounds.entrySet()) {
                  if (entry.getValue().contains(lastClickX, lastClickY)) {
                     this.toggle(entry.getKey());
                     return true;
                  }
               }
            }
            return true;
         }
      }
   }

   @Override
   public boolean onMousePriorityScroll(double lastClickX, double lastClickY, double d, double d2) {
      if (!this.expanded) {
         return false;
      } else if (this.listScissorBounds != null && this.listScissorBounds.contains(lastClickX, lastClickY)) {
         this.scrollHandler.method6104(d2 * 10.0);
         return true;
      } else {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return (boolean)(this.expanded && this.searchBox != null && this.searchBox.keyPressed(keyCode, keycode, modifiers)
         ? 1
         : super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers))));
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      return (boolean)(this.expanded && this.searchBox != null && this.searchBox.charTyped(codepoint, n) ? 1 : super.charTyped(new CharacterEvent(codepoint)));
   }
}