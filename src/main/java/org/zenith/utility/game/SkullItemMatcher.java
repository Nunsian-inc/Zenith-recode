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

   public SkullItemMatcher(String var1, ItemMatcher.Type var2, String var3) {
      this(var3, var1, var1, var2);
   }

   public SkullItemMatcher(String var1, String var2, String var3, ItemMatcher.Type var4) {
      super(Items.PLAYER_HEAD.getDefaultInstance(), var2, var3, var4);
      this.text = var1;
      this.text2 = this.method2783(var1);
      Builder var5 = DataComponentPatch.builder();
      var var6 = Optional.of("");
      var var7 = Optional.of(UUID.randomUUID());
      ImmutableMultimap var8 = ImmutableMultimap.of("textures", new Property("textures", var1));
      PropertyMap var9 = new PropertyMap(var8);
      ResolvableProfile var10 = ResolvableProfile.createResolved(new GameProfile(var7.orElse(UUID.randomUUID()), var6.orElse(""), var9));
      var5.set(DataComponents.PROFILE, var10);
      ItemInput var11 = new ItemInput(this.itemStack.typeHolder(), var5.build());

      try {
         this.itemStack = var11.createItemStack(1);
      } catch (CommandSyntaxException var13) {
      }
   }

   private String method2783(String var1) {
      try {
         String var2 = new String(Base64.getDecoder().decode(var1));
         int var3 = var2.indexOf("\"url\"");
         if (var3 != -1) {
            int var4 = var2.indexOf("\"", var3 + 5);
            if (var4 != -1) {
               int var5 = var2.indexOf("\"", var4 + 1);
               if (var5 != -1) {
                  return var2.substring(var4 + 1, var5);
               }
            }
         }
      } catch (Exception var6) {
      }

      return null;
   }

   @Override
   public boolean method2672(ItemStack var1) {
      if (!super.method2672(var1)) {
         return false;
      } else {
         ResolvableProfile var2 = var1.get(DataComponents.PROFILE);
         if (var2 != null) {
            PropertyMap var3 = var2.partialProfile().properties();
            if (var3 != null && var3.containsKey("textures")) {
               for (Property var5 : var3.get("textures")) {
                  if (this.text2 != null) {
                     String var6 = this.method2783(var5.value());
                     if (var6 != null && var6.equals(this.text2)) {
                        return true;
                     }
                  }

                  if (var5.value().contains(this.text)) {
                     return true;
                  }
               }
            }
         }

         CustomData var7 = var1.get(DataComponents.CUSTOM_DATA);
         CompoundTag var8 = var7 != null ? var7.copyTag() : null;
         if (var8 != null && var8.contains("SkullOwner")) {
            String var9 = var8.get("SkullOwner").toString();
            if (this.text2 != null && var9.contains(this.text2)) {
               return true;
            }

            if (var9.contains(this.text)) {
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
