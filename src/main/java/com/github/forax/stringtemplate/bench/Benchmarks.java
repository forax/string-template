package com.github.forax.stringtemplate.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-preview" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class Benchmarks {
  @Benchmark
  public String concat() {
    var message = "string template";
    return "hello " + message + " !";
  }

  @Benchmark
  public String with_STR() {
    var message = "string template";
    return STR."hello \{message} !";
  }

  static final StringTemplate.Processor<String, RuntimeException> STR_INTERPOLATE = StringTemplate::interpolate;

  @Benchmark
  public String with_interpolate() {
    var message = "string template";
    return STR_INTERPOLATE."hello \{message} !";
  }

  static final StringTemplate.Processor<String, RuntimeException> STR_BY_HAND = stringTemplate -> {
    var builder = new StringBuilder();
    var fragments = stringTemplate.fragments();
    var values = stringTemplate.values();
    for (var i = 0; i < fragments.size() - 1; i++) {
      var fragment = fragments.get(i);
      var value = values.get(i);
      builder.append(fragment).append(value);
    }
    builder.append(fragments.getLast());
    return builder.toString();
  };

  @Benchmark
  public String by_hand() {
    var message = "string template";
    return STR_BY_HAND."hello \{message} !";
  }
}
