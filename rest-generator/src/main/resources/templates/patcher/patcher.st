patcher(model, patcherPackageName, patcherSimpleName, patchRequestType, entityType, patchMappings) ::= <<

<if(patcherPackageName)>package <patcherPackageName>;<endif>

import <entityType>;
import <patchRequestType>;

public class <patcherSimpleName> {

    private final PatcherContext patcherContext

    public <patcherSimpleName>(final PatcherContext patcherContext) {
        this.patcherContext = patcherContext;
    }

    public <entityType.simpleName> <patchRequestType.simpleName> patch(<patchRequestType.simpleName> request, <entityType.simpleName> entity) {
        <patchMappings:patch()>
        return entity;
    }

}
>>