# HTML 2 PDF Conversor

### How to use

```java
public class MyClass {

    public static void main(String[] args) throws Exception {
        String uriHtml = args[0];
        String destinationPath = args[1];
        new Converter(uriHtml, destinationPath).convert();
    }

}
```