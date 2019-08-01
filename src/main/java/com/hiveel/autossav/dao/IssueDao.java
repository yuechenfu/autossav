package com.hiveel.autossav.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;
import org.h2.util.StringUtils;
import org.springframework.stereotype.Repository;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Vehicle;

@Repository
public interface IssueDao {
    @Insert("insert into issue(vehicleId, driverId, name, content, apptMinDate, apptMaxDate, status, odometer, lon, lat, tax, createAt, updateAt) "
            + "VALUES(#{vehicle.id}, #{driver.id}, #{name}, #{content}, #{apptMinDate}, #{apptMaxDate}, #{status}, #{odometer}, #{lon}, #{lat}, #{tax}, #{createAt}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Issue e);

    @Delete("delete from issue where id=#{id}")
    int delete(Issue e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Issue e);

    @Select("select * from issue where id=#{id}")
    @Results({
        @Result(property="driver.id", column="driverId"),
        @Result(property="vehicle.id", column="vehicleId")
    })
    Issue findById(Issue e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type= Sql.class, method = "find")
    @Results({@Result(property="driver.id", column="driverId"),@Result(property="vehicle.id", column="vehicleId")})
    List<Issue> find(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "countByVehicle")
    int countByVehicle(@Param("searchCondition")SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);   
    
    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({@Result(property="driver.id", column="driverId"),@Result(property="vehicle.id", column="vehicleId")})
    List<Issue> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);
    
    @SelectProvider(type = Sql.class, method = "countByDriver")
    int countByDriver(@Param("searchCondition")SearchCondition searchCondition, @Param("driver") final Person driver);
    
    @SelectProvider(type = Sql.class, method = "findByDriver")
    @Results({@Result(property="driver.id", column="driverId"),@Result(property="vehicle.id", column="vehicleId")})
    List<Issue> findByDriver(@Param("searchCondition") SearchCondition searchCondition, @Param("driver") final Person driver);

    class Sql {
        public static String update(final Issue e) {
            return new SQL() {{
                UPDATE("issue");
                if (e.getVehicle() != null) SET("vehicleId=#{vehicle.id}");
                if (e.getDriver() != null) SET("driverId=#{driver.id}");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getContent() != null) SET("content=#{content}");
                if (e.getApptMinDate() != null) SET("apptMinDate=#{apptMinDate}");
                if (e.getApptMaxDate() != null) SET("apptMaxDate=#{apptMaxDate}");
                if (e.getStatus() != null) SET("status=#{status}");
                if (e.getOdometer() != null) SET("odometer=#{odometer}");
                if (e.getLon() != null) SET("lon=#{lon}");
                if (e.getLat() != null) SET("lat=#{lat}");
                if (e.getTax() != null) SET("tax=#{tax}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("issue e");
                if (searchCondition.getStatus() != null) WHERE("status=#{searchCondition.status}");
                if (searchCondition.getMinDate() != null) WHERE("apptMinDate >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("apptMaxDate <= #{searchCondition.maxDate}");
                if (searchCondition.getIdList() != null) WHERE("vehicleId in ("+searchCondition.getIdList().stream().collect(Collectors.joining(","))+")");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("issue e");
                if (searchCondition.getStatus() != null) WHERE("status=#{searchCondition.status}");
                if (searchCondition.getMinDate() != null) WHERE("apptMinDate >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("apptMaxDate <= #{searchCondition.maxDate}");
                if (searchCondition.getIdList() != null) WHERE("vehicleId in ("+searchCondition.getIdList().stream().collect(Collectors.joining(","))+")");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("count(*)"); FROM("issue e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString();
        }        
        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*"); FROM("issue e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }    
        public static String countByDriver(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver) {
            return new SQL() {{
                SELECT("count(*)"); FROM("issue e");
                WHERE("driverId = #{driver.id}");
            }}.toString();
        }  
        public static String findByDriver(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver) {
            return new SQL() {{
                SELECT("*"); FROM("issue e");
                WHERE("driverId = #{driver.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }         
    }
}
