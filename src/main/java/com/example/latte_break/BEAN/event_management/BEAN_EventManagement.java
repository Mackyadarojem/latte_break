package com.example.latte_break.BEAN.event_management;

import lombok.Data;

@Data
public class BEAN_EventManagement {
    private int id;
    private String name;
    private String role;

    private String event_name;
    private String purpose;
    private String date;
    private String time;
    private String participants_ids;
    private String participants_name;


    private String date_from;
    private String date_to;
}
