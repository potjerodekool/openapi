field(field) ::= <<
<field.annotations:annotation(); separator="\n">
<field.modifiers; separator=" "> <field.type> <field.simpleName><if(field.value)> = <field.value><endif>;
>>