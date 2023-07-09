package com.github.forax.stringtemplate.log;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.util.List;
import java.util.Objects;

import static java.lang.invoke.MethodHandles.exactInvoker;
import static java.lang.invoke.MethodHandles.filterArguments;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodType.methodType;

final class LoggerInliningCache extends MutableCallSite {
  static final MethodHandle IDENTITY_CHECK, LOG, FALLBACK;

  static {
    var lookup = MethodHandles.lookup();
    try {
      IDENTITY_CHECK = lookup.findStatic(LoggerInliningCache.class, "identityCheck",
          methodType(boolean.class, List.class, System.Logger.Level.class, StringTemplate.class));
      var log = lookup.findVirtual(System.Logger.class, "log",
          methodType(void.class, System.Logger.Level.class, String.class));
      var interpolate = lookup.findVirtual(StringTemplate.class, "interpolate", methodType(String.class));
      LOG = filterArguments(log, 2, interpolate);
      FALLBACK = lookup.findVirtual(LoggerInliningCache.class, "fallback",
          methodType(MethodHandle.class, System.Logger.Level.class, StringTemplate.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public LoggerInliningCache() {
    super(methodType(void.class, System.Logger.Level.class, StringTemplate.class));
    setTarget(foldArguments(exactInvoker(type()), FALLBACK.bindTo(this)));
  }

  private static boolean identityCheck(List<String> fragments, System.Logger.Level level, StringTemplate stringTemplate) {
    return stringTemplate.fragments() == fragments;
  }

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  private MethodHandle fallback(System.Logger.Level level, StringTemplate stringTemplate) {
    var callerName = STACK_WALKER.walk(frames -> frames
        .filter(frame -> frame.getDeclaringClass() != LoggerInliningCache.class)
        .map(StackWalker.StackFrame::getClassName)
        .findFirst()
        .orElseGet(LoggerProcessor.class::getName));
    var logger = System.getLogger(callerName);

    var fragments = stringTemplate.fragments();
    var target = LoggerInliningCache.LOG.bindTo(logger);
    var gwt = MethodHandles.guardWithTest(
        LoggerInliningCache.IDENTITY_CHECK.bindTo(fragments),
        target,
        new LoggerInliningCache().dynamicInvoker()
    );
    setTarget(gwt);
    return target;
  }
}
