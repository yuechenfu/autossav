package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDriverRelateDao {
    @Insert("insert into vehicleDriverRelate(vehicleId, driverId, onDate, offDate, onOdometer, offOdometer, updateAt)"
    		+ " VALUES(#{vehicle.id}, #{driver.id}, #{onDate}, #{offDate}, #{onOdometer}, #{offOdometer}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(VehicleDriverRelate e);

    @Delete("delete from vehicleDriverRelate where id=#{id}")
    int delete(VehicleDriverRelate e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(VehicleDriverRelate e);
    
    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);    

    @Select("select * from vehicleDriverRelate where id=#{id}")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="driver.id", column="driverId")}) 
    VehicleDriverRelate findById(VehicleDriverRelate e);
    
    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="driver.id", column="driverId")})    
    List<VehicleDriverRelate> find(@Param("searchCondition") SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "countByVehicle")
    int countByVehicle(@Param("searchCondition")SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);  
    
    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="driver.id", column="driverId")})     
    List<VehicleDriverRelate> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);
    
    @SelectProvider(type = Sql.class, method = "countByDriver")
    int countByDriver(@Param("searchCondition")SearchCondition searchCondition, @Param("driver") final Person driver);  
    
    @SelectProvider(type = Sql.class, method = "findByDriver")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="driver.id", column="driverId")})     
    List<VehicleDriverRelate> findByDriver(@Param("searchCondition") SearchCondition searchCondition, @Param("driver") final Person driver);
    
    @SelectProvider(type = Sql.class, method = "findByDriverAndVehicle")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="driver.id", column="driverId")})     
    List<VehicleDriverRelate> findByDriverAndVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("driver") final Person driver, @Param("vehicle") final Vehicle vehicle);

    class Sql {
        public static String update(final VehicleDriverRelate e) {
            return new SQL() {{
                UPDATE("vehicleDriverRelate");
                SET("offDate=#{offDate}");
                SET("offOdometer=#{offOdometer}");
                SET("updateAt=#{updateAt}");                
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("vehicleDriverRelate");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("vehicleDriverRelate e");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("count(*)"); FROM("vehicleDriverRelate");
                if (searchCondition.getOffDate() != null ) WHERE("offDate = #{searchCondition.offDate}");
                WHERE("vehicleId = #{vehicle.id}");                
            }}.toString();
        }
        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*"); FROM("vehicleDriverRelate e");
                if (searchCondition.getOffDate() != null ) WHERE("offDate = #{searchCondition.offDate}");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }    
        public static String countByDriver(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver) {
            return new SQL() {{
                SELECT("count(*)"); FROM("vehicleDriverRelate e");
                if (searchCondition.getOffDate() != null ) WHERE("offDate = #{searchCondition.offDate}");
                WHERE("driverId = #{driver.id}");
            }}.toString();
        }
        public static String findByDriver(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver) {
            return new SQL() {{
                SELECT("*"); FROM("vehicleDriverRelate e");
                if (searchCondition.getOffDate() != null ) WHERE("offDate = #{searchCondition.offDate}");
                WHERE("driverId = #{driver.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }  
        public static String findByDriverAndVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("driver") final Person driver, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*"); FROM("vehicleDriverRelate e");
                if (searchCondition.getOffDate() != null ) WHERE("offDate = #{searchCondition.offDate}");
                WHERE("driverId = #{driver.id}");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
