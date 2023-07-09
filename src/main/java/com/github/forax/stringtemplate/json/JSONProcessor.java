package com.github.forax.stringtemplate.json;

import java.lang.StringTemplate.Processor;
import java.lang.invoke.MethodHandles;

public sealed interface JSONProcessor extends Processor<Object, RuntimeException> permits JSONProcessorImpl {
  @Override
  Object process(StringTemplate stringTemplate);

  Processor<JSONObject, RuntimeException> object();

  Processor<JSONArray, RuntimeException> array();

  JSONProcessor JSON = JSONProcessorImpl.createProcessor();
}