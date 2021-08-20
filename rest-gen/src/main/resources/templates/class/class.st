class(class) ::= <<
<class.annotations:annotation(); separator="\n">
public class <class.simpleName> <if(class.hasInterfaces)>implements <class.interfaces; separator=", ">  <endif> {

    <class.fields:field(); separator="\n\n">
    <class.methods:method(); separator="\n\n">
}

>>