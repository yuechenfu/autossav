package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SecurityCode implements Serializable, NotNullObject {
    private Long id;
    private String name;
    private String code;
    private SecurityCodeType type;
    private SecurityCodeStatus status;
    private LocalDateTime updateAt;
    private LocalDateTime createAt;

    public static final Null NULL = new Null();
    public void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void createAt() {
        createAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void fillNotRequire() {
            status = status != null ? status : SecurityCodeStatus.UN_USE;
    }

    private static class Null extends SecurityCode implements NullObject {
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public SecurityCode build() {
            try {
                return super.build(SecurityCode.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SecurityCodeType getType() {
        return type;
    }

    public void setType(SecurityCodeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SecurityCodeStatus getStatus() {
        return status;
    }

    public void setStatus(SecurityCodeStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "SecurityCode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", code=" + code +
                '}';
    }
}
