package com.github.forax.stringtemplate.log;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.forax.stringtemplate.log.LoggerProcessor.LOGGER;

// Benchmark                                               Mode  Cnt   Score   Error  Units
// LoggerBenchmarks.jul_concat_non_constant_message        avgt    5   4.801 ± 0.024  ns/op
// LoggerBenchmarks.jul_constant_message                   avgt    5   0.847 ± 0.006  ns/op
// LoggerBenchmarks.jul_lambda_non_constant_message        avgt    5   0.866 ± 0.004  ns/op
// LoggerBenchmarks.jul_str_non_constant_message           avgt    5   4.555 ± 0.028  ns/op
// LoggerBenchmarks.processor_constant_message             avgt    5   7.286 ± 0.005  ns/op
// LoggerBenchmarks.processor_non_constant_message         avgt    5  12.643 ± 0.028  ns/op
// LoggerBenchmarks.simple_processor_constant_message      avgt    5   5.692 ± 0.013  ns/op
// LoggerBenchmarks.simple_processor_non_constant_message  avgt    5  11.001 ± 0.052  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-preview" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LoggerBenchmarks {

  private static final Logger JUL_LOGGER;
  static {
    var logger = Logger.getLogger(Foo.class.getName());
    logger.setLevel(Level.OFF);
    JUL_LOGGER = logger;
  }

  private static final StringTemplate.Processor<Void, RuntimeException> SIMPLE_LOGGER =
      stringTemplate -> {
        JUL_LOGGER.severe(stringTemplate.interpolate());
        return null;
      };

  static class Foo {
    static void processor_logConstant() {
      LOGGER.error()."error !";
    }

    static void processor_logValue(int value) {
      LOGGER.error()."error \{ value } !";
    }

    static void simple_processor_logConstant() {
      SIMPLE_LOGGER."error !";
    }

    static void simple_processor_logValue(int value) {
      SIMPLE_LOGGER."error \{ value } !";
    }

    static void jul_logConstant() {
      JUL_LOGGER.severe("error !");
    }

    static void jul_concat_logValue(int value) {
      JUL_LOGGER.severe("error " + value + "!");
    }

    static void jul_str_logValue(int value) {
      JUL_LOGGER.severe(STR."error \{ value } !");
    }

    static void jul_lambda_logValue(int value) {
      JUL_LOGGER.severe(() -> "error " + value + "!");
    }
  }



  @Benchmark
  public void processor_constant_message() {
    Foo.processor_logConstant();
  }

  @Benchmark
  public void simple_processor_constant_message() {
    Foo.simple_processor_logConstant();
  }

  @Benchmark
  public void jul_constant_message() {
    Foo.jul_logConstant();
  }

  private int value = 42;

  @Benchmark
  public void processor_non_constant_message() {
    Foo.processor_logValue(value);
  }

  @Benchmark
  public void simple_processor_non_constant_message() {
    Foo.simple_processor_logValue(value);
  }

  @Benchmark
  public void jul_concat_non_constant_message() {
    Foo.jul_concat_logValue(value);
  }

  @Benchmark
  public void jul_str_non_constant_message() {
    Foo.jul_str_logValue(value);
  }

  @Benchmark
  public void jul_lambda_non_constant_message() {
    Foo.jul_lambda_logValue(value);
  }
}
