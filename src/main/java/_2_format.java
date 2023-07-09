import static java.util.FormatProcessor.FMT;

// $JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _2_format
void main() {
    var value = 12.23;
    System.out.println(FMT."result: %f\{ 2 * value }");
}

