package org.zenith.base.figura.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.zenith.base.figura.math.matrix.FiguraMat2;
import org.zenith.base.figura.math.matrix.FiguraMat3;
import org.zenith.base.figura.math.matrix.FiguraMat4;
import org.zenith.base.figura.math.matrix.FiguraMatrix;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.math.vector.FiguraVec4;
import org.zenith.base.figura.math.vector.FiguraVector;
import org.zenith.base.figura.runtime.RuntimeDefaults;

public final class MathUtils {
   public static final double DEG_TO_RAD = Math.PI / 180.0;
   public static final double RAD_TO_DEG = 180.0 / Math.PI;
   private static final String[] SIZE_UNITS = new String[]{"B", "KiB", "MiB", "GiB"};

   private MathUtils() {
   }

   public static FiguraVector<?, ?> sizedVector(double... values) {
      return switch (values.length) {
         case 2 -> FiguraVec2.of(values[0], values[1]);
         case 3 -> FiguraVec3.of(values[0], values[1], values[2]);
         case 4 -> FiguraVec4.of(values[0], values[1], values[2], values[3]);
         default -> throw new IllegalStateException("Cannot create vector of size: " + values.length);
      };
   }

   public static FiguraMatrix<?, ?> sizedMat(FiguraVector<?, ?>... vectors) {
      return switch (vectors.length) {
         case 2 -> FiguraMat2.of(vectors[0].index(0), vectors[0].index(1), vectors[1].index(0), vectors[1].index(1));
         case 3 -> FiguraMat3.of(
            vectors[0].index(0),
            vectors[0].index(1),
            vectors[0].index(2),
            vectors[1].index(0),
            vectors[1].index(1),
            vectors[1].index(2),
            vectors[2].index(0),
            vectors[2].index(1),
            vectors[2].index(2)
         );
         case 4 -> FiguraMat4.of(
            vectors[0].index(0),
            vectors[0].index(1),
            vectors[0].index(2),
            vectors[0].index(3),
            vectors[1].index(0),
            vectors[1].index(1),
            vectors[1].index(2),
            vectors[1].index(3),
            vectors[2].index(0),
            vectors[2].index(1),
            vectors[2].index(2),
            vectors[2].index(3),
            vectors[3].index(0),
            vectors[3].index(1),
            vectors[3].index(2),
            vectors[3].index(3)
         );
         default -> throw new IllegalStateException("Cannot create matrix of size: " + vectors.length);
      };
   }

   public static FiguraVec3 rotateAroundAxis(FiguraVec3 vector, FiguraVec3 axis, double degrees) {
      FiguraVec3 normalizedAxis = axis.normalized();
      Quaternionf value = new Quaternionf((float)vector.x, (float)vector.y, (float)vector.z, 0.0F);
      Quaternionf rotation = new Quaternionf().fromAxisAngleDeg((float)normalizedAxis.x, (float)normalizedAxis.y, (float)normalizedAxis.z, (float)degrees);
      Quaternionf conjugate = new Quaternionf(rotation).conjugate();
      rotation.mul(value).mul(conjugate);
      return FiguraVec3.of(rotation.x(), rotation.y(), rotation.z());
   }

   public static FiguraVec3 toCameraSpace(FiguraVec3 vector) {
      Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
      FiguraMat3 transform = FiguraMat3.of().set(new Matrix3f().rotation(camera.rotation()));
      Vec3 cameraPosition = camera.position();
      transform.invert();
      return vector.copy().subtract(cameraPosition.x, cameraPosition.y, cameraPosition.z).transform(transform).multiply(-1.0, 1.0, -1.0);
   }

