package io.m3.sql.apt.ex004;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;

import java.sql.Date;

@Table(value = "sales_record", pageable = true)
public interface SalesRecord {

    @PrimaryKey("id")
    int getId();

    void setId(int id);

    @Column("region")
    String getRegion();

    void setRegion(String region);

    @Column("country")
    String getCountry();

    void setCountry(String country);

    @Column("item_type")
    String getItemType();

    void setItemType(String itemType);

    @Column("sales_channel")
    String getSales();

    void setSales(String sales);

    @Column("order_priority")
    String getOrderPriority();

    void setOrderPriority(String priority);

    @Column("order_date")
    Date getOrderDate();

    void setOrderDate(Date date);

    @Column("order_id")
    long getOrderId();

    void setOrderId(long orderId);

    @Column("ship_date")
    Date getShipDate();

    void setShipDate(Date date);

    @Column("unit_sold")
    double getUnitSold();

    void setUnitSold(double unitSold);

    @Column("unit_price")
    double getUnitPrice();

    void setUnitPrice(double unitPrice);

    @Column("unit_cost")
    double getUnitCost();

    void setUnitCost(double unitCost);

    @Column("total_revenue")
    double getTotalRevenue();

    void setTotalRevenue(double totalRevenue);

    @Column("total_cost")
    double getTotalCost();

    void setTotalCost(double totalCost);

    @Column("total_profit")
    double getTotalProfit();

    void setTotalProfit(double totalProfit);

}