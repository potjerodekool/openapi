typeElement(typeElement) ::= <<
<if(typeElement.classKind)>
<typeElement:class()>
<elseif(typeElement.interfaceKind)>
<typeElement:interface()>
<else>
<typeElement.kind>
<endif>
>>