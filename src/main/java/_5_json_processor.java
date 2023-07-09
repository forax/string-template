import static com.github.forax.stringtemplate.json.JSONProcessor.JSON;

//  $JAVA_HOME/bin/java --enable-preview -cp target/string-template-1.0-SNAPSHOT-jar-with-dependencies.jar _5_json_processor
void main() {
  var jsonObject = JSON."""
       {
         "foo": "bar",
         "baz": 42
       }
       """;
  System.out.println(jsonObject);
}
