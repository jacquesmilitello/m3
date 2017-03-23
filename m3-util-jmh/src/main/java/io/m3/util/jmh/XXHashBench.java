package io.m3.util.jmh;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import io.m3.util.hash.Hashing;
import io.m3.util.unsafe.UnsafeString;
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
import org.openjdk.jmh.runner.options.VerboseMode;

import net.openhft.hashing.LongHashFunction;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class XXHashBench {

	public static final String value = "Hello World ! Hello World ! Hello World ! Hello World ! Hello World ! Hello World ! Hello World ! Hello World ! Hello World ! Hello World ! ";

	@Benchmark
	public void testm3HashReader() {
		for (int i = 0 ; i < 512 ; i++) {
			Hashing.xx(123456, new HashReader4String(UnsafeString.getChars(value)));	
		}
	}
	
	@Benchmark
	public void testOpenHft() {
		for (int i = 0 ; i < 512 ; i++) {
			LongHashFunction.xx(123456).hashChars(value);	
		}
	}
	

	@Benchmark
	public void testm3() {
		for (int i = 0 ; i < 512 ; i++) {
			Hashing.xx(123456, value);	
		}
	}
	
	

	@Test
	public void dotest() throws Exception {
		// @formatter:off
		Options opt = new OptionsBuilder().include(XXHashBench.class.getSimpleName()).forks(1).jvmArgs("-server","-XX:+AggressiveOpts","-XX:+UseFastAccessorMethods","-XX:+UseG1GC")
				.verbosity(VerboseMode.EXTRA)
				.shouldDoGC(true).build();
		// @formatter:on

		new Runner(opt).run();
	}

}
