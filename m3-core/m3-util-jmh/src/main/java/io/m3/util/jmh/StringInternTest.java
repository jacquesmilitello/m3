package io.m3.util.jmh;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class StringInternTest {

    public static final String SQL = "SELECT id,code,age,overall_rating FROM student WHERE id=? SELECT id,code,age,overall_rating FROM student WHERE id=? SELECT id,code,age,overall_rating FROM student WHERE id=?";

    private static final Map<String, Boolean> HASH_MAP = new HashMap<>();
    private static final Map<String, Boolean> IDENTITY_HASH_MAP = new IdentityHashMap<>();
    private static final Map<String, Boolean> FAST_HASH_MAP = new javolution.util.FastMap<>();

    static {
        HASH_MAP.put(SQL, Boolean.FALSE);
        IDENTITY_HASH_MAP.put(SQL, Boolean.FALSE);
        FAST_HASH_MAP.put(SQL, Boolean.FALSE);

        for (int i = 0; i < 1_000_000; i++) {
            HASH_MAP.put(SQL + i, Boolean.FALSE);
            IDENTITY_HASH_MAP.put((SQL + i).intern(), Boolean.FALSE);
            FAST_HASH_MAP.put((SQL + i).intern(), Boolean.FALSE);
        }
    }

    @Benchmark
    public void testHashMapGet() {
        for (int i = 0; i < 1_000_000; i++) {
            HASH_MAP.get(SQL);
        }
    }

    @Benchmark
    public void testHashMapPut() {
        Map<String, Boolean> map = new HashMap<>();
        for (int i = 0; i < 1_000_000; i++) {
            map.put(SQL + i, Boolean.FALSE);
        }
    }


    @Benchmark
    public void testIdentityHashMapGet() {
        for (int i = 0; i < 1_000_000; i++) {
            IDENTITY_HASH_MAP.get(SQL);
        }
    }

    @Benchmark
    public void testIdentityHashMapPut() {
        Map<String, Boolean> map = new IdentityHashMap<>();
        for (int i = 0; i < 1_000_000; i++) {
            map.put(SQL + i, Boolean.FALSE);
        }
    }

    @Benchmark
    public void testJavolutionMapGet() {
        for (int i = 0; i < 1_000_000; i++) {
            FAST_HASH_MAP.get(SQL);
        }
    }

    @Benchmark
    public void testJavolutionMapPut() {
        Map<String, Boolean> map = new javolution.util.FastMap<>();
        for (int i = 0; i < 1_000_000; i++) {
            map.put(SQL + i, Boolean.FALSE);
        }
    }

    @Test
    void dotest() throws Exception {
        // @formatter:off
        Options opt = new OptionsBuilder().include(StringInternTest.class.getSimpleName()).forks(2).jvmArgs("-server", "-XX:+AggressiveOpts", "-XX:+UseFastAccessorMethods", "-XX:+UseG1GC")
                .verbosity(VerboseMode.EXTRA)
                //.shouldDoGC(true)
                .build();
        // @formatter:on

        new Runner(opt).run();
    }

}
