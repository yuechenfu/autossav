package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoiceExamQuote;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceExamQuoteDao {
    @Insert("insert into invoiceExamQuote(invoiceExamId, name, labor, part, tax, updateAt) VALUES(#{invoiceExam.id}, #{name}, #{labor}, #{part}, #{tax} ,#{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(InvoiceExamQuote e);

    @Delete("delete from invoiceExamQuote where id=#{id}")
    int delete(InvoiceExamQuote e);
    
    @Delete("delete from invoiceExamQuote")
    int deleteAll();

    @UpdateProvider(type = Sql.class, method = "update")
    int update(InvoiceExamQuote e);

    @Select("select * from invoiceExamQuote where id=#{id}")
    @Results( @Result(property="invoiceExam.id", column="invoiceExamId") )
    InvoiceExamQuote findById(InvoiceExamQuote e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "find")
    @Results( @Result(property="invoiceExam.id", column="invoiceExamId") )
    List<InvoiceExamQuote> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final InvoiceExamQuote e) {
            return new SQL() {{
                UPDATE("invoiceExamQuote");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getLabor() != null) SET("labor=#{labor}");
                if (e.getPart() != null) SET("part=#{part}");
                if (e.getTax() != null) SET("tax=#{tax}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)");  FROM("invoiceExamQuote e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");   
                if (searchCondition.getGroup() != null) WHERE("invoiceExamId=#{searchCondition.group}");
            }}.toString();
        }

        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("invoiceExamQuote e");
                if (searchCondition.getName() != null) WHERE("name like #{searchCondition.like.name}");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}"); 
                if (searchCondition.getGroup() != null) WHERE("invoiceExamId=#{searchCondition.group}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
