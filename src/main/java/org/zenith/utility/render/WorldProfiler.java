package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldProfiler {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/WProf");
   public static boolean enabled = Boolean.getBoolean("zenith.wprof");
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   public static final int CONST_intValue6 = 5;
   public static final int CONST_intValue7 = 6;
   public static final int CONST_intValue8 = 7;
   public static final int CONST_intValue9 = 8;
   public static final int CONST_intValue10 = 9;
   public static final int CONST_intValue11 = 10;
   public static final int CONST_intValue12 = 11;
   public static final int CONST_intValue13 = 12;
   public static final int CONST_intValue14 = 13;
   public static final int CONST_intValue15 = 14;
   public static final int CONST_intValue16 = 15;
   public static final int CONST_intValue17 = 16;
   public static final int CONST_intValue18 = 17;
   public static final int CONST_intValue19 = 18;
   public static final int CONST_intValue20 = 19;
   public static final int CONST_intValue21 = 20;
   public static final int CONST_intValue22 = 21;
   public static final int CONST_intValue23 = 22;
   public static final int CONST_intValue24 = 23;
   public static final int CONST_intValue25 = 24;
   public static final int CONST_intValue26 = 25;
   public static final int CONST_intValue27 = 26;
   public static final int CONST_intValue28 = 27;
   private static final int CONST_intValue29 = 28;
   private static final String[] texts = new String[]{
      "depth.copy",
      "sky (sum)",
      "  sky.shader",
      "  sky.blit",
      "grade (sum)",
      "  grade.shader",
      "  grade.blit",
      "gloss (sum)",
      "  gloss.gloss",
      "  gloss.blurH",
      "  gloss.blurV",
      "  gloss.combine",
      "  gloss.blit",
      "weather.pass",
      "  snow.flush",
      "    snow.upload",
      "  batch.flush",
      "  surfaces",
      "    settling",
      "  footprints",
      "  snow.emit",
      "    snow.grid",
      "    snow.mesh",
      "  mist",
      "  precip",
      "build.sky",
      "build.grade",
      "build.gloss"
   };
   private static final int[] CONST_intValues = new int[28];
   private static final boolean[] CONST_flags = new boolean[28];
   public static final int CONST_intValue30 = 0;
   public static final int CONST_intValue31 = 1;
   public static final int CONST_intValue32 = 2;
   private static final int CONST_intValue33 = 3;
   private static final String[] texts2;
   public static final int CONST_intValue34 = 0;
   public static final int CONST_intValue35 = 1;
   public static final int CONST_intValue36 = 2;
   private static final int[] CONST_intValues2;
   private static final int[] CONST_intValues3;
   private static int intValue;
   private static int intValue2;
   private static final int CONST_intValue37 = 6;
   private static final long CONST_longValue = 3000000000L;
   private static final WorldProfiler.Entry[] entrys;
   private static int slot;
   private static WorldProfiler.Entry entry;
   private static boolean flag;
   private static final long[] CONST_longValues;
   private static final boolean[] CONST_flags2;
   private static final int[] CONST_intValues4;
   private static final long[] CONST_longValues2;
   private static final boolean[] CONST_flags3;
   private static long longValue;
   private static long lastFrame;
   private static int intValue3;
   private static long longValue2;
   private static long longValue3;
   private static int intValue4;
   private static int intValue5;
   private static int intValue6;
   private static final long[] CONST_longValues3;
   private static final long[] CONST_longValues4;
   private static final int[] CONST_intValues5;
   private static final long[] CONST_longValues5;
   private static final long[] CONST_longValues6;
   private static final int[] CONST_intValues6;
   private static final long[] CONST_longValues7;
   private static final int[] CONST_intValues7;

   private WorldProfiler() {
   }

   public static void setEnabled(boolean enabled2) {
      if (enabled2 != enabled) {
         enabled = enabled2;
         flag = true;
         entry = null;
         intValue = -1;

         for (WorldProfiler.Entry entry2 : entrys) {
            if (entry2 != null) {
               entry2.flag = false;
               entry2.intValue2 = 0;
               entry2.intValue = 0;
            }
         }

         logger.info("[WPROF] {}", enabled2 ? "enabled" : "disabled");
      }
   }

   public static long method782() {
      return enabled ? System.nanoTime() : 0L;
   }

   public static void method783(int n, long l) {
      if (l != 0L && enabled) {
         CONST_longValues[n] = CONST_longValues[n] + (System.nanoTime() - l);
         CONST_flags2[n] = true;
      }
   }

   public static void method784(int n) {
      if (enabled) {
         CONST_intValues4[n]++;
      }
   }

   public static int method785(int n) {
      if (enabled && entry != null) {
         WorldProfiler.Entry entry2 = entry;
         int n2 = entry2.method798();
         int n3 = entry2.method797();
         entry2.intValues2[n2] = n;
         entry2.intValues3[n2] = n3;
         entry2.intValues4[n2] = -1;
         GL33.glQueryCounter(entry2.intValues[n3], 36392);
         entry2.longValues[n2] = System.nanoTime();
         return slot << 20 | n2;
      } else {
         return -1;
      }
   }

   public static void method786(int n) {
      if (n >= 0 && enabled && entry != null) {
         WorldProfiler.Entry entry2 = entry;
         int n2 = n & 1048575;
         if (n >>> 20 == slot && n2 < entry2.intValue2) {
            long nanoTime = System.nanoTime();
            int n3 = entry2.intValues2[n2];
            CONST_longValues[n3] = CONST_longValues[n3] + (nanoTime - entry2.longValues[n2]);
            CONST_flags2[n3] = true;
            int n4 = entry2.method797();
            entry2.intValues4[n2] = n4;
            GL33.glQueryCounter(entry2.intValues[n4], 36392);
         }
      }
   }

   public static void method787(int n) {
      if (enabled) {
         intValue = n;
         intValue2 = 0;
      }
   }

   public static void method788() {
      intValue = -1;
   }

   public static Runnable method789(Runnable runnable) {
      if (enabled && intValue >= 0) {
         int n = CONST_intValues2[intValue] + Math.min(intValue2, CONST_intValues3[intValue] - 1);
         intValue2++;
         return () -> {
            int n2 = method785(n);
            runnable.run();
            method786(n2);
         };
      } else {
         return runnable;
      }
   }

   public static void method790() {
      if (enabled) {
         long nanoTime = System.nanoTime();
         if (lastFrame != 0L && nanoTime - lastFrame > 1000000000L) {
            flag = true;
         }

         if (flag) {
            flag = false;
            method794(nanoTime);
            lastFrame = 0L;
            Arrays.fill(CONST_longValues, 0L);
            Arrays.fill(CONST_flags2, false);
            Arrays.fill(CONST_intValues4, 0);
         } else if (lastFrame != 0L) {
            method791(nanoTime - lastFrame);
         }

         lastFrame = nanoTime;
         if (entry != null) {
            entry.flag = entry.intValue2 > 0;
         }

         method792();
         slot = (slot + 1) % 6;
         WorldProfiler.Entry entry2 = entrys[slot];
         if (entry2 == null) {
            entry2 = new WorldProfiler.Entry();
            entrys[slot] = entry2;
         }

         if (entry2.flag) {
            intValue5++;
            entry2.flag = false;
         }

         entry2.intValue2 = 0;
         entry2.intValue = 0;
         entry = entry2;
         if (nanoTime - longValue >= 3000000000L) {
            method795(nanoTime);
            method794(nanoTime);
         }
      }
   }

   private static void method791(long l) {
      intValue3++;
      longValue2 += l;
      longValue3 = Math.max(longValue3, l);

      for (int i = 0; i < 28; i++) {
         if (CONST_flags2[i]) {
            long l2 = CONST_longValues[i];
            CONST_longValues3[i] = CONST_longValues3[i] + l2;
            CONST_longValues4[i] = Math.max(CONST_longValues4[i], l2);
            CONST_intValues5[i]++;
            int n = CONST_intValues[i];
            if (n >= 0) {
               CONST_longValues[n] = CONST_longValues[n] + l2;
            }

            CONST_longValues[i] = 0L;
            CONST_flags2[i] = false;
         }
      }

      for (int i = 0; i < 28; i++) {
         if (CONST_longValues[i] != 0L) {
            long l2 = CONST_longValues[i];
            CONST_longValues3[i] = CONST_longValues3[i] + l2;
            CONST_longValues4[i] = Math.max(CONST_longValues4[i], l2);
            CONST_intValues5[i]++;
            CONST_longValues[i] = 0L;
         }
      }

      for (int i = 0; i < 3; i++) {
         CONST_longValues7[i] = CONST_longValues7[i] + CONST_intValues4[i];
         CONST_intValues7[i] = Math.max(CONST_intValues7[i], CONST_intValues4[i]);
         CONST_intValues4[i] = 0;
      }
   }

   private static void method792() {
      for (int i = 1; i <= 6; i++) {
         int n = (slot + i + 6) % 6;
         WorldProfiler.Entry entry = entrys[n];
         if (entry != null && entry.flag) {
            int intValue = entry.intValues[entry.intValue - 1];
            if (GL15.glGetQueryObjecti(intValue, 34919) == 0) {
               return;
            }

            intValue6 = intValue6 + (slot - n + 6) % 6 + 1;
            method793(entry);
            entry.flag = false;
         }
      }
   }

   private static void method793(WorldProfiler.Entry entry) {
      for (int i = 0; i < entry.intValue2; i++) {
         int n = entry.intValues4[i];
         if (n >= 0) {
            long glGetQueryObjecti64 = GL33.glGetQueryObjecti64(entry.intValues[entry.intValues3[i]], 34918);
            long l = GL33.glGetQueryObjecti64(entry.intValues[n], 34918);
            int n2 = entry.intValues2[i];
            long l2 = Math.max(0L, l - glGetQueryObjecti64);
            CONST_longValues2[n2] = CONST_longValues2[n2] + l2;
            CONST_flags3[n2] = true;
            int n3 = CONST_intValues[n2];
            if (n3 >= 0) {
               CONST_longValues2[n3] = CONST_longValues2[n3] + l2;
               CONST_flags3[n3] = true;
            }
         }
      }

      intValue4++;

      for (int i = 0; i < 28; i++) {
         if (CONST_flags3[i]) {
            long l = CONST_longValues2[i];
            CONST_longValues5[i] = CONST_longValues5[i] + l;
            CONST_longValues6[i] = Math.max(CONST_longValues6[i], l);
            CONST_intValues6[i]++;
            CONST_longValues2[i] = 0L;
            CONST_flags3[i] = false;
         }
      }
   }

   private static void method794(long nanoTime) {
      longValue = nanoTime;
      intValue3 = 0;
      longValue2 = 0L;
      longValue3 = 0L;
      intValue4 = 0;
      intValue5 = 0;
      intValue6 = 0;
      Arrays.fill(CONST_longValues3, 0L);
      Arrays.fill(CONST_longValues4, 0L);
      Arrays.fill(CONST_intValues5, 0);
      Arrays.fill(CONST_longValues5, 0L);
      Arrays.fill(CONST_longValues6, 0L);
      Arrays.fill(CONST_intValues6, 0);
      Arrays.fill(CONST_longValues7, 0L);
      Arrays.fill(CONST_intValues7, 0);
   }

   private static void method795(long nanoTime) {
      if (intValue3 != 0) {
         Minecraft minecraft = Minecraft.getInstance();
         RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
         String str = mainRenderTarget != null ? mainRenderTarget.width + "x" + mainRenderTarget.height : "?";
         double d = method796(longValue2) / intValue3;
         double d2 = 0.0;
         double d3 = 0.0;

         for (int i = 0; i < 28; i++) {
            if (CONST_flags[i]) {
               if (intValue4 > 0) {
                  d2 += method796(CONST_longValues5[i]) / intValue4;
               }

               d3 += method796(CONST_longValues3[i]) / intValue3;
            }
         }

         logger.info(
            String.format(
               Locale.ROOT,
               "[WPROF] ==== %.1fs | frames %d | frame avg %.3f ms (%.0f fps) max %.3f | res=%s | gpu frames %d lag %.1f dropped %d | weather total/frame: gpu %.3f ms, cpu %.3f ms",
               method796(nanoTime - longValue) / 1000.0,
               intValue3,
               d,
               d > 0.0 ? 1000.0 / d : 0.0,
               method796(longValue3),
               str,
               intValue4,
               intValue4 > 0 ? (double)intValue6 / intValue4 : 0.0,
               intValue5,
               d2,
               d3
            )
         );

         for (int i = 0; i < 28; i++) {
            if (CONST_intValues5[i] != 0 || CONST_intValues6[i] != 0) {
               StringBuilder builder = new StringBuilder(128);
               builder.append("[WPROF] ").append(String.format(Locale.ROOT, "%-18s", texts[i]));
               if (CONST_intValues6[i] > 0) {
                  builder.append(
                     String.format(
                        Locale.ROOT,
                        " gpu avg %7.3f max %7.3f",
                        method796(CONST_longValues5[i]) / CONST_intValues6[i],
                        method796(CONST_longValues6[i])
                     )
                  );
               } else {
                  builder.append("                              ");
               }

               if (CONST_intValues5[i] > 0) {
                  builder.append(
                     String.format(
                        Locale.ROOT,
                        " | cpu avg %7.3f max %7.3f",
                        method796(CONST_longValues3[i]) / CONST_intValues5[i],
                        method796(CONST_longValues4[i])
                     )
                  );
               }

               builder.append(String.format(Locale.ROOT, " | n %d/%d", Math.max(CONST_intValues5[i], CONST_intValues6[i]), intValue3));
               logger.info(builder.toString());
            }
         }

         for (int i = 0; i < 3; i++) {
            if (CONST_longValues7[i] != 0L) {
               logger.info(
                  String.format(
                     Locale.ROOT,
                     "[WPROF] %-18s avg %.3f/frame max %d total %d",
                     texts2[i],
                     (double)CONST_longValues7[i] / intValue3,
                     CONST_intValues7[i],
                     CONST_longValues7[i]
                  )
               );
            }
         }
      }
   }

   private static double method796(long l) {
      return l / 1000000.0;
   }

   static {
      Arrays.fill(CONST_intValues, -1);
      CONST_intValues[2] = 1;
      CONST_intValues[3] = 1;
      CONST_intValues[5] = 4;
      CONST_intValues[6] = 4;

      for (int i = 8; i <= 12; i++) {
         CONST_intValues[i] = 7;
      }

      CONST_flags[0] = true;
      CONST_flags[1] = true;
      CONST_flags[4] = true;
      CONST_flags[7] = true;
      CONST_flags[13] = true;
      CONST_flags[25] = true;
      CONST_flags[26] = true;
      CONST_flags[27] = true;
      texts2 = new String[]{"snow.gridRebuilds", "snow.meshRebuilds", "snow.uploads"};
      CONST_intValues2 = new int[]{2, 5, 8};
      CONST_intValues3 = new int[]{2, 2, 5};
      intValue = -1;
      entrys = new WorldProfiler.Entry[6];
      slot = -1;
      flag = true;
      CONST_longValues = new long[28];
      CONST_flags2 = new boolean[28];
      CONST_intValues4 = new int[3];
      CONST_longValues2 = new long[28];
      CONST_flags3 = new boolean[28];
      CONST_longValues3 = new long[28];
      CONST_longValues4 = new long[28];
      CONST_intValues5 = new int[28];
      CONST_longValues5 = new long[28];
      CONST_longValues6 = new long[28];
      CONST_intValues6 = new int[28];
      CONST_longValues7 = new long[3];
      CONST_intValues7 = new int[3];
   }

   private static final class Entry {
      public int[] intValues = new int[0];
      public int intValue;
      public int[] intValues2 = new int[32];
      public int[] intValues3 = new int[32];
      public int[] intValues4 = new int[32];
      public long[] longValues = new long[32];
      public int intValue2;
      public boolean flag;

      public int method797() {
         if (this.intValue == this.intValues.length) {
            int[] intValues = Arrays.copyOf(this.intValues, Math.max(16, this.intValues.length * 2));

            for (int i = this.intValues.length; i < intValues.length; i++) {
               intValues[i] = GL15.glGenQueries();
            }

            this.intValues = intValues;
         }

         return this.intValue++;
      }

      public int method798() {
         if (this.intValue2 == this.intValues2.length) {
            int newLength = this.intValue2 * 2;
            this.intValues2 = Arrays.copyOf(this.intValues2, newLength);
            this.intValues3 = Arrays.copyOf(this.intValues3, newLength);
            this.intValues4 = Arrays.copyOf(this.intValues4, newLength);
            this.longValues = Arrays.copyOf(this.longValues, newLength);
         }

         return this.intValue2++;
      }
   }
}
