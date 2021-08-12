package org.platonos.demo;

import io.swagger.v3.oas.models.media.StringSchema;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        //SpringDocUtils.getConfig().replaceWithSchema(JsonNullable.class, new StringSchema());

        SpringApplication.run(DemoApplication.class, args);
    }

}
