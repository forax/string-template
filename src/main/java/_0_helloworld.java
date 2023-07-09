// $JAVA_HOME/bin/java --enable-preview -cp target/classes _0_helloworld
void main() {
    var message = "string template";
    //String s = STR."hello \{message} !";
    //String s = STR.process(StringTemplate.of(List.of("hello ", " !"), List.of(message)));
    String s = STR.process(StringTemplate.RAW."hello \{message} !");
    System.out.println(s);
}


// void main();
//    Code:
//       0: ldc           #7                  // String string template
//       2: astore_1
//       3: getstatic     #9                  // Field java/lang/System.out:Ljava/io/PrintStream;
//       6: aload_1
//       7: invokedynamic #15,  0             // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
//      12: invokevirtual #19                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
//      15: return
