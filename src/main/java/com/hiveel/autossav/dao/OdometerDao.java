package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OdometerDao {

    @Insert("insert into odometer(relateId , type, mi, date, vehicleId, updateAt) "
            + "VALUES(#{relateId}, #{type}, #{mi}, #{date}, #{vehicle.id}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Odometer e);

    @Delete("delete from odometer where id=#{id}")
    int delete(Odometer e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Odometer e);

    @Select("select * from odometer where id=#{id}")
    @Results({@Result(property = "vehicle.id", column = "vehicleId")})
    Odometer findById(Odometer e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property = "vehicle.id", column = "vehicleId")})
    List<Odometer> find(@Param("searchCondition") SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "findByInspection")
    @Results({@Result(property = "vehicle.id", column = "vehicleId")})
    Odometer findByInspection(@Param("searchCondition") SearchCondition searchCondition, @Param("inspection") Inspection inspection);
       
    @SelectProvider(type = Sql.class, method = "findByIssue")
    @Results({@Result(property = "vehicle.id", column = "vehicleId")})
    Odometer findByIssue(@Param("searchCondition") SearchCondition searchCondition, @Param("issue") Issue issue);

    @SelectProvider(type = Sql.class, method = "countByVehicle")
    int countByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") Vehicle vehicle);
    
    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({@Result(property = "vehicle.id", column = "vehicleId")})
    List<Odometer> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") Vehicle vehicle);
  
    class Sql {
        public static String update(final Odometer e) {
            return new SQL() {{
                UPDATE("odometer");
                if (e.getRelateId() != null) SET("relateId=#{relateId}");
                if (e.getType() != null) SET("type=#{type}");
                if (e.getMi() != null) SET("mi=#{mi}");
                if (e.getDate() != null ) SET("date=#{date}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("odometer");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("odometer e");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByInspection(@Param("searchCondition") final SearchCondition searchCondition, @Param("inspection") final Inspection inspection) {
            return new SQL() {{
                SELECT("*"); FROM("odometer  e");
                WHERE(" relateId=#{inspection.id} and  type = 'INSPECTION'");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByIssue(@Param("searchCondition") final SearchCondition searchCondition, @Param("issue") final Issue issue) {
            return new SQL() {{
                SELECT("*"); FROM("odometer e");
                WHERE("relateId=#{issue.id} and type = 'ISSUE'");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("count(*)"); FROM("odometer");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString();
        }
        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*");  FROM("odometer e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }


}
