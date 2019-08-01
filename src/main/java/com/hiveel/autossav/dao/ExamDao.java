package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Exam;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamDao {
    @Insert("insert into exam(name, content, type, updateAt) VALUES(#{name}, #{content}, #{type}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Exam e);

    @Delete("delete from exam where id=#{id}")
    int delete(Exam e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Exam e);

    @Select("select * from exam where id=#{id}")
    Exam findById(Exam e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    List<Exam> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final Exam e) {
            return new SQL() {{
                UPDATE("exam");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getContent() != null) SET("content=#{content}");
                if (e.getType() != null) SET("type=#{type}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("exam e");
                if (searchCondition.getName() != null) { 
                    OR().WHERE("name like #{searchCondition.like.name}");
                    OR().WHERE("content like #{searchCondition.like.name}");
                }
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}"); 
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("exam e");
                if (searchCondition.getName() != null) { 
                    OR().WHERE("name like #{searchCondition.like.name}");
                    OR().WHERE("content like #{searchCondition.like.name}");
                }
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}");        
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
