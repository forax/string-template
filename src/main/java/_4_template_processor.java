import java.util.regex.Pattern;

// $JAVA_HOME/bin/java --enable-preview -cp target/classes _4_template_processor
record PatternProcessor() implements StringTemplate.Processor<Pattern, RuntimeException> {
  @Override
  public Pattern process(StringTemplate stringTemplate) {
    if (!stringTemplate.values().isEmpty()) {
      throw new IllegalStateException("no values allowed");
    }
    var pattern = String.join("", stringTemplate.fragments());
    return Pattern.compile(pattern);
  }

  public static final PatternProcessor PATTERN = new PatternProcessor();
}

void main() {
    Pattern pattern = PatternProcessor.PATTERN.".*";
    System.out.println(pattern);
}


