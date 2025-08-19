package com.example.latte_break.DAO.reports;

import com.example.latte_break.BEAN.reports.BEAN_Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_Report {
    JdbcTemplate template;

    public void DAO_Report() {
    }

    @Autowired
    public void DAO_Report(JdbcTemplate template) {
        this.template = template;
    }

    public List<BEAN_Report> getItemList(int category_id , String date_from , String date_to) {
        System.out.println(date_from);
        System.out.println(date_to);
        String sql = "SELECT \n" +
                "    CONCAT('Item','-', DATE_FORMAT(i.created_at, '%Y%m%d'), '-', LPAD(i.id, 4, '0')) AS item_code,\n" +
                "    i.id,\n" +
                "\tCONCAT('Batch','-', DATE_FORMAT(b.timestamp, '%Y%m%d'), '-', LPAD(b.id, 4, '0')) AS batch_code,\n" +
                "\tname,\n" +
                "\tc.category,\n" +
                "    i.unit_measurement,\n" +
                "    b.quantity as stock,\n" +
                "    i.expire,\n" +
                "    b.expiration_date\n" +
                "FROM tbl_item i\n" +
                "INNER JOIN ref_category_item c ON i.category_id = c.id\n" +
                "LEFT JOIN tbl_batch b ON i.id = b.item_id\n" +
                "WHERE b.quantity != 0\n" +
                "AND (? = 0 OR i.category_id = ?)\n" +
                "AND ( ( ? = '' OR ? = '' ) OR ( DATE_FORMAT(b.timestamp, '%Y%m%d') >= ? AND DATE_FORMAT(b.timestamp, '%Y%m%d') <=  ? ) );";
        return template.query(sql,new Object[]{category_id, category_id, date_from, date_to, date_from, date_to}, new RowMapper<BEAN_Report>() {
            @Override
            public BEAN_Report mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Report bean = new BEAN_Report();
                bean.setItem_code(rs.getString("item_code"));
                bean.setBatch_code(rs.getString("batch_code"));
                bean.setItem_name(rs.getString("name"));
                bean.setCategory(rs.getString("category"));
                bean.setExpiration_date(rs.getString("expiration_date"));
                bean.setStock(rs.getString("stock"));
                bean.setUnit_measurement(rs.getString("unit_measurement"));
                return bean;
            }
        });
    }
}
