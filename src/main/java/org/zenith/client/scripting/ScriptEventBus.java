package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.events.Cancellable;
import com.darkmagician6.eventapi.events.Event;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.KeyEvent;
import org.zenith.PacketEvent;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.BlockBreakingEvent;
import org.zenith.client.events.ChatReceiveEvent;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.ClickSlotEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.RespawnEvent;
import org.zenith.client.events.FovEvent;
import org.zenith.client.events.JumpEvent;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.PacketReceiveEvent;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;

public final class ScriptEventBus {
   public static final Map<String, ScriptEventBus.Data> items = new LinkedHashMap<>();
   public static final Map<String, Script.Type> items2 = new LinkedHashMap<>();
   public static final Map<String, String> items3 = new LinkedHashMap<>();

   private static void method1864(String text, Class<? extends Event> classValue, boolean flag, String str, BiFunction<Scriptable, Event, Scriptable> biFunction) {
      items.put(text, new ScriptEventBus.Data(text, classValue, flag, true, biFunction, str, List.of()));
   }

   private static void method1865(String str, Script.Type type, String str2) {
      items2.put(str, type);
      items3.put(str, str2);
   }

   private static ScriptValueUtils.Entry2 method1866(Scriptable scope, Event event) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      if (event instanceof Cancellable cancellable) {
         entry2.method6274("cancel", var1x -> {
            cancellable.setCancelled(!ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true));
            return null;
         });
         entry2.method6274("cancelled", var1x -> cancellable.isCancelled());
      }

      return entry2;
   }

   private ScriptEventBus() {
   }

   public static List<String> method1867() {
      ArrayList<String> strings = new ArrayList<>();

      for (ScriptEventBus.Data data : items.values()) {
         strings.add("`" + data.name() + "`" + (data.isFlag() ? " *(отменяемое)*" : "") + " — " + data.getText2());
      }

      for (Entry entry : items3.entrySet()) {
         strings.add("`" + (String)entry.getKey() + "` — " + (String)entry.getValue());
      }

      return strings;
   }

   public static byte method1868(Object id) {
      if (ScriptValueUtils.method6253(id)) {
         return 2;
      } else {
         String lowerCase = ScriptValueUtils.method6255(id).toLowerCase(Locale.ROOT);

         return switch (lowerCase) {
            case "highest" -> 0;
            case "high" -> 1;
            case "normal", "medium" -> 2;
            case "low" -> 3;
            case "lowest" -> 4;
            default -> throw ScriptValueUtils.method6251("неизвестный приоритет: " + ScriptValueUtils.method6255(id));
         };
      }
   }

   public static Scriptable method1869(Storage storage, Script workspace, Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      int[] ints = new int[]{0};
      LinkedHashMap map = new LinkedHashMap();
      BiFunction<String, Object[], Object> biFunction = (var4x, var5x) -> {
         var var6x = ScriptValueUtils.method6260(var5x, 1);
         Script.Type key = items2.get(var4x);
         if (key != null) {
            ScriptScreenApi.method7046(workspace, key, var6x);
            return -1.0;
         } else {
            ScriptEventBus.Data var8x = items.get(var4x);
            if (var8x == null) {
               throw ScriptValueUtils.method6251("неизвестное событие: " + var4x + " (список — events.names())");
            } else {
               byte b = method1868(ScriptValueUtils.arg(var5x, 2));
               ArrayList var10x = new ArrayList();
               var10x.add(EventManager.main().subscribe(var8x.getClassValue(), b, (var4xx, var5xx) -> method1870(storage, workspace, var8x, var6x, (Event)var5xx)));

               for (Class clazz : var8x.getItems()) {
                  var10x.add(EventManager.main().subscribe(clazz, b, (var4xx, var5xx) -> method1870(storage, workspace, var8x, var6x, (Event)var5xx)));
               }

               int n = ++ints[0];
               AutoCloseable autoCloseable = () -> var10x.forEach(r -> ((Runnable)r).run());
               map.put(n, autoCloseable);
               workspace.method1413(autoCloseable);
               return (double)n;
            }
         }
      };
      entry2.method6274("on", var1x -> biFunction.apply(ScriptValueUtils.method6254(var1x, 0, ""), var1x));
      entry2.method6274("once", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");
         var var5x = ScriptValueUtils.method6260(var3x, 1);
         int[] var6x = new int[]{0};
         boolean[] flags = new boolean[]{false};
         BaseFunction var8x = ScriptValueUtils.method6249(scope, "once", var5xx -> {
            if (flags[0]) {
               return null;
            } else {
               flags[0] = true;
               AutoCloseable var6xx = (AutoCloseable)map.remove(var6x[0]);
               if (var6xx != null) {
                  try {
                     var6xx.close();
                  } catch (Exception var8xx) {
                  }
               }

               return var5x.call(Context.getCurrentContext(), scope, scope, var5xx);
            }
         });
         Object obj = biFunction.apply(var4x, new Object[]{var4x, var8x, ScriptValueUtils.arg(var3x, 2)});
         var6x[0] = (int)Context.toNumber(obj);
         return obj;
      });
      entry2.method6274("off", var1x -> {
         AutoCloseable var2x = (AutoCloseable)map.remove(ScriptValueUtils.method6257(var1x, 0, -1));
         if (var2x == null) {
            return false;
         } else {
            try {
               var2x.close();
            } catch (Exception var4x) {
            }

            return true;
         }
      });
      entry2.method6274("names", var1x -> {
         var var2x = new ArrayList<>(items.keySet());
         var2x.addAll(items2.keySet());
         return ScriptValueUtils.method6267(scope, var2x);
      });

      for (String str : items.keySet()) {
         entry2.method6274(str, var2x -> biFunction.apply(str, new Object[]{str, ScriptValueUtils.arg(var2x, 0), ScriptValueUtils.arg(var2x, 1)}));
      }

      for (String str : items2.keySet()) {
         entry2.method6274(str, var2x -> biFunction.apply(str, new Object[]{str, ScriptValueUtils.arg(var2x, 0)}));
      }

      return entry2.getScriptableObject();
   }

   private static void method1870(Storage storage, Script workspace, ScriptEventBus.Data data, Function function, Event event) {
      if (workspace.isRunning() && ScriptEntityApi.method6575().isSameThread()) {
         if (!(event instanceof PacketEvent event2 && !event2.isOutgoing())) {
            if (!(event instanceof Cancellable cancellable && cancellable.isCancelled() && data.isFlag())) {
               Scriptable scriptable = ScriptScreenApi.method7044(workspace);
               if (scriptable != null) {
                  if (event instanceof DrawEvent event3) {
                     ScriptDrawApi.method2597(event3.getCustomDrawContext());
                     ScriptEntityApi.method6573(event3.getTickDelta());
                  }

                  if (event instanceof WorldRenderEvent event4) {
                     ScriptDrawApi.method2598(event4.getMatrixStack());
                     ScriptEntityApi.method6573(event4.getFloatValue());
                  }

                  try {
                     Context context = ScriptContextFactory.method7409();

                     Scriptable scriptableObject;
                     try {
                        scriptableObject = data.getBiFunction().apply(scriptable, event);
                     } catch (RuntimeException e) {
                        return;
                     } finally {
                        Context.exit();
                     }

                     Object id = storage.method1321(workspace, function, scriptableObject);
                     if (data.isFlag() && event instanceof Cancellable cancellable2 && id != null && !ScriptValueUtils.method6253(id) && Context.toBoolean(id)) {
                        cancellable2.setCancelled(true);
                     }
                  } finally {
                     if (event instanceof DrawEvent) {
                        ScriptDrawApi.end();
                     }

                     if (event instanceof WorldRenderEvent) {
                        ScriptDrawApi.method2599();
                     }
                  }
               }
            }
         }
      }
   }

   static {
      method1864("tick", TickEvent.class, false, "каждый тик клиента (20/с), и в меню", (scope, event) -> method1866(scope, event).getScriptableObject());
      method1864("update", UpdateEvent.class, false, "тик своего игрока, до физики", (scope, event) -> method1866(scope, event).getScriptableObject());
      method1864(
         "render2d",
         DrawEvent.class,
         false,
         "каждый кадр поверх HUD: рисуй render.* ; e.partial",
         (scope, event) -> method1866(scope, event).method6277("partial", (double)((DrawEvent)event).getTickDelta()).getScriptableObject()
      );
      method1864(
         "render3d",
         WorldRenderEvent.class,
         false,
         "каждый кадр мира: рисуй render3d.* ; e.partial",
         (scope, event) -> method1866(scope, event).method6277("partial", (double)((WorldRenderEvent)event).getFloatValue()).getScriptableObject()
      );
      method1864(
         "key",
         KeyEvent.class,
         false,
         "клавиша: key, action, mods, press, release, repeat, screenOpen",
         (scope, event) -> {
            KeyEvent event2 = (KeyEvent)event;
            return method1866(scope, event)
               .method6277("key", (double)event2.getKeyCode())
               .method6277("action", (double)event2.getIntValue())
               .method6277("mods", (double)event2.getModifiers())
               .method6277("press", event2.getIntValue() == 1)
               .method6277("release", event2.getIntValue() == 0)
               .method6277("repeat", event2.getIntValue() == 2)
               .method6277("screenOpen", ScriptEntityApi.method6575().gui.screen() != null)
               .getScriptableObject();
         }
      );
      method1864(
         "mouse",
         MouseButtonEvent.class,
         true,
         "кнопка мыши: button, press, release, screenOpen",
         (scope, event) -> {
            MouseButtonEvent event2 = (MouseButtonEvent)event;
            return method1866(scope, event)
               .method6277("button", (double)event2.getIntValue())
               .method6277("press", event2.getIntValue2() == 1)
               .method6277("release", event2.getIntValue2() == 0)
               .method6277("screenOpen", ScriptEntityApi.method6575().gui.screen() != null)
               .getScriptableObject();
         }
      );
      method1864("scroll", MouseScrollEvent.class, true, "прокрутка колеса в игре: vertical, horizontal", (scope, event) -> {
         MouseScrollEvent event2 = (MouseScrollEvent)event;
         return method1866(scope, event).method6277("vertical", event2.getDoubleValue2()).method6277("horizontal", event2.getDoubleValue()).getScriptableObject();
      });
      method1864("look", MouseRotationEvent.class, true, "поворот мышью за кадр: dx, dy; setDx(v), setDy(v)", (scope, event) -> {
         MouseRotationEvent event2 = (MouseRotationEvent)event;
         return method1866(scope, event).method6277("dx", event2.getDoubleValue()).method6277("dy", event2.getDoubleValue2()).method6274("setDx", var1x -> {
            event2.setDoubleValue(ScriptValueUtils.method6256(var1x, 0, event2.getDoubleValue()));
            return null;
         }).method6274("setDy", var1x -> {
            event2.setDoubleValue2(ScriptValueUtils.method6256(var1x, 0, event2.getDoubleValue2()));
            return null;
         }).getScriptableObject();
      });
      method1864(
         "attack",
         AttackEvent.class,
         true,
         "удар по сущности: target, stage ('pre'/'post')",
         (scope, event) -> {
            AttackEvent event2 = (AttackEvent)event;
            return method1866(scope, event)
               .method6277("target", ScriptEntityApi.method6576(scope, event2.getEntity()))
               .method6277("stage", event2.getType4().name().toLowerCase(Locale.ROOT))
               .getScriptableObject();
         }
      );
      method1864(
         "move",
         PlayerMoveEvent.class,
         true,
         "вектор движения тика: x, y, z; set(x, y, z)",
         (scope, event) -> {
            PlayerMoveEvent event2 = (PlayerMoveEvent)event;
            Vec3 vec3d = event2.getVec3d();
            return method1866(scope, event)
               .method6277("x", vec3d.x)
               .method6277("y", vec3d.y)
               .method6277("z", vec3d.z)
               .method6274(
                  "set",
                  var1x -> {
                     Vec3 var2x = event2.getVec3d();
                     event2.setVec3d(
                        new Vec3(
                           ScriptValueUtils.method6256(var1x, 0, var2x.x),
                           ScriptValueUtils.method6256(var1x, 1, var2x.y),
                           ScriptValueUtils.method6256(var1x, 2, var2x.z)
                        )
                     );
                     return null;
                  }
               )
               .method6274("setX", var1x -> {
                  Vec3 var2x = event2.getVec3d();
                  event2.setVec3d(new Vec3(ScriptValueUtils.method6256(var1x, 0, var2x.x), var2x.y, var2x.z));
                  return null;
               })
               .method6274("setY", var1x -> {
                  Vec3 var2x = event2.getVec3d();
                  event2.setVec3d(new Vec3(var2x.x, ScriptValueUtils.method6256(var1x, 0, var2x.y), var2x.z));
                  return null;
               })
               .method6274("setZ", var1x -> {
                  Vec3 var2x = event2.getVec3d();
                  event2.setVec3d(new Vec3(var2x.x, var2x.y, ScriptValueUtils.method6256(var1x, 0, var2x.z)));
                  return null;
               })
               .getScriptableObject();
         }
      );
      method1864(
         "move_input",
         MoveInputEvent.class,
         true,
         "ввод движения: forward, back, left, right, jump, sneak, sprint; setForward(b)... set(forward, strafe), none()",
         (scope, event) -> {
            MoveInputEvent event2 = (MoveInputEvent)event;
            Input playerInput = event2.getPlayerInput();
            return method1866(scope, event)
               .method6277("forward", playerInput.forward())
               .method6277("back", playerInput.backward())
               .method6277("left", playerInput.left())
               .method6277("right", playerInput.right())
               .method6277("jump", playerInput.jump())
               .method6277("sneak", playerInput.shift())
               .method6277("sprint", playerInput.sprint())
               .method6274("setForward", var1x -> {
                  event2.setForward(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setBack", var1x -> {
                  event2.setBackward(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setLeft", var1x -> {
                  event2.setLeft(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setRight", var1x -> {
                  event2.setRight(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setJump", var1x -> {
                  event2.setJump(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setSneak", var1x -> {
                  event2.setShift(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("setSprint", var1x -> {
                  event2.setSprint(ScriptValueUtils.method6259(var1x, 0, true));
                  return null;
               })
               .method6274("set", var1x -> {
                  event2.setMovement((float)ScriptValueUtils.method6256(var1x, 0, 0.0), (float)ScriptValueUtils.method6256(var1x, 1, 0.0));
                  return null;
               })
               .method6274("none", var1x -> {
                  event2.clearInput();
                  return null;
               })
               .getScriptableObject();
         }
      );
      method1864("jump", JumpEvent.class, false, "игрок прыгает", (scope, event) -> method1866(scope, event).getScriptableObject());
      method1864("sprint", SprintUpdateEvent.class, true, "можно ли бежать: canSprint; setCanSprint(b)", (scope, event) -> {
         SprintUpdateEvent event2 = (SprintUpdateEvent)event;
         return method1866(scope, event).method6277("canSprint", event2.isFlag2()).method6274("setCanSprint", var1x -> {
            event2.setFlag2(ScriptValueUtils.method6259(var1x, 0, true));
            return null;
         }).getScriptableObject();
      });
      method1864("fov", FovEvent.class, true, "поле зрения: fov; setFov(n)", (scope, event) -> {
         FovEvent event2 = (FovEvent)event;
         return method1866(scope, event).method6277("fov", (double)event2.getIntValue()).method6274("setFov", var1x -> {
            event2.setIntValue(Math.max(1, Math.min(179, ScriptValueUtils.method6257(var1x, 0, event2.getIntValue()))));
            return null;
         }).getScriptableObject();
      });
      method1864(
         "click_slot",
         ClickSlotEvent.class,
         true,
         "клик по слоту: windowId, slot, button, action",
         (scope, event) -> {
            ClickSlotEvent event2 = (ClickSlotEvent)event;
            return method1866(scope, event)
               .method6277("windowId", (double)event2.getIntValue())
               .method6277("slot", (double)event2.getIntValue2())
               .method6277("button", (double)event2.getIntValue3())
               .method6277("action", event2.getSlotActionType() == null ? "" : event2.getSlotActionType().name().toLowerCase(Locale.ROOT))
               .getScriptableObject();
         }
      );
      method1864(
         "break_block",
         BlockBreakingEvent.class,
         true,
         "ломание блока: x, y, z, side",
         (scope, event) -> {
            BlockBreakingEvent event2 = (BlockBreakingEvent)event;
            return method1866(scope, event)
               .method6277("x", (double)event2.getBlockPos().getX())
               .method6277("y", (double)event2.getBlockPos().getY())
               .method6277("z", (double)event2.getBlockPos().getZ())
               .method6277("side", event2.getDirection() == null ? "" : event2.getDirection().getSerializedName())
               .getScriptableObject();
         }
      );
      method1864("chat", ChatReceiveEvent.class, true, "входящее сообщение чата: text; setText(s) — подменить", (scope, event) -> {
         ChatReceiveEvent event2 = (ChatReceiveEvent)event;
         return method1866(scope, event).method6277("text", event2.getText() == null ? "" : event2.getText().getString()).method6274("setText", var1x -> {
            event2.method7858(Component.literal(ScriptValueUtils.method6254(var1x, 0, "")));
            return null;
         }).getScriptableObject();
      });
      method1864(
         "chat_send",
         ChatSendEvent.class,
         true,
         "своё сообщение перед отправкой: text, command",
         (scope, event) -> {
            ChatSendEvent event2 = (ChatSendEvent)event;
            return method1866(scope, event)
               .method6277("text", event2.getMessage() == null ? "" : event2.getMessage())
               .method6277("command", event2.method4602())
               .method6277("pre", event2.method4603())
               .getScriptableObject();
         }
      );
      items.put(
         "packet",
         new ScriptEventBus.Data(
            "packet",
            PacketEvent.class,
            true,
            true,
            (scope, event) -> {
               Packet packet3 = event instanceof PacketEvent event2 ? event2.getPacket() : ((PacketReceiveEvent)event).getPacket();
               boolean string = event instanceof PacketEvent event3 && event3.isOutgoing();
               return method1866(scope, event)
                  .method6277("name", ScriptEntityEvents.method7557(packet3))
                  .method6277("send", string)
                  .method6277("receive", !string)
                  .method6274("data", var2x -> ScriptEntityEvents.method7558(scope, packet3))
                  .getScriptableObject();
            },
            "пакет: name, send, receive, data(); отмена работает в обе стороны",
            List.of(PacketReceiveEvent.class)
         )
      );
      method1864("respawn", RespawnEvent.class, false, "возрождение или смена измерения", (scope, event) -> method1866(scope, event).getScriptableObject());
      method1864(
         "entity_spawn",
         EntityAddEvent.class,
         false,
         "сущность появилась: entity",
         (scope, event) -> method1866(scope, event)
            .method6277("entity", ScriptEntityApi.method6576(scope, ((EntityAddEvent)event).getEntity()))
            .getScriptableObject()
      );
      method1864(
         "entity_death",
         EntityDeathEvent.class,
         false,
         "сущность умерла: entity",
         (scope, event) -> method1866(scope, event)
            .method6277("entity", ScriptEntityApi.method6576(scope, ((EntityDeathEvent)event).getLivingEntity()))
            .getScriptableObject()
      );
      method1864("module_toggle", ModuleToggleEvent.class, false, "модуль переключён: name, enabled", (scope, event) -> {
         ModuleToggleEvent event2 = (ModuleToggleEvent)event;
         return method1866(scope, event).method6277("name", event2.getModule().getName()).method6277("enabled", event2.isEnabled()).getScriptableObject();
      });
      method1864("set_screen", SetScreenEvent.class, false, "открывается экран: screen (имя класса или '')", (scope, event) -> {
         SetScreenEvent event2 = (SetScreenEvent)event;
         return method1866(scope, event).method6277("screen", ScriptScreenApi.method7047(event2.getScreen())).getScriptableObject();
      });
      method1865("enable", Script.Type.ENABLE, "скрипт запущен");
      method1865("disable", Script.Type.DISABLE, "скрипт остановлен или перезагружается");
      method1865("world_change", Script.Type.WORLD_CHANGE, "сменился мир (вход, измерение)");
      method1865("health_change", Script.Type.HEALTH_CHANGE, "здоровье изменилось: e.from, e.to");
      method1865("death", Script.Type.DEATH, "свой игрок умер");
      method1865("title", Script.Type.TITLE, "титр на экране: e.kind (title/subtitle), e.text");
      method1865("actionbar", Script.Type.ACTIONBAR, "надпись над хотбаром: e.text");
      method1865("bossbar", Script.Type.BOSSBAR, "изменились боссбары: e.bars[{name, percent, color}]");
      method1865("sound_received", Script.Type.SOUND_RECEIVED, "сервер проиграл звук: e.id, e.category, e.x, e.y, e.z, e.volume, e.pitch");
      method1865("item_pickup", Script.Type.ITEM_PICKUP, "ты подобрал предмет: e — предмет, e.picked — сколько");
      method1865("block_update", Script.Type.BLOCK_UPDATE, "блок изменился: e.x, e.y, e.z, e.id, e.previous");
      method1865("container_open", Script.Type.CONTAINER_OPEN, "сервер открыл контейнер: e.type, e.title, e.syncId");
      method1865("server_join", Script.Type.SERVER_JOIN, "вход на сервер: e.address");
      method1865("server_leave", Script.Type.SERVER_LEAVE, "выход с сервера");
      method1865("screen", Script.Type.SCREEN, "сменился экран: e.screen, e.previous");
   }

   public static final class Data {
      private final String text;
      private final Class<? extends Event> classValue;
      private final boolean flag;
      private final boolean flag2;
      private final BiFunction<Scriptable, Event, Scriptable> biFunction;
      private final String text2;
      private final List<Class<? extends Event>> items;

      public String text() {
         return this.text;
      }

      public Class<? extends Event> classValue() {
         return this.classValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public BiFunction<Scriptable, Event, Scriptable> biFunction() {
         return this.biFunction;
      }

      public String text2() {
         return this.text2;
      }

      public List<Class<? extends Event>> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.classValue, other.classValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.biFunction, other.biFunction) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, classValue, flag, flag2, biFunction, text2, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "classValue=" + this.classValue + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "biFunction=" + this.biFunction + ", " + "text2=" + this.text2 + ", " + "items=" + this.items + "]";
      }

      public Data(
         String text,
         Class<? extends Event> classValue,
         boolean flag,
         boolean flag2,
         BiFunction<Scriptable, Event, Scriptable> biFunction,
         String str,
         List<Class<? extends Event>> items
      ) {
         this.text = text;
         this.classValue = classValue;
         this.flag = flag;
         this.flag2 = flag2;
         this.biFunction = biFunction;
         this.text2 = str;
         this.items = items;
      }

      public String name() {
         return this.text;
      }

      public Class<? extends Event> getClassValue() {
         return this.classValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public BiFunction<Scriptable, Event, Scriptable> getBiFunction() {
         return this.biFunction;
      }

      public String getText2() {
         return this.text2;
      }

      public List<Class<? extends Event>> getItems() {
         return this.items;
      }
   }
}
