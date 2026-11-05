package org.zenith.base.figura.lua;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

public final class LuaTypeManager {
   private final Map<Class<?>, LuaTable> metatables = new HashMap<>();

   public void generateMetatableFor(Class<?> type) {
      if (type != null && !this.metatables.containsKey(type)) {
         if (!type.isAnnotationPresent(LuaWhitelist.class)) {
            throw new IllegalArgumentException("Un-whitelisted Lua type " + type.getName());
         }

         Class<?> parent = type.getSuperclass();
         if (parent != null && parent.isAnnotationPresent(LuaWhitelist.class)) {
            this.generateMetatableFor(parent);
         }

         LuaTable metatable = new LuaTable();
         final LuaTable indexTable = new LuaTable();

         for (Class<?> current = type; current != null && current.isAnnotationPresent(LuaWhitelist.class); current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
               if (method.isAnnotationPresent(LuaWhitelist.class)) {
                  method.trySetAccessible();
                  String name = method.getName();
                  if (name.startsWith("__")) {
                     if (metatable.rawget(name).isnil()) {
                        if ("__index".equals(name)) {
                           final LuaFunction fallback = this.getWrapper(method);
                           metatable.set(name, new TwoArgFunction() {
                              public LuaValue call(LuaValue self, LuaValue key) {
                                 LuaValue direct = indexTable.get(key);
                                 return direct.isnil() ? fallback.call(self, key) : direct;
                              }
                           });
                        } else {
                           metatable.set(name, this.getWrapper(method));
                        }
                     }
                  } else if (indexTable.rawget(name).isnil()) {
                     indexTable.set(name, this.getWrapper(method));
                  }
               }
            }
         }

         if (metatable.rawget("__index").isnil()) {
            metatable.set("__index", indexTable);
         }

         if (metatable.rawget("__tostring").isnil()) {
            metatable.set("__tostring", new OneArgFunction() {
               public LuaValue call(LuaValue value) {
                  return LuaValue.valueOf(value.checkuserdata().toString());
               }
            });
         }

         LuaTable parentMetatable = this.metatables.get(parent);
         if (indexTable.rawget("__index").isnil() && parentMetatable != null) {
            LuaTable inherited = new LuaTable();
            inherited.set("__index", parentMetatable.get("__index"));
            indexTable.setmetatable(inherited);
         }

         this.metatables.put(type, metatable);
      }
   }

   public void dumpMetatables(LuaTable target) {
      for (Entry<Class<?>, LuaTable> entry : this.metatables.entrySet()) {
         LuaTypeDoc typeDoc = entry.getKey().getAnnotation(LuaTypeDoc.class);
         if (typeDoc != null) {
            String name = typeDoc.name();
            if (!target.rawget(name).isnil()) {
               throw new IllegalStateException("Two Lua types use the name " + name);
            }

            target.set(name, (LuaValue)entry.getValue());
         }
      }
   }

   public String getTypeName(Class<?> type) {
      LuaTypeDoc typeDoc = type.getAnnotation(LuaTypeDoc.class);
      return typeDoc == null ? type.getSimpleName() : typeDoc.name();
   }

   public VarArgFunction getWrapper(Method method) {
      final boolean isStatic = Modifier.isStatic(method.getModifiers());
      final Class<?> ownerType = method.getDeclaringClass();
      final Class<?>[] argumentTypes = method.getParameterTypes();
      final boolean[] required = requiredNotNil(method);
      return new VarArgFunction() {
         public Varargs invoke(Varargs args) {
            Object caller = isStatic ? null : args.checkuserdata(1, ownerType);
            Object[] actual = new Object[argumentTypes.length];
            int offset = isStatic ? 1 : 2;
            if (isStatic
               && argumentTypes.length > 0
               && !argumentTypes[0].isAssignableFrom(ownerType)
               && args.narg() > 0
               && args.arg1().isuserdata()
               && ownerType.isInstance(args.arg1().checkuserdata())) {
               offset++;
            }

            for (int i = 0; i < argumentTypes.length; i++) {
               int argumentIndex = i + offset;
               LuaValue luaArgument = args.arg(argumentIndex);
               if (luaArgument.isnil() && required[i]) {
                  throw new LuaError("Argument " + (i + 1) + " to " + method.getName() + " cannot be nil");
               }

               actual[i] = luaArgument.isnil()
                  ? LuaTypeManager.primitiveDefault(argumentTypes[i])
                  : LuaTypeManager.this.luaToJava(luaArgument, args, argumentIndex, argumentTypes[i]);
            }

            try {
               Object result = method.invoke(caller, actual);
               return result instanceof Varargs varargs ? varargs : LuaTypeManager.this.javaToLua(result);
            } catch (InvocationTargetException e) {
               Throwable cause = e.getCause();
               if (cause instanceof LuaError luaError) {
                  throw luaError;
               } else {
                  throw new LuaError(cause == null ? e : cause);
               }
            } catch (ReflectiveOperationException e) {
               throw new LuaError(e);
            }
         }

         public String tojstring() {
            return "function: " + method.getName();
         }
      };
   }

   public Object luaToJava(LuaValue value) {
      if (value.istable()) {
         return value.checktable();
      }

      if (!value.isnumber()) {
         if (value.isstring()) {
            return value.checkjstring();
         } else if (value.isboolean()) {
            return value.checkboolean();
         } else if (value.isfunction()) {
            return value.checkfunction();
         } else {
            return value.isuserdata() ? value.checkuserdata() : null;
         }
      } else {
         return !(value instanceof LuaInteger) && (!value.isint() || !(value instanceof LuaString)) ? value.checkdouble() : value.checkint();
      }
   }

   public Varargs javaToLua(Object value) {
      if (value == null) {
         return LuaValue.NIL;
      } else if (value instanceof LuaValue luaValue) {
         return luaValue;
      } else if (value instanceof Double number) {
         return LuaValue.valueOf(number);
      } else if (value instanceof Float number) {
         return LuaValue.valueOf(number.doubleValue());
      } else if (value instanceof Integer number) {
         return LuaValue.valueOf(number);
      } else if (value instanceof Long number) {
         return LuaValue.valueOf(number.doubleValue());
      } else if (value instanceof Short number) {
         return LuaValue.valueOf(number.intValue());
      } else if (value instanceof Byte number) {
         return LuaValue.valueOf(number.intValue());
      } else if (value instanceof Boolean bool) {
         return LuaValue.valueOf(bool);
      } else if (value instanceof Character character) {
         return LuaValue.valueOf(character);
      } else if (value instanceof String string) {
         return LuaValue.valueOf(string);
      } else if (value instanceof Component text) {
         return LuaValue.valueOf(
            ((JsonElement)ComponentSerialization.CODEC.encodeStart(RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), text).getOrThrow())
               .toString()
         );
      } else if (value instanceof Map<?, ?> map) {
         return this.wrapMap(map);
      } else if (value instanceof Collection<?> collection) {
         return this.wrapCollection(collection);
      } else {
         return (Varargs)(value.getClass().isArray() ? this.wrapArray(value) : this.wrap(value));
      }
   }

   private LuaValue wrap(Object instance) {
      Class<?> registered = instance.getClass();

      while (registered != null && !registered.isAnnotationPresent(LuaWhitelist.class)) {
         registered = registered.getSuperclass();
      }

      if (registered == null) {
         throw new LuaError("Attempt to expose un-whitelisted Java type " + instance.getClass().getName());
      }

      this.generateMetatableFor(registered);
      LuaUserdata result = new LuaUserdata(instance);
      result.setmetatable((LuaValue)this.metatables.get(registered));
      return result;
   }

   private LuaValue wrapMap(Map<?, ?> map) {
      LuaTable table = new LuaTable();

      for (Entry<?, ?> entry : map.entrySet()) {
         table.set(this.javaToLua(entry.getKey()).arg1(), this.javaToLua(entry.getValue()).arg1());
      }

      return table;
   }

   private LuaValue wrapCollection(Collection<?> values) {
      LuaTable table = new LuaTable();
      int index = 1;

      for (Object value : values) {
         table.set(index++, this.javaToLua(value).arg1());
      }

      return table;
   }

   private Varargs wrapArray(Object array) {
      LuaValue[] values = new LuaValue[Array.getLength(array)];

      for (int i = 0; i < values.length; i++) {
         values[i] = this.javaToLua(Array.get(array, i)).arg1();
      }

      return LuaValue.varargsOf(values);
   }

   private Object luaToJava(LuaValue value, Varargs args, int index, Class<?> expected) {
      if (expected == Object.class) {
         return this.luaToJava(value);
      } else if (expected == String.class) {
         return value.checkjstring();
      } else if (expected == boolean.class || expected == Boolean.class) {
         return value.toboolean();
      } else if (expected == double.class || expected == Double.class || expected == Number.class) {
         return value.checkdouble();
      } else if (expected == float.class || expected == Float.class) {
         return (float)value.checkdouble();
      } else if (expected == int.class || expected == Integer.class) {
         return value.checkint();
      } else if (expected == long.class || expected == Long.class) {
         return value.checklong();
      } else if (expected == LuaValue.class) {
         return value;
      } else if (expected == LuaTable.class) {
         return value.checktable();
      } else if (expected == LuaFunction.class) {
         return value.checkfunction();
      } else {
         return expected.isArray()
            ? this.luaVarargsToArray(value.istable() ? value.checktable().unpack() : args, value.istable() ? 1 : index, expected.getComponentType())
            : value.checkuserdata(expected);
      }
   }

   private Object luaVarargsToArray(Varargs args, int start, Class<?> componentType) {
      int length = Math.max(args.narg() - start + 1, 0);
      Object result = Array.newInstance(componentType, length);

      for (int i = 0; i < length; i++) {
         LuaValue value = args.arg(start + i);
         Array.set(result, i, this.luaToJava(value, args, start + i, componentType));
      }

      return result;
   }

   private static boolean[] requiredNotNil(Method method) {
      Parameter[] parameters = method.getParameters();
      boolean[] required = new boolean[parameters.length];

      for (int i = 0; i < parameters.length; i++) {
         required[i] = parameters[i].isAnnotationPresent(LuaNotNil.class);
      }

      return required;
   }

   private static Object primitiveDefault(Class<?> type) {
      if (!type.isPrimitive()) {
         return null;
      } else if (type == boolean.class) {
         return false;
      } else if (type == char.class) {
         return '\u0000';
      } else if (type == byte.class) {
         return (byte)0;
      } else if (type == short.class) {
         return (short)0;
      } else if (type == int.class) {
         return 0;
      } else {
         return type == long.class ? 0L : type == float.class ? 0.0 : 0.0;
      }
   }
}
