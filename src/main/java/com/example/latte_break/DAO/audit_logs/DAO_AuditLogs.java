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

    public List<BEAN_AuditLogs> getAuditLogs(String username, String date_from, String date_to){
        String sql = "SELECT \n" +
                "    a.action, \n" +
                "    a.timestamp, \n" +
                "    u.username\n" +
                "FROM tbl_audit a\n" +
                "INNER JOIN tbl_user u ON a.user_id = u.id\n" +
                "WHERE (? IS NULL OR u.username LIKE ?)\n" +
                "  AND (\n" +
                "        (? = '' OR ? = '') \n" +
                "        OR (DATE_FORMAT(a.timestamp, '%Y%m%d') BETWEEN ? AND ?)\n" +
                "      )\n" +
                "ORDER BY a.id DESC;";

        username = (username != null && !username.trim().isEmpty()) ? "%" + username + "%" : null;
        System.out.println("username >>" + username);
        System.out.println("date_from >>" + date_from);
        System.out.println("date_to >>" + date_to);
        return template.query(sql,new Object[]{username, username, date_from, date_to, date_from, date_to}, new RowMapper<BEAN_AuditLogs>() {
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
