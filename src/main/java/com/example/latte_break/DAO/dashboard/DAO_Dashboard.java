package com.example.latte_break.DAO.dashboard;

import com.example.latte_break.BEAN.dashboard.BEAN_Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DAO_Dashboard {
    JdbcTemplate template;

    public void DAO_Dashboard() {
    }

    @Autowired
    public void DAO_Dashboard(JdbcTemplate template) {
        this.template = template;
    }

}
