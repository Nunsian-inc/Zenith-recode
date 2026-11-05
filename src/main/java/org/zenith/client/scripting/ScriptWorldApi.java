package org.zenith.client.scripting;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.client.modules.impl.misc.FreeCamModule;
import org.zenith.utility.game.Util4;

public final class ScriptWorldApi {
   private ScriptWorldApi() {
   }

   private static Minecraft method4816() {
      return Minecraft.getInstance();
   }

   public static void method4817(Script workspace, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "scoreboard",
         var1x -> {
            if (method4816().level == null) {
               return null;
            } else {
               Scoreboard var2x = method4816().level.getScoreboard();
               Objective var3x = var2x.getDisplayObjective(DisplaySlot.SIDEBAR);
               if (var3x == null) {
                  return null;
               } else {
                  ArrayList<PlayerScoreEntry> playerScoreEntries = new ArrayList<>(var2x.listPlayerScores(var3x));
                  playerScoreEntries.removeIf(PlayerScoreEntry::isHidden);
                  playerScoreEntries.sort(Comparator.comparingInt(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER));
                  ArrayList arrayList = new ArrayList();

                  for (PlayerScoreEntry sub : playerScoreEntries.subList(0, Math.min(15, playerScoreEntries.size()))) {
                     PlayerTeam playersTeam = var2x.getPlayersTeam(sub.owner());
                     Object display = sub.display() != null ? sub.display() : PlayerTeam.formatNameForTeam(playersTeam, Component.literal(sub.owner()));
                     arrayList.add(
                        ScriptValueUtils.method6247(scope)
                           .method6277("text", ((net.minecraft.network.chat.Component)display).getString())
                           .method6277("owner", sub.owner())
                           .method6277("score", (double)sub.value())
                           .getScriptableObject()
                     );
                  }

                  return ScriptValueUtils.method6247(scope)
                     .method6277("title", var3x.getDisplayName().getString())
                     .method6277("lines", ScriptValueUtils.method6267(scope, arrayList))
                     .getScriptableObject();
               }
            }
         }
      );
      entry2.method6274(
         "tablist",
         var1x -> {
            if (method4816().getConnection() == null) {
               return null;
            } else {
               PlayerTabOverlay var2x = method4816().gui.hud.getTabList();
               ArrayList var3x = new ArrayList();

               for (PlayerInfo playerInfo : var2x.getPlayerInfos()) {
                  Object tabListDisplayName = playerInfo.getTabListDisplayName() != null
                     ? playerInfo.getTabListDisplayName()
                     : PlayerTeam.formatNameForTeam(playerInfo.getTeam(), Component.literal(playerInfo.getProfile().name()));
                  var3x.add(
                     ScriptValueUtils.method6247(scope)
                        .method6277("name", playerInfo.getProfile().name())
                        .method6277("display", ((net.minecraft.network.chat.Component)tabListDisplayName).getString())
                        .method6277("ping", (double)playerInfo.getLatency())
                        .method6277("gamemode", playerInfo.getGameMode() == null ? "" : playerInfo.getGameMode().getSerializedName())
                        .getScriptableObject()
                  );
               }

               return ScriptValueUtils.method6247(scope)
                  .method6277("header", var2x.header == null ? "" : var2x.header.getString())
                  .method6277("footer", var2x.footer == null ? "" : var2x.footer.getString())
                  .method6277("players", ScriptValueUtils.method6267(scope, var3x))
                  .getScriptableObject();
            }
         }
      );
      entry2.method6274(
         "bossbars",
         var1x -> {
            ArrayList var2x = new ArrayList();

            for (Object obj : method4816().gui.hud.getBossOverlay().events.values()) {
               LerpingBossEvent event = (LerpingBossEvent)obj;
               var2x.add(
                  ScriptValueUtils.method6247(scope)
                     .method6277("name", event.getName().getString())
                     .method6277("percent", (double)event.getProgress())
                     .method6277("color", event.getColor().getName())
                     .getScriptableObject()
               );
            }

            return ScriptValueUtils.method6267(scope, var2x);
         }
      );
      entry2.method6274(
         "title",
         var1x -> ScriptValueUtils.method6247(scope)
            .method6277("title", method4816().gui.hud.title == null ? "" : method4816().gui.hud.title.getString())
            .method6277("subtitle", method4816().gui.hud.subtitle == null ? "" : method4816().gui.hud.subtitle.getString())
            .getScriptableObject()
      );
      entry2.method6274(
         "sign",
         var1x -> {
            if (method4816().level == null) {
               return null;
            } else if (!(
               method4816()
                  .level
                  .getBlockEntity(
                     BlockPos.containing(
                        ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
                     )
                  ) instanceof SignBlockEntity var3x
            )) {
               return null;
            } else {
               ArrayList arrayList = new ArrayList();
               ArrayList arrayList2 = new ArrayList();

               for (Component message : var3x.getText(net.minecraft.world.level.block.entity.SignTextSlot.FRONT).getMessages(false)) {
                  arrayList.add(message.getString());
               }

               for (Component message : var3x.getText(net.minecraft.world.level.block.entity.SignTextSlot.BACK).getMessages(false)) {
                  arrayList2.add(message.getString());
               }

               return ScriptValueUtils.method6247(scope)
                  .method6277("front", ScriptValueUtils.method6267(scope, arrayList))
                  .method6277("back", ScriptValueUtils.method6267(scope, arrayList2))
                  .getScriptableObject();
            }
         }
      );
      entry2.method6274(
         "biome",
         var0x -> method4816().level == null
            ? ""
            : method4816()
               .level
               .getBiome(
                  BlockPos.containing(
                     ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
                  )
               )
               .getRegisteredName()
      );
      entry2.method6274(
         "light",
         var0x -> {
            if (method4816().level == null) {
               return 0.0;
            } else {
               BlockPos var1x = BlockPos.containing(
                  ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
               );
               String var2x = ScriptValueUtils.method6254(var0x, 3, "max").toLowerCase(Locale.ROOT);
               int var3x = method4816().level.getBrightness(LightLayer.BLOCK, var1x);
               int brightness = method4816().level.getBrightness(LightLayer.SKY, var1x);
               return (double)(var2x.equals("block") ? var3x : (var2x.equals("sky") ? brightness : Math.max(var3x, brightness)));
            }
         }
      );
      entry2.method6274("glow", var1x -> {
         Entity var2x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2x == null) {
            return null;
         } else {
            Object var3x = ScriptValueUtils.arg(var1x, 1);
            boolean flag = var3x instanceof Boolean flag2 && !flag2 || var3x == null;
            Util4.method1282(workspace, var2x.getId(), flag && !ScriptValueUtils.method6253(var3x) ? null : ScriptValueUtils.method6258(var1x, 1, -1));
            return null;
         }
      });
      entry2.method6274("hide", var1x -> {
         Entity var2x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2x != null) {
            Util4.method1283(workspace, var2x.getId(), !ScriptValueUtils.method6252(var1x, 1) || ScriptValueUtils.method6259(var1x, 1, true));
         }

         return null;
      });
   }

   public static Scriptable method4818(Scriptable scope, ItemStack mainHandItem) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      WrittenBookContent writtenBookContent = mainHandItem.get(DataComponents.WRITTEN_BOOK_CONTENT);
      WritableBookContent writableBookContent = mainHandItem.get(DataComponents.WRITABLE_BOOK_CONTENT);
      ArrayList arrayList = new ArrayList();
      if (writtenBookContent != null) {
         for (Filterable page : writtenBookContent.pages()) {
            if (arrayList.size() >= 100) {
               break;
            }

            arrayList.add(((Component)page.raw()).getString());
         }

         entry2.method6277("title", writtenBookContent.title().raw()).method6277("author", writtenBookContent.author());
      } else if (writableBookContent != null) {
         for (Filterable page : writableBookContent.pages()) {
            if (arrayList.size() >= 100) {
               break;
            }

            arrayList.add(page.raw());
         }
      }

      entry2.method6277("pages", ScriptValueUtils.method6267(scope, arrayList));
      String string = mainHandItem.getComponents().toString();
      entry2.method6277("components", string.length() > 8000 ? string.substring(0, 8000) : string);
      return entry2.getScriptableObject();
   }

   private static Direction method4819(Object[] objects, int n) {
      String lowerCase = ScriptValueUtils.method6254(objects, n, "up").toLowerCase(Locale.ROOT);

      for (Direction direction : Direction.values()) {
         if (direction.getSerializedName().equals(lowerCase)) {
            return direction;
         }
      }

      return Direction.UP;
   }

   public static void method4820(Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("place", var0x -> method4824(var0x, true));
      entry2.method6274("useOn", var0x -> method4824(var0x, false));
      entry2.method6274(
         "breakBlock",
         var0x -> {
            if (method4816().player != null && method4816().gameMode != null) {
               BlockPos var1x = BlockPos.containing(
                  ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
               );
               Direction var2x = method4819(var0x, 3);
               boolean continueDestroyBlock = method4816().gameMode.isDestroying()
                  ? method4816().gameMode.continueDestroyBlock(var1x, var2x)
                  : method4816().gameMode.startDestroyBlock(var1x, var2x);
               org.zenith.utility.game.SwingCompat.swing(method4816().player, InteractionHand.MAIN_HAND);
               return continueDestroyBlock;
            } else {
               return false;
            }
         }
      );
      entry2.method6274("stopBreaking", var0x -> {
         if (method4816().gameMode != null) {
            method4816().gameMode.stopDestroyBlock();
         }

         return null;
      });
      entry2.method6274("selectSlot", var0x -> {
         if (method4816().player == null) {
            return null;
         } else {
            method4816().player.getInventory().setSelectedSlot(Math.max(0, Math.min(8, ScriptValueUtils.method6257(var0x, 0, 0))));
            return null;
         }
      });
      entry2.method6274("item", var1x -> method4816().player == null ? null : method4818(scope, method4816().player.getMainHandItem()));
   }

   private static boolean method4821(BlockPos relative) {
      BlockState blockState = method4816().level.getBlockState(relative);
      return !blockState.isAir() && !blockState.canBeReplaced() && blockState.getFluidState().isEmpty();
   }

   public static boolean method4822(BlockPos pos) {
      return Boolean.TRUE.equals(method4824(new Object[]{(double)pos.getX(), (double)pos.getY(), (double)pos.getZ()}, true));
   }

   public static Object method4823(double d, double d2, double d3) {
      return method4824(new Object[]{d, d2, d3}, false);
   }

   private static Object method4824(Object[] objects, boolean flag) {
      if (method4816().player != null && method4816().gameMode != null && method4816().level != null) {
         BlockPos containing = BlockPos.containing(
            ScriptValueUtils.method6256(objects, 0, 0.0), ScriptValueUtils.method6256(objects, 1, 0.0), ScriptValueUtils.method6256(objects, 2, 0.0)
         );
         InteractionHand hand = ScriptValueUtils.method6254(objects, 4, "main").equals("off") ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         BlockPos relative;
         Direction opposite;
         if (flag) {
            if (!method4816().level.getBlockState(containing).canBeReplaced()) {
               return false;
            }

            relative = null;
            opposite = null;
            ArrayList<Direction> directions = new ArrayList<>(List.of(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP));
            if (ScriptValueUtils.method6252(objects, 3) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(objects, 3))) {
               directions.add(0, method4819(objects, 3).getOpposite());
            }

            for (Direction direction : directions) {
               BlockPos pos = containing.relative(direction);
               if (method4821(pos)) {
                  relative = pos;
                  opposite = direction.getOpposite();
                  break;
               }
            }

            if (relative == null) {
               return false;
            }
         } else {
            relative = containing;
            opposite = method4819(objects, 3);
         }

         Vec3 vec = Vec3.atCenterOf(relative).add(opposite.getStepX() * 0.5, opposite.getStepY() * 0.5, opposite.getStepZ() * 0.5);
         if (method4816().player.getEyePosition().distanceToSqr(vec) > 30.0) {
            return false;
         } else {
            InteractionResult useItemOn = method4816().gameMode.useItemOn(method4816().player, hand, new BlockHitResult(vec, opposite, relative, false));
            if (useItemOn.consumesAction()) {
               org.zenith.utility.game.SwingCompat.swing(method4816().player, hand);
            }

            return useItemOn.consumesAction();
         }
      } else {
         return false;
      }
   }

   public static Scriptable method4825(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      workspace.method1413(() -> {
         try {
            if (active()) {
               BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            }
         } catch (Throwable var1x) {
         }
      });
      entry2.method6274("available", var0x -> method4826());
      entry2.method6274(
         "goto",
         var1x -> {
            method4827();
            ScriptNotifyApi.method2192(
               workspace,
               ScriptValueUtils.method6256(var1x, 0, 0.0),
               ScriptValueUtils.method6252(var1x, 2) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var1x, 2))
                  ? ScriptValueUtils.method6256(var1x, 1, 0.0)
                  : Double.NaN,
               ScriptValueUtils.method6252(var1x, 2) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var1x, 2))
                  ? ScriptValueUtils.method6256(var1x, 2, 0.0)
                  : ScriptValueUtils.method6256(var1x, 1, 0.0)
            );
            BaritoneAPI.getProvider()
               .getPrimaryBaritone()
               .getCustomGoalProcess()
               .setGoalAndPath(
                  (Goal)(ScriptValueUtils.method6252(var1x, 2) && !ScriptValueUtils.method6253(ScriptValueUtils.arg(var1x, 2))
                     ? new GoalBlock(
                        ScriptValueUtils.method6257(var1x, 0, 0), ScriptValueUtils.method6257(var1x, 1, 0), ScriptValueUtils.method6257(var1x, 2, 0)
                     )
                     : new GoalXZ(ScriptValueUtils.method6257(var1x, 0, 0), ScriptValueUtils.method6257(var1x, 1, 0)))
               );
            return null;
         }
      );
      entry2.method6274(
         "gotoXZ",
         var0x -> {
            method4827();
            BaritoneAPI.getProvider()
               .getPrimaryBaritone()
               .getCustomGoalProcess()
               .setGoalAndPath(new GoalXZ(ScriptValueUtils.method6257(var0x, 0, 0), ScriptValueUtils.method6257(var0x, 1, 0)));
            return null;
         }
      );
      entry2.method6274(
         "near",
         var1x -> {
            method4827();
            ScriptNotifyApi.method2192(
               workspace, ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            );
            BaritoneAPI.getProvider()
               .getPrimaryBaritone()
               .getCustomGoalProcess()
               .setGoalAndPath(
                  new GoalNear(
                     new BlockPos(ScriptValueUtils.method6257(var1x, 0, 0), ScriptValueUtils.method6257(var1x, 1, 0), ScriptValueUtils.method6257(var1x, 2, 0)),
                     ScriptValueUtils.method6257(var1x, 3, 2)
                  )
               );
            return null;
         }
      );
      entry2.method6274("stop", var0x -> {
         if (method4826()) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
         }

         return null;
      });
      entry2.method6274("active", var0x -> active());
      entry2.method6274(
         "mine",
         var0x -> {
            method4827();
            ArrayList var1x = new ArrayList();

            for (Object id : ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0))) {
               Identifier tryParse = Identifier.tryParse(
                  ScriptValueUtils.method6255(id).contains(":") ? ScriptValueUtils.method6255(id) : "minecraft:" + ScriptValueUtils.method6255(id)
               );
               if (tryParse != null && BuiltInRegistries.BLOCK.containsKey(tryParse)) {
                  var1x.add(BuiltInRegistries.BLOCK.getValue(tryParse));
               }
            }

            if (var1x.isEmpty()) {
               throw ScriptValueUtils.method6251("pathfind.mine([\"diamond_ore\"])");
            } else {
               BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().mine((Block[])var1x.toArray(new Block[0]));
               return null;
            }
         }
      );
      return entry2.getScriptableObject();
   }

   private static boolean method4826() {
      try {
         return BaritoneAPI.getProvider() != null && BaritoneAPI.getProvider().getPrimaryBaritone() != null;
      } catch (Throwable e) {
         return false;
      }
   }

   private static boolean active() {
      return method4826() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   private static void method4827() {
      if (!method4826()) {
         throw ScriptValueUtils.method6251("Baritone недоступен");
      }
   }

   public static Scriptable method4828(Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      workspace.method1413(() -> Util4.method1291(workspace));
      entry2.method6274("perspective", var0x -> {
         if (!ScriptValueUtils.method6252(var0x, 0)) {
            return switch (method4816().options.getCameraType()) {
               case FIRST_PERSON -> "first";
               case THIRD_PERSON_BACK -> "back";
               case THIRD_PERSON_FRONT -> "front";
            };
         } else {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "first").toLowerCase(Locale.ROOT);
            Options options = method4816().options;

            options.setCameraType(switch (var1x) {
               case "back", "third", "1" -> CameraType.THIRD_PERSON_BACK;
               case "front", "2" -> CameraType.THIRD_PERSON_FRONT;
               default -> CameraType.FIRST_PERSON;
            });
            return null;
         }
      });
      entry2.method6274("zoom", var1x -> {
         Util4.method1284(workspace, (float)Math.max(1.0, ScriptValueUtils.method6256(var1x, 0, 1.0)));
         return null;
      });
      entry2.method6274("freecam", var0x -> {
         FreeCamModule var1x = FreeCamModule.INSTANCE;
         if (!ScriptValueUtils.method6252(var0x, 0)) {
            return var1x.isEnabled();
         } else {
            boolean var2x = ScriptValueUtils.method6259(var0x, 0, true);
            if (var1x.isEnabled() != var2x) {
               var1x.toggle();
            }

            return null;
         }
      });
      entry2.method6274("position", var1x -> {
         Vec3 var2x = method4816().gameRenderer.mainCamera().position();
         return ScriptValueUtils.method6270(scope, var2x.x, var2x.y, var2x.z);
      });
      entry2.method6274("yaw", var0x -> (double)method4816().gameRenderer.mainCamera().yRot());
      entry2.method6274("pitch", var0x -> (double)method4816().gameRenderer.mainCamera().xRot());
      return entry2.getScriptableObject();
   }
}
