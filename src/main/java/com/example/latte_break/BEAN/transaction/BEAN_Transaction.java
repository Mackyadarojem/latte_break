package com.example.latte_break.BEAN.transaction;

import lombok.Data;

@Data
public class BEAN_Transaction {
    private String transaction;
    private String products;
    private String created_at;
    private String created_by;

    private String date_from;
    private String date_to;
}
