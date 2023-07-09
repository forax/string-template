import static com.github.forax.stringtemplate.json.JSONProcessor.JSON;

void main() {
  var jsonObject = JSON."""
       {
         "foo": "bar"
       }
       """;
  System.out.println(jsonObject);
}
