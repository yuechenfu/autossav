package com.hiveel.autossav.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.h2.util.StringUtils;
import org.springframework.stereotype.Repository;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;

@Repository
public interface PersonDao {
    @Insert("insert into person(firstName, lastName, phone, email, driverLicense, groupId, type, imgsrc, createAt, updateAt) "
            + "VALUES(#{firstName}, #{lastName}, #{phone}, #{email}, #{driverLicense}, #{group.id}, #{type}, #{imgsrc}, #{createAt}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Person e);

    @Delete("delete from person where id=#{id}")
    int delete(Person e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Person e);

    @Select("select * from person where id=#{id}")
    @Results(
        @Result(property="group.id", column="groupId")
    )
    Person findById(Person e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type= Sql.class, method = "find")
    @Results(
        @Result(property="group.id", column="groupId")
    )
    List<Person> find(@Param("searchCondition")SearchCondition searchCondition);

    @Select("select count(*) from person where email=#{email}")
    int countByEmail(@Param("email")String email);

    @Select("select count(*) from person where phone=#{phone}")
    int countByPhone(@Param("phone")String phone);


    class Sql {
        public static String update(final Person e) {
            return new SQL() {{
                UPDATE("person");
                if (e.getFirstName() != null) SET("firstName=#{firstName}");
                if (e.getLastName() != null) SET("lastName=#{lastName}");
                if (e.getPhone() != null) SET("phone=#{phone}");
                if (e.getEmail() != null) SET("email=#{email}");
                if (e.getDriverLicense() != null) SET("driverLicense=#{driverLicense}");
                if (e.getGroup() != null) SET("groupId=#{group.id}");
                if (e.getImgsrc() != null) SET("imgsrc=#{imgsrc}");
                if (e.getType() != null) SET("type=#{type}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("person");
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}");
                if (searchCondition.getTypeList() != null) WHERE("type in ("+searchCondition.getTypeList().stream().map(e->"'"+e.toString()+"'").collect(Collectors.joining(","))+")");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");                 
                if (searchCondition.getName() != null) {
                    WHERE("firstName like #{searchCondition.like.name}");
                    OR().WHERE("lastName like #{searchCondition.like.name}");
                    OR().WHERE("phone like #{searchCondition.like.name}");
                    OR().WHERE("email like #{searchCondition.like.name}");
                    OR().WHERE("driverLicense like #{searchCondition.like.name}");
                    if ( StringUtils.isNumber(searchCondition.getName()) )
                        OR().WHERE("id=#{searchCondition.name}");
                }                
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("person e");
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}"); 
                if (searchCondition.getTypeList() != null) WHERE("type in ("+searchCondition.getTypeList().stream().map(e->"'"+e.toString()+"'").collect(Collectors.joining(","))+")");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");                 
                if (searchCondition.getName() != null) {
                    WHERE("firstName like #{searchCondition.like.name}");
                    OR().WHERE("lastName like #{searchCondition.like.name}");
                    OR().WHERE("phone like #{searchCondition.like.name}");
                    OR().WHERE("email like #{searchCondition.like.name}");
                    OR().WHERE("driverLicense like #{searchCondition.name}");
                    if ( StringUtils.isNumber(searchCondition.getName()) )
                        OR().WHERE("id=#{searchCondition.name}");
                }
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
