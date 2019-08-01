package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Address implements Serializable, NotNullObject {
    private Long id;
    private String name;
    private String content;
    private LocalDateTime updateAt;

    public static final Null NULL = new Null();
    public void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void fillNotRequire() {
        content = content != null ? content : "";
    }

    private static class Null extends Address implements NullObject {
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Address build() {
            try {
                return super.build(Address.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
	

	@Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
