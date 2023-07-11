import java.util.List;
import static java.lang.StringTemplate.RAW;

// $JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _3_raw
void main() {
    int value = 42;
    StringTemplate template1 = StringTemplate.of(List.of("result: ", " !"), List.of(value));
    System.out.println(template1);

    StringTemplate template2 = RAW."result: \{value} !";
    System.out.println(template2);
}
