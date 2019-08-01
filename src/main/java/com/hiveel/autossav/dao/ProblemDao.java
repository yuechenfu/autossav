package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface ProblemDao {

    @Insert("insert into problem(relateId , type, remark, examId, vehicleId, imgsrc1, imgsrc2, imgsrc3, imgsrc4, updateAt) "
            + "VALUES(#{relateId}, #{type}, #{remark}, #{exam.id}, #{vehicle.id}, #{imgsrc1}, #{imgsrc2}, #{imgsrc3}, #{imgsrc4}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Problem e);

    @Delete("delete from problem where id=#{id}")
    int delete(Problem e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Problem e);

    @Select("select * from problem where id=#{id}")
    @Results({@Result(property = "exam.id", column = "examId"), @Result(property = "vehicle.id", column = "vehicleId")})
    Problem findById(Problem e);
    
    @Select("select * from problem where relateId=#{relateId} and type=#{type} and examId=#{exam.id}")
    @Results({@Result(property = "exam.id", column = "examId"), @Result(property = "vehicle.id", column = "vehicleId")})
    Problem findByRelatedIdAndExamId(Problem e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property = "exam.id", column = "examId"), @Result(property = "vehicle.id", column = "vehicleId")})
    List<Problem> find(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "countByInspection")
    int countByInspection(@Param("searchCondition") SearchCondition searchCondition, @Param("inspection") Inspection inspection);
    
    @SelectProvider(type = Sql.class, method = "findByInspection")
    @Results({@Result(property = "exam.id", column = "examId"), @Result(property = "vehicle.id", column = "vehicleId")})
    List<Problem> findByInspection(@Param("searchCondition") SearchCondition searchCondition, @Param("inspection") Inspection inspection);
    
    @SelectProvider(type = Sql.class, method = "findByInspectionList")
    List<Problem> findByInspectionList(@Param("searchCondition")SearchCondition searchCondition, @Param("inspectionList") final List<Inspection> inspectionList);

    @SelectProvider(type = Sql.class, method = "countByIssue")
    int countByIssue(@Param("searchCondition") SearchCondition searchCondition, @Param("issue") Issue issue);
    
    @SelectProvider(type = Sql.class, method = "findByIssue")
    @Results({@Result(property = "exam.id", column = "examId"), @Result(property = "vehicle.id", column = "vehicleId")})
    List<Problem> findByIssue(@Param("searchCondition") SearchCondition searchCondition, @Param("issue") Issue issue);
    
    @SelectProvider(type = Sql.class, method = "findByIssueList")
    List<Problem> findByIssueList(@Param("searchCondition")SearchCondition searchCondition, @Param("issueList") final List<Issue> issueList);

    @SelectProvider(type = Sql.class, method = "findByVehicle")
    @Results({@Result(property = "exam.id", column = "examId"), @Result(property = "vehicle.id", column = "vehicleId")})
    List<Problem> findByVehicle(@Param("searchCondition") SearchCondition searchCondition, @Param("vehicle") Vehicle vehicle);

    @SelectProvider(type = Sql.class, method = "findByVehicleList")
    List<Problem> findByVehicleList(@Param("searchCondition")SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList); 
    
    class Sql {
        public static String update(final Problem e) {
            return new SQL() {{
                UPDATE("problem");
                if (e.getRelateId() != null) SET("relateId=#{relateId}");
                if (e.getType() != null) SET("type=#{type}");
                if (e.getRemark() != null) SET("remark=#{remark}");
                if (e.getExam() != null && e.getExam().getId() != null) SET("examId=#{exam.id}");
                if (e.getImgsrc1() != null) SET("imgsrc1=#{imgsrc1}");
                if (e.getImgsrc2() != null) SET("imgsrc2=#{imgsrc2}");
                if (e.getImgsrc3() != null) SET("imgsrc3=#{imgsrc3}");
                if (e.getImgsrc4() != null) SET("imgsrc4=#{imgsrc4}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("problem");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("problem e");
                if (searchCondition.getName() != null) WHERE("e.name like #{searchCondition.like.name}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String countByInspection(@Param("searchCondition") final SearchCondition searchCondition, @Param("inspection") final Inspection inspection) {
            return new SQL() {{
                SELECT("count(*)"); FROM("problem e");
                WHERE(" relateId=#{inspection.id} and type = 'INSPECTION'");
            }}.toString();
        }
        public static String findByInspection(@Param("searchCondition") final SearchCondition searchCondition, @Param("inspection") final Inspection inspection) {
            return new SQL() {{
                SELECT("*"); FROM("problem  e");
                WHERE(" relateId=#{inspection.id} and  type = 'INSPECTION'");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByInspectionList(@Param("searchCondition")SearchCondition searchCondition, @Param("inspectionList") final List<Inspection> inspectionList) {
            return new SQL() {{
                SELECT("*"); FROM("problem e");
                String inspectionIdListStr = inspectionList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(","));
                if (inspectionIdListStr.isEmpty()) inspectionIdListStr="0";
                WHERE("relateId in ("+inspectionIdListStr+") and type = 'INSPECTION'");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        
        public static String countByIssue(@Param("searchCondition") final SearchCondition searchCondition, @Param("issue") final Issue issue) {
            return new SQL() {{
                SELECT("count(*)"); FROM("problem e");
                WHERE(" relateId=#{issue.id} and type = 'ISSUE'");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
            }}.toString();
        }
        public static String findByIssue(@Param("searchCondition") final SearchCondition searchCondition, @Param("issue") final Issue issue) {
            return new SQL() {{
                SELECT("*"); FROM("problem e");
                WHERE("relateId=#{issue.id} and type = 'ISSUE'");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByIssueList(@Param("searchCondition")SearchCondition searchCondition, @Param("issueList") final List<Issue> issueList) {
            return new SQL() {{
                SELECT("*"); FROM("problem e");
                String issueIdListStr = issueList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(","));
                if (issueIdListStr.isEmpty()) issueIdListStr="0";
                WHERE("relateId in ("+issueIdListStr+") and type = 'ISSUE'");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }

        public static String findByVehicle(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicle") final Vehicle vehicle) {
            return new SQL() {{
                SELECT("*");  FROM("problem e");
                WHERE("vehicleId = #{vehicle.id}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        
        public static String findByVehicleList(@Param("searchCondition") final SearchCondition searchCondition, @Param("vehicleList") final List<Vehicle> vehicleList) {
            return new SQL() {{
                SELECT("*"); FROM("problem e");
                String vehicleIdListStr = vehicleList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(","));
                if (vehicleIdListStr.isEmpty()) vehicleIdListStr="0";
                WHERE("vehicleId in ("+vehicleIdListStr+")");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }


}
