import java.util.Map;
import org.zenith.utility.misc.AnimationCurve;
import org.zenith.utility.misc.ScriptHook;
import org.zenith.utility.misc.UniformByteVolume;

// Java 25: java -cp build/classes/java/main tests/ReadabilityChecks.java
class ReadabilityChecks {
    public static void main(String[] args) {
        checkHookOrderingAndUnsubscribe();
        checkVolumeCompaction();
        checkAnimationSampling();
        System.out.println("Readability checks passed: hooks, voxel storage, animation curves");
    }

    private static void checkHookOrderingAndUnsubscribe() {
        ScriptHook<StringBuilder> hook = new ScriptHook<>("check", StringBuilder.class, "check");
        Runnable unsubscribe = hook.subscribe(0, value -> value.append("first,"));
        hook.subscribe(-100, value -> value.append("early,"));
        hook.subscribe(0, value -> value.append("second,"));
        StringBuilder calls = new StringBuilder();
        check(hook.dispatch(calls) == calls, "dispatch must return its payload");
        check(calls.toString().equals("early,first,second,"), "priority and insertion ordering");
        unsubscribe.run();
        unsubscribe.run();
        calls.setLength(0);
        hook.dispatch(calls);
        check(calls.toString().equals("early,second,"), "idempotent unsubscribe");
        check(hook.active(), "remaining subscribers are active");
    }

    private static void checkVolumeCompaction() {
        UniformByteVolume volume = new UniformByteVolume(4, (byte) -127);
        check(volume.isUniform(), "new volume must be uniform");
        check(volume.getVoxel(0, 0, 0) == -127, "default voxel value");
        volume.setVoxel(3, 2, 1, (byte) 42);
        check(volume.getVoxel(3, 2, 1) == 42, "coordinate round trip");
        check(volume.getVoxel(1, 2, 3) == -127, "coordinates must not alias");
        check(volume.countAbove((byte) 0) == 1, "positive voxel count");
        check(!volume.tryCompact(), "nonuniform volume must not compact");
        volume.setVoxel(3, 2, 1, (byte) -127);
        check(volume.tryCompact() && volume.isUniform(), "uniform data compacts");
        check(volume.estimatedMemoryBytes() == 16, "compacted storage size");
    }

    private static void checkAnimationSampling() {
        AnimationCurve.Entry track = new AnimationCurve.Entry(
            new float[]{0, 2}, new float[]{0, 2, 4, 2, 4, 6}, false
        );
        AnimationCurve curve = new AnimationCurve("check", 2, true,
            Map.of(0, new AnimationCurve.Entry[]{track, null, null}), 1);
        check(curve.normalizeTime(-0.5F) == 1.5F, "negative looping time wraps");
        float[] pose = new float[9];
        curve.sampleBone(0, 1, pose);
        check(pose[0] == 1 && pose[1] == 3 && pose[2] == 5, "linear interpolation");
        check(pose[3] == 0 && pose[6] == 1, "missing channels retain identity");
        curve.sampleBone(10, 1, pose);
        check(pose[0] == 0 && pose[8] == 1, "unknown bone returns identity");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
