# openapi
Code generation for Spring.

```xml
<plugin>
    <groupId>com.github.potjerodekool</groupId>
    <artifactId>maven-plugin</artifactId>
    <version>1.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <openApiFile>openapi/spec.yml</openApiFile>
                <modelPackage>org.platonos.demo.api.model</modelPackage>
                <apiPackageName>org.platonos.demo.api</apiPackageName>
                <generateModels>true</generateModels>
                <generateApiDefintions>true</generateApiDefintions>
                <generateApiImplementations>true</generateApiImplementations>
                <dynamicModels>
                    CompanyDto
                </dynamicModels>
            </configuration>
        </execution>
    </executions>
</plugin>
```
