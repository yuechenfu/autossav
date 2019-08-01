package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class InvoiceExam implements Serializable, NotNullObject {
    private Long id;
    private Double price;
    private String date;
    private LocalDateTime updateAt;

    
	public void updateAt() {
		updateAt = LocalDateTime.now(ZoneId.of("UTC"));
	}
    
    public static final Null NULL = new Null();
    private static class Null extends InvoiceExam implements NullObject {
    }  
    public static class Builder extends AbstractBuilder {
        @Override
        public InvoiceExam build() {
            try {
                return super.build(InvoiceExam.class);
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	@Override
	public String toString() {
		return "InvoiceExam {id='" + id + "', price='" + price + "', date='" + date + "', updateAt='" + updateAt
				+ "}";
	}

}
