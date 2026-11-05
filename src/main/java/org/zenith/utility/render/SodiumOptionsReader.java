package org.zenith.utility.render;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class SodiumOptionsReader {
   private static final String text = "net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI";
   private static final String text2 = "net.caffeinemc.mods.sodium.client.gui.options.Option";
   private static List<SodiumOptionsReader.Data> items;

   private SodiumOptionsReader() {
   }

   public static List<SodiumOptionsReader.Data> method1598() {
      if (items != null) {
         return items;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null && minecraft.getWindow() != null) {
            try {
               Class clazz = Class.forName("net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI");
               Constructor declaredConstructor = null;

               for (Constructor constructor : clazz.getDeclaredConstructors()) {
                  if (constructor.getParameterCount() == 1) {
                     declaredConstructor = constructor;
                  }
               }

               if (declaredConstructor == null) {
                  return List.of();
               }

               declaredConstructor.setAccessible(true);
               Object obj = declaredConstructor.newInstance((Screen)null);
               Field declaredField = clazz.getDeclaredField("pages");
               declaredField.setAccessible(true);
               ArrayList coll = new ArrayList();

               for (Object obj2 : (List)declaredField.get(obj)) {
                  String string = ((Component)method1607(obj2, "getName")).getString();
                  var arrayList = new ArrayList<>((Collection)method1607(obj2, "getOptions"));
                  if (!arrayList.isEmpty()) {
                     coll.add(new SodiumOptionsReader.Data(string, arrayList));
                  }
               }

               items = List.copyOf(coll);
            } catch (ClassNotFoundException e) {
               items = List.of();
            } catch (Throwable e) {
               System.err.println("[Sodium] could not read option pages: " + e);
               items = List.of();
            }

            return items == null ? List.of() : items;
         } else {
            return List.of();
         }
      }
   }

   public static AbstractWidget method1599(Object obj) {
      try {
         Object obj2 = method1607(obj, "getControl");
         if (obj2 == null) {
            return null;
         } else {
            String name = obj2.getClass().getName();
            AbstractWidget abstractWidget;
            if (name.endsWith("TickBoxControl")) {
               abstractWidget = method1600(obj);
            } else if (name.endsWith("CyclingControl")) {
               abstractWidget = method1601(obj, obj2);
            } else {
               if (!name.endsWith("SliderControl") && !name.endsWith("SliderControlExtended")) {
                  return null;
               }

               abstractWidget = method1603(obj, obj2);
            }

            abstractWidget.active = (Boolean)method1607(obj, "isAvailable");
            return abstractWidget;
         }
      } catch (Throwable e) {
         return null;
      }
   }

   private static AbstractWidget method1600(Object obj) {
      String str = method1605(obj);
      boolean flag = (Boolean)method1607(obj, "getValue");
      Button[] buttons = new Button[]{Button.builder(method1604(str, CommonComponents.optionStatus(flag)), var2x -> {
         boolean var3x = !(Boolean)method1607(obj, "getValue");
         method1606(obj, var3x);
         var2x.setMessage(method1604(str, CommonComponents.optionStatus(var3x)));
      }).build()};
      return buttons[0];
   }

   private static AbstractWidget method1601(Object obj, Object obj2) throws Exception {
      String str = method1605(obj);
      Object[] objects = (Object[])method1609(obj2, "allowedValues");
      Component[] components = (Component[])method1609(obj2, "names");
      return Button.builder(method1604(str, method1602(obj, objects, components)), var4x -> {
         Object obj3 = method1607(obj, "getValue");
         int n = 0;

         for (int i = 0; i < objects.length; i++) {
            if (objects[i] == obj3) {
               n = i;
            }
         }

         Object intValue = objects[(n + 1) % objects.length];
         method1606(obj, intValue);
         var4x.setMessage(method1604(str, method1602(obj, objects, components)));
      }).build();
   }

   private static Component method1602(Object obj, Object[] objects, Component[] components) {
      Object obj2 = method1607(obj, "getValue");

      for (int i = 0; i < objects.length; i++) {
         if (objects[i] == obj2) {
            if (components != null && i < components.length && components[i] != null) {
               return components[i];
            }
            break;
         }
      }

      return Component.literal(String.valueOf(obj2));
   }

   private static AbstractWidget method1603(final Object obj, Object obj2) throws Exception {
      final int n = (Integer)method1609(obj2, "min");
      int n2 = (Integer)method1609(obj2, "max");
      final int n3 = (Integer)method1609(obj2, "interval");
      final Object obj3 = method1609(obj2, "mode");
      final Method method = obj3.getClass().getMethod("format", int.class);
      method.setAccessible(true);
      final String str = method1605(obj);
      final int n4 = (Integer)method1607(obj, "getValue");
      final int n5 = Math.max(1, (n2 - n) / n3);
      return new AbstractSliderButton(0, 0, 150, 20, Component.empty(), (double)(n4 - n) / (n2 - n)) {
         private int intValue = n4;

         {
            this.updateMessage();
         }

         @Override
         public void updateMessage() {
            Object var1x;
            try {
               var1x = (Component)method.invoke(obj3, this.intValue);
            } catch (Throwable var3x) {
               var1x = Component.literal(String.valueOf(this.intValue));
            }

            this.setMessage(SodiumOptionsReader.method1604(str, (Component)var1x));
         }

         @Override
         public void applyValue() {
            this.intValue = n + (int)Math.round(this.value * n5) * n3;
         }

         @Override
         public void onRelease(MouseButtonEvent click) {
            SodiumOptionsReader.method1606(obj, this.intValue);
         }
      };
   }

   private static Component method1604(String str, Component optionStatus) {
      return Component.literal(str + ": ").append(optionStatus);
   }

   private static String method1605(Object obj) {
      return ((Component)method1607(obj, "getName")).getString();
   }

   private static void method1606(Object obj, Object intValue) {
      try {
         Class clazz = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.Option");
         Method method = clazz.getMethod("setValue", Object.class);
         method.invoke(obj, intValue);
         clazz.getMethod("applyChanges").invoke(obj);
         Object obj2 = method1607(obj, "getStorage");
         if (obj2 != null) {
            obj2.getClass().getMethod("save").invoke(obj2);
         }

         Minecraft minecraft = Minecraft.getInstance();

         for (Object obj3 : (Collection)method1607(obj, "getFlags")) {
            String str = String.valueOf(obj3);
            switch (str) {
               case "REQUIRES_RENDERER_RELOAD":
                  minecraft.levelExtractor.allChanged();
                  break;
               case "REQUIRES_RENDERER_UPDATE":
                  minecraft.levelExtractor.allChanged();
                  break;
               case "REQUIRES_ASSET_RELOAD":
                  minecraft.delayTextureReload();
                  break;
               case "REQUIRES_VIDEOMODE_RELOAD":
                  minecraft.getWindow().changeFullscreenVideoMode();
            }
         }
      } catch (Throwable e) {
      }
   }

   private static Object method1607(Object obj, String name) {
      try {
         Method method = method1608(obj.getClass(), name);
         method.setAccessible(true);
         return method.invoke(obj);
      } catch (Throwable e) {
         return null;
      }
   }

   private static Method method1608(Class<?> clazz, String name) throws NoSuchMethodException {
      for (Class clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
         try {
            return clazz2.getDeclaredMethod(name);
         } catch (NoSuchMethodException e) {
         }
      }

      return clazz.getMethod(name);
   }

   private static Object method1609(Object obj, String name) throws Exception {
      Field declaredField = obj.getClass().getDeclaredField(name);
      declaredField.setAccessible(true);
      return declaredField.get(obj);
   }

   public static final class Data {
      private final String text;
      private final List<Object> items;

      public String text() {
         return this.text;
      }

      public List<Object> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(String text, List<Object> items) {
         this.text = text;
         this.items = items;
      }

      public String name() {
         return this.text;
      }

      public List<Object> getItems() {
         return this.items;
      }
   }
}
