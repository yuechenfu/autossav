package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Problem;
import com.hiveel.autossav.model.entity.Quote;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface QuoteDao {
    @Insert("insert into quote(problemId, name, labor, part, updateAt) VALUES(#{problem.id}, #{name}, #{labor}, #{part} ,#{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Quote e);

    @Delete("delete from quote where id=#{id}")
    int delete(Quote e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Quote e);

    @Select("select * from quote where id=#{id}")
    @Results( @Result(property="problem.id", column="problemId") )
    Quote findById(Quote e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "find")
    @Results( @Result(property="problem.id", column="problemId") )
    List<Quote> find(@Param("searchCondition") SearchCondition searchCondition);

    @Delete("delete from quote where problemId=#{problem.id}")
    int deleteByProblem(@Param("problem")Problem problem);
    
    @Select("select * from quote where problemId=#{problem.id}")
    @Results( @Result(property="problem.id", column="problemId") )
    List<Quote> findByProblem(@Param("searchCondition")SearchCondition searchCondition,@Param("problem")Problem problem);
    
    @SelectProvider(type = Sql.class, method = "findByProblemList")
    List<Quote> findByProblemList(@Param("searchCondition")SearchCondition searchCondition, @Param("problemList") final List<Problem> problemList); 

    class Sql {
        public static String update(final Quote e) {
            return new SQL() {{
                UPDATE("quote");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getLabor() != null) SET("labor=#{labor}");
                if (e.getPart() != null) SET("part=#{part}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)");  FROM("quote e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");   
            }}.toString();
        }

        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("quote e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");                   
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
        public static String findByProblemList(@Param("searchCondition") final SearchCondition searchCondition, @Param("problemList") final List<Problem> problemList) {
            return new SQL() {{
                SELECT("*"); FROM("quote e");
                String problemIdListStr=problemList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.joining(",")); 
                if (problemIdListStr.isEmpty()) problemIdListStr="0";
                WHERE("problemId in ("+problemIdListStr+")");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}"); 
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
