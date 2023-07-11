import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

// $JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _4_template_processor
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

void main() {
    String operator = "-";
    Pattern pattern = PatternProcessor.PATTERN."\{ operator }|foo";
    System.out.println(pattern);

    var matcher = pattern.matcher("-");
    System.out.println("matches(\"-\")? " + matcher.matches());
}


