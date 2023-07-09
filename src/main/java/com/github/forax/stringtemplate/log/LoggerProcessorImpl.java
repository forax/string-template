package com.github.forax.stringtemplate.log;

import java.lang.StringTemplate.Processor;
import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandle;
import java.util.Objects;

// should be loaded as a hidden class
record LoggerProcessorImpl(MethodHandle mh, Level level) implements LoggerProcessor, Processor<Void, RuntimeException> {
  static LoggerProcessor create(MethodHandle mh) {
    return new LoggerProcessorImpl(mh, null);
  }

  private static Processor<Void, RuntimeException> createAsProcessor(MethodHandle mh, Level level) {
    return new LoggerProcessorImpl(mh, level);
  }

  @Override
  public boolean equals(Object obj) {  // invokedynamic does not support hidden classes
    throw new UnsupportedOperationException();
  }
  @Override
  public int hashCode() {  // invokedynamic does not support hidden classes
    throw new UnsupportedOperationException();
  }
  @Override
  public String toString() {  // invokedynamic does not support hidden classes
    throw new UnsupportedOperationException();
  }

  @Override
  public Void process(StringTemplate stringTemplate) {
    Objects.requireNonNull(stringTemplate);
    try {
      mh.invokeExact(level, stringTemplate);
    } catch (Throwable t) {
      throw rethrow(t);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T rethrow(Throwable t) throws T {
    throw (T) t;
  }

  @Override
  public Processor<Void, RuntimeException> level(Level level) {
    return createAsProcessor(mh, level);
  }
  @Override
  public Processor<Void, RuntimeException> error() {
    return createAsProcessor(mh, Level.ERROR);
  }
  @Override
  public Processor<Void, RuntimeException> warning() {
    return createAsProcessor(mh, Level.WARNING);
  }
  @Override
  public Processor<Void, RuntimeException> info() {
    return createAsProcessor(mh, Level.INFO);
  }
  @Override
  public Processor<Void, RuntimeException> debug() {
    return createAsProcessor(mh, Level.DEBUG);
  }
  @Override
  public Processor<Void, RuntimeException> trace() {
    return createAsProcessor(mh, Level.TRACE);
  }
}
