package com.example.latte_break.BEAN.pos;

import lombok.Data;

import java.util.List;

@Data
public class BEAN_InvoiceRequest {
    private List<BEAN_POS> productList;
    private String total_amount;
    private String cash_tendered;
    private String change;
    private String mode_of_payment;
    private String referenceNo;
    private String senderName;
    private String serviceType;
    private String subTotal;
    private String discount;
    private String transaction;
}
