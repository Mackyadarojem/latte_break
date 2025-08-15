package com.example.latte_break.DAO;

import com.example.latte_break.BEAN.BEAN_Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_Login {
    JdbcTemplate template;

    public void DAO_Login() {
    }

    @Autowired
    public void DAO_Login(JdbcTemplate template) {
        this.template = template;
    }

    public int checkUser(String username, String password) {
        String sql = "Select id from tbl_user WHERE UPPER(username) = ? AND password = ? AND date_archived IS NULL";
        try {
            return template.queryForObject(sql, new Object[]{username.toUpperCase(), password}, Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }

    public BEAN_Account getInfo(int id) {
        String sql =
        "SELECT u.*, " +
                "       r.role_name, " +
                "       UPPER(CONCAT_WS(' ', u.first_name, u.middle_name, u.last_name)) AS name " +
                "FROM tbl_user u " +
                "LEFT JOIN ref_role r ON u.role_id = r.id " +
                "WHERE u.id = ?";
        return template.queryForObject(sql, new Object[]{id}, new RowMapper<BEAN_Account>() {
            @Override
            public BEAN_Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Account bean = new BEAN_Account();
                bean.setUsername(rs.getString("username"));
                bean.setRole_id(rs.getInt("role_id"));
                bean.setRole_name(rs.getString("role_name"));
                bean.setUser_id(rs.getInt("id"));
                bean.setFull_name(rs.getString("name"));
                return bean;
            }
        });
    }

    public int updateLastLogin(int id) {
        String sql = "UPDATE tbl_user SET last_login = NOW() WHERE id = ?";
        return template.update(sql, id);
    }

    public int updateLastLogout(int id) {
        String sql = "UPDATE tbl_user SET last_logout = NOW() WHERE id = ?";
        return template.update(sql, id);
    }

    public int updatePassword(String username, String password) {
        String sql = "Update tbl_user SET password = ? WHERE username = ?";
        try {
            return template.update(sql, password, username);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public int checkEmail(String email) {
        String sql = "Select id from tbl_user WHERE email = ?";
        try {
            return template.queryForObject(sql, new Object[]{email}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public int saveOTP(int otp, int id) {
        String sql = "UPDATE tbl_user SET otp_code = ?, otp_expiration_date = DATE_ADD(NOW(), INTERVAL 5 MINUTE) WHERE id = ?";
        try {
            return template.update(sql, new Object[]{otp, id});
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public int verifyOTP(int otp, int id) {
        String sql = "SELECT id from tbl_user WHERE id = ? AND otp_code = ? AND otp_expiration_date > DATE(NOW())";
        try {
            return template.queryForObject(sql, new Object[]{id, otp}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

}
