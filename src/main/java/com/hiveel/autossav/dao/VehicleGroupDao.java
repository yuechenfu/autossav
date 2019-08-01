package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleGroup;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleGroupDao {
    @Insert("insert into vehicleGroup(name, content, updateAt) VALUES(#{name}, #{content}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(VehicleGroup e);

    @Delete("delete from vehicleGroup where id=#{id}")
    int delete(VehicleGroup e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(VehicleGroup e);

    @Select("select * from vehicleGroup where id=#{id}")
    VehicleGroup findById(VehicleGroup e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    List<VehicleGroup> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final VehicleGroup e) {
            return new SQL() {{
                UPDATE("vehicleGroup");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getContent() != null) SET("content=#{content}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("vehicleGroup");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("vehicleGroup e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
