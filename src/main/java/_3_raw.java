import static java.lang.StringTemplate.RAW;

// $JAVA_HOME/bin/java --enable-preview -cp target/classes _1_text_block
void main() {
    var value = 42;
    StringTemplate template = RAW."result: \{value}  end";
    System.out.println(template);
}
