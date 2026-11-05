package org.zenith.utility.game;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPatch.Builder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;

public class SkullItemMatcher extends ItemMatcher {
   private final String text;
   private final String text2;

   public SkullItemMatcher(String str, ItemMatcher.Type type, String text) {
      this(text, str, str, type);
   }

   public SkullItemMatcher(String text, String str, String str2, ItemMatcher.Type type) {
      super(Items.PLAYER_HEAD.getDefaultInstance(), str, str2, type);
      this.text = text;
      this.text2 = this.method2783(text);
      Builder builder = DataComponentPatch.builder();
      var optionalStr = Optional.of("");
      var optionalUuid = Optional.of(UUID.randomUUID());
      ImmutableMultimap properties = ImmutableMultimap.of("textures", new Property("textures", text));
      PropertyMap propertyMap = new PropertyMap(properties);
      ResolvableProfile resolved = ResolvableProfile.createResolved(new GameProfile(optionalUuid.orElse(UUID.randomUUID()), optionalStr.orElse(""), propertyMap));
      builder.set(DataComponents.PROFILE, resolved);
      ItemInput itemInput = new ItemInput(this.itemStack.typeHolder(), builder.build());

      try {
         this.itemStack = itemInput.createItemStack(1);
      } catch (CommandSyntaxException e) {
      }
   }

   private String method2783(String text) {
      try {
         String str = new String(Base64.getDecoder().decode(text));
         int indexOf = str.indexOf("\"url\"");
         if (indexOf != -1) {
            int n = str.indexOf("\"", indexOf + 5);
            if (n != -1) {
               int endIndex = str.indexOf("\"", n + 1);
               if (endIndex != -1) {
                  return str.substring(n + 1, endIndex);
               }
            }
         }
      } catch (Exception e) {
      }

      return null;
   }

   @Override
   public boolean method2672(ItemStack stack) {
      if (!super.method2672(stack)) {
         return false;
      } else {
         ResolvableProfile resolvableProfile = stack.get(DataComponents.PROFILE);
         if (resolvableProfile != null) {
            PropertyMap properties = resolvableProfile.partialProfile().properties();
            if (properties != null && properties.containsKey("textures")) {
               for (Property property : properties.get("textures")) {
                  if (this.text2 != null) {
                     String str = this.method2783(property.value());
                     if (str != null && str.equals(this.text2)) {
                        return true;
                     }
                  }

                  if (property.value().contains(this.text)) {
                     return true;
                  }
               }
            }
         }

         CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
         CompoundTag copyTag = customData != null ? customData.copyTag() : null;
         if (copyTag != null && copyTag.contains("SkullOwner")) {
            String string = copyTag.get("SkullOwner").toString();
            if (this.text2 != null && string.contains(this.text2)) {
               return true;
            }

            if (string.contains(this.text)) {
               return true;
            }
         }

         return false;
      }
   }

   public String getText() {
      return this.text;
   }
}
