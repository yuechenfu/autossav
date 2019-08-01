package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InspectionStatus;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface InspectionDao {
    @Insert("insert into inspection(id, vehicleId, driverId, autosaveId, addressId, date, odometer, name, content, status, tax, createAt, updateAt)"
    		+ " VALUES(#{id}, #{vehicle.id}, #{driver.id}, #{autosave.id}, #{address.id}, #{date}, #{odometer}, #{name}, #{content}, #{status}, #{tax}, #{createAt}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Inspection e);

    @Delete("delete from inspection where id=#{id}")
    int delete(Inspection e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Inspection e);
    
    @UpdateProvider(type = Sql.class, method = "updateStatusByPendingAndDate")
    int updateStatusByPendingAndDate(Inspection e);

    @Select("select * from inspection where id=#{id}")
    @Results({@Result(property="vehicle.id", column="vehicleId"),@Result(property="driver.id", column="driverId"), @Result(property="address.id", column="addressId"), @Result(property="autosave.id", column="autosaveId")})
    Inspection findById(Inspection e);
    
    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);        
    
    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property="vehicle.id", column="vehicleId"),@Result(property="driver.id", column="driverId"), @Result(property="address.id", column="addressId"), @Result(property="autosave.id", column="autosaveId")}) 
    List<Inspection> find(@Param("searchCondition") SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "countByVehicle")
    int countByVehicle(@Param("searchCondition")SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);  
    
    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({@Result(property="vehicle.id", column="vehicleId"),@Result(property="driver.id", column="driverId"), @Result(property="address.id", column="addressId"), @Result(property="autosave.id", column="autosaveId")})    
    List<Inspection> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);

    @SelectProvider(type = Sql.class, method = "countByDriver")
    int countByDriver(@Param("searchCondition")SearchCondition searchCondition, @Param("driver") final Person driver);
    
    @SelectProvider(type = Sql.class, method = "findByDriver")
    @Results({@Result(property="vehicle.id", column="vehicleId"),@Result(property="driver.id", column="driverId"), @Result(property="address.id", column="addressId"), @Result(property="autosave.id", column="autosaveId")})   
    List<Inspection> findByDriver(@Param("searchCondition") SearchCondition searchCondition, @Param("driver") final Person driver);

    class Sql {
        public static String update(final Inspection e) {
            return new SQL() {{
                UPDATE("inspection");
                if (e.getVehicle() != null) SET("vehicleId=#{vehicle.id}");
                if (e.getDriver() != null) SET("driverId=#{driver.id}");
                if (e.getAutosave() != null) SET("autosaveId=#{autosave.id}");
                if (e.getAddress() != null) SET("addressId=#{address.id}");
                if (e.getDate() != null) SET("date=#{date}");
                if (e.getOdometer() != null) SET("odometer=#{odometer}");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getContent() != null) SET("content=#{content}");
                if (e.getStatus() != null) SET("status=#{status}");
                if (e.getTax() != null) SET("tax=#{tax}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String updateStatusByPendingAndDate(final Inspection e) {
            return new SQL() {{
                UPDATE("inspection");
                SET("status = #{status}");
                SET("updateAt = #{updateAt}");
                WHERE("status = 'PENDING'");
                AND().WHERE("date < #{date}");
            }}.toString();
        }
        
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("inspection");
                if (searchCondition.getStatus() != null) WHERE("status = #{searchCondition.status}");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");   
                if (searchCondition.getIdList() != null) WHERE("vehicleId in ("+searchCondition.getIdList().stream().collect(Collectors.joining(","))+")");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("inspection e");
                if (searchCondition.getStatus() != null) WHERE("status = #{searchCondition.status}");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");  
                if (searchCondition.getIdList() != null) WHERE("vehicleId in ("+searchCondition.getIdList().stream().collect(Collectors.joining(","))+")");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countByVehicle(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("inspection e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString();
        }        
        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*"); FROM("inspection e");
                WHERE("vehicleId = #{vehicle.id}");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");  
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }    
        public static String countByDriver(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver) {
            return new SQL() {{
                SELECT("count(*)"); FROM("inspection e");
                WHERE("driverId = #{driver.id}");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");  
            }}.toString();
        } 
        public static String findByDriver(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver) {
            return new SQL() {{
                SELECT("*"); FROM("inspection e");
                WHERE("driverId = #{driver.id}");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}"); 
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }          
    }
}
