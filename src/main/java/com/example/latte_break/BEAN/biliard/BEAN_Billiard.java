package com.example.latte_break.BEAN.biliard;

import lombok.Data;

@Data
public class BEAN_Billiard {
    private String customerName;
    private int hour;
    private int minutes;
    private boolean open_hour;
    private String start_time;
    private String end_time;
    private int table_number;
    private String status;
    private int id;
    private String duration;
    private String total_time;
}
