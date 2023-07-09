import static java.util.FormatProcessor.FMT;

// $JAVA_HOME/bin/java --enable-preview -cp target/classes _2_format
void main() {
    var value = 12.23;
    System.out.println(FMT."result: %f\{ 2 * value }");
}

