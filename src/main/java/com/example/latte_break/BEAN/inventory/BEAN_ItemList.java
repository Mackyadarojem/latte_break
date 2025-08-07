package com.example.latte_break.BEAN.inventory;

import lombok.Data;

import java.util.List;

@Data
public class BEAN_ItemList {
    private String stock;
    private int id;
    private String name;
    private int category_id;
    private String category_name;
    private String quantity;
    private String unit;
    private boolean expire;
    private String expiration_date;
    private String description;
    private String brand;
    private String created_by;
    private String created_at;
    private String updated_by;
    private String updated_at;
    private String method;
    private String archive_by;
    private String archive_at;
    private String critical_quantity;
    private String encoded_by;
    private String encoded_date;
    private int batch_id;
}
