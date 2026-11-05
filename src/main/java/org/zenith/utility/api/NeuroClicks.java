package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import org.zenith.ZenithAccess;

public final class NeuroClicks implements ZenithAccess {
   public static final NeuroClicks INSTANCE = new NeuroClicks();
   public static final String text = "neuro_clicks.json";
   public static final List<String> items = List.of("strong", "early", "exact", "relaxed");
   private static final int CONST_intValue = 72000;
   private final Map<String, NeuroClicks.Entry> items2 = new HashMap<>();
   private int intValue;
   private int intValue2;
   private double[] doubleValues;
   private double[] doubleValues2;
   private double[] doubleValues3;
   private double[] doubleValues4;
   private double doubleValue;
   private double doubleValue2;
   private volatile boolean loaded;
   private String status = "не загружался";
   private final SplittableRandom splittableRandom = new SplittableRandom();
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4;
   private int intValue5 = -1073741824;
   private float floatValue = 1.0F;
   private boolean flag = true;
   private int intValue6;
   private double doubleValue3;
   private int intValue7;
   private int intValue8 = -1;
   private int intValue9 = -1;
   private static final int CONST_intValue2 = 16;
   private int intValue10 = Integer.MIN_VALUE;
   private boolean flag2;
   private int intValue11 = Integer.MIN_VALUE;
   private int intValue12 = Integer.MAX_VALUE;
   private int intValue13 = Integer.MIN_VALUE;
   private int intValue14 = Integer.MAX_VALUE;
   private int intValue15 = Integer.MIN_VALUE;
   private boolean flag3;
   private BufferedWriter bufferedWriter;
   private int intValue16;

   private NeuroClicks() {
   }

   public String getStatus() {
      return this.status;
   }

