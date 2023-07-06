// $JAVA_HOME/bin/java --enable-preview -cp target/classes _1_text_block
void main() {
    var message = "string template";
    System.out.println(STR."""
        hello \{message} !
        """);
}
