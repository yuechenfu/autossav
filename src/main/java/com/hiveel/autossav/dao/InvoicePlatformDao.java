package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoicePlatform;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoicePlatformDao {
    @Insert("insert into invoicePlatform(price, date, updateAt) VALUES(#{price}, #{date}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(InvoicePlatform e);

    @Delete("delete from invoicePlatform where id=#{id}")
    int delete(InvoicePlatform e);

    @Delete("delete from invoicePlatform")
    int deleteAll();

    @UpdateProvider(type = Sql.class, method = "update")
    int update(InvoicePlatform e);

    @Select("select * from invoicePlatform where id=#{id}")
    InvoicePlatform findById(InvoicePlatform e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    List<InvoicePlatform> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final InvoicePlatform e) {
            return new SQL() {{
                UPDATE("invoicePlatform");
                if (e.getPrice() != null) SET("price=#{price}");
                if (e.getDate() != null) SET("date=#{date}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("invoicePlatform");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");   
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("invoicePlatform e");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");   
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
