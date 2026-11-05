package org.zenith.client.scripting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.TopLevel.Builtins;

public final class ScriptValueUtils {
   public static final int CONST_intValue = 32768;
   public static final int CONST_intValue2 = 32;
   public static final int CONST_intValue3 = 500000;

   private ScriptValueUtils() {
   }

   public static ScriptValueUtils.Entry2 method6246(Scriptable scope, ScriptableObject scriptableObject) {
      return new ScriptValueUtils.Entry2(scope, scriptableObject);
   }

   public static ScriptValueUtils.Entry2 method6247(Scriptable scope) {
      NativeObject nativeObject = new NativeObject();
      ScriptRuntime.setBuiltinProtoAndParent(nativeObject, scope, Builtins.Object);
      return new ScriptValueUtils.Entry2(scope, nativeObject);
   }

   public static ScriptValueUtils.Entry2 method6248(Scriptable scope) {
      return method6247(scope);
   }

   public static BaseFunction method6249(Scriptable scope, String str, ScriptValueUtils.Handler handler) {
      Callable callable = (var2x, var3x, scriptable, objects) -> {
         try {
            Object obj = handler.call(objects);
            return obj == null ? Undefined.instance : obj;
         } catch (RhinoException | ScriptContextFactory.Entry e) {
            throw e;
         } catch (ScriptValueUtils.Entry e) {
            throw ScriptRuntime.constructError("Error", e.getMessage());
         } catch (RuntimeException e) {
            throw ScriptRuntime.constructError("Error", str + ": " + method6250(e));
         }
      };
      return new LambdaFunction(scope, str, 0, callable);
   }

   private static String method6250(RuntimeException runtimeException) {
      String message = runtimeException.getMessage();
      if (message != null && !message.isBlank()) {
         return message.length() > 200 ? message.substring(0, 200) : message;
      } else {
         return "внутренняя ошибка";
      }
   }

   public static ScriptValueUtils.Entry method6251(String message) {
      return new ScriptValueUtils.Entry(message);
   }

   public static Object arg(Object[] objects, int n) {
      return n < objects.length ? objects[n] : Undefined.instance;
   }

   public static boolean method6252(Object[] objects, int n) {
      return n < objects.length && !method6253(objects[n]);
   }

   public static boolean method6253(Object id) {
      return id == null || id == Undefined.instance;
   }

   public static String method6254(Object[] objects, int n, String title) {
      Object id = arg(objects, n);
      return method6253(id) ? title : method6255(id);
   }

   public static String method6255(Object id) {
      if (method6253(id)) {
         return "";
      } else if (id instanceof CharSequence charSequence) {
         if (charSequence.length() > 32768) {
            throw method6251("строка длиннее 32768 символов");
         } else {
            return charSequence.toString();
         }
      } else if (!(id instanceof Number number)) {
         if (id instanceof Boolean flag) {
            return flag.toString();
         } else {
            String string = Context.toString(id);
            if (string.length() > 32768) {
               throw method6251("строка длиннее 32768 символов");
            } else {
               return string;
            }
         }
      } else {
         double doubleValue = number.doubleValue();
         return doubleValue == Math.rint(doubleValue) && !Double.isInfinite(doubleValue) && Math.abs(doubleValue) < 1.0E15 ? Long.toString((long)doubleValue) : Double.toString(doubleValue);
      }
   }

   public static double method6256(Object[] objects, int n, double x) {
      Object id = arg(objects, n);
      if (method6253(id)) {
         return x;
      } else {
         double number = Context.toNumber(id);
         return Double.isNaN(number) ? x : number;
      }
   }

   public static int method6257(Object[] objects, int n, int width) {
      double d = method6256(objects, n, width);
      if (d > 2.147483647E9) {
         return Integer.MAX_VALUE;
      } else {
         return d < -2.1474836E9F ? Integer.MIN_VALUE : (int)d;
      }
   }

   public static int method6258(Object[] objects, int n, int n2) {
      Object id = arg(objects, n);
      return method6253(id) ? n2 : (int)((long)Context.toNumber(id));
   }

   public static boolean method6259(Object[] objects, int n, boolean flag) {
      Object id = arg(objects, n);
      return method6253(id) ? flag : Context.toBoolean(id);
   }

   public static Function method6260(Object[] objects, int n) {
      if (arg(objects, n) instanceof Function function) {
         return function;
      } else {
         throw method6251("ожидалась функция в аргументе " + (n + 1));
      }
   }

   public static Function method6261(Object[] objects, int n) {
      return arg(objects, n) instanceof Function function ? function : null;
   }

   public static Scriptable method6262(Object[] objects, int n) {
      Object obj = arg(objects, n);
      return obj instanceof Scriptable scriptable && !(obj instanceof Function) ? scriptable : null;
   }

   public static Object method6263(Scriptable scriptable, String str) {
      if (scriptable == null) {
         return Undefined.instance;
      } else {
         Object property = ScriptableObject.getProperty(scriptable, str);
         return property == Scriptable.NOT_FOUND ? Undefined.instance : property;
      }
   }

   public static double method6264(Scriptable scriptable, String str, double d) {
      Object id = method6263(scriptable, str);
      if (method6253(id)) {
         return d;
      } else {
         double number = Context.toNumber(id);
         return Double.isNaN(number) ? d : number;
      }
   }

   public static boolean method6265(Scriptable scriptable, String str, boolean flag) {
      Object id = method6263(scriptable, str);
      return method6253(id) ? flag : Context.toBoolean(id);
   }