   public static FiguraVec4 worldToScreenSpace(FiguraVec3 worldSpace) {
      Minecraft client = Minecraft.getInstance();
      Camera camera = client.gameRenderer.mainCamera();
      Matrix3f view = new Matrix3f().rotation(camera.rotation());
      view.scale(-1.0F, 1.0F, -1.0F).invert();
      Vec3 cameraPosition = camera.position();
      FiguraVec3 difference = worldSpace.copy().subtract(cameraPosition.x, cameraPosition.y, cameraPosition.z);
      Vector3f cameraSpace = difference.asVec3f();
      view.transform(cameraSpace);
      Vector4f projected = new Vector4f(cameraSpace, 1.0F);
      Matrix4f projection = camera.projection.getMatrix(new Matrix4f());
      projection.transform(projected);
      float divisor = projected.w();
      return FiguraVec4.of(projected.x() / divisor, projected.y() / divisor, projected.z() / divisor, Math.sqrt(difference.dot(difference)));
   }

   public static String asFileSize(double size) {
      int unit;
      for (unit = 0; unit < SIZE_UNITS.length - 1 && size >= 1024.0; unit++) {
         size /= 1024.0;
      }

      DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
      format.setRoundingMode(RoundingMode.HALF_UP);
      return format.format(size) + SIZE_UNITS[unit];
   }

   public static float magicDelta(float speed, float delta) {
      return RuntimeDefaults.REDUCED_MOTION.value ? 1.0F : (float)(1.0 - Math.pow(speed, delta));
   }

   public static FiguraVec3 min(FiguraVec3 value, double min) {
      return FiguraVec3.of(Math.min(value.x, min), Math.min(value.y, min), Math.min(value.z, min));
   }

   public static FiguraVec3 max(FiguraVec3 value, double max) {
      return FiguraVec3.of(Math.max(value.x, max), Math.max(value.y, max), Math.max(value.z, max));
   }

   public static FiguraVec3 clamp(FiguraVec3 value, double min, double max) {
      return FiguraVec3.of(Math.min(Math.max(value.x, min), max), Math.min(Math.max(value.y, min), max), Math.min(Math.max(value.z, min), max));
   }

   public static FiguraVec3 lerp(double delta, FiguraVec3 previous, FiguraVec3 next) {
      return FiguraVec3.of(lerp(delta, previous.x, next.x), lerp(delta, previous.y, next.y), lerp(delta, previous.z, next.z));
   }

   public static double lerp(double delta, double previous, double next) {
      return delta * (next - previous) + previous;
   }

   public static FiguraVec3 catmullrom(double delta, FiguraVec3 previousA, FiguraVec3 previousB, FiguraVec3 nextA, FiguraVec3 nextB) {
      return FiguraVec3.of(
         catmullrom(delta, previousA.x, previousB.x, nextA.x, nextB.x),
         catmullrom(delta, previousA.y, previousB.y, nextA.y, nextB.y),
         catmullrom(delta, previousA.z, previousB.z, nextA.z, nextB.z)
      );
   }

   public static double catmullrom(double delta, double previousA, double previousB, double nextA, double nextB) {
      return 0.5
         * (
            2.0 * previousB
               + (-previousA + nextA) * delta
               + (2.0 * previousA - 5.0 * previousB + 4.0 * nextA - nextB) * delta * delta
               + (-previousA + 3.0 * previousB - 3.0 * nextA + nextB) * delta * delta * delta
         );
   }

   public static double bezier(double t, double p0, double p1, double p2, double p3) {
      double inverse = 1.0 - t;
      return p0 * inverse * inverse * inverse + 3.0 * p1 * inverse * inverse * t + 3.0 * p2 * inverse * t * t + p3 * t * t * t;
   }

   public static double bezierFindT(double x, double p0, double p1, double p2, double p3) {
      double t = 0.5;

      for (int i = 0; i < 100; i++) {
         double derivative = bezierDerivative(t, p0, p1, p2, p3);
         if (derivative == 0.0) {
            break;
         }

         double next = t - (bezier(t, p0, p1, p2, p3) - x) / derivative;
         if (Math.abs(next - t) < 1.0E-4) {
            return next;
         }

         t = next;
      }

      return t;
   }

   private static double bezierDerivative(double t, double p0, double p1, double p2, double p3) {
      double inverse = 1.0 - t;
      return 3.0 * (p1 - p0) * inverse * inverse + 6.0 * (p2 - p1) * inverse * t + 3.0 * (p3 - p2) * t * t;
   }
}
