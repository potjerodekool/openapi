patch(patchMapping) ::= <<
request.<patchMapping.getter>().ifPresent(value -> entity.<patchMapping.setter>(value);

>>