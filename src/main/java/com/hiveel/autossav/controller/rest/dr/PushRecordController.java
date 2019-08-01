package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.PushRecord;
import com.hiveel.autossav.service.PushRecordService;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("PushRecordControllerDr")
public class PushRecordController {
    @Autowired
    private PushRecordService service;

    @PutMapping({"/dr/me/pushRecord/{id}/unread"})
    public Rest<Boolean> updateUnread(@RequestAttribute("loginPerson") Person loginPerson, PushRecord e) {
        e.setPerson(loginPerson);
        e.setUnread(false);
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/dr/me/pushRecord/count"})
    public Rest<Integer> countByPerson(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        int count = service.countByPerson(searchCondition, e);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/dr/me/pushRecord"})
    public Rest<List<PushRecord>> findByPerson(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        List<PushRecord> list = service.findByPerson(searchCondition, e);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/me/pushRecord/count/unread"})
    public Rest<Integer> countByUnread(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        e.setUnread(true);
        int count = service.countByPerson(searchCondition, e);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/dr/me/pushRecord/unread"})
    public Rest<List<PushRecord>> findByUnread(@RequestAttribute("loginPerson") Person loginPerson, SearchCondition searchCondition, PushRecord e) {
        e.setPerson(loginPerson);
        e.setUnread(true);
        List<PushRecord> list = service.findByPerson(searchCondition, e);
        return Rest.createSuccess(list);
    }

}
