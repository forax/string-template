package com.github.forax.stringtemplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.forax.stringtemplate.json.JSONProcessorImpl.Schema;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

record JSONArrayImpl(List<Schema> schemas, List<Object> values) implements JSONArray {
  public int size() {
    return schemas.size();
  }
  @Override
  public boolean isEmpty() {
    return schemas.isEmpty();
  }

  public boolean equals(Object o) {
    if (!(o instanceof List<?> list)) {
      return false;
    }
    var size = schemas.size();
    if (size != list.size()) {
      return false;
    }
    var it = list.iterator();
    for(var i = 0; i < size; i++) {
      if (!it.hasNext()) {
        return false;
      }
      if (!Objects.equals(it.next(), get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    var size = schemas.size();
    var hashCode = 1;
    for (var i = 0; i < size; i++) {
      var element = get(i);
      hashCode = 31 * hashCode + Objects.hashCode(element);
    }
    return hashCode;
  }

  @Override
  public String toString() {
    var joiner = new StringJoiner(", ", "[", "]");
    var size = schemas.size();
    for (var i = 0; i < size; i++) {
      var element = get(i);
      joiner.add(String.valueOf(element));
    }
    return joiner.toString();
  }

  @Override
  public Object get(int index) {
    var schema = schemas.get(index);
    return JSONProcessorImpl.resolveJSON(schema, values);
  }

  @Override
  public boolean contains(Object o) {
    if (o == null) {
      return containsNull();
    }
    var size = schemas.size();
    for(var i = 0; i < size; i++) {
      if (o.equals(get(i))) {
        return true;
      }
    }
    return false;
  }

  private boolean containsNull() {
    var size = schemas.size();
    for(var i = 0; i < size; i++) {
      if (get(i) == null) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Iterator<Object> iterator() {
    var size = schemas.size();
    return new Iterator<>() {
      private int index;

      @Override
      public boolean hasNext() {
        return index < size;
      }

      @Override
      public Object next() {
        if (index >= size) {
          throw new NoSuchElementException();
        }
        return get(index++);
      }
    };
  }

  @Override
  public void forEach(Consumer<? super Object> action) {
    var size = schemas.size();
    for(var i = 0; i < size; i++) {
      action.accept(get(i));
    }
  }

  @Override
  public Object[] toArray() {
    var size = schemas.size();
    var array = new Object[size];
    for(var i = 0; i < size; i++) {
      array[i] = get(i);
    }
    return array;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    var size = schemas.size();
    var array = a.length < size ? Arrays.copyOf(a, size): a;
    for(var i = 0; i < size; i++) {
      array[i] = (T) get(i);  // should raise ArrayStoreException if mismatch
    }
    if (a.length > size) {
      array[size] = null;
    }
    return array;
  }

  @Override
  public <T> T[] toArray(IntFunction<T[]> generator) {
    var size = schemas.size();
    var array = generator.apply(size);
    for(var i = 0; i < size; i++) {
      array[i] = (T) get(i);  // should raise ArrayStoreException if mismatch
    }
    return array;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (var element : c) {
      if (!contains(element)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int indexOf(Object o) {
    var size = schemas.size();
    for(var i = 0; i < size; i++) {
      if (o.equals(get(i))) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    var size = schemas.size();
    for(var i = size; --i >= 0;) {
      if (o.equals(get(i))) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public ListIterator<Object> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<Object> listIterator(int start) {
    var size = schemas.size();
    if (start < 0 || start > size) {
      throw new IndexOutOfBoundsException();
    }
    return new ListIterator<>() {
      private int index = start;

      @Override
      public boolean hasNext() {
        return index < size;
      }

      @Override
      public Object next() {
        if (index >= size) {
          throw new NoSuchElementException();
        }
        return get(index++);
      }

      public Object previous() {
        if (index <= 0) {
          throw new NoSuchElementException();
        }
        return get(--index);
      }

      public boolean hasPrevious() {
        return index != 0;
      }

      public int nextIndex() {
        return index;
      }

      public int previousIndex() {
        return index - 1;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
      @Override
      public void set(Object o) {
        throw new UnsupportedOperationException();
      }
      @Override
      public void add(Object o) {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public Object getFirst() {
    if (schemas.isEmpty()) {
      throw new NoSuchElementException();
    }
    return get(0);
  }

  @Override
  public Object getLast() {
    var size = schemas.size();
    if (size == 0) {
      throw new NoSuchElementException();
    }
    return get(size - 1);
  }

  @Override
  public List<Object> subList(int fromIndex, int toIndex) {
    return List.copyOf(this).subList(fromIndex, toIndex);
  }

  private final class ArraySpliterator implements Spliterator<Object> {
    private int index;
    private final int end;

    private ArraySpliterator(int start, int end) {
      this.index = start;
      this.end = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Object> action) {
      if (index == end) {
        return false;
      }
      action.accept(get(index));
      return ++index == end;
    }

    @Override
    public Spliterator<Object> trySplit() {
      if (end - index < 1024) {
        return null;
      }
      var middle = (index + end) >>> 1;
      var spliterator = new ArraySpliterator(index, middle);
      index = middle;
      return spliterator;
    }

    @Override
    public long estimateSize() {
      return end - index;
    }

    @Override
    public int characteristics() {
      return SIZED | SUBSIZED | IMMUTABLE | ORDERED;
    }
  }

  @Override
  public Spliterator<Object> spliterator() {
    var size = schemas.size();
    return new ArraySpliterator(0, size);
  }

  @Override
  public Stream<Object> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public Stream<Object> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
  }


  @Override
  public JSONObject object(int index) {
    return (JSONObject) get(index);
  }

  @Override
  public JSONArray array(int index) {
    return (JSONArray) get(index);
  }

  @Override
  public boolean getBoolean(int index) {
    return (boolean) get(index);
  }
  @Override
  public int getInt(int index) {
    return (int) get(index);
  }
  @Override
  public long getLong(int index) {
    return (long) get(index);
  }
  @Override
  public double getDouble(int index) {
    return (double) get(index);
  }
  @Override
  public BigInteger getBigInteger(int index) {
    return (BigInteger) get(index);
  }
  @Override
  public BigDecimal getBigDecimal(int index) {
    return (BigDecimal) get(index);
  }
  @Override
  public String getString(int index) {
    return (String) get(index);
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
  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean addAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean addAll(int index, Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean removeIf(Predicate<? super Object> filter) {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void replaceAll(UnaryOperator<Object> operator) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void sort(Comparator<? super Object> c) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object set(int index, Object element) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void add(int index, Object element) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object remove(int index) {
    throw new UnsupportedOperationException();
  }
  @Override
  public void addFirst(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addLast(Object o) {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object removeFirst() {
    throw new UnsupportedOperationException();
  }
  @Override
  public Object removeLast() {
    throw new UnsupportedOperationException();
  }
}
