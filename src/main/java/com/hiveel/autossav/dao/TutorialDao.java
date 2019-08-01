package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Tutorial;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialDao {
    @Insert("insert into tutorial(name, filesrc, type, updateAt) VALUES(#{name}, #{filesrc}, #{type}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Tutorial e);

    @Delete("delete from tutorial where id=#{id}")
    int delete(Tutorial e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Tutorial e);

    @Select("select * from tutorial where id=#{id}")
    Tutorial findById(Tutorial e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    List<Tutorial> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final Tutorial e) {
            return new SQL() {{
                UPDATE("tutorial");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getFilesrc() != null) SET("filesrc=#{filesrc}");
                if (e.getType() != null) SET("type=#{type}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("tutorial e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}");  
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("tutorial e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}");        
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
