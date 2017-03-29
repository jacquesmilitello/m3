package io.m3.sql.apt.ex001;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table( value = "student")
public interface Student {

    @PrimaryKey("id")
    int getId();

    void setId(int id);

    @Column("code")
    @BusinessKey
    String getCode();

    void setCode(String code);

    @Column("age")
    Integer getAge();

    void setAge(Integer integer);

    @Column(value= "overall_rating" , nullable = true)
    Long getOverallRating();

    void setOverallRating(Long value);

}
