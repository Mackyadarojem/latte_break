package com.example.latte_break.BEAN.inventory;

import lombok.Data;

@Data
public class BEAN_ProductList {
    private int id;
    private int category_id;
    private String category_name;
    private String name;
    private String description;
    private  boolean available;
    private String method;
    private String archive_at;
    private String archive_by;
    private String price;
    private String priceMedium;
    private String priceLarge;
    private boolean drink;
    private int size;
}
