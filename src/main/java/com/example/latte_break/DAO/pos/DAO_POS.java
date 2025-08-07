package com.example.latte_break.DAO.pos;

import com.example.latte_break.BEAN.BEAN_Account;
import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class DAO_POS {
    JdbcTemplate template;

    public void DAO_POS() {
    }

    @Autowired
    public void DAO_POS(JdbcTemplate template) {
        this.template = template;
    }

    public List<BEAN_ProductList> getAllProduct() {
        String sql = "SELECT p.name, p.id, p.category_id, c.category, p.description, p.price, p.priceMedium, p.priceLarge, c.drink, c.size " +
                "FROM `tbl_product` p " +
                "JOIN ref_category c ON p.category_id = c.id " +
                "WHERE available != 0 ";
        return template.query(sql, new RowMapper<BEAN_ProductList>() {
            @Override
            public BEAN_ProductList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ProductList bean = new BEAN_ProductList();
                bean.setId(rs.getInt("id"));
                bean.setDescription(rs.getString("description"));
                bean.setName(rs.getString("name"));
                bean.setPrice(rs.getString("price"));
                bean.setPriceMedium(rs.getString("priceMedium"));
                bean.setPriceLarge(rs.getString("priceLarge"));
                bean.setCategory_name(rs.getString("category"));
                bean.setCategory_id(rs.getInt("category_id"));
                bean.setDrink(rs.getBoolean("drink"));
                bean.setSize(rs.getInt("size"));
                return bean;
            }
        });
    }

    public List<BEAN_ProductList> getAllCategory() {
        String sql = "SELECT id, category FROM ref_category";
        return template.query(sql, new RowMapper<BEAN_ProductList>() {
            @Override
            public BEAN_ProductList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ProductList bean = new BEAN_ProductList();
                bean.setCategory_id(rs.getInt("id"));
                bean.setCategory_name(rs.getString("category"));
                return bean;
            }
        });
    }

    public int verifyAdminPassword(String password) {
        String sql = "SELECT id FROM tbl_user " +
                "WHERE password = ? " +
                "AND date_archived IS NULL " +
                "AND role_id = 1 " +
                "LIMIT 1";
        try {
            Integer id = template.queryForObject(sql, new Object[]{password}, Integer.class);
            return (id != null) ? 1 : -1;
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public int addInvoiceRecord(String invoice_number, String products, String total_amount,
                                String cash_tendered, String mode_of_payment, String change, String dining_type,
                                String cashier, String discount,
                                String subTotal, String reference_no, String sender_name) {
        String sql = "INSERT INTO tbl_invoice (invoice_no, products, total_amount, " +
                "cash_tendered, change_amount, mode_of_payment, dining_type, " +
                "discount, cashier, created_at, subTotal, reference_no, sender_name)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?)";
        return template.update(sql, invoice_number, products, total_amount, cash_tendered, change, mode_of_payment, dining_type, discount, cashier, subTotal, reference_no, sender_name);
    }

    public String generateInvoiceNumber() {
        String todayStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String formattedDate = todayStr.substring(0, 4) + "-" + todayStr.substring(4, 6) + "-" + todayStr.substring(6, 8);
        java.sql.Date today = java.sql.Date.valueOf(formattedDate);

        // Step 1: Insert or update the counter for today
        template.update(
                "INSERT INTO invoice_sequence (seq_date, counter) VALUES (?, 1) " +
                        "ON DUPLICATE KEY UPDATE counter = counter + 1",
                today
        );

        // Step 2: Retrieve the updated counter
        Integer counter = template.queryForObject(
                "SELECT counter FROM invoice_sequence WHERE seq_date = ? LIMIT 1",
                new Object[]{today},
                Integer.class
        );
        // Step 3: Format and return the invoice number
        return String.format("INV-%s-%03d", todayStr, counter);
    }

    public int addTransaction(String products, String created_by, String transaction) {
        String sql = "INSERT into tbl_transaction (transaction, products, created_by, created_at)\n" +
                "VALUES (?, ?, ?, NOW())";
        return template.update(sql, transaction, products, created_by);
    }
}
