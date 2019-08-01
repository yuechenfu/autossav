package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Quote implements Serializable, NotNullObject {
    private Long id;
    private String name;
    private Double labor;
    private Double part;
    private Problem problem;
    private LocalDateTime updateAt;


    public static final Null NULL = new Null();

    private static class Null extends Quote implements NullObject {
        @Override
        public Problem getProblem() {
            return Problem.NULL;
        }
    }

    public void fillNotRequire() {
        labor = labor != null ? labor : 0d;
        part = part != null ? part : 0d;
    }

    public static class Builder extends AbstractBuilder {
        @Override
        public Quote build() {
            try {
                return super.build(Quote.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }

    public Long getId() {
        return id;
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

    public Double getLabor() {
        return labor;
    }

    public void setLabor(Double labor) {
        this.labor = labor;
    }

    public Double getPart() {
        return part;
    }

    public void setPart(Double part) {
        this.part = part;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", labor='" + labor + '\'' +
                ", part=" + part +
                '}';
    }
}
