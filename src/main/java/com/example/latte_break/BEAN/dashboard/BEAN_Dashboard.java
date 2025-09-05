package com.example.latte_break.BEAN.dashboard;

import lombok.Data;

@Data
public class BEAN_Dashboard {
    private String date;
    private String time;
    private String event_name;
    private int id;
    private String purpose;

//    products

    private int category_id;
    private String category_name;
    private String product_name;
    private int rn;

//    transaction

    private String transaction;
    private String transacted_by;
    private String products;
}
