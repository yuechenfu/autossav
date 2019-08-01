package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Problem implements Serializable, NotNullObject {
    private Long id;
    private Long relateId;
    private ProblemType type;
    private String remark;
    private String imgsrc1;
    private String imgsrc2;
    private String imgsrc3;
    private String imgsrc4;
    private Exam exam;
    private Vehicle vehicle;
    private LocalDateTime updateAt;

    //非数据库直接查出，由service设置
    private Inspection inspection;
    private Issue issue;

    public void fillNotRequire() {
        remark = remark != null ? remark : "";
        imgsrc1 = imgsrc1 != null ? imgsrc1 : "";
        imgsrc2 = imgsrc2 != null ? imgsrc2 : "";
        imgsrc3 = imgsrc3 != null ? imgsrc3 : "";
        imgsrc4 = imgsrc4 != null ? imgsrc4 : "";
    }

    public static final Null NULL = new Null();

    private static class Null extends Problem implements NullObject {
        @Override
        public Issue getIssue() {
            return Issue.NULL;
        }

        @Override
        public Inspection getInspection() {
            return Inspection.NULL;
        }

        @Override
        public Exam getExam() {
            return Exam.NULL;
        }

        @Override
        public Vehicle getVehicle() {
            return Vehicle.NULL;
        }
    }

    public static class Builder extends AbstractBuilder {
        @Override
        public Problem build() {
            try {
                return super.build(Problem.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }

    public Long getId() {
        return id == null ? 0 : id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelateId() {
        return relateId;
    }

    public void setRelateId(Long relateId) {
        this.relateId = relateId;
    }

    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImgsrc1() {
        return imgsrc1;
    }

    public void setImgsrc1(String imgsrc1) {
        this.imgsrc1 = imgsrc1;
    }

    public String getImgsrc2() {
        return imgsrc2;
    }

    public void setImgsrc2(String imgsrc2) {
        this.imgsrc2 = imgsrc2;
    }

    public String getImgsrc3() {
        return imgsrc3;
    }

    public void setImgsrc3(String imgsrc3) {
        this.imgsrc3 = imgsrc3;
    }

    public String getImgsrc4() {
        return imgsrc4;
    }

    public void setImgsrc4(String imgsrc4) {
        this.imgsrc4 = imgsrc4;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", relateId='" + relateId + '\'' +
                ", type='" + true + '\'' +
                ", remark=" + remark +
                '}';
    }
}
