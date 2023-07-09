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
  //@Benchmark
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

  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801008000::invoke@21
  //                                                                      ; - java.lang.invoke.DelegatingMethodHandle$Holder::delegate@13
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101800::guard@77
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101000::invoke@51
  //                                                                      ; - java.lang.invoke.Invokers$Holder::invokeExact_MT@19
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::process@10 (line 53)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@10 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e68088:         mov     x29, x0
  //            0x0000000114e6808c:         ldr     x0, [x28, #0x1b8]
  //   0.79%    0x0000000114e68090:         ldr     x10, [x28, #0x1c8]
  //            0x0000000114e68094:         add     x11, x0, #0x18
  //            0x0000000114e68098:         cmp     x11, x10
  //            0x0000000114e6809c:         b.hs    #0x30c              ;*putfield primitives {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.runtime.Carriers$CarrierObject::&lt;init&gt;@10 (line 420)
  //                                                                      ; - java.lang.runtime.StringTemplateImpl::&lt;init&gt;@3 (line 85)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@26
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e680a0:         orr     x10, xzr, #0x1
  //            0x0000000114e680a4:         str     x11, [x28, #0x1b8]
  //   0.01%    0x0000000114e680a8:         str     x10, [x0]
  //            0x0000000114e680ac:         mov     x10, #0x10e0000     ;   {metadata(&apos;com/github/forax/stringtemplate/json/JSONObjectImpl&apos;)}
  //            0x0000000114e680b0:         movk    x10, #0x76e8
  //            0x0000000114e680b4:         str     w10, [x0, #0x8]
  //   0.31%    0x0000000114e680b8:         prfm    pstl1keep, [x11, #0x180]
  //   0.48%    0x0000000114e680bc:         str     wzr, [x0, #0x14]    ;*new {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::resolveJSON@75 (line 168)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::resolveJSON@7 (line 163)
  //                                                                      ; - java.lang.invoke.DirectMethodHandle$Holder::invokeStatic@11
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801008000::invoke@21
  //                                                                      ; - java.lang.invoke.DelegatingMethodHandle$Holder::delegate@13
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101800::guard@77
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101000::invoke@51
  //                                                                      ; - java.lang.invoke.Invokers$Holder::invokeExact_MT@19
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::process@10 (line 53)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@10 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e680c0:         mov     x12, #0xd0050000    ;   {oop(a &apos;java/util/ImmutableCollections$Map1&apos;{0x00000006802ba900})}
  //            0x0000000114e680c4:         movk    x12, #0x7520
  //            0x0000000114e680c8:         lsr     x10, x29, #3
  //   0.01%    0x0000000114e680cc:         stp     w12, w10, [x0, #0xc];*invokespecial &lt;init&gt; {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::resolveJSON@82 (line 168)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::resolveJSON@7 (line 163)
  //                                                                      ; - java.lang.invoke.DirectMethodHandle$Holder::invokeStatic@11
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801008000::invoke@21
  //                                                                      ; - java.lang.invoke.DelegatingMethodHandle$Holder::delegate@13
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101800::guard@77
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101000::invoke@51
  //                                                                      ; - java.lang.invoke.Invokers$Holder::invokeExact_MT@19
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::process@10 (line 53)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@10 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //   0.01%    0x0000000114e680d0:         dmb     ish                 ;*synchronization entry
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONObjectImpl::&lt;init&gt;@-1 (line 21)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::resolveJSON@82 (line 168)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::resolveJSON@7 (line 163)
  //                                                                      ; - java.lang.invoke.DirectMethodHandle$Holder::invokeStatic@11
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801008000::invoke@21
  //                                                                      ; - java.lang.invoke.DelegatingMethodHandle$Holder::delegate@13
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101800::guard@77
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x0000000801101000::invoke@51
  //                                                                      ; - java.lang.invoke.Invokers$Holder::invokeExact_MT@19
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONProcessorImpl::process@10 (line 53)
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@10 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //   0.77%    0x0000000114e680d4:         ldr     x12, [sp, #0x20]
  //            0x0000000114e680d8:         ldarb   w10, [x12]          ;*getfield isDone {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@30 (line 192)
  //   0.01%    0x0000000114e680dc:         ldr     x11, [x28, #0x458]
  //            0x0000000114e680e0:         ldr     x13, [sp, #0x18]
  //            0x0000000114e680e4:         add     x22, x13, #0x1      ; ImmutableOopMap {[0]=Oop [8]=Oop [16]=Oop [96]=Oop r12=Derived_oop_[96] }
  //                                                                      ;*ifeq {reexecute=1 rethrow=0 return_oop=0}
  //                                                                      ; - (reexecute) com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@33 (line 192)
  //   3.24%    0x0000000114e680e8:         ldr     wzr, [x11]          ;   {poll}
  //            0x0000000114e680ec:         cbnz    w10, #0x2e0         ;*ifeq {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@33 (line 192)
  //            0x0000000114e680f0:         ldr     x19, [sp, #0x60]
  //            0x0000000114e680f4:         ldr     x20, [sp]
  //            0x0000000114e680f8:         ldr     x0, [x28, #0x1b8]
  //   1.15%    0x0000000114e680fc:         ldr     x10, [x28, #0x1c8]
  //            0x0000000114e68100:         add     x11, x0, #0x20
  //            0x0000000114e68104:         cmp     x11, x10
  //   0.42%    0x0000000114e68108:         b.hs    #0x260              ;*putfield primitives {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.runtime.Carriers$CarrierObject::&lt;init&gt;@10 (line 420)
  //                                                                      ; - java.lang.runtime.StringTemplateImpl::&lt;init&gt;@3 (line 85)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@26
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e6810c:         orr     x10, xzr, #0x1
  //            0x0000000114e68110:         str     x11, [x28, #0x1b8]
  //            0x0000000114e68114:         str     x10, [x0]
  //            0x0000000114e68118:         mov     x10, #0x1030000     ;   {metadata(&apos;java/lang/runtime/StringTemplateImpl&apos;)}
  //            0x0000000114e6811c:         movk    x10, #0x9660
  //            0x0000000114e68120:         prfm    pstl1keep, [x11, #0x180]
  //   0.28%    0x0000000114e68124:         str     w10, [x0, #0x8]
  //            0x0000000114e68128:         add     x10, x0, #0x10
  //            0x0000000114e6812c:         str     wzr, [x0, #0xc]
  //            0x0000000114e68130:         stp     xzr, xzr, [x10]     ;*invokevirtual allocateInstance {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.invoke.DirectMethodHandle::allocateInstance@12 (line 501)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@1
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e68134:         mov     x14, x0
  //   0.01%    0x0000000114e68138:         ldr     x0, [x28, #0x1b8]   ;*putfield primitives {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.runtime.Carriers$CarrierObject::&lt;init&gt;@10 (line 420)
  //                                                                      ; - java.lang.runtime.StringTemplateImpl::&lt;init&gt;@3 (line 85)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@26
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //   2.78%    0x0000000114e6813c:         ldr     x10, [x28, #0x1c8]
  //            0x0000000114e68140:         add     x11, x0, #0x18      ;*invokevirtual allocateInstance {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.invoke.DirectMethodHandle::allocateInstance@12 (line 501)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@1
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e68144:         cmp     x11, x10
  //   0.46%    0x0000000114e68148:         b.hs    #0x1dc              ;*putfield primitives {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.runtime.Carriers$CarrierObject::&lt;init&gt;@10 (line 420)
  //                                                                      ; - java.lang.runtime.StringTemplateImpl::&lt;init&gt;@3 (line 85)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@26
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //            0x0000000114e6814c:         orr     x10, xzr, #0x1
  //            0x0000000114e68150:         str     x11, [x28, #0x1b8]
  //            0x0000000114e68154:         movz    x13, #0x0, lsl #16  ;   {metadata({type array long})}
  //            0x0000000114e68158:         movk    x13, #0x2a80
  //            0x0000000114e6815c:         str     x10, [x0]
  //   0.02%    0x0000000114e68160:         str     w13, [x0, #0x8]
  //            0x0000000114e68164:         orr     w10, wzr, #0x1
  //            0x0000000114e68168:         prfm    pstl1keep, [x11, #0x180]
  //            0x0000000114e6816c:         str     w10, [x0, #0xc]
  //   0.01%    0x0000000114e68170:         prfm    pstl1keep, [x11, #0x200]
  //            0x0000000114e68174:         str     xzr, [x0, #0x10]
  //   1.59%    0x0000000114e68178:         prfm    pstl1keep, [x11, #0x280]
  //   0.01%    0x0000000114e6817c:         dmb     ishst               ;*newarray {reexecute=0 rethrow=0 return_oop=0}
  //                                                                      ; - java.lang.runtime.Carriers$CarrierObject::createPrimitivesArray@9 (line 432)
  //                                                                      ; - java.lang.runtime.Carriers$CarrierObject::&lt;init&gt;@7 (line 420)
  //                                                                      ; - java.lang.runtime.StringTemplateImpl::&lt;init&gt;@3 (line 85)
  //                                                                      ; - java.lang.invoke.LambdaForm$DMH/0x00000008010d4000::newInvokeSpecial@26
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010dcc00::invoke@50
  //                                                                      ; - java.lang.invoke.LambdaForm$MH/0x00000008010ddc00::invoke@14
  //                                                                      ; - java.lang.invoke.Invokers$Holder::linkToTargetMethod@5
  //                                                                      ; - com.github.forax.stringtemplate.json.JSONBenchmarks::bare_non_constant@5 (line 152)
  //                                                                      ; - com.github.forax.stringtemplate.json.jmh_generated.JSONBenchmarks_bare_non_constant_jmhTest::bare_non_constant_avgt_jmhStub@17 (line 190)
  //   0.05%    0x0000000114e68180:         mov     x10, x14
  //            0x0000000114e68184:         mov     x11, x0
  //            0x0000000114e68188:         eor     x11, x11, x10
  //            0x0000000114e6818c:         lsr     x13, x0, #3
  //            0x0000000114e68190:         lsr     x11, x11, #22
  //   1.55%    0x0000000114e68194:         str     w13, [x14, #0xc]
  //            0x0000000114e68198:         cbz     x11, #-0x288
  //            0x0000000114e6819c:         lsr     x10, x10, #9
  //            0x0000000114e681a0:         mov     x11, #0x8000
  //            0x0000000114e681a4:         movk    x11, #0x19cf, lsl #16
  //            0x0000000114e681a8:         movk    x11, #0x1, lsl #32
  //            0x0000000114e681ac:         add     x0, x11, x10
  //            0x0000000114e681b0:         ldrsb   w11, [x0]
  //            0x0000000114e681b4:         cmp     w11, #0x2
  //            0x0000000114e681b8:         b.eq    #-0x2a8
  //            0x0000000114e681bc:         ldr     x10, [x28, #0x48]

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
