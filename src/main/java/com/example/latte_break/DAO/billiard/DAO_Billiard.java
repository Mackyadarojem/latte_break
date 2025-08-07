package com.example.latte_break.DAO.billiard;

import com.example.latte_break.BEAN.biliard.BEAN_Billiard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_Billiard {
    JdbcTemplate template;

    public void DAO_Billiard() {
    }

    @Autowired
    public void DAO_Billiard(JdbcTemplate template) {
        this.template = template;
    }

    public int addBilliardSched(String customerName, int hour, int minutes, boolean openHour, String duration) {
        String status = "Not Started";
        String sql = "INSERT into tbl_billiard (customer_name, hour, minutes, open_hour, status, duration) " +
                "VALUES (?,?,?,?,?,?) ";
        return template.update(sql, new Object[]{customerName, hour, minutes, openHour, status, duration});
    }

    public List<BEAN_Billiard> getBilliardSched() {
        String sql = "SELECT id, hour, minutes, open_hour, DATE_FORMAT(start_time, '%Y-%m-%d %h:%i %p') as start_time,\n" +
                "status, DATE_FORMAT(end_time, '%Y-%m-%d %h:%i %p') as end_time, duration, total_time, customer_name\n" +
                "FROM tbl_billiard\n" +
                "ORDER BY id DESC";
        return template.query(sql, new RowMapper<BEAN_Billiard>() {
            @Override
            public BEAN_Billiard mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Billiard bean = new BEAN_Billiard();
                bean.setCustomerName(rs.getString("customer_name"));
                bean.setId(rs.getInt("id"));
                bean.setHour(rs.getInt("hour"));
                bean.setMinutes(rs.getInt("minutes"));
                bean.setOpen_hour(rs.getBoolean("open_hour"));
                bean.setStatus(rs.getString("status"));
                bean.setStart_time(rs.getString("start_time"));
                bean.setEnd_time(rs.getString("end_time"));
                bean.setDuration(rs.getString("duration"));
                bean.setTotal_time(rs.getString("total_time"));
                return bean;
            }
        });
    }

    public int startTime(int id, String duration) {
        String status = "Running";
        String sql = "UPDATE tbl_billiard SET start_time = NOW(), status = ?, end_time = DATE_ADD(NOW(), INTERVAL TIME_TO_SEC(?) SECOND) WHERE id = ?";
        return template.update(sql, status, duration, id);
    }

    public int startTime(int id) {
        String status = "Running";
        String sql = "UPDATE tbl_billiard SET start_time = NOW(), status = ? WHERE id = ?";
        return template.update(sql, status, id);
    }

    public int stopTime(int id) {
        String status = "Stopped";
        String sql = "UPDATE tbl_billiard SET end_time = NOW(), status = ? WHERE id = ?";
        return template.update(sql, status, id);
    }

    public int cancelSched(int id) {
        String status = "Cancelled";
        String sql = "UPDATE tbl_billiard SET status = ? WHERE id = ?";
        return template.update(sql, status, id);
    }

    public int paidSched(int id) {
        String status = "Paid";
        String sql = "UPDATE tbl_billiard SET status = ? WHERE id = ?";
        return template.update(sql, status, id);
    }

}
