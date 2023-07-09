package com.github.forax.stringtemplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.forax.stringtemplate.json.JSONProcessorImpl.Schema;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

record JSONObjectImpl(Map<String, Schema> schemaMap, List<Object> valueList) implements JSONObject {
  @Override
  public int size() {
    return schemaMap.size();
  }

  @Override
  public boolean isEmpty() {
    return schemaMap.isEmpty();
  }

  public boolean equals(Object o) {
    if (!(o instanceof Map<?, ?> map)) {
      return false;
    }
    var size = schemaMap.size();
    if (size != map.size()) {
      return false;
    }
    for (var entry : schemaMap.entrySet()) {
      var key = entry.getKey();
      var schema = entry.getValue();
      var value = JSONProcessorImpl.resolveJSON(schema, valueList);
      if (value == null) {
        if (!(map.get(key) == null && map.containsKey(key))) {
          return false;
        }
      } else {
        if (!value.equals(map.get(key))) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    for (var entry : schemaMap.entrySet()) {
      var key = entry.getKey();
      var schema = entry.getValue();
      var value = JSONProcessorImpl.resolveJSON(schema, valueList);
      var hash = (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
      hashCode += hash;
    }
    return hashCode;
  }

  @Override
  public String toString() {
    var joiner = new StringJoiner(", ", "{", "}");
    for (var entry : schemaMap.entrySet()) {
      var key = entry.getKey();
      var schema = entry.getValue();
      var value = JSONProcessorImpl.resolveJSON(schema, valueList);
      joiner.add(key + "=" + value);
    }
    return joiner.toString();
  }

  @Override
  public Object getOrDefault(Object key, Object defaultValue) {
    var schema = schemaMap.get(key);
    if (schema == null) {
      return defaultValue;
    }
    return JSONProcessorImpl.resolveJSON(schema, valueList);
  }

  @Override
  public Object get(Object key) {
    return getOrDefault(key, null);
  }

  @Override
  public boolean containsKey(Object key) {
    return schemaMap.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    var schemaMap = this.schemaMap;
    return new AbstractSet<>() {
      @Override
      public int size() {
        return schemaMap.size();
      }

      @Override
      public Iterator<Entry<String, Object>> iterator() {
        var it = schemaMap.entrySet().iterator();
        return new Iterator<>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Entry<String, Object> next() {
            var entry = it.next();
            var key = entry.getKey();
            var schema = entry.getValue();
            return Map.entry(key, JSONProcessorImpl.resolveJSON(schema, valueList));
          }
        };
      }
    };
  }

  @Override
  public Set<String> keySet() {
    var schemaMap = this.schemaMap;
    return new AbstractSet<>() {
      @Override
      public int size() {
        return schemaMap.size();
      }

      @Override
      public boolean contains(Object o) {
        return containsKey(o);
      }

      @Override
      public Iterator<String> iterator() {
        var it = schemaMap.keySet().iterator();
        return new Iterator<>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public String next() {
            return it.next();
          }
        };
      }
    };
  }

  @Override
  public Collection<Object> values() {
    var schemaMap = this.schemaMap;
    return new AbstractCollection<>() {
      @Override
      public int size() {
        return schemaMap.size();
      }

      @Override
      public Iterator<Object> iterator() {
        var it = schemaMap.values().iterator();
        return new Iterator<>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Object next() {
            var schema = it.next();
            return JSONProcessorImpl.resolveJSON(schema, valueList);
          }
        };
      }
    };
  }

  @Override
  public JSONObject object(String key) {
    return (JSONObject) get(key);
  }
  @Override
  public JSONArray array(String key) {
    return (JSONArray) get(key);
  }
  @Override
  public boolean getBoolean(String key) {
    return (boolean) get(key);
  }
  @Override
  public int getInt(String key) {
    return (int) get(key);
  }
  @Override
  public long getLong(String key) {
    return (long) get(key);
  }
  @Override
  public double getDouble(String key) {
    return (double) get(key);
  }
  @Override
  public BigInteger getBigInteger(String key) {
    return (BigInteger) get(key);
  }
  @Override
  public BigDecimal getBigDecimal(String key) {
    return (BigDecimal) get(key);
  }
  @Override
  public String getString(String key) {
    return (String) get(key);
  }

  @Override
  public String stringify() {
    try {
      return JSONProcessorImpl.MAPPER.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super Object> action) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object putIfAbsent(String key, Object value) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean remove(Object key, Object value) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean replace(String key, Object oldValue, Object newValue) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object replace(String key, Object value) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object put(String key, Object value) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void putAll(Map<? extends String, ?> m) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
