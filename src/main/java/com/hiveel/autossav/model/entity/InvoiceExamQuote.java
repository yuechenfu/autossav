package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class InvoiceExamQuote implements Serializable, NotNullObject {
    private Long id;
    private String name;
    private Double labor;
    private Double part;
    private Double tax;
    private InvoiceExam invoiceExam;
    private LocalDateTime updateAt;


    public static final Null NULL = new Null();

    private static class Null extends InvoiceExamQuote implements NullObject {
        @Override
        public InvoiceExam getInvoiceExam() {
            return InvoiceExam.NULL;
        }
    }

    public void fillNotRequire() {
        labor = labor != null ? labor : 0d;
        part = part != null ? part : 0d;
        tax = tax != null ? tax : 0.1;
    }

    public static class Builder extends AbstractBuilder {
        @Override
        public InvoiceExamQuote build() {
            try {
                return super.build(InvoiceExamQuote.class);
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

    public InvoiceExam getInvoiceExam() {
        return invoiceExam;
    }

    public void setInvoiceExam(InvoiceExam invoiceExam) {
        this.invoiceExam = invoiceExam;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Override
	public String toString() {
		return "InvoiceExamQuote {id='" + id + "', name='" + name + "', labor='" + labor + "', part='" + part
				+ "', tax='" + tax + "', invoiceExam='" + invoiceExam + "', updateAt='" + updateAt + "}";
	}
}
