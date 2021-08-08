parameter(parameter)::=<<
<if(parameter.hasAnnotations)><parameter.annotations:annotation()> <endif><if(parameter.hasModifiers)><parameter.modifiers;separator=" "> <endif><parameter.type> <parameter.simpleName>
>>