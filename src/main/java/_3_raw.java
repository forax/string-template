import static java.lang.StringTemplate.RAW;

// $JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _4_template_processor _3_raw
void main() {
    var value = 42;
    StringTemplate template = RAW."result: \{value}  end";
    System.out.println(template);
}
