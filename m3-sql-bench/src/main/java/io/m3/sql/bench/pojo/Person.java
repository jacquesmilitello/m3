package io.m3.sql.bench.pojo;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

import java.sql.Date;

@Table("person")
public interface Person {

    @PrimaryKey("id")
    int getId();

    void setId(int id);

    @Column("first_name")
    String getFirstName() ;

    void setFirstName(String firstName);

    @Column("middle_name")
    String getMiddleName();

    void setMiddleName(String middleName);

    @Column("last_name")
    String getLastName();

    void setLastName(String lastName);

    @Column("street")
    String getStreet();

    void setStreet(String street);

    @Column("city")
    String getCity();

    void setCity(String city);

    @Column("state")
    String getState();

    void setState(String state);

    @Column("zip")
    String getZip();

    void setZip(String zip);

    @Column("country")
    public String getCountry();

    public void setCountry(String country);

    @Column("phone")
    public String getPhone();

    public void setPhone(String phone);

    @Column("email")
    public String getEmail();

    public void setEmail(String email);

    @Column("birth_date")
    public Date getBirthDate();

    public void setBirthDate(Date birthDate);

    @Column("join_date")
    public Date getJoinDate();

    public void setJoinDate(Date joinDate);

    @Column("last_login_date")
    public Date getLastLoginDate();

    public void setLastLoginDate(Date lastLoginDate);

    @Column("login_count")
    public int getLoginCount();

    public void setLoginCount(int loginCount);
}