   public synchronized boolean method442() {
      if (this.loaded) {
         return true;
      } else {
         try (InputStream inputStream = this.method445()) {
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!"neurotest-clicks-v1".equals(jsonObject.get("format").getAsString())) {
               throw new IOException("формат " + jsonObject.get("format").getAsString());
            }

            JsonObject json = jsonObject.getAsJsonObject("profiles");

            for (String memberName : items) {
               JsonObject jsonObject2 = json.getAsJsonObject(memberName);
               NeuroClicks.Entry entry = new NeuroClicks.Entry();
               JsonObject jsonObject3 = jsonObject2.getAsJsonObject("crit");
               this.intValue = jsonObject3.get("f_max").getAsInt();
               this.intValue2 = jsonObject3.get("da_max").getAsInt();
               entry.doubleValues = method443(jsonObject3.getAsJsonArray("p"));
               JsonObject jsonObject4 = jsonObject2.getAsJsonObject("ground");
               entry.intValue = jsonObject4.get("d_min").getAsInt();
               entry.intValue2 = jsonObject4.get("d_max").getAsInt();
               entry.doubleValues2 = method444(jsonObject4.getAsJsonArray("h"));
               if (entry.doubleValues.length != this.intValue2
                  || entry.doubleValues[0].length != this.intValue
                  || entry.doubleValues2.length != entry.intValue2 - entry.intValue + 1) {
                  throw new IOException("размеры таблиц профиля " + memberName);
               }

               int n = Math.min(16, entry.intValue2) - entry.intValue + 1;
               entry.doubleValues3 = new double[n];
               double d = 1.0;
               double d2 = 0.0;

               for (int i = 0; i < n; i++) {
                  entry.doubleValues3[i] = d * entry.doubleValues2[i];
                  d *= 1.0 - entry.doubleValues2[i];
                  d2 += entry.doubleValues3[i];
               }

               for (int i = 0; i < n; i++) {
                  entry.doubleValues3[i] = entry.doubleValues3[i] / d2;
               }

               this.items2.put(memberName, entry);
            }

            JsonObject jsonObject2 = jsonObject.getAsJsonObject("sprint");
            this.doubleValues = method444(jsonObject2.getAsJsonArray("release_after_takeoff"));
            this.doubleValues2 = method444(jsonObject2.getAsJsonArray("repress_after_hit"));
            this.doubleValue = jsonObject2.get("hold_p").getAsDouble();
            JsonObject jsonObject3 = jsonObject2.getAsJsonObject("tap");
            this.doubleValue2 = jsonObject3.get("p").getAsDouble();
            this.doubleValues3 = method444(jsonObject3.getAsJsonArray("delay"));
            this.doubleValues4 = method444(jsonObject3.getAsJsonArray("dur"));
            this.loaded = true;
            this.status = "NeuroClicker загружен (neuro_clicks.json)";
         } catch (Exception e) {
            this.status = "NeuroClicker НЕ загружен (neuro_clicks.json): " + e.getMessage();
         }

         System.out.println(this.status);
         return this.loaded;
      }
   }

   private static double[][] method443(JsonArray jsonArray) {
      double[][] doubleses = new double[jsonArray.size()][];

      for (int i = 0; i < jsonArray.size(); i++) {
         doubleses[i] = method444(jsonArray.get(i).getAsJsonArray());
      }

      return doubleses;
   }

   private static double[] method444(JsonArray jsonArray) {
      double[] doubles = new double[jsonArray.size()];
      int n = 0;

      for (JsonElement json : jsonArray) {
         doubles[n++] = json.getAsDouble();
      }

      return doubles;
   }

   private InputStream method445() throws IOException {
      for (Path path : List.of(Path.of("neuro_clicks.json"), Path.of("models", "neuro_clicks.json"), Path.of("..", "models", "neuro_clicks.json"))) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = NeuroClicks.class.getClassLoader().getResourceAsStream("models/neuro_clicks.json");
      if (resourceAsStream == null) {
         throw new IOException("neuro_clicks.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   private int method446(double[] doubleValues3) {
      double nextDouble = this.splittableRandom.nextDouble();
      double d = 0.0;

      for (int i = 0; i < doubleValues3.length; i++) {
         d += doubleValues3[i];
         if (nextDouble < d) {
            return i;
         }
      }

      return doubleValues3.length - 1;
   }

   public void reset() {
      this.intValue3 = Integer.MIN_VALUE;
      this.intValue8 = -1;
      this.intValue9 = -1;
      this.intValue10 = Integer.MIN_VALUE;
      this.intValue11 = Integer.MIN_VALUE;
      this.intValue12 = Integer.MAX_VALUE;
      this.intValue13 = Integer.MIN_VALUE;
      this.intValue14 = Integer.MAX_VALUE;
      this.intValue15 = Integer.MIN_VALUE;
      this.flag3 = false;
      this.floatValue = 1.0F;
   }

   public void method447() {
      if (mc.player != null) {
         int tickCount = mc.player.tickCount;
         if (tickCount != this.intValue3) {
            if (tickCount != this.intValue3 + 1) {
               this.intValue5 = tickCount - 100;
               this.flag = mc.player.onGround();
               this.intValue6 = tickCount - 1;
               this.doubleValue3 = mc.player.getY();
               this.intValue7 = tickCount - 1;
               this.intValue8 = -1;
            }

            this.intValue3 = tickCount;
            this.intValue4 = tickCount;
            float attackStrengthScale = mc.player.getAttackStrengthScale(0.0F);
            if (attackStrengthScale < this.floatValue - 1.0E-4F) {
               this.intValue5 = tickCount - 1;
               this.intValue9 = -1;
            }

            this.floatValue = attackStrengthScale;
            int n = tickCount - 1;
            double y = mc.player.getY();
            this.flag = mc.player.onGround();
            if (this.flag) {
               this.intValue6 = n;
               this.doubleValue3 = y;
               this.intValue7 = n;
               this.intValue8 = -1;
            } else if (y > this.doubleValue3) {
               this.doubleValue3 = y;
               this.intValue7 = n;
            }
         }
      }
   }

   public int method448() {
      return this.intValue4 - this.intValue5;
   }

   public int method449() {
      return this.flag ? 0 : this.intValue4 - 1 - this.intValue7;
   }

   public boolean method450(boolean flag, String str, int n) {
      NeuroClicks.Entry entry = this.items2.get(str);
      if (entry == null) {
         return false;
      } else {
         return flag ? mc.player.getAttackStrengthScale(0.5F + n) > 0.9F : this.method448() + n >= entry.intValue;
      }
   }

   public boolean method451(boolean flag, String str) {
      if (this.intValue10 == this.intValue4) {
         return this.flag2;
      } else {
         this.intValue10 = this.intValue4;
         NeuroClicks.Entry entry = this.items2.get(str);
         if (entry == null) {
            return this.flag2 = false;
         } else {
            int n = this.method448();
            if (flag) {
               int n2 = this.method449();
               if (n2 < 1) {
                  return this.flag2 = false;
               } else {
                  if (this.intValue8 < 0) {
                     int n3 = Math.max(1, Math.min(this.intValue2, n - n2));
                     this.intValue8 = 1 + this.method446(entry.doubleValues[n3 - 1]);
                  }

                  return this.flag2 = n2 >= this.intValue8;
               }
            } else {
               if (this.intValue9 < 0) {
                  this.intValue9 = entry.intValue + this.method446(entry.doubleValues3);
               }

               return this.flag2 = n >= this.intValue9;
            }
         }
      }
   }

   public void method452(boolean flag, boolean down, boolean enabled) {
      int tickCount = mc.player.tickCount;
      if (!this.flag && this.flag3) {
         int n = 1 + this.method446(this.doubleValues2);
         this.intValue13 = n >= this.doubleValues2.length ? Integer.MAX_VALUE : tickCount + n;
         this.intValue12 = Integer.MAX_VALUE;
         this.flag3 = false;
      } else if (enabled && this.flag && flag && down && this.splittableRandom.nextDouble() < this.doubleValue2) {
         this.intValue14 = tickCount + this.method446(this.doubleValues3);
         this.intValue15 = this.intValue14 + 1 + this.method446(this.doubleValues4);
      }
   }

   public boolean method453(boolean forward, boolean flag, boolean flag2) {
      int tickCount = mc.player.tickCount;
      boolean flag3 = !mc.player.onGround();
      if (!flag3) {
         this.flag3 = false;
         this.intValue13 = Integer.MIN_VALUE;
         this.intValue12 = Integer.MAX_VALUE;
         this.intValue11 = Integer.MIN_VALUE;
         return forward && (tickCount < this.intValue14 || tickCount >= this.intValue15);
      } else {
         if (this.intValue11 != this.intValue6) {
            this.intValue11 = this.intValue6;
            this.intValue12 = forward && flag ? this.intValue6 + 1 + this.method446(this.doubleValues) : Integer.MAX_VALUE;
         }

         if (tickCount < this.intValue13) {
            return false;
         } else if (this.flag3) {
            return false;
         } else if ((tickCount >= this.intValue12 || flag2) && flag) {
            this.flag3 = true;
            return false;
         } else {
            return forward;
         }
      }
   }

   public void method454(boolean flag, boolean forward, boolean flag2, boolean flag3, boolean flag4) {
      if (this.intValue16 < 72000 && mc.player != null) {
         try {
            if (this.bufferedWriter == null) {
               this.bufferedWriter = Files.newBufferedWriter(
                  Path.of("neuro_clicks_log.csv"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND
               );
               this.bufferedWriter.write("age,target,d,f,on_ground,y,cd,w_user,w_out,sprint,attack,f_star\n");
            }

            this.bufferedWriter
               .write(
                  mc.player.tickCount
                     + ","
                     + (flag ? 1 : 0)
                     + ","
                     + this.method448()
                     + ","
                     + this.method449()
                     + ","
                     + (mc.player.onGround() ? 1 : 0)
                     + ","
                     + mc.player.getY()
                     + ","
                     + mc.player.getAttackStrengthScale(0.5F)
                     + ","
                     + (forward ? 1 : 0)
                     + ","
                     + (flag2 ? 1 : 0)
                     + ","
                     + (flag3 ? 1 : 0)
                     + ","
                     + (flag4 ? 1 : 0)
                     + ","
                     + this.intValue8
                     + "\n"
               );
            if (++this.intValue16 % 20 == 0) {
               this.bufferedWriter.flush();
            }
         } catch (IOException e) {
            this.intValue16 = 72000;
         }
      }
   }

   private static final class Entry {
      public double[][] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
      public int intValue;
      public int intValue2;
   }
}
