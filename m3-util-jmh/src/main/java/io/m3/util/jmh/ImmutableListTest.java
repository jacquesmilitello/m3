package io.m3.util.jmh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class ImmutableListTest {

    public final static int MAX = 100000;
    private final static List<Integer> ARRAY_LIST_INTEGER = new ArrayList<>();
    private final static List<String> ARRAY_LIST_STRINGS = new ArrayList<>();
    private final static ImmutableList<Integer> IMMUTABLE_INTEGER;
    private final static ImmutableList<String> IMMUTABLE_STRING;

    static {

        Integer[] integer = new Integer[MAX];
        String[] strings = new String[MAX];

        for (int i = 0 ; i < MAX ; i++) {
            ARRAY_LIST_INTEGER.add(i);
            ARRAY_LIST_STRINGS.add("HELLO WOLRD" + i);
            integer[i] = i;
            strings[i] = "HELLO WOLRD" + i;
        }
        IMMUTABLE_INTEGER = ImmutableList.copyOf(integer);
        IMMUTABLE_STRING = ImmutableList.copyOf(strings);
    }

    @Benchmark
    public void testArrayListInteger() {

        for (int i = 0 ; i < MAX ; i++) {
            ARRAY_LIST_INTEGER.get(i);
        }

    }

    @Benchmark
    public void testArrayListString() {
        for (int i = 0 ; i < MAX ; i++) {
            ARRAY_LIST_STRINGS.get(i);
        }
    }

    @Benchmark
    public void testArrayListStringIterator() {
        Iterator<String> it = ARRAY_LIST_STRINGS.iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    @Benchmark
    public void testImmultableistInteger() {
        for (int i = 0 ; i < MAX ; i++) {
            IMMUTABLE_INTEGER.get(i);
        }

    }

    @Benchmark
    public void testImmultableistString() {

        for (int i = 0 ; i < MAX ; i++) {
            IMMUTABLE_STRING.get(i);
        }

    }

    @Benchmark
    public void testImmutableListStringIterator() {
        Iterator<String> it = IMMUTABLE_STRING.iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    @Test
    public void dotest() throws Exception {
        // @formatter:off
        Options opt = new OptionsBuilder()
                .include(ImmutableListTest.class.getSimpleName())
                .forks(1)
                .build();
        // @formatter:on

        new Runner(opt).run();
    }
}
