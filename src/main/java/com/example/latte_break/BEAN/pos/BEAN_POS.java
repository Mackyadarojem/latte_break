package com.example.latte_break.BEAN.pos;

import lombok.Data;

import java.util.List;

@Data
public class BEAN_POS {
    private String productName;
    private String totalPrice;
    private String quantity;
    private String sugarLevel;
    private List<BEAN_AddOns> addOns;
    private String timeValue;
    private int billiard_id;

    private int productId;
    private String discount;
    private String size;
    private String category_name;
}
