package com.example.latte_break.DAO.audit_logs;

import com.example.latte_break.BEAN.audit_logs.BEAN_AuditLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_AuditLogs {
    JdbcTemplate template;

    public void DAO_AuditLogs() {
    }

    @Autowired
    public void DAO_AuditLogs(JdbcTemplate template) {
        this.template = template;
    }

    public int saveAuditLogs(int user_id, String action){
        String sql = "INSERT INTO tbl_audit (user_id, action, timestamp) \n" +
                "VALUES (?, ?, NOW())";
        return template.update(sql, new Object[]{user_id, action});
    }

    public List<BEAN_AuditLogs> getAuditLogs(){
        String sql = "SELECT a.action, a.timestamp, u.username FROM tbl_audit a \n" +
                "INNER JOIN tbl_user u ON a.user_id = u.id " +
                "ORDER BY a.id DESC";
        return template.query(sql, new RowMapper<BEAN_AuditLogs>() {
            @Override
            public BEAN_AuditLogs mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_AuditLogs bean = new BEAN_AuditLogs();
                bean.setAction(rs.getString("action"));
                bean.setUsername(rs.getString("username"));
                bean.setTimestamp(rs.getString("timestamp"));
                return bean;
            }
        });
    }
}
