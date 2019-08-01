package com.hiveel.autossav.dao;

import java.util.List;

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
import org.springframework.stereotype.Repository;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Vehicle;

@Repository
public interface PlanDao {
    @Insert("insert into plan(vehicleId, addressId, day, updateAt) VALUES(#{vehicle.id}, #{address.id}, #{day}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Plan e);

    @Delete("delete from plan where id=#{id}")
    int delete(Plan e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Plan e);

    @Select("select * from plan where id=#{id}")
    @Results({@Result(property="vehicle.id", column="vehicleId"), @Result(property="address.id", column="addressId")})
    Plan findById(Plan e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type= Sql.class, method = "find")
    @Results({
        @Result(property="vehicle.id", column="vehicleId"), @Result(property="address.id", column="addressId")
    })
    List<Plan> find(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({
        @Result(property="vehicle.id", column="vehicleId"), @Result(property="address.id", column="addressId")
    })    
    List<Plan> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle);


    class Sql {
        public static String update(final Plan e) {
            return new SQL() {{
                UPDATE("plan");
                if (e.getDay() != null) SET("day=#{day}");
                if (e.getVehicle() != null) SET("vehicleId=#{vehicle.id}");
                if (e.getAddress() != null) SET("addressId=#{address.id}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("plan");
                if (searchCondition.getMinDate() != null) WHERE("day >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("day <= #{searchCondition.maxDate}");               
                }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("plan e");
                if (searchCondition.getMinDate() != null) WHERE("day >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("day <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*"); FROM("plan e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }

    }
}
