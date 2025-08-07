package com.example.latte_break.DAO;


import com.example.latte_break.BEAN.BEAN_Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Component
public class DAO_Account {
    JdbcTemplate template;

    public void DAO_Account() {
    }

    @Autowired
    public void DAO_Account(JdbcTemplate template) {
        this.template = template;
    }

    public int addAccount(String first_name, String middle_name, String last_name,
                          String username, String password, int role_id,
                          String email) {
        String sql = "INSERT into tbl_user (first_name, middle_name, last_name, username, password, role_id, email)" +
                "VALUES(?,?,?,?,?,?,?)";
        return template.update(sql, first_name, middle_name, last_name, username,
                password, role_id, email);
    }

    public int checkUsername(String username) {
        String sql = "Select id from tbl_user WHERE username = ?";
        try {
            return template.queryForObject(sql, new Object[]{username}, Integer.class);
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

    public int checkUsernameById(String username, int id) {
        String sql = "Select id from tbl_user WHERE username = ? AND id != ?";
        try {
            return template.queryForObject(sql, new Object[]{username, id}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public int updateUserInfo(int user_id, String username, String first_name, String middle_name, String last_name,
                              int role_id, String email) {
        String sql = "Update tbl_user SET username = ?, first_name = ?, middle_name = ?, last_name = ?, " +
                "role_id = ?, email = ? WHERE id = ?";
        return template.update(sql, new Object[]{username, first_name, middle_name, last_name, role_id, email, user_id});
    }

    public List<BEAN_Account> getAllUser(String username, String full_name, int role_id) {
        String sql = "SELECT u.*, r.* FROM tbl_user u " +
                "LEFT JOIN ref_role r ON r.id = u.role_id " +
                "WHERE date_archived IS NULL " +
                "AND (? IS NULL OR username LIKE ?) " +
                "AND (? IS NULL OR first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ?) " +
                "AND (? IS NULL OR role_id = ?)";
        // Convert empty strings to NULL
        username = (username != null && !username.trim().isEmpty()) ? "%" + username + "%" : null;
        full_name = (full_name != null && !full_name.trim().isEmpty()) ? "%" + full_name + "%" : null;
        Integer roleIdObj = (role_id > 0) ? role_id : null;  // Treat 0 as NULL

        return template.query(sql, new RowMapper<BEAN_Account>() {
            @Override
            public BEAN_Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Account bean = new BEAN_Account();
                bean.setUser_id(rs.getInt("id"));
                bean.setUsername(rs.getString("username"));
                bean.setEmail(rs.getString("email"));
                bean.setFirst_name(rs.getString("first_name"));
                bean.setMiddle_name(rs.getString("middle_name"));
                bean.setLast_name(rs.getString("last_name"));
                bean.setRole_name(rs.getString("role_name"));
                bean.setRole_id(rs.getInt("role_id"));
                bean.setLast_login(rs.getString("last_login"));
                bean.setLast_logout(rs.getString("last_logout"));
                return bean;
            }
        }, new Object[]{username, username, full_name, full_name, full_name, full_name, roleIdObj, roleIdObj});
    }

    public List<BEAN_Account> getAllArchivedUser(String username, String full_name, int role_id) {
        String sql = "SELECT u.*, r.* FROM tbl_user u " +
                "LEFT JOIN ref_role r ON r.id = u.role_id " +
                "WHERE date_archived IS NOT NULL " +
                "AND (? IS NULL OR username LIKE ?) " +
                "AND (? IS NULL OR first_name LIKE ? OR middle_name LIKE ? OR last_name LIKE ?) " +
                "AND (? IS NULL OR role_id = ?)";
        // Convert empty strings to NULL
        username = (username != null && !username.trim().isEmpty()) ? "%" + username + "%" : null;
        full_name = (full_name != null && !full_name.trim().isEmpty()) ? "%" + full_name + "%" : null;
        Integer roleIdObj = (role_id > 0) ? role_id : null;  // Treat 0 as NULL

        return template.query(sql, new RowMapper<BEAN_Account>() {
            @Override
            public BEAN_Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_Account bean = new BEAN_Account();
                bean.setUser_id(rs.getInt("id"));
                bean.setUsername(rs.getString("username"));
                bean.setFirst_name(rs.getString("first_name"));
                bean.setMiddle_name(rs.getString("middle_name"));
                bean.setLast_name(rs.getString("last_name"));
                bean.setRole_name(rs.getString("role_name"));
                bean.setEmail(rs.getString("email"));
                bean.setRole_id(rs.getInt("role_id"));
                bean.setLast_login(rs.getString("last_login"));
                bean.setLast_logout(rs.getString("last_logout"));
                bean.setArchive_by(rs.getString("archive_by"));
                bean.setArchive_at(rs.getString("date_archived"));
                return bean;
            }
        }, new Object[]{username, username, full_name, full_name, full_name, full_name, roleIdObj, roleIdObj});
    }

    public int archiveUser(int id, String username) {
        String sql = "Update tbl_user SET date_archived = NOW(), archive_by = ? WHERE id = ?";
        return template.update(sql, username, id);
    }

    public int restoreAccount(int id) {
        String sql = "UPDATE tbl_user SET date_archived = NULL WHERE id = ?";
        return template.update(sql, id);
    }

    public int matchPass(String password, int id) {
        String sql = "SELECT id FROM tbl_user WHERE password = ? AND id = ?";
        try {
            return template.queryForObject(sql, new Object[]{password, id}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public int updatePassword(int user_id, String password) {
        String sql = "Update tbl_user SET password = ? WHERE id = ?";
        try {
            return template.update(sql, password, user_id);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }


    public int resetUser(int id, String username) {
        String sql = "Update tbl_user SET password = ? WHERE id = ?";
        return template.update(sql, username, id);
    }

    public int deleteAccount(int id) {
        String sql = "DELETE from tbl_user WHERE id = ?";
        return template.update(sql, id);
    }
}
