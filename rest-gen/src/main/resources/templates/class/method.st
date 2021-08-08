method(method) ::= <<

<method.javaDoc>
<method.annotations:annotation(); separator="\n">
<method.modifiers; separator=" "> <method.returnType> <method.simpleName>(<method.parameters:parameter(); separator=", ">)<if(method.hasAbstractModifier)>;<else> {
    <method.body>
}<endif>
>>