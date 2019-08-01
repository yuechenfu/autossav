package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoiceExam;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceExamDao {
    @Insert("insert into invoiceExam(price, date, updateAt) VALUES(#{price}, #{date}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(InvoiceExam e);

    @Delete("delete from invoiceExam where id=#{id}")
    int delete(InvoiceExam e);

    @Delete("delete from invoiceExam")
    int deleteAll();

    @UpdateProvider(type = Sql.class, method = "update")
    int update(InvoiceExam e);

    @Select("select * from invoiceExam where id=#{id}")
    InvoiceExam findById(InvoiceExam e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    List<InvoiceExam> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final InvoiceExam e) {
            return new SQL() {{
                UPDATE("invoiceExam");
                if (e.getPrice() != null) SET("price=#{price}");
                if (e.getDate() != null) SET("date=#{date}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{            	
                SELECT("count(*)"); FROM("invoiceExam");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");   
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("invoiceExam e");
                if (searchCondition.getMinDate() != null) WHERE("date >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("date <= #{searchCondition.maxDate}");   
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
