package com.example.latte_break.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DAO_Archive {
    JdbcTemplate template;

    public void DAO_Archive() {
    }

    @Autowired
    public void DAO_Archive(JdbcTemplate template) {
        this.template = template;
    }


}
