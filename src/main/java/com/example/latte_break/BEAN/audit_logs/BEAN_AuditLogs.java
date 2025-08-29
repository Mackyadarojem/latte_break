package com.example.latte_break.BEAN.audit_logs;

import lombok.Data;

@Data
public class BEAN_AuditLogs {
    int id;
    String username;
    String full_name;
    String action;
    String timestamp;
}
