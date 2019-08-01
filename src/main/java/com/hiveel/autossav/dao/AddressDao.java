package com.hiveel.autossav.dao;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressDao {
    @Insert("insert into address(name, content, updateAt) VALUES(#{name}, #{content}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Address e);

    @Delete("delete from address where id=#{id}")
    int delete(Address e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Address e);

    @Select("select * from address where id=#{id}")
    Address findById(Address e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition")SearchCondition searchCondition);
    
    @SelectProvider(type = Sql.class, method = "find")
    List<Address> find(@Param("searchCondition") SearchCondition searchCondition);

    class Sql {
        public static String update(final Address e) {
            return new SQL() {{
                UPDATE("address");
                if (e.getName() != null) SET("name=#{name}");
                if (e.getContent() != null) SET("content=#{content}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("address e");
                if (searchCondition.getName() != null) { 
                    OR().WHERE("name like #{searchCondition.like.name}");
                    OR().WHERE("content like #{searchCondition.like.name}");
                }
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}");   
            }}.toString();
        }
        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("address e");
                if (searchCondition.getName() != null) { 
                    OR().WHERE("name like #{searchCondition.like.name}");
                    OR().WHERE("content like #{searchCondition.like.name}");
                }
                if (searchCondition.getType() != null) WHERE("type=#{searchCondition.type}");        
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
