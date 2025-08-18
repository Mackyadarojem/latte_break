package com.example.latte_break.BEAN.reports;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BEAN_Report {
    private String item_code;
    private String batch_code;
    private String item_name;
    private String category;
    private String stock;
    private String expiration_date;
    private String unit_measurement;
}
