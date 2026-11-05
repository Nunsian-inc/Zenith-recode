package org.zenith.utility.misc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.phys.Vec3;

public final class ModuleHookDefinitions {
   private static final Map<String, Class12<?>> items = new LinkedHashMap<>();
   public static final Class12<ModuleHookDefinitions.Entry6> class12 = method6116(
      "entity_reach", ModuleHookDefinitions.Entry6.class, "дистанция удара по сущностям: e.value, e.vanilla; e.set(v)"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class122 = method6116(
      "block_reach", ModuleHookDefinitions.Entry6.class, "дистанция взаимодействия с блоками: e.value, e.vanilla; e.set(v)"
   );
   public static final Class12<ModuleHookDefinitions.Entry4> class123 = method6116(
      "firework_boost", ModuleHookDefinitions.Entry4.class, "разгон фейерверком на элитрах: e.velocity, e.player, e.look, e.life; e.set(x, y, z)"
   );
   public static final Class12<ModuleHookDefinitions.Entry5> class124 = method6116(
      "text", ModuleHookDefinitions.Entry5.class, "любой текст игры перед отрисовкой: e.text; e.setText(s), e.replace(a, b) — очень частый хук"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class125 = method6116(
      "fire_overlay", ModuleHookDefinitions.Entry.class, "оверлей огня на экране: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class126 = method6116(
      "wall_overlay", ModuleHookDefinitions.Entry.class, "оверлей блока в голове: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class127 = method6116(
      "darkness", ModuleHookDefinitions.Entry6.class, "затемнение от эффекта Darkness 0..1: e.value; e.set(v)"
   );
   public static final Class12<ModuleHookDefinitions.Entry3> class128 = method6116(
      "effect_fog", ModuleHookDefinitions.Entry3.class, "туман от эффекта: e.kind (blindness или darkness); e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class129 = method6116(
      "hurt_cam", ModuleHookDefinitions.Entry.class, "тряска камеры при уроне: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry> class1210 = method6116(
      "view_bob", ModuleHookDefinitions.Entry.class, "покачивание камеры при ходьбе: e.cancel()"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1211 = method6116(
      "brightness", ModuleHookDefinitions.Entry6.class, "яркость (gamma) 0..1+, e.value; e.set(v) — например 10 для полной яркости"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1212 = method6116(
      "world_time", ModuleHookDefinitions.Entry6.class, "время суток мира на клиенте в тиках (0..24000): e.value; e.set(t) — только визуально"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1213 = method6116(
      "rain", ModuleHookDefinitions.Entry6.class, "сила дождя на клиенте 0..1: e.value; e.set(v) — только визуально"
   );
   public static final Class12<ModuleHookDefinitions.Entry6> class1214 = method6116(
      "thunder", ModuleHookDefinitions.Entry6.class, "сила грозы на клиенте 0..1: e.value; e.set(v) — только визуально"
   );
   public static final Class12<ModuleHookDefinitions.Entry2> class1215 = method6116(
      "sound", ModuleHookDefinitions.Entry2.class, "игровой звук перед проигрыванием: e.id, e.category, e.x, e.y, e.z, e.volume, e.pitch; e.cancel()"
   );

   private static <C> Class12<C> method6116(String text, Class<C> classValue, String str) {
      Class12 class2 = new Class12(text, classValue, str);
      items.put(text, class2);
      return class2;
   }

   private ModuleHookDefinitions() {
   }

   public static Map<String, Class12<?>> method6117() {
      return Collections.unmodifiableMap(items);
   }

   public static double method6118(Class12<ModuleHookDefinitions.Entry6> class2, double returnValue) {
      return class2.active() ? class2.method1645(new ModuleHookDefinitions.Entry6(returnValue)).value : returnValue;
   }

   public static boolean method6119(Class12<ModuleHookDefinitions.Entry> class2) {
      return class2.active() && class2.method1645(new ModuleHookDefinitions.Entry()).cancelled;
   }

   public static final class Entry {
      public boolean cancelled;
   }

   public static final class Entry2 {
      public final String text;
      public final String text2;
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final float floatValue;
      public final float floatValue2;
      public boolean cancelled;

      public Entry2(String text, String name, double doubleValue, double y, double z, float floatValue, float f) {
         this.text = text;
         this.text2 = name;
         this.doubleValue = doubleValue;
         this.doubleValue2 = y;
         this.doubleValue3 = z;
         this.floatValue = floatValue;
         this.floatValue2 = f;
      }
   }

   public static final class Entry3 {
      public final String text;
      public boolean cancelled;

      public Entry3(String text) {
         this.text = text;
      }
   }

   public static final class Entry4 {
      public Vec3 vec3d;
      public final Vec3 vec3d2;
      public final Vec3 vec3d3;
      public final Vec3 vec3d4;
      public final int intValue;
      public final int intValue2;

      public Entry4(Vec3 vec3d, Vec3 vec3d3, Vec3 vec3d4, int intValue, int n) {
         this.vec3d = vec3d;
         this.vec3d2 = vec3d;
         this.vec3d3 = vec3d3;
         this.vec3d4 = vec3d4;
         this.intValue = intValue;
         this.intValue2 = n;
      }
   }

   public static final class Entry5 {
      public String text;

      public Entry5(String text) {
         this.text = text;
      }
   }

   public static final class Entry6 {
      public double value;
      public final double doubleValue;

      public Entry6(double doubleValue) {
         this.value = doubleValue;
         this.doubleValue = doubleValue;
      }
   }
}
