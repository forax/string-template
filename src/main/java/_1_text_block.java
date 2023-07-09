// $$JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _1_text_block
void main() {
    var message = "string template";
    System.out.println(STR."""
        hello \{message} !
        """);
}
