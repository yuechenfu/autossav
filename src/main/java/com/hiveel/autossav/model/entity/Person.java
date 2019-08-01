package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Person implements Serializable, NotNullObject {
    private Long id;
    private PersonType type;
    private PersonGroup group;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String driverLicense;
    private String imgsrc;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static final Null NULL = new Null();
    
    public void fillNotRequire() {
        phone = phone != null ? phone : "";
        email = email != null ? email : "";
        firstName = firstName != null ? firstName : "";
        lastName = lastName != null ? lastName : "";
        driverLicense = driverLicense != null ? driverLicense : "";
        imgsrc = imgsrc != null ? imgsrc : "";
   }
    public void createAt() {
        createAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }
    private static class Null extends Person implements NullObject {
        @Override
        public PersonGroup getGroup() { return PersonGroup.NULL; }
        
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Person build() {
            try {
                return super.build(Person.class);
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
    public PersonGroup getGroup() {
        return group;
    }
    public void setGroup(PersonGroup group) {
        this.group = group;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        if (!StringUtils.isEmpty(phone) && phone.length() == 10) {
            phone = "1" + phone; //自动加美国区号
        }
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDriverLicense() {
        return driverLicense;
    }
    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }


    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    public String getImgsrc() {
		return imgsrc;
	}
	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
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

    @Override
	public String toString() {
		return "Person {id='" + id + "', group='" + group + "', firstName='" + firstName + "', lastName='" + lastName
				+ "', phone='" + phone + "', email='" + email + "', driverLicense='" + driverLicense + "', type='"
                + type + "', imgsrc='" + imgsrc + "', createAt='" + createAt + "', updateAt='" + updateAt + "}";
	}
}
