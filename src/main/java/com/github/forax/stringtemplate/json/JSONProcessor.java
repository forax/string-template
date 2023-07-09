package com.github.forax.stringtemplate.json;

import java.lang.StringTemplate.Processor;
import java.lang.invoke.MethodHandles;

public sealed interface JSONProcessor extends Processor<Object, RuntimeException> permits JSONProcessorImpl {
  @Override
  Object process(StringTemplate stringTemplate);

  default Processor<JSONObject, RuntimeException> object() {
    return stringTemplate -> (JSONObject) process(stringTemplate);
  }

  default Processor<JSONArray, RuntimeException> array() {
    return stringTemplate -> (JSONArray) process(stringTemplate);
  }

  <T extends Record> Processor<T, RuntimeException> record(MethodHandles.Lookup lookup, Class<T> type);

  JSONProcessor JSON = JSONProcessorImpl.createProcessor();
}