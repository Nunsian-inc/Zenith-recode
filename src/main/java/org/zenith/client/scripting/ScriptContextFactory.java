package org.zenith.client.scripting;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Locale;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

public final class ScriptContextFactory extends ContextFactory {
   public static final ScriptContextFactory INSTANCE = new ScriptContextFactory();
   public static final long CONST_longValue = 250L;
   public static final long CONST_longValue2 = 3000L;
   public static final int CONST_intValue = 1000;
   private static final ClassShutter classShutter = str -> false;
   private static final WrapFactory wrapFactory = new WrapFactory() {
      @Override
      public Scriptable wrapAsJavaObject(Context context, Scriptable scriptable, Object obj, Class<?> clazz) {
         throw Context.reportRuntimeError("доступ к Java закрыт");
      }

      @Override
      public Scriptable wrapJavaClass(Context context, Scriptable scriptable, Class<?> clazz) {
         throw Context.reportRuntimeError("доступ к Java закрыт");
      }
   };
   private static final String[] texts = new String[]{
      "Packages",
      "java",
      "javax",
      "org",
      "com",
      "edu",
      "net",
      "getClass",
      "JavaAdapter",
      "JavaImporter",
      "importClass",
      "importPackage",
      "Continuation",
      "XML",
      "XMLList",
      "Namespace",
      "QName",
      "JavaException",
      "With",
      "Call",
      "load",
      "loadClass",
      "defineClass",
      "readFile",
      "readUrl",
      "runCommand",
      "spawn",
      "sync",
      "serialize",
      "deserialize",
      "ArrayBuffer",
      "SharedArrayBuffer",
      "DataView",
      "Int8Array",
      "Uint8Array",
      "Uint8ClampedArray",
      "Int16Array",
      "Uint16Array",
      "Int32Array",
      "Uint32Array",
      "Float32Array",
      "Float64Array",
      "BigInt64Array",
      "BigUint64Array"
   };
   private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
   private static final boolean CONST_flag = threadMXBean.isCurrentThreadCpuTimeSupported() && method7402();
   private static final MemoryPoolMXBean memoryPoolMXBean = method7403();
   private static final String text = "(function () {\n  var LIMIT = 1000000;\n  function guard(owner, name, check) {\n    var original = owner[name];\n    if (typeof original !== 'function') return;\n    Object.defineProperty(owner, name, {\n      value: function () { check.apply(this, arguments); return original.apply(this, arguments); },\n      writable: false, configurable: false, enumerable: false\n    });\n  }\n  function big(n) { return typeof n === 'number' && n > LIMIT; }\n  guard(String.prototype, 'repeat', function (n) {\n    if (String(this).length * (+n || 0) > LIMIT) throw new RangeError('строка больше миллиона символов');\n  });\n  guard(String.prototype, 'padStart', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(String.prototype, 'padEnd', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(Array.prototype, 'fill', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array.prototype, 'join', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array, 'from', function (src) { if (src != null && big(src.length)) throw new RangeError('массив больше миллиона элементов'); });\n  ['map', 'forEach', 'filter', 'reduce', 'reduceRight', 'some', 'every', 'indexOf', 'lastIndexOf', 'includes',\n   'find', 'findIndex', 'sort', 'reverse', 'splice', 'slice', 'concat', 'copyWithin', 'entries', 'keys', 'values',\n   'toString', 'toLocaleString', 'flat', 'flatMap'].forEach(function (name) {\n    guard(Array.prototype, name, function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  });\n  guard(String.prototype, 'split', function () { if (String(this).length > LIMIT) throw new RangeError('строка больше миллиона символов'); });\n})();\n";
   private static final org.mozilla.javascript.Script script = method7406();
   private static int intValue;

   private static boolean method7402() {
      try {
         if (!threadMXBean.isThreadCpuTimeEnabled()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
         }

         return threadMXBean.isThreadCpuTimeEnabled();
      } catch (RuntimeException e) {
         return false;
      }
   }

   private static MemoryPoolMXBean method7403() {
      for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
         if (memoryPoolMXBean.getType() == MemoryType.HEAP && memoryPoolMXBean.isCollectionUsageThresholdSupported()) {
            String lowerCase = memoryPoolMXBean.getName().toLowerCase(Locale.ROOT);
            if (lowerCase.contains("old") || lowerCase.contains("tenured")) {
               return memoryPoolMXBean;
            }
         }
      }

