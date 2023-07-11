import static com.github.forax.stringtemplate.json.JSONProcessor.JSON;

//  $JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _5_json_processor
void main() {
  var value = 42;
  var jsonObject = JSON."""
       {
         "foo": "bar",
         "baz": \{ value }
       }
       """;
  System.out.println(jsonObject);
}
