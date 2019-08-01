package com.hiveel.autossav.manager;

import com.google.gson.Gson;
import com.hiveel.autossav.controller.websocket.cache.AsSocketCache;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.PushRecordService;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import com.hiveel.push.sdk.model.PushAll;
import com.hiveel.push.sdk.model.PushSingle;
import com.hiveel.push.sdk.service.PushService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PushManager {

    @Autowired
    private PushService pushService;
    @Autowired
    private PushRecordService pushRecordService;
    @Autowired
    private AsSocketCache asSocketCache;
    @Autowired
    private PersonService personService;

    @Value("${autossav.apikey:hiveel}")
    private String apiKey;

    public void pushAndSaveRecord(PushRecord pushRecord) {
        boolean result = true;
        try {
            Person person = pushRecord.getPerson();
            if (person.getType() == PersonType.DR) {
                result = pushSingle2Client(pushRecord);
            } else {
                result = pushSingle2WebSocket(pushRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            pushRecord.setStatus(result ? PushRecordStatus.SUCCESS : PushRecordStatus.FAIL);
            pushRecordService.save(pushRecord);
        }
    }

    public Boolean pushSingle2Client(PushRecord pushRecord) throws FailException {
        String content = PushRecordType.getContent(pushRecord);
        Person person = pushRecord.getPerson();
        int count = 0;
        try {
            count = pushRecordService.countByPerson(new SearchCondition(), new PushRecord.Builder().set("person", person).set("unread", true).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int number = count + 1;
        PushSingle pushSingle =
                new PushSingle.Builder()
                        .set("personId", person.getId())
                        .set("content", content)
                        .set("number", number).build();
        Rest<Boolean> rest = pushService.sendSingle(apiKey, pushSingle);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("推送异常");
        }
        return rest.getData();
    }

    public Boolean pushSingle2WebSocket(PushRecord pushRecord) throws FailException {
        Person person = pushRecord.getPerson();
        try {
            boolean otherIsOnline = asSocketCache.getAsToSessionMap().containsKey(person.getId());
            if (!otherIsOnline) {
                return false;
            }
            String content = new Gson().toJson(pushRecord.getPayLoad());
            asSocketCache.send(person,content);
        } catch (IOException e) {
            LogUtil.error("websocket推送失败", e);
            throw new FailException("websocket推送失败");
        }
        return true;
    }

    public Boolean pushAll2Client(PushAll e) throws FailException {
        Rest<Boolean> rest = pushService.sendAll(apiKey, e);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("推送异常");
        }
        return rest.getData();
    }

    public void pushAll2WebSocket(PushRecord pushRecord, PersonType personType) {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setType(String.valueOf(personType));
        List<Person> personList = personService.find(searchCondition);
        personList.stream().forEach(person -> {
            ThreadUtil.run(() -> {
                PushRecord push = new PushRecord();
                BeanUtils.copyProperties(pushRecord,push);
                push.setPerson(person);
                pushAndSaveRecord(push);
            });
        });
    }

    //for testcase's code usage
    public void setPushService(PushService pushService) {
        this.pushService = pushService;
    }
    public void setAsSocketCache(AsSocketCache asSocketCache) {
        this.asSocketCache = asSocketCache;
    }
}
