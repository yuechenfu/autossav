package com.hiveel.autossav.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class PushRecord implements Serializable, NotNullObject {

    private Long id;
    private PushRecordType type;
    private Boolean unread;
    private Person person;
    private PushRecordStatus status;
    private Long relateId;
    @JsonIgnore
    private String data;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


    //not in db
    private Map<String, Object> payLoad = new HashMap<>();
    private String content;

    //todo 临时满足shaun的需求，后续删除
    private Vehicle vehicle; //后续删除
    private Issue issue;//后续删除
    private Inspection inspection;//后续删除

    public void fillNotRequire() {
        status = status != null ? status : PushRecordStatus.SUCCESS;
        content = content != null ? content : "";
        unread = unread != null ? unread : true;
    }

    public static final Null NULL = new Null();

    private static class Null extends PushRecord implements NullObject {
        @Override
        public Person getPerson() {
            return Person.NULL;
        }
    }

    public static class Builder extends AbstractBuilder {
        @Override
        public PushRecord build() {
            try {
                return super.build(PushRecord.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public void createAt() {
        createAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public Long getId() {
        return id == null ? 0 : id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PushRecordType getType() {
        return type;
    }

    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }

    public void setType(PushRecordType type) {
        this.type = type;
        this.addPayLoad("type", String.valueOf(type));
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public PushRecordStatus getStatus() {
        return status;
    }

    public Long getRelateId() {
        return relateId;
    }

    public void setRelateId(Long relateId) {
        this.relateId = relateId;
        this.addPayLoad("relateId",relateId);
    }

    public void setStatus(PushRecordStatus status) {
        this.status = status;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.addPayLoad("content", content);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Map<String, Object> getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(Map<String, Object> payLoad) {
        this.payLoad = payLoad;
    }


    public PushRecord addPayLoad(String key, Object obj) {
        this.getPayLoad().put(key, obj);
        return this;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", person='" + person + '\'' +
                ", payLoad='" + payLoad + '\'' +
                '}';
    }

    public static class PayLoadUnit{
        private String key;
        private String className;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
