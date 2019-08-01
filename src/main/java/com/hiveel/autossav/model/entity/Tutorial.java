package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Tutorial implements Serializable, NotNullObject {
    private Long id;
    private String name;
    private String filesrc;
    private TutorialType type;
    private LocalDateTime updateAt;

    public static final Null NULL = new Null();
    public void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void fillNotRequire() {
    	filesrc = filesrc != null ? filesrc : "";
    }

    private static class Null extends Tutorial implements NullObject {
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Tutorial build() {
            try {
                return super.build(Tutorial.class);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public String getFilesrc() {
		return filesrc;
	}
	public void setFilesrc(String filesrc) {
		this.filesrc = filesrc;
	}
	public TutorialType getType() {
		return type;
	}
	public void setType(TutorialType type) {
		this.type = type;
	}
	public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
	@Override
	public String toString() {
		return "Tutorial {id='" + id + "', name='" + name + "', filesrc='" + filesrc + "', type='" + type
				+ "', updateAt='" + updateAt + "}";
	}


}
