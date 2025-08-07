package com.example.latte_break.DAO.inventory;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_ItemList {

    JdbcTemplate template;

    public void DAO_ItemList() {
    }

    @Autowired
    public void DAO_ItemList(JdbcTemplate template) {
        this.template = template;
    }

    public List<BEAN_ItemList> getCategory() {
        String sql = "SELECT id, category FROM ref_category_item";
        return template.query(sql, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setCategory_id(rs.getInt("id"));
                bean.setCategory_name(rs.getString("category"));
                return bean;
            }
        });
    }

    public int addItem(String name, String brand, int category_id, String critical_quantity, String unit_measurement, boolean expire, String username, String description) {
        String sql = "INSERT INTO tbl_item (name, brand, category_id, critical_quantity, unit_measurement, expire, created_by, created_at, description) " +
                "VALUES (?,?,?,?,?,?,?,NOW(),?)";
        return template.update(sql, name, brand, category_id, critical_quantity, unit_measurement, expire, username, description);
    }

    public int updateItem(String name, String brand, int category_id, String critical_quantity, String unit_measurement, boolean expire, String username, String description, int id) {
        String sql = "UPDATE tbl_item SET name = ?, brand = ?, category_id = ?, critical_quantity = ?, unit_measurement = ?, expire = ?, updated_by = ?, description = ?, updated_at = NOW()  " +
                " WHERE id = ?";
        return template.update(sql, name, brand, category_id, critical_quantity, unit_measurement, expire, username, description, id);
    }

    public List<BEAN_ItemList> getAllItem(String name, int category_id) {
        System.out.println(category_id);
        String sql = "SELECT i.*, ci.category FROM tbl_item as i " +
                " LEFT JOIN ref_category_item ci ON i.category_id = ci.id " +
                " WHERE (archive_at IS NULL " +
                " OR archive_at = '')     " +
                " AND (? IS NULL OR name LIKE ?)" +
                " AND  ( ? = 0 OR category_id LIKE ?) ";
        name = (name != null && !name.trim().isEmpty()) ? "%" + name + "%" : null;
        return template.query(sql, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setCategory_id(rs.getInt("category_id"));
                bean.setId(rs.getInt("id"));
                bean.setCritical_quantity(rs.getString("critical_quantity"));
                bean.setBrand(rs.getString("brand"));
                bean.setName(rs.getString("name"));
                bean.setUnit(rs.getString("unit_measurement"));
                bean.setDescription(rs.getString("description"));
                bean.setExpire(rs.getBoolean("expire"));
                bean.setCategory_name(rs.getString("category"));
                bean.setCreated_at(rs.getString("created_at"));
                bean.setCreated_by(rs.getString("created_by"));
                bean.setUpdated_by(rs.getString("updated_by"));
                bean.setUpdated_at(rs.getString("updated_at"));
                bean.setStock(rs.getString("stock"));
                return bean;
            }
        }, new Object[]{name, name, category_id, category_id});
    }

    public int archiveItem(int id, String username) {
        String sql = "UPDATE tbl_item SET archive_at = NOW(), archive_by = ? " +
                "WHERE id = ? ";
        return template.update(sql, username, id);
    }

    public int addBatch(int id, String quantity, String expiration_date, String username) {
        String sql = "INSERT INTO tbl_batch (item_id, quantity, expiration_date, encoded_by) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update((Connection connection) -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"batch_id"});
            ps.setInt(1, id);
            ps.setString(2, quantity);
            ps.setString(3, expiration_date);
            ps.setString(4, username);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue(); // Return the generated batch_id
    }

    public int stockIn(int batch_id, int id, String quantity, String expiration_date, String username) {
        String sql = "INSERT INTO tbl_stockIn (batch_id, item_id, quantity, expiration_date, encoded_by)" +
                "VALUES (?,?,?,?,?)";
        return template.update(sql, batch_id, id, quantity, expiration_date, username);
    }

    public int updateStock(int id, String quantity) {
        String sql = "UPDATE tbl_item \n" +
                "SET stock = stock + ? \n" +
                "WHERE id = ?;";
        return template.update(sql, quantity, id);
    }

    public List<BEAN_ItemList> getStockInHistory() {
        String sql = "SELECT s.*, i.*,c.category from tbl_stockIn as s " +
                "LEFT JOIN tbl_item i ON s.item_id = i.id " +
                "LEFT JOIN ref_category_item c ON i.category_id = c.id " +
                "ORDER BY timestamp desc";
        return template.query(sql, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setName(rs.getString("name"));
                bean.setCategory_name(rs.getString("category"));
                bean.setBatch_id(rs.getInt("batch_id"));
                bean.setDescription(rs.getString("description"));
                bean.setEncoded_by(rs.getString("encoded_by"));
                bean.setEncoded_date(rs.getString("timestamp"));
                bean.setQuantity(rs.getString("quantity"));
                bean.setExpiration_date(rs.getString("expiration_date"));
                bean.setBrand(rs.getString("brand"));
                return bean;
            }
        });
    }

    public List<BEAN_ItemList> getBatchItemList() {
        String sql = "Select b.id as batch_id, i.id as item_id,i.name as item_name, c.category as category_name, b.quantity as availStock, i.description, i.brand, b.expiration_date, i.unit_measurement," +
                " b.encoded_by, b.timestamp, b.modified_by, b.modified_at " +
                " FROM tbl_batch b  " +
                " LEFT JOIN tbl_item i ON b.item_id = i.id" +
                " LEFT JOIN ref_category_item c ON i.category_id =  c.id" +
                " WHERE b.quantity != 0";
        return template.query(sql, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setBatch_id(rs.getInt("batch_id"));
                bean.setId(rs.getInt("item_id"));
                bean.setName(rs.getString("item_name"));
                bean.setCategory_name(rs.getString("category_name"));
                bean.setQuantity(rs.getString("availStock"));
                bean.setDescription(rs.getString("description"));
                bean.setBrand(rs.getString("brand"));
                bean.setExpiration_date(rs.getString("expiration_date"));
                bean.setUnit(rs.getString("unit_measurement"));
                bean.setEncoded_date(rs.getString("timestamp"));
                bean.setEncoded_by(rs.getString("encoded_by"));
                bean.setUpdated_at(rs.getString("modified_at"));
                bean.setUpdated_by(rs.getString("modified_by"));
                return bean;
            }
        });
    }

    public int deductBatch(int batch_id, String quantity, String username) {
        String sql = "UPDATE tbl_batch SET quantity = quantity - ?, modified_by = ?, modified_at = NOW() " +
                "WHERE id = ?";
        return template.update(sql, quantity, username, batch_id);
    }

    public int stockOut(int batch_id, int item_id, String quantity, String expiration_date, String username) {
        String sql = "INSERT into tbl_stockOut (batch_id, item_id, quantity, expiration_date, encoded_by) " +
                "VALUES (?,?,?,?,?) ";
        return template.update(sql, batch_id, item_id, quantity, expiration_date, username);
    }

    public int deductStock(int item_id, String quantity) {
        String sql = "Update tbl_item SET stock = stock - ? " +
                "WHERE id = ? ";
        return template.update(sql, quantity, item_id);
    }

    public List<BEAN_ItemList> getStockOutHistory() {
        String sql = "SELECT s.*, c.category, i.name, i.description, i.brand from tbl_stockOut s " +
                " LEFT JOIN tbl_item as i ON s.item_id = i.id " +
                " LEFT JOIN ref_category_item c ON i.category_id = c.id ORDER BY timestamp desc";
        return template.query(sql, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setQuantity(rs.getString("quantity"));
                bean.setName(rs.getString("name"));
                bean.setCategory_name(rs.getString("category"));
                bean.setEncoded_date(rs.getString("timestamp"));
                bean.setEncoded_by(rs.getString("encoded_by"));
                bean.setBatch_id(rs.getInt("batch_id"));
                bean.setDescription(rs.getString("description"));
                bean.setBrand(rs.getString("brand"));
                bean.setExpiration_date(rs.getString("expiration_date"));
                return bean;
            }
        });
    }

    public List<BEAN_ItemList> getBatchItemListById(int id) {
        String sql = "Select b.id as batch_id, i.id as item_id,i.name as item_name, c.category as category_name, b.quantity as availStock, i.description, i.brand, b.expiration_date," +
                " i.unit_measurement, b.encoded_by, b.timestamp, b.modified_by, b.modified_at  " +
                " FROM tbl_batch b  " +
                " LEFT JOIN tbl_item i ON b.item_id = i.id" +
                " LEFT JOIN ref_category_item c ON i.category_id =  c.id " +
                " WHERE b.item_id = ?";
        return template.query(sql, new Object[]{id}, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setBatch_id(rs.getInt("batch_id"));
                bean.setId(rs.getInt("item_id"));
                bean.setName(rs.getString("item_name"));
                bean.setCategory_name(rs.getString("category_name"));
                bean.setQuantity(rs.getString("availStock"));
                bean.setDescription(rs.getString("description"));
                bean.setBrand(rs.getString("brand"));
                bean.setExpiration_date(rs.getString("expiration_date"));
                bean.setUnit(rs.getString("unit_measurement"));
                bean.setEncoded_date(rs.getString("timestamp"));
                bean.setEncoded_by(rs.getString("encoded_by"));
                bean.setUpdated_at(rs.getString("modified_at"));
                bean.setUpdated_by(rs.getString("modified_by"));
                return bean;
            }
        });
    }

}
