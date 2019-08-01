package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
import com.hiveel.autossav.service.AddressService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {
    @Autowired
    private AddressService service;

    @PostMapping("/mg/address")
    public Rest<Long> save(Address e) throws ParameterException {
        ParameterExceptionUtil.verify("address.name", e.getName()).isLengthIn(1, 50);
        if (e.getContent() != null) ParameterExceptionUtil.verify("address.content", e.getContent()).isLengthIn(0, 200);
        service.save(e);
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/address/{id}")
    public Rest<Boolean> delete(Address e) throws ParameterException {
        ParameterExceptionUtil.verify("address.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/address/{id}")
    public Rest<Boolean> update(Address e) throws ParameterException {
        ParameterExceptionUtil.verify("address.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("address.name | content", e.getName(), e.getContent()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/address/{id}"})
    public Rest<Address> findById(Address e) throws ParameterException {
        ParameterExceptionUtil.verify("address.id", e.getId()).isPositive();
        Address data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/address/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/address"})
    public Rest<List<Address>> find(SearchCondition searchCondition) {
        List<Address> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
