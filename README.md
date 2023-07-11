# string-template
Java 21 adds the concept of String template, this is a repository of examples and perf tests of this feature

## JEP 438

The JEP 438 describes how the feature works
  https://openjdk.org/jeps/430

Note: I think this JEP should be re-titled as "template processor" because the JEP is more than just string templating.


## Examples

### [Hello World](src/main/java/_0_helloworld.java)

A template processor (`StringTemplate.TemplateProcessor<R,E extends Exception>`) is a code, a functional interface,
that takes a 'templated string' and returns an object.
A 'templated string' (`TemplatedString`) is a tuple of a list of strings called 'fragments' and
a list of values called 'values'.

There is a special syntax to invoke a template processor that does two things, first, from a string with some values
escaped in between '\(' and ')' creates a 'templated string' then calls a template processor with that 'templated string'.

Here is an example, with `TemplatedString.STR` a template processor that concatenate the 'templated string' to a String.
```java
TemplatedString.TemplateProcessor<String, RuntimeException> porocessor = TemplatedString.STR;
String value = "templated processor";
String result = processor."Hello \{ value } !";
System.out.println(result);  // Hello templated processor !
```

or using an import static
```java
import static java.lang.template.TemplatedString.STR;

String value = "templated processor";
String result = STR."Hello \{ value } !";
```

or because `STR` is imported by default (this is the only one)
```java
String value = "templated processor";
String result = STR."Hello \{ value } !";
```

A `TemplateProcessor<R,E>` is a functional interface that takes a `TemplatedString` and return a 'R'
(`E` is the type of the exception, you can use `RuntimeException` if no checked exception can be thrown).

A `TemplatedString` is a tuple of a list of _fragments_ (List<String>) and a list of _values_ (List<Object>).
For each value, there is always a fragment before it and a fragment after it, by example,
`templateProcessor."this is a \{ value } string"` creates a templated string fragments `["this is a ", " string"]`
and a list of values containing the value of the variable `value`, `["templated]`.


### [Text blocks](src/main/java/_1_text_block.java)

The syntax to call a template processor can use the classical string syntax ("") or the text block syntax (""" """).
Here is an example using the text block syntax
```java
String value = "templated processor";
String result = STR."""
  Hello \{ value } !
  """;
```


### [FMT](src/main/java/_2_format.java)

Apart from `STR` (aka `TemplatedString.STR`), the JDK provides another template processor named `FMT`
(aka `FormatProcessor.FMT`) which allow to prefix a value with a format (using the printf format).

By example, to format a double, we can write
```java
import static java.util.FormatProcessor.FMT;
...

double value = 12.23;
System.out.println(FMT."result: %.3f\{ 2 * value }");  // result: 24.460
```

You can notice that unlike `STR`, `FMT` has to be explicitly imported.
In terms of implementation, the `FMT` processor take a look to the 'fragment' before value and if it ends with a '%',
use that format to format the value. So the format is not part of the 'templated string' syntax but just something
this peculiar processor recognize.


### [Creating a templated string](src/main/java/_3_raw.java)

There are two ways to create a `TemplatedString`, you can create it from a list of fragments and a list of values
using `TemplatedString.of(fragments, values)` or you can use the `RAW` processor (aka `TEmplatedString.RAW`) provided
by the JDK which is a processor that returns a `TemplatedString`

```java
int value = 42;
StringTemplate template1 = StringTemplate.of(List.of("result: ", " !"), List.of(value));
System.out.println(template1);  // StringTemplate{ fragments = [ "result: ", " !" ], values = [42] }
  
StringTemplate template2 = RAW."result: \{value} !";
System.out.println(template2);  // StringTemplate{ fragments = [ "result: ", " !" ], values = [42] }
```


### [Your own template processor](src/main/java/_4_template_processor.java)

You can define your own template processor by implementing the function interface `StringTemplate.Processor`
defined as such
```java
@FunctionalInterface
interface Processor<R, E extends Exception> {
  R process(StringTemplate stringTemplate) throws E;
}
```

For example, if we want to define a template processor that produce a regex pattern, it can be done like this.
```java
record PatternProcessor() implements StringTemplate.Processor<Pattern, RuntimeException> {
  @Override
  public Pattern process(StringTemplate stringTemplate) {
    List<String> quoted = stringTemplate.values().stream()
        .map(value -> Pattern.quote(value.toString()))
        .toList();
    String pattern = StringTemplate.interpolate(stringTemplate.fragments(), quoted);
    return Pattern.compile(pattern);
  }

  public static final PatternProcessor PATTERN = new PatternProcessor();
}
...
String operator = "-";
Pattern pattern = PatternProcessor.PATTERN."\{ operator }|foo";
System.out.println(pattern);  // \Q-\E|foo
```

This processor escapes all the values using `Pattern.quote()` so a value is recognized as a plain text
and as a regex. Then it concatenates all the strings to create the pattern and then compiles it.


## More Template Processors

This repository also contains two processors to show how a real template processor can be implemented

### [JSONProcessor](src/main/java/_5_json_processor.java)

The JSON processor takes a JSON templated string and created the corresponding
[JSONObject](src/main/java/com/github/forax/stringtemplate/json/JSONObject.java)/[JSONArray](src/main/java/com/github/forax/stringtemplate/json/JSONArray.java)
data structure.

```java
  var value = 42;
  var jsonObject = JSON."""
       {
         "foo": "bar",
         "baz": \{ value }
       }
       """;
  System.out.println(jsonObject);
```

Internally, it uses [Jackson](https://github.com/FasterXML/jackson) to parse the structure (a kind of schema) and
from the schema lazyily produces the tree of data structure by inserting the values at the correct place.
The schema is only validated once, so it's more efficient than using `ObjectMapper.readTree()` that will parse
the string each time.


### [LoggerProcessor](src/main/java/_6_logger_processor.java)

The Logger processor detects the current class, asks for a logger, using `java.util.logging`) but the approach
is not specific to a logger API, and sends the interpolated string to the logger. 

```java
  var name = "Will Robinson";
  LOGGER.error()."Danger, \{ name } !";
```

Sadly, this implementation has a lot of [overhead](src/main/java/com/github/forax/stringtemplate/log/LoggerBenchmarks.java)
compared to logging with a lambda because the 'templated string' is always created 
even if the corresponding logging level is not enabled.
