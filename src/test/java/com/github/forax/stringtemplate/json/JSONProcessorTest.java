package com.github.forax.stringtemplate.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.github.forax.stringtemplate.json.JSONProcessor.JSON;
import static org.junit.jupiter.api.Assertions.*;

public class JSONProcessorTest {

  @Test
  public void basicObject() {
    var bob = "Bob";
    var object = JSON.object()."""
        {
          "name": \{ bob },
          "age": 12,
          "manager": false
        }
        """;
    assertAll(
        () -> assertEquals(3, object.size()),
        () -> assertEquals("Bob", object.get("name")),
        () -> assertEquals(12, object.getOrDefault("age", "default")),
        () -> assertTrue(object.containsKey("name")),
        () -> assertEquals(Map.of("name", "Bob", "age", 12, "manager", false), object)
    );
  }

  @Test
  public void basicArray() {
    var bob = "Bob";
    var array = JSON.array()."""
        [12, \{ bob }]
        """;
    assertEquals(List.of(12, "Bob"), array);
  }

  @Test
  public void empty() {
    var emptyObject = JSON."{}";
    assertTrue(emptyObject instanceof JSONObject object && object.isEmpty());

    var emptyArray = JSON."[]";
    assertTrue(emptyArray instanceof JSONArray array && array.isEmpty());
  }

  @Test
  public void loop() {
    var list = new ArrayList<JSONObject>();
    for(var i = 0; i < 10; i++) {
      list.add(JSON.object()."""
          {
            "value": 3.14,
            "index": \{ i }
          }
          """);
    }
    assertTrue(list.stream().allMatch(o -> o.get("value").equals(3.14)));
    IntStream.range(0, 10).forEach(i -> assertEquals(i, list.get(i).get("index")));
  }

  @Test
  public void fullyConstant() {
    class Foo {
      static Object data() {
        return JSON."""
          {
            "foo": 3.14
          }
          """;
      }
    }

    assertSame(Foo.data(), Foo.data());
  }

  @Test
  public void partiallyConstant() {
    class Foo {
      static JSONObject data(int value) {
        return JSON.object()."""
          {
            "foo": \{ value },
            "bar": {
              "baz": "hello"
            }
          }
          """;
      }
    }

    var data1 = Foo.data(1);
    var data2 = Foo.data(2);
    assertSame(data1.get("bar"), data2.get("bar"));
  }

  @Test
  public void stringifyObject() {
    var object = JSON.object()."""
          {
            "foo": 3.14
          }
          """;
    assertEquals("""
        {"foo":3.14}\
        """, object.stringify());
  }

  @Test
  public void stringifyArray() {
    var array = JSON.array()."""
          [true,false,null,12,-5,13.2,-7.4,"hello"]
          """;
    assertEquals("""
        [true,false,null,12,-5,13.2,-7.4,"hello"]\
        """, array.stringify());
  }
}