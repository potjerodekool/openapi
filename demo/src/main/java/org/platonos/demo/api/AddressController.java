package org.platonos.demo.api;

import io.swagger.annotations.Api;
import org.some.api.AddressesApi;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
public class AddressController implements AddressesApi {
}
