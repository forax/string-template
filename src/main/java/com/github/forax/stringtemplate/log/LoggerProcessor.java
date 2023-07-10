package com.github.forax.stringtemplate.log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.StringTemplate.Processor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup.ClassOption;
import java.lang.invoke.MethodType;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Level;

import static java.lang.invoke.MethodHandles.Lookup.ClassOption.NESTMATE;
import static java.lang.invoke.MethodHandles.Lookup.ClassOption.STRONG;
import static java.lang.invoke.MethodType.methodType;

public interface LoggerProcessor {
  LoggerProcessor LOGGER = create();

  private static LoggerProcessor create() {
    var lookup = MethodHandles.lookup();

    var implementationName = "/" + LoggerProcessor.class.getName().replace('.', '/') + "Impl.class";
    byte[] bytes;
    try(var inputStream = LoggerProcessor.class.getResourceAsStream(implementationName)) {
      if (inputStream == null) {
        throw new AssertionError("LoggerProcessor implementation bytecode not found");
      }
      bytes = inputStream.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    MethodHandle factory, fallback;
    try {
      var implLookup = lookup.defineHiddenClass(bytes, true, /*NESTMATE,*/ STRONG);
      var implClass = implLookup.lookupClass();

      // DEBUG
      //var implClass = lookup.defineClass(bytes);
      //var implLookup = lookup;

      factory = implLookup.findStatic(implClass, "create", methodType(LoggerProcessor.class, MethodHandle.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }

    var mh = new LoggerInliningCache().dynamicInvoker();
    try {
      return (LoggerProcessor) factory.invokeExact(mh);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  Processor<Void, RuntimeException> level(Level level);

  Processor<Void, RuntimeException> error();
  Processor<Void, RuntimeException> warning();
  Processor<Void, RuntimeException> info();
  Processor<Void, RuntimeException> debug();
  Processor<Void, RuntimeException> trace();
}
