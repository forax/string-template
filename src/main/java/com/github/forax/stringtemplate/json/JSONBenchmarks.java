package com.github.forax.stringtemplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.forax.stringtemplate.json.JSONProcessor.JSON;

// Benchmark                                    Mode  Cnt    Score    Error  Units
// JSONBenchmarks.bare_constant                 avgt    5    0.339 ±  0.207  ns/op
// JSONBenchmarks.constant                      avgt    5    9.862 ±  0.025  ns/op
// JSONBenchmarks.constant_map                  avgt    5   22.573 ±  0.037  ns/op
// JSONBenchmarks.constant_mapper_readTree      avgt    5  217.484 ±  0.262  ns/op
// JSONBenchmarks.constant_mapper_record        avgt    5  231.580 ± 34.225  ns/op
// JSONBenchmarks.bare_non_constant             avgt    5   19.227 ±  0.050  ns/op
// JSONBenchmarks.non_constant                  avgt    5   27.632 ±  0.047  ns/op
// JSONBenchmarks.non_constant_map              avgt    5   22.533 ±  0.039  ns/op
// JSONBenchmarks.non_constant_mapper_readTree  avgt    5  219.460 ±  0.370  ns/op
// JSONBenchmarks.non_constant_mapper_record    avgt    5  222.057 ±  0.823  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-preview" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class JSONBenchmarks {
  @Benchmark
  public Object bare_constant() {
    return JSON."""
        {
          "dog": {
              "name": "Scooby",
              "age": 7
          }    
        }
        """;
  }

  //@Benchmark
  public int constant() {
    var object = JSON.object()."""
        {
          "dog": {
              "name": "Scooby",
              "age": 7
          }    
        }
        """;
    return object.object("dog").getInt("age");
  }

  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@13 (line 40)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //            0x0000000130b36330:         mov     w12, wzr            ;*ifnonnull {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.util.ImmutableCollections$MapN::probe@25 (line 1332)
  //                                                                      ; - java.util.ImmutableCollections$MapN::get@16 (line 1242)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::getOrDefault@5 (line 80)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::get@3 (line 146)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObject::getInt@2 (line 23)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@32 (line 48)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //   1.29%    0x0000000130b36334:         mov     x15, #0x6258        ;   {oop(&quot;age&quot;{0x00000006802a6258})}
  //            0x0000000130b36338:         movk    x15, #0x802a, lsl #16
  //   0.19%    0x0000000130b3633c:         movk    x15, #0x6, lsl #32
  //   3.52%    0x0000000130b36340:         cmp     x13, x15
  //   0.28%    0x0000000130b36344:         b.eq    #0xbc
  //   1.69%    0x0000000130b36348:         ldr     w16, [x13, #0x8]
  //            0x0000000130b3634c:         movz    x15, #0x0, lsl #16  ;   {metadata(&apos;java/lang/String&apos;)}
  //            0x0000000130b36350:         movk    x15, #0xe8d8
  //            0x0000000130b36354:         cmp     w16, w15            ;*invokevirtual invokeBasic {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.invoke.Invokers$Holder::invokeExact_MT@19
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::process@10 (line 53)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessor::lambda$object$0@2 (line 11)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessor$$Lambda/0x00000008010cf398::process@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@13 (line 40)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //            0x0000000130b36358:         b.ne    #0x198c             ;*checkcast {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.String::equals@15 (line 1861)
  //                                                                      ; - java.util.ImmutableCollections$MapN::probe@35 (line 1334)
  //                                                                      ; - java.util.ImmutableCollections$MapN::get@16 (line 1242)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::getOrDefault@5 (line 80)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::get@3 (line 146)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObject::getInt@2 (line 23)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@32 (line 48)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //   0.01%    0x0000000130b3635c:         ldr     w16, [x13, #0x14]   ;*getfield value {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.String::equals@41 (line 1861)
  //                                                                      ; - java.util.ImmutableCollections$MapN::probe@35 (line 1334)
  //                                                                      ; - java.util.ImmutableCollections$MapN::get@16 (line 1242)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::getOrDefault@5 (line 80)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::get@3 (line 146)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObject::getInt@2 (line 23)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@32 (line 48)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //   1.43%    0x0000000130b36360:         ldrsb   w13, [x13, #0x10]   ;*getfield coder {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.String::equals@30 (line 1861)
  //                                                                      ; - java.util.ImmutableCollections$MapN::probe@35 (line 1334)
  //                                                                      ; - java.util.ImmutableCollections$MapN::get@16 (line 1242)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::getOrDefault@5 (line 80)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::get@3 (line 146)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObject::getInt@2 (line 23)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@32 (line 48)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //            0x0000000130b36364:         lsl     x15, x16, #3        ;*getfield value {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.String::equals@41 (line 1861)
  //                                                                      ; - java.util.ImmutableCollections$MapN::probe@35 (line 1334)
  //                                                                      ; - java.util.ImmutableCollections$MapN::get@16 (line 1242)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::getOrDefault@5 (line 80)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::get@3 (line 146)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObject::getInt@2 (line 23)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::constant@32 (line 48)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_constant_jmhTest::constant_avgt_jmhStub@17 (line 190)
  //            0x0000000130b36368:         cbnz    w13, #0xcd4
  //   1.65%    0x0000000130b3636c:         ldr     w13, [x15, #0xc]
  //            0x0000000130b36370:         cmp     w13, #0x3
  //   0.07%    0x0000000130b36374:         b.ne    #-0x74
  //            0x0000000130b36378:         orr     w4, wzr, #0x3
  //            0x0000000130b3637c:         add     x3, x15, #0x10
  //            0x0000000130b36380:         ldr     x1, [sp, #0x20]
  //            0x0000000130b36384:         mov     x0, #0x0
  //            0x0000000130b36388:         subs    x4, x4, #0x8
  //            0x0000000130b3638c:         b.lt    #0x30
  //            0x0000000130b36390:         ldr     x8, [x1], #0x8
  //            0x0000000130b36394:         ldr     x9, [x3], #0x8
  //            0x0000000130b36398:         subs    x4, x4, #0x8
  //            0x0000000130b3639c:         eor     x8, x8, x9

  //@Benchmark
  public int constant_map() {
    var map = Map.of("dog", Map.of("name", "Scooby", "age", 7));
    return (int) ((Map<String, Object>)(Map<?,?>) map.get("dog")).get("age");
  }


  final int age = 7;

  @Benchmark
  public Object bare_non_constant() {
    return JSON."""
        {
          "dog": {
              "name": "Scooby",
              "age": \{ age }
          }
        }
        """;
  }

  //@Benchmark
  public int non_constant() {
    var object = JSON.object()."""
        {
          "dog": {
              "name": "Scooby",
              "age": \{ age }
          }
        }
        """;
    return object.object("dog").getInt("age");
  }

  //@Benchmark
  public int non_constant_map() {
    var map = Map.of("dog", Map.of("name", "Scooby", "age", age));
    return (int) ((Map<String, Object>)(Map<?,?>) map.get("dog")).get("age");
  }


  private static final ObjectMapper MAPPER = new ObjectMapper();

  //@Benchmark
  public int constant_mapper_readTree() throws JsonProcessingException {
    var object = MAPPER.readTree("""
        {
          "dog": {
              "name": "Scooby",
              "age": 7
          }
        }
        """);
    return object.get("dog").get("age").asInt();
  }

  //@Benchmark
  public int non_constant_mapper_readTree() throws JsonProcessingException {
    var object = MAPPER.readTree(STR."""
        {
          "dog": {
              "name": "Scooby",
              "age": \{ age }
          }
        }
        """);
    return object.get("dog").get("age").asInt();
  }


  record Dog(String name, int age) {}
  record Universe(Dog dog) {}

  //@Benchmark
  public int constant_mapper_record() throws JsonProcessingException {
    var object = MAPPER.readValue("""
        {
          "dog": {
              "name": "Scooby",
              "age": 7
          }
        }
        """, Universe.class);
    return object.dog.age;
  }

  //@Benchmark
  public int non_constant_mapper_record() throws JsonProcessingException {
    var object = MAPPER.readValue(STR."""
        {
          "dog": {
              "name": "Scooby",
              "age": \{ age }
          }
        }
        """, Universe.class);
    return object.dog.age;
  }
}
