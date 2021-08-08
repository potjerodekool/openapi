model(model) ::= <<
<if(model.hasPackageName)>package <model.packageName>;<endif>

public class <model.name> {
    <model.properties:field()>
    <model.properties:getter()>
    <model.properties:setter()>
}

>>