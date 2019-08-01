package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.VehicleGroup;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface VehicleDao {
    @Insert("insert into vehicle(id, groupId, name, year, make, model, status, type, vin, plate, rental, imgsrc, odometer, createAt, updateAt) " +
            "VALUES(#{id}, #{group.id}, #{name}, #{year}, #{make}, #{model}, #{status}, #{type}, #{vin}, #{plate}, #{rental}, #{imgsrc}, #{odometer}, #{createAt}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Vehicle e);

    @Delete("delete from vehicle where id=#{id}")
    int delete(Vehicle e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Vehicle e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);

    @Select("select * from vehicle where id=#{id}")
    @Results(@Result(property="group.id", column="groupId"))
    Vehicle findById(Vehicle e);

    @SelectProvider(type = Sql.class, method = "find")
    @Results(@Result(property="group.id", column="groupId"))
    List<Vehicle> find(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "findByGroup")
    @Results(@Result(property="group.id", column="groupId"))
    List<Vehicle> findByGroup(@Param("searchCondition") SearchCondition searchCondition, @Param("group") final VehicleGroup group);

    @SelectProvider(type = Sql.class, method = "countExceptVehicleList")
    int countExceptVehicleList(@Param("searchCondition")SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList);

    @SelectProvider(type = Sql.class, method = "findExceptVehicleList")
    @Results(@Result(property="group.id", column="groupId"))
    List<Vehicle> findExceptVehicleList(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList);

    class Sql {
        public static String update(final Vehicle e) {
            return new SQL() {{
                UPDATE("vehicle");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getYear() != null) SET("year=#{year}");
                if (e.getMake() != null) SET("make=#{make}");
                if (e.getModel() != null) SET("model=#{model}");
                if (e.getGroup() != null) SET("groupId=#{group.id}");
                if (e.getStatus() != null) SET("status=#{status}");
                if (e.getVin() != null) SET("vin=#{vin}");
                if (e.getPlate() != null) SET("plate=#{plate}");
                if (e.getRental() != null ) SET("rental=#{rental}");
                if (e.getImgsrc() != null) SET("imgsrc=#{imgsrc}");
                if (e.getOdometer() != null) SET("odometer=#{odometer}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("vehicle e");
                if (searchCondition.getName() != null) {
                    WHERE("name like #{searchCondition.like.name}");
                    OR().WHERE("vin like #{searchCondition.like.name}");
                    OR().WHERE("plate like #{searchCondition.like.name}");
                    OR().WHERE("id like #{searchCondition.like.name}");
                }
                if (searchCondition.getStatus() != null) WHERE("status=#{searchCondition.status}");
                if (searchCondition.getRental() != null) WHERE("rental=#{searchCondition.rental}");
                if (searchCondition.getIdList() != null) WHERE("groupId in ("+searchCondition.getIdList().stream().collect(Collectors.joining(","))+")");
                if (searchCondition.getGroup() != null) WHERE("groupId=#{searchCondition.group}");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("vehicle e");
                if (searchCondition.getName() != null) {
                    WHERE("name like #{searchCondition.like.name}");
                    OR().WHERE("vin like #{searchCondition.like.name}");
                    OR().WHERE("plate like #{searchCondition.like.name}");
                    OR().WHERE("id like #{searchCondition.like.name}");
                }
                if (searchCondition.getStatus() != null) WHERE("status=#{searchCondition.status}");
                if (searchCondition.getRental() != null) WHERE("rental=#{searchCondition.rental}");
                if (searchCondition.getIdList() != null) WHERE("groupId in ("+searchCondition.getIdList().stream().collect(Collectors.joining(","))+")");
                if (searchCondition.getGroup() != null) WHERE("groupId=#{searchCondition.group}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByGroup(@Param("searchCondition") final SearchCondition searchCondition, @Param("group") final VehicleGroup vehicleGroup) {
            return new SQL() {{
                SELECT("*"); FROM("vehicle e");
                WHERE("groupId = #{group.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countExceptVehicleList(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList) {
            return new SQL() {{
                SELECT("count(*)"); FROM("vehicle e");
                String vehicleIdListStr=vehicleList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(","));
                if (vehicleIdListStr.isEmpty()) vehicleIdListStr="0";
                WHERE("id not in ("+vehicleIdListStr+")");
            }}.toString();
        }
        public static String findExceptVehicleList(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList) {
            return new SQL() {{
                SELECT("*"); FROM("vehicle e");
                String vehicleIdListStr=vehicleList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(","));
                if (vehicleIdListStr.isEmpty()) vehicleIdListStr="0";
                WHERE("id not in ("+vehicleIdListStr+")");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
