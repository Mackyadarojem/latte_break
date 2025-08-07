package com.example.latte_break.DAO.inventory;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_ArchiveItemList {
    JdbcTemplate template;

    public void DAO_ArchiveItemList(){
    }

    @Autowired
    public void DAO_ArchiveItemList(JdbcTemplate template){
        this.template = template;
    }

    public List<BEAN_ItemList> getAllArchivedItem(String name, int category_id){
        String sql = "SELECT i.*, c.category from tbl_item i " +
                " LEFT JOIN ref_category_item c ON i.category_id = c.id " +
                " WHERE (archive_at IS NOT NULL OR archive_at != '')" +
                " AND (? IS NULL OR name LIKE ?) " +
                " AND  ( ? = 0 OR category_id LIKE ?) ";
        name = (name != null && !name.trim().isEmpty()) ? "%" + name + "%" : null;
        return template.query(sql, new RowMapper<BEAN_ItemList>() {
            @Override
            public BEAN_ItemList mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_ItemList bean = new BEAN_ItemList();
                bean.setName(rs.getString("name"));
                bean.setId(rs.getInt("id"));
                bean.setDescription(rs.getString("description"));
                bean.setUnit(rs.getString("unit_measurement"));
                bean.setBrand(rs.getString("brand"));
                bean.setArchive_at(rs.getString("archive_at"));
                bean.setArchive_by(rs.getString("archive_by"));
                bean.setCritical_quantity(rs.getString("critical_quantity"));
                bean.setCategory_name(rs.getString("category"));
                bean.setCategory_id(rs.getInt("category_id"));
                return bean;
            }
        }, new Object[]{name, name, category_id, category_id});
    }

    public int restoreItem(String username, int id){
        String sql = "UPDATE tbl_item SET archive_at = NULL, archive_by = NULL, updated_at = NOW(), updated_by = ? " +
                " WHERE id = ? ";
        return template.update(sql,username, id);
    }

    public int deleteItem(int id){
        String sql = "DELETE from tbl_item WHERE id = ?";
        return template.update(sql, id);
    }


}
