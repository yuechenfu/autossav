package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushRecordDao {
    @Insert("insert into pushRecord(type, unread, personId, data, status, relateId , createAt, updateAt) "
            + "values(#{type}, #{unread}, #{person.id}, #{data},  #{status}, #{relateId},  #{createAt}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(PushRecord e);

    @Delete("delete from pushRecord where id=#{id}")
    int delete(PushRecord e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(PushRecord e);

    @Select("select * from pushRecord where id=#{id}")
    @Results({@Result(property = "person.id", column = "personId")})
    PushRecord findById(PushRecord e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property = "person.id", column = "personId")})
    List<PushRecord> find(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "countByPerson")
    int countByPerson(@Param("searchCondition") SearchCondition searchCondition, @Param("pushRecord") PushRecord e);

    @SelectProvider(type = Sql.class, method = "findByPerson")
    @Results({@Result(property = "person.id", column = "personId")})
    List<PushRecord> findByPerson(@Param("searchCondition") SearchCondition searchCondition, @Param("pushRecord") PushRecord e);

    class Sql {
        public static String update(final PushRecord e) {
            return new SQL() {{
                UPDATE("pushRecord");
                if (e.getData() != null ) SET("data=#{data}");
                if (e.getStatus() != null) SET("status=#{status}");
                if (e.getUnread() != null) SET("unread=#{unread}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("pushRecord e");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString();
        }

        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("pushRecord e");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }

        public static String countByPerson(@Param("searchCondition") final SearchCondition searchCondition, @Param("pushRecord") final PushRecord e) {
            return new SQL() {{
                SELECT("count(*)"); FROM("pushRecord e");
                WHERE("personId = #{pushRecord.person.id} ");
                if (e.getUnread() != null) WHERE(" unread = #{pushRecord.unread} ");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString();
        }

        public static String findByPerson(@Param("searchCondition") final SearchCondition searchCondition, @Param("pushRecord") final PushRecord e) {
            return new SQL() {{
                SELECT("*"); FROM("pushRecord e");
                WHERE("personId = #{pushRecord.person.id} ");
                if (e.getUnread() != null) WHERE(" unread = #{pushRecord.unread} ");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }

}
