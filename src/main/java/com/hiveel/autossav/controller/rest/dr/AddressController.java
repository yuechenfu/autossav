package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.entity.Address;
import com.hiveel.autossav.service.AddressService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController(value="DriverAddressController")
public class AddressController {
    @Autowired
    private AddressService service;

    @GetMapping({"/dr/address/{id}"})
    public Rest<Address> findById(Address e) throws ParameterException {
        ParameterExceptionUtil.verify("address.id", e.getId()).isPositive();
        Address data = service.findById(e);
        return Rest.createSuccess(data);
    }
}
