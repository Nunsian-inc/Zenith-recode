package org.zenith.base.figura.model.rendering.texture;

import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.figura.model.TextureCustomization;

public class FiguraTextureSet {
   public final String name;
   public final FiguraTexture[] textures = new FiguraTexture[4];

   public FiguraTextureSet(String name, FiguraTexture mainData, FiguraTexture emissiveData, FiguraTexture specularData, FiguraTexture normalData) {
      this.name = name;
      this.textures[0] = mainData;
      this.textures[1] = emissiveData;
      this.textures[2] = specularData;
      this.textures[3] = normalData;
   }

   public void clean() {
      for (FiguraTexture texture : this.textures) {
         if (texture != null) {
            texture.close();
         }
      }
   }

   public void uploadIfNeeded() {
      for (FiguraTexture texture : this.textures) {
         if (texture != null) {
            texture.uploadIfDirty();
         }
      }
   }

   public int getWidth() {
      for (FiguraTexture texture : this.textures) {
         if (texture != null) {
            return texture.getWidth();
         }
      }

      return -1;
   }

   public int getHeight() {
      for (FiguraTexture texture : this.textures) {
         if (texture != null) {
            return texture.getHeight();
         }
      }

      return -1;
   }

   public Identifier getOverrideTexture(UUID owner, TextureCustomization pair) {
      FiguraTextureSet.OverrideType type;
      if (pair != null && (type = pair.getOverrideType()) != null) {
         return switch (type) {
            case SKIN, CAPE, ELYTRA -> {
               ClientPacketListener connection = Minecraft.getInstance().getConnection();
               if (connection == null) {
                  Object obj = null;
                  Identifier id = (Identifier)obj;
                  yield id;
               } else {
                  PlayerInfo info = connection.getPlayerInfo(owner);
                  if (info == null) {
                     Object obj = null;
                     Identifier id = (Identifier)obj;
                     yield id;
                  } else {
                     Identifier id = switch (type) {
                        case CAPE -> info.getSkin().cape() == null ? null : info.getSkin().cape().texturePath();
                        case ELYTRA -> info.getSkin().elytra() == null ? Identifier.withDefaultNamespace("elytra") : info.getSkin().elytra().texturePath();
                        default -> info.getSkin().body().texturePath();
                     };
                     yield id;
                  }
               }
            }
            case RESOURCE -> {
               Identifier location;
               try {
                  location = Identifier.parse(String.valueOf(pair.getValue()));
               } catch (Exception e) {
                  location = MissingTextureAtlasSprite.getLocation();
                  yield location;
               }

               Identifier id = location;
               yield id;
            }
            case PRIMARY -> {
               Identifier location = this.textures[0] == null ? null : this.textures[0].getLocation();
               Identifier id = location;
               yield id;
            }
            case SECONDARY -> {
               Identifier location = this.textures[1] == null ? null : this.textures[1].getLocation();
               Identifier id = location;
               yield id;
            }
            case SPECULAR -> {
               Identifier location = this.textures[2] == null ? null : this.textures[2].getLocation();
               Identifier id = location;
               yield id;
            }
            case NORMAL -> {
               Identifier location = this.textures[3] == null ? null : this.textures[3].getLocation();
               Identifier id = location;
               yield id;
            }
            case CUSTOM -> {
               Identifier location;
               try {
                  location = ((FiguraTexture)pair.getValue()).getLocation();
               } catch (Exception e) {
                  location = MissingTextureAtlasSprite.getLocation();
                  Identifier id = location;
                  yield id;
               }

               Identifier id = location;
               yield id;
            }
         };
      } else {
         return null;
      }
   }

   public enum OverrideType {
      SKIN,
      CAPE,
      ELYTRA,
      RESOURCE(String.class, "String"),
      PRIMARY,
      SECONDARY,
      SPECULAR,
      NORMAL,
      CUSTOM(FiguraTexture.class, "Texture");

      @Nullable
      public final Type argumentType;
      @Nullable
      public final String typeName;

      OverrideType() {
         this.argumentType = null;
         this.typeName = null;
      }

      OverrideType(Type argumentType, String typeName) {
         this.argumentType = argumentType;
         this.typeName = typeName;
      }
   }
}
