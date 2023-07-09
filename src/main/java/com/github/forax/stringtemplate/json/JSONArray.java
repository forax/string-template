package com.github.forax.stringtemplate.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public sealed interface JSONArray extends List<Object> permits JSONArrayImpl {
  JSONObject object(int index);

  JSONArray array(int index);

  boolean getBoolean(int index);
  int getInt(int index);
  long getLong(int index);
  double getDouble(int index);
  BigInteger getBigInteger(int index);
  BigDecimal getBigDecimal(int index);
  String getString(int index);

  String stringify();
}
