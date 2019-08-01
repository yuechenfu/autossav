package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.PushRecord;
import com.hiveel.autossav.service.PushRecordService;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("PushRecordControllerMg")
public class PushRecordController {
    @Autowired
    private PushRecordService service;

    @PutMapping({"/mg/me/pushRecord/{id}/unread"})
    public Rest<Boolean> updateUnread(@RequestAttribute("loginPerson") Person loginPerson, PushRecord e) {
        e.setPerson(loginPerson);
        e.setUnread(false);
        return Rest.createSuccess(service.update(e));
    }

    @GetMapping({"/mg/me/pushRecord/count"})
    public Rest<Integer> countByPerson(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        int count = service.countByPerson(searchCondition, e);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/me/pushRecord"})
    public Rest<List<PushRecord>> findByPerson(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        List<PushRecord> list = service.findByPerson(searchCondition, e);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/me/pushRecord/count/unread"})
    public Rest<Integer> countByUnread(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        e.setUnread(true);
        int count = service.countByPerson(searchCondition, e);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/me/pushRecord/unread"})
    public Rest<List<PushRecord>> findByUnread(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        e.setUnread(true);
        List<PushRecord> list = service.findByPerson(searchCondition, e);
        return Rest.createSuccess(list);
    }
}
