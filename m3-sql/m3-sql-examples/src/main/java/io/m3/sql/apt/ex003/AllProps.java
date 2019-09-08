package io.m3.sql.apt.ex003;


import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

@Table(value = "all_prop")
public interface AllProps {


    @PrimaryKey("id")
    int getId();

    void setId(int id);

    @Column("string")
    String getString();

    void setString(String code);

    @Column("byte")
    byte getByte();

    void setByte(byte b);

    @Column("o_byte")
    Byte getObjectByte();

    void setObjectByte(Byte b);

    @Column("short")
    short getShort();

    void setShort(short s);

    @Column("o_short")
    Short getObjectShort();

    void setObjectShort(Short s);

    @Column("int")
    int getInt();

    void setInt(int i);

    @Column("o_int")
    Integer getObjectInt();

    void setObjectInt(Integer i);

    @Column("long")
    long getLong();

    void setLong(long l);

    @Column("o_long")
    Long getObjectLong();

    void setObjectLong(Long l);

    @Column("float")
    float getFloat();

    void setFloat(float l);

    @Column("o_float")
    float getObjectFloat();

    void setObjectFloat(float l);

    @Column("double")
    double getDouble();

    void setDouble(double l);

    @Column("o_double")
    Double getObjectDouble();

    void setObjectDouble(Double l);

    @Column("boolean")
    boolean getBoolean();

    void setBoolean(boolean l);

    @Column("o_boolean")
    Boolean getObjectBoolean();

    void setObjectBoolean(Boolean l);

}
