package com.example.latte_break.DAO.inventory;

import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_ProductList {
    JdbcTemplate template;

    public void DAO_Inventory() {
    }

    @Autowired
    public void DAO_Inventory(JdbcTemplate template) {
        this.template = template;
    }

    public int addProduct(String name, int category_id, String description, String price, String priceMedium, String priceLarge, boolean available) {
        String sql = "INSERT INTO tbl_product (name, category_id, description, price, priceMedium, priceLarge,  available)" +
                " VALUES (?,?,?,?,?,?,?)";
        return template.update(sql, name, category_id, description, price, priceMedium, priceLarge, available);
    }

    public int updateProduct(String name, int category_id, String description, String price, boolean available, int id) {
        String sql = "UPDATE tbl_product SET name = ?, category_id = ?, description = ?, price = ?, available = ? WHERE id = ?";
        return template.update(sql, name, category_id, description, price, available, id);
    }

    public int deleteProduct(int id) {
        String sql = "DELETE from tbl_product WHERE id = ?";
        return template.update(sql, id);
    }

    public List<BEAN_ProductList> getCategory() {
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

    public List<BEAN_ProductList> getAllProduct(String name, int category_id) {

        String sql = "SELECT tp.*, rc.category FROM tbl_product tp " +
                "LEFT JOIN ref_category rc ON tp.category_id = rc.id " +
                "WHERE (? IS NULL OR tp.name LIKE ?) " +
                "AND (? = 0 OR tp.category_id LIKE ?) " +
                "AND tp.archive_at IS NULL";

        name = (name != null && !name.trim().isEmpty()) ? "%" + name + "%" : null;

        return template.query(sql, new RowMapper<BEAN_ProductList>() {
            @Override
            public BEAN_ProductList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ProductList bean = new BEAN_ProductList();
                bean.setCategory_id(rs.getInt("category_id"));
                bean.setId(rs.getInt("id"));
                bean.setPrice(rs.getString("price"));
                bean.setPriceMedium(rs.getString("priceMedium"));
                bean.setPriceLarge(rs.getString("priceLarge"));
                bean.setAvailable(rs.getBoolean("available"));
                bean.setDescription(rs.getString("description"));
                bean.setName(rs.getString("name"));
                bean.setCategory_name(rs.getString("category"));
                return bean;
            }
        }, new Object[]{name, name, category_id, category_id});
    }

    public int archiveProduct(int id, String username) {
        String sql = "UPDATE tbl_product SET archive_at = NOW(), archive_by = ? WHERE id = ?";
        return template.update(sql, username, id);
    }

    public List<BEAN_ProductList> getArchiveProduct(String name, int category_id) {

        String sql = "SELECT tp.*, rc.category FROM tbl_product tp " +
                "LEFT JOIN ref_category rc ON tp.category_id = rc.id " +
                "WHERE (? IS NULL OR tp.name LIKE ?) " +
                "AND (? = 0 OR tp.category_id LIKE ?) " +
                "AND tp.archive_at IS NOT NULL";

        name = (name != null && !name.trim().isEmpty()) ? "%" + name + "%" : null;

        return template.query(sql, new RowMapper<BEAN_ProductList>() {
            @Override
            public BEAN_ProductList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ProductList bean = new BEAN_ProductList();
                bean.setCategory_id(rs.getInt("category_id"));
                bean.setId(rs.getInt("id"));
                bean.setPrice(rs.getString("price"));
                bean.setAvailable(rs.getBoolean("available"));
                bean.setDescription(rs.getString("description"));
                bean.setName(rs.getString("name"));
                bean.setCategory_name(rs.getString("category"));
                bean.setArchive_at(rs.getString("archive_at"));
                bean.setArchive_by(rs.getString("archive_by"));
                return bean;
            }
        }, new Object[]{name, name, category_id, category_id});
    }

    public int restoreProduct(String username, int id) {
        String sql = "UPDATE tbl_product SET archive_at = NULL, archive_by = NULL, updated_at = NOW(), updated_by = ? " +
                " WHERE id = ? ";
        return template.update(sql, username, id);
    }

    public int changeAvailability(Boolean available, int id){
        String sql = "Update tbl_product SET available = ? WHERE id = ? ";
        return template.update(sql, available, id);
    }

}