      return null;
   }

   private static long method7404() {
      return CONST_flag ? threadMXBean.getCurrentThreadCpuTime() / 1000000L : System.currentTimeMillis();
   }

   public static boolean method7405() {
      if (memoryPoolMXBean != null) {
         MemoryUsage collectionUsage = memoryPoolMXBean.getCollectionUsage();
         long max = memoryPoolMXBean.getUsage().getMax();
         if (collectionUsage != null && max > 0L && collectionUsage.getUsed() > max * 0.94) {
            return true;
         }
      }

      Runtime runtime = Runtime.getRuntime();
      return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()) < 50331648L;
   }

   private ScriptContextFactory() {
   }

   @Override
   public Context makeContext() {
      Context context = super.makeContext();
      context.setLanguageVersion(200);
      context.setOptimizationLevel(-1);
      context.setMaximumInterpreterStackDepth(1000);
      context.setInstructionObserverThreshold(5000);
      context.setClassShutter(classShutter);
      context.setWrapFactory(wrapFactory);
      context.setGeneratingDebug(true);
      return context;
   }

   @Override
   public boolean hasFeature(Context context, int n) {
      return switch (n) {
         case 6, 13, 20, 21 -> false;
         default -> super.hasFeature(context, n);
         case 10, 14 -> true;
      };
   }

   @Override
   public void observeInstructionCount(Context context, int n) {
      if (context.getThreadLocal(ScriptContextFactory.Entry2.class) instanceof ScriptContextFactory.Entry2 entry2 && entry2.longValue != 0L) {
         long now = System.currentTimeMillis();
         if (method7404() <= entry2.longValue && now <= entry2.longValue2) {
            if (now - entry2.longValue3 > 20L) {
               entry2.longValue3 = now;
               if (method7405()) {
                  throw new ScriptContextFactory.Entry("скрипт исчерпал память");
               }
            }
         } else {
            throw new ScriptContextFactory.Entry("скрипт превысил лимит времени (вечный цикл?)");
         }
      }
   }

   private static org.mozilla.javascript.Script method7406() {
      Context enterContext = INSTANCE.enterContext();

      org.mozilla.javascript.Script compileString;
      try {
         compileString = enterContext.compileString(
            "(function () {\n  var LIMIT = 1000000;\n  function guard(owner, name, check) {\n    var original = owner[name];\n    if (typeof original !== 'function') return;\n    Object.defineProperty(owner, name, {\n      value: function () { check.apply(this, arguments); return original.apply(this, arguments); },\n      writable: false, configurable: false, enumerable: false\n    });\n  }\n  function big(n) { return typeof n === 'number' && n > LIMIT; }\n  guard(String.prototype, 'repeat', function (n) {\n    if (String(this).length * (+n || 0) > LIMIT) throw new RangeError('строка больше миллиона символов');\n  });\n  guard(String.prototype, 'padStart', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(String.prototype, 'padEnd', function (n) { if (big(+n)) throw new RangeError('строка больше миллиона символов'); });\n  guard(Array.prototype, 'fill', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array.prototype, 'join', function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  guard(Array, 'from', function (src) { if (src != null && big(src.length)) throw new RangeError('массив больше миллиона элементов'); });\n  ['map', 'forEach', 'filter', 'reduce', 'reduceRight', 'some', 'every', 'indexOf', 'lastIndexOf', 'includes',\n   'find', 'findIndex', 'sort', 'reverse', 'splice', 'slice', 'concat', 'copyWithin', 'entries', 'keys', 'values',\n   'toString', 'toLocaleString', 'flat', 'flatMap'].forEach(function (name) {\n    guard(Array.prototype, name, function () { if (big(this.length)) throw new RangeError('массив больше миллиона элементов'); });\n  });\n  guard(String.prototype, 'split', function () { if (String(this).length > LIMIT) throw new RangeError('строка больше миллиона символов'); });\n})();\n",
            "guards",
            1,
            null
         );
      } finally {
         Context.exit();
      }

      return compileString;
   }

   public static void method7407() {
      if ((++intValue & 4095) == 0) {
         Context currentContext = Context.getCurrentContext();
         if (currentContext != null && currentContext.getFactory() == INSTANCE) {
            if (currentContext.getThreadLocal(ScriptContextFactory.Entry2.class) instanceof ScriptContextFactory.Entry2 entry2
               && entry2.longValue != 0L
               && (method7404() > entry2.longValue || System.currentTimeMillis() > entry2.longValue2)) {
               throw new ScriptContextFactory.Entry("регулярное выражение работает слишком долго");
            }
         }
      }
   }

   public static ScriptableObject method7408(Context context) {
      ScriptableObject initSafeStandardObjects = context.initSafeStandardObjects(null, false);

      for (String text : texts) {
         initSafeStandardObjects.delete(text);
      }

      script.exec(context, initSafeStandardObjects);
      return initSafeStandardObjects;
   }

   public static Context method7409() {
      return INSTANCE.enterContext();
   }

   public static Object method7410(long l, Callable function, Scriptable scope, Object[] scriptableObject) {
      Context enterContext = INSTANCE.enterContext();
      ScriptContextFactory.Entry2 entry2 = enterContext.getThreadLocal(ScriptContextFactory.Entry2.class) instanceof ScriptContextFactory.Entry2 entry22
         ? entry22
         : new ScriptContextFactory.Entry2();
      long longValue = entry2.longValue;
      boolean flag = longValue == 0L;

      Object obj;
      try {
         if (flag) {
            entry2.longValue = method7404() + l;
            entry2.longValue2 = System.currentTimeMillis() + l * 6L + 500L;
            entry2.longValue3 = 0L;
            enterContext.putThreadLocal(ScriptContextFactory.Entry2.class, entry2);
         }

         obj = function.call(enterContext, scope, scope, scriptableObject);
      } finally {
         if (flag) {
            entry2.longValue = 0L;
         }

         Context.exit();
      }

      return obj;
   }

   public static final class Entry extends Error {
      public Entry(String message) {
         super(message, null, false, false);
      }
   }

   private static final class Entry2 {
      public long longValue;
      public long longValue2;
      public long longValue3;
   }
}
