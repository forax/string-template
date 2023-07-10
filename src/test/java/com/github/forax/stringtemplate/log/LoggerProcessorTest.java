package com.github.forax.stringtemplate.log;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.forax.stringtemplate.log.LoggerProcessor.LOGGER;
import static org.junit.jupiter.api.Assertions.*;

public class LoggerProcessorTest {

  @Test
  public void level() {
    class Foo {
      static void test() {
        LOGGER.level(Level.WARNING)."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      assertAll(
          () -> assertEquals(java.util.logging.Level.WARNING, record.getLevel()),
          () -> assertEquals(Foo.class.getName(), record.getLoggerName()),
          () -> assertEquals("hello", record.getMessage()),
          () -> assertEquals(Foo.class.getName(), record.getSourceClassName()),
          () -> assertEquals("test", record.getSourceMethodName())
      );
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.WARNING);
    Foo.test();
    assertTrue(box.seen);
  }

  @Test
  public void levelDisabled() {
    class Foo {
      static void test() {
        LOGGER.level(Level.WARNING)."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.OFF);
    Foo.test();
    assertFalse(box.seen);
  }

  @Test
  public void error() {
    class Foo {
      static void test() {
        LOGGER.error()."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      assertAll(
          () -> assertEquals(java.util.logging.Level.SEVERE, record.getLevel()),
          () -> assertEquals(Foo.class.getName(), record.getLoggerName()),
          () -> assertEquals("hello", record.getMessage()),
          () -> assertEquals(Foo.class.getName(), record.getSourceClassName()),
          () -> assertEquals("test", record.getSourceMethodName())
      );
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.SEVERE);
    Foo.test();
    assertTrue(box.seen);
  }

  @Test
  public void warning() {
    class Foo {
      static void test() {
        LOGGER.warning()."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      assertAll(
          () -> assertEquals(java.util.logging.Level.WARNING, record.getLevel()),
          () -> assertEquals(Foo.class.getName(), record.getLoggerName()),
          () -> assertEquals("hello", record.getMessage()),
          () -> assertEquals(Foo.class.getName(), record.getSourceClassName()),
          () -> assertEquals("test", record.getSourceMethodName())
      );
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.WARNING);
    Foo.test();
    assertTrue(box.seen);
  }

  @Test
  public void info() {
    class Foo {
      static void test() {
        LOGGER.info()."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      assertAll(
          () -> assertEquals(java.util.logging.Level.INFO, record.getLevel()),
          () -> assertEquals(Foo.class.getName(), record.getLoggerName()),
          () -> assertEquals("hello", record.getMessage()),
          () -> assertEquals(Foo.class.getName(), record.getSourceClassName()),
          () -> assertEquals("test", record.getSourceMethodName())
      );
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.INFO);
    Foo.test();
    assertTrue(box.seen);
  }

  @Test
  public void debug() {
    class Foo {
      static void test() {
        LOGGER.debug()."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      assertAll(
          () -> assertEquals(java.util.logging.Level.FINE, record.getLevel()),
          () -> assertEquals(Foo.class.getName(), record.getLoggerName()),
          () -> assertEquals("hello", record.getMessage()),
          () -> assertEquals(Foo.class.getName(), record.getSourceClassName()),
          () -> assertEquals("test", record.getSourceMethodName())
      );
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.FINE);
    Foo.test();
    assertTrue(box.seen);
  }

  @Test
  public void trace() {
    class Foo {
      static void test() {
        LOGGER.trace()."hello";
      }
    }

    var logger = Logger.getLogger(Foo.class.getName());
    logger.setUseParentHandlers(false);
    var box = new Object() { boolean seen; };
    logger.setFilter(record -> {
      assertAll(
          () -> assertEquals(java.util.logging.Level.FINER, record.getLevel()),
          () -> assertEquals(Foo.class.getName(), record.getLoggerName()),
          () -> assertEquals("hello", record.getMessage()),
          () -> assertEquals(Foo.class.getName(), record.getSourceClassName()),
          () -> assertEquals("test", record.getSourceMethodName())
      );
      box.seen = true;
      return true;
    });
    logger.setLevel(java.util.logging.Level.FINER);
    Foo.test();
    assertTrue(box.seen);
  }
}