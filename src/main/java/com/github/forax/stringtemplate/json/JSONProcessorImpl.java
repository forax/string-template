package com.github.forax.stringtemplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterators;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodHandles.dropArguments;
import static java.lang.invoke.MethodHandles.exactInvoker;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodType.methodType;
import static java.util.stream.Collectors.toMap;

record JSONProcessorImpl(MethodHandle jsonMH) implements JSONProcessor {
  sealed interface Schema {}
  private record ObjectSchema(Map<String, Schema> schemaMap) implements Schema {
    private ObjectSchema {
      schemaMap = Map.copyOf(schemaMap);  // does not keep the order
                                          // otherwise, it means loosing performance
    }
  }
  private record ArraySchema(List<Schema> schemas) implements Schema {
    private ArraySchema {
      schemas = List.copyOf(schemas);
    }
  }
  private record ValueSchema(int slot) implements Schema {}
  private record ConstantSchema(Object constant) implements Schema {}

  @Override
  public Object process(StringTemplate stringTemplate) {
    Objects.requireNonNull(stringTemplate);
    try {
      return jsonMH.invokeExact(stringTemplate);
    } catch(Throwable t) {
      throw rethrow(t);
    }
  }

  @Override
  public StringTemplate.Processor<JSONObject, RuntimeException> object() {
    return stringTemplate -> (JSONObject) process(stringTemplate);
  }

  @Override
  public StringTemplate.Processor<JSONArray, RuntimeException> array() {
    return stringTemplate -> (JSONArray) process(stringTemplate);
  }

  static JSONProcessor createProcessor() {
    var jsonMH = new InliningCache().dynamicInvoker();
    return new JSONProcessorImpl(jsonMH);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> T rethrow(Throwable t) throws T {
    throw (T) t;
  }

  static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String HOLE = STR."\{ UUID.randomUUID() }";

  private static Schema createSchema(List<String> fragments, Function<Schema, Object> resolver) {
    var holes = Collections.nCopies(fragments.size() - 1, "\"" + HOLE + "\"");
    var template = StringTemplate.of(fragments, holes);
    var jsonText = template.interpolate();
    JsonNode node;
    try {
      node = MAPPER.readTree(jsonText);   // TODO, use Jackson stream API instead
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
    return new SchemaFactory().createSchema(node, resolver);
  }

  private static final class SchemaFactory {
    private int count;

    private boolean allConstants(Collection<Schema> collection) {
      return collection.stream().allMatch(schema -> schema instanceof ConstantSchema);
    }

    public Schema createSchema(JsonNode node, Function<Schema, Object> resolver) {
      return switch (node.getNodeType()) {
        case OBJECT -> {
          var properties = node.properties();
          var schemaMap = properties.stream().collect(toMap(Map.Entry::getKey, e -> createSchema(e.getValue(), resolver)));
          var schema = new ObjectSchema(schemaMap);
          if (allConstants(schemaMap.values())) {  // precompute if all schemas are constant
            var result = resolver.apply(schema);
            yield new ConstantSchema(result);
          }
          yield schema;
        }
        case ARRAY -> {
          var schemas = StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.elements(), 0), false)
              .map(n -> createSchema(n, resolver))
              .toList();
          var schema = new ArraySchema(schemas);
          if (allConstants(schemas)) {  // precompute if all schemas are constant
            var result = resolver.apply(schema);
            yield new ConstantSchema(result);
          }
          yield schema;
        }
        case STRING -> {
          var text = node.asText();
          if (text.equals(HOLE)) {
            yield new ValueSchema(count++);
          }
          yield new ConstantSchema(text);
        }
        case BOOLEAN -> new ConstantSchema(node.asBoolean());
        case NULL -> new ConstantSchema(null);
        case NUMBER -> {
          if (node.isInt()) {
            yield new ConstantSchema(node.asInt());
          }
          if (node.isLong()) {
            yield new ConstantSchema(node.asLong());
          }
          if (node.isBigInteger()) {
            yield new ConstantSchema(new BigInteger(node.asText()));
          }
          if (node.isBigDecimal()) {
            yield new ConstantSchema(new BigDecimal(node.asText()));
          }
          yield new ConstantSchema(node.asDouble());
        }
        case BINARY, MISSING, POJO -> throw new AssertionError("NYI");
      };
    }
  }

  private static Object resolveJSON(Schema schema, StringTemplate template) {
    return resolveJSON(schema, template.values());
  }

  static Object resolveJSON(Schema schema, List<Object> values) {
    return switch (schema) {
      case ObjectSchema(Map<String, Schema> schemaMap) -> new JSONObjectImpl(schemaMap, values);
      case ArraySchema(List<Schema> schemas) -> new JSONArrayImpl(schemas, values);
      case ValueSchema(var slot) -> values.get(slot);
      case ConstantSchema(var constant) -> constant;
    };
  }

  private static final class InliningCache extends MutableCallSite {
    private static final MethodHandle IDENTITY_CHECK, RESOLVE_JSON, FALLBACK;
    static {
      var lookup = MethodHandles.lookup();
      try {
        IDENTITY_CHECK = lookup.findStatic(InliningCache.class, "identityCheck",
            methodType(boolean.class, List.class, StringTemplate.class));
        RESOLVE_JSON = lookup.findStatic(JSONProcessorImpl.class, "resolveJSON",
            methodType(Object.class, Schema.class, StringTemplate.class));
        FALLBACK = lookup.findVirtual(InliningCache.class, "fallback",
            methodType(MethodHandle.class, StringTemplate.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    public InliningCache() {
      super(methodType(Object.class, StringTemplate.class));
      setTarget(foldArguments(exactInvoker(type()), FALLBACK.bindTo(this)));
    }

    private static boolean identityCheck(List<String> fragments, StringTemplate stringTemplate) {
      return stringTemplate.fragments() == fragments;
    }

    private MethodHandle fallback(StringTemplate stringTemplate) {
      var fragments = stringTemplate.fragments();
      var schema = createSchema(fragments, s -> resolveJSON(s, List.of()));
      MethodHandle target;
      if (fragments.size() == 1) {  // constant template
        var result = resolveJSON(schema, List.of());
        target = dropArguments(constant(Object.class, result), 0, StringTemplate.class);
      } else {
        target = RESOLVE_JSON.bindTo(schema);
      }
      var gwt = MethodHandles.guardWithTest(
          IDENTITY_CHECK.bindTo(fragments),
          target,
          new InliningCache().dynamicInvoker()
      );
      setTarget(gwt);
      return target;
    }
  }
}
