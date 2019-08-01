package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.PersonGroup;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonGroupDao {
    @Insert("insert into personGroup(id, type, name, dashboard, inspection, issues, exam, vehicle, person, invoice, setting, notification, updateAt) "
            + "VALUES(#{id}, #{type}, #{name}, #{dashboard}, #{inspection}, #{issues}, #{exam}, #{vehicle}, #{person}, #{invoice}, #{setting}, #{notification}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(PersonGroup e);

    @Delete("delete from personGroup where id=#{id}")
    int delete(PersonGroup e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(PersonGroup e);

    @Select("select * from personGroup where id=#{id}")
    PersonGroup findById(PersonGroup e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    @SelectProvider(type = Sql.class, method = "find")
    List<PersonGroup> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final PersonGroup e) {
            return new SQL() {{
                UPDATE("personGroup");
                if (e.getType() != null) SET("type=#{type}");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getDashboard() != null) SET("dashboard=#{dashboard}");
                if (e.getInspection() != null) SET("inspection=#{inspection}");
                if (e.getIssues() != null) SET("issues=#{issues}");
                if (e.getExam() != null) SET("exam=#{exam}");
                if (e.getVehicle() != null) SET("vehicle=#{vehicle}");
                if (e.getPerson() != null) SET("person=#{person}");
                if (e.getInvoice() != null) SET("finacial=#{finacial}");
                if (e.getSetting() != null) SET("setting=#{setting}");
                if (e.getNotification() != null) SET("notification=#{notification}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("personGroup");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getType() != null) WHERE("type = #{searchCondition.type}");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("personGroup e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getType() != null) WHERE("type = #{searchCondition.type}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
