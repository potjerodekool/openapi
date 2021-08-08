compilationUnit(compilationUnit) ::= <<
package <compilationUnit.packageElement>;

<compilationUnit.imports:importStatement(); separator="\n">

<compilationUnit.typeElement:typeElement()>
>>