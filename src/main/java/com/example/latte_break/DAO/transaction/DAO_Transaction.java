package com.example.latte_break.DAO.transaction;

import com.example.latte_break.BEAN.transaction.BEAN_Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_Transaction {
    JdbcTemplate template;

    public void DAO_Transaction() {
    }

    @Autowired
    public void DAO_Transaction(JdbcTemplate template) {
        this.template = template;
    }

    public List<BEAN_Transaction> getTransactionList(String transaction, String date_to, String date_from) {
        String sql = "SELECT * FROM tbl_transaction\n" +
                "WHERE (? = '' OR transaction = ?)\n" +
                "AND ( (? = '' OR ? = '') OR (created_at BETWEEN ? AND ?) )";
        return template.query(sql, new Object[]{transaction, transaction, date_from, date_to, date_from, date_to}, new RowMapper<BEAN_Transaction>() {
            @Override
            public BEAN_Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Transaction bean = new BEAN_Transaction();
                bean.setTransaction(rs.getString("transaction"));
                bean.setProducts(rs.getString("products"));
                bean.setCreated_at(rs.getString("created_at"));
                bean.setCreated_by(rs.getString("created_by"));
                return bean;
            }
        });
    }
}
