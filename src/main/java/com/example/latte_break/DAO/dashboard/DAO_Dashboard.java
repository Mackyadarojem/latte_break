package com.example.latte_break.DAO.dashboard;

import com.example.latte_break.BEAN.dashboard.BEAN_Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_Dashboard {
    JdbcTemplate template;

    public void DAO_Dashboard() {
    }

    @Autowired
    public void DAO_Dashboard(JdbcTemplate template) {
        this.template = template;
    }

    public List<BEAN_Dashboard> getTopProducts(){
        String sql = "WITH ranked_products AS (\n" +
                "    SELECT \n" +
                "        p.id,\n" +
                "        p.name, \n" +
                "        p.category_id, \n" +
                "        COUNT(ip.product_id) AS product_count,\n" +
                "        ROW_NUMBER() OVER (\n" +
                "            PARTITION BY p.category_id \n" +
                "            ORDER BY COUNT(ip.product_id) DESC\n" +
                "        ) AS rn\n" +
                "    FROM tbl_invoice_product ip\n" +
                "    INNER JOIN tbl_product p \n" +
                "        ON ip.product_id = p.id\n" +
                "    GROUP BY p.id, p.name, p.category_id\n" +
                ")\n" +
                "SELECT id, name, category_id, product_count, rn\n" +
                "FROM ranked_products\n" +
                "WHERE rn <= 3;\n";
        return template.query(sql, new RowMapper<BEAN_Dashboard>() {
            @Override
            public BEAN_Dashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Dashboard bean = new BEAN_Dashboard();
                bean.setProduct_name(rs.getString("name"));
                bean.setCategory_id(rs.getInt("category_id"));
                bean.setRn(rs.getInt("rn"));
                return bean;
            }
        });
    }

    public List<BEAN_Dashboard> getCategoryList(){
        String sql = "SELECT id, category FROM ref_category \n" +
                "WHERE deleted_at IS NULL";
        return template.query(sql, new RowMapper<BEAN_Dashboard>() {
            @Override
            public BEAN_Dashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Dashboard bean = new BEAN_Dashboard();
                bean.setCategory_name(rs.getString("category"));
                bean.setCategory_id(rs.getInt("id"));
                return bean;
            }
        });
    }

    public List<BEAN_Dashboard> getRecentTransaction(){
        String sql = "SELECT * FROM tbl_transaction \n" +
                "ORDER BY id DESC LIMIT 3";
        return template.query(sql, new RowMapper<BEAN_Dashboard>() {
            @Override
            public BEAN_Dashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Dashboard bean = new BEAN_Dashboard();
                bean.setTransaction(rs.getString("transaction"));
                bean.setTransacted_by(rs.getString("created_by"));
                bean.setProducts(rs.getString("products"));
                bean.setDate(rs.getString("created_at"));
                return bean;
            }
        });
    }
}
