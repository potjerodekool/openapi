method(method) ::= <<
<method.javaDoc>
<method.annotations:annotation(); separator="\n">
<method.modifiers; separator=" "> <if(!method.constructor)> <method.returnType><endif> <method.simpleName>(<method.parameters:parameter(); separator=", ">)<if(method.abstract)>;<else> {
    <method.body>
}<endif>
>>