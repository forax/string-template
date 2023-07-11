// $$JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _1_text_block
void main() {
    String value = "templated processor";
    String result = STR."""
      Hello \{ value } !
      """;
    System.out.println(result);
}
