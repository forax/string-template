package com.github.forax.stringtemplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public sealed interface JSONObject extends Map<String, Object> permits JSONObjectImpl {
  JSONObject object(String key);

  JSONArray array(String key);

  boolean getBoolean(String key);
  int getInt(String key);
  long getLong(String key);
  double getDouble(String key);
  BigInteger getBigInteger(String key);
  BigDecimal getBigDecimal(String key);
  String getString(String key);

  String stringify();
}
