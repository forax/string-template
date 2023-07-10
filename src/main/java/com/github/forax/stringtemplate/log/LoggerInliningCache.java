package com.github.forax.stringtemplate.log;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.invoke.MethodHandles.exactInvoker;
import static java.lang.invoke.MethodHandles.filterArguments;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodHandles.guardWithTest;
import static java.lang.invoke.MethodType.methodType;

final class LoggerInliningCache extends MutableCallSite {
  static final MethodHandle FRAGMENT_CHECK, LEVEL_CHECK, LOG, EMPTY, FALLBACK;

  static {
    var lookup = MethodHandles.lookup();
    try {
      FRAGMENT_CHECK = lookup.findStatic(LoggerInliningCache.class, "fragmentCheck",
          methodType(boolean.class, List.class, Level.class, StringTemplate.class));
      LEVEL_CHECK = lookup.findVirtual(Logger.class, "isLoggable",
          methodType(boolean.class, Level.class));
      var log = lookup.findVirtual(Logger.class, "log",
          methodType(void.class, Level.class, String.class));
      var interpolate = lookup.findVirtual(StringTemplate.class, "interpolate", methodType(String.class));
      LOG = filterArguments(log, 2, interpolate);
      EMPTY = MethodHandles.empty(methodType(void.class, Level.class, StringTemplate.class));
      FALLBACK = lookup.findVirtual(LoggerInliningCache.class, "fallback",
          methodType(MethodHandle.class, Level.class, StringTemplate.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public LoggerInliningCache() {
    super(methodType(void.class, Level.class, StringTemplate.class));
    setTarget(foldArguments(exactInvoker(type()), FALLBACK.bindTo(this)));
  }

  private static boolean fragmentCheck(List<String> fragments, Level level, StringTemplate stringTemplate) {
    return stringTemplate.fragments() == fragments;
  }

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  private MethodHandle fallback(Level level, StringTemplate stringTemplate) {
    var callerName = STACK_WALKER.walk(frames -> frames
        .filter(frame -> frame.getDeclaringClass() != LoggerInliningCache.class)
        .map(StackWalker.StackFrame::getClassName)
        .findFirst()
        .orElseGet(LoggerProcessor.class::getName));
    var logger = Logger.getLogger(callerName);

    var fragments = stringTemplate.fragments();
    var target = LOG.bindTo(logger);
    var gwt = guardWithTest(
        FRAGMENT_CHECK.bindTo(fragments),
        guardWithTest(
            LEVEL_CHECK.bindTo(logger),
            target,
            EMPTY),
        new LoggerInliningCache().dynamicInvoker()
    );
    setTarget(gwt);
    return target;
  }
}