   public static String method6266(Scriptable scriptable, String str, String name) {
      Object id = method6263(scriptable, str);
      return method6253(id) ? name : method6255(id);
   }

   public static Scriptable method6267(Scriptable scope, List<?> list) {
      NativeArray nativeArray = new NativeArray(list.toArray());
      ScriptRuntime.setBuiltinProtoAndParent(nativeArray, scope, Builtins.Array);
      return nativeArray;
   }

   public static Scriptable method6268(Scriptable scope, Object[] objects) {
      NativeArray nativeArray = new NativeArray(objects);
      ScriptRuntime.setBuiltinProtoAndParent(nativeArray, scope, Builtins.Array);
      return nativeArray;
   }

   public static List<Object> method6269(Object id) {
      ArrayList<Object> arrayList = new ArrayList<>();
      if (id instanceof NativeArray nativeArray) {
         long length = nativeArray.getLength();
         if (length > 10000L) {
            throw method6251("массив длиннее 10000 элементов");
         }

         for (int i = 0; i < length; i++) {
            Object obj = nativeArray.get(i, nativeArray);
            arrayList.add(obj == Scriptable.NOT_FOUND ? Undefined.instance : obj);
         }
      }

      return arrayList;
   }

   public static Scriptable method6270(Scriptable scope, double x, double y, double z) {
      return method6247(scope).method6277("x", x).method6277("y", y).method6277("z", z).getScriptableObject();
   }

   public static JsonElement method6271(Object id, int n) {
      return method6272(id, n, new int[1]);
   }

   private static JsonElement method6272(Object id, int n, int[] ints) {
      if (n > 32) {
         throw method6251("слишком глубокая вложенность");
      } else if (++ints[0] > 500000) {
         throw method6251("слишком большой объект (больше 500000 значений)");
      } else if (method6253(id)) {
         return JsonNull.INSTANCE;
      } else if (id instanceof Boolean bool) {
         return new JsonPrimitive(bool);
      } else if (id instanceof Number number) {
         return new JsonPrimitive(number.doubleValue());
      } else if (id instanceof CharSequence || id instanceof ConsString) {
         return new JsonPrimitive(method6255(id));
      } else if (id instanceof NativeArray nativeArray) {
         JsonArray jsonArray = new JsonArray();

         for (Object obj : method6269(nativeArray)) {
            jsonArray.add(method6272(obj, n + 1, ints));
         }

         return jsonArray;
      } else if (id instanceof Function) {
         return JsonNull.INSTANCE;
      } else if (id instanceof Scriptable scriptable) {
         JsonObject json = new JsonObject();
         Object[] ids = scriptable.getIds();
         if (ids.length > 10000) {
            throw method6251("объект больше 10000 ключей");
         } else {
            for (Object obj : ids) {
               String property = method6255(obj);
               Object id2 = obj instanceof Number number2 ? scriptable.get(number2.intValue(), scriptable) : scriptable.get(property, scriptable);
               if (!(id2 instanceof Function)) {
                  json.add(property, method6272(id2, n + 1, ints));
               }
            }

            return json;
         }
      } else {
         return new JsonPrimitive(method6255(id));
      }
   }

   public static Object method6273(Scriptable scope, JsonElement string) {
      if (string == null || string.isJsonNull()) {
         return null;
      } else if (string.isJsonPrimitive()) {
         JsonPrimitive jsonPrimitive = string.getAsJsonPrimitive();
         if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
         } else {
            return jsonPrimitive.isNumber() ? jsonPrimitive.getAsDouble() : jsonPrimitive.getAsString();
         }
      } else if (string.isJsonArray()) {
         ArrayList arrayList = new ArrayList();

         for (JsonElement json : string.getAsJsonArray()) {
            arrayList.add(method6273(scope, json));
         }

         return method6267(scope, arrayList);
      } else {
         ScriptValueUtils.Entry2 entry2 = method6247(scope);

         for (Map.Entry entry : string.getAsJsonObject().entrySet()) {
            entry2.method6277((String)entry.getKey(), method6273(scope, (JsonElement)entry.getValue()));
         }

         return entry2.getScriptableObject();
      }
   }

   public static final class Entry extends RuntimeException {
      public Entry(String message) {
         super(message, null, false, false);
      }
   }

   public static final class Entry2 {
      private final Scriptable scriptable;
      private final ScriptableObject scriptableObject;

      private Entry2(Scriptable scriptable, ScriptableObject scriptableObject) {
         this.scriptable = scriptable;
         this.scriptableObject = scriptableObject;
      }

      public ScriptValueUtils.Entry2 method6274(String str, ScriptValueUtils.Handler handler) {
         this.scriptableObject.defineProperty(str, ScriptValueUtils.method6249(this.scriptable, str, handler), 3);
         return this;
      }

      public ScriptValueUtils.Entry2 method6275(String str, Object obj) {
         this.scriptableObject.defineProperty(str, obj, 1);
         return this;
      }

      public ScriptValueUtils.Entry2 method6276(String str, Supplier<Object> supplier) {
         this.scriptableObject.defineProperty(str, ScriptValueUtils.method6249(this.scriptable, str, var1x -> supplier.get()), 3);
         return this;
      }

      public ScriptValueUtils.Entry2 method6277(String name, Object string) {
         this.scriptableObject.put(name, this.scriptableObject, string);
         return this;
      }

      public ScriptableObject getScriptableObject() {
         return this.scriptableObject;
      }
   }

   @FunctionalInterface
   public interface Handler {
      Object call(Object[] objects);
   }
}
