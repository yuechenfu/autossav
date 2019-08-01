package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Reminder;
import com.hiveel.autossav.model.entity.Vehicle;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface ReminderDao {
	@Insert("insert into reminder(id, vehicleId, inspectionId, content, `type`, createAt, updateAt, date) VALUES(#{id}, #{vehicle.id}, #{inspection.id}, #{content}, #{type}, #{createAt}, #{updateAt}, #{date})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Reminder e);

    @Delete("delete from reminder where id=#{id}")
    int delete(Reminder e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Reminder e);
    
    @Select("select * from reminder where id=#{id}")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="inspection.id", column="inspectionId")})
    Reminder findById(Reminder e);
    
    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);    
    
    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="inspection.id", column="inspectionId")})
    List<Reminder> find(@Param("searchCondition") SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="inspection.id", column="inspectionId")})    
    List<Reminder> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);

    @SelectProvider(type = Sql.class, method = "countByVehicleList")
    int countByVehicleList(@Param("searchCondition")SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList); 

    class Sql {
        public static String update(final Reminder e) {
            return new SQL() {{
                UPDATE("reminder");
                if (e.getVehicle() != null) SET("vehicleId=#{vehicle.id}");
                if (e.getContent() != null) SET("content=#{content}");
                if (e.getType() != null) SET("type=#{type}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("reminder");
                if (searchCondition.getName() != null) WHERE("content like #{searchCondition.like.name}");
                if (searchCondition.getType() != null) WHERE("type = #{searchCondition.type}");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("reminder e");
                if (searchCondition.getName() != null) WHERE("content like #{searchCondition.like.name}");
                if (searchCondition.getType() != null) WHERE("type = #{searchCondition.type}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*"); FROM("reminder e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countByVehicleList(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicleList") List<Vehicle> vehicleList) {
            return new SQL() {{
                SELECT("count(*)"); FROM("reminder");
                String vehicleIdListStr=vehicleList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(","));
                if (vehicleIdListStr.isEmpty()) vehicleIdListStr="0";
                WHERE("vehicleId in ("+vehicleIdListStr+")");
            }}.toString();
        }

    }
}
